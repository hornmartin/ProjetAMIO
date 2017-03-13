package com.example.boixel.projetamio;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import java.util.Calendar;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * Created by boixel on 11/03/2017.
 */

public class Settings extends PreferenceActivity implements TimePickerDialog.OnTimeSetListener {

    private Calendar calendar;
    private TimePicker timePick;

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("settings","settings");
        addPreferencesFromResource(R.xml.preferences);

        Preference beginHour = (Preference) findPreference("beginHour");
        beginHour.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimeDialog();
                return false;
            }
        });

        Preference endHour = (Preference) findPreference("endHour");
        endHour.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimeDialog();
                return false;
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
    }

    private void showTimeDialog(){
        // Use the current date as the default date in the picker
        calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(this, this, hour, min, true).show();

    }
}
