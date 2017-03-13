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

        //CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);

        /*checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Checkbox", "Checkbox state changed");
                SharedPreferences sharedPref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);
                sharedPref.edit().putBoolean("checkBoxState", checkbox.isChecked()).apply();
            }
        });*/
    }
}
