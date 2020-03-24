package com.rutar.flood_it_3d;

import android.util.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.content.res.*;

import static com.rutar.flood_it_3d.Flood_it_Activity.*;

public class Unificator {

public static int W;
public static int H;
public static final int etalon_W = 960;
public static final int etalon_H = 540;

public static float w_coef;
public static float h_coef;

private static int s_index = 31;
private static int n_index = 34;
private static int l_index = 2;

public static TextView[] text_Views_Small  = new TextView[s_index];
public static TextView[] text_Views_Normal = new TextView[n_index];
public static TextView[] text_Views_Large  = new TextView[s_index];

///////////////////////////////////////////////////////////////////////////////////////////////////

public static void init() {

DisplayMetrics metrics = new DisplayMetrics();
activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
W = metrics.widthPixels;
H = metrics.heightPixels;

w_coef = W * 1f / etalon_W;
h_coef = H * 1f / etalon_H;

///////////////////////////////////////////////////////////////////////////////////////////////////

Resources res = activity.getResources();

for (int s = 0; s < s_index; s++) {

    String index = "s_" + (s < 9 ? "0" : "") + (s + 1);
    int id = res.getIdentifier(index, "id", activity.getPackageName());

    text_Views_Small[s] = (TextView) activity.findViewById(id);
    text_Views_Small[s].setTypeface(typeface);
    text_Views_Small[s].setTextSize(TypedValue.COMPLEX_UNIT_PX, 30 * h_coef);
    text_Views_Small[s].setShadowLayer(1, 3 * w_coef, 3 * w_coef, Color.BLACK);

}

for (int n = 0; n < n_index; n++) {

    String index = "n_" + (n < 9 ? "0" : "") + (n + 1);
    int id = res.getIdentifier(index, "id", activity.getPackageName());

    text_Views_Normal[n] = (TextView) activity.findViewById(id);
    text_Views_Normal[n].setTypeface(typeface);
    text_Views_Normal[n].setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * h_coef);
    text_Views_Normal[n].setShadowLayer(1, 3 * w_coef, 3 * w_coef, Color.BLACK);

}

for (int l = 0; l < l_index; l++) {

    String index = "l_" + (l < 9 ? "0" : "") + (l + 1);
    int id = res.getIdentifier(index, "id", activity.getPackageName());

    text_Views_Normal[l] = (TextView) activity.findViewById(id);
    text_Views_Normal[l].setTypeface(typeface);
    text_Views_Normal[l].setTextSize(TypedValue.COMPLEX_UNIT_PX, 120 * h_coef);
    text_Views_Normal[l].setShadowLayer(1, 3 * w_coef, 3 * w_coef, Color.BLACK);

}

///////////////////////////////////////////////////////////////////////////////////////////////////

}

public static void set_Buttons_Width (int index) {

for (int z = 0; z < buttons.length; z++) {
    buttons[z].setVisibility(View.VISIBLE);
    buttons[z].getLayoutParams().width = (int)(H / (index == 2 ? 4f : 3f));
}

if (index < 2) { buttons[3].setVisibility(View.GONE);
                 buttons[7].setVisibility(View.GONE); }

if (index < 1) { buttons[2].setVisibility(View.GONE);
                 buttons[6].setVisibility(View.GONE); }

}

///////////////////////////////////////////////////////////////////////////////////////////////////

}