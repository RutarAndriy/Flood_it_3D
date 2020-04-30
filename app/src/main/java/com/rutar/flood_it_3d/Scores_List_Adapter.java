package com.rutar.flood_it_3d;

import android.view.*;
import android.widget.*;

public class Scores_List_Adapter extends BaseAdapter {

private static final int adapter_size = 48;
private static Scores_List_Adapter adapter = null;

// ................................................................................................

static Scores_List_Adapter get_Instance() {

if (adapter == null) { adapter = new Scores_List_Adapter(); }
return adapter;

}

///////////////////////////////////////////////////////////////////////////////////////////////////

private Scores_List_Adapter() {




}

///////////////////////////////////////////////////////////////////////////////////////////////////

private Scores_List_Adapter set_Model_Names (String[] names) { return adapter; }

private Scores_List_Adapter set_Score_Values (int[] scores) { return adapter; }

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public int getCount() { return adapter_size; }

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public Object getItem (int position) {
    return null;
}

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public long getItemId (int position) {
    return 0;
}

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public View getView (int position, View convert_view, ViewGroup parent) {
    return null;
}

// Кінець класу Scores_List_Adapter ///////////////////////////////////////////////////////////////

}