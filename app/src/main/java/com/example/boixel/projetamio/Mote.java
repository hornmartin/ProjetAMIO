package com.example.boixel.projetamio;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by martin on 13/03/2017.
 */

public class Mote implements Parcelable {
    private int id;
    private boolean isOn;
    private long since;
    private boolean notif;
    private double value;
    private long lastUpdate;

    private static final int MAX_MEASURES = 10;
    public LinkedList<Measure> measures;

    public Mote() {
        measures = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public boolean getisOn() {
        return isOn;
    }

    public long getSince() {
        return since;
    }

    public boolean getNotif() {
        return notif;
    }

    public double getValue() {
        return value;
    }

    public long getLastUpdate() {
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
        out.writeInt(id);
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
        id = in.readInt();
    }

    public void addMeasure(double value, long timestamp) {
        if (measures.size() < MAX_MEASURES) {
            measures.add(new Measure(value, timestamp));
            Collections.sort(measures);
        } else {
            if (timestamp > measures.getLast().getTimestamp()) {
                measures.removeFirst();
                measures.add(new Measure(value, timestamp));
            }
        }
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

