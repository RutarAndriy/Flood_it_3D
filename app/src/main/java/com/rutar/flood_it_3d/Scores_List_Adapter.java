package com.rutar.flood_it_3d;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.content.*;

import static com.rutar.flood_it_3d.Unification.*;

// ................................................................................................

public class Scores_List_Adapter extends BaseAdapter {

private static final int adapter_size = 48;

private static LayoutInflater inflater = null;
private static Scores_List_Adapter adapter = null;

private static String[] model_names = null;
private static String[] score_values = null;

// ................................................................................................

static Scores_List_Adapter get_Instance (Context context) {

if (adapter == null) { adapter = new Scores_List_Adapter(context); }
return adapter;

}

///////////////////////////////////////////////////////////////////////////////////////////////////

private Scores_List_Adapter (Context context) {

set_Model_Names(null);
set_Score_Values(null);

inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

}

///////////////////////////////////////////////////////////////////////////////////////////////////

Scores_List_Adapter set_Model_Names (String[] names) {

if (names != null &&
    names.length == adapter_size) { model_names = names; }

else { model_names = new String[0]; }

return adapter;

}

///////////////////////////////////////////////////////////////////////////////////////////////////

Scores_List_Adapter set_Score_Values (String[] scores) {

if (scores != null &&
    scores.length == adapter_size) { score_values = scores; }

else { score_values = new String[0]; }

return adapter;

}

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public int getCount() { return adapter_size; }

@Override
public Object getItem (int position) { return null; }

@Override
public long getItemId (int position) { return position; }

///////////////////////////////////////////////////////////////////////////////////////////////////

@SuppressLint("InflateParams")
@Override
public View getView (int position, View convert_view, ViewGroup parent) {

View view = convert_view;
if (view == null) { view = inflater.inflate(R.layout.score_list_item, null); }

TextView model = view.findViewById(R.id.model_name);
TextView score = view.findViewById(R.id.score_value);

model.setText(model_names[position]);
model.setTextSize(TypedValue.COMPLEX_UNIT_PX, 34 * h_coef);
model.setShadowLayer(1, 3 * w_coef, 3 * w_coef, Color.BLACK);

score.setText(score_values[position]);
score.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26 * h_coef);
score.setShadowLayer(1, 3 * w_coef, 3 * w_coef, Color.BLACK);

LinearLayout.LayoutParams model_params = (LinearLayout.LayoutParams) model.getLayoutParams();
model_params.setMargins(0,8,0,0);
model.setLayoutParams(model_params);

LinearLayout.LayoutParams score_params = (LinearLayout.LayoutParams) score.getLayoutParams();
score_params.setMargins(0,0,0,8);
score.setLayoutParams(score_params);

return view;

}

// Кінець класу Scores_List_Adapter ///////////////////////////////////////////////////////////////

}