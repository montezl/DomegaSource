package com.dmj.serviceimpl.examManagement;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.awardPoint.AwardPointDaoImpl;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.examManagement.ExamDAOImpl;
import com.dmj.daoimpl.examManagement.ExamManageDAOImpl;
import com.dmj.daoimpl.systemManagement.SystemDAOImpl;
import com.dmj.domain.Abilitydetail;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Averagescore;
import com.dmj.domain.Class;
import com.dmj.domain.Classexam;
import com.dmj.domain.CorrectInfo;
import com.dmj.domain.Define;
import com.dmj.domain.Exam;
import com.dmj.domain.Examinationnum;
import com.dmj.domain.Examinationnumimg;
import com.dmj.domain.ExamineeStuRecord;
import com.dmj.domain.Examlog;
import com.dmj.domain.Exampaper;
import com.dmj.domain.GeneralCorrectData;
import com.dmj.domain.Knowdetail;
import com.dmj.domain.ManageFile;
import com.dmj.domain.MarkError;
import com.dmj.domain.QuestionType;
import com.dmj.domain.Questiontypedetail;
import com.dmj.domain.RegExaminee;
import com.dmj.domain.Reg_Th_Log;
import com.dmj.domain.Remark;
import com.dmj.domain.Score;
import com.dmj.domain.Student;
import com.dmj.domain.Studentlevel;
import com.dmj.domain.Studentpaperimage;
import com.dmj.domain.Subject;
import com.dmj.domain.Task;
import com.dmj.domain.vo.QuestionGroup_question;
import com.dmj.domain.vo.Questiongroup_mark_setting;
import com.dmj.service.analysisManagement.AnalysisService;
import com.dmj.service.examManagement.ExamManageService;
import com.dmj.service.examManagement.ExamService;
import com.dmj.service.examManagement.ExportService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.service.test.StestService;
import com.dmj.serviceimpl.analysisManagement.AnalysisServiceImpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.serviceimpl.test.StestServiceimpl;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.ItemThresholdRegUtil;
import com.dmj.util.Util;
import com.zht.db.RowArg;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/* loaded from: ExportServiceImpl.class */
public class ExportServiceImpl implements ExportService {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    private ExamService examService = (ExamService) ServiceFactory.getObject(new ExamServiceImpl());
    private ExamManageService examManageService = (ExamManageService) ServiceFactory.getObject(new ExamManageServiceimpl());
    private SystemService system = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    private AnalysisService analy = (AnalysisService) ServiceFactory.getObject(new AnalysisServiceImpl());
    Logger log = Logger.getLogger(getClass());
    private ExamDAOImpl examDao = new ExamDAOImpl();
    ExamManageDAOImpl examManageDAO = new ExamManageDAOImpl();
    private SystemDAOImpl systemDAO = new SystemDAOImpl();
    private StestService stestService = (StestService) ServiceFactory.getObject(new StestServiceimpl());
    AwardPointDaoImpl awardPointDao = new AwardPointDaoImpl();

    @Override // com.dmj.service.examManagement.ExportService
    public List<AjaxData> getExam(Map<String, String> map, String userId) {
        String sql;
        String resourceType = map.get("resourceType");
        if ("-2".equals(userId) || "-1".equals(userId)) {
            sql = " SELECT DISTINCT e.examNum num,em.examName name from managefile e INNER JOIN exam em on e.examNum=em.examNum where e.resourceType = {resourceType} and em.isDelete='F' ORDER BY em.examdate DESC,e.examNum DESC ";
        } else {
            sql = "SELECT DISTINCT e.examNum num,em.examName name from managefile e INNER JOIN exam em on e.examNum=em.examNum  LEFT JOIN astrict a on e.examNum=a.examNum where e.resourceType = {resourceType} and a.partType=5  and a.userType=1 and a.`status`=1 and em.isDelete='F'  ORDER BY em.examdate DESC, e.examNum DESC";
        }
        Map args = new HashMap();
        args.put("resourceType", resourceType);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExportService
    public List<AjaxData> getGrade(Map<String, String> map, String userId) {
        String sql;
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String resourceType = map.get("resourceType");
        if ("-2".equals(userId) || "-1".equals(userId)) {
            sql = "SELECT DISTINCT g.gradeNum num,g.gradeName name from managefile e  LEFT JOIN basegrade g ON g.gradeNum = e.gradeNum   where e.examNum = {examNum} and e.resourceType =  {resourceType}  ORDER BY e.examNum DESC ";
        } else {
            sql = "SELECT DISTINCT g.gradeNum num,g.gradeName name from managefile e  LEFT JOIN basegrade g ON g.gradeNum = e.gradeNum   LEFT JOIN astrict a on e.examNum=a.examNum where e.examNum = {examNum}  and e.resourceType = {resourceType} and a.partType=5  and a.userType=1 and a.`status`=1  ORDER BY e.examNum DESC ";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("resourceType", resourceType);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExportService
    public List<AjaxData> getGrade_F(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String resourceType = map.get("resourceType");
        Map args = new HashMap();
        args.put("userId", userId);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("resourceType", resourceType);
        return this.dao2._queryBeanList("SELECT DISTINCT g.gradeNum num,g.gradeName name from managefile e  inner join   (  \tSELECT DISTINCT gradeNum FROM userposition WHERE userNum={userId}  \tUNION ALL   \tSELECT DISTINCT gradeNum FROM userposition_record WHERE userNum= {userId} AND examNum={examNum}  ) u on u.gradeNum = e.gradeNum  LEFT JOIN basegrade g ON g.gradeNum = e.gradeNum  LEFT JOIN astrict a on e.examNum=a.examNum where e.examNum = {examNum} and e.resourceType = {resourceType} and a.partType=5  and a.userType=1 and a.`status`=1  and g.gradeNum is not null", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExportService
    public List<AjaxData> getSubject(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String fufen = map.get("fufen");
        String resourceType = map.get("resourceType");
        String xuankezuhe = map.get("xuankezuhe");
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("subject_totalScore", "-1");
        args.put("resourceType", resourceType);
        args.put("fufen", fufen);
        List<AjaxData> list = new ArrayList<>();
        String tableName = "0".equals(fufen) ? "arealevel" : "arealevel_fufen";
        String sql = "SELECT g.subjectNum num ,sjt.subjectName name   FROM  (SELECT DISTINCT subjectNum FROM " + tableName + " WHERE examNum={examNum} AND gradeNum={gradeNum} AND studentType={studentType}  AND statisticType='0'  and xuankezuhe={xuankezuhe}  and issub != '0') g   LEFT JOIN `subject` sjt ON g.subjectNum=sjt.subjectNum  where g.subjectNum!={subject_totalScore}  order by sjt.orderNum ";
        List<?> _queryBeanList = this.dao2._queryBeanList(sql, AjaxData.class, args);
        List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT distinct e.subjectNum num ,sjt.subjectName name from managefile e  LEFT JOIN `subject` sjt ON e.subjectNum=sjt.subjectNum  WHERE examNum={examNum} AND gradeNum={gradeNum} and e.resourceType =  {resourceType} and source={fufen}  order by sjt.orderNum ", AjaxData.class, args);
        for (int i = 0; i < _queryBeanList.size(); i++) {
            AjaxData ajData = (AjaxData) _queryBeanList.get(i);
            for (int j = 0; j < _queryBeanList2.size(); j++) {
                AjaxData ajData2 = (AjaxData) _queryBeanList2.get(j);
                if (ajData.getNum().equals(ajData2.getNum())) {
                    list.add(ajData);
                }
            }
        }
        return list;
    }

    @Override // com.dmj.service.examManagement.ExportService
    public List<AjaxData> getSubject_F(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String fufen = map.get("fufen");
        String resourceType = map.get("resourceType");
        String xuankezuhe = map.get("xuankezuhe");
        List<AjaxData> list = new ArrayList<>();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("subject_totalScore", "-1");
        args.put("userId", userId);
        args.put("resourceType", resourceType);
        args.put("fufen", fufen);
        String tableName = "0".equals(fufen) ? "arealevel" : "arealevel_fufen";
        String sql = "SELECT g.subjectNum num ,sjt.subjectName name   FROM  (SELECT DISTINCT subjectNum FROM " + tableName + " WHERE examNum={examNum}  AND gradeNum={gradeNum} AND studentType={studentType}  AND statisticType='0'  and xuankezuhe={xuankezuhe}  and issub != '0') g   LEFT JOIN `subject` sjt ON g.subjectNum=sjt.subjectNum  where g.subjectNum!={subject_totalScore} order by sjt.orderNum ";
        List<?> _queryBeanList = this.dao2._queryBeanList(sql, AjaxData.class, args);
        List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT distinct e.subjectNum num ,sjt.subjectName name from managefile e  inner join   (  SELECT DISTINCT subjectNum FROM userposition WHERE  userNum={userId} AND subjectNum!='999'  UNION ALL   SELECT DISTINCT subjectNum FROM userposition_record WHERE examNum={examNum} AND userNum={userId} AND subjectNum!='999'  )u on u.subjectNum = e.subjectNum  LEFT JOIN `subject` sjt ON e.subjectNum=sjt.subjectNum  WHERE examNum={examNum} AND gradeNum={gradeNum} and e.resourceType =  {resourceType} and source={fufen}  order by sjt.orderNum", AjaxData.class, args);
        for (int i = 0; i < _queryBeanList.size(); i++) {
            AjaxData ajData = (AjaxData) _queryBeanList.get(i);
            for (int j = 0; j < _queryBeanList2.size(); j++) {
                AjaxData ajData2 = (AjaxData) _queryBeanList2.get(j);
                if (ajData.getNum().equals(ajData2.getNum())) {
                    list.add(ajData);
                }
            }
        }
        return list;
    }

    @Override // com.dmj.service.examManagement.ExportService
    public List<AjaxData> getFufen(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String resourceType = map.get("resourceType");
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("resourceType", resourceType);
        List list = this.dao2._queryBeanList("SELECT DISTINCT source num from managefile e where e.examNum={examNum} and e.gradeNum={gradeNum} and e.resourceType =  {resourceType} ORDER BY source desc", AjaxData.class, args);
        for (int i = 0; i < list.size(); i++) {
            AjaxData ajaxData = (AjaxData) list.get(i);
            if ("0".equals(ajaxData.getNum())) {
                ajaxData.setName("原始分");
            } else if ("1".equals(ajaxData.getNum())) {
                ajaxData.setName("赋分");
            }
        }
        return list;
    }

    @Override // com.dmj.service.examManagement.ExportService
    public List<AjaxData> getStudentType(Map<String, String> map) {
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        return this.dao2._queryBeanList("SELECT DISTINCT al.studentType num,d.name from arealevel al  INNER JOIN (SELECT value,name from data where type=25)d on d.value = al.studentType  where al.examNum={exam} and al.gradeNum={grade} ", AjaxData.class, args);
    }

    public List<AjaxData> getStudentTypeBySubject(Map<String, String> map) {
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        String subjectStr = "";
        if (!"-8".equals(subject)) {
            if ("-1".equals(subject)) {
                subject = map.get("sjt");
            }
            subjectStr = " and al.subjectNum={subject} ";
        }
        String sql = "SELECT DISTINCT al.studentType num,d.name from arealevel al  INNER JOIN (SELECT value,name from data where type=25)d on d.value = al.studentType  where al.examNum={exam} and al.gradeNum={grade} " + subjectStr;
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("exam", exam);
        args.put("grade", grade);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExportService
    public List<AjaxData> getXuanKeZuHe(Map<String, String> map) {
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        return this.dao2._queryBeanList("SELECT DISTINCT convert(xuankezuhe,char) num,s.subjectCombineName name from arealevel al LEFT JOIN subjectcombine s on al.xuankezuhe=s.subjectCombineNum  where al.examNum={exam} and al.gradeNum={grade} and al.studentType = {studentType} and s.isParent=1 or s.isParent is null", AjaxData.class, args);
    }

    public List<AjaxData> getXuanKeZuHeBySubject(Map<String, String> map) {
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        String subjectStr = "";
        if (!"-8".equals(subject)) {
            if ("-1".equals(subject)) {
                subject = map.get("sjt");
            }
            subjectStr = " and al.subjectNum={subject}  ";
        }
        String sql = "SELECT DISTINCT convert(al.xuankezuhe,char) num,s.subjectCombineName name from (select xuankezuhe from arealevel al  where al.examNum={exam} and al.gradeNum={grade} " + subjectStr + " and al.studentType = {studentType} )al LEFT JOIN subjectcombine s on al.xuankezuhe=s.subjectCombineNum  where s.isParent=1 or s.isParent is null";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExportService
    public List<AjaxData> getSchool(Map<String, String> map) {
        String sql;
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String userId = map.get("userId");
        String subjectStr = "";
        if (!"".equals(subject)) {
            if ("-1".equals(subject)) {
                subject = map.get("sjt");
            }
            subjectStr = " and subjectNum={subject} ";
        }
        String xuankezuhe = map.get("xuankezuhe");
        new ArrayList();
        boolean flag = false;
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("userId", userId);
        if ("-2".equals(userId) || "-1".equals(userId)) {
            sql = "SELECT DISTINCT convert(gl.schoolNum,char) num,sl.schoolName name from gradelevel gl INNER JOIN school sl on gl.schoolNum=sl.id where examNum={exam}  and gradeNum={grade} " + subjectStr + " and studentType={studentType} and xuankezuhe={xuankezuhe}  ";
            flag = true;
        } else {
            sql = "SELECT DISTINCT convert(gl.schoolNum,char) num,sl.schoolName name from gradelevel gl INNER JOIN school sl on gl.schoolNum=sl.id  INNER JOIN( \tSELECT schoolNum from schauthormanage where userId={userId} \tUNION \tSELECT schoolnum from `user` where id={userId} )u on gl.schoolNum=u.schoolNum where examNum={exam}  and gradeNum={grade} " + subjectStr + " and studentType={studentType} and xuankezuhe={xuankezuhe} ";
            String count = this.dao2._queryStr("SELECT count(1) from school sl  LEFT JOIN(  \tSELECT schoolNum from schauthormanage where userId={userId}  \tUNION  \tSELECT schoolnum from `user` where id={userId}  )u on sl.id=u.schoolNum  where u.schoolNum is null", args);
            if ("0".equals(count)) {
                flag = true;
            }
        }
        List _queryBeanList = this.dao2._queryBeanList(sql, AjaxData.class, args);
        if (_queryBeanList.size() > 0 && flag) {
            AjaxData aj = new AjaxData();
            aj.setNum("allschool");
            aj.setName("全区");
            _queryBeanList.add(aj);
        }
        return _queryBeanList;
    }

    public String getFileName(Map<String, String> map) {
        String str;
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        map.get("fufen");
        String resourceType = map.get("resourceType");
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String xuankezuhe = map.get("xuankezuhe");
        String str2 = "";
        if ("0".equals(resourceType)) {
            str2 = str2 + "单科目成绩_";
        } else if ("1".equals(resourceType)) {
            str2 = str2 + "总科目成绩_";
        } else if ("2".equals(resourceType)) {
            str2 = str2 + "小题分_";
        }
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("subject", subject);
        String str3 = (((str2 + this.dao2._queryStr("select examname from exam where examNum={exam} ", args) + "_") + this.dao2._queryStr("select gradename from basegrade where gradenum={grade} ", args) + "_") + this.dao2._queryStr("SELECT name from data where type=25 and value={studentType} ", args) + "_") + this.dao2._queryStr("SELECT subjectCombineName from subjectcombine where subjectcombinenum={xuankezuhe} ", args);
        if ("-8".equals(subject)) {
            str = str3 + "";
        } else {
            str = str3 + "_" + this.dao2._queryStr("SELECT subjectName from subject where subjectNum={subject} ", args);
        }
        return str;
    }

    public String getSchoolName(String schoolNum) {
        if ("allschool".equals(schoolNum)) {
            return "全区";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        String schoolname = this.dao2._queryStr("select schoolname from school where id={schoolNum} ", args);
        return schoolname;
    }

    public List sjtList(Map<String, String> map) {
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String fufen = map.get("fufen");
        String tableName = "0".equals(fufen) ? "arealevel" : "arealevel_fufen";
        String sql = "SELECT g.subjectNum num  FROM  ( \t\tSELECT DISTINCT subjectNum FROM " + tableName + " WHERE examNum={exam} AND gradeNum={grade}   AND statisticType='0'   and issub != '0'   \t) g   LEFT JOIN `subject` sjt ON g.subjectNum=sjt.subjectNum  where g.subjectNum!='-1'  \torder by sjt.orderNum";
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade);
        List list = this.dao2._queryColList(sql, args);
        return list;
    }

    public boolean getExportMoreSchType(String typeName, Map<String, String> map) {
        String rootPath = map.get("rootPath");
        String filePath = rootPath + "WEB-INF/classes/conff.properties";
        String typeVale = "";
        Properties easprop = new Properties();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            easprop.load(fis);
            typeVale = (String) easprop.get(typeName);
        } catch (Exception e) {
            this.log.info("", e);
        }
        if ("1".equals(typeVale)) {
            return true;
        }
        return false;
    }

    public boolean isExistInCondition(String[] arr, String schNum) {
        try {
            List<String> list = Arrays.asList(arr);
            List arrList = new ArrayList(list);
            arrList.add(schNum);
            Object[] newArr = arrList.toArray();
            Set set = new HashSet();
            for (Object obj : newArr) {
                set.add(obj);
            }
            if (set.toArray().length == arr.length) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.dmj.service.examManagement.ExportService
    public String download(Map<String, String> map) {
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        String fufen = map.get("fufen");
        String resourceType = map.get("resourceType");
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String xuankezuhe = map.get("xuankezuhe");
        String rootPath = map.get("rootPath");
        String filePath = fufen + File.separator + resourceType + File.separator + exam + File.separator + grade + File.separator + subject + File.separator + studentType + File.separator + xuankezuhe + File.separator;
        return downFile(rootPath, filePath, map);
    }

    @Override // com.dmj.service.examManagement.ExportService
    public String downloadAllsub(Map<String, String> map) {
        map.get(Const.EXPORTREPORT_examNum);
        map.get(Const.EXPORTREPORT_gradeNum);
        map.get(Const.EXPORTREPORT_subjectNum);
        map.get("fufen");
        map.get("resourceType");
        map.get(Const.EXPORTREPORT_studentType);
        map.get("xuankezuhe");
        String rootPath = map.get("rootPath");
        return downFileAllsub(rootPath, map);
    }

    public String downFile(String rootPath, String filePath, Map<String, String> map) {
        File dir;
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        String fufen = map.get("fufen");
        String resourceType = map.get("resourceType");
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("fufen", fufen);
        args.put("resourceType", resourceType);
        String status = this.dao2._queryStr("select ifnull(status,0) from managefile where examNum={exam} and gradeNum={grade} and subjectNum={subject} and source={fufen} and resourceType={resourceType} ", args);
        if ("1".equals(status)) {
            return "提示：导入科目总分成绩，没有小题分。";
        }
        String loginUser = map.get("loginUser");
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String[] schoolNumArr = schoolNum.split(Const.STRING_SEPERATOR);
        String oldRootPath = rootPath + "generateFile";
        String newRootPath = rootPath + "downloadFile";
        String oldPath = oldRootPath + File.separator + filePath;
        String newPath = newRootPath + File.separator + loginUser;
        createFolder(newPath);
        String newPath2 = newPath + File.separator + getFileName(map);
        deleteFileAndDirect(newPath2);
        createFolder(newPath2);
        try {
            dir = new File(oldPath);
        } catch (Exception e) {
        }
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files.length == 0) {
                return "文件尚未生成完毕，请稍后再试，或联系管理员确认。";
            }
            String fileSchoolNum = "";
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    String oldfileName = files[i].getName();
                    int lastIndex = oldfileName.lastIndexOf("_");
                    fileSchoolNum = fileSchoolNum + Const.STRING_SEPERATOR + oldfileName.substring(lastIndex + 1, oldfileName.lastIndexOf("."));
                }
            }
            String[] fileSchoolNumArr = fileSchoolNum.substring(1, fileSchoolNum.length()).split(Const.STRING_SEPERATOR);
            for (String str : schoolNumArr) {
                if (!isExistInCondition(fileSchoolNumArr, str)) {
                    return "文件尚未生成完毕，请稍后再试，或联系管理员确认。";
                }
            }
            for (int i2 = 0; i2 < files.length; i2++) {
                if (files[i2].isFile()) {
                    String oldfileName2 = files[i2].getName();
                    int lastIndex2 = oldfileName2.lastIndexOf("_");
                    int lastSecondIndex = oldfileName2.lastIndexOf(".");
                    StringBuffer sBuffer = new StringBuffer(oldfileName2);
                    String schNum = oldfileName2.substring(lastIndex2 + 1, lastSecondIndex);
                    if (isExistInCondition(schoolNumArr, schNum)) {
                        String newfileName = sBuffer.replace(0, lastSecondIndex, getSchoolName(schNum)).toString();
                        FileInputStream in = new FileInputStream(new File(oldPath + oldfileName2));
                        FileOutputStream out = new FileOutputStream(newPath2 + "\\" + newfileName);
                        byte[] buff = new byte[512];
                        while (true) {
                            int n = in.read(buff);
                            if (n == -1) {
                                break;
                            }
                            out.write(buff, 0, n);
                        }
                        out.flush();
                        in.close();
                        out.close();
                    }
                }
            }
            String url = getHttpPath(newRootPath + File.separator + loginUser + File.separator, getFileName(map), getFileName(map), map);
            return url;
        }
        return "文件尚未生成完毕，请稍后再试，或联系管理员确认。";
    }

    public String downFileAllsub(String rootPath, Map<String, String> map) {
        File dir;
        List<Map<String, Object>> messageData = new ArrayList<>();
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subjectArr = map.get(Const.EXPORTREPORT_subjectNum);
        String fufen = map.get("fufen");
        String resourceType = map.get("resourceType");
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String xuankezuhe = map.get("xuankezuhe");
        String loginUser = map.get("loginUser");
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String[] schoolNumArr = schoolNum.split(Const.STRING_SEPERATOR);
        String[] subjects = subjectArr.split(Const.STRING_SEPERATOR);
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("fufen", fufen);
        args.put("resourceType", resourceType);
        String oldRootPath = rootPath + "generateFile";
        String newRootPath = rootPath + "downloadFile";
        for (int i = 0; i < schoolNumArr.length; i++) {
            String school = schoolNumArr[i];
            map.replace(Const.EXPORTREPORT_subjectNum, "-8");
            String newPath = newRootPath + File.separator + loginUser + File.separator + (resourceType.equals("0") ? "全科目_" : "全科目_") + getFileName(map);
            if (i == 0) {
                deleteFileAndDirect(newPath);
            }
            createFolder(newPath);
            String newPath2 = newPath + File.separator + getSchoolName(school);
            deleteFileAndDirect(newPath2);
            createFolder(newPath2);
            for (String subject : subjects) {
                Map<String, Object> map1 = new HashMap<>();
                args.put("subject", subject);
                map.replace(Const.EXPORTREPORT_subjectNum, subject);
                String status = this.dao2._queryStr("select ifnull(status,0) from managefile where examNum={exam} and gradeNum={grade} and subjectNum={subject} and source={fufen} and resourceType={resourceType} ", args);
                if ("1".equals(status)) {
                    map1.put("schoolName", getSchoolName(school));
                    map1.put("subjectName", Convert.toStr(getSubjectName(subject).get(0).getSubjectName()));
                    map1.put("isdown", "导出失败(导入科目总分成绩，没有小题分)");
                    messageData.add(map1);
                } else {
                    String filePath = fufen + File.separator + resourceType + File.separator + exam + File.separator + grade + File.separator + subject + File.separator + studentType + File.separator + xuankezuhe + File.separator;
                    String oldPath = oldRootPath + File.separator + filePath;
                    try {
                        dir = new File(oldPath);
                    } catch (Exception e) {
                    }
                    if (dir.isDirectory()) {
                        File[] files = dir.listFiles();
                        if (files.length == 0) {
                            map1.put("schoolName", getSchoolName(school));
                            map1.put("subjectName", Convert.toStr(getSubjectName(subject).get(0).getSubjectName()));
                            map1.put("isdown", "导出失败(文件尚未生成完毕，请稍后再试，或联系管理员确认)");
                            messageData.add(map1);
                        } else {
                            map1.put("schoolName", getSchoolName(school));
                            map1.put("subjectName", Convert.toStr(getSubjectName(subject).get(0).getSubjectName()));
                            map1.put("isdown", "导出成功");
                            String fileSchoolNum = "";
                            for (int k = 0; k < files.length; k++) {
                                if (files[k].isFile()) {
                                    String oldfileName = files[k].getName();
                                    int lastIndex = oldfileName.lastIndexOf("_");
                                    String schNum = oldfileName.substring(lastIndex + 1, oldfileName.lastIndexOf("."));
                                    fileSchoolNum = fileSchoolNum + Const.STRING_SEPERATOR + schNum;
                                }
                            }
                            String[] fileSchoolNumArr = fileSchoolNum.substring(1, fileSchoolNum.length()).split(Const.STRING_SEPERATOR);
                            if (!isExistInCondition(fileSchoolNumArr, school)) {
                                map1.put("isdown", "导出失败(文件尚未生成完毕，请稍后再试，或联系管理员确认)");
                                messageData.add(map1);
                            } else {
                                for (int k2 = 0; k2 < files.length; k2++) {
                                    if (files[k2].isFile()) {
                                        String oldfileName2 = files[k2].getName();
                                        int lastIndex2 = oldfileName2.lastIndexOf("_");
                                        int lastSecondIndex = oldfileName2.lastIndexOf(".");
                                        StringBuffer sBuffer = new StringBuffer(oldfileName2);
                                        String schNum2 = oldfileName2.substring(lastIndex2 + 1, lastSecondIndex);
                                        if (schNum2.equals(school)) {
                                            String newfileName = sBuffer.replace(0, lastSecondIndex, Convert.toStr(getSubjectName(subject).get(0).getSubjectName())).toString();
                                            FileInputStream in = new FileInputStream(new File(oldPath + oldfileName2));
                                            FileOutputStream out = new FileOutputStream(newPath2 + "\\" + newfileName);
                                            byte[] buff = new byte[512];
                                            while (true) {
                                                int n = in.read(buff);
                                                if (n == -1) {
                                                    break;
                                                }
                                                out.write(buff, 0, n);
                                            }
                                            out.flush();
                                            in.close();
                                            out.close();
                                        }
                                    }
                                }
                                messageData.add(map1);
                            }
                        }
                    } else {
                        map1.put("schoolName", getSchoolName(school));
                        map1.put("subjectName", Convert.toStr(getSubjectName(subject).get(0).getSubjectName()));
                        map1.put("isdown", "导出失败(文件尚未生成完毕，请稍后再试，或联系管理员确认)");
                        messageData.add(map1);
                    }
                }
            }
        }
        map.replace(Const.EXPORTREPORT_subjectNum, "-8");
        String messagefilepath = newRootPath + File.separator + loginUser + File.separator + (resourceType.equals("0") ? "全科目_" : "全科目_") + getFileName(map) + File.separator + "文件生成结果.xls";
        ExcelWriter excelWriter = ExcelUtil.getWriter(messagefilepath);
        excelWriter.addHeaderAlias("schoolName", "学校");
        excelWriter.addHeaderAlias("subjectName", "科目");
        excelWriter.addHeaderAlias("isdown", "导出结果");
        excelWriter.setColumnWidth(0, 80);
        excelWriter.setColumnWidth(1, 40);
        excelWriter.setColumnWidth(2, 40);
        excelWriter.setOnlyAlias(true);
        excelWriter.write(messageData);
        excelWriter.close();
        String url = getHttpPath(newRootPath + File.separator + loginUser + File.separator, (resourceType.equals("0") ? "全科目_" : "全科目_") + getFileName(map), (resourceType.equals("0") ? "全科目_" : "全科目_") + getFileName(map), map);
        return url;
    }

    @Override // com.dmj.service.examManagement.ExportService
    public void deleteGenerateFile(Map<String, String> map) {
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String resourceType = map.get("resourceType");
        String rootPath = map.get("rootPath");
        map.put("userId", map.get("loginUser"));
        String dirFolder = rootPath + "generateFile" + File.separator + "0" + File.separator;
        String dirFolder1 = rootPath + "generateFile" + File.separator + "1" + File.separator;
        String dirFolder2 = dirFolder + resourceType + File.separator + exam + File.separator + grade;
        String dirFolder12 = dirFolder1 + resourceType + File.separator + exam + File.separator + grade;
        deleteFileAndDirect(dirFolder2);
        deleteFileAndDirect(dirFolder12);
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("resourceType", resourceType);
        this.dao2._execute("delete from managefile where examNum={exam} and gradeNum={grade} and resourceType={resourceType} ", args);
    }

    @Override // com.dmj.service.examManagement.ExportService
    public void deleteGenerateFilebysubject(Map<String, String> map) {
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        String resourceType = map.get("resourceType");
        String rootPath = map.get("rootPath");
        map.put("userId", map.get("loginUser"));
        String dirFolder = rootPath + "generateFile" + File.separator + "0" + File.separator;
        String dirFolder1 = rootPath + "generateFile" + File.separator + "1" + File.separator;
        String dirFolder2 = dirFolder + resourceType + File.separator + exam + File.separator + grade + File.separator + subject;
        String dirFolder12 = dirFolder1 + resourceType + File.separator + exam + File.separator + grade + File.separator + subject;
        deleteFileAndDirect(dirFolder2);
        deleteFileAndDirect(dirFolder12);
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("resourceType", resourceType);
        this.dao2._execute("delete from managefile where examNum={exam} and gradeNum={grade} and subjectNum={subject} and resourceType={resourceType} ", args);
    }

    @Override // com.dmj.service.examManagement.ExportService
    public void generateFile(Map<String, String> map) {
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String fufen = map.get("fufen");
        String resourceType = map.get("resourceType");
        String rootPath = map.get("rootPath");
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        map.put("userId", map.get("loginUser"));
        List sjtList = new ArrayList();
        if ("-1".equals(subject)) {
            sjtList = sjtList(map);
        } else {
            sjtList.add(subject);
        }
        String islevelClass = map.get("islevelClass");
        String dirFolder = rootPath + "generateFile" + File.separator + fufen + File.separator;
        createFolder(dirFolder);
        String dirFolder2 = dirFolder + resourceType + File.separator;
        createFolder(dirFolder2);
        String dirFolder3 = dirFolder2 + exam + File.separator;
        createFolder(dirFolder3);
        String dirFolder4 = dirFolder3 + grade + File.separator;
        createFolder(dirFolder4);
        for (int z = 0; z < sjtList.size(); z++) {
            String sjt = String.valueOf(sjtList.get(z));
            map.put("sjt", sjt);
            String dirFolder1 = dirFolder4 + sjt + File.separator;
            createFolder(dirFolder1);
            deleteFileAndDirect(dirFolder1);
            List<AjaxData> studentTypeList = getStudentTypeBySubject(map);
            map.put(Const.CORRECT_SCORECORRECT, "0");
            manageFileLog(map);
            for (int i = 0; i < studentTypeList.size(); i++) {
                String studentType = studentTypeList.get(i).getNum();
                String sTypeDirFolder = dirFolder1 + studentType + File.separator;
                createFolder(sTypeDirFolder);
                map.put(Const.EXPORTREPORT_studentType, studentType);
                List<AjaxData> xuanList = getXuanKeZuHeBySubject(map);
                for (int j = 0; j < xuanList.size(); j++) {
                    String xuankezuhe = xuanList.get(j).getNum();
                    String xuanDirFolder = sTypeDirFolder + xuankezuhe + File.separator;
                    createFolder(xuanDirFolder);
                    map.put("xuankezuhe", xuankezuhe);
                    if ("0".equals(resourceType)) {
                        writeSingleScoreSheet_xlsx(map, xuanDirFolder, "");
                    } else if ("1".equals(resourceType)) {
                        writeAllScoreSheet_xlsx(map, xuanDirFolder);
                    } else if ("2".equals(resourceType)) {
                        map.put("source", "0");
                        if ("T".equals(islevelClass)) {
                            writeSubjectDetailFcSheet(map, xuanDirFolder);
                        } else {
                            writeSubjectDetailSheet_xlsx(map, xuanDirFolder);
                        }
                    }
                }
            }
            map.put(Const.CORRECT_SCORECORRECT, "9");
            manageFileLog(map);
        }
    }

    public void writeSingleScoreSheet(Map<String, String> map, String schoolDirFolder, String schoolNum) {
        List schoolList;
        List<Studentlevel> singleSubjectList;
        String stuId;
        Label thrkm2;
        Number thrzg2;
        Number thrkg2;
        Number thrzf2;
        Label thrff2;
        Number thrgradepm2;
        Number thrpm2;
        Number thrff22;
        Label thrkm22;
        Number thrzg22;
        Number thrkg22;
        Number thrzf22;
        Label thrff23;
        Number thrgradepm22;
        Number thrpm22;
        Number thrff24;
        Label thrkm23;
        Number thrzg23;
        Number thrkg23;
        Number thrzf23;
        Label thrff25;
        Number thrgradepm23;
        Number thrpm23;
        Number thrff26;
        Label bianhao2;
        String loginUser = map.get("loginUser");
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        if ("-1".equals(subject)) {
            subject = map.get("sjt");
        }
        String fufen = map.get("fufen");
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String subCompose = map.get("xuankezuhe");
        String isJointStuType = map.get(Const.isJointStuType);
        String dispStuId = map.get("dispStuId");
        String threeSjt = map.get("threeSjt");
        String teacherCheckVal = map.get("teacherCheckVal");
        new ArrayList();
        new ArrayList();
        try {
            WritableFont illFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat illcFormat = new WritableCellFormat(illFont);
            illcFormat.setAlignment(Alignment.CENTRE);
            WritableFont illFont_all = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat illcFormat_all = new WritableCellFormat(illFont_all);
            illcFormat_all.setAlignment(Alignment.CENTRE);
            WritableFont fonttwo = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormattwo = new WritableCellFormat(fonttwo);
            cFormattwo.setAlignment(Alignment.CENTRE);
            List<AjaxData> subjectList = this.examManageService.getSjtByNum(subject);
            int teacherColumn = "T".equals(teacherCheckVal) ? 1 : 0;
            Set xuankezuheSCell7 = new HashSet();
            Set leibieSCell8 = new HashSet();
            if (null != subjectList && subjectList.size() > 0) {
                for (int c = 0; c < subjectList.size(); c++) {
                    if (null != schoolNum && !schoolNum.equals("-1") && !"allschool".equals(schoolNum)) {
                        schoolList = this.examManageService.getAllSchoolByExamNum(exam, schoolNum);
                    } else {
                        schoolList = this.analy.getSchool(exam, subjectList.get(c).getNum(), studentType, grade, fufen);
                    }
                    File f0 = new File(schoolDirFolder);
                    if (!f0.exists()) {
                        f0.createNewFile();
                    } else {
                        f0.delete();
                        f0.createNewFile();
                    }
                    WritableWorkbook wwBook2 = Workbook.createWorkbook(f0);
                    WritableSheet wSheet2 = wwBook2.createSheet(schoolList.get(0).getName(), 0);
                    if (null != schoolList && schoolList.size() > 0) {
                        int d = 0;
                        int preCount = 0;
                        int sheet2_rows = 0;
                        for (int a = 0; a < schoolList.size(); a++) {
                            List<Integer> removeSubList = new ArrayList<>();
                            String levelclass = this.system.getIsLevelClass(exam, grade, subject, null);
                            new ArrayList();
                            if (null != levelclass && levelclass.equals("T")) {
                                singleSubjectList = this.examManageService.getSingleSubjectData_levcla(exam, grade, subjectList.get(c).getNum(), "", schoolList.get(a).getNum(), "0", "0", isJointStuType, subCompose);
                            } else {
                                singleSubjectList = this.examManageService.getSingleSubjectData(exam, grade, subjectList.get(c).getNum(), "", schoolList.get(a).getNum(), "0", "0", "", "", false, isJointStuType, subCompose);
                            }
                            List l = new ArrayList();
                            new ArrayList();
                            HashMap hashMap = new HashMap();
                            HashMap hashMap2 = new HashMap();
                            HashMap hashMap3 = new HashMap();
                            if (null != threeSjt && threeSjt.equals("T")) {
                                l = this.examService.getExampaperNum_threeSjt(exam, subject, grade, null);
                                if (null != l && l.size() > 0) {
                                    for (int i = 0; i < l.size(); i++) {
                                        List<Studentlevel> sjt1 = this.examManageService.getSingleSubjectData(exam, grade, subjectList.get(c).getNum(), "", schoolList.get(a).getNum(), "0", "0", ((Exampaper) l.get(i)).getExamPaperNum().toString(), ((Exampaper) l.get(i)).getSubjectNum().toString(), true, isJointStuType, subCompose);
                                        if (null != sjt1 && sjt1.size() > 0) {
                                            if (i == 0) {
                                                for (int j = 0; j < sjt1.size(); j++) {
                                                    hashMap.put(sjt1.get(j).getRealStudentId(), sjt1.get(j));
                                                }
                                            }
                                            if (i == 1) {
                                                for (int j2 = 0; j2 < sjt1.size(); j2++) {
                                                    hashMap2.put(sjt1.get(j2).getRealStudentId(), sjt1.get(j2));
                                                }
                                            }
                                            if (i == 2) {
                                                for (int j3 = 0; j3 < sjt1.size(); j3++) {
                                                    hashMap3.put(sjt1.get(j3).getRealStudentId(), sjt1.get(j3));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (singleSubjectList != null && singleSubjectList.size() > 0) {
                                if (sheet2_rows == 0) {
                                    Label xuhao2 = new Label(0, 0, "序号");
                                    wSheet2.addCell(xuhao2);
                                    Label xuexiao2 = new Label(1, 0, "学校名称");
                                    wSheet2.addCell(xuexiao2);
                                    String graStr = "年级";
                                    if (null != levelclass && levelclass.equals("T")) {
                                        graStr = "教学班";
                                    }
                                    Label gname2 = new Label(2, 0, graStr);
                                    wSheet2.addCell(gname2);
                                    Label cname2 = new Label(3, 0, "班级");
                                    wSheet2.addCell(cname2);
                                    Label xuehao2 = new Label(4, 0, "学号");
                                    wSheet2.addCell(xuehao2);
                                    if (null != dispStuId && dispStuId.equals("T")) {
                                        bianhao2 = new Label(5, 0, "ID号");
                                    } else {
                                        bianhao2 = new Label(5, 0, "考号");
                                    }
                                    wSheet2.addCell(bianhao2);
                                    Label xuesheng2 = new Label(6, 0, "学生");
                                    wSheet2.addCell(xuesheng2);
                                    Label xuankezuhe2 = new Label(7, 0, "选科组合");
                                    wSheet2.addCell(xuankezuhe2);
                                    Label leibie2 = new Label(8, 0, "类别");
                                    wSheet2.addCell(leibie2);
                                    Label kemu2 = new Label(9, 0, "科目");
                                    wSheet2.addCell(kemu2);
                                    Label zhuguan2 = new Label(10, 0, "主观题");
                                    wSheet2.addCell(zhuguan2);
                                    Label keguan2 = new Label(11, 0, "客观题");
                                    wSheet2.addCell(keguan2);
                                    Label zongfen2 = new Label(12, 0, "原始分");
                                    wSheet2.addCell(zongfen2);
                                    Label fufenTitle = new Label(13, 0, "赋分");
                                    wSheet2.addCell(fufenTitle);
                                    Label gpaiming2 = new Label(14, 0, "年排名");
                                    wSheet2.addCell(gpaiming2);
                                    Label paiming2 = new Label(15, 0, "班排名");
                                    wSheet2.addCell(paiming2);
                                    Label beizhu2 = new Label(16, 0, "备注");
                                    wSheet2.addCell(beizhu2);
                                    if ("T".equals(teacherCheckVal)) {
                                        Label renkejiaoshi = new Label(17, 0, "任课教师");
                                        wSheet2.addCell(renkejiaoshi);
                                    }
                                    if (null != threeSjt && threeSjt.equals("T") && null != l && l.size() > 0) {
                                        for (int si = 0; si < l.size(); si++) {
                                            Label thrkemu2 = new Label(16 + teacherColumn + (si * (7 + teacherColumn)) + 1, 0, "科目");
                                            wSheet2.addCell(thrkemu2);
                                            Label thrzhuguan2 = new Label(16 + teacherColumn + (si * (7 + teacherColumn)) + 2, 0, "主观题");
                                            wSheet2.addCell(thrzhuguan2);
                                            Label thrkeguan2 = new Label(16 + teacherColumn + (si * (7 + teacherColumn)) + 3, 0, "客观题");
                                            wSheet2.addCell(thrkeguan2);
                                            Label thrzongfen2 = new Label(16 + teacherColumn + (si * (7 + teacherColumn)) + 4, 0, "原始分");
                                            wSheet2.addCell(thrzongfen2);
                                            Label thrfufenTitle2 = new Label(16 + teacherColumn + (si * (7 + teacherColumn)) + 5, 0, "赋分");
                                            wSheet2.addCell(thrfufenTitle2);
                                            Label thrgpaiming2 = new Label(16 + teacherColumn + (si * (7 + teacherColumn)) + 6, 0, "年排名");
                                            wSheet2.addCell(thrgpaiming2);
                                            Label thrpaiming2 = new Label(16 + teacherColumn + (si * (7 + teacherColumn)) + 7, 0, "班排名");
                                            wSheet2.addCell(thrpaiming2);
                                            if ("T".equals(teacherCheckVal)) {
                                                Label renkejiaoshi2 = new Label(16 + teacherColumn + (si * (7 + teacherColumn)) + 8, 0, "任课教师");
                                                wSheet2.addCell(renkejiaoshi2);
                                            }
                                        }
                                    }
                                }
                                DecimalFormat df = new DecimalFormat("0.00");
                                if (d > 0) {
                                    sheet2_rows += preCount;
                                }
                                if (sheet2_rows == 0) {
                                    sheet2_rows = 1;
                                }
                                List<String> pageNumList = new ArrayList<>();
                                for (int i2 = 0; i2 < singleSubjectList.size(); i2++) {
                                    String xkzhData = singleSubjectList.get(i2).getExt2();
                                    String lbData = singleSubjectList.get(i2).getStudentType();
                                    xuankezuheSCell7.add(xkzhData);
                                    leibieSCell8.add(lbData);
                                    if (i2 == 0) {
                                        String totalP = singleSubjectList.get(0).getTotalPage();
                                        for (int j4 = 1; j4 <= Integer.parseInt(totalP); j4++) {
                                            pageNumList.add(j4 + "");
                                        }
                                        if ("hideFufen".equals(singleSubjectList.get(0).getExt5())) {
                                            removeSubList.add(13);
                                        }
                                    }
                                    String exception_str = "";
                                    if (null == singleSubjectList.get(i2).getType() || "0".equals(singleSubjectList.get(i2).getType()) || "5".equals(singleSubjectList.get(i2).getType())) {
                                        exception_str = "[缺考]";
                                    } else if ("1".equals(singleSubjectList.get(i2).getType())) {
                                        exception_str = "[违纪]";
                                    } else if ("3".equals(singleSubjectList.get(i2).getType()) || "4".equals(singleSubjectList.get(i2).getType())) {
                                        exception_str = "[零分]";
                                    }
                                    new Label(6, i2 + 1, singleSubjectList.get(i2).getStudentName());
                                    String posPage = singleSubjectList.get(i2).getPossessPage();
                                    String missPage = "";
                                    if (null != posPage && !"".equals(posPage)) {
                                        List posPageList = Arrays.asList(StringUtils.split(posPage, Const.STRING_SEPERATOR));
                                        for (int j5 = 0; j5 < pageNumList.size(); j5++) {
                                            if (!posPageList.contains(pageNumList.get(j5))) {
                                                if ("".equals(missPage)) {
                                                    missPage = missPage + "缺";
                                                }
                                                missPage = missPage + "第" + pageNumList.get(j5) + "页,";
                                            }
                                        }
                                    }
                                    if (!"".equals(missPage)) {
                                        missPage = missPage.substring(0, missPage.length() - 1);
                                    }
                                    Number xh2 = new Number(0, sheet2_rows + i2, sheet2_rows + i2);
                                    wSheet2.addCell(xh2);
                                    Label xx2 = new Label(1, sheet2_rows + i2, singleSubjectList.get(i2).getSchoolName());
                                    wSheet2.addCell(xx2);
                                    String graName = singleSubjectList.get(i2).getGradeName();
                                    if (null != levelclass && levelclass.equals("T")) {
                                        graName = singleSubjectList.get(i2).getLevelClassName();
                                    }
                                    Label gna2 = new Label(2, sheet2_rows + i2, graName);
                                    wSheet2.addCell(gna2);
                                    Label cna2 = new Label(3, sheet2_rows + i2, singleSubjectList.get(i2).getClassName());
                                    wSheet2.addCell(cna2);
                                    String studentNum = singleSubjectList.get(i2).getStudentNum();
                                    if (null == studentNum) {
                                        studentNum = "";
                                    }
                                    Label xueh2 = new Label(4, sheet2_rows + i2, studentNum);
                                    wSheet2.addCell(xueh2);
                                    if (null != dispStuId && dispStuId.equals("T")) {
                                        stuId = singleSubjectList.get(i2).getRealStudentId();
                                    } else {
                                        stuId = singleSubjectList.get(i2).getExamineeNum();
                                    }
                                    Label bh2 = new Label(5, sheet2_rows + i2, stuId);
                                    wSheet2.addCell(bh2);
                                    Label xs2 = new Label(6, sheet2_rows + i2, singleSubjectList.get(i2).getStudentName());
                                    wSheet2.addCell(xs2);
                                    Label xkzh2 = new Label(7, sheet2_rows + i2, singleSubjectList.get(i2).getExt3());
                                    wSheet2.addCell(xkzh2);
                                    Label lb2 = new Label(8, sheet2_rows + i2, singleSubjectList.get(i2).getExt4());
                                    wSheet2.addCell(lb2);
                                    Label km2 = new Label(9, sheet2_rows + i2, singleSubjectList.get(i2).getSubjectName());
                                    wSheet2.addCell(km2);
                                    if (!"".equals(exception_str)) {
                                        Label bzhu2 = new Label(16, sheet2_rows + i2, exception_str + missPage, illcFormat_all);
                                        wSheet2.addCell(bzhu2);
                                    } else if (!"".equals(missPage)) {
                                        Label bzhu22 = new Label(16, sheet2_rows + i2, missPage);
                                        wSheet2.addCell(bzhu22);
                                    } else if (null != singleSubjectList.get(i2).getTotalScore()) {
                                        Label bzhu23 = new Label(16, sheet2_rows + i2, missPage);
                                        wSheet2.addCell(bzhu23);
                                        Number zg2 = new Number(10, sheet2_rows + i2, Double.parseDouble(df.format(singleSubjectList.get(i2).getSqts())));
                                        wSheet2.addCell(zg2);
                                        Number kg2 = new Number(11, sheet2_rows + i2, Double.parseDouble(df.format(singleSubjectList.get(i2).getOqts())));
                                        wSheet2.addCell(kg2);
                                        Number zf2 = new Number(12, sheet2_rows + i2, Double.parseDouble(df.format(singleSubjectList.get(i2).getTotalScore())));
                                        wSheet2.addCell(zf2);
                                        String fufen_ext5 = singleSubjectList.get(i2).getExt5();
                                        if (null == fufen_ext5 || "null".equals(fufen_ext5) || "hideFufen".equals(fufen_ext5)) {
                                            Label ff2 = new Label(13, sheet2_rows + i2, fufen_ext5);
                                            wSheet2.addCell(ff2);
                                        } else {
                                            Number ff22 = new Number(13, sheet2_rows + i2, Double.parseDouble(df.format(Double.valueOf(fufen_ext5))));
                                            wSheet2.addCell(ff22);
                                        }
                                        Number gradepm2 = new Number(14, sheet2_rows + i2, singleSubjectList.get(i2).getGradeRanking());
                                        wSheet2.addCell(gradepm2);
                                        Number pm2 = new Number(15, sheet2_rows + i2, singleSubjectList.get(i2).getClassRanking());
                                        wSheet2.addCell(pm2);
                                    }
                                    if ("T".equals(teacherCheckVal)) {
                                        Label renkejiaoshi3 = new Label(17, sheet2_rows + i2, singleSubjectList.get(i2).getExt6());
                                        wSheet2.addCell(renkejiaoshi3);
                                    }
                                    int sjtCount = 0;
                                    if (null != threeSjt && threeSjt.equals("T") && null != l && l.size() > 0) {
                                        if (null != hashMap && hashMap.size() > 0) {
                                            Studentlevel studentlevel = (Studentlevel) hashMap.get(singleSubjectList.get(i2).getRealStudentId());
                                            if (null != studentlevel) {
                                                if (i2 == 0 && "hideFufen".equals(studentlevel.getExt5())) {
                                                    removeSubList.add(Integer.valueOf(16 + teacherColumn + (0 * (7 + teacherColumn)) + 5));
                                                }
                                                if (0 % 2 == 0) {
                                                    thrkm23 = new Label(16 + teacherColumn + (0 * (7 + teacherColumn)) + 1, sheet2_rows + i2, studentlevel.getSubjectName());
                                                } else {
                                                    thrkm23 = new Label(16 + teacherColumn + (0 * (7 + teacherColumn)) + 1, sheet2_rows + i2, studentlevel.getSubjectName());
                                                }
                                                wSheet2.addCell(thrkm23);
                                                if (0 % 2 == 0) {
                                                    thrzg23 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 2, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel.getSqts())));
                                                } else {
                                                    thrzg23 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 2, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel.getSqts())));
                                                }
                                                wSheet2.addCell(thrzg23);
                                                if (0 % 2 == 0) {
                                                    thrkg23 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 3, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel.getOqts())));
                                                } else {
                                                    thrkg23 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 3, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel.getOqts())));
                                                }
                                                wSheet2.addCell(thrkg23);
                                                double totalScore = 0.0d;
                                                if (studentlevel.getTotalScore() != null) {
                                                    totalScore = studentlevel.getTotalScore().doubleValue();
                                                }
                                                if (0 % 2 == 0) {
                                                    thrzf23 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 4, sheet2_rows + i2, Double.parseDouble(df.format(totalScore)));
                                                } else {
                                                    thrzf23 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 4, sheet2_rows + i2, Double.parseDouble(df.format(totalScore)));
                                                }
                                                wSheet2.addCell(thrzf23);
                                                String fufen_ext52 = studentlevel.getExt5();
                                                if (null == fufen_ext52 || "null".equals(fufen_ext52) || "hideFufen".equals(fufen_ext52)) {
                                                    if (0 % 2 == 0) {
                                                        thrff25 = new Label(16 + teacherColumn + (0 * (7 + teacherColumn)) + 5, sheet2_rows + i2, fufen_ext52);
                                                    } else {
                                                        thrff25 = new Label(16 + teacherColumn + (0 * (7 + teacherColumn)) + 5, sheet2_rows + i2, fufen_ext52);
                                                    }
                                                    wSheet2.addCell(thrff25);
                                                } else {
                                                    if (0 % 2 == 0) {
                                                        thrff26 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 5, sheet2_rows + i2, Double.parseDouble(df.format(Double.valueOf(fufen_ext52))));
                                                    } else {
                                                        thrff26 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 5, sheet2_rows + i2, Double.parseDouble(df.format(Double.valueOf(fufen_ext52))));
                                                    }
                                                    wSheet2.addCell(thrff26);
                                                }
                                                if (0 % 2 == 0) {
                                                    thrgradepm23 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 6, sheet2_rows + i2, studentlevel.getGradeRanking());
                                                } else {
                                                    thrgradepm23 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 6, sheet2_rows + i2, studentlevel.getGradeRanking());
                                                }
                                                wSheet2.addCell(thrgradepm23);
                                                if (0 % 2 == 0) {
                                                    thrpm23 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 7, sheet2_rows + i2, studentlevel.getClassRanking());
                                                } else {
                                                    thrpm23 = new Number(16 + teacherColumn + (0 * (7 + teacherColumn)) + 7, sheet2_rows + i2, studentlevel.getClassRanking());
                                                }
                                                wSheet2.addCell(thrpm23);
                                                if ("T".equals(teacherCheckVal)) {
                                                    Label renkejiaoshi4 = new Label(16 + teacherColumn + (0 * (7 + teacherColumn)) + 8, sheet2_rows + i2, studentlevel.getExt6());
                                                    wSheet2.addCell(renkejiaoshi4);
                                                }
                                            }
                                            sjtCount = 0 + 1;
                                        }
                                        if (null != hashMap2 && hashMap2.size() > 0) {
                                            Studentlevel studentlevel2 = (Studentlevel) hashMap2.get(singleSubjectList.get(i2).getRealStudentId());
                                            if (null != studentlevel2) {
                                                if (i2 == 0 && "hideFufen".equals(studentlevel2.getExt5())) {
                                                    removeSubList.add(Integer.valueOf(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5));
                                                }
                                                if (sjtCount % 2 == 0) {
                                                    thrkm22 = new Label(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 1, sheet2_rows + i2, studentlevel2.getSubjectName());
                                                } else {
                                                    thrkm22 = new Label(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 1, sheet2_rows + i2, studentlevel2.getSubjectName());
                                                }
                                                wSheet2.addCell(thrkm22);
                                                if (sjtCount % 2 == 0) {
                                                    thrzg22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 2, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel2.getSqts())));
                                                } else {
                                                    thrzg22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 2, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel2.getSqts())));
                                                }
                                                wSheet2.addCell(thrzg22);
                                                if (sjtCount % 2 == 0) {
                                                    thrkg22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 3, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel2.getOqts())));
                                                } else {
                                                    thrkg22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 3, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel2.getOqts())));
                                                }
                                                wSheet2.addCell(thrkg22);
                                                double totalScore2 = 0.0d;
                                                if (studentlevel2.getTotalScore() != null) {
                                                    totalScore2 = studentlevel2.getTotalScore().doubleValue();
                                                }
                                                if (sjtCount % 2 == 0) {
                                                    thrzf22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 4, sheet2_rows + i2, Double.parseDouble(df.format(totalScore2)));
                                                } else {
                                                    thrzf22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 4, sheet2_rows + i2, Double.parseDouble(df.format(totalScore2)));
                                                }
                                                wSheet2.addCell(thrzf22);
                                                String fufen_ext53 = studentlevel2.getExt5();
                                                if (null == fufen_ext53 || "null".equals(fufen_ext53) || "hideFufen".equals(fufen_ext53)) {
                                                    if (sjtCount % 2 == 0) {
                                                        thrff23 = new Label(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5, sheet2_rows + i2, fufen_ext53);
                                                    } else {
                                                        thrff23 = new Label(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5, sheet2_rows + i2, fufen_ext53);
                                                    }
                                                    wSheet2.addCell(thrff23);
                                                } else {
                                                    if (sjtCount % 2 == 0) {
                                                        thrff24 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5, sheet2_rows + i2, Double.parseDouble(df.format(Double.valueOf(fufen_ext53))));
                                                    } else {
                                                        thrff24 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5, sheet2_rows + i2, Double.parseDouble(df.format(Double.valueOf(fufen_ext53))));
                                                    }
                                                    wSheet2.addCell(thrff24);
                                                }
                                                if (sjtCount % 2 == 0) {
                                                    thrgradepm22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 6, sheet2_rows + i2, studentlevel2.getGradeRanking());
                                                } else {
                                                    thrgradepm22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 6, sheet2_rows + i2, studentlevel2.getGradeRanking());
                                                }
                                                wSheet2.addCell(thrgradepm22);
                                                if (sjtCount % 2 == 0) {
                                                    thrpm22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 7, sheet2_rows + i2, studentlevel2.getClassRanking());
                                                } else {
                                                    thrpm22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 7, sheet2_rows + i2, studentlevel2.getClassRanking());
                                                }
                                                wSheet2.addCell(thrpm22);
                                                if ("T".equals(teacherCheckVal)) {
                                                    Label renkejiaoshi5 = new Label(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 8, sheet2_rows + i2, studentlevel2.getExt6());
                                                    wSheet2.addCell(renkejiaoshi5);
                                                }
                                            }
                                            sjtCount++;
                                        }
                                        if (null != hashMap3 && hashMap3.size() > 0) {
                                            Studentlevel studentlevel3 = (Studentlevel) hashMap3.get(singleSubjectList.get(i2).getRealStudentId());
                                            if (null != studentlevel3) {
                                                if (i2 == 0 && "hideFufen".equals(studentlevel3.getExt5())) {
                                                    removeSubList.add(Integer.valueOf(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5));
                                                }
                                                if (sjtCount % 2 == 0) {
                                                    thrkm2 = new Label(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 1, sheet2_rows + i2, studentlevel3.getSubjectName());
                                                } else {
                                                    thrkm2 = new Label(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 1, sheet2_rows + i2, studentlevel3.getSubjectName());
                                                }
                                                wSheet2.addCell(thrkm2);
                                                if (sjtCount % 2 == 0) {
                                                    thrzg2 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 2, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel3.getSqts())));
                                                } else {
                                                    thrzg2 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 2, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel3.getSqts())));
                                                }
                                                wSheet2.addCell(thrzg2);
                                                if (sjtCount % 2 == 0) {
                                                    thrkg2 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 3, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel3.getOqts())));
                                                } else {
                                                    thrkg2 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 3, sheet2_rows + i2, Double.parseDouble(df.format(studentlevel3.getOqts())));
                                                }
                                                wSheet2.addCell(thrkg2);
                                                double totalScore3 = 0.0d;
                                                if (studentlevel3.getTotalScore() != null) {
                                                    totalScore3 = studentlevel3.getTotalScore().doubleValue();
                                                }
                                                if (sjtCount % 2 == 0) {
                                                    thrzf2 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 4, sheet2_rows + i2, Double.parseDouble(df.format(totalScore3)));
                                                } else {
                                                    thrzf2 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 4, sheet2_rows + i2, Double.parseDouble(df.format(totalScore3)));
                                                }
                                                wSheet2.addCell(thrzf2);
                                                String fufen_ext54 = studentlevel3.getExt5();
                                                if (null == fufen_ext54 || "null".equals(fufen_ext54) || "hideFufen".equals(fufen_ext54)) {
                                                    if (sjtCount % 2 == 0) {
                                                        thrff2 = new Label(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5, sheet2_rows + i2, fufen_ext54);
                                                    } else {
                                                        thrff2 = new Label(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5, sheet2_rows + i2, fufen_ext54);
                                                    }
                                                    wSheet2.addCell(thrff2);
                                                } else {
                                                    if (sjtCount % 2 == 0) {
                                                        thrff22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5, sheet2_rows + i2, Double.parseDouble(df.format(Double.valueOf(fufen_ext54))));
                                                    } else {
                                                        thrff22 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5, sheet2_rows + i2, Double.parseDouble(df.format(Double.valueOf(fufen_ext54))));
                                                    }
                                                    wSheet2.addCell(thrff22);
                                                }
                                                if (sjtCount % 2 == 0) {
                                                    thrgradepm2 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 6, sheet2_rows + i2, studentlevel3.getGradeRanking());
                                                } else {
                                                    thrgradepm2 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 6, sheet2_rows + i2, studentlevel3.getGradeRanking());
                                                }
                                                wSheet2.addCell(thrgradepm2);
                                                if (sjtCount % 2 == 0) {
                                                    thrpm2 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 7, sheet2_rows + i2, studentlevel3.getClassRanking());
                                                } else {
                                                    thrpm2 = new Number(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 7, sheet2_rows + i2, studentlevel3.getClassRanking());
                                                }
                                                wSheet2.addCell(thrpm2);
                                                if ("T".equals(teacherCheckVal)) {
                                                    Label renkejiaoshi6 = new Label(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 8, sheet2_rows + i2, studentlevel3.getExt6());
                                                    wSheet2.addCell(renkejiaoshi6);
                                                }
                                            }
                                            int i3 = sjtCount + 1;
                                        }
                                    }
                                    ThreadUtil.sleep(5L);
                                }
                                preCount = singleSubjectList.size();
                                singleSubjectList.size();
                                if (d == schoolList.size() - 1) {
                                    removeOneSubExcelColumn(wSheet2, removeSubList, leibieSCell8, xuankezuheSCell7);
                                    wwBook2.write();
                                    wwBook2.close();
                                    xuankezuheSCell7.clear();
                                    leibieSCell8.clear();
                                }
                                d++;
                            } else {
                                removeOneSubExcelColumn(wSheet2, removeSubList, leibieSCell8, xuankezuheSCell7);
                                wwBook2.write();
                                wwBook2.close();
                                xuankezuheSCell7.clear();
                                leibieSCell8.clear();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            this.log.info("生成【单科目成绩】失败：--" + loginUser + "--exam:[" + exam + "]--grade:[" + grade + "]--subject:[" + subject + "]--studentType:[" + studentType + "]--subCompose:[" + subCompose + "]--fufen:[" + fufen + "]--" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS").format(new Date()) + e);
            e.printStackTrace();
        }
    }

    public String getDoubleString(double number) {
        String numberStr = null;
        if (((int) number) * 1000 == ((int) (number * 1000.0d))) {
            String numberStr2 = String.valueOf((int) number);
            return numberStr2;
        }
        DecimalFormat df = new DecimalFormat("######0.00");
        String values = df.format(new BigDecimal(number));
        Matcher matcher = Pattern.compile("^(-?\\d*)(\\.?0*)$").matcher(values);
        if (matcher.find()) {
            numberStr = Convert.toStr(matcher.group(1));
        }
        Matcher matcher1 = Pattern.compile("^(-?\\d*\\.\\d*[1-9])(0*)$").matcher(values);
        if (matcher1.find()) {
            numberStr = Convert.toStr(matcher1.group(1));
        }
        return numberStr;
    }

    public void writeSingleScoreSheet_xlsx(Map<String, String> map, String xuanDirFolder, String schoolNum) {
        List<Studentlevel> singleSubjectList;
        String[] biaotou;
        Object[] data;
        String loginUser = map.get("loginUser");
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        if ("-1".equals(subject)) {
            subject = map.get("sjt");
        }
        String fufen = map.get("fufen");
        String resourceType = map.get("resourceType");
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String subCompose = map.get("xuankezuhe");
        String isJointStuType = map.get(Const.isJointStuType);
        String dispStuId = map.get("dispStuId");
        String threeSjt = map.get("threeSjt");
        String teacherCheckVal = map.get("teacherCheckVal");
        String graDengjiCheckVal = map.get("graDengjiCheckVal");
        String areaDengjiCheckVal = map.get("areaDengjiCheckVal");
        String claRankCheckVal = map.get("claRankCheckVal");
        String graRankCheckVal = map.get("graRankCheckVal");
        String areaRankCheckVal = map.get("areaRankCheckVal");
        new ArrayList();
        new ArrayList();
        try {
            List<AjaxData> subjectList = this.examManageService.getSjtByNum(subject);
            int teacherColumn = "T".equals(teacherCheckVal) ? 1 : 0;
            int dLen = 0;
            int rankLen = 0;
            List<String> rankList = new ArrayList<>();
            if ("T".equals(graDengjiCheckVal)) {
                rankList.add("校等级");
                dLen = 0 + 1;
            }
            if ("T".equals(areaDengjiCheckVal)) {
                rankList.add("区等级");
                int i = dLen + 1;
            }
            if (null != claRankCheckVal && "T".equals(claRankCheckVal)) {
                rankList.add("班排名");
                rankLen = 0 + 1;
            }
            if (null != graRankCheckVal && "T".equals(graRankCheckVal)) {
                rankList.add("校排名");
                rankLen++;
            }
            if (null != areaRankCheckVal && "T".equals(areaRankCheckVal)) {
                rankList.add("区排名");
                int i2 = rankLen + 1;
            }
            if ("T".equals(teacherCheckVal)) {
                rankList.add("任课教师");
            }
            int rLen = rankList.size();
            Set xuankezuheSCell7 = new HashSet();
            Set leibieSCell8 = new HashSet();
            if (null != subjectList && subjectList.size() > 0) {
                for (int c = 0; c < subjectList.size(); c++) {
                    List<AjaxData> schoolList = this.analy.getSchool(exam, subjectList.get(c).getNum(), studentType, grade, fufen);
                    SXSSFWorkbook wwBook = new SXSSFWorkbook();
                    SXSSFSheet createSheet = wwBook.createSheet("全区");
                    if (null != schoolList && schoolList.size() > 0) {
                        String fileName_all = fufen + "_" + resourceType + "_" + exam + "_" + grade + "_" + subject + "_" + studentType + "_" + subCompose + "_allSchool";
                        String schoolDirFolder_all = xuanDirFolder + fileName_all + ".xlsx";
                        createFile(schoolDirFolder_all);
                        File f0_all = new File(schoolDirFolder_all);
                        if (!f0_all.exists()) {
                            f0_all.createNewFile();
                        } else {
                            f0_all.delete();
                            f0_all.createNewFile();
                        }
                        int d = 0;
                        int preCount = 0;
                        int sheet2_rows = 0;
                        for (int a = 0; a < schoolList.size(); a++) {
                            SXSSFWorkbook wwBook2 = new SXSSFWorkbook();
                            SXSSFSheet createSheet2 = wwBook2.createSheet(schoolList.get(a).getName());
                            String schoolNum2 = schoolList.get(a).getNum();
                            String fileName = fufen + "_" + resourceType + "_" + exam + "_" + grade + "_" + subject + "_" + studentType + "_" + subCompose + "_" + schoolNum2;
                            String schoolDirFolder = xuanDirFolder + fileName + ".xlsx";
                            createFile(schoolDirFolder);
                            File f0 = new File(schoolDirFolder);
                            if (!f0.exists()) {
                                f0.createNewFile();
                            } else {
                                f0.delete();
                                f0.createNewFile();
                            }
                            List<Integer> removeSubList = new ArrayList<>();
                            String levelclass = this.system.getIsLevelClass(exam, grade, subject, null);
                            new ArrayList();
                            if (null != levelclass && levelclass.equals("T")) {
                                singleSubjectList = this.examManageService.getSingleSubjectData_levcla(exam, grade, subjectList.get(c).getNum(), "", schoolList.get(a).getNum(), "0", "0", isJointStuType, subCompose);
                            } else {
                                singleSubjectList = this.examManageService.getSingleSubjectData(exam, grade, subjectList.get(c).getNum(), "", schoolList.get(a).getNum(), "0", "0", "", "", false, isJointStuType, subCompose);
                            }
                            List l = new ArrayList();
                            new ArrayList();
                            HashMap hashMap = new HashMap();
                            HashMap hashMap2 = new HashMap();
                            HashMap hashMap3 = new HashMap();
                            if (null != threeSjt && threeSjt.equals("T")) {
                                l = this.examService.getExampaperNum_threeSjt(exam, subject, grade, null);
                                if (null != l && l.size() > 0) {
                                    for (int i3 = 0; i3 < l.size(); i3++) {
                                        List<Studentlevel> sjt1 = this.examManageService.getSingleSubjectData(exam, grade, subjectList.get(c).getNum(), "", schoolList.get(a).getNum(), "0", "0", ((Exampaper) l.get(i3)).getExamPaperNum().toString(), ((Exampaper) l.get(i3)).getSubjectNum().toString(), true, isJointStuType, subCompose);
                                        if (null != sjt1 && sjt1.size() > 0) {
                                            if (i3 == 0) {
                                                for (int j = 0; j < sjt1.size(); j++) {
                                                    hashMap.put(sjt1.get(j).getRealStudentId(), sjt1.get(j));
                                                }
                                            }
                                            if (i3 == 1) {
                                                for (int j2 = 0; j2 < sjt1.size(); j2++) {
                                                    hashMap2.put(sjt1.get(j2).getRealStudentId(), sjt1.get(j2));
                                                }
                                            }
                                            if (i3 == 2) {
                                                for (int j3 = 0; j3 < sjt1.size(); j3++) {
                                                    hashMap3.put(sjt1.get(j3).getRealStudentId(), sjt1.get(j3));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (singleSubjectList != null && singleSubjectList.size() > 0) {
                                String graStr = "年级";
                                if (null != levelclass && levelclass.equals("T")) {
                                    graStr = "教学班";
                                }
                                if (null != dispStuId && dispStuId.equals("T")) {
                                    biaotou = new String[]{"序号", "学校名称", graStr, "班级", "学号", "考号", "ID号", "学生", "选科组合", "类别", "生源"};
                                } else {
                                    biaotou = new String[]{"序号", "学校名称", graStr, "班级", "学号", "考号", "学生", "选科组合", "类别", "生源"};
                                }
                                int len = biaotou.length;
                                Row wSheet2_row0 = createSheet2.createRow(0);
                                for (int i4 = 0; i4 < len; i4++) {
                                    wSheet2_row0.createCell(i4).setCellValue(biaotou[i4]);
                                }
                                String[] subTitle = {"科目", "主观题", "客观题", "原始分", "赋分"};
                                if ("hideFufen".equals(singleSubjectList.get(0).getExt5())) {
                                    subTitle = new String[]{"科目", "主观题", "客观题", "原始分"};
                                }
                                int subLen = subTitle.length;
                                for (int s = 0; s < subLen; s++) {
                                    wSheet2_row0.createCell(len + s).setCellValue(subTitle[s]);
                                }
                                for (int p = 0; p < rLen; p++) {
                                    wSheet2_row0.createCell(len + subLen + p).setCellValue(rankList.get(p));
                                }
                                Cell beizhuCell = wSheet2_row0.createCell(len + subLen + rLen);
                                beizhuCell.setCellValue("备注");
                                if (null != threeSjt && threeSjt.equals("T") && null != l && l.size() > 0) {
                                    for (int si = 0; si < l.size(); si++) {
                                        for (int s2 = 0; s2 < subLen; s2++) {
                                            wSheet2_row0.createCell(len + subLen + rLen + 1 + (si * (subLen + rLen)) + s2 + 1).setCellValue(subTitle[s2]);
                                        }
                                        for (int p2 = 0; p2 < rLen; p2++) {
                                            wSheet2_row0.createCell(len + subLen + rLen + 1 + (si * (subLen + rLen)) + subLen + p2 + 1).setCellValue(rankList.get(p2));
                                        }
                                    }
                                }
                                if (sheet2_rows == 0) {
                                    Row wSheet_row0 = createSheet.createRow(0);
                                    for (int i5 = 0; i5 < len; i5++) {
                                        Cell cell_all1 = wSheet_row0.createCell(i5);
                                        cell_all1.setCellValue(biaotou[i5]);
                                    }
                                    String[] strArr = {"科目", "主观题", "客观题", "原始分", "赋分"};
                                    if ("hideFufen".equals(singleSubjectList.get(0).getExt5())) {
                                        subTitle = new String[]{"科目", "主观题", "客观题", "原始分"};
                                    }
                                    int subLen2 = subTitle.length;
                                    for (int s3 = 0; s3 < subLen; s3++) {
                                        Cell cell_all2 = wSheet_row0.createCell(len + s3);
                                        cell_all2.setCellValue(subTitle[s3]);
                                    }
                                    for (int p3 = 0; p3 < rLen; p3++) {
                                        wSheet_row0.createCell(len + subLen2 + p3).setCellValue(rankList.get(p3));
                                    }
                                    Cell beizhuCell_all = wSheet_row0.createCell(len + subLen2 + rLen);
                                    beizhuCell_all.setCellValue("备注");
                                    if (null != threeSjt && threeSjt.equals("T") && null != l && l.size() > 0) {
                                        for (int si2 = 0; si2 < l.size(); si2++) {
                                            for (int s4 = 0; s4 < subLen2; s4++) {
                                                wSheet_row0.createCell(len + subLen2 + rLen + 1 + (si2 * (subLen2 + rLen)) + s4).setCellValue(subTitle[s4]);
                                            }
                                            for (int p4 = 0; p4 < rLen; p4++) {
                                                wSheet_row0.createCell(len + subLen2 + rLen + 1 + (si2 * (subLen2 + rLen)) + subLen2 + p4).setCellValue(rankList.get(p4));
                                            }
                                        }
                                    }
                                }
                                new DecimalFormat("0.00");
                                if (d > 0) {
                                    sheet2_rows += preCount;
                                }
                                if (sheet2_rows == 0) {
                                    sheet2_rows = 1;
                                }
                                List<String> pageNumList = new ArrayList<>();
                                for (int i6 = 0; i6 < singleSubjectList.size(); i6++) {
                                    String xkzhData = singleSubjectList.get(i6).getExt2();
                                    String lbData = singleSubjectList.get(i6).getStudentType();
                                    xuankezuheSCell7.add(xkzhData);
                                    leibieSCell8.add(lbData);
                                    if (i6 == 0) {
                                        String totalP = singleSubjectList.get(0).getTotalPage();
                                        for (int j4 = 1; j4 <= Integer.parseInt(totalP); j4++) {
                                            pageNumList.add(j4 + "");
                                        }
                                        if ("hideFufen".equals(singleSubjectList.get(0).getExt5())) {
                                            removeSubList.add(13);
                                        }
                                    }
                                    Row wSheet_rowI1 = createSheet.createRow(sheet2_rows + i6);
                                    Row wSheet2_rowI1 = createSheet2.createRow(i6 + 1);
                                    String graName = singleSubjectList.get(i6).getGradeName();
                                    if (null != levelclass && levelclass.equals("T")) {
                                        graName = singleSubjectList.get(i6).getLevelClassName();
                                    }
                                    String studentNum = singleSubjectList.get(i6).getStudentNum();
                                    if (null == studentNum) {
                                        studentNum = "";
                                    }
                                    if (null != dispStuId && dispStuId.equals("T")) {
                                        data = new Object[]{Integer.valueOf(i6 + 1), singleSubjectList.get(i6).getSchoolName(), graName, singleSubjectList.get(i6).getClassName(), studentNum, singleSubjectList.get(i6).getExamineeNum(), singleSubjectList.get(i6).getRealStudentId(), singleSubjectList.get(i6).getStudentName(), singleSubjectList.get(i6).getExt3(), singleSubjectList.get(i6).getExt4(), singleSubjectList.get(i6).getSource() + "-" + singleSubjectList.get(i6).getIsJoin()};
                                    } else {
                                        data = new Object[]{Integer.valueOf(i6 + 1), singleSubjectList.get(i6).getSchoolName(), graName, singleSubjectList.get(i6).getClassName(), studentNum, singleSubjectList.get(i6).getExamineeNum(), singleSubjectList.get(i6).getStudentName(), singleSubjectList.get(i6).getExt3(), singleSubjectList.get(i6).getExt4(), singleSubjectList.get(i6).getSource() + "-" + singleSubjectList.get(i6).getIsJoin()};
                                    }
                                    int len_data = data.length;
                                    for (int i_data = 0; i_data < len_data; i_data++) {
                                        Cell cell = wSheet2_rowI1.createCell(i_data);
                                        Cell cell_all = wSheet_rowI1.createCell(i_data);
                                        if (data[i_data] instanceof String) {
                                            cell.setCellValue(String.valueOf(data[i_data]));
                                            cell_all.setCellValue(String.valueOf(data[i_data]));
                                        } else if (null == data[i_data]) {
                                            cell.setCellValue("");
                                            cell_all.setCellValue("");
                                        } else {
                                            cell.setCellValue(Integer.valueOf(data[i_data].toString()).intValue());
                                            cell_all.setCellValue(Integer.valueOf(data[i_data].toString()).intValue());
                                        }
                                    }
                                    String exception_str = "";
                                    if (null == singleSubjectList.get(i6).getType() || "0".equals(singleSubjectList.get(i6).getType()) || "5".equals(singleSubjectList.get(i6).getType())) {
                                        exception_str = "[缺考]";
                                    } else if ("1".equals(singleSubjectList.get(i6).getType())) {
                                        exception_str = "[违纪]";
                                    } else if ("3".equals(singleSubjectList.get(i6).getType()) || "4".equals(singleSubjectList.get(i6).getType())) {
                                        exception_str = "[零分]";
                                    }
                                    singleSubjectList.get(i6).getPossessPage();
                                    String missPage = "";
                                    if (!"".equals(missPage)) {
                                        missPage = missPage.substring(0, missPage.length() - 1);
                                    }
                                    Object[] subData = new Object[5];
                                    if ("hideFufen".equals(singleSubjectList.get(0).getExt5())) {
                                        subData = new String[4];
                                    }
                                    int subLen_data = subData.length;
                                    List<Object> rankDataList = new ArrayList<>();
                                    if ("T".equals(graDengjiCheckVal)) {
                                        rankDataList.add(singleSubjectList.get(i6).getDengjixiao());
                                    }
                                    if ("T".equals(areaDengjiCheckVal)) {
                                        rankDataList.add(singleSubjectList.get(i6).getDengji());
                                    }
                                    if (null != claRankCheckVal && "T".equals(claRankCheckVal)) {
                                        rankDataList.add(Integer.valueOf(singleSubjectList.get(i6).getClassRanking()));
                                    }
                                    if (null != graRankCheckVal && "T".equals(graRankCheckVal)) {
                                        rankDataList.add(Integer.valueOf(singleSubjectList.get(i6).getGradeRanking()));
                                    }
                                    if (null != areaRankCheckVal && "T".equals(areaRankCheckVal)) {
                                        rankDataList.add(Integer.valueOf(singleSubjectList.get(i6).getAreaRanking()));
                                    }
                                    if ("T".equals(teacherCheckVal)) {
                                        rankDataList.add(singleSubjectList.get(i6).getExt6());
                                    }
                                    if (!"".equals(exception_str)) {
                                        Object[] subData2 = {singleSubjectList.get(i6).getSubjectName(), "", "", "", ""};
                                        if ("hideFufen".equals(singleSubjectList.get(0).getExt5())) {
                                            subData2 = new Object[]{singleSubjectList.get(i6).getSubjectName(), "", "", ""};
                                        }
                                        for (int s_data = 0; s_data < subLen_data; s_data++) {
                                            wSheet2_rowI1.createCell(len_data + s_data).setCellValue(String.valueOf(subData2[s_data]));
                                            wSheet_rowI1.createCell(len_data + s_data).setCellValue(String.valueOf(subData2[s_data]));
                                        }
                                        Cell beizhuDataCell = wSheet2_rowI1.createCell(len_data + subLen_data + rLen);
                                        beizhuDataCell.setCellValue(exception_str + missPage);
                                        Cell beizhuDataCell_all = wSheet_rowI1.createCell(len_data + subLen_data + rLen);
                                        beizhuDataCell_all.setCellValue(exception_str + missPage);
                                    } else if (null != singleSubjectList.get(i6).getTotalScore()) {
                                        DecimalFormat df01 = new DecimalFormat("0.0");
                                        String fufen_ext5 = singleSubjectList.get(i6).getExt5();
                                        Object[] subData3 = {singleSubjectList.get(i6).getSubjectName(), Convert.toDouble(Convert.toBigDecimal(df01.format(singleSubjectList.get(i6).getSqts())).stripTrailingZeros().toPlainString()), Convert.toDouble(Convert.toBigDecimal(df01.format(singleSubjectList.get(i6).getOqts())).stripTrailingZeros().toPlainString()), Convert.toDouble(Convert.toBigDecimal(df01.format(singleSubjectList.get(i6).getTotalScore())).stripTrailingZeros().toPlainString())};
                                        if (!"hideFufen".equals(singleSubjectList.get(0).getExt5())) {
                                            Object[] objArr = new Object[5];
                                            objArr[0] = singleSubjectList.get(i6).getSubjectName();
                                            objArr[1] = Convert.toDouble(Convert.toBigDecimal(df01.format(singleSubjectList.get(i6).getSqts())).stripTrailingZeros().toPlainString());
                                            objArr[2] = Convert.toDouble(Convert.toBigDecimal(df01.format(singleSubjectList.get(i6).getOqts())).stripTrailingZeros().toPlainString());
                                            objArr[3] = Convert.toDouble(Convert.toBigDecimal(df01.format(singleSubjectList.get(i6).getTotalScore())).stripTrailingZeros().toPlainString());
                                            objArr[4] = fufen_ext5 == null ? "" : Convert.toDouble(Convert.toBigDecimal(df01.format(Convert.toDouble(fufen_ext5.toString()))).stripTrailingZeros().toPlainString());
                                            subData3 = objArr;
                                        }
                                        for (int s_data2 = 0; s_data2 < subLen_data; s_data2++) {
                                            Cell cell2 = wSheet2_rowI1.createCell(len_data + s_data2);
                                            Cell cell_all3 = wSheet_rowI1.createCell(len_data + s_data2);
                                            if (subData3[s_data2] instanceof String) {
                                                cell2.setCellValue(String.valueOf(subData3[s_data2]));
                                                cell_all3.setCellValue(String.valueOf(subData3[s_data2]));
                                            } else if (null == subData3[s_data2]) {
                                                cell2.setCellValue("");
                                                cell_all3.setCellValue("");
                                            } else {
                                                cell2.setCellValue(((Double) subData3[s_data2]).doubleValue());
                                                cell_all3.setCellValue(((Double) subData3[s_data2]).doubleValue());
                                            }
                                        }
                                        Cell beizhuDataCell2 = wSheet2_rowI1.createCell(len_data + subLen_data + rLen);
                                        beizhuDataCell2.setCellValue(missPage);
                                        Cell beizhuDataCell_all2 = wSheet_rowI1.createCell(len_data + subLen_data + rLen);
                                        beizhuDataCell_all2.setCellValue(missPage);
                                        for (int p_data = 0; p_data < rLen; p_data++) {
                                            Cell pcell = wSheet2_rowI1.createCell(len_data + subLen_data + p_data);
                                            Cell pcell_all = wSheet_rowI1.createCell(len_data + subLen_data + p_data);
                                            if (rankDataList.get(p_data) instanceof String) {
                                                pcell.setCellValue(String.valueOf(rankDataList.get(p_data)));
                                                pcell_all.setCellValue(String.valueOf(rankDataList.get(p_data)));
                                            } else if (null == rankDataList.get(p_data)) {
                                                pcell.setCellValue("");
                                                pcell_all.setCellValue("");
                                            } else {
                                                pcell.setCellValue(((Integer) rankDataList.get(p_data)).intValue());
                                                pcell_all.setCellValue(((Integer) rankDataList.get(p_data)).intValue());
                                            }
                                        }
                                    }
                                    int sjtCount = 0;
                                    DecimalFormat df012 = new DecimalFormat("0.0");
                                    if (null != threeSjt && threeSjt.equals("T") && null != l && l.size() > 0) {
                                        if (null != hashMap && hashMap.size() > 0) {
                                            Studentlevel studentlevel = (Studentlevel) hashMap.get(singleSubjectList.get(i6).getRealStudentId());
                                            if (null != studentlevel) {
                                                if (i6 == 0 && "hideFufen".equals(studentlevel.getExt5())) {
                                                    removeSubList.add(Integer.valueOf(16 + teacherColumn + (0 * (7 + teacherColumn)) + 5));
                                                }
                                                double totalScore = 0.0d;
                                                if (studentlevel.getTotalScore() != null) {
                                                    totalScore = studentlevel.getTotalScore().doubleValue();
                                                }
                                                String fufen_ext52 = studentlevel.getExt5();
                                                Object[] subData_threeSjt = {studentlevel.getSubjectName(), Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel.getSqts())).stripTrailingZeros().toPlainString()), Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel.getOqts())).stripTrailingZeros().toPlainString()), Convert.toDouble(Convert.toBigDecimal(df012.format(totalScore)).stripTrailingZeros().toPlainString())};
                                                if (!"hideFufen".equals(studentlevel.getExt5())) {
                                                    Object[] objArr2 = new Object[5];
                                                    objArr2[0] = studentlevel.getSubjectName();
                                                    objArr2[1] = Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel.getSqts())).stripTrailingZeros().toPlainString());
                                                    objArr2[2] = Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel.getOqts())).stripTrailingZeros().toPlainString());
                                                    objArr2[3] = Convert.toDouble(Convert.toBigDecimal(df012.format(totalScore)).stripTrailingZeros().toPlainString());
                                                    objArr2[4] = fufen_ext52 == null ? "" : Convert.toDouble(Convert.toBigDecimal(df012.format(Convert.toDouble(fufen_ext52.toString()))).stripTrailingZeros().toPlainString());
                                                    subData_threeSjt = objArr2;
                                                }
                                                int subLen_data_threeSjt = subData_threeSjt.length;
                                                for (int s_data_threeSjt = 0; s_data_threeSjt < subLen_data_threeSjt; s_data_threeSjt++) {
                                                    Cell cell22 = wSheet2_rowI1.createCell(len + subLen_data_threeSjt + rLen + 1 + (0 * (subLen_data_threeSjt + rLen)) + s_data_threeSjt + 1);
                                                    Cell cell2_all = wSheet_rowI1.createCell(len + subLen_data_threeSjt + rLen + 1 + (0 * (subLen_data_threeSjt + rLen)) + s_data_threeSjt + 1);
                                                    if (subData_threeSjt[s_data_threeSjt] instanceof String) {
                                                        cell22.setCellValue(String.valueOf(subData_threeSjt[s_data_threeSjt]));
                                                        cell2_all.setCellValue(String.valueOf(subData_threeSjt[s_data_threeSjt]));
                                                    } else if (null == subData_threeSjt[s_data_threeSjt]) {
                                                        cell22.setCellValue("");
                                                        cell2_all.setCellValue("");
                                                    } else {
                                                        cell22.setCellValue(Double.valueOf(subData_threeSjt[s_data_threeSjt].toString()).doubleValue());
                                                        cell2_all.setCellValue(Double.valueOf(subData_threeSjt[s_data_threeSjt].toString()).doubleValue());
                                                    }
                                                }
                                                List<Object> rankDataList_threeSjt = new ArrayList<>();
                                                if ("T".equals(graDengjiCheckVal)) {
                                                    rankDataList_threeSjt.add(studentlevel.getDengjixiao());
                                                }
                                                if ("T".equals(areaDengjiCheckVal)) {
                                                    rankDataList_threeSjt.add(studentlevel.getDengji());
                                                }
                                                if (null != claRankCheckVal && "T".equals(claRankCheckVal)) {
                                                    rankDataList_threeSjt.add(Integer.valueOf(studentlevel.getClassRanking()));
                                                }
                                                if (null != graRankCheckVal && "T".equals(graRankCheckVal)) {
                                                    rankDataList_threeSjt.add(Integer.valueOf(studentlevel.getGradeRanking()));
                                                }
                                                if (null != areaRankCheckVal && "T".equals(areaRankCheckVal)) {
                                                    rankDataList_threeSjt.add(Integer.valueOf(studentlevel.getAreaRanking()));
                                                }
                                                if ("T".equals(teacherCheckVal)) {
                                                    rankDataList_threeSjt.add(studentlevel.getExt6());
                                                }
                                                int rLen_data_threeSjt = rankDataList_threeSjt.size();
                                                for (int p_data_threeSjt = 0; p_data_threeSjt < rLen_data_threeSjt; p_data_threeSjt++) {
                                                    Cell pcell2 = wSheet2_rowI1.createCell(len + subLen_data_threeSjt + rLen_data_threeSjt + 1 + (0 * (subLen_data_threeSjt + rLen_data_threeSjt)) + subLen_data_threeSjt + p_data_threeSjt + 1);
                                                    Cell pcell2_all = wSheet_rowI1.createCell(len + subLen_data_threeSjt + rLen_data_threeSjt + 1 + (0 * (subLen_data_threeSjt + rLen_data_threeSjt)) + subLen_data_threeSjt + p_data_threeSjt + 1);
                                                    if (rankDataList_threeSjt.get(p_data_threeSjt) instanceof String) {
                                                        pcell2.setCellValue(String.valueOf(rankDataList_threeSjt.get(p_data_threeSjt)));
                                                        pcell2_all.setCellValue(String.valueOf(rankDataList_threeSjt.get(p_data_threeSjt)));
                                                    } else if (null == rankDataList_threeSjt.get(p_data_threeSjt)) {
                                                        pcell2.setCellValue("");
                                                        pcell2_all.setCellValue("");
                                                    } else {
                                                        pcell2.setCellValue(Integer.valueOf(rankDataList_threeSjt.get(p_data_threeSjt).toString()).intValue());
                                                        pcell2_all.setCellValue(Integer.valueOf(rankDataList_threeSjt.get(p_data_threeSjt).toString()).intValue());
                                                    }
                                                }
                                            }
                                            sjtCount = 0 + 1;
                                        }
                                        if (null != hashMap2 && hashMap2.size() > 0) {
                                            Studentlevel studentlevel2 = (Studentlevel) hashMap2.get(singleSubjectList.get(i6).getRealStudentId());
                                            if (null != studentlevel2) {
                                                if (i6 == 0 && "hideFufen".equals(studentlevel2.getExt5())) {
                                                    removeSubList.add(Integer.valueOf(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5));
                                                }
                                                double totalScore2 = 0.0d;
                                                if (studentlevel2.getTotalScore() != null) {
                                                    totalScore2 = studentlevel2.getTotalScore().doubleValue();
                                                }
                                                String fufen_ext53 = studentlevel2.getExt5();
                                                Object[] subData_threeSjt2 = {studentlevel2.getSubjectName(), Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel2.getSqts())).stripTrailingZeros().toPlainString()), Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel2.getOqts())).stripTrailingZeros().toPlainString()), Convert.toDouble(Convert.toBigDecimal(df012.format(totalScore2)).stripTrailingZeros().toPlainString())};
                                                if (!"hideFufen".equals(studentlevel2.getExt5())) {
                                                    Object[] objArr3 = new Object[5];
                                                    objArr3[0] = studentlevel2.getSubjectName();
                                                    objArr3[1] = Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel2.getSqts())).stripTrailingZeros().toPlainString());
                                                    objArr3[2] = Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel2.getOqts())).stripTrailingZeros().toPlainString());
                                                    objArr3[3] = Convert.toDouble(Convert.toBigDecimal(df012.format(totalScore2)).stripTrailingZeros().toPlainString());
                                                    objArr3[4] = fufen_ext53 == null ? "" : Convert.toDouble(Convert.toBigDecimal(df012.format(Convert.toDouble(fufen_ext53.toString()))).stripTrailingZeros().toPlainString());
                                                    subData_threeSjt2 = objArr3;
                                                }
                                                int subLen_data_threeSjt2 = subData_threeSjt2.length;
                                                for (int s_data_threeSjt2 = 0; s_data_threeSjt2 < subLen_data_threeSjt2; s_data_threeSjt2++) {
                                                    Cell cell23 = wSheet2_rowI1.createCell(len + subLen_data_threeSjt2 + rLen + 1 + (sjtCount * (subLen_data_threeSjt2 + rLen)) + s_data_threeSjt2 + 1);
                                                    Cell cell2_all2 = wSheet_rowI1.createCell(len + subLen_data_threeSjt2 + rLen + 1 + (sjtCount * (subLen_data_threeSjt2 + rLen)) + s_data_threeSjt2 + 1);
                                                    if (subData_threeSjt2[s_data_threeSjt2] instanceof String) {
                                                        cell23.setCellValue(String.valueOf(subData_threeSjt2[s_data_threeSjt2]));
                                                        cell2_all2.setCellValue(String.valueOf(subData_threeSjt2[s_data_threeSjt2]));
                                                    } else if (null == subData_threeSjt2[s_data_threeSjt2]) {
                                                        cell23.setCellValue("");
                                                        cell2_all2.setCellValue("");
                                                    } else {
                                                        cell23.setCellValue(Double.valueOf(subData_threeSjt2[s_data_threeSjt2].toString()).doubleValue());
                                                        cell2_all2.setCellValue(Double.valueOf(subData_threeSjt2[s_data_threeSjt2].toString()).doubleValue());
                                                    }
                                                }
                                                List<Object> rankDataList_threeSjt2 = new ArrayList<>();
                                                if ("T".equals(graDengjiCheckVal)) {
                                                    rankDataList_threeSjt2.add(studentlevel2.getDengjixiao());
                                                }
                                                if ("T".equals(areaDengjiCheckVal)) {
                                                    rankDataList_threeSjt2.add(studentlevel2.getDengji());
                                                }
                                                if (null != claRankCheckVal && "T".equals(claRankCheckVal)) {
                                                    rankDataList_threeSjt2.add(Integer.valueOf(studentlevel2.getClassRanking()));
                                                }
                                                if (null != graRankCheckVal && "T".equals(graRankCheckVal)) {
                                                    rankDataList_threeSjt2.add(Integer.valueOf(studentlevel2.getGradeRanking()));
                                                }
                                                if (null != areaRankCheckVal && "T".equals(areaRankCheckVal)) {
                                                    rankDataList_threeSjt2.add(Integer.valueOf(studentlevel2.getAreaRanking()));
                                                }
                                                if ("T".equals(teacherCheckVal)) {
                                                    rankDataList_threeSjt2.add(studentlevel2.getExt6());
                                                }
                                                int rLen_data_threeSjt2 = rankDataList_threeSjt2.size();
                                                for (int p_data_threeSjt2 = 0; p_data_threeSjt2 < rLen_data_threeSjt2; p_data_threeSjt2++) {
                                                    Cell pcell22 = wSheet2_rowI1.createCell(len + subLen_data_threeSjt2 + rLen_data_threeSjt2 + 1 + (sjtCount * (subLen_data_threeSjt2 + rLen_data_threeSjt2)) + subLen_data_threeSjt2 + p_data_threeSjt2 + 1);
                                                    Cell pcell2_all2 = wSheet_rowI1.createCell(len + subLen_data_threeSjt2 + rLen_data_threeSjt2 + 1 + (sjtCount * (subLen_data_threeSjt2 + rLen_data_threeSjt2)) + subLen_data_threeSjt2 + p_data_threeSjt2 + 1);
                                                    if (rankDataList_threeSjt2.get(p_data_threeSjt2) instanceof String) {
                                                        pcell22.setCellValue(String.valueOf(rankDataList_threeSjt2.get(p_data_threeSjt2)));
                                                        pcell2_all2.setCellValue(String.valueOf(rankDataList_threeSjt2.get(p_data_threeSjt2)));
                                                    } else if (null == rankDataList_threeSjt2.get(p_data_threeSjt2)) {
                                                        pcell22.setCellValue("");
                                                        pcell2_all2.setCellValue("");
                                                    } else {
                                                        pcell22.setCellValue(Integer.valueOf(rankDataList_threeSjt2.get(p_data_threeSjt2).toString()).intValue());
                                                        pcell2_all2.setCellValue(Integer.valueOf(rankDataList_threeSjt2.get(p_data_threeSjt2).toString()).intValue());
                                                    }
                                                }
                                            }
                                            sjtCount++;
                                        }
                                        if (null != hashMap3 && hashMap3.size() > 0) {
                                            Studentlevel studentlevel3 = (Studentlevel) hashMap3.get(singleSubjectList.get(i6).getRealStudentId());
                                            if (null != studentlevel3) {
                                                if (i6 == 0 && "hideFufen".equals(studentlevel3.getExt5())) {
                                                    removeSubList.add(Integer.valueOf(16 + teacherColumn + (sjtCount * (7 + teacherColumn)) + 5));
                                                }
                                                double totalScore3 = 0.0d;
                                                if (studentlevel3.getTotalScore() != null) {
                                                    totalScore3 = studentlevel3.getTotalScore().doubleValue();
                                                }
                                                String fufen_ext54 = studentlevel3.getExt5();
                                                Object[] subData_threeSjt3 = {studentlevel3.getSubjectName(), Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel3.getSqts())).stripTrailingZeros().toPlainString()), Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel3.getOqts())).stripTrailingZeros().toPlainString()), Convert.toDouble(Convert.toBigDecimal(df012.format(totalScore3)).stripTrailingZeros().toPlainString())};
                                                if (!"hideFufen".equals(studentlevel3.getExt5())) {
                                                    Object[] objArr4 = new Object[5];
                                                    objArr4[0] = studentlevel3.getSubjectName();
                                                    objArr4[1] = Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel3.getSqts())).stripTrailingZeros().toPlainString());
                                                    objArr4[2] = Convert.toDouble(Convert.toBigDecimal(df012.format(studentlevel3.getOqts())).stripTrailingZeros().toPlainString());
                                                    objArr4[3] = Convert.toDouble(Convert.toBigDecimal(df012.format(totalScore3)).stripTrailingZeros().toPlainString());
                                                    objArr4[4] = fufen_ext54 == null ? "" : Convert.toDouble(Convert.toBigDecimal(df012.format(Convert.toDouble(fufen_ext54.toString()))).stripTrailingZeros().toPlainString());
                                                    subData_threeSjt3 = objArr4;
                                                }
                                                int subLen_data_threeSjt3 = subData_threeSjt3.length;
                                                for (int s_data_threeSjt3 = 0; s_data_threeSjt3 < subLen_data_threeSjt3; s_data_threeSjt3++) {
                                                    Cell cell24 = wSheet2_rowI1.createCell(len + subLen_data_threeSjt3 + rLen + 1 + (sjtCount * (subLen_data_threeSjt3 + rLen)) + s_data_threeSjt3 + 1);
                                                    Cell cell2_all3 = wSheet_rowI1.createCell(len + subLen_data_threeSjt3 + rLen + 1 + (sjtCount * (subLen_data_threeSjt3 + rLen)) + s_data_threeSjt3 + 1);
                                                    if (subData_threeSjt3[s_data_threeSjt3] instanceof String) {
                                                        cell24.setCellValue(String.valueOf(subData_threeSjt3[s_data_threeSjt3]));
                                                        cell2_all3.setCellValue(String.valueOf(subData_threeSjt3[s_data_threeSjt3]));
                                                    } else if (null == subData_threeSjt3[s_data_threeSjt3]) {
                                                        cell24.setCellValue("");
                                                        cell2_all3.setCellValue("");
                                                    } else {
                                                        cell24.setCellValue(Double.valueOf(subData_threeSjt3[s_data_threeSjt3].toString()).doubleValue());
                                                        cell2_all3.setCellValue(Double.valueOf(subData_threeSjt3[s_data_threeSjt3].toString()).doubleValue());
                                                    }
                                                }
                                                List<Object> rankDataList_threeSjt3 = new ArrayList<>();
                                                if ("T".equals(graDengjiCheckVal)) {
                                                    rankDataList_threeSjt3.add(studentlevel3.getDengjixiao());
                                                }
                                                if ("T".equals(areaDengjiCheckVal)) {
                                                    rankDataList_threeSjt3.add(studentlevel3.getDengji());
                                                }
                                                if (null != claRankCheckVal && "T".equals(claRankCheckVal)) {
                                                    rankDataList_threeSjt3.add(Integer.valueOf(studentlevel3.getClassRanking()));
                                                }
                                                if (null != graRankCheckVal && "T".equals(graRankCheckVal)) {
                                                    rankDataList_threeSjt3.add(Integer.valueOf(studentlevel3.getGradeRanking()));
                                                }
                                                if (null != areaRankCheckVal && "T".equals(areaRankCheckVal)) {
                                                    rankDataList_threeSjt3.add(Integer.valueOf(studentlevel3.getAreaRanking()));
                                                }
                                                if ("T".equals(teacherCheckVal)) {
                                                    rankDataList_threeSjt3.add(studentlevel3.getExt6());
                                                }
                                                int rLen_data_threeSjt3 = rankDataList_threeSjt3.size();
                                                for (int p_data_threeSjt3 = 0; p_data_threeSjt3 < rLen_data_threeSjt3; p_data_threeSjt3++) {
                                                    Cell pcell23 = wSheet2_rowI1.createCell(len + subLen_data_threeSjt3 + rLen_data_threeSjt3 + 1 + (sjtCount * (subLen_data_threeSjt3 + rLen_data_threeSjt3)) + subLen_data_threeSjt3 + p_data_threeSjt3 + 1);
                                                    Cell pcell2_all3 = wSheet_rowI1.createCell(len + subLen_data_threeSjt3 + rLen_data_threeSjt3 + 1 + (sjtCount * (subLen_data_threeSjt3 + rLen_data_threeSjt3)) + subLen_data_threeSjt3 + p_data_threeSjt3 + 1);
                                                    if (rankDataList_threeSjt3.get(p_data_threeSjt3) instanceof String) {
                                                        pcell23.setCellValue(String.valueOf(rankDataList_threeSjt3.get(p_data_threeSjt3)));
                                                        pcell2_all3.setCellValue(String.valueOf(rankDataList_threeSjt3.get(p_data_threeSjt3)));
                                                    } else if (null == rankDataList_threeSjt3.get(p_data_threeSjt3)) {
                                                        pcell23.setCellValue("");
                                                        pcell2_all3.setCellValue("");
                                                    } else {
                                                        pcell23.setCellValue(Integer.valueOf(rankDataList_threeSjt3.get(p_data_threeSjt3).toString()).intValue());
                                                        pcell2_all3.setCellValue(Integer.valueOf(rankDataList_threeSjt3.get(p_data_threeSjt3).toString()).intValue());
                                                    }
                                                }
                                            }
                                            int i7 = sjtCount + 1;
                                        }
                                    }
                                    ThreadUtil.sleep(5L);
                                }
                                preCount = singleSubjectList.size();
                                singleSubjectList.size();
                                d++;
                            }
                            FileOutputStream fileOut0 = new FileOutputStream(f0);
                            wwBook2.write(fileOut0);
                            fileOut0.flush();
                            wwBook2.dispose();
                            wwBook2.close();
                            fileOut0.close();
                        }
                        FileOutputStream fileOut_all = new FileOutputStream(f0_all);
                        wwBook.write(fileOut_all);
                        fileOut_all.flush();
                        wwBook.dispose();
                        wwBook.close();
                        fileOut_all.close();
                        xuankezuheSCell7.clear();
                        leibieSCell8.clear();
                    }
                }
            }
        } catch (Exception e) {
            this.log.info("生成【单科目成绩】失败：--" + loginUser + "--exam:[" + exam + "]--grade:[" + grade + "]--subject:[" + subject + "]--studentType:[" + studentType + "]--subCompose:[" + subCompose + "]--fufen:[" + fufen + "]--" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS").format(new Date()) + e);
            e.printStackTrace();
        }
    }

    public void writeAllScoreSheet(Map<String, String> map, String xuanDirFolder) {
        String studentID;
        String zongfen;
        try {
            String loginUser = map.get("loginUser");
            String exam = map.get(Const.EXPORTREPORT_examNum);
            String grade = map.get(Const.EXPORTREPORT_gradeNum);
            String subject = map.get(Const.EXPORTREPORT_subjectNum);
            String resourceType = map.get("resourceType");
            if ("-1".equals(subject)) {
                subject = map.get("sjt");
            }
            String fufen = map.get("fufen");
            String studentType = map.get(Const.EXPORTREPORT_studentType);
            String subCompose = map.get("xuankezuhe");
            String isJointStuType = map.get(Const.isJointStuType);
            String viewRankInfo = map.get(Const.viewRankInfo);
            String claRankCheckVal = map.get("claRankCheckVal");
            String graRankCheckVal = map.get("graRankCheckVal");
            String areaRankCheckVal = map.get("areaRankCheckVal");
            String dispStuId = map.get("dispStuId");
            String teacherCheckVal = map.get("teacherCheckVal");
            new ArrayList();
            List<Object[]> classStuAndSubScoreList = this.examManageService.getClassStuAndSub_new(exam, grade, "-1", "-1", studentType, "0", "0", viewRankInfo, isJointStuType, fufen, subCompose);
            String studentnumStr = (null == dispStuId || !dispStuId.equals("T")) ? "考号" : "ID号";
            WritableCellFormat cFormatCent3 = null;
            WritableCellFormat cFormat3 = null;
            WritableCellFormat cFormatRank3 = null;
            WritableWorkbook wwBook2_allSch = null;
            WritableSheet wSheet2_allSch = null;
            WorkbookSettings wbs = new WorkbookSettings();
            wbs.setGCDisabled(true);
            String isMoreSchool = this.analy.getExamIsMoreSchool(exam);
            boolean moreSchoolF = false;
            if (isMoreSchool != null && isMoreSchool.equals("T") && getExportMoreSchType(Const.MoreSchool, map)) {
                moreSchoolF = true;
                String fileName = fufen + "_" + resourceType + "_" + exam + "_" + grade + "_-8_" + studentType + "_" + subCompose + "_allschool";
                String schoolDirFolder = xuanDirFolder + fileName + ".xls";
                createFile(schoolDirFolder);
                File file = new File(schoolDirFolder);
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    } else {
                        file.delete();
                        file.createNewFile();
                    }
                    wwBook2_allSch = Workbook.createWorkbook(file, wbs);
                    wSheet2_allSch = wwBook2_allSch.createSheet("全区", 0);
                    WritableFont fontCent3 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                    cFormatCent3 = new WritableCellFormat(fontCent3);
                    cFormatCent3.setAlignment(Alignment.CENTRE);
                    NumberFormat nf23 = new NumberFormat("0.00");
                    WritableFont font3 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                    cFormat3 = new WritableCellFormat(font3, nf23);
                    cFormat3.setAlignment(Alignment.CENTRE);
                    WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                    cFormatRank3 = new WritableCellFormat(font);
                    cFormatRank3.setAlignment(Alignment.CENTRE);
                    WritableFont fontIllegal3 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                    WritableCellFormat cFormatIllegal3 = new WritableCellFormat(fontIllegal3);
                    cFormatIllegal3.setAlignment(Alignment.CENTRE);
                } catch (Exception e) {
                    this.log.info("生成表格createExcel失败。", e);
                }
            }
            if (null != classStuAndSubScoreList && classStuAndSubScoreList.size() > 0) {
                Set xuankezuheQCell6 = new HashSet();
                Set leibieQCell7 = new HashSet();
                Set xuankezuheSCell6 = new HashSet();
                Set leibieSCell7 = new HashSet();
                List<Integer> removeSubList = new ArrayList<>();
                List<String> rankList = new ArrayList<>();
                if (null != claRankCheckVal && "T".equals(claRankCheckVal)) {
                    rankList.add("班");
                }
                if (null != graRankCheckVal && "T".equals(graRankCheckVal)) {
                    rankList.add("校");
                }
                if (null != areaRankCheckVal && "T".equals(areaRankCheckVal)) {
                    rankList.add("区");
                }
                if ("T".equals(teacherCheckVal)) {
                    rankList.add("任课教师");
                }
                int rLen = rankList.size();
                String curSchool = "";
                WritableCellFormat cFormatCent2 = null;
                WritableCellFormat cFormat2 = null;
                WritableCellFormat cFormatRank2 = null;
                WritableWorkbook wwBook2 = null;
                WritableSheet wSheet2 = null;
                int curIndex2 = 0;
                for (int a = 0; a < classStuAndSubScoreList.size(); a++) {
                    Object[] _datas = classStuAndSubScoreList.get(a);
                    Object _subjectNum = _datas[10];
                    String[] _subjectNums = String.valueOf(_subjectNum).split(Const.STRING_SEPERATOR);
                    if (a == 0 && moreSchoolF) {
                        Label stuInfor = new Label(0, 0, "学生信息", cFormatCent3);
                        wSheet2_allSch.addCell(stuInfor);
                        wSheet2_allSch.mergeCells(0, 0, 7, 0);
                        String[] biaotou = {"序号", "学校", "班级", studentnumStr, "学号", "姓名", "选科组合", "类别"};
                        int len = biaotou.length;
                        for (int i = 0; i < len; i++) {
                            Label cell = new Label(i, 1, biaotou[i]);
                            wSheet2_allSch.addCell(cell);
                        }
                        Object g_subjectName = classStuAndSubScoreList.get(0)[11];
                        if (null != g_subjectName) {
                            String[] subjectNames = g_subjectName.toString().split(Const.STRING_SEPERATOR);
                            for (int i2 = 0; i2 < subjectNames.length; i2++) {
                                Label km = new Label(i2 + len + (i2 * rLen), 1, subjectNames[i2], cFormat3);
                                wSheet2_allSch.addCell(km);
                                for (int p = 0; p < rankList.size(); p++) {
                                    Label pcell = new Label(i2 + len + (i2 * rLen) + p + 1, 1, rankList.get(p), cFormat3);
                                    wSheet2_allSch.addCell(pcell);
                                }
                                if (!"T".equals(teacherCheckVal) || rLen != 1) {
                                    Label pMsg = new Label(i2 + len + (i2 * rLen) + 1, 0, "排名", cFormatCent3);
                                    wSheet2_allSch.addCell(pMsg);
                                    if ("T".equals(teacherCheckVal) && rLen > 2) {
                                        wSheet2_allSch.mergeCells(i2 + len + (i2 * rLen) + 1, 0, (((i2 + len) + (i2 * rLen)) + rLen) - 1, 0);
                                    } else if (!"T".equals(teacherCheckVal) && rLen > 1) {
                                        wSheet2_allSch.mergeCells(i2 + len + (i2 * rLen) + 1, 0, i2 + len + (i2 * rLen) + rLen, 0);
                                    }
                                }
                            }
                        }
                    }
                    String schNum = String.valueOf(_datas[8]);
                    if (!curSchool.equals(schNum)) {
                        curSchool = schNum;
                        curIndex2 = 0;
                        if (null != wwBook2) {
                            removeExcelColumn(wSheet2, removeSubList, rLen, leibieSCell7, xuankezuheSCell6);
                            wwBook2.write();
                            wwBook2.close();
                            leibieSCell7.clear();
                            xuankezuheSCell6.clear();
                        }
                        String fileName2 = fufen + "_" + resourceType + "_" + exam + "_" + grade + "_-8_" + studentType + "_" + subCompose + "_" + curSchool;
                        String schoolDirFolder2 = xuanDirFolder + fileName2 + ".xls";
                        createFile(schoolDirFolder2);
                        try {
                            File f1 = new File(schoolDirFolder2);
                            if (!f1.exists()) {
                                f1.createNewFile();
                            } else {
                                f1.delete();
                                f1.createNewFile();
                            }
                            wwBook2 = Workbook.createWorkbook(f1, wbs);
                            wSheet2 = wwBook2.createSheet(curSchool, 0);
                            WritableFont fontCent2 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                            cFormatCent2 = new WritableCellFormat(fontCent2);
                            cFormatCent2.setAlignment(Alignment.CENTRE);
                            NumberFormat nf232 = new NumberFormat("0.00");
                            WritableFont font2 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                            cFormat2 = new WritableCellFormat(font2, nf232);
                            cFormat2.setAlignment(Alignment.CENTRE);
                            WritableFont font4 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                            cFormatRank2 = new WritableCellFormat(font4);
                            cFormatRank2.setAlignment(Alignment.CENTRE);
                            WritableFont fontIllegal2 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                            WritableCellFormat cFormatIllegal2 = new WritableCellFormat(fontIllegal2);
                            cFormatIllegal2.setAlignment(Alignment.CENTRE);
                        } catch (Exception e2) {
                            this.log.info("生成表格createExcel失败。", e2);
                        }
                        Label stuInfor2 = new Label(0, 0, "学生信息", cFormatCent2);
                        wSheet2.addCell(stuInfor2);
                        wSheet2.mergeCells(0, 0, 7, 0);
                        String[] biaotou2 = {"序号", "学校", "班级", studentnumStr, "学号", "姓名", "选科组合", "类别"};
                        int len2 = biaotou2.length;
                        for (int i3 = 0; i3 < len2; i3++) {
                            Label cell2 = new Label(i3, 1, biaotou2[i3]);
                            wSheet2.addCell(cell2);
                        }
                        Object g_subjectName2 = classStuAndSubScoreList.get(0)[11];
                        Object g_p_fufen = classStuAndSubScoreList.get(0)[22];
                        if (null != g_subjectName2) {
                            String[] subjectNames2 = g_subjectName2.toString().split(Const.STRING_SEPERATOR);
                            for (int i4 = 0; i4 < subjectNames2.length; i4++) {
                                Label km2 = new Label(i4 + len2 + (i4 * rLen), 1, subjectNames2[i4], cFormat2);
                                wSheet2.addCell(km2);
                                if (rLen > 0) {
                                    if (a == 0) {
                                        String[] p_fufens = g_p_fufen.toString().split(Const.STRING_SEPERATOR);
                                        if ("1".equals(p_fufens[i4])) {
                                            removeSubList.add(Integer.valueOf(((i4 + len2) + ((i4 - 1) * rLen)) - 1));
                                        }
                                    }
                                    for (int p2 = 0; p2 < rankList.size(); p2++) {
                                        Label pcell2 = new Label(i4 + len2 + (i4 * rLen) + p2 + 1, 1, rankList.get(p2), cFormat2);
                                        wSheet2.addCell(pcell2);
                                    }
                                    if (!"T".equals(teacherCheckVal) || rLen != 1) {
                                        Label pMsg2 = new Label(i4 + len2 + (i4 * rLen) + 1, 0, "排名", cFormatCent2);
                                        wSheet2.addCell(pMsg2);
                                        if ("T".equals(teacherCheckVal) && rLen > 2) {
                                            wSheet2.mergeCells(i4 + len2 + (i4 * rLen) + 1, 0, (((i4 + len2) + (i4 * rLen)) + rLen) - 1, 0);
                                        } else if (!"T".equals(teacherCheckVal) && rLen > 1) {
                                            wSheet2.mergeCells(i4 + len2 + (i4 * rLen) + 1, 0, i4 + len2 + (i4 * rLen) + rLen, 0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    try {
                        xuankezuheQCell6.add(String.valueOf(_datas[19]));
                        xuankezuheSCell6.add(String.valueOf(_datas[19]));
                        leibieQCell7.add(String.valueOf(_datas[4]));
                        leibieSCell7.add(String.valueOf(_datas[4]));
                        if (null != dispStuId && dispStuId.equals("T")) {
                            studentID = String.valueOf(_datas[6]);
                        } else {
                            studentID = String.valueOf(_datas[5]);
                        }
                        String studentnum1 = String.valueOf(_datas[2]);
                        if (null == studentnum1 || "null".equals(studentnum1)) {
                            studentnum1 = "";
                        }
                        Number qCell = new Number(0, a + 2, a + 1);
                        Number sCell2 = new Number(0, curIndex2 + 2, curIndex2 + 1);
                        wSheet2.addCell(sCell2);
                        if (moreSchoolF) {
                            wSheet2_allSch.addCell(qCell);
                        }
                        String[] sdata = {String.valueOf(_datas[0]), String.valueOf(_datas[1]), studentID, studentnum1, String.valueOf(_datas[3]), String.valueOf(_datas[20]), String.valueOf(_datas[21])};
                        for (int j = 0; j < sdata.length; j++) {
                            Label dataCell = new Label(j + 1, a + 2, sdata[j]);
                            Label dataCell2 = new Label(j + 1, curIndex2 + 2, sdata[j]);
                            wSheet2.addCell(dataCell2);
                            if (moreSchoolF) {
                                wSheet2_allSch.addCell(dataCell);
                            }
                        }
                        int basicLen = sdata.length + 1;
                        Object biaozhunfenT = _datas[14];
                        if (null != biaozhunfenT) {
                            String[] zongfens = biaozhunfenT.toString().split(Const.STRING_SEPERATOR);
                            for (int z = 0; z < zongfens.length; z++) {
                                String zongfen2 = zongfens[z];
                                if ("--".equals(zongfen2)) {
                                    Object _illType = _datas[12];
                                    String[] _illTypes = _illType.toString().split(Const.STRING_SEPERATOR);
                                    if ("3".equals(_illTypes[z]) || "4".equals(_illTypes[z])) {
                                        zongfen = "[零分]";
                                    } else {
                                        zongfen = "[缺考]";
                                        Object _en = _datas[13];
                                        String[] _ens = _en.toString().split(Const.STRING_SEPERATOR);
                                        if (_subjectNums[z].length() >= 3) {
                                            if ("--".equals(_ens[z])) {
                                                zongfen = "--";
                                            } else if ("1".equals(_illTypes[z])) {
                                                zongfen = "[违纪]";
                                            }
                                        }
                                    }
                                    Label zongf = new Label(z + basicLen + (z * rLen), a + 2, zongfen, cFormat3);
                                    Label zongf2 = new Label(z + basicLen + (z * rLen), curIndex2 + 2, zongfen, cFormat2);
                                    wSheet2.addCell(zongf2);
                                    if (moreSchoolF) {
                                        wSheet2_allSch.addCell(zongf);
                                    }
                                } else {
                                    Number zongf3 = new Number(z + basicLen + (z * rLen), a + 2, Double.valueOf(zongfen2).doubleValue(), cFormat3);
                                    Number zongf22 = new Number(z + basicLen + (z * rLen), curIndex2 + 2, Double.valueOf(zongfen2).doubleValue(), cFormat2);
                                    wSheet2.addCell(zongf22);
                                    if (moreSchoolF) {
                                        wSheet2_allSch.addCell(zongf3);
                                    }
                                }
                                for (int rr = 0; rr < rLen; rr++) {
                                    int m2 = 16;
                                    if ("校".equals(rankList.get(rr))) {
                                        m2 = 17;
                                    } else if ("区".equals(rankList.get(rr))) {
                                        m2 = 18;
                                    } else if ("任课教师".equals(rankList.get(rr))) {
                                        Object g_subjectNum = _datas[10];
                                        String[] subjectNums = g_subjectNum.toString().split(Const.STRING_SEPERATOR);
                                        String currentSubjectNum = subjectNums[z];
                                        if (currentSubjectNum.length() >= 3) {
                                            String teacher = String.valueOf(_datas[23]).split(Const.STRING_SEPERATOR)[z];
                                            Label renkejiaoshi = new Label(z + basicLen + 1 + (z * rLen) + rr, a + 2, teacher, cFormatRank3);
                                            Label renkejiaoshi2 = new Label(z + basicLen + 1 + (z * rLen) + rr, curIndex2 + 2, teacher, cFormatRank2);
                                            wSheet2.addCell(renkejiaoshi2);
                                            if (moreSchoolF) {
                                                wSheet2_allSch.addCell(renkejiaoshi);
                                            }
                                        } else {
                                            Label banzhuren = new Label(z + basicLen + 1 + (z * rLen) + rr, a + 2, String.valueOf(_datas[24]), cFormatRank3);
                                            Label banzhuren2 = new Label(z + basicLen + 1 + (z * rLen) + rr, curIndex2 + 2, String.valueOf(_datas[24]), cFormatRank2);
                                            wSheet2.addCell(banzhuren2);
                                            if (moreSchoolF) {
                                                wSheet2_allSch.addCell(banzhuren);
                                            }
                                            if (a == 0) {
                                                Label banzhurenMsg = new Label(z + basicLen + 1 + (z * rLen) + rr, 1, "班主任", cFormat3);
                                                wSheet2_allSch.addCell(banzhurenMsg);
                                            }
                                            if (curIndex2 == 0) {
                                                Label banzhuren2Msg = new Label(z + basicLen + 1 + (z * rLen) + rr, 1, "班主任", cFormat2);
                                                wSheet2.addCell(banzhuren2Msg);
                                            }
                                        }
                                    }
                                    Object cRanks = _datas[m2];
                                    String cRank = cRanks.toString().split(Const.STRING_SEPERATOR)[z];
                                    if (null != cRank && !"--".equals(cRank)) {
                                        Number banjiRank = new Number(z + basicLen + 1 + (z * rLen) + rr, a + 2, Integer.valueOf(cRank).intValue(), cFormatRank3);
                                        Number banjiRank2 = new Number(z + basicLen + 1 + (z * rLen) + rr, curIndex2 + 2, Integer.valueOf(cRank).intValue(), cFormatRank2);
                                        wSheet2.addCell(banjiRank2);
                                        if (moreSchoolF) {
                                            wSheet2_allSch.addCell(banjiRank);
                                        }
                                    }
                                }
                            }
                        }
                        curIndex2++;
                        if (a == classStuAndSubScoreList.size() - 1) {
                            removeExcelColumn(wSheet2, removeSubList, rLen, leibieSCell7, xuankezuheSCell6);
                            wwBook2.write();
                            wwBook2.close();
                            if (moreSchoolF) {
                                removeExcelColumn(wSheet2_allSch, removeSubList, rLen, leibieQCell7, xuankezuheQCell6);
                                wwBook2_allSch.write();
                                wwBook2_allSch.close();
                            }
                        }
                    } catch (Exception e3) {
                        this.log.info("生成【总科目成绩】失败：--" + loginUser + "--exam:[" + exam + "]--grade:[" + grade + "]--subject:[" + subject + "]--studentType:[" + studentType + "]--subCompose:[" + subCompose + "]--fufen:[" + fufen + "]--" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS").format(new Date()) + e3);
                        e3.printStackTrace();
                    }
                }
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:231:0x0ea5 A[Catch: Exception -> 0x1732, Exception -> 0x17c8, TryCatch #2 {Exception -> 0x1732, blocks: (B:387:0x0e32, B:389:0x0e3c, B:225:0x0e52, B:227:0x0e61, B:229:0x0e70, B:231:0x0ea5, B:234:0x0ec4, B:236:0x0ece, B:237:0x0f8b, B:239:0x0f95, B:243:0x0fa5, B:245:0x0faf, B:247:0x11b9, B:249:0x11c1, B:251:0x11df, B:253:0x11f8, B:256:0x11fe, B:258:0x1212, B:259:0x1224, B:261:0x122c, B:263:0x123e, B:265:0x125f, B:267:0x1275, B:269:0x1299, B:272:0x12af, B:275:0x12c1, B:277:0x12ef, B:279:0x13ca, B:281:0x13d4, B:283:0x13e3, B:285:0x13f0, B:287:0x16c5, B:291:0x1404, B:294:0x15f5, B:296:0x1611, B:298:0x161c, B:302:0x1652, B:303:0x1662, B:305:0x1670, B:309:0x16a6, B:310:0x16b6, B:313:0x169a, B:315:0x1646, B:312:0x16bf, B:318:0x1421, B:321:0x143a, B:324:0x1453, B:327:0x146c, B:329:0x147e, B:331:0x14a1, B:333:0x14e6, B:336:0x1517, B:338:0x1550, B:341:0x1589, B:344:0x15bd, B:349:0x131b, B:351:0x134d, B:352:0x136f, B:354:0x137d, B:356:0x139f, B:357:0x13c1, B:358:0x13b1, B:359:0x135f, B:361:0x16cb, B:363:0x16dc, B:365:0x1708, B:369:0x0ffd, B:370:0x103f, B:374:0x104f, B:376:0x1059, B:377:0x10b3, B:380:0x1107, B:382:0x1111, B:383:0x116b, B:384:0x0f34, B:224:0x0e49), top: B:386:0x0e32, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:249:0x11c1 A[Catch: Exception -> 0x1732, Exception -> 0x17c8, TryCatch #2 {Exception -> 0x1732, blocks: (B:387:0x0e32, B:389:0x0e3c, B:225:0x0e52, B:227:0x0e61, B:229:0x0e70, B:231:0x0ea5, B:234:0x0ec4, B:236:0x0ece, B:237:0x0f8b, B:239:0x0f95, B:243:0x0fa5, B:245:0x0faf, B:247:0x11b9, B:249:0x11c1, B:251:0x11df, B:253:0x11f8, B:256:0x11fe, B:258:0x1212, B:259:0x1224, B:261:0x122c, B:263:0x123e, B:265:0x125f, B:267:0x1275, B:269:0x1299, B:272:0x12af, B:275:0x12c1, B:277:0x12ef, B:279:0x13ca, B:281:0x13d4, B:283:0x13e3, B:285:0x13f0, B:287:0x16c5, B:291:0x1404, B:294:0x15f5, B:296:0x1611, B:298:0x161c, B:302:0x1652, B:303:0x1662, B:305:0x1670, B:309:0x16a6, B:310:0x16b6, B:313:0x169a, B:315:0x1646, B:312:0x16bf, B:318:0x1421, B:321:0x143a, B:324:0x1453, B:327:0x146c, B:329:0x147e, B:331:0x14a1, B:333:0x14e6, B:336:0x1517, B:338:0x1550, B:341:0x1589, B:344:0x15bd, B:349:0x131b, B:351:0x134d, B:352:0x136f, B:354:0x137d, B:356:0x139f, B:357:0x13c1, B:358:0x13b1, B:359:0x135f, B:361:0x16cb, B:363:0x16dc, B:365:0x1708, B:369:0x0ffd, B:370:0x103f, B:374:0x104f, B:376:0x1059, B:377:0x10b3, B:380:0x1107, B:382:0x1111, B:383:0x116b, B:384:0x0f34, B:224:0x0e49), top: B:386:0x0e32, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:258:0x1212 A[Catch: Exception -> 0x1732, Exception -> 0x17c8, TryCatch #2 {Exception -> 0x1732, blocks: (B:387:0x0e32, B:389:0x0e3c, B:225:0x0e52, B:227:0x0e61, B:229:0x0e70, B:231:0x0ea5, B:234:0x0ec4, B:236:0x0ece, B:237:0x0f8b, B:239:0x0f95, B:243:0x0fa5, B:245:0x0faf, B:247:0x11b9, B:249:0x11c1, B:251:0x11df, B:253:0x11f8, B:256:0x11fe, B:258:0x1212, B:259:0x1224, B:261:0x122c, B:263:0x123e, B:265:0x125f, B:267:0x1275, B:269:0x1299, B:272:0x12af, B:275:0x12c1, B:277:0x12ef, B:279:0x13ca, B:281:0x13d4, B:283:0x13e3, B:285:0x13f0, B:287:0x16c5, B:291:0x1404, B:294:0x15f5, B:296:0x1611, B:298:0x161c, B:302:0x1652, B:303:0x1662, B:305:0x1670, B:309:0x16a6, B:310:0x16b6, B:313:0x169a, B:315:0x1646, B:312:0x16bf, B:318:0x1421, B:321:0x143a, B:324:0x1453, B:327:0x146c, B:329:0x147e, B:331:0x14a1, B:333:0x14e6, B:336:0x1517, B:338:0x1550, B:341:0x1589, B:344:0x15bd, B:349:0x131b, B:351:0x134d, B:352:0x136f, B:354:0x137d, B:356:0x139f, B:357:0x13c1, B:358:0x13b1, B:359:0x135f, B:361:0x16cb, B:363:0x16dc, B:365:0x1708, B:369:0x0ffd, B:370:0x103f, B:374:0x104f, B:376:0x1059, B:377:0x10b3, B:380:0x1107, B:382:0x1111, B:383:0x116b, B:384:0x0f34, B:224:0x0e49), top: B:386:0x0e32, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:363:0x16dc A[Catch: Exception -> 0x1732, Exception -> 0x17c8, TryCatch #2 {Exception -> 0x1732, blocks: (B:387:0x0e32, B:389:0x0e3c, B:225:0x0e52, B:227:0x0e61, B:229:0x0e70, B:231:0x0ea5, B:234:0x0ec4, B:236:0x0ece, B:237:0x0f8b, B:239:0x0f95, B:243:0x0fa5, B:245:0x0faf, B:247:0x11b9, B:249:0x11c1, B:251:0x11df, B:253:0x11f8, B:256:0x11fe, B:258:0x1212, B:259:0x1224, B:261:0x122c, B:263:0x123e, B:265:0x125f, B:267:0x1275, B:269:0x1299, B:272:0x12af, B:275:0x12c1, B:277:0x12ef, B:279:0x13ca, B:281:0x13d4, B:283:0x13e3, B:285:0x13f0, B:287:0x16c5, B:291:0x1404, B:294:0x15f5, B:296:0x1611, B:298:0x161c, B:302:0x1652, B:303:0x1662, B:305:0x1670, B:309:0x16a6, B:310:0x16b6, B:313:0x169a, B:315:0x1646, B:312:0x16bf, B:318:0x1421, B:321:0x143a, B:324:0x1453, B:327:0x146c, B:329:0x147e, B:331:0x14a1, B:333:0x14e6, B:336:0x1517, B:338:0x1550, B:341:0x1589, B:344:0x15bd, B:349:0x131b, B:351:0x134d, B:352:0x136f, B:354:0x137d, B:356:0x139f, B:357:0x13c1, B:358:0x13b1, B:359:0x135f, B:361:0x16cb, B:363:0x16dc, B:365:0x1708, B:369:0x0ffd, B:370:0x103f, B:374:0x104f, B:376:0x1059, B:377:0x10b3, B:380:0x1107, B:382:0x1111, B:383:0x116b, B:384:0x0f34, B:224:0x0e49), top: B:386:0x0e32, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:372:0x1049  */
    /* JADX WARN: Removed duplicated region for block: B:378:0x1101  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void writeAllScoreSheet_xlsx(java.util.Map<java.lang.String, java.lang.String> r14, java.lang.String r15) {
        /*
            Method dump skipped, instructions count: 6094
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.dmj.serviceimpl.examManagement.ExportServiceImpl.writeAllScoreSheet_xlsx(java.util.Map, java.lang.String):void");
    }

    public void writeSubjectDetailSheet(Map<String, String> map, String xuanDirFolder) {
        String loginUser = map.get("loginUser");
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        if ("-1".equals(subject)) {
            subject = map.get("sjt");
        }
        String fufen = map.get("fufen");
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String subCompose = map.get("xuankezuhe");
        String isJointStuType = map.get(Const.isJointStuType);
        String resourceType = map.get("resourceType");
        String school = "";
        List schoolList = this.analy.getSchool(exam, subject, studentType, grade, fufen);
        for (int i = 0; i < schoolList.size(); i++) {
            AjaxData ajaxData = schoolList.get(i);
            school = school + Const.STRING_SEPERATOR + String.valueOf(ajaxData.getNum());
        }
        List list = this.examManageService.getScoreDetail(exam, subject, Const.class_grade, grade, school.substring(1, school.length()), studentType, "1", isJointStuType, subCompose);
        if (null == list || list.size() <= 0) {
            return;
        }
        Object[] obj1 = (Object[]) list.get(0);
        if (null == obj1[9] || "null".equals(obj1[9]) || "".equals(obj1[9])) {
            Map args = new HashMap();
            args.put("exam", exam);
            args.put("grade", grade);
            args.put("subject", subject);
            args.put("fufen", fufen);
            args.put("resourceType", resourceType);
            this.dao2._execute("update managefile set status=1 where examNum={exam} and gradeNum={grade} and subjectNum={subject} and source={fufen} and resourceType={resourceType} ", args);
        }
        try {
            if (list.size() > 0 && null != obj1[9] && !"null".equals(obj1[9]) && !"".equals(obj1[9])) {
                String[] biaotou = {"序号", "学校名称*", "考号", "ID号*", "学号*", "班级*", "姓名", "选科组合", "类别", "备注", "总分", "客观题选项*"};
                int len = biaotou.length;
                Set xuankezuheQCell7 = new HashSet();
                Set leibieQCell8 = new HashSet();
                Set xuankezuheCCell7 = new HashSet();
                Set leibieCCell8 = new HashSet();
                Object[] obj0 = (Object[]) list.get(0);
                String schoolname = String.valueOf(obj0[0]);
                Map args1 = new HashMap();
                args1.put("schoolname", schoolname);
                String curSchool = this.dao2._queryStr("select id from school where schoolName={schoolname} ", args1);
                String[] questionNums = null;
                if (null != obj0[9] && !"null".equals(obj0[9]) && !"".equals(obj0[9])) {
                    questionNums = obj0[9].toString().split(Const.STRING_SEPERATOR);
                }
                String[] qTypes = null;
                if (obj0[12] != null) {
                    qTypes = obj0[12].toString().split(Const.STRING_SEPERATOR);
                }
                String fileName = fufen + "_" + resourceType + "_" + exam + "_" + grade + "_" + subject + "_" + studentType + "_" + subCompose + "_allschool";
                String schoolDirFolder = xuanDirFolder + fileName + ".xls";
                createFile(schoolDirFolder);
                File file = new File(schoolDirFolder);
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    file.delete();
                    file.createNewFile();
                }
                WritableWorkbook wwBook2_allSch = Workbook.createWorkbook(file);
                WritableSheet wSheet2_allSch = wwBook2_allSch.createSheet("全区", 0);
                WritableWorkbook wwBook = null;
                WritableSheet sheet = null;
                int jj = 0;
                for (int j = 0; j < list.size(); j++) {
                    Object[] obj = (Object[]) list.get(j);
                    if (null != obj[9] && !"null".equals(obj[9]) && !"".equals(obj[9])) {
                        String[] scores = String.valueOf(obj[10]).split(Const.STRING_SEPERATOR);
                        String[] qTypeScores = null;
                        if (obj[13] != null) {
                            qTypeScores = String.valueOf(obj[13]).split(Const.STRING_SEPERATOR);
                        }
                        if (j == 0) {
                            for (int i2 = 0; i2 < len; i2++) {
                                Label cell = new Label(i2, 0, biaotou[i2]);
                                wSheet2_allSch.addCell(cell);
                            }
                            for (int i3 = 0; i3 < questionNums.length; i3++) {
                                Label queNum = new Label(i3 + len, 0, "T" + questionNums[i3]);
                                wSheet2_allSch.addCell(queNum);
                            }
                            if (qTypes != null) {
                                for (int i4 = 0; i4 < qTypes.length; i4++) {
                                    Label qType = new Label(questionNums.length + len + i4, 0, qTypes[i4] == null ? " " : String.valueOf(qTypes[i4]));
                                    wSheet2_allSch.addCell(qType);
                                }
                            }
                        }
                        if (!String.valueOf(obj[0]).equals(schoolname)) {
                            if (null != wwBook) {
                                removeDetailExcelColumn(sheet, leibieCCell8, 8, xuankezuheCCell7, 7);
                                wwBook.write();
                                wwBook.close();
                                leibieCCell8.clear();
                                xuankezuheCCell7.clear();
                            }
                            wwBook = null;
                            sheet = null;
                            jj = 0;
                            schoolname = String.valueOf(obj[0]);
                            Map args2 = new HashMap();
                            args2.put("schoolname", schoolname);
                            curSchool = this.dao2._queryStr("select id from school where schoolName={schoolname} ", args2);
                        }
                        if (jj == 0) {
                            String fileName1 = fufen + "_" + resourceType + "_" + exam + "_" + grade + "_" + subject + "_" + studentType + "_" + subCompose + "_" + curSchool;
                            String schoolDirFolder1 = xuanDirFolder + fileName1 + ".xls";
                            createFile(schoolDirFolder1);
                            File excelFile = new File(schoolDirFolder1);
                            if (!excelFile.exists()) {
                                excelFile.createNewFile();
                            } else {
                                excelFile.delete();
                                excelFile.createNewFile();
                            }
                            wwBook = Workbook.createWorkbook(excelFile);
                            sheet = wwBook.createSheet("科目详情", 0);
                            for (int i5 = 0; i5 < len; i5++) {
                                Label cell2 = new Label(i5, 0, biaotou[i5]);
                                sheet.addCell(cell2);
                            }
                            for (int i6 = 0; i6 < questionNums.length; i6++) {
                                Label queNum2 = new Label(i6 + len, 0, "T" + questionNums[i6]);
                                sheet.addCell(queNum2);
                            }
                            if (qTypes != null) {
                                for (int i7 = 0; i7 < qTypes.length; i7++) {
                                    Label qType2 = new Label(questionNums.length + len + i7, 0, qTypes[i7] == null ? " " : String.valueOf(qTypes[i7]));
                                    sheet.addCell(qType2);
                                }
                            }
                        }
                        Number number = new Number(0, jj + 1, jj + 1);
                        sheet.addCell(number);
                        xuankezuheQCell7.add(String.valueOf(obj[14]));
                        xuankezuheCCell7.add(String.valueOf(obj[14]));
                        leibieQCell8.add(String.valueOf(obj[15]));
                        leibieCCell8.add(String.valueOf(obj[15]));
                        Object[] biaotouData = {obj[0], obj[1], obj[2], obj[3], obj[4], obj[5], obj[14], obj[15], obj[6], obj[7], obj[8]};
                        for (int k = 0; k < len - 1; k++) {
                            if (k == 8) {
                                if ("0".equals(String.valueOf(biaotouData[k])) || "5".equals(String.valueOf(biaotouData[k]))) {
                                    Label base = new Label(k + 1, jj + 1, "【缺考】");
                                    sheet.addCell(base);
                                } else if ("1".equals(String.valueOf(biaotouData[k]))) {
                                    Label base2 = new Label(k + 1, jj + 1, "【违纪】");
                                    sheet.addCell(base2);
                                } else if ("3".equals(String.valueOf(biaotouData[k])) || "4".equals(String.valueOf(biaotouData[k]))) {
                                    Label base3 = new Label(k + 1, jj + 1, "【零分】");
                                    sheet.addCell(base3);
                                } else {
                                    Label base4 = new Label(k + 1, jj + 1, "");
                                    sheet.addCell(base4);
                                }
                            } else {
                                Label base5 = new Label(k + 1, jj + 1, biaotouData[k] == null ? " " : String.valueOf(biaotouData[k]));
                                sheet.addCell(base5);
                            }
                        }
                        for (int z = 0; z < scores.length; z++) {
                            if (scores[z] == null || "".equals(scores[z])) {
                                Blank score = new Blank(z + len, jj + 1);
                                sheet.addCell(score);
                            } else {
                                Label score2 = new Label(z + len, jj + 1, scores[z] == null ? "" : scores[z].toString());
                                sheet.addCell(score2);
                            }
                        }
                        if (qTypeScores != null) {
                            for (int z2 = 0; z2 < qTypeScores.length; z2++) {
                                Label qTypeScore = new Label(questionNums.length + len + z2, jj + 1, qTypeScores[z2] == null ? "" : String.valueOf(qTypeScores[z2]));
                                sheet.addCell(qTypeScore);
                            }
                        }
                        jj++;
                        Number allSchNumber = new Number(0, j + 1, j + 1);
                        wSheet2_allSch.addCell(allSchNumber);
                        for (int k2 = 0; k2 < len - 1; k2++) {
                            if (k2 == 8) {
                                if ("0".equals(String.valueOf(biaotouData[k2])) || "5".equals(String.valueOf(biaotouData[k2]))) {
                                    Label base6 = new Label(k2 + 1, j + 1, "【缺考】");
                                    wSheet2_allSch.addCell(base6);
                                } else if ("1".equals(String.valueOf(biaotouData[k2]))) {
                                    Label base7 = new Label(k2 + 1, j + 1, "【违纪】");
                                    wSheet2_allSch.addCell(base7);
                                } else if ("3".equals(String.valueOf(biaotouData[k2])) || "4".equals(String.valueOf(biaotouData[k2]))) {
                                    Label base8 = new Label(k2 + 1, j + 1, "【零分】");
                                    wSheet2_allSch.addCell(base8);
                                } else {
                                    Label base9 = new Label(k2 + 1, j + 1, "");
                                    wSheet2_allSch.addCell(base9);
                                }
                            } else {
                                Label base10 = new Label(k2 + 1, j + 1, biaotouData[k2] == null ? " " : String.valueOf(biaotouData[k2]));
                                wSheet2_allSch.addCell(base10);
                            }
                        }
                        for (int z3 = 0; z3 < scores.length; z3++) {
                            if (scores[z3] == null || "".equals(scores[z3])) {
                                Blank score3 = new Blank(z3 + len, j + 1);
                                wSheet2_allSch.addCell(score3);
                            } else {
                                Label score4 = new Label(z3 + len, j + 1, scores[z3] == null ? "" : scores[z3].toString());
                                wSheet2_allSch.addCell(score4);
                            }
                        }
                        if (qTypeScores != null) {
                            for (int z4 = 0; z4 < qTypeScores.length; z4++) {
                                Label qTypeScore2 = new Label(questionNums.length + len + z4, j + 1, qTypeScores[z4] == null ? "" : String.valueOf(qTypeScores[z4]));
                                wSheet2_allSch.addCell(qTypeScore2);
                            }
                        }
                    }
                }
                if (null != wwBook) {
                    removeDetailExcelColumn(sheet, leibieCCell8, 8, xuankezuheCCell7, 7);
                    wwBook.write();
                    wwBook.close();
                    leibieCCell8.clear();
                    xuankezuheCCell7.clear();
                }
                if (null != wwBook2_allSch) {
                    removeDetailExcelColumn(wSheet2_allSch, leibieQCell8, 8, xuankezuheQCell7, 7);
                    wwBook2_allSch.write();
                    wwBook2_allSch.close();
                    leibieQCell8.clear();
                    xuankezuheQCell7.clear();
                }
            }
        } catch (Exception e) {
            this.log.info("生成【科目详情】失败：--" + loginUser + "--exam:[" + exam + "]--grade:[" + grade + "]--subject:[" + subject + "]--studentType:[" + studentType + "]--subCompose:[" + subCompose + "]--fufen:[" + fufen + "]--" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS").format(new Date()) + e);
            e.printStackTrace();
        }
    }

    public void writeSubjectDetailSheet_xlsx(Map<String, String> map, String xuanDirFolder) {
        String loginUser = map.get("loginUser");
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        if ("-1".equals(subject)) {
            subject = map.get("sjt");
        }
        String fufen = map.get("fufen");
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String subCompose = map.get("xuankezuhe");
        String isJointStuType = map.get(Const.isJointStuType);
        String resourceType = map.get("resourceType");
        String source = map.get("source");
        String school = "";
        List schoolList = this.analy.getSchool(exam, subject, studentType, grade, fufen);
        for (int i = 0; i < schoolList.size(); i++) {
            AjaxData ajaxData = schoolList.get(i);
            school = school + Const.STRING_SEPERATOR + String.valueOf(ajaxData.getNum());
        }
        List list = this.examManageService.getScoreDetail2(exam, subject, Const.class_grade, grade, school.substring(1, school.length()), studentType, "1", isJointStuType, subCompose, source);
        if (null == list || list.size() <= 0) {
            return;
        }
        Object[] obj1 = (Object[]) list.get(0);
        if (null == obj1[9] || "null".equals(obj1[9]) || "".equals(obj1[9])) {
            Map args = new HashMap();
            args.put("exam", exam);
            args.put("grade", grade);
            args.put("subject", subject);
            args.put("fufen", fufen);
            args.put("resourceType", resourceType);
            this.dao2._execute("update managefile set status=1 where examNum={exam} and gradeNum={grade} and subjectNum={subject} and source={fufen} and resourceType={resourceType} ", args);
        }
        try {
            if (list.size() > 0 && null != obj1[9] && !"null".equals(obj1[9]) && !"".equals(obj1[9])) {
                String[] biaotou = {"序号", "学校名称*", "考号", "ID号*", "学号*", "班级*", "姓名", "选科组合", "类别", "生源", "备注", "总分", "客观题选项*"};
                int len = biaotou.length;
                Set xuankezuheQCell7 = new HashSet();
                Set leibieQCell8 = new HashSet();
                Set xuankezuheCCell7 = new HashSet();
                Set leibieCCell8 = new HashSet();
                Object[] obj0 = (Object[]) list.get(0);
                String schoolname = String.valueOf(obj0[0]);
                Map args1 = new HashMap();
                args1.put("schoolname", schoolname);
                String curSchool = this.dao2._queryStr("select id from school where schoolName={schoolname} ", args1);
                String[] questionNums = null;
                if (null != obj0[9] && !"null".equals(obj0[9]) && !"".equals(obj0[9])) {
                    questionNums = obj0[9].toString().split(Const.STRING_SEPERATOR);
                }
                String[] qTypes = null;
                if (obj0[12] != null) {
                    qTypes = obj0[12].toString().split(Const.STRING_SEPERATOR);
                }
                String fileName = fufen + "_" + resourceType + "_" + exam + "_" + grade + "_" + subject + "_" + studentType + "_" + subCompose + "_allschool";
                String schoolDirFolder = xuanDirFolder + fileName + ".xlsx";
                createFile(schoolDirFolder);
                File file = new File(schoolDirFolder);
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    file.delete();
                    file.createNewFile();
                }
                SXSSFWorkbook wwBook2_allSch = new SXSSFWorkbook();
                SXSSFSheet createSheet = wwBook2_allSch.createSheet("全区");
                Row wSheet2_allSch_row0 = createSheet.createRow(0);
                SXSSFWorkbook wwBook = null;
                Sheet sheet = null;
                File excelFile = null;
                DecimalFormat df01 = new DecimalFormat("0.0");
                int jj = 0;
                for (int j = 0; j < list.size(); j++) {
                    Object[] obj = (Object[]) list.get(j);
                    if (null != obj[9] && !"null".equals(obj[9]) && !"".equals(obj[9])) {
                        String[] scores = String.valueOf(obj[10]).split(Const.STRING_SEPERATOR);
                        String[] qTypeScores = null;
                        if (obj[13] != null) {
                            qTypeScores = String.valueOf(obj[13]).split(Const.STRING_SEPERATOR);
                        }
                        if (j == 0) {
                            for (int i2 = 0; i2 < len; i2++) {
                                Cell cell = wSheet2_allSch_row0.createCell(i2);
                                cell.setCellValue(biaotou[i2]);
                            }
                            for (int i3 = 0; i3 < questionNums.length; i3++) {
                                Cell queNum = wSheet2_allSch_row0.createCell(i3 + len);
                                queNum.setCellValue("T" + questionNums[i3]);
                            }
                            if (qTypes != null) {
                                for (int i4 = 0; i4 < qTypes.length; i4++) {
                                    Cell qType = wSheet2_allSch_row0.createCell(questionNums.length + len + i4);
                                    qType.setCellValue(qTypes[i4] == null ? " " : String.valueOf(qTypes[i4]));
                                }
                            }
                        }
                        if (!String.valueOf(obj[0]).equals(schoolname)) {
                            if (null != wwBook) {
                                FileOutputStream fileOut = new FileOutputStream(excelFile);
                                wwBook.write(fileOut);
                                fileOut.flush();
                                wwBook.dispose();
                                wwBook.close();
                                fileOut.close();
                                leibieCCell8.clear();
                                xuankezuheCCell7.clear();
                            }
                            wwBook = null;
                            sheet = null;
                            jj = 0;
                            schoolname = String.valueOf(obj[0]);
                            Map args2 = new HashMap();
                            args2.put("schoolname", schoolname);
                            curSchool = this.dao2._queryStr("select id from school where schoolName={schoolname} ", args2);
                        }
                        if (jj == 0) {
                            String fileName1 = fufen + "_" + resourceType + "_" + exam + "_" + grade + "_" + subject + "_" + studentType + "_" + subCompose + "_" + curSchool;
                            String schoolDirFolder1 = xuanDirFolder + fileName1 + ".xlsx";
                            createFile(schoolDirFolder1);
                            excelFile = new File(schoolDirFolder1);
                            if (!excelFile.exists()) {
                                excelFile.createNewFile();
                            } else {
                                excelFile.delete();
                                excelFile.createNewFile();
                            }
                            wwBook = new SXSSFWorkbook();
                            sheet = wwBook.createSheet("小题分");
                            Row sheet_row0 = sheet.createRow(0);
                            for (int i5 = 0; i5 < len; i5++) {
                                Cell cell2 = sheet_row0.createCell(i5);
                                cell2.setCellValue(biaotou[i5]);
                            }
                            for (int i6 = 0; i6 < questionNums.length; i6++) {
                                Cell queNum2 = sheet_row0.createCell(i6 + len);
                                queNum2.setCellValue("T" + questionNums[i6]);
                            }
                            if (qTypes != null) {
                                for (int i7 = 0; i7 < qTypes.length; i7++) {
                                    Cell qType2 = sheet_row0.createCell(questionNums.length + len + i7);
                                    qType2.setCellValue(qTypes[i7] == null ? " " : String.valueOf(qTypes[i7]));
                                }
                            }
                        }
                        Row sheet_row_jj_1 = sheet.createRow(jj + 1);
                        Cell number = sheet_row_jj_1.createCell(0);
                        number.setCellValue(jj + 1);
                        xuankezuheQCell7.add(String.valueOf(obj[14]));
                        xuankezuheCCell7.add(String.valueOf(obj[14]));
                        leibieQCell8.add(String.valueOf(obj[15]));
                        leibieCCell8.add(String.valueOf(obj[15]));
                        Object[] biaotouData = {obj[0], obj[1], obj[2], obj[3], obj[4], obj[5], obj[14], obj[15], obj[16], obj[6], obj[7], obj[8]};
                        for (int k = 0; k < len - 1; k++) {
                            if (k == 9) {
                                String msg = "";
                                if ("0".equals(String.valueOf(biaotouData[k])) || "5".equals(String.valueOf(biaotouData[k]))) {
                                    msg = "【缺考】";
                                } else if ("1".equals(String.valueOf(biaotouData[k]))) {
                                    msg = "【违纪】";
                                } else if ("3".equals(String.valueOf(biaotouData[k])) || "4".equals(String.valueOf(biaotouData[k]))) {
                                    msg = "【零分】";
                                }
                                sheet_row_jj_1.createCell(k + 1).setCellValue(msg);
                            } else if (k == 10) {
                                Cell base = sheet_row_jj_1.createCell(k + 1);
                                if (biaotouData[k] == "") {
                                    base.setCellValue(biaotouData[k] == null ? "" : String.valueOf(biaotouData[k]));
                                } else {
                                    base.setCellValue((biaotouData[k] == null ? null : Convert.toDouble(Convert.toBigDecimal(df01.format(Double.valueOf(biaotouData[k].toString()))).stripTrailingZeros().toPlainString())).doubleValue());
                                }
                            } else {
                                sheet_row_jj_1.createCell(k + 1).setCellValue(biaotouData[k] == null ? " " : String.valueOf(biaotouData[k]));
                            }
                        }
                        for (int z = 0; z < scores.length; z++) {
                            Cell score = sheet_row_jj_1.createCell(z + len);
                            if (scores[z].length() == 0) {
                                score.setCellValue(scores[z] == null ? "" : scores[z].toString());
                            } else {
                                score.setCellValue((scores[z] == null ? null : Convert.toDouble(Convert.toBigDecimal(df01.format(Double.valueOf(scores[z]))).stripTrailingZeros().toPlainString())).doubleValue());
                            }
                        }
                        if (qTypeScores != null) {
                            for (int z2 = 0; z2 < qTypeScores.length; z2++) {
                                Cell qTypeScore = sheet_row_jj_1.createCell(questionNums.length + len + z2);
                                if (qTypeScores[z2].length() == 0) {
                                    qTypeScore.setCellValue(qTypeScores[z2] == null ? "" : qTypeScores[z2].toString());
                                } else {
                                    qTypeScore.setCellValue((qTypeScores[z2] == null ? null : Convert.toDouble(Convert.toBigDecimal(df01.format(Double.valueOf(qTypeScores[z2].toString()))).stripTrailingZeros().toPlainString())).doubleValue());
                                }
                            }
                        }
                        jj++;
                        Row wSheet2_allSch_row_j_1 = createSheet.createRow(j + 1);
                        Cell allSchNumber = wSheet2_allSch_row_j_1.createCell(0);
                        allSchNumber.setCellValue(j + 1);
                        for (int k2 = 0; k2 < len - 1; k2++) {
                            if (k2 == 8) {
                                String msg2 = "";
                                if ("0".equals(String.valueOf(biaotouData[k2])) || "5".equals(String.valueOf(biaotouData[k2]))) {
                                    msg2 = "【缺考】";
                                } else if ("1".equals(String.valueOf(biaotouData[k2]))) {
                                    msg2 = "【违纪】";
                                } else if ("3".equals(String.valueOf(biaotouData[k2])) || "4".equals(String.valueOf(biaotouData[k2]))) {
                                    msg2 = "【零分】";
                                }
                                wSheet2_allSch_row_j_1.createCell(k2 + 1).setCellValue(msg2);
                            } else {
                                Cell base2 = wSheet2_allSch_row_j_1.createCell(k2 + 1);
                                if (StrUtil.isEmpty(Convert.toStr(biaotouData[k2], ""))) {
                                    base2.setCellValue(" ");
                                } else if (k2 == 9) {
                                    base2.setCellValue(Convert.toDouble(Convert.toBigDecimal(df01.format(Double.valueOf(biaotouData[k2].toString()))).stripTrailingZeros().toPlainString()).doubleValue());
                                } else {
                                    base2.setCellValue(Convert.toStr(biaotouData[k2]));
                                }
                            }
                        }
                        for (int z3 = 0; z3 < scores.length; z3++) {
                            Cell score2 = wSheet2_allSch_row_j_1.createCell(z3 + len);
                            if (StrUtil.isEmpty(scores[z3])) {
                                score2.setCellValue("");
                            } else {
                                score2.setCellValue(Convert.toDouble(Convert.toBigDecimal(df01.format(Double.valueOf(scores[z3]))).stripTrailingZeros().toPlainString()).doubleValue());
                            }
                        }
                        if (qTypeScores != null) {
                            for (int z4 = 0; z4 < qTypeScores.length; z4++) {
                                Cell qTypeScore2 = wSheet2_allSch_row_j_1.createCell(questionNums.length + len + z4);
                                if (StrUtil.isEmpty(qTypeScores[z4])) {
                                    qTypeScore2.setCellValue("");
                                } else {
                                    qTypeScore2.setCellValue(Convert.toDouble(Convert.toBigDecimal(df01.format(Double.valueOf(qTypeScores[z4]))).stripTrailingZeros().toPlainString()).doubleValue());
                                }
                            }
                        }
                    }
                    ThreadUtil.sleep(5L);
                }
                if (null != wwBook) {
                    FileOutputStream fileOut2 = new FileOutputStream(excelFile);
                    wwBook.write(fileOut2);
                    fileOut2.flush();
                    wwBook.dispose();
                    wwBook.close();
                    fileOut2.close();
                    leibieCCell8.clear();
                    xuankezuheCCell7.clear();
                }
                if (null != wwBook2_allSch) {
                    FileOutputStream fileOut3 = new FileOutputStream(file);
                    wwBook2_allSch.write(fileOut3);
                    fileOut3.flush();
                    wwBook2_allSch.dispose();
                    wwBook2_allSch.close();
                    leibieQCell8.clear();
                    xuankezuheQCell7.clear();
                }
            }
        } catch (Exception e) {
            this.log.info("生成【小题分】失败：--" + loginUser + "--exam:[" + exam + "]--grade:[" + grade + "]--subject:[" + subject + "]--studentType:[" + studentType + "]--subCompose:[" + subCompose + "]--fufen:[" + fufen + "]--" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS").format(new Date()) + e);
            e.printStackTrace();
        }
    }

    public void writeSubjectDetailFcSheet(Map<String, String> map, String xuanDirFolder) {
        String loginUser = map.get("loginUser");
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        if ("-1".equals(subject)) {
            subject = map.get("sjt");
        }
        String fufen = map.get("fufen");
        String studentType = map.get(Const.EXPORTREPORT_studentType);
        String subCompose = map.get("xuankezuhe");
        String isJointStuType = map.get(Const.isJointStuType);
        String resourceType = map.get("resourceType");
        String school = "";
        List schoolList = this.analy.getSchool(exam, subject, studentType, grade, fufen);
        for (int i = 0; i < schoolList.size(); i++) {
            AjaxData ajaxData = schoolList.get(i);
            school = school + Const.STRING_SEPERATOR + String.valueOf(ajaxData.getNum());
        }
        List list = this.examManageService.getScoreDetailFc2(exam, subject, Const.class_grade, grade, school.substring(1, school.length()), studentType, "1", isJointStuType, subCompose);
        Object[] obj1 = (Object[]) list.get(0);
        if (null == obj1[9] || "null".equals(obj1[9]) || "".equals(obj1[9])) {
            Map args = new HashMap();
            args.put("exam", exam);
            args.put("grade", grade);
            args.put("subject", subject);
            args.put("fufen", fufen);
            args.put("resourceType", resourceType);
            this.dao2._execute("update managefile set status=1 where examNum={exam} and gradeNum={grade} and subjectNum={subject} and source={fufen} and resourceType={resourceType} ", args);
        }
        try {
            if (list.size() > 0 && null != obj1[9] && !"null".equals(obj1[9]) && !"".equals(obj1[9])) {
                String[] biaotou = {"序号", "学校名称*", "考号", "ID号*", "学号*", "教学班*", "班级*", "姓名", "选科组合", "类别", "备注", "总分", "客观题选项*"};
                int len = biaotou.length;
                Set xuankezuheQCell8 = new HashSet();
                Set leibieQCell9 = new HashSet();
                Set xuankezuheCCell8 = new HashSet();
                Set leibieCCell9 = new HashSet();
                Object[] obj0 = (Object[]) list.get(0);
                String schoolname = String.valueOf(obj0[0]);
                Map args1 = new HashMap();
                args1.put("schoolname", schoolname);
                String curSchool = this.dao2._queryStr("select id from school where schoolName={schoolname}", args1);
                String[] questionNums = obj0[9].toString().split(Const.STRING_SEPERATOR);
                String[] qTypes = null;
                if (obj0[12] != null) {
                    qTypes = obj0[12].toString().split(Const.STRING_SEPERATOR);
                }
                String fileName = fufen + "_" + resourceType + "_" + exam + "_" + grade + "_" + subject + "_" + studentType + "_" + subCompose + "_allschool";
                String schoolDirFolder = xuanDirFolder + fileName + ".xls";
                createFile(schoolDirFolder);
                File file = new File(schoolDirFolder);
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    file.delete();
                    file.createNewFile();
                }
                WritableWorkbook wwBook2_allSch = Workbook.createWorkbook(file);
                WritableSheet wSheet2_allSch = wwBook2_allSch.createSheet("全区", 0);
                WritableWorkbook wwBook = null;
                WritableSheet sheet = null;
                DecimalFormat df01 = new DecimalFormat("0.0");
                int jj = 0;
                for (int j = 0; j < list.size(); j++) {
                    Object[] obj = (Object[]) list.get(j);
                    String[] scores = String.valueOf(obj[10]).split(Const.STRING_SEPERATOR);
                    String[] qTypeScores = null;
                    if (obj[13] != null) {
                        qTypeScores = String.valueOf(obj[13]).split(Const.STRING_SEPERATOR);
                    }
                    if (j == 0) {
                        for (int i2 = 0; i2 < len; i2++) {
                            Label cell = new Label(i2, 0, biaotou[i2]);
                            wSheet2_allSch.addCell(cell);
                        }
                        for (int i3 = 0; i3 < questionNums.length; i3++) {
                            Label queNum = new Label(i3 + len, 0, "T" + questionNums[i3]);
                            wSheet2_allSch.addCell(queNum);
                        }
                        for (int i4 = 0; i4 < qTypes.length; i4++) {
                            Label qType = new Label(questionNums.length + len + i4, 0, qTypes[i4] == null ? " " : String.valueOf(qTypes[i4]));
                            wSheet2_allSch.addCell(qType);
                        }
                        if (qTypes != null) {
                            for (int i5 = 0; i5 < qTypes.length; i5++) {
                                Label qType2 = new Label(questionNums.length + len + i5, 0, qTypes[i5] == null ? " " : String.valueOf(qTypes[i5]));
                                wSheet2_allSch.addCell(qType2);
                            }
                        }
                    }
                    if (!String.valueOf(obj[0]).equals(schoolname)) {
                        if (null != wwBook) {
                            removeDetailExcelColumn(sheet, leibieCCell9, 9, xuankezuheCCell8, 8);
                            wwBook.write();
                            wwBook.close();
                            leibieCCell9.clear();
                            xuankezuheCCell8.clear();
                        }
                        wwBook = null;
                        sheet = null;
                        jj = 0;
                        schoolname = String.valueOf(obj[0]);
                        Map args2 = new HashMap();
                        args2.put("schoolname", schoolname);
                        curSchool = this.dao2._queryStr("select id from school where schoolName={schoolname} ", args2);
                    }
                    if (jj == 0) {
                        String fileName1 = fufen + "_" + resourceType + "_" + exam + "_" + grade + "_" + subject + "_" + studentType + "_" + subCompose + "_" + curSchool;
                        String schoolDirFolder1 = xuanDirFolder + fileName1 + ".xls";
                        createFile(schoolDirFolder1);
                        File excelFile = new File(schoolDirFolder1);
                        if (!excelFile.exists()) {
                            excelFile.createNewFile();
                        } else {
                            excelFile.delete();
                            excelFile.createNewFile();
                        }
                        wwBook = Workbook.createWorkbook(excelFile);
                        sheet = wwBook.createSheet("科目详情", 0);
                        for (int i6 = 0; i6 < len; i6++) {
                            Label cell2 = new Label(i6, 0, biaotou[i6]);
                            sheet.addCell(cell2);
                        }
                        for (int i7 = 0; i7 < questionNums.length; i7++) {
                            Label queNum2 = new Label(i7 + len, 0, "T" + questionNums[i7]);
                            sheet.addCell(queNum2);
                        }
                        for (int i8 = 0; i8 < qTypes.length; i8++) {
                            Label qType3 = new Label(questionNums.length + len + i8, 0, qTypes[i8] == null ? " " : String.valueOf(qTypes[i8]));
                            sheet.addCell(qType3);
                        }
                    }
                    Number number = new Number(0, jj + 1, jj + 1);
                    sheet.addCell(number);
                    xuankezuheQCell8.add(String.valueOf(obj[16]));
                    xuankezuheCCell8.add(String.valueOf(obj[16]));
                    leibieQCell9.add(String.valueOf(obj[17]));
                    leibieCCell9.add(String.valueOf(obj[17]));
                    Object[] biaotouData = {obj[0], obj[1], obj[2], obj[3], obj[15], obj[4], obj[5], obj[16], obj[18], obj[6], obj[7], obj[8]};
                    for (int k = 0; k < len - 1; k++) {
                        if (k == 10) {
                            if ("0".equals(String.valueOf(biaotouData[k])) || "5".equals(String.valueOf(biaotouData[k]))) {
                                Label base = new Label(k + 1, jj + 1, "【缺考】");
                                sheet.addCell(base);
                            } else if ("1".equals(String.valueOf(biaotouData[k]))) {
                                Label base2 = new Label(k + 1, jj + 1, "【违纪】");
                                sheet.addCell(base2);
                            } else if ("3".equals(String.valueOf(biaotouData[k])) || "4".equals(String.valueOf(biaotouData[k]))) {
                                Label base3 = new Label(k + 1, jj + 1, "【零分】");
                                sheet.addCell(base3);
                            } else {
                                Label base4 = new Label(k + 1, jj + 1, "");
                                sheet.addCell(base4);
                            }
                        } else if (k == 11) {
                            if (StrUtil.isNotEmpty(Convert.toStr(biaotouData[k], ""))) {
                                Number base5 = new Number(k + 1, jj + 1, Convert.toDouble(biaotouData[k], Double.valueOf(0.0d)).doubleValue());
                                sheet.addCell(base5);
                            } else {
                                Blank base6 = new Blank(k + 1, jj + 1);
                                sheet.addCell(base6);
                            }
                        } else {
                            Label base7 = new Label(k + 1, jj + 1, biaotouData[k] == null ? " " : String.valueOf(biaotouData[k]));
                            sheet.addCell(base7);
                        }
                    }
                    for (int z = 0; z < scores.length; z++) {
                        if (StrUtil.isEmpty(scores[z])) {
                            Blank score = new Blank(z + len, jj + 1);
                            sheet.addCell(score);
                        } else {
                            Number score2 = new Number(z + len, jj + 1, Convert.toDouble(Convert.toBigDecimal(df01.format(Double.valueOf(scores[z]))).stripTrailingZeros().toPlainString()).doubleValue());
                            sheet.addCell(score2);
                        }
                    }
                    for (int z2 = 0; z2 < qTypeScores.length; z2++) {
                        if (StrUtil.isEmpty(qTypeScores[z2])) {
                            Blank qTypeScore = new Blank(questionNums.length + len + z2, jj + 1);
                            sheet.addCell(qTypeScore);
                        } else {
                            Number qTypeScore2 = new Number(questionNums.length + len + z2, jj + 1, Convert.toDouble(Convert.toBigDecimal(df01.format(Double.valueOf(qTypeScores[z2]))).stripTrailingZeros().toPlainString()).doubleValue());
                            sheet.addCell(qTypeScore2);
                        }
                    }
                    jj++;
                    Number allSchNumber = new Number(0, jj + 1, jj + 1);
                    wSheet2_allSch.addCell(allSchNumber);
                    for (int k2 = 0; k2 < len - 1; k2++) {
                        if (k2 == 9) {
                            if ("0".equals(String.valueOf(biaotouData[k2])) || "5".equals(String.valueOf(biaotouData[k2]))) {
                                Label base8 = new Label(k2 + 1, jj + 1, "【缺考】");
                                wSheet2_allSch.addCell(base8);
                            } else if ("1".equals(String.valueOf(biaotouData[k2]))) {
                                Label base9 = new Label(k2 + 1, jj + 1, "【违纪】");
                                wSheet2_allSch.addCell(base9);
                            } else if ("3".equals(String.valueOf(biaotouData[k2])) || "4".equals(String.valueOf(biaotouData[k2]))) {
                                Label base10 = new Label(k2 + 1, jj + 1, "【零分】");
                                wSheet2_allSch.addCell(base10);
                            } else {
                                Label base11 = new Label(k2 + 1, jj + 1, "");
                                wSheet2_allSch.addCell(base11);
                            }
                        } else if (k2 == 10) {
                            if (StrUtil.isNotEmpty(Convert.toStr(biaotouData[k2], ""))) {
                                Number base12 = new Number(k2 + 1, jj + 1, Convert.toDouble(biaotouData[k2], Double.valueOf(0.0d)).doubleValue());
                                wSheet2_allSch.addCell(base12);
                            } else {
                                Blank base13 = new Blank(k2 + 1, jj + 1);
                                wSheet2_allSch.addCell(base13);
                            }
                        } else {
                            Label base14 = new Label(k2 + 1, jj + 1, biaotouData[k2] == null ? " " : String.valueOf(biaotouData[k2]));
                            wSheet2_allSch.addCell(base14);
                        }
                    }
                    for (int z3 = 0; z3 < scores.length; z3++) {
                        if (StrUtil.isEmpty(scores[z3])) {
                            Blank score3 = new Blank(z3 + len, jj + 1);
                            wSheet2_allSch.addCell(score3);
                        } else {
                            Number score4 = new Number(z3 + len, jj + 1, Convert.toDouble(Convert.toBigDecimal(df01.format(Double.valueOf(scores[z3]))).stripTrailingZeros().toPlainString()).doubleValue());
                            wSheet2_allSch.addCell(score4);
                        }
                    }
                    for (int z4 = 0; z4 < qTypeScores.length; z4++) {
                        if (StrUtil.isEmpty(qTypeScores[z4])) {
                            Blank qTypeScore3 = new Blank(questionNums.length + len + z4, jj + 1);
                            wSheet2_allSch.addCell(qTypeScore3);
                        } else {
                            Number qTypeScore4 = new Number(questionNums.length + len + z4, jj + 1, Convert.toDouble(Convert.toBigDecimal(df01.format(Double.valueOf(qTypeScores[z4]))).stripTrailingZeros().toPlainString()).doubleValue());
                            wSheet2_allSch.addCell(qTypeScore4);
                        }
                    }
                }
                if (null != wwBook) {
                    removeDetailExcelColumn(sheet, leibieCCell9, 9, xuankezuheCCell8, 8);
                    wwBook.write();
                    wwBook.close();
                    leibieCCell9.clear();
                    xuankezuheCCell8.clear();
                }
                if (null != wwBook2_allSch) {
                    removeDetailExcelColumn(wSheet2_allSch, leibieQCell9, 9, xuankezuheQCell8, 8);
                    wwBook2_allSch.write();
                    wwBook2_allSch.close();
                    leibieQCell9.clear();
                    xuankezuheQCell8.clear();
                }
            }
        } catch (Exception e) {
            this.log.info("生成【科目详情-教学班】失败：--" + loginUser + "--exam:[" + exam + "]--grade:[" + grade + "]--subject:[" + subject + "]--studentType:[" + studentType + "]--subCompose:[" + subCompose + "]--fufen:[" + fufen + "]--" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS").format(new Date()) + e);
            e.printStackTrace();
        }
    }

    public void manageFileLog(Map<String, String> map) {
        String exam = map.get(Const.EXPORTREPORT_examNum);
        String grade = map.get(Const.EXPORTREPORT_gradeNum);
        String subject = map.get(Const.EXPORTREPORT_subjectNum);
        if ("-1".equals(subject)) {
            subject = map.get("sjt");
        }
        String fufen = map.get("fufen");
        String resourceType = map.get("resourceType");
        String loginUser = map.get("loginUser");
        map.get("rootPath");
        String status = map.get(Const.CORRECT_SCORECORRECT);
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("fufen", fufen);
        args.put("resourceType", resourceType);
        this.dao2._execute("delete from managefile where examNum={exam} and gradeNum={grade} and subjectNum={subject} and source={fufen} and resourceType={resourceType} ", args);
        ManageFile manageFile = new ManageFile();
        manageFile.setExamNum(Integer.valueOf(Integer.parseInt(exam)));
        manageFile.setGradeNum(Integer.valueOf(Integer.parseInt(grade)));
        manageFile.setSubjectNum(Integer.valueOf(Integer.parseInt(subject)));
        manageFile.setSource(fufen);
        manageFile.setResourceType(resourceType);
        manageFile.setInsertUser(loginUser);
        manageFile.setInsertDate(DateUtil.getCurrentTime());
        manageFile.setStatus(status);
        this.dao2.save(manageFile);
    }

    public String getHttpPath(String newPath, String folderName, String zipName, Map<String, String> map) {
        deleteZipAll(newPath, folderName);
        methodZipAll(newPath, folderName, zipName);
        String zipF = zipName + ".zip";
        String urlS = map.get("urlS");
        String loginUser = map.get("loginUser");
        return (urlS.substring(0, urlS.lastIndexOf("/") + 1) + "downloadFile/" + loginUser + "/") + zipF;
    }

    public void deleteZipAll(String baseFolder, String folderName) {
        String zipFileName = baseFolder.replace('\\', '/') + folderName + ".zip";
        File zipFile = new File(zipFileName);
        if (zipFile.exists()) {
            zipFile.delete();
        }
    }

    public void createFolder(String dirFolder) {
        File folder = new File(dirFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void createFile(String dirFolder) {
        File file = new File(dirFolder);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String methodZipAll(String baseFolder, String folderName, String zipName) {
        deleteZipAll(baseFolder, zipName);
        String zipFileName = baseFolder.replace('\\', '/') + zipName + ".zip";
        compressAllZip(baseFolder, folderName, zipFileName);
        return zipName + ".zip";
    }

    public void compressAllZip(String dirPath, String srcName, String targetName) {
        if (null == dirPath || "".equals(dirPath)) {
            this.log.info("压缩失败:" + dirPath + "目录不存在");
            return;
        }
        File baseDir = new File(dirPath);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            this.log.info("压缩失败-----------:" + dirPath + "目录不存在");
            return;
        }
        String basicRootDir = baseDir.getAbsolutePath();
        File targetFile = new File(targetName);
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetFile));
            out.setEncoding("gbk");
            if (srcName.equals("*")) {
                compressDirToZip(basicRootDir, baseDir, out);
            } else {
                File file = new File(baseDir, srcName);
                if (file.isFile()) {
                    compressFileToZip(basicRootDir, file, out);
                } else {
                    compressDirToZip(basicRootDir, file, out);
                }
            }
            out.close();
        } catch (IOException e) {
            this.log.info(DateUtil.getCurrentTime() + "-----压缩失败：");
            this.log.info("", e);
        }
    }

    private void compressDirToZip(String basicRootDir, File dir, ZipOutputStream out) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files.length == 0) {
                ZipEntry entry = new ZipEntry(getFileName(basicRootDir, dir));
                try {
                    out.putNextEntry(entry);
                    out.closeEntry();
                    return;
                } catch (IOException e) {
                    this.log.info("", e);
                    return;
                }
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    compressFileToZip(basicRootDir, files[i], out);
                } else {
                    compressDirToZip(basicRootDir, files[i], out);
                }
            }
        }
    }

    private void compressFileToZip(String basicRootDir, File file, ZipOutputStream out) {
        byte[] buffer = new byte[4096];
        if (file.isFile()) {
            try {
                FileInputStream in = new FileInputStream(file);
                ZipEntry entry = new ZipEntry(getFileName(basicRootDir, file));
                out.putNextEntry(entry);
                while (true) {
                    int bytes_read = in.read(buffer);
                    if (bytes_read != -1) {
                        out.write(buffer, 0, bytes_read);
                    } else {
                        out.closeEntry();
                        in.close();
                        return;
                    }
                }
            } catch (IOException e) {
                this.log.info("", e);
            }
        }
    }

    private String getFileName(String basicRootDir, File file) {
        if (!basicRootDir.endsWith(File.separator)) {
            basicRootDir = basicRootDir + File.separator;
        }
        String filePath = file.getAbsolutePath();
        if (file.isDirectory()) {
            filePath = filePath + "/";
        }
        int index = filePath.indexOf(basicRootDir);
        return filePath.substring(index + basicRootDir.length());
    }

    public void deleteFileAndDirect(String dirFolder) {
        File dir = new File(dirFolder);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files.length == 0) {
                deleteEmptyDir(dir);
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    deleteSingleFile_file(files[i].toString());
                } else {
                    deleteDirect(files[i]);
                }
            }
            deleteEmptyDir(dir);
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
        file.delete();
    }

    public void deleteEmptyDir(File dir) {
        if (dir.isDirectory() && dir.listFiles().length == 0) {
            dir.delete();
        }
    }

    public Integer deleteOneByNum(String colum, String valule, Class cla) {
        return this.examDao.deleteOneByNum(colum, valule, cla);
    }

    public Object getOneByNum(String colum, String valule, Class cla) {
        return this.examDao.getOneByNum(colum, valule, cla);
    }

    public String getExampaperNumBySubjectAndGradeAndExam(String examNum, String subjectNum, String gradeNum) {
        return this.examDao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
    }

    public String judgeYouOrWang(String examNum, String subjectNum, String gradeNum) {
        return this.examDao.judgeYouOrWang(examNum, subjectNum, gradeNum);
    }

    public String checkIfHaveCalculate(String examNum, String subjectNum, String gradeNum) {
        return this.examDao.checkIfHaveCalculate(examNum, subjectNum, gradeNum);
    }

    public String getExampaperNum(String examNum, String subjectNum, String gradeNum) {
        return this.examDao.getExampaperNum(examNum, subjectNum, gradeNum);
    }

    public String getpExampaperNum(String examNum, String subjectNum, String gradeNum) {
        return this.examDao.getpExampaperNum(examNum, subjectNum, gradeNum);
    }

    public Exampaper getExampaper(String examNum, String subjectNum, String gradeNum) {
        return this.examDao.getExampaper(examNum, subjectNum, gradeNum);
    }

    public List<Exampaper> getExampaperNum_threeSjt(String examNum, String subjectNum, String gradeNum, String pexamPaperNum) {
        return this.examDao.getExampaperNum_threeSjt(examNum, subjectNum, gradeNum, pexamPaperNum);
    }

    public List<Subject> getSubjectName(String subjectNum) {
        return this.examDao.getSubjectName(subjectNum);
    }

    public Integer save(List<Define> defines, String Pnum, List<Knowdetail> knows) throws Exception {
        if (null != defines && defines.size() > 0) {
            this.dao2.batchSave(defines);
        }
        if (null != knows && knows.size() > 0) {
            this.dao2.batchSave(knows);
        }
        saveA(Pnum);
        saveCat(Pnum);
        return 1;
    }

    public Integer save_Abilitydetail(List<Define> defines, String Pnum, List<Abilitydetail> abilitys) throws Exception {
        if (null != defines && defines.size() > 0) {
            this.dao2.batchSave(defines);
        }
        if (null != abilitys && abilitys.size() > 0) {
            this.dao2.batchSave(abilitys);
        }
        saveA(Pnum);
        saveCat(Pnum);
        return 1;
    }

    public Integer save_know_ability(List<Define> defines, String Pnum, List<Knowdetail> knows, List<Abilitydetail> abilitys) throws Exception {
        if (null != defines && defines.size() > 0) {
            this.dao2.batchSave(defines);
        }
        if (null != knows && knows.size() > 0) {
            this.dao2.batchSave(knows);
        }
        if (null != abilitys && abilitys.size() > 0) {
            this.dao2.batchSave(abilitys);
        }
        saveA(Pnum);
        saveCat(Pnum);
        return 1;
    }

    public Integer save_know_ability_questiontype(List<Define> defines, String Pnum, List<Knowdetail> knows, List<Abilitydetail> abilitys, List<Questiontypedetail> questiondetail) throws Exception {
        if (null != defines && defines.size() > 0) {
            this.dao2.batchSave(defines);
        }
        if (null != knows && knows.size() > 0) {
            this.dao2.batchSave(knows);
        }
        if (null != abilitys && abilitys.size() > 0) {
            this.dao2.batchSave(abilitys);
        }
        if (null != questiondetail && questiondetail.size() > 0) {
            this.dao2.batchSave(questiondetail);
        }
        saveA(Pnum);
        saveCat(Pnum);
        return 1;
    }

    public Integer save_all(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        this.dao2._execute("UPDATE exampaper  SET totalScore ='0' WHERE examPaperNum={examPaperNum} ", args);
        return 1;
    }

    public Integer saveA(String Pnum) {
        try {
            Map args = new HashMap();
            args.put("Pnum", Pnum);
            Float fullscore = Float.valueOf(0.0f);
            List li2 = this.dao2._queryBeanList("SELECT  SUM(def.fullScore) fullScore FROM (SELECT id,fullScore ,questionType,questionNum,category,orderNum,choosename,isParent FROM define WHERE examPaperNum = {Pnum}  AND choosename='s'   UNION   SELECT id,fullScore ,questionType,questionNum,category,orderNum,choosename,isParent FROM define WHERE examPaperNum =  {Pnum}  AND choosename ='T'   AND category<>'-1'   ) def", Define.class, args);
            Float fullscore2 = Float.valueOf(fullscore.floatValue() + ((Define) li2.get(0)).getFullScore().floatValue());
            args.put("fullscore", fullscore2);
            List li3 = this.dao2._queryBeanList("SELECT   id,fullScore ,questionType,questionNum,category,orderNum,choosename,isParent FROM define WHERE examPaperNum =  {Pnum}  AND choosename ='T'  AND category='-1'  ", Define.class, args);
            if (null != li3 && li3.size() > 0) {
                for (int i = 0; i < li3.size(); i++) {
                    args.put("Id", ((Define) li3.get(i)).getId());
                    List li4 = this.dao2._queryBeanList("SELECT   SUM(def.fullScore) fullScore FROM (SELECT id,fullScore ,questionType,questionNum,category,orderNum,choosename,isParent FROM define WHERE choosename={Id}  ) def", Define.class, args);
                    fullscore2 = Float.valueOf(fullscore2.floatValue() + ((Define) li4.get(0)).getFullScore().floatValue());
                }
            }
            this.dao2._execute("update exampaper e SET totalScore ={fullscore} WHERE examPaperNum= {Pnum} ", args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public Integer saveCat(String Pnum) throws Exception {
        Float su;
        Float su2;
        Float valueOf;
        Float su3;
        Float valueOf2;
        List<RowArg> rowArgList = new ArrayList<>();
        Map args = new HashMap();
        args.put("Pnum", Pnum);
        List sql_li = this.dao2._queryBeanList("SELECT   DISTINCT def.category category FROM ((SELECT fullScore ,questionType,questionNum,category FROM define WHERE examPaperNum =  {Pnum}  AND choosename='s' )UNION (SELECT fullScore ,questionType,questionNum,category FROM define WHERE examPaperNum =  {Pnum}  AND choosename ='T')) def order by if(def.category= {Pnum} ,0,1)", Define.class, args);
        if (sql_li.size() > 1) {
            Float pFull = Float.valueOf(0.0f);
            for (int i = 0; i < sql_li.size(); i++) {
                Map args1 = new HashMap();
                args1.put("Pnum", Pnum);
                args1.put("Category", ((Define) sql_li.get(i)).getCategory());
                args1.put("Id", ((Define) sql_li.get(i)).getId());
                Float full = Float.valueOf(0.0f);
                if (((Define) sql_li.get(i)).getCategory().intValue() != -1) {
                    Float.valueOf(0.0f);
                    List li2 = this.dao2._queryBeanList("SELECT   SUM(def.fullScore) fullScore FROM (SELECT id,fullScore,questionnum,chooseName,category FROM define WHERE examPaperNum= {Pnum}   AND choosename='T'  AND category={Category}  )def", Define.class, args1);
                    if (null != li2 && li2.size() > 0 && null != ((Define) li2.get(0)).getFullScore() && !((Define) li2.get(0)).getFullScore().equals("")) {
                        su = ((Define) li2.get(0)).getFullScore();
                    } else {
                        su = Float.valueOf(0.0f);
                    }
                    Float full2 = Float.valueOf(full.floatValue() + su.floatValue());
                    List li3 = this.dao2._queryBeanList("SELECT   id,fullScore,questionnum,chooseName,category FROM define WHERE examPaperNum= {Pnum}  AND choosename='T' AND category='-1' ORDER BY orderNum ", Define.class, args1);
                    if (li3.size() > 0) {
                        for (int s = 0; s < li3.size(); s++) {
                            Map args2 = new HashMap();
                            args2.put("Pnum", Pnum);
                            args2.put("Id", ((Define) li3.get(s)).getId());
                            args2.put("Category", ((Define) sql_li.get(i)).getCategory());
                            List li4 = this.dao2._queryBeanList("SELECT   SUM(def.fullScore) fullScore FROM (SELECT id,fullScore,questionnum,chooseName,category,isParent  FROM define  WHERE examPaperNum= {Pnum}   AND choosename={Id}  AND category={Category}  )def", Define.class, args2);
                            if (null != ((Define) li4.get(0)).getFullScore() && !((Define) li4.get(0)).getFullScore().equals("")) {
                                su3 = ((Define) li4.get(0)).getFullScore();
                            } else {
                                su3 = Float.valueOf(0.0f);
                            }
                            full2 = Float.valueOf(full2.floatValue() + su3.floatValue());
                            List li5 = this.dao2._queryBeanList("SELECT   id,fullScore,questionnum,chooseName,category,isParent  FROM define  WHERE examPaperNum= {Pnum}   AND choosename={Id}  AND category='-1'   ", Define.class, args2);
                            if (null != li5 && li5.size() > 0) {
                                for (int j = 0; j < li5.size(); j++) {
                                    Map args4 = new HashMap();
                                    args4.put("Pnum", Pnum);
                                    args4.put("Category", ((Define) sql_li.get(i)).getCategory());
                                    args4.put("Id", ((Define) li5.get(j)).getId());
                                    List li8 = this.dao2._queryBeanList("SELECT   SUM(def.fullScore) fullScore FROM (SELECT id,fullScore,questionnum,chooseName,category  FROM subdefine WHERE examPaperNum= {Pnum}  AND category={Category}   AND pid={Id} )def", Define.class, args4);
                                    if (null != ((Define) li8.get(0)).getFullScore() && !((Define) li8.get(0)).getFullScore().equals("")) {
                                        valueOf2 = ((Define) li8.get(0)).getFullScore();
                                    } else {
                                        valueOf2 = Float.valueOf(0.0f);
                                    }
                                    Float su4 = valueOf2;
                                    full2 = Float.valueOf(full2.floatValue() + su4.floatValue());
                                }
                            }
                        }
                    }
                    List li9 = this.dao2._queryBeanList("SELECT   id,fullScore,questionnum,chooseName,category FROM define WHERE examPaperNum= {Pnum}  AND choosename='s' AND category='-1' ORDER BY orderNum ", Define.class, args1);
                    if (null != li9 && li9.size() > 0) {
                        for (int j2 = 0; j2 < li9.size(); j2++) {
                            Map args5 = new HashMap();
                            args5.put("Pnum", Pnum);
                            args5.put("Category", ((Define) sql_li.get(i)).getCategory());
                            args5.put("Id", ((Define) li9.get(j2)).getId());
                            List li10 = this.dao2._queryBeanList("SELECT   SUM(def.fullScore) fullScore FROM (SELECT id,fullScore,questionnum,chooseName,category  FROM subdefine WHERE examPaperNum= {Pnum}  AND category={Category}   AND pid={Id} )def", Define.class, args5);
                            if (null != ((Define) li10.get(0)).getFullScore() && !((Define) li10.get(0)).getFullScore().equals("")) {
                                valueOf = ((Define) li10.get(0)).getFullScore();
                            } else {
                                valueOf = Float.valueOf(0.0f);
                            }
                            Float su5 = valueOf;
                            full2 = Float.valueOf(full2.floatValue() + su5.floatValue());
                        }
                    }
                    List li6 = this.dao2._queryBeanList("SELECT   SUM(def.fullScore) fullScore FROM ((SELECT id,fullScore,questionnum,chooseName FROM define WHERE examPaperNum= {Pnum}  AND category={Category} AND choosename='s' ))def", Define.class, args1);
                    if (null != ((Define) li6.get(0)).getFullScore() && !((Define) li6.get(0)).getFullScore().equals("")) {
                        su2 = ((Define) li6.get(0)).getFullScore();
                    } else {
                        su2 = Float.valueOf(0.0f);
                    }
                    full = Float.valueOf(full2.floatValue() + su2.floatValue());
                }
                if (i == 0 && Pnum.equals(String.valueOf(((Define) sql_li.get(i)).getCategory()))) {
                    pFull = full;
                } else {
                    full = Float.valueOf(full.floatValue() + pFull.floatValue());
                }
                args1.put("full", full);
                rowArgList.add(new RowArg("update exampaper e SET totalScore ={full}  WHERE  exampaperNum={Category} ", args1));
            }
        }
        this.dao2._batchExecute(rowArgList);
        return 1;
    }

    public Integer delA(String examPaperNum, String category) {
        try {
            Map args = new HashMap();
            args.put("examPaperNum", examPaperNum);
            List li = this.dao2._queryBeanList("select DISTINCT examPaperNum from exampaper where examPaperNum<>{examPaperNum}  and pexamPaperNum={examPaperNum} ", Exampaper.class, args);
            if (li != null && li.size() > 0) {
                for (int i = 0; i < li.size(); i++) {
                    Map args1 = new HashMap();
                    args1.put("examPaperNum", examPaperNum);
                    args1.put("ExamPaperNum", ((Exampaper) li.get(i)).getExamPaperNum());
                    List li_F = this.dao2._queryBeanList("select sum(fullScore) fullScore from define where examPaperNum={examPaperNum}  and category={ExamPaperNum}  and p_questionNum='0'", Define.class, args1);
                    args1.put("FullScore", ((Define) li_F.get(0)).getFullScore());
                    this.dao2._execute("UPDATE exampaper set totalScore={FullScore} where examPaperNum={ExamPaperNum} ", args1);
                }
            }
            this.dao2._queryBeanList("select sum(fullScore) fullScore from define where examPaperNum={examPaperNum}  and  p_questionNum='0'", Define.class, args);
            this.dao2._execute("UPDATE exampaper set totalScore={FullScore}  where examPaperNum={examPaperNum} ", args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public Integer delOptionCount(String examPaperNum, String questionNum, String p_questionNum_id) throws Exception {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("p_questionNum_id", p_questionNum_id);
        List li = this.dao2._queryBeanList("select optionCount from define where examPaperNum={examPaperNum} and id={p_questionNum_id} ", Define.class, args);
        args.put("OptionCount", ((Define) li.get(0)).getOptionCount());
        this.dao2._execute("update define d SET optionCount = {OptionCount}-1 where examPaperNum={examPaperNum} and id={p_questionNum_id} ", args);
        return 1;
    }

    public Integer saveCat_cate(String Pnum) throws Exception {
        List<RowArg> rowArgList = new ArrayList<>();
        new ArrayList();
        Map args = new HashMap();
        args.put("Pnum", Pnum);
        List sql2_li = this.dao2._queryBeanList("SELECT   DISTINCT examPaperNum FROM exampaper WHERE pexamPaperNum= {Pnum}   and examPaperNum<> {Pnum} ", Exampaper.class, args);
        for (int i = 0; i < sql2_li.size(); i++) {
            Map args1 = new HashMap();
            args1.put("Pnum", Pnum);
            args1.put("ExamPaperNum", ((Exampaper) sql2_li.get(i)).getExamPaperNum());
            List list_f = this.dao2._queryBeanList("SELECT   SUM(def.fullScore) fullScore FROM ((SELECT SUM(fullScore) fullScore FROM define WHERE examPaperNum= {Pnum}  AND category={ExamPaperNum}  AND choosename='s' ) UNION(SELECT fullScore FROM define WHERE examPaperNum= {Pnum}  AND category={ExamPaperNum}  AND choosename<>'s' GROUP BY choosename))def", Define.class, args1);
            args1.put("FullScore", ((Define) list_f.get(0)).getFullScore());
            String sql = new StringBuilder().append("update exampaper e SET totalScore ='").append(((Define) list_f.get(0)).getFullScore()).toString() == null ? "0" : " {FullScore}  WHERE  exampaperNum={ExamPaperNum} ";
            rowArgList.add(new RowArg(sql, args1));
        }
        this.dao2._batchExecute(rowArgList);
        return 1;
    }

    public List<AjaxData> getAjaxClassList(String exam, String school, String subject, String grade) {
        return this.examDao.getAjaxClassList(exam, school, subject, grade);
    }

    public List<AjaxData> getAjaxExamList(String systemType) {
        return this.examDao.getAjaxExamList(systemType);
    }

    public List<AjaxData> getAjaxExamList_zy(String systemType, String userNum, String oneOrMore) {
        return this.examDao.getAjaxExamList_zy(systemType, userNum, oneOrMore);
    }

    public List<AjaxData> getAjaxGradeList(String exam, String subject, String school) {
        return this.examDao.getAjaxGradeList(exam, subject, school);
    }

    public List<AjaxData> getAjaxSchoolList(String exam, String subject) {
        return this.examDao.getAjaxSchoolList(exam, subject);
    }

    public List<AjaxData> getAjaxSubjectList(Integer exam, Integer systemType, Integer gradeNum) {
        return this.examDao.getAjaxSubjectList(exam, systemType, gradeNum);
    }

    public List<AjaxData> getAjaxSubjectList_zy(Integer exam, Integer systemType, Integer gradeNum) {
        return this.examDao.getAjaxSubjectList_zy(exam, systemType, gradeNum);
    }

    public List<AjaxData> getAjaxSubjectListForOE_zy(Integer exam, Integer systemType, Integer gradeNum) {
        return this.examDao.getAjaxSubjectListForOE_zy(exam, systemType, gradeNum);
    }

    public List<AjaxData> getUserPositionSubjectList(String exam, String userNum, String mark) {
        return this.examDao.getUserPositionSubjectList(exam, userNum, mark);
    }

    public List getUserroleNum(String userId) {
        return this.examDao.getUserroleNum(userId);
    }

    public List<AjaxData> getUserPositionGradeList(String examNum, String userNum, String subject, String mark) {
        return this.examDao.getUserPositionGradeList(examNum, userNum, subject, mark);
    }

    public List<AjaxData> getAjaxGradeList2(String exam, String subject, String systemType) {
        return this.examDao.getAjaxGradeList2(exam, subject, systemType);
    }

    public List<AjaxData> getAjaxGradeList2_zy(String exam, String subject, String systemType) {
        return this.examDao.getAjaxGradeList2_zy(exam, subject, systemType);
    }

    public List<Task> getAjaxQuestionNumList(Integer exampaperNum, String groupnum) {
        return this.examDao.getAjaxQuestionNumList(exampaperNum, groupnum);
    }

    public List<Task> getAjaxQuestionNumList2(Integer exampaperNum, String groupnum) {
        return this.examDao.getAjaxQuestionNumList2(exampaperNum, groupnum);
    }

    public List<Define> getAjaxQuestionNum(String exampaperNum) {
        return this.examDao.getAjaxQuestionNum(exampaperNum);
    }

    public List<Define> getqNumList(String exampaperNum, String qType) {
        return this.examDao.getqNumList(exampaperNum, qType);
    }

    public List<Task> getAjaxTaskList(Integer exampaperNum, String questionNum) {
        return this.examDao.getAjaxTaskList(exampaperNum, questionNum);
    }

    public List<Task> getAjaxTaskList2(Integer exampaperNum, String questionNum) {
        return this.examDao.getAjaxTaskList2(exampaperNum, questionNum);
    }

    public List<Task> getTeacherAcgScore(Integer exampaperNum, String questionNum) {
        return this.examDao.getTeacherAcgScore(exampaperNum, questionNum);
    }

    public List<MarkError> getMarkError(String examNum, String schoolNum, String gradeNum, String subjectNum) {
        return this.examDao.getMarkError(examNum, schoolNum, gradeNum, subjectNum);
    }

    public List<AjaxData> getAjaxExaminationRoomList(String exam, String subject, String grade) {
        return this.examDao.getAjaxExaminationRoomList(exam, subject, grade);
    }

    public List<Task> getAjaxFullScoreList(String exampaperNum, String questionNum) {
        return this.examDao.getAjaxFullScoreList(exampaperNum, questionNum);
    }

    public List<CorrectInfo> getCorrectList(Score s) {
        return this.examDao.getCorrectList(s);
    }

    public byte[] getQuesionImage(String scoreId) {
        return this.examDao.getQuesionImage(scoreId);
    }

    public List<Object> getCrossPageScoreIdList(String scoreId) {
        return this.examDao.getCrossPageScoreIdList(scoreId);
    }

    public byte[] getQuesionImage(String examPaperNum, String studentID, String questionNum, String scoreId) {
        return this.examDao.getQuesionImage(examPaperNum, studentID, questionNum, scoreId);
    }

    public List<String> getQuesionImage_scoreIdList(String examPaperNum, String studentID, String questionNum, String scoreId, String cross_page) {
        return this.examDao.getQuesionImage_scoreIdList(examPaperNum, studentID, questionNum, scoreId, cross_page);
    }

    public String getScoreId(String examPaperNum, String studentID) {
        return this.examDao.getScoreId(examPaperNum, studentID);
    }

    public byte[] getBigImage(String regId) {
        return this.examDao.getBigImage(regId);
    }

    public List<Integer> getScoreList(String exam, String subject, String testCenter, String grade, String examinationRoom, String studentId, String page, String correctscorestatus) {
        return this.examDao.getScoreList(exam, subject, testCenter, grade, examinationRoom, studentId, page, correctscorestatus);
    }

    public Integer updateScore(String examPaperNum, String studentID, String questionNum, String score) {
        return this.examDao.updateScore(examPaperNum, studentID, questionNum, score);
    }

    public List<AjaxData> getCorrectExamList(String stat, String type, String systemType, String userId, String oneOrMore) {
        return this.examDao.getCorrectExamList(stat, type, systemType, userId, oneOrMore);
    }

    public List<AjaxData> getCorrectExamList_zy(String stat, String type, String systemType, String userId, String oneOrMore) {
        return this.examDao.getCorrectExamList_zy(stat, type, systemType, userId, oneOrMore);
    }

    public List<AjaxData> getCorrectExamList_zyyh(String stat, String type, String systemType, String userId, String oneOrMore) {
        return this.examDao.getCorrectExamList_zyyh(stat, type, systemType, userId, oneOrMore);
    }

    public List<AjaxData> getCorrectExaminationRoomList(String exam, String school, String subject, String grade, String stat, String type, String systemType) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.examDao.getCorrectExaminationRoomList_online(exam, school, subject, grade, stat, systemType);
        }
        return this.examDao.getCorrectExaminationRoomList(exam, school, subject, grade, stat, type, "");
    }

    public List<AjaxData> getCorrectExaminationRoomList_zy(String exam, String school, String subject, String grade, String stat, String type, String systemType, String testCenter) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.examDao.getCorrectExaminationRoomList_online_zy(exam, school, subject, grade, stat, systemType, testCenter);
        }
        return this.examDao.getCorrectExaminationRoomList(exam, school, subject, grade, stat, type, testCenter);
    }

    public List<AjaxData> getCorrectExaminationRoomList_EP(String exam, String school, String subject, String grade, String stat, String type, String systemType, String examplace) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.examDao.getCorrectExaminationRoomList_online_ep(exam, school, subject, grade, stat, systemType, examplace);
        }
        return this.examDao.getCorrectExaminationRoomList_ep(exam, school, subject, grade, stat, type, examplace);
    }

    public List<AjaxData> getCorrectAuthExaminationRoomList_EP(String exam, String school, String subject, String grade, String stat, String type, String userId, String examplace) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.examDao.getCorrectAuthExaminationRoomList_online_ep(exam, school, subject, grade, stat, userId, examplace);
        }
        return this.examDao.getCorrectAuthExaminationRoomList_ep(exam, school, subject, grade, stat, type, examplace, userId);
    }

    public List<AjaxData> getCorrectClassList(String exam, String school, String subject, String grade, String stat, String type, String systemType, String testCenter) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null == ep) {
            return null;
        }
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.examDao.getCorrectClassList_online(exam, school, subject, grade, stat, type, testCenter);
        }
        return this.examDao.getCorrectClassList_online(exam, school, subject, grade, stat, type, testCenter);
    }

    public List<AjaxData> getCorrectGradeList(String exam, String subject, String school, String stat, String type, String systemType) {
        return this.examDao.getCorrectGradeList(exam, subject, school, stat, type, systemType);
    }

    public List<AjaxData> getCorrectGradeList_zy(String exam, String subject, String school, String stat, String type, String systemType, String testCenter) {
        return this.examDao.getCorrectGradeList_zy(exam, subject, school, stat, type, systemType, testCenter);
    }

    public List<AjaxData> getExamPlace(String exam, String subject, String grade, String stat, String type, String systemType) {
        return this.examDao.getExamPlace(exam, subject, grade, stat, type, systemType);
    }

    public List<AjaxData> getAuthExamPlace(String exam, String subject, String grade, String stat, String type, String userId) {
        return this.examDao.getAuthExamPlace(exam, subject, grade, stat, type, userId);
    }

    public List getCorrectSubjectList(String exam, String subject, String school, String stat, String type, String systemType) {
        return this.examDao.getCorrectSubjectList(exam, subject, school, stat, type, systemType);
    }

    public List<AjaxData> getCorrectSchoolList(String exam, String subject, String stat, String type, String systemType) {
        return this.examDao.getCorrectSchoolList(exam, subject, stat, type, systemType);
    }

    public List<AjaxData> getCorrctTCList(String exam, String subject, String stat, String type, String systemType) {
        return this.examDao.getCorrctTCList(exam, subject, stat, type, systemType);
    }

    public List<AjaxData> getCorrctAuthTCList(String exam, String subject, String stat, String type, String userId) {
        return this.examDao.getCorrctAuthTCList(exam, subject, stat, type, userId);
    }

    public boolean isManager(String userId) {
        return this.examDao.isManager(userId);
    }

    public List<AjaxData> getCorrectSchoolList_zy(String exam, String subject, String stat, String type, String systemType) {
        return this.examDao.getCorrectSchoolList_zy(exam, subject, stat, type, systemType);
    }

    public List<AjaxData> getCorrctSchoolList_EP(String exam, String subject, String stat, String type, String systemType, String examplace, String grade) {
        return this.examDao.getCorrctSchoolList_EP(exam, subject, stat, type, systemType, examplace, grade);
    }

    public List<AjaxData> getCorrctAuthSchoolList_EP(String exam, String subject, String stat, String type, String userId, String examplace, String grade) {
        return this.examDao.getCorrctAuthSchoolList_EP(exam, subject, stat, type, userId, examplace, grade);
    }

    public List<Define> getSQuestionNum(String exampaperNum, String gradeNum, String classnum) {
        return this.examDao.getSQuestionNum(exampaperNum, gradeNum, classnum);
    }

    public List<Task> getSTeacherNum(String exampaperNum, String gradeNum, String classnum, String questionNum) {
        return this.examDao.getSTeacherNum(exampaperNum, gradeNum, classnum, questionNum);
    }

    public List<Task> getSTeacherNum2(String exampaperNum, String gradeNum, String classnum, String questionNum, String groupType) {
        return this.examDao.getSTeacherNum2(exampaperNum, gradeNum, classnum, questionNum, groupType);
    }

    public List<Student> getSpotCheck_stuInfo(String scoreId, String examPaperNum, String questionNum) {
        return this.examDao.getSpotCheck_stuInfo(scoreId, examPaperNum, questionNum);
    }

    public List<Task> getSpotCheck(Integer examnum, Integer gradeNum, Integer subjectnum, String questionNum, Integer updateUser, Integer snumber, Integer pagestart, Integer pageSize, String examPaperNum) {
        return this.examDao.getSpotCheck(examnum, gradeNum, subjectnum, questionNum, updateUser, snumber, pagestart, pageSize, examPaperNum);
    }

    public List<Task> getSpotCheck3(Integer examnum, Integer gradeNum, Integer subjectnum, String questionNum, String updateUser, Integer snumber, Integer pagestart, Integer pageSize, String examPaperNum, String fenshuduan1, String fenshuduan2, String examineestatu) {
        return this.examDao.getSpotCheck3(examnum, gradeNum, subjectnum, questionNum, updateUser, snumber, pagestart, pageSize, examPaperNum, fenshuduan1, fenshuduan2, examineestatu);
    }

    public List<Task> getSpotCheck2(Integer exampaperNum, String questionNum, Integer studentId, String scoreId) {
        return this.examDao.getSpotCheck2(exampaperNum, questionNum, studentId, scoreId);
    }

    public List<Task> getSpotCheck2_detail(Integer exampaperNum, String questionNum, Integer studentId, String scoreId) {
        return this.examDao.getSpotCheck2_detail(exampaperNum, questionNum, studentId, scoreId);
    }

    public List<Task> getOneTwo(String examnum, String gradeNum, String subjectnum, String questionNum) {
        return this.examDao.getOneTwo(examnum, gradeNum, subjectnum, questionNum);
    }

    public List<Define> getSpotCheckFullScore(Integer examnum, Integer gradeNum, Integer subjectnum, String questionNum) {
        return this.examDao.getSpotCheckFullScore(examnum, gradeNum, subjectnum, questionNum);
    }

    public List<Score> getSpotCheckScore(Integer examnum, Integer gradeNum, Integer subjectnum, String scoreId, String questionnum) {
        return this.examDao.getSpotCheckScore(examnum, gradeNum, subjectnum, scoreId, questionnum);
    }

    public List<ExamineeStuRecord> getExamineeRecord(Integer examnum, Integer gradeNum, Integer subjectnum, String scoreId, String questionnum) {
        return this.examDao.getExamineeRecord(examnum, gradeNum, subjectnum, scoreId, questionnum);
    }

    public Integer addExamineeRecord(ExamineeStuRecord examineeStuRecord) {
        return this.examDao.addExamineeRecord(examineeStuRecord);
    }

    public List<Remark> getSpotCheckRemark(Integer examnum, Integer gradeNum, Integer subjectnum, String scoreId, String questionnum) {
        return this.examDao.getSpotCheckRemark(examnum, gradeNum, subjectnum, scoreId, questionnum);
    }

    public List<Exam> getExamNameByNum(String examNum) {
        return this.examDao.getExamNameByNum(examNum);
    }

    public List<Subject> getSubjectNameByNum(String subjectNum) {
        return this.examDao.getSubjectNameByNum(subjectNum);
    }

    public Object getClassNameByNum(String classNum, String gradeNum) {
        return this.examDao.getClassNameByNum(classNum, gradeNum);
    }

    public Object getGradeNameByNum(String schoolNum, String gradeNum) {
        return this.examDao.getGradeNameByNum(schoolNum, gradeNum);
    }

    public Object getSchoolNameByNum(String schoolNum) {
        return this.examDao.getSchoolNameByNum(schoolNum);
    }

    public List<Class> gets_pclassNum(String schoolNum, String gradeNum) {
        return this.examDao.gets_pclassNum(schoolNum, gradeNum);
    }

    public List<Remark> getSpotC(String exampaperNum, String questionNum, String schoolNum, String classNum, String gradeNum, String userNum, String scoreId) {
        return this.examDao.getSpotC(exampaperNum, questionNum, schoolNum, classNum, gradeNum, userNum, scoreId);
    }

    public List<MarkError> getSpotC_MarkError(String exampaperNum, String questionNum, String schoolNum, String classNum, String gradeNum, String userNum, String scoreId) {
        return this.examDao.getSpotC_MarkError(exampaperNum, questionNum, schoolNum, classNum, gradeNum, userNum, scoreId);
    }

    public int yanz(String exampaperNum, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum, String type, String userNum) {
        return this.examDao.yanz(exampaperNum, questionNum, studentId, schoolNum, classNum, gradeNum, type, userNum);
    }

    public int yanz1(Integer exampaperNum, String questionNum, String scoreId, Integer type, String userNum) {
        return this.examDao.yanz1(exampaperNum, questionNum, scoreId, type, userNum);
    }

    public List<Student> getStudent(String StudentId, String schoolNum, String gradeNum, String classNum) {
        return this.examDao.getStudent(StudentId, schoolNum, gradeNum, classNum);
    }

    public List<QuestionGroup_question> getQuestionGroupScheDule(Integer examnum, Integer gradeNum, Integer subjectnum, Integer examPaperNum) {
        return this.examDao.getQuestionGroupScheDule(examnum, gradeNum, subjectnum, examPaperNum);
    }

    public List<QuestionGroup_question> getQuestionGroupScheDule1(String examnum, String gradeNum, String subjectnum) {
        return this.examDao.getQuestionGroupScheDule1(examnum, gradeNum, subjectnum);
    }

    public List<QuestionGroup_question> getQuestionGroupSD(Integer examnum, Integer gradeNum, Integer subjectnum) {
        return this.examDao.getQuestionGroupSD(examnum, gradeNum, subjectnum);
    }

    public List jiancha(Integer examnum, Integer gradeNum, Integer subjectnum, Integer examPaperNum) {
        return this.examDao.jiancha(examnum, gradeNum, subjectnum, examPaperNum);
    }

    public Object getGroupNameBynum(String groupNum) {
        return this.examDao.getGroupNameBynum(groupNum);
    }

    public List<QuestionGroup_question> getGroupSize(String exampaperNum, String groupNum) {
        return this.examDao.getGroupSize(exampaperNum, groupNum);
    }

    public List<QuestionGroup_question> getQuesTask(Integer examnum, Integer gradeNum, Integer subjectnum, Integer examPaperNum) {
        return this.examDao.getQuesTask(examnum, gradeNum, subjectnum, examPaperNum);
    }

    public Object getStudentSize(String exampaperNum, String questionNum, String schoolNum, String gradeNum) {
        return this.examDao.getStudentSize(exampaperNum, questionNum, schoolNum, gradeNum);
    }

    public List<Questiongroup_mark_setting> getMarkTypeSche(String exampaperNum, String questionGroupNum) {
        return this.examDao.getMarkTypeSche(exampaperNum, questionGroupNum);
    }

    public Object getPaperNum(String examPaperNum, String questionNum, String schoolNum, String gradeNum) {
        return this.examDao.getPaperNum(examPaperNum, questionNum, schoolNum, gradeNum);
    }

    public Object getRemarkNum(String examPaperNum, String questionNum, String schoolNum, String gradeNum, int type) {
        return this.examDao.getRemarkNum(examPaperNum, questionNum, schoolNum, gradeNum, type);
    }

    public List<AjaxData> getCorrectSubjectList(String exam, String stat, String type, String systemType) {
        return this.examDao.getCorrectSubjectList(exam, stat, type, systemType);
    }

    public List<AjaxData> getCorrectSubjectList_zy(String exam, String stat, String type, String systemType) {
        return this.examDao.getCorrectSubjectList_zy(exam, stat, type, systemType);
    }

    public List<AjaxData> getCorrectSubjectList_zyyh(String exam, String stat, String type, String systemType) {
        return this.examDao.getCorrectSubjectList_zyyh(exam, stat, type, systemType);
    }

    public List<AjaxData> getCorrectExaminationRoomStudent(String exam, String testCenter, String grade, String examRoomNum, String exampaperNum, String examroomornot, String subject) {
        return this.examDao.getCorrectExaminationRoomStudent(exam, testCenter, grade, examRoomNum, exampaperNum, examroomornot, subject);
    }

    public List<AjaxData> getCorrectClassStudent(String exam, String school, String grade, String classNum, String exampaperNum) {
        return this.examDao.getCorrectClassStudent(exam, school, grade, classNum, exampaperNum);
    }

    public Integer updateScore(String[] id, double score, boolean moreThanFullMarks) {
        return this.examDao.updateScore(id, score, moreThanFullMarks);
    }

    public Integer updateCorrectStatus(String status, String examination, String exam, String subject, String grade, String loginUserId, String testCenter) {
        return this.examDao.updateCorrectStatus(status, examination, exam, subject, grade, loginUserId, testCenter);
    }

    public Integer SYNCCorrectingInfo() {
        return this.examDao.SYNCCorrectingInfo();
    }

    public CorrectInfo imageDetailInfo(CorrectInfo c) {
        return this.examDao.imageDetailInfo(c);
    }

    public List<CorrectInfo> getImageNotToScoreList(String examPaperNum, String school, String examinationRoom) {
        return this.examDao.getImageNotToScoreList(examPaperNum, school, examinationRoom);
    }

    public double getFullScore(String exampaperNum, String questionNum) {
        return this.examDao.getFullScore(exampaperNum, questionNum);
    }

    public String getCorrectStatus(String examination, String exam, String subject, String grade, String type) {
        Object obj = this.examDao.getCorrectStatus(examination, exam, subject, grade, type, "");
        if (null != obj) {
            return obj.toString();
        }
        return null;
    }

    public String commbinPreNum(String id) {
        return this.examDao.commbinPreNum(id);
    }

    public Integer updateExamineeNum(String preNum, String[] ids, String[] nums, String exam, String grade, String examRoom, String subject) throws Exception {
        String exampaperNum = this.examDao.getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
        updateData(preNum, ids, nums, exam, grade, examRoom, exampaperNum);
        FoundRepeat(exampaperNum, examRoom);
        if (getCountOfExceptionData(exampaperNum, examRoom).intValue() == 0) {
            updateImageAndScore(exampaperNum, examRoom);
            return 1;
        }
        return 2;
    }

    public void updateData(String preNum, String[] ids, String[] nums, String exam, String grade, String examRoom, String exampaperNum) throws Exception {
        List<String> sqls = new ArrayList<>();
        Integer num = getNumsOfStudentByExamroomId(examRoom);
        String str = "0";
        if (null != num && num.intValue() >= 100) {
            str = "";
        }
        for (int i = 0; i < ids.length; i++) {
            Map args = new HashMap();
            args.put("exam", exam);
            args.put("grade", grade);
            args.put("exampaperNum", exampaperNum);
            args.put("examRoom", examRoom);
            String sql = "";
            String id = ids[i].trim();
            nums[i].trim();
            args.put("id", id);
            if (null != nums[i] && !nums[i].equals("")) {
                String examineeNum = preNum.trim() + str + nums[i].trim();
                args.put("examineeNum", examineeNum);
                args.put("TYPE_EXAMINEENUM_ERROR_INVALID", "0");
                sql = !authexamineeNumExists(exam, grade, examRoom, examineeNum) ? "UPDATE regexaminee SET isModify= 'T', cNum = studentId , type = {TYPE_EXAMINEENUM_ERROR_INVALID} WHERE id = {id} " : "UPDATE regexaminee SET isModify= 'T' " + str + " , cNum = (SELECT studentID FROM examinationnum WHERE examNum={exam} AND gradeNum={grade} AND examineeNum={examineeNum} ) WHERE id = {id} ";
            }
            if (!sql.equals("")) {
                this.dao2._execute(sql, args);
            }
        }
        Map args2 = new HashMap();
        args2.put("exampaperNum", exampaperNum);
        args2.put("examRoom", examRoom);
        this.dao2._execute("UPDATE regexaminee SET cNum = studentId WHERE  examPaperNum={exampaperNum} AND examinationRoomNum={examRoom}  AND type is  NULL and isModify is null ", args2);
        if (sqls.size() > 0) {
        }
        sqls.clear();
    }

    public void FoundRepeat(String exampaperNum, String examroom) throws Exception {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("examroom", examroom);
        args.put("TYPE_EXAMINEENUM_ERROR_REPEAT", "1");
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT  id,examPaperNum,studentId,cNum,examinationRoomNum,page FROM  (SELECT id,examPaperNum,studentId,cNum,examinationRoomNum,page  FROM regexaminee \tWHERE examPaperNum={exampaperNum} AND examinationRoomNum={examroom} and cNum is not null) s GROUP BY examPaperNum,cNum,page HAVING COUNT(1)>1", Examinationnumimg.class, args);
        if (null == _queryBeanList || _queryBeanList.size() == 0) {
            return;
        }
        Iterator<?> it = _queryBeanList.iterator();
        while (it.hasNext()) {
            Examinationnumimg img = (Examinationnumimg) it.next();
            args.put("cNum", img.getcNum());
            this.dao2._execute("update regexaminee set type={TYPE_EXAMINEENUM_ERROR_REPEAT}  WHERE examPaperNum={exampaperNum} AND examinationRoomNum={examroom} and cNum={cNum} and page={cNum} ", args);
        }
    }

    public Integer updateImageAndScore(String exampaperNum, String examroom) throws Exception {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("examroom", examroom);
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT   studentId,cNum,page FROM regexaminee WHERE isModify is not NULL and type is null AND examPaperNum={exampaperNum}  AND examinationRoomNum={examroom} ", Examinationnumimg.class, args);
        if (null == _queryBeanList) {
            return 1;
        }
        Iterator<?> it = _queryBeanList.iterator();
        while (it.hasNext()) {
            Examinationnumimg img = (Examinationnumimg) it.next();
            Student s = (Student) getOneByNum(Const.EXPORTREPORT_studentId, img.getcNum(), Student.class);
            this.examManageDAO.modifyError(null, img.getStudentId(), s, exampaperNum, img.getPage());
        }
        return null;
    }

    public Integer getCountOfExceptionData(String exampaperNum, String examroom) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("examroom", examroom);
        Object obj = this.dao2._queryObject("SELECT   COUNT(1)  FROM  regexaminee WHERE   examPaperNum={exampaperNum} AND examinationRoomNum={examroom} AND type is not NULL", args);
        if (null == obj) {
            return 0;
        }
        return Integer.valueOf(Integer.parseInt(obj.toString()));
    }

    public Integer getNumsOfStudentByExamroomId(String id) throws Exception {
        return this.examDao.getNumsOfStudentByExamroomId(id);
    }

    public boolean authexamineeNumExists(String exam, String grade, String examroom, String examineeNum) throws Exception {
        return this.examDao.authexamineeNumExists(exam, grade, examroom, examineeNum);
    }

    public boolean authScoreExists(String exampaperNum, String studentId, String page) throws Exception {
        return this.examDao.authScoreExists(exampaperNum, studentId, page);
    }

    public String authError(String exampaperNum, String examroom, String school, String studentId, String page, String id) throws Exception {
        Object re;
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put(License.SCHOOL, school);
        args.put("examroom", examroom);
        args.put("page", page);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("id", id);
        Object obj = this.dao2._queryObject("SELECT id from examineenumerror WHERE examPaperNum={exampaperNum} AND schoolNum={school} AND examinationRoomNum={examroom} AND page={page} AND studentId={studentId} ", args);
        if (null != obj) {
            String ss = obj.toString();
            return ss;
        }
        Integer returnCode = Integer.valueOf(this.dao2._execute("INSERT INTO examineenumerror(examPaperNum,schoolNum,examinationRoomNum,page,studentId,insertUser,insertDate,errorType,examineeNum)SELECT examPaperNum,schoolNum,examinationRoomNum,page,studentId,insertUser,now(),'1','' FROM regexaminee WHERE id = {id} ", args));
        if (null != returnCode && null != (re = this.dao2._queryObject("SELECT e.id FROM regexaminee r LEFT join examineenumerror e ON e.examPaperNum= r.examPaperNum AND r.schoolNum=e.schoolNum AND r.examinationRoomNum=e.examinationRoomNum AND r.page=e.page AND r.studentId=e.studentId WHERE r.id={id} ", args))) {
            return re.toString();
        }
        return null;
    }

    public Integer updateRegStudentIdById(String id, String examineeNum, String exam, String grade, String examroom, String type) throws Exception {
        String sql = "UPDATE regexaminee SET studentId = (SELECT studentID FROM examinationnum WHERE examNum={exam} AND gradeNum={grade} AND examinationRoomNum={examroom} AND examineeNum={examineeNum} ) where id={id} ";
        if (null != type) {
            sql = "UPDATE regexaminee SET studentId ={examineeNum}  where id={id} ";
        }
        this.log.info("updateRegStudentIdById 修改考号相关记录之后 修改识别考号结果表 studentId  sql" + sql);
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("examroom", examroom);
        args.put("examineeNum", examineeNum);
        args.put("id", id);
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    public Integer updateRegStudentId(String oldStudentId, String newStudentId, String exampaperNum, String examroom, String page) throws Exception {
        this.log.info("updateRegStudentId 修改考号相关记录之后 修改识别考号结果表 studentId  sqlUPDATE regexaminee SET studentId= {newStudentId} WHERE examPaperNum={exampaperNum}  AND examinationRoomNum={examroom} AND studentId={oldStudentId} AND page={page} ");
        Map args = new HashMap();
        args.put("newStudentId", newStudentId);
        args.put("exampaperNum", exampaperNum);
        args.put("examroom", examroom);
        args.put("oldStudentId", oldStudentId);
        args.put("page", page);
        return Integer.valueOf(this.dao2._execute("UPDATE regexaminee SET studentId= {newStudentId} WHERE examPaperNum={exampaperNum}  AND examinationRoomNum={examroom} AND studentId={oldStudentId} AND page={page} ", args));
    }

    public Integer updateCorrectNumsStat(String status, String examination, String exam, String subject, String grade, String loginUserId, String testCenter, String examroomornot) {
        return this.examDao.updateCorrectNumsStat(status, examination, exam, subject, grade, loginUserId, testCenter, examroomornot);
    }

    public Define getQuesionDefineByExampaperNumAndQuesionNum(String epNum, String questionNum) {
        try {
            Map args = new HashMap();
            args.put("epNum", epNum);
            args.put("questionNum", questionNum);
            return (Define) this.dao2._queryBean("SELECT   DISTINCT   d.id,d.examPaperNum,d.questionNum,d.fullScore,d.questionType,d.difficult,d.inspectionlevel,d.one1,d.one2,d.one3,d.one4,d.one5,d.one6,d.one7,d.one8,d.one9,d.one10,d.one11,d.one12,d.one13,d.one14,d.one15,d.one16,d.one17,d.one18,d.one19,d.one20,d.one21,d.one22,d.one23,d.one24,d.one25,d.one26,d.multiple,d.errorRate,d.hasErrorSection,d.lengout,d.category,d.optionCount,d.category, d.deduction,CASE WHEN i.exampaperType='B' THEN d.answer_b  ELSE d.answer  END AS answer FROM (SELECT id,examPaperNum,questionNum,fullScore,questionType,difficult,inspectionlevel,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26, multiple,errorRate,hasErrorSection,lengout,category,optionCount,deduction,answer,answer_b  FROM define WHERE examPaperNum={epNum} AND id={questionNum} AND isParent!='1' AND choosename!='T' UNION ALL SELECT id,examPaperNum,questionNum,fullScore,questionType,difficult,inspectionlevel,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26, multiple,errorRate,hasErrorSection,lengout,category,optionCount,deduction,answer,answer_b  FROM subdefine WHERE examPaperNum={epNum} AND id={questionNum}) d LEFT JOIN illegal i ON  d.examPaperNum=i.examPaperNum WHERE d.examPaperNum={epNum} AND d.id={questionNum} ", Define.class, args);
        } catch (Exception e) {
            this.log.info("获取试题定义 getQuesionDefineByExampaperNumAndQuesionNum() ", e);
            e.printStackTrace();
            return null;
        }
    }

    public Define getQuesionDefineByExampaperNumAndQuesionNum1(String epNum, String questionNum, String stuid) {
        try {
            Map args = new HashMap();
            args.put("epNum", epNum);
            args.put("questionNum", questionNum);
            args.put("stuid", stuid);
            return (Define) this.dao2._queryBean("SELECT   DISTINCT   d.id,d.examPaperNum,d.questionNum,d.fullScore,d.questionType, d.difficult,d.inspectionlevel, CASE WHEN i.exampaperType='B' THEN d.one1b ELSE d.one1 END AS one1, CASE WHEN i.exampaperType='B' THEN d.one2b ELSE d.one2 END AS one2, CASE WHEN i.exampaperType='B' THEN d.one3b ELSE d.one3 END AS one3, CASE WHEN i.exampaperType='B' THEN d.one4b ELSE d.one4 END AS one4, CASE WHEN i.exampaperType='B' THEN d.one5b ELSE d.one5 END AS one5, CASE WHEN i.exampaperType='B' THEN d.one6b ELSE d.one6 END AS one6, CASE WHEN i.exampaperType='B' THEN d.one7b ELSE d.one7 END AS one7, CASE WHEN i.exampaperType='B' THEN d.one8b ELSE d.one8 END AS one8, CASE WHEN i.exampaperType='B' THEN d.one9b ELSE d.one9 END AS one9, CASE WHEN i.exampaperType='B' THEN d.one10b ELSE d.one10 END AS one10, CASE WHEN i.exampaperType='B' THEN d.one11b ELSE d.one11 END AS one11, CASE WHEN i.exampaperType='B' THEN d.one12b ELSE d.one12 END AS one12, CASE WHEN i.exampaperType='B' THEN d.one13b ELSE d.one13 END AS one13, CASE WHEN i.exampaperType='B' THEN d.one14b ELSE d.one14 END AS one14, CASE WHEN i.exampaperType='B' THEN d.one15b ELSE d.one15 END AS one15, CASE WHEN i.exampaperType='B' THEN d.one16b ELSE d.one16 END AS one16, CASE WHEN i.exampaperType='B' THEN d.one17b ELSE d.one17 END AS one17, CASE WHEN i.exampaperType='B' THEN d.one18b ELSE d.one18 END AS one18, CASE WHEN i.exampaperType='B' THEN d.one19b ELSE d.one19 END AS one19, CASE WHEN i.exampaperType='B' THEN d.one20b ELSE d.one20 END AS one20, CASE WHEN i.exampaperType='B' THEN d.one21b ELSE d.one21 END AS one21, CASE WHEN i.exampaperType='B' THEN d.one22b ELSE d.one22 END AS one22, CASE WHEN i.exampaperType='B' THEN d.one23b ELSE d.one23 END AS one23, CASE WHEN i.exampaperType='B' THEN d.one24b ELSE d.one24 END AS one24, CASE WHEN i.exampaperType='B' THEN d.one25b ELSE d.one25 END AS one25, CASE WHEN i.exampaperType='B' THEN d.one26b ELSE d.one26 END AS one26, d.multiple, d.errorRate,d.hasErrorSection,d.lengout,d.category,d.optionCount,d.category, d.deduction,CASE WHEN i.exampaperType='B' THEN d.answer_b  ELSE d.answer  END AS answer FROM (SELECT id,examPaperNum,questionNum,fullScore,questionType,difficult,inspectionlevel,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26,one1b,one2b,one3b,one4b,one5b,one6b,one7b,one8b,one9b,one10b,one11b,one12b,one13b,one14b,one15b,one16b,one17b,one18b,one19b,one20b,one21b,one22b,one23b,one24b,one25b,one26b, multiple,errorRate,hasErrorSection,lengout,category,optionCount,deduction,answer,answer_b  FROM define WHERE examPaperNum={epNum} AND id={questionNum} AND isParent!='1' AND choosename!='T' UNION ALL SELECT id,examPaperNum,questionNum,fullScore,questionType,difficult,inspectionlevel,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26,one1b,one2b,one3b,one4b,one5b,one6b,one7b,one8b,one9b,one10b,one11b,one12b,one13b,one14b,one15b,one16b,one17b,one18b,one19b,one20b,one21b,one22b,one23b,one24b,one25b,one26b, multiple,errorRate,hasErrorSection,lengout,category,optionCount,deduction,answer,answer_b  FROM subdefine WHERE examPaperNum={epNum} AND id={questionNum} ) d LEFT JOIN illegal i ON  d.examPaperNum=i.examPaperNum AND i.studentId={stuid}   WHERE d.examPaperNum={epNum} AND d.id={questionNum} ", Define.class, args);
        } catch (Exception e) {
            this.log.info("获取试题定义 getQuesionDefineByExampaperNumAndQuesionNum() ", e);
            e.printStackTrace();
            return null;
        }
    }

    public String updateAnswer(String epNum, String questionNum, String answer, String studentId, String scoreId, int val) {
        if (answer.equals(Const.WHITE_CHAR)) {
            answer = "";
        }
        Double.valueOf(0.0d);
        Define defineVal = getQuesionDefineByExampaperNumAndQuesionNum1(epNum, questionNum, studentId);
        Map<String, Object> defineMap = new HashMap<>();
        defineMap.put("id", defineVal.getId());
        defineMap.put("one1", defineVal.getOne1());
        defineMap.put("one2", defineVal.getOne2());
        defineMap.put("one3", defineVal.getOne3());
        defineMap.put("one4", defineVal.getOne4());
        defineMap.put("one5", defineVal.getOne5());
        defineMap.put("one6", defineVal.getOne6());
        defineMap.put("one7", defineVal.getOne7());
        defineMap.put("one8", defineVal.getOne8());
        defineMap.put("one9", defineVal.getOne9());
        defineMap.put("one10", defineVal.getOne10());
        defineMap.put("one11", defineVal.getOne11());
        defineMap.put("one12", defineVal.getOne12());
        defineMap.put("one13", defineVal.getOne13());
        defineMap.put("one14", defineVal.getOne14());
        defineMap.put("one15", defineVal.getOne15());
        defineMap.put("one16", defineVal.getOne16());
        defineMap.put("one17", defineVal.getOne17());
        defineMap.put("one18", defineVal.getOne18());
        defineMap.put("one19", defineVal.getOne19());
        defineMap.put("one20", defineVal.getOne20());
        defineMap.put("one21", defineVal.getOne21());
        defineMap.put("one22", defineVal.getOne22());
        defineMap.put("one23", defineVal.getOne23());
        defineMap.put("one24", defineVal.getOne24());
        defineMap.put("one25", defineVal.getOne25());
        defineMap.put("one26", defineVal.getOne26());
        defineMap.put("lengout", defineVal.getLengout());
        defineMap.put("deduction", defineVal.getDeduction());
        defineMap.put("fullScore", defineVal.getFullScore());
        defineMap.put("hasErrorSection", defineVal.getHasErrorSection());
        defineMap.put("multiple", defineVal.getMultiple());
        defineMap.put("answer", defineVal.getAnswer());
        defineMap.put("inspectionlevel", defineVal.getInspectionlevel());
        Double d = Double.valueOf(Util.suitAllObjSingleJudge(answer, defineMap));
        Integer returnCode = updateAnswerAndScoreToDB(epNum, questionNum, answer, studentId, d.doubleValue(), scoreId, val);
        if (null == returnCode) {
            return null;
        }
        return String.valueOf(d);
    }

    public double countScore(String answer, String questionNum, String examPaperNum, String scoreId) {
        Double.valueOf(0.0d);
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        args.put("scoreId", scoreId);
        String stuid = this.dao2._queryStr("select studentId from objectivescore where questionNum = {questionNum} and id= {scoreId}  ", args);
        Define defineVal = getQuesionDefineByExampaperNumAndQuesionNum1(examPaperNum, questionNum, stuid);
        Map<String, Object> defineMap = new HashMap<>();
        defineMap.put("id", defineVal.getId());
        defineMap.put("one1", defineVal.getOne1());
        defineMap.put("one2", defineVal.getOne2());
        defineMap.put("one3", defineVal.getOne3());
        defineMap.put("one4", defineVal.getOne4());
        defineMap.put("one5", defineVal.getOne5());
        defineMap.put("one6", defineVal.getOne6());
        defineMap.put("one7", defineVal.getOne7());
        defineMap.put("one8", defineVal.getOne8());
        defineMap.put("one9", defineVal.getOne9());
        defineMap.put("one10", defineVal.getOne10());
        defineMap.put("one11", defineVal.getOne11());
        defineMap.put("one12", defineVal.getOne12());
        defineMap.put("one13", defineVal.getOne13());
        defineMap.put("one14", defineVal.getOne14());
        defineMap.put("one15", defineVal.getOne15());
        defineMap.put("one16", defineVal.getOne16());
        defineMap.put("one17", defineVal.getOne17());
        defineMap.put("one18", defineVal.getOne18());
        defineMap.put("one19", defineVal.getOne19());
        defineMap.put("one20", defineVal.getOne20());
        defineMap.put("one21", defineVal.getOne21());
        defineMap.put("one22", defineVal.getOne22());
        defineMap.put("one23", defineVal.getOne23());
        defineMap.put("one24", defineVal.getOne24());
        defineMap.put("one25", defineVal.getOne25());
        defineMap.put("one26", defineVal.getOne26());
        defineMap.put("lengout", defineVal.getLengout());
        defineMap.put("deduction", defineVal.getDeduction());
        defineMap.put("fullScore", defineVal.getFullScore());
        defineMap.put("hasErrorSection", defineVal.getHasErrorSection());
        defineMap.put("multiple", defineVal.getMultiple());
        defineMap.put("answer", defineVal.getAnswer());
        defineMap.put("inspectionlevel", defineVal.getInspectionlevel());
        Double d = Double.valueOf(Util.suitAllObjSingleJudge(answer, defineMap));
        return d.doubleValue();
    }

    public double countScore1(String answer, String questionNum, String examPaperNum, String stuid) {
        Double.valueOf(0.0d);
        Define defineVal = getQuesionDefineByExampaperNumAndQuesionNum1(examPaperNum, questionNum, stuid);
        Map<String, Object> defineMap = new HashMap<>();
        defineMap.put("id", defineVal.getId());
        defineMap.put("one1", defineVal.getOne1());
        defineMap.put("one2", defineVal.getOne2());
        defineMap.put("one3", defineVal.getOne3());
        defineMap.put("one4", defineVal.getOne4());
        defineMap.put("one5", defineVal.getOne5());
        defineMap.put("one6", defineVal.getOne6());
        defineMap.put("one7", defineVal.getOne7());
        defineMap.put("one8", defineVal.getOne8());
        defineMap.put("one9", defineVal.getOne9());
        defineMap.put("one10", defineVal.getOne10());
        defineMap.put("one11", defineVal.getOne11());
        defineMap.put("one12", defineVal.getOne12());
        defineMap.put("one13", defineVal.getOne13());
        defineMap.put("one14", defineVal.getOne14());
        defineMap.put("one15", defineVal.getOne15());
        defineMap.put("one16", defineVal.getOne16());
        defineMap.put("one17", defineVal.getOne17());
        defineMap.put("one18", defineVal.getOne18());
        defineMap.put("one19", defineVal.getOne19());
        defineMap.put("one20", defineVal.getOne20());
        defineMap.put("one21", defineVal.getOne21());
        defineMap.put("one22", defineVal.getOne22());
        defineMap.put("one23", defineVal.getOne23());
        defineMap.put("one24", defineVal.getOne24());
        defineMap.put("one25", defineVal.getOne25());
        defineMap.put("one26", defineVal.getOne26());
        defineMap.put("lengout", defineVal.getLengout());
        defineMap.put("deduction", defineVal.getDeduction());
        defineMap.put("fullScore", defineVal.getFullScore());
        defineMap.put("hasErrorSection", defineVal.getHasErrorSection());
        defineMap.put("multiple", defineVal.getMultiple());
        defineMap.put("answer", defineVal.getAnswer());
        defineMap.put("inspectionlevel", defineVal.getInspectionlevel());
        Double d = Double.valueOf(Util.suitAllObjSingleJudge(answer, defineMap));
        return d.doubleValue();
    }

    public Integer updateAnswerAndScoreToDB(String epNum, String questionNum, String answer, String studentId, double score, String scoreId, int val) {
        return this.examDao.updateAnswerAndScoreToDB(epNum, questionNum, answer, studentId, score, scoreId, val);
    }

    public List<GeneralCorrectData> getGeneralCorrectData(String exam, String subject, String grade, String school) {
        return this.examDao.getGeneralCorrectData(exam, subject, grade, school);
    }

    public String getScoreModelByExam(String exam, String type) {
        return this.examDao.getScoreModelByExam(exam, type);
    }

    public double getFullScoreByExampaperAndQuestionNum(String exampaperNum, String questionNum) {
        return this.examDao.getFullScoreByExampaperAndQuestionNum(exampaperNum, questionNum);
    }

    public Define getDefineByExampaperNumAndQuestionNum(String exampaperNum, String questionNum) {
        return this.examDao.getDefineByExampaperNumAndQuestionNum(null == exampaperNum ? 0 : Integer.parseInt(exampaperNum), questionNum);
    }

    public String getQuestionScoreByQuestionInfo(String epNum, String questionNum, String score) {
        return this.examDao.getQuestionScoreByQuestionInfo(null == epNum ? 0 : Integer.parseInt(epNum), questionNum, score);
    }

    public List getSpotCheckChild(String exampaperNum, String questionnum, String scoreId) {
        return this.examDao.getSpotCheckChild(exampaperNum, questionnum, scoreId);
    }

    public RegExaminee changePage(String schoolNum, String exampaperNum, String studentId, String oldPage, String newPage, String xmlPath, String rotation, String loginUser, String paperType, String typeAB, String regId) {
        if (null == regId || "".equals(regId)) {
            return null;
        }
        RegExaminee regOld = getRegExaminee(regId);
        if (null != rotation) {
            rotation.replace("-", "a");
        }
        RegExaminee regObj = this.examManageDAO.authDataExistsFromRegExaminee(paperType, exampaperNum, studentId, schoolNum, Integer.parseInt(oldPage), null, null, null, null, null, regId);
        try {
            if (null != (regObj.getcNum() + "") && !"".equals(regObj.getcNum() + "")) {
                openExe(studentId, loginUser, xmlPath, schoolNum, exampaperNum, oldPage, newPage, rotation, regObj.getcNum() + "", regObj.getType() + "", typeAB);
            } else {
                openExe(studentId, loginUser, xmlPath, schoolNum, exampaperNum, oldPage, newPage, rotation, "null", regObj.getType() + "", typeAB);
            }
        } catch (Exception e) {
            this.log.info("调用exe 修改页码", e);
        }
        boolean result = authExeStatus(regId);
        String rel = "F";
        RegExaminee regNew = getRegExaminee(regId);
        if (result) {
            rel = "T";
            Examlog examlog = new Examlog();
            examlog.setOperate("修改页码-得分表中查到记录" + regNew.getStudentId());
            examlog.setExampaperNum(Integer.valueOf(Integer.parseInt(exampaperNum)));
            examlog.setStudentId(studentId);
            examlog.setExaminationRoomNum(regOld.getExaminationRoomNum());
            examlog.setInsertUser(loginUser);
            examlog.setInsertDate(DateUtil.getCurrentTime());
            this.dao2.save(examlog);
        }
        regNew.setExt4(rel);
        regNew.setOldStudentId(regOld.getStudentId() + "");
        regNew.setOldExamroomId(regOld.getExaminationRoomNum() + "");
        return regNew;
    }

    private RegExaminee getRegExaminee(String regId) {
        Map args = new HashMap();
        args.put("regId", regId);
        return (RegExaminee) this.dao2._queryBean("SELECT r.id,r.examPaperNum,r.schoolNum,r.studentId,r.examinationRoomNum,r.page,r.cNum,r.type,em.examineeNum ext1,s.studentName ext2,room.examinationRoomName ext3,e.examNum exam,e.subjectNum subject,e.gradeNum grade,exam.examinationRoomLength,exam.examineeLength FROM (SELECT * FROM regexaminee WHERE id={regId} ) r LEFT JOIN exampaper e ON e.examPaperNum = r.examPaperNum LEFT JOIN examinationnum  em ON em.studentID = r.studentId  AND e.examNum = em.examNum and  em.subjectNum=e.subjectnum LEFT JOIN examinationroom room ON room.id = em.examinationRoomNum LEFT JOIN student s ON s.id = r.studentId LEFT JOIN exam ON exam.examNum = e.examNum ", RegExaminee.class, args);
    }

    public Examinationnum getExaminationnumInfoByExamNumAndExaminee(String exam, String examroom, String examineeNum) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("examineeNum", examineeNum);
        args.put("examroom", examroom);
        return (Examinationnum) this.dao2._queryBean("SELECT r.studentID,r.examinationRoomNum id,r.schoolNum,s.studentName,em.examinationRoomName FROM ( SELECT studentID,examinationRoomNum,schoolNum FROM examinationnum WHERE examNum={exam} AND examineeNum={examineeNum}  AND examinationRoomNum={examroom} )r LEFT JOIN student s ON r.studentID = s.id LEFT JOIN examinationroom em ON em.id = r.examinationRoomNum ", Examinationnum.class, args);
    }

    public List<Object> getExamRoomPageRank(String epNum, String examroom, String school) {
        Exampaper ep = (Exampaper) this.examDao.getOneByNum("exampaperNum", epNum, Exampaper.class);
        try {
            String sql = (ep.getDoubleFaced() == null || !ep.getDoubleFaced().equals("F")) ? "SELECT CONCAT('[第',CONVERT(page,char),'页：',CONVERT(COUNT(1),char),'张]') pageStr FROM ( SELECT r.page,r.studentId,r.cNum FROM (SELECT page,studentId,cNum,exampaperNum,schoolNum,id  from regexaminee WHERE exampaperNum={epNum} AND examinationRoomNum={examroom}  and schoolNum={school} )r LEFT JOIN student s ON r.studentId = s.id AND r.schoolNum = s.schoolNum LEFT JOIN exampaper e ON e.examPaperNum =  r.examPaperNum  LEFT JOIN cantrecognized c ON c.regId = r.id" : "SELECT CONCAT('[第',CONVERT(page,char),'页：',CONVERT(COUNT(1),char),'张]') pageStr FROM ( SELECT r.page,r.studentId,r.cNum FROM (SELECT page,studentId,cNum,exampaperNum,schoolNum,id  from regexaminee WHERE exampaperNum={epNum} AND examinationRoomNum={examroom}  and schoolNum={school} )r LEFT JOIN student s ON r.studentId = s.id AND r.schoolNum = s.schoolNum LEFT JOIN exampaper e ON e.examPaperNum =  r.examPaperNum  LEFT JOIN cantrecognized c ON c.regId = r.id  ";
            String sql2 = sql + " WHERE c.id IS  NULL AND r.studentId IS NOT NULL ) re GROUP BY re.page having re.page>0 ";
            Map args = new HashMap();
            args.put("epNum", epNum);
            args.put("examroom", examroom);
            args.put(License.SCHOOL, school);
            return this.dao2._queryColList(sql2, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer openExe(String stuId, String username, String settingPath, String schoolNum, String examPaperNum, String oldPage, String newPage, String rotation, String cNum, String type, String typeAB) {
        String filePath = "";
        String exeCommand = "ReClipIntoDB.exe " + stuId + " " + username + " " + schoolNum + " " + examPaperNum + " " + oldPage + " " + newPage + " " + rotation + " " + cNum + " " + type + " " + (settingPath + "/WEB-INF/classes/c3p0.properties") + " " + typeAB;
        try {
            filePath = createfile(settingPath, exeCommand);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Runtime rn = Runtime.getRuntime();
        Thread b = null;
        Thread c = null;
        try {
            try {
                Process p = rn.exec(filePath);
                BufferedInputStream is1 = new BufferedInputStream(p.getInputStream());
                BufferedInputStream is2 = new BufferedInputStream(p.getErrorStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is1));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(is1));
                1 r0 = new 1(this, br);
                r0.start();
                2 r02 = new 2(this, br2);
                r02.start();
                if (p.waitFor() != 0) {
                    if (p.exitValue() == 1) {
                        this.log.error("命令执行失败!");
                    }
                    File f = new File(filePath);
                    f.delete();
                    try {
                        r0.stop();
                        r02.stop();
                        return null;
                    } catch (Exception e) {
                        this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e);
                        e.printStackTrace();
                        return null;
                    }
                }
                is1.close();
                is2.close();
                br.close();
                br2.close();
                File f2 = new File(filePath);
                f2.delete();
                Integer valueOf = Integer.valueOf(p.exitValue());
                try {
                    r0.stop();
                    r02.stop();
                    return valueOf;
                } catch (Exception e2) {
                    this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e2);
                    e2.printStackTrace();
                    return null;
                }
            } catch (Exception e3) {
                this.log.error("重置页码后调用exe文件重新识别图像，执行报错", e3);
                try {
                    b.stop();
                    c.stop();
                    return null;
                } catch (Exception e4) {
                    this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e4);
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
                this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e5);
                e5.printStackTrace();
                return null;
            }
        }
    }

    private String getRegId(String exampaperNum, String studentId, String oldPage, String newPage, String schoolNum, String paperType) {
        String sql = "select id from regexaminee WHERE exampaperNum={exampaperNum} AND studentId={studentId} AND page={oldPage}    ";
        if (schoolNum != null && !"-1".equals(schoolNum)) {
            sql = sql + "AND schoolNum={schoolNum}  ";
        }
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("oldPage", oldPage);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("paperType", paperType);
        Object obj = this.dao2._queryObject(sql + " and  type={paperType} ", args);
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    private boolean authExeStatus(String regId) {
        Map args = new HashMap();
        args.put("regId", regId);
        Object obj = this.dao2._queryObject("SELECT COUNT(c.questionNum)   FROM (  SELECT s.questionNum   FROM (SELECT examPaperNum,studentId,page FROM regexaminee  WHERE id={regId} )r    LEFT JOIN  score s  ON s.regId = r.id      WHERE s.questionNum IS not NULL    UNION  ALL   SELECT s.questionNum     FROM (SELECT examPaperNum,studentId,page FROM regexaminee  WHERE id={regId} )r    LEFT JOIN  objectivescore s  ON s.regId = r.id      WHERE s.questionNum IS not NULL) c", args);
        int count = (obj == null ? null : Integer.valueOf(Integer.parseInt(obj.toString()))).intValue();
        if (count == 0) {
            return false;
        }
        return true;
    }

    public Integer deletePageErrorInfo(String schoolNum, String exampaperNum, String studentId, String page) {
        List<String> sqls = new ArrayList<>();
        if (page.equals("1")) {
            sqls.add("DELETE FROM illegal WHERE exampaperNum={exampaperNum}   AND studentId={studentId}   AND schoolNum={schoolNum} ");
        }
        sqls.add("DELETE FROM score WHERE exampaperNum={exampaperNum} AND studentId={studentId} AND page={page} AND schoolNum={schoolNum} ");
        sqls.add("DELETE FROM examinationnumimg WHERE exampaperNum={exampaperNum}  AND studentId={studentId}  AND page={page}  AND schoolNum={schoolNum} ");
        sqls.add("DELETE FROM questionimage WHERE exampaperNum={exampaperNum}  AND studentId={studentId}  AND page={page}  AND schoolNum={schoolNum} ");
        sqls.add("DELETE FROM scoreimage WHERE exampaperNum={exampaperNum}  AND studentId={studentId}  AND page={page}  AND schoolNum={schoolNum} ");
        sqls.add("DELETE FROM examineenumerror WHERE exampaperNum={exampaperNum}  AND studentId={studentId}  AND page={page}  AND schoolNum={schoolNum} ");
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("page", page);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        this.dao2._batchExecute(sqls, args);
        insertIntoCannotrecognized(exampaperNum, studentId, page, schoolNum);
        return 1;
    }

    private void insertIntoCannotrecognized(String exampaperNum, String studentId, String page, String schoolNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("page", page);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        this.dao2._execute("INSERT INTO cantrecognized (studentId,examinationRoomNum,examNum,subjectNum,gradeNum,page,insertUser,insertDate) SELECT reg.studentId,reg.examinationRoomNum,e.examNum,e.subjectNum,e.gradeNum,reg.page,'',NOW() FROM ( \tSELECT * FROM regexaminee  WHERE exampaperNum={exampaperNum} AND studentId={studentId} AND page={page} AND schoolNum={schoolNum} )reg LEFT JOIN exampaper e ON reg.examPaperNum = e.examPaperNum", args);
    }

    private String createfile(String path, String exeCommand) {
        File file = null;
        FileOutputStream out = null;
        try {
            try {
                long l = System.currentTimeMillis();
                file = new File(path + File.separator + l + ".bat");
                out = new FileOutputStream(file, true);
                StringBuffer buffer = new StringBuffer();
                buffer.append("cd " + path + " ");
                buffer.append("\r\n");
                buffer.append(exeCommand);
                byte[] b = buffer.toString().getBytes();
                out.write(b);
                try {
                    out.close();
                } catch (IOException e) {
                    this.log.error(" 创建批处理文件 ", e);
                    e.printStackTrace();
                }
            } catch (Exception e2) {
                this.log.error(" 创建批处理文件 ", e2);
                e2.printStackTrace();
                try {
                    out.close();
                } catch (IOException e3) {
                    this.log.error(" 创建批处理文件 ", e3);
                    e3.printStackTrace();
                }
            }
            return file.getAbsolutePath();
        } catch (Throwable th) {
            try {
                out.close();
            } catch (IOException e4) {
                this.log.error(" 创建批处理文件 ", e4);
                e4.printStackTrace();
            }
            throw th;
        }
    }

    public Integer save(Object obj) {
        return Integer.valueOf(this.dao2.save(obj));
    }

    public void changeR(String exam, String grade, String subject, String answer, String questionNum, String fullScore, String one1, String one2, String one3, String one4, String one5, String one6) {
        Map args = new HashMap();
        args.put("answer", answer);
        args.put("fullScore", fullScore);
        args.put("one1", one1);
        args.put("one2", one2);
        args.put("one3", one3);
        args.put("one4", one4);
        args.put("one5", one5);
        args.put("one6", one6);
        args.put("questionNum", questionNum);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        this.dao2._execute("UPDATE define set answer={answer},fullScore={fullScore},one1={one1},one2={one2},one3={one3} ,one4={one4},one5={one5},one6={one6}  where questionNum={questionNum}  and examPaperNum=(select examPaperNum from exampaper  where examNum={exam}  and gradeNum={grade} and subjectNum={subject} )", args);
    }

    public void updateSc(double questionScore, String id) {
        Map args = new HashMap();
        args.put("questionScore", Double.valueOf(questionScore));
        args.put("id", id);
        this.dao2._execute("UPDATE objectivescore set questionScore={questionScore} where id={id} ", args);
    }

    public void updateScz(String exam, String grade, String subject, String questionNum, String fullScore, String id) {
        String examPaperNum = getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
        Map args = new HashMap();
        args.put("SCORE_EXCEPTION_DAFAULT", "-1");
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("fullScore", fullScore);
        args.put("id", id);
        args.put("SCORE_EXCEPTION_NORECOGNIZED", "2");
        args.put("SCORE_EXCEPTION_UPFULLMARKS", "4");
        this.dao2._execute("UPDATE score SET isException={SCORE_EXCEPTION_UPFULLMARKS} WHERE examPaperNum={examPaperNum} AND questionNum={questionNum}   AND questionScore > {fullScore} and id={id}  AND isException!={SCORE_EXCEPTION_NORECOGNIZED} ", args);
        this.dao2._execute("UPDATE score SET isException={SCORE_EXCEPTION_DAFAULT} WHERE examPaperNum={examPaperNum} AND questionNum={questionNum}   AND questionScore <= {fullScore} and id={id} AND isException!={SCORE_EXCEPTION_NORECOGNIZED} ", args);
    }

    public List zscoreList(String exam, String grade, String subject, String questionNum) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("questionNum", questionNum);
        return this.dao2._queryColList("select id from score where examPaperNum=(select examPaperNum from exampaper  where examNum={exam} and gradeNum={grade} and subjectNum={subject}  AND isHidden='F'  ) and questionNum={questionNum} ", args);
    }

    public String getschool(String studentId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        return (String) this.dao2._queryObject("select schoolNum from student where studentId={studentId}", args);
    }

    public Object getoneNote(String examPaperNum, String studentId, String questionNum, String type) {
        String sql = "select  schoolNum,page  from studentpaperimage  WHERE examPaperNum={examPaperNum} AND studentID={studentId} AND page = (select page from define where exampaperNum={examPaperNum} and questionNum = {questionNum})";
        if (null != type && type.equals("F")) {
            sql = "select img from studentpaperimage  WHERE examPaperNum={examPaperNum} AND studentID={studentId} AND page = {questionNum}";
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("questionNum", questionNum);
        return this.dao2._queryBean(sql, Studentpaperimage.class, args);
    }

    public Integer deleteK(String ext1, String questionNum) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        return Integer.valueOf(this.dao2._execute("delete from knowdetail where  questionNum={questionNum}", args));
    }

    public Float getfullScore(String exampaperNum, String questionNum1) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum1", questionNum1);
        return (Float) this.dao2._queryObject("select fullScore from define where exampapernum={exampaperNum} and questionnum={questionNum1} union all  select fullScore from subdefine where exampapernum={exampaperNum} and questionnum={questionNum1}", args);
    }

    public String getEx(String exampaperNum) throws Exception {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        List li = this.dao2._queryBeanList("select category from define where exampaperNum={exampaperNum} union all select category from subdefine where exampaperNum={exampaperNum}", Define.class, args);
        Map args1 = new HashMap();
        args1.put("examPaperNum1", ((Define) li.get(0)).getCategory());
        return (String) this.dao2._queryObject("SELECT r.examPaperNum examPaperNum FROM(SELECT * FROM exampaper WHERE examPaperNum={examPaperNum1} )r LEFT JOIN subject sjt ON sjt.subjectNum=r.subjectNum ORDER BY sjt.id desc", args1);
    }

    public void update_score(String subExamPaperNum, String gradeExamNum, String questionNum1) {
        this.log.info("update_score   sql：UPDATE score SET subExamPaperNum={subExamPaperNum}  WHERE examPaperNum={gradeExamNum} and questionNum={questionNum1}");
        Map args = new HashMap();
        args.put("subExamPaperNum", subExamPaperNum);
        args.put("gradeExamNum", gradeExamNum);
        args.put("questionNum1", questionNum1);
        this.dao2._execute("UPDATE score SET subExamPaperNum={subExamPaperNum}  WHERE examPaperNum={gradeExamNum} and questionNum={questionNum1}", args);
    }

    public void update_objectivescore(String subExamPaperNum, String gradeExamNum, String questionNum1) {
        Map args = new HashMap();
        args.put("subExamPaperNum", subExamPaperNum);
        args.put("gradeExamNum", gradeExamNum);
        args.put("questionNum1", questionNum1);
        this.dao2._execute("UPDATE objectivescore SET subExamPaperNum={subExamPaperNum}  WHERE examPaperNum={gradeExamNum} AND questionNum={questionNum1}", args);
    }

    public void getUpdate_Chosen_update(String examPaperNum, String questionNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        this.dao2._execute("UPDATE define set multiple='0', parent_question='T', child_question='T' ,p_questionNum='0' where exampaperNum={examPaperNum} and questionNum={questionNum}", args);
    }

    public List questionType_Out(String examPaperNum, String questionNum, String subjectNum) throws Exception {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryBeanList("SELECT name FROM questiontype WHERE num=(SELECT questiontype FROM questiontypedetail WHERE examPaperNum={examPaperNum} AND questionNum={questionNum}) AND subjectNum = {subjectNum}", QuestionType.class, args);
    }

    public String questiontypeIn(String questionName, String subjectNum) {
        Map args = new HashMap();
        args.put("questionName", questionName);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return (String) this.dao2._queryObject("SELECT num FROM questiontype WHERE name={questionName} AND subjectNum={subjectNum}", args);
    }

    public String getOptioncount(String examPaperNum) throws Exception {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        List li = this.dao2._queryBeanList("select category from define where exampaperNum={examPaperNum}", Define.class, args);
        Map args2 = new HashMap();
        args2.put("examPaperNum1", ((Define) li.get(0)).getCategory());
        return (String) this.dao2._queryObject("select MAX(optioncount)  from define where exampaperNum={exampaperNum1}", args2);
    }

    public String getDefine(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return (String) this.dao2._queryObject("select * from define where exampaperNum={examPaperNum}", args);
    }

    public List<Classexam> getCorrectSchoolList_export(String exam, String subject, String stat, String type) {
        return this.examDao.getCorrectSchoolList_export(exam, "", "", "");
    }

    public List<Exampaper> getExamPaperNums(String examNum, String gradeNum) {
        return this.examDao.getExamPaperNums(examNum, gradeNum);
    }

    public String addObjectiveQuesionImageSample(String examPaperNum, String studentId, String questionNum, String sampleVlue, String xmlPath, String loginUser, String scoreId, String regScore) {
        return this.examDao.addObjectiveQuesionImageSample(examPaperNum, studentId, questionNum, sampleVlue, xmlPath, loginUser, scoreId, regScore);
    }

    public List getObjectiveSamples() {
        return this.examDao.getObjectiveSamples();
    }

    public Object getTemplateType(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return this.dao2._queryObject("SELECT templateType FROM exampaper WHERE examPaperNum={examPaperNum}", args);
    }

    public byte[] getSampleImage(String id) {
        return this.examDao.getSampleImage(id);
    }

    public Integer deleteFromRegSample(String id) {
        return this.examDao.deleteFromRegSample(id);
    }

    public Integer re_Recognized_Objective(String examNum, String subjectNum, String gradeNum, String schoolNum, String examRoomNum, String examroomornot, String studentId, String optionType, String questionNum, String optioncount, String optionvalue, String reg_type, String option_val, String exePath, String loginUser, String examplace, String userId) {
        List<Score> list;
        String examPaperNum = this.examDao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        if (this.examDao.isManager(userId) || !"-1".equals(examplace)) {
            list = this.stestService.list("0", Integer.valueOf(examPaperNum).intValue(), examRoomNum, 0, 0, studentId, schoolNum, optioncount, optionvalue, optionType, examroomornot, questionNum, reg_type, "1", "F", examplace);
        } else {
            list = this.stestService.list_auth("0", Integer.valueOf(examPaperNum).intValue(), examRoomNum, 0, 0, studentId, schoolNum, optioncount, optionvalue, optionType, examroomornot, questionNum, reg_type, "1", "F", examplace, userId);
        }
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String curRegscore = list.get(i).getExt6();
                String curScoreId = list.get(i).getExt3();
                String curStudentId = String.valueOf(list.get(i).getExt8());
                String answer = "";
                String multiple = list.get(i).getExt7();
                if (null != multiple && multiple.equals("0")) {
                    answer = ItemThresholdRegUtil.getRegAnswerString(curRegscore, Integer.valueOf(option_val).intValue());
                }
                if (null != multiple && multiple.equals("1")) {
                    answer = ItemThresholdRegUtil.getRegAnswerString(curRegscore, Integer.valueOf(option_val).intValue());
                }
                int val = 0;
                if (null != answer && !"".equals(answer) && !"null".equals(answer)) {
                    val = ItemThresholdRegUtil.getThreshold(curRegscore, answer);
                    answer = Util.sort(answer);
                }
                updateAnswer(examPaperNum, list.get(i).getExt2(), answer, curStudentId, curScoreId, val);
            }
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        String templateType = this.dao2._queryStr("SELECT templateType FROM exampaper WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum}", args);
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
        save(reg_Th_Log);
        return 1;
    }

    public List getRegSampleById(String id) {
        return this.examDao.getRegSampleById(id);
    }

    public List<Reg_Th_Log> getRe_th_log(int size) {
        return this.examDao.getRe_th_log(size);
    }

    public Integer saveRe_th_log_description(String re_th_log_id, String des) {
        return this.examDao.saveRe_th_log_description(re_th_log_id, des);
    }

    public List authIfExistUpFullScoreException(String examPaperNum, String school, String examinationRoom, String studentId, String page) {
        String sql = " select distinct regScore from score where examPaperNum = {examPaperNum} and qtype= {qtype} and schoolNum = {school} and  isException = {isException} ";
        if (null != examinationRoom && !examinationRoom.equals("-1")) {
            sql = sql + "and  examinationRoomNum= {examinationRoom} ";
        }
        if (null != studentId && !studentId.equals("")) {
            sql = sql + "and  studentId= {studentId} ";
        }
        if (null != page && !page.equals("")) {
            sql = sql + "and  page= {page} ";
        }
        String sql2 = sql + "order by regScore";
        this.log.info("[" + getClass().getSimpleName() + "] : authIfExistUpFullScoreException()  sql: " + sql2);
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("qtype", "1");
        args.put(License.SCHOOL, school);
        args.put("isException", "4");
        args.put("examinationRoom", examinationRoom);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("page", page);
        return this.dao2._queryColList(sql2, args);
    }

    public int[] batchSave(List<Object> list) {
        return this.dao2.batchSave(list);
    }

    public String getExamPaperMark(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        try {
            List list = this.dao2._queryBeanList("SELECT DISTINCT id,examPaperNum,examNum,gradeNum,subjectNum,pexamPaperNum,type,jie,isHidden     FROM exampaper   WHERE 1=1 AND examPaperNum={examPaperNum}  and isHidden='F'", Exampaper.class, args);
            if (list != null && list.size() == 1) {
                return ((Exampaper) list.get(0)).getType();
            }
            return "0";
        } catch (Exception e) {
            this.log.info("获取当前试卷编号是有痕还是无痕", e);
            return "0";
        }
    }

    public List<Task> getQuesGroup(Integer exampaperNum, String groupnum) {
        return this.examDao.getQuesGroup(exampaperNum, groupnum);
    }

    public List<Task> qscoreta(Integer examPaperNum, String groupnum) {
        return this.examDao.qscoreta(examPaperNum, groupnum);
    }

    public List<Task> qscoreta2(Integer examPaperNum, String groupnum) {
        return this.examDao.qscoreta2(examPaperNum, groupnum);
    }

    public Integer clearUserQues(Integer examPaperNum, String groupNum, String userNum, String quesNum, String loginUserId, String rwCount) {
        return this.examDao.clearUserQues(examPaperNum, groupNum, userNum, quesNum, loginUserId, rwCount);
    }

    public void saveRemarkAndMarkError(Remark remark, MarkError markError, String updateSql, String updateSql2) {
        this.dao2.save(markError);
        this.awardPointDao.panfenjilu(markError.getScoreId() + "", markError.getQuestionNum() + "", markError.getUserNum() + "", markError.getInsertUser() + "");
    }

    public List searchInQuesImage(String examPaperNum, String studentId, String questionNum, String school, String page, String examinationRoomNum) {
        return this.examDao.searchInQuesImage(examPaperNum, studentId, questionNum, school, page, examinationRoomNum);
    }

    public Integer addToClipErrorMethod(String scoreId, String regId, String examPaperNum, String loginUserNum) {
        return this.examDao.addToClipErrorMethod(scoreId, regId, examPaperNum, loginUserNum);
    }

    public Integer removeFromClipErrorMethod(String regId, String examPaperNum, String loginUserNum) {
        return this.examDao.removeFromClipErrorMethod(regId, examPaperNum, loginUserNum);
    }

    public List reClipExamPaper(String xmlPath, String examPaperNum, String examRoom, String schoolNum, String objVal, String subVal, String illegal_misVal, String typeAB, String loginUserNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        this.dao2._execute("DELETE  FROM cliperror WHERE exampaperNum={examPaperNum}  AND `status`='1'", args);
        openExeClipExamPaper(xmlPath, examPaperNum, examRoom, schoolNum, objVal, subVal, illegal_misVal, typeAB, loginUserNum);
        List list = searchClipSuccess_failure_page(examPaperNum, examRoom, schoolNum);
        return list;
    }

    public Integer openExeClipExamPaper(String settingPath, String examPaperNum, String examRoom, String schoolNum, String objVal, String subVal, String illegal_misVal, String typeAB, String loginUserNum) {
        String filePath = "";
        String exeCommand = "ReClip.exe " + loginUserNum + " " + (settingPath + "/WEB-INF/classes/c3p0.properties") + " " + examPaperNum + " " + examRoom + " " + objVal + " " + subVal + " " + illegal_misVal + " " + typeAB;
        try {
            filePath = createfile(settingPath, exeCommand);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Runtime rn = Runtime.getRuntime();
        Thread b = null;
        Thread c = null;
        try {
            try {
                Process p = rn.exec(filePath);
                BufferedInputStream is1 = new BufferedInputStream(p.getInputStream());
                BufferedInputStream is2 = new BufferedInputStream(p.getErrorStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is1));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(is1));
                3 r0 = new 3(this, br);
                r0.start();
                4 r02 = new 4(this, br2);
                r02.start();
                if (p.waitFor() != 0) {
                    if (p.exitValue() == 1) {
                        this.log.error("命令执行失败!");
                    }
                    File f = new File(filePath);
                    f.delete();
                    try {
                        r0.stop();
                        r02.stop();
                        return null;
                    } catch (Exception e) {
                        this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e);
                        e.printStackTrace();
                        return null;
                    }
                }
                is1.close();
                is2.close();
                br.close();
                br2.close();
                File f2 = new File(filePath);
                f2.delete();
                Integer valueOf = Integer.valueOf(p.exitValue());
                try {
                    r0.stop();
                    r02.stop();
                    return valueOf;
                } catch (Exception e2) {
                    this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e2);
                    e2.printStackTrace();
                    return null;
                }
            } catch (Exception e3) {
                this.log.error("重置页码后调用exe文件重新识别图像，执行报错", e3);
                try {
                    b.stop();
                    c.stop();
                    return null;
                } catch (Exception e4) {
                    this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e4);
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
                this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e5);
                e5.printStackTrace();
                return null;
            }
        }
    }

    public List searchClipStuMethod(String examPaperNum, String examRoom, String schoolNum, String gradeNum, String loginUserNum) {
        return this.examDao.searchClipStuMethod(examPaperNum, examRoom, schoolNum, gradeNum, loginUserNum);
    }

    public List searchClipSuccess_failure_page(String examPaperNum, String examRoom, String schoolNum) {
        return this.examDao.searchClipSuccess_failure_page(examPaperNum, examRoom, schoolNum);
    }

    public Integer batchAddClipError(String examNum, String subjectNum, String gradeNum, String examRoomNum, String schoolNum, String loginUserNum) {
        return this.examDao.batchAddClipError(examNum, subjectNum, gradeNum, examRoomNum, schoolNum, loginUserNum);
    }

    public Integer batchRemoveClipError(String examNum, String subjectNum, String gradeNum, String examRoomNum, String schoolNum, String loginUserNum) {
        return this.examDao.batchRemoveClipError(examNum, subjectNum, gradeNum, examRoomNum, schoolNum, loginUserNum);
    }

    public Exampaper getTotalScoreCount(String exam, String subject, String grade) {
        return this.examDao.getTotalScoreCount(exam, subject, grade);
    }

    public void getAnalysisscoreValue(String examPaperNum) {
        this.examDao.getAnalysisscoreValue(examPaperNum);
    }

    public List<AjaxData> getClassList(String exam, String school, String subject, String grade, String stat, String type, String systemType, String subjectType) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null == ep) {
            return null;
        }
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.examDao.getClassList_online(exam, school, subject, grade, stat, type, subjectType);
        }
        return this.examDao.getClassList_online(exam, school, subject, grade, stat, type, subjectType);
    }

    public List<AjaxData> getExportStudentClassList(String exam, String school, String grade, String subjectType, String jie) {
        return this.examDao.getExportStudentClassList(exam, school, grade, subjectType, jie);
    }

    public void updateTaskData(String examPaperNum) {
        this.examDao.updateTaskData(examPaperNum);
    }

    public List gets_pques(String examnum, String subjectnum, String gradeNum, String schoolNum, String classnum, String type, String stype, String stuSource, float rate, String islevel, String subCompose) {
        return this.examDao.gets_pques(examnum, subjectnum, gradeNum, schoolNum, classnum, type, stype, stuSource, rate, islevel, subCompose);
    }

    public void updateExampaper_countScore(String totalScore, String analysisscore, String examPaperNum) {
        Map args = new HashMap();
        args.put("analysisscore", analysisscore);
        args.put("examPaperNum", examPaperNum);
        this.dao2._execute("UPDATE exampaper SET analysisscore = {analysisscore} WHERE examPaperNum={examPaperNum} ", args);
    }

    public void clipTwo(String scoreId, String examPaperNum, String user, String date, String examroomornot, String qtype) throws Exception {
        long stuId = GUID.getGUID();
        Map args0 = new HashMap();
        args0.put("examPaperNum", examPaperNum);
        args0.put("scoreId", scoreId);
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT regId,examinationRoomNum,schoolNum,testingCentreId FROM score WHERE  examPaperNum={examPaperNum} AND  id={scoreId} ", Score.class, args0);
        List<RowArg> rowArgList = new ArrayList<>();
        for (int i = 0; i < _queryBeanList.size(); i++) {
            String regId = ((Score) _queryBeanList.get(i)).getRegId();
            String examinationRoomNum = ((Score) _queryBeanList.get(i)).getExaminationRoomNum();
            int schoolNum = ((Score) _queryBeanList.get(i)).getSchoolNum().intValue();
            String testingCentreId = ((Score) _queryBeanList.get(i)).getTestingCentreId();
            Map args1 = new HashMap();
            args1.put("stuId", Long.valueOf(stuId));
            args1.put("examPaperNum", examPaperNum);
            args1.put("regId", regId);
            String idlistSql = "";
            if (qtype.equals("1")) {
                idlistSql = "SELECT id FROM score WHERE examPaperNum={examPaperNum} AND  regId={regId}";
            }
            if (qtype.equals("0")) {
                idlistSql = "SELECT id FROM objectivescore WHERE examPaperNum={examPaperNum} AND  regId={regId}";
            }
            if (qtype.equals("-1")) {
                idlistSql = "SELECT id FROM score WHERE examPaperNum={examPaperNum} AND  regId={regId} UNION ALL SELECT id FROM objectivescore WHERE examPaperNum={examPaperNum} AND regId={regId}";
            }
            List idlist = this.dao2._queryColList(idlistSql, args1);
            for (int j = 0; j < idlist.size(); j++) {
                String sid = String.valueOf(idlist.get(j));
                Map args2 = new HashMap();
                args2.put("examPaperNum", examPaperNum);
                args2.put("sid", sid);
                int count = this.dao2._queryInt("SELECT COUNT(1) FROM task WHERE exampaperNum={examPaperNum} AND scoreId={sid} AND status!='F' ", args2).intValue();
                if (count == 0) {
                    if (qtype.equals("1") || qtype.equals("-1")) {
                        rowArgList.add(new RowArg("DELETE FROM score WHERE examPaperNum={examPaperNum} AND  id={sid}  ", args2));
                    }
                    if (qtype.equals("0") || qtype.equals("-1")) {
                        rowArgList.add(new RowArg("DELETE FROM objectivescore WHERE examPaperNum={examPaperNum} AND  id={sid}  ", args2));
                    }
                    rowArgList.add(new RowArg("DELETE  FROM scoreimage WHERE scoreId={sid} ", args2));
                    rowArgList.add(new RowArg("DELETE  FROM questionimage   WHERE scoreId={sid}", args2));
                    rowArgList.add(new RowArg("DELETE FROM markerror WHERE exampaperNum={examPaperNum} AND scoreId={sid}", args2));
                    rowArgList.add(new RowArg("DELETE FROM task WHERE exampaperNum={examPaperNum} AND scoreId={sid}", args2));
                }
            }
            String examroomStr1 = "";
            String examroomStr2 = "";
            if (examroomornot.equals("0")) {
                examroomStr1 = ",examinationRoomNum";
                examroomStr2 = " ,{examinationRoomNum} ";
            }
            String statStr = "";
            String stat1 = "";
            if (qtype.equals("0")) {
                statStr = " AND stat!='1'";
                stat1 = "1";
            }
            if (qtype.equals("1")) {
                statStr = " AND stat!='2'";
                stat1 = "2";
            }
            if (qtype.equals("-1")) {
                stat1 = "0";
            }
            Map args22 = new HashMap();
            args22.put("examinationRoomNum", examinationRoomNum);
            args22.put("examPaperNum", examPaperNum);
            args22.put("regId", regId);
            args22.put(Const.EXPORTREPORT_schoolNum, Integer.valueOf(schoolNum));
            args22.put("testingCentreId", testingCentreId);
            args22.put("user", user);
            args22.put("date", date);
            args22.put("stat1", stat1);
            String countSql = "SELECT count(1) FROM cantrecognized  WHERE examPaperNum={examPaperNum} AND  regId={regId} " + statStr;
            int count2 = this.dao2._queryInt(countSql, args22).intValue();
            if (count2 == 0) {
                String sql_17 = "INSERT into cantrecognized (examPaperNum,regId" + examroomStr1 + ",schoolNum,testingCentreId,insertUser,insertDate,isDelete,status,stat) VALUES({examPaperNum},{regId} " + examroomStr2 + ",{schoolNum},{testingCentreId},{user},{date},'F','0',{stat1})";
                rowArgList.add(new RowArg(sql_17, args22));
            } else {
                rowArgList.add(new RowArg("UPDATE cantrecognized SET stat='0' WHERE examPaperNum={examPaperNum} AND regId={regId}", args22));
            }
            if (qtype.equals("-1")) {
                rowArgList.add(new RowArg("DELETE FROM examinationnumimg WHERE  regId={regId}  ", args1));
                rowArgList.add(new RowArg("DELETE FROM examineenumerror WHERE  examPaperNum={examPaperNum} AND  regId={regId}  ", args1));
                rowArgList.add(new RowArg("DELETE FROM illegal WHERE  examPaperNum={examPaperNum} AND  regId={regId}  ", args1));
                rowArgList.add(new RowArg("DELETE FROM illegalimage WHERE  regId={regId}  ", args1));
                rowArgList.add(new RowArg("DELETE FROM exampapertypeimage WHERE  regId={regId}  ", args1));
            }
            rowArgList.add(new RowArg("UPDATE regexaminee SET studentId={stuId} WHERE examPaperNum={examPaperNum} AND id={regId} ", args1));
            this.log.info("sql---UPDATE regexaminee SET studentId={stuId} WHERE examPaperNum={examPaperNum} AND id={regId} ");
        }
        if (rowArgList.size() > 0) {
            this.dao2._batchExecute(rowArgList);
        }
    }

    public void missJudge(String scoreId, String examPaperNum, String user, String date, String examroomornot) throws Exception {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        this.dao2._execute("update score set isException = '0' WHERE id = {scoreId}", args);
    }

    public String getscantype(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return this.dao2._queryStr("SELECT scanType FROM exampaper WHERE examPaperNum={examPaperNum}", args);
    }

    public boolean updateScoreIsModify(String ScoreId, String status) {
        Map args = new HashMap();
        args.put(Const.CORRECT_SCORECORRECT, status);
        args.put("ScoreId", ScoreId);
        Integer aInteger = Integer.valueOf(this.dao2._execute("update score set isModify = {status} WHERE id = {ScoreId}", args));
        if (null != aInteger && aInteger.intValue() > 0) {
            return true;
        }
        return false;
    }

    public Integer getCorrectListCount(Score s) {
        return this.examDao.getCorrectListCount(s);
    }

    public byte[] getobjectimg(String url, String img) {
        return this.examDao.getobjectimg(url, img);
    }

    public List<Task> getQuesScoreTask(String exampaperNum, String questionNum, String insertUser, String scoreId) {
        return this.examDao.getQuesScoreTask(exampaperNum, questionNum, insertUser, scoreId);
    }

    public Integer recognizeWrong(String[] ids) {
        new ArrayList();
        List<RowArg> rowArgList = new ArrayList<>();
        for (String id : ids) {
            Map args = new HashMap();
            args.put("id", id);
            rowArgList.add(new RowArg("update score set isException=5 where id = {id}", args));
        }
        try {
            this.dao2._batchExecute(rowArgList);
            return 1;
        } catch (Exception e) {
            return null;
        }
    }

    public List objecterranly(String examNum, String subjectNum, String gradeNum, String schoolNum, String classNum, String studentType, String type, String source) {
        return this.examDao.objecterranly(examNum, subjectNum, gradeNum, schoolNum, classNum, studentType, type, source);
    }

    public List geterrorStudentList(String examNum, String subjectNum, String gradeNum, String schoolNum, String classNum, String type, String studentType, String source, String questionNum, String sign, String answer) {
        return this.examDao.geterrorStudentList(examNum, subjectNum, gradeNum, schoolNum, classNum, type, studentType, source, questionNum, sign, answer);
    }

    public List<AjaxData> getStuExam(String studentId) {
        return this.examDao.getStuExam(studentId);
    }

    public int editExamineeNum(String examNum, String studentId, String schoolNum, String classNum) {
        return this.examDao.editExamineeNum(examNum, studentId, schoolNum, classNum);
    }

    public String setStuExampaperType(String studentId, String exampaperNum, String type) {
        return this.examDao.setStuExampaperType(studentId, exampaperNum, type);
    }

    public byte[] getobjectscore(String studnetId, String questionNum) {
        return this.examDao.getobjectscore(studnetId, questionNum);
    }

    public Exampaper getExampaperInfo(String examNum, String subjectNum, String gradeNum) {
        return this.examDao.getExampaperInfo(examNum, subjectNum, gradeNum);
    }

    public String getQuestionScoreByQuestionInfo(int epNum, String questionNum, String score) {
        return this.examDao.getQuestionScoreByQuestionInfo(epNum, questionNum, score);
    }

    public String isMerge(String exampaperNum, String questionNum) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        return this.dao2._queryStr("SELECT `merge` FROM define df LEFT JOIN subdefine sdf ON df.examPaperNum = sdf.examPaperNum AND df.id = sdf.pid WHERE sdf.id = {questionNum}", args);
    }

    public List<MarkError> getQueList(String exampaperNum, String questionNum, String scoreId) {
        try {
            Map args = new HashMap();
            args.put("questionNum", questionNum);
            args.put("scoreId", scoreId);
            args.put("exampaperNum", exampaperNum);
            return this.dao2._queryBeanList("SELECT s.id scoreId,s.questionNum questionNum from subdefine sdf INNER JOIN (SELECT pid from subdefine where id = {questionNum}) sdf2 on sdf2.pid = sdf.pid INNER JOIN score s on s.questionNum = sdf.id INNER JOIN (SELECT studentId from score where id = {scoreId}) s2 on s2.studentId = s.studentId where sdf.examPaperNum = {exampaperNum}", MarkError.class, args);
        } catch (Exception e) {
            return null;
        }
    }

    public String getGroupNumByQueNum(String questionNum) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        return this.dao2._queryStr("SELECT groupNum FROM questiongroup_question WHERE questionNum = {questionNum}", args);
    }

    public List<Map<String, Object>> getCaijueGroupList(String exampaperNum, String groupNum) {
        return this.examDao.getCaijueGroupList(exampaperNum, groupNum);
    }

    public List<Map<String, Object>> getNotCaijue(String exampaperNum, String groupNum) {
        return this.examDao.getNotCaijue(exampaperNum, groupNum);
    }

    public List<Map<String, Object>> getCaijueInfo(String exampaperNum, String groupNum) {
        return this.examDao.getCaijueInfo(exampaperNum, groupNum);
    }

    public String deleteExamineeNumOrNot(String classNum, String oldClassNum) {
        String flag = "0";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("oldClassNum", oldClassNum);
        if (!this.dao2._queryStr("select studentType from class where id={classNum}", args).equals(this.dao2._queryStr("select studentType from class where id={oldClassNum}", args))) {
            flag = "1";
        }
        return flag;
    }

    public int deleteExamineeNum(String studentId, String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        String id = this.dao2._queryStr("select id from student where studentId={studentId}", args);
        Map args1 = new HashMap();
        args1.put("id", id);
        args1.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao2._execute("delete from examinationnum where studentId={id} and examNum={examNum}", args1);
    }

    public String getweicaiqieNum1(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        String weicaiqie = this.dao2._queryStr("select count(1) weicaiqie from cantrecognized where exampaperNum={exampaperNum}", args);
        return weicaiqie;
    }

    public String getweicaiqieNum(String exam, String subject, String grade) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject}", args);
        Map args1 = new HashMap();
        args1.put("exampaperNum", exampaperNum);
        String weicaijue = this.dao2._queryStr("select count(1) weicaiqie from cantrecognized where exampaperNum= {exampaperNum}", args1);
        return weicaijue;
    }

    public List<Task> getStatistic(String exam, String subject, String grade, String questionNum, String updateUser, String fenshuduan1, String fenshuduan2) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject}", args);
        Map args1 = new HashMap();
        args1.put("exampaperNum", exampaperNum);
        args1.put("questionNum", questionNum);
        args1.put("updateUser", updateUser);
        args1.put("fenshuduan1", fenshuduan1);
        args1.put("fenshuduan2", fenshuduan2);
        String sql = "SELECT aa.id groupNum,aa.questionNum,count(DISTINCT aa.scoreId) ext1,count(DISTINCT aa.yishenhe) ext2,aa.fullScore ext3 FROM (SELECT t.questionNum id,IFNULL(subd.questionNum,d.questionNum) questionNum,t.scoreId scoreId,t.studentId studentId,esr.scoreId yishenhe,GROUP_CONCAT(distinct t.insertUser) insertUser,IFNULL(subd.fullScore,d.fullScore) fullScore  FROM task t LEFT JOIN define d on d.id = t.questionNum LEFT JOIN subdefine subd on subd.id = t.questionNum LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId AND esr.`status` = 'T' WHERE t.exampaperNum = {exampaperNum} and t.questionNum = {questionNum} GROUP BY t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 ) aa ";
        if ("".equals(fenshuduan1) && "".equals(fenshuduan2)) {
            if (!"-1".equals(updateUser)) {
                sql = sql + " where aa.insertUser in ({updateUser[]}) ";
            }
        } else {
            sql = sql + " inner JOIN score s ON s.id = aa.scoreId where s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
            if (!"-1".equals(updateUser)) {
                sql = sql + " and aa.insertUser in ({updateUser[]}) ";
            }
        }
        String sql2 = sql + "GROUP BY aa.id ";
        this.log.info("--判分审核统计普通子题【getStatistic】sql--" + sql2);
        return this.dao2._queryBeanList(sql2, Task.class, args1);
    }

    public List<Task> getHbcqGStatistic(String exam, String subject, String grade, String questionNum, String updateUser, String fenshuduan1, String fenshuduan2) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject}", args);
        Map args1 = new HashMap();
        args1.put("questionNum", questionNum);
        String subdCount = this.dao2._queryStr("select count(1) from subdefine where pid = {questionNum}", args1);
        String sSql = "";
        String sSql2 = "";
        Map args2 = new HashMap();
        args2.put("fenshuduan1", fenshuduan1);
        args2.put("fenshuduan2", fenshuduan2);
        args2.put("subdCount", subdCount);
        args2.put("exampaperNum", exampaperNum);
        args2.put("questionNum", questionNum);
        args2.put("updateUser", updateUser);
        if (!"".equals(fenshuduan1) || !"".equals(fenshuduan2)) {
            sSql = " LEFT JOIN score s ON s.id = t.scoreId ";
            sSql2 = " and sum(s.questionScore) BETWEEN {fenshuduan1} AND {fenshuduan2}";
        }
        String sql = "SELECT aa.id groupNum,aa.questionNum,count(DISTINCT aa.scoreId) ext1,sum(aa.yishenhe) ext2,aa.fullScore ext3 FROM (SELECT t.groupNum id,d.questionNum questionNum,t.scoreId scoreId,t.studentId studentId,IF(COUNT(esr.scoreId)={subdCount},1,0) yishenhe,GROUP_CONCAT(distinct t.insertUser) insertUser,d.fullScore fullScore  FROM task t LEFT JOIN define d ON d.id = t.groupNum LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId AND esr.`status` = 'T' " + sSql + "WHERE t.exampaperNum = {exampaperNum} and t.groupNum = {questionNum} GROUP BY t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 " + sSql2 + ") aa ";
        if (!"-1".equals(updateUser)) {
            sql = sql + " where aa.insertUser in ({updateUser[]}) ";
        }
        String sql2 = sql + "GROUP BY aa.id ";
        this.log.info("--判分审核统计合并裁切主题【getHbcqGStatistic】sql--" + sql2);
        return this.dao2._queryBeanList(sql2, Task.class, args2);
    }

    public List<Task> getAllStatistic_old(String exam, String subject, String grade, String questionNum, String updateUser, String fenshuduan1, String fenshuduan2) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject}", args);
        Map args1 = new HashMap();
        args1.put("exampaperNum", exampaperNum);
        args1.put("updateUser", updateUser);
        args1.put("fenshuduan1", fenshuduan1);
        args1.put("fenshuduan2", fenshuduan2);
        String sql = "SELECT aa.id groupNum,aa.questionNum,count(DISTINCT aa.scoreId) ext1,count(DISTINCT aa.yishenhe) ext2,aa.fullScore ext3 FROM (SELECT t.questionNum id,IFNULL(subd.questionNum,d.questionNum) questionNum,t.scoreId scoreId,t.studentId studentId,esr.scoreId yishenhe,GROUP_CONCAT(distinct t.insertUser) insertUser,IFNULL(d2.orderNum, d.orderNum) orderNum1,subd.orderNum orderNum2,IFNULL(subd.fullScore,d.fullScore) fullScore  FROM task t LEFT JOIN subdefine subd on subd.id = t.questionNum LEFT JOIN define d on d.id = t.questionNum LEFT JOIN define d2 ON d2.id = subd.pid LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId AND esr.`status` = 'T' WHERE t.exampaperNum = {exampaperNum} GROUP BY t.questionNum,t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 ) aa ";
        if ("".equals(fenshuduan1) && "".equals(fenshuduan2)) {
            if (!"-1".equals(updateUser)) {
                sql = sql + " where aa.insertUser in ({updateUser[]}) ";
            }
        } else {
            sql = sql + " inner JOIN score s ON s.id = aa.scoreId where s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
            if (!"-1".equals(updateUser)) {
                sql = sql + " and aa.insertUser in ({updateUser[]}) ";
            }
        }
        String sql2 = sql + "GROUP BY aa.id ORDER BY aa.orderNum1,aa.orderNum2 ";
        this.log.info("--判分审核统计全部试题【getAllStatistic】sql--" + sql2);
        return this.dao2._queryBeanList(sql2, Task.class, args1);
    }

    public List<Task> getAllStatistic(String exam, String subject, String grade, String questionNum, String updateUser, String fenshuduan1, String fenshuduan2) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject}", args);
        Map args1 = new HashMap();
        args1.put("exampaperNum", exampaperNum);
        args1.put("questionNum", questionNum);
        args1.put("updateUser", updateUser);
        args1.put("fenshuduan1", fenshuduan1);
        args1.put("fenshuduan2", fenshuduan2);
        String sql = "SELECT aa.id groupNum,aa.questionNum,count(DISTINCT aa.scoreId) ext1,count(DISTINCT aa.yishenhe) ext2,aa.fullScore ext3 FROM (SELECT t.questionNum id,d.questionNum questionNum,t.scoreId scoreId,t.studentId studentId,esr.scoreId yishenhe,GROUP_CONCAT(distinct t.insertUser) insertUser,d.orderNum orderNum,d.fullScore fullScore  FROM task t LEFT JOIN (select questionNum,id,fullScore,orderNum*1000 orderNum from define where examPaperNum={exampaperNum} and id in ({questionNum[]}) union all select subd.questionNum,subd.id,subd.fullScore,(d.orderNum*1000+subd.orderNum) orderNum from subdefine subd left join define d on d.id = subd.pid where subd.examPaperNum={exampaperNum} and subd.id in ({questionNum[]}) ) d ON d.id = t.questionNum LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId AND esr.`status` = 'T' WHERE t.exampaperNum = {exampaperNum} and d.id is not null GROUP BY t.questionNum,t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 ) aa ";
        if ("".equals(fenshuduan1) && "".equals(fenshuduan2)) {
            if (!"-1".equals(updateUser)) {
                sql = sql + " where aa.insertUser in ({updateUser[]}) ";
            }
        } else {
            sql = sql + " inner JOIN score s ON s.id = aa.scoreId where s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
            if (!"-1".equals(updateUser)) {
                sql = sql + " and aa.insertUser in ({updateUser[]}) ";
            }
        }
        String sql2 = sql + "GROUP BY aa.id ORDER BY aa.orderNum ";
        this.log.info("--判分审核统计全部试题【getAllStatistic】sql--" + sql2);
        return this.dao2._queryBeanList(sql2, Task.class, args1);
    }

    public List<Task> getStatistic_old(String exam, String subject, String grade, String questionNum, String updateUser, String fenshuduan1, String fenshuduan2) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject}", args);
        Map args1 = new HashMap();
        args1.put("exampaperNum", exampaperNum);
        args1.put("updateUser", updateUser);
        args1.put("fenshuduan1", fenshuduan1);
        args1.put("fenshuduan2", fenshuduan2);
        String queSql = "-1".equals(questionNum) ? "" : " and t.groupNum = {questionNum} ";
        String sql = "SELECT aa.id groupNum,aa.questionNum,count(DISTINCT aa.scoreId) ext1,count(DISTINCT aa.yishenhe) ext2,aa.fullScore ext3 FROM (SELECT t.groupNum id,IFNULL(subd.questionNum,d.questionNum) questionNum,t.scoreId scoreId,t.studentId studentId,esr.scoreId yishenhe,GROUP_CONCAT(distinct t.insertUser) insertUser,IFNULL(d2.orderNum, d.orderNum) orderNum1,subd.orderNum orderNum2,IFNULL(subd.fullScore,d.fullScore) fullScore  FROM task t LEFT JOIN subdefine subd ON subd.id = t.groupNum LEFT JOIN define d ON d.id = t.groupNum LEFT JOIN define d2 ON d2.id = subd.pid LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId AND esr.`status` = 'T' WHERE t.exampaperNum = {exampaperNum} " + queSql + "GROUP BY t.groupNum,t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 ) aa ";
        if ("".equals(fenshuduan1) && "".equals(fenshuduan2)) {
            if (!"-1".equals(updateUser)) {
                sql = sql + " where aa.insertUser in ({updateUser[]}) ";
            }
        } else {
            sql = sql + " inner JOIN score s ON s.id = aa.scoreId where s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2}";
            if (!"-1".equals(updateUser)) {
                sql = sql + " and aa.insertUser in ({updateUser[]}) ";
            }
        }
        return this.dao2._queryBeanList(sql + "GROUP BY aa.id ORDER BY aa.orderNum1,aa.orderNum2 ", Task.class, args1);
    }

    public List<AjaxData> getAjaxSchoolList(String examNum, String subjectNum, String gradeNum) {
        Object pSub = getPSub(examNum, gradeNum, subjectNum);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("pSub", pSub);
        return this.dao2._queryBeanList("SELECT DISTINCT en.schoolNum num,sch.schoolName name FROM examinationnum en INNER JOIN school sch on sch.id = en.schoolNum WHERE en.examNum = {examNum} and en.gradeNum = {gradeNum} and en.subjectNum = {pSub} ORDER BY convert(sch.schoolName using gbk)", AjaxData.class, args);
    }

    public List<AjaxData> getTeachSchool(String userId) {
        Map args = new HashMap();
        args.put("userId", userId);
        return this.dao2._queryBeanList("SELECT u.schoolnum num,sch.schoolName name FROM `user` u INNER JOIN school sch on sch.id = u.schoolnum WHERE u.id = {userId}", AjaxData.class, args);
    }

    public List<Averagescore> getAllAveScoreSet(String examNum, String subjectNum, String gradeNum, String jie, String schoolNum, String userId) {
        Object pSub = getPSub(examNum, gradeNum, subjectNum);
        new ArrayList();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("pSub", pSub);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        List _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT en.classNum classNum,cla.className className,ave.average,ave.mindev,ave.maxdev,'' ext1,ave.status,ave.isModify  FROM examinationnum en LEFT JOIN averagescore ave on ave.examNum = en.examNum and ave.subjectNum = {subjectNum} and ave.classNum = en.classNum LEFT JOIN class cla on cla.id = en.classNum WHERE en.examNum = {examNum} and en.gradeNum = {gradeNum} and en.subjectNum = {pSub} and en.schoolNum = {schoolNum} and cla.id is not null ORDER BY length(cla.className),convert(cla.className using gbk)", Averagescore.class, args);
        Averagescore allclass = (Averagescore) this.dao2._queryBean("SELECT classNum,'全年级' className,average,mindev,maxdev,'' ext1,status,isModify from averagescore where examNum = {examNum} and gradeNum = {gradeNum} and subjectNum = {subjectNum} and schoolNum = {schoolNum} and classNum = '-1'", Averagescore.class, args);
        if (null == allclass) {
            allclass = new Averagescore("-1", "全年级", null, Const.mindev, Const.maxdev, "", "0", "0");
        }
        _queryBeanList.add(allclass);
        return _queryBeanList;
    }

    public List<Averagescore> getClaAveScoreSet(String examNum, String subjectNum, String gradeNum, String jie, String schoolNum, String userId) {
        Object pSub = getPSub(examNum, gradeNum, subjectNum);
        new ArrayList();
        String isModify = "0";
        String ext1Str = "if(ep.status='8' or ep.status='9','disabled','') ext1";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        Averagescore allclass = (Averagescore) this.dao2._queryBean("SELECT classNum,'全年级' className,average,mindev,maxdev,'disabled' ext1,status,isModify from averagescore where examNum = {examNum} and gradeNum = {gradeNum} and subjectNum = {subjectNum} and schoolNum = {schoolNum} and classNum = '-1'", Averagescore.class, args);
        if (null == allclass) {
            allclass = new Averagescore("-1", "全年级", null, Const.mindev, Const.maxdev, "disabled", "0", "0");
        } else {
            isModify = allclass.getIsModify();
        }
        if (!"1".equals(isModify)) {
            Map args1 = new HashMap();
            args1.put(Const.EXPORTREPORT_examNum, examNum);
            args1.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            args1.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            args1.put("userId", userId);
            if (null != this.dao2._queryObject("SELECT t.id from exampaper ep left JOIN task t on t.exampaperNum = ep.pexamPaperNum where ep.examNum = {examNum} and ep.gradeNum = {gradeNum} and ep.subjectNum = {subjectNum}  and t.insertUser = {userId} and t.`status` = 'T' LIMIT 1", args1)) {
                ext1Str = "'disabled' ext1";
            }
        }
        String classSql = "SELECT DISTINCT en.classNum,cla.className,ave.average,ave.mindev,ave.maxdev," + ext1Str + ",ave.status FROM examinationnum en LEFT JOIN exampaper ep on ep.examNum = en.examNum and ep.gradeNum = en.gradeNum and ep.subjectNum = en.subjectNum LEFT JOIN userposition up on up.classNum = en.classNum and up.subjectNum = {subjectNum} LEFT JOIN averagescore ave on ave.examNum = en.examNum and ave.subjectNum = {subjectNum} and ave.classNum = en.classNum LEFT JOIN class cla on cla.id = en.classNum WHERE en.examNum = {examNum} and en.gradeNum = {gradeNum} and en.subjectNum = {pSub} and en.schoolNum = {schoolNum} and up.userNum = {userId} and up.type = '1' and cla.id is not null ORDER BY length(cla.className),convert(cla.className using gbk)";
        Map args2 = new HashMap();
        args2.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args2.put(Const.EXPORTREPORT_examNum, examNum);
        args2.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args2.put("pSub", pSub);
        args2.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args2.put("userId", userId);
        List _queryBeanList = this.dao2._queryBeanList(classSql, Averagescore.class, args2);
        _queryBeanList.add(allclass);
        return _queryBeanList;
    }

    public Object getPSub(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryObject("SELECT subjectNum from exampaper where examPaperNum = (SELECT pexamPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum})", args);
    }

    public void submitAveScoreSet(String examNum, String subjectNum, String gradeNum, String jie, String schoolNum, String classNum, String average, String mindev, String maxdev, String status, String isModify, String userId) {
        String currentTime = DateUtil.getCurrentTime();
        for (int i = 0; i < 2; i++) {
            Object claId = insertCommon(examNum, subjectNum, gradeNum, jie, schoolNum, classNum, average, mindev, maxdev, status, isModify, userId, currentTime);
            if (null != claId) {
                Object userroleId = getUserroleId(userId, Const.ROLE_YUEJUANGUANLIYUAN);
                if (!"-1".equals(classNum) || "-1".equals(userId) || "-2".equals(userId) || null != userroleId) {
                    Object[] params = {average, mindev, maxdev, status, isModify, userId, currentTime, jie, examNum, subjectNum, gradeNum, schoolNum, classNum};
                    this.dao2.execute("update averagescore set average=?,mindev=?,maxdev=?,status=?,isModify=?,updateUser=?,updateDate=?,jie=? where examNum=? and subjectNum=? and gradeNum=? and schoolNum=? and classNum=?", params);
                }
            }
            if (!"-1".equals(classNum)) {
                classNum = "-1";
                average = null;
                mindev = Const.mindev.toString();
                maxdev = Const.maxdev.toString();
            } else {
                return;
            }
        }
    }

    public Object insertCommon(String examNum, String subjectNum, String gradeNum, String jie, String schoolNum, String classNum, String average, String mindev, String maxdev, String status, String isModify, String userId, String currentTime) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        Object id = this.dao2._queryObject("SELECT   id from averagescore where examNum = {examNum} and gradeNum = {gradeNum} and subjectNum = {subjectNum} and schoolNum = {schoolNum} and classNum = {classNum} ", args);
        if (null == id) {
            Object[] params = {examNum, subjectNum, gradeNum, jie, schoolNum, classNum, average, mindev, maxdev, status, isModify, userId, currentTime, userId, currentTime};
            this.dao2.execute("insert into averagescore (examNum,subjectNum,gradeNum,jie,schoolNum,classNum,average,mindev,maxdev,status,isModify,insertUser,insertDate,updateUser,updateDate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", params);
        }
        return id;
    }

    public void updateStatus(String examNum, String subjectNum, String gradeNum, String jie, String schoolNum, String status, String isModify, String userId) {
        String currentTime = DateUtil.getCurrentTime();
        Object id = insertCommon(examNum, subjectNum, gradeNum, jie, schoolNum, "-1", null, Const.mindev.toString(), Const.maxdev.toString(), status, isModify, userId, currentTime);
        if (null != id) {
            Object[] params = {status, isModify, userId, currentTime, examNum, subjectNum, gradeNum, schoolNum, "-1"};
            this.dao2.execute("update averagescore set status=?,isModify=?,updateUser=?,updateDate=? where examNum=? and subjectNum=? and gradeNum=? and schoolNum=? and classNum=?", params);
        }
    }

    public List<AjaxData> getAveSetSchool(String userId) {
        if ("-2".equals(userId) || "-1".equals(userId)) {
            return this.dao2._queryBeanList("SELECT DISTINCT ave.schoolNum num,sch.schoolName name from averagescore ave INNER JOIN school sch on sch.id = ave.schoolNum ORDER BY convert(sch.schoolName using gbk)", AjaxData.class, null);
        }
        Map args = new HashMap();
        args.put("userId", userId);
        return this.dao2._queryBeanList("SELECT DISTINCT ave.schoolNum num,sch.schoolName name from averagescore ave INNER JOIN school sch on sch.id = ave.schoolNum LEFT JOIN schauthormanage h on h.schoolNum = sch.id and h.userId={userId} LEFT JOIN user u on u.schoolNum = sch.id and u.id = {userId} and u.usertype=1 where h.schoolNum is not null or u.schoolNum is not null ORDER BY convert(sch.schoolName using gbk)", AjaxData.class, args);
    }

    public List<AjaxData> getAllAveSetGrade(String schoolNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        return this.dao2._queryBeanList("SELECT DISTINCT ave.gradeNum num,bg.gradeName name from averagescore ave INNER JOIN basegrade bg on bg.gradeNum = ave.gradeNum where ave.schoolNum={schoolNum} and ave.status='0' ORDER BY ave.gradeNum", AjaxData.class, args);
    }

    public List<AjaxData> getAveSetGrade(String userId, String schoolNum) {
        Map args = new HashMap();
        args.put("userId", userId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        return this.dao2._queryBeanList("SELECT DISTINCT ave.gradeNum num,bg.gradeName name from averagescore ave INNER JOIN basegrade bg on bg.gradeNum = ave.gradeNum INNER JOIN (SELECT DISTINCT gradeNum FROM userposition WHERE userNum={userId} and schoolNum={schoolNum} UNION SELECT DISTINCT gradeNum FROM userposition_record WHERE userNum= {userId} and schoolNum={schoolNum} ) u on u.gradeNum = ave.gradeNum where ave.schoolNum={schoolNum} and ave.status='0' ORDER BY ave.gradeNum", AjaxData.class, args);
    }

    public List<AjaxData> getAveSetJie(String schoolNum, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao2._queryBeanList("SELECT DISTINCT jie num,CONCAT(jie,'级') name from grade  where schoolNum = {schoolNum} and gradeNum = {gradeNum}  ORDER BY jie desc", AjaxData.class, args);
    }

    public List<AjaxData> getAveSetExam(String schoolNum, String gradeNum, String jie) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("jie", jie);
        return this.dao2._queryBeanList("SELECT DISTINCT ave.examNum num,e.examName name from averagescore ave INNER JOIN exam e on e.examNum = ave.examNum INNER JOIN arealevel al on al.examNum = ave.examNum where ave.gradeNum = {gradeNum} and ave.schoolNum = {schoolNum} and ave.jie = {jie} and ave.status='0' ORDER BY e.examDate desc,e.insertDate desc", AjaxData.class, args);
    }

    public Object getUserroleId(String userNum, String roleNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("roleNum", roleNum);
        return this.dao2._queryObject("select id from userrole where userNum={userNum} and roleNum={roleNum} limit 1", args);
    }

    public List<QuestionGroup_question> getQuestionGroupSD1(Integer examnum, Integer gradeNum) {
        try {
            Map args = new HashMap();
            args.put("examnum", examnum);
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            return this.dao2._queryBeanList("SELECT hh.exampaperNum,hh.ext1,hh.ext2,sum(hh.ext3) ext3,sum(hh.ext4) ext4,hh.questionName from( SELECT z.exampaperNum,su.subjectNum ext1,su.subjectName ext2,   case     when d.choosename<>'s' and z.makType=1 then IFNULL(ch.allTotalNum*2,0)+IFNULL(x.count,0)     when d.choosename<>'s' and z.makType=0 then IFNULL(ch.allTotalNum,0)     when d.choosename='s' and z.makType=1 then IFNULL(r.count*2,0)+IFNULL(x.count,0)     when d.choosename='s' and z.makType=0 then IFNULL(r.count,0)      end ext3,  IFNULL(y.d_num ,0) ext4,   IF(t.groupNum is NULL ,'T','F') questionName FROM(   \t\t\tSELECT s.groupNum,s.groupName,s.groupType,s.exampaperNum,m.makType from questiongroup s LEFT JOIN questiongroup_mark_setting m on m.groupNum=s.groupNum  \t\t\tLEFT JOIN exampaper e on s.exampapernum=e.exampapernum where e.examNum={examnum} and e.gradeNum={gradeNum}  )z    LEFT JOIN (  \t\t\tSELECT t.groupNum,cast(count(1) as signed)  as  d_num     \t\t\tFROM task t LEFT JOIN exampaper e on t.exampapernum=e.exampapernum WHERE e.examNum={examnum} and e.gradeNum={gradeNum} AND t.status='T'  GROUP BY groupNum    )y ON z.groupNum = y.groupNum LEFT JOIN (   \t\t\tSELECT groupNum,count(1) count FROM task t LEFT JOIN exampaper e on t.exampapernum=e.exampapernum WHERE e.examNum={examnum} and e.gradeNum={gradeNum} and t.userNum=3  GROUP BY groupNum   )x ON z.groupNum = x.groupNum    LEFT JOIN choosescale ch on z.groupNum=ch.groupNum     LEFT JOIN (  \t\tSELECT id,choosename from define d LEFT JOIN exampaper e on d.exampapernum=e.exampapernum WHERE e.examNum={examnum} and e.gradeNum={gradeNum}     UNION      SELECT sb.id,d.choosename from define d LEFT JOIN exampaper e on d.exampapernum=e.exampapernum LEFT JOIN subdefine sb on sb.pid=d.id  WHERE e.examNum={examnum} and e.gradeNum={gradeNum}  \t) d on z.groupNum=d.id     LEFT JOIN (  \t\tselect d.id,r.dd count from(  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d  \t\t\tLEFT JOIN exampaper ep on d.exampapernum=ep.exampapernum  \t\t\tINNER JOIN exampaper e on d.category=e.examPaperNum where ep.examNum={examnum} and ep.gradeNum={gradeNum} and d.questionType=1   \t\t\tUNION\t  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category   \t\t\tfrom subdefine d LEFT JOIN exampaper ep on d.exampapernum=ep.exampapernum INNER JOIN exampaper e on d.category=e.examPaperNum   \t\t\twhere ep.examNum={examnum} and ep.gradeNum={gradeNum} and d.questionType=1  \t\t)d LEFT JOIN(  \t\t\tSELECT count(DISTINCT r.studentId) dd,r.exampaperNum ext1 from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum       WHERE  e.examNum={examnum} and e.gradeNum={gradeNum}  and   r.scan_import=0  GROUP BY r.exampaperNum  \t\t\tunion  \t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampapernum from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum \t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE e.examNum={examnum} and e.gradeNum={gradeNum}  and r.scan_import=0  \t\t\tUNION\t  \t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampaperNum from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum LEFT JOIN(  \t\t\t\tSELECT r.studentId,r.exampapernum from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum \t\t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \t\t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE e.examNum={examnum} and e.gradeNum={gradeNum}  and r.scan_import=0  \t\t\t)r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum  \t\t\tINNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3  \t\t\tWHERE e.examNum={examnum} and e.gradeNum={gradeNum}  and r.scan_import=0 and r1.studentId is null  \t\t)r on d.category=r.ext1   ) r on z.groupNum=r.id    LEFT JOIN (  \t\tselect  DISTINCT q.groupNum FROM (  \t\t\tSELECT groupNum,questionNum FROM questiongroup_question qq LEFT JOIN exampaper e on qq.exampapernum=e.exampapernum WHERE e.examNum={examnum} and e.gradeNum={gradeNum} GROUP BY groupNum    \t)q LEFT JOIN (   \t\tselect DISTINCT questionNum  FROM remark r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum where r.type='1' AND r.STATUS='F' AND e.examNum={examnum} and e.gradeNum={gradeNum}  \t\t) tt ON q.questionNum = tt.questionNum where tt.questionNum is not null   )t  ON z.groupNum = t.groupNum LEFT JOIN exampaper epp on z.examPaperNum=epp.examPaperNum LEFT JOIN subject su on epp.subjectnum=su.subjectNum WHERE z.groupNum IS NOT NULL)hh GROUP BY hh.examPaperNum", QuestionGroup_question.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<AjaxData> getSpotCheckExam(String userId) {
        if ("-2".equals(userId) || "-1".equals(userId)) {
            return this.dao2._queryBeanList("select distinct e.examNum num,e.examName name FROM exam e left join exampaper ep on ep.examNum = e.examNum where e.isDelete='F' and e.status<>'9' and ep.type='0' and ep.isHidden='F' order by e.examDate desc,e.insertDate desc", AjaxData.class, null);
        }
        Map args = new HashMap();
        args.put("userId", userId);
        return this.dao2._queryBeanList("select distinct e.examNum num,e.examName name FROM exam e left join exampaper ep on ep.examNum = e.examNum left join questiongroup_user qgu on qgu.exampaperNum = ep.examPaperNum where e.isDelete='F' and e.status<>'9' and ep.type='0' and qgu.userNum={userId} and qgu.userType <> '0' order by e.examDate desc,e.insertDate desc", AjaxData.class, args);
    }

    public List<AjaxData> getSpotCheckSubject(String examNum, String userId) {
        if ("-2".equals(userId) || "-1".equals(userId)) {
            Map args = new HashMap();
            args.put(Const.EXPORTREPORT_examNum, examNum);
            return this.dao2._queryBeanList("select distinct sub.subjectNum num,sub.subjectName name FROM `subject` sub left join exampaper ep on ep.subjectNum = sub.subjectNum where ep.examNum={examNum} and ep.type='0' and ep.isHidden='F'  order by sub.orderNum,sub.subjectNum", AjaxData.class, args);
        }
        Map args1 = new HashMap();
        args1.put(Const.EXPORTREPORT_examNum, examNum);
        args1.put("userId", userId);
        return this.dao2._queryBeanList("select distinct sub.subjectNum num,sub.subjectName name FROM exampaper ep left join questiongroup_user qgu on qgu.exampaperNum = ep.examPaperNum left join exampaper ep2 on ep2.examPaperNum = ep.pexamPaperNum left join `subject` sub on sub.subjectNum = ep2.subjectNum where ep.examNum={examNum} and ep.type='0' and qgu.userNum={userId} and qgu.userType <> '0' and ep2.examPaperNum is not null and sub.subjectNum is not null order by sub.orderNum,sub.subjectNum", AjaxData.class, args1);
    }

    public List<AjaxData> getSpotCheckGrade(String examNum, String subjectNum, String userId) {
        if ("-2".equals(userId) || "-1".equals(userId)) {
            Map args = new HashMap();
            args.put(Const.EXPORTREPORT_examNum, examNum);
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            return this.dao2._queryBeanList("select distinct bg.gradeNum num,bg.gradeName name FROM basegrade bg left join exampaper ep on ep.gradeNum = bg.gradeNum where ep.examNum={examNum} and ep.subjectNum={subjectNum} and ep.type='0' and ep.isHidden='F' order by bg.gradeNum", AjaxData.class, args);
        }
        Map args1 = new HashMap();
        args1.put(Const.EXPORTREPORT_examNum, examNum);
        args1.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args1.put("userId", userId);
        return this.dao2._queryBeanList("select distinct bg.gradeNum num,bg.gradeName name FROM basegrade bg left join exampaper ep on ep.gradeNum = bg.gradeNum left join questiongroup_user qgu on qgu.exampaperNum = ep.examPaperNum left join exampaper ep2 on ep2.examPaperNum = ep.pexamPaperNum where ep.examNum={examNum} and ep2.subjectNum={subjectNum} and ep.type='0' and qgu.userNum={userId} and qgu.userType <> '0' order by bg.gradeNum", AjaxData.class, args1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public List<AjaxData> getSpotCheckQuestion(String examNum, String subjectNum, String gradeNum, String userId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        Object pexamPaperNum = this.dao2._queryObject("select ep.pexamPaperNum from exampaper ep where ep.examNum={examNum} and ep.gradeNum={gradeNum} and ep.subjectNum={subjectNum}", args);
        Map args1 = new HashMap();
        args1.put("userId", userId);
        args1.put("pexamPaperNum", pexamPaperNum);
        String userType = this.dao2._queryStr("select userType from questiongroup_user where userNum={userId} and exampaperNum={pexamPaperNum} order by usertype desc limit 1", args1);
        List list = new ArrayList();
        if (userId.equals("-2") || userId.equals("-1") || "2".equals(userType)) {
            Map args2 = new HashMap();
            args2.put("pexamPaperNum", pexamPaperNum);
            list = this.dao2._queryBeanList("(SELECT IFNULL(subd.id,d.id) num,IFNULL(subd.questionNum,d.questionNum) name,'1' ext1,qg.groupNum ext3 from questiongroup_question qg LEFT JOIN define d on d.id = qg.questionNum LEFT JOIN subdefine subd on subd.id = qg.questionNum LEFT JOIN define d2 on d2.id = subd.pid where qg.exampaperNum = {pexamPaperNum} ORDER BY IFNULL(d.orderNum,d2.orderNum),subd.orderNum) UNION all (SELECT d.id num,d.questionNum name,'2' ext1,d.id ext3 from questiongroup qg LEFT JOIN define d on d.id = qg.groupNum where qg.exampaperNum = {pexamPaperNum} and qg.groupType = '2' ORDER BY d.orderNum) ORDER BY ext1,1", AjaxData.class, args2);
        } else {
            Map args3 = new HashMap();
            args3.put("pexamPaperNum", pexamPaperNum);
            args3.put("userId", userId);
            list.addAll(this.dao2._queryBeanList("select distinct qq.questionNum num,t.qNum name,'1' ext1,t.orderNum ext2,t.groupNum ext3 from questiongroup_user qu LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id ,CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum ,CASE WHEN d.isParent ='0' THEN d.category ELSE s.category  END AS category ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.pid  END AS groupNum from define d LEFT JOIN subdefine s ON d.id = s.pid where d.exampaperNum={pexamPaperNum} ) t on t.category = qu.exampaperNum LEFT JOIN questiongroup_question qq on qq.questionNum = t.id where qq.exampaperNum={pexamPaperNum} and qu.userType='2' and qu.userNum={userId} group by qq.questionNum ORDER BY t.orderNum", AjaxData.class, args3));
            Map args4 = new HashMap();
            args4.put("pexamPaperNum", pexamPaperNum);
            args4.put("userId", userId);
            list.addAll(this.dao2._queryBeanList("SELECT d.id num,d.questionNum name,'2' ext1,d.orderNum*1000 ext2,d.id ext3 from questiongroup qg inner JOIN define d on d.id = qg.groupNum inner JOIN questiongroup_user qgu on qgu.exampaperNum = d.category where qg.exampaperNum = {pexamPaperNum} and qg.groupType = '2' and qgu.userNum={userId} and qgu.userType='2' ORDER BY d.orderNum ", AjaxData.class, args4));
            if ("1".equals(userType)) {
                Map args5 = new HashMap();
                args5.put("pexamPaperNum", pexamPaperNum);
                args5.put("userId", userId);
                list.addAll(this.dao2._queryBeanList("select distinct qq.questionNum num,t.qNum name,'1' ext1,t.orderNum ext2,t.groupNum ext3 from questiongroup_user qu LEFT JOIN questiongroup_question qq on qu.groupNum = qq.groupNum LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id ,CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.pid  END AS groupNum from define d LEFT JOIN subdefine s ON d.id = s.pid where d.exampaperNum={pexamPaperNum}) t on qq.questionNum = t.id where qq.exampaperNum={pexamPaperNum} and qu.userType='1' and qu.userNum={userId}  group by qq.questionNum ORDER BY t.orderNum", AjaxData.class, args5));
                Map args6 = new HashMap();
                args6.put("pexamPaperNum", pexamPaperNum);
                args6.put("userId", userId);
                list.addAll(this.dao2._queryBeanList("SELECT d.id num,d.questionNum name,'2' ext1,d.orderNum*1000 ext2,d.id ext3 from questiongroup qg inner JOIN define d on d.id = qg.groupNum inner JOIN questiongroup_user qgu on qgu.groupNum = qg.groupNum where qg.exampaperNum = {pexamPaperNum} and qg.groupType = '2' and qgu.userNum={userId} and qgu.userType='1' ORDER BY d.orderNum ", AjaxData.class, args6));
                list.sort(Comparator.comparing((v0) -> {
                    return v0.getExt1();
                }).thenComparing((v0) -> {
                    return v0.getExt2();
                }));
            }
        }
        return list;
    }

    public List<AjaxData> getSpotCheckTeacher(String groupNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        return this.dao2._queryBeanList("select distinct qgu.userNum num,u.realname name from questiongroup_user qgu left join `user` u on u.id = qgu.userNum where qgu.groupNum in ({groupNum[]}) and qgu.userType <> '2' and u.id is not null", AjaxData.class, args);
    }

    public Map<String, Map<String, Map<String, Integer>>> getDataFormBigTableReg() {
        Map<String, Map<String, Map<String, Integer>>> map = new HashMap<>();
        Map<String, Map<String, Integer>> regTotal_map = new HashMap<>();
        Map<String, Map<String, Integer>> regXuan_map = new HashMap<>();
        Map<String, Map<String, Integer>> regXue_map = new HashMap<>();
        Map<String, Map<String, Integer>> regTotalIn_map = new HashMap<>();
        Map<String, Map<String, Integer>> regXuanIn_map = new HashMap<>();
        Map<String, Map<String, Integer>> regXueIn_map = new HashMap<>();
        try {
            List examList = this.dao2._queryColList("SELECT examNum from exam where status <>9 and isDelete='F' ", null);
            for (int h = 0; h < examList.size(); h++) {
                String examNum = String.valueOf(examList.get(h));
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.exampaperNum,-1 ext1,count( DISTINCT r.studentId) ext2 from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum  WHERE  e.examnum={examNum}  and   r.scan_import=0  GROUP BY r.exampaperNum )p on e.examPaperNum=p.examPaperNum where e.examNum={examNum}", RegExaminee.class, args);
                if (_queryBeanList.size() > 0) {
                    Map<String, Integer> reg_map = new HashMap<>();
                    int examPaperNum = ((RegExaminee) _queryBeanList.get(0)).getExamPaperNum();
                    for (int i = 0; i < _queryBeanList.size(); i++) {
                        RegExaminee reg = (RegExaminee) _queryBeanList.get(i);
                        if (examPaperNum != reg.getExamPaperNum()) {
                            regTotal_map.put(String.valueOf(examPaperNum), reg_map);
                            reg_map = new HashMap<>();
                            examPaperNum = reg.getExamPaperNum();
                        }
                        reg_map.put(reg.getExt1(), Integer.valueOf(Integer.parseInt(reg.getExt2())));
                        if (i == _queryBeanList.size() - 1) {
                            regTotal_map.put(String.valueOf(examPaperNum), reg_map);
                        }
                    }
                }
                Map args1 = new HashMap();
                args1.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.exampaperNum,-1 ext1,count(DISTINCT r.studentId) ext2 from regexaminee r  INNER JOIN student s on r.studentId=s.id   INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  INNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum  WHERE  e1.examnum={examNum}  and  r.scan_import=0  GROUP BY r.exampaperNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum}", RegExaminee.class, args1);
                if (_queryBeanList2.size() > 0) {
                    Map<String, Integer> reg_map2 = new HashMap<>();
                    int examPaperNum2 = ((RegExaminee) _queryBeanList2.get(0)).getExamPaperNum();
                    for (int i2 = 0; i2 < _queryBeanList2.size(); i2++) {
                        RegExaminee reg2 = (RegExaminee) _queryBeanList2.get(i2);
                        if (examPaperNum2 != reg2.getExamPaperNum()) {
                            regXuan_map.put(String.valueOf(examPaperNum2), reg_map2);
                            reg_map2 = new HashMap<>();
                            examPaperNum2 = reg2.getExamPaperNum();
                        }
                        reg_map2.put(reg2.getExt1(), Integer.valueOf(Integer.parseInt(reg2.getExt2())));
                        if (i2 == _queryBeanList2.size() - 1) {
                            regXuan_map.put(String.valueOf(examPaperNum2), reg_map2);
                        }
                    }
                }
                Map args2 = new HashMap();
                args2.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList3 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.exampaperNum,-1 ext1,count( DISTINCT r.studentId) ext2 from regexaminee r LEFT JOIN( \tSELECT r.studentId,r.exampapernum from regexaminee r  \tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum \tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE e1.examnum={examNum}  and r.scan_import=0 )r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum  INNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3 WHERE e1.examnum={examNum}  and r.scan_import=0 and r1.studentId is null GROUP BY r.exampaperNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum}", RegExaminee.class, args2);
                if (_queryBeanList3.size() > 0) {
                    Map<String, Integer> reg_map3 = new HashMap<>();
                    int examPaperNum3 = ((RegExaminee) _queryBeanList3.get(0)).getExamPaperNum();
                    for (int i3 = 0; i3 < _queryBeanList3.size(); i3++) {
                        RegExaminee reg3 = (RegExaminee) _queryBeanList3.get(i3);
                        if (examPaperNum3 != reg3.getExamPaperNum()) {
                            regXue_map.put(String.valueOf(examPaperNum3), reg_map3);
                            reg_map3 = new HashMap<>();
                            examPaperNum3 = reg3.getExamPaperNum();
                        }
                        reg_map3.put(reg3.getExt1(), Integer.valueOf(Integer.parseInt(reg3.getExt2())));
                        if (i3 == _queryBeanList3.size() - 1) {
                            regXue_map.put(String.valueOf(examPaperNum3), reg_map3);
                        }
                    }
                }
                Map args3 = new HashMap();
                args3.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList4 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.exampaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(DISTINCT r.studentId),0) ext2 from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum  INNER JOIN schoolgroup slg on r.schoolNum=slg.schoolNum  WHERE e.examnum={examNum}  and r.scan_import=0  GROUP BY r.exampaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum}", RegExaminee.class, args3);
                if (_queryBeanList4.size() > 0) {
                    Map<String, Integer> reg_map4 = new HashMap<>();
                    int examPaperNum4 = ((RegExaminee) _queryBeanList4.get(0)).getExamPaperNum();
                    for (int i4 = 0; i4 < _queryBeanList4.size(); i4++) {
                        RegExaminee reg4 = (RegExaminee) _queryBeanList4.get(i4);
                        if (examPaperNum4 != reg4.getExamPaperNum()) {
                            regTotalIn_map.put(String.valueOf(examPaperNum4), reg_map4);
                            reg_map4 = new HashMap<>();
                            examPaperNum4 = reg4.getExamPaperNum();
                        }
                        reg_map4.put(reg4.getExt1(), Integer.valueOf(Integer.parseInt(reg4.getExt2())));
                        if (i4 == _queryBeanList4.size() - 1) {
                            regTotalIn_map.put(String.valueOf(examPaperNum4), reg_map4);
                        }
                    }
                }
                Map args4 = new HashMap();
                args4.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList5 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.exampaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(DISTINCT r.studentId),0) ext2 from regexaminee r  INNER JOIN student s on r.studentId=s.id   INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  INNER JOIN schoolgroup slg on r.schoolNum=slg.schoolNum   INNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum  WHERE e1.examnum={examNum} and r.scan_import=0 GROUP BY r.exampaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum}", RegExaminee.class, args4);
                if (_queryBeanList5.size() > 0) {
                    Map<String, Integer> reg_map5 = new HashMap<>();
                    int examPaperNum5 = ((RegExaminee) _queryBeanList5.get(0)).getExamPaperNum();
                    for (int i5 = 0; i5 < _queryBeanList5.size(); i5++) {
                        RegExaminee reg5 = (RegExaminee) _queryBeanList5.get(i5);
                        if (examPaperNum5 != reg5.getExamPaperNum()) {
                            regXuanIn_map.put(String.valueOf(examPaperNum5), reg_map5);
                            reg_map5 = new HashMap<>();
                            examPaperNum5 = reg5.getExamPaperNum();
                        }
                        reg_map5.put(reg5.getExt1(), Integer.valueOf(Integer.parseInt(reg5.getExt2())));
                        if (i5 == _queryBeanList5.size() - 1) {
                            regXuanIn_map.put(String.valueOf(examPaperNum5), reg_map5);
                        }
                    }
                }
                String sql = "SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.examPaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(DISTINCT r.studentId),0) ext2 from( \tSELECT r.examPaperNum,r.schoolNum,r.studentId from regexaminee r  \tLEFT JOIN( \t\tSELECT r.studentId,r.exampapernum from regexaminee r INNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum \t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE e1.examNum=" + examNum + "  and r.scan_import=0 \t)r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum  \tINNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3 \tWHERE e1.examNum={examNum}  and r.scan_import=0 and r1.studentId is null )r INNER JOIN schoolgroup slg on r.schoolNum=slg.schoolNum  GROUP BY r.examPaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum}";
                Map args5 = new HashMap();
                args5.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList6 = this.dao2._queryBeanList(sql, RegExaminee.class, args5);
                if (_queryBeanList6.size() > 0) {
                    Map<String, Integer> reg_map6 = new HashMap<>();
                    int examPaperNum6 = ((RegExaminee) _queryBeanList6.get(0)).getExamPaperNum();
                    for (int i6 = 0; i6 < _queryBeanList6.size(); i6++) {
                        RegExaminee reg6 = (RegExaminee) _queryBeanList6.get(i6);
                        if (examPaperNum6 != reg6.getExamPaperNum()) {
                            regXueIn_map.put(String.valueOf(examPaperNum6), reg_map6);
                            reg_map6 = new HashMap<>();
                            examPaperNum6 = reg6.getExamPaperNum();
                        }
                        reg_map6.put(reg6.getExt1(), Integer.valueOf(Integer.parseInt(reg6.getExt2())));
                        if (i6 == _queryBeanList6.size() - 1) {
                            regXueIn_map.put(String.valueOf(examPaperNum6), reg_map6);
                        }
                    }
                }
            }
            map.put("regTotal", regTotal_map);
            map.put("regTotalIn", regTotalIn_map);
            map.put("regXuan", regXuan_map);
            map.put("regXuanIn", regXuanIn_map);
            map.put("regXue", regXue_map);
            map.put("regXueIn", regXueIn_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Map<String, Integer>> getDataFormBigTableRegChoose() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> regChooseTotal_map = new HashMap<>();
        Map<String, Integer> regChooseTotalIn_map = new HashMap<>();
        Map<String, Integer> regChooseXuan_map = new HashMap<>();
        Map<String, Integer> regChooseXuanIn_map = new HashMap<>();
        Map<String, Integer> regChooseXue_map = new HashMap<>();
        Map<String, Integer> regChooseXueIn_map = new HashMap<>();
        try {
            List examList = this.dao2._queryColList("SELECT examNum from exam where status <>9 and isDelete='F'", null);
            for (int h = 0; h < examList.size(); h++) {
                String examNum = String.valueOf(examList.get(h));
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList = this.dao2._queryBeanList("SELECT d.id,d.choosename,d.questionnum,d.examPaperNum from exam e LEFT JOIN exampaper ep on e.examNum=ep.examNum  LEFT JOIN define d on ep.examPaperNum=d.examPaperNum  where e.examNum={examNum} and d.choosename='T' ", Define.class, args);
                for (int i = 0; i < _queryBeanList.size(); i++) {
                    Define d = (Define) _queryBeanList.get(i);
                    Map args1 = new HashMap();
                    args1.put("choosename", d.getId());
                    String isParent = this.dao2._queryStr("SELECT isParent from define where choosename={choosename}", args1);
                    if ("0".equals(isParent)) {
                        Map args2 = new HashMap();
                        args2.put("examPaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args2.put("choosename", d.getId());
                        List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',d.id) id,count(1) ext1 from define d LEFT JOIN task t on d.id=t.groupNum where d.examPaperNum={examPaperNum} and d.choosename={choosename} GROUP BY d.id", Task.class, args2);
                        for (int j = 0; j < _queryBeanList2.size(); j++) {
                            Task t = (Task) _queryBeanList2.get(j);
                            regChooseTotal_map.put(t.getId(), Integer.valueOf(Integer.parseInt(t.getExt1())));
                        }
                        Map args3 = new HashMap();
                        args3.put("examPaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args3.put("choosename", d.getId());
                        List<?> _queryBeanList3 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',d.id) id,count(1) ext1 from define d LEFT JOIN task t on d.id=t.groupNum INNER JOIN student s ON t.studentId = s.id  INNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum  INNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum  where d.examPaperNum={examPaperNum} and d.choosename={choosename} GROUP BY d.id ", Task.class, args3);
                        for (int j2 = 0; j2 < _queryBeanList3.size(); j2++) {
                            Task t2 = (Task) _queryBeanList3.get(j2);
                            regChooseXuan_map.put(t2.getId(), Integer.valueOf(Integer.parseInt(t2.getExt1())));
                        }
                        Map args4 = new HashMap();
                        args4.put("exampaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args4.put("choosename", d.getId());
                        List<?> _queryBeanList4 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',d.id) id,count(1) ext1 from define d LEFT JOIN ( \tSELECT t.exampaperNum,t.groupNum,count(DISTINCT t.studentId) count FROM task t LEFT JOIN ( \t\t\tSELECT t.id FROM task t INNER JOIN student s ON t.studentId = s.id \t\t\tINNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum \t\tINNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum \t\tWHERE t.exampaperNum = {exampaperNum} \t) t1 ON t.id = t1.id WHERE t.exampaperNum = {exampaperNum} AND t1.id IS NULL ) t on d.id=t.groupNum where d.examPaperNum={exampaperNum} and d.choosename={choosename} GROUP BY d.id", Task.class, args4);
                        for (int j3 = 0; j3 < _queryBeanList4.size(); j3++) {
                            Task t3 = (Task) _queryBeanList4.get(j3);
                            regChooseXue_map.put(t3.getId(), Integer.valueOf(Integer.parseInt(t3.getExt1())));
                        }
                        Map args5 = new HashMap();
                        args5.put("examPaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args5.put("choosename", d.getId());
                        List<?> _queryBeanList5 = this.dao2._queryBeanList("SELECT CONCAT(sg.schoolGroupNum,'-',d.id) id,count(1) ext1 from define d LEFT JOIN task t on d.id=t.groupNum INNER JOIN student s ON t.studentId = s.id  INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum where d.examPaperNum={examPaperNum} and d.choosename={choosename} GROUP BY d.id,sg.schoolGroupNum", Task.class, args5);
                        for (int j4 = 0; j4 < _queryBeanList5.size(); j4++) {
                            Task t4 = (Task) _queryBeanList5.get(j4);
                            regChooseTotalIn_map.put(t4.getId(), Integer.valueOf(Integer.parseInt(t4.getExt1())));
                        }
                        Map args6 = new HashMap();
                        args6.put("examPaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args6.put("choosename", d.getId());
                        List<?> _queryBeanList6 = this.dao2._queryBeanList("SELECT CONCAT(sg.schoolGroupNum,'-',d.id) id,count(1) ext1 from define d LEFT JOIN task t on d.id=t.groupNum  INNER JOIN student s ON t.studentId = s.id  INNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum  INNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum  INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum  where d.examPaperNum={examPaperNum} and d.choosename={choosename} GROUP BY d.id,sg.schoolGroupNum", Task.class, args6);
                        for (int j5 = 0; j5 < _queryBeanList6.size(); j5++) {
                            Task t5 = (Task) _queryBeanList6.get(j5);
                            regChooseXuanIn_map.put(t5.getId(), Integer.valueOf(Integer.parseInt(t5.getExt1())));
                        }
                        Map args7 = new HashMap();
                        args7.put("exampaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args7.put("choosename", d.getId());
                        List<?> _queryBeanList7 = this.dao2._queryBeanList("SELECT CONCAT(t.schoolGroupNum,'-',d.id) id,count(1) ext1 from define d LEFT JOIN ( \tSELECT t.exampaperNum,t.groupNum,sg.schoolGroupNum,count(DISTINCT t.studentId) count FROM task t LEFT JOIN ( \t\t\tSELECT t.id FROM task t \t\t\tINNER JOIN student s ON t.studentId = s.id \t\t\tINNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum \t\t\tINNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum \t\t\tWHERE t.exampaperNum ={exampaperNum} \t\t) t1 ON t.id = t1.id  INNER JOIN student s ON t.studentId = s.id  INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum WHERE t.exampaperNum = {exampaperNum} AND t1.id IS NULL \t) t on d.id=t.groupNum where d.examPaperNum={exampaperNum} and d.choosename={choosename} GROUP BY d.id,t.schoolGroupNum", Task.class, args7);
                        for (int j6 = 0; j6 < _queryBeanList7.size(); j6++) {
                            Task t6 = (Task) _queryBeanList7.get(j6);
                            regChooseXueIn_map.put(t6.getId(), Integer.valueOf(Integer.parseInt(t6.getExt1())));
                        }
                    } else {
                        Map args8 = new HashMap();
                        args8.put("exampaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args8.put("choosename", d.getId());
                        List<?> _queryBeanList8 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN task t on sd.id=t.groupNum where d.examPaperNum={exampaperNum} and d.choosename={choosename} GROUP BY sd.id", Task.class, args8);
                        for (int j7 = 0; j7 < _queryBeanList8.size(); j7++) {
                            Task t7 = (Task) _queryBeanList8.get(j7);
                            regChooseTotal_map.put(t7.getId(), Integer.valueOf(Integer.parseInt(t7.getExt1())));
                        }
                        Map args9 = new HashMap();
                        args9.put("exampaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args9.put("choosename", d.getId());
                        List<?> _queryBeanList9 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN task t on sd.id=t.groupNum  INNER JOIN student s ON t.studentId = s.id  INNER JOIN subjectcombinedetail sdb ON s.subjectCombineNum = sdb.subjectCombineNum  INNER JOIN exampaper e ON sdb.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum  where d.examPaperNum={exampaperNum} and d.choosename={choosename} GROUP BY sd.id ", Task.class, args9);
                        for (int j8 = 0; j8 < _queryBeanList9.size(); j8++) {
                            Task t8 = (Task) _queryBeanList9.get(j8);
                            regChooseXuan_map.put(t8.getId(), Integer.valueOf(Integer.parseInt(t8.getExt1())));
                        }
                        Map args10 = new HashMap();
                        args10.put("exampaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args10.put("choosename", d.getId());
                        List<?> _queryBeanList10 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN ( \tSELECT t.exampaperNum,t.groupNum,count(DISTINCT t.studentId) count FROM task t LEFT JOIN ( \t\t\tSELECT t.id FROM task t \t\t\tINNER JOIN student s ON t.studentId = s.id \t\t\tINNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum \t\t\tINNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum \t\t\tWHERE t.exampaperNum ={exampaperNum} \t\t) t1 ON t.id = t1.id WHERE t.exampaperNum = {exampaperNum} AND t1.id IS NULL \t) t on sd.id=t.groupNum where d.examPaperNum={exampaperNum} and d.choosename={choosename} GROUP BY sd.id ", Task.class, args10);
                        for (int j9 = 0; j9 < _queryBeanList10.size(); j9++) {
                            Task t9 = (Task) _queryBeanList10.get(j9);
                            regChooseXue_map.put(t9.getId(), Integer.valueOf(Integer.parseInt(t9.getExt1())));
                        }
                        Map args11 = new HashMap();
                        args11.put("exampaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args11.put("choosename", d.getId());
                        List<?> _queryBeanList11 = this.dao2._queryBeanList("SELECT CONCAT(sg.schoolGroupNum,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN task t on sd.id=t.groupNum  INNER JOIN student s ON t.studentId = s.id INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum  where d.examPaperNum={exampaperNum} and d.choosename={choosename} GROUP BY sd.id,sg.schoolGroupNum", Task.class, args11);
                        for (int j10 = 0; j10 < _queryBeanList11.size(); j10++) {
                            Task t10 = (Task) _queryBeanList11.get(j10);
                            regChooseTotalIn_map.put(t10.getId(), Integer.valueOf(Integer.parseInt(t10.getExt1())));
                        }
                        Map args12 = new HashMap();
                        args12.put("exampaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args12.put("choosename", d.getId());
                        List<?> _queryBeanList12 = this.dao2._queryBeanList("SELECT CONCAT(sg.schoolGroupNum,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN task t on sd.id=t.groupNum  INNER JOIN student s ON t.studentId = s.id  INNER JOIN subjectcombinedetail sdb ON s.subjectCombineNum = sdb.subjectCombineNum  INNER JOIN exampaper e ON sdb.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum  INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum   where d.examPaperNum={exampaperNum} and d.choosename={choosename} GROUP BY sd.id,sg.schoolGroupNum", Task.class, args12);
                        for (int j11 = 0; j11 < _queryBeanList12.size(); j11++) {
                            Task t11 = (Task) _queryBeanList12.get(j11);
                            regChooseXuanIn_map.put(t11.getId(), Integer.valueOf(Integer.parseInt(t11.getExt1())));
                        }
                        Map args13 = new HashMap();
                        args13.put("exampaperNum", Integer.valueOf(d.getExamPaperNum()));
                        args13.put("choosename", d.getId());
                        List<?> _queryBeanList13 = this.dao2._queryBeanList("SELECT CONCAT(t.schoolGroupNum,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN ( \tSELECT t.exampaperNum,t.groupNum,sg.schoolGroupNum,count(DISTINCT t.studentId) count FROM task t LEFT JOIN ( \t\t\tSELECT t.id FROM task t \t\t\tINNER JOIN student s ON t.studentId = s.id \t\t\tINNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum \t\t\tINNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum \t\t\tWHERE t.exampaperNum ={exampaperNum}\t\t) t1 ON t.id = t1.id  INNER JOIN student s ON t.studentId = s.id  INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum WHERE t.exampaperNum = {exampaperNum} AND t1.id IS NULL \t) t on sd.id=t.groupNum where d.examPaperNum={exampaperNum} and d.choosename={choosename} GROUP BY sd.id,t.schoolGroupNum", Task.class, args13);
                        for (int j12 = 0; j12 < _queryBeanList13.size(); j12++) {
                            Task t12 = (Task) _queryBeanList13.get(j12);
                            regChooseXueIn_map.put(t12.getId(), Integer.valueOf(Integer.parseInt(t12.getExt1())));
                        }
                    }
                }
            }
            map.put("regChooseTotal", regChooseTotal_map);
            map.put("regChooseTotalIn", regChooseTotalIn_map);
            map.put("regChooseXuan", regChooseXuan_map);
            map.put("regChooseXuanIn", regChooseXuanIn_map);
            map.put("regChooseXue", regChooseXue_map);
            map.put("regChooseXueIn", regChooseXueIn_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Map<String, Integer>> getDataFormBigTableExaminaChoose() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> examinaChooseTotal_map = new HashMap<>();
        Map<String, Integer> examinaChooseTotalIn_map = new HashMap<>();
        Map<String, Integer> examinaChooseXuan_map = new HashMap<>();
        Map<String, Integer> examinaChooseXuanIn_map = new HashMap<>();
        Map<String, Integer> examinaChooseXue_map = new HashMap<>();
        Map<String, Integer> examinaChooseXueIn_map = new HashMap<>();
        try {
            List examList = this.dao2._queryColList("SELECT examNum from exam where status <>9 and isDelete='F'", null);
            for (int h = 0; h < examList.size(); h++) {
                String examNum = String.valueOf(examList.get(h));
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList = this.dao2._queryBeanList("SELECT  CONCAT(-1,'-',p.examPaperNum) id,count(n.studentId) ext1 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  where n.examNum={examNum} GROUP BY examPaperNum", Task.class, args);
                for (int j = 0; j < _queryBeanList.size(); j++) {
                    Task t = (Task) _queryBeanList.get(j);
                    examinaChooseTotal_map.put(t.getId(), Integer.valueOf(Integer.parseInt(t.getExt1())));
                }
                Map args1 = new HashMap();
                args1.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT  CONCAT('-1','-',p.examPaperNum) id,count(n.studentId) ext1 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and p.examPaperNum=e2.pexampaperNum  where n.examNum={examNum} GROUP BY p.examPaperNum", Task.class, args1);
                for (int j2 = 0; j2 < _queryBeanList2.size(); j2++) {
                    Task t2 = (Task) _queryBeanList2.get(j2);
                    examinaChooseXuan_map.put(t2.getId(), Integer.valueOf(Integer.parseInt(t2.getExt1())));
                }
                Map args2 = new HashMap();
                args2.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList3 = this.dao2._queryBeanList("SELECT CONCAT('-1','-',p.examPaperNum) id,count(n.studentId) ext1  from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  LEFT JOIN (  \tSELECT n.studentId,e2.pexamPaperNum from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  \tINNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   \tINNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and p.examPaperNum=e2.pexampaperNum WHERE n.examNum={examNum}  )n1 on n1.pexamPaperNum=p.examPaperNum and n.studentId=n1.studentId   INNER JOIN exampaper e2 on p.examPaperNum=e2.pexamPaperNum and e2.xuankaoqufen=3  WHERE n.examNum={examNum} and n1.studentId is null GROUP BY p.examPaperNum  ", Task.class, args2);
                for (int j3 = 0; j3 < _queryBeanList3.size(); j3++) {
                    Task t3 = (Task) _queryBeanList3.get(j3);
                    examinaChooseXue_map.put(t3.getId(), Integer.valueOf(Integer.parseInt(t3.getExt1())));
                }
                Map args3 = new HashMap();
                args3.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList4 = this.dao2._queryBeanList("SELECT  CONCAT(sg.schoolGroupNum,'-',p.examPaperNum) id,count(n.studentId) ext1 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  INNER JOIN schoolgroup sg ON n.schoolNum = sg.schoolNum where n.examNum={examNum} GROUP BY examPaperNum,sg.schoolGroupNum", Task.class, args3);
                for (int j4 = 0; j4 < _queryBeanList4.size(); j4++) {
                    Task t4 = (Task) _queryBeanList4.get(j4);
                    examinaChooseTotalIn_map.put(t4.getId(), Integer.valueOf(Integer.parseInt(t4.getExt1())));
                }
                Map args4 = new HashMap();
                args4.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList5 = this.dao2._queryBeanList("SELECT  CONCAT(sg.schoolGroupNum,'-',p.examPaperNum) id,count(n.studentId) ext1 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  INNER JOIN schoolgroup sg ON n.schoolNum = sg.schoolNum  INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and p.examPaperNum=e2.pexampaperNum  where n.examNum={examNum} GROUP BY p.examPaperNum,sg.schoolGroupNum", Task.class, args4);
                for (int j5 = 0; j5 < _queryBeanList5.size(); j5++) {
                    Task t5 = (Task) _queryBeanList5.get(j5);
                    examinaChooseXuanIn_map.put(t5.getId(), Integer.valueOf(Integer.parseInt(t5.getExt1())));
                }
                Map args5 = new HashMap();
                args5.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList6 = this.dao2._queryBeanList("SELECT CONCAT(sg.schoolGroupNum,'-',p.examPaperNum) id,count(n.studentId) ext1  from examinationnum n INNER JOIN schoolgroup sg ON n.schoolNum = sg.schoolNum  INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  LEFT JOIN (  \tSELECT n.studentId,e2.pexamPaperNum from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  \tINNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   \tINNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and p.examPaperNum=e2.pexampaperNum WHERE n.examNum={examNum}  )n1 on n1.pexamPaperNum=p.examPaperNum and n.studentId=n1.studentId   INNER JOIN exampaper e2 on p.examPaperNum=e2.pexamPaperNum and e2.xuankaoqufen=3   WHERE n.examNum={examNum} and n1.studentId is null GROUP BY p.examPaperNum,sg.schoolGroupNum", Task.class, args5);
                for (int j6 = 0; j6 < _queryBeanList6.size(); j6++) {
                    Task t6 = (Task) _queryBeanList6.get(j6);
                    examinaChooseXueIn_map.put(t6.getId(), Integer.valueOf(Integer.parseInt(t6.getExt1())));
                }
            }
            map.put("examinaChooseTotal", examinaChooseTotal_map);
            map.put("examinaChooseTotalIn", examinaChooseTotalIn_map);
            map.put("examinaChooseXuan", examinaChooseXuan_map);
            map.put("examinaChooseXuanIn", examinaChooseXuanIn_map);
            map.put("examinaChooseXue", examinaChooseXue_map);
            map.put("examinaChooseXueIn", examinaChooseXueIn_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Map<String, Map<String, Integer>>> getDataFormBigTableExamina() {
        Map<String, Map<String, Map<String, Integer>>> map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaTotal_map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaXuan_map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaXue_map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaTotalIn_map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaXuanIn_map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaXueIn_map = new HashMap<>();
        try {
            List examList = this.dao2._queryColList("SELECT examNum from exam where status <>9 and isDelete='F' ", null);
            for (int h = 0; h < examList.size(); h++) {
                String examNum = String.valueOf(examList.get(h));
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT p.examPaperNum,-1 ext1,count(n.studentId) ext2 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  where n.examNum={examNum} GROUP BY examPaperNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum}", RegExaminee.class, args);
                if (_queryBeanList.size() > 0) {
                    Map<String, Integer> examina_map = new HashMap<>();
                    int examPaperNum = ((RegExaminee) _queryBeanList.get(0)).getExamPaperNum();
                    for (int i = 0; i < _queryBeanList.size(); i++) {
                        RegExaminee reg = (RegExaminee) _queryBeanList.get(i);
                        if (examPaperNum != reg.getExamPaperNum()) {
                            examinaTotal_map.put(String.valueOf(examPaperNum), examina_map);
                            examina_map = new HashMap<>();
                            examPaperNum = reg.getExamPaperNum();
                        }
                        examina_map.put(reg.getExt1(), Integer.valueOf(Integer.parseInt(reg.getExt2())));
                        if (i == _queryBeanList.size() - 1) {
                            examinaTotal_map.put(String.valueOf(examPaperNum), examina_map);
                        }
                    }
                }
                Map args1 = new HashMap();
                args1.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT e1.examPaperNum,-1 ext1,count(n.studentId) ext2 from examinationnum n INNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum  INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and e1.examPaperNum=e2.pexampaperNum WHERE n.examNum={examNum} GROUP BY e1.examPaperNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum}", RegExaminee.class, args1);
                if (_queryBeanList2.size() > 0) {
                    Map<String, Integer> examina_map2 = new HashMap<>();
                    int examPaperNum2 = ((RegExaminee) _queryBeanList2.get(0)).getExamPaperNum();
                    for (int i2 = 0; i2 < _queryBeanList2.size(); i2++) {
                        RegExaminee reg2 = (RegExaminee) _queryBeanList2.get(i2);
                        if (examPaperNum2 != reg2.getExamPaperNum()) {
                            examinaXuan_map.put(String.valueOf(examPaperNum2), examina_map2);
                            examina_map2 = new HashMap<>();
                            examPaperNum2 = reg2.getExamPaperNum();
                        }
                        examina_map2.put(reg2.getExt1(), Integer.valueOf(Integer.parseInt(reg2.getExt2())));
                        if (i2 == _queryBeanList2.size() - 1) {
                            examinaXuan_map.put(String.valueOf(examPaperNum2), examina_map2);
                        }
                    }
                }
                Map args2 = new HashMap();
                args2.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList3 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT e1.examPaperNum,-1 ext1,count(n.studentId) ext2 from examinationnum n INNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum LEFT JOIN ( \tSELECT n.studentId,e2.pexamPaperNum from examinationnum n INNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum \tINNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum \tand e1.examPaperNum=e2.pexampaperNum WHERE n.examNum={examNum}  )n1 on n1.pexamPaperNum=e1.examPaperNum and n.studentId=n1.studentId INNER JOIN exampaper e2 on e1.examPaperNum=e2.pexamPaperNum and e2.xuankaoqufen=3 WHERE n.examNum={examNum} and n1.studentId is null GROUP BY e1.examPaperNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum}", RegExaminee.class, args2);
                if (_queryBeanList3.size() > 0) {
                    Map<String, Integer> examina_map3 = new HashMap<>();
                    int examPaperNum3 = ((RegExaminee) _queryBeanList3.get(0)).getExamPaperNum();
                    for (int i3 = 0; i3 < _queryBeanList3.size(); i3++) {
                        RegExaminee reg3 = (RegExaminee) _queryBeanList3.get(i3);
                        if (examPaperNum3 != reg3.getExamPaperNum()) {
                            examinaXue_map.put(String.valueOf(examPaperNum3), examina_map3);
                            examina_map3 = new HashMap<>();
                            examPaperNum3 = reg3.getExamPaperNum();
                        }
                        examina_map3.put(reg3.getExt1(), Integer.valueOf(Integer.parseInt(reg3.getExt2())));
                        if (i3 == _queryBeanList3.size() - 1) {
                            examinaXue_map.put(String.valueOf(examPaperNum3), examina_map3);
                        }
                    }
                }
                Map args3 = new HashMap();
                args3.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList4 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT p.examPaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(1),0) ext2 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  INNER JOIN schoolgroup slg on n.schoolNum=slg.schoolNum WHERE n.examNum={examNum}  GROUP BY p.examPaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args3);
                if (_queryBeanList4.size() > 0) {
                    Map<String, Integer> examina_map4 = new HashMap<>();
                    int examPaperNum4 = ((RegExaminee) _queryBeanList4.get(0)).getExamPaperNum();
                    for (int i4 = 0; i4 < _queryBeanList4.size(); i4++) {
                        RegExaminee reg4 = (RegExaminee) _queryBeanList4.get(i4);
                        if (examPaperNum4 != reg4.getExamPaperNum()) {
                            examinaTotalIn_map.put(String.valueOf(examPaperNum4), examina_map4);
                            examina_map4 = new HashMap<>();
                            examPaperNum4 = reg4.getExamPaperNum();
                        }
                        examina_map4.put(reg4.getExt1(), Integer.valueOf(Integer.parseInt(reg4.getExt2())));
                        if (i4 == _queryBeanList4.size() - 1) {
                            examinaTotalIn_map.put(String.valueOf(examPaperNum4), examina_map4);
                        }
                    }
                }
                Map args4 = new HashMap();
                args4.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList5 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT e1.examPaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(1),0) ext2  from examinationnum n INNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum  INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and e1.examPaperNum=e2.pexampaperNum   INNER JOIN schoolgroup slg on n.schoolNum=slg.schoolNum   WHERE n.examNum={examNum} GROUP BY e1.examPaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum}", RegExaminee.class, args4);
                if (_queryBeanList5.size() > 0) {
                    Map<String, Integer> examina_map5 = new HashMap<>();
                    int examPaperNum5 = ((RegExaminee) _queryBeanList5.get(0)).getExamPaperNum();
                    for (int i5 = 0; i5 < _queryBeanList5.size(); i5++) {
                        RegExaminee reg5 = (RegExaminee) _queryBeanList5.get(i5);
                        if (examPaperNum5 != reg5.getExamPaperNum()) {
                            examinaXuanIn_map.put(String.valueOf(examPaperNum5), examina_map5);
                            examina_map5 = new HashMap<>();
                            examPaperNum5 = reg5.getExamPaperNum();
                        }
                        examina_map5.put(reg5.getExt1(), Integer.valueOf(Integer.parseInt(reg5.getExt2())));
                        if (i5 == _queryBeanList5.size() - 1) {
                            examinaXuanIn_map.put(String.valueOf(examPaperNum5), examina_map5);
                        }
                    }
                }
                Map args5 = new HashMap();
                args5.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList6 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT e1.examPaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(1),0) ext2 from examinationnum n INNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum  LEFT JOIN (  \tSELECT n.studentId,e2.pexamPaperNum from examinationnum n  \tINNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum  \tINNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \tINNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and e1.examPaperNum=e2.pexampaperNum WHERE n.examNum={examNum}  )n1 on n1.pexamPaperNum=e1.examPaperNum and n.studentId=n1.studentId INNER JOIN exampaper e2 on e1.examPaperNum=e2.pexamPaperNum and e2.xuankaoqufen=3  INNER JOIN schoolgroup slg on n.schoolNum=slg.schoolNum WHERE n.examNum={examNum} and n1.studentId is null GROUP BY e1.examPaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args5);
                if (_queryBeanList6.size() > 0) {
                    Map<String, Integer> examina_map6 = new HashMap<>();
                    int examPaperNum6 = ((RegExaminee) _queryBeanList6.get(0)).getExamPaperNum();
                    for (int i6 = 0; i6 < _queryBeanList6.size(); i6++) {
                        RegExaminee reg6 = (RegExaminee) _queryBeanList6.get(i6);
                        if (examPaperNum6 != reg6.getExamPaperNum()) {
                            examinaXueIn_map.put(String.valueOf(examPaperNum6), examina_map6);
                            examina_map6 = new HashMap<>();
                            examPaperNum6 = reg6.getExamPaperNum();
                        }
                        examina_map6.put(reg6.getExt1(), Integer.valueOf(Integer.parseInt(reg6.getExt2())));
                        if (i6 == _queryBeanList6.size() - 1) {
                            examinaXueIn_map.put(String.valueOf(examPaperNum6), examina_map6);
                        }
                    }
                }
            }
            map.put("examinaTotal", examinaTotal_map);
            map.put("examinaTotalIn", examinaTotalIn_map);
            map.put("examinaXuan", examinaXuan_map);
            map.put("examinaXuanIn", examinaXuanIn_map);
            map.put("examinaXue", examinaXue_map);
            map.put("examinaXueIn", examinaXueIn_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public byte[] getImage(String location, String img) {
        return this.examDao.splitimgurl(location, img);
    }

    public boolean getYueStatusFromGroup(String groupNum) {
        boolean flag = true;
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        Object info = this.dao2._queryObject("SELECT   examPaperNum from questiongroup where groupNum={groupNum} and scancompleted=1 ", args);
        if (info == null) {
            flag = false;
        } else {
            String examPaperNum = String.valueOf(info);
            Map args1 = new HashMap();
            args1.put("examPaperNum", examPaperNum);
            String caCount = this.dao2._queryStr("SELECT   IFNULL(count(1),0) from cantrecognized where examPaperNum={examPaperNum} ", args1);
            if ("null" != caCount && !"0".equals(caCount)) {
                flag = false;
            } else if ("0".equals(caCount)) {
                Map args2 = new HashMap();
                args2.put("groupNum", groupNum);
                String tCount = this.dao2._queryStr("SELECT   count(1) count from task where groupNum={groupNum} and STATUS='F' ", args2);
                if ("null" != tCount && !"0".equals(tCount)) {
                    flag = false;
                } else if ("0".equals(tCount)) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.ExportService
    public void removeExcelColumn(WritableSheet sheet, List<Integer> removeSubList, int rLen, Set leibieCell7, Set xuankezuheCell6) {
        if (null != removeSubList && removeSubList.size() > 0) {
            for (int l = removeSubList.size() - 1; l >= 0; l--) {
                Integer subStart = removeSubList.get(l);
                for (int r = rLen; r > 0; r--) {
                    sheet.removeColumn(subStart.intValue() + r);
                }
            }
        }
        if (leibieCell7.size() < 2) {
            sheet.removeColumn(7);
        }
        if (xuankezuheCell6.size() < 2) {
            sheet.removeColumn(6);
        }
    }

    @Override // com.dmj.service.examManagement.ExportService
    public void removeOneSubExcelColumn(WritableSheet sheet, List<Integer> removeSubList, Set leibieCell8, Set xuankezuheCell7) {
        if (removeSubList.size() > 0) {
            for (int l = removeSubList.size() - 1; l >= 0; l--) {
                Integer subStart = removeSubList.get(l);
                sheet.removeColumn(subStart.intValue());
            }
        }
        if (leibieCell8.size() < 2) {
            sheet.removeColumn(8);
        }
        if (xuankezuheCell7.size() < 2) {
            sheet.removeColumn(7);
        }
    }

    public void removeOneSubExcelColumn_poi(Sheet sheet, List<Integer> removeSubList, Set leibieCell8, Set xuankezuheCell7) {
        if (removeSubList.size() > 0) {
            for (int l = removeSubList.size() - 1; l >= 0; l--) {
                Integer subStart = removeSubList.get(l);
                removeColumn_poi(sheet, subStart.intValue());
            }
        }
        if (leibieCell8.size() < 2) {
            removeColumn_poi(sheet, 8);
        }
        if (xuankezuheCell7.size() < 2) {
            removeColumn_poi(sheet, 7);
        }
    }

    public void removeColumn_poi(Sheet sheet, int removeColumnNum) {
        if (sheet == null) {
            return;
        }
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell cell = row.getCell(removeColumnNum);
            if (cell != null) {
                row.removeCell(cell);
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExportService
    public void removeDetailExcelColumn(WritableSheet sheet, Set leibieCell, int leibieCellIndex, Set xuankezuheCell, int xuankezuheCellIndex) {
        if (leibieCell.size() < 2) {
            sheet.removeColumn(leibieCellIndex);
        }
        if (xuankezuheCell.size() < 2) {
            sheet.removeColumn(xuankezuheCellIndex);
        }
    }

    @Override // com.dmj.service.examManagement.ExportService
    public Integer getBaseStatistic() {
        return this.dao2._queryInt("SELECT count(1) from statisticitem_school ", null);
    }
}
