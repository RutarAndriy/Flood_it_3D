package com.rutar.flood_it_3d;

import android.util.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.content.res.*;

import static com.rutar.flood_it_3d.Flood_it_Activity.*;

// ................................................................................................

class Unification {

static int W;
static int H;
static final int etalon_W = 960;
static final int etalon_H = 540;

static float w_coef;
static float h_coef;

private static int s_index = 31;
private static int n_index = 36;
private static int l_index = 2;

static TextView[] text_Views_Small  = new TextView[s_index];
static TextView[] text_Views_Normal = new TextView[n_index];

///////////////////////////////////////////////////////////////////////////////////////////////////

static void init() {

DisplayMetrics metrics = new DisplayMetrics();
activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
W = metrics.widthPixels;
H = metrics.heightPixels;

w_coef = W * 1f / etalon_W;
h_coef = H * 1f / etalon_H;

///////////////////////////////////////////////////////////////////////////////////////////////////

Resources res = activity.getResources();

for (int s = 30; s < s_index; s++) {

    String index = "s_" + (s < 9 ? "0" : "") + (s + 1);
    int id = res.getIdentifier(index, "id", activity.getPackageName());

    text_Views_Small[s] = (TextView) activity.findViewById(id);
    //text_Views_Small[s].setTypeface(typeface);
    text_Views_Small[s].setTextSize(TypedValue.COMPLEX_UNIT_PX, 30 * h_coef);
    text_Views_Small[s].setShadowLayer(1, 3 * w_coef, 3 * w_coef, Color.BLACK);

}

for (int n = 0; n < n_index; n++) {

    String index = "n_" + (n < 9 ? "0" : "") + (n + 1);
    int id = res.getIdentifier(index, "id", activity.getPackageName());

    text_Views_Normal[n] = (TextView) activity.findViewById(id);
    //text_Views_Normal[n].setTypeface(typeface);
    text_Views_Normal[n].setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * h_coef);
    text_Views_Normal[n].setShadowLayer(1, 3 * w_coef, 3 * w_coef, Color.BLACK);

}

for (int l = 0; l < l_index; l++) {

    String index = "l_" + (l < 9 ? "0" : "") + (l + 1);
    int id = res.getIdentifier(index, "id", activity.getPackageName());

    text_Views_Normal[l] = (TextView) activity.findViewById(id);
    //text_Views_Normal[l].setTypeface(typeface);
    text_Views_Normal[l].setTextSize(TypedValue.COMPLEX_UNIT_PX, 120 * h_coef);
    text_Views_Normal[l].setShadowLayer(1, 3 * w_coef, 3 * w_coef, Color.BLACK);

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод задає розмір кнопок, залежно від складності рівня

static void set_Buttons_Width (int index) {

int button_width = (int)(H / 2.5f);;

switch (index) {
    case 1:  button_width = (int)(H / 3f); break;
    case 2:  button_width = (int)(H / 4f); break;
    case 3:  button_width = (int)(H / 5f); break;
}

// ................................................................................................

for (int z = 1; z <= 10; z++) {

    int id = activity.get_Id("b_" + (z != 10 ? "0" : "") + z);

    activity.findViewById(id).setVisibility(View.VISIBLE);
    activity.findViewById(id).getLayoutParams().width = button_width;

}

// ................................................................................................

if (index < 3) { activity.findViewById(R.id.b_09).setVisibility(View.GONE);
                 activity.findViewById(R.id.b_10).setVisibility(View.GONE); }

if (index < 2) { activity.findViewById(R.id.b_07).setVisibility(View.GONE);
                 activity.findViewById(R.id.b_08).setVisibility(View.GONE); }

if (index < 1) { activity.findViewById(R.id.b_05).setVisibility(View.GONE);
                 activity.findViewById(R.id.b_06).setVisibility(View.GONE); }

}

// Кінець класу <Unification> /////////////////////////////////////////////////////////////////////

}