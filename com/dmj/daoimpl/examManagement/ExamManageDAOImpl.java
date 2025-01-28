package com.dmj.daoimpl.examManagement;

import cn.hutool.core.convert.Convert;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.questionGroup.QuestionGroupDaoImpl;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Basegrade;
import com.dmj.domain.Class;
import com.dmj.domain.Classlevel;
import com.dmj.domain.CorrectStatus;
import com.dmj.domain.Data;
import com.dmj.domain.Define;
import com.dmj.domain.Exam;
import com.dmj.domain.Examinationnum;
import com.dmj.domain.Examinationroom;
import com.dmj.domain.ExamineeNumError;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Grade;
import com.dmj.domain.MarkError;
import com.dmj.domain.ObjectiveScore;
import com.dmj.domain.Questionimage;
import com.dmj.domain.RegExaminee;
import com.dmj.domain.Remark;
import com.dmj.domain.School;
import com.dmj.domain.Score;
import com.dmj.domain.Scoreimage;
import com.dmj.domain.Student;
import com.dmj.domain.Studentlevel;
import com.dmj.domain.Studentpaperimage;
import com.dmj.domain.StudentsTotalScore;
import com.dmj.domain.Subject;
import com.dmj.domain.Task;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.zht.db.DbUtils;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/* loaded from: ExamManageDAOImpl.class */
public class ExamManageDAOImpl {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    ExamDAOImpl examDao = new ExamDAOImpl();
    private QuestionGroupDaoImpl questionGroupDao = new QuestionGroupDaoImpl();
    Logger log = Logger.getLogger(getClass());

    public List getPaperQuestion(String examNum, String gradeNum, String subjectNum, String examPaperNum) {
        String sql;
        if (examPaperNum.equals("null")) {
            examPaperNum = this.examDao.getExampaperNum(examNum, subjectNum, gradeNum);
            sql = "SELECT def.id,def.examPaperNum,GROUP_CONCAT(def.questionNum) questionNum,def.questionType,def.fullScore,def.stdAanswer,def.choosename,GROUP_CONCAT(def.orderNum) orderNum,def.isParent,def.pid   FROM(SELECT  id,examPaperNum, questionNum,questionType,fullScore,answer stdAanswer,IF(LENGTH(choosename)>4,'T',choosename) choosename , orderNum   ,isParent,IF(LENGTH(choosename)>4,choosename,id) pid  FROM define  WHERE category={examPaperNum} AND isParent='0'   UNION ALL  SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename,orderNum,'0' isParent,pid   FROM subdefine   WHERE category={examPaperNum}   ) def  GROUP BY def.id,def.pid   ORDER BY REPLACE(GROUP_CONCAT(def.questionNum),'_','.')*1,GROUP_CONCAT(def.orderNum),CONVERT(GROUP_CONCAT(def.questionNum) USING gbk) ";
        } else {
            sql = "SELECT def.id,def.examPaperNum,GROUP_CONCAT(def.questionNum) questionNum,def.questionType,def.fullScore,def.stdAanswer,def.choosename,GROUP_CONCAT(def.orderNum) orderNum,def.isParent,def.pid   FROM(SELECT  id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,IF(LENGTH(choosename)>4,'T',choosename) choosename , orderNum   ,isParent,IF(LENGTH(choosename)>4,choosename,id) pid  FROM define  WHERE examPaperNum={examPaperNum} AND isParent='0'   UNION ALL  SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename,orderNum,'0' isParent,pid   FROM subdefine   WHERE examPaperNum={examPaperNum}   ) def  GROUP BY def.id,def.pid   ORDER BY REPLACE(GROUP_CONCAT(def.questionNum),'_','.')*1,GROUP_CONCAT(def.orderNum),CONVERT(GROUP_CONCAT(def.questionNum) USING gbk) ";
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        this.log.info("获得科目在define表中的所有题目---sql:" + sql);
        return this.dao2._queryBeanList(sql, Define.class, args);
    }

    public String checkExamstatus(String[] examNums) {
        String showMsg = "";
        for (String examNum : examNums) {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
            List<?> _queryColList = this.dao2._queryColList("select concat(ex.examName ,\"的\",gra.gradeName,sub.subjectName,\"未计算完成；\") from exampaper EXP\tLEFT JOIN basegrade gra on EXP.gradeNum = gra.gradeNum\tLEFT JOIN subject sub on EXP.subjectNum = sub.subjectNum LEFT JOIN exam ex on ex.examNum = EXP.examNum where EXP.examNum = {examNum} and EXP.status =0 and EXP.isHidden='F'", String.class, args);
            for (int i = 0; i < _queryColList.size(); i++) {
                String msg = (String) _queryColList.get(i);
                showMsg = showMsg + String.valueOf(msg) + "\n";
            }
        }
        return showMsg;
    }

    public List getStudentsTotalScoreList_levelcla(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum) {
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        String examPaperNum = exampaperObj.getExamPaperNum() + "";
        String jie = exampaperObj.getJie();
        String totalPage = exampaperObj.getTotalPage();
        String pexamPaperNum = this.examDao.getpExampaperNum(examNum, subjectNum, gradeNum);
        String illStr = "(SELECT  a.studentId,GROUP_CONCAT(a.examineeNum) examineeNum,a.schoolNum,a.gradeNum   ,GROUP_CONCAT(a.aa) aa,a.examPaperNum,a.examinationRoomNum,a.type    FROM (SELECT DISTINCT r.*  FROM (SELECT sd.studentId,sd.gradeNum,sd.examNum,sd.examineeNum,GROUP_CONCAT(re.page) aa,re.exampaperNum,sd.examinationRoomNum,ill.type,sd.schoolNum   FROM (SELECT schoolNum,gradeNum,studentId,examNum,examineeNum,examinationRoomNum FROM examinationnum   WHERE examNum={examNum}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum}  and subjectNum ={gradeNum}  ) sd   LEFT JOIN (SELECT id,page,examPaperNum,examinationRoomNum,studentId FROM regexaminee   WHERE examPaperNum={pexamPaperNum} AND schoolNum={schoolNum}) re ON sd.studentid = re.studentid    LEFT JOIN (SELECT type,examPaperNum,studentId,examinationRoomNum FROM illegal   WHERE examPaperNum={pexamPaperNum}   AND schoolNum={schoolNum}  ) ill ON  ill.studentId=sd.studentId   GROUP BY sd.studentid   HAVING COUNT(page)  <{totalPage} ) r    UNION ALL  SELECT studentId,'" + gradeNum + "' gradeNum,'" + examNum + "' examNum,NULL examineeNum,NULL aa,exampaperNum,examinationRoomNum,type,schoolNum   FROM  illegal WHERE examPaperNum={pexamPaperNum}   AND schoolNum={schoolNum}   ) a    GROUP BY a.studentId)";
        String sql = "SELECT stu.studentId realStudentId,stu.studentNum,en.examineeNum,stu.studentName,sc.schoolNum,sch.schoolName,sc.classNum,cla.className,sc.totalScore,ilg.type,ilg.aa possessPage,'" + totalPage + "' totalPage,stu.type studentType,levcla.classNum levelClassNum,levcla.className levelClassName    FROM(  SELECT b.studentId,b.schoolNum,b.gradeNum,b.classNum,ROUND(SUM(b.questionScore),2) totalScore,  b.examPaperNum,b.examinationRoomNum  FROM (SELECT DISTINCT a.studentId,a.schoolNum,a.gradeNum,a.classNum,a.questionScore,a.examPaperNum,a.examinationRoomNum,a.questionNum    FROM (SELECT a.examPaperNum,a.studentId,a.schoolNum,a.gradeNum,a.classNum,a.questionScore,a.examinationRoomNum,a.questionNum   FROM score  a INNER JOIN define b on (b.examPaperNum={examPaperNum} or b.category = {examPaperNum})  AND isParent='0' and a.questionNum=b.id  WHERE a.schoolNum={schoolNum}   AND a.continued={FALSE}   UNION  ALL   SELECT d.examPaperNum,d.studentId,d.schoolNum,d.gradeNum,d.classNum,d.questionScore,d.examinationRoomNum,d.questionNum   FROM objectivescore d  INNER JOIN define c on (c.examPaperNum={examPaperNum} or c.category = {examPaperNum})  AND isParent='0' and d.questionNum=c.id WHERE d.schoolNum={schoolNum}  ) a   )b GROUP BY b.studentId   ) sc    LEFT JOIN (SELECT id,studentNum,type,schoolNum,classNum,gradeNum,studentName,jie,studentId FROM student   WHERE  gradeNum={gradeNum}  AND  schoolNum={schoolNum}  ) stu   ON sc.studentId=stu.id   LEFT JOIN (SELECT levstu.id,levstu.subjectNum,levstu.sid studentId,levstu.classNum levclaId,levcla.classNum,levcla.className   FROM levelstudent levstu  LEFT JOIN levelclass levcla  ON levcla.id={classNum} AND levstu.classNum=levcla.id  AND levcla.subjectNum={subjectNum}  WHERE levstu.classNum={classNum} AND levstu.schoolNum={schoolNum}   AND levstu.gradeNum={gradeNum} AND levstu.subjectNum={schoolNum}  )levcla   ON levcla.studentId=stu.id  LEFT JOIN (SELECT studentId,examineeNum FROM examinationnum  WHERE examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum} and subjectNum = {schoolNum}) en   ON en.studentId=stu.id   LEFT JOIN (SELECT  id,schoolNum,schoolName,shortname  FROM school WHERE id={schoolNum}) sch \tON sc.schoolNum=sch.id   LEFT JOIN (SELECT  id,classNum,className,classType,schoolNum,gradeNum  FROM class WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie} ) cla \tON  stu.classNum=cla.id    LEFT JOIN  " + illStr + " ilg    ON  sc.studentId=ilg.studentId   WHERE stu.studentId  IS NOT NULL AND levcla.studentId IS NOT NULL   UNION ALL  SELECT stu.studentId realStudentId,stu.studentNum,res.examineeNum,stu.studentName,stu.schoolNum,sch.schoolName,stu.classNum,cla.className,0 totalScore,'0' type,NULL possessPage  ,'3' totalPage,stu.type studentType,levcla.classNum levelClassNum,levcla.className levelClassName   FROM(SELECT sd.studentId,sd.examineeNum  FROM (SELECT studentId,examineeNum,schoolNum   FROM examinationnum  WHERE examNum={examNum}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum} and subjectNum = {schoolNum} ) sd   LEFT JOIN (SELECT studentId FROM regexaminee WHERE examPaperNum={pexamPaperNum} AND schoolNum={schoolNum}  ) re ON sd.studentid = re.studentid WHERE re.studentId IS NULL  ) res  LEFT JOIN student stu ON res.studentId=stu.id   LEFT JOIN levelstudent levstu ON res.studentId=levstu.sid  LEFT JOIN levelclass levcla ON levstu.classNum=levcla.id   LEFT JOIN school sch ON stu.schoolNum=sch.schoolNum   LEFT JOIN class cla ON stu.classNum=cla.id   WHERE levcla.id={classNum}   ORDER BY schoolNum ASC,classNum*1 ASC,type DESC,totalScore DESC";
        this.log.info("sql-按班级-单科详情  科目 总分：" + sql);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("pexamPaperNum", (Object) pexamPaperNum).put("totalPage", (Object) totalPage).put("examPaperNum", (Object) examPaperNum).put("FALSE", (Object) "F").put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("jie", (Object) jie);
        return this.dao2._queryBeanList(sql, StudentsTotalScore.class, args);
    }

    public List getStudentsTotalScoreList(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum) {
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        String examPaperNum = exampaperObj.getExamPaperNum() + "";
        exampaperObj.getJie();
        String totalPage = exampaperObj.getTotalPage();
        String pexamPaperNum = this.examDao.getpExampaperNum(examNum, subjectNum, gradeNum);
        String illStr = "(SELECT  a.studentId,GROUP_CONCAT(a.examineeNum) examineeNum,a.schoolNum,a.gradeNum   ,GROUP_CONCAT(a.aa) aa,a.examPaperNum,a.examinationRoomNum,a.type    FROM (SELECT DISTINCT r.*  FROM (SELECT sd.studentId,sd.gradeNum,sd.examNum,sd.examineeNum,GROUP_CONCAT(re.page) aa,re.exampaperNum,sd.examinationRoomNum,ill.type,sd.schoolNum   FROM (SELECT schoolNum,gradeNum,studentId,examNum,examineeNum,examinationRoomNum FROM examinationnum   WHERE examNum={examNum}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum}  and subjectNum = {subjectNum}   ) sd   LEFT JOIN (SELECT id,page,examPaperNum,examinationRoomNum,studentId FROM regexaminee   WHERE examPaperNum={pexamPaperNum} AND schoolNum={schoolNum}) re ON sd.studentid = re.studentid    LEFT JOIN (SELECT type,examPaperNum,studentId,examinationRoomNum FROM illegal   WHERE examPaperNum={pexamPaperNum}   AND schoolNum={schoolNum}  ) ill ON  ill.studentId=sd.studentId   GROUP BY sd.studentid   HAVING COUNT(page)  <{totalPage}) r    UNION ALL  SELECT studentId,'" + gradeNum + "' gradeNum,'" + examNum + "' examNum,NULL examineeNum,NULL aa,exampaperNum,examinationRoomNum,type,schoolNum   FROM  illegal WHERE examPaperNum={pexamPaperNum}   AND schoolNum={schoolNum}   ) a    GROUP BY a.studentId)";
        String sql = "SELECT stu.studentId realStudentId,stu.studentNum,en.examineeNum,stu.studentName,sc.schoolNum,sch.schoolName,sc.classNum,  cla.className,sc.totalScore,ilg.type,ilg.aa possessPage,'" + totalPage + "' totalPage,stu.type studentType   FROM(  SELECT b.studentId,b.schoolNum,b.gradeNum,b.classNum,ROUND(SUM(b.questionScore),2) totalScore,  b.examPaperNum,b.examinationRoomNum  FROM (SELECT DISTINCT a.studentId,a.schoolNum,a.gradeNum,a.classNum,a.questionScore,a.examPaperNum,a.examinationRoomNum,a.questionNum    FROM (SELECT a.examPaperNum,a.studentId,a.schoolNum,a.gradeNum,a.classNum,a.questionScore,a.examinationRoomNum,a.questionNum   FROM score  a INNER JOIN  (  select de.id,de.category from define de  where de.examPaperNum={examPaperNum} or de.category = {examPaperNum} and isParent='0'  union all  select sb.id,sb.category from subdefine sb  where sb.examPaperNum={examPaperNum} or sb.category = {examPaperNum}   )b on a.questionNum=b.id WHERE a.schoolNum={schoolNum}  AND a.classNum={classNum}   AND a.continued={FALSE}   UNION  ALL   SELECT d.examPaperNum,d.studentId,d.schoolNum,d.gradeNum,d.classNum,d.questionScore,d.examinationRoomNum,d.questionNum   FROM objectivescore d  INNER JOIN  (   select de.id,de.category from define de  where de.examPaperNum={examPaperNum} or de.category = {examPaperNum} and isParent='0'   union all   select sb.id,sb.category from subdefine sb  where sb.examPaperNum={examPaperNum} or sb.category = {examPaperNum}   )b on d.questionNum=b.id   WHERE d.schoolNum={schoolNum}  AND d.classNum={classNum} ) a   )b GROUP BY b.studentId   ) sc    LEFT JOIN (SELECT id,studentNum,type,schoolNum,classNum,gradeNum,studentName,jie,studentId FROM student   WHERE schoolNum={schoolNum} ) stu   ON sc.studentId=stu.id   LEFT JOIN (SELECT studentId,examineeNum FROM examinationnum  WHERE examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum} and subjectNum = {subjectNum}) en   ON en.studentId=stu.id   LEFT JOIN (SELECT  id,schoolNum,schoolName,shortname  FROM school WHERE id={schoolNum}) sch \tON sc.schoolNum=sch.id   LEFT JOIN (SELECT  id,classNum,className,classType,schoolNum,gradeNum  FROM class WHERE id={classNum}) cla \tON  stu.classNum=cla.id    LEFT JOIN  " + illStr + " ilg    ON  sc.studentId=ilg.studentId   WHERE stu.studentId  IS NOT NULL    UNION ALL  SELECT stu.studentId realStudentId,stu.studentNum,res.examineeNum,stu.studentName,stu.schoolNum,sch.schoolName,stu.classNum,cla.className,0 totalScore,'0' type,NULL possessPage  ,'3' totalPage,stu.type studentType  FROM(SELECT sd.studentId,sd.examineeNum   FROM (SELECT studentId,examineeNum,schoolNum   FROM  examinationnum  WHERE examNum={examNum}  AND schoolNum={schoolNum} AND gradeNum={gradeNum} and subjectNum = {subjectNum} ) sd   LEFT JOIN (SELECT studentId FROM regexaminee WHERE examPaperNum={pexamPaperNum} AND schoolNum={schoolNum}  ) re ON sd.studentid = re.studentid WHERE re.studentId IS NULL  ) res  LEFT JOIN student stu ON res.studentId=stu.id   LEFT JOIN school sch ON stu.schoolNum=sch.id  LEFT JOIN class cla ON stu.classNum=cla.id   WHERE cla.id={classNum}  ORDER BY schoolNum ASC,classNum*1 ASC,type DESC,totalScore DESC,studentNum";
        this.log.info("sql-按班级-单科详情  科目 总分：" + sql);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("pexamPaperNum", pexamPaperNum);
        args.put("totalPage", totalPage);
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("FALSE", "F");
        return this.dao2._queryBeanList(sql, StudentsTotalScore.class, args);
    }

    public List getStudentsTotalScoreListByExaminationRoom(String examNum, String gradeNum, String subjectNum, String examRoomNum, String schoolNum, String examroomornot) {
        String str;
        String sql;
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        String examPaperNum = exampaperObj.getExamPaperNum().toString();
        String jie = exampaperObj.getJie();
        String totalPage = exampaperObj.getTotalPage();
        if (null != examRoomNum && !"".equals(examRoomNum) && !"-1".equals(examRoomNum)) {
            str = "AND examinationRoomNum={examRoomNum} ";
        } else {
            str = "AND schoolNum={schoolNum}  ";
        }
        String ill_sql = "(SELECT  a.studentId,a.schoolNum,GROUP_CONCAT(a.aa) aa,a.examPaperNum,a.examinationRoomNum,a.type  FROM (SELECT DISTINCT r.*  FROM (SELECT re.studentId,re.schoolNum,GROUP_CONCAT(re.page) aa,re.exampaperNum,re.examinationRoomNum ,ill.type   FROM(SELECT studentId,type,examPaperNum FROM illegal  WHERE examPaperNum={examPaperNum} " + str + ") ill  LEFT JOIN(SELECT examPaperNum,studentId,page,examinationRoomNum,schoolNum FROM regexaminee WHERE examPaperNum={examPaperNum}  " + str + ") re  ON ill.studentId=re.studentId  GROUP BY re.studentid   HAVING COUNT(page)<{totalPage}) r  UNION ALL  SELECT ill.studentId,st.schoolNum,NULL aa,ill.examPaperNum,ill.examinationRoomNum,ill.type  FROM(SELECT studentId,type,examPaperNum,examinationRoomNum FROM illegal  WHERE examPaperNum=''  " + str + ")ill  LEFT JOIN(SELECT id,studentId,studentNum,schoolNum FROM student WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}  ) st  ON ill.studentId=st.id  )a  GROUP BY a.studentId)";
        String sql2 = "SELECT  stu.studentId,stu.studentNum,stu.studentName,stu.schoolNum,sch.schoolName,sc.classNum,cla.className  ,er.id roomId,er.examinationRoomNum,er.examinationRoomName,sc.totalScore,ill.type,ill.aa possessPage,'" + totalPage + "' totalPage,stu.type studentType   FROM(SELECT b.studentId,b.schoolNum,b.gradeNum,b.classNum,ROUND(SUM(b.questionScore),2) totalScore,b.examPaperNum,b.examinationRoomNum  FROM(SELECT DISTINCT a.studentId,a.schoolNum,a.gradeNum,a.classNum,a.questionScore,a.examPaperNum,a.examinationRoomNum,a.page,a.questionNum  FROM (SELECT examPaperNum,studentId,schoolNum,gradeNum,classNum,questionScore,examinationRoomNum,page,questionNum   FROM score    WHERE examPaperNum={examPaperNum}  AND continued='F'  " + str + "UNION  ALL  SELECT examPaperNum,studentId,schoolNum,gradeNum,classNum,questionScore,examinationRoomNum,page,questionNum  FROM objectivescore  WHERE examPaperNum={examPaperNum}  " + str + ") a )b GROUP BY b.studentId  ) sc  LEFT JOIN (SELECT id,studentId,studentName,studentNum,classNum,gradeNum,schoolNum,type FROM student  WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}) stu ON sc.studentId=stu.id\t  LEFT JOIN (SELECT id,schoolNum,schoolName FROM school WHERE id={schoolNum} ) sch \tON stu.schoolNum=sch.id\t  LEFT JOIN (SELECT gradeNum,gradeName FROM basegrade WHERE gradeNum={gradeNum})gra ON stu.gradeNum=gra.gradeNum   LEFT JOIN (SELECT id,classNum,className FROM class WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie} ) cla ON stu.classNum=cla.id   LEFT JOIN (SELECT id,examinationRoomNum,examinationRoomName FROM examinationroom   WHERE ";
        if (null != examRoomNum && !"-1".equals(examRoomNum) && !"".equals(examRoomNum)) {
            sql = sql2 + "id={examRoomNum} ";
        } else {
            sql = sql2 + "examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum}  ";
        }
        String sql3 = sql + ")er ON sc.examinationRoomNum=er.id   LEFT JOIN " + ill_sql + " ill ON sc.studentId=ill.studentId   WHERE stu.studentName IS NOT NULL  ORDER BY  stu.schoolNum ASC,stu.gradeNum,er.examinationRoomNum ASC,sc.totalScore DESC";
        this.log.info("sql--按考场导出-总分查询：" + sql3);
        Map args = new HashMap();
        args.put("examRoomNum", examRoomNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examPaperNum", examPaperNum);
        args.put("totalPage", totalPage);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("jie", jie);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao2._queryBeanList(sql3, StudentsTotalScore.class, args);
    }

    public List getQuestionInfo(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, String examPaperNum) {
        String texamPaperNum = this.examDao.getExampaperNum(examNum, subjectNum, gradeNum);
        String pexamPaperNum = this.examDao.getpExampaperNum(examNum, subjectNum, gradeNum);
        this.log.info("sql-按班级-查询科目详情-:SELECT res.realStudentId,res.examPaperNum,GROUP_CONCAT(res.questionNum) questionNum,res.qtype,SUM(res.questionScore) questionScore,res.answer,GROUP_CONCAT(res.schoolNum) schoolNum,GROUP_CONCAT(res.classNum) classNum,res.choosename,GROUP_CONCAT(res.orderNum) orderNum,res.isParent,res.pid   FROM(SELECT st.studentId realStudentId,def.examPaperNum   ,def.questionNum,def.questionType qtype,IFNULL(sc.questionScore,'-1') questionScore,IFNULL(sc.answer,'') answer,sc.schoolNum,sc.classNum,IF(LENGTH(def.choosename)>4,'T',def.choosename) choosename,def.orderNum,def.isParent,IF(LENGTH(def.choosename)>4,def.choosename,def.pid) pid,def.id   FROM(SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer   ,choosename,orderNum,isParent,id pid  FROM define  WHERE (examPaperNum={texamPaperNum} or category = {texamPaperNum}) AND isParent='0'   UNION ALL   SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename,orderNum ,'0' isParent,pid   FROM subdefine   WHERE (examPaperNum={texamPaperNum} or category = {texamPaperNum}) ) def   LEFT JOIN (SELECT DISTINCT studentId FROM regexaminee WHERE examPaperNum={pexamPaperNum}  AND schoolNum={schoolNum}  ) r ON 1=1   LEFT JOIN  student st  ON st.id =r.studentId   left JOIN (SELECT studentId,examPaperNum,questionNum,MAX(questionScore)questionScore,'' answer,schoolNum,classNum    FROM score WHERE schoolNum={schoolNum}  AND classNum={classNum}  AND continued='F'   GROUP BY studentId,questionNum    UNION ALL   SELECT studentId,examPaperNum,questionNum,questionScore,answer,schoolNum,classNum    FROM objectivescore WHERE schoolNum={schoolNum}  AND classNum={classNum}  ) sc    ON  sc.questionNum=def.id AND sc.studentId=st.id    )res   GROUP BY res.realStudentId,res.id,res.pid   ORDER BY res.realStudentId,REPLACE(GROUP_CONCAT(res.questionNum),'_','.')*1   ,GROUP_CONCAT(res.orderNum)*1   ,CONVERT(GROUP_CONCAT(res.questionNum) USING gbk)  ");
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("texamPaperNum", texamPaperNum);
        args.put("pexamPaperNum", pexamPaperNum);
        return this.dao2._queryBeanList("SELECT res.realStudentId,res.examPaperNum,GROUP_CONCAT(res.questionNum) questionNum,res.qtype,SUM(res.questionScore) questionScore,res.answer,GROUP_CONCAT(res.schoolNum) schoolNum,GROUP_CONCAT(res.classNum) classNum,res.choosename,GROUP_CONCAT(res.orderNum) orderNum,res.isParent,res.pid   FROM(SELECT st.studentId realStudentId,def.examPaperNum   ,def.questionNum,def.questionType qtype,IFNULL(sc.questionScore,'-1') questionScore,IFNULL(sc.answer,'') answer,sc.schoolNum,sc.classNum,IF(LENGTH(def.choosename)>4,'T',def.choosename) choosename,def.orderNum,def.isParent,IF(LENGTH(def.choosename)>4,def.choosename,def.pid) pid,def.id   FROM(SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer   ,choosename,orderNum,isParent,id pid  FROM define  WHERE (examPaperNum={texamPaperNum} or category = {texamPaperNum}) AND isParent='0'   UNION ALL   SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename,orderNum ,'0' isParent,pid   FROM subdefine   WHERE (examPaperNum={texamPaperNum} or category = {texamPaperNum}) ) def   LEFT JOIN (SELECT DISTINCT studentId FROM regexaminee WHERE examPaperNum={pexamPaperNum}  AND schoolNum={schoolNum}  ) r ON 1=1   LEFT JOIN  student st  ON st.id =r.studentId   left JOIN (SELECT studentId,examPaperNum,questionNum,MAX(questionScore)questionScore,'' answer,schoolNum,classNum    FROM score WHERE schoolNum={schoolNum}  AND classNum={classNum}  AND continued='F'   GROUP BY studentId,questionNum    UNION ALL   SELECT studentId,examPaperNum,questionNum,questionScore,answer,schoolNum,classNum    FROM objectivescore WHERE schoolNum={schoolNum}  AND classNum={classNum}  ) sc    ON  sc.questionNum=def.id AND sc.studentId=st.id    )res   GROUP BY res.realStudentId,res.id,res.pid   ORDER BY res.realStudentId,REPLACE(GROUP_CONCAT(res.questionNum),'_','.')*1   ,GROUP_CONCAT(res.orderNum)*1   ,CONVERT(GROUP_CONCAT(res.questionNum) USING gbk)  ", Score.class, args);
    }

    public List getQuestionInfo_levelcla(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, String examPaperNum) {
        String texamPaperNum = this.examDao.getExampaperNum(examNum, subjectNum, gradeNum);
        String pexamPaperNum = this.examDao.getpExampaperNum(examNum, subjectNum, gradeNum);
        Map args = StreamMap.create().put("texamPaperNum", (Object) texamPaperNum).put("pexamPaperNum", (Object) pexamPaperNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("FALSE", (Object) "F");
        this.log.info("sql-按班级-查询科目详情-:SELECT DISTINCT st.studentId realStudentId,def.examPaperNum,def.questionNum,def.questionType qtype,IFNULL(sc.questionScore,'0') questionScore,IFNULL(sc.answer,'') answer,sc.schoolNum,sc.classNum  FROM(SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename  FROM define  WHERE (examPaperNum={texamPaperNum} or category = {texamPaperNum} ) AND isParent='0'  AND choosename<>'T'  UNION   SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename  FROM subdefine   WHERE (examPaperNum={texamPaperNum} or category = {texamPaperNum})  ORDER BY REPLACE(questionNum,'_','.')*1   ) def    LEFT JOIN (SELECT DISTINCT studentId FROM regexaminee WHERE examPaperNum={pexamPaperNum}  AND schoolNum={schoolNum}  ) r ON 1=1   LEFT JOIN  (SELECT stu.id,stu.studentId   FROM levelstudent levstu   LEFT JOIN student stu  ON stu.gradeNum={gradeNum} AND stu.schoolNum={schoolNum} AND stu.id=levstu.sid   WHERE  levstu.subjectNum={subjectNum} AND levstu.classNum={classNum} ) st  ON st.id =r.studentId   left JOIN (SELECT studentId,examPaperNum,questionNum,MAX(questionScore)questionScore,'' answer,schoolNum,classNum    FROM score WHERE schoolNum={schoolNum}  AND continued={FALSE}   GROUP BY studentId,questionNum    UNION ALL   SELECT studentId,examPaperNum,questionNum,questionScore,answer,schoolNum,classNum    FROM objectivescore WHERE schoolNum={schoolNum}  ) sc    ON  sc.questionNum=def.id AND sc.studentId=st.id    WHERE st.studentId  IS NOT NULL    ORDER BY st.id,REPLACE(def.questionNum,'_','.')*1 ASC");
        return this.dao2._queryBeanList("SELECT DISTINCT st.studentId realStudentId,def.examPaperNum,def.questionNum,def.questionType qtype,IFNULL(sc.questionScore,'0') questionScore,IFNULL(sc.answer,'') answer,sc.schoolNum,sc.classNum  FROM(SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename  FROM define  WHERE (examPaperNum={texamPaperNum} or category = {texamPaperNum} ) AND isParent='0'  AND choosename<>'T'  UNION   SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename  FROM subdefine   WHERE (examPaperNum={texamPaperNum} or category = {texamPaperNum})  ORDER BY REPLACE(questionNum,'_','.')*1   ) def    LEFT JOIN (SELECT DISTINCT studentId FROM regexaminee WHERE examPaperNum={pexamPaperNum}  AND schoolNum={schoolNum}  ) r ON 1=1   LEFT JOIN  (SELECT stu.id,stu.studentId   FROM levelstudent levstu   LEFT JOIN student stu  ON stu.gradeNum={gradeNum} AND stu.schoolNum={schoolNum} AND stu.id=levstu.sid   WHERE  levstu.subjectNum={subjectNum} AND levstu.classNum={classNum} ) st  ON st.id =r.studentId   left JOIN (SELECT studentId,examPaperNum,questionNum,MAX(questionScore)questionScore,'' answer,schoolNum,classNum    FROM score WHERE schoolNum={schoolNum}  AND continued={FALSE}   GROUP BY studentId,questionNum    UNION ALL   SELECT studentId,examPaperNum,questionNum,questionScore,answer,schoolNum,classNum    FROM objectivescore WHERE schoolNum={schoolNum}  ) sc    ON  sc.questionNum=def.id AND sc.studentId=st.id    WHERE st.studentId  IS NOT NULL    ORDER BY st.id,REPLACE(def.questionNum,'_','.')*1 ASC", Score.class, args);
    }

    public List getQuestionInfoByExaminationRoom(String examNum, String gradeNum, String subjectNum, String examRoomNum, String schoolNum, String examroomornot, String examPaperNum) {
        String str = "";
        if (null != examRoomNum && !"-1".equals(examRoomNum) && !"".equals(examRoomNum)) {
            str = "AND examinationRoomNum={examRoomNum}  ";
        } else if (null != schoolNum && !"-1".equals(schoolNum) && !"".equals(schoolNum)) {
            str = "AND schoolNum={schoolNum}   ";
        }
        String sql = "SELECT DISTINCT st.studentId,c.examPaperNum,c.id questionNum,c.questionType qtype,IFNULL(sc.questionScore,'0') questionScore,IFNULL(sc.answer,'') answer,sc.schoolNum,sc.classNum  FROM(SELECT examPaperNum,questionNum,questionType,id  FROM define  WHERE examPaperNum={examPaperNum} AND isParent='0'  UNION    SELECT def.examPaperNum,subdef.questionNum,def.questionType,subdef.id  FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE def.examPaperNum={examPaperNum} AND def.isParent='1'  ) c\tLEFT JOIN (SELECT DISTINCT studentId FROM regexaminee WHERE examPaperNum={examPaperNum}   " + str + ")r ON 1=1  LEFT JOIN(SELECT id,studentId FROM student WHERE gradeNum={gradeNum}  ";
        if (null != schoolNum && !"-1".equals(schoolNum) && !"".equals(schoolNum)) {
            sql = sql + "AND schoolNum={schoolNum}   ";
        }
        String sql2 = sql + ") st  ON st.id =r.studentId  left JOIN(SELECT studentId,examPaperNum,questionNum,MIN(page)page,MAX(questionScore)questionScore,''answer,schoolNum,classNum   FROM score WHERE examPaperNum={examPaperNum}  AND continued='F'   " + str + "GROUP BY studentId,questionNum   UNION ALL  SELECT studentId,examPaperNum,questionNum,page,questionScore,answer,schoolNum,classNum   FROM objectivescore WHERE examPaperNum={examPaperNum}  " + str + ")sc ON sc.questionNum=c.id AND sc.studentId=st.id WHERE st.id IS NOT NULL  ORDER BY st.studentId,REPLACE(c.questionNum,'_','.')*1 ASC";
        this.log.info("---sql--按考场导出 科目题目详细：" + sql2);
        Map args = StreamMap.create().put("examRoomNum", (Object) examRoomNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("examPaperNum", (Object) examPaperNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryBeanList(sql2, Score.class, args);
    }

    public void clearStudentLevel() {
        new ArrayList();
        this.dao2.execute("delete from studentlevel");
    }

    public Integer getCreateTableData(String examNum, String subjectNum, String schoolNum, String gradeNum) {
        String sql2;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        int count = 0;
        if (null != subjectNum && !"-1".equals(subjectNum) && !"".equals(subjectNum)) {
            sql2 = "SELECT * from exampaper WHERE examNum={examNum} AND subjectNum={subjectNum} and gradeNum={gradeNum} ";
        } else {
            String exampapernums = this.dao2._queryStr("SELECT  GROUP_CONCAT(DISTINCT exampaperNum separator ',') FROM score WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} ", args);
            args.put("exampapernums", exampapernums);
            sql2 = "SELECT *  FROM exampaper  WHERE examPaperNum in ({exampapernums[]}) AND examNum={examNum} AND gradeNum={gradeNum} ";
        }
        this.log.info("--sql-插入中间表时，查询试卷科目的-" + sql2);
        if (null != this.dao2._queryBeanList(sql2, Exampaper.class, args)) {
            List<?> _queryBeanList = this.dao2._queryBeanList(sql2, Exampaper.class, args);
            for (int i = 0; i < _queryBeanList.size(); i++) {
                String exampapernu = "ExamPaperNum" + i;
                args.put(exampapernu, ((Exampaper) _queryBeanList.get(i)).getExamPaperNum());
                String sql = "INSERT INTO studentlevel (examNum,schoolNum,gradeNum,subjectNum,classNum,totalScore,sqts,oqts,studentId,insertUser,insertDate,description,isDelete,ext1,ext2,ext3,examPaperNum,examinationRoomNum) SELECT '" + ((Exampaper) _queryBeanList.get(i)).getExamNum() + "',stu.schoolNum,a.gradeNum,'" + ((Exampaper) _queryBeanList.get(i)).getSubjectNum() + "',stu.classNum ,ROUND(SUM(IF(a.qtype='0',0,questionScore)),2)+ROUND(SUM(IF(a.qtype='1',0,questionScore)),2) totalScore,ROUND(SUM(IF(a.qtype='0',0,questionScore)),2) sqts,ROUND(SUM(IF(a.qtype='1',0,questionScore)),2) oqts,a.studentId,a.insertUser,a.insertDate,NULL,'F',NULL,NULL,NULL,a.examPaperNum,a.examinationRoomNum  FROM (select questionScore,studentId,answer,qtype ,questionNum ,gradeNum,examPaperNum,examinationRoomNum,insertUser,insertDate  FROM score  where schoolNum={schoolNum} AND gradeNum={gradeNum} AND examPaperNum={" + exampapernu + "} ) a  LEFT JOIN student stu  ON a.studentId=stu.studentId  WHERE a.studentId=stu.studentId  GROUP BY stu.studentId";
                this.log.info("-sql---getCreateTableData--" + sql);
                count += this.dao2._execute(sql, args);
            }
        }
        return Integer.valueOf(count);
    }

    public List getCTDExamPapeerCount(String schoolNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        List schoolnums = this.dao2._queryColList("select GROUP_CONCAT(gradeNum separator ',') from grade where schoolNum={schoolNum} ", args);
        StringBuffer schoolNumstr = new StringBuffer();
        for (int i = 0; i < schoolnums.size(); i++) {
            if (i < schoolnums.size() - 1) {
                schoolNumstr.append(schoolnums.get(i) + Const.STRING_SEPERATOR);
            } else {
                schoolNumstr.append(schoolnums.get(i));
            }
        }
        args.put("schoolNumstr", schoolNumstr.toString());
        this.log.info("select DISTINCT ep.examPaperNum  from define def,exampaper ep,score sc,student stu  where ep.examNum in (select examNum from exam )  and ep.gradeNum in ({schoolNumstr[]}) and ep.subjectNum in (select subjectNum from `subject`)  and ep.examPaperNum=sc.examPaperNum  and sc.studentId in ({schoolNumstr[]}) and sc.examPaperNum=def.examPaperNum and sc.questionNum=def.questionNum  and sc.studentId=stu.studentId  ORDER BY ep.examPaperNum asc  -sql---getCTDExamPapeerCount---");
        return this.dao2._queryBeanList("select DISTINCT ep.examPaperNum  from define def,exampaper ep,score sc,student stu  where ep.examNum in (select examNum from exam )  and ep.gradeNum in ({schoolNumstr[]}) and ep.subjectNum in (select subjectNum from `subject`)  and ep.examPaperNum=sc.examPaperNum  and sc.studentId in ({schoolNumstr[]}) and sc.examPaperNum=def.examPaperNum and sc.questionNum=def.questionNum  and sc.studentId=stu.studentId  ORDER BY ep.examPaperNum asc  ", Score.class, args);
    }

    public List getCTDStudentCount(String schoolNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        List schoolnums = this.dao2._queryColList("select GROUP_CONCAT(gradeNum separator ',') from grade where schoolNum={schoolNum}", args);
        StringBuffer schoolNumstr = new StringBuffer();
        for (int i = 0; i < schoolnums.size(); i++) {
            if (i < schoolnums.size() - 1) {
                schoolNumstr.append(schoolnums.get(i) + Const.STRING_SEPERATOR);
            } else {
                schoolNumstr.append(schoolnums.get(i));
            }
        }
        args.put("schoolNumstr", schoolNumstr.toString());
        this.log.info("select DISTINCT sc.studentId  from define def,exampaper ep,score sc,student stu  where ep.examNum in (select examNum from exam )  and ep.gradeNum in ({schoolNumstr[]}) and ep.subjectNum in (select subjectNum from `subject`)  and ep.examPaperNum=sc.examPaperNum  and sc.studentId in ({schoolNumstr[]}) and sc.examPaperNum=def.examPaperNum and sc.questionNum=def.questionNum  and sc.studentId=stu.studentId  ORDER BY sc.studentId asc  -sql---getCTDStudentCount---");
        return this.dao2._queryBeanList("select DISTINCT sc.studentId  from define def,exampaper ep,score sc,student stu  where ep.examNum in (select examNum from exam )  and ep.gradeNum in ({schoolNumstr[]}) and ep.subjectNum in (select subjectNum from `subject`)  and ep.examPaperNum=sc.examPaperNum  and sc.studentId in ({schoolNumstr[]}) and sc.examPaperNum=def.examPaperNum and sc.questionNum=def.questionNum  and sc.studentId=stu.studentId  ORDER BY sc.studentId asc  ", Score.class, args);
    }

    public List getSingleSubjectData_levcla(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, String type, String source, String isJointStuType, String subCompose) {
        String fufenFlag;
        Map args = new HashMap();
        args.put("subCompose", subCompose);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("type", type);
        args.put("source", source);
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        String examPaperNum = exampaperObj.getExamPaperNum() + "";
        args.put("examPaperNum", examPaperNum);
        String jie = exampaperObj.getJie();
        args.put("jie", jie);
        String totalPage = exampaperObj.getTotalPage();
        args.put("totalPage", totalPage);
        Object subCPid = this.dao2._queryObject("SELECT pid from subjectcombine where subjectCombineNum={subCompose} ", args);
        if (null == this.dao2._queryObject("select id from jisuanzhonglei where gradeNum={gradeNum} and subjectNum={subjectNum} and jisuanzhonglei='1' limit 1", args)) {
            fufenFlag = ",'hideFufen' ext5,stul.classRanking,stul.gradeRanking,stul.areaRanking,IFNULL(stul.dengji,'') dengji,IFNULL(stul.dengjixiao,'') dengjixiao ";
        } else {
            fufenFlag = ",round(stul_fufen.totalScore,2) ext5,stul_fufen.classRanking,stul_fufen.gradeRanking,stul_fufen.areaRanking,IFNULL(stul_fufen.dengji,'') dengji,IFNULL(stul_fufen.dengjixiao,'') dengjixiao ";
        }
        String illStr = "(SELECT  a.studentId,GROUP_CONCAT(a.examineeNum) examineeNum,a.schoolNum,a.gradeNum   ,GROUP_CONCAT(a.aa) aa,a.examPaperNum,a.examinationRoomNum,a.type    FROM (SELECT DISTINCT r.*  FROM (SELECT sd.studentId,sd.gradeNum,sd.examNum,sd.examineeNum,GROUP_CONCAT(re.page) aa,re.exampaperNum,sd.examinationRoomNum,ill.type,sd.schoolNum   FROM (SELECT schoolNum,gradeNum,studentId,examNum,examineeNum,examinationRoomNum FROM examinationnum   WHERE examNum={examNum}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum} and subjectNum = {subjectNum} ) sd   LEFT JOIN (SELECT id,page,examPaperNum,examinationRoomNum,studentId FROM regexaminee   WHERE examPaperNum={examPaperNum} AND schoolNum={schoolNum}) re ON sd.studentid = re.studentid    LEFT JOIN (SELECT type,examPaperNum,studentId,examinationRoomNum FROM illegal   WHERE examPaperNum={examPaperNum}   AND schoolNum={schoolNum}  ) ill ON  ill.studentId=sd.studentId   GROUP BY sd.studentid   HAVING COUNT(page)  <{totalPage}) r    UNION ALL  SELECT studentId,'" + gradeNum + "' gradeNum,'" + examNum + "' examNum,NULL examineeNum,NULL aa,exampaperNum,examinationRoomNum,type,schoolNum   FROM  illegal WHERE examPaperNum={examPaperNum}   AND schoolNum={schoolNum}   ) a    GROUP BY a.studentId)";
        String classStr = "";
        String classStr1 = "";
        String classStr2 = "";
        if (null != classNum && !"".equals(classNum)) {
            classStr = "AND classNum={classNum} ";
            classStr1 = " and levcla.id={classNum} ";
            classStr2 = " and levstu.classNum={classNum}  ";
        }
        String sourceStr = "";
        if (!"0".equals(source)) {
            sourceStr = " AND source={source}";
        }
        String sql = "select DISTINCT  stu.schoolNum,sch.schoolName,stu.gradeNum,g.gradeName,stu.classNum,c.className,stul.examPaperNum,stul.studentId,stu.studentId  realStudentId,stu.studentName studentName,stu.studentNum ,en.examineeNum,stu.sourceName source,IF(stu.isjoin=0,'统计','不统计') isJoin,stul.statisticType,stul.studentType ext1,round(stul.sqts,2) sqts,round(stul.oqts,2) oqts,round(stul.totalScore,2) totalScore ,sub.subjectNum,IFNULL(sub.subjectName,'') subjectName,ilg.type,ilg.aa possessPage,'" + totalPage + "' totalPage,stu.type studentType,levcla.classNum levelClassNum,levcla.className levelClassName,stu.subjectCombineNum ext2,stu.subjectCombineName ext3,d.name ext4,IFNULL(upr.teacherName,'') ext6 " + fufenFlag + "FROM (SELECT  examPaperNum,studentId,classRanking,gradeRanking,areaRanking,statisticType,studentType,sqts,oqts,classNum,gradeNum,schoolNum,totalScore,dengji,dengjixiao    FROM studentlevel   WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} " + classStr + " AND examPaperNum={examPaperNum} AND statisticType={type}   AND source={source} and xuankezuhe = {subCompose}   union all select examPaperNum,studentId,classRanking,gradeRanking,areaRanking,statisticType,studentType,sqts,oqts,classNum,gradeNum,schoolNum,totalScore,dengji,''dengjixiao  from studentlevel_butongji s inner join   (select es.source from examsourceset es left join (select * from data where type=26 and value<>0) d on es.source=d.value where es.examNum={examNum} and es.isJoin=1) da on s.source=da.source where schoolNum={schoolNum} AND gradeNum={gradeNum}  " + classStr + " AND examPaperNum={examPaperNum} AND statisticType={type}  " + sourceStr + " and xuankezuhe = {subCompose} ) stul   LEFT JOIN (SELECT  examPaperNum,studentId,classRanking,gradeRanking,areaRanking,statisticType,studentType,sqts,oqts,classNum,gradeNum,schoolNum,totalScore,dengji,dengjixiao    FROM studentlevel_fufen   WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} " + classStr + " AND examPaperNum={examPaperNum} AND statisticType={type}   AND source={source} and xuankezuhe = {subCompose}    UNION ALLselect examPaperNum,studentId,classRanking_fufen,gradeRanking_fufen,areaRanking_fufen,statisticType,studentType,sqts,oqts,classNum,gradeNum,schoolNum,totalScore_fufen,dengji,''dengjixiao from studentlevel_butongji s inner join  (select es.source from examsourceset es left join (select * from data where type=26 and value<>0) d on es.source=d.value where es.examNum={examNum} and es.isJoin=1) da on s.source=da.source where schoolNum={schoolNum} AND gradeNum={gradeNum}  " + classStr + " AND examPaperNum={examPaperNum} AND statisticType={type}  " + sourceStr + " AND xuankezuhe = {subCompose}  ) stul_fufen on stul_fufen.studentId=stul.studentId   LEFT JOIN (SELECT examNum,subjectNum,gradeNum,examPaperNum FROM exampaper  WHERE examPaperNum={examPaperNum}) ep ON stul.examPaperNum=ep.examPaperNum    LEFT JOIN (SELECT subjectNum,subjectName,id,subjectType FROM `subject` WHERE subjectNum={subjectNum}) sub ON ep.subjectNum=sub.subjectNum   LEFT JOIN (SELECT gradeNum,gradeName FROM basegrade WHERE gradeNum={gradeNum}) g ON stul.gradeNum=g.gradeNum  LEFT JOIN (SELECT id,schoolNum,schoolName,schoolType FROM school WHERE id={schoolNum}) sch ON stul.schoolNum=sch.id   LEFT JOIN (SELECT s.id,s.schoolNum,s.gradeNum,s.classNum,s.jie,s.studentName,s.studentNum,s.type,s.studentId,s.subjectCombineNum,subc.subjectCombineName,s.source,da.name sourceName,da.isJoin FROM student s  left join subjectcombine subc on subc.subjectCombineNum=s.subjectCombineNum  left join (select examNum,e.source,es.value,es.name,e.isJoin from examsourceset e left join (select name,value from data where type=26)es on e.source=es.value where examNum={examNum}) da on s.source=da.value WHERE s.schoolNum={schoolNum}  AND s.gradeNum={gradeNum}    ";
        if (null != source && !"0".equals(source)) {
            sql = sql + "AND s.source='" + source + "'   ";
        }
        if (null != subCompose && !"0".equals(subCompose)) {
            if (null == subCPid) {
                sql = sql + " and subc.pid={subCompose} ";
            } else {
                sql = sql + " and s.subjectCombineNum={subCompose} ";
            }
        }
        String sql2 = sql + ") stu ON stul.studentId=stu.id    LEFT JOIN data d on d.type = '22' and d.value = stu.type LEFT JOIN (SELECT levstu.id,levstu.subjectNum,levstu.sid studentId,levstu.classNum levclaId,levcla.classNum,levcla.className   FROM levelstudent levstu  LEFT JOIN levelclass levcla  ON 1=1 " + classStr1 + " and levcla.id={classNum} AND levstu.classNum=levcla.id  AND levcla.subjectNum={subjectNum}  WHERE 1=1 " + classStr2 + " AND levstu.schoolNum={schoolNum}   AND levstu.gradeNum={gradeNum} AND levstu.subjectNum={subjectNum}  )levcla   ON levcla.studentId=stu.id  LEFT JOIN (SELECT studentId,examineeNum FROM examinationnum  WHERE examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum} and subjectNum = {subjectNum} ) en   ON en.studentId=stu.id   LEFT JOIN (SELECT id,classNum,className,classType FROM class WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}) c ON stu.classNum=c.id   LEFT JOIN    " + illStr + "ilg    ON stul.studentId=ilg.studentId   LEFT JOIN (select classNum,subjectNum,group_concat(IFNULL(userName,'--')) teacherName from userposition_record where examNum={examNum} and gradeNum={gradeNum} and schoolnum={schoolNum} " + classStr + " and type='1' group by classNum,subjectNum) upr on upr.classNum=levcla.levclaId and upr.subjectNum=levcla.subjectNum WHERE stu.studentName IS NOT NULL AND levcla.studentId IS NOT NULL  ORDER BY  if(isnull(stul.studentType),1,0),stul.studentType,stul.classNum ASC,totalScore DESC";
        this.log.info("sql-班级--科目主观题、客观题 导出 sql--" + sql2);
        return this.dao2._queryBeanList(sql2, Studentlevel.class, args);
    }

    public List<Studentlevel> getSingleSubjectData_levcla_fufen(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, String type, String source, String isJointStuType, String subCompose) {
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        String examPaperNum = exampaperObj.getExamPaperNum() + "";
        String jie = exampaperObj.getJie();
        String totalPage = exampaperObj.getTotalPage();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("examPaperNum", examPaperNum);
        args.put("totalPage", totalPage);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("type", type);
        args.put("source", source);
        args.put("subCompose", subCompose);
        args.put("jie", jie);
        String illStr = "(SELECT  a.studentId,GROUP_CONCAT(a.examineeNum) examineeNum,a.schoolNum,a.gradeNum   ,GROUP_CONCAT(a.aa) aa,a.examPaperNum,a.examinationRoomNum,a.type    FROM (SELECT DISTINCT r.*  FROM (SELECT sd.studentId,sd.gradeNum,sd.examNum,sd.examineeNum,GROUP_CONCAT(re.page) aa,re.exampaperNum,sd.examinationRoomNum,ill.type,sd.schoolNum   FROM (SELECT schoolNum,gradeNum,studentId,examNum,examineeNum,examinationRoomNum FROM examinationnum   WHERE examNum={examNum}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum} and subjectNum = {subjectNum} ) sd   LEFT JOIN (SELECT id,page,examPaperNum,examinationRoomNum,studentId FROM regexaminee   WHERE examPaperNum={examPaperNum} AND schoolNum={schoolNum}) re ON sd.studentid = re.studentid    LEFT JOIN (SELECT type,examPaperNum,studentId,examinationRoomNum FROM illegal   WHERE examPaperNum={examPaperNum}   AND schoolNum={schoolNum}  ) ill ON  ill.studentId=sd.studentId   GROUP BY sd.studentid   HAVING COUNT(page)  <{totalPage}) r    UNION ALL  SELECT studentId,'" + gradeNum + "' gradeNum,{examNum} examNum,NULL examineeNum,NULL aa,exampaperNum,examinationRoomNum,type,schoolNum   FROM  illegal WHERE examPaperNum={examPaperNum}   AND schoolNum={schoolNum}   ) a    GROUP BY a.studentId)";
        String stunameStr = "stu.studentName studentName,";
        String dataStr = "";
        if ("1".equals(isJointStuType)) {
            stunameStr = "IF(stu.type='0',stu.studentName,CONCAT(stu.studentName,d.name)) studentName,";
            dataStr = " LEFT JOIN data d on d.type = '22' and d.value = stu.type ";
        }
        String classStr = "";
        String classStr1 = "";
        String classStr2 = "";
        if (null != classNum && !"".equals(classNum)) {
            classStr = "AND classNum={classNum} ";
            classStr1 = " and levcla.id={classNum} ";
            classStr2 = " levstu.classNum={classNum}  ";
        }
        String sql = "select DISTINCT  stu.schoolNum,sch.schoolName,stu.gradeNum,g.gradeName,stu.classNum,c.className,stul.examPaperNum,stul.studentId,stu.studentId  realStudentId," + stunameStr + "stu.studentNum ,en.examineeNum,stul.classRanking,stul.gradeRanking,stul.statisticType,stul.studentType,round(stul.sqts,2) sqts,round(stul.oqts,2) oqts,round(stul.totalScore,2) totalScore ,sub.subjectNum,sub.subjectName,ilg.type,ilg.aa possessPage,'" + totalPage + "' totalPage,stu.type studentType,levcla.classNum levelClassNum,levcla.className levelClassName    FROM (SELECT  examPaperNum,studentId,classRanking,gradeRanking,statisticType,studentType,sqts,oqts,classNum,gradeNum,schoolNum,totalScore    FROM studentlevel_fufen   WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} " + classStr + " AND examPaperNum={examPaperNum} AND statisticType={type}   AND source={source} and xuankezuhe = {subCompose}  ) stul   LEFT JOIN (SELECT examNum,subjectNum,gradeNum,examPaperNum FROM exampaper  WHERE examPaperNum={examPaperNum}) ep ON stul.examPaperNum=ep.examPaperNum    LEFT JOIN (SELECT subjectNum,subjectName,id,subjectType FROM `subject` WHERE subjectNum={subjectNum}) sub ON ep.subjectNum=sub.subjectNum   LEFT JOIN (SELECT gradeNum,gradeName FROM basegrade WHERE gradeNum={gradeNum}) g ON stul.gradeNum=g.gradeNum  LEFT JOIN (SELECT id,schoolNum,schoolName,schoolType FROM school WHERE id={schoolNum}) sch ON stul.schoolNum=sch.id   LEFT JOIN (SELECT id,schoolNum,gradeNum,classNum,jie,studentName,studentNum,type,studentId    FROM student WHERE schoolNum={schoolNum}  AND gradeNum={gradeNum}    ";
        if (null != source && !"0".equals(source)) {
            sql = sql + "AND source={source}   ";
        }
        if (null != subCompose && !"0".equals(subCompose)) {
            sql = sql + "AND subjectCombineNum={subCompose}   ";
        }
        String sql2 = sql + ") stu ON stul.studentId=stu.id   " + dataStr + "LEFT JOIN (SELECT levstu.id,levstu.subjectNum,levstu.sid studentId,levstu.classNum levclaId,levcla.classNum,levcla.className   FROM levelstudent levstu  LEFT JOIN levelclass levcla  ON 1=1 " + classStr1 + " AND levstu.classNum=levcla.id  AND levcla.subjectNum={subjectNum}  WHERE 1=1 " + classStr2 + " AND levstu.schoolNum={schoolNum}   AND levstu.gradeNum={gradeNum} AND levstu.subjectNum={subjectNum}  )levcla   ON levcla.studentId=stu.id  LEFT JOIN (SELECT studentId,examineeNum FROM examinationnum  WHERE examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum} and subjectNum = {subjectNum} ) en   ON en.studentId=stu.id   LEFT JOIN (SELECT id,classNum,className,classType FROM class WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}) c ON stu.classNum=c.id   LEFT JOIN    " + illStr + "ilg    ON stul.studentId=ilg.studentId   WHERE stu.studentName IS NOT NULL AND levcla.studentId IS NOT NULL  ORDER BY  if(isnull(stul.studentType),1,0),stul.studentType,stul.classNum ASC,totalScore DESC";
        this.log.info("sql-班级--科目主观题、客观题 导出 sql--赋分--" + sql2);
        return this.dao2._queryBeanList(sql2, Studentlevel.class, args);
    }

    public List getSingleSubjectData(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, String type, String source, String sexamPaperNum, String sSjt, boolean flg, String isJointStuType, String subCompose) {
        String fufenFlag;
        String sql;
        String sql2;
        String sql3;
        String sql4;
        String sql5;
        String sql6;
        Map args = new HashMap();
        args.put("subCompose", subCompose);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("sexamPaperNum", sexamPaperNum);
        args.put("type", type);
        args.put("source", source);
        args.put("sSjt", sSjt);
        Exampaper exampaperObj = this.examDao.getExampaperInfo1(examNum, subjectNum, gradeNum);
        exampaperObj.getJie();
        String totalPage = exampaperObj.getTotalPage();
        String examPaperNum = exampaperObj.getExamPaperNum() + "";
        String c_subjectNum = exampaperObj.getSubjectNum() + "";
        String p_examPaperNum = exampaperObj.getPexamPaperNum() + "";
        args.put("c_subjectNum", c_subjectNum);
        args.put("p_examPaperNum", p_examPaperNum);
        args.put("totalPage", totalPage);
        args.put("examPaperNum", examPaperNum);
        Object subCPid = this.dao2._queryObject("SELECT pid from subjectcombine where subjectCombineNum={subCompose} ", args);
        if (this.dao2._queryInt("select count(1) from arealevel_fufen where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args).intValue() == 0) {
            fufenFlag = ",'hideFufen' ext5,stul.classRanking,stul.gradeRanking,stul.areaRanking,IFNULL(stul.dengji,'') dengji,IFNULL(stul.dengjixiao,'') dengjixiao ";
        } else {
            fufenFlag = ",round(stul_fufen.totalScore,2) ext5,stul_fufen.classRanking,stul_fufen.gradeRanking,stul_fufen.areaRanking,IFNULL(stul_fufen.dengji,'') dengji,IFNULL(stul_fufen.dengjixiao,'') dengjixiao ";
        }
        String illStr = "(SELECT  a.studentId,GROUP_CONCAT(a.examineeNum) examineeNum,a.schoolNum,a.gradeNum   ,GROUP_CONCAT(a.aa) aa,a.examPaperNum,a.examinationRoomNum,a.type    FROM (SELECT DISTINCT r.*  FROM (SELECT sd.studentId,sd.gradeNum,sd.examNum,sd.examineeNum,GROUP_CONCAT(re.page) aa,re.exampaperNum,sd.examinationRoomNum,ill.type,sd.schoolNum   FROM (SELECT schoolNum,gradeNum,studentId,examNum,examineeNum,examinationRoomNum FROM examinationnum   WHERE examNum={examNum}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum} and subjectNum = {c_subjectNum} ) sd   LEFT JOIN (SELECT id,page,examPaperNum,examinationRoomNum,studentId FROM regexaminee   WHERE examPaperNum={p_examPaperNum} AND schoolNum={schoolNum}) re ON sd.studentid = re.studentid    LEFT JOIN (SELECT type,examPaperNum,studentId,examinationRoomNum FROM illegal   WHERE examPaperNum={p_examPaperNum}   AND schoolNum={schoolNum}  ) ill ON  ill.studentId=sd.studentId   GROUP BY sd.studentid   HAVING COUNT(page)  <{totalPage}) r    UNION ALL  SELECT studentId,'" + gradeNum + "' gradeNum,'" + examNum + "' examNum,NULL examineeNum,NULL aa,exampaperNum,examinationRoomNum,type,schoolNum   FROM  illegal WHERE examPaperNum={p_examPaperNum}   AND schoolNum={schoolNum}   ) a    GROUP BY a.studentId)";
        String classStr = "";
        if (null != classNum && !"".equals(classNum)) {
            classStr = "AND classNum={classNum} ";
        }
        String sql7 = "select DISTINCT  stu.schoolNum,sch.schoolName,stu.gradeNum,g.gradeName,stu.classNum,c.className,stul.examPaperNum,en.studentId,stu.studentId  realStudentId,stu.studentName studentName,stu.studentNum ,en.examineeNum,stu.sourceName source,IF(stu.isjoin=0,'统计','不统计') isJoin,stul.statisticType,stul.studentType ext1,round(stul.sqts,2) sqts,round(stul.oqts,2) oqts,round(stul.totalScore,2) totalScore ,sub.subjectNum,IFNULL(sub.subjectName,'') subjectName,ilg.type,ilg.aa possessPage,'" + totalPage + "' totalPage,stu.type studentType,'' levelClassNum,'' levelClassName,stu.subjectCombineNum ext2,stu.subjectCombineName ext3,d.name ext4,IFNULL(upr.teacherName,'') ext6 " + fufenFlag + "FROM (SELECT studentId,examineeNum,gradeNum,schoolNum,classNum,subjectNum FROM examinationnum  WHERE examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum} and subjectNum = {c_subjectNum} " + classStr + ") en LEFT JOIN (SELECT  examPaperNum,studentId,classRanking,gradeRanking,areaRanking,statisticType,studentType,sqts,oqts,classNum,gradeNum,schoolNum,totalScore,dengji,dengjixiao    FROM studentlevel   WHERE schoolNum={schoolNum} AND gradeNum={gradeNum}  " + classStr;
        if (flg) {
            sql = sql7 + "AND examPaperNum={sexamPaperNum}    ";
        } else {
            sql = sql7 + "AND examPaperNum={examPaperNum}    ";
        }
        String sql8 = (sql + "AND statisticType={type}   AND source={source} and xuankezuhe = {subCompose}   ") + " union all select examPaperNum,studentId,classRanking,gradeRanking,areaRanking,statisticType,studentType,sqts,oqts,classNum,gradeNum,schoolNum,totalScore,dengji,''dengjixiao from studentlevel_butongji s inner join (select es.source from examsourceset es left join (select * from data where type=26 and value<>0) d on es.source=d.value where es.examNum={examNum} and es.isJoin=1) da on s.source=da.source where schoolNum ={schoolNum} AND gradeNum={gradeNum}  " + classStr;
        if (flg) {
            sql2 = sql8 + "AND examPaperNum={sexamPaperNum}    ";
        } else {
            sql2 = sql8 + "AND examPaperNum={examPaperNum}    ";
        }
        String sourceStr = "";
        if (!"0".equals(source)) {
            sourceStr = " AND source={source}";
        }
        String sql9 = (sql2 + " AND statisticType={type}   " + sourceStr + " and xuankezuhe = {subCompose} ") + " ) stul ON en.studentId = stul.studentId   left join  (SELECT  examPaperNum,studentId,classRanking,gradeRanking,areaRanking,statisticType,studentType,sqts,oqts,classNum,gradeNum,schoolNum,totalScore,dengji,dengjixiao     FROM studentlevel_fufen    WHERE schoolNum={schoolNum} AND gradeNum={gradeNum}  " + classStr;
        if (flg) {
            sql3 = sql9 + "AND examPaperNum={sexamPaperNum}    ";
        } else {
            sql3 = sql9 + "AND examPaperNum={examPaperNum}    ";
        }
        String sql10 = (sql3 + "AND statisticType={type}   AND source={source} and xuankezuhe = {subCompose}   ") + " UNION ALL  select examPaperNum,studentId,classRanking_fufen,gradeRanking_fufen,areaRanking_fufen,statisticType,studentType,sqts,oqts,classNum,gradeNum,schoolNum,totalScore_fufen,dengji,''dengjixiao  from studentlevel_butongji s inner join (select es.source from examsourceset es left join (select * from data where type=26 and value<>0) d on es.source=d.value where es.examNum={examNum} and es.isJoin=1) da on s.source=da.source where schoolNum ={schoolNum}  AND gradeNum ={gradeNum}  " + classStr;
        if (flg) {
            sql4 = sql10 + "AND examPaperNum={sexamPaperNum}    ";
        } else {
            sql4 = sql10 + "AND examPaperNum={examPaperNum}    ";
        }
        String sql11 = ((sql4 + "  AND statisticType={type}  " + sourceStr + " and xuankezuhe = {subCompose}") + ") stul_fufen ON en.studentId = stul_fufen.studentId  ") + "LEFT JOIN (SELECT examNum,subjectNum,gradeNum,examPaperNum FROM exampaper  WHERE ";
        if (flg) {
            sql5 = sql11 + "examPaperNum={sexamPaperNum} ";
        } else {
            sql5 = sql11 + "examPaperNum={examPaperNum}  ";
        }
        String sql12 = sql5 + ") ep ON stul.examPaperNum=ep.examPaperNum    LEFT JOIN (SELECT subjectNum,subjectName,id,subjectType FROM `subject` WHERE ";
        if (flg) {
            sql6 = sql12 + "subjectNum={sSjt}  ";
        } else {
            sql6 = sql12 + "subjectNum={subjectNum}  ";
        }
        String sql13 = (sql6 + ") sub ON ep.subjectNum=sub.subjectNum   LEFT JOIN (SELECT gradeNum,gradeName FROM basegrade WHERE gradeNum={gradeNum}) g ON en.gradeNum=g.gradeNum  LEFT JOIN (SELECT id,schoolNum,schoolName,schoolType FROM school WHERE id={schoolNum}) sch ON en.schoolNum=sch.id   LEFT JOIN (select classNum,subjectNum,group_concat(IFNULL(userName,'--')) teacherName from userposition_record where examNum={examNum} and gradeNum={gradeNum} and schoolnum={schoolNum} " + classStr + " and type='1' group by classNum,subjectNum) upr on upr.classNum=en.classNum and upr.subjectNum=sub.subjectNum LEFT JOIN (SELECT s.id,s.schoolNum,s.gradeNum,s.classNum,s.jie,s.studentName,s.studentNum,s.type,s.studentId,s.subjectCombineNum,subc.subjectCombineName,s.source,da.name sourceName,da.isJoin    FROM student s left join subjectcombine subc on subc.subjectCombineNum=s.subjectCombineNum  left join (select examNum,e.source,es.value,es.name,e.isJoin from examsourceset e left join (select name,value from data where type=26)es on e.source=es.value where examNum={examNum}) da on s.source=da.value ") + " WHERE s.schoolNum={schoolNum} ";
        if (null != source && !"0".equals(source)) {
            sql13 = sql13 + "AND s.source={source}   ";
        }
        if (null != subCompose && !"0".equals(subCompose)) {
            if (null == subCPid) {
                sql13 = sql13 + " and subc.pid={subCompose} ";
            } else {
                sql13 = sql13 + " and s.subjectCombineNum={subCompose} ";
            }
        }
        String sql14 = sql13 + ") stu ON en.studentId=stu.id    LEFT JOIN data d on d.type = '22' and d.value = stu.type LEFT JOIN (SELECT id,classNum,className,classType FROM class  ) c ON c.id = en.classNum LEFT JOIN    " + illStr + "ilg    ON en.studentId=ilg.studentId   WHERE stu.studentName IS NOT NULL  ORDER BY  if(isnull(stul.studentType),1,0),stul.studentType,en.classNum ASC,stu.studentNum";
        this.log.info("sql-班级--科目主观题、客观题 导出 sql--" + sql14);
        return this.dao2._queryBeanList(sql14, Studentlevel.class, args);
    }

    public List<Studentlevel> getSingleSubjectData_fufen(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, String type, String source, String sexamPaperNum, String sSjt, boolean flg, String isJointStuType, String subCompose) {
        String sql;
        String sql2;
        String sql3;
        Exampaper exampaperObj = this.examDao.getExampaperInfo1(examNum, subjectNum, gradeNum);
        String totalPage = exampaperObj.getTotalPage();
        String examPaperNum = exampaperObj.getExamPaperNum() + "";
        String c_subjectNum = exampaperObj.getSubjectNum() + "";
        String p_examPaperNum = exampaperObj.getPexamPaperNum() + "";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("c_subjectNum", c_subjectNum);
        args.put("p_examPaperNum", p_examPaperNum);
        args.put("totalPage", totalPage);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("sexamPaperNum", sexamPaperNum);
        args.put("examPaperNum", examPaperNum);
        args.put("type", type);
        args.put("source", source);
        args.put("subCompose", subCompose);
        args.put("sSjt", sSjt);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        String illStr = "(SELECT  a.studentId,GROUP_CONCAT(a.examineeNum) examineeNum,a.schoolNum,a.gradeNum   ,GROUP_CONCAT(a.aa) aa,a.examPaperNum,a.examinationRoomNum,a.type    FROM (SELECT DISTINCT r.*  FROM (SELECT sd.studentId,sd.gradeNum,sd.examNum,sd.examineeNum,GROUP_CONCAT(re.page) aa,re.exampaperNum,sd.examinationRoomNum,ill.type,sd.schoolNum   FROM (SELECT schoolNum,gradeNum,studentId,examNum,examineeNum,examinationRoomNum FROM examinationnum   WHERE examNum={examNum}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum} and subjectNum = {c_subjectNum} ) sd   LEFT JOIN (SELECT id,page,examPaperNum,examinationRoomNum,studentId FROM regexaminee   WHERE examPaperNum={p_examPaperNum} AND schoolNum={schoolNum}) re ON sd.studentid = re.studentid    LEFT JOIN (SELECT type,examPaperNum,studentId,examinationRoomNum FROM illegal   WHERE examPaperNum={p_examPaperNum}   AND schoolNum={schoolNum}  ) ill ON  ill.studentId=sd.studentId   GROUP BY sd.studentid   HAVING COUNT(page)  <{totalPage}) r    UNION ALL  SELECT studentId,'" + gradeNum + "' gradeNum,'" + examNum + "' examNum,NULL examineeNum,NULL aa,exampaperNum,examinationRoomNum,type,schoolNum   FROM  illegal WHERE examPaperNum={p_examPaperNum}   AND schoolNum={schoolNum}   ) a    GROUP BY a.studentId)";
        String stunameStr = "stu.studentName studentName,";
        String dataStr = "";
        if ("1".equals(isJointStuType)) {
            stunameStr = "IF(stu.type='0',stu.studentName,CONCAT(stu.studentName,d.name)) studentName,";
            dataStr = " LEFT JOIN data d on d.type = '22' and d.value = stu.type ";
        }
        String classStr = "";
        if (null != classNum && !"".equals(classNum)) {
            classStr = "AND classNum={classNum} ";
        }
        String sql4 = "select DISTINCT  stu.schoolNum,sch.schoolName,stu.gradeNum,g.gradeName,stu.classNum,c.className,stul.examPaperNum,en.studentId,stu.studentId  realStudentId," + stunameStr + "stu.studentNum ,en.examineeNum,stul.classRanking,stul.gradeRanking,stul.statisticType,stul.studentType,round(stul.sqts,2) sqts,round(stul.oqts,2) oqts,round(stul.totalScore,2) totalScore ,sub.subjectNum,sub.subjectName,ilg.type,ilg.aa possessPage,'" + totalPage + "' totalPage,stu.type studentType,'' levelClassNum,'' levelClassName    FROM (SELECT studentId,examineeNum,gradeNum,schoolNum,classNum,subjectNum FROM examinationnum  WHERE examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum} and subjectNum = {c_subjectNum} " + classStr + ") en LEFT JOIN (SELECT  examPaperNum,studentId,classRanking,gradeRanking,statisticType,studentType,sqts,oqts,classNum,gradeNum,schoolNum,totalScore    FROM studentlevel_fufen   WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} " + classStr;
        if (flg) {
            sql = sql4 + "AND examPaperNum={sexamPaperNum}    ";
        } else {
            sql = sql4 + "AND examPaperNum={examPaperNum}    ";
        }
        String sql5 = sql + "AND statisticType={type}   AND source={source} and xuankezuhe = {subCompose}  ) stul ON en.studentId = stul.studentId  LEFT JOIN (SELECT examNum,subjectNum,gradeNum,examPaperNum FROM exampaper  WHERE ";
        if (flg) {
            sql2 = sql5 + "examPaperNum={sexamPaperNum} ";
        } else {
            sql2 = sql5 + "examPaperNum={examPaperNum} ";
        }
        String sql6 = sql2 + ") ep ON stul.examPaperNum=ep.examPaperNum    LEFT JOIN (SELECT subjectNum,subjectName,id,subjectType FROM `subject` WHERE ";
        if (flg) {
            sql3 = sql6 + "subjectNum={sSjt}  ";
        } else {
            sql3 = sql6 + "subjectNum={subjectNum}  ";
        }
        String sql7 = sql3 + ") sub ON ep.subjectNum=sub.subjectNum   LEFT JOIN (SELECT gradeNum,gradeName FROM basegrade WHERE gradeNum={gradeNum} ) g ON en.gradeNum=g.gradeNum  LEFT JOIN (SELECT id,schoolNum,schoolName,schoolType FROM school WHERE id={schoolNum}) sch ON en.schoolNum=sch.id   LEFT JOIN (SELECT id,schoolNum,gradeNum,classNum,jie,studentName,studentNum,type,studentId    FROM student WHERE schoolNum={schoolNum} ";
        if (null != source && !"0".equals(source)) {
            sql7 = sql7 + "AND source={source}   ";
        }
        if (null != subCompose && !"0".equals(subCompose)) {
            sql7 = sql7 + "AND subjectCombineNum={subCompose}   ";
        }
        String sql8 = sql7 + ") stu ON en.studentId=stu.id   " + dataStr + "LEFT JOIN (SELECT id,classNum,className,classType FROM class  ) c ON c.id = en.classNum LEFT JOIN    " + illStr + "ilg    ON en.studentId=ilg.studentId   WHERE stu.studentName IS NOT NULL  ORDER BY  if(isnull(stul.studentType),1,0),stul.studentType,en.classNum ASC,stu.studentNum";
        this.log.info("sql-班级--科目主观题、客观题 导出 sql--赋分--" + sql8);
        return this.dao2._queryBeanList(sql8, Studentlevel.class, args);
    }

    public List getSingleSubjectDataExaminationRoom(String examNum, String gradeNum, String subjectNum, String examRoomNum, String schoolNum, String examroomornot, String type, String source) {
        String str;
        String sql;
        String sql2;
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        String examPaperNum = exampaperObj.getExamPaperNum().toString();
        String jie = exampaperObj.getJie();
        String totalPage = exampaperObj.getTotalPage();
        Map args = new HashMap();
        args.put("examRoomNum", examRoomNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examPaperNum", examPaperNum);
        args.put("totalPage", totalPage);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("jie", jie);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("type", type);
        args.put("source", source);
        if (null != examRoomNum && !"".equals(examRoomNum) && !"-1".equals(examRoomNum)) {
            str = "AND examinationRoomNum={examRoomNum} ";
        } else {
            str = "AND schoolNum={schoolNum}  ";
        }
        String ill_sql = "(SELECT  a.studentId,a.schoolNum,GROUP_CONCAT(a.aa) aa,a.examPaperNum,a.examinationRoomNum,a.type  FROM (SELECT DISTINCT r.*  FROM (SELECT re.studentId,re.schoolNum,GROUP_CONCAT(re.page) aa,re.exampaperNum,re.examinationRoomNum ,ill.type   FROM(SELECT studentId,type,examPaperNum FROM illegal  WHERE examPaperNum={examPaperNum} " + str + ") ill  LEFT JOIN(SELECT examPaperNum,studentId,page,examinationRoomNum,schoolNum FROM regexaminee WHERE examPaperNum={examPaperNum}  " + str + ") re  ON ill.studentId=re.studentId  GROUP BY re.studentid   HAVING COUNT(page)<{totalPage}) r  UNION ALL  SELECT ill.studentId,st.schoolNum,NULL aa,ill.examPaperNum,ill.examinationRoomNum,ill.type  FROM(SELECT studentId,type,examPaperNum,examinationRoomNum FROM illegal  WHERE examPaperNum=''  " + str + ")ill  LEFT JOIN(SELECT id,studentId,studentNum,schoolNum FROM student WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}  ";
        if (null != source && !"0".equals(source)) {
            ill_sql = ill_sql + "AND source=''  ";
        }
        String ill_sql2 = ill_sql + ") st  ON ill.studentId=st.id  )a  GROUP BY a.studentId)";
        String sql3 = "select DISTINCT  stu.schoolNum,sch.schoolName,stu.gradeNum,gra.gradeName,stu.classNum,cla.className,stul.examPaperNum,stul.studentId,stu.studentName,stu.studentNum ,stul.classRanking,stul.gradeRanking,stul.statisticType,stul.studentType,round(stul.sqts,2) sqts,round(stul.oqts,2) oqts,round((stul.sqts+stul.oqts),2) totalScore ,sub.subjectNum,sub.subjectName,en.examinationRoomNum,er.examinationRoomName,er.examinationRoomNum  eRoomNum,ilg.type,ilg.aa possessPage,'" + totalPage + "' totalPage,stu.type studentType   FROM(SELECT studentId,examinationRoomNum,examineeNum FROM examinationnum  WHERE ";
        if (null != examRoomNum && !"-1".equals(examRoomNum) && !"".equals(examRoomNum)) {
            sql = sql3 + "examinationRoomNum={examRoomNum} and subjectNum = {subjectNum} ";
        } else {
            sql = sql3 + "examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum}  and subjectNum = {subjectNum} ";
        }
        String sql4 = sql + ")en  LEFT JOIN(SELECT examPaperNum,studentId,classRanking,gradeRanking,statisticType,studentType,sqts,oqts,subjectNum  FROM studentlevel  WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND examPaperNum={examPaperNum}  AND statisticType={type}   AND source={source}  ) stul ON en.studentId=stul.studentId   LEFT JOIN (SELECT schoolNum,gradeNum,classNum,jie,studentName,studentNum,type,studentId,id   FROM student WHERE schoolNum={schoolNum}  AND gradeNum={gradeNum}  ";
        if (null != source && !"0".equals(source)) {
            sql4 = sql4 + "AND source={source}  ";
        }
        String sql5 = sql4 + "AND jie={jie}  ) stu ON stul.studentId=stu.id  LEFT JOIN (SELECT id,examinationRoomNum,examinationRoomName FROM examinationroom  WHERE ";
        if (null != examRoomNum && !"-1".equals(examRoomNum) && !"".equals(examRoomNum)) {
            sql2 = sql5 + "id={examRoomNum} ";
        } else {
            sql2 = sql5 + "examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum}  ";
        }
        String sql6 = sql2 + ")er ON en.examinationRoomNum=er.id   LEFT JOIN(SELECT subjectNum,subjectName FROM `subject` WHERE subjectNum={subjectNum} )sub ON stul.subjectNum=sub.subjectNum  LEFT JOIN(SELECT id,schoolNum,schoolName FROM school WHERE id={schoolNum} )sch ON stu.schoolNum=sch.id  LEFT JOIN(SELECT gradeNum,gradeName FROM basegrade WHERE gradeNum={gradeNum})gra ON stu.gradeNum=gra.gradeNum  LEFT JOIN(SELECT id,classNum,className FROM class WHERE schoolNum={schoolNum} AND gradeNum={gradeNum})cla ON stu.classNum=cla.id  LEFT JOIN  " + ill_sql2 + "  ilg ON en.studentId=ilg.studentId  WHERE stu.studentName IS NOT NULL  ORDER BY er.examinationRoomNum,stul.studentType ASC,totalScore DESC";
        this.log.info("sql-考场--科目主观题、客观题 导出sql--" + sql6);
        return this.dao2._queryBeanList(sql6, Studentlevel.class, args);
    }

    public List getSingleSubjectDataPage(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, int pageStart, int pageSize) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("pageStart", Integer.valueOf(pageStart));
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao2._queryBeanList("select stul.schoolNum,stul.gradeNum,stul.classNum,stul.examPaperNum,stul.studentId,stu.studentName,stul.sqts,stul.oqts,sub.subjectName,(stul.sqts+stul.oqts) totalScore  from studentlevel stul,exampaper ep,student stu ,`subject`  sub  where ep.examNum={examNum}  and ep.gradeNum={gradeNum}  and ep.subjectNum={subjectNum}  and stul.examPaperNum=ep.examPaperNum  and stul.schoolNum={schoolNum}  and stul.gradeNum={gradeNum}  and stul.classNum={classNum} and stul.studentId=stu.studentId  and sub.subjectNum={subjectNum}  ORDER BY totalScore desc  LIMIT {pageStart},{pageSize} ", Studentlevel.class, args);
    }

    public List getAllSubjectName(String schoolNum, String gradeNum, String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList("SELECT s.subjectName  from  (SELECT DISTINCT subjectNum FROM studentlevel  where   examNum = {examNum}  AND gradeNum={gradeNum} AND schoolNum={schoolNum} AND classNum LIKE '%%' ) AS  ds LEFT JOIN `subject` s ON ds.subjectNum = s.subjectNum ORDER BY s.id", Subject.class, args);
    }

    public List getTotalAndStudentId(String examNum, String gradeNum, String classNum, String schoolNum) {
        String sql = "select ss.schoolName,ss.studentId,ss.gradeNum,ss.classNum,ss.examPaperNum,sum(ss.totalScore) totalScore  from  (select DISTINCT stul.schoolNum,sch.schoolName,stul.gradeNum,stul.classNum,stu.studentName,stul.studentId,stul.totalScore,stul.examPaperNum  from studentlevel stul,exampaper ep,student stu ,`subject`  sub ,school sch  where ep.examNum={examNum}   and ep.gradeNum={gradeNum}  and stul.examPaperNum=ep.examPaperNum  and stul.schoolNum={schoolNum} and sch.schoolNum='" + schoolNum + "'  and stul.schoolNum=sch.schoolNum  and stul.gradeNum={gradeNum}  ";
        if (null != classNum && !classNum.equals("") && !classNum.equals("-1")) {
            sql = sql + "and stul.classNum={classNum}  ";
        }
        String sql2 = sql + "and stul.studentId=stu.studentId  ) ss  GROUP BY ss.studentId  ORDER BY totalScore  DESC";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        return this.dao2._queryBeanList(sql2, Studentlevel.class, args);
    }

    public String commbinSql(String examNum, String gradeNum, String schoolNum, String classNum) {
        String str = "  examNum = {examNum}   AND schoolNum={schoolNum} ";
        if (gradeNum != null && !gradeNum.equals("")) {
            str = str + " AND gradeNum={gradeNum}  ";
        }
        if (classNum != null && !classNum.equals("") && !classNum.equals("-1")) {
            str = str + " AND classNum LIKE {classNum}  ";
        }
        String str2 = "  examNum = {examNum}  AND schoolNum={schoolNum}  ";
        if (gradeNum != null && !gradeNum.equals("")) {
            str = str + " AND gradeNum={gradeNum}  ";
        }
        if (classNum != null && !classNum.equals("") && !classNum.equals("-1")) {
            str2 = str2 + " AND classNum LIKE {classNum}  ";
        }
        String str1 = "SELECT ds.subjectNum from (SELECT DISTINCT subjectNum FROM studentlevel  where " + str2 + " ) AS ds LEFT JOIN `subject` s ON ds.subjectNum = s.subjectNum ORDER BY s.id ";
        String str3 = " (SELECT * from studentlevel WHERE " + str + " ) AS s1  LEFT JOIN  student st ON st.studentId = s1.studentId ";
        String sql = "SELECT  CONCAT('select s1.studentid,st.studentName,sum(totalScore), ',GROUP_CONCAT(CONCAT('sum(if(s1.subjectNum=''',subjectNum,''' ,s1.totalScore,'''')) ')),'  from " + str3 + " ', 'group by s1.studentid,st.studentName order by sum(totalScore)  desc')   FROM (" + str1 + ") AS s ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) ("%" + classNum + "%"));
        return this.dao2._queryStr(sql, args);
    }

    public List getClassStuAndSub(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String isJointStuType, String subCompose) throws Exception {
        ArrayList arrayList;
        List list = new ArrayList();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ getTotalScore(?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, schoolNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, classNum);
                pstat.setString(5, studentType);
                pstat.setString(6, graduationType);
                pstat.setString(7, stuSourceType);
                pstat.setString(8, viewRankInfo);
                pstat.setString(9, isJointStuType);
                pstat.setString(10, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                arrayList = new ArrayList();
                while (rs.next()) {
                    Subject sub = new Subject();
                    sub.setSubjectNum(rs.getInt(1));
                    sub.setSubjectName(rs.getString(2));
                    arrayList.add(sub);
                }
                list.add(arrayList);
            } catch (Exception e) {
                e.printStackTrace();
                DbUtils.close(rs, pstat, conn);
            }
            if (arrayList.size() <= 0) {
                DbUtils.close(rs, pstat, conn);
                return null;
            }
            while (pstat.getMoreResults()) {
                ArrayList arrayList2 = new ArrayList();
                rs = pstat.getResultSet();
                rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] obj = new Object[15 + (arrayList.size() * 4)];
                    for (int i = 0; i < obj.length; i++) {
                        obj[i] = rs.getObject(i + 1);
                    }
                    arrayList2.add(obj);
                }
                list.add(arrayList2);
            }
            DbUtils.close(rs, pstat, conn);
            this.log.info("---存储过程-结果集数-" + list.size());
            return list;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public List<Object[]> getClassStuAndSub_new(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String isJointStuType, String fufen, String subCompose) throws Exception {
        List<Object[]> list = new ArrayList<>();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ getTotalScore_new2(?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, schoolNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, classNum);
                pstat.setString(5, studentType);
                pstat.setString(6, graduationType);
                pstat.setString(7, stuSourceType);
                pstat.setString(8, viewRankInfo);
                pstat.setString(9, isJointStuType);
                pstat.setString(10, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Object[] obj = new Object[28];
                    for (int i = 0; i < obj.length; i++) {
                        obj[i] = rs.getObject(i + 1);
                    }
                    list.add(obj);
                }
                DbUtils.close(rs, pstat, conn);
            } catch (Exception e) {
                this.log.info("--总科目成绩导出按班级存储过程【getClassStuAndSub_new】异常--" + e);
                DbUtils.close(rs, pstat, conn);
            }
            return list;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public List<List<?>> getClassStuAndSub_fufen(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String isJointStuType, String subCompose) throws Exception {
        List<Subject> temp1;
        List<List<?>> list = new ArrayList<>();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ getTotalScore_fufen(?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, schoolNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, classNum);
                pstat.setString(5, studentType);
                pstat.setString(6, graduationType);
                pstat.setString(7, stuSourceType);
                pstat.setString(8, viewRankInfo);
                pstat.setString(9, isJointStuType);
                pstat.setString(10, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                temp1 = new ArrayList<>();
                while (rs.next()) {
                    Subject sub = new Subject();
                    sub.setSubjectNum(rs.getInt(1));
                    sub.setSubjectName(rs.getString(2));
                    temp1.add(sub);
                }
                list.add(temp1);
            } catch (Exception e) {
                e.printStackTrace();
                DbUtils.close(rs, pstat, conn);
            }
            if (temp1.size() <= 0) {
                DbUtils.close(rs, pstat, conn);
                return null;
            }
            while (pstat.getMoreResults()) {
                List<?> arrayList = new ArrayList<>();
                rs = pstat.getResultSet();
                rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] obj = new Object[15 + (temp1.size() * 4)];
                    for (int i = 0; i < obj.length; i++) {
                        obj[i] = rs.getObject(i + 1);
                    }
                    arrayList.add(obj);
                }
                list.add(arrayList);
            }
            DbUtils.close(rs, pstat, conn);
            this.log.info("---存储过程-结果集数-赋分-" + list.size());
            return list;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public List<List<?>> getClassStuAndSub_bzf(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String fufen, String isJointStuType, String subCompose) throws Exception {
        List<Subject> temp1;
        String sql = "0".equals(fufen) ? "{call /* shard_host_HG=Read */ getTotalScore_bzf(?,?,?,?,?,?,?,?,?,?)}" : "{call /* shard_host_HG=Read */ getTotalScore_bzf_fufen(?,?,?,?,?,?,?,?,?,?)}";
        List<List<?>> list = new ArrayList<>();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, schoolNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, classNum);
                pstat.setString(5, studentType);
                pstat.setString(6, graduationType);
                pstat.setString(7, stuSourceType);
                pstat.setString(8, viewRankInfo);
                pstat.setString(9, isJointStuType);
                pstat.setString(10, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                temp1 = new ArrayList<>();
                while (rs.next()) {
                    Subject sub = new Subject();
                    sub.setSubjectNum(rs.getInt(1));
                    sub.setSubjectName(rs.getString(2));
                    temp1.add(sub);
                }
                list.add(temp1);
            } catch (Exception e) {
                e.printStackTrace();
                DbUtils.close(rs, pstat, conn);
            }
            if (temp1.size() <= 0) {
                DbUtils.close(rs, pstat, conn);
                return null;
            }
            while (pstat.getMoreResults()) {
                List<?> arrayList = new ArrayList<>();
                rs = pstat.getResultSet();
                rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] obj = new Object[12 + temp1.size()];
                    for (int i = 0; i < obj.length; i++) {
                        obj[i] = rs.getObject(i + 1);
                    }
                    arrayList.add(obj);
                }
                list.add(arrayList);
            }
            DbUtils.close(rs, pstat, conn);
            this.log.info("---存储过程-结果集数-标准分-" + list.size());
            return list;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public List<Object[]> getClassStuAndSub_bzf_new(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String fufen, String isJointStuType, String subCompose) throws Exception {
        String sql = "0".equals(fufen) ? "{call /* shard_host_HG=Read */ getTotalScore_bzf_new(?,?,?,?,?,?,?,?,?,?)}" : "{call /* shard_host_HG=Read */ getTotalScore_bzf_fufen_new(?,?,?,?,?,?,?,?,?,?)}";
        List<Object[]> list = new ArrayList<>();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, schoolNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, classNum);
                pstat.setString(5, studentType);
                pstat.setString(6, graduationType);
                pstat.setString(7, stuSourceType);
                pstat.setString(8, viewRankInfo);
                pstat.setString(9, isJointStuType);
                pstat.setString(10, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Object[] obj = new Object[16];
                    for (int i = 0; i < obj.length; i++) {
                        obj[i] = rs.getObject(i + 1);
                    }
                    list.add(obj);
                }
                DbUtils.close(rs, pstat, conn);
            } catch (Exception e) {
                this.log.info("--总科目成绩（标准分）导出存储过程【getClassStuAndSub_bzf_new】异常--" + e);
                DbUtils.close(rs, pstat, conn);
            }
            return list;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public List getClassStuAndSub_NoClaGraRank(String examNum, String gradeNum, String classNum, String schoolNum, String studentType) throws Exception {
        List list = new ArrayList();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ getTotalScore_NoClaGraRank(?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, schoolNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, classNum);
                pstat.setString(5, studentType);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                ArrayList arrayList = new ArrayList();
                while (rs.next()) {
                    Subject sub = new Subject();
                    sub.setSubjectNum(rs.getInt(1));
                    sub.setSubjectName(rs.getString(2));
                    arrayList.add(sub);
                }
                list.add(arrayList);
                while (pstat.getMoreResults()) {
                    ArrayList arrayList2 = new ArrayList();
                    rs = pstat.getResultSet();
                    rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        Object[] obj = new Object[9 + arrayList.size()];
                        for (int i = 0; i < obj.length; i++) {
                            obj[i] = rs.getObject(i + 1);
                        }
                        arrayList2.add(obj);
                    }
                    list.add(arrayList2);
                }
                DbUtils.close(rs, pstat, conn);
            } catch (Exception e) {
                e.printStackTrace();
                DbUtils.close(rs, pstat, conn);
            }
            this.log.info("---存储过程-结果集数-" + list.size());
            return list;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public List getAvgScoreAndStuCount(String examNum, String subjectNum, String schoolNum, String gradeNum, String classNum, String classType, String graduationType, String stuSourceType, String subCompose) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("graduationType", (Object) graduationType).put("classType", (Object) classType).put("stuSourceType", (Object) stuSourceType).put("subCompose", (Object) subCompose);
        return this.dao2._queryBeanList("SELECT * FROM classlevel    WHERE examNum={examNum} AND subjectNum={subjectNum}    AND schoolNum={schoolNum} AND gradeNum={gradeNum} AND classNum={classNum}    AND statisticType={graduationType} AND studentType={classType}   AND source={stuSourceType} and xuankezuhe = {subCompose} ", Classlevel.class, args);
    }

    public List<Classlevel> getAvgScoreAndStuCount_fufen(String examNum, String subjectNum, String schoolNum, String gradeNum, String classNum, String classType, String graduationType, String stuSourceType, String subCompose) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("graduationType", (Object) graduationType).put("classType", (Object) classType).put("stuSourceType", (Object) stuSourceType).put("subCompose", (Object) subCompose);
        return this.dao2._queryBeanList("SELECT * FROM classlevel_fufen    WHERE examNum={examNum} AND subjectNum={subjectNum}    AND schoolNum={schoolNum} AND gradeNum={gradeNum} AND classNum={classNum}   AND statisticType={graduationType} AND studentType={classType}   AND source={stuSourceType} and xuankezuhe = {subCompose}  ", Classlevel.class, args);
    }

    public List getClassType(String examNum, String gradeNum, String classNum, String schoolNum) throws Exception {
        String sql = "SELECT DISTINCT studentType from classlevel   WHERE schoolNum={schoolNum} AND gradeNum={gradeNum}  ";
        if (null != classNum && !"-1".equals(classNum) && !"".equals(classNum)) {
            sql = sql + "AND classNum={classNum}   ";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        new ArrayList();
        List list = this.dao2._queryBeanList(sql + "AND examNum={examNum}  ORDER BY studentType*1", Studentlevel.class, args);
        if (null != list && list.size() > 0) {
            return list;
        }
        return null;
    }

    public List<Studentlevel> getClassType_fufen(String examNum, String gradeNum, String classNum, String schoolNum) throws Exception {
        String sql = "SELECT DISTINCT studentType from classlevel_fufen   WHERE schoolNum={schoolNum}  AND gradeNum={gradeNum}  ";
        if (null != classNum && !"-1".equals(classNum) && !"".equals(classNum)) {
            sql = sql + "AND classNum={classNum}   ";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        List _queryBeanList = this.dao2._queryBeanList(sql + "AND examNum={examNum}  ORDER BY studentType*1", Studentlevel.class, args);
        if (null != _queryBeanList && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    public List getClassStuAndSubByExaminationRoom(String examNum, String gradeNum, String examRoomNum, String schoolNum, String graduationType, String stuSourceType) throws Exception {
        List list = new ArrayList();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ getTotalScoreByExamRoom(?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, schoolNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, examRoomNum);
                pstat.setString(5, "1");
                pstat.setString(6, graduationType);
                pstat.setString(7, stuSourceType);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                ArrayList arrayList = new ArrayList();
                while (rs.next()) {
                    Subject sub = new Subject();
                    sub.setSubjectNum(rs.getInt(1));
                    sub.setSubjectName(rs.getString(2));
                    arrayList.add(sub);
                }
                list.add(arrayList);
                while (pstat.getMoreResults()) {
                    ArrayList arrayList2 = new ArrayList();
                    rs = pstat.getResultSet();
                    rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        Object[] obj = new Object[12 + (arrayList.size() * 3)];
                        for (int i = 0; i < obj.length; i++) {
                            if (i == obj.length - 1) {
                                obj[i] = Integer.valueOf(rs.getInt(i + 1));
                            } else {
                                obj[i] = rs.getObject(i + 1);
                            }
                        }
                        arrayList2.add(obj);
                    }
                    list.add(arrayList2);
                }
                DbUtils.close(rs, pstat, conn);
            } catch (Exception e) {
                e.printStackTrace();
                DbUtils.close(rs, pstat, conn);
            }
            this.log.info("更新中间表数据条数--" + list.size());
            return list;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public List<Object[]> getClassStuAndSubPage(String examNum, String gradeNum, String classNum, String schoolNum, int pageStart, int pageSize) {
        String sql = commbinSql(examNum, gradeNum, schoolNum, classNum);
        if (null != sql) {
            String sql2 = sql + "   LIMIT {pageStart},{pageSize} ";
            Map args = StreamMap.create().put("pageStart", (Object) Integer.valueOf(pageStart)).put("pageSize", (Object) Integer.valueOf(pageSize));
            return this.dao2._queryArrayList(sql2, args);
        }
        return null;
    }

    public List getLeakScoreImage(Scoreimage scoreImage, int pageStart, int pageSize) {
        Map args = StreamMap.create().put("pageStart", (Object) Integer.valueOf(pageStart)).put("pageSize", (Object) Integer.valueOf(pageSize));
        return this.dao2._queryBeanList("select id,examPaperNum,studentID,REPLACE(questionNum,'_','.')*1  questionNum,scoreImg scoreImg,schoolNum,isDelete from scoreimage where isDelete='F' LIMIT {pageStart},{pageSize} ", Scoreimage.class, args);
    }

    public List getLeakScoreImageById(Scoreimage scoreImage, int sid) {
        Map args = StreamMap.create().put("sid", (Object) Integer.valueOf(sid));
        return this.dao2._queryBeanList("select id,examPaperNum,studentID,REPLACE(questionNum,'_','.') questionNum,scoreImg scoreImg,schoolNum,isDelete from scoreimage where isDelete='F' and id={sid} ", Scoreimage.class, args);
    }

    public Integer getScoreImageCount(Scoreimage scoreImage) {
        return this.dao2._queryInt("select count(1) from scoreimage where isDelete='F'", null);
    }

    public List getLeakScoreImages(Scoreimage scoreImage) {
        return this.dao2.queryBeanList("select id,examPaperNum,studentID,REPLACE(questionNum,'_','.') questionNum,scoreImg scoreImg,schoolNum,isDelete from scoreimage where isDelete='F' LIMIT 3,4", Scoreimage.class);
    }

    public List getStudentIdErrCount(ExamineeNumError examineeNumError, String searchType) {
        String sql = "select DISTINCT ene.id,ene.examPaperNum,ene.studentId,ene.errorType,ene.groupNum,ene.page,ene.examineeNum,e.examName,e.examNum,sub.subjectName,sub.subjectNum,g.gradeName,g.gradeNum,sch.schoolNum,sch.schoolName,ene.examinationRoomNum,er.examinationRoomName,er.examinationRoomNum eRoomNum   from examineenumerror ene   LEFT JOIN exampaper ep   ON ene.examPaperNum=ep.examPaperNum   LEFT JOIN exam e    ON ep.examNum=e.examNum   LEFT JOIN `subject` sub   ON ep.subjectNum=sub.subjectNum   LEFT JOIN grade g   ON ep.gradeNum=g.gradeNum   LEFT JOIN school sch    ON ene.schoolNum=sch.schoolNum   LEFT JOIN examinationroom er   ON ene.examinationRoomNum=er.id   WHERE ene.examinationRoomNum IN   \t\t\t\t\t\t(   \t\t\t\t\t\tSELECT DISTINCT id   \t\t\t\t\t\tFROM examinationroom   \t\t\t\t\t\tWHERE examNum={ExamNum}   \t\t\t\t\t\tAND schoolNum={SchoolNum}   \t\t\t\t\t\tAND gradeNum={GradeNum}   \t\t\t\t\t\t)  AND ene.examPaperNum IN   \t\t\t\t\t\t(   \t\t\t\t\t\tSELECT DISTINCT examPaperNum    \t\t\t\t\t\tFROM exampaper   \t\t\t\t\t\tWHERE examNum={ExamNum}  \t\t\t\t\t\tAND subjectNum={SubjectNum}   \t\t\t\t\t\tAND gradeNum={GradeNum}     \t\t\t\t\t\t)    AND ene.schoolNum={SchoolNum}   ";
        if (null != searchType && !searchType.equals("") && (searchType.equals("0") || searchType.equals("2") || searchType.equals("1"))) {
            sql = sql + "AND ene.errorType={searchType}    ";
        }
        if (null != examineeNumError.getExaminationRoomNum() && !examineeNumError.getExaminationRoomNum().equals("-1")) {
            sql = sql + "AND ene.examinationRoomNum={ExaminationRoomNum}   ";
        }
        String sql2 = sql + "AND g.gradeNum=(SELECT gradeNum FROM grade WHERE schoolNum={SchoolNum} AND gradeNum={GradeNum} )    ORDER BY er.examinationRoomNum ASC    ";
        this.log.info(searchType + ":SearchType-- sql----getStudentIdErrCount()-：" + sql2);
        Map args = new HashMap();
        args.put("ExamNum", examineeNumError.getExamNum());
        args.put("SchoolNum", examineeNumError.getSchoolNum());
        args.put("GradeNum", examineeNumError.getGradeNum());
        args.put("SubjectNum", examineeNumError.getSubjectNum());
        args.put("searchType", searchType);
        args.put("ExaminationRoomNum", examineeNumError.getExaminationRoomNum());
        return this.dao2._queryBeanList(sql2, ExamineeNumError.class, args);
    }

    public List getFirstStudent(ExamineeNumError examineeNumError, String id, String searchType) {
        String sql = "select DISTINCT ene.id,ene.examPaperNum,ene.studentId,ene.errorType,ene.groupNum,ene.page,ene.examineeNum,e.examName,e.examNum,sub.subjectName,sub.subjectNum,g.gradeName,g.gradeNum,sch.schoolNum,sch.schoolName,ene.examinationRoomNum,er.examinationRoomName,er.examinationRoomNum eRoomNum   from examineenumerror ene   LEFT JOIN exampaper ep   ON ene.examPaperNum=ep.examPaperNum   LEFT JOIN exam e    ON ep.examNum=e.examNum   LEFT JOIN `subject` sub   ON ep.subjectNum=sub.subjectNum   LEFT JOIN grade g   ON ep.gradeNum=g.gradeNum   LEFT JOIN school sch    ON ene.schoolNum=sch.schoolNum   LEFT JOIN examinationroom er   ON ene.examinationRoomNum=er.id   WHERE ene.examinationRoomNum IN   \t\t\t\t\t\t(   \t\t\t\t\t\tSELECT DISTINCT id   \t\t\t\t\t\tFROM examinationroom   \t\t\t\t\t\tWHERE examNum={ExamNum}   \t\t\t\t\t\tAND schoolNum={SchoolNum}   \t\t\t\t\t\tAND gradeNum={GradeNum}  \t\t\t\t\t\t)  AND ene.examPaperNum IN   \t\t\t\t\t\t(   \t\t\t\t\t\tSELECT DISTINCT examPaperNum    \t\t\t\t\t\tFROM exampaper   \t\t\t\t\t\tWHERE examNum={ExamNum}   \t\t\t\t\t\tAND subjectNum={SubjectNum}   \t\t\t\t\t\tAND gradeNum={GradeNum}   \t\t\t\t\t\t)    AND ene.schoolNum={SchoolNum}   ";
        if (null != searchType && !searchType.equals("") && (searchType.equals("0") || searchType.equals("2") || searchType.equals("1"))) {
            sql = sql + "AND ene.errorType={searchType}    ";
        }
        if (null != examineeNumError.getExaminationRoomNum() && !examineeNumError.getExaminationRoomNum().equals("-1")) {
            sql = sql + "AND ene.examinationRoomNum={ExaminationRoomNum}   ";
        }
        String sql2 = sql + "AND g.gradeNum=(SELECT gradeNum FROM grade WHERE schoolNum={SchoolNum}  AND gradeNum={GradeNum} )    ORDER BY er.examinationRoomNum ASC    ";
        this.log.info(searchType + ":SearchType-查询下一条考号有误的考生sql---：" + sql2);
        Map args = new HashMap();
        args.put("ExamNum", examineeNumError.getExamNum());
        args.put("SchoolNum", examineeNumError.getSchoolNum());
        args.put("GradeNum", examineeNumError.getGradeNum());
        args.put("SubjectNum", examineeNumError.getSubjectNum());
        args.put("searchType", searchType);
        args.put("ExaminationRoomNum", examineeNumError.getExaminationRoomNum());
        return this.dao2._queryBeanList(sql2, ExamineeNumError.class, args);
    }

    public List getErrStusFromExamineeNumError(ExamineeNumError examineeNumError, int pageStart, int pageSize, String searchType) {
        String sql = "select DISTINCT ene.id,ene.examPaperNum,ene.studentId,ene.errorType,ene.groupNum,ene.page,ene.examineeNum,e.examName,e.examNum,sub.subjectName,sub.subjectNum,g.gradeName,g.gradeNum,sch.schoolNum,sch.schoolName,ene.examinationRoomNum,er.examinationRoomName,er.examinationRoomNum eRoomNum   from examineenumerror ene   LEFT JOIN exampaper ep   ON ene.examPaperNum=ep.examPaperNum   LEFT JOIN exam e    ON ep.examNum=e.examNum   LEFT JOIN `subject` sub   ON ep.subjectNum=sub.subjectNum   LEFT JOIN grade g   ON ep.gradeNum=g.gradeNum   LEFT JOIN school sch    ON ene.schoolNum=sch.schoolNum   LEFT JOIN examinationroom er   ON ene.examinationRoomNum=er.id   WHERE ene.examinationRoomNum IN   \t\t\t\t\t\t(   \t\t\t\t\t\tSELECT DISTINCT id   \t\t\t\t\t\tFROM examinationroom   \t\t\t\t\t\tWHERE examNum={ExamNum}   \t\t\t\t\t\tAND schoolNum={SchoolNum}   \t\t\t\t\t\tAND gradeNum={GradeNum}   \t\t\t\t\t\t)  AND ene.examPaperNum IN   \t\t\t\t\t\t(   \t\t\t\t\t\tSELECT DISTINCT examPaperNum    \t\t\t\t\t\tFROM exampaper   \t\t\t\t\t\tWHERE examNum={ExamNum} \t\t\t\t\t\tAND subjectNum={SubjectNum}  \t\t\t\t\t\tAND gradeNum={GradeNum}  \t\t\t\t\t\t)    AND ene.schoolNum={SchoolNum}  ";
        if (null != searchType && !searchType.equals("") && (searchType.equals("0") || searchType.equals("2") || searchType.equals("1"))) {
            sql = sql + "AND ene.errorType={searchType}    ";
        }
        if (null != examineeNumError.getExaminationRoomNum() && !examineeNumError.getExaminationRoomNum().equals("-1")) {
            sql = sql + "AND ene.examinationRoomNum={ExaminationRoomNum}   ";
        }
        String sql2 = sql + "AND g.gradeNum=(SELECT gradeNum FROM grade WHERE schoolNum={SchoolNum}  AND gradeNum={GradeNum} )    ORDER BY er.examinationRoomNum ASC    LIMIT {pageStart},{pageSize} ";
        this.log.info(searchType + ":SearchType-带分页的考号有误的考生sql---：" + sql2);
        Map args = new HashMap();
        args.put("ExamNum", examineeNumError.getExamNum());
        args.put("SchoolNum", examineeNumError.getSchoolNum());
        args.put("GradeNum", examineeNumError.getSubjectNum());
        args.put("SubjectNum", examineeNumError.getSubjectNum());
        args.put("searchType", searchType);
        args.put("ExaminationRoomNum", examineeNumError.getExaminationRoomNum());
        args.put("pageStart", Integer.valueOf(pageStart));
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao2._queryBeanList(sql2, ExamineeNumError.class, args);
    }

    public List isExistInExamineeNumError(ExamineeNumError examineeNumError) {
        Map args = new HashMap();
        args.put("ExamPaperNum", examineeNumError.getExamPaperNum());
        args.put("SchoolNum", examineeNumError.getSchoolNum());
        args.put("StudentId", examineeNumError.getStudentId());
        args.put("Page", Integer.valueOf(examineeNumError.getPage()));
        args.put("ExaminationRoomNum", examineeNumError.getExaminationRoomNum());
        return this.dao2._queryBeanList("select * from examineenumerror  where examPaperNum={ExamPaperNum} AND schoolNum={SchoolNum}  AND studentId={StudentId}  AND page={Page}   and examinationRoomNum={ExaminationRoomNum}  ", ExamineeNumError.class, args);
    }

    public List getStudentPaperById(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        this.log.info("get examinationNumError img   sql: select ene.examPaperNum,ene.studentId,ene.schoolNum,ene.errorType,ene.groupNum,ene.page,spi.img from examineenumerror ene,studentpaperimage spi where ene.id={id} and ene.studentId=spi.studentId  AND ene.examPaperNum=spi.examPapernum AND ene.page=spi.page");
        return this.dao2._queryBeanList("select ene.examPaperNum,ene.studentId,ene.schoolNum,ene.errorType,ene.groupNum,ene.page,spi.img from examineenumerror ene,studentpaperimage spi where ene.id={id} and ene.studentId=spi.studentId  AND ene.examPaperNum=spi.examPapernum AND ene.page=spi.page", ExamineeNumError.class, args);
    }

    public void modifyError(String id, String sStudentId, Student stu, String errorPaperNum, int page) {
        new ArrayList();
        String sql = "delete from examineenumerror where  id={id} ";
        if (null == id || "".equals(id)) {
            sql = "delete from examineenumerror where examPaperNum={errorPaperNum} AND studentId={StudentId} AND page={page} ";
        }
        Map args = StreamMap.create().put("id", (Object) id).put("errorPaperNum", (Object) errorPaperNum).put("StudentId", (Object) stu.getStudentId()).put("page", (Object) Integer.valueOf(page)).put("sStudentId", (Object) sStudentId).put("SchoolNum", (Object) stu.getSchoolNum());
        this.log.info("modifyError---sql--" + sql + "sql2--update questionimage set studentId={StudentId} where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page} sql3--update scoreimage set studentId={StudentId} where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page} sq4l--update studentpaperimage set studentId={StudentId} where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page} sql5--update score set studentId={StudentId},schoolNum={SchoolNum}  where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page} sq6: update examinationnumimg set studentId={StudentId},schoolNum={SchoolNum}  where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page}  sql7: update regexaminee set studentId={StudentId},schoolNum={SchoolNum}  where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page}  sql8: update cantrecognized set studentId={StudentId}  where studentId={sStudentId}   and examNum=(SELECT examNum FROM exampaper WHERE examPaperNum={errorPaperNum})  AND subjectNum=(SELECT subjectNum FROM exampaper WHERE examPaperNum={errorPaperNum})  AND gradeNum=(SELECT gradeNum FROM exampaper WHERE examPaperNum={errorPaperNum})   AND page={page} ");
        this.dao2._execute(sql, args);
        this.dao2._execute("update questionimage set studentId={StudentId} where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update scoreimage set studentId={StudentId} where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update studentpaperimage set studentId={StudentId} where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update score set studentId={StudentId},schoolNum={SchoolNum}  where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update examinationnumimg set studentId={StudentId},schoolNum={SchoolNum}  where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update regexaminee set studentId={StudentId},schoolNum={SchoolNum}  where studentId={sStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update cantrecognized set studentId={StudentId}  where studentId={sStudentId}   and examNum=(SELECT examNum FROM exampaper WHERE examPaperNum={errorPaperNum})  AND subjectNum=(SELECT subjectNum FROM exampaper WHERE examPaperNum={errorPaperNum})  AND gradeNum=(SELECT gradeNum FROM exampaper WHERE examPaperNum={errorPaperNum})   AND page={page} ", args);
    }

    public void modifyError2(String eStudentId, ExamineeNumError examineeNumError, String errorPaperNum, int page) {
        new ArrayList();
        this.log.info("modifyError222---sql2--update questionimage set studentId={StudentId} where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page} sql3--update scoreimage set studentId={StudentId} where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page} sq4l--update studentpaperimage set studentId={StudentId} where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page} sql5--update score set studentId={StudentId},schoolNum={SchoolNum}   where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page} sq6: update examinationnumimg set studentId={StudentId},schoolNum={SchoolNum}  where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page}  sql7: update regexaminee set studentId={StudentId},schoolNum={SchoolNum}  where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page}  sql8: update cantrecognized set studentId={StudentId}  where studentId={eStudentId}   and examNum=(SELECT examNum FROM exampaper WHERE examPaperNum={errorPaperNum})  AND subjectNum=(SELECT subjectNum FROM exampaper WHERE examPaperNum={errorPaperNum})  AND gradeNum=(SELECT gradeNum FROM exampaper WHERE examPaperNum={errorPaperNum})  AND page={page} ");
        Map args = StreamMap.create().put("StudentId", (Object) examineeNumError.getStudentId()).put("eStudentId", (Object) eStudentId).put("errorPaperNum", (Object) errorPaperNum).put("page", (Object) Integer.valueOf(page)).put("SchoolNum", (Object) examineeNumError.getSchoolNum());
        this.dao2._execute("update questionimage set studentId={StudentId} where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update scoreimage set studentId={StudentId} where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update studentpaperimage set studentId={StudentId} where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update score set studentId={StudentId},schoolNum={SchoolNum}   where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update examinationnumimg set studentId={StudentId},schoolNum={SchoolNum}  where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update regexaminee set studentId={StudentId},schoolNum={SchoolNum}  where studentId={eStudentId} and examPaperNum={errorPaperNum} and page={page} ", args);
        this.dao2._execute("update cantrecognized set studentId={StudentId}  where studentId={eStudentId}   and examNum=(SELECT examNum FROM exampaper WHERE examPaperNum={errorPaperNum})  AND subjectNum=(SELECT subjectNum FROM exampaper WHERE examPaperNum={errorPaperNum})  AND gradeNum=(SELECT gradeNum FROM exampaper WHERE examPaperNum={errorPaperNum})  AND page={page} ", args);
    }

    public List<RegExaminee> getPageByPosAndNegMark(String exampaperNum, String oldstudentId, String posAndNegMark, String oldExamroomId, String schoolNum, String examroomornot) {
        String sql = "SELECT *  FROM regexaminee   WHERE  1=1    ";
        if (null != examroomornot && "0".equals(examroomornot) && null != oldExamroomId && !"".equals(oldExamroomId)) {
            sql = sql + "AND examinationRoomNum={oldExamroomId}   ";
        }
        Map args = StreamMap.create().put("oldExamroomId", (Object) oldExamroomId).put("exampaperNum", (Object) exampaperNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("posAndNegMark", (Object) posAndNegMark);
        List _queryBeanList = this.dao2._queryBeanList(sql + "AND examPaperNum={exampaperNum}   AND schoolNum={schoolNum}  AND cNum={posAndNegMark}    ORDER BY studentId  ", RegExaminee.class, args);
        if (null != _queryBeanList && _queryBeanList.size() >= 2) {
            return _queryBeanList;
        }
        return null;
    }

    public String getCNumByRegList(String exampaperNum, String studentId, String examRoomId, List regList, String testCenter, String examroomornot) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("examRoomId", examRoomId);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("testCenter", testCenter);
        boolean schFlag = true;
        String sql = "SELECT  cNum   FROM regexaminee    WHERE  examPaperNum={exampaperNum}   ";
        if (null != examroomornot && "0".equals(examroomornot) && null != examRoomId && !"".equals(examRoomId)) {
            sql = sql + "AND examinationRoomNum={examRoomId}  ";
            schFlag = false;
        }
        String sql2 = sql + "AND studentId={studentId}   ";
        int i = 0;
        while (true) {
            if (i >= regList.size()) {
                break;
            }
            String Page = "page" + i;
            args.put(Page, Integer.valueOf(((RegExaminee) regList.get(i)).getPage()));
            if (i != 0) {
                i++;
            } else {
                sql2 = sql2 + "AND page={" + Page + "} ";
                break;
            }
        }
        if (schFlag) {
            sql2 = sql2 + "   AND testingCentreId={testCenter}     ";
        }
        List<?> _queryBeanList = this.dao2._queryBeanList(sql2, RegExaminee.class, args);
        if (null != _queryBeanList && _queryBeanList.size() > 0) {
            return ((RegExaminee) _queryBeanList.get(0)).getcNum() + "";
        }
        return null;
    }

    public Integer modifyExamroomAndExamineeNum(String exampaperNum, String testCenter, String newExamroomNum, String oldstudentId, String newStudentId, int page, List regList, String cNum, String examroomornot, String curRegId, String newScannum, String inputExamineeNum) {
        if (null != cNum) {
        }
        Map args = new HashMap();
        args.put("page", Integer.valueOf(page));
        args.put("cNum", cNum);
        args.put("newScannum", newScannum);
        args.put("newStudentId", newStudentId);
        args.put("newExamroomNum", newExamroomNum);
        args.put("curRegId", curRegId);
        args.put("exampaperNum", exampaperNum);
        args.put("testCenter", testCenter);
        List<String> sqls = new ArrayList<>();
        String newScannumStr = null == newScannum ? "" : ",scannum={newScannum} ";
        String sql1 = "UPDATE regexaminee SET studentId={newStudentId},examinationRoomNum={newExamroomNum},isModify='T'" + newScannumStr + "  WHERE id={curRegId} ";
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT t.scoreId from task t INNER JOIN score s on t.scoreId=s.id WHERE s.regId={curRegId} ", Task.class, args);
        for (int k = 0; k < _queryBeanList.size(); k++) {
            String sscoreId = ((Task) _queryBeanList.get(k)).getScoreId();
            String sscoreId1 = "sscoreid1" + k;
            args.put(sscoreId1, sscoreId);
            sqls.add("update task set studentId = {newStudentId} ,insertUser=if(`status`='F','-1',insertUser) where scoreId={" + sscoreId1 + "}  ");
            sqls.add("update tag  set studentId = {newStudentId}  where scoreId={" + sscoreId1 + "} ");
        }
        sqls.add(sql1);
        sqls.add("UPDATE score SET studentId={newStudentId},examinationRoomNum={newExamroomNum}  WHERE regId={curRegId} ");
        sqls.add("UPDATE objectivescore SET studentId={newStudentId} ,examinationRoomNum={newExamroomNum}  WHERE regId={curRegId} ");
        sqls.add("UPDATE cantrecognized  SET examinationRoomNum={newExamroomNum}  WHERE regId={curRegId} ");
        sqls.add("UPDATE score  s  LEFT JOIN student st ON st.id = s.studentId  SET s.classNum = IFNULL(st.classNum,-1),s.schoolNum = IFNULL(st.schoolNum,s.schoolNum)  where s.regId={curRegId} ");
        sqls.add("UPDATE objectivescore  s  LEFT JOIN student st ON st.id = s.studentId  SET s.classNum = IFNULL(st.classNum,-1),s.schoolNum = IFNULL(st.schoolNum,s.schoolNum)  where s.regId={curRegId} ");
        sqls.add("UPDATE regexaminee  s  LEFT JOIN student st ON st.id = s.studentId  SET s.classNum = IFNULL(st.classNum,-1),s.schoolNum = IFNULL(st.schoolNum,s.schoolNum)  where s.id={curRegId} ");
        sqls.add("UPDATE tag  s  LEFT JOIN student st ON st.id = s.studentId  SET s.classNum = IFNULL(st.classNum,-1) ,s.schoolNum = IFNULL(st.schoolNum,s.schoolNum)   where s.examPaperNum={exampaperNum} AND s.studentId={newStudentId} ");
        sqls.add("DELETE FROM examineenumerror where regId={curRegId} ");
        this.log.info("method:modifyExamroomAndExamineeNum---sql1: " + sql1 + ",sql2:UPDATE score SET studentId={newStudentId},examinationRoomNum={newExamroomNum}  WHERE regId={curRegId} ,sql3:UPDATE objectivescore SET studentId={newStudentId} ,examinationRoomNum={newExamroomNum}  WHERE regId={curRegId} ,sql4:UPDATE cantrecognized  SET examinationRoomNum={newExamroomNum}  WHERE regId={curRegId} ,sql5:");
        this.log.info("method:modifyExamroomAndExamineeNum---sql6: UPDATE score  s  LEFT JOIN student st ON st.id = s.studentId  SET s.classNum = IFNULL(st.classNum,-1),s.schoolNum = IFNULL(st.schoolNum,s.schoolNum)  where s.regId={curRegId} ,sql7:UPDATE objectivescore  s  LEFT JOIN student st ON st.id = s.studentId  SET s.classNum = IFNULL(st.classNum,-1),s.schoolNum = IFNULL(st.schoolNum,s.schoolNum)  where s.regId={curRegId} ,sql8:UPDATE regexaminee  s  LEFT JOIN student st ON st.id = s.studentId  SET s.classNum = IFNULL(st.classNum,-1),s.schoolNum = IFNULL(st.schoolNum,s.schoolNum)  where s.id={curRegId} ,sql9:UPDATE tag  s  LEFT JOIN student st ON st.id = s.studentId  SET s.classNum = IFNULL(st.classNum,-1) ,s.schoolNum = IFNULL(st.schoolNum,s.schoolNum)   where s.examPaperNum={exampaperNum} AND s.studentId={newStudentId} ,deleteSql:DELETE FROM examineenumerror where regId={curRegId} ");
        if (page == 1) {
            String schoolNum = this.dao2._queryStr("SELECT schoolNum FROM student WHERE id = {newStudentId} ", args);
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
            sqls.add("UPDATE illegal ill SET ill.schoolNum = {schoolNum},ill.studentId={newStudentId},ill.examinationRoomNum={newExamroomNum}, ill.testingCentreId={testCenter} WHERE ill.regId={curRegId} ");
            this.log.info("method:modifyExamroomAndExamineeNum---sql10: UPDATE illegal ill SET ill.schoolNum = {schoolNum},ill.studentId={newStudentId},ill.examinationRoomNum={newExamroomNum}, ill.testingCentreId={testCenter} WHERE ill.regId={curRegId} ");
        }
        this.dao2._batchExecute(sqls, args);
        this.dao2._execute("call updateChooseQuestionByStudentid({exampaperNum},{newStudentId})", args);
        return 1;
    }

    public Integer mergeQuestion(String exampaperNum, String school, String newExamroomNum, String oldstudentId, String newStudentId, int page, List regList, String cNum, String examroomornot) {
        String str;
        List<String> s_deleteList = new ArrayList<>();
        List<String> o_deleteList = new ArrayList<>();
        Map args = new HashMap();
        args.put("page", Integer.valueOf(page));
        args.put("newStudentId", newStudentId);
        args.put("exampaperNum", exampaperNum);
        args.put(License.SCHOOL, school);
        String stuIdStr = "  AND studentId={newStudentId}   ";
        if (null != regList && regList.size() > 0) {
            String pageStr0 = "  AND  (page={page}   ";
            String stuIdStr2 = " \tAND  (studentId={newStudentId}   ";
            for (int i = 0; i < regList.size(); i++) {
                String Page1 = "Page1" + i;
                String StudentId1 = "StudentId1" + i;
                args.put(Page1, Integer.valueOf(((RegExaminee) regList.get(i)).getPage()));
                args.put(StudentId1, ((RegExaminee) regList.get(i)).getStudentId());
                pageStr0 = pageStr0 + " OR  page={" + Page1 + "} ";
                stuIdStr2 = stuIdStr2 + " \tOR  studentId={" + StudentId1 + "} ";
            }
            String str2 = pageStr0 + "  )  ";
            stuIdStr = stuIdStr2 + "  )  ";
        }
        String qNumListSql = "SELECT  a.questionNum,COUNT(a.questionNum),MAX(a.page) ,GROUP_CONCAT(a.page) ext3   FROM(SELECT questionNum,page FROM score WHERE  exampaperNum={exampaperNum}  AND schoolNum={school} " + stuIdStr + "UNION    SELECT questionNum,page FROM objectivescore WHERE  exampaperNum={exampaperNum}  AND schoolNum={school}  " + stuIdStr + ")a  GROUP BY a.questionNum  HAVING COUNT(a.questionNum)>1";
        List qNumList = this.dao2._queryBeanList(qNumListSql, Score.class, args);
        if (null != qNumList && qNumList.size() > 0) {
            for (int i2 = 0; i2 < qNumList.size(); i2++) {
                String qNum = ((Score) qNumList.get(i2)).getQuestionNum();
                String qnums = "qNum" + i2;
                String qPages = "qPage" + i2;
                int qPage = ((Score) qNumList.get(i2)).getPage();
                args.put(qPages, Integer.valueOf(qPage));
                String pageStr = ((Score) qNumList.get(i2)).getExt3();
                args.put(qnums, qNum);
                String pageStr02 = "AND page={" + qPages + "} ";
                if (pageStr.contains(Const.STRING_SEPERATOR)) {
                    Set set = new HashSet();
                    for (String str3 : pageStr.split(Const.STRING_SEPERATOR)) {
                        set.add(str3);
                    }
                    String[] pageArr = new String[set.size()];
                    set.toArray(pageArr);
                    Arrays.sort(pageArr);
                    if (pageArr.length > 2) {
                        String pageStr03 = "  AND  (  ";
                        for (int j = 1; j < pageArr.length; j++) {
                            if (j == 1) {
                                str = pageStr03 + "  page=" + pageArr[j];
                            } else {
                                str = pageStr03 + " OR  page=" + pageArr[j];
                            }
                            pageStr03 = str;
                        }
                        pageStr02 = pageStr03 + "  )  ";
                    }
                }
                String score_sql = "SELECT  DISTINCT studentId,page,questionNum   FROM score  WHERE examPaperNum={exampaperNum}  AND schoolNum={school}   AND questionNum={" + qnums + "}   " + pageStr02 + stuIdStr;
                List<?> _queryBeanList = this.dao2._queryBeanList(score_sql, Score.class, args);
                if (null != _queryBeanList && _queryBeanList.size() > 0) {
                    for (int j2 = 0; j2 < _queryBeanList.size(); j2++) {
                        String Page2 = "Page2" + i2;
                        String StudentId2 = "StudentId2" + i2;
                        args.put(StudentId2, ((Score) _queryBeanList.get(j2)).getStudentId());
                        args.put(Page2, Integer.valueOf(((Score) _queryBeanList.get(j2)).getPage()));
                        String s_delqNumSql = "DELETE FROM score WHERE  exampaperNum={exampaperNum}  AND schoolNum={school}  AND  studentId={" + StudentId2 + "}  AND questionNum={" + qnums + "}  AND page={" + Page2 + "} ";
                        this.log.info("score 表续题合并：" + s_delqNumSql);
                        s_deleteList.add(s_delqNumSql);
                    }
                }
                String objectivescore_sql = "SELECT  DISTINCT studentId,page,questionNum   FROM objectivescore  WHERE examPaperNum={exampaperNum}  AND schoolNum={school}   AND questionNum={" + qnums + "}  " + pageStr02 + stuIdStr;
                List<?> _queryBeanList2 = this.dao2._queryBeanList(objectivescore_sql, ObjectiveScore.class, args);
                if (null != _queryBeanList2 && _queryBeanList2.size() > 0) {
                    for (int j3 = 0; j3 < _queryBeanList2.size(); j3++) {
                        String Page3 = "Page3" + i2;
                        String StudentId3 = "StudentId3" + i2;
                        args.put(StudentId3, ((ObjectiveScore) _queryBeanList2.get(j3)).getStudentId());
                        args.put(Page3, Integer.valueOf(((ObjectiveScore) _queryBeanList2.get(j3)).getPage()));
                        String o_delqNumSql = "DELETE FROM objectivescore WHERE  exampaperNum={exampaperNum}  AND schoolNum={school}  AND studentId={StudentId3}  AND questionNum={" + qnums + "}  AND page={Page3} ";
                        this.log.info("objectivescore 表续题合并：" + o_delqNumSql);
                        o_deleteList.add(o_delqNumSql);
                    }
                }
            }
        }
        if (null != o_deleteList && o_deleteList.size() > 0) {
            this.dao2._batchExecute(o_deleteList, args);
        }
        if (null != s_deleteList && s_deleteList.size() > 0) {
            this.dao2._batchExecute(s_deleteList, args);
        }
        return 0;
    }

    public Integer mergeQuestion_task(String exampaperNum, String school, String newExamroomNum, String oldstudentId, String newStudentId, int page, List regList, String cNum, String examroomornot) {
        String str;
        Map args = new HashMap();
        args.put("page", Integer.valueOf(page));
        args.put("newStudentId", newStudentId);
        args.put("exampaperNum", exampaperNum);
        args.put(License.SCHOOL, school);
        String oldRoomId = "";
        String stuIdStr = "  AND studentId={newStudentId}   ";
        if (null != regList && regList.size() > 0) {
            oldRoomId = ((RegExaminee) regList.get(0)).getExaminationRoomNum() + "";
            args.put("oldRoomId", oldRoomId);
            String pageStr0 = "  AND  (page={page}   ";
            String stuIdStr2 = " \tAND  (studentId={newStudentId}   ";
            for (int i = 0; i < regList.size(); i++) {
                String StudentId1 = "StudentId1" + i;
                String Page1 = "Page1" + i;
                args.put(StudentId1, ((RegExaminee) regList.get(i)).getStudentId());
                args.put(Page1, Integer.valueOf(((RegExaminee) regList.get(i)).getPage()));
                pageStr0 = pageStr0 + " OR  page={" + Page1 + "} ";
                stuIdStr2 = stuIdStr2 + " \tOR  studentId={" + StudentId1 + "}  ";
            }
            String str2 = pageStr0 + "  )  ";
            stuIdStr = stuIdStr2 + "  )  ";
        }
        String qNumListSql = "SELECT  qms.makType,t.questionNum,COUNT(t.questionNum) ext1,MAX(t.page) page,GROUP_CONCAT(t.page) ext3   FROM (SELECT t.id,sc.id scoreId,sc.examPaperNum,sc.studentId,sc.page,sc.questionNum,t.groupNum   FROM(SELECT id,studentId,examPaperNum,questionNum,page FROM score  WHERE  exampaperNum={exampaperNum} AND schoolNum={school}   ";
        if (!oldRoomId.equals("")) {
            qNumListSql = qNumListSql + "AND examinationRoomNum={oldRoomId}  ";
        }
        String qNumListSql2 = qNumListSql + stuIdStr + "UNION   SELECT id,studentId,examPaperNum,questionNum,page FROM objectivescore    WHERE  exampaperNum={exampaperNum} AND schoolNum={school}  ";
        if (!oldRoomId.equals("")) {
            qNumListSql2 = qNumListSql2 + "AND examinationRoomNum={oldRoomId}  ";
        }
        List qNumList = this.dao2._queryBeanList(qNumListSql2 + stuIdStr + ") sc  LEFT JOIN(SELECT id,scoreId,exampaperNum,questionNum,groupNum   FROM task WHERE exampaperNum={exampaperNum} )   ON sc.id=t.scoreId) t  LEFT JOIN (SELECT questionGroupNum,makType,exampaperNum,id   FROM questiongroup_mark_setting WHERE exampaperNum={exampaperNum}   ) qms   ON t.exampaperNum=qms.exampaperNum AND t.groupNum=qms.questionGroupNum   GROUP BY t.questionNum   HAVING COUNT(t.questionNum)>1   ORDER BY t.questionNum", Task.class, args);
        if (null != qNumList && qNumList.size() > 0) {
            for (int i2 = 0; i2 < qNumList.size(); i2++) {
                String qNum = ((Task) qNumList.get(i2)).getQuestionNum().toString();
                int qPage = ((Task) qNumList.get(i2)).getPage();
                String qnums = "qNum" + i2;
                String qPages = "qPage" + i2;
                args.put(qnums, qNum);
                args.put(qPages, Integer.valueOf(qPage));
                String makTp = ((Task) qNumList.get(i2)).getMakType();
                int qCount = (int) Float.parseFloat(((Task) qNumList.get(i2)).getExt1());
                String pageStr = ((Task) qNumList.get(i2)).getExt3();
                String pageStr02 = "AND page={" + qPage + "} ";
                if (pageStr.contains(Const.STRING_SEPERATOR)) {
                    Set set = new HashSet();
                    for (String str3 : pageStr.split(Const.STRING_SEPERATOR)) {
                        set.add(str3);
                    }
                    String[] pageArr = new String[set.size()];
                    set.toArray(pageArr);
                    Arrays.sort(pageArr);
                    if (pageArr.length > 2) {
                        String pageStr03 = "  AND  (  ";
                        for (int j = 1; j < pageArr.length; j++) {
                            String pagea = "pagea" + j;
                            args.put(pagea, pageArr[j]);
                            if (j == 1) {
                                str = pageStr03 + "  page={" + pagea + "} ";
                            } else {
                                str = pageStr03 + " OR  page={" + pagea + "} ";
                            }
                            pageStr03 = str;
                        }
                        pageStr02 = pageStr03 + "  )  ";
                    }
                }
                if ((makTp.equals("0") && qCount > 1) || (makTp.equals("1") && qCount > 2)) {
                    String task_sql = "SELECT  *  FROM task  WHERE examPaperNum={exampaperNum}  AND schoolNum={school}   AND questionNum={" + qnums + "}    " + stuIdStr + pageStr02;
                    List<?> _queryBeanList = this.dao2._queryBeanList(task_sql, Task.class, args);
                    if (null != _queryBeanList && _queryBeanList.size() > 0) {
                        List<String> s_deleteList = new ArrayList<>();
                        for (int j2 = 0; j2 < _queryBeanList.size(); j2++) {
                            String StudentId2 = "StudentId2" + j2;
                            String Page2 = "Page2" + j2;
                            args.put(StudentId2, ((Task) _queryBeanList.get(j2)).getStudentId());
                            args.put(Page2, Integer.valueOf(((Task) _queryBeanList.get(j2)).getPage()));
                            String s_delqNumSql = "DELETE FROM task WHERE studentId={" + StudentId2 + "}  AND exampaperNum={exampaperNum}  AND schoolNum={school} AND questionNum={" + qnums + "}   AND page={" + Page2 + "} ";
                            this.log.info("task 表续题合并：" + s_delqNumSql);
                            s_deleteList.add(s_delqNumSql);
                            if (j2 == _queryBeanList.size()) {
                                if (null != s_deleteList && s_deleteList.size() > 0) {
                                    this.dao2._batchExecute(s_deleteList, args);
                                }
                                this.questionGroupDao.updatequestiontotalnum(Integer.valueOf(Integer.parseInt(exampaperNum)), null, qNum, null, "2");
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    public Integer updateCrossPageQuestion(String exampaperNum, String school, String newExamroomNum, String oldstudentId, String newStudentId, int page, List regList, String cNum, String examroomornot) {
        return 0;
    }

    public List getStudentInfoFromStudent(String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryBeanList("select * from student where studentId={studentId} ", Student.class, args);
    }

    public List getExaminationnumObj(String id, String examinationRoomNum, String examNum, String examineeNum, String schoolNum, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("id", id);
        args.put("examineeNum", examineeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        this.log.info("查询在学生表中是否存在该学生  sql:select count(1) from student where studentId=(select studentID from examinationnum where examNum={examNum} AND examinationRoomNum = (SELECT examinationRoomNum FROM examineenumerror WHERE id = {id}) and examineeNum={examineeNum} and schoolNum={schoolNum} and gradeNum={gradeNum} )");
        this.log.info("在考号表中是否存在该考号  sql:SELECT * FROM examinationnum WHERE examnum = {examNum} AND examinationRoomNum = (SELECT examinationRoomNum FROM examineenumerror WHERE id = {id}) and examineeNum={examineeNum} and schoolNum={schoolNum} and gradeNum={gradeNum}");
        Object returnCode = this.dao2._queryObject("select count(1) from student where studentId=(select studentID from examinationnum where examNum={examNum} AND examinationRoomNum = (SELECT examinationRoomNum FROM examineenumerror WHERE id = {id}) and examineeNum={examineeNum} and schoolNum={schoolNum} and gradeNum={gradeNum} )", args);
        if (null != returnCode) {
            if (Integer.parseInt(String.valueOf(returnCode)) == 1) {
                this.log.info("daoimpl getExaminationnum（）学生表中查询，存在且只有一个此学籍号：--" + this.dao2._queryInt("select count(1) from student where studentId=(select studentID from examinationnum where examNum={examNum} AND examinationRoomNum = (SELECT examinationRoomNum FROM examineenumerror WHERE id = {id}) and examineeNum={examineeNum} and schoolNum={schoolNum} and gradeNum={gradeNum} )", args));
                return this.dao2._queryBeanList("SELECT * FROM examinationnum WHERE examnum = {examNum} AND examinationRoomNum = (SELECT examinationRoomNum FROM examineenumerror WHERE id = {id}) and examineeNum={examineeNum} and schoolNum={schoolNum} and gradeNum={gradeNum}", Examinationnum.class, args);
            }
            this.log.info("学生表中不存在此学籍号--或者有多个-");
            return null;
        }
        return null;
    }

    public Score findOneFromScore(String examPaperNum, String studentId, String schoolNum, int page) {
        this.log.info("---score--从score表中查询正确的考号对应的学生--sqlselect DISTINCT examPaperNum,studentId,insertUser,schoolNum,page,examinationRoomNum from score  where studentId={studentId} and schoolNum={schoolNum}  and examPaperNum={examPaperNum} and page={page} ");
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("examPaperNum", (Object) examPaperNum).put("page", (Object) Integer.valueOf(page));
        if (null != this.dao2._queryBean("select DISTINCT examPaperNum,studentId,insertUser,schoolNum,page,examinationRoomNum from score  where studentId={studentId} and schoolNum={schoolNum}  and examPaperNum={examPaperNum} and page={page} ", Score.class, args)) {
            return (Score) this.dao2._queryBean("select DISTINCT examPaperNum,studentId,insertUser,schoolNum,page,examinationRoomNum from score  where studentId={studentId} and schoolNum={schoolNum}  and examPaperNum={examPaperNum} and page={page} ", Score.class, args);
        }
        return null;
    }

    public List<RegExaminee> authDataExistsFromRegExamineeList(String paperType, String examPaperNum, String studentId, String testCenter, int page, String cNum, String examRoomId, List regList, String useRegListFlg, String regOldOrNewFlag, String examroomornot) {
        String sql;
        String str;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("examRoomId", examRoomId);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("cNum", cNum);
        args.put("paperType", paperType);
        String sql2 = "select DISTINCT reg.id,reg.examPaperNum,reg.studentId,reg.insertUser,reg.testingCentreId,reg.page,reg.type,reg.examinationRoomNum,reg.cNum,ep.doubleFaced,ep.examNum exam   FROM (SELECT id,examPaperNum,studentId,insertUser,testingCentreId,page,type,examinationRoomNum,cNum  FROM regexaminee WHERE   examPaperNum={examPaperNum}   ";
        if ((null == cNum || "".equals(cNum)) && null != examroomornot && "0".equals(examroomornot) && examRoomId != null && !"".equals(examRoomId)) {
            sql2 = sql2 + "AND examinationRoomNum={examRoomId}   ";
        }
        if (null != studentId && !"".equals(studentId)) {
            sql2 = sql2 + "and studentId={studentId}   ";
        }
        if (null != useRegListFlg && "T".equals(useRegListFlg)) {
            cNum = getCNumByRegList(examPaperNum, studentId, examRoomId, regList, testCenter, examroomornot);
            args.put("cNum", cNum);
        }
        String pStr = "  AND page=" + page;
        if (null != cNum && !"".equals(cNum)) {
            sql = sql2 + "AND cNum={cNum}\t";
        } else {
            if (null != regList && regList.size() > 1) {
                String pStr2 = "  AND (";
                for (int i = 0; i < regList.size(); i++) {
                    String Page1 = "Page1" + i;
                    args.put(Page1, Integer.valueOf(((RegExaminee) regList.get(i)).getPage()));
                    if (i == 0) {
                        str = pStr2 + "  page={" + Page1 + "} ";
                    } else {
                        str = pStr2 + "  OR  page={" + Page1 + "} ";
                    }
                    pStr2 = str;
                }
                pStr = pStr2 + " )   ";
            }
            sql = sql2 + pStr;
        }
        if (null != paperType && !"".equals(paperType)) {
            sql = sql + "  and type={paperType}   ";
        }
        String sql3 = sql + ") reg   LEFT JOIN (SELECT examPaperNum,doubleFaced,examNum FROM exampaper WHERE examPaperNum={examPaperNum}) ep   ON reg.examPaperNum=ep.examPaperNum   ORDER BY reg.page*1    ";
        this.log.info("authDataExistsFromRegExamineeList---sql:" + sql3);
        new ArrayList();
        List _queryBeanList = this.dao2._queryBeanList(sql3, RegExaminee.class, args);
        if (null != _queryBeanList && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    public RegExaminee authDataExistsFromRegExaminee(String paperType, String examPaperNum, String studentId, String schoolNum, int page, String cNum, String oldExamroomId, List regOldList, String flg, String f, String regId) {
        new ArrayList();
        Map args = StreamMap.create().put("regId", (Object) regId).put("examPaperNum", (Object) examPaperNum);
        List<?> _queryBeanList = this.dao2._queryBeanList("select DISTINCT reg.id,reg.examPaperNum,reg.studentId,reg.insertUser,reg.schoolNum,reg.page,reg.type,reg.examinationRoomNum,reg.cNum,ep.doubleFaced,ep.examNum exam   FROM (SELECT id,examPaperNum,studentId,insertUser,schoolNum,page,type,examinationRoomNum,cNum  FROM regexaminee WHERE   id={regId}  ) reg   LEFT JOIN (SELECT examPaperNum,doubleFaced,examNum FROM exampaper WHERE examPaperNum={examPaperNum} ) ep   ON reg.examPaperNum=ep.examPaperNum    ", RegExaminee.class, args);
        if (null != _queryBeanList && _queryBeanList.size() > 0) {
            return (RegExaminee) _queryBeanList.get(0);
        }
        return null;
    }

    public List getOneFromExamineeNumError(String id, String schoolNum, String gradeNum, String examNum) {
        Map args = StreamMap.create().put("id", (Object) id).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryBeanList("select ene.id,ene.examPaperNum,ene.studentId,ene.errorType,ene.insertUser,ene.insertDate,ene.schoolNum,ene.groupNum,ene.page,er.id examinationRoomNum,er.examinationRoomName from examineenumerror ene,examinationroom er where ene.id={id} and ene.examinationRoomNum=er.id and er.schoolNum={schoolNum} and er.gradeNum={gradeNum} and er.examnum={examNum} ", ExamineeNumError.class, args);
    }

    public ExamineeNumError getExamineeNumErrorObjById(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        new ExamineeNumError();
        ExamineeNumError examineeNumError = (ExamineeNumError) this.dao2._queryBean("select *  from examineenumerror  where id={id} ", ExamineeNumError.class, args);
        if (null != examineeNumError) {
            return examineeNumError;
        }
        return null;
    }

    public Integer deleteByExamineeNumErrorObj(ExamineeNumError examineeNumError) {
        List<RegExaminee> list;
        Map args = new HashMap();
        args.put("Page", Integer.valueOf(examineeNumError.getPage()));
        args.put("SchoolNum", examineeNumError.getSchoolNum());
        args.put("StudentId", examineeNumError.getStudentId());
        args.put("ExamPaperNum", examineeNumError.getExamPaperNum());
        args.put("ExaminationRoomNum", examineeNumError.getExaminationRoomNum());
        List<String> sqls = new ArrayList<>();
        if (null != examineeNumError) {
            String pageSql = "  AND page={Page} ";
            String pageSqlError = "  AND page={Page} ";
            if (null != examineeNumError.getGroupNum() && !examineeNumError.getGroupNum().equals("") && null != (list = getPageByPosAndNegMark(examineeNumError.getExamPaperNum().toString(), examineeNumError.getStudentId().toString(), examineeNumError.getGroupNum().toString(), examineeNumError.getExaminationRoomNum().toString(), examineeNumError.getSchoolNum().toString(), null))) {
                args.put("Page0", Integer.valueOf(list.get(0).getPage()));
                args.put("Page1", Integer.valueOf(list.get(1).getPage()));
                pageSql = "  AND (page={Page0} OR page={Page1}) ";
                pageSqlError = "  AND groupNum='" + examineeNumError.getGroupNum() + "'     AND (page={Page0} OR page={Page1}) ";
            }
            String sql = "DELETE FROM examineenumerror WHERE schoolNum={SchoolNum}  AND studentId={StudentId}  AND examPaperNum={ExamPaperNum}  " + pageSqlError;
            String sql2 = "DELETE FROM questionimage WHERE schoolNum={SchoolNum}  AND studentId={StudentId}  AND examPaperNum={ExamPaperNum}  " + pageSql;
            String sql3 = "DELETE FROM scoreimage WHERE schoolNum={SchoolNum}  AND studentId={StudentId}  AND examPaperNum={ExamPaperNum}  " + pageSql;
            String sql4 = "DELETE FROM studentpaperimage WHERE schoolNum={SchoolNum}  AND studentId={StudentId}  AND examPaperNum={ExamPaperNum}   " + pageSql;
            String sql5 = "DELETE FROM score WHERE  schoolNum={SchoolNum}  AND studentId={StudentId}  AND examPaperNum={ExamPaperNum}   " + pageSql + "     AND examinationRoomNum={ExaminationRoomNum} ";
            String sql6 = "DELETE FROM examinationnumimg WHERE  schoolNum={SchoolNum}  AND studentId={StudentId}  AND examPaperNum={ExamPaperNum}   " + pageSql + "  AND examinationRoomNum={ExaminationRoomNum} ";
            String sql7 = "DELETE FROM regexaminee WHERE  schoolNum={SchoolNum}  AND studentId={StudentId}  AND examPaperNum={ExamPaperNum}    " + pageSql + "  AND examinationRoomNum={ExaminationRoomNum} ";
            String sql8 = "DELETE FROM tag WHERE  schoolNum={SchoolNum}  AND studentId={StudentId}  AND examPaperNum={ExamPaperNum}    " + pageSql + "  AND examinationRoomNum={ExaminationRoomNum} ";
            String sql9 = "DELETE FROM task WHERE  schoolNum={SchoolNum}  AND studentId={StudentId}  AND examPaperNum={ExamPaperNum}    " + pageSql + "  AND examinationRoomNum={ExaminationRoomNum} ";
            sqls.add(sql2);
            sqls.add(sql3);
            sqls.add(sql4);
            sqls.add(sql5);
            sqls.add(sql6);
            sqls.add(sql7);
            sqls.add(sql8);
            sqls.add(sql9);
            sqls.add(sql);
            this.log.info("sql---" + sql);
            this.log.info("sql2---" + sql2);
            this.log.info("sql3---" + sql3);
            this.log.info("sql4---" + sql4);
            this.log.info("sql5---" + sql5);
            this.log.info("sql6---" + sql6);
            this.dao2._batchExecute(sqls, args);
            return 1;
        }
        return null;
    }

    public Integer addOne(Object obj) {
        return Integer.valueOf(this.dao2.save(obj));
    }

    public void addObjList(List oList) {
        this.dao2.batchSave(oList);
    }

    public String getExcelFileNameByNum(String schoolNum, String examNum, String gradeNum, String classNum, String subjectNum, String radioValue, String studentType, String levelclass, String sjt) {
        Object obj;
        String excelName = "";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        if (null != examNum && !"".equals(examNum) && null != this.dao2._queryBean("select examName from exam where examNum={examNum} ", Exam.class, args)) {
            excelName = excelName + ((Exam) this.dao2._queryBean("select examName from exam where examNum={examNum} ", Exam.class, args)).getExamName() + "_";
        }
        if (null != schoolNum && !"-1".equals(schoolNum) && !"".equals(schoolNum) && null != this.dao2._queryBean("select schoolName from school where id={schoolNum}", School.class, args)) {
            excelName = excelName + ((School) this.dao2._queryBean("select schoolName from school where id={schoolNum}", School.class, args)).getSchoolName() + "_";
        }
        if (null != gradeNum && !"".equals(gradeNum) && null != this.dao2._queryBean("select gradeName    from basegrade    where gradeNum={gradeNum} ", Basegrade.class, args)) {
            excelName = excelName + ((Basegrade) this.dao2._queryBean("select gradeName    from basegrade    where gradeNum={gradeNum} ", Basegrade.class, args)).getGradeName() + "_";
        }
        if ("1".equals(radioValue)) {
            if (null != classNum && !"-1".equals(classNum) && !"".equals(classNum)) {
                String sql4 = "select className    from class where  id={classNum}   ";
                if (null != levelclass && levelclass.equals("T") && sjt.length() > 2) {
                    sql4 = "select className    from levelclass where  id={classNum}  limit 1 ";
                }
                Object obj2 = this.dao2._queryBean(sql4, Class.class, args);
                if (null != obj2) {
                    excelName = excelName + ((Class) obj2).getClassName() + "_";
                }
            } else {
                excelName = excelName + "全部班级_";
            }
        }
        if ("2".equals(radioValue)) {
            if (null != classNum && !"-1".equals(classNum) && !"".equals(classNum)) {
                Object obj3 = this.dao2._queryBean("SELECT  examinationRoomName    FROM examinationroom    WHERE id={classNum} ", Examinationroom.class, args);
                if (null != obj3) {
                    excelName = excelName + ((Examinationroom) obj3).getExaminationRoomName() + "_";
                }
            } else {
                excelName = excelName + "全部考场_";
            }
        }
        if (null != subjectNum && !"".equals(subjectNum) && null != (obj = this.dao2._queryBean("select subjectName FROM `subject` WHERE subjectNum={subjectNum} ", Subject.class, args))) {
            excelName = excelName + ((Subject) obj).getSubjectName() + "_";
        }
        if (null != studentType && !"".equals(studentType) && !"0".equals(studentType)) {
            excelName = excelName + querySubjectTypeName(studentType) + "_";
        }
        if (excelName.endsWith("_")) {
            excelName = excelName.substring(0, excelName.length() - 1);
        }
        return excelName;
    }

    public String getExcelFileNameByNum2(String schoolNum, String examNum, String gradeNum, String classNum, String subjectNum, String radioValue, String studentType, String levelclass, String sjt, String subCompose) {
        Object obj;
        String excelName = "";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        if (null != examNum && !"".equals(examNum) && null != this.dao2._queryBean("select examName from exam where examNum={examNum} ", Exam.class, args)) {
            excelName = excelName + ((Exam) this.dao2._queryBean("select examName from exam where examNum={examNum} ", Exam.class, args)).getExamName() + "_";
        }
        String excelName2 = excelName + schoolNum + "_";
        if (null != gradeNum && !"".equals(gradeNum) && null != this.dao2._queryBean("select gradeName    from basegrade    where gradeNum={gradeNum} ", Basegrade.class, args)) {
            excelName2 = excelName2 + ((Basegrade) this.dao2._queryBean("select gradeName    from basegrade    where gradeNum={gradeNum} ", Basegrade.class, args)).getGradeName() + "_";
        }
        if ("1".equals(radioValue)) {
            if (null != classNum && !"-1".equals(classNum) && !"".equals(classNum)) {
                String sql4 = "select className    from class where  id={classNum}   ";
                if (null != levelclass && levelclass.equals("T") && sjt.length() > 2) {
                    sql4 = "select className    from levelclass where  id={classNum}  limit 1 ";
                }
                Object obj2 = this.dao2._queryBean(sql4, Class.class, args);
                if (null != obj2) {
                    excelName2 = excelName2 + ((Class) obj2).getClassName() + "_";
                }
            } else {
                excelName2 = excelName2 + "全部班级_";
            }
        }
        if ("2".equals(radioValue)) {
            if (null != classNum && !"-1".equals(classNum) && !"".equals(classNum)) {
                Object obj3 = this.dao2._queryBean("SELECT  examinationRoomName    FROM examinationroom    WHERE id={classNum} ", Examinationroom.class, args);
                if (null != obj3) {
                    excelName2 = excelName2 + ((Examinationroom) obj3).getExaminationRoomName() + "_";
                }
            } else {
                excelName2 = excelName2 + "全部考场_";
            }
        }
        if (null != subjectNum && !"".equals(subjectNum) && null != (obj = this.dao2._queryBean("select subjectName FROM `subject` WHERE subjectNum={subjectNum} ", Subject.class, args))) {
            excelName2 = excelName2 + ((Subject) obj).getSubjectName() + "_";
        }
        if (null != studentType && !"".equals(studentType) && !"0".equals(studentType)) {
            excelName2 = excelName2 + querySubjectTypeName(studentType) + "_";
        }
        if (null != subCompose && !"".equals(subCompose) && !"0".equals(subCompose)) {
            excelName2 = excelName2 + querysubComposeName(subCompose) + "_";
        }
        if (excelName2.endsWith("_")) {
            excelName2 = excelName2.substring(0, excelName2.length() - 1);
        }
        return excelName2;
    }

    public String getExcelFileNameByNum3(String schoolNum, String examNum, String gradeNum, String classNum, String subjectNum, String radioValue, String studentType, String levelclass, String sjt, String subCompose) {
        String excelName;
        String excelName2 = "";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        if (null != examNum && !"".equals(examNum) && null != this.dao2._queryBean("select examName from exam where examNum={examNum} ", Exam.class, args)) {
            excelName2 = excelName2 + ((Exam) this.dao2._queryBean("select examName from exam where examNum={examNum} ", Exam.class, args)).getExamName() + "_";
        }
        if (null != subCompose && !"".equals(subCompose) && !"0".equals(subCompose)) {
            excelName = excelName2 + querysubComposeName(subCompose) + "_";
        } else {
            excelName = excelName2 + "全部_";
        }
        if (null != schoolNum && !"-1".equals(schoolNum) && !"".equals(schoolNum) && null != this.dao2._queryBean("select schoolName from school where id={schoolNum}", School.class, args)) {
            excelName = excelName + ((School) this.dao2._queryBean("select schoolName from school where id={schoolNum}", School.class, args)).getSchoolName() + "_";
        }
        if (null != gradeNum && !"".equals(gradeNum) && null != this.dao2._queryBean("select gradeName    from basegrade    where gradeNum={gradeNum} ", Basegrade.class, args)) {
            excelName = excelName + ((Basegrade) this.dao2._queryBean("select gradeName    from basegrade    where gradeNum={gradeNum} ", Basegrade.class, args)).getGradeName() + "_";
        }
        if ("1".equals(radioValue)) {
            if (null != classNum && !"-1".equals(classNum) && !"".equals(classNum)) {
                if (Const.class_grade.equals(classNum)) {
                    excelName = excelName + "全部班级_";
                } else {
                    String sql4 = "select className    from class where  id={classNum}   ";
                    if (null != levelclass && levelclass.equals("T") && sjt.length() > 2) {
                        sql4 = "select className    from levelclass where  id={classNum}  limit 1 ";
                    }
                    Object obj = this.dao2._queryBean(sql4, Class.class, args);
                    if (null != obj) {
                        excelName = excelName + ((Class) obj).getClassName() + "_";
                    }
                }
            } else {
                excelName = excelName + "全部班级_";
            }
        }
        if ("2".equals(radioValue)) {
            if (null != classNum && !"-1".equals(classNum) && !"".equals(classNum)) {
                Object obj2 = this.dao2._queryBean("SELECT  examinationRoomName    FROM examinationroom    WHERE id={classNum} ", Examinationroom.class, args);
                if (null != obj2) {
                    excelName = excelName + ((Examinationroom) obj2).getExaminationRoomName() + "_";
                }
            } else {
                excelName = excelName + "全部考场_";
            }
        }
        if (null != subjectNum && !"".equals(subjectNum)) {
            if (Const.all_subject.equals(subjectNum)) {
                excelName = excelName + "全部科目_";
            } else {
                Object obj3 = this.dao2._queryBean("select subjectName FROM `subject` WHERE subjectNum={subjectNum} ", Subject.class, args);
                if (null != obj3) {
                    excelName = excelName + ((Subject) obj3).getSubjectName() + "_";
                }
            }
        }
        if (null != studentType && !"".equals(studentType) && !"0".equals(studentType)) {
            excelName = excelName + querySubjectTypeName(studentType) + "_";
        }
        if (excelName.endsWith("_")) {
            excelName = excelName.substring(0, excelName.length() - 1);
        }
        return excelName;
    }

    public String querysubComposeName(String subCompose) {
        Map args = StreamMap.create().put("subCompose", (Object) subCompose);
        return this.dao2._queryStr("select subjectCombineName from subjectcombine where subjectCombineNum={subCompose}", args);
    }

    public String querySubjectTypeName(String subjectType) {
        String subTypeName = null;
        Map args = StreamMap.create().put("data_subjectType", (Object) Const.data_subjectType);
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT id,category,isDefault,type,`name`,`value`,orderNum,isLock,isDelete  FROM `data` WHERE  type={data_subjectType} ", Data.class, args);
        if (null != _queryBeanList && _queryBeanList.size() > 0) {
            int i = 0;
            while (true) {
                if (i >= _queryBeanList.size()) {
                    break;
                }
                if (!subjectType.equals(((Data) _queryBeanList.get(i)).getValue())) {
                    i++;
                } else {
                    subTypeName = ((Data) _queryBeanList.get(i)).getName();
                    break;
                }
            }
        }
        return subTypeName;
    }

    public String queryStuTypeName(String stuType) {
        String stuTypeName = null;
        if (null != stuType && "0".equals(stuType)) {
            return "";
        }
        Map args = StreamMap.create().put("Data_stuDetailType", (Object) "22");
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT id,category,isDefault,type,`name`,`value`,orderNum,isLock,isDelete  FROM `data` WHERE  type={Data_stuDetailType} ", Data.class, args);
        if (null != _queryBeanList && _queryBeanList.size() > 0) {
            int i = 0;
            while (true) {
                if (i >= _queryBeanList.size()) {
                    break;
                }
                if (!stuType.equals(((Data) _queryBeanList.get(i)).getValue())) {
                    i++;
                } else {
                    stuTypeName = ((Data) _queryBeanList.get(i)).getName();
                    break;
                }
            }
        }
        return stuTypeName;
    }

    public Integer insertIntoClassLevel(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        int count = 0;
        this.log.info("--sql2-插入classlevel表时，查询试卷科目-SELECT *  FROM exampaper  WHERE examPaperNum IN(SELECT DISTINCT examPaperNum FROM score )  AND examNum={examNum} ");
        if (null != this.dao2._queryBeanList("SELECT DISTINCT schoolNum,gradeNum FROM score ", Grade.class, args)) {
            List<?> _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT schoolNum,gradeNum FROM score ", Grade.class, args);
            for (int j = 0; j < _queryBeanList.size(); j++) {
                if (null != this.dao2._queryBeanList("SELECT *  FROM exampaper  WHERE examPaperNum IN(SELECT DISTINCT examPaperNum FROM score )  AND examNum={examNum} ", Exampaper.class, args)) {
                    List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT *  FROM exampaper  WHERE examPaperNum IN(SELECT DISTINCT examPaperNum FROM score )  AND examNum={examNum} ", Exampaper.class, args);
                    for (int i = 0; i < _queryBeanList2.size(); i++) {
                        String SchoolNum = "SchoolNum" + i;
                        String GradeNum = "GradeNum" + i;
                        String qExamPaperNum = "qExamPaperNum" + i;
                        args.put(SchoolNum, ((Grade) _queryBeanList.get(j)).getSchoolNum());
                        args.put(GradeNum, ((Grade) _queryBeanList.get(j)).getGradeNum());
                        args.put(qExamPaperNum, ((Exampaper) _queryBeanList2.get(i)).getExamPaperNum());
                        String sql = "INSERT INTO classlevel(className,schoolNum,gradeNum,classNum,examPaperNum,average,totalScore,variance,sd,insertUser,insertDate)  SELECT c.className,b.schoolNum,b.gradeNum,b.classNum,b.exampaperNum,SUM(b.totalScore)/COUNT(b.classNum) average,SUM(b.totalScore) totalScore,b.variance,b.sd,b.insertUser,b.insertDate  FROM  (SELECT a.studentId,stu.schoolNum,a.gradeNum,stu.classNum,a.exampaperNum,ROUND(SUM(questionScore),2) totalScore,0 variance,0 sd,a.insertUser,a.insertDate  FROM (select questionScore,studentId,answer,qtype ,questionNum ,gradeNum,examPaperNum,examinationRoomNum,insertUser,insertDate  FROM score  where 1=1  AND  schoolNum={" + SchoolNum + "}  AND gradeNum={" + GradeNum + "}  AND examPaperNum={" + qExamPaperNum + "} ) a   LEFT JOIN student stu  ON a.studentId=stu.studentId  WHERE a.studentId=stu.studentId  GROUP BY stu.studentId) b  LEFT JOIN class c ON b.classNum=c.classNum  WHERE b.gradeNum=c.gradeNum AND b.schoolNum=c.schoolNum  GROUP BY b.classNum";
                        this.log.info("-sql---insertIntoClassLevel--" + sql);
                        count += this.dao2._execute(sql, args);
                    }
                }
            }
        }
        return Integer.valueOf(count);
    }

    public Integer insertIntogradeLevel(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        int count = 0;
        this.log.info("--sql2-插入gradeLevel表时，查询试卷科目-SELECT *  FROM exampaper  WHERE examPaperNum IN(SELECT DISTINCT examPaperNum FROM score )  AND examNum={examNum} ");
        if (null != this.dao2._queryBeanList("SELECT DISTINCT schoolNum,gradeNum FROM score ", Grade.class, args)) {
            List<?> _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT schoolNum,gradeNum FROM score ", Grade.class, args);
            for (int j = 0; j < _queryBeanList.size(); j++) {
                if (null != this.dao2._queryBeanList("SELECT *  FROM exampaper  WHERE examPaperNum IN(SELECT DISTINCT examPaperNum FROM score )  AND examNum={examNum} ", Exampaper.class, args)) {
                    List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT *  FROM exampaper  WHERE examPaperNum IN(SELECT DISTINCT examPaperNum FROM score )  AND examNum={examNum} ", Exampaper.class, args);
                    for (int i = 0; i < _queryBeanList2.size(); i++) {
                        String getSchoolNum = "getSchoolNum" + i;
                        String ExamPaperNum = "ExamPaperNum" + i;
                        args.put("getSchoolNum", ((Grade) _queryBeanList.get(j)).getSchoolNum());
                        args.put("ExamPaperNum", ((Exampaper) _queryBeanList2.get(i)).getExamPaperNum());
                        String sql = "INSERT INTO gradelevel(gradeName,schoolNum,gradeNum,examPaperNum,average,totalScore,variance,sd,insertUser,insertDate)  SELECT g.gradeName,b.schoolNum,b.gradeNum,b.exampaperNum,SUM(b.totalScore)/COUNT(b.gradeNum) average,SUM(b.totalScore) totalScore,b.variance,b.sd,b.insertUser,b.insertDate  FROM  (SELECT a.studentId,stu.schoolNum,a.gradeNum,stu.classNum,a.exampaperNum,ROUND(SUM(questionScore),2) totalScore,0 variance,0 sd,a.insertUser,a.insertDate  FROM (select questionScore,studentId,answer,qtype ,questionNum ,gradeNum,examPaperNum,examinationRoomNum,insertUser,insertDate   FROM score   where 1=1   AND  schoolNum={" + getSchoolNum + "}  AND examPaperNum={" + ExamPaperNum + "} ) a   LEFT JOIN student stu  ON a.studentId=stu.studentId  WHERE a.studentId=stu.studentId   GROUP BY stu.studentId) b  LEFT JOIN grade  g ON b.gradeNum=g.gradeNum  WHERE b.schoolNum=g.schoolNum  GROUP BY b.gradeNum";
                        this.log.info("-sql---insertIntogradeLevel--" + sql);
                        count += this.dao2._execute(sql, args);
                    }
                }
            }
        }
        return Integer.valueOf(count);
    }

    public List<Studentlevel> getCheckStuPaperList(String examNum, String subjectNum, String gradeNum, String examRoom, String studentId, String schoolNum, String radioValue, String examPaperNum, String testCenter) {
        String sql2;
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("examRoom", examRoom);
        String schStr = "";
        if (null != testCenter && !"".equals(testCenter) && !"-1".equals(testCenter)) {
            schStr = "   AND testingCentreId={testCenter}  ";
        }
        if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            schStr = "   AND schoolNum={schoolNum}   ";
        }
        String sql22 = "SELECT DISTINCT examPaperNum,studentId   FROM regexaminee  WHERE examPaperNum={examPaperNum} and scan_import='0'   ";
        if (null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            if (null != radioValue && "1".equals(radioValue)) {
                if (null != studentId && !"".equals(studentId)) {
                    sql22 = sql22 + "AND studentId in ({studentId[]}) ";
                }
                sql2 = sql22 + "AND classNum={examRoom}  ";
            } else {
                sql2 = sql22 + "AND examinationRoomNum={examRoom}   ";
                if (null != studentId && !"".equals(studentId)) {
                    sql2 = sql2 + "AND studentId in ({studentId[]}) ";
                }
            }
        } else {
            sql2 = (null == studentId || "".equals(studentId)) ? sql22 + schStr : sql22 + "AND studentId in ({studentId[]}) ";
        }
        this.log.info("---sql2--查询regexaminee识别表中的试卷大图---" + sql2);
        return this.dao2._queryBeanList(sql2, Studentlevel.class, args);
    }

    public List<Studentlevel> getCheckStuPaperList_score(String examNum, String subjectNum, String gradeNum, String examRoom, String studentId, String schoolNum, String radioValue, String examPaperNum, String testCenter, String height_score, String low_score) {
        String sql2;
        String schStr = "";
        if (null != testCenter && !"".equals(testCenter) && !"-1".equals(testCenter)) {
            schStr = "   AND reg.testingCentreId={testCenter}   ";
        }
        if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            schStr = "   AND reg.schoolNum={schoolNum}   ";
        }
        String sql22 = "SELECT DISTINCT reg.examPaperNum,reg.studentId   FROM regexaminee reg LEFT JOIN studentlevel stul on stul.examPaperNum=reg.examPaperNum and stul.studentId =reg.studentId  WHERE reg.examPaperNum={examPaperNum} and reg.scan_import='0'   ";
        if (null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            if (null != radioValue && "1".equals(radioValue)) {
                if (null != studentId && !"".equals(studentId)) {
                    sql22 = sql22 + "AND reg.studentId={studentId}    ";
                }
                sql2 = sql22 + "AND reg.classNum={examRoom}   ";
            } else {
                sql2 = sql22 + "AND reg.examinationRoomNum='" + examRoom + "'   ";
                if (null != studentId && !"".equals(studentId)) {
                    sql2 = sql2 + "AND reg.studentId={studentId}    ";
                }
            }
        } else {
            sql2 = (null == studentId || "".equals(studentId)) ? sql22 + schStr : sql22 + "AND reg.studentId={studentId}    ";
        }
        if (!"NaN".equals(low_score) && null != low_score) {
            sql2 = sql2 + " and stul.totalScore >= {low_score} ";
        }
        if (!"NaN".equals(height_score) && null != height_score) {
            sql2 = sql2 + " and stul.totalScore <= {height_score} ";
        }
        this.log.info("---sql2--查询regexaminee识别表中的试卷大图---" + sql2);
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("examRoom", examRoom);
        args.put("low_score", low_score);
        args.put("height_score", height_score);
        return this.dao2._queryBeanList(sql2, Studentlevel.class, args);
    }

    public List getStuIdByNumOrName(String numOrname, String examNum, String schoolNum, String gradeNum, String cla, String radioValue, String testCenter, String subject) {
        List<?> _queryBeanList;
        List<?> _queryBeanList2;
        List<?> _queryBeanList3;
        Map args = new HashMap();
        args.put("cla", cla);
        args.put("numOrname", numOrname);
        args.put("testCenter", testCenter);
        args.put("subject", subject);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        new ArrayList();
        String sql = "";
        if (null != radioValue && "2".equals(radioValue) && null != cla && !"-1".equals(cla) && !"".equals(cla)) {
            sql = "SELECT studentId FROM examinationnum  WHERE examinationRoomNum={cla}  AND examineeNum={numOrname} AND testingCentreId = {testCenter} and subjectNum={subject} ";
        }
        if (sql.length() > 0 && null != (_queryBeanList3 = this.dao2._queryBeanList(sql, Student.class, args)) && _queryBeanList3.size() > 0) {
            return _queryBeanList3;
        }
        String testCenterStr = " AND en.testingCentreId={testCenter} ";
        if (testCenter.equals("-1")) {
            testCenterStr = "";
        }
        String sql2 = ((null == radioValue || !"1".equals(radioValue) || null == cla || "".equals(cla) || "-1".equals(cla)) ? "select DISTINCT s.id studentId from examinationnum en left join student s on en.studentId=s.id where    en.gradeNum={gradeNum}  " : "select DISTINCT s.id studentId from examinationnum en left join student s on en.studentId=s.id where   schoolNum={schoolNum} AND classNum={cla}  ") + " AND en.examNum={examNum}  " + testCenterStr + " AND en.subjectNum={subject}   and (en.examineeNum={numOrname} or studentName LIKE {numOrname} )";
        if (sql2.length() > 0 && null != (_queryBeanList2 = this.dao2._queryBeanList(sql2, Student.class, args)) && _queryBeanList2.size() > 0) {
            return _queryBeanList2;
        }
        if ("SELECT id studentId FROM student  WHERE studentId={numOrname} ".length() > 0 && null != (_queryBeanList = this.dao2._queryBeanList("SELECT id studentId FROM student  WHERE studentId={numOrname} ", Student.class, args)) && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    public List<Studentlevel> getCheckStuPaperListPage(String examNum, String subjectNum, String gradeNum, String examRoom, String studentId, String schoolNum, int pageStart, int pageSize, String radioValue, String examPaperNum, String testCenter) {
        String stuidSql;
        String sql2;
        String sql22;
        String sql23;
        String sql24;
        String sql25;
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put("examPaperNum", examPaperNum);
        args.put("examRoom", examRoom);
        args.put("pageStart", Integer.valueOf(pageStart));
        args.put("pageSize", Integer.valueOf(pageSize));
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        String schStr = "";
        if (null != testCenter && !"".equals(testCenter) && !"-1".equals(testCenter)) {
            schStr = "   AND testingCentreId={testCenter}   ";
        }
        if (null == studentId || "".equals(studentId)) {
            if (null == examRoom || "".equals(examRoom) || "-1".equals(examRoom)) {
                stuidSql = "SELECT DISTINCT examPaperNum,studentId   FROM regexaminee  WHERE examPaperNum={examPaperNum}  and scan_import='0'     " + schStr;
            } else {
                stuidSql = (null == radioValue || !"1".equals(radioValue)) ? "SELECT DISTINCT examPaperNum,studentId   FROM regexaminee  WHERE examPaperNum={examPaperNum}  and scan_import='0'     " + schStr + "   AND examinationRoomNum={examRoom}   " : "SELECT DISTINCT examPaperNum,studentId   FROM regexaminee  WHERE examPaperNum={examPaperNum}  and scan_import='0'     " + schStr + "   AND classNum= {examRoom}   ";
            }
            new ArrayList();
            List li = this.dao2._queryBeanList(stuidSql + "  ORDER BY studentId   LIMIT {pageStart},{pageSize} ", RegExaminee.class, args);
            if (null != li && li.size() > 0) {
                studentId = ((RegExaminee) li.get(0)).getStudentId() + "";
            }
        } else {
            String[] studentIdArr = studentId.split(Const.STRING_SEPERATOR);
            studentId = studentIdArr[pageStart];
        }
        args.put(Const.EXPORTREPORT_studentId, studentId);
        if (null == radioValue || !"2".equals(radioValue)) {
            sql2 = "SELECT DISTINCT a.id,a.className,a.studentName,a.studentId,a.page,a.totalScore,a.sqts,a.oqts,a.examPaperNum,a.examineeNum,a.examinationRoomNum,a.type ext1,a.fullScore   FROM (SELECT rt.id,c.className,st.studentName,rt.studentId,rt.page,il.type,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2)+ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) totalScore,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2) sqts,ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) oqts,ROUND(SUM(IF(rt.fullscore is null,0,rt.fullscore)),2) fullscore,  rt.examPaperNum,em.examineeNum,em.examinationRoomNum  FROM(SELECT rtsub.id,rtsub.studentId,rtsub.examPaperNum,rtsub.page,s.questionNum,s.qtype,s.questionScore,df.fullscore      FROM( SELECT id,examPaperNum,studentId,page    FROM regexaminee  WHERE examPaperNum={examPaperNum} and scan_import='0'  AND studentId={studentId} AND schoolNum={schoolNum}  ";
            if (null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
                sql2 = sql2 + "AND classNum={examRoom}  ";
            }
        } else {
            sql2 = (null == examRoom || "".equals(examRoom) || "-1".equals(examRoom)) ? "SELECT DISTINCT a.id,a.className,a.studentName,a.studentId,a.page,a.totalScore,a.sqts,a.oqts,a.examPaperNum,a.examineeNum,a.examinationRoomNum,a.type ext1,a.fullScore   FROM (SELECT rt.id,c.className,st.studentName,rt.studentId,rt.page,il.type,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2)+ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) totalScore,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2) sqts,ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) oqts,ROUND(SUM(IF(rt.fullscore is null,0,rt.fullscore)),2) fullscore,  rt.examPaperNum,em.examineeNum,em.examinationRoomNum  FROM(SELECT rtsub.id,rtsub.studentId,rtsub.examPaperNum,rtsub.page,s.questionNum,s.qtype,s.questionScore,df.fullscore      FROM( SELECT id,examPaperNum,studentId,page    FROM regexaminee  WHERE examPaperNum={examPaperNum} and scan_import='0'  AND studentId={studentId} " + schStr + "  " : "SELECT DISTINCT a.id,a.className,a.studentName,a.studentId,a.page,a.totalScore,a.sqts,a.oqts,a.examPaperNum,a.examineeNum,a.examinationRoomNum,a.type ext1,a.fullScore   FROM (SELECT rt.id,c.className,st.studentName,rt.studentId,rt.page,il.type,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2)+ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) totalScore,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2) sqts,ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) oqts,ROUND(SUM(IF(rt.fullscore is null,0,rt.fullscore)),2) fullscore,  rt.examPaperNum,em.examineeNum,em.examinationRoomNum  FROM(SELECT rtsub.id,rtsub.studentId,rtsub.examPaperNum,rtsub.page,s.questionNum,s.qtype,s.questionScore,df.fullscore      FROM( SELECT id,examPaperNum,studentId,page    FROM regexaminee  WHERE examPaperNum={examPaperNum} and scan_import='0'  AND examinationRoomNum={examRoom}  AND studentId={studentId} " + schStr;
        }
        String sql26 = sql2 + ")rtsub   LEFT JOIN (select examPaperNum,questionNum,'1' qtype,questionScore,studentId,page  from score  where examPaperNum={examPaperNum}  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql22 = sql26 + "AND examinationRoomNum={examRoom}  AND studentId={studentId} " + schStr;
        } else {
            sql22 = sql26 + "AND studentId={studentId} " + schStr + "   ";
        }
        String sql27 = sql22 + "UNION   SELECT examPaperNum,questionNum,'0' qtype,questionScore,studentId,page  from objectivescore  where examPaperNum={examPaperNum}  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql23 = sql27 + "AND examinationRoomNum={examRoom}  AND studentId={studentId} " + schStr;
        } else {
            sql23 = sql27 + "AND studentId={studentId} " + schStr + "   ";
        }
        String sql28 = sql23 + ") s ON s.studentId = rtsub.studentId AND s.page=rtsub.page   LEFT JOIN ( \tselect id,fullscore,page from define where examPaperNum={examPaperNum} \tUNION \tselect id,fullscore,page from subdefine where examPaperNum={examPaperNum} )df on df.id=s.questionNum and df.page=rtsub.page  ) rt   LEFT JOIN (SELECT type,studentId FROM illegal WHERE  examPaperNum={examPaperNum}  ) il ON il.studentId = rt.studentId LEFT JOIN (SELECT id,studentId,studentNum,studentName,classNum,jie FROM student WHERE   id={studentId} ) st ON st.id = rt.studentId    LEFT JOIN (SELECT id,classNum,className,jie FROM class WHERE  ";
        if (null != radioValue && "1".equals(radioValue) && null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql24 = sql28 + "id={examRoom}  ";
        } else {
            sql24 = sql28 + "1=1  ";
        }
        String sql29 = sql24 + ") c ON c.id = st.classNum   LEFT JOIN (SELECT examineeNum,examinationRoomNum,studentId FROM examinationnum    WHERE  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql25 = sql29 + "examinationRoomNum={examRoom} and subjectNum = {subjectNum}  " + schStr;
        } else {
            sql25 = sql29 + "examNum={examNum}  " + schStr + "  AND gradeNum={gradeNum} and subjectNum = {subjectNum} ";
        }
        String sql210 = sql25 + ") em ON em.studentID = rt.studentId GROUP BY rt.page   ) a   order by a.examineeNum,a.page";
        this.log.info("sql--getCheckStuPaperListPage--查询学生一科试卷的试卷页信息--" + sql210);
        return this.dao2._queryBeanList(sql210, Studentlevel.class, args);
    }

    public List<Studentlevel> getCheckStuPaperListPage_score(String examNum, String subjectNum, String gradeNum, String examRoom, String studentId, String schoolNum, int pageStart, int pageSize, String radioValue, String examPaperNum, String testCenter, String height_score, String low_score) {
        String stuidSql;
        String sql2;
        String sql22;
        String sql23;
        String sql24;
        String sql25;
        String schStr = "";
        String schStr2 = "";
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put("examPaperNum", examPaperNum);
        args.put("examRoom", examRoom);
        args.put("low_score", low_score);
        args.put("height_score", height_score);
        args.put("pageStart", Integer.valueOf(pageStart));
        args.put("pageSize", Integer.valueOf(pageSize));
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        if (null != testCenter && !"".equals(testCenter) && !"-1".equals(testCenter)) {
            schStr = "   AND reg.testingCentreId={testCenter}   ";
        }
        if (null != testCenter && !"".equals(testCenter) && !"-1".equals(testCenter)) {
            schStr2 = "   AND testingCentreId={testCenter}   ";
        }
        if (null == studentId || "".equals(studentId)) {
            if (null == examRoom || "".equals(examRoom) || "-1".equals(examRoom)) {
                stuidSql = "SELECT DISTINCT reg.examPaperNum,reg.studentId   FROM regexaminee reg LEFT JOIN studentlevel stul on stul.studentId=reg.studentId and stul.studentId =reg.studentId  WHERE reg.examPaperNum={examPaperNum} and reg.scan_import='0'   and stul.examPaperNum =  {examPaperNum} " + schStr;
            } else {
                stuidSql = (null == radioValue || !"1".equals(radioValue)) ? "SELECT DISTINCT reg.examPaperNum,reg.studentId   FROM regexaminee reg LEFT JOIN studentlevel stul on stul.studentId=reg.studentId and stul.studentId =reg.studentId  WHERE reg.examPaperNum={examPaperNum} and reg.scan_import='0'   and stul.examPaperNum =  {examPaperNum} " + schStr + "   AND reg.examinationRoomNum={examRoom}   " : "SELECT DISTINCT reg.examPaperNum,reg.studentId   FROM regexaminee reg LEFT JOIN studentlevel stul on stul.studentId=reg.studentId and stul.studentId =reg.studentId  WHERE reg.examPaperNum={examPaperNum} and reg.scan_import='0'   and stul.examPaperNum =  {examPaperNum} " + schStr + "   AND reg.classNum={examRoom}   ";
            }
            if (!"NaN".equals(low_score)) {
                stuidSql = stuidSql + " and stul.totalScore >= {low_score} ";
            }
            if (!"NaN".equals(height_score)) {
                stuidSql = stuidSql + " and stul.totalScore <= {height_score} ";
            }
            new ArrayList();
            List li = this.dao2._queryBeanList(stuidSql + "  ORDER BY stul.studentId   LIMIT {pageStart},{pageSize} ", RegExaminee.class, args);
            if (null != li && li.size() > 0) {
                studentId = ((RegExaminee) li.get(0)).getStudentId() + "";
            }
        } else {
            String[] studentIdArr = studentId.split(Const.STRING_SEPERATOR);
            studentId = studentIdArr[pageStart];
        }
        args.put(Const.EXPORTREPORT_studentId, studentId);
        if (null == radioValue || !"2".equals(radioValue)) {
            sql2 = "SELECT DISTINCT a.id,a.className,a.studentName,a.studentId,a.page,a.totalScore,a.sqts,a.oqts,a.examPaperNum,a.examineeNum,a.examinationRoomNum,a.type ext1,a.fullScore  FROM (SELECT rt.id,c.className,st.studentName,rt.studentId,rt.page,il.type,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2)+ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) totalScore,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2) sqts,ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) oqts,ROUND(SUM(IF(rt.fullscore is null,0,rt.fullscore)),2) fullscore,  rt.examPaperNum,em.examineeNum,em.examinationRoomNum  FROM(SELECT rtsub.id,rtsub.studentId,rtsub.examPaperNum,rtsub.page,s.questionNum,s.qtype,s.questionScore,df.fullscore     FROM( SELECT id,examPaperNum,studentId,page    FROM regexaminee  WHERE examPaperNum={examPaperNum} and scan_import='0'  AND studentId={studentId}  " + schStr2;
        } else {
            sql2 = (null == examRoom || "".equals(examRoom) || "-1".equals(examRoom)) ? "SELECT DISTINCT a.id,a.className,a.studentName,a.studentId,a.page,a.totalScore,a.sqts,a.oqts,a.examPaperNum,a.examineeNum,a.examinationRoomNum,a.type ext1,a.fullScore  FROM (SELECT rt.id,c.className,st.studentName,rt.studentId,rt.page,il.type,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2)+ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) totalScore,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2) sqts,ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) oqts,ROUND(SUM(IF(rt.fullscore is null,0,rt.fullscore)),2) fullscore,  rt.examPaperNum,em.examineeNum,em.examinationRoomNum  FROM(SELECT rtsub.id,rtsub.studentId,rtsub.examPaperNum,rtsub.page,s.questionNum,s.qtype,s.questionScore,df.fullscore     FROM( SELECT id,examPaperNum,studentId,page    FROM regexaminee  WHERE examPaperNum={examPaperNum} and scan_import='0'  AND studentId={studentId}  " + schStr2 : "SELECT DISTINCT a.id,a.className,a.studentName,a.studentId,a.page,a.totalScore,a.sqts,a.oqts,a.examPaperNum,a.examineeNum,a.examinationRoomNum,a.type ext1,a.fullScore  FROM (SELECT rt.id,c.className,st.studentName,rt.studentId,rt.page,il.type,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2)+ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) totalScore,ROUND(SUM(IF(rt.qtype='0',0,questionScore)),2) sqts,ROUND(SUM(IF(rt.qtype='1',0,questionScore)),2) oqts,ROUND(SUM(IF(rt.fullscore is null,0,rt.fullscore)),2) fullscore,  rt.examPaperNum,em.examineeNum,em.examinationRoomNum  FROM(SELECT rtsub.id,rtsub.studentId,rtsub.examPaperNum,rtsub.page,s.questionNum,s.qtype,s.questionScore,df.fullscore     FROM( SELECT id,examPaperNum,studentId,page    FROM regexaminee  WHERE examPaperNum={examPaperNum} and scan_import='0'  AND examinationRoomNum={examRoom}  AND studentId={studentId}  " + schStr2;
        }
        String sql26 = sql2 + ")rtsub   LEFT JOIN (select examPaperNum,questionNum,'1' qtype,questionScore,studentId,page  from score  where examPaperNum={examPaperNum}  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql22 = sql26 + "AND examinationRoomNum={examRoom}  AND studentId={studentId}  " + schStr2;
        } else {
            sql22 = sql26 + "AND studentId={studentId}  " + schStr2;
        }
        String sql27 = sql22 + "UNION   SELECT examPaperNum,questionNum,'0' qtype,questionScore,studentId,page  from objectivescore  where examPaperNum={examPaperNum}  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql23 = sql27 + "AND examinationRoomNum={examRoom}  AND studentId={studentId}  " + schStr2;
        } else {
            sql23 = sql27 + "AND studentId={studentId}  " + schStr2;
        }
        String sql28 = sql23 + ") s ON s.studentId = rtsub.studentId AND s.page=rtsub.page   \tLEFT JOIN \t(  \tselect id,fullscore,page from define where examPaperNum={examPaperNum}  \tUNION  \tselect id,fullscore,page from subdefine where examPaperNum={examPaperNum}  \t)df on df.id=s.questionNum and df.page=rtsub.page    ) rt   LEFT JOIN (SELECT type,studentId FROM illegal WHERE  examPaperNum={examPaperNum}  ) il ON il.studentId = rt.studentId LEFT JOIN (SELECT id,studentId,studentNum,studentName,classNum,jie FROM student WHERE  id={studentId}  ) st ON st.id = rt.studentId    LEFT JOIN (SELECT id,classNum,className,jie FROM class WHERE  ";
        if (null != radioValue && "1".equals(radioValue) && null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql24 = sql28 + "id={examRoom}  ";
        } else {
            sql24 = sql28 + "1=1  ";
        }
        String sql29 = sql24 + ") c ON c.id = st.classNum   LEFT JOIN (SELECT examineeNum,examinationRoomNum,studentId FROM examinationnum    WHERE  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql25 = sql29 + "examinationRoomNum={examRoom} and subjectNum = {subjectNum}  " + schStr2;
        } else {
            sql25 = sql29 + "examNum={examNum}  AND gradeNum={gradeNum} and subjectNum = {subjectNum} " + schStr2;
        }
        String sql210 = sql25 + ") em ON em.studentID = rt.studentId GROUP BY rt.page   ) a   order by a.examineeNum,a.page";
        this.log.info("sql--getCheckStuPaperListPage--查询学生一科试卷的试卷页信息--" + sql210);
        return this.dao2._queryBeanList(sql210, Studentlevel.class, args);
    }

    public List<Studentlevel> getCheckStuPaperListPage_Class(String examNum, String subjectNum, String gradeNum, String examRoom, String studentId, String schoolNum, int pageStart, int pageSize) {
        String sql = "SELECT a.className,a.studentName,a.studentId,a.page,a.totalScore,a.sqts,a.oqts,a.examPaperNum,a.examineeNum ,b.ext1,a.examinationRoomNum  FROM ( SELECT c.className,st.studentName,r.studentId,rt.page,ROUND(SUM(IF(s.qtype='0',0,questionScore)),2)+ROUND(SUM(IF(s.qtype='1',0,questionScore)),2) totalScore,ROUND(SUM(IF(s.qtype='0',0,questionScore)),2) sqts,ROUND(SUM(IF(s.qtype='1',0,questionScore)),2) oqts,r.examPaperNum,em.examineeNum,em.examinationRoomNum  FROM( SELECT DISTINCT studentId,examPaperNum  FROM regexaminee  WHERE examPaperNum=(SELECT examPaperNum FROM exampaper  WHERE examNum={examNum}  AND gradeNum={gradeNum} AND subjectNum={subjectNum} )  ";
        if (null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql = sql + "   AND classNum={examRoom} ";
        }
        if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            sql = sql + "   AND schoolNum={schoolNum} ";
        }
        if (null != studentId && !"".equals(studentId)) {
            sql = sql + "  AND studentId={studentId} ";
        }
        String sql2 = sql + "  ORDER BY studentId   ";
        if (null == studentId || "".equals(studentId)) {
            sql2 = sql2 + "   LIMIT {pageStart},{pageSize} ";
        }
        String sql3 = sql2 + "   ) r     LEFT JOIN   ( SELECT DISTINCT studentId,examPaperNum,page  FROM regexaminee  WHERE examPaperNum=(SELECT examPaperNum FROM exampaper  WHERE examNum={examNum}  AND gradeNum={gradeNum} AND subjectNum={subjectNum} )     ";
        if (null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql3 = sql3 + "   AND classNum={examRoom} ";
        }
        if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            sql3 = sql3 + "   AND schoolNum={schoolNum} ";
        }
        String sql4 = sql3 + ")  rt     ON r.studentId=rt.studentId AND r.examPaperNum = rt.examPaperNum LEFT JOIN score s ON s.studentId = rt.studentId AND s.examPaperNum = rt.examPaperNum   AND s.page=rt.page   LEFT JOIN student st ON st.studentId = rt.studentId  LEFT JOIN class c ON c.classNum = st.classNum  AND st.gradeNum=c.gradeNum AND st.schoolNum=c.schoolNum  AND st.jie=c.jie  LEFT JOIN exampaper e ON e.examPaperNum = rt.examPaperNum  LEFT JOIN examinationnum em ON em.studentID = rt.studentId AND e.examNum = em.examNum   where em.subjectNum = {subjectNum} GROUP BY rt.page ) a  LEFT JOIN(SELECT st.studentName,re.studentId,ROUND(SUM(s.questionscore),2) ext1,re.examPaperNum,em.examineeNum  FROM ( SELECT DISTINCT studentId,examPaperNum  FROM regexaminee  WHERE examPaperNum=(SELECT examPaperNum FROM exampaper  WHERE examNum={examNum}   AND gradeNum={gradeNum} AND subjectNum={subjectNum} )  ";
        if (null != schoolNum && !"".equals(schoolNum)) {
            sql4 = sql4 + "   AND schoolNum={schoolNum} ";
        }
        if (null != examRoom && !"".equals(examRoom) && !"-1".equals(examRoom)) {
            sql4 = sql4 + "   AND classNum={examRoom} ";
        }
        if (null != studentId && !"".equals(studentId)) {
            sql4 = sql4 + "  AND studentId={studentId} ";
        }
        String sql5 = sql4 + "  ORDER BY studentId   ";
        if (null == studentId || "".equals(studentId)) {
            sql5 = sql5 + "   LIMIT {pageStart} ,{pageSize} ";
        }
        String sql6 = sql5 + "   ) re  LEFT JOIN score s ON s.studentId = re.studentId AND s.examPaperNum = re.examPaperNum  LEFT JOIN student st ON st.studentId = re.studentId  LEFT JOIN exampaper e ON e.examPaperNum =re.examPaperNum  LEFT JOIN examinationnum em ON em.studentID = re.studentId AND e.examNum = em.examNum  where em.subjectNum = {subjectNum} ) b  ON a.studentId=b.studentId  WHERE a.examPaperNum=b.examPaperNum order by a.studentId ";
        this.log.info("sql--getCheckStuPaperListPage--查询学生一科试卷的试卷页信息--" + sql6);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("examRoom", examRoom);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("pageStart", Integer.valueOf(pageStart));
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao2._queryBeanList(sql6, Studentlevel.class, args);
    }

    public Studentpaperimage getspiObj(int id) {
        Map args = StreamMap.create().put("id", (Object) Integer.valueOf(id));
        this.log.info("get getspiObj img   sql: select * from studentpaperimage  where id={id} ");
        return (Studentpaperimage) this.dao2._queryBean("select * from studentpaperimage  where id={id} ", Studentpaperimage.class, args);
    }

    public List<Studentlevel> getCheckStuPaperDetail(String examNum, String subjectNum, String gradeNum, String examRoom, String studentId, String schoolNum, int pageStart, int pageSize, String radioValue, String examPaperNum, String testCenter) {
        String stuidSql;
        String sql;
        String sql2;
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examPaperNum", examPaperNum);
        args.put("examRoom", examRoom);
        args.put("pageStart", Integer.valueOf(pageStart));
        args.put("pageSize", Integer.valueOf(pageSize));
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        String schStr = "";
        if (null != testCenter && !"".equals(testCenter) && !"-1".equals(testCenter)) {
            schStr = "   AND testingCentreId={testCenter}   ";
        }
        if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            schStr = "   AND schoolNum={schoolNum}   ";
        }
        if (null == studentId || "".equals(studentId)) {
            if (null == examRoom || "".equals(examRoom) || "-1".equals(examRoom)) {
                stuidSql = "SELECT DISTINCT examPaperNum,studentId   FROM regexaminee  WHERE examPaperNum={examPaperNum} and scan_import='0'     " + schStr;
            } else {
                stuidSql = (null == radioValue || !"1".equals(radioValue)) ? "SELECT DISTINCT examPaperNum,studentId   FROM regexaminee  WHERE examPaperNum={examPaperNum} and scan_import='0'        AND examinationRoomNum={examRoom}   " + schStr : "SELECT DISTINCT examPaperNum,studentId   FROM regexaminee  WHERE examPaperNum={examPaperNum} and scan_import='0'     " + schStr + "   AND classNum={examRoom}   ";
            }
            new ArrayList();
            List li = this.dao2._queryBeanList(stuidSql + "  ORDER BY studentId   LIMIT {pageStart},{pageSize} ", RegExaminee.class, args);
            if (null != li && li.size() > 0) {
                studentId = ((RegExaminee) li.get(0)).getStudentId() + "";
            }
        } else {
            String[] studentIdArr = studentId.split(Const.STRING_SEPERATOR);
            studentId = studentIdArr[pageStart];
        }
        args.put(Const.EXPORTREPORT_studentId, studentId);
        String sql3 = "SELECT DISTINCT b.id,b.className,b.studentName,b.studentId,b.examPaperNum,b.page,b.questionNum,a.questionNum realQuestionNum,b.qtype,a.stdAanswer,b.answer,b.questionScore,a.fullScore,b.examineeNum,b.regScore FROM(SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename   FROM define  WHERE examPaperNum={examPaperNum} AND isParent=0  AND choosename<>'T'  UNION   SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename  FROM subdefine   WHERE examPaperNum={examPaperNum}   ) a  LEFT JOIN (SELECT c.className,st.studentName,r.studentId,r.page,r.id,r.questionNum,r.qtype,r.questionScore,r.answer,r.examPaperNum,em.examineeNum,r.regScore \tFROM(SELECT studentId,examPaperNum,page,id,questionNum,'1' qtype,questionScore,'' answer,regScore   FROM score WHERE examPaperNum='" + examPaperNum + "' AND continued ='F'  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"-1".equals(examRoom) && !"".equals(examRoom)) {
            sql3 = sql3 + "AND examinationRoomNum={examRoom}  " + schStr;
        }
        String sql4 = sql3 + "AND studentId={studentId} UNION ALL  SELECT studentId,examPaperNum,page,id,questionNum,'0' qtype,questionScore,answer,regScore   FROM objectivescore WHERE examPaperNum={examPaperNum}  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"-1".equals(examRoom) && !"".equals(examRoom)) {
            sql4 = sql4 + "AND examinationRoomNum={examRoom}  " + schStr;
        }
        String sql5 = sql4 + "AND studentId={studentId} ) r  LEFT JOIN (SELECT id,studentId,studentNum,studentName,classNum,jie  FROM student WHERE id={studentId} ) st ON st.id = r.studentId  \tLEFT JOIN (SELECT id,classNum,className,jie FROM class  WHERE  ";
        if (null != radioValue && "1".equals(radioValue) && null != examRoom && !"-1".equals(examRoom) && !"".equals(examRoom)) {
            sql = sql5 + "id={examRoom}  ";
        } else {
            sql = sql5 + "1=1 ";
        }
        String sql6 = sql + ") c ON c.id = st.classNum \tLEFT JOIN (SELECT examineeNum,examinationRoomNum,studentId FROM examinationnum   WHERE  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"-1".equals(examRoom) && !"".equals(examRoom)) {
            sql2 = sql6 + "examinationRoomNum={examRoom} and subjectNum ={subjectNum}  " + schStr;
        } else {
            sql2 = sql6 + "examNum={examNum}  AND gradeNum={gradeNum} and subjectNum ={subjectNum} " + schStr;
        }
        String sql7 = sql2 + ") em ON em.studentID = r.studentId   )b  ON a.id=b.questionNum  WHERE a.questionType=b.qtype   ORDER BY REPLACE(a.questionNum,'_','.')*1 ";
        this.log.info("sql--getCheckStuPaperDetail--" + sql7);
        return this.dao2._queryBeanList(sql7, Studentlevel.class, args);
    }

    public List<Studentlevel> getCheckStuPaperDetail_score(String examNum, String subjectNum, String gradeNum, String examRoom, String studentId, String schoolNum, int pageStart, int pageSize, String radioValue, String examPaperNum, String testCenter, String height_score, String low_score) {
        String stuidSql;
        String sql;
        String sql2;
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examPaperNum", examPaperNum);
        args.put("examRoom", examRoom);
        args.put("low_score", low_score);
        args.put("height_score", height_score);
        args.put("pageStart", Integer.valueOf(pageStart));
        args.put("pageSize", Integer.valueOf(pageSize));
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        String schStr = "";
        String schStr2 = "";
        if (null != testCenter && !"".equals(testCenter) && !"-1".equals(testCenter)) {
            schStr = "   AND reg.testingCentreId={testCenter}   ";
            schStr2 = "   AND testingCentreId={testCenter}   ";
        }
        if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            schStr = "   AND reg.schoolNum={schoolNum}   ";
            schStr2 = "   AND schoolNum={schoolNum}   ";
        }
        if (null == studentId || "".equals(studentId)) {
            if (null == examRoom || "".equals(examRoom) || "-1".equals(examRoom)) {
                stuidSql = "SELECT DISTINCT reg.examPaperNum,reg.studentId   FROM regexaminee reg LEFT JOIN studentlevel stul on stul.examPaperNum=reg.examPaperNum and stul.studentId =reg.studentId  WHERE reg.examPaperNum={examPaperNum} and reg.scan_import='0'   " + schStr;
            } else {
                stuidSql = (null == radioValue || !"1".equals(radioValue)) ? "SELECT DISTINCT reg.examPaperNum,reg.studentId   FROM regexaminee reg LEFT JOIN studentlevel stul on stul.examPaperNum=reg.examPaperNum and stul.studentId =reg.studentId  WHERE reg.examPaperNum={examPaperNum} and reg.scan_import='0'      AND reg.examinationRoomNum={examRoom}   " + schStr : "SELECT DISTINCT reg.examPaperNum,reg.studentId   FROM regexaminee reg LEFT JOIN studentlevel stul on stul.examPaperNum=reg.examPaperNum and stul.studentId =reg.studentId  WHERE reg.examPaperNum={examPaperNum} and reg.scan_import='0'   " + schStr + "   AND reg.classNum={examRoom}   ";
            }
            if (!"NaN".equals(low_score)) {
                stuidSql = stuidSql + " and stul.totalScore >= {low_score} ";
            }
            if (!"NaN".equals(height_score)) {
                stuidSql = stuidSql + " and stul.totalScore <= {height_score} ";
            }
            new ArrayList();
            List li = this.dao2._queryBeanList(stuidSql + "  ORDER BY studentId   LIMIT {pageStart},{pageSize}  ", RegExaminee.class, args);
            if (null != li && li.size() > 0) {
                studentId = ((RegExaminee) li.get(0)).getStudentId() + "";
            }
        } else {
            String[] studentIdArr = studentId.split(Const.STRING_SEPERATOR);
            studentId = studentIdArr[pageStart];
        }
        args.put(Const.EXPORTREPORT_studentId, studentId);
        String sql3 = "SELECT DISTINCT b.id,b.className,b.studentName,b.studentId,b.examPaperNum,b.page,b.questionNum,a.questionNum realQuestionNum,b.qtype,a.stdAanswer,b.answer,b.questionScore,a.fullScore,b.examineeNum,b.regScore FROM(SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename   FROM define  WHERE examPaperNum={examPaperNum} AND isParent=0  AND choosename<>'T'  UNION   SELECT id,examPaperNum,questionNum,questionType,fullScore,answer stdAanswer,choosename  FROM subdefine   WHERE examPaperNum={examPaperNum}   ) a  LEFT JOIN (SELECT c.className,st.studentName,r.studentId,r.page,r.id,r.questionNum,r.qtype,r.questionScore,r.answer,r.examPaperNum,em.examineeNum,r.regScore \tFROM(SELECT studentId,examPaperNum,page,id,questionNum,'1' qtype,questionScore,'' answer,regScore   FROM score WHERE examPaperNum={examPaperNum}  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"-1".equals(examRoom) && !"".equals(examRoom)) {
            sql3 = sql3 + "AND examinationRoomNum={examRoom}  " + schStr2;
        }
        String sql4 = sql3 + "AND studentId={studentId}  UNION ALL  SELECT studentId,examPaperNum,page,id,questionNum,'0' qtype,questionScore,answer,regScore   FROM objectivescore WHERE examPaperNum={examPaperNum}  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"-1".equals(examRoom) && !"".equals(examRoom)) {
            sql4 = sql4 + "AND examinationRoomNum={examRoom}  " + schStr2;
        }
        String sql5 = sql4 + "AND studentId={studentId}  ) r  LEFT JOIN (SELECT id,studentId,studentNum,studentName,classNum,jie  FROM student WHERE id={studentId}) st ON st.id = r.studentId  \tLEFT JOIN (SELECT id,classNum,className,jie FROM class  WHERE  ";
        if (null != radioValue && "1".equals(radioValue) && null != examRoom && !"-1".equals(examRoom) && !"".equals(examRoom)) {
            sql = sql5 + "id={examRoom}  ";
        } else {
            sql = sql5 + "1=1 ";
        }
        String sql6 = sql + ") c ON c.id = st.classNum \tLEFT JOIN (SELECT examineeNum,examinationRoomNum,studentId FROM examinationnum   WHERE  ";
        if (null != radioValue && "2".equals(radioValue) && null != examRoom && !"-1".equals(examRoom) && !"".equals(examRoom)) {
            sql2 = sql6 + "examinationRoomNum={examRoom} and subjectNum ={subjectNum}  " + schStr2;
        } else {
            sql2 = sql6 + "examNum={examNum} AND testingCentreId={testCenter} AND gradeNum={gradeNum} and subjectNum ={subjectNum}  ";
        }
        String sql7 = sql2 + ") em ON em.studentID = r.studentId   )b  ON a.id=b.questionNum  WHERE a.questionType=b.qtype   ORDER BY REPLACE(a.questionNum,'_','.')*1 ";
        this.log.info("sql--getCheckStuPaperDetail--" + sql7);
        return this.dao2._queryBeanList(sql7, Studentlevel.class, args);
    }

    public List getLeakFromScore(String examNum, String subjectNum, String gradeNum, String testCenter, String roomNum) {
        Map args = new HashMap();
        args.put("roomNum", roomNum);
        args.put("testCenter", testCenter);
        args.put("EXCEPTION_TYPE_MISSING", "0");
        args.put("FALSE", "F");
        args.put("SCORE_EXCEPTION_NOTTOSCORE", "0");
        args.put("SCORE_EXCEPTION_UPFULLMARKS_update", "1");
        String examPaperNum = this.examDao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        args.put("examPaperNum", examPaperNum);
        String illegalSql = "WHERE examPaperNum={examPaperNum}   ";
        if (null != roomNum && !"-1".equals(roomNum)) {
            illegalSql = illegalSql + " AND examinationRoomNum={roomNum}   ";
        }
        if (null != testCenter && !"-1".equals(testCenter)) {
            illegalSql = illegalSql + " AND testingCentreId={testCenter}   ";
        }
        String illegalSql2 = illegalSql + "     AND studentId NOT IN (\tSELECT DISTINCT studentId \tFROM illegal  \tWHERE 1=1  \tAND examPaperNum={examPaperNum}   ";
        if (null != testCenter && !"-1".equals(testCenter)) {
            illegalSql2 = illegalSql2 + "\tAND testingCentreId={testCenter}   ";
        }
        if (null != roomNum && !"-1".equals(roomNum)) {
            illegalSql2 = illegalSql2 + "\tAND examinationRoomNum={roomNum}   ";
        }
        String illegalSql3 = illegalSql2 + "\tAND  type={EXCEPTION_TYPE_MISSING}   )   ";
        String sql = "select DISTINCT sc.questionNum,sc.examPaperNum,def.questionNum as qNum from (SELECT *   FROM score  " + illegalSql3 + "AND isException IS NOT null        AND continued= {FALSE}   AND  (isException={SCORE_EXCEPTION_NOTTOSCORE} OR isException={SCORE_EXCEPTION_UPFULLMARKS_update}  OR isException='5' ))sc   LEFT JOIN (SELECT id,questionNum FROM define   UNION SELECT id,questionNum FROM subdefine )def  ON sc.questionNum=def.id ORDER BY qNum *1, REPLACE ( SUBSTRING( qNum, LOCATE('_', qNum) + 1, LENGTH(qNum) ), '_', '' ) ASC";
        List questionNumList = this.dao2._queryBeanList(sql, Score.class, args);
        List<Score> scoreList = new ArrayList<>();
        if (null != questionNumList) {
            for (int i = 0; i < questionNumList.size(); i++) {
                String pQuestionNum = "pQuestionNum" + i;
                args.put(pQuestionNum, ((Score) questionNumList.get(i)).getQuestionNum());
                String sql2 = "SELECT sc.*,def.questionNum  realQuestionNum   FROM (SELECT   '" + ((Score) questionNumList.get(i)).getQuestionNum() + "'  questionNum,examPaperNum,sum(IF(isException='0',1,0))  noscore,sum(IF(isException='1',1,0)) upfullscore,sum(IF(isException='2',1,0))  norecognized  ,sum(IF(isException='5',1,0))  ext1  FROM score   " + illegalSql3 + "  AND questionNum='" + ((Score) questionNumList.get(i)).getQuestionNum() + "'     AND isException IS NOT null     AND continued= {FALSE}   AND  (isException={SCORE_EXCEPTION_NOTTOSCORE} OR isException={SCORE_EXCEPTION_UPFULLMARKS_update}  OR isException='5' )   )sc   LEFT JOIN (SELECT id,questionNum,examPaperNum,category FROM define WHERE id={" + pQuestionNum + "} UNION SELECT id,questionNum,examPaperNum,category FROM subdefine WHERE id={" + pQuestionNum + "} )def  ON sc.questionNum=def.id  ";
                Score score = (Score) this.dao2._queryBean(sql2, Score.class, args);
                if (null != score && score.getUpfullscore() + score.getNoscore() + Integer.valueOf(score.getExt1()).intValue() != 0) {
                    scoreList.add(score);
                }
            }
        }
        return scoreList;
    }

    public List getExpQuestionImg(String schoolNum, String gradeNum, String examPaperNum, String questionNum, String examroomNum, String expMark, String examNum) {
        String sql;
        String sql2;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examroomNum", examroomNum);
        args.put("EXCEPTION_TYPE_MISSING", "0");
        args.put("questionNum", questionNum);
        args.put("FALSE", "F");
        args.put("SCORE_EXCEPTION_NOTTOSCORE", "0");
        args.put("SCORE_EXCEPTION_UPFULLMARKS_update", "1");
        args.put("expMark", expMark);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String illegalSql = "     AND sc.studentId NOT IN (\tSELECT DISTINCT studentId \tFROM illegal  \tWHERE examPaperNum={examPaperNum}   ";
        if (null != schoolNum && !"-1".equals(schoolNum)) {
            illegalSql = illegalSql + "\tAND testingCentreId={schoolNum}   ";
        }
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            illegalSql = illegalSql + "\tAND examinationRoomNum={examroomNum}   ";
        }
        String str = illegalSql + "\tAND  type={EXCEPTION_TYPE_MISSING}   )   ";
        String sql3 = "SELECT res.*,stu.studentId realStudentId,stu.studentName,stu.studentNum,sch.schoolName,baseg.gradeName,cla.className,er.examinationRoomName,def.fullScore,def.answer stdAnswer,def.questionType  FROM(SELECT sc.*,ill.type  FROM(SELECT id scoreId,regId ext1,questionNum,questionScore,studentId,examPaperNum,schoolNum,gradeNum,classNum,examinationRoomNum,isException,'' answer  FROM score WHERE examPaperNum={examPaperNum}  ";
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            sql = sql3 + "AND examinationRoomNum={examroomNum}  AND questionNum={questionNum}  ";
        } else {
            if (null != schoolNum && !"-1".equals(schoolNum)) {
                sql3 = sql3 + "AND testingCentreId={schoolNum}   ";
            }
            sql = sql3 + "AND questionNum='" + questionNum + "'    AND continued= {FALSE}   ";
        }
        if (null != expMark && "-1".equals(expMark)) {
            sql = sql + "AND (isException={SCORE_EXCEPTION_NOTTOSCORE} OR isException={SCORE_EXCEPTION_UPFULLMARKS_update} OR isException='5' ) ";
        }
        if (null != expMark && (expMark.equals("0") || expMark.equals("1") || expMark.equals("5"))) {
            sql = sql + "AND isException ={expMark}    ";
        }
        String sql4 = sql + ")sc  LEFT JOIN (SELECT regId,examPaperNum,schoolNum,examinationRoomNum,studentId,type FROM illegal  WHERE examPaperNum={examPaperNum}  ";
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            sql4 = sql4 + "AND examinationRoomNum={examroomNum}  ";
        } else if (null != schoolNum && !"-1".equals(schoolNum)) {
            sql4 = sql4 + "AND testingCentreId={schoolNum}   ";
        }
        String sql5 = sql4 + "AND type={EXCEPTION_TYPE_MISSING} ) ill  ON sc.studentId=ill.studentId  WHERE ill.studentId IS NULL) res  LEFT JOIN (SELECT id,studentId,studentNum,studentName,schoolNum,gradeNum,classNum   FROM student WHERE  ";
        if (null == schoolNum || !"-1".equals(schoolNum)) {
        }
        String sql6 = sql5 + "gradeNum={gradeNum})stu  ON res.studentId=stu.id  LEFT JOIN(SELECT id,schoolNum,schoolName FROM school  ";
        if (null == schoolNum || !"-1".equals(schoolNum)) {
        }
        String sql7 = sql6 + ")sch  ON stu.schoolNum=sch.id   LEFT JOIN(SELECT gradeNum,gradeName FROM basegrade WHERE gradeNum={gradeNum})baseg  ON stu.gradeNum=baseg.gradeNum   LEFT JOIN (SELECT id,classNum,className FROM class WHERE  ";
        if (null == schoolNum || !"-1".equals(schoolNum)) {
        }
        String sql8 = sql7 + "gradeNum={gradeNum})cla   ON stu.classNum=cla.id   LEFT JOIN(SELECT id,examinationRoomNum,examinationRoomName FROM examinationroom  WHERE  ";
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            sql2 = sql8 + "id={examroomNum}  ";
        } else {
            String sql9 = sql8 + "examNum={examNum}  ";
            if (null != schoolNum && !"-1".equals(schoolNum)) {
                sql9 = sql9 + "AND testingCentreId={schoolNum}   ";
            }
            sql2 = sql9 + "AND gradeNum={gradeNum}  ";
        }
        String sql10 = sql2 + ")er  ON res.examinationRoomNum=er.id   LEFT JOIN(SELECT id,questionNum,fullScore,answer,questionType   FROM define WHERE id={questionNum} AND examPaperNum={examPaperNum}  UNION  SELECT id,questionNum,fullScore,'' answer,'1' questionType   FROM subdefine WHERE id={questionNum}  AND examPaperNum={examPaperNum}  )def   ON res.questionNum=def.id   ORDER BY stu.studentId";
        this.log.info("--sql-查询每种得分异常--的题目详细信息---" + sql10);
        return this.dao2._queryBeanList(sql10, Questionimage.class, args);
    }

    public List getSentenImg(String schoolNum, String gradeNum, String examPaperNum, String questionNum, String examroomNum, String expMark, String ddoub, String examNum) {
        String sql;
        String rk2;
        String sql2;
        String sql3;
        String sql4;
        String sql5;
        Map args = new HashMap();
        args.put("TRUE", "T");
        args.put("ddoub", ddoub);
        args.put("questionNum", questionNum);
        args.put("examPaperNum", examPaperNum);
        String sql6 = "select DISTINCT r.id,r.type,r.exampaperNum,r.questionNum,r.questionScore, r.studentId,stu.studentName ext1,r.schoolNum,s.schoolName ext2,r.classNum,c.className ext3,r.gradeNum,e.examinationRoomName examinationRoomNum ,de.fullScore description ,tas.scoreid testMark ,qms.makType `status` from remark r,school s,student stu,define de,class c,examinationroom e ,task tas ,questiongroup q,questiongroup_question qq ,questiongroup_mark_setting qms  where 1=1 and de.child_question={TRUE}  and stu.jie=c.jie  ";
        if (ddoub.equals("T") || ddoub.equals("F")) {
            sql6 = sql6 + " and r.`status`={ddoub}  ";
        }
        String[] s = questionNum.split(Const.STRING_SEPERATOR);
        if (s.length == 1) {
            sql = sql6 + "  AND r.questionNum={questionNum}  ";
        } else {
            args.put("sgroupNum", s[1]);
            String questionNumstr = this.dao2._queryStr("select GROUP_CONCAT(questionNum separator ',') from questiongroup_question where exampaperNum={examPaperNum} and groupNum={sgroupNum} ", args);
            args.put("questionNumstr", questionNumstr);
            sql = sql6 + "  AND r.questionNum in ({questionNumstr[]}) ";
        }
        if ("0".equals(expMark)) {
            sql = sql + " AND r.type ={Reg_Th_Log_RE_JUDGMENG}    ";
        } else if ("1".equals(expMark)) {
            sql = sql + " AND r.type ='1'    ";
        }
        String str = (sql + " AND s.schoolNum=r.schoolNum\tand r.studentId=stu.studentId and c.classNum=r.classNum   and de.questionNum=r.questionNum and r.exampaperNum=de.examPaperNum  and e.id=r.examinationRoomNum and tas.exampaperNum=r.exampaperNum  and tas.questionNum=r.questionNum and tas.studentId=r.studentId  and tas.examinationRoomNum=r.examinationRoomNum   ") + "and qq.exampaperNum=r.exampaperNum and qq.questionNum=r.questionNum    and q.groupNum=qq.groupNum and q.exampaperNum=r.exampaperNum   and qms.exampaperNum=r.exampaperNum and   qms.questionGroupNum=qq.groupNum\tORDER BY studentId  ";
        String gNum = questionNum;
        String[] s2 = questionNum.split(Const.STRING_SEPERATOR);
        if (s2.length == 1) {
            rk2 = "AND questionNum={questionNum}  ";
        } else {
            args.put("sgroupNum", s2[1]);
            String questionNumstr2 = this.dao2._queryStr("select GROUP_CONCAT(questionNum separator ',') from questiongroup_question where exampaperNum={examPaperNum} and groupNum={sgroupNum} ", args);
            args.put("questionNumstr", questionNumstr2);
            rk2 = "   AND questionNum in ({questionNumstr[]}) ";
            gNum = s2[1];
        }
        String rk1 = ",rk.ext4 from ";
        String rk3 = "(SELECT rek2.*,group_concat(concat_ws('#',tea.teacherNum,tea.teacherName,tk.questionScore) SEPARATOR '#') ext4 FROM (SELECT id,type,exampaperNum,questionNum,questionScore,scoreId FROM remark WHERE `status`={ddoub} AND exampaperNum={examPaperNum} AND type ='1' " + rk2 + ")rek2 LEFT JOIN task tk ON tk.exampaperNum = rek2.exampaperNum AND tk.scoreId = rek2.scoreId AND tk.groupNum = {gNum}  LEFT JOIN user u ON u.id = tk.insertUser LEFT JOIN teacher tea ON tea.id = u.userid GROUP BY rek2.id ";
        if ("0".equals(expMark)) {
            rk3 = "(SELECT id,type,exampaperNum,questionNum,questionScore,scoreId FROM markerror WHERE exampaperNum={examPaperNum} AND type ={Reg_Th_Log_RE_JUDGMENG} " + rk2;
            rk1 = " from ";
        }
        String sql7 = ("SELECT DISTINCT rk.id,rk.type,rk.exampaperNum,rk.questionNum,rk.questionScore,sc.studentId,stu.studentName ext1,sc.schoolNum ,sch.schoolName ext2,sc.classNum,cla.className ext3,sc.gradeNum,er.examinationRoomName examinationRoomNum,def.fullScore description,rk.scoreId testMark,qms.makType `status`,stu.studentNum,def.cross_page    " + rk1 + rk3) + ") rk  LEFT JOIN (SELECT id,studentId,examinationRoomNum,schoolNum,classNum,gradeNum   FROM score WHERE examPaperNum={examPaperNum}   ";
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            sql7 = sql7 + "AND examinationRoomNum={examroomNum}  ";
        }
        if (s2.length == 1) {
            sql2 = sql7 + "AND questionNum={questionNum}  ";
        } else {
            args.put("sgroupNum", s2[1]);
            String questionNumstr3 = this.dao2._queryStr("select GROUP_CONCAT(questionNum separator ',') from questiongroup_question where exampaperNum={examPaperNum} and groupNum={sgroupNum} ", args);
            args.put("questionNumstr", questionNumstr3);
            sql2 = sql7 + "AND questionNum in ({questionNumstr[]}) ";
        }
        String sql8 = sql2 + ")sc  ON rk.scoreId=sc.id  LEFT JOIN (SELECT id,studentId,studentName,classNum,gradeNum,schoolNum,studentNum  FROM student WHERE  gradeNum={gradeNum}  ";
        if (null != schoolNum && !"-1".equals(schoolNum)) {
            sql8 = sql8 + " AND schoolNum={schoolNum}  ";
        }
        String sql9 = sql8 + ")stu  ON sc.studentId=stu.id  LEFT JOIN (SELECT id,examinationRoomNum,examinationRoomName FROM examinationroom  WHERE ";
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            sql3 = sql9 + "id={examroomNum}  ";
        } else {
            String sql10 = sql9 + " examNum='" + examNum + "'  ";
            if (null != schoolNum && !"-1".equals(schoolNum)) {
                sql10 = sql10 + "AND schoolNum={schoolNum}  ";
            }
            sql3 = sql10 + "AND gradeNum={gradeNum}  ";
        }
        String sql11 = sql3 + ")er  ON sc.examinationRoomNum=er.id  LEFT JOIN (SELECT id,schoolNum,schoolName  FROM school  ";
        if (null != schoolNum && !"-1".equals(schoolNum)) {
            sql11 = sql11 + "WHERE id={schoolNum} ";
        }
        String sql12 = sql11 + ")sch ON sc.schoolNum=sch.id  LEFT JOIN (SELECT id,classNum,className FROM class WHERE  ";
        if (null != schoolNum && !"-1".equals(schoolNum)) {
            sql12 = sql12 + "schoolNum={schoolNum}   AND   ";
        }
        String sql13 = sql12 + "gradeNum={gradeNum}) cla ON sc.classNum=cla.id   LEFT JOIN (SELECT id,examPaperNum,questionNum,fullScore,cross_page  FROM define  WHERE  ";
        if (s2.length == 1) {
            sql4 = sql13 + "id={questionNum}  ";
        } else {
            sql4 = sql13 + "examPaperNum={examPaperNum}  AND isParent='0'  ";
        }
        String sql14 = sql4 + "UNION   SELECT subdef.id,def.examPaperNum,subdef.questionNum,subdef.fullScore,subdef.cross_page   FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE  def.examPaperNum={examPaperNum} AND def.isParent='1'    ";
        if (s2.length == 1) {
            sql14 = sql14 + "subdef.id={questionNum}  OR def.id={questionNum}  ";
        }
        String sql15 = sql14 + "     ) def ON rk.questionNum=def.id  LEFT JOIN (SELECT exampaperNum,groupNum,groupName FROM questiongroup WHERE exampaperNum={examPaperNum}  ";
        if (s2.length != 1) {
            sql15 = sql15 + "AND groupNum={sgroupNum}  ";
        }
        String sql16 = sql15 + ") qg  ON qg.exampaperNum=rk.exampaperNum  LEFT JOIN (SELECT exampaperNum,groupNum,questionNum FROM questiongroup_question  WHERE exampaperNum={examPaperNum}  ";
        if (s2.length == 1) {
            sql5 = sql16 + "AND questionNum={questionNum}  ";
        } else {
            sql5 = sql16 + "AND groupNum={sgroupNum}   ";
        }
        String sql17 = sql5 + ")qq  ON qg.groupNum=qq.groupNum  LEFT JOIN(SELECT exampaperNum,groupNum,makType FROM questiongroup_mark_setting WHERE exampaperNum={examPaperNum} ";
        if (s2.length != 1) {
            sql17 = sql17 + "AND groupNum={sgroupNum}  ";
        }
        String sql18 = sql17 + ")qms  ON qg.groupNum=qms.groupNum  WHERE qq.groupNum IS NOT NULL";
        args.put("sgroupNum", s2[1]);
        args.put("Reg_Th_Log_RE_JUDGMENG", "2");
        args.put("gNum", gNum);
        args.put("examroomNum", examroomNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        return this.dao2._queryBeanList(sql18, Remark.class, args);
    }

    public List getExpQuestionImg_info_modified(String schoolNum, String gradeNum, String examPaperNum, String questionNum, String examroomNum, String expMark, String examNum) {
        String sql;
        String sql2;
        String illegalSql = "     AND sc.studentId NOT IN (\tSELECT DISTINCT studentId \tFROM illegal  \tWHERE 1=1  \tAND examPaperNum={examPaperNum}   ";
        if (null != schoolNum && !"-1".equals(schoolNum)) {
            illegalSql = illegalSql + "\tAND schoolNum={schoolNum}  ";
        }
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            illegalSql = illegalSql + "\tAND examinationRoomNum={examroomNum}   ";
        }
        String illegalSql2 = illegalSql + "\tAND  type={EXCEPTION_TYPE_MISSING}   )   ";
        String sql3 = "SELECT DISTINCT sc.id,sc.studentId,stu.studentName,sc.schoolNum,sch.schoolName,stu.gradeNum,sc.examinationRoomNum,er.examinationRoomName,sc.questionScore,sc.answer answer,sc.classNum,sc.questionNum,sc.examPaperNum,sc.isException,df.fullScore,df.answer stdAnswer,df.questionType   FROM score sc   LEFT JOIN illegal lgal ON  sc.studentId=lgal.studentId    AND sc.examPaperNum=lgal.examPaperNum AND sc.schoolNum=lgal.schoolNum    AND sc.examinationRoomNum=lgal.examinationRoomNum    LEFT JOIN student stu    ON sc.studentId=stu.studentId   LEFT JOIN school sch    ON sc.schoolNum=sch.schoolNum   LEFT JOIN questionimage qi   ON sc.studentId=qi.studentId AND sc.examPaperNum=qi.examPaperNum   AND sc.questionNum=qi.questionNum  AND sc.examinationRoomNum=qi.examinationRoomNum   AND sc.schoolNum=qi.schoolNum AND sc.page=qi.page    LEFT JOIN( SELECT id,questionNum,fullScore,answer,questionType  FROM define  WHERE examPaperNum={examPaperNum} AND isParent='0'  UNION ALL  SELECT subdef.id,subdef.questionNum,subdef.fullScore,'' answer,def.questionType  FROM define def   LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE def.examPaperNum={examPaperNum} AND def.isParent='1' )df    ON  df.child_question={TRUE}   AND   sc.examPaperNum=df.examPaperNum AND sc.questionNum=df.questionNum      LEFT JOIN examinationroom er     ON sc.examinationRoomNum=er.id    WHERE 1=1    AND sc.examPaperNum={examPaperNum}   ";
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            sql3 = sql3 + "AND sc.examinationRoomNum={examroomNum}  ";
        }
        if (null != schoolNum && !"-1".equals(schoolNum)) {
            sql3 = sql3 + "AND sc.schoolNum={schoolNum}   ";
        }
        String sql4 = sql3 + "AND sc.gradeNum={gradeNum}  AND sc.questionNum={questionNum}  " + illegalSql2;
        if (null != expMark && "-1".equals(expMark)) {
            sql4 = sql4 + "AND sc.isException IS NOT null   AND  (isException={MODIFIED_SCORE_EXCEPTION_NOTTOSCORE} OR isException={MODIFIED_SCORE_EXCEPTION_UPFULLMARKS_update} OR isException='15' )   ";
        }
        if (null != expMark && (expMark.equals("10") || expMark.equals("11") || expMark.equals(Const.Data_type_of_Wrong))) {
            sql4 = sql4 + "AND sc.isException ={expMark}    ";
        }
        String str = sql4 + "\t\tORDER BY sc.studentId ";
        String illegalSql3 = "     AND sc.studentId NOT IN (\tSELECT DISTINCT studentId \tFROM illegal  \tWHERE examPaperNum={examPaperNum}  ";
        if (null != schoolNum && !"-1".equals(schoolNum)) {
            illegalSql3 = illegalSql3 + "\tAND schoolNum={schoolNum}   ";
        }
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            illegalSql3 = illegalSql3 + "\tAND examinationRoomNum={examroomNum}   ";
        }
        String str2 = illegalSql3 + "\tAND  type={EXCEPTION_TYPE_MISSING}   )   ";
        String sql5 = "SELECT res.*,stu.studentId realStudentId,stu.studentName,stu.studentNum,sch.schoolName,baseg.gradeName,cla.className,er.examinationRoomName,def.fullScore,def.answer stdAnswer,def.questionType  FROM(SELECT sc.*,ill.type  FROM(SELECT id scoreId,regId ext1,questionNum,questionScore,studentId,examPaperNum,schoolNum,gradeNum,classNum,examinationRoomNum,isException,'' answer  FROM score WHERE examPaperNum={examPaperNum}  ";
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            sql = sql5 + "AND examinationRoomNum={examroomNum}  AND questionNum={questionNum}  ";
        } else {
            if (null != schoolNum && !"-1".equals(schoolNum)) {
                sql5 = sql5 + "AND schoolNum={schoolNum}   ";
            }
            sql = sql5 + "AND questionNum='" + questionNum + "'      AND continued= {FALSE}   ";
        }
        if (null != expMark && "-1".equals(expMark)) {
            sql = sql + "AND (isException={MODIFIED_SCORE_EXCEPTION_NOTTOSCORE} OR isException={MODIFIED_SCORE_EXCEPTION_UPFULLMARKS_update} OR isException='15' ) ";
        }
        if (null != expMark && (expMark.equals("10") || expMark.equals("11") || expMark.equals(Const.Data_type_of_Wrong))) {
            sql = sql + "AND isException ={expMark}    ";
        }
        String sql6 = sql + ")sc  LEFT JOIN (SELECT regId,examPaperNum,schoolNum,examinationRoomNum,studentId,type FROM illegal  WHERE examPaperNum={examPaperNum}  ";
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            sql6 = sql6 + "AND examinationRoomNum={examroomNum}  ";
        } else if (null != schoolNum && !"-1".equals(schoolNum)) {
            sql6 = sql6 + "AND schoolNum={schoolNum}   ";
        }
        String sql7 = sql6 + "AND type={EXCEPTION_TYPE_MISSING} ) ill  ON sc.studentId=ill.studentId  WHERE ill.studentId IS NULL) res  LEFT JOIN (SELECT id,studentId,studentNum,studentName,schoolNum,gradeNum,classNum   FROM student WHERE  ";
        if (null != schoolNum && !"-1".equals(schoolNum)) {
            sql7 = sql7 + "schoolNum={schoolNum} AND  ";
        }
        String sql8 = sql7 + "gradeNum={gradeNum})stu  ON res.studentId=stu.id  LEFT JOIN(SELECT id,schoolNum,schoolName FROM school  ";
        if (null != schoolNum && !"-1".equals(schoolNum)) {
            sql8 = sql8 + "WHERE id='" + schoolNum + "'  ";
        }
        String sql9 = sql8 + ")sch  ON stu.schoolNum=sch.id   LEFT JOIN(SELECT gradeNum,gradeName FROM basegrade WHERE gradeNum={gradeNum} )baseg  ON stu.gradeNum=baseg.gradeNum   LEFT JOIN (SELECT id,classNum,className FROM class WHERE  ";
        if (null != schoolNum && !"-1".equals(schoolNum)) {
            sql9 = sql9 + "schoolNum={schoolNum}  AND   ";
        }
        String sql10 = sql9 + "gradeNum={gradeNum} )cla   ON stu.classNum=cla.id   LEFT JOIN(SELECT id,examinationRoomNum,examinationRoomName FROM examinationroom  WHERE  ";
        if (null != examroomNum && !"-1".equals(examroomNum)) {
            sql2 = sql10 + "id='" + examroomNum + "'  ";
        } else {
            String sql11 = sql10 + "examNum='" + examNum + "'  ";
            if (null != schoolNum && !"-1".equals(schoolNum)) {
                sql11 = sql11 + "AND schoolNum={schoolNum}   ";
            }
            sql2 = sql11 + "AND gradeNum={gradeNum}  ";
        }
        String sql12 = sql2 + ")er  ON res.examinationRoomNum=er.id   LEFT JOIN(SELECT id,questionNum,fullScore,answer,questionType  FROM define   WHERE id={questionNum} AND examPaperNum={examPaperNum}  UNION ALL  SELECT id,questionNum,fullScore,'' answer,'1' questionType  FROM subdefine subdef  WHERE id={questionNum} AND examPaperNum={examPaperNum}  )def   ON res.questionNum=def.id   ORDER BY stu.studentId";
        this.log.info("--sql-查询已经修改过的得分异常--的题目详细信息---" + sql12);
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("examroomNum", (Object) examroomNum).put("EXCEPTION_TYPE_MISSING", (Object) "0").put("TRUE", (Object) "T").put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("questionNum", (Object) questionNum).put("MODIFIED_SCORE_EXCEPTION_NOTTOSCORE", (Object) "10").put("MODIFIED_SCORE_EXCEPTION_UPFULLMARKS_update", (Object) "11").put("expMark", (Object) expMark).put("FALSE", (Object) "F");
        return this.dao2._queryBeanList(sql12, Questionimage.class, args);
    }

    public byte[] getImage(String examPaperNum, String questionNum, String studentId) {
        this.log.info("getImage  sql: SELECT img FROM questionimage WHERE exampaperNum={examPaperNum} AND studentId={studentId} AND questionNum={questionNum} ");
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("questionNum", (Object) questionNum);
        List<byte[]> list = this.dao2._queryBlobList("SELECT img FROM questionimage WHERE exampaperNum={examPaperNum} AND studentId={studentId} AND questionNum={questionNum} ", args);
        this.log.info("得分异常--查询每个题目图片--sql--SELECT img FROM questionimage WHERE exampaperNum={examPaperNum} AND studentId={studentId} AND questionNum={questionNum} ");
        if (null == list) {
            return null;
        }
        if (list.size() != 1) {
            return this.dao2._queryBlob("SELECT img FROM questionimage WHERE exampaperNum={examPaperNum} AND studentId={studentId} AND questionNum={questionNum} ", args);
        }
        return list.get(0);
    }

    public Integer updateScore(String studentId, String examPaperNum, String questionNum, double inputScore, double oldScore, String qAnswer, String qType, String stuClassNum, String schoolNum, String examroomNum, String qException, String scoreId) {
        List<String> sqls = new ArrayList<>();
        String exceptionStr = "1" + qException;
        String qScore = this.examDao.getQuestionScoreByQuestionInfo(Integer.parseInt(examPaperNum), questionNum, Double.toString(inputScore));
        this.log.info("sql-得分异常--修改题目分数--UPDATE score SET questionScore={qScore} ,isException={exceptionStr} ,regScore={inputScore} ,isModify='T' WHERE  examPaperNum={examPaperNum}   AND questionNum={questionNum}  AND id={scoreId}  ");
        this.log.info("sql2-修改题目得分--写correctlog日志--INSERT INTO correctlog(questionNum,questionScore,studentId,examPaperNum,answer,qtype,oldScore,insertUser,insertDate,schoolNum,classNum,examinationRoomNum) VALUES({questionNum} ,{inputScore},{studentId},{examPaperNum},{qAnswer},{qType},{oldScore},'0', {CurrentTime},{schoolNum},{stuClassNum},{examroomNum}) ");
        sqls.add("UPDATE score SET questionScore={qScore} ,isException={exceptionStr} ,regScore={inputScore} ,isModify='T' WHERE  examPaperNum={examPaperNum}   AND questionNum={questionNum}  AND id={scoreId}  ");
        sqls.add("INSERT INTO correctlog(questionNum,questionScore,studentId,examPaperNum,answer,qtype,oldScore,insertUser,insertDate,schoolNum,classNum,examinationRoomNum) VALUES({questionNum} ,{inputScore},{studentId},{examPaperNum},{qAnswer},{qType},{oldScore},'0', {CurrentTime},{schoolNum},{stuClassNum},{examroomNum}) ");
        Map args = StreamMap.create().put("qScore", (Object) qScore).put("exceptionStr", (Object) exceptionStr).put("inputScore", (Object) Double.valueOf(inputScore)).put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum).put("scoreId", (Object) scoreId).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("qAnswer", (Object) qAnswer).put("qType", (Object) qType).put("oldScore", (Object) Double.valueOf(oldScore)).put("CurrentTime", (Object) DateUtil.getCurrentTime()).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("stuClassNum", (Object) stuClassNum).put("examroomNum", (Object) examroomNum);
        this.dao2._batchExecute(sqls, args);
        return 1;
    }

    public Integer updateScore_modified(String studentId, String examPaperNum, String questionNum, double inputScore, double oldScore, String qAnswer, String qType, String stuClassNum, String schoolNum, String examroomNum, String qException, String scoreId) {
        List<String> sqls = new ArrayList<>();
        String qScore = this.examDao.getQuestionScoreByQuestionInfo(Integer.parseInt(examPaperNum), questionNum, Double.toString(inputScore));
        this.log.info("sql-得分异常--再次修改题目分数--UPDATE score SET questionScore={qScore},regScore={inputScore}    WHERE  examPaperNum={examPaperNum}    AND questionNum={questionNum}  AND id={scoreId}   ");
        this.log.info("sql2-再次修改题目得分--写correctlog日志--INSERT INTO correctlog(questionNum,questionScore,studentId,examPaperNum,answer,qtype,oldScore,insertUser,insertDate,schoolNum,classNum,examinationRoomNum) VALUES({questionNum},{inputScore},{studentId},{examPaperNum},{qAnswer},{qType},{oldScore},'0',{CurrentTime},{schoolNum},{stuClassNum},{examroomNum} )");
        sqls.add("UPDATE score SET questionScore={qScore},regScore={inputScore}    WHERE  examPaperNum={examPaperNum}    AND questionNum={questionNum}  AND id={scoreId}   ");
        sqls.add("INSERT INTO correctlog(questionNum,questionScore,studentId,examPaperNum,answer,qtype,oldScore,insertUser,insertDate,schoolNum,classNum,examinationRoomNum) VALUES({questionNum},{inputScore},{studentId},{examPaperNum},{qAnswer},{qType},{oldScore},'0',{CurrentTime},{schoolNum},{stuClassNum},{examroomNum} )");
        Map args = StreamMap.create().put("qScore", (Object) qScore).put("inputScore", (Object) Double.valueOf(inputScore)).put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum).put("scoreId", (Object) scoreId).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("qAnswer", (Object) qAnswer).put("qType", (Object) qType).put("oldScore", (Object) Double.valueOf(oldScore)).put("CurrentTime", (Object) DateUtil.getCurrentTime()).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("stuClassNum", (Object) stuClassNum).put("examroomNum", (Object) examroomNum);
        this.dao2._batchExecute(sqls, args);
        return 1;
    }

    public List getRoomAndExamineeLength(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryBeanList("SELECT id,examinationRoomLength,examineeLength    FROM exam    WHERE examNum={examNum} ", Exam.class, args);
    }

    public List isExistTheExaminee(String examNum, String testCenter, String gradenum, String roomNum, String inputExaminee, String examPaperNum, int page, String groupNum, String oldStudentId, String oldExamroomId, String examroomornot) {
        List otherExamroomCNumList;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("testCenter", testCenter);
        args.put("gradenum", gradenum);
        args.put("inputExaminee", inputExaminee);
        args.put("examPaperNum", examPaperNum);
        this.log.info(" 考场验证 考号表sql----SELECT en.id,en.examinationRoomNum,en.examineeNum,en.studentId,en.examNum,en.gradeNum,en.schoolNum ,stu.studentId stuId   FROM (SELECT exami.id,exami.examinationRoomNum,exami.examineeNum,exami.studentId,exami.examNum,exami.gradeNum,exami.schoolNum    FROM examinationnum exami  LEFT JOIN exampaper exp  on exp.subjectNum = exami.subjectNum   WHERE exami.examNum={examNum}   AND exami.testingCentreId={testCenter} AND exami.gradeNum={gradenum}   AND exami.examineeNum={inputExaminee}  AND exp.exampaperNum = {examPaperNum} )  en  LEFT JOIN student stu ON en.studentId=stu.id");
        Examinationnum examinationnum = new Examinationnum();
        examinationnum.setExt5("F");
        examinationnum.setExt6("F");
        examinationnum.setExt7("F");
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT en.id,en.examinationRoomNum,en.examineeNum,en.studentId,en.examNum,en.gradeNum,en.schoolNum ,stu.studentId stuId   FROM (SELECT exami.id,exami.examinationRoomNum,exami.examineeNum,exami.studentId,exami.examNum,exami.gradeNum,exami.schoolNum    FROM examinationnum exami  LEFT JOIN exampaper exp  on exp.subjectNum = exami.subjectNum   WHERE exami.examNum={examNum}   AND exami.testingCentreId={testCenter} AND exami.gradeNum={gradenum}   AND exami.examineeNum={inputExaminee}  AND exp.exampaperNum = {examPaperNum} )  en  LEFT JOIN student stu ON en.studentId=stu.id", Examinationnum.class, args);
        if (null != _queryBeanList && _queryBeanList.size() == 1) {
            examinationnum.setExt5("T");
            if (null == ((Examinationnum) _queryBeanList.get(0)).getStuId() || "".equals(((Examinationnum) _queryBeanList.get(0)).getStuId())) {
                _queryBeanList.add(examinationnum);
                return _queryBeanList;
            }
            ((Examinationnum) _queryBeanList.get(0)).setExt6("T");
            args.put("ExaminationRoomNum", ((Examinationnum) _queryBeanList.get(0)).getExaminationRoomNum());
            this.log.info(" 考号验证考场表sql2：SELECT * FROM examinationroom WHERE id={ExaminationRoomNum} ");
            new ArrayList();
            List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT * FROM examinationroom WHERE id={ExaminationRoomNum} ", Examinationroom.class, args);
            if (null != _queryBeanList2 && _queryBeanList2.size() == 1) {
                ((Examinationnum) _queryBeanList.get(0)).setExaminationRoomName(((Examinationroom) _queryBeanList2.get(0)).getExaminationRoomName());
                examinationnum.setExt7("T");
                List<RegExaminee> regOldList = authDataExistsFromRegExamineeList("", examPaperNum, oldStudentId, testCenter, page, groupNum, oldExamroomId, null, null, null, examroomornot);
                List<RegExaminee> regNewList = authDataExistsFromRegExamineeList("", examPaperNum, ((Examinationnum) _queryBeanList.get(0)).getStudentID().toString(), testCenter, page, null, ((Examinationnum) _queryBeanList.get(0)).getExaminationRoomNum(), regOldList, "T", null, examroomornot);
                ((Examinationnum) _queryBeanList.get(0)).setExt3("F");
                if (null != groupNum && !"".equals(groupNum) && null != (otherExamroomCNumList = authCNumInOtherExamroom(examNum, testCenter, gradenum, examPaperNum, groupNum, oldExamroomId, oldStudentId, examroomornot))) {
                    ((Examinationnum) _queryBeanList.get(0)).setExt3("T");
                    if (null != examroomornot && "0".equals(examroomornot)) {
                        ((Examinationnum) _queryBeanList.get(0)).setExt4(((RegExaminee) otherExamroomCNumList.get(0)).getExaminationRoomName() + "存在" + otherExamroomCNumList.size() + "个相同试卷组号的试卷");
                    }
                    if (null != examroomornot && "1".equals(examroomornot)) {
                        String studentId = ((RegExaminee) otherExamroomCNumList.get(0)).getExt2();
                        if (null == studentId) {
                            ((Examinationnum) _queryBeanList.get(0)).setExt4("有未识别的学生存在" + otherExamroomCNumList.size() + "个相同试卷组号的试卷");
                        } else {
                            ((Examinationnum) _queryBeanList.get(0)).setExt4("ID号：" + studentId + "存在" + otherExamroomCNumList.size() + "个相同试卷组号的试卷");
                        }
                    }
                }
                if (null != regNewList && regNewList.size() > 0) {
                    String stuName = getStudentById(regNewList.get(0).getStudentId().toString());
                    ((Examinationnum) _queryBeanList.get(0)).setExt2("姓名:" + stuName);
                    ((Examinationnum) _queryBeanList.get(0)).setExt1("T");
                } else {
                    ((Examinationnum) _queryBeanList.get(0)).setExt1("F");
                }
                ((Examinationnum) _queryBeanList.get(0)).setExt5("T");
                ((Examinationnum) _queryBeanList.get(0)).setExt6("T");
                ((Examinationnum) _queryBeanList.get(0)).setExt7("T");
                return _queryBeanList;
            }
            _queryBeanList.add(examinationnum);
            return _queryBeanList;
        }
        _queryBeanList.add(examinationnum);
        return _queryBeanList;
    }

    public List authCNumInOtherExamroom(String examNum, String testCenter, String gradenum, String examPaperNum, String cNum, String examRoomId, String oldStudentId, String examroomornot) {
        String sql;
        String sql2 = "SELECT DISTINCT er.examinationRoomNum,er.examinationRoomName,reg.studentId,reg.cNum,reg.examPaperNum,s.studentName ext1,s.studentId ext2   FROM (SELECT  DISTINCT examinationRoomNum,cNum,studentId,id,examPaperNum   FROM regexaminee  WHERE examPaperNum={examPaperNum}  ";
        if (null != examroomornot && "0".equals(examroomornot)) {
            sql2 = sql2 + "AND examinationRoomNum<>{examRoomId}    ";
        }
        if (null != examroomornot && "1".equals(examroomornot)) {
            sql2 = sql2 + "AND studentId<>{oldStudentId}   ";
        }
        String sql3 = sql2 + "AND cNum='" + cNum + "'    ) reg   LEFT JOIN (SELECT id,examinationRoomNum,examinationRoomName FROM examinationroom WHERE   ";
        if (null != examroomornot && "0".equals(examroomornot)) {
            sql = sql3 + " id<>{examRoomId}  )er   ";
        } else {
            sql = sql3 + " examNum={examNum} AND testingCentreId={testCenter} AND gradeNum={gradenum}   ) er   ";
        }
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("examRoomId", (Object) examRoomId).put("oldStudentId", (Object) oldStudentId).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testCenter", (Object) testCenter).put("gradenum", (Object) gradenum);
        List list = this.dao2._queryBeanList((sql + " ON reg.examinationRoomNum=er.id ") + " LEFT JOIN  student  s on s.id= reg.studentId", RegExaminee.class, args);
        if (null != list && list.size() > 0) {
            return list;
        }
        return null;
    }

    public String getStudentById(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT id,studentId,studentNum,studentName FROM student WHERE id={id} ", Student.class, args);
        if (null != _queryBeanList && _queryBeanList.size() > 0) {
            return ((Student) _queryBeanList.get(0)).getStudentName();
        }
        return "";
    }

    public List searchCreateExam(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryBeanList("SELECT ep.id,ep.examNum,ep.examPaperNum,ep.gradeNum,ep.subjectNum,sub.subjectName  FROM exampaper ep  LEFT JOIN  `subject`  sub   ON ep.subjectNum=sub.subjectNum  WHERE examNum={examNum}   ORDER BY sub.subjectNum ASC", Exampaper.class, args);
    }

    public List getSchoolGrade() {
        Map args = new HashMap();
        List returnList = new ArrayList();
        ArrayList arrayList = new ArrayList();
        List schList = this.dao2.queryBeanList("SELECT DISTINCT schoolNum,schoolName    FROM school    ORDER BY schoolNum", School.class);
        if (schList != null && schList.size() > 0) {
            for (int i = 0; i < schList.size(); i++) {
                String SchoolNum = "SchoolNum" + i;
                args.put(SchoolNum, ((School) schList.get(i)).getSchoolNum());
                String sql3 = "SELECT DISTINCT g.gradeNum,g.gradeName,g.schoolNum,sch.schoolName   FROM grade g    LEFT JOIN school sch    ON g.schoolNum=sch.schoolNum     WHERE g.schoolNum={" + SchoolNum + "}    ORDER BY g.gradeNum ASC";
                List lis = this.dao2._queryBeanList(sql3, Grade.class, args);
                if (lis != null && lis.size() > 0) {
                    arrayList.add(lis);
                } else {
                    schList.remove(i);
                }
            }
            returnList.add(schList);
            returnList.add(arrayList);
        }
        return returnList;
    }

    public List searchDefine(String examNum) {
        List list = new ArrayList();
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        List list_1 = this.dao2._queryBeanList("SELECT DISTINCT def.examPaperNum,ep.examNum,ep.gradeNum,ep.subjectNum,sub.subjectName  FROM define def  LEFT JOIN exampaper ep  ON def.examPaperNum=ep.examPaperNum  LEFT JOIN  `subject`  sub   ON ep.subjectNum=sub.subjectNum  WHERE def.examPaperNum IN  (SELECT examPaperNum  FROM exampaper   WHERE examNum={examNum} ) ORDER BY sub.subjectNum ASC", CorrectStatus.class, args);
        List list_2 = this.dao2._queryBeanList("SELECT DISTINCT ep.examPaperNum,ep.examNum,ep.gradeNum,ep.subjectNum,sub.subjectName  FROM exampaper ep   LEFT JOIN   `subject`  sub    ON ep.subjectNum=sub.subjectNum   WHERE ep.examPaperNum  NOT IN   \t\t\t\t(   \t\t\t\t\tSELECT DISTINCT examPaperNum     \t\t\t\t\tFROM define    \t\t\t\t\tWHERE examPaperNum IN    \t\t\t\t\t\t\t\t\t\t(SELECT examPaperNum    \t\t\t\t\t\t\t\t\t\t\tFROM exampaper    \t\t\t\t\t\t\t\t\t\t\tWHERE examNum={examNum}    \t\t\t\t\t\t\t\t\t\t)   \t\t\t\t)   ORDER BY sub.subjectNum ASC", CorrectStatus.class, args);
        if (list_1 != null) {
            list.add(Integer.valueOf(list_1.size()));
        } else {
            list.add(0);
        }
        if (list_2 != null) {
            list.add(Integer.valueOf(list_2.size()));
        } else {
            list.add(0);
        }
        return list;
    }

    public List getGradeDefineDetail(String examNum, String finishOrNot) {
        List ll = getSchoolGrade();
        List schList = (List) ll.get(0);
        List gradeList = (List) ll.get(1);
        List returnList = new ArrayList();
        ArrayList arrayList = new ArrayList();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String sql = "";
        if (schList != null && schList.size() > 0) {
            for (int k = 0; k < schList.size(); k++) {
                ArrayList arrayList2 = new ArrayList();
                int i = 0;
                while (i < ((List) gradeList.get(k)).size()) {
                    String GradeNum = "GradeNum" + i;
                    args.put("GradeNum", ((Grade) ((List) gradeList.get(k)).get(i)).getGradeNum());
                    if (finishOrNot != null && "22".equals(finishOrNot)) {
                        sql = "SELECT DISTINCT df.examPaperNum,ep.gradeNum,ep.subjectNum,sub.subjectName   FROM define df  LEFT JOIN exampaper ep   ON df.examPaperNum=ep.examPaperNum   LEFT JOIN `subject` sub   ON ep.subjectNum=sub.subjectNum   WHERE df.examPaperNum IN   (   SELECT DISTINCT examPaperNum    FROM exampaper    WHERE examNum={examNum}   AND gradeNum={" + GradeNum + "}  )  ORDER BY sub.subjectNum ASC";
                    }
                    if (finishOrNot != null && "21".equals(finishOrNot)) {
                        sql = "SELECT DISTINCT ep.examPaperNum,ep.examNum,ep.gradeNum,ep.subjectNum,sub.subjectName   FROM exampaper ep   LEFT JOIN   `subject`  sub    ON ep.subjectNum=sub.subjectNum   WHERE ep.examPaperNum  NOT IN    \t\t\t\t(    \t\t\t\t\tSELECT DISTINCT examPaperNum    \t\t\t\t\tFROM define      \t\t\t\t\tWHERE examPaperNum IN     \t\t\t\t\t\t\t\t\t\t(SELECT DISTINCT examPaperNum    \t\t\t\t\t\t\t\t\t\t\tFROM exampaper     \t\t\t\t\t\t\t\t\t\t\tWHERE examNum={examNum}    \t\t\t\t\t\t\t\t\t\t\tAND gradeNum={" + GradeNum + "}    \t\t\t\t\t\t\t\t\t\t)   \t\t\t\t)     AND ep.examNum={examNum}  AND ep.gradeNum={" + GradeNum + "}   ORDER BY sub.subjectNum ASC";
                    }
                    List list = this.dao2._queryBeanList(sql, Exampaper.class, args);
                    if (list != null && list.size() > 0) {
                        arrayList2.add(list);
                    } else {
                        ((List) gradeList.get(k)).remove(i);
                        i--;
                    }
                    i++;
                }
                arrayList.add(arrayList2);
            }
        }
        returnList.add(gradeList);
        returnList.add(arrayList);
        return returnList;
    }

    public List viewScanExaminationRoom(String examNum) {
        List list = new ArrayList();
        List recList = getSchoolGrade();
        List schList = (List) recList.get(0);
        List gradeList = (List) recList.get(1);
        int total = 0;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        if (schList != null && schList.size() > 0) {
            for (int j = 0; j < schList.size(); j++) {
                for (int k = 0; k < ((List) gradeList.get(j)).size(); k++) {
                    String GradeNum = "GradeNum" + k;
                    String SchoolNum = "SchoolNum" + k;
                    args.put(GradeNum, ((Grade) ((List) gradeList.get(j)).get(k)).getGradeNum());
                    args.put(SchoolNum, ((Grade) ((List) gradeList.get(j)).get(k)).getSchoolNum());
                    String paperSql = "SELECT DISTINCT examPaperNum,subjectNum  FROM exampaper   WHERE examNum={examNum}     AND gradeNum={" + GradeNum + "}    ORDER BY subjectNum ASC  ";
                    String roomSql = "SELECT DISTINCT id,examinationRoomNum,examinationRoomName   FROM examinationroom    WHERE examNum={examNum}    AND schoolNum={" + SchoolNum + "}    AND gradeNum={" + GradeNum + "}   ORDER BY examinationRoomNum ASC";
                    List plist = this.dao2._queryBeanList(paperSql, Exampaper.class, args);
                    List rlist = this.dao2._queryBeanList(roomSql, Examinationroom.class, args);
                    if (plist != null && rlist != null) {
                        total += plist.size() * rlist.size();
                    }
                }
            }
        }
        List list_1 = this.dao2._queryBeanList("SELECT DISTINCT schoolNum,examPaperNum,examnitionRoom   FROM correctstatus    WHERE examNum={examNum} ", CorrectStatus.class, args);
        if (list_1 != null) {
            list.add(Integer.valueOf(list_1.size()));
            list.add(Integer.valueOf(total - list_1.size()));
        } else {
            list.add(0);
            list.add(Integer.valueOf(total));
        }
        return list;
    }

    public List getScanRoomSuject(String examNum, String scanOrNot) {
        List myList = new ArrayList();
        List recList = getSchoolGrade();
        List schList = (List) recList.get(0);
        List gradeList = (List) recList.get(1);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        if (schList != null && schList.size() > 0) {
            ArrayList arrayList = new ArrayList();
            int i = 0;
            while (i < schList.size()) {
                ArrayList arrayList2 = new ArrayList();
                int k = 0;
                while (k < ((List) gradeList.get(i)).size()) {
                    String GradeNum = "GradeNum" + k;
                    String SchoolNum = "SchoolNum" + k;
                    args.put(GradeNum, ((Grade) ((List) gradeList.get(i)).get(k)).getGradeNum());
                    args.put(SchoolNum, ((Grade) ((List) gradeList.get(i)).get(k)).getSchoolNum());
                    if (scanOrNot != null && scanOrNot.equals("22")) {
                        String sql = "SELECT DISTINCT cs.schoolNum,cs.gradeNum,ep.examPaperNum,ep.subjectNum,sub.subjectName    FROM correctstatus cs   LEFT JOIN exampaper ep    ON cs.examPaperNum=ep.examPaperNum    LEFT JOIN `subject` sub    ON ep.subjectNum=sub.subjectNum    WHERE cs.examPaperNum IN    (  SELECT DISTINCT examPaperNum   FROM exampaper    WHERE examNum={examNum}    AND gradeNum={" + GradeNum + "}  )   AND cs.schoolNum={" + SchoolNum + "}  AND cs.gradeNum={" + GradeNum + "}  ORDER BY ep.subjectNum ASC";
                        List list_1 = this.dao2._queryBeanList(sql, Exampaper.class, args);
                        if (list_1 != null && list_1.size() > 0) {
                            ArrayList arrayList3 = new ArrayList();
                            for (int ii = 0; ii < list_1.size(); ii++) {
                                String SubjectNum = "SubjectNum" + ii;
                                args.put(SubjectNum, ((Exampaper) list_1.get(ii)).getSubjectNum());
                                String sql2 = "SELECT  DISTINCT cs.examPaperNum,cs.examnitionRoom,er.examinationRoomNum,er.examinationRoomName,ep.subjectNum,sub.subjectName,er.gradeNum,g.gradeName,er.schoolNum   FROM correctstatus cs   LEFT JOIN examinationroom er     ON cs.examnitionRoom=er.id   LEFT JOIN exampaper ep   ON cs.examPaperNum=ep.examPaperNum   LEFT JOIN `subject` sub   ON ep.subjectNum=sub.subjectNum   LEFT JOIN grade g    ON cs.gradeNum=g.gradeNum     WHERE cs.examPaperNum=  (  SELECT DISTINCT examPaperNum   FROM examPaper  WHERE examNum={examNum}   AND subjectNum={" + SubjectNum + "}    AND gradeNum={" + GradeNum + "}  )  AND cs.examnitionRoom IN   (  SELECT DISTINCT id   FROM examinationroom   WHERE  examNum={examNum}      AND gradeNum={" + GradeNum + "}  AND schoolNum={" + SchoolNum + "}  )   ORDER BY er.examinationRoomNum ASC";
                                List sl = this.dao2._queryBeanList(sql2, CorrectStatus.class, args);
                                if (sl != null && sl.size() > 0) {
                                    arrayList3.add(sl);
                                }
                            }
                            if (arrayList3 != null && arrayList3.size() > 0) {
                                arrayList2.add(arrayList3);
                            } else {
                                ((List) gradeList.get(i)).remove(k);
                                k--;
                            }
                        } else {
                            ((List) gradeList.get(i)).remove(k);
                            k--;
                        }
                    }
                    if (scanOrNot != null && scanOrNot.equals("21")) {
                        String sql22 = "SELECT  DISTINCT  ep.examPaperNum,ep.gradeNum,g.gradeName,ep.subjectNum,sub.subjectName," + ((Grade) ((List) gradeList.get(i)).get(k)).getSchoolNum() + "  schoolNum   FROM exampaper ep    LEFT JOIN `subject` sub    ON ep.subjectNum=sub.subjectNum    LEFT JOIN grade g    ON ep.gradeNum=g.gradeNum    WHERE ep.examNum={examNum}     AND ep.gradeNum={" + GradeNum + "}     AND ep.examPaperNum NOT IN      (    SELECT DISTINCT examPaperNum    FROM correctstatus    WHERE examPaperNum IN    (   SELECT DISTINCT examPaperNum    FROM exampaper    WHERE examNum={examNum}    AND gradeNum={" + GradeNum + "}   )   AND schoolNum={" + SchoolNum + "}  )";
                        List gs = this.dao2._queryBeanList(sql22, Exampaper.class, args);
                        if (gs != null && gs.size() > 0) {
                            arrayList2.add(gs);
                        } else {
                            ((List) gradeList.get(i)).remove(k);
                            k--;
                        }
                    }
                    k++;
                }
                if (arrayList2 != null && arrayList2.size() > 0) {
                    arrayList.add(arrayList2);
                } else {
                    schList.remove(i);
                    i--;
                }
                i++;
            }
            myList.add(schList);
            myList.add(gradeList);
            myList.add(arrayList);
        }
        return myList;
    }

    public CorrectStatus getScoreCheckCount(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return (CorrectStatus) this.dao2._queryBean("SELECT examNum,SUM(IF(status='0',1,0)) notScan,SUM(IF(status='1',1,0)) nowScan,SUM(IF(status='2',1,0)) finishScan  FROM correctstatus  WHERE examNum={examNum} ", CorrectStatus.class, args);
    }

    public List getCertainCheckDetail(String examNum, String statusType) {
        List recList = getSchoolGrade();
        List schList = (List) recList.get(0);
        List gradeList = (List) recList.get(1);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("statusType", statusType);
        List myList = new ArrayList();
        ArrayList arrayList = new ArrayList();
        if (schList != null && schList.size() > 0) {
            ArrayList arrayList2 = new ArrayList();
            int i = 0;
            while (i < schList.size()) {
                ArrayList arrayList3 = new ArrayList();
                int k = 0;
                while (k < ((List) gradeList.get(i)).size()) {
                    String GradeNum = "GradeNum" + k;
                    String SchoolNum = "SchoolNum" + k;
                    args.put(GradeNum, ((Grade) ((List) gradeList.get(i)).get(k)).getGradeNum());
                    args.put(SchoolNum, ((Grade) ((List) gradeList.get(i)).get(k)).getSchoolNum());
                    List list_1 = this.dao2._queryBeanList("SELECT DISTINCT ep.examPaperNum,ep.subjectNum,sub.subjectName   FROM correctstatus cs   LEFT JOIN exampaper ep   ON cs.examPaperNum=ep.examPaperNum    LEFT JOIN `subject` sub    ON ep.subjectNum=sub.subjectNum    WHERE cs.examPaperNum IN     (   SELECT DISTINCT examPaperNum    FROM exampaper    WHERE examNum={examNum}  AND gradeNum={GradeNum}   )  AND cs.schoolNum={SchoolNum}    ORDER BY ep.subjectNum ASC", Exampaper.class, args);
                    if (list_1 != null && list_1.size() > 0) {
                        ArrayList arrayList4 = new ArrayList();
                        for (int j = 0; j < list_1.size(); j++) {
                            String SubjectNum = "SubjectNum" + j;
                            args.put(SubjectNum, ((Exampaper) list_1.get(j)).getSubjectNum());
                            List lis = this.dao2._queryBeanList("SELECT  cs.schoolNum,cs.gradeNum,g.gradeName,cs.subjectNum,sub.subjectName,cs.examnitionRoom,er.examinationRoomNum,er.examinationRoomName   FROM correctstatus cs   LEFT JOIN grade g   ON cs.gradeNum=g.gradeNum    LEFT JOIN `subject` sub    ON cs.subjectNum=sub.subjectNum     LEFT JOIN examinationroom er     ON cs.examnitionRoom=er.id    WHERE cs.examPaperNum IN    (    SELECT DISTINCT examPaperNum    FROM exampaper   WHERE examNum={examNum}    AND gradeNum={GradeNum}   AND subjectNum={SubjectNum}   )   AND cs.status={statusType}   AND cs.examnitionRoom IN    (   SELECT DISTINCT id    FROM examinationroom   WHERE examNum={examNum}     AND gradeNum={GradeNum}   AND schoolNum={SchoolNum}  )   ORDER BY er.examinationRoomNum", CorrectStatus.class, args);
                            if (lis != null && lis.size() > 0) {
                                arrayList4.add(lis);
                            }
                        }
                        arrayList3.add(arrayList4);
                    }
                    if (arrayList3 != null && arrayList3.size() > 0) {
                        arrayList2.add(arrayList3);
                    } else {
                        ((List) gradeList.get(i)).remove(k);
                        k--;
                    }
                    k++;
                }
                if (arrayList2 != null && arrayList2.size() > 0) {
                    arrayList.add(arrayList2);
                } else {
                    schList.remove(i);
                    i--;
                }
                i++;
            }
            arrayList.add(arrayList);
            myList.add(schList);
            myList.add(gradeList);
            myList.add(arrayList);
        }
        return myList;
    }

    public CorrectStatus getExamineeNumCheckCount(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return (CorrectStatus) this.dao2._queryBean("SELECT examNum,SUM(IF(numStatus='0',1,0)) notScan,SUM(IF(numStatus='1',1,0)) nowScan,SUM(IF(numStatus='2',1,0)) finishScan  FROM correctstatus  WHERE examNum={examNum} ", CorrectStatus.class, args);
    }

    public List getExamineeNumCheckDetail(String examNum, String numStatusType) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("numStatusType", numStatusType);
        List returnList = new ArrayList();
        List list_1 = this.dao2._queryBeanList("SELECT ep.examPaperNum,ep.subjectNum,sub.subjectName  FROM correctstatus cs  LEFT JOIN exampaper ep   ON cs.examPaperNum=ep.examPaperNum   LEFT JOIN `subject` sub   ON ep.subjectNum=sub.subjectNum   WHERE cs.examNum={examNum}   ORDER BY ep.subjectNum ASC", Exampaper.class, args);
        if (list_1 != null) {
            for (int i = 0; i < list_1.size(); i++) {
                String SubjectNum = "SubjectNum" + i;
                args.put(SubjectNum, ((Exampaper) list_1.get(i)).getSubjectNum());
                String sql2 = "SELECT cs.gradeNum,g.gradeName,cs.subjectNum,sub.subjectName,cs.examnitionRoom,er.examinationRoomNum,er.examinationRoomName  FROM correctstatus cs    LEFT JOIN grade g   ON cs.gradeNum=g.gradeNum    LEFT JOIN `subject` sub    ON cs.subjectNum=sub.subjectNum    LEFT JOIN examinationroom er    ON cs.examnitionRoom=er.id   WHERE cs.examNum={examNum}   AND cs.subjectNum={" + SubjectNum + "}   AND cs.numStatus={numStatusType}   ORDER BY er.examinationRoomNum";
                List lis = this.dao2._queryBeanList(sql2, CorrectStatus.class, args);
                if (lis != null && lis.size() > 0) {
                    returnList.add(lis);
                }
            }
        }
        return returnList;
    }

    public CorrectStatus getNumErrorCount(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String exampapernums = this.dao2._queryStr("SELECT  GROUP_CONCAT(DISTINCT gradeNum separator ',')examPaperNum FROM exam WHERE examNum={examNum}", args);
        args.put("exampapernums", exampapernums);
        return (CorrectStatus) this.dao2._queryBean("SELECT SUM(IF(errorType='0',1,0)) notScan,  SUM(IF(errorType='1',1,0)) nowScan, SUM(IF(errorType='2',1,0)) finishScan   FROM examineenumerror    WHERE examPaperNum in ({exampapernums[]}) ", CorrectStatus.class, args);
    }

    public List getNumErrorDetail(String examNum, String errorType) {
        List returnList = new ArrayList();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("errorType", errorType);
        List list_1 = this.dao2._queryBeanList("SELECT DISTINCT ene.examPaperNum,sub.subjectNum,sub.subjectName  FROM examineenumerror ene   LEFT JOIN exampaper ep   ON ene.examPaperNum= ep.examPaperNum   LEFT JOIN `subject` sub    ON ep.subjectNum=sub.subjectNum   WHERE ene.examPaperNum IN    (SELECT DISTINCT examPaperNum FROM exampaper WHERE examNum={examNum} )  ORDER BY sub.subjectNum ASC", Exampaper.class, args);
        if (list_1 != null) {
            for (int i = 0; i < list_1.size(); i++) {
                String SubjectNum = "SubjectNum" + i;
                args.put(SubjectNum, ((Exampaper) list_1.get(i)).getSubjectNum());
                List lis = this.dao2._queryBeanList("SELECT DISTINCT ene.examinationRoomNum eRoomNum,ep.subjectNum,sub.subjectName,ep.gradeNum,g.gradeName,er.examinationRoomNum,er.examinationRoomName   FROM examineenumerror ene   LEFT JOIN exampaper ep   ON ene.examPaperNum=ep.examPaperNum   LEFT JOIN grade g    ON ep.gradeNum=g.gradeNum   LEFT JOIN `subject` sub    ON ep.subjectNum=sub.subjectNum    LEFT JOIN examinationroom er    ON ene.examinationRoomNum=er.id   WHERE ene.examPaperNum=(SELECT DISTINCT examPaperNum FROM exampaper WHERE examNum={examNum}  AND subjectNum={SubjectNum} )  AND ene.errorType={errorType}   ORDER BY er.examinationRoomNum ASC", Examinationroom.class, args);
                if (lis != null && lis.size() > 0) {
                    returnList.add(lis);
                }
            }
        }
        return returnList;
    }

    public CorrectStatus getScoreExceptionCount(String exanNum) {
        Map args = StreamMap.create().put("exanNum", (Object) exanNum);
        return (CorrectStatus) this.dao2._queryBean("SELECT SUM(IF(isException='0',1,0)) notScan,SUM(IF(isException='4',1,0)) nowScan,SUM(IF(isException='2',1,0)) finishScan    FROM score   WHERE examPaperNum IN    (  SELECT DISTINCT examPaperNum   FROM exampaper   WHERE examNum={exanNum}  )", CorrectStatus.class, args);
    }

    public List getScoreExceptionDetail(String examNum, String scoreExceptionType) {
        List returnList = new ArrayList();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("SCORE_EXCEPTION_NORECOGNIZED", "0");
        args.put("scoreExceptionType", scoreExceptionType);
        List list_1 = this.dao2._queryBeanList("SELECT DISTINCT s.examPaperNum,ep.gradeNum,g.gradeName,ep.subjectNum,sub.subjectName  FROM score s   LEFT JOIN exampaper ep   ON s.examPaperNum=ep.examPaperNum   LEFT JOIN `subject` sub    ON ep.subjectNum=sub.subjectNum   LEFT JOIN grade g    ON ep.gradeNum=g.gradeNum  WHERE s.examPaperNum IN    (  SELECT DISTINCT examPaperNum    FROM exampaper    WHERE examNum={examNum}   )   AND s.isException <={SCORE_EXCEPTION_NORECOGNIZED}   ORDER BY ep.subjectNum ASC", Exampaper.class, args);
        if (list_1 != null) {
            for (int i = 0; i < list_1.size(); i++) {
                String SubjectNum = "SubjectNum" + i;
                args.put(SubjectNum, ((Exampaper) list_1.get(i)).getSubjectNum());
                List lis = this.dao2._queryBeanList("SELECT DISTINCT ep.subjectNum,sub.subjectName,ep.gradeNum,g.gradeName,er.id eRoomNum,er.examinationRoomNum,er.examinationRoomName  FROM score s   LEFT JOIN exampaper ep   ON s.examPaperNum=ep.examPaperNum   LEFT JOIN `subject` sub    ON ep.subjectNum=sub.subjectNum   LEFT JOIN grade g   ON ep.gradeNum=g.gradeNum   LEFT JOIN examinationroom er    ON s.examinationRoomNum=er.id   WHERE s.examPaperNum=   (   SELECT DISTINCT examPaperNum    FROM exampaper   WHERE examNum={examNum} AND subjectNum={SubjectNum}   )  AND s.isException={scoreExceptionType}   ORDER BY er.examinationRoomNum ASC", Examinationroom.class, args);
                if (lis != null && lis.size() > 0) {
                    returnList.add(lis);
                }
            }
        }
        return returnList;
    }

    public List getAllSchoolByExamNum(String examNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList("SELECT id num,schoolName name FROM school WHERE id={schoolNum} ", AjaxData.class, args);
    }

    public List getSjtByNum(String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryBeanList("SELECT subjectNum num,subjectName name FROM `subject` WHERE subjectNum={subjectNum} ", AjaxData.class, args);
    }

    public List getClassById(String classNum, String levelclass, String subjectNum) {
        String sql = "SELECT id num,className name FROM class WHERE id={classNum} ";
        if (null != levelclass && levelclass.equals("T") && null != subjectNum && subjectNum.length() > 2) {
            sql = "SELECT id num,className name FROM levelclass WHERE id={classNum} ";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public Map getShuangXiang(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._querySimpleMap("SELECT 100*a.num/b.numb num from   (SELECT COUNT( DISTINCT d.examPaperNum)  num FROM define d LEFT JOIN exampaper ex ON d.examPaperNum=ex.examPaperNum WHERE ex.examNum={examNum} ) a, (select count(1) numb from exampaper where examnum= {examNum} and isHidden='F')b", args);
    }

    public Map getCaiQie(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String exampapernums = this.dao2._queryStr("select GROUP_CONCAT(examPaperNum separator ',') from exampaper where examNum={examNum}", args);
        args.put("exampapernums", exampapernums);
        return this.dao2._querySimpleMap("select  100*a.num/b.numb num from  \t(select COUNT(1) num from template where examPaperNum in ({exampapernums[]}) )a ,    (select COUNT(1) numb from exampaper where examNum={examNum} and isHidden='F')b", args);
    }

    public Map getSaoMiao(String examNum) {
        String sql;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String scantype = this.dao2._queryStr("SELECT scantype FROM exam WHERE examNum={examNum} ", args);
        if (scantype.equals("1")) {
            sql = "SELECT a.num/b.num num FROM (SELECT count(DISTINCT examPaperNum) num FROM correctstatus WHERE examNum={examNum} )a, (select count(DISTINCT ex.subjectNum) num from examinationnum ex  WHERE ex.examNum={examNum} )b ";
        } else {
            sql = "SELECT a.num/b.num num FROM (SELECT count(DISTINCT examnitionRoom,exampaperNum) num FROM correctstatus WHERE examNum={examNum} )a, (select count(DISTINCT ex.examinationRoomNum) num from examinationnum ex  WHERE ex.examNum={examNum} )b ";
        }
        return this.dao2._querySimpleMap(sql, args);
    }

    public Map getHeDuiNum(String examNum) {
        String sql;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        String scantype = this.dao2._queryStr("SELECT scantype FROM exam WHERE examNum={examNum} ", args);
        if (scantype.equals("1")) {
            sql = "SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum) num FROM correctstatus WHERE examNum={examNum} )a, (SELECT count(DISTINCT exampaperNum) num FROM correctstatus WHERE examNum={examNum} AND numStatus='2')b";
        } else {
            sql = "SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE examNum={examNum} )a, (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE examNum={examNum} AND numStatus='2')b";
        }
        return this.dao2._querySimpleMap(sql, args);
    }

    public Map getBuLu(String examNum) {
        String sql;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        String scantype = this.dao2._queryStr("SELECT scantype FROM exam WHERE examNum={examNum} ", args);
        if (scantype.equals("1")) {
            sql = "SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum) num FROM correctstatus WHERE examnum={examNum} )a, (SELECT count(DISTINCT exampaperNum) num FROM correctstatus WHERE examNum={examNum}  AND appendStatus='2')b";
        } else {
            sql = "SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE examnum={examNum})a, (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE examNum={examNum} AND appendStatus='2')b";
        }
        return this.dao2._querySimpleMap(sql, args);
    }

    public Map getScore(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._querySimpleMap("SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE examnum={examNum} )a, (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE examNum={examNum} AND status='2' )b", args);
    }

    public List getquestiongroup(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String exampapernums = this.dao2._queryStr("select GROUP_CONCAT(exampaperNum separator ',') from exampaper where examNum={examNum}", args);
        args.put("exampapernums", exampapernums);
        return this.dao2._queryBeanList("select * from questiongroup where exampaperNum in ({exampapernums[]}) ", QuestionGroup.class, args);
    }

    public Map getpanfen(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._querySimpleMap("select 100*a.num/b.num num from (select count(1) num from score s LEFT JOIN exampaper ex ON s.examPaperNum=ex.examPaperNum where ex.examNum={examNum}) b, (select count(1) num from score s LEFT JOIN exampaper ex ON s.examPaperNum=ex.examPaperNum where ex.examNum={examNum} and isModify='T')a", args);
    }

    public Integer getException(String examNum, String mark1) {
        String sql;
        if (mark1.equals("1")) {
            sql = "SELECT  count(DISTINCT s.studentId,s.questionNum,s.examPaperNum ) FROM (SELECT exampaperNum,isException,studentId,questionNum FROM score )s LEFT JOIN exampaper ex ON s.examPaperNum=ex.exampaperNum LEFT JOIN illegal i ON s.studentId = i.studentId AND s.examPaperNum=i.examPaperNum  WHERE  ex.exampaperNum IS NOT NULL AND ex.examNum={examNum}  AND (s.isException='0' OR s.isException='1') AND i.type!='0'";
        } else {
            sql = "SELECT count(DISTINCT r.questionNum,r.examPaperNum )  FROM  remark r LEFT JOIN exampaper ex ON r.exampaperNum=ex.examPaperNum  AND r.`status`='F'  WHERE ex.examNum={examNum} AND ex.examPaperNum IS NOT NULL";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryInt(sql, args);
    }

    public List getExceptionExampaperNum(String examnum) {
        Map args = StreamMap.create().put("examnum", (Object) examnum);
        return this.dao2._queryBeanList("select examPaperNum from exampaper \twhere examnum={examnum} ", Exampaper.class, args);
    }

    public List getSchools() {
        return this.dao2.queryBeanList("SELECT DISTINCT schoolNum,schoolName  FROM school ORDER BY schoolNum", School.class);
    }

    public List getGrades(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList("SELECT DISTINCT gradeNum,gradeName,schoolNum\tFROM grade\t\tWHERE  schoolNum={schoolNum}  ORDER BY schoolNum,gradeNum", Grade.class, args);
    }

    public List getsubjects(String examNum, String gradeNum) {
        this.log.info("批量导出---获取科目   ----getsubjects---sql:SELECT DISTINCT examNum,gradeNum,subjectNum\t\tFROM exampaper\t\tWHERE  examNum={examNum}   AND gradeNum={gradeNum}  ORDER BY gradeNum,subjectNum");
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryBeanList("SELECT DISTINCT examNum,gradeNum,subjectNum\t\tFROM exampaper\t\tWHERE  examNum={examNum}   AND gradeNum={gradeNum}  ORDER BY gradeNum,subjectNum", Exampaper.class, args);
    }

    public List getClasses(String schoolNum, String examNum, String gradeNum, String subjectNum, String examPaperNum) {
        if (examPaperNum == null || "".equals(examPaperNum)) {
            examPaperNum = this.examDao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        return this.dao2._queryBeanList("SELECT DISTINCT classNum,schoolNum\t\tFROM classexam \tWHERE examPaperNum={examPaperNum}  AND schoolNum={schoolNum}\tORDER BY classNum*1 ", Class.class, args);
    }

    public void addMarkError(String scoreId, Integer exampaperNum, String questionNum, Double questionScore, String type, String insertUser, String insertDate) {
        MarkError m = new MarkError();
        m.setScoreId(scoreId);
        m.setExampaperNum(exampaperNum);
        m.setQuestionNum(questionNum);
        m.setQuestionScore(questionScore);
        m.setType(type);
        m.setInsertUser(insertUser);
        m.setInsertDate(insertDate);
        this.dao2.save(m);
    }

    public List getMarkError(String exampaperNum, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum, String examinationRoomNum, String scoreId) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("questionNum", (Object) questionNum).put("scoreId", (Object) scoreId);
        return this.dao2._queryBeanList("select * from markerror  where exampaperNum={exampaperNum} and questionNum={questionNum}  and scoreId={scoreId} ", MarkError.class, args);
    }

    public Map getpageNum(String examPaperNum, String questionNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum);
        return this.dao2._querySimpleMap("select page from questionImage where examPaperNum={examPaperNum} and questionNum={questionNum} ", args);
    }

    public List getLeakFromScore_modified(String examNum, String subjectNum, String gradeNum, String testCenter, String roomNum) {
        Map args = new HashMap();
        args.put("roomNum", roomNum);
        args.put("testCenter", testCenter);
        args.put("EXCEPTION_TYPE_MISSING", "0");
        args.put("FALSE", "F");
        args.put("MODIFIED_SCORE_EXCEPTION_NOTTOSCORE", "10");
        args.put("MODIFIED_SCORE_EXCEPTION_UPFULLMARKS_update", "11");
        String examPaperNum = this.examDao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        args.put("examPaperNum", examPaperNum);
        String illegalSql = "WHERE examPaperNum={examPaperNum}   ";
        if (null != roomNum && !"-1".equals(roomNum)) {
            illegalSql = illegalSql + " AND examinationRoomNum={roomNum}   ";
        }
        if (null != testCenter && !"-1".equals(testCenter)) {
            illegalSql = illegalSql + " AND testingCentreId={testCenter}   ";
        }
        String illegalSql2 = illegalSql + "     AND studentId NOT IN (\tSELECT DISTINCT studentId \tFROM illegal  \tWHERE  examPaperNum={examPaperNum}  ";
        if (null != roomNum && !"-1".equals(roomNum)) {
            illegalSql2 = illegalSql2 + "\tAND examinationRoomNum={roomNum}   ";
        }
        if (null != testCenter && !"-1".equals(testCenter)) {
            illegalSql2 = illegalSql2 + "\tAND testingCentreId={testCenter}   ";
        }
        String illegalSql3 = illegalSql2 + "\tAND  type={EXCEPTION_TYPE_MISSING}   )   ";
        String sql = "select DISTINCT sc.questionNum,sc.examPaperNum,def.questionNum as qNum from (SELECT   * FROM score sc   " + illegalSql3 + "AND isException IS NOT null        AND continued= {FALSE}   AND  (isException={MODIFIED_SCORE_EXCEPTION_NOTTOSCORE} OR isException={MODIFIED_SCORE_EXCEPTION_UPFULLMARKS_update} OR isException='15'))sc   LEFT JOIN (SELECT id,questionNum FROM define   UNION SELECT id,questionNum FROM subdefine )def  ON sc.questionNum=def.id ORDER BY qNum *1, REPLACE ( SUBSTRING( qNum, LOCATE('_', qNum) + 1, LENGTH(qNum) ), '_', '' ) ASC";
        List questionNumList = this.dao2._queryBeanList(sql, Score.class, args);
        List<Score> scoreList = new ArrayList<>();
        if (null != questionNumList) {
            for (int i = 0; i < questionNumList.size(); i++) {
                String QuestionNum = "QuestionNum" + i;
                args.put(QuestionNum, ((Score) questionNumList.get(i)).getQuestionNum());
                String sql2 = "SELECT sc.*,def.questionNum  realQuestionNum   FROM (SELECT   '" + ((Score) questionNumList.get(i)).getQuestionNum() + "'  questionNum,examPaperNum,sum(IF(isException='10',1,0))  noscore,sum(IF(isException='11',1,0)) upfullscore,sum(IF(isException='12',1,0))  norecognized , sum(IF(isException='15',1,0))  ext1  FROM score   " + illegalSql3 + "  AND questionNum={" + QuestionNum + "}    AND continued= {FALSE}   AND  (isException={MODIFIED_SCORE_EXCEPTION_NOTTOSCORE} OR isException={MODIFIED_SCORE_EXCEPTION_UPFULLMARKS_update} OR isException='15'))sc  LEFT JOIN (SELECT id,questionNum,examPaperNum,category FROM define WHERE id={" + QuestionNum + "}  UNION SELECT id,questionNum,examPaperNum,category FROM subdefine WHERE id={" + QuestionNum + "} )def  ON sc.questionNum=def.id";
                Score score = (Score) this.dao2._queryBean(sql2, Score.class, args);
                if (null != score && score.getUpfullscore() + score.getNoscore() + Integer.valueOf(score.getExt1()).intValue() != 0) {
                    scoreList.add(score);
                }
            }
        }
        return scoreList;
    }

    public List getsentence(String examNum, String subjectNum, String gradeNum, String schoolNum, String roomNum) {
        String examPaperNum = this.examDao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        Map args = new HashMap();
        args.put("FALSE", "F");
        args.put("examPaperNum", examPaperNum);
        String sql = "select DISTINCT questionNum from remark      WHERE `status`={FALSE}  AND  exampaperNum={examPaperNum}    ";
        List questionNumList = this.dao2._queryBeanList(sql, Remark.class, args);
        List<Remark> remarkList = new ArrayList<>();
        if (questionNumList != null) {
            for (int i = 0; i < questionNumList.size(); i++) {
                String QuestionNum = "QuestionNum" + i;
                args.put(QuestionNum, ((Remark) questionNumList.get(i)).getQuestionNum());
                String sql2 = "SELECT rk.*,def.questionNum id   FROM(SELECT '" + ((Remark) questionNumList.get(i)).getQuestionNum() + "'  questionNum,exampaperNum,sum(IF(type='2',1,0))  chongpan, sum(IF(type='1',1,0)) caijue  FROM remark     WHERE `status`={FALSE}  AND  exampaperNum={examPaperNum}    AND questionNum={" + QuestionNum + "}  ) rk   LEFT JOIN (SELECT id,questionNum,examPaperNum  FROM define  WHERE examPaperNum={examPaperNum} AND isParent='0'  UNION ALL  SELECT subdef.id,subdef.questionNum,subdef.examPaperNum  FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid  WHERE def.examPaperNum={examPaperNum} AND def.isParent='1'  )def ON rk.questionNum=def.id  WHERE def.id IS NOT NULL  ";
                Remark remark = (Remark) this.dao2._queryBean(sql2, Remark.class, args);
                if (remark != null && remark.getChongpan() + remark.getCaijue() != 0) {
                    remarkList.add(remark);
                }
            }
        }
        return remarkList;
    }

    public List sentenceModified(String examNum, String subjectNum, String gradeNum, String schoolNum, String roomNum) {
        Map args = new HashMap();
        args.put("TRUE", "T");
        String examPaperNum = this.examDao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        args.put("examPaperNum", examPaperNum);
        String sql = "select DISTINCT questionNum from remark     WHERE `status`={TRUE}  AND  exampaperNum={examPaperNum}    ";
        List questionNumList = this.dao2._queryBeanList(sql, Remark.class, args);
        List<Remark> remarkList = new ArrayList<>();
        if (questionNumList != null) {
            for (int i = 0; i < questionNumList.size(); i++) {
                String QuestionNum = "QuestionNum" + i;
                args.put(QuestionNum, ((Remark) questionNumList.get(i)).getQuestionNum());
                String sql2 = "SELECT rk.*,def.questionNum id   FROM(SELECT '" + ((Remark) questionNumList.get(i)).getQuestionNum() + "'  questionNum,exampaperNum,sum(IF(type='2',1,0))  chongpan, sum(IF(type='1',1,0)) caijue  FROM remark    WHERE `status`={TRUE}  AND  exampaperNum={examPaperNum}    AND questionNum={" + QuestionNum + "}  ) rk   LEFT JOIN (SELECT id,questionNum,examPaperNum  FROM define   WHERE examPaperNum={examPaperNum} AND isParent='0'  UNION ALL  SELECT subdef.id,subdef.questionNum,subdef.examPaperNum  FROM define def  LEFT JOIN subdefine subdef ON def.id=subdef.pid   WHERE def.examPaperNum={examPaperNum} AND def.isParent='1'   )def ON rk.questionNum=def.id  WHERE def.id IS NOT NULL  ";
                Remark remark = (Remark) this.dao2._queryBean(sql2, Remark.class, args);
                if (remark != null && remark.getChongpan() + remark.getCaijue() != 0) {
                    remarkList.add(remark);
                }
            }
        }
        return remarkList;
    }

    public List getExcelFileNameByNum(String examNum, String gradeNum, String subjectNum) {
        Object obj;
        List returnList = new ArrayList();
        String excelName = "";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        if (this.dao2._queryBean("select examName from exam where examNum={examNum} ", Exam.class, args) != null) {
            excelName = excelName + ((Exam) this.dao2._queryBean("select examName from exam where examNum={examNum} ", Exam.class, args)).getExamName() + "_";
        }
        if (this.dao2._queryBean("select DISTINCT gradeName    from grade where gradeNum={gradeNum} ", Grade.class, args) != null) {
            excelName = excelName + ((Grade) this.dao2._queryBean("select DISTINCT gradeName    from grade where gradeNum={gradeNum} ", Grade.class, args)).getGradeName() + "_";
        }
        if (subjectNum != null && (obj = this.dao2._queryBean("select subjectName FROM `subject` WHERE subjectNum={subjectNum} ", Subject.class, args)) != null) {
            String str = excelName + ((Subject) obj).getSubjectName() + "_";
        }
        return returnList;
    }

    public List parentExamPaperNum(String examNum, String subjectNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        List li = this.dao2._queryBeanList("SELECT DISTINCT subjectNum,gradeNum,examPaperNum,pexampaperNum   FROM exampaper\t\tWHERE  subjectNum={subjectNum}\t   AND gradeNum={gradeNum}   AND  examNum={examNum}\t ", Exampaper.class, args);
        if (li != null && li.size() > 0) {
            return li;
        }
        return null;
    }

    public Integer updateSentenceScore(String scoreId, String questionScore) {
        List<String> sqls = new ArrayList<>();
        Map args = StreamMap.create().put("questionScore", (Object) questionScore).put("scoreId", (Object) scoreId);
        sqls.add("UPDATE score SET questionScore={questionScore}   WHERE id={scoreId} ");
        this.dao2._batchExecute(sqls, args);
        return 1;
    }

    public String authorExampaperIlleagal(String examNum, String subjectNum, String gradeNum, String studentId, String schoolNum) {
        String examPaperNum = this.examDao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        String sql = "SELECT type FROM illegal WHERE examPaperNum='" + examPaperNum + "'   AND studentId={studentId} AND schoolNum={schoolNum} ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryStr(sql, args);
    }

    public void addToCorrectstatus(String examNum, String subjectNum, String gradeNum, String examPaperNum, String testCenter, String roomId) {
        if (null == examPaperNum || "".equals(examPaperNum)) {
            if (null == subjectNum || "".equals(subjectNum)) {
                Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
                List li_examPaper = this.dao2._queryBeanList("SELECT examPaperNum,examNum,subjectNum,gradeNum FROM exampaper WHERE examNum={examNum} AND gradeNum={gradeNum} ", Exampaper.class, args);
                if (li_examPaper != null && li_examPaper.size() > 0) {
                    for (int i = 0; i < li_examPaper.size(); i++) {
                        excuteAddToCorrsta(examNum, ((Exampaper) li_examPaper.get(i)).getExamPaperNum().toString(), testCenter, roomId);
                    }
                    return;
                }
                return;
            }
            excuteAddToCorrsta(examNum, this.examDao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum), testCenter, roomId);
            return;
        }
        excuteAddToCorrsta(examNum, examPaperNum, testCenter, roomId);
    }

    public void excuteAddToCorrsta(String examNum, String examPaperNum, String testCenter, String roomId) {
        String sql;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("testCenter", testCenter);
        args.put("roomId", roomId);
        String sql_auth = "SELECT /* shard_host_HG=Write */ DISTINCT examPaperNum,examNum,subjectNum,gradeNum,testingCentreId    FROM correctstatus WHERE  examPaperNum={examPaperNum}   AND examNum={examNum} AND testingCentreId={testCenter}  ";
        if (null != roomId && !"-1".equals(roomId) && !"".equals(roomId)) {
            sql_auth = sql_auth + "AND examnitionRoom={roomId}   ";
        }
        List li_auth = this.dao2._queryBeanList(sql_auth, CorrectStatus.class, args);
        if (null == li_auth || li_auth.size() == 0) {
            String sql2 = "INSERT INTO correctstatus( examPaperNum,examNum,subjectNum,gradeNum,testingCentreId,examnitionRoom,classNum,`status`,numStatus,insertUser,insertdate ) SELECT examPaperNum,examNum,subjectNum,gradeNum,'" + testCenter + "',";
            if (null != roomId && !"-1".equals(roomId) && !"".equals(roomId)) {
                sql = sql2 + roomId;
            } else {
                sql = sql2 + "-1";
            }
            this.dao2._execute(sql + ",-1,'0','0','0',NOW() FROM exampaper WHERE examNum={examNum}    and exampaperNum={examPaperNum} ", args);
        }
    }

    public void areaAllComplete(String user, int examNum, String gradeNum) throws Exception {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum)).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("user", (Object) user);
        this.dao2._execute("call sub_statisticItem({examNum},{gradeNum},{user})", args);
    }

    public void fufenComplete(String user, int examNum, String gradeNum) {
        long aaaa = new Date().getTime();
        this.log.info("【计算赋分】##################   call sub_fufen(?,?,?)  user:" + user + " examNum: " + examNum + "  gradeNum: " + gradeNum + "  date: " + DateUtil.getCurrentTime() + " 计算时长：" + (new Date().getTime() - aaaa));
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum)).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("user", (Object) user);
        this.dao2._execute("call sub_fufen({examNum} ,{gradeNum},{user} )", args);
    }

    public Map getbulu(String examNum) {
        return null;
    }

    public int getExamMsgByChecked(String schoolNum, String gradeNum, String subjectNum, String classStr, int classType, String stuType) {
        String sqlString;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("classStr", (Object) classStr).put("stuType", (Object) stuType).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        if (classType == 1) {
            sqlString = "select COUNT(1) from student  where schoolNum ={schoolNum} and gradeNum = {gradeNum} and isDelete = 'F' and classNum in ({classStr[]}) AND type in ({stuType[]}) ";
        } else {
            sqlString = "SELECT COUNT(1) FROM levelstudent lstu LEFT JOIN student stu on lstu.sid = stu.id  where lstu.schoolNum ={schoolNum} and lstu.gradeNum ={gradeNum} and lstu.subjectNum = {subjectNum} and lstu.classNum in ({classStr[]})  AND stu.type in ({stuType[]}) ";
        }
        int count = this.dao2._queryInt(sqlString, args).intValue();
        return count;
    }

    public List<Object[]> getClassStuAndSubOneFile(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String isJointStuType, String fufen, String subCompose) throws Exception {
        List<Object[]> list = new ArrayList<>();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ getTotalScoreOneFile2(?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, schoolNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, classNum);
                pstat.setString(5, studentType);
                pstat.setString(6, graduationType);
                pstat.setString(7, stuSourceType);
                pstat.setString(8, viewRankInfo);
                pstat.setString(9, isJointStuType);
                pstat.setString(10, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Object[] obj = new Object[28];
                    for (int i = 0; i < obj.length; i++) {
                        obj[i] = rs.getObject(i + 1);
                    }
                    list.add(obj);
                }
                DbUtils.close(rs, pstat, conn);
            } catch (Exception e) {
                this.log.info("--总科目成绩导出单个文件存储过程【getClassStuAndSubOneFile】异常--" + e);
                DbUtils.close(rs, pstat, conn);
            }
            return list;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public List<Object[]> getClassleicengFile(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String isJointStuType, String fufen, String subCompose, String topItemId) throws Exception {
        StreamMap streamMap = StreamMap.create();
        streamMap.put(Const.EXPORTREPORT_examNum, (Object) examNum);
        streamMap.put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        streamMap.put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        streamMap.put(Const.EXPORTREPORT_classNum, (Object) classNum);
        streamMap.put(Const.EXPORTREPORT_studentType, (Object) studentType);
        streamMap.put("graduationType", (Object) graduationType);
        streamMap.put("stuSourceType", (Object) stuSourceType);
        streamMap.put(Const.viewRankInfo, (Object) viewRankInfo);
        streamMap.put(Const.isJointStuType, (Object) isJointStuType);
        streamMap.put("subCompose", (Object) subCompose);
        streamMap.put("topItemId", (Object) topItemId);
        List<Object[]> objects = this.dao2._queryArrayList("call getTotalScoreLeiceng({examNum},{schoolNum},{gradeNum},{classNum},{studentType},{graduationType},{stuSourceType},{viewRankInfo},{isJointStuType},{subCompose},{topItemId})", streamMap);
        return objects;
    }

    public List<Map<String, Object>> getQuestionGroupInfoList(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return this.dao2._queryMapList("SELECT qg.groupNum,qg.exampaperNum,qs.makType,qs.judgetype,qq.hebingshu,qg.scancompleted,if(LENGTH(d.choosename) > 1,1,0) xuazuoti,if(LENGTH(d.choosename) > 1, d.choosename,qg.groupNum) choosename,if(LENGTH(d.choosename) > 1, d.questionNum ,d.subquestionNum) questionName,d.orderNum,d.suborderNum,if(LENGTH(d.choosename) > 1, CONCAT(choosename,d.suborderNum),qg.groupNum) xuazuotifenzu, IFNULL(e.xuankaoqufen,'1') xuankaoqufen FROM (SELECT groupNum,exampaperNum,scancompleted FROM questiongroup WHERE exampaperNum = {examPaperNum}) qg LEFT JOIN (SELECT groupNum, makType,judgetype FROM questiongroup_mark_setting WHERE exampaperNum = {examPaperNum}) qs ON qg.groupNum = qs.groupNum LEFT JOIN (SELECT groupNum,count(1) hebingshu FROM questiongroup_question WHERE exampaperNum = {examPaperNum} GROUP BY groupNum) qq ON qg.groupNum = qq.groupNum LEFT JOIN (SELECT questionNum questionNum, questionNum subquestionNum,id,choosename,isParent,`merge`,orderNum orderNum,0 suborderNum,category  FROM define WHERE exampaperNum = {examPaperNum} UNION SELECT d.questionNum questionNum ,sb.questionNum subquestionNum,sb.id,d.choosename,'2' isParent,'2' merge,d.orderNum orderNum,sb.orderNum suborderNum,sb.category FROM define d LEFT JOIN subdefine sb ON sb.pid = d.id WHERE d.exampaperNum = {examPaperNum} ) d ON qs.groupNum = d.id INNER JOIN exampaper e on d.category=e.examPaperNum", TypeEnum.StringObject, args);
    }

    public List<Map<String, Object>> getTeacherWorkloadInfoList(String groupNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        return this.dao2._queryMapList("select r.schoolnum,r.userNum,r.groupNum,r.exampaperNum,r.userType,r.assister,r.schoolGroupNum,r.schoolGroupName,r.schoolName,r.teacherNum,r.teacherName,r.zaiyue,r.num,r.groupcount,sum(r.yipanshu) yipanCount,sum(r.questionScore)/sum(r.yipanshu) pingjunfen from ( select qu.userId,qu.schoolnum,qu.userNum,qu.groupNum,qu.exampaperNum,qu.userType,qu.assister,qu.schoolGroupNum,qu.schoolGroupName,qu.schoolName,qu.teacherNum,qu.teacherName,IF(MAX(tu.updateTime) IS NULL || IFNULL(TIMESTAMPDIFF(MINUTE,MAX(tu.updateTime),NOW()),4) > 2,'否','是') zaiyue,q.num,y.groupcount,IFNULL(SUM(IF(tu.STATUS = 'T', 1, 0))/y.groupcount,0 )  yipanshu ,sum(tu.questionScore) questionScore from  ( SELECT u.userId userId,u.schoolnum schoolnum ,qu.userNum, qu.groupNum,qu.exampaperNum,qu.userType,slg.schoolGroupNum,slg.schoolGroupName,IF (aj.assister IS NULL, 0, 1) assister,ifnull(sc.schoolName, '-') schoolName,te.teacherNum,te.teacherName FROM questiongroup_user qu LEFT JOIN assistyuejuan aj ON qu.groupNum = aj.groupNum AND qu.userNum = aj.assister LEFT JOIN `user` u ON qu.userNum = u.id and u.userType = 1 LEFT JOIN schoolgroup slg ON u.schoolNum = slg.schoolNum LEFT JOIN teacher te ON u.userId = te.id LEFT JOIN school sc ON u.schoolnum = sc.id WHERE qu.groupNum IN ({groupNum[]}) and qu.userType<>'2' and u.id is not null ) qu LEFT JOIN ( SELECT q.num, q.groupNum, q.insertUser FROM quota q WHERE q.groupNum IN ({groupNum[]}) ) q ON qu.groupNum = q.groupNum  AND qu.userNum = q.insertUser LEFT JOIN ( SELECT groupNum, count(DISTINCT(questionNum)) AS groupcount FROM questiongroup_question WHERE groupNum IN ({groupNum[]}) GROUP BY groupNum ) y ON qu.groupNum = y.groupNum LEFT JOIN task tu ON qu.groupNum = tu.groupNum AND qu.userNum = tu.insertUser and tu.STATUS = 'T' GROUP BY qu.groupNum,qu.userNum )r GROUP BY r.userNum", TypeEnum.StringObject, args);
    }

    public int getYipanCount(String examPaperNum, String groupNum, int hebingshu) {
        Map args = new HashMap();
        args.put("hebingshu", Integer.valueOf(hebingshu));
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        Object yipanCount = this.dao2._queryObject("SELECT FLOOR(count(1)/{hebingshu}) FROM task WHERE examPaperNum={examPaperNum} and groupNum={groupNum} AND `status`='T' ", args);
        return Convert.toInt(yipanCount, 0).intValue();
    }

    public List<Map<String, Object>> getYipanCount_fenzu(String examPaperNum, String groupNum, int hebingshu) {
        Map args = new HashMap();
        args.put("hebingshu", Integer.valueOf(hebingshu));
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        return this.dao2._queryMapList("select g.schoolGroupNum,FLOOR(count(1)/{hebingshu}) yipanCount from (SELECT studentid FROM task WHERE examPaperNum={examPaperNum} and groupNum={groupNum} AND `status`='T') r LEFT JOIN student s on r.studentid=s.id LEFT JOIN schoolgroup g on g.schoolNum=s.schoolNum GROUP BY g.schoolGroupNum", TypeEnum.StringObject, args);
    }

    public int getSanpingCount(String examPaperNum, String groupNum, int hebingshu) {
        Map args = new HashMap();
        args.put("hebingshu", Integer.valueOf(hebingshu));
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        Object sanpingCount = this.dao2._queryObject("SELECT FLOOR(count(1)/{hebingshu}) FROM task WHERE examPaperNum={examPaperNum} and groupNum={groupNum} AND userNum='3' ", args);
        return Convert.toInt(sanpingCount, 0).intValue();
    }

    public List<Map<String, Object>> getSanpingCount_fenzu(String examPaperNum, String groupNum, int hebingshu) {
        Map args = new HashMap();
        args.put("hebingshu", Integer.valueOf(hebingshu));
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        return this.dao2._queryMapList("select g.schoolGroupNum,FLOOR(count(1)/{hebingshu}) sanpingCount from (SELECT studentid FROM task WHERE examPaperNum={examPaperNum} and groupNum={groupNum} AND userNum='3') r LEFT JOIN student s on r.studentid=s.id LEFT JOIN schoolgroup g on g.schoolNum=s.schoolNum GROUP BY g.schoolGroupNum", TypeEnum.StringObject, args);
    }

    public List<Map<String, Object>> getWeicaijueList(String examPaperNum, String groupNum, int hebingshu) {
        Map args = new HashMap();
        args.put("hebingshu", Integer.valueOf(hebingshu));
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        return this.dao2._queryMapList("SELECT `status` FROM remark WHERE examPaperNum={examPaperNum} and groupNum={groupNum} AND type='1' ", TypeEnum.StringObject, args);
    }

    public int getChongpanCount(String examPaperNum, String groupNum, int hebingshu) {
        Map args = new HashMap();
        args.put("hebingshu", Integer.valueOf(hebingshu));
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        Object chongpanCount = this.dao2._queryObject("SELECT FLOOR(count(1)/{hebingshu}) FROM remark WHERE examPaperNum={examPaperNum} and groupNum={groupNum} AND type='2' and `status`='F' ", args);
        return Convert.toInt(chongpanCount, 0).intValue();
    }

    public int getWeicaiqieCount(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        Object weicaiqieCount = this.dao2._queryObject("select count(1) from cantrecognized where examPaperNum={examPaperNum}", args);
        return Convert.toInt(weicaiqieCount, 0).intValue();
    }

    public int getWeikaifangTizuCount(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        Object weikaifangTizuCount = this.dao2._queryObject("select count(1) from questiongroup where exampaperNum={examPaperNum} and stat=0", args);
        return Convert.toInt(weikaifangTizuCount, 0).intValue();
    }

    public int getWeikaifangKaodianCount(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        Object weikaifangKaodianCount = this.dao2._queryObject("select count(1) from testingcentredis where examPaperNum={examPaperNum} and isDis=0", args);
        return Convert.toInt(weikaifangKaodianCount, 0).intValue();
    }

    public int getKaodianCount(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        Object weikaifangKaodianCount = this.dao2._queryObject("select count(1) from testingcentredis where examPaperNum={examPaperNum} ", args);
        return Convert.toInt(weikaifangKaodianCount, 0).intValue();
    }
}
