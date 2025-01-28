package com.dmj.daoimpl.systemManagement;

import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.Area;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.Baseinfolog;
import com.dmj.domain.Data;
import com.dmj.domain.Examlog;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Log;
import com.dmj.domain.OnlineUser;
import com.dmj.domain.QuestionManger;
import com.dmj.domain.School;
import com.dmj.domain.Userposition;
import com.dmj.util.Const;
import com.dmj.util.config.Configuration;
import com.zht.db.TypeEnum;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;

/* loaded from: SystemDAOImpl.class */
public class SystemDAOImpl {
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());

    public List getAeraInfo() {
        return this.dao.queryBeanList("select /* shard_host_HG=Write */ * from area ", Area.class);
    }

    public List<Data> getDataByType(String type) {
        Map args = new HashMap();
        args.put("type", type);
        return this.dao._queryBeanList("select  * from data where type = {type} order by orderNum", Data.class, args);
    }

    public List<Map<String, String>> getUserReportPosition(String type) {
        Map args = new HashMap();
        args.put("type", type);
        return this.dao._queryMapList("select d.id,d.value,d.name,r.qufen from data  d left join (select dvalue,1 qufen from report_position) r on d.value=r.dvalue where type={type} ", TypeEnum.StringObject, args);
    }

    public Object getOneByNum(String colum, String value, Class cla) throws Exception {
        if (colum == null || value == null) {
            return null;
        }
        String sql = "select * from " + cla.getSimpleName() + " where " + colum + " = {value} ";
        Map args = new HashMap();
        args.put("value", value);
        return this.dao._queryBean(sql, cla, args);
    }

    public List getSchools() {
        Map args = new HashMap();
        args.put("WriteNodeHostGroupQueryRule", "/* shard_host_HG=Write */");
        args.put("Data_SCHOOL_TYPE", "4");
        args.put("TRUE", "T");
        return this.dao._queryBeanList("select   s.id,s.schoolName,s.shortname,s.schooladdress,d.name schooltype,s.insertuser,s.insertdate,s.schoolNum from school  s left join data  d on d.type = {Data_SCHOOL_TYPE}  and d.value = s.schoolType where s.isDelete != {TRUE}  order by s.schoolNum", School.class, args);
    }

    public List getSchools(String schoolName) {
        Map args = new HashMap();
        args.put("WriteNodeHostGroupQueryRule", "/* shard_host_HG=Write */");
        args.put("Data_SCHOOL_TYPE", "4");
        args.put("TRUE", "T");
        String schoolNameStr = "";
        if (schoolName != null && !schoolName.equals("")) {
            schoolNameStr = " and s.schoolName like {schoolName} ";
            args.put("schoolName", "%" + schoolName + "%");
        }
        String sql = "select   s.id,s.schoolName,s.shortname,s.schooladdress,d.name schooltype,s.insertuser,s.insertdate,s.schoolNum,slg.schoolGroupNum,ifnull(slg.schoolGroupName,'') schoolGroupName,sl.logo,s.schoolId from school  s LEFT JOIN schoolgroup slg on s.id=slg.schoolNum left join data  d on d.type = {Data_SCHOOL_TYPE}  and d.value = s.schoolType LEFT JOIN schoollogo sl on sl.schoolNum=s.id  where s.isDelete != {TRUE} " + schoolNameStr + " order by convert(s.schoolName using gbk)";
        return this.dao._queryMapList(sql, null, args);
    }

    public List getSchoolQuotaList(String schoolName, String groupNum) {
        Map args = new HashMap();
        args.put("WriteNodeHostGroupQueryRule", "/* shard_host_HG=Write */");
        args.put("Data_SCHOOL_TYPE", "4");
        args.put("TRUE", "T");
        String oneGroupNum = groupNum.split("_")[0];
        String schoolNameStr = "";
        if (schoolName != null && !schoolName.equals("")) {
            schoolNameStr = " and s.schoolName like {schoolName} ";
            args.put("schoolName", "%" + schoolName + "%");
        }
        String sql = "select /* shard_host_HG=Write */ s.id,s.schoolName,s.shortname,s.schooladdress,d.name schooltype,s.insertuser,s.insertdate,s.schoolNum,slg.schoolGroupNum,ifnull(slg.schoolGroupName,'') schoolGroupName  from schoolquota sq LEFT JOIN school s on sq.schoolNum=s.id LEFT JOIN schoolgroup slg on s.id=slg.schoolNum left join data  d on d.type = {Data_SCHOOL_TYPE} and d.value = s.schoolType where sq.groupNum={oneGroupNum} and s.isDelete != {TRUE} " + schoolNameStr + " order by s.schoolNum";
        args.put("oneGroupNum", oneGroupNum);
        return this.dao._queryMapList(sql, null, args);
    }

    public Object deleteOneByNum(String colum, String value, Class cla) {
        if (colum == null || value == null) {
            return null;
        }
        Map args = new HashMap();
        args.put("SimpleName", cla.getSimpleName());
        args.put("colum", colum);
        args.put("value", value);
        return Integer.valueOf(this.dao._execute("delete from {SimpleName} where {colum}  = {value} ", args));
    }

    public Integer deleteschool(String schoolNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        if (null != this.dao._queryObject("select id from score where schoolNum ={schoolNum}  limit 1", args)) {
            return Integer.valueOf(this.dao._execute("update school set isDelete = 'T' where id={schoolNum} ", args));
        }
        this.dao._execute("delete from  school where id={schoolNum} ", args);
        this.dao._execute("delete from  class where schoolNum={schoolNum} ", args);
        this.dao._execute("delete from  grade where schoolNum={schoolNum} ", args);
        this.dao._execute("delete from  student where schoolNum={schoolNum} ", args);
        this.dao._execute("delete from  teacher where schoolNum={schoolNum} ", args);
        this.dao._execute("DELETE FROM `user` WHERE schoolNum={schoolNum} ", args);
        this.dao._execute("DELETE FROM userposition WHERE schoolNum={schoolNum} ", args);
        this.dao._execute("DELETE  ur FROM userrole ur LEFT JOIN user u ON ur.userNum=u.id WHERE u.schoolNum={schoolNum} ", args);
        return 1;
    }

    public Map getCurrentJie() {
        return this.dao._queryOrderMap("select gradeNum,max(jie) from grade where isDelete='F' group by gradeNum", TypeEnum.StringObject, null);
    }

    public List getObjectsByParam(Map map, Class cla) {
        if (null == map || map.size() <= 0) {
            return null;
        }
        Map args = new HashMap();
        args.put("SimpleName", cla.getSimpleName());
        StringBuffer sql = new StringBuffer("select * from " + cla.getSimpleName() + "  where 1=1 ");
        for (Object key : map.keySet()) {
            sql.append(" and " + key + " =  '" + map.get(key) + "' ");
        }
        this.log.info("  method:getObjectsByParam  sql: " + sql.toString());
        return this.dao._queryBeanList(sql.toString(), cla, args);
    }

    public List getExamLog(Examlog examLog) {
        Map args = new HashMap();
        String sql = "select exlog.id,exlog.operate,exlog.examNum,exlog.exampaperNum,sub.subjectName,exlog.studentId,exlog.classNum, exlog.insertUser,exlog.insertDate,exlog.Description,exlog.isDelete, stu.studentName,c.className,ex.examName,ex.examDate,g.gradeNum,g.gradeName,sch.schoolNum,sch.schoolName from examlog  exlog,exam ex,student stu,grade g,school sch,class c,exampaper ep,`subject` sub where exlog.studentId=stu.id and exlog.classNum=c.classNum and c.gradeNum=g.gradeNum and c.schoolNum=sch.schoolNum and exlog.exampaperNum=ep.examPaperNum and ep.subjectNum=sub.subjectNum ";
        if (null != examLog.getStudentId() && !examLog.getStudentId().equals("")) {
            sql = sql + " and stu.studentId={studentId} ";
            args.put(Const.EXPORTREPORT_studentId, examLog.getStudentId());
        }
        if (null != examLog.getClassNum() && !examLog.getClassNum().equals("")) {
            sql = sql + " and exlog.examNum={examNum} ";
            args.put(Const.EXPORTREPORT_examNum, examLog.getExamNum());
        }
        if (null != examLog.getGradeNum() && !examLog.getGradeNum().equals("")) {
            sql = sql + " and g.gradeNum={gradeNum} ";
            args.put(Const.EXPORTREPORT_gradeNum, examLog.getGradeNum());
        }
        if (null != examLog.getSchoolName() && !examLog.getSchoolName().equals("")) {
            sql = sql + " and sch.schoolName={schoolName} ";
            args.put("schoolName", examLog.getSchoolName());
        }
        return this.dao._queryBeanList(sql, Examlog.class, args);
    }

    public byte[] getimage(String id) {
        Map args = new HashMap();
        args.put("id", id);
        return this.dao._queryBlob(" select scoreImg  from scoreimage where id={id} ", args);
    }

    public List getBasicLog(Baseinfolog bLog, int pageStart, int pageSize) {
        Map args = new HashMap();
        String sql = "SELECT bg.*,stu.studentName,sch.schoolName,baseg.gradeName , u.realname  FROM baseinfolog bg   LEFT JOIN student stu    ON bg.studentId=stu.id  LEFT JOIN school sch   ON bg.schoolNum=sch.schoolNum   LEFT JOIN basegrade baseg   ON bg.gradeNum=baseg.gradeNum   LEFT JOIN user u ON u.id= bg.insertUser   WHERE 1=1   ";
        if (bLog.getStudentName() != null && !"".equals(bLog.getStudentName().trim())) {
            sql = sql + "AND u.realname LIKE {realname}   ";
            args.put("realname", "%" + bLog.getStudentName().trim() + "%");
        }
        if (bLog.getOperate() != null && !"".equals(bLog.getOperate().trim())) {
            sql = sql + "AND bg.operate LIKE {operate}   ";
            args.put("operate", "%" + bLog.getOperate().trim() + "%");
        }
        if (bLog.getGradeNum() != null && !"".equals(bLog.getGradeNum() + "".trim())) {
            sql = sql + "AND bg.gradeNum={gradeNum}  ";
            args.put(Const.EXPORTREPORT_gradeNum, bLog.getGradeNum() + "".trim());
        }
        if (bLog.getSchoolNum() != null && !"".equals(bLog.getSchoolNum() + "".trim())) {
            sql = sql + "AND bg.schoolNum={schoolNum}  ";
            args.put(Const.EXPORTREPORT_schoolNum, bLog.getSchoolNum() + "".trim());
        }
        String sql2 = sql + "\t ORDER BY bg.insertDate DESC LIMIT {pageStart},{pageSize} ";
        args.put("pageStart", Integer.valueOf(pageStart));
        args.put("pageSize", Integer.valueOf(pageSize));
        this.log.info("baselog 日志 sql---" + sql2);
        return this.dao._queryBeanList(sql2, Baseinfolog.class, args);
    }

    public Integer getAllBaseLogRowCount(Baseinfolog bLog) {
        Map args = new HashMap();
        String sql = "SELECT COUNT(1) FROM baseinfolog bg   LEFT JOIN student stu    ON bg.studentId=stu.id  LEFT JOIN school sch   ON bg.schoolNum=sch.schoolNum   LEFT JOIN basegrade baseg   ON bg.gradeNum=baseg.gradeNum   LEFT JOIN user u ON u.id= bg.insertUser   WHERE 1=1   ";
        if (bLog.getStudentName() != null && !"".equals(bLog.getStudentName().trim())) {
            sql = sql + "AND u.realname LIKE {realname}  ";
            args.put("realname", "%" + bLog.getStudentName().trim() + "%");
        }
        if (bLog.getOperate() != null && !"".equals(bLog.getOperate().trim())) {
            sql = sql + "AND bg.operate LIKE {operate}    ";
            args.put("operate", "%" + bLog.getOperate().trim() + "%");
        }
        if (bLog.getGradeNum() != null && !"".equals(bLog.getGradeNum() + "".trim())) {
            sql = sql + "AND bg.gradeNum={gradeNum}   ";
            args.put(Const.EXPORTREPORT_gradeNum, bLog.getGradeNum() + "".trim());
        }
        if (bLog.getSchoolNum() != null && !"".equals(bLog.getSchoolNum() + "".trim())) {
            sql = sql + "AND bg.schoolNum={schoolNum} ";
            args.put(Const.EXPORTREPORT_schoolNum, bLog.getSchoolNum() + "".trim());
        }
        this.log.info("baselog rowcount 日志 sql---" + sql);
        return this.dao._queryInt(sql, args);
    }

    public List getExamLog(Examlog eLog, int pageStart, int pageSize) {
        Map args = new HashMap();
        String sql = "SELECT elog.*,stu.studentName,er.examinationRoomName,bg.gradeName,sch.schoolName,e.examType,e.examName,e.examDate,sub.subjectName  ,u.realname  FROM examlog elog   LEFT JOIN student stu   ON elog.studentId=stu.id    LEFT JOIN school sch    ON stu.schoolNum=sch.id    LEFT JOIN exam e    ON elog.examNum=e.examNum    LEFT JOIN examPaper ep    ON elog.exampaperNum=ep.exampaperNum   LEFT JOIN basegrade bg   ON ep.gradeNum=bg.gradeNum    LEFT JOIN `subject` sub    ON ep.subjectNum=sub.subjectNum   LEFT JOIN examinationroom er    ON elog.examinationRoomNum=er.id   LEFT JOIN `user` u ON u.id = elog.insertUser  WHERE 1=1    ";
        if (eLog.getInputStuId() != null && !"".equals(eLog.getInputStuId() + "".trim())) {
            sql = sql + "AND elog.studentId=(SELECT id FROM student  WHERE studentId={studentId}  limit 1)";
            args.put(Const.EXPORTREPORT_studentId, eLog.getInputStuId() + "".trim());
        }
        if (eLog.getExamName() != null && !"".equals(eLog.getExamName().trim())) {
            sql = sql + "AND elog.examNum IN (SELECT DISTINCT examNum FROM exam WHERE examName LIKE {examName})   ";
            args.put("examName", "%" + eLog.getExamName().trim() + "%");
        }
        if ((eLog.getExamNum() != null && !"".equals(eLog.getExamNum() + "".trim())) || ((eLog.getGradeNum() != null && !"".equals(eLog.getGradeNum() + "".trim())) || (eLog.getSchoolNum() != null && !"".equals(eLog.getSchoolNum() + "".trim())))) {
            String sql2 = sql + "AND elog.exampaperNum IN   \t\t(\t\tSELECT DISTINCT exampaperNum   \t\tFROM exampaper    \t\tWHERE 1=1    ";
            if (eLog.getExamNum() != null && !"".equals(eLog.getExamNum() + "".trim())) {
                sql2 = sql2 + "\t\tAND examNum={examNum} ";
                args.put(Const.EXPORTREPORT_examNum, eLog.getExamNum() + "".trim());
            }
            if (eLog.getGradeNum() != null && !"".equals(eLog.getGradeNum() + "".trim())) {
                sql2 = sql2 + "\t\tAND gradeNum={gradeNum}   ";
                args.put(Const.EXPORTREPORT_gradeNum, eLog.getGradeNum() + "".trim());
            }
            sql = sql2 + "\t\t)   ";
        }
        if (eLog.getSchoolNum() != null && !"".equals(eLog.getSchoolNum() + "".trim())) {
            sql = sql + "\tAND sch.schoolNum={schoolNum} ";
            args.put(Const.EXPORTREPORT_schoolNum, eLog.getSchoolNum() + "".trim());
        }
        if (eLog.getOperate() != null && !"".equals(eLog.getOperate().trim())) {
            sql = sql + "AND elog.operate LIKE {operate}   ";
            args.put("operate", "%" + eLog.getOperate().trim() + "%");
        }
        String sql3 = sql + "\t\tORDER BY elog.insertDate DESC\t LIMIT {pageStart} ,{pageSize} ";
        args.put("pageStart", Integer.valueOf(pageStart));
        args.put("pageSize", Integer.valueOf(pageSize));
        this.log.info("examlog ---sql--" + sql3);
        return this.dao._queryBeanList(sql3, Examlog.class, args);
    }

    public Integer getAllExamLogRowCount(Examlog eLog) {
        Map args = new HashMap();
        String sql = "SELECT count(1)  FROM examlog elog   LEFT JOIN student stu   ON elog.studentId=stu.id    LEFT JOIN school sch    ON stu.schoolNum=sch.id    LEFT JOIN exam e    ON elog.examNum=e.examNum    LEFT JOIN examPaper ep    ON elog.exampaperNum=ep.exampaperNum   LEFT JOIN basegrade bg   ON ep.gradeNum=bg.gradeNum    LEFT JOIN `subject` sub    ON ep.subjectNum=sub.subjectNum   LEFT JOIN examinationroom er    ON elog.examinationRoomNum=er.id   LEFT JOIN `user` u ON u.id = elog.insertUser  WHERE 1=1    ";
        if (eLog.getInputStuId() != null && !"".equals(eLog.getInputStuId() + "".trim())) {
            sql = sql + "AND elog.studentId=(SELECT id FROM student  WHERE studentId={studentId} limit 1)";
            args.put(Const.EXPORTREPORT_studentId, eLog.getInputStuId() + "".trim());
        }
        if (eLog.getExamName() != null && !"".equals(eLog.getExamName().trim())) {
            sql = sql + "AND elog.examNum IN (SELECT DISTINCT examNum FROM exam WHERE examName LIKE {examName} )   ";
            args.put("examName", "%" + eLog.getExamName().trim() + "%");
        }
        if ((eLog.getExamNum() != null && !"".equals(eLog.getExamNum() + "".trim())) || ((eLog.getGradeNum() != null && !"".equals(eLog.getGradeNum() + "".trim())) || (eLog.getSchoolNum() != null && !"".equals(eLog.getSchoolNum() + "".trim())))) {
            String sql2 = sql + "AND elog.exampaperNum IN   \t\t(\t\tSELECT DISTINCT exampaperNum   \t\tFROM exampaper    \t\tWHERE 1=1    ";
            if (eLog.getExamNum() != null && !"".equals(eLog.getExamNum() + "".trim())) {
                sql2 = sql2 + "\t\tAND examNum={examNum}  ";
                args.put(Const.EXPORTREPORT_examNum, eLog.getExamNum() + "".trim());
            }
            if (eLog.getGradeNum() != null && !"".equals(eLog.getGradeNum() + "".trim())) {
                sql2 = sql2 + "\t\tAND gradeNum={gradeNum} ";
                args.put(Const.EXPORTREPORT_gradeNum, eLog.getGradeNum() + "".trim());
            }
            sql = sql2 + "\t\t)   ";
        }
        if (eLog.getSchoolNum() != null && !"".equals(eLog.getSchoolNum() + "".trim())) {
            sql = sql + "\tAND sch.schoolNum={schoolNum} ";
            args.put(Const.EXPORTREPORT_schoolNum, eLog.getSchoolNum() + "".trim());
        }
        if (eLog.getOperate() != null && !"".equals(eLog.getOperate().trim())) {
            sql = sql + "AND elog.operate LIKE {operate}    ";
            args.put("operate", "%" + eLog.getOperate().trim() + "%");
        }
        this.log.info("examlog rowcount ---sql--" + sql);
        return this.dao._queryInt(sql, args);
    }

    public List getAllLogs(Log log) {
        Map args = new HashMap();
        StringBuffer buffer = new StringBuffer("select u.id,l.operate,l.ip,u.username,u.realname,u.usertype,l.insertdate,ifnull(l.description,'') from log l left join (SELECT id , username, realname ,usertype from  `user` UNION ALL SELECT id, username,realname,usertype from userparent ) u on u.id = l.insertUser where 1=1 ");
        if (null != log.getOperate() && !log.getOperate().equals("")) {
            buffer.append(" and l.operate LIKE {operate}  ");
            args.put("operate", "%" + log.getOperate() + "%");
        }
        if (null != log.getUsername() && !log.getUsername().equals("")) {
            buffer.append(" and u.realname LIKE {realname} ");
            args.put("realname", "%" + log.getUsername() + "%'");
        }
        if (null != log.getStarttime() && !log.getStarttime().equals("")) {
            buffer.append(" and l.insertdate > {Starttime} ");
            args.put("Starttime", log.getStarttime());
        }
        if (null != log.getEndtime() && !log.getEndtime().equals("")) {
            buffer.append(" and l.insertdate < {Endtime} ");
            args.put("Endtime", log.getEndtime());
        }
        buffer.append(" order by l.insertdate desc  ");
        if (log.getPageStart() >= 0) {
            buffer.append(" limit {PageStart} , {PageSize} ");
            args.put("PageStart", Integer.valueOf(log.getPageStart()));
            args.put("PageSize", Integer.valueOf(log.getPageSize()));
        }
        return this.dao._queryBeanList(buffer.toString(), Log.class, args);
    }

    public int getAllLogsCount(Log log) {
        Map args = new HashMap();
        StringBuffer buffer = new StringBuffer("select count(1) from log l left join user u on u.id = l.insertUser where 1=1 ");
        if (null != log.getOperate() && !log.getOperate().equals("")) {
            buffer.append(" and l.operate LIKE {operate} ");
            args.put("operate", "%" + log.getOperate() + "%");
        }
        if (null != log.getUsername() && !log.getUsername().equals("")) {
            buffer.append(" and u.realname LIKE {realname} ");
            args.put("realname", "%" + log.getUsername() + "%");
        }
        if (null != log.getStarttime() && !log.getStarttime().equals("")) {
            buffer.append(" and l.insertdate > {Starttime} ");
            args.put("Starttime", log.getStarttime());
        }
        if (null != log.getEndtime() && !log.getEndtime().equals("")) {
            buffer.append(" and l.insertdate < {Endtime} ");
            args.put("Endtime", log.getEndtime());
        }
        return this.dao._queryInt(buffer.toString(), args).intValue();
    }

    public Map<Data, List<Data>> getExamSetList(String category) {
        Map<Data, List<Data>> map2 = new HashMap<>();
        Map args = new HashMap();
        String sql = "SELECT id,isDefault,type,name,value,isLock FROM `data`  WHERE 1=1";
        String sql2 = "SELECT id,isDefault,type,name,value,isLock  FROM `data` WHERE type='0' and value>=6 and value<=12  ORDER BY VALUE*1 ASC";
        if (category != null && !"".equals(category)) {
            sql = sql + "  and category={category} ";
            sql2 = sql2 + "  and category={category} ";
            args.put("category", category);
        }
        try {
            String sqlTemp = sql;
            List pNameValueList = this.dao._queryBeanList(sql2, Data.class, args);
            if (pNameValueList != null) {
                for (int i = 0; i < pNameValueList.size(); i++) {
                    args.put("type", ((Data) pNameValueList.get(i)).getValue());
                    List cNameValueList = this.dao._queryBeanList(sql + "  and  type={type} ", Data.class, args);
                    if (cNameValueList != null && cNameValueList.size() > 0) {
                        List<Data> cList = new ArrayList<>();
                        for (int j = 0; j < cNameValueList.size(); j++) {
                            cList.add((Data) cNameValueList.get(j));
                        }
                        map2.put((Data) pNameValueList.get(i), cList);
                    }
                    sql = sqlTemp;
                    args.remove("type");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.log.info("数据查询异常--");
        }
        return map2;
    }

    public void updateDataTable(int id, String isDefault, String isLock) {
        Map args = new HashMap();
        args.put("id", Integer.valueOf(id));
        args.put("isDefault", isDefault);
        this.dao._execute("UPDATE `data` SET isDefault='F' WHERE id IN(SELECT a.id  FROM(\tSELECT * \tFROM `data` \tWHERE type=(SELECT type FROM `data` WHERE id={id} )\tAND id!={id} ) a )", args);
        this.dao._execute("UPDATE `data` SET isDefault={isDefault} WHERE id={id} ", args);
    }

    public void updateImg_Set(int id, String isDefault) {
        Map args = new HashMap();
        args.put("isDefault", isDefault);
        args.put("id", Integer.valueOf(id));
        this.dao._execute("UPDATE `data` SET isDefault={isDefault}  WHERE id={id} ", args);
    }

    public Map<Data, List<Data>> getExamSet8KList(String category) {
        Map<Data, List<Data>> map2 = new HashMap<>();
        Map args = new HashMap();
        String sql = "SELECT id,isDefault,type,name,value,isLock FROM `data`  WHERE 1=1";
        String sql2 = "SELECT id,isDefault,type,name,value,isLock  FROM `data` WHERE type='92'";
        if (category != null && !"".equals(category)) {
            sql = sql + "  and category={category} ";
            sql2 = sql2 + "  and category={category} ";
            args.put("category", category);
        }
        try {
            String sqlTemp = sql;
            List pNameValueList = this.dao._queryBeanList(sql2, Data.class, args);
            if (pNameValueList != null) {
                for (int i = 0; i < pNameValueList.size(); i++) {
                    String type = ((Data) pNameValueList.get(i)).getValue();
                    String type2 = type + i;
                    args.put(type2, type2);
                    List cNameValueList = this.dao._queryBeanList(sql + "  and  type={" + type2 + "} ", Data.class, args);
                    if (cNameValueList != null && cNameValueList.size() > 0) {
                        List<Data> cList = new ArrayList<>();
                        for (int j = 0; j < cNameValueList.size(); j++) {
                            cList.add((Data) cNameValueList.get(j));
                        }
                        map2.put((Data) pNameValueList.get(i), cList);
                    }
                    sql = sqlTemp;
                }
            }
        } catch (Exception e) {
            this.log.info("数据查询异常--", e);
        }
        return map2;
    }

    public Integer setDBPath(String filePath) {
        Properties pro = new Properties();
        String str = getClass().getResource("").getPath().toString().replace("%20", " ");
        String path = str.substring(1, str.indexOf("WEB-INF")) + "WEB-INF/" + Const.DB_backup_path;
        try {
            InputStream in = new FileInputStream(path);
            pro.load(in);
            pro.setProperty("backupPath", filePath);
            OutputStream os = new FileOutputStream(path);
            pro.store(os, "");
            os.close();
            in.close();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String[] getImgIsDeleteMark() {
        Map args = new HashMap();
        args.put("type", "12");
        String[] s = new String[4];
        try {
            List lis = this.dao._queryBeanList("SELECT isDefault,type,name,`value`   FROM `data`   WHERE type={type}  order by value*1 asc", Data.class, args);
            for (int i = 0; i < lis.size(); i++) {
                int v = Integer.parseInt(((Data) lis.get(i)).getValue()) % 10;
                s[v - 1] = ((Data) lis.get(i)).getIsDefault();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i2 = 0; i2 < s.length; i2++) {
            if (s[i2].equals("")) {
                if (i2 == 0) {
                    s[i2] = "F";
                } else if (i2 == 1) {
                    s[i2] = "F";
                } else if (i2 == 2) {
                    s[i2] = "T";
                } else if (i2 == 3) {
                    s[i2] = "T";
                }
            }
        }
        return s;
    }

    public void deleteTableImg(String examNum, String paperImg_checked, String questionImg_checked, String questionScoreImg_checked, String examineNumImg_checked) {
        List<String> sqls = new ArrayList<>();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        new ArrayList();
        List list = this.dao._queryBeanList("SELECT DISTINCT examPaperNum FROM exampaper WHERE examNum={examNum} ", Exampaper.class, args);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                args.put("exampaperNum", ((Exampaper) list.get(i)).getExamPaperNum());
                if (paperImg_checked != null && "T".equals(paperImg_checked)) {
                    sqls.add("DELETE stuimg FROM studentpaperimage stuimg  LEFT JOIN (SELECT id FROM regexaminee WHERE exampaperNum={exampaperNum} )reg ON stuimg.regId=reg.id WHERE reg.id IS NOT NULL");
                    this.dao._execute("DELETE stuimg FROM studentpaperimage stuimg  LEFT JOIN (SELECT id FROM regexaminee WHERE exampaperNum={exampaperNum} )reg ON stuimg.regId=reg.id WHERE reg.id IS NOT NULL", args);
                    this.log.info((i + 1) + "---sql---DELETE stuimg FROM studentpaperimage stuimg  LEFT JOIN (SELECT id FROM regexaminee WHERE exampaperNum={exampaperNum} )reg ON stuimg.regId=reg.id WHERE reg.id IS NOT NULL");
                }
                if (questionImg_checked != null && "T".equals(questionImg_checked)) {
                    sqls.add("DELETE qimg FROM questionimage qimg  LEFT JOIN (SELECT id FROM score WHERE exampaperNum={exampaperNum} )sc ON qimg.scoreId=sc.id WHERE sc.id IS NOT NULL");
                    this.dao._execute("DELETE qimg FROM questionimage qimg  LEFT JOIN (SELECT id FROM score WHERE exampaperNum={exampaperNum} )sc ON qimg.scoreId=sc.id WHERE sc.id IS NOT NULL", args);
                    this.log.info((i + 1) + "---sql---DELETE qimg FROM questionimage qimg  LEFT JOIN (SELECT id FROM score WHERE exampaperNum={exampaperNum} )sc ON qimg.scoreId=sc.id WHERE sc.id IS NOT NULL");
                }
                if (questionScoreImg_checked != null && "T".equals(questionScoreImg_checked)) {
                    sqls.add("DELETE simg FROM scoreimage simg  LEFT JOIN (SELECT id FROM score WHERE exampaperNum={exampaperNum} )sc ON simg.scoreId=sc.id WHERE sc.id IS NOT NULL");
                    this.dao._execute("DELETE simg FROM scoreimage simg  LEFT JOIN (SELECT id FROM score WHERE exampaperNum={exampaperNum} )sc ON simg.scoreId=sc.id WHERE sc.id IS NOT NULL", args);
                    this.log.info((i + 1) + "---sql---DELETE simg FROM scoreimage simg  LEFT JOIN (SELECT id FROM score WHERE exampaperNum={exampaperNum} )sc ON simg.scoreId=sc.id WHERE sc.id IS NOT NULL");
                }
                if (examineNumImg_checked != null && "T".equals(examineNumImg_checked)) {
                    sqls.add("DELETE eximg FROM examinationnumimg eximg  LEFT JOIN (SELECT id FROM regexaminee WHERE exampaperNum={exampaperNum} )reg ON eximg.regId=reg.id WHERE reg.id IS NOT NULL");
                    this.dao._execute("DELETE eximg FROM examinationnumimg eximg  LEFT JOIN (SELECT id FROM regexaminee WHERE exampaperNum={exampaperNum} )reg ON eximg.regId=reg.id WHERE reg.id IS NOT NULL", args);
                    this.log.info((i + 1) + "---sql---DELETE eximg FROM examinationnumimg eximg  LEFT JOIN (SELECT id FROM regexaminee WHERE exampaperNum={exampaperNum} )reg ON eximg.regId=reg.id WHERE reg.id IS NOT NULL");
                }
                args.remove("exampaperNum");
            }
        }
    }

    public int getAllquestionMangerCount(QuestionManger questionManger, String subjectNum) {
        Map args = new HashMap();
        StringBuffer buf = new StringBuffer("  select   distinct  count(1) from   subject     a      left  join   questionType   b     on   a.subjectNum=b.subjectNum   left  join  (select distinct stage  from  basegrade) c      on b.stage=c.stage     left  join  data  d   on  c.stage=d.`value`   where d.type='5'  ");
        if (subjectNum != null && !subjectNum.equals("")) {
            buf.append(" and b.subjectNum={subjectNum} ");
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        }
        return this.dao._queryInt(buf.toString(), args).intValue();
    }

    public String filepath(AwardPoint awardPoint) {
        Map args = new HashMap();
        args.put("operate", "1");
        return this.dao._queryStr("select  IF(COUNT(1)=0,1, para )   from   config  where  operate={operate} and type='0' ", args);
    }

    public String deletetimeList() {
        Map args = new HashMap();
        args.put("operate", "3");
        return this.dao._queryStr("select   IF(COUNT(1)=0,0, para )  from   config  where  operate={operate}  and type='0' ", args);
    }

    public String timesplitList() {
        Map args = new HashMap();
        args.put("operate", "2");
        return this.dao._queryStr(" select  IF(COUNT(1)=0,0, para ) from  config   where  operate={operate} and type='0' ", args);
    }

    public String begintimesplitList() {
        Map args = new HashMap();
        args.put("operate", "2");
        return this.dao._queryStr(" select  IF(COUNT(1)=0,10, para ) from  config   where  operate={operate} and type='0' ", args);
    }

    public List subjectList(QuestionManger questionManger) {
        return this.dao.queryBeanList("SELECT  subjectNum,subjectName  FROM  `subject`", QuestionManger.class);
    }

    public void delquestion(String[] ids, String user, String date) {
        List<Map> argMapList = new ArrayList<>();
        for (String str : ids) {
            Map args = new HashMap();
            args.put("id", str);
            argMapList.add(args);
        }
        this.dao._batchUpdate("delete  from questionType  where  id={id} ", argMapList);
    }

    public void addQuestiontype(String subjectNum, String name, String user, String date, String stage) {
        int id = 0;
        int num = 0;
        new QuestionManger();
        List<?> queryBeanList = this.dao.queryBeanList("select  IFNULL(max(id ),0) as id ,IFNULL(max(num ),0) as num from questionType ", QuestionManger.class);
        for (int i = 0; i < queryBeanList.size(); i++) {
            id = Integer.parseInt(((QuestionManger) queryBeanList.get(i)).getId()) + 1;
            num = Integer.parseInt(((QuestionManger) queryBeanList.get(i)).getNum()) + 1;
        }
        Map args = new HashMap();
        args.put("id", Integer.valueOf(id));
        args.put("num", Integer.valueOf(num));
        args.put("name", name);
        args.put("stage", stage);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("user", user);
        args.put("date", date);
        this.dao._execute("insert  into   questionType(id,num,pnum,name,stage, subjectNum,level,insertuser,insertdate) values({id},{num},'-1',{name},{stage},{subjectNum} ,'0',{user} ,{date})", args);
    }

    public Integer exitsQuestiontype(String subjectNum, String name, String stage) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("name", name);
        args.put("stage", stage);
        return this.dao._queryInt(" select count(1) as count  from  questionType where  subjectNum={subjectNum}  and name={name}  and stage={stage}", args);
    }

    public List statgeList(QuestionManger questionManger) {
        return this.dao.queryBeanList("select  distinct  b.name,b.value from  basegrade  a  left  join  data  b   on  a.stage=b.`value`   and   b.type='5' ", QuestionManger.class);
    }

    public List questionupList(String id, QuestionManger questionManger) {
        Map args = new HashMap();
        args.put("id", id);
        return this.dao._queryBeanList("select  subjectNum,stage,name  from  questiontype where id={id}  ", QuestionManger.class, args);
    }

    public void updateQuestiontype(String subjectNum, String name, String user, String date, String stage, QuestionManger questionManger) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("name", name);
        args.put("user", user);
        args.put("date", date);
        args.put("stage", stage);
        args.put("id", questionManger.getId());
        this.dao._execute("update questionType  set  subjectNum={subjectNum} , name={name} ,updateUser={user} ,updateDate={date},stage={stage}  where id={id}  ", args);
    }

    public String examNumexecute(String exams, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put("exams", exams);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryStr("select  if(count(1)=0,1,b.examPaperNum )as examPaperNum   from  exampaper      b  where       b.examNum={exams}   and         b.subjectNum={subjectNum}   and         b.gradeNum={gradeNum}  ", args);
    }

    public List examNumexecute2(String exams, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        String subjectStr = "";
        if (subjectNum != null && !subjectNum.equals("") && !subjectNum.equals("-1")) {
            subjectStr = " and   b.subjectNum={subjectNum}  ";
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        } else if (subjectNum.equals("-1")) {
            subjectStr = " and isHidden='F' group by b.subjectNum";
        }
        String sql = "select  if(count(1)=0,1,b.examPaperNum )as examPaperNum   from  exampaper      b  where       b.examNum={exams}  and         b.gradeNum={gradeNum} " + subjectStr;
        args.put("exams", exams);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryColList(sql, args);
    }

    public List<AwardPoint> getExam() {
        return this.dao.queryBeanList("  SELECT exam.examNum num,exam.examName name   from  (select a.*  from exampaper  a    where    isDelete='F'  ) ep    left  JOIN   exam  on exam.examNum=ep.examNum and exam.isDelete='F'  where  exam.examNum  is  not  null  GROUP BY ep.examNum   ORDER BY exam.insertDate DESC", AwardPoint.class);
    }

    public List<AwardPoint> getSchool(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryBeanList(" select distinct sch.id num,sch.schoolName name from  examinationnum en  left join school sch on sch.id=en.schoolNum where en.examNum = {examNum} ", AwardPoint.class, args);
    }

    public List<AwardPoint> getGrade(String examNum, String schoolNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        return this.dao._queryBeanList(" select  distinct  a.num,a.name   from   (SELECT distinct e.gradeNum num ,g.gradeName name,e.examPaperNum from   exampaper e   LEFT JOIN grade g on e.gradeNum = g.gradeNum   WHERE e.examNum = {examNum} and g.schoolnum={schoolNum}  AND e.isHidden='F' )a  ORDER BY a.num ", AwardPoint.class, args);
    }

    public List<AwardPoint> getSubject(String examNum, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryBeanList(" select  distinct  a.name,a.num  from  ( SELECT DISTINCT s.subjectName name,e.subjectNum num,e.examPaperNum from exampaper e   LEFT JOIN subject s on e.subjectNum = s.subjectNum WHERE e.examNum ={examNum}  and e.gradeNum ={gradeNum}      AND e.isHidden='F' )a  ", AwardPoint.class, args);
    }

    public void deleteImg(String examPaperNum, String paperImg_checked, String questionImg_checked, String questionScoreImg_checked, String examineNumImg_checked, String exampapertypeimage_ckecked, String illegalimage_checked) {
        String studentpaperimage = "";
        String questionimage = "";
        String scoreimage = "";
        String examinationnumimg = "";
        String exampapertypeimage = "";
        String illegalimage = "";
        String flage = "";
        if (questionImg_checked != null && "T".equals(questionImg_checked)) {
            questionimage = "questionimage";
            flage = "0";
        }
        if (questionScoreImg_checked != null && "T".equals(questionScoreImg_checked)) {
            scoreimage = "scoreimage";
            flage = "0";
        }
        if (flage != null && flage.equals("0")) {
            deleteImage(scoreimage, questionimage, examinationnumimg, studentpaperimage, exampapertypeimage, illegalimage, flage, examPaperNum);
        }
        if (paperImg_checked != null && "T".equals(paperImg_checked)) {
            studentpaperimage = "studentpaperimage";
            flage = "1";
        }
        if (examineNumImg_checked != null && "T".equals(examineNumImg_checked)) {
            examinationnumimg = "examinationnumimg";
            flage = "1";
        }
        if (exampapertypeimage_ckecked != null && "T".equals(exampapertypeimage_ckecked)) {
            exampapertypeimage = "exampapertypeimage";
            flage = "1";
        }
        if (illegalimage_checked != null && "T".equals(illegalimage_checked)) {
            illegalimage = "illegalimage";
            flage = "1";
        }
        if (flage != null && flage.equals("1")) {
            deleteImage(scoreimage, questionimage, examinationnumimg, studentpaperimage, exampapertypeimage, illegalimage, flage, examPaperNum);
        }
    }

    public void deleteImage(String scoreimage, String questionimage, String examinationnumimg, String studentpaperimage, String exampapertypeimage, String illegalimage, String deleteflag, String examPaperNum) {
        try {
            Object[] params = {examPaperNum, scoreimage, questionimage, examinationnumimg, studentpaperimage, exampapertypeimage, illegalimage, deleteflag};
            this.dao._execute("CALL delete_image_data(?,?,?,?,?,?,?,?)", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String dataBasecpu(String dataName) {
        Map args = new HashMap();
        args.put("dataName", dataName);
        return this.dao._queryStr("select   CEILING((sum(DATA_LENGTH)+sum(INDEX_LENGTH))/(1024*1024*1024))  as zb  from information_schema.tables   where table_schema={dataName} ", args);
    }

    public List getuserInfo(String userid, String usertype, ServletContext context) {
        String sql;
        Map args = new HashMap();
        args.put("userid", userid);
        if (!usertype.equals("1")) {
            return this.dao._queryBeanList((((((("select DISTINCT u.realname realName,sch.shortname schoolName,g.gradeName,cla.className ,stu.studentName from  (select id ,schoolnum,userid ,realname,usertype from user ") + "\tUNION ALL SELECT id ,schoolnum,userid,realname,usertype from userparent)   u  ") + " LEFT JOIN school sch ON  sch.id = u.schoolnum ") + "  LEFT JOIN student stu ON stu.id = u.userid ") + "  LEFT JOIN grade g ON g.gradeNum = stu.gradeNum ") + "  LEFT JOIN class cla ON cla.id = stu.classNum") + "  WHERE u.id IS NOT NULL  AND u.id ={userid}  ORDER BY u.id*1  ", OnlineUser.class, args);
        }
        String level = Configuration.getInstance().getLevelclass();
        String sql2 = "SELECT CONCAT(IF (rr.type = 4,CONCAT('教研主任：',rr.gradeName,rr.description),''),IF (rr.type = 3,CONCAT('年级主任：', rr.gradeName),''),IF (rr.type = 2,CONCAT('班主任：',rr.gradeName,rr.className),''),\tIF (rr.type = 1,CONCAT(rr.description, ':',rr.gradeName,rr.className),'')) description ,rr.shortname,rr.gradeName,rr.type,rr.teacherName,rr.className FROM (select DISTINCT su.subjectName description,sc.shortname ,up.type,g.gradeName,te.teacherName,group_concat(DISTINCT cla.className) className from userposition  up  LEFT JOIN school sc ON sc.id = up.schoolnum  LEFT JOIN grade g ON g.gradeNum = up.gradeNum  LEFT JOIN user  u ON u.id = up.userNum  LEFT JOIN teacher te ON te.id = u.userid ";
        if (level.equals("T")) {
            sql = sql2 + " LEFT JOIN levelclass cla ON cla.id = up.classNum ";
        } else {
            sql = sql2 + " LEFT JOIN class cla ON cla.id = up.classNum ";
        }
        return this.dao._queryBeanList(sql + "  AND cla.isDelete = 'F' LEFT JOIN `subject` su ON su.subjectNum = up.subjectNum  where  u.id = {userid} )rr", Userposition.class, args);
    }
}
