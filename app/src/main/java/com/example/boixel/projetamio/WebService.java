package com.example.boixel.projetamio;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
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

    private Timer myTimer;
    private MyTimerTask myTask;
    private ArrayList<TimeConditon> conditions;
    private HashMap<String,Mote> motes;

    public WebService() {
    }

    private void sendBroadcast (ArrayList<HashMap<String, String>> moteDataList){
        Intent intent = new Intent ("value"); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("light_value1", moteDataList.get(moteDataList.size()-2).get("value"));
        intent.putExtra("light_value2", moteDataList.get(moteDataList.size()-1).get("value"));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("WebService", "Service web lancé!");
        conditions = new ArrayList<>();
        updateConditions();
        motes = new HashMap<>();


        myTask = new MyTimerTask();
        myTimer = new Timer();
        myTimer.schedule(myTask, 3000, 3000);
        return START_STICKY;
    }

    class MyTimerTask extends TimerTask {
        public void run() {
            URL url;
            HttpURLConnection urlConnection = null;
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
                value = obj.getDouble("value");
                mote = obj.getString("mote");

                if(motes.containsKey(mote))
                    motes.get(mote). addMeasure(value, timestamp);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkNotificationCondition(){
    }
}





