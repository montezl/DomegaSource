package com.dmj.util.quartz;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

/* loaded from: QuartzManager.class */
public class QuartzManager {
    public static int DISTRIBUTE_QUESTION_JOB_COMPLATE_COUNT;
    public static int DISTRIBUTE_QUESTION_JOB_MAX_COUNT;
    public static int INTERVAL_MINUTES;
    public static int INTERVAL_QUESTIONGROUP_SECOND;
    public static int MAX_STEAL_QUENUM;
    private static Logger log = Logger.getLogger("stealLogger");
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    public static String JOB_NAME = "QUESTION_GROUP_PROGRESS_JOB";
    public static String JOB_GROUP_NAME = "ZHT_QUESTION_GROUP_PROGRESS_JOB_GROUP";
    public static String TRIGGER_NAME = "QUESTION_GROUP_PROGRESS_TRIGGER";
    public static String TRIGGER_GROUP_NAME = "ZHT_QUESTION_GROUP_PROGRESS_TRIGGER_GROUP";
    public static long intervalSeconds = 0;
    public static String DISTRIBUTE_QUESTION_GROUP_JOB_NAME = "DISTRIBUTE_QUESTION_GROUP_JOB";
    public static String DISTRIBUTE_QUESTION_GROUP_JOB_GROUP_NAME = "ZHT_DISTRIBUTE_QUESTION_GROUP_JOB_GROUP";
    public static String DISTRIBUTE_QUESTION_GROUP_TRIGGER_NAME = "DISTRIBUTE_QUESTION_GROUP_TRIGGER";
    public static String DISTRIBUTE_QUESTION_GROUP_TRIGGER_GROUP_NAME = "ZHT_DISTRIBUTE_QUESTION_GROUP_TRIGGER_GROUP";
    public static String DISTRIBUTE_QUESTION_JOB_NAME = "DISTRIBUTE_QUESTION_JOB";
    public static String DISTRIBUTE_QUESTION_JOB_GROUP_NAME = "ZHT_DISTRIBUTE_QUESTION_JOB_GROUP";
    public static String DISTRIBUTE_QUESTION_TRIGGER_NAME = "DISTRIBUTE_QUESTION_TRIGGER";
    public static String DISTRIBUTE_QUESTION_TRIGGER_GROUP_NAME = "ZHT_DISTRIBUTE_QUESTION_TRIGGER_GROUP";
    public static String HIKARI_MONITOR_JOB_NAME = "HIKARI_MONITOR_JOB_NAME";
    public static String HIKARI_MONITOR_JOB_GROUP_NAME = "HIKARI_MONITOR_JOB_GROUP_NAME";
    public static String HIKARI_MONITOR_TRIGGER_NAME = "HIKARI_MONITOR_TRIGGER_NAME";
    public static String HIKARI_MONITOR_TRIGGER_GROUP_NAME = "HIKARI_MONITOR_TRIGGER_GROUP_NAME";
    public static String IMGAGE_CHECK_JOB_NAME = "IMGAGE_CHECK_JOB_NAME";
    public static String IMGAGE_CHECK_JOB_GROUP_NAME = "IMGAGE_CHECK_JOB_GROUP_NAME";
    public static String IMGAGE_CHECK_TRIGGER_NAME = "IMGAGE_CHECK_TRIGGER_NAME";
    public static String IMGAGE_CHECK_TRIGGER_GROUP_NAME = "IMGAGE_CHECK_TRIGGER_GROUP_NAME";
    public static String ONLINE_USER_JOB_NAME = "ONLINE_USER_JOB_NAME";
    public static String ONLINE_USER_JOB_GROUP_NAME = "ONLINE_USER_JOB_GROUP_NAME";
    public static String ONLINE_USER_TRIGGER_NAME = "ONLINE_USER_TRIGGER_NAME";
    public static String ONLINE_USER_TRIGGER_GROUP_NAME = "ONLINE_USER_TRIGGER_GROUP_NAME";
    public static String SCAN_PROCESS_JOB_NAME = "SCAN_PROCESS_JOB_NAME";
    public static String SCAN_PROCESS_JOB_GROUP_NAME = "SCAN_PROCESS_JOB_GROUP_NAME";
    public static String SCAN_PROCESS_TRIGGER_NAME = "SCAN_PROCESS_TRIGGER_NAME";
    public static String SCAN_PROCESS_TRIGGER_GROUP_NAME = "SCAN_PROCESS_TRIGGER_GROUP_NAME";
    public static String BIGTABLE_DATA_JOB_NAME = "BIGTABLE_DATA_JOB_NAME";
    public static String BIGTABLE_DATA_JOB_GROUP_NAME = "BIGTABLE_DATA_JOB_GROUP_NAME";
    public static String BIGTABLE_DATA_TRIGGER_NAME = "BIGTABLE_DATA_TRIGGER_NAME";
    public static String BIGTABLE_DATA_TRIGGER_GROUP_NAME = "BIGTABLE_DATA_TRIGGER_GROUP_NAME";
    public static String SUBJECT_PROGRESS_JOB_NAME = "SUBJECT_PROGRESS_JOB_NAME";
    public static String SUBJECT_PROGRESS_JOB_GROUP_NAME = "SUBJECT_PROGRESS_JOB_GROUP_NAME";
    public static String SUBJECT_PROGRESS_TRIGGER_NAME = "SUBJECT_PROGRESS_TRIGGER_NAME";
    public static String SUBJECT_PROGRESS_TRIGGER_GROUP_NAME = "SUBJECT_PROGRESS_TRIGGER_GROUP_NAME";
    public static String YUEJUAN_PROGRESS_JOB_NAME = "YUEJUAN_PROGRESS_JOB_NAME";
    public static String YUEJUAN_PROGRESS_JOB_GROUP_NAME = "YUEJUAN_PROGRESS_JOB_GROUP_NAME";
    public static String YUEJUAN_PROGRESS_TRIGGER_NAME = "YUEJUAN_PROGRESS_TRIGGER_NAME";
    public static String YUEJUAN_PROGRESS_TRIGGER_GROUP_NAME = "YUEJUAN_PROGRESS_TRIGGER_GROUP_NAME";
    public static String REMOVE_COMPLETED_GROUP_JOB_NAME = "REMOVE_COMPLETED_GROUP_JOB_NAME";
    public static String REMOVE_COMPLETED_GROUP_JOB_GROUP_NAME = "REMOVE_COMPLETED_GROUP_JOB_GROUP_NAME";
    public static String REMOVE_COMPLETED_GROUP_TRIGGER_NAME = "REMOVE_COMPLETED_GROUP_TRIGGER_NAME";
    public static String REMOVE_COMPLETED_GROUP_TRIGGER_GROUP_NAME = "REMOVE_COMPLETED_GROUP_TRIGGER_GROUP_NAME";
    public static String OPEN_OCS_JOB_NAME = "OPEN_OCS_JOB_NAME";
    public static String OPEN_OCS_JOB_GROUP_NAME = "OPEN_OCS_JOB_GROUP_NAME";
    public static String OPEN_OCS_TRIGGER_NAME = "OPEN_OCS_TRIGGER_NAME";
    public static String OPEN_OCS_TRIGGER_GROUP_NAME = "OPEN_OCS_TRIGGER_GROUP_NAME";
    public static long DISTRIBUTE_QUESTION_JOB_BEGIN = 0;
    public static int DISTRIBUTE_QUESTION_JOB_THREAD_COUNT = 10;
    public static int MIN_DISTRIBUTE_QUENUM = 10;
    public static boolean DISTRIBUTE_QUESTION_GROUP_JOB_FIRST_RUN = true;
    public static final Byte o = (byte) 1;

    static {
        DISTRIBUTE_QUESTION_JOB_COMPLATE_COUNT = -1;
        DISTRIBUTE_QUESTION_JOB_MAX_COUNT = 10;
        INTERVAL_MINUTES = 10;
        INTERVAL_QUESTIONGROUP_SECOND = 30;
        MAX_STEAL_QUENUM = 100;
        try {
            ResourceBundle resource = ResourceBundle.getBundle("quartz");
            int threadCount = Integer.valueOf(resource.getString("org.quartz.threadPool.threadCount")).intValue();
            if (threadCount < 12) {
                DISTRIBUTE_QUESTION_JOB_COMPLATE_COUNT = 10;
            } else {
                DISTRIBUTE_QUESTION_JOB_MAX_COUNT = (int) Math.ceil(Integer.valueOf(resource.getString("org.quartz.threadPool.threadCount")).intValue() * 0.8d);
            }
            INTERVAL_MINUTES = Integer.valueOf(resource.getString("intervalMinutes")).intValue();
            INTERVAL_QUESTIONGROUP_SECOND = Integer.valueOf(resource.getString("interval_questiongroup_second")).intValue();
            MAX_STEAL_QUENUM = Integer.valueOf(resource.getString("MaxStealQueNum")).intValue();
        } catch (Exception e) {
        }
    }

    public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class jobClass, String cron, Map<String, Object> arg) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
            if (arg != null) {
                jobDetail.getJobDataMap().putAll(arg);
            }
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            triggerBuilder.withIdentity(triggerName, triggerGroupName);
            triggerBuilder.startNow();
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
            CronTrigger trigger = triggerBuilder.build();
            sched.scheduleJob(jobDetail, trigger);
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void modifyJobTime(String jobName, String jobGroupName, String triggerName, String triggerGroupName, String cron) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            CronTrigger trigger = sched.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                triggerBuilder.withIdentity(triggerName, triggerGroupName);
                triggerBuilder.startNow();
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                sched.rescheduleJob(triggerKey, triggerBuilder.build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            sched.pauseTrigger(triggerKey);
            sched.unscheduleJob(triggerKey);
            sched.deleteJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void startJobs() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void shutdownJobs() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void addQuestinGroupProgressJob(int sec, ServletContext context) {
        Map<String, Object> map = new HashMap<>();
        map.put("context", context);
        addJob(JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME, NewQuestionGroupProgressJob.class, "0/" + sec + " * * * * ?", map);
    }

    public static void addHikariMonitorJob(int sec) {
        addJob(HIKARI_MONITOR_JOB_NAME, HIKARI_MONITOR_JOB_GROUP_NAME, HIKARI_MONITOR_TRIGGER_NAME, HIKARI_MONITOR_TRIGGER_GROUP_NAME, HikariMonitorJob.class, "0/" + sec + " * * * * ?", null);
    }

    public static void addScanProcessJob(int sec) {
        addJob(SCAN_PROCESS_JOB_NAME, SCAN_PROCESS_JOB_GROUP_NAME, SCAN_PROCESS_TRIGGER_NAME, SCAN_PROCESS_TRIGGER_GROUP_NAME, HikariMonitorJob.class, "0/" + sec + " * * * * ?", null);
    }

    public static void addImageCheckJob(int sec, ServletContext context) {
        Map<String, Object> map = new HashMap<>();
        map.put("context", context);
        addJob(IMGAGE_CHECK_JOB_NAME, IMGAGE_CHECK_JOB_GROUP_NAME, IMGAGE_CHECK_TRIGGER_NAME, IMGAGE_CHECK_TRIGGER_GROUP_NAME, ImageCheckJob.class, "0/" + sec + " * * * * ?", map);
    }

    public static void addOnlineUserJob(int sec, ServletContext context) {
        Map<String, Object> map = new HashMap<>();
        map.put("context", context);
        addJob(ONLINE_USER_JOB_NAME, ONLINE_USER_JOB_GROUP_NAME, ONLINE_USER_TRIGGER_NAME, ONLINE_USER_TRIGGER_GROUP_NAME, OnlineUserJob.class, "0/" + sec + " * * * * ?", map);
    }

    public static void addBigTableDataJob(int min, ServletContext context) {
        Map<String, Object> map = new HashMap<>();
        map.put("context", context);
        addJob(BIGTABLE_DATA_JOB_NAME, BIGTABLE_DATA_JOB_GROUP_NAME, BIGTABLE_DATA_TRIGGER_NAME, BIGTABLE_DATA_TRIGGER_GROUP_NAME, BigTableDataJob.class, "0 0/" + min + " * * * ?", map);
    }

    public static void addSubjectProgressJob(int min, ServletContext context) {
        Map<String, Object> map = new HashMap<>();
        map.put("context", context);
        addJob(SUBJECT_PROGRESS_JOB_NAME, SUBJECT_PROGRESS_JOB_GROUP_NAME, SUBJECT_PROGRESS_TRIGGER_NAME, SUBJECT_PROGRESS_TRIGGER_GROUP_NAME, SubjectProgressJob.class, "0 0/" + min + " * * * ?", map);
    }

    public static void addYuejuanProgressJob(int min, ServletContext context) {
        Map<String, Object> map = new HashMap<>();
        map.put("context", context);
        addJob(YUEJUAN_PROGRESS_JOB_NAME, YUEJUAN_PROGRESS_JOB_GROUP_NAME, YUEJUAN_PROGRESS_TRIGGER_NAME, YUEJUAN_PROGRESS_TRIGGER_GROUP_NAME, YuejuanProgressJob.class, "0 0/" + min + " * * * ?", map);
    }

    public static void removeCompletedGroup(int min, ServletContext context) {
        Map<String, Object> map = new HashMap<>();
        map.put("context", context);
        addJob(REMOVE_COMPLETED_GROUP_JOB_NAME, REMOVE_COMPLETED_GROUP_JOB_GROUP_NAME, REMOVE_COMPLETED_GROUP_TRIGGER_NAME, REMOVE_COMPLETED_GROUP_TRIGGER_GROUP_NAME, RemoveCompletedGroup.class, "0 0/" + min + " * * * ?", map);
    }

    public static void modifyQuestinGroupProgressJobTime(int sec) {
        modifyJobTime(JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME, "0/" + sec + " * * * * ?");
    }

    public static void removeQuestinGroupProgressJob() {
        removeJob(JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME);
    }

    public static void addDistributeQuestionGroupJob() {
        Map<String, Object> map = new HashMap<>();
        addJob(DISTRIBUTE_QUESTION_GROUP_JOB_NAME, DISTRIBUTE_QUESTION_GROUP_JOB_GROUP_NAME, DISTRIBUTE_QUESTION_GROUP_TRIGGER_NAME, DISTRIBUTE_QUESTION_GROUP_TRIGGER_GROUP_NAME, DistributeQuestionGroupJob.class, "0 0/" + INTERVAL_MINUTES + " * * * ?", map);
    }

    public static void addDistributeQuestionJob(List<List<Object[]>> allList) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            for (int i = 0; i < allList.size(); i++) {
                List<Object[]> list = allList.get(i);
                String uuid = UUID.randomUUID().toString();
                JobDetail jobDetail = JobBuilder.newJob(DistributeQuestionJob.class).withIdentity(DISTRIBUTE_QUESTION_JOB_NAME + uuid, DISTRIBUTE_QUESTION_JOB_GROUP_NAME).build();
                jobDetail.getJobDataMap().put("list", list);
                SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger().withIdentity(DISTRIBUTE_QUESTION_TRIGGER_NAME + uuid, DISTRIBUTE_QUESTION_TRIGGER_GROUP_NAME).startAt(new Date()).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3).withRepeatCount(0)).build();
                sched.scheduleJob(jobDetail, simpleTrigger);
                Thread.sleep(10L);
            }
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void Set_DISTRIBUTE_QUESTION_JOB_COMPLATE_COUNT(boolean clear) {
        if (clear) {
            DISTRIBUTE_QUESTION_JOB_COMPLATE_COUNT = 0;
            return;
        }
        DISTRIBUTE_QUESTION_JOB_COMPLATE_COUNT++;
        log.info("   @@@@@@@@@@@@@  分发题组线程的完成情况：   @@@@@@@@@@@   " + DISTRIBUTE_QUESTION_JOB_COMPLATE_COUNT + "/" + DISTRIBUTE_QUESTION_JOB_THREAD_COUNT);
        if (DISTRIBUTE_QUESTION_JOB_COMPLATE_COUNT == DISTRIBUTE_QUESTION_JOB_THREAD_COUNT) {
            log.info("   *****************  分发总线程分发结束       *****************  用时：" + (System.currentTimeMillis() - DISTRIBUTE_QUESTION_JOB_BEGIN));
        }
    }

    public static void addOpenOcsJob(int min, ServletContext context) {
        Map<String, Object> map = new HashMap<>();
        map.put("context", context);
        addJob(OPEN_OCS_JOB_NAME, OPEN_OCS_JOB_GROUP_NAME, OPEN_OCS_TRIGGER_NAME, OPEN_OCS_TRIGGER_GROUP_NAME, OpenOcsJob.class, "0 0/" + min + " * * * ?", map);
    }

    public static String printDateStr() {
        return String.format(" %1$tY-%1$te-%1$tm %1$tH:%1$tM:%1$tS:%1$tL", new Date());
    }

    public static void main(String[] args) {
        try {
            addJob(JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME, TestJob2.class, "0/5 * * * * ?", new HashMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
