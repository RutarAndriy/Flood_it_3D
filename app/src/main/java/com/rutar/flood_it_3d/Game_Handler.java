package com.rutar.flood_it_3d;

import android.annotation.SuppressLint;
import android.os.*;
import android.view.*;
import android.view.animation.*;

import static com.rutar.flood_it_3d.Unificator.*;
import static com.rutar.flood_it_3d.Game_Updator.*;
import static com.rutar.flood_it_3d.Flood_it_Activity.*;

import static java.lang.String.format;

// ................................................................................................

public class Game_Handler extends Handler {

@SuppressLint({"DefaultLocale", "SetTextI18n"})
@Override
public void handleMessage (Message msg) {

switch (msg.what) {

// Обертання усіх ігрових кнопок
case 0:

for (int z = 0; z < buttons.length; z++) {
    if (Build.VERSION.SDK_INT > 10) {
        buttons[z].setRotation(rotate_angle * (z%2 == 0 ? 1 : -1));
    }
}

break;

// Пауза - натискання кнопки назад або меню
case 1:

if (!pause_is_on) {

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

    text_Views_Normal[25].setText(format("%d/%d", dinamic_parts.size(), triangle_count));
    text_Views_Normal[26].setText(format("%s %d", activity.get_String(R.string.n_27), step_count));
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
case 4:

if (scores[model_index] == 0 ||
    scores[model_index] > step_count) {

    scores[model_index] = step_count;
    activity.save_Settings("level_" + model_index, step_count);

}

if (model_index < model_count - 1) { model_index++; }

anim_is_running = true;
complete_fade_in.setAnimationListener(new Animation.AnimationListener() {

    @Override
    public void onAnimationStart (Animation animation) {
        if (Build.VERSION.SDK_INT > 10) { l_complete.setAlpha(1); }
    }

    @Override
    public void onAnimationEnd (Animation animation) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try { Thread.sleep(2000); }
                catch (Exception ignored) {}
                handler.sendEmptyMessage(2);
            }
        }).start();
    }

    @Override
    public void onAnimationRepeat (Animation animation) {}

});

text_Views_Normal[23].setText(activity.get_String(R.string.n_24) + " " + step_count);
l_complete.startAnimation(complete_fade_in);
l_complete.setVisibility(View.VISIBLE);
if (Build.VERSION.SDK_INT > 10) { l_complete.setAlpha(0); }

break;

// У даній версії гри не використовується
case 5: break;

// Показ меню допомоги
case 6:

anim_is_running = true;
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
case 7:

need_help = 0;
activity.save_Settings("help", 0);

fade_out.setAnimationListener(new Animation.AnimationListener() {

    @Override
    public void onAnimationStart (Animation animation) {}

    @Override
    public void onAnimationEnd (Animation animation) {
        anim_is_running = false;
        l_help.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat (Animation animation) {}

});

l_help.startAnimation(fade_out);
break;

// Почати гру -> Назад
case 8: activity.on_View_Click(activity.findViewById(R.id.n_20));
        break;

// Перехід назад із різних пунктів меню
case 9:

// Таблиця рекордів -> Назад
if (l_score.getVisibility() == View.VISIBLE)
    { activity.on_View_Click(activity.findViewById(R.id.n_18)); }

// Налаштування -> Назад
else if (l_settings.getVisibility() == View.VISIBLE)
    { activity.on_View_Click(activity.findViewById(R.id.n_16)); }

// Про програму -> Назад
else if (l_about.getVisibility() == View.VISIBLE)
    { activity.on_View_Click(activity.findViewById(R.id.n_11)); }

// Вихід -> Назад
else if (l_exit.getVisibility() == View.VISIBLE)
    { activity.on_View_Click(activity.findViewById(R.id.n_08)); }

// Меню -> Назад
else if (l_exit.getVisibility() == View.GONE)
    { activity.on_View_Click(activity.findViewById(R.id.n_05)); }

break;

}
}

// Кінець класу <Game_Handler> ////////////////////////////////////////////////////////////////////

}