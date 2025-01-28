package com.dmj.serviceimpl.examManagement;

import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Grade;
import com.dmj.domain.School;
import com.dmj.domain.Student;
import com.dmj.domain.Subject;
import com.dmj.service.examManagement.AjaxQueryService;
import com.dmj.util.Const;
import com.zht.db.StreamMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/* loaded from: AjaxQueryServiceImpl.class */
public class AjaxQueryServiceImpl implements AjaxQueryService {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());

    @Override // com.dmj.service.examManagement.AjaxQueryService
    public List<Student> queryStudentInfo(String name, String examineeNum, String exam, String testCenter, String gradeNum, String subject) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("testCenter", testCenter);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("subject", subject);
        args.put("examineeNum", "%" + examineeNum + "%");
        args.put("name", "%" + name + "%");
        return this.dao2._queryBeanList("SELECT st.studentId,st.studentName,enum.examineeNum ext1,room.examinationRoomName ext2,  cl.className className,room.examinationRoomNum ext5  FROM ( SELECT examineeNum,studentID,examinationRoomNum FROM examinationnum WHERE examNum={exam}    and testingCentreId = {testCenter} AND gradeNum = {gradeNum} AND subjectNum = {subject} ) enum LEFT JOIN student st ON st.id = enum.studentID LEFT JOIN class cl ON cl.schoolNum=st.schoolNum AND cl.gradeNum=st.gradeNum AND cl.id=st.classNum LEFT JOIN examinationroom room ON room.id = enum.examinationRoomNum WHERE  examineeNum LIKE {examineeNum} OR st.studentName LIKE {name} ", Student.class, args);
    }

    @Override // com.dmj.service.examManagement.AjaxQueryService
    public List<Grade> getGradeBymoreExam(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryBeanList("SELECT DISTINCT e.gradeNum,g.gradeName FROM exampaper e INNER JOIN basegrade g on e.gradeNum=g.gradeNum WHERE e.examNum in ({examNum[]}) GROUP BY gradeNum", Grade.class, args);
    }

    @Override // com.dmj.service.examManagement.AjaxQueryService
    public List<Subject> getSubjectByGrade(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryBeanList("SELECT g.subjectNum ,sjt.subjectName FROM  (SELECT DISTINCT subjectNum FROM gradelevel WHERE examNum in ({examNum[]}) AND gradeNum={gradeNum}  AND statisticType='0' and source='0'   and issub != '0'  ) g   LEFT JOIN `subject` sjt ON g.subjectNum=sjt.subjectNum  where g.subjectNum!='-1' order by sjt.orderNum ", Subject.class, args);
    }

    @Override // com.dmj.service.examManagement.AjaxQueryService
    public List<School> getSchoolBySubject(Map<String, String> map, String userId) {
        String sql;
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String subjectNum = map.get(Const.EXPORTREPORT_subjectNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("userId", (Object) userId);
        if (userId.equals("-1") || userId.equals("-2")) {
            sql = "select  DISTINCT g.schoolNum,s.shortname schoolName from gradelevel g INNER join school  s on s.id = g.schoolNum and g.examNum in ({examNum[]})   where g.gradeNum={gradeNum} and g.subjectNum={subjectNum} and g.source = '0' and g.statisticType = '0'  ORDER BY convert(s.schoolName using gbk)";
        } else {
            sql = "select  DISTINCT g.schoolNum,s.shortname schoolName from gradelevel g INNER join school  s on s.id = g.schoolNum and g.examNum in ({examNum[]})   left join schauthormanage h on h.schoolNum = s.id and h.userId={userId}   left join user t on t.schoolNum = s.id  and t.id = {userId} and t.usertype=1   where g.gradeNum={gradeNum} and g.source = '0' and g.statisticType = '0'  and (h.schoolNum is not null or t.schoolNum is not null) ORDER BY convert(s.schoolName using gbk)";
        }
        return this.dao2._queryBeanList(sql, School.class, args);
    }

    @Override // com.dmj.service.examManagement.AjaxQueryService
    public List<AjaxData> getGraduationType(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String subjectNum = map.get(Const.EXPORTREPORT_subjectNum);
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("Data_graduationType", Const.Data_graduationType);
        return this.dao2._queryBeanList("select r.statisticType num, d.name name from (select distinct statisticType  from gradelevel where examNum in ({examNum[]}) and gradeNum={gradeNum} and subjectNum={subjectNum} and schoolNum={schoolNum}) r left join (SELECT `value` num,`name` name  FROM `data` WHERE type={Data_graduationType}) d on d.num=r.statisticType", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.AjaxQueryService
    public List<AjaxData> getSourceType(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String subjectNum = map.get(Const.EXPORTREPORT_subjectNum);
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("Data_stuSource", (Object) Const.Data_stuSource);
        return this.dao2._queryBeanList("select r.source num, d.name name from (select distinct source  from gradelevel where examNum in ({examNum[]}) and gradeNum={gradeNum} and subjectNum={subjectNum} and schoolNum={schoolNum}) r left join (SELECT `value` num,`name` name  FROM `data` WHERE type={Data_stuSource}) d on d.num=r.source", AjaxData.class, args);
    }
}
