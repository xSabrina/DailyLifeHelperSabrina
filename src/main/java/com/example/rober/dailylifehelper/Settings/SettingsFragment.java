package com.example.rober.dailylifehelper.Settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.rober.dailylifehelper.R;

public class SettingsFragment extends PreferenceFragment {

    /*
        creates preferences.xml depending on clicked header
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        String settings = getArguments().getString("settings");
        if (settings.equals("notifications")){
            addPreferencesFromResource(R.xml.preferences_notifications);
        }
        if (settings.equals("gps")){
            addPreferencesFromResource(R.xml.preferences_gps);
        }

    }

}
