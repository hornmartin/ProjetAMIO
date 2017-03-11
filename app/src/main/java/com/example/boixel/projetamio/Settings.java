package com.example.boixel.projetamio;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * Created by boixel on 11/03/2017.
 */

public class Settings extends PreferenceActivity {

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("settings","settings");
        addPreferencesFromResource(R.xml.preferences);
    }
}
