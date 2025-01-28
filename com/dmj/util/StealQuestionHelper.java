package com.dmj.util;

import com.dmj.daoimpl.awardPoint.AwardPointDaoImpl;
import com.dmj.util.quartz.QuartzManager;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

/* loaded from: StealQuestionHelper.class */
public class StealQuestionHelper {
    private String examPaperNum;
    private String groupNum;
    private String insertUser;
    private int maxStealQuNum;
    private String groupType;
    private int fenfaTimeout;
    private String isShuangPing;
    private int sleep = 5;
    static byte[] lock = new byte[1];
    private static final Logger log = Logger.getLogger("stealLogger");
    static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    static AwardPointDaoImpl dao = new AwardPointDaoImpl();

    public StealQuestionHelper(String examPaperNum, String groupNum, String insertUser, String groupType, int maxStealQuNum, int fenfaTimeout, String isShuangPing) {
        this.examPaperNum = examPaperNum;
        this.groupNum = groupNum;
        this.insertUser = insertUser;
        this.groupType = groupType;
        this.maxStealQuNum = maxStealQuNum;
        this.fenfaTimeout = fenfaTimeout * 1000;
        this.isShuangPing = isShuangPing;
    }

    public void dispatcher() {
        if (check()) {
            steal();
        }
    }

    public boolean check() {
        boolean flag;
        long begin = System.currentTimeMillis();
        while (true) {
            synchronized (lock) {
                flag = queue.contains(this.groupNum);
                if (!flag) {
                    queue.add(this.groupNum);
                }
            }
            if (flag) {
                try {
                    Thread.sleep(this.sleep);
                    this.fenfaTimeout -= this.sleep;
                } catch (InterruptedException e) {
                }
                if (this.fenfaTimeout <= 0) {
                    log.info(String.format("%s   试卷：%s --- 题组：%s --- 用户：%s --- 类型：%s  排队超时结束 ,用时：%s。。。", QuartzManager.printDateStr(), this.examPaperNum, this.groupNum, this.insertUser, this.groupType, Long.valueOf(System.currentTimeMillis() - begin)));
                    return false;
                }
                continue;
            } else {
                log.info(String.format("%s   试卷：%s --- 题组：%s --- 用户：%s --- 类型：%s  排队结束 ,用时：%s。。。", QuartzManager.printDateStr(), this.examPaperNum, this.groupNum, this.insertUser, this.groupType, Long.valueOf(System.currentTimeMillis() - begin)));
                return true;
            }
        }
    }

    public void steal() {
        long begin = System.currentTimeMillis();
        try {
            try {
                dao.stealQuestion(this.examPaperNum, this.groupNum, this.insertUser, this.groupType, this.maxStealQuNum, this.isShuangPing);
                log.info(String.format("%s   试卷：%s --- 题组：%s --- 用户：%s --- 类型：%s  分发存储过程执行结束,用时：%s 。。。", QuartzManager.printDateStr(), this.examPaperNum, this.groupNum, this.insertUser, this.groupType, Long.valueOf(System.currentTimeMillis() - begin)));
                unlock();
            } catch (Exception e) {
                log.info(String.format("%s   试卷：%s --- 题组：%s --- 用户：%s --- 类型：%s  分发存储过程执行结束，发生异常,用时：%s 。。。", QuartzManager.printDateStr(), this.examPaperNum, this.groupNum, this.insertUser, this.groupType, Long.valueOf(System.currentTimeMillis() - begin)));
                unlock();
            }
        } catch (Throwable th) {
            unlock();
            throw th;
        }
    }

    public void unlock() {
        queue.remove(this.groupNum);
    }
}
