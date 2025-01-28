package com.dmj.serviceimpl.systemManagement;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.questionGroup.QuestionGroupDaoImpl;
import com.dmj.daoimpl.userManagement.UserDAOImpl;
import com.dmj.domain.Class;
import com.dmj.domain.Grade;
import com.dmj.domain.School;
import com.dmj.domain.Subject;
import com.dmj.domain.Teacher;
import com.dmj.domain.User;
import com.dmj.domain.Userposition;
import com.dmj.domain.leq.TDataParameters;
import com.dmj.service.examManagement.UtilSystemService;
import com.dmj.service.questionGroup.QuestionGroupService;
import com.dmj.service.reportManagement.ReportExportService;
import com.dmj.service.systemManagement.TeacherManageService;
import com.dmj.service.teachingInformation.StudentService;
import com.dmj.serviceimpl.examManagement.UtilSystemServiceimpl;
import com.dmj.serviceimpl.questionGroup.QuestionGroupImpl;
import com.dmj.serviceimpl.reportManagement.ReportExportServiceimpl;
import com.dmj.serviceimpl.teachingInformation.StudentServiceimpl;
import com.dmj.util.CommonUtil;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.excel.CheckCellUtil;
import com.dmj.util.excel.ExcelHelper;
import com.dmj.util.msg.RspMsg;
import com.zht.db.RowArg;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.SubException;
import com.zht.db.TypeEnum;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* loaded from: TeacherManageServiceimpl.class */
public class TeacherManageServiceimpl implements TeacherManageService {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());
    QuestionGroupDaoImpl qdi = new QuestionGroupDaoImpl();
    UserDAOImpl userDao = new UserDAOImpl();
    private QuestionGroupService qgi = (QuestionGroupService) ServiceFactory.getObject(new QuestionGroupImpl());
    private UtilSystemService uss = (UtilSystemService) ServiceFactory.getObject(new UtilSystemServiceimpl());
    private ReportExportService res = (ReportExportService) ServiceFactory.getObject(new ReportExportServiceimpl());
    private StudentService stu = (StudentService) ServiceFactory.getObject(new StudentServiceimpl());
    DecimalFormat df = new DecimalFormat("0");
    private boolean errorFlag = false;
    private boolean rowBgColor = false;

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Teacher> listTeacher(String schoolNum, String searchNum, String searchName, String searchsex, String searchtitle, int pageStart, int pageSize, String subjectName, String result, String userId, String gradeNum, String leicengId, String positionName, String phone) {
        String sql2;
        Map args1 = StreamMap.create().put("userNum", (Object) userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args1);
        String schStr2 = "";
        if (!"-1".equals(schoolNum)) {
            schStr2 = " and t.schoolNum={schoolNum} ";
        }
        String graStr = "";
        if (!"-1".equals(gradeNum)) {
            graStr = " and u.gradeNum={gradeNum} ";
        }
        String subStr3 = "";
        if (null != subjectName && !"".equals(subjectName) && !"-1".equals(subjectName)) {
            subStr3 = " and u.subjectNum = {subjectName} ";
        }
        String poiStr = "";
        if (null != positionName && !"".equals(positionName) && !"-1".equals(positionName)) {
            poiStr = " and u.type = {positionName} ";
        }
        String tjSql = (((("" + ("".equals(searchNum) ? "" : " and t.teacherNum LIKE {searchNum} ")) + ("".equals(searchName) ? "" : " and t.teacherName LIKE {searchName}  ")) + ("".equals(searchsex) ? "" : " and t.sex LIKE {searchsex} ")) + ("".equals(searchtitle) ? "" : " and t.title LIKE {searchtitle} ")) + ("".equals(phone) ? "" : " and us.mobile LIKE {phone} ");
        String sql22 = "SELECT rrr.teacherNum teacherNum  ,GROUP_CONCAT(rrr.name) ext5 ,rrr.schoolNum,rrr.teacherName,rrr.id , rrr.title,rrr.sex,rrr.userNum ext4,rrr.subjectNum,rrr.schoolName  FROM ( SELECT  rr.teacherNum, CONCAT(case rr.type when 0 then CONCAT(rr.name) when 5 then CONCAT(rr.name) when 4 then CONCAT(rr.name,'：',rr.gradeName,rr.subjectName) when 3 then CONCAT(rr.name,'：',rr.gradeName) when 2 then CONCAT(rr.name,'：',rr.gradeName,rr.className) when 1 then CONCAT(rr.subjectName,':',rr.gradeName, if(rr.islevel='1',rr.levelclassName,rr.className) ) else CONCAT(rr.NAME,if(rr.gradeName is null and rr.subjectName is null,'',CONCAT('：',IFNULL(rr.gradeName,''),IFNULL(rr.subjectName,''),IFNULL(rr.className,'')))) end) as name,rr.schoolNum,rr.teacherName,rr.id,rr.title,rr.sex,rr.userNum,rr.subjectNum,rr.schoolName FROM ( SELECT  r.teacherNum,r.subjectName,r.gradeName,r.type,r.name,GROUP_CONCAT(r.className) className,r.schoolNum,r.teacherName,r.id,r.title,r.sex,r.userNum,r.subjectNum ,GROUP_CONCAT(r.levelclassName) levelclassName ,r.schoolName,r.islevel FROM (\tSELECT  e.teacherNum,e.gradeNum,e.subjectNum,e.classNum,e.type,e.name,e.gradeName,e.subjectName,e.className,e.schoolNum,e.teacherName,e.id,e.title,e.sex,e.userNum ,e.levelclassName , e.schoolName,e.islevel FROM\t(\tSELECT t.teacherNum,t.teacherName,u.gradeNum,u.subjectNum,u.classNum,u.type,d.name,t.id,t.title,t.sex, us.id userNum \t,g.gradeName,s.subjectName,c.className,t.schoolNum  ,cl.className levelclassName  , \tsch.schoolName,g.islevel\tFROM teacher t LEFT JOIN\tuser us ON t.id = us.userid AND  t.isDelete='F' \tLEFT JOIN  userposition u  ON us.id = u.userNum and u.schoolnum = t.schoolNum \tLEFT JOIN grade g ON u.gradeNum = g.gradeNum AND g.schoolNum = u.schoolNum AND g.isDelete='F' \tLEFT JOIN `subject` s ON s.subjectNum = u.subjectNum LEFT JOIN class c ON c.id = u.classNum AND c.isDelete='F'  LEFT JOIN levelclass cl ON cl.id = u.classNum AND cl.isDelete='F' \tLEFT JOIN school  sch ON sch.id  = t.schoolNum  LEFT JOIN data d on d.value = u.type and d.type = '31' ";
        if ("-1".equals(schoolNum)) {
            if (null != leicengId && !"".equals(leicengId)) {
                sql2 = (sql22 + " left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leicengId} group by sItemId) ss on ss.id = sch.id  where sch.isDelete='F' and ss.id is not null ") + " and us.userType = '1' " + poiStr + graStr + subStr3 + schStr2 + tjSql + "\t)e";
            } else if (!userId.equals("-1") && !userId.equals("-2") && null == map) {
                sql2 = (sql22 + " left join schoolscanpermission sa on sa.schoolNum = sch.id and sa.userNum={userId}   left join user te on te.schoolNum = sch.id  and te.id = {userId} and te.usertype=1   where sch.isDelete='F' and (sa.schoolNum is not null or te.schoolNum is not null)   ") + " and us.userType = '1' " + poiStr + graStr + subStr3 + schStr2 + tjSql + "\t)e";
            } else {
                sql2 = sql22 + " where us.userType = '1' " + poiStr + graStr + subStr3 + schStr2 + tjSql + "\t)e";
            }
        } else {
            sql2 = sql22 + " where us.userType = '1' " + poiStr + graStr + subStr3 + schStr2 + tjSql + "\t)e";
        }
        String sql23 = sql2 + "  ) r  GROUP BY  r.teacherNum ,if(r.type<6,0,1),r.type, r.subjectNum,r.gradeNum  )rr )rrr  GROUP BY rrr.teacherNum ORDER BY CONVERT(rrr.schoolName USING GBK),rrr.teacherNum*1 LIMIT {pageStart},{pageSize} ";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("subjectName", subjectName);
        args.put("positionName", positionName);
        args.put("searchNum", "%" + searchNum + "%");
        args.put("searchName", "%" + searchName + "%");
        args.put("searchsex", "%" + searchsex + "%");
        args.put("searchtitle", "%" + searchtitle + "%");
        args.put("phone", "%" + phone + "%");
        args.put("leicengId", leicengId);
        args.put("userId", userId);
        args.put("pageStart", Integer.valueOf(pageStart));
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao2._queryBeanList(sql23, Teacher.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public RspMsg addTeacher(String teacherNum, String teacherName, String sex, String title, String birthday, String worktime, String user, String date, String schoolNum, String mobile, String email) throws ParseException {
        String bb;
        String ww;
        String tSql;
        String uSql;
        String urSql;
        List<RowArg> sqls = new ArrayList<>();
        String tbirthday = null;
        if (birthday != null) {
            tbirthday = birthday;
        }
        if (birthday == null || birthday.equals("")) {
            bb = null;
        } else {
            bb = "{tbirthday}";
        }
        String tworktime = null;
        if (worktime != null) {
            tworktime = worktime;
        }
        if (worktime == null || worktime.equals("")) {
            ww = null;
        } else {
            ww = "{tworktime}";
        }
        if (!"".equals(mobile) && !checkMobile(teacherNum, mobile, null)) {
            return new RspMsg(401, "此联系电话 " + mobile + " 已被其他教师用户使用！", null);
        }
        if ((mobile.equals("") || null == mobile) && !teacherNum.equals("") && CommonUtil.isMobile(teacherNum)) {
            mobile = teacherNum;
        }
        try {
            Map args = new HashMap();
            args.put("teacherNum", teacherNum);
            args.put("teacherName", teacherName);
            args.put("sex", sex);
            args.put("title", title);
            args.put("tbirthday", tbirthday);
            args.put("tworktime", tworktime);
            args.put("user", user);
            args.put("date", date);
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
            args.put("mobile", mobile);
            args.put("email", email);
            Object t_res = this.dao2._queryObject("select id from teacher where teacherNum = {teacherNum} ", args);
            if (null == t_res) {
                String teacherid = GUID.getGUIDStr();
                tSql = "insert into teacher  (id,teacherNum,teacherName,sex,title,birthday,worktime,insertUser,insertDate,updateUser,updateDate,schoolNum,mobile,email,isDelete) VALUES ({teacherid},{teacherNum},{teacherName},{sex},{title}," + bb + Const.STRING_SEPERATOR + ww + ",{user},{date},{user},{date},{schoolNum},{mobile},{email} ,'F') ";
                args.put("teacherid", teacherid);
            } else {
                String teacherid2 = String.valueOf(t_res);
                args.put("teacherid", teacherid2);
                tSql = "update teacher set teacherNum={teacherNum} ,teacherName={teacherName},sex={sex} ,title={title},birthday={bb},worktime={ww} ,schoolNum={schoolNum} ,mobile={mobile} ,email={email},updateUser={user} ,updateDate={date} where id = {teacherid} ";
                args.put("teacherid", teacherid2);
            }
            sqls.add(new RowArg(tSql, args));
            args.put("password", Const.USER_PASSWORD);
            args.put("usertype", "1");
            Object u_res = this.dao2._queryObject("select id from user where username = {teacherNum}  and usertype = '1'", args);
            args.put("ur_res", u_res);
            if (null == u_res) {
                String userid = GUID.getGUIDStr();
                uSql = "insert into user (id,userid,isDelete,username,password,usertype,realname,insertUser,insertDate,schoolnum,mobile) values ({userid} ,{teacherid} ,'F',{teacherNum},{password},{usertype},{teacherName},{user},{date},{schoolNum},{mobile} )";
                args.put("userid", userid);
            } else {
                String userid2 = String.valueOf(u_res);
                uSql = "update user set userid={teacherid} ,username={teacherNum},password={password},usertype={usertype} ,realname={teacherName},schoolnum={schoolNum} ,mobile={mobile} ,isDelete='F',insertUser={user} ,insertDate={date}  where id = {userid} ";
                args.put("userid", userid2);
            }
            sqls.add(new RowArg(uSql, args));
            args.put("roletype", "1");
            Object ur_res = this.dao2._queryObject("select id from userrole where userNum = {userid} and roleNum = {roletype}  limit 1", args);
            args.put("ur_res", ur_res);
            if (null == ur_res) {
                urSql = "INSERT INTO userrole (userNum,roleNum,insertUser,insertDate,isDelete) VALUES ({userid},{roletype},{user},{date},'F') ";
            } else {
                urSql = "update userrole set userNum={userid},roleNum={roletype},insertUser={user},insertDate={date} ,isDelete='F' where id = {ur_res} ";
            }
            sqls.add(new RowArg(urSql, args));
            this.dao2._batchExecute(sqls);
            sqls.clear();
            return new RspMsg(200, "恭喜，操作成功!", null);
        } catch (Exception e) {
            return new RspMsg(Const.height_500, e.getMessage(), null);
        }
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public RspMsg register(String teacherNum, String teacherName, String password, String sex, String title, String birthday, String worktime, String user, String date, String schoolNum, String mobile, String email) throws ParseException {
        String bb;
        String ww;
        String tSql;
        String uSql;
        String urSql;
        List<RowArg> sqls = new ArrayList<>();
        String tbirthday = null;
        if (birthday != null) {
            tbirthday = birthday;
        }
        if (birthday == null || birthday.equals("")) {
            bb = null;
        } else {
            bb = "{tbirthday}";
        }
        String tworktime = null;
        if (worktime != null) {
            tworktime = worktime;
        }
        if (worktime == null || worktime.equals("")) {
            ww = null;
        } else {
            ww = "{tworktime}";
        }
        if (!"".equals(mobile) && !checkMobile(teacherNum, mobile, null)) {
            return new RspMsg(401, "此联系电话 " + mobile + " 已被其他教师用户使用！", null);
        }
        try {
            Map args = new HashMap();
            args.put("teacherNum", teacherNum);
            args.put("teacherName", teacherName);
            args.put("sex", sex);
            args.put("title", title);
            args.put("tbirthday", tbirthday);
            args.put("tworktime", tworktime);
            args.put("user", user);
            args.put("date", date);
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
            args.put("mobile", mobile);
            args.put("email", email);
            Object t_res = this.dao2._queryObject("select id from teacher where teacherNum = {teacherNum} ", args);
            if (null == t_res) {
                String teacherid = GUID.getGUIDStr();
                tSql = "insert into teacher  (id,teacherNum,teacherName,sex,title,birthday,worktime,insertUser,insertDate,updateUser,updateDate,schoolNum,mobile,email,isDelete) VALUES ({teacherid},{teacherNum},{teacherName},{sex},{title}," + bb + Const.STRING_SEPERATOR + ww + ",{user},{date},{user},{date},{schoolNum},{mobile},{email} ,'F') ";
                args.put("teacherid", teacherid);
            } else {
                String teacherid2 = String.valueOf(t_res);
                args.put("teacherid", teacherid2);
                tSql = "update teacher set teacherNum={teacherNum} ,teacherName={teacherName},sex={sex} ,title={title},birthday={bb},worktime={ww} ,schoolNum={schoolNum} ,mobile={mobile} ,email={email},updateUser={user} ,updateDate={date} where id = {teacherid} ";
                args.put("teacherid", teacherid2);
            }
            sqls.add(new RowArg(tSql, args));
            args.put("password", password);
            args.put("usertype", "1");
            Object u_res = this.dao2._queryObject("select id from user where username = {teacherNum}  and usertype = '1'", args);
            args.put("ur_res", u_res);
            if (null == u_res) {
                String userid = GUID.getGUIDStr();
                uSql = "insert into user (id,userid,isDelete,username,password,usertype,realname,insertUser,insertDate,schoolnum,mobile) values ({userid} ,{teacherid} ,'F',{teacherNum},md5({password}),{usertype},{teacherName},{user},{date},{schoolNum},{mobile} )";
                args.put("userid", userid);
            } else {
                String userid2 = String.valueOf(u_res);
                uSql = "update user set userid={teacherid} ,username={teacherNum},password=md5({password}),usertype={usertype} ,realname={teacherName},schoolnum={schoolNum} ,mobile={mobile} ,isDelete='F',insertUser={user} ,insertDate={date}  where id = {userid} ";
                args.put("userid", userid2);
            }
            sqls.add(new RowArg(uSql, args));
            args.put("roletype", "1");
            Object ur_res = this.dao2._queryObject("select id from userrole where userNum = {userid} and roleNum = {roletype}  limit 1", args);
            args.put("ur_res", ur_res);
            if (null == ur_res) {
                urSql = "INSERT INTO userrole (userNum,roleNum,insertUser,insertDate,isDelete) VALUES ({userid},{roletype},{user},{date},'F') ";
            } else {
                urSql = "update userrole set userNum={userid},roleNum={roletype},insertUser={user},insertDate={date} ,isDelete='F' where id = {ur_res} ";
            }
            sqls.add(new RowArg(urSql, args));
            this.dao2._batchExecute(sqls);
            sqls.clear();
            return new RspMsg(200, "注册成功", null);
        } catch (Exception e) {
            return new RspMsg(Const.height_500, e.getMessage(), null);
        }
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Map<String, String> getSchoolMap() {
        return this.dao2.queryOrderMap("select id,schoolName from school where isDelete='F'", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Map<String, String> getLimitSchoolMap(String userId) {
        String sql;
        if (userId.equals("-1") || userId.equals("-2")) {
            sql = "select id,schoolName from school where isDelete='F' order by convert(schoolName using gbk)";
        } else {
            sql = " select c.id,c.schoolName from school c   left join schauthormanage s on s.schoolNum = c.id and s.userId={userId}  left join user t on t.schoolNum = c.id  and t.id = {userId}  and t.usertype=1  where c.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null) order by convert(c.schoolName using gbk)";
        }
        Map args = new HashMap();
        args.put("userId", userId);
        return this.dao2._queryOrderMap(sql, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Map<String, String> getLimitSchoolMap2(String userId) {
        String sql;
        Map args = new HashMap();
        args.put("userId", userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if (userId.equals("-1") || userId.equals("-2") || map != null) {
            sql = "select id,schoolName from school where isDelete='F' order by convert(schoolName using gbk)";
        } else {
            sql = " select c.id,c.schoolName from school c   left join schoolscanpermission s on s.schoolNum = c.id and s.userNum={userId}  left join user t on t.schoolNum = c.id  and t.id = {userId}  and t.usertype=1  where c.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null) order by convert(c.schoolName using gbk)";
        }
        return this.dao2._queryOrderMap(sql, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Map<String, String> getUserSchoolMap(String uid, String userType, String sousuo) {
        String sql = " select c.id,c.schoolName from school c   left join schauthormanage s on s.schoolNum = c.id and s.userId={uid}   left join user t on t.schoolNum = c.id  and t.id = {uid} and t.usertype=1  where c.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null) and c.schoolName like {sousuo} ";
        if (uid.equals("-2") || uid.equals("-1") || userType.equals("0")) {
            sql = "select id,schoolName from school where isDelete='F' and schoolName like {sousuo}";
        }
        Map args = new HashMap();
        args.put("uid", uid);
        args.put("sousuo", "%" + sousuo + "%");
        return this.dao2._queryOrderMap(sql, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Map<String, String> getUserSchoolMap2(String uid, String userType, String sousuo) {
        String sql;
        Map args = new HashMap();
        args.put("uid", uid);
        args.put("sousuo", "%" + sousuo + "%");
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={uid} and type=1 limit 1", args);
        if (uid.equals("-2") || uid.equals("-1") || userType.equals("0") || null != map) {
            sql = "select id,schoolName from school where isDelete='F' and schoolName like {sousuo}";
        } else {
            sql = " select c.id,c.schoolName from school c   left join schoolscanpermission s on s.schoolNum = c.id and s.userNum={uid}   left join user t on t.schoolNum = c.id  and t.id = {uid} and t.usertype=1  where c.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null) and c.schoolName like {sousuo} ";
        }
        return this.dao2._queryOrderMap(sql, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteT(String id, String school, String uid) {
        this.qdi.deletetask(null, null, uid, "1", "");
        this.qdi.deleteGroupfz_user(null, null, "user", uid);
        Map args = new HashMap();
        args.put("id", id);
        args.put("uid", uid);
        this.dao2._execute("DELETE ur  FROM userrole ur  WHERE ur.userNum={uid} ", args);
        this.dao2._execute("DELETE urs  FROM userrole_sub urs  WHERE urs.userNum={uid} ", args);
        this.dao2._execute("DELETE us  FROM userposition us WHERE us.userNum={uid} ", args);
        this.dao2._execute("DELETE FROM `user`  WHERE id={uid}  ", args);
        this.dao2._execute("DELETE FROM teacher WHERE id={id} ", args);
        this.dao2._execute("DELETE us  FROM schoolscanpermission us WHERE us.userNum={uid} ", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List CheckIsTeacherQu(String uid) {
        Map args = new HashMap();
        args.put("uid", uid);
        return this.dao2._queryArrayList("select DISTINCT q.groupNum, e.examNum,e.examName,ba.gradeNum,ba.gradeName,su.subjectNum,su.subjectName,sd.questionNum,q.exampaperNum,q.userNum,ur.realname from exampaper ex  left join   exam e on e.examNum = ex.examNum  left join   questiongroup_user q on q.exampaperNum=ex.examPaperNum  left join  user ur on  ur.id = q.userNum  left join  (  select id,questionNum,examPaperNum from define d  union  select id,questionNum,examPaperNum from subdefine s  ) sd on sd.id = q.groupNum and ex.examPaperNum = sd.examPaperNum  left join  `subject` su on su.subjectNum = ex.subjectNum  left join  basegrade ba on ba.gradeNum = ex.gradeNum  where ur.id = {uid}  and e.`status` != '9' and q.groupNum is NOT NULL  ORDER BY e.examNum,ex.examPaperNum,ba.gradeNum ", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Teacher getAllByid(String id, String userb) {
        Map args = new HashMap();
        args.put("id", id);
        if ("user".equals(userb)) {
            return (Teacher) this.dao2._queryBean("select * from teacher where id ={id} ", Teacher.class, args);
        }
        return (Teacher) this.dao2._queryBean("select * from teacher where id ={id} ", Teacher.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public RspMsg editTeacher(String id, String teacherName, String title, String mobile, String email, String sex, String teacherNum, String schoolNum) {
        if (!"".equals(mobile) && !checkMobile(null, mobile, id)) {
            return new RspMsg(401, "此联系电话 " + mobile + " 已被其他教师用户使用！", null);
        }
        try {
            Map args = new HashMap();
            args.put("teacherNum", teacherNum);
            args.put("teacherName", teacherName);
            args.put("title", title);
            args.put("mobile", mobile);
            args.put("email", email);
            args.put("sex", sex);
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
            args.put("id", id);
            this.dao2._execute("UPDATE `user` SET username={teacherNum} ,realname={teacherName} ,mobile={mobile} ,email={email} ,schoolNum={schoolNum} WHERE usertype='1' and userid={id} ", args);
            this.dao2._execute("UPDATE teacher SET teacherNum={teacherNum},teacherName={teacherName} ,title={title} ,mobile={mobile} ,email={email} ,sex={sex} ,schoolNum={schoolNum} WHERE id={id} ", args);
            return new RspMsg(200, "恭喜，操作成功!", null);
        } catch (Exception e) {
            return new RspMsg(Const.height_500, e.getMessage(), null);
        }
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void subUserSchAuth(String tid, String teacherNum, String schoolNums, String user, String operatetype, String someTeacher) {
        String[] schoolValue = schoolNums.split(Const.STRING_SEPERATOR);
        Map args = new HashMap();
        args.put("user", user);
        args.put("someTeacher", someTeacher);
        args.put("tid", tid);
        if (operatetype.equals("allgroup")) {
            this.dao2._execute("delete from schauthormanage where type=1 ", args);
            List<?> _queryBeanList = this.dao2._queryBeanList(" select  t.id,t.teacherNum  from user u  left join teacher t on t.id = u.userid  where u.usertype=1 ", Teacher.class, args);
            for (int i = 0; i < _queryBeanList.size(); i++) {
                String teacherId = ((Teacher) _queryBeanList.get(i)).getId().toString();
                args.put("teacherId", teacherId);
                for (int j = 0; j < schoolValue.length; j++) {
                    if (schoolValue.length >= 1 && !"".equals(schoolValue[j])) {
                        args.put("schoolValuej", schoolValue[j]);
                        this.dao2._execute("insert into schauthormanage (userId,teacherId,teacherNum,teacherName,type,insertUser,insertDate,updateUser,updateDate,schoolNum,isDelete)  select  u.id,t.id,t.teacherNum,t.teacherName,u.usertype,{user} ,NOW(),{user} ,NOW(),{schoolValuej}  ,'F'  from user u  left join teacher t on t.id = u.userid  where u.usertype='1' and u.userid = {teacherId} ", args);
                    }
                }
            }
        }
        if (operatetype.equals("someGroup")) {
            this.dao2._execute("delete from schauthormanage where type=1 and teacherId in ({someTeacher[]}) ", args);
            List<?> _queryBeanList2 = this.dao2._queryBeanList(" select  t.id,t.teacherNum  from user u  left join teacher t on t.id = u.userid  where u.usertype=1 and t.id in ({someTeacher[]}) ", Teacher.class, args);
            for (int i2 = 0; i2 < _queryBeanList2.size(); i2++) {
                String teacherId2 = ((Teacher) _queryBeanList2.get(i2)).getId().toString();
                args.put("teacherId", teacherId2);
                for (int j2 = 0; j2 < schoolValue.length; j2++) {
                    if (schoolValue.length >= 1 && !"".equals(schoolValue[j2])) {
                        args.put("schoolValuej", schoolValue[j2]);
                        this.dao2._execute("insert into schauthormanage (userId,teacherId,teacherNum,teacherName,type,insertUser,insertDate,updateUser,updateDate,schoolNum,isDelete)  select  u.id,t.id,t.teacherNum,t.teacherName,u.usertype,{user} ,NOW(),{user},NOW(),{schoolValuej} ,'F'  from user u  left join teacher t on t.id = u.userid  where u.usertype='1' and u.userid = {teacherId} ", args);
                    }
                }
            }
        }
        if (operatetype.equals("add") || operatetype.equals("addall")) {
            for (String str : schoolValue) {
                args.put("schoolValuei", str);
                int count = this.dao2._queryInt("select count(1) from schauthormanage where teacherId = {tid}  and  schoolNum = {schoolValuei}  ", args).intValue();
                if (count == 0) {
                    this.dao2._execute("insert into schauthormanage (userId,teacherId,teacherNum,teacherName,type,insertUser,insertDate,updateUser,updateDate,schoolNum,isDelete)  select  u.id,t.id,t.teacherNum,t.teacherName,u.usertype,{user} ,NOW(),{user} ,NOW(),{schoolValuei} ,'F'  from user u  left join teacher t on t.id = u.userid  where u.usertype='1' and u.userid = {tid}  ", args);
                }
            }
        }
        if (operatetype.equals("clear") || operatetype.equals("clearall")) {
            for (String str2 : schoolValue) {
                args.put("schoolValuei", str2);
                this.dao2._execute("delete from schauthormanage where teacherId = {tid}  and  schoolNum = {schoolValuei}  ", args);
            }
        }
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void subUserSchAuthScan(String tid, String teacherNum, String schoolNums, String user, String operatetype, String someTeacher) {
        String[] schoolValue = schoolNums.split(Const.STRING_SEPERATOR);
        Map args = new HashMap();
        args.put("user", user);
        args.put("someTeacher", someTeacher);
        args.put("tid", tid);
        String userNum = this.dao2._queryStr("select id from user where userid = {tid}", args);
        args.put("userNum", userNum);
        if (operatetype.equals("add") || operatetype.equals("addall")) {
            for (String str : schoolValue) {
                args.put("schoolValuei", str);
                int count = this.dao2._queryInt("select count(1) from schoolscanpermission where userNum={userNum} and schoolNum = {schoolValuei}  ", args).intValue();
                if (count == 0) {
                    this.dao2._execute("INSERT INTO schoolscanpermission (`userNum`, `type`, `schoolNum`, `insertUser`, `insertDate`) VALUES ({userNum},'2',{schoolValuei},{user},NOW())", args);
                }
            }
        }
        if (operatetype.equals("clear") || operatetype.equals("clearall")) {
            for (String str2 : schoolValue) {
                args.put("schoolValuei", str2);
                this.dao2._execute("delete from schoolscanpermission where userNum={userNum} and  schoolNum = {schoolValuei}  ", args);
            }
        }
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<School> getUserManageSch(String tid) {
        Map args = new HashMap();
        args.put("tid", tid);
        return this.dao2._queryBeanList("select s.schoolNum,c.schoolName from schauthormanage s inner join school c on c.id=s.schoolNum where teacherId={tid}  ", School.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<School> getUserManageSchScan(String tid) {
        Map args = new HashMap();
        args.put("tid", tid);
        return this.dao2._queryBeanList("select s.schoolNum,c.schoolName from schoolscanpermission s left join user u on s.userNum=u.id inner join school c on c.id=s.schoolNum where u.userid={tid}  ", School.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Integer count(String teacherNum, String school) {
        Map args = new HashMap();
        args.put("teacherNum", teacherNum);
        return this.dao2._queryInt("select count(1) from user where (userName={teacherNum}  or loginname={teacherNum}) and usertype='1' and isDelete='F'", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void bacthdelteacher(String user, String date, String school, String[] ids, String[] uids) throws Exception {
        List<RowArg> rowArgList = new ArrayList<>();
        for (int i = 0; i < uids.length; i++) {
            Map args = new HashMap();
            args.put("idsi", ids[i]);
            Object Tnum = this.dao2._queryObject("select t.teacherNum from changeSchool_teacher cst left join teacher t on t.teacherNum = cst.teacherNum where t.id = {idsi}  and cst.status='0' and t.schoolNum <> cst.schoolNum", args);
            if (null == Tnum) {
                String uid = uids[i];
                args.put("uid", uid);
                rowArgList.add(new RowArg("DELETE FROM teacher WHERE id={idsi}", args));
                rowArgList.add(new RowArg("DELETE FROM userrole WHERE userNum={uid} ", args));
                rowArgList.add(new RowArg("DELETE FROM userrole_sub WHERE userNum={uid} ", args));
                rowArgList.add(new RowArg("DELETE FROM userposition WHERE userNum={uid} ", args));
                rowArgList.add(new RowArg("DELETE FROM `user` WHERE id={uid} ", args));
                rowArgList.add(new RowArg("DELETE FROM `schoolscanpermission` WHERE userNum={uid} ", args));
                this.qdi.deleteGroupfz_user(null, null, "user", uid);
            } else {
                this.stu.passChangeSchoolApply(Tnum.toString(), "2", null, user);
            }
        }
        this.dao2._batchExecute(rowArgList);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Grade> getgradeByschool(String school) {
        Map args = new HashMap();
        args.put(License.SCHOOL, school);
        return this.dao2._queryBeanList("select gradeNum,gradeName from grade where schoolNum={school}  AND isDelete='F' order by gradeNum*1", Grade.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Class> getclassBygrade(int grade, String school) {
        Map args = new HashMap();
        args.put(License.SCHOOL, school);
        args.put("grade", Integer.valueOf(grade));
        return this.dao2._queryBeanList("select cl.className,cl.id classNum,cl.gradeNum,cl.jie from class cl LEFT JOIN grade gr ON cl.schoolNum=gr.schoolNum and  cl.gradeNum = gr.gradeNum and cl.jie=gr.jie   WHERE   cl.schoolNum={school}  and cl.gradeNum={grade}   AND cl.isDelete='F' AND gr.isDelete='F' order by cl.classNum*1", Class.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Subject> getsubjectlist() {
        return this.dao2.queryBeanList("select subjectNum,subjectName from subject where isHidden='F' order by subjectNum*1", Subject.class);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void suposition(String userNum, String schoolNum, String[] gradeNum, String[] classNum, String subjectNum, String date, String user) {
        if (!subjectNum.equals("null")) {
            for (int i = 0; i < classNum.length; i++) {
                String className = getCName(classNum[i], schoolNum, gradeNum[i], null);
                String subjectName = getSubName(subjectNum);
                String stage = getStage(gradeNum[i]);
                int jie = getjie(schoolNum, gradeNum[i]);
                String sql = "insert into userposition (userNum,type,insertDate,description,schoolNum,insertUser,gradeNum,subjectNum,classNum,jie,stage) VALUES ({userNum},'1',{date},{className} " + subjectName + "老师',{schoolNum},{user},{gradeNumi} ,{subjectNum} ,{classNumi},{jie},{stage} )";
                Map args = new HashMap();
                args.put("userNum", userNum);
                args.put("date", date);
                args.put("className", className);
                args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                args.put("user", user);
                args.put("gradeNumi", gradeNum[i]);
                args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                args.put("classNumi", classNum[i]);
                args.put("jie", Integer.valueOf(jie));
                args.put("stage", stage);
                this.dao2._execute(sql, args);
            }
        }
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void supositionnew(String userNum, String schoolNum, String gradeNum, String classNum, String subjectNum, String date, String user, String className, String subjectName) {
        String stage = getStage(gradeNum);
        int jie = getjie(schoolNum, gradeNum);
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("date", date);
        args.put("className", className + subjectName + "老师");
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("user", user);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("subjectName", subjectNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("jie", Integer.valueOf(jie));
        args.put("stage", stage);
        this.dao2._execute("insert into userposition (userNum,type,insertDate,description,schoolNum,insertUser,gradeNum,subjectNum,classNum,jie,stage) VALUES ({userNum},'1',{date},{className} ,{schoolNum},{user},{gradeNum},{subjectNum},{classNum},{jie},{stage} )", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void tSubmit(String userNum, String type, String date, String school, String user, String[] gradeNums, String[] classNums) {
        for (int i = 0; i < classNums.length; i++) {
            String className = getCName(classNums[i], school, gradeNums[i], null);
            String stage = getStage(gradeNums[i]);
            Map args = new HashMap();
            args.put("userNum", userNum);
            args.put("type", type);
            args.put("date", date);
            args.put("className", className + "班主任");
            args.put(License.SCHOOL, school);
            args.put("user", user);
            args.put("gradeNums", gradeNums);
            args.put("classNums", classNums[i]);
            args.put("stage", stage);
            this.dao2._execute("insert into userposition(userNum,type,insertDate,description,schoolNum,insertUser,gradeNum,classNum,stage,subjectNum) VALUES({userNum} ,{type},{date},{className},{school} ,{user} ,{gradeNums},{classNums} ,{stage} ,'999')", args);
        }
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void tSubmitnew(String userNum, String type, String date, String school, String user, String gradeNums, String classNums, String className) {
        int jie = getjie(school, gradeNums);
        String stage = getStage(gradeNums);
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("type", type);
        args.put("date", date);
        args.put("className", className + "班主任");
        args.put(License.SCHOOL, school);
        args.put("user", user);
        args.put("gradeNums", gradeNums);
        args.put("classNums", classNums);
        args.put("jie", Integer.valueOf(jie));
        args.put("stage", stage);
        this.dao2._execute("insert into userposition(userNum,type,insertDate,description,schoolNum,insertUser,gradeNum,classNum,jie,stage,subjectNum) VALUES({userNum} ,{type},{date},{className},{school} ,{user} ,{gradeNums},{classNums} ,{jie},{stage} ,'999')", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Map<String, String> getGrBySch(String school) {
        Map args = new HashMap();
        args.put(License.SCHOOL, school);
        return this.dao2._queryOrderMap("select gradeNum,gradeName from grade where schoolNum={school}  AND isDelete='F' order by gradeNum*1", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void gSubmit(String userNum, String type, String date, String school, String user, String gradeNum, String description) {
        int jie = getjie(school, gradeNum);
        String stage = getStage(gradeNum);
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("type", type);
        args.put("date", date);
        args.put("description", description);
        args.put(License.SCHOOL, school);
        args.put("user", user);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("jie", Integer.valueOf(jie));
        args.put("stage", stage);
        this.dao2._execute("insert into userposition(userNum,type,insertDate,description,schoolNum,insertUser,gradeNum,jie,stage,subjectNum) VALUES({userNum} ,{type},{date},{description},{school},{user},{gradeNum},{jie},{stage} ,'999')", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Map<String, String> getsubjectMap() {
        return this.dao2.queryOrderMap("select subjectNum,subjectName from subject where isHidden='F' order by subjectNum*1", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void studySubmit(String userNum, String type, String date, String school, String user, String gradeNum, String subjectNum, String description) {
        int jie = getjie(school, gradeNum);
        String stage = getStage(gradeNum);
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("type", type);
        args.put("date", date);
        args.put("description", description);
        args.put(License.SCHOOL, school);
        args.put("user", user);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("jie", Integer.valueOf(jie));
        args.put("stage", stage);
        this.dao2._execute("insert into userposition(userNum,type,insertDate,description,schoolNum,insertUser,gradeNum,subjectNum,jie,stage) values({userNum},{type},{date},{description},{school},{user},{gradeNum},{subjectNum},{jie},{stage} )", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void schSubmit(String userNum, String type, String date, String school, String user, String stage) {
        String descriptionStr = "";
        if (stage.equals("51")) {
            descriptionStr = "小学校长";
        }
        if (stage.equals("52")) {
            descriptionStr = "初中校长";
        }
        if (stage.equals("53")) {
            descriptionStr = "高中校长";
        }
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("type", type);
        args.put("date", date);
        args.put(License.SCHOOL, school);
        args.put("user", user);
        args.put("stage", stage);
        args.put("descriptionStr", descriptionStr);
        this.dao2._execute("insert into userposition(userNum,type,insertDate,schoolNum,insertUser,stage,description) values({userNum} ,{type} ,{date},{school} ,{user} ,{stage},{descriptionStr} )", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> getByNum(String userNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        return this.dao2._queryBeanList("select classNum,gradeNum from userposition where userNum={userNum}  AND subjectNum='999' and (classNum is not null AND classNum !='')", Userposition.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Integer getcountByNum(String userNum) {
        return null;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> getByNumSub(String userNum, String subjectNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryBeanList("select  classNum,gradeNum from userposition where userNum={userNum} and subjectNum ={subjectNum} ", Userposition.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteByNumSub(String userNum, String subjectNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        this.dao2._execute("delete from userposition where userNum={userNum} and subjectNum ={subjectNum}  AND type='1'", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> infoGrByNum(String userNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        return this.dao2._queryBeanList("select gradeNum,schoolnum from userposition where userNum={userNum}  and  subjectNum='999' and (classNum is null OR classNum='')", Userposition.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> getStudyByNum(String userNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        return this.dao2._queryBeanList("select subjectNum,gradeNum from userposition where userNum={userNum}  and  subjectNum!='999' and (classNum is null OR classNum='' )", Userposition.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> getSchByNum(String userNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        return this.dao2._queryBeanList("select stage, schoolnum from userposition where userNum={userNum}  and stage is not null AND type='5'", Userposition.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteByNumT(String userNum, String type) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("type", type);
        this.dao2._execute("delete from userposition where userNum={userNum} and type={type}  and  subjectNum='999'", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteByNumG(String userNum, String type) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("type", type);
        this.dao2._execute("delete from userposition where userNum={userNum}  and type={type}  and subjectNum='999' and classNum is null", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteByNumStudy(String userNum, String type) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("type", type);
        this.dao2._execute("DELETE from userposition where userNum={userNum}  and type={type}  and subjectNum!='999' and classNum is null", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> getType(String userNum, String school) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put(License.SCHOOL, school);
        return this.dao2._queryBeanList("select DISTINCT u.type,c.className ,g.gradeName,s.subjectName,u.stage  from userposition u LEFT JOIN class c ON u.classNum=c.id LEFT JOIN grade g ON u.gradeNum=g.gradeNum LEFT JOIN `subject` s ON s.subjectNum=u.subjectNum  where u.userNum={userNum} and u.schoolnum={school} ", Userposition.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteByNumSch(String userNum, String type) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("type", type);
        this.dao2._execute("DELETE from userposition where userNum={userNum} and type={type}  and stage is not null", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getCName(String classNum, String school, String gradeNum, String isFc) {
        String sql = "SELECT className from class where   id={classNum} ";
        if ("T".equals(isFc)) {
            sql = "SELECT className from levelclass where   id={classNum} ";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_classNum, classNum);
        return this.dao2._queryStr(sql, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getSubName(String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryStr("SELECT subjectName from `subject` where subjectNum={subjectNum} ", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getGradeName(String gradeNum, String school) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(License.SCHOOL, school);
        return this.dao2._queryStr("select gradeName from grade where   gradeNum={gradeNum} and schoolNum={school} and isDelete='F'", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void importT(File file, String filename, String user, String date, Map jie_Map, String result, String schoolNum) throws IOException, ParseException {
        XSSFRow xRow;
        String sex;
        String title;
        String email;
        String tid;
        String getclassNumSql;
        HSSFRow hssfrow;
        String sex2;
        String title2;
        String email2;
        String tid2;
        String getclassNumSql2;
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        InputStream input = null;
        List<String> sqls = new ArrayList<>();
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
        }
        int ind = filename.lastIndexOf(".");
        List<String> teaList = new ArrayList<>();
        boolean flag1 = "xls".equalsIgnoreCase(filename.substring(ind + 1));
        boolean flag2 = "xlsx".equalsIgnoreCase(filename.substring(ind + 1));
        XSSFWorkbook xb = null;
        new HashMap();
        if (flag2) {
            try {
                xb = new XSSFWorkbook(input);
            } catch (Exception e2) {
            }
            xb.getSheetAt(0).getRow(0);
            for (int i = 0; i < xb.getNumberOfSheets(); i++) {
                XSSFSheet xsheet = xb.getSheetAt(i);
                for (int j = 2; j < xsheet.getPhysicalNumberOfRows() && (xRow = xsheet.getRow(j)) != null; j++) {
                    int y = xRow.getRowNum();
                    XSSFCell teacherNum1 = xRow.getCell(0);
                    String Tnum = getCellValue(teacherNum1, 1, y + 1, i + 1).trim().replace(" ", "");
                    XSSFCell teacherName1 = xRow.getCell(1);
                    String teacherName = getCellValue(teacherName1, 2, y + 1, i + 1).trim().replace(" ", "");
                    XSSFCell sex1 = xRow.getCell(2);
                    if (null == sex1 || sex1.equals("")) {
                        sex = "";
                    } else {
                        String sex3 = String.valueOf(sex1).trim();
                        sex = sex3.replace(" ", "");
                    }
                    XSSFCell title1 = xRow.getCell(3);
                    if (null == title1 || title1.equals("")) {
                        title = "";
                    } else {
                        String title3 = String.valueOf(title1);
                        title = title3.replace(" ", "");
                    }
                    String birthday3 = "";
                    XSSFCell birthday2 = xRow.getCell(4);
                    if (birthday2 != null) {
                        if (CellType._NONE == birthday2.getCellType()) {
                            if (HSSFDateUtil.isCellDateFormatted(birthday2)) {
                                Date birthday1 = birthday2.getDateCellValue();
                                birthday3 = sdf.format(birthday1);
                            }
                        } else if (birthday2.getCellType() == CellType.STRING && null != birthday2) {
                            birthday3 = String.valueOf(birthday2);
                        }
                    }
                    if (!birthday3.equals("") && null != birthday3 && !birthday3.equals("null")) {
                        sdf.parse(birthday3);
                    }
                    String birthdayStr = "";
                    String birthdayStr2 = "";
                    String birthdayStr3 = "";
                    String worktimeStr2 = "";
                    String worktimeStr3 = "";
                    if (!birthday3.equals("")) {
                        birthdayStr2 = ",birthday";
                        birthdayStr = ",'" + birthday3 + "'";
                        birthdayStr3 = ",birthday='" + birthday3 + "'";
                    }
                    XSSFCell worktime2 = xRow.getCell(5);
                    String worktime3 = "";
                    if (worktime2 != null) {
                        if (CellType._NONE == worktime2.getCellType()) {
                            if (HSSFDateUtil.isCellDateFormatted(worktime2)) {
                                Date worktime1 = worktime2.getDateCellValue();
                                worktime3 = sdf.format(worktime1);
                            }
                        } else if (worktime2.getCellType() == CellType.STRING && null != worktime2) {
                            worktime3 = String.valueOf(worktime2);
                        }
                    }
                    if (null != worktime3 && !worktime3.equals("") && !worktime3.equals("null")) {
                        sdf.parse(worktime3);
                    }
                    String worktimeStr = "";
                    if (!worktime3.equals("")) {
                        worktimeStr2 = ",worktime";
                        worktimeStr = ",'" + worktime3 + "'";
                        worktimeStr3 = ",worktime='" + worktime3 + "'";
                    }
                    XSSFCell schoolCell = xRow.getCell(6);
                    String schoolName = String.valueOf(schoolCell).trim();
                    String schoolsql = "select id from school where schoolName='" + schoolName + "'";
                    String chooseSchool = "select schoolName from school where id='" + schoolNum + "'";
                    String school = this.dao2.queryStr(schoolsql, null);
                    String chooseSchName = this.dao2.queryStr(chooseSchool, null);
                    String username = String.valueOf(user);
                    if (!chooseSchName.equals(schoolName) && !schoolNum.equals("-1")) {
                        throwException("导入文件的学校名称 和 界面所选择的学校名称不一样，请检查！");
                    }
                    if (null == school || school.equals("") || school.equals("null")) {
                        throwException("第" + (j + 1) + "行  " + schoolName + "   这个学校不存在...");
                    }
                    if (schoolNum.equals("-1") && !username.equals("-1") && !username.equals("-2")) {
                        String sql = " select /* shard_host_HG=Write */ c.schoolName from school c   left join schauthormanage s on s.schoolNum = c.id and s.userId='" + username + "'  left join user t on t.schoolNum = c.id  and t.id = '" + username + "'  where c.isDelete='F' and c.id = '" + school + "' and (s.schoolNum is not null or t.schoolNum is not null)  ";
                        String value = this.dao2.queryStr(sql, null);
                        if (null == value || value.equals("") || value.equals("null")) {
                            throwException("您对该导入文件中第" + (j + 1) + "行的学校  '" + schoolName + "' 没有管理的权限，不能导入！");
                        }
                    }
                    XSSFCell mobile1 = xRow.getCell(7);
                    String mobil = "";
                    if (mobile1 != null) {
                        if (mobile1.getCellType() == CellType.NUMERIC) {
                            mobil = this.df.format(mobile1.getNumericCellValue());
                        } else if (mobile1.getCellType() == CellType.STRING) {
                            mobil = String.valueOf(mobile1).replace(" ", "");
                        }
                    }
                    if (null == mobil || mobil.equals("")) {
                        mobil = "";
                    } else if (!CommonUtil.isMobile(mobil)) {
                        throwException("第" + (j + 1) + "行的联系电话  " + mobil + " 手机号格式不正确，请检查！");
                    }
                    XSSFCell email1 = xRow.getCell(8);
                    if (null == email1 || email1.equals("") || email1.equals("null")) {
                        email = "";
                    } else {
                        String email3 = String.valueOf(email1);
                        email = email3.replace(" ", "");
                    }
                    String gettidSql = "SELECT /* shard_host_HG=Write */ t.id,t.schoolNum,sch.schoolName FROM teacher t LEFT JOIN school sch ON sch.id = t.schoolNum WHERE t.teacherNum='" + Tnum + "' ";
                    Teacher tea = (Teacher) this.dao2.queryBean(gettidSql, Teacher.class, null);
                    if (null != tea) {
                        tid = String.valueOf(tea.getId());
                        String schNum = String.valueOf(tea.getSchoolNum());
                        String schName = tea.getSchoolName();
                        if (!school.equals(schNum)) {
                            throwException("已经存在 " + schName + " 这个学校的教师ID号 " + Tnum + " ,请检查第 " + (j + 1) + " 行的教师ID号和学校信息是否正确！");
                        }
                    } else {
                        tid = GUID.getGUIDStr();
                    }
                    if (!"".equals(mobil) && !checkMobile(Tnum, mobil, null)) {
                        throwException("第" + (j + 1) + "行的联系电话  " + mobil + " 手机号已被其他教师用户使用，请检查！");
                    }
                    String getuidSql = "SELECT /* shard_host_HG=Write */ u.id FROM `user` u LEFT JOIN teacher t ON u.userid=t.id WHERE t.teacherNum='" + Tnum + "' AND u.usertype='1'";
                    String userid = GUID.getGUIDStr();
                    if (null != this.dao2.queryStr(getuidSql, null) && !"".equals(this.dao2.queryStr(getuidSql, null)) && !"null".equals(this.dao2.queryStr(getuidSql, null))) {
                        userid = this.dao2.queryStr(getuidSql, null);
                    }
                    XSSFCell postCell = xRow.getCell(10);
                    String postName = "";
                    if (null != postCell) {
                        postName = String.valueOf(postCell);
                    }
                    String getpostNumSql = "SELECT value FROM `data` WHERE name='" + postName + "' and type = 31";
                    String postNum = this.dao2.queryStr(getpostNumSql, null);
                    if (null != postName && !"".equals(postName)) {
                        if (null == postNum || "".equals(postNum) || "null".equals(postNum)) {
                            throwException("系统中没有" + postName + "这个职务，请检查。。。。。。");
                        } else {
                            XSSFCell subjectNameCell = xRow.getCell(11);
                            String subjectName = "";
                            if (null != subjectNameCell) {
                                subjectName = String.valueOf(subjectNameCell);
                                if (subjectName.indexOf("（") != -1) {
                                    subjectName = subjectName.replace("（", "(").replace(" ", "");
                                }
                                if (subjectName.indexOf("）") != -1) {
                                    subjectName = subjectName.replace("）", ")");
                                }
                            }
                            String getsubNumSql = "SELECT subjectNum FROM `subject` WHERE subjectName='" + subjectName + "'";
                            String subjectNum = this.dao2.queryStr(getsubNumSql, null);
                            if (null != subjectName && !"".equals(subjectName) && !"null".equals(subjectName)) {
                                if (null == subjectNum || "".equals(subjectNum) || "null".equals(subjectNum)) {
                                    throwException("系统中没有" + subjectName + "这个科目，请检查。。。。。。");
                                }
                            } else if (postNum.equals("1") || postNum.equals("4")) {
                                throwException("第" + (j + 1) + "行没有填写任教科目，请检查。。。。。。");
                            }
                            XSSFCell gradeNameCell = xRow.getCell(12);
                            String gradeName = "";
                            if (null != gradeNameCell) {
                                gradeName = String.valueOf(gradeNameCell);
                            }
                            String getGNumSql = "SELECT gradeNum FROM grade WHERE gradeName='" + gradeName + "' and schoolNum = " + school;
                            String gradeNum = this.dao2.queryStr(getGNumSql, null);
                            if (null != gradeName && !"".equals(gradeName) && !"null".equals(gradeName)) {
                                if (null == gradeNum || "".equals(gradeNum) || "null".equals(gradeNum)) {
                                    throwException(schoolName + "学校下没有" + gradeName + "这个年级，请检查。。。。。。");
                                }
                            } else if (postNum.equals("1") || postNum.equals("2") || postNum.equals("3") || postNum.equals("4") || postNum.equals("5")) {
                                throwException("第" + (j + 1) + "行没有填写任教年级，请检查。。。。。。");
                            }
                            String jie = String.valueOf(jie_Map.get(gradeNum));
                            XSSFCell classNameCell = xRow.getCell(13);
                            String classNameList = "";
                            if (null != classNameCell && !classNameCell.equals("") && !classNameCell.equals("null")) {
                                if (classNameCell.getCellType() == CellType.NUMERIC) {
                                    classNameList = this.df.format(classNameCell.getNumericCellValue());
                                } else if (classNameCell.getCellType() == CellType.STRING) {
                                    classNameList = classNameCell.getStringCellValue().trim();
                                }
                            } else if (postNum.equals("1") || postNum.equals("2")) {
                                throwException("第" + (j + 1) + "行没有填写任教班级，请检查。。。。。。");
                            }
                            if (null != classNameCell && !classNameCell.equals("") && classNameList.indexOf("，") != -1) {
                                classNameList = classNameList.replaceAll("，", Const.STRING_SEPERATOR);
                            }
                            String[] classNameList2 = classNameList.split(Const.STRING_SEPERATOR);
                            XSSFCell perClassCell = xRow.getCell(14);
                            String perClass = "1";
                            if (null != perClassCell && !perClassCell.equals("") && !perClassCell.equals("null") && perClassCell.getCellType() != CellType.BLANK) {
                                if (perClassCell.getCellType() == CellType.NUMERIC) {
                                    perClass = this.df.format(perClassCell.getNumericCellValue());
                                } else if (perClassCell.getCellType() == CellType.STRING) {
                                    perClass = perClassCell.getStringCellValue().trim();
                                }
                            } else if (postNum.equals("1") || postNum.equals("2")) {
                                throwException("第" + (j + 1) + "行没有填写班级权限，请检查。。。。。。");
                            }
                            XSSFCell perGradeCell = xRow.getCell(15);
                            String perGrade = "1";
                            if (null != perGradeCell && !perGradeCell.equals("") && !perGradeCell.equals("null") && perGradeCell.getCellType() != CellType.BLANK) {
                                if (perGradeCell.getCellType() == CellType.NUMERIC) {
                                    perGrade = this.df.format(perGradeCell.getNumericCellValue());
                                } else if (perGradeCell.getCellType() == CellType.STRING) {
                                    perGrade = perGradeCell.getStringCellValue().trim();
                                }
                            } else if (postNum.equals("1") || postNum.equals("2") || postNum.equals("3") || postNum.equals("4")) {
                                throwException("第" + (j + 1) + "行没有填写年级权限，请检查。。。。。。");
                            }
                            XSSFCell perSubjectCell = xRow.getCell(16);
                            String perSubject = "1";
                            if (null != perSubjectCell && !perSubjectCell.equals("") && !perSubjectCell.equals("null") && perSubjectCell.getCellType() != CellType.BLANK) {
                                if (perSubjectCell.getCellType() == CellType.NUMERIC) {
                                    perSubject = this.df.format(perSubjectCell.getNumericCellValue());
                                } else if (perSubjectCell.getCellType() == CellType.STRING) {
                                    perSubject = perSubjectCell.getStringCellValue().trim();
                                }
                            } else if (postNum.equals("1") || postNum.equals("4")) {
                                throwException("第" + (j + 1) + "行没有填写科目权限，请检查。。。。。。");
                            }
                            String stage = getStage(gradeNum);
                            int count = 0;
                            if (postNum.equals("1") || postNum.equals("2")) {
                                for (String className : classNameList2) {
                                    String classNum = "";
                                    String desc = className + subjectName + "老师";
                                    if (postNum.equals("1")) {
                                        if (result.equals("F")) {
                                            getclassNumSql = "SELECT id ext1 FROM class WHERE className='" + className + "' AND gradeNum='" + gradeNum + "' AND schoolNum='" + school + "' AND isDelete='F'";
                                        } else {
                                            getclassNumSql = "SELECT id ext1 FROM levelclass WHERE subjectNum='" + subjectNum + "' AND schoolNum='" + school + "' AND gradeNum='" + gradeNum + "' AND className='" + className + "'  AND isDelete='F'";
                                        }
                                        classNum = this.dao2.queryStr(getclassNumSql, null);
                                        String whereSql = " WHERE schoolNum='" + school + "' AND gradeNum='" + gradeNum + "' AND classNum='" + classNum + "' AND subjectNum='" + subjectNum + "' AND jie='" + jie + "' AND usernum!='" + userid + "' AND type = " + postNum;
                                        if (null != classNum && !classNum.equals("null") && !classNum.equals("") && null != gradeNum && !gradeNum.equals("null") && !gradeNum.equals("")) {
                                            count = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + whereSql, null).intValue();
                                        }
                                        if (count > 0) {
                                            sqls.add("delete from userposition" + whereSql);
                                        }
                                        if (null == classNum || classNum.equals("null") || classNum.trim().equals("")) {
                                            if (result.equals("F")) {
                                                throwException("没有" + gradeName + className + "这个班级,请检查Excel表中的第" + (j + 1) + "行");
                                            } else {
                                                throwException(subjectName + "科目下没有" + gradeName + className + "这个教学班班级,请检查Excel表中的第" + (j + 1) + "行");
                                            }
                                        }
                                    } else if (postNum.equals("2")) {
                                        String getclassNumSql3 = "SELECT id ext1 FROM class WHERE className='" + className + "' AND gradeNum='" + gradeNum + "' AND schoolNum='" + school + "' AND isDelete='F'";
                                        classNum = this.dao2.queryStr(getclassNumSql3, null);
                                        if (null == classNum || "null".equals(classNum) || "".equals(classNum)) {
                                            throwException("没有" + gradeName + className + "这个班级,请检查Excel表中的第" + (j + 1) + "行");
                                        }
                                        String whereSql2 = " WHERE schoolNum='" + school + "' AND gradeNum='" + gradeNum + "' AND classNum='" + classNum + "' AND type=" + postNum + " AND  subjectNum='999' AND userNum!='" + userid + "'";
                                        if (null != classNum && !classNum.equals("null") && !classNum.equals("") && null != gradeNum && !gradeNum.equals("null") && !gradeNum.equals("")) {
                                            count = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + whereSql2, null).intValue();
                                        }
                                        if (count > 0) {
                                            sqls.add("delete from userposition" + whereSql2);
                                        }
                                        if (null == classNum || "null".equals(classNum) || "".equals(classNum)) {
                                            throwException("没有" + gradeName + className + "这个班级,请检查Excel表中的第" + (j + 1) + "行");
                                        }
                                        desc = className + "班主任";
                                        subjectNum = "999";
                                        perSubject = "1";
                                    }
                                    String insertUserPositionSql = "INSERT INTO userposition(userNum,type,insertDate,description,schoolnum,gradenum,subjectNum,classNum,insertUser,jie,stage,permission_class,permission_grade,permission_subject) VALUES('" + userid + "','" + postNum + "','" + date + "','" + desc + "','" + school + "'," + gradeNum + Const.STRING_SEPERATOR + subjectNum + Const.STRING_SEPERATOR + classNum + ",'" + user + "','" + jie + "','" + stage + "','" + perClass + "','" + perGrade + "','" + perSubject + "') ON DUPLICATE KEY UPDATE  userNum='" + userid + "',type='" + postNum + "',schoolNum='" + school + "',gradenum=" + gradeNum + ",subjectNum=" + subjectNum + ",classnum=" + classNum + ",jie='" + jie + "',stage='" + stage + "',permission_class='" + perClass + "',permission_grade='" + perGrade + "',permission_subject='" + perSubject + "' ";
                                    sqls.add(insertUserPositionSql);
                                }
                            } else {
                                String desc2 = gradeName + "年级主任";
                                String classNum2 = null;
                                if (postNum.equals("3")) {
                                    String whereSql3 = " WHERE schoolNum='" + school + "' AND gradeNum='" + gradeNum + "' AND type=" + postNum + " AND  subjectNum='999' AND userNum!='" + userid + "'";
                                    int count2 = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + whereSql3, null).intValue();
                                    if (count2 > 0) {
                                        sqls.add("delete from userposition" + whereSql3);
                                    }
                                    subjectNum = "999";
                                    perClass = "1";
                                    perSubject = "1";
                                } else if (postNum.equals("4")) {
                                    String whereSql4 = " WHERE schoolNum='" + school + "' AND gradeNum='" + gradeNum + "' AND type=" + postNum + " AND  subjectNum='" + subjectNum + "' AND userNum!='" + userid + "'";
                                    int count3 = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + whereSql4, null).intValue();
                                    if (count3 > 0) {
                                        sqls.add("delete from userposition" + whereSql4);
                                    }
                                    desc2 = gradeName + "的" + subjectName + "教研主任";
                                    classNum2 = null;
                                    perClass = "1";
                                } else if (postNum.equals("5")) {
                                    String stageNameSql = "select name from data where type = 5 and value = " + stage;
                                    String stageName = this.dao2.queryStr(stageNameSql, null);
                                    desc2 = stageName + "校长";
                                    int count4 = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + (" WHERE schoolNum='" + school + "' AND type=" + postNum + " AND userNum!='" + userid + "'"), null).intValue();
                                    if (count4 > 0) {
                                    }
                                    classNum2 = null;
                                    gradeNum = null;
                                    subjectNum = null;
                                    perClass = "1";
                                    perGrade = "1";
                                    perSubject = "1";
                                } else {
                                    desc2 = postName;
                                    int count5 = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + (" WHERE type=" + postNum + " AND userNum!='" + userid + "'"), null).intValue();
                                    if (count5 > 0) {
                                    }
                                    if ("null".equals(jie)) {
                                        jie = null;
                                    }
                                }
                                String insertUserPositionSql2 = "INSERT INTO userposition(userNum,type,insertDate,description,schoolnum,gradenum,subjectNum,classNum,insertUser,jie,stage,permission_class,permission_grade,permission_subject) VALUES('" + userid + "','" + postNum + "','" + date + "','" + desc2 + "','" + school + "'," + gradeNum + Const.STRING_SEPERATOR + subjectNum + Const.STRING_SEPERATOR + classNum2 + ",'" + user + "'," + jie + ",'" + stage + "','" + perClass + "','" + perGrade + "','" + perSubject + "') ON DUPLICATE KEY UPDATE  userNum='" + userid + "',type='" + postNum + "',schoolNum='" + school + "',gradenum=" + gradeNum + ",subjectNum=" + subjectNum + ",classnum=" + classNum2 + ",jie=" + jie + ",stage='" + stage + "',permission_class='" + perClass + "',permission_grade='" + perGrade + "',permission_subject='" + perSubject + "'";
                                sqls.add(insertUserPositionSql2);
                            }
                        }
                    }
                    Boolean delFlag = true;
                    int i1 = 0;
                    while (true) {
                        if (i1 >= teaList.size()) {
                            break;
                        }
                        if (!Tnum.equals(teaList.get(i1))) {
                            i1++;
                        } else {
                            delFlag = false;
                            break;
                        }
                    }
                    teaList.add(Tnum);
                    if (delFlag.booleanValue()) {
                        String deleteUserPositionSql = "delete from userposition where userNum = " + userid;
                        this.dao2.execute(deleteUserPositionSql);
                    }
                    String sql2 = "insert into teacher  (id,teacherNum,teacherName,sex,title" + birthdayStr2 + worktimeStr2 + ",insertUser,insertDate,updateUser,updateDate,schoolNum,mobile,email,isDelete) VALUES ('" + tid + "','" + Tnum + "','" + teacherName + "','" + sex + "','" + title + "'" + birthdayStr + worktimeStr + ",'" + user + "','" + date + "','" + user + "','" + date + "','" + school + "','" + mobil + "','" + email + "','F') on DUPLICATE KEY UPDATE  teacherNum='" + Tnum + "',teacherName='" + teacherName + "',sex='" + sex + "',title='" + title + "',schoolNum='" + school + "',mobile='" + mobil + "',email='" + email + "'" + birthdayStr3 + worktimeStr3 + ",isDelete='F'";
                    sqls.add(sql2);
                    String pas1 = "";
                    String pas2 = "";
                    XSSFCell password1 = xRow.getCell(9);
                    String pw1 = String.valueOf(password1).trim();
                    String password = Const.USER_PASSWORD;
                    if (null != pw1 && !"".equals(pw1) && !"null".equals(pw1)) {
                        pas1 = "md5(";
                        pas2 = ")";
                        if (password1.getCellType() == CellType.NUMERIC) {
                            password = this.df.format(password1.getNumericCellValue());
                        } else if (password1.getCellType() == CellType.STRING) {
                            password = String.valueOf(password1).replace(" ", "");
                        }
                    }
                    if (null == pw1 || "".equals(pw1) || "null".equals(pw1)) {
                        String getupasSql = "select /* shard_host_HG=Write */ password from user where id = " + userid;
                        Object getupas = this.dao2.queryObject(getupasSql);
                        if (null != String.valueOf(getupas) && !"".equals(String.valueOf(getupas)) && !"null".equals(String.valueOf(getupas))) {
                            password = String.valueOf(getupas);
                        }
                    }
                    String userSql = "insert into user (id,userid,isDelete,username,password,usertype,realname,insertUser,insertDate,schoolnum,isUser) values ( '" + userid + "', '" + tid + "', 'F', '" + Tnum + "'," + pas1 + " '" + password.toLowerCase() + "'" + pas2 + ", '1', '" + teacherName + "', '" + user + "', '" + date + "', '" + school + "', 'T') ON DUPLICATE KEY UPDATE  userid='" + tid + "', username='" + Tnum + "', password=" + pas1 + "'" + password.toLowerCase() + "'" + pas2 + ", usertype='1', realname='" + teacherName + "', schoolnum='" + school + "', isDelete='F'";
                    sqls.add(userSql);
                    String userroleSql = "INSERT INTO userrole(userNum,roleNum,insertUser,insertDate,isDelete)  VALUES('" + userid + "','1','" + user + "','" + date + "','F')  ON DUPLICATE KEY UPDATE userNum='" + userid + "',roleNum='1'";
                    sqls.add(userroleSql);
                    this.dao2.batchExecute(sqls);
                    sqls.clear();
                }
            }
        }
        if (flag1) {
            POIFSFileSystem fs = new POIFSFileSystem(input);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            wb.getSheetAt(0).getRow(0);
            for (int i2 = 0; i2 < wb.getNumberOfSheets(); i2++) {
                HSSFSheet sheet = wb.getSheetAt(i2);
                for (int j2 = 2; j2 < sheet.getPhysicalNumberOfRows() && (hssfrow = sheet.getRow(j2)) != null; j2++) {
                    int y2 = hssfrow.getRowNum();
                    HSSFCell teacherNum12 = hssfrow.getCell(0);
                    String Tnum2 = getCellValue(teacherNum12, 1, y2 + 1, i2 + 1).trim().replace(" ", "");
                    HSSFCell teacherName12 = hssfrow.getCell(1);
                    String teacherName2 = getCellValue(teacherName12, 2, y2 + 1, i2 + 1).trim().replace(" ", "");
                    HSSFCell sex12 = hssfrow.getCell(2);
                    if (null == sex12 || sex12.equals("")) {
                        sex2 = "";
                    } else {
                        String sex4 = String.valueOf(sex12).trim();
                        sex2 = sex4.replace(" ", "");
                    }
                    HSSFCell title12 = hssfrow.getCell(3);
                    if (null == title12 || title12.equals("")) {
                        title2 = "";
                    } else {
                        String title4 = String.valueOf(title12);
                        title2 = title4.replace(" ", "");
                    }
                    String birthday32 = "";
                    HSSFCell birthday22 = hssfrow.getCell(4);
                    if (birthday22 != null) {
                        if (CellType._NONE == birthday22.getCellType()) {
                            if (HSSFDateUtil.isCellDateFormatted(birthday22)) {
                                Date birthday12 = birthday22.getDateCellValue();
                                birthday32 = sdf.format(birthday12);
                            }
                        } else if (birthday22.getCellType() == CellType.STRING && null != birthday22) {
                            birthday32 = String.valueOf(birthday22);
                        }
                    }
                    if (!birthday32.equals("") && null != birthday32 && !birthday32.equals("null")) {
                        sdf.parse(birthday32);
                    }
                    String birthdayStr4 = "";
                    String birthdayStr22 = "";
                    String birthdayStr32 = "";
                    String worktimeStr22 = "";
                    String worktimeStr32 = "";
                    if (!birthday32.equals("")) {
                        birthdayStr22 = ",birthday";
                        birthdayStr4 = ",'" + birthday32 + "'";
                        birthdayStr32 = ",birthday='" + birthday32 + "'";
                    }
                    HSSFCell worktime22 = hssfrow.getCell(5);
                    String worktime32 = "";
                    if (worktime22 != null) {
                        if (CellType._NONE == worktime22.getCellType()) {
                            if (HSSFDateUtil.isCellDateFormatted(worktime22)) {
                                Date worktime12 = worktime22.getDateCellValue();
                                worktime32 = sdf.format(worktime12);
                            }
                        } else if (worktime22.getCellType() == CellType.STRING && null != worktime22) {
                            worktime32 = String.valueOf(worktime22);
                        }
                    }
                    if (null != worktime32 && !worktime32.equals("") && !worktime32.equals("null")) {
                        sdf.parse(worktime32);
                    }
                    String worktimeStr4 = "";
                    if (!worktime32.equals("")) {
                        worktimeStr22 = ",worktime";
                        worktimeStr4 = ",'" + worktime32 + "'";
                        worktimeStr32 = ",worktime='" + worktime32 + "'";
                    }
                    HSSFCell schoolCell2 = hssfrow.getCell(6);
                    String schoolName2 = String.valueOf(schoolCell2).trim();
                    String schoolsql2 = "select id from school where schoolName='" + schoolName2 + "'";
                    String chooseSchool2 = "select schoolName from school where id='" + schoolNum + "'";
                    String school2 = this.dao2.queryStr(schoolsql2, null);
                    String chooseSchName2 = this.dao2.queryStr(chooseSchool2, null);
                    String username2 = String.valueOf(user);
                    if (!chooseSchName2.equals(schoolName2) && !schoolNum.equals("-1")) {
                        throwException("导入文件的学校名称 和 界面所选择的学校名称不一样，请检查！");
                    }
                    if (null == school2 || school2.equals("") || school2.equals("null")) {
                        throwException("第" + (j2 + 1) + "行  " + schoolName2 + "   这个学校不存在...");
                    }
                    if (schoolNum.equals("-1") && !username2.equals("-1") && !username2.equals("-2")) {
                        String sql3 = " select /* shard_host_HG=Write */ c.schoolName from school c   left join schauthormanage s on s.schoolNum = c.id and s.userId='" + username2 + "'  left join user t on t.schoolNum = c.id  and t.id = '" + username2 + "'  where c.isDelete='F' and c.id = '" + school2 + "' and (s.schoolNum is not null or t.schoolNum is not null)  ";
                        String value2 = this.dao2.queryStr(sql3, null);
                        if (null == value2 || value2.equals("") || value2.equals("null")) {
                            throwException("您对该导入文件中第" + (j2 + 1) + "行的学校  '" + schoolName2 + "' 没有管理的权限，不能导入！");
                        }
                    }
                    HSSFCell mobile12 = hssfrow.getCell(7);
                    String mobil2 = "";
                    if (mobile12 != null) {
                        if (mobile12.getCellType() == CellType.NUMERIC) {
                            mobil2 = this.df.format(mobile12.getNumericCellValue());
                        } else if (mobile12.getCellType() == CellType.STRING) {
                            mobil2 = String.valueOf(mobile12).replace(" ", "");
                        }
                    }
                    if (null == mobil2 || mobil2.equals("")) {
                        mobil2 = "";
                    } else if (!CommonUtil.isMobile(mobil2)) {
                        throwException("第" + (j2 + 1) + "行的联系电话  " + mobil2 + " 手机号格式不正确，请检查！");
                    }
                    HSSFCell email12 = hssfrow.getCell(8);
                    if (null == email12 || email12.equals("") || email12.equals("null")) {
                        email2 = "";
                    } else {
                        String email4 = String.valueOf(email12);
                        email2 = email4.replace(" ", "");
                    }
                    String gettidSql2 = "SELECT /* shard_host_HG=Write */ t.id,t.schoolNum,sch.schoolName FROM teacher t LEFT JOIN school sch ON sch.id = t.schoolNum WHERE t.teacherNum='" + Tnum2 + "' ";
                    Teacher tea2 = (Teacher) this.dao2.queryBean(gettidSql2, Teacher.class, null);
                    if (null != tea2) {
                        tid2 = String.valueOf(tea2.getId());
                        String schNum2 = String.valueOf(tea2.getSchoolNum());
                        String schName2 = tea2.getSchoolName();
                        if (!school2.equals(schNum2)) {
                            throwException("已经存在 " + schName2 + " 这个学校的教师ID号 " + Tnum2 + " ,请检查第 " + (j2 + 1) + " 行的教师ID号和学校信息是否正确！");
                        }
                    } else {
                        tid2 = GUID.getGUIDStr();
                    }
                    if (!"".equals(mobil2) && !checkMobile(Tnum2, mobil2, null)) {
                        throwException("第" + (j2 + 1) + "行的联系电话  " + mobil2 + " 手机号已被其他教师用户使用，请检查！");
                    }
                    String getuidSql2 = "SELECT /* shard_host_HG=Write */ u.id FROM `user` u LEFT JOIN teacher t ON u.userid=t.id WHERE t.teacherNum='" + Tnum2 + "' AND u.usertype='1'";
                    String userid2 = GUID.getGUIDStr();
                    if (null != this.dao2.queryStr(getuidSql2, null) && !"".equals(this.dao2.queryStr(getuidSql2, null)) && !"null".equals(this.dao2.queryStr(getuidSql2, null))) {
                        userid2 = this.dao2.queryStr(getuidSql2, null);
                    }
                    HSSFCell postCell2 = hssfrow.getCell(10);
                    String postName2 = "";
                    if (null != postCell2) {
                        postName2 = String.valueOf(postCell2);
                    }
                    String getpostNumSql2 = "SELECT value FROM `data` WHERE name='" + postName2 + "' and type = 31";
                    String postNum2 = this.dao2.queryStr(getpostNumSql2, null);
                    if (null != postName2 && !"".equals(postName2)) {
                        if (null == postNum2 || "".equals(postNum2) || "null".equals(postNum2)) {
                            throwException("系统中没有" + postName2 + "这个职务，请检查。。。。。。");
                        } else {
                            HSSFCell subjectNameCell2 = hssfrow.getCell(11);
                            String subjectName2 = "";
                            if (null != subjectNameCell2) {
                                subjectName2 = String.valueOf(subjectNameCell2);
                                if (subjectName2.indexOf("（") != -1) {
                                    subjectName2 = subjectName2.replace("（", "(").replace(" ", "");
                                }
                                if (subjectName2.indexOf("）") != -1) {
                                    subjectName2 = subjectName2.replace("）", ")");
                                }
                            }
                            String getsubNumSql2 = "SELECT subjectNum FROM `subject` WHERE subjectName='" + subjectName2 + "'";
                            String subjectNum2 = this.dao2.queryStr(getsubNumSql2, null);
                            if (null != subjectName2 && !"".equals(subjectName2) && !"null".equals(subjectName2)) {
                                if (null == subjectNum2 || "".equals(subjectNum2) || "null".equals(subjectNum2)) {
                                    throwException("系统中没有" + subjectName2 + "这个科目，请检查。。。。。。");
                                }
                            } else if (postNum2.equals("1") || postNum2.equals("4")) {
                                throwException("第" + (j2 + 1) + "行没有填写任教科目，请检查。。。。。。");
                            }
                            HSSFCell gradeNameCell2 = hssfrow.getCell(12);
                            String gradeName2 = "";
                            if (null != gradeNameCell2) {
                                gradeName2 = String.valueOf(gradeNameCell2);
                            }
                            String getGNumSql2 = "SELECT gradeNum FROM grade WHERE gradeName='" + gradeName2 + "' and schoolNum = " + school2;
                            String gradeNum2 = this.dao2.queryStr(getGNumSql2, null);
                            if (null != gradeName2 && !"".equals(gradeName2) && !"null".equals(gradeName2)) {
                                if (null == gradeNum2 || "".equals(gradeNum2) || "null".equals(gradeNum2)) {
                                    throwException(schoolName2 + "学校下没有" + gradeName2 + "这个年级，请检查。。。。。。");
                                }
                            } else if (postNum2.equals("1") || postNum2.equals("2") || postNum2.equals("3") || postNum2.equals("4") || postNum2.equals("5")) {
                                throwException("第" + (j2 + 1) + "行没有填写任教年级，请检查。。。。。。");
                            }
                            String jie2 = String.valueOf(jie_Map.get(gradeNum2));
                            HSSFCell classNameCell2 = hssfrow.getCell(13);
                            String classNameList3 = "";
                            if (null != classNameCell2 && !classNameCell2.equals("") && !classNameCell2.equals("null")) {
                                if (classNameCell2.getCellType() == CellType.NUMERIC) {
                                    classNameList3 = this.df.format(classNameCell2.getNumericCellValue());
                                } else if (classNameCell2.getCellType() == CellType.STRING) {
                                    classNameList3 = classNameCell2.getStringCellValue().trim();
                                }
                            } else if (postNum2.equals("1") || postNum2.equals("2")) {
                                throwException("第" + (j2 + 1) + "行没有填写任教班级，请检查。。。。。。");
                            }
                            if (null != classNameCell2 && !classNameCell2.equals("") && classNameList3.indexOf("，") != -1) {
                                classNameList3 = classNameList3.replaceAll("，", Const.STRING_SEPERATOR);
                            }
                            String[] classNameList22 = classNameList3.split(Const.STRING_SEPERATOR);
                            HSSFCell perClassCell2 = hssfrow.getCell(14);
                            String perClass2 = "1";
                            if (null != perClassCell2 && !perClassCell2.equals("") && !perClassCell2.equals("null") && perClassCell2.getCellType() != CellType.BLANK) {
                                if (perClassCell2.getCellType() == CellType.NUMERIC) {
                                    perClass2 = this.df.format(perClassCell2.getNumericCellValue());
                                } else if (perClassCell2.getCellType() == CellType.STRING) {
                                    perClass2 = perClassCell2.getStringCellValue().trim();
                                }
                            } else if (postNum2.equals("1") || postNum2.equals("2")) {
                                throwException("第" + (j2 + 1) + "行没有填写班级权限，请检查。。。。。。");
                            }
                            HSSFCell perGradeCell2 = hssfrow.getCell(15);
                            String perGrade2 = "1";
                            if (null != perGradeCell2 && !perGradeCell2.equals("") && !perGradeCell2.equals("null") && perGradeCell2.getCellType() != CellType.BLANK) {
                                if (perGradeCell2.getCellType() == CellType.NUMERIC) {
                                    perGrade2 = this.df.format(perGradeCell2.getNumericCellValue());
                                } else if (perGradeCell2.getCellType() == CellType.STRING) {
                                    perGrade2 = perGradeCell2.getStringCellValue().trim();
                                }
                            } else if (postNum2.equals("1") || postNum2.equals("2") || postNum2.equals("3") || postNum2.equals("4")) {
                                throwException("第" + (j2 + 1) + "行没有填写年级权限，请检查。。。。。。");
                            }
                            HSSFCell perSubjectCell2 = hssfrow.getCell(16);
                            String perSubject2 = "1";
                            if (null != perSubjectCell2 && !perSubjectCell2.equals("") && !perSubjectCell2.equals("null") && perSubjectCell2.getCellType() != CellType.BLANK) {
                                if (perSubjectCell2.getCellType() == CellType.NUMERIC) {
                                    perSubject2 = this.df.format(perSubjectCell2.getNumericCellValue());
                                } else if (perSubjectCell2.getCellType() == CellType.STRING) {
                                    perSubject2 = perSubjectCell2.getStringCellValue().trim();
                                }
                            } else if (postNum2.equals("1") || postNum2.equals("4")) {
                                throwException("第" + (j2 + 1) + "行没有填写科目权限，请检查。。。。。。");
                            }
                            String stage2 = getStage(gradeNum2);
                            int count6 = 0;
                            if (postNum2.equals("1") || postNum2.equals("2")) {
                                for (String className2 : classNameList22) {
                                    String classNum3 = "";
                                    String desc3 = className2 + subjectName2 + "老师";
                                    if (postNum2.equals("1")) {
                                        if (result.equals("F")) {
                                            getclassNumSql2 = "SELECT id ext1 FROM class WHERE className='" + className2 + "' AND gradeNum='" + gradeNum2 + "' AND schoolNum='" + school2 + "' AND isDelete='F'";
                                        } else {
                                            getclassNumSql2 = "SELECT id ext1 FROM levelclass WHERE subjectNum='" + subjectNum2 + "' AND schoolNum='" + school2 + "' AND gradeNum='" + gradeNum2 + "' AND className='" + className2 + "'  AND isDelete='F'";
                                        }
                                        classNum3 = this.dao2.queryStr(getclassNumSql2, null);
                                        String whereSql5 = " WHERE schoolNum='" + school2 + "' AND gradeNum='" + gradeNum2 + "' AND classNum='" + classNum3 + "' AND subjectNum='" + subjectNum2 + "' AND jie='" + jie2 + "' AND usernum!='" + userid2 + "' AND type = " + postNum2;
                                        if (null != classNum3 && !classNum3.equals("null") && !classNum3.equals("") && null != gradeNum2 && !gradeNum2.equals("null") && !gradeNum2.equals("")) {
                                            count6 = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + whereSql5, null).intValue();
                                        }
                                        if (count6 > 0) {
                                            sqls.add("delete from userposition" + whereSql5);
                                        }
                                        if (null == classNum3 || classNum3.equals("null") || classNum3.trim().equals("")) {
                                            if (result.equals("F")) {
                                                throwException("没有" + gradeName2 + className2 + "这个班级,请检查Excel表中的第" + (j2 + 1) + "行");
                                            } else {
                                                throwException(subjectName2 + "科目下没有" + gradeName2 + className2 + "这个教学班班级,请检查Excel表中的第" + (j2 + 1) + "行");
                                            }
                                        }
                                    } else if (postNum2.equals("2")) {
                                        String getclassNumSql4 = "SELECT id ext1 FROM class WHERE className='" + className2 + "' AND gradeNum='" + gradeNum2 + "' AND schoolNum='" + school2 + "' AND isDelete='F'";
                                        classNum3 = this.dao2.queryStr(getclassNumSql4, null);
                                        if (null == classNum3 || "null".equals(classNum3) || "".equals(classNum3)) {
                                            throwException("没有" + gradeName2 + className2 + "这个班级,请检查Excel表中的第" + (j2 + 1) + "行");
                                        }
                                        String whereSql6 = " WHERE schoolNum='" + school2 + "' AND gradeNum='" + gradeNum2 + "' AND classNum='" + classNum3 + "' AND type=" + postNum2 + " AND  subjectNum='999' AND userNum!='" + userid2 + "'";
                                        if (null != classNum3 && !classNum3.equals("null") && !classNum3.equals("") && null != gradeNum2 && !gradeNum2.equals("null") && !gradeNum2.equals("")) {
                                            count6 = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + whereSql6, null).intValue();
                                        }
                                        if (count6 > 0) {
                                            sqls.add("delete from userposition" + whereSql6);
                                        }
                                        if (null == classNum3 || "null".equals(classNum3) || "".equals(classNum3)) {
                                            throwException("没有" + gradeName2 + className2 + "这个班级,请检查Excel表中的第" + (j2 + 1) + "行");
                                        }
                                        desc3 = className2 + "班主任";
                                        subjectNum2 = "999";
                                        perSubject2 = "1";
                                    }
                                    String insertUserPositionSql3 = "INSERT INTO userposition(userNum,type,insertDate,description,schoolnum,gradenum,subjectNum,classNum,insertUser,jie,stage,permission_class,permission_grade,permission_subject) VALUES('" + userid2 + "','" + postNum2 + "','" + date + "','" + desc3 + "','" + school2 + "'," + gradeNum2 + Const.STRING_SEPERATOR + subjectNum2 + Const.STRING_SEPERATOR + classNum3 + ",'" + user + "','" + jie2 + "','" + stage2 + "','" + perClass2 + "','" + perGrade2 + "','" + perSubject2 + "') ON DUPLICATE KEY UPDATE  userNum='" + userid2 + "',type='" + postNum2 + "',schoolNum='" + school2 + "',gradenum=" + gradeNum2 + ",subjectNum=" + subjectNum2 + ",classnum=" + classNum3 + ",jie='" + jie2 + "',stage='" + stage2 + "',permission_class='" + perClass2 + "',permission_grade='" + perGrade2 + "',permission_subject='" + perSubject2 + "'";
                                    sqls.add(insertUserPositionSql3);
                                }
                            } else {
                                String desc4 = gradeName2 + "年级主任";
                                String classNum4 = null;
                                if (postNum2.equals("3")) {
                                    String whereSql7 = " WHERE schoolNum='" + school2 + "' AND gradeNum='" + gradeNum2 + "' AND type=" + postNum2 + " AND  subjectNum='999' AND userNum!='" + userid2 + "'";
                                    int count7 = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + whereSql7, null).intValue();
                                    if (count7 > 0) {
                                        sqls.add("delete from userposition" + whereSql7);
                                    }
                                    subjectNum2 = "999";
                                    perClass2 = "1";
                                    perSubject2 = "1";
                                } else if (postNum2.equals("4")) {
                                    String whereSql8 = " WHERE schoolNum='" + school2 + "' AND gradeNum='" + gradeNum2 + "' AND type=" + postNum2 + " AND  subjectNum='" + subjectNum2 + "' AND userNum!='" + userid2 + "'";
                                    int count8 = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + whereSql8, null).intValue();
                                    if (count8 > 0) {
                                        sqls.add("delete from userposition" + whereSql8);
                                    }
                                    desc4 = gradeName2 + "的" + subjectName2 + "教研主任";
                                    classNum4 = null;
                                    perClass2 = "1";
                                } else if (postNum2.equals("5")) {
                                    String stageNameSql2 = "select name from data where type = 5 and value = " + stage2;
                                    String stageName2 = this.dao2.queryStr(stageNameSql2, null);
                                    desc4 = stageName2 + "校长";
                                    int count9 = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + (" WHERE schoolNum='" + school2 + "' AND type=" + postNum2 + " AND userNum!='" + userid2 + "'"), null).intValue();
                                    if (count9 > 0) {
                                    }
                                    classNum4 = null;
                                    gradeNum2 = null;
                                    subjectNum2 = null;
                                    perClass2 = "1";
                                    perGrade2 = "1";
                                    perSubject2 = "1";
                                } else {
                                    desc4 = postName2;
                                    int count10 = this.dao2.queryInt("SELECT /* shard_host_HG=Write */ count(1) FROM userposition" + (" WHERE type=" + postNum2 + " AND userNum!='" + userid2 + "'"), null).intValue();
                                    if (count10 > 0) {
                                    }
                                    if ("null".equals(jie2)) {
                                        jie2 = null;
                                    }
                                }
                                String insertUserPositionSql4 = "INSERT INTO userposition(userNum,type,insertDate,description,schoolnum,gradenum,subjectNum,classNum,insertUser,jie,stage,permission_class,permission_grade,permission_subject) VALUES('" + userid2 + "','" + postNum2 + "','" + date + "','" + desc4 + "','" + school2 + "'," + gradeNum2 + Const.STRING_SEPERATOR + subjectNum2 + Const.STRING_SEPERATOR + classNum4 + ",'" + user + "'," + jie2 + ",'" + stage2 + "','" + perClass2 + "','" + perGrade2 + "','" + perSubject2 + "') ON DUPLICATE KEY UPDATE  userNum='" + userid2 + "',type='" + postNum2 + "',schoolNum='" + school2 + "',gradenum=" + gradeNum2 + ",subjectNum=" + subjectNum2 + ",classnum=" + classNum4 + ",jie=" + jie2 + ",stage='" + stage2 + "',permission_class='" + perClass2 + "',permission_grade='" + perGrade2 + "',permission_subject='" + perSubject2 + "'";
                                sqls.add(insertUserPositionSql4);
                            }
                        }
                    }
                    Boolean delFlag2 = true;
                    int i12 = 0;
                    while (true) {
                        if (i12 >= teaList.size()) {
                            break;
                        }
                        if (!Tnum2.equals(teaList.get(i12))) {
                            i12++;
                        } else {
                            delFlag2 = false;
                            break;
                        }
                    }
                    teaList.add(Tnum2);
                    if (delFlag2.booleanValue()) {
                        String deleteUserPositionSql2 = "delete from userposition where userNum = " + userid2;
                        this.dao2.execute(deleteUserPositionSql2);
                    }
                    String sql4 = "insert into teacher  (id,teacherNum,teacherName,sex,title" + birthdayStr22 + worktimeStr22 + ",insertUser,insertDate,updateUser,updateDate,schoolNum,mobile,email,isDelete) VALUES ('" + tid2 + "','" + Tnum2 + "','" + teacherName2 + "','" + sex2 + "','" + title2 + "'" + birthdayStr4 + worktimeStr4 + ",'" + user + "','" + date + "','" + user + "','" + date + "','" + school2 + "','" + mobil2 + "','" + email2 + "','F') on DUPLICATE KEY UPDATE  teacherNum='" + Tnum2 + "',teacherName='" + teacherName2 + "',sex='" + sex2 + "',title='" + title2 + "',schoolNum='" + school2 + "',mobile='" + mobil2 + "',email='" + email2 + "'" + birthdayStr32 + worktimeStr32 + ",isDelete='F'";
                    sqls.add(sql4);
                    String pas12 = "";
                    String pas22 = "";
                    HSSFCell password12 = hssfrow.getCell(9);
                    String pw12 = String.valueOf(password12).trim();
                    String password2 = Const.USER_PASSWORD;
                    if (null != pw12 && !"".equals(pw12) && !"null".equals(pw12)) {
                        pas12 = "md5(";
                        pas22 = ")";
                        if (password12.getCellType() == CellType.NUMERIC) {
                            password2 = this.df.format(password12.getNumericCellValue());
                        } else if (password12.getCellType() == CellType.STRING) {
                            password2 = String.valueOf(password12).replace(" ", "");
                        }
                    }
                    if (null == pw12 || "".equals(pw12) || "null".equals(pw12)) {
                        String getupasSql2 = "select /* shard_host_HG=Write */ password from user where id = " + userid2;
                        Object getupas2 = this.dao2.queryObject(getupasSql2);
                        if (null != String.valueOf(getupas2) && !"".equals(String.valueOf(getupas2)) && !"null".equals(String.valueOf(getupas2))) {
                            password2 = String.valueOf(getupas2);
                        }
                    }
                    String userSql2 = "insert into user (id,userid,isDelete,username,password,usertype,realname,insertUser,insertDate,schoolnum,isUser) values ( '" + userid2 + "', '" + tid2 + "', 'F', '" + Tnum2 + "'," + pas12 + " '" + password2.toLowerCase() + "'" + pas22 + ", '1', '" + teacherName2 + "', '" + user + "', '" + date + "', '" + school2 + "', 'T') ON DUPLICATE KEY UPDATE  userid='" + tid2 + "', username='" + Tnum2 + "', password=" + pas12 + "'" + password2.toLowerCase() + "'" + pas22 + ", usertype='1', realname='" + teacherName2 + "', schoolnum='" + school2 + "', isDelete='F'";
                    sqls.add(userSql2);
                    String userroleSql2 = "INSERT INTO userrole(userNum,roleNum,insertUser,insertDate,isDelete)  VALUES('" + userid2 + "','1','" + user + "','" + date + "','F')  ON DUPLICATE KEY UPDATE userNum='" + userid2 + "',roleNum='1'";
                    sqls.add(userroleSql2);
                    this.dao2.batchExecute(sqls);
                    sqls.clear();
                }
            }
        }
        try {
            input.close();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    public String getCellValue(XSSFCell cell, int s, int y, int sheet) {
        if (cell == null) {
            throw new RuntimeException("第" + sheet + "个sheet 第 " + y + " 行 " + s + " 列单元格为空！");
        }
        String c = cell.toString();
        if (c.trim().equals("")) {
            throw new RuntimeException("第" + sheet + "个sheet 第 " + (cell.getRowIndex() + 1) + " 行 " + (cell.getColumnIndex() + 1) + " 列单元格为空！");
        }
        String returnv = "";
        if (cell.getCellType() == CellType.NUMERIC) {
            returnv = this.df.format(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            returnv = cell.getStringCellValue().trim();
        }
        return returnv;
    }

    public String getCellValue(HSSFCell cell, int s, int y, int sheet) {
        if (cell == null) {
            throw new RuntimeException("第" + sheet + "个sheet 第 " + y + " 行 " + s + " 列单元格为空！");
        }
        String c = cell.toString();
        if (c.trim().equals("")) {
            throw new RuntimeException("第" + sheet + "个sheet 第 " + (cell.getRowIndex() + 1) + " 行 " + (cell.getColumnIndex() + 1) + " 列单元格为空！");
        }
        String returnv = "";
        if (cell.getCellType() == CellType.NUMERIC) {
            returnv = this.df.format(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            returnv = cell.getStringCellValue().trim();
        }
        return returnv;
    }

    private void throwException(String e) {
        throw new SubException(e);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getSubNum(String userNum, String school) {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put(License.SCHOOL, (Object) school);
        return this.dao2._queryStr("SELECT  subjectNum FROM userposition WHERE  type='1' AND userNum={userNum}  AND schoolnum={school}   AND subjectNum !='999'  ORDER BY subjectNum*1  LIMIT 0,1", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> getcNumList(String TNum, String school, String subject) {
        Map args = StreamMap.create().put(License.SCHOOL, (Object) school).put("subject", (Object) subject).put("TNum", (Object) TNum);
        return this.dao2._queryBeanList("SELECT classNum,gradeNum FROM userposition WHERE type='1'   AND schoolnum={school} AND subjectNum={subject}  AND userNum={TNum} ", Userposition.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Integer isExist(String type, String schoolNum, String gradeNum, String subjectNum, String classNum) {
        Map args = StreamMap.create().put("type", (Object) type).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        return this.dao2._queryInt("SELECT count(id) FROM userposition WHERE  type={type}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum} AND subjectNum={subjectNum}  AND classNum={classNum}  ", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List isExistRecord(String Tnum, String type, String schoolNum, String gradeNum, String subjectNum, String classNum) {
        String sql = null == classNum ? "SELECT t.teacherNum ext1,t.teacherName,up.description,g.gradeName,sub.subjectName FROM userposition up LEFT JOIN user u ON u.id = up.userNum LEFT JOIN teacher t ON t.id = u.userid LEFT JOIN grade g ON g.gradeNum = up.gradeNum AND g.schoolNum = up.schoolnum LEFT JOIN subject sub ON sub.subjectNum = up.subjectNum WHERE up.userNum != {Tnum} AND up.type={type}  AND up.schoolnum={schoolNum} AND up.gradeNum={gradeNum} AND up.subjectNum={subjectNum} AND up.classNum is null" : "SELECT t.teacherNum ext1,t.teacherName,up.description,g.gradeName,sub.subjectName FROM userposition up LEFT JOIN user u ON u.id = up.userNum LEFT JOIN teacher t ON t.id = u.userid LEFT JOIN grade g ON g.gradeNum = up.gradeNum AND g.schoolNum = up.schoolnum LEFT JOIN subject sub ON sub.subjectNum = up.subjectNum WHERE up.userNum != {Tnum} AND up.type={type}  AND up.schoolnum={schoolNum} AND up.gradeNum={gradeNum} AND up.subjectNum={subjectNum} AND up.classNum = {classNum} ";
        Map args = StreamMap.create().put("Tnum", (Object) Tnum).put("type", (Object) type).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        return this.dao2._queryBeanList(sql, Userposition.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Integer isExistT(String type, String gradeNum, String classNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("type", (Object) type);
        return this.dao2._queryInt("SELECT count(id) FROM userposition WHERE schoolNum={schoolNum}  AND gradeNum={gradeNum}  AND classNum={classNum} AND type={type} AND subjectNum='999'", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Integer isExistG(String type, String gradeNum, String schoolNum) {
        Map args = StreamMap.create().put("type", (Object) type).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryInt("SELECT count(id) FROM userposition WHERE type={type} AND  schoolnum={schoolNum} AND gradeNum={gradeNum} ", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Integer isExistStudy(String type, String gradeNum, String schoolNum, String subjectNum) {
        Map args = StreamMap.create().put("type", (Object) type).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryInt("SELECT count(id) FROM userposition WHERE type={type}  AND schoolnum={schoolNum}  AND gradeNum={gradeNum} AND subjectNum={subjectNum} ", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Integer isExistSch(String type, String schoolNum, String stage) {
        Map args = StreamMap.create().put("type", (Object) type).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("stage", (Object) stage);
        return this.dao2._queryInt("SELECT count(id) FROM userposition WHERE type={type} AND schoolnum={schoolNum}  AND stage={stage} ", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getTname(String userNum, String school) {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put(License.SCHOOL, (Object) school);
        return this.dao2._queryStr("SELECT realname FROM user WHERE id={userNum} AND schoolNum={school} ", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Integer tcount(String schoolNum, String searchNum, String searchName, String searchsex, String searchtitle, String subjectName, String userId, String gradeNum, String leicengId, String positionName, String phone) {
        Map args1 = StreamMap.create().put("userNum", (Object) userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args1);
        String schStr = "";
        String subStr2 = "";
        String graStr = "";
        String limitstr1 = "";
        String limitstr2 = "";
        if (!"-1".equals(schoolNum)) {
            schStr = "t.schoolNum={schoolNum}  and ";
        } else if (null != leicengId && !"".equals(leicengId)) {
            limitstr1 = "LEFT JOIN school  sch ON sch.id  = t.schoolNum left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leicengId}  group by sItemId) ss on ss.id = sch.id ";
            limitstr2 = " sch.isDelete='F' and ss.id is not null and ";
        } else if (!userId.equals("-1") && !userId.equals("-2") && null == map) {
            limitstr1 = "LEFT JOIN school  sch ON sch.id  = t.schoolNum  left join schoolscanpermission sa on sa.schoolNum = sch.id and sa.userNum={userId}   left join user te on te.schoolNum = sch.id  and te.id = {userId}  ";
            limitstr2 = " sch.isDelete='F' and (sa.schoolNum is not null or te.schoolNum is not null) and ";
        }
        if (null != subjectName && !"".equals(subjectName) && !subjectName.equals("-1")) {
            subStr2 = "  u.subjectNum={subjectName} and ";
        }
        if (null != gradeNum && !"".equals(gradeNum) && !gradeNum.equals("-1")) {
            graStr = "  u.gradeNum={gradeNum}and ";
        }
        String poiStr = "";
        if (null != positionName && !"".equals(positionName) && !positionName.equals("-1")) {
            poiStr = "  u.type={positionName}  and ";
        }
        String sql = "select count(DISTINCT t.id )  from teacher t  LEFT JOIN user us ON t.id=us.userid  LEFT JOIN userposition u ON us.id = u.userNum  " + limitstr1 + " where " + poiStr + schStr + graStr + subStr2 + limitstr2 + " t.isDelete='F' and us.usertype = '1' ";
        String sql2 = ((((sql + ("".equals(searchNum) ? "" : " and t.teacherNum LIKE {searchNum}  ")) + ("".equals(searchName) ? "" : " and t.teacherName LIKE {searchName}  ")) + ("".equals(phone) ? "" : " and us.mobile LIKE {phone}  ")) + ("".equals(searchsex) ? "" : " and t.sex LIKE {searchsex}  ")) + ("".equals(searchtitle) ? "" : " and t.title LIKE {searchtitle} ");
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("leicengId", (Object) leicengId).put("userId", (Object) userId).put("subjectName", (Object) subjectName).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("positionName", (Object) positionName).put("searchNum", (Object) ("%" + searchNum + "%")).put("searchName", (Object) ("%" + searchName + "%")).put("phone", (Object) ("%" + phone + "%")).put("searchsex", (Object) ("%" + searchsex + "%")).put("searchtitle", (Object) ("%" + searchtitle + "%"));
        return this.dao2._queryInt(sql2, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Teacher> listTeacher_out(String schoolNum) {
        String sql = "select t.id,t.teacherNum,t.teacherName,t.sex,t.title,t.mobile,t.email,t.birthday,s.schoolName,t.worktime,ROUND(((UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(worktime))/(360*24*60*60)),1) ext1 from teacher t LEFT JOIN school s ON s.id=t.schoolNum where  t.isDelete='F'";
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql = sql + " and t.schoolNum={schoolNum}  ";
        }
        String sql2 = sql + " ORDER BY teacherNum";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList(sql2, Teacher.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Teacher> listLimitTeacher_out(String schoolNum, String userId) {
        String sql = "select u.id,t.teacherNum,t.teacherName,t.sex,t.title,t.mobile,t.email,t.birthday,s.schoolName,t.worktime,ROUND(((UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(worktime))/(360*24*60*60)),1) ext1,u.password ext2 from teacher t LEFT JOIN user u on u.userid = t.id LEFT JOIN school s ON s.id=t.schoolNum where  t.isDelete='F' and t.schoolNum <> '0' ";
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql = sql + " and t.schoolNum={schoolNum}  ";
        }
        String sql2 = sql + " ORDER BY teacherNum";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList(sql2, Teacher.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Teacher> schoolLimitList(String schoolNum, String userId, String leicengId) {
        String sql;
        String lcSql = "";
        String lcWSql = "";
        if ("-1".equals(schoolNum) && null != leicengId && !"".equals(leicengId)) {
            lcSql = "left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leicengId}  group by sItemId) ss on ss.id = t.schoolNum ";
            lcWSql = " and ss.id is not null ";
        }
        if (userId.equals("-1") || userId.equals("-2")) {
            sql = "SELECT DISTINCT s.id,t.schoolNum,s.schoolName from teacher t LEFT JOIN school s ON s.id=t.schoolNum and s.isDelete='F' " + lcSql + "WHERE t.isDelete='F' and t.schoolNum <> '0' and s.id is not null " + lcWSql;
        } else {
            sql = "SELECT DISTINCT s.id,t.schoolNum,s.schoolName from teacher t LEFT JOIN school s ON s.id=t.schoolNum and s.isDelete='F'  left join schauthormanage sc on sc.schoolNum = s.id and sc.userId={userId}  " + lcSql + " left join user u on u.schoolNum = s.id  and u.id ={userId} and u.usertype=1  where  t.isDelete='F' and t.schoolNum <> '0' and s.id is not null and (sc.schoolNum is not null or u.schoolNum is not null)  " + lcWSql;
        }
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql = sql + " and t.schoolNum={schoolNum} ";
        }
        Map args = StreamMap.create().put("leicengId", (Object) leicengId).put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList(sql + " ORDER BY t.schoolNum ASC", Teacher.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Teacher> schoolList(String schoolNum) {
        String sql = "SELECT DISTINCT s.id,t.schoolNum,s.schoolName from teacher t LEFT JOIN school s ON s.id=t.schoolNum and s.isDelete='F' WHERE t.isDelete='F' ";
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql = sql + " and t.schoolNum={schoolNum}  ";
        }
        String sql2 = sql + " ORDER BY t.schoolNum ASC";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList(sql2, Teacher.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void delAndaddT(String tNum, String dir, String gradeNum, String schoolNum, String classNum, String user, String date) {
        int jie = getjie(schoolNum, gradeNum);
        Map args = StreamMap.create().put("dir", (Object) dir).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) Integer.valueOf(jie));
        this.dao2._execute("DELETE FROM userposition WHERE type={dir} AND schoolNum={schoolNum}  AND gradeNum={gradeNum}  AND classNum={classNum}  AND subjectNum='999' AND jie={jie} ", args);
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("classNum+", (Object) classNum);
        String className = this.dao2._queryStr("SELECT className FROM class WHERE schoolNum={schoolNum}  AND gradeNum={gradeNum}  AND isDelete='F' AND id={classNum}  ", args2);
        String stage = getStage(gradeNum);
        Map args3 = StreamMap.create().put("tNum", (Object) tNum).put("dir", (Object) dir).put("description", (Object) (className + "班主任")).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("user", (Object) user).put("jie", (Object) Integer.valueOf(jie)).put("date", (Object) date).put("stage", (Object) stage);
        this.dao2._execute("INSERT INTO userposition(userNum,type,description,schoolNum,gradeNum,classNum,insertUser,jie,insertDate,stage,subjectNum) VALUES({tNum},{dir},{description},{schoolNum},{gradeNum},{classNum},{user},{jie},{date},{stage} ,'999')", args3);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void delAndAddGrade(String tNum, String dir, String gradeNum, String schoolNum, String user, String date) {
        int jie = getjie(schoolNum, gradeNum);
        Map args = StreamMap.create().put("dir", (Object) dir).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) Integer.valueOf(jie));
        this.dao2._execute("DELETE FROM userposition WHERE type={dir} AND schoolNum={schoolNum} AND gradeNum={gradeNum}  AND subjectNum='999'  AND (classNum IS NULL OR classNum='') AND jie={jie} ", args);
        String gradeName = getGradeName(gradeNum, schoolNum);
        String stage = getStage(gradeNum);
        Map args2 = StreamMap.create().put("tNum", (Object) tNum).put("dir", (Object) dir).put("date", (Object) date).put("description", (Object) (gradeName + "年级主任")).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("user", (Object) user).put("jie", (Object) Integer.valueOf(jie)).put("stage", (Object) stage);
        this.dao2._execute("INSERT INTO userposition(userNum,type,insertDate,description,schoolNum,gradeNum,insertUser,jie,stage,subjectNum)VALUES({tNum},{dir},{date},{description},{schoolNum},{gradeNum},{user},{jie},{stage},'999')", args2);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void delAndAddStudy(String tNum, String dir, String gradeNum, String schoolNum, String user, String date, String subjectNum) {
        int jie = getjie(schoolNum, gradeNum);
        Map args = StreamMap.create().put("dir", (Object) dir).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("jie", (Object) Integer.valueOf(jie));
        this.dao2._execute("DELETE FROM userposition WHERE type={dir} AND schoolNum={schoolNum} AND gradeNum={gradeNum}  AND subjectNum={subjectNum}  AND (classNum IS NULL OR classNum='') AND jie={jie} ", args);
        String gradeName = getGradeName(gradeNum, schoolNum);
        String stage = getStage(gradeNum);
        Map args2 = StreamMap.create().put("tNum", (Object) tNum).put("dir", (Object) dir).put("date", (Object) date).put("description", (Object) (gradeName + "教研主任")).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("user", (Object) user).put("jie", (Object) Integer.valueOf(jie)).put("stage", (Object) stage);
        this.dao2._execute("INSERT INTO userposition(userNum,type,insertDate,description,schoolNum,gradeNum,subjectNum,insertUser,jie,stage) VALUES({tNum},{dir},{date},{description},{schoolNum},{gradeNum},{subjectNum},{user},{jie},{stage})", args2);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void delAndAddSch(String tNum, String dir, String schoolNum, String user, String date, String stage) {
        Map args = StreamMap.create().put("dir", (Object) dir).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("stage", (Object) stage);
        this.dao2._execute("DELETE FROM userposition WHERE type={dir}  AND schoolNum={schoolNum}  AND stage={stage}  ", args);
        String stageName = "";
        if (stage.equals("51")) {
            stageName = "小学";
        } else if (stage.equals("52")) {
            stageName = "初中";
        } else if (stage.equals("53")) {
            stageName = "高中";
        }
        String finalStageName = stageName;
        Map args2 = StreamMap.create().put("tNum", (Object) tNum).put("dir", (Object) dir).put("date", (Object) date).put("description", (Object) (finalStageName + "校长")).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("stage", (Object) stage).put("user", (Object) user);
        this.dao2._execute("INSERT INTO userposition(userNum,type,insertDate,description,schoolNum,stage,insertUser)VALUES({tNum},{dir},{date},{description},{schoolNum},{stage},{user})", args2);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void delAndAddClass(String tNum, String schoolNum, String gradeNum, String classNum, String subjectNum, String date, String user) {
        int jie = getjie(schoolNum, gradeNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) Integer.valueOf(jie));
        this.dao2._execute("DELETE FROM userposition WHERE type='1' AND schoolNum={schoolNum}  AND gradeNum={gradeNum}  AND subjectNum={subjectNum}  AND classNum={classNum} AND jie={jie} ", args);
        String className = getCName(classNum, schoolNum, gradeNum, null);
        String subjectName = getSubName(subjectNum);
        String stage = getStage(gradeNum);
        Map args2 = StreamMap.create().put("tNum", (Object) tNum).put("date", (Object) date).put("description", (Object) (className + subjectName + "老师")).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("user", (Object) user).put("jie", (Object) Integer.valueOf(jie)).put("stage", (Object) stage);
        this.dao2._execute("INSERT INTO userposition(userNum,type,insertDate,description,schoolNum,gradeNum,subjectNum,classNum,insertUser,jie,stage) VALUES({tNum},'1',{date},{description} ,{schoolNum} ,{gradeNum} ,{subjectNum} ,{classNum} ,{user},{jie},{stage})", args2);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> teacher_List(String userNum, String schoolNum, String gradeNum, String subject, String classNum) {
        String sql = "SELECT DISTINCT us.type,us.gradeNum,us.subjectNum,sub.subjectName,gr.gradeName FROM teacher t LEFT JOIN\t`user` u ON t.id = u.userid AND  t.isDelete='F' LEFT JOIN  userposition us  ON u.id = us.userNum LEFT JOIN grade gr ON us.gradeNum = gr.gradeNum LEFT JOIN `subject` sub ON sub.subjectNum = us.subjectNum LEFT JOIN class c ON c.id = us.classNum WHERE 1=1";
        if (null != userNum && !userNum.equals("-1") && !userNum.equals("") && !userNum.equals("null")) {
            sql = sql + " and t.id={userNum} ";
        }
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("") && !schoolNum.equals("null")) {
            sql = sql + " and us.schoolnum={schoolNum} ";
        }
        return this.dao2._queryBeanList(sql + "AND us.type={type} AND sub.subjectNum is NOT NULL", Userposition.class, StreamMap.create().put("userNum", (Object) userNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("type", (Object) "1"));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> teacher_newList(String userNum, String schoolNum, String isFc) {
        return this.dao2._queryBeanList("SELECT rr.type,rr.subjectNum,rr.gradeNum,rr.classNum,rr.ext1,rr.subjectName,rr.gradeName,if(rr.islevel='1',rr.levelclassName,rr.className) className,rr.permissionClass,rr.permissionGrade,rr.permissionSubject FROM (SELECT r.teacherNum,r.subjectName,r.gradeName,r.type,GROUP_CONCAT(r.className) className,r.subjectNum,GROUP_CONCAT(r.levelclassName) levelclassName,r.gradeNum,r.classNum,r.ext1,MAX(r.permissionClass) permissionClass,MAX(r.permissionGrade) permissionGrade,MAX(r.permissionSubject) permissionSubject,r.islevel FROM (SELECT e.teacherNum,e.gradeNum,e.subjectNum,e.classNum,e.type,e.gradeName,e.subjectName,e.className,e.levelclassName,e.ext1,e.permissionClass,e.permissionGrade,e.permissionSubject,e.islevel FROM (SELECT t.teacherNum,u.gradeNum,u.subjectNum,u.classNum,u.type,g.gradeName,s.subjectName,c.className,cl.className levelclassName,d.name ext1,u.permission_class permissionClass,u.permission_grade permissionGrade,u.permission_subject permissionSubject,g.islevel FROM teacher t LEFT JOIN (SELECT id,isDelete,userid FROM user WHERE usertype = '1') us ON t.id = us.userid AND t.isDelete = 'F' LEFT JOIN userposition u ON us.id = u.userNum LEFT JOIN grade g ON u.gradeNum = g.gradeNum AND g.schoolNum = u.schoolNum AND g.isDelete = 'F' LEFT JOIN `subject` s ON s.subjectNum = u.subjectNum LEFT JOIN class c ON c.id = u.classNum AND c.isDelete = 'F' LEFT JOIN levelclass cl ON cl.id = u.classNum AND cl.isDelete = 'F' LEFT JOIN DATA d ON d.value = u.type AND d.type = 31 WHERE u.userNum = {userNum} and u.schoolnum={schoolNum} ) e ) r GROUP BY r.type,r.subjectNum,r.gradeNum ) rr", Userposition.class, StreamMap.create().put("userNum", (Object) userNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> teacher_List_al(String userNum, String schoolNum, String gradeNum, String subject, String classNum) {
        String sql = "SELECT us.type,us.gradeNum,us.subjectNum,sub.subjectName,gr.gradeName FROM teacher t LEFT JOIN `user` u ON t.id=u.userid AND t.isDelete='F' LEFT JOIN userposition us ON u.id=us.userNum LEFT JOIN grade gr ON gr.schoolNum=us.schoolnum AND gr.gradeNum=us.gradeNum LEFT JOIN `subject` sub ON sub.subjectNum=us.subjectNum  WHERE 1=1 ";
        if (null != userNum && !userNum.equals("-1") && !userNum.equals("")) {
            sql = sql + " and t.id={userNum} ";
        }
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql = sql + " and us.schoolnum={schoolNum} ";
        }
        return this.dao2._queryBeanList(sql, Userposition.class, StreamMap.create().put("userNum", (Object) userNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> teacher_class_List(String userNum, String schoolNum, String gradeNum, String subject, String level) {
        String sql = (level.equals("T") ? "SELECT DISTINCT us.schoolnum, us.classNum,cla.className FROM teacher t LEFT JOIN `user` u ON t.id=u.userid AND  t.isDelete='F' LEFT JOIN userposition us ON u.id=us.userNum LEFT JOIN grade gr ON gr.schoolNum=us.schoolnum AND gr.gradeNum=us.gradeNum  LEFT JOIN `subject` sub ON sub.subjectNum=us.subjectNum LEFT JOIN levelclass cla ON cla.id=us.classNum " : "SELECT DISTINCT us.schoolnum, us.classNum,cla.className FROM teacher t LEFT JOIN `user` u ON t.id=u.userid AND  t.isDelete='F' LEFT JOIN userposition us ON u.id=us.userNum LEFT JOIN grade gr ON gr.schoolNum=us.schoolnum AND gr.gradeNum=us.gradeNum  LEFT JOIN `subject` sub ON sub.subjectNum=us.subjectNum LEFT JOIN class cla ON cla.id=us.classNum ") + "WHERE 1=1 ";
        if (null != userNum && !userNum.equals("-1") && !userNum.equals("")) {
            sql = sql + " AND t.id={userNum} ";
        }
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql = sql + " AND us.schoolnum={schoolnum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql = sql + " AND us.gradeNum={gradeNum} ";
        }
        if (null != subject && !subject.equals("-1") && !subject.equals("")) {
            sql = sql + " AND us.subjectNum={subject} ";
        }
        return this.dao2._queryBeanList(sql + " AND us.classNum is not null ", Userposition.class, StreamMap.create().put("userNum", (Object) userNum).put("schoolnum", (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("subject", (Object) subject));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> teacher_class(String userNum, String schoolNum, String gradeNum, String type) {
        String sql = "SELECT us.classNum,cla.className,gr.gradeNum gradeNum,gr.gradeName FROM teacher t LEFT JOIN `user` u ON t.id=u.userid AND t.isDelete='F' LEFT JOIN userposition us ON u.id=us.userNum LEFT JOIN grade gr ON gr.schoolNum=t.schoolnum AND gr.gradeNum=us.gradeNum LEFT JOIN `subject` sub ON sub.subjectNum=us.subjectNum LEFT JOIN class cla ON cla.id=us.classNum  WHERE 1=1 ";
        if (null != userNum && !userNum.equals("-1") && !userNum.equals("")) {
            sql = sql + " AND t.id={userNum} ";
        }
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql = sql + " AND us.schoolnum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql = sql + " AND us.gradeNum={gradeNum} ";
        }
        if (null != type && !type.equals("-1") && !type.equals("")) {
            sql = sql + " AND us.type={type} ";
        }
        return this.dao2._queryBeanList(sql, Userposition.class, StreamMap.create().put("userNum", (Object) userNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("type", (Object) type));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getStage(String gradeNum) {
        String stage = "";
        if (gradeNum.equals("1") || gradeNum.equals("2") || gradeNum.equals("3") || gradeNum.equals("4") || gradeNum.equals("5") || gradeNum.equals("6")) {
            stage = "51";
        } else if (gradeNum.equals("7") || gradeNum.equals("8") || gradeNum.equals("9")) {
            stage = "52";
        } else if (gradeNum.equals("10") || gradeNum.equals("11") || gradeNum.equals("12")) {
            stage = "53";
        }
        return stage;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Teacher getclassLeader(String teacherNum, int schoolNum) {
        return (Teacher) this.dao2._queryBean("SELECT cl.className ext3 ,gr.gradeName ext2 FROM userposition u LEFT JOIN grade gr ON gr.schoolNum=u.schoolNum and  gr.gradeNum = u.gradeNum  and gr.jie = u.jie LEFT JOIN class cl ON  cl.id=u.classNum AND cl.isDelete='F'  WHERE u.userNum={teacherNum} AND u.schoolNum={schoolNum} and u.type='2'", Teacher.class, StreamMap.create().put("teacherNum", (Object) teacherNum).put(Const.EXPORTREPORT_schoolNum, (Object) Integer.valueOf(schoolNum)));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void delclassBySub(String tNum, String subjectNum, String school) {
        this.dao2._execute("DELETE FROM userposition WHERE   type='1'  AND schoolNum={school} AND subjectNum={subjectNum}  AND userNum={tNum} ", StreamMap.create().put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("tNum", (Object) tNum));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Map getschoolList() {
        return this.dao2._queryOrderMap("SELECT id,schoolName FROM school WHERE isDelete='F'", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteTBySch(String schoolNum) {
        this.qgi.deleteteacherimport(schoolNum);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteLimitTBySch(String schoolNum, String user) {
        String deleteteacher;
        String deleteuser;
        String sql3;
        String deleteuserrole;
        String updatetask;
        String groupuser;
        if (!schoolNum.equals("-1")) {
            deleteteacher = "DELETE FROM teacher WHERE schoolNum={schoolNum} ";
            deleteuser = "delete from user WHERE usertype='1' AND schoolNum={schoolNum}";
            sql3 = "DELETE FROM userposition WHERE schoolNum={schoolNum} ";
            deleteuserrole = "DELETE ur FROM  userrole ur left JOIN (select id,schoolNum FROM user where usertype='1') u ON u.id=ur.userNum  where u.id=ur.userNum  and u.schoolNum={schoolNum}";
            updatetask = "Update task t left JOIN (select id,schoolNum FROM user where usertype='1') u ON u.id=t.insertUser  set t.insertUser='-1',t.porder=0,t.fenfaDate=0   where t.status='F'  and u.schoolNum={schoolNum}";
            groupuser = "DELETE qu FROM  QuestionGroup_user qu left JOIN (select id,schoolNum FROM user where usertype='1') u ON u.id=qu.userNum  where u.id=qu.userNum  and u.schoolNum={schoolNum}";
        } else {
            deleteteacher = "DELETE th FROM teacher th left join school c on th.schoolNum = c.id left join schauthormanage s on s.schoolNum = c.id and s.userId={user}  left join user t on t.schoolNum = c.id  and t.id = {user} and t.usertype=1  where c.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null)  ";
            deleteuser = "delete th from user th left join school c on th.schoolNum = c.id left join schauthormanage s on s.schoolNum = c.id and s.userId={user}  left join user t on t.schoolNum = c.id  and t.id = {user} and t.usertype=1  where c.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null)  ";
            sql3 = "DELETE th FROM userposition th left join school c on th.schoolNum = c.id left join schauthormanage s on s.schoolNum = c.id and s.userId={user}  left join user t on t.schoolNum = c.id  and t.id = {user} and t.usertype=1  where c.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null)  ";
            deleteuserrole = "DELETE ur  from userrole ur  left JOIN   (  select us.id,us.schoolNum FROM user us  left join schauthormanage s on s.schoolNum = us.schoolnum and s.userId ={user}   left join user t on t.schoolnum = us.schoolnum  and t.id = {user}  and t.usertype=1  where us.usertype='1' and (s.schoolNum is not null or t.schoolNum is not null)  ) u ON u.id=ur.userNum   where u.id=ur.userNum ";
            updatetask = " Update task t  left JOIN   (  select us.id,us.schoolNum FROM user us  left join schauthormanage s on s.schoolNum = us.schoolnum and s.userId ={user}  left join user t on t.schoolnum = us.schoolnum  and t.id = {user}  and t.usertype=1  where us.usertype='1' and (s.schoolNum is not null or t.schoolNum is not null)   ) u ON u.id=t.insertUser  set t.insertUser='-1',t.porder=0,t.fenfaDate=0 where t.status='F' and u.id=t.insertUser ";
            groupuser = "DELETE qu FROM  QuestionGroup_user qu   left JOIN   (  select us.id,us.schoolNum FROM user us  left join schauthormanage s on s.schoolNum = us.schoolnum and s.userId ={user}  left join user t on t.schoolnum = us.schoolnum  and t.id = {user} and t.usertype=1  where us.usertype='1' and (s.schoolNum is not null or t.schoolNum is not null)   ) u ON u.id=qu.userNum   where u.id=qu.userNum  ";
        }
        this.dao2._execute(deleteuserrole, null);
        this.dao2._execute(groupuser, null);
        this.dao2._execute(updatetask, null);
        this.dao2._execute(deleteteacher, null);
        this.dao2._execute(deleteuser, null);
        this.dao2._execute(sql3, null);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getTid(String userNum, String school) {
        return this.dao2._queryStr("SELECT id FROM teacher WHERE teacherNum={userNum} AND schoolNum={school}", StreamMap.create().put("userNum", (Object) userNum).put(License.SCHOOL, (Object) school));
    }

    public String getschid(String school) {
        return this.dao2._queryStr("select id from school where schoolNum={school}", StreamMap.create().put(License.SCHOOL, (Object) school));
    }

    public int getjie(String schoolNum, String gradeNum) {
        return this.dao2._queryInt("SELECT jie FROM grade WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND isDelete='F'", StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum)).intValue();
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Class> getlevelclassBygrade(String subjectNum, int grade, String school) {
        String sql = "select cl.className,cl.id classNum,cl.gradeNum,cl.jie from levelclass cl LEFT JOIN grade gr ON cl.schoolNum=gr.schoolNum and  cl.gradeNum = gr.gradeNum and cl.jie=gr.jie   WHERE   cl.schoolNum='" + school + "' and cl.gradeNum={grade} AND cl.subjectNum={subjectNum}  AND cl.isDelete='F' AND gr.isDelete='F' ORDER BY  LENGTH(cl.classNum),cl.classNum";
        return this.dao2._queryBeanList(sql, Class.class, StreamMap.create().put("grade", (Object) Integer.valueOf(grade)).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public boolean isLevelClass(String schoolNum, String gradeNum) {
        if ("1".equals(this.dao2._queryStr("select islevel from grade where schoolNum = {schoolNum} and gradeNum = {gradeNum} and isDelete = 'F' limit 1", StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum)))) {
            return true;
        }
        return false;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Userposition> getlevelcNumList(String TNum, String school, String subject) {
        return this.dao2._queryBeanList("SELECT cl.id classNum,cl.className,cl.gradeNum,gr.gradeName,u.classNum ext1,u.gradeNum ext2 FROM levelclass cl LEFT JOIN userposition u ON cl.subjectNum=u.subjectNum AND u.type='1'  AND cl.schoolNum=u.schoolnum AND cl.id=u.classNum AND u.userNum={TNum} LEFT JOIN grade gr ON gr.schoolNum=cl.schoolnum AND gr.gradeNum=cl.gradeNum WHERE cl.schoolNum={school} AND cl.subjectNum={subject}  ORDER BY  gr.gradeNum*1,REPLACE(cl.classNum,'班','' )*1 ", Userposition.class, StreamMap.create().put("TNum", (Object) TNum).put(License.SCHOOL, (Object) school).put("subject", (Object) subject));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getlevelCName(String classNum) {
        return this.dao2._queryStr("SELECT className from levelclass where id={classNum}", StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List querySchoolList() {
        return this.dao2._queryBeanList("SELECT id,schoolName FROM school WHERE isDelete='F'", School.class, null);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Integer insertUserposition(Userposition up, int j, int i, String type, String isMultipleTeachers) {
        String delSql;
        String jie = up.getExt1();
        String stage = up.getStage();
        if ("-1".equals(type)) {
            if (i == -1) {
                String delSql2 = "DELETE FROM userposition WHERE type = {type}  AND schoolnum = {schoolnum} ";
                if (null != up.getGradeNum()) {
                    delSql2 = delSql2 + " AND gradeNum ={gradeNum} AND jie = {jie}";
                }
                if (null != up.getSubjectNum()) {
                    delSql2 = delSql2 + " AND subjectNum = {subjectNum}";
                }
                if (null != up.getClassNum()) {
                    delSql2 = delSql2 + " AND classNum = {classNum}";
                }
                Map args = StreamMap.create().put("type", (Object) up.getType()).put("schoolnum", (Object) up.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) up.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) up.getSubjectNum()).put("userNum", (Object) up.getUserNum()).put("jie", (Object) jie).put(Const.EXPORTREPORT_classNum, (Object) up.getClassNum());
                this.dao2._execute(delSql2 + " AND userNum = {userNum}", args);
            } else if (i != -1 && i != 0) {
                String delSql3 = "DELETE FROM userposition WHERE type = {type}  AND schoolnum = {schoolnum} AND gradeNum = {gradeNum}";
                if (null != up.getSubjectNum()) {
                    delSql3 = delSql3 + " AND subjectNum = {subjectNum}";
                }
                if (null != up.getClassNum()) {
                    delSql3 = delSql3 + " AND classNum = {classNum}";
                }
                String delSql4 = delSql3 + " AND jie = {jie}";
                if ("1".equals(isMultipleTeachers)) {
                    delSql4 = delSql4 + " AND userNum = {userNum}";
                }
                Map args2 = StreamMap.create().put("type", (Object) up.getType()).put(Const.EXPORTREPORT_gradeNum, (Object) up.getGradeNum()).put("schoolnum", (Object) up.getSchoolNum()).put(Const.EXPORTREPORT_subjectNum, (Object) up.getSubjectNum()).put(Const.EXPORTREPORT_classNum, (Object) up.getClassNum()).put("jie", (Object) jie).put("userNum", (Object) up.getUserNum());
                this.dao2._execute(delSql4, args2);
            }
        } else {
            if (!"1".equals(up.getType()) && !"2".equals(up.getType()) && !"3".equals(up.getType()) && !"4".equals(up.getType())) {
                delSql = "DELETE FROM userposition WHERE type = {type}  AND userNum = {userNum}";
            } else {
                jie = String.valueOf(getjie(up.getSchoolNum(), up.getGradeNum()));
                stage = getStage(up.getGradeNum());
                String delSql5 = "DELETE FROM userposition WHERE type = {type}  AND schoolnum = {schoolnum} AND gradeNum = {gradeNum} AND subjectNum = {subjectNum} ";
                if (null != up.getClassNum()) {
                    delSql5 = delSql5 + " AND classNum = {classNum}";
                }
                delSql = delSql5 + " AND jie ={jie}";
                if ("1".equals(isMultipleTeachers)) {
                    delSql = delSql + " AND userNum = {userNum}";
                }
            }
            Map args3 = StreamMap.create().put("type", (Object) up.getType()).put("schoolnum", (Object) up.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) up.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) up.getSubjectNum()).put(Const.EXPORTREPORT_classNum, (Object) up.getClassNum()).put("userNum", (Object) up.getUserNum()).put("jie", (Object) jie);
            this.dao2._execute(delSql, args3);
        }
        if ("0".equals(up.getType())) {
            addUserRole(up.getUserNum(), "-3", up.getInsertUser());
        }
        Object[] args4 = {up.getUserNum(), up.getType(), up.getDescription(), up.getSchoolNum(), up.getGradeNum(), up.getSubjectNum(), up.getClassNum(), jie, stage, up.getInsertUser(), DateUtil.getCurrentTime(), up.getPermissionClass(), up.getPermissionGrade(), up.getPermissionSubject()};
        return Integer.valueOf(this.dao2.execute("INSERT INTO userposition (userNum,type,description,schoolNum,gradeNum,subjectNum,classNum,jie,stage,insertUser,insertDate,permission_class,permission_grade,permission_subject)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)", args4));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getNameByTypeAndValue(String type, String value) {
        return this.dao2._queryStr("SELECT name FROM data WHERE type = {type} AND value = {value}", StreamMap.create().put("type", (Object) type).put("value", (Object) value));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Integer delTeaPos(String Tnum, String type, String school, String subjectNum, String gradeNum) {
        String sql;
        String sql2;
        Map args = StreamMap.create().put("Tnum", (Object) Tnum).put("type", (Object) type).put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        if ("0".equals(type)) {
            this.dao2._execute("delete from userrole where userNum = {Tnum} and roleNum = '-3'", args);
        }
        if (!"1".equals(type) && !"2".equals(type) && !"3".equals(type) && !"4".equals(type) && !"5".equals(type)) {
            String sql3 = "".equals(gradeNum) ? "DELETE FROM userposition WHERE type = {type} AND schoolnum = {school} AND gradeNum is null " : "DELETE FROM userposition WHERE type = {type} AND schoolnum = {school} AND gradeNum = {gradeNum} ";
            if ("".equals(subjectNum)) {
                sql2 = sql3 + " AND subjectNum is null ";
            } else {
                sql2 = sql3 + " AND subjectNum = {subjectNum}";
            }
            sql = sql2 + " AND userNum = {Tnum}";
        } else {
            String sql4 = "DELETE FROM userposition WHERE type = {type} AND schoolnum = {school}";
            if (!"5".equals(type)) {
                sql4 = sql4 + " AND subjectNum = {subjectNum} AND gradeNum = {gradeNum}";
            }
            sql = sql4 + " AND userNum = {Tnum}";
        }
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public boolean checkMobile(String username, String mobile, String id) {
        String userSql;
        String teacherSql;
        if (null == id) {
            userSql = " and username <> {username} ";
            teacherSql = " and teacherNum <> {username} ";
        } else {
            userSql = " and userid <> {id}";
            teacherSql = " and id <>  {id}";
        }
        String mobileSql = "(select id from user where mobile = {mobile} and usertype = '1' " + userSql + " limit 1) union all (select id from teacher where mobile = {mobile} " + teacherSql + " limit 1) ";
        List<Object> mobileIds = this.dao2._queryColList(mobileSql, StreamMap.create().put("username", (Object) username).put("id", (Object) id).put("mobile", (Object) mobile));
        if (mobileIds.size() > 0) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Map<String, Object> getTeacherSchoolNum(String Tnum) {
        List<Map<String, Object>> list = this.dao2._queryMapList("select u.id,u.userid,ifnull(u.schoolNum,'0')schoolNum,ifnull(s.schoolName,'')schoolName from user u left join school s on u.schoolNum = s.id  where (u.userName={Tnum} or u.loginname={Tnum}) and u.userType='1' and u.isDelete='F' ", TypeEnum.StringObject, StreamMap.create().put("Tnum", (Object) Tnum));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Map<String, Object>> getSchoolNumByUser(String user) {
        List<Map<String, Object>> list = this.dao2._queryMapList("select schoolNum from schauthormanage  where userId={user} union select schoolNum from user where id={user}", TypeEnum.StringObject, StreamMap.create().put("user", (Object) user));
        return list;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String changeSchool_teacher(Teacher teacher, String oldSchool) {
        Object obj;
        String flag = "T";
        try {
            obj = this.dao2._queryObject("select id from changeSchool_teacher where status='0' and teacherNum={teacherNum}", StreamMap.create().put("teacherNum", (Object) teacher.getTeacherNum()));
        } catch (Exception e) {
            e.printStackTrace();
            flag = "转校申请失败，请稍后重试！";
        }
        if (null != obj) {
            String birthday = "".equals(teacher.getBirthday()) ? null : teacher.getBirthday();
            String worktime = "".equals(teacher.getWorktime()) ? null : teacher.getWorktime();
            Object[] params = {teacher.getTeacherNum(), teacher.getTeacherName(), teacher.getSex(), teacher.getTitle(), birthday, worktime, teacher.getSchoolNum(), teacher.getMobile(), teacher.getEmail(), oldSchool, teacher.getInsertUser(), "0", teacher.getInsertUser(), teacher.getInsertDate(), obj.toString()};
            this.dao2._execute("update changeSchool_teacher set teacherNum=?,teacherName=?,sex=?,title=?,birthday=?,worktime=?,schoolNum=?,mobile=?,email=?,originalSchool=?,appealTeacher=?,status=?,updateUser=?,updateDate=? where id=?", params);
            return flag;
        }
        String cloumns = "(teacherNum,teacherName,sex,title,schoolNum,mobile,email,originalSchool,appealTeacher,status,insertUser,insertDate";
        String values = "values({teacherNum},{teacherName},{sex},{title},{schoolNum},{mobile},{email},{originalSchool},{appealTeacher},{status},{insertUser},{insertDate}";
        String birthday2 = teacher.getBirthday();
        if (birthday2 != null && !birthday2.equals("")) {
            cloumns = cloumns + ",birthday";
            values = values + ",{birthday}";
        }
        String workTime = teacher.getWorktime();
        if (workTime != null && !workTime.equals("")) {
            cloumns = cloumns + ",workTime";
            values = values + ",{workTime}";
        }
        String sql = "insert into changeSchool_teacher" + (cloumns + ")") + (values + ")");
        Map args = StreamMap.create().put("teacherNum", (Object) teacher.getTeacherNum()).put("teacherName", (Object) teacher.getTeacherName()).put("sex", (Object) teacher.getSex()).put("title", (Object) teacher.getTitle()).put(Const.EXPORTREPORT_schoolNum, (Object) teacher.getSchoolNum()).put("mobile", (Object) teacher.getMobile()).put("email", (Object) teacher.getEmail()).put("originalSchool", (Object) oldSchool).put("appealTeacher", (Object) teacher.getInsertUser()).put(Const.CORRECT_SCORECORRECT, (Object) 0).put("insertUser", (Object) teacher.getInsertUser()).put("insertDate", (Object) teacher.getInsertDate()).put("birthday", (Object) birthday2).put("workTime", (Object) workTime);
        int i = this.dao2._execute(sql, args);
        if (i < 1) {
            flag = "转校申请失败，请稍后重试！";
        }
        return flag;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String cancelUserPosition(String id) {
        this.dao2._execute("delete from userposition where userNum={id}", StreamMap.create().put("id", (Object) id));
        return "T";
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void addNotice(String content, String type, String userType, String insertUser) {
        Map args = StreamMap.create().put("type", (Object) type).put("content", (Object) content).put("userType", (Object) userType).put("insertUser", (Object) insertUser);
        if (type.equals("3")) {
            Object id = this.dao2._queryObject("select id from note where type='1' limit 1 ", args);
            if (null != id) {
                this.dao2._execute("delete from note where type='1' ", args);
            }
            this.dao2._execute("insert into note(type,noteName,noteValue,insertUser,insertDate) values('1',{content},'0',{insertUser},now())", args);
            return;
        }
        String userTypeStr = "1".equals(type) ? "" : " and userType={userType} ";
        String exit_sql = "select id from notice where type={type} " + userTypeStr + " limit 1 ";
        Object id2 = this.dao2._queryObject(exit_sql, args);
        if (null != id2) {
            String delSql = "delete from notice where type={type} " + userTypeStr;
            this.dao2._execute(delSql, args);
        }
        this.dao2._execute("insert into notice(content,type,userType) values({content},{type},{userType})", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getNoticeInfo(String type, String userType) {
        String userType2 = ("2".equals(userType) || "3".equals(userType)) ? "3" : "1";
        Map args = new HashMap();
        args.put("userType", userType2);
        args.put("type", type);
        String userTypeStr = "1".equals(type) ? "" : " and userType={userType} ";
        String sql = "select ifnull(content,'') from notice where type={type} " + userTypeStr + " limit 1";
        String content = this.dao2._queryStr(sql, args);
        return StrUtil.isEmpty(content) ? "" : content;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getNoteInfo(String type) {
        Map args = new HashMap();
        args.put("type", type);
        String content = this.dao2._queryStr("select ifnull(noteName,'') from note where type={type} limit 1", args);
        return StrUtil.isEmpty(content) ? "" : content;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Grade> getGradeListBySchool(String schoolNum) {
        String sql;
        if ("-1".equals(schoolNum)) {
            sql = "select distinct gradeNum,gradeName from grade where isDelete='F' order by gradeNum*1";
        } else {
            sql = "select gradeNum,gradeName from grade where schoolNum={schoolNum} AND isDelete='F' order by gradeNum*1";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList(sql, Grade.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public boolean isSchoolManager(String userId) {
        Map args = StreamMap.create().put("userId", (Object) userId);
        Object _id = this.dao2._queryObject("select id from userposition where type = '0' and userNum = {userId} limit 1", args);
        if (null == _id) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<School> getAuthSchools(String userId) {
        String sql;
        Map args = StreamMap.create().put("userId", (Object) userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if ("-1".equals(userId) || "-2".equals(userId) || null != map) {
            sql = "select id,schoolNum,schoolName,shortname from school where isDelete = 'F' order by schoolNum*1";
        } else {
            sql = "select distinct sch.id,sch.schoolNum,sch.schoolName,sch.shortname from (select schoolNum from schoolscanpermission where userNum = {userId} union select schoolNum from user where id = {userId} and isDelete = 'F') sam left join school sch on sch.id = sam.schoolNum where sch.id is not null order by sch.schoolNum*1 ";
        }
        return this.dao2._queryBeanList(sql, School.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<String> getAuthSchoolIdList(String userId) {
        String sql;
        if ("-1".equals(userId) || "-2".equals(userId)) {
            sql = "select id from school where isDelete = 'F'";
        } else {
            sql = "select distinct sch.id from (select schoolNum from schauthormanage where userId = {userId} and isDelete = 'F' union select schoolNum from user where id = {userId} and isDelete = 'F') sam left join school sch on sch.id = sam.schoolNum where sch.id is not null";
        }
        Map args = StreamMap.create().put("userId", (Object) userId);
        return this.dao2._queryColList(sql, String.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<String> getschAuthSchoolIdList(String userId) {
        String sql;
        Map args = StreamMap.create().put("userId", (Object) userId);
        Map<String, Object> ismanageMap = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if ("-1".equals(userId) || "-2".equals(userId) || null != ismanageMap) {
            sql = "select id from school where isDelete = 'F'";
        } else {
            sql = "select distinct sch.id from (select schoolNum from schoolscanpermission where userNum = {userId} union select schoolNum from user where id = {userId} and isDelete = 'F') sam left join school sch on sch.id = sam.schoolNum where sch.id is not null";
        }
        return this.dao2._queryColList(sql, String.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteNotice(String type, String userType) {
        String userTypeStr = "1".equals(type) ? "" : " and userType={userType} ";
        String sql = "delete from notice where type={type} " + userTypeStr;
        Map args = StreamMap.create().put("type", (Object) type).put("userType", (Object) userType);
        this.dao2._execute(sql, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteNote() {
        this.dao2.execute("delete from note where type=1 ");
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Map<String, Object>> getBeixuanTeacher(String schoolNum, String userpostion, String userName) {
        String schoolNumStr = "";
        if (schoolNum != null && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            schoolNumStr = "and t.schoolNum={schoolNum} ";
        }
        String userpostionStr1 = "";
        String userpostionStr2 = "";
        if (userpostion != null && !userpostion.equals("")) {
            userpostionStr1 = "LEFT JOIN userposition up on u.id = up.userNum ";
            userpostionStr2 = "and up.type={userpostion} ";
        }
        String userNameStr = "";
        if (userName != null && !userName.equals("")) {
            userNameStr = "and t.teacherName like {userName} ";
        }
        String sql = "select DISTINCT t.id ,t.teacherName from teacher t LEFT JOIN user u on t.id = u.userid " + userpostionStr1 + "where u.userType='1' " + schoolNumStr + userpostionStr2 + userNameStr;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("userpostion", (Object) userpostion).put("userName", (Object) ("%" + userName + "%"));
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
        return list;
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Teacher> getTeacherList(String schoolNum, String gradeNum, String subjectNum, String teacherNum, String teacherName, String searchsex, String searchtitle, String levelClass, String userId, String leicengId, String position, String phone) {
        Map args = StreamMap.create().put("position", (Object) position).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("teacherNum", (Object) ("%" + teacherNum + "%")).put("teacherName", (Object) ("%" + teacherName + "%")).put("searchsex", (Object) ("%" + searchsex + "%")).put("searchtitle", (Object) ("%" + searchtitle + "%")).put("phone", (Object) ("%" + phone + "%")).put("leicengId", (Object) leicengId).put("userId", (Object) userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        String schStr2 = "";
        String graStr = "";
        String teaNumStr = "";
        String teaNameStr = "";
        String searchsexStr = "";
        String searchtitleStr = "";
        String phoneStr = "";
        String usFlag = "0";
        if (null != position && !"".equals(position) && !"-1".equals(position)) {
            graStr = " where type = {position} ";
            usFlag = "1";
        }
        if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            schStr2 = " and schoolNum={schoolNum}  ";
            if ("".equals(graStr)) {
                graStr = " where schoolnum = {schoolNum} ";
            } else {
                graStr = graStr + " and schoolnum={schoolNum} ";
            }
        }
        if (null != gradeNum && !"".equals(gradeNum) && !"-1".equals(gradeNum)) {
            if ("".equals(graStr)) {
                graStr = " where gradeNum={gradeNum} ";
            } else {
                graStr = graStr + " and gradeNum={gradeNum} ";
            }
            usFlag = "1";
        }
        if (null != subjectNum && !"".equals(subjectNum) && !"-1".equals(subjectNum)) {
            if ("".equals(graStr)) {
                graStr = " where subjectNum = {subjectNum} ";
            } else {
                graStr = graStr + " and subjectNum = {subjectNum} ";
            }
            usFlag = "1";
        }
        if (null != teacherNum && !"".equals(teacherNum)) {
            teaNumStr = " and teacherNum LIKE {teacherNum} ";
        }
        if (null != teacherName && !"".equals(teacherName)) {
            teaNameStr = " and teacherName LIKE {teacherName} ";
        }
        if (null != searchsex && !"".equals(searchsex)) {
            searchsexStr = " and sex LIKE {searchsex} ";
        }
        if (null != searchtitle && !"".equals(searchtitle)) {
            searchtitleStr = " aand title LIKE {searchtitle} ";
        }
        if (null != phone && !"".equals(phone)) {
            phoneStr = " and us.mobile like {phone} ";
        }
        String sql2 = "SELECT  e.teacherNum,e.id,e.userNum ext4,e.realname ext3  FROM ( SELECT t.teacherNum,t.id,us.id userNum,us.realName   FROM ( select teacherNum,id,schoolNum,isDelete  from teacher  where 1=1 " + schStr2 + teaNumStr + teaNameStr + searchsexStr + searchtitleStr + " and isDelete = 'F'  ) t  LEFT JOIN (SELECT id,userid,mobile,realName FROM `user` WHERE 1=1" + schStr2 + " and usertype='1' and isDelete = 'F')us ON t.id = us.userid " + ("1".equals(usFlag) ? " inner JOIN " : " LEFT JOIN ") + " (select userNum from userposition " + graStr + ") u ON us.id = u.userNum  ";
        if ("-1".equals(schoolNum)) {
            if (null != leicengId && !"".equals(leicengId)) {
                sql2 = sql2 + " left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leicengId} group by sItemId) ss on ss.id = t.schoolNum  where ss.id is not null ";
            } else if (!userId.equals("-1") && !userId.equals("-2") && null == map) {
                sql2 = sql2 + " inner join (select schoolNum FROM schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) sa on cast(sa.schoolNum as char) = cast(t.schoolNum as char)   where sa.schoolNum is not null   ";
            }
        } else {
            sql2 = sql2 + " where 1=1 ";
        }
        String sql22 = sql2 + phoneStr + " )e GROUP BY e.teacherNum  ORDER BY e.teacherNum*1 ";
        this.log.info("--教师管理查询符合条件的教师数据【getTeacherList】--" + sql22);
        return this.dao2._queryBeanList(sql22, Teacher.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public RspMsg checkTeacherFile(File file, String fileName, String userId, String currentTime, Map<String, String> jie_map, String isMultipleTeachers) {
        Row row;
        this.errorFlag = false;
        ExcelHelper excelHelper = new ExcelHelper(file);
        try {
            Workbook workbook = excelHelper.creatWorkbook();
            Sheet sheet = workbook.getSheetAt(0);
            Row row1 = sheet.getRow(1);
            int columnLen = row1.getPhysicalNumberOfCells();
            if (null != row1 && columnLen < 17) {
                return new RspMsg(410, "excel文件第二行的表头列数与导入模板不符合，请检查！", null);
            }
            CellStyle errorRowStyle = workbook.createCellStyle();
            errorRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            errorRowStyle.setFillForegroundColor((short) 10);
            CellStyle errorCellStyle = workbook.createCellStyle();
            errorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            errorCellStyle.setFillForegroundColor((short) 13);
            Set<String> teacherNumSet = new HashSet<>();
            Set<String> mobileSet = new HashSet<>();
            Set<String> postNum1Set = new HashSet<>();
            Set<String> postNum2Set = new HashSet<>();
            Set<String> postNum3Set = new HashSet<>();
            Set<String> postNum4Set = new HashSet<>();
            Set<String> postNum5Set = new HashSet<>();
            List<String> authSchoolList = getAuthSchoolIdList(userId);
            for (int i = 2; i < sheet.getPhysicalNumberOfRows() && null != (row = sheet.getRow(i)); i++) {
                this.rowBgColor = false;
                Cell teacherNumCell = row.getCell(0);
                String teacherNum = "";
                if (null == teacherNumCell) {
                    teacherNumCell = row.createCell(0);
                } else {
                    teacherNum = CheckCellUtil.getCellValue(teacherNumCell);
                }
                String specialCharStr_teacherNum = CheckCellUtil.isSpecialChar(teacherNum);
                if ("".equals(teacherNum) || "ERROR".equals(teacherNum)) {
                    setError(file, sheet, row, teacherNumCell, "教师ID号不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else if (null != specialCharStr_teacherNum || teacherNum.length() > 50) {
                    String pizhu = "教师ID号不能含有特殊字符（" + specialCharStr_teacherNum + "）且长度不超过50个字符";
                    setError(file, sheet, row, teacherNumCell, pizhu, columnLen, errorRowStyle, errorCellStyle);
                } else {
                    String finalTeacherNum = teacherNum;
                    Map args = StreamMap.create().put("teacherNum", (Object) finalTeacherNum);
                    School existSch = (School) this.dao2._queryBean("select t.schoolNum id,sch.schoolName from (select schoolNum from teacher where teacherNum = {teacherNum} ) t left join school sch on sch.id = t.schoolNum limit 1", School.class, args);
                    if (null != existSch && !"-1".equals(userId) && !"-2".equals(userId)) {
                        boolean ff = false;
                        int ii = 0;
                        while (true) {
                            if (ii >= authSchoolList.size()) {
                                break;
                            }
                            if (!String.valueOf(authSchoolList.get(ii)).equals(String.valueOf(existSch.getId()))) {
                                ii++;
                            } else {
                                ff = true;
                                break;
                            }
                        }
                        if (!ff) {
                            String pizhu2 = "系统中已经存在 " + existSch.getSchoolName() + " 这个学校的教师ID号 " + teacherNum + " ，您目前没有这个学校的操作权限。如果是转校教师，请到添加页面做转校申请！";
                            setError(file, sheet, row, teacherNumCell, pizhu2, columnLen, errorRowStyle, errorCellStyle);
                        }
                    }
                }
                Cell teacherNameCell = row.getCell(1);
                String teacherName = "";
                if (null == teacherNameCell) {
                    teacherNameCell = row.createCell(1);
                } else {
                    teacherName = CheckCellUtil.getCellValue(teacherNameCell);
                }
                String specialCharStr_teacherName = CheckCellUtil.isSpecialChar(teacherName);
                if ("".equals(teacherName) || "ERROR".equals(teacherName)) {
                    setError(file, sheet, row, teacherNameCell, "教师姓名不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else if (null != specialCharStr_teacherName || teacherName.length() > 50) {
                    String pizhu3 = "教师姓名不能含有特殊字符（" + specialCharStr_teacherName + "）且长度不超过50个字符";
                    setError(file, sheet, row, teacherNameCell, pizhu3, columnLen, errorRowStyle, errorCellStyle);
                }
                Cell sexCell = row.getCell(2);
                String sexName = "";
                if (null == sexCell) {
                    sexCell = row.createCell(2);
                } else {
                    sexName = CheckCellUtil.getCellValue(sexCell);
                }
                if (!"".equals(sexName) && !"ERROR".equals(sexName) && !"男".equals(sexName) && !"女".equals(sexName)) {
                    setError(file, sheet, row, sexCell, "性别只能填“男”或者“女”", columnLen, errorRowStyle, errorCellStyle);
                }
                Cell titleCell = row.getCell(3);
                String titleName = "";
                if (null == titleCell) {
                    titleCell = row.createCell(3);
                } else {
                    titleName = CheckCellUtil.getCellValue(titleCell);
                }
                String specialCharStr_titleName = CheckCellUtil.isSpecialChar(titleName);
                if (!"".equals(titleName) && !"ERROR".equals(titleName) && (null != specialCharStr_titleName || titleName.length() > 50)) {
                    String pizhu4 = "职称不能含有特殊字符（" + specialCharStr_titleName + "）且长度不超过50个字符";
                    setError(file, sheet, row, titleCell, pizhu4, columnLen, errorRowStyle, errorCellStyle);
                }
                Cell birthdayCell = row.getCell(4);
                String birthday = "";
                if (null == birthdayCell) {
                    birthdayCell = row.createCell(4);
                } else {
                    birthday = CheckCellUtil.getCellValue(birthdayCell);
                }
                if (!"".equals(birthday) && !"ERROR".equals(birthday) && !CheckCellUtil.isDate(birthday)) {
                    setError(file, sheet, row, birthdayCell, "出生日期格式或者数据有误", columnLen, errorRowStyle, errorCellStyle);
                }
                Cell workingCell = row.getCell(5);
                String working = "";
                if (null == workingCell) {
                    workingCell = row.createCell(5);
                } else {
                    working = CheckCellUtil.getCellValue(workingCell);
                }
                if (!"".equals(working) && !"ERROR".equals(working) && !CheckCellUtil.isDate(working)) {
                    setError(file, sheet, row, workingCell, "参加工作时间格式或者数据有误", columnLen, errorRowStyle, errorCellStyle);
                }
                boolean schoolFlag = false;
                String schoolNum = "";
                String schoolName = "";
                Cell schoolNameCell = row.getCell(6);
                if (null == schoolNameCell) {
                    schoolNameCell = row.createCell(6);
                } else {
                    schoolName = CheckCellUtil.getCellValue(schoolNameCell);
                }
                if ("".equals(schoolName) || "ERROR".equals(schoolName)) {
                    setError(file, sheet, row, schoolNameCell, "学校不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else {
                    String finalSchoolName = schoolName;
                    Map args2 = StreamMap.create().put("schoolName", (Object) finalSchoolName);
                    School sch = (School) this.dao2._queryBean("select id from school where schoolName = {schoolName} limit 1", School.class, args2);
                    if (null == sch) {
                        String pizhu5 = "系统中没有 " + schoolName + " 这个学校";
                        setError(file, sheet, row, schoolNameCell, pizhu5, columnLen, errorRowStyle, errorCellStyle);
                    } else if ("-1".equals(userId) || "-2".equals(userId)) {
                        schoolFlag = true;
                        schoolNum = String.valueOf(sch.getId());
                    } else {
                        boolean ff2 = false;
                        int ii2 = 0;
                        while (true) {
                            if (ii2 >= authSchoolList.size()) {
                                break;
                            }
                            if (!String.valueOf(authSchoolList.get(ii2)).equals(String.valueOf(sch.getId()))) {
                                ii2++;
                            } else {
                                ff2 = true;
                                break;
                            }
                        }
                        if (!ff2) {
                            String pizhu6 = "您没有 " + schoolName + " 这个学校的操作权限";
                            setError(file, sheet, row, schoolNameCell, pizhu6, columnLen, errorRowStyle, errorCellStyle);
                        } else {
                            schoolFlag = true;
                            schoolNum = String.valueOf(sch.getId());
                        }
                    }
                }
                Cell mobileCell = row.getCell(7);
                String mobile = "";
                if (null == mobileCell) {
                    mobileCell = row.createCell(7);
                } else {
                    mobile = CheckCellUtil.getCellValue(mobileCell);
                }
                if (!"".equals(mobile) && !"ERROR".equals(mobile)) {
                    if (!CheckCellUtil.isMobile(mobile)) {
                        setError(file, sheet, row, mobileCell, "联系电话格式错误", columnLen, errorRowStyle, errorCellStyle);
                    } else {
                        String errorStr = "";
                        if (teacherNumSet.add(teacherNum) && !mobileSet.add(mobile)) {
                            errorStr = errorStr + "excel表中 " + mobile + " 联系电话重复，请检查";
                        }
                        if (!"".equals(teacherNum) || !"ERROR".equals(teacherNum)) {
                            String finalMobile = mobile;
                            String finalTeacherNum1 = teacherNum;
                            Map args3 = StreamMap.create().put("mobile", (Object) finalMobile).put("teacherNum", (Object) finalTeacherNum1);
                            Object mob = this.dao2._queryObject("select id from teacher where mobile = {mobile}  and teacherNum <> {teacherNum}  limit 1", args3);
                            if (null != mob) {
                                errorStr = errorStr + "系统中已有其他教师用户使用手机号 " + mobile + "，请检查";
                            }
                        }
                        if (!"".equals(errorStr)) {
                            setError(file, sheet, row, mobileCell, errorStr, columnLen, errorRowStyle, errorCellStyle);
                        }
                    }
                }
                Cell eMailCell = row.getCell(8);
                String eMail = "";
                if (null == eMailCell) {
                    eMailCell = row.createCell(8);
                } else {
                    eMail = CheckCellUtil.getCellValue(eMailCell);
                }
                if (!"".equals(eMail) && !"ERROR".equals(eMail) && (!CheckCellUtil.isEMail(eMail) || eMail.length() > 32)) {
                    setError(file, sheet, row, eMailCell, "E-mail格式错误或长度不超过32个字符", columnLen, errorRowStyle, errorCellStyle);
                }
                Cell presetPasswordCell = row.getCell(9);
                String presetPassword = "";
                if (null == presetPasswordCell) {
                    presetPasswordCell = row.createCell(9);
                } else {
                    presetPassword = CheckCellUtil.getCellValue(presetPasswordCell);
                }
                String specialCharStr_presetPassword = CheckCellUtil.isSpecialChar(presetPassword);
                if (!"".equals(presetPassword) && !"ERROR".equals(presetPassword) && (null != specialCharStr_presetPassword || presetPassword.length() > 20 || presetPassword.length() < 6)) {
                    String pizhu7 = "预置密码不能含有特殊字符（" + specialCharStr_presetPassword + "）且长度为6-20个字符";
                    setError(file, sheet, row, presetPasswordCell, pizhu7, columnLen, errorRowStyle, errorCellStyle);
                }
                boolean positionFlag = false;
                String postNum = "";
                Cell positionNameCell = row.getCell(10);
                String positionName = "";
                if (null == positionNameCell) {
                    positionNameCell = row.createCell(10);
                } else {
                    positionName = CheckCellUtil.getCellValue(positionNameCell);
                }
                if (!"".equals(positionName) && !"ERROR".equals(positionName)) {
                    String finalPositionName = positionName;
                    Map args4 = StreamMap.create().put("positionName", (Object) finalPositionName);
                    postNum = this.dao2._queryStr("select value from data where name = {positionName}  and type = 31 limit 1", args4);
                    if (null == postNum || "".equals(postNum) || "null".equals(postNum)) {
                        String pizhu8 = "系统中没有 " + positionName + " 这个职务";
                        setError(file, sheet, row, positionNameCell, pizhu8, columnLen, errorRowStyle, errorCellStyle);
                    } else {
                        positionFlag = true;
                    }
                }
                boolean subjectFlag = false;
                String subjectNum = "";
                String subjectName = "";
                if (positionFlag && (postNum.equals("1") || postNum.equals("4") || postNum.equals("-2"))) {
                    Cell subjectNameCell = row.getCell(11);
                    if (null == subjectNameCell) {
                        subjectNameCell = row.createCell(11);
                    } else {
                        subjectName = CheckCellUtil.getCellValue(subjectNameCell);
                    }
                    if ("".equals(subjectName) || "ERROR".equals(subjectName)) {
                        setError(file, sheet, row, subjectNameCell, "普通老师、教研主任和备课组长职务下的科目不能为空", columnLen, errorRowStyle, errorCellStyle);
                    } else {
                        String finalSubjectName = subjectName;
                        Map args5 = StreamMap.create().put("subjectName", (Object) finalSubjectName);
                        Object subNum = this.dao2._queryObject("select subjectNum from subject where subjectName={subjectName}  limit 1", args5);
                        if (null == subNum) {
                            String pizhu9 = "系统中没有 " + subjectName + " 这个科目";
                            setError(file, sheet, row, subjectNameCell, pizhu9, columnLen, errorRowStyle, errorCellStyle);
                        } else {
                            subjectFlag = true;
                            subjectNum = String.valueOf(subNum);
                        }
                    }
                }
                boolean gradeFlag = false;
                String gradeNum = "";
                String gradeName = "";
                String islevel = "";
                if (positionFlag) {
                    Cell gradeNameCell = row.getCell(12);
                    if (null == gradeNameCell) {
                        gradeNameCell = row.createCell(12);
                    } else {
                        gradeName = CheckCellUtil.getCellValue(gradeNameCell);
                    }
                    if ("".equals(gradeName) || "ERROR".equals(gradeName)) {
                        if (postNum.equals("1") || postNum.equals("2") || postNum.equals("3") || postNum.equals("4") || postNum.equals("5") || postNum.equals("-2")) {
                            setError(file, sheet, row, gradeNameCell, "普通老师、班主任、年级主任、教研主任、备课组长和学校领导职务下的年级不能为空", columnLen, errorRowStyle, errorCellStyle);
                        }
                    } else if (!schoolFlag) {
                        setError(file, sheet, row, gradeNameCell, "请完善该教师的学校信息后填写与学校信息相符的年级", columnLen, errorRowStyle, errorCellStyle);
                    } else {
                        String finalGradeName = gradeName;
                        String finalSchoolNum = schoolNum;
                        Map args6 = StreamMap.create().put("gradeName", (Object) finalGradeName).put(Const.EXPORTREPORT_schoolNum, (Object) finalSchoolNum);
                        Object[] g = this.dao2._queryArray("select gradeNum,islevel from grade WHERE gradeName={gradeName}  and schoolNum ={schoolNum}   and isDelete = 'F' limit 1", args6);
                        if (null == g) {
                            String pizhu10 = "系统中 " + schoolName + " 这个学校下没有 " + gradeName + " 这个年级";
                            setError(file, sheet, row, gradeNameCell, pizhu10, columnLen, errorRowStyle, errorCellStyle);
                        } else {
                            gradeFlag = true;
                            gradeNum = String.valueOf(g[0]);
                            islevel = String.valueOf(g[1]);
                        }
                    }
                }
                String _set1 = "1".equals(isMultipleTeachers) ? teacherNum : "";
                String _msg1 = "1".equals(isMultipleTeachers) ? "教师工号为" + teacherNum + "的" : "";
                if (positionFlag) {
                    Cell classNameCell = row.getCell(13);
                    String className = "";
                    if (null == classNameCell) {
                        classNameCell = row.createCell(13);
                    } else {
                        className = CheckCellUtil.getCellValue(classNameCell);
                    }
                    if ("".equals(className) || "ERROR".equals(className)) {
                        if (postNum.equals("1") || postNum.equals("2")) {
                            setError(file, sheet, row, classNameCell, "普通老师和班主任职务下的班级不能为空", columnLen, errorRowStyle, errorCellStyle);
                        }
                    } else if (!schoolFlag || !gradeFlag) {
                        String richText = "请完善该教师的学校和年级信息后填写与学校和年级信息相符的班级";
                        if ("1".equals(islevel) && postNum.equals("1") && !subjectFlag) {
                            richText = "请完善该教师的学校、年级和科目信息后填写与学校、年级和科目信息相符的班级";
                        }
                        setError(file, sheet, row, classNameCell, richText, columnLen, errorRowStyle, errorCellStyle);
                    } else if ("1".equals(islevel) && postNum.equals("1")) {
                        String[] classNameList = className.replace("，", Const.STRING_SEPERATOR).split(Const.STRING_SEPERATOR);
                        String errorClas = "";
                        String repeatStr = "";
                        for (String cla : classNameList) {
                            String finalSubjectNum = subjectNum;
                            String finalSchoolNum1 = schoolNum;
                            String finalGradeNum = gradeNum;
                            Map args7 = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) finalSubjectNum).put(Const.EXPORTREPORT_schoolNum, (Object) finalSchoolNum1).put(Const.EXPORTREPORT_gradeNum, (Object) finalGradeNum).put("cla", (Object) cla);
                            Object claNum = this.dao2._queryObject("select id from levelclass where subjectNum = {subjectNum}  and schoolNum = {schoolNum}  and gradeNum = {gradeNum} and className = {cla}  and isDelete = 'F' limit 1", args7);
                            if (null == claNum) {
                                errorClas = errorClas + cla + "，";
                            }
                            if (postNum.equals("1") && !postNum1Set.add(schoolName + "_" + subjectName + "_" + gradeName + "_" + cla + "_" + _set1)) {
                                repeatStr = repeatStr + cla + "，";
                            } else if (postNum.equals("2") && !postNum2Set.add(schoolName + "_" + gradeName + "_" + cla + "_" + _set1)) {
                                repeatStr = repeatStr + cla + "，";
                            }
                        }
                        if (!"".equals(errorClas)) {
                            errorClas = "系统中 " + schoolName + " 这个学校 " + gradeName + " 这个年级 " + subjectName + " 这个科目下没有 " + errorClas.substring(0, errorClas.length() - 1) + " 班级";
                        }
                        if (postNum.equals("1") && !"".equals(repeatStr)) {
                            repeatStr = "excel表中" + _msg1 + gradeName + repeatStr.substring(0, repeatStr.length() - 1) + "的" + subjectName + "老师重复，请检查";
                        } else if (postNum.equals("2") && !"".equals(repeatStr)) {
                            repeatStr = "excel表中" + _msg1 + gradeName + repeatStr.substring(0, repeatStr.length() - 1) + "的班主任重复，请检查";
                        }
                        if (!"".equals(errorClas) || !"".equals(repeatStr)) {
                            setError(file, sheet, row, classNameCell, errorClas + repeatStr, columnLen, errorRowStyle, errorCellStyle);
                        }
                    } else {
                        String[] classNameList2 = className.replace("，", Const.STRING_SEPERATOR).split(Const.STRING_SEPERATOR);
                        String errorClas2 = "";
                        String repeatStr2 = "";
                        for (String cla2 : classNameList2) {
                            Map args8 = new HashMap();
                            args8.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                            args8.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                            args8.put("cla", cla2);
                            Object claNum2 = this.dao2._queryObject("select id from class where schoolNum = {schoolNum} and gradeNum = {gradeNum} and className = {cla}  and isDelete = 'F' limit 1", args8);
                            if (null == claNum2) {
                                errorClas2 = errorClas2 + cla2 + "，";
                            }
                            if (postNum.equals("1") && !postNum1Set.add(schoolName + "_" + subjectName + "_" + gradeName + "_" + cla2 + "_" + _set1)) {
                                repeatStr2 = repeatStr2 + cla2 + "，";
                            } else if (postNum.equals("2") && !postNum2Set.add(schoolName + "_" + gradeName + "_" + cla2 + "_" + _set1)) {
                                repeatStr2 = repeatStr2 + cla2 + "，";
                            }
                        }
                        if (!"".equals(errorClas2)) {
                            errorClas2 = "系统中 " + schoolName + " 这个学校 " + gradeName + " 这个年级下没有 " + errorClas2.substring(0, errorClas2.length() - 1) + " 班级";
                        }
                        if (postNum.equals("1") && !"".equals(repeatStr2)) {
                            repeatStr2 = "excel表中" + _msg1 + gradeName + repeatStr2.substring(0, repeatStr2.length() - 1) + "的" + subjectName + "老师重复，请检查";
                        } else if (postNum.equals("2") && !"".equals(repeatStr2)) {
                            repeatStr2 = "excel表中" + _msg1 + gradeName + repeatStr2.substring(0, repeatStr2.length() - 1) + "的班主任重复，请检查";
                        }
                        if (!"".equals(errorClas2) || !"".equals(repeatStr2)) {
                            setError(file, sheet, row, classNameCell, errorClas2 + repeatStr2, columnLen, errorRowStyle, errorCellStyle);
                        }
                    }
                }
                if (positionFlag) {
                    if (postNum.equals("3")) {
                        if (!postNum3Set.add(schoolName + "_" + gradeName + "_" + _set1)) {
                            Cell errorCell = row.createCell(18);
                            String pizhu11 = "excel表中" + _msg1 + gradeName + "的年级主任重复，请检查";
                            setError(file, sheet, row, errorCell, pizhu11, columnLen, errorRowStyle, errorCellStyle);
                        }
                    } else if (postNum.equals("4")) {
                        if (!postNum4Set.add(schoolName + "_" + gradeName + "_" + subjectName + "_" + _set1)) {
                            Cell errorCell2 = row.createCell(18);
                            String pizhu12 = "excel表中" + _msg1 + gradeName + "的" + subjectName + "教研主任重复，请检查";
                            setError(file, sheet, row, errorCell2, pizhu12, columnLen, errorRowStyle, errorCellStyle);
                        }
                    } else if (postNum.equals("-2") && !postNum5Set.add(schoolName + "_" + gradeName + "_" + subjectName + "_" + _set1)) {
                        Cell errorCell3 = row.createCell(18);
                        String pizhu13 = "excel表中" + _msg1 + gradeName + "的" + subjectName + "备课组长重复，请检查";
                        setError(file, sheet, row, errorCell3, pizhu13, columnLen, errorRowStyle, errorCellStyle);
                    }
                }
                if (positionFlag && (postNum.equals("1") || postNum.equals("2"))) {
                    Cell perClassCell = row.getCell(14);
                    String perClass = "";
                    if (null == perClassCell) {
                        perClassCell = row.createCell(14);
                    } else {
                        perClass = CheckCellUtil.getCellValue(perClassCell);
                    }
                    if ("".equals(perClass) || "ERROR".equals(perClass)) {
                        setError(file, sheet, row, perClassCell, "普通老师和班主任职务下的权限【班】不能为空；填写0代表无班级权限，1代表有班级权限", columnLen, errorRowStyle, errorCellStyle);
                    }
                }
                if (positionFlag && (postNum.equals("1") || postNum.equals("2") || postNum.equals("3") || postNum.equals("4"))) {
                    Cell perGradeCell = row.getCell(15);
                    String perGrade = "";
                    if (null == perGradeCell) {
                        perGradeCell = row.createCell(15);
                    } else {
                        perGrade = CheckCellUtil.getCellValue(perGradeCell);
                    }
                    if ("".equals(perGrade) || "ERROR".equals(perGradeCell)) {
                        setError(file, sheet, row, perGradeCell, "普通老师、班主任、年级主任和教研主任职务下的权限【年级】不能为空；填写0代表无年级权限，1代表有年级权限", columnLen, errorRowStyle, errorCellStyle);
                    }
                }
                if (positionFlag && (postNum.equals("1") || postNum.equals("4"))) {
                    Cell perSubjectCell = row.getCell(16);
                    String perSubject = "";
                    if (null == perSubjectCell) {
                        perSubjectCell = row.createCell(16);
                    } else {
                        perSubject = CheckCellUtil.getCellValue(perSubjectCell);
                    }
                    if ("".equals(perSubject) || "ERROR".equals(perSubject)) {
                        setError(file, sheet, row, perSubjectCell, "普通老师和教研主任职务下的权限【科目】不能为空；填写0代表无科目权限，1代表有科目权限", columnLen, errorRowStyle, errorCellStyle);
                    }
                }
            }
            if (this.errorFlag) {
                FileOutputStream fOut = new FileOutputStream(file);
                workbook.write(fOut);
                fOut.flush();
                fOut.close();
                return new RspMsg(Const.height_500, "导入文件信息错误", null);
            }
            return new RspMsg(200, "导入文件信息正确", null);
        } catch (Exception e) {
            return new RspMsg(410, "check error:" + e.getMessage(), null);
        }
    }

    public void setError(File file, Sheet sheet, Row row, Cell cell, String pizhu, int columnLen, CellStyle errorRowStyle, CellStyle errorCellStyle) {
        if (!this.rowBgColor) {
            CheckCellUtil.setRowStyle(row, columnLen, errorRowStyle);
            this.rowBgColor = true;
            this.errorFlag = true;
        }
        CheckCellUtil.setCellStyle(file, sheet, cell, pizhu, errorCellStyle);
    }

    /* JADX WARN: Type inference failed for: r0v107, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v115, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v123, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v131, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v139, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v147, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v155, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v163, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v83, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v91, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v99, types: [java.lang.Object[], java.lang.Object[][]] */
    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void importTeacher(File file, String fileName, String userId, String currentTime, Map<String, String> jie_map, String isMultipleTeachers) throws Exception {
        Row row;
        String presetPassword;
        String str;
        ExcelHelper excelHelper = new ExcelHelper(file);
        Workbook workbook = excelHelper.creatWorkbook();
        Sheet sheet = workbook.getSheetAt(0);
        List<Object[]> insertTeacherParams = new ArrayList<>();
        List<Object[]> updateTeacherParams = new ArrayList<>();
        List<Object[]> updateChangeschoolParams = new ArrayList<>();
        List<Object[]> insertUserParams = new ArrayList<>();
        List<Object[]> updateUserParams = new ArrayList<>();
        List<Object[]> insertUserroleParams = new ArrayList<>();
        List<Object[]> updateUserroleParams = new ArrayList<>();
        List<Object[]> deleteUserpositionParams = new ArrayList<>();
        List<Object[]> deleteUserpositionParams2 = new ArrayList<>();
        List<Object[]> insertUserPositionParams = new ArrayList<>();
        List<Object[]> updateUserPositionParams = new ArrayList<>();
        Map<String, TDataParameters> tMap = new HashMap<>();
        Map<String, TDataParameters> tupMap = new HashMap<>();
        String currentTime2 = DateUtil.getCurrentTime();
        new SecureUtil();
        int len = sheet.getPhysicalNumberOfRows();
        for (int i = 2; i < len && null != (row = sheet.getRow(i)); i++) {
            String teacherId = GUID.getGUIDStr();
            String userNum = GUID.getGUIDStr();
            Cell teacherNumCell = row.getCell(0);
            String teacherNum = CheckCellUtil.getCellValue(teacherNumCell);
            Cell teacherNameCell = row.getCell(1);
            String teacherName = CheckCellUtil.getCellValue(teacherNameCell);
            Cell sexCell = row.getCell(2);
            String sexName = CheckCellUtil.getCellValue(sexCell);
            Cell titleCell = row.getCell(3);
            String titleName = CheckCellUtil.getCellValue(titleCell);
            Cell birthdayCell = row.getCell(4);
            String birthday = CheckCellUtil.getCellValue(birthdayCell);
            if ("".equals(birthday) || "ERROR".equals(birthday)) {
                birthday = null;
            }
            Cell workingCell = row.getCell(5);
            String working = CheckCellUtil.getCellValue(workingCell);
            if ("".equals(working) || "ERROR".equals(working)) {
                working = null;
            }
            Cell schoolNameCell = row.getCell(6);
            String schoolName = CheckCellUtil.getCellValue(schoolNameCell);
            Map args_schSql = StreamMap.create().put("schoolName", (Object) schoolName);
            String schoolNum = String.valueOf(this.dao2._queryObject("select id from school where schoolName = {schoolName} limit 1", args_schSql));
            if ("null".equals(schoolNum)) {
                schoolNum = null;
            }
            Cell mobileCell = row.getCell(7);
            String mobile = CheckCellUtil.getCellValue(mobileCell);
            if ((mobile.equals("") || null == mobile) && !teacherNum.equals("") && CommonUtil.isMobile(teacherNum)) {
                mobile = teacherNum;
            }
            Cell eMailCell = row.getCell(8);
            String eMail = CheckCellUtil.getCellValue(eMailCell);
            Cell presetPasswordCell = row.getCell(9);
            String presetPassword2 = CheckCellUtil.getCellValue(presetPasswordCell);
            if ("".equals(presetPassword2) || "ERROR".equals(presetPassword2)) {
                Map args_sql2 = StreamMap.create().put("teacherNum", (Object) teacherNum);
                Object oldPassword = this.dao2._queryObject("select u.password from (select id from teacher where teacherNum = {teacherNum}) t left join user u on u.userid = t.id limit 1", args_sql2);
                if (null == oldPassword) {
                    presetPassword = Const.USER_PASSWORD;
                } else {
                    presetPassword = String.valueOf(oldPassword);
                }
            } else {
                presetPassword = SecureUtil.md5(presetPassword2.toLowerCase());
            }
            boolean positionFlag = false;
            String postNum = "";
            Cell positionNameCell = row.getCell(10);
            String positionName = CheckCellUtil.getCellValue(positionNameCell);
            if (!"".equals(positionName) && !"ERROR".equals(positionName)) {
                Map args_sql3 = StreamMap.create().put("positionName", (Object) positionName);
                postNum = String.valueOf(this.dao2._queryObject("select value from data where name = {positionName} and type = 31 limit 1", args_sql3));
                positionFlag = true;
            }
            TDataParameters tDataParameters = new TDataParameters(teacherId, teacherNum, teacherName, sexName, titleName, birthday, working, userId, currentTime2, schoolNum, mobile, eMail, "F", userNum, presetPassword, "1", "T", "1");
            tMap.put(teacherNum, tDataParameters);
            if (positionFlag) {
                Cell subjectNameCell = row.getCell(11);
                String subjectName = CheckCellUtil.getCellValue(subjectNameCell);
                Map args_subSql = StreamMap.create().put("subjectName", (Object) subjectName);
                String subjectNum = String.valueOf(this.dao2._queryObject("select subjectNum from subject where subjectName={subjectName} limit 1", args_subSql));
                String subjectNum2 = ("null".equals(subjectNum) || "".equals(subjectNum)) ? null : subjectNum;
                Cell gradeNameCell = row.getCell(12);
                String gradeName = CheckCellUtil.getCellValue(gradeNameCell);
                String finalSchoolNum = schoolNum;
                Map args_graSql = StreamMap.create().put("gradeName", (Object) gradeName).put(Const.EXPORTREPORT_schoolNum, (Object) finalSchoolNum);
                Object[] g = this.dao2._queryArray("select gradeNum,islevel from grade WHERE gradeName={gradeName} and schoolNum = {schoolNum} and isDelete = 'F' limit 1", args_graSql);
                String gradeNum = null;
                String islevel = "";
                if (null != g) {
                    gradeNum = String.valueOf(g[0]);
                    islevel = String.valueOf(g[1]);
                }
                String gradeNum2 = ("null".equals(gradeNum) || "".equals(gradeNum)) ? null : gradeNum;
                String jie = String.valueOf(jie_map.get(gradeNum2));
                String jie2 = ("null".equals(jie) || "".equals(jie)) ? null : jie;
                String stage = null == gradeNum2 ? null : getStage(gradeNum2);
                Cell classNameCell = row.getCell(13);
                String className = CheckCellUtil.getCellValue(classNameCell);
                Cell perClassCell = row.getCell(14);
                String perClass = "1".equals(CheckCellUtil.getCellValue(perClassCell)) ? "1" : "0";
                Cell perGradeCell = row.getCell(15);
                String perGrade = "1".equals(CheckCellUtil.getCellValue(perGradeCell)) ? "1" : "0";
                Cell perSubjectCell = row.getCell(16);
                String perSubject = "1".equals(CheckCellUtil.getCellValue(perSubjectCell)) ? "1" : "0";
                if (postNum.equals("1")) {
                    String[] classNameList = className.replace("，", Const.STRING_SEPERATOR).split(Const.STRING_SEPERATOR);
                    if ("1".equals(islevel)) {
                        for (String cla : classNameList) {
                            String finalSchoolNum1 = schoolNum;
                            Map args_claSql = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum2).put(Const.EXPORTREPORT_schoolNum, (Object) finalSchoolNum1).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum2).put("cla", (Object) cla);
                            String classNum = String.valueOf(this.dao2._queryObject("select id from levelclass where subjectNum = {subjectNum} and schoolNum = {schoolNum} and gradeNum = {gradeNum} and className = {cla} and isDelete = 'F' limit 1", args_claSql));
                            String desc = cla + subjectName + "老师";
                            TDataParameters tupDataParameters = new TDataParameters(teacherNum, userId, currentTime2, schoolNum, userNum, postNum, desc, gradeNum2, subjectNum2, classNum, jie2, stage, perClass, perGrade, perSubject);
                            tupMap.put(postNum + schoolNum + gradeNum2 + subjectNum2 + classNum + jie2 + userNum, tupDataParameters);
                        }
                    } else {
                        for (String cla2 : classNameList) {
                            String finalSchoolNum2 = schoolNum;
                            Map args_claSql2 = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) finalSchoolNum2).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum2).put("cla", (Object) cla2);
                            String classNum2 = String.valueOf(this.dao2._queryObject("select id from class where schoolNum = {schoolNum} and gradeNum = {gradeNum} and className = {cla} and isDelete = 'F' limit 1", args_claSql2));
                            String desc2 = cla2 + subjectName + "老师";
                            TDataParameters tupDataParameters2 = new TDataParameters(teacherNum, userId, currentTime2, schoolNum, userNum, postNum, desc2, gradeNum2, subjectNum2, classNum2, jie2, stage, perClass, perGrade, perSubject);
                            tupMap.put(postNum + schoolNum + gradeNum2 + subjectNum2 + classNum2 + jie2 + userNum, tupDataParameters2);
                        }
                    }
                } else if (postNum.equals("2")) {
                    String[] classNameList2 = className.replace("，", Const.STRING_SEPERATOR).split(Const.STRING_SEPERATOR);
                    for (String cla3 : classNameList2) {
                        String finalSchoolNum3 = schoolNum;
                        Map args_claSql3 = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) finalSchoolNum3).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum2).put("cla", (Object) cla3);
                        String classNum3 = String.valueOf(this.dao2._queryObject("select id from class where schoolNum = {schoolNum} and gradeNum = {gradeNum} and className = {cla} and isDelete = 'F' limit 1", args_claSql3));
                        String desc3 = cla3 + "班主任";
                        TDataParameters tupDataParameters3 = new TDataParameters(teacherNum, userId, currentTime2, schoolNum, userNum, postNum, desc3, gradeNum2, "999", classNum3, jie2, stage, perClass, perGrade, "1");
                        tupMap.put(postNum + schoolNum + gradeNum2 + subjectNum2 + classNum3 + jie2 + userNum, tupDataParameters3);
                    }
                } else if (postNum.equals("3")) {
                    String desc4 = gradeName + "年级主任";
                    TDataParameters tupDataParameters4 = new TDataParameters(teacherNum, userId, currentTime2, schoolNum, userNum, postNum, desc4, gradeNum2, "999", (String) null, jie2, stage, "1", perGrade, "1");
                    tupMap.put(postNum + schoolNum + gradeNum2 + subjectNum2 + ((Object) null) + jie2 + userNum, tupDataParameters4);
                } else if (postNum.equals("4")) {
                    String desc5 = gradeName + "的" + subjectName + "教研主任";
                    TDataParameters tupDataParameters5 = new TDataParameters(teacherNum, userId, currentTime2, schoolNum, userNum, postNum, desc5, gradeNum2, subjectNum2, (String) null, jie2, stage, "1", perGrade, perSubject);
                    tupMap.put(postNum + schoolNum + gradeNum2 + subjectNum2 + ((Object) null) + jie2 + userNum, tupDataParameters5);
                } else if (postNum.equals("5")) {
                    Map args_stageNameSql = StreamMap.create().put("stage", (Object) stage);
                    String stageName = String.valueOf(this.dao2._queryObject("select name from data where type = '5' and value = {stage} limit 1", args_stageNameSql));
                    String desc6 = stageName + "校长";
                    TDataParameters tupDataParameters6 = new TDataParameters(teacherNum, userId, currentTime2, schoolNum, userNum, postNum, desc6, (String) null, (String) null, (String) null, jie2, stage, "1", "1", "1");
                    tupMap.put(postNum + schoolNum + ((Object) null) + ((Object) null) + ((Object) null) + jie2 + userNum, tupDataParameters6);
                } else if (postNum.equals("0")) {
                    TDataParameters tupDataParameters7 = new TDataParameters(teacherNum, userId, currentTime2, schoolNum, userNum, postNum, positionName, (String) null, (String) null, (String) null, jie2, stage, "1", "1", "1");
                    tupMap.put(postNum + schoolNum + ((Object) null) + ((Object) null) + ((Object) null) + jie2 + userNum, tupDataParameters7);
                } else {
                    if ("null".equals(jie2)) {
                        jie2 = null;
                    }
                    if (StrUtil.isNotEmpty(className)) {
                        String[] classNameList3 = className.replace("，", Const.STRING_SEPERATOR).split(Const.STRING_SEPERATOR);
                        for (String cla4 : classNameList3) {
                            String finalSchoolNum32 = schoolNum;
                            Map args_claSql4 = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) finalSchoolNum32).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum2).put("cla", (Object) cla4);
                            String classNum4 = String.valueOf(this.dao2._queryObject("select id from class where schoolNum = {schoolNum} and gradeNum = {gradeNum} and className = {cla} and isDelete = 'F' limit 1", args_claSql4));
                            if (StrUtil.isEmpty(classNum4) || "null".equals(classNum4)) {
                                classNum4 = null;
                                str = positionName;
                            } else {
                                str = cla4 + positionName;
                            }
                            String desc7 = str;
                            TDataParameters tupDataParameters8 = new TDataParameters(teacherNum, userId, currentTime2, schoolNum, userNum, postNum, desc7, gradeNum2, subjectNum2, classNum4, jie2, stage, perClass, perGrade, perSubject);
                            tupMap.put(postNum + schoolNum + gradeNum2 + subjectNum2 + classNum4 + jie2 + userNum, tupDataParameters8);
                        }
                    } else {
                        TDataParameters tupDataParameters9 = new TDataParameters(teacherNum, userId, currentTime2, schoolNum, userNum, postNum, positionName, gradeNum2, subjectNum2, (String) null, jie2, stage, perClass, perGrade, perSubject);
                        tupMap.put(postNum + schoolNum + gradeNum2 + subjectNum2 + ((Object) null) + jie2 + userNum, tupDataParameters9);
                    }
                }
            }
        }
        for (Map.Entry<String, TDataParameters> next : tMap.entrySet()) {
            TDataParameters tData = next.getValue();
            Object[] tParam = {tData.getTeacherNum()};
            Object t = this.dao2.queryObject("select id from teacher where teacherNum=? ", tParam);
            if (null == t) {
                Object[] insertTeacherParam = {tData.getTeacherId(), tData.getTeacherNum(), tData.getTeacherName(), tData.getSex(), tData.getTitle(), tData.getBirthday(), tData.getWorktime(), tData.getInsertUser(), tData.getInsertDate(), tData.getInsertUser(), tData.getInsertDate(), tData.getSchoolNum(), tData.getMobile(), tData.getEmail(), tData.getIsDelete()};
                insertTeacherParams.add(insertTeacherParam);
            } else {
                tData.setTeacherId(String.valueOf(t));
                Object[] updateTeacherParam = {tData.getTeacherName(), tData.getSex(), tData.getTitle(), tData.getBirthday(), tData.getWorktime(), tData.getInsertUser(), tData.getInsertDate(), tData.getSchoolNum(), tData.getMobile(), tData.getEmail(), tData.getIsDelete(), tData.getTeacherId()};
                updateTeacherParams.add(updateTeacherParam);
            }
            Object[] cs_tParam = {tData.getTeacherNum(), "0"};
            Object cs_t = this.dao2.queryObject("select id from changeSchool_teacher where teacherNum=? and status=?", cs_tParam);
            if (null != cs_t) {
                Object[] updateChangeschoolParam = {tData.getTeacherNum(), tData.getTeacherName(), tData.getSex(), tData.getTitle(), tData.getBirthday(), tData.getWorktime(), tData.getSchoolNum(), tData.getMobile(), tData.getEmail(), "1", "-3", tData.getInsertUser(), tData.getInsertDate(), cs_t.toString()};
                updateChangeschoolParams.add(updateChangeschoolParam);
            }
            Object[] uParam = {tData.getTeacherNum(), "1"};
            Object u = this.dao2.queryObject("select id from user where username=? and usertype=? ", uParam);
            if (null == u) {
                Object[] insertUserParam = {tData.getUserId(), tData.getTeacherId(), tData.getTeacherNum(), tData.getPassword(), tData.getUsertype(), tData.getTeacherName(), tData.getSchoolNum(), tData.getMobile(), tData.getEmail(), tData.getIsUser(), tData.getIsDelete(), tData.getInsertUser(), tData.getInsertDate()};
                insertUserParams.add(insertUserParam);
            } else {
                tData.setUserId(String.valueOf(u));
                Object[] updateUserParam = {tData.getPassword(), tData.getTeacherName(), tData.getSchoolNum(), tData.getMobile(), tData.getEmail(), tData.getIsUser(), tData.getIsDelete(), tData.getInsertUser(), tData.getInsertDate(), tData.getUserId()};
                updateUserParams.add(updateUserParam);
            }
            Object[] urParam = {tData.getUserId(), "1"};
            Object ur = this.dao2.queryObject("select id from userrole where userNum=? and roleNum=? ", urParam);
            if (null == ur) {
                Object[] insertUserroleParam = {tData.getUserId(), tData.getRoleNum(), tData.getInsertUser(), tData.getInsertDate(), tData.getIsDelete()};
                insertUserroleParams.add(insertUserroleParam);
            } else {
                Object[] updateUserroleParam = {tData.getInsertUser(), tData.getInsertDate(), tData.getIsDelete(), String.valueOf(ur)};
                updateUserroleParams.add(updateUserroleParam);
            }
        }
        for (TDataParameters tupData : tupMap.values()) {
            String uKey = tupData.getTeacherNum();
            String uId = tMap.get(uKey).getUserId();
            String _gra = tupData.getGradeNum();
            String _sub = tupData.getSubjectNum();
            String _cla = tupData.getClassNum();
            String _jie = tupData.getJie();
            String selUserpositionSql = "select id FROM userposition WHERE type=? and schoolnum=? " + (null == _gra ? " and gradeNum is ? " : " and gradeNum = ? ");
            String selUserpositionSql2 = (((selUserpositionSql + (null == _sub ? " and subjectNum is ? " : " and subjectNum = ? ")) + (null == _cla ? " and classNum is ? " : " and classNum = ? ")) + (null == _jie ? " and jie is ? " : " and jie = ? ")) + " and userNum=? ";
            Object[] deleteUserpositionParam = {tupData.getType(), tupData.getSchoolNum(), tupData.getGradeNum(), tupData.getSubjectNum(), tupData.getClassNum(), tupData.getJie(), uId};
            if (!"1".equals(isMultipleTeachers)) {
                if (tupData.getType().equals("1") || tupData.getType().equals("2")) {
                    deleteUserpositionParams.add(deleteUserpositionParam);
                } else if (tupData.getType().equals("3") || tupData.getType().equals("4")) {
                    deleteUserpositionParams2.add(deleteUserpositionParam);
                }
            }
            Object up = this.dao2.queryObject(selUserpositionSql2, deleteUserpositionParam);
            if (null == up) {
                Object[] insertUserpositionParam = {uId, tupData.getType(), tupData.getDescription(), tupData.getSchoolNum(), tupData.getGradeNum(), tupData.getSubjectNum(), tupData.getClassNum(), tupData.getJie(), tupData.getStage(), tupData.getInsertUser(), tupData.getInsertDate(), tupData.getPermission_class(), tupData.getPermission_grade(), tupData.getPermission_subject()};
                insertUserPositionParams.add(insertUserpositionParam);
            } else {
                Object[] updateUserpositionParam = {tupData.getDescription(), tupData.getStage(), tupData.getInsertUser(), tupData.getInsertDate(), tupData.getPermission_class(), tupData.getPermission_grade(), tupData.getPermission_subject(), String.valueOf(up)};
                updateUserPositionParams.add(updateUserpositionParam);
            }
        }
        this.log.info("【教师导入】数据入库开始：当前操作人" + userId + "----" + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
        if (insertTeacherParams.size() > 0) {
            ?? r0 = new Object[insertTeacherParams.size()];
            insertTeacherParams.toArray((Object[]) r0);
            this.dao2.batchExecuteByLimit("INSERT INTO teacher (id,teacherNum,teacherName,sex,title,birthday,worktime,insertUser,insertDate,updateUser,updateDate,schoolNum,mobile,email,isDelete) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ", r0, 300);
        }
        if (updateTeacherParams.size() > 0) {
            ?? r02 = new Object[updateTeacherParams.size()];
            updateTeacherParams.toArray((Object[]) r02);
            this.dao2.batchExecuteByLimit("update teacher set teacherName=?,sex=?,title=?,birthday=?,worktime=?,updateUser=?,updateDate=?,schoolNum=?,mobile=?,email=?,isDelete=? where id=? ", r02, 300);
        }
        if (updateChangeschoolParams.size() > 0) {
            ?? r03 = new Object[updateChangeschoolParams.size()];
            updateChangeschoolParams.toArray((Object[]) r03);
            this.dao2.batchExecuteByLimit("update changeSchool_teacher set teacherNum=?,teacherName=?,sex=?,title=?,birthday=?,worktime=?,schoolNum=?,mobile=?,email=?,status=?,dealTeacher=?,updateUser=?,updateDate=? where id=?", r03, 300);
        }
        if (insertUserParams.size() > 0) {
            ?? r04 = new Object[insertUserParams.size()];
            insertUserParams.toArray((Object[]) r04);
            this.dao2.batchExecuteByLimit("INSERT INTO user (id,userid,username,password,usertype,realname,schoolnum,mobile,email,isUser,isDelete,insertUser,insertDate)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ", r04, 300);
        }
        if (updateUserParams.size() > 0) {
            ?? r05 = new Object[updateUserParams.size()];
            updateUserParams.toArray((Object[]) r05);
            this.dao2.batchExecuteByLimit("update user set password=?,realname=?,schoolnum=?,mobile=?,email=?,isUser=?,isDelete=?,insertUser=?,insertDate=? where id=? ", r05, 300);
        }
        if (insertUserroleParams.size() > 0) {
            ?? r06 = new Object[insertUserroleParams.size()];
            insertUserroleParams.toArray((Object[]) r06);
            this.dao2.batchExecuteByLimit("INSERT INTO userrole (userNum,roleNum,insertUser,insertDate,isDelete) VALUES (?,?,?,?,?) ", r06, 300);
        }
        if (updateUserroleParams.size() > 0) {
            ?? r07 = new Object[updateUserroleParams.size()];
            updateUserroleParams.toArray((Object[]) r07);
            this.dao2.batchExecuteByLimit("update userrole set insertUser=?,insertDate=?,isDelete=? where id=? ", r07, 300);
        }
        if (deleteUserpositionParams.size() > 0) {
            ?? r08 = new Object[deleteUserpositionParams.size()];
            deleteUserpositionParams.toArray((Object[]) r08);
            this.dao2.batchExecuteByLimit("DELETE FROM userposition WHERE type=? and schoolnum=? and gradeNum=? and subjectNum=? and classNum=? and jie=? and userNum<>?", r08, 300);
        }
        if (deleteUserpositionParams2.size() > 0) {
            ?? r09 = new Object[deleteUserpositionParams2.size()];
            deleteUserpositionParams2.toArray((Object[]) r09);
            this.dao2.batchExecuteByLimit("DELETE FROM userposition WHERE type=? and schoolnum=? and gradeNum=? and subjectNum=? and classNum is ? and jie=? and userNum<>?", r09, 300);
        }
        if (insertUserPositionParams.size() > 0) {
            ?? r010 = new Object[insertUserPositionParams.size()];
            insertUserPositionParams.toArray((Object[]) r010);
            this.dao2.batchExecuteByLimit("INSERT INTO userposition (userNum,type,description,schoolnum,gradenum,subjectNum,classNum,jie,stage,insertUser,insertDate,permission_class,permission_grade,permission_subject) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ", r010, 300);
        }
        if (updateUserPositionParams.size() > 0) {
            ?? r011 = new Object[updateUserPositionParams.size()];
            updateUserPositionParams.toArray((Object[]) r011);
            this.dao2.batchExecuteByLimit("update userposition set description=?,stage=?,insertUser=?,insertDate=?,permission_class=?,permission_grade=?,permission_subject=? where id=? ", r011, 300);
        }
        this.dao2.execute("delete up.* from userposition up left join `user` u on u.id = up.userNum where up.schoolnum <> u.schoolnum ");
        this.log.info("【教师导入】数据入库结束：当前操作人" + userId + "----" + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Grade> getAuthGradeListBySchool(String userId, String leicengId) {
        String sql;
        if (null == leicengId || "".equals(leicengId)) {
            sql = "select distinct gra.gradeNum,gra.gradeName from grade gra left join (select schoolNum from schauthormanage where userId = {userId} and isDelete = 'F' union select schoolNum from user where id = {userId} and isDelete = 'F') sam on sam.schoolNum = gra.schoolNum where sam.schoolNum is not null";
        } else {
            sql = "select distinct gra.gradeNum,gra.gradeName from grade gra left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leicengId} group by sItemId) ss on ss.id = gra.schoolNum where ss.id is not null";
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put("leicengId", (Object) leicengId);
        return this.dao2._queryBeanList(sql, Grade.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public User getTeacherSchoolNumByUser(String userId) {
        Map args = StreamMap.create().put("userId", (Object) userId);
        return (User) this.dao2._queryBean("select u.schoolNum from user u left join school sch on sch.id = u.schoolNum where u.id = {userId} and sch.isDelete = 'F' ", User.class, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Object[]> getRepeatedMobileList() {
        return this.dao2._queryArrayList("select DISTINCT a.id,a.mobile from teacher a left join teacher b on a.mobile=b.mobile where length(a.mobile)=11 and a.updateDate > b.updateDate ", null);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String exportOutTeacherLeq(String leicengId, String school, String gradeNum, String subjectName, String searchnum, String searchName, String searchsex, String searchtitle, String userId, String result, String dirPath, String agent, String schoolName, String positionName, String phone) {
        String folderPath = "ExportFolder/exportTeacherExcel/" + userId;
        String excelName = "导出教师信息_" + schoolName;
        File excelFile = this.res.getRptExcelFile(excelName, dirPath, folderPath);
        try {
            WritableWorkbook wwBook = jxl.Workbook.createWorkbook(excelFile);
            try {
                WritableSheet sheet = wwBook.createSheet("教师基础信息", 0);
                try {
                    List<Object[]> dataList = getExportTeacherData(leicengId, school, gradeNum, subjectName, searchnum, searchName, searchsex, searchtitle, userId, result, positionName, phone);
                    Label biaotou1 = new Label(0, 0, "教师基础信息");
                    sheet.addCell(biaotou1);
                    Label biaotou2 = new Label(10, 0, "职务分配");
                    sheet.addCell(biaotou2);
                    sheet.mergeCells(0, 0, 9, 0);
                    sheet.mergeCells(10, 0, 16, 0);
                    String[] excelTitle = {"教师ID号", "教师姓名", "性别", "职称", "出生日期", "参加工作时间", "学校", "联系电话", "E-mail", "预置密码", "职务", "科目", "年级", "班级", "权限【班】", "权限【年级】", "权限【科目】"};
                    for (int i = 0; i < excelTitle.length; i++) {
                        Label teacherCell = new Label(i, 1, excelTitle[i]);
                        sheet.addCell(teacherCell);
                    }
                    for (int j = 0; j < dataList.size(); j++) {
                        Object[] t = dataList.get(j);
                        for (int z = 0; z < excelTitle.length; z++) {
                            String cellVal = String.valueOf(t[z]);
                            Label oneCell = new Label(z, j + 2, "null".equals(cellVal) ? "" : cellVal);
                            sheet.addCell(oneCell);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wwBook.write();
                if (wwBook != null) {
                    try {
                        wwBook.close();
                    } catch (WriteException e2) {
                        e2.printStackTrace();
                    }
                }
            } catch (Exception e3) {
                if (wwBook != null) {
                    try {
                        wwBook.close();
                    } catch (WriteException e4) {
                        e4.printStackTrace();
                    }
                }
            } catch (Throwable th) {
                if (wwBook != null) {
                    try {
                        wwBook.close();
                    } catch (WriteException e5) {
                        e5.printStackTrace();
                        throw th;
                    }
                }
                throw th;
            }
        } catch (Exception e6) {
            e6.printStackTrace();
        }
        return excelFile.getName();
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String exportOutTeacherPost(String leicengId, String school, String gradeNum, String subjectName, String searchnum, String searchName, String searchsex, String searchtitle, String userId, String result, String dirPath, String agent, String schoolName, String positionName, String phone) {
        String folderPath = "ExportFolder/exportTeacherExcel/" + userId;
        String excelName = "导出教师班级职务信息_" + schoolName;
        File excelFile = this.res.getRptExcelFile(excelName, dirPath, folderPath);
        try {
            WritableWorkbook wwBook = jxl.Workbook.createWorkbook(excelFile);
            try {
                WritableSheet sheet = wwBook.createSheet("教师基础信息", 0);
                try {
                    List<Object[]> dataList = getExportTeacherDataPost(leicengId, school, gradeNum, subjectName, searchnum, searchName, searchsex, searchtitle, userId, result, positionName, phone);
                    Label biaotou1 = new Label(0, 0, "教师基础信息");
                    sheet.addCell(biaotou1);
                    Label biaotou2 = new Label(10, 0, "职务分配");
                    sheet.addCell(biaotou2);
                    sheet.mergeCells(0, 0, 9, 0);
                    sheet.mergeCells(10, 0, 16, 0);
                    String[] excelTitle = {"教师ID号", "教师姓名", "性别", "职称", "出生日期", "参加工作时间", "学校", "联系电话", "E-mail", "预置密码", "职务", "科目", "年级", "班级", "权限【班】", "权限【年级】", "权限【科目】"};
                    for (int i = 0; i < excelTitle.length; i++) {
                        Label teacherCell = new Label(i, 1, excelTitle[i]);
                        sheet.addCell(teacherCell);
                    }
                    for (int j = 0; j < dataList.size(); j++) {
                        Object[] t = dataList.get(j);
                        for (int z = 0; z < excelTitle.length; z++) {
                            String cellVal = String.valueOf(t[z]);
                            Label oneCell = new Label(z, j + 2, "null".equals(cellVal) ? "" : cellVal);
                            sheet.addCell(oneCell);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wwBook.write();
                if (wwBook != null) {
                    try {
                        wwBook.close();
                    } catch (WriteException e2) {
                        e2.printStackTrace();
                    }
                }
            } catch (Exception e3) {
                if (wwBook != null) {
                    try {
                        wwBook.close();
                    } catch (WriteException e4) {
                        e4.printStackTrace();
                    }
                }
            } catch (Throwable th) {
                if (wwBook != null) {
                    try {
                        wwBook.close();
                    } catch (WriteException e5) {
                        e5.printStackTrace();
                        throw th;
                    }
                }
                throw th;
            }
        } catch (Exception e6) {
            e6.printStackTrace();
        }
        return excelFile.getName();
    }

    public List<Object[]> getExportTeacherData(String leicengId, String schoolNum, String gradeNum, String subjectName, String searchNum, String searchName, String searchsex, String searchtitle, String userId, String result, String positionName, String phone) {
        String sql;
        String schStr2 = "";
        if (!"-1".equals(schoolNum)) {
            schStr2 = " and schoolNum={schoolNum} ";
        }
        String graStr = "";
        if (!"-1".equals(gradeNum)) {
            graStr = " and u.gradeNum={gradeNum} ";
        }
        String subStr3 = "";
        if (null != subjectName && !"".equals(subjectName) && !"-1".equals(subjectName)) {
            subStr3 = " and u.subjectNum = {subjectName} ";
        }
        String poiStr = "";
        if (null != positionName && !"".equals(positionName) && !"-1".equals(positionName)) {
            poiStr = " and u.type = {positionName} ";
        }
        String sql2 = (((((" SELECT t.teacherNum,t.teacherName,t.sex,t.title,t.birthday,t.worktime,sch.schoolName,t.mobile,t.email,'' ext1, d.name,s.subjectName,g.gradeName,GROUP_CONCAT(if(g.islevel='1',cl.className,c.className) order by convert(if(g.islevel='1',cl.className,c.className) using gbk)) className,max(u.permission_class),max(u.permission_grade),max(u.permission_subject),u.type,t.schoolNum FROM  (select teacherNum,teacherName,id,title,sex,schoolNum,isDelete,birthday,worktime,mobile,email from teacher where 1=1" + schStr2) + ("".equals(searchNum) ? "" : " and teacherNum LIKE {searchNum} ")) + ("".equals(searchName) ? "" : " and teacherName LIKE {searchName} ")) + ("".equals(searchsex) ? "" : " and sex LIKE {searchsex} ")) + ("".equals(searchtitle) ? "" : " and title LIKE {searchtitle} ")) + " ) t  LEFT JOIN user us ON t.id = us.userid AND  t.isDelete='F'  LEFT JOIN  userposition u  ON us.id = u.userNum  LEFT JOIN data d ON d.value = u.type and d.type = '31'  LEFT JOIN grade g ON u.gradeNum = g.gradeNum AND g.schoolNum = u.schoolNum AND g.isDelete='F'  LEFT JOIN `subject` s ON s.subjectNum = u.subjectNum  LEFT JOIN class c ON c.id = u.classNum AND c.isDelete='F'  LEFT JOIN levelclass cl ON cl.id = u.classNum AND cl.isDelete='F'  LEFT JOIN school  sch ON sch.id  = t.schoolNum ";
        boolean f = true;
        if ("-1".equals(schoolNum)) {
            if (null != leicengId && !"".equals(leicengId)) {
                sql2 = sql2 + " left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leicengId} group by sItemId) ss on ss.id = sch.id  where sch.isDelete='F' and ss.id is not null ";
                f = false;
            } else if (!userId.equals("-1") && !userId.equals("-2")) {
                sql2 = sql2 + " left join schauthormanage sa on sa.schoolNum = sch.id and sa.userId={userId}   left join user te on te.schoolNum = sch.id  and te.id = {userId} and te.usertype=1   where sch.isDelete='F' and (sa.schoolNum is not null or te.schoolNum is not null) ";
                f = false;
            }
        }
        if (f) {
            sql = sql2 + " where us.usertype = '1' " + poiStr + graStr + subStr3;
        } else {
            sql = sql2 + " and us.usertype = '1' " + poiStr + graStr + subStr3;
        }
        String sql3 = ((sql + ("".equals(phone) ? "" : " and us.mobile LIKE {phone} ")) + " GROUP BY t.teacherNum,u.type,u.subjectNum,u.gradeNum  ") + " ORDER BY -sch.schoolName,-t.schoolNum,t.teacherNum*1 ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("subjectName", (Object) subjectName).put("positionName", (Object) positionName).put("searchNum", (Object) ("%" + searchNum + "%")).put("searchName", (Object) ("%" + searchName + "%")).put("phone", (Object) ("%" + phone + "%")).put("searchsex", (Object) ("%" + searchsex + "%")).put("'searchtitle", (Object) ("%" + searchtitle + "%")).put("leicengId", (Object) leicengId).put("userId", (Object) userId);
        return this.dao2._queryArrayList(sql3, args);
    }

    public List<Object[]> getExportTeacherDataPost(String leicengId, String schoolNum, String gradeNum, String subjectName, String searchNum, String searchName, String searchsex, String searchtitle, String userId, String result, String positionName, String phone) {
        String sql;
        String schStr2 = "";
        if (!"-1".equals(schoolNum)) {
            schStr2 = " and schoolNum={schoolNum} ";
        }
        String graStr = "";
        if (!"-1".equals(gradeNum)) {
            graStr = " and u.gradeNum={gradeNum} ";
        }
        String subStr3 = "";
        if (null != subjectName && !"".equals(subjectName) && !"-1".equals(subjectName)) {
            subStr3 = " and u.subjectNum = {subjectName} ";
        }
        String poiStr = "";
        if (null != positionName && !"".equals(positionName) && !"-1".equals(positionName)) {
            poiStr = " and u.type = {positionName} ";
        }
        String sql2 = (((((" SELECT t.teacherNum,t.teacherName,t.sex,t.title,t.birthday,t.worktime,sch.schoolName,t.mobile,t.email,'' ext1, d.name,s.subjectName,g.gradeName,if(g.islevel='1',cl.className,c.className) className, u.permission_class,u.permission_grade,u.permission_subject,u.type,t.schoolNum FROM  (select teacherNum,teacherName,id,title,sex,schoolNum,isDelete,birthday,worktime,mobile,email from teacher where 1=1" + schStr2) + ("".equals(searchNum) ? "" : " and teacherNum LIKE {searchNum} ")) + ("".equals(searchName) ? "" : " and teacherName LIKE {searchName} ")) + ("".equals(searchsex) ? "" : " and sex LIKE {searchsex} ")) + ("".equals(searchtitle) ? "" : " and title LIKE {searchtitle} ")) + " ) t  LEFT JOIN user us ON t.id = us.userid AND  t.isDelete='F'  LEFT JOIN  userposition u  ON us.id = u.userNum  LEFT JOIN data d ON d.value = u.type and d.type = '31'  LEFT JOIN grade g ON u.gradeNum = g.gradeNum AND g.schoolNum = u.schoolNum AND g.isDelete='F'  LEFT JOIN `subject` s ON s.subjectNum = u.subjectNum  LEFT JOIN class c ON c.id = u.classNum AND c.isDelete='F'  LEFT JOIN levelclass cl ON cl.id = u.classNum AND cl.isDelete='F'  LEFT JOIN school  sch ON sch.id  = t.schoolNum ";
        boolean f = true;
        if ("-1".equals(schoolNum)) {
            if (null != leicengId && !"".equals(leicengId)) {
                sql2 = sql2 + " left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leicengId} group by sItemId) ss on ss.id = sch.id  where sch.isDelete='F' and ss.id is not null ";
                f = false;
            } else if (!userId.equals("-1") && !userId.equals("-2")) {
                sql2 = sql2 + " left join schauthormanage sa on sa.schoolNum = sch.id and sa.userId={userId}   left join user te on te.schoolNum = sch.id  and te.id = {userId} and te.usertype=1   where sch.isDelete='F' and (sa.schoolNum is not null or te.schoolNum is not null) ";
                f = false;
            }
        }
        if (f) {
            sql = sql2 + " where us.usertype = '1' " + poiStr + graStr + subStr3;
        } else {
            sql = sql2 + " and us.usertype = '1' " + poiStr + graStr + subStr3;
        }
        String sql3 = ((sql + ("".equals(phone) ? "" : " and us.mobile LIKE {phone} ")) + " GROUP BY t.teacherNum,u.type,u.subjectNum,u.gradeNum,u.classNum  ") + " ORDER BY -sch.schoolName,-t.schoolNum,t.teacherNum*1 ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("subjectName", (Object) subjectName).put("positionName", (Object) positionName).put("searchNum", (Object) ("%" + searchNum + "%")).put("searchName", (Object) ("%" + searchName + "%")).put("phone", (Object) ("%" + phone + "%")).put("searchsex", (Object) ("%" + searchsex + "%")).put("'searchtitle", (Object) ("%" + searchtitle + "%")).put("leicengId", (Object) leicengId).put("userId", (Object) userId);
        return this.dao2._queryArrayList(sql3, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void addUserRole(String userNum, String roleNum, String insertUser) {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put("roleNum", (Object) roleNum).put("insertUser", (Object) insertUser);
        this.dao2._execute("insert into userrole (userNum,roleNum,insertUser,insertDate) values ({userNum},{roleNum},{insertUser},now()) ON DUPLICATE KEY UPDATE insertUser={insertUser},insertDate=now()", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Map<String, Object>> getSchoolInfo(String usertype, String leiceng, String userId) {
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leiceng != null) {
            leicengStr1 = leicengStr1 + " left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leiceng} group by sItemId) ss on ss.id = u.schoolNum ";
            leicengStr2 = " and ss.id is not null ";
        }
        String sql = "select distinct u.schoolNum,s.schoolName from user u left join school s on u.schoolNum = s.id left join schauthormanage sth on u.schoolNum = sth.schoolNum and sth.userId= {userId} " + leicengStr1 + "where u.isDelete='F' and s.isDelete = 'F' and u.usertype= {usertype} " + leicengStr2 + "order by length(u.schoolNum),u.schoolNum ";
        Map args = StreamMap.create().put("leiceng", (Object) leiceng).put("userId", (Object) userId).put("usertype", (Object) usertype);
        return this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Map<String, Object>> getLoginCountList(String usertype, String leiceng, String school, String gradeNum, String classNum, String start, String end, String userId) {
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leiceng != null && (school == null || school.equals("-1"))) {
            leicengStr1 = leicengStr1 + " left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leiceng} group by sItemId) ss on ss.id = u.schoolNum ";
            leicengStr2 = " and ss.id is not null ";
        }
        String schoolStr = "";
        if (school != null && !school.equals("") && !school.equals("-1")) {
            schoolStr = " and u.schoolNum={school} ";
        }
        String gradeStr = "";
        if (gradeNum != null && !gradeNum.equals("") && !gradeNum.equals("-1")) {
            gradeStr = " and s.gradeNum={gradeNum} ";
        }
        String classStr = "";
        if (classNum != null && !classNum.equals("") && !classNum.equals("-1")) {
            classStr = " and s.classNum={classNum} ";
        }
        String stratStr = "";
        if (start != null && !start.equals("")) {
            stratStr = " and l.insertDate > {start} ";
        }
        String endStr = "";
        if (end != null && !end.equals("")) {
            endStr = " and l.insertDate < {end} ";
        }
        String userTypeStr2 = "";
        String userTypeStr1 = "";
        String userTypeStr3 = "";
        if (usertype != null && usertype.equals("1")) {
            userTypeStr1 = "left join user u on l.insertUser = u.id AND u.usertype={usertype} ";
        } else if (usertype != null && (usertype.equals("2") || usertype.equals("3"))) {
            userTypeStr1 = "left join user u on l.insertUser = u.id AND u.usertype={usertype} ";
            userTypeStr2 = "left join student s on u.userid = s.id  LEFT JOIN class c ON s.classNum = c.id left join grade g on s.gradeNum=g.gradeNum and s.jie = g.jie ";
            userTypeStr3 = " and c.isDelete='F' and g.isDelete='F' ";
            if (usertype.equals("3")) {
                userTypeStr1 = "left join parentUser u on l.insertUser = u.id AND u.usertype={usertype} ";
            }
        }
        String userStr1 = "";
        String userStr2 = "";
        if (userId != null && !userId.equals("-1") && !userId.equals("-2")) {
            userStr1 = "left join schauthormanage sch on u.schoolNum = sch.schoolNum and sch.userId = {userId} ";
            userStr2 = " and sch.schoolNum is not null ";
        }
        String sql = "SELECT ifnull(sc.schoolName,'--')schoolName,";
        if (!usertype.equals("1")) {
            sql = sql + "c.className,";
        }
        String sql2 = sql + "u.realname,u.id,MAX(l.insertDate)lastDate,COUNT(l.id) dlcs FROM log l " + userTypeStr1 + userTypeStr2 + userStr1 + leicengStr1 + " LEFT JOIN school sc ON u.schoolnum = sc.id  where l.operate='登录系统' and u.isDelete='F' " + schoolStr + classStr + userStr2 + leicengStr2 + userTypeStr3 + stratStr + endStr + gradeStr + " GROUP BY l.insertUser,u.schoolnum ORDER BY LENGTH(sc.id),sc.id,LENGTH(u.id),u.id ";
        Map args = StreamMap.create().put("leiceng", (Object) leiceng).put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("start", (Object) start).put("end", (Object) end).put("usertype", (Object) usertype).put("userId", (Object) userId);
        return this.dao2._queryMapList(sql2, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Map<String, Object>> getClassInfo(String leiceng, String school, String grade) {
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leiceng != null && (school == null || school.equals("-1"))) {
            leicengStr1 = leicengStr1 + " left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leiceng} group by sItemId) ss on ss.id = c.schoolNum ";
            leicengStr2 = " and ss.id is not null ";
        }
        String schoolStr = "";
        if (school != null && !school.equals("") && !school.equals("-1")) {
            schoolStr = " and c.schoolNum={school} ";
        }
        String gradeStr = "";
        if (grade != null && !grade.equals("") && !grade.equals("-1")) {
            gradeStr = " and c.gradeNum={grade} ";
        }
        String sql = "select c.id,c.className  from class c " + leicengStr1 + "where c.isDelete='F' " + schoolStr + " " + gradeStr + leicengStr2 + "order by length(c.classNum),c.classNum";
        Map args = StreamMap.create().put("leiceng", (Object) leiceng).put(License.SCHOOL, (Object) school).put("grade", (Object) grade);
        return this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public List<Map<String, Object>> getGradeInfo(String leiceng, String school) {
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leiceng != null && (school == null || school.equals("-1"))) {
            leicengStr1 = leicengStr1 + " left join (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leiceng} group by sItemId) ss on ss.id = g.schoolNum ";
            leicengStr2 = " and ss.id is not null ";
        }
        String schoolStr = "";
        if (school != null && !school.equals("") && !school.equals("-1")) {
            schoolStr = " and g.schoolNum={school} ";
        }
        String sql = "select distinct g.gradeNum,g.gradeName from grade g " + leicengStr1 + "where g.isDelete='F' " + schoolStr + " " + leicengStr2 + "order by length(g.gradeNum),g.gradeNum";
        Map args = StreamMap.create().put("leiceng", (Object) leiceng).put(License.SCHOOL, (Object) school);
        return this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void deleteUserposition(String teacherId, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("teacherId", (Object) teacherId);
        this.dao2._execute("delete from userposition where schoolNum={schoolNum} and userNum= (select id from user where userid={teacherId} and usertype='1')", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void resetTeaPw(List<Teacher> tList, String[] uids, String _pwd) {
        if (null != tList) {
            for (int j = 0; j < tList.size(); j++) {
                Teacher tea = tList.get(j);
                this.userDao.updatepasword(tea.getExt4(), _pwd);
            }
            return;
        }
        if (null != uids) {
            for (String str : uids) {
                this.userDao.updatepasword(str, _pwd);
            }
        }
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public void bacthdeluserposition(List<Teacher> tList, String[] uids, String gradeNum, String subjectNum, String position) {
        String typeStr = "-1".equals(position) ? "" : " and type={position} ";
        String graStr = "-1".equals(gradeNum) ? "" : " and gradeNum={gradeNum} ";
        String subStr = "-1".equals(subjectNum) ? "" : " and subjectNum={subjectNum} ";
        Map args = StreamMap.create().put("position", (Object) position).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        if (null != tList) {
            for (int i = 0; i < tList.size(); i++) {
                Teacher t = tList.get(i);
                args.put("userNum", t.getExt4());
                String delSql = "delete from userposition where userNum = {userNum}" + typeStr + graStr + subStr;
                this.dao2._execute(delSql, args);
            }
            return;
        }
        if (null != uids) {
            for (String str : uids) {
                args.put("userNum", str);
                String delSql2 = "delete from userposition where userNum = {userNum}" + typeStr + graStr + subStr;
                this.dao2._execute(delSql2, args);
            }
        }
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public Object getChangeSchool(String teacherNum) {
        Map args = StreamMap.create().put("teacherNum", (Object) teacherNum);
        return this.dao2._queryObject("select schoolNum from changeSchool_teacher where status='0' and teacherNum={teacherNum}", args);
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getIsSchoolScanper(String userId) {
        Map args = StreamMap.create().put("userId", (Object) userId);
        Map<String, Object> map1 = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} limit 1", args);
        Map<String, Object> map2 = this.dao2._querySimpleMap("select id from userposition where userNum={userId}  and type=0 limit 1", args);
        if (null != map1 && null != map2) {
            return "3";
        }
        if (null != map1) {
            return "1";
        }
        if (null != map2) {
            return "2";
        }
        return "0";
    }

    @Override // com.dmj.service.systemManagement.TeacherManageService
    public String getUserName(String uid) {
        Map args = StreamMap.create().put("uid", (Object) uid);
        return this.dao2._queryStr("select realName from user where id={uid}", args);
    }
}
