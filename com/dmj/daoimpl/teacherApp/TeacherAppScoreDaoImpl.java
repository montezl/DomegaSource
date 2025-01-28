package com.dmj.daoimpl.teacherApp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.dmj.daoimpl.awardPoint.AwardPointDaoImpl;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.Exam;
import com.dmj.domain.ExamData;
import com.dmj.domain.PersonWorkRecord;
import com.dmj.domain.ScoreData;
import com.dmj.domain.Studentlevel;
import com.dmj.domain.Task;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.Const;
import com.zht.db.DbUtils;
import com.zht.db.ServiceFactory;
import com.zht.db.TypeEnum;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;

/* loaded from: TeacherAppScoreDaoImpl.class */
public class TeacherAppScoreDaoImpl {
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());
    public static SystemService systemService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());

    public List getMarkSjt(String userId) {
        List list = new ArrayList();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                String sql = "SELECT  ex.examNum,ex.examName,ep.examPaperNum,ep.gradeNum,ba.gradeName,ep.subjectNum,sj.subjectName FROM(SELECT exampaperNum,groupNum FROM questiongroup_user WHERE (userType = '0' or userType = '1')  and userNum=" + userId + " ) g LEFT JOIN  questiongroup qg on qg.groupNum=g.groupNum LEFT JOIN  exampaper  ep  ON g.exampaperNum=ep.exampaperNum LEFT JOIN  exam       ex  ON ex.examNum=ep.examNum LEFT JOIN  subject    sj  ON sj.subjectNum=ep.subjectNum LEFT JOIN  basegrade  ba  ON ba.gradeNum=ep.gradeNum WHERE  ex.isDelete='F' and ex.status!='9' and qg.stat='1' GROUP BY g.exampaperNum ORDER BY ex.examNum,ep.gradeNum,sj.orderNum";
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall(sql);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Studentlevel sl = new Studentlevel();
                    sl.setExamNum(Integer.valueOf(Integer.parseInt(rs.getString(1))));
                    sl.setExt1(rs.getString(2));
                    sl.setExamPaperNum(Integer.valueOf(Integer.parseInt(rs.getString(3))));
                    sl.setGradeNum(Integer.valueOf(Integer.parseInt(rs.getString(4))));
                    sl.setGradeName(rs.getString(5));
                    sl.setSubjectNum(Integer.valueOf(Integer.parseInt(rs.getString(6))));
                    sl.setSubjectName(rs.getString(7));
                    list.add(sl);
                }
                DbUtils.close(rs, pstat, conn);
                return list;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public List getGroupDetail(Map<String, String> map, String userId, ServletContext context) {
        String sql;
        String examPaerNum = map.get("examPaperNum");
        map.get("groupNum");
        String orderName = map.get("orderName");
        String orderVal = map.get("orderVal");
        String enforce = systemService.fenzu(String.valueOf(examPaerNum));
        String orderStr = "";
        if (orderName != null && !"".equals(orderName)) {
            if (orderName.indexOf("schoolName") != -1) {
                if ("desc".equals(orderVal)) {
                    orderStr = " ORDER BY CONVERT(t.schoolName USING gbk) desc ";
                } else {
                    orderStr = " ORDER BY CONVERT(t.schoolName USING gbk) asc ";
                }
            } else if ("noJudge".equals(orderName)) {
                if ("desc".equals(orderVal)) {
                    orderStr = " ORDER BY t.ext5*1 desc ";
                } else {
                    orderStr = " ORDER BY t.ext5*1 asc ";
                }
            } else if ("yueJuanStatus".equals(orderName)) {
                if ("desc".equals(orderVal)) {
                    orderStr = " ORDER BY CONVERT(t.ext4 USING gbk) desc ";
                } else {
                    orderStr = " ORDER BY CONVERT(t.ext4 USING gbk) asc ";
                }
            } else if ("groupProgressWeb".equals(orderName)) {
                orderStr = "0".equals(enforce) ? " ORDER BY CONVERT(t.schoolName USING gbk),t.ext5*1 desc " : " ORDER BY t.schoolGroupNum, CONVERT(t.schoolName USING gbk),t.ext5*1 desc ";
            }
        } else {
            orderStr = " ORDER BY t.ext5*1 desc ";
        }
        Object[] qinfo = this.dao._queryArray("SELECT GROUP_CONCAT(t.id) from( \tSELECT d.id,if(d.num='s' or d.num is null,d.id,d.num) num from( \t\tSELECT d.id,d.questionNum,d.choosename num \t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id \t\t \t\twhere d.examPaperNum={examPaperNum} \t\tunion \t\t \t\tSELECT sb.id,sb.questionNum,CONCAT(d1.id,sb.orderNum) num  \t\tfrom subdefine sb left join define d  on sb.pid=d.id  \t\tLEFT JOIN define d1 on d.choosename=d1.id \t\t \t\twhere sb.examPaperNum={examPaperNum} \t\t) d  \t)t  where t.num={groupNum} GROUP BY t.num", map);
        String newGroupNum = String.valueOf(qinfo[0]);
        map.put("newGroupNum", newGroupNum);
        if ("0".equals(enforce)) {
            String sql2 = "SELECT t.* from( select qqq.*,qqq.ext33 ext3, (case when qqq.totalnojust=0 and qqq.ext33>0 then -99999 else qqq.ext33 end) as ext5 from(SELECT tt.*,convert(IFNULL(tt.totalNum,0)-IFNULL(h.d_num,0),signed) alltotalnojust from(";
            sql = (sql2 + "SELECT t.id,t.schoolNum,ifnull(t.schoolname,'-') schoolname,t.teacherNum,t.teacherName,'' schoolGroupName,t.assister,t.numOfStudent,t.totalNum,t.u_count teacherCount,  convert(IFNULL(t.totalNum,0)-IFNULL(t.d_num,0),signed) totalnojust,convert(IFNULL(t.num,(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))),signed) ext1,   convert(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0),signed) ext2 ,     convert(IFNULL(t.num-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0)),(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0))   ),signed) ext33, if(MAX(t.updateTime) is null||IFNULL(TIMESTAMPDIFF(MINUTE,MAX(t.updateTime),NOW()),4)>2,'否','是') ext4 from( \t\tselect qu.id,u.schoolNum,qu.assister,ifnull(sc.schoolname,'-')schoolname,te.teacherNum,te.teacherName,qu.userNum, \t\tq.num,q1.num num1,q1.yueJuan,quu.u_count,tu.status,y.tempcount,tu.updateTime,tu.insertUser,k.d_num, \t\tcase when s.makType=1 then IFNULL(r.count*2,0)+IFNULL(x.count,0) else IFNULL(r.count ,0)end totalNum, \t\tcase when s.makType=1 then IFNULL(st.count*2,0)+IFNULL(x.count,0) else IFNULL(st.count ,0)end\tnumOfStudent  \t\tfrom ( \t\t\tSELECT u.id,qu.userNum,qu.groupNum,qu.exampaperNum,qu.userType,slg.schoolGroupNum,slg.schoolGroupName,if(aj.assister is null,0,1) assister from questiongroup_user qu \t\t\tLEFT JOIN assistyuejuan aj on qu.groupNum=aj.groupNum and qu.userNum=aj.assister LEFT JOIN `user` u on qu.userNum=u.id LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum WHERE qu.groupNum in ({newGroupNum[]})     ) qu LEFT join user u on qu.userNum = u.id  LEFT join (  \t\t\tSELECT q.groupNum,m.makType,dd.choosename from questiongroup q \t\t\tINNER JOIN questiongroup_mark_setting m on q.groupNum=m.groupNum \t\t\tINNER JOIN ( \t\t\t\tSELECT d.id orderId,d.id ,d.questionNum groupName,choosename from define d where d.id in ({newGroupNum[]})  \t\t\t\tUNION  \t\t\t\tSELECT sd.pid orderId,sd.id,sd.questionNum groupName,d.choosename  from define d LEFT JOIN subdefine sd on sd.pid=d.id where sd.id in ({newGroupNum[]})  \t\t\t) dd on q.groupNum = dd.id \t) s on qu.groupNum = s.groupNum LEFT JOIN ( \t\t\t\tSELECT count(DISTINCT studentId) count,r.exampapernum from regexaminee r where r.examPaperNum ={examPaperNum}  and r.scan_import=0 \t ) r on qu.exampapernum=r.exampapernum left join ( \t\t\tselect s.examPaperNum ,count(en.studentId) count,qms.groupNum          from examinationnum en LEFT join exampaper s on en.examNum = s.examNum and en.subjectNum = s.subjectNum and en.gradeNum = s.gradeNum          LEFT JOIN questiongroup_mark_setting qms ON s.exampaperNum = qms.exampaperNum where s.examPaperNum ={examPaperNum}          GROUP BY s.examPaperNum,qms.groupNum \t)st on qu.exampaperNum=st.exampaperNum and qu.groupNum = st.groupNum LEFT JOIN ( \t\t\tSELECT groupNum,count(DISTINCT(questionNum)) as tempcount from questiongroup_question where groupNum in ({newGroupNum[]}) GROUP BY groupNum    \t)y ON qu.groupNum = y.groupNum LEFT JOIN (     SELECT t.groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num FROM task  t      WHERE t.groupNum in ({newGroupNum[]}) AND  t.status='T' GROUP BY t.groupNum  \t)k ON qu.groupNum = k.groupNum  LEFT JOIN (   \t\tSELECT t.groupNum,count(1) count FROM task  t WHERE t.groupNum in ({newGroupNum[]}) and t.status='F'  GROUP BY groupNum   )z ON qu.groupNum = z.groupNum LEFT JOIN ( \t\t\tSELECT groupNum,count(1) count FROM task  t  WHERE t.groupNum in ({newGroupNum[]})  and t.userNum=3  GROUP BY groupNum \t)x ON qu.groupNum = x.groupNum LEFT join ( \t\t\t\tselect q.num,q.groupNum,q.exampaperNum,q.insertUser from quota q WHERE q.groupNum in ({newGroupNum[]}) \t) q on qu.groupNum = q.groupNum and qu.userNum = q.insertUser LEFT JOIN( \t\t\t\tSELECT count(1) yueJuan,sum(q.num) num,q.groupNum FROM quota q WHERE q.groupNum in ({newGroupNum[]}) GROUP BY q.groupNum \t)q1 ON qu.groupNum = q1.groupNum left join ( \t\t\t\tselect qu.exampaperNum,qu.groupNum,count(DISTINCT(qu.userNum)) u_count from questiongroup_user qu where qu.groupNum in ({newGroupNum[]}) GROUP BY qu.exampaperNum,qu.groupNum \t) quu on qu.groupNum = quu.groupNum  \tLEFT JOIN task tu ON qu.groupNum = tu.groupNum and qu.userNum = tu.insertUser \tLEFT join school sc on u.schoolNum = sc.id  \tLEFT join teacher te on u.userId = te.id  \twhere qu.groupNum in ({newGroupNum[]}) )t  group by t.userNum  )tt LEFT JOIN ( \t\tSELECT u.groupNum,sum(d_num) d_num from( \t\t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num \t\t\tFROM task  t WHERE t.groupNum in ({newGroupNum[]}) AND  t.status='T' GROUP BY groupNum \t\t)u  )h on 1=1 ") + " )qqq )t ";
        } else {
            String sql3 = "SELECT t.* from( select qqq.*,qqq.ext33 ext3, (case when qqq.totalnojust=0 and qqq.ext33>0 then -99999 else qqq.ext33 end) as ext5 from(SELECT tt.*,convert(IFNULL(tt.alltotalNum,0)-IFNULL(h.d_num,0),signed) alltotalnojust from(";
            sql = (sql3 + "SELECT t.id,t.schoolNum,ifnull(t.schoolname,'-') schoolname,t.teacherNum,t.teacherName,t.schoolGroupNum,t.schoolGroupName,t.assister,t.numOfStudent,t.alltotalNum,t.totalNum,t.u_count teacherCount,  convert(IFNULL(t.totalNum,0)-IFNULL(t.d_num,0),signed) totalnojust,convert(IFNULL(t.num,(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))),signed) ext1,   convert(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0),signed) ext2 ,     convert(IFNULL(t.num-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0)),(t.numOfStudent-ifnull(t.num1,0))/(t.u_count-ifnull(t.yueJuan,0))-CEILING(IFNULL(SUM(IF(t.status='T',1,0))/t.tempcount,0))   ),signed) ext33, if(MAX(t.updateTime) is null||IFNULL(TIMESTAMPDIFF(MINUTE,MAX(t.updateTime),NOW()),4)>2,'否','是') ext4 from( \t\tselect qu.id,qu.userNum,qu.assister,u.schoolNum,ifnull(sc.schoolname,'-')schoolname,te.teacherNum,te.teacherName,qu.schoolGroupNum,qu.schoolGroupName, \t\tq.num,q1.num num1,q1.yueJuan,quu.u_count,tu.status,y.tempcount,tu.updateTime,tu.insertUser,k.d_num, \t\tcase when s.makType=1 then IFNULL(r1.count*2,0)+IFNULL(x1.count,0) else IFNULL(r1.count ,0)end alltotalNum, \t \tcase when s.makType=1 then IFNULL(r.count*2,0)+IFNULL(x.count,0) else IFNULL(r.count ,0)end totalNum,\t\tcase when s.makType=1 then IFNULL(st.count*2,0)+IFNULL(x.count,0) else IFNULL(st.count ,0)end numOfStudent \t \tfrom ( \t\t\tSELECT u.id,qu.userNum,qu.groupNum,qu.exampaperNum,qu.userType,slg.schoolGroupNum,slg.schoolGroupName,if(aj.assister is null,0,1) assister from questiongroup_user qu  \t\t\tLEFT JOIN assistyuejuan aj on qu.groupNum=aj.groupNum and qu.userNum=aj.assister LEFT JOIN `user` u on qu.userNum=u.id LEFT JOIN schoolgroup slg on u.schoolNum=slg.schoolNum WHERE qu.groupNum in ({newGroupNum[]}) \t \t) qu LEFT join user u on qu.userNum = u.id LEFT join ( \t\t\t\tSELECT q.groupNum,m.makType,dd.choosename from questiongroup q \t\t\t\tINNER JOIN questiongroup_mark_setting m on q.groupNum=m.groupNum \t\t\t\tINNER JOIN ( \t\t\t\t\tSELECT d.id orderId,d.id ,d.questionNum groupName,choosename from define d where d.id in ({newGroupNum[]})  \t\t\t\t\tUNION \t\t\t\t\tSELECT sd.pid orderId,sd.id,sd.questionNum groupName,d.choosename  from define d LEFT JOIN subdefine sd on sd.pid=d.id where sd.id in ({newGroupNum[]})  \t\t\t\t) dd on q.groupNum = dd.id    \t) s on qu.groupNum = s.groupNum LEFT JOIN (           select d.id,r.ext1 examPaperNum,r.schoolGroupNum,r.count from(  \t\t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (s1.xuankaoqufen=2 or s1.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d   \t\t\t\tleft join exampaper s on d.exampaperNum = s.exampaperNum left join exampaper s1 on d.category = s1.exampaperNum where d.examPaperNum={examPaperNum}   \t\t\t\tUNION  \t\t\t\tSELECT sd.id,sd.examPaperNum,CASE WHEN (s1.xuankaoqufen=2 or s1.xuankaoqufen=3) then sd.category else sd.examPaperNum end category from subdefine sd   \t\t\t\tleft join exampaper s on sd.exampaperNum = s.exampaperNum left join exampaper s1 on sd.category = s1.exampaperNum where sd.examPaperNum={examPaperNum}   \t\t\t)d LEFT JOIN(  \t\t\t\tSELECT slg.schoolGroupNum,sss.ext1,count(1) count from(  \t\t\t\t\tSELECT DISTINCT r.studentId,r.schoolNum,r.exampapernum ext1 from regexaminee r where r.examPaperNum={examPaperNum} and r.scan_import=0  \t\t\t\t\tunion  \t\t\t\t\tSELECT r.studentId,r.schoolNum,e.examPaperNum ext1 FROM(  \t\t\t\t\t\tSELECT DISTINCT r.studentId,r.schoolNum,r.exampapernum from regexaminee r where r.examPaperNum={examPaperNum} and r.scan_import=0  \t\t\t\t\t)r INNER JOIN student s on r.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid   \t\t\t\t\tINNER JOIN exampaper e on ls.subjectNum=e.subjectNum and r.exampaperNum=e.pexampaperNum  \t\t\t)sss INNER JOIN schoolgroup slg on sss.schoolNum=slg.schoolNum GROUP BY sss.ext1,slg.schoolGroupNum  \t\t\t)r on d.category=r.ext1    ) r on qu.schoolGroupNum=r.schoolGroupNum and qu.groupNum=r.id  LEFT JOIN (  \t\tselect d.id,count(DISTINCT r.studentId) count from(  \t\t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (s1.xuankaoqufen=2 or s1.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d   \t\t\t\tleft join exampaper s on d.exampaperNum = s.exampaperNum left join exampaper s1 on d.category = s1.exampaperNum where d.examPaperNum = {examPaperNum}   \t\t\t\tUNION  \t\t\t\tSELECT sd.id,sd.examPaperNum,CASE WHEN (s1.xuankaoqufen=2 or s1.xuankaoqufen=3) then sd.category else sd.examPaperNum end category from subdefine sd   \t\t\t\tleft join exampaper s on sd.exampaperNum = s.exampaperNum left join exampaper s1 on sd.category = s1.exampaperNum where sd.examPaperNum ={examPaperNum}   \t\t)d LEFT JOIN(   \t\t\tSELECT DISTINCT r.studentId,r.exampapernum ext1 from regexaminee r WHERE r.examPaperNum ={examPaperNum}  and r.scan_import=0   \t\t\tunion   \t\t\tSELECT r.studentId,e.examPaperNum ext1 FROM(   \t\t\t\tSELECT DISTINCT r.studentId,r.exampapernum from regexaminee r WHERE r.examPaperNum ={examPaperNum}  and r.scan_import=0   \t\t\t)r INNER JOIN student s on r.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid    \t\t\tINNER JOIN exampaper e on ls.subjectNum=e.subjectNum and r.exampaperNum=e.pexampaperNum   \t\t)r on d.category=r.ext1 GROUP BY d.id   ) r1 on qu.groupNum=r1.id LEFT JOIN (    \t\tselect r.ext1 exampapernum,r.schoolGroupNum,d.id,r.count from( \t\t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (s1.xuankaoqufen=2 or s1.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d  \t\t\t\tleft join exampaper s on d.exampaperNum = s.exampaperNum  \t\t\t\tleft join exampaper s1 on d.category = s1.exampaperNum where d.id in ({newGroupNum[]}) \t\t\t\tUNION \t\t\t\tSELECT sd.id,sd.examPaperNum,CASE WHEN (s1.xuankaoqufen=2 or s1.xuankaoqufen=3) then sd.category else sd.examPaperNum end category from subdefine sd  \t\t\t\tleft join exampaper s on sd.exampaperNum = s.exampaperNum left join exampaper s1 on sd.category = s1.exampaperNum where sd.id in ({newGroupNum[]}) \t\t\t)d LEFT JOIN( \t\t\t\t\tSELECT sss.examNum,sss.gradeNum,sss.subjectNum,slg.schoolGroupNum,sss.ext1,count(1) count from( \t\t\t\t\t\t\tSELECT n.examNum,n.gradeNum,n.subjectNum,n.studentId,n.schoolNum,s.examPaperNum ext1 from examinationnum n \t\t\t\t\t\t\tINNER join exampaper s on n.examNum=s.examNum and n.gradeNum=s.gradeNum and n.subjectNum=s.subjectNum where s.examPaperNum ={examPaperNum} \t\t\t\t\t\t\tunion \t\t\t\t\t\t\tSELECT r.examNum,r.gradeNum,r.subjectNum,r.studentId,r.schoolNum,e.examPaperNum ext1 FROM( \t\t\t\t\t\t\t\tSELECT n.examNum,n.gradeNum,n.subjectNum,n.studentId,n.schoolNum,s.examPaperNum from examinationnum n  \t\t\t\t\t\t\t\tINNER join exampaper s on n.examNum=s.examNum and n.gradeNum=s.gradeNum and n.subjectNum=s.subjectNum where s.examPaperNum ={examPaperNum} \t\t\t\t\t\t\t)r INNER JOIN student s on r.studentId=s.id INNER JOIN levelstudent ls on s.id=ls.sid  \t\t\t\t\t\t\tINNER JOIN exampaper e on ls.subjectNum=e.subjectNum and r.examPaperNum=e.pexampaperNum \t\t\t\t\t)sss INNER JOIN schoolgroup slg on sss.schoolNum=slg.schoolNum GROUP BY sss.ext1,slg.schoolGroupNum \t\t\t)r on d.category=r.ext1   ) st on qu.schoolGroupNum=st.schoolGroupNum and qu.groupNum=st.id LEFT JOIN (   \t\tSELECT groupNum,count(DISTINCT(questionNum)) as tempcount from questiongroup_question where groupNum in ({newGroupNum[]}) GROUP BY groupNum   )y ON qu.groupNum = y.groupNum LEFT JOIN (   \t\tSELECT t.groupNum,slg.schoolGroupNum,count(1) count FROM task  t  LEFT JOIN student stu on t.studentId=stu.id INNER JOIN schoolgroup slg on stu.schoolNum=slg.schoolNum   \t\t\tWHERE t.groupNum in ({newGroupNum[]}) and t.status='F'  GROUP BY slg.schoolGroupNum   )z ON qu.groupNum = z.groupNum and qu.schoolGroupNum=z.schoolGroupNum LEFT JOIN(     SELECT t.groupNum,slg.schoolGroupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num     FROM task  t  LEFT JOIN student stu on t.studentId=stu.id INNER JOIN schoolgroup slg on stu.schoolNum=slg.schoolNum     WHERE t.groupNum in ({newGroupNum[]}) AND  t.status='T' GROUP BY t.groupNum,slg.schoolGroupNum  \t)k ON qu.groupNum = k.groupNum and qu.schoolGroupNum=k.schoolGroupNum   LEFT JOIN (    \t\t\tSELECT t.groupNum,count(1) count FROM task  t  LEFT JOIN student stu on t.studentId=stu.id  \t\t\t\tWHERE t.groupNum in ({newGroupNum[]}) and t.userNum=3  GROUP BY t.groupNum   )x1 ON qu.groupNum = x1.groupNum  LEFT JOIN (   \t\t\tSELECT t.groupNum,slg.schoolGroupNum,count(1) count FROM task  t  LEFT JOIN student stu on t.studentId=stu.id INNER JOIN schoolgroup slg on stu.schoolNum=slg.schoolNum  \t\t\t\tWHERE t.groupNum in ({newGroupNum[]}) and t.userNum=3  GROUP BY t.groupNum,slg.schoolGroupNum \t)x ON qu.groupNum = x.groupNum and qu.schoolGroupNum=x.schoolGroupNum LEFT join (    \t\t\t\tselect q.num,q.groupNum,q.insertUser from quota q WHERE q.groupNum in ({newGroupNum[]})  \t) q on qu.groupNum = q.groupNum and qu.userNum = q.insertUser LEFT JOIN(    \t\t\tSELECT sum(q.yuejuan) yuejuan,sl.schoolgroupNum,sum(q.num) num,q.groupNum FROM schoolgroup sl  \t\t\t\tINNER JOIN (  \t\t\t\t\tSELECT count(1) yueJuan,u.schoolNum,sum(qou.num) num,qou.groupNum from quota qou  \t\t\t\t\tINNER JOIN user u on qou.insertUser=u.id and u.userType=1 \t\t\t\t\tWHERE qou.groupNum in ({newGroupNum[]}) GROUP BY qou.groupNum,u.schoolNum  \t\t\t\t)q on sl.schoolNum=q.schoolNum GROUP BY q.groupNum,sl.schoolgroupNum \t)q1 ON qu.groupNum=q1.groupNum and qu.schoolgroupNum = q1.schoolgroupNum left join (   \t\t\tSELECT qu.exampaperNum,qu.groupNum,sl.schoolgroupNum,sum(qu.count) u_count FROM schoolgroup sl \t\t\t\tINNER JOIN ( \t\t\t\t\tselect qu.exampaperNum,qu.groupNum,u.schoolNum,count(1) count from questiongroup_user qu \t\t\t\t\tINNER JOIN user u on qu.userNum=u.id and u.userType=1  \t\t\t\t\tWHERE qu.groupNum in ({newGroupNum[]}) GROUP BY qu.groupNum,u.schoolNum \t\t\t\t)qu on sl.schoolNum=qu.schoolNum GROUP BY qu.groupNum,sl.schoolgroupNum \t) quu on qu.groupNum=quu.groupNum and qu.schoolgroupNum = quu.schoolgroupNum    \t\tLEFT JOIN task tu ON qu.groupNum = tu.groupNum and qu.userNum = tu.insertUser  \t\tLEFT join school sc on u.schoolNum = sc.id     \t\tLEFT join teacher te on u.userId = te.id    \twhere qu.groupNum in ({newGroupNum[]}) and qu.userType <> 2  )t  group by t.userNum )tt LEFT JOIN ( \t\tSELECT u.groupNum,sum(d_num) d_num from( \t\t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num \t\t\tFROM task  t WHERE t.groupNum in ({newGroupNum[]}) AND  t.status='T' GROUP BY groupNum \t\t)u  )h on 1=1 ") + " )qqq )t ";
        }
        String sql22 = "SELECT t.*,IFNULL(pan.exampaperyipan,0) exampaperyipan from ( " + sql + ") t LEFT JOIN ( SELECT count(1) exampaperyipan,updateUser from task WHERE  exampaperNum={examPaperNum} and  `Status`='T' GROUP BY updateUser) pan ON t.id=pan.updateUser " + orderStr;
        List list = this.dao._queryBeanList(sql22, Task.class, map);
        Map<String, Integer> schMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Task t = (Task) list.get(i);
            String schoolNum = t.getSchoolNum();
            Integer weipanshu = Integer.valueOf(Integer.parseInt(t.getExt33()));
            if (schMap.containsKey(schoolNum)) {
                weipanshu = Integer.valueOf(weipanshu.intValue() + Integer.parseInt(String.valueOf(schMap.get(schoolNum))));
            }
            schMap.put(schoolNum, weipanshu);
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            Task t2 = (Task) list.get(i2);
            t2.setSchweipan(String.valueOf(schMap.get(t2.getSchoolNum())));
        }
        for (int i3 = 0; i3 < list.size(); i3++) {
            Task t3 = (Task) list.get(i3);
            Integer totalnojust = Integer.valueOf(Integer.parseInt(t3.getTotalnojust()));
            Integer schweipan = Integer.valueOf(Integer.parseInt(t3.getSchweipan()));
            Integer yudingliang = Integer.valueOf(Integer.parseInt(t3.getExt1()));
            Integer weipanshu2 = Integer.valueOf(Integer.parseInt(t3.getExt33()));
            String assister = t3.getAssister();
            if ((totalnojust.intValue() == 0 || schweipan.intValue() == 0) && weipanshu2.intValue() > 0 && "1".equals(enforce)) {
                t3.setExt3("----");
            }
            if (yudingliang.intValue() < 0) {
                t3.setExt1("0");
                t3.setExt3("0");
            }
            if ("1".equals(assister)) {
                t3.setExt1("----");
                t3.setExt3("----");
            }
        }
        for (int i4 = 0; i4 < list.size(); i4++) {
            Task t4 = (Task) list.get(i4);
            Integer totalnojust2 = Integer.valueOf(Integer.parseInt(t4.getTotalnojust()));
            Integer schweipan2 = Integer.valueOf(Integer.parseInt(t4.getSchweipan()));
            if (totalnojust2.intValue() == 0 && schweipan2.intValue() > 0 && "1".equals(enforce)) {
                t4.setSchweipan("----");
            }
        }
        return list;
    }

    public List getGroupProgress(Map<String, String> map, String userId, ServletContext context) {
        map.get("examPaperNum");
        String orderName = map.get("orderName");
        map.get("orderVal");
        String orderStr = " ORDER BY tt.notJudge desc,tt.a,tt.b,tt.c ";
        if (orderName != null && !"".equals(orderName)) {
            if ("jp_groupNum".equals(orderName)) {
                orderStr = " ORDER BY tt.a,tt.b,tt.c ";
            } else if ("jp_notJudge".equals(orderName)) {
                orderStr = " ORDER BY tt.notJudge desc,tt.a,tt.b,tt.c ";
            }
        }
        String sql = "SELECT tt.groupNum,tt.groupName,tt.groupType,tt.makType,tt.judgetype,tt.examPaperNum,tt.totalNum,tt.ext10,tt.questionname,tt.hasJudge,cast(notJudge as signed) notJudge,tt.progress from(select t.*,IFNULL(k.d_num ,0) hasJudge,t.totalNum-IFNULL(k.d_num ,0) notJudge,convert((IFNULL(k.d_num ,0)/t.totalNum)*100,decimal(10,2)) progress,k.a,k.b,k.c  from(  \tSELECT if(k.name is null,q.groupNum,k.num) groupNum,if(k.name is null,q.groupName,k.name) groupName,q.groupType,z.makType,z.judgetype,z.exampaperNum,  \t\tcase        \t\twhen z.makType=1 then IFNULL(r.count*2,0)+IFNULL(x.count,0)      \t\twhen z.makType=0 then IFNULL(r.count,0)     end totalNum,d.choosename ext10,  \t\tIF(t.groupNum is NULL ,'T','F') questionName FROM(  \t\t\t\tSELECT s.groupNum,s.exampaperNum,m.makType,m.judgetype FROM(  \t\t\t\t\tSELECT groupNum,exampaperNum FROM questiongroup WHERE exampaperNum={examPaperNum}\t\t  \t\t\t\t) s  LEFT JOIN (  \t\t\t\t\tSELECT groupNum,makType,judgetype FROM questiongroup_mark_setting WHERE exampaperNum={examPaperNum} \t  \t\t\t\t) m ON m.groupNum=s.groupNum  \t\t)z  LEFT JOIN (  \t\t\t\tSELECT groupNum,count(DISTINCT studentId) count FROM task   WHERE exampaperNum={examPaperNum}  and userNum=3  GROUP BY groupNum    \t\t)x ON z.groupNum = x.groupNum  LEFT JOIN questiongroup q ON q.groupNum=z.groupNum     \t\tLEFT JOIN (  \t\t\tSELECT id,choosename from define WHERE exampaperNum={examPaperNum}    \t\t\tUNION       \t\t\tSELECT sb.id,d.choosename from define d  LEFT JOIN subdefine sb on sb.pid=d.id  WHERE d.exampaperNum={examPaperNum}  \t\t) d on q.groupNum=d.id    LEFT JOIN (  \t\t\tselect d.id,r.dd count from(  \t\t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d   \t\t\t\tINNER JOIN exampaper e on d.category=e.examPaperNum where d.examPaperNum={examPaperNum}  and d.questionType=1  \t\t\t  \t\t\t\tUNION \t\t\t  \t\t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from subdefine d   \t\t\t\tINNER JOIN exampaper e on d.category=e.examPaperNum where d.examPaperNum={examPaperNum} and d.questionType=1 \t\t  \t\t\t)d LEFT JOIN(  \t\t\t\tSELECT count(DISTINCT studentId) dd,exampapernum ext1 from regexaminee WHERE exampaperNum={examPaperNum} and scan_import=0 \t\t  \t\t\t\tunion \t\t\t  \t\t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampapernum from regexaminee r  \t\t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   \t\t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE r.exampaperNum={examPaperNum}  and r.scan_import=0   \t\t\t\tUNION\t \t\t\t  \t\t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampaperNum from regexaminee r LEFT JOIN(  \t\t\t\t\tSELECT r.studentId,r.exampapernum from regexaminee r \t\t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \t\t\t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE r.exampaperNum={examPaperNum}   and r.scan_import=0 \t\t\t  \t\t\t)r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum \t\t\tINNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3  \t\t\tWHERE r.exampaperNum={examPaperNum}  and r.scan_import=0 and r1.studentId is null \t\t)r on d.category=r.ext1    \t\t) r on q.groupNum=r.id left JOIN (  \t\t\t\t\tSELECT d.id,d.questionNum,if(d.num='s' or d.num is null,d.id,d.num) num,d1.name from(  \t\t\t\t\t\t\t\tSELECT d.id,d.questionNum,d.choosename num from define d  \t\t\t\t\t\t\t\tWHERE d.examPaperNum={examPaperNum}  and d.questiontype=1  \t\t\t\t\t\t\t\tunion  \t\t\t\t\t\t\t\tSELECT sb.id,sb.questionNum,CONCAT(d1.id,sb.orderNum) num  \t\t\t\t\t\t\t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id  \t\t\t\t\t\t\t\tWHERE sb.examPaperNum={examPaperNum}  and sb.questiontype=1  \t\t\t\t\t)d LEFT JOIN(  \t\t\t\t\t\t\tSELECT choosename num,GROUP_CONCAT(questionNum separator '-') name from define d  \t\t\t\t\t\t\tWHERE d.examPaperNum={examPaperNum} and LENGTH(choosename)>1  \t\t\t\t\t\t\tand (isParent=0 or `merge`=1) and questiontype=1 GROUP BY choosename  \t\t\t\t\t\t\tunion  \t\t\t\t\t\t\tSELECT d.num,CONCAT(GROUP_CONCAT(questionNum separator '-'),'_',d.orderNum) name from(  \t\t\t\t\t\t\t\tSELECT sb.id,sb.orderNum,sb.questionNum yy,d.questionNum,CONCAT(d1.id,sb.orderNum) num  \t\t\t\t\t\t\t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id   \t\t\t\t\t\t\t\tWHERE sb.examPaperNum={examPaperNum} and LENGTH(d.choosename)>1 and d.`merge`=0 and sb.questiontype=1  \t\t\t\t\t\t\t)d GROUP BY d.num  \t\t\t\t\t)d1 on d.num=d1.num  \t\t\t)k on z.groupNum=k.id  LEFT JOIN (  \t\t\t\tselect  DISTINCT q.groupNum FROM (  \t\t\t\t\tSELECT groupNum,questionNum FROM questiongroup_question WHERE exampaperNum ={examPaperNum}  GROUP BY groupNum     \t\t\t\t)q LEFT JOIN (  \t\t\t\t\tselect DISTINCT questionNum  FROM remark where type='1' AND STATUS='F' AND exampaperNum={examPaperNum}  \t\t\t\t) tt ON q.questionNum = tt.questionNum where tt.questionNum is not null    \t\t)t  ON z.groupNum = t.groupNum WHERE z.groupNum IS NOT NULL   \t\tgroup by k.num  \t)t LEFT JOIN(  \tSELECT d.num,sum(t.d_num) d_num,d.a,d.b,d.c from(  \t\tSELECT d.id,d.questionNum,d.a,d.b,d.c,if(d.num='s' or d.num is null,d.id,d.num) num from(  \t\t\t\t\tSELECT * from(  \t\t\t\t\t\tSELECT d.id,d.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,d.orderNum b,0 c,d.choosename num  \t\t\t\t\t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id  \t\t\t\t\t\tWHERE d.examPaperNum={examPaperNum} and d.questiontype=1  \t\t\t\t\t\tunion  \t\t\t\t\t\tSELECT sb.id,sb.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,sb.orderNum b, d.orderNum c,CONCAT(d1.id,sb.orderNum) num  \t\t\t\t\t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id  \t\t\t\t\t\tWHERE sb.examPaperNum={examPaperNum} and sb.questiontype=1  \t\t\t\t\t) d  \t\t\t)d  \t)d LEFT JOIN(  \t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num  \t\tFROM task  t   \t\tWHERE t.examPaperNum={examPaperNum} AND  t.status='T'   GROUP BY groupNum  \t)\tt on d.id=t.groupNum GROUP BY d.num  )k on t.groupNum=k.num ";
        List returnList = this.dao._queryBeanList(sql + ")tt " + orderStr, ExamData.class, map);
        List<Map<String, Object>> maps = this.dao._queryMapList("select a.examPaperNum,a.groupNum,sum(if(`status`='F',1,0))/queNum weicaijue  from remark r inner join ( select q.examPaperNum,q.questionNum,groupNum,count(groupNum)queNum from  questiongroup_question  q left join define d on q.questionNum=d.id where  q.examPaperNum={examPaperNum} group by groupNum )a on r.examPaperNum=a.exampaperNum and r.groupNum=a.groupNum where r.type='1' GROUP BY a.groupNum ", TypeEnum.StringObject, map);
        for (int i = 0; i < returnList.size(); i++) {
            ExamData examData = (ExamData) returnList.get(i);
            examData.setGroupName("T" + examData.getGroupName());
            for (int j = 0; j < maps.size(); j++) {
                String groupNum = examData.getGroupNum();
                String groupNum2 = maps.get(j).get("groupNum").toString();
                if (groupNum.equals(groupNum2)) {
                    examData.setWeiCaijue(Convert.toInt(maps.get(j).get("weicaijue")));
                }
            }
        }
        return returnList;
    }

    public List getSubjectProgress(String userId, ServletContext context, String type) {
        String sql;
        Map<String, Map<String, Object>> yuejuanProgressMap = (Map) context.getAttribute(Const.yuejuanProgressMap);
        List<ScoreData> list = new ArrayList<>();
        Map args = new HashMap();
        args.put("userId", userId);
        if ("my".equals(type)) {
            sql = "SELECT DISTINCT e.examNum,e.examName from questiongroup_user qu  INNER JOIN questiongroup qp on qu.groupNum=qp.groupNum INNER JOIN user u on qu.userNum=u.id LEFT JOIN exampaper ep on qu.examPaperNum=ep.examPaperNum INNER JOIN exam e on ep.examNum=e.examNum where qp.stat=1 and u.id={userId} and e.status <>9 and e.isDelete='F' ORDER BY e.examDate desc ";
        } else {
            sql = "select examNum,examName from exam where status <>9 and isDelete='F' ORDER BY examDate desc";
            Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
            if (!"-1".equals(userId) && !"-2".equals(userId) && null == map) {
                sql = "select DISTINCT e.examNum,e.examName from exam e LEFT JOIN examschool es ON e.examNum=es.examNum  right JOIN (select schoolNum from schoolscanpermission where userNum={userId} union select schoolNum from user where id={userId}) scm ON CAST(es.schoolNum as char)=CAST(scm.schoolNum  as char)  where e.status <>9 and e.isDelete='F' ORDER BY e.examDate desc";
            }
        }
        List examList = this.dao._queryBeanList(sql, Exam.class, args);
        for (int h = 0; h < examList.size(); h++) {
            ScoreData scData = new ScoreData();
            Exam e = (Exam) examList.get(h);
            String examNum = e.getExamNum() + "";
            String examName = e.getExamName();
            String sql2 = "SELECT e.*,IFNULL(q.scancompleted,0) scancompleted,IFNULL(t.openDis,0) openDis,IFNULL(t1.totalDis,0) totalDis,IFNULL(s.weicaiqie,0) weicaiqie,IF(IFNULL(qu.count2,0)=0,'F','T') yuejuanStatus, case when IF(IFNULL(qms.count2,0)=0,'F','T')='T' then IF(IFNULL(qu1.count2,0)=0,'F','T') else 'F' end caijueStatus,  if(r.count3=0,'F','T') auth,q.stat kaifangYuejuan from(  \tSELECT DISTINCT e.examPaperNum,e.gradeNum,bg.gradeName,e.subjectNum,s.subjectName,s.orderNum from(  \t\tSELECT examPaperNum,examNum,gradeNum,subjectNum FROM exampaper  \t\tWHERE examNum={examNum}  AND isHidden='F' AND type = '0'  \t) e LEFT JOIN `subject` s on e.subjectNum = s.subjectNum  \tLEFT JOIN basegrade bg on e.gradeNum=bg.gradeNum  )e LEFT JOIN (  \tSELECT q.exampaperNum,max(scancompleted) scancompleted,max(stat) stat FROM questiongroup q left join exampaper e on q.exampaperNum=e.exampaperNum where e.examNum={examNum} GROUP BY exampaperNum  ) q on e.examPaperNum=q.examPaperNum LEFT JOIN(  \tSELECT t.examPaperNum,count(1) openDis from testingcentredis t INNER JOIN testingcentre t1 on t.testingCentreId=t1.id LEFT JOIN exampaper e on t.examPapernum=e.examPaperNum  \twhere e.examNum={examNum} and t.isDis=1 GROUP BY t.examPaperNum  )t on e.examPaperNum=t.exampapernum LEFT JOIN(  \tSELECT t.examPaperNum,count(1) totalDis from testingcentredis t INNER JOIN testingcentre t1 on t.testingCentreId=t1.id LEFT JOIN exampaper e on t.examPapernum=e.examPaperNum  \twhere e.examNum={examNum}  GROUP BY t.examPaperNum  )t1 on e.examPaperNum=t1.exampapernum LEFT JOIN (  \tSELECT c.exampaperNum,count(1) weicaiqie FROM cantrecognized c left join exampaper e on c.exampaperNum=e.exampaperNum where e.examNum={examNum} GROUP BY exampaperNum  ) s on e.examPaperNum=s.examPaperNum LEFT JOIN (  \tSELECT q.exampaperNum,count(1) count2 from questiongroup_user q LEFT JOIN exampaper e on q.exampaperNum=e.examPaperNum  \twhere e.examNum={examNum} and q.userType<>2 and q.userNum={userId}   GROUP BY q.exampaperNum  ) qu on e.examPaperNum=qu.examPaperNum LEFT JOIN (    SELECT qms.exampaperNum,count(1) count2 from questiongroup_mark_setting qms left JOIN exampaper ep on qms.exampaperNum=ep.examPaperNum    left JOIN exam e on ep.examNum=e.examNum where e.examNum={examNum} and qms.judgetype<>1 GROUP BY qms.exampaperNum ) qms on e.examPaperNum=qms.examPaperNum LEFT JOIN (    SELECT q.exampaperNum,count(1) count2 from questiongroup_user q LEFT JOIN exampaper e on q.exampaperNum=e.examPaperNum    where e.examNum={examNum} and q.userType<>0 and q.userNum={userId}  GROUP BY q.exampaperNum   ) qu1 on e.examPaperNum=qu1.examPaperNum LEFT JOIN (  \tSELECT count(1) count3 from userrole ur LEFT JOIN resourcerole rr on ur.roleNum=rr.roleNum LEFT JOIN resource r on rr.resource=r.num  \twhere ur.userNum={userId} and r.num in(3602,3607)  ) r on 1=1 ";
            if ("my".equals(type)) {
                sql2 = sql2 + " where q.stat=1 ";
            }
            Map argss = new HashMap();
            argss.put(Const.EXPORTREPORT_examNum, examNum);
            argss.put("userId", userId);
            List returnList = this.dao._queryBeanList(sql2 + " ORDER BY e.gradeNum asc,e.orderNum asc ", ExamData.class, argss);
            this.dao._queryBeanList("select c.examPaperNum,yicaijue,weicaijue,allcaijue from  ( SELECT r.exampaperNum examPaperNum FROM remark r LEFT JOIN exampaper e ON r.exampaperNum = e.examPaperNum WHERE e.examNum ={examNum} AND r.type = '1' and groupNum is not null group by groupNum ) c  left join  ( select examPaperNum,sum(yipan) yicaijue,sum(weipan)weicaijue,sum(groupNums)allcaijue from( select r.examPaperNum,r.groupNum,sum(if(status='T',1,0))/queNum yipan,sum(if(status='F',1,0))/queNum weipan,count(r.groupNum)/queNum groupNums from remark r  left join ( select q.examPaperNum,q.groupNum,count(groupNum)queNum from questiongroup_question q left join examPaper ep on q.examPaperNum=ep.examPaperNum left join define d on q.questionNum=d.id where ep.examNum={examNum} group by q.groupNum )a on  r.groupNum=a.groupNum where r.type=1  and r.groupNum is not null group by r.examPaperNum,r.groupNum order by examPaperNum ) a GROUP BY examPaperNum ) b on c.examPaperNum=b.examPaperNum ", ExamData.class, argss);
            List<Map<String, Object>> maps = this.dao._queryMapList("select c.examPaperNum,yicaijue,weicaijue,allcaijue from  ( SELECT r.exampaperNum examPaperNum FROM remark r LEFT JOIN exampaper e ON r.exampaperNum = e.examPaperNum WHERE e.examNum ={examNum} AND r.type = '1' and groupNum is not null group by groupNum ) c  left join  ( select examPaperNum,sum(yipan) yicaijue,sum(weipan)weicaijue,sum(groupNums)allcaijue from( select r.examPaperNum,r.groupNum,sum(if(status='T',1,0))/queNum yipan,sum(if(status='F',1,0))/queNum weipan,count(r.groupNum)/queNum groupNums from remark r  left join ( select q.examPaperNum,q.groupNum,count(groupNum)queNum from questiongroup_question q left join examPaper ep on q.examPaperNum=ep.examPaperNum left join define d on q.questionNum=d.id where ep.examNum={examNum} group by q.groupNum )a on  r.groupNum=a.groupNum where r.type=1  and r.groupNum is not null group by r.examPaperNum,r.groupNum order by examPaperNum ) a GROUP BY examPaperNum ) b on c.examPaperNum=b.examPaperNum ", TypeEnum.StringObject, argss);
            for (int i = 0; i < returnList.size(); i++) {
                ExamData exData = (ExamData) returnList.get(i);
                String examPaperNum = exData.getExamPaperNum();
                String progress = "0";
                String ext3 = "0";
                String ext4 = "0";
                if (CollUtil.isNotEmpty(yuejuanProgressMap)) {
                    Map<String, Object> oneSubjectResDataMap = yuejuanProgressMap.get(examPaperNum);
                    if (CollUtil.isNotEmpty(oneSubjectResDataMap)) {
                        Map<String, Object> kemuResDataMap = (Map) oneSubjectResDataMap.get("subjectProgress");
                        if (CollUtil.isNotEmpty(kemuResDataMap)) {
                            progress = Convert.toStr(kemuResDataMap.get("baifenbi")) + "%";
                            ext3 = Convert.toStr(kemuResDataMap.get("totalCount"));
                            ext4 = Convert.toStr(kemuResDataMap.get("yipanCount"));
                        }
                    }
                }
                exData.setProgress(progress);
                exData.setExt3(ext3);
                exData.setExt4(ext4);
                if (maps.size() != 0) {
                    for (int j = 0; j < maps.size(); j++) {
                        if (exData.getExamPaperNum().equals(maps.get(j).get("examPaperNum").toString())) {
                            exData.setAllCaijue(Convert.toInt(maps.get(j).get("allcaijue")));
                            exData.setWeiCaijue(Convert.toInt(maps.get(j).get("weiCaijue")));
                        }
                    }
                } else {
                    exData.setAllCaijue(0);
                    exData.setWeiCaijue(0);
                }
            }
            scData.setExamName(examName);
            scData.setExamNum(examNum);
            scData.setList(returnList);
            list.add(scData);
            argss.clear();
        }
        return list;
    }

    public List getRemarkProgress(String userId, ServletContext context) {
        List<ScoreData> list = new ArrayList<>();
        Map args = new HashMap();
        args.put("userId", userId);
        List examList = this.dao._queryBeanList("SELECT DISTINCT e.examNum,e.examName from questiongroup_user q LEFT JOIN exampaper ep on q.exampaperNum=ep.examPaperNum INNER JOIN remark r on q.examPaperNum=r.examPaperNum INNER JOIN exam e on ep.examNum=e.examNum   where q.userType<>0 and q.userNum={userId} and e.status<>9 and e.isDelete='F'  ORDER BY examDate desc ", Exam.class, args);
        for (int h = 0; h < examList.size(); h++) {
            ScoreData scData = new ScoreData();
            Exam e = (Exam) examList.get(h);
            String examNum = e.getExamNum() + "";
            String examName = e.getExamName();
            Map argss = new HashMap();
            argss.put(Const.EXPORTREPORT_examNum, examNum);
            argss.put("userId", userId);
            Map<String, Object> map = this.dao._querySimpleMap("select c.examPaperNum,yicaijue,weicaijue,allcaijue from   (  SELECT r.exampaperNum examPaperNum FROM remark r  LEFT JOIN exampaper e ON r.exampaperNum = e.examPaperNum  WHERE e.examNum ={examNum} AND r.type = '1' and groupNum is not null group by groupNum  ) c   left join   (  select examPaperNum,sum(yipan) yicaijue,sum(weipan)weicaijue,sum(groupNums)allcaijue from(  select r.examPaperNum,r.groupNum,sum(if(status='T',1,0))/queNum yipan,sum(if(status='F',1,0))/queNum weipan,count(r.groupNum)/queNum groupNums from remark r   left join (  select q.examPaperNum,q.groupNum,count(groupNum)queNum from questiongroup_question q  left join examPaper ep on q.examPaperNum=ep.examPaperNum  left join define d on q.questionNum=d.id  where ep.examNum={examNum} group by q.groupNum  )a on  r.groupNum=a.groupNum  where r.type=1  and r.groupNum is not null  group by r.examPaperNum,r.groupNum order by examPaperNum  ) a GROUP BY examPaperNum  ) b on c.examPaperNum=b.examPaperNum ", argss);
            Integer allCaijue = 0;
            Integer yiCaijue = 0;
            if (map != null) {
                allCaijue = Convert.toInt(map.get("allcaijue"));
                yiCaijue = Convert.toInt(map.get("yicaijue"));
            }
            List returnList = this.dao._queryBeanList("SELECT e.*,IFNULL(q.scancompleted,0) scancompleted,IFNULL(t.openDis,0) openDis,IFNULL(t1.totalDis,0) totalDis,IFNULL(s.weicaiqie,0) weicaiqie,r.yicaiqie ext4,r.caiqie ext3,r.progress from(    SELECT DISTINCT e.examPaperNum,e.gradeNum,bg.gradeName,e.subjectNum,s.subjectName,s.orderNum from(      SELECT qu.examPaperNum,e.examNum,e.gradeNum,e.subjectNum from questiongroup_user qu LEFT JOIN exampaper e on qu.examPaperNum=e.examPaperNum     where e.examNum={examNum}  and qu.userNum={userId}  and qu.userType<>0     ) e LEFT JOIN `subject` s on e.subjectNum = s.subjectNum   LEFT JOIN basegrade bg on e.gradeNum=bg.gradeNum   )e LEFT JOIN (    SELECT q.exampaperNum,max(scancompleted) scancompleted FROM questiongroup q left join exampaper e on q.exampaperNum=e.exampaperNum where e.examNum={examNum} GROUP BY exampaperNum   ) q on e.examPaperNum=q.examPaperNum LEFT JOIN(    SELECT t.examPaperNum,count(1) openDis from testingcentredis t    INNER JOIN testingcentre t1 on t.testingCentreId=t1.id LEFT JOIN exampaper e on t.examPapernum=e.examPaperNum      where e.examNum={examNum}  and t.isDis=1 GROUP BY t.examPaperNum   )t on e.examPaperNum=t.exampapernum LEFT JOIN(    SELECT t.examPaperNum,count(1) totalDis from testingcentredis t INNER JOIN testingcentre t1 on t.testingCentreId=t1.id    LEFT JOIN exampaper e on t.examPapernum=e.examPaperNum      where e.examNum={examNum} GROUP BY t.examPaperNum   )t1 on e.examPaperNum=t1.exampapernum LEFT JOIN (    SELECT c.exampaperNum,count(1) weicaiqie FROM cantrecognized c left join exampaper e on c.exampaperNum=e.exampaperNum where e.examNum={examNum}  GROUP BY exampaperNum   ) s on e.examPaperNum=s.examPaperNum inner JOIN(  SELECT y.*,concat(floor(y.yicaiqie/y.caiqie*100),'%') progress from(   SELECT q.examPaperNum,ifnull(r.count,0) yicaiqie,ifnull(r1.count1,0) caiqie FROM(      SELECT qu.examPaperNum FROM questiongroup_question qu LEFT JOIN exampaper e on qu.exampaperNum=e.examPaperNum         where e.examNum={examNum}  GROUP BY qu.exampaperNum         ) q  inner JOIN (     SELECT e.exampaperNum,r.count from exampaper e LEFT JOIN(  \t\tSELECT exampaperNum,count(id) count from remark where status='T' and type=1 GROUP BY exampaperNum \t)r on e.examPaperNum=r.exampaperNum \twhere e.examNum={examNum}  GROUP BY e.exampaperNum  ) r ON q.exampaperNum=r.exampaperNum LEFT JOIN (      SELECT r.exampaperNum,count(id) count1 FROM remark r LEFT JOIN exampaper e on r.exampaperNum=e.examPaperNum         where e.examNum={examNum} AND r.type='1' GROUP BY r.exampaperNum   ) r1 ON q.exampaperNum=r1.exampaperNum  )y )r on e.examPaperNum=r.examPaperNum ORDER BY e.gradeNum asc,e.orderNum asc  ", ExamData.class, argss);
            scData.setExamName(examName);
            scData.setExamNum(examNum);
            ExamData examDatas = (ExamData) returnList.get(0);
            examDatas.setExt3(allCaijue.toString());
            examDatas.setExt4(yiCaijue.toString());
            Double d = Double.valueOf((Convert.toDouble(yiCaijue).doubleValue() / Convert.toDouble(allCaijue).doubleValue()) * 100.0d);
            DecimalFormat df = new DecimalFormat(".##");
            String progress = df.format(d) + "%";
            examDatas.setProgress(progress);
            scData.setList(returnList);
            list.add(scData);
            argss.clear();
        }
        return list;
    }

    public List<AwardPoint> getMarkGroupList(String examPaperNum, String userId) {
        Map<String, PersonWorkRecord> personWorkRecordMap = AwardPointDaoImpl.personWorkRecordMap;
        new ArrayList();
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("userId", userId);
        List _queryBeanList = this.dao._queryBeanList("SELECT u.*,q.groupName,q.groupType,q.stat `status`,q.correctForbid,d.* FROM   ( \tSELECT examPaperNum,groupNum,userNum FROM questiongroup_user  WHERE exampaperNum={examPaperNum} AND userNum={userId} and groupNum is not null \t)u\t\tLEFT JOIN (  \t\tSELECT examPaperNum,groupNum,total,groupName,grouptype,stat,ifnull(correctForbid,0) correctForbid   FROM questiongroup  WHERE exampaperNum={examPaperNum}   \t) q  ON u.examPaperNum=q.examPaperNum AND u.groupNum=q.groupNum \tLEFT JOIN ( \t\t\tSELECT * from( \t\t\t\tSELECT d.id,d.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,d.orderNum b,0 c,d.choosename choosename \t\t\t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id \t\t\t\twhere d.examPaperNum={examPaperNum} \t\t\t\tunion \t\t\t\tSELECT sb.id,sb.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,sb.orderNum b, d.orderNum c,ifnull(CONCAT(d1.id,sb.orderNum),'s') choosename \t\t\t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id \t\t\t\twhere sb.examPaperNum={examPaperNum} \t\t\t) d \t)d on q.groupNum=d.id ORDER BY d.a,d.b,d.c ", AwardPoint.class, args);
        List list1 = new ArrayList();
        for (int i = 0; i < _queryBeanList.size(); i++) {
            AwardPoint aw = (AwardPoint) _queryBeanList.get(i);
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
        return _queryBeanList;
    }

    public List<AwardPoint> getRemarkGroupList(String exampaperNum, String userNum) {
        String qu_sql;
        StringBuffer rows = new StringBuffer();
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("userNum", userNum);
        String kmzj = this.dao._queryStr("SELECT count(1) from questiongroup_user qu  INNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum  where e.pexamPaperNum={exampaperNum} and qu.userType='2' and qu.userNum={userNum} ", args);
        String pjstr = "";
        if (kmzj.equals("0")) {
            pjstr = "  right  join  ( select  groupNum   from   questiongroup_user   where    examPaperNum={exampaperNum}    and   userType='1'  and  userNum={userNum} )  b \ton  b.groupNum=c.groupNum    ";
        }
        String count = this.dao._queryStr("SELECT IF(qu.exampaperNum=e.pexamPaperNum,1,0) from questiongroup_user qu INNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum where e.pexamPaperNum={exampaperNum}  and  qu.userNum={userNum} and qu.userType=2", args);
        if ("0".equals(count)) {
            qu_sql = "SELECT qu.groupNum,qu.questionNum from questiongroup_question qu INNER JOIN ( \tSELECT b.id from questiongroup_user qu INNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum \t\tleft JOIN( \t\t\t(select id,questionnum,exampaperNum,category from define where exampaperNum={exampaperNum}  and questiontype='1')  \t\t\tunion all  \t\t\t(select id,questionnum,exampaperNum,category  from subdefine WHERE exampaperNum={exampaperNum} ) \t\t)b on qu.examPaperNum=b.category \t\twhere qu.userNum={userNum} and qu.userType=2 \t)a on qu.questionNum=a.id \twhere  qu.examPaperNum={exampaperNum} ";
        } else {
            qu_sql = "select groupNum,questionNum  from   questiongroup_question  where   examPaperNum={exampaperNum} ";
        }
        rows.append("   select   distinct a.groupnum ,qp.groupType,a.exampaperNum, df.questionNum groupName,df.fullScore, \t   a.count1  as  count1 ,\t   a.count2  as  count2 , \t   IFNULL( a.count2/a.count1,0)*100 as  zb \t   from    ( " + qu_sql + "  )  c left join questiongroup qp on c.groupnum=qp.groupnum " + pjstr + "\t    left   join \t   (  \t    select exampaperNum,groupnum, IFNULL(COUNT(1)/count(distinct questionNum),0) as  count1, \t    IFNULL(COUNT(IF(status='T',1,NULL))/count(distinct questionNum),0)  as   count2 from  remark   a           where   a.type ='1'          and  a.examPaperNum={exampaperNum}    \t     group  by  a.groupnum   \t   )a  \t   on       c.groupnum=a.groupnum left join ( \t\t\tSELECT * from( \t\t\t\tSELECT d.id,d.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,d.orderNum b,0 c,d.choosename choosename,d.fullscore \t\t\t\tfrom define d LEFT JOIN define d1 on d.choosename=d1.id \t\t\t\twhere d.examPaperNum={exampaperNum} \t\t\t\tunion \t\t\t\tSELECT sb.id,sb.questionNum,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,sb.orderNum b, d.orderNum c,ifnull(CONCAT(d1.id,sb.orderNum),'s') choosename,sb.fullscore \t\t\t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id \t\t\t\twhere sb.examPaperNum={exampaperNum} \t\t\t) d )df on df.id=a.groupnum \t   where     a.groupnum   is  not  null   order by df.a,df.b,df.c ");
        List list = this.dao._queryBeanList(rows.toString(), AwardPoint.class, args);
        return list;
    }

    public List<Map<String, Object>> getRemarkYiPanList(String examPaperNum, String groupNum, String insertUser) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        StringBuffer sql = new StringBuffer("SELECT r.insertUser,r.questionNum groupNum,s.studentId,cast(r.questionNum as char) questionId,cast(r.questionScore as char) score, cast(r.scoreId as char) scoreId,cast(r.id as char) taskId,t.otherScore ");
        sql.append(" from remark r left join score s on r.scoreId=s.id ");
        sql.append(" LEFT JOIN (  SELECT t.scoreId,GROUP_CONCAT(u.realname,' :',t.questionScore) otherScore from task t  left join user u on t.insertUser=u.id  where t.groupNum={groupNum}  and t.`Status`='T' GROUP BY t.scoreId ) t on r.scoreId=t.scoreId ");
        sql.append(" where r.questionNum={groupNum} and r.status='T' and r.insertUser={insertUser} and r.type=1 ORDER BY r.updateTime ASC ");
        return this.dao._queryMapList(sql.toString(), TypeEnum.StringObject, args);
    }

    public List<Map<String, Object>> getYiPanList(String examPaperNum, String groupNum, String insertUser) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        StringBuffer sql = new StringBuffer("SELECT t.insertUser,t.groupNum,studentId ");
        sql.append(",GROUP_CONCAT(t.questionNum ORDER BY d.orderNum) questionId ");
        sql.append(",GROUP_CONCAT(t.questionScore ORDER BY\td.orderNum) score ");
        sql.append(",GROUP_CONCAT(t.scoreId ORDER BY\td.orderNum) scoreId ");
        sql.append(",GROUP_CONCAT(t.id ORDER BY d.orderNum) taskId ");
        sql.append("from task t LEFT JOIN ( ");
        sql.append("SELECT id,questionNum,orderNum*1 orderNum,id pid,cross_page from define WHERE examPaperNum={examPaperNum}   ");
        sql.append("UNION ALL ");
        sql.append("SELECT s.id,s.questionNum,d.orderNum+s.orderNum*0.001 orderNum,s.pid,s.cross_page  from define d  ");
        sql.append("RIGHT JOIN subdefine s on s.pid=d.id WHERE d.examPaperNum={examPaperNum}  ");
        sql.append(") d on d.id=t.questionNum ");
        sql.append("WHERE t.groupNum={groupNum}  and t.status='T' and t.insertUser={insertUser}  GROUP BY t.studentId ORDER BY t.updateTime ASC");
        return this.dao._queryMapList(sql.toString(), TypeEnum.StringObject, args);
    }
}
