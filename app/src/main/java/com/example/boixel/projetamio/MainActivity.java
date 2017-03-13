package com.example.boixel.projetamio;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static java.lang.Long.parseLong;

public class MainActivity extends AppCompatActivity {
    HashMap<String,HashMap<String, String>> moteDataList = new HashMap<String,HashMap<String, String>>();
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "Creation de l'activité");
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

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
                Log.d("mail", "button");
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                //emailIntent.setData(Uri.parse("mailto:meskhen@gmail.com"));
                emailIntent.setType("message/rfc822");
                //emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"malo.boixel@telecomnancy.net"});
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

        Boolean checkBoxState = sharedPref.getBoolean("onBoot", false);
        Log.d("CheckBoxStatus", checkBoxState.toString());
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            //put here whaterver you want your activity to do with the intent received
            String mote_data = intent.getStringExtra("data");
            Log.d("json", mote_data);
            parseJSON(mote_data);
            TextView mote1 = (TextView) findViewById(R.id.mote1);
            mote1.setText("Mote 9.138 "+moteDataList.get("9.138").get("value"));
            TextView mote2 = (TextView) findViewById(R.id.mote2);
            mote2.setText("Mote 81.77 "+moteDataList.get("81.77").get("value"));
            TextView mote3 = (TextView) findViewById(R.id.mote3);
            mote3.setText("Mote 153.11 "+moteDataList.get("153.111").get("value"));
            TextView mote4 = (TextView) findViewById(R.id.mote4);
            mote4.setText("Mote 53.105 "+moteDataList.get("53.105").get("value"));
            TextView mote5 = (TextView) findViewById(R.id.mote5);
            mote5.setText("Mote 77.106 "+moteDataList.get("77.106").get("value"));

            Date d =  new Date(parseLong(moteDataList.get("77.106").get("timestamp")));

            TextView time = (TextView) findViewById(R.id.timestamp);
            time.setText(d.toString().substring(0,19));
        }
    };

    private void parseJSON(String str){
        try {
            JSONObject reader = new JSONObject(str);
            JSONArray data = reader.getJSONArray("data");
            for(int i = 0; i < data.length(); i++){
                JSONObject obj = data.getJSONObject(i);

                String timestamp = obj.getString("timestamp");
                String label = obj.getString("label");
                String value = obj.getString("value");
                String mote = obj.getString("mote");

                HashMap<String, String> mote_data = new HashMap<>();

                mote_data.put("timestamp", timestamp);
                mote_data.put("label", label);
                mote_data.put("value", value);
                //mote_data.put("mote", mote);

                moteDataList.put(mote, mote_data);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("value"));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
    }

    public void onLightChange(){
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_name)
                        .setContentTitle("Alerte lumière!")
                        .setContentText("Une lampe est restée allumée!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
     // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
     // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        int mId = 3;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    public class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            Boolean checkBoxState = sharedPref.getBoolean("checkBoxState", false);
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
