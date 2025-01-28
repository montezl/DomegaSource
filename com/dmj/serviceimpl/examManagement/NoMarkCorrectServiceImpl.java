package com.dmj.serviceimpl.examManagement;

import cn.hutool.core.util.StrUtil;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Examinationnum;
import com.dmj.domain.Examinationroom;
import com.dmj.domain.ExamineeNumError;
import com.dmj.domain.Examlog;
import com.dmj.domain.Exampaper;
import com.dmj.domain.RegExaminee;
import com.dmj.domain.Score;
import com.dmj.domain.TestData;
import com.dmj.domain.User;
import com.dmj.service.examManagement.NoMarkCorrectService;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.zht.db.RowArg;
import com.zht.db.StreamMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/* loaded from: NoMarkCorrectServiceImpl.class */
public class NoMarkCorrectServiceImpl implements NoMarkCorrectService {
    private static List<String> lockRegidList = new ArrayList();
    private static Object regIdLock = new Object();
    Logger log = Logger.getLogger(getClass());
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public List<TestData> getNoMarkCorrectList(String examPaperNum, int index, int pageSize, String flag, String schoolNum, String exam, String grade, String cexamroom, String cexamineeNum, String cstuId, String examroom, String etype, String testCenter, String subject) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("testCenter", testCenter);
        args.put("cexamroom", cexamroom);
        args.put("grade", grade);
        args.put("exam", exam);
        args.put("cexamineeNum", cexamineeNum);
        args.put("cstuId", cstuId);
        args.put("examroom", examroom);
        args.put("examPaperNum", examPaperNum);
        args.put("subject", subject);
        args.put("index", Integer.valueOf(index));
        args.put("pageSize", Integer.valueOf(pageSize));
        String schoolNumStr = "";
        String schoolStr = "";
        if (!schoolNum.equals("-1")) {
            schoolNumStr = " AND reg.schoolNum={schoolNum} ";
            schoolStr = " AND schoolNum={schoolNum} ";
        }
        if (testCenter != null && !"".equals(testCenter)) {
            schoolNumStr = " AND reg.testingCentreId={testCenter} ";
            schoolStr = " AND testingCentreId={testCenter} ";
        }
        String cexamroomStr = "";
        String cexamineeNumStr = "";
        String cstuIdStr = "";
        if (null != cexamroom && !cexamroom.equals("")) {
            String getexamroomSql = "SELECT id FROM examinationroom WHERE examinationRoomNum={cexamroom} AND gradeNum={grade} " + schoolNumStr + " AND examNum={exam} ";
            String examroomend = this.dao2._queryStr(getexamroomSql, args);
            args.put("examroomend", examroomend);
            cexamroomStr = " AND s.examinationRoomNum LIKE {examroomend} ";
        }
        if (null != cexamineeNum && !cexamineeNum.equals("")) {
            cexamineeNumStr = " AND examineeNum LIKE {cexamineeNum} ";
        }
        if (null != cstuId && !cstuId.equals("")) {
            cstuIdStr = " AND stu.studentId LIKE {cstuId} ";
        }
        String examroomStr1 = "";
        String examroomStr2 = "";
        String examroomStr3 = "";
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1") && etype.equals("0")) {
            examroomStr1 = " AND reg.examinationRoomNum={examroom} ";
            examroomStr2 = " AND examinationRoomNum={examroom} ";
            examroomStr3 = " AND enm.examinationRoomNum =s.examinationRoomNum AND enm.testingCentreId =s.testingCentreId";
        }
        String sql3 = "SELECT  s.studentid ext1,stu.studentName ext2,s.page ext3,enm.examineeNum ext4,s.examPaperNum ext5,s.examinationRoomNum ext6,s.id ext7,s.cNum ext8,s.schoolNum ext9,s.type  ext10   FROM   ( SELECT r.id,r.studentid,r.page,r.schoolNum,r.examPaperNum,r.examinationRoomNum,r.cNum,r.type,r.testingCentreId   FROM   (SELECT reg.id,reg.studentid,reg.page,reg.schoolNum,reg.examPaperNum  ,reg.examinationRoomNum,reg.cNum,reg.type,reg.testingCentreId   FROM regexaminee reg    LEFT JOIN cantrecognized cant ON reg.studentId=cant.studentId     AND reg.page=cant.page   AND reg.type=cant.type AND reg.cNum=cant.cNum  LEFT JOIN examineenumerror err    ON reg.examPaperNum=err.examPaperNum AND reg.testingCentreId=err.testingCentreId  AND reg.studentId=err.studentId   AND reg.cNum=err.groupNum AND reg.page=err.page   WHERE\treg.examPaperNum = {examPaperNum} " + schoolNumStr + examroomStr1 + "AND cant.id IS  NULL   ";
        if ("1".equals(flag)) {
            sql3 = sql3 + "AND err.id IS NOT NULL    ";
        }
        if ("2".equals(flag)) {
            sql3 = sql3 + "AND err.id IS NULL    ";
        }
        return this.dao2._queryBeanList(sql3 + ")r   )s   LEFT JOIN student stu ON stu.studentid = s.studentid   LEFT JOIN (SELECT * FROM examinationnum WHERE examNum={exam} AND gradeNum={grade} " + schoolStr + examroomStr2 + " AND subjectNum={subject}) enm ON enm.studentId = s.studentid " + examroomStr3 + " WHERE 1=1 " + cexamineeNumStr + cexamroomStr + cstuIdStr + " ORDER BY enm.examineeNum, s.studentid,s.page  LIMIT {index},{pageSize} ", TestData.class, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public Integer updateCorrectNumsStat(String status, String schoolNum, String exam, String subject, String grade, String loginUserId, String examroom) {
        String schoolNumStr = "";
        if (!schoolNum.equals("-1")) {
            schoolNumStr = " AND schoolNum={schoolNum} ";
        }
        if (status.equals("1")) {
        }
        String examroomStr = "";
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1")) {
            examroomStr = " AND examnitionRoom={examroom} ";
        }
        Object obj = getCorrectStatus(schoolNum, exam, subject, grade, "2", examroom);
        if (null == obj || Integer.parseInt(obj.toString()) <= Integer.parseInt(status)) {
            String sql = "update correctStatus set numStatus = {status}  ,insertUser = {loginUserId}  where  examNum = {exam} and subjectNum={subject} and gradeNum = {grade}  " + schoolNumStr + examroomStr;
            Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("loginUserId", (Object) loginUserId).put("examroom", (Object) examroom).put(Const.CORRECT_SCORECORRECT, (Object) status).put("exam", (Object) exam).put("subject", (Object) subject).put("grade", (Object) grade);
            return Integer.valueOf(this.dao2._execute(sql, args));
        }
        return 1;
    }

    public Object getCorrectStatus(String schoolNum, String exam, String subject, String grade, String type, String examroom) {
        String schoolNumStr = "";
        if (!schoolNum.equals("-1")) {
            schoolNumStr = " and schoolNum={schoolNum} ";
        }
        String statStr = " status ";
        if (null != type && type.equals("2")) {
            statStr = " numStatus ";
        }
        String examroomStr = "";
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1")) {
            examroomStr = " AND examnitionRoom={examroom} ";
        }
        String sql = " select " + statStr + " from correctStatus where 1=1  and examNum = {exam} and subjectNum={subject} and gradeNum = {grade} " + schoolNumStr + examroomStr;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examroom", examroom);
        args.put("exam", exam);
        args.put("subject", subject);
        args.put("grade", grade);
        return this.dao2._queryObject(sql, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public List<Object> getExamRoomPageRank(String examNum, String subjectNum, String examPaperNum, String testCenter, String gradeNum, String examroom, String examroomornot) {
        String examroomStr = "";
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1") && examroomornot.equals("0")) {
            examroomStr = "AND  examinationRoomNum={examroom} AND testingCentreId={testCenter}  ";
        } else if (null != testCenter && !testCenter.equals("-1") && !testCenter.equals("")) {
            examroomStr = "AND testingCentreId={testCenter}  ";
        }
        try {
            String sql = "SELECT CONCAT('[第',CONVERT(page,char),'页：',CONVERT(COUNT(1),char),']') pageStr  FROM (SELECT r.page,r.studentId,r.cNum  FROM (SELECT id,examPaperNum,studentId,page,cNum,schoolNum,type from regexaminee  WHERE exampaperNum={examPaperNum}  " + examroomStr + ")r  LEFT JOIN (SELECT id,regId,examPaperNum FROM cantrecognized  WHERE examPaperNum={examPaperNum}  " + examroomStr + ") c ON  c.regId = r.id  WHERE c.id IS  NULL AND r.studentId IS NOT NULL  )re  GROUP BY re.page having re.page>0";
            Map args = StreamMap.create().put("examroom", (Object) examroom).put("testCenter", (Object) testCenter).put("examPaperNum", (Object) examPaperNum);
            return this.dao2._queryColList(sql, args);
        } catch (Exception e) {
            this.log.info("页码分布", e);
            return null;
        }
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public Integer count(String exam, String grade, String exampaperNum, String examroomNum, String flag, String flagMd, String paperPage, String schoolNum, String cexamroom, String cstuName, String cexamineeNum, String cstuId, String examroomornot, String testCenter, String subject, String errorType) {
        String sql3;
        String sql32;
        String sql33;
        String sql34;
        String sctcs = "";
        String regStr = "";
        String enmStr = "";
        if (null != schoolNum && !schoolNum.equals("")) {
            sctcs = " AND schoolNum={schoolNum} ";
            regStr = " AND reg.schoolNum={schoolNum} ";
            enmStr = " AND enm.schoolNum={schoolNum} ";
        }
        if (null != testCenter && !testCenter.equals("")) {
            sctcs = " AND testingCentreId = {testCenter} ";
            regStr = " AND reg.testingCentreId = {testCenter} ";
            enmStr = " AND enm.testingCentreId = {testCenter} ";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("testCenter", testCenter);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("cexamroom", cexamroom);
        args.put("cstuId", cstuId);
        args.put("examroomNum", examroomNum);
        args.put("exampaperNum", exampaperNum);
        args.put("TRUE", "T");
        args.put("paperPage", paperPage);
        args.put("cexamineeNum", cexamineeNum);
        args.put("errorType", errorType);
        args.put("cstuName", "%" + cstuName + "%");
        String cexamroomId = "";
        if (null != cexamroom && !cexamroom.equals("")) {
            String cexamroomIdSql = "SELECT id FROM examinationroom WHERE examNum={exam} " + sctcs + " AND gradeNum={grade} AND subjectNum = {subject} AND (examinationRoomNum={cexamroom} or examinationRoomName = {cexamroom} )";
            cexamroomId = this.dao2._queryStr(cexamroomIdSql, args);
            args.put("cexamroomId", cexamroomId);
        }
        String stuId = "";
        if (null != cstuId && !cstuId.equals("")) {
            stuId = this.dao2._queryStr("SELECT id FROM student WHERE studentId={cstuId} ", args);
            args.put("stuId", stuId);
        }
        String room_schoolStr = "";
        String room_schoolStr_cant = "";
        String roomId = "";
        if (null != cexamroomId && !"".equals(cexamroomId)) {
            room_schoolStr = "AND  reg.examinationRoomNum={cexamroomId} AND  reg.testingCentreId={testCenter}  ";
            room_schoolStr_cant = "AND  cant.examinationRoomNum={cexamroomId} AND  cant.testingCentreId={testCenter}  ";
            roomId = cexamroomId;
        } else if (null != examroomNum && !examroomNum.equals("") && !examroomNum.equals("-1") && examroomornot.equals("0")) {
            room_schoolStr = "AND  reg.examinationRoomNum={examroomNum} ";
            room_schoolStr_cant = "AND  cant.examinationRoomNum={examroomNum} ";
            roomId = examroomNum;
        } else {
            if (null != schoolNum && !schoolNum.equals("")) {
                room_schoolStr = "AND  (reg.schoolNum={schoolNum}  or reg.schoolNum='0') ";
                room_schoolStr_cant = "AND  cant.schoolNum={schoolNum}  ";
            }
            if (null != testCenter && !testCenter.equals("")) {
                room_schoolStr = "AND reg.testingCentreId={testCenter} ";
                room_schoolStr_cant = "AND  cant.testingCentreId={testCenter}  ";
            }
        }
        args.put("roomId", roomId);
        String errStr = "";
        if (null != schoolNum && !schoolNum.equals("")) {
            errStr = "AND (err.schoolNum={schoolNum} or err.schoolNum='0') ";
        }
        if (null != testCenter && !testCenter.equals("")) {
            errStr = "AND err.testingCentreId={testCenter} ";
        }
        boolean fla = true;
        String sql35 = "SELECT  count(1)   FROM   ( SELECT r.id,r.studentid,r.page,r.schoolNum,r.examPaperNum,r.examinationRoomNum,r.cNum,r.type   FROM   (SELECT reg.id,reg.studentid,reg.page,reg.schoolNum,reg.examPaperNum  ,reg.examinationRoomNum,reg.cNum,reg.type    FROM (select id,studentId,page,schoolNum,examPaperNum,examinationRoomNum,cNum,type from regexaminee where exampaperNum={exampaperNum}    " + room_schoolStr;
        if (null != stuId && !stuId.equals("")) {
            sql35 = sql35 + "AND studentId ={stuId}   ";
        }
        if (null != flag && flag.equals("2")) {
            sql35 = sql35 + "AND isModify ={TRUE}   ";
        }
        if (null != paperPage && !paperPage.equals("-1")) {
            sql35 = sql35 + "AND page ={paperPage}  ";
        }
        String sql36 = sql35 + ") reg    LEFT JOIN (select id,examPaperNum,regId,stat from cantrecognized where exampaperNum={exampaperNum}  " + room_schoolStr + ") cant   ON reg.id=cant.regId   AND cant.stat=0   LEFT JOIN (select id,studentId,page,schoolNum,groupNum,errorType,regId  from examineenumerror where exampaperNum={exampaperNum}  ";
        if (null != roomId && !"".equals(roomId)) {
            sql36 = sql36 + "AND examinationRoomNum={roomId}  ";
            fla = false;
        }
        if (null != stuId && !stuId.equals("")) {
            sql36 = sql36 + "AND studentId ={stuId}   ";
            fla = false;
        }
        if (fla) {
            sql36 = sql36 + sctcs;
        }
        String sql37 = sql36 + ") err  ON  reg.id=err.regId    WHERE cant.id IS  NULL   ";
        if ("1".equals(flag)) {
            sql37 = sql37 + "AND err.id IS NOT NULL    ";
        }
        if ("0".equals(flag)) {
        }
        String sql38 = sql37 + ")r   )s   LEFT JOIN (select id,studentId,studentName,studentNum from student   where  ";
        if (null != stuId && !stuId.equals("")) {
            sql3 = sql38 + "id ={stuId}   ";
        } else {
            sql3 = sql38 + " gradeNum={grade} " + sctcs + " ";
        }
        String sql39 = sql3 + " ) stu ON stu.id = s.studentid   LEFT JOIN (SELECT examineeNum, examinationRoomNum, studentId FROM examinationnum WHERE   ";
        if (null != roomId && !"".equals(roomId)) {
            sql32 = sql39 + " examinationRoomNum={roomId}  ";
        } else {
            sql32 = sql39 + " examNum={exam} " + sctcs + "  AND gradeNum={grade} AND subjectNum={subject}  ";
        }
        if (null != cexamineeNum && !"".equals(cexamineeNum)) {
            sql32 = sql32 + "AND  examineeNum={cexamineeNum}   ";
        }
        String sql310 = sql32 + ") enm   ON enm.studentId = s.studentid  ";
        if (null != cexamineeNum && !"".equals(cexamineeNum)) {
            sql310 = sql310 + "WHERE enm.examineeNum={cexamineeNum}  ";
        }
        if (null != cstuName && !"".equals(cstuName)) {
            String str = sql310 + " and stu.studentName like {cstuName}  ";
        }
        String sql311 = "SELECT  count(1)   FROM regexaminee reg    LEFT JOIN cantrecognized cant   ON reg.id=cant.regId AND cant.exampaperNum={exampaperNum}  " + room_schoolStr_cant + "AND cant.stat=0   LEFT JOIN examineenumerror err  ON  reg.id=err.regId  AND err.exampaperNum={exampaperNum}  ";
        if (null != roomId && !"".equals(roomId)) {
            sql311 = sql311 + "AND err.examinationRoomNum={roomId} AND  err.testingCentreId={testCenter}  ";
            fla = false;
        }
        if (null != stuId && !stuId.equals("")) {
            sql311 = sql311 + "AND err.studentId ={stuId}   ";
            fla = false;
        }
        if (fla) {
            sql311 = sql311 + errStr;
        }
        String sql312 = sql311 + "LEFT JOIN student stu ON reg.studentId=stu.id  ";
        if (null != cstuName && !"".equals(cstuName)) {
            sql312 = sql312 + " and stu.studentName like {cstuName}  ";
        }
        if (null != stuId && !stuId.equals("")) {
            sql33 = sql312 + "AND stu.id ={stuId}   ";
        } else {
            sql33 = sql312 + "AND stu.gradeNum={grade} " + regStr + " ";
        }
        String sql313 = sql33 + "LEFT JOIN examinationnum enm ON enm.studentId = reg.studentid  ";
        if (null != roomId && !"".equals(roomId)) {
            sql34 = sql313 + " AND enm.examinationRoomNum={roomId} AND  enm.testingCentreId={testCenter}  ";
        } else {
            sql34 = sql313 + " AND enm.examNum={exam} " + enmStr + " AND enm.gradeNum={grade}  ";
        }
        if (null != cexamineeNum && !"".equals(cexamineeNum)) {
            sql34 = sql34 + "AND enm.examineeNum={cexamineeNum}   ";
        }
        String sql314 = sql34 + " AND enm.subjectNum = {subject} WHERE reg.examPaperNum={exampaperNum}  " + room_schoolStr;
        if (null != stuId && !stuId.equals("")) {
            sql314 = sql314 + "AND reg.studentId ={stuId}   ";
        }
        if (null != flag && flag.equals("2")) {
            sql314 = sql314 + "AND reg.isModify ={TRUE}   ";
        }
        if (null != paperPage && !paperPage.equals("-1")) {
            sql314 = sql314 + "AND reg.page ={paperPage}  ";
        }
        String sql315 = sql314 + "AND cant.id IS  NULL   ";
        if ("1".equals(flag)) {
            sql315 = sql315 + "AND err.id IS NOT NULL    ";
        }
        if (null != cexamineeNum && !"".equals(cexamineeNum)) {
            sql315 = sql315 + "AND enm.examineeNum={cexamineeNum}  ";
        }
        if (null != cstuName && !"".equals(cstuName)) {
            sql315 = sql315 + " and stu.studentName like {cstuName}  ";
        }
        if (!"-1".equals(errorType)) {
            sql315 = sql315 + " and err.errorType = {errorType} ";
        }
        this.log.info("考号校对总数--getAllNumImg---sql:" + sql315);
        return this.dao2._queryInt(sql315, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public Integer countOfCorner(String exam, String grade, String subject, String exampaperNum, String testCenter, String examroomornot, String examroom, String cexamroom, String cstuName, String cexamineeNum, String cstuId, String paperPage, String errorType) {
        String _erSql = (!"0".equals(examroomornot) || "-1".equals(examroom)) ? "" : " and reg.examinationRoomNum = {examroom} ";
        String cErSql = !"".equals(cexamroom) ? " and (er.examinationRoomNum = {cexamroom} or er.examinationRoomName = {cexamroom} ) " : "";
        String cStuNameSql = !"".equals(cstuName) ? " and stu.studentName like {cstuName} " : "";
        String cEnSql = !"".equals(cexamineeNum) ? " and en.examineeNum = {cexamineeNum} " : "";
        String cStuIdSql = !"".equals(cstuId) ? " and stu.studentId = {cstuId} " : "";
        String cPagSql = !"-1".equals(paperPage) ? " and reg.page = {paperPage} " : "";
        String cErrSql = !"-1".equals(errorType) ? " and err.errorType = {errorType} " : "";
        String sql = "select count(1) from corner cn LEFT JOIN regexaminee reg on reg.id = cn.regid LEFT JOIN examinationnum en on en.studentId = reg.studentId and en.examNum = {exam} and en.gradeNum = {grade} and en.subjectNum = {subject} LEFT JOIN examinationroom er on er.id = en.examinationRoomNum LEFT JOIN examineenumerror err on err.regId = cn.regid LEFT JOIN student stu on stu.id = reg.studentId where reg.examPaperNum = {exampaperNum} and reg.testingCentreId = {testCenter} " + _erSql + cPagSql + cEnSql + cErSql + cStuNameSql + cErrSql + cStuIdSql;
        this.log.info("考号校对--疑似折角总数[countOfCorner]--sql:" + sql);
        Map args = StreamMap.create().put("examroom", (Object) examroom).put("cexamroom", (Object) cexamroom).put("cexamineeNum", (Object) cexamineeNum).put("cstuName", (Object) ("%" + cstuName + "%")).put("cstuId", (Object) cstuId).put("paperPage", (Object) paperPage).put("errorType", (Object) errorType).put("exam", (Object) exam).put("grade", (Object) grade).put("subject", (Object) subject).put("exampaperNum", (Object) exampaperNum).put("testCenter", (Object) testCenter);
        return this.dao2._queryInt(sql, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public Integer modifyExamroomAndExamineeNum(String examPaperNum, String school, String newExamroomNum, String oldStudentId, String newStudentId, int page, String cNum, String oldExamroomId, String examroomornot) {
        RegExaminee reg;
        List<RegExaminee> regOldList = authDataExistsFromRegExamineeList("", examPaperNum, oldStudentId, school, page, cNum, oldExamroomId, null, null, null, examroomornot);
        List<RegExaminee> regNewList2 = authDataExistsFromRegExamineeList("", examPaperNum, newStudentId, school, page, null, newExamroomNum, regOldList, "T", null, examroomornot);
        List<RegExaminee> regNewList = null;
        if (regNewList2 != null && regNewList2.size() > 0) {
            regNewList = authDataExistsFromRegExamineeList("", examPaperNum, newStudentId, school, page, regNewList2.get(0).getcNum() + "", newExamroomNum, null, null, null, examroomornot);
        }
        new RegExaminee();
        if (regNewList != null) {
            reg = regNewList.get(0);
        } else {
            reg = null;
        }
        if (null == reg) {
            Integer returnCode = modifyExamroomAndExamineeNum1(examPaperNum, school, newExamroomNum, oldStudentId, newStudentId, page, regOldList, cNum);
            if (null == returnCode) {
                return returnCode;
            }
            return null;
        }
        String tempStudentId = GUID.getGUIDStr();
        if (null != modifyExamroomAndExamineeNum1(examPaperNum, school, reg.getExaminationRoomNum() + "", reg.getStudentId() + "", tempStudentId, page, regNewList, reg.getcNum() + "")) {
            for (RegExaminee regObj : regNewList) {
                ExamineeNumError error = new ExamineeNumError();
                error.setPage(regObj.getPage());
                error.setErrorType("1");
                error.setExaminationRoomNum(regObj.getExaminationRoomNum());
                error.setSchoolNum(Integer.valueOf(regObj.getSchoolNum()));
                error.setExamineeNum("");
                error.setInsertDate(DateUtil.getCurrentTime());
                error.setGroupNum(regObj.getcNum());
                this.dao2.save(error);
            }
            Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
            List<?> _queryBeanList = this.dao2._queryBeanList("SELECT  DISTINCT examNum,gradeNum,subjectNum   FROM exampaper     WHERE examPaperNum={examPaperNum} ", Exampaper.class, args);
            Examlog examlog = new Examlog();
            examlog.setOperate("修改考号-来自识别表-插入考号有误表");
            examlog.setExamNum(((Exampaper) _queryBeanList.get(0)).getExamNum());
            examlog.setExaminationRoomNum(reg.getExaminationRoomNum());
            examlog.setInsertDate(DateUtil.getCurrentTime());
            examlog.setSubjectNum(((Exampaper) _queryBeanList.get(0)).getSubjectNum());
            examlog.setGradeNum(((Exampaper) _queryBeanList.get(0)).getGradeNum());
            this.dao2.save(examlog);
        }
        Integer code2 = modifyExamroomAndExamineeNum1(examPaperNum, school, newExamroomNum, oldStudentId, newStudentId, page, regOldList, cNum);
        if (null == code2) {
            return code2;
        }
        return null;
    }

    public List<RegExaminee> authDataExistsFromRegExamineeList(String paperType, String examPaperNum, String studentId, String schoolNum, int page, String cNum, String oldExamroomId, List regOldList, String flg, String regOldOrNewFlag, String examroomornot) {
        List<RegExaminee> twoPaperList2;
        Map args = new HashMap();
        String schoolNumStr = "";
        if (!schoolNum.equals("-1")) {
            schoolNumStr = "and reg.schoolNum={schoolNum}   ";
        }
        String sql = "select DISTINCT reg.id,reg.examPaperNum,reg.studentId,reg.insertUser,reg.schoolNum,reg.page,reg.type,reg.examinationRoomNum,reg.cNum,ep.doubleFaced,ep.examNum exam   from regexaminee reg   LEFT JOIN exampaper ep   ON reg.examPaperNum=ep.examPaperNum    where 1=1 ";
        if (cNum != null && !"".equals(cNum)) {
            sql = sql + "and reg.cNum={cNum}   ";
        }
        if (examroomornot.equals("0") && null != oldExamroomId && !oldExamroomId.equals("")) {
            sql = sql + "and reg.examinationRoomNum={oldExamroomId}   ";
        }
        if (studentId != null && !studentId.equals("")) {
            sql = sql + "and reg.studentId={studentId}   ";
        }
        if (paperType != null && !"".equals(paperType)) {
            sql = sql + "and reg.type={paperType}   ";
        }
        String sql2 = sql + schoolNumStr + "and reg.examPaperNum={examPaperNum}   ";
        String s = "   and reg.page=" + page;
        if (flg != null && "T".equals(flg) && null != oldExamroomId && !oldExamroomId.equals("") && examroomornot.equals("0")) {
            s = s + "\t\tand reg.examinationRoomNum={oldExamroomId}   ";
        }
        if (cNum != null && !"".equals(cNum) && (twoPaperList2 = getPageByPosAndNegMark(examPaperNum, studentId, cNum, oldExamroomId, schoolNum, examroomornot)) != null && twoPaperList2.size() >= 2) {
            args.put("twoPaperList20", Integer.valueOf(twoPaperList2.get(0).getPage()));
            args.put("twoPaperList21", Integer.valueOf(twoPaperList2.get(1).getPage()));
            s = "  AND (reg.page={twoPaperList20} OR reg.page={twoPaperList21}) ";
        }
        if (flg != null && "T".equals(flg) && regOldList != null && regOldList.size() >= 2) {
            if (oldExamroomId != null && !oldExamroomId.equals("") && examroomornot.equals("0")) {
                args.put("regOldList0", Integer.valueOf(((RegExaminee) regOldList.get(0)).getPage()));
                args.put("regOldList1", Integer.valueOf(((RegExaminee) regOldList.get(1)).getPage()));
                String str = s + "\t\tand reg.examinationRoomNum={oldExamroomId}   ";
            }
            s = "  AND (reg.page={regOldList0}  OR reg.page={regOldList1} ) ";
        }
        String sql3 = sql2 + s;
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("cNum", cNum);
        args.put("oldExamroomId", oldExamroomId);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("paperType", paperType);
        args.put("examPaperNum", examPaperNum);
        this.log.info("-authDataExistsFromRegExaminee-查询正面（正反面）是否在reg表中有--sql:" + sql3);
        new ArrayList();
        List _queryBeanList = this.dao2._queryBeanList(sql3, RegExaminee.class, args);
        if (_queryBeanList != null && _queryBeanList.size() > 0) {
            return _queryBeanList;
        }
        return null;
    }

    public Integer modifyExamroomAndExamineeNum1(String exampaperNum, String school, String newExamroomNum, String oldstudentId, String newStudentId, int page, List regOldList, String cNum) {
        Map args = new HashMap();
        args.put("page", Integer.valueOf(page));
        args.put("cNum", cNum);
        args.put("regOldList0", Integer.valueOf(((RegExaminee) regOldList.get(0)).getPage()));
        args.put("regOldList1", Integer.valueOf(((RegExaminee) regOldList.get(1)).getPage()));
        args.put("newStudentId", newStudentId);
        args.put("newExamroomNum", newExamroomNum);
        args.put("exampaperNum", exampaperNum);
        args.put("oldstudentId", oldstudentId);
        args.put(License.SCHOOL, school);
        String pageSql = "  AND page={page} ";
        String pageSql8 = "  AND c.page={page}  ";
        String pageSql9 = "  AND s.page={page}  ";
        String cNumSql = " AND  cNum IS NULL  ";
        String errorcNumSql = " AND  groupNum IS NULL  ";
        if (cNum != null) {
            cNumSql = " AND  cNum ={cNum}  ";
            errorcNumSql = " AND  groupNum ={cNum}   ";
        }
        if (regOldList != null && regOldList.size() >= 2) {
            pageSql = "  AND (page={regOldList0} OR page={regOldList1}) ";
            pageSql8 = "  AND (c.page={regOldList0} OR c.page={regOldList1})) ";
            pageSql9 = "  AND (s.page={regOldList0} OR s.page={regOldList1}) ) ";
        }
        List<String> sqls = new ArrayList<>();
        String sql1 = "UPDATE regexaminee SET studentId={newStudentId},examinationRoomNum={newExamroomNum} where examPaperNum={exampaperNum} AND studentId={oldstudentId} " + pageSql + cNumSql + "  AND schoolNum={school} ";
        String sql2 = "UPDATE score SET studentId={newStudentId},examinationRoomNum={newExamroomNum} ,description='000' where examPaperNum={exampaperNum}  AND studentId={oldstudentId}  " + pageSql + "  AND schoolNum={school} ";
        String sql4 = "UPDATE examinationnumimg SET studentId={newStudentId},examinationRoomNum={newExamroomNum}  where examPaperNum={exampaperNum}  AND studentId={oldstudentId}  " + pageSql + "  AND schoolNum={school} ";
        String sql5 = "UPDATE scoreimage SET studentId={newStudentId},examinationRoomNum={newExamroomNum}  where examPaperNum={exampaperNum}  AND studentId={oldstudentId}  " + pageSql + "  AND schoolNum={school} ";
        String sql6 = "UPDATE questionimage SET studentId={newStudentId},examinationRoomNum={newExamroomNum}  where examPaperNum={exampaperNum}  AND studentId={oldstudentId}  " + pageSql + "  AND schoolNum={school} ";
        String sql7 = "UPDATE studentpaperimage SET studentId={newStudentId},examinationRoomNum={newExamroomNum}  where examPaperNum={exampaperNum}  AND studentId={oldstudentId}  " + pageSql + cNumSql + "  AND schoolNum={school} ";
        String sql8 = "UPDATE cantrecognized c LEFT JOIN exampaper e ON c.examNum = e.examNum AND c.subjectNum = e.subjectNum AND c.gradeNum = e.gradeNum SET c.studentId={newStudentId} ,examinationRoomNum={newExamroomNum}  WHERE e.examPaperNum={exampaperNum}  " + pageSql8 + cNumSql + "  AND studentId={oldstudentId}   ";
        String sql_tag = "UPDATE tag SET studentId={newStudentId},examinationRoomNum={newExamroomNum}  where examPaperNum={exampaperNum}  AND studentId={oldstudentId}  " + pageSql + "  AND schoolNum={school} ";
        String pageStr0 = "  AND page={page}  ";
        if (regOldList != null && regOldList.size() >= 2) {
            pageStr0 = " AND (page={regOldList0} OR page={regOldList1} ) ";
        }
        String qNumListSql = "SELECT /* shard_host_HG=Write */ questionNum FROM score WHERE  exampaperNum={exampaperNum}  AND (studentId={newStudentId} OR studentId={oldstudentId} ) AND schoolNum={school} " + pageStr0;
        List qNumList = this.dao2._queryColList(qNumListSql, args);
        for (int i = 0; i < qNumList.size(); i++) {
            String qNum = (String) qNumList.get(i);
            args.put("qNum", qNum);
            String pageStr = "  AND page!={page}  ";
            if (regOldList != null && regOldList.size() >= 2) {
                pageStr = "   AND page!={regOldList0} AND page!={regOldList1} ";
            }
            String qNumCountSql = "SELECT /* shard_host_HG=Write */ count(1) FROM score where  exampaperNum={exampaperNum}  AND (studentId={newStudentId} OR studentId={oldstudentId} ) AND schoolNum={school}  AND questionNum={qNum} " + pageStr;
            int count = this.dao2._queryInt(qNumCountSql, args).intValue();
            if (count > 0) {
                String pageStr3 = "  AND page={page}  ";
                if (regOldList != null && regOldList.size() >= 2) {
                    pageStr3 = "  AND page={regOldList1} ";
                }
                String getqNumSql = "SELECT /* shard_host_HG=Write */ questionNum FROM score where  exampaperNum={exampaperNum}  AND (studentId={newStudentId} OR studentId={oldstudentId} ) AND schoolNum={school}  AND questionNum={qNum} " + pageStr3;
                String qNum2 = this.dao2._queryStr(getqNumSql, args);
                args.put("qNum2", qNum2);
                String delqNumSql = "DELETE FROM score WHERE studentId={oldstudentId}  AND exampaperNum={exampaperNum}  AND schoolNum={school}  AND questionNum={qNum2} " + pageStr3;
                this.dao2._execute(delqNumSql, args);
            }
        }
        String sql9 = "UPDATE score  s  LEFT JOIN student st ON st.studentId = s.studentId  SET s.classNum = st.classNum  where s.examPaperNum={exampaperNum}  AND s.studentId={newStudentId} " + pageSql9 + "  AND s.schoolNum={school} ";
        String sql90 = "UPDATE regexaminee  s  LEFT JOIN student st ON st.studentId = s.studentId  SET s.classNum = st.classNum  where s.examPaperNum={exampaperNum}  AND s.studentId={newStudentId} " + pageSql9 + cNumSql + "  AND s.schoolNum={school} ";
        String sql91 = "UPDATE tag  s  LEFT JOIN student st ON st.studentId = s.studentId  SET s.classNum = st.classNum  where s.examPaperNum={exampaperNum}  AND s.studentId={newStudentId} " + pageSql9 + "  AND s.schoolNum={school} ";
        String deleteSql = "DELETE FROM examineenumerror where examPaperNum={exampaperNum}  AND studentId={oldstudentId}   " + pageSql + errorcNumSql + "  AND schoolNum={school} ;";
        sqls.add(sql1);
        sqls.add(sql2);
        sqls.add(sql4);
        sqls.add(sql5);
        sqls.add(sql6);
        sqls.add(sql7);
        sqls.add(sql8);
        sqls.add(sql_tag);
        sqls.add(sql9);
        sqls.add(sql90);
        sqls.add(sql91);
        sqls.add(deleteSql);
        sqls.add("UPDATE illegalimage SET studentId={newStudentId},examinationRoomNum={newExamroomNum}  WHERE studentId={oldstudentId}   AND schoolNum={school}  AND examPaperNum={exampaperNum} ");
        if (page == 1) {
            sqls.add("UPDATE illegal SET studentId={newStudentId},examinationRoomNum={newExamroomNum}  WHERE examPaperNum={exampaperNum}  AND studentId={oldstudentId}  AND schoolNum={school}  ");
            this.log.info("method:modifyExamroomAndExamineeNum---sql10: UPDATE illegal SET studentId={newStudentId},examinationRoomNum={newExamroomNum}  WHERE examPaperNum={exampaperNum}  AND studentId={oldstudentId}  AND schoolNum={school}  ");
        }
        this.log.info("method:modifyExamroomAndExamineeNum---sql--" + sql1 + "sql2--" + sql2 + "sq4l--" + sql4 + "sql5--" + sql5 + "sq6: " + sql6 + " sql7: " + sql7 + " sql8: " + sql8);
        this.log.info("method:modifyExamroomAndExamineeNum---sql9: " + sql9 + "deleteSql: " + deleteSql + ",sql90:" + sql90 + ",sql_tag:" + sql_tag + ",sql91:" + sql91);
        this.dao2._batchExecute(sqls, args);
        return 1;
    }

    public List<RegExaminee> getPageByPosAndNegMark(String exampaperNum, String oldstudentId, String posAndNegMark, String oldExamroomId, String schoolNum, String examroomornot) {
        String examroomStr = "";
        if (null != examroomornot && examroomornot.equals("0") && null != oldExamroomId && !oldExamroomId.equals("") && !oldExamroomId.equals("-1")) {
            examroomStr = "AND examinationRoomNum={oldExamroomId}   ";
        }
        String sql = "SELECT *  FROM regexaminee   WHERE  1=1    " + examroomStr + "AND examPaperNum={exampaperNum}    AND schoolNum={schoolNum}   AND cNum={posAndNegMark} ";
        Map args = StreamMap.create().put("oldExamroomId", (Object) oldExamroomId).put("exampaperNum", (Object) exampaperNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("posAndNegMark", (Object) posAndNegMark);
        List _queryBeanList = this.dao2._queryBeanList(sql, RegExaminee.class, args);
        if (_queryBeanList != null && _queryBeanList.size() >= 2) {
            return _queryBeanList;
        }
        return null;
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public void updateNumStat(String stat, String exam, String subject, String testCenter, String grade, String examroom, String examroomornot, String examPaperNum) {
        String sql = "UPDATE correctstatus SET numStatus={stat}  WHERE examPaperNum={exampaperNum}   ";
        if (null != examroom && examroomornot.equals("0")) {
            sql = sql + " AND examnitionRoom={examroom}   ";
        } else if (null != testCenter && !"".equals(testCenter) && !"-1".equals(testCenter)) {
            sql = sql + "AND testingCentreId={testCenter}   ";
        }
        Map args = StreamMap.create().put("stat", (Object) stat).put("exampaperNum", (Object) examPaperNum).put("examroom", (Object) examroom).put("testCenter", (Object) testCenter);
        this.dao2._execute(sql, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public Integer getNcount(String examPaperNum, String examroom, String examroomornot, String testCenter) {
        String sql = "SELECT count(1) FROM cantrecognized WHERE examPaperNum={exampaperNum}   ";
        if (null != examroom && !"".equals(examroom) && examroomornot.equals("0")) {
            sql = sql + " AND examinationRoomNum={examroom}   ";
        } else if (null != testCenter && !"".equals(testCenter) && !"-1".equals(testCenter)) {
            sql = sql + "AND testingCentreId={testCenter}   ";
        }
        Map args = StreamMap.create().put("exampaperNum", (Object) examPaperNum).put("examroom", (Object) examroom).put("testCenter", (Object) testCenter);
        return this.dao2._queryInt(sql, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public void setNoCorrectStat(String exam, String subject, String grade, int pagecount, String examroom, String examroomornot, String testCenter, String examPaperNum) {
        String str;
        if (pagecount == 0) {
            str = "appendStatus={STATUS_CORRECT_COMPLETE} ";
        } else {
            str = "appendStatus={STATUS_CORRECT_DEFAULT} ";
        }
        String sql = "UPDATE correctstatus set " + str + " where examPaperNum={exampaperNum}    ";
        if (null != examroom && !"".equals(examroom) && examroomornot.equals("0")) {
            sql = sql + " AND examnitionRoom={examroom}   ";
        } else if (null != testCenter && !"".equals(testCenter) && !"-1".equals(testCenter)) {
            sql = sql + "AND testingCentreId={testCenter}   ";
        }
        Map args = new HashMap();
        args.put("STATUS_CORRECT_COMPLETE", "2");
        args.put("STATUS_CORRECT_DEFAULT", "0");
        args.put("exampaperNum", examPaperNum);
        args.put("examroom", examroom);
        args.put("testCenter", testCenter);
        this.dao2._execute(sql, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public boolean UpdateQuestionGroupTotalCount(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        List<Object> groupNumList = this.dao2._queryColList("SELECT groupNum from questiongroup where examPaperNum={examPaperNum} ", args);
        if (null == groupNumList) {
            return false;
        }
        for (Object groupNum : groupNumList) {
            args.put("groupNum", groupNum);
            Object count = this.dao2._queryObject("SELECT count(1) from task where examPaperNum={examPaperNum} and groupNum={groupNum} ", args);
            args.put("count", count);
            if (null != count) {
                this.dao2._execute(" update questiongroup set totalnum={count} where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
            }
        }
        return true;
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public Integer deleteImg(ExamineeNumError examineeNumError, String examroomornot) throws Exception {
        new ArrayList();
        int rtnVal = 0;
        if (examineeNumError != null) {
            try {
                Object[] params = {examineeNumError.getRegId(), examineeNumError.getExamPaperNum()};
                this.dao2.execute("CALL del_paperInfo_examineeNumCheck(?,?)", params);
                rtnVal = 1;
            } catch (Exception e) {
                this.log.info("考号校对--删除试卷库里信息", e);
                throw e;
            }
        }
        return Integer.valueOf(rtnVal);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public void deleteTeaAppeal(String regId, String studentId, String exampaperNum) throws Exception {
        try {
            Map args = StreamMap.create().put("regId", (Object) regId).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("exampaperNum", (Object) exampaperNum);
            this.dao2._execute("delete from teacherappeal where  exampaperNum={exampaperNum} and studentId={studentId} and regId={regId};", args);
        } catch (Exception e) {
            this.log.info("删除试卷-申诉数据：", e);
            throw e;
        }
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public List isExistTheExaminee(String examNum, String schoolNum, String gradenum, String roomNum, String inputExaminee, String examPaperNum, int page, String groupNum, String oldStudentId, String oldExamroomId, String id, String examroomornot) {
        List otherExamroomCNumList;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("gradenum", gradenum);
        args.put("roomNum", roomNum);
        args.put("inputExaminee", inputExaminee);
        args.put("page", Integer.valueOf(page));
        args.put("exampaperNum", examPaperNum);
        this.log.info(" 考场验证 考场表sql----SELECT  id examinationRoomNum,examinationRoomNum eRoomNum   FROM examinationroom   WHERE examNum={examNum}   AND schoolNum={schoolNum}   AND gradeNum={gradenum}   AND examinationRoomNum={roomNum} ");
        new ArrayList();
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT  id examinationRoomNum,examinationRoomNum eRoomNum   FROM examinationroom   WHERE examNum={examNum}   AND schoolNum={schoolNum}   AND gradeNum={gradenum}   AND examinationRoomNum={roomNum} ", Examinationroom.class, args);
        if (_queryBeanList != null && _queryBeanList.size() == 1) {
            args.put("roomList0", ((Examinationroom) _queryBeanList.get(0)).getExaminationRoomNum());
            this.log.info(" 考号验证考号表sql2：SELECT en.id,en.examinationRoomNum,er.examinationRoomNum eRoomNum,er.examinationRoomName,en.studentID,en.examineeNum,stu.studentName   FROM examinationnum en    LEFT JOIN student stu   ON en.studentID=stu.studentId   LEFT JOIN examinationroom er   ON en.examinationRoomNum=er.id   WHERE en.examNum={examNum}  AND en.schoolNum={schoolNum}   AND en.gradeNum={gradenum}   AND en.examinationRoomNum={roomList0}   AND en.examineeNum={inputExaminee}   AND stu.schoolNum={schoolNum}   AND stu.gradeNum={gradenum} ");
            new ArrayList();
            List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT en.id,en.examinationRoomNum,er.examinationRoomNum eRoomNum,er.examinationRoomName,en.studentID,en.examineeNum,stu.studentName   FROM examinationnum en    LEFT JOIN student stu   ON en.studentID=stu.studentId   LEFT JOIN examinationroom er   ON en.examinationRoomNum=er.id   WHERE en.examNum={examNum}  AND en.schoolNum={schoolNum}   AND en.gradeNum={gradenum}   AND en.examinationRoomNum={roomList0}   AND en.examineeNum={inputExaminee}   AND stu.schoolNum={schoolNum}   AND stu.gradeNum={gradenum} ", Examinationnum.class, args);
            args.put("ExaminationRoomNum0", ((Examinationnum) _queryBeanList2.get(0)).getExaminationRoomNum());
            args.put("StudentID0", ((Examinationnum) _queryBeanList2.get(0)).getStudentID());
            if (_queryBeanList2 != null && _queryBeanList2.size() == 1) {
                List<RegExaminee> regOldList = authDataExistsFromRegExamineeList("", examPaperNum, oldStudentId, schoolNum, page, groupNum, oldExamroomId, null, null, null, examroomornot);
                List<RegExaminee> regNewList2 = authDataExistsFromRegExamineeList("", examPaperNum, ((Examinationnum) _queryBeanList2.get(0)).getStudentID().toString(), schoolNum, page, null, ((Examinationnum) _queryBeanList2.get(0)).getExaminationRoomNum(), regOldList, "T", null, examroomornot);
                String pageSql = "  AND page={page}  ";
                if (regNewList2 != null && regNewList2.size() > 0) {
                    List<RegExaminee> regNewList = authDataExistsFromRegExamineeList("", examPaperNum, ((Examinationnum) _queryBeanList2.get(0)).getStudentID().toString(), schoolNum, page, regNewList2.get(0).getcNum() + "", ((Examinationnum) _queryBeanList2.get(0)).getExaminationRoomNum(), null, null, null, examroomornot);
                    args.put("regNewList0", Integer.valueOf(regNewList.get(0).getPage()));
                    args.put("regNewList1", Integer.valueOf(regNewList.get(1).getPage()));
                    if (regNewList != null && regNewList.size() == 2) {
                        pageSql = "  AND (page={regNewList0} OR page={regNewList1})  ";
                    }
                } else if (regOldList != null && regOldList.size() == 2) {
                    pageSql = "  AND (page={regNewList0} OR page={regNewList1})  ";
                }
                ((Examinationnum) _queryBeanList2.get(0)).setExt3("F");
                if (groupNum != null && !"".equals(groupNum) && (otherExamroomCNumList = authCNumInOtherExamroom(examPaperNum, groupNum, oldExamroomId, id, examroomornot)) != null) {
                    ((Examinationnum) _queryBeanList2.get(0)).setExt3("T");
                    ((Examinationnum) _queryBeanList2.get(0)).setExt4("存在" + otherExamroomCNumList.size() + "个相同试卷组号的试卷");
                }
                String regexamroomStr = "";
                if (examroomornot.equals("0")) {
                    regexamroomStr = " AND examinationRoomNum={ExaminationRoomNum0} ";
                }
                String sql3 = "SELECT *  FROM regexaminee   WHERE examPaperNum={exampaperNum}    AND schoolNum={schoolNum}   AND studentId={StudentID0}    " + regexamroomStr + pageSql;
                this.log.info("考号验证reg表sql3：" + sql3);
                List<?> _queryBeanList3 = this.dao2._queryBeanList(sql3, RegExaminee.class, args);
                if (groupNum != null && !"".equals(groupNum)) {
                    if (_queryBeanList3 != null && _queryBeanList3.size() > 0) {
                        ((Examinationnum) _queryBeanList2.get(0)).setExt2(((RegExaminee) _queryBeanList3.get(0)).getcNum() + "");
                        ((Examinationnum) _queryBeanList2.get(0)).setExt1("T");
                    } else {
                        ((Examinationnum) _queryBeanList2.get(0)).setExt1("F");
                    }
                } else if (_queryBeanList3 != null && _queryBeanList3.size() == 1) {
                    ((Examinationnum) _queryBeanList2.get(0)).setExt1("T");
                } else {
                    ((Examinationnum) _queryBeanList2.get(0)).setExt1("F");
                }
                return _queryBeanList2;
            }
            return null;
        }
        return null;
    }

    public List authCNumInOtherExamroom(String examPaperNum, String cNum, String examRoomId, String id, String examroomornot) {
        String examroomStr2 = "";
        if (examroomornot.equals("0")) {
            examroomStr2 = " LEFT JOIN examinationroom er ON reg.examinationRoomNum=er.id";
        }
        String sql = "SELECT  DISTINCT  reg.studentId,reg.cNum,reg.examPaperNum  FROM (SELECT  DISTINCT examinationRoomNum,cNum,studentId,id,examPaperNum   FROM regexaminee  WHERE 1=1 AND cNum={cNum}  AND id!={id}   AND examPaperNum={exampaperNum} AND examinationRoomNum<>{examRoomId} ) reg  " + examroomStr2;
        Map args = new HashMap();
        args.put("cNum", cNum);
        args.put("id", id);
        args.put("exampaperNum", examPaperNum);
        args.put("examRoomId", examRoomId);
        List list = this.dao2._queryBeanList(sql, RegExaminee.class, args);
        if (list != null && list.size() > 0) {
            return list;
        }
        return null;
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public String getpaintMode(String exam, String subject, String grade) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade).put("subject", (Object) subject);
        return this.dao2._queryStr("SELECT paintMode  FROM exampaper WHERE examNum={exam} and gradeNum={grade} and subjectNum={subject} ", args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public List<AjaxData> getsubType(String subject) {
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("data_subjectType", Const.data_subjectType);
        return this.dao2._queryBeanList("SELECT sub.subjectType num,dat.`name` name FROM(SELECT subjectType FROM `subject` WHERE subjectNum={subject} ) sub   LEFT JOIN(  SELECT id,category,isDefault,type,`name`,`value`,orderNum,isLock,isDelete  FROM `data` WHERE  type={data_subjectType} )dat ON sub.subjectType=dat.`value`", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public String getstuType(String exam, String gradeNum, String testCenter, String examineeNum) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("testCenter", (Object) testCenter).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("examineeNum", (Object) examineeNum);
        return this.dao2._queryStr("SELECT cl.studentType FROM (SELECT studentId FROM examinationnum WHERE examNum={exam} AND testingCentreId={testCenter}  AND gradeNum ={gradeNum}  AND  examineeNum={examineeNum}  ) ex LEFT JOIN student st ON ex.studentId=st.id  LEFT JOIN class cl ON cl.id=st.classNum", args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public String getexamroomOrNot(String exam, String subject, String grade) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("subject", (Object) subject).put("grade", (Object) grade);
        return this.dao2._queryStr("SELECT scantype FROM exampaper WHERE examNum={exam} AND subjectNum={subject} AND gradeNum={grade} ", args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public String getexamScanType(String exam) {
        Map args = StreamMap.create().put("exam", (Object) exam);
        return this.dao2._queryStr("SELECT scantype FROM exam WHERE  examNum={exam}  ", args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public void cliptwo(ExamineeNumError examineeNumError, String examroomornot) {
        new ArrayList();
        List<RowArg> rowArgList = new ArrayList<>();
        Map args = new HashMap();
        args.put("RegId", examineeNumError.getRegId());
        args.put("ExamPaperNum", examineeNumError.getExamPaperNum());
        args.put("ExaminationRoomNum", examineeNumError.getExaminationRoomNum());
        args.put("SchoolNum", examineeNumError.getSchoolNum());
        args.put("InsertUser", examineeNumError.getInsertUser());
        args.put("InsertDate", examineeNumError.getInsertDate());
        if (examineeNumError != null) {
            List idlist = this.dao2._queryColList("SELECT /* shard_host_HG=Write */ id FROM score WHERE examPaperNum={ExamPaperNum} AND  regId={RegId} UNION ALL SELECT id FROM objectivescore WHERE examPaperNum={ExamPaperNum} AND regId={RegId} ", args);
            for (int i = 0; i < idlist.size(); i++) {
                String scoreId = String.valueOf(idlist.get(i));
                Map args2 = new HashMap();
                args2.put("scoreId", scoreId);
                args.put("ExamPaperNum", examineeNumError.getExamPaperNum());
                rowArgList.add(new RowArg("DELETE  FROM scoreimage WHERE scoreId={scoreId} ", args2));
                rowArgList.add(new RowArg("DELETE  FROM questionimage   WHERE scoreId={scoreId} ", args2));
                rowArgList.add(new RowArg("DELETE FROM tag WHERE exampaperNum={ExamPaperNum} AND scoreId={scoreId} ", args2));
                rowArgList.add(new RowArg("DELETE FROM task WHERE exampaperNum={ExamPaperNum} AND scoreId={scoreId} ", args2));
                rowArgList.add(new RowArg("DELETE FROM markerror WHERE exampaperNum={ExamPaperNum} AND scoreId={scoreId} ", args2));
                rowArgList.add(new RowArg("DELETE FROM remark WHERE exampaperNum={ExamPaperNum}  AND scoreId={scoreId} ", args2));
                rowArgList.add(new RowArg("DELETE FROM remarkimg WHERE scoreId={scoreId} ", args2));
            }
            String examroomStr1 = "";
            String examroomStr2 = "";
            if (examroomornot.equals("0") && !"-1".equals(examineeNumError.getExaminationRoomNum())) {
                examroomStr1 = ",examinationRoomNum";
                examroomStr2 = " ,{ExaminationRoomNum} ";
            }
            int count = this.dao2._queryInt("SELECT count(1) FROM cantrecognized WHERE examPaperNum={ExamPaperNum} AND regId={RegId} ", args).intValue();
            if (count == 0) {
                String sql_17 = "INSERT into cantrecognized (examPaperNum,regId" + examroomStr1 + ",schoolNum,insertUser,insertDate,isDelete,status) VALUES({ExamPaperNum},{RegId} " + examroomStr2 + ",{SchoolNum},{InsertUser},{InsertDate} ,'F','0')";
                rowArgList.add(new RowArg(sql_17, args));
            }
            rowArgList.add(new RowArg("DELETE FROM objectivescore WHERE examPaperNum={ExamPaperNum} AND  regId={RegId}  ", args));
            rowArgList.add(new RowArg("DELETE FROM examinationnumimg WHERE  regId={RegId}  ", args));
            rowArgList.add(new RowArg("DELETE FROM examineenumerror WHERE  regId={RegId}  ", args));
            rowArgList.add(new RowArg("DELETE FROM illegal WHERE examPaperNum={ExamPaperNum} AND  regId={RegId}  ", args));
            rowArgList.add(new RowArg("DELETE FROM illegalimage WHERE  regId={RegId}  ", args));
            rowArgList.add(new RowArg("DELETE FROM exampapertypeimage WHERE  regId={RegId} ", args));
            rowArgList.add(new RowArg("DELETE FROM score WHERE examPaperNum=ExamPaperNum} AND  regId={RegId}  ", args));
            this.log.info("sql2---DELETE FROM score WHERE examPaperNum=ExamPaperNum} AND  regId={RegId}  ");
            this.log.info("sql3---DELETE FROM objectivescore WHERE examPaperNum={ExamPaperNum} AND  regId={RegId}  ");
            this.log.info("sql4---DELETE FROM examinationnumimg WHERE  regId={RegId}  ");
            this.dao2._batchExecute(rowArgList);
        }
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public void cliptwo_qtype(ExamineeNumError examineeNumError, String examroomornot, String qtype, String insertNum, String groupNum, String reason) {
        String regId = examineeNumError.getRegId();
        try {
            synchronized (regIdLock) {
                if (!lockRegidList.contains(regId)) {
                    lockRegidList.add(regId);
                    cliptwo_qtype_sub(examineeNumError, examroomornot, qtype, insertNum, groupNum, reason);
                    synchronized (regIdLock) {
                        lockRegidList.remove(regId);
                    }
                    return;
                }
                synchronized (regIdLock) {
                    lockRegidList.remove(regId);
                }
            }
        } catch (Throwable th) {
            synchronized (regIdLock) {
                lockRegidList.remove(regId);
                throw th;
            }
        }
    }

    public void cliptwo_qtype_sub(ExamineeNumError examineeNumError, String examroomornot, String qtype, String insertNum, String groupNum, String reason) {
        String idlistSql;
        List<RowArg> rowArgList = new ArrayList<>();
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("RegId", examineeNumError.getRegId());
        args.put("ExamPaperNum", examineeNumError.getExamPaperNum());
        args.put("TestingCentreId", examineeNumError.getTestingCentreId());
        args.put("InsertUser", examineeNumError.getInsertUser());
        args.put("InsertDate", examineeNumError.getInsertDate());
        args.put("reason", reason);
        if (examineeNumError != null) {
            String idlistSql_groupNum = "";
            if (!StrUtil.isEmpty(groupNum)) {
                idlistSql_groupNum = " AND questionNum={groupNum} ";
            }
            if (qtype.equals("0")) {
                idlistSql = "SELECT id FROM objectivescore WHERE  regId={RegId} " + idlistSql_groupNum;
            } else {
                idlistSql = "SELECT id FROM score WHERE   regId={RegId} " + idlistSql_groupNum + " UNION ALL SELECT id FROM objectivescore WHERE  regId={RegId}" + idlistSql_groupNum;
            }
            List idlist = this.dao2._queryColList(idlistSql, args);
            for (int i = 0; i < idlist.size(); i++) {
                String scoreId = String.valueOf(idlist.get(i));
                Map args1 = new HashMap();
                args1.put("scoreId", scoreId);
                String isShiping = "0";
                if (qtype.equals("1") || qtype.equals("-1") || qtype.equals("-11")) {
                    isShiping = StrUtil.isNotEmpty(this.dao2._queryStr("select id from testquestion where scoreId={scoreId} limit 1", args1)) ? "1" : "0";
                    if ("0".equals(isShiping)) {
                        rowArgList.add(new RowArg("DELETE FROM score WHERE   id={scoreId}  ", args1));
                    }
                }
                if (qtype.equals("0") || qtype.equals("-1") || qtype.equals("-10")) {
                    rowArgList.add(new RowArg("DELETE FROM objectivescore WHERE  id={scoreId}  ", args1));
                }
                rowArgList.add(new RowArg("DELETE  FROM scoreimage WHERE scoreId={scoreId}  ", args1));
                if ("0".equals(isShiping)) {
                    rowArgList.add(new RowArg("DELETE  FROM questionimage   WHERE scoreId={scoreId}  ", args1));
                }
                rowArgList.add(new RowArg("DELETE FROM markerror WHERE scoreId={scoreId}  ", args1));
                rowArgList.add(new RowArg("DELETE FROM objitem WHERE scoreid={scoreId}  ", args1));
                if ("0".equals(isShiping)) {
                    rowArgList.add(new RowArg("DELETE FROM task WHERE  scoreId={scoreId}   ", args1));
                    rowArgList.add(new RowArg("DELETE FROM remark WHERE  scoreId={scoreId}  ", args1));
                }
            }
            args.put("stat1", "0");
            rowArgList.add(new RowArg("DELETE FROM clippagemark WHERE regid={RegId}   ", args));
            String examroomStr1 = "";
            String examroomStr2 = "";
            if (examroomornot.equals("0") && !"-1".equals(examineeNumError.getExaminationRoomNum())) {
                examroomStr1 = ",examinationRoomNum";
                examroomStr2 = " ,'" + examineeNumError.getExaminationRoomNum() + "' ";
            }
            String countSql = "SELECT count(1) FROM cantrecognized WHERE  regId={RegId}  AND status<>'1'";
            int count = this.dao2._queryInt(countSql, args).intValue();
            if (count == 0) {
                String countSql1 = "SELECT count(1) FROM cantrecognized WHERE  regId={RegId}  AND status<>'1'";
                int count1 = this.dao2._queryInt(countSql1, args).intValue();
                if (count1 == 0) {
                    RegExaminee re = (RegExaminee) this.dao2._queryBean("SELECT schoolNum,examinationRoomNum,testingCentreId FROM regexaminee WHERE id={RegId} ", RegExaminee.class, args);
                    String sql_17 = "INSERT into cantrecognized (examPaperNum,regId" + examroomStr1 + ",testingCentreId,insertUser,insertDate,isDelete,status,stat,reason) VALUES({ExamPaperNum},{RegId} " + examroomStr2 + ",{TestingCentreId_re},{InsertUser},{InsertDate},'F','0',{stat1} ,{reason} ) ";
                    args.put("TestingCentreId_re", re.getTestingCentreId());
                    rowArgList.add(new RowArg(sql_17, args));
                }
                rowArgList.add(new RowArg("DELETE FROM examinationnumimg WHERE  regId={RegId}   ", args));
                rowArgList.add(new RowArg("DELETE FROM examineenumerror WHERE  regId={RegId}   ", args));
                rowArgList.add(new RowArg("DELETE FROM illegalimage WHERE  regId={RegId}   ", args));
                rowArgList.add(new RowArg("DELETE FROM exampapertypeimage WHERE  regId={RegId}   ", args));
            } else {
                rowArgList.add(new RowArg("UPDATE cantrecognized SET stat='0',reason=concat(reason,{reason} ) WHERE  regId={RegId}  ", args));
                rowArgList.add(new RowArg("DELETE FROM examinationnumimg WHERE  regId={RegId}   ", args));
                rowArgList.add(new RowArg("DELETE FROM examineenumerror WHERE  regId={RegId}   ", args));
                rowArgList.add(new RowArg("DELETE FROM illegalimage WHERE  regId={RegId}   ", args));
                rowArgList.add(new RowArg("DELETE FROM exampapertypeimage WHERE  regId={RegId}   ", args));
            }
            this.log.info("sql4---DELETE FROM examinationnumimg WHERE  regId={RegId}   ");
            if (rowArgList.size() > 0) {
                this.dao2._batchExecute(rowArgList, 100);
            }
        }
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public String getScantype(String exampaperNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryStr("SELECT scanType FROM exampaper WHERE examPaperNum={exampaperNum} ", args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public String getcrosspage(String questionNum) {
        Map args = StreamMap.create().put("questionNum", (Object) questionNum);
        return this.dao2._queryStr("SELECT cross_page FROM define WHERE id={questionNum}  UNION ALL SELECT cross_page FROM subdefine WHERE id={questionNum} ", args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public Integer checkclip(String regId) {
        Map args = StreamMap.create().put("regId", (Object) regId);
        return this.dao2._queryInt("SELECT count(1) FROM studentpaperimage WHERE regId={regId} ", args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public String gettemplateType(String exampaperNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryStr("SELECT templateType FROM exampaper WHERE examPaperNum={exampaperNum} ", args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public String getpagelist(String examNum, String gradeNum, String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao2._queryStr("SELECT totalPage FROM exampaper WHERE examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public void batchclip(String exampaperNum, String page, String regid, String examroomornot, String examroomNum, String user, String date, String qtype, String school, String reason, String str_questionNum) {
        try {
            synchronized (regIdLock) {
                if (!lockRegidList.contains(regid)) {
                    lockRegidList.add(regid);
                    batchclip_sub(exampaperNum, page, regid, examroomornot, examroomNum, user, date, qtype, school, reason, str_questionNum);
                    synchronized (regIdLock) {
                        lockRegidList.remove(regid);
                    }
                    return;
                }
                synchronized (regIdLock) {
                    lockRegidList.remove(regid);
                }
            }
        } catch (Throwable th) {
            synchronized (regIdLock) {
                lockRegidList.remove(regid);
                throw th;
            }
        }
    }

    public void batchclip_sub(String exampaperNum, String page, String regid, String examroomornot, String examroomNum, String user, String date, String qtype, String school, String reason, String str_questionNum) {
        List<RowArg> rowArgList = new ArrayList<>();
        Map args = new HashMap();
        args.put("regid", regid);
        args.put("str_questionNum", str_questionNum);
        args.put("exampaperNum", exampaperNum);
        args.put("user", user);
        args.put("date", date);
        args.put("reason", reason);
        List<String> regIdList = new ArrayList<>();
        String index = "0";
        for (int i1 = 0; i1 < regIdList.size(); i1++) {
            if (regid.equals(regIdList.get(i1))) {
                index = "1";
            }
        }
        regIdList.add(regid);
        if (index.equals("0")) {
            String idlistSql = "";
            String strSql_questionNum = "";
            if (qtype.equals("1")) {
                idlistSql = "SELECT id FROM score WHERE   regId={regid} ";
            }
            if (qtype.equals("0")) {
                idlistSql = "SELECT id FROM objectivescore WHERE  regId={regid} ";
            }
            if (!StrUtil.isEmpty(str_questionNum) && !"-1".equals(str_questionNum)) {
                strSql_questionNum = " AND questionNum={str_questionNum} ";
            }
            if (qtype.equals("-1")) {
                idlistSql = "SELECT id FROM score WHERE   regId={regid} " + strSql_questionNum + " UNION ALL SELECT id FROM objectivescore WHERE  regId={regid} " + strSql_questionNum;
            }
            List idlist = this.dao2._queryColList(idlistSql, args);
            for (int i = 0; i < idlist.size(); i++) {
                String scoreId = String.valueOf(idlist.get(i));
                Map args1 = new HashMap();
                args1.put("scoreId", scoreId);
                String isShiping = "0";
                int count = this.dao2._queryInt("SELECT COUNT(1) FROM task WHERE  scoreId={scoreId}  AND status!='F'  ", args1).intValue();
                if (count == 0) {
                    if (qtype.equals("1") || qtype.equals("-1")) {
                        isShiping = StrUtil.isNotEmpty(this.dao2._queryStr("select id from testquestion where scoreId={scoreId} limit 1", args1)) ? "1" : "0";
                        if ("0".equals(isShiping)) {
                            rowArgList.add(new RowArg("DELETE FROM score WHERE   id={scoreId}   ", args1));
                        }
                    }
                    if (qtype.equals("0") || qtype.equals("-1")) {
                        rowArgList.add(new RowArg("DELETE FROM objectivescore WHERE   id={scoreId}   ", args1));
                    }
                    rowArgList.add(new RowArg("DELETE  FROM scoreimage WHERE scoreId={scoreId}  ", args1));
                    if ("0".equals(isShiping)) {
                        rowArgList.add(new RowArg("DELETE  FROM questionimage   WHERE scoreId={scoreId} ", args1));
                    }
                    rowArgList.add(new RowArg("DELETE FROM markerror WHERE  scoreId={scoreId} ", args1));
                    rowArgList.add(new RowArg("DELETE FROM objitem WHERE scoreid={scoreId} ", args1));
                }
                if ("0".equals(isShiping)) {
                    rowArgList.add(new RowArg("DELETE FROM task WHERE  scoreId={scoreId}  AND status='F'  AND userNum!='3'", args1));
                }
            }
            String statStr = "";
            String statStr1 = "";
            String stat1 = "";
            if (qtype.equals("0")) {
                statStr = " AND stat!='1'";
                statStr1 = " AND stat='1'";
                stat1 = "1";
            }
            if (qtype.equals("1")) {
                statStr = " AND stat!='2'";
                statStr1 = " AND stat='2'";
                stat1 = "2";
            }
            if (qtype.equals("-1")) {
                stat1 = "0";
            }
            args.put("stat1", stat1);
            rowArgList.add(new RowArg("DELETE FROM clippagemark WHERE regid={regid}   ", args));
            String countSql = "SELECT count(1) FROM cantrecognized WHERE  regId={regid} " + statStr + " AND status<>'1'";
            int count2 = this.dao2._queryInt(countSql, args).intValue();
            if (count2 == 0) {
                String countSql1 = "SELECT count(1) FROM cantrecognized WHERE  regId={regid} " + statStr1 + " AND status<>'1'";
                int count1 = this.dao2._queryInt(countSql1, args).intValue();
                if (count1 == 0) {
                    RegExaminee re = (RegExaminee) this.dao2._queryBean("SELECT schoolNum,examinationRoomNum,testingCentreId FROM regexaminee WHERE id={regid} ", RegExaminee.class, args);
                    args.put("ExaminationRoomNum", re.getExaminationRoomNum());
                    args.put("SchoolNum", Integer.valueOf(re.getSchoolNum()));
                    args.put("TestingCentreId", re.getTestingCentreId());
                    rowArgList.add(new RowArg("INSERT into cantrecognized (examPaperNum,regId,examinationRoomNum,schoolNum,testingCentreId,insertUser,insertDate,isDelete,status,stat,reason) VALUES({exampaperNum} ,{regid} ,{ExaminationRoomNum},{SchoolNum},{TestingCentreId},{user},{date},'F','0',{stat1},{reason} )", args));
                }
            } else {
                rowArgList.add(new RowArg("UPDATE cantrecognized SET stat='0',reason=concat(reason,{reason} ) WHERE  regId={regid}  AND status<>'1'", args));
                rowArgList.add(new RowArg("DELETE FROM examinationnumimg WHERE  regId={regid}   ", args));
                rowArgList.add(new RowArg("DELETE FROM examineenumerror WHERE  regId={regid}   ", args));
                rowArgList.add(new RowArg("DELETE FROM illegalimage WHERE  regId={regid}   ", args));
                rowArgList.add(new RowArg("DELETE FROM exampapertypeimage WHERE  regId={regid}   ", args));
            }
            if (qtype.equals("-1")) {
                rowArgList.add(new RowArg("DELETE FROM examinationnumimg WHERE  regId={regid}   ", args));
                rowArgList.add(new RowArg("DELETE FROM examineenumerror WHERE  regId={regid}   ", args));
                rowArgList.add(new RowArg("DELETE FROM illegalimage WHERE  regId={regid}   ", args));
                rowArgList.add(new RowArg("DELETE FROM exampapertypeimage WHERE  regId={regid}   ", args));
            }
        }
        this.dao2._batchExecute(rowArgList, 100);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public void clip_objitem(String exampaperNum, String examroom, String regId, String schoolNum, String examroomornot, String user, String date) {
        List<RowArg> rowArgList = new ArrayList<>();
        Map args = new HashMap();
        args.put("regId", regId);
        args.put("examroom", examroom);
        args.put("exampaperNum", exampaperNum);
        args.put("SchoolNum", schoolNum);
        args.put("user", user);
        args.put("date", date);
        List idlist = this.dao2._queryColList("SELECT id FROM objectivescore WHERE  regId={regId} ", args);
        for (int i = 0; i < idlist.size(); i++) {
            String scoreId = String.valueOf(idlist.get(i));
            Map args1 = new HashMap();
            args1.put("scoreId", scoreId);
            int count = this.dao2._queryInt("SELECT COUNT(1) FROM task WHERE  scoreId={scoreId}  AND status!='F' ", args1).intValue();
            if (count == 0) {
                rowArgList.add(new RowArg("DELETE FROM objectivescore WHERE  id={scoreId}   ", args1));
                rowArgList.add(new RowArg("DELETE  FROM scoreimage WHERE scoreId={scoreId}  ", args1));
                rowArgList.add(new RowArg("DELETE  FROM questionimage   WHERE scoreId={scoreId} ", args1));
                rowArgList.add(new RowArg("DELETE FROM markerror WHERE scoreId={scoreId} ", args1));
                rowArgList.add(new RowArg("DELETE FROM objitem WHERE scoreid={scoreId} ", args1));
            }
            rowArgList.add(new RowArg("DELETE FROM task WHERE  scoreId={scoreId}  AND status='F'  AND userNum!='3' ", args1));
        }
        args.put("stat1", "1");
        rowArgList.add(new RowArg("DELETE FROM choosenamerecord WHERE regId={regId} ", args));
        String examroomStr1 = "";
        String examroomStr2 = "";
        if (examroomornot.equals("0") && examroom.equals("-1")) {
            examroomStr1 = ",examinationRoomNum";
            examroomStr2 = " ,{examroom} ";
        }
        String countSql = "SELECT count(1) FROM cantrecognized WHERE regId={regId}  AND stat!='1'";
        int count2 = this.dao2._queryInt(countSql, args).intValue();
        if (count2 == 0) {
            String countSql1 = "SELECT count(1) FROM cantrecognized WHERE  regId={regId}  AND stat='1' ";
            int count1 = this.dao2._queryInt(countSql1, args).intValue();
            if (count1 == 0) {
                RegExaminee re = (RegExaminee) this.dao2._queryBean("SELECT schoolNum,examinationRoomNum,testingCentreId FROM regexaminee WHERE id={regId} ", RegExaminee.class, args);
                args.put("TestingCentreId", re.getTestingCentreId());
                String sql_17 = "INSERT into cantrecognized (examPaperNum,regId" + examroomStr1 + ",schoolNum,testingCentreId,insertUser,insertDate,isDelete,status,stat) VALUES({exampaperNum} ,{regId} " + examroomStr2 + ",{SchoolNum} ,{TestingCentreId} ,{user},{date},'F','0',{stat1} )";
                rowArgList.add(new RowArg(sql_17, args));
            }
        } else {
            rowArgList.add(new RowArg("UPDATE cantrecognized SET stat='0' WHERE  regId={regId} ", args));
            rowArgList.add(new RowArg("DELETE FROM examinationnumimg WHERE  regId={regId}  ", args));
            rowArgList.add(new RowArg("DELETE FROM examineenumerror WHERE  regId={regId}   ", args));
            rowArgList.add(new RowArg("DELETE FROM illegal WHERE   regId={regId}   ", args));
            rowArgList.add(new RowArg("DELETE FROM illegalimage WHERE  regId={regId}   ", args));
            rowArgList.add(new RowArg("DELETE FROM exampapertypeimage WHERE  regId={regId}   ", args));
        }
        this.log.info("sql4---DELETE FROM examinationnumimg WHERE  regId={regId}  ");
        this.dao2._batchExecute(rowArgList);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public List getregIdList(String exampaperNum, String stuid, String page, String twopage) {
        if (null == twopage) {
        }
        String pageStr = "";
        if (!page.equals("-1")) {
            pageStr = " AND page={page} ";
        }
        String regIdSql = "SELECT id FROM regexaminee WHERE examPaperNum={exampaperNum}  AND studentId={stuid}  " + pageStr;
        Map args = StreamMap.create().put("page", (Object) page).put("exampaperNum", (Object) exampaperNum).put("stuid", (Object) stuid);
        return this.dao2._queryColList(regIdSql, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public List<Score> stulist(String qtype, int examPaperNum, String examinationRoomNum, String studentName, String testCenter, String examroomornot) {
        String schoolStr;
        String examroom = "";
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (!examinationRoomNum.equals("-1") && examroomornot.equals("0")) {
            examroom = " and s.examinationRoomNum={examinationRoomNum} ";
        }
        if ("-1".equals(testCenter)) {
            schoolStr = "";
        } else {
            schoolStr = " AND s.testingCentreId={testCenter} ";
        }
        String stuNameStr = "";
        if (null != studentName && !"".equals(studentName) && !"null".equals(studentName) && !"undefined".equals(studentName)) {
            stuNameStr = " AND st.studentName={studentName} ";
        }
        String sql = "select  s.studentId ext1, s.examinationRoomNum ext2, s.page page from regexaminee s LEFT join student  st on s.studentid=st.id where s.examPaperNum={exampaperNum}  " + stuNameStr + examroom + "  " + schoolStr + " GROUP BY s.studentId ";
        Map args = new HashMap();
        args.put("examinationRoomNum", examinationRoomNum);
        args.put("testCenter", testCenter);
        args.put("studentName", studentName);
        args.put("exampaperNum", Integer.valueOf(examPaperNum));
        return this.dao2._queryBeanList(sql, Score.class, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public String getdoubleFaced(String exampaperNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryStr("SELECT doubleFaced FROM exampaper WHERE examPaperNum={exampaperNum} ", args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public List getregIdlist(String regId, String exampaperNum) {
        Map args = StreamMap.create().put("regId", (Object) regId).put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryBeanList("SELECT reg.id FROM (SELECT cNum FROM regexaminee WHERE id={regId} )c  LEFT JOIN  (SELECT examPaperNum,studentId,cNum,id FROM regexaminee )reg  ON reg.cNum=c.cNum WHERE reg.examPaperNum={exampaperNum} ", RegExaminee.class, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public List getregIdlist2(String regId, String exampaperNum) {
        Map args = StreamMap.create().put("regId", (Object) regId).put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryBeanList("SELECT reg.id FROM (SELECT studentId FROM regexaminee WHERE id={regId} )c  LEFT JOIN  (SELECT examPaperNum,studentId,cNum,id FROM regexaminee )reg  ON reg.studentId=c.studentId WHERE reg.examPaperNum={exampaperNum} ", RegExaminee.class, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public List getPanFenData(String questionNum, String studentId) {
        Map args = StreamMap.create().put("questionNum", (Object) questionNum).put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryBeanList("select d.questionNum ext1,d.fullscore ext2,s.questionScore questionScore,t.userNum ext3,e.teacherName ext4,e.teacherNum ext5,  t.questionScore ext6,case when q.makType=0 then '-1' else q.judgetype end ext7,t.updateTime updateDate,t.status ext8 from score s  left join  task t on t.scoreId=s.id  left join  questiongroup_mark_setting q on q.groupNum=t.groupNum  left join ( select a.id,a.fullscore,a.questionNum from define a UNION select b.id,b.fullscore,b.questionNum from subdefine b ) d ON d.id = s.questionNum  left join  user u on u.id=t.insertUser  left join  teacher e on e.id=u.userId  where s.questionNum={questionNum} and s.studentId={studentId} and s.continued='F' ", Score.class, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public List getCaiJueData(String questionNum, String studentId) {
        Map args = StreamMap.create().put("questionNum", (Object) questionNum).put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryBeanList("select s.questionScore questionScore,if(r.insertUser='-2','dmj',u.realname) ext4,if(r.insertUser='-2','dmj',u.username) ext5,  r.questionScore ext6,r.insertDate updateDate,r.status ext8,r.insertUser from score s  inner join   remark r on r.scoreId=s.id and r.type='1'  left join  user u on u.id=r.insertUser  where s.questionNum={questionNum} and s.studentId={studentId} ", Score.class, args);
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public int getCantrecognizedCount(String exampaperNum, String testCenter, String examroom, String examroomornot) {
        String room_schoolStr;
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1") && examroomornot.equals("0")) {
            room_schoolStr = "AND examinationRoomNum={examroom} AND testingCentreId={testCenter}  ";
        } else {
            room_schoolStr = "AND testingCentreId={testCenter}  ";
        }
        String sql = "select count(1) from cantrecognized where examPaperNum={exampaperNum}   " + room_schoolStr;
        Map args = StreamMap.create().put("examroom", (Object) examroom).put("testCenter", (Object) testCenter).put("exampaperNum", (Object) exampaperNum);
        return this.dao2._queryInt(sql, args).intValue();
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public int getQueyeCount(String exampaperNum, String testCenter, String examroom, String examroomornot) {
        String sql = " SELECT sum(if(reg.page <> reg.totalPage,1,0)) regqueye FROM (select count(1) Page,p.totalPage totalPage from regexaminee r LEFT JOIN exampaper p on r.examPaperNum=p.examPaperNum LEFT JOIN student s on s.id=r.studentId WHERE r.scan_import='0' and r.examPaperNum={exampaperNum}  and r.testingCentreId={testCenter} ";
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1") && examroomornot.equals("0")) {
            sql = sql + "AND r.examinationRoomNum={examroom} ";
        }
        String sql2 = sql + " GROUP BY r.studentId ) reg ";
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("testCenter", (Object) testCenter).put("examroom", (Object) examroom);
        return this.dao2._queryInt(sql2, args).intValue();
    }

    @Override // com.dmj.service.examManagement.NoMarkCorrectService
    public User getUserById(String id) {
        StringBuffer userSql = new StringBuffer();
        userSql.append("select username,mobile,realname,schoolnum schoolNum from user ");
        userSql.append("where id={id} ");
        StringBuffer sql = new StringBuffer();
        sql.append("select u.*,sch.schoolName from (").append(userSql).append(") u ");
        sql.append("left join school sch on sch.id=u.schoolnum ");
        Map args = StreamMap.create().put("id", (Object) id);
        return (User) this.dao2._queryBean(sql.toString(), User.class, args);
    }
}
