package com.dmj.daoimpl.questionGroup;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.dmj.auth.bean.License;
import com.dmj.cs.util.SingleDoubleMarkingCache;
import com.dmj.daoimpl.awardPoint.AwardPointDaoImpl;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.AjaxData;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.Define;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Grade;
import com.dmj.domain.PersonWorkRecord;
import com.dmj.domain.Role;
import com.dmj.domain.School;
import com.dmj.domain.Schoolgroup;
import com.dmj.domain.Subject;
import com.dmj.domain.Task;
import com.dmj.domain.Teacher;
import com.dmj.domain.User;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.domain.vo.QuestionGroup_question;
import com.dmj.domain.vo.QuestionGroup_user;
import com.dmj.domain.vo.Questiongroup_mark_setting;
import com.dmj.domain.vo.TestingcentreDis;
import com.dmj.service.examManagement.ExamService;
import com.dmj.service.examManagement.UtilSystemService;
import com.dmj.serviceimpl.examManagement.ExamServiceImpl;
import com.dmj.serviceimpl.examManagement.UtilSystemServiceimpl;
import com.dmj.util.CommonUtil;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.config.Configuration;
import com.zht.db.ServiceFactory;
import com.zht.db.TypeEnum;
import common.Logger;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

/* loaded from: QuestionGroupDaoImpl.class */
public class QuestionGroupDaoImpl {
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());
    org.apache.log4j.Logger log4J = org.apache.log4j.Logger.getLogger(getClass());
    private UtilSystemService uss = (UtilSystemService) ServiceFactory.getObject(new UtilSystemServiceimpl());
    public static ExamService examService = (ExamService) ServiceFactory.getObject(new ExamServiceImpl());
    public HttpServletRequest request;

    /* JADX WARN: Multi-variable type inference failed */
    public List<AjaxData> getExam(String userId, String oneOrMore) {
        new ArrayList();
        Map args = new HashMap();
        args.put("STATUS_EXAM_COMPLETE", "9");
        args.put("exam_type_online", "0");
        args.put("isHidden", "F");
        args.put("userNum", userId);
        List _queryBeanList = this.dao._queryBeanList("SELECT DISTINCT em.examNum num,em.examName name from exampaper e  LEFT JOIN exam em on e.examNum=em.examNum  where e.type={exam_type_online}  AND e.isHidden={isHidden}  and em.isDelete='F' and  em.status!={STATUS_EXAM_COMPLETE}  ORDER BY em.insertDate DESC", AjaxData.class, args);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args);
        if (userId != null && "" != userId && !userId.equals("-1") && !userId.equals("-2") && null == map) {
            if ("1".equals(oneOrMore)) {
                if (null == map) {
                    _queryBeanList = this.dao._queryBeanList("SELECT DISTINCT em.examNum num,em.examName name from exampaper e  LEFT JOIN exam em on e.examNum=em.examNum  LEFT JOIN examschool es ON em.examNum=es.examNum  right JOIN (select schoolNum from schoolscanpermission where userNum={userNum} union select  schoolNum from user where id={userNum}) scm ON CAST(es.schoolNum as char)=CAST(scm.schoolNum  as char)   where e.type={exam_type_online}  AND e.isHidden={isHidden}  and em.isDelete='F' and  em.status!={STATUS_EXAM_COMPLETE}  ORDER BY em.insertDate DESC", AjaxData.class, args);
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

    public List<AjaxData> getGrade(String exam) {
        String sql;
        if ("".equals(exam)) {
            sql = "SELECT distinct g.gradeNum num ,g.gradeName name from  grade g  ";
        } else {
            sql = "SELECT distinct e.gradeNum num ,g.gradeName name from exampaper e LEFT JOIN `grade` g on e.gradeNum = g.gradeNum and e.jie=g.jie WHERE e.examNum ={exam}  and e.type={exam_type_online} AND e.isHidden={FALSE} ORDER BY g.gradeNum asc ";
        }
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("exam_type_online", "0");
        args.put("FALSE", "F");
        return this.dao._queryBeanList(sql, AjaxData.class, args);
    }

    public String getSubjectCount(String exam, String grade) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("isHidden", "F");
        args.put("exam_type_online", "0");
        return this.dao._queryStr("SELECT cast(count(1) as char(20)) from exampaper e LEFT JOIN subject s on e.subjectNum = s.subjectNum  WHERE e.examNum ={exam} and e.gradeNum ={grade}  AND e.isHidden={isHidden}  and e.type={exam_type_online} ", args);
    }

    public List<AjaxData> getSubject(String exam, String grade) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("isHidden", "F");
        args.put("exam_type_online", "0");
        return this.dao._queryBeanList("SELECT DISTINCT s.subjectName name,e.subjectNum num from exampaper e LEFT JOIN subject s on e.subjectNum = s.subjectNum WHERE e.examNum ={exam}  and e.gradeNum ={grade}  AND e.isHidden={isHidden}  and e.type={exam_type_online} ", AjaxData.class, args);
    }

    public List<AjaxData> getSubject1(String exam, String grade) {
        String gradeStr = "";
        if (grade != null && !grade.equals("") && !grade.equals("-1")) {
            gradeStr = " and e.gradeNum = {grade} ";
        }
        String sql = "SELECT DISTINCT s.subjectName name,e.subjectNum num from exampaper e LEFT JOIN subject s on e.subjectNum = s.subjectNum WHERE e.exampaperNum not in (select distinct pexampaperNum from exampaper where exampaperNum != pexampaperNum) and e.examNum = {exam} " + gradeStr + " and e.type={exam_type_online} order by s.orderNum";
        Map args = new HashMap();
        args.put("grade", grade);
        args.put("exam", exam);
        args.put("exam_type_online", "0");
        return this.dao._queryBeanList(sql, AjaxData.class, args);
    }

    public Exampaper getexamPaperNum(String exam, String grade, String subject, String jie) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("subject", subject);
        args.put("grade", grade);
        return (Exampaper) this.dao._queryBean("select examPaperNum,pexamPaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject} ", Exampaper.class, args);
    }

    public String getgroupDownCount(String examPaperNum, String groupNum, String type) {
        String sql;
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = new HashMap();
        args.put("newGroupNum", newGroupNum2);
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNumArr[0]);
        if (type.equals("0")) {
            sql = "select count(1) from questiongroup_question qq where qq.exampaperNum={examPaperNum} and groupNum={groupNum} ";
        } else {
            sql = "select count(DISTINCT qq.userNum) from questiongroup_user qq where qq.exampaperNum={examPaperNum}  and groupNum in ({newGroupNum[]}) ";
        }
        String count = this.dao._queryStr(sql, args);
        return count;
    }

    public void createGroup(QuestionGroup qg, List<String> add, QuestionGroup_question qgq) {
        Map args = new HashMap();
        args.put("groupNum", qgq.getGroupNum());
        args.put("examPaperNum", qgq.getExampaperNum());
        if (!"".equals(qg) && null != qg) {
            qg.setTotalnum("0");
            this.dao.save(qg);
            createGroupfz_mark_setting(qg.getExampaperNum(), qg.getGroupNum(), qg.getInsertUser());
            if (add != null && add.size() != 0) {
                for (String questionNum : add) {
                    QuestionGroup_question qgq2 = new QuestionGroup_question();
                    qgq2.setId(GUID.getGUIDStr());
                    qgq2.setInsertDate(DateUtil.getCurrentDay());
                    qgq2.setInsertUser(qgq.getInsertUser());
                    qgq2.setQuestionNum(questionNum);
                    qgq2.setGroupNum(qgq.getGroupNum());
                    qgq2.setExampaperNum(qgq.getExampaperNum());
                    this.dao.save(qgq2);
                    deleteGroupfz(qgq2.getExampaperNum(), qgq2.getQuestionNum(), "", "1", "");
                    args.put("questionNum", questionNum);
                    this.dao._execute("update task set groupNum={groupNum} where examPaperNum={examPaperNum} and groupNum={questionNum}  ", args);
                    String markval = this.dao._queryStr("select /* shard_host_HG=Write */ makType from questiongroup_mark_setting  where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
                    if (markval.equals("0")) {
                        this.dao._execute(" delete from task where examPaperNum={examPaperNum}   and groupNum={groupNum} and usernum!='1' ", args);
                        this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and groupNum={groupNum} ' and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
                        this.dao._execute("delete from remark where examPaperNum={examPaperNum}  and questionNum={questionNum}  ", args);
                    } else if (markval.equals("1")) {
                        this.dao._execute(" delete from task where examPaperNum={examPaperNum}  and groupNum={groupNum}  and usernum!='1' ", args);
                        this.dao._execute(" INSERT INTO task(id,scoreId,examPaperNum,questionNum,insertDate,updateTime,isException,isDelete ,status ,groupNum,rownum,insertuser,testingCentreId,userNum)SELECT r.*,'2' FROM  \t(  SELECT  UUID_SHORT() id,scoreId,examPaperNum,questionNum,now() insertDate,now()  updateTime,'F' isException, isDelete ,'F' status,groupNum,0 rownum,-1 insertuser,testingCentreId  from  task  where examPaperNum={examPaperNum}   and groupNum={groupNum} ' and usernum='1')r  ", args);
                    }
                    args.put("counttask2", this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args));
                    this.dao._execute(" update questiongroup set totalnum={counttask2}  where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
                    args.remove("questionNum");
                    args.remove("counttask2");
                }
                return;
            }
            return;
        }
        if (add != null && add.size() != 0) {
            for (String questionNum2 : add) {
                QuestionGroup_question qgq22 = new QuestionGroup_question();
                qgq22.setId(GUID.getGUIDStr());
                qgq22.setInsertDate(DateUtil.getCurrentDay());
                qgq22.setInsertUser(qgq.getInsertUser());
                qgq22.setQuestionNum(questionNum2);
                qgq22.setGroupNum(qgq.getGroupNum());
                qgq22.setExampaperNum(qgq.getExampaperNum());
                this.dao.save(qgq22);
                deleteGroupfz(qgq22.getExampaperNum(), qgq22.getQuestionNum(), "", "1", "");
                args.put("questionNum", questionNum2);
                this.dao._execute("update task set groupNum={groupNum}  where examPaperNum={examPaperNum} and groupNum={groupNum} ", args);
                String markval2 = this.dao._queryStr("select /* shard_host_HG=Write */ makType from questiongroup_mark_setting  where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
                if (markval2.equals("0")) {
                    this.dao._execute(" delete from task where examPaperNum={examPaperNum}   and groupNum={groupNum}  and usernum!='1' ", args);
                    this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and groupNum={groupNum}   and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
                    this.dao._execute("delete from remark where examPaperNum={examPaperNum}  and questionNum={questionNum} ", args);
                } else if (markval2.equals("1")) {
                    this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and groupNum={groupNum}  and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
                    this.dao._execute("delete from remark where examPaperNum={examPaperNum}  and questionNum={questionNum} ", args);
                    this.dao._execute(" delete from task where examPaperNum={examPaperNum}   and groupNum={groupNum}  and usernum!='1' ", args);
                    this.dao._execute(" INSERT INTO task(id,scoreId,examPaperNum,questionNum,insertDate,updateTime,isException,isDelete ,status ,groupNum,rownum,insertuser,testingCentreId,userNum)SELECT r.*,'2' FROM  \t(  SELECT  UUID_SHORT() id,scoreId,examPaperNum,questionNum,now() insertDate,now() updateTime,'F' isException, isDelete ,'F' status,groupNum,0 rownum,-1 insertuser,testingCentreId  from  task  where examPaperNum={examPaperNum}   and groupNum={groupNum}  and usernum='1')r  ", args);
                }
                String counttask2 = this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
                String updateGroupCount = " update questiongroup set totalnum={counttask2}" + counttask2 + "' where examPaperNum={examPaperNum} " + qgq.getExampaperNum() + "' and groupNum={groupNum} " + qgq.getGroupNum() + "'";
                args.put("counttask2", counttask2);
                this.dao._execute(updateGroupCount, args);
                args.remove("questionNum");
                args.remove("counttask2");
            }
        }
    }

    public List restQuestion(String examPaperNum, String tab, String type, String userId, String schoolNum, String schoolGroupNum) {
        List list;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("userId", userId);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        new ArrayList();
        if ("6".equals(tab)) {
            list = this.dao._queryBeanList("select qq.exampaperNum,qq.groupNum,qs.makType,qq.questionNum,t.questionName,IFNULL(qq.shensuStatus,'0') shensuStatus,IFNULL(qq.shensuYuzhi,'') shensuYuzhi,t.fullScore ext1 from questiongroup_question qq LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS questionName ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id ,CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore ,CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum from define d LEFT JOIN subdefine s ON d.id = s.pid where d.exampaperNum={examPaperNum} ) t on qq.questionNum = t.id LEFT JOIN questiongroup_mark_setting qs on qq.groupNum=qs.groupNum where qq.exampaperNum={examPaperNum}group by qq.questionNum ORDER BY t.orderNum", QuestionGroup_question.class, args);
        } else if (!tab.equals("3")) {
            String sql1 = "";
            String sql2 = "";
            String sgWStr = "";
            if ("authority".equals(type)) {
                if (!"-1".equals(userId) && !"-2".equals(userId) && null == map) {
                    String schStr = (StrUtil.isEmpty(schoolNum) || "-1".equals(schoolNum)) ? "" : " and schoolnum={schoolNum} ";
                    sql1 = " INNER JOIN(SELECT DISTINCT sq.groupNum from schoolquota sq   inner JOIN(  SELECT schoolnum from user where id={userId} " + schStr + " UNION   SELECT schoolnum from schoolscanpermission where userNum={userId} " + schStr + " )s on sq.schoolnum=s.schoolnum  where sq.examPaperNum={examPaperNum}  )sa on aaa.groupNum=sa.groupNum ";
                    sql2 = " inner JOIN(  SELECT schoolnum from user where id={userId} " + schStr + " UNION   SELECT schoolnum from schoolscanpermission where userNum={userId} " + schStr + " )s on u.schoolnum=s.schoolnum ";
                } else {
                    String schStr2 = (StrUtil.isEmpty(schoolNum) || "-1".equals(schoolNum)) ? "" : " and id={schoolNum} ";
                    sql1 = " INNER JOIN(SELECT DISTINCT sq.groupNum from schoolquota sq   inner JOIN(  SELECT id schoolNum from school " + schStr2 + " )s on sq.schoolnum=s.schoolNum  where sq.examPaperNum={examPaperNum}  )sa on aaa.groupNum=sa.groupNum ";
                    sql2 = " inner JOIN(  SELECT id schoolNum from school " + schStr2 + " )s on u.schoolnum=s.schoolNum ";
                }
            } else if ("authority_group".equals(type)) {
                if ("-2".equals(schoolGroupNum)) {
                    sgWStr = " ";
                    sql1 = " ";
                    sql2 = " ";
                } else {
                    if ("-1".equals(schoolGroupNum)) {
                        sgWStr = " and sg.schoolGroupNum is null ";
                    } else {
                        sgWStr = " and sg.schoolGroupNum={schoolGroupNum} ";
                    }
                    sql1 = " INNER JOIN (SELECT DISTINCT sq.groupNum from schoolquota sq   left join schoolgroup sg on sg.schoolNum=sq.schoolnum  where sq.examPaperNum={examPaperNum} " + sgWStr + "  )sa on aaa.groupNum=sa.groupNum ";
                    sql2 = " left join schoolgroup sg on sg.schoolNum=u.schoolnum ";
                }
            }
            String sql = "select aaa.exampaperNum,GROUP_CONCAT( DISTINCT aaa.groupNum ORDER BY IFNULL(_d.orderNum,sd.orderNum),subd.orderNum SEPARATOR '_' ) groupNum,  aaa.groupName,aaa.groupType,aaa.groupcount2,aaa.stat,aaa.scancompleted,aaa.upperFloat ,q1.id quid,q1.userType,u.id userNum,  GROUP_CONCAT(DISTINCT q1.realname) realName,count(DISTINCT qu.userNum) count2,  aaa.choosename ext1,IFNULL( sd_d.questionNum, _dd.questionNum ) mainName,subd.orderNum,IFNULL(_d.fullScore,subd.fullScore) fullScore,aaa.autoCommitForbid,aaa.yuejuanWay,aaa.aotoCommitDefault,aaa.correctForbid,IFNULL(sub.subjectName,sub2.subjectName) subjectName  from (  select q.exampaperNum,q.groupNum,q.groupName,q.groupType,q.groupcount2,q.stat,q.scancompleted,q.upperFloat,dd.choosename,q.autoCommitForbid,q.yuejuanWay,q.aotoCommitDefault,q.correctForbid  from  (  SELECT q1.exampaperNum,q1.groupNum,q1.groupName,q1.groupType,IFNULL(count(qgq1.groupNum),0) groupcount2,stat,scancompleted,upperFloat,ifnull(autoCommitForbid,0) autoCommitForbid,ifnull(aotoCommitDefault,1) aotoCommitDefault,ifnull(correctForbid,0) correctForbid,yuejuanWay from questiongroup q1 LEFT join questiongroup_question qgq1 on q1.exampaperNum=qgq1.exampaperNum and q1.groupNum=qgq1.groupNum   where q1.exampaperNum={examPaperNum} and q1.groupType!='0' group by q1.groupNum  ) q LEFT JOIN (  select * from(    SELECT d.id ,d.orderNum,d.choosename from define d where d.exampaperNum ={examPaperNum}      UNION     SELECT sb.id,sb.orderNum,d.choosename from define d LEFT JOIN subdefine sb on sb.pid=d.id where sb.exampaperNum = {examPaperNum}  )d ORDER BY d.orderNum  ) dd on q.groupNum = dd.id  ) aaa   " + sql1 + "LEFT JOIN subdefine subd ON subd.id = aaa.groupNum   LEFT JOIN define sd ON sd.id = subd.pid   LEFT JOIN define sd_d ON sd_d.id = sd.choosename   LEFT JOIN define _d ON _d.id = aaa.groupNum   LEFT JOIN define _dd ON _dd.id = _d.choosename   LEFT JOIN exampaper ep ON ep.examPaperNum = _d.category and ep.isHidden='T' LEFT JOIN exampaper ep2 ON ep2.examPaperNum = subd.category and ep2.isHidden='T' LEFT JOIN subject sub ON sub.subjectNum = ep.subjectNum LEFT JOIN subject sub2 ON sub2.subjectNum = ep2.subjectNum LEFT JOIN questiongroup_user qu on aaa.exampaperNum=qu.exampaperNum  AND aaa.groupNum=qu.groupNum and qu.userType<>2  LEFT JOIN(SELECT q1.*,u.realname from questiongroup_user q1 left join user u on q1.userNum=u.id " + sql2 + "where q1.examPaperNum={examPaperNum} and q1.usertype='1' " + sgWStr + ")q1 on aaa.groupNum=q1.groupNum left join user u on q1.userNum=u.id   GROUP BY if(ext1='s',aaa.groupNum,CONCAT(ext1,IFNULL(subd.orderNum,''))) ,q1.usertype   ORDER BY IFNULL( _d.orderNum, sd.orderNum ),subd.orderNum";
            args.put("userId", userId);
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
            args.put("schoolGroupNum", schoolGroupNum);
            list = this.dao._queryBeanList(sql, QuestionGroup.class, args);
        } else {
            list = this.dao._queryBeanList("select GROUP_CONCAT(q.groupNum SEPARATOR '_') groupNum,q.groupName,q.groupType,q.exampaperNum,q.stat,q.yuejuanWay,q.scancompleted,q.upperFloat,qms.id qmsid,qms.makType makType,qms.type qmsType,qms.judgetype,qms.threeWarn,d.fullScore ,d.errorRate,d.isParent ext1,IFNULL( sd.choosename, _d.choosename ) choosename,IFNULL( sd_d.questionNum, _dd.questionNum ) mainName,subd.orderNum  from questionGroup q left join questiongroup_mark_setting qms  on q.exampaperNum=qms.exampaperNum and q.groupNum=qms.groupNum LEFT JOIN( \tselect '0' isParent, id,exampaperNum,fullScore,errorRate from define where examPaperNum={examPaperNum} \tUNION \tselect  '1' isParent, id ,exampaperNum,fullScore,errorRate  from subdefine WHERE examPaperNum={examPaperNum} )d on q.exampaperNum=d.exampaperNum and q.groupNum=d.id LEFT JOIN subdefine subd ON subd.id = q.groupNum LEFT JOIN define sd ON sd.id = subd.pid LEFT JOIN define sd_d ON sd_d.id = sd.choosename LEFT JOIN define _d ON _d.id = q.groupNum LEFT JOIN define _dd ON _dd.id = _d.choosename  where q.exampaperNum={examPaperNum}  and q.groupType!='0'  GROUP BY if(IFNULL(sd.choosename,_d.choosename)='s',q.groupNum,CONCAT(IFNULL(sd.choosename,_d.choosename),IFNULL(subd.orderNum,'')))  ORDER BY IFNULL(_d.orderNum,sd.orderNum),subd.orderNum", QuestionGroup.class, args);
        }
        return list;
    }

    public List getYuZhiList(String examPaperNum, String groupNum, String groupType, String userId, String isParent) {
        String[] groupNumArr = groupNum.split("_");
        String groupNumStr = "";
        for (String str : groupNumArr) {
            groupNumStr = groupNumStr + str + Const.STRING_SEPERATOR;
        }
        String groupNumStr2 = groupNumStr.substring(0, groupNumStr.length() - 1);
        Map args = new HashMap();
        args.put("groupNumStr", groupNumStr2);
        String sql = "";
        if ("2".equals(groupType)) {
            sql = "SELECT id,questionNum,fullScore fullScore1,errorRate from subdefine where pid in ({groupNumStr[]}) ";
        } else if ("1".equals(groupType)) {
            if ("0".equals(isParent)) {
                sql = "SELECT id,questionNum,fullScore fullScore1,errorRate from define where id in ({groupNumStr[]}) ";
            } else {
                sql = "SELECT id,questionNum,fullScore fullScore1,errorRate from subdefine where id in ({groupNumStr[]}) ";
            }
        }
        return this.dao._queryBeanList(sql, Define.class, args);
    }

    public List<TestingcentreDis> getTestingcentreDis(String examPaperNum) {
        new ArrayList();
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return this.dao._queryBeanList("select t.*,t1.testingCentreName ext1 from testingcentredis t  INNER JOIN testingcentre t1 on t.testingCentreId=t1.id  where t.exampaperNum={examPaperNum}  ORDER BY CONVERT(t1.testingCentreName USING gbk) asc ", TestingcentreDis.class, args);
    }

    public TestingcentreDis getTestingcentreDisOne(String examPaperNum, String testingCentreId, String school_limit, String insertUser) {
        String isDis;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("testingCentreId", testingCentreId);
        args.put("insertUser", insertUser);
        String testingcentredis = Configuration.getInstance().getTestingcentredis();
        if ("1".equals(testingcentredis)) {
            isDis = "1";
        } else if ("1".equals(school_limit)) {
            isDis = "1";
        } else {
            isDis = "0";
        }
        List<?> _queryBeanList = this.dao._queryBeanList("select ec.testingCentreId from examinationroom ec    LEFT JOIN exampaper e on ec.examNum=e.examNum and ec.gradeNum=e.gradeNum and ec.subjectNum=e.subjectNum     where e.examPaperNum={examPaperNum}  GROUP BY ec.testingCentreId ", TestingcentreDis.class, args);
        List<Map> argMapList = new ArrayList<>();
        for (int i = 0; i < _queryBeanList.size(); i++) {
            int finalI = i;
            String finalIsDis = isDis;
            Map args2 = new HashMap();
            args2.put("examPaperNum", examPaperNum);
            args2.put("TestingCentreId", ((TestingcentreDis) _queryBeanList.get(finalI)).getTestingCentreId());
            args2.put("isDis", finalIsDis);
            argMapList.add(args2);
        }
        this.dao._batchExecute("insert into testingcentreDis (id,examPaperNum,testingCentreId,isDis,insertUser,insertDate) values(UUID_SHORT(),{examPaperNum},{TestingCentreId},{isDis},'-2',now() ) ON DUPLICATE KEY UPDATE updateUser='-2',updateDate=now() ", argMapList);
        this.dao._execute("DELETE t from testingcentredis t INNER JOIN(  SELECT id from testingcentredis td left join (  select ec.testingCentreId from examinationroom ec  LEFT JOIN exampaper e on ec.examNum=e.examNum and ec.gradeNum=e.gradeNum and ec.subjectNum=e.subjectNum     where e.examPaperNum={examPaperNum} GROUP BY ec.testingCentreId   )e on td.testingCentreId=e.testingCentreId  where td.examPaperNum={examPaperNum}  and e.testingCentreId is null )t1 on t.id=t1.id", args);
        return (TestingcentreDis) this.dao._queryBean("select t.* from testingcentredis t  where t.exampaperNum={examPaperNum}  and t.testingCentreId={testingCentreId} ", TestingcentreDis.class, args);
    }

    public void resetSaoMiaoStatus(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        String count = this.dao._queryStr("SELECT IFNULL(count(1),0) from questiongroup where examPaperNum={examPaperNum} and (scancompleted=0 or scancompleted is null)", args);
        if (!"0".equals(count)) {
            this.dao._execute("update questiongroup set scancompleted=0 where examPaperNum={examPaperNum} ", args);
        }
    }

    public String updateTestingcentreDis(String examPaperNum, String testingCentreId, String isDis, String userId) {
        String aa;
        String str = "";
        if (!"".equals(testingCentreId) && !"null".equals(testingCentreId) && null != testingCentreId) {
            str = str + " and testingCentreId={testingCentreId} ";
        }
        String sql = "update testingcentredis set isDis={isDis},updateUser={userId},updateDate={updateDate}  where exampaperNum={examPaperNum} " + str;
        Map args = new HashMap();
        args.put("isDis", isDis);
        args.put("userId", userId);
        args.put("updateDate", DateUtil.getCurrentTime());
        args.put("examPaperNum", examPaperNum);
        args.put("testingCentreId", testingCentreId);
        Integer.valueOf(this.dao._execute(sql, args));
        if ("0".equals(isDis)) {
            String sql2 = "select testingCentreId from testingcentredis where exampaperNum={examPaperNum} " + str;
            List<?> _queryBeanList = this.dao._queryBeanList(sql2, TestingcentreDis.class, args);
            for (int i = 0; i < _queryBeanList.size(); i++) {
                String testingId = ((TestingcentreDis) _queryBeanList.get(i)).getTestingCentreId();
                Map args2 = new HashMap();
                args2.put("examPaperNum", examPaperNum);
                args2.put("testingId", testingId);
                List<?> _queryBeanList2 = this.dao._queryBeanList("select id from task where examPaperNum={examPaperNum} and testingCentreId={testingId} and status='F' ", Task.class, args2);
                List<Map> argMapList = new ArrayList<>();
                for (int j = 0; j < _queryBeanList2.size(); j++) {
                    String id = ((Task) _queryBeanList2.get(j)).getId();
                    Map argss = new HashMap();
                    argss.put("id", id);
                    argMapList.add(argss);
                }
                this.dao._batchExecute("update task set insertUser='-1',porder=0,fenfaDate=0 where id={id}", argMapList);
                this.log4J.info("【考点分发关闭回收】考点--" + testingId + "--共回收了【" + _queryBeanList2.size() + "】条，操作人--" + userId);
                List<?> _queryBeanList3 = this.dao._queryBeanList("select id from task where examPaperNum={examPaperNum} and testingCentreId={testingId}  and status='T' and insertUser<>updateUser ", Task.class, args2);
                List<Map> argMapLists = new ArrayList<>();
                for (int j2 = 0; j2 < _queryBeanList3.size(); j2++) {
                    String id2 = ((Task) _queryBeanList3.get(j2)).getId();
                    Map argsss = new HashMap();
                    argsss.put("id", id2);
                    argMapLists.add(argsss);
                }
                this.dao._batchUpdate("update task set insertUser=updateUser where id={id}", argMapLists);
                this.log4J.info("【考点分发关闭-回收过程中的异常数据】考点--" + testingId + "--共处理了【" + _queryBeanList3.size() + "】条，操作人--" + userId);
            }
        }
        if ("0".equals(isDis)) {
            Map args_updateSql = new HashMap();
            args_updateSql.put("scancompleted", 0);
            args_updateSql.put("exampaperNum", examPaperNum);
            this.dao._execute("update questiongroup set scancompleted={scancompleted}  where exampaperNum={exampaperNum} ", args_updateSql);
            this.log4J.info("【修改扫描状态为未完成】----【裁切校对--关闭考点开放】，考点--【" + testingCentreId + "】,科目试卷编号---【" + examPaperNum + "】，修改人---【" + userId + "】");
            aa = "1";
        } else {
            Map args_testingCentreSql = new HashMap();
            args_testingCentreSql.put("examPaperNum", examPaperNum);
            List<Map<String, Object>> mapList = this.dao._queryMapList("select * from testingcentredis where exampaperNUm={examPaperNum} and isDis=0", TypeEnum.StringObject, args_testingCentreSql);
            if (mapList.size() == 0) {
                Map args_updateSql2 = new HashMap();
                args_updateSql2.put("scancompleted", 1);
                args_updateSql2.put("exampaperNum", examPaperNum);
                this.dao._execute("update questiongroup set scancompleted={scancompleted}  where exampaperNum={exampaperNum} ", args_updateSql2);
                this.log4J.info("【修改扫描状态为完成】----【裁切校对--开启考点开放】，考点--【" + testingCentreId + "】,科目试卷编号---【" + examPaperNum + "】，修改人---【" + userId + "】");
                aa = "1";
            } else {
                aa = "1";
            }
        }
        return String.valueOf(aa);
    }

    public List errorRateandfullsore(String groupNum) {
        new ArrayList();
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        return this.dao._queryBeanList("(select '0' isParent, id,exampaperNum,fullScore,errorRate from define where id={groupNum}  )  union all (select  '1' isParent, id ,exampaperNum,fullScore,errorRate  from subdefine WHERE id={groupNum} )", AwardPoint.class, args);
    }

    public int updateThreeWarn(String groupNum, String value) {
        Map args = new HashMap();
        args.put("value", value);
        args.put("groupNum", groupNum);
        return this.dao._execute("update questiongroup_mark_setting set threeWarn={value}  where groupNum={groupNum} ", args);
    }

    public List getGroup(String examPaperNum, String tab) {
        String sql;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        if (tab.equals("1")) {
            sql = "select q.*,qu.id quid,qu.userType userType,u.id userNum,u.realname realName from (select q1.exampaperNum,q1.groupNum,q1.groupName,q1.groupType,IFNULL(count(qgq1.groupNum),0) count2  from questiongroup q1 LEFT join questiongroup_question qgq1  on q1.exampaperNum=qgq1.exampaperNum and q1.groupNum=qgq1.groupNum where q1.exampaperNum={examPaperNum}  and  q1.groupType='0' group by q1.groupNum  ) q  LEFT JOIN questiongroup_user qu on  q.exampaperNum=qu.exampaperNum  AND qu.groupNum=q.groupNum AND qu.usertype='1' left join user u on qu.userNum=u.id  GROUP BY  q.groupNum,qu.usertype  ORDER BY CASE  WHEN LOCATE('-',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('-' IN q.groupName)-1),'.01')*1  WHEN LOCATE('_',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('_' IN q.groupName)-1),'.02')*1  ELSE CONCAT(q.groupName,'.1')*1 END  ASC ,REPLACE(q.groupName,'_','.')*1";
        } else if (tab.equals("2")) {
            sql = "SELECT q.*,IFNULL(qu.num,0) count2,q1.id quid,q1.userType userType,u.id userNum,u.realname realName  FROM  ( SELECT qp.exampaperNum,qp.groupNum,qp.groupName,qp.groupType FROM questiongroup qp left JOIN questiongroup_question qq ON qp.exampaperNum=qq.exampaperNum AND qp.groupNum=qq.groupNum  where qp.exampaperNum={examPaperNum}  and qp.groupType='0' GROUP BY qp.groupNum HAVING  COUNT(qq.id)>0 )q LEFT JOIN ( SELECT  exampaperNum,groupNum,COUNT(1) num from questiongroup_user  WHERE  exampaperNum={examPaperNum}  GROUP BY groupnum) qu  on  q.exampaperNum=qu.exampaperNum  AND qu.groupNum=q.groupNum LEFT JOIN questiongroup_user q1 ON  q.exampaperNum=q1.exampaperNum  AND q1.groupNum=q.groupNum AND q1.userType='1' LEFT JOIN user  u ON u.id=q1.userNum GROUP BY  q.groupNum,q1.usertype  ORDER BY CASE  WHEN LOCATE('-',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('-' IN q.groupName)-1),'.01')*1  WHEN LOCATE('_',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('_' IN q.groupName)-1),'.02')*1  ELSE CONCAT(q.groupName,'.1')*1 END  ASC ,REPLACE(q.groupName,'_','.')*1";
        } else {
            sql = "select q.exampaperNum,q.groupNum,q.groupName,q.groupType,qms.id qmsid,qms.makType makType,qms.type qmsType   from questionGroup q left join questiongroup_mark_setting qms  on q.exampaperNum=qms.exampaperNum and q.groupNum=qms.groupNum  where q.exampaperNum={examPaperNum}  and q.groupType='0' ORDER BY CASE  WHEN LOCATE('-',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('-' IN q.groupName)-1),'.01')*1  WHEN LOCATE('_',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('_' IN q.groupName)-1),'.02')*1  ELSE CONCAT(q.groupName,'.1')*1 END  ASC ,REPLACE(q.groupName,'_','.')*1";
        }
        return this.dao._queryBeanList(sql, QuestionGroup.class, args);
    }

    public List getAllQuestion(String examPaperNum, String insertUser) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("insertUser", insertUser);
        return this.dao._queryBeanList("SELECT z.examPaperNum,z.groupNum,z.groupName,z.groupType,z.stat from( \tSELECT groupNum from questiongroup_user where exampaperNum={examPaperNum}  and userNum={insertUser} \t)q inner JOIN questiongroup z on q.groupNum=z.groupNum  ORDER BY CASE  WHEN LOCATE('-',z.groupName)>0 THEN  CONCAT(SUBSTR(z.groupName,1,POSITION('-' IN z.groupName)-1),'.01')*1  WHEN LOCATE('_',z.groupName)>0 THEN  CONCAT(SUBSTR(z.groupName,1,POSITION('_' IN z.groupName)-1),'.02')*1  ELSE CONCAT(z.groupName,'.1')*1 END  ASC ,REPLACE(z.groupName,'_','.')*1", QuestionGroup.class, args);
    }

    public int insertTestingDis(String examPaperNum, String insertUser, String school_limit) {
        String isDis;
        String testingcentredis = Configuration.getInstance().getTestingcentredis();
        if ("1".equals(testingcentredis)) {
            isDis = "1";
        } else if ("1".equals(school_limit)) {
            isDis = "1";
        } else {
            isDis = "0";
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        List<?> _queryBeanList = this.dao._queryBeanList("select ec.testingCentreId from examinationroom ec    LEFT JOIN exampaper e on ec.examNum=e.examNum and ec.gradeNum=e.gradeNum and ec.subjectNum=e.subjectNum     where e.examPaperNum={examPaperNum}  GROUP BY ec.testingCentreId ", TestingcentreDis.class, args);
        for (int i = 0; i < _queryBeanList.size(); i++) {
            int finalI = i;
            String finalIsDis = isDis;
            Map args2 = new HashMap();
            args2.put("examPaperNum", examPaperNum);
            args2.put("TestingCentreId", ((TestingcentreDis) _queryBeanList.get(finalI)).getTestingCentreId());
            args2.put("isDis", finalIsDis);
            this.dao._execute("insert into testingcentreDis (id,examPaperNum,testingCentreId,isDis,insertUser,insertDate) values(UUID_SHORT(),{examPaperNum},{TestingCentreId},{isDis},'-2',now() ) ON DUPLICATE KEY UPDATE updateUser='-2',updateDate=now() ", args2);
        }
        this.dao._execute("DELETE t from testingcentredis t INNER JOIN(  SELECT id from testingcentredis td left join (  select ec.testingCentreId from examinationroom ec  LEFT JOIN exampaper e on ec.examNum=e.examNum and ec.gradeNum=e.gradeNum and ec.subjectNum=e.subjectNum     where e.examPaperNum={examPaperNum} GROUP BY ec.testingCentreId   )e on td.testingCentreId=e.testingCentreId  where td.examPaperNum={examPaperNum}  and e.testingCentreId is null )t1 on t.id=t1.id", args);
        return 1;
    }

    public List getQuestionNum(QuestionGroup_question qq) {
        String sql;
        Map args = new HashMap();
        args.put("exampaperNum", qq.getExampaperNum());
        args.put("groupNum", qq.getGroupNum());
        if ("2".equals(qq.getGroupType())) {
            sql = "select qq.id,qq.questionNum,qq.exampaperNum,d.questionName from((SELECT  id,questionNum,exampaperNum FROM questiongroup_question  WHERE exampaperNum={exampaperNum}  and groupNum={groupNum} ')qq left join  (select id questionNum,questionnum questionName from subdefine   WHERE exampaperNum={exampaperNum} )d on qq.questionNum=d.questionNum) ORDER BY CASE  WHEN LOCATE('-',d.questionName)>0 THEN CONCAT(SUBSTR(d.questionName,1,POSITION('-' IN d.questionName)-1),'.01')*1  WHEN LOCATE('_',d.questionName)>0 THEN CONCAT(SUBSTR(d.questionName,1,POSITION('_' IN d.questionName)-1),'.02')*1  ELSE CONCAT(d.questionName,'.1')*1 END  ASC ,REPLACE(d.questionName,'_','.')*1";
        } else {
            sql = "select qq.id,qq.questionNum,qq.exampaperNum,d.questionName from((SELECT  id,questionNum,exampaperNum FROM questiongroup_question   WHERE exampaperNum={exampaperNum} and groupNum={groupNum} ')qq left join  ((select id questionNum,questionnum questionName from define   WHERE exampaperNum={exampaperNum} )  union all (select id questionNum,questionnum questionName from subdefine WHERE exampaperNum={exampaperNum}  )d on qq.questionNum=d.questionNum) ORDER BY CASE  WHEN LOCATE('-',d.questionName)>0 THEN CONCAT(SUBSTR(d.questionName,1,POSITION('-' IN d.questionName)-1),'.01')*1  WHEN LOCATE('_',d.questionName)>0 THEN CONCAT(SUBSTR(d.questionName,1,POSITION('_' IN d.questionName)-1),'.02')*1  ELSE CONCAT(d.questionName,'.1')*1 END  ASC ,REPLACE(d.questionName,'_','.')*1";
        }
        return this.dao._queryBeanList(sql, QuestionGroup_question.class, args);
    }

    public List getXztQuestionNum(QuestionGroup_question qq) {
        Map args = new HashMap();
        args.put("GroupNum", qq.getGroupNum());
        args.put("exampaperNum", qq.getExampaperNum());
        return this.dao._queryBeanList("select qq.id,qq.questionNum,qq.exampaperNum,sd.questionNum questionName FROM questiongroup_question qq inner join (select subd.id,subd.questionNum,d2.orderNum,subd.orderNum orderNum2 from define d left join define d2 on d2.choosename=d.choosename left join subdefine subd on subd.pid=d2.id where d.id ={GroupNum} ) sd on sd.id=qq.questionNum WHERE qq.exampaperNum={exampaperNum} ORDER BY sd.orderNum,sd.orderNum2", QuestionGroup_question.class, args);
    }

    public List getrestQuestionNum(String examPaperNum, String groupNum, int tab) {
        Map args = new HashMap();
        args.put("exampaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        String sql = "";
        if (tab == 1) {
            sql = "select exampaperNum,groupNum,groupName from questionGroup where exampaperNum={exampaperNum}  and groupNum!={groupNum} and groupType='0'";
        } else if (tab == 2) {
            sql = "select exampaperNum,groupNum,groupName from questionGroup  where exampaperNum={examPaperNum}  and groupNum!={groupNum}  ";
        }
        return this.dao._queryBeanList(sql, QuestionGroup.class, args);
    }

    public Integer updateGroupQuestion(QuestionGroup qg, QuestionGroup_question qq, String loginName) {
        if (null != qg.getGroupName() && !"".equals(qg.getGroupName())) {
            this.dao.save(qg);
            createGroupfz_mark_setting(qg.getExampaperNum(), qg.getGroupNum(), qg.getInsertUser());
        }
        Map args = new HashMap();
        args.put("groupNum", qq.getGroupNum());
        args.put("questionNum", qq.getQuestionNum());
        args.put("exampaperNum", qq.getExampaperNum());
        if (qq.getGroupNum2() != null) {
            deletetask(qq.getExampaperNum(), qq.getGroupNum2(), null, "", "");
        } else {
            deletetask(qq.getExampaperNum(), qq.getGroupNum(), null, "", "");
        }
        this.dao._execute("update QuestionGroup_question set groupNum = {groupNum}  where questionNum ={questionNum}  and exampaperNum={exampaperNum} ", args);
        this.dao._execute("update task set groupNum={groupNum}  where examPaperNum={examPaperNum} and questionNum={questionNum} ", args);
        String markval = this.dao._queryStr("select /* shard_host_HG=Write */ makType from questiongroup_mark_setting  where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
        if (markval.equals("0")) {
            this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}   and groupNum={groupNum}  and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
            this.dao._execute("delete from remark where examPaperNum={examPaperNum} and questionNum={questionNum} ", args);
            this.dao._execute(" delete from task where examPaperNum={examPaperNum}  and groupNum={groupNum}  and usernum!='1' ", args);
        } else if (markval.equals("1")) {
            this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and groupNum={groupNum} and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
            this.dao._execute("delete from remark where examPaperNum={examPaperNum} and questionNum={questionNum} ", args);
            this.dao._execute(" delete from task where examPaperNum={examPaperNum} and groupNum={groupNum}  and usernum!='1' ", args);
            this.dao._execute(" INSERT INTO task(id,scoreId,examPaperNum,questionNum,insertDate,updateTime,isException,isDelete ,status ,groupNum, rownum,insertuser,testingCentreId,userNum)SELECT r.*,'2' FROM  \t(  SELECT  UUID_SHORT() id,scoreId,examPaperNum,questionNum,now() insertDate,now() updateTime,'F' isException, isDelete ,'F' status,groupNum,0 rownum,-1 insertuser,testingCentreId  from  task  where examPaperNum={examPaperNum}  and groupNum={groupNum}  and usernum='1')r  ", args);
        }
        args.put("groupNum2", qq.getGroupNum2());
        String counttask1 = this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where examPaperNum={examPaperNum} and groupNum={groupNum2} ", args).toString();
        String counttask2 = this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
        args.put("counttask1", counttask1);
        this.dao._execute(" update questiongroup set totalnum={counttask1} where examPaperNum={examPaperNum}  and groupNum={groupNum2} ", args);
        args.put("counttask2", counttask2);
        this.dao._execute(" update questiongroup set totalnum={counttask2}  where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
        return 1;
    }

    public Integer removeGroupQuestion(Integer examPaperNum, String questionNum, String groupNum, String questionName, String loginName) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        args.put("questionNum", questionNum);
        deletetask(examPaperNum, groupNum, null, "", "");
        this.dao._execute("delete from QuestionGroup_question where examPaperNum ={examPaperNum}  and groupNum={groupNum} and questionNum={questionNum} ", args);
        createGroupfz(examPaperNum, questionNum, questionName, loginName, "1");
        this.dao._execute("update task set groupNum={questionNum}  where examPaperNum={examPaperNum} ' and questionNum={questionNum}  ", args);
        this.dao._execute(" delete from task where examPaperNum={examPaperNum} and groupNum={questionNum} and usernum!='1' ", args);
        this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and groupNum={questionNum}  and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
        this.dao._execute("delete from remark where examPaperNum={examPaperNum} and questionNum={questionNum} ", args);
        String counttask1 = this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
        String counttask2 = this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where examPaperNum={examPaperNum} and groupNum={groupNum} ", args);
        args.put("counttask1", counttask1);
        this.dao._execute(" update questiongroup set totalnum={counttask1} where examPaperNum={examPaperNum} and groupNum={groupNum}", args);
        args.put("counttask2", counttask2);
        this.dao._execute(" update questiongroup set totalnum={counttask2}  where examPaperNum={examPaperNum}  and groupNum={questionNum} ", args);
        return null;
    }

    public Integer deletegroup(QuestionGroup_question qq) {
        Map args = new HashMap();
        args.put("examPaperNum", qq.getExampaperNum());
        args.put("groupNum", qq.getGroupNum());
        List<QuestionGroup_question> list = this.dao._queryBeanList("SELECT id,exampaperNum,groupNum,questionNum from questiongroup_question where examPaperNum ={examPaperNum}  and groupNum={groupNum} ", QuestionGroup_question.class, args);
        deleteGroupfz(qq.getExampaperNum(), qq.getGroupNum(), "", "0", "");
        new QuestionGroup();
        for (QuestionGroup_question qgq : list) {
            new QuestionGroup_question();
            Map args2 = new HashMap();
            args2.put("exampaperNum", qgq.getExampaperNum());
            args2.put("questionNum", qgq.getQuestionNum());
            String questionName = this.dao._queryStr("select a.questionNum from ((select id,questionnum from define where exampaperNum={exampaperNum}  and id={questionNum} ) union all (select id,questionnum  from subdefine WHERE exampaperNum={exampaperNum}  and id={questionNum} ))a", args2);
            createGroupfz(qgq.getExampaperNum(), qgq.getQuestionNum(), questionName, qq.getInsertUser(), "1");
            args2.put("groupNum", qgq.getGroupNum());
            this.dao._execute("update task set groupNum={questionNum}  where examPaperNum={examPaperNum}  and questionNum={questionNum}  and groupNum={groupNum} ", args2);
            this.dao._execute(" delete from task where examPaperNum={examPaperNum} and groupNum={questionNum} and usernum!='1' ", args2);
            this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and groupNum={questionNum}  and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args2);
            this.dao._execute("delete from remark where examPaperNum={examPaperNum} and questionNum={groupNum}", args2);
            String counttask2 = this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where examPaperNum={examPaperNum} and questionNum={questionNum} ", args2);
            args2.put("counttask2", counttask2);
            this.dao._execute(" update questiongroup set totalnum={counttask2} where examPaperNum={examPaperNum}  and groupNum={questionNum}", args2);
        }
        return null;
    }

    public List getGroupByName(Integer examPaperNum, String name) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("name", name);
        return this.dao._queryBeanList("select groupNum,count(1) count2 from questionGroup where examPaperNum={examPaperNum} and groupName={name} ", QuestionGroup.class, args);
    }

    public List getPjUserList(String examNum, String examPaperNum, String roleNum, String realName, String roleType, String schoolNum, String note, String ext1, String category, String newGroupNum, String type, String schoolGroupNum) {
        Map args = new HashMap();
        args.put("note", note);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("examPaperNum", examPaperNum);
        args.put("roleType", roleType);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        String realName2 = StrUtil.isEmpty(realName) ? "" : realName;
        args.put("realName", "%" + realName2 + "%");
        args.put("category", category);
        args.put("newGroupNum", newGroupNum);
        args.put("schoolGroupNum", schoolGroupNum);
        String sgStr = "";
        String sgWStr = "";
        if ("authority_group".equals(type) && !"-2".equals(schoolGroupNum) && "-1".equals(schoolNum)) {
            sgStr = " left join schoolgroup sg on sg.schoolNum=u.schoolnum ";
            sgWStr = "-1".equals(schoolGroupNum) ? " and sg.schoolGroupNum is null " : " and sg.schoolGroupNum={schoolGroupNum} ";
        }
        String noteStr = "";
        if ("1".equals(ext1) && !"-1".equals(note)) {
            noteStr = noteStr + " and e.note={note}  ";
        }
        String zikemuStr = examPaperNum.equals(category) ? "" : " and us.exampaperNum={category} ";
        String sql = "SELECT u.*,g.gradeName,if(us.exampaperNum is not null,us.subjectName,'') ext1 from userrole ur   LEFT JOIN(  SELECT us.exampaperNum,s.subjectName,us.userNum from userrole_sub us inner JOIN exampaper e on us.exampaperNum=e.examPaperNum  LEFT JOIN `subject` s on e.subjectNum=s.subjectNum where e.pexamPaperNum={examPaperNum}  )us on ur.userNum=us.userNum LEFT JOIN role r on ur.roleNum=r.roleNum LEFT JOIN user u on u.id=ur.userNum LEFT JOIN userposition up on up.userNum=u.id  left JOIN basegrade g on g.gradeNum=up.gradeNum " + sgStr + "LEFT JOIN extragroupuser e on e.userNum=u.id  LEFT JOIN (SELECT userNum from questiongroup_user WHERE examPaperNum={examPaperNum} AND groupNum in ({newGroupNum[]})) qu ON qu.userNum=ur.userNum WHERE r.examNum={examNum}  and r.examPaperNum={examPaperNum}  and r.type={roleType} AND qu.userNum is null " + zikemuStr + noteStr + sgWStr;
        if (!"-1".equals(schoolNum)) {
            sql = sql + " and u.schoolNum={schoolNum} ";
        }
        if (realName2 != null) {
            sql = (sql + " and (u.realName like {realName}  ") + " or u.username like {realName} ) ";
        }
        return this.dao._queryBeanList(sql + " GROUP BY u.username ORDER BY length(u.username),u.username,g.gradeNum asc", User.class, args);
    }

    public List getPjUserList2(String examNum, String examPaperNum, String roleNum, String realName, String roleType, String schoolNum, String note, String ext1, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        String realNameStr = "";
        if (StrUtil.isNotEmpty(realName)) {
            realNameStr = " and( realname like {realname} or username like {realname}) ";
            args.put("realname", "%" + realName + "%");
        }
        String subjectStr = "";
        if (!subjectNum.equals("-1")) {
            subjectStr = " and up.subjectNum={subjectNum}";
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        }
        String sql = "select a.id id,a.userId userid,realName,a.userName,a.`password`,a.isUser,a.isDelete,a.insertUser,a.insertDate,a.mobile,schoolNum,a.gradeNum,bg.gradeName,a.subjectNum,GROUP_CONCAT(subjectName) ext1 from ( select u.id id,u.userId userId,u.`password`,u.realName,u.userName,u.isUser,u.isDelete,u.insertUser,u.insertDate,u.mobile,up.type,d.name,up.description,up.schoolNum,gradeNum,up.subjectNum from user u left join userposition up on u.id= up.userNum left join data d on d.type=31 and up.type=d.value  where u.schoolNum={schoolNum} and userType=1 " + realNameStr + subjectStr + " group by id,up.subjectNum order by userNum )a  left join subject sub on a.subjectNum=sub.subjectNum left join basegrade bg on bg.gradeNum=a.gradeNum group By a.id ";
        return this.dao._queryBeanList(sql, User.class, args);
    }

    public List<User> getUsersByRoleNum(String examNum, String roleNum) {
        Map args = new HashMap();
        args.put("roleNum", roleNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryBeanList("select u.* from userrole ur,role r,user u  where r.roleNum = ur.roleNum and u.id=ur.userNum  and ur.roleNum ={roleNum}  and r.examNum={examNum} ", User.class, args);
    }

    public List<User> getUsersByGroupNum(String examPaperNum, String groupNum, String realName, String schoolNum, String type, String loginUserId, String schoolGroupNum) {
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = new HashMap();
        args.put("newGroupNum", newGroupNum2);
        args.put("loginUserId", loginUserId);
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("realName", "%" + realName + "%");
        args.put("schoolGroupNum", schoolGroupNum);
        String authStr = "";
        if ("authority".equals(type)) {
            authStr = authStr + "INNER JOIN(  SELECT schoolnum from user where id={loginUserId}  UNION   SELECT schoolnum from schauthormanage where userId={loginUserId} )sa on u.schoolnum=sa.schoolnum";
        }
        String sgStr = "";
        String sgWStr = "";
        if ("authority_group".equals(type) && !"-2".equals(schoolGroupNum) && "-1".equals(schoolNum)) {
            sgStr = " left join schoolgroup sg on sg.schoolNum=u.schoolnum ";
            sgWStr = "-1".equals(schoolGroupNum) ? " and sg.schoolGroupNum is null " : " and sg.schoolGroupNum={schoolGroupNum} ";
        }
        String sql = "select u.*,gra.gradeName from QuestionGroup_user qgu left join user u  on u.id=qgu.userNum " + authStr + sgStr + " LEFT join userposition up on up.userNum=u.id   left join grade gra on up.gradeNum=gra.gradeNum  where  u.id=qgu.userNum  and qgu.exampaperNum={examPaperNum}  and qgu.groupNum in ({newGroupNum[]}) " + sgWStr + " ";
        if (!"-1".equals(schoolNum) && schoolNum != null) {
            sql = sql + " and u.schoolNum={schoolNum} ";
        }
        if (realName != null) {
            sql = (sql + " and (u.realName like {realName}  ") + " or u.username like {realName} ) ";
        }
        return this.dao._queryBeanList(sql + " GROUP BY u.username  ORDER BY gra.gradeNum asc,length(u.username),u.username ", User.class, args);
    }

    public List<User> querygroupuserz(String examPaperNum, String groupNum, String realName, String type, String loginUserId) {
        Map args = new HashMap();
        args.put("loginUserId", loginUserId);
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        args.put("realName", "%" + realName + "%");
        String authStr = "";
        if ("authority".equals(type)) {
            authStr = authStr + "INNER JOIN(  SELECT schoolnum from user where id={loginUserId}  UNION   SELECT schoolnum from schauthormanage where userId={loginUserId} )sa on u.schoolnum=sa.schoolnum";
        }
        String sql = "select u.*,gra.gradeName from QuestionGroup_user qgu left join user u  on u.id=qgu.userNum " + authStr + " LEFT join userposition up on up.userNum=u.id   left join grade gra on up.gradeNum=gra.gradeNum  where  u.id=qgu.userNum  and qgu.exampaperNum={examPaperNum}  and qgu.groupNum={groupNum}  and qgu.userType=1 ";
        if (realName != null) {
            sql = (sql + " and (u.realName like {realName} ") + " or u.username like {realName}  ) ";
        }
        return this.dao._queryBeanList(sql + " GROUP BY u.username  ORDER BY gra.gradeNum asc,length(u.username),u.username ", User.class, args);
    }

    public List<User> querygroupuserzByGroup(String examPaperNum, String groupNum, String realName, String schoolGroupNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        args.put("schoolGroupNum", schoolGroupNum);
        args.put("realName", "%" + realName + "%");
        String sgStr = "";
        String sgWStr = "";
        if (!"-2".equals(schoolGroupNum)) {
            sgStr = " left join schoolgroup sg on sg.schoolNum=u.schoolnum ";
            sgWStr = "-1".equals(schoolGroupNum) ? " and sg.schoolGroupNum is null " : " and sg.schoolGroupNum={schoolGroupNum} ";
        }
        String sql = "select u.*,gra.gradeName from questiongroup_user qgu left join user u on u.id=qgu.userNum " + sgStr + "LEFT join userposition up on up.userNum=u.id left join grade gra on up.gradeNum=gra.gradeNum where u.id=qgu.userNum  and qgu.exampaperNum={examPaperNum}  and qgu.groupNum={groupNum} and qgu.userType=1 " + sgWStr;
        if (StrUtil.isNotEmpty(realName)) {
            sql = (sql + " and (u.realName like {realName} ") + " or u.username like {realName}  ) ";
        }
        return this.dao._queryBeanList(sql + " GROUP BY u.username ORDER BY gra.gradeNum asc,length(u.username),u.username ", User.class, args);
    }

    public List queryQuestionUser(QuestionGroup_user qgu) {
        String[] groupNumArr = qgu.getGroupNum().split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = new HashMap();
        args.put("newGroupNum", newGroupNum2);
        args.put("examPaperNum", qgu.getExampaperNum());
        String chooseDefine = "";
        String orderStr = "";
        String header = "";
        if (groupNumArr.length > 1) {
            header = " ,qu.groupNum ext4,qu1.groupNum ext5 ";
            chooseDefine = "left JOIN( \tSELECT GROUP_CONCAT(qu.groupNum ORDER BY qu.groupNum SEPARATOR '_') groupNum,qu.userNum,count(1) count from questiongroup_user qu where qu.groupNum in ({newGroupNum[]}) GROUP BY userNum )qu on  qgu.userNum=qu.userNum left JOIN(   \tSELECT GROUP_CONCAT(qu.groupNum ORDER BY qu.groupNum SEPARATOR '_') groupNum,qu.userNum,count(1) count from questiongroup_user qu where qu.groupNum in ({newGroupNum[]}) and (appendTeacher<>1 or appendTeacher is null)  GROUP BY userNum )qu1 on  qgu.userNum=qu1.userNum  LEFT JOIN( SELECT GROUP_CONCAT(d1.id SEPARATOR '_') id,case when d1.orderNum=0 then d.questionNum else CONCAT(d.questionNum,'_',d1.orderNum)end questionNum,d.orderNum orderNum1  from define d INNER JOIN(\tSELECT id,choosename,0 orderNum,d.orderNum orderNum1 FROM define d where d.id in ({newGroupNum[]}) UNION\tSELECT sd.id,d.choosename,sd.orderNum,d.orderNum orderNum1 from define d LEFT JOIN subdefine sd on d.id=sd.pid where sd.id in ({newGroupNum[]}) )d1 on d.id=d1.choosename where examPaperNum={examPaperNum} and d.choosename='T' GROUP BY d.id union SELECT d.id,d.questionNum,d.orderNum orderNum1 FROM define d where d.id in ({newGroupNum[]})  UNION SELECT sd.id,sd.questionNum,sd.orderNum orderNum1 FROM subdefine sd LEFT JOIN define d on sd.pid=d.id where sd.id in ({newGroupNum[]}) )d on  qu.groupNum=d.id and qu1.groupNum=d.id ";
            orderStr = " length(qu.groupNum) desc,length(qu1.groupNum) desc,d.orderNum1, ";
        }
        String sql = "SELECT  DISTINCT u.id,u.realName,u.userName,qt.num ext1,if(p.fenzuyuejuan=1,sg.schoolGroupName,null) ext3,sch.schoolName ,ifnull(ay.status,'') ext2" + header + "  FROM questiongroup_user qgu  left join   user u     on u.id=qgu.userNum  left join quota qt on qt.insertUser=qgu.userNum and qgu.groupNum=qt.groupNum  LEFT JOIN school sch ON sch.id = u.schoolnum  LEFT JOIN schoolgroup sg on sg.schoolNum = sch.id  LEFT JOIN assistYuejuan ay on qgu.groupNum=ay.groupNum and qgu.userNum= ay.assister   LEFT JOIN exampaper p on qgu.exampaperNum=p.examPaperNum " + chooseDefine + " WHERE u.id=qgu.userNum and qgu.exampaperNum={examPaperNum}   and qgu.groupNum in ({newGroupNum[]})  ORDER BY IF(sg.schoolGroupName is null,1,0),convert(sg.schoolGroupName using gbk)," + orderStr + " IF(sch.schoolName is null,1,0),convert(sch.schoolName using gbk),  convert(u.realname using gbk)";
        return this.dao._queryBeanList(sql, User.class, args);
    }

    public Map querychooselist(QuestionGroup_user qgu) {
        String[] groupNumArr = qgu.getGroupNum().split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = new HashMap();
        args.put("newGroupNum", newGroupNum2);
        args.put("examPaperNum", qgu.getExampaperNum());
        return this.dao._queryOrderMap("SELECT GROUP_CONCAT(d1.id SEPARATOR '_') id,case when d1.orderNum=0 then d.questionNum else CONCAT(d.questionNum,'_',d1.orderNum)end questionNum  from define d  INNER JOIN(\tSELECT id,choosename,0 orderNum,d.orderNum orderNum1 FROM define d where d.id in ({newGroupNum[]}) UNION\tSELECT sd.id,d.choosename,sd.orderNum,d.orderNum orderNum1 from define d LEFT JOIN subdefine sd on d.id=sd.pid where sd.id in ({newGroupNum[]}) )d1 on cast(d.id as char)=d1.choosename where examPaperNum={examPaperNum} and d.choosename='T' GROUP BY d.id union SELECT d.id,d.questionNum FROM define d where d.id in ({newGroupNum[]})  UNION SELECT sd.id,sd.questionNum FROM subdefine sd LEFT JOIN define d on sd.pid=d.id where sd.id in ({newGroupNum[]}) ", TypeEnum.StringObject, args);
    }

    public Integer removeGroupUser(String examPaperNum, String groupNum, String userNum) {
        String[] groupNumArr = groupNum.split("_");
        for (String groupNum2 : groupNumArr) {
            Map args = new HashMap();
            args.put("groupNum", groupNum2);
            args.put("userNum", userNum);
            this.dao._execute("delete from quota where groupNum={groupNum}  and insertUser={userNum}", args);
            args.put("examPaperNum", examPaperNum);
            this.dao._execute("delete from QuestionGroup_user where examPaperNum = {examPaperNum}  and groupNum={groupNum} and userNum={userNum} ", args);
            this.log.info("【移除题组用户】考试--" + examPaperNum + "--题组【" + groupNum2 + "】--教师【" + userNum + "】");
        }
        return 1;
    }

    public Integer updateGroupUser_old(QuestionGroup_user qu, String oldgroupNum) {
        String insertDate = DateUtil.getCurrentTime();
        Map args = new HashMap();
        args.put("oldgroupNum", oldgroupNum);
        args.put("insertUser", qu.getUserNum());
        new ArrayList();
        List numList = this.dao._queryArrayList("select num from quota where groupNum = {oldgroupNum}  and insertUser = {insertUser} ", args);
        if (numList.size() != 0) {
            int num = ((Integer) numList.get(0)[0]).intValue();
            args.put("exampaperNum", qu.getExampaperNum());
            args.put("groupNum", qu.getGroupNum());
            args.put("num", Integer.valueOf(num));
            args.put("insertDate", insertDate);
            this.dao._execute("insert into  quota (exampaperNum,groupNum,num,insertUser,insertDate) values({exampaperNum},{groupNum},{num},{insertUser},{insertDate} ) ON DUPLICATE KEY UPDATE num={num} ", args);
            this.dao._execute("delete from quota where groupNum ={oldgroupNum} and insertUser ={insertUser} ", args);
        }
        deletetask(qu.getExampaperNum(), oldgroupNum, qu.getUserNum(), "1", "");
        this.dao._execute("update QuestionGroup_user set groupNum ={groupNum}  ,userType='0' where userNum = {insertUser}  and exampaperNum={exampaperNum}  and groupNum={oldgroupNum} ", args);
        int a = this.dao._execute("update assistYuejuan set groupNum ={groupNum} where assister ={insertUser} and exampaperNum={exampaperNum} and groupNum={oldgroupNum} ", args);
        return Integer.valueOf(a);
    }

    public Integer updateGroupUser(QuestionGroup_user qu, String oldgroupNum) {
        String[] oldgroupNumArr = oldgroupNum.split("_");
        String[] newgroupNumArr = qu.getGroupNum().split("_");
        String insertDate = DateUtil.getCurrentTime();
        Map args = new HashMap();
        args.put("userNum", qu.getUserNum());
        args.put("exampaperNum", qu.getExampaperNum());
        for (int j = 0; j < newgroupNumArr.length; j++) {
            args.put("groupNum", oldgroupNumArr[0]);
            String sql = "insert into   QuestionGroup_user (id,exampaperNum,groupNum,userType,userNum,insertUser,insertDate) select UUID_SHORT(),exampaperNum,'" + newgroupNumArr[j] + "',userType,userNum,insertUser,'" + insertDate + "' from QuestionGroup_user where userNum ={userNum}  and exampaperNum={exampaperNum}  and groupNum={groupNum} ";
            this.dao._execute(sql, args);
            String sql2 = "insert into   assistYuejuan (examPaperNum,examNum,gradeNum,subjectNum,groupNum,assister,updateUser,updateDate,status) select examPaperNum,examNum , gradeNum,subjectNum,'" + newgroupNumArr[j] + "',assister,updateUser,'" + insertDate + "',status from assistYuejuan where assister ={userNum}  and exampaperNum={exampaperNum} and groupNum={groupNum} ";
            this.dao._execute(sql2, args);
        }
        for (String str : oldgroupNumArr) {
            Map args2 = new HashMap();
            args2.put("oldgroupNumArr", str);
            args2.put("userNum", qu.getUserNum());
            args2.put("exampaperNum", qu.getExampaperNum());
            this.dao._execute("delete from quota where groupNum ={oldgroupNumArr} and insertUser ={userNum}  ", args2);
            String count = this.dao._queryStr("select ifnull(count(1),0) from task where groupNum={oldgroupNumArr} and insertUser={userNum} and status='T' ", args2);
            if (!"0".equals(count)) {
                args2.put("exampaperNum", qu.getExampaperNum());
                args2.put("count", count);
                args2.put("insertDate", insertDate);
                this.dao._execute("insert into  quota (exampaperNum,groupNum,num,insertUser,insertDate) values({exampaperNum},{oldgroupNumArr},{count},{userNum},{insertDate}) ON DUPLICATE KEY UPDATE num={count} ", args2);
            } else {
                this.dao._execute("delete from QuestionGroup_user where userNum ={userNum}  and exampaperNum={exampaperNum}  and groupNum={oldgroupNumArr} ", args2);
                this.dao._execute("delete from assistYuejuan where assister ={userNum}  and exampaperNum={exampaperNum}  and groupNum={oldgroupNumArr} ", args2);
            }
            List<?> _queryBeanList = this.dao._queryBeanList("select id from task where groupNum={oldgroupNumArr}  and insertUser={userNum}  and status='F' ", Task.class, args2);
            List<Map> argMapList = new ArrayList<>();
            for (int j2 = 0; j2 < _queryBeanList.size(); j2++) {
                String id = ((Task) _queryBeanList.get(j2)).getId();
                Map argss = new HashMap();
                argss.put("id", id);
                argMapList.add(argss);
            }
            this.dao._batchUpdate("update task set insertUser='-1',porder=0,fenfaDate=0 where id={id} ", argMapList);
            this.log4J.info("【移动老师回收】移动的老师--" + qu.getUserNum() + "--共回收了【" + _queryBeanList.size() + "】条");
        }
        return 1;
    }

    public void questionLeader(String examPaperNum, String groupNum, String userNum, String usertype, String loginUserId) {
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = new HashMap();
        args.put("newGroupNum", newGroupNum2);
        args.put("userNum", userNum);
        if (groupNumArr.length > 1) {
            List<?> _queryBeanList = this.dao._queryBeanList("select groupNum from questiongroup_user where groupNum in ({newGroupNum[]}) and userNum={userNum} ", QuestionGroup.class, args);
            if (_queryBeanList.size() != groupNumArr.length) {
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("examPaperNum", examPaperNum);
                paramMap.put("type", "2");
                paramMap.put("userNumStr", userNum);
                paramMap.put("oldgroupNum", ((QuestionGroup) _queryBeanList.get(0)).getGroupNum());
                switchChoose(paramMap, loginUserId);
            }
        }
        List<Map> argMapList = new ArrayList<>();
        for (String groupNum2 : groupNumArr) {
            Map args2 = new HashMap();
            args2.put("usertype", usertype);
            args2.put("examPaperNum", examPaperNum);
            args2.put("groupNum", groupNum2);
            args2.put("userNum", userNum);
            argMapList.add(args2);
        }
        this.dao._batchUpdate("update QuestionGroup_user set userType={usertype}  where examPaperNum ={examPaperNum}  and groupNum={groupNum}  and userNum={userNum} ", argMapList);
        args.put("ROLE_TIZUZHANG", Const.ROLE_TIZUZHANG);
        Object userroleId = this.dao._queryObject("select id from userrole where userNum={userNum} and roleNum={ROLE_TIZUZHANG} ", args);
        if (null == userroleId) {
            if ("1".equals(usertype)) {
                String currentTime = DateUtil.getCurrentTime();
                args.put("loginUserId", loginUserId);
                args.put("currentTime", currentTime);
                this.dao._execute("insert into userrole (userNum,roleNum,insertUser,insertDate,isDelete) values ({userNum},{ROLE_TIZUZHANG},{loginUserId},{currentTime},'F')", args);
                return;
            }
            return;
        }
        if ("0".equals(usertype)) {
            args.put("WH_TIZZ", "1");
            if (null == this.dao._queryObject("select /* shard_host_HG=Write */ id from questiongroup_user where userNum={userNum}  and userType={WH_TIZZ} limit 1", args)) {
                this.dao._execute("delete from userrole where userNum={userNum} and roleNum={ROLE_TIZUZHANG}", args);
            }
        }
    }

    public QuestionGroup_user querySubjectLeader(String examPaperNum, String userType) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("userType", userType);
        return (QuestionGroup_user) this.dao._queryBean("select qu.*,u.userName userName,u.realName realName from QuestionGroup_user qu,user u  where qu.userNum=u.id and examPaperNum={examPaperNum}  and qu.userType={userType} ", QuestionGroup_user.class, args);
    }

    public List queryQuestionLeader(String examPaperNum, String groupNum, String userNum, String type) {
        String authStr = "";
        if ("authority".equals(type)) {
            authStr = authStr + "INNER JOIN(  SELECT schoolnum from user where id={userNum}  UNION   SELECT schoolnum from schauthormanage where userId={userNum} )sa on u.schoolnum=sa.schoolnum";
        }
        String sql = "select qu.*,u.realName userName from QuestionGroup_user qu,user u " + authStr + " where qu.userNum=u.id and examPaperNum={examPaperNum} and groupNum={groupNum}  and qu.userType='1' ";
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        return this.dao._queryBeanList(sql, QuestionGroup_user.class, args);
    }

    public Integer insertQuestiongroup_user(String examPaperNum, String groupNum, String userNum) {
        String sql = "insert into questiongroup_user (id,exampaperNum,groupNum,userType,userNum,insertUser,insertDate,appendTeacher,isFinished)    SELECT UUID_SHORT()," + examPaperNum + ",d.id,0," + userNum + Const.STRING_SEPERATOR + userNum + ",now(),1,0 from(    SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={groupNum}  and d.choosename<>'s'  union     SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename    LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum    where sb.id={groupNum} and d.choosename<>'s'  )d LEFT JOIN questiongroup_user qu on d.id=qu.groupNum and qu.exampaperNum={examPaperNum}  and qu.userNum={userNum}    where qu.id is null    ";
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("examPaperNum", examPaperNum);
        args.put("userNum", userNum);
        Integer str = Integer.valueOf(this.dao._execute(sql, args));
        this.dao._execute("INSERT INTO quota(examPaperNum,groupNum,num,insertUser,insertDate)  SELECT qo.exampaperNum,d.id,qo.num,qo.insertUser,now() FROM questiongroup_user qu INNER JOIN quota qo on qu.groupNum=qo.groupNum and qu.userNum=qo.insertUser  LEFT JOIN(    SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={groupNum} and d.choosename<>'s'    union         SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename        LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum        where sb.id={groupNum} and d.choosename<>'s'  )d on 1=1   LEFT JOIN quota qt on d.id=qt.groupNum and qt.exampaperNum={examPaperNum}  and qt.insertUser={userNum}    where qu.groupNum={groupNum} and qu.userNum={userNum} and qt.id is null    ", args);
        return str;
    }

    public String switchChoose(Map<String, String> paramMap, String insertUser) {
        String examPaperNum = paramMap.get("examPaperNum");
        String type = paramMap.get("type");
        String userNum = paramMap.get("userNumStr");
        String oldgroupNum = paramMap.get("oldgroupNum");
        String newgroupNum = paramMap.get("newgroupNum");
        String str = "";
        Map args = new HashMap();
        args.put("newgroupNum", newgroupNum);
        args.put("examPaperNum", examPaperNum);
        args.put("userNum", userNum);
        if ("0".equals(type)) {
            Map<String, String> map = this.dao._queryOrderMap("SELECT qu.groupName,cast(ifnull(count(1),0) as char) from task t      left join questiongroup qu on t.groupNum=qu.groupNum INNER join(       SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={newgroupNum}  and d1.id<>{newgroupNum}  union        SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename       LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum       where sb.id={newgroupNum}  and sbb.id<>{newgroupNum}   )d on t.groupNum=d.id and t.exampaperNum={examPaperNum} and t.insertUser={userNum}  and t.`status`='T'     GROUP BY t.groupNum ", TypeEnum.StringString, args);
            if (map.size() > 0) {
                for (String key : map.keySet()) {
                    if (key != null) {
                        str = key + "题有" + map.get(key) + "题，";
                    }
                }
                str = str + "已判，请在题组进度里先重置该教师的判分";
            } else {
                String otherGroupNum = this.dao._queryStr("SELECT GROUP_CONCAT(d.id) FROM(   SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={newgroupNum} and d1.id<>{newgroupNum}  union    SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename   LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum   where sb.id={newgroupNum} and sbb.id<> {newgroupNum}   )d", args);
                args.put("otherGroupNum", otherGroupNum);
                str = this.dao._execute("delete from questiongroup_user where examPaperNum={examPaperNum} and groupNum in ({otherGroupNum[]}) and userNum={userNum}", args) + "";
                this.dao._execute("delete from quota where examPaperNum={examPaperNum} and groupNum in ({otherGroupNum[]}) and insertUser={userNum} ", args);
                this.dao._execute("delete from assistyuejuan where groupNum in ({otherGroupNum[]}) and assister={userNum}", args);
                this.dao._execute("update questiongroup_user set userType = 0 where examPaperNum={examPaperNum}  and groupNum in ({otherGroupNum[]}) and userNum={userNum}", args);
                this.dao._execute("delete from userrole where roleNum='-4' and userNum={userNum} ", args);
                this.dao._execute("update task set insertUser='-1',porder=0,fenfaDate=0 where examPaperNum={examPaperNum}  and groupNum in ({otherGroupNum[]}) and insertUser={userNum} and status='F'", args);
            }
        } else if ("1".equals(type)) {
            Map<String, String> map2 = this.dao._queryOrderMap("select qu.groupName,cast(ifnull(count(1),0) as char) from task t left join questiongroup qu on t.groupNum=qu.groupNum where t.examPaperNum={examPaperNum}  and t.groupNum ={oldgroupNum} and t.insertUser={userNum} and t.status='T' GROUP BY t.groupNum", TypeEnum.StringString, args);
            if (map2.size() > 0) {
                for (String key2 : map2.keySet()) {
                    if (key2 != null) {
                        str = key2 + "题有" + map2.get(key2) + "题已判，请在题组进度里先重置该教师的判分";
                    }
                }
            } else {
                args.put("oldgroupNum", oldgroupNum);
                str = this.dao._execute("update questiongroup_user set groupNum={newgroupNum} where exampaperNum={examPaperNum} and groupNum={oldgroupNum} and userNum={userNum}", args) + "";
                this.dao._execute("update assistyuejuan set groupNum={newgroupNum} where groupNum={oldgroupNum}  and assister={userNum}", args);
                this.dao._execute("update quota set groupNum={newgroupNum} where exampaperNum={examPaperNum} and groupNum={oldgroupNum} and insertUser={userNum} ", args);
                this.dao._execute("update task set insertUser='-1',porder=0,fenfaDate=0 where examPaperNum={examPaperNum}and groupNum ={oldgroupNum}  and insertUser={userNum} and status='F'", args);
            }
        } else if ("2".equals(type)) {
            String sql = "insert into questiongroup_user (id,exampaperNum,groupNum,userType,userNum,insertUser,insertDate,isFinished)    SELECT UUID_SHORT()," + examPaperNum + ",d.id,0," + userNum + Const.STRING_SEPERATOR + insertUser + ",now(),0 from(    SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={oldgroupNum}  union     SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename    LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum    where sb.id={oldgroupNum}  )d LEFT JOIN questiongroup_user qu on d.id=qu.groupNum and qu.exampaperNum={examPaperNum} and qu.userNum={userNum}   where qu.id is null    ";
            args.put("oldgroupNum", oldgroupNum);
            str = this.dao._execute(sql, args) + "";
            this.dao._execute("INSERT INTO quota(examPaperNum,groupNum,num,insertUser,insertDate)  SELECT qo.exampaperNum,d.id,qo.num,qo.insertUser,now() FROM questiongroup_user qu INNER JOIN quota qo on qu.groupNum=qo.groupNum and qu.userNum=qo.insertUser  LEFT JOIN(    SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={oldgroupNum}    union         SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename        LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum        where sb.id={oldgroupNum}  )d on 1=1   LEFT JOIN quota qt on d.id=qt.groupNum and qt.exampaperNum={examPaperNum}  and qt.insertUser={userNum}     where qu.groupNum={oldgroupNum}  and qu.userNum={userNum}  and qt.id is null   ", args);
            this.dao._execute(" INSERT INTO assistyuejuan(examPaperNum,examNum,gradeNum,subjectNum,groupNum,assister,updateUser,updateDate, status)  SELECT ay.exampaperNum,ay.examNum,ay.gradeNum,ay.subjectNum,d.id,ay.assister,ay.updateUser,now(),ay.status FROM questiongroup_user qu INNER JOIN assistyuejuan ay on qu.groupNum=ay.groupNum and qu.userNum=ay.assister LEFT JOIN(   SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={oldgroupNum}    union        SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename       LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum       where sb.id={oldgroupNum} )d on 1=1  LEFT JOIN assistyuejuan qt on d.id=qt.groupNum and qt.exampaperNum={examPaperNum}  and qt.assister={userNum}    where qu.groupNum={oldgroupNum}  and qu.userNum={userNum}  and qt.id is null    ", args);
        }
        return str;
    }

    public List fetchTiNum(Integer examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return this.dao._queryBeanList("SELECT q.groupNum,q.groupName,q.exampaperNum,q.groupType,  case   when d.choosename<>'s' and qe.makType=1 then IFNULL(ch.allTotalNum*2,0)+IFNULL(x.count,0)   when d.choosename<>'s' and qe.makType=0 then IFNULL(ch.allTotalNum,0)    when d.choosename='s' and qe.makType=1 then IFNULL(r.count*2,0)+IFNULL(x.count,0)    when d.choosename='s' and qe.makType=0 then IFNULL(r.count,0)   end count2 FROM(  SELECT groupNum,groupName,exampaperNum,groupType FROM questiongroup WHERE exampaperNum={examPaperNum}    )q LEFT JOIN (  SELECT groupNum,makType,case when isnull(threeWarn) then 0 else threeWarn END threeWarn from questiongroup_mark_setting where exampaperNum={examPaperNum}  )qe on q.groupNum=qe.groupNum  LEFT JOIN (  select d.id,count(DISTINCT r.studentId) count from(  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d INNER JOIN exampaper e on d.category=e.examPaperNum where d.examPaperNum={examPaperNum} and d.questionType=1   \t\t\tUNION  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from subdefine d INNER JOIN exampaper e on d.category=e.examPaperNum where d.examPaperNum={examPaperNum} and d.questionType=1  \t\t)d LEFT JOIN(  \t\t\tSELECT DISTINCT studentId,exampapernum ext1 from regexaminee WHERE exampaperNum={examPaperNum}  and scan_import=0 \t\t\tunion \t\t\tSELECT DISTINCT r.studentId,e1.exampapernum from regexaminee r \t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum \t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE r.exampaperNum={examPaperNum}   and r.scan_import=0 \t\t\tUNION\t \t\t\tSELECT DISTINCT r.studentId,e1.exampaperNum from regexaminee r LEFT JOIN( \t\t\t\tSELECT r.studentId,r.exampapernum from regexaminee r \t\t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum \t\t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE r.exampaperNum={examPaperNum}  and r.scan_import=0 \t\t\t)r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum \t\t\tINNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3 \t\t\tWHERE r.exampaperNum={examPaperNum}   and r.scan_import=0 and r1.studentId is null  \t\t)r on d.category=r.ext1 GROUP BY d.id  ) r on q.groupNum=r.id  LEFT JOIN (  \t\t\tSELECT groupNum,count(1) count FROM task   WHERE exampaperNum={examPaperNum} and userNum=3  GROUP BY groupNum    )x ON q.groupNum = x.groupNum  LEFT JOIN ( \t SELECT id,choosename from define WHERE exampaperNum={examPaperNum} \t  UNION \t  SELECT sb.id,d.choosename from define d  \t  LEFT JOIN subdefine sb on sb.pid=d.id \t  where d.examPaperNum={examPaperNum} \t) d on q.groupNum=d.id LEFT JOIN choosescale ch on q.groupNum=ch.groupNum  GROUP BY q.groupNum ORDER BY CASE  WHEN LOCATE('-',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('-' IN q.groupName)-1),'.01')*1   ELSE CONCAT(q.groupName,'.1')*1 END  ASC ,REPLACE(q.groupName,'_','.')*1", AwardPoint.class, args);
    }

    public Map<String, Map<String, String>> fetchTiNumchoose(String[] qNumArr) {
        Map<String, Map<String, String>> returnMap = new HashMap<>();
        for (int k = 0; k < qNumArr.length; k++) {
            String[] groupNumArr = qNumArr[k].split("_");
            String newGroupNum = "";
            for (String str : groupNumArr) {
                newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
            }
            String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
            Map args = new HashMap();
            args.put("newGroupNum", newGroupNum2);
            returnMap.put(qNumArr[k], this.dao._queryOrderMap("SELECT q.groupName,ifnull(count(DISTINCT t.studentId),0) from task t LEFT JOIN questiongroup q on t.groupNum=q.groupNum where t.groupNum in ({newGroupNum[]}) GROUP BY t.groupNum", TypeEnum.StringString, args));
        }
        return returnMap;
    }

    public Integer updateMarkType(String examPaperNum, String groupNum, String makType, String makType2, String qmsType, String groupType, String loginNum) {
        if (!qmsType.equals("3")) {
            String[] groupNumArr = groupNum.split("_");
            List<Map> argMapList = new ArrayList<>();
            for (String groupNum2 : groupNumArr) {
                Map args = new HashMap();
                args.put("makType", makType);
                args.put("examPaperNum", examPaperNum);
                args.put("groupNum", groupNum2);
                args.put("qmsType", qmsType);
                argMapList.add(args);
                this.dao._execute("update questiongroup_mark_setting set makType={makType} where examPaperNum={examPaperNum}  and groupNum={groupNum}  and Type={qmsType} ", args);
                this.log4J.info("【单双评切换单个-questiongroup_mark_setting】--userId:" + loginNum + "--examPaperNum:" + examPaperNum + "--groupNum" + groupNum2 + "--makType" + makType + "--Type" + qmsType);
            }
            this.dao._batchUpdate("update questiongroup_mark_setting set makType={makType} where examPaperNum={examPaperNum}  and groupNum={groupNum}  and Type={qmsType} ", argMapList);
        } else {
            Map args2 = new HashMap();
            args2.put("makType2", makType2);
            args2.put("examPaperNum", examPaperNum);
            this.dao._execute("update questiongroup_mark_setting qms inner JOIN questiongroup qp on qms.groupNum=qp.groupNum set qms.makType={makType2}  where qms.examPaperNum={examPaperNum}  and qms.type=1 ", args2);
            this.log4J.info("【单双评切换全部-questiongroup_mark_setting】--userId:" + loginNum + "--examPaperNum:" + examPaperNum + "--makType" + makType);
        }
        return 0;
    }

    public Integer updateAssess(String examPaperNum, String groupNum, String makType, String makType2, String qmsType, String groupType, String loginNum) {
        SingleDoubleMarkingCache.remove(examPaperNum);
        try {
            Thread.currentThread();
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!qmsType.equals("3")) {
            String[] groupNumArr = groupNum.split("_");
            for (String groupNum2 : groupNumArr) {
                Map args = new HashMap();
                args.put("groupNum", groupNum2);
                if (makType.equals("0")) {
                    this.log4J.info("【单双评切换】--userId:" + loginNum + "--examPaperNum:" + examPaperNum + "--groupNum" + groupNum2 + "--从多评到单评");
                    this.dao._execute("DELETE  FROM task   where groupNum={groupNum}  and userNum='3' ", args);
                    this.dao._execute("DELETE t FROM task as t INNER JOIN(  SELECT id,count(1) count from (SELECT scoreid,id from task WHERE groupNum={groupNum} ORDER BY scoreId,`status`) t GROUP BY scoreid having count>1 ) t2  on t.id = t2.id where t.groupNum={groupNum} ", args);
                    args.put("examPaperNum", examPaperNum);
                    this.dao._execute("update task t set userNum='1' where examPaperNum={examPaperNum} and groupNum={groupNum} ", args);
                    this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and groupNum={groupNum} and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
                    this.dao._execute("delete from remark where examPaperNum={examPaperNum}  and questionNum={groupNum} ", args);
                } else if (makType.equals("1")) {
                    this.log4J.info("【单双评切换】--userId:" + loginNum + "--examPaperNum:" + examPaperNum + "--groupNum" + groupNum2 + "--从单评到多评");
                    args.put("examPaperNum", examPaperNum);
                    this.dao._execute(" INSERT INTO task(id,scoreId,examPaperNum,questionNum,insertDate,updateTime,isException,isDelete ,status ,groupNum, studentId,insertuser,testingCentreId,userNum)SELECT r.*,'2' FROM  \t(  SELECT  UUID_SHORT() id,scoreId,examPaperNum,questionNum,insertDate,'' updateTime,'F' isException, isDelete ,'F' status,groupNum,studentId,-1 insertuser,testingCentreId  from (select *,count(1) count from task  where examPaperNum={examPaperNum}   and groupNum={groupNum}  GROUP BY scoreId having count=1 )t   )r  ", args);
                }
            }
        } else {
            Map args2 = new HashMap();
            args2.put("examPaperNum", examPaperNum);
            List<QuestionGroup_question> countgrouplist = this.dao._queryBeanList(" SELECT  qq.id,qq.questionNum,qq.exampaperNum,qq.groupNum,qm.makType FROM questiongroup_question   qq LEFT JOIN questiongroup_mark_setting qm on qq.groupNum=qm.groupNum  LEFT JOIN questiongroup qp on qq.groupNum=qp.groupNum  WHERE qq.exampaperNum={examPaperNum} group by qq.groupNum order by length(qq.questionNum),qq.questionNum", QuestionGroup_question.class, args2);
            if (countgrouplist.size() != 0) {
                for (QuestionGroup_question qq : countgrouplist) {
                    Map args22 = new HashMap();
                    args22.put("groupNum", qq.getGroupNum());
                    args22.put("examPaperNum", qq.getExampaperNum());
                    if (makType2.equals("0")) {
                        this.log4J.info("【单双评切换】--userId:" + loginNum + "--examPaperNum:" + examPaperNum + "--groupNum" + groupNum + "--从多评到单评");
                        if ("0".equals(qq.getMakType())) {
                            this.dao._execute("DELETE  FROM task   where groupNum={groupNum} and userNum='3' ", args22);
                            this.dao._execute("DELETE t FROM task as t INNER JOIN(  SELECT id,count(1) count from (SELECT scoreid,id from task WHERE groupNum={groupNum} ORDER BY scoreId,`status`) t GROUP BY scoreid having count > 1 ) t2  on t.id = t2.id where t.groupNum={groupNum}  ", args22);
                            this.dao._execute("update task t set userNum='1' where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args22);
                            this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and groupNum={groupNum}  and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args22);
                            this.dao._execute("delete from remark where examPaperNum={examPaperNum}  and questionNum={questionNum} ", args22);
                        }
                    } else if (makType2.equals("1")) {
                        this.log4J.info("【单双评切换】--userId:" + loginNum + "--examPaperNum:" + examPaperNum + "--groupNum" + groupNum + "--从单评到多评");
                        if ("1".equals(qq.getMakType())) {
                            this.dao._execute(" INSERT INTO task(id,scoreId,examPaperNum,questionNum,insertDate,updateTime,isException,isDelete ,status ,groupNum, studentId,insertuser,testingCentreId,userNum)SELECT r.*,'2' FROM  \t(  SELECT  UUID_SHORT() id,scoreId,examPaperNum,questionNum,insertDate,'' updateTime,'F' isException, isDelete ,'F' status,groupNum,studentId,-1 insertuser,testingCentreId  from (select *,count(1) count from task  where examPaperNum={examPaperNum} and groupNum={groupNum}  GROUP BY scoreId having count=1)t  )r  ", args22);
                        }
                    }
                }
            }
        }
        return 0;
    }

    public Integer createqms(Integer examPaperNum, String groupNum, String qmsType, String loginName) {
        Questiongroup_mark_setting qms = new Questiongroup_mark_setting();
        qms.setId(GUID.getGUIDStr());
        qms.setExampaperNum(examPaperNum);
        qms.setGroupNum(groupNum);
        qms.setMakType("0");
        qms.setType("1");
        qms.setInsertUser(loginName);
        qms.setInsertDate(DateUtil.getCurrentDay());
        return Integer.valueOf(this.dao.save(qms));
    }

    public List queryQuestionLeader2(String examPaperNum, String groupNum, String userNum, String type) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        if ("authority".equals(type)) {
            String str = "INNER JOIN(  SELECT schoolnum from user where id={userNum}  UNION   SELECT schoolnum from schauthormanage where userId={userNum} )sa on u.schoolnum=sa.schoolnum";
        }
        return this.dao._queryBeanList("select qu.*,u.realName userName from QuestionGroup_user qu,user u  where qu.userNum=u.id and examPaperNum={examPaperNum} and groupNum={groupNum}   and qu.userType='1' ", QuestionGroup_user.class, args);
    }

    public List queryQuestionLeader2ByGroup(String examPaperNum, String groupNum, String schoolGroupNum) {
        String sql;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        args.put("schoolGroupNum", schoolGroupNum);
        if ("-2".equals(schoolGroupNum)) {
            sql = "select qu.*,u.realName userName from questiongroup_user qu inner join user u on u.id=qu.userNum where qu.userNum=u.id and examPaperNum={examPaperNum} and groupNum={groupNum} and qu.userType='1' ";
        } else {
            String sgWStr = "-1".equals(schoolGroupNum) ? " and sg.schoolGroupNum is null " : " and sg.schoolGroupNum={schoolGroupNum} ";
            sql = "select qu.*,u.realName userName from questiongroup_user qu inner join user u on u.id=qu.userNum left join schoolgroup sg on sg.schoolNum=u.schoolnum where qu.userNum=u.id and examPaperNum={examPaperNum} and groupNum={groupNum} and qu.userType='1' " + sgWStr;
        }
        return this.dao._queryBeanList(sql, QuestionGroup_user.class, args);
    }

    public String queryMoveOne(Integer examPaperNum, String groupNum, String userNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        args.put("userNum", userNum);
        String count = this.dao._queryStr("select count(1) from QuestionGroup_user where examPaperNum={examPaperNum}  and groupNum={groupNum}  and userNum={userNum} ", args);
        return count;
    }

    public String getGroupCount(String examPaperNum, String tab) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        String sql = null;
        if (tab.equals("1")) {
            sql = "select count(1) from QuestionGroup where examPaperNum={examPaperNum} and groupType='0'";
        } else if (tab.equals("2")) {
            sql = "select count(1) from QuestionGroup where examPaperNum={examPaperNum} ";
        }
        String count = this.dao._queryStr(sql, args);
        return count;
    }

    public String getGroupNumber(String examPaperNum, String groupNum) {
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = new HashMap();
        args.put("newGroupNum", newGroupNum2);
        args.put("examPaperNum", examPaperNum);
        String count = this.dao._queryStr("select ifnull(count(1),0) from QuestionGroup where examPaperNum={examPaperNum}  and groupNum not in ({newGroupNum[]}) ", args);
        return count;
    }

    public String getTaskNumber(String examPaperNum, String groupNum, String userId) {
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = new HashMap();
        args.put("newGroupNum", newGroupNum2);
        args.put("examPaperNum", examPaperNum);
        args.put("userId", userId);
        String count = this.dao._queryStr("select ifnull(count(1),0) from task where examPaperNum={examPaperNum} and groupNum in ({newGroupNum[]}) and insertUser={userId} and status='T' ", args);
        return count;
    }

    public Integer createGroupfz(Integer examPaperNum, String questionNum, String questionName, String loginName, String groupType) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        String merge = "";
        if (this.dao._queryObject("select /* shard_host_HG=Write */  pid from subdefine where id={questionNum} ", args) != null) {
            String pid = this.dao._queryStr("select /* shard_host_HG=Write */  pid from subdefine where id={questionNum} ", args);
            args.put("pid", pid);
            merge = this.dao._queryStr("select /* shard_host_HG=Write */ `merge` from define where id={pid} ", args);
        }
        if (merge.equals("1")) {
            return 0;
        }
        QuestionGroup qg = new QuestionGroup();
        qg.setExampaperNum(examPaperNum);
        qg.setGroupNum(questionNum);
        qg.setGroupName(questionName);
        qg.setInsertUser(loginName);
        qg.setInsertDate(DateUtil.getCurrentDay());
        String questionGroup = Configuration.getInstance().getQuestionGroup();
        qg.setStat(questionGroup);
        if ("1".equals(questionGroup)) {
            qg.setScancompleted("1");
        }
        if (groupType != null && !groupType.equals("1")) {
            qg.setGroupType("0");
        } else {
            qg.setGroupType("1");
        }
        Integer i = Integer.valueOf(this.dao.save(qg));
        createGroupfz_question(examPaperNum, questionNum, loginName);
        createGroupfz_mark_setting(examPaperNum, questionNum, loginName);
        return i;
    }

    public Integer createGroupfz_question(Integer examPaperNum, String questionNum, String loginName) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        String pid = "";
        String merge = "";
        String groupNum = questionNum;
        if (this.dao._queryObject("select /* shard_host_HG=Write */ pid from subdefine where id={questionNum} ", args) != null) {
            pid = this.dao._queryStr("select /* shard_host_HG=Write */ pid from subdefine where id={questionNum} ", args);
            args.put("pid", pid);
            merge = this.dao._queryStr("select /* shard_host_HG=Write */ `merge` from define where id={pid} ", args);
        }
        if (merge.equals("1")) {
            groupNum = pid;
        }
        QuestionGroup_question qgq = new QuestionGroup_question();
        qgq.setId(GUID.getGUIDStr());
        qgq.setExampaperNum(examPaperNum);
        qgq.setGroupNum(groupNum);
        qgq.setQuestionNum(questionNum);
        qgq.setInsertUser(loginName);
        qgq.setInsertDate(DateUtil.getCurrentDay());
        return Integer.valueOf(this.dao.save(qgq));
    }

    public Integer createGroupfz_mark_setting(Integer examPaperNum, String questionNum, String loginName) {
        Questiongroup_mark_setting qms = new Questiongroup_mark_setting();
        qms.setId(GUID.getGUIDStr());
        qms.setExampaperNum(examPaperNum);
        qms.setGroupNum(questionNum);
        qms.setMakType("0");
        qms.setType("1");
        qms.setInsertUser(loginName);
        qms.setInsertDate(DateUtil.getCurrentDay());
        qms.setJudgetype("1");
        return Integer.valueOf(this.dao.save(qms));
    }

    public Integer deleteGroupfz(Integer examPaperNum, String questionNum, String type, String groupType, String shuang) {
        String sql;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        if (type.equals("-1")) {
            sql = "delete from questionGroup  where  exampaperNum={examPaperNum} ";
        } else if (groupType != null && !groupType.equals("1")) {
            sql = "delete from questionGroup  where  exampaperNum={examPaperNum}  and groupNum={questionNum}  and groupType='0' ";
        } else {
            sql = "delete from questionGroup  where  exampaperNum={examPaperNum}  and groupNum={questionNum} and groupType='1' ";
        }
        deletetask(examPaperNum, questionNum, null, type, shuang);
        deleteGroupfz_mark_setting(examPaperNum, questionNum, type);
        deleteGroupfz_question(examPaperNum, questionNum, type);
        deleteGroupfz_user(examPaperNum, questionNum, type, null);
        Integer i = Integer.valueOf(this.dao._execute(sql, args));
        return i;
    }

    public Integer deleteGroupfz_question(Integer examPaperNum, String questionNum, String type) {
        String sql;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        if (type.equals("-1")) {
            sql = "delete from QuestionGroup_question  where examPaperNum ={examPaperNum} ";
        } else if (type.equals("0")) {
            sql = "delete from QuestionGroup_question  where examPaperNum ={examPaperNum} and questionNum={questionNum} ";
        } else {
            sql = "delete from QuestionGroup_question  where examPaperNum ={examPaperNum}  and groupNum={questionNum} ";
        }
        return Integer.valueOf(this.dao._execute(sql, args));
    }

    public Integer deleteGroupfz_mark_setting(Integer examPaperNum, String questionNum, String type) {
        String sql;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        if (type.equals("-1")) {
            sql = "delete from QuestionGroup_mark_setting where examPaperNum={examPaperNum} ";
        } else {
            sql = "delete from QuestionGroup_mark_setting where examPaperNum={examPaperNum} and groupNum={questionNum} ";
        }
        return Integer.valueOf(this.dao._execute(sql, args));
    }

    public Integer deleteGroupfz_user(Integer examPaperNum, String questionNum, String type, String userNum) {
        String sql;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("userNum", userNum);
        if (type.equals("-1")) {
            sql = "delete from QuestionGroup_user where examPaperNum={examPaperNum} ";
        } else if (type.equals("0")) {
            sql = "delete from QuestionGroup_user where examPaperNum={examPaperNum} and groupNum={questionNum} ";
        } else if (type.equals("1")) {
            sql = "delete from QuestionGroup_user where examPaperNum={examPaperNum} and userNum={userNum} ";
        } else if (type.equals("user")) {
            sql = "delete from QuestionGroup_user where  userNum={userNum} ";
        } else {
            sql = "delete from QuestionGroup_user where examPaperNum={examPaperNum} and groupNum={questionNum} ";
        }
        return Integer.valueOf(this.dao._execute(sql, args));
    }

    public void deletetask(Integer examPaperNum, String questionNum, String userNum, String type, String shuang) {
        ServletContext context = ServletActionContext.getServletContext();
        this.request = ServletActionContext.getRequest();
        String loginId = new CommonUtil().getLoginUserNum(this.request);
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        if (type.equals("-1")) {
            delTaskById(examPaperNum, null, null);
            if ("shuang".equals(shuang)) {
                if (null != this.dao._queryObject("select /* shard_host_HG=Write */ id from task where examPaperNum={examPaperNum}  and usernum!='1' limit 1", args)) {
                    this.dao._execute(" delete from task where examPaperNum={examPaperNum}  and usernum!='1' ", args);
                }
                if (null != this.dao._queryObject("select /* shard_host_HG=Write */ s.id from score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}   and usernum='1' and status='T')t on s.id=t.scoreid where s.id=t.scoreid limit 1", args)) {
                    this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and status='T'  and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
                }
                if (null != this.dao._queryObject("select /* shard_host_HG=Write */ id from remark where examPaperNum={examPaperNum} limit 1", args)) {
                    this.dao._execute("delete from remark where examPaperNum={examPaperNum} ", args);
                }
                if (null != this.dao._queryObject("select /* shard_host_HG=Write */ id from task where exampaperNum={examPaperNum}  limit 1", args)) {
                    this.dao._execute("update  task set groupnum=questionnum where exampaperNum={examPaperNum} ", args);
                }
                updatequestiontotalnum(examPaperNum, null, null, "", "2");
            }
            addLog(this.request.getRequestURI(), "双向细目表导入调用回收task", loginId, this.request.getRemoteAddr());
            return;
        }
        args.put("questionNum", questionNum);
        Iterator it = this.dao._queryBeanList("SELECT /* shard_host_HG=Write */ examPaperNum,questionNum,groupNum from questiongroup_question where  examPaperNum ={examPaperNum} and groupNum={questionNum} ", QuestionGroup_question.class, args).iterator();
        if ((examPaperNum == null || examPaperNum.equals("")) && ((questionNum == null || questionNum.equals("")) && (userNum != null || !userNum.equals("")))) {
            addLog(this.request.getRequestURI(), "退出登录调用回收task", loginId, this.request.getRemoteAddr());
            return;
        }
        if ((examPaperNum != null || !examPaperNum.equals("")) && ((questionNum == null || questionNum.equals("")) && (userNum != null || !userNum.equals("")))) {
            args.put("userNum", userNum);
            List groupNumlist = this.dao._queryColList("select /* shard_host_HG=Write */ groupNum from questiongroup_user where exampaperNum={examPaperNum} and userNum={userNum}  ", args);
            for (int i = 0; i < groupNumlist.size(); i++) {
                String querygroupNumstr = String.valueOf(groupNumlist.get(i));
                Map args2 = new HashMap();
                args2.put("userNum", userNum);
                args2.put("examPaperNum", examPaperNum);
                if (querygroupNumstr != null && !querygroupNumstr.equals("null")) {
                    args2.put("querygroupNumint", querygroupNumstr);
                    deleteGroupfz_user(examPaperNum, querygroupNumstr, "1", userNum);
                    if (null != this.dao._queryObject("select /* shard_host_HG=Write */ id from quota where groupNum={querygroupNumint}  and insertUser={userNum}  limit 1", args2)) {
                        args2.put("querygroupNumint", querygroupNumstr);
                        this.dao._execute("delete from quota where groupNum={querygroupNumint}  and insertUser={userNum} ", args2);
                    }
                    delAssisterById(examPaperNum, querygroupNumstr, userNum);
                    if (null != this.dao._queryObject("select /* shard_host_HG=Write */ id from task where exampaperNum={examPaperNum}  and groupNum={querygroupNumint}  and insertUser={userNum}  and status='F' limit 1", args2)) {
                        this.dao._execute("update  task set insertUser='-1' where exampaperNum={examPaperNum}  and groupNum={querygroupNumint}  and insertUser={userNum}  and status='F'", args2);
                    }
                    context.removeAttribute("groupstucount" + querygroupNumstr);
                    addLog(this.request.getRequestURI(), "题组管理人员设置调用回收task", loginId, this.request.getRemoteAddr());
                }
            }
            return;
        }
        if ((examPaperNum != null || !examPaperNum.equals("")) && ((questionNum != null || !questionNum.equals("")) && (userNum == null || userNum.equals("")))) {
            while (it.hasNext()) {
                QuestionGroup_question qgq2 = (QuestionGroup_question) it.next();
                delTaskById(examPaperNum, qgq2.getQuestionNum(), null);
                context.removeAttribute("groupstucount" + qgq2.getGroupNum());
                context.removeAttribute("groupstucount" + qgq2.getQuestionNum());
            }
            if ("shuang".equals(shuang)) {
                if (null != this.dao._queryObject(" select /* shard_host_HG=Write */ id from task where examPaperNum={examPaperNum}  and questionNum={questionNum}  and usernum!='1' limit 1 ", args)) {
                    this.dao._execute(" delete from task where examPaperNum={examPaperNum}  and questionNum={questionNum}  and usernum!='1' ", args);
                }
                if (null != this.dao._queryObject("select /* shard_host_HG=Write */ s.id from score  s inner join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and groupNum={questionNum} and status='T' and usernum='1')t on s.id=t.scoreid ", args)) {
                    this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and groupNum={questionNum}  and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
                }
                if (null != this.dao._queryObject("select /* shard_host_HG=Write */ id from remark where examPaperNum={examPaperNum} and questionNum={questionNum}  limit 1", args)) {
                    this.dao._execute("delete from remark where examPaperNum={examPaperNum} and questionNum={questionNum} ", args);
                }
                if (null != this.dao._queryObject("select /* shard_host_HG=Write */ id from task where exampaperNum={examPaperNum} and questionNum={questionNum} limit 1", args)) {
                    this.dao._execute("update  task set groupnum={questionNum}  where exampaperNum={examPaperNum}  and questionNum={questionNum} ", args);
                }
                updatequestiontotalnum(examPaperNum, null, questionNum, "", "2");
                addLog(this.request.getRequestURI(), "双向细目表操作单个题调用回收task", loginId, this.request.getRemoteAddr());
                return;
            }
            return;
        }
        while (it.hasNext()) {
            QuestionGroup_question qgq22 = (QuestionGroup_question) it.next();
            delTaskById(examPaperNum, qgq22.getQuestionNum(), userNum);
            delAssisterById(examPaperNum, qgq22.getQuestionNum(), userNum);
            delQuota(examPaperNum, qgq22.getQuestionNum(), userNum);
            context.removeAttribute("groupstucount" + qgq22.getGroupNum());
        }
        addLog(this.request.getRequestURI(), "题组管理题组变更调用回收task", loginId, this.request.getRemoteAddr());
    }

    public void delTaskById(Integer examPaperNum, String questionNum, String userNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("userNum", userNum);
        String pstr = "";
        if (examPaperNum != null) {
            pstr = "examPaperNum={examPaperNum} and ";
        }
        String qstr = "";
        if (questionNum != null) {
            qstr = "questionNum={questionNum} and ";
        }
        String ustr = "";
        if (userNum != null) {
            ustr = "insertUser={userNum}  and ";
        }
        String sql = "select /* shard_host_HG=Write */ id from  task where " + pstr + qstr + ustr + " status='F'";
        List<Object> list = this.dao._queryColList(sql, args);
        List<Map> argMapList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (Object oo : list) {
                Map argss = new HashMap();
                argss.put("oo", oo);
                argMapList.add(argss);
            }
            this.dao._batchUpdate("update  task set insertUser='-1' where id ={oo} ", argMapList);
        }
    }

    public void delQuota(Integer examPaperNum, String questionNum, String userNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("userNum", userNum);
        String pstr = "";
        if (examPaperNum != null) {
            pstr = "examPaperNum={examPaperNum}  and ";
        }
        String qstr = "";
        if (questionNum != null) {
            qstr = "groupNum={questionNum}  and ";
        }
        String ustr = "";
        if (userNum != null) {
            ustr = "insertUser={userNum}  ";
        }
        String sql = "select /* shard_host_HG=Write */ id from  quota where " + pstr + qstr + ustr;
        List<Object> list = this.dao._queryColList(sql, args);
        List<Map> argMapList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (Object oo : list) {
                Map argss = new HashMap();
                argss.put("oo", oo);
                argMapList.add(argss);
            }
            this.dao._batchUpdate("delete from  quota  where id ={oo} ", argMapList);
        }
    }

    public void delAssisterById(Integer examPaperNum, String questionNum, String userNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("userNum", userNum);
        String pstr = "";
        if (examPaperNum != null) {
            pstr = "examPaperNum={examPaperNum} and ";
        }
        String qstr = "";
        if (questionNum != null) {
            qstr = "groupNum={questionNum}  and ";
        }
        String ustr = "";
        if (userNum != null) {
            ustr = "assister={userNum}  ";
        }
        String sql = "select /* shard_host_HG=Write */ id from  assistyuejuan where " + pstr + qstr + ustr;
        List<Object> list = this.dao._queryColList(sql, args);
        List<Map> argMapList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (Object oo : list) {
                Map argss = new HashMap();
                argss.put("oo", oo);
                argMapList.add(argss);
            }
            this.dao._batchUpdate("delete from  assistyuejuan  where id ={oo} ", argMapList);
        }
    }

    public void recoverytask(String examPaperNum, String groupNum, String questionNum, String userNum) {
        Map<String, PersonWorkRecord> personWorkRecordMap = AwardPointDaoImpl.personWorkRecordMap;
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("userNum", userNum);
        args.put("questionNum", questionNum);
        args.put("examPaperNum", examPaperNum);
        if ((examPaperNum != null || !examPaperNum.equals("")) && ((groupNum != null || !groupNum.equals("")) && null != questionNum && !questionNum.equals("") && null != userNum && !userNum.equals(""))) {
            this.dao._execute("delete r from remark r inner JOIN( \t\tSELECT scoreId FROM task where groupNum={groupNum} and updateUser={userNum} \t)t on r.scoreId=t.scoreid  where r.type<>'1' or r.status<>'T'", args);
            this.dao._execute("delete m from markerror m INNER JOIN questiongroup_question qq on m.questionNum=qq.questionNum  where qq.groupNum={groupNum}  and m.userNum={userNum} ", args);
            this.dao._execute("DELETE q FROM remarkimg q  right JOIN (  select scoreId,insertUser  from task where groupNum={groupNum} and insertUser={userNum}   )t  on t.scoreId=q.scoreid  where q.insertUser=t.insertUser", args);
            this.dao._execute(" update task t left JOIN remark r on r.scoreId=t.scoreid set t.insertUser='-1', t.updateUser='-1',t.status='F',t.isException='F',t.rownum='-1',t.isDelete='F',t.porder=0,t.fenfaDate=0,t.updateTime='',t.xuankaoqufen=1   where r.scoreId is null and  t.groupNum={groupNum}  and t.insertUser={userNum} ", args);
            this.dao._execute(" update score s INNER JOIN  (select scoreid, if(AVG(questionScore)*10 mod 10=5,AVG(questionScore), ROUND(AVG(questionScore))) avgscore  from task where  `status`= 'T' and  groupNum={groupNum} GROUP BY scoreid  HAVING count(1) = 2) t on s.id=t.scoreid left join remark r on r.scoreId=t.scoreid set s.questionScore=t.avgscore  where r.scoreId is null ", args);
            this.dao._execute(" UPDATE  task t INNER join ( select a.id,b.userNum from (SELECT id,scoreId from  task where   groupNum={groupNum} and  userNum='3' and `status`= 'T') a INNER JOIN task b on a.scoreId=b.scoreId and b.status= 'F') s on t.id=s.id set t.userNum=s.userNum", args);
            this.dao._execute("UPDATE  task t INNER join ( select b.id from  (SELECT scoreId from  task where  groupNum={groupNum}  GROUP BY scoreId ,userNum HAVING count(1)=2 ) a INNER JOIN task b on a.scoreId=b.scoreId and b.status= 'F') s on t.id=s.id set t.userNum=3", args);
            this.dao._execute("DELETE t from task t INNER JOIN ( select a1.scoreId from  (SELECT id,scoreId,questionScore from task  where groupNum={groupNum}  and  userNum=1 and `status`= 'T' ) a1 INNER join  (SELECT id,scoreId,questionScore from task  where groupNum={groupNum}  and  userNum=2 and `status`= 'T' ) a2 on a1.scoreId=a2.scoreId INNER join  ( select  errorRate  from define where id ={groupNum}  union all select  errorRate  from subdefine where id ={groupNum}  ) b on 1=1  where   ABS(a1.questionScore-a2.questionScore)< b.errorRate) s on t.scoreId=s.scoreId where t.usernum=3", args);
            this.dao._execute("DELETE t from task t INNER JOIN ( select scoreId from task where groupNum={groupNum}  and (userNum=1 or userNum=2) and `status`='F' group by scoreId  ) s  on t.scoreId=s.scoreId  where t.usernum=3", args);
            this.dao._execute("delete from workrecord where groupNum={groupNum}  and insertUser={userNum} ", args);
            this.dao._execute("delete from questionstepscore where questionNum={questionNum}  and insertUser={userNum} ", args);
            Object count = this.dao._queryObject("SELECT GROUP_CONCAT(d.id)  from (  SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={questionNum}  and d1.id<>{questionNum}   union     SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename    LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum    where sb.id={questionNum}  and sbb.id<>{questionNum}    )d INNER JOIN questiongroup_user qu on d.id=qu.groupNum   and qu.examPaperNum={examPaperNum}  and qu.userNum={userNum} and qu.appendTeacher=1", args);
            if (count != null) {
                Map<String, String> map = this.dao._queryOrderMap("SELECT qu.groupName,cast(ifnull(count(1),0) as char) from task t      left join questiongroup qu on t.groupNum=qu.groupNum INNER join(       SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={questionNum}  and d1.id<>{questionNum}   union        SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename       LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum       where sb.id={questionNum}  and sbb.id<>{questionNum}    )d on t.groupNum=d.id and t.exampaperNum={examPaperNum}  and t.insertUser={userNum}  and t.`status`='T'     GROUP BY t.groupNum ", TypeEnum.StringString, args);
                if (map.size() == 0) {
                    args.put("count", count);
                    this.dao._execute("delete from questiongroup_user where examPaperNum={examPaperNum}  and groupNum in ({count[]}) and userNum={userNum}  and appendTeacher=1 ", args);
                }
            }
            personWorkRecordMap.remove(groupNum + "-" + userNum);
            return;
        }
        if ((examPaperNum != null || !examPaperNum.equals("")) && ((groupNum != null || !groupNum.equals("")) && ((questionNum == null || questionNum.equals("")) && null != userNum && !userNum.equals("")))) {
            this.dao._execute("delete r from remark r inner JOIN( \t\tSELECT scoreId FROM task where groupNum={groupNum} and updateUser={userNum} \t)t on r.scoreId=t.scoreid ", args);
            this.dao._execute("delete m from markerror m INNER JOIN questiongroup_question qq on m.questionNum=qq.questionNum  where qq.groupNum={groupNum} and m.userNum={userNum} ", args);
            this.dao._execute("DELETE q FROM remarkimg q  right JOIN (  select scoreId,insertUser  from task where groupNum={groupNum} and insertUser={userNum}  )t  on t.scoreId=q.scoreid  where q.insertUser=t.insertUser", args);
            this.dao._execute("DELETE t1 FROM task t1  right JOIN (select scoreId  from task where groupNum={groupNum} and insertUser={userNum} )t2  on t2.scoreId=t1.scoreid  where t1.userNum='3'", args);
            this.dao._execute(" update task set insertUser='-1',status='F',isException='F',rownum='-1',isDelete='F',porder=0,fenfaDate=0,updateTime='',xuankaoqufen=1   where groupNum={groupNum}   and insertUser={userNum} ", args);
            this.dao._execute("delete from workrecord where groupNum={groupNum}  and insertUser={userNum} ", args);
            this.dao._execute("delete from questionstepscore where questionNum={groupNum}   and insertUser={userNum} ", args);
            personWorkRecordMap.remove(groupNum + "-" + userNum);
            return;
        }
        if ((examPaperNum == null && examPaperNum.equals("")) || ((groupNum == null && groupNum.equals("")) || (userNum != null && !userNum.equals("")))) {
            if (examPaperNum == null && examPaperNum.equals("")) {
                return;
            }
            if (groupNum == null || groupNum.equals("")) {
                if ((userNum == null || userNum.equals("")) && questionNum != null && !questionNum.equals("")) {
                    this.dao._execute("delete r from remark r inner JOIN( \t\tSELECT scoreId FROM task where groupNum={groupNum}  and updateUser={userNum} \t)t on r.scoreId=t.scoreid ", args);
                    this.dao._execute("delete m from markerror m INNER JOIN questiongroup_question qq on m.questionNum=qq.questionNum  where qq.groupNum={groupNum}  and m.userNum={userNum} ", args);
                    this.dao._execute("DELETE q FROM remarkimg q  right JOIN (  select scoreId,insertUser  from task where groupNum={groupNum}  and insertUser={userNum}  )t  on t.scoreId=q.scoreid  where q.insertUser=t.insertUser", args);
                    Object groupNumobj = this.dao._queryObject("select groupNum from questiongroup_question where exampaperNum={examPaperNum}  and  questionNum={questionNum} group by groupNum", args);
                    args.put("groupNumobj", groupNumobj);
                    this.dao._execute("DELETE t1 FROM task t1  right JOIN (select scoreId  from task where groupNum={groupNumobj}  and insertUser={userNum}  )t2  on t2.scoreId=t1.scoreid  where t1.userNum='3'", args);
                    this.dao._execute("update task set indertUser='-1',status='F',isException='F',rownum='-1',isDelete='F',porder=0,fenfaDate=0,updateTime='',xuankaoqufen=1  where groupNum={groupNumobj}   and insertUser={userNum} ", args);
                    this.dao._execute("delete from workrecord  where groupNum={groupNumobj}  and insertUser={userNum} ", args);
                    this.dao._execute("delete from questionstepscore where questionNum={questionNum}  and insertUser={userNum} ", args);
                    return;
                }
                return;
            }
            return;
        }
        List userlist = this.dao._queryColList(" select userNum from questiongroup_user where exampaperNum={examPaperNum}  and groupNum={groupNum} and userType<>2 ", args);
        for (int i = 0; i < userlist.size(); i++) {
            String userNum2 = String.valueOf(userlist.get(i));
            Map argss = new HashMap();
            argss.put("groupNum", groupNum);
            argss.put("userNum", userNum2);
            argss.put("questionNum", questionNum);
            argss.put("examPaperNum", examPaperNum);
            if (questionNum != null && !questionNum.equals("")) {
                this.dao._execute("DELETE q FROM remarkimg q  right JOIN (  select scoreId,insertUser  from task where groupNum={groupNum} and insertUser={userNum}  )t  on t.scoreId=q.scoreid  where q.insertUser=t.insertUser", argss);
                this.dao._execute("DELETE t1 FROM task t1  right JOIN (select scoreId  from task where groupNum={groupNum}  and insertUser={userNum}  )t2  on t2.scoreId=t1.scoreid  where t1.userNum='3'", argss);
                this.dao._execute(" update task set insertUser='-1',status='F',isException='F',rownum='-1',isDelete='F',porder=0,fenfaDate=0,updateTime='',xuankaoqufen=1  where groupNum={groupNum}   and insertUser={userNum} ", argss);
                this.dao._execute("delete from workrecord  where groupNum={groupNum}   and insertUser={userNum} ", argss);
                this.dao._execute("delete from questionstepscore where questionNum={questionNum}  and insertUser={userNum} ", argss);
            } else {
                this.dao._execute("DELETE q FROM remarkimg q  right JOIN (  select scoreId,insertUser  from task where groupNum={groupNum} and insertUser={userNum}   )t  on t.scoreId=q.scoreid  where q.insertUser=t.insertUser", argss);
                this.dao._execute("DELETE t1 FROM task t1  right JOIN (select scoreId  from task where groupNum={groupNum}  and insertUser={userNum} )t2  on t2.scoreId=t1.scoreid  where t1.userNum='3'", argss);
                this.dao._execute(" update task set insertUser='-1',status='F',isException='F',rownum='-1',isDelete='F',porder=0,fenfaDate=0,updateTime='',xuankaoqufen=1   where groupNum={groupNum}   and insertUser={userNum} ", argss);
                this.dao._execute("delete from workrecord  where groupNum={groupNum}  and insertUser={userNum} ", argss);
                this.dao._execute("delete from questionstepscore where questionNum={groupNum}  and insertUser={userNum} ", argss);
            }
            Object count2 = this.dao._queryObject("SELECT GROUP_CONCAT(d.id)  from (  SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={questionNum} and d1.id<>{questionNum}  union     SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename    LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum    where sb.id={questionNum} and sbb.id<>{questionNum}  )d INNER JOIN questiongroup_user qu on d.id=qu.groupNum   and qu.examPaperNum={examPaperNum} and qu.userNum={userNum} and qu.appendTeacher=1", argss);
            argss.put("count", count2);
            if (count2 != null) {
                Map<String, String> map2 = this.dao._queryOrderMap("SELECT qu.groupName,cast(ifnull(count(1),0) as char) from task t      left join questiongroup qu on t.groupNum=qu.groupNum INNER join(       SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={questionNum} and d1.id<>{questionNum}    union        SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename       LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum       where sb.id={questionNum}  and sbb.id<>{questionNum}   )d on t.groupNum=d.id and t.exampaperNum={examPaperNum} and t.insertUser={userNum}and t.`status`='T'     GROUP BY t.groupNum ", TypeEnum.StringString, argss);
                if (map2.size() == 0) {
                    this.dao._execute("delete from questiongroup_user where examPaperNum={examPaperNum} and groupNum in ({count[]}) and userNum={userNum} and appendTeacher=1 ", argss);
                }
            }
            personWorkRecordMap.remove(groupNum + "-" + userNum2);
        }
        if (questionNum == null || questionNum.equals("")) {
            this.dao._execute("delete r from remark r inner join questiongroup_question q on q.groupNum={groupNum}  where r.questionNum=q.questionNum ", args);
            this.dao._execute("delete r from markerror r inner join questiongroup_question q on q.groupNum={groupNum}  where r.questionNum=q.questionNum ", args);
        }
    }

    public void resetPanfenByTi(String examPaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("examPaperNum", examPaperNum);
        Map<String, PersonWorkRecord> personWorkRecordMap = AwardPointDaoImpl.personWorkRecordMap;
        List list = this.dao._queryColList("SELECT userNum from questiongroup_user where groupNum={groupNum} ", args);
        for (int i = 0; i < list.size(); i++) {
            String userNum = String.valueOf(list.get(i));
            personWorkRecordMap.remove(groupNum + "-" + userNum);
        }
        this.dao._execute("DELETE t1 FROM task t1  right JOIN (select scoreId  from task where groupNum={groupNum}  and exampaperNum={examPaperNum}  )t2  on t2.scoreId=t1.scoreid  where t1.userNum='3'", args);
        this.dao._execute(" update task set insertUser='-1',status='F',isException='F',rownum='-1',isDelete='F',porder=0,fenfaDate=0,updateTime='',xuankaoqufen=1   where groupNum={groupNum} and exampaperNum={examPaperNum} ", args);
        this.dao._execute("delete from workrecord  where groupNum={groupNum}  and exampaperNum={examPaperNum} ", args);
        this.dao._execute("delete r from remark r inner join questiongroup_question q on q.groupNum={groupNum}  where r.questionNum=q.questionNum and r.exampaperNum={examPaperNum}  ", args);
        this.dao._execute("delete r from markerror r inner join questiongroup_question q on q.groupNum={groupNum}  where r.questionNum=q.questionNum  and r.exampaperNum={examPaperNum} ", args);
        this.dao._execute("delete from questionstepscore where questionNum={groupNum} and exampaperNum={examPaperNum} ", args);
        Object returnCount = this.dao._queryObject("SELECT cast(ifnull(sum(1),0) as char) from task t  left join questiongroup qu on t.groupNum=qu.groupNum INNER join(   SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={groupNum} and d1.id<>{groupNum} union    SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename   LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum   where sb.id={groupNum} and sbb.id<> {groupNum} )d on t.groupNum=d.id and t.exampaperNum={examPaperNum} and t.`status`='T'", args);
        if ("SELECT cast(ifnull(sum(1),0) as char) from task t  left join questiongroup qu on t.groupNum=qu.groupNum INNER join(   SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={groupNum} and d1.id<>{groupNum} union    SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename   LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum   where sb.id={groupNum} and sbb.id<> {groupNum} )d on t.groupNum=d.id and t.exampaperNum={examPaperNum} and t.`status`='T'" != 0 && "0".equals(String.valueOf(returnCount))) {
            Object count = this.dao._queryObject("SELECT GROUP_CONCAT(d.id) from (SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={groupNum}  union    SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename   LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum   where sb.id={groupNum} )d ", args);
            args.put("count", count);
            this.dao._execute("delete from questiongroup_user where examPaperNum={examPaperNum} and groupNum in ({count[]}) and appendTeacher=1 ", args);
        }
        this.dao._execute("delete qci from remarkimg qci inner join (select scoreId from task where exampaperNum={examPaperNum}  and groupNum={groupNum} )t on t.scoreId=qci.scoreId ", args);
    }

    public Integer updateGroupfz(Integer examPaperNum, String questionNum, String questionName) {
        Map args = new HashMap();
        args.put("questionName", questionName);
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        Integer i = Integer.valueOf(this.dao._execute("update questiongroup  set groupName={questionName}   where exampaperNum={examPaperNum}  and groupNum={questionNum} ", args));
        return i;
    }

    public void updatetaskquestionName(String examPaperNum, String newQuestionNum, String oldQuestionNum) {
        Map args = new HashMap();
        args.put("newQuestionNum", newQuestionNum);
        args.put("examPaperNum", examPaperNum);
        args.put("oldQuestionNum", oldQuestionNum);
        this.dao._execute(" update task set questionnum={newQuestionNum}  where examPaperNum={examPaperNum}  and questionNum={oldQuestionNum} ", args);
    }

    public Integer updateGroupfz_question(String examPaperNum, String newQuestionNum, String oldQuestionNum) {
        String sql;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("oldQuestionNum", oldQuestionNum);
        args.put("newQuestionNum", newQuestionNum);
        String s = this.dao._queryStr("select count(1) from questiongroup_question where examPaperNum={examPaperNum}  and groupNum={oldQuestionNum}  and questionNum={oldQuestionNum} ", args);
        if (!s.equals("0")) {
            sql = "update questiongroup_question set groupNum={newQuestionNum} ,questionNum={newQuestionNum} ' where examPaperNum={examPaperNum}  and groupNum={oldQuestionNum} ' and questionNum={oldQuestionNum} ";
        } else {
            sql = "update questiongroup_question set questionNum={newQuestionNum}  where examPaperNum={examPaperNum} and questionNum={oldQuestionNum} ";
        }
        return Integer.valueOf(this.dao._execute(sql, args));
    }

    public Integer updateGroupfz_user(String examPaperNum, String newQuestionNum, String oldQuestionNum) {
        Map args = new HashMap();
        args.put("newQuestionNum", newQuestionNum);
        args.put("examPaperNum", examPaperNum);
        args.put("oldQuestionNum", oldQuestionNum);
        return Integer.valueOf(this.dao._execute("update questiongroup_user set groupNum={newQuestionNum}  where  examPaperNum={examPaperNum}  and groupNum={oldQuestionNum} ", args));
    }

    public Integer updateGroupfz_mark_setting(String examPaperNum, String newQuestionNum, String oldQuestionNum) {
        Map args = new HashMap();
        args.put("newQuestionNum", newQuestionNum);
        args.put("examPaperNum", examPaperNum);
        args.put("oldQuestionNum", oldQuestionNum);
        return Integer.valueOf(this.dao._execute("update questiongroup_mark_setting set groupNum={newQuestionNum} where examPaperNum={examPaperNum}  and groupNum={oldQuestionNum} ", args));
    }

    public String gettaskCount(Integer examPaperNum, String groupNum, String userNum) {
        String count = null;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        String groupNumStr = " and groupNum={groupNum} ";
        args.put("groupNum", groupNum);
        if (groupNum.indexOf("_") != -1) {
            groupNum.replace("_", Const.STRING_SEPERATOR);
            groupNumStr = " and groupNum in ({groupNum[]}) ";
        }
        String sql3 = "SELECT examPaperNum,questionNum,groupNum from questiongroup_question where  examPaperNum ={examPaperNum} " + groupNumStr;
        List<QuestionGroup_question> list = this.dao._queryBeanList(sql3, QuestionGroup_question.class, args);
        if (list.size() > 0) {
            for (QuestionGroup_question qgq2 : list) {
                Map argss = new HashMap();
                argss.put("examPaperNum", examPaperNum);
                argss.put("questionNum", qgq2.getQuestionNum());
                argss.put("userNum", userNum);
                if ((examPaperNum != null || !examPaperNum.equals("")) && ((qgq2.getQuestionNum() != null || !qgq2.getQuestionNum().equals("")) && (userNum == null || userNum.equals("")))) {
                    count = this.dao._queryStr("select count(1) from task where examPaperNum={examPaperNum} and questionNum={questionNum}  and insertUser!='-1'", argss);
                } else if ((examPaperNum != null || !examPaperNum.equals("")) && ((qgq2.getQuestionNum() != null || !qgq2.getQuestionNum().equals("")) && (userNum != null || !userNum.equals("")))) {
                    count = this.dao._queryStr("select count(1) from task where examPaperNum={examPaperNum} and questionNum={questionNum}  and insertUser={userNum} ", argss);
                }
                if (!count.equals("0")) {
                    break;
                }
            }
        } else {
            String sql4 = "SELECT count(1) from questiongroup where  examPaperNum ={examPaperNum} " + groupNumStr;
            count = this.dao._queryStr(sql4, args);
            if (!"0".equals(sql4)) {
                count = "0";
            }
        }
        return count;
    }

    public List getkgQuestionNum(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return this.dao._queryBeanList("select questionNum from define where examPaperNum={examPaperNum}  and questionType='1' ORDER BY length(questionNum),questionNum", Define.class, args);
    }

    public String getGCount(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return this.dao._queryStr("select count(1) from questionGroup where examPaperNum={examPaperNum} ", args);
    }

    public List getTeacher(String teacherName) {
        Map args = new HashMap();
        args.put("USER_COMMON", "1");
        args.put("teacherName", "%" + teacherName + "%");
        String sql = "select id,userid,username,usertype,realname from user where usertype={USER_COMMON}  ";
        if (teacherName != null) {
            sql = sql + " and (realName like {teacherName} or username like {teacherName})";
        }
        return this.dao._queryBeanList(sql, User.class, args);
    }

    public void updateGroupName(String examPaperNum, String groupNum, String groupName) {
        Map args = new HashMap();
        args.put("groupName", groupName);
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        this.dao._execute("update questiongroup set groupName={groupName} where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
    }

    public String queryUpdateGroupName(String examPaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        return this.dao._queryStr("select groupName from questiongroup where examPaperNum={examPaperNum} and groupNum={groupNum} ", args);
    }

    public List getfpzt(String exam, String examPaperNum) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("examPaperNum", examPaperNum);
        String sql = "select r.examPaperNum examPaperNum,count(r.roleNum) count,r.type from userrole ur  left join   (select roleNum,examPaperNum,type from role where examNum={exam} ) r  on r.roleNum=ur.roleNum  GROUP BY r.roleNum";
        if (null != examPaperNum && !"".equals(examPaperNum)) {
            sql = "select r.examPaperNum examPaperNum,count(r.roleNum) count,r.type from userrole ur  left join   (select roleNum,examPaperNum,type from role where examNum={exam} and examPaperNum={examPaperNum} ) r  on r.roleNum=ur.roleNum  GROUP BY r.roleNum";
        }
        return this.dao._queryBeanList(sql, Role.class, args);
    }

    public List getexampaperList(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        deletesubTeacher(examNum);
        return this.dao._queryBeanList("select examPaperNum,examNum,gradeNum,subjectNum from exampaper where examNum={examNum} ", Exampaper.class, args);
    }

    public List getsubjetTeacher(Integer subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao._queryBeanList("select t.teacherNum from (select userNum from userposition where subjectNum={subjectNum} ) up left join teacher t on t.teacherNum=up.userNum GROUP BY t.teacherNum", Teacher.class, args);
    }

    public void deletesubTeacher(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        List<?> _queryColList = this.dao._queryColList("select roleNum from role where examNum={examNum} and type='4'", String.class, args);
        List<Map> argMapList = new ArrayList<>();
        if (_queryColList != null && _queryColList.size() > 0) {
            Iterator<?> it = _queryColList.iterator();
            while (it.hasNext()) {
                String roleNum = (String) it.next();
                Map argss = new HashMap();
                argss.put("roleNum", roleNum);
                argMapList.add(argss);
            }
            this.dao._batchUpdate("delete from userrole where roleNum={roleNum} ", argMapList);
        }
        this.dao._execute("delete from role where examNum={examNum} ", args);
    }

    public List getGroupNumZ(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        return this.dao._queryBeanList("SELECT  q.groupName,q.groupNum  from questiongroup q  where q.exampaperNum={exampaperNum}  ORDER BY CASE  WHEN LOCATE('-',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('-' IN q.groupName)-1),'.01')*1  WHEN LOCATE('_',q.groupName)>0 THEN CONCAT(SUBSTR(q.groupName,1,POSITION('_' IN q.groupName)-1),'.02')*1  ELSE CONCAT(q.groupName,'.1')*1 END  ASC ,REPLACE(q.groupName,'_','.')*1", QuestionGroup.class, args);
    }

    public List bfExportQuestionUser(String exam, String grade) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        return this.dao._queryBeanList("select s.subjectNum,s.subjectName from  (select  subjectNum from exampaper where examNum={exam}  and gradeNum={grade} ) e,`subject` s  where e.subjectNum=s.subjectNum", Subject.class, args);
    }

    public List getMaxTitle(Integer exa, Integer gradeNum, String type) {
        Map args = new HashMap();
        args.put("exa", exa);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        String sql = "";
        if (type.equals("maxg")) {
            sql = "select MAX(groupNum) groupNum  from ( select a.exampaperNum,count(DISTINCT a.groupNum) groupNum,COUNT(a.userNum) userNum from ( select q.exampaperNum,q.groupNum,q.userNum,u.realname from questiongroup_user q ,`user` u where q.exampaperNum in  (select examPaperNum from exampaper where examNum={exa}  and gradeNum={gradeNum}  )  and u.id=q.userNum ORDER BY  q.exampaperNum   ) a GROUP BY a.exampaperNum ) al  ";
        } else if (type.equals("group")) {
            sql = "SELECT DISTINCT qu.groupNum,qg.groupName ext1 from questiongroup_user qu,questiongroup qg   where qu.exampaperNum={exa}  and qu.exampaperNum=qg.exampaperNum and qu.groupNum=qg.groupNum";
        } else if (type.equals("subject")) {
            sql = "SELECT DISTINCT e.examPaperNum,e.subjectNum ext1,s.subjectName ext2 from exampaper e  LEFT JOIN subject s on e.subjectNum = s.subjectNum  WHERE e.examNum ={exa}  and e.gradeNum ={gradeNum}  AND e.isHidden='F' and e.type='0'";
        } else if (type.equals("user")) {
            sql = "select DISTINCT u.userName userName,userNum ext1,u.realname ext2,sch.schoolName ext3 from questiongroup_user q ,`user` u  left join school sch on sch.id=u.schoolNum where q.exampaperNum={exa}  and u.id=q.userNum";
        } else if (type.equals("all")) {
            sql = "select qgu.exampaperNum,qgu.groupNum,qgu.userNum ext1,u.realname ext2,qa.num ext3 from questiongroup_user qgu LEFT JOIN quota qa on qa.exampaperNum = qgu.exampaperNum and qa.groupNum = qgu.groupNum and qa.insertUser = qgu.userNum LEFT JOIN user u on u.id = qgu.userNum WHERE qgu.exampaperNum ={exa} ";
        } else if (type.equals("examname")) {
            sql = "select examName ext1 from exam where examNum={exa} ";
        }
        return this.dao._queryBeanList(sql, QuestionGroup_user.class, args);
    }

    public List getGradeM(String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryBeanList("select gradeNum,gradeName,schoolNum from grade  where gradeNum={gradeNum} ", Grade.class, args);
    }

    public void updatequestiontotalnum(Integer exampaperNum, String groupNum, String questionNum, String page, String type) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("questionNum", questionNum);
        args.put("page", page);
        args.put("exampaperNum", exampaperNum);
        String groupNumsql = "";
        String questionNumsql = "";
        String pagesql = "";
        String groupNumsql2 = "";
        if (!"".equals(groupNum) && null != groupNum && !"null".equals(groupNum)) {
            groupNumsql = groupNumsql + " and groupNum={groupNum} ";
        }
        if (!"".equals(questionNum) && null != questionNum && !"null".equals(questionNum)) {
            questionNumsql = questionNumsql + " and questionNum={questionNum} ";
            groupNumsql2 = groupNumsql2 + " and groupNum={questionNum} ";
        }
        if (!"".equals(page) && null != page && !"null".equals(page)) {
            pagesql = pagesql + " and page={page} ";
        }
        if (type.equals("0")) {
            String groupNumlistsql = " select /* shard_host_HG=Write */ exampaperNum,groupNum,questionNum  from task where exampaperNum={exampaperNum} " + pagesql + " group by groupNum ";
            List groupNumList = this.dao._queryBeanList(groupNumlistsql, AwardPoint.class, args);
            for (int i = 0; i < groupNumList.size(); i++) {
                AwardPoint ap = (AwardPoint) groupNumList.get(i);
                Map argss = new HashMap();
                argss.put("exampaperNum", exampaperNum);
                argss.put("groupNum", ap.getGroupNum());
                String totalnumCount = this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where exampaperNum={exampaperNum}   and groupNum={groupNum} ", argss);
                if (null != this.dao._queryObject(" select /* shard_host_HG=Write */ groupNum from questiongroup where examPaperNum={exampaperNum}  and groupNum={groupNum} limit 1", argss)) {
                    argss.put("totalnumCount", totalnumCount);
                    this.dao._execute(" update questiongroup set totalnum={totalnumCount} where examPaperNum={exampaperNum}  and groupNum={groupNum} ", argss);
                }
            }
            return;
        }
        if (type.equals("1")) {
            String totalnumsql = " select /* shard_host_HG=Write */ count(1) from task where exampaperNum={exampaperNum} " + groupNumsql + "  ";
            String totalnumCount2 = this.dao._queryStr(totalnumsql, args);
            if (null != this.dao._queryObject(" select /* shard_host_HG=Write */ groupNum from questiongroup where examPaperNum={exampaperNum}  and groupNum={groupNum} limit 1", args)) {
                args.put("totalnumCount", totalnumCount2);
                this.dao._execute(" update questiongroup set totalnum={totalnumCount} where examPaperNum={exampaperNum}  and groupNum={groupNum} ", args);
                return;
            }
            return;
        }
        if (type.equals("2")) {
            String groupNumlistsql2 = "select /* shard_host_HG=Write */ qq.* from  (select examPaperNum,questionNum,groupNum from questiongroup_question where exampaperNum={exampaperNum}  " + questionNumsql + ")qq left join  (select exampaperNum,groupNum,groupName,totalnum from questiongroup where exampaperNum={exampaperNum}  " + groupNumsql2 + ")q on qq.groupnum=q.groupnum";
            List groupNumList2 = this.dao._queryBeanList(groupNumlistsql2, QuestionGroup_question.class, args);
            for (int i2 = 0; i2 < groupNumList2.size(); i2++) {
                QuestionGroup_question qq = (QuestionGroup_question) groupNumList2.get(i2);
                Map argss2 = new HashMap();
                argss2.put("exampaperNum", exampaperNum);
                argss2.put("groupNum", qq.getGroupNum());
                this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where exampaperNum={exampaperNum}   and groupNum={groupNum} ", argss2);
                if (null != this.dao._queryObject(" select /* shard_host_HG=Write */ groupNum from questiongroup where examPaperNum={exampaperNum}  and groupNum={groupNum} limit 1", argss2)) {
                    this.dao._execute(" update questiongroup set totalnum={totalnumCount} where examPaperNum={exampaperNum}  and groupNum={groupNum} ", argss2);
                }
            }
        }
    }

    public void addLog(String url, String name, String userid, String ip) {
        Map args = new HashMap();
        args.put("url", url);
        args.put(License.IP, ip);
        args.put("userid", userid);
        args.put("insertDate", DateUtil.getCurrentTime());
        args.put("name", name);
        if (userid != null && !userid.equals("null")) {
            this.dao._execute("insert into log(requestUrl,ip,insertUser,insertDate,operate)values({url},{ip},{userid} ,{insertDate},{name})", args);
        }
    }

    public void xxthb(Integer examPaperNum, String groupNum, String questionName, String loginName, String groupType) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        List<?> _queryBeanList = this.dao._queryBeanList("SELECT d2.id,d2.questionNum FROM define d LEFT JOIN define d2 on d2.choosename=d.choosename where d.id={groupNum} and d.choosename<>'s' and d2.`merge`='0'", Define.class, args);
        if (_queryBeanList.size() <= 0) {
            Define d = new Define();
            d.setId(groupNum);
            d.setQuestionNum(questionName);
            _queryBeanList.add(d);
        }
        Iterator<?> it = _queryBeanList.iterator();
        while (it.hasNext()) {
            Define chooseObj = (Define) it.next();
            String groupNum2 = chooseObj.getId();
            String questionName2 = chooseObj.getQuestionNum();
            Map argss = new HashMap();
            argss.put("groupNum", groupNum2);
            this.dao._execute("update define set  `merge`='1'  where id={groupNum} ", argss);
            createxxthb(examPaperNum, groupNum2, questionName2, loginName, groupType);
            createGroupfz_mark_setting(examPaperNum, groupNum2, loginName);
            argss.put("examPaperNum", examPaperNum);
            List list = this.dao._queryColList("select id from subdefine where examPaperNum={examPaperNum} and pid={groupNum} ", argss);
            for (int i = 0; i < list.size(); i++) {
                Object o = list.get(i);
                String questionNum = String.valueOf(o);
                Map args2 = new HashMap();
                args2.put("groupNum", groupNum2);
                args2.put("examPaperNum", examPaperNum);
                args2.put("questionNum", questionNum);
                this.dao._execute("update questiongroup_question set groupNum={groupNum} where examPaperNum={examPaperNum}  and  questionNum={questionNum} ", args2);
                this.dao._execute("delete from questionGroup  where  exampaperNum={examPaperNum}  and groupNum={questionNum} ", args2);
                deleteGroupfz_user(Integer.valueOf(examPaperNum.intValue()), questionNum, "0", null);
                this.dao._execute("delete from questiongroup_mark_setting  where examPaperNum={examPaperNum}  and  groupNum={questionNum} ", args2);
                this.dao._execute(" delete from task where examPaperNum={examPaperNum}  and groupNum={questionNum}  and usernum!='1' ", args2);
                this.dao._execute("update task set groupNum={groupNum}  where examPaperNum={examPaperNum} and questionNum={questionNum} ", args2);
                this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and questionNum={questionNum}  and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args2);
                this.dao._execute("delete from remark where examPaperNum={examPaperNum} and questionNum={questionNum}", args2);
            }
            this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where examPaperNum={examPaperNum}  and groupNum={groupNum} ", argss);
            this.dao._execute(" update questiongroup set totalnum={counttask2} where examPaperNum={examPaperNum}  and groupNum={groupNum} ", argss);
        }
    }

    public void qxhb(Integer examPaperNum, String groupNum, String questionName, String loginName, String groupType) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        List<?> _queryBeanList = this.dao._queryBeanList("SELECT d2.id,d2.questionNum FROM define d LEFT JOIN define d2 on d2.choosename=d.choosename where d.id={groupNum}  and d.choosename<>'s' and d2.`merge`='1'", Define.class, args);
        if (_queryBeanList.size() <= 0) {
            Define d = new Define();
            d.setId(groupNum);
            d.setQuestionNum(questionName);
            _queryBeanList.add(d);
        }
        Iterator<?> it = _queryBeanList.iterator();
        while (it.hasNext()) {
            Define chooseObj = (Define) it.next();
            String groupNum2 = chooseObj.getId();
            chooseObj.getQuestionNum();
            Map argss = new HashMap();
            argss.put("groupNum", groupNum2);
            argss.put("examPaperNum", examPaperNum);
            this.dao._execute("update define set  `merge`='0'  where id={groupNum} ", argss);
            List list = this.dao._queryBeanList("select id groupNum,questionNum groupName from subdefine where examPaperNum={examPaperNum}  and pid={groupNum}  AND questionType='1'", QuestionGroup.class, argss);
            for (int i = 0; i < list.size(); i++) {
                QuestionGroup qg = (QuestionGroup) list.get(i);
                createxxthb(examPaperNum, qg.getGroupNum(), qg.getGroupName(), loginName, groupType);
                createGroupfz_question(examPaperNum, qg.getGroupNum(), loginName);
                createGroupfz_mark_setting(examPaperNum, qg.getGroupNum(), loginName);
                Map args2 = new HashMap();
                args2.put("groupNum", qg.getGroupNum());
                args2.put("examPaperNum", examPaperNum);
                this.dao._execute("update task set groupNum={groupNum}  where examPaperNum={examPaperNum}  and questionNum={groupNum}  and usernum='1'", args2);
                this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum}  and questionNum={questionNum} and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args2);
                this.dao._queryStr(" select /* shard_host_HG=Write */ count(1) from task where examPaperNum={examPaperNum} and groupNum={groupNum} ", args2);
                this.dao._execute(" update questiongroup set totalnum={counttask2} where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args2);
            }
            this.dao._execute("delete from questionGroup  where  exampaperNum={examPaperNum}  and groupNum={groupNum}  ", argss);
            deleteGroupfz_question(examPaperNum, groupNum2, "2");
            deleteGroupfz_user(Integer.valueOf(examPaperNum.intValue()), groupNum2, "0", null);
            this.dao._execute("delete from questiongroup_mark_setting  where examPaperNum={examPaperNum}  and  groupNum={groupNum} ", argss);
            this.dao._execute(" delete from task where examPaperNum={examPaperNum} and groupNum={groupNum}  and usernum!='1' ", argss);
            this.dao._execute("delete from remark where examPaperNum={examPaperNum}  and questionNum={groupNum} ", argss);
        }
    }

    public void createxxthb(Integer examPaperNum, String groupNum, String questionName, String loginName, String groupType) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        String merge = "";
        String pid = this.dao._queryStr("select pid from subdefine where id={groupNum} ", args);
        if (StrUtil.isNotEmpty(pid)) {
            args.put("pid", pid);
            merge = this.dao._queryStr("select /* shard_host_HG=Write */ `merge` from define where id={pid} ", args);
        }
        if ("1".equals(merge)) {
            return;
        }
        QuestionGroup qg = new QuestionGroup();
        qg.setExampaperNum(examPaperNum);
        qg.setGroupNum(groupNum);
        qg.setGroupName(questionName);
        qg.setInsertUser(loginName);
        qg.setInsertDate(DateUtil.getCurrentDay());
        qg.setStat("0");
        if (groupType != null && groupType.equals("0")) {
            qg.setGroupType("0");
        } else if (groupType != null && groupType.equals("2")) {
            qg.setGroupType("2");
        } else {
            qg.setGroupType("1");
        }
        Integer.valueOf(this.dao.save(qg));
    }

    public void daoruxxt(Integer examPaperNum, String groupNum, String questionNum, String questionName, String loginName, String groupType) {
        if (groupType.equals("0")) {
            createxxthb(examPaperNum, groupNum, questionName, loginName, "2");
            createGroupfz_mark_setting(examPaperNum, groupNum, loginName);
            return;
        }
        QuestionGroup_question qgq = new QuestionGroup_question();
        qgq.setId(GUID.getGUIDStr());
        qgq.setExampaperNum(examPaperNum);
        qgq.setGroupNum(groupNum);
        qgq.setQuestionNum(questionNum);
        qgq.setInsertUser(loginName);
        qgq.setInsertDate(DateUtil.getCurrentDay());
        this.dao.save(qgq);
    }

    public void updateforbidden(String exampaperNum, String groupNum, String stat, String alltype) {
        Map args = new HashMap();
        args.put("stat", stat);
        args.put("exampaperNum", exampaperNum);
        if ("all".equals(alltype)) {
            this.dao._execute("update questiongroup set stat={stat} where exampaperNum={exampaperNum} ", args);
            return;
        }
        new ArrayList();
        String[] groupNumArr = groupNum.split("_");
        List<Map> argMapList = new ArrayList<>();
        for (int i = 0; i < groupNumArr.length; i++) {
            int finalI = i;
            Map argss = new HashMap();
            argss.put("stat", stat);
            argss.put("groupNum", groupNumArr[finalI]);
            argMapList.add(argss);
        }
        this.dao._batchUpdate("update questiongroup set stat={stat} where groupNum={groupNum} ", argMapList);
    }

    public List<Map<String, String>> updateShensuStatus(String exampaperNum, String questionNum, String stat, String alltype) {
        Map args = new HashMap();
        args.put("stat", stat);
        args.put("questionNum", questionNum);
        args.put("exampaperNum", exampaperNum);
        if ("0".equals(stat)) {
            if ("all".equals(alltype)) {
                this.dao._execute("update questiongroup_question set shensuStatus={stat} where exampaperNum={exampaperNum} ", args);
                return null;
            }
            this.dao._execute("update questiongroup_question set shensuStatus={stat} where questionNum={questionNum} ", args);
            return null;
        }
        if ("1".equals(stat)) {
            List<Map<String, String>> paramList = new ArrayList<>();
            if ("all".equals(alltype)) {
                this.dao._queryMapList("select questionNum,shensuYuzhi from questiongroup_question where exampaperNum={exampaperNum} ", TypeEnum.StringString, args).forEach(ques -> {
                    String shensuYuzhi = (String) ques.get("shensuYuzhi");
                    if (StrUtil.isEmpty(shensuYuzhi)) {
                        shensuYuzhi = getChushiYuzhi((String) ques.get("questionNum"));
                    }
                    ques.put("stat", stat);
                    ques.put("shensuYuzhi", shensuYuzhi);
                    paramList.add(ques);
                });
            } else if ("all2".equals(alltype)) {
                this.dao._queryMapList("select qq.questionNum,qq.shensuYuzhi,qq.groupNum from questiongroup_question qq LEFT JOIN questiongroup_mark_setting q on qq.groupNum= q.groupNum where qq.exampaperNum={exampaperNum} AND makType='0' ", TypeEnum.StringString, args).forEach(ques2 -> {
                    String shensuYuzhi = (String) ques2.get("shensuYuzhi");
                    if (StrUtil.isEmpty(shensuYuzhi)) {
                        shensuYuzhi = getChushiYuzhi((String) ques2.get("questionNum"));
                    }
                    ques2.put("stat", stat);
                    ques2.put("shensuYuzhi", shensuYuzhi);
                    paramList.add(ques2);
                });
            } else {
                String shensuYuzhi = this.dao._queryStr("select shensuYuzhi from questiongroup_question where questionNum={questionNum} ", args);
                if (StrUtil.isEmpty(shensuYuzhi)) {
                    shensuYuzhi = getChushiYuzhi(questionNum);
                }
                Map<String, String> ques3 = new HashMap<>();
                ques3.put("stat", stat);
                ques3.put("shensuYuzhi", shensuYuzhi);
                ques3.put("questionNum", questionNum);
                paramList.add(ques3);
            }
            if (CollUtil.isNotEmpty(paramList)) {
                this.dao._batchExecute("update questiongroup_question set shensuStatus={stat},shensuYuzhi={shensuYuzhi} where questionNum={questionNum} ", paramList);
                return paramList;
            }
            return null;
        }
        return null;
    }

    public String getChushiYuzhi(String questionNum) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        String fullScore = this.dao._queryStr("select fullScore from define where id={questionNum} union select fullScore from subdefine where id={questionNum} ", args);
        BigDecimal chushiYuzhi = Convert.toBigDecimal(fullScore, BigDecimal.valueOf(0L)).multiply(BigDecimal.valueOf(0.2d)).subtract(BigDecimal.valueOf(0.5d)).setScale(1, 4);
        return Convert.toStr(chushiYuzhi.compareTo(BigDecimal.valueOf(0L)) == -1 ? BigDecimal.valueOf(0L) : chushiYuzhi, "0");
    }

    public void updateautoCommitforbidden(String exampaperNum, String groupNum, String stat, String alltype) {
        updateautoCommitdefault(exampaperNum, groupNum, "0".equals(stat) ? "1" : "0", alltype);
        Map args = new HashMap();
        args.put("stat", stat);
        if ("all".equals(alltype)) {
            args.put("exampaperNum", exampaperNum);
            this.dao._execute("update questiongroup set autoCommitForbid={stat} where exampaperNum={exampaperNum}", args);
            return;
        }
        new ArrayList();
        new ArrayList();
        String[] groupNumArr = groupNum.split("_");
        List<Map> argMapList = new ArrayList<>();
        for (int i = 0; i < groupNumArr.length; i++) {
            int finalI = i;
            Map argss = new HashMap();
            argss.put("stat", stat);
            argss.put("groupNum", groupNumArr[finalI]);
            argMapList.add(argss);
        }
        this.dao._batchUpdate("update questiongroup set autoCommitForbid={stat} where groupNum={groupNum} ", argMapList);
    }

    public void updateautoCommitdefault(String exampaperNum, String groupNum, String stat, String alltype) {
        if ("all".equals(alltype)) {
            Map args = new HashMap();
            args.put("stat", stat);
            args.put("exampaperNum", exampaperNum);
            this.dao._execute("update questiongroup set aotoCommitDefault={stat}  where exampaperNum={exampaperNum} ", args);
            return;
        }
        new ArrayList();
        String[] groupNumArr = groupNum.split("_");
        List<Map> argMapList = new ArrayList<>();
        for (int i = 0; i < groupNumArr.length; i++) {
            int finalI = i;
            Map args2 = new HashMap();
            args2.put("stat", stat);
            args2.put("groupNum", groupNumArr[finalI]);
            argMapList.add(args2);
        }
        this.dao._batchUpdate("update questiongroup set aotoCommitDefault={stat} where groupNum={groupNum} ", argMapList);
    }

    public void updatecorrectForbid(String exampaperNum, String groupNum, String stat, String alltype) {
        if ("all".equals(alltype)) {
            Map args = new HashMap();
            args.put("stat", stat);
            args.put("exampaperNum", exampaperNum);
            this.dao._execute("update questiongroup set correctForbid={stat} where exampaperNum={exampaperNum} ", args);
            return;
        }
        new ArrayList();
        String[] groupNumArr = groupNum.split("_");
        List<Map> argMapList = new ArrayList<>();
        for (int i = 0; i < groupNumArr.length; i++) {
            int finalI = i;
            Map args2 = new HashMap();
            args2.put("stat", stat);
            args2.put("groupNum", groupNumArr[finalI]);
            argMapList.add(args2);
        }
        this.dao._batchUpdate("update questiongroup set correctForbid={stat}  where groupNum={groupNum} ", argMapList);
    }

    public void updateYuejuanWay(String examPaperNum, String groupNum, String checkedArry) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        args.put("checkedArry", checkedArry);
        this.dao._execute("update questiongroup set yuejuanWay={checkedArry} where examPaperNum={examPaperNum} and groupNum={groupNum}", args);
    }

    public void updatejudgetype(String groupNum, String judgetype) {
        Map args = new HashMap();
        args.put("judgetype", judgetype);
        args.put("groupNum", groupNum);
        this.dao._execute("update questiongroup_mark_setting set judgetype={judgetype} where groupNum={groupNum} ", args);
    }

    public void updatejudgetype2(String groupNum, String judgetype) {
        String[] groupNumArr = groupNum.split("_");
        List<Map> argMapList = new ArrayList<>();
        for (String questionNum : groupNumArr) {
            Map args = new HashMap();
            args.put("judgetype", judgetype);
            args.put("questionNum", questionNum);
            argMapList.add(args);
        }
        this.dao._batchUpdate("update questiongroup_mark_setting set judgetype={judgetype}  where groupNum={questionNum} ", argMapList);
    }

    public void nm() {
        List list = this.dao.queryBeanList("SELECT count(1) totalnum,questionNum groupNum from task GROUP BY questionNum", QuestionGroup.class);
        List<Map> argMapList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            QuestionGroup d = (QuestionGroup) list.get(i);
            Map args = new HashMap();
            args.put("totalnum", d.getTotalnum());
            args.put("groupNum", d.getTotalnum());
            argMapList.add(args);
        }
        this.dao._batchUpdate("update questiongroup set totalnum={totalnum}  where  groupNum={groupNum} ", argMapList);
    }

    public static void main(String[] args) {
        QuestionGroupDaoImpl qd = new QuestionGroupDaoImpl();
        qd.nm();
    }

    public Integer saomiao(String exampaperNum, String scancompleted) {
        Map args = new HashMap();
        args.put("scancompleted", scancompleted);
        args.put("exampaperNum", exampaperNum);
        return Integer.valueOf(this.dao._execute("update questiongroup set scancompleted={scancompleted}  where exampaperNum={exampaperNum} ", args));
    }

    public Object huixiansaomiao(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        Object oo = this.dao._queryObject("select scancompleted FROM questiongroup where exampaperNum={exampaperNum}  limit 1", args);
        if (oo == null) {
            oo = 0;
        }
        return oo;
    }

    public List<School> getLeftSchool(String leftInputStr, String leiceng) {
        String leicengStr1 = "";
        String leicengStr2 = "";
        Map args = new HashMap();
        args.put("leftInputStr", "%" + leftInputStr + "%");
        if (leiceng != null && !leiceng.equals("") && !leiceng.equals("null")) {
            leicengStr1 = " LEFT JOIN (select DISTINCT sItemId schoolNum from statisticitem_school where topItemId={leiceng}  and statisticItem='01' ) t2 ON s.id = t2.schoolNum ";
            leicengStr2 = " and t2.schoolNum is not null ";
            args.put("leiceng", leiceng);
        }
        String sql = "select DISTINCT s.id,s.schoolName from  school s  LEFT JOIN schoolgroup sg ON sg.schoolNum = s.id " + leicengStr1 + "WHERE sg.id IS NULL AND s.isDelete = 'F' AND s.isDelete = 'F'" + leicengStr2 + "  AND s.schoolName LIKE {leftInputStr}order by s.schoolNum ";
        return this.dao._queryBeanList(sql, School.class, args);
    }

    public int addGroupSchool(Schoolgroup sg) {
        Map args = new HashMap();
        args.put("schoolGroupName", sg.getSchoolGroupName());
        args.put(Const.EXPORTREPORT_schoolNum, sg.getSchoolNum());
        args.put("schoolGroupNum", sg.getSchoolGroupNum());
        args.put("insertUser", sg.getInsertUser());
        args.put("updateUser", sg.getUpdateUser());
        return this.dao._execute("INSERT INTO schoolgroup (schoolGroupName,schoolNum,schoolGroupNum,insertUser,insertDate,updateUser,updateDate) VALUES ({schoolGroupName},{schoolNum},{schoolGroupNum} ,{insertUser},now(),{updateUser},now()) ON DUPLICATE KEY UPDATE schoolGroupName = {schoolGroupName},schoolNum = {schoolNum} ,schoolGroupNum = {schoolGroupNum},updateUser ={updateUser},updateDate = now()", args);
    }

    public int delGroupSchool(Schoolgroup sg) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, sg.getSchoolNum());
        args.put("schoolGroupNum", sg.getSchoolGroupNum());
        return this.dao._execute("DELETE FROM schoolgroup WHERE schoolNum ={schoolNum} AND schoolGroupNum = {schoolGroupNum}", args);
    }

    public List<Schoolgroup> getSchoolGroupData() {
        return this.dao.queryBeanList("SELECT schoolGroupNum,schoolGroupName FROM schoolgroup GROUP BY schoolGroupNum order by convert(schoolGroupName using gbk)", Schoolgroup.class);
    }

    public List<School> getGroupSchoolData(String schoolGroupNum, String rightInputStr, String leiceng) {
        Map args = new HashMap();
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leiceng != null && !leiceng.equals("") && !leiceng.equals("null")) {
            leicengStr1 = " LEFT JOIN (select DISTINCT sItemId schoolNum from statisticitem_school where topItemId={leiceng}  and statisticItem='01' ) t2 ON sch.id = t2.schoolNum ";
            leicengStr2 = " and t2.schoolNum is not null ";
            args.put("leiceng", leiceng);
        }
        String wSql = "";
        if (null != rightInputStr && !"".equals(rightInputStr)) {
            args.put("rightInputStr", "%" + rightInputStr + "%");
            wSql = " AND sch.schoolName LIKE {rightInputStr} ";
        }
        args.put("schoolGroupNum", schoolGroupNum);
        String sql = "SELECT sg.schoolNum,sch.schoolName FROM schoolgroup sg LEFT JOIN school sch ON sch.id = sg.schoolNum " + leicengStr1 + "WHERE sg.schoolGroupNum = {schoolGroupNum}" + wSql + leicengStr2;
        return this.dao._queryBeanList(sql, School.class, args);
    }

    public int deleteSchoolGroup(String schoolGroupNum) {
        Map args = new HashMap();
        args.put("schoolGroupNum", schoolGroupNum);
        return this.dao._execute("DELETE FROM schoolgroup WHERE schoolGroupNum = {schoolGroupNum}", args);
    }

    public Object isExistSchGroupName(String schoolGroupName, String schoolGroupNum) {
        Map args = new HashMap();
        args.put("schoolGroupName", schoolGroupName);
        return this.dao._queryObject("SELECT schoolGroupNum FROM schoolgroup WHERE schoolGroupName ={schoolGroupName} LIMIT 1", args);
    }

    public int updateSchGroupName(String schoolGroupName, String schoolGroupNum) {
        Map args = new HashMap();
        args.put("schoolGroupName", schoolGroupName);
        args.put("schoolGroupNum", schoolGroupNum);
        return this.dao._execute("UPDATE schoolgroup SET schoolGroupName ={schoolGroupName}  WHERE schoolGroupNum ={schoolGroupNum}", args);
    }

    public List<Map<String, Object>> getAutoSubjectInfo(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        String exampaperNum = this.dao._queryStr("select exampaperNum from exampaper where examNum={examNum}  and gradeNum={gradeNum}  and subjectNum={subjectNum} ", args);
        args.put("exampaperNum", exampaperNum);
        Object count = this.dao._queryObject("select count(1)  from exampaper where pexampaperNum={exampaperNum}", args);
        String sql = "select ex.subjectNum,s.subjectName from exampaper ex left join subject s on ex.subjectNum=s.subjectNum where  ex.pexampaperNum={exampaperNum} ";
        if (Convert.toInt(count, 0).intValue() > 1) {
            sql = "select ex.subjectNum,s.subjectName from exampaper ex left join subject s on ex.subjectNum=s.subjectNum where ex.exampaperNum!=ex.pexampaperNum and ex.pexampaperNum={exampaperNum} ";
        }
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    public String getAutoDistribuitType(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        Object exampaperNum = this.dao._queryObject("select pexampaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("exampaperNum", exampaperNum);
        String type = this.dao._queryStr("select distinct type from distributeauto where exampaperNum={exampaperNum}  and subjectNum={subjectNum} ", args);
        return type;
    }
}
