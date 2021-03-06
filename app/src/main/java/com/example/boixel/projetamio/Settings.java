package com.example.boixel.projetamio;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import java.util.Calendar;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * Created by boixel on 11/03/2017.
 */

public class Settings extends PreferenceActivity implements TimePickerDialog.OnTimeSetListener {

    private Calendar calendar;
    private TimePicker timePick;
    private String picker;

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("settings","settings");
        addPreferencesFromResource(R.xml.preferences);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Preference beginHour = (Preference) findPreference("beginHour1");
        beginHour.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimeDialog(sharedPref.getInt("beginHours1", 0));
                picker = "begin1";
                return false;
            }
        });

        Preference endHour = (Preference) findPreference("endHour1");
        endHour.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimeDialog(sharedPref.getInt("endHours1", 0));
                picker = "end1";
                return false;
            }
        });

        Preference beginHour2 = (Preference) findPreference("beginHour2");
        beginHour2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimeDialog(sharedPref.getInt("beginHours2", 0));
                picker = "begin2";
                return false;
            }
        });

        Preference endHour2 = (Preference) findPreference("endHour2");
        endHour2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimeDialog(sharedPref.getInt("endHours2", 0));
                picker = "end2";
                return false;
            }
        });

        Preference beginHour3 = (Preference) findPreference("beginHour3");
        beginHour3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimeDialog(sharedPref.getInt("beginHours3", 0));
                picker = "begin3";
                return false;
            }
        });

        Preference endHour3 = (Preference) findPreference("endHour3");
        endHour3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimeDialog(sharedPref.getInt("endHours3", 0));
                picker = "end3";
                return false;
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch(picker){
            case "end1":
                editor.putInt("endHours1", hours*60+minutes);
                editor.commit();
                break;
            case "begin1":
                editor.putInt("beginHours1", hours*60+minutes);
                editor.commit();
                break;
            case "end2":
                editor.putInt("endHours2", hours*60+minutes);
                editor.commit();
                break;
            case "begin2":
                editor.putInt("beginHours2", hours*60+minutes);
                editor.commit();
                break;
            case "end3":
                editor.putInt("endHours3", hours*60+minutes);
                editor.commit();
                break;
            case "begin3":
                editor.putInt("beginHours3", hours*60+minutes);
                editor.commit();
                break;
            default:
                break;
        }
    }

    private void showTimeDialog(int hours){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("all", sharedPref.getAll().toString());
        calendar = Calendar.getInstance();
        int hour;
        int min;
        if(hours == 0) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            min = calendar.get(Calendar.MINUTE);
        }else {
            min = hours % 60;
            hour = (hours - (hours % 60))/60;
        }
        new TimePickerDialog(this, this, hour, min, true).show();

    }
}
