package com.rutar.flood_it_3d;

import android.view.*;

import com.jme3.math.*;
import com.jme3.input.event.*;
import com.jme3.input.controls.*;

import static com.rutar.flood_it_3d.Flood_it_3D.*;
import static com.rutar.flood_it_3d.Game_Updator.*;
import static com.rutar.flood_it_3d.Flood_it_Activity.*;

///////////////////////////////////////////////////////////////////////////////////////////////////

public class Listener {

private static float delta;
private static float scale;
private static float distance;

///////////////////////////////////////////////////////////////////////////////////////////////////
// Обробка Android клавіш - Back та Home а також обробка жестів
public static TouchListener touchListener = new TouchListener() {
@Override
public void onTouch (String key, TouchEvent event, float tpf) {

if (event.getType() == TouchEvent.Type.SCALE_START) {

    distance = event.getScaleSpan();
    scale = game_node_child.getLocalScale().getZ();

}

if (event.getType() == TouchEvent.Type.SCALE_MOVE ||
    event.getType() == TouchEvent.Type.SCALE_END) {

    float scale_factor = scale * (event.getScaleSpan() / distance);

    if (scale_factor > 1.45) { scale_factor = 1.45f; }
    if (scale_factor < 0.8) { scale_factor = 0.8f; }

game_node_child.setLocalScale(scale_factor);

}

// ................................................................................................

// Гра -> Назад або Гра -> Меню
if ((key.equals("Back") || key.equals("Menu")) &&
     game_state == 4 &&
     l_help.getVisibility() == View.GONE) { handler.sendEmptyMessage(1); }

// Почати гру -> Назад
else if (key.equals("Back") && game_state == 3) { handler.sendEmptyMessage(8); }

// Меню -> Назад
else if (key.equals("Back") && game_state == 2) { handler.sendEmptyMessage(9); }

}
};

///////////////////////////////////////////////////////////////////////////////////////////////////
// Обробка довгих натискань
public static final AnalogListener analog_Listener = new AnalogListener() {

@Override
public void onAnalog (String name, float value, float tpf) {

delta = value;

if (delta > 0.01 * (touch_sensitive + 1))   { delta = 0.01f * (touch_sensitive + 1); }
if (delta < 0.0003 * (touch_sensitive + 1)) { delta = 0f; }

///////////////////////////////////////////////////////////////////////////////////////////////////

if (game_state == 3) {

if (name.equals("y-")) { preview_rotate_angle += delta * FastMath.RAD_TO_DEG * 3; }
if (name.equals("y+")) { preview_rotate_angle -= delta * FastMath.RAD_TO_DEG * 3; }

if (preview_rotate_angle <= 0)  { preview_rotate_angle = 0;  }
if (preview_rotate_angle >= 90) { preview_rotate_angle = 90; }

}

///////////////////////////////////////////////////////////////////////////////////////////////////

if (game_state == 4) {

if (name.equals("x+")) { game_node_main.rotate(0, delta * 3, 0);  }
if (name.equals("x-")) { game_node_main.rotate(0, delta * -3, 0); }

if (name.equals("y+")) {
    Vector3f xAxis = game_node_child.worldToLocal(Vector3f.UNIT_X, null);
    Quaternion quaternion = new Quaternion();
    game_node_child.rotate(quaternion.fromAngleAxis(delta * -3, xAxis));
}

if (name.equals("y-")) {
    Vector3f xAxis = game_node_child.worldToLocal(Vector3f.UNIT_X, null);
    Quaternion quaternion = new Quaternion();
    game_node_child.rotate(quaternion.fromAngleAxis(delta * 3, xAxis));
}

}

///////////////////////////////////////////////////////////////////////////////////////////////////

}
};

///////////////////////////////////////////////////////////////////////////////////////////////////
// Обробка натискань на view компоненти
public static void on_View_Click (View view) {

if (view.getId() == R.id.n_34) { view.startAnimation(press_animation);
                                 Utils.click_processing(view.getId()); }

if (view.getId() == R.id.n_05 &&
    !Rate_Dialog.rate_is_show &&
    !Rate_Dialog.app_is_rated &&
     Rate_Dialog.show_rate_dialog &&
     Flood_it_Activity.app_load_count > 3) { Rate_Dialog.show_Rate_Dialog();
                                             return; }

if (hide_off && !anim_is_running && !second_anim_run) {

view.startAnimation(press_animation);

switch (view.getId()) {

case R.id.n_01: // Start Game
case R.id.n_02: // High Score
case R.id.n_03: // Settings
case R.id.n_04: // About
case R.id.n_05: // Exit
case R.id.n_07: // Exit -> No
case R.id.n_08: // Exit -> Yes
case R.id.n_11: // About -> Back
case R.id.n_16: // Settings -> Back
case R.id.n_18: // High Score -> Back
case R.id.n_20: // Start Game -> Back
case R.id.n_21: // Start Game -> Easy or Normal or Hard
case R.id.l_01: // <<
case R.id.l_02: // >>

hide_off = false;
Utils.background_Fade_In(view.getId());
break;

///////////////////////////////////////////////////////////////////////////////////////////////////

case R.id.n_22: // Start Game -> Play

if (level_is_lock) { Utils.show_Lock_Message(); }
else { hide_off = false;
       Utils.background_Fade_In(view.getId());
       loading.setVisibility(View.VISIBLE);
       loading.startAnimation(background_fade_in); }
break;

///////////////////////////////////////////////////////////////////////////////////////////////////

case R.id.debug: debug_index += (debug_index < 2 ? 1 : -2); break;

case R.id.logo_3d: rotate_index = rotate_index > 90 ? 40 : rotate_index; break;

///////////////////////////////////////////////////////////////////////////////////////////////////

default:
Utils.click_processing(view.getId());
break;

}

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////

}
