package com.rutar.flood_it_3d;

import android.os.*;
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
import com.jme3.input.controls.*;

import java.util.*;
import java.util.logging.*;

import static com.rutar.flood_it_3d.Listener.*;
import static com.rutar.flood_it_3d.Unificator.*;
import static com.rutar.flood_it_3d.Game_Updator.*;

///////////////////////////////////////////////////////////////////////////////////////////////////

public class Flood_it_Activity extends AndroidHarnessMod implements Animation.AnimationListener {

public static String TAG = "Flood_it";

public static Flood_it_Activity activity;

public static InputManager my_Input_Manager;

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

finishOnAppStop = true;

splashPicID = 0;
layoutRes = R.layout.flood_it_layout;
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

handler = new Handler() {

@Override
public void handleMessage (Message msg) {

switch (msg.what) {

// Обертання усіх ігрових кнопок
case 0: for (int z = 0; z < buttons.length; z++) {
            if (Build.VERSION.SDK_INT > 10) {
                buttons[z].setRotation(rotate_angle * (z%2 == 0 ? 1 : -1));
            }
        }
        break;

// Пауза - натискання кнопки назад або меню
case 1: if (!pause_is_on) {

        anim_is_running = true;
        fade_in.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart (Animation animation) {
            pause_is_on = true;
            if (Build.VERSION.SDK_INT > 10) { l_pause.setAlpha(1); }
        }
        @Override
        public void onAnimationEnd (Animation animation) { anim_is_running = false; }
        @Override
        public void onAnimationRepeat (Animation animation) {}
        });

        text_Views_Normal[25].setText(String.format("%d/%d", dinamic_parts.size(), triangle_count));
        text_Views_Normal[26].setText(String.format("%s %d", get_String(R.string.n_27), step_count));
        l_pause.startAnimation(fade_in);
        l_pause.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT > 10) { l_pause.setAlpha(0); }

        }
        break;

// Перехід з гри до меню вибору моделі
case 2: hide_off = false;
        Utils.background_Fade_In(-1);
        break;

// Анімація завантаження моделі
case 3: anim_is_running = true;
        loading.startAnimation(background_fade_out);
        Utils.background_Fade_Out();
        break;

// Рівень завершено
case 4: if (scores[model_index] == 0 ||
            scores[model_index] > step_count) { scores[model_index] = step_count;
                                                save_Settings("level_" + model_index, step_count); }

        if (model_index < model_count - 1) { model_index++; }

        anim_is_running = true;
        complete_fade_in.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart (Animation animation) {
            if (Build.VERSION.SDK_INT > 10) { l_complete.setAlpha(1); }
        }
        @Override
        public void onAnimationEnd (Animation animation) { new Thread(new Runnable() {
                                                           @Override
                                                           public void run() {
                                                               try { Thread.sleep(2000); }
                                                               catch (Exception e) {}
                                                               handler.sendEmptyMessage(2);
                                                           }}).start(); }
        @Override
        public void onAnimationRepeat (Animation animation) {}
        });

        text_Views_Normal[23].setText(get_String(R.string.n_24) + " " + step_count);
        l_complete.startAnimation(complete_fade_in);
        l_complete.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT > 10) { l_complete.setAlpha(0); }
        break;

// У даній версії гри не використовується
case 5: break;

// Показ меню допомоги
case 6: anim_is_running = true;
        help_fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation) {
                if (Build.VERSION.SDK_INT > 10) { l_help.setAlpha(1); }
            }
            @Override
            public void onAnimationEnd (Animation animation) {}
            @Override
            public void onAnimationRepeat (Animation animation) {}
        });

        l_help.startAnimation(help_fade_in);
        l_help.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT > 10) { l_help.setAlpha(0); }
        break;

// Закриття меню допомоги
case 7: need_help = 0;
        activity.save_Settings("help", 0);

        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation) {}
            @Override
            public void onAnimationEnd (Animation animation) { anim_is_running = false;
                l_help.setVisibility(View.GONE); }
            @Override
            public void onAnimationRepeat (Animation animation) {}
        });

        l_help.startAnimation(fade_out);
        break;

}

}
};

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
public void initialize() { super.initialize(); }

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

}