package com.rutar.flood_it_3d;

import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.annotation.*;
import android.content.res.*;
import android.view.animation.*;

import android.os.Handler;

import com.jme3.app.*;
import com.jme3.input.*;
import com.jme3.input.event.*;
import com.jme3.input.controls.*;

import java.util.*;
import java.util.logging.*;

import static com.rutar.flood_it_3d.Listener.*;
import static com.rutar.flood_it_3d.Unificator.*;
import static com.rutar.flood_it_3d.Game_Updator.*;

///////////////////////////////////////////////////////////////////////////////////////////////////

public class Flood_it_Activity extends AndroidHarness implements Animation.AnimationListener {

public static Flood_it_Activity activity;

public static Animation fade_in;
public static Animation fade_out;
public static Animation help_fade_in;
public static Animation press_animation;
public static Animation complete_fade_in;
public static Animation background_fade_in;
public static Animation background_fade_out;
public static Animation fade_in_out_annimation;

public static ViewGroup l_menu;
public static ViewGroup l_play;
public static ViewGroup l_score;
public static ViewGroup l_settings;
public static ViewGroup l_about;
public static ViewGroup l_exit;
public static ViewGroup l_complete;
public static ViewGroup l_pause;
public static ViewGroup l_lock;
public static ViewGroup l_help;

public static ViewGroup button_board;

public static int game_state = -1;

public static Typeface typeface;
public static TextView background;
public static ProgressBar loading;

public static TextView  logo_01;
public static ImageView logo_02;

public static ImageView lock;
public static ImageView[] buttons = new ImageView[8];

public static int sound = 0;
public static int language = 0;
public static int def_language = 0;
public static int touch_sensitive = 0;

public static int app_load_count = 0;

public static boolean hide_off = true;

public static Handler handler;
public static float rotate_angle = 0;

public static boolean pause_is_on = false;
public static boolean level_is_lock = false;
public static SharedPreferences.Editor editor;

public static int[] scores = new int[30];
public static boolean thread_is_alive;

public static boolean anim_is_running = false;
public static boolean second_anim_run = false;

///////////////////////////////////////////////////////////////////////////////////////////////////

public Flood_it_Activity() { LogManager.getLogManager().getLogger("").setLevel(Level.OFF); }

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public void onCreate (Bundle bundle) {

eglBitsPerPixel = 24;
eglAlphaBits = 0;
eglDepthBits = 16;
eglSamples = 4;
eglStencilBits = 0;

frameRate = -1;

joystickEventsEnabled = false;
mouseEventsEnabled = true;
keyEventsEnabled = false;
handleExitHook = false;
finishOnAppStop = true;

splashPicID = 0;
appClass = Flood_it_3D.class.getCanonicalName();

///////////////////////////////////////////////////////////////////////////////////////////////////

SharedPreferences settings_reader = getPreferences(MODE_PRIVATE);
SharedPreferences.Editor settings_writer = settings_reader.edit();
Rate_Dialog.app_is_rated   = settings_reader.getBoolean("app_is_rated", false);
Flood_it_Activity.app_load_count = settings_reader.getInt("app_load_count", 0) + 1;
settings_writer.putInt("app_load_count", app_load_count);
settings_writer.commit();

///////////////////////////////////////////////////////////////////////////////////////////////////

game_state = 1;
int default_language = -1;
String lang = Locale.getDefault().getLanguage();
if      (lang.equals("uk")) { default_language = 1; }
else if (lang.equals("ru")) { default_language = 2; }
else                        { default_language = 0; }

Locale locale = null;
sound = load_Settings("sound", 1);
need_help = load_Settings("help", 1);
model_index = load_Settings("model_index", 0);
touch_sensitive = load_Settings("sensitive", 2);
def_language = language = load_Settings("language", default_language);

switch (language) {
    case 1:  locale = new Locale("uk"); break;
    case 2:  locale = new Locale("ru"); break;
    default: locale = new Locale("en");
}

Locale.setDefault(locale);
Configuration config = new Configuration();
config.locale = locale;
getBaseContext().getResources().updateConfiguration(config,
                 getBaseContext().getResources().getDisplayMetrics());

///////////////////////////////////////////////////////////////////////////////////////////////////

super.onCreate(bundle);

FrameLayout game_layout = new FrameLayout(this);
FrameLayout keys_layout = (FrameLayout) LayoutInflater
                          .from(this).inflate(R.layout.flood_it_layout, null);

// Відкріплення GLSurfaceView від батьківського елемента
((FrameLayout)view.getParent()).removeView(view);

// Додавання GLSurfaceView та layout'а ігрових клавіш до загального layout'а
game_layout.addView(view);
game_layout.addView(keys_layout);

// Відображення загального layout'а
setContentView(game_layout);

// Приховування наекранних клавіш
hide_NavigationBar();

// Визначення версії гри
String version_name = "unknown";
String version_info = getResources().getString(R.string.n_10);

try { version_name = getPackageManager().getPackageInfo(getPackageName(), 0).versionName; }
catch (Exception e) { version_name = "1.0"; }

((TextView) findViewById(R.id.n_10)).setText(String.format(version_info, version_name));

///////////////////////////////////////////////////////////////////////////////////////////////////

for (int z = 0; z < sound_volume.length; z++) {
    sound_volume[z] = 0;
    delta_volume[z] = 0;
}

init_Components();

}

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
protected void onStop() { super.onStop(); }

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
protected void onResume() {

super.onResume();

if (game_state == 4) {

pause_is_on = true;
text_Views_Normal[25].setText(String.format("%d/%d", dinamic_parts.size(), triangle_count));
text_Views_Normal[26].setText(String.format("%s %d", get_String(R.string.n_27), step_count));
l_pause.setVisibility(View.VISIBLE);

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////

private static final int hide_navbar_flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                           | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                           | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                           | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                           | View.SYSTEM_UI_FLAG_FULLSCREEN
                                           | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

///////////////////////////////////////////////////////////////////////////////////////////////////
// Приховування наекранних кнопок

public void hide_NavigationBar() {

// Для старих версій Android
if (Build.VERSION.SDK_INT > 11 &&
    Build.VERSION.SDK_INT < 19) {

View decor_view = getWindow().getDecorView();
decor_view.setSystemUiVisibility(View.GONE);

}

// Для нових версій Android
else if (Build.VERSION.SDK_INT >= 19) {

final View decor_view = getWindow().getDecorView();
decor_view.setSystemUiVisibility(hide_navbar_flags);

decor_view.setOnSystemUiVisibilityChangeListener
          (new View.OnSystemUiVisibilityChangeListener() {

    @Override
    public void onSystemUiVisibilityChange (int visibility) {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)  {
            decor_view.setSystemUiVisibility(hide_navbar_flags);
        }
    }
});

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public void onWindowFocusChanged (boolean has_focus) {

super.onWindowFocusChanged(has_focus);

if (!has_focus) { return; }
else { hide_NavigationBar(); }

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Ініціалізація компонентів

@SuppressLint("HandlerLeak")
private void init_Components() {

thread_is_alive = true;

l_menu       = findViewById(R.id.l_menu);
l_play       = findViewById(R.id.l_play);
l_score      = findViewById(R.id.l_score);
l_settings   = findViewById(R.id.l_settings);
l_about      = findViewById(R.id.l_about);
l_exit       = findViewById(R.id.l_exit);
l_complete   = findViewById(R.id.l_complete);
l_pause      = findViewById(R.id.l_pause);
l_lock       = findViewById(R.id.l_lock);
l_help       = findViewById(R.id.l_help);

button_board = findViewById(R.id.button_board);

logo_01      = findViewById(R.id.logo_01);
logo_02      = findViewById(R.id.logo_02);

lock         = findViewById(R.id.lock);

///////////////////////////////////////////////////////////////////////////////////////////////////

for (int z = 0; z < buttons.length; z++) {
    buttons[z] = findViewById(get_Id("b_0" + (z + 1)));
}

loading = findViewById(R.id.loading);
background = findViewById(R.id.background);
typeface = Typeface.createFromAsset(getAssets(), "fonts/v_Steadfast_Regular.ttf");

press_animation = AnimationUtils.loadAnimation(this, R.anim.press_anim);
fade_in_out_annimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_out_anim);

press_animation.setAnimationListener(this);

background_fade_in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
background_fade_in.setDuration(500);
background_fade_out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
background_fade_out.setDuration(500);
fade_in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
fade_in.setDuration(500);
fade_out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
fade_out.setDuration(500);

help_fade_in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
help_fade_in.setStartOffset(1500);
help_fade_in.setDuration(700);

complete_fade_in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
complete_fade_in.setStartOffset(700);
complete_fade_in.setDuration(700);

activity = this;
Unificator.init();

background_fade_out.setStartOffset(3000);
Utils.background_Fade_Out();

///////////////////////////////////////////////////////////////////////////////////////////////////

handler = new Game_Handler();

///////////////////////////////////////////////////////////////////////////////////////////////////

new Thread(new Runnable() {

@Override
public void run() {
    while (thread_is_alive) {
        rotate_angle += 1.5f;
        handler.sendEmptyMessage(0);

        try { Thread.sleep(25); }
        catch (Exception e) {}
    }
}
}).start();

///////////////////////////////////////////////////////////////////////////////////////////////////

text_Views_Normal[12].setText(activity.get_String_Value("n_13_" + sound));
text_Views_Normal[13].setText(activity.get_String_Value("n_14_" + touch_sensitive));
text_Views_Normal[14].setText(activity.get_String_Value("n_15_" + language));

for (int z = 0; z < 30; z++) { scores[z] = load_Settings("level_" + z, 0); }
reload_Scores_Table();

}

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public void initialize() {

super.initialize();

InputManager input_manager = getJmeApplication().getInputManager();

input_manager.addMapping("Back", new TouchTrigger(TouchInput.KEYCODE_BACK));
input_manager.addMapping("Menu", new TouchTrigger(TouchInput.KEYCODE_MENU));
input_manager.addListener(touchListener, "Back", "Menu");

}

///////////////////////////////////////////////////////////////////////////////////////////////////

public void on_View_Click (View view) { Listener.on_View_Click(view); }

///////////////////////////////////////////////////////////////////////////////////////////////////

public int get_Id (String value) { return getResources().
                                          getIdentifier(value, "id", getPackageName()); }

///////////////////////////////////////////////////////////////////////////////////////////////////

public String get_String (int id) { return getResources().getString(id); }

///////////////////////////////////////////////////////////////////////////////////////////////////

public int get_String_Value (String value) { return getResources().
                                                    getIdentifier(value, "string",
                                                                  getPackageName()); }

///////////////////////////////////////////////////////////////////////////////////////////////////

public void save_Settings (String key, int value) {

editor = getPreferences(MODE_PRIVATE).edit();
editor.putInt(key, value);
editor.apply();

}

///////////////////////////////////////////////////////////////////////////////////////////////////

public int load_Settings (String key, int default_value) {

return getPreferences(MODE_PRIVATE).getInt(key, default_value);

}

///////////////////////////////////////////////////////////////////////////////////////////////////

public void reload_Scores_Table() {

for (int z = 0; z < 30; z++) {

if (scores[z] == 0) { text_Views_Small[z].setText("" + (z + 1) + ". - "); }
else { text_Views_Small[z].setText("" + (z + 1) + ". " + scores[z] + " " + get_String(R.string.s_0)); }

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public void onAnimationStart (Animation animation) { anim_is_running = true; }
@Override
public void onAnimationEnd (Animation animation) { anim_is_running = false; }
@Override
public void onAnimationRepeat (Animation animation) {}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Власна реалізація прослуховування клавіш

@Override
public boolean onKeyUp (int key_Code, KeyEvent event) {

// Обробка клавіш необхідна лише для Android 9.0+
if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) { return false; }

TouchEvent touch_event = new TouchEvent();
touch_event.set(TouchEvent.Type.KEY_UP);

switch (key_Code) {

// Кнопка "Назад"
case KeyEvent.KEYCODE_BACK:
    touchListener.onTouch("Back", touch_event, 0);
    break;

// Кнопка "Меню"
case KeyEvent.KEYCODE_MENU:
    touchListener.onTouch("Menu", touch_event, 0);
    break;

}

return false;

}

// Кінець класу <Flood_it_Activity> ///////////////////////////////////////////////////////////////

}