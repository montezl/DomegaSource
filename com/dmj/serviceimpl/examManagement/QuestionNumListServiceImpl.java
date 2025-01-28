package com.dmj.serviceimpl.examManagement;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.awardPoint.AwardPointDaoImpl;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Choosenamerecord;
import com.dmj.domain.Define;
import com.dmj.domain.Exampaper;
import com.dmj.domain.ObjectiveScore;
import com.dmj.domain.RegExaminee;
import com.dmj.domain.Score;
import com.dmj.service.examManagement.ExamService;
import com.dmj.service.examManagement.QuestionNumListService;
import com.dmj.service.historyTable.HistoryTableService;
import com.dmj.serviceimpl.historyTable.HistoryTableServiceImpl;
import com.dmj.util.Const;
import com.dmj.util.GUID;
import com.dmj.util.ItemThresholdRegUtil;
import com.zht.db.DbUtils;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import common.Logger;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.jfree.util.Log;

/* loaded from: QuestionNumListServiceImpl.class */
public class QuestionNumListServiceImpl implements QuestionNumListService {
    BaseDaoImpl2 dao2 = new BaseDaoImpl2();
    private AwardPointDaoImpl awardPointDaoImpl = new AwardPointDaoImpl();
    private HistoryTableService hts = (HistoryTableService) ServiceFactory.getObject(new HistoryTableServiceImpl());
    private ExamService examService = (ExamService) ServiceFactory.getObject(new ExamServiceImpl());
    Logger log = Logger.getLogger(getClass());

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> list(String qtype, int examPaperNum, String examinationRoomNum, int pagestart, int pagesize, String studentName, String school, String qNum, String jie, String stuScore, String examineeNumOStuId, String exam, String gradeNum, String flag, String examroomornot, String examplace, String subject, String numbererror, String pendingAppeal, String processedAppeal, String studentId) {
        String schoolStr;
        String qNumStr;
        String sql;
        String sql2;
        String sql3;
        String sql4;
        String levelstudentSql = "";
        String exampaperSql = "";
        String defineOnSql = "";
        String defineWhereSql = "";
        Map args = new HashMap();
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pagesize", Integer.valueOf(pagesize));
        args.put("examinationRoomNum", examinationRoomNum);
        args.put(License.SCHOOL, school);
        args.put("exam", exam);
        args.put("examineeNumOStuId", examineeNumOStuId);
        args.put("subject", subject);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("qNum", qNum);
        args.put("stuScore", stuScore);
        args.put("examplace", examplace);
        args.put("studentName", "%" + studentName + "%");
        args.put(Const.EXPORTREPORT_studentId, studentId);
        if (isXuanxue(String.valueOf(examPaperNum))) {
            levelstudentSql = " LEFT JOIN levelstudent ls ON ls.sid = s.studentId ";
            exampaperSql = " LEFT JOIN exampaper ep ON ep.gradeNum = ls.gradeNum and ep.subjectNum = ls.subjectNum and ep.pexamPaperNum = {examPaperNum} ";
            defineOnSql = " d.category = ep.examPaperNum and ";
            defineWhereSql = " and d.id is not null ";
        }
        String flagstr = "";
        if (!flag.equals("F")) {
            flagstr = "  LIMIT {pagestart},{pagesize} ";
        }
        String examroom = "";
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (!examinationRoomNum.equals("-1") && examroomornot.equals("0")) {
            examroom = " and examinationRoomNum={examinationRoomNum}  ";
        }
        String schoolStr1 = "";
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = "AND schoolNum={school}  ";
            schoolStr1 = "AND ob.schoolNum={school}  ";
        }
        long sid = 0;
        String sidStr1 = "";
        if (null != examineeNumOStuId && !"".equals(examineeNumOStuId) && !"null".equals(examineeNumOStuId)) {
            if (null != this.dao2._queryStr("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId}) and e.subjectNum={subject} and s.gradeNum={gradeNum}  ", args) && !"".equals(this.dao2._queryStr("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId}) and e.subjectNum={subject} and s.gradeNum={gradeNum}  ", args)) && !"null".equals(this.dao2._queryStr("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId}) and e.subjectNum={subject} and s.gradeNum={gradeNum}  ", args))) {
                sid = this.dao2._queryLong("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId}) and e.subjectNum={subject} and s.gradeNum={gradeNum}  ", args).longValue();
            }
            args.put("sid", Long.valueOf(sid));
            sidStr1 = " AND ob.studentId={sid}  ";
        }
        String appealStr1 = "";
        String appealStr2 = "";
        String cloumns = "";
        if ((processedAppeal != null && !processedAppeal.equals("")) || (pendingAppeal != null && !pendingAppeal.equals(""))) {
            appealStr2 = " and ap.scoreId is not null";
            cloumns = ",if(ap.reason is null,'',ap.reason) reason,ifnull(ap.rejectReason,'')rejectReason ";
            if (processedAppeal != null && !processedAppeal.equals("") && pendingAppeal != null && !pendingAppeal.equals("")) {
                appealStr1 = appealStr1 + " and te.status is not null ";
            } else if (pendingAppeal != null && !pendingAppeal.equals("")) {
                appealStr1 = appealStr1 + " and te.status = '0' ";
            } else if (processedAppeal != null && !processedAppeal.equals("")) {
                appealStr1 = appealStr1 + " and te.status <> '0' and te.status is not null ";
            }
        }
        if (qNum.equals("-1")) {
            qNumStr = "";
        } else {
            qNumStr = " AND questionNum={qNum}  ";
        }
        String stuScoreStr = "";
        if (null != stuScore && !"".equals(stuScore) && !"null".equals(stuScore)) {
            stuScoreStr = "  and questionScore={stuScore} ";
        }
        String epString = "";
        if (!examplace.equals("-1")) {
            epString = " and testingCentreId={examplace} ";
        }
        String studentStr1 = "";
        String studentStr2 = "";
        if (!studentName.equals("")) {
            studentStr1 = " LEFT JOIN student s on ob.studentid = s.id";
            studentStr2 = " and s.studentName LIKE {studentName} ";
        }
        String qtypeStr = "";
        if (qtype.equals("-1")) {
            qtypeStr = " (SELECT ob.id,ob.page, '' answer,ob. questionScore,ob.examPaperNum,1 qtype ,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,0 description,ob.regScore from score ob " + studentStr1 + " WHERE  examPaperNum={examPaperNum} " + studentStr2 + examroom + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + "  AND continued='F'  UNION ALL SELECT ob.id,ob.page,ob.answer,ob.questionScore,ob.examPaperNum,0 qtype ,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,ob.description,ob.regScore from objectivescore ob " + studentStr1 + " WHERE ob.exampaperNum={examPaperNum} " + studentStr2 + examroom + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + "  )s";
        } else if (qtype.equals("0")) {
            qtypeStr = " (SELECT ob.id,ob.page,ob.answer,ob.questionScore,ob.examPaperNum,0 qtype,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,ob.description,ob.regScore from objectivescore ob " + studentStr1 + " WHERE exampaperNum={examPaperNum} " + studentStr2 + examroom + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + " )s";
        } else if (qtype.equals("1")) {
            qtypeStr = " (SELECT ob.id,ob.page, '' answer, ob.questionScore,ob.examPaperNum,1 qtype,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,0 description,ob.regScore from score ob " + studentStr1 + " WHERE  examPaperNum={examPaperNum} " + studentStr2 + examroom + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + " AND continued='F' )s";
        }
        if (!studentName.equals("")) {
            String sql5 = "SELECT s.id ext2,ce.regId cRegId,ce.status ceStatus,s.regId regId, s.studentId ext8,st.studentName studentName,st.studentId realStudentId,d.qNum ext3,d.id questionNum,s.answer answer,CASE WHEN ill.exampaperType='B' THEN d.answer_b  ELSE  d.answer  END AS yanswer,s.questionScore questionScore,s.examPaperNum examPaperNum,s.examinationRoomNum examinationRoomNum,d.fullScore ext1,s.qtype,s.schoolNum ext5,d.optionCount ext4 ,s.page page,d.choosename,d.isParent,d.defId ,s.regScore ext6 ,if(ap.suggestScore is null,'',ap.suggestScore)suggestScore,case when ap.status=0 then '申诉' when ap.status=1 then '申诉修改' when ap.status=2 then '申诉驳回' else '' end appealStatus" + cloumns + " FROM " + qtypeStr + " LEFT JOIN (SELECT regId,exampaperType,studentId,examPaperNum FROM illegal WHERE examPaperNum={examPaperNum} " + schoolStr + epString + ") ill ON  s.studentId=ill.studentId  AND s.examPaperNum=ill.examPaperNum " + levelstudentSql + exampaperSql + " LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore, CASE WHEN d.isParent ='0' THEN d.answer ELSE s.answer END AS answer, CASE WHEN d.isParent ='0' THEN d.answer_b ELSE s.answer_b END AS answer_b, CASE WHEN d.isParent ='0' THEN d.optionCount ELSE s.optionCount END AS optionCount, d.choosename, isParent,d.id defId,  CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum,d.category FROM define d LEFT JOIN subdefine s ON d.id=s.pid   WHERE d.examPaperNum={examPaperNum})d ON " + defineOnSql + " d.id = s.questionNum  LEFT JOIN (SELECT s.id,s.studentId,s.studentNum,s.studentName FROM examinationnum ex left JOIN student s on ex.studentId = s.id left join exampaper exp on ex.examNum = exp.examNum and ex.subjectNum = exp.subjectNum and ex.gradeNum = exp.gradeNum WHERE  exp.exampaperNum= {examPaperNum} ";
            if (!examplace.equals("-1")) {
                sql5 = sql5 + " and ex.testingCentreId =  {examplace} ";
            }
            if (sid != 0) {
                sql3 = sql5 + " and s.id={sid}  ";
            } else {
                sql3 = sql5 + " and s.gradeNum={gradeNum} ";
                if (school != null && !school.equals("-1")) {
                    sql3 = sql3 + "and s.schoolNum= {school} ";
                }
            }
            String sql6 = sql3 + ") st ON st.id = s.studentId    LEFT JOIN (SELECT id,examPaperNum,regId,status  FROM cliperror WHERE examPaperNum={examPaperNum} )ce  ON s.regId=ce.regId   LEFT JOIN (SELECT te.scoreId,te.status,te.reason,te.suggestScore,te.rejectReason  FROM teacherappeal te  where 1=1 " + appealStr1 + ") ap  ON s.id=ap.scoreId  ";
            if (numbererror != null) {
                String sql1 = "SELECT count(1) FROM  define  a LEFT join (select id,choosename from define where exampapernum={examPaperNum}  GROUP BY choosename) b ON  CAST(a.id AS CHAR)=b.choosename LEFT join subdefine c  on c.pid=a.id or c.pid=b.id WHERE a.exampapernum={examPaperNum}  and LENGTH(a.choosename) < 2";
                int num = this.dao2._queryInt(sql1, args).intValue();
                sql6 = ((sql6 + "  inner join(select a.studentid , count(1) tishu from (select studentid from objectivescore where exampapernum={examPaperNum} UNION all select studentid from score where exampapernum={examPaperNum}  and continued='F' )  a  GROUP BY  a.studentid HAVING tishu <> (") + num) + "))tishu  on tishu.studentid=s.studentId";
            }
            String sql7 = sql6 + " where ";
            if (StrUtil.isNotEmpty(studentId)) {
                sql4 = sql7 + " st.id={studentId} ";
            } else {
                sql4 = sql7 + " st.studentName LIKE {studentName} ";
            }
            sql2 = sql4 + defineWhereSql + appealStr2 + " ORDER BY d.orderNum,s.answer,s.description " + flagstr;
        } else {
            String sql8 = "SELECT s.id ext2,ce.regId cRegId,ce.status ceStatus,s.regId regId, s.studentId ext8,st.studentName studentName,st.studentId realStudentId,d.qNum ext3,d.id questionNum,s.answer answer,CASE WHEN ill.exampaperType='B' THEN d.answer_b  ELSE  d.answer  END AS yanswer,s.questionScore questionScore,s.examPaperNum examPaperNum,s.examinationRoomNum examinationRoomNum,d.fullScore ext1,s.qtype qtype,s.schoolNum ext5,d.optionCount ext4,s.page page,d.choosename,d.isParent,d.defId ,s.regScore ext6 ,if(ap.suggestScore is null,'',ap.suggestScore)suggestScore,case when ap.status=0 then '申诉' when ap.status=1 then '申诉修改' when ap.status=2 then '申诉驳回' else '' end appealStatus" + cloumns + " FROM" + qtypeStr + " LEFT JOIN (SELECT regId,exampaperType,studentId,examPaperNum FROM illegal WHERE examPaperNum={examPaperNum} " + schoolStr + epString + ")ill ON  s.studentId=ill.studentId  AND s.examPaperNum=ill.examPaperNum " + levelstudentSql + exampaperSql + " LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore, CASE WHEN d.isParent ='0' THEN d.answer ELSE s.answer END AS answer, CASE WHEN d.isParent ='0' THEN d.answer_b ELSE s.answer_b END AS answer_b, CASE WHEN d.isParent ='0' THEN d.optionCount ELSE s.optionCount END AS optionCount,  d.choosename, isParent,d.id defId,   CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum,d.category FROM define d LEFT JOIN subdefine s ON d.id=s.pid  WHERE d.examPaperNum={examPaperNum} )d ON " + defineOnSql + " d.id = s.questionNum  LEFT JOIN (SELECT s.id,s.studentId,s.studentNum,s.studentName FROM examinationnum ex left JOIN student s on ex.studentId = s.id left join exampaper exp on ex.examNum = exp.examNum and ex.subjectNum = exp.subjectNum and ex.gradeNum = exp.gradeNum WHERE  exp.exampaperNum= " + examPaperNum;
            if (!examplace.equals("-1")) {
                sql8 = sql8 + " and ex.testingCentreId =  {examplace} ";
            }
            if (sid != 0) {
                sql = sql8 + " and s.id={sid}  ";
            } else {
                sql = sql8 + " and s.gradeNum={gradeNum} ";
                if (school != null && !school.equals("-1")) {
                    sql = sql + "and s.schoolNum= {school} ";
                }
            }
            String sql9 = sql + ") st ON st.id = s.studentId   LEFT JOIN (SELECT id,examPaperNum,regId,status  FROM cliperror WHERE examPaperNum={examPaperNum} )ce  ON s.regId=ce.regId   LEFT JOIN (SELECT te.scoreId,te.status,te.reason,te.suggestScore,te.rejectReason  FROM teacherappeal te  where 1=1 " + appealStr1 + ") ap  ON s.id=ap.scoreId  ";
            if (numbererror != null) {
                String sql12 = "SELECT count(1) FROM  define  a LEFT join (select id,choosename from define where exampapernum={examPaperNum}  GROUP BY choosename) b ON  CAST(a.id AS CHAR)=b.choosename LEFT join subdefine c  on c.pid=a.id or c.pid=b.id WHERE a.exampapernum={examPaperNum} and LENGTH(a.choosename) < 2";
                int num2 = this.dao2._queryInt(sql12, args).intValue();
                sql9 = ((sql9 + "  inner join(select a.studentid , count(1) tishu from (select studentid from objectivescore where exampapernum={examPaperNum}  UNION all select studentid from score where exampapernum={examPaperNum}  and continued='F' )  a  GROUP BY  a.studentid HAVING tishu <> (") + num2) + "))tishu  on tishu.studentid=s.studentId";
            }
            sql2 = sql9 + " where 1=1 " + defineWhereSql + appealStr2 + " ORDER BY d.orderNum,s.answer,s.description " + flagstr;
        }
        return this.dao2._queryMapList(sql2, null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Score> list2(String qtype, String examPaperNum, String examinationRoomNum, int pagestart, int pagesize, String studentName, String school, String qNum, String jie, String dcclass, String imagetype, String arrChk) {
        List<String> list;
        String schoolStr;
        String qNumStr;
        String sql;
        Map args = new HashMap();
        new ArrayList();
        if (null != arrChk && !arrChk.equals("")) {
            list = Arrays.asList(arrChk.split(Const.STRING_SEPERATOR));
        } else {
            list = new ArrayList<>();
        }
        String studentidsql = " and (";
        for (int i = 0; i < list.size(); i++) {
            String studentid = list.get(i);
            String studentidi = "studentid" + i;
            args.put(studentidi, studentid);
            if (list.size() - 1 != i) {
                studentidsql = studentidsql + " s.studentid={" + studentidi + "} or  ";
            } else if (list.size() - 1 == i) {
                studentidsql = studentidsql + " s.studentid={" + studentidi + "}  ) ";
            }
        }
        if (list.size() == 0) {
            studentidsql = "";
        }
        String examroom = "";
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (dcclass.equals("cl")) {
            examroom = examroom + " and classNum={examinationRoomNum}  ";
        } else if (dcclass.equals("ex")) {
            examroom = " and examinationRoomNum={examinationRoomNum} ";
        }
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = "AND schoolNum={school} ";
        }
        if (qNum.equals("-1")) {
            qNumStr = "";
        } else {
            qNumStr = " AND questionNum={qNum} ";
        }
        String qtypeStr = "";
        if (qtype.equals("-1")) {
            qtypeStr = " (SELECT id,regid,page,examPaperNum,1 qtype, examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum from score WHERE  examPaperNum={examPaperNum}  and continued='F' " + schoolStr + examroom + qNumStr + " UNION ALL SELECT id,regid,page,examPaperNum,0 qtype,examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum from objectivescore WHERE exampaperNum={examPaperNum} " + schoolStr + examroom + qNumStr + ")s";
        } else if (qtype.equals("0")) {
            qtypeStr = " ( SELECT id,regid,page,examPaperNum,0 qtype, examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum from objectivescore WHERE exampaperNum={examPaperNum} " + schoolStr + examroom + qNumStr + ")s";
        } else if (qtype.equals("1")) {
            qtypeStr = " (SELECT id,regid,page,examPaperNum,1 qtype ,examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum from score WHERE  examPaperNum={examPaperNum} " + schoolStr + examroom + qNumStr + "  and continued='F')s";
        }
        if (!"".equals(studentName)) {
            String sql2 = "SELECT s.id id, s.regId regId,s.page, s.studentId studentId,st.studentName studentName ,d.id questionNum,d.questionNum questionName,s.examPaperNum examPaperNum,s.qtype qtype,s.examinationRoomNum examinationRoomNum,c.className className,etr.examinationRoomName examinationRoomName,s.qtype qtype  FROM " + qtypeStr + " LEFT JOIN (SELECT id,examPaperNum,questionNum,questionType,fullScore   FROM define  WHERE examPaperNum={examPaperNum}   AND isParent={isParent0}     UNION ALL   SELECT subdef.id,def.examPaperNum,subdef.questionNum,def.questionType,subdef.fullScore   FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE def.examPaperNum={examPaperNum}   AND def.isParent={isParent1}       ) d ON d.exampaperNum = s.exampaperNum   AND d.id = s.questionNum      LEFT JOIN student st ON st.id = s.studentId    ";
            String sql3 = ((sql2 + " LEFT JOIN class c ON s.classNum=c.id ") + " left join examinationroom etr on etr.id=s.examinationRoomNum and etr.gradeNum=s.gradeNum and etr.schoolNum=s.schoolNum ") + "LEFT JOIN (select exampaperNum,id from regexaminee where exampapernum={examPaperNum} )reg   on s.regId = reg.id  where st.studentName={studentName} ";
            if ("qt".equals(imagetype)) {
                sql = (sql3 + " GROUP BY s.id  ") + " ORDER BY s.questionNum*1 ";
            } else {
                sql = (sql3 + " GROUP BY s.examPaperNum,s.studentId  ") + " ORDER BY CONVERT(s.studentName using gbk) ";
            }
        } else {
            String sql4 = (((("SELECT s.id id, s.regId regId,s.page, s.studentId studentId,st.studentName studentName,d.id questionNum,d.questionNum questionName,s.examPaperNum examPaperNum,s.qtype qtype,s.examinationRoomNum examinationRoomNum,c.className className,etr.examinationRoomName examinationRoomName,gra.gradeName gradeName, s.qtype qtype,d.cross_page,examp.totalPage  FROM  (SELECT id,regId,page,examPaperNum,examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum,1 qtype from score WHERE  examPaperNum={examPaperNum}  and continued='F' " + schoolStr + qNumStr + examroom + " UNION ALL SELECT id,regId,page,examPaperNum,examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum,0 qtype from objectivescore WHERE exampaperNum={examPaperNum} " + schoolStr + qNumStr + examroom + ")s left join exampaper examp on examp.exampaperNum=s.exampaperNum  LEFT JOIN (SELECT id,examPaperNum,questionNum,questionType,fullScore,cross_page FROM define  WHERE examPaperNum={examPaperNum}   AND isParent={isParent0}     UNION ALL   SELECT subdef.id,def.examPaperNum,subdef.questionNum,def.questionType,subdef.fullScore,subdef.cross_page FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE def.examPaperNum={examPaperNum}   AND def.isParent={isParent1}             ) d ON d.exampaperNum = s.exampaperNum   AND d.id = s.questionNum  LEFT JOIN student st ON st.id = s.studentId    ") + " left join grade gra on gra.gradeNum=s.gradeNum and gra.schoolNum=s.schoolNum ") + " LEFT JOIN class c ON s.classNum = c.id    ") + " left join examinationroom etr on etr.id=s.examinationRoomNum and etr.gradeNum=s.gradeNum ") + "LEFT JOIN (select exampaperNum,id from regexaminee where exampapernum={examPaperNum} )  reg   on s.regId = reg.id   where d.id = s.questionNum  " + studentidsql;
            if ("qt".equals(imagetype)) {
                sql = (sql4 + " GROUP BY s.id  ") + " ORDER BY s.questionNum*1 ";
            } else {
                sql = (sql4 + " GROUP BY s.examPaperNum,s.studentId  ") + " ORDER BY CONVERT(s.studentName using gbk) ";
            }
        }
        args.put("examinationRoomNum", examinationRoomNum);
        args.put(License.SCHOOL, school);
        args.put("qNum", qNum);
        args.put("examPaperNum", examPaperNum);
        args.put("qtype", qtype);
        args.put("isParent0", 0);
        args.put("isParent1", 1);
        args.put("studentName", studentName);
        return this.dao2._queryBeanList(sql, Score.class, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<RegExaminee> getStuRegList(String examPaperNum, String examinationRoomNum, String school, String dcclass, String arrChk) {
        List<String> list;
        Map args = new HashMap();
        new ArrayList();
        if (null != arrChk && !arrChk.equals("")) {
            list = Arrays.asList(arrChk.split(Const.STRING_SEPERATOR));
        } else {
            list = new ArrayList<>();
        }
        String studentidsql = " and (";
        for (int i = 0; i < list.size(); i++) {
            String studentid = list.get(i);
            String studentidi = "studentidi" + i;
            args.put(studentidi, studentid);
            if (list.size() - 1 != i) {
                studentidsql = studentidsql + " studentId={" + studentidi + "} or  ";
            } else if (list.size() - 1 == i) {
                studentidsql = studentidsql + " studentId={" + studentidi + "} ) ";
            }
        }
        if (list.size() == 0) {
            studentidsql = "";
        }
        String examroom = "";
        String classStr = "";
        String tabStr = "";
        String nameStr = "";
        if ("cl".equals(dcclass)) {
            nameStr = ",cla.className ext1";
            tabStr = " LEFT JOIN class cla on cla.id=st.classNum ";
            if (!"-1".equals(examinationRoomNum)) {
                classStr = classStr + " where st.classNum={examinationRoomNum} ";
            }
        } else if ("ex".equals(dcclass)) {
            nameStr = ",er.examinationRoomName ext1";
            tabStr = " LEFT JOIN examinationroom er on er.id=reg.examinationRoomNum ";
            if (!"-1".equals(examinationRoomNum)) {
                examroom = " and examinationRoomNum={examinationRoomNum}  ";
            }
        }
        String schoolStr = "-1".equals(school) ? "" : " AND schoolNum={school} ";
        String sql = "SELECT reg.id,reg.pageStr,reg.studentId" + nameStr + ",st.studentName ext2 FROM (SELECT GROUP_CONCAT(id ORDER BY page) id,GROUP_CONCAT(page ORDER BY page) pageStr,studentId,schoolNum,examinationRoomNum from regexaminee where examPaperNum={examPaperNum}  " + examroom + studentidsql + schoolStr + " GROUP BY studentId) reg LEFT JOIN student st ON st.id = reg.studentId " + tabStr + classStr + " ORDER BY CONVERT(ext1 using gbk),CONVERT(st.studentName using gbk) ";
        args.put("examinationRoomNum", examinationRoomNum);
        args.put(License.SCHOOL, school);
        args.put("examPaperNum", examPaperNum);
        return this.dao2._queryBeanList(sql, RegExaminee.class, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<String> list2dt(String examPaperNum, String studentId) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryColList("select id from regexaminee where examPaperNum={examPaperNum}  and studentId={studentId}  order by page", args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Score> his_list2(String qtype, String examPaperNum, String examinationRoomNum, int pagestart, int pagesize, String studentName, String school, String qNum, String jie, String zerro, String dcclass, String imagetype, String arrChk) {
        String schoolStr;
        String qNumStr;
        String sql;
        new ArrayList();
        String studentidsql = "";
        if (arrChk != null && !arrChk.equals("")) {
            studentidsql = " and s.studentid={arrChk} ";
        }
        String examroom = "";
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (dcclass.equals("cl")) {
            examroom = examroom + " and classNum={examinationRoomNum}  ";
        } else if (dcclass.equals("ex")) {
            examroom = " and examinationRoomNum={examinationRoomNum} ";
        }
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = "AND schoolNum={school} ";
        }
        String qIdStr = "";
        if (qNum.equals("-1")) {
            qNumStr = "";
        } else {
            qIdStr = " AND id={qNum} ";
            qNumStr = " AND questionNum={qNum} ";
        }
        String historytableNum = this.hts.tableNum(arrChk, "0");
        String qtypeStr = "";
        String questiontype = "";
        if (!qtype.equals("-1")) {
            if (qtype.equals("0")) {
                questiontype = " and questiontype={qtype}  ";
                qtypeStr = " (SELECT id,regid,page,examPaperNum,questionType qtype ,examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum from " + historytableNum + "  WHERE  examPaperNum={examPaperNum} " + schoolStr + examroom + qNumStr + "  and continued='F' " + questiontype + ")s";
            } else if (qtype.equals("1")) {
                questiontype = " and questiontype={qtype}  ";
                qtypeStr = " (SELECT id,regid,page,examPaperNum,questionType qtype ,examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum from " + historytableNum + "  WHERE  examPaperNum={examPaperNum} " + schoolStr + examroom + qNumStr + "  and continued='F' " + questiontype + ")s";
            }
        }
        if (zerro.equals("2")) {
        }
        if (!"".equals(studentName)) {
            String sql2 = "SELECT s.id id, s.regId regId,s.page, s.studentId studentId,st.studentName studentName ,d.id questionNum,d.questionNum questionName,s.examPaperNum examPaperNum,s.qtype qtype,s.examinationRoomNum examinationRoomNum,c.className className,etr.examinationRoomName examinationRoomName,s.qtype qtype  FROM " + qtypeStr + " LEFT JOIN (select id,questionNum,exampaperNum from his_define where exampaperNum={examPaperNum} " + qIdStr + " and (( type='0' and p_questionNum=0) or (type='0' and   p_questionNum!=0) or (type='3' and p_questionNum!=0)) " + questiontype + ") d ON d.exampaperNum = s.exampaperNum   AND d.id = s.questionNum      LEFT JOIN student st ON st.id = s.studentId    ";
            String sql3 = ((sql2 + " LEFT JOIN class c ON s.classNum=c.id ") + " left join his_examinationroom etr on etr.id=s.examinationRoomNum and etr.gradeNum=s.gradeNum and etr.schoolNum=s.schoolNum ") + "LEFT JOIN (select exampaperNum,id from his_regexaminee where exampapernum={examPaperNum} )reg   on s.regId = reg.id  where st.studentName={studentName} ";
            if (imagetype.equals("dt")) {
                sql3 = sql3 + " GROUP BY s.examPaperNum,s.studentId  ";
            }
            sql = sql3 + " ORDER BY s.questionNum*1 ";
        } else {
            String sql4 = (((("SELECT s.id id, s.regId regId,s.page, s.studentId studentId,st.studentName studentName,d.id questionNum,d.questionNum questionName,s.examPaperNum examPaperNum,s.qtype qtype,s.examinationRoomNum examinationRoomNum,c.className className,etr.examinationRoomName examinationRoomName,gra.gradeName gradeName, s.qtype qtype,dinfo.cross_page  FROM " + qtypeStr + " left join his_exampaper examp on examp.exampaperNum=s.exampaperNum  LEFT JOIN (select id,questionNum,exampaperNum from his_define where exampaperNum={examPaperNum} " + qIdStr + " and (( type='0' and p_questionNum=0) or (type='0' and   p_questionNum!=0) or (type='3' and p_questionNum!=0)) " + questiontype + ") d ON d.exampaperNum = s.exampaperNum   AND d.id = s.questionNum left join (select defineid,cross_page from his_defineinfo where exampapernum={examPaperNum} )dinfo on dinfo.defineid=d.id     LEFT JOIN student st ON st.id = s.studentId    ") + " left join grade gra on gra.gradeNum=s.gradeNum and gra.schoolNum=s.schoolNum ") + " LEFT JOIN class c ON s.classNum = c.id    ") + " left join his_examinationroom etr on etr.id=s.examinationRoomNum and etr.gradeNum=s.gradeNum and etr.schoolNum=s.schoolNum ") + "LEFT JOIN (select exampaperNum,id from his_regexaminee where exampapernum={examPaperNum} )  reg   on s.regId = reg.id   where d.id = s.questionNum  " + studentidsql;
            if (imagetype.equals("dt")) {
                sql4 = sql4 + " GROUP BY s.examPaperNum,s.studentId,s.page  ";
            }
            sql = sql4 + "  ORDER BY s.questionNum ";
        }
        Map args = new HashMap();
        args.put("arrChk", arrChk);
        args.put("examinationRoomNum", examinationRoomNum);
        args.put(License.SCHOOL, school);
        args.put("qNum", qNum);
        args.put("qtype", qtype);
        args.put("examPaperNum", examPaperNum);
        args.put("studentName", studentName);
        return this.dao2._queryBeanList(sql, Score.class, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Score> getstudent(String qtype, int examPaperNum, String examinationRoomNum, int pagestart, int pagesize, String studentName, String school, String qNum, String jie, String zerro, String dcclass, String imagetype, String arrChk) {
        String schoolStr;
        String qNumStr;
        String sql;
        Map args = new HashMap();
        args.put("examinationRoomNum", examinationRoomNum);
        args.put(License.SCHOOL, school);
        args.put("qNum", qNum);
        args.put("QUESTION_TYPE_OBJECTIVE", "0");
        args.put("QUESTION_TYPE_SUBJECTIVE", "1");
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("isParent0", 0);
        args.put("isParent1", 1);
        args.put("studentName", "%" + studentName + "%");
        String examroom = "";
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (dcclass.equals("cl")) {
            examroom = examroom + " and classNum={examinationRoomNum}  ";
        } else if (dcclass.equals("ex")) {
            examroom = " and examinationRoomNum={examinationRoomNum} ";
        }
        List<String> list = new ArrayList<>();
        String studentidsql = " and (";
        for (int i = 0; i < list.size(); i++) {
            String studentid = list.get(i);
            String studentidi = "studentidi" + i;
            args.put(studentidi, studentid);
            if (list.size() - 1 != i) {
                studentidsql = studentidsql + " s.studentid={" + studentidi + "} or  ";
            } else if (list.size() - 1 == i) {
                studentidsql = studentidsql + " s.studentid={" + studentidi + "}   ) ";
            }
        }
        if (list.size() == 0) {
            studentidsql = "";
        }
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = "AND schoolNum={school} ";
        }
        if (qNum.equals("-1")) {
            qNumStr = "";
        } else {
            qNumStr = " AND questionNum={qNum} ";
        }
        if (!qtype.equals("-1") && !qtype.equals("0") && qtype.equals("1")) {
        }
        String zerroStr = "";
        if (zerro.equals("2")) {
            zerroStr = " and s.questionScore='0' ";
        }
        if (!studentName.equals("")) {
            String sql2 = "SELECT s.id id, reg.Id regId,s.page, s.studentId studentId,st.studentId studentNum,st.studentName studentName,d.questionNum questionNum,s.examPaperNum examPaperNum,s.qtype qtype,s.examinationRoomNum examinationRoomNum,c.className className,etr.examinationRoomName examinationRoomName,gra.gradeName gradeName  FROM  (SELECT id,page,examPaperNum,1 qtype,examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum from score WHERE  examPaperNum={examPaperNum} " + schoolStr + qNumStr + examroom + " and continued='F'  GROUP BY studentId,questionNum  UNION ALL SELECT id,page,examPaperNum,0 qtype,examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum from objectivescore WHERE exampaperNum={examPaperNum} " + schoolStr + qNumStr + examroom + "   GROUP BY studentId,questionNum )s LEFT JOIN (SELECT id,examPaperNum,questionNum,questionType,fullScore   FROM define  WHERE examPaperNum={examPaperNum}  AND isParent={isParent0}     UNION ALL   SELECT subdef.id,def.examPaperNum,subdef.questionNum,def.questionType,subdef.fullScore   FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE def.examPaperNum={examPaperNum}   AND def.isParent={isParent1}        ) d ON d.exampaperNum = s.exampaperNum   AND d.id = s.questionNum      LEFT JOIN student st ON st.id = s.studentId    ";
            sql = (((((sql2 + " left join grade gra on gra.gradeNum=s.gradeNum and gra.schoolNum=s.schoolNum ") + " LEFT JOIN class c ON s.gradeNum=c.gradeNum AND s.schoolNum=c.schoolNum AND s.classNum=c.id  AND st.jie=c.jie     ") + " left join examinationroom etr on etr.id=s.examinationRoomNum and etr.gradeNum=s.gradeNum ") + "LEFT JOIN regexaminee reg    ON s.examPaperNum=reg.examPaperNum AND s.schoolNum=reg.schoolNum AND s.studentId=reg.studentId   AND s.examinationRoomNum=reg.examinationRoomNum AND s.page=reg.page AND s.classNum=reg.classNum      where (st.studentName like {studentName}  or st.studentId like {studentName}  )" + zerroStr) + " GROUP BY s.studentId  ") + " ORDER BY s.classNum,s.questionNum*1,s.studentId, s.qtype*1 ";
        } else {
            String sql3 = "SELECT s.id id, reg.Id regId,s.page, s.studentId studentId,st.studentId studentNum,st.studentName studentName,d.questionNum questionNum,s.examPaperNum examPaperNum,s.qtype qtype,s.examinationRoomNum examinationRoomNum,c.className className,etr.examinationRoomName examinationRoomName,gra.gradeName gradeName  FROM  (SELECT id,page,examPaperNum,1 qtype,examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum from score WHERE  examPaperNum={examPaperNum} " + schoolStr + qNumStr + examroom + " and continued='F'  GROUP BY studentId,questionNum  UNION ALL SELECT id,page,examPaperNum,0 qtype,examinationRoomNum,studentId,questionNum,schoolNum,classNum,gradeNum from objectivescore WHERE exampaperNum={examPaperNum} " + schoolStr + qNumStr + examroom + "   GROUP BY studentId,questionNum )s LEFT JOIN (SELECT id,examPaperNum,questionNum,questionType,fullScore   FROM define  WHERE examPaperNum={examPaperNum}  AND isParent={isParent0}     UNION ALL   SELECT subdef.id,def.examPaperNum,subdef.questionNum,def.questionType,subdef.fullScore   FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE def.examPaperNum={examPaperNum}   AND def.isParent={isParent1}       ) d ON d.exampaperNum = s.exampaperNum   AND d.id = s.questionNum     LEFT JOIN student st ON st.id = s.studentId    ";
            sql = (((((sql3 + " left join grade gra on gra.gradeNum=s.gradeNum and gra.schoolNum=s.schoolNum ") + " LEFT JOIN class c ON s.gradeNum=c.gradeNum AND s.schoolNum=c.schoolNum AND s.classNum=c.id  AND st.jie=c.jie     ") + " left join examinationroom etr on etr.id=s.examinationRoomNum and etr.gradeNum=s.gradeNum  ") + "LEFT JOIN regexaminee reg    ON s.examPaperNum=reg.examPaperNum AND s.schoolNum=reg.schoolNum AND s.studentId=reg.studentId   AND s.examinationRoomNum=reg.examinationRoomNum AND s.page=reg.page AND s.classNum=reg.classNum      where 1=1 " + zerroStr + studentidsql) + " GROUP BY s.studentId  ") + "  ORDER BY s.classNum,s.qtype*1,s.questionNum,s.studentId ";
        }
        return this.dao2._queryBeanList(sql, Score.class, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Score> his_getstudent(String examPaperNum, String examinationRoomNum, String studentName, String school, String dcclass, String gradeNum) {
        String schoolStr;
        String sql;
        Map args = new HashMap();
        args.put("examinationRoomNum", examinationRoomNum);
        args.put(License.SCHOOL, school);
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("studentName", "%" + studentName + "%");
        String examroom = "";
        String classid = "";
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (dcclass.equals("cl")) {
            examroom = examroom + " and classNum={examinationRoomNum}  ";
            classid = classid + " and id={examinationRoomNum}  ";
        } else if (dcclass.equals("ex")) {
        }
        List<String> list = new ArrayList<>();
        String studentidsql = " and (";
        for (int i = 0; i < list.size(); i++) {
            String studentid = list.get(i);
            String studentidi = "studentidi" + i;
            args.put(studentidi, studentid);
            if (list.size() - 1 != i) {
                studentidsql = studentidsql + " s.studentid={" + studentidi + "} or  ";
            } else if (list.size() - 1 == i) {
                studentidsql = studentidsql + " s.studentid={" + studentidi + "}  ) ";
            }
        }
        if (list.size() == 0) {
        }
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = " AND schoolNum={school} ";
        }
        if (!studentName.equals("")) {
            sql = "  select stl.id id,stl.studentId studentId,st.studentId studentNum,st.studentName studentName,  c.className className,gra.gradeName gradeName from (select * from his_studentlevel where examPaperNum={examPaperNum} " + schoolStr + examroom + "      and source='0' and statisticType='0')stl   LEFT JOIN (SELECT id,studentId,studentName,gradeNum,classNum,jie from student where gradeNum={gradeNum} " + schoolStr + examroom + ") st   ON st.id = stl.studentId left join (SELECT * from grade where gradeNum={gradeNum} " + schoolStr + ") gra on gra.gradeNum=stl.gradeNum and gra.schoolNum=stl.schoolNum LEFT JOIN (SELECT * from class  where gradeNum={gradeNum} " + schoolStr + classid + ") c ON stl.gradeNum=c.gradeNum AND stl.schoolNum=c.schoolNum AND stl.classNum=c.id  AND st.jie=c.jie where (st.studentName like {studentName}  or st.studentId like {studentName}  ) GROUP BY stl.studentId   ORDER BY stl.classNum,stl.studentId ";
        } else {
            sql = "  select stl.id id,stl.studentId studentId,st.studentId studentNum,st.studentName studentName,  c.className className,gra.gradeName gradeName from (select * from his_studentlevel where examPaperNum={examPaperNum} " + schoolStr + examroom + "      and source='0' and statisticType='0')stl   LEFT JOIN (SELECT id,studentId,studentName,gradeNum,classNum,jie from student where gradeNum={gradeNum} " + schoolStr + examroom + ") st   ON st.id = stl.studentId left join (SELECT * from grade where gradeNum={gradeNum} " + schoolStr + ") gra on gra.gradeNum=stl.gradeNum and gra.schoolNum=stl.schoolNum LEFT JOIN (SELECT * from class  where gradeNum={gradeNum} " + schoolStr + classid + ") c ON stl.gradeNum=c.gradeNum AND stl.schoolNum=c.schoolNum AND stl.classNum=c.id  AND st.jie=c.jie GROUP BY stl.studentId   ORDER BY stl.classNum,stl.studentId ";
        }
        return this.dao2._queryBeanList(sql, Score.class, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String getsubjectName(String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryStr("select subjectName from subject where subjectNum={subjectNum} ", args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Score> list_clipError(String qtype, String examPaperNum, String examinationRoomNum, int pagestart, int pagesize, String studentId, String school, String qNum, String jie, String clip_succ_fail, String gradeNum, String regId, String studentName) {
        String sql;
        String sql2;
        if (null != studentName && !"".equals(studentName) && !"---".equals(studentName)) {
            Map args = StreamMap.create().put("studentName", (Object) ("%" + studentName + "%"));
            String id = this.dao2._queryStr("SELECT id FROM student WHERE studentName LIKE {studentName} ", args);
            if (null != id && !"".equals(id)) {
                studentId = id;
            }
        }
        String schoolStr = "";
        if (null != school && !"".equals(school) && !school.equals("-1")) {
            schoolStr = "AND schoolNum={school}  ";
        }
        String sch_room_stuId = "";
        if (null == examinationRoomNum || "".equals(examinationRoomNum) || examinationRoomNum.equals("-1")) {
            if (null != studentId && !studentId.equals("") && !studentId.equals("-1")) {
                sch_room_stuId = sch_room_stuId + "AND  studentId={studentId}    ";
            }
            if (null != school && !"".equals(school) && !school.equals("-1")) {
                sch_room_stuId = sch_room_stuId + "AND schoolNum={school}   ";
            }
        } else {
            sch_room_stuId = sch_room_stuId + "AND examinationRoomNum={examinationRoomNum}   ";
            if (null != studentId && !studentId.equals("") && !studentId.equals("-1")) {
                sch_room_stuId = sch_room_stuId + "AND  studentId={studentId}     ";
            }
        }
        String sql3 = ((null == regId || "".equals(regId)) ? "SELECT s.id id, ce.regId regId,ce.status ceStatus,s.page,st.studentId realStudentId, s.studentId studentId,st.studentName studentName,d.questionNum ext3,d.id questionNum,s.answer answer,d.answer yanswer,s.questionScore questionScore,s.examPaperNum examPaperNum,s.qtype qtype,s.examinationRoomNum examinationRoomNum,d.fullScore ext1  FROM(SELECT id,MIN(page) page, '' answer, MAX(questionScore) questionScore,examPaperNum,1 qtype ,examinationRoomNum,studentId,questionNum,schoolNum,classNum,regId from score WHERE  examPaperNum={examPaperNum}    " + sch_room_stuId : "SELECT s.id id, ce.regId regId,ce.status ceStatus,s.page,st.studentId realStudentId, s.studentId studentId,st.studentName studentName,d.questionNum ext3,d.id questionNum,s.answer answer,d.answer yanswer,s.questionScore questionScore,s.examPaperNum examPaperNum,s.qtype qtype,s.examinationRoomNum examinationRoomNum,d.fullScore ext1  FROM(SELECT id,MIN(page) page, '' answer, MAX(questionScore) questionScore,examPaperNum,1 qtype ,examinationRoomNum,studentId,questionNum,schoolNum,classNum,regId from score WHERE  examPaperNum={examPaperNum}    AND regId={regId}  ") + "GROUP BY studentId,questionNum    UNION ALL SELECT id,page,answer,questionScore,examPaperNum,0 qtype ,examinationRoomNum,studentId,questionNum,schoolNum,classNum,regId from objectivescore WHERE exampaperNum={examPaperNum}    ";
        if (null != regId && !"".equals(regId)) {
            sql = sql3 + "AND regId={regId}   ";
        } else {
            sql = sql3 + sch_room_stuId;
        }
        String sql4 = sql + ")s  LEFT JOIN (SELECT id,questionNum,answer,fullScore  FROM define WHERE examPaperNum={examPaperNum}    ) d ON  d.id = s.questionNum   LEFT JOIN (SELECT id,studentId,studentNum,studentName FROM student WHERE   ";
        if (null != studentId && !"".equals(studentId) && !"-1".equals(studentId)) {
            sql2 = sql4 + "id={studentId}    ";
        } else {
            sql2 = sql4 + "gradeNum={gradeNum}  " + schoolStr;
        }
        String sql5 = sql2 + ") st ON st.id = s.studentId   LEFT JOIN (SELECT id,examPaperNum,regId,status  FROM cliperror WHERE examPaperNum={examPaperNum}   ";
        if (null != regId && !"".equals(regId)) {
            sql5 = sql5 + "AND regId={regId}   ";
        }
        if (null != clip_succ_fail && clip_succ_fail.equals("T")) {
            sql5 = sql5 + "  AND  status ={clipError_success}   ";
        }
        if (null != clip_succ_fail && clip_succ_fail.equals("F")) {
            sql5 = sql5 + "  AND  status ={clipError_failure}   ";
        }
        String sql6 = sql5 + ")ce  ON s.regId=ce.regId   where  ce.regId IS NOT NULL  AND  ce.status IS NOT NULL    ORDER BY s.questionNum*1 LIMIT {pagestart},{pagesize} ";
        Map args1 = new HashMap();
        args1.put(License.SCHOOL, school);
        args1.put(Const.EXPORTREPORT_studentId, studentId);
        args1.put("examinationRoomNum", examinationRoomNum);
        args1.put("examPaperNum", examPaperNum);
        args1.put("regId", regId);
        args1.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args1.put("clipError_success", 1);
        args1.put("clipError_failure", 0);
        args1.put("pagestart", Integer.valueOf(pagestart));
        args1.put("pagesize", Integer.valueOf(pagesize));
        return this.dao2._queryBeanList(sql6, Score.class, args1);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public int getexampapernum(String examNum, String subjectNum, String gradeNum) {
        int aa = 0;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        if (null != this.dao2._queryStr("select examPaperNum  from exampaper  where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args) && !"".equals(this.dao2._queryStr("select examPaperNum  from exampaper  where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args)) && !"null".equals(this.dao2._queryStr("select examPaperNum  from exampaper  where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args))) {
            aa = this.dao2._queryInt("select examPaperNum  from exampaper  where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args).intValue();
        }
        return aa;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Define> getqNumList(int examPaperNum, String qtype) {
        String qtypeStr = "";
        if (qtype.equals("-1")) {
            qtypeStr = "";
        } else if (qtype.equals("0")) {
            qtypeStr = " AND questionType={QUESTION_TYPE_OBJECTIVE} ";
        } else if (qtype.equals("1")) {
            qtypeStr = " AND questionType={QUESTION_TYPE_SUBJECTIVE} ";
        }
        String sql = "SELECT id,questionNum ext1 FROM define WHERE examPaperNum={examPaperNum} " + qtypeStr + " AND choosename!='T' AND isParent!='1' UNION ALL SELECT id,questionNum   FROM subdefine WHERE examPaperNum={examPaperNum} " + qtypeStr + " ORDER BY REPLACE(ext1,'_','.')*1";
        Map args = new HashMap();
        args.put("QUESTION_TYPE_OBJECTIVE", "0");
        args.put("QUESTION_TYPE_SUBJECTIVE", "1");
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        return this.dao2._queryBeanList(sql, Define.class, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public void updateScore(String qScore, String scoreId) {
        this.examService.deleteScoreBiaoji(qScore, scoreId);
        Map args = StreamMap.create().put("qScore", (Object) qScore).put("SCORE_EXCEPTION_DAFAULT", (Object) "-1").put("scoreId", (Object) scoreId);
        this.dao2._execute("UPDATE score SET questionScore={qScore} ,isException={SCORE_EXCEPTION_DAFAULT} WHERE id={scoreId} ", args);
        this.examService.updateStudentTotalScore(scoreId);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public Integer getCount(int examPaperNum, String qtype, String examroom, String school, String qNum, String studentName, String exam, String examineeNumOStuId, String stuScore, String examroomornot, String grade, String examplace, String subject, String numbererror, String pendingAppeal, String processedAppeal, String appealed, String classPermission, String uid) {
        String examroomStr;
        String qNumStr;
        String sql;
        String levelstudentSql = "";
        String exampaperSql = "";
        String defineOnSql = "";
        String defineWhereSql = "";
        if (isXuanxue(String.valueOf(examPaperNum))) {
            levelstudentSql = " LEFT JOIN levelstudent ls ON ls.sid = s.studentId ";
            exampaperSql = " LEFT JOIN exampaper ep ON ep.gradeNum = ls.gradeNum and ep.subjectNum = ls.subjectNum and ep.pexamPaperNum = {examPaperNum}  ";
            defineOnSql = " LEFT JOIN define d on d.category = ep.examPaperNum and d.id = s.questionNum ";
            defineWhereSql = " and d.id is not null ";
        }
        String schoolStr1 = "";
        if (!school.equals("-1")) {
            schoolStr1 = " AND ob.schoolNum={school}  ";
        }
        if (!examroom.equals("-1") && examroomornot.equals("0")) {
            examroomStr = " AND examinationRoomNum={examroom} ";
        } else {
            examroomStr = "";
        }
        if (!qNum.equals("-1")) {
            qNumStr = " AND questionNum={qNum}  ";
        } else {
            qNumStr = "";
        }
        String sid = "0";
        String sidStr1 = "";
        if (null != examineeNumOStuId && !"".equals(examineeNumOStuId) && !"null".equals(examineeNumOStuId)) {
            Map args = StreamMap.create().put("examPaperNum", (Object) Integer.valueOf(examPaperNum)).put(License.SCHOOL, (Object) school).put("examroom", (Object) examroom).put("qNum", (Object) qNum).put("exam", (Object) exam).put("examineeNumOStuId", (Object) examineeNumOStuId).put("subject", (Object) subject).put("grade", (Object) grade);
            if (null != this.dao2._queryStr("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId})  and e.subjectNum ={subject} AND s.gradeNum={grade} ", args) && !"".equals(this.dao2._queryStr("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId})  and e.subjectNum ={subject} AND s.gradeNum={grade} ", args)) && !"null".equals(this.dao2._queryStr("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId})  and e.subjectNum ={subject} AND s.gradeNum={grade} ", args))) {
                sid = this.dao2._queryStr("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId})  and e.subjectNum ={subject} AND s.gradeNum={grade} ", args);
            }
            sidStr1 = " AND ob.studentId={sid} ";
        }
        Map args1 = new HashMap();
        args1.put("examPaperNum", Integer.valueOf(examPaperNum));
        args1.put(License.SCHOOL, school);
        args1.put("examroom", examroom);
        args1.put("qNum", qNum);
        args1.put("exam", exam);
        args1.put("examineeNumOStuId", examineeNumOStuId);
        args1.put("subject", subject);
        args1.put("grade", grade);
        args1.put("sid", sid);
        args1.put("stuScore", stuScore);
        args1.put("examplace", examplace);
        args1.put("studentName", "%" + studentName + "%");
        args1.put("uid", uid);
        String appealStr1 = "";
        String appealStr2 = "";
        if ((processedAppeal != null && !processedAppeal.equals("")) || (pendingAppeal != null && !pendingAppeal.equals(""))) {
            appealStr2 = " and ap.scoreId is not null";
            if (processedAppeal != null && !processedAppeal.equals("") && pendingAppeal != null && !pendingAppeal.equals("")) {
                appealStr1 = " and te.status is not null ";
            } else if (pendingAppeal != null && !pendingAppeal.equals("")) {
                appealStr1 = " and te.status = '0' ";
            } else if (processedAppeal != null && !processedAppeal.equals("")) {
                appealStr1 = " and te.status <> '0' and te.status is not null ";
            }
        }
        String appealedStr1 = "";
        if (appealed != null && !appealed.equals("")) {
            appealedStr1 = " and ap.scoreId is not null";
        }
        String stuScoreStr = "";
        if (null != stuScore && !"".equals(stuScore) && !"null".equals(stuScore)) {
            stuScoreStr = "  and questionScore={stuScore} ";
        }
        String epString = "";
        if (!examplace.equals("-1")) {
            epString = " and testingCentreId={examplace} ";
        }
        String studentStr1 = "";
        String studentStr2 = "";
        if (!studentName.equals("")) {
            studentStr1 = " LEFT JOIN student s on ob.studentid = s.id";
            studentStr2 = " and s.studentName LIKE {studentName} ";
        }
        String qtypeStr = "";
        if (qtype.equals("-1")) {
            qtypeStr = " (SELECT ob.id,ob.page, '' answer,ob. questionScore,ob.examPaperNum,1 qtype ,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,0 description,ob.regScore from score ob " + studentStr1 + " WHERE  examPaperNum={examPaperNum} " + studentStr2 + examroomStr + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + "  AND continued='F'  UNION ALL SELECT ob.id,ob.page,ob.answer,ob.questionScore,ob.examPaperNum,0 qtype ,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,ob.description,ob.regScore from objectivescore ob " + studentStr1 + " WHERE ob.exampaperNum={examPaperNum} " + studentStr2 + examroomStr + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + "  )s";
        } else if (qtype.equals("0")) {
            qtypeStr = " (SELECT ob.id,ob.page,ob.answer,ob.questionScore,ob.examPaperNum,0 qtype,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,ob.description,ob.regScore from objectivescore ob " + studentStr1 + " WHERE exampaperNum={examPaperNum} " + studentStr2 + studentStr2 + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + " )s";
        } else if (qtype.equals("1")) {
            qtypeStr = " (SELECT ob.id,ob.page, '' answer, ob.questionScore,ob.examPaperNum,1 qtype,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,0 description,ob.regScore from score ob " + studentStr1 + " WHERE  examPaperNum={examPaperNum} " + studentStr2 + studentStr2 + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + " AND continued='F' )s";
        }
        if (!studentName.equals("")) {
            String sql2 = "SELECT count(1)  FROM";
            if (classPermission != null && classPermission.equals("0")) {
                sql2 = sql2 + " (SELECT DISTINCT classNum FROM userposition_record WHERE examNum={exam} and gradeNum={grade}   AND userNum={uid} AND (type='2' OR (type='1' AND subjectNum={subject})) UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={grade}  AND userNum={uid} AND (type='2' OR (type='1' AND subjectNum={subject} ))) u LEFT JOIN";
            }
            String sql3 = sql2 + qtypeStr;
            if (classPermission != null && classPermission.equals("0")) {
                sql3 = sql3 + " ON u.classNum = s.classNum";
            }
            String sql4 = (sql3 + levelstudentSql + exampaperSql + defineOnSql) + " LEFT JOIN student st ON st.id = s.studentId LEFT JOIN (SELECT te.scoreId,te.status  FROM teacherappeal te  where 1=1 " + appealStr1 + ") ap  ON s.id=ap.scoreId   ";
            if (numbererror != null) {
                String sql1 = "SELECT count(1) FROM  define  a LEFT join (select id,choosename from define where exampapernum={examPaperNum}   GROUP BY choosename) b ON  CAST(a.id AS CHAR)=b.choosename LEFT join subdefine c  on c.pid=a.id or c.pid=b.id WHERE a.exampapernum={examPaperNum}  and LENGTH(a.choosename) < 2";
                int num = this.dao2._queryInt(sql1, args1).intValue();
                args1.put("num", Integer.valueOf(num));
                sql4 = ((sql4 + "  inner join(select a.studentid , count(1) tishu from (select studentid from objectivescore where exampapernum={examPaperNum}  UNION all select studentid from score where exampapernum={examPaperNum}    and continued='F')  a  GROUP BY  a.studentid HAVING tishu <> (") + " {num} ") + "))tishu  on tishu.studentid=s.studentId";
            }
            sql = sql4 + " where st.studentName LIKE {studentName} " + defineWhereSql + appealStr2 + appealedStr1;
        } else {
            String sql5 = "SELECT count(1)  FROM";
            if (classPermission != null && classPermission.equals("0")) {
                sql5 = sql5 + " (SELECT DISTINCT classNum FROM userposition_record WHERE examNum={exam} and gradeNum={grade}   AND userNum={uid} AND (type='2' OR (type='1' AND subjectNum={subject} )) UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={grade}   AND userNum={uid}  AND (type='2' OR (type='1' AND subjectNum={subject} ))) u LEFT JOIN";
            }
            String sql6 = sql5 + qtypeStr;
            if (classPermission != null && classPermission.equals("0")) {
                sql6 = sql6 + " ON u.classNum = s.classNum";
            }
            String sql7 = (sql6 + levelstudentSql + exampaperSql + defineOnSql) + " LEFT JOIN student st ON st.id = s.studentId LEFT JOIN (SELECT te.scoreId,te.status  FROM teacherappeal te  where 1=1 " + appealStr1 + ") ap  ON s.id=ap.scoreId   ";
            if (numbererror != null) {
                String sql12 = "SELECT count(1) FROM  define  a LEFT join (select id,choosename from define where exampapernum={examPaperNum}   GROUP BY choosename) b ON  CAST(a.id AS CHAR)=b.choosename LEFT join subdefine c  on c.pid=a.id or c.pid=b.id WHERE a.exampapernum={examPaperNum}  and LENGTH(a.choosename) < 2";
                int num2 = this.dao2._queryInt(sql12, args1).intValue();
                sql7 = ((sql7 + "  inner join(select a.studentid , count(1) tishu from (select studentid from objectivescore where exampapernum={examPaperNum}  UNION all select studentid from score where exampapernum={examPaperNum}    and continued='F')  a  GROUP BY  a.studentid HAVING tishu <> (") + num2) + "))tishu  on tishu.studentid=s.studentId";
            }
            sql = sql7 + " where 1=1 " + defineWhereSql + appealStr2 + appealedStr1;
        }
        return this.dao2._queryInt(sql, args1);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public Integer getCountClipError(String examPaperNum, String qtype, String examinationRoomNum, String school, String qNum, String studentId, String clip_succ_fail, String gradeNum, String regId, String studentName) {
        String sql;
        String sql2;
        String id;
        Map args = new HashMap();
        args.put("studentName", "%" + studentName + "%");
        args.put(License.SCHOOL, school);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("examinationRoomNum", examinationRoomNum);
        args.put("examPaperNum", examPaperNum);
        args.put("regId", regId);
        args.put("clipError_success", 1);
        args.put("clipError_failure", 0);
        if (null != studentName && !"".equals(studentName) && !"---".equals(studentName) && null != (id = this.dao2._queryStr("SELECT id FROM student WHERE studentName LIKE {studentName} ", args)) && !"".equals(id)) {
            studentId = id;
        }
        String schoolStr = "";
        if (null != school && !"".equals(school) && !school.equals("-1")) {
            schoolStr = "AND schoolNum={school}   ";
        }
        String sch_room_stuId = "";
        if (null == examinationRoomNum || "".equals(examinationRoomNum) || examinationRoomNum.equals("-1")) {
            if (null != studentId && !studentId.equals("") && !studentId.equals("-1")) {
                sch_room_stuId = sch_room_stuId + "AND  studentId={studentId}     ";
            }
            if (null != school && !"".equals(school) && !school.equals("-1")) {
                sch_room_stuId = sch_room_stuId + "AND schoolNum={school}   ";
            }
        } else {
            sch_room_stuId = sch_room_stuId + "AND examinationRoomNum={examinationRoomNum}   ";
            if (null != studentId && !studentId.equals("") && !studentId.equals("-1")) {
                sch_room_stuId = sch_room_stuId + "AND  studentId={studentId}     ";
            }
        }
        String sql3 = ((null == regId || "".equals(regId)) ? "SELECT count(1)  FROM(SELECT id,MIN(page) page, '' answer, MAX(questionScore) questionScore,examPaperNum,1 qtype ,examinationRoomNum,studentId,questionNum,schoolNum,classNum,regId from score WHERE  examPaperNum={examPaperNum}    " + sch_room_stuId : "SELECT count(1)  FROM(SELECT id,MIN(page) page, '' answer, MAX(questionScore) questionScore,examPaperNum,1 qtype ,examinationRoomNum,studentId,questionNum,schoolNum,classNum,regId from score WHERE  examPaperNum={examPaperNum}    AND regId={regId}   ") + "GROUP BY studentId,questionNum    UNION ALL SELECT id,page,answer,questionScore,examPaperNum,0 qtype ,examinationRoomNum,studentId,questionNum,schoolNum,classNum,regId from objectivescore WHERE exampaperNum={examPaperNum}    ";
        if (null != regId && !"".equals(regId)) {
            sql = sql3 + "AND regId={regId}   ";
        } else {
            sql = sql3 + sch_room_stuId;
        }
        String sql4 = sql + ")s  LEFT JOIN (SELECT id,questionNum,answer,fullScore  FROM define WHERE examPaperNum={examPaperNum}    ) d ON  d.id = s.questionNum   LEFT JOIN (SELECT id,studentId,studentNum,studentName FROM student WHERE   ";
        if (null != studentId && !"".equals(studentId) && !"-1".equals(studentId)) {
            sql2 = sql4 + "id={studentId}    ";
        } else {
            sql2 = sql4 + "gradeNum='" + gradeNum + "'  " + schoolStr;
        }
        String sql5 = sql2 + ") st ON st.id = s.studentId   LEFT JOIN (SELECT id,examPaperNum,regId,`status`  FROM cliperror WHERE examPaperNum={examPaperNum}   ";
        if (null != regId && !"".equals(regId)) {
            sql5 = sql5 + "AND regId={regId}   ";
        }
        if (null != clip_succ_fail && clip_succ_fail.equals("T")) {
            sql5 = sql5 + "  AND  `status` ={clipError_success}  ";
        }
        if (null != clip_succ_fail && clip_succ_fail.equals("F")) {
            sql5 = sql5 + "  AND  `status` ={clipError_failure}   ";
        }
        return this.dao2._queryInt(sql5 + ")ce  ON s.regId=ce.regId   where  ce.regId IS NOT NULL  AND  ce.`status` IS NOT NULL    ORDER BY s.questionNum*1 ", args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Define> getSwitchQues(String examPaperNum, String defineId, String subdefineId, String choosename, String isParent) {
        return this.awardPointDaoImpl.chooseQuestionNumList(examPaperNum, null, subdefineId, choosename, "1", isParent);
    }

    /* JADX WARN: Finally extract failed */
    @Override // com.dmj.service.examManagement.QuestionNumListService
    public int modifyQuesNum(String examPaperNum, String scoreId, String studentId, String qytpe, String newQuestionNum, String isParent, String proofreadingSource) {
        boolean cflg;
        int i;
        Integer rtn = 0;
        String oldQuesNum = "";
        String regId = "";
        String sql2 = ((null == qytpe || !qytpe.equals("1")) ? "SELECT id,studentId,questionNum,regScore ext1,regId ,answer FROM objectivescore   " : "SELECT id,studentId,questionNum,regScore ext1,regId ,'' answer FROM score   ") + " WHERE id={scoreId} AND examPaperNum={examPaperNum} ";
        new ArrayList();
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("examPaperNum", examPaperNum);
        args.put("newQuestionNum", newQuestionNum);
        if (null == studentId || studentId.equals("")) {
            if (null != qytpe && qytpe.equals("1")) {
                List sList = this.dao2._queryBeanList(sql2, Score.class, args);
                if (null != sList && sList.size() > 0) {
                    oldQuesNum = String.valueOf(((Score) sList.get(0)).getQuestionNum());
                    studentId = String.valueOf(((Score) sList.get(0)).getStudentId());
                    regId = String.valueOf(((Score) sList.get(0)).getRegId());
                }
            } else {
                List sList2 = this.dao2._queryBeanList(sql2, ObjectiveScore.class, args);
                if (null != sList2 && sList2.size() > 0) {
                    oldQuesNum = String.valueOf(((ObjectiveScore) sList2.get(0)).getQuestionNum());
                    studentId = String.valueOf(((ObjectiveScore) sList2.get(0)).getStudentId());
                    regId = String.valueOf(((ObjectiveScore) sList2.get(0)).getRegId());
                    ((ObjectiveScore) sList2.get(0)).getExt1();
                    ((ObjectiveScore) sList2.get(0)).getAnswer();
                }
            }
        }
        if (null != isParent && isParent.equals("1")) {
            List questionNumList = new ArrayList();
            Connection conn = DbUtils.getConnection();
            CallableStatement pstat = null;
            ResultSet rs = null;
            try {
                try {
                    pstat = conn.prepareCall("{call /* shard_host_HG=Read */ queryChooseQues(?,?,?)}");
                    pstat.setString(1, scoreId);
                    pstat.setString(2, newQuestionNum);
                    pstat.setString(3, qytpe);
                    pstat.executeQuery();
                    rs = pstat.getResultSet();
                    while (rs.next()) {
                        int count = rs.getMetaData().getColumnCount();
                        Object[] rowArr = new Object[count];
                        for (int i2 = 0; i2 < rowArr.length; i2++) {
                            rowArr[i2] = rs.getObject(i2 + 1);
                            if (i2 == rowArr.length - 1) {
                                questionNumList.add(rowArr);
                            }
                        }
                    }
                    DbUtils.close(rs, pstat, conn);
                    if (null != questionNumList && questionNumList.size() > 0) {
                        boolean flg = false;
                        boolean flg2 = true;
                        for (int i22 = 0; i22 < questionNumList.size(); i22++) {
                            Object[] rowArr2 = (Object[]) questionNumList.get(i22);
                            if (flg2) {
                                String cross_page = String.valueOf(rowArr2[5]);
                                regId = String.valueOf(rowArr2[10]);
                                if (cross_page.equals("F")) {
                                    flg2 = false;
                                    flg = true;
                                }
                            }
                        }
                        for (int i3 = 0; i3 < questionNumList.size(); i3++) {
                            Object[] rowArr3 = (Object[]) questionNumList.get(i3);
                            String qytpe2 = String.valueOf(rowArr3[4]);
                            String newQuestionNum2 = String.valueOf(rowArr3[2]);
                            String pid = String.valueOf(rowArr3[7]);
                            String oldQuestionNum = String.valueOf(rowArr3[0]);
                            String oldPid = String.valueOf(rowArr3[8]);
                            String merge = String.valueOf(rowArr3[6]);
                            String pidi = "pidi" + i3;
                            args.put(pidi, pid);
                            Integer orderNum = 0;
                            if (i3 == 0) {
                                cflg = true;
                                String choosename = "";
                                String defsql = "SELECT id,questionNum,orderNum,choosename FROM define WHERE id={" + pidi + "} AND examPaperNum={examPaperNum} ";
                                List defList = this.dao2._queryBeanList(defsql, Define.class, args);
                                if (null != defList && defList.size() > 0) {
                                    choosename = ((Define) defList.get(0)).getChoosename();
                                }
                                String choosenamei = "choosenamei" + i3;
                                args.put(choosenamei, choosename);
                                String defsql2 = "SELECT id,questionNum,choosename,orderNum,(@rowNum:=@rowNum+1) ext1  FROM define,(SELECT (@rowNum :=0) ) b WHERE choosename={" + choosenamei + "}  ORDER BY orderNum";
                                List defList2 = this.dao2._queryBeanList(defsql2, Define.class, args);
                                if (null != defList2 && defList2.size() > 0) {
                                    int z = 0;
                                    int j = 0;
                                    while (true) {
                                        if (j >= defList2.size()) {
                                            break;
                                        }
                                        String curDefId = ((Define) defList2.get(j)).getId();
                                        if (curDefId.equals(pid)) {
                                            orderNum = Integer.valueOf(z);
                                            break;
                                        }
                                        z += 2;
                                        j++;
                                    }
                                }
                            } else {
                                cflg = false;
                            }
                            if (!newQuestionNum2.equals(oldQuestionNum)) {
                                i = Integer.valueOf(rtn.intValue() + modifyQuesNum_allques(examPaperNum, studentId, qytpe2, newQuestionNum2, pid, oldQuestionNum, oldPid, merge, orderNum, regId, flg, cflg, proofreadingSource));
                            } else {
                                i = 1;
                            }
                            rtn = i;
                        }
                    }
                } catch (SQLException e) {
                    Log.info("选做题切换题目", e);
                    throw new RuntimeException(e);
                }
            } catch (Throwable th) {
                DbUtils.close(rs, pstat, conn);
                throw th;
            }
        } else if (!newQuestionNum.equals(oldQuesNum)) {
            Integer orderNum2 = 0;
            String choosename2 = "";
            List defList3 = this.dao2._queryBeanList("SELECT id,questionNum,orderNum,choosename FROM define WHERE id={newQuestionNum} AND examPaperNum={examPaperNum} ", Define.class, args);
            if (null != defList3 && defList3.size() > 0) {
                choosename2 = ((Define) defList3.get(0)).getChoosename();
            }
            args.put("choosename", choosename2);
            List defList22 = this.dao2._queryBeanList("SELECT id,questionNum,choosename,orderNum,(@rowNum:=@rowNum+1) ext1  FROM define,(SELECT (@rowNum :=0) ) b WHERE choosename={choosename}  ORDER BY orderNum", Define.class, args);
            if (null != defList22 && defList22.size() > 0) {
                int z2 = 0;
                int i4 = 0;
                while (true) {
                    if (i4 >= defList22.size()) {
                        break;
                    }
                    String curDefId2 = ((Define) defList22.get(i4)).getId();
                    if (curDefId2.equals(newQuestionNum)) {
                        orderNum2 = Integer.valueOf(z2);
                        break;
                    }
                    z2 += 2;
                    i4++;
                }
            }
            rtn = Integer.valueOf(modifyQuesNum_allques(examPaperNum, studentId, qytpe, newQuestionNum, newQuestionNum, oldQuesNum, oldQuesNum, "0", orderNum2, regId, true, true, proofreadingSource));
        } else {
            rtn = 1;
        }
        return rtn.intValue();
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public int modifyQuesNum_allques(String examPaperNum, String studentId, String qytpe, String newQuestionNum, String pid, String oldQuestionNum, String oldPid, String merge, Integer newOrderNum, String regId, boolean regBool, boolean updateBool, String proofreadingSource) {
        Integer rtn = 0;
        Map args = new HashMap();
        args.put("oldQuestionNum", oldQuestionNum);
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        String sql3 = ((null == qytpe || !qytpe.equals("1")) ? "SELECT id,studentId,questionNum,regScore ext1 ,answer FROM objectivescore   " : "SELECT id,studentId,questionNum,regScore ext1 ,'' answer FROM score   ") + " WHERE questionNum={oldQuestionNum} AND examPaperNum={examPaperNum}  AND studentId={studentId}   ";
        new ArrayList();
        if (null != qytpe && qytpe.equals("1")) {
            List list3 = this.dao2._queryBeanList(sql3, Score.class, args);
            if (null != list3 && list3.size() > 0) {
                for (int j = 0; j < list3.size(); j++) {
                    String scoreId = String.valueOf(((Score) list3.get(j)).getId());
                    String studentId2 = String.valueOf(((Score) list3.get(j)).getStudentId());
                    rtn = Integer.valueOf(rtn.intValue() + modifyQuesNum_small(examPaperNum, scoreId, studentId2, qytpe, newQuestionNum, pid, "", "", merge, oldQuestionNum, oldPid, newOrderNum, regId, regBool, updateBool, proofreadingSource));
                }
            }
        } else {
            List list32 = this.dao2._queryBeanList(sql3, ObjectiveScore.class, args);
            if (null != list32 && list32.size() > 0) {
                for (int j2 = 0; j2 < list32.size(); j2++) {
                    String scoreId2 = String.valueOf(((ObjectiveScore) list32.get(j2)).getId());
                    String studentId3 = String.valueOf(((ObjectiveScore) list32.get(j2)).getStudentId());
                    String regScore = ((ObjectiveScore) list32.get(j2)).getExt1();
                    String answer = ((ObjectiveScore) list32.get(j2)).getAnswer();
                    rtn = Integer.valueOf(rtn.intValue() + modifyQuesNum_small(examPaperNum, scoreId2, studentId3, qytpe, newQuestionNum, pid, regScore, answer, merge, oldQuestionNum, oldPid, newOrderNum, regId, regBool, updateBool, proofreadingSource));
                }
            }
        }
        return rtn.intValue();
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public int modifyQuesNum_small(String examPaperNum, String scoreId, String studentId, String qytpe, String newQuestionNum, String pid, String regScore, String answer, String merge, String oldQuestionNum, String oldPid, Integer newOrderNum, String regId, boolean regBool, boolean updateBool, String proofreadingSource) {
        Integer rtn = 0;
        String sql_score = (null == qytpe || !qytpe.equals("1")) ? " update objectivescore   " : " update score   ";
        Map args = StreamMap.create().put("newQuestionNum", (Object) newQuestionNum).put("scoreId", (Object) scoreId);
        Integer a = Integer.valueOf(this.dao2._execute(sql_score + " a  set    a.questionNum={newQuestionNum} ,a.questionScore='0',isModify='F' where  a.id={scoreId} ", args));
        if (null == a) {
            a = 0;
        }
        Integer rtn2 = Integer.valueOf(rtn.intValue() + a.intValue());
        if (regBool && updateBool) {
            Integer a2 = updateChooseRecord(regId, newOrderNum, oldPid, pid, proofreadingSource);
            if (null == a2) {
                a2 = 0;
            }
            rtn2 = Integer.valueOf(rtn2.intValue() + a2.intValue());
        }
        this.awardPointDaoImpl.updatexzwork(scoreId, merge, oldQuestionNum, newQuestionNum, oldPid, pid);
        new ArrayList();
        if (null != qytpe && qytpe.equals("0")) {
            int val = 0;
            if (null != answer && !"".equals(answer) && !"null".equals(answer)) {
                val = ItemThresholdRegUtil.getThreshold(regScore, answer);
            }
            this.examService.updateAnswer(examPaperNum, newQuestionNum, answer, null, scoreId, val);
        }
        return rtn2.intValue();
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public Integer updateChooseRecord(String regId, Integer newOrderNum, String oldPid, String pid, String proofreadingSource) {
        String sql_choosenamerecord = "";
        Map args = new HashMap();
        args.put("regId", regId);
        args.put("oldPid", oldPid);
        args.put("pid", pid);
        args.put("proofreadingSource", proofreadingSource);
        List list1 = this.dao2._queryBeanList("SELECT id,regId,choosename,questionNum,regStr,max  FROM choosenamerecord WHERE regId={regId}  AND questionNum={oldPid} ", Choosenamerecord.class, args);
        if (null != list1 && list1.size() > 0) {
            String max = ((Choosenamerecord) list1.get(0)).getMax();
            String regStr = ((Choosenamerecord) list1.get(0)).getRegStr();
            args.put("max", max);
            if (regStr.length() >= newOrderNum.intValue() + 2) {
                regStr.substring(newOrderNum.intValue(), newOrderNum.intValue() + 2);
            }
            sql_choosenamerecord = "UPDATE choosenamerecord SET questionNum={pid} ,max={max} ,isModify='1',proofreadingStatus='1',proofreadingSource={proofreadingSource}  WHERE regId={regId}  AND questionNum={oldPid} ";
        }
        if (sql_choosenamerecord.length() > 0) {
            return Integer.valueOf(this.dao2._execute(sql_choosenamerecord, args));
        }
        return null;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public boolean ModifyChooseNameInfo(Map<String, String> map) {
        try {
            Map args = new HashMap();
            args.put("reg_id", map.get("reg_id"));
            args.put("newQues", map.get("newQues"));
            args.put("choose_name", map.get("choose_name"));
            args.put("order_num", map.get("order_num"));
            args.put("is_parent", map.get("is_parent"));
            args.put("isMerge", map.get("isMerge"));
            this.dao2._execute("call updateChooseQuestion({reg_id},{newQues},{choose_name},{order_num},{is_parent},{isMerge})", args);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List isCrossPage(String examPaperNum, Integer page) {
        new ArrayList();
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("page", (Object) page);
        List li = this.dao2._queryBeanList("SELECT sub.id,sub.questionNum,sub.questionType,sub.choosename,sub.cross_page,sub.page,sub.pid,def.choosename ext1 FROM subdefine sub   LEFT JOIN define def ON sub.pid=def.id AND def.examPaperNum={examPaperNum}   WHERE sub.examPaperNum={examPaperNum}  AND sub.cross_page='T' AND sub.page={page} ", Define.class, args);
        if (null == li || li.size() == 0) {
            return null;
        }
        return li;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List updateAllChildQues(String examPaperNum, String testCenter, String studentId, Integer curPage) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("regId", "");
        args.put("chooseName", "");
        args.put("curPage", curPage);
        args.put("a", -1);
        List li0 = this.dao2._queryBeanList("SELECT /* shard_host_HG=Write */ def.id,def.page,def.questionNum,reg.id ext1,reg.cNum ext2  FROM regexaminee reg  LEFT JOIN define def ON reg.examPaperNum={examPaperNum}  AND studentId={studentId}   AND reg.page=def.page  WHERE def.examPaperNum={examPaperNum}  AND def.cross_page='T' AND def.choosename='T' AND def.isParent='1'  ORDER BY reg.page,def.questionNum,def.orderNum", Define.class, args);
        if (null != li0 && li0.size() > 0) {
            for (int i = 0; i < li0.size(); i++) {
                String chooseName = ((Define) li0.get(i)).getId();
                String regId = ((Define) li0.get(i)).getExt1();
                Integer curPage2 = Integer.valueOf(((Define) li0.get(i)).getPage());
                String chooseNamei = "chooseNamei" + i;
                args.put(chooseNamei, chooseName);
                String regIdi = "regIdi" + i;
                args.put(regIdi, regId);
                String curPagei = "curPagei" + i;
                args.put(curPagei, curPage2);
                String sql3 = "SELECT /* shard_host_HG=Write */ def.page ext1,def.questionNum ext2,cr.questionNum ,def.choosename,def.isParent ext3,cr.max,cr.regStr  FROM define def  LEFT JOIN choosenamerecord cr ON cr.regId={" + regIdi + "}  AND cr.choosename={" + chooseNamei + "} AND def.id=cr.questionNum   WHERE def.examPaperNum={examPaperNum}  AND def.choosename={" + chooseNamei + "}  AND cr.id IS NOT NULL";
                List li3 = this.dao2._queryBeanList(sql3, Choosenamerecord.class, args);
                String sql2 = "SELECT /* shard_host_HG=Write */ sc.id,sc.questionNum,sc.page,sub.questionType,sub.pid ext1,sc.regId   FROM  subdefine sub   LEFT JOIN (  SELECT id,questionNum,page FROM score WHERE examPaperNum={examPaperNum}  AND studentId={studentId}   AND regId={" + regIdi + "}    UNION ALL  SELECT id,questionNum,page FROM objectivescore WHERE examPaperNum={examPaperNum}  AND studentId={studentId}   AND regId={" + regIdi + "}   ) sc  ON sub.examPaperNum={examPaperNum}  AND sub.cross_page='T' AND sub.page={" + curPagei + "}  AND sub.id=sc.questionNum   WHERE sub.examPaperNum={examPaperNum}  AND sub.cross_page='T' AND sub.page={" + curPagei + "}  ORDER BY sc.page DESC";
                List li2 = this.dao2._queryBeanList(sql2, Score.class, args);
                if (null != li3 && li3.size() > 0 && null != li2 && li2.size() > 0) {
                    for (int j = 0; j < li3.size(); j++) {
                        String cPid = ((Choosenamerecord) li3.get(j)).getQuestionNum();
                        String cPage = ((Choosenamerecord) li3.get(j)).getExt1();
                        if (j < li2.size()) {
                            String sPid = ((Score) li2.get(j)).getExt1();
                            String sPage = String.valueOf(((Score) li2.get(j)).getPage());
                            String cPidi = "cPidi" + i;
                            args.put(cPidi, cPid);
                            if (!cPid.equals(sPid) && cPage.equals(sPage)) {
                                String sql = "CALL updateChooseQuestion({" + regIdi + "},{" + cPidi + "},{" + chooseNamei + "},{a},{a},{a})";
                                try {
                                    Object[] objArr = {regId, cPid, chooseName, "-1", "-1", "-1"};
                                    this.dao2._execute(sql, args);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
        return null;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public Integer modifyObjectieveSwithAB(String examPaperNum, String stuId, Integer cur_page, String reg_id) {
        try {
            Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("stuId", (Object) stuId).put("cur_page", (Object) cur_page.toString()).put("reg_id", (Object) reg_id);
            this.dao2._execute("CALL modifyObjectieveSwithAB({examPaperNum},{stuId},{cur_page},{reg_id})", args);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> list_Teacher(String qtype, int examPaperNum, String examinationRoomNum, int pagestart, int pagesize, String studentName, String school, String qNum, String jie, String stuScore, String examineeNumOStuId, String exam, String gradeNum, String flag, String examroomornot, String examplace, String subject, String appeal, String classPermission, String uid) {
        String schoolStr;
        String qNumStr;
        String sql;
        String sql2;
        String sql3;
        String flagstr = "";
        if (!flag.equals("F")) {
            flagstr = "  LIMIT {pagestart},{pagesize} ";
        }
        String examroom = "";
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (!examinationRoomNum.equals("-1") && examroomornot.equals("0")) {
            examroom = " and examinationRoomNum={examinationRoomNum}   ";
        }
        String schoolStr1 = "";
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = "AND schoolNum={school}   ";
            schoolStr1 = "AND  ob.schoolNum={school}   ";
        }
        int sid = 0;
        String sidStr1 = "";
        Map args = new HashMap();
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pagesize", Integer.valueOf(pagesize));
        args.put("examinationRoomNum", examinationRoomNum);
        args.put(License.SCHOOL, school);
        args.put("exam", exam);
        args.put("examineeNumOStuId", examineeNumOStuId);
        args.put("subject", subject);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("sid", 0);
        args.put("qNum", qNum);
        args.put("stuScore", studentName);
        args.put("examplace", examplace);
        args.put("studentName", "%" + studentName + "%");
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("uid", uid);
        if (null != examineeNumOStuId && !"".equals(examineeNumOStuId) && !"null".equals(examineeNumOStuId)) {
            if (null != this.dao2._queryStr("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId}) and e.subjectNum={subject} and s.gradeNum={gradeNum} ", args) && !"".equals(this.dao2._queryStr("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId}) and e.subjectNum={subject} and s.gradeNum={gradeNum} ", args)) && !"null".equals(this.dao2._queryStr("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId}) and e.subjectNum={subject} and s.gradeNum={gradeNum} ", args))) {
                sid = this.dao2._queryInt("SELECT  s.id FROM student s LEFT JOIN examinationnum e ON s.id=e.studentId WHERE  e.examNum={exam} AND ( s.studentId={examineeNumOStuId} OR e.examineeNum={examineeNumOStuId}) and e.subjectNum={subject} and s.gradeNum={gradeNum} ", args).intValue();
            }
            sidStr1 = " AND ob.studentId={sid}  ";
        }
        String appealStr = "";
        if (appeal != null && !appeal.equals("")) {
            appealStr = " and ap.scoreId is not null ";
        }
        if (qNum.equals("-1")) {
            qNumStr = "";
        } else {
            qNumStr = " AND questionNum={qNum}   ";
        }
        String stuScoreStr = "";
        if (null != stuScore && !"".equals(stuScore) && !"null".equals(stuScore)) {
            stuScoreStr = "  and questionScore={stuScore}  ";
        }
        String epString = "";
        if (!examplace.equals("-1")) {
            epString = " and testingCentreId={examplace} ";
        }
        String studentStr1 = "";
        String studentStr2 = "";
        if (!studentName.equals("")) {
            studentStr1 = " LEFT JOIN student s on ob.studentid = s.id";
            studentStr2 = " and s.studentName LIKE {studentName} ";
        }
        String qtypeStr = "";
        if (qtype.equals("-1")) {
            qtypeStr = " (SELECT ob.id,ob.page, '' answer,ob. questionScore,ob.examPaperNum,1 qtype ,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,0 description,ob.regScore,ob.insertUser from score ob " + studentStr1 + " WHERE  examPaperNum={examPaperNum} " + studentStr2 + examroom + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + "  AND continued='F'  UNION ALL SELECT ob.id,ob.page,ob.answer,ob.questionScore,ob.examPaperNum,0 qtype ,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,ob.description,ob.regScore,ob.insertUser from objectivescore ob " + studentStr1 + " WHERE ob.exampaperNum={examPaperNum} " + studentStr2 + examroom + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + "  )s";
        } else if (qtype.equals("0")) {
            qtypeStr = " (SELECT ob.id,ob.page,ob.answer,ob.questionScore,ob.examPaperNum,0 qtype,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,ob.description,ob.regScore,ob.insertUser from objectivescore ob " + studentStr1 + " WHERE exampaperNum={examPaperNum} " + studentStr2 + examroom + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + " )s";
        } else if (qtype.equals("1")) {
            qtypeStr = " (SELECT ob.id,ob.page, '' answer, ob.questionScore,ob.examPaperNum,1 qtype,ob.examinationRoomNum,ob.studentId,ob.questionNum,ob.schoolNum,ob.classNum,ob.regId,0 description,ob.regScore,ob.insertUser from score ob " + studentStr1 + " WHERE  examPaperNum={examPaperNum} " + studentStr2 + examroom + schoolStr1 + qNumStr + sidStr1 + stuScoreStr + epString + " AND continued='F' )s";
        }
        if (!studentName.equals("")) {
            String sql4 = "SELECT s.id ext2,s.insertUser,s.regId regId, s.studentId ext8,st.studentName studentName,st.studentId realStudentId,d.qNum ext3,d.id questionNum,s.answer answer,CASE WHEN ill.exampaperType='B' THEN d.answer_b  ELSE  d.answer  END AS yanswer,s.questionScore questionScore,s.examPaperNum examPaperNum,s.examinationRoomNum examinationRoomNum,d.fullScore ext1,s.qtype,s.schoolNum ext5,d.optionCount ext4 ,s.page page,d.choosename,d.isParent,d.defId ,s.regScore ext6,ifnull(ex.appealDate,'')appealDate,case when ap.status = 0 then '申诉' when ap.status=1 then '申诉修改' when ap.status = 2 then '申诉驳回' else '' end appealStatus,if(ap.appealer='-2','dmj',if(ap.appealer is null,'',ap.realname)) appealer  FROM";
            if (classPermission != null && classPermission.equals("0")) {
                sql4 = sql4 + " (SELECT DISTINCT classNum FROM userposition_record WHERE examNum={exam} and gradeNum={gradeNum}   AND userNum={uid}  AND (type='2' OR (type='1' AND subjectNum={subject} ))  UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={gradeNum}   AND userNum={uid}  AND (type='2' OR (type='1' AND subjectNum={subject} )) ) u LEFT JOIN";
            }
            String sql5 = sql4 + qtypeStr;
            if (classPermission != null && classPermission.equals("0")) {
                sql5 = sql5 + " ON u.classNum = s.classNum";
            }
            String sql6 = sql5 + " LEFT JOIN (SELECT regId,exampaperType,studentId,examPaperNum FROM illegal WHERE examPaperNum={examPaperNum} " + schoolStr + ") ill ON  s.studentId=ill.studentId  AND s.examPaperNum=ill.examPaperNum  LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore, CASE WHEN d.isParent ='0' THEN d.answer ELSE s.answer END AS answer, CASE WHEN d.isParent ='0' THEN d.answer_b ELSE s.answer_b END AS answer_b, CASE WHEN d.isParent ='0' THEN d.optionCount ELSE s.optionCount END AS optionCount, d.choosename, isParent,d.id defId,  CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum FROM define d LEFT JOIN subdefine s ON d.id=s.pid   WHERE d.examPaperNum={examPaperNum} )d ON  d.id = s.questionNum  LEFT JOIN (SELECT id,studentId,studentNum,studentName FROM student WHERE  ";
            if (sid != 0) {
                sql3 = sql6 + "id='" + sid + "'  ";
            } else {
                sql3 = sql6 + "gradeNum={gradeNum}  " + schoolStr + "  ";
            }
            sql2 = (sql3 + ") st ON st.id = s.studentId    LEFT JOIN (SELECT id,examPaperNum,regId,status  FROM cliperror WHERE examPaperNum={examPaperNum} )ce  ON s.regId=ce.regId   LEFT JOIN (SELECT examPaperNum,appealDate  FROM examPaper WHERE examPaperNum={examPaperNum} ) ex  ON s.examPaperNum=ex.examPaperNum   LEFT JOIN (SELECT te.scoreId,te.status,te.appealer,u.realname  FROM teacherappeal te left join user u on te.appealer = u.id ) ap  ON s.id=ap.scoreId   ") + " where st.studentName LIKE {studentName} " + appealStr + "  ORDER BY d.orderNum,s.answer,s.description " + flagstr;
        } else {
            String sql7 = "SELECT s.id ext2,s.insertUser,s.regId regId, s.studentId ext8,st.studentName studentName,st.studentId realStudentId,d.qNum ext3,d.id questionNum,s.answer answer,CASE WHEN ill.exampaperType='B' THEN d.answer_b  ELSE  d.answer  END AS yanswer,s.questionScore questionScore,s.examPaperNum examPaperNum,s.examinationRoomNum examinationRoomNum,d.fullScore ext1,s.qtype qtype,s.schoolNum ext5,d.optionCount ext4,s.page page,d.choosename,d.isParent,d.defId ,s.regScore ext6,ifnull(ex.appealDate,'')appealDate,case when ap.status = 0 then '申诉' when ap.status=1 then '申诉修改' when ap.status = 2 then '申诉驳回' else '' end appealStatus,if(ap.appealer='-2','dmj',if(ap.appealer is null,'',ap.realname)) appealer  FROM";
            if (classPermission != null && classPermission.equals("0")) {
                sql7 = sql7 + " (SELECT DISTINCT classNum FROM userposition_record WHERE examNum={exam} and gradeNum={gradeNum}  AND userNum={uid}  AND (type='2' OR (type='1' AND subjectNum={subject} )) UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={gradeNum}  AND userNum={uid}  AND (type='2' OR (type='1' AND subjectNum={subject} ))) u LEFT JOIN";
            }
            String sql8 = sql7 + qtypeStr;
            if (classPermission != null && classPermission.equals("0")) {
                sql8 = sql8 + " ON u.classNum = s.classNum";
            }
            String sql9 = sql8 + " LEFT JOIN (SELECT regId,exampaperType,studentId,examPaperNum FROM illegal WHERE examPaperNum={examPaperNum} " + schoolStr + ")ill ON  s.studentId=ill.studentId  AND s.examPaperNum=ill.examPaperNum  LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore, CASE WHEN d.isParent ='0' THEN d.answer ELSE s.answer END AS answer, CASE WHEN d.isParent ='0' THEN d.answer_b ELSE s.answer_b END AS answer_b, CASE WHEN d.isParent ='0' THEN d.optionCount ELSE s.optionCount END AS optionCount,  d.choosename, isParent,d.id defId,   CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum FROM define d LEFT JOIN subdefine s ON d.id=s.pid  WHERE d.examPaperNum={examPaperNum}  )d ON  d.id = s.questionNum  LEFT JOIN (SELECT id,studentId,studentNum,studentName FROM student WHERE   ";
            if (sid != 0) {
                sql = sql9 + "id={sid}  ";
            } else {
                sql = sql9 + "gradeNum={gradeNum}  " + schoolStr + "  ";
            }
            sql2 = (sql + ") st ON st.id = s.studentId   LEFT JOIN (SELECT id,examPaperNum,regId,status  FROM cliperror WHERE examPaperNum={examPaperNum} )ce  ON s.regId=ce.regId   LEFT JOIN (SELECT examPaperNum,appealDate  FROM examPaper WHERE examPaperNum={examPaperNum} ) ex  ON s.examPaperNum=ex.examPaperNum   LEFT JOIN (SELECT te.scoreId,te.status,te.appealer,u.realname  FROM teacherappeal te left join user u on te.appealer = u.id ) ap  ON s.id=ap.scoreId   ") + " where 1=1 " + appealStr + " ORDER BY d.orderNum,s.answer,s.description " + flagstr;
        }
        Map args1 = new HashMap();
        args1.put("pagestart", Integer.valueOf(pagestart));
        args1.put("pagesize", Integer.valueOf(pagesize));
        args1.put("examinationRoomNum", examinationRoomNum);
        args1.put(License.SCHOOL, school);
        args1.put("exam", exam);
        args1.put("examineeNumOStuId", examineeNumOStuId);
        args1.put("subject", subject);
        args1.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args1.put("sid", Integer.valueOf(sid));
        args1.put("qNum", qNum);
        args1.put("stuScore", studentName);
        args1.put("examplace", examplace);
        args1.put("studentName", "%" + studentName + "%");
        args1.put("examPaperNum", Integer.valueOf(examPaperNum));
        args1.put("uid", uid);
        return this.dao2._queryMapList(sql2, null, args1);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String appeal(Map map) {
        String flag = "";
        String scoreId = map.get("scoreId") + "";
        Map args = StreamMap.create().put("scoreId", (Object) scoreId);
        List list = this.dao2._queryColList("select count(1) a_num from teacherappeal te where te.scoreId={scoreId} ", args);
        if (Integer.parseInt(list.get(0) + "") > 0) {
            return "该条记录已被申诉过！";
        }
        Set<String> set = map.keySet();
        String columns = "(";
        String values = "(";
        for (String key : set) {
            columns = columns + key + Const.STRING_SEPERATOR;
            values = values + map.get(key) + Const.STRING_SEPERATOR;
        }
        String sql = "insert into teacherappeal" + (columns + "insertDate)") + " value" + (values + "NOW())");
        try {
            Map args1 = StreamMap.create().put("scoreId", (Object) scoreId);
            int count = this.dao2._execute(sql, args1);
            if (count > 0) {
                flag = "T";
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = "F";
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String reject(String scoreId, String loginUser, String rejectReason) {
        String flag = "F";
        if (scoreId != null && !scoreId.equals("")) {
            String rejectReasonStr = "";
            if (rejectReason != null && !rejectReason.equals("")) {
                rejectReasonStr = ",rejectReason='" + rejectReason + "'";
            }
            String sql = "update teacherappeal set status = '2',updater={loginUser} " + rejectReasonStr + " where scoreId={scoreId} ";
            Map args = StreamMap.create().put("loginUser", (Object) loginUser).put("scoreId", (Object) scoreId);
            int count = this.dao2._execute(sql, args);
            if (count > 0) {
                flag = "T";
            }
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String modify(String scoreId, String loginUser, String qScore) {
        String flag = "F";
        if (scoreId != null && !scoreId.equals("")) {
            Map args = StreamMap.create().put("loginUser", (Object) loginUser).put("qScore", (Object) qScore).put("scoreId", (Object) scoreId);
            int count = this.dao2._execute("update teacherappeal set status = '1',updater={loginUser},scoremodified={qScore} where scoreId={scoreId} ", args);
            if (count > 0) {
                flag = "T";
            }
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String cancelAppeal(String scoreId) {
        String flag = "F";
        if (scoreId != null && !scoreId.equals("")) {
            Map args = StreamMap.create().put("scoreId", (Object) scoreId);
            int count = this.dao2._execute("delete from teacherappeal where scoreId={scoreId} ", args);
            if (count > 0) {
                flag = "T";
            }
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String getLoginRealName(String userId) {
        String realName = "";
        if (userId != null && !userId.equals("")) {
            Map args = StreamMap.create().put("userId", (Object) userId);
            List list = this.dao2._queryColList("select realname from user where userId={userId} ", args);
            if (list != null && list.size() > 0) {
                realName = list.get(0) + "";
            }
        }
        return realName;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getAppealDealList() {
        return null;
    }

    public List<Map<String, Object>> getSubjectInfo_old(String exam, String userId) {
        String sql = "select distinct ex.subjectNum,s.subjectName FROM exampaper ex LEFT JOIN (select exampaperNum from questiongroup_user where userNum={userId}  AND userType <> '0' GROUP BY exampaperNum) t ON ex.examPaperNum = t.exampaperNum LEFT JOIN `subject` s ON ex.subjectNum = s.subjectNum LEFT JOIN teacherappeal te on ex.pexamPaperNum = te.exampaperNum WHERE ex.examNum={exam}  AND t.exampaperNum is not null and te.exampaperNum is not null";
        if (userId.equals("-2") || userId.equals("-1")) {
            sql = "select distinct ex.subjectNum,s.subjectName FROM exampaper ex LEFT JOIN `subject` s ON ex.subjectNum = s.subjectNum LEFT JOIN teacherappeal te on ex.exampaperNum = te.exampaperNum WHERE  s.isDelete='F' and te.exampaperNum is not null and s.isHidden = 'F' and ex.examPaperNum = ex.pexamPaperNum and ex.examNum={exam} ";
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put("exam", (Object) exam);
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getSubjectInfo(String exam, String userId) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("userId", (Object) userId);
        if ("-2".equals(userId) || "-1".equals(userId)) {
            return this.dao2._queryMapList("select distinct ex.subjectNum,s.subjectName FROM exampaper ex LEFT JOIN `subject` s ON ex.subjectNum = s.subjectNum WHERE s.isDelete='F' and s.isHidden = 'F' and ex.examPaperNum = ex.pexamPaperNum and ex.examNum={exam} order by s.orderNum,s.subjectNum", null, args);
        }
        return this.dao2._queryMapList("select distinct ep2.subjectNum,sub.subjectName FROM exampaper ep inner join questiongroup_user qgu on qgu.exampaperNum = ep.examPaperNum inner join exampaper ep2 on ep2.examPaperNum = ep.pexamPaperNum inner join `subject` sub on sub.subjectNum = ep2.subjectNum WHERE qgu.userNum={userId} and qgu.userType <> '0' and ep.examNum={exam} order by sub.orderNum,sub.subjectNum", null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getSubjectInfo4(String exam, String userId) {
        String sql = "select distinct ex.subjectNum,s.subjectName FROM exampaper ex LEFT JOIN (select exampaperNum from questiongroup_user where userNum={userId}  AND userType <> '0' GROUP BY exampaperNum) t ON ex.examPaperNum = t.exampaperNum LEFT JOIN `subject` s ON ex.subjectNum = s.subjectNum LEFT JOIN assistYuejuan te on ex.examNUm = te.examNum and ex.subjectNUm=te.subjectNum and ex.gradeNum= te.gradeNum WHERE ex.examNum={exam}  AND t.exampaperNum is not null and te.id is not null";
        if (userId.equals("-2") || userId.equals("-1")) {
            sql = "select distinct ex.subjectNum,s.subjectName FROM exampaper ex LEFT JOIN `subject` s ON ex.subjectNum = s.subjectNum LEFT JOIN assistYuejuan te on ex.examNUm = te.examNum and ex.subjectNUm=te.subjectNum and ex.gradeNum= te.gradeNum WHERE  s.isDelete='F' and te.id is not null and s.isHidden = 'F' and ex.examPaperNum = ex.pexamPaperNum and ex.examNum={exam} ";
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put("exam", (Object) exam);
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getSubjectInfo1(String exam, String userId) {
        Map args = StreamMap.create().put("exam", (Object) exam);
        List<Map<String, Object>> list = this.dao2._queryMapList("select distinct ex.subjectNum,s.subjectName FROM exampaper ex LEFT JOIN `subject` s ON ex.subjectNum = s.subjectNum WHERE  s.isDelete='F' and s.isHidden = 'F' and ex.appealDate is not null and ex.examPaperNum = ex.pexamPaperNum and ex.examNum={exam} and ex.jisuanType='1' order by s.orderNum", null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getSubjectInfo3(String exam, String grade, String userId) {
        Map args = StreamMap.create().put("grade", (Object) grade).put("exam", (Object) exam);
        List<Map<String, Object>> list = this.dao2._queryMapList("select distinct s.subjectNum,s.subjectName FROM exampaper e LEFT JOIN subject s ON e.subjectNum = s.subjectNum  where e.examPaperNum = e.pexamPaperNum AND e.gradeNum={grade} and e.examNum={exam} ", null, args);
        return list;
    }

    public List<Map<String, Object>> getExamInfo_old(String userId) {
        String sql = "select distinct ex.examNum,e.examName FROM exampaper ex LEFT JOIN (select exampaperNum from questiongroup_user where userNum={userId}  AND userType <> '0' GROUP BY exampaperNum) t ON ex.examPaperNum = t.exampaperNum LEFT JOIN exam e ON ex.examNum = e.examNum LEFT JOIN teacherappeal te on ex.pexamPaperNum = te.exampaperNum WHERE e.isDelete='F' and te.exampaperNum is not null and t.exampaperNum is not null and e.status<>'9'";
        if (userId.equals("-2") || userId.equals("-1")) {
            sql = "select distinct ex.examNum,e.examName FROM exampaper ex LEFT JOIN exam e ON ex.examNum = e.examNum LEFT JOIN teacherappeal te on ex.exampaperNum = te.exampaperNum WHERE e.isDelete='F' and te.exampaperNum is not null and e.status<>'9'";
        }
        Map args = StreamMap.create().put("userId", (Object) userId);
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getExamInfo(String userId) {
        Map args = new HashMap();
        args.put("userId", userId);
        if ("-2".equals(userId) || "-1".equals(userId)) {
            return this.dao2._queryMapList("select examNum,examName FROM exam WHERE isDelete='F' and status<>'9' order by examDate desc,insertDate desc", null, args);
        }
        return this.dao2._queryMapList("select distinct ep.examNum,e.examName FROM exampaper ep inner join exam e ON e.examNum = ep.examNum inner join questiongroup_user qgu on qgu.exampaperNum = ep.examPaperNum WHERE qgu.userNum={userId} and qgu.userType <> '0' and e.isDelete='F' and e.status<>'9' order by e.examDate desc,e.insertDate desc", null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getExamInfo4(String userId, String oneOrMore) {
        Map args = StreamMap.create().put("userNum", (Object) userId);
        List<Map<String, Object>> list = this.dao2._queryMapList("select distinct ex.examNum,e.examName FROM exampaper ex LEFT JOIN exam e ON ex.examNum = e.examNum LEFT JOIN assistYuejuan te on ex.examNUm = te.examNum and ex.subjectNUm=te.subjectNum and ex.gradeNum= te.gradeNum  WHERE e.isDelete='F' and te.id is not null and e.status<>'9'", TypeEnum.StringObject, args);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args);
        if (!userId.equals("-2") && !userId.equals("-1") && null == map) {
            if ("1".equals(oneOrMore)) {
                if (null == map) {
                }
            } else if ("2".equals(oneOrMore)) {
                List<AjaxData> ajaxData = this.examService.getUserNoPerExam(userId);
                if (ajaxData.size() != 0) {
                    for (AjaxData data : ajaxData) {
                        String num = data.getNum();
                        list.removeIf(o -> {
                            return num.equals(Convert.toStr(o.get(Const.EXPORTREPORT_examNum)));
                        });
                    }
                }
            }
        }
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getExamInfo1(String userId) {
        Map args = new HashMap();
        args.put("userId", userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        String sql = "select distinct ex.examNum,e.examName FROM exampaper ex  LEFT JOIN exam e ON ex.examNum = e.examNum LEFT JOIN astrict a on a.examNum=e.examNum WHERE e.isDelete='F' and e.status<>'9' and a.partType='5' and a.userType='1' and a.`status`='1' and ex.jisuanType='1' and ex.appealDate is not null ORDER BY e.examDate desc,e.insertDate desc";
        if (!"-1".equals(userId) && !"-2".equals(userId) && null == map) {
            sql = "select distinct ex.examNum,e.examName FROM exampaper ex  LEFT JOIN exam e ON ex.examNum = e.examNum LEFT JOIN astrict a on a.examNum=e.examNum  left join examschool es on e.examNum=es.examNum LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} union select schoolNum from user where id={userId}) sc on cast(es.schoolNum as char)=cast(sc.schoolNum as char) WHERE e.isDelete='F' and e.status<>'9' and a.partType='5' and a.userType='1' and a.`status`='1' and ex.jisuanType='1' and ex.appealDate is not null ORDER BY e.examDate desc,e.insertDate desc";
        }
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getExamInfo3(String userId, String oneOrMore) {
        Map args = StreamMap.create().put("userNum", (Object) userId);
        List<Map<String, Object>> list = this.dao2.queryMapList("select distinct e.examNum,e.examName from exam e WHERE e.isDelete='F' and e.status<>'9' ");
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args);
        if (userId != null && "" != userId && !userId.equals("-1") && !userId.equals("-2") && null == map) {
            if ("1".equals(oneOrMore)) {
                if (null == map) {
                    list = this.dao2._queryMapList("select distinct e.examNum,e.examName from exam e   LEFT JOIN examschool es ON e.examNum=es.examNum  right JOIN (select schoolNum from schoolscanpermission where userNum={userNum}  union select  schoolNum from user where id={userNum} ) scm ON CAST(es.schoolNum as char)=CAST(scm.schoolNum  as char)  WHERE e.isDelete='F' and e.status<>'9'  ORDER BY e.examDate DESC,e.insertDate DESC", TypeEnum.StringObject, args);
                }
            } else if ("2".equals(oneOrMore)) {
                List<AjaxData> ajaxData = this.examService.getUserNoPerExam(userId);
                if (ajaxData.size() != 0) {
                    for (AjaxData data : ajaxData) {
                        String num = data.getNum();
                        list.removeIf(o -> {
                            return num.equals(Convert.toStr(o.get(Const.EXPORTREPORT_examNum)));
                        });
                    }
                }
            }
        }
        return list;
    }

    public List<Map<String, Object>> getGradeInfo_old(String exam, String subject, String userId) {
        String sql = "select distinct ex.gradeNum,g.gradeName FROM exampaper ex LEFT JOIN (select exampaperNum from questiongroup_user where userNum={userId}  AND userType <> '0' GROUP BY exampaperNum) t ON ex.examPaperNum = t.exampaperNum LEFT JOIN basegrade g ON ex.gradeNum = g.gradeNum LEFT JOIN teacherappeal te on ex.pexamPaperNum = te.exampaperNum WHERE ex.examPaperNum = ex.pexamPaperNum and ex.examNum={exam} and ex.subjectNum = {subject}  AND t.exampaperNum is not null and te.exampaperNum is not null ";
        if (userId.equals("-2") || userId.equals("-1")) {
            sql = "select distinct ex.gradeNum,g.gradeName FROM exampaper ex LEFT JOIN basegrade g ON ex.gradeNum = g.gradeNum LEFT JOIN teacherappeal te on ex.exampaperNum = te.exampaperNum WHERE g.isDelete='F' and ex.examPaperNum = ex.pexamPaperNum and te.exampaperNum is not null and ex.examNum={exam} and ex.subjectNum = {subject} ";
        }
        Map args = new HashMap();
        args.put("userId", userId);
        args.put("exam", exam);
        args.put("subject", subject);
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getGradeInfo(String exam, String subject, String userId) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("subject", subject);
        args.put("userId", userId);
        if ("-2".equals(userId) || "-1".equals(userId)) {
            return this.dao2._queryMapList("select distinct ex.gradeNum,g.gradeName FROM exampaper ex LEFT JOIN basegrade g ON ex.gradeNum = g.gradeNum WHERE g.isDelete='F' and ex.examPaperNum = ex.pexamPaperNum  and ex.examNum={exam} and ex.subjectNum = {subject}  order by g.gradeNum", null, args);
        }
        return this.dao2._queryMapList("select distinct ep2.gradeNum,bg.gradeName FROM exampaper ep inner join questiongroup_user qgu on qgu.exampaperNum = ep.examPaperNum inner join exampaper ep2 on ep2.examPaperNum = ep.pexamPaperNum inner join basegrade bg on bg.gradeNum = ep2.gradeNum WHERE qgu.userNum={userId} and qgu.userType <> '0' and ep.examNum={exam} and ep2.subjectNum={subject} order by bg.gradeNum", null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getGradeInfo4(String exam, String subject, String userId) {
        String sql = "select distinct ex.gradeNum,g.gradeName FROM exampaper ex LEFT JOIN (select exampaperNum from questiongroup_user where userNum={userId}  AND userType <> '0' GROUP BY exampaperNum) t ON ex.examPaperNum = t.exampaperNum LEFT JOIN basegrade g ON ex.gradeNum = g.gradeNum LEFT JOIN assistYuejuan te on ex.examNUm = te.examNum and ex.subjectNUm=te.subjectNum and ex.gradeNum= te.gradeNum  WHERE ex.examPaperNum = ex.pexamPaperNum and ex.examNum={exam} and ex.subjectNum = {subject}  AND t.exampaperNum is not null and te.id is not null ";
        if (userId.equals("-2") || userId.equals("-1")) {
            sql = "select distinct ex.gradeNum,g.gradeName FROM exampaper ex LEFT JOIN basegrade g ON ex.gradeNum = g.gradeNum LEFT JOIN assistYuejuan te on ex.examNUm = te.examNum and ex.subjectNUm=te.subjectNum and ex.gradeNum= te.gradeNum  WHERE g.isDelete='F' and ex.examPaperNum = ex.pexamPaperNum and te.id is not null and ex.examNum={exam} and ex.subjectNum = {subject} ";
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put("exam", (Object) exam).put("subject", (Object) subject);
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getGradeInfo1(String exam, String subject, String userId) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("subject", (Object) subject);
        List<Map<String, Object>> list = this.dao2._queryMapList("select distinct ex.gradeNum,g.gradeName FROM exampaper ex LEFT JOIN basegrade g ON ex.gradeNum = g.gradeNum LEFT JOIN astrict a on a.gradeNum=ex.gradeNum WHERE g.isDelete='F' and a.partType='5' and a.userType='1' and a.`status`='1' and ex.examPaperNum = ex.pexamPaperNum and ex.appealDate is not null and ex.examNum={exam} and ex.subjectNum = {subject} and ex.jisuanType='1' order by g.gradeNum", null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getGradeInfo3(String exam, String userId) {
        Map args = StreamMap.create().put("exam", (Object) exam);
        List<Map<String, Object>> list = this.dao2._queryMapList("select distinct g.gradeNum,g.gradeName FROM exampaper e LEFT JOIN basegrade g ON e.gradeNum = g.gradeNum WHERE g.isDelete='F' and e.examNum={exam} ", null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getQNumInfo(String exam, String subject, String grade, String userId, String flag) {
        String sql;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("subject", subject);
        args.put("grade", grade);
        args.put("userId", userId);
        String exampaerNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and subjectNum={subject} and gradeNum={grade} ", args);
        args.put("exampaerNum", exampaerNum);
        if (userId.equals("-2") || userId.equals("-1")) {
            sql = "select distinct qq.questionNum,t.qNum from questiongroup_question qq LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id ,CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum from define d LEFT JOIN subdefine s ON d.id = s.pid where d.exampaperNum={exampaerNum}) t on qq.questionNum = t.id where qq.exampaperNum={exampaerNum} ";
        } else {
            String userType = this.dao2._queryStr("select userType from questiongroup_user where userNum={userId} and exampaperNum={exampaerNum} order by usertype desc limit 0,1", args);
            if (flag.equals("2") && (userType == null || userType.equals("0") || userType.equals("null"))) {
                return null;
            }
            sql = "select distinct qq.questionNum,t.qNum from questiongroup_user qu LEFT JOIN questiongroup_question qq on qu.groupNum = qq.groupNum LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id ,CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum from define d LEFT JOIN subdefine s ON d.id = s.pid where d.exampaperNum={exampaerNum}) t on qq.questionNum = t.id where qq.exampaperNum={exampaerNum} ";
            if (flag.equals("2")) {
                if (userType != null && userType.equals("1")) {
                    sql = sql + " and userType='1' and qu.userNum={userId} ";
                } else if (userType != null && userType.equals("2")) {
                    sql = sql + " and userType<>'2' ";
                }
            }
        }
        List<Map<String, Object>> list = this.dao2._queryMapList(sql + " group by qq.questionNum  ORDER BY t.orderNum", null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getLeaderQNumInfo(String exam, String subject, String grade, String userId) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("userId", userId);
        Object pexamPaperNum = this.dao2._queryObject("select ep.pexamPaperNum from exampaper ep where ep.examNum={exam} and ep.gradeNum={grade}  and ep.subjectNum={subject}", args);
        args.put("pexamPaperNum", pexamPaperNum);
        String userType = this.dao2._queryStr("select userType from questiongroup_user where userNum={userId} and exampaperNum={pexamPaperNum} order by usertype desc limit 1", args);
        List<Map<String, Object>> list = new ArrayList<>();
        if (userId.equals("-2") || userId.equals("-1") || "2".equals(userType)) {
            return this.dao2._queryMapList("select distinct qq.questionNum,t.qNum from questiongroup_question qq LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id ,CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum from define d LEFT JOIN subdefine s ON d.id = s.pid where d.exampaperNum={pexamPaperNum}) t on qq.questionNum = t.id where qq.exampaperNum={pexamPaperNum} group by qq.questionNum ORDER BY t.orderNum", null, args);
        }
        List<Map<String, Object>> zkm_tzzList = this.dao2._queryMapList("select distinct qq.questionNum,t.qNum,t.orderNum from questiongroup_user qu LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id ,CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum ,CASE WHEN d.isParent ='0' THEN d.category ELSE s.category  END AS category from define d LEFT JOIN subdefine s ON d.id = s.pid where d.exampaperNum={pexamPaperNum} ) t on t.category = qu.exampaperNum LEFT JOIN questiongroup_question qq on qq.questionNum = t.id where qq.exampaperNum={pexamPaperNum} and qu.userType='2' and qu.userNum={userId} group by qq.questionNum ORDER BY t.orderNum", null, args);
        list.addAll(zkm_tzzList);
        if ("1".equals(userType)) {
            List<Map<String, Object>> tzzList = this.dao2._queryMapList("select distinct qq.questionNum,t.qNum,t.orderNum from questiongroup_user qu LEFT JOIN questiongroup_question qq on qu.groupNum = qq.groupNum LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id ,CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum from define d LEFT JOIN subdefine s ON d.id = s.pid where d.exampaperNum={pexamPaperNum} ) t on qq.questionNum = t.id where qq.exampaperNum={pexamPaperNum} and qu.userType='1' and qu.userNum={userId} group by qq.questionNum ORDER BY t.orderNum", null, args);
            list.addAll(tzzList);
            list.sort((o1, o2) -> {
                return Integer.valueOf(o1.get("orderNum").toString()).intValue() - Integer.valueOf(o2.get("orderNum").toString()).intValue();
            });
        }
        return list;
    }

    public List<Map<String, Object>> appealDealListInfo_old(String exam, String grade, String subject, String qNum, int pageCount, int pageSize, String loginUser, String appealStatus) {
        String appealStr;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("loginUser", loginUser);
        args.put("qNum", qNum);
        args.put("pageCount", Integer.valueOf(pageCount));
        args.put("pageSize", Integer.valueOf(pageSize));
        if (exam == null || !exam.equals("")) {
        }
        if (grade == null || !grade.equals("")) {
        }
        if (subject == null || !subject.equals("")) {
        }
        String examPaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and subjectNum={subject} and gradeNum={grade} ", args);
        args.put("examPaperNum", examPaperNum);
        String userType = this.dao2._queryStr("select userType from questiongroup_user where userNum={loginUser} and exampaperNum={examPaperNum}  order by usertype desc limit 0,1", args);
        if (loginUser.equals("-2") || loginUser.equals("-1")) {
            userType = "2";
        }
        String qNumStr = "";
        String qGroupStr1 = "";
        String qGroupStr2 = "";
        if (qNum != null && !qNum.equals("") && !qNum.equals("null") && !qNum.equals("-1")) {
            qNumStr = " and d.id={qNum} ";
        }
        if (qNum != null && qNum.equals("-1") && userType.equals("1")) {
            qGroupStr1 = " LEFT JOIN (select * from questiongroup_question  where exampaperNum={examPaperNum} )  qq on s.questionNum=qq.questionNum LEFT JOIN (select * from questiongroup_user where exampaperNum={examPaperNum}  and userType=1) qu on qq.groupNum = qu.groupNum ";
            qGroupStr2 = " and qu.groupNum is not null and qu.userNum=" + loginUser;
        }
        if (appealStatus.equals("-1")) {
            appealStr = " and ap.status is not null";
        } else if (appealStatus.equals("1")) {
            appealStr = " and ap.status='0'";
        } else if (appealStatus.equals("2")) {
            appealStr = " and (ap.status='1' or ap.status='2')";
        } else {
            return null;
        }
        String sql = "SELECT s.questionNum,s.id scoreId,s.regId regId,s.studentId studentId,d.qNum qNum,s.questionScore questionScore,d.fullScore,s.examPaperNum examPaperNum,s.examinationRoomNum examinationRoomNum, s.schoolNum schoolNum,s.page page,if(ap.reason is null,'',ap.reason) reason,if(ap.suggestScore is null,'',ap.suggestScore)suggestScore,ifnull(ex.appealDate, '')appealDate, CASE WHEN ap. STATUS = 0 THEN '申诉' WHEN ap. STATUS = 1 THEN '申诉修改' WHEN ap. STATUS = 2 THEN '申诉驳回' ELSE '' END appealStatus,IF(ap.appealer IS NULL,'',ap.realname)appealer FROM score s LEFT JOIN(SELECT CASE WHEN d.isParent = '0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent = '0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore,CASE WHEN d.isParent = '0' THEN d.orderNum * 1000 ELSE d.orderNum * 1000 + s.orderNum END AS orderNum FROM define d LEFT JOIN subdefine s ON d.id = s.pid WHERE d.examPaperNum = {examPaperNum} )d ON d.id = s.questionNum LEFT JOIN( SELECT examPaperNum,appealDate FROM examPaper WHERE examPaperNum = {examPaperNum} )ex ON s.examPaperNum = ex.examPaperNum LEFT JOIN(SELECT te.scoreId,te.status,te.appealer,u.realname,te.suggestScore,te.reason FROM teacherappeal te LEFT JOIN USER u ON te.appealer = u.id)ap ON s.id = ap.scoreId " + qGroupStr1 + "WHERE s.continued = 'F' and s.examPaperNum = {examPaperNum}  " + qNumStr + qGroupStr2 + appealStr + " ORDER BY d.orderNum LIMIT {pageCount},{pageSize} ";
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> appealDealListInfo(String exam, String grade, String subject, String qNum, int pageCount, int pageSize, String loginUser, String appealStatus, String isDealOwnSchoolAppeal) {
        String appealStr;
        if (null == qNum || "".equals(qNum)) {
            return null;
        }
        if ("-1".equals(appealStatus)) {
            appealStr = " and ap.status is not null ";
        } else if ("1".equals(appealStatus)) {
            appealStr = " and ap.status='0' ";
        } else if ("2".equals(appealStatus)) {
            appealStr = " and (ap.status='1' or ap.status='2') ";
        } else {
            return null;
        }
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("qNum", qNum);
        args.put("pageCount", Integer.valueOf(pageCount));
        args.put("pageSize", Integer.valueOf(pageSize));
        args.put("loginUser", loginUser);
        String filterbyschool1 = "";
        String filterbyschool2 = "";
        if (isDealOwnSchoolAppeal.equals("0") && !loginUser.equals("-2") && !loginUser.equals("-1")) {
            filterbyschool1 = " and stu.schoolNum!=u2.schoolNum ";
            filterbyschool2 = " LEFT JOIN student stu ON stu.id = ap.studentId LEFT JOIN (select DISTINCT schoolnum from user where id={loginUser}) u2 on 1=1 ";
        }
        Object pexamPaperNum = this.dao2._queryObject("select pexamPaperNum from exampaper where examNum={exam} and gradeNum={grade}  and subjectNum={subject} ", args);
        args.put("pexamPaperNum", pexamPaperNum);
        String sql = "SELECT s.questionNum,s.id scoreId,s.regId regId,s.studentId studentId,d.qNum qNum,s.questionScore questionScore,d.fullScore,s.examPaperNum examPaperNum,s.examinationRoomNum examinationRoomNum,s.schoolNum schoolNum,s.page page,if(ap.reason is null,'',ap.reason) reason,if(ap.suggestScore is null,'',ap.suggestScore) suggestScore,if(ap.scorebeformodify is null,0,ap.scorebeformodify) scorebeformodify,ifnull(ex.appealDate, '') appealDate,ifnull(ex.appealDealDate, '') appealDealDate,CASE WHEN ap. STATUS = 0 THEN '申诉' WHEN ap. STATUS = 1 THEN '申诉修改' WHEN ap. STATUS = 2 THEN '申诉驳回' ELSE '' END appealStatus,IF(ap.appealer IS NULL,'',u.realname)appealer,IFNULL(u.username,'') username,IFNULL(u.realname,'') realname,IFNULL(sch.schoolName,'') schoolName," + loginUser + " loginUserId FROM score s LEFT JOIN (select questionNum qNum,id,fullScore,orderNum*1000 orderNum from define where examPaperNum={pexamPaperNum} and id in ({qNum[]}) union all select subd.questionNum qNum,subd.id,subd.fullScore,(d.orderNum*1000+subd.orderNum) orderNum from subdefine subd left join define d on d.id = subd.pid where subd.examPaperNum={pexamPaperNum} and subd.id in ({qNum[]}) ) d ON d.id = s.questionNum LEFT JOIN examPaper ex ON ex.examPaperNum = s.examPaperNum LEFT JOIN teacherappeal ap ON ap.scoreId = s.id LEFT JOIN `user` u ON u.id = ap.appealer LEFT JOIN school sch ON sch.id = u.schoolnum " + filterbyschool2 + "WHERE s.continued = 'F' and s.examPaperNum = {pexamPaperNum} and d.id is not null " + filterbyschool1 + appealStr + "ORDER BY d.orderNum LIMIT {pageCount},{pageSize} ";
        return this.dao2._queryMapList(sql, null, args);
    }

    public List appealDealListCount_old(String exam, String grade, String subject, String qNum, String loginUser, String appealStatus) {
        String appealStr;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("loginUser", loginUser);
        args.put("qNum", qNum);
        String examStr = "";
        if (exam != null && !exam.equals("")) {
            examStr = " and examNum={exam} ";
        }
        String gradeStr = "";
        if (grade != null && !grade.equals("")) {
            gradeStr = " and gradeNum={grade} ";
        }
        String subjectStr = "";
        if (subject != null && !subject.equals("")) {
            subjectStr = " and subjectNum={subject} ";
        }
        String shiJuanSql = "select distinct exampaperNum from exampaper where 1=1 " + examStr + gradeStr + subjectStr;
        List list = this.dao2._queryColList(shiJuanSql, args);
        String examPaperNum = "";
        if (list.size() > 0) {
            examPaperNum = list.get(0) + "";
        }
        args.put("examPaperNum", examPaperNum);
        String userType = this.dao2._queryStr("select userType from questiongroup_user where userNum={loginUser} and exampaperNum={examPaperNum}  order by usertype desc limit 0,1", args);
        if (loginUser.equals("-2") || loginUser.equals("-1")) {
            userType = "2";
        }
        String qNumStr = "";
        String qGroupStr1 = "";
        String qGroupStr2 = "";
        if (qNum != null && !qNum.equals("") && !qNum.equals("null") && !qNum.equals("-1")) {
            qNumStr = " and s.questionNum={qNum} ";
        } else if (qNum != null && qNum.equals("-1") && userType.equals("1")) {
            qGroupStr1 = " LEFT JOIN (select * from questiongroup_question  where exampaperNum={examPaperNum} )  qq on s.questionNum=qq.questionNum LEFT JOIN (select * from questiongroup_user where exampaperNum={examPaperNum}  and userType=1) qu on qq.groupNum = qu.groupNum ";
            qGroupStr2 = " and qu.groupNum is not null and qu.userNum={loginUser} ";
        }
        if (appealStatus.equals("-1")) {
            appealStr = " and ap.status is not null";
        } else if (appealStatus.equals("1")) {
            appealStr = " and ap.status='0'";
        } else if (appealStatus.equals("2")) {
            appealStr = " and (ap.status='1' or ap.status='2')";
        } else {
            return null;
        }
        String sql = "SELECT count(1) as l_count FROM (SELECT te.scoreId,te.status FROM teacherappeal te where te.examPaperNum={examPaperNum}  )ap left join score s ON s.id = ap.scoreId " + qGroupStr1 + "WHERE s.continued='F' " + qNumStr + appealStr + qGroupStr2;
        return this.dao2._queryColList(sql, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List appealDealListCount(String exam, String grade, String subject, String qNum, String loginUser, String appealStatus) {
        String appealStr;
        if (null == qNum || "".equals(qNum)) {
            return null;
        }
        if ("-1".equals(appealStatus)) {
            appealStr = " and ap.status is not null ";
        } else if ("1".equals(appealStatus)) {
            appealStr = " and ap.status='0' ";
        } else if ("2".equals(appealStatus)) {
            appealStr = " and (ap.status='1' or ap.status='2') ";
        } else {
            return null;
        }
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("qNum", qNum);
        Object pexamPaperNum = this.dao2._queryObject("select pexamPaperNum from exampaper where examNum={exam} and gradeNum={grade}  and subjectNum={subject} ", args);
        args.put("pexamPaperNum", pexamPaperNum);
        String sql = "SELECT count(1) as l_count FROM score s LEFT JOIN (select id from define where examPaperNum={pexamPaperNum} and id in ({qNum[]}) union all select id from subdefine where examPaperNum={pexamPaperNum}  and id in ({qNum[]}) ) d ON d.id = s.questionNum LEFT JOIN teacherappeal ap ON ap.scoreId = s.id WHERE s.continued = 'F' and s.examPaperNum = {pexamPaperNum}  and d.id is not null " + appealStr;
        return this.dao2._queryColList(sql, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String ifExistAppealData(String exam, String grade, String subject) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        String flag = "T";
        String examStr = "";
        if (exam != null && !exam.equals("")) {
            examStr = " and examNum={exam} ";
        }
        String gradeStr = "";
        if (grade != null && !grade.equals("")) {
            gradeStr = " and gradeNum={grade} ";
        }
        String subjectStr = "";
        if (subject != null && !subject.equals("")) {
            subjectStr = " and subjectNum={subject} ";
        }
        String shiJuanSql = "select distinct exampaperNum from exampaper where 1=1 " + examStr + gradeStr + subjectStr;
        List list = this.dao2._queryColList(shiJuanSql, args);
        String examPaperNum = "";
        if (list.size() > 0) {
            examPaperNum = list.get(0) + "";
        }
        args.put("examPaperNum", examPaperNum);
        List c_list = this.dao2._queryColList("select count(scoreId) s_count from teacherappeal where status='0' and exampaperNum={examPaperNum} ", args);
        int count = 0;
        if (c_list != null) {
            count = Integer.parseInt(c_list.get(0) + "");
        }
        if (count > 0) {
            flag = "本科目仍有未处理完的申诉数据！";
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getSchoolInfo(String exam, String subject, String grade, String item, String uid) {
        Map args = StreamMap.create().put("item", (Object) item).put("exam", (Object) exam).put("grade", (Object) grade).put("subject", (Object) subject).put("uid", (Object) uid);
        String statisItem = "";
        String statisItemStr = "";
        if (item != null && !item.equals("")) {
            statisItem = " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId={item} ) ss on gl.schoolNum = ss.sItemId ";
            statisItemStr = " and ss.sItemId is not null ";
        }
        String sql = "select gl.schoolNum,s.schoolName from gradelevel gl left join school s on gl.schoolNum = s.id " + statisItem + " where s.isDelete='F' and gl.examNum={exam} and gl.gradeNum={grade} and gl.subjectNum={subject} " + statisItemStr + " GROUP BY gl.schoolNum order by s.schoolname";
        if (!uid.equals("-1") && !uid.equals("-2")) {
            Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={uid} and type=1 limit 1", args);
            if (null == map) {
                sql = "select gl.schoolNum,s.schoolName from gradelevel gl left join (select sc.schoolNum from schoolscanpermission sc where sc.userNum={uid} union select schoolNum from user where id={uid}) sc on gl.schoolNum=sc.schoolNum left join school s on gl.schoolNum = s.id " + statisItem + " where sc.schoolNum is not null and s.isDelete='F' and gl.examNum={exam} and gl.gradeNum={grade} and gl.subjectNum={subject} " + statisItemStr + " GROUP BY gl.schoolNum order by s.schoolname";
            }
        }
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getClassInfo(String exam, String subject, String grade, String item, String school) {
        String statisItem = "";
        String statisItemStr = "";
        if ((school == null || school.equals("")) && item != null && !item.equals("")) {
            statisItem = " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId={item} ) ss on cl.schoolNum = ss.sItemId";
            statisItemStr = " and ss.sItemId is not null ";
        }
        String sql = "select DISTINCT cl.classNum,c.className from classlevel cl left join class c on cl.classNum = c.id " + statisItem + "where c.isDelete='F' and cl.examNum={exam} and cl.subjectNum={subject} and cl.gradeNum={grade} and cl.schoolNum={school} " + statisItemStr + " order by length(className),className";
        Map args = StreamMap.create().put("item", (Object) item).put("exam", (Object) exam).put("subject", (Object) subject).put("grade", (Object) grade).put(License.SCHOOL, (Object) school);
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getStudentInfo(String exam, String subject, String grade, String item, String school, String classNum) {
        String statisItem = " ";
        String statisItemStr = " ";
        if ((school == null || school.equals("")) && item != null && !item.equals("")) {
            statisItem = " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId='" + item + "') ss on cl.schoolNum = ss.sItemId";
            statisItemStr = " and ss.sItemId is not null ";
        }
        String classStr = "";
        if (classNum != null && !classNum.equals("") && !classNum.equals("-1")) {
            classStr = " and classNum=" + classNum;
        }
        String sql = "select distinct st.studentId,s.studentName from  ( select studentId from studentlevel where examNum={exam} and gradeNum={grade} and subjectNum={subject} and schoolNum={school} " + classStr + " union select  studentId from studentlevel_butongji where examNum={exam} and gradeNum={grade} and subjectNum={subject} and schoolNum={school}  " + classStr + " ) st left join student s on st.studentid=s.id " + statisItem + "where s.isDelete='F' " + statisItemStr + "order by s.studentName ";
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("subject", subject);
        args.put("grade", grade);
        args.put(License.SCHOOL, school);
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getQuestionInfo(String exam, String subject, String grade, String item, String school, String classNum, String studentId) {
        String statisItem = "";
        String statisItemStr = "";
        if ((school == null || school.equals("")) && item != null && !item.equals("")) {
            statisItem = " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId={item} ) ss on s.schoolNum = ss.sItemId";
            statisItemStr = " and ss.sItemId is not null ";
        }
        String classStr = "";
        if (classNum != null && !classNum.equals("") && !classNum.equals("-1")) {
            classStr = " and st.classNum={classNum} ";
        }
        String studentStr = "";
        if (studentId != null && !studentId.equals("") && !studentId.equals("-1")) {
            studentStr = " and t.studentId={studentId} ";
        }
        String sql = "select distinct qu.groupNum,qu.groupname from student s left join task t on t.studentid=s.id left join questionGroup qu on t.groupNum = qu.groupNum left join exampaper exp on qu.exampaperNum = exp.exampaperNum " + statisItem + "where s.isDelete='F' and exp.examNum={exam} and exp.subjectNum={subject} and exp.gradeNum={grade}  and s.schoolNum={school} " + classStr + studentStr + statisItemStr + " order by length(qu.groupname),qu.groupname";
        Map args = new HashMap();
        args.put("item", item);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        args.put("subject", subject);
        args.put("grade", grade);
        args.put(License.SCHOOL, school);
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> appealListInfo(String exam, String grade, String subject, String qNum, int pageCount, int pageSize, String studentId, String classNum, String appeal, String leiceng, String loginUser, String school) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("loginUser", loginUser);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(License.SCHOOL, school);
        args.put("pageCount", Integer.valueOf(pageCount));
        args.put("pageSize", Integer.valueOf(pageSize));
        if (exam == null || !exam.equals("")) {
        }
        if (grade == null || !grade.equals("")) {
        }
        if (subject == null || !subject.equals("")) {
        }
        String examPaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam}  and subjectNum={subject} and gradeNum={grade} ", args);
        args.put("examPaperNum", examPaperNum);
        this.dao2._queryStr("select userType from questiongroup_user where userNum={loginUser} order by usertype desc", args);
        if (loginUser.equals("-2") || loginUser.equals("-1")) {
        }
        String qNumStr = "";
        if (qNum != null && !qNum.equals("") && !qNum.equals("null") && !qNum.equals("-1")) {
            qNumStr = " and d.id=" + qNum;
        }
        if (qNum == null || qNum.equals("-1")) {
        }
        String appealStr = "";
        if (appeal != null && !appeal.equals("")) {
            appealStr = " and ap.status is not null";
        }
        String studentStr = "";
        if (studentId != null && !studentId.equals("") && !studentId.equals("-1")) {
            studentStr = " and s.studentId = {studentId} ";
        }
        String classStr = "";
        if (classNum != null && !classNum.equals("") && !classNum.equals("-1")) {
            classStr = " and s.classNum = {classNum} ";
        }
        String sql = "SELECT s.questionNum,qgq.groupNum,s.insertUser,s.id scoreId,s.regId regId,s.studentId studentId,stu.studentName,d.qNum qNum,s.questionScore questionScore,d.fullScore,s.examPaperNum examPaperNum,s.examinationRoomNum examinationRoomNum, s.schoolNum schoolNum,s.page page,if(ap.reason is null,'',ap.reason) reason,if(ap.suggestScore is null,'',ap.suggestScore)suggestScore,ifnull(ex.appealDate, '')appealDate, CASE WHEN ap. STATUS = 0 THEN '申诉' WHEN ap. STATUS = 1 THEN '申诉修改' WHEN ap. STATUS = 2 THEN '申诉驳回' ELSE '' END appealStatus,IF(ap.appealer IS NULL,'',if(ap.appealer ='-2','dmj',u.realname))appealer,qgq.shensuStatus,qgq.shensuYuzhi FROM score s LEFT JOIN(SELECT CASE WHEN d.isParent = '0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent = '0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore,CASE WHEN d.isParent = '0' THEN d.orderNum * 1000 ELSE d.orderNum * 1000 + s.orderNum END AS orderNum FROM define d LEFT JOIN subdefine s ON d.id = s.pid WHERE d.examPaperNum = {examPaperNum} )d ON d.id = s.questionNum LEFT JOIN( SELECT examPaperNum,appealDate FROM examPaper WHERE examPaperNum = {examPaperNum} )ex ON s.examPaperNum = ex.examPaperNum LEFT JOIN (select groupNum,questionNum,IFNULL(shensuStatus,'0') shensuStatus,IFNULL(shensuYuzhi,0) shensuYuzhi from questiongroup_question where exampaperNum={examPaperNum} ) qgq ON qgq.questionNum = s.questionNum LEFT JOIN (select groupNum from questiongroup where exampaperNum={examPaperNum} ) qg ON qg.groupNum = qgq.groupNum LEFT JOIN teacherappeal ap ON s.id = ap.scoreId LEFT JOIN USER u ON ap.appealer = u.id LEFT JOIN student stu on s.studentId = stu.id WHERE s.continued = 'F' AND s.schoolNum= {school}  and s.examPaperNum = {examPaperNum} " + qNumStr + "" + appealStr + studentStr + classStr + " ORDER BY d.orderNum LIMIT {pageCount},{pageSize} ";
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public Integer getTeacherAppealCount(int examPaperNum, String classNum, String school, String qNum, String studentId, String exam, String grade, String subject, String appealed, String classPermission, String uid) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("uid", uid);
        args.put("qNum", qNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put(License.SCHOOL, school);
        if (exam == null || !exam.equals("")) {
        }
        if (grade == null || !grade.equals("")) {
        }
        if (subject == null || !subject.equals("")) {
        }
        this.dao2._queryStr("select userType from questiongroup_user where userNum={uid}  order by usertype desc", args);
        if (uid.equals("-2") || uid.equals("-1")) {
        }
        String qNumStr = "";
        if (qNum != null && !qNum.equals("") && !qNum.equals("null") && !qNum.equals("-1")) {
            qNumStr = " and d.id={qNum}  ";
        }
        if (qNum == null || qNum.equals("-1")) {
        }
        String appealStr = "";
        if (appealed != null && !appealed.equals("")) {
            appealStr = " and ap.status is not null";
        }
        String studentStr = "";
        if (studentId != null && !studentId.equals("") && !studentId.equals("-1")) {
            studentStr = " and s.studentId = {studentId}   ";
        }
        String classStr = "";
        if (classNum != null && !classNum.equals("") && !classNum.equals("-1")) {
            classStr = " and s.classNum = {classNum} ";
        }
        String sql = "SELECT count(s.id) pCount FROM score s LEFT JOIN(SELECT CASE WHEN d.isParent = '0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent = '0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore,CASE WHEN d.isParent = '0' THEN d.orderNum * 1000 ELSE d.orderNum * 1000 + s.orderNum END AS orderNum FROM define d LEFT JOIN subdefine s ON d.id = s.pid WHERE d.examPaperNum = {examPaperNum} )d ON d.id = s.questionNum LEFT JOIN( SELECT examPaperNum,appealDate FROM examPaper WHERE examPaperNum = {examPaperNum} )ex ON s.examPaperNum = ex.examPaperNum LEFT JOIN teacherappeal ap ON s.id = ap.scoreId LEFT JOIN USER u ON ap.appealer = u.id WHERE s.continued = 'F' and s.schoolNum= {school}  AND s.examPaperNum = {examPaperNum} " + qNumStr + "" + appealStr + studentStr + classStr;
        return this.dao2._queryInt(sql, args);
    }

    public List<Map<String, Object>> getTeacherAppealCountInfo_old(String exam, String grade, String subject, String qNum, String yuejuan, String teacherNum, String loginUser) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("loginUser", loginUser);
        args.put("qNum", qNum);
        args.put("yuejuan", "%" + yuejuan + "%");
        args.put("teacherNum", teacherNum);
        String examStr = "";
        if (exam != null && !exam.equals("")) {
            examStr = " and examNum={exam} ";
        }
        String gradeStr = "";
        if (grade != null && !grade.equals("")) {
            gradeStr = " and gradeNum={grade} ";
        }
        String subjectStr = "";
        if (subject != null && !subject.equals("")) {
            subjectStr = " and subjectNum={subject} ";
        }
        String shiJuanSql = "select distinct exampaperNum from exampaper where 1=1 " + examStr + gradeStr + subjectStr;
        List list_exam = this.dao2._queryColList(shiJuanSql, args);
        String examPaperNum = "";
        if (list_exam.size() > 0) {
            examPaperNum = list_exam.get(0) + "";
        }
        args.put("examPaperNum", examPaperNum);
        String userType = this.dao2._queryStr("select userType from questiongroup_user where userNum={loginUser}  and exampaperNum={examPaperNum}  order by usertype desc limit 0,1", args);
        if (loginUser.equals("-2") || loginUser.equals("-1")) {
            userType = "2";
        }
        String qNumStr = "";
        String qGroupStr1 = "";
        String qGroupStr2 = "";
        if (qNum != null && !qNum.equals("") && !qNum.equals("null") && !qNum.equals("-1")) {
            qNumStr = " and t.questionNum={qNum} ";
        } else if (qNum != null && qNum.equals("-1") && userType.equals("1")) {
            qGroupStr1 = " LEFT JOIN (select * from questiongroup_question  where exampaperNum={examPaperNum} )  qq on t.questionNum=qq.questionNum LEFT JOIN (select * from questiongroup_user where exampaperNum={examPaperNum}  and userType=1) qu on qq.groupNum = qu.groupNum ";
            qGroupStr2 = " and qu.groupNum is not null and qu.userNum={loginUser} ";
        }
        String yuejuanStr = "";
        if (yuejuan != null && !yuejuan.equals("")) {
            yuejuanStr = " and u.realName like {yuejuan} ";
        }
        String teacherStr = "";
        if (teacherNum != null && !teacherNum.equals("")) {
            teacherStr = " and te.teacherNum={teacherNum} ";
        }
        String sql = "select t.questionNum qNum,ifnull(d.questionNum,sub.questionNum)questionNum,t.marker,te.teacherNum,u.realname,count(1) c_appeal,sum(if(t.`status`=1,1,0)) c_change,sum(if(t.`status`=2,1,0)) c_reject from teacherappeal t LEFT JOIN exampaper e ON t.exampaperNum = e.exampaperNum LEFT JOIN user u ON t.marker = u.id LEFT JOIN teacher te ON u.userid = te.id LEFT JOIN define d ON t.questionNum = d.id LEFT JOIN subdefine sub ON t.questionNum = sub.id " + qGroupStr1 + "where e.examNum={exam} and e.gradeNum={grade} and e.subjectNum={subject} " + yuejuanStr + teacherStr + qNumStr + qGroupStr2 + "GROUP BY t.questionNum,t.marker ";
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getTeacherAppealCountInfo(String exam, String grade, String subject, String qNum, String yuejuan, String teacherNum, String loginUser) {
        if (null == qNum || "".equals(qNum)) {
            return null;
        }
        String yuejuan2 = yuejuan.replace(" ", "");
        String yuejuanStr = !"".equals(yuejuan2) ? " and u.realName like {yuejuan} " : "";
        String teacherStr = !"".equals(teacherNum) ? " and te.teacherNum={teacherNum} " : "";
        Map args = new HashMap();
        args.put("yuejuan", "%" + yuejuan2 + "%");
        args.put("teacherNum", teacherNum);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("qNum", qNum);
        Object pexamPaperNum = this.dao2._queryObject("select pexamPaperNum from exampaper where examNum={exam} and gradeNum={grade}  and subjectNum={subject} ", args);
        args.put("pexamPaperNum", pexamPaperNum);
        String sql = "select t.questionNum qNum,d.questionNum questionNum,t.marker,te.teacherNum,u.realname,count(1) c_appeal,sum(if(t.`status`=1,1,0)) c_change,sum(if(t.`status`=2,1,0)) c_reject from teacherappeal t LEFT JOIN user u ON t.marker = u.id LEFT JOIN teacher te ON u.userid = te.id LEFT JOIN (select questionNum,id,orderNum*1000 orderNum from define where examPaperNum={pexamPaperNum}  and id in ({qNum[]}) union all select subd.questionNum,subd.id,(d.orderNum*1000+subd.orderNum) orderNum from subdefine subd left join define d on d.id = subd.pid where subd.examPaperNum={pexamPaperNum}  and subd.id in ({qNum[]}) ) d ON d.id = t.questionNum where t.examPaperNum={pexamPaperNum}  and d.id is not null " + yuejuanStr + teacherStr + "GROUP BY t.questionNum,t.marker order by d.orderNum ";
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getDetailTeacherAppealCountInfo(String qNum, String marker, String status, String appealer) {
        String statusStr = "";
        if (status != null && !status.equals("-1")) {
            statusStr = " and t.status={status} ";
        }
        String appealerStr = "";
        if (appealer != null && !appealer.equals("")) {
            appealerStr = " and (t2.teacherNum={appealer} or u2.realname like {appealer}) ";
        }
        String sql = "select t.examPaperNum,t.scoreId,t.studentId,t.questionNum qNum,ifnull(d.questionNum,sub.questionNum)questionNum, t.marker,ifnull(t1.teacherNum,'') teacherNum1,if(t.marker='-2','dmj',u1.realname) r1,t.appealer,ifnull(t2.teacherNum,'') teacherNum2,if(t.appealer='-2','dmj',u2.realname) r2, CASE WHEN t.`status`=0 THEN '申诉' WHEN t.`status`=1 THEN '申诉修改' WHEN t.`status`=2 THEN '申诉驳回' ELSE '--' END status ,IFNULL(t.updater,'')updater,ifnull(t3.teacherNum,'') teacherNum3,if(t.updater is not null,if(t.updater='-2','dmj',u3.realname),'') r3 FROM teacherappeal t LEFT JOIN user u1 ON t.marker = u1.id  LEFT JOIN teacher t1 ON t1.id = u1.userid  LEFT JOIN user u2 ON t.appealer = u2.id LEFT JOIN teacher t2 ON t2.id = u2.userid  LEFT JOIN user u3 ON t.updater = u3.id LEFT JOIN teacher t3 ON t3.id = u3.userid  LEFT JOIN define d ON t.questionNum = d.id LEFT JOIN subdefine sub ON t.questionNum = sub.id where t.questionNum={qNum} and t.marker={marker} " + statusStr + appealerStr;
        Map args = new HashMap();
        args.put(Const.CORRECT_SCORECORRECT, status);
        args.put("appealer", "%" + appealer + "%");
        args.put("qNum", qNum);
        args.put("marker", marker);
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String exportTeacherAppealCountInfo(String exam, String grade, String subject, String qNum, String yuejuan, String teacherNum, String userId, String xmlPath, String examName, String gradeName, String subjectName, String loginUser) {
        String path = "ExportFolder/teacherappeal/" + examName + "/" + gradeName + "-" + subjectName + "/" + userId;
        String filePath = xmlPath + "/" + path;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File excelFile = new File(filePath + "/教师申诉次数统计表.xls");
            if (!excelFile.exists()) {
                excelFile.createNewFile();
            }
            WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormatTitle = new WritableCellFormat(titleFont);
            cFormatTitle.setAlignment(Alignment.CENTRE);
            cFormatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormat = new WritableCellFormat(font);
            cFormat.setAlignment(Alignment.LEFT);
            cFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableFont font3 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormat3 = new WritableCellFormat(font3);
            cFormat.setAlignment(Alignment.LEFT);
            cFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableFont font1 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormat1 = new WritableCellFormat(font1);
            cFormat1.setAlignment(Alignment.CENTRE);
            cFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableFont font2 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormat2 = new WritableCellFormat(font2);
            cFormat2.setAlignment(Alignment.LEFT);
            cFormat2.setVerticalAlignment(VerticalAlignment.CENTRE);
            WorkbookSettings wbs = new WorkbookSettings();
            wbs.setGCDisabled(true);
            WritableWorkbook wwBook = Workbook.createWorkbook(excelFile, wbs);
            WritableSheet sheet = wwBook.createSheet("教师申诉次数统计", 0);
            List<Map<String, Object>> list = getTeacherAppealCountInfo(exam, grade, subject, qNum, yuejuan, teacherNum, loginUser);
            String titleHtml = "考试：" + examName + "    年级：" + gradeName + "    科目：" + subjectName;
            Label cell0 = new Label(0, 0, "教师申诉次数统计", cFormat);
            sheet.addCell(cell0);
            Label cell1 = new Label(0, 1, titleHtml, cFormat3);
            sheet.addCell(cell1);
            sheet.setRowView(0, Const.height_400);
            sheet.mergeCells(0, 0, 17, 0);
            sheet.mergeCells(0, 1, 17, 1);
            String[] title = {"题号", "阅卷员工号", "阅卷员", "申诉次数", "修改次数", "驳回次数"};
            for (int i = 0; i < title.length; i++) {
                Label cell = new Label(i, 2, title[i], cFormat1);
                sheet.addCell(cell);
            }
            sheet.setColumnView(1, 20);
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> ta = list.get(j);
                Label cell2 = new Label(0, j + 3, String.valueOf(ta.get("questionNum")), cFormat1);
                sheet.addCell(cell2);
                Label imgCell = new Label(1, j + 3, String.valueOf(ta.get("teacherNum")), cFormat1);
                sheet.addCell(imgCell);
                Label cell3 = new Label(2, j + 3, String.valueOf(ta.get("realname")), cFormat1);
                sheet.addCell(cell3);
                Number cell4 = new Number(3, j + 3, Integer.valueOf(ta.get("c_appeal").toString()).intValue(), cFormat1);
                sheet.addCell(cell4);
                Number cell5 = new Number(4, j + 3, Integer.valueOf(ta.get("c_change").toString()).intValue(), cFormat1);
                sheet.addCell(cell5);
                Number cell6 = new Number(5, j + 3, Integer.valueOf(ta.get("c_reject").toString()).intValue(), cFormat1);
                sheet.addCell(cell6);
            }
            wwBook.write();
            wwBook.close();
        } catch (Exception e) {
            e.printStackTrace();
            this.log.info("--导出申诉处理详情" + userId + "失败--" + e.getMessage());
        }
        return path + "/教师申诉次数统计表.xls";
    }

    public boolean isXuanxue(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        List res = this.dao2._queryArrayList("select xuankaoqufen from exampaper where pexamPaperNum = {examPaperNum} ", args);
        if (null != res && res.size() == 3) {
            for (int i = 0; i < 3; i++) {
                Object[] xx = res.get(i);
                if (null == xx[0] || "".equals(String.valueOf(xx[0]))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getListInfo3(String exam, String grade, String subject, String user) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("user", user);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject} ", args);
        args.put("examPaperNum", exampaperNum);
        List<Map<String, Object>> list = this.dao2._queryMapList("SELECT distinct q.groupName,q.groupNum,  case     when d.choosename<>'s' then IFNULL(tt.count,0)    when d.choosename='s' and z.makType=1 then IFNULL(r.count*2,0)+IFNULL(x.count,0)    when d.choosename='s' and z.makType=0 then IFNULL(r.count,0)      end totalNum,d.choosename ext10,IFNULL(y.d_num ,0) d_num,ay.status,    IF(t.groupNum is NULL ,'T','F') questionName FROM(    \t\t\tSELECT s.groupNum,s.exampaperNum,m.makType  FROM(    \t\t\t\t\t\tSELECT groupNum,exampaperNum FROM questiongroup WHERE exampaperNum={examPaperNum}    \t\t\t) s  LEFT JOIN (   \t\t\t\t\t\tSELECT groupNum,makType FROM questiongroup_mark_setting WHERE exampaperNum={examPaperNum}    \t\t\t) m ON m.groupNum=s.groupNum   LEFT JOIN (   \t\t\t\t\t\tselect groupNum,count(1) count from questiongroup_question where exampaperNum={examPaperNum}  GROUP BY groupNum    \t\t\t)d on s.groupNum=d.groupNum  LEFT JOIN (   \t\t\t\t\t\tSELECT groupNum,makType from questiongroup_mark_setting where exampaperNum={examPaperNum}    \t\t\t)qe on s.groupNum=qe.groupNum    )z    LEFT JOIN (   \t\t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num,count(DISTINCT(questionNum)) as tempcount      \t\t\tFROM task   WHERE exampaperNum={examPaperNum}  AND status='T'   GROUP BY groupNum     )y ON z.groupNum = y.groupNum LEFT JOIN (    \t\t\tSELECT groupNum,count(1) count FROM task   WHERE exampaperNum={examPaperNum}  and userNum=3  GROUP BY groupNum     )x ON z.groupNum = x.groupNum    LEFT JOIN assistYuejuan ay ON z.groupNum=ay.groupNum and ay.assister={user}   LEFT JOIN questiongroup q ON q.groupNum=z.groupNum     LEFT JOIN (   SELECT id,choosename from define WHERE exampaperNum={examPaperNum}       UNION       SELECT sb.id,d.choosename from define d  LEFT JOIN subdefine sb on sb.pid=d.id  WHERE d.exampaperNum={examPaperNum}   \t) d on q.groupNum=d.id     LEFT JOIN (   \t\tselect d.id,r.dd count from(  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d   \t\t\tINNER JOIN exampaper e on d.category=e.examPaperNum where d.examPaperNum={examPaperNum}  and d.questionType=1   \t\t\tUNION  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from subdefine d   \t\t\tINNER JOIN exampaper e on d.category=e.examPaperNum where d.examPaperNum={examPaperNum}  and d.questionType=1  \t\t)d LEFT JOIN(  \t\t\tSELECT count(DISTINCT studentId) dd,exampapernum ext1 from regexaminee WHERE exampaperNum={examPaperNum}   and scan_import=0  \t\t\tunion  \t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampapernum from regexaminee r  \t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE r.exampaperNum={examPaperNum}   and r.scan_import=0  \t\t\tUNION\t  \t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampaperNum from regexaminee r LEFT JOIN(  \t\t\t\tSELECT r.studentId,r.exampapernum from regexaminee r  \t\t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \t\t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE r.exampaperNum={examPaperNum}   and r.scan_import=0  \t\t\t)r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum  \t\t\tINNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3  \t\t\tWHERE r.exampaperNum={examPaperNum}   and r.scan_import=0 and r1.studentId is null  \t\t)r on d.category=r.ext1  ) r on q.groupNum=r.id LEFT JOIN (  \t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed) count from task where examPaperNum={examPaperNum}  GROUP BY groupNum  )tt on q.groupNum=tt.groupNum    LEFT JOIN (   \t\tselect  DISTINCT q.groupNum FROM (  \t\t\t\t\t\tSELECT groupNum,questionNum FROM questiongroup_question WHERE exampaperNum ={examPaperNum}  GROUP BY groupNum    \t\t)q LEFT JOIN (  \t\t\t\t\t\tselect DISTINCT questionNum  FROM remark where type='1' AND STATUS='F' AND exampaperNum={examPaperNum}   \t\t) tt ON q.questionNum = tt.questionNum where tt.questionNum is not null   )t  ON z.groupNum = t.groupNum WHERE z.groupNum IS NOT NULL    ORDER BY CASE  WHEN LOCATE('-',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('-' IN q.groupName)-1),'.01')*1   ELSE CONCAT(q.groupName,'.1')*1 END  ASC ,REPLACE(q.groupName,'_','.')*1 ", null, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String appealAssist(String exam, String grade, String subject, String groupNum, String user) {
        String sql;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("groupNum", groupNum);
        args.put("user", user);
        String flag = "F";
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject} ", args);
        args.put("examPaperNum", exampaperNum);
        String qid = this.dao2._queryStr("select ifnull(qu.id,'') from questiongroup_user qu left join exampaper e on qu.exampaperNum=e.exampaperNum where qu.userType!='2' AND qu.groupNum={groupNum} AND qu.userNum={user} and e.examNum={exam} and e.gradeNum={grade} and e.subjectNum={subject} ", args);
        if (StrUtil.isNotEmpty(qid)) {
            flag = "您已经是该题组的阅卷员，无法申请协该题组的阅卷！";
        } else {
            String count = this.dao2._queryStr("select ifnull(count(1),0) from assistyuejuan where examPaperNum={examPaperNum}  and groupNum={groupNum} and assister={user} ", args);
            if ("0".equals(count)) {
                sql = "insert into assistyuejuan(examNum,gradeNum,subjectNum,exampaperNum,groupNum,assister,updateUser,updateDate,status) values({exam},{grade},{subject},{examPaperNum} ,{groupNum},{user},{user},now(),0)";
            } else {
                sql = "update assistyuejuan set status=0 where examPaperNum={examPaperNum}  and groupNum={groupNum} and assister={user} ";
            }
            int i = this.dao2._execute(sql, args);
            if (i > 0) {
                flag = "T";
            }
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String cancelAssist(String exam, String grade, String subject, String groupNum, String user) {
        Map args = new HashMap();
        args.put("user", user);
        args.put("groupNum", groupNum);
        this.dao2._queryStr("select ifnull(status,'') from assistyuejuan where assister={user} and groupNum={groupNum} ", args);
        String flag = "F";
        int i = this.dao2._execute("delete from assistyuejuan where groupNum={groupNum} and assister={user} ", args);
        if (i > 0) {
            flag = "T";
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> assistListInfo(String exam, String grade, String subject, String qNum, String assistStatus) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("qNum", qNum);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject} ", args);
        args.put("examPaperNum", exampaperNum);
        String qNumStr = "";
        if (qNum != null && !qNum.equals("-1")) {
            qNumStr = " and qg.groupNum={qNum} ";
        }
        String assistStr = "";
        if (assistStatus != null && assistStatus.equals("1")) {
            assistStr = " and ay.status='0' ";
        } else if (assistStatus != null && assistStatus.equals("2")) {
            assistStr = " and ay.status!='0' ";
        }
        String sql = "select qg.groupNum,dd.groupName ,ifnull(y.d_num,0)d_num,ay.assister,u.realName,ay.status, case when ay.status=1 then '通过' when ay.status=2 then '驳回' when ay.status=3 then '主动取消' else '' end statusStr,case  when dd.choosename='s' and m.makType=1 then IFNULL(r.count*2,0)+IFNULL(x.count,0) when dd.choosename='s' and m.makType=0 then IFNULL(r.count,0)  when dd.choosename<>'s' and m.makType=1 then IFNULL(ch.alltotalNum*2,0)+IFNULL(x.count,0)when dd.choosename<>'s' and m.makType=0 then IFNULL(ch.alltotalNum,0)  end totalNum from questiongroup qg LEFT JOIN exampaper e ON qg.exampaperNum= e.examPaperNum LEFT JOIN  (             SELECT d.id orderId,d.id ,d.questionNum groupName,d.choosename from define d left join exampaper s on d.exampaperNum = s.exampaperNum where s.examNum={exam}  and s.gradeNum={grade}  and s.subjectNum= {subject}              UNION \t\t\t   SELECT sd.pid orderId,sd.id,sd.questionNum groupName,d.choosename choosename  from define d LEFT JOIN subdefine sd on sd.pid=d.id left join exampaper s on sd.exampaperNum = s.exampaperNum where  s.examNum={exam}  and s.gradeNum={grade}  and s.subjectNum= {subject}              ) dd on qg.groupNum = dd.id LEFT JOIN  ( \t\t\t   SELECT groupNum,makType FROM questiongroup_mark_setting q LEFT JOIN exampaper s ON q.exampaperNum = s.examPaperNum \t\t\t   WHERE s.examNum={exam} and s.gradeNum={grade} and s.subjectNum= {subject}             ) m ON m.groupNum=qg.groupNum LEFT JOIN  (             select d.id,count(DISTINCT r.studentId) count from(             SELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d INNER JOIN exampaper e on d.category=e.examPaperNum where d.exampaperNum={examPaperNum}  and d.questionType=1   \t\t\tUNION  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from subdefine d INNER JOIN exampaper e on d.category=e.examPaperNum where d.exampaperNum={examPaperNum}  and d.questionType=1             )d LEFT JOIN(              SELECT DISTINCT r.studentId,r.exampapernum ext1 from regexaminee r left join exampaper s on r.exampaperNum = s.exampaperNum WHERE s.examNum={exam}  and s.gradeNum={grade}  and s.subjectNum= {subject}  and r.scan_import=0             union \t\t\t   SELECT r.studentId,e.examPaperNum ext1 FROM(             SELECT DISTINCT r.studentId,r.exampapernum from regexaminee r left join exampaper s on r.exampaperNum = s.exampaperNum WHERE s.examNum={exam}  and s.gradeNum={grade}  and s.subjectNum= {subject}  and r.scan_import=0             )r INNER JOIN student s on r.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid              INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and r.exampaperNum=e.pexampaperNum             )r on d.category=r.ext1 GROUP BY d.id             ) r on qg.groupNum=r.id LEFT JOIN  (             SELECT groupNum,count(1) count FROM task  t  LEFT JOIN exampaper s ON t.exampaperNum = s.examPaperNum  WHERE s.examNum={exam}  and s.gradeNum={grade}  and s.subjectNum= {subject}  and t.userNum=3  GROUP BY groupNum            )x ON qg.groupNum = x.groupNum LEFT JOIN choosescale ch on qg.groupNum=ch.groupNum LEFT JOIN  (             SELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num,count(DISTINCT(questionNum)) as tempcount             FROM task  t  LEFT JOIN exampaper s ON t.exampaperNum = s.examPaperNum  WHERE s.examNum={exam}  and s.gradeNum={grade}  and s.subjectNum= {subject}  AND  t.status='T'   GROUP BY groupNum             )y ON qg.groupNum = y.groupNum LEFT JOIN assistYuejuan ay ON qg.groupNum=ay.groupNum left join user u on ay.assister= u.id where ay.groupNum is not null and e.examNum={exam}  and e.gradeNum={grade}  and e.subjectNum= {subject}  " + qNumStr + assistStr;
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String agreeAssist(String exam, String grade, String subject, String groupNum, String assister, String user) {
        Map args = new HashMap();
        args.put("user", user);
        args.put("groupNum", groupNum);
        args.put("assister", assister);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        String flag = "T";
        int i = this.dao2._execute("update assistyuejuan set status='1',updateuser={user} ,updateDate=now() where groupNum={groupNum} and assister={assister} ", args);
        if (i == 0) {
            flag = "F";
        }
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam}  and gradeNum={grade}  and subjectNum={subject} ", args);
        args.put("examPaperNum", exampaperNum);
        String roleNum = this.dao2._queryStr("select roleNum from role r left join exampaper exp on r.exampaperNum=exp.exampaperNum where r.type='4' and exp.examNum={exam}  and exp.gradeNum={grade}  and exp.subjectNum={subject}  ", args);
        args.put("roleNum", roleNum);
        if (flag.equals("T") && StrUtil.isEmpty(roleNum)) {
            args.put("roleNum", GUID.getGUIDStr());
            int i2 = this.dao2._execute("insert into role(roleNum,roleName,schoolNum,examNum,exampaperNum,type,insertUser,insertDate,isDelete) values({roleNum},'阅卷员','0',{exam} ,{examPaperNum} ,'4',{user},now(),'F')", args);
            if (i2 == 0) {
                flag = "F";
            }
        }
        if (flag.equals("T")) {
            String id = this.dao2._queryStr("select id from userrole where roleNum={roleNum} and userNum={assister} ", args);
            if (StrUtil.isEmpty(id)) {
                int i3 = this.dao2._execute("insert into userrole (userNum,roleNum,insertUser,insertDate,isDelete) values({assister},{roleNum},{user},now(),'F') ", args);
                if (i3 == 0) {
                    flag = "F";
                }
            }
        }
        if (flag.equals("T")) {
            String count = this.dao2._queryStr("select ifnull(count(1),0) from questionGroup_user where examPaperNum={examPaperNum}  and groupNum={groupNum} and userNum={assister} ", args);
            if ("0".equals(count)) {
                String uuid = GUID.getGUIDStr();
                args.put("uuid", uuid);
                int i4 = this.dao2._execute("insert into questionGroup_user (id,exampaperNum,groupNum,userType,userNum,insertUser,insertDate,isFinished) values({uuid},{examPaperNum} ,{groupNum},'0',{assister},{user},now(),0) ", args);
                if (i4 == 0) {
                    flag = "F";
                }
            }
        }
        if (flag.equals("T")) {
            String count2 = this.dao2._queryStr("select ifnull(count(1),0) from quota where examPaperNum={examPaperNum}  and groupNum={groupNum} and insertUser={assister} ", args);
            if ("0".equals(count2)) {
                int i5 = this.dao2._execute("insert into quota (exampaperNum,groupNum,num,insertUser,insertDate) values({examPaperNum} ,{groupNum},'0',{assister},now()) ", args);
                if (i5 == 0) {
                    flag = "F";
                }
            }
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String rejectAssist(String groupNum, String assister, String user) {
        String flag = "F";
        Map args = StreamMap.create().put("user", (Object) user).put("groupNum", (Object) groupNum).put("assister", (Object) assister);
        int i = this.dao2._execute("update assistYuejuan set status='2',updateuser={user},updateDate=now() where groupNum={groupNum} and assister={assister} ", args);
        if (i > 0) {
            flag = "T";
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String getAppealDealDate(String exam, String grade, String subject) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade).put("subject", (Object) subject);
        Object appealDealDate = this.dao2._queryObject("select appealDealDate from exampaper where examNum = {exam}  and gradeNum = {grade}  and subjectNum = {subject} ", args);
        return null == appealDealDate ? "" : appealDealDate.toString();
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, String>> getExamAndGraAndSubData() {
        StringBuffer sql = new StringBuffer();
        sql.append("select e.examNum,e.examName,bg.gradeNum,bg.gradeName,sub.subjectNum,sub.subjectName from exampaper al ");
        sql.append("inner join exam e on e.examNum=al.examNum ");
        sql.append("inner join basegrade bg on bg.gradeNum=al.gradeNum ");
        sql.append("inner join `subject` sub on sub.subjectNum=al.subjectNum ");
        sql.append("where e.isDelete='F' group by al.examPaperNum ");
        sql.append("order by e.examDate desc,e.examNum,bg.gradeNum,sub.orderNum,sub.subjectNum ");
        return this.dao2.queryMapList(sql.toString());
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getexamList() {
        return this.dao2._queryMapList("select e.examNum num,e.examName name from  exam e  where e.isDelete='F' order by e.examDate desc,e.examNum ", null, null);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getgradeList(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao2._queryMapList("select DISTINCT bg.gradeNum num,bg.gradeName name from exampaper al inner join basegrade bg on bg.gradeNum=al.gradeNum and al.examNum={examNum} order by bg.gradeNum ", null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getstudentTypeList(String examNum, String gradeNum) {
        Map args = new HashMap();
        args.put("exam", examNum);
        args.put("grade", gradeNum);
        return this.dao2._queryMapList("SELECT DISTINCT al.studentType num,d.name from arealevel al  INNER JOIN (SELECT value,name from data where type='25')d on d.value = al.studentType  where al.examNum={exam} and al.gradeNum={grade} ", null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getxuankezuheList(String examNum, String gradeNum, String studentType) {
        String sql = "select distinct al.xuankezuhe num,sc.subjectCombineName name,ifnull(sc.isParent,0) ext1 from arealevel al inner join subjectcombine sc on al.xuankezuhe = sc.subjectCombineNum where al.examNum={exam} and al.gradeNum={grade}  ";
        if (null != studentType && !studentType.equals("")) {
            sql = sql + " and al.studentType={subjectType} ";
        }
        String sql2 = sql + " order by sc.orderNum  ";
        Map args = new HashMap();
        args.put("exam", examNum);
        args.put("grade", gradeNum);
        args.put("subjectType", studentType);
        return this.dao2._queryMapList(sql2, null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getsubjectList(String examNum, String gradeNum, String studentType, String xuankezuhe) {
        String xuankezuheStr = "";
        String studentTypeStr = "";
        if (null != xuankezuhe && !xuankezuhe.equals("")) {
            xuankezuheStr = "and al.xuankezuhe= {xuankezuhe} ";
        }
        if (null != studentType && !studentType.equals("")) {
            studentTypeStr = "and al.studentType= {studentType} ";
        }
        String sql = "SELECT DISTINCT al.subjectNum num,s.subjectName name FROM arealevel al LEFT JOIN `subject` s ON s.subjectNum=al.subjectNum where al.examNum = {exam} and al.gradeNum = {grade} " + xuankezuheStr + studentTypeStr + " and s.subjectNum is not null order by s.orderNum";
        Map args = new HashMap();
        args.put("xuankezuhe", xuankezuhe);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("exam", examNum);
        args.put("grade", gradeNum);
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, String>> getQNumData(String exam, String grade, String subject) {
        StringBuffer sql = new StringBuffer();
        sql.append("select alq.questionNum num,IFNULL(d.questionNum,subd.questionNum) name from ");
        sql.append("(select distinct questionNum from arealevel_question where examNum={exam}  and gradeNum={grade}  and subjectNum={subject}  and qtype='1') alq ");
        sql.append("left join define d on d.id=alq.questionNum and d.isParent='0' and d.choosename<>'T' ");
        sql.append("left join subdefine subd on subd.id=alq.questionNum ");
        sql.append("left join define d2 on d2.id=subd.pid ");
        sql.append("where d.id is not null or subd.id is not null ");
        sql.append("order by IFNULL(d.orderNum,d2.orderNum),subd.orderNum ");
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade).put("subject", (Object) subject);
        return this.dao2._queryMapList(sql.toString(), TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, String>> getTcAndErList(String exam, String grade, String subject) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        Object examPaperNum = this.dao2._queryObject("select examPaperNum from exampaper where examNum={exam}  and gradeNum={grade}  and subjectNum={subject} ", args);
        args.put("examPaperNum", examPaperNum);
        StringBuffer sql = new StringBuffer();
        sql.append("select r.testingCentreId,tc.testingCentreName,r.examinationRoomNum,er.examinationRoomName from ");
        sql.append("(select testingCentreId,examinationRoomNum from regexaminee where examPaperNum={examPaperNum}  and scan_import='0' group by testingCentreId,examinationRoomNum) r ");
        sql.append("inner join testingcentre tc on tc.id=r.testingCentreId ");
        sql.append("inner join examinationroom er on er.id=r.examinationRoomNum ");
        sql.append("order by convert(tc.testingCentreName using gbk),convert(er.examinationRoomName using gbk) ");
        return this.dao2._queryMapList(sql.toString(), TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getStuRegDataByTeachUnit(Exampaper ep, String teachUnit, String teachUnit_s, String exportMode, String paiming, String fenshu, String levelclass, String studentName) {
        StringBuffer stuSql = new StringBuffer();
        if ("00".equals(teachUnit_s)) {
            stuSql.append("select studentId,totalScore,schoolNum,classNum from statisticstudentlevel where statisticId={teachUnit}  and examPaperNum={ExamPaperNum}  ");
        } else if ("01".equals(teachUnit_s)) {
            stuSql.append("select studentId,totalScore,schoolNum,classNum from studentlevel where examPaperNum={ExamPaperNum}  and schoolNum={teachUnit}  ");
        } else if ("02".equals(teachUnit_s)) {
            stuSql.append("select studentId,totalScore,schoolNum,classNum from studentlevel where classNum={teachUnit}  and examPaperNum={ExamPaperNum}  ");
        }
        if ("fenshu".equals(exportMode)) {
            stuSql.append("and totalScore>={fenshu} ");
        }
        stuSql.append("group by studentId ");
        if ("paiming".equals(exportMode)) {
            stuSql.append("order by totalScore desc limit {paiming} ");
        }
        StringBuffer regSql = new StringBuffer();
        regSql.append("select studentId,id,page from regexaminee where examPaperNum={PexamPaperNum}  ");
        StringBuffer sql = new StringBuffer();
        sql.append("select sl.schoolNum,sch.schoolName,sl.classNum,cla.className,sl.totalScore,sl.studentId sid,stu.studentName,stu.studentId,reg.page,reg.id regId from (");
        sql.append(stuSql);
        sql.append(") sl inner join (");
        sql.append(regSql);
        sql.append(") reg on reg.studentId=sl.studentId ");
        sql.append("left join school sch on sch.id=sl.schoolNum ");
        Object[] objArr = new Object[1];
        objArr[0] = "T".equals(levelclass) ? Const.levelclass : "class";
        sql.append(StrUtil.format("left join {} cla on cla.id=sl.classNum ", objArr));
        if ("".equals(studentName)) {
            sql.append("left join student stu on stu.id=sl.studentId ");
        } else {
            sql.append("inner join (");
            sql.append("select id,studentId,studentName from student where studentName like {studentName} ");
            sql.append(") stu on stu.id=sl.studentId ");
        }
        Map args = new HashMap();
        args.put("teachUnit", teachUnit);
        args.put("ExamPaperNum", ep.getExamPaperNum());
        args.put("fenshu", fenshu);
        args.put("paiming", paiming);
        args.put("PexamPaperNum", ep.getPexamPaperNum());
        args.put("studentName", "%" + studentName + "%");
        return this.dao2._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getStuRegDataByExamroom(Exampaper ep, String testingcentre, String examroom, String studentName) {
        StringBuffer stuSql = new StringBuffer();
        stuSql.append("select studentId,totalScore from studentlevel where examPaperNum={ExamPaperNum}  ");
        StringBuffer regSql = new StringBuffer();
        String erStr = "-1".equals(examroom) ? "" : " and examinationRoomNum={examroom}  ";
        String tcStr = "-1".equals(testingcentre) ? "" : " and testingCentreId={testingcentre}  ";
        regSql.append("select studentId,id,page,testingCentreId,examinationRoomNum from regexaminee where examPaperNum={PexamPaperNum}  ");
        regSql.append(erStr);
        regSql.append(tcStr);
        StringBuffer sql = new StringBuffer();
        sql.append("select reg.testingCentreId,tc.testingCentreName,reg.examinationRoomNum,er.examinationRoomName,er.examinationRoomName className,sl.totalScore,sl.studentId sid,stu.studentName,stu.studentId,reg.page,reg.id regId from (");
        sql.append(stuSql);
        sql.append(") sl inner join (");
        sql.append(regSql);
        sql.append(") reg on reg.studentId=sl.studentId ");
        sql.append("left join testingcentre tc on tc.id=reg.testingCentreId ");
        sql.append("left join examinationroom er on er.id=reg.examinationRoomNum ");
        if ("".equals(studentName)) {
            sql.append("left join student stu on stu.id=sl.studentId ");
        } else {
            sql.append("inner join (");
            sql.append("select id,studentId,studentName from student where studentName like {studentName}  ");
            sql.append(") stu on stu.id=sl.studentId ");
        }
        Map args = new HashMap();
        args.put("ExamPaperNum", ep.getExamPaperNum());
        args.put("examroom", examroom);
        args.put("testingcentre", testingcentre);
        args.put("PexamPaperNum", ep.getPexamPaperNum());
        args.put("studentName", "%" + studentName + "%");
        return this.dao2._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getStuQnumDataByTeachUnit(Exampaper ep, String teachUnit, String teachUnit_s, String exportMode, String paiming, String fenshu, String qNum, String levelclass, String studentName) {
        String scoreStr = "fenshu".equals(exportMode) ? " and questionScore>={fenshu} " : "";
        StringBuffer stuScoreSql = new StringBuffer();
        if ("00".equals(teachUnit_s)) {
            StringBuffer stuSql = new StringBuffer();
            stuSql.append("select distinct studentId from statisticstudentlevel where statisticId={teachUnit}  and examPaperNum={ExamPaperNum}  ");
            StringBuffer scoreSql = new StringBuffer();
            scoreSql.append("select studentId,questionScore,schoolNum,classNum,id scoreId from score where examPaperNum={PexamPaperNum}  and questionNum={qNum} and continued='F'  ");
            scoreSql.append(scoreStr);
            stuScoreSql.append("select sco.* from (");
            stuScoreSql.append(stuSql);
            stuScoreSql.append(") sl inner join (");
            stuScoreSql.append(scoreSql);
            stuScoreSql.append(") sco on sco.studentId=sl.studentId ");
        } else {
            String claStr = "01".equals(teachUnit_s) ? " where schoolNum={teachUnit}  " : " where classNum={teachUnit}  ";
            stuScoreSql.append("select studentId,questionScore,schoolNum,classNum,id scoreId from score ");
            stuScoreSql.append(claStr);
            stuScoreSql.append(" and examPaperNum={PexamPaperNum}  and questionNum={qNum} and continued='F'  ");
            stuScoreSql.append(scoreStr);
        }
        String rankStr = "paiming".equals(exportMode) ? " order by sl.questionScore desc limit {paiming} " : "";
        StringBuffer sql = new StringBuffer();
        sql.append("select sl.schoolNum,sch.schoolName,sl.classNum,cla.className,sl.questionScore,sl.studentId sid,stu.studentName,stu.studentId,sl.scoreId from (");
        sql.append(stuScoreSql);
        sql.append(") sl ");
        sql.append("left join school sch on sch.id=sl.schoolNum ");
        Object[] objArr = new Object[1];
        objArr[0] = "T".equals(levelclass) ? Const.levelclass : "class";
        sql.append(StrUtil.format("left join {} cla on cla.id=sl.classNum ", objArr));
        if ("".equals(studentName)) {
            sql.append("left join student stu on stu.id=sl.studentId ");
        } else {
            sql.append("inner join (");
            sql.append("select id,studentId,studentName from student where studentName like {studentName} ");
            sql.append(") stu on stu.id=sl.studentId ");
        }
        sql.append(rankStr);
        Map args = new HashMap();
        args.put("fenshu", fenshu);
        args.put("teachUnit", teachUnit);
        args.put("ExamPaperNum", ep.getExamPaperNum());
        args.put("PexamPaperNum", ep.getPexamPaperNum());
        args.put("qNum", qNum);
        args.put("paiming", paiming);
        args.put("studentName", "%" + studentName + "%");
        return this.dao2._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getStuQnumDataByExamroom(Exampaper ep, String testingcentre, String examroom, String qNum, String studentName) {
        StringBuffer scoreSql = new StringBuffer();
        String erStr = "-1".equals(examroom) ? "" : " and examinationRoomNum={examroom}  ";
        String tcStr = "-1".equals(testingcentre) ? "" : " and testingCentreId={testingcentre}  ";
        scoreSql.append("select studentId,questionScore,testingCentreId,examinationRoomNum,id scoreId from score where questionNum={qNum}  and examPaperNum={PexamPaperNum} and continued='F'  ");
        scoreSql.append(erStr);
        scoreSql.append(tcStr);
        StringBuffer sql = new StringBuffer();
        sql.append("select sl.testingCentreId,tc.testingCentreName,sl.examinationRoomNum,er.examinationRoomName,er.examinationRoomName className,sl.questionScore,sl.studentId sid,stu.studentName,stu.studentId,sl.scoreId from (");
        sql.append(scoreSql);
        sql.append(") sl ");
        sql.append("left join testingcentre tc on tc.id=sl.testingCentreId ");
        sql.append("left join examinationroom er on er.id=sl.examinationRoomNum ");
        if ("".equals(studentName)) {
            sql.append("left join student stu on stu.id=sl.studentId ");
        } else {
            sql.append("inner join (");
            sql.append("select id,studentId,studentName from student where studentName like {studentName} ");
            sql.append(") stu on stu.id=sl.studentId ");
        }
        Map args = new HashMap();
        args.put("examroom", examroom);
        args.put("testingcentre", testingcentre);
        args.put("qNum", qNum);
        args.put("PexamPaperNum", ep.getPexamPaperNum());
        args.put("studentName", "%" + studentName + "%");
        return this.dao2._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getQueAndAnsList(String examPaperNum) {
        StringBuffer sql = new StringBuffer();
        sql.append("select id,page,imgtype from answerexampaperimage where examPaperNum={examPaperNum}  ");
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao2._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public String getShensuYuzhi(String questionNum) {
        StringBuffer sql = new StringBuffer();
        sql.append("select IFNULL(shensuYuzhi,0) shensuYuzhi from questiongroup_question ");
        sql.append("where questionNum={questionNum} ");
        Map args = StreamMap.create().put("questionNum", (Object) questionNum);
        return this.dao2._queryStr(sql.toString(), args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public int getQuesTotalCount(int examPaperNum) {
        StringBuffer dSql = new StringBuffer();
        dSql.append("select a.id,a.choosename from define a ");
        dSql.append("where ");
        dSql.append(" a.examPaperNum={examPaperNum}  ");
        dSql.append("group by a.choosename ");
        StringBuffer sql = new StringBuffer();
        sql.append("select count(1) from define a ");
        sql.append("left join (");
        sql.append(dSql);
        sql.append(") b on b.choosename=CAST(a.id AS CHAR) ");
        sql.append("left join subdefine c on c.pid=a.id or c.pid=b.id ");
        sql.append("where ");
        sql.append(" a.examPaperNum={examPaperNum}  ");
        sql.append("and LENGTH(a.choosename) < 2");
        Map args = StreamMap.create().put("examPaperNum", (Object) Integer.valueOf(examPaperNum));
        Object res = this.dao2._queryObject(sql.toString(), args);
        if (null == res) {
            return 0;
        }
        return Integer.valueOf(res.toString()).intValue();
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getTishuBuquanData(int examPaperNum, String testingCentreId, int totalCount) {
        String tcStr = "-1".equals(testingCentreId) ? "" : " and testingCentreId={testingCentreId}  ";
        StringBuffer objSql = new StringBuffer();
        objSql.append("select studentId,testingCentreId,regId,page from objectivescore ");
        objSql.append("where ");
        objSql.append(" examPaperNum={examPaperNum}  ");
        objSql.append(tcStr);
        StringBuffer sSql = new StringBuffer();
        sSql.append("select studentId,testingCentreId,regId,page from score ");
        sSql.append("where ");
        sSql.append(" examPaperNum={examPaperNum}  ");
        sSql.append(tcStr);
        sSql.append("and continued='F' ");
        StringBuffer stuSql = new StringBuffer();
        stuSql.append("select a.studentId,a.testingCentreId,a.regId,a.page,count(1) tishu from (");
        stuSql.append(objSql);
        stuSql.append("union all ");
        stuSql.append(sSql);
        stuSql.append(") a ");
        stuSql.append("group by a.studentId ");
        stuSql.append("HAVING tishu <> {totalCount}");
        StringBuffer sql = new StringBuffer();
        sql.append("select tc.testingCentreName,stu.studentName,stu.studentId,0 yichangType,'题数不全' yichangStatus,s.studentId sId,s.regId,s.page,s.testingCentreId from (");
        sql.append(stuSql);
        sql.append(") s ");
        sql.append("left join student stu on stu.id=s.studentId ");
        sql.append("left join testingcentre tc on tc.id=s.testingCentreId ");
        Map args = new HashMap();
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("testingCentreId", testingCentreId);
        args.put("totalCount", Integer.valueOf(totalCount));
        return this.dao2._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getDuotiData(int examPaperNum, String testingCentreId) {
        String tcStr = "-1".equals(testingCentreId) ? "" : " and testingCentreId={testingCentreId}  ";
        StringBuffer objSql = new StringBuffer();
        objSql.append("select studentId,testingCentreId,questionNum,regId,page from objectivescore ");
        objSql.append("where ");
        objSql.append(" examPaperNum={examPaperNum}  ");
        objSql.append(tcStr);
        StringBuffer sSql = new StringBuffer();
        sSql.append("select studentId,testingCentreId,questionNum,regId,page from score ");
        sSql.append("where ");
        sSql.append(" examPaperNum={examPaperNum}  ");
        sSql.append(tcStr);
        sSql.append("and continued='F' ");
        StringBuffer dSql = new StringBuffer();
        dSql.append("select id from define ");
        dSql.append("where ");
        dSql.append(" examPaperNum={examPaperNum}  ");
        StringBuffer subdSql = new StringBuffer();
        subdSql.append("select id from subdefine ");
        subdSql.append("where ");
        subdSql.append(" examPaperNum={examPaperNum}  ");
        StringBuffer stuSql = new StringBuffer();
        stuSql.append("select a.studentId,a.testingCentreId,a.regId,a.page,count(1) tishu from (");
        stuSql.append(objSql);
        stuSql.append("union all ");
        stuSql.append(sSql);
        stuSql.append(") a ");
        stuSql.append("left join (");
        stuSql.append(dSql);
        stuSql.append("union all ");
        stuSql.append(subdSql);
        stuSql.append(") d on d.id=a.questionNum ");
        stuSql.append("where d.id is null ");
        stuSql.append("group by a.studentId ");
        StringBuffer sql = new StringBuffer();
        sql.append("select tc.testingCentreName,stu.studentName,stu.studentId,1 yichangType,'多题' yichangStatus,s.studentId sId,s.regId,s.page,s.testingCentreId from (");
        sql.append(stuSql);
        sql.append(") s ");
        sql.append("left join student stu on stu.id=s.studentId ");
        sql.append("left join testingcentre tc on tc.id=s.testingCentreId ");
        Map args = new HashMap();
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("testingCentreId", testingCentreId);
        return this.dao2._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getTiChongfuData(int examPaperNum, String testingCentreId) {
        String tcStr = "-1".equals(testingCentreId) ? "" : " and testingCentreId={testingCentreId}  ";
        StringBuffer objSql = new StringBuffer();
        objSql.append("select studentId,testingCentreId,questionNum,regId,page from objectivescore ");
        objSql.append("where ");
        objSql.append(" examPaperNum={examPaperNum}  ");
        objSql.append(tcStr);
        StringBuffer sSql = new StringBuffer();
        sSql.append("select studentId,testingCentreId,questionNum,regId,page from score ");
        sSql.append("where ");
        sSql.append(" examPaperNum={examPaperNum}  ");
        sSql.append(tcStr);
        sSql.append("and continued='F' ");
        StringBuffer stuSql = new StringBuffer();
        stuSql.append("select a.studentId,a.testingCentreId,a.regId,a.page,count(1) tishu from (");
        stuSql.append(objSql);
        stuSql.append("union all ");
        stuSql.append(sSql);
        stuSql.append(") a ");
        stuSql.append("group by a.studentid,a.questionNum ");
        stuSql.append("having tishu>1 ");
        StringBuffer sql = new StringBuffer();
        sql.append("select tc.testingCentreName,stu.studentName,stu.studentId,2 yichangType,'题目重复' yichangStatus,s.studentId sId,s.regId,s.page,s.testingCentreId from (");
        sql.append(stuSql);
        sql.append(") s ");
        sql.append("left join student stu on stu.id=s.studentId ");
        sql.append("left join testingcentre tc on tc.id=s.testingCentreId ");
        Map args = new HashMap();
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("testingCentreId", testingCentreId);
        return this.dao2._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public List<Map<String, Object>> getStuAllQuesList(String sId, String examPaperNum) {
        StringBuffer objSql = new StringBuffer();
        objSql.append("select studentId,questionNum,page,id,regId,examPaperNum,questionScore,isModify,'0' qType from objectivescore ");
        objSql.append("where ");
        objSql.append(" studentId={sId}  ");
        objSql.append("and ");
        objSql.append(" examPaperNum={examPaperNum}  ");
        StringBuffer sSql = new StringBuffer();
        sSql.append("select studentId,questionNum,page,id,regId,examPaperNum,questionScore,isModify,'1' qType from score ");
        sSql.append("where ");
        sSql.append(" studentId={sId}  ");
        sSql.append("and ");
        sSql.append(" examPaperNum={examPaperNum}  ");
        sSql.append("and continued='F' ");
        StringBuffer stuSql = new StringBuffer();
        stuSql.append("select stu.studentName,IFNULL(d.questionNum,subd.questionNum) questionName,s.page,s.id scoreId,s.regId,s.studentId");
        stuSql.append(",s.questionNum,s.examPaperNum,s.questionScore,s.isModify,s.qType,re.cnum from (");
        stuSql.append(objSql);
        stuSql.append("union all ");
        stuSql.append(sSql);
        stuSql.append(") s ");
        stuSql.append("left join define d on d.id=s.questionNum ");
        stuSql.append("left join subdefine subd on subd.id=s.questionNum ");
        stuSql.append("left join define d2 on d2.id=subd.pid ");
        stuSql.append("left join regexaminee re on s.studentId=re.studentId and s.examPaperNum=re.examPaperNum and s.regid=re.id ");
        stuSql.append("left join student stu on stu.id=s.studentId ");
        stuSql.append("order by IFNULL(d.orderNum,d2.orderNum),subd.orderNum ");
        Map args = new HashMap();
        args.put("sId", sId);
        args.put("examPaperNum", examPaperNum);
        return this.dao2._queryMapList(stuSql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.QuestionNumListService
    public void deleteStuOneQuestion(String scoreId, String qType) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        if ("1".equals(qType)) {
            StringBuffer tSql = new StringBuffer();
            tSql.append("delete from task ");
            tSql.append("where scoreId={scoreId} ");
            this.dao2._execute(tSql.toString(), args);
            StringBuffer sSql = new StringBuffer();
            sSql.append("delete from score ");
            sSql.append("where id={scoreId} ");
            this.dao2._execute(sSql.toString(), args);
            return;
        }
        StringBuffer objsSql = new StringBuffer();
        objsSql.append("delete from objectivescore ");
        objsSql.append("where id={scoreId} ");
        this.dao2._execute(objsSql.toString(), args);
    }
}
