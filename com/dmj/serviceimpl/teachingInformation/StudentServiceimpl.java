package com.dmj.serviceimpl.teachingInformation;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.userManagement.UserDAOImpl;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Basegrade;
import com.dmj.domain.Baseinfolog;
import com.dmj.domain.ChangeSchoolStudent;
import com.dmj.domain.Class;
import com.dmj.domain.Data;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Grade;
import com.dmj.domain.Levelclass;
import com.dmj.domain.Levelstudent;
import com.dmj.domain.School;
import com.dmj.domain.Student;
import com.dmj.domain.Subject;
import com.dmj.domain.SubjectCombine;
import com.dmj.domain.Teacher;
import com.dmj.domain.User;
import com.dmj.domain.Userposition;
import com.dmj.domain.Userrole;
import com.dmj.service.teachingInformation.StudentService;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.msg.RspMsg;
import com.zht.db.RowArg;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONArray;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;

/* loaded from: StudentServiceimpl.class */
public class StudentServiceimpl implements StudentService {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    UserDAOImpl userDao = new UserDAOImpl();

    @Override // com.dmj.service.teachingInformation.StudentService
    public Object[] getStudent(Map<String, String> map, Map jie_map, String userId) {
        String count;
        Map args2 = new HashMap();
        args2.put("userId", userId);
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String leiceng = map.get("leicengId");
        String position = map.get("position");
        String sql = "";
        String strSchauthormanage = "";
        String statisticitem = "";
        String strPosition = "";
        String sch = "";
        String schNum = "";
        String jieInfo = "";
        String leicengStr = "";
        Map<String, Object> ismanageMap = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args2);
        if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2") && null == ismanageMap) {
            strSchauthormanage = " left join schoolscanpermission s on s.schoolNum = sl.id and s.userNum={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ";
            sch = "  and (s.schoolNum is not null or t.schoolNum is not null) ";
        }
        if (!"-1".equals(schoolNum)) {
            schNum = " and st.schoolNum={schoolNum}";
        } else if (!leiceng.equals("")) {
            leicengStr = leicengStr + " and ss.sItemId is not null";
        }
        if ("-1".equals(schoolNum) && !leiceng.equals("")) {
            statisticitem = " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId={leiceng}) ss on st.schoolNum = ss.sItemId";
        }
        String graNum = "";
        if (!"-1".equals(map.get(Const.EXPORTREPORT_gradeNum))) {
            graNum = " and st.gradeNum={gradeNum}";
            jieInfo = " and st.jie={jie}";
        }
        String classNum = "";
        if (!"-1".equals(map.get(Const.EXPORTREPORT_classNum))) {
            classNum = " and st.classNum={classNum}";
        }
        String subjectCombineNum = "";
        if (!"-1".equals(map.get("subjectCombineNum"))) {
            subjectCombineNum = " and st.subjectCombineNum={subjectCombineNum}";
        }
        String sex = "";
        if (!"-1".equals(map.get("sex"))) {
            sex = " and st.sex={sex}";
        }
        String studentName = "";
        if (null != map.get("studentName") && !"".equals(map.get("studentName"))) {
            studentName = " and st.studentName like {studentName}";
        }
        String stuID = "";
        if (null != map.get("stuID") && !"".equals(map.get("stuID"))) {
            stuID = " and (st.studentid={stuID} or st.studentNum={stuID})";
        }
        String note = "";
        if (null != map.get("description") && !"".equals(map.get("description"))) {
            note = " and st.description like {description}";
        }
        if (position.indexOf("5") != -1 || position.indexOf("0") != -1 || userId.equals("-1") || userId.equals("-2") || position.equals("999")) {
            if (!userId.equals("-1") && !userId.equals("-2") && position.indexOf("0") == -1 && !position.equals("999")) {
                strPosition = strPosition + " inner JOIN(   SELECT schoolnum from userposition WHERE  userNum = {userId}  and (type=0 or type=5)  )u on u.schoolnum = st.schoolNum ";
            }
            sql = "SELECT uu.id ext4,st.id as id , sl.schoolName as schoolName,st.schoolNum as schoolNum ,g.gradeName as gradeName ,g.gradeNum , c.className as className,st.studentName,st.studentID ,st.sex,st.studentNum,st.description,st.note,st.yzexaminationnum,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext1  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + strPosition + " LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum   LEFT JOIN levelstudent ls on st.id=ls.sid      LEFT JOIN levelclass lc on lc.id=ls.classNum      INNER JOIN user uu ON st.id=uu.userid   and uu.userType=2  LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum   ";
        } else if (position.indexOf("3") != -1) {
            String str = " union  SELECT uu.id ext4,st.id as id , sl.schoolName as schoolName,st.schoolNum as schoolNum ,g.gradeName as gradeName ,g.gradeNum , c.className as className,st.studentName,st.studentID ,st.sex,st.studentNum,st.description,st.note,st.yzexaminationnum,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext1  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT schoolnum,gradeNum,classNum from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum   LEFT JOIN levelstudent ls on st.id=ls.sid      LEFT JOIN levelclass lc on lc.id=ls.classNum      INNER JOIN user uu ON st.id=uu.userid   and uu.userType=2  LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum   ";
            sql = "SELECT uu.id ext4,st.id as id , sl.schoolName as schoolName,st.schoolNum as schoolNum ,g.gradeName as gradeName ,g.gradeNum , c.className as className,st.studentName,st.studentID ,st.sex,st.studentNum,st.description,st.note,st.yzexaminationnum,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext1  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + (strPosition + " inner JOIN(   SELECT schoolnum,gradeNum from userposition WHERE  userNum = {userId}  and type=3  )u on u.schoolnum = st.schoolNum  and u.gradeNum = st.gradeNum") + " LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum   LEFT JOIN levelstudent ls on st.id=ls.sid      LEFT JOIN levelclass lc on lc.id=ls.classNum      INNER JOIN user uu ON st.id=uu.userid   and uu.userType=2  LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum   ";
            List<Userposition> up = getPositionByuser(userId);
            for (int i = 0; i < up.size(); i++) {
                String type = up.get(i).getType();
                String gra = up.get(i).getGradeNum();
                if (gra.indexOf(map.get(Const.EXPORTREPORT_gradeNum)) != -1 && "2".equals(type)) {
                    sql = "SELECT uu.id ext4,st.id as id , sl.schoolName as schoolName,st.schoolNum as schoolNum ,g.gradeName as gradeName ,g.gradeNum , c.className as className,st.studentName,st.studentID ,st.sex,st.studentNum,st.description,st.note,st.yzexaminationnum,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext1  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT schoolnum,gradeNum,classNum from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum   LEFT JOIN levelstudent ls on st.id=ls.sid      LEFT JOIN levelclass lc on lc.id=ls.classNum      INNER JOIN user uu ON st.id=uu.userid   and uu.userType=2  LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum   ";
                }
            }
        } else if (position.indexOf("2") != -1) {
            sql = "SELECT uu.id ext4,st.id as id , sl.schoolName as schoolName,st.schoolNum as schoolNum ,g.gradeName as gradeName ,g.gradeNum , c.className as className,st.studentName,st.studentID ,st.sex,st.studentNum,st.description,st.note,st.yzexaminationnum,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext1  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT schoolnum,gradeNum,classNum from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum   LEFT JOIN levelstudent ls on st.id=ls.sid      LEFT JOIN levelclass lc on lc.id=ls.classNum      INNER JOIN user uu ON st.id=uu.userid   and uu.userType=2  LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum   ";
        }
        String sql2 = sql + " where st.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' and st.nodel=0 " + leicengStr + sch + schNum + graNum + jieInfo + classNum + subjectCombineNum + sex + studentName + stuID + note + " GROUP BY st.studentId   order by st.schoolNum*1, st.gradeNum*0.1 ASC  ";
        Map args_sql = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("leiceng", (Object) leiceng).put(Const.EXPORTREPORT_gradeNum, (Object) map.get(Const.EXPORTREPORT_gradeNum)).put("jie", jie_map.get(map.get(Const.EXPORTREPORT_gradeNum))).put(Const.EXPORTREPORT_classNum, (Object) map.get(Const.EXPORTREPORT_classNum)).put("subjectCombineNum", (Object) map.get("subjectCombineNum")).put("sex", (Object) map.get("sex")).put("studentName", (Object) ("%" + map.get("studentName") + "%")).put("stuID", (Object) map.get("stuID")).put("description", (Object) ("%" + map.get("description") + "%"));
        if ("0".equals(map.get("count"))) {
            count = this.dao2._queryBeanList(sql2, Levelstudent.class, args_sql).size() + "";
        } else {
            count = map.get("count");
        }
        Integer.valueOf(map.get("index")).intValue();
        Integer.valueOf(map.get("pageSize")).intValue();
        List list = this.dao2._queryBeanList(sql2.toString(), Student.class, args_sql, Integer.valueOf(map.get("index")).intValue(), Integer.valueOf(map.get("pageSize")).intValue());
        return new Object[]{count, list};
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void resetStuPw(List<Student> stuList, String _pwd) {
        for (int j = 0; j < stuList.size(); j++) {
            Student stu = stuList.get(j);
            this.userDao.updatepasword(stu.getExt4(), _pwd);
        }
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getGradeIslevel(Map<String, String> map, Map jie_map, String userId) {
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String jie = String.valueOf(jie_map.get(gradeNum));
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        String str = String.valueOf(this.dao2._queryObject("select MAX(islevel) from grade where gradeNum={gradeNum} and jie ={jie}", args_sql));
        return str;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Object[] getStudentLevel(Map<String, String> map, Map jie_map, String userId) {
        String count;
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String leiceng = map.get("leicengId");
        String position = map.get("position");
        String sql = "";
        String strSchauthormanage = "";
        String statisticitem = "";
        String strPosition = "";
        String sch = "";
        String schNum = "";
        String jieInfo = "";
        String leicengStr = "";
        if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2")) {
            strSchauthormanage = " left join schauthormanage s on s.schoolNum = sl.id and s.userId={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ";
            sch = "  and (s.schoolNum is not null or t.schoolNum is not null) ";
        }
        if (!"-1".equals(schoolNum)) {
            schNum = " and st.schoolNum={schoolNum}";
        } else if (!leiceng.equals("")) {
            leicengStr = leicengStr + " and ss.sItemId is not null";
        }
        if ("-1".equals(schoolNum) && !leiceng.equals("")) {
            statisticitem = " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId={leiceng}) ss on st.schoolNum = ss.sItemId";
        }
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String graNum = "";
        if (!"-1".equals(gradeNum)) {
            graNum = " and les.gradeNum={gradeNum}";
        }
        if (null != jie_map.get(gradeNum) && !jie_map.get(gradeNum).equals("-1") && !jie_map.get(gradeNum).equals("") && !jie_map.get(gradeNum).equals("null")) {
            jieInfo = "and les.jie={jie}  ";
        }
        String subjectNum = "";
        if (!"-1".equals(map.get(Const.EXPORTREPORT_subjectNum))) {
            subjectNum = " and les.subjectNum={subjectNum}";
        }
        String classNum = "";
        if (!"-1".equals(map.get(Const.EXPORTREPORT_classNum))) {
            classNum = " and les.classNum={classNum}";
        }
        String xuankaoNum = "";
        if (!"-1".equals(map.get("xuankaoNum")) && !"".equals(map.get("xuankaoNum"))) {
            xuankaoNum = " and les.xuankaoqufen={xuankaoqufen}";
        } else if ("".equals(map.get("xuankaoNum"))) {
            xuankaoNum = " and les.xuankaoqufen is null";
        }
        String sex = "";
        if (!"-1".equals(map.get("sex"))) {
            sex = " and st.sex={sex}";
        }
        String studentName = "";
        if (null != map.get("studentName") && !"".equals(map.get("studentName"))) {
            studentName = " and st.studentName like {studentName}";
        }
        String stuID = "";
        if (null != map.get("stuID") && !"".equals(map.get("stuID"))) {
            stuID = " and st.studentid={stuID}";
        }
        String note = "";
        if (null != map.get("description") && !"".equals(map.get("description"))) {
            note = " and les.description like {description}";
        }
        if (position.indexOf("5") != -1 || position.indexOf("0") != -1 || userId.equals("-1") || userId.equals("-2") || position.equals("999")) {
            if (!userId.equals("-1") && !userId.equals("-2") && position.indexOf("0") == -1 && !position.equals("999")) {
                strPosition = strPosition + " inner JOIN(   SELECT * from userposition WHERE  userNum = {userId}  and (type=0 or type=5)  )u on u.schoolnum = st.schoolNum ";
            }
            sql = "SELECT les.id , les.sid ,sl.schoolName ,les.jie,les.schoolNum , g.gradeName ,g.gradeNum , lec.className,les.classNum , st.studentName ,su.subjectName ,su.subjectNum,st.studentID ,st.studentNum,st.sex,les.description,les.xuankaoqufen  FROM levelstudent les LEFT JOIN student st ON st.id=les.sid LEFT JOIN school sl ON les.schoolNum=sl.id LEFT JOIN grade g ON les.gradeNum=g.gradeNum  AND les.schoolNum=g.schoolNum and g.islevel=1 LEFT JOIN levelclass lec ON les.classNum=lec.id LEFT JOIN `subject` su ON les.subjectNum=su.subjectNum " + strSchauthormanage + statisticitem + strPosition + "WHERE st.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and lec.isDelete='F' " + leicengStr + sch + schNum + graNum + subjectNum + classNum + xuankaoNum + sex + studentName + stuID + note + " order by st.schoolNum*1, st.gradeNum*0.1";
        } else if (position.indexOf("3") != -1) {
            String sql1 = " union  SELECT les.id , les.sid ,sl.schoolName ,les.jie,les.schoolNum , g.gradeName ,g.gradeNum , lec.className,les.classNum , st.studentName ,su.subjectName ,su.subjectNum,st.studentID ,st.studentNum,st.sex,les.description,les.xuankaoqufen  FROM levelstudent les LEFT JOIN student st ON st.id=les.sid LEFT JOIN school sl ON les.schoolNum=sl.id LEFT JOIN grade g ON les.gradeNum=g.gradeNum  AND les.schoolNum=g.schoolNum and g.islevel=1 LEFT JOIN levelclass lec ON les.classNum=lec.id LEFT JOIN `subject` su ON les.subjectNum=su.subjectNum " + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT * from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum WHERE st.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and lec.isDelete='F' " + leicengStr + sch + schNum + graNum + subjectNum + classNum + xuankaoNum + sex + studentName + stuID + note;
            sql = "SELECT les.id , les.sid ,sl.schoolName ,les.jie,les.schoolNum , g.gradeName ,g.gradeNum , lec.className,les.classNum , st.studentName ,su.subjectName ,su.subjectNum,st.studentID ,st.studentNum,st.sex,les.description,les.xuankaoqufen  FROM levelstudent les LEFT JOIN student st ON st.id=les.sid LEFT JOIN school sl ON les.schoolNum=sl.id LEFT JOIN grade g ON les.gradeNum=g.gradeNum  AND les.schoolNum=g.schoolNum and g.islevel=1 LEFT JOIN levelclass lec ON les.classNum=lec.id LEFT JOIN `subject` su ON les.subjectNum=su.subjectNum " + strSchauthormanage + statisticitem + (strPosition + " inner JOIN(   SELECT * from userposition WHERE  userNum = {userId}  and type=3  )u on u.schoolnum = st.schoolNum  and u.gradeNum = st.gradeNum") + "WHERE st.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and lec.isDelete='F' " + leicengStr + sch + schNum + graNum + subjectNum + classNum + xuankaoNum + sex + studentName + stuID + note + sql1;
            List<Userposition> up = getPositionByuser(userId);
            for (int i = 0; i < up.size(); i++) {
                String type = up.get(i).getType();
                String gra = up.get(i).getGradeNum();
                if (gra.indexOf(gradeNum) != -1 && "2".equals(type)) {
                    sql = " SELECT les.id , les.sid ,sl.schoolName ,les.jie,les.schoolNum , g.gradeName ,g.gradeNum , lec.className,les.classNum , st.studentName ,su.subjectName ,su.subjectNum,st.studentID ,st.studentNum,st.sex,les.description,les.xuankaoqufen  FROM levelstudent les LEFT JOIN student st ON st.id=les.sid LEFT JOIN school sl ON les.schoolNum=sl.id LEFT JOIN grade g ON les.gradeNum=g.gradeNum  AND les.schoolNum=g.schoolNum and g.islevel=1 LEFT JOIN levelclass lec ON les.classNum=lec.id LEFT JOIN `subject` su ON les.subjectNum=su.subjectNum " + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT * from userposition WHERE userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum WHERE st.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and lec.isDelete='F' " + leicengStr + sch + schNum + graNum + subjectNum + classNum + xuankaoNum + sex + studentName + stuID + note + "  order by st.schoolNum*1, st.gradeNum*0.1";
                }
            }
        } else if (position.indexOf("2") != -1) {
            sql = " SELECT les.id , les.sid ,sl.schoolName ,les.jie,les.schoolNum , g.gradeName ,g.gradeNum , lec.className,les.classNum , st.studentName ,su.subjectName ,su.subjectNum,st.studentID ,st.studentNum,st.sex,les.description,les.xuankaoqufen  FROM levelstudent les LEFT JOIN student st ON st.id=les.sid LEFT JOIN school sl ON les.schoolNum=sl.id LEFT JOIN grade g ON les.gradeNum=g.gradeNum  AND les.schoolNum=g.schoolNum and g.islevel=1 LEFT JOIN levelclass lec ON les.classNum=lec.id LEFT JOIN `subject` su ON les.subjectNum=su.subjectNum " + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT * from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum WHERE st.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and lec.isDelete='F' " + leicengStr + sch + schNum + graNum + subjectNum + classNum + xuankaoNum + sex + studentName + stuID + note + "  order by st.schoolNum*1, st.gradeNum*0.1";
        }
        Map args_sql = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("leiceng", (Object) leiceng).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", jie_map.get(gradeNum)).put(Const.EXPORTREPORT_subjectNum, (Object) map.get(Const.EXPORTREPORT_subjectNum)).put(Const.EXPORTREPORT_classNum, (Object) map.get(Const.EXPORTREPORT_classNum)).put("xuankaoqufen", (Object) map.get("xuankaoNum")).put("sex", (Object) map.get("sex")).put("studentName", (Object) ("%" + map.get("studentName") + "%")).put("stuID", (Object) map.get("stuID")).put("description", (Object) ("%" + map.get("description") + "%"));
        String sqlAll = ("select les.id , les.sid ,les.jie,les.schoolName ,les.schoolNum , les.gradeName ,les.gradeNum , les.className,les.classNum , les.studentName ,les.subjectName ,les.subjectNum,les.studentID ,les.studentNum,les.description,d.name as ext1,les.xuankaoqufen,les.sex ext2 from (" + sql) + "  )les  left join (  select * from data d where type=32  )d on d.value=les.xuankaoqufen ";
        if ("0".equals(map.get("count"))) {
            count = this.dao2._queryBeanList(sqlAll, Levelstudent.class, args_sql).size() + "";
        } else {
            count = map.get("count");
        }
        Integer.valueOf(map.get("index")).intValue();
        Integer.valueOf(map.get("pageSize")).intValue();
        List list = this.dao2._queryBeanList(sqlAll.toString() + jieInfo, Levelstudent.class, args_sql, Integer.valueOf(map.get("index")).intValue(), Integer.valueOf(map.get("pageSize")).intValue());
        return new Object[]{count, list};
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getGradesBySchool(String school) {
        String schStr = "-1".equals(school) ? "" : " schoolNum ={school} and ";
        String schoolSQL = "select distinct gradeNum , gradeName from grade where " + schStr + "isDelete='F' order by gradeNum";
        Map args_schoolSQL = StreamMap.create().put(License.SCHOOL, (Object) school);
        return this.dao2._queryOrderMap(schoolSQL, TypeEnum.StringString, args_schoolSQL);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getGradesBySchool1(String school, String userId) {
        List<Userposition> up = getPositionByuser(userId);
        for (int i = 0; i < up.size(); i++) {
            String type = up.get(i).getType();
            if (type.indexOf("5") != -1) {
                return getGradesBySchool(school);
            }
            if (type.indexOf("3") != -1 || type.indexOf("2") != -1) {
                String schStr = "-1".equals(school) ? "" : " u.schoolnum={school} and ";
                String schoolSQL = "SELECT DISTINCT g.gradeNum,g.gradeName FROM  grade g  LEFT JOIN userposition u on u.gradeNum=g.gradeNum and u.schoolnum=g.schoolNum  where " + schStr + " u.userNum={userId} and u.type NOT IN(1,4)";
                Map args_schoolSQL = StreamMap.create().put(License.SCHOOL, (Object) school).put("userId", (Object) userId);
                return this.dao2._queryOrderMap(schoolSQL, TypeEnum.StringString, args_schoolSQL);
            }
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getSubjectMap(String school, String gradeNum) {
        Map args_sql = StreamMap.create().put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryOrderMap("SELECT lec.subjectNum subjectNum ,su.subjectName subjectName FROM levelclass lec LEFT JOIN `subject` su ON su.subjectNum=lec.subjectNum WHERE lec.schoolNum={school} AND lec.gradeNum={gradeNum}  ORDER BY lec.id ", TypeEnum.StringString, args_sql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer getLevelClassNumCount(String subjectNum, String gradeNum, String schoolNum, String classNum) {
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        return Convert.toInt(this.dao2._queryObject("SELECT COUNT(classNum) FROM levelclass WHERE subjectNum={subjectNum} AND gradeNum={gradeNum} AND schoolNum={schoolNum} AND classNum={classNum} ", args_sql), 0);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer getLevelClassNameCount(String subjectNum, String gradeNum, String schoolNum, String className) {
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("className", (Object) className);
        return Convert.toInt(this.dao2._queryObject("SELECT COUNT(className) FROM levelclass WHERE subjectNum={subjectNum} AND gradeNum={gradeNum} AND schoolNum={schoolNum} AND className={className} ", args_sql), 0);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String IsExistclass(String schoolNum, String gradeNum, String[] classNums) {
        String flag = "";
        for (String classNum : classNums) {
            Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum);
            Object o = this.dao2._queryObject("select classNum from class where schoolNum={schoolNum} and gradeNum={gradeNum} and classNum={classNum} AND isDelete='F' limit 0,1", args_sql);
            if (null != o) {
                flag = flag + o + Const.STRING_SEPERATOR;
            }
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getClassByGrade(String school, String grade, String classType, String jie) {
        String gradeSQL = "SELECT cl.id ,CONCAT(cl.className, CONCAT('(',d.`name`,')')) FROM class cl LEFT JOIN `data` d ON d.type='25' AND cl.studentType = d.`value`   WHERE cl.isDelete='F' AND cl.schoolNum={school} AND cl.gradeNum={grade} AND cl.jie={jie}   ORDER BY length(classNum), classNum";
        Map args_gradeSQL = StreamMap.create().put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("jie", (Object) jie);
        return this.dao2._queryOrderMap(gradeSQL, TypeEnum.StringString, args_gradeSQL);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getClassByGrade2(String school, String grade, String classType, String jie, String userId) {
        List<Userposition> up = getPositionByuser(userId);
        for (int i = 0; i < up.size(); i++) {
            String type = up.get(i).getType();
            String gra = up.get(i).getGradeNum();
            if (type.indexOf("5") != -1) {
                return getClassByGrade(school, grade, classType, jie);
            }
            if (type.indexOf("3") != -1 && gra.indexOf(grade) != -1) {
                return getClassByGrade(school, grade, classType, jie);
            }
            if (type.indexOf("2") != -1 && gra.indexOf(grade) != -1) {
                Map args_classSQL = StreamMap.create().put(License.SCHOOL, (Object) school).put("userId", (Object) userId).put("grade", (Object) grade).put("jie", (Object) jie);
                return this.dao2._queryOrderMap("SELECT DISTINCT(c.id) id,CONCAT(c.className, CONCAT('(',d.`name`,')')) FROM  class c  LEFT JOIN `data` d ON d.type='25' AND c.studentType = d.`value`  LEFT JOIN userposition u on u.gradeNum=c.gradeNum and u.schoolnum=c.schoolNum and u.classNum=c.id  where u.schoolnum={school} and u.userNum={userId} AND c.gradeNum={grade} AND c.jie={jie}  and c.isDelete='F'   ORDER BY length(c.classNum), c.classNum", TypeEnum.StringString, args_classSQL);
            }
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getClassByGrade1(String school, String grade, String classType, String jie, String stuType) {
        String classTypeString = "AND cl.studentType={classType}";
        if (classType == "-1") {
            classTypeString = "";
        }
        String gradeSQL = "SELECT cl.id ,CONCAT(cl.className, CONCAT('(',d.`name`,')')) FROM class cl LEFT JOIN `data` d ON d.type='25' AND cl.studentType = d.`value`   WHERE cl.isDelete='F' AND cl.schoolNum={school} AND cl.gradeNum={grade} AND cl.jie={jie}" + classTypeString + "   ORDER BY cl.studentType,length(classNum), classNum";
        Map args_gradeSQL = StreamMap.create().put("classType", (Object) classType).put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("jie", (Object) jie);
        return this.dao2._queryOrderMap(gradeSQL, TypeEnum.StringString, args_gradeSQL);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Object[]> getClassByGradeLeq(String school, String grade, String classType, String jie, String stuType) {
        Map args_gradeSQL = StreamMap.create().put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("jie", (Object) jie);
        return this.dao2._queryArrayList("SELECT cl.id ,CONCAT(cl.className, CONCAT('(',d.`name`,')')),cl.studentType FROM class cl LEFT JOIN `data` d ON d.type='25' AND cl.studentType = d.`value`   WHERE cl.isDelete='F' AND cl.schoolNum={school} AND cl.gradeNum={grade} AND cl.jie={jie} ORDER BY cl.studentType,length(classNum), classNum", args_gradeSQL);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getLevelClassByGrade(String school, String grade, String subject, String classType, String jie) {
        String gradeSQL = "SELECT cl.id ,CONCAT(cl.className, CONCAT('(',d.`name`,')')) FROM levelclass cl LEFT JOIN `data` d ON d.type='25' AND cl.studentType = d.`value`   WHERE cl.isDelete='F' AND cl.schoolNum={school} AND cl.gradeNum={grade} ";
        if (null != subject && !subject.equals("")) {
            gradeSQL = gradeSQL + "AND cl.subjectNum={subject}";
        }
        String gradeSQL2 = gradeSQL + " AND cl.jie={jie}  ORDER BY cl.studentType,length(classNum), classNum";
        Map args_gradeSQL = StreamMap.create().put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("subject", (Object) subject).put("jie", (Object) jie);
        return this.dao2._queryOrderMap(gradeSQL2, TypeEnum.StringString, args_gradeSQL);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Object[]> getLevelClassByGradeLeq(String school, String grade, String subject, String classType, String jie) {
        String gradeSQL = "SELECT cl.id ,CONCAT(cl.className, CONCAT('(',d.`name`,')')),cl.studentType FROM levelclass cl LEFT JOIN `data` d ON d.type='25' AND cl.studentType = d.`value`   WHERE cl.isDelete='F' AND cl.schoolNum={school} AND cl.gradeNum={grade} ";
        if (null != subject && !subject.equals("")) {
            gradeSQL = gradeSQL + "AND cl.subjectNum={subject}";
        }
        String gradeSQL2 = gradeSQL + " AND cl.jie={jie}  ORDER BY cl.studentType,length(classNum), classNum";
        Map args_gradeSQL = StreamMap.create().put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("subject", (Object) subject).put("jie", (Object) jie);
        return this.dao2._queryArrayList(gradeSQL2, args_gradeSQL);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getLevelClassByGrade1(String school, String grade, String subject, String classType, String jie, String userId, String position) {
        List<Userposition> up = getPositionByuser(userId);
        for (int i = 0; i < up.size(); i++) {
            String type = up.get(i).getType();
            String gra = up.get(i).getGradeNum();
            if (type.indexOf("5") != -1) {
                return getLevelClassByGrade(school, grade, subject, classType, jie);
            }
            if (type.indexOf("3") != -1 && gra.indexOf(grade) != -1) {
                return getLevelClassByGrade(school, grade, subject, classType, jie);
            }
            if (type.indexOf("2") != -1 && gra.indexOf(grade) != -1) {
                Map args_classSQL = StreamMap.create().put("userId", (Object) userId).put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("jie", (Object) jie).put("subject", (Object) subject);
                return this.dao2._queryOrderMap("SELECT sss.classNum,CONCAT(l.className, CONCAT('(',d.`name`,')')) from( SELECT ss.id,ss.schoolNum,ss.gradeNum,ss.jie,ll.classNum classNum from( select s.id,s.schoolNum,s.gradeNum,s.jie,u.classNum from student s  inner JOIN userposition u on s.schoolNum=u.schoolNum and s.gradeNum=u.gradeNum and s.classNum=u.classNum  and u.userNum={userId} where s.schoolNum={school} and s.gradeNum={grade} and s.jie={jie}  )ss LEFT JOIN levelstudent ll on ss.id=ll.sid and ss.schoolNum=ll.schoolNum and ss.gradeNum=ll.gradeNum  and ss.jie=ll.jie  where ll.schoolNum={school} and ll.gradeNum={grade} and ll.jie={jie} and subjectNum={subject}  )sss left JOIN levelclass l on sss.classNum=l.id  LEFT JOIN `data` d ON d.type='25' AND l.studentType = d.`value` GROUP BY sss.classNum", TypeEnum.StringString, args_classSQL);
            }
        }
        return null;
    }

    public List<Userposition> getPositionByuser(String username) {
        if (!"-1".equals(username) && !"-2".equals(username)) {
            Map args_sql = StreamMap.create().put("username", (Object) username);
            return this.dao2._queryBeanList("SELECT  GROUP_CONCAT(DISTINCT d.type) type,  GROUP_CONCAT(DISTINCT d.schoolNum) schoolNum, GROUP_CONCAT(DISTINCT d.gradeNum) gradeNum,  GROUP_CONCAT(DISTINCT d.classNum) classNum   from(  select type,schoolNum,gradeNum,classNum from userposition where userNum={username}  and type<>1 and type<>4  UNION  SELECT '' type, schoolNum,'' gradeNum,'' classNum  from schauthormanage where userId={username}  )d GROUP BY d.gradeNum ORDER BY d.type desc,d.gradeNum ", Userposition.class, args_sql);
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getStudentBySchoolAndGradeAndClass(String schoolNum, String gradeNum, String classNum) {
        String studentSql = "select id,CONCAT(studentName,'-',studentId) as studentName from student where schoolNum=" + schoolNum + " and gradeNum={gradeNum} and classNum={classNum} and isDelete='F'";
        Map args_studentSql = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        return this.dao2._queryOrderMap(studentSql, TypeEnum.StringString, args_studentSql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getLevelClassBySubject(String school, String grade, String classType, String jie) {
        String gradeSQL = "SELECT DISTINCT s.subjectNum subjectNum, s.subjectName subjectName FROM levelclass cl LEFT JOIN `subject` s ON cl.subjectNum=s.subjectNum   WHERE cl.isDelete='F' AND cl.schoolNum={school} AND cl.gradeNum={grade} AND cl.jie={jie}  ORDER BY length(classNum), classNum";
        Map args_gradeSQL = StreamMap.create().put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("jie", (Object) jie);
        return this.dao2._queryOrderMap(gradeSQL, TypeEnum.StringString, args_gradeSQL);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<Integer, String> getSubject() {
        return this.dao2._queryOrderMap("select subjectNum,subjectName from subject where isHidden='F' order by orderNum", TypeEnum.IntegerString, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getLevelInfo(String studentId) {
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryOrderMap("SELECT c.levelsubject,c.classNum from levelclass lc LEFT join class c ON c.classNum=lc.levelClassNum right JOIN student st on st.gradeNum=lc.gradeNum and st.studentId=lc.studentId where lc.studentId={studentId}", TypeEnum.StringString, args_sql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Student getSchoolGradeInfo(String studentId) {
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return (Student) this.dao2._queryBean("SELECT schoolNum,gradeNum from student where isDelete='F' and  studentId={studentId}", Student.class, args_sql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getLevelClass(String schoolNum, String gradeNum, String subjectNum) {
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryOrderMap("SELECT classNum,className from class WHERE isDelete='F' and classType='2' and schoolNum={schoolNum} and gradeNum={gradeNum} and levelsubject={subjectNum}", TypeEnum.StringString, args_sql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String stuIsExist(String studentId, String user) {
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        Object obj = this.dao2._queryObject("select isDelete from user where usertype='2' and(username={studentId} or loginname={studentId})", args_sql);
        String flag = "0";
        if (null == obj) {
            flag = "0";
        } else if (obj.toString().equals("T")) {
            List<Map<String, Object>> list1 = this.dao2._queryMapList("select st.id,st.schoolNum,s.schoolName,g.gradeNum,g.gradeName,c.className,st.studentName,st.jie from student st left JOIN school s on st.schoolNum = s.id LEFT join basegrade g on st.gradeNum = g.gradeNum LEFT JOIN class c on st.classNum = c.id and st.schoolNum = c.schoolNum and st.gradeNum = c.gradeNum where st.studentId = {studentId} and st.isDelete='T' ", TypeEnum.StringObject, args_sql);
            Map args_sql2 = StreamMap.create().put("user", (Object) user);
            List<Map<String, Object>> list2 = this.dao2._queryMapList("select schoolNum from schauthormanage  where userId={user} union select schoolNum from user where id={user}", TypeEnum.StringObject, args_sql2);
            List<Object> list3 = new ArrayList<>();
            Iterator<Map<String, Object>> it = list2.iterator();
            while (it.hasNext()) {
                list3.add(it.next().get(Const.EXPORTREPORT_schoolNum));
            }
            Map map = new HashMap();
            if (user.equals("-1") || user.equals("-2") || list3.contains(list1.get(0).get(Const.EXPORTREPORT_schoolNum))) {
                map.put("id", String.valueOf(list1.get(0).get("id")));
                map.put("msg", "ID号(" + studentId + ")和已删除的记录重复（" + list1.get(0).get("schoolName") + "--" + list1.get(0).get("jie") + "届--" + list1.get(0).get("gradeName") + "--" + list1.get(0).get("studentName") + "），是否继续使用这个ID号?");
                map.put("flag", "3");
                map.put("oldSchool", list1.get(0).get(Const.EXPORTREPORT_schoolNum));
            } else {
                map.put("id", String.valueOf(list1.get(0).get("id")));
                map.put("msg", "");
                map.put("flag", "4");
                map.put("oldSchool", list1.get(0).get(Const.EXPORTREPORT_schoolNum));
            }
            flag = JSONArray.fromObject(map).toString();
        } else if (obj.toString().equals("F")) {
            List<Map<String, Object>> list12 = this.dao2._queryMapList("select st.id,st.schoolNum,s.schoolName,g.gradeName,c.className,st.studentId,st.studentName from student st left JOIN school s on st.schoolNum = s.id LEFT join basegrade g on st.gradeNum = g.gradeNum LEFT JOIN class c on st.classNum = c.id and st.schoolNum = c.schoolNum and st.gradeNum = c.gradeNum where st.studentId = {studentId} and st.isDelete='F' ", TypeEnum.StringObject, args_sql);
            Map args_sql22 = StreamMap.create().put("user", (Object) user);
            List<Map<String, Object>> list22 = this.dao2._queryMapList("select schoolNum from schauthormanage  where userId={user} union select schoolNum from user where id={user}", TypeEnum.StringObject, args_sql22);
            List<Object> list32 = new ArrayList<>();
            Iterator<Map<String, Object>> it2 = list22.iterator();
            while (it2.hasNext()) {
                list32.add(it2.next().get(Const.EXPORTREPORT_schoolNum));
            }
            Map map2 = new HashMap();
            map2.put(Const.EXPORTREPORT_studentId, list12.get(0).get(Const.EXPORTREPORT_studentId));
            map2.put("editMsg", "ID号 " + list12.get(0).get(Const.EXPORTREPORT_studentId) + " 已被" + list12.get(0).get("schoolName") + Const.STRING_SEPERATOR + list12.get(0).get("gradeName") + Const.STRING_SEPERATOR + list12.get(0).get("className") + "的" + list12.get(0).get("studentName") + "使用！");
            if (user.equals("-1") || user.equals("-2") || list32.contains(list12.get(0).get(Const.EXPORTREPORT_schoolNum))) {
                map2.put("id", String.valueOf(list12.get(0).get("id")));
                map2.put("msg", list12.get(0).get("schoolName") + Const.STRING_SEPERATOR + list12.get(0).get("gradeName") + Const.STRING_SEPERATOR + list12.get(0).get("className") + "已有该学生,是否变更信息？");
                map2.put("flag", "1");
                map2.put("oldSchool", list12.get(0).get(Const.EXPORTREPORT_schoolNum));
            } else {
                map2.put("id", String.valueOf(list12.get(0).get("id")));
                map2.put("msg", list12.get(0).get("schoolName") + Const.STRING_SEPERATOR + list12.get(0).get("gradeName") + Const.STRING_SEPERATOR + list12.get(0).get("className") + "已有该学生,是否申请转校？");
                map2.put("flag", "2");
                map2.put("oldSchool", list12.get(0).get(Const.EXPORTREPORT_schoolNum));
            }
            flag = JSONArray.fromObject(map2).toString();
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Student yzexaminationnumIsExist(String gradeNum, String yzexaminationnum) {
        Map args_yzsql = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("yzexaminationnum", (Object) yzexaminationnum);
        Student s1 = (Student) this.dao2._queryBean("select * from student where gradeNum={gradeNum} and yzexaminationnum={yzexaminationnum} and isDelete='F' and nodel=0 ", Student.class, args_yzsql);
        return s1;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void addStudent(Student student, User user, List<Levelclass> list, List<Baseinfolog> logList, Userrole userrole) {
        List<RowArg> li = new ArrayList<>();
        li.add(getStuSQL(student));
        li.add(getUserSQL(user));
        li.add(getUserrole(userrole));
        li.addAll(getBatchLevelClassSQL(list));
        this.dao2._batchExecute(li);
        this.dao2.batchSave(logList);
        updateChangeSchool(student);
    }

    public void updateChangeSchool(Student stu) {
        String userId = stu.getInsertUser();
        InitStudentServiceimpl tone = new InitStudentServiceimpl();
        Userposition position = tone.getPositionByuser(userId);
        Map args_sql1 = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) stu.getStudentId());
        Student changeStu = (Student) this.dao2._queryBean("select schoolNum,originalSchool ext1 from changeSchool_student where  studentId={studentId} order by insertDate desc limit 1", Student.class, args_sql1);
        if (changeStu != null && null != position && null != position.getSchoolNum() && position.getSchoolNum().indexOf("$" + changeStu.getSchoolNum() + "$") != -1 && position.getSchoolNum().indexOf("$" + changeStu.getExt1() + "$") != -1 && changeStu.getSchoolNum().equals(stu.getSchoolNum())) {
            Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) stu.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) stu.getGradeNum()).put(Const.EXPORTREPORT_classNum, (Object) stu.getClassNum()).put("studentName", (Object) stu.getStudentName()).put("studentNum", (Object) stu.getStudentNum()).put("source", (Object) stu.getSource()).put("sex", (Object) stu.getSex()).put("originalSchool", (Object) changeStu.getExt1()).put("updateUser", (Object) stu.getInsertUser()).put("updateDate", (Object) DateUtil.getCurrentTime()).put(Const.EXPORTREPORT_studentId, (Object) stu.getStudentId()).put("yzexaminationnum", (Object) stu.getYzexaminationnum()).put("subjectCombineNum", (Object) stu.getSubjectCombineNum()).put("xuejiSchool", (Object) stu.getXuejiSchool()).put("xuejiClass", (Object) stu.getXuejiClass()).put("homeAddress", (Object) stu.getHomeAddress());
            this.dao2._execute("update changeSchool_student set schoolNum={schoolNum},gradeNum={gradeNum},classNum={classNum},studentName={studentName},studentNum={studentNum},source={source},sex={sex},originalSchool={originalSchool},status='1',type='3',updateUser={updateUser},updateDate={updateDate},dealTeacher='-3' ,yzexaminationnum={yzexaminationnum},subjectCombineNum={subjectCombineNum},xuejiSchool={xuejiSchool},xuejiClass={xuejiClass},homeAddress={homeAddress} where studentId={studentId}", args_sql);
        }
    }

    private RowArg getUserSQL(User user) {
        Map args_sql = StreamMap.create().put("id", (Object) user.getId()).put("userid", (Object) user.getUserid()).put("username", (Object) user.getUsername()).put("password", (Object) user.getPassword()).put("usertype", (Object) user.getUsertype()).put("realname", (Object) user.getRealname()).put("schoolnum", (Object) user.getSchoolNum()).put("insertUser", (Object) user.getInsertUser()).put("insertDate", (Object) user.getInsertDate()).put("mobile", (Object) user.getMobile());
        return new RowArg("insert into user (id,userid,isDelete,username,password,usertype,realname,schoolnum,insertUser,insertDate,isUser,mobile) values ( {id}, {userid}, 'F', {username}, {password}, {usertype}, {realname}, {schoolnum}, {insertUser}, {insertDate}, 'T', {mobile} ) ON DUPLICATE KEY UPDATE  userid={userid}, username={username}, password={password}, usertype={usertype}, realname={realname}, schoolnum={schoolnum}, mobile={mobile}, isDelete='F'", args_sql);
    }

    private RowArg getUserrole(Userrole userrole) {
        Map args_sql = StreamMap.create().put("userNum", (Object) userrole.getUserNum()).put("roleNum", (Object) userrole.getRoleNum()).put("insertUser", (Object) userrole.getInsertUser()).put("insertDate", (Object) userrole.getInsertDate()).put("isDelete", (Object) userrole.getIsDelete());
        return new RowArg("INSERT INTO userrole(userNum,roleNum,insertUser,insertDate,isDelete)  VALUES({userNum},{roleNum},{insertUser},{insertDate},{isDelete})  ON DUPLICATE KEY UPDATE userNum={userNum},roleNum={roleNum}", args_sql);
    }

    private List<RowArg> getBatchLevelClassSQL(List<Levelclass> li) {
        List<RowArg> list = new ArrayList<>();
        for (Levelclass levelclass : li) {
            list.add(getLevelClassSQL(levelclass));
        }
        return list;
    }

    private RowArg getLevelClassSQL(Levelclass levelclass) {
        String sql = "insert into levelclass (isDelete,studentId,studentName,gradeNum,schoolNum,insertUser,insertDate,updateUser,updateDate,levelClassNum" + (levelclass.getNote() == null ? "" : ",note") + ")  values(  'F', {studentId},{studentName},{gradeNum},{schoolNum}, {insertUser},{insertDate},{updateUser},{updateDate}, {levelClassNum}" + (levelclass.getNote() == null ? "" : ",{note}") + " ) ON DUPLICATE KEY UPDATE  studentId={studentId}, studentName={studentName}, gradeNum={gradeNum}, schoolNum={schoolNum}, updateUser={updateUser}, updateDate={updateDate}, levelClassNum={levelClassNum}, isDelete='F'" + (levelclass.getNote() == null ? "" : ",note={note}");
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) levelclass.getStudentId()).put("studentName", (Object) levelclass.getStudentName()).put(Const.EXPORTREPORT_gradeNum, (Object) levelclass.getGradeNum()).put(Const.EXPORTREPORT_schoolNum, (Object) levelclass.getSchoolNum()).put("insertUser", (Object) levelclass.getInsertUser()).put("insertDate", (Object) levelclass.getInsertDate()).put("updateUser", (Object) levelclass.getUpdateUser()).put("updateDate", (Object) levelclass.getUpdateDate()).put("levelClassNum", (Object) levelclass.getLevelClassNum()).put("note", (Object) levelclass.getNote());
        return new RowArg(sql, args_sql);
    }

    private RowArg getStuSQL(Student stu) {
        StringBuffer stucolsb = new StringBuffer();
        StringBuffer stuvaluesb = new StringBuffer();
        StringBuffer stuupdateValuesb = new StringBuffer();
        String stuCName = stu.getOldName();
        if (stuCName != null) {
            stucolsb.append(",oldName");
            stuvaluesb.append(",{stuCName}");
            stuupdateValuesb.append(", oldName={stuCName}");
        }
        String sex = stu.getSex();
        if (sex != null) {
            stucolsb.append(",sex");
            stuvaluesb.append(",{sex}");
            stuupdateValuesb.append(", sex={sex}");
        }
        String note = stu.getNote();
        if (note != null) {
            stucolsb.append(",note");
            stuvaluesb.append(",{note}");
            stuupdateValuesb.append(", note={note}");
        }
        String stuSql = "insert into student (id,studentId,isDelete,studentNum,studentName" + ((Object) stucolsb) + ",gradeNum,classNum,schoolNum,insertUser,insertDate,updateUser,updateDate,jie,type,source,nodel,yzexaminationnum,subjectCombineNum,description,xuejiSchool,xuejiClass,homeAddress)  values(  {id},{studentId},'F',{studentNum},{studentName}" + ((Object) stuvaluesb) + ", {gradeNum},{classNum},{schoolNum}, {insertUser},{insertDate},{updateUser},{updateDate},{jie},{type},{source},'0',{yzexaminationnum},{subjectCombineNum},{description},{xuejiSchool},{xuejiClass},{homeAddress}) ON DUPLICATE KEY UPDATE  studentId={studentId}, studentNum={studentNum}, studentName={studentName}" + ((Object) stuupdateValuesb) + ", gradeNum={gradeNum}, classNum={classNum}, schoolNum={schoolNum}, updateUser={updateUser}, updateDate={updateDate}, isDelete='F',nodel=0, jie={jie}, type={type}, source={source}, yzexaminationnum={yzexaminationnum}, subjectCombineNum={subjectCombineNum}, description={description}, xuejiSchool={xuejiSchool}, xuejiClass={xuejiClass}, homeAddress={homeAddress}";
        Map args_stuSql = StreamMap.create().put("stuCName", (Object) stuCName).put("sex", (Object) sex).put("note", (Object) note).put("id", (Object) stu.getId()).put(Const.EXPORTREPORT_studentId, (Object) stu.getStudentId()).put("studentNum", (Object) stu.getStudentNum()).put("studentName", (Object) stu.getStudentName()).put(Const.EXPORTREPORT_gradeNum, (Object) stu.getGradeNum()).put(Const.EXPORTREPORT_classNum, (Object) stu.getClassNum()).put(Const.EXPORTREPORT_schoolNum, (Object) stu.getSchoolNum()).put("insertUser", (Object) stu.getInsertUser()).put("insertDate", (Object) stu.getInsertDate()).put("updateUser", (Object) stu.getUpdateUser()).put("updateDate", (Object) stu.getUpdateDate()).put("jie", (Object) stu.getJie()).put("type", (Object) stu.getType()).put("source", (Object) stu.getSource()).put("yzexaminationnum", (Object) stu.getYzexaminationnum()).put("subjectCombineNum", (Object) stu.getSubjectCombineNum()).put("description", (Object) stu.getDescription()).put("xuejiSchool", (Object) stu.getXuejiSchool()).put("xuejiClass", (Object) stu.getXuejiClass()).put("homeAddress", (Object) stu.getHomeAddress());
        return new RowArg(stuSql, args_stuSql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void editStudent(Student student, Baseinfolog log) {
        Map args_sql = StreamMap.create().put("id", (Object) student.getId());
        Student ss = (Student) this.dao2._queryBean("select insertUser,insertDate from student where id={id}", Student.class, args_sql);
        student.setInsertUser(ss.getInsertUser());
        student.setInsertDate(ss.getInsertDate());
        RowArg stuRowArg = getStuSQL(student);
        this.dao2._execute(stuRowArg.getSql(), stuRowArg.getArg());
        String password_stu = "";
        if (StrUtil.isNotBlank(student.getYuzhiPassword())) {
            password_stu = ",password={yuzhiPassword}";
        }
        String updateUserSql = "UPDATE `user` SET username={username},mobile={mobile},realname={realname},schoolnum={schoolnum},isdelete='F'" + password_stu + " WHERE usertype='2' and userid={userid}";
        Map args_updateUserSql = StreamMap.create().put("username", (Object) student.getStudentId()).put("mobile", (Object) student.getExt1()).put("realname", (Object) student.getStudentName()).put("schoolnum", (Object) student.getSchoolNum()).put("userid", (Object) log.getStudentId()).put("yuzhiPassword", (Object) SecureUtil.md5(student.getYuzhiPassword().toLowerCase()));
        this.dao2._execute(updateUserSql, args_updateUserSql);
        this.dao2.save(log);
        updateChangeSchool(student);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void submitEditLevelStudent(String[] del, Map<String, String> map, List<Levelclass> list, String user, String date, List<Baseinfolog> logs) {
        List<RowArg> sqls = new ArrayList<>();
        for (Levelclass levelclass : list) {
            String stuLevelSql = "insert into levelclass (isDelete,studentId,studentName,gradeNum,schoolNum,insertUser,insertDate,updateUser,updateDate,levelClassNum" + (levelclass.getNote() == null ? "" : ",note") + ")  values(  'F', {studentId}, {studentName}, {gradeNum}, {schoolNum}, {insertUser}, {insertDate}, {updateUser}, {updateDate}, {levelClassNum}" + (levelclass.getNote() == null ? "" : ",{note}") + " ) ON DUPLICATE KEY UPDATE  studentId={studentId}, studentName={studentName}, gradeNum={gradeNum}, schoolNum={schoolNum}, updateUser={updateUser}, updateDate={updateDate}, levelClassNum={levelClassNum}, isDelete='F'" + (levelclass.getNote() == null ? "" : ",note={note}");
            Map args_stuLevelSql = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) levelclass.getStudentId()).put("studentName", (Object) levelclass.getStudentName()).put(Const.EXPORTREPORT_gradeNum, (Object) levelclass.getGradeNum()).put(Const.EXPORTREPORT_schoolNum, (Object) levelclass.getSchoolNum()).put("insertUser", (Object) levelclass.getInsertUser()).put("insertDate", (Object) levelclass.getInsertDate()).put("updateUser", (Object) levelclass.getUpdateUser()).put("updateDate", (Object) levelclass.getUpdateDate()).put("levelClassNum", (Object) levelclass.getLevelClassNum()).put("note", (Object) levelclass.getNote());
            sqls.add(new RowArg(stuLevelSql, args_stuLevelSql));
        }
        for (int i = 0; i < del.length; i++) {
            int finalI = i;
            Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) map.get(Const.EXPORTREPORT_studentId)).put(Const.EXPORTREPORT_classNum, (Object) del[finalI]).put(Const.EXPORTREPORT_schoolNum, (Object) map.get(Const.EXPORTREPORT_schoolNum)).put(Const.EXPORTREPORT_gradeNum, (Object) map.get(Const.EXPORTREPORT_gradeNum)).put("user", (Object) user).put("date", (Object) date);
            if (this.dao2._queryObject("SELECT s.id FROM (select id from score where studentId={studentId} and classNum={classNum} UNION ALL SELECT id FROM objectivescore WHERE studentId={studentId} AND classNum={classNum})s LIMIT 0,1", args) == null) {
                this.dao2._execute("DELETE  from levelclass where schoolNum={schoolNum} and studentID = {studentId} and gradeNum={gradeNum} and levelclassnum ={classNum}", args);
            } else {
                this.dao2._execute("update levelclass set isDelete='F',updateUser={user},updateDate={date} where schoolNum={schoolNum} and studentID = {studentId} and gradeNum={gradeNum} and levelclassnum ={classNum}", args);
            }
        }
        this.dao2._batchExecute(sqls);
        this.dao2.batchSave(logs);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void delLevelClassStu(String id, String user, String date) {
        new Levelclass();
        Map args = StreamMap.create().put("id", (Object) id).put("user", (Object) user).put("date", (Object) date);
        Object o = this.dao2._queryBean("select * from levelclass where id={id}", Levelclass.class, args);
        if (this.dao2._queryObject("SELECT s.id FROM (SELECT studentId,classNum,id FROM score UNION ALL SELECT studentId ,classNum ,id FROM objectivescore)s LEFT JOIN levelclass l ON s.studentId=l.studentId AND s.classNum=l.levelClassNum WHERE l.id={id} LIMIT 0,1", args) == null) {
            this.dao2._execute("DELETE  from levelclass where id={id} ", args);
        } else {
            this.dao2._execute("update levelclass set isDelete='T',updateUser={user},updateDate={date}  where id={id} ", args);
        }
        if (null != o) {
            Levelclass levelclass = (Levelclass) o;
            Baseinfolog levelclassLog = new Baseinfolog();
            levelclassLog.setClassNum(levelclass.getLevelClassNum());
            levelclassLog.setGradeNum(Integer.valueOf(levelclass.getGradeNum()));
            levelclassLog.setOperate(Const.log_delete_student);
            levelclassLog.setSchoolNum(Integer.valueOf(levelclass.getSchoolNum()));
            levelclassLog.setInsertUser(user);
            levelclassLog.setInsertDate(date);
            levelclassLog.setStudentId(levelclass.getStudentId());
            this.dao2.save(levelclassLog);
        }
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void delLevelClassStudent(String id, String user, String date) {
        Map args = StreamMap.create().put("id", (Object) id);
        this.dao2._execute("DELETE  from levelstudent where id={id} ", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void delCommonClassStu(String id, String user, String date, String type) {
        List<String> sqls = new ArrayList<>();
        Student student = new Student();
        Map args = StreamMap.create().put("id", (Object) id);
        Object o = this.dao2._queryBean("select * from student where id={id}", Student.class, args);
        if (null != o) {
            student = (Student) o;
        }
        Student finalStudent = student;
        Map args_sqls = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) finalStudent.getId()).put("id", (Object) id).put("username", (Object) finalStudent.getStudentId());
        sqls.add("DELETE FROM `user` WHERE username={username} AND usertype='2'");
        sqls.add("DELETE FROM userrole WHERE userNum={studentId} AND roleNum='2'");
        sqls.add("DELETE FROM examinationnum WHERE studentId={id}");
        sqls.add("DELETE from levelstudent WHERE sid={id} ");
        if (type.equals("1")) {
            sqls.add("DELETE FROM testquestion WHERE studentId={id}");
            sqls.add("DELETE FROM test_student WHERE studentId={id}");
            sqls.add("DELETE qimg FROM remarkimg qimg LEFT JOIN  (SELECT id FROM objectivescore WHERE studentId={id} UNION ALL SELECT id FROM score WHERE studentId={id})s ON qimg.scoreId=s.id WHERE s.id IS NOT NULL");
            sqls.add("DELETE sc FROM scoreimage sc LEFT JOIN  (SELECT id FROM objectivescore WHERE studentId={id} UNION ALL SELECT id FROM score WHERE studentId={id})s ON sc.scoreId=s.id WHERE s.id IS NOT NULL");
            sqls.add("DELETE re FROM remark re LEFT JOIN  (SELECT id FROM objectivescore WHERE studentId={id} UNION ALL SELECT id FROM score WHERE studentId={id})s ON re.scoreId=s.id WHERE s.id IS NOT NULL");
            sqls.add("DELETE ts FROM task ts LEFT JOIN  (SELECT id FROM objectivescore WHERE studentId={id} UNION ALL SELECT id FROM score WHERE studentId={id})s ON ts.scoreId=s.id WHERE s.id IS NOT NULL");
            sqls.add("DELETE stuimg FROM studentpaperimage stuimg LEFT JOIN regexaminee reg ON stuimg.regId=reg.id WHERE reg.studentId={id} AND reg.id IS NOT NULL");
            sqls.add("DELETE eximg FROM examinationnumimg eximg LEFT JOIN regexaminee reg ON eximg.regId=reg.id WHERE reg.studentId={id} AND reg.id IS NOT NULL");
            sqls.add("DELETE extypeimg FROM exampapertypeimage extypeimg LEFT JOIN regexaminee reg ON extypeimg.regId=reg.id WHERE reg.studentId={id} AND reg.id IS NOT NULL");
            sqls.add("DELETE err FROM examineenumerror err LEFT JOIN regexaminee reg ON err.regId=reg.id WHERE reg.studentId={id} AND reg.id IS NOT NULL");
            sqls.add("DELETE FROM studentlevel WHERE studentId={id}");
            sqls.add("DELETE FROM illegal WHERE studentId={id}");
            sqls.add("DELETE iimg FROM illegalimage iimg LEFT JOIN illegal ill ON ill.regId=iimg.regId WHERE ill.studentId={id} AND ill.regId IS NOT NULL");
            sqls.add("DELETE obj FROM objitem obj LEFT JOIN regexaminee reg ON obj.regId=reg.id WHERE reg.studentId={id} AND reg.id IS NOT NULL");
            sqls.add("DELETE FROM objectivescore WHERE studentId={id}");
            sqls.add("DELETE FROM score WHERE studentId={id}");
            sqls.add("DELETE FROM regexaminee WHERE studentId={id}");
        }
        sqls.add("DELETE  from student where id={id}");
        this.dao2._batchExecute(sqls, args_sqls);
        if (null != o) {
            Baseinfolog stuLogBaseinfolog = new Baseinfolog();
            stuLogBaseinfolog.setClassNum(student.getClassNum() + "");
            stuLogBaseinfolog.setGradeNum(student.getGradeNum());
            stuLogBaseinfolog.setOperate(Const.log_delete_student);
            stuLogBaseinfolog.setSchoolNum(student.getSchoolNum());
            stuLogBaseinfolog.setInsertUser(user);
            stuLogBaseinfolog.setInsertDate(date);
            stuLogBaseinfolog.setStudentId(student.getId());
            this.dao2.save(stuLogBaseinfolog);
        }
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public RspMsg batchDelStu(String level, String[] ids, String date, String sch, String grade, String cla, Map jie_map, String position, String userId, String leiceng) {
        List<RowArg> sqls = new ArrayList<>();
        List<Baseinfolog> logList = new ArrayList<>();
        if (ids == null || "".equals(ids)) {
            List<Student> stuList = getStudentlist(sch, grade, cla, jie_map, position, userId, leiceng);
            ids = new String[stuList.size()];
            for (int i = 0; i < stuList.size(); i++) {
                ids[i] = stuList.get(i).getId().toString();
            }
        }
        StringBuffer enStuStr = new StringBuffer();
        int enStuCount = 0;
        for (int i2 = 0; i2 < ids.length; i2++) {
            Baseinfolog stuLogBaseinfolog = new Baseinfolog();
            Student student = new Student();
            String[] finalIds = ids;
            int finalI = i2;
            Map args_o = StreamMap.create().put("id", (Object) finalIds[finalI]);
            Object o = this.dao2._queryBean("select classNum,gradeNum,schoolNum,studentName from student where id={id}", Student.class, args_o);
            if (null != o) {
                student = (Student) o;
            }
            RspMsg msg = checkStuEnInfo(ids[i2]);
            if (msg.getCode() == 401) {
                if (enStuCount < 10) {
                    enStuStr.append(student.getStudentName()).append("、");
                }
                enStuCount++;
            } else {
                String count = String.valueOf(this.dao2._queryObject("select ifnull(count(1),0) from studentlevel where studentId={id} ", args_o));
                if (count == null || "0".equals(count)) {
                    sqls.add(new RowArg("DELETE from levelstudent WHERE sid={id} ", args_o));
                    sqls.add(new RowArg("DELETE  from student where id={id}", args_o));
                    sqls.add(new RowArg("DELETE FROM `user` WHERE userId={id} AND usertype='2'", args_o));
                    sqls.add(new RowArg("DELETE FROM userrole WHERE userNum={id} AND roleNum='2'", args_o));
                    String userParentId = String.valueOf(this.dao2._queryObject("SELECT id FROM userparent where userid={id}", args_o));
                    if (!"null".equals(userParentId)) {
                        sqls.add(new RowArg("DELETE FROM userparent WHERE id={userParentId}", args_o));
                        sqls.add(new RowArg("DELETE FROM userrole WHERE userNum={userParentId} AND roleNum='-2' ", args_o));
                    }
                    args_o.put("userParentId", userParentId);
                    stuLogBaseinfolog.setOperate(Const.log_delete_student);
                } else {
                    sqls.add(new RowArg("update student set isDelete='T',nodel=1,updateUser={userId},updateDate={date} where id={id}", args_o));
                    sqls.add(new RowArg("update user set isDelete='T' where userId={id} AND usertype='2' ", args_o));
                    args_o.put("userId", userId);
                    args_o.put("date", date);
                    stuLogBaseinfolog.setOperate(Const.log_update_student);
                }
                stuLogBaseinfolog.setClassNum(student.getClassNum());
                stuLogBaseinfolog.setGradeNum(student.getGradeNum());
                stuLogBaseinfolog.setSchoolNum(student.getSchoolNum());
                stuLogBaseinfolog.setInsertUser(userId);
                stuLogBaseinfolog.setInsertDate(date);
                stuLogBaseinfolog.setStudentId(ids[i2]);
                logList.add(stuLogBaseinfolog);
                updateChangeStu(ids[i2], userId);
            }
        }
        if (logList.size() > 0) {
            this.dao2.batchSave(logList);
        }
        if (sqls.size() > 0) {
            this.dao2._batchExecute(sqls, 300);
        }
        if (enStuCount > 0) {
            enStuStr.deleteCharAt(enStuStr.length() - 1);
            if (enStuCount > 10) {
                enStuStr.append("等");
            }
            enStuStr.append(enStuCount).append("个学生正在参加考试，请先到考生考号列表里删除考号！");
            return new RspMsg(401, enStuStr.toString(), null);
        }
        return new RspMsg(200, "恭喜，操作成功！", null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void deleteLevelStudent(String[] ids, String userId, String date, String sch, String grade, String cla, Map jie_map, String position, String subjectNum) {
        if (ids == null || "".equals(ids)) {
            List<Student> stuList = getStudentlevellist(sch, grade, cla, jie_map, position, userId, subjectNum);
            ids = new String[stuList.size()];
            for (int i = 0; i < stuList.size(); i++) {
                ids[i] = stuList.get(i).getId().toString();
            }
        }
        for (int i2 = 0; i2 < ids.length; i2++) {
            String[] finalIds = ids;
            int finalI = i2;
            Map args = StreamMap.create().put("id", (Object) finalIds[finalI]);
            this.dao2._execute("DELETE  from levelstudent where id={id}", args);
        }
    }

    public List<Student> getStudentlevellist(String sch, String grade, String cla, Map jie_map, String position, String userId, String subjectNum) {
        String sql = "";
        String jieInfo = "";
        String schNum = "";
        String graNum = "";
        String astrict = "";
        String astrictStr = "";
        if (!"-1".equals(sch)) {
            schNum = " and st.schoolNum={sch}";
        }
        if (!"-1".equals(grade)) {
            graNum = " and st.gradeNum={grade}";
            jieInfo = " and st.jie={jie}";
        } else {
            astrict = "  left join (select id,gradeNum from astrict where partType = '1' and (now()<startTime or now()>endTime)) a on st.gradeNum = a.gradeNum  ";
            astrictStr = " and a.id is NULL ";
        }
        String classNum = "";
        if (!"-1".equals(cla)) {
            classNum = " and st.classNum={cla}";
        }
        String subNum = "";
        if (!"-1".equals(subjectNum)) {
            subNum = " and st.subjectNum={subjectNum}";
        }
        if (position.indexOf("5") != -1 || position.indexOf("0") != -1 || userId.equals("-1") || userId.equals("-2")) {
            sql = "SELECT st.id from levelstudent st   LEFT JOIN school sl ON st.schoolNum=sl.id " + astrict + " where sl.isDelete='F'  " + schNum + graNum + classNum + astrictStr + subNum + jieInfo;
        }
        Map args = StreamMap.create().put("sch", (Object) sch).put("grade", (Object) grade).put("jie", jie_map.get(grade)).put("cla", (Object) cla).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryBeanList(sql, Student.class, args);
    }

    public List<Student> getStudentlevellist2(String sch, String grade, String cla, Map jie_map, String position, String userId, String subjectNum) {
        String schNum3;
        String sql = "";
        String jieInfo = "";
        String schNum = "";
        String graNum = "";
        String astrict = "";
        String astrictStr = "";
        String schNum2 = "";
        if (!userId.equals("-1") && !userId.equals("-2")) {
            schNum2 = " left join   (  select DISTINCT a.schoolNum,a.userId  from (   select userId userId,schoolNum from schauthormanage where userId=100109415037796514   union all  select  id userId,schoolNum from user where id=100109415037796514  ) a  \n)s on st.schoolNum=s.schoolNum ";
            schNum3 = " LEFT JOIN school sl ON s.schoolNum=sl.id";
        } else {
            if (!"-1".equals(sch)) {
                schNum = " and st.schoolNum={sch}";
            }
            schNum3 = " LEFT JOIN school sl ON st.schoolNum=sl.id";
        }
        if (!"-1".equals(grade)) {
            graNum = " and st.gradeNum={grade}";
            jieInfo = " and st.jie={jie}";
        } else {
            astrict = "  left join (select id,gradeNum from astrict where partType = '1' and (now()<startTime or now()>endTime)) a on st.gradeNum = a.gradeNum  ";
            astrictStr = " and a.id is NULL ";
        }
        String classNum = "";
        if (!"-1".equals(cla)) {
            classNum = " and st.classNum={cla}";
        }
        String subNum = "";
        if (!"-1".equals(subjectNum)) {
            subNum = " and st.subjectNum={subjectNum}";
        }
        if (position.indexOf("5") != -1 || position.indexOf("0") != -1 || userId.equals("-1") || userId.equals("-2")) {
            sql = "SELECT st.id from levelstudent st  " + schNum2 + schNum3 + astrict + " where sl.isDelete='F'  " + schNum + graNum + classNum + astrictStr + subNum + jieInfo;
        }
        Map args = StreamMap.create().put("sch", (Object) sch).put("grade", (Object) grade).put("jie", jie_map.get(grade)).put("cla", (Object) cla).put("userId", (Object) userId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryBeanList(sql, Student.class, args);
    }

    public List<Student> getStudentlist(String sch, String grade, String cla, Map jie_map, String position, String userId, String leiceng) {
        String schJoin;
        String sql = "";
        String jieInfo = "";
        String schNum = "";
        String dmjschNum = "";
        String graNum = "";
        String astrict = "";
        String astrictStr = "";
        Map args = new HashMap();
        args.put("userId", userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if (!userId.equals("-1") && !userId.equals("-2") && null == map) {
            if (!"-1".equals(sch) || (null != leiceng && !leiceng.equals(""))) {
                schNum = " left join (  select DISTINCT a.schoolNum,a.userId  from (  select userNum userId,schoolNum from schoolscanpermission where userNum={userId} and schoolNum={sch}  union all   select  id userId,schoolNum from user where id={userId} and schoolNum={sch}  ) a  )s on st.schoolNum=s.schoolNum";
            } else {
                schNum = " left join (  select DISTINCT a.schoolNum,a.userId  from (  select userNum userId,schoolNum from schoolscanpermission where userNum={userId}   union all   select  id userId,schoolNum from user where id={userId}  ) a  )s on st.schoolNum=s.schoolNum";
            }
            schJoin = " LEFT JOIN school sl ON s.schoolNum=sl.id";
        } else {
            if (!"-1".equals(sch) || (null != leiceng && !leiceng.equals(""))) {
                dmjschNum = " and st.schoolNum={sch}";
            }
            schJoin = " LEFT JOIN school sl ON st.schoolNum=sl.id";
        }
        if (!"-1".equals(grade)) {
            graNum = " and st.gradeNum={grade}";
            jieInfo = " and st.jie={jie}";
        } else {
            astrict = "  left join (select id,gradeNum from astrict where partType = '1' and (now()<startTime or now()>endTime)) a on st.gradeNum = a.gradeNum  ";
            astrictStr = " and a.id is NULL ";
        }
        String classNum = "";
        if (!"-1".equals(cla)) {
            classNum = " and st.classNum={cla}";
        }
        if (position.indexOf("5") != -1 || position.indexOf("0") != -1 || position.indexOf("999") != -1 || userId.equals("-1") || userId.equals("-2") || null == map) {
            sql = "SELECT st.id from student st  " + schNum + schJoin + astrict + " where st.isDelete='F' and sl.isDelete='F'  " + dmjschNum + graNum + classNum + astrictStr + jieInfo;
        }
        if (null != leiceng && !leiceng.equals("") && sch.equals("-1")) {
            Map args2 = new HashMap();
            args2.put("leiceng", leiceng);
            List list = this.dao2._queryColList("SELECT DISTINCT sItemId from statisticitem_school WHERE topItemId={leiceng} AND statisticitem='01'", args2);
            List<Student> allStudentList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String schoolNum = Convert.toStr(list.get(i));
                Map args_sql = StreamMap.create().put("sch", (Object) schoolNum).put("grade", (Object) grade).put("jie", jie_map.get(grade)).put("userId", (Object) userId).put("cla", (Object) cla);
                allStudentList.addAll(this.dao2._queryBeanList(sql, Student.class, args_sql));
            }
            return allStudentList;
        }
        Map args_sql2 = StreamMap.create().put("sch", (Object) sch).put("grade", (Object) grade).put("jie", jie_map.get(grade)).put("userId", (Object) userId).put("cla", (Object) cla);
        return this.dao2._queryBeanList(sql, Student.class, args_sql2);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Student getStuInfo(String id, String userb) {
        Map args_sql = StreamMap.create().put("id", (Object) id);
        return (Student) this.dao2._queryBean("SELECT s.studentId ,s.studentName studentName,s.gradeNum gradeNum,s.source source ,s.studentNum studentNum,s.type type,s.oldName oldName,s.note note,c.id classNum,s.schoolNum schoolNum,s.id id,s.sex,u.mobile ext1,s.description,s.nodel,s.yzexaminationnum,s.subjectCombineNum,GROUP_CONCAT(sj.subjectNum) ext2,GROUP_CONCAT(sj.subjectName) ext3,s.xuejiSchool,s.xuejiClass,s.homeAddress FROM student s  LEFT JOIN class c ON c.id=s.classNum LEFT JOIN user u on s.id=u.userid    LEFT JOIN subjectcombine sb on s.subjectCombineNum=sb.subjectCombineNum   LEFT JOIN subjectcombineDetail sbd on sb.subjectCombineNum=sbd.subjectCombineNum  LEFT JOIN subject sj on sbd.subjectNum=sj.subjectNum  where s.id={id} GROUP BY sbd.subjectCombineNum  ", Student.class, args_sql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getExamType() {
        return this.dao2._queryOrderMap("select value,name from data where isDelete='F' and type='1'", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getSchoolMap() {
        return this.dao2._queryOrderMap("select id,schoolName from school where isDelete='F'", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getLimitSchoolMap(String userId) {
        String sql;
        Map args_sql = StreamMap.create().put("userId", (Object) userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args_sql);
        if (userId.equals("-1") || userId.equals("-2") || map != null) {
            sql = "select distinct g.schoolNum id,s.schoolName from grade g left join school s on g.schoolNum=s.id where g.isDelete='F' and s.isDelete='F' order by convert(s.schoolName using gbk)";
        } else {
            sql = " select distinct g.schoolnum id,c.schoolName from grade g left join school c on g.schoolNum = c.id  left join schoolscanpermission s on s.schoolNum = c.id and s.userNum={userId}  left join user t on t.schoolNum = c.id and t.id = {userId} and t.usertype=1  where c.isDelete='F' and g.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null)  order by convert(c.schoolName using gbk)";
        }
        return this.dao2._queryOrderMap(sql, TypeEnum.StringString, args_sql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getLimitSchoolMapOfAll(String userId) {
        String sql;
        Map args_sql = StreamMap.create().put("userId", (Object) userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args_sql);
        if (userId.equals("-1") || userId.equals("-2") || map != null) {
            sql = "select s.id id,s.schoolName from school s where s.isDelete='F' order by convert(s.schoolName using gbk)";
        } else {
            sql = " select distinct c.id id,c.schoolName from school c  left join schoolscanpermission s on s.schoolNum = c.id and s.userNum={userId}  left join user t on t.schoolNum = c.id and t.id = {userId} and t.usertype=1  where c.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null)  order by convert(c.schoolName using gbk)";
        }
        return this.dao2._queryOrderMap(sql, TypeEnum.StringString, args_sql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Subject> getXuankaoSubject() {
        return this.dao2._queryBeanList("select subjectNum,subjectName from subject where xuankaoqufen=2 ", Subject.class, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Integer> isOnlySubjectCombine(String subjectCombineName, String subjectNum) {
        List<Integer> list = new ArrayList<>();
        Map args_sql = StreamMap.create().put("subjectCombineName", (Object) subjectCombineName);
        int a1 = Convert.toInt(this.dao2._queryObject("select count(id) from subjectCombine where subjectCombineName={subjectCombineName}", args_sql), 0).intValue();
        list.add(Integer.valueOf(a1));
        String[] subjectArr = subjectNum.split(Const.STRING_SEPERATOR);
        Map args2_sql = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("count", (Object) Integer.valueOf(subjectArr.length));
        int a2 = Convert.toInt(this.dao2._queryObject("SELECT count(1) from( \tSELECT subjectCombineNum,count(1) count FROM subjectcombinedetail WHERE subjectNum in ({subjectNum[]}) GROUP BY subjectCombineNum HAVING count={count})t INNER JOIN( \tSELECT subjectCombineNum,count(1) count FROM subjectcombinedetail  GROUP BY subjectCombineNum HAVING count={count})t1 on t.subjectCombineNum=t1.subjectCombineNum", args2_sql), 0).intValue();
        list.add(Integer.valueOf(a2));
        return list;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Integer> isOnlySubjectCombineEdit(String subjectCombineNum, String subjectCombineName, String subjectNum) {
        List<Integer> list = new ArrayList<>();
        Map args = StreamMap.create().put("subjectCombineName", (Object) subjectCombineName).put("subjectCombineNum", (Object) subjectCombineNum);
        int a1 = Integer.parseInt(String.valueOf(this.dao2._queryObject("SELECT count(id) from subjectcombine where subjectcombineName={subjectCombineName} and subjectCombineNum!={subjectCombineNum}", args)));
        list.add(Integer.valueOf(a1));
        String[] subjectArr = subjectNum.split(Const.STRING_SEPERATOR);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("count", Integer.valueOf(subjectArr.length));
        int a2 = Integer.parseInt(String.valueOf(this.dao2._queryObject("SELECT count(1) from( \tSELECT subjectCombineNum,count(1) count FROM subjectcombinedetail WHERE subjectCombineNum<>{subjectCombineNum} and subjectNum in ({subjectNum[]}) GROUP BY subjectCombineNum HAVING count={count})t INNER JOIN( \tSELECT subjectCombineNum,count(1) count FROM subjectcombinedetail WHERE subjectCombineNum<>{subjectCombineNum}  GROUP BY subjectCombineNum HAVING count={count})t1 on t.subjectCombineNum=t1.subjectCombineNum", args)));
        list.add(Integer.valueOf(a2));
        return list;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Integer> isExistSubjectCombineByClass(String classNum) {
        List<Integer> list = new ArrayList<>();
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum);
        int a1 = Integer.parseInt(String.valueOf(this.dao2._queryObject("SELECT COUNT(DISTINCT subjectcombineNum) count from student where classNum={classNum} and isDelete='F' and subjectCombineNum<>0", args)));
        list.add(Integer.valueOf(a1));
        return list;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Integer> isExistDataBySubjectCombine(String subjectCombineNum) {
        List<Integer> list = new ArrayList<>();
        Map args = StreamMap.create().put("subjectCombineNum", (Object) subjectCombineNum);
        int a1 = Integer.parseInt(String.valueOf(this.dao2._queryObject("select count(DISTINCT xuankezuhe) count from arealevel where xuankezuhe in ({subjectCombineNum[]}) ", args)));
        int a2 = Integer.parseInt(String.valueOf(this.dao2._queryObject("select count(DISTINCT studentId) count from student where subjectCombineNum in ({subjectCombineNum[]}) ", args)));
        list.add(Integer.valueOf(a1));
        list.add(Integer.valueOf(a2));
        return list;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<SubjectCombine> getSubjectCombineList() {
        return this.dao2._queryBeanList("SELECT s.id,s.subjectCombineNum,s.subjectCombineName,s.isParent,s.pid,s2.subjectCombineName ext1,GROUP_CONCAT(sb.subjectNum) ext2,GROUP_CONCAT(sj.subjectName) ext3  from subjectcombine s   LEFT JOIN subjectcombine s2 on s.pid=s2.subjectCombineNum  INNER JOIN subjectcombinedetail sb on s.subjectCombineNum=sb.subjectCombineNum  INNER JOIN subject sj on sb.subjectNum=sj.subjectNum   where s.isDelete='F'  GROUP BY s.subjectCombineNum ORDER BY s.orderNum asc  ", SubjectCombine.class, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<SubjectCombine> getSubjectCombineNotFirstList() {
        return this.dao2._queryBeanList("SELECT subjectCombineNum,subjectCombineName from subjectcombine where isParent=0  ", SubjectCombine.class, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<SubjectCombine> getSubjectCombineByParam(Map<String, String> map) {
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String classNum = map.get(Const.EXPORTREPORT_classNum);
        String schStr = "";
        if (!"-1".equals(schoolNum)) {
            schStr = schStr + " and schoolNum={schoolNum} ";
        }
        String gradeStr = "";
        if (!"-1".equals(gradeNum)) {
            gradeStr = gradeStr + " and gradeNum={gradeNum} ";
        }
        String classStr = "";
        if (!"-1".equals(classNum)) {
            classStr = classStr + " and classNum={classNum} ";
        }
        String sql = "SELECT s.subjectCombineNum,if(s.subjectCombineNum=0||ISNULL(s.subjectCombineNum=0),'无',s.subjectCombineName) subjectCombineName from(  SELECT DISTINCT sb.subjectCombineNum,sb.subjectCombineName from  (select distinct subjectCombineNum from student  where 1=1 " + gradeStr + schStr + classStr + " ) s left join subjectcombine sb on s.subjectCombineNum=sb.subjectCombineNum  )s ORDER BY s.subjectCombineNum ";
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        return this.dao2._queryBeanList(sql, SubjectCombine.class, args_sql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<SubjectCombine> getSubjectCombineisFirstList() {
        return this.dao2._queryBeanList("SELECT subjectCombineNum,subjectCombineName from subjectcombine where isParent=1  ", SubjectCombine.class, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List editSubjectCombine(String subjectCombineNum) {
        List list = new ArrayList();
        Map args_sql = StreamMap.create().put("subjectCombineNum", (Object) subjectCombineNum);
        SubjectCombine sc = (SubjectCombine) this.dao2._queryBean("SELECT s.id,s.subjectCombineNum,s.subjectCombineName,s.isParent,s.pid,s2.subjectCombineName ext1 from subjectcombine s  LEFT JOIN subjectcombine s2 on s.pid=s2.subjectCombineNum  where s.subjectCombineNum={subjectCombineNum}  ", SubjectCombine.class, args_sql);
        list.add(sc);
        list.add(this.dao2._queryBeanList("select s.subjectNum,s.subjectName,  CASE when s.subjectNum=st.subjectNum then 1 else 0 end ext1   from subject s  LEFT JOIN subjectcombinedetail st on s.subjectNum=st.subjectNum and st.subjectCombineNum={subjectCombineNum}  where s.xuankaoqufen=2 ", Subject.class, args_sql));
        list.add(this.dao2._queryBeanList("SELECT * from subjectcombine where isParent=1 ", SubjectCombine.class, null));
        return list;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void insertFirstSubject(Map<String, String> map, String userId) {
        List<RowArg> sqls = new ArrayList<>();
        String operDate = DateUtil.getCurrentTime();
        String subjectCombineNum = GUID.getGUIDStr();
        String subjectCombineName = map.get("subjectCombineName");
        String isParent = map.get("isParent");
        String[] subjectArr = map.get(Const.EXPORTREPORT_subjectNum).split(Const.STRING_SEPERATOR);
        int orderNum = Integer.parseInt(String.valueOf(this.dao2._queryObject("select IFNULL(max(orderNum),0) from subjectCombine", null))) + 1;
        for (int i = 0; i < subjectArr.length; i++) {
            int finalI = i;
            Map args_sql1 = StreamMap.create().put("subjectCombineNum", (Object) subjectCombineNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectArr[finalI]).put("orderNum", (Object) Integer.valueOf(finalI)).put("userId", (Object) userId).put("operDate", (Object) operDate);
            sqls.add(new RowArg("insert into subjectCombineDetail (subjectCombineNum,subjectNum,orderNum,insertUser,insertDate,updateUser,updateDate,isDelete) values ({subjectCombineNum},{subjectNum},{orderNum},{userId},{operDate},{userId},{operDate},'F')", args_sql1));
        }
        Map args_sql2 = StreamMap.create().put("subjectCombineNum", (Object) subjectCombineNum).put("subjectCombineName", (Object) subjectCombineName).put("isParent", (Object) isParent).put("orderNum", (Object) Integer.valueOf(orderNum)).put("userId", (Object) userId).put("operDate", (Object) operDate);
        sqls.add(new RowArg("insert into subjectCombine (subjectCombineNum,subjectCombineName,isParent,orderNum,insertUser,insertDate,updateUser,updateDate,isDelete) values ({subjectCombineNum},{subjectCombineName},{isParent},{orderNum},{userId},{operDate},{userId},{operDate},'F')", args_sql2));
        this.dao2._batchExecute(sqls);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void insertSubjectCombine(Map<String, String> map, String userId) {
        List<RowArg> sqls = new ArrayList<>();
        String operDate = DateUtil.getCurrentTime();
        String subjectCombineNum = GUID.getGUIDStr();
        String subjectCombineName = map.get("subjectCombineName");
        String isParent = map.get("isParent");
        String firstSubject = map.get("firstSubject");
        String[] subjectArr = map.get(Const.EXPORTREPORT_subjectNum).split(Const.STRING_SEPERATOR);
        int orderNum = Integer.parseInt(String.valueOf(this.dao2._queryObject("select IFNULL(max(orderNum),0) from subjectCombine", null))) + 1;
        for (int i = 0; i < subjectArr.length; i++) {
            int finalI = i;
            Map args_sql1 = StreamMap.create().put("subjectCombineNum", (Object) subjectCombineNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectArr[finalI]).put("orderNum", (Object) Integer.valueOf(finalI)).put("userId", (Object) userId).put("operDate", (Object) operDate);
            sqls.add(new RowArg("insert into subjectCombineDetail (subjectCombineNum,subjectNum,orderNum,insertUser,insertDate,updateUser,updateDate,isDelete) values ({subjectCombineNum},{subjectNum},{orderNum},{userId},{operDate},{userId},{operDate},'F')", args_sql1));
        }
        Map args_sql2 = StreamMap.create().put("subjectCombineNum", (Object) subjectCombineNum).put("subjectCombineName", (Object) subjectCombineName).put("isParent", (Object) isParent).put("firstSubject", (Object) firstSubject).put("orderNum", (Object) Integer.valueOf(orderNum)).put("userId", (Object) userId).put("operDate", (Object) operDate);
        sqls.add(new RowArg("insert into subjectCombine (subjectCombineNum,subjectCombineName,isParent,pid,orderNum,insertUser,insertDate,updateUser,updateDate,isDelete) values ({subjectCombineNum},{subjectCombineName},{isParent},{firstSubject},{orderNum},{userId},{operDate},{userId},{operDate},'F')", args_sql2));
        this.dao2._batchExecute(sqls);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void updateSubjectCombine(Map<String, String> map, String userId) {
        List<RowArg> sqls = new ArrayList<>();
        String operDate = DateUtil.getCurrentTime();
        String subjectCombineNum = map.get("subjectCombineNum");
        String subjectCombineName = map.get("subjectCombineName");
        String firstSubject = map.get("firstSubject");
        String[] subjectArr = map.get(Const.EXPORTREPORT_subjectNum).split(Const.STRING_SEPERATOR);
        Map args_sql = StreamMap.create().put("subjectCombineNum", (Object) subjectCombineNum);
        sqls.add(new RowArg("delete from subjectCombineDetail where subjectCombineNum={subjectCombineNum}", args_sql));
        for (int i = 0; i < subjectArr.length; i++) {
            int finalI = i;
            Map args_sql1 = StreamMap.create().put("subjectCombineNum", (Object) subjectCombineNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectArr[finalI]).put("orderNum", (Object) Integer.valueOf(finalI)).put("userId", (Object) userId).put("operDate", (Object) operDate);
            sqls.add(new RowArg("insert into subjectCombineDetail (subjectCombineNum,subjectNum,orderNum,insertUser,insertDate,updateUser,updateDate,isDelete) values ({subjectCombineNum},{subjectNum},{orderNum},{userId},{operDate},{userId},{operDate},'F') ON DUPLICATE KEY UPDATE updateUser={userId},updateDate={operDate}", args_sql1));
        }
        Map args_sql2 = StreamMap.create().put("subjectCombineName", (Object) subjectCombineName).put("firstSubject", (Object) firstSubject).put("userId", (Object) userId).put("operDate", (Object) operDate).put("subjectCombineNum", (Object) subjectCombineNum);
        sqls.add(new RowArg("update subjectCombine set subjectCombineName={subjectCombineName},pid={firstSubject},updateUser={userId},updateDate={operDate} where subjectCombineNum={subjectCombineNum}", args_sql2));
        this.dao2._batchExecute(sqls);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void updateSubjectCombineByClass(Map<String, String> map, String userId) {
        String operDate = DateUtil.getCurrentTime();
        String subjectCombineNum = map.get("subjectCombineNum");
        String classNum = map.get(Const.EXPORTREPORT_classNum);
        Map args_sql2 = StreamMap.create().put("subjectCombineNum", (Object) subjectCombineNum).put("userId", (Object) userId).put("operDate", (Object) operDate).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        this.dao2._execute("update student set subjectCombineNum={subjectCombineNum},updateUser={userId},updateDate={operDate} where classNum={classNum} and isDelete='F' ", args_sql2);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void delSubjectCombine(Map<String, String> map, String userId) {
        List<String> sqls = new ArrayList<>();
        String subjectCombineNum = map.get("subjectCombineNum");
        Map args = StreamMap.create().put("subjectCombineNum", (Object) subjectCombineNum);
        sqls.add("delete from subjectCombine where subjectCombineNum={subjectCombineNum}");
        sqls.add("delete from subjectCombineDetail where subjectCombineNum={subjectCombineNum}");
        this.dao2._batchExecute(sqls, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void batchdelSubjectCombine(Map<String, String> map, String userId) {
        List<RowArg> sqls = new ArrayList<>();
        String ids = map.get("ids");
        String[] idArr = ids.split(Const.STRING_SEPERATOR);
        for (int i = 0; i < idArr.length; i++) {
            int finalI = i;
            Map args = StreamMap.create().put("subjectCombineNum", (Object) idArr[finalI]);
            sqls.add(new RowArg("delete from subjectCombine where subjectCombineNum={subjectCombineNum}", args));
            sqls.add(new RowArg("delete from subjectCombineDetail where subjectCombineNum={subjectCombineNum}", args));
        }
        this.dao2._batchExecute(sqls);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void reSortSubjectCombine(Map<String, String> map, String userId) {
        new ArrayList();
        String ids = map.get("ids");
        String[] idArr = ids.split(Const.STRING_SEPERATOR);
        for (int i = 0; i < idArr.length; i++) {
            int finalI = i;
            Map args = StreamMap.create().put("subjectCombineNum", (Object) idArr[finalI]).put("i", (Object) Integer.valueOf(finalI));
            this.dao2._execute("update subjectCombine set orderNum=null where subjectCombineNum={subjectCombineNum}", args);
            this.dao2._execute("update subjectCombine set orderNum={i} where subjectCombineNum={subjectCombineNum}", args);
        }
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getSchoolMapByNum(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryOrderMap("select id,schoolName from school where schoolNum={schoolNum}", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Grade> getGradeList(String schooolNum) {
        Map args = StreamMap.create().put("schooolNum", (Object) schooolNum);
        return this.dao2._queryBeanList("select DISTINCT g.gradeNum,g.gradeName,g.schoolNum,g.jie ,t.teacherName ext1,g.islevel  from grade g LEFT JOIN userposition u ON u.schoolNum = g.schoolNum AND u.gradeNum = g.gradeNum AND u.type='3' AND u.jie=g.jie  LEFT JOIN `user` us ON u.userNum=us.id  LEFT JOIN teacher t ON  t.id = us.userid  where g.isDelete='F' and g.schoolNum={schooolNum} order by g.gradeNum*1,g.jie*1", Grade.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Grade> getAllGradeList() {
        return this.dao2._queryBeanList("select  DISTINCT gradeNum,gradeName,jie from grade where isDelete='F' order by gradeNum desc", Grade.class, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getOtherGrade(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryOrderMap("SELECT bg.gradeNum as gradeNum,bg.gradeName as gradeName from basegrade bg left join (SELECT * from grade where schoolNum={schoolNum} and isDelete='F') g ON bg.gradeNum=g.gradeNum where g.id is null", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void addGrade(List<Grade> grades) {
        List<RowArg> sqls = new ArrayList<>();
        List<Baseinfolog> logs = new ArrayList<>();
        for (Grade grade : grades) {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) grade.getGradeNum()).put("gradeName", (Object) grade.getGradeName()).put(Const.EXPORTREPORT_schoolNum, (Object) grade.getSchoolNum()).put("insertUser", (Object) grade.getInsertUser()).put("insertDate", (Object) grade.getInsertDate()).put("updateUser", (Object) grade.getUpdateUser()).put("updateDate", (Object) grade.getUpdateDate()).put("jie", (Object) grade.getJie()).put("islevel", (Object) grade.getIslevel());
            sqls.add(new RowArg("insert into grade (isDelete,gradeNum,gradeName,schoolNum,insertUser,insertDate,updateUser,updateDate,jie,islevel) values ( 'F', {gradeNum}, {gradeName}, {schoolNum}, {insertUser}, {insertDate}, {updateUser}, {updateDate}, {jie}, {islevel}) ON DUPLICATE KEY UPDATE  gradeNum={gradeNum}, gradeName={gradeName}, schoolNum={schoolNum}, updateUser={updateUser}, jie={jie}, isDelete='F' , islevel={islevel}", args));
            Baseinfolog gradeLog = new Baseinfolog();
            gradeLog.setGradeNum(grade.getGradeNum());
            gradeLog.setOperate(Const.log_add_grade);
            gradeLog.setSchoolNum(grade.getSchoolNum());
            gradeLog.setInsertUser(grade.getInsertUser());
            gradeLog.setInsertDate(grade.getInsertDate());
            logs.add(gradeLog);
        }
        this.dao2._batchExecute(sqls);
        this.dao2.batchSave(logs);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void delGrade(String schoolNum, String gradeNum, String user, String date, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie);
        Object object = this.dao2._queryBean("select * from grade where gradeNum={gradeNum} and schoolNum={schoolNum} and jie={jie}", Grade.class, args);
        if (null != object) {
            Grade grade = (Grade) object;
            Baseinfolog gradeLog = new Baseinfolog();
            gradeLog.setGradeNum(grade.getGradeNum());
            gradeLog.setOperate(Const.log_delete_grade);
            gradeLog.setSchoolNum(grade.getSchoolNum());
            gradeLog.setInsertUser(grade.getInsertUser());
            gradeLog.setInsertDate(grade.getInsertDate());
            this.dao2.save(gradeLog);
        }
        this.dao2._execute("UPDATE student SET nodel='1',isDelete='T'  where schoolNum={schoolNum} and gradeNum={gradeNum} and jie={jie}", args);
        this.dao2._execute("update user u INNER JOIN student s on s.id=u.userid and u.userType=2 set u.isDelete='T' where s.schoolNum={schoolNum} and s.gradeNum={gradeNum} and s.jie={jie}", args);
        this.dao2._execute("delete from class where schoolNum={schoolNum} and gradeNum={gradeNum} and jie={jie}", args);
        this.dao2._execute("delete from grade where schoolNum={schoolNum} and gradeNum={gradeNum} and jie={jie}", args);
        this.dao2._execute("DELETE FROM userposition WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} and jie={jie}", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void batchDelGrade(String schoolNum, String[] gradeNums, String user, String date, String[] jies) {
        List<RowArg> sqls = new ArrayList<>();
        List<Baseinfolog> logs = new ArrayList<>();
        for (int i = 0; i < gradeNums.length; i++) {
            String gradeNum = gradeNums[i];
            String jie = jies[i];
            Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
            sqls.add(new RowArg("UPDATE student SET nodel='1',isDelete='T'  where schoolNum={schoolNum} and gradeNum={gradeNum} and jie={jie}", args));
            sqls.add(new RowArg("update user u INNER JOIN student s on s.id=u.userid and u.userType=2 set u.isDelete='T' where s.schoolNum={schoolNum} and s.gradeNum={gradeNum} and s.jie={jie}", args));
            sqls.add(new RowArg("delete from class where schoolNum={schoolNum} and gradeNum={gradeNum} and jie={jie}", args));
            sqls.add(new RowArg("delete from grade where schoolNum={schoolNum} and gradeNum={gradeNum} and jie={jie}", args));
            sqls.add(new RowArg("DELETE FROM userposition WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} and jie={jie}", args));
            Object object = this.dao2._queryBean("select * from grade where gradeNum={gradeNum} and schoolNum={schoolNum} and jie={jie}", Grade.class, args);
            if (null != object) {
                Grade grade = (Grade) object;
                Baseinfolog gradeLog = new Baseinfolog();
                gradeLog.setGradeNum(grade.getGradeNum());
                gradeLog.setOperate(Const.log_delete_grade);
                gradeLog.setSchoolNum(grade.getSchoolNum());
                gradeLog.setInsertUser(grade.getInsertUser());
                gradeLog.setInsertDate(grade.getInsertDate());
                logs.add(gradeLog);
            }
        }
        this.dao2._batchExecute(sqls);
        this.dao2.batchSave(logs);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void exportExcel() {
        String key;
        String key2;
        String key3;
        Map<String, String> map = this.dao2._queryOrderMap("SELECT CONCAT('2104',RIGHT(CKH,4) ),right(CDA,3) from cj", TypeEnum.StringString, null);
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("g", "ABC");
        keyMap.put("k", "ABD");
        keyMap.put("j", "BD");
        keyMap.put("f", "BC");
        keyMap.put("n", "BCD");
        keyMap.put("m", "ACD");
        keyMap.put("e", "AC");
        keyMap.put("c", "AB");
        keyMap.put("i", "AD");
        keyMap.put("l", "CD");
        keyMap.put(".", Const.WHITE_CHAR);
        ListOrderedMap map0 = ListOrderedMap.decorate(map);
        int len = map0.size();
        Map<String, Scores> rightMap = new HashMap<>();
        for (int i = 0; i < len; i++) {
            String stuno = map0.get(i).toString();
            String value = map0.getValue(i).toString();
            char[] keys = value.toCharArray();
            String key21 = keys[0] + "";
            String key21Value = keyMap.get(key21);
            String[] score = new String[3];
            if (null != key21Value) {
                key = "" + Const.STRING_SEPERATOR + key21Value;
            } else {
                key = "" + Const.STRING_SEPERATOR + key21;
                key21Value = key21;
            }
            if ("CD".indexOf(key21Value + "") != -1) {
                if (key21Value.length() == 2) {
                    score[0] = "4";
                } else {
                    score[0] = "2";
                }
            } else {
                score[0] = "0";
            }
            String key22 = keys[1] + "";
            String key22Value = keyMap.get(key22);
            if (null != key22Value) {
                key2 = key + Const.STRING_SEPERATOR + key22Value;
            } else {
                key2 = key + Const.STRING_SEPERATOR + key22;
                key22Value = key22;
            }
            if ("C".equals(key22Value + "")) {
                score[1] = "4";
            } else {
                score[1] = "0";
            }
            String key23 = keys[2] + "";
            String key23Value = keyMap.get(key23);
            if (null != key23Value) {
                key3 = key2 + Const.STRING_SEPERATOR + key23Value;
            } else {
                key3 = key2 + Const.STRING_SEPERATOR + key23;
                key23Value = key23;
            }
            if ("BCD".indexOf(key23Value + "") != -1) {
                if (key23Value.length() == 3) {
                    score[2] = "4";
                } else {
                    score[2] = "2";
                }
            } else {
                score[2] = "0";
            }
            rightMap.put(stuno, new Scores(key3, score));
        }
        File file = new File("C:/a.xls");
        try {
            Workbook wb = Workbook.getWorkbook(file);
            WritableWorkbook wwb = Workbook.createWorkbook(file, wb);
            WritableSheet sheet = wwb.getSheet(0);
            int rows = sheet.getRows();
            for (int i2 = 1; i2 < rows; i2++) {
                String stuno2 = sheet.getCell(2, i2).getContents().trim();
                Scores s = rightMap.get(stuno2);
                Label t21 = new Label(20, i2, s.getScore()[0]);
                sheet.addCell(t21);
                Label t22 = new Label(21, i2, s.getScore()[1]);
                sheet.addCell(t22);
                Label t23 = new Label(22, i2, s.getScore()[2]);
                sheet.addCell(t23);
                Label t36 = new Label(36, i2, sheet.getCell(36, i2).getContents().trim() + s.getKey());
                sheet.addCell(t36);
            }
            wwb.write();
            wb.close();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void addClass(List<Class> class1) {
        this.dao2.batchSave(class1);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void addlevelclass(List<Levelclass> class1) {
        this.dao2.batchSave(class1);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Class> getClassList(String schoolNum, String gradeNum, String classType, String jie) {
        Map args = StreamMap.create().put("jie", (Object) jie).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryBeanList("SELECT DISTINCT c.*,g.gradeName as gradeName, s.schoolName as schoolName, c.jie jie , t.teacherName ext1, GROUP_CONCAT(t1.rkjs ORDER BY t1.orderNum Separator ';') rkjs,g2.islevel from class c left join basegrade g ON g.gradeNum=c.gradeNum  left join grade g2 ON g2.gradeNum=c.gradeNum and g2.jie=c.jie and c.schoolNum = g2.schoolNum  LEFT JOIN (select GROUP_CONCAT(t.teacherName)teacherName,u.classNum from userposition u   LEFT JOIN user us ON us.id = u.userNum LEFT JOIN teacher t ON us.userid = t.id AND u.schoolNum = t.schoolNum where u.type='2' AND u.jie={jie} GROUP BY u.classNum)t ON  t.classNum = c.id left join school s on c.schoolNum=s.id   LEFT JOIN (select up.classNum,s.orderNum, CONCAT(s.subjectName,':',GROUP_CONCAT(u.realname)) rkjs from userposition up LEFT JOIN user u ON up.userNum = u.id LEFT JOIN subject s ON up.subjectNum = s.subjectNum WHERE up.type='1' and up.schoolnum={schoolNum} AND up.gradeNum={gradeNum} GROUP BY up.subjectNum,up.classNum ) t1 ON c.id = t1.classNum  where c.isDelete='F' and c.gradeNum={gradeNum} and c.schoolNum={schoolNum} and c.jie={jie} group by c.id order by CONVERT(c.className USING gbk) ", Class.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Levelclass> getLevelClassList(String schoolNum, String gradeNum, String subjectNum, String classType, String jie) {
        String sql = "SELECT DISTINCT c.*,g.gradeName as gradeName, s.schoolName as schoolName,sub.subjectName subjectName, c.jie jie , t.teacherName ext1,GROUP_CONCAT(t1.rkjs ORDER BY t1.orderNum Separator ';') rkjs,g2.islevel from levelclass c left join basegrade g ON g.gradeNum=c.gradeNum  left join grade g2 ON g2.gradeNum=c.gradeNum and g2.jie=c.jie  and c.schoolNum = g2.schoolNum  and g2.isLevel=1  LEFT JOIN userposition u ON  u.classNum = c.id AND u.type='2'   LEFT JOIN user us ON us.id = u.userNum LEFT JOIN teacher t ON us.userid = t.id AND u.schoolNum = t.schoolNum left join school s on c.schoolNum=s.id  LEFT JOIN `subject` sub ON c.subjectNum=sub.subjectNum  LEFT JOIN (select up.classNum,s.orderNum, CONCAT(s.subjectName,':',GROUP_CONCAT(u.realname)) rkjs from userposition up LEFT JOIN user u ON up.userNum = u.id LEFT JOIN subject s ON up.subjectNum = s.subjectNum WHERE up.type='1' and up.schoolnum={schoolNum} AND up.gradeNum={gradeNum} GROUP BY up.subjectNum,up.classNum ) t1 ON c.id = t1.classNum  where c.isDelete='F' and c.gradeNum={gradeNum} and c.schoolNum={schoolNum} ";
        if (null != subjectNum && !subjectNum.equals("-1") && !subjectNum.equals("")) {
            sql = sql + "AND c.subjectNum={subjectNum}";
        }
        String sql2 = sql + " and c.jie={jie} group by c.id order by LENGTH(c.classNum),c.classNum ASC";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("jie", (Object) jie);
        return this.dao2._queryBeanList(sql2, Levelclass.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String delClassIsExistScore(String schoolNum, String classNum, String user, String date, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
        Object count = this.dao2._queryObject("SELECT id from classlevel where schoolNum={schoolNum} and gradeNum={gradeNum} and classNum={classNum} and jie ={jie}  LIMIT 1", args);
        if (count == null || "".equals(count)) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String delClassIsExistStu(String schoolNum, String classNum, String user, String date, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
        Object count = this.dao2._queryObject("SELECT id from student where schoolNum={schoolNum} and gradeNum={gradeNum} and classNum={classNum} and jie ={jie} and nodel=0 and isDelete='F' LIMIT 1", args);
        if (count == null || "".equals(count)) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String delLevelClassIsExistScore(String schoolNum, String classNum, String user, String date, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
        Object count = this.dao2._queryObject("SELECT id from classlevel_fc where schoolNum={schoolNum} and gradeNum={gradeNum} and classNum={classNum} and jie ={jie}  LIMIT 1", args);
        if (count == null || "".equals(count)) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String delLevelClassIsExistStu(String schoolNum, String classNum, String user, String date, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
        Object count = this.dao2._queryObject("SELECT id from levelstudent where schoolNum={schoolNum} and gradeNum={gradeNum} and classNum={classNum} and jie ={jie}  LIMIT 1", args);
        if (count == null || "".equals(count)) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void delClass(String schoolNum, String classNum, String user, String date, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        this.dao2._execute("delete from class where id={classNum} ", args);
        this.dao2._execute("delete u from user u INNER JOIN student s on s.id=u.userid and u.userType=2 where s.schoolNum={schoolNum} and s.gradeNum={gradeNum} and s.classNum={classNum}", args);
        this.dao2._execute("delete from student where schoolNum={schoolNum} and gradeNum={gradeNum} and classNum={classNum} and jie ={jie} and nodel=0 and isDelete='F' ", args);
        this.dao2._execute("delete u from userparent u INNER JOIN student s on s.id=u.userid where s.schoolNum={schoolNum} and s.gradeNum={gradeNum} and s.classNum={classNum}", args);
        this.dao2._execute("delete u from userrole u INNER JOIN student s on s.id=u.userNum and u.roleNum=-2  where s.schoolNum={schoolNum} and s.gradeNum={gradeNum} and s.classNum={classNum}", args);
        this.dao2._execute("DELETE FROM userposition WHERE classNum={classNum} ", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void delLevelClass(String schoolNum, String classNum, String user, String date, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum);
        this.dao2._execute("delete from levelclass where id={classNum} ", args);
        this.dao2._execute("delete from levelstudent where classNum={classNum} ", args);
        this.dao2._execute("DELETE FROM userposition WHERE classNum={classNum} ", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void batchDelClass(String schoolNum, String[] classNums, String user, String date, String gradeNum, String jie) {
        new ArrayList();
        for (String classNum : classNums) {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
            this.dao2._execute("delete from class where id={classNum} ", args);
            this.dao2._execute("delete u from user u INNER JOIN student s on s.id=u.userid and u.userType=2 where s.schoolNum={schoolNum} and s.gradeNum={gradeNum} and s.classNum={classNum}", args);
            this.dao2._execute("delete from student where schoolNum={schoolNum} and gradeNum={gradeNum} and classNum={classNum} and jie ={jie} and nodel=0 and isDelete='F' ", args);
            this.dao2._execute("DELETE FROM userposition WHERE classNum={classNum} ", args);
        }
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String delClassIsExistScoreBatch(String schoolNum, String[] classNums, String user, String date, String gradeNum, String jie) {
        String classNum = StringUtils.join(classNums, Const.STRING_SEPERATOR);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
        Object count = this.dao2._queryObject("SELECT id from classlevel where schoolNum={schoolNum} and gradeNum={gradeNum} and classNum in ({classNum[]}) and jie ={jie} LIMIT 1", args);
        if (count == null || "".equals(count)) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String delClassIsExistStuBatch(String schoolNum, String[] classNums, String user, String date, String gradeNum, String jie) {
        String classNum = StringUtils.join(classNums, Const.STRING_SEPERATOR);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
        return String.valueOf(this.dao2._queryObject("select GROUP_CONCAT(DISTINCT cl.className) from student s INNER JOIN class cl on s.classNum=cl.id where s.schoolNum={schoolNum} and s.gradeNum={gradeNum} and s.classNum in ({classNum[]}) and s.jie={jie}", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void batchDelLevelClsss(String[] classNums) {
        List<RowArg> sqls = new ArrayList<>();
        for (String classNum : classNums) {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum);
            sqls.add(new RowArg("delete from levelclass where id={classNum}", args));
            sqls.add(new RowArg("delete from levelstudent where classNum={classNum}", args));
        }
        this.dao2._batchExecute(sqls);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String delLevelClassIsExistScoreBatch(String schoolNum, String[] classNums, String user, String date, String gradeNum, String jie) {
        String classNum = StringUtils.join(classNums, Const.STRING_SEPERATOR);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
        Object count = this.dao2._queryObject("SELECT id from classlevel_fc where schoolNum={schoolNum} and gradeNum={gradeNum} and classNum in ({classNum[]}) and jie ={jie} LIMIT 1", args);
        if (count == null || "".equals(count)) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String delLevelClassIsExistStuBatch(String schoolNum, String[] classNums, String user, String date, String gradeNum, String jie) {
        String classNum = StringUtils.join(classNums, Const.STRING_SEPERATOR);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
        return String.valueOf(this.dao2._queryObject("select GROUP_CONCAT(DISTINCT cl.className) from levelstudent s INNER JOIN levelclass cl on s.classNum=cl.id where s.schoolNum={schoolNum} and s.gradeNum={gradeNum} and s.classNum in ({classNum[]}) and s.jie={jie}", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void editClass(Class cls) {
        this.dao2.update(cls);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Class editclassinfo(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        return (Class) this.dao2._queryBean("select * from class where id={id}", Class.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Levelclass editLevelClassinfo(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        return (Levelclass) this.dao2._queryBean("select * from levelclass where id={id}", Levelclass.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void addClasslog(List<Baseinfolog> loglist) {
        this.dao2.batchSave(loglist);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String schoolNameByNum(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return (String) this.dao2._queryObject("select schoolName from school where id={schoolNum}", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String gradeNameByNum(String gradeNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return (String) this.dao2._queryObject("select gradeName from grade where gradeNum={gradeNum} AND schoolNum={schoolNum}", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getClaTypeMap() {
        return this.dao2._queryOrderMap("SELECT VALUE ,NAME FROM `data` WHERE type='3'", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getclaTypeName(String classType) {
        Map args = StreamMap.create().put("classType", (Object) classType);
        return (String) this.dao2._queryObject("SELECT name FROM `data` WHERE `value`={classType}  AND type='3'", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getclaTypeNum(String classTypeName) {
        Map args = StreamMap.create().put("classTypeName", (Object) classTypeName);
        return (String) this.dao2._queryObject("SELECT `value` FROM `data` WHERE `name`={classTypeName}", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void upgrade(String schoolNum, String gradeNum, String jie, String endgNum, String user, String date, String result) {
        if (endgNum.equals("-1")) {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
            this.dao2._execute("INSERT INTO student_upgrade SELECT * FROM student  WHERE   schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args);
            this.dao2._execute("UPDATE grade SET isDelete='T' WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie}", args);
            this.dao2._execute("DELETE FROM userposition WHERE schoolnum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args);
            this.dao2._execute("UPDATE class SET isDelete='T' WHERE schoolNum={schoolNum}  AND gradeNum={gradeNum} AND jie={jie}", args);
            this.dao2._execute("UPDATE user u INNER JOIN( \tSELECT id,studentId from student where schoolNum={schoolNum} AND gradeNum={gradeNum} and jie={jie}\t)s on u.userid=s.id and u.username=s.studentId \tset u.isDelete='T' \twhere u.userType=2", args);
            this.dao2._execute("UPDATE student SET isDelete='T',nodel=1 WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args);
            if (result.equals("T")) {
                this.dao2._execute("DELETE FROM userposition WHERE schoolnum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args);
                this.dao2._execute("INSERT INTO levelstudent_upgrade SELECT * FROM levelstudent WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args);
                this.dao2._execute("UPDATE levelclass SET isDelete='T' WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie}", args);
                return;
            }
            return;
        }
        String gName = (String) this.dao2._queryObject("SELECT gradeName FROM basegrade WHERE gradeNum={endgNum}", StreamMap.create().put("endgNum", (Object) endgNum));
        ugrade(schoolNum, jie, gName, gradeNum, user, date, result);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void upgrade2(String gradeNum, String jie, String endgNum, String user, String date, String result) {
        if (endgNum.equals("-1")) {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
            this.dao2._execute("INSERT INTO student_upgrade SELECT * FROM student  WHERE  gradeNum={gradeNum} AND jie={jie}", args);
            this.dao2._execute("UPDATE grade SET isDelete='T' WHERE gradeNum={gradeNum} AND jie={jie}", args);
            this.dao2._execute("DELETE FROM userposition WHERE gradeNum={gradeNum} AND jie={jie}", args);
            this.dao2._execute("UPDATE class SET isDelete='T' WHERE gradeNum={gradeNum} AND jie={jie}", args);
            this.dao2._execute("UPDATE user u INNER JOIN( \tSELECT id,studentId from student where gradeNum={gradeNum} and jie={jie}\t)s on u.userid=s.id and u.username=s.studentId \tset u.isDelete='T' \twhere u.userType=2", args);
            this.dao2._execute("UPDATE student SET isDelete='T',nodel=1 WHERE gradeNum={gradeNum} AND jie={jie}", args);
            this.dao2._execute("DELETE FROM userparent WHERE userid in (SELECT id FROM student WHERE gradeNum={gradeNum} AND jie={jie} )", args);
            if (result.equals("T")) {
                this.dao2._execute("DELETE FROM userposition WHERE gradeNum={gradeNum} AND jie={jie}", args);
                this.dao2._execute("UPDATE levelclass SET isDelete='T' WHERE gradeNum={gradeNum} AND jie={jie}", args);
                return;
            }
            return;
        }
        String gName = (String) this.dao2._queryObject("SELECT gradeName FROM basegrade WHERE gradeNum={endgNum}", StreamMap.create().put("endgNum", (Object) endgNum));
        ugrade2(jie, gName, gradeNum, user, date, result);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Basegrade> upgradeList(String schoolNum, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        String gName = (String) this.dao2._queryObject("SELECT gradeName FROM grade WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args);
        args.put("gName", gName);
        String bgNum = String.valueOf(this.dao2._queryObject("SELECT gradeNum FROM basegrade where gradeName={gName}", args));
        args.put("bgNum", Integer.valueOf(bgNum));
        return this.dao2._queryBeanList("SELECT gradeNum,gradeName FROM basegrade WHERE (gradeNum*1)>{bgNum} ORDER BY gradeNum*1 LIMIT 0,1", Basegrade.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Basegrade> upgradeList2(String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) Integer.valueOf(gradeNum));
        return this.dao2._queryBeanList("SELECT gradeNum,gradeName FROM basegrade WHERE (gradeNum*1)>{gradeNum} ORDER BY gradeNum*1 LIMIT 0,1", Basegrade.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Class> getClassListBySchGr(String schoolNum, String grade, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("grade", (Object) grade).put("jie", (Object) jie);
        return this.dao2._queryBeanList("SELECT id,className FROM class WHERE schoolNum={schoolNum} AND gradeNum={grade} AND jie={jie} order by classNum*1", Class.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void submovecla(String ids, String classNum, String exams) {
        String[] newids = ids.split(Const.STRING_SEPERATOR);
        String[] examNums = exams.split(Const.STRING_SEPERATOR);
        for (String examNum : examNums) {
            for (String id : newids) {
                Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum).put("id", (Object) id).put(Const.EXPORTREPORT_examNum, (Object) examNum);
                this.dao2._execute("UPDATE student SET classNum={classNum} where id={id} ", args);
                this.dao2._execute("UPDATE examinationnum en LEFT JOIN student stu ON en.studentId = stu.id SET en.classNum = {classNum} WHERE stu.id = {id} AND en.examNum = {examNum}", args);
            }
        }
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getgradeName(String gradeNum) {
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) Integer.valueOf(gradeNum));
        return (String) this.dao2._queryObject("SELECT gradeName FROM basegrade WHERE(gradeNum*1)>{gradeNum} ORDER BY gradeNum*1 LIMIT 0,1", args_sql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void ugrade(String schoolNum, String jie, String gradeName, String gradeNum, String user, String date, String result) {
        Map args_sql = StreamMap.create().put("gradeName", (Object) gradeName).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie);
        String gNum = String.valueOf(this.dao2._queryObject("SELECT gradeNum FROM basegrade WHERE gradeName={gradeName}", args_sql));
        this.dao2._execute("UPDATE grade SET isDelete='T' WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie} ", args_sql);
        String islevel = String.valueOf(this.dao2._queryObject("SELECT islevel FROM grade WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args_sql));
        Map args_addgradeSql = StreamMap.create().put("gNum", (Object) gNum).put("gradeName", (Object) gradeName).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("user", (Object) user).put("date", (Object) date).put("jie", (Object) jie).put("islevel", (Object) islevel);
        this.dao2._execute("INSERT INTO grade (gradeNum,gradeName,schoolNum,insertUser,insertDate,isDelete,jie,updateDate,updateUser,islevel) VALUES({gNum},{gradeName},{schoolNum},{user},{date},'F',{jie},{date},{user},{islevel})", args_addgradeSql);
        this.dao2._execute("UPDATE class SET isDelete='T' WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie}", args_sql);
        this.dao2._execute("INSERT INTO student_upgrade SELECT * FROM student WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args_sql);
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT * FROM class WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie}", Class.class, args_sql);
        for (int i = 0; i < _queryBeanList.size(); i++) {
            String classNum = ((Class) _queryBeanList.get(i)).getClassNum();
            String oldid = ((Class) _queryBeanList.get(i)).getId();
            String className = ((Class) _queryBeanList.get(i)).getClassName();
            String stypeStr = "";
            String stypeStr2 = "";
            String studentType = ((Class) _queryBeanList.get(i)).getStudentType();
            if (null != studentType && !studentType.equals("")) {
                stypeStr = ",studentType";
                stypeStr2 = ",{studentType}";
            }
            String ctype = "";
            String ctype2 = "";
            String classType = ((Class) _queryBeanList.get(i)).getClassType();
            if (null != classType && !classType.equals("")) {
                ctype = ",classType";
                ctype2 = ",{classType}";
            }
            String levelStr = "";
            String levelStr2 = "";
            String levelsubject = ((Class) _queryBeanList.get(i)).getLevelsubject();
            if (null != levelsubject && !levelsubject.equals("")) {
                levelStr = ",levelsubject";
                levelStr2 = ",{levelsubject}";
            }
            String addclasssql = "INSERT INTO class(id,classNum,className,gradeNum,schoolNum,insertUser,insertDate,updateUser,updateDate" + stypeStr + ctype + levelStr + ",isDelete,jie) VALUES({id},{classNum},{className},{gNum},{schoolNum},{user},{date},{user},{date}" + stypeStr2 + ctype2 + levelStr2 + ",'F',{jie})";
            Map args_addclasssql = StreamMap.create().put("id", (Object) GUID.getGUIDStr()).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("className", (Object) className).put("gNum", (Object) gNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("user", (Object) user).put("date", (Object) date).put("jie", (Object) jie);
            this.dao2._execute(addclasssql, args_addclasssql);
            Map args_getclasidSql = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("gNum", (Object) gNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
            String cid = String.valueOf(this.dao2._queryObject("SELECT id FROM class WHERE schoolNum={schoolNum} AND gradeNum={gNum} AND classNum={classNum} AND isDelete='F' AND jie={jie}", args_getclasidSql));
            Map args = StreamMap.create().put("gNum", (Object) gNum).put("cid", (Object) cid).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("oldid", (Object) oldid);
            this.dao2._execute("UPDATE student SET gradeNum={gNum},classNum={cid} WHERE schoolNum={schoolNum} AND jie={jie} AND gradeNum={gradeNum} AND classNum={oldid}", args);
            String upuserpositionSql = "UPDATE userposition SET gradeNum={gNum},classNum={cid} WHERE schoolnum={schoolNum} AND gradeNum={gradeNum} AND classNum={oldid} AND jie={jie}";
            if (result.equals("T")) {
                upuserpositionSql = upuserpositionSql + " and type = '2'";
            }
            this.dao2._execute(upuserpositionSql, args);
        }
        if (result.equals("T")) {
            Map args2 = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
            this.dao2._execute("INSERT INTO levelstudent_upgrade SELECT * FROM levelstudent  WHERE   schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args2);
            this.dao2._execute("UPDATE levelclass SET isDelete='T' WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie}", args2);
            List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT * FROM levelclass WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie}", Levelclass.class, args2);
            for (int i2 = 0; i2 < _queryBeanList2.size(); i2++) {
                String levelclassNum = ((Levelclass) _queryBeanList2.get(i2)).getClassNum();
                String oldid2 = ((Levelclass) _queryBeanList2.get(i2)).getId();
                String levelclassName = ((Levelclass) _queryBeanList2.get(i2)).getClassName();
                String levlesubject = ((Levelclass) _queryBeanList2.get(i2)).getSubjectNum();
                String stypeStr3 = "";
                String stypeStr22 = "";
                String levelstudentType = ((Levelclass) _queryBeanList2.get(i2)).getStudentType();
                if (null != levelstudentType && !levelstudentType.equals("")) {
                    stypeStr3 = ",studentType";
                    stypeStr22 = ",{levelstudentType}";
                }
                String ctype3 = "";
                String ctype22 = "";
                String levelclassType = ((Levelclass) _queryBeanList2.get(i2)).getClassType();
                if (null != levelclassType && !levelclassType.equals("")) {
                    ctype3 = ",classType";
                    ctype22 = ",{levelclassType}";
                }
                String addclasssql2 = "INSERT INTO levelclass(classNum,className,gradeNum,schoolNum,insertDate" + stypeStr3 + ctype3 + ",isDelete,jie,subjectNum) VALUES({levelclassNum},{levelclassName},{gNum},{schoolNum},{date}" + stypeStr22 + ctype22 + ",'F',{jie},{levlesubject})";
                Map args_addclasssql2 = StreamMap.create().put("levelclassNum", (Object) levelclassNum).put("levelclassName", (Object) levelclassName).put("gNum", (Object) gNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("date", (Object) date).put("jie", (Object) jie).put("levlesubject", (Object) levlesubject);
                this.dao2._execute(addclasssql2, args_addclasssql2);
                int cid2 = Integer.valueOf(String.valueOf(this.dao2._queryObject("SELECT MAX(id) FROM levelclass", null))).intValue();
                Map args_ustudentSql = StreamMap.create().put("gNum", (Object) gNum).put("cid", (Object) Integer.valueOf(cid2)).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("oldid", (Object) oldid2);
                this.dao2._execute("UPDATE levelstudent SET gradeNum={gNum},classNum={cid} WHERE schoolNum={schoolNum} AND jie={jie} AND gradeNum={gradeNum} AND classNum={oldid}", args_ustudentSql);
                this.dao2._execute("UPDATE userposition SET gradeNum={gNum},classNum={cid} WHERE schoolnum={schoolNum} AND gradeNum={gradeNum} AND classNum={oldid} AND jie={jie} and type != 2", args_ustudentSql);
            }
        }
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void ugrade2(String jie, String gradeName, String gradeNum, String user, String date, String result) {
        Map args_getgNameSql = StreamMap.create().put("gradeName", (Object) gradeName);
        String gNum = String.valueOf(this.dao2._queryObject("SELECT gradeNum FROM basegrade WHERE gradeName={gradeName}", args_getgNameSql));
        Map args_delgrade = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        this.dao2._execute("UPDATE grade SET isDelete='T' WHERE gradeNum={gradeNum}  AND jie={jie} ", args_delgrade);
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT distinct schoolNum num FROM grade WHERE gradeNum={gradeNum} AND jie={jie}", AjaxData.class, args_delgrade);
        for (int j = 0; j < _queryBeanList.size(); j++) {
            String schoolNum = ((AjaxData) _queryBeanList.get(j)).getNum();
            Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
            String islevel = String.valueOf(this.dao2._queryObject("SELECT islevel FROM grade WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args));
            Map args_addgradeSql = StreamMap.create().put("gNum", (Object) gNum).put("gradeName", (Object) gradeName).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("user", (Object) user).put("date", (Object) date).put("jie", (Object) jie).put("islevel", (Object) islevel);
            this.dao2._execute("INSERT INTO grade (gradeNum,gradeName,schoolNum,insertUser,insertDate,isDelete,jie,updateDate,updateUser,islevel) VALUES({gNum},{gradeName},{schoolNum},{user},{date},'F',{jie},{date},{user},{islevel})", args_addgradeSql);
            this.dao2._execute("UPDATE class SET isDelete='T' WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie}", args);
            this.dao2._execute("INSERT INTO student_upgrade SELECT * FROM student  WHERE   schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}", args);
            List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT * FROM class WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie}", Class.class, args);
            for (int i = 0; i < _queryBeanList2.size(); i++) {
                String classNum = ((Class) _queryBeanList2.get(i)).getClassNum();
                String oldid = ((Class) _queryBeanList2.get(i)).getId();
                String className = ((Class) _queryBeanList2.get(i)).getClassName();
                String stypeStr = "";
                String stypeStr2 = "";
                String studentType = ((Class) _queryBeanList2.get(i)).getStudentType();
                if (null != studentType && !studentType.equals("")) {
                    stypeStr = ",studentType";
                    stypeStr2 = ",{studentType}";
                }
                String ctype = "";
                String ctype2 = "";
                String classType = ((Class) _queryBeanList2.get(i)).getClassType();
                if (null != classType && !classType.equals("")) {
                    ctype = ",classType";
                    ctype2 = ",{classType}";
                }
                String levelStr = "";
                String levelStr2 = "";
                String levelsubject = ((Class) _queryBeanList2.get(i)).getLevelsubject();
                if (null != levelsubject && !levelsubject.equals("")) {
                    levelStr = ",levelsubject";
                    levelStr2 = ",{levelsubject}";
                }
                String addclasssql = "INSERT INTO class(id,classNum,className,gradeNum,schoolNum,insertUser,insertDate,updateUser,updateDate" + stypeStr + ctype + levelStr + ",isDelete,jie) VALUES({id},{classNum},{className},{gNum},{schoolNum},{user},{date},{user},{date}" + stypeStr2 + ctype2 + levelStr2 + ",'F',{jie})";
                Map args_addclasssql = StreamMap.create().put(Const.EXPORTREPORT_studentType, (Object) studentType).put("classType", (Object) classType).put("levelsubject", (Object) levelsubject).put("id", (Object) GUID.getGUIDStr()).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("className", (Object) className).put("gNum", (Object) gNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("user", (Object) user).put("date", (Object) date).put("jie", (Object) jie);
                this.dao2._execute(addclasssql, args_addclasssql);
                Map args_getclasidSql = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("gNum", (Object) gNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
                String cid = String.valueOf(this.dao2._queryObject("SELECT id FROM class WHERE schoolNum={schoolNum} AND gradeNum={gNum} AND classNum={classNum} AND isDelete='F' AND jie={jie}", args_getclasidSql));
                String classAverageSql = "insert into classaverage select '" + cid + "',subjectNum,isDelete,average,'" + date + "','" + user + "' from classaverage where classNum={oldid} and isDelete='F'";
                Map args_sql = StreamMap.create().put("oldid", (Object) oldid).put("gNum", (Object) gNum).put("cid", (Object) cid).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("date", (Object) date);
                this.dao2._execute(classAverageSql, args_sql);
                this.dao2._execute("UPDATE student SET gradeNum={gNum},classNum={cid} WHERE schoolNum={schoolNum} AND jie={jie} AND gradeNum={gradeNum} AND classNum={oldid}", args_sql);
                String upuserpositionSql = "UPDATE userposition SET gradeNum={gNum},classNum={cid},insertDate={date} WHERE schoolnum={schoolNum} AND gradeNum={gradeNum} AND classNum={oldid} AND jie={jie} ";
                if (result.equals("T")) {
                    upuserpositionSql = upuserpositionSql + " and type = '2'";
                }
                this.dao2._execute(upuserpositionSql, args_sql);
            }
            if (result.equals("T")) {
                Map args_dellevelclassSql = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie);
                this.dao2._execute("UPDATE levelclass SET isDelete='T' WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie}", args_dellevelclassSql);
                List<?> _queryBeanList3 = this.dao2._queryBeanList("SELECT * FROM levelclass WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND jie={jie}", Levelclass.class, args_dellevelclassSql);
                for (int i2 = 0; i2 < _queryBeanList3.size(); i2++) {
                    String levelclassNum = ((Levelclass) _queryBeanList3.get(i2)).getClassNum();
                    String oldid2 = ((Levelclass) _queryBeanList3.get(i2)).getId();
                    String levelclassName = ((Levelclass) _queryBeanList3.get(i2)).getClassName();
                    String levlesubject = ((Levelclass) _queryBeanList3.get(i2)).getSubjectNum();
                    String stypeStr3 = "";
                    String stypeStr22 = "";
                    String levelstudentType = ((Levelclass) _queryBeanList3.get(i2)).getStudentType();
                    if (null != levelstudentType && !levelstudentType.equals("")) {
                        stypeStr3 = ",studentType";
                        stypeStr22 = ",{levelstudentType}";
                    }
                    String ctype3 = "";
                    String ctype22 = "";
                    String levelclassType = ((Levelclass) _queryBeanList3.get(i2)).getClassType();
                    if (null != levelclassType && !levelclassType.equals("")) {
                        ctype3 = ",classType";
                        ctype22 = ",{levelclassType}";
                    }
                    String addclasssql2 = "INSERT INTO levelclass(classNum,className,gradeNum,schoolNum,insertDate" + stypeStr3 + ctype3 + ",isDelete,jie,subjectNum) VALUES({levelclassNum},{levelclassName},{gNum},{schoolNum},{date}" + stypeStr22 + ctype22 + ",'F',{jie},{levlesubject})";
                    Map args_addclasssql2 = StreamMap.create().put("levelstudentType", (Object) levelstudentType).put("levelclassType", (Object) levelclassType).put("levelclassNum", (Object) levelclassNum).put("levelclassName", (Object) levelclassName).put("gNum", (Object) gNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("date", (Object) date).put("jie", (Object) jie).put("levlesubject", (Object) levlesubject);
                    this.dao2._execute(addclasssql2, args_addclasssql2);
                    Map args_sql2 = StreamMap.create().put("gNum", (Object) gNum).put("cid", (Object) String.valueOf(String.valueOf(this.dao2._queryObject("SELECT MAX(id) FROM levelclass", null)))).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("oldid", (Object) oldid2).put("date", (Object) date);
                    this.dao2._execute("UPDATE levelstudent SET gradeNum={gNum},classNum={cid} WHERE schoolNum={schoolNum} AND jie={jie} AND gradeNum={gradeNum} AND classNum={oldid}", args_sql2);
                    this.dao2._execute("UPDATE userposition SET gradeNum={gNum},classNum={cid},insertDate={date} WHERE schoolnum={schoolNum} AND gradeNum={gradeNum} AND classNum={oldid} AND jie={jie} and type !=2", args_sql2);
                }
            }
            Map args_upGradeSql = StreamMap.create().put("gNum", (Object) gNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
            this.dao2._execute("UPDATE userposition SET gradeNum={gNum} WHERE gradeNum={gradeNum} AND jie={jie} and schoolnum={schoolNum}", args_upGradeSql);
        }
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getStuIdOldCNum(int schoolNum, String gradeNum, String studentId) {
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) Integer.valueOf(schoolNum)).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return String.valueOf(this.dao2._queryObject("SELECT classNum from student WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND studentId={studentId} ", args_sql));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getStuIdOldSchoolNum(String studentId) {
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return String.valueOf(this.dao2._queryObject("SELECT schoolNum from student WHERE  studentId={studentId}  ", args_sql));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public int getStuOldGradeNum(int schoolNum, String studentId) {
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        String oo = String.valueOf(this.dao2._queryObject("SELECT gradeNum FROM student where studentId={studentId}", args_sql));
        if (null == oo || "null".equals(oo) || "".equals(oo)) {
            return -1;
        }
        return Integer.valueOf(oo).intValue();
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void updatecNum(int schoolNum, String studentId, String gradeNum, String oldclassNum, String newclassNum) {
        List<String> sqls = new ArrayList<>();
        Map args = StreamMap.create().put("newclassNum", (Object) newclassNum).put(Const.EXPORTREPORT_studentId, (Object) studentId).put(Const.EXPORTREPORT_schoolNum, (Object) Integer.valueOf(schoolNum)).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("oldclassNum", (Object) oldclassNum);
        sqls.add("UPDATE examinationnum SET classNum={newclassNum} WHERE studentId={studentId} ");
        sqls.add("UPDATE ctb_error_reason SET classNum={newclassNum} WHERE studentId={studentId}");
        sqls.add("UPDATE ctb_experience SET classNum={newclassNum} WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND classNum={oldclassNum} AND  studentId={studentId} ");
        sqls.add("UPDATE examlog SET classNum={newclassNum} WHERE studentId={studentId} ");
        sqls.add("UPDATE regexaminee SET classNum={newclassNum} WHERE  schoolNum={schoolNum} AND classNum={oldclassNum} AND studentId={studentId} ");
        sqls.add("UPDATE score SET classNum={newclassNum} WHERE  schoolNum={schoolNum} AND gradeNum={gradeNum} AND classNum={oldclassNum} AND studentId={studentId} ");
        sqls.add("UPDATE tag SET classNum={newclassNum} where  schoolNum={schoolNum} AND gradeNum={gradeNum} AND classNum={oldclassNum} AND studentId={studentId} ");
        sqls.add("UPDATE objectivescore SET classNum={newclassNum} WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND classNum={oldclassNum} AND studentId={studentId}  ");
        this.dao2._batchExecute(sqls, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void updatecschoolNum(String oldschoolNum, int newschoolNum, String studentId, String gradeNum, String oldclassNum, String newclassNum) {
        List<String> sqls = new ArrayList<>();
        Map args = StreamMap.create().put("newschoolNum", (Object) Integer.valueOf(newschoolNum)).put("newclassNum", (Object) newclassNum).put(Const.EXPORTREPORT_studentId, (Object) studentId);
        sqls.add("UPDATE examinationnum SET schoolNum={newschoolNum},classNum={newclassNum} WHERE studentId={studentId} ");
        sqls.add("UPDATE userparent SET schoolNum={newschoolNum} WHERE userid={studentId}");
        sqls.add("delete from levelstudent where sid= {studentId} ");
        sqls.add("UPDATE score SET schoolNum={newschoolNum},classNum={newclassNum} WHERE studentId={studentId} ");
        sqls.add("UPDATE objectivescore SET schoolNum={newschoolNum},classNum={newclassNum} WHERE studentId={studentId} ");
        sqls.add("UPDATE tag SET schoolNum={newschoolNum},classNum={newclassNum} WHERE studentId={studentId} ");
        sqls.add("UPDATE ctb_error_reason SET schoolNum={newschoolNum},classNum={newclassNum} WHERE studentId={studentId} ");
        sqls.add("UPDATE ctb_experience SET schoolNum={newschoolNum},classNum={newclassNum} WHERE studentId={studentId} ");
        this.dao2._batchExecute(sqls, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void updateschoolNumInfo(String oldschoolNum, int newschoolNum, String studentId, String gradeNum, String oldclassNum, String newclassNum) {
        List<String> sqls = new ArrayList<>();
        Map args = StreamMap.create().put("newschoolNum", (Object) Integer.valueOf(newschoolNum)).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("newclassNum", (Object) newclassNum);
        sqls.add("UPDATE userparent SET schoolNum={newschoolNum} WHERE userid={studentId}");
        sqls.add("delete from levelstudent where sid= {studentId} ");
        this.dao2._batchExecute(sqls, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void dellevelstudent(String studentId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        this.dao2._execute("delete from levelstudent where sid= {studentId} ", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List ExcelOut(String schoolNum, String gradeNum, String classNum, String sex, String studentId, String studentName, String note, String jie, String position, String userId, String[] stuIds, String leiceng, String subjectCombineNum) {
        String classNum2;
        String subjectCombineNum2;
        String sex2;
        String studentName2;
        String note2;
        Map args = new HashMap();
        args.put("userId", userId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("leiceng", leiceng);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("jie", jie);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("subjectCombineNum", subjectCombineNum);
        args.put("sex", sex);
        args.put("studentName", "%" + studentName + "%");
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("note", "%" + note + "%");
        String sql = "";
        String strSchauthormanage = "";
        String statisticitem = "";
        String sch = "";
        String schNum = "";
        String jieInfo = "";
        String leicengStr = "";
        String strPosition = "";
        Map<String, Object> ismanageMap = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2") && null == ismanageMap) {
            strSchauthormanage = " left join schoolscanpermission s on s.schoolNum = sl.id and s.userNum={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ";
            sch = "  and (s.schoolNum is not null or t.schoolNum is not null) ";
        }
        if (!"-1".equals(schoolNum)) {
            schNum = " and st.schoolNum={schoolNum}";
        } else if (!leiceng.equals("")) {
            leicengStr = leicengStr + " and ss.sItemId is not null";
        }
        if ("-1".equals(schoolNum) && !leiceng.equals("")) {
            statisticitem = " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId={leiceng}) ss on st.schoolNum = ss.sItemId";
        }
        String sids = "";
        String sidsStr = "";
        if (stuIds != null) {
            for (String str : stuIds) {
                sids = sids + str + Const.STRING_SEPERATOR;
            }
            args.put("sids", sids.substring(0, sids.length() - 1));
            sidsStr = " and st.id in ({sids[]}) ";
        }
        String graNum = "";
        if (!"-1".equals(gradeNum) && !"".equals(gradeNum) && !"null".equals(gradeNum) && null != gradeNum) {
            graNum = " and st.gradeNum={gradeNum}";
            jieInfo = " and st.jie={jie}";
        }
        if (!"-1".equals(classNum) && !"".equals(classNum) && !"null".equals(classNum) && null != classNum) {
            classNum2 = " and st.classNum={classNum}";
        } else {
            classNum2 = "";
        }
        if (!"-1".equals(subjectCombineNum)) {
            subjectCombineNum2 = " and st.subjectCombineNum={subjectCombineNum}";
        } else {
            subjectCombineNum2 = "";
        }
        if (!"-1".equals(sex) && !"".equals(sex) && !"null".equals(sex) && null != sex) {
            sex2 = " and st.sex={sex}";
        } else {
            sex2 = "";
        }
        if (null != studentName && !"".equals(studentName) && !"null".equals(studentName)) {
            studentName2 = " and st.studentName like '%" + studentName + "%'";
        } else {
            studentName2 = "";
        }
        if (null == studentId || "".equals(studentId) || "null".equals(studentId)) {
        }
        if (null != note && !"".equals(note) && !"null".equals(note)) {
            note2 = " and st.description like '%" + note + "%'";
        } else {
            note2 = "";
        }
        if (position.indexOf("5") != -1 || position.indexOf("0") != -1 || userId.equals("-1") || userId.equals("-2") || position.equals("999")) {
            if (!userId.equals("-1") && !userId.equals("-2") && position.indexOf("0") == -1 && position.indexOf("999") == -1) {
                strPosition = strPosition + " inner JOIN(   SELECT schoolnum from userposition WHERE  userNum = {userId}  and (type=0 or type=5)  )u on u.schoolnum = st.schoolNum ";
            }
            sql = "SELECT st.jie,st.schoolNum,sl.schoolName,g.gradeNum,g.gradeName,c.classNum classNum_num,c.className, c.studentType,c.classType, st.studentNum,st.studentId,st.studentName,st.yzexaminationnum,st.oldName,st.sex ,st.note,st.type,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext3,uu.mobile ext1,sb.subjectName,lc.className classname1,sb.ordernum,GROUP_CONCAT(sb.subjectName,'-',lc.className ORDER BY sb.orderNum ASC) ext2,st.xuejiSchool,st.xuejiClass,st.homeAddress,st.description  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + strPosition + " LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum    LEFT JOIN levelstudent ls on st.id=ls.sid    LEFT JOIN levelclass lc on lc.id=ls.classNum    inner JOIN user uu ON st.id=uu.userid   and uu.userType=2   LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum  ";
        } else if (position.indexOf("3") != -1) {
            String str2 = "union SELECT st.jie,st.schoolNum,sl.schoolName,g.gradeNum,g.gradeName,c.classNum classNum_num,c.className, c.studentType, st.studentNum,st.studentId,st.studentName,st.oldName,st.sex ,st.note,st.type,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext3,uu.mobile ext1,sb.subjectName,lc.className classname1,sb.ordernum,,GROUP_CONCAT(sb.subjectName,'-',lc.className ORDER BY sb.orderNum ASC)  ext2  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT schoolnum,gradeNum,classNum from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum    LEFT JOIN levelstudent ls on st.id=ls.sid    LEFT JOIN levelclass lc on lc.id=ls.classNum    inner JOIN user uu ON st.id=uu.userid   and uu.userType=2   LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum  ";
            sql = "SELECT st.jie,st.schoolNum,sl.schoolName,g.gradeNum,g.gradeName,c.classNum classNum_num,c.className, c.studentType, st.studentNum,st.studentId,st.studentName,st.oldName,st.sex ,st.note,st.type,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext3,uu.mobile ext1,sb.subjectName,lc.className classname1,sb.ordernum,GROUP_CONCAT(sb.subjectName,'-',lc.className ORDER BY sb.orderNum ASC) ext2,st.xuejiSchool,st.xuejiClass,st.homeAddress,st.description  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + (strPosition + " inner JOIN(   SELECT schoolnum,gradeNum from userposition WHERE userNum = {userId}  and type=3  )u on u.schoolnum = st.schoolNum  and u.gradeNum = st.gradeNum") + " LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum    LEFT JOIN levelstudent ls on st.id=ls.sid    LEFT JOIN levelclass lc on lc.id=ls.classNum    inner JOIN user uu ON st.id=uu.userid   and uu.userType=2   LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum  ";
            List<Userposition> up = getPositionByuser(userId);
            for (int i = 0; i < up.size(); i++) {
                String type = up.get(i).getType();
                String gra = up.get(i).getGradeNum();
                if (gra.indexOf(gradeNum) != -1 && "2".equals(type)) {
                    sql = "SELECT st.jie,st.schoolNum,sl.schoolName,g.gradeNum,g.gradeName,c.classNum classNum_num,c.className, c.studentType, st.studentNum,st.studentId,st.studentName,st.oldName,st.sex ,st.note,st.type,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext3,uu.mobile ext1,sb.subjectName,lc.className classname1,sb.ordernum,GROUP_CONCAT(sb.subjectName,'-',lc.className ORDER BY sb.orderNum ASC) ext2,st.xuejiSchool,st.xuejiClass,st.homeAddress,st.description  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT schoolnum,gradeNum,classNum from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum    LEFT JOIN levelstudent ls on st.id=ls.sid    LEFT JOIN levelclass lc on lc.id=ls.classNum    inner JOIN user uu ON st.id=uu.userid   and uu.userType=2   LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum  ";
                }
            }
        } else if (position.indexOf("2") != -1) {
            sql = "SELECT st.jie,st.schoolNum,sl.schoolName,g.gradeNum,g.gradeName,c.classNum classNum_num,c.className, c.studentType, st.studentNum,st.studentId,st.studentName,st.oldName,st.sex ,st.note,st.type,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext3,uu.mobile ext1,sb.subjectName,lc.className classname1,sb.ordernum,,GROUP_CONCAT(sb.subjectName,'-',lc.className ORDER BY sb.orderNum ASC) ext2,st.xuejiSchool,st.xuejiClass,st.homeAddress,st.description  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT schoolnum,gradeNum,classNum from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum    LEFT JOIN levelstudent ls on st.id=ls.sid    LEFT JOIN levelclass lc on lc.id=ls.classNum    inner JOIN user uu ON st.id=uu.userid   and uu.userType=2   LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum  ";
        }
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql + " where st.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' and st.nodel=0 " + leicengStr + sch + schNum + graNum + jieInfo + classNum2 + subjectCombineNum2 + sex2 + studentName2 + note2 + sidsStr + " GROUP BY st.studentId   order by st.schoolNum*1, st.gradeNum*0.1,c.classNum*1 ASC,st.studentId asc  ", Student.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> ExcelOut2(String schoolNum, String gradeNum, String classNum, String sex, String studentId, String studentName, String note, String jie, String position, String userId, String[] stuIds, String leiceng, String subjectCombineNum) {
        String classNum2;
        String subjectCombineNum2;
        String sex2;
        String studentName2;
        String note2;
        Map args = new HashMap();
        args.put("userId", userId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("leiceng", leiceng);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("jie", jie);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("subjectCombineNum", subjectCombineNum);
        args.put("sex", sex);
        args.put("studentName", "%" + studentName + "%");
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("note", "%" + note + "%");
        String sql = "";
        String strSchauthormanage = "";
        String statisticitem = "";
        String sch = "";
        String schNum = "";
        String jieInfo = "";
        String leicengStr = "";
        String strPosition = "";
        Map<String, Object> ismanageMap = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2") && null == ismanageMap) {
            strSchauthormanage = " left join schoolscanpermission s on s.schoolNum = sl.id and s.userNum={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ";
            sch = "  and (s.schoolNum is not null or t.schoolNum is not null) ";
        }
        if (!"-1".equals(schoolNum)) {
            schNum = " and st.schoolNum={schoolNum}";
        } else if (!leiceng.equals("")) {
            leicengStr = leicengStr + " and ss.sItemId is not null";
        }
        if ("-1".equals(schoolNum) && !leiceng.equals("")) {
            statisticitem = " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId={leiceng}) ss on st.schoolNum = ss.sItemId";
        }
        String sids = "";
        String sidsStr = "";
        if (stuIds != null) {
            for (String str : stuIds) {
                sids = sids + str + Const.STRING_SEPERATOR;
            }
            args.put("sids", sids.substring(0, sids.length() - 1));
            sidsStr = " and st.id in ({sids[]}) ";
        }
        String graNum = "";
        if (!"-1".equals(gradeNum) && !"".equals(gradeNum) && !"null".equals(gradeNum) && null != gradeNum) {
            graNum = " and st.gradeNum={gradeNum}";
            jieInfo = " and st.jie={jie}";
        }
        if (!"-1".equals(classNum) && !"".equals(classNum) && !"null".equals(classNum) && null != classNum) {
            classNum2 = " and st.classNum={classNum}";
        } else {
            classNum2 = "";
        }
        if (!"-1".equals(subjectCombineNum)) {
            subjectCombineNum2 = " and st.subjectCombineNum={subjectCombineNum}";
        } else {
            subjectCombineNum2 = "";
        }
        if (!"-1".equals(sex) && !"".equals(sex) && !"null".equals(sex) && null != sex) {
            sex2 = " and st.sex={sex}";
        } else {
            sex2 = "";
        }
        if (null != studentName && !"".equals(studentName) && !"null".equals(studentName)) {
            studentName2 = " and st.studentName like {studentName}";
        } else {
            studentName2 = "";
        }
        if (null == studentId || "".equals(studentId) || "null".equals(studentId)) {
        }
        if (null != note && !"".equals(note) && !"null".equals(note)) {
            note2 = " and st.description like {note}";
        } else {
            note2 = "";
        }
        if (position.indexOf("5") != -1 || position.indexOf("0") != -1 || userId.equals("-1") || userId.equals("-2")) {
            if (!userId.equals("-1") && !userId.equals("-2") && position.indexOf("0") == -1) {
                strPosition = strPosition + " inner JOIN(   SELECT schoolnum from userposition WHERE  userNum = {userId}  and (type=0 or type=5)  )u on u.schoolnum = st.schoolNum ";
            }
            sql = "SELECT st.jie,sl.schoolName,g.gradeName,d.name sjtTypeName,c.className,d1.name classTypeName,st.studentName,st.studentId,st.studentNum,st.sex,d2.name source,d3.name stuTypeName,st.yzexaminationnum,'' pasd,uu.mobile ext1,st.xuejiSchool,st.xuejiClass,st.homeAddress,d4.name note,st.description,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext3  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + strPosition + " LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum    LEFT JOIN levelstudent ls on st.id=ls.sid    LEFT JOIN levelclass lc on lc.id=ls.classNum    inner JOIN user uu ON st.id=uu.userid   and uu.userType=2   LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum  ";
        } else if (position.indexOf("3") != -1) {
            String str2 = "union SELECT st.jie,sl.schoolName,g.gradeName,d.name sjtTypeName,c.className,d1.name classTypeName,st.studentName,st.studentId,st.studentNum,st.sex,d2.name source,d3.name stuTypeName,st.yzexaminationnum,'' pasd,uu.mobile ext1,'' note,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext3  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT schoolnum,gradeNum,classNum from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum    LEFT JOIN levelstudent ls on st.id=ls.sid    LEFT JOIN levelclass lc on lc.id=ls.classNum    inner JOIN user uu ON st.id=uu.userid   and uu.userType=2   LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum  ";
            sql = "SELECT st.jie,sl.schoolName,g.gradeName,d.name sjtTypeName,c.className,d1.name classTypeName,st.studentName,st.studentId,st.studentNum,st.sex,d2.name source,d3.name stuTypeName,st.yzexaminationnum,'' pasd,uu.mobile ext1,st.xuejiSchool,st.xuejiClass,st.homeAddress,d4.name note,st.description,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext3  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + (strPosition + " inner JOIN(   SELECT schoolnum,gradeNum from userposition WHERE userNum = {userId}  and type=3  )u on u.schoolnum = st.schoolNum  and u.gradeNum = st.gradeNum") + " LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum    LEFT JOIN levelstudent ls on st.id=ls.sid    LEFT JOIN levelclass lc on lc.id=ls.classNum    inner JOIN user uu ON st.id=uu.userid   and uu.userType=2   LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum  ";
            List<Userposition> up = getPositionByuser(userId);
            for (int i = 0; i < up.size(); i++) {
                String type = up.get(i).getType();
                String gra = up.get(i).getGradeNum();
                if (gra.indexOf(gradeNum) != -1 && "2".equals(type)) {
                    sql = "SELECT st.jie,sl.schoolName,g.gradeName,d.name sjtTypeName,c.className,d1.name classTypeName,st.studentName,st.studentId,st.studentNum,st.sex,d2.name source,d3.name stuTypeName,st.yzexaminationnum,'' pasd,uu.mobile ext1,st.xuejiSchool,st.xuejiClass,st.homeAddress,d4.name note,st.description,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext3  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT schoolnum,gradeNum,classNum from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum    LEFT JOIN levelstudent ls on st.id=ls.sid    LEFT JOIN levelclass lc on lc.id=ls.classNum    inner JOIN user uu ON st.id=uu.userid   and uu.userType=2   LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum  ";
                }
            }
        } else if (position.indexOf("2") != -1) {
            sql = "SELECT st.jie,sl.schoolName,g.gradeName,d.name sjtTypeName,c.className,d1.name classTypeName,st.studentName,st.studentId,st.studentNum,st.sex,d2.name source,d3.name stuTypeName,st.yzexaminationnum,'' pasd,uu.mobile ext1,st.xuejiSchool,st.xuejiClass,st.homeAddress,d4.name note,st.description,if(sj.subjectCombineNum=0||ISNULL(sj.subjectCombineNum),'',sj.subjectCombineName) ext3  from student st   LEFT JOIN school sl ON st.schoolNum=sl.id" + strSchauthormanage + statisticitem + " inner JOIN(  \tSELECT schoolnum,gradeNum,classNum from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN subjectcombine sj on st.subjectCombineNum=sj.subjectCombineNum    LEFT JOIN levelstudent ls on st.id=ls.sid    LEFT JOIN levelclass lc on lc.id=ls.classNum    inner JOIN user uu ON st.id=uu.userid   and uu.userType=2   LEFT JOIN `subject` sb on ls.subjectNum=sb.subjectNum  ";
        }
        return this.dao2._queryMapList((sql + "inner join data d on c.studentType=d.value and d.type=25 inner join data d1 on c.classType=d1.value and d1.type=3 inner join data d2 on st.source=d2.value and d2.type=26 inner join data d3 on st.type=d3.value and d3.type=22 inner join data d4 on st.note=d4.value and d4.type=28 ") + " where st.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' and st.nodel=0 " + leicengStr + sch + schNum + graNum + jieInfo + classNum2 + subjectCombineNum2 + sex2 + studentName2 + note2 + sidsStr + " GROUP BY st.studentId   order by st.schoolNum*1, st.gradeNum*0.1,c.classNum*1 ASC,st.studentId asc  ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List ExcelOutLevel(String schoolNum, String gradeNum, String classNum, String sex, String studentId, String studentName, String note, String jie, String subjectNum, String xuankaoNum, String position, String userId, String[] stuIds, String leiceng) {
        String subjectNum2;
        String classNum2;
        String xuankaoNum2;
        String sex2;
        String stuID;
        String note2;
        Map args = new HashMap();
        args.put("userId", userId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("leiceng", leiceng);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("jie", jie);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("xuankaoNum", xuankaoNum);
        args.put("sex", sex);
        args.put("note", "%" + note + "%");
        String sql = "";
        String strSchauthormanage = "";
        String statisticitem = "";
        String sch = "";
        String schNum = "";
        String jieInfo = "";
        String leicengStr = "";
        String strPosition = "";
        Map<String, Object> ismanageMap = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2") && null == ismanageMap) {
            strSchauthormanage = " left join schoolscanpermission s on s.schoolNum = sl.id and s.userNum={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ";
            sch = "  and (s.schoolNum is not null or t.schoolNum is not null) ";
        }
        if (!"-1".equals(schoolNum)) {
            schNum = " and st.schoolNum={schoolNum}";
        } else if (!leiceng.equals("")) {
            leicengStr = leicengStr + " and ss.sItemId is not null";
        }
        if ("-1".equals(schoolNum) && !leiceng.equals("")) {
            statisticitem = " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId={leiceng}) ss on st.schoolNum = ss.sItemId";
        }
        String sids = "";
        String sidsStr = "";
        if (stuIds != null) {
            for (String str : stuIds) {
                sids = sids + str + Const.STRING_SEPERATOR;
            }
            args.put("sids", sids.substring(0, sids.length() - 1));
            sidsStr = " and st.id in ({sids[]}) ";
        }
        String graNum = "";
        if (!"-1".equals(gradeNum) && !"".equals(gradeNum) && !"null".equals(gradeNum) && null != gradeNum) {
            graNum = " and les.gradeNum={gradeNum}";
        }
        if (null != jie && !jie.equals("-1") && !jie.equals("") && !jie.equals("null")) {
            jieInfo = "and les.jie={jie}  ";
        }
        if (!"-1".equals(subjectNum) && !"".equals(subjectNum) && !"null".equals(subjectNum) && null != subjectNum) {
            subjectNum2 = " and les.subjectNum={subjectNum}";
        } else {
            subjectNum2 = "";
        }
        if (!"-1".equals(classNum) && !"".equals(classNum) && !"null".equals(classNum) && null != classNum) {
            classNum2 = " and les.classNum={classNum}";
        } else {
            classNum2 = "";
        }
        if (!"-1".equals(xuankaoNum) && !"".equals(xuankaoNum) && !"null".equals(xuankaoNum) && null != xuankaoNum) {
            xuankaoNum2 = " and les.xuankaoqufen={xuankaoNum}";
        } else if ("".equals(xuankaoNum)) {
            xuankaoNum2 = " and les.xuankaoqufen is null";
        } else {
            xuankaoNum2 = "";
        }
        if (!"-1".equals(sex) && !"".equals(sex) && !"null".equals(sex) && null != sex) {
            sex2 = " and st.sex={sex}";
        } else {
            sex2 = "";
        }
        if (0 != "" && !"".equals("") && !"null".equals("")) {
            stuID = " and st.studentid={stuID}";
            args.put("stuID", stuID);
        } else {
            stuID = "";
        }
        if (null != note && !"".equals(note) && !"null".equals(note)) {
            note2 = " and les.description like {note}";
        } else {
            note2 = "";
        }
        if (position.indexOf("5") != -1 || position.indexOf("0") != -1 || userId.equals("-1") || userId.equals("-2") || position.equals("999")) {
            if (!userId.equals("-1") && !userId.equals("-2") && position.indexOf("0") == -1 && position.indexOf("999") == -1) {
                strPosition = strPosition + " inner JOIN(   SELECT * from userposition WHERE  userNum = {userId}  and (type=0 or type=5)  )u on u.schoolnum = st.schoolNum ";
            }
            sql = "SELECT les.jie,st.schoolNum,sch.schoolName,g.gradeNum,g.gradeName,c.classNum classNum_num,c.className, c.studentType, st.studentNum,st.studentId,st.studentName,st.oldName,st.sex ,les.description,d.`name` ,sub.subjectName,d1.`name` ext1 FROM levelstudent les  LEFT JOIN student st ON st.id=les.sid  LEFT JOIN grade g ON les.gradeNum=g.gradeNum AND les.schoolNum=g.schoolNum AND les.jie=g.jie  LEFT JOIN levelclass c ON les.classNum=c.id  LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'  LEFT JOIN `data` d1 on d1.`value`=les.xuankaoqufen and d1.type='32'  LEFT JOIN school sch ON les.schoolNum=sch.id " + strSchauthormanage + statisticitem + strPosition + " LEFT JOIN `subject` sub ON les.subjectNum=sub.subjectNum  WHERE g.isDelete='F' " + leicengStr + sch + schNum + graNum + subjectNum2 + classNum2 + xuankaoNum2 + sex2 + studentName + stuID + note2 + sidsStr + jieInfo + " ORDER BY sch.schoolName ASC,g.gradeNum ASC,c.classNum*1 ASC,st.id ";
        } else if (position.indexOf("3") != -1) {
            String sql1 = "union  SELECT les.jie,st.schoolNum,sch.schoolName,g.gradeNum,g.gradeName,c.classNum classNum_num,c.className, c.studentType, st.studentNum,st.studentId,st.studentName,st.oldName,st.sex ,les.description,d.`name` ,sub.subjectName,d1.`name` ext1 FROM levelstudent les  LEFT JOIN student st ON st.id=les.sid  LEFT JOIN grade g ON les.gradeNum=g.gradeNum AND les.schoolNum=g.schoolNum AND les.jie=g.jie  LEFT JOIN levelclass c ON les.classNum=c.id  LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'  LEFT JOIN `data` d1 on d1.`value`=les.xuankaoqufen and d1.type='32'  LEFT JOIN school sch ON les.schoolNum=sch.id " + strSchauthormanage + statisticitem + " LEFT JOIN `subject` sub ON les.subjectNum=sub.subjectNum  inner JOIN(  \tSELECT * from userposition WHERE  userNum = '" + userId + "'  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum WHERE g.isDelete='F' " + leicengStr + sch + schNum + graNum + subjectNum2 + classNum2 + xuankaoNum2 + sex2 + studentName + stuID + note2 + sidsStr + jieInfo;
            sql = "SELECT les.jie,st.schoolNum,sch.schoolName,g.gradeNum,g.gradeName,c.classNum classNum_num,c.className, c.studentType, st.studentNum,st.studentId,st.studentName,st.oldName,st.sex ,les.description,d.`name` ,sub.subjectName,d1.`name` ext1 FROM levelstudent les  LEFT JOIN student st ON st.id=les.sid  LEFT JOIN grade g ON les.gradeNum=g.gradeNum AND les.schoolNum=g.schoolNum AND les.jie=g.jie  LEFT JOIN levelclass c ON les.classNum=c.id  LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'  LEFT JOIN `data` d1 on d1.`value`=les.xuankaoqufen and d1.type='32'  LEFT JOIN school sch ON les.schoolNum=sch.id " + strSchauthormanage + statisticitem + (strPosition + " inner JOIN(   SELECT * from userposition WHERE  userNum = {userId}  and type=3  )u on u.schoolnum = st.schoolNum  and u.gradeNum = st.gradeNum") + " LEFT JOIN `subject` sub ON les.subjectNum=sub.subjectNum  WHERE g.isDelete='F' " + leicengStr + sch + schNum + graNum + subjectNum2 + classNum2 + xuankaoNum2 + sex2 + studentName + stuID + note2 + sidsStr + jieInfo + sql1;
            List<Userposition> up = getPositionByuser(userId);
            for (int i = 0; i < up.size(); i++) {
                String type = up.get(i).getType();
                String gra = up.get(i).getGradeNum();
                if (gra.indexOf(gradeNum) != -1 && "2".equals(type)) {
                    sql = " SELECT les.jie,st.schoolNum,sch.schoolName,g.gradeNum,g.gradeName,c.classNum classNum_num,c.className, c.studentType, st.studentNum,st.studentId,st.studentName,st.oldName,st.sex ,les.description,d.`name` ,sub.subjectName,d1.`name` ext1 FROM levelstudent les  LEFT JOIN student st ON st.id=les.sid  LEFT JOIN grade g ON les.gradeNum=g.gradeNum AND les.schoolNum=g.schoolNum AND les.jie=g.jie  LEFT JOIN levelclass c ON les.classNum=c.id  LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'  LEFT JOIN `data` d1 on d1.`value`=les.xuankaoqufen and d1.type='32'  LEFT JOIN school sch ON les.schoolNum=sch.id " + strSchauthormanage + statisticitem + " LEFT JOIN `subject` sub ON les.subjectNum=sub.subjectNum  inner JOIN(  \tSELECT * from userposition WHERE  userNum = {userId} and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum WHERE g.isDelete='F' " + leicengStr + sch + schNum + graNum + subjectNum2 + classNum2 + xuankaoNum2 + sex2 + studentName + stuID + note2 + sidsStr + jieInfo;
                }
            }
        } else if (position.indexOf("2") != -1) {
            sql = " SELECT les.jie,st.schoolNum,sch.schoolName,g.gradeNum,g.gradeName,c.classNum classNum_num,c.className, c.studentType, st.studentNum,st.studentId,st.studentName,st.oldName,st.sex ,les.description,d.`name` ,sub.subjectName,d1.`name` ext1 FROM levelstudent les  LEFT JOIN student st ON st.id=les.sid  LEFT JOIN grade g ON les.gradeNum=g.gradeNum AND les.schoolNum=g.schoolNum AND les.jie=g.jie  LEFT JOIN levelclass c ON les.classNum=c.id  LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'  LEFT JOIN `data` d1 on d1.`value`=les.xuankaoqufen and d1.type='32'  LEFT JOIN school sch ON les.schoolNum=sch.id " + strSchauthormanage + statisticitem + " LEFT JOIN `subject` sub ON les.subjectNum=sub.subjectNum  inner JOIN(  \tSELECT * from userposition WHERE  userNum = {userId}  and type=2  )u1 on u1.schoolnum = st.schoolNum  and u1.gradeNum = st.gradeNum   AND u1.classNum = st.classNum WHERE g.isDelete='F' " + leicengStr + sch + schNum + graNum + subjectNum2 + classNum2 + xuankaoNum2 + sex2 + studentName + stuID + note2 + sidsStr + jieInfo;
        }
        String sqlAll = "" + sql;
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sqlAll, Levelstudent.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List getDataSubjectType(String type, String value) {
        Map args = StreamMap.create().put("type", (Object) type).put("value", (Object) value);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT name, value  FROM `data` WHERE type={type} AND `value`={value}", Data.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getStudentlevelSubject(String schoolNum, String gradeNum, String jie) {
        String schNum = "";
        String graNum = "";
        String jieInfo = "";
        if (!"-1".equals(schoolNum) && !"".equals(schoolNum) && !"null".equals(schoolNum) && null != schoolNum) {
            schNum = " and schoolNum={schoolNum}";
        }
        if (!"-1".equals(gradeNum) && !"".equals(gradeNum) && !"null".equals(gradeNum) && null != gradeNum) {
            graNum = " and gradeNum={gradeNum}";
            jieInfo = " and jie={jie}";
        }
        String str = "SELECT s.subjectName,CAST(ls.rownum AS signed) rownum  from ( \tselect subjectNum,@rownum:=@rownum+1 rownum from levelstudent,(SELECT @rownum := -1) r where 1=1 " + schNum + graNum + jieInfo + " GROUP BY subjectNum \t)ls LEFT JOIN `subject` s on ls.subjectNum=s.subjectNum ORDER BY s.orderNum ";
        String sql = "SELECT ls.subjectName,CAST(@rownum:=@rownum+1 AS signed) rownum from( \t\tSELECT ls.subjectNum,sj.subjectName from levelstudent ls INNER JOIN `subject` sj on ls.subjectNum=sj.subjectNum where 1=1 " + schNum + graNum + jieInfo + " GROUP BY ls.subjectNum ORDER BY sj.orderNum \t)ls,(SELECT @rownum := -1) r";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        Map<String, String> levelMap = this.dao2._queryOrderMap(sql, TypeEnum.StringString, args);
        return levelMap;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List getSourceData(String studentNum, String gradeNum, String classNum, String schoolNum, String type) {
        String stunum = "";
        String grnum = "";
        String clanum = "";
        String scnum = "";
        String ty = "";
        if (null != studentNum && !studentNum.equals("-1") && !studentNum.equals("") && !studentNum.equals("null")) {
            stunum = "AND s.studentId={studentNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("") && !gradeNum.equals("null")) {
            grnum = "AND s.gradeNum={gradeNum}  ";
        }
        if (null != classNum && !classNum.equals("-1") && !classNum.equals("") && !classNum.equals("null")) {
            clanum = "AND s.classNum={classNum}  ";
        }
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("") && !schoolNum.equals("null")) {
            scnum = "AND s.schoolNum={schoolNum} ";
        }
        if (null != type && !type.equals("-1") && !type.equals("") && !type.equals("null")) {
            ty = ty + "AND d.type={type}";
        }
        String sql = "SELECT s.source,d.`name` FROM student s LEFT JOIN `data` d ON d.`value`=s.source WHERE 1=1 " + stunum + grnum + clanum + scnum + ty;
        Map args = StreamMap.create().put("studentNum", (Object) studentNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("type", (Object) type);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql, Data.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List getSourceDataLevel(String studentNum, String gradeNum, String classNum, String schoolNum, String type) {
        String stunum = "";
        String grnum = "";
        String clanum = "";
        String scnum = "";
        String ty = "";
        if (null != studentNum && !studentNum.equals("-1") && !studentNum.equals("")) {
            stunum = "AND st.studentId={studentNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            grnum = "AND s.gradeNum={gradeNum}  ";
        }
        if (null != classNum && !classNum.equals("-1") && !classNum.equals("") && !classNum.equals("null")) {
            clanum = "AND s.classNum={classNum}  ";
        }
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            scnum = "AND s.schoolNum={schoolNum} ";
        }
        if (null != type && !type.equals("-1") && !type.equals("")) {
            ty = ty + "AND d.type={type}";
        }
        String sql = "SELECT st.source,d.`name` FROM levelstudent s LEFT JOIN student st ON s.sid=st.id LEFT JOIN `data` d ON d.`value`=st.source WHERE 1=1 " + stunum + grnum + clanum + scnum + ty;
        Map args = StreamMap.create().put("studentNum", (Object) studentNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("type", (Object) type);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql, Data.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer Student_count(String schoolNum, String gradeNum, String classNum, String sex, String studentId, String studentName, String note, String Student_Type, String jie, String position, String userId, String[] stuIds, String subjectCombineNum) {
        String userStr = "";
        String sql = "SELECT COUNT(1) from student s LEFT JOIN grade g ON s.gradeNum=g.gradeNum and s.schoolNum=g.schoolNum and s.jie=g.jie LEFT JOIN class c ON s.classNum=c.id  ";
        if (!userId.equals("-1") && !userId.equals("-2") && position.indexOf("0") == -1 && position.indexOf("5") == -1) {
            List<Userposition> up = getPositionByuser(userId);
            for (int i = 0; i < up.size(); i++) {
                String type = up.get(i).getType();
                String gra = up.get(i).getGradeNum();
                if (gra.indexOf(gradeNum) != -1) {
                    if (type.indexOf("3") != -1) {
                        sql = sql + " LEFT JOIN userposition u on u.gradeNum=s.gradeNum and u.schoolnum=s.schoolNum ";
                    } else if ("2".equals(type)) {
                        sql = sql + " LEFT JOIN userposition u on u.gradeNum=s.gradeNum and u.schoolnum=s.schoolNum and u.classNum=s.classNum ";
                    }
                    userStr = " and u.userNum={userId} ";
                }
            }
        }
        String sql2 = sql + "LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'left JOIN school sch ON s.schoolNum=sch.id  where g.isDelete='F' " + userStr + "";
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql2 = sql2 + "and s.schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql2 = sql2 + "and s.gradeNum={gradeNum}  ";
        }
        if (null != classNum && !classNum.equals("-1") && !classNum.equals("")) {
            sql2 = sql2 + "and s.classNum={classNum}  ";
        }
        if (null != subjectCombineNum && !subjectCombineNum.equals("-1") && !subjectCombineNum.equals("")) {
            sql2 = sql2 + "and s.subjectCombineNum={subjectCombineNum}  ";
        }
        if (null != sex && !sex.equals("-1") && !sex.equals("")) {
            sql2 = sql2 + "and s.sex={sex} ";
        }
        if (null != studentId && !studentId.equals("-1") && !studentId.equals("")) {
            sql2 = sql2 + "and s.studentId={studentId}  ";
        }
        if (null != note && !note.equals("-1") && !note.equals("")) {
            sql2 = sql2 + "and s.description={note} ";
        }
        if (null != Student_Type && !Student_Type.equals("-1") && !Student_Type.equals("")) {
            sql2 = sql2 + "and c.studentType={Student_Type} ";
        }
        if (null != jie && !jie.equals("-1") && !jie.equals("") && !jie.equals("null")) {
            sql2 = sql2 + "and s.jie={jie}  ";
        }
        String sql3 = sql2 + "AND c.classNum is NOT NULL and s.isDelete='F'  ";
        Map args = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("subjectCombineNum", (Object) subjectCombineNum).put("sex", (Object) sex).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("note", (Object) note).put("Student_Type", (Object) Student_Type).put("jie", (Object) jie);
        return Integer.valueOf(this.dao2._queryObject(sql3, args).toString());
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer studentType_count(String gradeNum, String studentType, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_studentType, (Object) studentType);
        return Integer.valueOf(this.dao2._queryObject("SELECT COUNT(studentType) from student s LEFT JOIN class c ON s.classNum=c.id  and s.jie=c.jie where s.gradeNum={gradeNum} and c.studentType={studentType} ", args).toString());
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List classNum_count(String schoolNum, String gradeNum, String classNum, String sex, String studentId, String studentName, String note, String jie, String position, String userId, String[] stuIds) {
        String userStr = "";
        String sql = "SELECT DISTINCT c.className,s.classNum,c.classNum classNum_num,c.studentType,sch.schoolName from student s LEFT JOIN grade g ON s.gradeNum=g.gradeNum and s.schoolNum=g.schoolNum and s.jie=g.jie LEFT JOIN class c ON s.classNum=c.id  ";
        if (!userId.equals("-1") && !userId.equals("-2") && !position.equals("999") && position.indexOf("0") == -1 && position.indexOf("5") == -1) {
            List<Userposition> up = getPositionByuser(userId);
            for (int i = 0; i < up.size(); i++) {
                String type = up.get(i).getType();
                String gra = up.get(i).getGradeNum();
                if (gra.indexOf(gradeNum) != -1) {
                    if (type.indexOf("3") != -1) {
                        sql = sql + " LEFT JOIN userposition u on u.gradeNum=s.gradeNum and u.schoolnum=s.schoolNum ";
                    } else if ("2".equals(type)) {
                        sql = sql + " LEFT JOIN userposition u on u.gradeNum=s.gradeNum and u.schoolnum=s.schoolNum and u.classNum=s.classNum ";
                    }
                    userStr = " and u.userNum={userId} ";
                }
            }
        }
        String sql2 = sql + "LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'left JOIN school sch ON s.schoolNum=sch.id  where g.isDelete='F' " + userStr + "";
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql2 = sql2 + "and  s.schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql2 = sql2 + "and s.gradeNum={gradeNum}  ";
        }
        if (null != classNum && !classNum.equals("-1") && !classNum.equals("")) {
            sql2 = sql2 + "and s.classNum={classNum}  ";
        }
        if (null != studentName && !studentName.equals("-1") && !studentName.equals("")) {
            sql2 = sql2 + " and s.studentName like {studentName} ";
        }
        if (null != sex && !sex.equals("-1") && !sex.equals("")) {
            sql2 = sql2 + "and s.sex={sex} ";
        }
        if (null != studentId && !studentId.equals("-1") && !studentId.equals("")) {
            sql2 = sql2 + "and s.studentId={studentId}  ";
        }
        if (null != note && !note.equals("-1") && !note.equals("")) {
            sql2 = sql2 + "and s.description={note} ";
        }
        if (null != jie && !jie.equals("-1") && !jie.equals("") && !jie.equals("null")) {
            sql2 = sql2 + "and s.jie={jie}  ";
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("studentName", (Object) ("%" + studentName + "%")).put("sex", (Object) sex).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("note", (Object) note).put("jie", (Object) jie);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql2 + "AND c.classNum is NOT NULL and s.isDelete='F' ORDER BY sch.schoolName  ASC, c.classNum*1 ASC ", Student.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List classNum_countLevel(String schoolNum, String gradeNum, String classNum, String sex, String studentId, String studentName, String note, String jie, String position, String userId, String[] stuIds) {
        String sql = "SELECT DISTINCT c.className,s.classNum,c.classNum classNum_num,c.studentType from levelstudent s LEFT JOIN student st ON s.sid=st.id LEFT JOIN grade g ON s.gradeNum=g.gradeNum and s.schoolNum=g.schoolNum and s.jie=g.jie LEFT JOIN levelclass c ON s.classNum=c.id  ";
        String userStr = "";
        if (!userId.equals("-1") && !userId.equals("-2") && !position.equals("999") && position.indexOf("0") == -1 && position.indexOf("5") == -1) {
            List<Userposition> up = getPositionByuser(userId);
            for (int i = 0; i < up.size(); i++) {
                String type = up.get(i).getType();
                String gra = up.get(i).getGradeNum();
                if (gra.indexOf(gradeNum) != -1) {
                    if (type.indexOf("3") != -1) {
                        sql = sql + " LEFT JOIN userposition u on u.gradeNum=s.gradeNum and u.schoolnum=s.schoolNum ";
                    } else if ("2".equals(type)) {
                        sql = sql + " LEFT JOIN userposition u on u.gradeNum=s.gradeNum and u.schoolnum=s.schoolNum and u.classNum=s.classNum ";
                    }
                    userStr = " and u.userNum={userId} ";
                }
            }
        }
        String sql2 = sql + "LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'left JOIN school sch ON s.schoolNum=sch.id  where g.isDelete='F' " + userStr;
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql2 = sql2 + "and  s.schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql2 = sql2 + "and s.gradeNum={gradeNum}  ";
        }
        if (null != classNum && !classNum.equals("-1") && !classNum.equals("")) {
            sql2 = sql2 + "and s.classNum={classNum}  ";
        }
        if (null != sex && !sex.equals("-1") && !sex.equals("")) {
            sql2 = sql2 + "and st.sex={sex} ";
        }
        if (null != studentId && !studentId.equals("-1") && !studentId.equals("")) {
            sql2 = sql2 + "and st.studentId={studentId}  ";
        }
        if (null != note && !note.equals("-1") && !note.equals("")) {
            sql2 = sql2 + "and s.description={note} ";
        }
        if (null != jie && !jie.equals("-1") && !jie.equals("") && !jie.equals("null")) {
            sql2 = sql2 + "and s.jie={jie}  ";
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("sex", (Object) sex).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("note", (Object) note).put("jie", (Object) jie);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql2 + "AND c.classNum is NOT NULL ORDER BY sch.schoolName  ASC, c.classNum*1 ASC ", Levelstudent.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List studentType(String schoolNum, String gradeNum, String classNum, String sex, String studentId, String studentName, String note, String jie, String position, String userId, String[] stuIds) {
        String sql = "SELECT DISTINCT c.studentType from student s  LEFT JOIN grade g ON s.gradeNum=g.gradeNum and s.schoolNum=g.schoolNum and s.jie=g.jie LEFT JOIN class c ON s.classNum=c.id  LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'left JOIN school sch ON s.schoolNum=sch.id  where g.isDelete='F' ";
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql = sql + "and  s.schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql = sql + "and s.gradeNum={gradeNum} ";
        }
        if (null != classNum && !classNum.equals("-1") && !classNum.equals("")) {
            sql = sql + "and s.classNum={classNum}  ";
        }
        if (null != studentName && !studentName.equals("-1") && !studentName.equals("")) {
            sql = sql + " and s.studentName like {studentName} ";
        }
        if (null != sex && !sex.equals("-1") && !sex.equals("")) {
            sql = sql + "and s.sex={sex} ";
        }
        if (null != studentId && !studentId.equals("-1") && !studentId.equals("")) {
            sql = sql + "and s.studentId={studentId}  ";
        }
        if (null != note && !note.equals("-1") && !note.equals("")) {
            sql = sql + "and s.description={note} ";
        }
        if (null != jie && !jie.equals("-1") && !jie.equals("") && !jie.equals("null")) {
            sql = sql + "and s.jie={jie} ";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("studentName", (Object) ("%" + studentName + "%")).put("sex", (Object) sex).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("note", (Object) note).put("jie", (Object) jie);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql + "AND c.classNum is NOT NULL ORDER BY sch.schoolName  ASC, c.classNum*1 ASC ", Student.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List studentTypeLevel(String schoolNum, String gradeNum, String classNum, String sex, String studentId, String studentName, String note, String jie, String position, String userId, String[] stuIds) {
        String sql = "SELECT DISTINCT c.studentType from levelstudent s  LEFT JOIN grade g ON s.gradeNum=g.gradeNum and s.schoolNum=g.schoolNum and s.jie=g.jie LEFT JOIN student st ON s.sid=st.id LEFT JOIN levelclass c ON s.classNum=c.id  LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'left JOIN school sch ON s.schoolNum=sch.id  where g.isDelete='F' ";
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql = sql + "and  s.schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql = sql + "and s.gradeNum={gradeNum} ";
        }
        if (null != classNum && !classNum.equals("-1") && !classNum.equals("")) {
            sql = sql + "and s.classNum={classNum}  ";
        }
        if (null != sex && !sex.equals("-1") && !sex.equals("")) {
            sql = sql + "and st.sex={sex} ";
        }
        if (null != studentId && !studentId.equals("-1") && !studentId.equals("")) {
            sql = sql + "and s.studentId={studentId}  ";
        }
        if (null != note && !note.equals("-1") && !note.equals("")) {
            sql = sql + "and s.description={note} ";
        }
        if (null != jie && !jie.equals("-1") && !jie.equals("") && !jie.equals("null")) {
            sql = sql + "and s.jie={jie}  ";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("sex", (Object) sex).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("note", (Object) note).put("jie", (Object) jie);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql + "AND c.classNum is NOT NULL ORDER BY sch.schoolName  ASC, c.classNum*1 ASC ", Levelstudent.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List student_School() {
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT s.schoolNum ,sch.schoolName from student s LEFT JOIN grade g ON s.gradeNum=g.gradeNum and s.schoolNum=g.schoolNum and s.jie=g.jie LEFT JOIN class c ON s.classNum=c.id  LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'left JOIN school sch ON s.schoolNum=sch.id  where s.isDelete='F'  ORDER BY s.schoolNum ASC ", Student.class, null);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List studentLimitSchool(String userId, String leiceng) {
        Map args = StreamMap.create().put("userId", (Object) userId).put("leiceng", (Object) leiceng);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        String sql = "SELECT DISTINCT s.schoolNum ,sch.schoolName from student s LEFT JOIN grade g ON s.gradeNum=g.gradeNum and s.schoolNum=g.schoolNum and s.jie=g.jie LEFT JOIN class c ON s.classNum=c.id  LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'left JOIN school sch ON s.schoolNum=sch.id  ";
        String sch = "";
        String staSql = "";
        if (!userId.equals("-1") || !userId.equals("-2") || null == map) {
            sql = sql + " left join schoolscanpermission sa on sa.schoolNum = sch.id and sa.userNum={userId}  left join user t on t.schoolNum = sch.id  and t.id = {userId} and t.usertype=1 ";
            sch = " and (sa.schoolNum is not null or t.schoolNum is not null) ";
        }
        if (!leiceng.equals("")) {
            sql = sql + " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId={leiceng}) ss on s.schoolNum = ss.sItemId";
            staSql = " and ss.sItemId is not null ";
        }
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql + " where s.isDelete='F' " + sch + staSql + " ORDER BY s.schoolNum ASC ", Student.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List studentSchoolList(String schoolNum, String level) {
        String sc = "";
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sc = " AND s.schoolNum={schoolNum}";
        }
        String sql = "SELECT DISTINCT s.schoolNum ,sch.schoolName from student s LEFT JOIN grade g ON s.gradeNum=g.gradeNum and s.schoolNum=g.schoolNum and s.jie=g.jie LEFT JOIN class c ON s.classNum=c.id  LEFT JOIN `data` d ON c.classType=d.`value`  AND d.type='3'left JOIN school sch ON s.schoolNum=sch.id  where s.isDelete='F' " + sc + " ORDER BY s.schoolNum ASC ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql, Student.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List student_grade(String schoolNum, String gradeNum, String jie, String position, String userId, String[] stuIds) {
        String userStr = "";
        String sql = "select DISTINCT g.gradeName,s.gradeNum from student s LEFT JOIN grade g ON s.gradeNum=g.gradeNum and s.jie=g.jie ";
        if (!userId.equals("-1") && !userId.equals("-2") && !position.equals("999") && position.indexOf("0") == -1 && position.indexOf("5") == -1) {
            sql = sql + " LEFT JOIN userposition u on u.gradeNum=s.gradeNum and u.schoolnum=s.schoolNum and u.type<>1 and u.type<>4 ";
            userStr = " and u.userNum={userId} ";
        }
        String sql2 = sql + "where g.isDelete='F' " + userStr + "";
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql2 = sql2 + "  and s.schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql2 = sql2 + " and s.gradeNum={gradeNum}  ";
        }
        if (null != jie && !jie.equals("-1") && !jie.equals("") && !jie.equals("null")) {
            sql2 = sql2 + "and s.jie={jie}  ";
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql2 + "ORDER BY s.gradeNum ASC", Student.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List student_gradeLevel(String schoolNum, String gradeNum, String jie, String position, String userId, String[] stuIds) {
        String sql = "select DISTINCT g.gradeName,s.gradeNum from levelstudent s LEFT JOIN grade g ON s.gradeNum=g.gradeNum and s.jie=g.jie ";
        String userStr = "";
        if (!userId.equals("-1") && !userId.equals("-2") && !position.equals("999") && position.indexOf("0") == -1 && position.indexOf("5") == -1) {
            sql = sql + " LEFT JOIN userposition u on u.gradeNum=s.gradeNum and u.schoolnum=s.schoolNum and u.type<>1 and u.type<>4 ";
            userStr = " and u.userNum={userId} ";
        }
        String sql2 = sql + "where g.isDelete='F' " + userStr;
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql2 = sql2 + "  and s.schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql2 = sql2 + " and s.gradeNum={gradeNum}  ";
        }
        if (null != jie && !jie.equals("-1") && !jie.equals("") && !jie.equals("null")) {
            sql2 = sql2 + "and s.jie={jie}  ";
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList(sql2 + "ORDER BY s.gradeNum ASC", Levelstudent.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer studentType_count_all(String schoolNum, String gradeNum, String classNum, String sex, String studentType, String jie, String position, String userId, String[] stuIds) {
        String userStr = "";
        String sql = "SELECT COUNT(DISTINCT s.id) count from student s LEFT JOIN class c ON s.classNum=c.id  ";
        if (!userId.equals("-1") && !userId.equals("-2") && position.indexOf("0") == -1 && position.indexOf("5") == -1) {
            List<Userposition> up = getPositionByuser(userId);
            for (int i = 0; i < up.size(); i++) {
                String type = up.get(i).getType();
                String gra = up.get(i).getGradeNum();
                if (gra.indexOf(gradeNum) != -1) {
                    if (type.indexOf("3") != -1) {
                        sql = sql + " LEFT JOIN userposition u on u.gradeNum=s.gradeNum and u.schoolnum=s.schoolNum ";
                    } else if ("2".equals(type)) {
                        sql = sql + " LEFT JOIN userposition u on u.gradeNum=s.gradeNum and u.schoolnum=s.schoolNum and u.classNum=s.classNum ";
                    }
                    userStr = " and u.userNum={userId} ";
                }
            }
        }
        String sql2 = sql + " where 1=1 AND c.isDelete='F'  AND s.isDelete='F' " + userStr + "";
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql2 = sql2 + "  and s.schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql2 = sql2 + " and s.gradeNum={gradeNum}  ";
        }
        if (null != studentType && !studentType.equals("-1") && !studentType.equals("")) {
            sql2 = sql2 + " and c.studentType={studentType}  ";
        }
        if (null != jie && !jie.equals("-1") && !jie.equals("") && !jie.equals("null")) {
            sql2 = sql2 + "and s.jie={jie}  ";
        }
        if (null != sex && !sex.equals("-1") && !sex.equals("") && !sex.equals("null")) {
            sql2 = sql2 + "and s.sex={sex} ";
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_studentType, (Object) studentType).put("jie", (Object) jie).put("sex", (Object) sex);
        return Integer.valueOf(this.dao2._queryObject(sql2, args).toString());
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List getgradeListByschool(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList("SELECT gradeNum,gradeName FROM grade WHERE schoolNum={schoolNum} AND isDelete='F'", Grade.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Teacher> getteacherList(String schoolNum, String tName) {
        String tNameStr = "";
        if (null != tName && !tName.equals("null") && !tName.equals("") && !tName.equals("请选择") && tName.indexOf("--") == -1) {
            tNameStr = " AND t.teacherName LIKE {teacherName} OR t.teacherNum LIKE {teacherName}";
        }
        String sql = "SELECT u.id id ,t.teacherNum,t.teacherName  FROM teacher t LEFT JOIN `user` u ON t.id=u.userid WHERE t.schoolNum={schoolNum} AND u.usertype='1' " + tNameStr;
        Map args = StreamMap.create().put("teacherName", (Object) ("%" + tName + "%")).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList(sql, Teacher.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void addGradeLeader(String schoolNum, String gradeNum, String userNum, String user, String date, String type) {
        int jie = getjie(gradeNum, schoolNum);
        Map args_sql1 = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        this.dao2._execute("DELETE FROM userposition WHERE type='3' AND schoolnum={schoolNum} AND gradeNum={gradeNum}  AND subjectNum ='999' ", args_sql1);
        String gradeName = gradeNameByNum(gradeNum, schoolNum);
        String stage = getStage(gradeNum);
        Map args_sql2 = StreamMap.create().put("userNum", (Object) userNum).put("date", (Object) date).put("description", (Object) (gradeName + "年级主任")).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("user", (Object) user).put("jie", (Object) Integer.valueOf(jie)).put("stage", (Object) stage);
        this.dao2._execute("INSERT INTO userposition(userNum,type,insertDate,description,schoolnum,gradeNum,insertUser,jie,stage,subjectNum)VALUES({userNum},'3',{date},{description},{schoolNum},{gradeNum},{user},{jie},{stage},'999')", args_sql2);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer isExistGradeLeader(String schoolNum, String gradeNum, String userNum) {
        getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("userNum", (Object) userNum);
        return Integer.valueOf(this.dao2._queryObject("SELECT count(1) FROM userposition WHERE type='3' AND schoolnum={schoolNum} AND gradeNum={gradeNum}  AND subjectNum='999' AND userNum!={userNum}", args).toString());
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getclassNameByNum(String schoolNum, String gradeNum, String classNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum);
        return this.dao2._queryObject("SELECT className FROM class WHERE id={classNum} AND isDelete='F'", args).toString();
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer isExistClassLeader(String schoolNum, String gradeNum, String userNum, String classNum) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) Integer.valueOf(jie)).put("userNum", (Object) userNum);
        return Integer.valueOf(this.dao2._queryObject("SELECT count(1) FROM userposition WHERE type='2' AND schoolNum={schoolNum} AND gradeNum={gradeNum} and classNum={classNum} AND subjectNum ='999' AND jie={jie} AND userNum!={userNum}", args).toString());
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void addClassLeader(String schoolNum, String gradeNum, String userNum, String user, String date, String type, String classNum) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) Integer.valueOf(jie)).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("userNum", (Object) userNum);
        this.dao2._execute("DELETE FROM userposition WHERE  schoolNum={schoolNum} AND gradeNum={gradeNum} AND type='2'   AND  subjectNum ='999' AND jie={jie} AND userNum={userNum}", args);
        this.dao2._execute("DELETE FROM userposition WHERE  schoolNum={schoolNum} AND gradeNum={gradeNum} AND type='2'  AND  subjectNum ='999' AND jie={jie} AND classNum={classNum}", args);
        String className = getclassNameByNum(schoolNum, gradeNum, classNum);
        String stage = getStage(gradeNum);
        Map args_sql2 = StreamMap.create().put("userNum", (Object) userNum).put("date", (Object) date).put("description", (Object) (className + "班主任")).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("user", (Object) user).put("jie", (Object) Integer.valueOf(jie)).put("stage", (Object) stage);
        this.dao2._execute("INSERT INTO userposition(userNum,type,insertDate,description,schoolNum,gradeNum,classNum,insertUser,jie,stage,subjectNum)VALUES({userNum},'2',{date},{description},{schoolNum},{gradeNum},{classNum},{user},{jie},{stage},'999')", args_sql2);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer isOtherTeacher(String schoolNum, String gradeNum, String classNum, String userNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("userNum", (Object) userNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        int i = Integer.valueOf(this.dao2._queryObject("SELECT count(1) FROM userposition WHERE type='2' and schoolNum={schoolNum}  and  subjectNum ='999'  AND usernum={userNum} AND classNum!={classNum} ", args).toString()).intValue();
        int i0 = Integer.valueOf(this.dao2._queryObject("SELECT count(1) FROM userposition WHERE type='2' and schoolNum={schoolNum}  and  subjectNum ='999'   AND  usernum={userNum} AND   gradeNum!={gradeNum}", args).toString()).intValue();
        return Integer.valueOf(i + i0);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Teacher getTname(String schoolNum, String tNum, String gradeNum, String classNum) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) Integer.valueOf(jie));
        return (Teacher) this.dao2._queryBean("SELECT t.teacherNum,t.teacherNum,t.teacherName FROM teacher t    LEFT JOIN user us ON us.userid = t.id AND usertype='1' LEFT JOIN userposition u ON us.id = u.userNum  AND u.type='2'   WHERE u.schoolNum={schoolNum} AND u.gradeNum={gradeNum} AND u.classNum={classNum} AND u.type='2' AND u.jie={jie}", Teacher.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getClassNamebyTNum(String schoolNum, String gradeNum, String tNum) {
        Map args = StreamMap.create().put("tNum", (Object) tNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return String.valueOf(this.dao2._queryObject("SELECT c.className FROM userposition u LEFT JOIN class c ON  u.classNum = c.id WHERE u.userNum={tNum} AND u.schoolNum={schoolNum}  AND u.subjectNum ='999' AND u.type='2'", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Subject> getsubList() {
        return this.dao2._queryBeanList("SELECT subjectNum,subjectName,xuankaoqufen FROM `subject` WHERE isHidden='F' ORDER BY orderNum", Subject.class, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void addsubjectTeacher(String schoolNum, String gradeNum, String tNum, String subjectNum, String classNum, String date, String user) {
        String className = getclassNameByNum(schoolNum, gradeNum, classNum);
        String subjectName = getsubjectName(subjectNum);
        int jie = getjie(gradeNum, schoolNum);
        String stage = getStage(gradeNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) Integer.valueOf(jie)).put("tNum", (Object) tNum).put("date", (Object) date).put("description", (Object) (className + subjectName + "老师")).put("user", (Object) user).put("stage", (Object) stage);
        this.dao2._execute("DELETE FROM userposition WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND subjectNum={subjectNum} AND type='1' AND classNum={classNum} AND jie={jie} ", args);
        this.dao2._execute("INSERT INTO userposition(userNum,type,insertDate,description,schoolnum,gradenum,subjectNum,classNum,insertUser,jie,stage) VALUES({tNum},'1',{date},{description},{schoolNum},{gradeNum},{subjectNum},{classNum},{user},{jie},{stage}) ON DUPLICATE KEY UPDATE  userNum={tNum},type='1',schoolNum={schoolNum},gradenum={gradeNum},subjectNum={subjectNum},classnum={classNum},jie={jie},stage={stage}", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer isExistsubjectTeacher(String schoolNum, String gradeNum, String subjectNum, String tNum, String classNum) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("jie", (Object) Integer.valueOf(jie)).put("tNum", (Object) tNum);
        return Integer.valueOf(String.valueOf(this.dao2._queryObject("SELECT count(1) FROM userposition WHERE type='1' AND schoolNum={schoolNum} AND gradeNum={gradeNum} AND classNum={classNum} AND subjectNum={subjectNum} AND jie={jie} AND userNum!={tNum}", args)));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getsubjectName(String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return String.valueOf(this.dao2._queryObject("SELECT subjectName FROM `subject` WHERE subjectNum={subjectNum} AND isHidden='F'", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getTNameBysubNum(String schoolNum, String gradeNum, String subjectNum, String classNum, String tNum) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) Integer.valueOf(jie)).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("tNum", (Object) tNum);
        return String.valueOf(this.dao2._queryObject("SELECT t.teacherName FROM teacher t LEFT JOIN user u ON t.id=u.userid LEFT JOIN userposition us ON us.userNum=u.id WHERE us.schoolnum={schoolNum} AND us.gradeNum={gradeNum} AND us.classNum={classNum} AND us.jie={jie} AND us.subjectNum={subjectNum} AND us.type='1' AND us.userNum!={tNum}", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Teacher> getTnameBysubNumAndCNum(String schoolNum, String tNum, String gradeNum, String classNum) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) Integer.valueOf(jie));
        return this.dao2._queryBeanList("SELECT  t.id ,t.teacherName,t.teacherNum FROM userposition u LEFT JOIN user us ON us.id = u.userNum  LEFT JOIN teacher t ON us.userid=t.id  WHERE u.schoolnum={schoolNum} AND u.gradeNum={gradeNum} AND u.classNum={classNum} AND u.jie={jie} AND u.type='1' AND u.subjectNum='101' ", Teacher.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getTname1(String schoolNum, String tNum, String type) {
        String sql;
        if ("1".equals(type)) {
            sql = "SELECT realname FROM user WHERE id={tNum} and isDelete='F'";
        } else {
            sql = "SELECT realname FROM user WHERE username={tNum}  and schoolNum={schoolNum} and isDelete='F'";
        }
        Map args = StreamMap.create().put("tNum", (Object) tNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return String.valueOf(this.dao2._queryObject(sql, args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Teacher> gettNameBysubNumAndClassNum(String schoolNum, String tNum, String gradeNum, String classNum, String subjectNum) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) Integer.valueOf(jie)).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryBeanList("SELECT  t.teacherNum ,t.teacherName FROM userposition u LEFT JOIN user us ON us.id=u.userNum LEFT JOIN teacher t ON us.userid=t.id  WHERE u.schoolnum={schoolNum} AND u.gradeNum={gradeNum} AND u.classNum={classNum} AND u.jie={jie} AND u.subjectNum={subjectNum} AND u.type='1' ", Teacher.class, args);
    }

    public String getStage(String gradeNum) {
        String stage = "";
        if (gradeNum.equals("1") || gradeNum.equals("2") || gradeNum.equals("3") || gradeNum.equals("4") || gradeNum.equals("5") || gradeNum.equals("6")) {
            stage = "1";
        } else if (gradeNum.equals("7") || gradeNum.equals("8") || gradeNum.equals("9")) {
            stage = "2";
        } else if (gradeNum.equals("10") || gradeNum.equals("11") || gradeNum.equals("12")) {
            stage = "3";
        }
        return stage;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getGradeLeaderNum(String schoolNum, String gradeNum) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) Integer.valueOf(jie));
        return String.valueOf(this.dao2._queryObject("SELECT t.teacherNum FROM userposition u LEFT JOIN user us on us.id = u.userNum LEFT JOIN teacher t ON us.userid=t.id WHERE  u.schoolNum={schoolNum} AND u.type='3' AND u.gradeNum={gradeNum} AND u.jie={jie}", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer isExistStudyLeader(String schoolNum, String gradeNum, String userNum, String subjectNum) {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return Integer.valueOf(this.dao2._queryObject("SELECT count(1) FROM userposition WHERE userNum!={userNum} AND type='4' AND schoolnum={schoolNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum} ", args).toString());
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Userposition getSubNameByTNum(String schoolNum, String subjectNum, String userNum, String gradeNum) {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return (Userposition) this.dao2._queryBean("SELECT DISTINCT t.teacherName ext1 ,su.subjectName ext2 from  userposition u LEFT JOIN `subject` su ON u.subjectNum=su.subjectNum LEFT JOIN user us ON us.id = u.userNum LEFT JOIN teacher t ON u.schoolnum=t.schoolNum AND us.userid=t.id WHERE u.userNum!={userNum} AND u.schoolnum={schoolNum} AND u.type='4' AND u.gradeNum={gradeNum} AND u.subjectNum={subjectNum}", Userposition.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void addStudyLeader(String schoolNum, String gradeNum, String subjectNum, String userNum, String user, String date) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        this.dao2._execute("DELETE FROM userposition WHERE userNum!={userNum} AND type='4' AND schoolNum={schoolNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum}", args);
        this.dao2._execute("DELETE FROM userposition WHERE  type='4' AND schoolNum={schoolNum} AND subjectNum={subjectNum}  and gradeNum={gradeNum}", args);
        String stage = getStage(gradeNum);
        String subjectName = getsubjectName(subjectNum);
        String gradeName = String.valueOf(this.dao2._queryObject("SELECT gradeName FROM grade WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND isDelete='F'", args));
        args.put("date", date);
        args.put("description", gradeName + "的" + subjectName + "教研主任");
        args.put("stage", stage);
        args.put("user", user);
        args.put("jie", Integer.valueOf(jie));
        this.dao2._execute("INSERT INTO userposition(userNum,type,insertDate,description,schoolNum,gradeNum,subjectNum,stage,insertUser,jie) VALUES({userNum},'4',{date},{description},{schoolNum},{gradeNum},{subjectNum},{stage},{user},{jie})", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Teacher getStudyLeaderInfo(String schoolNum, String gradeNum) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) Integer.valueOf(jie));
        return (Teacher) this.dao2._queryBean("SELECT DISTINCT t.teacherNum,t.teacherName,us.id ext1 from   teacher t LEFT JOIN user us ON us.userid = t.id LEFT JOIN userposition u ON  u.userNum=us.id  WHERE  u.schoolnum={schoolNum} AND u.type='4' AND u.gradeNum={gradeNum} AND u.jie={jie} AND u.subjectNum='101'", Teacher.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Teacher getStudyLeaderBySubNumChange(String schoolNum, String gradeNum, String subjectNum) {
        int jie = getjie(gradeNum, schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("jie", (Object) Integer.valueOf(jie));
        return (Teacher) this.dao2._queryBean("SELECT  t.teacherNum,t.teacherName  from   teacher t LEFT JOIN user us ON us.userid = t.id LEFT JOIN userposition u ON u.userNum=us.id  WHERE  u.schoolnum={schoolNum} AND u.gradeNum={gradeNum}  AND u.type='4' AND u.subjectNum={subjectNum} AND u.jie={jie}", Teacher.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getStuType() {
        return this.dao2._queryOrderMap("SELECT VALUE,NAME FROM `data` WHERE type='22'", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getStuNoteType() {
        return this.dao2._queryOrderMap("SELECT VALUE,NAME FROM `data` WHERE type='28'", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getstudentTypeMap() {
        return this.dao2._queryOrderMap("SELECT VALUE,NAME FROM `data` WHERE type='25' ORDER BY VALUE*1", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getStuTypeName(String value) {
        Map args = StreamMap.create().put("value", (Object) value);
        return (String) this.dao2._queryObject("SELECT NAME FROM `data` WHERE type='25'  AND VALUE={value}", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String checkSuperManager(String userId) {
        Map args = StreamMap.create().put("userId", (Object) userId);
        return this.dao2._queryObject("select count(1) from userrole where userNum = {userId} and roleNum = -1 ", args).toString();
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<AjaxData> getClassList(String exam, String school, String subject, String grade, String cla, String type, String subjectType) {
        String exm = "";
        String sc = "";
        String sub = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exm = " AND examNum = {exam}";
        }
        if (null != school && !school.equals("") && !school.equals("-1")) {
            sc = " AND schoolNum={school}";
        }
        if (null != subject && !subject.equals("") && !subject.equals("-1")) {
            sub = " AND subjectNum = {subject}";
        }
        String sql = "SELECT  DISTINCT cl.classNum num,cl.className name FROM (SELECT  ce.examPaperNum,ce.classNum,ce.schoolNum,ep.jie,ep.gradeNum FROM (SELECT * FROM exampaper WHERE 1=1 " + exm + sub + " AND gradeNum='" + grade + "') ep LEFT JOIN ( SELECT * FROM classexam WHERE 1=1 " + sc + " ) ce ON ep.examPaperNum=ce.examPaperNum ) rs LEFT JOIN class cl ON rs.jie=cl.jie AND rs.schoolNum=cl.schoolNum AND rs.gradeNum=cl.gradeNum AND rs.classNum=cl.classNum AND cl.studentType={subjectType} WHERE cl.classNum IS NOT NULL ORDER BY cl.classNum*1 ";
        Map args = StreamMap.create().put("exam", (Object) exam).put(License.SCHOOL, (Object) school).put("subject", (Object) subject).put("subjectType", (Object) subjectType);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Exampaper> getExamListStudent(String examNum, String jie) {
        String exm = "";
        if (null != examNum && !examNum.equals("") && !examNum.equals("-1")) {
            exm = " AND examNum = {examNum}";
        }
        String sql = "SELECT DISTINCT examNum,examPaperNum,jie,gradeNum,subjectNum FROM exampaper WHERE 1=1 " + exm + " AND jie={jie}";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("jie", (Object) jie);
        return this.dao2._queryBeanList(sql, Exampaper.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getExamNameOne(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryObject("SELECT examName from exam where examNum={examNum}", args).toString();
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<AjaxData> getStudentClassList(String exam, String school, String grade, String cla, String subject, String type, String studentType, String level) {
        String exm = "";
        String sc = "";
        String cl = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exm = " AND examNum = {exam}";
        }
        if (null != school && !school.equals("") && !school.equals("-1")) {
            sc = " AND schoolNum={school}";
        }
        if (null != cla && !cla.equals("") && !cla.equals("-1")) {
            cl = " AND id={cla}";
        }
        String sql = "SELECT DISTINCT c.classNum num,cla.className name  FROM ( SELECT  DISTINCT en.classNum,en.schoolNum  FROM (SELECT * FROM exampaper WHERE 1=1 " + exm + ") ex LEFT JOIN examinationnum en on en.subjectNum=ex.subjectNum and en.gradeNum=ex.gradeNum and en.examNum=ex.examNum LEFT JOIN (SELECT * FROM class WHERE 1=1 " + sc + cl + " AND gradeNum={grade} AND studentType={studentType}) cla ON cla.id=en.classNum   WHERE 1=1   AND ex.gradeNum={grade} AND cla.studentType={studentType} ) c LEFT JOIN (SELECT * FROM class WHERE 1=1 " + cl + " AND gradeNum={grade} AND studentType={studentType}) cla ON  cla.id=c.classNum WHERE 1=1  AND cla.id IS NOT NULL ORDER BY cla.classNum*1 ";
        Map args = StreamMap.create().put("exam", (Object) exam).put(License.SCHOOL, (Object) school).put("cla", (Object) cla).put("grade", (Object) grade).put(Const.EXPORTREPORT_studentType, (Object) studentType);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void addschool(String schoolNum, String areaNum, String schoolName, String shortname, String schoolAddress, String schoolType, String user, String data, String des, String xkwschoolId) {
        String areaNumStr = "";
        String areaNumStr1 = "";
        String areaNumStr2 = "";
        if (null != areaNum && !areaNum.equals("null") && !areaNum.equals("")) {
            areaNumStr = ",areaNum";
            areaNumStr1 = ",{areaNum}";
            areaNumStr2 = " areaNum={areaNum},";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("shortname", (Object) shortname);
        Object schoolId = this.dao2._queryObject("select id from school where schoolNum={schoolNum} ", args);
        if (null != schoolId) {
            args.put("schoolId", schoolId.toString());
            Object sItemId = this.dao2._queryObject("select id from statisticrelation where sonStatisticId={schoolId} and isLeaf='T' limit 1", args);
            if (null != sItemId) {
                this.dao2._execute("update statisticrelation set sonStatisticName={shortname} where sonStatisticId={schoolId} and isLeaf='T' ", args);
            }
            Object sItemId2 = this.dao2._queryObject("select id from statisticitem where sItemId={schoolId} and statisticItem='01' limit 1", args);
            if (null != sItemId2) {
                this.dao2._execute("update statisticitem set sItemName={shortname} where sItemId={schoolId} and statisticItem='01' ", args);
            }
        }
        String sql = "INSERT INTO school(schoolNum" + areaNumStr + ",schoolName,schoolAddress,shortname,schoolType,insertUser,insertDate,updateUser,updateDate,description,isDelete,schoolId)VALUES({schoolNum}" + areaNumStr1 + ",{schoolName},{schoolAddress},{shortname},{schoolType},{user},{data},{user},{data},{des},'F',{schoolId}) ON DUPLICATE KEY UPDATE " + areaNumStr2 + "schoolName={schoolName},schoolid={schoolId},shortname={shortname}, schoolAddress={schoolAddress} ,schoolType={schoolType},description={des},isDelete='F'";
        Map args_sql = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("schoolName", (Object) schoolName).put("shortname", (Object) shortname).put("schoolType", (Object) schoolType).put("user", (Object) user).put("data", (Object) data).put("schoolAddress", (Object) schoolAddress).put("areaNum", (Object) areaNum).put("schoolAddress", (Object) schoolAddress).put("des", (Object) des).put("schoolId", (Object) xkwschoolId);
        this.dao2._execute(sql, args_sql);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getStuSource() {
        return this.dao2._queryOrderMap("SELECT `value`,`name` FROM `data` WHERE type='26' AND isDelete='F' ORDER BY orderNum*1", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getclasId(String schoolNum, String gradeNum, String classNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
        String cid = String.valueOf(this.dao2._queryObject("SELECT id FROM class WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND classNum={classNum} AND jie={jie} AND isDelete='F'", args));
        return cid;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getTid(String schoolNum, String Tnum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("Tnum", (Object) Tnum);
        return String.valueOf(this.dao2._queryObject("SELECT id FROM teacher WHERE schoolNum={schoolNum} AND teacherNum={Tnum}", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getschid(String school) {
        Map args = StreamMap.create().put(License.SCHOOL, (Object) school);
        return String.valueOf(this.dao2._queryObject("select id from school where schoolNum={school}", args));
    }

    public Integer getStuid(String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return Integer.valueOf(String.valueOf(this.dao2._queryObject("SELECT id FROM student WHERE studentId={studentId}", args)));
    }

    public int getjie(String gradeNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        int aa = Convert.toInt(this.dao2._queryObject("SELECT jie FROM grade WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND isDelete='F'", args), 0).intValue();
        return aa;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer checkjie(String schoolNum, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        return Integer.valueOf(String.valueOf(this.dao2._queryObject("SELECT count(jie) FROM grade WHERE schoolNum={schoolNum} AND gradeNum!={gradeNum} AND jie={jie}", args)));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<AjaxData> checkjie2(String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        return this.dao2._queryBeanList("select h.schoolName num,s.gradeName name,s.jie ext1 from school h  inner join  (  select g.gradeNum,g.gradeName,g.schoolNum,g.jie from grade g  inner join  (  select c.gradeNum from basegrade c  inner join   basegrade d on c.stage=d.stage  where d.gradeNum={gradeNum} and c.gradeNum!={gradeNum}  )r on r.gradeNum=g.gradeNum\twhere g.jie={jie} and g.isDelete='F'  )s on h.id=s.schoolNum", AjaxData.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<AjaxData> checkGradeJie(String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryBeanList("select h.schoolName num,g.gradeName name,g.jie ext1 ,g.isDelete ext2 from school h  inner join   grade g on h.id=g.schoolNum  where g.gradeNum={gradeNum}", AjaxData.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public int checkSchNum() {
        return Integer.valueOf(String.valueOf(this.dao2._queryObject("select count(1) from school WHERE isDelete='F'", null))).intValue();
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer beforedelcheck(String schoolNum, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        return Integer.valueOf(String.valueOf(this.dao2._queryObject("SELECT COUNT(s.id) FROM (SELECT id,gradeNum,schoolNum,studentId FROM score WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} UNION ALL SELECT id,gradeNum,schoolNum,studentId FROM objectivescore WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} )s  LEFT JOIN student st ON st.id=s.studentId  AND s.gradeNum=st.gradeNum  WHERE st.gradeNum={gradeNum} AND st.jie={jie} LIMIT 0,1", args)));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getLevelStudent(String schoolNum, String gradeNum, String classNum, String jie) {
        String sql = "SELECT id value,studentName name FROM student WHERE 1=1 ";
        if (null != schoolNum && !schoolNum.equals("-1") && !schoolNum.equals("")) {
            sql = sql + "AND schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("-1") && !gradeNum.equals("")) {
            sql = sql + "AND gradeNum={gradeNum} ";
        }
        if (null != classNum && !classNum.equals("-1") && !classNum.equals("")) {
            sql = sql + "AND classNum={classNum} ";
        }
        String sql2 = sql + "AND  jie={jie}";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie);
        return this.dao2._queryOrderMap(sql2, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer addStudentLevel(String subjectNum, String sid, String classNum, String schoolNum, String gradeNum, Map jie, String description, int count, String xuankaoNum) {
        if (count >= 1) {
            return 1;
        }
        Levelstudent s = new Levelstudent();
        s.setSubjectNum(subjectNum);
        s.setSid(sid);
        s.setClassNum(classNum);
        s.setSchoolNum(schoolNum);
        s.setGradeNum(gradeNum);
        s.setJie(String.valueOf(jie.get(gradeNum)));
        s.setDescription(description);
        s.setInsertDate(DateUtil.getCurrentTime());
        s.setXuankaoqufen(xuankaoNum);
        this.dao2.save(s);
        return Integer.valueOf(count);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer delStuAndUserById(String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        this.dao2._execute("delete from user where username={studentId}", args);
        return Integer.valueOf(this.dao2._execute("delete from student where studentId={studentId}", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer getStudentLevelCount(String subjectNum, String sid, String classNum, String schoolNum, String gradeNum, Map jie, String description) {
        String schStr = "";
        String grdStr = "";
        String claStr = "";
        if (null != schoolNum && !"-1".equals(schoolNum) && !"".equals(schoolNum)) {
            schStr = schStr + " AND schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !"-1".equals(gradeNum) && !"".equals(gradeNum)) {
            grdStr = grdStr + " AND gradeNum={gradeNum} AND jie={jie} ";
        }
        if (null != classNum && !"-1".equals(classNum) && !"".equals(classNum)) {
            claStr = claStr + " AND classNum={classNum} ";
        }
        String sql = !"".equals(sid) ? "SELECT COUNT(id) FROM levelstudent WHERE 1=1  and sid={sid} " : "SELECT COUNT(id) FROM levelstudent WHERE 1=1 " + schStr + grdStr + claStr;
        if (null != subjectNum && !subjectNum.equals("") && !subjectNum.equals("-1")) {
            sql = sql + " AND subjectNum={subjectNum} ";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", jie.get(gradeNum)).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("sid", (Object) sid).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return Integer.valueOf(this.dao2._queryObject(sql, args).toString());
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer getStudentLevelCount2(String subjectNum, String sid, String classNum, String schoolNum, String gradeNum, Map jie, String description, String userId, String leiceng) {
        String schStr = "";
        String grdStr = "";
        String claStr = "";
        new ArrayList();
        Map args2 = new HashMap();
        args2.put("userId", userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args2);
        if (!userId.equals("-1") && !userId.equals("-2") && null == map) {
            schStr = schStr + " left join   (  select DISTINCT a.schoolNum,a.userId  from (   select userNum userId,schoolNum from schoolscanpermission where userNum={userId}   union all  select  id userId,schoolNum from user where id={userId}  ) a  )s on st.schoolNum=s.schoolNum";
        }
        String schStr2 = schStr + " WHERE 1=1";
        if ((null != schoolNum && !"-1".equals(schoolNum) && !"".equals(schoolNum)) || (null != leiceng && !leiceng.equals(""))) {
            schStr2 = schStr2 + " AND st.schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !"-1".equals(gradeNum) && !"".equals(gradeNum)) {
            grdStr = grdStr + " AND st.gradeNum={gradeNum} AND st.jie={jie} ";
        }
        if (null != classNum && !"-1".equals(classNum) && !"".equals(classNum)) {
            claStr = claStr + " AND st.classNum={classNum} ";
        }
        String sql = !"".equals(sid) ? "SELECT COUNT(id) FROM levelstudent st  " + schStr2 + " and st.sid={sid} " : "SELECT COUNT(id) FROM levelstudent st  " + schStr2 + grdStr + claStr;
        if (null != subjectNum && !subjectNum.equals("") && !subjectNum.equals("-1")) {
            sql = sql + " AND st.subjectNum={subjectNum} ";
        }
        if (null != leiceng && !leiceng.equals("") && schoolNum.equals("-1")) {
            Map args3 = new HashMap();
            args3.put("leiceng", leiceng);
            List list = this.dao2._queryColList("SELECT DISTINCT sItemId from statisticitem_school WHERE topItemId={leiceng} AND statisticitem='01' ", args3);
            Integer count = 0;
            for (int i = 0; i < list.size(); i++) {
                String schoolNum2 = Convert.toStr(list.get(i));
                Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum2).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", jie.get(gradeNum)).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("sid", (Object) sid).put("userId", (Object) userId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
                Integer num = this.dao2._queryInt(sql, args);
                count = Integer.valueOf(count.intValue() + num.intValue());
            }
            return count;
        }
        Map args4 = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", jie.get(gradeNum)).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("sid", (Object) sid).put("userId", (Object) userId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return Integer.valueOf(this.dao2._queryObject(sql, args4).toString());
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void updateLevelStudent(String id, String schoolNum, String gradeNum, String subjectNum, String classNum, String description, String xuankaoNum, String user, String date) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("description", (Object) description).put("xuankaoNum", (Object) xuankaoNum).put("id", (Object) id);
        this.dao2._execute("UPDATE levelstudent SET subjectNum={subjectNum} , classNum={classNum},description={description},xuankaoqufen={xuankaoNum} WHERE id={id}", args);
        String.valueOf(this.dao2._queryObject("select sid from levelstudent where id={id} ", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void updateLevelClass(String id, String schoolNum, String gradeNum, String subjectNum, String classNum, String className) {
        Map args = StreamMap.create().put("className", (Object) className).put("id", (Object) id);
        this.dao2._execute("UPDATE levelclass SET className={className},classNum={className} WHERE id={id}", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer getDelStudentSelLevelStudent(String[] ids, int user, String date) {
        return 1;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getStuTypeByGradeNum(String schoolNum, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        return this.dao2._queryOrderMap("SELECT DISTINCT(d.value),d.name from  class c LEFT JOIN (SELECT value,name FROM data WHERE type='25')  d ON d.value= c.studentType WHERE c.schoolNum={schoolNum} AND c.gradeNum={gradeNum} AND c.jie={jie} ORDER BY d.value*1 ", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer getstumaxcount() {
        return Integer.valueOf(String.valueOf(this.dao2._queryObject("SELECT COUNT(1) FROM student where isDelete='F' and nodel=0", null)));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void updateid_record(int count) {
        Map args = StreamMap.create().put("count", (Object) Integer.valueOf(count));
        this.dao2._execute("UPDATE id_record SET  id_val={count} WHERE tableName='student'", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer checkStuScoreCount(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        Object count = this.dao2._queryObject("select count(1) from studentlevel where studentId={id}", args);
        String val = null == count ? "0" : count.toString();
        return Integer.valueOf(val);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public RspMsg checkStuEnInfo(String studentId) {
        StringBuffer enSql = new StringBuffer();
        enSql.append("select distinct examNum from examinationnum ");
        enSql.append("where studentId={studentId}");
        StringBuffer sql = new StringBuffer();
        sql.append("select en.examNum from (");
        sql.append(enSql);
        sql.append(") en ");
        sql.append("left join exam e on e.examNum=en.examNum ");
        sql.append("where e.isDelete='F' and e.`status`='0' ");
        sql.append("limit 1 ");
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        if (null == this.dao2._queryObject(sql.toString(), args)) {
            return new RspMsg(200, "此学生未参加考试", null);
        }
        return new RspMsg(401, "此学生正在参加考试，请先到考生考号列表里删除考号！", null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String IsExistlevelclass(String schoolNum, String gradeNum, String[] classNum, String subjectNum, String jie) {
        String flag = "";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum[0]).put("jie", (Object) jie).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        Object o = this.dao2._queryObject("select classNum from levelclass where schoolNum={schoolNum} and gradeNum={gradeNum} and classNum={classNum} AND isDelete='F' AND jie={jie} AND subjectNum={subjectNum} limit 0,1", args);
        if (null != o) {
            flag = String.valueOf(o);
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<Integer, String> getGrade(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryOrderMap("SELECT DISTINCT gradeNum,gradeName FROM grade WHERE isDelete='F'  union select DISTINCT g.gradeNum,g.gradeName from grade g LEFT JOIN exampaper exp ON exp.gradeNum = g.gradeNum AND exp.jie = g.jie where exp.examNum = {examNum} order by gradeNum desc", TypeEnum.IntegerString, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getStuTypeByGradeNum_new(String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        return this.dao2._queryOrderMap("SELECT DISTINCT(d.value),d.name from  class c LEFT JOIN (SELECT value,name FROM data WHERE type='25')  d ON d.value= c.studentType WHERE  c.gradeNum={gradeNum} AND c.jie={jie} ORDER BY d.value*1 ", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getSchoolMap_new(String gradeNum, String stuType) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryOrderMap("SELECT DISTINCT sc.id,sc.schoolName FROM school sc LEFT JOIN grade cl ON sc.id=cl.schoolNum WHERE cl.gradeNum={gradeNum} AND cl.isDelete='F'", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<School> getUserSchoolMap_new(String gradeNum, String stuType, String examNum) {
        String sql;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("stuType", (Object) stuType);
        Map<String, Object> ismanageMap = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={stuType} and type=1 limit 1", args);
        if ("-1".equals(stuType) || "-2".equals(stuType) || null != ismanageMap) {
            sql = "SELECT DISTINCT s.id,s.schoolName,t.id ext1,t.testingCentreName ext2 FROM school s LEFT JOIN grade g ON g.schoolNum=s.id LEFT JOIN testingcentre_school ts on ts.examNum={examNum} and ts.schoolNum=s.id LEFT JOIN testingcentre t on t.id = ts.testingCentreId and t.examNum={examNum} where g.gradeNum={gradeNum} and s.isDelete='F' order by convert(s.schoolName using gbk)";
        } else {
            sql = "SELECT DISTINCT s.id,s.schoolName,t.id ext1,t.testingCentreName ext2 FROM school s LEFT JOIN grade g ON g.schoolNum=s.id LEFT JOIN schoolscanpermission sm on sm.schoolNum = s.id and sm.userNum={stuType} LEFT JOIN testingcentre_school ts on ts.examNum={examNum} and ts.schoolNum=s.id LEFT JOIN testingcentre t on t.id = ts.testingCentreId and t.examNum={examNum} where g.gradeNum={gradeNum} and s.isDelete='F' and sm.schoolNum is not null  order by convert(s.schoolName using gbk)";
        }
        return this.dao2._queryBeanList(sql, School.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer checkHighGrade(String schoolNum, String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        String res = Convert.toStr(this.dao2._queryObject("SELECT gradeNum FROM grade WHERE schoolNum={schoolNum} AND gradeNum>{gradeNum} AND isDelete='F' LIMIT 1", args));
        if (null != res && !"null".equals(res) && !"".equals(res)) {
            int g = Integer.valueOf(res).intValue();
            int g1 = g - Integer.valueOf(gradeNum).intValue();
            if (g1 == 1) {
                return 1;
            }
            return 0;
        }
        return 0;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer checkallHighGrade(String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        String res = Convert.toStr(this.dao2._queryObject("SELECT gradeNum FROM grade WHERE gradeNum>{gradeNum} AND isDelete='F' ORDER BY gradeNum*1 LIMIT 1", args));
        if (null != res && !"null".equals(res) && !"".equals(res)) {
            int g = Integer.valueOf(res).intValue();
            int g1 = g - Integer.valueOf(gradeNum).intValue();
            if (g1 == 1) {
                return 1;
            }
            return 0;
        }
        return 0;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Object> checkexamNum(String examNum, String Length, String idOrNum, String classString, String levelclass, String yinyongType) {
        String studentNum;
        List<Object> firstList = new ArrayList<>();
        if (idOrNum.equals("1")) {
            studentNum = "studentNum";
        } else {
            studentNum = Const.EXPORTREPORT_studentId;
        }
        Map args_sqlString = StreamMap.create().put("Length", (Object) Length).put("classString", (Object) classString);
        String sqlString = "select RIGHT(" + studentNum + ",{Length}) id,COUNT(RIGHT(" + studentNum + ",{Length})) num from student where classNum in ({classString[]})  and isDelete = 'F' and nodel = '0' \tGROUP BY RIGHT(" + studentNum + ",{Length}) ORDER BY num desc LIMIT 1";
        if (levelclass.equals("T")) {
            sqlString = "select RIGHT(stu." + studentNum + ",{Length}) id,COUNT(RIGHT(stu." + studentNum + ",{Length})) num from student stu RIGHT JOIN   ( select DISTINCT sid from levelstudent WHERE   classNum in ({classString[]}) ) lstu  ON lstu.sid = stu.id  where stu.isDelete = 'F' and stu.nodel = '0' \tGROUP BY RIGHT(stu." + studentNum + ",{Length}) ORDER BY num desc LIMIT 1";
        }
        Object[] first = this.dao2._queryArray(sqlString, args_sqlString);
        if (null != first) {
            int num = Integer.parseInt(first[1].toString());
            if (num > 1) {
                args_sqlString.put("pLen", first[0]);
                String sqlString2 = "select concat(" + studentNum + ",studentName) from student where RIGHT(" + studentNum + ",{Length})={pLen} and classNum in ({classString[]}) and isDelete = 'F' and nodel = '0'";
                firstList = this.dao2._queryColList(sqlString2, args_sqlString);
            }
        }
        return firstList;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Map<String, String> getXuanKaoStatus() {
        return this.dao2._queryOrderMap("SELECT value,name FROM data d WHERE d.type=32", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getXuankaoStatusBySubject(String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return String.valueOf(this.dao2._queryObject("select IFNULL(xuankaoqufen,1) count from subject where subjectNum={subjectNum} ", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Object bijiaoStudentId(String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryObject("select CONCAT('【',sl.schoolName,'---',s.studentName,'】')  from student s INNER JOIN school sl on s.schoolNum=sl.id where s.studentId={studentId} and s.isDelete='F' ", args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> getStudentByGradeAndClass(String school, String grade, String classNum, String jie) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT st.studentName,st.studentID,st.id FROM student st ");
        sql.append("LEFT JOIN school sl ON st.schoolNum = sl.id ");
        sql.append("LEFT JOIN grade g ON g.gradeNum = st.gradeNum AND st.schoolNum = g.schoolNum AND st.jie = g.jie ");
        sql.append("LEFT JOIN class c ON st.classNum = c.id ");
        sql.append("WHERE st.isDelete = 'F' AND sl.isDelete = 'F' AND g.isDelete = 'F' AND c.isDelete = 'F' ");
        if (school != null && !school.equals("") && !school.equals("null")) {
            sql.append(" AND sl.id={school}");
        }
        if (grade != null && !grade.equals("") && !grade.equals("null")) {
            sql.append(" AND g.gradeNum={grade} and g.jie={jie}");
        }
        if (classNum != null && !classNum.equals("") && !classNum.equals("null")) {
            sql.append(" AND c.id={classNum}");
        }
        Map args = StreamMap.create().put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("jie", (Object) jie).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        return this.dao2._queryMapList(sql.toString(), TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<User> getStudentListJiaZhang(Map<String, String> map, String userId) {
        String str;
        Map args_sql = new HashMap();
        args_sql.put("username", userId);
        Map<String, Object> ismanageMap = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={username} and type=1 limit 1", args_sql);
        StringBuffer sql = new StringBuffer();
        String graduate = map.get("graduate");
        if (graduate.equals("1")) {
            str = " and st.isDelete='T' ";
        } else {
            str = " and st.isDelete='F' ";
        }
        String sch = "";
        sql.append("SELECT st.id as id , sl.schoolName as schoolName,st.schoolNum as schoolNum ,g.gradeName as gradeName ,g.gradeNum ,c.className as className,st.studentName as stuname,st.studentID as ext1,if(u.realname is null,'',u.realname) realname,if(u.username is null,'',u.username) username,st.isDelete,st.nodel ext2 ,st.jie from student st  LEFT JOIN school sl ON st.schoolNum=sl.id ");
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2") && null == ismanageMap) {
            sql.append(" left join schoolscanpermission s on s.schoolNum = sl.id and s.userNum={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ");
            sch = " and (s.schoolNum is not null or t.schoolNum is not null) ";
        }
        sql.append(" LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.jie = g.jie AND st.schoolNum = g.schoolNum  LEFT JOIN class c ON st.classNum=c.id   LEFT JOIN userparent u ON st.id=u.userid  where 1=1 " + sch + " ");
        if (!"-1".equals(schoolNum)) {
            sql.append(" and st.schoolNum={schoolNum}");
        }
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        if (!"-1".equals(gradeNum)) {
            sql.append(" and st.gradeNum={gradeNum}");
        }
        String classNum = map.get(Const.EXPORTREPORT_classNum);
        if (!"-1".equals(classNum)) {
            sql.append(" and c.id={classNum}");
        }
        String studentName = map.get("studentName");
        if (null != studentName && !"".equals(studentName)) {
            sql.append(" and st.studentName like {studentName}");
        }
        String stuID = map.get("stuID");
        if (null != stuID && !"".equals(stuID)) {
            sql.append(" and (st.studentid={stuID} or st.studentNum={stuID})");
        }
        String userName = map.get("userName");
        if (null != userName && !"".equals(userName)) {
            sql.append(" and u.username={userName}");
        }
        String realName = map.get("realName");
        if (null != realName && !"".equals(realName)) {
            sql.append(" and u.realName like {realName}");
        }
        sql.append(str);
        String index = map.get("index");
        String pageSize = map.get("pageSize");
        sql.append(" order by st.schoolNum, st.gradeNum , st.classNum ");
        if (index != null && !index.equals("") && pageSize != null && !pageSize.equals("")) {
            sql.append(" LIMIT {index},{pageSize}");
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("studentName", (Object) ("%" + studentName + "%")).put("stuID", (Object) stuID).put("userName", (Object) userName).put("realName", (Object) ("%" + realName + "%")).put("index", (Object) index).put("pageSize", (Object) pageSize);
        List list = this.dao2._queryBeanList(sql.toString(), User.class, args);
        return list;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> getstudentInfoJiaZhang(String id, String userName) {
        String userNameStr = "";
        if (userName != null && !userName.equals("")) {
            userNameStr = " and u.username= {userName}";
        }
        String sql = "SELECT s.studentId ,s.studentName studentName,s.gradeNum gradeNum,c.id classNum,s.schoolNum schoolNum,s.id id,if(u.realname is null, '',u.realname) realname,if(u.username is null,'',u.username) username FROM student s LEFT JOIN class c ON c.id=s.classNum LEFT JOIN userparent u ON s.id=u.userid where s.id={id}" + userNameStr;
        Map args = StreamMap.create().put("userName", (Object) userName).put("id", (Object) id);
        return this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> ifStudentRelevance(String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryMapList("select count(mobile) c_stu from userparent where userid= {studentId}", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> ifStudentisExits(String studentId, String userName) {
        String studentIdStr = "";
        if (studentId != null && !studentId.equals("")) {
            studentIdStr = " AND userid= {studentId}";
        }
        String userNameStr = "";
        if (userName != null && !userName.equals("")) {
            userNameStr = " AND userName={userName}";
        }
        String sql = "select count(1) c_stu from userparent where 1=1 " + studentIdStr + userNameStr;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId).put("userName", (Object) userName);
        return this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String insertStudentJiaZhang(Map<String, Object> map) {
        String flag;
        String password = map.get("password") + "";
        if (password == null || "".equals(password) || "null".equals(password)) {
            password = "111111";
        }
        String finalPassword = password;
        Map args = StreamMap.create().put("id", map.get("id")).put(Const.EXPORTREPORT_schoolNum, map.get(Const.EXPORTREPORT_schoolNum)).put("userId", map.get("userId")).put("userName", map.get("userName")).put("password", (Object) finalPassword.toLowerCase()).put("realName", map.get("realName")).put("currentTime", map.get("currentTime"));
        try {
            this.dao2._execute(" insert into userparent (id,schoolnum,userid,username,password,usertype,realname,mobile,insertUser,insertDate,autoreg) value ({id},{schoolNum},{userId},{userName},md5({password}),'3',{realName},{userName},'1',{currentTime},'1')", args);
            flag = "T";
        } catch (Exception e) {
            e.printStackTrace();
            flag = "F";
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String updateStudentJiaZhang(Map<String, String> map) {
        String oldUserName = map.get("oldUserName");
        String userName = map.get("userName");
        String realName = map.get("realName");
        String password = map.get("password");
        String passwordStr = "";
        if (password != null && !"".equals(password)) {
            passwordStr = " ,password=md5({password})";
        }
        StringBuffer sql = new StringBuffer();
        sql.append("update userparent set ");
        if (userName != null && !userName.equals("")) {
            sql.append(" userName ={userName},mobile={userName}");
        }
        sql.append(" ,realName = {realName}");
        sql.append(passwordStr);
        sql.append(" where username={oldUserName}");
        Map args = StreamMap.create().put("password", (Object) password.toLowerCase()).put("userName", (Object) userName).put("realName", (Object) realName).put("oldUserName", (Object) oldUserName);
        String flag = "";
        try {
            int count = this.dao2._execute(sql.toString(), args);
            if (count > 0) {
                flag = "T";
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = "F";
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String deleteStudentJiaZhang(String[] userIds, String[] userNames) {
        String flag = "T";
        List<RowArg> list1 = new ArrayList<>();
        List<RowArg> list2 = new ArrayList<>();
        for (int i = 0; i < userIds.length; i++) {
            if (userNames[i] != null && !userNames[i].equals("")) {
                int finalI = i;
                Map args = StreamMap.create().put("userid", (Object) userIds[finalI]).put("username", (Object) userNames[finalI]);
                String id = getIdFromUserparent(userIds[i], userNames[i]);
                if (id != null && !id.equals("")) {
                    args.put("id", id);
                    list2.add(new RowArg("delete from userrole where userNum={id}", args));
                }
                list1.add(new RowArg("delete from userparent where userid={userid} and username={username}", args));
            }
        }
        try {
            this.dao2._batchExecute(list1);
            this.dao2._batchExecute(list2);
        } catch (Exception e) {
            e.printStackTrace();
            flag = "F";
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getStudentListJiaZhangCount(Map<String, String> map, String userId) {
        StringBuffer sql = new StringBuffer();
        String sch = "";
        Map args_sql = new HashMap();
        args_sql.put("username", userId);
        Map<String, Object> ismanageMap = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={username} and type=1 limit 1", args_sql);
        sql.append("SELECT st.id as id , sl.schoolName as schoolName,st.schoolNum as schoolNum ,g.gradeName as gradeName ,g.gradeNum ,c.className as className,st.studentName as stuname,st.studentID as ext1,if(u.realname is null,'',u.realname) realname,if(u.username is null,'',u.username) username,st.isDelete,st.nodel ext2 from student st  LEFT JOIN school sl ON st.schoolNum=sl.id ");
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2") && null == ismanageMap) {
            sql.append(" left join schoolscanpermission s on s.schoolNum = sl.id and s.userNum={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ");
            sch = " and (s.schoolNum is not null or t.schoolNum is not null) ";
        }
        sql.append(" LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.jie = g.jie AND st.schoolNum = g.schoolNum  LEFT JOIN class c ON st.classNum=c.id   LEFT JOIN userparent u ON st.id=u.userid  where 1=1 " + sch + " ");
        if (!"-1".equals(schoolNum)) {
            sql.append(" and st.schoolNum={schoolNum}");
        }
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        if (!"-1".equals(gradeNum)) {
            sql.append(" and st.gradeNum={gradeNum}");
        }
        String classNum = map.get(Const.EXPORTREPORT_classNum);
        if (!"-1".equals(classNum)) {
            sql.append(" and c.id={classNum}");
        }
        String studentName = map.get("studentName");
        if (null != studentName && !"".equals(studentName)) {
            sql.append(" and st.studentName like {studentName}");
        }
        String stuID = map.get("stuID");
        if (null != stuID && !"".equals(stuID)) {
            sql.append(" and (st.studentid={stuID} or st.studentNum={stuID})");
        }
        String userName = map.get("userName");
        if (null != userName && !"".equals(userName)) {
            sql.append(" and u.username={userName}");
        }
        String realName = map.get("realName");
        if (null != realName && !"".equals(realName)) {
            sql.append(" and u.realName like {realName}");
        }
        map.get("index");
        map.get("pageSize");
        sql.append(" order by st.schoolNum, st.gradeNum , st.classNum ");
        Map args = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("studentName", (Object) ("%" + studentName + "%")).put("stuID", (Object) stuID).put("userName", (Object) userName).put("realName", (Object) ("%" + realName + "%"));
        List list = this.dao2._queryBeanList(sql.toString(), User.class, args);
        return list.size() + "";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getIdFromUserparent(String userId, String userName) {
        Map args = StreamMap.create().put("userId", (Object) userId).put("userName", (Object) userName);
        List<Map<String, Object>> list = this.dao2._queryMapList("select id from userparent where userid = {userId} and username = {userName}", TypeEnum.StringObject, args);
        String id = "";
        if (list != null && list.size() > 0) {
            Map map = list.get(0);
            id = map.get("id") + "";
        }
        return id;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String insertUserrole(String tableName, String[] fields, Object[] values) {
        String flag;
        try {
            this.dao2.insert(tableName, fields, values);
            flag = "T";
        } catch (Exception e) {
            e.printStackTrace();
            flag = "F";
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public int selectUseridsByUsername(String userName, String gradeNum) {
        String graSql = StrUtil.isEmpty(gradeNum) ? "" : " and s.gradeNum={gradeNum} ";
        String sql = "select count(1) from userparent up left join student s on s.id=up.userid where up.username={userName} and s.isDelete='F' and nodel='0' " + graSql;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("userName", (Object) userName);
        return Integer.valueOf(this.dao2._queryObject(sql, args).toString()).intValue();
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List selectIdFromStudent(String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        List list = this.dao2._queryColList("select id from student where studentId={studentId}", args);
        return list;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> exportStudentListJiaZhang(Map<String, String> map, String userId) {
        Map args2 = new HashMap();
        args2.put("userId", userId);
        StringBuffer sql = new StringBuffer();
        String sch = "";
        sql.append("SELECT st.id as id , sl.schoolName as schoolName,st.schoolNum as schoolNum ,g.gradeName as gradeName ,g.gradeNum ,c.className as className,st.studentName,st.studentID ,if(u.realname is null,'',SUBSTRING_INDEX(u.realname, ',', 1)) realname1,if(u.username is null,'',SUBSTRING_INDEX(u.username, ',', 1)) username1,if(u.realname is null,'',IF(LOCATE(',',u.realname) = 0,'',SUBSTRING_INDEX(u.realname, ',', -1))) realname2,if(u.username is null,'',IF(LOCATE(',',u.username) = 0,'',SUBSTRING_INDEX(u.username, ',', -1))) username2 from student st  LEFT JOIN school sl ON st.schoolNum=sl.id ");
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        Map<String, Object> ismanageMap = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args2);
        if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2") && null == ismanageMap) {
            sql.append(" left join schoolscanpermission s on s.schoolNum = sl.id and s.userNum={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ");
            sch = "  and (s.schoolNum is not null or t.schoolNum is not null) ";
        }
        sql.append(" LEFT JOIN basegrade g on  g.gradeNum=st.gradeNum  LEFT JOIN class c ON st.classNum=c.id   LEFT JOIN (SELECT userid,GROUP_CONCAT(realname)realname,GROUP_CONCAT(username)username FROM userparent GROUP BY userid) u ON st.id=u.userid  where st.isDelete='F' and st.nodel='0' " + sch + " ");
        if (!"-1".equals(schoolNum)) {
            sql.append(" and st.schoolNum={schoolNum}");
        }
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        if (!"-1".equals(gradeNum)) {
            sql.append(" and st.gradeNum={gradeNum}");
        }
        String classNum = map.get(Const.EXPORTREPORT_classNum);
        if (!"-1".equals(classNum)) {
            sql.append(" and c.id={classNum}");
        }
        String studentName = map.get("studentName");
        if (null != studentName && !"".equals(studentName)) {
            sql.append(" and st.studentName like {studentName}");
        }
        String stuID = map.get("stuID");
        if (null != stuID && !"".equals(stuID)) {
            sql.append(" and (st.studentid={stuID} or st.studentNum={stuID})");
        }
        String userName = map.get("userName");
        if (null != userName && !"".equals(userName)) {
            sql.append(" and u.username={userName}");
        }
        String realName = map.get("realName");
        if (null != realName && !"".equals(realName)) {
            sql.append(" and u.realName like {realName}");
        }
        sql.append(" order by st.schoolNum, st.gradeNum , st.classNum ");
        Map args = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("studentName", (Object) ("%" + studentName + "%")).put("stuID", (Object) stuID).put("userName", (Object) userName).put("realName", (Object) ("%" + realName + "%"));
        List list = this.dao2._queryMapList(sql.toString(), TypeEnum.StringObject, args);
        return list;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String updateJiazhang(String userName, String realName, String password) {
        String flag = "F";
        if (userName != null && !userName.equals("")) {
            String passwordStr = "";
            if (password != null && !password.equals("")) {
                passwordStr = ",password=md5({password}) ";
            }
            String sql = "update userparent set realName={realName}" + passwordStr;
            String sql2 = sql + " where username={userName}";
            Map args = StreamMap.create().put("realName", (Object) realName).put("password", (Object) password.toLowerCase()).put("userName", (Object) userName);
            int i = this.dao2._execute(sql2, args);
            if (i > 0) {
                flag = "T";
            }
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String changeSchool_student(Map<String, String> map, String user) {
        String flag = "T";
        String studentId = map.get(Const.EXPORTREPORT_studentId) + "";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId).put("studentName", (Object) map.get("studentName")).put("source", (Object) map.get("source")).put("grade", (Object) map.get("grade")).put("class", (Object) map.get("class")).put(Const.EXPORTREPORT_schoolNum, (Object) map.get(Const.EXPORTREPORT_schoolNum)).put("studentNum", (Object) map.get("studentNum")).put("stuNoteType", (Object) map.get("stuNoteType")).put("studentCName", (Object) map.get("studentCName")).put("sex", (Object) map.get("sex")).put("description", (Object) map.get("description")).put("oldSchool", (Object) map.get("oldSchool")).put("user", (Object) user).put("yzexaminationnum", (Object) map.get("yzexaminationnum")).put("subjectCombineNum", (Object) map.get("subjectCombineNum")).put("xuejiSchool", (Object) map.get("xuejiSchool")).put("xuejiClass", (Object) map.get("xuejiClass")).put("homeAddress", (Object) map.get("homeAddress"));
        if (StrUtil.isBlank(map.get("yuzhiPassword"))) {
            args.put("yuzhiPassword", "");
        } else {
            args.put("yuzhiPassword", SecureUtil.md5(map.get("yuzhiPassword").toLowerCase()));
        }
        Object id = this.dao2._queryObject("select id from changeSchool_student where status='0' and studentId={studentId} order by insertDate desc limit 1", args);
        if (id != null && !id.equals("")) {
            return "该学生已申请转校，不可重复申请";
        }
        String currentTime = DateUtil.getCurrentTime();
        args.put("currentTime", currentTime);
        String sql = "insert into changeSchool_student (studentId,studentName,source,gradeNum,classNum,schoolNum,studentNum,type,note,oldname,sex,description,originalSchool,appealTeacher,status,insertUser,insertDate,yzexaminationnum,yuzhiPassword,subjectCombineNum,xuejiSchool,xuejiClass,homeAddress) values({studentId},{studentName},{source},{grade},{class},{schoolNum},{studentNum},3,{stuNoteType},{studentCName},{sex},{description},{oldSchool},{user},0,{user},{currentTime},{yzexaminationnum},{yuzhiPassword},{subjectCombineNum},{xuejiSchool},{xuejiClass},{homeAddress})";
        int i = this.dao2._execute(sql, args);
        if (i < 1) {
            flag = "F";
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String cancelLevelStudentInfo(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        this.dao2._execute("delete from levelStudent where sid={id}", args);
        return "T";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Object[] getChangeSchoolList(Map<String, String> map, String userId) {
        Map args1 = StreamMap.create().put("userNum", (Object) userId);
        Map<String, Object> map1 = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args1);
        String flag = map.get("flag");
        String sql = "";
        String sql_count = "";
        String statusStr = "";
        String status = map.get(Const.CORRECT_SCORECORRECT);
        if (status != null && !status.equals("") && !status.equals("-1")) {
            statusStr = " and cs.status ={status}";
        }
        String schoolNumStr = "";
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        if (schoolNum != null && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            schoolNumStr = " and cs.schoolNum={schoolNum}";
        }
        String powerStr = "";
        if (schoolNum.equals("-1") && !userId.equals("-1") && !userId.equals("-2") && null == map1) {
            powerStr = " and (t1.schoolNum is not null or t2.schoolNum is not null)";
        }
        String studentIdStr = "";
        String studentId = map.get(Const.EXPORTREPORT_studentId);
        if (studentId != null && !studentId.equals("")) {
            studentIdStr = " and cs.studentId={studentId}";
        }
        String studentNameStr = "";
        String studentName = map.get("studentName");
        if (studentName != null && !studentName.equals("")) {
            studentNameStr = " and cs.studentName like {studentName}";
        }
        String teacherId = map.get("teacherId");
        String teacherName = map.get("teacherName");
        if (flag != null && flag.equals("1")) {
            String sql2 = sql + "SELECT cs.id,cs.studentName,cs.studentId,bg.gradeName,cs.schoolNum,s1.schoolName applySchool,cs.originalSchool,s2.schoolName oldSchool,IFNULL(cs.description,'')description,IFNULL(u1.realname,'')applyTeacher, case when cs.`status` = '0' then '转校中' when cs.`status`='1' then '转校完' when cs.`status`='2' then '转校驳回' else '' end  status, IFNULL(case when cs.dealTeacher='-3' then '智能匹配' when  cs.dealTeacher='-2' then 'dmj' else u2.realname end,'') dealTeacher,  IFNULL(up.realname,'') realname,IFNULL(up1.realname,'') realname1,ifnull(cs.insertDate,'') insertDate,ifnull(cs.updateDate,'') updateDate FROM changeschool_student cs LEFT JOIN basegrade bg on cs.gradeNum=bg.gradeNum LEFT JOIN school s1 on cs.schoolNum = s1.id LEFT JOIN school s2 on cs.originalSchool = s2.id LEFT JOIN user u1 on cs.appealTeacher = u1.id LEFT JOIN user u2 on cs.dealTeacher = u2.id LEFT JOIN ( \tSELECT up.schoolnum,GROUP_CONCAT(u.realname,'--',u.mobile) realname from userposition up LEFT JOIN user u on up.userNum=u.id \twhere up.type=0 GROUP BY up.schoolNum )up on cs.schoolNum=up.schoolnum LEFT JOIN ( \tSELECT up.schoolnum,GROUP_CONCAT(u.realname,'--',u.mobile) realname from userposition up LEFT JOIN user u on up.userNum=u.id  \twhere up.type=0 GROUP BY up.schoolNum )up1 on cs.originalSchool=up1.schoolnum ";
            String sql_count2 = sql_count + "SELECT count(cs.id) c_count FROM changeschool_student cs ";
            if (schoolNum.equals("-1") && !userId.equals("-1") && !userId.equals("-2") && null == map1) {
                sql2 = sql2 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.schoolNum = t1.schoolNum LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t2 on cs.originalSchool = t2.schoolNum ";
                sql_count2 = sql_count2 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.schoolNum = t1.schoolNum LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t2 on cs.originalSchool = t2.schoolNum ";
            }
            sql = sql2 + "WHERE 1=1 " + studentIdStr + studentNameStr + statusStr + schoolNumStr + powerStr + " order by cs.`status`,cs.insertDate desc";
            sql_count = sql_count2 + "WHERE 1=1 " + studentIdStr + studentNameStr + statusStr + schoolNumStr + powerStr;
        } else if ("2".equals(flag)) {
            String sql3 = sql + "SELECT cs.id,cs.studentName,cs.studentId,bg.gradeName,cs.schoolNum,s1.schoolName applySchool,cs.originalSchool,s2.schoolName oldSchool,IFNULL(cs.description,'')description,IFNULL(u1.realname,'')applyTeacher, case when cs.`status` = '0' then '转校中' when cs.`status`='1' then '转校完' when cs.`status`='2' then '转校驳回' else '' end  status, IFNULL(case when cs.dealTeacher='-3' then '智能匹配' when  cs.dealTeacher='-2' then 'dmj' else u2.realname end,'') dealTeacher,  IFNULL(up.realname,'') realname,IFNULL(up1.realname,'') realname1,ifnull(cs.insertDate,'') insertDate,ifnull(cs.updateDate,'') updateDate FROM changeschool_student cs LEFT JOIN basegrade bg on cs.gradeNum=bg.gradeNum LEFT JOIN school s1 on cs.schoolNum = s1.id LEFT JOIN school s2 on cs.originalSchool = s2.id LEFT JOIN user u1 on cs.appealTeacher = u1.id LEFT JOIN user u2 on cs.dealTeacher = u2.id LEFT JOIN ( \tSELECT up.schoolnum,GROUP_CONCAT(u.realname,'--',u.mobile) realname from userposition up LEFT JOIN user u on up.userNum=u.id \twhere up.type=0 GROUP BY up.schoolNum )up on cs.schoolNum=up.schoolnum LEFT JOIN ( \tSELECT up.schoolnum,GROUP_CONCAT(u.realname,'--',u.mobile) realname from userposition up LEFT JOIN user u on up.userNum=u.id  \twhere up.type=0 GROUP BY up.schoolNum )up1 on cs.originalSchool=up1.schoolnum ";
            String sql_count3 = sql_count + "SELECT count(cs.id) c_count FROM changeschool_student cs ";
            if (schoolNum.equals("-1") && !userId.equals("-1") && !userId.equals("-2") && null == map1) {
                sql3 = sql3 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.schoolNum = t1.schoolNum LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t2 on cs.originalSchool = t2.schoolNum ";
                sql_count3 = sql_count3 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.schoolNum = t1.schoolNum LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t2 on cs.originalSchool = t2.schoolNum ";
            }
            sql = sql3 + "WHERE 1=1 " + studentIdStr + studentNameStr + statusStr + schoolNumStr + powerStr + " order by cs.`status`,cs.insertDate desc";
            sql_count = sql_count3 + "WHERE 1=1 " + studentIdStr + studentNameStr + statusStr + schoolNumStr + powerStr;
        } else if ("3".equals(flag)) {
            String teacherIdStr = "";
            if (teacherId != null && !teacherId.equals("")) {
                teacherIdStr = " and cs.teacherNum={teacherId}";
            }
            String teacherNameStr = "";
            if (teacherName != null && !teacherName.equals("")) {
                teacherNameStr = " and cs.teacherName like {teacherName}";
            }
            String sql4 = sql + "SELECT cs.id,cs.teacherName,cs.teacherNum,cs.schoolNum,s1.schoolName applySchool,cs.originalSchool,s2.schoolName oldSchool,IFNULL(u1.realname,'')applyTeacher, case when cs.`status` = '0' then '转校中' when cs.`status`='1' then '转校完' when cs.`status`='2' then '转校驳回' else '' end  status, IFNULL(case when cs.dealTeacher='-3' then '智能匹配' when  cs.dealTeacher='-2' then 'dmj' else u2.realname end,'') dealTeacher,  IFNULL(up.realname,'') realname,IFNULL(up1.realname,'') realname1,ifnull(cs.insertDate,'') insertDate,ifnull(cs.updateDate,'') updateDate FROM changeschool_teacher cs LEFT JOIN school s1 on cs.schoolNum = s1.id LEFT JOIN school s2 on cs.originalSchool = s2.id LEFT JOIN user u1 on cs.appealTeacher = u1.id LEFT JOIN user u2 on cs.dealTeacher = u2.id LEFT JOIN ( \tSELECT up.schoolnum,GROUP_CONCAT(u.realname,'--',u.mobile) realname from userposition up LEFT JOIN user u on up.userNum=u.id \twhere up.type=0 GROUP BY up.schoolNum )up on cs.schoolNum=up.schoolnum LEFT JOIN ( \tSELECT up.schoolnum,GROUP_CONCAT(u.realname,'--',u.mobile) realname from userposition up LEFT JOIN user u on up.userNum=u.id  \twhere up.type=0 GROUP BY up.schoolNum )up1 on cs.originalSchool=up1.schoolnum ";
            String sql_count4 = sql_count + "SELECT count(cs.id) c_count FROM changeschool_teacher cs ";
            if (schoolNum.equals("-1") && !userId.equals("-1") && !userId.equals("-2") && null == map1) {
                sql4 = sql4 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.schoolNum = t1.schoolNum LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t2 on cs.originalSchool = t2.schoolNum ";
                sql_count4 = sql_count4 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.schoolNum = t1.schoolNum LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t2 on cs.originalSchool = t2.schoolNum ";
            }
            sql = sql4 + "WHERE 1=1 " + teacherIdStr + teacherNameStr + statusStr + schoolNumStr + powerStr + " order by cs.`status`,cs.insertDate desc";
            sql_count = sql_count4 + "WHERE 1=1 " + teacherIdStr + teacherNameStr + statusStr + schoolNumStr + powerStr;
        } else if ("4".equals(flag)) {
            String teacherIdStr2 = "";
            if (teacherId != null && !teacherId.equals("")) {
                teacherIdStr2 = " and cs.teacherNum={teacherId}";
            }
            String teacherNameStr2 = "";
            if (teacherName != null && !teacherName.equals("")) {
                teacherNameStr2 = " and cs.teacherName like {teacherName}";
            }
            String sql5 = sql + "SELECT cs.id,cs.teacherName,cs.teacherNum,cs.schoolNum,s1.schoolName applySchool,cs.originalSchool,s2.schoolName oldSchool,IFNULL(u1.realname,'')applyTeacher, case when cs.`status` = '0' then '转校中' when cs.`status`='1' then '转校完' when cs.`status`='2' then '转校驳回' else '' end  status, IFNULL(case when cs.dealTeacher='-3' then '智能匹配' when  cs.dealTeacher='-2' then 'dmj' else u2.realname end,'') dealTeacher,  IFNULL(up.realname,'') realname,IFNULL(up1.realname,'') realname1,ifnull(cs.insertDate,'') insertDate,ifnull(cs.updateDate,'') updateDate FROM changeschool_teacher cs LEFT JOIN school s1 on cs.schoolNum = s1.id LEFT JOIN school s2 on cs.originalSchool = s2.id LEFT JOIN user u1 on cs.appealTeacher = u1.id LEFT JOIN user u2 on cs.dealTeacher = u2.id LEFT JOIN ( \tSELECT up.schoolnum,GROUP_CONCAT(u.realname,'--',u.mobile) realname from userposition up LEFT JOIN user u on up.userNum=u.id \twhere up.type=0 GROUP BY up.schoolNum )up on cs.schoolNum=up.schoolnum LEFT JOIN ( \tSELECT up.schoolnum,GROUP_CONCAT(u.realname,'--',u.mobile) realname from userposition up LEFT JOIN user u on up.userNum=u.id  \twhere up.type=0 GROUP BY up.schoolNum )up1 on cs.originalSchool=up1.schoolnum ";
            String sql_count5 = sql_count + "SELECT count(cs.id) c_count FROM changeschool_teacher cs ";
            if (schoolNum.equals("-1") && !userId.equals("-1") && !userId.equals("-2") && null == map1) {
                sql5 = sql5 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.schoolNum = t1.schoolNum LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t2 on cs.originalSchool = t2.schoolNum ";
                sql_count5 = sql_count5 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.schoolNum = t1.schoolNum LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t2 on cs.originalSchool = t2.schoolNum ";
            }
            sql = sql5 + "WHERE 1=1 " + teacherIdStr2 + teacherNameStr2 + statusStr + schoolNumStr + powerStr + " order by cs.`status`,cs.insertDate desc";
            sql_count = sql_count5 + "WHERE 1=1 " + teacherIdStr2 + teacherNameStr2 + statusStr + schoolNumStr + powerStr;
        }
        Map args = StreamMap.create().put(Const.CORRECT_SCORECORRECT, (Object) status).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("studentName", (Object) ("%" + studentName + "%")).put("userId", (Object) userId).put("teacherId", (Object) teacherId).put("teacherName", (Object) ("%" + teacherName + "%")).put("index", (Object) map.get("index")).put("pageSize", (Object) map.get("pageSize"));
        List<Map<String, Object>> list = this.dao2._queryMapList(sql + " limit {index},{pageSize}", TypeEnum.StringObject, args);
        String count = this.dao2._queryObject(sql_count, args) + "";
        return new Object[]{list, count};
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String passChangeSchoolApply(String num, String flag, Map jie, String user) throws Exception {
        String flagStr = "T";
        if (flag.equals("1")) {
            List<String> sqlList = new ArrayList<>();
            Map args_stuInfo_sql = StreamMap.create().put("num", (Object) num);
            Student student = (Student) this.dao2._queryBean("select studentId,studentName,source,gradeNum,classNum,schoolNum,studentNum,type,note,oldName,sex,description,yzexaminationnum,yuzhiPassword,subjectCombineNum,xuejiSchool,xuejiClass,homeAddress from changeSchool_student  where studentId={num} order by insertDate desc limit 1", Student.class, args_stuInfo_sql);
            String jieInfo = jie.get(student.getGradeNum() + "") + "";
            Map args = StreamMap.create().put("studentName", (Object) student.getStudentName()).put("studentNum", (Object) student.getStudentNum()).put("source", (Object) student.getSource()).put(Const.EXPORTREPORT_gradeNum, (Object) student.getGradeNum()).put(Const.EXPORTREPORT_classNum, (Object) student.getClassNum()).put(Const.EXPORTREPORT_schoolNum, (Object) student.getSchoolNum()).put("type", (Object) student.getType()).put("note", (Object) student.getNote()).put("jieInfo", (Object) jieInfo).put(Const.EXPORTREPORT_studentId, (Object) student.getStudentId()).put("user", (Object) user).put("updateDate", (Object) DateUtil.getCurrentTime()).put("num", (Object) num).put("sex", (Object) student.getSex()).put("yzexaminationnum", (Object) student.getYzexaminationnum()).put("yuzhiPassword", (Object) student.getYuzhiPassword()).put("subjectCombineNum", (Object) student.getSubjectCombineNum()).put("xuejiSchool", (Object) student.getXuejiSchool()).put("xuejiClass", (Object) student.getXuejiClass()).put("homeAddress", (Object) student.getHomeAddress()).put("description", (Object) student.getDescription());
            String stuStr = "";
            if (StrUtil.isNotBlank(student.getStudentNum())) {
                stuStr = stuStr + ",studentNum={studentNum}";
            }
            if (StrUtil.isNotBlank(student.getSex())) {
                stuStr = stuStr + ",sex={sex}";
            }
            if (StrUtil.isNotBlank(student.getYzexaminationnum())) {
                stuStr = stuStr + ",yzexaminationnum={yzexaminationnum}";
            }
            if (StrUtil.isNotBlank(student.getXuejiSchool())) {
                stuStr = stuStr + ",xuejiSchool={xuejiSchool}";
            }
            if (StrUtil.isNotBlank(student.getXuejiClass())) {
                stuStr = stuStr + ",xuejiClass={xuejiClass}";
            }
            if (StrUtil.isNotBlank(student.getHomeAddress())) {
                stuStr = stuStr + ",homeAddress={homeAddress}";
            }
            if (StrUtil.isNotBlank(student.getDescription())) {
                stuStr = stuStr + ",description={description}";
            }
            if (!"0".equals(student.getNote())) {
                stuStr = stuStr + ",note={note}";
            }
            String sql2 = "update student set studentName={studentName}, source={source},gradeNum={gradeNum},classNum={classNum}, schoolNum={schoolNum},isDelete='F',nodel=0,type={type},jie={jieInfo},subjectCombineNum={subjectCombineNum}" + stuStr + " where studentId={studentId}";
            sqlList.add(sql2);
            String id = this.dao2._queryObject("select id from student where studentId={studentId}", args) + "";
            args.put("id", id);
            String passwordStr_stu = StrUtil.isEmpty(student.getYuzhiPassword()) ? "" : ",password={yuzhiPassword} ";
            String sql3 = "update user set schoolNum={schoolNum},realName={studentName},isDelete='F'" + passwordStr_stu + " where userid={id}";
            sqlList.add(sql3);
            sqlList.add("delete from levelStudent where sid={id}");
            sqlList.add("update changeSchool_student set status='1',dealTeacher={user},updateUser={user},updateDate={updateDate} where studentId={num} and status='0'");
            int[] count = this.dao2._batchExecute(sqlList, args);
            boolean result = false;
            for (int h = 0; h < count.length; h++) {
                if (h != 2 && count[h] == 0) {
                    result = true;
                }
            }
            if (result) {
                flagStr = "F";
            }
        } else {
            List<String> sqlList2 = new ArrayList<>();
            Map args_teaInfo_sql = StreamMap.create().put("num", (Object) num);
            Teacher teacher = (Teacher) this.dao2._queryBean("select teacherNum,teacherName,birthday,worktime,title,schoolNum,mobile,email,sex from changeSchool_teacher where teacherNum={num}", Teacher.class, args_teaInfo_sql);
            String birthday = StrUtil.isNotEmpty(teacher.getBirthday()) ? teacher.getBirthday() : null;
            String worktime = StrUtil.isNotEmpty(teacher.getWorktime()) ? teacher.getWorktime() : null;
            Map args2 = StreamMap.create().put("teacherName", (Object) teacher.getTeacherName()).put("birthday", (Object) birthday).put("worktime", (Object) worktime).put(Const.EXPORTREPORT_schoolNum, (Object) teacher.getSchoolNum()).put("title", (Object) teacher.getTitle()).put("mobile", (Object) teacher.getMobile()).put("email", (Object) teacher.getEmail()).put("sex", (Object) teacher.getSex()).put("teacherNum", (Object) teacher.getTeacherNum()).put("user", (Object) user).put("updateDate", (Object) DateUtil.getCurrentTime());
            sqlList2.add("update teacher set teacherName={teacherName},birthday={birthday}, worktime={worktime}, schoolNum={schoolNum},title={title},mobile={mobile}, email={email},sex={sex} where teacherNum={teacherNum}");
            String id2 = this.dao2._queryObject("select u.id from user u left join teacher t on u.userid = t.id where t.teacherNum={teacherNum}", args2) + "";
            args2.put("id", id2);
            sqlList2.add("update user set schoolNum={schoolNum},realName={teacherName},mobile={mobile},email={email} where id={id}");
            sqlList2.add("delete from userposition where userNum={id}");
            sqlList2.add("update changeSchool_teacher set status='1',dealTeacher={user},updateUser={user},updateDate={updateDate} where teacherNum={teacherNum} and status='0'");
            int[] count2 = this.dao2._batchExecute(sqlList2, args2);
            boolean result2 = false;
            for (int h2 = 0; h2 < count2.length; h2++) {
                if (h2 != 2 && count2[h2] == 0) {
                    result2 = true;
                }
            }
            if (result2) {
                flagStr = "F";
            }
        }
        return flagStr;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String rejectChangeSchoolApply(String id, String flag, String user) {
        String tableName;
        String flagStr = "T";
        if (flag.equals("1")) {
            tableName = "changeSchool_student";
        } else {
            tableName = "changeSchool_teacher";
        }
        String sql = "update " + tableName + " set status='2',dealTeacher={user},updateUser={user},updateDate={updateDate} where id={id}";
        Map args = StreamMap.create().put("user", (Object) user).put("updateDate", (Object) DateUtil.getCurrentTime()).put("id", (Object) id);
        int i = this.dao2._execute(sql, args);
        if (i < 1) {
            flagStr = "F";
        }
        return flagStr;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> getApplySchoolInfo(String userId, String type) {
        Map args = StreamMap.create().put("userId", (Object) userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        String sql = "";
        if ("1".equals(type)) {
            String sql2 = "select cs.schoolNum,s.schoolName from changeSchool_student cs left join school s on cs.schoolNum = s.id ";
            if (!userId.equals("-1") && !userId.equals("-2") && null != map) {
                sql2 = sql2 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.schoolNum = t1.schoolNum WHERE t1.schoolNum is not null ";
            }
            sql = sql2 + "group by cs.schoolNum ";
        } else if ("2".equals(type)) {
            String sql3 = "select cs.originalSchool,s.schoolName from changeSchool_student cs left join school s on cs.originalSchool = s.id ";
            if (!userId.equals("-1") && !userId.equals("-2") && null != map) {
                sql3 = sql3 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.originalSchool = t1.schoolNum WHERE t1.schoolNum is not null ";
            }
            sql = sql3 + "group by cs.originalSchool ";
        } else if ("3".equals(type)) {
            String sql4 = "select cs.schoolNum,s.schoolName from changeSchool_teacher cs left join school s on cs.schoolNum = s.id ";
            if (!userId.equals("-1") && !userId.equals("-2") && null != map) {
                sql4 = sql4 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.schoolNum = t1.schoolNum WHERE t1.schoolNum is not null ";
            }
            sql = sql4 + "group by cs.schoolNum ";
        } else if ("4".equals(type)) {
            String sql5 = "select cs.originalSchool,s.schoolName from changeSchool_teacher cs left join school s on cs.originalSchool = s.id ";
            if (!userId.equals("-1") && !userId.equals("-2") && null != map) {
                sql5 = sql5 + "LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}) t1 on cs.originalSchool = t1.schoolNum WHERE t1.schoolNum is not null ";
            }
            sql = sql5 + "group by cs.originalSchool ";
        }
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
        return list;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<String> getPowerSchoolInfo(String userId) {
        String sql;
        Map args = StreamMap.create().put("userId", (Object) userId);
        Map<String, Object> map1 = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if (userId.equals("-1") || userId.equals("-2") || null != map1) {
            sql = "select id schoolNum from school group by id";
        } else {
            sql = "select schoolNum from schoolscanpermission where userNum={userId} UNION select schoolNum from user where id={userId}";
        }
        List<String> list1 = new ArrayList<>();
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
        for (Map<String, Object> map : list) {
            list1.add(map.get(Const.EXPORTREPORT_schoolNum) + "");
        }
        return list1;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String delChangeSchoolInfo(String id, String flag) {
        String tableName;
        String flagStr = "T";
        if (flag.equals("1")) {
            tableName = "changeSchool_student";
        } else {
            tableName = "changeSchool_teacher";
        }
        String sql = "delete from " + tableName + " where id={id}";
        Map args = StreamMap.create().put("id", (Object) id);
        int i = this.dao2._execute(sql, args);
        if (i < 1) {
            flagStr = "F";
        }
        return flagStr;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String batchDelChangeSchoolInfo(String[] ids, String flag) {
        String tableName;
        String flagStr = "T";
        if (flag.equals("1")) {
            tableName = "changeSchool_student";
        } else {
            tableName = "changeSchool_teacher";
        }
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "delete from " + tableName + " where id={id}";
        for (String id : ids) {
            Map args = StreamMap.create().put("id", (Object) id);
            list.add(args);
        }
        int[] i = this.dao2._batchExecute(sql, list);
        for (int j : i) {
            if (j < 1) {
                flagStr = "F";
            }
        }
        return flagStr;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getClassNumByClassId(String classId) {
        Map args = StreamMap.create().put("classId", (Object) classId);
        return this.dao2._queryObject("select classNum from class where id= {classId}", args) + "";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Object> getTeacherList(String school, String subject, String classNum, String gradeNum) {
        Map args = StreamMap.create().put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        Object list1 = this.dao2._queryMapList("SELECT u.id,t.teacherNum,t.teacherName FROM teacher t LEFT JOIN user u ON t.id = u.userid and u.usertype='1' where t.schoolNum={school}", TypeEnum.StringObject, args);
        Object list2 = this.dao2._queryMapList("select s.subjectNum,ifnull(u.userNum,'')userNum,ifnull(u.userName,'')userName from `subject` s  LEFT JOIN (select GROUP_CONCAT(up.userNum) userNum,GROUP_CONCAT(u.realName) userName ,up.subjectNum,up.classNum from userposition up LEFT join user u ON up.userNum=u.id where up.type=1 and up.classNum={classNum}  and up.schoolNum={school} and up.gradeNum={gradeNum} GROUP BY up.subjectNum ) u ON s.subjectNum = u.subjectNum where s.isDelete ='F' and s.isHidden='F' ", TypeEnum.StringObject, args);
        List<Object> list = new ArrayList<>();
        list.add(list1);
        list.add(list2);
        return list;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String setRkjs(String school, String classNum, String grade, String subject, String rkjs, String user, String jie) {
        Map args_deleteSql = StreamMap.create().put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("subject", (Object) subject).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        this.dao2._execute("delete from userposition where type='1' and schoolNum={school} and gradeNum={grade} and subjectNum={subject} and classNum= {classNum}", args_deleteSql);
        String flag = "F";
        if (rkjs != null && !rkjs.equals("")) {
            String[] rkjss = rkjs.split(Const.STRING_SEPERATOR);
            List<Map<String, Object>> sqls = new ArrayList<>();
            for (int i = 0; i < rkjss.length; i++) {
                int finalI = i;
                Map args = StreamMap.create().put("userNum", (Object) rkjss[finalI]).put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("subject", (Object) subject).put("jie", (Object) jie).put("user", (Object) user);
                sqls.add(args);
            }
            this.dao2._batchExecute("insert into userposition(userNum,type,schoolNum,gradeNum,classNum,subjectNum,jie,insertUser,insertDate,permission_class,permission_grade,permission_subject) value({userNum},'1',{school},{grade},{classNum},{subject},{jie},{user},now(),'0','0','0')", sqls);
            flag = "T";
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String deleteTeacherLeader(String schoolNum, String gradeNum, String classNum) {
        String flag = "F";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        try {
            this.dao2._execute("delete from userposition where type='2' and schoolNum={schoolNum} and gradeNum={gradeNum} and classNum= {classNum}", args);
            flag = "T";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> getsubRkjsList(String classNum, String gradeNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryMapList("select s.subjectNum,s.subjectName,ifnull(u.userNum,'')userNum,ifnull(u.userName,'')userName from `subject` s  LEFT JOIN (select GROUP_CONCAT(up.userNum) userNum,GROUP_CONCAT(u.realName) userName ,up.subjectNum,up.classNum from userposition up LEFT join user u ON up.userNum=u.id where up.type=1 and up.classNum={classNum}  and up.schoolNum={schoolNum} and up.gradeNum={gradeNum} GROUP BY up.subjectNum ) u ON s.subjectNum = u.subjectNum where s.isDelete ='F' and s.isHidden='F' ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> getRkjsClassInfo(String gradeNum, String schoolNum, String user) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryMapList("select id classNum,className from class where schoolNum={schoolNum} and gradeNum={gradeNum} and isDelete='F'", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String deleteRkjs(String school, String classNum, String grade, String subject) {
        String flag = "F";
        Map args = StreamMap.create().put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("subject", (Object) subject);
        try {
            this.dao2._execute("delete from userposition where schoolNum={school} and gradeNum={grade} and classNum={classNum} and subjectNum={subject} and type='1' ", args);
            flag = "T";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String batchSetRkjs(String school, String classNum, String grade, String rkjs, String user, String jie) {
        String[] rkjss = rkjs.split(";");
        List<Map<String, Object>> sqls = new ArrayList<>();
        for (String str : rkjss) {
            String[] params = str.split(":");
            if (params.length > 1) {
                String subject = params[0];
                String teacher = params[1];
                Map args_deleteSql = StreamMap.create().put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("subject", (Object) subject).put(Const.EXPORTREPORT_classNum, (Object) classNum);
                this.dao2._execute("delete from userposition where type='1' and schoolNum={school} and gradeNum={grade} and subjectNum={subject} and classNum= {classNum}", args_deleteSql);
                String[] teachers = teacher.split(Const.STRING_SEPERATOR);
                for (int j = 0; j < teachers.length; j++) {
                    int finalJ = j;
                    Map args = StreamMap.create().put("userNum", (Object) teachers[finalJ]).put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("subject", (Object) subject).put("jie", (Object) jie).put("user", (Object) user);
                    sqls.add(args);
                }
            }
        }
        this.dao2._batchExecute("insert into userposition(userNum,type,schoolNum,gradeNum,classNum,subjectNum,jie,insertUser,insertDate,permission_class,permission_grade,permission_subject) value({userNum},'1',{school},{grade},{classNum},{subject},{jie},{user},now(),'0','0','0')", sqls);
        return "T";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public boolean isSchoolManager(String userId) {
        Map args = StreamMap.create().put("userId", (Object) userId);
        Object _id = this.dao2._queryObject("select id from userposition where type = '0' and userNum = {userId} limit 1", args);
        if (null == _id) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Object[] getStuExcepSub(Map<String, String> map, Map jie_map, String userId) {
        String count;
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        map.get("leicengId");
        map.get("position");
        String jieInfo = "";
        String schNum = "-1".equals(schoolNum) ? "" : " and stu.schoolNum={schoolNum}";
        String graNum = "";
        if (!"-1".equals(map.get(Const.EXPORTREPORT_gradeNum))) {
            graNum = " and stu.gradeNum={gradeNum}";
            jieInfo = " and stu.jie={jie}";
        }
        String classNum = "";
        if (!"-1".equals(map.get(Const.EXPORTREPORT_classNum))) {
            classNum = " and stu.classNum={classNum}";
        }
        String subjectCombineNum = "";
        if (!"-1".equals(map.get("subjectCombineNum"))) {
            subjectCombineNum = " and stu.subjectCombineNum={subjectCombineNum}";
        }
        String sex = "";
        if (!"-1".equals(map.get("sex"))) {
            sex = " and stu.sex={sex}";
        }
        String studentName = "";
        if (null != map.get("studentName") && !"".equals(map.get("studentName"))) {
            studentName = " and stu.studentName like {studentName}";
        }
        String stuID = "";
        if (null != map.get("stuID") && !"".equals(map.get("stuID"))) {
            stuID = " and (stu.studentid={stuID} or stu.studentNum={stuID})";
        }
        String note = "";
        if (null != map.get("description") && !"".equals(map.get("description"))) {
            note = " and stu.description like {description}";
        }
        String sql1 = "" + schNum + graNum + jieInfo + classNum + subjectCombineNum + sex + studentName + stuID + note;
        String sql = "SELECT stu.id,sl.schoolName,g.gradeName,c.className,stu.studentName,stu.studentId,stu_b.subjectCombineName ext1,s1.subjectName ext2,s2.subjectName ext3,s3.subjectName ext4  from student stu   LEFT JOIN school sl ON stu.schoolNum=sl.id LEFT JOIN grade g on  g.gradeNum=stu.gradeNum and stu.schoolNum=g.schoolNum AND stu.jie = g.jie  LEFT JOIN class c ON stu.classNum=c.id    INNER JOIN user uu ON stu.id=uu.userid   and uu.userType=2  LEFT JOIN subjectcombine stu_b on stu.subjectCombineNum=stu_b.subjectCombineNum  inner JOIN(  \tSELECT ls.sid,GROUP_CONCAT(e.subjectName) subjectName from levelstudent ls  \tINNER JOIN student stu on ls.sid=stu.id  \tINNER JOIN `subject` e on ls.subjectNum=e.subjectNum   where 1=1 " + sql1 + " \tand e.xuankaoqufen is not NULL  GROUP BY ls.sid  )s1 on stu.id=s1.sid  LEFT JOIN(  \tSELECT ls.sid,GROUP_CONCAT(e.subjectName) subjectName from levelstudent ls  \tINNER JOIN student stu on ls.sid=stu.id  \tINNER JOIN `subject` e on ls.subjectNum=e.subjectNum and e.xuankaoqufen=2  \tLEFT JOIN(  \t\tSELECT s.id,s.studentName,sb.subjectCombineName,sd.subjectNum FROM student s  \t\tLEFT JOIN subjectcombine sb on s.subjectCombineNum=sb.subjectCombineNum  \t\tLEFT JOIN subjectcombinedetail sd on sb.subjectCombineNum=sd.subjectCombineNum   \t)s on ls.sid=s.id   and ls.subjectNum=s.subjectNum   where 1=1 " + sql1 + " \tand s.id is NULL GROUP BY ls.sid  )s2 on stu.id=s2.sid  LEFT JOIN(  \tSELECT ls.sid,GROUP_CONCAT(e.subjectName) subjectName from levelstudent ls  \tINNER JOIN student stu on ls.sid=stu.id  \t\tINNER JOIN `subject` e on ls.subjectNum=e.subjectNum  \t\tINNER JOIN(  \t\t\tSELECT s.id,sb.subjectCombineName,sub.subjectNum1 subjectNum FROM student s  \t\t\tLEFT JOIN subjectcombine sb on s.subjectCombineNum=sb.subjectCombineNum  \t\t\tLEFT JOIN subjectcombinedetail sd on sb.subjectCombineNum=sd.subjectCombineNum  \t\t\tLEFT JOIN (  \t\t\t\tSELECT s.subjectNum,s.subjectName,s1.subjectNum subjectNum1,s1.subjectName subjectName1 from (  \t\t\t\tSELECT * from `subject` where xuankaoqufen=2 or xuankaoqufen=3  \t\t\t\t)s LEFT JOIN (  \t\t\t\tSELECT * from `subject` where xuankaoqufen=2 or xuankaoqufen=3  \t\t\t\t)s1 on s.pid =s1.pid and s.xuankaoqufen<>s1.xuankaoqufen   \t\t\t)sub on sd.subjectNum=sub.subjectNum   \t\t)s on ls.sid=s.id  and ls.subjectNum=s.subjectNum   where 1=1 " + sql1 + " \tGROUP BY ls.sid  )s3 on stu.id=s3.sid  where stu.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' and stu.nodel=0 " + sql1;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) map.get(Const.EXPORTREPORT_gradeNum)).put("jie", jie_map.get(map.get(Const.EXPORTREPORT_gradeNum))).put(Const.EXPORTREPORT_classNum, (Object) map.get(Const.EXPORTREPORT_classNum)).put("subjectCombineNum", (Object) map.get("subjectCombineNum")).put("sex", (Object) map.get("sex")).put("studentName", (Object) ("%" + map.get("studentName") + "%")).put("stuID", (Object) map.get("stuID")).put("description", (Object) ("%" + map.get("description") + "%"));
        if ("0".equals(map.get("count"))) {
            count = this.dao2._queryBeanList(sql, Levelstudent.class, args).size() + "";
        } else {
            count = map.get("count");
        }
        Integer.valueOf(map.get("index")).intValue();
        Integer.valueOf(map.get("pageSize")).intValue();
        List list = this.dao2._queryBeanList(sql.toString(), Student.class, args, Integer.valueOf(map.get("index")).intValue(), Integer.valueOf(map.get("pageSize")).intValue());
        return new Object[]{count, list};
    }

    public List<User> getJiaZhang_statis_old(Map<String, String> map, String userId) {
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String schStr = "";
        String garStr = "";
        if (!"-1".equals(schoolNum) && null != schoolNum && !"".equals(schoolNum)) {
            schStr = schStr + " and s.schoolNum={schoolNum}";
        }
        if (!"-1".equals(gradeNum) && null != gradeNum && !"".equals(gradeNum)) {
            garStr = garStr + " and s.gradeNum={gradeNum}";
        }
        String sql = "SELECT * from(SELECT a.* from(SELECT s.*,u.realname,u.mobile,IFNULL(up.count,0) ext2 from( \tSELECT sl.id schoolNum,sl.schoolName,g.jie,g.gradeNum,g.gradeName,c.id classNum,c.className,count(1) ext1 from student s  \t\tINNER JOIN school sl on s.schoolNum=sl.id \t\tINNER JOIN grade g on s.schoolNum=g.schoolNum and s.gradeNum=g.gradeNum \t\tINNER JOIN class c on s.classNum=c.id where sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' and s.isDelete='F' and s.nodel=0 " + schStr + garStr + " GROUP BY s.schoolNum,s.gradeNum,s.classNum \t)s  LEFT JOIN userposition uo on s.schoolNum=uo.schoolNum and s.gradeNum=uo.gradeNum and s.jie=uo.jie and s.classNum=uo.classNum and uo.subjectNum=999 and uo.type=2 LEFT JOIN user u on uo.userNum=u.id LEFT JOIN( \t\tSELECT s.schoolNum,s.jie,s.gradeNum,s.classNum,count(1) count from( \t\t\tSELECT DISTINCT up.userid from userparent up \t\t\t)up INNER JOIN student s on up.userid=s.id where s.isDelete='F' and s.nodel=0 " + schStr + garStr + " GROUP BY s.schoolNum,s.gradeNum,s.classNum \t)up on s.schoolNum=up.schoolNum and s.gradeNum=up.gradeNum and s.jie=up.jie and s.classNum=up.classNum";
        String sql2 = ((sql + ")a union ") + "SELECT '999999' schoolNum,'合计' schoolName,'' jie,'' gradeNum,'' gradeName,'' classNum,'' className,SUM(s.ext1),'' realname,'' mobile,IFNULL(SUM(up.count),0) ext2 from( \tSELECT sl.id schoolNum,sl.schoolName,g.jie,g.gradeNum,g.gradeName,c.id classNum,c.className,count(1) ext1 from student s  \t\tINNER JOIN school sl on s.schoolNum=sl.id \t\tINNER JOIN grade g on s.schoolNum=g.schoolNum and s.gradeNum=g.gradeNum \t\tINNER JOIN class c on s.classNum=c.id where sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' and s.isDelete='F' and s.nodel=0 " + schStr + garStr + " GROUP BY s.schoolNum,s.gradeNum,s.classNum \t)s  LEFT JOIN userposition uo on s.schoolNum=uo.schoolNum and s.gradeNum=uo.gradeNum and s.jie=uo.jie and s.classNum=uo.classNum and uo.subjectNum=999 and uo.type=2 LEFT JOIN user u on uo.userNum=u.id LEFT JOIN( \t\tSELECT s.schoolNum,s.jie,s.gradeNum,s.classNum,count(1) count from( \t\t\tSELECT DISTINCT up.userid from userparent up \t\t\t)up INNER JOIN student s on up.userid=s.id where s.isDelete='F' and s.nodel=0 " + schStr + garStr + " GROUP BY s.schoolNum,s.gradeNum,s.classNum \t)up on s.schoolNum=up.schoolNum and s.gradeNum=up.gradeNum and s.jie=up.jie and s.classNum=up.classNum") + ")a ORDER BY a.schoolNum*1, a.gradeNum*0.1,a.className*1";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryBeanList(sql2.toString(), User.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<User> getJiaZhang_statis(Map<String, String> map, String userId) {
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("userNum", (Object) userId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        String schStr = "";
        String garStr = "";
        String manageStr = "";
        if (!"-1".equals(schoolNum) && null != schoolNum && !"".equals(schoolNum)) {
            schStr = schStr + " and s.schoolNum={schoolNum}";
        }
        if (!"-1".equals(gradeNum) && null != gradeNum && !"".equals(gradeNum)) {
            garStr = garStr + " and s.gradeNum={gradeNum}";
        }
        Map<String, Object> ismanage = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args);
        if (schoolNum.equals("-1") && !userId.equals("-1") && !userId.equals("-2") && null == ismanage) {
            manageStr = " inner join (select schoolNum from schoolscanpermission where userNum={userNum} union select schoolNum from user where id={userNum}) schm  on s.schoolNum= schm.schoolNum ";
        }
        String sql = "SELECT s.*,u.realname,u.mobile,IFNULL(up.count,0) ext2,IFNULL(tct.sy_count,0) sy_count,IFNULL(tct.jf_count,0) jf_count from( \tSELECT sl.id schoolNum,sl.schoolName,s.jie,g.gradeNum,g.gradeName,c.id classNum,c.className,IFNULL(count(1),0) ext1 from student s  \t\tLEFT JOIN school sl on s.schoolNum=sl.id " + manageStr + "\t\tLEFT JOIN basegrade g on s.gradeNum=g.gradeNum \t\tLEFT JOIN class c on s.classNum=c.id where s.isDelete='F' and s.nodel=0 " + schStr + garStr + " GROUP BY s.schoolNum,s.gradeNum,s.classNum \t)s  LEFT JOIN userposition uo on s.schoolNum=uo.schoolNum and s.gradeNum=uo.gradeNum and s.jie=uo.jie and s.classNum=uo.classNum and uo.subjectNum=999 and uo.type=2 LEFT JOIN user u on uo.userNum=u.id LEFT JOIN( \t\tSELECT s.schoolNum,s.jie,s.gradeNum,s.classNum,count(1) count from( \t\t\tSELECT DISTINCT up.userid from userparent up \t\t\t)up INNER JOIN student s on up.userid=s.id where s.isDelete='F' and s.nodel=0 " + schStr + garStr + " GROUP BY s.schoolNum,s.gradeNum,s.classNum \t)up on s.schoolNum=up.schoolNum and s.gradeNum=up.gradeNum and s.jie=up.jie and s.classNum=up.classNum LEFT JOIN( \t\tSELECT s.schoolNum,s.jie,s.gradeNum,s.classNum,sum(if(state=1,1,0)) sy_count,sum(if(state=0,1,0)) jf_count from taocan_time tct\t\tINNER JOIN student s on tct.user_id=s.studentId where s.isDelete='F' and s.nodel=0 " + schStr + garStr + " GROUP BY s.schoolNum,s.gradeNum,s.classNum \t)tct on s.schoolNum=tct.schoolNum and s.gradeNum=tct.gradeNum and s.jie=tct.jie and s.classNum=tct.classNum ORDER BY CONVERT(s.schoolName using gbk),s.gradeNum,CONVERT(s.className using gbk)";
        return this.dao2._queryBeanList(sql.toString(), User.class, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getJiaZhang_statisCount(Map<String, String> map, String userId) {
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String schStr = "";
        String garStr = "";
        if (!"-1".equals(schoolNum) && null != schoolNum && !"".equals(schoolNum)) {
            schStr = schStr + " and s.schoolNum={schoolNum}";
        }
        if (!"-1".equals(gradeNum) && null != gradeNum && !"".equals(gradeNum)) {
            garStr = garStr + " and s.gradeNum={gradeNum}";
        }
        String sql = "SELECT s.*,u.realname,u.mobile,IFNULL(up.count,0) ext2 from( \tSELECT sl.id schoolNum,sl.schoolName,g.jie,g.gradeNum,g.gradeName,c.id classNum,c.className,count(1) ext1 from student s  \t\tINNER JOIN school sl on s.schoolNum=sl.id \t\tINNER JOIN grade g on s.schoolNum=g.schoolNum and s.gradeNum=g.gradeNum \t\tINNER JOIN class c on s.classNum=c.id where sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' and s.isDelete='F' and s.nodel=0 " + schStr + garStr + " GROUP BY s.schoolNum,s.gradeNum,s.classNum \t)s  LEFT JOIN userposition uo on s.schoolNum=uo.schoolNum and s.gradeNum=uo.gradeNum and s.jie=uo.jie and s.classNum=uo.classNum and uo.subjectNum=999 and uo.type=2 LEFT JOIN user u on uo.userNum=u.id LEFT JOIN( \t\tSELECT s.schoolNum,s.jie,s.gradeNum,s.classNum,count(1) count from( \t\t\tSELECT DISTINCT up.userid from userparent up \t\t\t)up INNER JOIN student s on up.userid=s.id where s.isDelete='F' and s.nodel=0 " + schStr + garStr + " GROUP BY s.schoolNum,s.gradeNum,s.classNum \t)up on s.schoolNum=up.schoolNum and s.gradeNum=up.gradeNum and s.jie=up.jie and s.classNum=up.classNum";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return String.valueOf(this.dao2._queryBeanList(sql.toString(), User.class, args).size());
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Object[] getStudentDetail(Map<String, String> map, String userId) {
        String sql;
        String count;
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String classNum = map.get(Const.EXPORTREPORT_classNum);
        map.get("jie");
        String tdType = map.get("tdType");
        String schStr = "";
        String graStr = "";
        String claStr = "";
        if (!"-1".equals(schoolNum) && null != schoolNum && !"".equals(schoolNum) && !"999999".equals(schoolNum)) {
            schStr = schStr + " and s.schoolNum={schoolNum} ";
        }
        if (!"-1".equals(gradeNum) && null != gradeNum && !"".equals(gradeNum)) {
            graStr = graStr + " and s.gradeNum={gradeNum} ";
        }
        if (!"-1".equals(classNum) && null != classNum && !"".equals(classNum)) {
            claStr = claStr + " and s.classNum={classNum} ";
        }
        if ("3".equals(tdType)) {
            sql = "SELECT sl.schoolname,g.gradename,c.className,s.studentId userid,s.studentName stuname from student s  LEFT JOIN userparent up on up.userid=s.id  LEFT JOIN school sl on sl.id=s.schoolNum  LEFT JOIN basegrade g on g.gradeNum=s.gradeNum  LEFT JOIN class c on c.id=s.classNum  where s.isDelete='F' and s.nodel=0 " + schStr + graStr + claStr + " and up.id is null  ORDER BY CONVERT(sl.schoolName USING gbk),g.gradeNum,CONVERT(c.className USING gbk),CONVERT(s.studentName USING gbk)";
        } else if ("2".equals(tdType)) {
            sql = "SELECT sl.schoolname,g.gradename,c.className,s.studentId userid,s.studentName stuname,GROUP_CONCAT(if(up.realname is null || up.realname='','-',up.realname) order by up.id) realname ,GROUP_CONCAT(ifnull(up.username,'-') order by up.id) username from userparent up  LEFT JOIN student s on s.id=up.userid  LEFT JOIN taocan_time tct on tct.user_id=s.studentId  LEFT JOIN school sl on sl.id=s.schoolNum  LEFT JOIN basegrade g on g.gradeNum=s.gradeNum  LEFT JOIN class c on c.id=s.classNum  where s.isDelete='F' and s.nodel=0 " + schStr + graStr + claStr + " and tct.id is null  group by s.id  ORDER BY CONVERT(sl.schoolName USING gbk),g.gradeNum,CONVERT(c.className USING gbk),CONVERT(s.studentName USING gbk)";
        } else {
            sql = "SELECT sl.schoolname,g.gradename,c.className,s.studentId userid,s.studentName stuname ,GROUP_CONCAT(if(up.realname is null || up.realname='','-',up.realname) order by up.id) realname ,GROUP_CONCAT(ifnull(up.username,'-') order by up.id) username from taocan_time tct  LEFT JOIN student s on s.studentId=tct.user_id  LEFT JOIN userparent up on up.userid=s.id  LEFT JOIN school sl on sl.id=s.schoolNum  LEFT JOIN basegrade g on g.gradeNum=s.gradeNum  LEFT JOIN class c on c.id=s.classNum  where tct.state='" + tdType + "' and s.isDelete='F' and s.nodel=0 " + schStr + graStr + claStr + " group by s.id  ORDER BY CONVERT(sl.schoolName USING gbk),g.gradeNum,CONVERT(c.className USING gbk),CONVERT(s.studentName USING gbk)";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        if ("0".equals(map.get("count"))) {
            count = String.valueOf(this.dao2._queryBeanList(sql, User.class, args).size());
        } else {
            count = map.get("count");
        }
        return new Object[]{count, this.dao2._queryBeanList(sql.toString(), User.class, args)};
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String changeSchoolDirectly(Map<String, String> map, String userId) {
        String flag = "T";
        String studentId = map.get(Const.EXPORTREPORT_studentId) + "";
        String currentTime = DateUtil.getCurrentTime();
        List<String> sqlList = new ArrayList<>();
        Map args_sql1 = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        String password_stu = "";
        if (StrUtil.isNotBlank(map.get("yuzhiPassword"))) {
            password_stu = SecureUtil.md5(map.get("yuzhiPassword").toLowerCase());
        }
        String count1 = String.valueOf(this.dao2._queryObject("select ifnull(count(id),0) from changeSchool_student where  studentId={studentId} order by insertDate desc limit 1", args_sql1));
        if ("0".equals(count1)) {
            String sql = "insert into changeSchool_student (studentId,studentName,source,gradeNum,classNum,schoolNum,studentNum,type,note,oldname,sex,description,originalSchool,appealTeacher,status,insertUser,insertDate,dealTeacher,yzexaminationnum,yuzhiPassword,subjectCombineNum,xuejiSchool,xuejiClass,homeAddress) values({studentId},{studentName},{source},{grade},{class},{schoolNum},{studentNum},3,{stuNoteType},{studentCName},{sex},{description},{oldSchool},{userId},1,{userId},{currentTime},'-3',{yzexaminationnum},{yuzhiPassword},{subjectCombineNum},{xuejiSchool},{xuejiClass},{homeAddress})";
            this.dao2._execute(sql, StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) map.get(Const.EXPORTREPORT_studentId)).put("studentName", (Object) map.get("studentName")).put("source", (Object) map.get("source")).put("grade", (Object) map.get("grade")).put("class", (Object) map.get("class")).put(Const.EXPORTREPORT_schoolNum, (Object) map.get(Const.EXPORTREPORT_schoolNum)).put("studentNum", (Object) map.get("studentNum")).put("stuNoteType", (Object) map.get("stuNoteType")).put("studentCName", (Object) map.get("studentCName")).put("sex", (Object) map.get("sex")).put("description", (Object) map.get("description")).put("oldSchool", (Object) map.get("oldSchool")).put("userId", (Object) userId).put("currentTime", (Object) currentTime).put("yzexaminationnum", (Object) map.get("yzexaminationnum")).put("yuzhiPassword", (Object) password_stu).put("subjectCombineNum", (Object) map.get("subjectCombineNum")).put("xuejiSchool", (Object) map.get("xuejiSchool")).put("xuejiClass", (Object) map.get("xuejiClass")).put("homeAddress", (Object) map.get("homeAddress")));
        } else {
            this.dao2._execute("update changeSchool_student set schoolNum={schoolNum},gradeNum={grade},classNum={class},studentName={studentName},studentNum={studentNum},source={source},sex={sex},originalSchool={oldSchool},status='1',type='3',updateUser={userId},updateDate={currentTime},dealTeacher='-3',yzexaminationnum={yzexaminationnum},yuzhiPassword={yuzhiPassword},subjectCombineNum={subjectCombineNum},xuejiSchool={xuejiSchool},xuejiClass={xuejiClass},homeAddress={homeAddress} where studentId={studentId}", StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) map.get(Const.EXPORTREPORT_schoolNum)).put("grade", (Object) map.get("grade")).put("class", (Object) map.get("class")).put("studentName", (Object) map.get("studentName")).put("studentNum", (Object) map.get("studentNum")).put("source", (Object) map.get("source")).put("sex", (Object) map.get("sex")).put("oldSchool", (Object) map.get("oldSchool")).put("userId", (Object) userId).put("currentTime", (Object) currentTime).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("yzexaminationnum", (Object) map.get("yzexaminationnum")).put("yuzhiPassword", (Object) password_stu).put("subjectCombineNum", (Object) map.get("subjectCombineNum")).put("xuejiSchool", (Object) map.get("xuejiSchool")).put("xuejiClass", (Object) map.get("xuejiClass")).put("homeAddress", (Object) map.get("homeAddress")));
        }
        Map args_stuInfo_sql = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        Student student = (Student) this.dao2._queryBean("select studentId,studentName,source,gradeNum,classNum,schoolNum,studentNum,type,note,oldName,sex,description,yzexaminationnum,yuzhiPassword,subjectCombineNum,xuejiSchool,xuejiClass,homeAddress  from changeSchool_student  where studentId={studentId} order by insertDate desc limit 1", Student.class, args_stuInfo_sql);
        String jieInfo = map.get("jie");
        Map args = StreamMap.create().put("studentName", (Object) student.getStudentName()).put("studentNum", (Object) student.getStudentNum()).put("source", (Object) student.getSource()).put(Const.EXPORTREPORT_gradeNum, (Object) student.getGradeNum()).put(Const.EXPORTREPORT_classNum, (Object) student.getClassNum()).put(Const.EXPORTREPORT_schoolNum, (Object) student.getSchoolNum()).put("type", (Object) student.getType()).put("note", (Object) student.getNote()).put("jieInfo", (Object) jieInfo).put("oldName", (Object) student.getOldName()).put("sex", (Object) student.getSex()).put("description", (Object) student.getDescription()).put(Const.EXPORTREPORT_studentId, (Object) student.getStudentId()).put("yzexaminationnum", (Object) student.getYzexaminationnum()).put("subjectCombineNum", (Object) student.getSubjectCombineNum()).put("xuejiSchool", (Object) student.getXuejiSchool()).put("xuejiClass", (Object) student.getXuejiClass()).put("homeAddress", (Object) student.getHomeAddress()).put("yuzhiPassword", (Object) password_stu);
        if (StrUtil.isNotBlank(map.get("yuzhiPassword"))) {
        }
        sqlList.add("update student set studentName={studentName},isDelete='F',nodel=0,studentNum={studentNum}, source={source},gradeNum={gradeNum},classNum={classNum}, schoolNum={schoolNum},type={type},note={note},jie={jieInfo}, sex={sex},description={description}, source={source},yzexaminationnum={yzexaminationnum},subjectCombineNum={subjectCombineNum}, xuejiSchool={xuejiSchool},xuejiClass={xuejiClass},homeAddress={homeAddress} where studentId={studentId}");
        String id = this.dao2._queryObject("select id from student where studentId={studentId}", args) + "";
        args.put("id", id);
        String sql3 = "update user set schoolNum={schoolNum},realName={studentName},isDelete='F',username={studentId}" + password_stu + " where userid={id}";
        sqlList.add(sql3);
        sqlList.add("delete from levelstudent where sid={id}");
        int[] count = this.dao2._batchExecute(sqlList, args);
        for (int i : count) {
            if (i == 0) {
                flag = "F";
            }
        }
        return flag;
    }

    public void updateChangeStu(String id, String userId) {
        String currentTime = DateUtil.getCurrentTime();
        Map args_sql = StreamMap.create().put("id", (Object) id);
        Student stu = (Student) this.dao2._queryBean("select * from student where id={id}", Student.class, args_sql);
        Map args2_sql = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) stu.getStudentId());
        ChangeSchoolStudent changeStu = (ChangeSchoolStudent) this.dao2._queryBean("select * from changeSchool_student where  studentId={studentId} order by insertDate desc limit 1", ChangeSchoolStudent.class, args2_sql);
        if (changeStu != null) {
            Map args3_sql = StreamMap.create().put("userId", (Object) userId).put("currentTime", (Object) currentTime).put(Const.EXPORTREPORT_studentId, (Object) stu.getStudentId());
            this.dao2._execute("update changeSchool_student set type='3',status='1',updateUser={userId},updateDate={currentTime},dealTeacher='-3' where studentId={studentId}", args3_sql);
            List<RowArg> sqlList = new ArrayList<>();
            String jieInfo = String.valueOf(stu.getJie());
            Map args_sql2 = StreamMap.create().put("studentName", (Object) changeStu.getStudentName()).put("studentNum", (Object) changeStu.getStudentNum()).put("source", (Object) changeStu.getSource()).put(Const.EXPORTREPORT_gradeNum, (Object) changeStu.getGradeNum()).put(Const.EXPORTREPORT_classNum, (Object) changeStu.getClassNum()).put(Const.EXPORTREPORT_schoolNum, (Object) changeStu.getSchoolNum()).put("type", (Object) changeStu.getType()).put("note", (Object) changeStu.getNote()).put("jieInfo", (Object) jieInfo).put("sex", (Object) changeStu.getSex()).put(Const.EXPORTREPORT_studentId, (Object) stu.getStudentId());
            sqlList.add(new RowArg("update student set studentName={studentName},isDelete='F',nodel=0,studentNum={studentNum}, source={source},gradeNum={gradeNum},classNum={classNum}, schoolNum={schoolNum},type={type},note={note},jie={jieInfo}, sex={sex} where studentId={studentId}", args_sql2));
            String idd = String.valueOf(this.dao2._queryObject("select id from student where studentId={studentId}", args_sql2));
            args_sql2.put("idd", idd);
            sqlList.add(new RowArg("update user set schoolNum={schoolNum},realName={studentName},isDelete='F' where userid={idd}", args_sql2));
            args_sql2.put("id", id);
            sqlList.add(new RowArg("delete from levelStudent where sid={id}", args_sql2));
            this.dao2._batchExecute(sqlList);
        }
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void delStuByScore(String id, String userId) {
        List<RowArg> sqls = new ArrayList<>();
        List<Baseinfolog> logList = new ArrayList<>();
        Baseinfolog stuLogBaseinfolog = new Baseinfolog();
        String date = DateUtil.getCurrentTime();
        Student student = new Student();
        Map args_stuSql = StreamMap.create().put("id", (Object) id);
        Object o = this.dao2._queryBean("select classNum,gradeNum,schoolNum from student where id={id}", Student.class, args_stuSql);
        if (null != o) {
            student = (Student) o;
        }
        Map args_sql = StreamMap.create().put("id", (Object) id);
        String count = String.valueOf(this.dao2._queryObject("select count(1) from studentlevel where studentId={id}", args_sql));
        if (count != null && !"0".equals(count)) {
            Map args = StreamMap.create().put("userId", (Object) userId).put("date", (Object) date).put("id", (Object) id);
            sqls.add(new RowArg("update student set isDelete='T',nodel=1,updateUser={userId},updateDate={date} where id={id}", args));
            sqls.add(new RowArg("update user set isDelete='T' where userId={id} AND usertype='2' ", args));
            stuLogBaseinfolog.setOperate(Const.log_update_student);
        } else {
            Map args2 = StreamMap.create().put("id", (Object) id);
            sqls.add(new RowArg("DELETE from levelstudent WHERE sid={id} ", args2));
            sqls.add(new RowArg("DELETE  from student where id={id}", args2));
            sqls.add(new RowArg("DELETE FROM `user` WHERE userId={id} AND usertype='2'", args2));
            sqls.add(new RowArg("DELETE FROM userrole WHERE userNum={id} AND roleNum='2'", args2));
            String userParentId = String.valueOf(this.dao2._queryObject("SELECT id FROM userparent where userid={id}", args2));
            if (!"null".equals(userParentId)) {
                Map args22 = StreamMap.create().put("userParentId", (Object) userParentId);
                sqls.add(new RowArg("DELETE FROM userparent WHERE id={userParentId}", args22));
                sqls.add(new RowArg("DELETE FROM userrole WHERE userNum={userParentId} AND roleNum='-2' ", args22));
            }
            stuLogBaseinfolog.setOperate(Const.log_delete_student);
        }
        stuLogBaseinfolog.setClassNum(student.getClassNum());
        stuLogBaseinfolog.setGradeNum(student.getGradeNum());
        stuLogBaseinfolog.setSchoolNum(student.getSchoolNum());
        stuLogBaseinfolog.setInsertUser(userId);
        stuLogBaseinfolog.setInsertDate(date);
        stuLogBaseinfolog.setStudentId(id);
        logList.add(stuLogBaseinfolog);
        this.dao2.batchSave(logList);
        this.dao2._batchExecute(sqls);
        updateChangeStu(id, userId);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getGradeIslevel(String grade, String jie) {
        Map args = StreamMap.create().put("grade", (Object) grade).put("jie", (Object) jie);
        return String.valueOf(this.dao2._queryObject("select islevel from grade where gradeNum={grade} and isdelete='F' and jie={jie} limit 0,1", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public void updateShoufeiStudent(com.alibaba.fastjson.JSONArray data, String userId, String currentTime) {
        this.dao2._execute("delete from taocan_time", null);
        if (data.size() <= 0) {
            return;
        }
        List<Map<String, Object>> inParams = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject jSONObject = data.getJSONObject(i);
            Map args_insertSql = StreamMap.create().put("user_id", jSONObject.get("user_id")).put("end_time", jSONObject.get("end_time")).put("state", jSONObject.get("state")).put("insertUser", (Object) userId).put("insertDate", (Object) currentTime);
            inParams.add(args_insertSql);
        }
        this.dao2._batchUpdate("insert into taocan_time (user_id,end_time,state,insertUser,insertDate) VALUES ({user_id},{end_time},{state},{insertUser},{insertDate}) ", inParams, Const.height_500);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String getTheLatestTime() {
        Object res = this.dao2._queryObject("select insertDate from taocan_time limit 1", null);
        return null == res ? "" : res.toString();
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> getschools() {
        return this.dao2._queryMapList("select id,schoolNum,schoolName from school where isDelete='F'", null, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> getsubjectTypes() {
        return this.dao2._queryMapList("SELECT name,value from `data` WHERE type='25'", null, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> getfirstSubject() {
        return this.dao2._queryMapList("SELECT subjectCombineNum,subjectCombineName from subjectCombine WHERE isParent=1", null, null);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public String importgaokaoyifenyiduan(String fufen, String subjectType, String firstSubject, String year, List<List<Object>> list, String loginUser) {
        String firstSubject2 = firstSubject.equals("-1") ? "0" : firstSubject;
        delgaokaoyifenyiduan(fufen, subjectType, firstSubject2, year, "gaokaoyifenyiduan");
        int last = list.size() - 1;
        Convert.toInt(list.get(last).get(2));
        for (int i = 0; i < list.size(); i++) {
            String score = Convert.toStr(list.get(i).get(0));
            String num = Convert.toStr(list.get(i).get(1));
            String allnum = Convert.toStr(list.get(i).get(2));
            Integer Ranking = Integer.valueOf((Convert.toInt(allnum).intValue() - Convert.toInt(num).intValue()) + 1);
            Map args = new HashMap();
            args.put("fufen", fufen);
            args.put("year", year);
            args.put("subjectType", subjectType);
            args.put("firstSubject", firstSubject2);
            args.put("score", score);
            args.put("num", num);
            args.put("allnum", allnum);
            args.put("Ranking", Ranking);
            args.put("insertUser", loginUser);
            Integer.valueOf(this.dao2._execute("INSERT INTO gaokaoyifenyiduan (id,isfufen,`year`,studentType,firstSubject,score,num,allnum,ranking,insertUser,insertDate) VALUES  (UUID_SHORT(),{fufen},{year},{subjectType},{firstSubject},{score},{num},{allnum},{Ranking},{insertUser},NOW())", args));
        }
        return "导入成功";
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer setgaokaoshangxian(String fufen, String subjectType, String firstSubject, String year, String score, String type, String typename, String loginUser) {
        delgaokaoyifenyiduan(fufen, subjectType, firstSubject, year, "gaokaoshangxian");
        Map args = new HashMap();
        args.put("fufen", fufen);
        args.put("year", year);
        args.put("subjectType", subjectType);
        args.put("firstSubject", firstSubject);
        args.put("score", score);
        args.put("type", type);
        args.put("typeName", typename);
        args.put("insertUser", loginUser);
        return Integer.valueOf(this.dao2._execute("INSERT INTO `gaokaoshangxian`(`id`, `isfufen`, `year`, `studentType`, `firstSubject`, `score`, `type`, `typeName`, `insertUser`, `insertDate`) VALUES\n(UUID_SHORT(),{fufen},{year},{subjectType},{firstSubject}, {score}, {type}, {typeName}, {insertUser},NOW()) ", args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer isExsit(String fufen, String subjectType, String firstSubject, String year, String table) {
        String sql = "SELECT count(1) from " + table + " WHERE isfufen={fufen} and year={year} and studentType={subjectType} and  firstSubject={firstSubject}";
        Map args = new HashMap();
        args.put("fufen", fufen);
        args.put("year", year);
        args.put("subjectType", subjectType);
        args.put("firstSubject", firstSubject);
        return this.dao2._queryInt(sql, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer delgaokaoyifenyiduan(String fufen, String subjectType, String firstSubject, String year, String table) {
        String sql = "delete from " + table + " WHERE isfufen={fufen} and year={year} and studentType={subjectType} and  firstSubject={firstSubject}";
        Map args = new HashMap();
        args.put("fufen", fufen);
        args.put("year", year);
        args.put("subjectType", subjectType);
        args.put("firstSubject", firstSubject);
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> getyifenyiduanData(String fufen, String subjectType, String firstSubject, String year, String table) {
        String sql = "SELECT * from " + table + " WHERE isfufen={fufen} and studentType={subjectType} and  firstSubject={firstSubject} and year={year}";
        Map args = new HashMap();
        args.put("fufen", fufen);
        args.put("subjectType", subjectType);
        args.put("firstSubject", firstSubject);
        args.put("year", year);
        return this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer resetYuzhi(String schoolNum, String gradeNum, String classNum, String subCom, String sex, String stuId, String stuName, String beizhu) {
        String sql = "UPDATE student set yzexaminationnum= '' WHERE schoolNum={schoolNum} and isDelete='F' ";
        if (!gradeNum.equals("-1")) {
            sql = sql + " AND gradeNum={gradeNum} ";
        }
        if (!classNum.equals("-1")) {
            sql = sql + " AND classNum={classNum} ";
        }
        if (!subCom.equals("-1")) {
            sql = sql + " AND subjectCombineNum={subjectCombineNum} ";
        }
        if (!sex.equals("-1")) {
            sql = sql + " AND sex={sex} ";
        }
        if (!stuId.equals("")) {
            sql = sql + " AND studentId={studentId} ";
        }
        if (!stuName.equals("")) {
            sql = sql + " AND studentName LIKE {studentName} ";
        }
        if (!beizhu.equals("")) {
            sql = sql + " AND description LIKE {description} ";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("subjectCombineNum", subCom);
        args.put("sex", sex);
        args.put(Const.EXPORTREPORT_studentId, stuId);
        args.put("studentName", "%" + stuName + "%");
        args.put("description", "%" + beizhu + "%");
        Integer row = Integer.valueOf(this.dao2._execute(sql, args));
        return row;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public Integer resetYuzhi2(String[] temp) {
        for (int i = 1; i < temp.length; i++) {
            String stuId = temp[i];
            Map args = new HashMap();
            args.put("stuid", stuId);
            this.dao2._execute("UPDATE student set yzexaminationnum= '' WHERE id={stuid} and isDelete='F'", args);
        }
        return 1;
    }

    @Override // com.dmj.service.teachingInformation.StudentService
    public List<Map<String, Object>> getpiciData() {
        return this.dao2._queryMapList("SELECT name,`value` from `data` WHERE   type =21 ", null, null);
    }
}
