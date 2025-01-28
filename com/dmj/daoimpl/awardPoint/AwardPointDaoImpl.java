package com.dmj.daoimpl.awardPoint;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.AjaxData;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.ChooseScale;
import com.dmj.domain.PersonWorkRecord;
import com.dmj.domain.QuestionGroupInfo;
import com.dmj.domain.QuestionGroupTemp;
import com.dmj.domain.Remark;
import com.dmj.domain.Schoolgroup;
import com.dmj.domain.ScoreRule;
import com.dmj.domain.Task;
import com.dmj.domain.Testingcentre;
import com.dmj.domain.Userposition;
import com.dmj.domain.Workrecord;
import com.dmj.domain.config;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.domain.vo.QuestionGroup_question;
import com.dmj.domain.vo.TestingcentreDis;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.config.Configuration;
import com.zht.db.DbUtils;
import com.zht.db.ServiceFactory;
import com.zht.db.TypeEnum;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

/* loaded from: AwardPointDaoImpl.class */
public class AwardPointDaoImpl {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());
    private Logger log4j = Logger.getLogger(getClass());
    private static final Byte[] lock = new Byte[0];
    public static SystemService systemService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    private static volatile ConcurrentHashMap<String, Object> intoDbCNumMap = new ConcurrentHashMap<>();
    public static Map<String, PersonWorkRecord> personWorkRecordMap = new HashMap();

    public List<AwardPoint> getpanfen(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getStudentId());
        return this.dao2._queryBeanList("select      a.userType  from  questionGroup_user a    where   a.exampaperNum={ExampaperNum}  and   a.userNum={InsertUser}", AwardPoint.class, args);
    }

    public List GetInsertUser(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("getQuestionNum", aw.getQuestionNum());
        args.put("getStudentId", aw.getStudentId());
        return this.dao2._queryBeanList("select distinct insertUser from task where exampaperNum={ExampaperNum} and questionNum={QuestionNum}  and studentId={StudentId} '  ORDER BY insertUser ", Task.class, args);
    }

    public List listGroupNum(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryBeanList("select  distinct  a.groupNum  from    questiongroup_question    a  left  join   questiongroup_mark_setting  b on     a.groupNum=b.groupNum   where   a.exampaperNum={ExampaperNum}   and  a.questionNum={QuestionNum}    and   a.groupNum  is  not  null  ", AwardPoint.class, args);
    }

    public Integer caichong(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryInt(" select   distinct   a.questionNum  from remark  a   where  a.exampaperNum={ExampaperNum}   and userNum ={InsertUser} and   a.type='1'   ", args);
    }

    public Integer questionNumcai(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryInt(" select   distinct   a.questionNum  from remark  a   where a.exampaperNum={ExampaperNum}    and userNum ={InsertUser}  and a.type='2'   ", args);
    }

    public Object[] gettizhu(int exampaperNum, String insertUser) {
        String valueOf;
        StringBuffer rows = new StringBuffer();
        StringBuffer rowss = new StringBuffer();
        List list2 = new ArrayList();
        new ArrayList();
        List<AwardPoint> list4 = new ArrayList<>();
        String questionNum = "";
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("insertUser", insertUser);
        List templist1 = this.dao2._queryBeanList("SELECT u.*,q.groupName,q.groupType,q.correctForbid,q.status,d.* FROM   ( \tSELECT examPaperNum,groupNum,userNum FROM questiongroup_user  WHERE exampaperNum={exampaperNum} AND userNum={insertUser} and groupNum is not null \t)u\t\tLEFT JOIN (  \t\tSELECT q.examPaperNum,q.groupNum,q.total,q.groupName,q.grouptype,q.stat,ifnull(q.correctForbid,0) correctForbid,t.status   FROM questiongroup  q left join testquestion_define t on q.groupNum=t.groupNum WHERE q.exampaperNum={exampaperNum}   \t) q  ON u.examPaperNum=q.examPaperNum AND u.groupNum=q.groupNum \tLEFT JOIN ( \t\t\tSELECT * from( \t\t\t\tSELECT d.id,d.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,d.orderNum b,0 c,d.choosename choosename \t\t\t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id \t\t\t\twhere d.examPaperNum={exampaperNum}\tunion \t\t\t\tSELECT sb.id,sb.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,sb.orderNum b, d.orderNum c,ifnull(CONCAT(d1.id,sb.orderNum),'s') choosename \t\t\t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id \t\t\t\twhere sb.examPaperNum={exampaperNum}\t) d \t)d on q.groupNum=d.id ORDER BY d.a,d.b,d.c ", AwardPoint.class, args);
        List list1 = new ArrayList();
        for (int i = 0; i < templist1.size(); i++) {
            AwardPoint aw = (AwardPoint) templist1.get(i);
            String groupUser = aw.getGroupNum() + "-" + aw.getUserNum();
            if (personWorkRecordMap.containsKey(groupUser)) {
                PersonWorkRecord pwe = personWorkRecordMap.get(groupUser);
                aw.setPersonYiPan(pwe.getPersonYiPan());
                aw.setPersonYingPan(pwe.getPersonYingPan());
                aw.setGroupInCompltedTotal(pwe.getGroupInCompltedTotal());
                aw.setGroupInTotal(pwe.getGroupInTotal());
                aw.setCompletedTotal(pwe.getCompletedTotal());
                aw.setAllTotal(pwe.getAllTotal());
            }
            list1.add(aw);
        }
        String kmzj = this.dao2._queryStr("SELECT count(1) from questiongroup_user qu  INNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum  where e.pexamPaperNum={exampaperNum} and qu.userType='2' and qu.userNum={insertUser}", args);
        String pjstr = "";
        if (kmzj.equals("0")) {
            pjstr = "  right join  ( select  groupNum   from   questiongroup_user   where    examPaperNum={exampaperNum}    and   userType='1'  and  userNum={insertUser} )  b \ton  b.groupNum=c.groupNum    ";
        }
        rowss.append("  select    c.groupNum    from (SELECT groupNum ,questionNum,exampaperNum from questiongroup_question WHERE exampaperNum= {exampaperNum}) c\t  " + pjstr + "  LIMIT 0,1 ");
        List list5 = this.dao2._queryBeanList(rowss.toString(), AwardPoint.class, args);
        String againid = this.dao2._queryStr(" select if(count(1)=0,-11,a.id )  from  remark     a     where     a.exampaperNum={exampaperNum}    and     a.type='2'   and a.userNum ={insertUser}  LIMIT 0,1  ", args);
        if (againid != null && !againid.equals("-11")) {
            rows.append("select  rk.exampaperNum, rk.questionNum,df.questionNum groupName from  (select    distinct  exampaperNum, questionNum  from  remark       where  exampaperNum={exampaperNum}   and   type='2'   and userNum ={insertUser}) rk  left join (select a.id,a.questionNum from ((select id,questionnum from define where exampaperNum={exampaperNum} and questiontype='1') union all (select id,questionnum  from subdefine WHERE exampaperNum={exampaperNum} ))a   )df on df.id=rk.questionNum ORDER BY length(df.questionNum),convert(df.questionNum using gbk)  ");
            list2 = this.dao2._queryBeanList(rows.toString(), AwardPoint.class, args);
            for (int i2 = 0; i2 < list2.size(); i2++) {
                AwardPoint awardPoint = (AwardPoint) list2.get(i2);
                if (questionNum.equals("") || questionNum == "") {
                    valueOf = String.valueOf(awardPoint.getQuestionNum());
                } else {
                    valueOf = questionNum + Const.STRING_SEPERATOR + String.valueOf(awardPoint.getQuestionNum());
                }
                questionNum = valueOf;
            }
            String[] a = questionNum.split(Const.STRING_SEPERATOR);
            new AwardPoint();
            for (int j = 0; j < a.length; j++) {
                int fill = j;
                Map args1 = new HashMap();
                args1.put("exampaperNum", Integer.valueOf(exampaperNum));
                args1.put("insertUse", insertUser);
                args1.put("questionNum", a[fill]);
                list4.add((AwardPoint) this.dao2._queryBean("select   count(1) as count  from  remark     a    where  a.exampaperNum={exampaperNum}  and   a.type ='2'   and   a.userNum ={insertUse}   and a.questionNum={questionNum}' ORDER BY length(a.questionNum),convert(a.questionNum using gbk) ", AwardPoint.class, args1));
            }
        }
        return new Object[]{list4, list1, list2, list5};
    }

    public List caiquestionNum(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        return this.dao2._queryBeanList(" select   distinct a.questionNum  from remark  a     where   a.exampaperNum={ExampaperNum}   and   a.type='1'    ORDER BY length(a.questionNum),convert(a.questionNum using gbk)  ", AwardPoint.class, args);
    }

    public List chongquestionNum(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList(" select   distinct  a.exampaperNum, a.questionNum  from remark  a    where    a.exampaperNum={ExampaperNum}   and a.userNum ={InsertUser}  and    a.type='2'    ORDER BY length(a.questionNum),convert(a.questionNum using gbk) ", AwardPoint.class, args);
    }

    public Object[] caijue(int exampaperNum, String userNum) {
        String qu_sql;
        StringBuffer rows = new StringBuffer();
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("userNum", userNum);
        String kmzj = this.dao2._queryStr("SELECT count(1) from questiongroup_user qu  INNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum  where e.pexamPaperNum={exampaperNum} and qu.userType='2' and qu.userNum={userNum} ", args);
        String pjstr = "";
        if (kmzj.equals("0")) {
            pjstr = "  right  join  ( select  groupNum   from   questiongroup_user   where    examPaperNum={exampaperNum}    and   userType='1'  and  userNum={userNum} )  b \ton  b.groupNum=c.groupNum    ";
        }
        String count = this.dao2._queryStr("SELECT IF(qu.exampaperNum=e.pexamPaperNum,1,0) from questiongroup_user qu INNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum where e.pexamPaperNum={exampaperNum} and  qu.userNum={userNum} and qu.userType=2", args);
        if ("0".equals(count)) {
            qu_sql = "SELECT qu.groupNum,qu.questionNum from questiongroup_question qu INNER JOIN ( \tSELECT b.id from questiongroup_user qu INNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum \t\tleft JOIN( \t\t\t(select id,questionnum,exampaperNum,category from define where exampaperNum={exampaperNum} and questiontype='1')  \t\t\tunion all  \t\t\t(select id,questionnum,exampaperNum,category  from subdefine WHERE exampaperNum={exampaperNum} ) \t\t)b on qu.examPaperNum=b.category \t\twhere qu.userNum={userNum} and qu.userType=2 \t)a on qu.questionNum=a.id \twhere  qu.examPaperNum={exampaperNum} ";
        } else {
            qu_sql = "select   groupNum,questionNum  from   questiongroup_question  where   examPaperNum={exampaperNum} ";
        }
        rows.append("   select   distinct a.groupnum ,qp.groupType,a.exampaperNum, df.questionNum groupName,df.fullScore, \t   a.count1  as  count1 ,\t   a.count2  as  count2 , \t   IFNULL( a.count2/a.count1,0)*100 as  zb \t   from    ( " + qu_sql + "  )  c left join questiongroup qp on c.groupnum=qp.groupnum " + pjstr + "\t    left   join \t   (  \t    select exampaperNum,groupnum, IFNULL(COUNT(1)/count(distinct questionNum),0) as  count1, \t    IFNULL(COUNT(IF(status='T',1,NULL))/count(distinct questionNum),0)  as   count2 from  remark   a           where   a.type ='1'          and  a.examPaperNum={exampaperNum}    \t     group  by  a.groupnum   \t   )a  \t   on       c.groupnum=a.groupnum left join ( \t\t\tSELECT * from( \t\t\t\tSELECT d.id,d.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,d.orderNum b,0 c,d.choosename choosename,d.fullscore \t\t\t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id \t\t\t\twhere d.examPaperNum={exampaperNum}\tunion \t\t\t\tSELECT sb.id,sb.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,sb.orderNum b, d.orderNum c,ifnull(CONCAT(d1.id,sb.orderNum),'s') choosename,sb.fullscore \t\t\t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id \t\t\t\twhere sb.examPaperNum={exampaperNum}\t) d )df on df.id=a.groupnum \t   where     a.groupnum   is  not  null   order by df.a,df.b,df.c ");
        List list2 = this.dao2._queryBeanList(rows.toString(), AwardPoint.class, args);
        StringBuffer rowss = new StringBuffer();
        rowss.append("select    distinct  a.exampaperNum, a.questionNum  from  remark     a   where    a.exampaperNum={exampaperNum}  and a.userNum ={userNum}   and   a.type='2'   ORDER BY length(a.questionNum),convert(a.questionNum using gbk)  ");
        List list5 = this.dao2._queryBeanList(rowss.toString(), AwardPoint.class, args);
        return new Object[]{list2, list5};
    }

    public String caiImage(String questionNum, AwardPoint aw, String[] studentId) {
        String sql = "";
        List<Map> list1 = new ArrayList<>();
        for (int i = 0; i < studentId.length; i++) {
            sql = "select b.img as  tupian   from   questionimage  b where   b.examPaperNum={ExampaperNum}  and  b.questionNum={questionNum}   and  b.studentId={studentId}";
            int fill = i;
            Map args = new HashMap();
            args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
            args.put("questionNum", questionNum);
            args.put(Const.EXPORTREPORT_studentId, studentId[fill]);
            list1.add(args);
        }
        List<?> _queryColList = this.dao2._queryColList(sql, String.class, list1);
        if (null != _queryColList && _queryColList.size() > 0) {
            return (String) _queryColList.get(0);
        }
        return null;
    }

    public byte[] caitu(String insertNum, String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("insertNum", insertNum);
        List<byte[]> list = this.dao2._queryBlobList("select  img as img    from  remarkimg   b where   b.scoreId={scoreId}  and  b.insertUser={insertNum} ", args);
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public byte[] caituPaperComment(String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        return (byte[]) this.dao2._queryObject("select  img as img    from  remarkimg   b where   b.scoreId={scoreId}  LIMIT 0,1", args);
    }

    public byte[] caituchong(String questionNum, AwardPoint aw, String studentId) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        return (byte[]) this.dao2._queryObject("select  img as img    from  questioncommentimage   b where   b.examPaperNum={ExampaperNum}   and  b.questionNum={questionNum}  and  b.studentId={studentId}", args);
    }

    public byte[] getScoreImage2(String questionNum, AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("questionNum", questionNum);
        return (byte[]) this.dao2._queryObject("  select a.img as   rightquestion  from  answerquestionimage  a  where  a.examPaperNum={ExampaperNum} and a.questionNum={questionNum}", args);
    }

    public byte[] getScoreImage1(String questionNum, int exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("questionNum", questionNum);
        return (byte[]) this.dao2._queryObject("select  e.img as yuanquestion from exampaperquestionimage  e    where  e.exampaperNum={exampaperNum} and  e.questionNum={questionNum}", args);
    }

    public List queryTag(AwardPoint aw) {
        return this.dao2.queryBeanList("select  distinct   a.name,a.value as tagvalue  from data  a  where   a.type='13'", AwardPoint.class);
    }

    public Integer updateTask(String score, String id, AwardPoint aw) {
        Map args = new HashMap();
        args.put("score", score);
        args.put("InsertDate", aw.getInsertDate());
        args.put("InsertUser", aw.getInsertUser());
        args.put("IsException", aw.getIsException());
        args.put("id", id);
        return Integer.valueOf(this.dao2._execute("update task  a  set  a.questionScore={score},  a.updateTime={InsertDate}, a.updateUser={InsertUser}, a.status='T',    a.isException={IsException}   where  a.id={id}", args));
    }

    public Integer updateTaskyi(String questionNum, String time, String insertUser, String score, int id, AwardPoint aw) {
        Map args = new HashMap();
        args.put("score", score);
        args.put("InsertDate", aw.getInsertDate());
        args.put("InsertUser", aw.getInsertUser());
        args.put("IsException", aw.getIsException());
        args.put("Status", aw.getStatus());
        args.put("id", Integer.valueOf(id));
        return Integer.valueOf(this.dao2._execute("update task  a  set  a.questionScore={score},   a.updateTime={InsertDate}, a.updateUser={InsertUser},  a.isException={IsException},  a.status={Status}   where  a.id={id}", args));
    }

    public Integer update(AwardPoint aw) {
        try {
            this.dao2._execute("update task  a  set  a.questionScore={questionScore},  a.updateTime={insertDate}, a.updateUser={insertDate}, a.isException={isException},   a.status='T' where  a.id={id}", aw);
            this.dao2._execute("update score  a  set  a.questionScore={questionScore},a.isModify='T', a.insertUser={insertDate}  where  a.id={scoreId}", aw);
            return 2;
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("报错", e);
            throw new RuntimeException(e);
        }
    }

    public Integer updatet(String id, String score, String insertUser, String isException, String time, String scoreId, String taskUserNum, String makType, String judgerule) {
        String updateSql;
        String timesql = "";
        if (!"checkObj_value".equals(time)) {
            timesql = timesql + " ,a.updateTime={time}  ";
        }
        try {
            String sql1 = "update task  a  set  a.questionScore={score}, a.status='T',  a.insertUser={insertUser} ,a.updateUser={insertUser} " + timesql + "   where  a.id={id}";
            Map args1 = new HashMap();
            args1.put("time", time);
            args1.put("score", score);
            args1.put("insertUser", insertUser);
            args1.put("id", id);
            int a = this.dao2._execute(sql1, args1);
            int b = 0;
            if ("1".equals(makType)) {
                Map args = new HashMap();
                args.put("scoreId", scoreId);
                int noJustify = ((Long) this.dao2._queryObject("select /* shard_host_HG=Write */ count(1) from task where scoreId={scoreId} and status='F' ", args)).intValue();
                if (noJustify == 2) {
                    if ("0".equals(judgerule)) {
                        updateSql = "update task  set porder=case when (`status`='F')  then 10  end  where scoreId = {scoreId} and insertUser<>{insertUser} ";
                    } else {
                        updateSql = "update task  set porder=case when (`status`='F')  then 2  end  where scoreId = {scoreId} and insertUser<>{insertUser} ";
                    }
                    Map args2 = new HashMap();
                    args2.put("scoreId", score);
                    args2.put("insertUser", insertUser);
                    b = this.dao2._execute(updateSql, args2);
                }
            }
            int[] c = {a, b};
            String re = ArrayUtils.toString(c, Const.STRING_SEPERATOR);
            return Integer.valueOf("-1".equals(String.valueOf(re.indexOf("0"))) ? 1 : 0);
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("报错task打分", e);
            throw new RuntimeException(e);
        }
    }

    public Integer updates(String scoreId, String score, String insertUser, String time) {
        try {
            Map args = new HashMap();
            args.put("score", score);
            args.put("insertUser", insertUser);
            args.put("scoreId", scoreId);
            return Integer.valueOf(this.dao2._execute("update score  a  set  a.questionScore={score}, a.isModify='T', a.insertUser={insertUser}  where  a.id={scoreId}", args));
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("报错", e);
            throw new RuntimeException(e);
        }
    }

    public Integer updateTag(String tagvalue, String questionNum, AwardPoint aw) {
        Map args = new HashMap();
        args.put("tagvalue", tagvalue);
        args.put("ScoreId", aw.getScoreId());
        return Integer.valueOf(this.dao2._execute("update    tag   a  set a.tag={tagvalue}    where a.scoreId={ScoreId} ", args));
    }

    public Integer insertTag(AwardPoint awardPoint) {
        String sql = "INSERT INTO tag(scoreId,studentId,exampaperNum,questionNum,schoolNum,classNum,gradeNum,tag,insertUser,insertDate)  select id,studentId,exampaperNum,questionNum,schoolNum,classNum,gradeNum,'" + awardPoint.getTagvalue() + "','" + awardPoint.getInsertUser() + "',now() from  score  where  id={ScoreId}";
        Map args = new HashMap();
        args.put("ScoreId", awardPoint.getScoreId());
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    public Integer tagCount(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ScoreId", aw.getScoreId());
        args.put("Tagvalue", aw.getTagvalue());
        String s = this.dao2._queryObject("select  count(a.id) as count  from  tag  a    where a.scoreId={ScoreId} and a.tag={Tagvalue}", args).toString();
        return Integer.valueOf(Integer.parseInt(s));
    }

    public Integer biaoTag(AwardPoint aw, String id) {
        String sql;
        if (aw.getTag() == null || aw.getTag().equals("")) {
            if (aw.getGroupType().equals("2")) {
                sql = " update  task  a   set a.isException={IsException}, a.status={Status} , a.updateTime={UpdateTime}  WHERE a.groupNum={GroupNum} and a.insertUser={InsertUser}   and rownum={Rownum}";
            } else {
                sql = " update  task  a   set a.isException={IsException}, a.status={Status} , a.updateTime={UpdateTime},  a.insertUser={InsertUser}  WHERE  a.id={id}";
            }
        } else if (aw.getGroupType().equals("2")) {
            sql = " update  test_task  a   set a.isException={IsException}, a.status={Status} , a.updateTime={UpdateTime} WHERE a.groupNum={GroupNum} and a.insertUser={InsertUser}   and rownum={Rownum}";
        } else {
            sql = " update  test_task  a   set a.isException={IsException}, a.status={Status} , a.updateTime={UpdateTime},  a.insertUser={InsertUser}  WHERE  a.id={id}";
        }
        Map args = new HashMap();
        args.put("IsException", aw.getIsException());
        args.put("Status", aw.getScoreId());
        args.put("UpdateTime", aw.getUpdateTime());
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        args.put("id", id);
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    public Integer biaoremarkTag(AwardPoint aw) {
        Map args = new HashMap();
        args.put("Status", aw.getStatus());
        args.put("id", aw.getId());
        return Integer.valueOf(this.dao2._execute("update  remark  a   set    a.status={Status} WHERE  a.id={id}", args));
    }

    public Integer delteTag(AwardPoint aw, String id) {
        String sql;
        if (aw.getTag() == null || aw.getTag().equals("")) {
            if (aw.getGroupType().equals("2")) {
                sql = "  update  task  a   set a.isException='F',  a.status='F',a.rowNum='-1',a.questionScore=0,a.insertUser='-1'   WHERE a.groupNum={GroupNum} and rownum={Rownum} and a.insertUser={InsertUser} ";
            } else {
                sql = "  update  task  a   set a.isException='F',   a.status='F',a.rowNum='-1',a.questionScore=0,a.insertUser='-1'  WHERE  a.id={id} ";
            }
        } else if (aw.getGroupType().equals("2")) {
            sql = "  update  test_task  a   set a.isException='F',   a.status='F' ,a.rowNum='-1',a.questionScore=0,a.insertUser='-1' WHERE a.groupNum={GroupNum} and a.insertUser={InsertUser}   and rownum= {Rownum}";
        } else {
            sql = "  update  test_task  a   set a.isException='F',   a.status='F'  ,a.rowNum='-1',a.questionScore=0,a.insertUser='-1'  WHERE  a.id={id} ";
        }
        Map args = new HashMap();
        args.put("GroupNum", aw.getGroupNum());
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("id", id);
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    public Integer biaoremarkYi(AwardPoint aw, String id) {
        Map args = new HashMap();
        args.put("IsException", aw.getIsException());
        args.put("Status", aw.getStatus());
        args.put("UpdateTime", aw.getUpdateTime());
        args.put("InsertUser", aw.getInsertUser());
        args.put("id", id);
        return Integer.valueOf(this.dao2._execute("update  remark  a   set a.isException={IsException} ,  a.status={Status},  a.insertDate={UpdateTime},  a.insertUser= {InsertUser}  WHERE  a.id={id} ", args));
    }

    public Integer delteremarkYi(AwardPoint aw, String id) {
        Map args = new HashMap();
        args.put("IsException", aw.getIsException());
        args.put("Status", aw.getStatus());
        args.put("id", id);
        return Integer.valueOf(this.dao2._execute(" update  remark  a   set a.isException={IsException} ,  a.status={Status}  WHERE  a.id={id}", args));
    }

    public Integer delteremarkTag(AwardPoint aw) {
        Map args = new HashMap();
        args.put("Status", aw.getStatus());
        args.put("Id", aw.getId());
        return Integer.valueOf(this.dao2._execute("update  remark  a   set   a.status={Status}  WHERE  a. id={Id}", args));
    }

    public List ttizhuzb(AwardPoint aw, int makType) {
        String sql = " SELECT  IFNULL(COUNT(1),0) as  count1,IFNULL(COUNT(IF(insertUser='" + aw.getInsertUser() + "' ,1,NULL)),0)  as   completecount  FROM task  WHERE groupNum={GroupNum}  and  exampaperNum={ExampaperNum}  and status='T' ";
        Map args = new HashMap();
        args.put("InsertUser", aw.getInsertUser());
        args.put("GroupNum", aw.getGroupNum());
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        return this.dao2._queryBeanList(sql, AwardPoint.class, args);
    }

    public int usercomplete(String insertUser, String groupNum, int exampaperNum) {
        String sql = " SELECT  IFNULL(COUNT(IF(insertUser='" + insertUser + "',1,NULL)),0)  as   completecount  FROM task  WHERE exampaperNum={ExampaperNum}  and   groupNum={groupNum}  and status='T' and isException='F'";
        Map args = new HashMap();
        args.put("InsertUser", insertUser);
        args.put("ExampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        return this.dao2._queryInt(sql, args).intValue();
    }

    public List ttizhuzbzj(AwardPoint aw, String makType) {
        String sql = "SELECT z.groupNum,z.num,z.maktype,z.exampaperNum,IFNULL(z.t_num,0) as count2   ,IFNULL(y.d_num,0) as count1 ,IFNULL(y.p_num,0)  as  completecount    ,CASE  WHEN z.t_num IS NULL OR z.t_num=0 THEN 100 \tWHEN IFNULL(y.d_num,0)>IFNULL(z.t_num,0) THEN 100 ELSE ROUND(IFNULL(y.d_num,0)/IFNULL(z.t_num,0)*100,2) \tEND zb \t   FROM      (    SELECT s.groupNum, s.num,s.exampaperNum,m.makType,IF(m.makType='0',s.num,s.num*2) t_num    FROM    (  SELECT q.groupNum,q.exampaperNum,COUNT(s.id) num  FROM   (  SELECT q.groupNum,q.examPaperNum,q.questionNum  FROM \t(SELECT examPaperNum,groupNum,questionNum FROM questiongroup_question WHERE exampaperNum={ExampaperNum}\tAND groupNum={GroupNum}) q  ) q  LEFT join (select * from (SELECT id, questionNum,studentId,isAppend FROM score WHERE examPaperNum={ExampaperNum})r  where isAppend !='T') s ON s.questionNum=q.questionNum  GROUP BY q.groupNum   ) s    LEFT JOIN (SELECT groupNum,makType FROM questiongroup_mark_setting WHERE exampaperNum={ExampaperNum} AND groupNum={GroupNum}) m ON m.groupNum=s.groupNum  )z   LEFT JOIN(  SELECT groupNum,COUNT(1) d_num,COUNT(IF(insertUser='" + aw.getInsertUser() + "',1,NULL)) p_num  FROM task  WHERE exampaperNum={ExampaperNum} AND status='T' AND groupNum={GroupNum})y   ON z.groupNum = y.groupNum ";
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList(sql, AwardPoint.class, args);
    }

    public Integer sumscore(AwardPoint aw) {
        try {
            StringBuffer buffer = new StringBuffer();
            StringBuffer buffer2 = buffer.append(" select   IFNULL(totalnum,0) as  t_num  from  questiongroup    where   exampaperNum={ExampaperNum}   and   groupNum={GroupNum}");
            Map args = new HashMap();
            args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
            args.put("GroupNum", aw.getGroupNum());
            return this.dao2._queryInt(buffer2.toString(), args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List countzb(AwardPoint aw, String makType) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryBeanList("SELECT   IFNULL(z.t_num,0) as  count2,IFNULL(y.d_num,0) as  count1   from   ( SELECT s.groupNum, m.makType,IF(m.makType='0',s.num,s.num*2) t_num   FROM    (   SELECT q.groupNum,COUNT(s.id) num  FROM  (SELECT exampaperNum,groupNum,questionNum FROM questiongroup_question WHERE exampaperNum={ExampaperNum} and  groupNum={GroupNum} ) q  LEFT join (SELECT questionNum,studentId,isAppend,id FROM score WHERE examPaperNum={ExampaperNum}  qtype='1') s ON s.questionNum=q.questionNum   WHERE s.isAppend !='T'   ) s  LEFT JOIN (SELECT groupNum,makType FROM questiongroup_mark_setting WHERE exampaperNum={ExampaperNum}) m ON m.groupNum=s.groupNum)z   LEFT JOIN(  SELECT groupNum,COUNT(id) d_num  FROM task WHERE exampaperNum={ExampaperNum} AND status='T'  and  groupNum={GroupNum}  ) y  ON z.groupNum = y.groupNum  LEFT JOIN questiongroup q ON q.groupNum=z.groupNum ", AwardPoint.class, args);
    }

    public List<AwardPoint> questionNum(int exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        return this.dao2._queryBeanList("   select    b.questionNum from  questiongroup_question   b    where   b.examPaperNum={exampaperNum}  and b.groupNum={groupNum} limit 1", AwardPoint.class, args);
    }

    public AwardPoint adminTi(AwardPoint aw, String insertNum) {
        Map args = new HashMap();
        args.put("UserNum", aw.getUserNum());
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return (AwardPoint) this.dao2._queryBean("select count(1)  as count  from   task   b   left join   questionGroup_question   c  on    b.exampaperNum=c.exampaperNum  and   b.questionNum=c.questionNum  left  join (select  distinct a.userNum,a.groupNum,a.exampaperNum from questionGroup_user a  where a.userNum={UserNum} and a.exampaperNum={ExampaperNum}) r  on  r.exampaperNum=c.exampaperNum  and  r.groupNum=c.groupNum  where   r.groupNum={GroupNum} and  b.status='F'  and  b.insertUser={UserNum}", AwardPoint.class, args);
    }

    public Integer finsishping(AwardPoint aw, String questionNum) throws Exception {
        StringBuffer buffer = new StringBuffer();
        StringBuffer buffer2 = buffer.append(" SELECT  COUNT(1)  FROM   (   SELECT  id  FROM task   WHERE exampaperNum={ExampaperNum} AND groupNum={GroupNum} AND status='F' AND insertUser={InsertUser} )b");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryInt(buffer2.toString(), args);
    }

    public List getTaskHaveJusityByUpdateTime(int exampaperNum, String insertUser, String groupNum, String questionNum, String updateTime, String str, String groupType, String starttime, String endtime, String startscore, String endscore) throws Exception {
        StringBuffer buffer = new StringBuffer();
        String newNum = "";
        Map args = new HashMap();
        try {
            String updateStr = "";
            String timesql = "";
            if (!"".equals(updateTime)) {
                updateStr = "ne".equals(str) ? " and t.updateTime>{updateTime}" : " and t.updateTime<{updateTime} ";
            }
            if (null != starttime && null != endtime && !"".equals(starttime) && !"".equals(endtime)) {
                timesql = timesql + "  and t.updateTime>={starttime} and t.updateTime<={endtime} ";
            }
            String scoresql = "  ";
            if (null != startscore && null != endscore && !"".equals(startscore) && !"".equals(endscore)) {
                scoresql = scoresql + "  and t.questionScore>={startscore} and t.questionScore<={endscore} ";
            }
            if (!"2".equals(groupType)) {
                if ("ne".equals(str)) {
                    buffer = buffer.append(" SELECT t.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore  FROM task t left join define d on t.questionNum=d.id LEFT JOIN subdefine sd on t.questionnum=sd.id   LEFT JOIN   questiongroup qg on qg.groupNum=t.groupNum  WHERE t.groupNum = {groupNum}  AND   t.insertUser={insertUser} " + updateStr + timesql + scoresql + " and t.status='T' ORDER BY t.updateTime asc limit 1 ");
                } else if ("pre".equals(str)) {
                    buffer = buffer.append(" SELECT t.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore  FROM task t left join define d on t.questionNum=d.id LEFT JOIN subdefine sd on t.questionnum=sd.id  LEFT JOIN   questiongroup qg on qg.groupNum=t.groupNum  WHERE t.groupNum = {groupNum}  AND   t.insertUser= {insertUser} " + updateStr + timesql + scoresql + " and t.status='T' ORDER BY t.updateTime desc limit 1 ");
                } else if ("fb".equals(str)) {
                    buffer = buffer.append(" SELECT t.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore  FROM task t left join define d on t.questionNum=d.id LEFT JOIN subdefine sd on t.questionnum=sd.id  LEFT JOIN   questiongroup qg on qg.groupNum=t.groupNum  WHERE t.groupNum = {groupNum}  AND   t.insertUser= {insertUser} and t.updateTime={updateTime}  and t.status='T'  ORDER BY t.updateTime asc limit 1 ");
                }
            } else {
                newNum = questionNum;
                if (questionNum == null || "".equals(questionNum) || "null".equals(questionNum)) {
                    Map args1 = new HashMap();
                    args1.put("groupNum", groupNum);
                    newNum = this.dao2._queryStr("select questionNum from questiongroup_question where groupNum={groupNum} limit 1 ", args1);
                }
                if ("ne".equals(str)) {
                    buffer = buffer.append(" select t.*,subd.groupname,subd.fullscore from (     SELECT t.questionNum,t.`status`,t.id,t.scoreId,t.studentId,  t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime from task t INNER JOIN(     SELECT t.groupNum,t.studentId,userNum from task t where t.exampaperNum={exampaperNum} and t.questionNum={newNum} AND   t.insertUser={insertUser}  and t.status='T'    " + updateStr + timesql + scoresql + " ORDER BY updateTime asc limit 1   )t1 on t.groupNum=t1.groupNum and t.studentId=t1.studentId and t.userNum=t1.userNum where t.groupNum={groupNum} and t.insertUser={insertUser}  and t.status='T'  )t left join  (select id,questionNum groupname,ordernum,fullscore from subdefine where exampaperNum={exampaperNum}  AND pid={groupNum})subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
                } else if ("pre".equals(str)) {
                    buffer = buffer.append(" select t.*,subd.groupname,subd.fullscore from (     SELECT t.questionNum,t.`status`,t.id,t.scoreId,t.studentId,  t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime from task t INNER JOIN(     SELECT t.groupNum,t.studentId,userNum from task t where t.exampaperNum={exampaperNum} and t.questionNum={newNum} AND   t.insertUser={insertUser}  and t.status='T'    " + updateStr + timesql + scoresql + " ORDER BY updateTime desc limit 1   )t1 on t.groupNum=t1.groupNum and t.studentId=t1.studentId and t.userNum=t1.userNum where t.groupNum={groupNum} and t.insertUser={insertUser} and t.status='T'    )t left join  (select id,questionNum groupname,ordernum,fullscore from subdefine where exampaperNum={exampaperNum}  AND pid={groupNum})subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
                }
            }
            args.put("updateTime", updateTime);
            args.put("starttime", starttime);
            args.put("endtime", endtime);
            args.put("startscore", startscore);
            args.put("endscore", endscore);
            args.put("groupNum", groupNum);
            args.put("insertUser", insertUser);
            args.put("exampaperNum", Integer.valueOf(exampaperNum));
            args.put("newNum", newNum);
            args.put("groupNum", groupNum);
            args.put("insertUser", insertUser);
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List getTaskByUpdateTime(int exampaperNum, String insertUser, String groupNum, String updateTime, String str, String groupType) throws Exception {
        StringBuffer buffer = new StringBuffer();
        Map args = new HashMap();
        try {
            String updateStr = "";
            if (!"".equals(updateTime)) {
                updateStr = "ne".equals(str) ? " and t.updateTime>{updateTime}" : " and t.updateTime<{updateTime}";
            }
            if (!"2".equals(groupType)) {
                if ("ne".equals(str)) {
                    buffer = buffer.append(" SELECT /* shard_host_HG=Write */ t.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,qg.groupname  FROM task t  LEFT JOIN   questiongroup qg on qg.groupNum=t.groupNum  WHERE t.groupNum = {groupNum}  AND   t.insertUser={insertUser}" + updateStr + " and t.status='T' ORDER BY t.updateTime asc limit 1 ");
                } else if ("pre".equals(str)) {
                    buffer = buffer.append(" SELECT /* shard_host_HG=Write */ t.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,qg.groupname  FROM task t  LEFT JOIN   questiongroup qg on qg.groupNum=t.groupNum  WHERE t.groupNum = {groupNum}  AND   t.insertUser={insertUser}" + updateStr + " and t.status='T' ORDER BY t.updateTime desc limit 1 ");
                }
            } else if ("ne".equals(str)) {
                buffer = buffer.append(" SELECT /* shard_host_HG=Write */ t.*,subd.groupname  from task t INNER JOIN(  SELECT * from(     SELECT groupNum,studentId,updateTime from task where exampaperNum={exampaperNum} AND groupNum={groupNum}       AND   insertUser={insertUser} and status='T' GROUP BY studentId   )t where 1=1 " + updateStr + " ORDER BY updateTime asc limit 1 )t2 on t.groupNum=t2.groupNum and t.studentId=t2.studentId left join  (select id,questionNum groupname,ordernum from subdefine where exampaperNum={exampaperNum}  AND pid={groupNum})subd on t.questionNum=subd.id   order  by  subd.ordernum   asc  ");
            } else if ("pre".equals(str)) {
                buffer = buffer.append(" SELECT /* shard_host_HG=Write */ t.*,subd.groupname  from task t INNER JOIN(  SELECT * from(     SELECT groupNum,studentId,updateTime from task where exampaperNum={exampaperNum} AND groupNum={groupNum}       AND   insertUser={insertUser} and status='T' GROUP BY studentId   )t where 1=1 " + updateStr + " ORDER BY updateTime desc limit 1 )t2 on t.groupNum=t2.groupNum and t.studentId=t2.studentId left join  (select id,questionNum groupname,ordernum from subdefine where exampaperNum={exampaperNum}  AND pid={groupNum})subd on t.questionNum=subd.id   order  by  subd.ordernum   asc  ");
            }
            args.put("updateTime", updateTime);
            args.put("groupNum", groupNum);
            args.put("insertUser", insertUser);
            args.put("exampaperNum", Integer.valueOf(exampaperNum));
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public String scanStatus(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        Object statu = this.dao2._queryObject("SELECT /* shard_host_HG=Write */ scancompleted from questiongroup WHERE groupNum={groupNum}", args);
        if (statu != null) {
            return String.valueOf(statu);
        }
        return "";
    }

    public Integer getShengXiaoTask(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        Object count1 = this.dao2._queryObject("SELECT id from task WHERE groupNum={groupNum} and status='F' limit 1 ", args);
        if (count1 == null || "".equals(count1)) {
            return 0;
        }
        return 1;
    }

    public String cantrecognized(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        String count1 = this.dao2._queryStr("SELECT a.count from (  select r.page,count( DISTINCT r.studentid) count from cantrecognized c   INNER JOIN regexaminee r on c.regId=r.id    where c.examPaperNum={exampaperNum} GROUP BY r.page) a   INNER join (    \tselect IFNULL(d.page,d1.page) page from define d LEFT JOIN define d1 on d.choosename=d1.id where d.id={groupNum} UNION   \tselect page from subdefine where id={groupNum} ) b on b.page=a.page", args);
        if (StrUtil.isEmpty(count1)) {
            count1 = "0";
        }
        String questionNum = groupNum;
        String merge = this.dao2._queryStr("SELECT ifnull(merge,0) from define where id={groupNum}", args);
        if ("1".equals(merge)) {
            questionNum = this.dao2._queryStr("SELECT sb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id where d.id={groupNum} limit 1", args);
        }
        args.put("questionNum", questionNum);
        String count2 = this.dao2._queryStr("SELECT count(DISTINCT s.studentId) from( \tselect * from cantrecognized where examPaperNum={exampaperNum} \t)c INNER JOIN score s on c.examPaperNum=s.examPaperNum and c.regId=s.regId \t where questionNum={questionNum}", args);
        if (StrUtil.isEmpty(count2)) {
            count2 = "0";
        }
        if (count1.equals(count2)) {
            return "0";
        }
        return "";
    }

    public String getRestTask(int exampaperNum, String insertUser, String groupNum, String groupType, String makType) {
        String sql;
        String enforce = systemService.fenzu(String.valueOf(exampaperNum));
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        if ("0".equals(enforce)) {
            sql = "select /* shard_host_HG=Write */ id from task where groupNum={groupNum} and insertUser<>-1 and `status`='F' limit 1 ";
        } else {
            String schoolGroupNum = this.dao2._queryStr("SELECT slg.schoolGroupNum FROM USER u INNER JOIN schoolgroup slg ON u.schoolNum = slg.schoolNum WHERE u.id = {insertUser} AND u.userType = 1 ", args);
            args.put("schoolGroupNum", schoolGroupNum);
            sql = "select /* shard_host_HG=Write */ t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum where t.groupNum={groupNum} and t.insertUser<>-1 and slg.schoolGroupNum={schoolGroupNum} and t.`status`='F' limit 1";
        }
        String count1 = this.dao2._queryStr(sql, args);
        if (count1 == null || "".equals(count1)) {
            return "0";
        }
        return "1";
    }

    public Integer otherTeacherHasQum(int exampaperNum, String insertUser, String groupNum, String groupType, String makType) throws Exception {
        String sql = "SELECT IFNULL(e.xuankaoqufen,1) from( \tSELECT category from define where id={groupNum}  union SELECT category from subdefine where id={groupNum}\t)d  INNER JOIN exampaper e on d.category=e.examPaperNum";
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        String xuankaoqufen = this.dao2._queryStr(sql, args);
        String enforce = systemService.fenzu(String.valueOf(exampaperNum));
        if ("0".equals(enforce)) {
            if ("0".equals(makType)) {
                if ("1".equals(xuankaoqufen)) {
                    sql = "select /* shard_host_HG=Write */ id from task where examPaperNum={exampaperNum} and groupNum={groupNum} and insertUser<>-1 and `status`='F' limit 1 ";
                } else if ("2".equals(xuankaoqufen)) {
                    sql = "select /* shard_host_HG=Write */ t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum where t.groupNum={groupNum} and t.insertUser<>-1 and t.`status`='F' limit 1";
                } else {
                    sql = "SELECT /* shard_host_HG=Write */ t.id from task t LEFT JOIN(select t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum where t.groupNum={groupNum} and t.insertUser<>-1 and t.`status`='F')t1 on t.id=t1.id where t.groupNum={groupNum}  and t.insertUser<>-1 and t.`status`='F' and t1.id is null limit 1";
                }
            } else if ("1".equals(makType)) {
                if ("1".equals(xuankaoqufen)) {
                    sql = " SELECT /* shard_host_HG=Write */ r.id FROM(SELECT id, scoreId FROM task WHERE  groupNum={groupNum}  AND insertUser<>'-1' and insertUser<>{insertUser} and status='F' GROUP BY scoreId)r LEFT JOIN  (SELECT scoreId  FROM task  WHERE  groupNum={groupNum}  AND insertUser={insertUser} and `status`='T')t ON r.scoreId = t.scoreId WHERE t.scoreId IS NULL limit 1";
                } else if ("2".equals(xuankaoqufen)) {
                    sql = " SELECT /* shard_host_HG=Write */ r.id FROM(SELECT t.id, t.scoreId FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum WHERE  t.groupNum={groupNum}  AND t.insertUser<>'-1' and t.insertUser<>{insertUser} and t.status='F' GROUP BY t.scoreId)r LEFT JOIN  (SELECT scoreId  FROM task  WHERE  groupNum={groupNum}  AND insertUser={insertUser}  and `status`='T')t ON r.scoreId = t.scoreId WHERE t.scoreId IS NULL limit 1";
                } else {
                    sql = " SELECT /* shard_host_HG=Write */ r.id FROM(SELECT t.id,t.scoreId from task t LEFT JOIN(SELECT t.id FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum WHERE  t.groupNum={groupNum}  AND t.insertUser<>'-1' and t.insertUser<>{insertUser} and t.status='F')t1 on t.id=t1.id WHERE  t.groupNum={groupNum}  AND t.insertUser<>'-1' and t.insertUser<>{insertUser} and t.status='F' and t1.id is NULL  GROUP BY t.scoreId)r LEFT JOIN  (SELECT scoreId  FROM task  WHERE  groupNum={groupNum}  AND insertUser={insertUser}  and `status`='T')t ON r.scoreId = t.scoreId WHERE t.scoreId IS NULL limit 1 ";
                }
            }
        } else {
            sql = "SELECT slg.schoolGroupNum FROM USER u INNER JOIN schoolgroup slg ON u.schoolNum = slg.schoolNum WHERE u.id = {insertUser} AND u.userType = 1 ";
            String schoolGroupNum = this.dao2._queryStr(sql, args);
            args.put("schoolGroupNum", schoolGroupNum);
            if ("0".equals(makType)) {
                if ("1".equals(xuankaoqufen)) {
                    sql = "select /* shard_host_HG=Write */ t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum where t.exampaperNum={exampaperNum} and t.groupNum={groupNum} and t.insertUser<>-1 and slg.schoolGroupNum={schoolGroupNum} and t.`status`='F' limit 1";
                } else if ("2".equals(xuankaoqufen)) {
                    sql = "select /* shard_host_HG=Write */ t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum where t.groupNum={groupNum} and t.insertUser<>-1 and slg.schoolGroupNum={schoolGroupNum} and t.`status`='F' limit 1 ";
                } else {
                    sql = "SELECT /* shard_host_HG=Write */ t.id from task t LEFT JOIN(select t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum where t.groupNum={groupNum}  and t.insertUser<>-1 and slg.schoolGroupNum={schoolGroupNum} and t.`status`='F')t1 on t.id=t1.id where t.groupNum={groupNum}  and t.insertUser<>-1 and slg.schoolGroupNum={schoolGroupNum} and t.`status`='F' and t1.id is null limit 1 ";
                }
            } else if ("1".equals(makType)) {
                if ("1".equals(xuankaoqufen)) {
                    sql = " SELECT /* shard_host_HG=Write */ r.id FROM(SELECT t.id, t.scoreId FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser<>'-1' and t.insertUser<>{insertUser} and slg.schoolGroupNum={schoolGroupNum} and t.status='F' GROUP BY t.scoreId)r LEFT JOIN  (SELECT t.scoreId  FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser={insertUser} and t.`status`='T')t ON r.scoreId = t.scoreId WHERE t.scoreId IS NULL limit 1";
                } else if ("2".equals(xuankaoqufen)) {
                    sql = "SELECT /* shard_host_HG=Write */ r.id FROM(SELECT t.id, t.scoreId FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser<>'-1' and t.insertUser<>{insertUser} and t.status='F' and slg.schoolGroupNum={schoolGroupNum} GROUP BY t.scoreId)r LEFT JOIN  (SELECT t.scoreId  FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser={insertUser} and t.`status`='T')t ON r.scoreId = t.scoreId WHERE t.scoreId IS NULL limit 1";
                } else {
                    sql = "SELECT /* shard_host_HG=Write */ r.id FROM(SELECT t.id,t.scoreId from task t LEFT join(SELECT t.id FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser<>'-1' and t.insertUser<>{insertUser} and t.status='F' and slg.schoolGroupNum={schoolGroupNum} )t1 on t.id=t1.id INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser<>'-1' and t.insertUser<>{insertUser} and t.status='F' and slg.schoolGroupNum={schoolGroupNum} and t1.id is NULL GROUP BY t.scoreId)r LEFT JOIN  (SELECT t.scoreId  FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser={insertUser} and t.`status`='T')t ON r.scoreId = t.scoreId WHERE t.scoreId IS NULL limit 1";
                }
            }
        }
        Object count1 = this.dao2._queryObject(sql, args);
        if (count1 == null || "".equals(count1)) {
            return 0;
        }
        return 1;
    }

    public Integer openTestDisQum(int exampaperNum, String insertUser, String groupNum, String groupType, String makType) throws Exception {
        String sql;
        String count2;
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("insertUser", insertUser);
        String xuankaoqufen = this.dao2._queryStr("SELECT IFNULL(e.xuankaoqufen,1) from( \tSELECT category from define where id={groupNum} union SELECT category from subdefine where id={groupNum} )d  INNER JOIN exampaper e on d.category=e.examPaperNum", args);
        String enforce = systemService.fenzu(String.valueOf(exampaperNum));
        if ("0".equals(enforce)) {
            if ("1".equals(xuankaoqufen)) {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE tc.examPaperNum={exampaperNum} and t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 limit 1";
            } else if ("2".equals(xuankaoqufen)) {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 limit 1";
            } else {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t left JOIN(SELECT t.id from task t INNER JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1)t1 on t.id=t1.id INNER JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 and t1.id is null limit 1";
            }
        } else {
            String schoolGroupNum = this.dao2._queryStr("SELECT slg.schoolGroupNum FROM USER u INNER JOIN schoolgroup slg ON u.schoolNum = slg.schoolNum WHERE u.id = {insertUser} AND u.userType = 1 ", args);
            args.put("schoolGroupNum", schoolGroupNum);
            if ("1".equals(xuankaoqufen)) {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE tc.exampaperNum={exampaperNum} and t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 and slg.schoolGroupNum={schoolGroupNum} limit 1";
            } else if ("2".equals(xuankaoqufen)) {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId  WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 and slg.schoolGroupNum={schoolGroupNum} limit 1";
            } else {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t LEFT JOIN(SELECT t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 and slg.schoolGroupNum={schoolGroupNum})t1 on t.id=t1.id LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 and slg.schoolGroupNum={schoolGroupNum} and t1.id is null limit 1";
            }
        }
        config con = systemService.getOpeFromConfig("24");
        int conPara = null == con ? 0 : Convert.toInt(con.getPara(), 0).intValue();
        if ("1".equals(enforce) || conPara == 0) {
            count2 = this.dao2._queryStr("SELECT  t.id from task t LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId LEFT JOIN student st on st.id=t.studentId WHERE tc.examPaperNum={exampaperNum} and t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 and st.id is null limit 1", args);
        } else {
            count2 = "";
        }
        if (count2 == null || "".equals(count2)) {
            Object count1 = this.dao2._queryObject(sql, args);
            if (count1 == null || "".equals(count1)) {
                return 0;
            }
            return 1;
        }
        return 2;
    }

    public Integer closeTestDisQum(int exampaperNum, String insertUser, String groupNum, String groupType, String makType) throws Exception {
        String sql;
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        String xuankaoqufen = this.dao2._queryStr("SELECT IFNULL(e.xuankaoqufen,1) from( \tSELECT category from define where id={groupNum} union SELECT category from subdefine where id={groupNum}\t)d  INNER JOIN exampaper e on d.category=e.examPaperNum", args);
        String enforce = systemService.fenzu(String.valueOf(exampaperNum));
        if ("0".equals(enforce)) {
            if ("1".equals(xuankaoqufen)) {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t  LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0 limit 1";
            } else if ("2".equals(xuankaoqufen)) {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0 limit 1";
            } else {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t left JOIN(SELECT t.id from task t INNER JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0)t1 on t.id=t1.id INNER JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0 and t1.id is null limit 1";
            }
        } else {
            String schoolGroupNum = this.dao2._queryStr("SELECT slg.schoolGroupNum FROM USER u INNER JOIN schoolgroup slg ON u.schoolNum = slg.schoolNum WHERE u.id = {insertUser} AND u.userType = 1 ", args);
            args.put("schoolGroupNum", schoolGroupNum);
            if ("1".equals(xuankaoqufen)) {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0 and slg.schoolGroupNum={schoolGroupNum} limit 1";
            } else if ("2".equals(xuankaoqufen)) {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId  WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=01 and slg.schoolGroupNum={schoolGroupNum} limit 1";
            } else {
                sql = "SELECT /* shard_host_HG=Write */ t.id from task t LEFT JOIN(SELECT t.id from task t INNER JOIN student s on t.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0 and slg.schoolGroupNum={schoolGroupNum})t1 on t.id=t1.id LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0 and slg.schoolGroupNum={schoolGroupNum} and t1.id is null limit 1";
            }
        }
        Object count1 = this.dao2._queryObject(sql, args);
        if (count1 == null || "".equals(count1)) {
            return 0;
        }
        return 1;
    }

    public String getOtherChooseCount(String exampaperNum, String groupNum, String insertUser) throws Exception {
        String sql;
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("exampaperNum", exampaperNum);
        args.put("insertUser", insertUser);
        String xuankaoqufen = this.dao2._queryStr("SELECT IFNULL(e.xuankaoqufen,1) from( \tSELECT category from define where id={groupNum} union SELECT category from subdefine where id={groupNum}\t)d  INNER JOIN exampaper e on d.category=e.examPaperNum", args);
        args.put("xuankaoqufen", xuankaoqufen);
        String enforce = systemService.fenzu(String.valueOf(exampaperNum));
        if ("0".equals(enforce)) {
            sql = "SELECT cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num from task t inner JOIN( \tSELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={groupNum}\t\tunion  \t\tSELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename \t\tLEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum \t\twhere sb.id={groupNum}\t)d on t.groupNum=d.id \twhere t.exampaperNum={exampaperNum} and t.status='F' and t.xuankaoqufen={xuankaoqufen}";
        } else {
            String schoolGroupNum = this.dao2._queryStr("SELECT slg.schoolGroupNum FROM USER u INNER JOIN schoolgroup slg ON u.schoolNum = slg.schoolNum WHERE u.id = {insertUser} AND u.userType = 1 ", args);
            args.put("schoolGroupNum", schoolGroupNum);
            sql = "SELECT cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num from task t inner JOIN( \tSELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={groupNum}\t\tunion  \t\tSELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename \t\tLEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum \t\twhere sb.id={groupNum}\t)d on t.groupNum=d.id LEFT JOIN student s on t.studentId=s.id \tLEFT JOIN schoolgroup slg on s.schoolnum=slg.schoolNum \twhere t.exampaperNum={exampaperNum} and t.status='F' and t.xuankaoqufen=1 and slg.schoolGroupNum={schoolGroupNum}";
        }
        Object count1 = this.dao2._queryObject(sql, args);
        return String.valueOf(count1 == null ? "0" : count1);
    }

    public String getChooseNameType(String groupNum) throws Exception {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        return this.dao2._queryStr("SELECT d.choosename from define d where d.id={groupNum} union  SELECT d.choosename from subdefine sb left join define d  on sb.pid=d.id where sb.id={groupNum}", args);
    }

    public Integer getTaskNo(int exampaperNum, String insertUser, String groupNum, String groupType, String makType) throws Exception {
        String count2;
        String count3;
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        String xuankaoqufen = this.dao2._queryStr("SELECT IFNULL(e.xuankaoqufen,1) from( \tSELECT category from define where id={groupNum} union SELECT category from subdefine where id={groupNum}\t)d  INNER JOIN exampaper e on d.category=e.examPaperNum INNER JOIN `subject` sj on e.subjectNum=sj.subjectNum", args);
        args.put("xuankaoqufen", xuankaoqufen);
        String enforce = systemService.fenzu(String.valueOf(exampaperNum));
        String sql = "SELECT slg.schoolGroupNum FROM USER u INNER JOIN schoolgroup slg ON u.schoolNum = slg.schoolNum WHERE u.id = {insertUser} AND u.userType = 1 ";
        String schoolGroupNum = this.dao2._queryStr(sql, args);
        args.put("schoolGroupNum", schoolGroupNum);
        if ("0".equals(enforce)) {
            if ("1".equals(xuankaoqufen)) {
                if (!"1".equals(makType)) {
                    sql = "select count(1) from task where groupNum={groupNum} and insertUser<>-1 and `status`='F' ";
                } else if ("1".equals(makType)) {
                    sql = " SELECT count(1) FROM(SELECT id, scoreId FROM task WHERE  groupNum={groupNum}  AND insertUser<>'-1' and insertUser<>{insertUser} and status='F' GROUP BY scoreId)r LEFT JOIN  (SELECT scoreId  FROM task  WHERE  groupNum={groupNum}  AND insertUser={insertUser} and `status`='T')t ON r.scoreId = t.scoreId WHERE t.scoreId IS NULL";
                }
                count2 = this.dao2._queryStr("SELECT count(1) from task t  LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 ", args);
                count3 = this.dao2._queryStr("SELECT count(1) from task t  LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0 ", args);
            } else {
                if (!"1".equals(makType)) {
                    sql = "select count(1) from task t INNER JOIN student s on t.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and t.exampaperNum=e.pexampaperNum   and e.xuankaoqufen={xuankaoqufen} where t.groupNum={groupNum} and t.insertUser<>-1 and t.`status`='F' ";
                } else if ("1".equals(makType)) {
                    sql = " SELECT count(1) FROM(SELECT t.id, t.scoreId FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and t.exampaperNum=e.pexampaperNum   and e.xuankaoqufen={xuankaoqufen} WHERE  t.groupNum={groupNum}  AND t.insertUser<>'-1' and t.insertUser<>{insertUser} and t.status='F' GROUP BY t.scoreId)r LEFT JOIN  (SELECT t.scoreId  FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and t.exampaperNum=e.pexampaperNum   and e.xuankaoqufen={xuankaoqufen} WHERE  t.groupNum={groupNum}  AND t.insertUser={insertUser} and t.`status`='T')t ON r.scoreId = t.scoreId WHERE t.scoreId IS NULL";
                }
                count2 = this.dao2._queryStr("SELECT count(1) from task t  LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId  INNER JOIN student s on t.studentId=s.id  INNER JOIN levelstudent ls on s.id=ls.sid  INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and t.exampaperNum=e.pexampaperNum   and e.xuankaoqufen={xuankaoqufen} WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 ", args);
                count3 = this.dao2._queryStr("SELECT count(1) from task t  LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId INNER JOIN student s on t.studentId=s.id  INNER JOIN levelstudent ls on s.id=ls.sid  INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and t.exampaperNum=e.pexampaperNum   and e.xuankaoqufen={xuankaoqufen}  WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0 ", args);
            }
        } else if ("1".equals(xuankaoqufen)) {
            if (!"1".equals(makType)) {
                sql = "select count(1) from task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum where t.groupNum={groupNum} and t.insertUser<>-1 and slg.schoolGroupNum={schoolGroupNum} and t.`status`='F' ";
            } else if ("1".equals(makType)) {
                sql = " SELECT count(1) FROM(SELECT t.id, t.scoreId FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser<>'-1' and t.insertUser<>{insertUser} and t.status='F' GROUP BY t.scoreId)r LEFT JOIN  (SELECT t.scoreId  FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser={insertUser} and t.`status`='T')t ON r.scoreId = t.scoreId WHERE t.scoreId IS NULL";
            }
            count2 = this.dao2._queryStr("SELECT count(1) from task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 and slg.schoolGroupNum={schoolGroupNum} ", args);
            count3 = this.dao2._queryStr("SELECT count(1) from task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0 and slg.schoolGroupNum={schoolGroupNum} ", args);
        } else {
            if (!"1".equals(makType)) {
                sql = "select count(1) from task t INNER JOIN student s on t.studentId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum INNER JOIN levelstudent ls on s.id=ls.sid INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and t.exampaperNum=e.pexampaperNum   and e.xuankaoqufen={xuankaoqufen} where t.groupNum={groupNum} and t.insertUser<>-1 and slg.schoolGroupNum={schoolGroupNum} and t.`status`='F' ";
            } else if ("1".equals(makType)) {
                sql = "SELECT count(1) FROM(SELECT t.id, t.scoreId FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and t.exampaperNum=e.pexampaperNum   and e.xuankaoqufen={xuankaoqufen} INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser<>'-1' and t.insertUser<>{insertUser} and t.status='F' and slg.schoolGroupNum={schoolGroupNum} GROUP BY t.scoreId)r LEFT JOIN  (SELECT t.scoreId  FROM task t INNER JOIN student s on t.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and t.exampaperNum=e.pexampaperNum   and e.xuankaoqufen={xuankaoqufen} INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum WHERE  t.groupNum={groupNum}  AND t.insertUser={insertUser} and t.`status`='T' and slg.schoolGroupNum={schoolGroupNum})t ON r.scoreId = t.scoreId WHERE t.scoreId IS NULL";
            }
            count2 = this.dao2._queryStr("SELECT count(1) from task t INNER JOIN student s on t.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and t.exampaperNum=e.pexampaperNum   and e.xuankaoqufen={xuankaoqufen} INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId  WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=1 and slg.schoolGroupNum={schoolGroupNum} ", args);
            count3 = this.dao2._queryStr("SELECT count(1) from task t INNER JOIN student s on t.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid INNER JOIN exampaper e on ls.subjectNum=e.subjectNum and t.exampaperNum=e.pexampaperNum   and e.xuankaoqufen={xuankaoqufen} INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum LEFT JOIN testingcentredis tc on t.examPaperNum=tc.examPaperNum and t.testingCentreId=tc.testingCentreId WHERE t.groupNum={groupNum} and t.insertUser='-1' and tc.isDis=0 and slg.schoolGroupNum={schoolGroupNum} ", args);
        }
        Object count1 = this.dao2._queryObject(sql, args);
        String count = String.valueOf(count1);
        if (!"0".equals(count)) {
            return 1;
        }
        if (!"0".equals(String.valueOf(count2))) {
            return 2;
        }
        if (!"0".equals(String.valueOf(count3))) {
            return 3;
        }
        return 4;
    }

    public List getTask(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        StringBuffer buffer = new StringBuffer();
        Map args = new HashMap();
        try {
            args.put("exampaperNum", Integer.valueOf(exampaperNum));
            args.put("groupNum", groupNum);
            args.put("insertUser", insertUser);
            if (!"2".equals(groupType)) {
                buffer = buffer.append("select /* shard_host_HG=Write */ t.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,t.groupname,t.fullscore,'' score  from( SELECT t.fenfaDate,t.porder,t.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore   FROM task t  left join define d on t.questionNum=d.id LEFT JOIN subdefine sd on t.questionnum=sd.id LEFT JOIN   questiongroup qg on  qg.groupNum=t.groupNum  WHERE t.exampaperNum ={exampaperNum} and t.groupNum = {groupNum}  AND t.insertUser = {insertUser} AND t.STATUS = 'F' ) t ORDER BY t.porder ASC,t.fenfaDate DESC LIMIT 1; ");
            } else if ("2".equals(groupType)) {
                String studentId = this.dao2._queryStr("select /* shard_host_HG=Write */ studentId from task where exampaperNum={exampaperNum}  AND groupNum={groupNum} AND   insertUser={insertUser} and status='F' ORDER BY fenfaDate desc limit 1", args);
                args.put(Const.EXPORTREPORT_studentId, studentId);
                buffer = buffer.append(" select /* shard_host_HG=Write */ t.*,subd.groupname,subd.fullscore,'' score from (SELECT studentId,questionNum,`status`,id,scoreId,  rownum,questionScore,exampaperNum,isException,insertUser,userNum,groupNum,updateTime   FROM task  WHERE   exampaperNum={exampaperNum}  AND groupNum={groupNum}  AND   insertUser={insertUser} and studentId={studentId} and status='F')t left join  (select id,questionNum groupname,ordernum,fullscore from subdefine where exampaperNum={exampaperNum}  AND pid={groupNum})subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
            }
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public Integer getTaskPersonYiPan(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        String sql;
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        Object id = this.dao2._queryObject(" SELECT id from define where id= {groupNum} and choosename <>'s'  UNION  SELECT sd.id from subdefine sd LEFT JOIN define d on sd.pid=d.id  where sd.id={groupNum} and d.choosename <>'s'", args);
        Map args1 = new HashMap();
        args1.put("groupNum", groupNum);
        args1.put("insertUser", insertUser);
        args1.put("exampaperNum", Integer.valueOf(exampaperNum));
        if (id == null) {
            sql = "SELECT count( 1 ) count FROM task  WHERE groupNum ={groupNum} and insertUser={insertUser} AND STATUS = 'T' group by questionNum LIMIT 1 ";
        } else {
            sql = "SELECT ifnull(count(DISTINCT studentId),0) from task t inner JOIN( \tSELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename \t\twhere d.id={groupNum} and d.choosename <> 's' \t\tUNION \t\tSELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename \t\tLEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum \t\twhere sb.id={groupNum} and  d.choosename <>'s' \t)d on t.groupNum=d.id  \twhere t.examPaperNum={exampaperNum}  and t.`status`='T'  and t.insertUser={insertUser} ";
        }
        return Convert.toInt(this.dao2._queryInt(sql, args1), 0);
    }

    public List getTaskHaveJusity(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        StringBuffer buffer = new StringBuffer();
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        try {
            if (!"2".equals(groupType)) {
                buffer = buffer.append(" SELECT t.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore  FROM task t  LEFT JOIN define d on t.questionNum=d.id LEFT JOIN subdefine sd on t.questionnum=sd.id  left join questiongroup qg on qg.groupNum=t.groupNum  WHERE t.groupNum = {groupNum} AND t.insertUser = {insertUser} AND t.STATUS = 'T'  ORDER BY updateTime desc limit 1 ");
            } else if ("2".equals(groupType)) {
                int count = ((Long) this.dao2._queryObject("select count(1) from questiongroup_question where groupNum={groupNum}", args)).intValue();
                args.put("count", Integer.valueOf(count));
                buffer = buffer.append(" select t.*,subd.groupname,subd.fullscore from (SELECT studentId,questionNum,`status`,id,scoreId,  rownum,questionScore,exampaperNum,isException,insertUser,userNum,groupNum,updateTime  FROM task  WHERE   exampaperNum={exampaperNum}  AND groupNum={groupNum}  AND   insertUser={insertUser} and status='T' ORDER BY updateTime desc limit 0,{count})t left join  (select id,questionNum groupname,ordernum,fullscore from subdefine where exampaperNum= {exampaperNum}  AND pid={groupNum})subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
            }
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List getGoTaskHaveJusity(int exampaperNum, String insertUser, String groupNum, String questionNum, String groupType, int goNum, String starttime, String endtime, String startscore, String endscore) throws Exception {
        Map args = new HashMap();
        args.put("starttime", starttime);
        args.put("endtime", endtime);
        args.put("startscore", startscore);
        args.put("endscore", endscore);
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        args.put("goNum", Integer.valueOf(goNum - 1));
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        StringBuffer buffer = new StringBuffer();
        String timesql = "  ";
        if (null != starttime && null != endtime && !"".equals(starttime) && !"".equals(endtime)) {
            timesql = timesql + "  and t.updateTime>={starttime} and t.updateTime<={endtime}  ";
        }
        String scoresql = "  ";
        if (null != startscore && null != endscore && !"".equals(startscore) && !"".equals(endscore)) {
            scoresql = scoresql + "  and t.questionScore>={startscore} and t.questionScore<={endscore} ";
        }
        try {
            if (!"2".equals(groupType)) {
                buffer = buffer.append(" SELECT t.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore  FROM task t left join define d on t.questionNum=d.id LEFT JOIN subdefine sd on t.questionnum=sd.id  LEFT JOIN   questiongroup qg on qg.groupNum=t.groupNum  WHERE t.groupNum = {groupNum} AND t.insertUser = {insertUser} AND t.STATUS = 'T' " + timesql + scoresql + " ORDER BY updateTime asc limit {goNum},1 ");
            } else if ("2".equals(groupType)) {
                String newNum = questionNum;
                if (questionNum == null || "".equals(questionNum) || "null".equals(questionNum)) {
                    newNum = this.dao2._queryStr("select questionNum from questiongroup_question where groupNum={groupNum} limit 1 ", args);
                }
                args.put("newNum", newNum);
                buffer = buffer.append(" select t.*,subd.groupname,subd.fullscore from ( SELECT t.questionNum,t.`status`,t.id,t.scoreId,t.studentId,  t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime from task t INNER JOIN(    SELECT t.groupNum,t.studentId from task t where t.exampaperNum={exampaperNum} and t.questionNum={newNum} AND   t.insertUser={insertUser}  and t.status='T'        " + timesql + scoresql + " ORDER BY t.updateTime asc  limit  {goNum},1  )t1 on t.groupNum=t1.groupNum and t.studentId=t1.studentId where t.groupNum={groupNum} and t.insertUser={insertUser}  and t.status='T' )t left join  (select id,questionNum groupname,ordernum,fullscore from subdefine where exampaperNum={exampaperNum}  AND pid={groupNum})subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
            }
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List finishTaskById(int exampaperNum, String insertUser, String groupNum, String questionNum, String groupType, String id) throws Exception {
        Map args = new HashMap();
        args.put("id", id);
        return this.dao2._queryBeanList("SELECT t.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore from task t  LEFT JOIN define d on t.questionNum=d.id  LEFT JOIN subdefine sd on t.questionnum=sd.id   left join questiongroup qg on qg.groupNum=t.groupNum where t.id in ({id[]}) order by sd.ordernum ", AwardPoint.class, args);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public List getRemark(Map<String, String> map) throws Exception {
        String groupNum = map.get("groupNum");
        String groupType = map.get("groupType");
        String questionNum = map.get("questionNum");
        String userNum = map.get("userNum");
        String exampaperNum = map.get("exampaperNum");
        List remarkList = new ArrayList();
        String time = DateUtil.getCurrentTime2();
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("questionNum", questionNum);
        args.put("time", time);
        args.put("groupNum", groupNum);
        String isFenzu = systemService.fenzu(exampaperNum);
        if ("1".equals(isFenzu)) {
            String schoolGroupNum = String.valueOf(this.dao2._queryObject("SELECT IFNULL(slg.schoolGroupNum,-1) from user u LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum where u.id={userNum}", args) == null ? "-1" : this.dao2._queryObject("SELECT IFNULL(slg.schoolGroupNum,-1) from user u LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum where u.id={userNum}", args));
            args.put("schoolGroupNum", schoolGroupNum);
        }
        try {
            if ("1".equals(groupType)) {
                remarkList = this.dao2._queryBeanList("select r.*,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore,'' score from remark r left join define d on r.questionNum=d.id LEFT JOIN subdefine sd on r.questionnum=sd.id LEFT JOIN   questiongroup qg on  qg.groupNum=r.questionNum  where r.questionNum={questionNum} and r.status='F' and r.type='1' and r.insertUser={userNum} order by r.studentId limit 1", Remark.class, args);
                if (remarkList.size() == 0) {
                    remarkstealQuestion(exampaperNum, groupNum, userNum);
                    remarkList = this.dao2._queryBeanList("select r.*,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore,'' score from remark r left join define d on r.questionNum=d.id LEFT JOIN subdefine sd on r.questionnum=sd.id LEFT JOIN   questiongroup qg on  qg.groupNum=r.questionNum  where r.questionNum={questionNum} and r.status='F' and r.type='1' and r.insertUser={userNum} order by r.studentId limit 1", Remark.class, args);
                }
            } else {
                String studentId = this.dao2._queryStr("SELECT studentId from remark r where r.groupNum={groupNum} and r.status='F' and r.type='1' and r.insertUser={userNum} order by r.studentId limit 1", args);
                args.put(Const.EXPORTREPORT_studentId, studentId);
                remarkList = this.dao2._queryBeanList("select r.*,ifnull(d.questionNum,sd.questionNum) groupname,ifnull(d.fullscore,sd.fullscore) fullscore,'' score from remark r left join define d on r.questionNum=d.id LEFT JOIN subdefine sd on r.questionNum=sd.id  where r.groupNum={groupNum} and r.status='F' and r.type='1' and r.insertUser={userNum}  and r.studentId={studentId} order  by  ifnull(sd.ordernum,d.orderNum),r.studentId   asc ", Remark.class, args);
                if (remarkList.size() == 0) {
                    remarkstealQuestion(exampaperNum, groupNum, userNum);
                    String studentId2 = this.dao2._queryStr("SELECT studentId from remark r where r.groupNum={groupNum} and r.status='F' and r.type='1' and r.insertUser={userNum} order by r.studentId  limit 1", args);
                    args.put("studentId2", studentId2);
                    remarkList = this.dao2._queryBeanList("select r.*,ifnull(d.questionNum,sd.questionNum) groupname,ifnull(d.fullscore,sd.fullscore) fullscore,'' score from remark r left join define d on r.questionNum=d.id LEFT JOIN subdefine sd on r.questionNum=sd.id  where r.groupNum={groupNum} and r.status='F' and r.type='1' and r.insertUser={userNum}  and r.studentId={studentId2} order  by  ifnull(sd.ordernum,d.orderNum),r.studentId   asc ", Remark.class, args);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return remarkList;
    }

    public void remarkstealQuestion(String exampaperNum, String groupNum, String userNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        args.put("userNum", userNum);
        Object lock2 = null;
        try {
            if (intoDbCNumMap.containsKey(groupNum)) {
                lock2 = intoDbCNumMap.get(groupNum);
                synchronized (lock2) {
                    try {
                        lock2.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                lock2 = new Object();
            }
            intoDbCNumMap.put(groupNum, lock2);
            this.dao2._update("call remarkstealQuestion({exampaperNum},{groupNum},{userNum})", args);
            synchronized (lock2) {
                lock2.notifyAll();
                intoDbCNumMap.remove(groupNum);
            }
        } catch (Throwable th) {
            synchronized (lock2) {
                lock2.notifyAll();
                intoDbCNumMap.remove(groupNum);
                throw th;
            }
        }
    }

    public List getRemarkByUpdateTime(Map<String, String> map) throws Exception {
        map.get("examPaperNum");
        String questionNum = map.get("questionNum");
        String userNum = map.get("userNum");
        String updateTime = map.get("updateTime");
        String str = map.get("str");
        String updateStr = "";
        if (!"".equals(updateTime)) {
            updateStr = "ne".equals(str) ? " and t.updateTime>{updateTime}" : " and t.updateTime<{updateTime}";
        }
        StringBuffer buffer = new StringBuffer();
        if ("ne".equals(str)) {
            buffer = buffer.append(" SELECT t.*  FROM remark t   WHERE t.questionNum = {questionNum}  AND   t.insertUser={userNum}" + updateStr + " and t.type='1' and t.status='T' ORDER BY t.updateTime asc limit 1 ");
        } else if ("pre".equals(str)) {
            buffer = buffer.append(" SELECT t.*  FROM remark t  WHERE t.questionNum = {questionNum}  AND   t.insertUser={userNum} " + updateStr + " and t.type='1' and t.status='T' ORDER BY t.updateTime desc limit 1 ");
        }
        Map args = new HashMap();
        args.put("updateTime", updateTime);
        args.put("questionNum", questionNum);
        args.put("userNum", userNum);
        return this.dao2._queryBeanList(buffer.toString(), Remark.class, args);
    }

    public List getRemarkHaveJusity(Map<String, String> map) throws Exception {
        String exampaperNum = map.get("exampaperNum");
        String groupNum = map.get("groupNum");
        String groupType = map.get("groupType");
        String userNum = map.get("userNum");
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("userNum", userNum);
        args.put("exampaperNum", exampaperNum);
        StringBuffer buffer = new StringBuffer();
        try {
            if ("1".equals(groupType)) {
                buffer = buffer.append(" SELECT t.*,ifnull(d.questionNum,sd.questionNum) groupname,ifnull(d.fullscore,sd.fullscore) fullscore,'' score  FROM remark t left join define d on t.questionNum=d.id LEFT JOIN subdefine sd on t.questionnum=sd.id  WHERE t.groupNum = {groupNum} AND t.insertUser = {userNum} and t.type='1' AND t.STATUS = 'T'  ORDER BY updateTime desc limit 1");
            } else {
                int count = ((Long) this.dao2._queryObject("select count(1) from questiongroup_question where groupNum={groupNum} ", args)).intValue();
                args.put("count", Integer.valueOf(count));
                buffer = buffer.append(" select t.*,subd.groupname,subd.fullscore from (SELECT questionNum,`status`,id,scoreId,  rownum,questionScore,exampaperNum,isException,insertUser,userNum,groupNum,updateTime   FROM remark  WHERE groupNum={groupNum}  AND   insertUser={userNum} and status='T' ORDER BY updateTime desc limit 0,{count})t left join  (select id,questionNum groupname,ordernum,fullscore from subdefine where exampaperNum={exampaperNum} AND pid={groupNum})subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
            }
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        return this.dao2._queryBeanList(buffer.toString(), Remark.class, args);
    }

    public List getGoRemarkHaveJusity(Map<String, String> map) throws Exception {
        String exampaperNum = map.get("exampaperNum");
        String groupNum = map.get("groupNum");
        String groupType = map.get("groupType");
        String questionNum = map.get("questionNum");
        String userNum = map.get("userNum");
        map.get("updateTime");
        map.get("str");
        String starttime = map.get("starttime");
        String endtime = map.get("endtime");
        String startscore = map.get("startscore");
        String endscore = map.get("endscore");
        String goNum = map.get("goNum");
        Map args = new HashMap();
        args.put("starttime", starttime);
        args.put("endtime", endtime);
        args.put("startscore", startscore);
        args.put("endscore", endscore);
        args.put("questionNum", questionNum);
        args.put("userNum", userNum);
        args.put("gonum", Integer.valueOf(Integer.parseInt(goNum) - 1));
        args.put("groupNum", groupNum);
        args.put("exampaperNum", exampaperNum);
        args.put("goNum", Integer.valueOf(Integer.parseInt(goNum) - 1));
        StringBuffer buffer = new StringBuffer();
        String timesql = "  ";
        if (null != starttime && null != endtime && !"".equals(starttime) && !"".equals(endtime)) {
            timesql = timesql + "  and t.updateTime>={starttime} and t.updateTime<={endtime} ";
        }
        String scoresql = "  ";
        if (null != startscore && null != endscore && !"".equals(startscore) && !"".equals(endscore)) {
            scoresql = scoresql + "  and t.questionScore>={startscore} and t.questionScore<={endscore} ";
        }
        try {
            if ("1".equals(groupType)) {
                buffer = buffer.append(" SELECT t.*,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore,'' score  FROM remark t left join define d on t.questionNum=d.id LEFT JOIN subdefine sd on t.questionnum=sd.id LEFT JOIN   questiongroup qg on  qg.groupNum=t.questionNum  WHERE t.questionNum = {questionNum} AND t.insertUser = {userNum} and t.type='1' AND t.STATUS = 'T' " + timesql + scoresql + " ORDER BY updateTime asc limit {gonum},1 ");
            } else {
                String newNum = questionNum;
                if (questionNum == null || "".equals(questionNum) || "null".equals(questionNum)) {
                    newNum = this.dao2._queryStr("select questionNum from questiongroup_question where groupNum={groupNum} limit 1 ", args);
                }
                args.put("newNum", newNum);
                buffer = buffer.append(" select t.*,subd.groupname,subd.fullscore from ( SELECT t.questionNum,t.`status`,t.id,t.scoreId,s.studentId,  t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime from remark t left join score s on t.scoreId=s.id INNER JOIN(    SELECT t.groupNum,s.studentId from remark t left join score s on t.scoreId=s.id where t.exampaperNum={exampaperNum} and t.questionNum={newNum} AND   t.insertUser={userNum}  and t.status='T'        " + timesql + scoresql + " ORDER BY t.updateTime asc  limit {goNum},1  )t1 on t.groupNum=t1.groupNum and s.studentId=t1.studentId where t.groupNum={groupNum} and t.insertUser={userNum}  and t.status='T')t left join  (select id,questionNum groupname,ordernum,fullscore from subdefine where exampaperNum={exampaperNum} AND pid={groupNum} )subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
            }
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        return this.dao2._queryBeanList(buffer.toString(), Remark.class, args);
    }

    public List finishRemarkById(Map<String, String> map) throws Exception {
        String id = map.get("id");
        Map args = new HashMap();
        args.put("id", id);
        return this.dao2._queryBeanList("SELECT s.studentId,t.questionNum,t.`status`,t.id,t.scoreId,t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore from remark t  INNER JOIN score s on t.scoreId=s.id  LEFT JOIN define d on t.questionNum=d.id  LEFT JOIN subdefine sd on t.questionnum=sd.id   left join questiongroup qg on qg.groupNum=t.groupNum where t.id in ({id[]}) order by sd.ordernum ", Remark.class, args);
    }

    public List getRemarkyipanscore(Map<String, String> map) throws Exception {
        String exampaperNum = map.get("exampaperNum");
        String groupNum = map.get("groupNum");
        map.get("questionNum");
        String userNum = map.get("userNum");
        String starttime = map.get("starttime");
        String endtime = map.get("endtime");
        String startscore = map.get("startscore");
        String endscore = map.get("endscore");
        map.get("startval");
        map.get("indexval");
        Map args = new HashMap();
        args.put("starttime", starttime);
        args.put("endtime", endtime);
        args.put("startscore", startscore);
        args.put("endscore", endscore);
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        args.put("userNum", userNum);
        if (null != starttime && null != endtime && !"".equals(starttime) && !"".equals(endtime)) {
            String str = "  and r.updateTime>={starttime} and r.updateTime<={endtime} ";
        }
        if (null != startscore && null != endscore && !"".equals(startscore) && !"".equals(endscore)) {
            String str2 = "    and r.questionScore>={startscore} and r.questionScore<={endscore} ";
        }
        StringBuffer buffer = new StringBuffer();
        List resultList = this.dao2._queryBeanList(buffer.append("select t.studentId,t.updateTime,  GROUP_CONCAT(t.id ORDER BY subd.ordernum) id,  GROUP_CONCAT(scoreid ORDER BY subd.ordernum) scoreId,  GROUP_CONCAT(questionNum ORDER BY subd.ordernum) questionNum, GROUP_CONCAT(questionScore ORDER BY subd.ordernum) questionScore from (  select * from remark where exampaperNum={exampaperNum} and groupNum={groupNum} and insertUser={userNum}  and status='T' )t  left join  (  (select  id,ordernum,questionNum groupname  from define where exampaperNum={exampaperNum} and questiontype=1)    UNION ALL    (select id,ordernum,questionNum groupname from subdefine  where exampaperNum={exampaperNum} )  )subd  on subd.id=t.questionNum   GROUP BY t.studentId,userNum  order  by  t.updateTime asc,subd.ordernum asc ").toString(), Remark.class, args);
        for (int k = 0; k < resultList.size(); k++) {
            Remark remark = (Remark) resultList.get(k);
            remark.setIndex(String.valueOf(k + 1));
            List tempList = new ArrayList();
            for (int p = 0; p < remark.getScoreId().split(Const.STRING_SEPERATOR).length; p++) {
                Remark ap = new Remark();
                ap.setScoreId(remark.getScoreId().split(Const.STRING_SEPERATOR)[p]);
                ap.setQuestionNum(remark.getQuestionNum().split(Const.STRING_SEPERATOR)[p]);
                ap.setQuestionScore(String.valueOf(remark.getQuestionScore()).split(Const.STRING_SEPERATOR)[p]);
                tempList.add(ap);
            }
            remark.setaList(tempList);
        }
        return resultList;
    }

    public List getRemarkHaveJusityByUpdateTime(Map<String, String> map) throws Exception {
        String exampaperNum = map.get("exampaperNum");
        String groupNum = map.get("groupNum");
        String groupType = map.get("groupType");
        String questionNum = map.get("questionNum");
        String userNum = map.get("userNum");
        String updateTime = map.get("updateTime");
        String str = map.get("upordown");
        String starttime = map.get("starttime");
        String endtime = map.get("endtime");
        String startscore = map.get("startscore");
        String endscore = map.get("endscore");
        Map args = new HashMap();
        args.put("updateTime", updateTime);
        args.put("starttime", starttime);
        args.put("endtime", endtime);
        args.put("startscore", startscore);
        args.put("endscore", endscore);
        args.put("groupNum", groupNum);
        args.put("userNum", userNum);
        args.put("exampaperNum", exampaperNum);
        StringBuffer buffer = new StringBuffer();
        try {
            String updateStr = "";
            String timesql = "";
            if (!"".equals(updateTime)) {
                updateStr = "ne".equals(str) ? " and t.updateTime>{updateTime}" : " and t.updateTime<{updateTime}";
            }
            if (null != starttime && null != endtime && !"".equals(starttime) && !"".equals(endtime)) {
                timesql = timesql + "  and t.updateTime>={starttime} and t.updateTime<={endtime} ";
            }
            String scoresql = "  ";
            if (null != startscore && null != endscore && !"".equals(startscore) && !"".equals(endscore)) {
                scoresql = scoresql + "  and t.questionScore>={startscore} and t.questionScore<={endscore} ";
            }
            if ("1".equals(groupType)) {
                if ("ne".equals(str)) {
                    buffer = buffer.append(" SELECT t.*,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore,'' score   FROM remark t left join define d on t.questionNum=d.id LEFT JOIN subdefine sd on t.questionnum=sd.id LEFT JOIN   questiongroup qg on  qg.groupNum=t.questionNum  WHERE t.groupNum = {groupNum}  AND   t.insertUser={userNum}" + updateStr + timesql + scoresql + " and t.type='1' and t.status='T' ORDER BY t.updateTime asc limit 1 ");
                } else if ("pre".equals(str)) {
                    buffer = buffer.append(" SELECT t.*,qg.groupname,ifnull(d.fullscore,sd.fullscore) fullscore,'' score   FROM remark t left join define d on t.questionNum=d.id LEFT JOIN subdefine sd on t.questionnum=sd.id LEFT JOIN   questiongroup qg on  qg.groupNum=t.questionNum  WHERE t.groupNum = {groupNum}  AND   t.insertUser={userNum}" + updateStr + timesql + scoresql + " and t.type='1' and t.status='T' ORDER BY t.updateTime desc limit 1 ");
                }
            } else {
                String newNum = questionNum;
                if (questionNum == null || "".equals(questionNum) || "null".equals(questionNum)) {
                    newNum = this.dao2._queryStr("select questionNum from questiongroup_question where groupNum={groupNum} limit 1 ", args);
                }
                args.put("newNum", newNum);
                if ("ne".equals(str)) {
                    buffer = buffer.append(" select t.*,subd.groupname,subd.fullscore from (     SELECT t.questionNum,t.`status`,t.id,t.scoreId,s.studentId,  t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime from remark t  left join score s on t.scoreId=s.id INNER JOIN(     SELECT t.groupNum,s.studentId from remark t left join score s on t.scoreId=s.id where t.exampaperNum={exampaperNum} and t.questionNum={newNum} AND   t.insertUser={userNum}  and t.status='T'    " + updateStr + timesql + scoresql + " ORDER BY updateTime asc limit 1   )t1 on t.groupNum=t1.groupNum and s.studentId=t1.studentId where t.groupNum={groupNum} and t.insertUser={userNum}  and t.status='T'  )t left join  (select id,questionNum groupname,ordernum,fullscore from subdefine where exampaperNum={exampaperNum}  AND pid={groupNum})subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
                } else if ("pre".equals(str)) {
                    buffer = buffer.append(" select t.*,subd.groupname,subd.fullscore from (     SELECT t.questionNum,t.`status`,t.id,t.scoreId,s.studentId,  t.rownum,t.questionScore,t.exampaperNum,t.isException,t.insertUser,t.userNum,t.groupNum,t.updateTime from remark t  left join score s on t.scoreId=s.id INNER JOIN(     SELECT t.groupNum,s.studentId from remark t left join score s on t.scoreId=s.id where t.exampaperNum={exampaperNum} and t.questionNum={newNum} AND   t.insertUser={userNum}  and t.status='T'    " + updateStr + timesql + scoresql + " ORDER BY updateTime desc limit 1   )t1 on t.groupNum=t1.groupNum and s.studentId=t1.studentId where t.groupNum={groupNum} and t.insertUser={userNum}  and t.status='T'  )t left join  (select id,questionNum groupname,ordernum,fullscore from subdefine where exampaperNum={exampaperNum}  AND pid={groupNum})subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
                }
            }
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        return this.dao2._queryBeanList(buffer.toString(), Remark.class, args);
    }

    public Integer otherTeacherHasRemarkQum(Map<String, String> map) throws Exception {
        String examPaperNum = map.get("exampaperNum");
        String questionNum = map.get("questionNum");
        String userNum = map.get("userNum");
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        new ArrayList();
        String sgStr = "";
        String isFenzu = systemService.fenzu(examPaperNum);
        if ("1".equals(isFenzu)) {
            String schoolGroupNum = this.dao2._queryStr("SELECT IFNULL(slg.schoolGroupNum,-1) from user u LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum where u.id={userNum}", args) == null ? "-1" : this.dao2._queryStr("SELECT IFNULL(slg.schoolGroupNum,-1) from user u LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum where u.id={userNum}", args);
            args.put("schoolGroupNum", schoolGroupNum);
            sgStr = " LEFT JOIN score s on r.scoreId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum and slg.schoolGroupNum={schoolGroupNum} ";
        }
        String sql = "select r.id from remark r " + sgStr;
        return Integer.valueOf(this.dao2._queryBeanList(sql + " where r.examPaperNum={examPaperNum} and r.questionNum={questionNum} and r.type='1' and r.status='F' and r.insertUser<>'-1' and r.insertUser<>{userNum} limit 1", Remark.class, args).size());
    }

    public Integer countRemark(Map<String, String> map) throws Exception {
        String groupNum = map.get("groupNum");
        String userNum = map.get("userNum");
        map.get("updateTime");
        map.get("upordown");
        String starttime = map.get("starttime");
        String endtime = map.get("endtime");
        String startscore = map.get("startscore");
        String endscore = map.get("endscore");
        Map args = new HashMap();
        StringBuffer buffer = new StringBuffer();
        String timesql = "  ";
        if (null != starttime && null != endtime && !"".equals(starttime) && !"".equals(endtime)) {
            timesql = timesql + "  and b.updateTime>={starttime} and b.updateTime<={endtime} ";
        }
        String scoresql = "  ";
        if (null != startscore && null != endscore && !"".equals(startscore) && !"".equals(endscore)) {
            scoresql = scoresql + "  and b.questionScore>={startscore} and b.questionScore<={endscore} ";
        }
        StringBuffer buffer2 = buffer.append(" select   cast(count(1)/count(distinct questionNum) as signed) as count  from    remark   b  WHERE b.groupNum={groupNum} and     b.insertUser= {userNum}  and     b.status='T' and b.type=1 " + timesql + scoresql);
        args.put("starttime", starttime);
        args.put("endtime", endtime);
        args.put("startscore", startscore);
        args.put("endscore", endscore);
        args.put("groupNum", groupNum);
        args.put("userNum", userNum);
        String res = this.dao2._queryStr(buffer2.toString(), args);
        return Convert.toInt(res, 0);
    }

    public Integer getTaskNOJustify(Map<String, String> map) throws Exception {
        String examPaperNum = map.get("examPaperNum");
        String questionNum = map.get("questionNum");
        String userNum = map.get("userNum");
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("questionNum", questionNum);
        new ArrayList();
        String sgStr = "";
        String isFenzu = systemService.fenzu(examPaperNum);
        if ("1".equals(isFenzu)) {
            String schoolGroupNum = this.dao2._queryStr("SELECT IFNULL(slg.schoolGroupNum,-1) from user u LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum where u.id={userNum}", args) == null ? "-1" : this.dao2._queryStr("SELECT IFNULL(slg.schoolGroupNum,-1) from user u LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum where u.id={userNum}", args);
            args.put("schoolGroupNum", schoolGroupNum);
            sgStr = " LEFT JOIN score s on r.scoreId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum and slg.schoolGroupNum={schoolGroupNum} ";
        }
        String sql = "select r.scoreId from task r " + sgStr;
        return Integer.valueOf(this.dao2._queryBeanList(sql + " where r.groupNum={questionNum} and r.status='F' limit 1", Remark.class, args).size());
    }

    public Integer GCRemark(Map<String, String> map) throws Exception {
        String groupNum = map.get("groupNum");
        map.get("questionNum");
        String userNum = map.get("userNum");
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("userNum", userNum);
        StringBuffer buffer = new StringBuffer();
        return Integer.valueOf(this.dao2._execute(buffer.append(" update remark set insertuser='-1',updateTime='' WHERE groupNum={groupNum} and insertUser={userNum} and status='F' and type=1 ").toString(), args));
    }

    public Integer GCRemarkAll() throws Exception {
        List list = this.dao2.queryBeanList(" SELECT r.id from remark r where r.status='F' and r.insertUser<>-1 and (UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.updateTime))>180 ", Remark.class);
        for (int i = 0; i < list.size(); i++) {
            Remark r = (Remark) list.get(i);
            Map args = new HashMap();
            args.put("Id", r.getId());
            this.dao2._execute("update remark r set r.insertUser=-1,r.updateTime='' where id={Id}", args);
        }
        return 1;
    }

    public String getTeacherReamarkStatus(Map<String, String> map) throws Exception {
        String userNum = map.get("userNum");
        Map args = new HashMap();
        args.put("userNum", userNum);
        Object obj = this.dao2._queryObject(" SELECT count(1) from questiongroup_user q LEFT JOIN exampaper e on q.exampaperNum=e.examPaperNum  where 1=1 and q.userType<>0 and q.userNum={userNum}", args);
        if (obj != null && !"0".equals(String.valueOf(obj))) {
            return "T";
        }
        return "F";
    }

    public String getQuestionMode(Map<String, String> map) throws Exception {
        String userNum = map.get("userNum");
        Map args = new HashMap();
        args.put("userNum", userNum);
        Object obj = this.dao2._queryObject(" SELECT count(1) from questiongroup_mark_setting qms   INNER join(    SELECT DISTINCT q.exampaperNum FROM questiongroup_user q    INNER JOIN exampaper ep on q.exampaperNum=ep.examPaperNum    INNER JOIN exam e on ep.examNum=e.examNum    WHERE q.userNum={userNum} and q.userType<>0 and e.status<>9 and e.isDelete='F' )q on qms.exampaperNum=q.exampaperNum INNER JOIN exampaper ep on qms.exampaperNum=ep.examPaperNum INNER JOIN exam e on ep.examNum=e.examNum where qms.judgetype<>1 and e.status<>9 and e.isDelete='F' ", args);
        if (obj != null && !"0".equals(String.valueOf(obj))) {
            return "T";
        }
        return "F";
    }

    public String queryremarkyipanllcount(Map<String, String> map) throws Exception {
        String questionNum = map.get("questionNum");
        String userNum = map.get("userNum");
        StringBuffer buffer = new StringBuffer();
        StringBuffer buffer2 = buffer.append(" select   count(1) as count  from    remark   b  WHERE b.questionNum={questionNum} and     b.insertUser={userNum} and     b.status='T' and b.type=1 ");
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        args.put("userNum", userNum);
        return this.dao2._queryStr(buffer2.toString(), args);
    }

    public Remark getRemarkProgress(Map<String, String> map) throws Exception {
        String examPaperNum = map.get("exampaperNum");
        String groupNum = map.get("groupNum");
        String userNum = map.get("userNum");
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("groupNum", groupNum);
        String sgStr = "";
        String isFenzu = systemService.fenzu(examPaperNum);
        if ("1".equals(isFenzu)) {
            String schoolGroupNum = this.dao2._queryStr("SELECT IFNULL(slg.schoolGroupNum,-1) from user u LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum where u.id={userNum}", args) == null ? "-1" : this.dao2._queryStr("SELECT IFNULL(slg.schoolGroupNum,-1) from user u LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum where u.id={userNum}", args);
            args.put("schoolGroupNum", schoolGroupNum);
            sgStr = " LEFT JOIN score s on r.scoreId=s.id INNER JOIN schoolgroup slg on s.schoolNum=slg.schoolNum and slg.schoolGroupNum={schoolGroupNum} ";
        }
        String sql = "SELECT r.questionNum,ifnull(r.count,0) count,ifnull(r1.count1,0) count1,ifnull(r2.count2,0) count2 from(   SELECT r.questionNum,count(1)/count(distinct r.questionNum) count from remark r where r.groupNum={groupNum} and type='1' and status='T' and insertuser={userNum} )r LEFT JOIN(    SELECT count(1)/count(distinct r.questionNum) count1 from remark r " + sgStr + " where r.groupNum={groupNum} and type='1' and status='T' )r1 on 1=1 LEFT JOIN(    SELECT count(1)/count(distinct r.questionNum) count2 from remark r " + sgStr + " where r.groupNum={groupNum} and type='1' )r2 on 1=1";
        return (Remark) this.dao2._queryBean(sql, Remark.class, args);
    }

    public List getRemarkTeacherInfo(Map<String, String> map, List remarkList) throws Exception {
        String groupNum = map.get("groupNum");
        String scoreIds = "";
        for (int k = 0; k < remarkList.size(); k++) {
            scoreIds = scoreIds + ((Remark) remarkList.get(k)).getScoreId() + Const.STRING_SEPERATOR;
        }
        String scoreIds2 = scoreIds.substring(0, scoreIds.length() - 1);
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("scoreIds", scoreIds2);
        return this.dao2._queryBeanList("SELECT ifnull(d.questionNum,sd.questionNum) ext1,GROUP_CONCAT(u.realname,'-',t.questionScore,'-',r.isException) ext2 from task t left join remark r on t.scoreId=r.scoreId  LEFT JOIN define d on t.questionNum=d.id  LEFT JOIN subdefine sd on t.questionNum=sd.id  left join user u on t.insertUser=u.id where t.groupNum={groupNum} and   t.scoreId in ({scoreIds[]}) GROUP BY t.scoreId", Remark.class, args);
    }

    public Integer updateReamrkScore(Map<String, String> map) throws Exception {
        String id = map.get("id");
        String scoreId = map.get("scoreId");
        String questionNum = map.get("questionNum");
        String score = map.get("score");
        String checkObj_value = map.get("checkObj_value");
        String time = DateUtil.getCurrentTime2();
        String updateTimeStr = "";
        if (!"1".equals(checkObj_value)) {
            updateTimeStr = ",updateTime={time}";
        }
        String sql = "update remark set status='T',questionScore={score}" + updateTimeStr + " where id={id} and scoreId={scoreId} and questionNum={questionNum}";
        Map args = new HashMap();
        args.put("time", time);
        args.put("score", score);
        args.put("id", id);
        args.put("scoreId", scoreId);
        args.put("questionNum", questionNum);
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    public List ping1(int exampaperNum, String insertUser, String groupNum, int rownum, String groupType) throws Exception {
        StringBuffer buffer = new StringBuffer();
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("rownum", Integer.valueOf(rownum));
        args.put("insertUser", insertUser);
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        try {
            if (!"2".equals(groupType)) {
                buffer = buffer.append(" select t.*,subd.groupname from (SELECT questionNum,`status`,id,scoreId,  rownum,questionScore,exampaperNum,isException,insertUser,userNum,groupNum   FROM task   WHERE     groupNum={groupNum}  AND rownum={rownum}   AND   insertUser={insertUser})t left join  (select questionNum from questiongroup_question  where groupNum={groupNum})qq  on qq.questionNum=t.questionNum  left join ((select  id,choosename pid,ordernum,questionNum groupname from define where exampaperNum={exampaperNum} and questiontype='1') UNION ALL (select id,pid,ordernum,questionNum groupname from subdefine where exampaperNum={exampaperNum} and questiontype='1' ))subd on subd.id=qq.questionNum order  by  subd.ordernum   asc ");
            } else if ("2".equals(groupType)) {
                buffer = buffer.append(" select t.*,subd.groupname from (SELECT questionNum,`status`,id,scoreId,  rownum,questionScore,exampaperNum,isException,insertUser,userNum,groupNum  FROM task  WHERE   exampaperNum={exampaperNum} AND groupNum={groupNum}  AND rownum={rownum}  AND   insertUser={insertUser} )t left join  (select id,questionNum groupname,ordernum from subdefine where exampaperNum={exampaperNum} AND pid={groupNum})subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
            }
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public Integer countqueryping(String tag, int exampaperNum, String insertNum, String groupNum, String questionNum, String starttime, String endtime, String startscore, String endscore, String groupType) throws Exception {
        StringBuffer buffer = new StringBuffer();
        String timesql = "  ";
        Map args = new HashMap();
        args.put("starttime", starttime);
        args.put("endtime", endtime);
        args.put("startscore", startscore);
        args.put("endscore", endscore);
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        args.put("insertNum", insertNum);
        if (null != starttime && null != endtime && !"".equals(starttime) && !"".equals(endtime)) {
            timesql = timesql + "  and b.updateTime>={starttime} and b.updateTime<={endtime} ";
        }
        String scoresql = "  ";
        if (null != startscore && null != endscore && !"".equals(startscore) && !"".equals(endscore)) {
            scoresql = scoresql + "  and b.questionScore>={startscore} and b.questionScore<={endscore} ";
        }
        if (tag == null || tag == "") {
            if (!"2".equals(groupType)) {
                buffer = buffer.append(" select   count(1) as count  from    task   b   WHERE   b.examPaperNum={exampaperNum}  and     b.groupNum={groupNum} and     b.insertUser={insertNum} and     b.status='T' " + timesql + scoresql + "  order   by   b.updateTime   desc  ");
            } else if ("2".equals(groupType)) {
                String newNum = questionNum;
                if (questionNum == null || "".equals(questionNum) || "null".equals(questionNum)) {
                    newNum = this.dao2._queryStr("select questionNum from questiongroup_question where groupNum={groupNum}  limit 1 ", args);
                }
                args.put("newNum", newNum);
                buffer = buffer.append("select count(1)as count  from( select   count(1)  from    task   b  WHERE   b.examPaperNum={exampaperNum} and b.questionNum={newNum} and     b.insertUser={insertNum} and     b.status='T' " + timesql + scoresql + " group by studentId,userNum  order   by   b.updateTime   desc)a  ");
            }
        } else {
            buffer = buffer.append(" select count(1) as count from(select   b.rownum  from    test_task   b  WHERE   b.examPaperNum={exampaperNum}  and     b.groupNum={groupNum}  and     b.insertUser={insertNum} and     b.status='T'  group by b.rownum  order   by   b.updateTime   desc )b2");
        }
        return this.dao2._queryInt(buffer.toString(), args);
    }

    public Integer countYibiao(String tag, int exampaperNum, String insertNum, String groupNum) throws Exception {
        StringBuffer buffer;
        StringBuffer buffer2 = new StringBuffer();
        if (tag == null || tag == "") {
            buffer = buffer2.append(" SELECT  COUNT(1)   FROM    (     SELECT * FROM task    WHERE  exampaperNum={exampaperNum}   AND groupNum={groupNum}    AND  isException='Y'  AND insertUser={insertNum}    AND  status='T'  )b");
        } else {
            buffer = buffer2.append(" SELECT COUNT(1)  FROM  ( SELECT *    FROM test_task   WHERE  exampaperNum={exampaperNum} AND groupNum={groupNum}    AND  isException='Y'   AND insertUser= {insertNum}    AND  status='T'  )b");
        }
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        args.put("insertNum", insertNum);
        return this.dao2._queryInt(buffer.toString(), args);
    }

    public List yibiao(AwardPoint aw, String tag, int exampaperNum, String insertNum, String groupNum) throws Exception {
        StringBuffer buffer;
        StringBuffer buffer2 = new StringBuffer();
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        args.put("insertNum", insertNum);
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("PageSize", Integer.valueOf(aw.getPageSize()));
        if (tag == null || tag == "") {
            if (!"2".equals(aw.getGroupType())) {
                buffer = buffer2.append(" select    b.questionNum ,b.status,b.id,b.updateTime ,b.scoreId, b.rownum, b.questionScore, b.examPaperNum,b.isException,b.insertUser,b.userNum from    task   b  WHERE  b.examPaperNum={exampaperNum}  and  b.groupNum={groupNum} and  b.isException='Y'  and  b.insertUser={insertNum} order  by   REPLACE(questionNum,'_','.')*1   asc  ");
                buffer.append(" LIMIT {PageStart} , {PageSize}");
            } else {
                this.dao2._queryObject("select  ifnull(MIN(rownum),1) FROM task where groupNum={groupNum} and  isException='Y' and insertUser={insertNum}", args);
                buffer = buffer2.append(" select t.*,subd.groupname from (select questionNum ,status,id,updateTime ,scoreId,rownum,questionScore,examPaperNum,isException,insertUser,userNum from task where groupNum={groupNum} and  isException='Y'  and insertUser={insertNum} ' and rowNum={o})t left join   (select id,questionNum groupname,ordernum from subdefine where exampaperNum={exampaperNum}   AND pid={groupNum})subd on t.questionNum=subd.id  order  by  subd.ordernum   asc ");
            }
        } else if (!aw.getGroupType().equals("2")) {
            buffer = buffer2.append(" select    b.questionNum ,b.status,b.id,b.updateTime ,b.scoreId, b.rownum, b.questionScore, b.examPaperNum,b.isException,b.insertUser,b.userNum from    test_task   b  WHERE  b.examPaperNum={exampaperNum}  and  b.groupNum={groupNum}  and  b.isException='Y'  and  b.insertUser={insertNum} order  by   b.updateTime   desc  ");
            buffer.append("LIMIT {PageStart} , {PageSize}");
        } else {
            Object o = this.dao2._queryObject("select  ifnull(MIN(rownum),1) FROM test_task where groupNum={groupNum}  and  isException='Y' and insertUser={insertNum}", args);
            args.put("o", o);
            buffer = buffer2.append(" select    b.questionNum ,b.status,b.id,b.updateTime ,b.scoreId, b.rownum, b.questionScore, b.examPaperNum,b.isException,b.insertUser,b.userNum from    test_task   b  WHERE  b.examPaperNum={exampaperNum}  and  b.groupNum={groupNum} and  b.isException='Y'  and  b.insertUser={insertNum}' and rowNum={o} order  by   b.updateTime   desc  ");
        }
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public Integer countchaYi(AwardPoint aw, String tag, int exampaperNum, String insertNum, String groupNum) throws Exception {
        StringBuffer buffer;
        StringBuffer buffer2 = new StringBuffer();
        if (tag == null || tag.equals("")) {
            buffer = buffer2.append(" select   count(1) as count  from      task   b   where   b.examPaperNum={exampaperNum}  and  b.groupNum={groupNum}  and  b.insertUser={insertNum}  and  b.isException='p'   order  by   b.updateTime   desc  ");
        } else {
            buffer = buffer2.append(" select   count(1) as count  from      test_task   b   where   b.examPaperNum={exampaperNum}  and  b.groupNum={groupNum}  and  b.insertUser={insertNum} and  b.isException='p'  order  by   b.updateTime   desc  ");
        }
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        args.put("insertNum", insertNum);
        return this.dao2._queryInt(buffer.toString(), args);
    }

    public List chayi(AwardPoint aw, String tag, int exampaperNum, String insertNum, String groupNum) throws Exception {
        StringBuffer buffer;
        StringBuffer buffer2 = new StringBuffer();
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        args.put("insertNum", insertNum);
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("PageSize", Integer.valueOf(aw.getPageSize()));
        if (aw.getTag() == null || aw.getTag().equals("")) {
            buffer = buffer2.append(" select    b.questionNum,b.status,b.id,b.scoreId, b.rownum, b.questionScore, b.examPaperNum,b.isException,b.insertUser  from     task   b  WHERE   b.examPaperNum={exampaperNum} and  b.groupNum={groupNum}  and  b.isException='p'  and  b.insertUser={insertNum} order  by   b.updateTime   desc  ");
            buffer.append(" LIMIT {PageStart}, {PageSize}");
        } else {
            buffer = buffer2.append(" select    b.questionNum,b.status,b.id,b.scoreId, b.rownum, b.questionScore, b.examPaperNum,b.isException,b.insertUser  from    test_task   b  WHERE   b.examPaperNum={exampaperNum} and  b.groupNum={groupNum}  and  b.isException='p'  and  b.insertUser={insertNum} order  by   b.updateTime   desc  ");
            buffer.append(" LIMIT {PageStart}, {PageSize}");
        }
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public Integer updatescore(String time, String insertUser, String score, String id, AwardPoint aw) {
        Map args = new HashMap();
        args.put("score", score);
        args.put("time", time);
        args.put("insertUser", insertUser);
        args.put("ScoreId", aw.getScoreId());
        return Integer.valueOf(this.dao2._execute("update score  a  set  a.questionScore={score},  a.insertDate={time} ,a.isModify='T', a.insertUser={insertUser} where  id={ScoreId}", args));
    }

    public Integer updatetempscore(String time, String insertUser, String score, String id, AwardPoint aw) {
        Map args = new HashMap();
        args.put("score", score);
        args.put("time", time);
        args.put("insertUser", insertUser);
        args.put("ScoreId", aw.getScoreId());
        return Integer.valueOf(this.dao2._execute("update score  a  set  a.questionScore={score},  a.insertDate={time}, a.insertUser={insertUser}  where  id={ScoreId}", args));
    }

    public AwardPoint markSetting(AwardPoint aw) {
        Map args = new HashMap();
        args.put("GroupNum", aw.getGroupNum());
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        return (AwardPoint) this.dao2._queryBean("select   makType  from   questiongroup_mark_setting  where  groupNum={GroupNum} and   exampaperNum={ExampaperNum}", AwardPoint.class, args);
    }

    public List scorePingList(String id) {
        Map args = new HashMap();
        args.put("id", id);
        return this.dao2._queryBeanList(" select   /* shard_host_HG=Write */  a.questionScore,a.insertUser from    task   a   where  a.scoreId={id}    and   a.status='T' order by userNum ", AwardPoint.class, args);
    }

    public List yiscorePingList(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ScoreId", aw.getScoreId());
        return this.dao2._queryBeanList(" select     a.questionScore      from    task   a   where  a.scoreId={ScoreId}   and      a.status='T'   limit 0,1  ", AwardPoint.class, args);
    }

    public List secondscorePingList(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("StudentId", aw.getStudentId());
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryBeanList("select     a.questionScore      from    task   a   where   a.exampaperNum={ExampaperNum}  and  a.studentId={StudentId}   and    a.questionNum={QuestionNum}  and   a.status='T'  limit 1,1  ", AwardPoint.class, args);
    }

    public List yiscorePingList1(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ScoreId", aw.getScoreId());
        return this.dao2._queryBeanList("select      a.questionScore      from    task   a   where    a.scoreId={ScoreId}  and   a.status='T'   limit 0,1  ", AwardPoint.class, args);
    }

    public List secondscorePingList1(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ScoreId", aw.getScoreId());
        return this.dao2._queryBeanList("select     a.questionScore      from    task   a   where    a.scoreId={ScoreId}  and   a.status='T'   limit 1,1  ", AwardPoint.class, args);
    }

    public AwardPoint finishstudentIdaw(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        return (AwardPoint) this.dao2._queryBean("select  studentId   from remark  where  exampaperNum={ExampaperNum}\t and  type='1'   and  status='T' \t and  questionNum={QuestionNum}   ORDER  BY  insertDate desc  LIMIT 0,1\t", AwardPoint.class, args);
    }

    public Integer insertmarkerror(AwardPoint aw) {
        Map args = new HashMap();
        args.put("Id", aw.getId());
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("StudentId", aw.getStudentId());
        args.put("ClassNum", aw.getClassNum());
        args.put("GradeNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("InsertDate", aw.getInsertDate());
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("SchoolNum", Integer.valueOf(aw.getSchoolNum()));
        args.put("Type", aw.getType());
        return Integer.valueOf(this.dao2._execute("insert into  markerror(id,questionNum,studentId,classNum,gradeNum,insertUser, insertDate,exampaperNum,schoolNum,type)values({Id},{QuestionNum},{StudentId},{ClassNum},{GradeNum},{InsertUser},{InsertDate},{ExampaperNum},{SchoolNum},{Type})", args));
    }

    public synchronized List caipan(AwardPoint aw) {
        this.log.error("start------------------==" + aw.getInsertUser());
        StringBuffer buffer = new StringBuffer();
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        try {
            Object oid = this.dao2._queryObject("select id from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum}  and type='1'   and insertUser={InsertUser} and rownum={PageStart} limit 1", args);
            if (null == oid) {
                Object oid2 = this.dao2._queryObject("select id from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum}  and status='F' and type='1'  and insertUser='-1' order by rownum limit 1", args);
                args.put("oid", oid2);
                Object rowNums = this.dao2._queryObject("select max(rownum) from remark where exampaperNum={ExampaperNum}   and questionNum={QuestionNum}  and type='1'   and insertUser={InsertUser} ", args);
                if (null == rowNums) {
                    rowNums = 0;
                }
                Integer rowNumint = Integer.valueOf(rowNums + "");
                args.put("rowNumint", rowNumint);
                this.dao2._execute("update remark set insertuser={InsertUser},rowNum= ({rowNumint} + 1)  where id={oid} ", args);
                this.log.error("null == oid------------------==" + (rowNumint.intValue() + 1));
            } else {
                this.log.error("null != oid------------------==" + aw.getInsertUser());
            }
            buffer.append("select   a.questionNum,a.status,a.scoreId,a.isException,a.questionScore,a.rownum, a.id,  a.examPaperNum,a.insertUser  ,c.fullScore from  remark    a     left  join  ((select id,questionnum,fullScore,examPaperNum from define where exampaperNum= {ExampaperNum} and id={QuestionNum} ) union all (select id,questionnum,fullScore,examPaperNum  from subdefine WHERE exampaperNum= {ExampaperNum} and id={QuestionNum})) c  on   a.questionNum=c.id  AND  a.exampaperNum=c.exampaperNum  where a.id={oid} and a.type ='1'  and   a.examPaperNum={ExampaperNum} and   a.questionNum={QuestionNum} and   a.rownum={Rownum} and a.insertuser={InsertUser} group  by  a.questionNum  order by  a.rownum asc ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.log.error("querysql------------------==" + buffer.toString());
        this.log.error("end------------------==" + aw.getInsertUser());
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List nocaipan(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer("select  id  from  remark  a   where   a.examPaperNum={ExampaperNum} and   a.type ='1'  and   a.status='F' and   a.questionNum={QuestionNum} and a.insertuser={InsertUser} order by  a.rownum asc  ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public synchronized List caipan1(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer();
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        try {
            Object oid = this.dao2._queryObject("select id from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum} and status='F' and type='1'  and insertUser={InsertUser} limit 1", args);
            if (null == oid) {
                Object oid2 = this.dao2._queryObject("select id from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum} and status='F' and type='1'  and insertUser='-1' order by rownum limit 1", args);
                args.put("oid", oid2);
                Object rowNums = this.dao2._queryObject("select max(rownum) from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum}  and type='1' and insertUser={InsertUser} ", args);
                if (null == rowNums) {
                    rowNums = 0;
                }
                Integer rowNumint = Integer.valueOf(rowNums + "");
                args.put("rowNumint", rowNumint);
                this.dao2._execute("update remark set insertuser={InsertUser} ,rowNum=({rowNumint} + 1) where id={oid}", args);
            }
            buffer.append("  select  a.questionNum,a.status,a.id,a.isException, a.questionScore,a.rownum,a.scoreId,  a.examPaperNum,a.insertUser ,c.fullScore from  remark    a     left  join  ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum}) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum})) c  on   a.questionNum=c.id  AND  a.exampaperNum=c.exampaperNum  where a.id={oid} and  a.status='F'   and   a.type ='1'   and  a.examPaperNum={ExampaperNum} and   a.questionNum={QuestionNum} order by  a.rownum asc ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List finishcaipan(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer(" select   a.questionNum,a.status,a.scoreId,a.isException, a.questionScore ,a.rownum, a.id,  a.examPaperNum,a.insertUser  ,c.fullScore from  remark    a     left  join ( (select id,exampaperNum,fullScore from define where examPaperNum = {ExampaperNum} ) UNION ALL ( select id,exampaperNum,fullScore from subdefine where examPaperNum = {ExampaperNum} ) ) c   on   a.questionNum=c.id  AND  a.exampaperNum=c.exampaperNum  where  a.examPaperNum={ExampaperNum} and a.insertUser={InsertUser} and   a.type ='1'   and   a.status='T'    and   a.questionNum={QuestionNum} order by  a.insertDate desc  ");
        if (aw.getPageStart() >= 0) {
            buffer.append(" limit {PageStart}, {PageSize} ");
        }
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("PageSize", Integer.valueOf(aw.getPageSize()));
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public Integer finishcaipanCount(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer(" select   count(1) as count  from   remark    a    where  a.examPaperNum={ExampaperNum} and   a.type ='1'   and   a.status='T'   and   a.questionNum={QuestionNum} and a.insertuser={InsertUser}");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryInt(buffer.toString(), args);
    }

    public List yicaipan(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer(" select   a.questionNum,a.status,a.scoreId,a.isException, a.questionScore ,a.rownum, a.id,  a.examPaperNum,a.insertUser  ,c.fullScore from  remark    a     left  join  ( (select id,exampaperNum,fullScore from define where examPaperNum = {ExampaperNum} ) UNION ALL  ( select id,exampaperNum,fullScore from subdefine where examPaperNum = {ExampaperNum}) ) c  on   a.questionNum=c.id  AND  a.exampaperNum=c.exampaperNum  where  a.examPaperNum={ExampaperNum} and   a.type ='1'  and    a.isException='Y'  and   a.questionNum={QuestionNum}  and a.insertuser={InsertUser}  order by a.insertDate  asc  ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        if (aw.getPageStart() >= 0) {
            buffer.append(" limit 0,1");
        }
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public Integer yicaipanCount(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer("select  count(1) as count  from  remark    a     where a.examPaperNum={ExampaperNum} and    a.type ='1'  and    a.isException='Y'  and    a.questionNum={QuestionNum} and a.insertuser={InsertUser}");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryInt(buffer.toString(), args);
    }

    public Integer countyichacai(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer("select  count(1) as count   from  remark    a    where  a.examPaperNum={ExampaperNum} and    a.type ='1'  and    a.isException='p'  and    a.questionNum={QuestionNum} and a.insertuser={InsertUser} order by  a.rownum asc ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryInt(buffer.toString(), args);
    }

    public List yicaichapan(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer(" select   a.questionNum,a.status,a.scoreId,a.isException, a.questionScore ,a.rownum, a.id,  a.examPaperNum,a.insertUser   ,c.fullScore from  remark    a     left  join  ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum}) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum})) c   on   a.questionNum=c.id  AND  a.exampaperNum=c.exampaperNum  where  a.examPaperNum={ExampaperNum} and   a.type ='1'   and    a.isException='P'  and   a.questionNum={QuestionNum} and a.insertuser={InsertUser}  order by a.insertDate  asc ");
        if (aw.getPageStart() >= 0) {
            buffer.append(" limit {PageStart} , {PageSize} ");
        }
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("PageSize", Integer.valueOf(aw.getPageSize()));
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List finishchongpan(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer("  select    a.questionNum,a.questionScore,a.rownum,a.id,a.scoreId,  a.examPaperNum,a.status,a.isException ,a.userNum as  insertUser ,c.fullScore from  remark    a     left  join  ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum} ) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum} )) c  on   a.questionNum=c.id   AND  a.exampaperNum=c.exampaperNum  where   a.examPaperNum={ExampaperNum} and     a.userNum={InsertUser} and     a.type ='2'  and     a.status='T'   and     a.questionNum={QuestionNum} order by a.insertDate asc ");
        buffer.append(" limit {PageStart} , {PageSize} ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("PageSize", Integer.valueOf(aw.getPageSize()));
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public Integer finishchongpanCount(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer("select count(1) as count   from  remark    a    where   a.examPaperNum={ExampaperNum} and   a.type ='2' and   a.status='T'  and  a.userNum={InsertUser} and  a.questionNum={QuestionNum} order by  a.rownum asc ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryInt(buffer.toString(), args);
    }

    public List yichongpan(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer(" select     a.questionNum,a.status,a.scoreId,a.isException, a.questionScore,a.rownum, a.id,  a.examPaperNum,a.userNum as  insertUser ,c.fullScore from  remark    a     left  join  ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum}) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum})) c   on   a.questionNum=c.id  AND  a.exampaperNum=c.exampaperNum   where   a.examPaperNum={ExampaperNum} and     a.userNum={InsertUser} and     a.type ='2'    and     a.isException='Y'   and     a.questionNum={QuestionNum} order by  a.rownum asc  LIMIT 0,1  ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public Integer yichongpanCount(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer("select  count(1) as count  from   remark    a    where  a.examPaperNum={ExampaperNum} and    a.userNum={InsertUser} and    a.type ='2'    and    a.isException='Y'  and    a.questionNum={QuestionNum} order by  a.rownum asc ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryInt(buffer.toString(), args);
    }

    public Integer countyichachong(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer(" select  count(1) as count   from  remark    a   where  a.examPaperNum={ExampaperNum} and    a.userNum={InsertUser} and    a.type ='2'   and    a.isException='p'  and    a.questionNum={QuestionNum} order by  a.rownum asc ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryInt(buffer.toString(), args);
    }

    public List yichongchapan(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer("select   distinct  a.questionNum,a.status,a.scoreId,a.isException,a.questionScore ,a.rownum, a.id,  a.examPaperNum,a.userNum as  insertUser  ,c.fullScore from  remark    a     left  join  ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum}) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum})) c  on   a.questionNum=c.id  AND  a.exampaperNum=c.exampaperNum  where  a.examPaperNum={ExampaperNum} and    a.type ='2'   and    a.isException='p'  and    a.userNum={InsertUser} and    a.questionNum={QuestionNum} order by  a.rownum asc ");
        if (aw.getPageStart() >= 0) {
            buffer.append(" limit {PageStart}, {PageSize} ");
        }
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("PageSize", Integer.valueOf(aw.getPageSize()));
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List chongpanfen(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer("  select    a.questionNum, a.questionScore,a.rownum,a.id,a.scoreId,  a.examPaperNum,a.status,a.isException ,a.userNum as  insertUser ,c.fullScore from  remark    a     left  join  ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum}) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum})) c   on    a.questionNum=c.id  AND      a.exampaperNum=c.exampaperNum   where    a.userNum={InsertUser}  and      a.examPaperNum={ExampaperNum} and      a.type ='2'  and      a.status='F'    and  a.questionNum={QuestionNum}  order by  a.rownum asc ");
        buffer.append("limit 0,1 ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List weichongpanfen(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer("select *  from  remark    a     where   a.type ='2'  and  a.examPaperNum={ExampaperNum} and  a.status='F'   and a.insertUser={InsertUser} and  a.questionNum={QuestionNum} order by  a.rownum asc ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List chongpanfen1(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer(" select   a.questionNum,a.questionScore,a.rownum,a.id,a.scoreId,  a.examPaperNum,a.status,a.isException ,a.userNum as  insertUser ,c.fullScore from  remark    a     left  join  ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum}) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum})) c   on   a.questionNum=c.id  AND  a.exampaperNum=c.exampaperNum  where   a.examPaperNum={ExampaperNum} and   a.userNum={InsertUser} and   a.type ='2'   and   a.questionNum={QuestionNum}  and a.rownum={Rownum} order by  a.rownum asc ");
        buffer.append("limit 0,1 ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public Integer insertmarkError(AwardPoint aw) {
        return Integer.valueOf(this.dao2._execute("insert into  markerror (questionNum,insertUser, insertDate,exampaperNum,type,userNum,questionScore,scoreId)values({questionNum},{insertUser},{insertDate},{exampaperNum},{type},{userNum},{questionScore},{scoreId})", aw));
    }

    public AwardPoint completeTask2(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        return (AwardPoint) this.dao2._queryBean("SELECT COUNT(1) as  count  FROM task   where   exampaperNum={ExampaperNum}   AND  groupNum={GroupNum} AND status='T'  and  insertUser={InsertUser} ", AwardPoint.class, args);
    }

    public List usercompleteTask2(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("UserNum", aw.getUserNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList("select  count(1) as count  from  task  d ,   (select * from  questiongroup_question  where exampaperNum={ExampaperNum} and groupNum={GroupNum}) b,  (select * from questiongroup where exampaperNum={ExampaperNum} and  groupNum={GroupNum}) c ,   ( SELECT * FROM questiongroup_user WHERE userNum={UserNum} AND exampaperNum={ExampaperNum} and groupNum={GroupNum}) r    where   d.exampaperNum=b.exampaperNum  and   d.questionNum=b.questionNum    and  c.groupNum=b.groupNum   and   r.groupNum=b.groupNum   and   b.groupNum={GroupNum}  and   d.status='T'    and d.insertUser={InsertUser} ", AwardPoint.class, args);
    }

    public List listusercompleteTask2(AwardPoint aw) {
        Map args = new HashMap();
        args.put("GroupNum", aw.getGroupNum());
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList("select  IFNULL(t. tnum,0)  as  count, qq.questionNum  from     (select  distinct  questionNum   from  task    where  groupNum={GroupNum}    and exampaperNum={ExampaperNum}  and insertUser={InsertUser})qq   left  join    (SELECT qz.examPaperNum,qz.groupNum,COUNT(t.id) tnum ,t.questionNum   FROM   (SELECT * FROM questiongroup qp WHERE  exampaperNum={ExampaperNum}   and  groupNum={GroupNum} ) qz   LEFT JOIN questiongroup_question qq ON qz.exampaperNum=qq.exampaperNum AND qz.groupNum=qq.groupNum   LEFT JOIN task t ON t.exampaperNum=qz.exampaperNum    AND t.questionNum=qq.questionNum AND status='T'  and t.insertUser={InsertUser}   group  by  t.questionNum )t    on  t.questionNum=qq.questionNum", AwardPoint.class, args);
    }

    public AwardPoint sumusercompleteTask(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        return (AwardPoint) this.dao2._queryBean("select    count(1) as  count  from  task  d  where    d.exampaperNum={ExampaperNum} and d.groupNum={GroupNum}   and    d.insertUser={InsertUser}  and   d.status='T'", AwardPoint.class, args);
    }

    public AwardPoint userNumbean(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        return (AwardPoint) this.dao2._queryBean("select  *  from  questiongroup_user   where   exampaperNum={ExampaperNum} and  groupNum={GroupNum}  and  userNum={InsertUser} ", AwardPoint.class, args);
    }

    public AwardPoint completeTime(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return (AwardPoint) this.dao2._queryBean("select  max(d.updateTime)as maxtime,min(d.updateTime) as mintime from  task  d  where     d.updateUser  is  not  null    and   d.exampaperNum={ExampaperNum}   and   d.groupNum={GroupNum}", AwardPoint.class, args);
    }

    public Integer exam(int exam, int grade, int subject) {
        Map args = new HashMap();
        args.put("exam", Integer.valueOf(exam));
        args.put("subject", Integer.valueOf(subject));
        args.put("grade", Integer.valueOf(grade));
        return this.dao2._queryInt(" select   b.examPaperNum   from  exampaper b   where        b.examNum={exam}  and      b.subjectNum={subject} and   b.gradeNum={grade}", args);
    }

    public List groupNumList() {
        return this.dao2.queryBeanList(" select  groupNum  from  questiongroup  where  groupNum is  not  null order  by   groupNum  asc ", AwardPoint.class);
    }

    public int countchongpanfen(AwardPoint aw) {
        StringBuffer buffer = new StringBuffer("   select  count(1) as count   from  remark    a   where   a.type ='2'  and a.userNum={InsertUser} and  a.examPaperNum={ExampaperNum} and  a.status='F'   and  a.questionNum={QuestionNum} order by  a.rownum asc ");
        Map args = new HashMap();
        args.put("InsertUser", aw.getInsertUser());
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryInt(buffer.toString(), args).intValue();
    }

    public Map<String, Object> ttizhuzb1(AwardPoint aw, String makType) {
        String sql;
        if (makType.equals("0")) {
            sql = "SELECT  case   when  t.tnum=0 then  0    else  round (t.tnum/(r.cnum*(qs.makType+1)) )   end  zb   FROM  (  \tSELECT qz.examPaperNum,qz.groupNum,COUNT(s.id) cnum  \tFROM \t(SELECT * FROM questiongroup qp WHERE groupNum={GroupNum} and exampaperNum={ExampaperNum} ) qz \tLEFT JOIN questiongroup_question qq ON qz.exampaperNum=qq.exampaperNum AND qz.groupNum=qq.groupNum \tLEFT JOIN score s ON s.examPaperNum=qz.exampaperNum AND s.questionNum=qq.questionNum   AND  s.isAppend!='T'    )r  LEFT JOIN questiongroup_mark_setting qs ON qs.exampaperNum=r.examPaperNum AND qs.groupNum=r.groupNum  LEFT JOIN   (  SELECT qz.examPaperNum,qz.groupNum,COUNT(t.id) tnum  \tFROM  (SELECT * FROM questiongroup qp WHERE groupNum={GroupNum}) qz \tLEFT JOIN questiongroup_question qq ON qz.exampaperNum=qq.exampaperNum AND qz.groupNum=qq.groupNum \tLEFT JOIN task t ON t.exampaperNum=qz.exampaperNum AND t.questionNum=qq.questionNum AND status='T'  )t ON 1=1 ";
        } else {
            sql = "SELECT  case   when  t.tnum=0 then  0    else  round (t.tnum/(r.cnum*(qs.makType+1)) )   end  zb   FROM  (  SELECT qz.examPaperNum,qz.groupNum,COUNT(s.id) cnum  FROM \t(SELECT * FROM questiongroup qp WHERE groupNum={GroupNum} and exampaperNum={ExampaperNum} ) qz \tLEFT JOIN questiongroup_question qq ON qz.exampaperNum=qq.exampaperNum AND qz.groupNum=qq.groupNum \tLEFT JOIN score s ON s.examPaperNum=qz.exampaperNum AND s.questionNum=qq.questionNum  AND  s.isAppend!='T'   )r  LEFT JOIN questiongroup_mark_setting qs ON qs.exampaperNum=r.examPaperNum AND qs.groupNum=r.groupNum  LEFT JOIN  (  SELECT qz.examPaperNum,qz.groupNum,COUNT(t.id) tnum  \tFROM  (SELECT * FROM questiongroup qp WHERE groupNum={GroupNum}) qz \tLEFT JOIN questiongroup_question qq ON qz.exampaperNum=qq.exampaperNum AND qz.groupNum=qq.groupNum \tLEFT JOIN task t ON t.exampaperNum=qz.exampaperNum AND t.questionNum=qq.questionNum AND status='T'   )t ON 1=1 ";
        }
        Map args = new HashMap();
        args.put("GroupNum", aw.getGroupNum());
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        return this.dao2._querySimpleMap(sql, args);
    }

    public List<AwardPoint> questinNumList(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryBeanList("select    a.questionNum   from  questiongroup_question    a   where   a.exampaperNum={ExampaperNum} and  a.groupNum={GroupNum} ", AwardPoint.class, args);
    }

    public List questinNumList1(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryBeanList("select     distinct a.questionNum,a.studentId from  task a , questiongroup_question  b  where   a.examPaperNum=b.examPaperNum  and     b.examPaperNum={ExampaperNum}  and     b.groupNum={GroupNum} AND a.questionNum=b.questionNum order  by   questionNum   asc  ", AwardPoint.class, args);
    }

    public Integer reMarkGroup(String scoreId, String id, String insertUser, String groupNum, String questionNum, int exampaperNum, String questionScore, String studentId) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("questionScore", questionScore);
        args.put("scoreId", scoreId);
        String count = this.dao2._queryStr("SELECT ifnull(count(1),0) from remark r INNER JOIN score s on r.scoreId=s.id where r.groupNum={groupNum} and s.studentId={studentId} ", args);
        if ("0".equals(count)) {
            this.dao2._execute("insert  into  remark (scoreId,groupNum,questionScore,questionNum,insertUser, insertDate,exampaperNum,type,status,userNum,rownum,isException,source,studentId)  SELECT s.id,qq.groupNum,s.questionScore,s.questionNum,-1,now(),s.examPaperNum,1,'F',-1,-1,'W','1',s.studentId from score s INNER JOIN questiongroup_question qq on s.questionNum=qq.questionNum WHERE qq.groupNum={groupNum} and s.studentId={studentId} ", args);
        }
        this.dao2._execute("update remark set isException='F',questionScore={questionScore} where scoreId={scoreId}", args);
        return 1;
    }

    public Integer reMarkF(String scoreId, String id, String insertUser, String questionNum, int exampaperNum, int sumsize, String groupNum, String studentId) {
        try {
            Map args = new HashMap();
            args.put("scoreId", scoreId);
            args.put("questionNum", questionNum);
            args.put("exampaperNum", Integer.valueOf(exampaperNum));
            args.put("sumsize", Integer.valueOf(sumsize));
            args.put("groupNum", groupNum);
            args.put(Const.EXPORTREPORT_studentId, studentId);
            return Integer.valueOf(this.dao2._execute("insert  into  remark (scoreId,questionNum, insertUser, insertDate,exampaperNum,type,status,userNum,rownum,isException,source,groupNum,studentId)  values( {scoreId},{questionNum},'-1', NOW(),{exampaperNum} , '1', 'F','-1',{sumsize},'F',1,{groupNum},{studentId})", args));
        } catch (Exception e) {
            return null;
        }
    }

    public Integer supplyRemarkData(String groupNum2) {
        try {
            Map args = new HashMap();
            String[] groupNumArr = groupNum2.split("_");
            for (String groupNum : groupNumArr) {
                args.put("groupNum", groupNum);
                String count = this.dao2._queryStr("select ifnull(count(1),0) from questiongroup_question where groupNum={groupNum} ", args);
                args.put("count", count);
                this.dao2._execute("insert  into  remark (scoreId,questionNum,insertUser, insertDate,exampaperNum,type,status,userNum,rownum,isException,source,groupNum,questionScore,studentId) SELECT s.id,s.questionNum,-1,now(),s.exampaperNum,1,'F',-1,-1,'W',1,qq.groupNum,s.questionScore,s.studentId from score s INNER JOIN questiongroup_question qq on s.questionNum=qq.questionNum INNER JOIN( SELECT s.studentId,r.groupNum,r.insertUser from remark r inner join score s on r.scoreId=s.id  where r.groupNum={groupNum} GROUP BY s.studentId having count(1)<>{count}  )r on qq.groupNum=r.groupNum and s.studentId=r.studentId  left JOIN remark r1 on s.id=r1.scoreId  where qq.groupNum={groupNum} and r1.id is null and s.continued='F'", args);
                this.dao2._batchUpdate("update remark set status={status},insertUser={insertUser} where groupNum={groupNum} and studentId={studentId}", this.dao2._queryMapList("select groupNum,MIN(status) status,studentId,Max(insertUser) insertUser from remark where groupNum={groupNum} and isException='F' group by studentId", TypeEnum.StringString, args));
            }
            return 1;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer supplyRemarkDataByStudent(String groupNum2, String studentId) {
        int i = 0;
        try {
            Map args = new HashMap();
            args.put(Const.EXPORTREPORT_studentId, studentId);
            String[] groupNumArr = groupNum2.split("_");
            for (String groupNum : groupNumArr) {
                args.put("groupNum", groupNum);
                String count = this.dao2._queryStr("select ifnull(count(1),0) from questiongroup_question where groupNum={groupNum} ", args);
                args.put("count", count);
                i = this.dao2._execute("insert  into  remark (scoreId,questionNum,insertUser, insertDate,exampaperNum,type,status,userNum,rownum,isException,source,groupNum,questionScore,studentId) SELECT s.id,s.questionNum,-1,now(),s.exampaperNum,1,'F',-1,-1,'W',1,qq.groupNum,s.questionScore,s.studentId from score s INNER JOIN questiongroup_question qq on s.questionNum=qq.questionNum INNER JOIN( SELECT r.studentId,r.groupNum from remark r  where r.groupNum={groupNum} and r.studentId={studentId} GROUP BY r.studentId having count(1)<>{count}  )r on qq.groupNum=r.groupNum and s.studentId=r.studentId  left JOIN remark r1 on s.id=r1.scoreId  where qq.groupNum={groupNum} and r1.id is null and s.continued='F'", args);
            }
            return Integer.valueOf(i);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer supplyTaskData(String groupNum2) {
        try {
            Map args = new HashMap();
            String[] groupNumArr = groupNum2.split("_");
            for (String groupNum : groupNumArr) {
                args.put("groupNum", groupNum);
                String count = this.dao2._queryStr("select ifnull(count(1),0) from questiongroup_question where groupNum={groupNum} ", args);
                args.put("count", count);
                this.dao2._execute("insert into task (id,scoreId,exampaperNum,groupNum,questionNum,questionScore,studentId,userNum,isException,isDelete,status,insertUser,insertDate,testingCentreId)  SELECT UUID_SHORT(),s.id,s.exampaperNum,qq.groupNum,s.questionNum,s.questionScore,s.studentId,3,'W','F','F',-1,NOW(),s.testingCentreId  from score s INNER JOIN questiongroup_question qq on s.questionNum=qq.questionNum  INNER JOIN(   SELECT t.* from task t where t.groupNum={groupNum} and userNum=3 GROUP BY t.studentId having count(1)<>{count} )r on qq.groupNum=r.groupNum and s.studentId=r.studentId  left JOIN task t on s.id=t.scoreId and t.userNum=3  where qq.groupNum={groupNum} and t.id is null and s.continued='F'", args);
            }
            return 1;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer supplyTaskDataByStudent(String groupNum2, String studentId) {
        int i = 0;
        try {
            String[] groupNumArr = groupNum2.split("_");
            for (String groupNum : groupNumArr) {
                Map args = new HashMap();
                args.put("groupNum", groupNum);
                args.put(Const.EXPORTREPORT_studentId, studentId);
                String count = this.dao2._queryStr("select ifnull(count(1),0) from questiongroup_question where groupNum={groupNum} ", args);
                args.put("count", count);
                List list = this.dao2._queryColList(" SELECT UUID_SHORT(),s.id,s.exampaperNum,qq.groupNum,s.questionNum,s.questionScore,s.studentId,3,'W','F','F',-1,NOW(),s.testingCentreId  from score s INNER JOIN questiongroup_question qq on s.questionNum=qq.questionNum  INNER JOIN(   SELECT t.* from task t where t.groupNum={groupNum} and userNum=3 and t.studentId={studentId} GROUP BY t.studentId having count(1)<>{count}  )r on qq.groupNum=r.groupNum and s.studentId=r.studentId  left JOIN task t on s.id=t.scoreId and t.userNum=3  where qq.groupNum={groupNum} and t.id is null and s.continued='F' ", args);
                if (list.size() > 0) {
                    i = this.dao2._execute("insert into task (id,scoreId,exampaperNum,groupNum,questionNum,questionScore,studentId,userNum,isException,isDelete,status,insertUser,insertDate,testingCentreId)  SELECT UUID_SHORT(),s.id,s.exampaperNum,qq.groupNum,s.questionNum,s.questionScore,s.studentId,3,'W','F','F',-1,NOW(),s.testingCentreId  from score s INNER JOIN questiongroup_question qq on s.questionNum=qq.questionNum  INNER JOIN(   SELECT t.* from task t where t.groupNum={groupNum} and userNum=3 and t.studentId={studentId} GROUP BY t.studentId having count(1)<>{count}  )r on qq.groupNum=r.groupNum and s.studentId=r.studentId  left JOIN task t on s.id=t.scoreId and t.userNum=3  where qq.groupNum={groupNum} and t.id is null and s.continued='F'", args);
                }
            }
            return Integer.valueOf(i);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer reMark(String scoreId, String id, String insertUser, String questionNum, int exampaperNum, int sumsize) {
        try {
            Map args = new HashMap();
            args.put("scoreId", scoreId);
            args.put("questionNum", questionNum);
            args.put("exampaperNum", Integer.valueOf(exampaperNum));
            args.put("sumsize", Integer.valueOf(sumsize));
            return Integer.valueOf(this.dao2._execute("insert  into  remark (scoreId,questionNum, insertUser, insertDate,exampaperNum,type,status,userNum,rownum,isException)   values({scoreId},{questionNum},'-1',  NOW(), {exampaperNum} , '1',  'F', '-1',{sumsize},'F')", args));
        } catch (Exception e) {
            return null;
        }
    }

    public Integer reMark2(String scoreId, String questionScore, String insertUser, String questionNum, String exampaperNum) {
        String time = DateUtil.getCurrentTime2();
        try {
            Map args = new HashMap();
            args.put("scoreId", scoreId);
            args.put("questionNum", questionNum);
            args.put("questionScore", questionScore);
            args.put("insertUser", insertUser);
            args.put("exampaperNum", exampaperNum);
            args.put("time", time);
            return Integer.valueOf(this.dao2._execute("insert  into  remark (scoreId,questionNum,questionScore, insertUser, insertDate,exampaperNum,type,status,userNum,rownum,isException,updateTime)  values( {scoreId},{questionNum},{questionScore},{insertUser}, NOW(), {exampaperNum}, '1', 'T', '-1', '-1', 'F',{time} )", args));
        } catch (Exception e) {
            return null;
        }
    }

    public Integer insertTask3Group(String scoreId, String id, String insertUser, String groupNum, String questionNum, int exampaperNum, String score, String studentId) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("scoreId", scoreId);
        String count = this.dao2._queryStr("SELECT ifnull(count(1),0) from task t where t.groupNum={groupNum} and t.studentId={studentId} and t.userNum=3 ", args);
        long fenfaDate = System.currentTimeMillis();
        if ("0".equals(count)) {
            String str = "insert  into  task (id,scoreId,exampaperNum,groupNum,questionNum,questionScore,userNum,isException,porder,studentId,isDelete,status, insertUser,insertDate,updateUser,updateTime,fenfaDate,testingCentreId,xuankaoqufen)  select  UUID_SHORT() id,scoreId,exampaperNum,groupNum,questionNum,'" + score + "' questionScore,3 userNum,'W' isException,10 porder,studentId,isDelete,'F' status, -1 insertUser,insertDate,-1 updateUser,'' updateTime," + fenfaDate + " fenfaDate,testingCentreId,xuankaoqufen  from task where groupNum={groupNum} and studentId={studentId} and userNum=1 ";
            this.dao2._execute("insert  into  task (id,scoreId,exampaperNum,groupNum,questionNum,questionScore,userNum,isException,porder,studentId,isDelete,status,insertUser,insertDate,updateUser,updateTime,fenfaDate,testingCentreId,xuankaoqufen)  select  UUID_SHORT() id,t.scoreId,t.exampaperNum,t.groupNum,t.questionNum,s.questionScore,3 userNum,'W' isException,10 porder,t.studentId,t.isDelete,'F' status,-1 insertUser,t.insertDate,  -1 updateUser,'' updateTime,0 fenfaDate,t.testingCentreId,t.xuankaoqufen  from task t INNER JOIN score s on t.scoreId=s.id   where t.groupNum={groupNum} and t.studentId={studentId} and t.userNum=1;", args);
        }
        this.dao2._execute("update task set isException='F' where scoreId={scoreId} and userNum=3 ", args);
        return 1;
    }

    public Integer inserttask3(String scoreId, String questionNum) {
        try {
            Map args = new HashMap();
            args.put("scoreId", scoreId);
            this.dao2._execute("delete from task where scoreid={scoreId} and userNum='3'", args);
            long fenfaDate = System.currentTimeMillis();
            args.put("fenfaDate", Long.valueOf(fenfaDate));
            return Integer.valueOf(this.dao2._execute("insert  into  task (id,scoreId,exampaperNum,groupNum,questionNum,questionScore,userNum,isException,porder,studentId,isDelete,status, insertUser,insertDate,updateUser,updateTime,fenfaDate,testingCentreId,xuankaoqufen)  select  UUID_SHORT() id,scoreId,exampaperNum,groupNum,questionNum,0 questionScore,3 userNum,'F' isException,10 porder,studentId,isDelete,'F' status, -1 insertUser,insertDate,-1 updateUser,'' updateTime,{fenfaDate} fenfaDate  ,testingCentreId,xuankaoqufen  from task where scoreid={scoreId} limit 1", args));
        } catch (Exception e) {
            return null;
        }
    }

    public Integer inserttask3F(String scoreId, String questionNum) {
        try {
            long fenfaDate = System.currentTimeMillis();
            Map args = new HashMap();
            args.put("fenfaDate", Long.valueOf(fenfaDate));
            args.put("scoreId", scoreId);
            return Integer.valueOf(this.dao2._execute("insert  into  task (id,scoreId,exampaperNum,groupNum,questionNum,questionScore,userNum,isException,porder,studentId,isDelete,status, insertUser,insertDate,updateUser,updateTime,fenfaDate,testingCentreId,xuankaoqufen)  select  UUID_SHORT() id,scoreId,exampaperNum,groupNum,questionNum,0 questionScore,3 userNum,'F' isException,10 porder,studentId,isDelete,'F' status, -1 insertUser,insertDate,-1 updateUser,'' updateTime,{fenfaDate} fenfaDate  ,testingCentreId,xuankaoqufen  from task where scoreid={scoreId} limit 1", args));
        } catch (Exception e) {
            return null;
        }
    }

    public void updateTotalCount(String questionNum) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        String groupNum = this.dao2._queryStr("select groupNum from questiongroup_question where questionNum={questionNum}", args);
        args.put("groupNum", groupNum);
        String num = this.dao2._queryStr("select /* shard_host_HG=Write */ count(1) from task where groupNum={groupNum}", args);
        args.put("num", num);
        this.dao2._execute("update questiongroup set totalnum={num} where groupNum={groupNum}", args);
    }

    public Integer updateChooseQuestion(AwardPoint aw, String tagquestionNum) {
        String sql4;
        String sql5;
        String sql6;
        String sql2;
        String sql1 = "";
        new ArrayList();
        Map args = new HashMap();
        args.put("tagquestionNum", tagquestionNum);
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("ScoreId", aw.getScoreId());
        args.put("InsertDate", aw.getInsertDate());
        args.put("Id", aw.getId());
        args.put("QuestionNum", aw.getQuestionNum());
        Object gnum = this.dao2._queryObject("select questionNum from questiongroup_question where groupnum={tagquestionNum} limit {PageStart} ,1", args);
        args.put("gnum", gnum);
        if ("2".equals(aw.getGroupType())) {
            sql4 = "update   markerror   set  questionNum={tagquestionNum}   where    scoreId={ScoreId}";
            sql5 = "update remark    set  insertDate={InsertDate},  questionNum={gnum}  where  id={Id}  and  type='1'";
            sql6 = "  update remark   set  insertDate={InsertDate},   questionNum={gnum} where   scoreId={ScoreId}  and type='2'";
        } else {
            sql1 = "update score  a  set    a.questionNum={tagquestionNum} where  a.id={ScoreId}";
            sql4 = "update   markerror   set  questionNum={tagquestionNum}   where    scoreId={ScoreId}";
            sql5 = "update remark    set  insertDate={InsertDate} ,  questionNum={tagquestionNum} where  id={Id}  and  type='1'";
            sql6 = "  update remark   set    insertDate={InsertDate}, questionNum={tagquestionNum}  where   scoreId={ScoreId}  and type='2'";
        }
        if (aw.getTag() == null || aw.getTag().equals("")) {
            if ("2".equals(aw.getGroupType())) {
                sql1 = "update score  a  set    a.questionNum={gnum} where  a.id={ScoreId}";
                sql2 = " update  task   set  groupNum={tagquestionNum} ,questionNum={gnum} ,insertuser='-1',isException='F',status='F' where  id={Id}";
            } else {
                sql2 = " update  task   set  groupNum={gnum},questionNum={tagquestionNum},insertuser='-1',isException='F',status='F' where  id={Id}";
            }
        } else {
            Object o = this.dao2._queryObject("select count(1) from testquestion where questionNum={gnum}", args);
            String stro = String.valueOf(o);
            if ("2".equals(aw.getGroupType())) {
                if (stro.equals("0")) {
                    this.dao2._execute("update testquestion set groupNum={tagquestionNum},questionNum={gnum} where questionNum={QuestionNum}", args);
                }
                sql2 = " update  test_task   set  groupNum={tagquestionNum},questionNum={gnum} ,insertuser='-1',isException='F',status='F' where  id={Id} ";
            } else {
                if (stro.equals("0")) {
                    String str = "INSERT INTO testquestion(  scoreId  ,examPaperNum,questionNum,studentId,page,insertDate,status ,groupNum,insertuser)  select scoreId  ,examPaperNum," + gnum + ",studentId,page,insertDate,status ," + tagquestionNum + ",insertuser from testquestion where questionNum={QuestionNum} ";
                    this.dao2._execute("update testquestion set groupNum={tagquestionNum},questionNum={gnum} where questionNum={QuestionNum} ", args);
                }
                sql2 = " update   test_task   set  groupNum={gnum} ,questionNum={tagquestionNum} ,insertuser='-1',isException='F',status='F' where  id={Id}";
            }
        }
        if (StrUtil.isNotEmpty(aw.getTag())) {
            this.dao2._execute(sql1, args);
        }
        this.dao2._execute(sql2, args);
        this.dao2._execute(sql4, args);
        this.dao2._execute(sql5, args);
        this.dao2._execute(sql6, args);
        return 1;
    }

    public Integer updatechoosetask(AwardPoint aw) {
        Map args = new HashMap();
        args.put("OldquestionNum", aw.getOldquestionNum());
        args.put("GroupNum", aw.getGroupNum());
        Object o4 = this.dao2._queryObject("select count(1) from task where groupNum={OldquestionNum} ", args);
        args.put("o4", o4);
        this.dao2._execute("update questiongroup set totalnum={o4} where groupNum={OldquestionNum} ", args);
        Object o = this.dao2._queryObject("select count(1) from task where groupNum={GroupNum} ", args);
        args.put("o", o);
        return Integer.valueOf(this.dao2._execute("update questiongroup set totalnum={o}  where groupNum={GroupNum} ", args));
    }

    public List choosequestion(AwardPoint aw, String tagquestionNum) {
        Map args = new HashMap();
        args.put("Id", aw.getId());
        return this.dao2._queryBeanList("select count(1) from  task  where  id={Id}  ", AwardPoint.class, args);
    }

    public Integer updatechooseremark(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ScoreId", aw.getScoreId());
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        return Integer.valueOf(this.dao2._execute("update remark   set  rownum=rownum-1  where  scoreId={ScoreId}  and rownum>={Rownum} ", args));
    }

    public Integer updatechoosechongremark(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ScoreId", aw.getScoreId());
        args.put("InsertUser", aw.getInsertUser());
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        return Integer.valueOf(this.dao2._execute(" update remark   set  rownum=rownum-1   where  scoreId={ScoreId}   and insertUser={InsertUser}  and type='2'   and rownum>={Rownum} ", args));
    }

    public Integer updatenewremark(AwardPoint aw) {
        Map args = new HashMap();
        args.put("QuestionScore", aw.getQuestionScore());
        args.put("IsException", aw.getIsException());
        args.put("InsertDate", aw.getInsertUser());
        args.put("InsertUser", aw.getInsertUser());
        args.put("ConstTRUE", "T");
        args.put("Id", aw.getId());
        return Integer.valueOf(this.dao2._execute(" update remark set questionScore={QuestionScore} , isException={IsException} , insertDate={InsertDate} ,insertUser={InsertUser} ,status={ConstTRUE}  where id={Id} ", args));
    }

    public Integer updateremark(AwardPoint aw) {
        Map args = new HashMap();
        args.put("QuestionScore", aw.getQuestionScore());
        args.put("ConstTRUE", "T");
        args.put("InsertUser", aw.getInsertUser());
        args.put("Id", aw.getId());
        args.put("Description", aw.getDescription());
        args.put("ScoreId", aw.getScoreId());
        String sql1 = " update remark set  questionScore={QuestionScore} ,status={ConstTRUE} ,   insertUser={InsertUser} ,insertDate=NOW(),updateTime=Now()  where id={Id} ";
        if ("2".equals(aw.getType())) {
            sql1 = " delete from markerror where id={Id}";
        }
        this.dao2._execute(sql1, args);
        this.dao2._execute("update score   set questionScore={QuestionScore} ,isModify={ConstTRUE},description={Description},insertUser={InsertUser},insertDate=NOW()   where id={ScoreId} ", args);
        return 1;
    }

    public Integer updateremarkexception(String questionNum, String time, String insertUser, String score, String id, AwardPoint aw) {
        Map args = new HashMap();
        args.put("QuestionScore", aw.getQuestionScore());
        args.put("InsertDate", aw.getInsertDate());
        args.put("InsertUser", aw.getInsertUser());
        args.put("Status", aw.getStatus());
        args.put("IsException", aw.getIsException());
        args.put("Id", id);
        args.put("score", score);
        args.put("time", time);
        args.put("insertUser", insertUser);
        args.put("ScoreId", aw.getScoreId());
        this.dao2._execute("update remark  a  set  a.questionScore={QuestionScore} ,  a.insertDate={InsertDate}, a.insertUser={InsertUser},  a.status={Status} , a.isException={IsException} where  a.id={Id} ", args);
        this.dao2._execute("update score  a  set  a.questionScore={score},  a.insertDate={time} , a.isModify='T', a.insertUser={insertUser} where  id={ScoreId} ", args);
        return 1;
    }

    public AwardPoint maxupdatetime(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        return (AwardPoint) this.dao2._queryBean("select    max(IFNULL(updateTime, 0))  as   updateTime   from  task   where     exampaperNum={ExampaperNum} order  by   updateTime  desc ", AwardPoint.class, args);
    }

    public AwardPoint dangmaxTime(AwardPoint aw) {
        Map args = new HashMap();
        args.put("StudentId", aw.getScoreId());
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        return (AwardPoint) this.dao2._queryBean("select    max(updateTime)  as   updateTime   from  task   where   studentId = {StudentId} and exampaperNum={ExampaperNum}    order  by   updateTime  desc ", AwardPoint.class, args);
    }

    public AwardPoint remarkmaxTime(AwardPoint aw) {
        Map args = new HashMap();
        args.put("StudentId", aw.getStudentId());
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        return (AwardPoint) this.dao2._queryBean("select    max(updateTime)  as   updateTime   from  remark   where   studentId = {StudentId}  and exampaperNum={ExampaperNum}    order  by   updateTime  desc ", AwardPoint.class, args);
    }

    public List ping2(AwardPoint aw, String questionNum, String str) throws Exception {
        StringBuffer buffer = new StringBuffer();
        StringBuffer buffer2 = buffer.append("select    b.questionNum ,b.studentId,b.classNum,b.gradeNum,b.id,b.schoolNum ,b.updateTime , b.rownum,b.questionScore, c.fullScore ,b.scoreId, b.examPaperNum   from   questiongroup_question   a  left  join   task   b  on  a.exampaperNum=b.exampaperNum  and a.questionNum=b.questionNum  left  join   ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum} ) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum}))  c  on    c.id=b.questionNum  and   c.examPaperNum=b.exampaperNum  WHERE  c.examPaperNum={ExampaperNum} and b.rownum  is  not null  and    b.id IS NOT NULL   and   b.rownum={Rownum}  and  b.insertUser={InsertUser}  and  a.groupNum={GroupNum}   order  by   b.rownum, REPLACE(b.questionNum,'_','.')*1    asc  ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryBeanList(buffer2.toString(), AwardPoint.class, args);
    }

    public List ping3(AwardPoint aw, String questionNum, String str) throws Exception {
        StringBuffer buffer = new StringBuffer().append("select    b.questionNum ,b.studentId,b.classNum,b.gradeNum,b.id,b.schoolNum ,b.updateTime , b.rownum,b.questionScore, c.fullScore ,b.scoreId, b.examPaperNum,b.isException ,b.examinationRoomNum,b.status  from   questiongroup_question   a  left  join   task   b  on  a.exampaperNum=b.exampaperNum  and a.questionNum=b.questionNum  left  join    ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum}) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum}))  c  on  c.questionNum=b.questionNum  and  c.examPaperNum=b.exampaperNum  WHERE b.id IS NOT NULL  and  b.rownum  is  not null   and  c.examPaperNum={ExampaperNum}    and  b.rownum={Rownum} and  b.insertUser={InsertUser} and  a.groupNum={GroupNum}   order  by  replace(b.questionNum,'_','.')*1    asc  ");
        if (aw.getPageStart() >= 0) {
            buffer.append(" limit {PageStart}, {PageSize} ");
        }
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("GroupNum", aw.getGroupNum());
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("PageSize", Integer.valueOf(aw.getPageSize()));
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List noping(AwardPoint aw, String questionNum, String str) throws Exception {
        StringBuffer buffer = new StringBuffer().append("select    b.questionNum ,b.studentId,b.classNum,b.gradeNum,b.id,b.schoolNum ,b.updateTime, b.rownum,b.questionScore,b.scoreId,c.fullScore , b.examPaperNum,b.isException ,b.examinationRoomNum,b.status  from   questiongroup_question   a  left  join   task   b  on  a.exampaperNum=b.exampaperNum   and a.questionNum=b.questionNum  left  join    ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum}) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum}))  c   on  c.questionNum=b.questionNum  and  c.examPaperNum=b.exampaperNum   WHERE   c.examPaperNum={ExampaperNum} and  b.rownum  is  not null    and   b.id IS NOT NULL  and  b.rownum={Rownum} and  b.insertUser={InsertUser}  and  a.groupNum={GroupNum} order  by  replace(b.questionNum,'_','.')*1    asc  ");
        buffer.append(" limit {PageStart}, {PageSize} ");
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("GroupNum", aw.getGroupNum());
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("PageSize", Integer.valueOf(aw.getPageSize()));
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public AwardPoint sumList(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        args.put("GroupNum", aw.getGroupNum());
        return (AwardPoint) this.dao2._queryBean("select    max( b.rownum) as rownum    from   questiongroup_question   a  left  join   task   b    on  a.exampaperNum=b.exampaperNum   and a.questionNum=b.questionNum   WHERE b.id IS NOT NULL  and  b.examPaperNum={ExampaperNum}  and  a.questionNum={QuestionNum} and  b.rownum  is  not null    and b.insertUser={InsertUser} and  a.groupNum={GroupNum} ", AwardPoint.class, args);
    }

    public AwardPoint quesrow(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return (AwardPoint) this.dao2._queryBean("select   max(a.questionNum)  as  questionNum   from   questiongroup_question   a  left  join   task   b     on  a.exampaperNum=b.exampaperNum    and a.questionNum=b.questionNum  WHERE b.id IS NOT NULL   and  b.rownum  is  not null    and  b.examPaperNum={ExampaperNum}   and  a.groupNum={GroupNum} ", AwardPoint.class, args);
    }

    public AwardPoint sumremark(int exampaperNum, String questionNum) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        return (AwardPoint) this.dao2._queryBean(" select /* shard_host_HG=Write */ max(IFNULL(a.rownum, 0) ) as   rownum    from   remark     a    where   questionNum={questionNum}  and  exampaperNum={exampaperNum}   and   a.type ='1' ", AwardPoint.class, args);
    }

    public AwardPoint sumchongpan(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        return (AwardPoint) this.dao2._queryBean("select max(IFNULL(a.rownum, 0) ) as   rownum   from   remark     a     where      a.examPaperNum={ExampaperNum}  and   a.questionNum={QuestionNum}   and   a.type ='2' ", AwardPoint.class, args);
    }

    public AwardPoint maxupdateremarktime(AwardPoint aw) {
        return (AwardPoint) this.dao2._queryBean("select  max( IFNULL(a.rownum, 0) ) as   rownum  from   remark  a   where  a.type='1'  ", AwardPoint.class, null);
    }

    public AwardPoint remarkmaxRownum(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        return (AwardPoint) this.dao2._queryBean("select  max( IFNULL(a.rownum, 0) ) as   rownum  from   remark  a  where  a.type='1'      a.examPaperNum={ExampaperNum} and a.insertUser={InsertUser} ", AwardPoint.class, args);
    }

    public List quertyquestion(AwardPoint aw) {
        Map args = new HashMap();
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryBeanList("select   max(a.questionNum*1)  as questionNum ,min(a.questionNum*1)  as minquestionNum  from   task a left join questiongroup_question  b on  a.exampaperNum=b.exampaperNum  and a.questionNum=b.questionNum   and a.rownum={Rownum}   and  a.insertUser={InsertUser} where  b.groupNum={GroupNum}  order  by  rownum ,questionNum  asc  ", AwardPoint.class, args);
    }

    public AwardPoint minquestionNum(AwardPoint aw) {
        Map args = new HashMap();
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("GroupNum", aw.getGroupNum());
        return (AwardPoint) this.dao2._queryBean("select   max(a.questionNum*1)  as questionNum ,min(a.questionNum*1)  as minquestionNum  from   task a left join questiongroup_question  b on  a.exampaperNum=b.exampaperNum  and a.questionNum=b.questionNum   and a.rownum={Rownum}   and  a.insertUser={InsertUser}  where  b.groupNum={GroupNum}   order  by  rownum ,questionNum  asc  ", AwardPoint.class, args);
    }

    public List<Task> getPanFenTableList(Map<String, String> map, String userId) {
        String sql;
        String groupNum = map.get("groupNum");
        String pageNo = map.get("pageNo");
        String pageSize = map.get("pageSize");
        String limitSql = "";
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        if (pageNo != null && pageSize != null) {
            Integer page1 = Integer.valueOf(Integer.parseInt(pageNo));
            Integer page2 = Integer.valueOf(Integer.parseInt(pageSize));
            args.put("page1", Integer.valueOf((page1.intValue() - 1) * page2.intValue()));
            args.put("page2", page2);
            limitSql = " LIMIT {page1},{page2} ";
        }
        String exampaperNum = this.dao2._queryStr("select exampaperNum from questiongroup where groupNum={groupNum} ", args);
        String enforce = systemService.fenzu(String.valueOf(exampaperNum));
        if ("0".equals(enforce)) {
            sql = "SELECT t1.scoreId,t1.ext1,t1.ext2,t1.ext3 from( SELECT t.scoreId,'-1' ext1,  GROUP_CONCAT(t.schoolName,'-',t.realName,'-',t.username,'-',t.status ORDER BY t.status desc Separator ',') ext2,  ifnull(GROUP_CONCAT(t.note),'') ext3, GROUP_CONCAT(t.note1) note1  from (  \tSELECT t.scoreId,t.insertUser,sl.schoolName,u.realName,u.username,t.status,t.userNum,case when t.status='F' then CONCAT('需',if(t.userNum=3,3,2),'评') end note   ,case when t.status='F' then t.userNum end note1  from task t INNER JOIN(  \t\tSELECT scoreId,GROUP_CONCAT(`status`) str1 from task where groupNum={groupNum} GROUP BY scoreId  \t\tHAVING POSITION('F' IN str1) and POSITION('T' IN str1)  \t)t1 on t.scoreId=t1.scoreId  \tINNER JOIN user u on t.insertUser=u.id   \tINNER JOIN school sl on u.schoolNum=sl.id   \twhere t.groupNum={groupNum}  )t GROUP BY t.scoreId )t1 ORDER BY t1.note1 desc " + limitSql;
        } else {
            sql = "SELECT t1.scoreId,t1.ext1,t1.ext2,t1.ext3 from(  SELECT t.schoolGroupNum,t.schoolGroupName ext1,t.scoreId,  GROUP_CONCAT(t.schoolName,'-',t.realName,'-',t.username,'-',t.status ORDER BY t.status desc Separator ',') ext2,  ifnull(GROUP_CONCAT(t.note),'') ext3,  GROUP_CONCAT(t.note1) note1  from (  \tSELECT slg.schoolGroupNum,slg.schoolGroupName,  \t\tt.scoreId,t.insertUser,sl.schoolName,u.realName,u.username,t.status,t.userNum,case when t.status='F' then CONCAT('需',if(t.userNum=3,3,2),'评') end note  \t\t,case when t.status='F' then t.userNum end note1 from task t INNER JOIN(  \t\tSELECT scoreId,GROUP_CONCAT(`status`) str1 from task t  \t\twhere t.groupNum={groupNum} GROUP BY t.scoreId  \t\tHAVING POSITION('F' IN str1) and POSITION('T' IN str1)  \t)t1 on t.scoreId=t1.scoreId  \tINNER JOIN user u on t.insertUser=u.id  \tINNER JOIN  schoolgroup slg on u.schoolNum=slg.schoolNum  \tINNER JOIN school sl on u.schoolNum=sl.id   \twhere t.groupNum= {groupNum} )t GROUP BY t.scoreId   )t1 ORDER BY t1.schoolGroupNum asc,t1.note1 desc " + limitSql;
        }
        return this.dao2._queryBeanList(sql, Task.class, args);
    }

    public Map getReferenceNum(Map<String, String> map, String userId) {
        String sql;
        String examPaperNum = map.get("examPaperNum");
        String enforce = systemService.fenzu(String.valueOf(examPaperNum));
        if ("0".equals(enforce)) {
            sql = "SELECT q1.schoolGroupNum,q1.schoolGroupName,ifnull(q.ext1,0) ext1,q1.ext2,format(ifnull(q.ext1,0)/ifnull(q1.ext2,1)*100,1) ext3,q1.ext4 from(   SELECT -1 schoolGroupNum,null schoolGroupName,count(distinct r.studentId) ext1 from regexaminee r   where r.examPaperNum={examPaperNum} )q RIGHT JOIN(   SELECT -1 schoolGroupNum,null schoolGroupName,count(1) ext2,count(distinct n.schoolNum) ext4 from examinationnum n    LEFT JOIN exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum   where p.examPaperNum={examPaperNum} )q1 on q.schoolGroupNum=q1.schoolGroupNum ORDER BY q1.schoolGroupName";
        } else {
            sql = "SELECT q1.schoolGroupNum,q1.schoolGroupName,ifnull(q.ext1,0) ext1,q1.ext2,format(ifnull(q.ext1,0)/ifnull(q1.ext2,1)*100,1) ext3,q1.ext4 from(   SELECT ifnull(slg.schoolGroupNum,-2) schoolGroupNum,slg.schoolGroupName,count(distinct r.studentId) ext1 from regexaminee r   LEFT JOIN student s on r.studentId=s.id   LEFT JOIN schoolgroup slg on s.schoolNum=slg.schoolNum   where r.examPaperNum={examPaperNum}  GROUP BY slg.schoolGroupNum )q RIGHT JOIN(   SELECT ifnull(slg.schoolGroupNum,-2) schoolGroupNum,ifnull(slg.schoolGroupName,'未分组') schoolGroupName,count(1) ext2,count(distinct n.schoolNum) ext4 from examinationnum n    LEFT JOIN exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum   LEFT JOIN schoolgroup slg on n.schoolNum=slg.schoolNum   where p.examPaperNum={examPaperNum} GROUP BY slg.schoolGroupNum )q1 on q.schoolGroupNum=q1.schoolGroupNum ORDER BY q1.schoolGroupName";
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        List<?> _queryBeanList = this.dao2._queryBeanList(sql, Schoolgroup.class, args);
        Map returnMap = new HashMap();
        returnMap.put("dataBody", _queryBeanList);
        return returnMap;
    }

    public List getNoOpenQuestionGroupList(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String subjectNum = map.get(Const.EXPORTREPORT_subjectNum);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryBeanList("SELECT q.groupName from questiongroup q LEFT JOIN exampaper e on q.examPapernum=e.examPaperNum  where e.examNum={examNum} and e.gradeNum={gradeNum} and e.subjectNum={subjectNum} and q.stat=0", QuestionGroup.class, args);
    }

    public List getNoOpenTestingCentreList(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String subjectNum = map.get(Const.EXPORTREPORT_subjectNum);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryBeanList("SELECT tt.testingCentreName from testingcentredis t  LEFT JOIN testingcentre tt on t.testingcentreId=tt.id  LEFT JOIN exampaper e on t.examPapernum=e.examPaperNum  where e.examNum={examNum} and e.gradeNum={gradeNum} and e.subjectNum={subjectNum} and t.isDis=0", Testingcentre.class, args);
    }

    public List remarksize(String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        return this.dao2._queryBeanList("select  /* shard_host_HG=Write */ *  from  remark  a   where   a.scoreId={scoreId} and  type='1' ", AwardPoint.class, args);
    }

    public Integer deleteremark(int remark_id) {
        Map args = new HashMap();
        args.put("remark_id", Integer.valueOf(remark_id));
        return Integer.valueOf(this.dao2._execute("delete  from   remark  where  id={remark_id}  and  type='1' ", args));
    }

    public Integer deletemarkerror(String remarkId) {
        Map args = new HashMap();
        args.put("remarkId", remarkId);
        return Integer.valueOf(this.dao2._execute("delete from markerror    where  scoreId={remarkId} ", args));
    }

    public List markerrorsize(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ScoreId", aw.getScoreId());
        return this.dao2._queryBeanList("select  *  from  markerror   a   where  a.scoreId={ScoreId} ", AwardPoint.class, args);
    }

    public List countcai(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList("  select a.count1,b.count2,c.count3,a.count1/b.count2 as zb  from   ( select  count(1)  as count1        from  remark   a  \t   where   a.type ='1'    and  a.examPaperNum={ExampaperNum} \t   and     a.status='T'   and   a.questionNum={QuestionNum}  ) a ,\t  ( select  count(1)  as count2   from  remark   a          where   a.type ='1'  and  a.examPaperNum={ExampaperNum}  and   a.questionNum={QuestionNum}) b,   ( select  count(1)  as count3        from  remark     \t   where   type ='1'    and examPaperNum={ExampaperNum}  \t   and     status='T'   and   questionNum={QuestionNum}  and insertuser={InsertUser}) c   where   1=1", AwardPoint.class, args);
    }

    public List countchong(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("UserNum", aw.getUserNum());
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryBeanList(" select IFNULL(a.count1,0) as count1,    IFNULL(b.count2,0)  as  count2, IFNULL(round(a.count1/b.count2,2)*100,0.00)  as zb  from   ( select  count(1)  as count1,examPaperNum,questionNum   from  remark   a  where   a.type ='2'  and  a.examPaperNum={ExampaperNum} and     a.status='T'  and a.userNum={UserNum}  and   a.questionNum={QuestionNum} ) a   left join  ( select  count(1)  as count2, examPaperNum,questionNum from  remark   a  where   a.type ='2'  and  a.examPaperNum={ExampaperNum}  and   a.questionNum={QuestionNum}  and a.userNum={UserNum} ) b   on 1=1 ", AwardPoint.class, args);
    }

    public List countcaicount2(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryBeanList(" select b.count2  from   ( select  count(1)  as count2, examPaperNum,questionNum from  remark   a  where   a.type ='1'  and  a.status='T'  and  a.examPaperNum={ExampaperNum}    and   a.questionNum={QuestionNum}) b ", AwardPoint.class, args);
    }

    public List questionstudentList(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryBeanList("select  distinct a.studentId   from  score   a left join  questiongroup_question  b    on  a.questionNum=b.questionNum     and a.examPaperNum=b.exampaperNum    where   a.examPaperNum={ExampaperNum}   and  b.groupNum={GroupNum} ", AwardPoint.class, args);
    }

    public List errorRateList(String questionNum) {
        String sql = " select  a.errorRate  from  ( (select id,exampaperNum,fullScore,errorRate from define where id={questionNum} )  union all (select id,exampaperNum,fullScore,errorRate  from subdefine WHERE id={questionNum} ))a";
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        return this.dao2._queryBeanList(sql, AwardPoint.class, args);
    }

    public List<AwardPoint> getYuejuanExam(String userNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("STATUS_EXAM_COMPLETE", "9");
        return this.dao2._queryBeanList("SELECT DISTINCT exam.examNum num,exam.examName name  FROM(SELECT exampaperNum,groupNum FROM questiongroup_user WHERE userNum={userNum} and userType<>2 ) g  left join  questiongroup qg on qg.groupNum=g.groupNum LEFT JOIN  exampaper  ep ON g.exampaperNum=ep.exampaperNum  left JOIN  exam  on exam.examNum=ep.examNum   and exam.isDelete='F' and exam.status!= {STATUS_EXAM_COMPLETE} where qg.stat='1' and exam.examNum is not null ", AwardPoint.class, args);
    }

    public List<AwardPoint> getRemarkExam(String userNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("STATUS_EXAM_COMPLETE", "9");
        return this.dao2._queryBeanList("SELECT DISTINCT exam.examNum num,exam.examName name  FROM(SELECT exampaperNum,groupNum FROM questiongroup_user WHERE userNum={userNum} and userType<>0 ) g  LEFT JOIN  exampaper  ep ON g.exampaperNum=ep.exampaperNum  left JOIN  exam  on exam.examNum=ep.examNum  and exam.isDelete='F' and exam.status!= {STATUS_EXAM_COMPLETE} where exam.examNum is not null ", AwardPoint.class, args);
    }

    public List<AwardPoint> getYuejuanGrade(Integer exam, String insertUser) {
        Map args = new HashMap();
        args.put("insertUser", insertUser);
        args.put("exam", exam);
        args.put("exam_type_online", "0");
        args.put("FALSE", "F");
        return this.dao2._queryBeanList(" SELECT distinct grade.gradeNum  as  num ,grade.gradeName  as  name  FROM( SELECT qu.exampaperNum,qu.groupNum from questiongroup_user qu INNER JOIN(  \tSELECT e.pexamPaperNum from questiongroup_user qu  \tINNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum  \twhere qu.userNum={insertUser} and qu.userType<>2  )qu1 on qu.exampaperNum=qu1.pexamPaperNum where qu.userNum={insertUser} and qu.userType<>2  and qu.groupNum is not null  ) g  left join  questiongroup qg on qg.groupNum=g.groupNum   LEFT JOIN   ( select  gradeNum, exampaperNum  from    exampaper   e   WHERE e.examNum = {exam}  and e.type={exam_type_online}   AND e.isHidden={FALSE}  ) ep    ON   g.exampaperNum=ep.exampaperNum   left JOIN    basegrade grade     on ep.gradeNum=grade.gradeNum where  qg.stat='1'   and ep.exampaperNum  is   not   null ", AwardPoint.class, args);
    }

    public List<AwardPoint> getRemarkGrade(Integer exam, String insertUser) {
        Map args = new HashMap();
        args.put("insertUser", insertUser);
        args.put("exam", exam);
        args.put("exam_type_online", "0");
        args.put("FALSE", "F");
        return this.dao2._queryBeanList(" SELECT distinct grade.gradeNum  as  num ,grade.gradeName  as  name  FROM(  \tSELECT e.pexamPaperNum exampaperNum,qu.groupNum from questiongroup_user qu  \tINNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum  \twhere qu.userNum={insertUser} and qu.userType<>0 ) g   LEFT JOIN   ( select  gradeNum, exampaperNum  from    exampaper   e   WHERE e.examNum = {exam}  and e.type={exam_type_online}     AND e.isHidden={FALSE}  ) ep    ON   g.exampaperNum=ep.exampaperNum    left JOIN  basegrade  grade      on ep.gradeNum=grade.gradeNum where  ep.exampaperNum  is   not   null ", AwardPoint.class, args);
    }

    public List<AwardPoint> getYuejuanSubject(Integer exam, Integer grade, String insertUser) {
        Map args = new HashMap();
        args.put("insertUser", insertUser);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("FALSE", "F");
        args.put("exam_type_online", "0");
        return this.dao2._queryBeanList("   SELECT distinct   subject.subjectNum  as  num ,subject.subjectName  as  name    FROM(SELECT qu.exampaperNum,qu.groupNum from questiongroup_user qu INNER JOIN(  \tSELECT e.pexamPaperNum from questiongroup_user qu  \tINNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum  \twhere qu.userNum={insertUser} and qu.userType<>2  )qu1 on qu.exampaperNum=qu1.pexamPaperNum where qu.userNum={insertUser} and qu.userType<>2 and qu.groupNum is not null ) g  left join  questiongroup qg on qg.groupNum=g.groupNum    LEFT JOIN   ( select  gradeNum, exampaperNum,subjectNum  from    exampaper   e     WHERE e.examNum = {exam}    and e.gradeNum = {grade} AND e.isHidden={FALSE}  and e.type={exam_type_online} ) ep    ON          g.exampaperNum=ep.exampaperNum    left JOIN   (select subjectNum, subjectName from  subject)subject    on       ep.subjectNum=subject.subjectNum    where  qg.stat='1'   and   ep.exampaperNum  is   not   null ", AwardPoint.class, args);
    }

    public List<AwardPoint> getRemarkSubject(Integer exam, Integer grade, String insertUser) {
        Map args = new HashMap();
        args.put("insertUser", insertUser);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("FALSE", "F");
        args.put("exam_type_online", "0");
        return this.dao2._queryBeanList(" SELECT distinct   subject.subjectNum  as  num ,subject.subjectName  as  name    FROM(  \tSELECT e.pexamPaperNum exampaperNum,qu.groupNum from questiongroup_user qu  \tINNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum  \twhere qu.userNum={insertUser} and qu.userType<>0 ) g    LEFT JOIN   ( select  gradeNum, exampaperNum,subjectNum  from    exampaper   e     WHERE e.examNum = {exam}    and e.gradeNum = {grade} AND e.isHidden={FALSE} and e.type={exam_type_online} ) ep    ON          g.exampaperNum=ep.exampaperNum    left JOIN   (select subjectNum, subjectName from  subject)subject    on       ep.subjectNum=subject.subjectNum    where ep.exampaperNum  is   not   null ", AwardPoint.class, args);
    }

    public List<AwardPoint> getExam(String userNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("STATUS_EXAM_COMPLETE", "9");
        String stat = "";
        String num = this.dao2._queryStr(" SELECT count(1) FROM questiongroup_user WHERE userNum={userNum} and userType=2 ", args);
        if ("0".equals(num)) {
            stat = " and qg.stat='1' ";
        }
        String sql = "SELECT DISTINCT exam.examNum num,exam.examName name  FROM(SELECT exampaperNum,groupNum FROM questiongroup_user WHERE userNum={userNum} ) g  left join  questiongroup qg on qg.groupNum=g.groupNum LEFT JOIN  exampaper  ep ON g.exampaperNum=ep.exampaperNum  left JOIN  exam  on exam.examNum=ep.examNum   and exam.isDelete='F' and exam.status!={STATUS_EXAM_COMPLETE} where    exam.examNum  is  not  null " + stat;
        return this.dao2._queryBeanList(sql, AwardPoint.class, args);
    }

    public List<AwardPoint> getGrade(int exam, String insertUser) {
        String stat = "";
        Map args = new HashMap();
        args.put("insertUser", insertUser);
        args.put("exam", Integer.valueOf(exam));
        args.put("exam_type_online", "0");
        args.put("FALSE", "F");
        String num = this.dao2._queryStr(" SELECT count(1) FROM questiongroup_user WHERE userNum={insertUser} and userType=2 ", args);
        if ("0".equals(num)) {
            stat = "and qg.stat='1' ";
        }
        String sql = "  SELECT     distinct   grade.gradeNum  as  num ,grade.gradeName  as  name   FROM       ( SELECT qu.exampaperNum,qu.groupNum from questiongroup_user qu INNER JOIN(  \tSELECT e.pexamPaperNum from questiongroup_user qu  \tINNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum  \twhere qu.userNum={insertUser} )qu1 on qu.exampaperNum=qu1.pexamPaperNum where qu.userNum={insertUser} and qu.groupNum is not null ) g  left join  questiongroup qg on qg.groupNum=g.groupNum   LEFT JOIN   ( select  gradeNum, exampaperNum  from    exampaper   e   WHERE e.examNum = {exam}  and e.type={exam_type_online}  AND e.isHidden={FALSE} ' ) ep   ON   g.exampaperNum=ep.exampaperNum    left JOIN    grade     on ep.gradeNum=grade.gradeNum   where  ep.exampaperNum  is   not   null " + stat;
        return this.dao2._queryBeanList(sql, AwardPoint.class, args);
    }

    public List<AwardPoint> getGrade2(Integer exam, String insertUser) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("exam_type_online", "0");
        args.put("FALSE", "F");
        return this.dao2._queryBeanList("select  distinct  a.num,a.name   from  (SELECT e.gradeNum num ,g.gradeName name,e.examPaperNum    from  (SELECT examPaperNum,gradeNum FROM exampaper WHERE examNum={exam} AND type={exam_type_online}  AND isHidden={FALSE} ) e   LEFT JOIN basegrade g on e.gradeNum = g.gradeNum    )a  left  join  questiongroup_user    b   on  a.examPaperNum=b.exampaperNum  where    b.groupNum  is  not  null   ORDER BY a.num*1", AwardPoint.class, args);
    }

    public String getSubjectCount2(Integer exam, Integer grade, String userNum) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("FALSE", "F");
        args.put("exam_type_online", "0");
        return this.dao2._queryStr("select  a.count as  count from  (SELECT count(1) as count,e.examPaperNum  from exampaper e LEFT JOIN subject s on e.subjectNum = s.subjectNum WHERE e.examNum = {exam} and e.gradeNum = {grade}  AND  e.isHidden={FALSE} and e.type={exam_type_online} ) a left  join  questiongroup_user    b   on  a.examPaperNum=b.exampaperNum   where    b.groupNum  is  not  null ", args);
    }

    public List<AwardPoint> getSubject(int exam, int grade, String insertUser) {
        String stat = "";
        Map args = new HashMap();
        args.put("insertUser", insertUser);
        args.put("exam", Integer.valueOf(exam));
        args.put("grade", Integer.valueOf(grade));
        args.put("FALSE", "F");
        args.put("exam_type_online", "0");
        String num = this.dao2._queryStr(" SELECT count(1) FROM questiongroup_user WHERE userNum={insertUser} and userType=2 ", args);
        if ("0".equals(num)) {
            stat = "and qg.stat='1' ";
        }
        String sql = "   SELECT       distinct   subject.subjectNum  as  num ,subject.subjectName  as  name    FROM        (SELECT qu.exampaperNum,qu.groupNum from questiongroup_user qu INNER JOIN(  \tSELECT e.pexamPaperNum from questiongroup_user qu  \tINNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum  \twhere qu.userNum={insertUser} )qu1 on qu.exampaperNum=qu1.pexamPaperNum where qu.userNum={insertUser} and qu.groupNum is not null ) g  left join  questiongroup qg on qg.groupNum=g.groupNum    LEFT JOIN   ( select  gradeNum, exampaperNum,subjectNum  from    exampaper   e     WHERE e.examNum = {exam}   and e.gradeNum = {grade} AND e.isHidden={FALSE} and e.type={exam_type_online} ) ep    ON          g.exampaperNum=ep.exampaperNum    left JOIN   (select subjectNum, subjectName from  subject)subject    on       ep.subjectNum=subject.subjectNum    where    ep.exampaperNum  is   not   null " + stat;
        return this.dao2._queryBeanList(sql, AwardPoint.class, args);
    }

    public List<AwardPoint> getSubject2(Integer exam, Integer grade, String insertUser) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("FALSE", "F");
        args.put("exam_type_online", "0");
        return this.dao2._queryBeanList("SELECT  DISTINCT  a.name,a.num   FROM (SELECT  s.subjectName name,e.subjectNum num,e.examPaperNum   FROM (SELECT examPaperNum,subjectNum FROM exampaper WHERE examNum={exam} AND gradeNum={grade}   AND isHidden={FALSE} AND type={exam_type_online} ) e   LEFT JOIN subject s on e.subjectNum = s.subjectNum    )a   left  join  questiongroup_user   b  on  a.examPaperNum=b.exampaperNum    left  join  questiongroup   qg  on  a.examPaperNum=qg.exampaperNum    where  qg.stat=1 and  b.groupNum  is  not  null ", AwardPoint.class, args);
    }

    public List<Userposition> jzth(String jie, String num, int examNum) {
        Map args = new HashMap();
        args.put("num", num);
        args.put("jie", jie);
        args.put(Const.EXPORTREPORT_examNum, Integer.valueOf(examNum));
        return this.dao2._queryBeanList("select DISTINCT  e.subjectNum from   ( select * from questiongroup_user )  q  LEFT JOIN  (\t\tselect * from exampaper where examNum={examNum} ) e  ON q.exampaperNum=e.examPaperNum   where q.userNum={num} and e.id IS NOT NULL  ", Userposition.class, args);
    }

    public List groupavgcount(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryBeanList(" SELECT  a.questionNum,case when b.count2=0 then 0   else    round(a.count1/b.count2 ,1)  end as count,a.count1 as  count1,b.count2 as count2 from  (   SELECT COUNT(1)  as count1,t.questionNum,t.exampaperNum  FROM  (SELECT exampaperNum,questionNum FROM questiongroup_question   WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum} ) q  LEFT JOIN task  t   ON t.exampaperNum=q.exampaperNum  and  t.questionNum=q.questionNum  group  by  t.questionNum  ) a   left  join   (    select  count( distinct a.userNum )  as  count2   from  onlineuser a  left  join   questiongroup_user  b  on a.userNum=b.userNum    and   b.exampaperNum={ExampaperNum} and   b.groupNum={GroupNum} ) b   on  1=1 ", AwardPoint.class, args);
    }

    public AwardPoint sumgroupavgcount(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return (AwardPoint) this.dao2._queryBean(" SELECT  a.questionNum,case when b.count2=0 then 0   else    round(a.count1/b.count2 ,1)  end as count,a.count1 as  count1,b.count2 as count2 from  (  SELECT COUNT(1)  as count1,t.questionNum,t.exampaperNum  FROM  (SELECT exampaperNum,questionNum FROM questiongroup_question    WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum} ) q  LEFT JOIN task  t   ON t.exampaperNum=q.exampaperNum  and  t.questionNum=q.questionNum    ) a   left  join  (   select  count( distinct a.userNum )  as  count2   from  onlineuser a  left  join   questiongroup_user  b  on a.userNum=b.userNum   and   b.exampaperNum={ExampaperNum}  and   b.groupNum={GroupNum} ) b   on  1=1 ", AwardPoint.class, args);
    }

    public List groupavgmarkrrate(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryBeanList(" select   round(a.count1/b.count2,2)*100 as count, b.questionNum from  (    select  b.questionNum,   b.exampaperNum,   count(a.id)as count1  from  (  SELECT exampaperNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum} AND groupNum={GroupNum} ) b    left  join (select id,questionNum,exampaperNum from  markerror   where  exampaperNum={ExampaperNum}) a    on    a.exampaperNum=b.exampaperNum  and   a.questionNum=b.questionNum    group  by  b.questionNum    ) a   left  join   (SELECT COUNT(1) as count2 ,q.questionNum,q.exampaperNum   FROM  ( \tSELECT qq.exampaperNum,qq.questionNum,qm.makType  \tFROM \t(SELECT exampaperNum,groupNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum}) qq \tLEFT JOIN questiongroup_mark_setting qm ON qq.groupNum=qm.groupNum AND qq.exampaperNum=qm.exampaperNum  ) q   LEFT JOIN task  s ON s.examPaperNum=q.exampaperNum AND s.questionNum=q.questionNum  group  by  s.questionNum ) b   on  a.examPaperNum=b.exampaperNum AND a.questionNum=b.questionNum  ", AwardPoint.class, args);
    }

    public AwardPoint sumgroupavgmarkrrate(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return (AwardPoint) this.dao2._queryBean(" select   round(a.count1/b.count2,2)*100 as count, b.questionNum from  (   select  b.questionNum,   b.exampaperNum,  count(a.id)as count1   from  (  SELECT exampaperNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum} AND groupNum={GroupNum}  ) b    left  join (select id,questionNum,exampaperNum from  markerror   where  exampaperNum={ExampaperNum}) a    on    a.exampaperNum=b.exampaperNum  and   a.questionNum=b.questionNum  ) a   left  join   (SELECT COUNT(1) as count2 ,q.questionNum,q.exampaperNum   FROM   ( \tSELECT qq.exampaperNum,qq.questionNum,qm.makType  FROM \t(SELECT exampaperNum,groupNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum}) qq \tLEFT JOIN questiongroup_mark_setting qm ON qq.groupNum=qm.groupNum AND qq.exampaperNum=qm.exampaperNum  ) q   LEFT JOIN task  s ON s.examPaperNum=q.exampaperNum AND s.questionNum=q.questionNum  group  by  s.questionNum ) b   on  a.examPaperNum=b.exampaperNum AND a.questionNum=b.questionNum  ", AwardPoint.class, args);
    }

    public List groupmarkrrate(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList(" select   IFNULL(round(a.count1/b.count2,2)*100,0.00)  as count  from   (    select  b.questionNum,   b.exampaperNum,   count(a.id)as count1   from  (  SELECT exampaperNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum} AND groupNum={GroupNum} ) b    left  join (select id,questionNum,exampaperNum from  markerror   where  exampaperNum={ExampaperNum} and insertUser={InsertUser} ) a    on    a.exampaperNum=b.exampaperNum  and   a.questionNum=b.questionNum    group  by  b.questionNum    ) a   left  join   (SELECT COUNT(1) as count2 ,q.questionNum,q.exampaperNum  FROM   ( \tSELECT qq.exampaperNum,qq.questionNum,qm.makType  \tFROM \t(SELECT exampaperNum,groupNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum}) qq \tLEFT JOIN questiongroup_mark_setting qm ON qq.groupNum=qm.groupNum AND qq.exampaperNum=qm.exampaperNum  ) q   LEFT JOIN task  s ON s.examPaperNum=q.exampaperNum AND s.questionNum=q.questionNum   where      s.insertUser={InsertUser}  group  by  s.questionNum ) b   on  a.examPaperNum=b.exampaperNum AND a.questionNum=b.questionNum  ", AwardPoint.class, args);
    }

    public AwardPoint sumgroupmarkrrate(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        return (AwardPoint) this.dao2._queryBean(" select   round(a.count1/b.count2,2)*100 as count,b.questionNum from   (    select  b.questionNum,   b.exampaperNum,   count(a.id)as count1   from  (  SELECT exampaperNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum} AND groupNum={GroupNum} ) b    left  join (select id,questionNum,exampaperNum from  markerror   where  exampaperNum={ExampaperNum} and insertUser={InsertUser} ) a    on    a.exampaperNum=b.exampaperNum  and   a.questionNum=b.questionNum    ) a   left  join   (SELECT COUNT(1) as count2 ,q.questionNum,q.exampaperNum   FROM   ( \tSELECT qq.exampaperNum,qq.questionNum,qm.makType  \tFROM \t(SELECT exampaperNum,groupNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum}) qq \tLEFT JOIN questiongroup_mark_setting qm ON qq.groupNum=qm.groupNum AND qq.exampaperNum=qm.exampaperNum  ) q   LEFT JOIN task  s ON s.examPaperNum=q.exampaperNum AND s.questionNum=q.questionNum   where      s.insertUser={InsertUser}  group  by  s.questionNum  ) b   on  a.examPaperNum=b.exampaperNum AND a.questionNum=b.questionNum  ", AwardPoint.class, args);
    }

    public List useravgscore(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList("SELECT   round(avg(t.questionScore),2) as count ,t.questionNum   FROM  ( SELECT exampaperNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum}) q   LEFT JOIN task  t ON t.exampaperNum=q.exampaperNum AND t.questionNum=q.questionNum   where t.insertUser={InsertUser} group  by  t.questionNum  ", AwardPoint.class, args);
    }

    public List groupavgscore(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryBeanList("SELECT     round(avg(t.questionScore),2)  as count ,t.questionNum  FROM  ( SELECT exampaperNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum}) q   LEFT JOIN task  t ON t.exampaperNum=q.exampaperNum AND t.questionNum=q.questionNum  group  by  t.questionNum  ", AwardPoint.class, args);
    }

    public AwardPoint sumuseravgscore(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        return (AwardPoint) this.dao2._queryBean("SELECT   round(avg(t.questionScore),2) as count ,t.questionNum   FROM  ( SELECT exampaperNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum} ) q   LEFT JOIN task  t ON t.exampaperNum=q.exampaperNum AND t.questionNum=q.questionNum   where t.insertUser={InsertUser} ", AwardPoint.class, args);
    }

    public AwardPoint sumgroupavgscore(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return (AwardPoint) this.dao2._queryBean("SELECT   round(avg(t.questionScore),2)  as count ,t.questionNum  FROM  ( SELECT exampaperNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum} ) q   LEFT JOIN task  t ON t.exampaperNum=q.exampaperNum AND t.questionNum=q.questionNum ", AwardPoint.class, args);
    }

    public AwardPoint groupsumcount(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return (AwardPoint) this.dao2._queryBean("SELECT IF(q.makType='0',COUNT(1),COUNT(1)*2  ) as  count    FROM   ( \tSELECT qq.exampaperNum,qq.questionNum,qm.makType  \tFROM  \t(SELECT exampaperNum,groupNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum}   AND groupNum={GroupNum}) qq   LEFT JOIN questiongroup_mark_setting qm ON qq.groupNum=qm.groupNum AND qq.exampaperNum=qm.exampaperNum   ) q   LEFT JOIN score s ON s.examPaperNum=q.exampaperNum AND s.questionNum=q.questionNum   AND  s.isAppend!='T'  ", AwardPoint.class, args);
    }

    public AwardPoint groupcompletecount(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("TRUE", "T");
        return (AwardPoint) this.dao2._queryBean("SELECT count(1) as count    FROM   (  SELECT exampaperNum,questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum} AND groupNum={GroupNum} ) q  LEFT JOIN task  t ON t.exampaperNum=q.exampaperNum AND t.questionNum=q.questionNum  where t.status={TRUE} ", AwardPoint.class, args);
    }

    public Integer saveimage(AwardPoint obj) {
        return Integer.valueOf(this.dao2.save("questionCommentImage", obj));
    }

    public Map<String, Object> getPropertys(Object object) {
        Map<String, Object> map = new HashMap<>();
        try {
            Class<?> objClass = object.getClass();
            Field[] fields = objClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(object));
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List tulist(AwardPoint aw, String id) {
        Map args = new HashMap();
        args.put("id", id);
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList("select  id from  remarkimg    where  scoreId={id} and  insertUser={InsertUser} ", AwardPoint.class, args);
    }

    public Integer deletetulist(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ScoreId", aw.getScoreId());
        args.put("InsertUser", aw.getInsertUser());
        return Integer.valueOf(this.dao2._execute("delete  from  remarkimg    where   scoreId={ScoreId}   and  insertUser={InsertUser} ", args));
    }

    public List tulistcai(AwardPoint aw, String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList("select  id  from  remarkimg    where   scoreId={scoreId}  and  insertUser={InsertUser} ", AwardPoint.class, args);
    }

    public Integer deletecaitulist(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("StudentId", aw.getStudentId());
        return Integer.valueOf(this.dao2._execute("delete  from  questioncommentimage    where   exampaperNum={ExampaperNum}   and questionNum={QuestionNum} and studentId={StudentId} ", args));
    }

    public Integer listminrownum(int exampaperNum, String insertUser, String groupNum) {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        return this.dao2._queryInt(" SELECT IFNULL(MIN(rownum),-11)   as   rownum      FROM  task   WHERE   exampaperNum={exampaperNum} and    groupNum={groupNum} AND    insertUser={insertUser}  AND    STATUS='F'", args);
    }

    public List ping(AwardPoint aw, String questionNum, String str) throws Exception {
        StringBuffer buffer = new StringBuffer().append("select    b.questionNum ,b.studentId, b.status, b.classNum,b.gradeNum,b.scoreId,b.id,b.schoolNum ,b.updateTime , b.rownum,b.questionScore, c.fullScore , b.examPaperNum,b.isException ,b.examinationRoomNum,b.insertUser  from   questiongroup_question   a  left  join   task   b  on  a.exampaperNum=b.exampaperNum  and a.questionNum=b.questionNum  left  join    ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum}) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum} ))  c   on  c.questionNum=b.questionNum  and  c.examPaperNum=b.exampaperNum   WHERE  c.examPaperNum={ExampaperNum}  and   a.groupNum={GroupNum} and   b.id IS NOT NULL  and  b.rownum  is  not null   and   b.status='F'  and   b.insertUser={InsertUser}  order  by   b.rownum, REPLACE(b.questionNum,'_','.')*1  asc  ");
        if (aw.getPageStart() >= 0) {
            buffer.append(" limit {PageStart}, {PageSize} ");
        }
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("PageSize", Integer.valueOf(aw.getPageSize()));
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public List gaiquestNumList(AwardPoint aw) {
        Map args = new HashMap();
        args.put("GroupNum", aw.getGroupNum());
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        return this.dao2._queryBeanList(" select  distinct  questionNum    from     task   where  groupNum={GroupNum} and exampaperNum={ExampaperNum} ", AwardPoint.class, args);
    }

    public List yichangzb(AwardPoint aw) {
        String sql;
        if (aw.getTag() == null || aw.getTag().equals("")) {
            sql = " select  \tCASE  WHEN    b.count=0 or b.count is null  THEN 0 \t\tELSE ROUND(IFNULL(c.count,0)/b.count,2) \tEND zb ,   IFNULL(a.count,0) as  count1,   IFNULL(b.count,0)   as   count2,   IFNULL(c.count ,0)  as  count \tfrom   \t(  select  COUNT(1) as  count,groupNum, exampaperNum  from  task  where   exampaperNum={ExampaperNum} and  groupNum={GroupNum}  and    isException='p'   and  insertUser={InsertUser} )a left  join  (select  COUNT(1) as count,groupNum, exampaperNum   from  task  where   exampaperNum={ExampaperNum} and  groupNum={GroupNum}  and   insertUser={InsertUser} and    isException!='F' and isException!='C'  )b     on   1=1      left  join  (       select  COUNT(1) as  count,groupNum, exampaperNum  from  task  where   exampaperNum={ExampaperNum}\t and  groupNum={GroupNum}    \t\t\t\t and    isException='p'  and  insertUser={InsertUser}         ) c       on   1=1  ";
        } else {
            sql = " select  \tCASE  WHEN    b.count=0 or b.count is null  THEN 0 \t\tELSE ROUND(IFNULL(c.count,0)/b.count,2) \tEND zb ,   IFNULL(a.count,0) as  count1,   IFNULL(b.count,0)   as   count2,    IFNULL(c.count ,0)  as  count  \tfrom  \t(  select  COUNT(1) as  count,groupNum, exampaperNum  from  test_task  where   exampaperNum={ExampaperNum} and  groupNum={GroupNum}  and    isException='p'   and  insertUser={InsertUser} )a left  join  (select  COUNT(1) as count,groupNum, exampaperNum   from  test_task  where   exampaperNum={ExampaperNum} and  groupNum={GroupNum}  and   insertUser={InsertUser} and    isException!='F' and isException!='C' )b     on   1=1        left  join  (       select  COUNT(1) as  count,groupNum, exampaperNum  from  test_task  where   exampaperNum={ExampaperNum} and  groupNum={GroupNum}   \t\t\t\t and    isException='p'   and  insertUser={InsertUser}        ) c     on   1=1  ";
        }
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList(sql, AwardPoint.class, args);
    }

    public List yichangcaidu(AwardPoint aw) {
        String sql = " select  \tCASE  WHEN   b.count=0 or b.count is null  THEN 0 \tELSE ROUND(IFNULL(a.count,0)/b.count*100,2) \tEND zb ,  IFNULL(a.count,0) as  count1,   IFNULL(b.count,0)   as   count2," + aw.getQuestionNum() + " as questionNum    from    (   select  count(1) as count  from    (select  exampaperNum,questionNum from  remark  where   exampaperNum={ExampaperNum}   and  type='1'    AND   isException='p' and  questionNum={QuestionNum} and insertuser={InsertUser} ) r      left  join   (select   distinct  exampaperNum,questionNum  from  task  t    where    exampaperNum={ExampaperNum}  and  questionNum={QuestionNum} ) a     on  r.exampaperNum=a.exampaperNum   and   r.questionNum=a.questionNum    )a     left  join    ( select count(1) as count  from     (  select  exampaperNum,questionNum  from  remark  where    exampaperNum={ExampaperNum}  and  type='1'     and    isException!='F'  and  questionNum={QuestionNum} and insertuser={InsertUser} ) r      left  join   (   select   distinct  exampaperNum,questionNum    from  task    where   exampaperNum={ExampaperNum}  and  questionNum={QuestionNum}    ) a        on  r.exampaperNum=a.exampaperNum   and   r.questionNum=a.questionNum ) b    on 1=1 ";
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        return this.dao2._queryBeanList(sql, AwardPoint.class, args);
    }

    public List yichangchongdu(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("InsertUser", aw.getInsertUser());
        args.put("QuestionNum", aw.getQuestionNum());
        return this.dao2._queryBeanList("select  \tCASE  WHEN    b.count=0 or b.count is null  THEN 0 \tELSE ROUND(IFNULL(a.count,0)/b.count*100,2) \tEND zb ,  IFNULL(a.count,0) as  count1,   IFNULL(b.count,0)   as   count2   from    (   select  count(1) as count  from   (select  exampaperNum,questionNum,id from  remark  where   exampaperNum={ExampaperNum}   and  type='2'  and  userNum={InsertUser} AND   isException='p'  and  questionNum={QuestionNum}) r    left  join   ( select   distinct  exampaperNum,questionNum  from  task  t    where    exampaperNum={ExampaperNum}  and  questionNum={QuestionNum}) a    on  r.exampaperNum=a.exampaperNum  and   r.questionNum=a.questionNum     )a    left  join   ( select count(1) as count  from     (  select   distinct    exampaperNum,questionNum,id  from  remark  where    exampaperNum={ExampaperNum}  and  type='2'  and  userNum={InsertUser} and    isException!='F'  and  questionNum={QuestionNum} ) r    left  join   (select   distinct  exampaperNum,questionNum    from  task   where   exampaperNum={ExampaperNum}  and  questionNum={QuestionNum}   ) a    on  r.exampaperNum=a.exampaperNum    and   r.questionNum=a.questionNum ) b    on 1=1 ", AwardPoint.class, args);
    }

    public List teachList(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("ScoreId", aw.getScoreId());
        return this.dao2._queryBeanList("  select    b.realname as  teacherName ,a.questionScore,a.insertUser,a.scoreId from  user  b   left  join     task  a    on   b.id=a.insertUser \t where   a.exampaperNum={ExampaperNum} \tand  a.questionNum={QuestionNum}  and     a.scoreId={ScoreId} ", AwardPoint.class, args);
    }

    public List taskuserList(AwardPoint aw, String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        return this.dao2._queryBeanList("select  insertUser  from  task  where scoreId={scoreId} ", AwardPoint.class, args);
    }

    public List chooseQuestionNumList(String exampaperNum, String groupNum, String questionNum, String p_questionNum, String groupType, String isParent) {
        String sql;
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("p_questionNum", p_questionNum);
        args.put("groupNum", groupNum);
        args.put("questionNum", questionNum);
        if (groupType.equals("2")) {
            sql = "select   id,questionNum  FROM  DEFINE   WHERE  examPaperNum={exampaperNum}  AND choosename={p_questionNum} and id!={groupNum} ";
        } else if (!"2".equals(groupType) && isParent.equals("1")) {
            String ordernum = this.dao2._queryStr(" select   ordernum  FROM  subDEFINE   WHERE  examPaperNum={exampaperNum}   and id={questionNum} ", args);
            args.put("ordernum", ordernum);
            sql = "select sd.id,sd.questionNum from  (select id,choosename from define where examPaperNum={exampaperNum} AND choosename={p_questionNum} )d left join subdefine sd  on d.id=sd.pid where sd.examPaperNum={exampaperNum} and sd.ordernum={ordernum} and sd.id <> {questionNum}   ORDER BY length(sd.questionNum),convert(sd.questionNum using gbk) ";
        } else {
            sql = "select   id,questionNum  FROM  DEFINE   WHERE  examPaperNum={exampaperNum}  AND choosename={p_questionNum} and id!={groupNum} ORDER BY length(questionNum),convert(questionNum using gbk)";
        }
        return this.dao2._queryBeanList(sql, AwardPoint.class, args);
    }

    public Integer firstrownumss(String tag, AwardPoint aw, String groupNum, int exampaperNum) {
        String sql;
        if (tag == null || tag.equals("")) {
            sql = "select  count(id)   from task    where    insertUser={InsertUser}   and  rownum={Rownum}    and groupNum={groupNum}  and  examPaperNum={exampaperNum} and status='F'  ";
        } else {
            sql = "select  count(id)   from test_task    where    insertUser={InsertUser}   and  rownum={Rownum}   and groupNum={groupNum}  and  examPaperNum={exampaperNum} and status='F'  ";
        }
        Map args = new HashMap();
        args.put("InsertUser", aw.getInsertUser());
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        args.put("groupNum", aw.getGroupNum());
        args.put("exampaperNum", Integer.valueOf(aw.getExampaperNum()));
        return this.dao2._queryInt(sql, args);
    }

    public Integer rownumrecord(int exampaperNum, String insertNum, String groupNum, int rownum, String starttime, String endtime) {
        String timesql = "  ";
        if (null != starttime && null != endtime && !"".equals(starttime) && !"".equals(endtime)) {
            timesql = timesql + "  and updateTime>{starttime} and updateTime<{endtime}  ";
        }
        String sql = " select IFNULL(count(1) ,0)  from   task  where   exampaperNum='" + exampaperNum + "' and   groupNum={groupNum} and rownum<={rownum}  AND insertUser={insertNum} " + timesql;
        Map args = new HashMap();
        args.put("starttime", starttime);
        args.put("endtime", endtime);
        args.put("groupNum", groupNum);
        args.put("rownum", Integer.valueOf(rownum));
        args.put("insertNum", insertNum);
        return this.dao2._queryInt(sql, args);
    }

    public List delTarecord(int exampaperNum, String insertNum, String groupNum, int rownumrecord, String starttime, String endtime) {
        StringBuffer buffer = new StringBuffer();
        String timesql = "  ";
        if (null != starttime && null != endtime && !"".equals(starttime) && !"".equals(endtime)) {
            timesql = timesql + "  and updateTime>{starttime}  and updateTime<{endtime} ";
        }
        try {
            buffer = buffer.append(" SELECT questionNum,`status`,id,updateTime,scoreId,rownum,questionScore,exampaperNum,isException,insertUser,groupNum    FROM test_task    WHERE   exampaperNum={exampaperNum}     AND     groupNum={groupNum}   AND   insertUser={insertNum} " + timesql + "   order  by  rownum  asc  limit {rownumrecord} ,1 ");
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        Map args = new HashMap();
        args.put("starttime", starttime);
        args.put("endtime", endtime);
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        args.put("insertNum", insertNum);
        args.put("rownumrecord", Integer.valueOf(rownumrecord));
        return this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
    }

    public Integer completeList(AwardPoint awardPoint) {
        String sql;
        if (awardPoint.getTag() == null || awardPoint.getTag().equals("")) {
            sql = "select  IFNULL(count(id),0) as count    from  task  where exampaperNum={ExampaperNum}  and  groupNum={GroupNum} and status='F'";
        } else {
            sql = "select  IFNULL(count(id),0) as count    from  test_task  where exampaperNum={ExampaperNum}  and  groupNum={GroupNum} and status='F'";
        }
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(awardPoint.getExampaperNum()));
        args.put("GroupNum", awardPoint.getGroupNum());
        return this.dao2._queryInt(sql, args);
    }

    public List notaskList(AwardPoint awardPoint) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(awardPoint.getExampaperNum()));
        args.put("QuestionNum", awardPoint.getQuestionNum());
        return this.dao2._queryBeanList("  SELECT  s.id      FROM   (SELECT id  FROM score    WHERE exampaperNum={ExampaperNum}  and   questionNum={QuestionNum} AND ismodify='T') s  LEFT JOIN  (SELECT scoreId,id  FROM task WHERE exampaperNum={ExampaperNum}\tand   questionNum={QuestionNum} )  t ON s.id = t.scoreId  \tWHERE t.id IS NULL ", AwardPoint.class, args);
    }

    public List notaskdoubleList(AwardPoint awardPoint) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(awardPoint.getExampaperNum()));
        args.put("QuestionNum", awardPoint.getQuestionNum());
        return this.dao2._queryBeanList("  SELECT  s.id     FROM   (SELECT id  FROM score    WHERE exampaperNum={ExampaperNum} and   questionNum={QuestionNum} AND ismodify='T') s  LEFT JOIN  (SELECT scoreId,id  FROM task WHERE exampaperNum={ExampaperNum}\tand   questionNum={QuestionNum} )  t ON s.id = t.scoreId  \tWHERE t.id IS NULL ", AwardPoint.class, args);
    }

    public Integer updateTscore(AwardPoint awardPoint) {
        try {
            Map args = new HashMap();
            args.put("ScoreId", awardPoint.getScoreId());
            return Integer.valueOf(this.dao2._execute("update  score  set  ismodify='F'  WHERE   id={ScoreId} ", args));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer onlineuser(AwardPoint awardPoint) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(awardPoint.getExampaperNum()));
        args.put("GroupNum", awardPoint.getGroupNum());
        return this.dao2._queryInt("SELECT IFNULL(COUNT(1),0) as u_num   FROM  (SELECT groupNum,userNum FROM questiongroup_user WHERE   exampaperNum={ExampaperNum} AND groupNum={GroupNum} ) u     LEFT JOIN onlineuser o ON u.userNum=o.userNum    WHERE o.id IS NOT NULL ", args);
    }

    public List questionNumList(AwardPoint awardPoint) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(awardPoint.getExampaperNum()));
        args.put("GroupNum", awardPoint.getGroupNum());
        return this.dao2._queryBeanList("SELECT questionNum FROM questiongroup_question WHERE  exampaperNum={ExampaperNum}  AND groupNum={GroupNum} ", AwardPoint.class, args);
    }

    public Integer completecount(AwardPoint aw) {
        String sql;
        if (aw.getTag() == null || aw.getTag().equals("")) {
            sql = " SELECT  IFNULL(COUNT(1),0) as  count1  FROM task   WHERE exampaperNum={ExampaperNum}  AND groupNum={GroupNum} ";
        } else {
            sql = " SELECT  IFNULL(COUNT(1),0) as  count1   FROM test_task    WHERE exampaperNum={ExampaperNum}  AND groupNum={GroupNum} ";
        }
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryInt(sql, args);
    }

    public Integer complettaskecount(AwardPoint aw) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("GroupNum", aw.getGroupNum());
        return this.dao2._queryInt(" SELECT  IFNULL(COUNT(1),0) as  count1   FROM task    WHERE exampaperNum={ExampaperNum} AND groupNum={GroupNum} and status='F'", args);
    }

    public Integer deletenocomplTask(AwardPoint awardPoint, String huishouteype) {
        Integer fh = 0;
        Map args = new HashMap();
        args.put("GroupNum", awardPoint.getGroupNum());
        args.put("InsertUser", awardPoint.getInsertUser());
        if ("huishou".equals(huishouteype)) {
            List queryidlist = this.dao2._queryColList("select id from task  where groupNum={GroupNum}  and insertUser={InsertUser} and status='F' and isException != 'C' ", args);
            for (int i = 0; i < queryidlist.size(); i++) {
                int a = i;
                args.put("id", queryidlist.get(a));
                fh = Integer.valueOf(this.dao2._execute(" update task set insertUser='-1',status='F',isException='F',porder=0,fenfaDate=0  where id={id} ", args));
            }
            this.log.error("退出题组回收task=====" + awardPoint.getInsertUser());
        }
        return fh;
    }

    public void insertTask(AwardPoint awardPoint, String insertUser) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(awardPoint.getExampaperNum()));
        args.put("GroupNum", awardPoint.getGroupNum());
        args.put("QuestionNum", awardPoint.getQuestionNum());
        int maxgroupRownum = this.dao2._queryInt("SELECT IFNULL(MAX(rownum),0) as maxRownum FROM task WHERE exampaperNum={ExampaperNum} AND groupNum={GroupNum} ", args).intValue();
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT  r.id  as scoreId   FROM   (   SELECT  s.id  \t  FROM \t(SELECT id ,isAppend,insertDate  FROM score   WHERE examPaperNum={ExampaperNum}  AND  questionNum={QuestionNum}  and  continued='F'  and isAppend='T'  ) s \t  LEFT JOIN (SELECT scoreId,id  FROM task WHERE examPaperNum={ExampaperNum} AND  questionNum={QuestionNum} )  t    ON s.id = t.scoreId  \tWHERE t.id IS NULL   AND s.isAppend!='T' ORDER BY s.insertDate )r ", AwardPoint.class, args);
        List<Object> list2 = new ArrayList<>();
        for (int il = 0; il < _queryBeanList.size(); il++) {
            AwardPoint aw = new AwardPoint();
            maxgroupRownum++;
            long id = GUID.getGUID();
            String data = DateUtil.getCurrentTime();
            String scoreId = ((AwardPoint) _queryBeanList.get(il)).getScoreId();
            aw.setId(Long.toString(id));
            aw.setScoreId(scoreId);
            aw.setExampaperNum(awardPoint.getExampaperNum());
            aw.setGroupNum(awardPoint.getGroupNum());
            aw.setQuestionNum(awardPoint.getQuestionNum());
            aw.setQuestionScore("0");
            aw.setUserNum("1");
            aw.setIsException("F");
            aw.setRownum(maxgroupRownum);
            aw.setIsDelete("F");
            aw.setStatus("F");
            aw.setInsertUser(insertUser);
            aw.setInsertDate(data);
            list2.add(aw);
        }
        if (_queryBeanList.size() > 0) {
            batchSaveTask(list2);
        }
    }

    public void doublefentask(int exampaperNum, String insertUser, String groupNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("insertUser", insertUser);
        new ArrayList();
        int q_count = this.dao2._queryBeanList("SELECT questionNum FROM questiongroup_question WHERE  groupNum={groupNum}  AND   exampaperNum={exampaperNum} ", AwardPoint.class, args).size();
        int maxgroupRownum = this.dao2._queryInt("SELECT IFNULL(MAX(rownum),0) as maxRownum FROM task WHERE exampaperNum={exampaperNum} AND insertUser={insertUser} AND groupNum={groupNum}", args).intValue();
        for (int k = 0; k < q_count; k++) {
            args.put("k", Integer.valueOf(k));
            String questionNum = this.dao2._queryStr("SELECT questionNum as questionNum FROM questiongroup_question WHERE  groupNum={groupNum} AND   exampaperNum={exampaperNum} limit {k} ,1", args);
            args.put("questionNum", questionNum);
            int maxRownum2 = maxgroupRownum;
            try {
                String sqlfenfa2 = "SELECT r.id as scoreId ,r.examPaperNum,'" + questionNum + "'as questionNum,'" + groupNum + "' as  groupNum   FROM  (  SELECT r.*   FROM   ( \tSELECT  s.id ,s.examPaperNum,    IFNULL(t.insertUser,'') insertUser,s.insertDate,s.isAppend \t FROM \t(SELECT id,examPaperNum,questionNum,insertDate,isAppend FROM score     WHERE  exampaperNum={exampaperNum} AND questionNum={questionNum} and  continued='F' ) s \t LEFT JOIN (SELECT id,insertUser,scoreId FROM task WHERE exampaperNum={exampaperNum}  AND questionNum={questionNum} ) t ON s.id = t.scoreId   GROUP BY s.id HAVING COUNT(s.id)<2   )r   WHERE r.insertUser!={insertUser}   AND r.isAppend!='T'   ORDER BY r.insertDate   )r ";
                List<?> _queryBeanList = this.dao2._queryBeanList(sqlfenfa2, AwardPoint.class, args);
                List<Object> list2 = new ArrayList<>();
                for (int il = 0; il < _queryBeanList.size(); il++) {
                    AwardPoint task = new AwardPoint();
                    maxRownum2++;
                    long id = GUID.getGUID();
                    String data = DateUtil.getCurrentTime();
                    String scoreId = ((AwardPoint) _queryBeanList.get(il)).getScoreId();
                    task.setId(Long.toString(id));
                    task.setScoreId(scoreId);
                    task.setExampaperNum(exampaperNum);
                    task.setGroupNum(groupNum);
                    task.setQuestionNum(questionNum);
                    task.setQuestionScore("0");
                    task.setUserNum("2");
                    task.setIsException("F");
                    task.setRownum(maxRownum2);
                    task.setIsDelete("F");
                    task.setStatus("F");
                    task.setInsertUser(insertUser);
                    task.setInsertDate(data);
                    list2.add(task);
                }
                if (_queryBeanList.size() > 0) {
                    batchSaveTask(list2);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public int[] batchSaveTask(List<Object> list) {
        return this.dao2.batchSave("task", (List<?>) list);
    }

    public List doubleList(AwardPoint awardPoint) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(awardPoint.getExampaperNum()));
        args.put("QuestionNum", awardPoint.getQuestionNum());
        return this.dao2._queryBeanList(" SELECT  s.id as scoreId    FROM   (  SELECT id    FROM score   WHERE examPaperNum={ExampaperNum} AND  questionNum={QuestionNum}  ) s   LEFT JOIN (SELECT  id,scoreId    FROM task WHERE examPaperNum={ExampaperNum} AND  questionNum={QuestionNum} ) t ON s.id = t.scoreId   GROUP BY s.id  HAVING COUNT(s.id)<2", AwardPoint.class, args);
    }

    public List<AwardPoint> shitag(int exam, int grade, int subject, AwardPoint aw) {
        Map args = new HashMap();
        args.put("exam", Integer.valueOf(exam));
        args.put("grade", Integer.valueOf(grade));
        args.put("subject", Integer.valueOf(subject));
        args.put("TestOrFormally", "1");
        return this.dao2._queryBeanList("select a.type,a.value    from  exampaperparameter  a  where  a.examNum={exam}  and  a.gradeNum={grade}  and  a.subjectNum={subject}  and  a.type={TestOrFormally} ", AwardPoint.class, args);
    }

    public List<AwardPoint> totalNumList(int exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        return this.dao2._queryBeanList(" select  groupNum,totalNum,exampaperNum  from  questiongroup    where  exampaperNum={exampaperNum}  and  totalNum     is  null", AwardPoint.class, args);
    }

    public void updategroupNum(String groupNum, AwardPoint awardPoint) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(awardPoint.getExampaperNum()));
        args.put("groupNum", groupNum);
        this.dao2._execute("UPDATE questiongroup q  inner JOIN   ( \t\t\tSELECT exampaperNum,groupNum,COUNT(id) num \t\t\tFROM  task   where   exampaperNum={ExampaperNum}  and    groupNum={groupNum}    ) r ON r.exampaperNum=q.exampaperNum AND r.groupNum=q.groupNum   \tSET q.totalnum=r.num", args);
    }

    public void updategroupNumexecute(AwardPoint awardPoint, String tagquestionNum) {
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(awardPoint.getExampaperNum()));
        args.put("Id", awardPoint.getId());
        args.put("groupNum", awardPoint.getGroupNum());
        this.dao2._queryStr("select distinct groupNum from  task  where   exampaperNum={ExampaperNum} and id={Id} ", args);
        String rownum = this.dao2._queryStr("select  if(count(rownum)=0,0, max(rownum))  from  task  where   exampaperNum={ExampaperNum}  and  groupNum={groupNum} ", args);
        this.dao2._queryStr("select distinct userNum from  task  where   exampaperNum={ExampaperNum} and  groupNum={groupNum} ", args);
        int aa = Integer.parseInt(rownum) + 1;
        args.put("aa", Integer.valueOf(aa));
        this.dao2._execute(" update  task  set  groupNum={groupNum} , rownum={aa} ,insertuser='-1',isException='F',status='F'  where id={Id} ", args);
    }

    public void insertquestionstepscore(AwardPoint awardPoint, String width, String height, String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("InsertUser", awardPoint.getInsertUser());
        args.put("ExampaperNum", Integer.valueOf(awardPoint.getExampaperNum()));
        args.put("QuestionNum", awardPoint.getQuestionNum());
        args.put(Const.EXPORTREPORT_step, Integer.valueOf(awardPoint.getStep()));
        args.put("QuestionScore", awardPoint.getQuestionScore());
        int step = this.dao2._queryInt(" select IFNULL(MAX(step),0) as  step  from  questionstepscore where  scoreId={scoreId} and  insertuser={InsertUser} ", args).intValue();
        int i = step + 1;
        String xxx = String.format("%.4f", Double.valueOf(Double.parseDouble(awardPoint.getAxis_x()) / Double.parseDouble(width)));
        String yyy = String.format("%.4f", Double.valueOf(Double.parseDouble(awardPoint.getAxis_y()) / Double.parseDouble(height)));
        float xx = Float.parseFloat(xxx);
        float yy = Float.parseFloat(yyy);
        args.put("xx", Float.valueOf(xx));
        args.put("yy", Float.valueOf(yy));
        this.dao2._execute("insert  into  questionstepscore ( scoreId,examPaperNum,questionNum ,step,questionScore, insertUser,insertDate,axis_x,axis_y  )    values({scoreId} ,{ExampaperNum} ,{QuestionNum},{step},{QuestionScore},{InsertUser}, NOW(),{xx},{yy} )", args);
    }

    public List insertquestionstepscoreList(AwardPoint awardPoint, String id) {
        Map args = new HashMap();
        args.put("id", id);
        args.put("InsertUser", awardPoint.getInsertUser());
        return this.dao2._queryBeanList(" select id,step, scoreId, questionScore,axis_x,axis_y from  questionstepscore  where  scoreId={id}  and  insertuser={InsertUser}  order  by  step  asc  ", AwardPoint.class, args);
    }

    public void updatequestionstepscore(String id, String score) {
        Map args = new HashMap();
        args.put("score", score);
        args.put("id", id);
        this.dao2._execute("update questionstepscore set questionScore={score} where id={id} ", args);
    }

    public void deletequestionstepscore(String id, String step, String insertUser) {
        Map args = new HashMap();
        args.put("id", id);
        this.dao2._execute("delete  from  questionstepscore  where id={id} ", args);
    }

    public void updateupquestionstepscore(String scoreId, String step, String userNum) {
        new ArrayList();
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put(Const.EXPORTREPORT_step, step);
        args.put("userNum", userNum);
        List<Object> ids = this.dao2._queryColList("select  id  from  questionstepscore  where scoreId={scoreId} and  step>={step} ", args);
        String time = DateUtil.getCurrentTime();
        args.put("time", time);
        for (Object obj : ids) {
            args.put("obj", obj);
            this.dao2._execute("update questionstepscore set step=step-1,updateUser={userNum} ,updateTime={time}   where id = {obj} ", args);
        }
    }

    public List checkstep(String scoreId) {
        new ArrayList();
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        List<Object> ids = this.dao2._queryColList("select  COUNT(1)  from  questionstepscore  where scoreId={scoreId} ", args);
        return ids;
    }

    public List quertyaxis_xList(String step, String insertUser, String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("insertUser", insertUser);
        int prestep = this.dao2._queryInt(" select IFNULL(MAX(step),0) as  step  from  questionstepscore where  scoreId={scoreId}  and  insertuser={insertUser} ", args).intValue();
        args.put("prestep", Integer.valueOf(prestep));
        return this.dao2._queryBeanList(" select id,step, scoreId, questionScore,axis_x,axis_y from  questionstepscore  where  scoreId={scoreId} and  insertuser={insertUser}  and step={prestep}", AwardPoint.class, args);
    }

    public List fullscoreList(int exampaperNum, String groupNum) {
        List list = new ArrayList();
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        List list1 = this.dao2._queryBeanList("select    fullScore,a.id as questionNum,a.questionnum as ext1,a.cross_page   from   (  select  groupNum,exampaperNum,questionNum  from   questiongroup_question   where   groupNum={groupNum}  and    exampaperNum={exampaperNum}    ) b   left  join ( (select id,questionnum,cross_page,fullScore,orderNum from define where examPaperNum={exampaperNum}) union all (select id,questionnum,cross_page,fullScore,orderNum  from subdefine WHERE exampaperNum={exampaperNum} )) a    on       b.questionNum=a.id   order  by  a.orderNum ", AwardPoint.class, args);
        List list2 = this.dao2._queryBeanList("  select   makType  from   questiongroup_mark_setting  where  exampaperNum={exampaperNum}  and    groupNum={groupNum} ", AwardPoint.class, args);
        list.add(list1);
        list.add(list2);
        list.add(getxztype(exampaperNum, groupNum));
        return list;
    }

    public List getxztype(int exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        List list = new ArrayList();
        this.dao2._queryObject("select groupType from questiongroup where  exampapernum={exampaperNum} and groupNum={groupNum} ", args);
        List questionlist = this.dao2._queryColList("select questionNum from questiongroup_question  where exampapernum={exampaperNum} and groupNum={groupNum} ", args);
        for (int i = 0; i < questionlist.size(); i++) {
            String questionNum = String.valueOf(questionlist.get(i));
            args.put("questionNum", questionNum);
            AwardPoint aw = (AwardPoint) this.dao2._queryBean("select choosename p_questionNum,id,isParent from define where exampapernum={exampaperNum} and id={questionNum} ", AwardPoint.class, args);
            if (null != aw) {
                list.add(aw);
            } else {
                list.add((AwardPoint) this.dao2._queryBean("select d.* from (select pid from subdefine where exampapernum={exampaperNum} and id={questionNum} )sd left join (select id,choosename p_questionNum,isParent from define where exampapernum={exampaperNum} )d   on d.id=sd.pid", AwardPoint.class, args));
            }
        }
        return list;
    }

    public String maxrownum(int exampaperNum, String insertUser, String groupNum) {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        return this.dao2._queryStr(" SELECT IFNULL(MAX(rownum),0) as maxRownum FROM task WHERE exampaperNum={exampaperNum}  AND groupNum={groupNum} AND  insertUser={insertUser} ", args);
    }

    public int markType(int exampaperNum, String questionNum) {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("questionNum", questionNum);
        return this.dao2._queryInt(" select a.makType  from  questiongroup_mark_setting a  left  join  questiongroup_question b  on  a.groupNum=b.groupNum   where   b.exampaperNum={exampaperNum}  and  b.questionNum={questionNum} ", args).intValue();
    }

    public List insertworkrecordList(int exampaperNum, String groupNum, String insertUser) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        return this.dao2._queryBeanList("select  IFNULL(count(1),0)num,totalscore from   workrecord   where     groupNum={groupNum} and   insertUser={insertUser}  limit  1 ", Workrecord.class, args);
    }

    public int updateworkrecord(int exampaperNum, String groupNum, String insertUser, String insertDate, String totalscore, String chayi_id, String sequence) {
        String sql;
        try {
            String numStr = "";
            if ("0".equals(sequence)) {
                numStr = numStr + " num=num+1, ";
            }
            if ("shiping".equals(totalscore)) {
                if ("T".equals(chayi_id)) {
                    sql = "update   workrecord  set   insertDate={insertDate} where  groupNum={groupNum} and   insertUser={insertUser} ";
                } else {
                    sql = "update   workrecord  set    num=num+1,insertDate={insertDate}  where  groupNum={groupNum}  and   insertUser={insertUser} ";
                }
            } else if ("T".equals(chayi_id)) {
                sql = "update   workrecord  set    totalscore={totalscore} ,insertDate={insertDate} where  groupNum={groupNum}  and   insertUser={insertUser} ";
            } else {
                sql = "update   workrecord  set   " + numStr + " totalscore={totalscore} ,insertDate={insertDate} where  groupNum={groupNum}  and   insertUser={insertUser}  ";
            }
            Map args = new HashMap();
            args.put("insertDate", insertDate);
            args.put("groupNum", groupNum);
            args.put("insertUser", insertUser);
            args.put("totalscore", totalscore);
            int i = this.dao2._execute(sql, args);
            return i;
        } catch (Exception e) {
            this.log.error("报错taskWork记录工作量", e);
            throw e;
        }
    }

    public int updateworkrecord1(int exampaperNum, String groupNum, String insertUser, int count, String insertDate, String totalscore) {
        Map args = new HashMap();
        args.put("count", Integer.valueOf(count));
        args.put("totalscore", totalscore);
        args.put("insertDate", insertDate);
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        return this.dao2._execute("update     workrecord  set    num={count},totalscore={totalscore} ,insertDate={insertDate} where  groupNum={groupNum}  and   insertUser={insertUser} ", args);
    }

    public int insertworkrecord(int exampaperNum, String groupNum, String insertUser, String insertDate, String totalscore) {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        args.put("insertDate", insertDate);
        args.put("totalscore", totalscore);
        return this.dao2._execute("insert into   workrecord (exampaperNum,groupNum,insertUser,insertDate,totalscore) values({exampaperNum},{groupNum},{insertUser},{insertDate},{totalscore} )  ", args);
    }

    public List totalNumsession(int exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        args.put("groupNum", groupNum);
        return this.dao2._queryBeanList("select      totalNum   from  questiongroup  where   exampaperNum={exampaperNum}  and  groupNum={groupNum}   ", QuestionGroupTemp.class, args);
    }

    public List<QuestionGroupTemp> usercompleteList(String groupNum, int exampaperNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        return this.dao2._queryBeanList(" select    IFNULL(sum(num) ,0) as num  from   workrecord  where      groupNum={groupNum} ", QuestionGroupTemp.class, args);
    }

    public List groupNumList(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        return this.dao2._queryBeanList(" select  groupNum from  questiongroup  WHERE exampaperNum={exampaperNum}  AND   groupNum  IS  NOT  NULL", QuestionGroupTemp.class, args);
    }

    public void updategroupNum(String groupNum, int exampaperNum, int totalnum) {
        Map args = new HashMap();
        args.put("totalnum", Integer.valueOf(totalnum));
        args.put("groupNum", groupNum);
        this.dao2._execute("update  questiongroup   set   totalnum={totalnum}   where  groupNum={groupNum} ", args);
    }

    public Integer groupNummarkType(String groupNum, int exampaperNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        return this.dao2._queryInt("select  makType  from  questiongroup_mark_setting  where  groupNum={groupNum} ", args);
    }

    public Integer onlineuser(String insertUser) {
        Map args = new HashMap();
        args.put("insertUser", insertUser);
        return this.dao2._queryInt("select   IFNULL(count(1),0)  FROM  onlineuser  where userNum={insertUser}", args);
    }

    public List<AwardPoint> score(String groupNum, String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("groupNum", groupNum);
        String studentId = this.dao2._queryStr("select  studentId  from  score  where  id={scoreId} ", args);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        return this.dao2._queryBeanList("SELECT DISTINCT s.regId,s.schoolNum,s.page,s.examinationRoomNum,s.studentId FROM score s LEFT JOIN questiongroup_question qgq ON s.questionNum = qgq.questionNum WHERE qgq.groupNum = {groupNum} AND s.studentId = {studentId} ", AwardPoint.class, args);
    }

    public String scanType(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        return this.dao2._queryStr("select   scanType  from  exampaper   where  examPaperNum={exampaperNum} ", args);
    }

    public Integer shiping(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        return this.dao2._queryInt("select   IFNULL(count(1),0)  FROM  testquestion_define  where exampaperNum={exampaperNum}  and status='T'", args);
    }

    public String querygroupNum(String exampaperNum, String questionNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        return this.dao2._queryStr("select a.groupNum  from  (  select      groupNum ,exampaperNum  from  questiongroup     where  exampaperNum={exampaperNum} ) a   left  join    ( select  exampaperNum,groupNum  from  questiongroup_question   where  exampaperNum={exampaperNum} and  questionNum={questionNum}) b   on  a.groupNum=b.groupNum  where   b.groupNum  is  not  null ", args);
    }

    public void updatefullscore(String exampaperNum, String groupNum, String questionNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        args.put("groupNum", groupNum);
        this.dao2._execute("delete  from  remark  where  exampaperNum={exampaperNum} and  questionNum={questionNum} ", args);
        if (null != this.dao2._queryObject("select id from task where exampaperNum={exampaperNum}  and  groupNum={groupNum} and userNum='3' limit 1", args)) {
            this.dao2._execute("delete from task where exampaperNum={exampaperNum}  and  groupNum={groupNum} and userNum='3' ", args);
        }
        this.dao2._execute("update  task  set  status='F',INSERTUSER='-1',questionscore='0'  ,rownum='-1'   where  exampaperNum={exampaperNum}  and  groupNum={groupNum} and status='T'  ", args);
    }

    public List questionscoreList(String exampaperNum, String groupNum, String questionNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        args.put("groupNum", groupNum);
        return this.dao2._queryBeanList("   select   distinct  a.scoreId   from     ( select   scoreId, groupNum from   task  where     exampaperNum={exampaperNum} and  questionNum={questionNum}  and  groupNum={groupNum} and status='T' ) a      left  join  ( select  groupNum ,exampaperNum  from  questiongroup_mark_setting  where     exampaperNum={exampaperNum}  and  groupNum={groupNum}  and  makType='1' )  b     on  a.groupNum=b.groupNum     where   b.groupNum is  not  null ", AwardPoint.class, args);
    }

    public void deleteremark(String scoreId, String exampaperNum, String questionNum) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        this.dao2._execute("delete  from  remark  where  scoreId={scoreId}  and  exampaperNum={exampaperNum}  and  questionNum={questionNum}", args);
    }

    public Integer groupcount(String groupNum, String exampaperNum, String insertUser) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        return this.dao2._queryInt("select  IFNULL(count(0),0)  from  questiongroup_user  where   exampaperNum={exampaperNum} and   groupNum={groupNum}  and  userNum={insertUser} ", args);
    }

    public String querytaskwork(String examNum, String gradeNum, String subject) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("subject", subject);
        Object obj = this.dao2._queryObject("select count(1) from examinationnum ex where ex.examNum={examNum} and ex.gradeNum={gradeNum} and ex.subjectnum={subject} ", args);
        String objstr = String.valueOf(obj);
        return objstr;
    }

    public String groupworkcount(String exampaperNum, String groupNum, String insertUser) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        Object obj = this.dao2._queryObject("select IFNULL(SUM(num),0) from workrecord where exampaperNum={exampaperNum} and groupNum={groupNum} and  insertUser={insertUser} ", args);
        String objstr = String.valueOf(obj);
        return objstr;
    }

    public List groupscorecount(String exampaperNum, String groupNum, String insertUser) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        List questionNumlist = this.dao2._queryColList("SELECT questionNum  from questiongroup_question where exampaperNum={exampaperNum} and groupNum={groupNum}", args);
        List list = new ArrayList();
        for (int i = 0; i < questionNumlist.size(); i++) {
            Object o = questionNumlist.get(i);
            String questionNumstr = String.valueOf(o);
            args.put("questionNumstr", questionNumstr);
            Task t = (Task) this.dao2._queryBean("select  IFNULL(ROUND(avg(questionscore),2),0)questionScoreavg,questionNum questionNum2 from task  where   groupNum={groupNum} and insertUser={insertUser} and questionNum={questionNumstr} and status='T'", Task.class, args);
            list.add(t);
        }
        return list;
    }

    public Map allgroupworkcount(String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        List questionNumlist = this.dao2._queryColList("SELECT questionNum  from questiongroup_question where exampaperNum={exampaperNum} and groupNum={groupNum} ", args);
        Map map = new HashMap();
        for (int i = 0; i < questionNumlist.size(); i++) {
            Object o = questionNumlist.get(i);
            String questionNumstr = String.valueOf(o);
            args.put("questionNumstr", questionNumstr);
            Map m = this.dao2._queryArrayMap("select insertUser,questionNum, IFNULL(ROUND(avg(questionscore),2),0) questionScoreavg,count(id) count from task  where   groupNum={groupNum} and questionNum={questionNumstr} and status='T' group by insertUser", "insertUser", args);
            map.put(o.toString(), m);
        }
        return map;
    }

    public Map groupavgcount(String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("exampaperNum", exampaperNum);
        Object questionTotalCount = this.dao2._queryObject("SELECT totalnum from questiongroup WHERE groupNum={groupNum} ", args);
        int count = questionTotalCount == null ? 0 : Integer.valueOf(questionTotalCount.toString()).intValue();
        List<Object> list = this.dao2._queryColList("select userNum from questiongroup_user WHERE exampaperNum={exampaperNum} and groupNum={groupNum} and userType='0'", args);
        int total = 1;
        Map map = this.dao2._queryOrderMap("SELECT insertUser,num from quota WHERE exampaperNum={exampaperNum} and groupNum={groupNum} ", TypeEnum.StringObject, args);
        if (map == null) {
            map = new HashMap();
        }
        if (list != null && list.size() > 0) {
            total = list.size();
            for (Object o : list) {
                if (!map.containsKey(o.toString())) {
                    map.put(String.valueOf(o), Integer.valueOf((int) Math.ceil(count / total)));
                }
            }
        }
        map.put("avg_" + groupNum, Integer.valueOf((int) Math.ceil(count / total)));
        return map;
    }

    public List getyipanscore(String exampaperNum, String groupNum, String questionNum, String starttime, String endtime, String startscore, String endscore, String inserUser, String startval, String indexval) throws SQLException {
        List list = new ArrayList();
        if (null != groupNum && null != exampaperNum && null != inserUser) {
            CallableStatement pstat = null;
            ResultSet rs = null;
            Connection conn = null;
            try {
                try {
                    conn = DbUtils.getConnection();
                    pstat = conn.prepareCall("{call /* shard_host_HG=Read */ getyipanscore(?,?,?,?,?,?,?,?,?,?)}");
                    pstat.setString(1, exampaperNum);
                    pstat.setString(2, groupNum);
                    pstat.setString(3, questionNum);
                    pstat.setString(4, inserUser);
                    pstat.setString(5, startscore);
                    pstat.setString(6, endscore);
                    pstat.setString(7, starttime);
                    pstat.setString(8, endtime);
                    pstat.setString(9, startval);
                    pstat.setString(10, indexval);
                    pstat.executeQuery();
                    rs = pstat.getResultSet();
                    ArrayList arrayList = new ArrayList();
                    while (rs.next()) {
                        QuestionGroup qg = new QuestionGroup();
                        qg.setGroupNum(rs.getString(1));
                        qg.setGroupName(rs.getString(2));
                        arrayList.add(qg);
                    }
                    list.add(arrayList);
                    while (pstat.getMoreResults()) {
                        ArrayList arrayList2 = new ArrayList();
                        rs = pstat.getResultSet();
                        rs.getMetaData().getColumnCount();
                        while (rs.next()) {
                            new Task();
                            Object[] obj = new Object[14 + arrayList.size() + arrayList.size()];
                            for (int i = 0; i < obj.length; i++) {
                                obj[i] = String.valueOf(rs.getObject(i + 1));
                            }
                            arrayList2.add(obj);
                        }
                        list.add(arrayList2);
                    }
                    DbUtils.close(rs, pstat, conn);
                    return list;
                } catch (Exception e) {
                    e.printStackTrace();
                    DbUtils.close(rs, pstat, conn);
                }
            } catch (Throwable th) {
                DbUtils.close(rs, pstat, conn);
                throw th;
            }
        }
        return list;
    }

    public List finishTasklist(String exampaperNum, String groupNum, String questionNum, String fieldType, String orderType, String startscore, String endscore, String insertUser, String startval, String indexval) throws SQLException {
        StringBuffer buffer = new StringBuffer();
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        args.put("insertUser", insertUser);
        args.put("groupNum", groupNum);
        try {
            buffer = buffer.append("select t.studentId,t.updateTime,  GROUP_CONCAT(t.id ORDER BY subd.ordernum) id,  GROUP_CONCAT(scoreid ORDER BY subd.ordernum) scoreId,  GROUP_CONCAT(questionNum ORDER BY subd.ordernum) questionNum, GROUP_CONCAT(questionScore ORDER BY subd.ordernum) questionScore from (  select b.* from task b  where b.exampaperNum={exampaperNum} and  b.groupNum={groupNum} AND b.insertUser={insertUser}  and b.status='T' )t  left join  (  (select  id,ordernum,questionNum groupname  from define where exampaperNum={exampaperNum} and questiontype=1)    UNION ALL    (select id,ordernum,questionNum groupname from subdefine  where exampaperNum={exampaperNum})  )subd  on subd.id=t.questionNum   GROUP BY t.studentId,userNum  order  by  t.updateTime asc,subd.ordernum asc ");
        } catch (Exception e) {
            this.log.error("ROWNUM报错： " + ((Object) buffer));
        }
        List<?> _queryBeanList = this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
        for (int k = 0; k < _queryBeanList.size(); k++) {
            AwardPoint awardPoint = (AwardPoint) _queryBeanList.get(k);
            awardPoint.setIndex(String.valueOf(k + 1));
            List tempList = new ArrayList();
            for (int p = 0; p < awardPoint.getScoreId().split(Const.STRING_SEPERATOR).length; p++) {
                AwardPoint ap = new AwardPoint();
                ap.setScoreId(awardPoint.getScoreId().split(Const.STRING_SEPERATOR)[p]);
                ap.setQuestionNum(awardPoint.getQuestionNum().split(Const.STRING_SEPERATOR)[p]);
                ap.setQuestionScore(awardPoint.getQuestionScore().split(Const.STRING_SEPERATOR)[p]);
                tempList.add(ap);
            }
            awardPoint.setaList(tempList);
        }
        return _queryBeanList;
    }

    public List getQuestionNumList(String groupNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        return this.dao2._queryBeanList(" select q.*,ifnull(d.questionNum,sd.questionNum) groupName from questiongroup_question q   left JOIN define d on q.questionNum=d.id  left JOIN subdefine sd on q.questionNum=sd.id  where q.groupNum={groupNum} ", QuestionGroup_question.class, args);
    }

    public void panfenjilu(String scoreId, String questionNum, String userNum, String loginUserId) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        args.put("userNum", userNum);
        args.put("loginUserId", loginUserId);
        args.put("scoreId", scoreId);
        Integer maxfenfa = Integer.valueOf(String.valueOf(this.dao2.queryObject("select operate from config where para='maxpanfenff'")));
        Object maxrownum = this.dao2._queryObject("SELECT max(rownum) from task where questionNum={questionNum} and insertUser={userNum} ", args);
        args.put("maxrownum", Integer.valueOf(Integer.valueOf(String.valueOf(maxrownum)).intValue() + maxfenfa.intValue() + 2));
        this.dao2._execute("update task  a  set\ta.updateTime=now(), a.updateUser={loginUserId} ,    a.status='F',a.isException='C',a.rownum={maxrownum},porder=0,fenfaDate=0   where   a.scoreId={scoreId} and  a.insertUser={userNum}  ", args);
        this.dao2._execute("delete from task where scoreId={scoreId}  and  insertUser <> {userNum} and usernum='3'", args);
        this.dao2._execute("update workrecord wr  left join (select groupNum, questionscore from  task  where   scoreId={scoreId} and insertuser={userNum} )t  on wr.groupNum=t.groupNum     set wr.num=wr.num-1 ,wr.totalScore=wr.totalScore-t.questionscore  where wr.groupNum=t.groupNum and wr.insertuser={userNum} ", args);
    }

    public void updatexzwork(String scoreId, String merge, String oldquestionNum, String newquestionNum, String oldgroupNum, String newgroupNum) {
        String p_newGroupNum;
        String p_oldGroupNum;
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("newquestionNum", newquestionNum);
        if ("1".equals(merge)) {
            p_newGroupNum = newgroupNum;
            p_oldGroupNum = oldgroupNum;
        } else {
            p_newGroupNum = newquestionNum;
            p_oldGroupNum = oldquestionNum;
        }
        args.put("p_newGroupNum", p_newGroupNum);
        args.put("p_oldGroupNum", p_oldGroupNum);
        this.dao2._execute("delete from task where scoreid={scoreId} and userNum='3'", args);
        this.dao2._execute("update workrecord wr  left join (select groupNum, questionscore,insertuser from  task  where    scoreId={scoreId} )t  on wr.groupNum=t.groupNum and wr.insertuser=t.insertuser     set wr.num=wr.num-1 ,wr.totalScore=wr.totalScore-t.questionscore  where wr.groupNum=t.groupNum and wr.insertuser=t.insertuser ", args);
        this.dao2._execute("update   markerror   set  questionNum={newquestionNum}   where    scoreId={scoreId}", args);
        this.dao2._execute("update remark   set   questionNum={newquestionNum}   where  scoreid={scoreId}  and  type='1' ", args);
        this.dao2._execute("update remark   set  questionNum={newquestionNum}   where  scoreId={scoreId} and type='2' ", args);
        this.dao2._execute(" update  task   set  groupNum={p_newGroupNum} ,questionNum={newquestionNum},questionScore=0,insertUser='-1',rownum='-1',`status`='F',isException='F'  where  scoreid={scoreId} ", args);
        Object o4 = this.dao2._queryObject("select /* shard_host_HG=Write */ count(1) from task where groupNum={p_oldGroupNum} ", args);
        args.put("o4", o4);
        this.dao2._execute("update questiongroup set totalnum={o4} where groupNum={p_oldGroupNum}", args);
        Object oo = this.dao2._queryObject("select /* shard_host_HG=Write */ count(1) from task where groupNum={p_oldGroupNum}", args);
        args.put("oo", oo);
        this.dao2._execute("update questiongroup set totalnum={oo} where groupNum={p_oldGroupNum}", args);
        String tqcount = this.dao2._queryStr("select count(1) from testquestion where questionNum={p_oldGroupNum}", args);
        if (!tqcount.equals("0")) {
            this.dao2._execute("delete from testquestion where scoreId={scoreId} ", args);
            this.dao2._execute("delete from  test_task where  scoreId={scoreId} ", args);
        }
    }

    public void deletetaskuserNum3(String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        this.dao2._execute("delete from task where scoreid={scoreId} and userNum='3'", args);
    }

    public String queryjudgetype(String questionNum) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        return this.dao2._queryStr("SELECT qms.judgetype from  questiongroup_question qq left JOIN questiongroup_mark_setting qms  on qms.groupNum=qq.groupNum where qq.questionNum={questionNum}  ", args);
    }

    public void resettingWorkrecord(String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        if (null == groupNum) {
            List grouplist = this.dao2._queryColList("SELECT groupNum from questiongroup where exampaperNum={exampaperNum} ", args);
            for (int i = 0; i < grouplist.size(); i++) {
                args.put("groupNum", String.valueOf(grouplist.get(i)));
                String userNum = this.dao2._queryStr("select insertUser from workrecord where groupNum={groupNum} LIMIT 1 ", args);
                args.put("userNum", userNum);
                if (!"null".equals(userNum)) {
                    this.dao2._execute("update task set insertUser={userNum}  where groupNum={groupNum}  and insertUser='-1'  and status='T'", args);
                }
                List<?> _queryBeanList = this.dao2._queryBeanList(" select /* shard_host_HG=Write */ studentId ext1,userNum ext2 from (  select t.id,t.questionNum,LENGTH(group_concat(distinct t.`status` separator '')) le,t.studentId,t.userNum from task t  where t.groupNum={groupNum} GROUP BY t.studentId,t.userNum  )a  where a.le>1 ", AjaxData.class, args);
                for (int j = 0; j < _queryBeanList.size(); j++) {
                    String studentIdVal = ((AjaxData) _queryBeanList.get(j)).getExt1();
                    String userNumVal = ((AjaxData) _queryBeanList.get(j)).getExt2();
                    args.put("studentIdVal", studentIdVal);
                    args.put("userNumVal", userNumVal);
                    this.dao2._execute("update task t  set t.insertUser='-1' , t.status='F' , t.rowNum='-1',porder=0,fenfaDate=0,updateTime='',xuankaoqufen=1  where t.studentId={studentIdVal} and t.userNum={userNumVal} and t.groupNum={groupNum} ", args);
                }
                String makType = this.dao2._queryStr("select /* shard_host_HG=Write */ makType from questiongroup_mark_setting where exampaperNum={exampaperNum} and groupNum={groupNum}", args);
                if ("1".equals(makType)) {
                    this.dao2._execute("insert into task(id,scoreId,examPaperNum,groupNum,questionNum,questionScore,studentId,userNum,isException,rownum,isDelete,`status`,porder,insertUser,insertDate,fenfaDate,updateUser,updateTime,testingCentreId)  SELECT /* shard_host_HG=Write */ UUID_SHORT(),t.scoreId,t.examPaperNum,t.groupNum,t.questionNum,0,t.studentId,2,t.isException,t.rownum,t.isDelete,'F',2,'-1',now(),'0','-2',now(),t.testingCentreId  from(  SELECT * from task t where exampaperNum= {exampaperNum} and groupNum={groupNum} and userNum=1  )t left JOIN (  SELECT * from task t where exampaperNum= {exampaperNum} and groupNum={groupNum} and userNum=2  ) t1 on t.scoreId=t1.scoreId where  t1.id is null", args);
                }
                this.dao2._execute("update workrecord set num=0 where exampaperNum={exampaperNum} and groupNum={groupNum} ", args);
                this.dao2._execute("update workrecord w   right join  (select  cast(count(1)/count(DISTINCT(questionNum))as signed) tnum,groupNum,insertUser from task  where exampaperNum={exampaperNum} and groupNum={groupNum} and `status`='T'  GROUP BY insertUser)t on w.groupNum=t.groupNum and t.insertUser=w.insertUser  set w.num=t.tnum where  w.exampaperNum={exampaperNum} and w.groupNum={groupNum} ", args);
                this.dao2._execute("update questiongroup w  right join  (select count(1) tnum,groupNum from task  where exampaperNum={exampaperNum} and groupNum={groupNum}  )t on w.groupNum=t.groupNum  set w.totalnum=t.tnum where  w.exampaperNum={exampaperNum} and w.groupNum={groupNum} ", args);
            }
            return;
        }
        String userNum2 = this.dao2._queryStr("select insertUser from workrecord where groupNum={groupNum} LIMIT 1 ", args);
        args.put("userNum", userNum2);
        if (!"null".equals(userNum2)) {
            this.dao2._execute("update task set insertUser={userNum}  where groupNum={groupNum}  and insertUser='-1'  and status='T'", args);
        }
        List<?> _queryBeanList2 = this.dao2._queryBeanList(" select /* shard_host_HG=Write */ studentId ext1,userNum ext2 from (  select t.id,t.questionNum,LENGTH(group_concat(distinct t.`status` separator '')) le,t.studentId,t.userNum from task t  where t.groupNum={groupNum} GROUP BY t.studentId,t.userNum  )a  where a.le>1 ", AjaxData.class, args);
        for (int j2 = 0; j2 < _queryBeanList2.size(); j2++) {
            String studentIdVal2 = ((AjaxData) _queryBeanList2.get(j2)).getExt1();
            String userNumVal2 = ((AjaxData) _queryBeanList2.get(j2)).getExt2();
            args.put("studentIdVal", studentIdVal2);
            args.put("userNumVal", userNumVal2);
            this.dao2._execute("update task t  inner JOIN  score s on t.scoreId=s.id  set t.insertUser='-1' , t.status='F' , t.rowNum='-1',porder=0,fenfaDate=0,updateTime='',xuankaoqufen=1   where t.studentId={studentIdVal} and t.userNum={userNumVal} and t.groupNum={groupNum} ", args);
        }
        String makType2 = this.dao2._queryStr("select /* shard_host_HG=Write */ makType from questiongroup_mark_setting where exampaperNum={exampaperNum}  and groupNum={groupNum} ", args);
        if ("1".equals(makType2)) {
            this.dao2._execute("insert into task(id,scoreId,examPaperNum,groupNum,questionNum,questionScore,studentId,userNum,isException,rownum,isDelete,`status`,porder,insertUser,insertDate,fenfaDate,updateUser,updateTime,testingCentreId)  SELECT /* shard_host_HG=Write */ UUID_SHORT(),t.scoreId,t.examPaperNum,t.groupNum,t.questionNum,0,t.studentId,2,t.isException,t.rownum,t.isDelete,'F',2,'-1',now(),'0','-2',now(),t.testingCentreId  from(  SELECT * from task t where exampaperNum= {exampaperNum} and groupNum={groupNum} and userNum=1  )t left JOIN (  SELECT * from task t where exampaperNum= {exampaperNum} and groupNum={groupNum} and userNum=2  ) t1 on t.scoreId=t1.scoreId where  t1.id is null", args);
        }
    }

    public QuestionGroupInfo getYingPanGroupInfo(String exampaperNum, String groupNum) {
        String sql;
        String sql2;
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("exampaperNum", exampaperNum);
        args.put("task_insertUser_defalt", "-1");
        QuestionGroupInfo info = new QuestionGroupInfo();
        info.setExampaperNum(exampaperNum);
        info.setGroupNum(groupNum);
        String enforce = systemService.fenzu(exampaperNum);
        info.setSchoolGroup("1".equals(enforce));
        Object stepoo = this.dao2._queryObject("SELECT makType from questiongroup_mark_setting WHERE groupNum={groupNum} ", args);
        int step = (stepoo == null || !stepoo.toString().equals("1")) ? 1 : 2;
        info.setStep(step);
        Object questionCountoo = this.dao2._queryObject("select count(1) from questiongroup_question where groupNum={groupNum} ", args);
        int questionCount = questionCountoo == null ? 0 : Integer.valueOf(questionCountoo.toString()).intValue();
        info.setQuestionCount(questionCount);
        String xuankaoqufen = this.dao2._queryStr("SELECT IFNULL(e.xuankaoqufen,1) from(SELECT category from define where id={groupNum} union SELECT category from subdefine where id={groupNum}\t)d  INNER JOIN exampaper e on d.category=e.examPaperNum ", args);
        Map<String, Object> userMap = this.dao2._queryOrderMap("select qu.userNum,if(p.fenzuyuejuan=0,-1,IFNULL(sp.schoolGroupNum,-1)) schoolGroupNum from questiongroup_user qu left join exampaper p on qu.exampaperNum=p.examPaperNum LEFT JOIN `user` u ON u.id = qu.userNum LEFT JOIN schoolgroup sp on sp.schoolNum =u.schoolnum   WHERE qu.groupNum={groupNum} ", TypeEnum.StringObject, args);
        info.setUserSchoolGroupMap(userMap);
        if (userMap != null && userMap.size() > 0) {
            info.setTeacherCount(userMap.size());
        } else {
            info.setTeacherCount(1);
        }
        Map<String, Integer> schoolGroupTeacherCountMap = new HashMap<>();
        if (userMap != null && userMap.size() > 0) {
            Iterator<Map.Entry<String, Object>> it = userMap.entrySet().iterator();
            while (it.hasNext()) {
                String schoolGroupNum = it.next().getValue().toString();
                if (schoolGroupTeacherCountMap.containsKey(schoolGroupNum)) {
                    schoolGroupTeacherCountMap.put(schoolGroupNum, Integer.valueOf(schoolGroupTeacherCountMap.get(schoolGroupNum).intValue() + 1));
                } else {
                    schoolGroupTeacherCountMap.put(schoolGroupNum, 1);
                }
            }
        }
        if (!info.isSchoolGroup()) {
            if ("1".equals(xuankaoqufen)) {
                sql = "SELECT -1 schoolGroupNum ,count(1) from examinationnum n LEFT join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum WHERE p.examPaperNum={exampaperNum} ";
            } else if ("2".equals(xuankaoqufen)) {
                sql = "SELECT -1 schoolGroupNum,IFNULL(count(1),0) count  from examinationnum n LEFT join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and e1.examPaperNum=e2.pexampaperNum WHERE e1.examPaperNum={exampaperNum} ";
            } else {
                sql = "SELECT -1 schoolGroupNum,IFNULL(count(1),0) count from examinationnum n LEFT join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum LEFT JOIN (SELECT n.studentId,e2.pexamPaperNum from examinationnum n LEFT join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and e1.examPaperNum=e2.pexampaperNum WHERE e1.examPaperNum={exampaperNum})n1 on n1.pexamPaperNum=e1.examPaperNum and n.studentId=n1.studentId INNER JOIN exampaper e2 on e1.examPaperNum=e2.pexamPaperNum and e2.xuankaoqufen=3 WHERE e1.examPaperNum={exampaperNum} and n1.studentId is null ";
            }
        } else if ("1".equals(xuankaoqufen)) {
            sql = "SELECT IFNULL(sg.schoolGroupNum,-1) schoolGroupNum ,count(1) from examinationnum n LEFT join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum LEFT JOIN schoolgroup sg on sg.schoolNum=n.schoolNum WHERE p.examPaperNum={exampaperNum} group by sg.schoolGroupNum";
        } else if ("2".equals(xuankaoqufen)) {
            sql = "SELECT IFNULL(slg.schoolGroupNum,-1) schoolGroupNum,IFNULL(count(1),0) count  from examinationnum n LEFT join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and e1.examPaperNum=e2.pexampaperNum LEFT JOIN schoolgroup slg on n.schoolNum=slg.schoolNum WHERE e1.examPaperNum={exampaperNum} GROUP BY slg.schoolGroupNum";
        } else {
            sql = "SELECT IFNULL(slg.schoolGroupNum,-1) schoolGroupNum,IFNULL(count(1),0) count from examinationnum n LEFT join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum LEFT JOIN (SELECT n.studentId,e2.pexamPaperNum from examinationnum n LEFT join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and e1.examPaperNum=e2.pexampaperNum WHERE e1.examPaperNum={exampaperNum})n1 on n1.pexamPaperNum=e1.examPaperNum and n.studentId=n1.studentId INNER JOIN exampaper e2 on e1.examPaperNum=e2.pexamPaperNum and e2.xuankaoqufen=3 LEFT JOIN schoolgroup slg on n.schoolNum=slg.schoolNum WHERE e1.examPaperNum={exampaperNum} and n1.studentId is null GROUP BY slg.schoolGroupNum";
        }
        Map<String, Object> schoolGroupStuMap = this.dao2._queryOrderMap(sql, TypeEnum.StringObject, args);
        Map<String, Integer> schoolGroupTotalMap = new HashMap<>();
        if (schoolGroupStuMap != null && schoolGroupStuMap.size() > 0) {
            for (Map.Entry<String, Object> entry : schoolGroupStuMap.entrySet()) {
                schoolGroupTotalMap.put(Convert.toStr(entry.getKey()), Integer.valueOf(Convert.toInt(entry.getValue()).intValue() * info.getStep()));
            }
        }
        if (!info.isSchoolGroup()) {
            if ("1".equals(xuankaoqufen)) {
                sql2 = "SELECT -1 schoolGroupNum ,count(1) from task t LEFT JOIN testingcentredis td on  t.exampaperNum=td.examPaperNum and t.testingCentreId=td.testingCentreId WHERE t.exampaperNum={exampaperNum} and t.groupNum={groupNum} and t.status = 'F' and t.insertUser={task_insertUser_defalt}  and td.isDis=1 ";
            } else if ("2".equals(xuankaoqufen)) {
                sql2 = "SELECT -1 schoolGroupNum ,count(1) from task t INNER JOIN student st on t.studentId=st.id LEFT JOIN testingcentredis td on  t.exampaperNum=td.examPaperNum and t.testingCentreId=td.testingCentreId INNER JOIN subjectcombinedetail  sd on st.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum WHERE t.exampaperNum={exampaperNum} and t.groupNum={groupNum}  and t.status = 'F' and t.insertUser='-1' and td.isDis=1 ";
            } else {
                sql2 = "SELECT -1 schoolGroupNum ,count(1) from task t LEFT JOIN(SELECT t.id from task t INNER JOIN student st on t.studentId=st.id LEFT JOIN testingcentredis td on  t.exampaperNum=td.examPaperNum and t.testingCentreId=td.testingCentreId INNER JOIN subjectcombinedetail  sd on st.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum WHERE t.exampaperNum={exampaperNum}  and t.groupNum={groupNum}  and t.status = 'F' and t.insertUser='-1' and td.isDis=1)t1 on t.id=t1.id LEFT JOIN testingcentredis td on  t.exampaperNum=td.examPaperNum and t.testingCentreId=td.testingCentreId INNER JOIN student st on t.studentId=st.id WHERE t.exampaperNum={exampaperNum} and t.groupNum={groupNum}  and t.status = 'F' and t.insertUser='-1' and td.isDis=1 and t1.id is null ";
            }
        } else if ("1".equals(xuankaoqufen)) {
            sql2 = "SELECT IFNULL(sg.schoolGroupNum,-1) schoolGroupNum ,count(1) from task t left JOIN score s on t.scoreid = s.id left join schoolgroup sg on sg.schoolNum = s.schoolNum LEFT JOIN testingcentredis td on  t.exampaperNum=td.examPaperNum and t.testingCentreId=td.testingCentreId WHERE t.exampaperNum={exampaperNum} and t.groupNum={groupNum} and t.status = 'F' and t.insertUser={task_insertUser_defalt} and td.isDis=1 group by sg.schoolGroupNum";
        } else if ("2".equals(xuankaoqufen)) {
            sql2 = "SELECT IFNULL(sg.schoolGroupNum,-1) schoolGroupNum ,count(1) from task t INNER JOIN student st on t.studentId=st.id left join schoolgroup sg on sg.schoolNum = st.schoolNum LEFT JOIN testingcentredis td on  t.exampaperNum=td.examPaperNum and t.testingCentreId=td.testingCentreId INNER JOIN subjectcombinedetail  sd on st.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum WHERE t.exampaperNum={exampaperNum} and t.groupNum={groupNum}  and t.status = 'F' and t.insertUser='-1' and td.isDis=1 group by sg.schoolGroupNum";
        } else {
            sql2 = "SELECT IFNULL(sg.schoolGroupNum,-1) schoolGroupNum ,count(1) from task t LEFT JOIN(SELECT t.id from task t INNER JOIN student st on t.studentId=st.id LEFT JOIN testingcentredis td on  t.exampaperNum=td.examPaperNum and t.testingCentreId=td.testingCentreId INNER JOIN subjectcombinedetail  sd on st.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e on sd.subjectNum=e.subjectNum and t.examPaperNum=e.pexampaperNum WHERE t.exampaperNum={exampaperNum} and t.groupNum={groupNum}  and t.status = 'F' and t.insertUser='-1' and td.isDis=1)t1 on t.id=t1.id LEFT JOIN testingcentredis td on  t.exampaperNum=td.examPaperNum and t.testingCentreId=td.testingCentreId INNER JOIN student st on t.studentId=st.id LEFT join schoolgroup sg on sg.schoolNum = st.schoolNum WHERE t.exampaperNum={exampaperNum} and t.groupNum={groupNum}  and t.status = 'F' and t.insertUser='-1' and td.isDis=1 and t1.id is null group by sg.schoolGroupNum";
        }
        Map<String, Object> schoolGroupScanTotalMap = this.dao2._queryOrderMap(sql2, TypeEnum.StringObject, args);
        Map<String, Object> map = this.dao2._queryOrderMap("SELECT insertUser,num from quota WHERE exampaperNum={exampaperNum} and groupNum={groupNum} ", TypeEnum.StringObject, args);
        Map<String, Integer> schoolGroupspecifiedTeacherCountMap = new HashMap<>();
        Map<String, Integer> schoolGroupspecifiedTeacherTotalMap = new HashMap<>();
        for (Map.Entry<String, Object> entry2 : map.entrySet()) {
            String key = entry2.getKey();
            int count = ((Integer) entry2.getValue()).intValue();
            if (userMap.containsKey(key)) {
                String schoolGroupNum2 = userMap.get(key).toString();
                if (schoolGroupspecifiedTeacherTotalMap.containsKey(schoolGroupNum2)) {
                    schoolGroupspecifiedTeacherTotalMap.put(schoolGroupNum2, Integer.valueOf(count + schoolGroupspecifiedTeacherTotalMap.get(schoolGroupNum2).intValue()));
                } else {
                    schoolGroupspecifiedTeacherTotalMap.put(schoolGroupNum2, Integer.valueOf(count));
                }
                if (schoolGroupspecifiedTeacherCountMap.containsKey(schoolGroupNum2)) {
                    schoolGroupspecifiedTeacherCountMap.put(schoolGroupNum2, Integer.valueOf(schoolGroupspecifiedTeacherCountMap.get(schoolGroupNum2).intValue() + 1));
                } else {
                    schoolGroupspecifiedTeacherCountMap.put(schoolGroupNum2, 1);
                }
            }
        }
        Map<String, Integer> teacherYingPanTotallMap = new HashMap<>();
        Iterator<Map.Entry<String, Object>> it2 = userMap.entrySet().iterator();
        while (it2.hasNext()) {
            String insertUser = it2.next().getKey();
            String schoolGroupNum3 = userMap.get(insertUser).toString();
            int total = 0;
            if (schoolGroupTotalMap.containsKey(schoolGroupNum3)) {
                total = schoolGroupTotalMap.get(schoolGroupNum3).intValue();
            }
            int scanTotal = 0;
            if (schoolGroupScanTotalMap.containsKey(schoolGroupNum3)) {
                scanTotal = Integer.parseInt(schoolGroupScanTotalMap.get(schoolGroupNum3).toString());
            }
            if (total == 0 || scanTotal == 0) {
                teacherYingPanTotallMap.put(insertUser, 0);
            } else {
                int scanTotal2 = scanTotal / info.getQuestionCount();
                Integer specifiedTeacherCount = schoolGroupspecifiedTeacherCountMap.get(schoolGroupNum3);
                Integer specifiedTeacherCount2 = Integer.valueOf(specifiedTeacherCount == null ? 0 : specifiedTeacherCount.intValue());
                Integer specifiedTeacherTotal = schoolGroupspecifiedTeacherTotalMap.get(schoolGroupNum3);
                Integer specifiedTeacherTotal2 = Integer.valueOf(specifiedTeacherTotal == null ? 0 : specifiedTeacherTotal.intValue());
                if (map.containsKey(insertUser)) {
                    int actualTeacherCount = schoolGroupTeacherCountMap.get(schoolGroupNum3).intValue() - specifiedTeacherCount2.intValue();
                    if ((actualTeacherCount <= 0 ? 0 : actualTeacherCount) == 0) {
                        total = specifiedTeacherTotal2.intValue();
                    }
                    teacherYingPanTotallMap.put(insertUser, Integer.valueOf((int) Math.ceil((Double.parseDouble(map.get(insertUser).toString()) / total) * scanTotal2)));
                } else {
                    int actualTeacherCount2 = schoolGroupTeacherCountMap.get(schoolGroupNum3).intValue() - specifiedTeacherCount2.intValue();
                    int actualTeacherCount3 = actualTeacherCount2 <= 0 ? 1 : actualTeacherCount2;
                    int actualTeacherTotal = total - specifiedTeacherTotal2.intValue();
                    int yingPan = (int) Math.ceil(((actualTeacherTotal / actualTeacherCount3) / total) * scanTotal2);
                    if (yingPan <= 0) {
                        yingPan = 1;
                    }
                    teacherYingPanTotallMap.put(insertUser, Integer.valueOf(yingPan));
                }
            }
        }
        teacherYingPanTotallMap.put("-9", 1000);
        info.setYingPanWorkMap(teacherYingPanTotallMap);
        return info;
    }

    public void testingCentre(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        List<?> _queryBeanList = this.dao2._queryBeanList("select testingCentreId from examinationnum ec LEFT JOIN exampaper e on ec.examNum=e.examNum and ec.gradeNum=e.gradeNum and ec.subjectNum=e.subjectNum where e.examPaperNum={exampaperNum} GROUP BY ec.testingCentreId ", TestingcentreDis.class, args);
        for (int i = 0; i < _queryBeanList.size(); i++) {
            String isDis = Configuration.getInstance().getTestingcentredis();
            int a = i;
            args.put("TestingCentreId", ((TestingcentreDis) _queryBeanList.get(a)).getTestingCentreId());
            args.put("isDis", isDis);
            this.dao2._execute("insert into testingcentreDis (examPaperNum,testingCentreId,isDis,insertUser,insertDate) values({exampaperNum} ,{TestingCentreId},{isDis},'-2',now()) ON DUPLICATE KEY UPDATE updateUser='-2',updateDate=now() ", args);
        }
    }

    public QuestionGroupInfo getQuestionGroupInfo(String exampaperNum, String groupNum, ServletContext context) {
        String sql;
        String sql2;
        String sql3;
        String sql4;
        Map<String, Integer> reg_map;
        Map<String, Integer> stu_map;
        String str;
        String sql5;
        String sql6;
        String sql7;
        String sql8;
        String sql9;
        String sql10;
        String sql11;
        String sql12;
        String sql13;
        String sql14;
        Map<String, Map<String, Map<String, Integer>>> bigTableMapReg = (Map) context.getAttribute(Const.bigTableMapReg);
        Map<String, Map<String, Map<String, Integer>>> bigTableMapExamina = (Map) context.getAttribute(Const.bigTableMapExamina);
        if (CollUtil.isEmpty(bigTableMapReg) || CollUtil.isEmpty(bigTableMapExamina)) {
            return null;
        }
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("exampaperNum", exampaperNum);
        long aaaa = System.currentTimeMillis();
        QuestionGroupInfo info = new QuestionGroupInfo();
        info.setExampaperNum(exampaperNum);
        info.setGroupNum(groupNum);
        String enforce = systemService.fenzu(exampaperNum);
        info.setSchoolGroup("1".equals(enforce));
        String xuankaoqufen = this.dao2._queryStr("SELECT IFNULL(e.xuankaoqufen,1) ext1 from(SELECT d.id,d.category from define d where d.id={groupNum} union SELECT sd.id,sd.category from subdefine sd where sd.id={groupNum})d INNER JOIN exampaper e on d.category=e.examPaperNum where d.id={groupNum} ", args);
        args.put("xuankaoqufen", xuankaoqufen);
        Object[] stepoo = this.dao2._queryArray("SELECT  makType,judgetype from questiongroup_mark_setting WHERE groupNum={groupNum} ", args);
        int step = (stepoo[0] == null || !stepoo[0].toString().equals("1")) ? 1 : 2;
        info.setStep(step);
        info.setJudgetype(Integer.parseInt(String.valueOf(stepoo[1])));
        Object[] qinfo = this.dao2._queryArray("SELECT q.groupName,q.groupType,q.upperFloat,d.num chooseName,0 ext1,q.correctForbid from( SELECT groupNum,groupName,groupType,IFNULL(upperFloat,0) upperFloat,IFNULL(correctForbid,0) correctForbid from questiongroup WHERE groupNum={groupNum})q LEFT JOIN ( SELECT d.id,d.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,d.orderNum b,0 c,d.choosename num from define d LEFT JOIN define d1 on d.choosename=d1.id where d.id={groupNum} union SELECT sb.id,sb.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,sb.orderNum b, d.orderNum c,CONCAT(d1.id,sb.orderNum) num from subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id where sb.id={groupNum}\t)d on q.groupNum=d.id", args);
        info.setGroupName(String.valueOf(qinfo[0]));
        info.setGroupType(String.valueOf(qinfo[1]));
        info.setUpperFloat(String.valueOf(qinfo[2]));
        info.setChoosename(String.valueOf(qinfo[3] == null ? "s" : qinfo[3]));
        info.setExt1(String.valueOf(qinfo[4]));
        info.setCorrectForbid(String.valueOf(qinfo[5]));
        Object questionCountoo = this.dao2._queryObject("select count(1) from questiongroup_question where groupNum={groupNum} ", args);
        int questionCount = questionCountoo == null ? 0 : Integer.valueOf(questionCountoo.toString()).intValue();
        info.setQuestionCount(questionCount);
        Object[] qinfoo = this.dao2._queryArray("SELECT scancompleted,totalnum from questiongroup WHERE groupNum={groupNum} ", args);
        if (qinfo == null) {
            return null;
        }
        Object scancompletedoo = qinfoo[0];
        info.setScanStatus((scancompletedoo == null || !scancompletedoo.toString().equals("1")) ? "0" : "1");
        Map<String, Object> userMap = this.dao2._queryOrderMap("select qu.userNum,if(p.fenzuyuejuan=0,-1,IFNULL(sp.schoolGroupNum,-1)) schoolGroupNum from questiongroup_user qu left join exampaper p on qu.exampaperNum=p.examPaperNum LEFT JOIN `user` u ON u.id = qu.userNum LEFT JOIN schoolgroup sp on sp.schoolNum =u.schoolnum   WHERE qu.groupNum={groupNum} ", TypeEnum.StringObject, args);
        info.setUserSchoolGroupMap(userMap);
        if (userMap != null && userMap.size() > 0) {
            info.setTeacherCount(userMap.size());
        } else {
            info.setTeacherCount(1);
        }
        Map assisterUserMap = this.dao2._queryOrderMap("select qu.userNum,cast(if(ass.assister is not null,0,1) as char) from questiongroup_user qu LEFT JOIN `user` u ON u.id = qu.userNum LEFT JOIN assistyuejuan ass on qu.groupNum=ass.groupNum and qu.userNum=ass.assister WHERE qu.groupNum={groupNum} ", TypeEnum.StringObject, args);
        Map map = this.dao2._queryOrderMap("SELECT insertUser,num from quota WHERE exampaperNum={exampaperNum} and groupNum={groupNum} ", TypeEnum.StringObject, args);
        new HashMap();
        if (!info.isSchoolGroup()) {
            sql = "SELECT -1 schoolGroupNum,ifnull(sum(qu.num),0) count from quota qu where groupNum={groupNum} ";
        } else {
            sql = "SELECT slg1.schoolGroupNum,IFNULL(slg2.count, 0) FROM(SELECT DISTINCT schoolGroupNum FROM schoolgroup) slg1 LEFT JOIN (SELECT IFNULL(sp.schoolGroupNum ,-1) schoolGroupNum,sum(qu.num) count from quota qu LEFT JOIN `user` u ON u.id = qu.insertUser LEFT JOIN schoolgroup sp ON sp.schoolNum = u.schoolnum where groupNum={groupNum} GROUP BY sp.schoolGroupNum) slg2 ON slg1.schoolGroupNum = slg2.schoolGroupNum";
        }
        Map<String, Integer> specifiedWorkMap = this.dao2._queryOrderMap(sql, TypeEnum.StringInteger, args);
        new HashMap();
        if (!info.isSchoolGroup()) {
            sql2 = "SELECT -1 schoolGroupNum,ifnull(count(1),0) count from quota qu where groupNum={groupNum} ";
        } else {
            sql2 = "SELECT slg1.schoolGroupNum,IFNULL(slg2.count, 0) FROM(SELECT DISTINCT schoolGroupNum FROM schoolgroup) slg1 LEFT JOIN (SELECT IFNULL(sp.schoolGroupNum ,-1) schoolGroupNum,count(1) count from quota qu LEFT JOIN `user` u ON u.id = qu.insertUser LEFT JOIN schoolgroup sp ON sp.schoolNum = u.schoolnum where groupNum={groupNum} GROUP BY sp.schoolGroupNum) slg2 ON slg1.schoolGroupNum = slg2.schoolGroupNum";
        }
        Map<String, Integer> specifiedTeacherMap = this.dao2._queryOrderMap(sql2, TypeEnum.StringInteger, args);
        new HashMap();
        if (!info.isSchoolGroup()) {
            sql3 = "SELECT -1 schoolGroupNum,ifnull(count(1),0)  FROM questiongroup_user qu WHERE qu.groupNum ={groupNum} ";
        } else {
            sql3 = "SELECT slg1.schoolGroupNum,IFNULL(slg2.count, 0) FROM(SELECT DISTINCT schoolGroupNum FROM schoolgroup) slg1 LEFT JOIN (SELECT IFNULL(sp.schoolGroupNum ,-1) schoolGroupNum,count(1) count FROM questiongroup_user qu LEFT JOIN `user` u ON u.id = qu.userNum LEFT JOIN schoolgroup sp ON sp.schoolNum = u.schoolnum WHERE qu.groupNum = {groupNum} GROUP BY sp.schoolGroupNum) slg2 ON slg1.schoolGroupNum = slg2.schoolGroupNum";
        }
        Map<String, Integer> teacherMap = this.dao2._queryOrderMap(sql3, TypeEnum.StringInteger, args);
        Map<String, Integer> beforecompletedTotalMap = new HashMap<>();
        beforecompletedTotalMap.put("-1", 0);
        Map<String, Map<String, Object>> map2 = new HashMap<>();
        if (!info.isSchoolGroup()) {
            sql4 = "SELECT insertUser,groupNum,GROUP_CONCAT(questionNum,'&',questionName,'&',questionScoreavg ORDER BY questionNum) questionInfo,schoolGroupNum, IFNULL(ROUND(sum(questionScoreavg*yipanTotal)/sum(yipanTotal),2),0) questionScoreavg,sum(yipanTotal) yipanTotal, 0 yingpanTotal from (select s.insertUser,s.groupNum,s.questionNum,IFNULL(d.questionNum,sd.questionNum) questionName,s.questionScoreavg,s.yipanTotal,-1 schoolGroupNum from (select insertUser,groupNum,questionNum,IFNULL(ROUND(avg(questionscore),2),0) questionScoreavg,count(id) yipanTotal from task where groupNum={groupNum} and status='T' group by insertUser,questionNum) s LEFT JOIN define d on s.questionNum=d.id LEFT JOIN subdefine sd on s.questionNum=sd.id ) t GROUP BY insertUser ";
        } else {
            sql4 = "SELECT insertUser,groupNum,GROUP_CONCAT(questionNum,'&',questionName,'&',questionScoreavg ORDER BY questionNum) questionInfo,IFNULL(schoolGroupNum,-1) schoolGroupNum,IFNULL(ROUND(sum(questionScoreavg*yipanTotal)/sum(yipanTotal),2),0) questionScoreavg,sum(yipanTotal) yipanTotal,0 yingpanTotal from (select s.insertUser insertUser,s.groupNum groupNum,s.questionNum questionNum,IFNULL(d.questionNum,sd.questionNum) questionName,sg.schoolGroupNum schoolGroupNum,questionScoreavg,yipanTotal from (select insertUser,groupNum,questionNum,IFNULL(ROUND(avg(questionscore),2),0) questionScoreavg,count(id) yipanTotal from task where groupNum={groupNum} and  status='T' group by insertUser,questionNum ) s LEFT JOIN `user` u on u.id=s.insertUser LEFT JOIN schoolgroup sg on sg.schoolNum = u.schoolNum LEFT JOIN define d on s.questionNum=d.id LEFT JOIN subdefine sd on s.questionNum=sd.id ) t GROUP BY insertUser,schoolGroupNum ORDER BY schoolGroupNum";
        }
        List<Map<String, Object>> list = this.dao2._queryMapList(sql4, null, args);
        if (list != null && list.size() > 0) {
            for (Map m : list) {
                String insertUser = m.get("insertUser").toString();
                args.put("insertUser", insertUser);
                if (map2.containsKey(insertUser)) {
                    Map<String, Object> innerMap = map2.get(insertUser);
                    innerMap.put("yipanTotal", Integer.valueOf(Integer.parseInt(innerMap.get("yipanTotal").toString()) + Integer.parseInt(m.get("yipanTotal").toString())));
                    if (userMap.containsKey(insertUser) && userMap.get(insertUser).toString().equals(m.get("schoolGroupNum").toString())) {
                        innerMap.put("questionInfo", m.get("questionInfo"));
                        innerMap.put("questionScoreavg", m.get("questionScoreavg"));
                    }
                } else {
                    map2.put(insertUser, m);
                }
            }
            int wancheng = 0;
            if (!info.isSchoolGroup()) {
                for (int i = 0; i < list.size(); i++) {
                    String yipanTotal = list.get(i).get("yipanTotal").toString();
                    wancheng += Integer.parseInt(yipanTotal);
                    if (i == list.size() - 1) {
                        beforecompletedTotalMap.put("-1", Integer.valueOf(wancheng));
                    }
                }
            } else {
                String schGroupNum = list.get(0).get("schoolGroupNum").toString();
                for (int i2 = 0; i2 < list.size(); i2++) {
                    Map<String, Object> m2 = list.get(i2);
                    String schoolGroupNum = m2.get("schoolGroupNum").toString();
                    args.put("insertUser", m2.get("insertUser").toString());
                    String yipanTotal2 = m2.get("yipanTotal").toString();
                    if (!schGroupNum.equals(schoolGroupNum)) {
                        beforecompletedTotalMap.put(schGroupNum, Integer.valueOf(wancheng));
                        wancheng = 0;
                        schGroupNum = schoolGroupNum;
                    } else {
                        wancheng += Integer.parseInt(yipanTotal2);
                    }
                    if (i2 == list.size() - 1) {
                        beforecompletedTotalMap.put(schGroupNum, Integer.valueOf(wancheng));
                    }
                }
            }
            if ("99033284194591945".equals(groupNum)) {
            }
        }
        Collection<Map<String, Object>> coll = map2.values();
        for (Map<String, Object> value : coll) {
            int newYipanTotal = Integer.valueOf(value.get("yipanTotal").toString()).intValue();
            value.put("yipanTotal", Integer.valueOf((int) Math.ceil(Integer.valueOf(newYipanTotal).intValue() / info.getQuestionCount())));
        }
        info.setAvgTotal((int) Math.ceil((info.getStudentCount() * info.getStep()) / info.getTeacherCount()));
        if (info.getQuestionCount() > 1) {
            Map<String, Object> questionAvgScoreMap = this.dao2._queryOrderMap("SELECT /* shard_host_HG=Write */ questionNum,IFNULL(ROUND(avg(questionscore),2),0) questionScoreavg from  task  where groupNum={groupNum} and status='T' group by questionNum", TypeEnum.StringObject, args);
            info.setQuestionAvgScoreMap(questionAvgScoreMap);
        }
        Map<String, Object> schoolGroupAvgInfo = this.dao2._queryOrderMap("SELECT questionNum,GROUP_CONCAT(schoolGroupNum,'-',questionScoreavg) schoolGroupAvgInfo from (select t.questionNum,sp.schoolGroupNum,IFNULL(ROUND(avg(t.questionscore),2),0) questionScoreavg from task  t LEFT JOIN score s on s.id=t.scoreId LEFT JOIN schoolgroup sp on sp.schoolNum=s.schoolNum where t.groupNum={groupNum} and t.status='T' group by t.questionNum,sp.schoolGroupNum ) t2 GROUP BY questionNum", TypeEnum.StringObject, args);
        Map<String, Map<String, Object>> schoolGroupQuestionAvgInfoMap = new HashMap<>();
        if (schoolGroupAvgInfo != null && schoolGroupAvgInfo.size() > 0) {
            Set<Map.Entry<String, Object>> entrySet = schoolGroupAvgInfo.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                String key = entry.getKey();
                Object value2 = entry.getValue();
                Map<String, Object> tempMap = new HashMap<>();
                if (value2 != null && !value2.toString().equals("")) {
                    String[] allSchoolGroupAvgInfoArray = value2.toString().split(Const.STRING_SEPERATOR);
                    for (String str2 : allSchoolGroupAvgInfoArray) {
                        String[] oneSchoolGroupAvgInfoArray = str2.split("-");
                        String schoolGroupNum2 = oneSchoolGroupAvgInfoArray[0];
                        String avg = oneSchoolGroupAvgInfoArray[1];
                        tempMap.put(schoolGroupNum2, avg);
                    }
                }
                schoolGroupQuestionAvgInfoMap.put(key, tempMap);
            }
        }
        info.setSchoolGroupQuestionAvgInfoMap(schoolGroupQuestionAvgInfoMap);
        double groupAvgScore = 0.0d;
        int newUserCount = 0;
        int completedTotal = 0;
        if (userMap != null && userMap.size() > 0) {
            Set<String> setEach = userMap.keySet();
            Map<String, String> completedTotalMap = new HashMap<>();
            if (info.isSchoolGroup()) {
                completedTotalMap = this.dao2._queryOrderMap("select schoolGroupNum,CAST(0 AS signed) ext1 from schoolgroup where enforce=1 GROUP BY schoolGroupNum", TypeEnum.StringString, args);
            }
            completedTotalMap.put("-1", "0");
            new HashMap();
            new HashMap();
            if ("1".equals(xuankaoqufen)) {
                reg_map = !info.isSchoolGroup() ? bigTableMapReg.get("regTotal").get(exampaperNum) : bigTableMapReg.get("regTotalIn").get(exampaperNum);
                stu_map = !info.isSchoolGroup() ? bigTableMapExamina.get("examinaTotal").get(exampaperNum) : bigTableMapExamina.get("examinaTotalIn").get(exampaperNum);
            } else if ("2".equals(xuankaoqufen)) {
                reg_map = !info.isSchoolGroup() ? bigTableMapReg.get("regXuan").get(exampaperNum) : bigTableMapReg.get("regXuanIn").get(exampaperNum);
                stu_map = !info.isSchoolGroup() ? bigTableMapExamina.get("examinaXuan").get(exampaperNum) : bigTableMapExamina.get("examinaXuanIn").get(exampaperNum);
            } else {
                reg_map = !info.isSchoolGroup() ? bigTableMapReg.get("regXue").get(exampaperNum) : bigTableMapReg.get("regXueIn").get(exampaperNum);
                stu_map = !info.isSchoolGroup() ? bigTableMapExamina.get("examinaXue").get(exampaperNum) : bigTableMapExamina.get("examinaXueIn").get(exampaperNum);
            }
            Map<String, Map<String, Map<String, Integer>>> bigTableMapTaskChoose = (Map) context.getAttribute(Const.bigTableMapTaskChoose);
            Map<String, String> taskChooseMap = new HashMap<>();
            if (!"s".equals(info.getChoosename())) {
                String sql15 = "SELECT t.insertUser,ifnull(cast(count(1)/" + info.getQuestionCount() + " as signed),0) from task t inner JOIN( \tSELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename \t\twhere d.id={groupNum} \t\tUNION \t\tSELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename \t\tLEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum \t\twhere sb.id={groupNum} \t)d on t.groupNum=d.id  \twhere t.examPaperNum={exampaperNum} and t.xuankaoqufen={xuankaoqufen} and t.`status`='T'  \tGROUP BY t.insertUser";
                taskChooseMap = this.dao2._queryOrderMap(sql15, TypeEnum.StringString, args);
                reg_map = !info.isSchoolGroup() ? bigTableMapTaskChoose.get("taskChooseTotal").get(groupNum) : bigTableMapTaskChoose.get("taskChooseTotalIn").get(groupNum);
                if (!info.isSchoolGroup()) {
                    sql14 = "select -1 schoolGroupNum,ifnull(count(DISTINCT qu.userNum),0) from questiongroup_user qu INNER JOIN( SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={groupNum} union SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum where sb.id={groupNum})d on qu.groupNum=d.id LEFT JOIN `user` u ON u.id = qu.userNum ";
                } else {
                    sql14 = "select IFNULL(sp.schoolGroupNum,-1) schoolGroupNum,count(DISTINCT qu.userNum) from questiongroup_user qu INNER JOIN(SELECT d1.id from define d left JOIN  define d1 on d.choosename=d1.choosename where d.id={groupNum} union SELECT sbb.id from subdefine sb LEFT JOIN define d on sb.pid=d.id LEFT JOIN define d2 on d.choosename=d2.choosename LEFT JOIN subdefine sbb on d2.id=sbb.pid  and sb.orderNum=sbb.orderNum where sb.id={groupNum} )d on qu.groupNum=d.id LEFT JOIN `user` u ON u.id = qu.userNum LEFT JOIN schoolgroup sp on sp.schoolNum =u.schoolnum GROUP BY sp.schoolGroupNum";
                }
                teacherMap = this.dao2._queryOrderMap(sql14, TypeEnum.StringInteger, args);
            }
            Map<String, Integer> three_map = new HashMap<>();
            Map<String, String> yiPanTotalMap = new HashMap<>();
            Map<String, String> reMainMap = new HashMap<>();
            Map<String, Integer> hasFinishTotalNumMap = new HashMap<>();
            Map<String, Integer> noFinishTotalQuotaMap = new HashMap<>();
            if (info.getStep() == 2 && info.getJudgetype() != 0) {
                Object threeAlloo = this.dao2._queryObject("SELECT cast(count(1)/count(DISTINCT(questionNum)) as signed) from task WHERE examPaperNum={exampaperNum} and groupNum={groupNum} and userNum=3", args);
                int threeAll = threeAlloo == null ? 0 : Integer.valueOf(threeAlloo.toString()).intValue();
                info.setThreeAllTotal(threeAll);
                if (!info.isSchoolGroup()) {
                    sql9 = "SELECT -1,ifnull(sum(r.count),0) count from (SELECT t.groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed) count from task t where t.groupNum={groupNum} and userNum=3 )r where r.groupNum={groupNum} ";
                } else {
                    sql9 = "SELECT slg1.schoolGroupNum,IFNULL(slg2.count, 0) FROM(SELECT DISTINCT schoolGroupNum FROM schoolgroup) slg1 LEFT JOIN (SELECT ifnull(slg.schoolGroupNum,-1) schoolGroupNum,sum(r.count) count from (SELECT t.groupNum,s.schoolNum,cast(count(1)/count(DISTINCT(questionNum)) as signed) count from task t INNER JOIN student s on t.studentId=s.id where t.groupNum={groupNum} and userNum=3 GROUP BY s.schoolNum)r LEFT JOIN schoolgroup slg on r.schoolNum=slg.schoolNum where r.groupNum={groupNum} GROUP BY slg.schoolGroupNum) slg2 ON slg1.schoolGroupNum = slg2.schoolGroupNum";
                }
                three_map = this.dao2._queryOrderMap(sql9, TypeEnum.StringInteger, args);
                if (!info.isSchoolGroup()) {
                    sql10 = "SELECT -1,IFNULL(count(1),0) from( SELECT studentId,groupNum,count(1) count from task where groupNum={groupNum} and status='T'  GROUP BY studentId HAVING count>1 )t ";
                } else {
                    sql10 = "SELECT IFNULL(slg.schoolGroupNum,-1),IFNULL(count(1),0) from( SELECT studentId,groupNum,count(1) count from task where groupNum={groupNum} and status='T' GROUP BY studentId HAVING count>1 )t left JOIN student s on t.studentId = s.id left join schoolgroup slg on slg.schoolNum = s.schoolNum GROUP BY slg.schoolGroupNum";
                }
                yiPanTotalMap = this.dao2._queryOrderMap(sql10, TypeEnum.StringString, args);
                if (!info.isSchoolGroup()) {
                    sql11 = "SELECT -1,IFNULL(count(1),0) from(SELECT studentId,groupNum,count(1) count from task where groupNum={groupNum} and status='F' and userNum<>3 GROUP BY studentId )t ";
                } else {
                    sql11 = "SELECT IFNULL(slg.schoolGroupNum,-1),IFNULL(count(1),0) from(SELECT studentId,groupNum,count(1) count from task where groupNum={groupNum} and status='F' and userNum<>3 GROUP BY studentId )t left JOIN student s on t.studentId = s.id left join schoolgroup slg on slg.schoolNum = s.schoolNum GROUP BY slg.schoolGroupNum";
                }
                reMainMap = this.dao2._queryOrderMap(sql11, TypeEnum.StringString, args);
                if (!info.isSchoolGroup()) {
                    sql12 = "select -1 schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN( SELECT qw.* from(SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count>=qw.num )e on qu.groupNum=e.groupNum and qu.userNum=e.userNum where qu.groupNum={groupNum} ";
                } else {
                    sql12 = "select IFNULL(sp.schoolGroupNum ,-1) schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN(SELECT qw.* from( SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count>=qw.num )e on qu.groupNum=e.groupNum and qu.userNum=e.userNum LEFT JOIN `user` u ON u.id = qu.userNum LEFT JOIN schoolgroup sp ON sp.schoolNum = u.schoolnum where qu.groupNum={groupNum} GROUP BY sp.schoolGroupNum";
                }
                hasFinishTotalNumMap = this.dao2._queryOrderMap(sql12, TypeEnum.StringInteger, args);
                if (!info.isSchoolGroup()) {
                    sql13 = "select -1 schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN(SELECT qw.* from(SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count<qw.num )e on qu.groupNum=e.groupNum and qu.userNum=e.userNum  where qu.groupNum={groupNum} ";
                } else {
                    sql13 = "select IFNULL(sp.schoolGroupNum ,-1) schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN(SELECT qw.* from(SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count<qw.num )e on qu.groupNum=e.groupNum and qu.userNum=e.userNum LEFT JOIN `user` u ON u.id = e.userNum LEFT JOIN schoolgroup sp ON sp.schoolNum = u.schoolnum where qu.groupNum={groupNum} GROUP BY sp.schoolGroupNum";
                }
                noFinishTotalQuotaMap = this.dao2._queryOrderMap(sql13, TypeEnum.StringInteger, args);
            } else {
                info.setThreeAllTotal(0);
            }
            if (stu_map != null && !"".equals(stu_map) && !"null".equals(stu_map) && stu_map.size() > 0) {
                if (three_map.size() == 0) {
                    for (String schoolgroupNum : stu_map.keySet()) {
                        three_map.put(schoolgroupNum, 0);
                    }
                }
                if (yiPanTotalMap.size() == 0) {
                    for (String schoolgroupNum2 : stu_map.keySet()) {
                        yiPanTotalMap.put(schoolgroupNum2, "0");
                    }
                }
                if (reMainMap.size() == 0) {
                    for (String schoolgroupNum3 : stu_map.keySet()) {
                        reMainMap.put(schoolgroupNum3, "0");
                    }
                }
                if (hasFinishTotalNumMap.size() == 0) {
                    for (String schoolgroupNum4 : stu_map.keySet()) {
                        hasFinishTotalNumMap.put(schoolgroupNum4, 0);
                    }
                }
                if (noFinishTotalQuotaMap.size() == 0) {
                    for (String schoolgroupNum5 : stu_map.keySet()) {
                        noFinishTotalQuotaMap.put(schoolgroupNum5, 0);
                    }
                }
                if (specifiedWorkMap.size() == 0) {
                    for (String schoolgroupNum6 : stu_map.keySet()) {
                        specifiedWorkMap.put(schoolgroupNum6, 0);
                    }
                }
                if (specifiedTeacherMap.size() == 0) {
                    for (String schoolgroupNum7 : stu_map.keySet()) {
                        specifiedTeacherMap.put(schoolgroupNum7, 0);
                    }
                }
                Map<String, Integer> preDictThreeMap = new HashMap<>();
                Map<String, Double> threePingLvMap = new HashMap<>();
                if (info.getStep() == 2 && info.getJudgetype() != 0) {
                    for (String schoolGroupNum3 : stu_map.keySet()) {
                        int three_count = Integer.parseInt(String.valueOf(three_map.get(schoolGroupNum3) == null ? "0" : three_map.get(schoolGroupNum3)));
                        int yiPanTotal = Integer.parseInt(String.valueOf(yiPanTotalMap.get(schoolGroupNum3) == null ? "0" : yiPanTotalMap.get(schoolGroupNum3)));
                        double sanpinglv = Double.valueOf(three_count).doubleValue() / (yiPanTotal == 0 ? 1 : yiPanTotal);
                        int benzuwancheng1 = beforecompletedTotalMap.get(schoolGroupNum3) == null ? 0 : beforecompletedTotalMap.get(schoolGroupNum3).intValue();
                        double wanchenglv1 = Double.valueOf(benzuwancheng1).doubleValue() / ((Integer.parseInt(String.valueOf(stu_map.get(schoolGroupNum3))) * 2) + three_count == 0 ? 1 : r0);
                        if (sanpinglv < 0.1d || wanchenglv1 < 0.5d) {
                            sanpinglv = 0.1d;
                        }
                        int reMain = Integer.parseInt(String.valueOf(reMainMap.get(schoolGroupNum3) == null ? "0" : reMainMap.get(schoolGroupNum3)));
                        int yugusanping = (int) (reMain * sanpinglv);
                        preDictThreeMap.put(schoolGroupNum3, Integer.valueOf(yugusanping));
                        threePingLvMap.put(schoolGroupNum3, Double.valueOf(sanpinglv));
                    }
                }
                if (preDictThreeMap.size() == 0) {
                    for (String schoolgroupNum8 : stu_map.keySet()) {
                        preDictThreeMap.put(schoolgroupNum8, 0);
                    }
                }
                if (threePingLvMap.size() == 0) {
                    for (String schoolgroupNum9 : stu_map.keySet()) {
                        threePingLvMap.put(schoolgroupNum9, Double.valueOf(0.0d));
                    }
                }
                new HashMap();
                Map<String, Integer> allTotal_map = new HashMap<>();
                Map<String, Integer> allTotal_map_before = "1".equals(info.getScanStatus()) ? bigTableMapReg.get("regTotal").get(exampaperNum) : bigTableMapExamina.get("examinaTotal").get(exampaperNum);
                info.setAllTotal(Integer.parseInt(String.valueOf(allTotal_map_before.get("-1") == null ? "0" : allTotal_map_before.get("-1"))));
                Map<String, Integer> total_map = new HashMap<>();
                Map<String, Integer> total1_map = new HashMap<>();
                for (String schoolGroupNum4 : stu_map.keySet()) {
                    int student_count = Integer.parseInt(String.valueOf(stu_map.get(schoolGroupNum4)));
                    int reg_count = Integer.parseInt(String.valueOf(reg_map.get(schoolGroupNum4) == null ? "0" : reg_map.get(schoolGroupNum4)));
                    int three_count2 = Integer.parseInt(String.valueOf(three_map.get(schoolGroupNum4) == null ? "0" : three_map.get(schoolGroupNum4)));
                    int three_count1 = Integer.parseInt(String.valueOf(preDictThreeMap.get(schoolGroupNum4) == null ? "0" : preDictThreeMap.get(schoolGroupNum4)));
                    int allTotal_count = Integer.parseInt(String.valueOf(allTotal_map_before.get("-1") == null ? "0" : allTotal_map_before.get("-1")));
                    if ("1".equals(info.getScanStatus())) {
                        total_map.put(schoolGroupNum4, Integer.valueOf((reg_count * info.getStep()) + three_count2));
                    } else {
                        total_map.put(schoolGroupNum4, Integer.valueOf((student_count * info.getStep()) + three_count2));
                    }
                    total1_map.put(schoolGroupNum4, Integer.valueOf((student_count * info.getStep()) + three_count2 + three_count1));
                    allTotal_map.put(schoolGroupNum4, Integer.valueOf((allTotal_count * info.getStep()) + info.getThreeAllTotal()));
                }
                for (String key2 : setEach) {
                    args.put("insertUser", key2);
                    String schoolGroupNum5 = String.valueOf(userMap.get(key2));
                    if (stu_map.containsKey(schoolGroupNum5)) {
                        int info_total = total_map.get(schoolGroupNum5).intValue();
                        int info_total1 = total1_map.get(schoolGroupNum5).intValue();
                        int info_three = Integer.parseInt(String.valueOf(three_map.get(schoolGroupNum5)));
                        int info_allTotal = allTotal_map.get(schoolGroupNum5).intValue();
                        Map<String, Object> m3 = new HashMap();
                        if (map2.containsKey(key2)) {
                            m3 = map2.get(key2);
                            m3.put("groupInTotal", Integer.valueOf(info_total));
                            m3.put("threeTotal", Integer.valueOf(info_three));
                        } else {
                            m3.put("groupInTotal", Integer.valueOf(info_total));
                            m3.put("threeTotal", Integer.valueOf(info_three));
                            m3.put("insertUser", key2);
                            m3.put("groupNum", groupNum);
                            m3.put("questionScoreavg", 0);
                            m3.put("questionInfo", "0&0");
                            m3.put("yipanTotal", 0);
                            map2.put(key2, m3);
                            newUserCount++;
                        }
                        m3.put("assisterType", assisterUserMap.get(key2));
                        Integer.parseInt(String.valueOf(three_map.get(schoolGroupNum5) == null ? "0" : three_map.get(schoolGroupNum5)));
                        int benzuwancheng = beforecompletedTotalMap.get(schoolGroupNum5) == null ? 0 : beforecompletedTotalMap.get(schoolGroupNum5).intValue();
                        Integer.parseInt(String.valueOf(yiPanTotalMap.get(schoolGroupNum5) == null ? "0" : yiPanTotalMap.get(schoolGroupNum5)));
                        double wanchenglv = Double.valueOf(benzuwancheng).doubleValue() / (info_total == 0 ? 1 : info_total);
                        int specifiedTeacherCount = Integer.parseInt(String.valueOf(specifiedTeacherMap.get(schoolGroupNum5)));
                        int specifiedWorkTotal = Integer.parseInt(String.valueOf(specifiedWorkMap.get(schoolGroupNum5)));
                        info.setTeacherCount(Integer.parseInt(String.valueOf(teacherMap.get(schoolGroupNum5) == null ? "0" : teacherMap.get(schoolGroupNum5))));
                        String sanpinglv2 = threePingLvMap.get(schoolGroupNum5).doubleValue() + "";
                        if (specifiedTeacherCount != 0 && specifiedTeacherCount == info.getTeacherCount() && info.getStep() == 2 && !"0.1".equals(sanpinglv2) && info.getJudgetype() != 0 && "0".equals(info.getCorrectForbid()) && wanchenglv > 0.5d) {
                            int hasFinishTotalNum = Integer.parseInt(String.valueOf(hasFinishTotalNumMap.get(schoolGroupNum5)));
                            int noFinishTotalQuota = (noFinishTotalQuotaMap.get(schoolGroupNum5) == null || Integer.parseInt(String.valueOf(noFinishTotalQuotaMap.get(schoolGroupNum5))) == 0) ? 1 : Integer.parseInt(String.valueOf(noFinishTotalQuotaMap.get(schoolGroupNum5)));
                            int quota = Integer.parseInt(String.valueOf(map.get(key2)));
                            if (Integer.parseInt(String.valueOf(m3.get("yipanTotal"))) < quota) {
                                Map yiPanArgs = new HashMap();
                                yiPanArgs.put("insertUser", key2);
                                yiPanArgs.put("exampaperNum", exampaperNum);
                                yiPanArgs.put("groupNum", groupNum);
                                Integer yiPanNum = Convert.toInt(this.dao2._queryInt("SELECT count(1) count FROM task  WHERE groupNum ={groupNum} and insertUser={insertUser} AND STATUS = 'T' group by questionNum LIMIT 1 ", yiPanArgs), 0);
                                m3.put("yipanTotal", yiPanNum);
                            }
                            Integer stuCount = stu_map.get(schoolGroupNum5);
                            String isContinue = Configuration.getInstance().getIsContinue();
                            if (Integer.parseInt(String.valueOf(m3.get("yipanTotal"))) >= quota) {
                                m3.put("yingpanTotal", map.get(key2));
                            } else if (isContinue.equals("0")) {
                                if (quota != 0) {
                                    if (!info.isSchoolGroup()) {
                                        sql7 = "select -1 schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN(SELECT qw.* from(SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count<qw.num and qw.num>{stuCount})e on qu.groupNum=e.groupNum and qu.userNum=e.userNum  where qu.groupNum={groupNum} ";
                                    } else {
                                        sql7 = "select IFNULL(sp.schoolGroupNum ,-1) schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN(SELECT qw.* from(SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count<qw.num  and qw.num>{stuCount} )e on qu.groupNum=e.groupNum and qu.userNum=e.userNum LEFT JOIN `user` u ON u.id = e.userNum LEFT JOIN schoolgroup sp ON sp.schoolNum = u.schoolnum where qu.groupNum={groupNum} GROUP BY sp.schoolGroupNum";
                                    }
                                    args.put("stuCount", stu_map.get(schoolGroupNum5));
                                    this.dao2._queryOrderMap(sql7, TypeEnum.StringObject, args);
                                    if (!info.isSchoolGroup()) {
                                        sql8 = "select -1 schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN( SELECT qw.* from(SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count>=qw.num and qw.num>{stuCount})e on qu.groupNum=e.groupNum and qu.userNum=e.userNum where qu.groupNum={groupNum} ";
                                    } else {
                                        sql8 = "select IFNULL(sp.schoolGroupNum ,-1) schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN(SELECT qw.* from( SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count>=qw.num and qw.num>{stuCount} )e on qu.groupNum=e.groupNum and qu.userNum=e.userNum LEFT JOIN `user` u ON u.id = qu.userNum LEFT JOIN schoolgroup sp ON sp.schoolNum = u.schoolnum where qu.groupNum={groupNum} GROUP BY sp.schoolGroupNum";
                                    }
                                    this.dao2._queryOrderMap(sql8, TypeEnum.StringObject, args);
                                    int aa = info_total1 - hasFinishTotalNum;
                                    double bb = quota / noFinishTotalQuota;
                                    int cc = (int) Math.ceil(aa * bb);
                                    args.put("cc", Integer.valueOf(cc));
                                    args.put("insertUser", m3.get("insertUser"));
                                    if (cc > quota) {
                                        m3.put("yingpanTotal", Integer.valueOf(cc));
                                        this.dao2._execute("insert into   quota (exampaperNum,groupNum,num,insertUser,insertDate) values({exampaperNum},{groupNum} ,{cc},{insertUser} ,now() )  ON DUPLICATE KEY UPDATE num={cc} ", args);
                                    } else {
                                        m3.put("yingpanTotal", Integer.valueOf(quota));
                                    }
                                } else {
                                    m3.put("yingpanTotal", 0);
                                }
                            } else if (quota >= stuCount.intValue()) {
                                m3.put("yingpanTotal", map.get(key2));
                            } else if (quota != 0) {
                                if (!info.isSchoolGroup()) {
                                    sql5 = "select -1 schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN(SELECT qw.* from(SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count<qw.num and qw.num>{stuCount})e on qu.groupNum=e.groupNum and qu.userNum=e.userNum  where qu.groupNum={groupNum} ";
                                } else {
                                    sql5 = "select IFNULL(sp.schoolGroupNum ,-1) schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN(SELECT qw.* from(SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count<qw.num  and qw.num>{stuCount} )e on qu.groupNum=e.groupNum and qu.userNum=e.userNum LEFT JOIN `user` u ON u.id = e.userNum LEFT JOIN schoolgroup sp ON sp.schoolNum = u.schoolnum where qu.groupNum={groupNum} GROUP BY sp.schoolGroupNum";
                                }
                                args.put("stuCount", stu_map.get(schoolGroupNum5));
                                Map<Object, Object> noFinishDayuStuCountTotalCountMap = this.dao2._queryOrderMap(sql5, TypeEnum.StringObject, args);
                                if (!info.isSchoolGroup()) {
                                    sql6 = "select -1 schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN( SELECT qw.* from(SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count>=qw.num and qw.num>{stuCount})e on qu.groupNum=e.groupNum and qu.userNum=e.userNum where qu.groupNum={groupNum} ";
                                } else {
                                    sql6 = "select IFNULL(sp.schoolGroupNum ,-1) schoolGroupNum,ifnull(sum(e.num),0) from questiongroup_user qu LEFT JOIN(SELECT qw.* from( SELECT qu.*,ifnull(t.count,0) count,qt.num from questiongroup_user qu left join (select groupNum,insertuser,count(1) count from task t where t.groupNum={groupNum} and t.status='T' GROUP BY t.insertUser ) t on qu.groupNum=t.groupNum and qu.userNum=t.insertUser inner join quota qt on qu.groupNum=qt.groupNum and qu.userNum=qt.insertUser where qu.groupNum={groupNum} )qw where qw.count>=qw.num and qw.num>{stuCount})e on qu.groupNum=e.groupNum and qu.userNum=e.userNum LEFT JOIN `user` u ON u.id = qu.userNum LEFT JOIN schoolgroup sp ON sp.schoolNum = u.schoolnum where qu.groupNum={groupNum} GROUP BY sp.schoolGroupNum";
                                }
                                this.dao2._queryOrderMap(sql6, TypeEnum.StringObject, args);
                                int aa2 = (info_total1 - hasFinishTotalNum) - Integer.parseInt(noFinishDayuStuCountTotalCountMap.get(schoolGroupNum5).toString());
                                double bb2 = quota / (noFinishTotalQuota - Integer.parseInt(noFinishDayuStuCountTotalCountMap.get(schoolGroupNum5).toString()));
                                int cc2 = (int) Math.ceil(aa2 * bb2);
                                if (cc2 > stu_map.get(schoolGroupNum5).intValue()) {
                                    cc2 = stu_map.get(schoolGroupNum5).intValue();
                                }
                                args.put("cc", Integer.valueOf(cc2));
                                args.put("insertUser", m3.get("insertUser"));
                                if (cc2 > quota) {
                                    m3.put("yingpanTotal", Integer.valueOf(cc2));
                                    this.dao2._execute("insert into   quota (exampaperNum,groupNum,num,insertUser,insertDate) values({exampaperNum},{groupNum} ,{cc},{insertUser} ,now() )  ON DUPLICATE KEY UPDATE num={cc} ", args);
                                } else {
                                    m3.put("yingpanTotal", Integer.valueOf(quota));
                                }
                            } else {
                                m3.put("yingpanTotal", 0);
                            }
                        } else {
                            int yingpanCount = 0;
                            if (map != null && map.size() > 0) {
                                if (map.containsKey(key2)) {
                                    m3.put("yingpanTotal", map.get(key2));
                                } else {
                                    int actualTeacherCount = info.getTeacherCount() <= specifiedTeacherCount ? 1 : info.getTeacherCount() - specifiedTeacherCount;
                                    int yingpanCount2 = (int) Math.ceil((info_total1 - specifiedWorkTotal) / actualTeacherCount);
                                    yingpanCount = yingpanCount2 < 0 ? 0 : yingpanCount2;
                                    args.put("yingpanCount", Integer.valueOf(yingpanCount));
                                    m3.put("yingpanTotal", Integer.valueOf(yingpanCount));
                                }
                            } else {
                                int yingpanCount3 = (int) Math.ceil(info_total1 / info.getTeacherCount());
                                yingpanCount = yingpanCount3 < 0 ? 0 : yingpanCount3;
                                m3.put("yingpanTotal", Integer.valueOf(yingpanCount));
                                args.put("yingpanCount", Integer.valueOf(yingpanCount));
                            }
                            args.put("insertUser", m3.get("insertUser"));
                            if (map.get(m3.get("insertUser")) == null && Integer.parseInt(String.valueOf(m3.get("yipanTotal"))) >= yingpanCount && yingpanCount != 0 && info.getStep() == 2) {
                                this.dao2._execute("insert into   quota (exampaperNum,groupNum,num,insertUser,insertDate) values({exampaperNum},{groupNum},{yingpanCount},{insertUser} ,now() )  ON DUPLICATE KEY UPDATE num={yingpanCount} ", args);
                            }
                        }
                        Map<String, Object> map3 = m3;
                        if (taskChooseMap == null) {
                            str = "0";
                        } else {
                            str = taskChooseMap.get(key2) == null ? "0" : taskChooseMap.get(key2);
                        }
                        map3.put("yipanChooseTotal", str);
                        m3.put("choosename", info.getChoosename());
                        groupAvgScore += Double.valueOf(m3.get("questionScoreavg").toString()).doubleValue() * Integer.valueOf(m3.get("yipanTotal").toString()).intValue();
                        if (completedTotalMap.containsKey(schoolGroupNum5)) {
                            int completed1 = Integer.parseInt(String.valueOf(completedTotalMap.get(schoolGroupNum5)));
                            completedTotalMap.put(schoolGroupNum5, String.valueOf(completed1 + Integer.valueOf(m3.get("yipanTotal").toString()).intValue()));
                        }
                        completedTotal += Integer.valueOf(m3.get("yipanTotal").toString()).intValue();
                        PersonWorkRecord pwe = new PersonWorkRecord();
                        pwe.setExamPaperNum(exampaperNum);
                        pwe.setGroupNum(groupNum);
                        pwe.setUserNum(String.valueOf(m3.get("insertUser")));
                        pwe.setPersonYiPan(m3.get("yipanTotal").toString());
                        pwe.setPersonYingPan(m3.get("yingpanTotal").toString());
                        pwe.setGroupInTotal(String.valueOf(info_total));
                        pwe.setAllTotal(String.valueOf(info_allTotal));
                        personWorkRecordMap.put(groupNum + "-" + m3.get("insertUser"), pwe);
                    }
                }
                for (String key3 : setEach) {
                    args.put("insertUser", key3);
                    String schoolGroupNum6 = String.valueOf(userMap.get(key3));
                    if (stu_map.containsKey(schoolGroupNum6)) {
                        map2.get(key3).put("groupInCompltedTotal", completedTotalMap.get(schoolGroupNum6));
                        if (personWorkRecordMap.containsKey(groupNum + "-" + key3)) {
                            PersonWorkRecord pwe2 = personWorkRecordMap.get(groupNum + "-" + key3);
                            pwe2.setGroupInCompltedTotal(String.valueOf(completedTotalMap.get(schoolGroupNum6)));
                            pwe2.setCompletedTotal(String.valueOf(completedTotal));
                        }
                    }
                }
            }
        }
        info.setCompletedTotal(completedTotal);
        double avgScore = new BigDecimal(groupAvgScore / (completedTotal == 0 ? 1 : completedTotal)).setScale(2, 4).doubleValue();
        info.setAvgScore(avgScore);
        info.setUserQuestionGroupInfoMap(map2);
        if (System.currentTimeMillis() - aaaa > 2000) {
            this.log4j.info("【进度条】【" + groupNum + "】-------- " + (new Date().getTime() - aaaa));
        }
        return info;
    }

    public List<ChooseScale> getChoose(List<ChooseScale> cslist, QuestionGroupInfo info) {
        Map args = new HashMap();
        args.put("ExampaperNum", info.getExampaperNum());
        int dahui = this.dao2._queryInt("SELECT IFNULL(CAST(count(1)/e.totalPage AS signed),0) from cantrecognized c  INNER JOIN regexaminee r on c.regId=r.id LEFT JOIN exampaper e on c.exampaperNum=e.exampaperNum  WHERE c.exampaperNum={ExampaperNum} ", args).intValue();
        String chooseScaleNum = "";
        String chooseScaleCount = "";
        int chooseScaleTotal = 0;
        String orderNum = cslist.get(0).getOrderNum();
        for (int i = 0; i < cslist.size(); i++) {
            ChooseScale cs = cslist.get(i);
            if (!orderNum.equals(cs.getOrderNum())) {
                for (int h = 0; h < cslist.size(); h++) {
                    if (orderNum.equals(cslist.get(h).getOrderNum())) {
                        ChooseScale beforecs = cslist.get(h);
                        beforecs.setChooseScaleNum(chooseScaleNum);
                        beforecs.setChooseScaleCount(chooseScaleCount);
                        beforecs.setChooseScaleTotal(chooseScaleTotal + "");
                    }
                }
                orderNum = cs.getOrderNum();
                chooseScaleNum = "";
                chooseScaleCount = "";
                chooseScaleTotal = 0;
            }
            chooseScaleNum = chooseScaleNum + cs.getGroupNum() + ":";
            String scTotalNum = "null".equals(cs.getTotalNum()) ? "0" : cs.getTotalNum();
            chooseScaleCount = chooseScaleCount + scTotalNum + ":";
            chooseScaleTotal += Integer.parseInt(scTotalNum);
            if (i == cslist.size() - 1) {
                for (int h2 = 0; h2 < cslist.size(); h2++) {
                    if (orderNum.equals(cslist.get(h2).getOrderNum())) {
                        ChooseScale beforecs2 = cslist.get(h2);
                        beforecs2.setChooseScaleNum(chooseScaleNum);
                        beforecs2.setChooseScaleCount(chooseScaleCount);
                        beforecs2.setChooseScaleTotal(chooseScaleTotal + "");
                    }
                }
            }
        }
        for (int i2 = 0; i2 < cslist.size(); i2++) {
            ChooseScale chooseScale = cslist.get(i2);
            String scTotalNum2 = "null".equals(chooseScale.getTotalNum()) ? "0" : chooseScale.getTotalNum();
            String scTotal = "null".equals(chooseScale.getTotal()) ? "0" : chooseScale.getTotal();
            String scChooseScaleTotal = "0".equals(chooseScale.getChooseScaleTotal()) ? "1" : chooseScale.getChooseScaleTotal();
            int totalNum = Integer.parseInt(scTotalNum2) + (((dahui * info.getStep()) * Integer.parseInt(scTotalNum2)) / Integer.parseInt(scChooseScaleTotal)) + Integer.parseInt(chooseScale.getThree());
            chooseScale.setTotalNum(String.valueOf(totalNum));
            int allTotal = Integer.parseInt(scTotal);
            String upperFloat = (info.getUpperFloat() == null || "".equals(info.getUpperFloat()) || "null".equals(info.getUpperFloat())) ? "0" : info.getUpperFloat();
            int total = ((allTotal * Integer.parseInt(scTotalNum2)) / Integer.parseInt(scChooseScaleTotal)) + Math.round((allTotal * Integer.parseInt(upperFloat)) / 100) + Integer.parseInt(chooseScale.getThree());
            chooseScale.setTotal(String.valueOf(total));
        }
        Map<String, Integer> tempAlltotalNum = new HashMap<>();
        Map<String, Integer> tempAlltotal = new HashMap<>();
        for (int i3 = 0; i3 < cslist.size(); i3++) {
            ChooseScale cs2 = cslist.get(i3);
            String scTotalNum3 = "null".equals(cs2.getTotalNum()) ? "0" : cs2.getTotalNum();
            String scTotal2 = "null".equals(cs2.getTotal()) ? "0" : cs2.getTotal();
            int tempTotalNum = Integer.parseInt(scTotalNum3);
            if (tempAlltotalNum.containsKey(cs2.getGroupNum())) {
                tempTotalNum += tempAlltotalNum.get(cs2.getGroupNum()).intValue();
            }
            tempAlltotalNum.put(cs2.getGroupNum(), Integer.valueOf(tempTotalNum));
            int tempTotal = Integer.parseInt(scTotal2);
            if (tempAlltotal.containsKey(cs2.getGroupNum())) {
                tempTotal += tempAlltotal.get(cs2.getGroupNum()).intValue();
            }
            tempAlltotal.put(cs2.getGroupNum(), Integer.valueOf(tempTotal));
        }
        for (int i4 = 0; i4 < cslist.size(); i4++) {
            ChooseScale cs3 = cslist.get(i4);
            cs3.setAllTotalNum((tempAlltotalNum.get(cs3.getGroupNum()).intValue() + Integer.parseInt(cs3.getThreeAll())) + "");
            cs3.setAllTotal((tempAlltotal.get(cs3.getGroupNum()).intValue() + Integer.parseInt(cs3.getThreeAll())) + "");
        }
        long xuan3 = System.currentTimeMillis();
        this.dao2.batchSave(cslist);
        this.log4j.info((System.currentTimeMillis() - xuan3) + "---xuan3----");
        return cslist;
    }

    public Map getScoreRuleList(Map<String, String> map, String userId) {
        String examPaperNum = map.get("examPaperNum");
        String groupNum = map.get("groupNum");
        Map returnMap = new HashMap();
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        returnMap.put("scoreRuleList", this.dao2._queryBeanList("SELECT qgq.groupNum,qgq.questionNum,qs.scoreRuleType,qs.questionRule,qs.questionbuchang,d.d.fullScore,qp.autoCommitForbid,qp.aotoCommitDefault from questiongroup_question qgq  LEFT JOIN question_scorerule qs  on qgq.questionNum = qs.questionNum LEFT JOIN questiongroup qp on qgq.groupNum=qp.groupNum  LEFT join( \tSELECT id,fullScore,orderNum from define d where d.examPaperNum={examPaperNum}\tUNION \tSELECT sd.id,sd.fullScore,sd.orderNum from define d left join subdefine sd on d.id=sd.pid where d.examPaperNum={examPaperNum})d on qgq.questionNum=d.id WHERE qgq.groupNum = {groupNum} ORDER BY d.orderNum", ScoreRule.class, args));
        return returnMap;
    }

    public List<Object[]> getRunningGroupListInfo() {
        return this.dao2.queryArrayList("select t.examPaperNum,t.groupNum from  (  SELECT t.examPaperNum ,t.groupNum ,testingCentreId  from task t WHERE t.insertUser='-1' and t.`status`='F' GROUP BY t.examPaperNum,t.groupNum,testingCentreId  ) t LEFT JOIN testingcentredis td  on  t.examPaperNum=td.examPaperNum and t.testingCentreId=td.testingCentreId  where td.isDis=1  GROUP BY t.examPaperNum,t.groupNum");
    }

    public void stealQuestion(String exampaperNum, String groupNum, String insertUser, String groupType, int maxStealQuNum, String isShuangPing) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        args.put("groupType", groupType);
        args.put("maxStealQuNum", Integer.valueOf(maxStealQuNum));
        args.put("isShuangPing", isShuangPing);
        this.dao2._update("call stealQuestion({exampaperNum},{groupNum},{insertUser},{groupType},{maxStealQuNum},{isShuangPing})", args);
    }

    public void updateScoreLeq(String scoreId, String questionScore, String uid) {
        Map args = new HashMap();
        args.put("questionScore", questionScore);
        args.put("uid", uid);
        args.put("scoreId", scoreId);
        this.dao2._execute("update score set questionScore = {questionScore},insertUser = {uid},insertDate=now() where id = {scoreId}", args);
    }

    public void deleteTaskUserNum3Leq(String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        this.dao2._execute("delete from task where scoreId = {scoreId} and userNum = '3'", args);
    }

    public void deleteTaskUserNum3LeqGroup(String groupNum, String scoreId, boolean flag) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("groupNum", groupNum);
        this.dao2._execute("update task set isException='W' where scoreId={scoreId} and userNum = '3' ", args);
        if (flag) {
            String studentId = this.dao2._queryStr("select studentId from score where id={scoreId} ", args);
            args.put(Const.EXPORTREPORT_studentId, studentId);
            String isExceptionStr = this.dao2._queryStr("SELECT GROUP_CONCAT(t.isException) from task t where t.groupNum ={groupNum} and t.studentId={studentId} and t.userNum=3 ", args);
            if (isExceptionStr.indexOf("F") == 1) {
                List taskList = this.dao2._queryBeanList("SELECT t.* from task t where t.groupNum ={groupNum}  and t.studentId={studentId} and t.userNum=3 ", Task.class, args);
                for (int k = 0; k < taskList.size(); k++) {
                    Task t = (Task) taskList.get(k);
                    args.put("Id", t.getId());
                    this.dao2._execute("delete from task where id={Id} ", args);
                }
            }
        }
    }

    public Integer updateTaskIsException(String scoreId, String questionNum, String type) {
        Map args = new HashMap();
        args.put("type", type);
        args.put("scoreId", scoreId);
        args.put("questionNum", questionNum);
        return Integer.valueOf(this.dao2._execute("update task set isException={type} where scoreId = {scoreId} and questionNum = {questionNum}", args));
    }

    public void deleteRemarkLeq(String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        this.dao2._execute("delete from remark where scoreId = {scoreId} and type = '1'", args);
    }

    public void deleteRemarkLeqGroup(String groupNum, String scoreId, boolean flag) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("groupNum", groupNum);
        this.dao2._execute("update remark set isException='W' where scoreId={scoreId} and type='1' ", args);
        if (flag) {
            String studentId = this.dao2._queryStr("select studentId from score where id={scoreId} ", args);
            args.put(Const.EXPORTREPORT_studentId, studentId);
            String isExceptionStr = this.dao2._queryStr("SELECT GROUP_CONCAT(r.isException) from remark r inner join task t on r.scoreId=t.scoreId where t.groupNum ={groupNum} and t.studentId={studentId} and t.userNum=1 ", args);
            if (isExceptionStr.indexOf("F") == 1) {
                List remarkList = this.dao2._queryBeanList("SELECT r.* from remark r inner join task t on r.scoreId=t.scoreId where t.groupNum ={groupNum} and t.studentId={studentId} and t.userNum=1 ", Remark.class, args);
                for (int k = 0; k < remarkList.size(); k++) {
                    Remark r = (Remark) remarkList.get(k);
                    args.put("Id", r.getId());
                    this.dao2._execute("delete from remark where id={Id} ", args);
                }
            }
        }
    }

    public void dispatcherTask(Map<String, Object> eachInsertUserQuestionGroupMap) {
        int mergeSubQuestionCount = ((Integer) eachInsertUserQuestionGroupMap.get("mergeSubQuestionCount")).intValue();
        Object examPaperNum = eachInsertUserQuestionGroupMap.get("examPaperNum");
        Object groupNum = eachInsertUserQuestionGroupMap.get("groupNum");
        Object insertUser = eachInsertUserQuestionGroupMap.get("insertUser");
        Object yingFencount = eachInsertUserQuestionGroupMap.get("yingFencount");
        Map args = new HashMap();
        args.put("exampaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        args.put("yingFencount", yingFencount);
        if (mergeSubQuestionCount < 2) {
            this.dao2._update("call task_dispatcher({exampaperNum},{groupNum},{insertUser},{yingFencount})", args);
        } else {
            this.dao2._update("call task_dispatcherxxt({exampaperNum},{groupNum},{insertUser},{yingFencount})", args);
        }
    }

    public int deleteQuestionStepScore(String id) {
        Map args = new HashMap();
        args.put("id", id);
        String count = this.dao2._queryStr("select ifNull(count(1),0) from questionstepscore where scoreId={id} ", args);
        if (!"0".equals(count)) {
            this.dao2._execute("delete from questionstepscore where scoreId={id} ", args);
            return 1;
        }
        return 1;
    }
}
