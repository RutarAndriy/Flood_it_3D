package com.rutar.flood_it_3d;

import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.annotation.*;
import android.view.animation.*;

import static com.rutar.flood_it_3d.Unificator.*;
import static com.rutar.flood_it_3d.Flood_it_3D.*;
import static com.rutar.flood_it_3d.Game_Updator.*;
import static com.rutar.flood_it_3d.Flood_it_Activity.*;

public class Utils {

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод затуманює фон
public static void background_Fade_In (final int id) {

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
public static void background_Fade_Out() {

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
public static void post_Fade_Out() {

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
public static void click_processing (int id) {

switch (id) {

// Game -> Choice
case -1: l_play.setVisibility(View.VISIBLE);
         model_Available_Test();
         update_Preview_Text();
         activity.save_Settings("model_index", model_index);
         button_board.setVisibility(View.GONE);
         l_complete.setVisibility(View.GONE);
         l_pause.setVisibility(View.GONE);
         Utils.background_Fade_Out();
         pause_is_on = false;
         change_index = 3;
         game_state = 3;
         play_Sounds(0);
         break;

// Hide JME logo
case 0: l_menu.setVisibility(View.VISIBLE);
        background_fade_in.setStartOffset(0);
        logo_01.setVisibility(View.GONE);
        logo_02.setVisibility(View.GONE);
        play_Sounds(0);
        Utils.update_Preview_Text();
        Utils.background_Fade_Out();
        rotate_index = 0;
        break;

// Menu -> Choice
case R.id.n_01: l_menu.setVisibility(View.GONE);
                l_play.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                model_Available_Test();
                game_state = 3;
                change_index = 3;
                break;

// Menu -> Score
case R.id.n_02: l_menu.setVisibility(View.GONE);
                l_score.setVisibility(View.VISIBLE);
                activity.reload_Scores_Table();
                Utils.background_Fade_Out();
                change_index = 1;
                break;

// Menu -> Settings
case R.id.n_03: l_menu.setVisibility(View.GONE);
                l_settings.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                change_index = 1;
                break;

// Menu -> About
case R.id.n_04: l_menu.setVisibility(View.GONE);
                l_about.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                change_index = 1;
                break;

// Menu -> Exit
case R.id.n_05: l_menu.setVisibility(View.GONE);
                l_exit.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                change_index = 1;
                break;

// Choice -> Menu
case R.id.n_20: l_play.setVisibility(View.GONE);
                l_menu.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                lock.setVisibility(View.GONE);
                activity.save_Settings("model_index", model_index);
                game_state = 2;
                change_index = 2;
                rotate_index = rotate_index > 150 ? 0 : rotate_index;
                break;

// Choice -> Game
case R.id.n_22: l_play.setVisibility(View.GONE);
                button_board.setVisibility(View.VISIBLE);
                Unificator.set_Buttons_Width(model_index/10);
                play_Sounds(model_index/10+1);
                change_index = 4;
                game_state = 4;
                break;

// Score -> Menu
case R.id.n_18: l_score.setVisibility(View.GONE);
                l_menu.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                change_index = 2;
                rotate_index = rotate_index > 150 ? 0 : rotate_index;
                break;

// Settings -> Menu
case R.id.n_16: l_settings.setVisibility(View.GONE);
                l_menu.setVisibility(View.VISIBLE);
                if (text_Views_Normal[15].getText().
                    equals(activity.getString(R.string.n_16_1))) { System.exit(0); }

                else { Utils.background_Fade_Out();
                       change_index = 2;
                       rotate_index = rotate_index > 150 ? 0 : rotate_index; }
                break;

// About -> Menu
case R.id.n_11: l_about.setVisibility(View.GONE);
                l_menu.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                change_index = 2;
                rotate_index = rotate_index > 150 ? 0 : rotate_index;
                break;

// Exit -> Menu
case R.id.n_07: l_exit.setVisibility(View.GONE);
                l_menu.setVisibility(View.VISIBLE);
                Utils.background_Fade_Out();
                change_index = 2;
                rotate_index = rotate_index > 150 ? 0 : rotate_index;
                break;

// Exit -> Yes
case R.id.n_08: System.exit(0);
                break;

// Settings 1
case R.id.n_13: sound += sound == 0 ? 1 : -1;
                activity.save_Settings("sound", sound);
                text_Views_Normal[12].setText(activity.get_String_Value("n_13_" + sound));
                break;

// Settings 2
case R.id.n_14: touch_sensitive += touch_sensitive < 4 ? 1 : -4;
                activity.save_Settings("sensitive", touch_sensitive);
                text_Views_Normal[13].setText(activity.get_String_Value("n_14_" + touch_sensitive));
                break;

// Settings 3
case R.id.n_15: language += language < 2 ? 1 : -2;
                activity.save_Settings("language", language);
                text_Views_Normal[14].setText(activity.get_String_Value("n_15_" + language));
                if (language == def_language) { text_Views_Normal[15].setText(R.string.n_16_0); }
                else { text_Views_Normal[15].setText(R.string.n_16_1); }
                break;

// Previous model - <<
case R.id.l_01: model_index -= model_index > 0 ? 1 : -model_count + 1;
                background_Fade_Out();
                model_Available_Test();
                update_Preview_Text();
                change_index = 3;
                break;

// Next model - >>
case R.id.l_02: model_index += model_index < (model_count - 1) ? 1 : -model_count + 1;
                background_Fade_Out();
                model_Available_Test();
                update_Preview_Text();
                change_index = 3;
                break;

case R.id.b_01: color_index = 0; change_index = 5; break;
case R.id.b_02: color_index = 1; change_index = 5; break;
case R.id.b_03: color_index = 4; change_index = 5; break;
case R.id.b_04: color_index = 6; change_index = 5; break;
case R.id.b_05: color_index = 2; change_index = 5; break;
case R.id.b_06: color_index = 3; change_index = 5; break;
case R.id.b_07: color_index = 5; change_index = 5; break;
case R.id.b_08: color_index = 7; change_index = 5; break;

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

// Not used
//case R.id.rate: try { Intent intent = new Intent(Intent.ACTION_VIEW);
//                      intent.setData(Uri.parse("market://details?id=com.rutar.flood_it_3d"));
//                      activity.startActivity(intent); }
//                catch (Exception e) { Log.e(Flood_it_Activity.TAG, "PlayMarketApp not found");
//                                      Toast.makeText(Flood_it_Activity.activity,
//                                                     R.string.PM_Error,
//                                                     Toast.LENGTH_SHORT).show(); }
//                break;

case R.id.lock: show_Lock_Message();
                break;

case R.id.n_34: handler.sendEmptyMessage(7);
                break;

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////

public static void update_Preview_Text() {

text_Views_Normal[18].setText(activity.get_String_Value("n_19_" + model_index));
text_Views_Normal[20].setText(activity.get_String_Value("n_21_" + model_index/10));

String triangle_count = activity.getResources().getString(R.string.s_31);
text_Views_Small[30].setText(triangle_count + " " + model_triangles_count[model_index]);

}

///////////////////////////////////////////////////////////////////////////////////////////////////

private static void model_Available_Test() {

if (model_index != 0 &&
   (scores[model_index-1] == 0 ||
    model_index >= max_model_index ||
    scores[model_index-1] > max_steps_count[model_index-1])) {

level_is_lock = true;
lock.setVisibility(View.VISIBLE);

}

else { level_is_lock = false;
       lock.setVisibility(View.GONE); }

}

///////////////////////////////////////////////////////////////////////////////////////////////////

@SuppressLint("SetTextI18n")
public static void show_Lock_Message() {

anim_is_running = true;
fade_in_out_annimation.setAnimationListener(new Animation.AnimationListener() {
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

if (model_index < max_model_index) {
    text_Views_Normal[29].setText(activity.get_String(R.string.n_30_0) + " " +
                                 (max_steps_count[model_index-1] + 1) + " " +
                                  activity.get_String(R.string.n_30_1)); }

else { text_Views_Normal[29].setText(R.string.n_30_2); }

l_lock.startAnimation(fade_in_out_annimation);
l_lock.setVisibility(View.VISIBLE);
if (Build.VERSION.SDK_INT > 10) { l_lock.setAlpha(0); }

}

///////////////////////////////////////////////////////////////////////////////////////////////////

}
