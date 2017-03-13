package com.example.boixel.projetamio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import android.os.Parcel;
import android.os.Parcelable;

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
    private String notif;
    private double value;
    private long lastUpdate;
    private boolean hasChange;

    public LinkedList<Measure> measures;

    public Mote(String address){
        this.address = address;
        measures = new LinkedList<>();
        onOff = "off";
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

    public String getNotif(){
        return notif;
    }

    public double getValue(){
        return value;
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
        out.writeDouble(value);
        out.writeLong(lastUpdate);
        out.writeString(notif);
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
        value = in.readDouble();
        lastUpdate = in.readLong();
        notif = in.readString();
    }

    public Mote withMeasure(double value, long timestamp){
        if(measures.size() < MAX_MEASURES){
            measures.add(new Measure(value,timestamp));
            Collections.sort(measures);
        } else {
            if (timestamp > measures.getLast().getTimestamp()) {
                measures.removeFirst();
                measures.add(new Measure(value, timestamp));
            }
        }
        value = measures.getLast().getValue();
        lastUpdate = measures.getLast().getTimestamp();
        return this;
    }

    public boolean updateStatus() {
        hasChange = (Math.abs(measures.getLast().getValue()-measures.get(measures.size()-2).getValue())>CHANGE_GAP);
        onOff = "undecided";
        if(measures.getLast().getValue()>ON_OFF_THRESHOLD)
            onOff="on";
        else if(measures.getLast().getValue()<OFF_ON_THRESHOLD)
            onOff="off";
        return hasChange;
    }

    public boolean hasChange(){
        boolean result = hasChange;
        hasChange = false;
        return  result;
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
