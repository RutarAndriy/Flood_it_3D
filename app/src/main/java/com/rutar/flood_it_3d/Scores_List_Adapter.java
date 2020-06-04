package com.rutar.flood_it_3d;

import android.util.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.annotation.*;

import static com.rutar.flood_it_3d.Constants.*;
import static com.rutar.flood_it_3d.Unification.*;

import static com.rutar.flood_it_3d.Flood_it_Activity.activity;

// ................................................................................................

public class Scores_List_Adapter extends BaseAdapter {

private final int adapter_size = 48;

private LayoutInflater inflater;

private ListView list_view = null;
private String[] model_names = null;
private String[] score_values = null;

private int elements_per_display = 5;

///////////////////////////////////////////////////////////////////////////////////////////////////
// Конструктор

Scores_List_Adapter (Context context) {

set_Model_Names(null);
set_Score_Values(null);

inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод задає імена моделей

void set_Model_Names (String[] names) {

if (names != null &&
    names.length == adapter_size) { model_names = names; }

else { model_names = new String[0]; }

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод задає числові значення рекордів

void set_Score_Values (String[] scores) {

if (scores != null &&
    scores.length == adapter_size) { score_values = scores; }

else { score_values = new String[0]; }

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

if (view == null)      { view = inflater.inflate(R.layout.score_list_item, null); }
if (list_view == null) { list_view = activity.findViewById(R.id.game_score_list); }

TextView model = view.findViewById(R.id.model_name);
TextView score = view.findViewById(R.id.score_value);

model.setText(model_names[position]);
model.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30 * h_coef);
model.setShadowLayer(1, 3 * w_coef, 3 * w_coef, Color.BLACK);

score.setText(score_values[position]);
score.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26 * h_coef);
score.setShadowLayer(1, 3 * w_coef, 3 * w_coef, Color.BLACK);

switch (position/(model_per_level)) {
    case 0: model.setTextColor(0xff7E65FE);break;
    case 1: model.setTextColor(0xff65F475);break;
    case 2: model.setTextColor(0xffFE6565);break;
    case 3: model.setTextColor(0xff747474);break;
}

view.setTag(position);
view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                   list_view.getHeight()/elements_per_display));

return view;

}

// Кінець класу Scores_List_Adapter ///////////////////////////////////////////////////////////////

}