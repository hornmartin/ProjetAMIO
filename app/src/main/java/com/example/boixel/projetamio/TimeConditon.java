package com.example.boixel.projetamio;

import android.content.Intent;

import java.sql.Time;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by martin on 13/03/2017.
 */

public class TimeConditon {
    private String name;
    private Set<Integer> days;
    private int lowerBoundInSeconds;
    private int upperBoundInSeconds;
    private boolean isOn;
    private boolean onChange;
    private static final int SECONDS_PER_DAY = 24*60*60;
    private static final int SECONDS_PER_WEEK = SECONDS_PER_DAY*7;
    private static final Map<String,Integer> DAYS;
    static {
        Map<String,Integer> aMap = new HashMap<>();
        aMap.put("thursday", 0);
        aMap.put("friday",1);
        aMap.put("saturday",2);
        aMap.put("sunday",3);
        aMap.put("monday",4);
        aMap.put("tuesday",5);
        aMap.put("wednesday",6);
        DAYS = Collections.unmodifiableMap(aMap);
    }
    public TimeConditon(String name){
        this.name=name;
        days = new HashSet<>();
        lowerBoundInSeconds =0;
        upperBoundInSeconds =24*60-1;
        isOn=true;
        onChange=false;
    }

    public TimeConditon withName(String name){
        this.name = name;
        return this;
    }

    public TimeConditon withDay(String day){
        if(DAYS.containsKey(day))
            days.add(DAYS.get(day));
        return this;
    }

    public TimeConditon withUpperBound(int upperBoundInSeconds){
        this.upperBoundInSeconds = upperBoundInSeconds;
        return this;
    }

    public TimeConditon withLowerBound(int lowerBoundInSeconds){
        this.lowerBoundInSeconds = lowerBoundInSeconds;
        return this;
    }

    public TimeConditon withNotifyOnChange(boolean onChange){
        this.onChange = onChange;
        return this;
    }

    public TimeConditon withNotifyWhenOn(boolean isOn){
        this.isOn = isOn;
        return this;
    }

    public String getName(){
        return name;
    }

    private boolean isInWindow(long timestampInSeconds){
        if(!days.contains((timestampInSeconds%SECONDS_PER_WEEK)/SECONDS_PER_DAY))
            return false;
        if((timestampInSeconds%SECONDS_PER_DAY)<lowerBoundInSeconds)
            return false;
        if((timestampInSeconds%SECONDS_PER_DAY)>upperBoundInSeconds)
            return false;
        return true;
    }

    public boolean checkMote(Mote mote){
        if(!isInWindow(mote.getLastUpdate()))
            return false;
        if(isOn){
            if(mote.isOnOrOff().equals("on")) {
                mote.addConditionsMatched(this);
                return true;
            }
        }
        if(onChange){
            if(mote.hasChange()) {
                mote.addConditionsMatched(this);
                return true;
            }
        }
        return false;
    }

}
