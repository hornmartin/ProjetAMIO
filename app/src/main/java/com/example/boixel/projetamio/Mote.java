package com.example.boixel.projetamio;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by boixel on 13/03/2017.
 */

public class Mote implements Parcelable{
    private static final int MAX_MEASURES = 10;
    private static final double ON_OFF_THRESHOLD=260.0;
    private static final double OFF_ON_THRESHOLD=230.0;
    private static final double CHANGE_GAP=60.0;
    private String address;
    private String onOff;
    private long since;
    private String conditionsMatched;
    private double lastValue;
    private long lastUpdate;
    private boolean hasChange;
    private long updateDeltaInSeconds;

    public LinkedList<Measure> measures;

    public Mote(String address){
        this.address = address;
        measures = new LinkedList<>();
        onOff = "undecided";
        conditionsMatched ="";
    }

    public String getAddress(){
        return address;
    }

    public String isOnOrOff(){
        return onOff;
    }

    public long getSince(){
        return since;
    }

    public void addConditionsMatched(TimeConditon condition){
        this.conditionsMatched +=condition.getName()+",";
    }

    public String getConditionsMatched(){
        if(conditionsMatched.length()>0)
            return conditionsMatched.substring(0, conditionsMatched.length()-1);
        return "";
    }

    public double getLastValue(){
        return lastValue;
    }

    public long getLastUpdate(){
        return lastUpdate;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(address);
        out.writeString(onOff);
        out.writeLong(since);
        out.writeDouble(lastValue);
        out.writeLong(lastUpdate);
        out.writeString(conditionsMatched);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Mote> CREATOR = new Parcelable.Creator<Mote>() {
        public Mote createFromParcel(Parcel in) {
            return new Mote(in);
        }

        public Mote[] newArray(int size) {
            return new Mote[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Mote(Parcel in) {
        address = in.readString();
        onOff = in.readString();
        since = in.readLong();
        lastValue = in.readDouble();
        lastUpdate = in.readLong();
        conditionsMatched = in.readString();
        updateDeltaInSeconds = 0;
    }

    public Mote withMeasure(double value, long timestamp){
        updateDeltaInSeconds = 0;
        int size = measures.size();
        Log.d("Web Service","Adding measure to a "+size+" list");
        if(size < MAX_MEASURES){
            measures.add(new Measure(value,timestamp));
            Collections.sort(measures);
            if(size>0)
                if(measures.getLast().getTimestamp()<measures.get(size-1).getTimestamp())
            Log.d("WebService", "tu sais pas compter");
        } else {
            updateDeltaInSeconds = timestamp - measures.getLast().getTimestamp();
            Log.d("Web Service","no new measures : "+timestamp+":"+measures.getLast().getTimestamp());
            if (updateDeltaInSeconds>0) {
                measures.removeFirst();
                measures.add(new Measure(value, timestamp));
            } else {
                updateDeltaInSeconds=0;
            }
        }
        this.lastValue = measures.getLast().getValue();
        lastUpdate = measures.getLast().getTimestamp();
        return this;
    }

    public void updateStatus() {
        hasChange = (Math.abs(measures.getLast().getValue()-measures.get(measures.size()-2).getValue())>CHANGE_GAP);
        String localOnOff = "undecided";
        if(measures.getLast().getValue()>ON_OFF_THRESHOLD)
            localOnOff="on";
        else if(measures.getLast().getValue()<OFF_ON_THRESHOLD)
            localOnOff="off";
        if(updateDeltaInSeconds>0) {
            if (localOnOff.equals(onOff))
                since+=updateDeltaInSeconds;
            else
                since=0;
            conditionsMatched = "";
        }
    }

    public boolean hasChange(){
        return  hasChange;
    }

    @Override
    public String toString(){
        String result ="Light near mote ";
        result+=address;
        if(onOff.equals("undecided"))
            result+=" may be on ";
        else
            result+=" is "+onOff;
        result+=" since "+since+" seconds\n";
        if(conditionsMatched.length()>0)
            result+=conditionsMatched+" conditions have been matched";
        result+= lastValue +" lm measured the "+(new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss")).format(new Date(lastUpdate));
        return result;
    }

    public class Measure implements Comparable<Measure> {
        double value;
        long timestamp;

        public Measure(double value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public double getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public int compareTo(Measure o) {
            return (int) (timestamp - o.getTimestamp());
        }
    }
}
