package com.dmj.util.schedule.task;

import com.dmj.util.schedule.TimerUtil;
import com.zht.db.DbUtils;
import java.util.TimerTask;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

/* loaded from: ShutDown.class */
public class ShutDown implements ServletContextListener {
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
            defaultScheduler.shutdown(true);
            DbUtils.closeDs();
            for (TimerTask t : TimerUtil.TimerList) {
                try {
                    t.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
