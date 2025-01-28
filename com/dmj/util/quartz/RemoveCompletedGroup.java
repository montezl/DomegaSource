package com.dmj.util.quartz;

import com.dmj.domain.QuestionGroupInfo;
import com.dmj.service.examManagement.ExamService;
import com.dmj.serviceimpl.examManagement.ExamServiceImpl;
import com.dmj.util.Const;
import com.zht.db.ServiceFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
/* loaded from: RemoveCompletedGroup.class */
public class RemoveCompletedGroup implements Job {
    private static ExamService esc = (ExamService) ServiceFactory.getObject(new ExamServiceImpl());
    private Logger log4j = Logger.getLogger(getClass());

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            ServletContext context = (ServletContext) arg0.getJobDetail().getJobDataMap().get("context");
            ConcurrentHashMap<String, QuestionGroupInfo> map = (ConcurrentHashMap) context.getAttribute(Const.task_groupNum_defalt);
            List<String> list = (ArrayList) context.getAttribute(Const.wait_remove_groupNum);
            this.log4j.info("RemoveCompletedGroup::::" + map);
            if (map != null && map.size() > 0) {
                map.entrySet().parallelStream().forEach(new 1(this, list));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
