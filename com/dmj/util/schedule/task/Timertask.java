package com.dmj.util.schedule.task;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

/* loaded from: Timertask.class */
public class Timertask {
    public Logger log = Logger.getLogger(getClass());
    Timer timer = new Timer(true);

    public void addTask(TimerTask task, Date firstDate, long period, String message) {
        try {
            this.log.info(message + " start.............");
            this.timer.schedule(task, firstDate, period);
        } catch (Exception e) {
            this.log.info(message + " :error", e);
            e.printStackTrace();
        }
    }

    public void addgroupTask(TimerTask task, Date firstDate, long period, String message, Timer timer0) {
        try {
            timer0.schedule(task, firstDate, period);
        } catch (Exception e) {
            this.log.info(message + " :error", e);
            e.printStackTrace();
        }
    }
}
