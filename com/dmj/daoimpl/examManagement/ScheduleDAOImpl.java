package com.dmj.daoimpl.examManagement;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import com.dmj.action.base.HttpUrlImageUtils.ImageStreamUtil;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.Exampaper;
import com.dmj.domain.vo.Imgpath;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.Util;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/* loaded from: ScheduleDAOImpl.class */
public class ScheduleDAOImpl {
    public HttpServletRequest request;
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    Util myUtil = new Util();
    Logger log = Logger.getLogger(getClass());
    SystemService ssi = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    public String picUrl = this.ssi.getImageServerUri();

    public List<String> getOutDateOfExam() {
        Map args = StreamMap.create().put("OUTDATE_EXAM", (Object) Const.OUTDATE_EXAM);
        this.log.info("getOutDateOfExam 获取达到移动到历史表的考试条件的考试编号  sql : select DISTINCT examNum from exam  where (UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(updateDate))/(24*60*60) > {OUTDATE_EXAM}  and isDelete != 'T' ");
        return this.dao2._queryColList("select DISTINCT examNum from exam  where (UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(updateDate))/(24*60*60) > {OUTDATE_EXAM}  and isDelete != 'T' ", String.class, args);
    }

    public List<String> getExamPaperNumByExamnum(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryColList("select exampaperNum from exampaper where examnum = {examNum} ", String.class, args);
    }

    public Integer moveData() throws Exception {
        List<String> examNums = getOutDateOfExam();
        if (null == examNums || examNums.size() <= 0) {
            this.log.info("定时线程：【历史表数据】   没有过期的考试数据需要移动！ ");
            return null;
        }
        for (String examNum : examNums) {
            this.log.info("过期的考试num ：" + examNum);
            moveByExamnum(examNum);
            moveByExampapernum(examNum);
        }
        return null;
    }

    public String moveByExamnum(String examNum) throws Exception {
        new ArrayList();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        List<String> tableNames = this.myUtil.getTableNameByType(Const.EXPORTREPORT_examNum);
        for (String tname : tableNames) {
            String newTablename = "his_" + DateUtil.getLastYearString() + "_" + tname;
            if (!authTableExistsAndCreate(tname, newTablename)) {
                this.log.info("定时线程：【历史表数据】 table：" + tname + " authTableExistsAndCreate 方法返回false ... ");
            } else {
                String sql = "insert into " + newTablename + " select * from " + tname + " where examNum = {examNum} ";
                Integer returnCode = Integer.valueOf(this.dao2._execute(sql, args));
                if (null == returnCode) {
                    this.log.info("定时线程：【历史表数据】 table：" + tname + " 挪动到历史表返回null ，抛出异常，使数据回滚");
                    throw new Exception("定时线程-移动数据table：" + tname + "失败");
                }
                String del = "delete from " + tname + " where examNum = {examNum} ";
                Integer reCode = Integer.valueOf(this.dao2._execute(del, args));
                if (null == reCode) {
                    this.log.info("定时线程：【历史表数据】 移动数据到历史表后， 删除原table：" + tname + "  时返回null，抛出异常，使数据回滚");
                    throw new Exception("定时线程-移动完成后，删除table：" + tname + "数据失败");
                }
            }
        }
        return null;
    }

    public Integer moveByExampapernum(String examNum) throws Exception {
        new ArrayList();
        List<String> tableNames = this.myUtil.getTableNameByType("exampaperNum");
        Map args = new HashMap();
        args.put("epNum", examNum);
        List<String> eps = getExamPaperNumByExamnum(examNum);
        if (null == eps || eps.size() <= 0) {
            this.log.info("定时线程：【历史表数据】 根据考试编号 " + examNum + "查询相关的exampaperNum结果集为空。");
            return null;
        }
        for (String str : eps) {
            for (String tname : tableNames) {
                String newTablename = "his_" + DateUtil.getLastYearString() + "_" + tname;
                if (!authTableExistsAndCreate(tname, newTablename)) {
                    this.log.info("定时线程：【历史表数据】 table：" + newTablename + " authTableExistsAndCreate 方法返回false ... ");
                } else {
                    String sql = "insert into " + newTablename + " select * from " + tname + " where exampaperNum = {epNum} ";
                    Integer returnCode = Integer.valueOf(this.dao2._execute(sql, args));
                    if (null == returnCode) {
                        this.log.info("定时线程：【历史表数据】 table：" + tname + " 挪动到历史表返回null ，抛出异常，使数据回滚");
                        throw new Exception("定时线程-移动数据失败");
                    }
                    String del = "delete from " + tname + " where exampaperNum = {epNum} ";
                    Integer reCode = Integer.valueOf(this.dao2._execute(del, args));
                    if (null == reCode) {
                        this.log.info("定时线程：【历史表数据】 移动数据到历史表后， 删除原table：" + tname + "  时返回null，抛出异常，使数据回滚");
                        throw new Exception("定时线程-移动完成后，删除table：" + tname + "数据失败");
                    }
                }
            }
        }
        return 1;
    }

    public boolean authTableExistsAndCreate(String tableName, String newTablename) {
        boolean result = authTableExists(newTablename);
        if (result) {
            return true;
        }
        boolean returunCode = createTable(tableName, newTablename);
        if (returunCode) {
            return true;
        }
        return false;
    }

    public boolean authTableExists(String tableName) {
        String sql = "show tables LIKE '" + tableName + "'";
        List<?> queryColList = this.dao2.queryColList(sql);
        if (null != queryColList && queryColList.size() > 0) {
            return true;
        }
        return false;
    }

    public boolean createTable(String fromTable, String newTable) {
        String sql = "create table " + newTable + " select * from " + fromTable + " where 1=2";
        Integer returnCode = Integer.valueOf(this.dao2.execute(sql));
        if (null != returnCode) {
            return true;
        }
        return false;
    }

    public String updateExamComplete() {
        try {
            List<Object[]> res = this.dao2._queryArrayList("select e.examNum,ep.examPaperNum from  (select examNum,status from exam where (UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(exam.examDate))/(24*60*60) > 30 ) e left join exampaper ep on ep.examNum=e.examNum  where e.status!='9' ", null);
            String[] gudingRoleNumArr = {"4", "3"};
            for (int i = 0; i < res.size(); i++) {
                Map args = new HashMap();
                String epNum = String.valueOf(res.get(i)[1]);
                args.put("epNum", epNum);
                this.dao2._execute("insert into taskhistory (select * from task where exampaperNum={epNum} ) ", args);
                this.dao2._execute("delete from task where exampaperNum={epNum} ", args);
                String examNum = String.valueOf(res.get(i)[0]);
                if (!"-1".equals(examNum)) {
                    this.ssi.deleteUserroleByExam(examNum, gudingRoleNumArr);
                }
            }
        } catch (Exception e) {
            this.log.error("定时任务：修改完成考试状态-删除任务表报错 ", e);
        }
        return Convert.toStr(Integer.valueOf(this.dao2._execute("UPDATE exam SET status= '9' where (UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(exam.examDate))/(24*60*60) > 30 ", null)));
    }

    public List<String> getTableNameByType(String type, String newpath) {
        Properties pro = new Properties();
        String path = newpath + "WEB-INF/history_data.properties";
        List<String> tableNameList = new ArrayList<>();
        try {
            InputStream in = new FileInputStream(path);
            pro.load(in);
            String tableNameStr = "";
            if (type.equals(Const.EXPORTREPORT_examNum)) {
                tableNameStr = pro.getProperty(Const.EXPORTREPORT_examNum);
            }
            if (type.equals("exampaperNum")) {
                tableNameStr = pro.getProperty("exampaperNum");
            }
            if (tableNameStr != null) {
                String[] tableNameArray = tableNameStr.split(Const.STRING_SEPERATOR);
                for (String tableName : tableNameArray) {
                    tableNameList.add(tableName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.log.info("获取删除数据 表名异常 ");
        }
        return tableNameList;
    }

    public Integer optimizeTable(String path) {
        List<String> sqls = new ArrayList<>();
        List<String> list1 = getTableNameByType("exampaperNum", path);
        sqls.add("OPTIMIZE TABLE class");
        sqls.add("OPTIMIZE TABLE student");
        sqls.add("OPTIMIZE TABLE tempscore");
        for (String str1 : list1) {
            String str = "OPTIMIZE TABLE ";
            sqls.add(str + str1);
        }
        List<String> list2 = getTableNameByType(Const.EXPORTREPORT_examNum, path);
        for (String strExam : list2) {
            String strExam1 = "OPTIMIZE TABLE ";
            sqls.add(strExam1 + strExam);
        }
        int[] returnCode = this.dao2.batchExecute(sqls);
        if (null == returnCode) {
            return null;
        }
        return Integer.valueOf(returnCode.length);
    }

    public String deleteExamMessage(Integer days, Integer examNum) {
        Map args = new HashMap();
        args.put("days", days);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        try {
            if (examNum.intValue() == -100) {
                List examlist = this.dao2._queryColList("  select  examNum from exam    where ((UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(examDate))/(24*60*60)) >{days} and STATUS='9' and isDelete='F'", args);
                for (int i = 0; i < examlist.size(); i++) {
                    String examNums = String.valueOf(examlist.get(i));
                    args.put("examNums", examNums);
                    Imgpath ip = (Imgpath) this.dao2._queryBean("select location locationurl,filename tableimg  from imgpath where examNum={examNums} and selected='1'", Imgpath.class, args);
                    String scoreimgurl = ip.getLocationurl() + "/" + ip.getTableimg() + "/3";
                    String Pic_AbPage = ip.getLocationurl() + "/" + ip.getTableimg() + "/5";
                    String Pic_Illegal = ip.getLocationurl() + "/" + ip.getTableimg() + "/4";
                    getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + scoreimgurl);
                    getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_AbPage);
                    getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_Illegal);
                }
            } else if (examNum.intValue() != -100) {
                Imgpath ip2 = (Imgpath) this.dao2._queryBean("select location locationurl,filename tableimg  from imgpath where examNum={examNum} and selected='1'", Imgpath.class, args);
                String scoreimgurl2 = ip2.getLocationurl() + "/" + ip2.getTableimg() + "/3";
                String Pic_AbPage2 = ip2.getLocationurl() + "/" + ip2.getTableimg() + "/5";
                String Pic_Illegal2 = ip2.getLocationurl() + "/" + ip2.getTableimg() + "/4";
                getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + scoreimgurl2);
                getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_AbPage2);
                getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_Illegal2);
            }
            Object[] params = {days};
            this.dao2.execute("CALL deleteExamMessage(?)", params);
            if (examNum.intValue() == -100) {
                addExamLog(null, "删除批量考试部分信息", -1, "");
            } else if (examNum.intValue() != -100) {
                addExamLog(examNum, "删除单次考试部分信息", -1, "");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String deleteExam(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select location locationurl,filename tableimg  from imgpath where examNum={examNum} and selected='1'", Imgpath.class, args);
        String Pic_ExaminationNum = ip.getLocationurl() + "/" + ip.getTableimg() + "/1";
        String Pic_Question = ip.getLocationurl() + "/" + ip.getTableimg() + "/2";
        String scoreimgurl = ip.getLocationurl() + "/" + ip.getTableimg() + "/3";
        String Pic_Illegal = ip.getLocationurl() + "/" + ip.getTableimg() + "/4";
        String Pic_AbPage = ip.getLocationurl() + "/" + ip.getTableimg() + "/5";
        String Pic_Big = ip.getLocationurl() + "/" + ip.getTableimg() + "/6";
        String Pic_SrcQuestion = ip.getLocationurl() + "/" + ip.getTableimg() + "/7";
        String Pic_SrcAnswer = ip.getLocationurl() + "/" + ip.getTableimg() + "/8";
        addExamLog(Integer.valueOf(examNum), "删除单次考试所有信息", -1, "");
        try {
            Object[] params = {examNum};
            this.dao2.execute("CALL deleteExam(?)", params);
            getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_ExaminationNum);
            getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_Question);
            getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + scoreimgurl);
            getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_Illegal);
            getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_AbPage);
            getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_Big);
            getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_SrcQuestion);
            getImageStream(this.picUrl + "imageAction!deleteOneFile.action" + Pic_SrcAnswer);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addExamLog(Integer examNum, String name, Integer userid, String desc) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("userid", userid);
        args.put("CurrentTime", DateUtil.getCurrentTime());
        args.put("name", name);
        if (examNum != null) {
            this.dao2._execute("insert into examlog(examNum,insertUser,insertDate,operate)values({examNum},{userid},{CurrentTime},{name})", args);
        } else {
            this.dao2._execute("insert into examlog(insertUser,insertDate,operate)values({userid},{CurrentTime},{name})", args);
        }
    }

    public boolean resetIdRecordValue() {
        try {
            this.dao2.execute("CALL resetIdRecordValue()");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteuserMessage() {
        this.dao2.execute("delete from userMessage where YYYYMMDD != DATE_FORMAT(now(),'%Y-%m')");
        this.dao2.execute("DELETE cs from changeschool_student cs INNER JOIN( select id from changeschool_student where ((UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(insertDate))/(24*60*60)) > 180 )cs1 on cs.id=cs1.id");
        this.dao2.execute("DELETE cs from changeschool_teacher cs INNER JOIN( select id from changeschool_teacher where ((UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(insertDate))/(24*60*60)) > 180 )cs1 on cs.id=cs1.id");
    }

    public void deleteCornerInfo() {
        Map args = new HashMap();
        List<Object> regidList = this.dao2.queryColList("select regid from corner where ((UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(insertDate))/(24*60*60)) > 180 order by insertDate asc");
        for (int i = 0; i < regidList.size(); i++) {
            args.put("regidListi", regidList.get(i));
            this.dao2._execute("delete from corner where regid = {regidListi} ", args);
        }
    }

    public void deleteGenerateFile(String rootPath) {
        List<?> queryBeanList = this.dao2.queryBeanList("select DISTINCT examNum,gradeNum from managefile where ((UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(insertDate))/(24*60*60)) > 180 order by insertDate asc", Exampaper.class);
        String[] resourceTypeArr = {"0", "1", "2"};
        for (int i = 0; i < queryBeanList.size(); i++) {
            int exam = ((Exampaper) queryBeanList.get(i)).getExamNum().intValue();
            int grade = ((Exampaper) queryBeanList.get(i)).getGradeNum().intValue();
            this.log.info("删除生成文件-考试：" + exam + "---年级：" + grade);
            String dirFolder = rootPath + "generateFile" + File.separator + "0" + File.separator;
            String dirFolder1 = rootPath + "generateFile" + File.separator + "1" + File.separator;
            for (int j = 0; j < resourceTypeArr.length; j++) {
                String deleteDirFolder = dirFolder + resourceTypeArr[j] + File.separator + exam + File.separator + grade;
                String deleteDirFolder1 = dirFolder1 + resourceTypeArr[j] + File.separator + exam + File.separator + grade;
                deleteFileAndDirect(deleteDirFolder);
                deleteFileAndDirect(deleteDirFolder1);
            }
            Map args = StreamMap.create().put("exam", (Object) Integer.valueOf(exam)).put("grade", (Object) Integer.valueOf(grade));
            this.dao2._execute("delete from managefile where examNum={exam} and gradeNum={grade} ", args);
        }
    }

    public void deleteLog(String rootPath) {
        String dirFolder = rootPath + "log";
        deleteExpireFile(dirFolder);
    }

    public void deleteExpireFile(String dirFolder) {
        File dir = new File(dirFolder);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                Calendar cal = Calendar.getInstance();
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cal.setTimeInMillis(file.lastModified());
                long betweenDay = cn.hutool.core.date.DateUtil.between(cn.hutool.core.date.DateUtil.date(), cn.hutool.core.date.DateUtil.date(cal), DateUnit.DAY);
                if (betweenDay >= 30) {
                    deleteSingleFile_file(files[i].toString());
                }
            }
        }
    }

    public void deleteFileAndDirect(String dirFolder) {
        File dir = new File(dirFolder);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files.length == 0) {
                deleteEmptyDir(dir);
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    deleteSingleFile_file(files[i].toString());
                } else {
                    deleteDirect(files[i]);
                }
            }
            deleteEmptyDir(dir);
        }
    }

    public void deleteDirect(File dird) {
        if (dird.isDirectory()) {
            File[] files = dird.listFiles();
            if (files.length == 0) {
                deleteEmptyDir(dird);
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    deleteSingleFile_file(files[i].toString());
                } else {
                    deleteDirect(files[i]);
                }
            }
            deleteEmptyDir(dird);
        }
    }

    public void deleteSingleFile_file(String srcName) {
        File file = new File(srcName);
        file.delete();
    }

    public void deleteEmptyDir(File dir) {
        if (dir.isDirectory() && dir.listFiles().length == 0) {
            dir.delete();
        }
    }

    public byte[] getImageStream(String urlString) {
        return ImageStreamUtil.getImageStream(urlString);
    }

    public boolean Skip() {
        return true;
    }
}
