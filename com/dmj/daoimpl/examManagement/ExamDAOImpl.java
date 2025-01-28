package com.dmj.daoimpl.examManagement;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.dmj.action.base.HttpUrlImageUtils.ImageStreamUtil;
import com.dmj.auth.bean.License;
import com.dmj.auth.util.Util;
import com.dmj.cs.util.CsUtils;
import com.dmj.daoimpl.awardPoint.AwardPointDaoImpl;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.teacherApp.TeacherAppScoreDaoImpl;
import com.dmj.domain.AjaxData;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.Class;
import com.dmj.domain.Classexam;
import com.dmj.domain.ClipError;
import com.dmj.domain.CorrectInfo;
import com.dmj.domain.Define;
import com.dmj.domain.Exam;
import com.dmj.domain.ExamineeStuRecord;
import com.dmj.domain.Examlog;
import com.dmj.domain.Exampaper;
import com.dmj.domain.GeneralCorrectData;
import com.dmj.domain.MarkError;
import com.dmj.domain.ObjecterrorAnaly;
import com.dmj.domain.Questionimage;
import com.dmj.domain.RegExaminee;
import com.dmj.domain.RegSample;
import com.dmj.domain.Reg_Th_Log;
import com.dmj.domain.Remark;
import com.dmj.domain.Score;
import com.dmj.domain.Student;
import com.dmj.domain.Subject;
import com.dmj.domain.Task;
import com.dmj.domain.User;
import com.dmj.domain.Userrole;
import com.dmj.domain.vo.Imgpath;
import com.dmj.domain.vo.QuestionGroup_question;
import com.dmj.domain.vo.Questiongroup_mark_setting;
import com.dmj.service.examManagement.ExamService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.examManagement.ExamServiceImpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.ItemThresholdRegUtil;
import com.zht.db.DbUtils;
import com.zht.db.RowArg;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

/* loaded from: ExamDAOImpl.class */
public class ExamDAOImpl {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());
    private SystemService cis = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    TeacherAppScoreDaoImpl teacherAppScoreDao = new TeacherAppScoreDaoImpl();
    AwardPointDaoImpl awardPointDao = new AwardPointDaoImpl();
    private String uploadFileName;
    private String uploadContentType;
    public static ExamService dao = (ExamService) ServiceFactory.getObject(new ExamServiceImpl());
    public static SystemService systemService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    public static ExamService examService = (ExamService) ServiceFactory.getObject(new ExamServiceImpl());

    public Object getOneByNum(String colum, String value, Class cla) {
        if (colum == null || value == null) {
            return null;
        }
        String sql = "select * from " + cla.getSimpleName() + " where " + colum + " = {value} ";
        Map args = StreamMap.create().put("value", (Object) value);
        return this.dao2._queryBean(sql, cla, args);
    }

    public Integer deleteOneByNum(String colum, String value, Class cla) {
        if (colum == null || value == null) {
            return null;
        }
        String sql = "delete from " + cla.getSimpleName() + " where " + colum + " = {value}";
        this.log.info("### deleteOneByNum sql: " + sql);
        Map args = new HashMap();
        args.put("value", value);
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    public String getExampaperNumBySubjectAndGradeAndExam(String examNum, String subjectNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryStr(" select examPaperNum from exampaper where examnum={examNum} and  subjectnum={subjectNum} and gradenum ={gradeNum} and isHidden='F' ", args);
    }

    public String getExampaperNumBySubjectAndGradeAndExam1(String examNum, String subjectNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryStr(" select examPaperNum from exampaper where examnum={examNum} and  subjectnum={subjectNum} and gradenum ={gradeNum} and isHidden='F'  ", args);
    }

    public String judgeYouOrWang(String examNum, String subjectNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryStr(" select type from exampaper where examnum={examNum} and  subjectnum={subjectNum} and gradenum ={gradeNum} ", args);
    }

    public String checkIfHaveCalculate(String examNum, String subjectNum, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        String exampapernum = this.dao2._queryStr(" select examPaperNum from exampaper where examnum={examNum} and  subjectnum={subjectNum} and gradenum ={gradeNum}  ", args);
        args.put("exampapernum", exampapernum);
        return this.dao2._queryStr("select count(1) from studentlevel where examPaperNum={exampapernum} ", args);
    }

    public String getExampaperNum(String examNum, String subjectNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryStr(" select examPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
    }

    public String getpExampaperNum(String examNum, String subjectNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryStr(" select pexamPaperNum from exampaper where examNum={examNum} and gradeNum ={gradeNum} and subjectNum={subjectNum} ", args);
    }

    public Exampaper getExampaper(String examNum, String subjectNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return (Exampaper) this.dao2._queryBean(" select examPaperNum,pexamPaperNum,xuankaoqufen,isHidden,fenzuyuejuan from exampaper where examnum={examNum} and  subjectnum={subjectNum} and gradenum ={gradeNum}  ", Exampaper.class, args);
    }

    public List<Exampaper> getExampaperNum_threeSjt(String examNum, String subjectNum, String gradeNum, String pexamPaperNum) {
        String pexamPaperNum2 = getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        Map args = StreamMap.create().put("pexamPaperNum", (Object) pexamPaperNum2);
        return this.dao2._queryBeanList(" SELECT * FROM exampaper WHERE pexamPaperNum={pexamPaperNum} AND totalScore!=0 AND isHidden!='F'", Exampaper.class, args);
    }

    public String getExampaperNumBySubjectAndGradeAndExam(Integer examNum, Integer subjectNum, Integer gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryStr(" select examPaperNum from exampaper where examnum={examNum}  and subjectnum={subjectNum} and  gradenum ={gradeNum} and isHidden='F' ", args);
    }

    public Exampaper getExampaperInfo(String examNum, String subjectNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return (Exampaper) this.dao2._queryBean(" select examPaperNum,jie,IFNULL(totalpage,'1') totalPage,pexamPaperNum from exampaper where  examnum={examNum}  and  gradenum ={gradeNum} and subjectnum={subjectNum} ", Exampaper.class, args);
    }

    public Exampaper getExampaperInfo1(String examNum, String subjectNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return (Exampaper) this.dao2._queryBean(" SELECT e1.jie,IFNULL(e1.totalpage,'1') totalPage,(case when e1.examPaperNum<>e2.examPaperNum then e1.subjectNum else e2.subjectnum end) subjectnum,e1.examPaperNum pexamPaperNum, e2.examPaperNum  from exampaper e1  LEFT JOIN(  select * from exampaper where  examnum={examNum}  )e2 on e1.examPaperNum=e2.pexamPaperNum    where   e1.examnum={examNum} and e2.examPaperNum is not null  and e2.subjectNum ={subjectNum} and  e1.gradenum ={gradeNum} ", Exampaper.class, args);
    }

    public Exampaper getExampaperInfo_his(String examNum, String subjectNum, String gradeNum, String isHistory) {
        String sql;
        if (isHistory.equals("F")) {
            sql = " select examPaperNum,jie,IFNULL(totalpage,'1') totalPage,pexamPaperNum from exampaper where  examnum={examNum} and subjectnum={subjectNum}  and  gradenum ={gradeNum} ";
        } else {
            sql = " select examPaperNum,jie,IFNULL(totalpage,'1') totalPage,pexamPaperNum from his_exampaper where  examnum={examNum} and subjectnum={subjectNum}  and  gradenum ={gradeNum} ";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return (Exampaper) this.dao2._queryBean(sql, Exampaper.class, args);
    }

    public List<Subject> getSubjectName(String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryBeanList("SELECT subjectName,subjectType FROM `subject` WHERE subjectNum={subjectNum} ", Subject.class, args);
    }

    public void updateTaskData(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        this.dao2._execute("UPDATE questiongroup q LEFT JOIN\t(SELECT exampaperNum,groupNum,COUNT(id) num\tFROM task WHERE exampaperNum={examPaperNum} GROUP BY exampaperNum, groupNum) r ON r.exampaperNum=q.exampaperNum AND r.groupNum=q.groupNum SET q.totalnum=r.num\tWHERE q.exampaperNum={examPaperNum} ", args);
    }

    public Integer save(List defines) {
        Integer returnCode = null;
        for (Object obj : defines) {
            Define define = (Define) obj;
            returnCode = Integer.valueOf(this.dao2.save(define));
        }
        return returnCode;
    }

    public List<AjaxData> getAjaxGradeList(String exam, String subject, String school) {
        Map args = StreamMap.create().put("exam", (Object) exam).put(License.SCHOOL, (Object) school).put("subject", (Object) subject);
        return this.dao2._queryBeanList("SELECT DISTINCT ep.gradeNum num,g.gradeName name from (select examNum,exampapernum,classNum,schoolNum from classexam where examNum={exam} and schoolNum={school}) c  LEFT join class ON c.classNum = class.id left join  (select examNum,exampaperNum,gradeNum,subjectNum where examNum={exam} and subjectNum={school} ) ep ON ep.exampapernum = c.exampapernum LEFT JOIN basegrade g on g.gradeNum = ep.gradeNum  WHERE  ep.examnum = {exam} and ep.subjectNum = {subject} and c.schoolNum = {school} ", AjaxData.class, args);
    }

    public List<AjaxData> getAjaxSchoolList(String exam, String subject) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("subject", (Object) subject);
        return this.dao2._queryBeanList("SELECT  DISTINCT c.schoolNum num , s.schoolName name from classexam c LEFT JOIN school s on c.schoolNum = s.id WHERE c.exampapernum in ( SELECT ep.exampapernum from exampaper ep where ep.examnum = {exam} and ep.subjectNum = {subject} )", AjaxData.class, args);
    }

    public List<AjaxData> getAjaxClassList(String exam, String school, String subject, String grade) {
        this.log.info("getAjaxClassList:--select c.classNum num, class.className name  from classexam c LEFT JOIN class on c.classNum = class.id and class.schoolNum={school} WHERE c.examPaperNum = (  \t\tSELECT exampaper.examPaperNum \t\tfrom exampaper \t\twhere exampaper.examNum = {exam}\t and exampaper.subjectNum = {subject}\tand exampaper.gradeNum = {grade}) AND c.schoolNum = {school}  ");
        Map args = StreamMap.create().put(License.SCHOOL, (Object) school).put("exam", (Object) exam).put("subject", (Object) subject).put("grade", (Object) grade);
        return this.dao2._queryBeanList("select c.classNum num, class.className name  from classexam c LEFT JOIN class on c.classNum = class.id and class.schoolNum={school} WHERE c.examPaperNum = (  \t\tSELECT exampaper.examPaperNum \t\tfrom exampaper \t\twhere exampaper.examNum = {exam}\t and exampaper.subjectNum = {subject}\tand exampaper.gradeNum = {grade}) AND c.schoolNum = {school}  ", AjaxData.class, args);
    }

    public List<AjaxData> getAjaxExamList(String systemType) {
        Map args = StreamMap.create().put(License.SYSTYPE, (Object) systemType).put("STATUS_EXAM_COMPLETE", (Object) "9");
        return this.dao2._queryBeanList("select examNum num,examName name from exam where isDelete ='F' and type={systemType} and status!={STATUS_EXAM_COMPLETE} ORDER BY insertDate DESC", AjaxData.class, args);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public List<AjaxData> getAjaxExamList_zy(String systemType, String userId, String oneOrMore) {
        new ArrayList();
        Map args = StreamMap.create().put("STATUS_EXAM_COMPLETE", (Object) "9").put("userNum", (Object) userId);
        List _queryBeanList = this.dao2._queryBeanList("select examNum num,examName name,type ext1,examDate ext2 from exam where isDelete ='F'  and status!={STATUS_EXAM_COMPLETE} ORDER BY examDate DESC,insertDate DESC", AjaxData.class, args);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args);
        if (userId != null && "" != userId && !userId.equals("-1") && !userId.equals("-2") && null == map) {
            if ("1".equals(oneOrMore)) {
                if (null == map) {
                    _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT e.examNum num,e.examName name,e.type ext1,e.examDate ext2  from exam e  LEFT JOIN examschool es ON e.examNum=es.examNum  right JOIN (select schoolNum from schoolscanpermission where userNum={userNum} union select schoolNum from user where id={userNum}) scm ON CAST(es.schoolNum as char)=CAST(scm.schoolNum  as char)  WHERE e.isDelete ='F' and status!={STATUS_EXAM_COMPLETE}  ORDER BY e.examDate DESC,e.insertDate DESC", AjaxData.class, args);
                }
            } else if ("2".equals(oneOrMore)) {
                List<AjaxData> ajaxData = getUserNoPerExam(userId);
                if (ajaxData.size() != 0) {
                    for (AjaxData data : ajaxData) {
                        String num = data.getNum();
                        _queryBeanList.removeIf(o -> {
                            return num.equals(Convert.toStr(o.getNum()));
                        });
                    }
                }
            }
        }
        return _queryBeanList;
    }

    public List<AjaxData> getAjaxSubjectList(Integer exam, Integer systemType, Integer gradeNum) {
        String sql = "SELECT DISTINCT e.subjectNum num ,s.subjectName name,e.examPaperNum ext1 ,e.appealDate ext2,IFNULL(qq.shensunum,0) ext3 from (SELECT examPaperNum,examNum,gradeNum,subjectNum,appealDate FROM exampaper  WHERE examNum={exam}   ";
        if (null != gradeNum) {
            sql = sql + "AND gradeNum={gradeNum}   ";
        }
        String sql2 = sql + "AND isHidden={FALSE} AND type = '0'  ) e  LEFT JOIN `subject` s on e.subjectNum = s.subjectNum LEFT JOIN (SELECT examPaperNum ,count(1) shensunum from questiongroup_question WHERE shensuStatus=1 GROUP BY exampaperNum )qq ON e.examPaperNum=qq.exampaperNum  order by s.orderNum";
        Map args = StreamMap.create().put("exam", (Object) exam).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("FALSE", (Object) "F");
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    public List<AjaxData> getAjaxSubjectList_zy(Integer exam, Integer systemType, Integer gradeNum) {
        String sql = "SELECT DISTINCT e.subjectNum num ,s.subjectName name,s.xuankaoqufen ext1  from (SELECT examPaperNum,examNum,gradeNum,subjectNum FROM exampaper   WHERE examNum={exam} and  type <2  ";
        if (null != gradeNum) {
            sql = sql + "AND gradeNum={gradeNum}  ";
        }
        String sql2 = sql + "AND isHidden={FALSE} ) e   LEFT JOIN `subject` s on e.subjectNum = s.subjectNum order by s.orderNum,e.subjectNum";
        Map args = StreamMap.create().put("exam", (Object) exam).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("FALSE", (Object) "F");
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    public List<AjaxData> getAjaxSubjectListForOE_zy(Integer exam, Integer systemType, Integer gradeNum) {
        String sql = "SELECT DISTINCT e.subjectNum num ,s.subjectName name,s.xuankaoqufen ext1  from (SELECT examPaperNum,examNum,gradeNum,subjectNum FROM exampaper   WHERE examNum={exam} and  type =3  ";
        if (null != gradeNum) {
            sql = sql + "AND gradeNum={gradeNum}   ";
        }
        String sql2 = sql + "AND isHidden={FALSE} ) e  LEFT JOIN `subject` s on e.subjectNum = s.subjectNum order by s.orderNum,e.subjectNum";
        Map args = StreamMap.create().put("exam", (Object) exam).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("FALSE", (Object) "F");
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    public List<AjaxData> getUserPositionSubjectList(String exam, String userNum, String mark) {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put("exam", (Object) exam).put("FALSE", (Object) "F").put(Const.SYSTEM_TYPE, (Object) mark);
        return this.dao2._queryBeanList("SELECT DISTINCT e.realsjtNum num,e.realsjtName name   FROM (  SELECT DISTINCT us.subjectNum,substring(su.subjectName,1,2) subjectName   FROM (SELECT s.pid subjectNum   FROM (SELECT DISTINCT subjectNum FROM userposition WHERE userNum={userNum} AND subjectNum is NOT NULL   ) u LEFT JOIN `subject` s ON s.subjectNum=u.subjectNum UNION SELECT DISTINCT u.subjectNum subjectNum FROM userposition u   WHERE u.userNum={userNum} AND u.subjectNum is NOT NULL   )us LEFT JOIN `subject` su ON us.subjectNum=su.subjectNum   )us   LEFT JOIN (SELECT ep.subjectNum,ep.isHidden,substring(sjt.subjectName,1,2) subjectName,sjt.subjectNum realsjtNum,sjt.subjectName realsjtName FROM exampaper ep    LEFT JOIN `subject` sjt ON ep.subjectNum=sjt.subjectNum WHERE ep.examNum={exam} AND ep.isHidden={FALSE} AND ep.type={mark}   ) e ON e.subjectName=us.subjectName WHERE e.isHidden is not null", AjaxData.class, args);
    }

    public List getUserroleNum(String userId) {
        Map args = StreamMap.create().put("userId", (Object) userId);
        return this.dao2._queryBeanList("SELECT roleNum FROM userrole WHERE userNum={userId} ", Userrole.class, args);
    }

    public List<AjaxData> getUserPositionGradeList(String examNum, String userNum, String subject, String mark) {
        String sql = "SELECT distinct u.gradeNum num, g.gradeName name FROM userposition u LEFT JOIN grade g ON u.gradeNum = g.gradeNum AND u.schoolnum = g.schoolNum AND u.jie = g.jie LEFT JOIN exampaper e ON u.gradeNum = e.gradeNum AND u.subjectNum = e.subjectNum AND u.jie = g.jie WHERE examNum={examNum} ";
        if (userNum != null && !userNum.equals("")) {
            sql = sql + " AND u.userNum={userNum} ";
        }
        if (mark != null && !mark.equals("") && !mark.equals("-1")) {
            sql = sql + " AND e.type={mark} ";
        }
        String sql2 = sql + " AND u.subjectNum is not null ORDER BY num";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("userNum", (Object) userNum).put(Const.SYSTEM_TYPE, (Object) mark);
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    public List<AjaxData> getAjaxGradeList2(String exam, String subject, String systemType) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("subject", (Object) subject).put(License.SYSTYPE, (Object) systemType);
        return this.dao2._queryBeanList("SELECT DISTINCT g.gradeName name,e.gradeNum num from (SELECT *  FROM exampaper WHERE examNum={exam} AND subjectNum={subject} AND  isHidden='F' and type={systemType}) e LEFT JOIN basegrade g on e.gradeNum = g.gradeNum ", AjaxData.class, args);
    }

    public List<AjaxData> getAjaxGradeList2_zy(String exam, String subject, String systemType) {
        String aa = "3".equals(systemType) ? "and type ='3' " : "";
        String sql = "SELECT DISTINCT g.gradeName name,e.gradeNum num,e.totalScore ext1,e.jie ext2 from (SELECT *  FROM exampaper WHERE examNum={exam} AND subjectNum={subject} AND  isHidden='F' " + aa + ") e  LEFT JOIN basegrade g on e.gradeNum = g.gradeNum order by e.gradeNum DESC";
        Map args = StreamMap.create().put("exam", (Object) exam).put("subject", (Object) subject);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getAjaxExaminationRoomList(String exam, String subject, String grade) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("subject", (Object) subject).put("grade", (Object) grade);
        return this.dao2._queryBeanList("SELECT DISTINCT er.examinationRoomName name,er.examinationRoomNum num from exampaper ep,examinationroom er LEFT JOIN examineenumerror ene on er.examinationRoomNum = ene.examinationRoom WHERE ep.examNum = {exam} and ep.subjectNum = {subject} and ep.gradeNum={grade} and ep.examPaperNum=ene.errorPaperNum", AjaxData.class, args);
    }

    public List<Task> getAjaxQuestionNumList(Integer exampaperNum, String groupnum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryBeanList("select DISTINCT groupNum,groupName realQuestionNum,groupType ext1 from questiongroup where exampaperNum={exampaperNum} ORDER BY REPLACE(groupName,'_','.')*1", Task.class, args);
    }

    public List<Task> getAjaxQuestionNumList2(Integer exampaperNum, String groupnum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryBeanList("SELECT DISTINCT qq.questionNum groupNum,d.questionNum realQuestionNum FROM questiongroup_question qq  LEFT JOIN (select IF(d.isparent='1',sub.id,d.id) id ,IF(d.isparent='1',sub.questionNum,d.questionNum)questionNum FROM define d LEFT JOIN subdefine sub ON d.id = sub.pid where d.exampaperNum={exampaperNum} )d ON qq.questionNum = d.id WHERE qq.exampaperNum = {exampaperNum} ORDER BY REPLACE(d.questionNum, '_', '.')* 1  ", Task.class, args);
    }

    public List<Define> getAjaxQuestionNum(String exampaperNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryBeanList("SELECT def.num,def.name ,def.choosename,def.isParent FROM (SELECT id num,questionNum name ,choosename,isParent FROM define  WHERE examPaperNum={exampaperNum} AND questionType='1' AND isParent='0' UNION ( SELECT subdef.id num,subdef.questionNum name ,def.choosename,def.isParent FROM define def LEFT JOIN subdefine subdef ON def.id=subdef.pid   WHERE def.examPaperNum={exampaperNum} AND def.questionType='1' AND def.isParent='1' ) )def WHERE def.choosename <>'T' ORDER BY REPLACE(def.name,'_','.')*1  ASC", Define.class, args);
    }

    public List<Define> getqNumList(String exampaperNum, String qType) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryBeanList("SELECT def.num,def.name ,def.choosename,def.isParent FROM (SELECT id num,questionNum name ,choosename,isParent FROM define def WHERE def.examPaperNum={exampaperNum}   AND def.isParent='0' UNION ( SELECT subdef.id num,subdef.questionNum name ,def.choosename,def.isParent FROM define def LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE def.examPaperNum={exampaperNum}  AND def.isParent='1' ) )def WHERE def.choosename <>'T' ORDER BY REPLACE(def.name,'_','.')*1  ASC", Define.class, args);
    }

    public List<Task> getAjaxTaskList(Integer exampaperNum, String questionNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("questionNum", (Object) questionNum).put("task_insertUser_defalt", (Object) "-1").put("TRUE", (Object) "T");
        return this.dao2._queryBeanList("SELECT t.questionNum,u.realname updateUser,t.questionScore,u.userid,tch.teacherNum,sch.schoolName   FROM(SELECT questionNum,exampaperNum,insertUser,questionScore FROM task  WHERE exampaperNum={exampaperNum} AND questionNum={questionNum}  AND insertUser<>{task_insertUser_defalt}  AND `status`={TRUE}   )t  LEFT JOIN `user` u ON u.id=t.insertUser LEFT JOIN teacher tch ON tch.id = u.userid  LEFT JOIN school sch ON sch.id=u.schoolNum   WHERE u.id IS NOT NULL", Task.class, args);
    }

    public List<Task> getAjaxTaskList2_old(Integer exampaperNum, String questionNum) {
        String sql;
        String enforce = this.cis.fenzu(String.valueOf(exampaperNum));
        if ("0".equals(enforce)) {
            sql = "SELECT t.schoolNum,ifnull(t.schoolname,'-') schoolname,t.teacherNum,t.teacherName,t.numOfStudent,t.u_count teacherCount, convert(IFNULL(t.num,(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))),signed) ext1,   convert(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0),signed) ext2 ,     convert(IFNULL(t.num-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0)),(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0))   ),signed) ext3, if(MAX(t.updateTime) is null||IFNULL(TIMESTAMPDIFF(MINUTE,MAX(t.updateTime),NOW()),4)>2,'否','是') ext4 from( \t\tselect u.schoolNum,ifnull(sc.schoolname,'-')schoolname,te.teacherNum,te.teacherName,qu.userNum,y.d_num, \t\tq.num,q1.num num1,q1.yueJuan,quu.u_count,tu.status,y.tempcount,tu.updateTime,tu.insertUser, \t\tcase when s.choosename<>'s' and s.makType=1 then IFNULL(ch.total*2,0)+IFNULL(x.count,0)when s.choosename<>'s' and s.makType=0 then IFNULL(ch.total,0)when s.choosename='s' and s.makType=1 \t\tthen IFNULL(st.count*2,0)+IFNULL(x.count,0)when s.choosename='s' and s.makType=0 then IFNULL(st.count ,0)end numOfStudent  \t\tfrom ( \t\t\tSELECT qu.userNum,qu.groupNum,qu.exampaperNum,qu.userType,slg.schoolGroupNum,slg.schoolGroupName,0 assist from questiongroup_user qu \t\t\tLEFT JOIN `user` u on qu.userNum=u.id LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum WHERE qu.groupNum={questionNum}     ) qu LEFT join user u on qu.userNum = u.id  LEFT join (  \t\t\tSELECT q.groupNum,m.makType,dd.choosename from questiongroup q \t\t\tINNER JOIN questiongroup_mark_setting m on q.groupNum=m.groupNum \t\t\tINNER JOIN ( \t\t\t\tSELECT d.id orderId,d.id ,d.questionNum groupName,choosename from define d where d.id={questionNum}  \t\t\t\tUNION  \t\t\t\tSELECT sd.pid orderId,sd.id,sd.questionNum groupName,d.choosename  from define d LEFT JOIN subdefine sd on sd.pid=d.id where sd.id={questionNum}  \t\t\t) dd on q.groupNum = dd.id \t) s on qu.groupNum = s.groupNum LEFT JOIN choosescale ch on qu.groupNum=ch.groupNum left join ( \t\t\tselect s.examPaperNum ,count(en.studentId) count,qms.groupNum          from examinationnum en LEFT join exampaper s on en.examNum = s.examNum and en.subjectNum = s.subjectNum and en.gradeNum = s.gradeNum          LEFT JOIN questiongroup_mark_setting qms ON s.exampaperNum = qms.exampaperNum where s.examPaperNum ={exampaperNum}          GROUP BY s.examPaperNum,qms.groupNum \t)st on qu.exampaperNum=st.exampaperNum and qu.groupNum = st.groupNum LEFT JOIN ( \t\t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num,count(DISTINCT(questionNum)) as tempcount \t\t\tFROM task  t WHERE t.groupNum={questionNum}  AND  t.status='T' GROUP BY groupNum   \t)y ON qu.groupNum = y.groupNum LEFT JOIN ( \t\t\tSELECT groupNum,count(1) count FROM task  t  WHERE t.groupNum={questionNum}   and t.userNum=3  GROUP BY groupNum \t)x ON qu.groupNum = x.groupNum LEFT join ( \t\t\t\tselect q.num,q.groupNum,q.exampaperNum,q.insertUser from quota q WHERE q.groupNum={questionNum} \t) q on qu.groupNum = q.groupNum and qu.userNum = q.insertUser LEFT JOIN( \t\t\t\tSELECT count(1) yueJuan,sum(q.num) num,q.groupNum FROM quota q WHERE q.groupNum={questionNum}  GROUP BY q.groupNum \t)q1 ON qu.groupNum = q1.groupNum left join ( \t\t\t\tselect qu.exampaperNum,qu.groupNum,count(DISTINCT(qu.userNum)) u_count from questiongroup_user qu where qu.groupNum={questionNum}  GROUP BY qu.exampaperNum,qu.groupNum \t) quu on qu.groupNum = quu.groupNum  \tLEFT JOIN task tu ON qu.groupNum = tu.groupNum and qu.userNum = tu.insertUser \tLEFT join school sc on u.schoolNum = sc.id  \tLEFT join teacher te on u.userId = te.id  \twhere qu.groupNum={questionNum} )t  group by t.userNum";
        } else {
            sql = "SELECT t.schoolNum,ifnull(t.schoolname,'-') schoolname,t.teacherNum,t.teacherName,t.schoolGroupNum,t.schoolGroupName,t.numOfStudent,t.u_count teacherCount, convert(IFNULL(t.num,(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))),signed) ext1,   convert(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0),signed) ext2 ,     convert(IFNULL(t.num-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0)),(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0))   ),signed) ext3, if(MAX(t.updateTime) is null||IFNULL(TIMESTAMPDIFF(MINUTE,MAX(t.updateTime),NOW()),4)>2,'否','是') ext4 from( \t\tselect qu.userNum,u.schoolNum,ifnull(sc.schoolname,'-')schoolname,te.teacherNum,te.teacherName,qu.schoolGroupNum,qu.schoolGroupName,y.d_num, \t\tq.num,q1.num num1,q1.yueJuan,quu.u_count,tu.status,y.tempcount,tu.updateTime,tu.insertUser, \t \tcase when s.choosename<>'s' and s.makType=1 then IFNULL(ch.total*2,0)+IFNULL(x.count,0)when s.choosename<>'s' and s.makType=0 then IFNULL(ch.total,0)when s.choosename='s' and s.makType=1 then IFNULL(st.count*2,0)+IFNULL(x.count,0)when s.choosename='s' and s.makType=0 then IFNULL(st.count ,0)end numOfStudent \t \tfrom ( \t\t\tSELECT qu.userNum,qu.groupNum,qu.exampaperNum,qu.userType,slg.schoolGroupNum,slg.schoolGroupName,0 assist from questiongroup_user qu  \t\t\tLEFT JOIN `user` u on qu.userNum=u.id LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum WHERE qu.groupNum={questionNum} \t \t) qu LEFT join user u on qu.userNum = u.id LEFT join ( \t\t\t\tSELECT q.groupNum,m.makType,dd.choosename from questiongroup q \t\t\t\tINNER JOIN questiongroup_mark_setting m on q.groupNum=m.groupNum \t\t\t\tINNER JOIN ( \t\t\t\t\tSELECT d.id orderId,d.id ,d.questionNum groupName,choosename from define d where d.id={questionNum}  \t\t\t\t\tUNION \t\t\t\t\tSELECT sd.pid orderId,sd.id,sd.questionNum groupName,d.choosename  from define d LEFT JOIN subdefine sd on sd.pid=d.id where sd.id={questionNum}  \t\t\t\t) dd on q.groupNum = dd.id    \t) s on qu.groupNum = s.groupNum LEFT JOIN choosescale ch on qu.groupNum=ch.groupNum  and qu.schoolGroupNum=ch.schoolGroupNum LEFT JOIN (    \t\tselect r.ext1 exampapernum,r.schoolGroupNum,d.id,r.count from( \t\t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (s1.xuankaoqufen=2 or s1.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d  \t\t\t\tleft join exampaper s on d.exampaperNum = s.exampaperNum  \t\t\t\tleft join exampaper s1 on d.category = s1.exampaperNum where d.id={questionNum} \t\t\t\tUNION \t\t\t\tSELECT sd.id,sd.examPaperNum,CASE WHEN (s1.xuankaoqufen=2 or s1.xuankaoqufen=3) then sd.category else sd.examPaperNum end category from subdefine sd  \t\t\t\tleft join exampaper s on sd.exampaperNum = s.exampaperNum left join exampaper s1 on sd.category = s1.exampaperNum where sd.id={questionNum} \t\t\t)d LEFT JOIN( \t\t\t\t\tSELECT sss.examNum,sss.gradeNum,sss.subjectNum,slg.schoolGroupNum,sss.ext1,count(1) count from( \t\t\t\t\t\t\tSELECT n.examNum,n.gradeNum,n.subjectNum,n.studentId,n.schoolNum,s.examPaperNum ext1 from examinationnum n \t\t\t\t\t\t\tINNER join exampaper s on n.examNum=s.examNum and n.gradeNum=s.gradeNum and n.subjectNum=s.subjectNum where s.examPaperNum ={exampaperNum} \t\t\t\t\t\t\tunion \t\t\t\t\t\t\tSELECT r.examNum,r.gradeNum,r.subjectNum,r.studentId,r.schoolNum,e.examPaperNum ext1 FROM( \t\t\t\t\t\t\t\tSELECT n.examNum,n.gradeNum,n.subjectNum,n.studentId,n.schoolNum,s.examPaperNum from examinationnum n  \t\t\t\t\t\t\t\tINNER join exampaper s on n.examNum=s.examNum and n.gradeNum=s.gradeNum and n.subjectNum=s.subjectNum where s.examPaperNum ={exampaperNum} \t\t\t\t\t\t\t)r INNER JOIN student s on r.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid  \t\t\t\t\t\t\tINNER JOIN exampaper e on ls.subjectNum=e.subjectNum and r.examPaperNum=e.pexampaperNum \t\t\t\t\t)sss INNER JOIN schoolgroup slg on sss.schoolNum=slg.schoolNum GROUP BY sss.ext1,slg.schoolGroupNum \t\t\t)r on d.category=r.ext1   ) st on qu.schoolGroupNum=st.schoolGroupNum and qu.groupNum=st.id LEFT JOIN (  \t\t\tSELECT t.groupNum,slg.schoolGroupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num,count(DISTINCT(questionNum)) as tempcount  \t\t\t\tFROM task  t  LEFT JOIN student stu on t.studentId=stu.id INNER JOIN schoolgroup slg on stu.schoolNum=slg.schoolNum  \t\t\t\tWHERE t.groupNum={questionNum}  AND  t.status='T'   GROUP BY t.groupNum,slg.schoolGroupNum     )y ON qu.groupNum = y.groupNum and qu.schoolGroupNum=y.schoolGroupNum LEFT JOIN (   \t\t\tSELECT t.groupNum,slg.schoolGroupNum,count(1) count FROM task  t  LEFT JOIN student stu on t.studentId=stu.id INNER JOIN schoolgroup slg on stu.schoolNum=slg.schoolNum  \t\t\t\tWHERE t.groupNum={questionNum}  and t.userNum=3  GROUP BY t.groupNum,slg.schoolGroupNum \t)x ON qu.groupNum = x.groupNum and qu.schoolGroupNum=x.schoolGroupNum LEFT join (    \t\t\t\tselect q.num,q.groupNum,q.insertUser from quota q WHERE q.groupNum={questionNum}  \t) q on qu.groupNum = q.groupNum and qu.userNum = q.insertUser LEFT JOIN(    \t\t\tSELECT sum(q.yuejuan) yuejuan,sl.schoolgroupNum,sum(q.num) num,q.groupNum FROM schoolgroup sl  \t\t\t\tINNER JOIN (  \t\t\t\t\tSELECT count(1) yueJuan,u.schoolNum,sum(qou.num) num,qou.groupNum from quota qou  \t\t\t\t\tINNER JOIN user u on qou.insertUser=u.id and u.userType=1 \t\t\t\t\tWHERE qou.groupNum={questionNum}  GROUP BY qou.groupNum,u.schoolNum  \t\t\t\t)q on sl.schoolNum=q.schoolNum GROUP BY q.groupNum,sl.schoolgroupNum \t)q1 ON qu.groupNum=q1.groupNum and qu.schoolgroupNum = q1.schoolgroupNum left join (   \t\t\tSELECT qu.exampaperNum,qu.groupNum,sl.schoolgroupNum,sum(qu.count) u_count FROM schoolgroup sl \t\t\t\tINNER JOIN ( \t\t\t\t\tselect qu.exampaperNum,qu.groupNum,u.schoolNum,count(1) count from questiongroup_user qu \t\t\t\t\tINNER JOIN user u on qu.userNum=u.id and u.userType=1  \t\t\t\t\tWHERE qu.groupNum={questionNum} GROUP BY qu.groupNum,u.schoolNum \t\t\t\t)qu on sl.schoolNum=qu.schoolNum GROUP BY qu.groupNum,sl.schoolgroupNum \t) quu on qu.groupNum=quu.groupNum and qu.schoolgroupNum = quu.schoolgroupNum    \t\tLEFT JOIN task tu ON qu.groupNum = tu.groupNum and qu.userNum = tu.insertUser  \t\tLEFT join school sc on u.schoolNum = sc.id     \t\tLEFT join teacher te on u.userId = te.id    \twhere qu.groupNum={questionNum}  and qu.userType <> 2  )t  group by t.userNum ";
        }
        Map args = StreamMap.create().put("questionNum", (Object) questionNum).put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryBeanList(sql, Task.class, args);
    }

    public List<Task> getAjaxTaskList2(Integer exampaperNum, String questionNum) {
        String questionNumStr;
        String sql;
        int xuanzuotiNum = questionNum.split("_").length;
        if (questionNum.indexOf("_") != -1) {
            questionNumStr = questionNum.replace("_", Const.STRING_SEPERATOR);
        } else {
            questionNumStr = questionNum;
        }
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        args.put("exampaperNum", exampaperNum);
        args.put("questionNumStr", questionNumStr);
        String enforce = this.cis.fenzu(String.valueOf(exampaperNum));
        if ("0".equals(enforce)) {
            sql = "SELECT t.schoolNum,ifnull(t.schoolname,'-') schoolname,t.teacherNum,t.teacherName,t.numOfStudent,t.u_count teacherCount, convert(IFNULL(t.num,(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))),signed) ext1,   convert(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0),signed) ext2 ,     convert(IFNULL(t.num-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0)),(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0))   ),signed) ext3, if(MAX(t.updateTime) is null||IFNULL(TIMESTAMPDIFF(MINUTE,MAX(t.updateTime),NOW()),4)>2,'否','是') ext4 from( \t\tselect u.schoolNum,ifnull(sc.schoolname,'-')schoolname,te.teacherNum,te.teacherName,qu.userNum,y.d_num, \t\tq.num,q1.num num1,q1.yueJuan,quu.u_count,tu.status,y.tempcount,tu.updateTime,tu.insertUser, \t\tcase when s.makType=1 \t\tthen IFNULL(st.count*2,0)+IFNULL(x.count,0)when s.makType=0 then IFNULL(st.count ,0)end numOfStudent  \t\tfrom ( \t\t\tSELECT qu.userNum,qu.groupNum,qu.exampaperNum,qu.userType,slg.schoolGroupNum,slg.schoolGroupName,0 assist from questiongroup_user qu \t\t\tLEFT JOIN `user` u on qu.userNum=u.id LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum WHERE qu.groupNum in ({questionNumStr[]})     ) qu LEFT join user u on qu.userNum = u.id  LEFT join (  \t\t\tSELECT q.groupNum,m.makType,dd.choosename from questiongroup q \t\t\tINNER JOIN questiongroup_mark_setting m on q.groupNum=m.groupNum \t\t\tINNER JOIN ( \t\t\t\tSELECT d.id orderId,d.id ,d.questionNum groupName,choosename from define d where d.id in ({questionNumStr[]})  \t\t\t\tUNION  \t\t\t\tSELECT sd.pid orderId,sd.id,sd.questionNum groupName,d.choosename  from define d LEFT JOIN subdefine sd on sd.pid=d.id where sd.id in ({questionNumStr[]})  \t\t\t) dd on q.groupNum = dd.id \t) s on qu.groupNum = s.groupNum left join ( \t\t\tselect s.examPaperNum ,count(en.studentId) count,qms.groupNum          from examinationnum en LEFT join exampaper s on en.examNum = s.examNum and en.subjectNum = s.subjectNum and en.gradeNum = s.gradeNum          LEFT JOIN questiongroup_mark_setting qms ON s.exampaperNum = qms.exampaperNum where s.examPaperNum ={exampaperNum}          GROUP BY s.examPaperNum,qms.groupNum \t)st on qu.exampaperNum=st.exampaperNum and qu.groupNum = st.groupNum LEFT JOIN ( \t\t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num,count(DISTINCT(questionNum)) as tempcount \t\t\tFROM task  t WHERE t.groupNum in ({questionNumStr[]}) AND  t.status='T' GROUP BY groupNum   \t)y ON qu.groupNum = y.groupNum LEFT JOIN ( \t\t\tSELECT groupNum,count(1) count FROM task  t  WHERE t.groupNum in ({questionNumStr[]}) and t.userNum=3  GROUP BY groupNum \t)x ON qu.groupNum = x.groupNum LEFT join ( \t\t\t\tselect q.num*" + xuanzuotiNum + " num,q.groupNum,q.exampaperNum,q.insertUser from quota q WHERE q.groupNum in ({questionNumStr[]}) \t) q on qu.groupNum = q.groupNum and qu.userNum = q.insertUser LEFT JOIN( \t\t\t\tSELECT count(1) yueJuan,sum(q.num)*" + xuanzuotiNum + " num,q.groupNum FROM quota q WHERE q.groupNum in ({questionNumStr[]}) GROUP BY q.groupNum \t)q1 ON qu.groupNum = q1.groupNum left join ( \t\t\t\tselect qu.exampaperNum,qu.groupNum,count(DISTINCT(qu.userNum)) u_count from questiongroup_user qu where qu.groupNum in ({questionNumStr[]}) GROUP BY qu.exampaperNum,qu.groupNum \t) quu on qu.groupNum = quu.groupNum  \tLEFT JOIN task tu ON qu.groupNum = tu.groupNum and qu.userNum = tu.insertUser \tLEFT join school sc on u.schoolNum = sc.id  \tLEFT join teacher te on u.userId = te.id  \twhere qu.groupNum in ({questionNumStr[]}) )t  group by t.userNum";
        } else {
            sql = "SELECT t.schoolNum,ifnull(t.schoolname,'-') schoolname,t.teacherNum,t.teacherName,t.schoolGroupNum,t.schoolGroupName,t.numOfStudent,t.u_count teacherCount, convert(IFNULL(t.num,(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))),signed) ext1,   convert(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0),signed) ext2 ,     convert(IFNULL(t.num-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0)),(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0))   ),signed) ext3, if(MAX(t.updateTime) is null||IFNULL(TIMESTAMPDIFF(MINUTE,MAX(t.updateTime),NOW()),4)>2,'否','是') ext4 from( \t\tselect qu.userNum,u.schoolNum,ifnull(sc.schoolname,'-')schoolname,te.teacherNum,te.teacherName,qu.schoolGroupNum,qu.schoolGroupName,y.d_num, \t\tq.num,q1.num num1,q1.yueJuan,quu.u_count,tu.status,y.tempcount,tu.updateTime,tu.insertUser, \t \tcase when s.makType=1 then IFNULL(st.count*2,0)+IFNULL(x.count,0)when s.makType=0 then IFNULL(st.count ,0)end numOfStudent \t \tfrom ( \t\t\tSELECT qu.userNum,qu.groupNum,qu.exampaperNum,qu.userType,slg.schoolGroupNum,slg.schoolGroupName,0 assist from questiongroup_user qu  \t\t\tLEFT JOIN `user` u on qu.userNum=u.id LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum WHERE qu.groupNum in ({questionNumStr[]}) \t \t) qu LEFT join user u on qu.userNum = u.id LEFT join ( \t\t\t\tSELECT q.groupNum,m.makType,dd.choosename from questiongroup q \t\t\t\tINNER JOIN questiongroup_mark_setting m on q.groupNum=m.groupNum \t\t\t\tINNER JOIN ( \t\t\t\t\tSELECT d.id orderId,d.id ,d.questionNum groupName,choosename from define d where d.id in ({questionNumStr[]})  \t\t\t\t\tUNION \t\t\t\t\tSELECT sd.pid orderId,sd.id,sd.questionNum groupName,d.choosename  from define d LEFT JOIN subdefine sd on sd.pid=d.id where sd.id in ({questionNumStr[]})  \t\t\t\t) dd on q.groupNum = dd.id    \t) s on qu.groupNum = s.groupNum LEFT JOIN (    \t\tselect r.ext1 exampapernum,r.schoolGroupNum,d.id,r.count from( \t\t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (s1.xuankaoqufen=2 or s1.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d  \t\t\t\tleft join exampaper s on d.exampaperNum = s.exampaperNum  \t\t\t\tleft join exampaper s1 on d.category = s1.exampaperNum where d.id in ({questionNumStr[]}) \t\t\t\tUNION \t\t\t\tSELECT sd.id,sd.examPaperNum,CASE WHEN (s1.xuankaoqufen=2 or s1.xuankaoqufen=3) then sd.category else sd.examPaperNum end category from subdefine sd  \t\t\t\tleft join exampaper s on sd.exampaperNum = s.exampaperNum left join exampaper s1 on sd.category = s1.exampaperNum where sd.id in ({questionNumStr[]}) \t\t\t)d LEFT JOIN( \t\t\t\t\tSELECT sss.examNum,sss.gradeNum,sss.subjectNum,slg.schoolGroupNum,sss.ext1,count(1) count from( \t\t\t\t\t\t\tSELECT n.examNum,n.gradeNum,n.subjectNum,n.studentId,n.schoolNum,s.examPaperNum ext1 from examinationnum n \t\t\t\t\t\t\tINNER join exampaper s on n.examNum=s.examNum and n.gradeNum=s.gradeNum and n.subjectNum=s.subjectNum where s.examPaperNum ={exampaperNum} \t\t\t\t\t\t\tunion \t\t\t\t\t\t\tSELECT r.examNum,r.gradeNum,r.subjectNum,r.studentId,r.schoolNum,e.examPaperNum ext1 FROM( \t\t\t\t\t\t\t\tSELECT n.examNum,n.gradeNum,n.subjectNum,n.studentId,n.schoolNum,s.examPaperNum from examinationnum n  \t\t\t\t\t\t\t\tINNER join exampaper s on n.examNum=s.examNum and n.gradeNum=s.gradeNum and n.subjectNum=s.subjectNum where s.examPaperNum ={exampaperNum} \t\t\t\t\t\t\t)r INNER JOIN student s on r.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid  \t\t\t\t\t\t\tINNER JOIN exampaper e on ls.subjectNum=e.subjectNum and r.examPaperNum=e.pexampaperNum \t\t\t\t\t)sss INNER JOIN schoolgroup slg on sss.schoolNum=slg.schoolNum GROUP BY sss.ext1,slg.schoolGroupNum \t\t\t)r on d.category=r.ext1   ) st on qu.schoolGroupNum=st.schoolGroupNum and qu.groupNum=st.id LEFT JOIN (  \t\t\tSELECT t.groupNum,slg.schoolGroupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num,count(DISTINCT(questionNum)) as tempcount  \t\t\t\tFROM task  t  LEFT JOIN student stu on t.studentId=stu.id INNER JOIN schoolgroup slg on stu.schoolNum=slg.schoolNum  \t\t\t\tWHERE t.groupNum in ({questionNumStr[]}) AND  t.status='T'   GROUP BY t.groupNum,slg.schoolGroupNum     )y ON qu.groupNum = y.groupNum and qu.schoolGroupNum=y.schoolGroupNum LEFT JOIN (   \t\t\tSELECT t.groupNum,slg.schoolGroupNum,count(1) count FROM task  t  LEFT JOIN student stu on t.studentId=stu.id INNER JOIN schoolgroup slg on stu.schoolNum=slg.schoolNum  \t\t\t\tWHERE t.groupNum in ({questionNumStr[]}) and t.userNum=3  GROUP BY t.groupNum,slg.schoolGroupNum \t)x ON qu.groupNum = x.groupNum and qu.schoolGroupNum=x.schoolGroupNum LEFT join (    \t\t\t\tselect q.num*" + xuanzuotiNum + " num,q.groupNum,q.insertUser from quota q WHERE q.groupNum in ({questionNumStr[]})  \t) q on qu.groupNum = q.groupNum and qu.userNum = q.insertUser LEFT JOIN(    \t\t\tSELECT sum(q.yuejuan) yuejuan,sl.schoolgroupNum,sum(q.num) num,q.groupNum FROM schoolgroup sl  \t\t\t\tINNER JOIN (  \t\t\t\t\tSELECT count(1) yueJuan,u.schoolNum,sum(qou.num)*" + xuanzuotiNum + " num,qou.groupNum from quota qou  \t\t\t\t\tINNER JOIN user u on qou.insertUser=u.id and u.userType=1 \t\t\t\t\tWHERE qou.groupNum in ({questionNumStr[]}) GROUP BY qou.groupNum,u.schoolNum  \t\t\t\t)q on sl.schoolNum=q.schoolNum GROUP BY q.groupNum,sl.schoolgroupNum \t)q1 ON qu.groupNum=q1.groupNum and qu.schoolgroupNum = q1.schoolgroupNum left join (   \t\t\tSELECT qu.exampaperNum,qu.groupNum,sl.schoolgroupNum,sum(qu.count) u_count FROM schoolgroup sl \t\t\t\tINNER JOIN ( \t\t\t\t\tselect qu.exampaperNum,qu.groupNum,u.schoolNum,count(1) count from questiongroup_user qu \t\t\t\t\tINNER JOIN user u on qu.userNum=u.id and u.userType=1  \t\t\t\t\tWHERE qu.groupNum in ({questionNumStr[]}) GROUP BY qu.groupNum,u.schoolNum \t\t\t\t)qu on sl.schoolNum=qu.schoolNum GROUP BY qu.groupNum,sl.schoolgroupNum \t) quu on qu.groupNum=quu.groupNum and qu.schoolgroupNum = quu.schoolgroupNum    \t\tLEFT JOIN task tu ON qu.groupNum = tu.groupNum and qu.userNum = tu.insertUser  \t\tLEFT join school sc on u.schoolNum = sc.id     \t\tLEFT join teacher te on u.userId = te.id    \twhere  qu.groupNum in ({questionNumStr[]}) and qu.userType <> 2  )t  group by t.userNum ";
        }
        return this.dao2._queryBeanList(sql, Task.class, args);
    }

    public List<Task> getTeacherAcgScore(Integer exampaperNum, String questionNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("questionNum", (Object) questionNum).put("task_insertUser_defalt", (Object) "-1").put("TRUE", (Object) "T");
        return this.dao2._queryBeanList("select t.questionNum,u.realname updateUser,IFNULL(t.c,0) questionScore,tch.teacherNum,sch.schoolName  from(select userNum from questiongroup_user   where exampaperNum={exampaperNum}  and groupNum=(SELECT groupNum from questiongroup_question  where exampaperNum={exampaperNum}  and questionNum={questionNum} )  ) qu   LEFT JOIN(SELECT questionNum,insertUser,ROUND(SUM(questionScore)/COUNT(questionScore),2) c  FROM task  WHERE exampaperNum={exampaperNum}  AND questionNum={questionNum}  AND insertUser<>{task_insertUser_defalt}  AND `status`={TRUE}   GROUP BY insertUser   )t ON qu.userNum=t.insertUser   LEFT JOIN `user` u ON qu.userNum=u.id   LEFT JOIN teacher tch ON tch.id = u.userid  LEFT JOIN school sch ON sch.id = u.schoolnum   WHERE u.realname IS NOT NULL UNION ALL  SELECT questionNum,'总平均分' updateUser,IFNULL(ROUND(SUM(questionScore)/COUNT(questionScore),2),0) questionScore,'' teacherNum,'' schoolName   FROM task   WHERE exampaperNum={exampaperNum} AND questionNum={questionNum}  AND insertUser<> {task_insertUser_defalt}  AND `status`={TRUE} ", Task.class, args);
    }

    public List<Task> getAjaxSQuestionList(String exam, String subject, String grade) {
        String ep = getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
        Map args = StreamMap.create().put("ep", (Object) ep);
        return this.dao2._queryBeanList(" select exampaperNum from define where exampaperNum= {ep} union all  select exampaperNum from subdefine where exampaperNum= {ep} order by orderNum", Task.class, args);
    }

    public List<Task> getAjaxFullScoreList(String exampaperNum, String questionNum) {
        Map args = StreamMap.create().put("questionNum", (Object) questionNum).put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryBeanList("select fullScore ext1 from define where id={questionNum}  and  examPaperNum={exampaperNum}  union all   select fullScore ext1 from subdefine where id={questionNum}  and  examPaperNum={exampaperNum}  ", Task.class, args);
    }

    public List<MarkError> getMarkError(String examNum, String schoolNum, String gradeNum, String subjectNum) {
        String examPaperNum = getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("a", (Object) 0).put("b", (Object) 1);
        return this.dao2._queryBeanList("SELECT mr.questionNum,def.questionNum ext1,mr.page   FROM(SELECT questionNum,COUNT(1) page FROM markerror WHERE exampaperNum={examPaperNum}  GROUP BY questionNum)mr  LEFT JOIN(SELECT id,questionNum,examPaperNum  FROM define  WHERE examPaperNum={examPaperNum} AND isParent={a}   UNION    SELECT subdef.id,subdef.questionNum,def.examPaperNum   FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid   WHERE def.examPaperNum={examPaperNum} AND def.isParent={b}  )def ON mr.questionNum=def.id   ORDER BY REPLACE(def.questionNum,'_','.')*1", MarkError.class, args);
    }

    public List<Define> getSQuestionNum(String examnum, String gradeNum, String subjectnum) {
        String exampaperNum = getExampaperNumBySubjectAndGradeAndExam(examnum, subjectnum, gradeNum);
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("QUESTION_TYPE_SUBJECTIVE", (Object) "1");
        return this.dao2._queryBeanList("SELECT questionNum,id  FROM define  WHERE examPaperNum={exampaperNum}  AND isParent='0'  AND questionType={QUESTION_TYPE_SUBJECTIVE} and choosename<>'T'   UNION ALL   SELECT subdef.questionNum,subdef.id  FROM subdefine subdef WHERE subdef.examPaperNum={exampaperNum} AND subdef.questionType={QUESTION_TYPE_SUBJECTIVE}    ORDER BY questionNum *1, REPLACE ( SUBSTRING( questionNum, LOCATE('_', questionNum) + 1, LENGTH(questionNum) ), '_', '' ) ASC", Define.class, args);
    }

    public List<Task> getSTeacherNum(String examnum, String gradeNum, String subjectnum, String questionNum) {
        Map args = new HashMap();
        args.put("examnum", examnum);
        args.put("subjectnum", subjectnum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={examnum} and  subjectNum ={subjectnum} and gradeNum={gradeNum} ", args);
        String sql = (null == questionNum || "-1".equals(questionNum)) ? "select DISTINCT t.insertUser,u.realname ext1  from task t inner join user u on u.id=t.insertUser and t.insertUser<>{task_insertUser_defalt}  and t.exampaperNum={exampaperNum}  " : "select DISTINCT t.insertUser,u.realname ext1  from task t inner join user u on u.id=t.insertUser and t.insertUser<>{task_insertUser_defalt}  AND t.questionNum={questionNum}  ";
        args.put("task_insertUser_defalt", "-1");
        args.put("questionNum", questionNum);
        args.put("exampaperNum", exampaperNum);
        return this.dao2._queryBeanList(sql, Task.class, args);
    }

    public List<Task> getSTeacherNum2(String examnum, String gradeNum, String subjectnum, String questionNum, String groupType) {
        String sql;
        Map args = new HashMap();
        args.put("examnum", examnum);
        args.put("subjectnum", subjectnum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("task_insertUser_defalt", "-1");
        args.put("questionNum", questionNum);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={examnum} and  subjectNum ={subjectnum} and gradeNum={gradeNum} ", args);
        args.put("exampaperNum", exampaperNum);
        if ("1".equals(groupType)) {
            sql = "select DISTINCT t.insertUser,u.realname ext1  from task t inner join user u on u.id=t.insertUser and t.insertUser<>{task_insertUser_defalt}  AND t.questionNum={questionNum}  ";
        } else {
            sql = "2".equals(groupType) ? "select DISTINCT t.insertUser,u.realname ext1  from task t inner join user u on u.id=t.insertUser and t.insertUser<>{task_insertUser_defalt}  AND t.groupNum={questionNum}  " : "select DISTINCT t.insertUser,u.realname ext1  from task t inner join user u on u.id=t.insertUser and t.insertUser<>{task_insertUser_defalt}  and t.exampaperNum={exampaperNum}  ";
        }
        return this.dao2._queryBeanList(sql, Task.class, args);
    }

    public List<Student> getSpotCheck_stuInfo(String scoreId, String examPaperNum, String questionNum) {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId).put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum);
        return this.dao2._queryBeanList("SELECT s.studentId,stu.studentName,stu.studentNum FROM score s   LEFT JOIN student stu   ON s.studentId=stu.id  WHERE s.id={scoreId}  AND s.examPaperNum={examPaperNum} AND s.questionNum={questionNum} ", Student.class, args);
    }

    public List<Task> getSpotCheck(Integer examnum, Integer gradeNum, Integer subjectnum, String questionNum, Integer updateUser, Integer snumber, Integer pagestart, Integer pageSize, String examPaperNum) {
        Map args = new HashMap();
        args.put("examnum", examnum);
        args.put("subjectnum", subjectnum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("exampaperNum", examPaperNum);
        args.put("updateUser", updateUser);
        args.put("questionNum", questionNum);
        args.put("snumber", snumber);
        args.put("pagestart", pagestart);
        args.put("pageSize", pageSize);
        if (null == examPaperNum || "".equals(examPaperNum)) {
            this.dao2._queryStr("select exampaperNum from exampaper where examNum={examnum} and subjectNum={subjectnum} and  gradeNum ={gradeNum} ", args);
        }
        args.put("exampaperNum", examPaperNum);
        String sql = "select DISTINCT t.scoreId,t.exampaperNum,t.questionNum,def.questionNum realQuestionNum   FROM(SELECT id,scoreId,exampaperNum,questionNum,insertUser,questionScore  FROM task WHERE  exampaperNum={exampaperNum}  ";
        if (null != updateUser && updateUser.intValue() != -1) {
            sql = sql + "AND insertUser={updateUser} ";
        }
        String sql2 = sql + "AND `status`='T'  ";
        if (null != questionNum && !"-1".equals(questionNum) && !"0".equals(questionNum)) {
            sql2 = sql2 + "AND questionNum={questionNum}  ";
        }
        String sql3 = sql2 + ")t  LEFT JOIN (SELECT id,questionNum,questionType  FROM define  WHERE examPaperNum={exampaperNum} AND isParent={a} UNION    SELECT subdef.id,subdef.questionNum,def.questionType  FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE def.examPaperNum={exampaperNum} AND def.isParent={b})def  ON t.questionNum=def.id  ";
        if (snumber.intValue() != -1) {
            sql3 = sql3 + "ORDER BY rand() LIMIT {snumber} ";
        } else if (pageSize.intValue() != -1) {
            sql3 = sql3 + "LIMIT {pagestart} ,{pageSize} ";
        }
        args.put("a", 0);
        args.put("b", 1);
        return this.dao2._queryBeanList(sql3, Task.class, args);
    }

    public List<Task> getSpotCheck3(Integer examnum, Integer gradeNum, Integer subjectnum, String questionNum, String updateUser, Integer snumber, Integer pagestart, Integer pageSize, String examPaperNum, String fenshuduan1, String fenshuduan2, String examineestatu) {
        Map args = new HashMap();
        args.put("examnum", examnum);
        args.put("subjectnum", subjectnum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("exampaperNum", examPaperNum);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("updateUser", updateUser);
        args.put("questionNum", questionNum);
        args.put("snumber", snumber);
        args.put("pagestart", pagestart);
        args.put("pageSize", pageSize);
        if (null == examPaperNum || "".equals(examPaperNum)) {
            this.dao2._queryStr("select exampaperNum from exampaper where examNum={examnum} and subjectNum={subjectnum} and  gradeNum ={gradeNum} ", args);
        }
        String examineesql = "";
        String examineeConditionsql = "";
        if (examineestatu != null && !"".equals(examineestatu) && !"-1".equals(examineestatu)) {
            examineesql = " left join examineesturecord eer on eer.scoreId=t.scoreId ";
            if ("T".equals(examineestatu)) {
                examineeConditionsql = " and eer.id is not null ";
            } else if ("F".equals(examineestatu)) {
                examineeConditionsql = " and eer.id is null ";
            }
        }
        String sql = " SELECT DISTINCT  t.scoreId,t.exampaperNum,t.questionNum,def.questionNum realQuestionNum FROM task t LEFT JOIN( SELECT ifnull(subdef.id,def.id) id ,ifnull(subdef.questionNum,def.questionNum) questionNum ,ifnull(subdef.questionType,def.questionType) questionType ,def.orderNum orderNum1,subdef.orderNum orderNum2  FROM define def LEFT JOIN subdefine subdef ON def.id = subdef.pid WHERE def.examPaperNum = {exampaperNum} )def ON t.questionNum = def.id  INNER JOIN score s  ON t.scoreId = s.id" + examineesql + " where s.examPaperNum= {exampaperNum}   and t.status = 'T'  and t.exampaperNum = {exampaperNum}  ";
        if (fenshuduan1 != "") {
            sql = sql + " AND s.questionScore>={fenshuduan1}  ";
        }
        if (fenshuduan2 != "") {
            sql = sql + " AND s.questionScore<={fenshuduan2} ";
        }
        if (null != updateUser && !updateUser.equals("-1")) {
            sql = sql + " AND t.insertUser={updateUser}  ";
        }
        if (null != questionNum && !"-1".equals(questionNum) && !"0".equals(questionNum)) {
            sql = sql + " AND t.questionNum={questionNum}  ";
        }
        String sql2 = sql + examineeConditionsql;
        if (snumber.intValue() == -1) {
            sql2 = sql2 + "ORDER BY def.orderNum1,def.orderNum2  ";
        }
        if (snumber.intValue() != -1) {
            sql2 = sql2 + "ORDER BY rand() LIMIT {snumber} ";
        } else if (pageSize.intValue() != -1) {
            sql2 = sql2 + "LIMIT {pagestart} ,{pageSize} ";
        }
        return this.dao2._queryBeanList(sql2, Task.class, args);
    }

    public List<Task> getSpotCheck2(Integer exampaperNum, String questionNum, Integer studentId, String scoreId) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("TRUE", (Object) "T").put("questionNum", (Object) questionNum).put("scoreId", (Object) scoreId);
        return this.dao2._queryBeanList("SELECT questionScore,userNum FROM task  WHERE  exampaperNum={exampaperNum} '  AND `status`={TRUE} AND questionNum={questionNum} AND scoreId={scoreId}  ORDER BY userNum", Task.class, args);
    }

    public List<Task> getSpotCheck2_detail(Integer exampaperNum, String questionNum, Integer studentId, String scoreId) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("TRUE", (Object) "T").put("questionNum", (Object) questionNum).put("scoreId", (Object) scoreId);
        return this.dao2._queryBeanList("SELECT t.questionScore,t.userNum,t.insertUser,u.username ext1,u.realname ext2 ,isException FROM task t   LEFT JOIN `user` u ON t.insertUser=u.id  WHERE  t.exampaperNum={exampaperNum}  AND t.`status`={TRUE} AND t.questionNum={questionNum} AND t.scoreId={scoreId} ", Task.class, args);
    }

    public List<Task> getQuesScoreTask(String exampaperNum, String questionNum, String insertUser, String scoreId) {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId).put("insertUser", (Object) insertUser);
        return this.dao2._queryBeanList("SELECT id,scoreId,`status`,questionScore FROM task WHERE scoreId={scoreId} AND insertUser={insertUser} ", Task.class, args);
    }

    public List<Task> getOneTwo(String examnum, String gradeNum, String subjectnum, String questionNum) {
        Map args = new HashMap();
        args.put("examnum", examnum);
        args.put("subjectnum", subjectnum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("questionNum", questionNum);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={examnum} and subjectNum={subjectnum} and  gradeNum ={gradeNum} ", args);
        args.put("exampaperNum", exampaperNum);
        return this.dao2._queryBeanList("\tselect * from questiongroup_question where groupNum in  ( select questionGroupNum from questiongroup_mark_setting where makType='1' and exampaperNum={exampaperNum} ) and exampaperNum={exampaperNum} and questionNum={questionNum} ", Task.class, args);
    }

    public List<Remark> getSpotC(String exampaperNum, String questionNum, String schoolNum, String classNum, String gradeNum, String userNum, String scoreId) {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put("exampaperNum", (Object) exampaperNum).put("questionNum", (Object) questionNum);
        return this.dao2._queryBeanList("select MAX(rownum) rownum from remark where userNum={userNum} and  exampaperNum={exampaperNum}  and questionNum={questionNum} and type='2' ", Remark.class, args);
    }

    public List<MarkError> getSpotC_MarkError(String exampaperNum, String questionNum, String schoolNum, String classNum, String gradeNum, String userNum, String scoreId) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        args.put("scoreId", scoreId);
        return this.dao2._queryBeanList("select id,scoreId,userNum,questionNum from markerror where userNum={userNum}  and  exampaperNum={exampaperNum}  and questionNum={questionNum} AND scoreId={scoreId} and type='2' ", MarkError.class, args);
    }

    public int yanz(String exampaperNum, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum, String type, String userNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("questionNum", (Object) questionNum).put(Const.EXPORTREPORT_studentId, (Object) studentId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("type", (Object) type).put("userNum", (Object) userNum);
        return this.dao2._queryBeanList("select * from remark where exampaperNum={exampaperNum} and questionNum={questionNum} and studentId={studentId} and schoolNum={schoolNum} and classNum={classNum} and gradeNum={gradeNum} and type={type} and userNum={userNum} ", Remark.class, args).size();
    }

    public int yanz1(Integer exampaperNum, String questionNum, String scoreId, Integer type, String userNum) {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put("scoreId", (Object) scoreId).put("exampaperNum", (Object) exampaperNum).put("questionNum", (Object) questionNum).put("type", (Object) type);
        return this.dao2._queryBeanList("select * from markerror   where userNum={userNum} AND scoreId={scoreId} AND exampaperNum={exampaperNum} AND questionNum={questionNum}  AND type={type} ", Remark.class, args).size();
    }

    public List<Define> getSpotCheckFullScore(Integer examnum, Integer gradeNum, Integer subjectnum, String questionNum) {
        Map args = StreamMap.create().put("questionNum", (Object) questionNum);
        return this.dao2._queryBeanList("select fullScore,questionNum  from define where  id={questionNum} union all  select fullScore,questionNum  from subdefine where id={questionNum} ", Define.class, args);
    }

    public List<Score> getSpotCheckScore(Integer examnum, Integer gradeNum, Integer subjectnum, String scoreId, String questionnum) {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId);
        return this.dao2._queryBeanList("select questionScore from score where id={scoreId} ", Score.class, args);
    }

    public List<ExamineeStuRecord> getExamineeRecord(Integer examnum, Integer gradeNum, Integer subjectnum, String scoreId, String questionnum) {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId);
        return this.dao2._queryBeanList("SELECT u.username ext1,s.status from(select userId,status from examineesturecord where scoreId={scoreId} )s LEFT join user u on s.userId=u.id", ExamineeStuRecord.class, args);
    }

    public Integer addExamineeRecord(ExamineeStuRecord examineeStuRecord) {
        Map args = new HashMap();
        args.put("ScoreId", examineeStuRecord.getScoreId());
        args.put("UserId", examineeStuRecord.getUserId());
        args.put("InsertDate", examineeStuRecord.getInsertDate());
        args.put("Status", examineeStuRecord.getStatus());
        if (null == this.dao2._queryObject("select /* shard_host_HG=Write */ id from examineesturecord where scoreId = {ScoreId} and status = 'T' limit 1", args)) {
            return Integer.valueOf(this.dao2._execute("insert into examineesturecord (scoreId,userId,insertDate,status) values ({ScoreId},{UserId},{InsertDate},{Status}) ON DUPLICATE KEY UPDATE scoreId={ScoreId},userId={UserId},insertDate={InsertDate},status={Status} ", args));
        }
        return 1;
    }

    public List<Remark> getSpotCheckRemark(Integer examnum, Integer gradeNum, Integer subjectnum, String scoreId, String questionnum) {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId);
        return this.dao2._queryBeanList("SELECT questionScore,`status` FROM remark WHERE scoreId={scoreId}  AND type='1' LIMIT 1 ", Remark.class, args);
    }

    public List<User> getSpotCheckChild(String exampaperNum, String questionnum, String scoreId) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("questionnum", (Object) questionnum).put("scoreId", (Object) scoreId);
        return this.dao2._queryBeanList("select a.realname username,b.*  from `user` a  LEFT JOIN (  select insertUser id,0 userType   from task  where scoreId={scoreId} AND insertUser<>'-1' AND `status`='T'   ) b ON a.id=b.id  WHERE b.id IS NOT NULL", User.class, args);
    }

    public List<QuestionGroup_question> getQuestionGroupScheDule(Integer examnum, Integer gradeNum, Integer subjectnum, Integer examPaperNum) {
        if (null != examPaperNum) {
            Map args = StreamMap.create().put("examnum", (Object) examnum).put("subjectnum", (Object) subjectnum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("examPaperNum", (Object) examPaperNum);
            return this.dao2._queryBeanList("SELECT COUNT(groupNum) ext1,COUNT(groupNum) ext2,SUM(IFNULL(totalNum,0)) ext3   FROM questiongroup WHERE exampaperNum={examPaperNum}    and stat='1' ", QuestionGroup_question.class, args);
        }
        return null;
    }

    public List<QuestionGroup_question> getQuestionGroupScheDule1(String examnum, String gradeNum, String subjectnum) {
        Map args = new HashMap();
        args.put("examnum", examnum);
        args.put("subjectnum", subjectnum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        if (this.dao2._queryObject("select exampaperNum from exampaper where examNum={examnum} and subjectNum={subjectnum} and  gradeNum ={gradeNum} ", args) != null) {
            String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={examnum} and subjectNum={subjectnum} and  gradeNum ={gradeNum} ", args);
            args.put("exampaperNum", exampaperNum);
            return this.dao2._queryBeanList("select DISTINCT  qp.exampaperNum,qp.groupNum  from questiongroup  qg LEFT JOIN questiongroup_question  qp ON qg.groupNum=qp.groupNum  and qg.exampaperNum=qp.exampaperNum where qg.exampaperNum={exampaperNum} and qp.exampaperNum={exampaperNum}  ORDER BY convert(qg.groupName using gbk)", QuestionGroup_question.class, args);
        }
        return null;
    }

    public List getQuestionGroupSD(Integer examnum, Integer gradeNum, Integer subjectnum) {
        String examPaperNum = getExampaperNumBySubjectAndGradeAndExam(examnum.toString(), subjectnum.toString(), gradeNum.toString());
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return this.dao2._queryBeanList(" SELECT t.*,t.ext3-t.ext4 ext5,convert(if(t.ext3=0,0,(t.ext4/t.ext3)*100)  ,decimal(10,2)) ext6 from(  SELECT distinct q.groupName,q.groupNum,q.groupType,z.makType,z.exampaperNum,o.ext11, case z.makType when 1 then IFNULL(r.count ,0)*2 else IFNULL(r.count ,0) end ext1,    case    when d.choosename<>'s' then IFNULL(tt.count,0)    when d.choosename='s' and z.makType=1 then IFNULL(r.count*2,0)+IFNULL(x.count,0)    when d.choosename='s' and z.makType=0 then IFNULL(r.count,0)     end ext3,d.choosename ext10,IFNULL(y.d_num ,0) ext4,IFNULL(e.caiqie,0) ext12,IFNULL(e.yicaiqie,0) ext7,IFNULL(e.chongpan,0) ext8, IFNULL(x.count,0) ext9,   IF(t.groupNum is NULL ,'T','F') questionName FROM(  \t\t\tSELECT s.groupNum,s.exampaperNum,m.makType FROM(  \t\t\t\t\t\tSELECT groupNum,exampaperNum FROM questiongroup WHERE exampaperNum={examPaperNum}  \t\t\t) s  LEFT JOIN (  \t\t\t\t\t\tSELECT groupNum,makType FROM questiongroup_mark_setting WHERE exampaperNum={examPaperNum}  \t\t\t) m ON m.groupNum=s.groupNum   LEFT JOIN (  \t\t\t\t\t\tselect groupNum,count(1) count from questiongroup_question where exampaperNum={examPaperNum}  GROUP BY groupNum  \t\t\t)d on s.groupNum=d.groupNum  LEFT JOIN (  \t\t\t\t\t\tSELECT groupNum,makType from questiongroup_mark_setting where exampaperNum={examPaperNum}  \t\t\t)qe on s.groupNum=qe.groupNum  )z    LEFT JOIN (  \t\t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num,count(DISTINCT(questionNum)) as tempcount   \t\t\tFROM task   WHERE exampaperNum={examPaperNum}  AND status='T'   GROUP BY groupNum   )y ON z.groupNum = y.groupNum LEFT JOIN (  \t\t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as count FROM task   WHERE exampaperNum={examPaperNum}  and userNum=3  GROUP BY groupNum   )x ON z.groupNum = x.groupNum  LEFT JOIN(  \t\t\tSELECT q.groupNum,ifnull(r.count,0) yicaiqie,ifnull(r1.count1,0) caiqie,ifnull(r2.count2,0) chongpan   FROM(   \t\t\t\tSELECT examPaperNum,groupNum,questionNum FROM questiongroup_question WHERE exampaperNum={examPaperNum}  GROUP BY groupNum       \t\t\t) q  LEFT JOIN (   \t\t\t\tSELECT questionNum,count(id) count FROM remark WHERE exampaperNum={examPaperNum}  AND status='T' AND type='1' and source=1 GROUP BY questionNum \t\t\t) r ON q.questionNum=r.questionNum LEFT JOIN (   \t\t\t\tSELECT questionNum,count(id) count1 FROM remark WHERE exampaperNum={examPaperNum}  AND type='1' and source=1 GROUP BY questionNum \t\t\t) r1 ON q.questionNum=r1.questionNum LEFT JOIN (   \t\t\t\tSELECT questionNum,count(id) count2 FROM markerror WHERE exampaperNum={examPaperNum}  AND type='2' GROUP BY questionNum      \t\t\t) r2 ON q.questionNum=r2.questionNum       )e ON e.groupNum=z.groupNum     LEFT JOIN questiongroup q ON q.groupNum=z.groupNum    LEFT JOIN ( SELECT id,choosename from define WHERE exampaperNum={examPaperNum}    UNION     SELECT sb.id,d.choosename from define d  LEFT JOIN subdefine sb on sb.pid=d.id  WHERE d.exampaperNum={examPaperNum} ) d on q.groupNum=d.id    LEFT JOIN (  \t\tselect d.id,r.dd count from( \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d INNER JOIN exampaper e on d.category=e.examPaperNum where d.examPaperNum={examPaperNum}  and d.questionType=1  \t\t\tUNION \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from subdefine d INNER JOIN exampaper e on d.category=e.examPaperNum where d.examPaperNum={examPaperNum}  and d.questionType=1 \t\t)d LEFT JOIN(  \t\t\tSELECT count(DISTINCT studentId) dd,exampapernum ext1 from regexaminee WHERE exampaperNum={examPaperNum}   and scan_import=0 \t\t\tunion \t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampapernum from regexaminee r \t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum \t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE r.exampaperNum={examPaperNum}   and r.scan_import=0 \t\t\tUNION\t \t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampaperNum from regexaminee r LEFT JOIN( \t\t\t\tSELECT r.studentId,r.exampapernum from regexaminee r \t\t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum \t\t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE r.exampaperNum={examPaperNum}   and r.scan_import=0 \t\t\t)r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum \t\t\tINNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3 \t\t\tWHERE r.exampaperNum={examPaperNum}   and r.scan_import=0 and r1.studentId is null \t\t)r on d.category=r.ext1  ) r on q.groupNum=r.id left join(SELECT g.id groupNum,if(f.name is null,g.id,f.name) ext11 from( \tSELECT d.id,d.questionNum,if(d.choosename='s',d.id,d.choosename) num \t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id \t\tWHERE d.examPaperNum={examPaperNum}  and d.questiontype=1 \t\tunion \t\tSELECT sb.id,sb.questionNum,CONCAT(d1.id,sb.orderNum) num \t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id \t\tWHERE sb.examPaperNum ={examPaperNum}  and sb.questiontype=1 )g LEFT JOIN( \tSELECT GROUP_CONCAT(d.id SEPARATOR '_') name,GROUP_CONCAT(d.questionNum),d.num from( \t\tSELECT d.id,d.questionNum,d.choosename num \t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id \t\tWHERE d.examPaperNum={examPaperNum}  and d.questiontype=1 \t\tunion \t\tSELECT sb.id,sb.questionNum,CONCAT(d1.id,sb.orderNum) num \t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id \t\tWHERE sb.examPaperNum ={examPaperNum}  and sb.questiontype=1 \t) d GROUP BY d.num )f on g.num=f.num) o on o.groupNum=q.groupNum LEFT JOIN ( \t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed) count from task where examPaperNum={examPaperNum} GROUP BY groupNum )tt on q.groupNum=tt.groupNum   LEFT JOIN (  \t\tselect  DISTINCT q.groupNum FROM (  \t\t\t\t\t\tSELECT groupNum,questionNum FROM questiongroup_question WHERE exampaperNum = {examPaperNum}  GROUP BY groupNum   \t\t)q LEFT JOIN (  \t\t\t\t\t\tselect DISTINCT questionNum  FROM remark where type='1' AND STATUS='F'  and source=1 AND exampaperNum={examPaperNum}  \t\t) tt ON q.questionNum = tt.questionNum where tt.questionNum is not null  )t  ON z.groupNum = t.groupNum WHERE z.groupNum IS NOT NULL     ORDER BY CASE  WHEN LOCATE('-',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('-' IN q.groupName)-1),'.01')*1   ELSE CONCAT(q.groupName,'.1')*1 END  ASC ,REPLACE(q.groupName,'_','.')*1  )t", QuestionGroup_question.class, args);
    }

    public List jiancha(Integer examnum, Integer gradeNum, Integer subjectnum, Integer examPaperNum) {
        if (examPaperNum != null) {
            Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("TRUE", (Object) "T");
            return this.dao2._queryBeanList("SELECT count(1) ext1  FROM  task   WHERE exampaperNum={examPaperNum} AND status={TRUE}   ", Score.class, args);
        }
        return null;
    }

    public Object getGroupNameBynum(String groupNum) {
        Map args = StreamMap.create().put("groupNum", (Object) groupNum);
        return this.dao2._queryStr("select groupName from questiongroup where groupNum={groupNum} ", args);
    }

    public List<QuestionGroup_question> getGroupSize(String exampaperNum, String groupNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) groupNum);
        return this.dao2._queryBeanList("SELECT exampaperNum,groupNum,questionNum from questiongroup_question   WHERE exampaperNum={exampaperNum} AND groupNum={groupNum} ", QuestionGroup_question.class, args);
    }

    public List<QuestionGroup_question> getQuesTask(Integer examnum, Integer gradeNum, Integer subjectnum, Integer examPaperNum) {
        if (examPaperNum != null) {
            Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("TRUE", (Object) "T");
            return this.dao2._queryBeanList("select sum(d_num) ext1 from(select cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num    from  task   WHERE exampaperNum={examPaperNum} and  `status`={TRUE}  GROUP BY groupNum )d", QuestionGroup_question.class, args);
        }
        return null;
    }

    public Object getStudentSize(String examPaperNum, String questionNum, String schoolNum, String gradeNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryStr("select count(1) from task where  examPaperNum={examPaperNum} and questionNum={questionNum} and schoolNum={schoolNum} and gradeNum={gradeNum} and status='T'", args);
    }

    public List<Questiongroup_mark_setting> getMarkTypeSche(String exampaperNum, String questionGroupNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("questionGroupNum", (Object) questionGroupNum);
        return this.dao2._queryBeanList("select * from questiongroup_mark_setting where exampaperNum={exampaperNum} ' and questionGroupNum={questionGroupNum} ", Questiongroup_mark_setting.class, args);
    }

    public Object getPaperNum(String examPaperNum, String questionNum, String schoolNum, String gradeNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryStr("select count(1) from score where examPaperNum={examPaperNum} and questionNum={questionNum} and schoolNum={schoolNum} and gradeNum={gradeNum} ", args);
    }

    public Object getRemarkNum(String examPaperNum, String questionNum, String schoolNum, String gradeNum, int type) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("type", (Object) Integer.valueOf(type));
        return this.dao2._queryStr("select count(1) from remark where exampaperNum={examPaperNum} and questionNum={questionNum} and schoolNum={schoolNum} and gradeNum={gradeNum} and type={type} ", args);
    }

    public List<Exam> getExamNameByNum(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryBeanList("select e.examDate,d.name examName from exam e,`data` d where d.`value`=e.examType and d.type='1' and examNum={examNum} ", Exam.class, args);
    }

    public Object getSchoolNameByNum(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryStr("select  schoolName from school where id={schoolNum} ", args);
    }

    public List<Subject> getSubjectNameByNum(String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryBeanList("select subjectName,type from `subject` where subjectNum={subjectNum} ", Subject.class, args);
    }

    public Object getClassNameByNum(String classNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryStr("select className from class where id={classNum} and gradeNum={gradeNum} ", args);
    }

    public Object getGradeNameByNum(String schoolNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryStr("select gradeName from grade where schoolNum={schoolNum} and gradeNum={gradeNum} ", args);
    }

    public List<Class> gets_pclassNum(String schoolNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryBeanList("select * from class where schoolNum={schoolNum}  and gradeNum={gradeNum} ORDER BY classNum", Class.class, args);
    }

    /* JADX WARN: Code restructure failed: missing block: B:53:0x01e7, code lost:
    
        if (r0.size() > 0) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x01f1, code lost:
    
        if (r21.getMoreResults() == false) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x01f4, code lost:
    
        r22 = r21.getResultSet();
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0204, code lost:
    
        if (r22.next() == false) goto L87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0207, code lost:
    
        r0 = new java.util.ArrayList();
        r27 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0223, code lost:
    
        if (r27 >= (r22.getMetaData().getColumnCount() + 1)) goto L88;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x0229, code lost:
    
        if (r27 <= 1) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x022c, code lost:
    
        r0.add(cn.hutool.core.convert.Convert.toInt(r22.getString(r27).toString()));
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x0257, code lost:
    
        r27 = r27 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0246, code lost:
    
        r0.add(r22.getString(r27));
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x025d, code lost:
    
        r0.add(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x026a, code lost:
    
        r0.add(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x0276, code lost:
    
        com.zht.db.DbUtils.close(r22, r21, r23);
     */
    /* JADX WARN: Finally extract failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List gets_pques(java.lang.String r7, java.lang.String r8, java.lang.String r9, java.lang.String r10, java.lang.String r11, java.lang.String r12, java.lang.String r13, java.lang.String r14, float r15, java.lang.String r16, java.lang.String r17) {
        /*
            Method dump skipped, instructions count: 674
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.dmj.daoimpl.examManagement.ExamDAOImpl.gets_pques(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, float, java.lang.String, java.lang.String):java.util.List");
    }

    public List<Student> getStudent(String StudentId, String schoolNum, String gradeNum, String classNum) {
        Map args = StreamMap.create().put("StudentId", (Object) StudentId);
        return this.dao2._queryBeanList("select studentId,studentName,id from student where studentId={StudentId} ", Student.class, args);
    }

    public List getCorrectList(Score s) {
        int examPaperNum = s.getExamPaperNum().intValue();
        String testCenter = s.getTestingCentreId();
        double score = s.getQuestionScore().doubleValue();
        String examinationRoom = s.getExaminationRoomNum();
        String studentId = s.getStudentId();
        s.getPage();
        String correctscorestatus = s.getIsModify();
        String sql = "SELECT sco.id id ,regId,sco.questionNum questionNum ,sco.studentID studentId,sco.isModify isModify ,sco.isException isException,sco.page page,r.cNum,r.type FROM (SELECT  regId,id,questionNum,studentID,examPaperNum ,isModify,isException,page  FROM score  WHERE  examPaperNum ={examPaperNum} ";
        if (!"-1".equals(examinationRoom)) {
            sql = sql + " and examinationRoomNum= {examinationRoom}  ";
        }
        String sql2 = sql + " and isAppend = 'F'  and isException in(-1,4,2,11,14,15,10,12) ";
        if (null != studentId) {
            sql2 = sql2 + "and studentId= {studentId} ";
        }
        if (null != correctscorestatus && correctscorestatus.equals("1")) {
            sql2 = sql2 + "and  isModify= 'F' ";
        }
        if (null != correctscorestatus && correctscorestatus.equals("2")) {
            sql2 = sql2 + "and ( isModify= 'T' or isModify= '1' ) ";
        }
        String sql3 = sql2 + "and testingCentreId={testCenter}  AND  ABS(regScore-{score})<0.00001  ) sco    LEFT JOIN (select studentId,page,cNum,type,id from regexaminee where exampaperNum={examPaperNum} and testingCentreId={testCenter}) r ON  sco.regId = r.id    left join ( select  d.questionNum,d.id,d.page from (\t\tselect questionNum,id,page from define where  exampaperNum={examPaperNum}  union all  \tselect questionNum,id,page from subdefine where  exampaperNum={examPaperNum} )  d ) d on d.id=sco.questionNum and d.page = sco.page   where d.id = sco.questionNum   order by isModify ";
        Map args = StreamMap.create().put("examPaperNum", (Object) Integer.valueOf(examPaperNum)).put("examinationRoom", (Object) examinationRoom).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("testCenter", (Object) testCenter).put("score", (Object) Double.valueOf(score));
        this.log.info("get correct img list  sql: " + sql3);
        return this.dao2._queryBeanList(sql3, CorrectInfo.class, args);
    }

    public Integer getCorrectListCount(Score s) {
        int examPaperNum = s.getExamPaperNum().intValue();
        int school = s.getSchoolNum().intValue();
        double score = s.getQuestionScore().doubleValue();
        String examinationRoom = s.getExaminationRoomNum();
        String studentId = s.getStudentId();
        s.getPage();
        String correctscorestatus = s.getIsModify();
        String sql = "SELECT count(1) FROM (SELECT  regId,id,questionNum,studentID,examPaperNum ,isModify,isException,page  FROM score  WHERE  examPaperNum ={examPaperNum} ";
        if (!"-1".equals(examinationRoom)) {
            sql = sql + " and examinationRoomNum= {examinationRoom}  ";
        }
        String sql2 = sql + " and isAppend = 'F'  and (isException = {SCORE_EXCEPTION_NORECOGNIZED}  or isException = {SCORE_EXCEPTION_DAFAULT} or  isException = {SCORE_EXCEPTION_UPFULLMARKS} )  ";
        if (null != studentId) {
            sql2 = sql2 + "and studentId= {studentId}  and ";
        }
        if (null != correctscorestatus && correctscorestatus.equals("1")) {
            sql2 = sql2 + "and  isModify= 'F' ";
        }
        if (null != correctscorestatus && correctscorestatus.equals("2")) {
            sql2 = sql2 + "and ( isModify= 'T' or isModify= '1' ) ";
        }
        String sql3 = sql2 + "and schoolNum={school}  AND  ABS(regScore-{score} )<0.00001  ) sco    LEFT JOIN (select studentId,page,cNum,type,id from regexaminee where exampaperNum={examPaperNum} and schoolNum={school} ) r ON  sco.regId = r.id    left join ( select  d.questionNum,d.id,d.page  from (select questionNum,id,page from define where  exampaperNum={examPaperNum} union all select questionNum,id,page from subdefine where  exampaperNum={examPaperNum} )  d ) d on d.id=sco.questionNum and d.page = sco.page   where d.questionNum is not null    order by null ";
        this.log.info("get correct img list  sql: " + sql3);
        Map args = new HashMap();
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("examinationRoom", examinationRoom);
        args.put("SCORE_EXCEPTION_NORECOGNIZED", "2");
        args.put("SCORE_EXCEPTION_DAFAULT", "-1");
        args.put("SCORE_EXCEPTION_UPFULLMARKS", "4");
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(License.SCHOOL, Integer.valueOf(school));
        args.put("score", Double.valueOf(score));
        Object object = this.dao2._queryObject(sql3, args);
        return Integer.valueOf(Integer.parseInt(String.valueOf(object)));
    }

    public List getScoreList(String exam, String subject, String testCenter, String grade, String examinationRoom, String studentId, String page, String correctscorestatus) {
        if (null == exam || null == testCenter) {
            return null;
        }
        String epNum = getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
        String sql = " select distinct regScore from score where exampaperNum={epNum} ";
        if (null != examinationRoom && !examinationRoom.equals("-1") && !"undefined".equals(examinationRoom) && !"".equals(examinationRoom)) {
            sql = sql + " and  examinationRoomNum= {examinationRoom}  ";
        }
        String sql2 = sql + " and testingCentreId = {testCenter} and isAppend = 'F' and isException in(-1,4,2,11,14,15,10,12)  ";
        if (null != studentId && !studentId.equals("")) {
            sql2 = sql2 + " and  studentId= {studentId} ";
        }
        if (null != correctscorestatus && correctscorestatus.equals("1")) {
            sql2 = sql2 + " and  isModify= 'F'";
        }
        if (null != correctscorestatus && correctscorestatus.equals("2")) {
            sql2 = sql2 + " and ( isModify= 'T' or isModify= '1' )";
        }
        String sql3 = sql2 + " order by regScore";
        this.log.info("[" + getClass().getSimpleName() + "] : getScoreList()  sql: " + sql3);
        Map args = StreamMap.create().put("epNum", (Object) epNum).put("examinationRoom", (Object) examinationRoom).put("testCenter", (Object) testCenter).put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryColList(sql3, args);
    }

    public Integer updateScore(String examPaperNum, String studentID, String questionNum, String score) {
        String realScore = getQuestionScoreByQuestionInfo(null == examPaperNum ? 0 : Integer.parseInt(examPaperNum), questionNum, score);
        Map args = StreamMap.create().put("realScore", (Object) realScore).put("score", (Object) score).put("questionNum", (Object) questionNum).put("studentID", (Object) studentID).put("examPaperNum", (Object) examPaperNum);
        return Integer.valueOf(this.dao2._execute("UPDATE score  SET questionScore = {realScore},regScore={score} , isModify = 'T'  WHERE questionNum = {questionNum} AND studentID={studentID} AND examPaperNum={examPaperNum} ", args));
    }

    public String getQuestionScoreByQuestionInfo(int epNum, String questionNum, String score) {
        Define d = getDefineByExampaperNumAndQuestionNum(epNum, questionNum);
        if (null == d) {
            return "0";
        }
        String realScore = score;
        if (d.getQuestionType().equals("1")) {
            String rs = getScoreModelByExam(String.valueOf(epNum), "22");
            if (null == rs || rs.equals("")) {
                rs = Const.score_plus;
            }
            if (rs.equals(Const.score_reduces)) {
                Float rscore = Float.valueOf(d.getFullScore().floatValue() - Float.valueOf(score).floatValue());
                realScore = Util.round(rscore, 1).toString();
            }
        }
        return realScore;
    }

    public List getCorrectExaminationRoomStudent(String exam, String testCenter, String grade, String examRoomNum, String exampaperNum, String examroomornot, String subject) {
        Map args = new HashMap();
        args.put("examRoomNum", examRoomNum);
        args.put("exampaperNum", exampaperNum);
        args.put("exam", exam);
        args.put("testCenter", testCenter);
        args.put("grade", grade);
        args.put("subject", subject);
        String examroomNumStr = "";
        if (null != examRoomNum && !"".equals(examRoomNum) && "0".equals(examroomornot)) {
            examroomNumStr = " AND examinationRoomNum = {examRoomNum} ";
        }
        Object obj = this.dao2._queryObject("SELECT totalPage FROM exampaper WHERE examPaperNum={exampaperNum} ", args);
        int page = null == obj ? 0 : Integer.parseInt(obj.toString());
        args.put("page", Integer.valueOf(page));
        String sql = "SELECT DISTINCT sd.studentid num,CONCAT(ssd.studentName,'-',sd.examineeNum) name,COUNT(DISTINCT page)   FROM \t (SELECT e.examinationRoomNum,e.examineeNum,e.studentId FROM examinationnum e  WHERE e.examNum={exam}  AND e.testingCentreId = {testCenter}   AND e.gradeNum={grade} AND e.subjectNum ={subject} " + examroomNumStr + " ) sd    LEFT JOIN (  SELECT r.examPaperNum,r.studentId,r.page   FROM (SELECT examPaperNum,studentId,page,Id FROM regexaminee WHERE examPaperNum={exampaperNum} " + examroomNumStr + ") r LEFT JOIN (SELECT regId FROM cantrecognized WHERE exampaperNum={exampaperNum} " + examroomNumStr + " )  c   ON   c.regId=r.Id \tWHERE c.regId IS NULL ) s ON sd.studentid = s.studentid AND exampaperNum = {exampaperNum}  LEFT JOIN student ssd ON ssd.id = sd.studentid   GROUP BY sd.studentid HAVING COUNT(DISTINCT page)   < {page}  order by  sd.examineeNum";
        this.log.info("getCorrectExaminationRoomStudent  sql: " + sql);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectClassStudent(String exam, String school, String grade, String classNum, String exampaperNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum).put("grade", (Object) grade).put("exam", (Object) exam).put(License.SCHOOL, (Object) school);
        this.log.info("getCorrectClassStudent  sql:  SELECT r.studentId num,st.studentName name  FROM \t( SELECT st.studentId FROM studentlevel st where st.classNum={classNum} AND st.gradeNum={grade} AND st.examNum={exam}  AND schoolNum={school} ) r LEFT JOIN student st ON st.id = r.studentId");
        return this.dao2._queryBeanList(" SELECT r.studentId num,st.studentName name  FROM \t( SELECT st.studentId FROM studentlevel st where st.classNum={classNum} AND st.gradeNum={grade} AND st.examNum={exam}  AND schoolNum={school} ) r LEFT JOIN student st ON st.id = r.studentId", AjaxData.class, args);
    }

    public List getCorrectClassList_online(String exam, String school, String subject, String grade, String stat, String type, String testCenter) {
        String exm = "";
        String tc = "";
        String sc = "";
        String sub = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exm = " AND examNum = {exam} ";
        }
        if (null != testCenter && !testCenter.equals("")) {
            tc = " AND r.testingCentreId={testCenter} ";
        }
        if (null != school && !school.equals("") && !school.equals("-1")) {
            sc = " AND r.schoolNum={school} ";
        }
        if (null != subject && !subject.equals("") && !subject.equals("-1")) {
            sub = " AND subjectNum = {subject} ";
        }
        String sql = "SELECT c.id num,c.className name   \tFROM   \t\t(   \t\t\t\tSELECT DISTINCT r.classNum,e.gradeNum,r.schoolNum,e.jie  \t\tFROM   \t\t\t\t(SELECT gradeNum,examPaperNum,jie FROM exampaper WHERE 1=1 " + exm + sub + "   AND gradeNum={grade} ) e   \t\t\t\tLEFT JOIN regexaminee r ON e.examPaperNum=r.examPaperNum   \t\t\t\tWHERE 1=1 " + sc + tc + "\t\t\t)r   \t\tLEFT JOIN class c ON  c.id=r.classNum       where c.className is not null  ORDER BY length(c.className),convert(c.className using gbk)  ";
        this.log.info("get getCorrectClassList list sql: " + sql);
        Map args = StreamMap.create().put("exam", (Object) exam).put("testCenter", (Object) testCenter).put(License.SCHOOL, (Object) school).put("subject", (Object) subject).put("grade", (Object) grade);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getClassList_online(String exam, String school, String subject, String grade, String stat, String type, String subjectType) {
        String exm = "";
        String sc = "";
        String sub = "";
        String st = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exm = " AND examNum = {exam} ";
        }
        if (null != school && !school.equals("") && !school.equals("-1")) {
            sc = " AND r.schoolNum={school} ";
        }
        if (null != subject && !subject.equals("") && !subject.equals("-1")) {
            sub = " AND subjectNum = {subject} ";
        }
        if (null != subjectType && !subjectType.equals("") && !subjectType.equals("-1")) {
            st = " AND c.studentType = {subjectType} ";
        }
        String sql = "SELECT c.id num,c.className name   FROM  \t(   \t\t\t\tSELECT DISTINCT r.classNum,e.gradeNum,r.schoolNum,e.jie   \tFROM   \t\t\t\t(SELECT gradeNum,examPaperNum,jie FROM exampaper WHERE 1=1 " + exm + sub + "   AND gradeNum={grade} AND type={exam_type_online} ) e   \t\t\t\tLEFT JOIN regexaminee r ON e.examPaperNum=r.examPaperNum   \t\t\t\tWHERE 1=1 " + sc + "\t\t\t)r   \t\tLEFT JOIN class c ON  c.id=r.classNum      where c.className is not null " + st + " ORDER BY length(c.className),convert(c.className using gbk)  ";
        this.log.info("get getCorrectClassList list sql: " + sql);
        Map args = StreamMap.create().put("exam", (Object) exam).put(License.SCHOOL, (Object) school).put("subject", (Object) subject).put("subjectType", (Object) subjectType).put("grade", (Object) grade).put("exam_type_online", (Object) "0");
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectClassList(String exam, String school, String subject, String grade, String stat, String type, String testCenter) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " and " + type + "  = {stat}  ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + "numStatus = {STATUS_CORRECT_COMPLETE}  and ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = " AND subjectNum = {subject} ";
        }
        String str2 = "";
        if (null != school && !school.equals("")) {
            str2 = " schoolNum = {school} ";
        }
        if (null != testCenter && !testCenter.equals("")) {
            str2 = " testingCentreId = {testCenter} ";
        }
        String sql = "SELECT b.id num,b.className name FROM   \t( \t\tSELECT DISTINCT s.classNum classNum,examroom.gradeNum ,examroom.jie \t\tFROM  \t\t\t(       \tSELECT DISTINCT em.id num,c.gradeNum,e.jie \t\t\t\tFROM  \t\t\t\t\t(SELECT gradeNum,examPaperNum,examnitionRoom FROM correctstatus WHERE " + str2 + str + sub + " AND gradeNum = {grade} AND  examNum = {exam} ) c \t\t\t\tleft JOIN exampaper e ON c.examPaperNum = e.examPaperNum \t\t\t\tleft JOIN examinationroom em ON em.id = c.examnitionRoom\t\t\t) examroom   \t\tLEFT JOIN\t\texaminationnum  e  ON examroom.num = e.examinationRoomNum        \t\tLEFT JOIN\t\tstudent s ON s.id = e.studentid \t) g LEFT JOIN class b ON b.id = g.classNum    where b.className is not null  ORDER BY length(b.className),convert(b.className using gbk) ";
        this.log.info("get getCorrectClassList list sql: " + sql);
        Map args = new HashMap();
        args.put("stat", stat);
        args.put("STATUS_CORRECT_COMPLETE", "2");
        args.put("subject", subject);
        args.put(License.SCHOOL, school);
        args.put("testCenter", testCenter);
        args.put("grade", grade);
        args.put("exam", exam);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectExaminationRoomList(String exam, String school, String subject, String grade, String stat, String type, String testCenter) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " and " + type + "  = {stat} ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + " and numStatus = {STATUS_CORRECT_COMPLETE} ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = " AND subjectNum = {subject} ";
        }
        String str2 = "";
        if (null != school && !school.equals("")) {
            str2 = " AND schoolNum = {school} ";
        }
        if (null != testCenter && !testCenter.equals("")) {
            str2 = " AND testingCentreId = {testCenter} ";
        }
        String sql = "SELECT DISTINCT em.id num ,em.examinationRoomName name  FROM  \t(SELECT examnitionRoom FROM correctstatus WHERE  examNum = {exam} " + str + sub + " AND gradeNum = {grade} " + str2 + " ) c left JOIN examinationroom em ON em.id = c.examnitionRoom order by em.examinationRoomNum";
        this.log.info("get examroom list sql: " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("subject", (Object) subject).put(License.SCHOOL, (Object) school).put("testCenter", (Object) testCenter).put("exam", (Object) exam).put("grade", (Object) grade);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectExaminationRoomList_ep(String exam, String school, String subject, String grade, String stat, String type, String examplace) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " and " + type + "  = {stat} ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + " and numStatus = {STATUS_CORRECT_COMPLETE} ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = " AND subjectNum = {subject} ";
        }
        String epstring = "";
        if (!examplace.equals("-1")) {
            epstring = " AND testingCentreId = {examplace} ";
        }
        String sql = "SELECT DISTINCT em.id num ,em.examinationRoomName name  FROM  \t(SELECT examnitionRoom FROM correctstatus WHERE  examNum = {exam}" + str + sub + epstring + " AND gradeNum = {grade} ) c left JOIN examinationroom em ON em.id = c.examnitionRoom order by em.examinationRoomNum";
        this.log.info("get examroom list sql: " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("subject", (Object) subject).put("examplace", (Object) examplace).put("exam", (Object) exam).put("grade", (Object) grade);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectAuthExaminationRoomList_ep(String exam, String school, String subject, String grade, String stat, String type, String examplace, String userId) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " and " + type + "  = {stat} ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + " and numStatus = {STATUS_CORRECT_COMPLETE} ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = " AND subjectNum = {subject} ";
        }
        String sql = "SELECT DISTINCT em.id num ,em.examinationRoomName name FROM (SELECT examnitionRoom,testingCentreId FROM correctstatus WHERE examNum = {exam} " + str + sub + " AND gradeNum ={grade} ) c left join scanpermission scanp on scanp.testingCentreId = c.testingCentreId left JOIN examinationroom em ON em.id = c.examnitionRoom where scanp.userNum = {userId}  order by em.examinationRoomNum";
        this.log.info("get authexamroom list sql: " + sql);
        Map args = new HashMap();
        args.put("stat", stat);
        args.put("STATUS_CORRECT_COMPLETE", "2");
        args.put("subject", subject);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("userId", userId);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectExaminationRoomList_zy(String exam, String school, String subject, String grade, String stat, String type) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " and " + type + "  = {stat} ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + " and numStatus = {STATUS_CORRECT_COMPLETE} ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = " AND subjectNum = {subject} ";
        }
        String sql = "SELECT DISTINCT em.id num ,em.examinationRoomName name  FROM  \t(SELECT examnitionRoom FROM correctstatus WHERE  examNum = {exam} " + str + sub + " AND gradeNum = {grade} AND schoolNum = {school} ) c left JOIN examinationroom em ON em.id = c.examnitionRoom order by em.examinationRoomNum";
        this.log.info("get examroom list sql: " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("subject", (Object) subject).put("exam", (Object) exam).put("grade", (Object) grade).put(License.SCHOOL, (Object) school);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectExaminationRoomList_online(String exam, String school, String subject, String grade, String stat, String type) {
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = " AND subjectNum = {subject} ";
        }
        String sql = "SELECT DISTINCT  c.id num,c.examinationRoomName name  \tFROM   \t(  \t\t\t\tSELECT DISTINCT r.classNum,e.gradeNum,r.schoolNum,e.jie,r.examinationRoomNum   \t\t\t\tFROM   \t\t\t\t(SELECT examPaperNum,gradeNum,jie FROM exampaper WHERE examNum={exam} " + sub + "   AND gradeNum={grade} AND type={type} ) e   \t\t\t\tLEFT JOIN regexaminee r ON e.examPaperNum=r.examPaperNum   \t\t\t\tWHERE r.schoolNum={school}\t)r  \t\tLEFT JOIN examinationroom c ON c.id=r.examinationRoomNum   where c.id is not null  order by c.examinationRoomNum ";
        Map args = StreamMap.create().put("subject", (Object) subject).put("exam", (Object) exam).put("grade", (Object) grade).put("type", (Object) type).put(License.SCHOOL, (Object) school);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectExaminationRoomList_online_zy(String exam, String school, String subject, String grade, String stat, String type, String testCenter) {
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = " AND subjectNum = {subject} ";
        }
        String str = "";
        String str2 = "";
        if (null != school && !school.equals("")) {
            str = " WHERE r.schoolNum={school} ";
        }
        if (null != testCenter && !testCenter.equals("")) {
            str = " WHERE r.testingCentreId={testCenter} ";
            str2 = "AND c.testingCentreId ={testCenter} ";
        }
        String sql = "SELECT DISTINCT  c.id num,c.examinationRoomName name  FROM   \t(  \t\t\t\tSELECT DISTINCT r.classNum,e.gradeNum,r.schoolNum,e.jie,r.examinationRoomNum   \t\t\t\tFROM   \t\t\t\t(SELECT examPaperNum,gradeNum,jie FROM exampaper WHERE examNum={exam} " + sub + "   AND gradeNum={grade}) e   \t\t\t\tLEFT JOIN regexaminee r ON e.examPaperNum=r.examPaperNum   " + str + "\t\t\t)r  \t\tLEFT JOIN examinationroom c ON c.id=r.examinationRoomNum   where c.id is not null " + str2 + " order by c.examinationRoomNum ";
        Map args = StreamMap.create().put("subject", (Object) subject).put(License.SCHOOL, (Object) school).put("testCenter", (Object) testCenter).put("exam", (Object) exam).put("grade", (Object) grade);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectExaminationRoomList_online_ep(String exam, String school, String subject, String grade, String stat, String type, String examplace) {
        String sub = "";
        String epstring = "";
        if (null != subject && !subject.equals("")) {
            sub = " AND subjectNum = {subject} ";
        }
        if (!examplace.equals("-1")) {
            epstring = " AND r.testingCentreId = {examplace}  ";
        }
        String sql = "SELECT DISTINCT  c.id num,c.examinationRoomName name  \t\tFROM   \t\t(  \t\t\t\tSELECT DISTINCT r.classNum,e.gradeNum,r.schoolNum,e.jie,r.examinationRoomNum,r.testingCentreId \t\t\t\tFROM   \t\t\t\t(SELECT examPaperNum,gradeNum,jie FROM exampaper WHERE examNum={exam} " + sub + "   AND gradeNum={grade}) e   \t\t\t\tLEFT JOIN regexaminee r ON e.examPaperNum=r.examPaperNum   \t\t\t)r  \t\tLEFT JOIN examinationroom c ON c.id=r.examinationRoomNum   where c.id is not null " + epstring + " order by c.examinationRoomNum ";
        Map args = StreamMap.create().put("subject", (Object) subject).put("examplace", (Object) examplace).put("exam", (Object) exam).put("grade", (Object) grade);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectAuthExaminationRoomList_online_ep(String exam, String school, String subject, String grade, String stat, String userId, String examplace) {
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = " AND subjectNum = {subject} ";
        }
        String sql = "SELECT DISTINCT  c.id num,c.examinationRoomName name FROM (SELECT DISTINCT r.classNum,e.gradeNum,r.schoolNum,e.jie,r.examinationRoomNum,r.testingCentreId FROM \t(SELECT examPaperNum,gradeNum,jie FROM exampaper WHERE examNum={exam} " + sub + " AND gradeNum={grade} ) e \tLEFT JOIN regexaminee r ON e.examPaperNum=r.examPaperNum )r LEFT JOIN scanpermission scanp on scanp.testingCentreId = r.testingCentreId LEFT JOIN examinationroom c ON c.id=r.examinationRoomNum where scanp.userNum = {userId} and c.id is not null order by c.examinationRoomNum ";
        this.log.info(getClass().getSimpleName() + "[getCorrectAuthExaminationRoomList_online_ep()]:  sql# " + sql);
        Map args = StreamMap.create().put("subject", (Object) subject).put("exam", (Object) exam).put("grade", (Object) grade).put("userId", (Object) userId);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectGradeList(String exam, String subject, String school, String stat, String type, String systemType) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = "  and " + type + "  = {stat}  ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + " and numStatus = {STATUS_CORRECT_COMPLETE} ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("") && !subject.equals("-1")) {
            sub = " AND subjectNum = {subject} ";
        }
        String schoolStr = " ";
        if (null != school && !school.equals("") && !school.equals("-1")) {
            schoolStr = "and schoolNUm = {school} ";
        }
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = "and examNum = {exam}  ";
        }
        String sql = "SELECT DISTINCT b.gradeNum num ,b.gradeName name  FROM \t(SELECT examPaperNum,gradeNum FROM correctstatus WHERE 1=1 " + schoolStr + str + sub + exa + ") c left join exampaper e on c.examPaperNum = e.examPaperNum left JOIN basegrade b ON b.gradeNum = c.gradeNum  where e.type = {systemType} order by c.gradeNum  ";
        this.log.info(getClass().getSimpleName() + "[getCorrectGradeList()]:  sql# " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("subject", (Object) subject).put(License.SCHOOL, (Object) school).put("exam", (Object) exam).put(License.SYSTYPE, (Object) systemType);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectGradeList_zy(String exam, String subject, String school, String stat, String type, String systemType, String testCenter) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = "  and " + type + "  = {stat}  ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + " and numStatus = {STATUS_CORRECT_COMPLETE} ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("") && !subject.equals("-1")) {
            sub = " AND subjectNum = {subject} ";
        }
        if (null == school || school.equals("") || !school.equals("-1")) {
        }
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = "and examNum = {exam}  ";
        }
        String tcStr = "";
        if (null != testCenter && !testCenter.equals("") && !testCenter.equals("-1")) {
            tcStr = "and testingCentreId = {testCenter}  ";
        }
        String sql = "SELECT DISTINCT b.gradeNum num ,b.gradeName name FROM \t(SELECT examPaperNum,gradeNum FROM correctstatus WHERE 1=1 " + str + sub + exa + tcStr + ") c left join exampaper e on c.examPaperNum = e.examPaperNum left JOIN basegrade b ON b.gradeNum = c.gradeNum order by b.gradeNum";
        this.log.info(getClass().getSimpleName() + "[getCorrectGradeList()]:  sql# " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("subject", (Object) subject).put(License.SCHOOL, (Object) school).put("exam", (Object) exam).put("testCenter", (Object) testCenter);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getExamPlace(String exam, String subject, String grade, String stat, String type, String systemType) {
        String sub = "";
        if (null != subject && !subject.equals("") && !subject.equals("-1")) {
            sub = " AND subjectNum = {subject} ";
        }
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = " and examNum = {exam}  ";
        }
        String gradesql = "";
        if (null != grade && !grade.equals("") && !grade.equals("-1")) {
            gradesql = " and gradeNum = {grade}  ";
        }
        String sql = "select t.id num,t.testingCentreName name from(select DISTINCT testingCentreId from examinationroom e where 1=1 " + exa + gradesql + sub + ")e LEFT JOIN testingcentre t on e.testingCentreId=t.id where t.id is not null order by convert(t.testingCentreName using gbk)";
        this.log.info(getClass().getSimpleName() + "[getExamPlace()]:  sql# " + sql);
        Map args = StreamMap.create().put("subject", (Object) subject).put("exam", (Object) exam).put("grade", (Object) grade);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getAuthExamPlace(String exam, String subject, String grade, String stat, String type, String userId) {
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = " and examNum = {exam}  ";
        }
        String sub = "";
        if (null != subject && !subject.equals("") && !subject.equals("-1")) {
            sub = " AND subjectNum = {subject} ";
        }
        String gradesql = "";
        if (null != grade && !grade.equals("") && !grade.equals("-1")) {
            gradesql = " and gradeNum = {grade}  ";
        }
        String sql = "select distinct tcs.testingCentreId num,tc.testingCentreName name from  (select DISTINCT testingCentreId from examinationroom e where 1=1 " + exa + gradesql + sub + ")e left join (select testingCentreId,schoolNum from testingcentre_school where examNum={exam} ) tcs ON e.testingCentreId=tcs.testingCentreId left join (SELECT es.schoolNum from examschool es  LEFT JOIN schoolscanpermission scm ON es.schoolNum=scm.schoolNum  WHERE scm.userNum={userId} AND es.examNum={exam} ) sam on CAST(sam.schoolNum AS CHAR) = CAST(tcs.schoolNum AS CHAR)  left join  (select id,testingCentreNum,testingCentreName from testingcentre where examNum = {exam}) tc on tc.id = tcs.testingCentreId  where sam.schoolNum is not null and tc.id is not null order by convert(tc.testingCentreName using gbk)";
        this.log.info(getClass().getSimpleName() + "[getAuthExamPlace()]:  sql# " + sql);
        Map args = StreamMap.create().put("exam", (Object) exam).put("subject", (Object) subject).put("grade", (Object) grade).put("userId", (Object) userId);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List getCorrectSubjectList(String exam, String subject, String school, String stat, String type, String systemType) {
        if (null == subject || subject.equals("") || !subject.equals("-1")) {
        }
        String schoolStr = " ";
        if (null != school && !school.equals("") && !school.equals("-1")) {
            schoolStr = "and schoolNum = {school}  ";
        }
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = "and examNum = {exam}  ";
        }
        String sql = "SELECT DISTINCT c.subjectNum num ,s.subjectName name FROM \t(SELECT examPaperNum,gradeNum,subjectNum FROM correctstatus WHERE 1=1 " + schoolStr + exa + ") c left join exampaper e on c.examPaperNum = e.examPaperNum LEFT JOIN `subject` s ON c.subjectNum=s.subjectNum left JOIN basegrade b ON b.gradeNum = c.gradeNum  where e.type ={systemType} ";
        this.log.info(getClass().getSimpleName() + "[getCorrectGradeList()]:  sql# " + sql);
        Map args = StreamMap.create().put("subject", (Object) subject).put(License.SCHOOL, (Object) school).put("exam", (Object) exam).put(License.SYSTYPE, (Object) systemType);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getCorrectSchoolList(String exam, String subject, String stat, String type, String systemType) {
        if (null != stat && !stat.equals("")) {
            String str = " and " + type + "  = {stat}  ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                String str2 = str + "  and numStatus = {STATUS_CORRECT_COMPLETE} ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = "and subjectNum = {subject} ";
        }
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = "and examNum = {exam}  ";
        }
        String sql = " SELECT DISTINCT sch.id num ,sch.shortname name FROM   (  \tselect examPaperNum from exampaper where type = {systemType} " + sub + exa + "  ) c  inner join regexaminee r on  c.examPaperNum = r.examPaperNum   inner join school sch ON sch.id = r.schoolNum ";
        this.log.info(getClass().getSimpleName() + "[getCorrectSchoolList()]:  sql# " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("subject", (Object) subject).put("exam", (Object) exam).put(License.SYSTYPE, (Object) systemType);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getCorrctTCList(String exam, String subject, String stat, String type, String systemType) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " " + type + "  = {stat} and ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + "numStatus = {STATUS_CORRECT_COMPLETE} and ";
            }
        }
        String sql = "SELECT DISTINCT tc.id num,tc.testingCentreName name  FROM (SELECT examPaperNum,examNum,testingCentreId FROM correctstatus WHERE " + str + " examNum = {exam} AND subjectNum = {subject}) c  LEFT JOIN exampaper e on e.examPaperNum=c.examPaperNum LEFT JOIN exam ex ON ex.examNum = c.examNum LEFT JOIN testingcentre tc ON tc.id = c.testingCentreId WHERE ex.examNum is not NULL and tc.id is not NULL order by convert(tc.testingCentreName using gbk) ";
        this.log.info(getClass().getSimpleName() + "[getCorrectTCList()]:  sql# " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("exam", (Object) exam).put("subject", (Object) subject);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getCorrctTCList2(String exam, String subject, String grade, String stat, String type, String systemType) {
        String sub = "";
        if (null != subject && !subject.equals("") && !subject.equals("-1")) {
            sub = " AND subjectNum in ({subjectNum[]}) ";
        }
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = " and examNum = {exam}  ";
        }
        String gradesql = "";
        if (null != grade && !grade.equals("") && !grade.equals("-1")) {
            gradesql = " and gradeNum = {grade}  ";
        }
        String sql = "select t.id num,t.testingCentreName name from(select DISTINCT testingCentreId from examinationroom e where 1=1 " + exa + gradesql + sub + ")e LEFT JOIN testingcentre t on e.testingCentreId=t.id where t.id is not null order by convert(t.testingCentreName using gbk)";
        this.log.info(getClass().getSimpleName() + "[getExamPlace()]:  sql# " + sql);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subject).put("exam", (Object) exam).put("grade", (Object) grade);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getCorrctAuthTCList(String exam, String subject, String stat, String type, String userId) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " " + type + "  = {stat} and ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + "numStatus = {STATUS_CORRECT_COMPLETE} and ";
            }
        }
        String sql = "select distinct tc.id num,tc.testingCentreName name from  (select testingCentreId from correctstatus where " + str + " examNum = {exam} AND subjectNum = {subject})e left join (select testingCentreId,schoolNum from testingcentre_school where examNum={exam} ) tcs ON e.testingCentreId=tcs.testingCentreId left join (SELECT es.schoolNum from examschool es  LEFT JOIN schoolscanpermission scm ON es.schoolNum=scm.schoolNum  WHERE scm.userNum={userId} AND es.examNum={exam} ) sam on sam.schoolNum = tcs.schoolNum  left join  (select id,testingCentreNum,testingCentreName from testingcentre where examNum = {exam}) tc on tc.id = tcs.testingCentreId  where sam.schoolNum is not null and tc.id is not null order by convert(tc.testingCentreName using gbk)";
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("exam", (Object) exam).put("subject", (Object) subject).put("userId", (Object) userId);
        this.log.info(getClass().getSimpleName() + "[getCorrctAuthTCList()]:  sql# " + sql);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getCorrctAuthTCList2(String exam, String subject, String grade, String stat, String type, String userId) {
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = " and examNum = {exam}  ";
        }
        String sub = "";
        if (null != subject && !subject.equals("") && !subject.equals("-1")) {
            sub = " AND subjectNum in ({subjectNum[]}) ";
        }
        String gradesql = "";
        if (null != grade && !grade.equals("") && !grade.equals("-1")) {
            gradesql = " and gradeNum = {grade}  ";
        }
        String sql = "select distinct tc.id num,tc.testingCentreName name from  (select DISTINCT testingCentreId from examinationroom e where 1=1 " + exa + gradesql + sub + ")e left join (select testingCentreId,schoolNum from testingcentre_school where examNum={exam} ) tcs ON e.testingCentreId=tcs.testingCentreId left join (SELECT es.schoolNum from examschool es  LEFT JOIN schoolscanpermission scm ON es.schoolNum=scm.schoolNum  WHERE scm.userNum={userId} AND es.examNum={exam} ) sam on sam.schoolNum = tcs.schoolNum  left join  (select id,testingCentreNum,testingCentreName from testingcentre where examNum = {exam}) tc on tc.id = tcs.testingCentreId  where sam.schoolNum is not null and tc.id is not null order by convert(tc.testingCentreName using gbk)";
        this.log.info(getClass().getSimpleName() + "[getAuthExamPlace()]:  sql# " + sql);
        Map args = StreamMap.create().put("exam", (Object) exam).put(Const.EXPORTREPORT_subjectNum, (Object) subject).put("grade", (Object) grade).put("userId", (Object) userId);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public boolean isManager(String userId) {
        if ("-1".equals(userId) || "-2".equals(userId)) {
            return true;
        }
        Map args = StreamMap.create().put("userId", (Object) userId);
        return null != this.dao2._queryObject("(select id from userrole where userNum = {userId} and roleNum = '-1' limit 1 )union (select id from schoolscanpermission where type = '1' and userNum = {userId} limit 1)", args);
    }

    public List<AjaxData> getCorrectSchoolList_zy(String exam, String subject, String stat, String type, String systemType) {
        if (null != stat && !stat.equals("")) {
            String str = " and " + type + "  = {stat}  ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                String str2 = str + "  and numStatus = {STATUS_CORRECT_COMPLETE} ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = "and subjectNum = {subject} ";
        }
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = "and examNum = {exam}  ";
        }
        String sql = " SELECT DISTINCT sch.id num ,sch.shortname name FROM   (  \tselect examPaperNum from exampaper where 1=1 " + sub + exa + "  ) c  inner join regexaminee r on  c.examPaperNum = r.examPaperNum   inner join school sch ON sch.id = r.schoolNum ";
        this.log.info(getClass().getSimpleName() + "[getCorrectSchoolList()]:  sql# " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("subject", (Object) subject).put("exam", (Object) exam);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getCorrctSchoolList_EP(String exam, String subject, String stat, String type, String systemType, String examplace, String grade) {
        if (null != stat && !stat.equals("")) {
            String str = " and " + type + "  = {stat}  ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                String str2 = str + "  and numStatus = {STATUS_CORRECT_COMPLETE} ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = "and subjectNum = {subject} ";
        }
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = "and examNum = {exam}  ";
        }
        String epstring = "";
        if (!examplace.equals("-1")) {
            epstring = " and testingCentreId = {examplace}  ";
        }
        String gradeString = "";
        if (null != grade && !grade.equals("")) {
            gradeString = "and gradeNum = {grade} ";
        }
        String sql = " SELECT DISTINCT sch.id num ,sch.shortname name FROM   (  \tselect examPaperNum from exampaper where 1=1 " + sub + exa + gradeString + "  ) c  inner join regexaminee r on  c.examPaperNum = r.examPaperNum " + epstring + "  inner join school sch ON sch.id = r.schoolNum ";
        this.log.info(getClass().getSimpleName() + "[getCorrectSchoolList()]:  sql# " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("subject", (Object) subject).put("exam", (Object) exam).put("examplace", (Object) examplace).put("grade", (Object) grade);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getCorrctAuthSchoolList_EP(String exam, String subject, String stat, String type, String userId, String examplace, String grade) {
        if (null != stat && !stat.equals("")) {
            String str = " and " + type + "  = {stat} ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                String str2 = str + "  and numStatus = {STATUS_CORRECT_COMPLETE} ";
            }
        }
        String sub = "";
        if (null != subject && !subject.equals("")) {
            sub = "and subjectNum = {subject} ";
        }
        String exa = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            exa = "and examNum = {exam}  ";
        }
        String gradeString = "";
        if (null != grade && !grade.equals("")) {
            gradeString = "and gradeNum = {grade} ";
        }
        String sql = " SELECT DISTINCT sch.id num ,sch.shortname name FROM (select examPaperNum from exampaper where 1=1 " + sub + exa + gradeString + ") c inner join regexaminee r on  c.examPaperNum = r.examPaperNum left join scanpermission scanp on scanp.testingCentreId = r.testingCentreId inner join school sch ON sch.id = r.schoolNum where scanp.userNum = {userId} ";
        this.log.info(getClass().getSimpleName() + "[getCorrctAuthSchoolList_EP()]:  sql# " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("subject", (Object) subject).put("exam", (Object) exam).put("grade", (Object) grade).put("userId", (Object) userId);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<Classexam> getCorrectSchoolList_export(String examNum, String subjectNum, String gradeNum, String examPaperNum) {
        if (null == subjectNum || !subjectNum.equals("")) {
        }
        String sql = ((null == examPaperNum || "".equals(examPaperNum)) ? "SELECT sch.id schoolNum,sch.schoolName FROM (SELECT  DISTINCT schoolNum FROM correctstatus WHERE   examNum = {examNum}   " : "SELECT sch.id schoolNum,sch.schoolName FROM (SELECT  DISTINCT schoolNum FROM correctstatus WHERE   examPaperNum = {examPaperNum}  ") + " ) c    left JOIN school sch ON sch.id = c.schoolNum  ORDER BY sch.id ASC  ";
        this.log.info(getClass().getSimpleName() + "[getCorrectSchoolList_export()]:  sql# " + sql);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("examPaperNum", (Object) examPaperNum).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryBeanList(sql, Classexam.class, args);
    }

    public List<AjaxData> getCorrectSubjectList(String exam, String stat, String type, String systemType) {
        String str = "";
        String subTypeString = "";
        if (!"2".equals(systemType)) {
            subTypeString = "and e.type={systemType}  ";
        }
        if (null != stat && !stat.equals("")) {
            str = " " + type + "  = {stat} and ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + "numStatus = {STATUS_CORRECT_COMPLETE}  and ";
            }
        }
        String sql = "SELECT DISTINCT s.subjectNum num,s.subjectName name FROM (SELECT examPaperNum,examNum,subjectNum FROM correctstatus WHERE " + str + " examNum = {exam} ) c LEFT JOIN exampaper e on e.examPaperNum=c.examPaperNum LEFT JOIN exam ex ON ex.examNum = c.examNum LEFT JOIN `subject` s ON s.subjectNum = c.subjectNum WHERE ex.examNum is not NULL " + subTypeString + " order by s.id ";
        Map args = StreamMap.create().put(License.SYSTYPE, (Object) systemType).put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("exam", (Object) exam);
        this.log.info(getClass().getSimpleName() + "[getCorrectSubjectList()]:  sql# " + sql);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getCorrectSubjectList_zy(String exam, String stat, String type, String systemType) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " " + type + "  = {stat}  and ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + "numStatus = {STATUS_CORRECT_COMPLETE} and ";
            }
        }
        String sql = " SELECT DISTINCT s.subjectNum num,s.subjectName name FROM  (SELECT examPaperNum,examNum,subjectNum FROM correctstatus WHERE " + str + " examNum = {exam} ) c  LEFT JOIN exampaper e on e.examPaperNum=c.examPaperNum  LEFT JOIN exam ex ON ex.examNum = c.examNum  LEFT JOIN `subject` s ON s.subjectNum = c.subjectNum  WHERE ex.examNum is not NULL  order by s.id ";
        this.log.info(getClass().getSimpleName() + "[getCorrectSubjectList()]:  sql# " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("exam", (Object) exam);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public List<AjaxData> getCorrectSubjectList_zyyh(String exam, String stat, String type, String systemType) {
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " " + type + "  = {stat} and ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + "numStatus = {STATUS_CORRECT_COMPLETE} and ";
            }
        }
        String sql = "SELECT DISTINCT s.subjectNum num,s.subjectName name  FROM (SELECT examPaperNum,examNum,subjectNum FROM correctstatus WHERE " + str + " examNum = {exam} ) c LEFT JOIN exampaper e on e.examPaperNum=c.examPaperNum LEFT JOIN exam ex ON ex.examNum = c.examNum LEFT JOIN `subject` s ON s.subjectNum = c.subjectNum WHERE ex.examNum is not NULL  order by s.id ";
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("exam", (Object) exam);
        this.log.info(getClass().getSimpleName() + "[getCorrectSubjectList()]:  sql# " + sql);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public List<AjaxData> getCorrectExamList(String stat, String type, String systemType, String userId, String oneOrMore) {
        new ArrayList();
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " where " + type + " = {stat} ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + " and numStatus = {STATUS_CORRECT_COMPLETE}  ";
            }
        }
        String sql = "SELECT DISTINCT c.examNum num,ex.examName name  FROM (SELECT DISTINCT examPaperNum,examNum FROM correctstatus " + str + " ) c LEFT JOIN exampaper e on e.examPaperNum=c.examPaperNum LEFT JOIN exam ex ON ex.examNum = c.examNum where  ex.examNum is not null and e.type={systemType} and ex.isdelete = {FALSE} and ex.status != {STATUS_EXAM_COMPLETE} ORDER BY ex.status ASC,ex.insertDate DESC";
        this.log.info(getClass().getSimpleName() + "[getCorrectExamList()]:  sql# " + sql);
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put(License.SYSTYPE, (Object) systemType).put("FALSE", (Object) "F").put("userNum", (Object) userId).put("STATUS_EXAM_COMPLETE", (Object) "9");
        List _queryBeanList = this.dao2._queryBeanList(sql, AjaxData.class, args);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args);
        if (userId != null && "" != userId && !userId.equals("-1") && !userId.equals("-2") && null == map) {
            if ("1".equals(oneOrMore)) {
                if (null == map) {
                    _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT c.examNum num,ex.examName name  FROM (SELECT DISTINCT examPaperNum,examNum FROM correctstatus " + str + " ) c  LEFT JOIN exampaper e on e.examPaperNum=c.examPaperNum  LEFT JOIN exam ex ON ex.examNum = c.examNum  LEFT JOIN examschool es ON e.examNum=es.examNum   right JOIN (select schoolNum from schoolscanpermission where userNum={userNum} union select  schoolNum from user where id={userNum} ) scm ON cast(es.schoolNum as char)=cast(scm.schoolNum as char)  where  ex.examNum is not null and e.type={systemType}  and ex.isdelete = {FALSE} and ex.status != {STATUS_EXAM_COMPLETE} ORDER BY ex.status ASC,ex.insertDate DESC", AjaxData.class, args);
                }
            } else if ("2".equals(oneOrMore)) {
                List<AjaxData> ajaxData = getUserNoPerExam(userId);
                if (ajaxData.size() != 0) {
                    for (AjaxData data : ajaxData) {
                        String num = data.getNum();
                        _queryBeanList.removeIf(o -> {
                            return num.equals(Convert.toStr(o.getNum()));
                        });
                    }
                }
            }
        }
        return _queryBeanList;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public List<AjaxData> getCorrectExamList_zy(String stat, String type, String systemType, String userId, String oneOrMore) {
        new ArrayList();
        Map args = StreamMap.create().put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("FALSE", (Object) "F").put("userNum", (Object) userId).put("STATUS_EXAM_COMPLETE", (Object) "9");
        String str = "";
        if (null != stat && !stat.equals("")) {
            str = " where " + type + " = {stat} ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + " and numStatus = {STATUS_CORRECT_COMPLETE}  ";
            }
        }
        String sql = "SELECT DISTINCT c.examNum num,ex.examName name  FROM (SELECT DISTINCT examPaperNum,examNum FROM correctstatus " + str + " ) c LEFT JOIN exampaper e on e.examPaperNum=c.examPaperNum LEFT JOIN exam ex ON ex.examNum = c.examNum where  ex.examNum is not null and ex.isdelete = {FALSE} and ex.status != {STATUS_EXAM_COMPLETE} ORDER BY ex.status ASC,ex.insertDate DESC";
        List _queryBeanList = this.dao2._queryBeanList(sql, AjaxData.class, args);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args);
        if (userId != null && "" != userId && !userId.equals("-1") && !userId.equals("-2") && null == map) {
            if ("1".equals(oneOrMore)) {
                if (null == map) {
                    sql = "SELECT DISTINCT c.examNum num,ex.examName name  FROM (SELECT DISTINCT examPaperNum,examNum FROM correctstatus " + str + " ) c LEFT JOIN exampaper e on e.examPaperNum=c.examPaperNum LEFT JOIN exam ex ON ex.examNum = c.examNum  LEFT JOIN examschool es ON ex.examNum=es.examNum  right JOIN (select schoolNum from schoolscanpermission where userNum={userNum}) scm ON CAST(es.schoolNum as char)=CAST(scm.schoolNum  as char)   where  ex.examNum is not null and ex.isdelete = {FALSE} and ex.status != {STATUS_EXAM_COMPLETE}  ORDER BY ex.status ASC,ex.insertDate DESC";
                    _queryBeanList = this.dao2._queryBeanList(sql, AjaxData.class, args);
                }
            } else if ("2".equals(oneOrMore)) {
                List<AjaxData> ajaxData = examService.getUserNoPerExam(userId);
                if (ajaxData.size() != 0) {
                    for (AjaxData data : ajaxData) {
                        String num = data.getNum();
                        _queryBeanList.removeIf(o -> {
                            return num.equals(Convert.toStr(o.getNum()));
                        });
                    }
                }
            }
        }
        this.log.info(getClass().getSimpleName() + "[getCorrectExamList()]:  sql# " + sql);
        return _queryBeanList;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public List<AjaxData> getCorrectExamList_zyyh(String stat, String type, String systemType, String userId, String oneOrMore) {
        new ArrayList();
        String str = "";
        String seleExamTypeString = "";
        if (!"2".equals(systemType)) {
            seleExamTypeString = "and e.type={systemType} or e.type=2";
        }
        if (null != stat && !stat.equals("")) {
            str = " where " + type + " = {stat} ";
            if (null == type || type.equals(Const.CORRECT_APPENDCORRECT)) {
                str = str + " and numStatus = {STATUS_CORRECT_COMPLETE}  ";
            }
        }
        String sql = "SELECT DISTINCT c.examNum num,ex.examName name  FROM (SELECT DISTINCT examPaperNum,examNum FROM correctstatus " + str + " ) c LEFT JOIN exampaper e on e.examPaperNum=c.examPaperNum LEFT JOIN exam ex ON ex.examNum = c.examNum where  ex.examNum is not null  " + seleExamTypeString + " and ex.isdelete = {FALSE} and ex.status != {STATUS_EXAM_COMPLETE} ORDER BY ex.status ASC,ex.insertDate DESC";
        this.log.info(getClass().getSimpleName() + "[getCorrectExamList()]:  sql# " + sql);
        Map args = StreamMap.create().put(License.SYSTYPE, (Object) systemType).put("stat", (Object) stat).put("STATUS_CORRECT_COMPLETE", (Object) "2").put("FALSE", (Object) "F").put("STATUS_EXAM_COMPLETE", (Object) "9").put("userNum", (Object) userId);
        List _queryBeanList = this.dao2._queryBeanList(sql, AjaxData.class, args);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args);
        if (userId != null && "" != userId && !userId.equals("-1") && !userId.equals("-2") && null == map) {
            if ("1".equals(oneOrMore)) {
                if (null == map) {
                    _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT c.examNum num,ex.examName name  FROM (SELECT DISTINCT examPaperNum,examNum FROM correctstatus " + str + " ) c LEFT JOIN exampaper e on e.examPaperNum=c.examPaperNum LEFT JOIN exam ex ON ex.examNum = c.examNum  LEFT JOIN examschool es ON ex.examNum=es.examNum  right JOIN (select schoolNum from schoolscanpermission where userNum={userNum}) scm ON CAST(es.schoolNum as char)=CAST(scm.schoolNum  as char)   where  ex.examNum is not null  " + seleExamTypeString + " and ex.isdelete = {FALSE} and ex.status != {STATUS_EXAM_COMPLETE} ORDER BY ex.status ASC,ex.insertDate DESC", AjaxData.class, args);
                }
            } else if ("2".equals(oneOrMore)) {
                List<AjaxData> ajaxData = examService.getUserNoPerExam(userId);
                if (ajaxData.size() != 0) {
                    for (AjaxData data : ajaxData) {
                        String num = data.getNum();
                        _queryBeanList.removeIf(o -> {
                            return num.equals(Convert.toStr(o.getNum()));
                        });
                    }
                }
            }
        }
        return _queryBeanList;
    }

    public Integer updateScore(String[] ids, double score, boolean moreThanFullMarks) {
        List<RowArg> list = new ArrayList<>();
        ExamServiceImpl examService2 = new ExamServiceImpl();
        String time = DateUtil.getCurrentTime();
        for (String id : ids) {
            Map args = new HashMap();
            args.put("SCORE_EXCEPTION_UPFULLMARKS_update", "1");
            args.put("score", Double.valueOf(score));
            args.put("TRUE", "T");
            args.put("id", id);
            String sqls = "INSERT INTO correctlog(questionNum,questionScore,studentId,examPaperNum,answer,qtype,oldScore,examinationRoomNum,insertUser,insertdate) SELECT questionNum,'" + score + "',studentId,examPaperNum,NULL,1,questionScore,examinationRoomNum,insertUser,NOW()  FROM score where id = {id} ";
            list.add(new RowArg(sqls, args));
            Score s = (Score) getOneByNum("id", id, Score.class);
            if (null == s) {
                return null;
            }
            String exceptionStatus = Const.Data_type_of_Wrong;
            if (moreThanFullMarks) {
                exceptionStatus = " {SCORE_EXCEPTION_UPFULLMARKS_update} ";
            }
            args.put("exceptionStatus", exceptionStatus);
            String realScore = getQuestionScoreByQuestionInfo(s.getExamPaperNum().intValue(), String.valueOf(s.getQuestionNum()), String.valueOf(score));
            args.put("realScore", realScore);
            list.add(new RowArg("update score set questionScore = {realScore} ,regScore= {score},isModify={TRUE} ,isException={exceptionStatus } where id = {id} ", args));
            dao.deleteScoreBiaoji(realScore, id);
            AwardPoint awardPoint = new AwardPoint();
            awardPoint.setQuestionNum(s.getQuestionNum());
            awardPoint.setInsertUser(s.getInsertUser());
            awardPoint.setInsertDate(time);
            awardPoint.setExampaperNum(s.getExamPaperNum().intValue());
            awardPoint.setType("1");
            awardPoint.setUserNum(s.getInsertUser());
            awardPoint.setQuestionScore(String.valueOf(score));
            awardPoint.setScoreId(s.getId());
            awardPoint.setIsException("F");
            examService2.deleteMarkError(awardPoint);
            dao.addRemark(s.getId(), s.getQuestionNum(), String.valueOf(s.getExamPaperNum()), s.getInsertUser(), time, null);
        }
        try {
            this.dao2._batchExecute(list);
            for (String str : ids) {
                dao.updateStudentTotalScore(str);
            }
            return 1;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer updateCorrectStatus(String status, String examination, String exam, String subject, String grade, String loginUserId, String testCenter) {
        String sql;
        Map args = new HashMap();
        args.put("loginUserId", loginUserId);
        args.put(Const.CORRECT_SCORECORRECT, status);
        args.put("exam", exam);
        args.put("subject", subject);
        args.put("grade", grade);
        args.put("testCenter", testCenter);
        args.put("SCORE_EXCEPTION_UPFULLMARKS_update", "1");
        args.put("SCORE_EXCEPTION_UPFULLMARKS", "4");
        args.put("examination", examination);
        if (status.equals("1")) {
        }
        Object obj = getCorrectStatus(examination, exam, subject, grade, "1", testCenter);
        if (null != obj && Integer.parseInt(obj.toString()) > Integer.parseInt(status)) {
            return 1;
        }
        if (null == examination || examination.equals("") || examination.equals("-1") || examination.equals("undefined")) {
            sql = "update correctStatus set status = {status}  ,insertUser = {loginUserId}  where  1=1   and examNum = {exam} and subjectNum={subject}   and gradeNum = {grade}  and testingCentreId = {testCenter}   ";
            if (status != null && status.equals("2")) {
                String examPaperNum = getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
                args.put("examPaperNum", examPaperNum);
                this.log.info("修改考场的分数校对状态时修改所有识别超满分的题目异常标志--sql2---UPDATE score SET isException= {SCORE_EXCEPTION_UPFULLMARKS_update}   WHERE examPaperNum={examPaperNum}   AND isException={SCORE_EXCEPTION_UPFULLMARKS}    and testingCentreId = {testCenter}   ");
                this.dao2._execute("UPDATE score SET isException= {SCORE_EXCEPTION_UPFULLMARKS_update}   WHERE examPaperNum={examPaperNum}   AND isException={SCORE_EXCEPTION_UPFULLMARKS}    and testingCentreId = {testCenter}   ", args);
            }
        } else {
            sql = "update correctStatus set status = {status}  ,insertUser = {loginUserId}  where examnitionRoom = {examination}    and examNum = {exam} and subjectNum={subject}   and gradeNum = {grade}   ";
            if (status != null && status.equals("2")) {
                String examPaperNum2 = getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
                args.put("examPaperNum", examPaperNum2);
                this.log.info("修改考场的分数校对状态时修改所有识别超满分的题目异常标志--sql2---UPDATE score SET isException={SCORE_EXCEPTION_UPFULLMARKS_update}   WHERE examPaperNum={examPaperNum}  AND examinationRoomNum={examination}     AND isException={SCORE_EXCEPTION_UPFULLMARKS}    ");
                this.dao2._execute("UPDATE score SET isException={SCORE_EXCEPTION_UPFULLMARKS_update}   WHERE examPaperNum={examPaperNum}  AND examinationRoomNum={examination}     AND isException={SCORE_EXCEPTION_UPFULLMARKS}    ", args);
            }
        }
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    public Integer updateCorrectNumsStat(String status, String examination, String exam, String subject, String grade, String loginUserId, String testCenter, String examroomornot) {
        String examPaperNum = getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
        if (status.equals("1")) {
        }
        Object obj = getCorrectStatus(examination, exam, subject, grade, "2", testCenter);
        if (null != obj && Integer.parseInt(obj.toString()) > Integer.parseInt(status)) {
            return 1;
        }
        String sql = (null == examroomornot || !examroomornot.equals("1")) ? "update correctStatus set numStatus = {status}  ,insertUser = {loginUserId}   where  examPaperNum= {examPaperNum}    and  examnitionRoom = {examination}   " : "update correctStatus set numStatus = {status}   ,insertUser = {loginUserId}   where    examPaperNum= {examPaperNum}  AND testingCentreId={testCenter}   ";
        Map args = StreamMap.create().put("loginUserId", (Object) loginUserId).put(Const.CORRECT_SCORECORRECT, (Object) status).put("examPaperNum", (Object) examPaperNum).put("testCenter", (Object) testCenter).put("examination", (Object) examination);
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    public Object getCorrectStatus(String examination, String exam, String subject, String grade, String type, String testCenter) {
        String statStr = " status  ";
        if (null != type && type.equals("2")) {
            statStr = " numStatus  ";
        }
        String sql = " select " + statStr + " from correctStatus where 1=1   ";
        if (null != examination && !"".equals(examination) && !"-1".equals(examination) && !"undefined".equals(examination)) {
            sql = sql + "and examnitionRoom = {examination}   ";
        }
        String sql2 = sql + "and examNum = {exam}  and subjectNum={subject}   and gradeNum = {grade}   ";
        if (null != testCenter && !"".equals(testCenter)) {
            sql2 = sql2 + " AND testingCentreId={testCenter}   ";
        }
        Map args = StreamMap.create().put("examination", (Object) examination).put("exam", (Object) exam).put("subject", (Object) subject).put("grade", (Object) grade).put("testCenter", (Object) testCenter);
        return this.dao2._queryObject(sql2, args);
    }

    public Integer SYNCCorrectingInfo() {
        this.log.info(getClass().getSimpleName() + "[SYNCCorrectingInfo()]  sql: INSERT INTO correctstatus(subjectNum,examNum,gradeNum,schoolNum,examnitionRoom,status,insertdate,classNum) SELECT e.subjectNum,e.examNum,e.gradeNum,s.schoolNum,s.examinationRoomNum,'0',now(),s.classNum FROM\t\t(SELECT DISTINCT examPaperNum,schoolNum,examinationRoomNum,classNum FROM score  ) s LEFT JOIN exampaper e ON s.examPaperNum = e.exampaperNum");
        return Integer.valueOf(this.dao2.execute("INSERT INTO correctstatus(subjectNum,examNum,gradeNum,schoolNum,examnitionRoom,status,insertdate,classNum) SELECT e.subjectNum,e.examNum,e.gradeNum,s.schoolNum,s.examinationRoomNum,'0',now(),s.classNum FROM\t\t(SELECT DISTINCT examPaperNum,schoolNum,examinationRoomNum,classNum FROM score  ) s LEFT JOIN exampaper e ON s.examPaperNum = e.exampaperNum"));
    }

    public CorrectInfo imageDetailInfo(CorrectInfo c) {
        Map args = StreamMap.create().put("StudentId", (Object) c.getStudentId()).put("QuestionNum", (Object) c.getQuestionNum());
        return (CorrectInfo) this.dao2._queryBean("select fullScore,questionNum,(select studentname from student where id = {StudentId} ) studentName  from define where id={QuestionNum}  and questionNum is not null union all  select fullScore,questionNum,(select studentname from student where id = {StudentId} ) studentName  from subdefine where id={QuestionNum}  and questionNum is not null", CorrectInfo.class, args);
    }

    public List<CorrectInfo> getImageNotToScoreList(String examPaperNum, String school, String examinationRoom) {
        String str = "";
        if (null != examinationRoom && !examinationRoom.equals("")) {
            str = " and examinationRoomNum = {examinationRoom} ";
        }
        String sql = "SELECT id,questionNum,studentId,examPaperNum ext1 FROM score  WHERE examPaperNum = {examPaperNum}  AND schoolNum = {school} " + str + "   AND isException = {SCORE_EXCEPTION_NOTTOSCORE} ";
        this.log.info(getClass().getSimpleName() + "[getImageNotToScoreList()] sql: " + sql);
        Map args = StreamMap.create().put("examinationRoom", (Object) examinationRoom).put("examPaperNum", (Object) examPaperNum).put(License.SCHOOL, (Object) school).put("SCORE_EXCEPTION_NOTTOSCORE", (Object) "0");
        return this.dao2._queryBeanList(sql, CorrectInfo.class, args);
    }

    public double getFullScoreByExampaperAndQuestionNum(String exampaperNum, String questionNum) {
        this.log.info(getClass().getSimpleName() + "[getFullScoreByExampaperAndQuestionNum()] sql: SELECT  fullScore FROM define WHERE  AND questionNum={questionNum} union all SELECT  fullScore FROM subdefine WHERE  AND questionNum={questionNum} ");
        Map args = StreamMap.create().put("questionNum", (Object) questionNum);
        return this.dao2._queryDouble("SELECT  fullScore FROM define WHERE  AND questionNum={questionNum} union all SELECT  fullScore FROM subdefine WHERE  AND questionNum={questionNum} ", args).doubleValue();
    }

    public double getFullScore(String id, String questionNum) {
        Map args = StreamMap.create().put("id", (Object) id);
        Object r = this.dao2._queryObject("select d.fullscore  from (select exampapernum,questionnum from score where id = {id} ) e left join define d on d.exampapernum = e.exampapernum and e.questionnum = d.id ", args);
        if (null == r) {
            r = this.dao2._queryObject("select d.fullscore  from (select exampapernum,questionnum from score where id = {id} ) e left join subdefine d on d.exampapernum = e.exampapernum and e.questionnum = d.id ", args);
        }
        return Double.valueOf(String.valueOf(r)).doubleValue();
    }

    public byte[] getQuesionImage(String scoreId) {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from (select  imgpath,img  from questionImage  WHERE scoreId={scoreId} )s  left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return yuejuansplitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        return null;
    }

    public List<Object> getCrossPageScoreIdList(String scoreId) {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId);
        return this.dao2._queryColList("SELECT s2.id from score s1 LEFT JOIN score s2 on s1.studentId=s2.studentId and s1.questionNum=s2.questionNum  WHERE s1.id={scoreId} and s2.page!=s1.page ORDER BY s2.page", args);
    }

    public Map<String, Map<String, Object>> getCrossPageScoreId2Map(List scoreIds) {
        Map<String, Object> arg = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        sb.append("select * from (");
        for (int i = 0; i < scoreIds.size(); i++) {
            String keyId = "id" + i;
            arg.put(keyId, scoreIds.get(i));
            String sql = "SELECT  s2.id,{} scoreId,{} orderNum,s2.page from score s1 LEFT JOIN score s2 on s1.studentId=s2.studentId and s1.questionNum=s2.questionNum  WHERE s1.id={" + keyId + " } and s2.page!=s1.page";
            sb.append(StrUtil.format(sql, new Object[]{scoreIds.get(i), Integer.valueOf(i)}));
            if (i < scoreIds.size() - 1) {
                sb.append(" UNION all ");
            }
        }
        sb.append(") q order by orderNum,page");
        return this.dao2._query2Map(sb.toString(), "scoreId", arg);
    }

    public byte[] getobjectimg(String url, String img) {
        if (null != url) {
            return splitimgurl(url, img);
        }
        return null;
    }

    public byte[] getQuesionImage(String examPaperNum, String studentID, String questionNum, String scoreId) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("scoreId", scoreId);
        args.put("questionNum", questionNum);
        args.put("studentID", studentID);
        if (null == scoreId) {
            return null;
        }
        if (null == questionNum || questionNum.equals("-1")) {
            String getsql = "SELECT examPaperNum,studentId,questionNum FROM score WHERE ";
            if (null != examPaperNum && !"-1".equals(examPaperNum)) {
                getsql = getsql + " exampaperNum = {examPaperNum} and ";
            }
            Object[] obj = this.dao2._queryArray(getsql + "id={scoreId} ", args);
            if (null != obj) {
                examPaperNum = null == obj[0] ? "-1" : obj[0].toString();
                studentID = null == obj[1] ? "-1" : obj[1].toString();
                String questionNum2 = null == obj[2] ? "-1" : obj[2].toString();
                args.put("examPaperNum", examPaperNum);
                args.put("questionNum", questionNum2);
                args.put("studentID", studentID);
            }
        }
        String questionType = "0";
        String cross_page = "F";
        Object[] obj2 = this.dao2._queryArray("select questiontype,cross_page from define where id = {questionNum}    union all  select '1' questiontype,cross_page from subdefine where id = {questionNum} ", args);
        if (null != obj2) {
            questionType = null == obj2[0] ? "0" : obj2[0].toString();
            cross_page = null == obj2[0] ? "F" : obj2[1].toString();
        }
        if (null != questionType && questionType.equals("0")) {
            Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (select  imgpath,img    from questionImage  WHERE scoreId={scoreId} )s  left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            if (null != ip) {
                return splitimgurl(ip.getLocationurl(), ip.getTableimg());
            }
            ServletContext sc = ServletActionContext.getServletContext();
            String realPath = sc.getRealPath("/");
            String file = realPath + "/common/image/bucunzai.png";
            return FileUtil.readBytes(file);
        }
        if (null != cross_page && cross_page.equals("F")) {
            Imgpath ip2 = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (select  imgpath,img    from questionImage  WHERE scoreId={scoreId} )s  left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            if (null != ip2) {
                return splitimgurl(ip2.getLocationurl(), ip2.getTableimg());
            }
            ServletContext sc2 = ServletActionContext.getServletContext();
            String realPath2 = sc2.getRealPath("/");
            String file2 = realPath2 + "/common/image/bucunzai.png";
            return FileUtil.readBytes(file2);
        }
        if (null == studentID || "-1".equals(studentID)) {
            String getsql2 = "SELECT examPaperNum,studentId,page,questionNum FROM score WHERE ";
            if (null != examPaperNum && !examPaperNum.equals("")) {
                getsql2 = getsql2 + " exampaperNum = {examPaperNum} and ";
            }
            Object[] obj22 = this.dao2._queryArray(getsql2 + "id=" + scoreId, args);
            args.put("studentID", null == obj22[1] ? "0" : obj22[1].toString());
        }
        List<Object> list = this.dao2._queryColList("SELECT id FROM score WHERE examPaperNum={examPaperNum} AND studentId={studentID} AND questionNum={questionNum} order by page asc", args);
        if (null == list || list.size() <= 1) {
            Imgpath ip3 = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (select  imgpath,img    from questionImage  WHERE scoreId={scoreId} )s  left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            if (null != ip3) {
                return splitimgurl(ip3.getLocationurl(), ip3.getTableimg());
            }
            ServletContext sc3 = ServletActionContext.getServletContext();
            String realPath3 = sc3.getRealPath("/");
            String file3 = realPath3 + "/common/image/bucunzai.png";
            return FileUtil.readBytes(file3);
        }
        List<byte[]> imgs = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            scoreId = String.valueOf(list.get(i));
            args.put("scoreId", scoreId);
            Imgpath ip4 = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (select  imgpath,img    from questionImage  WHERE scoreId={scoreId} )s  left join (select id,location from imgpath )ip on s.imgpath=ip.id ", Imgpath.class, args);
            if (null != ip4) {
                imgs.add(splitimgurl(ip4.getLocationurl(), ip4.getTableimg()));
            }
        }
        return commbinImage(scoreId, imgs);
    }

    public byte[] toCommbin_zht(List<String> imgList) {
        if (null == imgList || imgList.size() <= 0) {
            return new byte[0];
        }
        try {
            List<BufferedImage> imgs = new ArrayList<>();
            int width = 0;
            int totalHt = 0;
            int[] hts = new int[imgList.size()];
            for (int i = 0; i < imgList.size(); i++) {
                imgs.add(Thumbnails.of(new String[]{imgList.get(i)}).scale(0.5d).asBufferedImage());
                int ht = imgs.get(i).getHeight();
                totalHt += ht;
                hts[i] = ht;
                int wid = imgs.get(i).getWidth();
                if (wid > width) {
                    width = wid;
                }
            }
            Thumbnails.Builder<BufferedImage> outBuilder = Thumbnails.of(new BufferedImage[]{new BufferedImage(width, totalHt, 1)});
            for (int i2 = 0; i2 < imgs.size(); i2++) {
                BufferedImage bi = imgs.get(i2);
                outBuilder.watermark(new 1(this), Thumbnails.of(new BufferedImage[]{bi}).scale(1.0d).asBufferedImage(), 1.0f);
            }
            BufferedImage outImage = outBuilder.scale(1.0d).asBufferedImage();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(outImage, Const.IMAGE_FORMAT, out);
            byte[] bytes = out.toByteArray();
            outImage.flush();
            try {
                out.close();
            } catch (Exception e) {
            }
            return bytes;
        } catch (Exception e2) {
            e2.printStackTrace();
            return new byte[0];
        }
    }

    public byte[] splitimgurl(String oneurl, String endurl) {
        String file = oneurl + "/" + endurl;
        File imgfile = new File(file);
        ServletContext sc = ServletActionContext.getServletContext();
        String realPath = sc.getRealPath("/");
        if (!imgfile.exists()) {
            file = realPath + "/common/image/bucunzai.png";
        } else if (imgfile.length() == 0) {
            file = realPath + "/common/image/tupiansunhuai.png";
        }
        return FileUtil.readBytes(file);
    }

    public byte[] splitimgurl2(String oneurl, String endurl) {
        String file = oneurl + "/" + endurl;
        File imgfile = new File(file);
        ServletContext sc = ServletActionContext.getServletContext();
        String realPath = sc.getRealPath("/");
        if (!imgfile.exists()) {
            file = realPath + "/common/image/bucunzai2.png";
        } else if (imgfile.length() == 0) {
            file = realPath + "/common/image/tupiansunhuai.png";
        }
        return FileUtil.readBytes(file);
    }

    public byte[] yuejuansplitimgurl(String oneurl, String endurl) {
        String file = oneurl + "/" + endurl;
        byte[] data = new byte[0];
        try {
            InputStream input = new FileInputStream(file);
            int i = input.available();
            data = new byte[i];
            input.read(data);
            input.close();
        } catch (IOException e) {
        }
        return data;
    }

    public byte[] getSampleImage(String id) {
        this.log.info("getSampleImage  sql: select  img  img  from regsample  WHERE id={id} ");
        Map args = StreamMap.create().put("id", (Object) id);
        List<byte[]> list = this.dao2._queryBlobList("select  img  img  from regsample  WHERE id={id} ", args);
        if (null == list) {
            return null;
        }
        if (list.size() != 1) {
            return (byte[]) this.dao2._queryObject("select  img  img  from regsample  WHERE id={id} ", args);
        }
        return list.get(0);
    }

    public String addObjectiveQuesionImageSample(String examPaperNum, String studentId, String questionNum, String sampleVlue, String exePath, String loginUser, String scoreId, String regScore) {
        List<RegSample> allSamples = getObjectiveSamples();
        if (allSamples != null && allSamples.size() > 5) {
            return "6,0";
        }
        String ri = "0,0";
        String ip = this.cis.getImageServerUri();
        if ("".equals(ip)) {
            ip = exePath;
        }
        String aaUrl = ip + "imageAction!fetchQuestionImage.action?scoreId=" + scoreId;
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        byte[] imgByte = getImageStream(aaUrl);
        RegSample regSample = new RegSample();
        if (null != imgByte) {
            String templateType = this.dao2._queryStr("SELECT templateType FROM exampaper WHERE examPaperNum={examPaperNum} ", args);
            if (null != templateType && "1".equals(templateType)) {
                regSample.setTemplateType(templateType);
            } else {
                regSample.setTemplateType("0");
            }
            regSample.setImg(imgByte);
            regSample.setSection(sampleVlue);
            regSample.setInsertUser(loginUser);
            regSample.setInsertDate(DateUtil.getCurrentTime());
            Integer val = Integer.valueOf(ItemThresholdRegUtil.getThreshold(regScore, sampleVlue));
            regSample.setVal(String.valueOf(val));
            int r = this.dao2.save(regSample);
            ri = r + Const.STRING_SEPERATOR + val;
        }
        return ri;
    }

    public Integer deleteFromRegSample(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        return Integer.valueOf(this.dao2._execute("DELETE FROM regsample WHERE id={id} ", args));
    }

    public List getRegSampleById(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT id,val FROM regsample WHERE id={id} ", RegSample.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    public Integer openExe(String examPaperNum, String regSampleId, String questionNum, String sampleVlue, String exePath, String loginUser) {
        String exeP = exePath + "SampleRec.exe";
        String exeCommand = exeP + " " + examPaperNum + " " + (exePath + "WEB-INF/classes/c3p0.properties") + " " + regSampleId + " " + sampleVlue + " " + questionNum;
        this.log.info("添加样本--exeCommand:" + exeCommand);
        Runtime rn = Runtime.getRuntime();
        Thread b = null;
        Thread c = null;
        try {
            try {
                Process p = rn.exec(exeCommand);
                BufferedInputStream is1 = new BufferedInputStream(p.getInputStream());
                BufferedInputStream is2 = new BufferedInputStream(p.getErrorStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is1));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(is1));
                2 r0 = new 2(this, br);
                r0.start();
                3 r02 = new 3(this, br2);
                r02.start();
                if (p.waitFor() != 0) {
                    if (p.exitValue() == 1) {
                        this.log.error("命令执行失败!");
                    }
                    try {
                        r0.stop();
                        r02.stop();
                        return null;
                    } catch (Exception e) {
                        this.log.error("添加样本域值", e);
                        e.printStackTrace();
                        return null;
                    }
                }
                is1.close();
                is2.close();
                br.close();
                br2.close();
                Integer valueOf = Integer.valueOf(p.exitValue());
                try {
                    r0.stop();
                    r02.stop();
                    return valueOf;
                } catch (Exception e2) {
                    this.log.error("添加样本域值", e2);
                    e2.printStackTrace();
                    return null;
                }
            } catch (Exception e3) {
                this.log.error("添加样本域值，执行报错", e3);
                try {
                    b.stop();
                    c.stop();
                    return null;
                } catch (Exception e4) {
                    this.log.error("添加样本域值", e4);
                    e4.printStackTrace();
                    return null;
                }
            }
        } catch (Throwable th) {
            try {
                b.stop();
                c.stop();
                throw th;
            } catch (Exception e5) {
                this.log.error("添加样本域值", e5);
                e5.printStackTrace();
                return null;
            }
        }
    }

    public List getObjectiveSamples() {
        this.log.info("获取所有样本---getObjectiveSamples sql: SELECT id,img,section,val\tFROM regsample");
        new ArrayList();
        List<?> queryBeanList = this.dao2.queryBeanList("SELECT id,img,section,val\tFROM regsample", RegSample.class);
        if (queryBeanList != null && queryBeanList.size() > 0) {
            return queryBeanList;
        }
        return null;
    }

    public RegSample getRegSample() {
        this.log.info("获取最新添加的一条样本---getObjectiveSamples sql: SELECT id,img,section,val\tFROM regsample ORDER BY insertDate DESC LIMIT 1");
        return (RegSample) this.dao2._queryBean("SELECT id,img,section,val\tFROM regsample ORDER BY insertDate DESC LIMIT 1", RegSample.class, null);
    }

    public byte[] getBigImage(String regId) {
        Map args = StreamMap.create().put("regId", (Object) regId);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from(select  imgpath,img    from studentpaperimage  WHERE regId={regId} )s  left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        ServletContext sc = ServletActionContext.getServletContext();
        String realPath = sc.getRealPath("/");
        String file = realPath + "/common/image/bucunzai.png";
        return FileUtil.readBytes(file);
    }

    public byte[] commbinImage(String scoreId, List<byte[]> list) {
        List<BufferedImage> imgs = new ArrayList<>();
        while (0 < list.size()) {
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(list.get(0));
                list.remove(0);
                imgs.add(ImageIO.read(in));
                in.close();
            } catch (Exception e) {
                this.log.error("拼图：", e);
                e.printStackTrace();
                return null;
            }
        }
        return toCommbin2(scoreId, imgs);
    }

    public byte[] toCommbin2(String scoreId, List<BufferedImage> imgs) throws Exception {
        if (null == imgs || imgs.size() <= 0) {
            return new byte[0];
        }
        int width = 0;
        int imageNum = imgs.size();
        Integer[] hts = new Integer[imgs.size()];
        int totalHt = 0;
        for (int i = 0; i < imageNum; i++) {
            int ht = imgs.get(i).getHeight();
            totalHt += ht;
            hts[i] = Integer.valueOf(ht);
            int wid = imgs.get(i).getWidth();
            if (wid > width) {
                width = wid;
            }
        }
        BufferedImage outImage = new BufferedImage(width, totalHt, 1);
        Graphics2D graphics = outImage.getGraphics();
        Graphics2D g2d = graphics;
        g2d.setComposite(AlphaComposite.Src);
        g2d.setBackground(Color.white);
        g2d.fillRect(0, 0, width, totalHt);
        for (int i2 = 0; i2 < imageNum; i2++) {
            int ht2 = 0;
            for (int j = 0; j < i2; j++) {
                ht2 += hts[j].intValue();
            }
            g2d.drawImage(imgs.get(i2), 0, ht2, (ImageObserver) null);
        }
        if (null != graphics) {
            graphics.dispose();
        }
        if (null != g2d) {
            g2d.dispose();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(new BufferedImage[]{outImage}).scale(1.0d).outputFormat("gif").toOutputStream(os);
        byte[] bytes = os.toByteArray();
        outImage.flush();
        try {
            os.close();
        } catch (Exception e) {
        }
        return bytes;
    }

    public String commbinPreNum(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        Object obj = this.dao2._queryObject("SELECT  CONCAT( RIGHT(CONCAT('00',e.schoolNum),2), RIGHT(CONCAT('00',e.gradeNum),2), RIGHT(CONCAT('000',e.examinationRoomNum),3)  )FROM examinationroom e  WHERE id = {id} ", args);
        if (null == obj) {
            return null;
        }
        return obj.toString();
    }

    public Integer getNumsOfStudentByExamroomId(String id) throws Exception {
        Map args = StreamMap.create().put("id", (Object) id);
        Object obj = this.dao2._queryObject("SELECT COUNT(1) FROM examinationnum WHERE examinationRoomNum = {id}", args);
        if (null == obj) {
            return null;
        }
        return Integer.valueOf(Integer.parseInt(obj.toString()));
    }

    public boolean authScoreExists(String exampaperNum, String studentId, String page) throws Exception {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("page", (Object) page);
        if (this.dao2._queryInt("SELECT COUNT(1) FROM score WHERE examPaperNum={exampaperNum} AND studentId = {studentId} AND page={page} ", args).intValue() > 0) {
            return true;
        }
        return false;
    }

    public boolean authexamineeNumExists(String exam, String grade, String examroom, String examineeNum) throws Exception {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade).put("examroom", (Object) examroom).put("examineeNum", (Object) examineeNum);
        if (this.dao2._queryInt("SELECT COUNT(1) FROM examinationnum WHERE examNum={exam} AND gradeNum={grade} AND examinationRoomNum={examroom} AND examineeNum={examineeNum} ", args).intValue() > 0) {
            return true;
        }
        return false;
    }

    public Integer updateAnswerAndScoreToDB(String epNum, String questionNum, String answer, String studentId, double score, String scoreId, int val) {
        String isException;
        String isModify;
        Map args = StreamMap.create().put("score", (Object) Double.valueOf(score)).put("answer", (Object) answer).put("scoreId", (Object) scoreId);
        if (null == answer || answer.equals("null") || answer.equals("")) {
            isException = " ,isException='0' ";
            isModify = " ,isModify='F' ";
        } else {
            isException = " ,isException='1' ";
            isModify = " ,isModify='T' ";
        }
        String regMinToMaxStr = "";
        if (StrUtil.isNotEmpty(answer)) {
            String regResult = this.dao2._queryStr("select regResult from objectivescore where id={scoreId}", args);
            if (StrUtil.isNotEmpty(regResult)) {
                int[] regMinMax = CsUtils.getRegMinOrMaxResult(answer, regResult);
                int regMin = regMinMax[0];
                int regMax = regMinMax[1];
                BigDecimal regMinToMax_new = BigDecimal.valueOf(0L);
                if (regMax > 0) {
                    regMinToMax_new = Convert.toBigDecimal(Integer.valueOf(regMin)).divide(Convert.toBigDecimal(Integer.valueOf(regMax)), 2, RoundingMode.HALF_UP);
                }
                args.put("regMinToMax_new", regMinToMax_new);
                regMinToMaxStr = ",regMinToMax={regMinToMax_new} ";
            }
        }
        String sql = "UPDATE objectivescore SET questionScore={score} , answer={answer} " + isModify + isException + regMinToMaxStr + " WHERE id={scoreId} ";
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    public List<GeneralCorrectData> getGeneralCorrectData(String exam, String subject, String grade, String school) {
        Exampaper exampaperObj = getExampaperInfo(exam, subject, grade);
        String examPaperNum = exampaperObj.getExamPaperNum().toString();
        String totalPage = exampaperObj.getTotalPage();
        if (null == examPaperNum) {
            return null;
        }
        String sql = "SELECT s.subjectName, e.examinationRoomName,en.count numOfPeople,en.count * " + totalPage + " numOfImage,r.count numOfExistImage,e.id examinationRoomNum,'" + examPaperNum + "' examPaperNum,s.subjectNum ,sch.testingCentreName schoolName,sch.id schoolNum  FROM(SELECT COUNT(1) count,examinationRoomNum,examPaperNum,testingCentreId  FROM regexaminee WHERE examPaperNum={examPaperNum} group by testingCentreId,examinationRoomNum   ) r  LEFT JOIN (SELECT id,examinationRoomNum,examinationRoomName,testingCentreId  FROM examinationroom WHERE examNum={exam} AND gradeNum={grade} ) e ON r.examinationRoomNum = e.id   LEFT JOIN (SELECT COUNT(id) count,examinationRoomNum,testingCentreId   FROM examinationnum WHERE examNum={exam}  AND gradeNum={grade}  and subjectNum={subject} GROUP BY testingCentreId,examinationRoomNum)en ON r.examinationRoomNum=en.examinationRoomNum  LEFT JOIN (SELECT subjectNum,subjectName FROM `subject` WHERE subjectNum={subject}) s ON 1=1  LEFT JOIN testingcentre sch ON sch.id = e.testingCentreId   ORDER BY sch.testingCentreNum,e.examinationRoomNum ";
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("exam", (Object) exam).put("grade", (Object) grade).put("subject", (Object) subject);
        return this.dao2._queryBeanList(sql, GeneralCorrectData.class, args);
    }

    public String getScoreModelByExam(String exam, String type) {
        String sql = "SELECT scoreModel FROM exam WHERE examNum={exam} ";
        if (null != type && type.equals("")) {
            sql = "SELECT e.scoreModel FROM exampaper ep LEFT JOIN  exam  e ON e.examNum = ep.examNum WHERE ep.examPaperNum={exam} ";
        }
        Map args = StreamMap.create().put("exam", (Object) exam);
        Object obj = this.dao2._queryObject(sql, args);
        if (null == obj) {
            return Const.score_plus;
        }
        return obj.toString();
    }

    public Define getDefineByExampaperNumAndQuestionNum(int exampaperNum, String questionNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) Integer.valueOf(exampaperNum)).put("questionNum", (Object) questionNum);
        Define r = (Define) this.dao2._queryBean("SELECT  * FROM define WHERE examPaperNum={exampaperNum} AND id={questionNum} ", Define.class, args);
        if (null == r) {
            r = (Define) this.dao2._queryBean("SELECT  id,examPaperNum,pid,questionNum,category,fullScore,errorRate,page,cross_page,orderNum,'1'questiontype FROM subdefine WHERE examPaperNum={exampaperNum} AND id={questionNum} ", Define.class, args);
        }
        return r;
    }

    public List<Exampaper> getExamPaperNums(String examNum, String gradeNum) {
        new ArrayList();
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        List _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT examPaperNum,subjectNum,gradeNum    FROM exampaper   WHERE 1=1 AND examNum={examNum} AND gradeNum={gradeNum} ", Exampaper.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    public Integer re_Recognized_Objective(String examNum, String subjectNum, String gradeNum, String schoolNum, String examRoomNum, String studentId, String optionType, String questionNum, String optioncount, String optionvalue, String reg_type, String option_val, String exePath, String loginUser) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        String templateType = this.dao2._queryStr("SELECT templateType FROM exampaper WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum} ", args);
        Reg_Th_Log reg_Th_Log = new Reg_Th_Log();
        if (null != templateType && "1".equals(templateType)) {
            reg_Th_Log.setTemplateType(templateType);
        } else {
            reg_Th_Log.setTemplateType("0");
        }
        reg_Th_Log.setVal(option_val);
        reg_Th_Log.setType("1");
        reg_Th_Log.setInsertUser(loginUser);
        reg_Th_Log.setInsertDate(DateUtil.getCurrentTime());
        this.dao2.save(reg_Th_Log);
        return 1;
    }

    public Integer recognized_Objective_openExe(String examNum, String subjectNum, String gradeNum, String schoolNum, String examRoomNum, String studentId, String questionNum, String reg_type, String option_val, String exePath, String loginUser) {
        if (schoolNum == null || "".equals(schoolNum) || "-1".equals(schoolNum)) {
            schoolNum = "null";
        }
        if (examRoomNum == null || "".equals(examRoomNum) || "-1".equals(examRoomNum)) {
            examRoomNum = "null";
        }
        if (studentId == null || "".equals(studentId)) {
            studentId = "null";
        }
        if (questionNum == null || "".equals(questionNum)) {
            questionNum = "null";
        }
        String exeP = exePath + "ReRecObjective.exe";
        String exeCommand = exeP + " " + (exePath + "WEB-INF/classes/c3p0.properties") + " " + examNum + " " + schoolNum + " " + subjectNum + " " + gradeNum + " " + examRoomNum + " " + studentId + " " + questionNum + " " + reg_type + " " + option_val;
        this.log.info("(自定义阈值)重新识别--exeCommand:" + exeCommand);
        Runtime rn = Runtime.getRuntime();
        Thread b = null;
        Thread c = null;
        try {
            try {
                Process p = rn.exec(exeCommand);
                BufferedInputStream is1 = new BufferedInputStream(p.getInputStream());
                BufferedInputStream is2 = new BufferedInputStream(p.getErrorStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is1));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(is1));
                4 r0 = new 4(this, br);
                r0.start();
                5 r02 = new 5(this, br2);
                r02.start();
                if (p.waitFor() != 0) {
                    if (p.exitValue() == 1) {
                        this.log.error("p.exitValue() == 1--命令执行失败!");
                    }
                    try {
                        r0.stop();
                        r02.stop();
                        return null;
                    } catch (Exception e) {
                        this.log.error("重新识别", e);
                        e.printStackTrace();
                        return null;
                    }
                }
                is1.close();
                is2.close();
                br.close();
                br2.close();
                Integer valueOf = Integer.valueOf(p.exitValue());
                try {
                    r0.stop();
                    r02.stop();
                    return valueOf;
                } catch (Exception e2) {
                    this.log.error("重新识别", e2);
                    e2.printStackTrace();
                    return null;
                }
            } catch (Exception e3) {
                this.log.error("重新识别，执行报错", e3);
                try {
                    b.stop();
                    c.stop();
                    return null;
                } catch (Exception e4) {
                    this.log.error("重新识别", e4);
                    e4.printStackTrace();
                    return null;
                }
            }
        } catch (Throwable th) {
            try {
                b.stop();
                c.stop();
                throw th;
            } catch (Exception e5) {
                this.log.error("重新识别", e5);
                e5.printStackTrace();
                return null;
            }
        }
    }

    public List<Reg_Th_Log> getRe_th_log(int size) {
        Map args = StreamMap.create().put("size", (Object) Integer.valueOf(size));
        new ArrayList();
        List _queryBeanList = this.dao2._queryBeanList("SELECT *  FROM reg_th_log   ORDER BY insertDate DESC  LIMIT 0,{size} ", Reg_Th_Log.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    public Integer saveRe_th_log_description(String re_th_log_id, String des) {
        Map args = new HashMap();
        args.put("des", des);
        args.put("re_th_log_id", re_th_log_id);
        this.dao2._execute("UPDATE reg_th_log SET description={des}  WHERE id={re_th_log_id} ", args);
        new ArrayList();
        List li = this.dao2._queryBeanList("SELECT *  FROM reg_th_log  WHERE 1=1 AND id={re_th_log_id}  AND description={des} ", Reg_Th_Log.class, args);
        if (li != null && li.size() == 1) {
            return 1;
        }
        return 0;
    }

    public List<Task> getQuesGroup(Integer exampaperNum, String groupnum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("groupnum", (Object) groupnum).put("task_insertUser_defalt", (Object) "-1").put("TRUE", (Object) "T");
        return this.dao2._queryBeanList("SELECT h.*,def.questionNum realQuestionNum,if(tu.updatetime is null||TIMESTAMPDIFF(MINUTE,tu.updatetime,NOW())>3,'否','是') ext3   FROM (select u.realname updateUser,t.insertUser,t.questionNum,IFNULL(ROUND(AVG(t.questionScore),2),0) ext1 ,COUNT(t.id) ext2,tch.teacherNum,\tsch.schoolName  from(SELECT id,questionNum,questionScore,exampaperNum,groupNum,insertUser   FROM task WHERE exampaperNum={exampaperNum} AND groupNum={groupnum}  AND insertUser!={task_insertUser_defalt}  AND `status`={TRUE}   ) t   LEFT JOIN `user` u ON t.insertUser=u.id  LEFT JOIN teacher tch ON tch.id= u.userid  LEFT JOIN school sch ON sch.id = u.schoolnum group by t.questionNum,t.insertUser   ) h   LEFT JOIN (SELECT insertUser,COUNT(1) ext3 FROM task WHERE exampaperNum = {exampaperNum} AND groupNum = {groupnum} AND isException = 'Y' AND STATUS = 'T' GROUP BY insertUser) yt ON yt.insertUser = h.insertUser LEFT JOIN (select MAX(t.updateTime) updateTime ,t.groupNum,t.insertUser from task t  where t.status='T' and t.exampaperNum = {exampaperNum} and t.groupNum={groupnum} GROUP BY t.insertUser) tu ON  h.insertUser = tu.insertUser LEFT JOIN (SELECT id,questionNum,orderNum  FROM define  WHERE examPaperNum={exampaperNum} AND isParent='0'  UNION ALL  SELECT subdef.id,subdef.questionNum,subdef.orderNum  FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE def.examPaperNum={exampaperNum} AND def.isParent='1'  )def  ON h.questionNum=def.id  ORDER BY h.insertUser,h.ext2 DESC,def.orderNum", Task.class, args);
    }

    public String getGroupInfo(Integer examPaperNum, String groupnum) {
        Map args = new HashMap();
        args.put("groupnum", groupnum);
        args.put("examPaperNum", examPaperNum);
        String str = "";
        String choosename = this.dao2._queryStr("SELECT choosename from define WHERE id={groupnum} UNION   SELECT d.choosename from define d    LEFT JOIN subdefine sb on sb.pid=d.id  WHERE sb.id={groupnum} ", args);
        if (!"s".equals(choosename)) {
            str = this.dao2._queryStr("SELECT if(f.name is null,g.id,f.name) ext1 from( \tSELECT d.id,d.questionNum,if(d.choosename='s',d.id,d.choosename) num \t\t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id \t\t\tWHERE d.id={groupnum} and d.questiontype=1 \t\t\tunion \t\t\tSELECT sb.id,sb.questionNum,CONCAT(d1.id,sb.orderNum) num \t\t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id \t\t\tWHERE sb.id ={groupnum} and sb.questiontype=1 \t)g left JOIN( \t\tSELECT GROUP_CONCAT(d.questionNum) name,GROUP_CONCAT(d.questionNum),d.num from( \t\t\tSELECT d.id,d.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,d.orderNum b,0 c,d.choosename num \t\t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id \t\t\tWHERE d.examPaperNum={examPaperNum} and d.questiontype=1 \t\t\tunion \t\t\tSELECT sb.id,sb.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,sb.orderNum b, d.orderNum c,CONCAT(d1.id,sb.orderNum) num \t\t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id \t\t\tWHERE sb.examPaperNum ={examPaperNum} and sb.questiontype=1 \t\t) d GROUP BY d.num \t)f on g.num=f.num", args);
        }
        return str;
    }

    public List<Task> panfendetail(Integer examPaperNum, String groupnum) {
        String sql1;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("groupnum", groupnum);
        String sql2 = "";
        String sql22 = "";
        String sql21 = "";
        String enforce = this.cis.fenzu(String.valueOf(examPaperNum));
        Object[] qinfo = this.dao2._queryArray("SELECT GROUP_CONCAT(t.id) from( \tSELECT d.id,if(d.num='s' or d.num is null,d.id,d.num) num from( \t\tSELECT d.id,d.questionNum,d.choosename num \t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id \t\t \t\twhere d.examPaperNum={examPaperNum}   union  \t\tSELECT sb.id,sb.questionNum,CONCAT(d1.id,sb.orderNum) num  \t\tfrom subdefine sb left join define d  on sb.pid=d.id  \t\tLEFT JOIN define d1 on d.choosename=d1.id \t\t \t\twhere sb.examPaperNum={examPaperNum}) d  \t)t  where t.num={groupnum}  GROUP BY t.num", args);
        String newGroupNum = String.valueOf(qinfo[0]);
        args.put("newGroupNum", newGroupNum);
        if ("0".equals(enforce)) {
            sql1 = "select if(count/count1>=0.8,'T','F') from( \tselect count(1) count from task where groupNum in ({newGroupNum[]}) and status='T' \t)t LEFT JOIN( \t\tselect count(1) count1 from task where groupNum in ({newGroupNum[]}) \t)t1 on 1=1";
            sql2 = "select q.userNum,cast(count(t.scoreid) as char) ext1 from ( \tselect userNum from  questiongroup_user where groupNum in ({newGroupNum[]}) \t)  q left join ( \t\tselect scoreid,GROUP_CONCAT(concat('d',UPDATEuser,'d')) UPDATEuser ,GROUP_CONCAT(STATUS) aa from task where groupNum in ({newGroupNum[]}) GROUP BY scoreid \t) t on POSITION(concat('d',q.userNum,'d') in t.UPDATEuser)=0 and POSITION( 'F' in t.aa)>0 \tGROUP BY q.userNum";
        } else {
            sql1 = "SELECT if(POSITION('T' IN GROUP_CONCAT(t3.statu)),'T','F') from( \tselect t.schoolGroupNum,if(count/count1>=0.8,'T','F') statu from( \t\t\tselect slg.schoolGroupNum,count(1) count from task t INNER JOIN student s on t.studentId=s.id \t\t\tINNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum \t\t\twhere t.groupNum in ({newGroupNum[]}) and t.status='T' GROUP BY slg.schoolGroupNum \t\t)t LEFT JOIN( \t\t\tselect slg.schoolGroupNum,count(1) count1 from task t INNER JOIN student s on t.studentId=s.id \t\t\tINNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum \t\t\twhere t.groupNum in ({newGroupNum[]}) GROUP BY slg.schoolGroupNum \t\t)t1 on t.schoolGroupNum=t1.schoolGroupNum  \t)t3";
            sql21 = "SELECT q.userNum,sc.schoolGroupNum from      (select userNum from  questiongroup_user where groupNum in ({newGroupNum[]}) )q   left join  `user` u on u.id=q.userNum   LEFT join schoolgroup sc on sc.schoolNum=u.schoolNum ";
            sql22 = "select t.UPDATEuser,t.aa,sc.schoolgroupnum from (select scoreid,GROUP_CONCAT(concat('d',UPDATEuser,'d')) UPDATEuser ,GROUP_CONCAT(STATUS) aa  from task where groupNum in ({newGroupNum[]}) GROUP BY      scoreid  ) t  LEFT join score s  on s.id=t.scoreid LEFT join schoolgroup sc on sc.schoolNum=s.schoolNum WHERE POSITION( 'F' in t.aa)>0 ";
        }
        Map<String, String> infoMap = new HashMap<>();
        infoMap.put("examPaperNum", examPaperNum + "");
        infoMap.put("groupNum", groupnum);
        infoMap.put("orderName", "groupProgressWeb");
        List list1 = this.teacherAppScoreDao.getGroupDetail(infoMap, "", null);
        String makType = this.dao2._queryStr("SELECT makType from questiongroup_mark_setting where groupNum in ({newGroupNum[]}) ", args);
        if ("1".equals(makType)) {
            String statu = this.dao2._queryStr(sql1, args);
            if ("T".equals(statu)) {
                if ("0".equals(enforce)) {
                    Map<String, String> map = this.dao2._queryOrderMap(sql2, TypeEnum.StringString, args);
                    for (int i = 0; i < list1.size(); i++) {
                        Task task = list1.get(i);
                        task.setUnread(map.get(task.getId()));
                    }
                } else {
                    Long.valueOf(System.currentTimeMillis());
                    List<Map<String, Object>> list21 = this.dao2._queryMapList(sql21, null, args);
                    List<Map<String, Object>> list22 = this.dao2._queryMapList(sql22, null, args);
                    Map<String, String> map2 = new HashMap<>();
                    Long.valueOf(System.currentTimeMillis());
                    for (int i2 = 0; i2 < list21.size(); i2++) {
                        String usernum = list21.get(i2).get("userNum").toString();
                        String schoolGroupNum1 = list21.get(i2).get("schoolGroupNum").toString();
                        String flag = "0";
                        int j = 0;
                        while (true) {
                            if (j >= list22.size()) {
                                break;
                            }
                            String updateuser = list22.get(j).get("UPDATEuser").toString();
                            String schoolGroupNum2 = list22.get(j).get("schoolGroupNum").toString();
                            if (updateuser.indexOf(usernum) != -1 || !schoolGroupNum1.equals(schoolGroupNum2)) {
                                j++;
                            } else {
                                map2.put(usernum, "1");
                                flag = "1";
                                break;
                            }
                        }
                        if (flag.equals("0")) {
                            map2.put(usernum, "0");
                        }
                    }
                    for (int i3 = 0; i3 < list1.size(); i3++) {
                        Task task2 = list1.get(i3);
                        task2.setUnread(map2.get(task2.getId()));
                    }
                }
            }
        }
        List tempList1 = new ArrayList();
        List tempList2 = new ArrayList();
        for (int i4 = 0; i4 < list1.size(); i4++) {
            Task task3 = list1.get(i4);
            if (task3.getUnread() == null) {
                tempList1.add(task3);
            } else {
                tempList2.add(task3);
            }
        }
        for (int j2 = 0; j2 < tempList2.size(); j2++) {
            tempList1.add((Task) tempList2.get(j2));
        }
        return list1;
    }

    public List<Task> qscoreta(Integer examPaperNum, String groupnum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("FALSE", (Object) "F").put("task_insertUser_defalt", (Object) "-1").put("groupnum", (Object) groupnum);
        return this.dao2._queryBeanList("SELECT sch.schoolName,tch.teacherNum, u.realname ext1,u.username ext3,CAST(count(1)/count1 AS signed) ext2,t.insertUser  FROM (SELECT insertUser,questionNum   FROM task WHERE exampaperNum={examPaperNum} AND `status`={FALSE}  AND insertUser!={task_insertUser_defalt}  AND groupNum={groupnum}   )t  LEFT JOIN(SELECT count(questionNum) count1,questionNum FROM questiongroup_question   WHERE exampaperNum={examPaperNum} AND groupNum={groupnum}    )qq  ON t.questionNum=qq.questionNum   LEFT JOIN `user` u ON u.id=t.insertUser   LEFT JOIN teacher tch ON tch.id = u.userid  LEFT JOIN school sch ON sch.id = u.schoolnum  GROUP BY u.id", Task.class, args);
    }

    public List<Task> qscoreta2(Integer examPaperNum, String groupnum) {
        String sql;
        String enforce = systemService.fenzu(String.valueOf(examPaperNum));
        if ("0".equals(enforce)) {
            sql = "SELECT t.insertUser ext3,s.schoolName ext2,u.username teacherNum,u.realname teachername,cast(count(1)/count(DISTINCT(t.questionNum)) as signed) ext0,ifnull(qu.id,'F') ext1,t.groupNum,t.exampaperNum from task t  LEFT JOIN questiongroup_user qu on t.groupNum=qu.groupNum and t.insertUser=qu.userNum LEFT JOIN user u on t.insertUser=u.id  LEFT JOIN school s on u.schoolnum=s.id where t.groupNum={groupnum} and t.status='F' GROUP BY t.insertUser ORDER BY ifnull(qu.id,'F') desc ";
        } else {
            sql = "SELECT t.ext3,if(t.ext3='-1','未分发',t.ext2) ext2,if(t.ext3='-1','未分发',t.teacherNum) teacherNum,if(t.ext3='-1','未分发',t.teachername) teachername,sum(t.ext0) ext0,if(locate('F',GROUP_CONCAT(t.ext1))=1,'F',-9) ext1,t.groupNum,t.exampaperNum from( select t.insertUser ext3,s.schoolName ext2,u.username teacherNum,u.realname teachername,cast(count(1)/count(DISTINCT(t.questionNum)) as signed) ext0, case when (qu.id is null ||slg2.schoolGroupNum<>slg.schoolGroupNum) then 'F' else qu.id end ext1 ,t.groupNum,t.exampaperNum  from task t  LEFT JOIN questiongroup_user qu on t.groupNum=qu.groupNum and t.insertUser=qu.userNum LEFT JOIN user u on t.insertUser=u.id  LEFT JOIN school s on u.schoolnum=s.id  LEFT JOIN schoolgroup slg2 on u.schoolNum=slg2.schoolNum LEFT JOIN student stu on t.studentId=stu.id LEFT JOIN schoolgroup slg on stu.schoolNum=slg.schoolNum where t.groupNum={groupnum} and t.status='F' GROUP BY t.insertUser,slg.schoolGroupNum )t GROUP BY t.ext3 ";
        }
        Map args = StreamMap.create().put("groupnum", (Object) groupnum);
        return this.dao2._queryBeanList(sql, Task.class, args);
    }

    public Integer clearUserQues(Integer examPaperNum, String groupNum, String userNum, String quesNum, String loginUserId, String rwCount) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("task_insertUser_defalt", "-1");
        args.put("FALSE", "F");
        args.put("examPaperNum", examPaperNum);
        args.put("userNum", userNum);
        Object questionCountoo = this.dao2._queryObject("select count(1) from questiongroup_question where groupNum={groupNum} ", args);
        int questionCount = questionCountoo == null ? 0 : Integer.valueOf(questionCountoo.toString()).intValue();
        int rwCount1 = Integer.parseInt(rwCount) * questionCount;
        args.put("rwCount1", Integer.valueOf(rwCount1));
        Examlog examlog = new Examlog();
        examlog.setExampaperNum(examPaperNum);
        examlog.setOperate("回收题组");
        examlog.setInsertUser(loginUserId);
        examlog.setInsertDate(DateUtil.getCurrentDay());
        examlog.setDescription(userNum + ",题组号" + groupNum + ",数目：" + rwCount);
        this.dao2.save(examlog);
        this.dao2._execute("UPDATE task SET insertUser={task_insertUser_defalt} ,`status`={FALSE},porder=0,fenfaDate=0,updateTime='',xuankaoqufen=1   WHERE  exampaperNum={examPaperNum}  AND groupNum={groupNum}  AND insertUser={userNum}  AND `status`={FALSE}   LIMIT {rwCount1} ", args);
        return Integer.valueOf(Integer.parseInt(rwCount));
    }

    public List searchInQuesImage(String examPaperNum, String studentId, String questionNum, String school, String page, String examinationRoomNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put(Const.EXPORTREPORT_studentId, (Object) studentId).put(License.SCHOOL, (Object) school).put("page", (Object) page).put("examinationRoomNum", (Object) examinationRoomNum);
        return this.dao2._queryBeanList("SELECT DISTINCT id  FROM questionimage WHERE 1=1   AND examPaperNum={examPaperNum} AND studentId={studentId}  AND schoolNum={school} AND page={page} AND examinationRoomNum={examinationRoomNum}   LIMIT 1", Questionimage.class, args);
    }

    public String getUploadFileName() {
        return this.uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public String getUploadContentType() {
        return this.uploadContentType;
    }

    public void setUploadContentType(String uploadContentType) {
        this.uploadContentType = uploadContentType;
    }

    public Integer addToClipErrorMethod(String scoreId, String regId, String examPaperNum, String loginUserNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("scoreId", scoreId);
        args.put("regId", regId);
        int aa = 0;
        List scoreList = this.dao2._queryBeanList("SELECT sc.id scId,sc.regId,ce.regId clipRegId,sc.examPaperNum  FROM (SELECT examPaperNum,schoolNum,studentId,examinationRoomNum,page,classNum,id,regId   FROM score sc WHERE examPaperNum={examPaperNum}  AND  id={scoreId}    UNION ALL   SELECT examPaperNum,schoolNum,studentId,examinationRoomNum,page,classNum,id,regId    FROM objectivescore  WHERE examPaperNum={examPaperNum}  AND  id={scoreId}     )sc   LEFT JOIN (SELECT id,examPaperNum,regId FROM cliperror  WHERE examPaperNum={examPaperNum}  AND regId={regId}  ) ce  ON sc.regId=ce.regId   ", Score.class, args);
        if (null != scoreList && scoreList.size() > 0) {
            if (null == ((Score) scoreList.get(0)).getClipRegId() || "".equals(((Score) scoreList.get(0)).getClipRegId())) {
                ClipError clipError = new ClipError();
                clipError.setExamPaperNum(((Score) scoreList.get(0)).getExamPaperNum());
                clipError.setRegId(((Score) scoreList.get(0)).getRegId());
                clipError.setInsertUser(loginUserNum);
                clipError.setInsertDate(DateUtil.getCurrentTime());
                clipError.setUpdateUser(loginUserNum);
                clipError.setUpdateDate(DateUtil.getCurrentTime());
                aa = this.dao2.save(clipError);
            } else {
                aa = this.dao2._execute("UPDATE cliperror SET `status`=NULL WHERE examPaperNum={examPaperNum} AND regId={regId} ", args);
            }
        }
        return Integer.valueOf(aa);
    }

    public Integer removeFromClipErrorMethod(String regId, String examPaperNum, String loginUserNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("regId", (Object) regId);
        return Integer.valueOf(this.dao2._execute("DELETE FROM cliperror WHERE examPaperNum={examPaperNum}  AND  regId={regId}  ", args));
    }

    public List searchClipStuMethod(String examPaperNum, String examRoom, String schoolNum, String gradeNum, String loginUserNum) {
        String sql = "SELECT DISTINCT ce.`status`,IFNULL(stu.studentName,'---') studentName,IFNULL(stu.studentId,'---') realStudentId,reg.*    FROM (SELECT id,regId,examPaperNum,`status`  FROM cliperror WHERE examPaperNum={examPaperNum} ) ce   LEFT JOIN (SELECT id,examPaperNum,schoolNum,studentId,examinationRoomNum,page,classNum,cNum,type   FROM regexaminee  WHERE examPaperNum={examPaperNum}  ";
        if (null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql = sql + "AND examinationRoomNum={examRoom}     ";
        } else if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            sql = sql + "AND schoolNum={schoolNum}    ";
        }
        String sql2 = sql + ") reg ON ce.regId=reg.id    LEFT JOIN (SELECT id,studentId,studentNum,studentName FROM student WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} ) stu ON reg.studentId=stu.id  WHERE reg.id IS NOT NULL";
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("examRoom", (Object) examRoom).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryBeanList(sql2, RegExaminee.class, args);
    }

    public List searchClipSuccess_failure_page(String examPaperNum, String examRoom, String schoolNum) {
        List returnList = new ArrayList();
        new ArrayList();
        new ArrayList();
        new ArrayList();
        String sql3_end = ") ce   LEFT JOIN (SELECT id,examPaperNum,examinationRoomNum,page  FROM regexaminee   WHERE examPaperNum={examPaperNum}   ";
        if (null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql3_end = sql3_end + "AND examinationRoomNum={examRoom}    ";
        } else if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            sql3_end = sql3_end + "AND schoolNum={schoolNum}    ";
        }
        String sql3_end2 = sql3_end + ") reg ON ce.regId=reg.id  WHERE reg.id IS NOT NULL   ";
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("clipError_success", (Object) 0).put("clipError_failure", (Object) 0).put("examRoom", (Object) examRoom).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        List noClipList = this.dao2._queryBeanList("SELECT  DISTINCT COUNT(IFNULL(ce.`status`,2)) ext1,reg.page ext2   FROM (SELECT id,examPaperNum,regId,`status`  FROM cliperror WHERE examPaperNum={examPaperNum} AND `status` IS NULL  " + sql3_end2, ClipError.class, args);
        List sList = this.dao2._queryBeanList("SELECT  DISTINCT COUNT(IFNULL(ce.`status`,2)) ext1,reg.page ext2   FROM (SELECT id,examPaperNum,regId,`status`  FROM cliperror WHERE examPaperNum={examPaperNum} AND `status`={clipError_success}  " + sql3_end2, ClipError.class, args);
        List fList = this.dao2._queryBeanList("SELECT  DISTINCT COUNT(IFNULL(ce.`status`,2)) ext1,reg.page ext2   FROM (SELECT id,examPaperNum,regId,`status`  FROM cliperror WHERE examPaperNum={examPaperNum} AND `status`={clipError_failure}  " + sql3_end2, ClipError.class, args);
        returnList.add(noClipList);
        returnList.add(sList);
        returnList.add(fList);
        return returnList;
    }

    public Integer batchAddClipError(String examNum, String subjectNum, String gradeNum, String examRoomNum, String schoolNum, String loginUserNum) {
        Map args = new HashMap();
        args.put("examRoomNum", examRoomNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        String examPaperNum = getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        args.put("examPaperNum", examPaperNum);
        String sql = "SELECT reg.id,ce.regId   FROM (SELECT id FROM regexaminee WHERE examPaperNum={examPaperNum}   ";
        if (null != examRoomNum && !"".equals(examRoomNum) && !"-1".equals(examRoomNum)) {
            sql = sql + "AND examinationRoomNum={examRoomNum}  ";
        } else if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            sql = sql + "AND schoolNum={schoolNum}  ";
        }
        List clipErrorList = new ArrayList();
        List regList = this.dao2._queryBeanList(sql + "  ) reg     LEFT JOIN (SELECT id,examPaperNum,regId FROM cliperror WHERE examPaperNum={examPaperNum} ) ce    ON reg.id=ce.regId   WHERE ce.regId IS NULL", RegExaminee.class, args);
        if (null != regList && regList.size() > 0) {
            for (int i = 0; i < regList.size(); i++) {
                if (null != ((RegExaminee) regList.get(i)).getId() && !"".equals(((RegExaminee) regList.get(i)).getId())) {
                    ClipError clipError = new ClipError();
                    clipError.setExamPaperNum(Integer.valueOf(Integer.parseInt(examPaperNum)));
                    clipError.setRegId(((RegExaminee) regList.get(i)).getId());
                    clipError.setInsertUser(loginUserNum);
                    clipError.setInsertDate(DateUtil.getCurrentTime());
                    clipError.setUpdateUser(loginUserNum);
                    clipError.setUpdateDate(DateUtil.getCurrentTime());
                    clipErrorList.add(clipError);
                }
            }
        }
        if (null != clipErrorList && clipErrorList.size() > 0) {
            return Integer.valueOf(this.dao2.batchSave(clipErrorList).length);
        }
        return 1;
    }

    public Integer batchRemoveClipError(String examNum, String subjectNum, String gradeNum, String examRoomNum, String schoolNum, String loginUserNum) {
        Map args = new HashMap();
        args.put("examRoomNum", examRoomNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        String examPaperNum = getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        args.put("examPaperNum", examPaperNum);
        String sql = "SELECT reg.id,ce.regId    FROM (SELECT id FROM regexaminee WHERE examPaperNum={examPaperNum}    ";
        if (null != examRoomNum && !"".equals(examRoomNum) && !"-1".equals(examRoomNum)) {
            sql = sql + "AND examinationRoomNum={examRoomNum}   ";
        } else if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            sql = sql + "AND schoolNum={schoolNum}   ";
        }
        List clipErrorList = new ArrayList();
        List regList = this.dao2._queryBeanList(sql + "  ) reg    LEFT JOIN (SELECT id,examPaperNum,regId FROM cliperror WHERE examPaperNum={examPaperNum} ) ce    ON reg.id=ce.regId   WHERE ce.regId IS NOT NULL", RegExaminee.class, args);
        if (null != regList && regList.size() > 0) {
            for (int i = 0; i < regList.size(); i++) {
                String regid = ((RegExaminee) regList.get(i)).getRegId();
                if (null != regid && !"".equals(regid)) {
                    String ids = "regid" + i;
                    args.put(ids, regid);
                    String sql_delete = "DELETE FROM cliperror WHERE regId={" + ids + "}";
                    clipErrorList.add(sql_delete);
                }
            }
        }
        if (null != clipErrorList && clipErrorList.size() > 0) {
            this.dao2._batchExecute((List<String>) clipErrorList, args);
            return 1;
        }
        return 1;
    }

    public void updateExampaper_score(String totalScore, String analysisscore, String examPaperNum) {
        dao.updateExampaper_countScore(totalScore, analysisscore, examPaperNum);
    }

    public Exampaper getTotalScoreCount(String exam, String subject, String grade) {
        String exampaperNum = getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
        if (null == exampaperNum || exampaperNum.equals("")) {
            return null;
        }
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return (Exampaper) this.dao2._queryBean("SELECT totalScore,analysisscore FROM exampaper WHERE examPaperNum={exampaperNum} ", Exampaper.class, args);
    }

    public void getAnalysisscoreValue(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        new ArrayList();
        List analyList = this.dao2._queryBeanList("SELECT analysisscore FROM exampaper WHERE examPaperNum={examPaperNum} ", Exampaper.class, args);
        if (((Exampaper) analyList.get(0)).getAnalysisscore().floatValue() <= 0.0f) {
        }
    }

    public List<AjaxData> getExportStudentClassList(String exam, String school, String grade, String subjectType, String jie) {
        ArrayList arrayList = new ArrayList();
        new ArrayList();
        List cc = new ArrayList();
        new ArrayList();
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("jie", jie);
        String sql = "SELECT e.examPaperNum FROM exampaper e WHERE 1=1 ";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            sql = sql + " AND e.examNum={exam} ";
        }
        if (null != grade && !grade.equals("") && !grade.equals("-1")) {
            sql = sql + " AND e.gradeNum={grade} ";
        }
        List<?> _queryBeanList = this.dao2._queryBeanList(sql, Exampaper.class, args);
        for (int i = 0; i < _queryBeanList.size(); i++) {
            String exampapernu = "ExamPaperNum" + i;
            String sql1 = " SELECT s.classNum num,s.className name FROM classexam cm LEFT JOIN class s ON s.classNum=cm.classNum WHERE cm.examPaperNum={" + exampapernu + "} AND s.jie={jie} ";
            args.put(exampapernu, ((Exampaper) _queryBeanList.get(i)).getExamPaperNum());
            arrayList.add(sql1);
        }
        cc.add(arrayList);
        return cc;
    }

    public String getScoreId(String examPaperNum, String studentID) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("studentID", (Object) studentID);
        new ArrayList();
        List ScoreId = this.dao2._queryColList("SELECT id FROM score where studentId = {examPaperNum} and questionNum = {studentID} ", args);
        if (ScoreId.isEmpty()) {
            return "notfind";
        }
        return String.valueOf(ScoreId.get(0));
    }

    public List<String> getQuesionImage_scoreIdList(String examPaperNum, String studentID, String questionNum, String scoreId, String cross_page) {
        String p0;
        String p1;
        if (null == scoreId) {
            return null;
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("scoreId", scoreId);
        args.put("questionNum", questionNum);
        args.put("studentID", studentID);
        List<String> list = new ArrayList<>();
        if (null == questionNum || questionNum.equals("")) {
            String getsql = "SELECT examPaperNum,studentId,page,questionNum FROM score WHERE ";
            if (null != examPaperNum && !examPaperNum.equals("")) {
                getsql = getsql + " exampaperNum = {examPaperNum} and ";
            }
            this.dao2._queryArray(getsql + "id={scoreId} ", args);
        }
        String questionType = "0";
        Object[] obj = this.dao2._queryArray("select questiontype,cross_page from define where id = {questionNum} union all  select '1' questiontype,cross_page from subdefine where id = {questionNum} ", args);
        if (null != obj) {
            if (obj[0] == null) {
                p0 = "0";
            } else {
                p0 = obj[0].toString();
            }
            if (obj[1] == null) {
                p1 = "F";
            } else {
                p1 = obj[1].toString();
            }
            questionType = null == obj[0] ? "0" : p0;
            String str = null == obj[0] ? "F" : p1;
        }
        if (null != questionType && questionType.equals("0")) {
            list.add(scoreId);
            return list;
        }
        if (null != cross_page && cross_page.equals("F")) {
            list.add(scoreId);
            return list;
        }
        if (null == studentID || studentID.equals("")) {
            String getsql2 = "SELECT examPaperNum,studentId,page,questionNum FROM score WHERE ";
            if (null != examPaperNum && !examPaperNum.equals("")) {
                getsql2 = getsql2 + " exampaperNum = {examPaperNum} and ";
            }
            Object[] obj2 = this.dao2._queryArray(getsql2 + "id={scoreId} ", args);
            String obj3 = (obj2 == null || null == obj2[1]) ? "" : obj2[1].toString();
        }
        return this.dao2._queryColList("SELECT id FROM score WHERE examPaperNum={examPaperNum}  AND studentId={studentID} AND questionNum={questionNum} order by page asc", String.class, args);
    }

    public byte[] getobjectscore(String studnetId, String questionNum) {
        byte[] aa = new byte[0];
        Map args = StreamMap.create().put("questionNum", (Object) questionNum).put("studnetId", (Object) studnetId);
        List pizhu = this.dao2._queryColList("SELECT img FROM `remarkimg` where scoreId =(select id from score where questionNum = {questionNum} and studentId = {studnetId} and continued = 'T') ORDER BY insertDate desc limit 1 ", args);
        if (!pizhu.isEmpty()) {
            return (byte[]) pizhu.get(0);
        }
        return aa;
    }

    public byte[] getImageStream(String urlString) {
        return ImageStreamUtil.getImageStream(urlString);
    }

    public List objecterranly(String examNum, String subjectNum, String gradeNum, String schoolNum, String classNum, String studentType, String type, String source) {
        List list = new ArrayList();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ objecterranly(?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, studentType.equals("null") ? "" : studentType);
                pstat.setString(7, type.equals("null") ? "" : type);
                pstat.setString(8, source.equals("null") ? "" : source);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                new ArrayList();
                while (rs.next()) {
                    ObjecterrorAnaly o = new ObjecterrorAnaly();
                    o.setOrder1(rs.getString(1));
                    o.setOrder2(rs.getString(2));
                    o.setName1(rs.getString(3));
                    o.setQuestonNum(rs.getString(4));
                    o.setValue1(rs.getString(5));
                    o.setQuestionScore(rs.getString(6));
                    o.setFullScore(rs.getString(7));
                    o.setQuestionNum(rs.getString(8));
                    list.add(o);
                }
                DbUtils.close(rs, pstat, conn);
                return list;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public boolean Skip() {
        return true;
    }

    public List geterrorStudentList(String examNum, String subjectNum, String gradeNum, String schoolNum, String classNum, String type, String studentType, String source, String questionNum, String sign, String answer) {
        String sql = "select stu.studentName,dsub.answer,if(obj.answer='','空选',obj.answer) answer1 from  (SELECT st.studentId ,s.studentName,st.examPaperNum from studentlevel  st   LEFT JOIN student  s ON s.id=st.studentId  WHERE st.examNum ={examNum}  and st.subjectNum ={subjectNum} AND st.statisticType ={type} AND st.source ={source} and st.studentType= {studentType}  AND st.classNum = {classNum} and st.xuankezuhe='0'  ) stu LEFT JOIN  (SELECT id,questionNum,fullScore,examPaperNum,answer FROM define WHERE   id= {questionNum}  UNION all SELECT subdef.id,subdef.questionNum,subdef.fullScore,subdef.examPaperNum,subdef.answer FROM subdefine subdef   WHERE   subdef.id= {questionNum} )dsub  ON 1=1  LEFT JOIN objectivescore obj on obj.questionNum=dsub.id and obj.studentId= stu.studentId  INNER JOIN illegal ill ON ill.examPaperNum = dsub.examPaperNum AND ill.studentId = stu.studentId  AND ill.type <> 0 AND ill.type <> 1  WHERE  dsub.id= {questionNum} AND obj.questionScore < dsub.fullScore";
        if ("7".equals(sign)) {
            if ("空选".equals(answer)) {
                sql = sql + " and obj.answer ='' ";
            } else {
                sql = sql + " and obj.answer ={answer} ";
            }
        }
        String sql2 = sql + " ORDER BY answer1";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("type", type);
        args.put("source", source);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("questionNum", questionNum);
        args.put("answer", answer);
        return this.dao2._queryBeanList(sql2, Student.class, args);
    }

    public List<AjaxData> getStuExam(String studentId) {
        String sql = "select distinct e.examNum num,e.examName name,e.type ext1,e.examDate ext2 from exam e right join examinationnum en on e.examNum = en.examNum left join student s on s.id = en.studentId  where e.isDelete ='F'  and e.status!={STATUS_EXAM_COMPLETE} and s.studentId = {studentId} ORDER BY e.insertDate DESC";
        if ("-1".equals(studentId)) {
            sql = "select e.examNum num,e.examName name,e.type ext1,e.examDate ext2 from exam e  where e.isDelete ='F'  and e.status!={STATUS_EXAM_COMPLETE} ORDER BY e.insertDate DESC";
        }
        Map args = StreamMap.create().put("STATUS_EXAM_COMPLETE", (Object) "9").put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public int editExamineeNum(String examNum, String studentId, String schoolNum, String classNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String sId = this.dao2._queryStr("select id from student where studentId={studentId} ", args);
        args.put("sId", sId);
        this.dao2._execute("UPDATE examinationnum en SET en.schoolNum = {schoolNum} ,en.classNum = {classNum} WHERE en.studentId = {sId} AND en.examNum = {examNum}", args);
        StringBuffer epSql = new StringBuffer();
        epSql.append("select gradeNum,subjectNum,examPaperNum from exampaper ");
        epSql.append("where examNum={examNum}");
        StringBuffer enSql = new StringBuffer();
        enSql.append("select gradeNum,subjectNum from examinationnum ");
        enSql.append("where studentId={sId} and examNum={examNum} ");
        StringBuffer allEpSql = new StringBuffer();
        allEpSql.append("select examPaperNum from (");
        allEpSql.append(enSql);
        allEpSql.append(") en inner join (");
        allEpSql.append(epSql);
        allEpSql.append(") ep on ep.gradeNum=en.gradeNum and ep.subjectNum=en.subjectNum");
        List<Object> examPaperNumList = this.dao2._queryColList(allEpSql.toString(), args);
        List<String> sqlList = new ArrayList<>();
        examPaperNumList.forEach(obj -> {
            String objs = "" + obj;
            args.put(objs, obj);
            StringBuffer regListSql = new StringBuffer();
            regListSql.append("select id from regexaminee where studentId={sId} and examPaperNum={" + objs + "}");
            List<Object> regIdList = this.dao2._queryColList(regListSql.toString(), args);
            if (regIdList.size() > 0) {
                StringBuffer illSql = new StringBuffer();
                illSql.append("update illegal set schoolNum={schoolNum} where studentId={sId} and examPaperNum={" + objs + "}");
                sqlList.add(illSql.toString());
                StringBuffer regSql = new StringBuffer();
                regSql.append("update regexaminee set schoolNum={schoolNum},classNum={classNum} where studentId={sId} and examPaperNum={" + objs + "}");
                sqlList.add(regSql.toString());
                StringBuffer scoreSql = new StringBuffer();
                scoreSql.append("update score set schoolNum={schoolNum},classNum={classNum} where studentId={sId} and examPaperNum={" + objs + "}");
                sqlList.add(scoreSql.toString());
                StringBuffer objscoreSql = new StringBuffer();
                objscoreSql.append("update objectivescore set schoolNum={schoolNum},classNum={classNum} where studentId={sId} and examPaperNum={" + objs + "}");
                sqlList.add(objscoreSql.toString());
                StringBuffer tagSql = new StringBuffer();
                tagSql.append("update tag set schoolNum={schoolNum},classNum={classNum} where studentId={sId} and examPaperNum={" + objs + "}");
                sqlList.add(tagSql.toString());
                regIdList.forEach(regId -> {
                    String regIds = "" + regId;
                    args.put("regIds", regId);
                    StringBuffer canSql = new StringBuffer();
                    canSql.append("update cantrecognized set schoolNum={schoolNum} where regId={" + regIds + "} ");
                    sqlList.add(canSql.toString());
                });
            }
        });
        if (sqlList.size() > 0) {
            this.dao2._batchExecute(sqlList, args);
            return 1;
        }
        return 1;
    }

    public String setStuExampaperType(String studentId, String exampaperNum, String type) {
        Map args = StreamMap.create().put("type", (Object) type).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("exampaperNum", (Object) exampaperNum);
        return String.valueOf(this.dao2._execute("UPDATE illegal set type={type} where studentId = {studentId} and examPaperNum = {exampaperNum} ", args));
    }

    public List<Map<String, Object>> getCaijueGroupList(String exampaperNum, String groupNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) groupNum);
        return this.dao2._queryMapList("select u.schoolnum,r.insertUser,ifnull(s.schoolName,'')schoolName,t.teacherNum,t.teacherName,r.caijue,r.avgScore,questionNum from teacher t LEFT JOIN user u on t.id = u.userId LEFT JOIN (select r.insertUser,COUNT(r.id) caijue,IFNULL(d.questionNum,sd.questionNum) questionNum,IFNULL(ROUND(AVG(r.questionScore),2),0) avgScore  FROM remark r LEFT JOIN questiongroup_question qq  ON r.questionNum = qq.questionNum  left join define d on qq.questionNum=d.id  left join subdefine sd on qq.questionNum=sd.id where r.type='1' and r.`status` = 'T' and r.exampaperNum = {exampaperNum} and qq.groupNum = {groupNum} group by r.questionNum,r.insertUser) r  ON u.id = r.insertUser LEFT JOIN school s on u.schoolnum = s.id  where  r.insertUser is not NULL", null, args);
    }

    public List<Map<String, Object>> getNotCaijue(String exampaperNum, String groupNum) {
        Map args = StreamMap.create().put("groupNum", (Object) groupNum);
        return this.dao2._queryMapList("SELECT s.schoolName,t.teacherName,t.teacherNum,r.count,CAST(r.insertUser AS CHAR) insertUser from(  \tSELECT insertUser,count(1) count from remark where questionNum={groupNum} and `status`='F' GROUP BY insertUser  \t)r INNER JOIN user u on r.insertUser=u.id  \tINNER JOIN teacher t on u.userid=t.id  \tINNER JOIN school s on t.schoolNum=s.id", null, args);
    }

    public List<Map<String, Object>> getCaijueInfo(String exampaperNum, String groupNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) groupNum);
        return this.dao2._queryMapList("select COUNT(r.id) totalNum, count(CASE WHEN (r.status = 'F') THEN r.id END) weicaijue , count(CASE WHEN (r.status = 'T') THEN r.id END) yicaijue from remark r LEFT JOIN questiongroup_question qg ON r.questionNum = qg.questionNum where type = '1' and qg.exampaperNum={exampaperNum} AND qg.groupNum={groupNum} and qg.groupNum is not null", null, args);
    }

    public List<Task> getAllQuestionNumList(String exampaperNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryBeanList("(SELECT IFNULL(subd.id,d.id) questionNum,IFNULL(subd.questionNum,d.questionNum) realQuestionNum,'1' ext1 from questiongroup_question qg LEFT JOIN define d on d.id = qg.questionNum LEFT JOIN subdefine subd on subd.id = qg.questionNum LEFT JOIN define d2 on d2.id = subd.pid where qg.exampaperNum = {exampaperNum} ORDER BY IFNULL(d.orderNum,d2.orderNum),subd.orderNum) UNION all (SELECT d.id questionNum,d.questionNum realQuestionNum,'2' ext1 from questiongroup qg LEFT JOIN define d on d.id = qg.groupNum where qg.exampaperNum = {exampaperNum} and qg.groupType = '2' ORDER BY d.orderNum) ORDER BY ext1,1", Task.class, args);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public List<AjaxData> getUserNoPerExam(String userNum) {
        List list = new ArrayList();
        Map args = new HashMap();
        args.put("userNum", userNum);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args);
        if (null == map) {
            list = this.dao2._queryBeanList("select e.examNum num,e.examName name,type ext1,examDate ext2  from exam e  left join examSchool es on e.examNum=es.examNum  left join ( SELECT schoolNum FROM schoolscanpermission WHERE userNum ={userNum}) b on es.schoolNum=b.schoolNum  where isDelete ='F'  and b.schoolNum is null group by e.examNum ORDER BY examDate DESC,e.insertDate DESC", AjaxData.class, args);
        }
        return list;
    }
}
