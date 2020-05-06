package com.rutar.flood_it_3d;

import android.os.*;
import android.util.Log;
import android.view.*;
import android.content.*;
import android.graphics.*;
import java.lang.reflect.*;
import android.annotation.*;
import android.view.animation.*;

import static com.rutar.flood_it_3d.Listener.*;
import static com.rutar.flood_it_3d.Constants.*;
import static com.rutar.flood_it_3d.Unification.*;
import static com.rutar.flood_it_3d.Game_Update.*;
import static com.rutar.flood_it_3d.Flood_it_Activity.*;

class Utils {

private static int last_back_button_text = R.string.settings_back;

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод затуманює фон
static void background_Fade_In (final int id) {

anim_is_running = true;
background_fade_in.setAnimationListener(new Animation.AnimationListener() {

@Override
public void onAnimationStart (Animation animation) {
    background.setVisibility(View.VISIBLE);
}
@Override
public void onAnimationEnd (Animation animation) { click_processing(id); }
@Override
public void onAnimationRepeat (Animation animation) {}

});

background.startAnimation(background_fade_in);

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод розвиднює фон
static void background_Fade_Out() {

background_fade_out.setAnimationListener(new Animation.AnimationListener() {

@Override
public void onAnimationStart (Animation animation) {}
@Override
public void onAnimationEnd (Animation animation) { background.setVisibility(View.GONE);
                                                   anim_is_running = false;
                                                   hide_off = true;
                                                   post_Fade_Out(); }
@Override
public void onAnimationRepeat (Animation animation) {}

});

background.startAnimation(background_fade_out);

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод виконується після розвиднення фону
static void post_Fade_Out() {

switch (game_state) {

// Запуск гри
case 1: background_fade_out.setStartOffset(0);
        background_fade_in.setStartOffset(3000);
        background_Fade_In(0);
        game_state = 2;
        break;

// Гра
case 4: loading.setVisibility(View.GONE);
        if (need_help == 1) { handler.sendEmptyMessage(6); }
        break;

}

}

///////////////////////////////////////////////////////////////////////////////////////////////////
static void click_processing (int id) {

switch (id) {

// Play Game -> Choice Model to play
case -1: set_Background_Speed();
         l_play.setVisibility(View.VISIBLE);
         set_Model_Preview_Angle();
         model_Available_Test();
         update_Preview_Text();
         activity.save_Settings("model_index", model_index);
         button_board.setVisibility(View.GONE);
         l_complete.setVisibility(View.GONE);
         l_pause.setVisibility(View.GONE);
         Utils.background_Fade_Out();
         flood_it_3D.play_Sounds(0);
         processing_time = -1;
         optimizing_time = -1;
         pause_is_on = false;
         game_state_index = 3;
         game_state = 3;

         break;

// Hide JME logo
case 0: l_menu.setVisibility(View.VISIBLE);
        background_fade_in.setStartOffset(0);
        logo_01.setVisibility(View.GONE);
        logo_02.setVisibility(View.GONE);
        Utils.update_Preview_Text();
        Utils.background_Fade_Out();
        flood_it_3D.play_Sounds(0);
        rotate_index = 0;
        break;

// Menu -> Choice
case R.id.n_01: set_Background_Speed();
                l_menu.setVisibility(View.GONE);
                l_play.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                set_Model_Preview_Angle();
                model_Available_Test();
                game_state = 3;
                game_state_index = 3;
                break;

// Menu -> Score
case R.id.n_02: set_Background_Speed();
                l_menu.setVisibility(View.GONE);
                l_score.setVisibility(View.VISIBLE);
                activity.reload_Scores_Table();
                Utils.background_Fade_Out();
                game_state_index = 1;
                break;

// Menu -> Settings
case R.id.n_03: set_Background_Speed();
                l_menu.setVisibility(View.GONE);
                l_settings.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                game_state_index = 1;
                break;

// Menu -> About
case R.id.n_04: set_Background_Speed();
                l_menu.setVisibility(View.GONE);
                l_about.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                game_state_index = 1;
                break;

// Menu -> Exit
case R.id.n_05: set_Background_Speed();
                l_menu.setVisibility(View.GONE);
                l_exit.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                game_state_index = 1;
                break;

// Choice -> Menu
case R.id.n_20: set_Background_Speed();
                l_play.setVisibility(View.GONE);
                l_menu.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                lock.setVisibility(View.GONE);
                activity.save_Settings("model_index", model_index);
                game_state = 2;
                game_state_index = 2;
                rotate_index = rotate_index > 150 ? 0 : rotate_index;
                break;

// Choice -> Game
case R.id.n_22: set_Background_Speed();
                l_play.setVisibility(View.GONE);
                button_board.setVisibility(View.VISIBLE);
                Unification.set_Buttons_Width(model_index/model_per_level);
                flood_it_3D.play_Sounds(model_index/model_per_level+1);
                game_state_index = 4;
                game_state = 4;
                break;

// High Score -> Menu
case R.id.n_18: set_Background_Speed();
                l_score.setVisibility(View.GONE);
                l_menu.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                game_state_index = 2;
                rotate_index = rotate_index > 150 ? 0 : rotate_index;
                break;

// High Score -> List Item
case R.id.score_list_item: model_index = list_item_position;
                           l_score.setVisibility(View.GONE);
                           l_play.setVisibility(View.VISIBLE);
                           set_Background_Speed();
                           background_Fade_Out();
                           set_Model_Preview_Angle();
                           model_Available_Test();
                           update_Preview_Text();
                           game_state_index = 3;
                           game_state = 3;
                           break;

// Settings -> Menu
case R.id.n_16: set_Background_Speed();
                l_settings.setVisibility(View.GONE);
                l_menu.setVisibility(View.VISIBLE);
                if (text_Views_Normal[15].getText().equals(activity
                                         .getString(R.string.settings_restart)))
                     { flood_it_3D.restart_App(); }
                else { Utils.background_Fade_Out();
                       game_state_index = 2;
                       rotate_index = rotate_index > 150 ? 0 : rotate_index; }
                break;

// About -> Menu
case R.id.n_11: set_Background_Speed();
                l_about.setVisibility(View.GONE);
                l_menu.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                game_state_index = 2;
                rotate_index = rotate_index > 150 ? 0 : rotate_index;
                break;

// Exit -> Menu
case R.id.n_07: set_Background_Speed();
                l_exit.setVisibility(View.GONE);
                l_menu.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                game_state_index = 2;
                rotate_index = rotate_index > 150 ? 0 : rotate_index;
                break;

// Exit -> Yes
case R.id.n_08: System.exit(0);
                break;

// Налаштування звуків
case R.id.n_13: sound -= sound > 0 ? 1 : -2;
                String sound_value;
                switch (sound) {
                    case 0:  sound_value = "settings_sound_off"; break;
                    case 1:  sound_value = "settings_sound_old"; break;
                    default: sound_value = "settings_sound_new"; break;
                }
                activity.save_Settings("sound", sound);
                flood_it_3D.play_Sounds(sound != 0 ? 0 : -1);
                text_Views_Normal[12].setText(activity.get_String_Value(sound_value));
                break;

// Налаштування вигляду кнопок
case R.id.n_36: buttons_type += buttons_type < 5 ? 1 : -5;
                activity.save_Settings("buttons", buttons_type);
                text_Views_Normal[35].setText(activity
                    .get_String_Value("settings_buttons_type_" + (buttons_type + 1)));
                break;

// Налаштування переливання кольорів
case R.id.n_35: String color_transfusion;
                switch (func_stages) {
                    case 60: func_stages = 0;
                             color_transfusion = "settings_color_transfusion_off";
                             break;
                    case 45: func_stages = 60;
                             color_transfusion = "settings_color_transfusion_slow";
                             break;
                    case 30: func_stages = 45;
                             color_transfusion = "settings_color_transfusion_normal";
                             break;
                    default: func_stages = 30;
                             color_transfusion = "settings_color_transfusion_fast";
                             break;
                }
                calculate_Function();
                activity.save_Settings("transfusion", func_stages);
                text_Views_Normal[34].setText(activity.get_String_Value(color_transfusion));
                break;

// Налаштування чутливості керування
case R.id.n_14: touch_sensitive += touch_sensitive < 4 ? 1 : -4;
                activity.save_Settings("sensitive", touch_sensitive);
                text_Views_Normal[13].setText(activity
                    .get_String_Value("settings_sensitive_" + (touch_sensitive + 1)));
                break;

// Налаштування мови
case R.id.n_15:

    language += language < 2 ? 1 : -2;
    String game_language;
    switch (language) {
        case 1:  game_language = "settings_language_uk"; break;
        case 2:  game_language = "settings_language_ru"; break;
        default: game_language = "settings_language_en"; break;
    }

    int temp = last_back_button_text;
    activity.save_Settings("language", language);
    text_Views_Normal[14].setText(activity.get_String_Value(game_language));

    if (language == def_language) { last_back_button_text = R.string.settings_back;
                                    text_Views_Normal[15].setText(R.string.settings_back); }
    else                          { last_back_button_text = R.string.settings_restart;
                                    text_Views_Normal[15].setText(R.string.settings_restart); }

    if (temp != last_back_button_text) { text_Views_Normal[15].startAnimation(press_animation); }

break;

// Easy or Normal or Hard or Very Hard
case R.id.n_21: set_Background_Speed();
                int index = (model_index / 12 + 1) * 12;
                model_index = index < model_count ? index : 0;
                background_Fade_Out();
                set_Model_Preview_Angle();
                model_Available_Test();
                update_Preview_Text();
                game_state_index = 3;
                break;

// Previous model - <<
case R.id.l_01: model_index -= model_index > 0 ? 1 : -model_count + 1;
                set_Background_Speed();
                background_Fade_Out();
                set_Model_Preview_Angle();
                model_Available_Test();
                update_Preview_Text();
                game_state_index = 3;
                break;

// Next model - >>
case R.id.l_02: model_index += model_index < (model_count - 1) ? 1 : -model_count + 1;
                set_Background_Speed();
                background_Fade_Out();
                set_Model_Preview_Angle();
                model_Available_Test();
                update_Preview_Text();
                game_state_index = 3;
                break;

// Кнопки зміни кольору
case R.id.b_01: if (is_done && color_index != 1)
    { color_index = 1;  game_state_index = 5; } break;
case R.id.b_02: if (is_done && color_index != 2)
    { color_index = 2;  game_state_index = 5; } break;
case R.id.b_03: if (is_done && color_index != 3)
    { color_index = 3;  game_state_index = 5; } break;
case R.id.b_04: if (is_done && color_index != 4)
    { color_index = 4;  game_state_index = 5; } break;
case R.id.b_05: if (is_done && color_index != 5)
    { color_index = 5;  game_state_index = 5; } break;
case R.id.b_06: if (is_done && color_index != 6)
    { color_index = 6;  game_state_index = 5; } break;
case R.id.b_07: if (is_done && color_index != 7)
    { color_index = 7;  game_state_index = 5; } break;
case R.id.b_08: if (is_done && color_index != 8)
    { color_index = 8;  game_state_index = 5; } break;
case R.id.b_09: if (is_done && color_index != 9)
    { color_index = 9;  game_state_index = 5; } break;
case R.id.b_10: if (is_done && color_index != 10)
    { color_index = 10; game_state_index = 5; } break;

case R.id.n_28: handler.sendEmptyMessage(2);
                break;

case R.id.n_29: if (pause_is_on) {

                anim_is_running = true;
                second_anim_run = true;
                fade_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart (Animation animation) {}
                @Override
                public void onAnimationEnd (Animation animation) { l_pause.setVisibility(View.GONE);
                                                                   anim_is_running = false;
                                                                   second_anim_run = false;
                                                                   pause_is_on = false; }
                @Override
                public void onAnimationRepeat (Animation animation) {}

                });

                l_pause.startAnimation(fade_out);

                }
                break;

case R.id.lock: show_Lock_Message();
                break;

case R.id.n_34: handler.sendEmptyMessage(7);
                break;

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////

static void update_Preview_Text() {

// Назва фігури
String model_name = "model_" + (model_index < 10 ? "0" : "") + model_index;
text_Views_Normal[18].setText(activity.get_String_Value(model_name));

int level_complexity;
switch (model_index/model_per_level) {
    case 0:  level_complexity = R.string.play_easy;      break;
    case 1:  level_complexity = R.string.play_normal;    break;
    case 2:  level_complexity = R.string.play_hard;      break;
    default: level_complexity = R.string.play_very_hard; break;
}

// Складність рівня
text_Views_Normal[20].setText(activity.get_String(level_complexity));

// Кількість трикутників
text_Views_Small[30].setText(activity
                    .get_Formatted_String(R.string.play_triangle_count,
                                          model_triangles_count[model_index]));

}

///////////////////////////////////////////////////////////////////////////////////////////////////

private static void set_Model_Preview_Angle() {
    preview_rotate_angle = model_preview_angle[model_index];
}

///////////////////////////////////////////////////////////////////////////////////////////////////

private static void model_Available_Test() {

if (model_index != 0 &&
   (scores[model_index-1] == 0 ||
    scores[model_index-1] > max_steps_count[model_index-1])) {

level_is_lock = true;
lock.setVisibility(View.VISIBLE);

}

else { level_is_lock = false;
       lock.setVisibility(View.GONE); }

}

///////////////////////////////////////////////////////////////////////////////////////////////////

@SuppressLint("SetTextI18n")
static void show_Lock_Message() {

anim_is_running = true;
fade_in_out_animation.setAnimationListener(new Animation.AnimationListener() {

    @Override
    public void onAnimationStart (Animation animation) {
        second_anim_run = true;
        if (Build.VERSION.SDK_INT > 10) { l_lock.setAlpha(1); }
    }
    @Override
    public void onAnimationEnd (Animation animation) { anim_is_running = false;
                                                       second_anim_run = false;
                                                       l_lock.setVisibility(View.GONE); }
    @Override
    public void onAnimationRepeat (Animation animation) {}
});

text_Views_Normal[29].setText(activity.get_Formatted_String(R.string.play_not_enough_points, max_steps_count[model_index-1] + 1));

l_lock.startAnimation(fade_in_out_animation);
l_lock.setVisibility(View.VISIBLE);
if (Build.VERSION.SDK_INT > 10) { l_lock.setAlpha(0); }

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод для глобальної заміни шрифту в програмі
// Інформація взята із StackOverflow.com

public static void override_Default_Font (Context context,
                                          String default_Font_Name_To_Override,
                                          String custom_Font_File_Name_In_Assets) {

try {

final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), String
                                   .format("fonts/%1$s.ttf", custom_Font_File_Name_In_Assets));

final Field defaultFontTypefaceField = Typeface.class
                                      .getDeclaredField(default_Font_Name_To_Override);

defaultFontTypefaceField.setAccessible(true);
defaultFontTypefaceField.set(null, customFontTypeface);

}

catch (Exception e) {}

}

// Кінець класу <Utils> ///////////////////////////////////////////////////////////////////////////

}
