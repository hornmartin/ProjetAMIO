package com.example.boixel.projetamio;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class WebService extends Service {
    private static final int NUMBER_OF_WINDOWS=3;
    private Timer myTimer;
    private MyTimerTask myTask;
    private ArrayList<TimeConditon> conditions;
    private HashMap<String,Mote> motes;
    private SharedPreferences sharedPref;
    private static final String[] days = {"monday","tuesday","wednesday","thursday","friday","saturday","sunday"};


    public WebService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int update_period = Integer.parseInt(sharedPref.getString("update", "10"));
        Log.d("WebService", "Service web lancé!");
        conditions = new ArrayList<>();
        updateConditions();
        motes = new HashMap<>();
        new getData().execute("http://iotlab.telecomnancy.eu/rest/data/1/light1/10");
        myTask = new MyTimerTask();
        myTimer = new Timer();
        myTimer.schedule(myTask, update_period*1000, update_period*1000);
        return START_STICKY;
    }

    class MyTimerTask extends TimerTask {
        public void run() {
            new getData().execute("http://iotlab.telecomnancy.eu/rest/data/1/light1/last");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myTimer.cancel();
        myTask.cancel();
        Log.d("WebService", "Service web terminé.");
    }

    private class getData extends AsyncTask<String , Void ,String> {
        String server_response;
        int responseCode = 0;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                responseCode = urlConnection.getResponseCode();
                Log.d("Debug", "http response : "+responseCode);
                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(responseCode != 200){
                Toast.makeText(getApplicationContext(),
                        "Error "+responseCode, Toast.LENGTH_LONG).show();
            }
            Log.e("Response", "" + server_response);
            if (server_response != null){
                updateMotes(server_response);
                checkNotificationCondition();
            }
        }


        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }
    }

    private void updateConditions(){
        while(conditions.size()<NUMBER_OF_WINDOWS)
            conditions.add(new TimeConditon("plage"+conditions.size()));
        String name;
        TimeConditon condition;
        for(int i=0;i<NUMBER_OF_WINDOWS;i++){
            condition=conditions.get(i);

            name = sharedPref.getString("window"+i,"default");
            condition.withName(name);
            if(name.equals("default"))
                Log.d("WebService","title fail");
            for(String d : days)
                if(sharedPref.getBoolean(d+i,false))
                    condition.withDay(d);
        }
    }

    private void updateMotes(String json){
        try {
            JSONObject reader = new JSONObject(json);
            JSONArray data = reader.getJSONArray("data");
            long timestamp;
            String mote;
            double value;
            for(int i = 0; i < data.length(); i++){
                JSONObject obj = data.getJSONObject(i);

                timestamp = obj.getLong("timestamp");
                while(timestamp>9999999999L)    //timestamp in seconds
                    timestamp/=1000;
                value = obj.getDouble("value");
                mote = obj.getString("mote");

                if(motes.containsKey(mote))
                    motes.get(mote).withMeasure(value, timestamp);
                else
                    motes.put(mote,new Mote(mote).withMeasure(value,timestamp));
            }
            for(Mote m : motes.values()){
                m.updateStatus();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkNotificationCondition(){
        Log.d("Web Service","check");
        boolean notify;
        for(Mote m : motes.values()){
            notify = false;
            for(TimeConditon t : conditions){
                if(t.checkMote(m))
                    notify=true;
            }
            if(notify)
                notify(m);
            sendBroadcast(m);
        }
    }



    private void sendBroadcast (Mote mote){
        Log.d("Web Service","broadcast");
        Intent intent = new Intent (MainActivity.RECEIVE_MOTE_INFO); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("mote", mote);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    public void notify(Mote mote){
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_name)
                        .setContentTitle("A light is on")
                        .setContentText(mote.toString());

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
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        int mId = 3;
        mNotificationManager.notify(mId, mBuilder.build());
    }


    public class BootBroadcastReceiver extends BroadcastReceiver {
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





