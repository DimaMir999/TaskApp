package org.dimamir999.testapp.services;

import android.text.format.Time;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by dimamir999 on 03.02.16.
 */
public class CurrentDateService {

    private Time getCurrentTime(){
        Time time = new Time(Time.getCurrentTimezone());
        time.setToNow();
        return time;
    }

    public Date getCurrentDateStart(){
        Time time = getCurrentTime();
        Time startTime = new Time();
        startTime.set(time.monthDay, time.month, time.year);
        Date startDate = new Date(startTime.toMillis(true));
        return startDate;
    }

    public Date getCurrentDateEnd(){
        Time time = getCurrentTime();
        Time endTime = new Time();
        time.set(time.toMillis(true) + TimeUnit.DAYS.toMillis(1));
        endTime.set(time.monthDay, time.month, time.year);
        Date endDate = new Date(endTime.toMillis(true));
        return endDate;
    }

    public Date getCurrentDateTime(){
        return new Date(System.currentTimeMillis());
    }
}
