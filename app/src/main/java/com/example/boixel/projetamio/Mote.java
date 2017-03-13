package com.example.boixel.projetamio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by martin on 13/03/2017.
 */

public class Mote {
    private static final int MAX_MEASURES = 10;
    public LinkedList<Measure> measures;

    public Mote(){
        measures = new LinkedList<>();
    }

    public void addMeasure(double value, long timestamp){
        if(measures.size() < MAX_MEASURES){
            measures.add(new Measure(value,timestamp));
            Collections.sort(measures);
        } else {
            if (timestamp > measures.getLast().getTimestamp()) {
                measures.removeFirst();
                measures.add(new Measure(value, timestamp));
            }
        }
    }


    public class Measure implements Comparable<Measure>{
        double value;
        long timestamp;

        public Measure(double value, long timestamp){
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
            return (int) (timestamp-o.getTimestamp());
        }
    }
}
