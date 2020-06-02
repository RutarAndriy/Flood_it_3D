package com.rutar.flood_it_3d;

import android.os.*;
import android.support.v7.widget.AppCompatImageView;
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
import static com.rutar.flood_it_3d.Unification.*;
import static com.rutar.flood_it_3d.Game_Update.*;

// ................................................................................................

public class Flood_it_Activity extends AndroidHarness implements Animation.AnimationListener {

static Flood_it_3D flood_it_3D;
static Flood_it_Activity activity;

static Animation fade_in;
static Animation fade_out;
static Animation help_fade_in;
static Animation press_animation;
static Animation complete_fade_in;
static Animation background_fade_in;
static Animation background_fade_out;
static Animation fade_in_out_animation;

static ViewGroup l_menu;
static ViewGroup l_play;
static ViewGroup l_score;
static ViewGroup l_settings;
static ViewGroup l_about;
static ViewGroup l_exit;
static ViewGroup l_complete;
static ViewGroup l_pause;
static ViewGroup l_lock;
static ViewGroup l_help;

static ViewGroup button_board;

static int game_state = -1;
static int rotation_direction = 0;

static Typeface typeface;
static TextView background;
static ProgressBar loading;

static ImageView jme_logo;

static FrameLayout lock;
static FrameLayout[] buttons_l = new FrameLayout[10];
static FrameLayout[] buttons_s = new FrameLayout[10];

static int sound = 0;
static int language = 0;
static int buttons_type = 0;
static int def_language = 0;
static int touch_sensitive = 0;

static int app_load_count = 0;

static boolean hide_off = true;

static Handler handler;
static float rotate_angle = 0;

static boolean pause_is_on = false;
static boolean level_is_lock = false;
static SharedPreferences.Editor editor;

static boolean thread_is_alive;

static boolean anim_is_running = false;
static boolean second_anim_run = false;

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

// ................................................................................................

Utils.override_Default_Font(getApplicationContext(), "SANS_SERIF", "v_Steadfast_Regular");

// ................................................................................................

SharedPreferences settings_reader = getPreferences(MODE_PRIVATE);
SharedPreferences.Editor settings_writer = settings_reader.edit();
Rate_Dialog.app_is_rated   = settings_reader.getBoolean("app_is_rated", false);
Flood_it_Activity.app_load_count = settings_reader.getInt("app_load_count", 0) + 1;
settings_writer.putInt("app_load_count", app_load_count);
settings_writer.commit();

///////////////////////////////////////////////////////////////////////////////////////////////////

game_state = 1;
int default_language = 0;
String lang = Locale.getDefault().getLanguage();
if      (lang.equals("uk")) { default_language = 1; }
else if (lang.equals("ru")) { default_language = 2; }

Locale locale = null;
sound = load_Settings("sound", 2);
need_help = load_Settings("help", 1);
buttons_type = load_Settings("buttons", 1);
model_index = load_Settings("model_index", 0);
func_stages = load_Settings("transfusion", 45);
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
String version_info = getResources().getString(R.string.about_text);

try { version_name = getPackageManager().getPackageInfo(getPackageName(), 0).versionName; }
catch (Exception e) { version_name = "1.0"; }

((TextView) findViewById(R.id.n_10)).setText(String.format(version_info, version_name));

///////////////////////////////////////////////////////////////////////////////////////////////////

flood_it_3D = (Flood_it_3D) getJmeApplication();
init_Components();

}

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
protected void onStop() { super.onStop(); }

///////////////////////////////////////////////////////////////////////////////////////////////////

@SuppressLint("DefaultLocale")
@Override
protected void onResume() {

super.onResume();

if (game_state == 4) {

pause_is_on = true;
text_Views_Normal[25].setText(String.format("%d/%d", dynamic_index_list.size(), triangle_count));
text_Views_Normal[26].setText(String.format("%s %d", get_String(R.string.game_step_count), step_count));
l_pause.setVisibility(View.VISIBLE);

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////

private static final int hide_nav_bar_flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
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
decor_view.setSystemUiVisibility(hide_nav_bar_flags);

decor_view.setOnSystemUiVisibilityChangeListener
          (new View.OnSystemUiVisibilityChangeListener() {

    @Override
    public void onSystemUiVisibilityChange (int visibility) {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)  {
            decor_view.setSystemUiVisibility(hide_nav_bar_flags);
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
jme_logo     = findViewById(R.id.jme_logo);
lock         = findViewById(R.id.lock);

///////////////////////////////////////////////////////////////////////////////////////////////////

for (int z = 0; z < buttons_l.length; z++) {
    buttons_l[z] = findViewById(get_Id("b_" + ((z + 1) < 10 ? "0" : "") + (z + 1) + "_l"));
    buttons_s[z] = findViewById(get_Id("b_" + ((z + 1) < 10 ? "0" : "") + (z + 1) + "_s"));
}

loading = findViewById(R.id.loading);
background = findViewById(R.id.background);
typeface = Typeface.createFromAsset(getAssets(), "fonts/v_Steadfast_Regular.ttf");

press_animation = AnimationUtils.loadAnimation(this, R.anim.press_anim);
fade_in_out_animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_out_anim);

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
Unification.init();

background_fade_out.setStartOffset(3000);
Utils.background_Fade_Out();

///////////////////////////////////////////////////////////////////////////////////////////////////

handler = new Game_Handler();

///////////////////////////////////////////////////////////////////////////////////////////////////

new Thread(new Runnable() {

@Override
public void run() {
    while (thread_is_alive) {
        rotate_angle += 1.0f;
        handler.sendEmptyMessage(0);

        try { Thread.sleep(25); }
        catch (Exception e) {}
    }
}
}).start();

///////////////////////////////////////////////////////////////////////////////////////////////////

String sound_value;
switch (sound) {
    case 0:  sound_value = "settings_sound_off"; break;
    case 1:  sound_value = "settings_sound_old"; break;
    default: sound_value = "settings_sound_new"; break;
}

String buttons_style;
switch (buttons_type) {
    case 0:  buttons_style = "settings_buttons_type_1"; break;
    case 1:  buttons_style = "settings_buttons_type_2"; break;
    case 2:  buttons_style = "settings_buttons_type_3"; break;
    case 3:  buttons_style = "settings_buttons_type_4"; break;
    case 4:  buttons_style = "settings_buttons_type_5"; break;
    case 5:  buttons_style = "settings_buttons_type_6"; break;
    default: buttons_style = "settings_buttons_type_7"; break;
}

String color_transfusion;
switch (func_stages) {
    case 30: color_transfusion = "settings_color_transfusion_fast";   break;
    case 45: color_transfusion = "settings_color_transfusion_normal"; break;
    case 60: color_transfusion = "settings_color_transfusion_slow";   break;
    default: color_transfusion = "settings_color_transfusion_off";    break;
}

String game_language;
switch (language) {
    case 1:  game_language = "settings_language_uk"; break;
    case 2:  game_language = "settings_language_ru"; break;
    default: game_language = "settings_language_en"; break;
}

// Налаштування звуків
text_Views_Normal[12].setText(activity.get_String_Value(sound_value));

// Налаштування вигляду кнопок
text_Views_Normal[35].setText(activity.get_String_Value(buttons_style));

// Налаштування переливання кольорів
text_Views_Normal[34].setText(activity.get_String_Value(color_transfusion));

// Налаштування чутливості керування
text_Views_Normal[13].setText(activity
                     .get_String_Value("settings_sensitive_" + (touch_sensitive + 1)));

// Налаштування мови
text_Views_Normal[14].setText(activity.get_String_Value(game_language));

// Заповнення даних таблиці рекордів
for (int z = 0; z < 30; z++) { scores[z] = load_Settings("level_" + z, 0); }
reload_Scores_Table();

// Ініціалізація ігрових кнопок
init_Game_Buttons();

}

///////////////////////////////////////////////////////////////////////////////////////////////////

public void init_Game_Buttons() {

String layout_id = null;
FrameLayout frame_layout = null;
AppCompatImageView image_view = null;

int temp = buttons_type;
if (temp == 6) { temp = (int)(Math.random() * 6); }

rotation_direction = (temp % 2 == 0 ? 1 : -1);
String type = temp > 3 ? "c" : temp > 1 ? "b" : "a";

String [] colors = { "red",  "blue",   "green", "yellow", "white",
                     "gray", "violet", "cyan",  "lime",   "orange" };

// ................................................................................................

for (int z = 1; z <= 10; z++) {

    String id = z < 10 ? "0" + z : "" + z;

    layout_id = String.format("b_%1$s_l", id);
    frame_layout = findViewById(get_Id(layout_id));
    image_view = (AppCompatImageView) frame_layout.getChildAt(0);
    image_view.setImageResource(get_Drawable_Id(String
              .format("button_%1$s_%2$s_external", type, colors[z-1])));

    layout_id = String.format("b_%1$s_s", id);
    frame_layout = findViewById(get_Id(layout_id));
    image_view = (AppCompatImageView) frame_layout.getChildAt(0);
    image_view.setImageResource(get_Drawable_Id(String
              .format("button_%1$s_%2$s_internal", type, colors[z-1])));

}
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

public int get_Id (String value)
    { return getResources().getIdentifier(value, "id", getPackageName()); }

///////////////////////////////////////////////////////////////////////////////////////////////////

public int get_Drawable_Id (String value)
    { return getResources().getIdentifier(value, "drawable", getPackageName()); }

///////////////////////////////////////////////////////////////////////////////////////////////////

public String get_String (int id) { return getResources().getString(id); }

///////////////////////////////////////////////////////////////////////////////////////////////////

public String get_Formatted_String (int id, Object ... values)
    { return String.format(getResources().getString(id), values); }

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

@SuppressLint("SetTextI18n")
public void reload_Scores_Table() {

ListView list_view = activity.findViewById(R.id.game_score_list);

String[] model_names = new String[48];
String[] score_values = new String[48];

for (int index = 0; index < 48; index++) {

    String model = "model_" + (index <= 9 ? "0" : "") + index;
    model_names[index] = get_String(activity.get_String_Value(model));

    if (scores[index] == 0) { score_values[index] = " - "; }
    else { score_values[index] = get_Formatted_String(R.string.score_step_count, scores[index]); }

}

Scores_List_Adapter adapter = new Scores_List_Adapter(activity);
adapter.set_Model_Names(model_names);
adapter.set_Score_Values(score_values);

list_view.setOnItemClickListener(list_view_listener);
list_view.setAdapter(adapter);

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
