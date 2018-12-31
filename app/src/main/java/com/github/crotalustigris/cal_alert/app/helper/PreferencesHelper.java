package com.github.crotalustigris.cal_alert.app.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.crotalustigris.cal_alert.R;

public class PreferencesHelper {

    public static void saveEnabledState(Context act, boolean isEnabled) {
        SharedPreferences pref = act.getSharedPreferences(act.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean(act.getString(R.string.pref_is_enabled), isEnabled);
        ed.apply();
    }

    public static boolean getEnabledState(Context act) {
        SharedPreferences pref = act.getSharedPreferences(act.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        return pref.getBoolean(act.getString(R.string.pref_is_enabled), true);

    }
}
