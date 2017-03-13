package com.example.boixel.projetamio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.*;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Long.parseLong;

public class MainActivity extends AppCompatActivity {
    public static final String RECEIVE_MOTE_INFO = "RECEIVE_MOTE_INFO";
    private HashMap<String, Integer> motes;
    private ArrayList<Integer> views;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        motes = new HashMap<>();
        views = new ArrayList<>();
        views.add(R.id.mote1);
        views.add(R.id.mote2);
        views.add(R.id.mote3);
        views.add(R.id.mote4);
        views.add(R.id.mote5);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "Creation de l'activité");
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("endHours1", 6*60);
        editor.putInt("beginHours1", 23*60);
        editor.putInt("endHours2", 23*60);
        editor.putInt("beginHours2", 19*60);
        editor.putString("update", "10");
        editor.commit();

        ToggleButton serviceButton = (ToggleButton) findViewById(R.id.serviceButton);
        serviceButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                Log.d("service", "Button check");
                if (button.isChecked()) {
                    startService(new Intent(getBaseContext(), WebService.class));
                }
                else{
                    stopService(new Intent(getBaseContext(), WebService.class));
                }

            }
        });

        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("settings", "button");
                Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(settingsIntent);
            }
        });

        Button mailButton = (Button) findViewById(R.id.mailButton);

        mailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String mail_adress = sharedPref.getString("mail", "malo.boixel@telecomnancy.net");
                TextView moteText = (TextView) findViewById(R.id.mote1);
                String textMote = moteText.getText().toString()+"\n";
                moteText = (TextView) findViewById(R.id.mote2);
                textMote += moteText.getText().toString()+"\n";
                moteText = (TextView) findViewById(R.id.mote3);
                textMote += moteText.getText().toString()+"\n";
                moteText = (TextView) findViewById(R.id.mote4);
                textMote += moteText.getText().toString()+"\n";
                moteText = (TextView) findViewById(R.id.mote5);
                textMote += moteText.getText().toString()+"\n";
                Log.d("mail", "button");
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                //emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("message/rfc822");
                //emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Economie d'énergie");
                emailIntent.putExtra(Intent.EXTRA_TEXT, textMote);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {mail_adress});
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

        Log.d("all", sharedPref.getAll().toString());
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            //put here whaterver you want your activity to do with the intent received
            //Intent i = getIntent();
            if(!intent.getAction().equals(RECEIVE_MOTE_INFO)){
                return;
            }

            Mote mote = intent.getParcelableExtra("mote");
            int i;
            if(motes.containsKey(mote.getAddress())){
                i = motes.get(mote.getAddress());
            } else {
                if(motes.values().size()>views.size()){
                    i = new Random(views.size()).nextInt();
                } else {
                    i = motes.values().size();
                }
                motes.put(mote.getAddress(),i);
            }
            Log.d("WebService",i+"");
            ((TextView)findViewById(views.get(i))).setText(mote.toString());
            /*
            TextView moteText;
            switch(mote.getAddress()){
                case "9.138":
                    moteText = (TextView) findViewById(R.id.mote1);
                    moteText.setText(/*"Mote 9.138 "+mote.getLastValue()mote.toString());
                    break;
                case "81.77":
                    moteText = (TextView) findViewById(R.id.mote2);
                    moteText.setText("Mote 81.77 "+mote.getLastValue());
                    break;
                case "153.111":
                    moteText = (TextView) findViewById(R.id.mote3);
                    moteText.setText("Mote 153.11 "+mote.getLastValue());
                    break;
                case "53.105":
                    moteText = (TextView) findViewById(R.id.mote4);
                    moteText.setText("Mote 53.105 "+mote.getLastValue());
                    break;
                case "77.106":
                    moteText = (TextView) findViewById(R.id.mote5);
                    moteText.setText("Mote 77.106 "+mote.getLastValue());
                    break;
                default:
                    break;
            }
        */
            TextView time = (TextView) findViewById(R.id.timestamp);
        }
    };


    protected void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_MOTE_INFO);
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, intentFilter);
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
    }


    public class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            Boolean checkBoxState = sharedPref.getBoolean("onBoot", false);
            Log.d("CheckBoxStatus", checkBoxState.toString());
            if(checkBoxState){

                if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                    Intent i = new Intent(context, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }

        }

    }

}
