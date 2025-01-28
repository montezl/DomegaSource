package com.dmj.serviceimpl.reportManagement;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.examManagement.ExamDAOImpl;
import com.dmj.daoimpl.reportManagement.ReportDaoImpl;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Class;
import com.dmj.domain.Define;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Examsetting;
import com.dmj.domain.Grade;
import com.dmj.domain.Gradelevel;
import com.dmj.domain.IndexIntegral;
import com.dmj.domain.MyReport;
import com.dmj.domain.QuestionType;
import com.dmj.domain.ReportParameter;
import com.dmj.domain.Resource;
import com.dmj.domain.RptHeader;
import com.dmj.domain.Student;
import com.dmj.domain.Studentlevel;
import com.dmj.domain.Subject;
import com.dmj.domain.User;
import com.dmj.service.analysisManagement.AnalysisService;
import com.dmj.service.reportManagement.G1allIndexService;
import com.dmj.service.reportManagement.ReportService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.analysisManagement.AnalysisServiceImpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.Const;
import com.dmj.util.config.Configuration;
import com.zht.db.DbUtils;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.io.File;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

/* loaded from: ReportServiceImpl.class */
public class ReportServiceImpl implements ReportService {
    public static final String isHistory_False = "F";
    public static SystemService system = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();
    ReportDaoImpl rpd = new ReportDaoImpl();
    ExamDAOImpl examDao = new ExamDAOImpl();
    Logger log = Logger.getLogger(getClass());
    private String isHistory = "F";
    private AnalysisService analy = (AnalysisService) ServiceFactory.getObject(new AnalysisServiceImpl());
    private G1allIndexService g = (G1allIndexService) ServiceFactory.getObject(new G1allIndexServiceimpl());

    @Override // com.dmj.service.reportManagement.ReportService
    public Map<String, Object> getClassRankCompareHeader(ReportParameter para) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) para.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) para.getGradeNum()).put(Const.EXPORTREPORT_studentType, (Object) para.getStudentType()).put(Const.EXPORTREPORT_classNum, (Object) para.getClassNum().replaceAll(Const.STRING_SEPERATOR, "','"));
        return this.dao._queryOrderMap("SELECT  r.classNum,r.className FROM (SELECT classNum,className FROM class WHERE schoolNum={schoolNum}  AND gradeNum={gradeNum}  AND studentType={studentType}) r WHERE r.classNum in ({classNum[]}) ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List<Object> getClassRankCompareHeader2(ReportParameter para) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) para.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) para.getGradeNum()).put(Const.EXPORTREPORT_studentType, (Object) para.getStudentType()).put(Const.EXPORTREPORT_classNum, (Object) para.getClassNum().replaceAll(Const.STRING_SEPERATOR, "','"));
        return this.dao._queryColList("SELECT r.className FROM (SELECT classNum,className FROM class WHERE schoolNum={schoolNum}  AND gradeNum={gradeNum}  AND studentType={studentType}) r WHERE r.classNum in ({classNum[]}) ", args);
    }

    public static void main(String[] args) {
        ReportParameter rp = new ReportParameter();
        rp.setClassNum("1,2,3,4");
        rp.setSchoolNum("152630");
        rp.setGradeNum("12");
        rp.setStudentType("1");
        List<Object> list = new ReportServiceImpl().getClassRankCompareHeader2(rp);
        for (Object obj : list) {
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List maxClaScore(String examNum, String SubjectNum, String gradeNum, String schoolNum, String classNum, String type, String stype, String stuSource, String isHistory, String fufen, String subCompose, String islevel) throws Exception {
        String sql;
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ getAllSubjectMaxScore_fufen(?,?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ getAllSubjectMaxScore(?,?,?,?,?,?,?,?,?,?)}";
        }
        List list = new ArrayList();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, SubjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, type.equals("null") ? "" : type);
                pstat.setString(7, stype.equals("null") ? "" : stype);
                pstat.setString(8, stuSource.equals("null") ? "" : stuSource);
                pstat.setString(9, subCompose.equals("null") ? "" : subCompose);
                pstat.setString(10, islevel.equals("null") ? "" : islevel);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                String preSjt = "";
                ArrayList arrayList = new ArrayList();
                new ArrayList();
                while (rs.next()) {
                    Object[] rowArr = new Object[count];
                    String curSjt = rs.getString(5);
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    if (preSjt.equals("") || preSjt.equals(curSjt)) {
                        arrayList.add(rowArr);
                    } else {
                        list.add(arrayList);
                        arrayList = new ArrayList();
                        arrayList.add(rowArr);
                    }
                    preSjt = curSjt;
                }
                list.add(arrayList);
                DbUtils.close(rs, pstat, conn);
            } catch (Exception e) {
                e.printStackTrace();
                DbUtils.close(rs, pstat, conn);
            }
            return list;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List<Class> getStudentScoreClass(String schoolNum, String gradeNum, String classNum, String exam) throws Exception {
        List<Class> list = new ArrayList<>();
        CallableStatement pstat = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = DbUtils.getConnection();
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ toTeacher_studentScoreClassRank_getClass(?,?,?,?)}");
                pstat.setString(1, exam);
                pstat.setString(2, schoolNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, classNum);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Class c = new Class();
                    c.setClassNum(rs.getString(1));
                    c.setClassName(rs.getString(2));
                    list.add(c);
                }
                DbUtils.close(rs, pstat, conn);
            } catch (Exception e) {
                e.printStackTrace();
                DbUtils.close(rs, pstat, conn);
            }
            return list;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x00d6, code lost:
    
        if (r14.getMoreResults() == false) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x00d9, code lost:
    
        r15 = r14.getResultSet();
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x00e9, code lost:
    
        if (r15.next() == false) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x00ec, code lost:
    
        r0 = new java.util.ArrayList();
        r20 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0108, code lost:
    
        if (r20 >= (r15.getMetaData().getColumnCount() + 1)) goto L39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0114, code lost:
    
        if (r15.getString(r20) != null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0117, code lost:
    
        r0.add("---");
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0135, code lost:
    
        r20 = r20 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0124, code lost:
    
        r0.add(r15.getString(r20));
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x013b, code lost:
    
        r0.add(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0148, code lost:
    
        r0.add(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0154, code lost:
    
        com.zht.db.DbUtils.close(r15, r14, r16);
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x00cc, code lost:
    
        if (r0.size() > 0) goto L9;
     */
    @Override // com.dmj.service.reportManagement.ReportService
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List getExpStudentScore(java.lang.String r5, java.lang.String r6, java.lang.String r7, java.lang.String r8, java.lang.String r9, java.lang.String r10, java.lang.String r11) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 386
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.dmj.serviceimpl.reportManagement.ReportServiceImpl.getExpStudentScore(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.util.List");
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List<Student> selectStudent(String exam, String gradeNum, String classNum, String schoolNum, String subjectType, String source) {
        String ex = "";
        String sch = "";
        String gra = "";
        String clas = "";
        String sub = "";
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            ex = ex + " AND examNum={exam} ";
        }
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sch = sch + " AND schoolNum={schoolNum} ";
        }
        if (null != gradeNum && !gradeNum.equals("") && !gradeNum.equals("-1")) {
            gra = gra + " AND gradeNum={gradeNum}";
        }
        if (null != classNum && !classNum.equals("") && !classNum.equals("-1")) {
            clas = clas + " AND classNum={classNum}";
        }
        if (null != subjectType && !subjectType.equals("") && !subjectType.equals("-1")) {
            sub = sub + " AND studentType={subjectType} ";
        }
        String sql = "SELECT  s.studentName,s.studentNum,sl.studentId FROM (SELECT studentId FROM studentlevel WHERE 1=1 " + ex + sch + gra + clas + sub + " AND subjectNum={subjectNum} AND statisticType='0' AND source={source} ) sl LEFT JOIN (SELECT id,studentName,studentId,studentNum FROM student ) s ON s.id = sl.studentId";
        Map args = StreamMap.create().put("exam", (Object) exam).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("subjectType", (Object) subjectType).put(Const.EXPORTREPORT_subjectNum, (Object) Const.subjectTotal).put("source", (Object) source);
        return this.dao._queryBeanList(sql, Student.class, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getStudentScore(String examNum, String gradeNum, String subjectNum, String schoolNum, String classNum, String statisticType, String studentType, String type, String stuSource, String examPaperNum) {
        new ArrayList();
        String sql = "SELECT s.studentId,t.studentName,s.totalScore,s.classRanking,s.gradeRanking FROM \t(SELECT studentId,totalScore,classRanking,gradeRanking FROM studentlevel   \tWHERE schoolNum={schoolNum}  AND gradeNum={gradeNum} AND classNum={classNum}  AND  examPaperNum={examPaperNum}  AND subjectNum={subjectNum}  AND  examNum={examNum}     AND statisticType={type}  AND studentType={studentType}  AND source={stuSource} ) s   LEFT JOIN (SELECT id,studentId,studentName FROM student WHERE  classNum={classNum}     ";
        if (null != stuSource && !"0".equals(stuSource)) {
            sql = sql + "AND source={stuSource}   ";
        }
        String sql2 = sql + "  ) t ON s.studentId=t.id  ORDER BY s.classRanking";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("examPaperNum", (Object) examPaperNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("type", (Object) type).put(Const.EXPORTREPORT_studentType, (Object) studentType).put("stuSource", (Object) stuSource);
        return this.dao._queryBeanList(sql2, Studentlevel.class, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public Integer addCollect(MyReport mr) {
        return this.rpd.addCollect(mr);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List queryMyReport(String loginNum, List now) {
        return this.rpd.queryMyReport(loginNum, now);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public void deleteMyReport(String loginNum, MyReport mr) {
        this.rpd.deleteMyReport(loginNum, mr);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public Integer queryOne(MyReport mr) {
        return this.rpd.queryOne(mr);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public Integer queryNull(String loginNum, List now) {
        return this.rpd.queryNull(loginNum, now);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List queryreportselect() {
        return this.dao.queryBeanList(" select keyNum num,keyName name from report_keyword  ", AjaxData.class);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public void runReportEngineAndExport(String birtHome, String srcName, String loginUserNum, String[] stuRptArr, String[] teachRptArr, String examNum, String gradeNum, String subjectNum) {
        this.rpd.runReportEngineAndExport(birtHome, srcName, loginUserNum, stuRptArr, teachRptArr, examNum, gradeNum, subjectNum);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List<Resource> getRptTypeName(String pnum) {
        return this.rpd.getRptTypeName(pnum);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List<AjaxData> getSubject(String exam, String grade) {
        return this.rpd.getSubject(exam, grade);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public Map<String, String> getSubject1(String exam, String grade) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade);
        return this.dao._queryOrderMap("SELECT DISTINCT s.subjectNum num,CAST(r.totalScore AS CHAR) ext1   FROM    (SELECT examPaperNum,subjectNum,jie,gradeNum,totalScore FROM exampaper   WHERE examNum={exam}  AND gradeNum={grade} ) r     LEFT JOIN `subject` s ON r.subjectNum=s.subjectNum   ORDER BY s.id ", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public String getSchauthor(String loginNum) {
        String sql;
        Map args2 = new HashMap();
        if ("-1".equals(loginNum) || "-2".equals(loginNum)) {
            sql = "select CONCAT('$',GROUP_CONCAT(DISTINCT id separator '$'),'$') from school ";
        } else {
            sql = "select GROUP_CONCAT(type) type from userposition where userNum={loginNum} ";
            Map args = StreamMap.create().put("loginNum", (Object) loginNum);
            String returnVal = this.dao._queryStr(sql, args);
            if (returnVal == null) {
                sql = "SELECT CONCAT('$',schoolNum,'$')  from user  where id={loginNum} ";
            } else if (returnVal.indexOf("0") != -1) {
                sql = "SELECT   CONCAT('$',GROUP_CONCAT(DISTINCT d.schoolNum separator '$'),'$') schoolNum  from(  select schoolNum from userposition where userNum={loginNum}    UNION  SELECT  schoolNum from schauthormanage where userId={loginNum}   union  SELECT schoolNum  from user where id={loginNum}   )d";
            }
            args2.put("loginNum", loginNum);
        }
        return this.dao._queryStr(sql, args2);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getClaTotalRankList(String examNum, String gradeNum, String schoolNum, String studentType, String type, String source, String isHistory, String subjectNum) {
        return this.rpd.getClaTotalRankList(examNum, gradeNum, schoolNum, studentType, type, source, isHistory, subjectNum);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getUpperLineData_old(String examNum, String sjt, String gradeNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String subOrClaLine, String subjectNum, String lineType, String classNum, String reCalcu) {
        List rtnist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            pstat = conn.prepareCall("{call /* shard_host_HG=Read */ upperToLine(?,?,?,?,?,?,?,?,?,?,?,?)}");
            pstat.setString(1, examNum);
            pstat.setString(2, sjt);
            pstat.setString(3, gradeNum);
            pstat.setString(4, schoolNum);
            pstat.setString(5, studentType);
            pstat.setString(6, graduationType);
            pstat.setString(7, stuSourceType);
            pstat.setString(8, subOrClaLine);
            pstat.setString(9, subjectNum);
            pstat.setString(10, lineType);
            pstat.setString(11, classNum);
            pstat.setString(12, reCalcu);
            pstat.executeQuery();
            rs = pstat.getResultSet();
            new HashMap();
            String subNum = "";
            String claId = "";
            Object[] objArr = new Object[7];
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            while (rs.next()) {
                int count = rs.getMetaData().getColumnCount();
                Object[] obj = new Object[count];
                for (int i = 0; i < obj.length; i++) {
                    obj[i] = rs.getObject(i + 1);
                }
                arrayList3.add(obj);
                String curClaId = String.valueOf(rs.getObject(4));
                String curSubNum = String.valueOf(rs.getObject(8));
                Object[] curObjArr = new Object[7];
                if (null != subOrClaLine && "0".equals(subOrClaLine)) {
                    if ("".equals(claId) || claId.equals(curClaId)) {
                        if ("".equals(subNum) || subNum.equals(curSubNum)) {
                            curObjArr[0] = rs.getObject(9);
                            curObjArr[1] = rs.getObject(8);
                            curObjArr[2] = studentType;
                            curObjArr[3] = rs.getObject(11);
                            curObjArr[4] = rs.getObject(14);
                            curObjArr[5] = rs.getObject(14);
                            curObjArr[6] = rs.getObject(10);
                            arrayList2.add(curObjArr);
                        } else {
                            arrayList.add(arrayList2);
                            arrayList2 = new ArrayList();
                            curObjArr[0] = rs.getObject(9);
                            curObjArr[1] = rs.getObject(8);
                            curObjArr[2] = studentType;
                            curObjArr[3] = rs.getObject(11);
                            curObjArr[4] = rs.getObject(14);
                            curObjArr[5] = rs.getObject(14);
                            curObjArr[6] = rs.getObject(10);
                            arrayList2.add(curObjArr);
                        }
                        claId = curClaId;
                        subNum = curSubNum;
                    }
                } else {
                    curObjArr[0] = rs.getObject(9);
                    curObjArr[1] = rs.getObject(8);
                    curObjArr[2] = studentType;
                    curObjArr[3] = rs.getObject(4);
                    curObjArr[4] = rs.getObject(4);
                    curObjArr[5] = rs.getObject(10);
                    curObjArr[6] = rs.getObject(5);
                    arrayList2.add(curObjArr);
                }
            }
            arrayList.add(arrayList2);
            rtnist.add(arrayList);
            rtnist.add(arrayList3);
            while (pstat.getMoreResults()) {
                ArrayList arrayList4 = new ArrayList();
                rs = pstat.getResultSet();
                int count2 = rs.getMetaData().getColumnCount();
                if (null != arrayList2 && arrayList2.size() > 0) {
                    while (rs.next()) {
                        Object[] obj2 = new Object[count2];
                        for (int i2 = 0; i2 < obj2.length; i2++) {
                            obj2[i2] = rs.getObject(i2 + 1);
                        }
                        arrayList4.add(obj2);
                    }
                }
                rtnist.add(arrayList4);
            }
            DbUtils.close(rs, pstat, conn);
            this.log.info("--getUpperLineData-" + rtnist.size());
            return rtnist;
        } catch (SQLException e) {
            DbUtils.close(rs, pstat, conn);
            return rtnist;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getUpperLineData(String examNum, String gradeNum, String teachUnit_s, String teachUnit, String studentType, String type, String source, String subCompose, String fufen) {
        List rtnist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            pstat = conn.prepareCall("{call /* shard_host_HG=Read */ upperToLine(?,?,?,?,?,?,?,?,?)}");
            pstat.setString(1, examNum);
            pstat.setString(2, gradeNum);
            pstat.setString(3, teachUnit_s);
            pstat.setString(4, teachUnit);
            pstat.setString(5, studentType);
            pstat.setString(6, type);
            pstat.setString(7, source);
            pstat.setString(8, subCompose);
            pstat.setString(9, fufen);
            pstat.executeQuery();
            rs = pstat.getResultSet();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int index = 0;
            String jiaoxuedanwei0 = "";
            int hejidanwei = 0;
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            Set<String> subSet = new HashSet<>();
            while (rs.next()) {
                index++;
                if (index == 1) {
                    jiaoxuedanwei0 = rs.getString(1);
                }
                if (jiaoxuedanwei0.equals(rs.getString(1))) {
                    if (subSet.add(rs.getString(3)) && arrayList4.size() > 0) {
                        arrayList3.add(arrayList4);
                        arrayList4 = new ArrayList();
                    }
                    String[] pc = {rs.getString(4), rs.getString(6), rs.getString(7)};
                    arrayList4.add(pc);
                } else if (arrayList4.size() > 0) {
                    arrayList3.add(arrayList4);
                    arrayList4 = new ArrayList();
                }
                if (hejidanwei > 0 && rs.getInt(10) != 1) {
                    arrayList.add(arrayList2);
                    arrayList2 = new ArrayList();
                    hejidanwei = 0;
                }
                int count = rs.getMetaData().getColumnCount();
                String[] obj = new String[count + 1];
                int i = 0;
                while (i < obj.length) {
                    obj[i] = i == 10 ? obj[1] : rs.getString(i + 1);
                    i++;
                }
                arrayList2.add(obj);
                if (rs.getInt(10) == 1) {
                    if (hejidanwei == 0) {
                        Object[] obj0 = (Object[]) arrayList2.get(0);
                        obj0[10] = obj[1];
                    }
                    obj[1] = "合计";
                    hejidanwei++;
                } else if (rs.getInt(10) == 2) {
                    obj[1] = obj[1] + "合计";
                }
            }
            arrayList.add(arrayList2);
            rtnist.add(arrayList3);
            rtnist.add(arrayList);
            DbUtils.close(rs, pstat, conn);
            this.log.info("--getUpperLineData-" + rtnist.size());
            return rtnist;
        } catch (SQLException e) {
            DbUtils.close(rs, pstat, conn);
            return rtnist;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getUpperLineData2(String examNum, String gradeNum, String teachUnit_s, String teachUnit, String studentType, String type, String source, String subCompose, String fufen) {
        String teachUnitSql;
        String hejiteachUnitSql;
        String hejistuSql;
        String isfufen = fufen.equals("0") ? "" : "_fufen";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("type", type);
        args.put("source", source);
        args.put("subCompose", subCompose);
        args.put("teachUnit", teachUnit);
        args.put("fufen", fufen);
        List resultlist = new ArrayList();
        args.put(Const.EXPORTREPORT_schoolNum, "-1");
        List<Map<String, Object>> piciData = this.dao._queryMapList("SELECT s.subjectNum,s.subjectName,o.type,d.name,o.score from onlineindicator o LEFT JOIN `data` d ON d.type='21' AND d.`value`=o.type LEFT JOIN `subject`s ON s.subjectNum=o.subjectNum WHERE o.examNum={examNum} AND o.schoolNum={schoolNum} AND o.gradeNum={gradeNum} AND o.studentType={studentType} AND o.statisticType={type} AND o.source={source} AND o.xuankezuhe={subCompose} and o.fenshuyuan={fufen} ORDER BY o.subjectNum,o.type ", TypeEnum.StringObject, args);
        if (null == piciData || piciData.size() == 0) {
            args.put(Const.EXPORTREPORT_schoolNum, teachUnit);
            piciData = this.dao._queryMapList("SELECT s.subjectNum,s.subjectName,o.type,d.name,o.score from onlineindicator o LEFT JOIN `data` d ON d.type='21' AND d.`value`=o.type LEFT JOIN `subject`s ON s.subjectNum=o.subjectNum WHERE o.examNum={examNum} AND o.schoolNum={schoolNum} AND o.gradeNum={gradeNum} AND o.studentType={studentType} AND o.statisticType={type} AND o.source={source} AND o.xuankezuhe={subCompose} and o.fenshuyuan={fufen} ORDER BY o.subjectNum,o.type ", TypeEnum.StringObject, args);
        }
        LinkedHashMap<Object, List<Map<String, Object>>> piciDataBysub = (LinkedHashMap) piciData.stream().collect(Collectors.groupingBy(obj -> {
            return obj.get(Const.EXPORTREPORT_subjectNum);
        }, LinkedHashMap::new, Collectors.toList()));
        if (teachUnit_s.equals("00")) {
            teachUnitSql = "SELECT s.id sItemId,s.schoolName sItemName,gl.numOfStudent,gl.subjectNum from gradelevel" + isfufen + " gl LEFT JOIN school s ON gl.schoolNum=s.id   left join statisticitem ss on s.id = ss.sItemId AND ss.examNum={examNum}  WHERE ss.statisticItem='01' and ss.topItemId={teachUnit} AND  gl.examNum={examNum} AND gl.gradeNum={gradeNum} AND gl.studentType={studentType} AND gl.statisticType={type} AND gl.source={source} AND gl.xuankezuhe={subCompose} group by ss.sItemId,gl.subjectNum order by CONVERT(s.schoolName USING gbk)";
            hejiteachUnitSql = "SELECT statisticId sItemId,statisticName sItemName,numOfStudent,subjectNum from statisticlevel" + isfufen + " WHERE statisticId={teachUnit} AND examNum={examNum} AND gradeNum={gradeNum} AND studentType={studentType} AND statisticType={type} AND source={source} AND xuankezuhe={subCompose} ";
            hejistuSql = "SELECT sf.studentId,sf.schoolNum,sf.classNum,sf.totalScore FROM studentlevel" + isfufen + " sf LEFT JOIN school s ON sf.schoolNum=s.id   left join statisticitem ss on s.id = ss.sItemId AND ss.examNum={examNum}  WHERE ss.statisticItem='01' and ss.topItemId={teachUnit} AND sf.examNum={examNum} AND sf.gradeNum={gradeNum} AND sf.studentType={studentType} AND sf.statisticType={type} AND sf.source={source} AND sf.xuankezuhe={subCompose} AND sf.subjectNum={subjectNum} AND sf.totalScore >= {minScore}";
        } else {
            teachUnitSql = "SELECT c.id sItemId,c.classNum,c.className sItemName,cl.numOfStudent,cl.subjectNum from classlevel" + isfufen + " cl LEFT JOIN class c ON cl.classNum=c.id   WHERE cl.schoolNum={teachUnit} AND cl.examNum={examNum} AND cl.gradeNum={gradeNum} AND cl.studentType={studentType} AND cl.statisticType={type} AND cl.source={source}  AND cl.xuankezuhe={subCompose} ORDER BY c.classNum ";
            hejiteachUnitSql = "SELECT '999' sItemId,'全年级' sItemName,numOfStudent,subjectNum FROM gradelevel" + isfufen + " WHERE schoolNum={teachUnit} AND examNum={examNum} AND gradeNum={gradeNum} AND studentType={studentType} AND statisticType={type} AND source={source} AND xuankezuhe={subCompose} ";
            hejistuSql = "SELECT studentId,schoolNum,classNum,totalScore FROM studentlevel" + isfufen + " WHERE examNum={examNum} AND schoolNum={teachUnit} AND gradeNum={gradeNum} AND studentType={studentType} AND statisticType={type} AND source={source} AND xuankezuhe={subCompose} AND subjectNum={subjectNum} AND totalScore >= {minScore}";
        }
        List<Map<String, Object>> teachUnitData = this.dao._queryMapList(teachUnitSql, TypeEnum.StringObject, args);
        LinkedHashMap<Object, List<Map<String, Object>>> teachUnitDatabyteachUnit = (LinkedHashMap) teachUnitData.stream().collect(Collectors.groupingBy(obj2 -> {
            return obj2.get("sItemId");
        }, LinkedHashMap::new, Collectors.toList()));
        String finalhejistuSql = hejistuSql;
        ArrayList arrayList = new ArrayList();
        LinkedHashMap<Object, LinkedHashMap<Object, List<Map<String, Object>>>> allsub_teachData = new LinkedHashMap<>();
        LinkedHashMap<Object, List<Map<String, Object>>> allsub_teachDataheji = new LinkedHashMap<>();
        piciDataBysub.forEach((sub, subData) -> {
            LinkedHashMap<Object, List<Map<String, Object>>> hejioneschlistbyteachUnit;
            arrayList.add(subData);
            Integer size = Integer.valueOf(subData.size());
            String subjectNum = Convert.toStr(((Map) subData.get(size.intValue() - 1)).get(Const.EXPORTREPORT_subjectNum));
            Convert.toStr(((Map) subData.get(size.intValue() - 1)).get("subjectName"));
            Double score = Convert.toDouble(((Map) subData.get(size.intValue() - 1)).get("score"));
            new HashMap();
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            args.put("minScore", score);
            List<Map<String, Object>> hejioneschlist = this.dao._queryMapList(finalhejistuSql, TypeEnum.StringObject, args);
            new LinkedHashMap();
            if (teachUnit_s.equals("00")) {
                hejioneschlistbyteachUnit = (LinkedHashMap) hejioneschlist.stream().collect(Collectors.groupingBy(obj3 -> {
                    return obj3.get(Const.EXPORTREPORT_schoolNum);
                }, LinkedHashMap::new, Collectors.toList()));
            } else {
                hejioneschlistbyteachUnit = (LinkedHashMap) hejioneschlist.stream().collect(Collectors.groupingBy(obj4 -> {
                    return obj4.get(Const.EXPORTREPORT_classNum);
                }, LinkedHashMap::new, Collectors.toList()));
            }
            allsub_teachData.put(subjectNum, hejioneschlistbyteachUnit);
            allsub_teachDataheji.put(subjectNum, hejioneschlist);
        });
        ArrayList arrayList2 = new ArrayList();
        Map<String, Integer> hejitolineMap = new HashMap<>();
        teachUnitDatabyteachUnit.forEach((key, teachData) -> {
            LinkedHashMap<Object, List<Map<String, Object>>> teachDatabysub = (LinkedHashMap) teachData.stream().collect(Collectors.groupingBy(obj3 -> {
                return obj3.get(Const.EXPORTREPORT_subjectNum);
            }, LinkedHashMap::new, Collectors.toList()));
            String sItemId = Convert.toStr(((Map) teachData.get(0)).get("sItemId"));
            String schoolName = Convert.toStr(((Map) teachData.get(0)).get("sItemName"));
            piciDataBysub.forEach((sub2, subData2) -> {
                Integer size = Integer.valueOf(subData2.size());
                String subjectNum = Convert.toStr(((Map) subData2.get(size.intValue() - 1)).get(Const.EXPORTREPORT_subjectNum));
                String subjectName = Convert.toStr(((Map) subData2.get(size.intValue() - 1)).get("subjectName"));
                Convert.toDouble(((Map) subData2.get(size.intValue() - 1)).get("score"));
                new HashMap();
                String numOfStudent = "0";
                if (null != teachDatabysub.get(Convert.toInt(subjectNum))) {
                    numOfStudent = Convert.toStr(((Map) ((List) teachDatabysub.get(Convert.toInt(subjectNum))).get(0)).get("numOfStudent"));
                }
                List<Map<String, Object>> picilist = (List) piciDataBysub.get(Convert.toInt(subjectNum));
                if (teachUnit_s.equals("00")) {
                    if (null != allsub_teachData.get(subjectNum) && null != ((LinkedHashMap) allsub_teachData.get(subjectNum)).get(Convert.toInt(sItemId))) {
                        List<Map<String, Object>> oneschlist = (List) ((LinkedHashMap) allsub_teachData.get(subjectNum)).get(Convert.toInt(sItemId));
                        for (int k = 0; k < picilist.size(); k++) {
                            Double scorepici = Convert.toDouble(picilist.get(k).get("score"));
                            Integer onlinenum = 0;
                            String picitype = Convert.toStr(picilist.get(k).get("type"));
                            if (null != scorepici) {
                                List<Map<String, Object>> stulist = (List) oneschlist.stream().filter(m -> {
                                    return Convert.toDouble(m.get("totalScore")).doubleValue() >= scorepici.doubleValue();
                                }).collect(Collectors.toList());
                                onlinenum = Integer.valueOf(stulist.size());
                            }
                            Integer numofpici_sub = 0;
                            if (null != hejitolineMap.get(subjectNum + "_" + picitype)) {
                                numofpici_sub = (Integer) hejitolineMap.get(subjectNum + "_" + picitype);
                            }
                            hejitolineMap.put(subjectNum + "_" + picitype, Integer.valueOf(numofpici_sub.intValue() + onlinenum.intValue()));
                            String[] onlinearr = {sItemId, schoolName, subjectNum, subjectName, Convert.toStr(picilist.get(k).get("type")), Convert.toStr(picilist.get(k).get("name")), Convert.toStr(picilist.get(k).get("score")), numOfStudent, onlinenum.toString()};
                            arrayList2.add(onlinearr);
                        }
                        return;
                    }
                    for (int k2 = 0; k2 < picilist.size(); k2++) {
                        Convert.toDouble(picilist.get(k2).get("score"));
                        String picitype2 = Convert.toStr(picilist.get(k2).get("type"));
                        Integer onlinenum2 = 0;
                        Integer numofpici_sub2 = 0;
                        if (null != hejitolineMap.get(subjectNum + "_" + picitype2)) {
                            numofpici_sub2 = (Integer) hejitolineMap.get(subjectNum + "_" + picitype2);
                        }
                        hejitolineMap.put(subjectNum + "_" + picitype2, Integer.valueOf(numofpici_sub2.intValue() + onlinenum2.intValue()));
                        String[] onlinearr2 = {sItemId, schoolName, subjectNum, subjectName, Convert.toStr(picilist.get(k2).get("type")), Convert.toStr(picilist.get(k2).get("name")), Convert.toStr(picilist.get(k2).get("score")), numOfStudent, onlinenum2.toString()};
                        arrayList2.add(onlinearr2);
                    }
                    return;
                }
                if (((LinkedHashMap) allsub_teachData.get(subjectNum)).size() > 0 && null != ((LinkedHashMap) allsub_teachData.get(subjectNum)).get(Convert.toLong(sItemId))) {
                    List<Map<String, Object>> oneschlist2 = (List) ((LinkedHashMap) allsub_teachData.get(subjectNum)).get(Convert.toLong(sItemId));
                    for (int k3 = 0; k3 < picilist.size(); k3++) {
                        Double scorepici2 = Convert.toDouble(picilist.get(k3).get("score"));
                        String picitype3 = Convert.toStr(picilist.get(k3).get("type"));
                        Integer onlinenum3 = 0;
                        if (null != scorepici2) {
                            List<Map<String, Object>> stulist2 = (List) oneschlist2.stream().filter(m2 -> {
                                return Convert.toDouble(m2.get("totalScore")).doubleValue() >= scorepici2.doubleValue();
                            }).collect(Collectors.toList());
                            onlinenum3 = Integer.valueOf(stulist2.size());
                        }
                        Integer numofpici_sub3 = 0;
                        if (null != hejitolineMap.get(subjectNum + "_" + picitype3)) {
                            numofpici_sub3 = (Integer) hejitolineMap.get(subjectNum + "_" + picitype3);
                        }
                        hejitolineMap.put(subjectNum + "_" + picitype3, Integer.valueOf(numofpici_sub3.intValue() + onlinenum3.intValue()));
                        String[] onlinearr3 = {sItemId, schoolName, subjectNum, subjectName, Convert.toStr(picilist.get(k3).get("type")), Convert.toStr(picilist.get(k3).get("name")), Convert.toStr(picilist.get(k3).get("score")), numOfStudent, onlinenum3.toString()};
                        arrayList2.add(onlinearr3);
                    }
                    return;
                }
                for (int k4 = 0; k4 < picilist.size(); k4++) {
                    Convert.toDouble(picilist.get(k4).get("score"));
                    String picitype4 = Convert.toStr(picilist.get(k4).get("type"));
                    Integer onlinenum4 = 0;
                    Integer numofpici_sub4 = 0;
                    if (null != hejitolineMap.get(subjectNum + "_" + picitype4)) {
                        numofpici_sub4 = (Integer) hejitolineMap.get(subjectNum + "_" + picitype4);
                    }
                    hejitolineMap.put(subjectNum + "_" + picitype4, Integer.valueOf(numofpici_sub4.intValue() + onlinenum4.intValue()));
                    String[] onlinearr4 = {sItemId, schoolName, subjectNum, subjectName, Convert.toStr(picilist.get(k4).get("type")), Convert.toStr(picilist.get(k4).get("name")), Convert.toStr(picilist.get(k4).get("score")), numOfStudent, onlinenum4.toString()};
                    arrayList2.add(onlinearr4);
                }
            });
        });
        String finalHejiteachUnitSql = hejiteachUnitSql;
        piciDataBysub.forEach((sub2, subData2) -> {
            Integer size = Integer.valueOf(subData2.size());
            String subjectNum = Convert.toStr(((Map) subData2.get(size.intValue() - 1)).get(Const.EXPORTREPORT_subjectNum));
            String subjectName = Convert.toStr(((Map) subData2.get(size.intValue() - 1)).get("subjectName"));
            Double score = Convert.toDouble(((Map) subData2.get(size.intValue() - 1)).get("score"));
            new HashMap();
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            args.put("minScore", score);
            List<Map<String, Object>> picilist = (List) piciDataBysub.get(Convert.toInt(subjectNum));
            List<Map<String, Object>> hejiteachUnitData = this.dao._queryMapList(finalHejiteachUnitSql, TypeEnum.StringObject, args);
            String sItemId = hejiteachUnitData.get(0).get("sItemId").toString();
            String schoolName = hejiteachUnitData.get(0).get("sItemName").toString();
            LinkedHashMap<Object, List<Map<String, Object>>> teachDatabysub = (LinkedHashMap) hejiteachUnitData.stream().collect(Collectors.groupingBy(obj3 -> {
                return obj3.get(Const.EXPORTREPORT_subjectNum);
            }, LinkedHashMap::new, Collectors.toList()));
            String numOfStudent = "0";
            if (null != teachDatabysub.get(Convert.toInt(subjectNum))) {
                numOfStudent = Convert.toStr(teachDatabysub.get(Convert.toInt(subjectNum)).get(0).get("numOfStudent"));
            }
            for (int k = 0; k < picilist.size(); k++) {
                Double scorepici = Convert.toDouble(picilist.get(k).get("score"));
                Integer onlinenum = 0;
                if (null != scorepici) {
                    String picitype = Convert.toStr(picilist.get(k).get("type"));
                    onlinenum = (Integer) hejitolineMap.get(subjectNum + "_" + picitype);
                }
                String[] onlinearr = {sItemId, schoolName + "合计", subjectNum, subjectName, Convert.toStr(picilist.get(k).get("type")), Convert.toStr(picilist.get(k).get("name")), Convert.toStr(picilist.get(k).get("score")), numOfStudent, onlinenum.toString()};
                arrayList2.add(onlinearr);
            }
        });
        resultlist.add(arrayList);
        resultlist.add(arrayList2);
        return resultlist;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getDoubleLineData_old(String examNum, String sjt, String gradeNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String subOrClaLine, String subjectNum, String lineType, String classNum, String reCalcu) {
        List rtnist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            pstat = conn.prepareCall("{call /* shard_host_HG=Read */ doubleUpperToLine(?,?,?,?,?,?,?,?,?,?,?,?)}");
            pstat.setString(1, examNum);
            pstat.setString(2, sjt);
            pstat.setString(3, gradeNum);
            pstat.setString(4, schoolNum);
            pstat.setString(5, studentType);
            pstat.setString(6, graduationType);
            pstat.setString(7, stuSourceType);
            pstat.setString(8, subOrClaLine);
            pstat.setString(9, subjectNum);
            pstat.setString(10, lineType);
            pstat.setString(11, classNum);
            pstat.setString(12, reCalcu);
            pstat.executeQuery();
            rs = pstat.getResultSet();
            new HashMap();
            String subNum = "";
            String claId = "";
            Object[] objArr = new Object[7];
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            while (rs.next()) {
                int count = rs.getMetaData().getColumnCount();
                Object[] obj = new Object[count];
                for (int i = 0; i < obj.length; i++) {
                    obj[i] = rs.getObject(i + 1);
                }
                arrayList3.add(obj);
                String curClaId = String.valueOf(rs.getObject(3));
                String curSubNum = String.valueOf(rs.getObject(6));
                Object[] curObjArr = new Object[7];
                if (null != subOrClaLine && "0".equals(subOrClaLine)) {
                    if ("".equals(claId) || claId.equals(curClaId)) {
                        if ("".equals(subNum) || subNum.equals(curSubNum)) {
                            curObjArr[0] = rs.getObject(9);
                            curObjArr[1] = rs.getObject(6);
                            curObjArr[2] = studentType;
                            curObjArr[3] = rs.getObject(11);
                            curObjArr[4] = rs.getObject(12);
                            curObjArr[5] = rs.getObject(13);
                            curObjArr[6] = rs.getObject(10);
                            arrayList2.add(curObjArr);
                        } else {
                            arrayList.add(arrayList2);
                            arrayList2 = new ArrayList();
                            curObjArr[0] = rs.getObject(9);
                            curObjArr[1] = rs.getObject(6);
                            curObjArr[2] = studentType;
                            curObjArr[3] = rs.getObject(11);
                            curObjArr[4] = rs.getObject(12);
                            curObjArr[5] = rs.getObject(13);
                            curObjArr[6] = rs.getObject(10);
                            arrayList2.add(curObjArr);
                        }
                        claId = curClaId;
                        subNum = curSubNum;
                    }
                } else {
                    curObjArr[0] = rs.getObject(9);
                    curObjArr[1] = rs.getObject(6);
                    curObjArr[2] = studentType;
                    curObjArr[3] = rs.getObject(3);
                    curObjArr[4] = rs.getObject(3);
                    curObjArr[5] = rs.getObject(10);
                    curObjArr[6] = rs.getObject(4);
                    arrayList2.add(curObjArr);
                }
            }
            arrayList.add(arrayList2);
            rtnist.add(arrayList);
            rtnist.add(arrayList3);
            while (pstat.getMoreResults()) {
                ArrayList arrayList4 = new ArrayList();
                rs = pstat.getResultSet();
                int count2 = rs.getMetaData().getColumnCount();
                if (null != arrayList2 && arrayList2.size() > 0) {
                    while (rs.next()) {
                        Object[] obj2 = new Object[count2];
                        for (int i2 = 0; i2 < obj2.length; i2++) {
                            obj2[i2] = rs.getObject(i2 + 1);
                        }
                        arrayList4.add(obj2);
                    }
                }
                rtnist.add(arrayList4);
            }
            DbUtils.close(rs, pstat, conn);
            this.log.info("---getDoubleLineData-" + rtnist.size());
            return rtnist;
        } catch (SQLException e) {
            DbUtils.close(rs, pstat, conn);
            return rtnist;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getDoubleLineData(String examNum, String gradeNum, String teachUnit_s, String teachUnit, String studentType, String type, String source, String subCompose, String fufen) {
        List rtnist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        Long.valueOf(new Date().getTime());
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            pstat = conn.prepareCall("{call /* shard_host_HG=Read */ doubleUpperToLine(?,?,?,?,?,?,?,?,?)}");
            pstat.setString(1, examNum);
            pstat.setString(2, gradeNum);
            pstat.setString(3, teachUnit_s);
            pstat.setString(4, teachUnit);
            pstat.setString(5, studentType);
            pstat.setString(6, type);
            pstat.setString(7, source);
            pstat.setString(8, subCompose);
            pstat.setString(9, fufen);
            pstat.executeQuery();
            rs = pstat.getResultSet();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int index = 0;
            String jiaoxuedanwei0 = "";
            int hejidanwei = 0;
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            Set<String> subSet = new HashSet<>();
            while (rs.next()) {
                index++;
                if (index == 1) {
                    jiaoxuedanwei0 = rs.getString(1);
                }
                if (jiaoxuedanwei0.equals(rs.getString(1))) {
                    if (subSet.add(rs.getString(3)) && arrayList4.size() > 0) {
                        arrayList3.add(arrayList4);
                        arrayList4 = new ArrayList();
                    }
                    String[] pc = {rs.getString(4), rs.getString(6), rs.getString(7)};
                    arrayList4.add(pc);
                } else if (arrayList4.size() > 0) {
                    arrayList3.add(arrayList4);
                    arrayList4 = new ArrayList();
                }
                if (hejidanwei > 0 && rs.getInt(10) != 1) {
                    arrayList.add(arrayList2);
                    arrayList2 = new ArrayList();
                    hejidanwei = 0;
                }
                int count = rs.getMetaData().getColumnCount();
                String[] obj = new String[count + 1];
                int i = 0;
                while (i < obj.length) {
                    obj[i] = i == 10 ? obj[1] : rs.getString(i + 1);
                    i++;
                }
                arrayList2.add(obj);
                if (rs.getInt(10) == 1) {
                    if (hejidanwei == 0) {
                        Object[] obj0 = (Object[]) arrayList2.get(0);
                        obj0[10] = obj[1];
                    }
                    obj[1] = "合计";
                    hejidanwei++;
                } else if (rs.getInt(10) == 2) {
                    obj[1] = obj[1] + "合计";
                }
            }
            arrayList.add(arrayList2);
            rtnist.add(arrayList3);
            rtnist.add(arrayList);
            DbUtils.close(rs, pstat, conn);
            Long.valueOf(new Date().getTime());
            this.log.info("--getUpperLineData-" + rtnist.size());
            return rtnist;
        } catch (SQLException e) {
            DbUtils.close(rs, pstat, conn);
            return rtnist;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getDoubleLineData2(String examNum, String gradeNum, String teachUnit_s, String teachUnit, String studentType, String type, String source, String subCompose, String fufen) {
        String teachUnitSql;
        String hejiteachUnitSql;
        String hejistuSql;
        String isfufen = fufen.equals("0") ? "" : "_fufen";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("type", type);
        args.put("source", source);
        args.put("subCompose", subCompose);
        args.put("teachUnit", teachUnit);
        args.put("fufen", fufen);
        List resultlist = new ArrayList();
        args.put(Const.EXPORTREPORT_schoolNum, "-1");
        List<Map<String, Object>> piciData = this.dao._queryMapList("SELECT s.subjectNum,s.subjectName,o.type,d.name,o.score,s.isHidden from onlineindicator o LEFT JOIN `data` d ON d.type='21' AND d.`value`=o.type LEFT JOIN `subject`s ON s.subjectNum=o.subjectNum WHERE o.examNum={examNum} AND o.schoolNum={schoolNum} AND o.gradeNum={gradeNum} AND o.studentType={studentType} AND o.statisticType={type} AND o.source={source} AND o.xuankezuhe={subCompose} and o.fenshuyuan={fufen} ORDER BY o.subjectNum,o.type ", TypeEnum.StringObject, args);
        if (null == piciData || piciData.size() == 0) {
            args.put(Const.EXPORTREPORT_schoolNum, teachUnit);
            piciData = this.dao._queryMapList("SELECT s.subjectNum,s.subjectName,o.type,d.name,o.score,s.isHidden from onlineindicator o LEFT JOIN `data` d ON d.type='21' AND d.`value`=o.type LEFT JOIN `subject`s ON s.subjectNum=o.subjectNum WHERE o.examNum={examNum} AND o.schoolNum={schoolNum} AND o.gradeNum={gradeNum} AND o.studentType={studentType} AND o.statisticType={type} AND o.source={source} AND o.xuankezuhe={subCompose} and o.fenshuyuan={fufen} ORDER BY o.subjectNum,o.type ", TypeEnum.StringObject, args);
        }
        LinkedHashMap<Object, List<Map<String, Object>>> piciDataBysub = (LinkedHashMap) piciData.stream().collect(Collectors.groupingBy(obj -> {
            return obj.get(Const.EXPORTREPORT_subjectNum);
        }, LinkedHashMap::new, Collectors.toList()));
        if (teachUnit_s.equals("00")) {
            teachUnitSql = "SELECT s.id sItemId,s.schoolName sItemName,gl.numOfStudent,gl.subjectNum from gradelevel" + isfufen + " gl LEFT JOIN school s ON gl.schoolNum=s.id   left join statisticitem ss on s.id = ss.sItemId AND ss.examNum={examNum}  WHERE ss.statisticItem='01' and ss.topItemId={teachUnit} AND  gl.examNum={examNum} AND gl.gradeNum={gradeNum} AND gl.studentType={studentType} AND gl.statisticType={type} AND gl.source={source} AND gl.xuankezuhe={subCompose} group by ss.sItemId,gl.subjectNum order by CONVERT(s.schoolName USING gbk)";
            hejiteachUnitSql = "SELECT statisticId sItemId,statisticName sItemName,numOfStudent,subjectNum from statisticlevel" + isfufen + " WHERE statisticId={teachUnit} AND examNum={examNum} AND gradeNum={gradeNum} AND studentType={studentType} AND statisticType={type} AND source={source} AND xuankezuhe={subCompose} ";
            hejistuSql = "SELECT sf.studentId,sf.schoolNum,sf.classNum,sf.totalScore FROM studentlevel" + isfufen + " sf LEFT JOIN school s ON sf.schoolNum=s.id   left join statisticitem ss on s.id = ss.sItemId AND ss.examNum={examNum}  WHERE ss.statisticItem='01' and ss.topItemId={teachUnit} AND sf.examNum={examNum} AND sf.gradeNum={gradeNum} AND sf.studentType={studentType} AND sf.statisticType={type} AND sf.source={source} AND sf.xuankezuhe={subCompose} AND sf.subjectNum={subjectNum} AND sf.totalScore >= {minScore} order by sf.studentId ";
        } else {
            teachUnitSql = "SELECT c.id sItemId,c.classNum,c.className sItemName,cl.numOfStudent,cl.subjectNum from classlevel" + isfufen + " cl LEFT JOIN class c ON cl.classNum=c.id   WHERE cl.schoolNum={teachUnit} AND cl.examNum={examNum} AND cl.gradeNum={gradeNum} AND cl.studentType={studentType} AND cl.statisticType={type} AND cl.source={source}  AND cl.xuankezuhe={subCompose} ORDER BY c.classNum ";
            hejiteachUnitSql = "SELECT '999' sItemId,'全年级' sItemName,numOfStudent,subjectNum FROM gradelevel" + isfufen + " WHERE schoolNum={teachUnit} AND examNum={examNum} AND gradeNum={gradeNum} AND studentType={studentType} AND statisticType={type} AND source={source} AND xuankezuhe={subCompose} ";
            hejistuSql = "SELECT studentId,schoolNum,classNum,totalScore FROM studentlevel" + isfufen + " WHERE examNum={examNum} AND schoolNum={teachUnit} AND gradeNum={gradeNum} AND studentType={studentType} AND statisticType={type} AND source={source} AND xuankezuhe={subCompose} AND subjectNum={subjectNum} AND totalScore >= {minScore} order by studentId ";
        }
        List<Map<String, Object>> teachUnitData = this.dao._queryMapList(teachUnitSql, TypeEnum.StringObject, args);
        LinkedHashMap<Object, List<Map<String, Object>>> teachUnitDatabyteachUnit = (LinkedHashMap) teachUnitData.stream().collect(Collectors.groupingBy(obj2 -> {
            return obj2.get("sItemId");
        }, LinkedHashMap::new, Collectors.toList()));
        String finalhejistuSql = hejistuSql;
        ArrayList arrayList = new ArrayList();
        LinkedHashMap<Object, LinkedHashMap<Object, List<Map<String, Object>>>> allsub_teachData = new LinkedHashMap<>();
        LinkedHashMap<Object, List<Map<String, Object>>> allsub_teachDataheji = new LinkedHashMap<>();
        piciDataBysub.forEach((sub, subData) -> {
            LinkedHashMap<Object, List<Map<String, Object>>> hejioneschlistbyteachUnit;
            arrayList.add(subData);
            Integer size = Integer.valueOf(subData.size());
            String subjectNum = Convert.toStr(((Map) subData.get(size.intValue() - 1)).get(Const.EXPORTREPORT_subjectNum));
            Convert.toStr(((Map) subData.get(size.intValue() - 1)).get("subjectName"));
            Double score = Convert.toDouble(((Map) subData.get(size.intValue() - 1)).get("score"));
            new HashMap();
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            args.put("minScore", score);
            List<Map<String, Object>> hejioneschlist = this.dao._queryMapList(finalhejistuSql, TypeEnum.StringObject, args);
            new LinkedHashMap();
            if (teachUnit_s.equals("00")) {
                hejioneschlistbyteachUnit = (LinkedHashMap) hejioneschlist.stream().collect(Collectors.groupingBy(obj3 -> {
                    return obj3.get(Const.EXPORTREPORT_schoolNum);
                }, LinkedHashMap::new, Collectors.toList()));
            } else {
                hejioneschlistbyteachUnit = (LinkedHashMap) hejioneschlist.stream().collect(Collectors.groupingBy(obj4 -> {
                    return obj4.get(Const.EXPORTREPORT_classNum);
                }, LinkedHashMap::new, Collectors.toList()));
            }
            allsub_teachData.put(subjectNum, hejioneschlistbyteachUnit);
            allsub_teachDataheji.put(subjectNum, hejioneschlist);
        });
        ArrayList arrayList2 = new ArrayList();
        LinkedHashMap<Object, List<Map<String, Object>>> zongfenstuData = new LinkedHashMap<>();
        Map<String, Integer> hejitolineMap = new HashMap<>();
        teachUnitDatabyteachUnit.forEach((key, teachData) -> {
            LinkedHashMap<Object, List<Map<String, Object>>> teachDatabysub = (LinkedHashMap) teachData.stream().collect(Collectors.groupingBy(obj3 -> {
                return obj3.get(Const.EXPORTREPORT_subjectNum);
            }, LinkedHashMap::new, Collectors.toList()));
            String sItemId = Convert.toStr(((Map) teachData.get(0)).get("sItemId"));
            String schoolName = Convert.toStr(((Map) teachData.get(0)).get("sItemName"));
            piciDataBysub.forEach((sub2, subData2) -> {
                Integer size = Integer.valueOf(subData2.size());
                String subjectNum = Convert.toStr(((Map) subData2.get(size.intValue() - 1)).get(Const.EXPORTREPORT_subjectNum));
                String subjectName = Convert.toStr(((Map) subData2.get(size.intValue() - 1)).get("subjectName"));
                Convert.toDouble(((Map) subData2.get(size.intValue() - 1)).get("score"));
                new HashMap();
                String numOfStudent = "0";
                if (null != teachDatabysub.get(Convert.toInt(subjectNum))) {
                    numOfStudent = Convert.toStr(((Map) ((List) teachDatabysub.get(Convert.toInt(subjectNum))).get(0)).get("numOfStudent"));
                }
                List<Map<String, Object>> picilist = (List) piciDataBysub.get(Convert.toInt(subjectNum));
                if (teachUnit_s.equals("00")) {
                    if (null != ((LinkedHashMap) allsub_teachData.get(subjectNum)).get(Convert.toInt(sItemId))) {
                        List<Map<String, Object>> oneschlist = (List) ((LinkedHashMap) allsub_teachData.get(subjectNum)).get(Convert.toInt(sItemId));
                        for (int k = 0; k < picilist.size(); k++) {
                            Double scorepici = Convert.toDouble(picilist.get(k).get("score"));
                            String picitype = Convert.toStr(picilist.get(k).get("type"));
                            Integer onlinenum = 0;
                            if (null != scorepici) {
                                List<Map<String, Object>> stulist = (List) oneschlist.stream().filter(m -> {
                                    return Convert.toDouble(m.get("totalScore")).doubleValue() >= scorepici.doubleValue();
                                }).collect(Collectors.toList());
                                List<String> studentidList = (List) stulist.stream().map(m2 -> {
                                    return Convert.toStr(m2.get(Const.EXPORTREPORT_studentId));
                                }).distinct().collect(Collectors.toList());
                                if (!subjectNum.equals("-1")) {
                                    List<Map<String, Object>> zongfenstulist = (List) zongfenstuData.get(picitype + "_" + sItemId);
                                    if (null != zongfenstulist && zongfenstulist.size() != 0) {
                                        List<String> studentidList2 = (List) zongfenstulist.stream().map(m3 -> {
                                            return Convert.toStr(m3.get(Const.EXPORTREPORT_studentId));
                                        }).distinct().collect(Collectors.toList());
                                        DateUtil.timer();
                                        studentidList.retainAll(studentidList2);
                                        onlinenum = Integer.valueOf(studentidList.size());
                                    }
                                } else {
                                    zongfenstuData.put(picitype + "_" + sItemId, stulist);
                                    onlinenum = Integer.valueOf(stulist.size());
                                }
                            }
                            Integer numofpici_sub = 0;
                            if (null != hejitolineMap.get(subjectNum + "_" + picitype)) {
                                numofpici_sub = (Integer) hejitolineMap.get(subjectNum + "_" + picitype);
                            }
                            hejitolineMap.put(subjectNum + "_" + picitype, Integer.valueOf(numofpici_sub.intValue() + onlinenum.intValue()));
                            String[] onlinearr = {sItemId, schoolName, subjectNum, subjectName, picitype, Convert.toStr(picilist.get(k).get("name")), Convert.toStr(picilist.get(k).get("score")), numOfStudent, onlinenum.toString()};
                            arrayList2.add(onlinearr);
                        }
                        return;
                    }
                    for (int k2 = 0; k2 < picilist.size(); k2++) {
                        Convert.toDouble(picilist.get(k2).get("score"));
                        Integer onlinenum2 = 0;
                        String picitype2 = Convert.toStr(picilist.get(k2).get("type"));
                        Integer numofpici_sub2 = 0;
                        if (null != hejitolineMap.get(subjectNum + "_" + picitype2)) {
                            numofpici_sub2 = (Integer) hejitolineMap.get(subjectNum + "_" + picitype2);
                        }
                        hejitolineMap.put(subjectNum + "_" + picitype2, Integer.valueOf(numofpici_sub2.intValue() + onlinenum2.intValue()));
                        String[] onlinearr2 = {sItemId, schoolName, subjectNum, subjectName, Convert.toStr(picilist.get(k2).get("type")), Convert.toStr(picilist.get(k2).get("name")), Convert.toStr(picilist.get(k2).get("score")), numOfStudent, onlinenum2.toString()};
                        arrayList2.add(onlinearr2);
                    }
                    return;
                }
                if (null != ((LinkedHashMap) allsub_teachData.get(subjectNum)).get(Convert.toLong(sItemId))) {
                    List<Map<String, Object>> oneschlist2 = (List) ((LinkedHashMap) allsub_teachData.get(subjectNum)).get(Convert.toLong(sItemId));
                    for (int k3 = 0; k3 < picilist.size(); k3++) {
                        Double scorepici2 = Convert.toDouble(picilist.get(k3).get("score"));
                        String picitype3 = Convert.toStr(picilist.get(k3).get("type"));
                        Integer onlinenum3 = 0;
                        if (null != scorepici2) {
                            List<Map<String, Object>> stulist2 = (List) oneschlist2.stream().filter(m4 -> {
                                return Convert.toDouble(m4.get("totalScore")).doubleValue() >= scorepici2.doubleValue();
                            }).collect(Collectors.toList());
                            List<String> studentidList3 = (List) stulist2.stream().map(m5 -> {
                                return Convert.toStr(m5.get(Const.EXPORTREPORT_studentId));
                            }).distinct().collect(Collectors.toList());
                            if (!subjectNum.equals("-1")) {
                                List<Map<String, Object>> zongfenstulist2 = (List) zongfenstuData.get(picitype3 + "_" + sItemId);
                                if (null != zongfenstulist2 && zongfenstulist2.size() != 0) {
                                    List<String> studentidList22 = new ArrayList<>();
                                    if (null != zongfenstulist2) {
                                        studentidList22 = (List) zongfenstulist2.stream().map(m6 -> {
                                            return Convert.toStr(m6.get(Const.EXPORTREPORT_studentId));
                                        }).distinct().collect(Collectors.toList());
                                    }
                                    DateUtil.timer();
                                    studentidList3.retainAll(studentidList22);
                                    onlinenum3 = Integer.valueOf(studentidList3.size());
                                }
                            } else {
                                zongfenstuData.put(picitype3 + "_" + sItemId, stulist2);
                                onlinenum3 = Integer.valueOf(stulist2.size());
                            }
                        }
                        Integer numofpici_sub3 = 0;
                        if (null != hejitolineMap.get(subjectNum + "_" + picitype3)) {
                            numofpici_sub3 = (Integer) hejitolineMap.get(subjectNum + "_" + picitype3);
                        }
                        hejitolineMap.put(subjectNum + "_" + picitype3, Integer.valueOf(numofpici_sub3.intValue() + onlinenum3.intValue()));
                        String[] onlinearr3 = {sItemId, schoolName, subjectNum, subjectName, Convert.toStr(picilist.get(k3).get("type")), Convert.toStr(picilist.get(k3).get("name")), Convert.toStr(picilist.get(k3).get("score")), numOfStudent, onlinenum3.toString()};
                        arrayList2.add(onlinearr3);
                    }
                    return;
                }
                for (int k4 = 0; k4 < picilist.size(); k4++) {
                    Convert.toDouble(picilist.get(k4).get("score"));
                    Integer onlinenum4 = 0;
                    String picitype4 = Convert.toStr(picilist.get(k4).get("type"));
                    Integer numofpici_sub4 = 0;
                    if (null != hejitolineMap.get(subjectNum + "_" + picitype4)) {
                        numofpici_sub4 = (Integer) hejitolineMap.get(subjectNum + "_" + picitype4);
                    }
                    hejitolineMap.put(subjectNum + "_" + picitype4, Integer.valueOf(numofpici_sub4.intValue() + onlinenum4.intValue()));
                    String[] onlinearr4 = {sItemId, schoolName, subjectNum, subjectName, Convert.toStr(picilist.get(k4).get("type")), Convert.toStr(picilist.get(k4).get("name")), Convert.toStr(picilist.get(k4).get("score")), numOfStudent, onlinenum4.toString()};
                    arrayList2.add(onlinearr4);
                }
            });
        });
        new HashMap();
        String finalHejiteachUnitSql = hejiteachUnitSql;
        piciDataBysub.forEach((sub2, subData2) -> {
            Integer size = Integer.valueOf(subData2.size());
            String subjectNum = Convert.toStr(((Map) subData2.get(size.intValue() - 1)).get(Const.EXPORTREPORT_subjectNum));
            String subjectName = Convert.toStr(((Map) subData2.get(size.intValue() - 1)).get("subjectName"));
            Double score = Convert.toDouble(((Map) subData2.get(size.intValue() - 1)).get("score"));
            new HashMap();
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            args.put("minScore", score);
            List<Map<String, Object>> picilist = (List) piciDataBysub.get(Convert.toInt(subjectNum));
            List<Map<String, Object>> hejiteachUnitData = this.dao._queryMapList(finalHejiteachUnitSql, TypeEnum.StringObject, args);
            String sItemId = hejiteachUnitData.get(0).get("sItemId").toString();
            String schoolName = hejiteachUnitData.get(0).get("sItemName").toString();
            LinkedHashMap<Object, List<Map<String, Object>>> teachDatabysub = (LinkedHashMap) hejiteachUnitData.stream().collect(Collectors.groupingBy(obj3 -> {
                return obj3.get(Const.EXPORTREPORT_subjectNum);
            }, LinkedHashMap::new, Collectors.toList()));
            String numOfStudent = "0";
            if (null != teachDatabysub.get(Convert.toInt(subjectNum))) {
                numOfStudent = Convert.toStr(teachDatabysub.get(Convert.toInt(subjectNum)).get(0).get("numOfStudent"));
            }
            for (int k = 0; k < picilist.size(); k++) {
                Double scorepici = Convert.toDouble(picilist.get(k).get("score"));
                Integer onlinenum = 0;
                if (null != scorepici) {
                    String picitype = Convert.toStr(picilist.get(k).get("type"));
                    onlinenum = (Integer) hejitolineMap.get(subjectNum + "_" + picitype);
                }
                String[] onlinearr = {sItemId, schoolName + "合计", subjectNum, subjectName, Convert.toStr(picilist.get(k).get("type")), Convert.toStr(picilist.get(k).get("name")), Convert.toStr(picilist.get(k).get("score")), numOfStudent, onlinenum.toString()};
                arrayList2.add(onlinearr);
            }
        });
        resultlist.add(arrayList);
        resultlist.add(arrayList2);
        return resultlist;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getScoreList(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String history, String fufen, String subCompose, String islevel) {
        String sql = "{call /* shard_host_HG=Read */ student_score(?,?,?,?,?,?,?,?,?,?)}";
        if (fufen.equals("1")) {
            sql = "{call /* shard_host_HG=Read */ student_score_fufen(?,?,?,?,?,?,?,?,?,?)}";
        }
        List rtnList = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, type);
                pstat.setString(7, studentType);
                pstat.setString(8, source);
                pstat.setString(9, subCompose);
                pstat.setString(10, islevel);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                ArrayList arrayList = new ArrayList();
                int count = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] obj = new Object[count];
                    int i = 0;
                    while (i < obj.length) {
                        obj[i] = i == 0 ? rs.getString(i + 1) : rs.getObject(i + 1);
                        i++;
                    }
                    arrayList.add(obj);
                }
                rtnList.add(arrayList);
                ArrayList arrayList2 = new ArrayList();
                while (pstat.getMoreResults()) {
                    rs = pstat.getResultSet();
                    int count2 = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        Object[] obj2 = new Object[count2];
                        for (int i2 = 0; i2 < obj2.length; i2++) {
                            obj2[i2] = rs.getObject(i2 + 1);
                        }
                        arrayList2.add(obj2);
                    }
                    rtnList.add(arrayList2);
                }
                DbUtils.close(rs, pstat, conn);
            } catch (SQLException e) {
                this.log.info("", e);
                DbUtils.close(rs, pstat, conn);
            }
            return rtnList;
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getStuQuesAnaly(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String history, String subCompose, String teachUnit, String teachUnit_s) {
        List<Object[]> rtnlist = new ArrayList<>();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ student_question_analy(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, teachUnit);
                pstat.setString(5, subCompose);
                pstat.setString(6, type);
                pstat.setString(7, studentType);
                pstat.setString(8, source);
                pstat.setString(9, teachUnit_s);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] objArr = new Object[count];
                    for (int i = 0; i < objArr.length; i++) {
                        objArr[i] = rs.getObject(i + 1);
                    }
                    rtnlist.add(objArr);
                }
                DbUtils.close(rs, pstat, conn);
                if (rtnlist.size() == 1 && Convert.toStr(rtnlist.get(0)[0]).equals("整卷")) {
                    rtnlist = null;
                }
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public LinkedHashMap<Object, List<Map<String, Object>>> getNanDuDengJi(String examNum, String gradeNum, String subjectNum, String studentType, String type, String source, String subCompose, String teachUnit, String teachUnit_s) {
        List<Map<String, Object>> rtnlist = new ArrayList<>();
        new ArrayList();
        new LinkedHashMap();
        Connection conn = DbUtils.getConnection();
        Map<String, String> nandumap = new HashMap<>();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ nandudengji_s(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, teachUnit);
                pstat.setString(5, subCompose);
                pstat.setString(6, type);
                pstat.setString(7, studentType);
                pstat.setString(8, source);
                pstat.setString(9, teachUnit_s);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(Const.EXPORTREPORT_examNum, rs.getString(1));
                    map.put(Const.EXPORTREPORT_gradeNum, rs.getString(2));
                    map.put("statisticId", rs.getString(3));
                    map.put(Const.EXPORTREPORT_subjectNum, rs.getString(4));
                    map.put("exampaperNum", rs.getString(5));
                    map.put("qtype", rs.getString(6));
                    map.put("qTypeName", rs.getString(7));
                    map.put("sumCount", rs.getString(8));
                    map.put("sumfullScore", NumberUtil.toStr(Double.valueOf(rs.getDouble(9))));
                    map.put("nandu", rs.getString(10));
                    map.put("count", rs.getString(11));
                    map.put("fullScore", NumberUtil.toStr(Double.valueOf(rs.getDouble(12))));
                    map.put("baifenbi", NumberUtil.toStr(Double.valueOf(rs.getDouble(13))));
                    map.put("questionNum", rs.getString(14));
                    map.put("questionName", rs.getString(15));
                    map.put("questionType", rs.getString(16));
                    rtnlist.add(map);
                    nandumap.put(rs.getString(6), rs.getString(6));
                }
                DbUtils.close(rs, pstat, conn);
                LinkedHashMap<Object, List<Map<String, Object>>> maplist = (LinkedHashMap) rtnlist.stream().collect(Collectors.groupingBy(m -> {
                    return m.get("qtype");
                }, LinkedHashMap::new, Collectors.toList()));
                return maplist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public LinkedHashMap<Object, List<Map<String, Object>>> getQuFenDuDengJi(String examNum, String gradeNum, String subjectNum, String studentType, String type, String source, String subCompose, String teachUnit, String teachUnit_s) {
        List<Map<String, Object>> rtnlist = new ArrayList<>();
        new ArrayList();
        new LinkedHashMap();
        Connection conn = DbUtils.getConnection();
        Map<String, Object> nandumap = new HashMap<>();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ qufendudengji_s(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, teachUnit);
                pstat.setString(5, subCompose);
                pstat.setString(6, type);
                pstat.setString(7, studentType);
                pstat.setString(8, source);
                pstat.setString(9, teachUnit_s);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(Const.EXPORTREPORT_examNum, rs.getString(1));
                    map.put(Const.EXPORTREPORT_gradeNum, rs.getString(2));
                    map.put("statisticId", rs.getString(3));
                    map.put(Const.EXPORTREPORT_subjectNum, rs.getString(4));
                    map.put("exampaperNum", rs.getString(5));
                    map.put("qtype", rs.getString(6));
                    map.put("qTypeName", rs.getString(7));
                    map.put("sumCount", rs.getString(8));
                    map.put("sumfullScore", NumberUtil.toStr(Double.valueOf(rs.getDouble(9))));
                    map.put("qufendu", rs.getString(10));
                    map.put("count", rs.getString(11));
                    map.put("fullScore", NumberUtil.toStr(Double.valueOf(rs.getDouble(12))));
                    map.put("baifenbi", NumberUtil.toStr(Double.valueOf(rs.getDouble(13))));
                    map.put("questionNum", rs.getString(14));
                    map.put("questionName", rs.getString(15));
                    map.put("questionType", rs.getString(16));
                    rtnlist.add(map);
                    nandumap.put(rs.getString(6), rs.getString(6));
                }
                DbUtils.close(rs, pstat, conn);
                LinkedHashMap<Object, List<Map<String, Object>>> maplist = (LinkedHashMap) rtnlist.stream().collect(Collectors.groupingBy(m -> {
                    return m.get("qtype");
                }, LinkedHashMap::new, Collectors.toList()));
                return maplist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List<List<Object>> getshitinandufenbu(String exampaperNum, String gradeNum, String subjectNum, String statisticId, String studentType, String type, String source, String subCompose, String teachUnit, String teachUnit_s, String qtype, String questionNum, String questionName) {
        String queFullScoreSql;
        String sql;
        Map<String, Object> args = new HashMap<>();
        args.put("exampaperNum", exampaperNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("qtype", qtype);
        args.put("statisticId", statisticId);
        args.put("questionNum", questionNum);
        args.put("questionName", questionName);
        args.put("teachUnit", teachUnit);
        args.put("teachUnit_s", teachUnit_s);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("type", type);
        args.put("source", source);
        args.put("subCompose", subCompose);
        Object totalScore = this.dao._queryStr("select totalScore from exampaper where exampaperNum={exampaperNum} and isDelete='F'", args);
        if ("".equals(questionNum) || questionNum == null || "null".equals(questionNum)) {
            queFullScoreSql = "select sum(fullScore) fullScore from ( select exampaperNum,id questionNum,questionNum questionName,fullScore fullScore from define where exampaperNum={exampaperNum} and questionType={qtype} and isParent<>1 union all  select exampaperNum,id questionNum,questionNum questionName,fullScore fullScore  from subdefine where exampaperNum={exampaperNum} and questionType={qtype}  )a ";
        } else {
            queFullScoreSql = "select fullScore fullScore from define where exampaperNum={exampaperNum} and id={questionNum}  union all   select fullScore fullScore  from subdefine where exampaperNum={exampaperNum} and id={questionNum}";
        }
        String fullScore = this.dao._queryStr(queFullScoreSql, args);
        args.put("fullScore", fullScore);
        args.put("totalScore", totalScore);
        String scoreStr = "";
        String sqtOrOqtStr = "";
        if (qtype.equals("0")) {
            sqtOrOqtStr = "oqts";
            scoreStr = " left join objectivescore s on  s.questionNum=d.questionNum";
        } else if (qtype.equals("1")) {
            sqtOrOqtStr = "sqts";
            scoreStr = " left join score s on  s.questionNum=d.questionNum";
        }
        if (teachUnit_s.equals("00")) {
            if ("".equals(questionNum) || questionNum == null || "null".equals(questionNum)) {
                sql = "select exampaperNum,gradeNum,subjectNum,totalScore," + sqtOrOqtStr + " questionScore from statisticstudentlevel st  where st.exampaperNum={exampaperNum} and st.gradeNum={gradeNum} and st.statisticId={teachUnit}  and xuankezuhe={subCompose} and studentType={studentType} and statisticType={type} and source={source} order by totalScore";
            } else {
                sql = "SELECT st.studentId,st.classNum ,st.totalScore,'" + questionNum + "' questionNum,'" + questionName + "' questionName,sum(m.questionScore) questionScore,sum(m.fullScore) FROM  statisticstudentlevel  st  left join  ( select d.exampaperNum,s.studentId,s.questionNum,d.questionName,s.questionScore questionScore,d.fullScore fullScore from  ( select exampaperNum,id questionNum,questionNum questionName,fullScore fullScore from define where exampaperNum={exampaperNum} and id={questionNum}  union all  select exampaperNum,id questionNum,questionNum questionName,fullScore fullScore  from subdefine where exampaperNum={exampaperNum} and (pid={questionNum} or id={questionNum})   ) d " + scoreStr + "  where s.exampaperNum={exampaperNum}  order by studentId,questionNum ) m on st.exampaperNum=m.exampaperNum and st.studentId=m.studentId where st.exampaperNum={exampaperNum} and st.gradeNum={gradeNum} and st.statisticId={teachUnit} and xuankezuhe={subCompose} and studentType={studentType} and statisticType={type} and source={source}  and m.questionScore is not null   group by studentId order by totalScore desc ";
            }
        } else {
            String statisticStr = "";
            if (teachUnit_s.equals("01")) {
                statisticStr = " and st.schoolNum={teachUnit}";
            } else if (teachUnit_s.equals("02")) {
                statisticStr = " and st.classNum={teachUnit}";
            }
            if ("".equals(questionNum) || questionNum == null || "null".equals(questionNum)) {
                sql = "select exampaperNum,gradeNum,subjectNum,totalScore," + sqtOrOqtStr + " questionScore from studentlevel st  where st.exampaperNum={exampaperNum} and st.gradeNum={gradeNum} " + statisticStr + "  and xuankezuhe={subCompose} and studentType={studentType} and statisticType={type} and source={source} order by totalScore ";
            } else {
                sql = "SELECT st.studentId,st.classNum ,st.totalScore,'" + questionNum + "' questionNum,'" + questionName + "' questionName,sum(m.questionScore) questionScore,sum(m.fullScore)  FROM  studentlevel  st  left join  ( select d.exampaperNum,s.studentId,s.questionNum,d.questionName,s.questionScore questionScore,d.fullScore fullScore from  ( select exampaperNum,id questionNum,questionNum questionName,fullScore fullScore from define where exampaperNum={exampaperNum} and id={questionNum}  union all  select exampaperNum,id questionNum,questionNum questionName,fullScore fullScore  from subdefine where exampaperNum={exampaperNum} and (pid={questionNum} or id={questionNum})  ) d " + scoreStr + "  where s.exampaperNum={exampaperNum}  order by studentId,questionNum ) m on st.exampaperNum=m.exampaperNum and st.studentId=m.studentId where st.exampaperNum={exampaperNum} and st.gradeNum={gradeNum} " + statisticStr + "   and xuankezuhe={subCompose} and studentType={studentType} and statisticType={type} and source={source}  and m.questionScore is not null   group by studentId order by totalScore desc ";
            }
        }
        List<Map<String, Object>> dataList = this.dao._queryMapList(sql, TypeEnum.StringObject, args);
        List<Map<String, Object>> stepList = new ArrayList<>();
        Map<String, Object> stepMap = new HashMap<>();
        stepMap.put("start", 0);
        stepMap.put("end", 0);
        stepMap.put("duibibandu", Double.valueOf(0.0d));
        stepList.add(stepMap);
        DecimalFormat df = new DecimalFormat("0.00");
        for (int i = 0; i < Convert.toFloat(totalScore).floatValue(); i += 5) {
            int k = i + 5;
            double duibidu = Convert.toDouble(Integer.valueOf(i + k)).doubleValue() / 2.0d;
            Map<String, Object> stepMap2 = new HashMap<>();
            stepMap2.put("start", Integer.valueOf(i));
            stepMap2.put("end", Integer.valueOf(k));
            stepMap2.put("duibibandu", Double.valueOf(duibidu / Convert.toFloat(totalScore).floatValue()));
            stepList.add(stepMap2);
        }
        List<List<Object>> data = new ArrayList<>();
        for (int k2 = 0; k2 < stepList.size(); k2++) {
            List<Object> list = new ArrayList<>();
            Integer start = Convert.toInt(stepList.get(k2).get("start"));
            Integer end = Convert.toInt(stepList.get(k2).get("end"));
            float duibibandu = Convert.toFloat(stepList.get(k2).get("duibibandu")).floatValue();
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (int m = 0; m < dataList.size(); m++) {
                new HashMap();
                if (Convert.toFloat(dataList.get(m).get("totalScore")).floatValue() > start.intValue() && Convert.toFloat(dataList.get(m).get("totalScore")).floatValue() <= end.intValue()) {
                    mapList.add(dataList.get(m));
                }
            }
            float sum = 0.0f;
            float avg = 0.0f;
            float nandu = 0.0f;
            if (mapList.size() > 0) {
                for (int i2 = 0; i2 < mapList.size(); i2++) {
                    sum = (float) (sum + Convert.toDouble(mapList.get(i2).get("questionScore")).doubleValue());
                }
                avg = sum / mapList.size();
                nandu = avg / Convert.toFloat(fullScore).floatValue();
            }
            list.add(start);
            list.add(end);
            list.add(exampaperNum);
            list.add(gradeNum);
            list.add(subjectNum);
            list.add(totalScore);
            list.add(questionNum);
            list.add(questionName);
            list.add(Integer.valueOf(mapList.size()));
            list.add(Float.valueOf(avg));
            if (mapList.size() <= 0) {
                list.add(null);
            } else {
                list.add(df.format(nandu));
            }
            list.add(df.format(duibibandu));
            data.add(list);
        }
        return data;
    }

    /* JADX WARN: Finally extract failed */
    @Override // com.dmj.service.reportManagement.ReportService
    public List getQuesScore(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String history, String subCompose, String teachUnit, String contrastObject, String teachUnit_s, String contrast_s) {
        List rtnlist = new ArrayList();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ toTeacher_class_question_score(?,?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, teachUnit);
                pstat.setString(5, teachUnit_s);
                pstat.setString(6, contrastObject);
                pstat.setString(7, contrast_s);
                pstat.setString(8, type);
                pstat.setString(9, studentType);
                pstat.setString(10, source);
                pstat.setString(11, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                String preId = "";
                int fRow = 0;
                while (rs.next()) {
                    Object[] objArr = new Object[count];
                    for (int i = 0; i < objArr.length; i++) {
                        if (i == 0) {
                            String currId = String.valueOf(rs.getObject(i + 1));
                            if (fRow == 0) {
                                Define def = new Define();
                                def.setId(String.valueOf(rs.getObject(i + 14)));
                                def.setQuestionNum(String.valueOf(rs.getObject(i + 3)));
                                def.setFullScore(Float.valueOf(String.valueOf(rs.getObject(i + 4))));
                                def.setQuestionType(String.valueOf(rs.getObject(i + 10)));
                                def.setNumofStudent(String.valueOf(rs.getObject(i + 16)));
                                arrayList.add(def);
                                preId = String.valueOf(rs.getObject(i + 1));
                            } else if (currId.equals(preId)) {
                                Define def2 = new Define();
                                def2.setId(String.valueOf(rs.getObject(i + 14)));
                                def2.setQuestionNum(String.valueOf(rs.getObject(i + 3)));
                                def2.setFullScore(Float.valueOf(String.valueOf(rs.getObject(i + 4))));
                                def2.setQuestionType(String.valueOf(rs.getObject(i + 10)));
                                def2.setNumofStudent(String.valueOf(rs.getObject(i + 16)));
                                arrayList.add(def2);
                                preId = String.valueOf(rs.getObject(i + 1));
                            }
                        }
                        objArr[i] = rs.getObject(i + 1);
                    }
                    arrayList2.add(objArr);
                    fRow = 1;
                }
                DbUtils.close(rs, pstat, conn);
                String classNum0 = "";
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args.put("statisticType", type);
                args.put(Const.EXPORTREPORT_studentType, studentType);
                args.put("source", source);
                args.put("xuankezuhe", subCompose);
                String exampaperNum = this.dao._queryStr("SELECT examPaperNum from exampaper WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum}", args);
                args.put("exampaperNum", exampaperNum);
                int j = arrayList2.size();
                int i2 = 0;
                while (i2 < arrayList2.size()) {
                    String classNum1 = Convert.toStr(((Object[]) arrayList2.get(i2))[0]);
                    if (!classNum1.equals(classNum0) && i2 != 0) {
                        args.put(Const.EXPORTREPORT_classNum, classNum0);
                        String teachOrContrast_s = Convert.toStr(((Object[]) arrayList2.get(i2 - 1))[14]);
                        String className = Convert.toStr(((Object[]) arrayList2.get(i2 - 1))[4]);
                        args.put("className", className);
                        String sqlall = "";
                        if (teachOrContrast_s.equals("00")) {
                            sqlall = "SELECT statisticId,-1 c_order,'整卷' questionNum,fullScore,{className},'' teacher,'' classDirector,IFNULL(average/fullScore,-1) score,IF(average=0,0,sd/average) cv,'' qtype,ifnull(average,-1) avgscore,'','','','00','' from statisticlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND statisticId={classNum} limit 1";
                        } else if (teachOrContrast_s.equals("01")) {
                            sqlall = "SELECT schoolNum,-1 c_order,'整卷' questionNum,fullScore,{className},'' teacher,'' classDirector,IFNULL(average/fullScore,-1) score,IF(average=0,0,sd/average) cv,'' qtype,ifnull(average,-1) avgscore,'','','','01','' from gradelevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND schoolNum={classNum} limit 1";
                        } else if (teachOrContrast_s.equals("02")) {
                            sqlall = "SELECT classNum,-1 c_order,'整卷' questionNum,fullScore,{className},'' teacher,'' classDirector,IFNULL(average/fullScore,-1) score,IF(average=0,0,sd/average) cv,'' qtype,ifnull(average,-1) avgscore,'','','','02','' from classlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND classNum={classNum} limit 1";
                        }
                        arrayList2.add(i2, this.dao._queryArray(sqlall, args));
                        i2++;
                        j++;
                    }
                    if (i2 == j - 1) {
                        args.put(Const.EXPORTREPORT_classNum, classNum0);
                        String teachOrContrast_s2 = Convert.toStr(((Object[]) arrayList2.get(i2))[14]);
                        String className2 = Convert.toStr(((Object[]) arrayList2.get(i2))[4]);
                        args.put("className", className2);
                        String sqlall2 = "";
                        if (teachOrContrast_s2.equals("00")) {
                            sqlall2 = "SELECT statisticId,-1 c_order,'整卷' questionNum,fullScore,{className},'' teacher,'' classDirector,IFNULL(average/fullScore,-1) score,IF(average=0,0,sd/average) cv,'' qtype,ifnull(average,-1) avgscore,'','','','00','' from statisticlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND statisticId={classNum} limit 1";
                        } else if (teachOrContrast_s2.equals("01")) {
                            sqlall2 = "SELECT schoolNum,-1 c_order,'整卷' questionNum,fullScore,{className},'' teacher,'' classDirector,IFNULL(average/fullScore,-1) score,IF(average=0,0,sd/average) cv,'' qtype,ifnull(average,-1) avgscore,'','','','01','' from gradelevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND schoolNum={classNum} limit 1";
                        } else if (teachOrContrast_s2.equals("02")) {
                            sqlall2 = "SELECT classNum,-1 c_order,'整卷' questionNum,fullScore,{className},'' teacher,'' classDirector,IFNULL(average/fullScore,-1) score,IF(average=0,0,sd/average) cv,'' qtype,ifnull(average,-1) avgscore,'','','','02','' from classlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND classNum={classNum} limit 1";
                        }
                        Object[] listall = this.dao._queryArray(sqlall2, args);
                        arrayList2.add(i2 + 1, listall);
                        Define def3 = new Define();
                        def3.setId("999");
                        def3.setQuestionNum("整卷");
                        def3.setFullScore(Convert.toFloat(listall[3]));
                        def3.setQuestionType("");
                        arrayList.add(def3);
                        i2++;
                        j++;
                    }
                    classNum0 = classNum1;
                    i2++;
                }
                rtnlist.add(arrayList);
                rtnlist.add(arrayList2);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public String getjisuantype(String exam, String grade, String subject) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, exam);
        args.put(Const.EXPORTREPORT_gradeNum, grade);
        args.put(Const.EXPORTREPORT_subjectNum, subject);
        String jisuanType = this.dao._queryStr("select  jisuanType from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}", args);
        return jisuanType;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public String idhavechoose(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        String exampaperNum = this.dao._queryStr("select exampaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}", args);
        args.put("exampaperNum", exampaperNum);
        return this.dao._queryStr("select count(1) from (SELECT id from define d WHERE (examPaperNum={exampaperNum} or category={exampaperNum}) AND choosename <>'T' AND choosename <>'s'  UNION SELECT id from subdefine sd WHERE (examPaperNum={exampaperNum} or category={exampaperNum}) AND choosename <>'T' AND choosename <>'s') d", args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getObjOption(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String history, String subCompose) {
        ArrayList arrayList = new ArrayList();
        Map<String, String> optMap = new HashMap<>();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ toTeacher_class_Objective_option_analy(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, type);
                pstat.setString(7, studentType);
                pstat.setString(8, source);
                pstat.setString(9, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                boolean flg = false;
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                String quesNum = "";
                String claNum = "";
                while (rs.next()) {
                    String[] rowArr = new String[count];
                    String curQuesNum = rs.getString(3);
                    String curClaNum = rs.getString(2);
                    if (flg && (!quesNum.equals(curQuesNum) || !claNum.equals(curClaNum))) {
                        if (!quesNum.equals(curQuesNum)) {
                            arrayList2.add(arrayList3);
                            arrayList3 = new ArrayList();
                            arrayList.add(arrayList2);
                            arrayList2 = new ArrayList();
                        }
                        if (quesNum.equals(curQuesNum) && !claNum.equals(curClaNum)) {
                            arrayList2.add(arrayList3);
                            arrayList3 = new ArrayList();
                        }
                    }
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = String.valueOf(rs.getObject(i + 1));
                        if (i == 7) {
                            optMap.put(rs.getString(i + 1), rs.getString(i + 1));
                        }
                    }
                    arrayList3.add(rowArr);
                    quesNum = rs.getString(3);
                    claNum = rs.getString(2);
                    flg = true;
                }
                arrayList2.add(arrayList3);
                arrayList.add(arrayList2);
                DbUtils.close(rs, pstat, conn);
                List myList = new ArrayList();
                myList.add(arrayList);
                Object[] key = optMap.keySet().toArray();
                Arrays.sort(key);
                myList.add(key);
                return myList;
            } catch (SQLException e) {
                this.log.info("", e);
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getKnledgScore(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String history, String subCompose, String teachUnit, String teachUnit_s, String contrastObject, String contrast_s) {
        List rtnlist = new ArrayList();
        new HashMap();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ toTeacher_class_knowledge_score(?,?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, teachUnit);
                pstat.setString(5, teachUnit_s);
                pstat.setString(6, contrastObject);
                pstat.setString(7, contrast_s);
                pstat.setString(8, type);
                pstat.setString(9, studentType);
                pstat.setString(10, source);
                pstat.setString(11, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                String claNum = "";
                boolean flg = true;
                while (rs.next()) {
                    String curClaNum = rs.getString(2);
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    if ("".equals(claNum) || (claNum.equals(curClaNum) && flg)) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("ext3", rs.getString(4));
                        hashMap.put("knoName", rs.getString(5));
                        hashMap.put("ext1", rs.getString(6));
                        hashMap.put("questionName", rs.getString(12));
                        arrayList.add(hashMap);
                    } else {
                        flg = false;
                    }
                    claNum = curClaNum;
                    arrayList2.add(rowArr);
                }
                String classNum0 = "";
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args.put("statisticType", type);
                args.put(Const.EXPORTREPORT_studentType, studentType);
                args.put("source", source);
                args.put("xuankezuhe", subCompose);
                String exampaperNum = this.dao._queryStr("SELECT examPaperNum from exampaper WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum}", args);
                args.put("exampaperNum", exampaperNum);
                int j = arrayList2.size();
                int i2 = 0;
                while (i2 < arrayList2.size()) {
                    String classNum1 = Convert.toStr(((Object[]) arrayList2.get(i2))[1]);
                    if (!classNum1.equals(classNum0) && i2 != 0) {
                        args.put(Const.EXPORTREPORT_classNum, classNum0);
                        String teachOrContrast_s = Convert.toStr(((Object[]) arrayList2.get(i2 - 1))[12]);
                        String sqlall = "";
                        if (teachOrContrast_s.equals("00")) {
                            sqlall = "SELECT -1 c_order,statisticId,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','00' from statisticlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND statisticId={classNum} limit 1";
                        } else if (teachOrContrast_s.equals("01")) {
                            sqlall = "SELECT -1 c_order,schoolNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','01' from gradelevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND schoolNum={classNum} limit 1";
                        } else if (teachOrContrast_s.equals("02")) {
                            sqlall = "SELECT -1 c_order,classNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','02' from classlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND classNum={classNum} limit 1";
                        }
                        arrayList2.add(i2, this.dao._queryArray(sqlall, args));
                        i2++;
                        j++;
                    }
                    if (i2 == arrayList2.size() - 1) {
                        args.put(Const.EXPORTREPORT_classNum, classNum0);
                        String teachOrContrast_s2 = Convert.toStr(((Object[]) arrayList2.get(i2))[12]);
                        String sqlall2 = "";
                        if (teachOrContrast_s2.equals("00")) {
                            sqlall2 = "SELECT -1 c_order,statisticId,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','00' from statisticlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND statisticId={classNum} limit 1";
                        } else if (teachOrContrast_s2.equals("01")) {
                            sqlall2 = "SELECT -1 c_order,schoolNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','01' from gradelevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND schoolNum={classNum} limit 1";
                        } else if (teachOrContrast_s2.equals("02")) {
                            sqlall2 = "SELECT -1 c_order,classNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','02' from classlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND classNum={classNum} limit 1";
                        }
                        Object[] listall = this.dao._queryArray(sqlall2, args);
                        arrayList2.add(i2 + 1, listall);
                        HashMap hashMap2 = new HashMap();
                        hashMap2.put("ext3", Convert.toStr(listall[3]));
                        hashMap2.put("knoName", Convert.toStr(listall[4]));
                        hashMap2.put("ext1", Convert.toStr(listall[5]));
                        hashMap2.put("questionName", "");
                        arrayList.add(hashMap2);
                        i2++;
                        j++;
                    }
                    classNum0 = classNum1;
                    i2++;
                }
                rtnlist.add(arrayList);
                rtnlist.add(arrayList2);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getKnowScoreRank(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String step, String history) {
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ toTeacher_know_score_rank(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, step);
                pstat.setString(7, type);
                pstat.setString(8, studentType);
                pstat.setString(9, source);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                String knowNum = "";
                String claNum = "";
                boolean flg = false;
                while (rs.next()) {
                    Object[] rowArr = new Object[count];
                    String curknowNum = rs.getString(1);
                    String curClaNum = rs.getString(4);
                    if (flg && (!knowNum.equals(curknowNum) || !claNum.equals(curClaNum))) {
                        if (!knowNum.equals(curknowNum)) {
                            arrayList.add(arrayList2);
                            arrayList2 = new ArrayList();
                            rtnlist.add(arrayList);
                            arrayList = new ArrayList();
                        }
                        if (knowNum.equals(curknowNum) && !claNum.equals(curClaNum)) {
                            arrayList.add(arrayList2);
                            arrayList2 = new ArrayList();
                        }
                    }
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    arrayList2.add(rowArr);
                    knowNum = curknowNum;
                    claNum = curClaNum;
                    flg = true;
                }
                arrayList.add(arrayList2);
                rtnlist.add(arrayList);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // com.dmj.service.reportManagement.ReportService
    public List getAblityScore(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String history, String subCompose, String teachUnit, String teachUnit_s, String contrastObject, String contrast_s) {
        List rtnlist = new ArrayList();
        new HashMap();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                if (!"null".equals(examNum) && !"".equals(examNum) && !"null".equals(subjectNum) && !"".equals(subjectNum) && !"null".equals(gradeNum) && !"".equals(gradeNum)) {
                    pstat = conn.prepareCall("{call /* shard_host_HG=Read */ toTeacher_class_ability_score(?,?,?,?,?,?,?,?,?,?,?)}");
                    pstat.setString(1, examNum);
                    pstat.setString(2, subjectNum);
                    pstat.setString(3, gradeNum);
                    pstat.setString(4, teachUnit);
                    pstat.setString(5, teachUnit_s);
                    pstat.setString(6, contrastObject);
                    pstat.setString(7, contrast_s);
                    pstat.setString(8, type.equals("null") ? "" : type);
                    pstat.setString(9, studentType.equals("null") ? "" : studentType);
                    pstat.setString(10, source.equals("null") ? "" : source);
                    pstat.setString(11, subCompose.equals("null") ? "" : subCompose);
                    pstat.executeQuery();
                    rs = pstat.getResultSet();
                    int count = rs.getMetaData().getColumnCount();
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    String claNum = "";
                    boolean flg = true;
                    while (rs.next()) {
                        String curClaNum = rs.getString(2);
                        Object[] rowArr = new Object[count];
                        for (int i = 0; i < rowArr.length; i++) {
                            rowArr[i] = rs.getObject(i + 1);
                        }
                        if ("".equals(claNum) || (claNum.equals(curClaNum) && flg)) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("ext3", rs.getString(4));
                            hashMap.put("knoName", rs.getString(5));
                            hashMap.put("ext1", rs.getString(6));
                            hashMap.put("questionName", rs.getString(12));
                            arrayList.add(hashMap);
                        } else {
                            flg = false;
                        }
                        claNum = curClaNum;
                        arrayList2.add(rowArr);
                    }
                    String classNum0 = "";
                    Map args = new HashMap();
                    args.put(Const.EXPORTREPORT_examNum, examNum);
                    args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                    args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                    args.put("statisticType", type);
                    args.put(Const.EXPORTREPORT_studentType, studentType);
                    args.put("source", source);
                    args.put("xuankezuhe", subCompose);
                    String exampaperNum = this.dao._queryStr("SELECT examPaperNum from exampaper WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum}", args);
                    args.put("exampaperNum", exampaperNum);
                    int j = arrayList2.size();
                    int i2 = 0;
                    while (i2 < arrayList2.size()) {
                        String classNum1 = Convert.toStr(((Object[]) arrayList2.get(i2))[1]);
                        if (!classNum1.equals(classNum0) && i2 != 0) {
                            args.put(Const.EXPORTREPORT_classNum, classNum0);
                            String teachOrContrast_s = Convert.toStr(((Object[]) arrayList2.get(i2 - 1))[12]);
                            String sqlall = "";
                            if (teachOrContrast_s.equals("00")) {
                                sqlall = "SELECT -1 c_order,statisticId,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','00' from statisticlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND statisticId={classNum} limit 1";
                            } else if (teachOrContrast_s.equals("01")) {
                                sqlall = "SELECT -1 c_order,schoolNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','01' from gradelevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND schoolNum={classNum} limit 1";
                            } else if (teachOrContrast_s.equals("02")) {
                                sqlall = "SELECT -1 c_order,classNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','02' from classlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND classNum={classNum} limit 1";
                            }
                            arrayList2.add(i2, this.dao._queryArray(sqlall, args));
                            i2++;
                            j++;
                        }
                        if (i2 == arrayList2.size() - 1) {
                            args.put(Const.EXPORTREPORT_classNum, classNum0);
                            String teachOrContrast_s2 = Convert.toStr(((Object[]) arrayList2.get(i2))[12]);
                            String sqlall2 = "";
                            if (teachOrContrast_s2.equals("00")) {
                                sqlall2 = "SELECT -1 c_order,statisticId,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','00' from statisticlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND statisticId={classNum} limit 1";
                            } else if (teachOrContrast_s2.equals("01")) {
                                sqlall2 = "SELECT -1 c_order,schoolNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','01' from gradelevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND schoolNum={classNum} limit 1";
                            } else if (teachOrContrast_s2.equals("02")) {
                                sqlall2 = "SELECT -1 c_order,classNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','02' from classlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND classNum={classNum} limit 1";
                            }
                            Object[] listall = this.dao._queryArray(sqlall2, args);
                            arrayList2.add(i2 + 1, listall);
                            HashMap hashMap2 = new HashMap();
                            hashMap2.put("ext3", Convert.toStr(listall[3]));
                            hashMap2.put("knoName", Convert.toStr(listall[4]));
                            hashMap2.put("ext1", Convert.toStr(listall[5]));
                            hashMap2.put("questionName", "");
                            arrayList.add(hashMap2);
                            i2++;
                            j++;
                        }
                        classNum0 = classNum1;
                        i2++;
                    }
                    rtnlist.add(arrayList);
                    rtnlist.add(arrayList2);
                }
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                e.printStackTrace();
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getAblityScoreRank(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String step, String history) {
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ toTeacher_ability_score_rank(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, step);
                pstat.setString(7, type);
                pstat.setString(8, studentType);
                pstat.setString(9, source);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                String knowNum = "";
                String claNum = "";
                boolean flg = false;
                while (rs.next()) {
                    Object[] rowArr = new Object[count];
                    String curknowNum = rs.getString(1);
                    String curClaNum = rs.getString(4);
                    if (flg && (!knowNum.equals(curknowNum) || !claNum.equals(curClaNum))) {
                        if (!knowNum.equals(curknowNum)) {
                            arrayList.add(arrayList2);
                            arrayList2 = new ArrayList();
                            rtnlist.add(arrayList);
                            arrayList = new ArrayList();
                        }
                        if (knowNum.equals(curknowNum) && !claNum.equals(curClaNum)) {
                            arrayList.add(arrayList2);
                            arrayList2 = new ArrayList();
                        }
                    }
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    arrayList2.add(rowArr);
                    knowNum = curknowNum;
                    claNum = curClaNum;
                    flg = true;
                }
                arrayList.add(arrayList2);
                rtnlist.add(arrayList);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getQtypeScore(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String history, String subCompose, String teachUnit, String teachUnit_s, String contrastObject, String contrast_s) {
        List rtnlist = new ArrayList();
        new HashMap();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ toTeacher_class_questiontype_score(?,?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, teachUnit);
                pstat.setString(5, teachUnit_s);
                pstat.setString(6, contrastObject);
                pstat.setString(7, contrast_s);
                pstat.setString(8, type);
                pstat.setString(9, studentType);
                pstat.setString(10, source);
                pstat.setString(11, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                String claNum = "";
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                boolean flg = true;
                while (rs.next()) {
                    String curClaNum = "";
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        if (i == 1) {
                            curClaNum = rs.getString(i + 1);
                        }
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    if ("".equals(claNum) || (claNum.equals(curClaNum) && flg)) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("ext1", rs.getString(4));
                        hashMap.put("ext2", rs.getString(5));
                        hashMap.put("ext3", rs.getString(6));
                        hashMap.put("questionName", rs.getString(12));
                        arrayList2.add(hashMap);
                    } else {
                        flg = false;
                    }
                    claNum = curClaNum;
                    arrayList.add(rowArr);
                }
                String classNum0 = "";
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args.put("statisticType", type);
                args.put(Const.EXPORTREPORT_studentType, studentType);
                args.put("source", source);
                args.put("xuankezuhe", subCompose);
                String exampaperNum = this.dao._queryStr("SELECT examPaperNum from exampaper WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum}", args);
                args.put("exampaperNum", exampaperNum);
                int j = arrayList.size();
                int i2 = 0;
                while (i2 < arrayList.size()) {
                    String classNum1 = Convert.toStr(((Object[]) arrayList.get(i2))[1]);
                    if (!classNum1.equals(classNum0) && i2 != 0) {
                        args.put(Const.EXPORTREPORT_classNum, classNum0);
                        String teachOrContrast_s = Convert.toStr(((Object[]) arrayList.get(i2 - 1))[12]);
                        String sqlall = "";
                        if (teachOrContrast_s.equals("00")) {
                            sqlall = "SELECT -1 c_order,statisticId,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','00' from statisticlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND statisticId={classNum} limit 1";
                        } else if (teachOrContrast_s.equals("01")) {
                            sqlall = "SELECT -1 c_order,schoolNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','01' from gradelevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND schoolNum={classNum} limit 1";
                        } else if (teachOrContrast_s.equals("02")) {
                            sqlall = "SELECT -1 c_order,classNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','02' from classlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND classNum={classNum} limit 1";
                        }
                        arrayList.add(i2, this.dao._queryArray(sqlall, args));
                        i2++;
                        j++;
                    }
                    if (i2 == arrayList.size() - 1) {
                        args.put(Const.EXPORTREPORT_classNum, classNum0);
                        String teachOrContrast_s2 = Convert.toStr(((Object[]) arrayList.get(i2))[12]);
                        String sqlall2 = "";
                        if (teachOrContrast_s2.equals("00")) {
                            sqlall2 = "SELECT -1 c_order,statisticId,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','00' from statisticlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND statisticId={classNum} limit 1";
                        } else if (teachOrContrast_s2.equals("01")) {
                            sqlall2 = "SELECT -1 c_order,schoolNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','01' from gradelevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND schoolNum={classNum} limit 1";
                        } else if (teachOrContrast_s2.equals("02")) {
                            sqlall2 = "SELECT -1 c_order,classNum,'','','整卷',fullScore,IFNULL(average/fullScore,-1) rate,IF(average=0,0,sd/average) cv,'' teacher,'' classDirector,'','','02' from classlevel  where examPaperNum={exampaperNum} AND gradeNum={gradeNum} AND statisticType={statisticType}  AND studentType={studentType}  AND source={source} and xuankezuhe ={xuankezuhe} AND classNum={classNum} limit 1";
                        }
                        Object[] listall = this.dao._queryArray(sqlall2, args);
                        arrayList.add(i2 + 1, listall);
                        HashMap hashMap2 = new HashMap();
                        hashMap2.put("ext1", Convert.toStr(listall[3]));
                        hashMap2.put("ext2", Convert.toStr(listall[4]));
                        hashMap2.put("ext3", Convert.toStr(listall[5]));
                        hashMap2.put("questionName", "");
                        arrayList2.add(hashMap2);
                        i2++;
                        j++;
                    }
                    classNum0 = classNum1;
                    i2++;
                }
                rtnlist.add(arrayList2);
                rtnlist.add(arrayList);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getQtypeScoreRank(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String step, String history) {
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ toTeacher_questiontype_score_rank(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, step);
                pstat.setString(7, type);
                pstat.setString(8, studentType);
                pstat.setString(9, source);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                String qTypeNum = "";
                String claNum = "";
                boolean flg = false;
                while (rs.next()) {
                    Object[] rowArr = new Object[count];
                    String curqTypeNum = rs.getString(1);
                    String curClaNum = rs.getString(4);
                    if (flg && (!qTypeNum.equals(curqTypeNum) || !claNum.equals(curClaNum))) {
                        if (!qTypeNum.equals(curqTypeNum)) {
                            arrayList.add(arrayList2);
                            arrayList2 = new ArrayList();
                            rtnlist.add(arrayList);
                            arrayList = new ArrayList();
                        }
                        if (qTypeNum.equals(curqTypeNum) && !claNum.equals(curClaNum)) {
                            arrayList.add(arrayList2);
                            arrayList2 = new ArrayList();
                        }
                    }
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    arrayList2.add(rowArr);
                    qTypeNum = curqTypeNum;
                    claNum = curClaNum;
                    flg = true;
                }
                arrayList.add(arrayList2);
                rtnlist.add(arrayList);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getScoreSectionRank(String areaval, String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String step, String history, String fufen, String subCompose, String studentReportShowItem) {
        String sql;
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ allsubjectScore_moreClass_onescore_rank_compare_fufen(?,?,?,?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ allsubjectScore_moreClass_onescore_rank_compare(?,?,?,?,?,?,?,?,?,?,?,?)}";
        }
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, "".equals(areaval) ? null : areaval);
                pstat.setString(2, examNum);
                pstat.setString(3, subjectNum);
                pstat.setString(4, gradeNum);
                pstat.setString(5, schoolNum);
                pstat.setString(6, classNum);
                pstat.setString(7, step);
                pstat.setString(8, type);
                pstat.setString(9, studentType);
                pstat.setString(10, source);
                pstat.setString(11, subCompose);
                pstat.setString(12, studentReportShowItem);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                String claNum = "";
                ArrayList arrayList = new ArrayList();
                int preLeijirenshu = 0;
                while (rs.next()) {
                    String curClaNum = rs.getString(5);
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        if (i == 9) {
                            int cell9 = rs.getInt(i + 1);
                            if (cell9 > preLeijirenshu) {
                                rowArr[i] = Integer.valueOf(cell9);
                                preLeijirenshu = cell9;
                            } else {
                                rowArr[i] = Integer.valueOf(preLeijirenshu);
                            }
                        } else {
                            rowArr[i] = rs.getObject(i + 1);
                        }
                    }
                    if (!"".equals(claNum) && !claNum.equals(curClaNum)) {
                        rtnlist.add(arrayList);
                        arrayList = new ArrayList();
                        preLeijirenshu = 0;
                    }
                    arrayList.add(rowArr);
                    claNum = curClaNum;
                }
                if (arrayList.size() > 0) {
                    rtnlist.add(arrayList);
                }
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getScoreSectionRank2(String areaval, String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String step, String history, String fufen, String subCompose, String islevel, String teachUnit, String teachUnit_s, String contrastObject, String contrast_s) {
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ allsubjectScore_moreClass_onescore_rank_compare_new_2(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, teachUnit);
                pstat.setString(5, contrastObject);
                pstat.setString(6, step);
                pstat.setString(7, type);
                pstat.setString(8, studentType);
                pstat.setString(9, source);
                pstat.setString(10, subCompose);
                pstat.setString(11, islevel);
                pstat.setString(12, teachUnit_s);
                pstat.setString(13, contrast_s);
                pstat.setString(14, fufen);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                String claNum = "";
                ArrayList arrayList = new ArrayList();
                while (rs.next()) {
                    String curClaNum = rs.getString(5);
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    if (!"".equals(claNum) && !claNum.equals(curClaNum)) {
                        rtnlist.add(arrayList);
                        arrayList = new ArrayList();
                    }
                    arrayList.add(rowArr);
                    claNum = curClaNum;
                }
                if (arrayList.size() > 0) {
                    rtnlist.add(arrayList);
                }
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List gettopStepHeader(String examNum, String gradeNum, String classNum, String type, String source, String subCompose, String teachUnit, String teachUnit_s, String contrastObject, String contrast_s) {
        String sql;
        if (teachUnit_s == "01") {
            sql = "select DISTINCT sl.subjectNum,sb.subjectName,0 as fufen from studentlevel sl INNER JOIN subject sb on sl.subjectNum=sb.subjectNum  where examNum={examNum} and schoolNum={teachUnit} and gradeNum={gradeNum} and xuankezuhe={subCompose}  and source={source} and statisticType={type}  UNION ALL  select DISTINCT sl.subjectNum,sb.subjectName,1 as fufen  from studentLevel_fufen sl INNER JOIN subject sb on sl.subjectNum=sb.subjectNum  where examNum={examNum} and schoolNum={teachUnit}  and gradeNum={gradeNum}  and xuankezuhe={subCompose}  and source={source} and statisticType={type} ";
        } else {
            sql = "select DISTINCT sl.subjectNum,sb.subjectName,0 as fufen from studentlevel sl INNER JOIN subject sb on sl.subjectNum=sb.subjectNum  where examNum={examNum} and classNum={teachUnit}  and gradeNum={gradeNum}  and xuankezuhe={subCompose}  and source={source} and statisticType={type} UNION ALL  select DISTINCT sl.subjectNum,sb.subjectName,1 as fufen  from studentLevel_fufen sl INNER JOIN subject sb on sl.subjectNum=sb.subjectNum where examNum={examNum}  and gradeNum={gradeNum}  and classNum={teachUnit} and xuankezuhe={subCompose}  and source={source} and statisticType={type}  ";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("teachUnit", teachUnit);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("subCompose", subCompose);
        args.put("source", source);
        args.put("type", type);
        return this.dao._queryArrayList(sql, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List gettopStepData(String xuanze, String examNum, String gradeNum, String subjectNum, String subCompose, String studentType, String statisticType, String source, String topStep2, String topStepPer, String teachUnit, String teachUnit_s, String contrastObject, String contrast_s) {
        String sql;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("subCompose", subCompose);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("statisticType", statisticType);
        args.put("source", source);
        args.put("teachUnit", teachUnit);
        args.put("teachUnit_s", teachUnit_s);
        args.put("contrastObject", contrastObject);
        args.put("contrast_s", contrast_s);
        if (xuanze.equals("0")) {
            sql = "call B9TopStep2_per({examNum},{gradeNum},{subjectNum},{subCompose},{studentType},{statisticType},{source},{topStep},{teachUnit},{teachUnit_s},{contrastObject},{contrast_s})";
            args.put("topStep", topStepPer);
        } else {
            sql = "call B9TopStep2({examNum},{gradeNum},{subjectNum},{subCompose},{studentType},{statisticType},{source},{topStep},{teachUnit},{teachUnit_s},{contrastObject},{contrast_s})";
            args.put("topStep", topStep2);
        }
        new ArrayList();
        List dataList = new ArrayList();
        try {
            List<List<Map<String, Object>>> lists = this.dao._queryMoreResultList(sql, args);
            if (lists.get(1).size() > 0) {
                List<Map<String, Object>> jiaoxuedanweiList = (List) lists.get(1).stream().filter(top -> {
                    return "0".equals(Convert.toStr(top.get("duibidueixian_qufen")));
                }).collect(Collectors.toList());
                Map<String, List<Map<String, Object>>> duibiduixiangMap = (Map) lists.get(1).stream().collect(Collectors.groupingBy(m -> {
                    return Convert.toStr(m.get(Const.EXPORTREPORT_step));
                }));
                for (int i = 0; i < lists.get(0).size(); i++) {
                    String step = Convert.toStr(lists.get(0).get(i).get(Const.EXPORTREPORT_step));
                    String[] jiaoxuedanweiAvgScores = Convert.toStr(jiaoxuedanweiList.get(i).get("avgScore")).split(Const.STRING_SEPERATOR);
                    List<Map<String, Object>> stepList = duibiduixiangMap.get(step);
                    for (int j = 0; j < jiaoxuedanweiAvgScores.length; j++) {
                        List<Float> list = new ArrayList<>();
                        for (int d = 0; d < stepList.size(); d++) {
                            String avgScore1 = Convert.toStr(stepList.get(d).get("avgScore")).split(Const.STRING_SEPERATOR)[j];
                            if (!avgScore1.equals("--")) {
                                Float avgScore = Convert.toFloat(Convert.toStr(stepList.get(d).get("avgScore")).split(Const.STRING_SEPERATOR)[j]);
                                list.add(avgScore);
                            }
                        }
                        Float min = Float.valueOf(0.0f);
                        if (list.size() != 0) {
                            min = (Float) Collections.min(list);
                        }
                        for (int d2 = 0; d2 < stepList.size(); d2++) {
                            String avgScore12 = Convert.toStr(stepList.get(d2).get("avgScore")).split(Const.STRING_SEPERATOR)[j];
                            if (!avgScore12.equals("--")) {
                                Float avgScore2 = Convert.toFloat(Convert.toStr(stepList.get(d2).get("avgScore")).split(Const.STRING_SEPERATOR)[j]);
                                if (Float.compare(min.floatValue(), avgScore2.floatValue()) == 0) {
                                    if (j == 0) {
                                        stepList.get(d2).put("teaIsMin", "true");
                                    } else {
                                        String teaIsMin = Convert.toStr(stepList.get(d2).get("teaIsMin"));
                                        stepList.get(d2).put("teaIsMin", teaIsMin + ",true");
                                    }
                                } else if (j == 0) {
                                    stepList.get(d2).put("teaIsMin", "false");
                                } else {
                                    String teaIsMin2 = Convert.toStr(stepList.get(d2).get("teaIsMin"));
                                    stepList.get(d2).put("teaIsMin", teaIsMin2 + ",false");
                                }
                            } else if (j == 0) {
                                stepList.get(d2).put("teaIsMin", "false");
                            } else {
                                String teaIsMin3 = Convert.toStr(stepList.get(d2).get("teaIsMin"));
                                stepList.get(d2).put("teaIsMin", teaIsMin3 + ",false");
                            }
                        }
                    }
                }
                List<Map<String, Object>> lists2 = new ArrayList<>();
                for (String key : duibiduixiangMap.keySet()) {
                    List<Map<String, Object>> maps = duibiduixiangMap.get(key);
                    lists2.addAll(maps);
                }
                List<Map<String, Object>> jiaoxuedanweiList2 = (List) lists2.stream().filter(top2 -> {
                    return "0".equals(Convert.toStr(top2.get("duibidueixian_qufen")));
                }).collect(Collectors.toList());
                jiaoxuedanweiList2.sort((m1, m2) -> {
                    return Convert.toInt(m1.get(Const.EXPORTREPORT_step)).compareTo(Convert.toInt(m2.get(Const.EXPORTREPORT_step)));
                });
                Map<String, List<Map<String, Object>>> duibiduixiangMap2 = (Map) ((List) lists2.stream().filter(top3 -> {
                    return "1".equals(Convert.toStr(top3.get("duibidueixian_qufen")));
                }).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(m3 -> {
                    return Convert.toStr(m3.get(Const.EXPORTREPORT_step));
                }));
                dataList.add(lists.get(0));
                dataList.add(lists.get(1));
                dataList.add(jiaoxuedanweiList2);
                dataList.add(duibiduixiangMap2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List gettopStepB10Data(String xuanze, String examNum, String gradeNum, String subjectNum, String subCompose, String studentType, String statisticType, String source, String topStep2, String teachUnit, String teachUnit_s, String contrastObject, String contrast_s) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("subCompose", subCompose);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("statisticType", statisticType);
        args.put("source", source);
        args.put("teachUnit", teachUnit);
        args.put("teachUnit_s", teachUnit_s);
        args.put("contrastObject", contrastObject);
        args.put("contrast_s", contrast_s);
        String sql = "";
        if (xuanze.equals("0")) {
            sql = "call B10TopStep2_per({type},{examNum},{gradeNum},{subjectNum},{subCompose},{studentType},{statisticType},{source},{topStep},{teachUnit},{teachUnit_s},{contrastObject},{contrast_s})";
            args.put("type", "0");
        } else if (xuanze.equals("1")) {
            sql = "call B10TopStep2({type},{examNum},{gradeNum},{subjectNum},{subCompose},{studentType},{statisticType},{source},{topStep},{teachUnit},{teachUnit_s},{contrastObject},{contrast_s})";
            args.put("type", "0");
        } else if (xuanze.equals("2")) {
            sql = "call B10TopStep2_per({type},{examNum},{gradeNum},{subjectNum},{subCompose},{studentType},{statisticType},{source},{topStep},{teachUnit},{teachUnit_s},{contrastObject},{contrast_s})";
            args.put("type", "1");
        } else if (xuanze.equals("3")) {
            sql = "call B10TopStep2({type},{examNum},{gradeNum},{subjectNum},{subCompose},{studentType},{statisticType},{source},{topStep},{teachUnit},{teachUnit_s},{contrastObject},{contrast_s})";
            args.put("type", "1");
        }
        args.put("topStep", topStep2);
        new ArrayList();
        List dataList = new ArrayList();
        try {
            List<List<Map<String, Object>>> lists = this.dao._queryMoreResultList(sql, args);
            if (lists.get(1) != null) {
                List<Map<String, Object>> jiaoxuedanweiList = (List) lists.get(1).stream().filter(top -> {
                    return "0".equals(Convert.toStr(top.get("duibidueixian_qufen")));
                }).collect(Collectors.toList());
                Map<String, List<Map<String, Object>>> duibiduixiangMap = (Map) ((List) lists.get(1).stream().collect(Collectors.toList())).stream().collect(Collectors.groupingBy(m -> {
                    return Convert.toStr(m.get("step_per"));
                }));
                for (int i = 0; i < lists.get(0).size(); i++) {
                    String step = Convert.toStr(lists.get(0).get(i).get(Const.EXPORTREPORT_step));
                    String[] jiaoxuedanweiAvgScores = Convert.toStr(jiaoxuedanweiList.get(i).get("avgScore")).split(Const.STRING_SEPERATOR);
                    List<Map<String, Object>> stepList = duibiduixiangMap.get(step);
                    for (int j = 0; j < jiaoxuedanweiAvgScores.length; j++) {
                        List<Float> list = new ArrayList<>();
                        List<Float> list_total = new ArrayList<>();
                        for (int d = 0; d < stepList.size(); d++) {
                            if (j == 0) {
                                String avgtotalScore = Convert.toStr(stepList.get(d).get("avgtotalScore"));
                                if (!avgtotalScore.equals("--")) {
                                    Float avgtotalScore_f = Convert.toFloat(stepList.get(d).get("avgtotalScore"));
                                    list_total.add(avgtotalScore_f);
                                }
                            }
                            if (!Convert.toStr(stepList.get(d).get("avgScore")).split(Const.STRING_SEPERATOR)[j].equals("--")) {
                                Float avgScore = Convert.toFloat(Convert.toStr(stepList.get(d).get("avgScore")).split(Const.STRING_SEPERATOR)[j]);
                                list.add(avgScore);
                            }
                        }
                        Float min_total = Float.valueOf(0.0f);
                        if (j == 0 && list_total.size() != 0) {
                            min_total = (Float) Collections.min(list_total);
                        }
                        Float min = Float.valueOf(0.0f);
                        if (list.size() != 0) {
                            min = (Float) Collections.min(list);
                        }
                        int gangCount_zongfen = 0;
                        int count_zongfen = 0;
                        for (int s = 0; s < list_total.size(); s++) {
                            if (Convert.toStr(list_total.get(s)).equals("--")) {
                                gangCount_zongfen++;
                            } else if (Convert.toStr(list_total.get(s)).equals(Convert.toStr(min_total))) {
                                count_zongfen++;
                            }
                        }
                        int gangCount = 0;
                        int count = 0;
                        for (int s2 = 0; s2 < list.size(); s2++) {
                            if (Convert.toStr(list.get(s2)).equals("--")) {
                                gangCount++;
                            } else if (Convert.toStr(list.get(s2)).equals(Convert.toStr(min))) {
                                count++;
                            }
                        }
                        for (int d2 = 0; d2 < stepList.size(); d2++) {
                            if (count == list.size() - gangCount) {
                                String str = Convert.toStr(stepList.get(d2).get("avgScore")).split(Const.STRING_SEPERATOR)[j];
                                if (j == 0) {
                                    stepList.get(d2).put("teaIsMin", "false");
                                } else {
                                    String teaIsMin = Convert.toStr(stepList.get(d2).get("teaIsMin"));
                                    stepList.get(d2).put("teaIsMin", teaIsMin + ",false");
                                }
                            } else {
                                String avgScore1 = Convert.toStr(stepList.get(d2).get("avgScore")).split(Const.STRING_SEPERATOR)[j];
                                if (!avgScore1.equals("--")) {
                                    Float avgScore2 = Convert.toFloat(avgScore1);
                                    if (Float.compare(min.floatValue(), avgScore2.floatValue()) == 0) {
                                        if (j == 0) {
                                            stepList.get(d2).put("teaIsMin", "true");
                                        } else {
                                            String teaIsMin2 = Convert.toStr(stepList.get(d2).get("teaIsMin"));
                                            stepList.get(d2).put("teaIsMin", teaIsMin2 + ",true");
                                        }
                                    } else if (j == 0) {
                                        stepList.get(d2).put("teaIsMin", "false");
                                    } else {
                                        String teaIsMin3 = Convert.toStr(stepList.get(d2).get("teaIsMin"));
                                        stepList.get(d2).put("teaIsMin", teaIsMin3 + ",false");
                                    }
                                } else if (j == 0) {
                                    stepList.get(d2).put("teaIsMin", "false");
                                } else {
                                    String teaIsMin4 = Convert.toStr(stepList.get(d2).get("teaIsMin"));
                                    stepList.get(d2).put("teaIsMin", teaIsMin4 + ",false");
                                }
                            }
                            if (count_zongfen == list_total.size() - gangCount_zongfen) {
                                if (j == 0) {
                                    Convert.toFloat(Convert.toStr(stepList.get(d2).get("avgtotalScore")));
                                    stepList.get(d2).put("teaIsMin_totalScore", "false");
                                }
                            } else if (j == 0) {
                                String avgScore_total1 = Convert.toStr(stepList.get(d2).get("avgtotalScore"));
                                if (!avgScore_total1.equals("--")) {
                                    Float avgScore_total = Convert.toFloat(avgScore_total1);
                                    if (Float.compare(min_total.floatValue(), avgScore_total.floatValue()) == 0) {
                                        stepList.get(d2).put("teaIsMin_totalScore", "true");
                                    } else {
                                        stepList.get(d2).put("teaIsMin_totalScore", "false");
                                    }
                                }
                            }
                        }
                    }
                }
                List<Map<String, Object>> lists2 = new ArrayList<>();
                for (String key : duibiduixiangMap.keySet()) {
                    List<Map<String, Object>> maps = duibiduixiangMap.get(key);
                    lists2.addAll(maps);
                }
                List<Map<String, Object>> jiaoxuedanweiList2 = (List) lists2.stream().filter(top2 -> {
                    return "0".equals(Convert.toStr(top2.get("duibidueixian_qufen")));
                }).collect(Collectors.toList());
                jiaoxuedanweiList2.sort((m1, m2) -> {
                    return Convert.toInt(m1.get("step_per")).compareTo(Convert.toInt(m2.get("step_per")));
                });
                Map<String, List<Map<String, Object>>> duibiduixiangMap2 = (Map) ((List) lists2.stream().filter(top3 -> {
                    return "1".equals(Convert.toStr(top3.get("duibidueixian_qufen")));
                }).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(m3 -> {
                    return Convert.toStr(m3.get("step_per"));
                }));
                dataList.add(lists.get(0));
                dataList.add(lists.get(1));
                dataList.add(jiaoxuedanweiList2);
                dataList.add(duibiduixiangMap2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getExamScoreView(String teachUnit_s, String examNum, String gradeNum, String teachUnit, String subjectNum, String classNum, String studentType, String type, String source, String history, String fufen, String subCompose) {
        String sql;
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ to_sjt_r_exam_score_veiw_fufen(?,?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ to_sjt_r_exam_score_veiw(?,?,?,?,?,?,?,?,?,?)}";
        }
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, ("null".equals(teachUnit_s) || teachUnit_s == null) ? "" : teachUnit_s);
                pstat.setString(2, ("null".equals(examNum) || examNum == null) ? "" : examNum);
                pstat.setString(3, ("null".equals(subjectNum) || subjectNum == null) ? "" : subjectNum);
                pstat.setString(4, ("null".equals(gradeNum) || gradeNum == null) ? "" : gradeNum);
                pstat.setString(5, ("null".equals(teachUnit) || teachUnit == null) ? "" : teachUnit);
                pstat.setString(6, ("null".equals(classNum) || classNum == null) ? "" : classNum);
                pstat.setString(7, ("null".equals(type) || type == null) ? "" : type);
                pstat.setString(8, ("null".equals(studentType) || studentType == null) ? "" : studentType);
                pstat.setString(9, ("null".equals(source) || source == null) ? "" : source);
                pstat.setString(10, ("null".equals(subCompose) || subCompose == null) ? "" : subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    rtnlist.add(rowArr);
                }
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getAllSubAvgView(String areaval, String examNum, String gradeNum, String schoolNum, String classNum, String studentType, String type, String source, String history, String fufen, String subCompose, String teachUnit, String teachUnit_s) {
        String sql;
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ to_gd_allclass_allSubject_average_veiw_fufen(?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ to_gd_allclass_allSubject_average_veiw(?,?,?,?,?,?,?,?)}";
        }
        ArrayList arrayList = new ArrayList();
        List myList = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, ("null".equals(examNum) || examNum == null) ? "" : examNum);
                pstat.setString(2, ("null".equals(gradeNum) || gradeNum == null) ? "" : gradeNum);
                pstat.setString(3, ("null".equals(teachUnit) || teachUnit == null) ? "" : teachUnit);
                pstat.setString(4, ("null".equals(teachUnit_s) || teachUnit_s == null) ? "" : teachUnit_s);
                pstat.setString(5, ("null".equals(type) || type == null) ? "" : type);
                pstat.setString(6, ("null".equals(studentType) || studentType == null) ? "" : studentType);
                pstat.setString(7, ("null".equals(source) || source == null) ? "" : source);
                pstat.setString(8, ("null".equals(subCompose) || subCompose == null) ? "" : subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                String claNum = "";
                while (rs.next()) {
                    String curClaNum = rs.getString(3);
                    Object[] rowArr = new Object[count];
                    if (null != rs.getString(1) && rs.getString(1).equals("8")) {
                        Subject sjt = new Subject();
                        sjt.setId(rs.getInt(2));
                        sjt.setSubjectNum(rs.getInt(6));
                        sjt.setSubjectName(rs.getString(7));
                        arrayList2.add(sjt);
                    }
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    if (!"".equals(claNum) && !claNum.equals(curClaNum)) {
                        arrayList.add(arrayList3);
                        arrayList3 = new ArrayList();
                    }
                    arrayList3.add(rowArr);
                    claNum = curClaNum;
                }
                arrayList.add(arrayList3);
                myList.add(arrayList2);
                myList.add(arrayList);
                DbUtils.close(rs, pstat, conn);
                return myList;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getMoreExamOnesjtScoreView(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String history, String fufen, String subCompose, String islevel) {
        String sql;
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ to_teacher_more_exam_sjt_score_veiw_fufen(?,?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ to_teacher_more_exam_sjt_score_veiw(?,?,?,?,?,?,?,?,?,?)}";
        }
        ArrayList arrayList = new ArrayList();
        Map<String, String> optMap = new HashMap<>();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, type);
                pstat.setString(7, studentType);
                pstat.setString(8, source);
                pstat.setString(9, subCompose);
                pstat.setString(10, islevel);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList2 = new ArrayList();
                String claNum = "";
                while (rs.next()) {
                    String curClaNum = rs.getString(4);
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    optMap.put(rs.getString(2) + "" + rs.getString(12), rs.getString(11));
                    if (!"".equals(claNum) && !claNum.equals(curClaNum)) {
                        arrayList.add(arrayList2);
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(rowArr);
                    claNum = curClaNum;
                }
                arrayList.add(arrayList2);
                DbUtils.close(rs, pstat, conn);
                ArrayList arrayList3 = new ArrayList();
                Object[] key = optMap.keySet().toArray();
                Arrays.sort(key);
                for (int i2 = 0; i2 < key.length; i2++) {
                    Object[] key2 = {key[i2], optMap.get(key[i2])};
                    arrayList3.add(key2);
                }
                List rt = new ArrayList();
                rt.add(arrayList3);
                rt.add(arrayList);
                return rt;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getStudentImproveFallAnaly(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String sNum, String c_exam, String history, String fufen, String subCompose, String islevel) {
        String sql;
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ toTeacher_student_improve_fall_analy_fufen(?,?,?,?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ toTeacher_student_improve_fall_analy(?,?,?,?,?,?,?,?,?,?,?,?)}";
        }
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, type);
                pstat.setString(7, studentType);
                pstat.setString(8, source);
                pstat.setString(9, sNum);
                pstat.setString(10, c_exam);
                pstat.setString(11, subCompose);
                pstat.setString(12, islevel);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    rtnlist.add(rowArr);
                }
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getTeachertopStudent(String examNum, String subjectNum, String gradeNum, String schoolNum, String classNum, String studentType, String type, String source, String sNum, String history, String fufen, String subCompose, String islevel) {
        String sql;
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ toTeacher_topStudent_fufen(?,?,?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ toTeacher_topStudent(?,?,?,?,?,?,?,?,?,?,?)}";
        }
        ArrayList arrayList = new ArrayList();
        Map<Integer, String> subjectMap = new TreeMap<>();
        ArrayList arrayList2 = new ArrayList();
        Connection conn = DbUtils.getConnection();
        ArrayList arrayList3 = new ArrayList();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, type);
                pstat.setString(7, studentType);
                pstat.setString(8, source);
                pstat.setString(9, sNum);
                pstat.setString(10, subCompose);
                pstat.setString(11, islevel);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList4 = new ArrayList();
                new ArrayList();
                String claNum = "";
                String stuStr = "";
                while (rs.next()) {
                    String curClaNum = rs.getString(5);
                    subjectMap.put(Integer.valueOf(rs.getInt(11)), rs.getString(4));
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    if (!"".equals(claNum) && !claNum.equals(curClaNum)) {
                        arrayList.add(arrayList4);
                        arrayList4 = new ArrayList();
                    }
                    arrayList4.add(rowArr);
                    Student stu = new Student();
                    if (stuStr.indexOf(rs.getString(5)) == -1) {
                        stu.setStudentId(rs.getString(5));
                        stuStr = stuStr + rs.getString(5) + Const.STRING_SEPERATOR;
                        arrayList3.add(stu);
                    }
                    claNum = curClaNum;
                }
                arrayList.add(arrayList4);
                DbUtils.close(rs, pstat, conn);
                List myList = new ArrayList();
                myList.add(arrayList);
                Set entries = subjectMap.entrySet();
                for (Map.Entry<Integer, String> entry : entries) {
                    Integer key = entry.getKey();
                    String value = entry.getValue();
                    Subject subject = new Subject();
                    subject.setId(key);
                    subject.setSubjectName(value);
                    arrayList2.add(subject);
                }
                myList.add(arrayList2);
                myList.add(arrayList3);
                return myList;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getTeacherScoreAnaly(String examNum, String gradeNum, String schoolNum, String subjectNum, String studentType, String type, String source, String c_exam, String history, String rank, String fufen, String subCompose, String islevel, String teachUnit, String teachUnit_s) {
        String sql;
        String statistic = "";
        if (teachUnit_s.equals("00")) {
            statistic = "_statistics";
        }
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ teacherScoreAnaly_new" + statistic + "_fufen(?,?,?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ teacherScoreAnaly_new" + statistic + "(?,?,?,?,?,?,?,?,?,?,?)}";
        }
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, teachUnit);
                pstat.setString(5, c_exam);
                pstat.setString(6, type);
                pstat.setString(7, source);
                pstat.setString(8, studentType);
                pstat.setString(9, subCompose);
                pstat.setString(10, islevel);
                pstat.setString(11, rank);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList = new ArrayList();
                String subId = "";
                while (rs.next()) {
                    String curSubId = rs.getString(1);
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    if (!"".equals(subId) && !subId.equals(curSubId)) {
                        rtnlist.add(arrayList);
                        arrayList = new ArrayList();
                    }
                    arrayList.add(rowArr);
                    subId = curSubId;
                }
                rtnlist.add(arrayList);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getTeacherScoreAnaly2(String examNum, String gradeNum, String teachUnit, String subjectNum, String studentType, String type, String source, String c_exam, String history, String rank, String fufen, String teachUnit_s) {
        String sql;
        String fufenStr = "";
        if ("1".equals(fufen)) {
            fufenStr = "_fufen";
        }
        if (!"01".equals(teachUnit_s)) {
            sql = "{call /* shard_host_HG=Read */ teacherScoreAnaly_statistics" + fufenStr + "(?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ teacherScoreAnaly" + fufenStr + "(?,?,?,?,?,?,?,?,?)}";
        }
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, teachUnit);
                pstat.setString(5, c_exam);
                pstat.setString(6, type);
                pstat.setString(7, source);
                pstat.setString(8, studentType);
                if (!"01".equals(teachUnit_s)) {
                    pstat.setString(9, teachUnit);
                } else {
                    pstat.setString(9, rank);
                }
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList = new ArrayList();
                String subId = "";
                while (rs.next()) {
                    String curSubId = rs.getString(1);
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    if (!"".equals(subId) && !subId.equals(curSubId)) {
                        rtnlist.add(arrayList);
                        arrayList = new ArrayList();
                    }
                    arrayList.add(rowArr);
                    subId = curSubId;
                }
                rtnlist.add(arrayList);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getOneSubAnaly(String teachUnit_s, String examNum, String gradeNum, String teachUnit, String subjectNum, String studentType, String type, String source, String history, String fufen, String subCompose) {
        String sql;
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ SubjectScoreAnaly_fufen(?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ SubjectScoreAnaly(?,?,?,?,?,?,?,?,?)}";
        }
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, teachUnit_s);
                pstat.setString(2, examNum);
                pstat.setString(3, subjectNum);
                pstat.setString(4, gradeNum);
                pstat.setString(5, teachUnit);
                pstat.setString(6, ("null".equals(type) || type == null) ? "" : type);
                pstat.setString(7, ("null".equals(studentType) || studentType == null) ? "" : studentType);
                pstat.setString(8, ("null".equals(source) || source == null) ? "" : source);
                pstat.setString(9, subCompose.equals("null") ? "" : subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    rtnlist.add(rowArr);
                }
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List<List<Gradelevel>> getAllSubAnaly(String teachUnit_s, String examNum, String gradeNum, String teachUnit, String subjectNum, String studentType, String type, String source, String sNum, String history, String fufen, String l2Allsubpassrait, String subCompose, String islevel, String classNum) {
        List<List<Gradelevel>> list = new ArrayList<>();
        String isShowReferenceRate = Configuration.getInstance().getIsShowReferenceRate();
        String l2 = Configuration.getInstance().getL2Allsubpassrait();
        Map<String, Double> teachUnitToJigelvMap = new HashMap<>();
        Double quankeJigelv_all = null;
        new ArrayList();
        DateUtil.timer();
        if ("1".equals(l2Allsubpassrait)) {
            List<Map<String, Object>> examSubList = this.g.getExamSubjectList(examNum, gradeNum);
            List<String> zonghekemuList = (List) examSubList.stream().filter(m -> {
                return "T".equals(m.get("isHidden"));
            }).map(m2 -> {
                return Convert.toStr(m2.get("pexamPaperNum"));
            }).distinct().collect(Collectors.toList());
            zonghekemuList.forEach(pexamPaperNum -> {
                examSubList.removeIf(m3 -> {
                    return pexamPaperNum.equals(Convert.toStr(m3.get("examPaperNum")));
                });
            });
            List<Map<String, Object>> allStuOfJigeList = new ArrayList<>();
            int i = 0;
            int iLen = examSubList.size();
            while (true) {
                if (i >= iLen) {
                    break;
                }
                Map<String, Object> oneSubMap = examSubList.get(i);
                String currentSubjectNum = Convert.toStr(oneSubMap.get(Const.EXPORTREPORT_subjectNum));
                BigDecimal totalScore = Convert.toBigDecimal(oneSubMap.get("totalScore"), BigDecimal.valueOf(0L));
                BigDecimal jigeScore = totalScore.multiply(BigDecimal.valueOf(0.6d));
                List<Map<String, Object>> jigeStuListOfOneSub = this.g.getJigeStuListOfOneSub(examNum, gradeNum, currentSubjectNum, teachUnit_s, teachUnit, type, studentType, source, subCompose, fufen, islevel, jigeScore);
                if (i == 0) {
                    allStuOfJigeList.addAll(jigeStuListOfOneSub);
                } else {
                    if (CollUtil.isEmpty(jigeStuListOfOneSub)) {
                        allStuOfJigeList = new ArrayList<>();
                        break;
                    }
                    List<Map<String, Object>> allStuOfJigeList_new = new ArrayList<>();
                    allStuOfJigeList.forEach(oneStuMap -> {
                        String studentId = Convert.toStr(oneStuMap.get(Const.EXPORTREPORT_studentId), "");
                        Optional res = jigeStuListOfOneSub.stream().filter(m3 -> {
                            return studentId.equals(Convert.toStr(m3.get(Const.EXPORTREPORT_studentId)));
                        }).findAny();
                        if (res.isPresent()) {
                            allStuOfJigeList_new.add(oneStuMap);
                        }
                    });
                    allStuOfJigeList = allStuOfJigeList_new;
                }
                i++;
            }
            if (CollUtil.isNotEmpty(allStuOfJigeList)) {
                Map<String, Long> teachUnitToJigecountMap = new HashMap<>();
                if ("00".equals(teachUnit_s)) {
                    teachUnitToJigecountMap = (Map) allStuOfJigeList.stream().collect(Collectors.groupingBy(m3 -> {
                        return Convert.toStr(m3.get("teachUnitId"));
                    }, Collectors.counting()));
                } else if ("01".equals(teachUnit_s)) {
                    teachUnitToJigecountMap = (Map) allStuOfJigeList.stream().collect(Collectors.groupingBy(m4 -> {
                        return Convert.toStr(m4.get(Const.EXPORTREPORT_classNum));
                    }, Collectors.counting()));
                }
                List<Map<String, Object>> cankaorenshuOfZongfenList = this.g.getCankaorenshuOfZongfenList(examNum, gradeNum, teachUnit_s, teachUnit, type, studentType, source, subCompose, fufen, islevel);
                AtomicReference<Long> quankeJigecount_all = new AtomicReference<>(0L);
                teachUnitToJigecountMap.forEach((teachUnitId, quankeJigecount) -> {
                    Optional<Map<String, Object>> res = cankaorenshuOfZongfenList.stream().filter(m5 -> {
                        return teachUnitId.equals(Convert.toStr(m5.get("teachUnitId")));
                    }).findAny();
                    if (res.isPresent()) {
                        Double quankejigelv = null;
                        double cankaorenshu = Convert.toDouble(res.get().get("numOfStudent"), Double.valueOf(0.0d)).doubleValue();
                        if (cankaorenshu > 0.0d) {
                            quankejigelv = Double.valueOf(quankeJigecount.longValue() / cankaorenshu);
                        }
                        teachUnitToJigelvMap.put(teachUnitId, quankejigelv);
                    }
                    quankeJigecount_all.updateAndGet(v -> {
                        return Long.valueOf(v.longValue() + quankeJigecount.longValue());
                    });
                });
                Double cankaorenshu_all2 = Double.valueOf(0.0d);
                for (int i2 = 0; i2 < cankaorenshuOfZongfenList.size(); i2++) {
                    Double can = Convert.toDouble(cankaorenshuOfZongfenList.get(i2).get("numOfStudent"));
                    cankaorenshu_all2 = Double.valueOf(cankaorenshu_all2.doubleValue() + can.doubleValue());
                }
                if (cankaorenshu_all2.doubleValue() > 0.0d) {
                    quankeJigelv_all = Double.valueOf(quankeJigecount_all.get().longValue() / cankaorenshu_all2.doubleValue());
                }
            }
        }
        List<Map<String, Object>> allSubList = this.g.getExamSubjectList(examNum, gradeNum, teachUnit_s, teachUnit, studentType, type, source, subCompose);
        Double finalQuankeJigelv_all = quankeJigelv_all;
        allSubList.forEach(oneSubObj -> {
            String currentSubjectNum2 = Convert.toStr(oneSubObj.get(Const.EXPORTREPORT_subjectNum));
            String subjectName = Convert.toStr(oneSubObj.get("subjectName"));
            try {
                DateUtil.timer();
                List<Gradelevel> oneSubDataList = this.g.getallIndex(teachUnit_s, examNum, currentSubjectNum2, gradeNum, teachUnit, classNum, type, studentType, source, sNum, fufen, "1", subCompose, islevel, "schOrcl", "asc");
                if (CollUtil.isNotEmpty(oneSubDataList)) {
                    List<Map<String, Object>> enCountOfOneTeachUnit = new ArrayList<>();
                    if ("1".equals(isShowReferenceRate)) {
                        enCountOfOneTeachUnit = this.g.getEnCountOfOneTeachUnit(examNum, gradeNum, currentSubjectNum2, teachUnit_s, teachUnit);
                    }
                    List<Map<String, Object>> finalEnCountOfOneTeachUnit = enCountOfOneTeachUnit;
                    AtomicInteger enCount_all = new AtomicInteger();
                    oneSubDataList.forEach(oneSubDataMap -> {
                        String teachUnitId2 = oneSubDataMap.getClassNum();
                        double cankaorenshu = Convert.toDouble(oneSubDataMap.getNumOfStudent(), Double.valueOf(0.0d)).doubleValue();
                        oneSubDataMap.setSubjectNum(currentSubjectNum2);
                        oneSubDataMap.setSubjectName(subjectName);
                        if ("1".equals(l2)) {
                            if (CollUtil.isEmpty(teachUnitToJigelvMap)) {
                                oneSubDataMap.setQuankejigelv(Double.valueOf(0.0d));
                            } else if (teachUnitToJigelvMap.containsKey(teachUnitId2)) {
                                Double quankeJigelv = (Double) teachUnitToJigelvMap.get(teachUnitId2);
                                oneSubDataMap.setQuankejigelv(quankeJigelv);
                            }
                        }
                        String key = "00".equals(teachUnit_s) ? Const.EXPORTREPORT_schoolNum : Const.EXPORTREPORT_classNum;
                        if ("1".equals(isShowReferenceRate) && CollUtil.isNotEmpty(finalEnCountOfOneTeachUnit)) {
                            Optional<Map<String, Object>> res = finalEnCountOfOneTeachUnit.stream().filter(m5 -> {
                                return teachUnitId2.equals(Convert.toStr(m5.get(key)));
                            }).findAny();
                            if (res.isPresent()) {
                                int enCount = Convert.toInt(res.get().get("enCount"), 0).intValue();
                                Double cankaolv = enCount > 0 ? Double.valueOf(cankaorenshu / enCount) : null;
                                oneSubDataMap.setCankaolv(cankaolv);
                                enCount_all.addAndGet(enCount);
                            }
                        }
                        if (("00".equals(teachUnit_s) && teachUnit.equals(oneSubDataMap.getClassNum())) || ("01".equals(teachUnit_s) && Const.class_grade.equals(oneSubDataMap.getClassNum()))) {
                            oneSubDataMap.setQuankejigelv(finalQuankeJigelv_all);
                            double cankaorenshu_all = Convert.toDouble(oneSubDataMap.getNumOfStudent(), Double.valueOf(0.0d)).doubleValue();
                            Double cankaolv_all = enCount_all.get() > 0 ? Double.valueOf(cankaorenshu_all / enCount_all.get()) : null;
                            oneSubDataMap.setCankaolv(cankaolv_all);
                        }
                    });
                    list.add(oneSubDataList);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getQtypeScoreDetail(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String history, String subCompose) {
        String isMoreSchool = this.analy.getExamIsMoreSchool(examNum);
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ cnine_questiontype_score_detail_view(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, type);
                pstat.setString(7, studentType);
                pstat.setString(8, source);
                pstat.setString(9, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                String stuid = "";
                boolean flg = true;
                while (rs.next()) {
                    String curStuid = rs.getString(15);
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    if ("".equals(stuid) || stuid.equals(curStuid)) {
                        if (flg) {
                            QuestionType questionType = new QuestionType();
                            questionType.setNum(rs.getString(8));
                            questionType.setName(rs.getString(13));
                            questionType.setExt1(rs.getString(12));
                            questionType.setExt2(rs.getString(9));
                            arrayList.add(questionType);
                        }
                        arrayList2.add(rowArr);
                    } else {
                        flg = false;
                        arrayList3.add(arrayList2);
                        arrayList2 = new ArrayList();
                        arrayList2.add(rowArr);
                    }
                    stuid = curStuid;
                }
                arrayList3.add(arrayList2);
                rtnlist.add(arrayList);
                Collections.sort(arrayList3, new 1(this));
                rtnlist.add(arrayList3);
                rtnlist.add(isMoreSchool);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getQtypeScoreDetail_excel(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String studentType, String type, String source, String history) {
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ cnine_questiontype_score_detail_view(?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, type);
                pstat.setString(7, studentType);
                pstat.setString(8, source);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                HashMap hashMap = new HashMap();
                String stuid = "";
                boolean flg = true;
                while (rs.next()) {
                    String curStuid = rs.getString(15);
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    if ("".equals(stuid) || stuid.equals(curStuid)) {
                        if (flg) {
                            QuestionType questionType = new QuestionType();
                            questionType.setNum(rs.getString(8));
                            questionType.setName(rs.getString(13));
                            questionType.setExt1(rs.getString(12));
                            questionType.setExt2(rs.getString(9));
                            arrayList.add(questionType);
                        }
                        arrayList2.add(rowArr);
                    } else {
                        flg = false;
                        hashMap.put(stuid, arrayList2);
                        arrayList2 = new ArrayList();
                        arrayList2.add(rowArr);
                    }
                    stuid = curStuid;
                }
                if (!"".equals(stuid)) {
                    hashMap.put(stuid, arrayList2);
                }
                rtnlist.add(arrayList);
                rtnlist.add(hashMap);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public String rptFileFolder(String examNum, String schoolNum, String gradeNum, String studentType, String subjectNum) {
        return this.rpd.rptFileFolder(examNum, schoolNum, gradeNum, studentType, subjectNum);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getReportGrade(String examNum, String subjectNum, String schoolNum, String studentType, User user) {
        List<AjaxData> list_grade;
        String usertype = "1";
        String userNum = "";
        ServletActionContext.getServletContext();
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
        }
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && usertype.equals("2")) {
                list_grade = this.analy.getGrade_Student(examNum, subjectNum, schoolNum, studentType, userNum);
            } else {
                list_grade = this.analy.getGrade(examNum, subjectNum, schoolNum, studentType, "");
            }
        } else if (usertype != null && !usertype.equals("") && usertype.equals("2")) {
            list_grade = this.analy.getHistoryGrade_Student(examNum, subjectNum, schoolNum, studentType, userNum);
        } else {
            list_grade = this.analy.getHistoryGrade(examNum, subjectNum, schoolNum, studentType, userNum);
        }
        return list_grade;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getReportClass(String examNum, String subjectNum, String schoolNum, String gradeNum, String studentType, User user, String subCompose, String islevel) {
        List<AjaxData> list_class;
        String usertype = "1";
        String userNum = "";
        ServletActionContext.getServletContext();
        String levelclass = system.getIsLevelClass(examNum, gradeNum, subjectNum, null);
        if (null == levelclass || levelclass.equals("")) {
            levelclass = "F";
        }
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
        }
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && usertype.equals("2")) {
                list_class = this.analy.getClass_Student(examNum, subjectNum, schoolNum, gradeNum, studentType, userNum, levelclass, islevel, subCompose);
            } else {
                list_class = this.analy.getClass(examNum, subjectNum, schoolNum, gradeNum, studentType, levelclass, islevel, subCompose);
            }
        } else if (usertype != null && !usertype.equals("") && usertype.equals("2")) {
            list_class = this.analy.getHistoryClass_Student(examNum, subjectNum, schoolNum, gradeNum, studentType, userNum);
        } else {
            list_class = this.analy.getHistoryClass(examNum, subjectNum, schoolNum, gradeNum, studentType, levelclass);
        }
        return list_class;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getReportClass1(String examNum, String subjectNum, String schoolNum, String gradeNum, String studentType, User user, String subCompose, String islevel) {
        List<AjaxData> list_class;
        String usertype = "1";
        String userNum = "";
        ServletActionContext.getServletContext();
        String levelclass = system.getIsLevelClass(examNum, gradeNum, subjectNum, null);
        if (null == levelclass || levelclass.equals("")) {
            levelclass = "F";
        }
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
        }
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && usertype.equals("2")) {
                list_class = this.analy.getClass_Student(examNum, subjectNum, schoolNum, gradeNum, studentType, userNum, levelclass, islevel, subCompose);
            } else {
                list_class = this.analy.getClass1(examNum, subjectNum, schoolNum, gradeNum, studentType, levelclass);
            }
        } else if (usertype != null && !usertype.equals("") && usertype.equals("2")) {
            list_class = this.analy.getHistoryClass_Student(examNum, subjectNum, schoolNum, gradeNum, studentType, userNum);
        } else {
            list_class = this.analy.getHistoryClass1(examNum, subjectNum, schoolNum, gradeNum, studentType, levelclass);
        }
        return list_class;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getReportSutdent(String examNum, String subjectNum, String schoolNum, String gradeNum, String studentType, User user, String cla, String subCompose, String islevel) {
        List<AjaxData> list;
        String usertype = "1";
        String userNum = "";
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
        }
        String levelclass = system.getIsLevelClass(examNum, gradeNum, subjectNum, null);
        if (null == levelclass || levelclass.equals("")) {
            levelclass = "F";
        }
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && usertype.equals("2")) {
                list = this.analy.getStudent_Student(examNum, subjectNum, schoolNum, gradeNum, cla, userNum);
            } else {
                list = this.analy.getStudent(examNum, subjectNum, schoolNum, gradeNum, cla, levelclass, islevel, subCompose);
            }
        } else if (usertype != null && !usertype.equals("") && usertype.equals("2")) {
            list = this.analy.getHistoryStudent_Student(examNum, subjectNum, schoolNum, gradeNum, cla, userNum);
        } else {
            list = this.analy.getHistoryStudent(examNum, subjectNum, schoolNum, gradeNum, cla);
        }
        return list;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getObjOptionItem(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String questionId, String studentType, String type, String source, String history, String originalOption, String subCompose) {
        List rtnlist = new ArrayList();
        new HashMap();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ toTeacher_class_Objective_option_analy_optionItem(?,?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, questionId);
                pstat.setString(7, type);
                pstat.setString(8, studentType);
                pstat.setString(9, source);
                pstat.setString(10, originalOption);
                pstat.setString(11, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                ArrayList arrayList = new ArrayList();
                while (rs.next()) {
                    int count = rs.getMetaData().getColumnCount();
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                        if (i == rowArr.length - 1) {
                            arrayList.add(rowArr);
                        }
                    }
                }
                rtnlist.add(arrayList);
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                while (pstat.getMoreResults()) {
                    String pCla = "";
                    rs = pstat.getResultSet();
                    while (rs.next()) {
                        String cCla = rs.getString(2);
                        int count2 = rs.getMetaData().getColumnCount();
                        String[] rowArr2 = new String[count2];
                        if (!pCla.equals("") && !pCla.equals(cCla)) {
                            arrayList3.add(arrayList2);
                            arrayList2 = new ArrayList();
                        }
                        for (int i2 = 0; i2 < rowArr2.length; i2++) {
                            rowArr2[i2] = rs.getString(i2 + 1);
                        }
                        arrayList2.add(rowArr2);
                        pCla = cCla;
                    }
                    arrayList3.add(arrayList2);
                    rtnlist.add(arrayList3);
                }
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public void deleteExportFiles(String dirPath, String[] names) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files.length == 0) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String fname = files[i].getName();
                for (String str : names) {
                    if (fname.contains(str)) {
                        deleteSingleFile_file(files[i].toString());
                    }
                }
            } else {
                String fname2 = files[i].getName();
                for (String str2 : names) {
                    if (fname2.contains(str2)) {
                        deleteDirect(files[i]);
                    }
                }
            }
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public void getNamesFromConff(String dirPath) {
        String typeVale = Configuration.getInstance().getDeleteNames();
        String[] names = typeVale.split(Const.STRING_SEPERATOR);
        deleteExportFiles(dirPath, names);
        for (String str : names) {
            File dir = new File(dirPath + str + "/");
            if (dir.mkdirs()) {
            }
        }
    }

    public void deleteDirect(File dird) {
        if (dird.isDirectory()) {
            File[] files = dird.listFiles();
            if (files.length == 0) {
                deleteEmptyDir(dird);
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    deleteSingleFile_file(files[i].toString());
                } else {
                    deleteDirect(files[i]);
                }
            }
            deleteEmptyDir(dird);
        }
    }

    public void deleteSingleFile_file(String srcName) {
        File file = new File(srcName);
        if ((System.currentTimeMillis() - file.lastModified()) / 60000 > 1440) {
            file.delete();
        }
    }

    public void deleteEmptyDir(File dir) {
        if (dir.isDirectory() && dir.listFiles().length == 0) {
            dir.delete();
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List authRole(String examPaperNum, String groupNum, String userId) {
        String sql = "SELECT userNum FROM userrole WHERE userNum={userId}  AND roleNum='-1'  UNION  SELECT userNum FROM questiongroup_user WHERE exampaperNum={examPaperNum}   ";
        Map args = new HashMap();
        args.put("userId", userId);
        args.put("examPaperNum", examPaperNum);
        if (null != groupNum && !"".equals(groupNum)) {
            sql = sql + "and groupNum={groupNum}   ";
            args.put("groupNum", groupNum);
        }
        String sql2 = sql + "AND userNum={userId}  AND userType!='0'";
        args.put("userId", userId);
        if (userId.equals("-1") || userId.equals("-2")) {
            sql2 = "SELECT '" + userId + "' userNum";
        }
        return this.dao._queryArrayList(sql2, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getTitle(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String quesId, String teachUnit, String teachUnit_s) {
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        String examPaperNum = exampaperObj.getExamPaperNum().toString();
        new ArrayList();
        String level_question = "classlevel_question";
        String tUnitStr = "";
        if (teachUnit_s.equals("01")) {
            level_question = "gradelevel_question";
            tUnitStr = " and lq.schoolNum={teachUnit} ";
        } else if (teachUnit_s.equals("02")) {
            level_question = "classlevel_question";
            tUnitStr = " and lq.classNum={teachUnit}  ";
        } else if (teachUnit_s.equals("00")) {
            level_question = "statistic_question";
            tUnitStr = " and lq.statisticId={teachUnit}  ";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        String stage = this.dao._queryStr("SELECT stage FROM basegrade WHERE gradeNum = {gradeNum} ", args);
        String sql = "SELECT d.fullScore,d.answer,d.questionType,lq.average,GROUP_CONCAT(DISTINCT k.knoName Separator '**'),GROUP_CONCAT(DISTINCT a.abilityName Separator '**') FROM (SELECT id,fullScore,answer,questionType FROM define WHERE id = {quesId}  UNION ALL SELECT id,fullScore,answer,questionType FROM subdefine WHERE id ={quesId}  ) d LEFT JOIN " + level_question + " lq ON d.id = lq.questionNum LEFT JOIN knowdetail kd ON d.id = kd.questionNum AND kd.examPaperNum = lq.examPaperNum LEFT JOIN abilitydetail ad ON d.id = ad.questionNum AND ad.examPaperNum = lq.examPaperNum LEFT JOIN knowledge k ON k.num = kd.konwNum AND k.subjectNum = lq.subjectNum AND k.stage = {stage}  LEFT JOIN ability a ON a.abilitynum = ad.abilityNum AND a.subjectNum = lq.subjectNum AND a.stage = {stage}  where lq.examPaperNum = {examPaperNum} " + tUnitStr + " AND lq.subjectNum = {subjectNum} ";
        Map args2 = StreamMap.create().put("quesId", (Object) quesId).put("stage", (Object) stage).put("examPaperNum", (Object) examPaperNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("teachUnit", (Object) teachUnit);
        return this.dao._queryArrayList(sql, args2);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public int getAllT3Stu(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String quesId, String minScore, String maxScore, String teachUnit, String teachUnit_s) {
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        String pexamPaperNum = exampaperObj.getPexamPaperNum().toString();
        String range = "";
        if (!"".equals(minScore) && !"".equals(maxScore)) {
            range = "WHERE s.questionScore BETWEEN {minScore} AND {maxScore}";
        } else if (!"".equals(minScore)) {
            range = "WHERE s.questionScore >={minScore}  ";
        } else if (!"".equals(maxScore)) {
            range = "WHERE s.questionScore <= {maxScore} ";
        }
        String tUnitStr = "";
        if (teachUnit_s.equals("01")) {
            tUnitStr = " and schoolNum={teachUnit}  ";
        } else if (teachUnit_s.equals("02")) {
            tUnitStr = " and classNum={teachUnit}  ";
        } else if (teachUnit_s.equals("00")) {
            tUnitStr = " and topitemid={teachUnit}  ";
        }
        String sql2 = "SELECT count(1) FROM (SELECT studentId,schoolNum,gradeNum,classNum,questionScore,'-' answer,examPaperNum FROM score WHERE examPaperNum = {pexamPaperNum} " + tUnitStr + " AND questionNum ={quesId}   AND continued = 'F' UNION ALL SELECT studentId,schoolNum,gradeNum,classNum,questionScore,answer,examPaperNum FROM objectivescore WHERE examPaperNum = {pexamPaperNum} " + tUnitStr + " AND questionNum ={quesId}  ) s inner JOIN illegal ill ON ill.examPaperNum = s.examPaperNum AND ill.studentId = s.studentId AND ill.type=2 " + range;
        if (teachUnit_s.equals("00")) {
            sql2 = "SELECT count(1) FROM (SELECT s.studentId,s.schoolNum,s.gradeNum,s.classNum,s.questionScore,'-' answer,s.examPaperNum FROM score s left join (select distinct sitemid from statisticitem where examNum={examNum}  " + tUnitStr + ") t on s.schoolNum = t.sitemid WHERE t.sitemid is not null  AND s.examPaperNum = {pexamPaperNum}  AND s.questionNum = {quesId}  AND s.continued = 'F' UNION ALL SELECT s.studentId,s.schoolNum,s.gradeNum,s.classNum,s.questionScore,s.answer,s.examPaperNum FROM objectivescore s left join (select distinct sitemid from statisticitem where examNum={examNum}  " + tUnitStr + ") t on s.schoolNum = t.sitemid WHERE t.sitemid is not null  AND s.examPaperNum = {pexamPaperNum}  AND s.questionNum = {quesId} ) s inner JOIN illegal ill ON ill.examPaperNum = s.examPaperNum AND ill.studentId = s.studentId AND ill.type=2 " + range;
        }
        Map args = StreamMap.create().put("minScore", (Object) minScore).put("maxScore", (Object) maxScore).put("teachUnit", (Object) teachUnit).put("pexamPaperNum", (Object) pexamPaperNum).put("quesId", (Object) quesId).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryInt(sql2, args).intValue();
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public int getAllT3StuFc(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String quesId, String minScore, String maxScore, String teachUnit, String teachUnit_s) {
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        String pexamPaperNum = exampaperObj.getPexamPaperNum().toString();
        String range = "";
        if (!"".equals(minScore) && !"".equals(maxScore)) {
            range = "WHERE s.questionScore BETWEEN {minScore}  AND {maxScore} ";
        } else if (!"".equals(minScore)) {
            range = "WHERE s.questionScore >= {minScore} ";
        } else if (!"".equals(maxScore)) {
            range = "WHERE s.questionScore <= {maxScore} ";
        }
        String tUnitStr = "";
        if (teachUnit_s.equals("01")) {
            tUnitStr = " and schoolNum={teachUnit}  ";
        } else if (teachUnit_s.equals("02")) {
            tUnitStr = " AND studentId IN (SELECT DISTINCT sid FROM levelstudent_record WHERE examNum = {examNum} AND subjectNum ={subjectNum}   AND gradeNum = {gradeNum} and classNum={teachUnit})";
        } else if (teachUnit_s.equals("00")) {
            tUnitStr = " and topitemid={teachUnit}  ";
        }
        String sql2 = "SELECT count(1) FROM (SELECT studentId,schoolNum,gradeNum,classNum,questionScore,'-' answer,examPaperNum FROM score WHERE examPaperNum = {pexamPaperNum} " + tUnitStr + " AND questionNum = {quesId}  AND continued = 'F' UNION ALL SELECT studentId,schoolNum,gradeNum,classNum,questionScore,answer,examPaperNum FROM objectivescore WHERE examPaperNum = {pexamPaperNum} " + tUnitStr + " AND questionNum = {quesId} ) s inner JOIN illegal ill ON ill.examPaperNum = s.examPaperNum AND ill.studentId = s.studentId AND ill.type=2 " + range;
        if (teachUnit_s.equals("00")) {
            sql2 = "SELECT count(1) FROM (SELECT s.studentId,s.schoolNum,s.gradeNum,s.classNum,s.questionScore,'-' answer,s.examPaperNum FROM score s left join (select distinct sitemid from statisticitem where examNum={examNum}  " + tUnitStr + ") t on s.schoolNum = t.sitemid WHERE t.sitemid is not null  and s.examPaperNum = {pexamPaperNum}  AND s.questionNum = {quesId}  AND s.continued = 'F' UNION ALL SELECT s.studentId,s.schoolNum,s.gradeNum,s.classNum,s.questionScore,s.answer,s.examPaperNum FROM objectivescore s left join (select distinct sitemid from statisticitem where examNum={examNum} " + tUnitStr + ") t on s.schoolNum = t.sitemid WHERE t.sitemid is not null  and s.examPaperNum = {pexamPaperNum}  AND s.questionNum = {quesId} ) s inner JOIN illegal ill ON ill.examPaperNum = s.examPaperNum AND ill.studentId = s.studentId AND ill.type=2 " + range;
        }
        Map args = StreamMap.create().put("minScore", (Object) minScore).put("maxScore", (Object) maxScore).put("teachUnit", (Object) teachUnit).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("pexamPaperNum", (Object) pexamPaperNum).put("quesId", (Object) quesId);
        return this.dao._queryInt(sql2, args).intValue();
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getT3StuList(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String quesId, String minScore, String maxScore, int pagestart, int pagesize, String teachUnit, String teachUnit_s) {
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        exampaperObj.getExamPaperNum().toString();
        String pexamPaperNum = exampaperObj.getPexamPaperNum().toString();
        new ArrayList();
        String range = "";
        if (!"".equals(minScore) && !"".equals(maxScore)) {
            range = "WHERE s.questionScore BETWEEN {minScore} AND {maxScore} ";
        } else if (!"".equals(minScore)) {
            range = "WHERE s.questionScore >= {minScore} ";
        } else if (!"".equals(maxScore)) {
            range = "WHERE s.questionScore <= {maxScore} ";
        }
        String tUnitStr = "";
        if (teachUnit_s.equals("01")) {
            tUnitStr = " and schoolNum={teachUnit}  ";
        } else if (teachUnit_s.equals("02")) {
            tUnitStr = " and classNum={teachUnit} ";
        } else if (teachUnit_s.equals("00")) {
            tUnitStr = " and topitemid={teachUnit}  ";
        }
        String sql = "SELECT sch.schoolName,g.gradeName,c.className,IFNULL(stu.studentName,''),IFNULL(stu.studentId,''),s.questionScore,s.answer FROM (SELECT studentId,schoolNum,gradeNum,classNum,questionScore,'-' answer,examPaperNum FROM score WHERE examPaperNum = {pexamPaperNum} " + tUnitStr + " AND questionNum ={quesId}   AND continued = 'F' UNION ALL SELECT studentId,schoolNum,gradeNum,classNum,questionScore,answer,examPaperNum FROM objectivescore WHERE examPaperNum = {pexamPaperNum} " + tUnitStr + " AND questionNum = {quesId} ) s inner JOIN illegal ill ON ill.examPaperNum = s.examPaperNum AND ill.studentId = s.studentId AND ill.type=2 LEFT JOIN student stu ON stu.id = s.studentId LEFT JOIN school sch ON sch.id = s.schoolNum LEFT JOIN grade g ON g.gradeNum = s.gradeNum AND g.schoolNum = s.schoolNum AND g.jie = stu.jie LEFT JOIN class c ON c.id = s.classNum " + range + " ORDER BY s.questionScore LIMIT {pagestart}  , {pagesize} ";
        if (teachUnit_s.equals("00")) {
            sql = "SELECT sch.schoolName,g.gradeName,c.className,IFNULL(stu.studentName,''),IFNULL(stu.studentId,''),s.questionScore,s.answer FROM (SELECT s.studentId,s.schoolNum,s.gradeNum,s.classNum,s.questionScore,'-' answer,s.examPaperNum FROM score s left join (select distinct sitemid from statisticitem where examNum={examNum} " + tUnitStr + ") t on s.schoolNum = t.sitemid WHERE t.sitemid is not null and s.examPaperNum = {pexamPaperNum}  AND s.questionNum = {quesId}  AND s.continued = 'F' UNION ALL SELECT s.studentId,s.schoolNum,s.gradeNum,s.classNum,s.questionScore,s.answer,s.examPaperNum FROM objectivescore s left join (select distinct sitemid from statisticitem where examNum={examNum}" + tUnitStr + ") t on s.schoolNum = t.sitemid WHERE t.sitemid is not null and s.examPaperNum = {pexamPaperNum}  AND s.questionNum = {quesId} ) s inner JOIN illegal ill ON ill.examPaperNum = s.examPaperNum AND ill.studentId = s.studentId AND ill.type=2 LEFT JOIN student stu ON stu.id = s.studentId LEFT JOIN school sch ON sch.id = s.schoolNum LEFT JOIN grade g ON g.gradeNum = s.gradeNum AND g.schoolNum = s.schoolNum AND g.jie = stu.jie LEFT JOIN class c ON c.id = s.classNum " + range + " ORDER BY s.questionScore LIMIT {pagestart}  , {pagesize} ";
        }
        Map args = StreamMap.create().put("minScore", (Object) minScore).put("maxScore", (Object) maxScore).put("teachUnit", (Object) teachUnit).put("pexamPaperNum", (Object) pexamPaperNum).put("quesId", (Object) quesId).put("pagestart", (Object) Integer.valueOf(pagestart)).put("pagesize", (Object) Integer.valueOf(pagesize)).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryArrayList(sql, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getT3StuListFc(String examNum, String gradeNum, String schoolNum, String subjectNum, String classNum, String quesId, String minScore, String maxScore, int pagestart, int pagesize, String teachUnit, String teachUnit_s) {
        Exampaper exampaperObj = this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
        exampaperObj.getExamPaperNum().toString();
        String pexamPaperNum = exampaperObj.getPexamPaperNum().toString();
        new ArrayList();
        String range = "";
        if (!"".equals(minScore) && !"".equals(maxScore)) {
            range = " AND s.questionScore BETWEEN {minScore} AND {maxScore} ";
        } else if (!"".equals(minScore)) {
            range = " AND s.questionScore >= {minScore} ";
        } else if (!"".equals(maxScore)) {
            range = " AND s.questionScore <={maxScore} ";
        }
        String tUnitStr = "";
        String tUnitStr1 = "";
        String lsSql1 = "";
        String lsSql2 = "";
        if (teachUnit_s.equals("01")) {
            tUnitStr = " and schoolNum={teachUnit}  ";
            lsSql1 = "LEFT JOIN levelstudent ls ON ls.sid = s.studentId ";
            lsSql2 = " AND c.id = ls.classNum";
        } else if (teachUnit_s.equals("02")) {
            tUnitStr = " AND studentId IN (SELECT DISTINCT sid FROM levelstudent_record WHERE examNum = {examNum}  AND subjectNum = {subjectNum}  AND gradeNum = {gradeNum} and classNum={teachUnit}  ) ";
            tUnitStr1 = " and c.id={teachUnit}  ";
        } else if (teachUnit_s.equals("00")) {
            tUnitStr = " and topitemid={teachUnit}  ";
            lsSql1 = "LEFT JOIN levelstudent ls ON ls.sid = s.studentId ";
            lsSql2 = " AND c.id = ls.classNum";
        }
        String sql = "SELECT sch.schoolName,g.gradeName,c.className,IFNULL(stu.studentName,''),IFNULL(stu.studentId,''),s.questionScore,s.answer FROM (SELECT studentId,schoolNum,gradeNum,classNum,questionScore,'-' answer,examPaperNum FROM score WHERE examPaperNum = {pexamPaperNum} " + tUnitStr + " AND questionNum = {quesId}  AND continued = 'F' UNION ALL SELECT studentId,schoolNum,gradeNum,classNum,questionScore,answer,examPaperNum FROM objectivescore WHERE examPaperNum = {pexamPaperNum} " + tUnitStr + " AND questionNum ={quesId}  ) s inner JOIN illegal ill ON ill.examPaperNum = s.examPaperNum AND ill.studentId = s.studentId AND ill.type=2 LEFT JOIN student stu ON stu.id = s.studentId LEFT JOIN school sch ON sch.id = s.schoolNum LEFT JOIN grade g ON g.gradeNum = s.gradeNum AND g.schoolNum = s.schoolNum AND g.jie = stu.jie " + lsSql1 + "LEFT JOIN levelclass c ON 1=1" + lsSql2 + " where c.subjectNum = {subjectNum} " + tUnitStr1 + range + " ORDER BY s.questionScore LIMIT {pagestart},{pagesize}  ";
        if (teachUnit_s.equals("00")) {
            sql = "SELECT sch.schoolName,g.gradeName,c.className,IFNULL(stu.studentName,''),IFNULL(stu.studentId,''),s.questionScore,s.answer FROM (SELECT s.studentId,s.schoolNum,s.gradeNum,s.classNum,s.questionScore,'-' answer,s.examPaperNum FROM score s left join (select distinct sitemid from statisticitem where examNum={examNum} " + tUnitStr + ") t on s.schoolNum = t.sitemid WHERE t.sitemid is not null and s.examPaperNum ={pexamPaperNum}   AND s.questionNum ={quesId} AND s.continued = 'F' UNION ALL SELECT s.studentId,s.schoolNum,s.gradeNum,s.classNum,s.questionScore,s.answer,s.examPaperNum FROM objectivescore s left join (select distinct sitemid from statisticitem where examNum={examNum}  " + tUnitStr + ") t on s.schoolNum = t.sitemid WHERE t.sitemid is not null and s.examPaperNum = {pexamPaperNum}  AND s.questionNum ={quesId} ) s inner JOIN illegal ill ON ill.examPaperNum = s.examPaperNum AND ill.studentId = s.studentId AND ill.type=2 LEFT JOIN student stu ON stu.id = s.studentId LEFT JOIN school sch ON sch.id = s.schoolNum LEFT JOIN grade g ON g.gradeNum = s.gradeNum AND g.schoolNum = s.schoolNum AND g.jie = stu.jie " + lsSql1 + "LEFT JOIN levelclass c ON 1=1" + lsSql2 + " where c.subjectNum = {subjectNum} " + tUnitStr1 + range + " ORDER BY s.questionScore LIMIT {pagestart}  , {pagesize} ";
        }
        Map args = StreamMap.create().put("minScore", (Object) minScore).put("maxScore", (Object) maxScore).put("teachUnit", (Object) teachUnit).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("pexamPaperNum", (Object) pexamPaperNum).put("quesId", (Object) quesId).put("pagestart", (Object) Integer.valueOf(pagestart)).put("pagesize", (Object) Integer.valueOf(pagesize));
        return this.dao._queryArrayList(sql, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List<RptHeader> getOptionStudentList(String examNum, String subjectNum, String gradeNum, String studentType, String type, String source, String classNum, String schoolNum, String qNum, String option, String result, String subCompose) {
        String optionStr;
        String sql;
        if (option.equals("kx")) {
            optionStr = " and o.answer = '' ";
        } else {
            optionStr = " and o.answer like {option} ";
        }
        String cstr = "";
        String scstr = "";
        if (!schoolNum.equals("allschool")) {
            scstr = " and o.schoolNum={schoolNum} ";
        }
        if (result.equals("T")) {
            if (!classNum.equals(Const.class_grade)) {
                cstr = " and lc.id={classNum} ";
            }
            sql = "select h.shortname schoolName,g.gradeName,lc.className,s.studentName from levelstudent ls  LEFT JOIN student s ON s.id = ls.sid  left join objectivescore o on o.studentId=s.id  LEFT JOIN levelclass lc ON lc.id = ls.classNum  left join basegrade g on g.gradeNum=o.gradeNum  left join school h on h.id=o.schoolNum  where o.questionNum={qNum}  " + optionStr + " and o.gradeNum={gradeNum} " + scstr + cstr + " and lc.subjectNum = {subjectNum}   ORDER BY o.studentId ";
        } else {
            if (!classNum.equals(Const.class_grade)) {
                cstr = " and o.classNum={classNum} ";
            }
            sql = "select h.shortname schoolName,g.gradeName,c.className,s.studentName from student s  inner join objectivescore o on o.studentId=s.id  inner join studentlevel r on r.examPaperNum=o.examPaperNum  and r.studentId=o.studentId  left join class c on c.id=o.classNum  left join basegrade g on g.gradeNum=o.gradeNum  left join school h on h.id=o.schoolNum  where o.questionNum={qNum}  " + optionStr + " and o.gradeNum={gradeNum} " + scstr + cstr + "and r.statisticType={type}  and r.source={source}  and r.xuankezuhe={subCompose} ORDER BY o.studentId";
        }
        Map args = StreamMap.create().put("option", (Object) ("%" + option + "%")).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("qNum", (Object) qNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("type", (Object) type).put("source", (Object) source).put("subCompose", (Object) subCompose);
        return this.dao._queryBeanList(sql, RptHeader.class, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List<Map<String, Object>> getStudent(String examNum, String subjectNum, String gradeNum, String schoolNum, String leiceng, String examinee) {
        Map<String, String> args_isZhukeSub = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        String subjectNum2 = this.dao._queryStr("select subjectNum from exampaper where exampaperNum=( select pexamPaperNum from exampaper where examNum={examNum} and subjectNum={subjectNum} and gradeNum={gradeNum})", args_isZhukeSub);
        String schNum = "";
        String sjtNum = "";
        String statisticitem = "";
        if (!"-1".equals(schoolNum)) {
            schNum = " and st.schoolNum={schoolNum}";
        } else if (!leiceng.equals("")) {
            schNum = schNum + " and ss.sItemId is not null";
        }
        if (subjectNum2 != null && !"".equals(subjectNum2) && !"null".equals(subjectNum2)) {
            sjtNum = " and en.subjectNum={subjectNum} ";
        }
        if ("-1".equals(schoolNum) && !leiceng.equals("")) {
            statisticitem = " left join (select DISTINCT topItemId,sItemId from statisticitem_school where statisticItem='01' and topItemId={leiceng} ) ss on st.schoolNum = ss.sItemId";
        }
        String sql = "0".equals(examinee) ? "SELECT cast(@rd := @rd+1 as char)  as id,g.* from(SELECT cast(@rd := @rd+1 as char)  as id,g.* from( select  DISTINCT s.schoolName,g.gradeName,c.className,b.examineeNum,a.studentId,a.studentName from ( SELECT @rd:=0,st.studentId,st.studentNum,st.studentName,st.classNum,st.schoolNum,st.gradeNum,st.jie,st.id id from student st  " + statisticitem + "where st.gradeNum={gradeNum} " + schNum + "   and st.isDelete='F' ) a left join( select examNum,gradeNum,schoolNum,classNum,studentId,examineeNum  from examinationnum en where  en.examNum={examNum} and en.gradeNum={gradeNum} " + sjtNum + ")b on a.gradeNum=b.gradeNum and a.schoolNum=b.schoolNum and a.classNum=b.classNum and a.id=b.studentId left join grade g on a.gradeNum=g.gradeNum and a.schoolNum=g.schoolNum and a.jie=g.jie and g.isDelete='F' left join school s  on a.schoolNum=s.id and s.isDelete='F' left join class c on a.classNum=c.id and c.isDelete='F' group by a.studentId order by a.schoolNum,a.classNum*1)g,(SELECT @rd:=0)h" : "SELECT cast(@rd := @rd+1 as char)  as id,g.* from(SELECT DISTINCT sc.schoolName,bg.gradeName,cl.className,en.examineeNum,st.studentId,st.studentName from student st " + statisticitem + " INNER JOIN examinationnum en on st.id=en.studentId and st.schoolNum=en.schoolNum and st.gradeNum=en.gradeNum and st.classNum=en.classNum  INNER JOIN school sc  ON sc.id = st.schoolNum  INNER JOIN basegrade bg on st.gradeNum=bg.gradeNum  INNER JOIN class cl  ON cl.id = st.classNum  where st.gradeNum={gradeNum} " + schNum + " and st.isDelete='F' and cl.isDelete='F' and bg.isDelete='F' and sc.isDelete='F'  and en.examNum={examNum} " + sjtNum + " group  by studentId ORDER BY en.testingCentreId * 1,sc.id*1,bg.gradeNum*1,cl.classNum*1,en.examinationRoomNum*1,en.examineeNum*1 ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum2).put("leiceng", (Object) leiceng).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryMapList(sql + ")g,(SELECT @rd:=0)h", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List<Define> getDefine(String examNum, String subjectNum, String gradeNum, String schoolNum) {
        String sql;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        Exampaper exampaper = (Exampaper) this.dao._queryBean("SELECT examPaperNum,isHidden from exampaper where examNum={examNum}  and subjectNum={subjectNum}  and gradeNum={gradeNum} ", Exampaper.class, args);
        if (exampaper.getIsHidden().equals("T")) {
            sql = "SELECT id,questionNum,orderNum,questiontype qtype,fullScore,choosename FROM define   WHERE category={examPaperNum}  and choosename!='T' AND isParent='0'  UNION    SELECT subdef.id,subdef.questionNum,subdef.orderNum,subdef.questiontype qtype,subdef.fullScore,def.choosename    FROM define def   INNER JOIN subdefine subdef ON def.id=subdef.pid    WHERE def.category={examPaperNum}  and subdef.choosename!='T' ORDER BY questionNum *1, REPLACE ( SUBSTRING( questionNum, LOCATE('_', questionNum) + 1, LENGTH(questionNum) ), '_', '' ) ASC";
        } else {
            sql = "SELECT id,questionNum,orderNum,questiontype qtype,fullScore,choosename FROM define   WHERE examPaperNum={examPaperNum}  and choosename!='T' AND isParent='0'  UNION    SELECT subdef.id,subdef.questionNum,subdef.orderNum,subdef.questiontype qtype,subdef.fullScore,def.choosename    FROM define def   INNER JOIN subdefine subdef ON def.id=subdef.pid    WHERE def.examPaperNum={examPaperNum}  and subdef.choosename!='T' ORDER BY questionNum *1, REPLACE ( SUBSTRING( questionNum, LOCATE('_', questionNum) + 1, LENGTH(questionNum) ), '_', '' ) ASC";
        }
        Map args2 = StreamMap.create().put("examPaperNum", (Object) exampaper.getExamPaperNum());
        return this.dao._queryBeanList(sql, Define.class, args2);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public Map<String, String> getchooseDefine(String examNum, String subjectNum, String gradeNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        Exampaper exampaper = (Exampaper) this.dao._queryBean("SELECT examPaperNum from exampaper where examNum={examNum} and subjectNum={subjectNum}  and gradeNum={gradeNum} ", Exampaper.class, args);
        Map args2 = StreamMap.create().put("category", (Object) exampaper.getExamPaperNum());
        return this.dao._querySimpleMap("SELECT b.choosename,GROUP_CONCAT(b.questionNum) questionNum from( SELECT id,questionNum,orderNum,questiontype qtype,fullScore,choosename   FROM define   WHERE category={category} and choosename!='s' and choosename!='T' AND isParent='0'   UNION     SELECT subdef.id,subdef.questionNum,subdef.orderNum,subdef.questiontype qtype,subdef.fullScore,def.choosename   FROM define def    INNER JOIN subdefine subdef ON def.id=subdef.pid     WHERE def.category={category} and subdef.choosename!='s' and subdef.choosename!='T' ORDER BY questionNum *1,  REPLACE ( SUBSTRING( questionNum, LOCATE('_', questionNum) + 1, LENGTH(questionNum) ), '_', '' ) ASC )b GROUP BY b.choosename", args2);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public Examsetting getExamsettingData(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return (Examsetting) this.dao._queryBean("SELECT missingExam,discipline,lingfe,zonfen FROM examsetting where examNum = {examNum} ", Examsetting.class, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public String isYufabu(String examNum, String gradeNum, String subjectNum) {
        if (StrUtil.isEmpty(subjectNum)) {
            return null;
        }
        String subStr = subjectNum.length() < 3 ? "" : " and e2.subjectNum={subjectNum}";
        String sql1 = "select max(e1.appealDealDate) appealDealDate,e1.jisuanType from exampaper e1 left join exampaper e2 on e2.pexamPaperNum=e1.examPaperNum where e2.examNum={examNum}  and e2.gradeNum={gradeNum} " + subStr;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        Map<String, Object> epMap = this.dao._querySimpleMap(sql1, args);
        if (CollUtil.isEmpty(epMap)) {
            return null;
        }
        String jisuanType = Convert.toStr(epMap.get("jisuanType"));
        if ("0".equals(jisuanType)) {
            return "2";
        }
        Object appealDealDate = epMap.get("appealDealDate");
        if (null == appealDealDate) {
            return null;
        }
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        Object insertDate = this.dao._queryObject("SELECT insertDate FROM arealevel where examNum={examNum}  and gradeNum={gradeNum} and subjectNum={subjectNum}  LIMIT 1", args2);
        if (null == insertDate) {
            return null;
        }
        Date appealDealDateTime = DateUtil.parse(appealDealDate.toString() + ":00:00", "yyyy-MM-dd HH:mm:ss");
        if (DateUtil.parse(insertDate.toString(), "yyyy-MM-dd HH:mm:ss").before(appealDealDateTime)) {
            return "1";
        }
        return null;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getTeacherIntegral(String examNum, String gradeNum, String schoolNum, String subjectNum, String studentType, String type, String source, String c_exam, String history, String rank, String fufen, String subCompose, String islevel, String teachUnit, String teachUnit_s, String ordertype, String orderval) {
        String sql;
        String statistic = "";
        if (teachUnit_s.equals("00")) {
            statistic = "_q";
        }
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ teacherIntegral" + statistic + "_fufen(?,?,?,?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ teacherIntegral" + statistic + "(?,?,?,?,?,?,?,?,?,?,?,?)}";
        }
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, teachUnit);
                pstat.setString(5, type);
                pstat.setString(6, source);
                pstat.setString(7, studentType);
                pstat.setString(8, subCompose);
                pstat.setString(9, islevel);
                pstat.setString(10, rank);
                pstat.setString(11, ordertype);
                pstat.setString(12, orderval);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                ArrayList arrayList = new ArrayList();
                while (rs.next()) {
                    Object[] rowArr = new Object[count];
                    for (int i = 0; i < rowArr.length; i++) {
                        rowArr[i] = rs.getObject(i + 1);
                    }
                    arrayList.add(rowArr);
                }
                rtnlist.add(arrayList);
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getTeacherIndexIntegralData(String examNum, String stage) {
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ teacherIndexIntegral(?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, stage);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                int count = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    IndexIntegral il = new IndexIntegral();
                    il.setsItemName(String.valueOf(rs.getObject(1)));
                    il.setCompositescore(String.valueOf(rs.getObject(11)));
                    il.setCompositerank(String.valueOf(rs.getObject(12)));
                    il.setIndexone(String.valueOf(rs.getObject(3)));
                    il.setIndexonefinish(String.valueOf(rs.getObject(4)));
                    il.setIndexonescore(String.valueOf(rs.getObject(5)));
                    il.setIndextwo(String.valueOf(rs.getObject(6)));
                    il.setIndextwofinish(String.valueOf(rs.getObject(7)));
                    il.setIndextwoscore(String.valueOf(rs.getObject(8)));
                    il.setDevelopscore(String.valueOf(rs.getObject(9)));
                    il.setAlljifenscore(String.valueOf(rs.getObject(10)));
                    List<IndexIntegral> list = new ArrayList<>();
                    int num = (count - 12) / 16;
                    int start = 12;
                    for (int j = 0; j < num; j++) {
                        IndexIntegral a = new IndexIntegral();
                        a.setGradeNum(String.valueOf(rs.getObject(start + 2)));
                        a.setNumOfstudent(String.valueOf(rs.getObject(start + 3)));
                        a.setNumOfStudent2(String.valueOf(rs.getObject(start + 4)));
                        a.setBaoKaoStudent(String.valueOf(rs.getObject(start + 5)));
                        a.setReferrate(String.valueOf(rs.getObject(start + 6)));
                        a.setTotalscore(String.valueOf(rs.getObject(start + 7)));
                        a.setAverage(String.valueOf(rs.getObject(start + 8)));
                        a.setPass(String.valueOf(rs.getObject(start + 9)));
                        a.setPassrate(String.valueOf(rs.getObject(start + 10)));
                        a.setJifen(String.valueOf(rs.getObject(start + 12)));
                        a.setJifenrank(String.valueOf(rs.getObject(start + 13)));
                        a.setOldrank(String.valueOf(rs.getObject(start + 14)));
                        a.setJifenscore(String.valueOf(rs.getObject(start + 16)));
                        start += 16;
                        list.add(a);
                    }
                    il.setList(list);
                    rtnlist.add(il);
                }
                DbUtils.close(rs, pstat, conn);
                return rtnlist;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getTeacherIndexIntegral(String examNum, String stage) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("stage", (Object) stage);
        String count = this.dao._queryStr("select ifnull(count(1),0) from indexintegral where examNum={examNum}  and stage={stage}", args);
        if ("0".equals(count)) {
            Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
            String gradelevelcount = this.dao._queryStr("select ifnull(count(1),0) from gradelevel where examNum={examNum}  ", args2);
            if ("0".equals(gradelevelcount)) {
                return new ArrayList();
            }
            Map args3 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
            String realityStage = this.dao._queryStr("SELECT DISTINCT g.stage from gradelevel gl INNER JOIN basegrade g on gl.gradeNum=g.gradeNum where gl.examNum={examNum} ", args3);
            if (!stage.equals(realityStage)) {
                return new ArrayList();
            }
            Map args4 = StreamMap.create().put("stage", (Object) stage);
            String eeeeee = this.dao._queryStr("SELECT DISTINCT i.examNum from indexintegral i INNER JOIN exam e on i.examNum=e.examNum where i.stage={stage}  ORDER BY e.examDate desc LIMIT 1 ", args4);
            String sql = "INSERT INTO `indexintegral`  (`stage`, `examNum`, `sItemId`, `indexone`, `indextwo`,`numOfStudent`,`baoKaoStudent`,  `gradeone`, `gradeonepercentage`, `gradeonerank`,  `gradetwo`, `gradetwopercentage`, `gradetworank`,  `gradethree`, `gradethreepercentage`, `gradethreerank`) SELECT r.stage," + examNum + ",r.sItemId,r.indexone,r.indextwo,r.numOfStudent,r.baoKaoStudent, r.gradeone,r.gradeonepercentage,r.gradeonerank, r.gradetwo,r.gradetwopercentage,gradetworank,  r.gradethree,r.gradethreepercentage,r.gradethreerank from(  SELECT if(a.sonStatisticName=-1,'考核指标',a.sonStatisticName) sItemName,ifnull(bg1.gradeName,'无') gradeonename,ifnull(bg2.gradeName,'无') gradetwoname,ifnull(bg3.gradeName,'无') gradethreename,a.* from(  (SELECT ii.*,'-1' sonStatisticName from indexintegral ii where ii.examNum={eeeeee}  and ii.stage={stage}  and ii.sItemId='-1')   UNION   (SELECT ii.*,ifnull(s.schoolName,sl.sonStatisticName) from indexintegral ii LEFT JOIN school s on ii.sItemId=s.id   left JOIN statisticrelation sl on ii.sItemId=sl.sonStatisticId   where ii.examNum={eeeeee}  and ii.stage={stage}  and ii.sItemId<>'-1')   )a  LEFT JOIN basegrade bg1 on a.gradeone=bg1.gradeNum   LEFT JOIN basegrade bg2 on a.gradetwo=bg2.gradeNum   LEFT JOIN basegrade bg3 on a.gradethree=bg3.gradeNum )r ";
            Map args5 = StreamMap.create().put("eeeeee", (Object) eeeeee).put("stage", (Object) stage);
            this.dao._execute(sql, args5);
        }
        Map args7 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("stage", (Object) stage);
        return this.dao._queryBeanList("SELECT if(a.sonStatisticName=-1,'考核指标',a.sonStatisticName) sItemName,ifnull(bg1.gradeName,'无') gradeonename,ifnull(bg2.gradeName,'无') gradetwoname,ifnull(bg3.gradeName,'无') gradethreename,a.* from( (SELECT ii.*,'-1' sonStatisticName from indexintegral ii where ii.examNum={examNum}and ii.stage={stage} and ii.sItemId='-1') UNION (SELECT ii.*,ifnull(s.schoolName,sl.sonStatisticName) from indexintegral ii LEFT JOIN school s on ii.sItemId=s.id left JOIN statisticrelation sl on ii.sItemId=sl.sonStatisticId where ii.examNum={examNum} and ii.stage={stage}  and ii.sItemId<>'-1') )a LEFT JOIN basegrade bg1 on a.gradeone=bg1.gradeNum LEFT JOIN basegrade bg2 on a.gradetwo=bg2.gradeNum LEFT JOIN basegrade bg3 on a.gradethree=bg3.gradeNum", IndexIntegral.class, args7);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public Integer updateIndexIntegral(String examNum, String stage, String sitemid, String field, String value) {
        String sql;
        Map args = new HashMap();
        args.put("value", value);
        args.put("stage", stage);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        if ("all".equals(sitemid)) {
            sql = "update indexIntegral set " + field + "={value}  where stage={stage} and examNum={examNum} ";
        } else {
            sql = "update indexIntegral set " + field + "={value}  where stage={stage} and examNum={examNum}  and sItemId={sitemid}";
            args.put("sitemid", sitemid);
        }
        return Integer.valueOf(this.dao._execute(sql, args));
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public List getTeacherIndexGrade(String examNum, String stage) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("stage", (Object) stage);
        return this.dao._queryBeanList("SELECT DISTINCT gradeone gradeNum,bg.gradeName from(SELECT sItemId,gradeone,gradeonerank from indexintegral i1 where examNum={examNum}  and stage={stage}  and sItemId<>'-1' and gradeone<>-1 UNION SELECT sItemId,gradetwo,gradetworank from indexintegral i1 where examNum={examNum} and stage={stage}  and sItemId<>'-1' and gradetwo<>-1 UNION SELECT sItemId,gradethree,gradethreerank from indexintegral i1 where examNum={examNum}  and stage={stage}  and sItemId<>'-1' and gradethree<>-1 )t LEFT JOIN basegrade bg on t.gradeone=bg.gradeNum INNER JOIN(  SELECT DISTINCT gradeNum from gradelevel where examNum={examNum}and subjectNum=-1 )g on t.gradeone=g.gradeNum;", Grade.class, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public IndexIntegral getTeacherIndex(String examNum, String stage) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("stage", (Object) stage);
        return (IndexIntegral) this.dao._queryBean("SELECT indexone,indextwo from indexintegral where examNum={examNum}  and stage={stage} and sItemId='-1' ", IndexIntegral.class, args);
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public Map<String, Object> getSankeyData(String examNum, String gradeNum, String subjectNum, String teachUnit_s, String teachUnit, String c_exam, String islevel, String subCompose, String studentType, String type, String source, String fufen) {
        Map<String, Object> dataMap = new HashMap<>();
        List baseDataList = getStuAllExamDengjiData(examNum, gradeNum, subjectNum, teachUnit_s, teachUnit, c_exam, islevel, subCompose, studentType, type, source, fufen);
        List<Map<String, String>> stuAllExamDengjiList = (List) baseDataList.get(0);
        List<Map<String, Object>> allDengjiList = getAllDengji(gradeNum, subjectNum);
        Map<String, Object> weifenzuDengjiMap = new HashMap<>();
        weifenzuDengjiMap.put("dengji", "未分组");
        weifenzuDengjiMap.put("ordernum", 100);
        allDengjiList.add(weifenzuDengjiMap);
        if (null == stuAllExamDengjiList || stuAllExamDengjiList.size() <= 0 || null == allDengjiList || allDengjiList.size() <= 0) {
            return null;
        }
        List<Map<String, String>> exams = (List) baseDataList.get(1);
        Map<String, List<Map<String, String>>> oneExamOneDengjiAllStuDataMap = (Map) stuAllExamDengjiList.stream().collect(Collectors.groupingBy(m -> {
            return ((String) m.get("examName")) + "_" + ((String) m.get("dengji"));
        }));
        dataMap.put("oneExamOneDengjiMap", oneExamOneDengjiAllStuDataMap);
        dataMap.put("exams", exams);
        dataMap.put("dengjis", allDengjiList);
        dataMap.put("stuAllExamDengjiList", stuAllExamDengjiList);
        return dataMap;
    }

    @Override // com.dmj.service.reportManagement.ReportService
    public Map<String, Object> getappealDate(String examNum, String gradeNum, String subejctNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subejctNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._querySimpleMap("SELECT appealDate from exampaper WHERE examNum ={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum} ", args);
    }

    public List getStuAllExamDengjiData(String examNum, String gradeNum, String subjectNum, String teachUnit_s, String teachUnit, String c_exam, String islevel, String subCompose, String studentType, String type, String source, String fufen) {
        String sql;
        if ("1".equals(fufen)) {
            sql = "{call /* shard_host_HG=Read */ to_sangjitu_fufen(?,?,?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call /* shard_host_HG=Read */ to_sangjitu(?,?,?,?,?,?,?,?,?,?,?)}";
        }
        List rtnList = new ArrayList();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall(sql);
                pstat.setString(1, examNum);
                pstat.setString(2, gradeNum);
                pstat.setString(3, subjectNum);
                pstat.setString(4, teachUnit_s);
                pstat.setString(5, teachUnit);
                pstat.setString(6, c_exam);
                pstat.setString(7, islevel);
                pstat.setString(8, subCompose);
                pstat.setString(9, studentType);
                pstat.setString(10, type);
                pstat.setString(11, source);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                rs.getMetaData().getColumnCount();
                String[] mapKeys = {"schoolName", "className", Const.EXPORTREPORT_studentId, "studentName", Const.EXPORTREPORT_examNum, "examName", "examDate", "dengji"};
                String[] examMapKeys = {Const.EXPORTREPORT_examNum, "examName", "examDate", "type", "orderNum"};
                while (rs.next()) {
                    HashMap hashMap = new HashMap();
                    for (int i = 0; i < mapKeys.length; i++) {
                        String value = rs.getString(i + 1);
                        if ("dengji".equals(mapKeys[i]) && null == value) {
                            value = "未分组";
                        }
                        hashMap.put(mapKeys[i], value);
                    }
                    arrayList.add(hashMap);
                }
                rtnList.add(arrayList);
                while (pstat.getMoreResults()) {
                    rs = pstat.getResultSet();
                    while (rs.next()) {
                        HashMap hashMap2 = new HashMap();
                        for (int i2 = 0; i2 < examMapKeys.length; i2++) {
                            hashMap2.put(examMapKeys[i2], rs.getString(i2 + 1));
                        }
                        arrayList2.add(hashMap2);
                    }
                }
                rtnList.add(arrayList2);
                DbUtils.close(rs, pstat, conn);
                return rtnList;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    public List<Map<String, Object>> getAllDengji(String gradeNum, String subjectNum) {
        StringBuffer sql = new StringBuffer();
        sql.append("select dengji,ordernum from jisuanzhonglei ");
        sql.append("where gradeNum={gradeNum}  and subjectNum={subjectNum}  and jisuanzhonglei='2' ");
        sql.append("group by dengji ");
        sql.append("order by ordernum");
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao._queryMapList(sql.toString(), TypeEnum.StringObject, args);
    }
}
