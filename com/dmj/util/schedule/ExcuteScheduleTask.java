package com.dmj.util.schedule;

import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.DateUtil;
import com.dmj.util.schedule.task.Timertask;
import com.zht.db.ServiceFactory;
import java.util.Date;
import java.util.TimerTask;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;

/* loaded from: ExcuteScheduleTask.class */
public class ExcuteScheduleTask {
    public Logger log = Logger.getLogger(getClass());
    private SystemService his = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());

    public void executeScheduleTask(ServletContext context, String filePath) {
        try {
            Timertask task = new Timertask();
            Date date = DateUtil.getScheduleStartTime();
            TimerTask cleanSMSmsg = new CleanSMSmsg(filePath);
            task.addTask(cleanSMSmsg, date, 86400000L, "删除userMessage表多余数据");
            TimerUtil.TimerList.add(cleanSMSmsg);
            TimerTask updateExamComplete = new UpdateExamComplete();
            task.addTask(updateExamComplete, date, 86400000L, "创建超过一个月的考试修改为完成状态");
            TimerUtil.TimerList.add(updateExamComplete);
            TimerTask deleteDownloadTempFile = new DeleteDownloadTempFile(filePath);
            task.addTask(deleteDownloadTempFile, date, 86400000L, "删除项目中的下载临时文件");
            TimerUtil.TimerList.add(deleteDownloadTempFile);
            TimerTask deleteCornerInfo = new DeleteCornerInfo();
            task.addTask(deleteCornerInfo, date, 86400000L, "删除半年前的折角表数据");
            TimerUtil.TimerList.add(deleteCornerInfo);
            TimerTask deleteGenerateFile = new DeleteGenerateFile(filePath);
            task.addTask(deleteGenerateFile, date, 86400000L, "删除半年前的生成文件数据");
            TimerUtil.TimerList.add(deleteGenerateFile);
            TimerTask deleteLog = new DeleteLog(filePath);
            task.addTask(deleteLog, date, 86400000L, "删除超过一个月的log日志");
            TimerUtil.TimerList.add(deleteLog);
            long para1 = Long.valueOf(this.his.timesplitList().trim()).longValue();
            if (Long.toString(para1).equals("0")) {
                Long.valueOf(this.his.begintimesplitList().trim()).longValue();
            }
            long para2 = Long.valueOf(this.his.deletetimeList().trim()).longValue();
            if (Long.toString(para2).equals("0")) {
            }
        } catch (Exception e) {
            this.log.info("定时任务报错", e);
            e.printStackTrace();
        }
    }
}
