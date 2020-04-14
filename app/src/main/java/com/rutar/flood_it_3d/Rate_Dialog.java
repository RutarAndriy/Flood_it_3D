package com.rutar.flood_it_3d;

// Created by Rutar_Andriy on 27.03.2017.

import android.app.*;
import android.net.*;
import android.widget.*;
import android.content.*;

import static com.rutar.flood_it_3d.Flood_it_Activity.*;

// ................................................................................................

public class Rate_Dialog {

public static boolean app_is_rated = false;
public static boolean rate_is_show = false;

public static boolean show_rate_dialog = true;

///////////////////////////////////////////////////////////////////////////////////////////////////
// Показ діалогового вікна оцінювання

public static void show_Rate_Dialog() {

String app_name = activity.getString(R.string.app_name);
String rate_message = activity.getString(R.string.rate_text);

rate_message = String.format(rate_message, app_name);

///////////////////////////////////////////////////////////////////////////////////////////////////

AlertDialog.Builder builder = new AlertDialog.Builder(activity);

builder
.setCancelable(false)
.setTitle(R.string.app_name)
.setMessage(rate_message)

// Оцінити
///////////////////////////////////////////////////////////////////////////////////////////////////

.setPositiveButton(R.string.rate_yes, new DialogInterface.OnClickListener() {

    public void onClick (DialogInterface dialog, int id) {

    Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

    try { activity.startActivity(goToMarket);
          app_is_rated = true; }

    catch (Exception e) { Toast.makeText(activity, R.string.rate_error,
                          Toast.LENGTH_LONG).show(); }

    save_Rate_Settings();
    dialog.cancel();
    rate_is_show = false;

}})

// Пізніше
///////////////////////////////////////////////////////////////////////////////////////////////////

.setNeutralButton(R.string.rate_later, new DialogInterface.OnClickListener() {

            public void onClick (DialogInterface dialog, int id) {

    dialog.cancel();
    rate_is_show = false;

}})

// Ніколи
///////////////////////////////////////////////////////////////////////////////////////////////////

.setNegativeButton(R.string.rate_no, new DialogInterface.OnClickListener() {

            public void onClick (DialogInterface dialog, int id) {

    app_is_rated = true;

    save_Rate_Settings();
    dialog.cancel();
    rate_is_show = false;

}});

///////////////////////////////////////////////////////////////////////////////////////////////////

final AlertDialog dialog = builder.create();

dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

    @Override
    public void onDismiss (DialogInterface dialog) {

        rate_is_show = false;

    }
    });

dialog.show();

rate_is_show = true;
show_rate_dialog = false;

}

///////////////////////////////////////////////////////////////////////////////////////////////////

public static void save_Rate_Settings() {

SharedPreferences settings_reader = activity.getPreferences(MODE_PRIVATE);
SharedPreferences.Editor settings_writer = settings_reader.edit();

settings_writer.putBoolean("app_is_rated", app_is_rated);
settings_writer.putInt("app_load_count", app_load_count);
settings_writer.commit();

}

// Кінець класу <Rate_Dialog> /////////////////////////////////////////////////////////////////////

}
