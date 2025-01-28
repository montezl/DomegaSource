package com.dmj.serviceimpl.examManagement;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.dmj.auth.bean.License;
import com.dmj.cs.bean.Rectangle;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.examManagement.ExamDAOImpl;
import com.dmj.daoimpl.examManagement.ExamManageDAOImpl;
import com.dmj.daoimpl.systemManagement.SystemDAOImpl;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Astrict;
import com.dmj.domain.Baseinfolog;
import com.dmj.domain.Class;
import com.dmj.domain.Classlevel;
import com.dmj.domain.CorrectStatus;
import com.dmj.domain.Data;
import com.dmj.domain.Define;
import com.dmj.domain.Exam;
import com.dmj.domain.ExamSourceSet;
import com.dmj.domain.Examinationnum;
import com.dmj.domain.Examinationroom;
import com.dmj.domain.ExamineeNumError;
import com.dmj.domain.ExamineeNumGroup;
import com.dmj.domain.Examlog;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Examsetting;
import com.dmj.domain.Grade;
import com.dmj.domain.GroupClass;
import com.dmj.domain.Pjbdata;
import com.dmj.domain.Questionimage;
import com.dmj.domain.RegExaminee;
import com.dmj.domain.School;
import com.dmj.domain.Score;
import com.dmj.domain.ScoreCalden;
import com.dmj.domain.Scoreimage;
import com.dmj.domain.Shangxian;
import com.dmj.domain.StuExamInfo;
import com.dmj.domain.Student;
import com.dmj.domain.Studentlevel;
import com.dmj.domain.Studentpaperimage;
import com.dmj.domain.Subject;
import com.dmj.domain.Testingcentre;
import com.dmj.domain.Testingcentre_school;
import com.dmj.domain.Userrole;
import com.dmj.domain.examManagement.Fourratios_setting;
import com.dmj.domain.leq.EDataParameters;
import com.dmj.domain.leq.Student_examinationNumber;
import com.dmj.domain.vo.OnlineIndicator;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.service.analysisManagement.AnalysisService;
import com.dmj.service.examManagement.ExamManageService;
import com.dmj.service.examManagement.QuestionNumListService;
import com.dmj.service.examManagement.UtilSystemService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.analysisManagement.AnalysisServiceImpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.ChineseCharacterUtil;
import com.dmj.util.Conffig;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.config.Configuration;
import com.dmj.util.excel.CheckCellUtil;
import com.dmj.util.excel.ExcelHelper;
import com.dmj.util.msg.RspMsg;
import com.zht.db.DbUtils;
import com.zht.db.RowArg;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.SubException;
import com.zht.db.TypeEnum;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;

/* loaded from: ExamManageServiceimpl.class */
public class ExamManageServiceimpl implements ExamManageService {
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());
    private ExamManageDAOImpl examManageDAO = new ExamManageDAOImpl();
    private AnalysisService analy = (AnalysisService) ServiceFactory.getObject(new AnalysisServiceImpl());
    private SystemDAOImpl sys = new SystemDAOImpl();
    private ExamDAOImpl examDAO = new ExamDAOImpl();
    private UtilSystemService examroomIdsys = (UtilSystemService) ServiceFactory.getObject(new UtilSystemServiceimpl());
    DecimalFormat df = new DecimalFormat("0");
    private SystemService his = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    private QuestionNumListService questionNumListService = (QuestionNumListService) ServiceFactory.getObject(new QuestionNumListServiceImpl());
    private boolean errorFlag = false;
    private boolean rowBgColor = false;

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> gettopItem() {
        return this.dao._queryMapList(" SELECT s2.* from (SELECT rootId from statisticitem_school  LIMIT 1) s1 LEFT JOIN (SELECT * from statisticitem_school WHERE topItemId=rootId and LEVEL='0' and pitemid=rootId) s2 on s1.rootId=s2.rootID; ", null, null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Integer> getSubjectByExamNum(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryColList("SELECT s.subjectNum from subject s right JOIN ( SELECT  subjectNum from exampaper where examNum={examNum} and isHidden='F') esp on s.subjectNum=esp.subjectNum ORDER BY s.id", Integer.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getAllSubjectByExamNum(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryMapList("select ep.subjectNum,GROUP_CONCAT(distinct sub.subjectName order by sub.orderNum) subjectNames from (select examPaperNum,subjectNum from exampaper where examNum={examNum} and isHidden='F') ep left join (select pexamPaperNum,subjectNum from exampaper where examNum={examNum} and isHidden='T') ep2 on ep2.pexamPaperNum=ep.examPaperNum left join subject sub on sub.subjectNum=ep2.subjectNum group by ep.subjectNum ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<Integer, String> getSubject(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryOrderMap("SELECT DISTINCT s.subjectNum,s.subjectName from subject s right JOIN ( SELECT  subjectNum from exampaper where examNum={examNum} and isHidden='F') esp on s.subjectNum=esp.subjectNum ORDER BY s.orderNum", TypeEnum.IntegerString, args);
    }

    public Student getgradeSchNum(String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return (Student) this.dao._queryBean("select gradeNum,schoolNum from student where studentId={studentId} ", Student.class, args);
    }

    public Map<String, String> getPaperByExamNum(int examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum));
        return this.dao._queryOrderMap("SELECT CONCAT(gradeNum,'_',subjectNum),examPaperNum from exampaper where examNum={examNum} ", TypeEnum.StringString, args);
    }

    public List<String> getClassByPaperSchool(int examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum));
        return this.dao._queryColList("SELECT CONCAT(ce.examPaperNum,'_',ce.schoolNum,'_',ce.classNum) from classexam ce LEFT JOIN exampaper ep ON ce.examPaperNum=ep.examPaperNum WHERE ep.examNum={examNum} and ep.isHidden='F'", String.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<String> getSchoolByExamNum(String examNum, String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao._queryColList("SELECT DISTINCT(ce.schoolNum) from classexam ce  LEFT JOIN  examPaper ep on ce.examPaperNum=ep.examPaperNum WHERE ep.examNum={examNum}  and ep.subjectNum={subjectNum} ", String.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<String> getGradeByExamNum(String examNum, String subjectNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryColList("SELECT DISTINCT(ep.gradeNum) from classexam ce  LEFT JOIN  examPaper ep on ce.examPaperNum=ep.examPaperNum WHERE ep.examNum={examNum}  and ep.subjectNum={subjectNum}  and ce.schoolNum={schoolNum}  order by ep.gradeNum", String.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void delExamRoomInfo(String oldExamNum, String examNum) {
        Map args = StreamMap.create().put("oldExamNum", (Object) oldExamNum);
        List<Object[]> list = this.dao._queryArrayList("SELECT ce.schoolNum,ep.gradeNum  from classexam ce left JOIN exampaper ep on ep.examPaperNum=ce.examPaperNum WHERE ep.examNum={oldExamNum}  GROUP BY ce.schoolNum,ep.gradeNum", args);
        List<RowArg> rowArgList = new ArrayList<>();
        for (Object[] objects : list) {
            Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, objects[1]).put(Const.EXPORTREPORT_schoolNum, objects[0]);
            rowArgList.add(new RowArg("delete FROM examinationnum WHERE examNum={examNum} and gradeNum={gradeNum}  and schoolNum={schoolNum}", args2));
            rowArgList.add(new RowArg("delete FROM examinationroom WHERE examNum={examNum} and gradeNum={gradeNum}  and schoolNum={schoolNum} ", args2));
        }
        if (rowArgList.size() > 0) {
            this.dao._batchExecute(rowArgList);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<String> getClassByExamNumSchoolNum(String examNum, String subjectNum, String schoolNum, String gradeNum, String stuType) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("stuType", (Object) stuType);
        return this.dao._queryColList("SELECT DISTINCT(ce.classNum) from classexam ce  LEFT JOIN class c ON c.id=ce.classNum  LEFT JOIN  examPaper ep on ce.examPaperNum=ep.examPaperNum WHERE ep.examNum={examNum}  and ep.subjectNum={subjectNum}  and ce.schoolNum={schoolNum}  and ep.gradeNum={gradeNum} AND c.studentType={stuType} ", String.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Exam getExamInfo(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return (Exam) this.dao._queryBean("select examNum,examName,type,paintMode,scanType,examDate,scoreModel,rule,examinationRoomLength,examineeLength,examtype from exam where isDelete='F' and examNum={examNum} ", Exam.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void addOrEditExam(Exam exam, String[] subjectNums, String[] schoolNums, String[] gradeNums, String[] classNums, Map jieMap, String hasmark, String examineetype, String scantype, int mark, String[] schoolData) {
        String user = exam.getInsertUser();
        String date = exam.getInsertDate();
        int examNum = exam.getExamNum().intValue();
        String paperStr = "";
        List<Examlog> logList = new ArrayList<>();
        List<Exampaper> exampapers = new ArrayList<>();
        ListOrderedMap existPaperMap = ListOrderedMap.decorate(getPaperByExamNum(examNum));
        Set<String> subjectSet = new HashSet<>();
        if (existPaperMap != null) {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum));
            List type = this.dao._queryArrayList("select type from exam where examNum= {examNum}", args);
            if (!type.isEmpty()) {
                Object[] obj = type.get(0);
                exam.setType(obj[0] + "");
            }
        }
        int paperNum = 0;
        Map args2 = StreamMap.create().put("examType", (Object) exam.getExamType());
        Object beforeExamNum = this.dao._queryObject("SELECT e.examNum from examtypegroup etg LEFT JOIN exam e on e.examType = etg.examtypeNum where e.examType = {examType}  and e.isDelete='F' ORDER BY e.examDate desc,e.insertDate desc LIMIT 1", args2);
        List<String> retainEP = new ArrayList<>();
        for (int i = 0; i < subjectNums.length; i++) {
            String key = gradeNums[i] + "_" + subjectNums[i];
            if (subjectSet.add(key)) {
                paperStr = paperStr + key + Const.STRING_SEPERATOR;
                Exampaper paper = new Exampaper();
                if (existPaperMap.get(key) != null) {
                    retainEP.add(key);
                    paperNum = Integer.valueOf(existPaperMap.get(key).toString()).intValue();
                    Map args3 = new HashMap();
                    args3.put("paperNum", Integer.valueOf(paperNum));
                    Object paperObject = this.dao._queryBean("select  * from exampaper where exampapernum={paperNum} ", Exampaper.class, args3);
                    paper = (Exampaper) paperObject;
                } else {
                    ServletContext context = ServletActionContext.getServletContext();
                    String typeLic = Configuration.getInstance().getType();
                    String typeyw = Configuration.getInstance().getExamInitType();
                    ((Integer) context.getAttribute(Const.school_limit)).intValue();
                    if (!"2".equals(typeLic)) {
                        typeyw = typeLic;
                    }
                    Boolean existBefore = false;
                    if (null != beforeExamNum) {
                        Map args4 = new HashMap();
                        args4.put("beforeExamNum", beforeExamNum);
                        args4.put(Const.EXPORTREPORT_gradeNum, gradeNums[i]);
                        args4.put(Const.EXPORTREPORT_subjectNum, subjectNums[i]);
                        Exampaper beforeEp = (Exampaper) this.dao._queryBean("SELECT ep.type,ep.templateType,ep.abtype,ep.paintMode from exampaper ep where ep.examNum ={beforeExamNum}  and ep.gradeNum = {gradeNum}  and ep.subjectNum = {subjectNum} ", Exampaper.class, args4);
                        if (null != beforeEp) {
                            paper.setType(beforeEp.getType());
                            paper.setTemplateType(beforeEp.getTemplateType());
                            paper.setAbtype(beforeEp.getAbtype());
                            paper.setPaintMode(beforeEp.getPaintMode());
                            existBefore = true;
                        }
                    }
                    if (!existBefore.booleanValue()) {
                        paper.setType(typeyw);
                        paper.setTemplateType("1");
                        paper.setAbtype("0");
                        paper.setPaintMode("112");
                    }
                    paperNum = this.examroomIdsys.getAutoID("exampaper", "exampaperNum");
                    paper.setExamNum(Integer.valueOf(examNum));
                    paper.setExamPaperNum(Integer.valueOf(paperNum));
                    paper.setGradeNum(Integer.valueOf(gradeNums[i]));
                    paper.setIsDelete("F");
                    paper.setSubjectNum(Integer.valueOf(subjectNums[i]));
                    paper.setPexamPaperNum(Integer.valueOf(paperNum));
                    paper.setIsHidden("F");
                    paper.setInsertUser(user);
                    paper.setInsertDate(date);
                    paper.setUpdateUser(user);
                    paper.setUpdateDate(date);
                    paper.setTotalScore(Double.valueOf(0.0d));
                    paper.setStatus("0");
                    paper.setTotalPage("0");
                    paper.setDoubleFaced("T");
                    paper.setPaperSize("91");
                    paper.setShowTag("F");
                    paper.setExamineeInstructions("0");
                    paper.setAnalysisscore(Float.valueOf(0.0f));
                    paper.setMultipagestuinfo("0");
                    paper.setFenzuyuejuan("1");
                }
                paper.setScanType(scantype);
                if (StrUtil.isNotEmpty(Convert.toStr(jieMap.get(gradeNums[i])))) {
                    paper.setJie(Convert.toStr(jieMap.get(gradeNums[i]), (String) null));
                }
                exampapers.add(paper);
                Examlog log = new Examlog();
                log.setExamNum(Integer.valueOf(examNum));
                log.setExampaperNum(Integer.valueOf(paperNum));
                log.setGradeNum(Convert.toInt(gradeNums[i], 0));
                log.setSubjectNum(Convert.toInt(subjectNums[i], 0));
                log.setInsertUser(user);
                log.setInsertDate(date);
                log.setIsDelete("F");
                log.setOperate(Const.log_add_exampaper);
                logList.add(log);
            }
            for (int e = 0; e < exampapers.size(); e++) {
                if ((gradeNums[i] + "_" + subjectNums[i]).equals(exampapers.get(e).getGradeNum() + "_" + exampapers.get(e).getSubjectNum())) {
                    paperNum = exampapers.get(e).getExamPaperNum().intValue();
                }
            }
            String str = paperNum + "_";
        }
        List<RowArg> delsql = new ArrayList<>();
        for (Object p : existPaperMap.keySet()) {
            if (paperStr.indexOf(String.valueOf(p)) == -1) {
                String pNum = String.valueOf(existPaperMap.get(p));
                Map args5 = StreamMap.create().put("pNum", (Object) pNum);
                int cc = this.dao._queryInt("SELECT  COUNT(1) FROM exampaper WHERE pexamPaperNum={pNum}", args5).intValue();
                if (cc > 0) {
                    Map args6 = StreamMap.create().put("pNum", (Object) pNum);
                    delsql.add(new RowArg("delete from examPaper where pexamPaperNum={pNum}", args6));
                }
                Map argspa = StreamMap.create().put("existpaperNum", (Object) pNum);
                delsql.add(new RowArg("DELETE FROM exampaperparameter WHERE examPaperNum={existpaperNum} ", argspa));
            }
        }
        for (Object existpaperNumKey : existPaperMap.keySet()) {
            Object existpaperNum = existPaperMap.get(existpaperNumKey);
            Map args7 = StreamMap.create().put("existpaperNum", existpaperNum);
            delsql.add(new RowArg("delete from examPaper where examPaperNum={existpaperNum} ", args7));
            if (!retainEP.contains(existpaperNumKey.toString())) {
                String[] graAndSub = existpaperNumKey.toString().split("_");
                Map args8 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum)).put(Const.EXPORTREPORT_gradeNum, (Object) graAndSub[0]).put(Const.EXPORTREPORT_subjectNum, (Object) graAndSub[1]);
                delsql.add(new RowArg("delete from examinationroom where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}", args8));
            }
            Examlog log2 = new Examlog();
            log2.setExamNum(Integer.valueOf(examNum));
            log2.setExampaperNum(Integer.valueOf(existpaperNum.toString()));
            log2.setInsertUser(user);
            log2.setInsertDate(date);
            log2.setIsDelete("F");
            log2.setOperate(Const.log_delete_exampaper);
            logList.add(log2);
        }
        if (mark == 1) {
            this.dao.save(exam);
            Examlog log3 = new Examlog();
            log3.setExamNum(Integer.valueOf(examNum));
            log3.setInsertUser(user);
            log3.setInsertDate(date);
            log3.setIsDelete("F");
            log3.setOperate(Const.EXAM_ADD);
            logList.add(log3);
        } else {
            this.dao.update(exam);
            Examlog log4 = new Examlog();
            log4.setExamNum(Integer.valueOf(examNum));
            log4.setInsertUser(user);
            log4.setInsertDate(date);
            log4.setIsDelete("F");
            log4.setOperate(Const.EXAM_UPDATE);
            logList.add(log4);
        }
        if (delsql != null && delsql.size() > 0) {
            this.dao._batchExecute(delsql);
        }
        if (exampapers != null && exampapers.size() > 0) {
            this.dao.batchSave(exampapers);
        }
        addOneExamsetting(examNum, beforeExamNum, user, date);
        addOneSourcesetting(examNum, beforeExamNum, user, date);
        this.his.cteateimgepath(String.valueOf(examNum));
        this.dao.batchSave(logList);
        Map args9 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum));
        List<?> _queryBeanList = this.dao._queryBeanList("SELECT  DISTINCT ex.examPaperNum,ex.examNum,ex.gradeNum,ex.subjectNum FROM exampaper ex LEFT JOIN exampaper ex2 ON ex.examPaperNum=ex2.pexamPaperNum WHERE ex.examNum={examNum}  AND ex2.examPaperNum IS NOT NULL", Exampaper.class, args9);
        for (int i2 = 0; i2 < _queryBeanList.size(); i2++) {
            Exampaper e2 = (Exampaper) _queryBeanList.get(i2);
            Map args10 = StreamMap.create().put("examPaperNum", (Object) e2.getExamPaperNum());
            String id = this.dao._queryStr("select  id from exampaperparameter where examPaperNum={examPaperNum} ", args10);
            if (id == null || "".equals(id) || "null".equals(id)) {
                Map args11 = StreamMap.create().put("examPaperNum", (Object) e2.getExamPaperNum()).put(Const.EXPORTREPORT_examNum, (Object) e2.getExamNum()).put(Const.EXPORTREPORT_gradeNum, (Object) e2.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) e2.getSubjectNum()).put("user", (Object) user);
                this.dao._execute("INSERT INTO exampaperparameter(examPaperNum,examNum,gradeNum,subjectNum,type,value,insertUser,insertDate,updateUser,updateDate) values({examPaperNum},{examNum},{gradeNum},{subjectNum},1,1,{user},now(),{user},now()) ", args11);
            }
        }
        StringBuffer zkmSql = new StringBuffer();
        zkmSql.append("select distinct pexamPaperNum from exampaper ");
        zkmSql.append("where examNum={examNum} and isHidden='T'");
        Map args12 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum));
        List pepList = this.dao._queryColList(zkmSql.toString(), args12);
        for (Object pep : pepList) {
            StringBuffer selSql = new StringBuffer();
            selSql.append("select examPaperNum from exampaper ");
            selSql.append("where examPaperNum={pep} and isHidden='F' limit 1");
            Map args13 = StreamMap.create().put("pep", pep);
            if (null == this.dao._queryObject(selSql.toString(), args13)) {
                StringBuffer delSql = new StringBuffer();
                delSql.append("delete from exampaper ");
                delSql.append("where pexamPaperNum={pep} and isHidden='T' ");
                Map args14 = StreamMap.create().put("pep", pep);
                this.dao._execute(delSql.toString(), args14);
            }
        }
        if (mark == 1) {
            autoTestingcentre(String.valueOf(exam.getExamNum()), exam.getInsertUser());
        } else {
            editExamSchool(String.valueOf(exam.getExamNum()), exam.getInsertUser(), schoolData);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void addOrEditExamSchool(Exam exam, String[] schoolData) {
        String examNum = Convert.toStr(exam.getExamNum());
        String user = Convert.toStr(exam.getInsertUser());
        String date = Convert.toStr(exam.getInsertDate());
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        this.dao._execute("delete from examSchool where examNum={examNum}", args);
        List<Map<String, Object>> args_sqls = new ArrayList<>();
        for (String str : schoolData) {
            Map args_add = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) str).put("insertUser", (Object) user).put("insertDate", (Object) date).put("updateUser", (Object) user).put("updateDate", (Object) date);
            args_sqls.add(args_add);
        }
        this.dao._batchExecute("insert into examschool(examNum,schoolNum,insertUser,insertDate,updateUser,updateDate) VALUES({examNum},{schoolNum},{insertUser},{insertDate},{updateUser},{updateDate})", args_sqls);
    }

    public void editExamSchool(String examNum, String insertUser, String[] schoolData) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("insertUser", (Object) insertUser);
        List<Map<String, Object>> schoolNumList = this.dao._queryMapList("select schoolNum from testingcentre_school where examNum={examNum}", TypeEnum.StringObject, args);
        if (schoolNumList.size() < schoolData.length) {
            List<Map<String, String>> sqlmap = new ArrayList<>();
            String insertTestingCentreSql = "insert into testingcentre (id,examNum,testingCentreNum,testingCentreName,testingCentreLocation,insertUser,insertDate,isDelete) select UUID_SHORT()," + examNum + ",sch.schoolNum,sch.schoolName,sch.schoolAddress," + insertUser + ",now(),sch.isDelete from school sch  where sch.isDelete = 'F' and sch.id={schoolNum} ";
            String insertTesSchSql = "insert into testingcentre_school (id,examNum,testingCentreId,schoolNum,insertUser,insertDate,updateUser,updateDate,isDelete) select UUID_SHORT(),tc.examNum,tc.id,sch.id," + insertUser + ",now()," + insertUser + ",now(),tc.isDelete from (select id,examNum,testingCentreNum,isDelete from testingcentre where examNum = {examNum} ) tc left join school sch on sch.schoolNum = tc.testingCentreNum ";
            if (schoolNumList.size() == 0) {
                for (String str : schoolData) {
                    Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("insertUser", (Object) insertUser).put(Const.EXPORTREPORT_schoolNum, (Object) str);
                    sqlmap.add(args2);
                }
            } else {
                List<Map<String, String>> schoolMapList = new ArrayList<>();
                for (String str2 : schoolData) {
                    Map<String, String> maps = new HashMap<>();
                    maps.put(Const.EXPORTREPORT_schoolNum, str2);
                    schoolMapList.add(maps);
                }
                for (int i = 0; i < schoolNumList.size(); i++) {
                    String schoolNum = Convert.toStr(schoolNumList.get(i).get(Const.EXPORTREPORT_schoolNum));
                    schoolMapList.removeIf(o -> {
                        return schoolNum.equals(o.get(Const.EXPORTREPORT_schoolNum));
                    });
                }
                for (int l = 0; l < schoolMapList.size(); l++) {
                    Map args22 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("insertUser", (Object) insertUser).put(Const.EXPORTREPORT_schoolNum, (Object) Convert.toStr(schoolMapList.get(l).get(Const.EXPORTREPORT_schoolNum)));
                    sqlmap.add(args22);
                }
            }
            this.dao._batchExecute(insertTestingCentreSql, sqlmap);
            this.dao._execute("delete from testingcentre_school where examNum={examNum}", args);
            this.dao._execute(insertTesSchSql, args);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object[] getExamList(Map<String, String> map) {
        String isDelete = map.get("isDelete");
        String sql = "SELECT  ex.type,ex.examNum,ex.examName,examDate,d.name as examTypeName,ex.status,if(sch.count>3 ,1,0)isMoreSchool,'1' ext1 from exam ex LEFT JOIN (SELECT value,name from data WHERE type={type}  and isDelete='F') d ON ex.examType=d.value left join (select count(1)count from school) sch on 1=1  where  ";
        String rowSql = "select count(ex.examNum) from exam ex where ";
        String examName = map.get("examName");
        String examType = map.get("examType");
        String beginExamDate = map.get("beginExamDate");
        String endExamDate = map.get("endExamDate");
        map.get("mark1");
        String completeexam = map.get("completeexam");
        Map args = new HashMap();
        args.put("type", "1");
        args.put(Const.CORRECT_SCORECORRECT, "9");
        args.put("isDelete", isDelete);
        args.put("user", map.get("user"));
        if (completeexam.equals("0")) {
            sql = sql + "  ex.status!={status}  AND";
            rowSql = rowSql + "  ex.status!={status} and ";
        }
        if (completeexam.equals("1")) {
            sql = sql + "  ex.status={status}  AND";
            rowSql = rowSql + "  ex.status={status}  AND";
        }
        String sql2 = sql + "   ex.isDelete={isDelete} ";
        String rowSql2 = rowSql + "   ex.isDelete={isDelete} ";
        if (examName != null && !"".equals(examName.trim())) {
            sql2 = sql2 + " and ex.examName like {examName} ";
            rowSql2 = rowSql2 + " and ex.examName like {examName} ";
            args.put("examName", "%" + examName + "%");
        }
        if (examType != null && !"".equals(examType.trim()) && !"-1".equals(examType.trim())) {
            sql2 = sql2 + " and ex.examType = {examType} ";
            rowSql2 = rowSql2 + " and ex.examType = {examType} ";
            args.put("examType", examType);
        }
        if (beginExamDate != null && !"".equals(beginExamDate.trim())) {
            sql2 = sql2 + " and ex.examDate >= {beginExamDate} ";
            rowSql2 = rowSql2 + " and ex.examDate >= {beginExamDate} ";
            args.put("beginExamDate", beginExamDate);
        }
        if (endExamDate != null && !"".equals(endExamDate.trim())) {
            sql2 = sql2 + " and ex.examDate <= {endExamDate} ";
            String str = rowSql2 + " and ex.examDate <= {endExamDate} ";
            args.put("endExamDate", endExamDate);
        }
        List<?> _queryBeanList = this.dao._queryBeanList((sql2 + " order by ex.examDate desc ").toString(), Exam.class, args);
        Map<String, Object> map1 = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={user} and type=1 limit 1", args);
        if (!map.get("user").equals("-1") && !map.get("user").equals("-2") && null == map1) {
            List<AjaxData> ajaxData = this.examDAO.getUserNoPerExam(map.get("user"));
            if (ajaxData.size() != 0) {
                for (AjaxData data : ajaxData) {
                    String num = data.getNum();
                    _queryBeanList.removeIf(o -> {
                        return num.equals(Convert.toStr(o.getExamNum()));
                    });
                }
            }
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < _queryBeanList.size(); i++) {
            ((Exam) _queryBeanList.get(i)).getExt1().split(Const.STRING_SEPERATOR);
            if (!((Exam) _queryBeanList.get(i)).getExt1().contains("--")) {
                arrayList.add(_queryBeanList.get(i));
            }
        }
        String count = "0";
        if ("0".equals(map.get("count"))) {
            count = Convert.toStr(Integer.valueOf(_queryBeanList.size()));
        }
        int intValue = Convert.toInt(count).intValue() % Convert.toInt(map.get("pageSize")).intValue() == 0 ? Convert.toInt(count).intValue() / Convert.toInt(map.get("pageSize")).intValue() : (Convert.toInt(count).intValue() / Convert.toInt(map.get("pageSize")).intValue()) + 1;
        List<Exam> listData = (List) arrayList.stream().skip(Convert.toInt(map.get("index")).intValue()).limit(Convert.toInt(map.get("pageSize")).intValue()).collect(Collectors.toList());
        for (int i2 = 0; i2 < listData.size(); i2++) {
            String examNum = listData.get(i2).getExamNum().toString();
            Map args1 = new HashMap();
            args1.put(Const.EXPORTREPORT_examNum, examNum);
            String jieIschange = this.dao._queryStr("SELECT IF(ep.jie=g.jie ,1,0) from (SELECT gradeNum,jie from exampaper WHERE examNum={examNum} LIMIT 1)ep LEFT JOIN grade g ON ep.gradeNum=g.gradeNum AND g.isdelete='F' limit 1 ", args1);
            listData.get(i2).setJieIschange(jieIschange);
        }
        return new Object[]{count, listData};
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void delOrRevokeExam(int examNum, String isDelete, String user) {
        String date = DateUtil.getCurrentTime();
        Map args = StreamMap.create().put("isDelete", (Object) isDelete).put("user", (Object) user).put("date", (Object) date).put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum));
        this.dao._execute("update exam set isDelete={isDelete} ,updateUser={user} ,updateDate={date}  where examNum={examNum} ", args);
        Examlog log = new Examlog();
        log.setExamNum(Integer.valueOf(examNum));
        log.setInsertUser(user);
        log.setInsertDate(date);
        log.setIsDelete("F");
        log.setOperate((isDelete.equals("F") ? "撤销删除" : "删除") + "考试");
        this.dao.save(log);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void batchDelOrRevokeExam(String[] examNums, String isDelete, String user) {
        String date = DateUtil.getCurrentTime();
        StringBuffer sbf = new StringBuffer();
        List<Examlog> logList = new ArrayList<>();
        for (String examNum : examNums) {
            sbf.append(examNum + Const.STRING_SEPERATOR);
            Examlog log = new Examlog();
            log.setExamNum(Integer.valueOf(examNum));
            log.setInsertUser(user);
            log.setInsertDate(date);
            log.setIsDelete("F");
            log.setOperate(isDelete.equals("F") ? "撤销删除" : "删除考试");
            logList.add(log);
        }
        Map args = StreamMap.create().put("isDelete", (Object) isDelete).put("user", (Object) user).put("date", (Object) date).put(Const.EXPORTREPORT_examNum, (Object) sbf.deleteCharAt(sbf.length() - 1).toString());
        this.dao._execute("update exam set isDelete={isDelete} ,updateUser={user} ,updateDate={date}  where  examNum in ({examNum[]}) ", args);
        this.dao.batchSave(logList);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getPaperQuestion(String examNum, String gradeNum, String subjectNum, String examPaperNum) {
        return this.examManageDAO.getPaperQuestion(examNum, gradeNum, subjectNum, examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getStudentsTotalScoreList(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum) {
        return this.examManageDAO.getStudentsTotalScoreList(examNum, gradeNum, subjectNum, classNum, schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getStudentsTotalScoreList_levelcla(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum) {
        return this.examManageDAO.getStudentsTotalScoreList_levelcla(examNum, gradeNum, subjectNum, classNum, schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getStudentsTotalScoreListByExaminationRoom(String examNum, String gradeNum, String subjectNum, String examRoomNum, String schoolNum, String examroomornot) {
        return this.examManageDAO.getStudentsTotalScoreListByExaminationRoom(examNum, gradeNum, subjectNum, examRoomNum, schoolNum, examroomornot);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getQuestionInfo(String examNum, String gradeNum, String subjectName, String classNum, String schoolNum, String examPaperNum) {
        return this.examManageDAO.getQuestionInfo(examNum, gradeNum, subjectName, classNum, schoolNum, examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getQuestionInfo_levelcla(String examNum, String gradeNum, String subjectName, String classNum, String schoolNum, String examPaperNum) {
        return this.examManageDAO.getQuestionInfo_levelcla(examNum, gradeNum, subjectName, classNum, schoolNum, examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getQuestionInfoByExaminationRoom(String examNum, String gradeNum, String subjectNum, String examRoomNum, String schoolNum, String examroomornot, String examPaperNum) {
        return this.examManageDAO.getQuestionInfoByExaminationRoom(examNum, gradeNum, subjectNum, examRoomNum, schoolNum, examroomornot, examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void clearStudentLevel() {
        this.examManageDAO.clearStudentLevel();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer getCreateTableData(String examNum, String subjectNum, String schoolNum, String gradeNum) {
        return this.examManageDAO.getCreateTableData(examNum, subjectNum, schoolNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getCTDExamPapeerCount(String schoolNum) {
        return this.examManageDAO.getCTDExamPapeerCount(schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getCTDStudentCount(String schoolNum) {
        return this.examManageDAO.getCTDStudentCount(schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getSingleSubjectData_levcla(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, String type, String source, String isJointStuType, String subCompose) {
        return this.examManageDAO.getSingleSubjectData_levcla(examNum, gradeNum, subjectNum, classNum, schoolNum, type, source, isJointStuType, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getSingleSubjectData_levcla_fufen(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, String type, String source, String isJointStuType, String subCompose) {
        return this.examManageDAO.getSingleSubjectData_levcla_fufen(examNum, gradeNum, subjectNum, classNum, schoolNum, type, source, isJointStuType, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getSingleSubjectData(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, String type, String source, String sexamPaperNum, String sSjt, boolean flg, String isJointStuType, String subCompose) {
        return this.examManageDAO.getSingleSubjectData(examNum, gradeNum, subjectNum, classNum, schoolNum, type, source, sexamPaperNum, sSjt, flg, isJointStuType, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getSingleSubjectData_fufen(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, String type, String source, String sexamPaperNum, String sSjt, boolean flg, String isJointStuType, String subCompose) {
        return this.examManageDAO.getSingleSubjectData_fufen(examNum, gradeNum, subjectNum, classNum, schoolNum, type, source, sexamPaperNum, sSjt, flg, isJointStuType, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getSingleSubjectDataExaminationRoom(String examNum, String gradeNum, String subjectNum, String examRoomNum, String schoolNum, String examroomornot, String type, String source) {
        return this.examManageDAO.getSingleSubjectDataExaminationRoom(examNum, gradeNum, subjectNum, examRoomNum, schoolNum, examroomornot, type, source);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getSingleSubjectDataPage(String examNum, String gradeNum, String subjectNum, String classNum, String schoolNum, int pageStart, int pageSize) {
        return this.examManageDAO.getSingleSubjectDataPage(examNum, gradeNum, subjectNum, classNum, schoolNum, pageStart, pageSize);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getAllSubjectName(String schoolNum, String gradeNum, String examNum) {
        return this.examManageDAO.getAllSubjectName(schoolNum, gradeNum, examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getExcelFileNameByNum(String schoolNum, String examNum, String gradeNum, String classNum, String subjectNum, String radioValue, String studentType, String levelclass, String sjt) {
        return this.examManageDAO.getExcelFileNameByNum(schoolNum, examNum, gradeNum, classNum, subjectNum, radioValue, studentType, levelclass, sjt);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getExcelFileNameByNum2(String schoolNum, String examNum, String gradeNum, String classNum, String subjectNum, String radioValue, String studentType, String levelclass, String sjt, String subCompose) {
        return this.examManageDAO.getExcelFileNameByNum2(schoolNum, examNum, gradeNum, classNum, subjectNum, radioValue, studentType, levelclass, sjt, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getExcelFileNameByNum3(String schoolNum, String examNum, String gradeNum, String classNum, String subjectNum, String radioValue, String studentType, String levelclass, String sjt, String subCompose) {
        return this.examManageDAO.getExcelFileNameByNum3(schoolNum, examNum, gradeNum, classNum, subjectNum, radioValue, studentType, levelclass, sjt, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getTotalAndStudentId(String examNum, String gradeNum, String classNum, String schoolNum) {
        return this.examManageDAO.getTotalAndStudentId(examNum, gradeNum, classNum, schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getClassStuAndSub(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String isJointStuType, String subCompose) throws Exception {
        return this.examManageDAO.getClassStuAndSub(examNum, gradeNum, classNum, schoolNum, studentType, graduationType, stuSourceType, viewRankInfo, isJointStuType, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Object[]> getClassStuAndSub_new(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String isJointStuType, String fufen, String subCompose) throws Exception {
        return this.examManageDAO.getClassStuAndSub_new(examNum, gradeNum, classNum, schoolNum, studentType, graduationType, stuSourceType, viewRankInfo, isJointStuType, fufen, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<List<?>> getClassStuAndSub_fufen(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String isJointStuType, String subCompose) throws Exception {
        return this.examManageDAO.getClassStuAndSub_fufen(examNum, gradeNum, classNum, schoolNum, studentType, graduationType, stuSourceType, viewRankInfo, isJointStuType, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<List<?>> getClassStuAndSub_bzf(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String fufen, String isJointStuType, String subCompose) throws Exception {
        return this.examManageDAO.getClassStuAndSub_bzf(examNum, gradeNum, classNum, schoolNum, studentType, graduationType, stuSourceType, viewRankInfo, fufen, isJointStuType, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Object[]> getClassStuAndSub_bzf_new(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String fufen, String isJointStuType, String subCompose) throws Exception {
        return this.examManageDAO.getClassStuAndSub_bzf_new(examNum, gradeNum, classNum, schoolNum, studentType, graduationType, stuSourceType, viewRankInfo, fufen, isJointStuType, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getClassStuAndSub_NoClaGraRank(String examNum, String gradeNum, String classNum, String schoolNum, String studentType) throws Exception {
        return this.examManageDAO.getClassStuAndSub_NoClaGraRank(examNum, gradeNum, classNum, schoolNum, studentType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getAvgScoreAndStuCount(String examNum, String subjectNum, String schoolNum, String gradeNum, String classNum, String classType, String graduationType, String stuSourceType, String subCompose) {
        return this.examManageDAO.getAvgScoreAndStuCount(examNum, subjectNum, schoolNum, gradeNum, classNum, classType, graduationType, stuSourceType, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Classlevel> getAvgScoreAndStuCount_fufen(String examNum, String subjectNum, String schoolNum, String gradeNum, String classNum, String classType, String graduationType, String stuSourceType, String subCompose) {
        return this.examManageDAO.getAvgScoreAndStuCount_fufen(examNum, subjectNum, schoolNum, gradeNum, classNum, classType, graduationType, stuSourceType, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getClassType(String examNum, String gradeNum, String classNum, String schoolNum) throws Exception {
        return this.examManageDAO.getClassType(examNum, gradeNum, classNum, schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getClassType_fufen(String examNum, String gradeNum, String classNum, String schoolNum) throws Exception {
        return this.examManageDAO.getClassType_fufen(examNum, gradeNum, classNum, schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getClassStuAndSubByExaminationRoom(String examNum, String gradeNum, String examRoomNum, String schoolNum, String graduationType, String stuSourceType) throws Exception {
        return this.examManageDAO.getClassStuAndSubByExaminationRoom(examNum, gradeNum, examRoomNum, schoolNum, graduationType, stuSourceType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List isExistInExamineeNumError(ExamineeNumError examineeNumError) {
        return this.examManageDAO.isExistInExamineeNumError(examineeNumError);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Object[]> getClassStuAndSubPage(String examNum, String gradeNum, String classNum, String schoolNum, int pageStart, int pageSize) {
        return this.examManageDAO.getClassStuAndSubPage(examNum, gradeNum, classNum, schoolNum, pageStart, pageSize);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer getScoreImageCount(Scoreimage scoreImage) {
        return this.examManageDAO.getScoreImageCount(scoreImage);
    }

    public List getLeakScoreImage(Scoreimage scoreImage, int pageStart, int pageSize) {
        return this.examManageDAO.getLeakScoreImage(scoreImage, pageStart, pageSize);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getLeakScoreImageById(Scoreimage scoreImage, int sid) {
        return this.examManageDAO.getLeakScoreImageById(scoreImage, sid);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getLeakScoreImages(Scoreimage scoreImage) {
        return this.examManageDAO.getLeakScoreImages(scoreImage);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getStudentPaperById(String id) {
        return this.examManageDAO.getStudentPaperById(id);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getOneFromExamineeNumError(String id, String schoolNum, String gradeNum, String examNum) {
        return this.examManageDAO.getOneFromExamineeNumError(id, schoolNum, gradeNum, examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public ExamineeNumError getExamineeNumErrorObjById(String id) {
        return this.examManageDAO.getExamineeNumErrorObjById(id);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer deleteByExamineeNumErrorObj(ExamineeNumError examineeNumError) {
        return this.examManageDAO.deleteByExamineeNumErrorObj(examineeNumError);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void modifyError(String id, String studentId, Student stu, String errorPaperNum, int page) {
        this.examManageDAO.modifyError(id, studentId, stu, errorPaperNum, page);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void modifyError2(String id, String sStudentId, Student stu, String eStudentId, ExamineeNumError examineeNumError, String errorPaperNum, int page) {
        if (this.examManageDAO.addOne(examineeNumError).intValue() == 1) {
            this.examManageDAO.modifyError2(eStudentId, examineeNumError, errorPaperNum, page);
            this.examManageDAO.modifyError(id, sStudentId, stu, errorPaperNum, page);
        } else {
            this.log.info("未成功提取到考号有误表中，也未更新相应表");
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer modifyExamroomAndExamineeNum(String examPaperNum, String testCenter, String newExamroomNum, String oldStudentId, String newStudentId, int page, String cNum, String oldExamroomId, String examroomornot, String examNum, String newScannum) {
        RegExaminee reg;
        List<RegExaminee> regOldList = this.examManageDAO.authDataExistsFromRegExamineeList("", examPaperNum, oldStudentId, testCenter, page, cNum, oldExamroomId, null, null, null, examroomornot);
        List<RegExaminee> regNewList = this.examManageDAO.authDataExistsFromRegExamineeList("", examPaperNum, newStudentId, testCenter, page, null, newExamroomNum, regOldList, "T", null, examroomornot);
        new RegExaminee();
        if (regNewList != null) {
            reg = regNewList.get(0);
        } else {
            reg = null;
        }
        if (null == reg) {
            int regOldInt = 0;
            for (int i = 0; i < regOldList.size(); i++) {
                if (i == 0) {
                    Examlog examlog = new Examlog();
                    examlog.setOperate("修改考号-无冲突-原学籍号-" + regOldList.get(i).getStudentId());
                    examlog.setExampaperNum(Integer.valueOf(Integer.parseInt(examPaperNum)));
                    examlog.setExamNum(null);
                    examlog.setStudentId(newStudentId);
                    examlog.setExaminationRoomNum(newExamroomNum);
                    examlog.setInsertUser("0");
                    examlog.setInsertDate(DateUtil.getCurrentTime());
                    examlog.setSubjectNum(null);
                    examlog.setGradeNum(null);
                    this.dao.save(examlog);
                }
                regOldInt += this.examManageDAO.modifyExamroomAndExamineeNum(examPaperNum, testCenter, newExamroomNum, regOldList.get(i).getStudentId() + "", newStudentId, regOldList.get(i).getPage(), regOldList, cNum, examroomornot, regOldList.get(i).getId() + "", newScannum, newScannum).intValue();
                if (i == regOldList.size() - 1) {
                    this.questionNumListService.updateAllChildQues(examPaperNum, testCenter, newStudentId, Integer.valueOf(regOldList.get(i).getPage()));
                }
                if (regOldList.get(i).getPage() != 1) {
                    this.questionNumListService.modifyObjectieveSwithAB(examPaperNum, newStudentId, Integer.valueOf(regOldList.get(i).getPage()), regOldList.get(i).getId());
                }
            }
            if (!newExamroomNum.equals(oldExamroomId)) {
                this.examManageDAO.addToCorrectstatus(examNum, "", "", examPaperNum, testCenter, newExamroomNum);
            }
            if (0 == regOldInt) {
                return Integer.valueOf(regOldInt);
            }
            return null;
        }
        int regNewInt = 0;
        String tempStudentId = GUID.getGUID() + "";
        for (int i2 = 0; i2 < regNewList.size(); i2++) {
            regNewInt += this.examManageDAO.modifyExamroomAndExamineeNum(examPaperNum, testCenter, reg.getExaminationRoomNum() + "", regNewList.get(i2).getStudentId() + "", tempStudentId, regNewList.get(i2).getPage(), regNewList, reg.getcNum() + "", examroomornot, regNewList.get(i2).getId() + "", null, newScannum).intValue();
            if (i2 == regNewList.size() - 1) {
                this.questionNumListService.updateAllChildQues(examPaperNum, testCenter, tempStudentId, Integer.valueOf(regNewList.get(i2).getPage()));
            }
            if (regNewList.get(i2).getPage() != 1) {
                this.questionNumListService.modifyObjectieveSwithAB(examPaperNum, tempStudentId, Integer.valueOf(regNewList.get(i2).getPage()), regNewList.get(i2).getId());
            }
        }
        if (0 != regNewInt) {
            for (RegExaminee regObj : regNewList) {
                ExamineeNumError error = new ExamineeNumError();
                error.setRegId(regObj.getId());
                error.setPage(regObj.getPage());
                error.setExamPaperNum(Integer.valueOf(Integer.parseInt(examPaperNum)));
                error.setErrorType("1");
                error.setExaminationRoomNum(regObj.getExaminationRoomNum());
                error.setStudentId(tempStudentId);
                error.setSchoolNum(0);
                error.setTestingCentreId(regObj.getTestingCentreId());
                error.setExamineeNum(newScannum);
                error.setInsertUser("0");
                error.setInsertDate(DateUtil.getCurrentTime());
                error.setGroupNum(regObj.getcNum());
                this.dao.save(error);
            }
            Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
            List<?> _queryBeanList = this.dao._queryBeanList("SELECT  DISTINCT examNum,gradeNum,subjectNum   FROM exampaper WHERE examPaperNum={examPaperNum}", Exampaper.class, args);
            Examlog examlog2 = new Examlog();
            examlog2.setOperate("修改考号-来自识别表-插入考号有误表");
            examlog2.setExampaperNum(Integer.valueOf(Integer.parseInt(examPaperNum)));
            examlog2.setExamNum(((Exampaper) _queryBeanList.get(0)).getExamNum());
            examlog2.setStudentId(tempStudentId);
            examlog2.setExaminationRoomNum(reg.getExaminationRoomNum());
            examlog2.setInsertUser("0");
            examlog2.setInsertDate(DateUtil.getCurrentTime());
            examlog2.setSubjectNum(((Exampaper) _queryBeanList.get(0)).getSubjectNum());
            examlog2.setGradeNum(((Exampaper) _queryBeanList.get(0)).getGradeNum());
            this.dao.save(examlog2);
        }
        int code2 = 0;
        for (int i3 = 0; i3 < regOldList.size(); i3++) {
            if (i3 == 0) {
                Examlog examlog3 = new Examlog();
                examlog3.setOperate("修改考号-有冲突-原学籍号-" + regOldList.get(i3).getStudentId());
                examlog3.setExampaperNum(Integer.valueOf(Integer.parseInt(examPaperNum)));
                examlog3.setExamNum(null);
                examlog3.setStudentId(newStudentId);
                examlog3.setExaminationRoomNum(newExamroomNum);
                examlog3.setInsertUser("0");
                examlog3.setInsertDate(DateUtil.getCurrentTime());
                examlog3.setSubjectNum(null);
                examlog3.setGradeNum(null);
                this.dao.save(examlog3);
            }
            code2 += this.examManageDAO.modifyExamroomAndExamineeNum(examPaperNum, testCenter, newExamroomNum, regOldList.get(i3).getStudentId() + "", newStudentId, regOldList.get(i3).getPage(), regOldList, cNum, examroomornot, regOldList.get(i3).getId() + "", newScannum, newScannum).intValue();
            if (i3 == regOldList.size() - 1) {
                this.questionNumListService.updateAllChildQues(examPaperNum, testCenter, newStudentId, Integer.valueOf(regOldList.get(i3).getPage()));
            }
            if (regOldList.get(i3).getPage() != 1) {
                this.questionNumListService.modifyObjectieveSwithAB(examPaperNum, newStudentId, Integer.valueOf(regOldList.get(i3).getPage()), regOldList.get(i3).getId());
            }
        }
        if (!newExamroomNum.equals(oldExamroomId)) {
            this.examManageDAO.addToCorrectstatus(examNum, "", "", examPaperNum, testCenter, newExamroomNum);
        }
        if (0 == code2) {
            return Integer.valueOf(code2);
        }
        return null;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getStudentInfoFromStudent(String studentId) {
        return this.examManageDAO.getStudentInfoFromStudent(studentId);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Examinationnum getExaminationnumObj(String id, String examinationRoomNum, String examNum, String examineeNum, String schoolNum, String gradeNum) {
        new ArrayList();
        List<Examinationnum> list = this.examManageDAO.getExaminationnumObj(id, examinationRoomNum, examNum, examineeNum, schoolNum, gradeNum);
        if (null == list || list.size() == 0) {
            this.log.info("list为空--");
            return null;
        }
        return list.get(0);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Score findOneFromScore(String examPaperNum, String studentId, String schoolNum, int page) {
        Score s = this.examManageDAO.findOneFromScore(examPaperNum, studentId, schoolNum, page);
        this.log.info("score 对象---" + s.getStudentId());
        return s;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RegExaminee authDataExistsFromRegExaminee(String paperType, String examPaperNum, String studentId, String schoolNum, int page, String cNum, String oldExamroomId, List<RegExaminee> regOldList, String flg, String regId) {
        RegExaminee regExaminee = this.examManageDAO.authDataExistsFromRegExaminee(paperType, examPaperNum, studentId, schoolNum, page, cNum, oldExamroomId, regOldList, flg, null, regId);
        return regExaminee;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer addOne(Object obj) {
        return this.examManageDAO.addOne(obj);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void addObjList(List<Object> oList) {
        this.examManageDAO.addObjList(oList);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getStudentIdErrCount(ExamineeNumError examineeNumError, String searchType) {
        return this.examManageDAO.getStudentIdErrCount(examineeNumError, searchType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getFirstStudent(ExamineeNumError examineeNumError, String id, String searchType) {
        List l = this.examManageDAO.getFirstStudent(examineeNumError, id, searchType);
        this.log.info("查询学号有误的学生列表的第一条的list  getFirstStudent()--" + l.size());
        return l;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getErrStusFromExamineeNumError(ExamineeNumError examineeNumError, int pageStart, int pageSize, String searchType) {
        return this.examManageDAO.getErrStusFromExamineeNumError(examineeNumError, pageStart, pageSize, searchType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Object[]> getBeforeExamList(String srcExamRoomNum) {
        Map args = StreamMap.create().put("srcExamRoomNum", (Object) srcExamRoomNum);
        return this.dao._queryArrayList("SELECT examNum,examName,examDate from exam where examNum <> {srcExamRoomNum} and examDate BETWEEN DATE_SUB(NOW(),INTERVAL 1 YEAR) and  NOW() ORDER BY examDate desc", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void copyExamRoomInfo(String srcExamNum, String tagExamNum) {
        Map args = StreamMap.create().put("tagExamNum", (Object) tagExamNum);
        this.dao._execute("delete from examinationnum where examNum={tagExamNum} ", args);
        this.dao._execute("delete from examinationroom where examNum={tagExamNum} ", args);
        this.dao._execute("delete from testingcentre where examNum={tagExamNum} ", args);
        this.dao._execute("delete from testingcentre_school where examNum={tagExamNum} ", args);
        Map args2 = StreamMap.create().put("srcExamNum", (Object) srcExamNum);
        List<?> _queryColList = this.dao._queryColList("select id from testingcentre where examNum={srcExamNum} ", String.class, args2);
        List<RowArg> rowArgList = new ArrayList<>();
        for (int i = 0; i < _queryColList.size(); i++) {
            String testingid = GUID.getGUIDStr();
            String cenid = String.valueOf(_queryColList.get(i));
            String insertTesting = "insert INTO testingcentre(id,examNum,testingCentreNum,testingCentreName,testingCentreLocation,insertUser,insertDate,isDelete) select " + testingid + Const.STRING_SEPERATOR + tagExamNum + " examNum ,testingCentreNum,testingCentreName,testingCentreLocation,insertUser,insertDate,isDelete from testingcentre where id = {cenid} ";
            Map insertTestingargs = StreamMap.create().put("cenid", (Object) cenid);
            rowArgList.add(new RowArg(insertTesting, insertTestingargs));
            String addTcAndSchSql = "insert into testingcentre_school (id,examNum,testingCentreId,schoolNum,insertUser,insertDate,updateUser,updateDate,isDelete) select UUID_SHORT()," + tagExamNum + Const.STRING_SEPERATOR + testingid + ",schoolNum,insertUser,insertDate,updateUser,updateDate,isDelete from testingcentre_school where testingCentreId = {cenid} ";
            rowArgList.add(new RowArg(addTcAndSchSql, insertTestingargs));
            Map args3 = StreamMap.create().put("srcExamNum", (Object) srcExamNum).put("cenid", (Object) cenid);
            List<Object> idList = this.dao._queryColList("select id from examinationroom where examNum={srcExamNum} and testingCentreId = {cenid} ", args3);
            for (Object id : idList) {
                String erId = String.valueOf(id);
                String newid = GUID.getGUIDStr();
                String insertExamRoomSql = "insert into examinationroom (id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,examNum,isDelete,testingCentreId,subjectNum,testLocation) select '" + newid + "' ,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,'" + tagExamNum + "',isDelete," + testingid + ",subjectNum,testLocation from examinationroom where id={erId} ";
                Map args4 = StreamMap.create().put("erId", (Object) erId);
                rowArgList.add(new RowArg(insertExamRoomSql, args4));
                String insertExaminiNumSql = "insert into examinationnum(examinationRoomNum,examineeNum,studentId,insertUser,insertDate,examNum,schoolNum,gradeNum,classNum,testingCentreId,subjectNum,seatNum) select '" + newid + "' ,en.examineeNum,en.studentId,en.insertUser,en.insertDate,'" + tagExamNum + "',stu.schoolNum,stu.gradeNum,stu.classNum," + testingid + ",en.subjectNum,en.seatNum from examinationnum en inner join student stu on stu.id=en.studentId where en.examinationRoomNum={erId}  and stu.isDelete='F' and stu.nodel='0'";
                rowArgList.add(new RowArg(insertExaminiNumSql, args4));
            }
        }
        this.dao._batchExecute(rowArgList);
        Map args5 = StreamMap.create().put("tagExamNum", (Object) tagExamNum);
        this.dao._execute("update examinationroom er LEFT JOIN (select examinationRoomNum,gradeNum from examinationnum where examNum={tagExamNum} GROUP BY examinationRoomNum) en on en.examinationRoomNum=er.id set er.gradeNum=en.gradeNum where er.examnum={tagExamNum}  and er.gradeNum<>en.gradeNum  ", args5);
        this.dao._execute("DELETE er from examinationroom er LEFT JOIN exampaper ep on ep.examNum=er.examNum AND ep.gradeNum = er.gradeNum AND ep.subjectNum = er.subjectNum where er.examnum={tagExamNum} and ep.examPaperNum is null", args5);
        this.dao._execute("DELETE e from examinationnum e LEFT JOIN exampaper ep on ep.examNum=e.examNum AND ep.gradeNum = e.gradeNum AND ep.subjectNum = e.subjectNum where e.examnum={tagExamNum} and ep.examPaperNum is null", args5);
        deleteExaminationNumber(tagExamNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void setClassNum(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        this.dao._execute("update examinationnum ex inner JOIN student st on st.id = ex.studentId set ex.classNum = st.classNum where ex.examNum = {examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void divideExamRoom(String user, String examNum, String type, String stuType, String elength, String idornum, String btnval, JSONArray jsonArray, String classType) {
        String studentListSql;
        DecimalFormat examroomDf = new DecimalFormat(creatStr(3, '0'));
        new DecimalFormat(creatStr(-1, '0'));
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        int stuNo = 1;
        int index = 0;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject examDataJsonObject = (JSONObject) jsonArray.get(i);
            String classString = examDataJsonObject.get("classStr").toString();
            String[] arr = classString.split(Const.STRING_SEPERATOR);
            String gradeStr = examDataJsonObject.get("gradeStr").toString();
            String schoolStr = examDataJsonObject.get("schoolStr").toString();
            String subjectStr = examDataJsonObject.get("subjectStr").toString();
            String testingCentreId = examDataJsonObject.get("testingCentreStr").toString();
            if (set1.add(subjectStr + gradeStr + schoolStr)) {
                autoDeleteEn(examNum, schoolStr, gradeStr, subjectStr, "examinationnum");
            }
            if (set2.add(subjectStr + gradeStr + testingCentreId)) {
                index = 0;
            }
            List classNumList = Arrays.asList(arr);
            for (int j = 0; j < classNumList.size(); j++) {
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_classNum, classNumList.get(j));
                args.put("stuType", stuType);
                args.put(Const.EXPORTREPORT_examNum, examNum);
                args.put("schoolStr", schoolStr);
                args.put("gradeStr", gradeStr);
                if (classType.equals("1")) {
                    studentListSql = type.equals("3") ? "SELECT sid id,examinationNumber studentId,classNum FROM student_examinationNumber WHERE examNum = {examNum} and schoolNum={schoolStr}  AND gradeNum={gradeStr}  and classNum ={classNum} and type in ({stuType[]})  " : "SELECT sid id,examinationNumber studentId,classNum FROM student_examinationNumber WHERE examNum = {examNum}  and schoolNum={schoolStr}  AND gradeNum={gradeStr}  and classNum ={classNum} and type in ({stuType[]})   ORDER BY RAND()";
                } else {
                    studentListSql = "SELECT stu.sid id,stu.examinationNumber studentId,stu.classNum FROM student_examinationNumber stu  LEFT JOIN levelstudent stul ON stul.sid = stu.sid where  stu.examNum = {examNum}  and   stul.schoolNum={schoolStr}  AND stul.gradeNum={gradeStr}  and stul.classNum ={classNum}  and stul.subjectNum={subjectStr} and stu.type in ({stuType[]}) ORDER BY RAND()";
                    args.put("subjectStr", subjectStr);
                }
                List<?> _queryBeanList = this.dao._queryBeanList(studentListSql, Student.class, args);
                getShortNameByNum(schoolStr);
                int examinationRoomNum = index + 1;
                Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum)).put(Const.EXPORTREPORT_subjectNum, (Object) subjectStr).put(Const.EXPORTREPORT_gradeNum, (Object) gradeStr);
                String examinationRoomId = this.dao._queryStr("select  id from examinationroom where examNum = {examNum}  and testingCentreId ={testingCentreId}  and examinationRoomNum = {examinationRoomNum} and  subjectNum ={subjectNum}  and gradeNum = {gradeNum}  and isDelete ='F'", args2);
                if (null == examinationRoomId || "" == examinationRoomId || "null" == examinationRoomId) {
                    String examinationroomid = GUID.getGUIDStr();
                    Map args3 = StreamMap.create().put("examinationroomid", (Object) examinationroomid).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum)).put("gradeStr", (Object) gradeStr).put("user", (Object) user).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectStr);
                    this.dao._execute("insert into examinationroom (id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,examNum,isDelete,testingCentreId,subjectNum) values({examinationroomid},{examinationRoomNum},CONCAT({examinationRoomNum},'考场'),{gradeStr},{user},now(),{examNum},'F',{testingCentreId},{subjectNum})", args3);
                    examinationRoomId = this.dao._queryStr("select  id from examinationroom where examNum = {examNum}  and testingCentreId ={testingCentreId}  and examinationRoomNum = {examinationRoomNum} and  subjectNum ={subjectNum}  and gradeNum = {gradeNum}  and isDelete ='F'", args2);
                }
                String finalExaminationRoomId = examinationRoomId;
                Map args4 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put("gradeStr", (Object) gradeStr).put("examinationRoomId", (Object) finalExaminationRoomId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectStr);
                this.dao._execute("delete from examinationnum where examNum ={examNum}  and  testingCentreId = {testingCentreId}  and gradeNum ={gradeStr}  and examinationroomNum = {examinationRoomId}  and subjectNum={subjectNum} and isDelete ='F'", args4);
                String[] arg = {"examinationRoomNum", "examineeNum", "studentID", "insertUser", "insertDate", Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_classNum, "testingCentreId", Const.EXPORTREPORT_subjectNum, "seatNum"};
                List<Object[]> list = new ArrayList<>();
                int sLen = _queryBeanList.size();
                int seatNumLen = Convert.toStr(Integer.valueOf(sLen)).length();
                for (int h = 0; h < sLen; h++) {
                    if (sLen != 0) {
                        String stuId = ((Student) _queryBeanList.get(h)).getId();
                        String studentId = ((Student) _queryBeanList.get(h)).getStudentId();
                        String calssNum = ((Student) _queryBeanList.get(h)).getClassNum();
                        String examinationRoomNums = StrUtil.fillBefore(Convert.toStr(Integer.valueOf(h + 1)), '0', seatNumLen);
                        String str = examinationRoomId + h;
                        String str2 = studentId + h;
                        String str3 = stuId + h;
                        String str4 = examNum + h;
                        String str5 = schoolStr + h;
                        String str6 = calssNum + h;
                        String str7 = testingCentreId + h;
                        String str8 = subjectStr + h;
                        String str9 = examinationRoomNums + h;
                        String currentTime = DateUtil.getCurrentTime();
                        Object[] fileds = {examinationRoomId, studentId, stuId, user, currentTime, examNum, schoolStr, gradeStr, calssNum, testingCentreId, subjectStr, examinationRoomNums};
                        list.add(fileds);
                        stuNo++;
                    }
                }
                if (!CollUtil.isEmpty(list)) {
                    this.dao.batchInsert("examinationnum", arg, list);
                    index++;
                }
            }
        }
        deleteExaminationNumber(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void divideExamRoomOfGdkc(String user, String examNum, String type, String stuType, String elength, String idornum, String btnval, JSONArray jsonArray, String classType) {
        String studentListSql;
        List<?> _queryBeanList;
        DecimalFormat examroomDf = new DecimalFormat(creatStr(3, '0'));
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        int index = 0;
        HashMap hashMap = new HashMap();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject examDataJsonObject = (JSONObject) jsonArray.get(i);
            String classString = examDataJsonObject.get("classStr").toString();
            String[] arr = classString.split(Const.STRING_SEPERATOR);
            String gradeStr = examDataJsonObject.get("gradeStr").toString();
            String schoolStr = examDataJsonObject.get("schoolStr").toString();
            String subjectStr = examDataJsonObject.get("subjectStr").toString();
            String testingCentreId = examDataJsonObject.get("testingCentreStr").toString();
            if (set1.add(subjectStr + gradeStr + schoolStr)) {
                autoDeleteEn(examNum, schoolStr, gradeStr, subjectStr, "examinationnum");
            }
            if (set2.add(subjectStr + gradeStr + testingCentreId)) {
                index = 0;
            }
            List classNumList = Arrays.asList(arr);
            for (int j = 0; j < classNumList.size(); j++) {
                String classStr = String.valueOf(classNumList.get(j));
                if (hashMap.containsKey(gradeStr + schoolStr + classStr)) {
                    _queryBeanList = (List) hashMap.get(gradeStr + schoolStr + classStr);
                } else {
                    Map args = new HashMap();
                    args.put("classStr", classStr);
                    args.put("stuType", stuType);
                    args.put(Const.EXPORTREPORT_examNum, examNum);
                    args.put("schoolStr", schoolStr);
                    args.put("gradeStr", gradeStr);
                    if (classType.equals("1")) {
                        studentListSql = type.equals("3") ? "SELECT sid id,examinationNumber studentId,classNum FROM student_examinationNumber WHERE examNum = {examNum} and schoolNum={schoolStr}  AND gradeNum={gradeStr}  and classNum = {classStr} and type in ({stuType[]})  " : "SELECT sid id,examinationNumber studentId,classNum FROM student_examinationNumber WHERE examNum = {examNum} and schoolNum={schoolStr}  AND gradeNum={gradeStr}  and classNum = {classStr} and type in ({stuType[]})   ORDER BY RAND()";
                    } else {
                        studentListSql = "SELECT stu.sid id,stu.examinationNumber studentId,stu.classNum FROM student_examinationNumber stu  LEFT JOIN levelstudent stul ON stul.sid = stu.sid where  stu.examNum ={examNum}   and   stul.schoolNum={schoolStr}  AND stul.gradeNum={gradeStr}  and stul.classNum = {classStr}   and stul.subjectNum={subjectStr} and stu.type in ({stuType[]}) ORDER BY RAND()";
                        args.put("subjectStr", subjectStr);
                    }
                    _queryBeanList = this.dao._queryBeanList(studentListSql, Student.class, args);
                    hashMap.put(gradeStr + schoolStr + classStr, _queryBeanList);
                }
                getShortNameByNum(schoolStr);
                int examinationRoomNum = index + 1;
                Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put("gradeStr", (Object) gradeStr).put(Const.EXPORTREPORT_subjectNum, (Object) subjectStr).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum));
                String examinationRoomId = this.dao._queryStr("select  id from examinationroom where examNum = {examNum}  and testingCentreId ={testingCentreId}  and gradeNum={gradeStr}  and subjectNum ={subjectNum}  and examinationRoomNum = {examinationRoomNum}  and isDelete ='F'", args2);
                if (null == examinationRoomId || "" == examinationRoomId || "null" == examinationRoomId) {
                    String examinationroomid = GUID.getGUIDStr();
                    Map args3 = StreamMap.create().put("examinationroomid", (Object) examinationroomid).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum)).put("gradeStr", (Object) gradeStr).put("user", (Object) user).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectStr);
                    this.dao._execute("insert into examinationroom (id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,examNum,isDelete,testingCentreId,subjectNum) values({examinationroomid},{examinationRoomNum},CONCAT({examinationRoomNum},'考场'),{gradeStr},{user},now(),{examNum},'F',{testingCentreId},{subjectNum})", args3);
                    examinationRoomId = this.dao._queryStr("select  id from examinationroom where examNum = {examNum}  and testingCentreId ={testingCentreId}  and gradeNum={gradeStr}  and subjectNum ={subjectNum}  and examinationRoomNum = {examinationRoomNum}  and isDelete ='F'", args2);
                }
                String finalExaminationRoomId1 = examinationRoomId;
                Map args4 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put("gradeStr", (Object) gradeStr).put("examinationRoomId", (Object) finalExaminationRoomId1).put(Const.EXPORTREPORT_subjectNum, (Object) subjectStr);
                this.dao._execute("delete from examinationnum where examNum ={examNum}  and  testingCentreId = {testingCentreId}  and gradeNum ={gradeStr} and examinationroomNum = {examinationRoomId}  and subjectNum={subjectNum}  and isDelete ='F'", args4);
                String[] arg = {"examinationRoomNum", "examineeNum", "studentID", "insertUser", "insertDate", Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_classNum, "testingCentreId", Const.EXPORTREPORT_subjectNum, "seatNum"};
                List<Object[]> list = new ArrayList<>();
                int sLen = _queryBeanList.size();
                int seatNumLen = Convert.toStr(Integer.valueOf(sLen)).length();
                for (int h = 0; h < sLen; h++) {
                    if (sLen != 0) {
                        String stuId = ((Student) _queryBeanList.get(h)).getId();
                        String studentId = ((Student) _queryBeanList.get(h)).getStudentId();
                        String calssNum = ((Student) _queryBeanList.get(h)).getClassNum();
                        String examinationRoomNums = StrUtil.fillBefore(Convert.toStr(Integer.valueOf(h + 1)), '0', seatNumLen);
                        String str = examinationRoomId + h;
                        String str2 = studentId + h;
                        String str3 = stuId + h;
                        String str4 = examNum + h;
                        String str5 = schoolStr + h;
                        String str6 = calssNum + h;
                        String str7 = testingCentreId + h;
                        String str8 = subjectStr + h;
                        String str9 = examinationRoomNums + h;
                        String str10 = user + h;
                        String str11 = gradeStr + h;
                        String currentTime = DateUtil.getCurrentTime();
                        Object[] fileds = {examinationRoomId, studentId, stuId, user, currentTime, examNum, schoolStr, gradeStr, calssNum, testingCentreId, subjectStr, examinationRoomNums};
                        list.add(fileds);
                    }
                }
                if (!CollUtil.isEmpty(list)) {
                    this.dao.batchInsert("examinationnum", arg, list);
                    index++;
                }
            }
        }
        deleteExaminationNumber(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> printStuExamID(String user, String examNum, String testCenter, String subjectNum, String levelclass, String userId) {
        String sql;
        String str = "";
        Map args2 = new HashMap();
        args2.put("userId", userId);
        Map<String, Object> ismanageMap = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args2);
        if (!testCenter.equals("-1")) {
            str = " and en.testingCentreId={testCenter} ";
        }
        String subSql = "";
        String subSql2 = "";
        if (!subjectNum.equals("-1")) {
            subSql = " and en.subjectNum={subjectNum}  ";
        } else {
            subSql2 = " en.subjectNum * 1, ";
        }
        if (userId.equals("-1") || userId.equals("-2") || null != ismanageMap) {
            sql = "SELECT er.examinationRoomNum as examinationRoomNum, en.examineeNum as examineeNum, s.studentID as studentID, s.studentName as studentName, c.className as className, g.gradeName as gradeName, sh.schoolName as schoolName, tc.testingCentreNum AS testingCentreNum, tc.testingCentreName AS testingCentreLocation, er.testLocation AS testLocation, sub.subjectName AS subjectName, en.xuankaoqufen AS xkqf, en.seatNum AS seatNum, ifnull(subc.subjectCombineName,'') subjectCombineName  from examinationnum en  left JOIN examinationroom er on en.examinationRoomNum=er.id AND er.examNum=en.examNum AND er.testingCentreId = en.testingCentreId AND er.gradeNum=en.gradeNum AND er.subjectNum=en.subjectNum  LEFT JOIN student s ON en.studentID=s.id  LEFT JOIN subjectcombine subc ON subc.subjectCombineNum=s.subjectCombineNum  LEFT JOIN testingcentre tc ON tc.id = en.testingCentreId  left JOIN school sh ON sh.id=en.schoolNum  LEFT JOIN basegrade g ON g.gradeNum=en.gradeNum  LEFT JOIN subject sub ON sub.subjectNum=en.subjectNum  LEFT JOIN class c ON c.id = en.classNum  WHERE en.examNum={examNum} " + str + subSql + " order by " + subSql2 + " tc.testingCentreNum * 1,sh.schoolNum*1,g.gradeNum*1,c.classNum*1,er.examinationRoomNum*1,en.examineeNum*1";
        } else {
            sql = "SELECT er.examinationRoomNum as examinationRoomNum, en.examineeNum as examineeNum, s.studentID as studentID, s.studentName as studentName, c.className as className, g.gradeName as gradeName, sh.schoolName as schoolName, tc.testingCentreNum AS testingCentreNum, tc.testingCentreName AS testingCentreLocation, er.testLocation AS testLocation, sub.subjectName AS subjectName, en.xuankaoqufen AS xkqf, en.seatNum AS seatNum, ifnull(subc.subjectCombineName,'') subjectCombineName  from examinationnum en  left JOIN examinationroom er on en.examinationRoomNum=er.id AND er.examNum=en.examNum AND er.testingCentreId = en.testingCentreId AND er.gradeNum=en.gradeNum AND er.subjectNum=en.subjectNum  LEFT JOIN student s ON en.studentID=s.id  LEFT JOIN subjectcombine subc ON subc.subjectCombineNum=s.subjectCombineNum  LEFT JOIN testingcentre tc ON tc.id = en.testingCentreId  inner join (select schoolNum from schoolscanpermission where userNum={userId} union select schoolNum from user where id={userId})u on u.schoolNum=en.schoolNum left JOIN school sh ON sh.id=en.schoolNum  LEFT JOIN basegrade g ON g.gradeNum=en.gradeNum  LEFT JOIN subject sub ON sub.subjectNum=en.subjectNum  LEFT JOIN class c ON c.id = en.classNum  WHERE en.examNum={examNum} " + str + subSql + " order by " + subSql2 + " tc.testingCentreNum * 1,sh.schoolNum*1,g.gradeNum*1,c.classNum*1,er.examinationRoomNum*1,en.examineeNum*1";
        }
        Map args = StreamMap.create().put("testCenter", (Object) testCenter).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("userId", (Object) userId).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryBeanList(sql, StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object getexamName(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryObject("select examName from exam where examNum={examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List allCompleteIfUpGrade(int examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum));
        new ArrayList();
        List rtnlist = this.dao._queryArrayList(" select * from grade f  inner join   (select  gradeNum,jie from exampaper where examNum ={examNum} )d on d.gradeNum = f.gradeNum and d.jie = f.jie  where f.isDelete = 'T' ", args);
        return rtnlist;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String areaAllComplete(String user, int examNum, String gradeNum) throws Exception {
        try {
            DateUtil.getCurrentTime();
            this.examManageDAO.areaAllComplete(user, examNum, gradeNum);
            DateUtil.getCurrentTime();
            return "true";
        } catch (Exception e) {
            return e.toString();
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String fufenComplete(String user, int examNum, String gradeNum) throws Exception {
        try {
            DateUtil.getCurrentTime();
            this.examManageDAO.fufenComplete(user, examNum, gradeNum);
            DateUtil.getCurrentTime();
            return "true";
        } catch (Exception e) {
            return e.toString();
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String completeExam(String user, int examNum, String[] counts) throws Exception {
        String date = DateUtil.getCurrentTime();
        Map args = StreamMap.create().put(Const.CORRECT_SCORECORRECT, (Object) "8").put("user", (Object) user).put("date", (Object) date).put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum));
        this.dao._execute("update exam set status={status} ,insertUser={user},insertDate={date} where examNum={examNum} ", args);
        String aa = this.analy.countIMIT(examNum, user, counts);
        if (!"true".equals(aa) && !"false".equals(aa)) {
            return aa;
        }
        Examlog log = new Examlog();
        log.setExamNum(Integer.valueOf(examNum));
        log.setInsertUser(user);
        log.setInsertDate(date);
        log.setIsDelete("F");
        log.setOperate("完成考试");
        this.dao.save(log);
        return "true";
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, String> getschool(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryOrderMap("select distinct sc.id, sc.schoolName from examinationnum ex,school sc where sc.id=ex.schoolNum and ex.examNum={examNum}  order by sc.schoolNum", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, String> getTestCenter(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryOrderMap("select distinct tc.id, tc.testingCentreName from examinationnum ex,testingcentre tc where tc.id=ex.testingCentreId and ex.examNum={examNum}  order by tc.testingCentreNum*1", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, String> getAuthTestCenter(String examNum, String userId) {
        String sql;
        Map args2 = new HashMap();
        args2.put("userNum", userId);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args2);
        if ("-1".equals(userId) || "-2".equals(userId) || null != map) {
            sql = "select distinct ex.testingCentreId id, tc.testingCentreName from (select distinct testingCentreId from examinationnum where examNum = {examNum} )ex left join (select id,testingCentreNum,testingCentreName from testingcentre where examNum = {examNum} ) tc on tc.id=ex.testingCentreId where tc.id is not null order by tc.testingCentreNum*1";
        } else {
            sql = "select distinct ex.testingCentreId id, tc.testingCentreName from (select distinct testingCentreId from examinationnum where examNum = {examNum} ) ex left join (select id,testingCentreId,schoolNum from testingcentre_school where examNum = {examNum} ) tcs on tcs.testingCentreId = ex.testingCentreId left join (select schoolNum from schoolscanpermission where userNum ={userId} union select schoolNum from user where id = {userId} and isDelete = 'F') sam on sam.schoolNum = tcs.schoolNum left join (select id,testingCentreNum,testingCentreName from testingcentre where examNum = {examNum} ) tc on tc.id=ex.testingCentreId where tcs.id is not null and sam.schoolNum is not null and tc.id is not null order by tc.testingCentreNum*1";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("userId", (Object) userId);
        return this.dao._queryOrderMap(sql, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, String> getAuthSchool(String examNum, String userId) {
        String sql;
        if ("-1".equals(userId) || "-2".equals(userId)) {
            sql = "select distinct ex.schoolNum id, sch.schoolName from (select distinct schoolNum from examinationnum where examNum = {examNum} )ex left join school sch on sch.id=ex.schoolNum where sch.id is not null order by sch.schoolNum*1";
        } else {
            sql = "select distinct ex.schoolNum id, sch.schoolName from (select distinct schoolNum from examinationnum where examNum = {examNum} ) ex left join (select schoolNum from schauthormanage where userId ={userId}  and isDelete = 'F' union select schoolNum from user where id = {userId}  and isDelete = 'F') sam on sam.schoolNum = ex.schoolNum left join school sch on sch.id=ex.schoolNum where sam.schoolNum is not null and sch.id is not null order by sch.schoolNum*1";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("userId", (Object) userId);
        return this.dao._queryOrderMap(sql, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, String> getEnSubject(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryOrderMap("SELECT DISTINCT sub.subjectNum,sub.subjectName FROM(SELECT DISTINCT subjectNum FROM examinationroom WHERE examNum ={examNum}  ) ex INNER JOIN SUBJECT sub ON sub.subjectNum = ex.subjectNum ORDER BY sub.subjectNum * 1", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getexamclass(String examNum, String schoolNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryColList("select distinct cl.classNum  from exam ex,class cl,examinationroom exroom where  ex.examNum=exroom.examNum and exroom.schoolNum=cl.schoolNum and exroom.gradeNum=cl.gradeNum and exroom.gradeNum={gradeNum} and ex.examNum={examNum}  and exroom.schoolNum={schoolNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getexamschool(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryColList("select distinct schoolNum from examinationnum where examNum={examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getExamTC(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryColList("select distinct testingCentreId from examinationnum where examNum={examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getexamallschool1(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryColList("select distinct schoolNum from examinationnum where examNum={examNum}", StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> getexamallschool(String examNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryBeanList(" select schoolNum,schoolName  from   school  where  schoolNum={schoolNum} ", StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object getschnum(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryObject("select schoolName from school where schoolNum={schoolNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object getclanum(String classNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) classNum);
        return this.dao._queryObject("select className from class where classNum={classNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getexamgrade(String examNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryColList("select DISTINCT gr.gradeNum from exam ex,grade gr,examinationroom exroom where ex.examNum=exroom.examNum and exroom.schoolNum=gr.schoolNum and ex.examNum={examNum}  and exroom.schoolNum={schoolNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer insertIntoClassLevel(String examNum) {
        return this.examManageDAO.insertIntoClassLevel(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer insertIntogradeLevel(String examNum) {
        return this.examManageDAO.insertIntogradeLevel(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getCheckStuPaperList(String examNum, String subjectNum, String gradeNum, String examRoomNum, String studentId, String schoolNum, String radioValue, String examPaperNum, String testCenter) {
        return this.examManageDAO.getCheckStuPaperList(examNum, subjectNum, gradeNum, examRoomNum, studentId, schoolNum, radioValue, examPaperNum, testCenter);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getCheckStuPaperList_score(String examNum, String subjectNum, String gradeNum, String examRoomNum, String studentId, String schoolNum, String radioValue, String examPaperNum, String testCenter, String height_score, String low_score) {
        return this.examManageDAO.getCheckStuPaperList_score(examNum, subjectNum, gradeNum, examRoomNum, studentId, schoolNum, radioValue, examPaperNum, testCenter, height_score, low_score);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getCheckStuPaperListPage(String examNum, String subjectNum, String gradeNum, String examRoomNum, String studentId, String schoolNum, int pageStart, int pageSize, String radioValue, String examPaperNum, String testCenter) {
        return this.examManageDAO.getCheckStuPaperListPage(examNum, subjectNum, gradeNum, examRoomNum, studentId, schoolNum, pageStart, pageSize, radioValue, examPaperNum, testCenter);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getCheckStuPaperListPage_score(String examNum, String subjectNum, String gradeNum, String examRoomNum, String studentId, String schoolNum, int pageStart, int pageSize, String radioValue, String examPaperNum, String testCenter, String height_score, String low_score) {
        return this.examManageDAO.getCheckStuPaperListPage_score(examNum, subjectNum, gradeNum, examRoomNum, studentId, schoolNum, pageStart, pageSize, radioValue, examPaperNum, testCenter, height_score, low_score);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getCheckStuPaperListPage_Class(String examNum, String subjectNum, String gradeNum, String examRoom, String studentId, String schoolNum, int pageStart, int pageSize) {
        return this.examManageDAO.getCheckStuPaperListPage_Class(examNum, subjectNum, gradeNum, examRoom, studentId, schoolNum, pageStart, pageSize);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Studentpaperimage getspiObj(int id) {
        return this.examManageDAO.getspiObj(id);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getCheckStuPaperDetail(String examNum, String subjectNum, String gradeNum, String examRoomNum, String studentId, String schoolNum, int pageStart, int pageSize, String radioValue, String examPaperNum, String testCenter) {
        return this.examManageDAO.getCheckStuPaperDetail(examNum, subjectNum, gradeNum, examRoomNum, studentId, schoolNum, pageStart, pageSize, radioValue, examPaperNum, testCenter);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getCheckStuPaperDetail_score(String examNum, String subjectNum, String gradeNum, String examRoomNum, String studentId, String schoolNum, int pageStart, int pageSize, String radioValue, String examPaperNum, String testCenter, String height_score, String low_score) {
        return this.examManageDAO.getCheckStuPaperDetail_score(examNum, subjectNum, gradeNum, examRoomNum, studentId, schoolNum, pageStart, pageSize, radioValue, examPaperNum, testCenter, height_score, low_score);
    }

    public String creatStr(int lenght, char ch) {
        String string = "";
        for (int i = 0; i < lenght; i++) {
            string = string + ch;
        }
        return string;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getexamNumByName(String examName) {
        Map args = StreamMap.create().put("examName", (Object) examName);
        int a = 0;
        if (null != this.dao._queryStr("SELECT examNum from exam where examName={examName} ", args) && !"".equals(this.dao._queryStr("SELECT examNum from exam where examName={examName} ", args)) && !"null".equals(this.dao._queryStr("SELECT examNum from exam where examName={examName} ", args))) {
            a = this.dao._queryInt("SELECT examNum from exam where examName={examName} ", args).intValue();
        }
        return String.valueOf(a);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void subexamset(Examsetting examset, String user, String date) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examset.getExamNum());
        this.dao._execute("DELETE from examsetting where examNum={examNum} ", args);
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examset.getExamNum()).put("levera", (Object) Float.valueOf(examset.getLevera())).put("leverb", (Object) Float.valueOf(examset.getLeverb())).put("leverc", (Object) Float.valueOf(examset.getLeverc())).put("leverd", (Object) Float.valueOf(examset.getLeverd())).put("levere", (Object) Float.valueOf(examset.getLevere())).put("excellence", (Object) Float.valueOf(examset.getExcellence())).put("wellrate", (Object) Float.valueOf(examset.getWellrate())).put("highScore", (Object) Float.valueOf(examset.getHighScore())).put("insertUser", (Object) user).put("lowScore", (Object) Float.valueOf(examset.getLowScore())).put("insertdate", (Object) date).put("missingExam", (Object) examset.getMissingExam()).put("discipline", (Object) examset.getDiscipline()).put("lingfe", (Object) examset.getLingfe()).put("zonfen", (Object) examset.getZonfen()).put("RSRw_highScore", (Object) Float.valueOf(examset.getRSRw_highScore())).put("RSRw_excellence", (Object) Float.valueOf(examset.getRSRw_excellence())).put("RSRw_wellrate", (Object) Float.valueOf(examset.getRSRw_wellrate())).put("RSRw_lowScore", (Object) Float.valueOf(examset.getRSRw_lowScore())).put("RSRw_average", (Object) Float.valueOf(examset.getRSRw_average()));
        this.dao._execute("INSERT INTO examsetting(examNum,levera,leverb,leverc,leverd,levere,excellence,wellrate,highScore,insertUser,insertdate,lowScore,missingExam,discipline,lingfe,zonfen,RSRw_highScore,RSRw_excellence,RSRw_wellrate,RSRw_lowScore,RSRw_average) VALUES ({examNum},{levera},{leverb},{leverc},{leverd},{levere},{excellence},{wellrate},{highScore},{insertUser},{insertdate},{lowScore},{missingExam},{discipline},{lingfe},{zonfen},{RSRw_highScore},{RSRw_excellence},{RSRw_wellrate},{RSRw_lowScore},{RSRw_average} )", args2);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void deleteByexamNum(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        this.dao._execute("DELETE from examsetting where examNum={examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Examsetting getone(String examNum) {
        if (null == examNum) {
            Object recentExamNum = this.dao.queryObject("select examNum from exam order by examDate desc limit 1");
            if (null == recentExamNum) {
                return null;
            }
            examNum = recentExamNum.toString();
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select CAST(ROUND(levera*100,0) AS FLOAT) levera,CAST(ROUND(leverb*100,0) AS FLOAT) leverb,CAST(ROUND(leverc*100,0) AS FLOAT) leverc,CAST(ROUND(leverd*100,0) AS FLOAT) leverd,CAST(ROUND(levere*100,0) AS FLOAT) levere");
        sql.append(",CAST(ROUND(excellence*100) AS FLOAT) excellence,CAST(ROUND(wellrate*100) AS FLOAT) wellrate,CAST(ROUND(highScore*100,0) AS FLOAT) highScore,CAST(ROUND(lowScore*100,0) AS FLOAT) lowScore");
        sql.append(",IFNULL(missingExam,0) missingExam,IFNULL(discipline,0) discipline,IFNULL(lingfe,0) lingfe,IFNULL(zonfen,1) zonfen");
        sql.append(",RSRw_highScore,RSRw_excellence,RSRw_wellrate,RSRw_lowScore,RSRw_average");
        sql.append(" from examsetting ");
        sql.append("where examNum={examNum}");
        String finalExamNum = examNum;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) finalExamNum);
        return (Examsetting) this.dao._queryBean(sql.toString(), Examsetting.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getLeakFromScore(String examNum, String subjectNum, String gradeNum, String testCenter, String roomNum) {
        return this.examManageDAO.getLeakFromScore(examNum, subjectNum, gradeNum, testCenter, roomNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getLeakFromScoreForScoreModify(String examNum, String subjectNum, String gradeNum, String testCenter, String roomNum, String studentId, String page) {
        String examPaperNum = this.examDAO.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("roomNum", roomNum);
        args.put("testCenter", testCenter);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("type", "0");
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("page", page);
        String illegalSql = "WHERE examPaperNum= {examPaperNum}  ";
        if (roomNum != null && !"-1".equals(roomNum) && !"".equals(roomNum)) {
            illegalSql = illegalSql + " AND examinationRoomNum= {roomNum}   ";
        }
        if (testCenter != null && !"-1".equals(testCenter) && !"".equals(testCenter)) {
            illegalSql = illegalSql + " AND testingCentreId= {testCenter}    ";
        }
        String stuSql = "SELECT GROUP_CONCAT(DISTINCT studentId) FROM illegal  \tWHERE examPaperNum={examPaperNum} ";
        if (testCenter != null && !"-1".equals(testCenter)) {
            stuSql = stuSql + "\tAND testingCentreId={testCenter}  ";
        }
        if (roomNum != null && !"-1".equals(roomNum) && !"".equals(roomNum)) {
            stuSql = stuSql + "\tAND examinationRoomNum={roomNum}   ";
        }
        Object studentIds = this.dao._queryObject(stuSql + "\tAND  type={type} ", args);
        args.put("studentIds", Convert.toStr(studentIds, ""));
        String illegalSql2 = illegalSql + "  \t  AND gradeNum= {gradeNum}          AND studentId not in ({studentIds[]}) ";
        if (studentId != null && !"".equals(studentId)) {
            illegalSql2 = illegalSql2 + " AND studentId={studentId}  ";
        }
        if (page != null && !"".equals(page)) {
            illegalSql2 = illegalSql2 + " AND page={page}    ";
        }
        String sql = "SELECT DISTINCT questionNum,examPaperNum   FROM score    " + illegalSql2 + "AND isException IS NOT null      AND  (isException={isException}  OR isException={isException1})  ORDER BY REPLACE(questionNum,'_','.')*1 ASC";
        args.put("isException", "0");
        args.put("isException1", "1");
        this.log.info(" 进入分数校对或点击完成处理时提示是否还有异常分数- getLeakFromScoreForScoreModify-sql=====:" + sql);
        List questionNumList = this.dao._queryBeanList(sql, Score.class, args);
        return questionNumList;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getExpQuestionImg(String schoolNum, String gradeNum, String examPaperNum, String questionNum, String examroomNum, String expMark, String examNum) {
        return this.examManageDAO.getExpQuestionImg(schoolNum, gradeNum, examPaperNum, questionNum, examroomNum, expMark, examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public byte[] getImage(String examPaperNum, String questionNum, String studentId) {
        return this.examManageDAO.getImage(examPaperNum, questionNum, studentId);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer updateScore(String studentId, String examPaperNum, String questionNum, double inputScore, double oldScore, String qAnswer, String qType, String stuClassNum, String schoolNum, String examroomNum, String qException, String scoreId) {
        return this.examManageDAO.updateScore(studentId, examPaperNum, questionNum, inputScore, oldScore, qAnswer, qType, stuClassNum, schoolNum, examroomNum, qException, scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer updateScore_modified(String studentId, String examPaperNum, String questionNum, double inputScore, double oldScore, String qAnswer, String qType, String stuClassNum, String schoolNum, String examroomNum, String qException, String scoreId) {
        return this.examManageDAO.updateScore_modified(studentId, examPaperNum, questionNum, inputScore, oldScore, qAnswer, qType, stuClassNum, schoolNum, examroomNum, qException, scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getRoomAndExamineeLength(String examNum) {
        return this.examManageDAO.getRoomAndExamineeLength(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List isExistTheExaminee(String examNum, String testCenter, String gradenum, String roomNum, String inputExaminee, String examPaperNum, int page, String groupNum, String oldStudentId, String oldExamroomId, String examroomornot) {
        return this.examManageDAO.isExistTheExaminee(examNum, testCenter, gradenum, roomNum, inputExaminee, examPaperNum, page, groupNum, oldStudentId, oldExamroomId, examroomornot);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List searchCreateExam(String examNum) {
        return this.examManageDAO.searchCreateExam(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List searchDefine(String examNum) {
        return this.examManageDAO.searchDefine(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List viewScanExaminationRoom(String examNum) {
        return this.examManageDAO.viewScanExaminationRoom(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getScanRoomSuject(String examNum, String scanOrNot) {
        return this.examManageDAO.getScanRoomSuject(examNum, scanOrNot);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getSchoolGrade() {
        return this.examManageDAO.getSchoolGrade();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getGradeDefineDetail(String examNum, String finishOrNot) {
        return this.examManageDAO.getGradeDefineDetail(examNum, finishOrNot);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public CorrectStatus getScoreCheckCount(String examNum) {
        return this.examManageDAO.getScoreCheckCount(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getCertainCheckDetail(String examNum, String statusType) {
        return this.examManageDAO.getCertainCheckDetail(examNum, statusType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public CorrectStatus getExamineeNumCheckCount(String examNum) {
        return this.examManageDAO.getExamineeNumCheckCount(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getExamineeNumCheckDetail(String examNum, String numStatusType) {
        return this.examManageDAO.getExamineeNumCheckDetail(examNum, numStatusType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public CorrectStatus getNumErrorCount(String examNum) {
        return this.examManageDAO.getNumErrorCount(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getNumErrorDetail(String examNum, String errorType) {
        return this.examManageDAO.getNumErrorDetail(examNum, errorType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public CorrectStatus getScoreExceptionCount(String exanNum) {
        return this.examManageDAO.getScoreExceptionCount(exanNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getScoreExceptionDetail(String examNum, String scoreExceptionType) {
        return this.examManageDAO.getScoreExceptionDetail(examNum, scoreExceptionType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void importexamStudent(String examNum, File file, String filename, int user, String date, String type, String che, String Upchecked) throws IOException {
        String tname;
        String stuId;
        String classNum;
        String gradeNum;
        String schoolNum;
        String stuId2;
        String classNum2;
        String gradeNum2;
        String schoolNum2;
        new ArrayList();
        Set<String> index = new HashSet<>();
        List stuIdExamroomExamineeList = new ArrayList();
        new ArrayList();
        List examNumList = new ArrayList();
        if (user == -1) {
            tname = "admin";
        } else {
            String tname2 = String.valueOf(user);
            tname = tname2.replace("-", "");
        }
        String deltsql = "DROP TABLE IF EXISTS temp_examroom" + tname + "";
        this.dao.execute(deltsql);
        String createSql = "CREATE TABLE temp_examroom" + tname + " SELECT * FROM examinationroom WHERE 1=1 AND examNum={examNum} ";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute(createSql, args);
        String deltsql2 = "DROP TABLE IF EXISTS temp_testCenter" + tname;
        this.dao.execute(deltsql2);
        String createSql2 = "CREATE TABLE temp_testCenter" + tname + " SELECT * FROM testingcentre WHERE 1=1 AND examNum={examNum} ";
        this.dao._execute(createSql2, args);
        Exam eLength = (Exam) this.dao._queryBean("SELECT examinationRoomLength,examineeLength FROM exam  WHERE examnum={examNum} ", Exam.class, args);
        InputStream input = new FileInputStream(file);
        if (filename == null) {
            throwException("请选择Excel表之后再导入...");
        }
        int ind = filename.lastIndexOf(".");
        boolean flag1 = "xls".equalsIgnoreCase(filename.substring(ind + 1));
        boolean flag2 = "xlsx".equalsIgnoreCase(filename.substring(ind + 1));
        XSSFWorkbook xb = null;
        if (flag2) {
            try {
                xb = new XSSFWorkbook(input);
            } catch (Exception e) {
            }
            XSSFSheet xsheet = xb.getSheetAt(0);
            xsheet.getRow(0);
            int j = 1;
            while (true) {
                if (j >= xsheet.getPhysicalNumberOfRows()) {
                    break;
                }
                ArrayList arrayList = new ArrayList();
                XSSFRow xRow = xsheet.getRow(j);
                if (xRow == null) {
                    break;
                }
                int y = xRow.getRowNum();
                XSSFCell examroomNum1 = xRow.getCell(7);
                String examroomNum = getCellValue(examroomNum1, 7, y);
                if (che.equals("1") && examroomNum.length() != eLength.getExaminationRoomLength()) {
                    throwException("请检查Excel表中第" + (j + 1) + "行第8列的考场位数是否正确！");
                    break;
                }
                XSSFCell examineeNum1 = xRow.getCell(10);
                String examineeNum = getCellValue(examineeNum1, 10, y);
                if (che.equals("1") && examineeNum.length() != eLength.getExamineeLength()) {
                    throwException("请检查Excel表中第" + (j + 1) + "行第11列的考号位数是否正确！");
                    break;
                }
                XSSFCell studentName = xRow.getCell(6);
                String stuName = getCellValue(studentName, 6, y).trim().replaceAll("\u3000", "").replace(" ", "");
                XSSFCell testCentreNum = xRow.getCell(0);
                String tcNum = getCellValue(testCentreNum, 0, y).trim().replace(" ", "");
                XSSFCell testCentreLocation = xRow.getCell(1);
                String tcLocation = getCellValue(testCentreLocation, 1, y).trim().replace(" ", "");
                XSSFCell testLocation = xRow.getCell(8);
                String tLocation = "";
                if (testLocation != null) {
                    tLocation = testLocation.getStringCellValue();
                }
                String tLocation2 = tLocation.replace(" ", "");
                String jie = "";
                if (type.equals("1")) {
                    XSSFCell schoolName = xRow.getCell(2);
                    String schName = getCellValue(schoolName, 2, y).trim();
                    schoolNum = getschNum(schName.replace(" ", ""));
                    XSSFCell gradeName = xRow.getCell(3);
                    String gName = getCellValue(gradeName, 3, y).trim();
                    gradeNum = getgNum(gName.replace(" ", ""));
                    String testingCentreId = getTcId(examNum, tcNum);
                    if (Upchecked.equals("1") && index.add(testingCentreId + "--" + gradeNum)) {
                        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
                        this.dao._execute("DELETE FROM examinationroom WHERE examNum={examNum} AND testingCentreId={testingCentreId}  AND gradeNum={gradeNum} ", args2);
                        this.dao._execute("DELETE FROM examinationnum  WHERE examNum={examNum}  AND testingCentreId={testingCentreId}  AND gradeNum={gradeNum} ", args2);
                    }
                    XSSFCell studentNum = xRow.getCell(5);
                    String stuNum = getCellValue(studentNum, 5, y);
                    Student stu = getStuByNum(stuNum);
                    if (stu == null || "".equals(stu)) {
                        throwException("学号为" + stuNum + "学生不存在，请检查EXCEL表格！");
                    } else {
                        jie = String.valueOf(stu.getJie());
                    }
                    classNum = String.valueOf(stu.getClassNum());
                    stuId = stu.getStudentId();
                    if (!stu.getStudentName().equals(stuName)) {
                        throwException("学生姓名为：" + stuName + "学生与学生名单不匹配，请检查EXCEL表格！");
                    }
                    Map args3 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_studentId, (Object) getStuId2(stuId));
                    int stuCount = this.dao._queryInt("SELECT count(1) FROM examinationnum WHERE examNum={examNum}  AND studentId={studentId} ", args3).intValue();
                    if (stuCount > 0) {
                        Map args4 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_studentId, (Object) getStuId2(stuId));
                        Student stuerr = (Student) this.dao._queryBean("SELECT s.schoolName ext1,g.gradeName ext2 FROM examinationnum ex left join school s on s.id = ex.schoolNum left join grade g on g.gradeNum = ex.gradeNum WHERE ex.examNum={examNum} AND ex.studentId={studentId} ", Student.class, args4);
                        throwException("学号： " + stuNum + " 已经被  " + stuerr.getExt1() + " " + stuerr.getExt2() + " 分配过考号了！请确认");
                    }
                } else {
                    XSSFCell studentId = xRow.getCell(5);
                    stuId = getCellValue(studentId, 5, y);
                    classNum = getStuOneClassNum(stuId);
                    Map args5 = StreamMap.create().put("stuId", (Object) stuId);
                    Student stu2 = (Student) this.dao._queryBean("select gradeNum,schoolNum,studentName,jie from student where studentId={stuId} ", Student.class, args5);
                    if (stu2 == null) {
                        gradeNum = "";
                        schoolNum = "";
                    } else {
                        gradeNum = String.valueOf(stu2.getGradeNum());
                        schoolNum = String.valueOf(stu2.getSchoolNum());
                        String testingCentreId2 = getTcId(examNum, tcNum);
                        if (Upchecked.equals("1") && index.add(testingCentreId2 + "--" + gradeNum)) {
                            Map args6 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId2).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
                            this.dao._execute("DELETE FROM examinationroom WHERE examNum={examNum} AND testingCentreId={testingCentreId}  AND gradeNum={gradeNum} ", args6);
                            this.dao._execute("DELETE FROM examinationnum  WHERE examNum={examNum}  AND testingCentreId={testingCentreId}  AND gradeNum={gradeNum} ", args6);
                        }
                    }
                    if (null == gradeNum || gradeNum.equals("") || gradeNum.equals("null") || null == schoolNum || schoolNum.equals("") || schoolNum.equals("null")) {
                        throwException("ID号为：" + stuId + "的学生在学生基础信息中不存在，请检查该学生的基础信息！");
                    }
                    if (Upchecked.equals("0")) {
                        String sql = "SELECT r.id,r.examineeNum FROM (select er.id ,ex.examineeNum from examinationnum ex LEFT JOIN examinationroom  er ON er.id = ex.examinationRoomNum  where ex.schoolNum = {schoolNum}  AND ex.examNum = {examNum} and  ex.gradeNum={gradeNum} )r";
                        Map args7 = new HashMap();
                        args7.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                        args7.put(Const.EXPORTREPORT_examNum, examNum);
                        args7.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                        examNumList = this.dao._queryBeanList(sql, Pjbdata.class, args7);
                    }
                    if (!stu2.getStudentName().equals(stuName)) {
                        throwException("学生姓名为：" + stuName + "学生与学生名单不匹配，请检查EXCEL表格！");
                    }
                    jie = String.valueOf(stu2.getJie());
                }
                Map args8 = new HashMap();
                args8.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                args8.put(Const.EXPORTREPORT_classNum, classNum);
                args8.put(Const.EXPORTREPORT_examNum, examNum);
                args8.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args8.put("jie", jie);
                String ischeckep = this.dao._queryStr("select * from exampaper ep inner join classexam clex on clex.examPaperNum = ep.examPaperNum where clex.schoolNum = {schoolNum}  and clex.classNum = {classNum}  and ep.examNum = {examNum}  and ep.gradeNum = {gradeNum}  and ep.jie = {jie}  ", args8);
                if (null == ischeckep || ischeckep.equals("") || ischeckep.equals("null")) {
                    throwException("第" + (j + 1) + "行所在的学校、年级或班级并没有参加考试，请在编辑考试中检查！");
                }
                String sqlTTC = "select id from temp_testCenter" + tname + " where testingCentreNum={tcNum}  and examNum={examNum} ";
                Map args9 = new HashMap();
                args9.put("tcNum", tcNum);
                args9.put(Const.EXPORTREPORT_examNum, examNum);
                String tcId = this.dao._queryStr(sqlTTC, args9);
                if (null == tcId || tcId.equals("") || tcId.equals("null")) {
                    tcId = GUID.getGUIDStr();
                    String sql2 = "INSERT INTO temp_testCenter" + tname + "(id,examNum,testingCentreNum,testingCentreName,insertUser,insertDate) VALUES({tcId},{examNum} ,{tcNum} ,{tcLocation} ,{user} ,{date} )";
                    Map args10 = new HashMap();
                    args10.put("tcId", tcId);
                    args10.put(Const.EXPORTREPORT_examNum, examNum);
                    args10.put("tcNum", tcNum);
                    args10.put("tcLocation", tcLocation);
                    args10.put("user", Integer.valueOf(user));
                    args10.put("date", date);
                    this.dao._execute(sql2, args10);
                }
                Map args11 = new HashMap();
                args11.put("tcId", tcId);
                args11.put(Const.EXPORTREPORT_examNum, examNum);
                args11.put("tcNum", tcNum);
                args11.put("tcLocation", tcLocation);
                args11.put("user", Integer.valueOf(user));
                args11.put("date", date);
                this.dao._execute("insert into testingcentre(id,examNum,testingCentreNum,testingCentreName,insertUser,insertDate)\tVALUES({tcId},{examNum},{tcNum},{tcLocation},{user},{date}) on DUPLICATE KEY UPDATE testingCentreNum={tcNum} ,testingCentreName={tcLocation},examNum={examNum} ", args11);
                String sql3 = "select id from temp_examroom" + tname + " where examinationRoomNum={examroomNum}  and testingCentreId={tcId}  and gradeNum={gradeNum}  and examNum={examNum} ";
                Map args12 = new HashMap();
                args12.put("examroomNum", examroomNum);
                args12.put("tcId", tcId);
                args12.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args12.put(Const.EXPORTREPORT_examNum, examNum);
                String guid = this.dao._queryStr(sql3, args12);
                if (null == guid || guid.equals("") || guid.equals("null")) {
                    guid = GUID.getGUIDStr();
                    String sql4 = "INSERT INTO temp_examroom" + tname + "(id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,isDelete,examNum,testingCentreId,testLocation) VALUES({guid},{examroomNum},{examroomNum},{examroomNum2},{gradeNum},{user},{date},,'F',{examNum} ,{tcId},{tLocation})";
                    Map args13 = new HashMap();
                    args13.put("guid", guid);
                    args13.put("examroomNum", examroomNum);
                    args13.put("examroomNum2", examroomNum + "考场");
                    args13.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                    args13.put("user", Integer.valueOf(user));
                    args13.put("date", date);
                    args13.put(Const.EXPORTREPORT_examNum, examNum);
                    args13.put("tcId", tcId);
                    args13.put("tLocation", tLocation2);
                    this.dao._execute(sql4, args13);
                }
                Map args14 = new HashMap();
                args14.put("guid", guid);
                args14.put("examroomNum", examroomNum);
                args14.put("examroomNum2", examroomNum + "考场");
                args14.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args14.put("user", Integer.valueOf(user));
                args14.put("date", date);
                args14.put(Const.EXPORTREPORT_examNum, examNum);
                args14.put("tcId", tcId);
                args14.put("tLocation", tLocation2);
                this.dao._execute("insert into examinationroom(id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,isDelete,examNum,testingCentreId,testLocation)\tVALUES({guid},{examroomNum},{examroomNum2},{gradeNum},{user},{date},'F',{examNum},{tcId},{tLocation}) on DUPLICATE KEY UPDATE examinationRoomNum={examroomNum} ,examinationRoomName={examroomNum2} ,gradeNum={examNum} ,examNum={examNum},testingCentreId= {tcId} ,testLocation={tLocation} ", args14);
                for (int i1 = 0; i1 < stuIdExamroomExamineeList.size(); i1++) {
                    List stuIdExamroomExaminee1 = (List) stuIdExamroomExamineeList.get(i1);
                    if (guid.equals(stuIdExamroomExaminee1.get(0)) && examineeNum.equals(stuIdExamroomExaminee1.get(1))) {
                        throwException("考号：" + examineeNum + "重复，请检查Excel表格。。。。。。");
                    }
                }
                if (Upchecked.equals("0")) {
                    for (int m = 0; m < examNumList.size(); m++) {
                        Pjbdata pdata = (Pjbdata) examNumList.get(m);
                        if (guid.equals(pdata.getId()) && examineeNum.equals(pdata.getExamineeNum())) {
                            throwException("考号：" + examineeNum + "重复，请检查Excel表格。。。。。。");
                        }
                    }
                }
                arrayList.add(guid);
                arrayList.add(examineeNum);
                stuIdExamroomExamineeList.add(stuIdExamroomExamineeList.size(), arrayList);
                String stuId3 = getStuId2(stuId);
                Map args15 = new HashMap();
                args15.put("stuId", stuId3);
                args15.put("guid", guid);
                args15.put(Const.EXPORTREPORT_examNum, examNum);
                Map args16 = new HashMap();
                args16.put("stuId", stuId3);
                args16.put("guid", guid);
                args16.put("examineeNum", examineeNum);
                args16.put(Const.EXPORTREPORT_examNum, examNum);
                int stuexamCount = this.dao._queryInt("SELECT count(1) FROM examinationnum WHERE studentId={stuId} and examinationRoomNum={guid}  AND examNum={examNum} ", args15).intValue();
                int stuexamCount2 = this.dao._queryInt("SELECT count(1) FROM examinationnum WHERE studentId={stuId}  and examinationRoomNum={guid} AND examineeNum={examineeNum} AND examNum={examNum} ", args16).intValue();
                if (stuexamCount == 0 || stuexamCount2 == 0) {
                    Map args17 = new HashMap();
                    args17.put(Const.EXPORTREPORT_examNum, examNum);
                    args17.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                    args17.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                    args17.put("examineeNum", examineeNum);
                    String oldStuId = this.dao._queryStr("SELECT studentId FROM examinationnum WHERE examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum} AND examineeNum={examineeNum}  ", args17);
                    if (null != oldStuId && !"".equals(oldStuId) && !"null".equals(oldStuId)) {
                        String checkStuScoreSql = "SELECT count(s.id) FROM (SELECT examPaperNum,id FROM score WHERE studentId={oldStuId} " + oldStuId + "' AND schoolNum={schoolNum} ' AND gradeNum={gradeNum} \tUNION ALL SELECT exampaperNum,id FROM objectivescore WHERE studentId={oldStuId}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum} ) s\tLEFT JOIN exampaper ex ON s.exampaperNum = ex.examPaperNum WHERE ex.examNum={examNum} ";
                        Map args18 = new HashMap();
                        args18.put("oldStuId", oldStuId);
                        args18.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                        args18.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                        args18.put(Const.EXPORTREPORT_examNum, examNum);
                        int stuScoreCount = this.dao._queryInt(checkStuScoreSql, args18).intValue();
                        if (stuScoreCount > 0) {
                            updatestuexamroom(String.valueOf(stuId3), gradeNum, schoolNum, examNum, guid, examineeNum, oldStuId);
                        }
                    }
                }
                Map args19 = new HashMap();
                args19.put("guid", guid);
                args19.put("examineeNum", examineeNum);
                args19.put("stuId", stuId3);
                args19.put("user", Integer.valueOf(user));
                args19.put("date", date);
                args19.put(Const.EXPORTREPORT_examNum, examNum);
                args19.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                args19.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args19.put(Const.EXPORTREPORT_classNum, classNum);
                args19.put("tcId", tcId);
                try {
                    this.dao._execute("insert into examinationnum (examinationRoomNum,examineeNum,studentID,insertUser,insertDate,isDelete,examNum,schoolNum,gradeNum,classNum,testingCentreId) VALUES ({guid},{examineeNum},{stuId},{user},{date},'F',{examNum},{schoolNum},{gradeNum},{classNum},{tcId}  ) on DUPLICATE KEY UPDATE examinationRoomNum={guid},examineeNum= {examineeNum} ,studentID= {stuId} ,examNum={examNum},schoolNum={schoolNum},gradeNum={gradeNum},classNum={classNum},testingCentreId={tcId} ", args19);
                } catch (Exception e2) {
                }
                j++;
            }
        }
        if (flag1) {
            POIFSFileSystem fs = new POIFSFileSystem(input);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            sheet.getRow(0);
            int j2 = 1;
            while (true) {
                if (j2 >= sheet.getPhysicalNumberOfRows()) {
                    break;
                }
                ArrayList arrayList2 = new ArrayList();
                HSSFRow hssfrow = sheet.getRow(j2);
                if (hssfrow == null) {
                    break;
                }
                int y2 = hssfrow.getRowNum();
                HSSFCell examroomNum12 = hssfrow.getCell(7);
                String examroomNum2 = getCellValue(examroomNum12, 7, y2);
                if (che.equals("1") && examroomNum2.length() != eLength.getExaminationRoomLength()) {
                    throwException("请检查Excel表中第" + (j2 + 1) + "行第8列的考场位数是否正确！");
                    break;
                }
                HSSFCell examineeNum12 = hssfrow.getCell(10);
                String examineeNum2 = getCellValue(examineeNum12, 10, y2);
                if (che.equals("1") && examineeNum2.length() != eLength.getExamineeLength()) {
                    throwException("请检查Excel表中第" + (j2 + 1) + "行第11列的考号位数是否正确！");
                    break;
                }
                HSSFCell studentName2 = hssfrow.getCell(6);
                String stuName2 = getCellValue(studentName2, 6, y2).trim().replaceAll("\u3000", "").replace(" ", "");
                HSSFCell testCentreNum2 = hssfrow.getCell(0);
                String tcNum2 = getCellValue(testCentreNum2, 0, y2).trim().replace(" ", "");
                HSSFCell testCentreLocation2 = hssfrow.getCell(1);
                String tcLocation2 = getCellValue(testCentreLocation2, 1, y2).trim().replace(" ", "");
                HSSFCell testLocation2 = hssfrow.getCell(8);
                String tLocation3 = "";
                if (testLocation2 != null) {
                    tLocation3 = testLocation2.getStringCellValue();
                }
                String tLocation4 = tLocation3.replace(" ", "");
                String jie2 = "";
                if (type.equals("1")) {
                    HSSFCell schoolName2 = hssfrow.getCell(2);
                    String schName2 = getCellValue(schoolName2, 2, y2).trim();
                    schoolNum2 = getschNum(schName2.replace(" ", ""));
                    HSSFCell gradeName2 = hssfrow.getCell(3);
                    String gName2 = getCellValue(gradeName2, 3, y2).trim();
                    gradeNum2 = getgNum(gName2.replace(" ", ""));
                    String testingCentreId3 = getTcId(examNum, tcNum2);
                    if (Upchecked.equals("1") && index.add(testingCentreId3 + "--" + gradeNum2)) {
                        Map args20 = new HashMap();
                        args20.put(Const.EXPORTREPORT_examNum, examNum);
                        args20.put("testingCentreId", testingCentreId3);
                        args20.put(Const.EXPORTREPORT_gradeNum, gradeNum2);
                        this.dao._execute("DELETE FROM examinationroom WHERE examNum={examNum} AND testingCentreId={testingCentreId}  AND gradeNum={gradeNum}  ", args20);
                        this.dao._execute("DELETE FROM examinationnum  WHERE examNum={examNum}' AND testingCentreId={testingCentreId} AND gradeNum={gradeNum} ", args20);
                    }
                    HSSFCell studentNum2 = hssfrow.getCell(5);
                    String stuNum2 = getCellValue(studentNum2, 5, y2);
                    Student stu3 = getStuByNum(stuNum2);
                    if (stu3 == null || "".equals(stu3)) {
                        throwException("学号为" + stuNum2 + "学生不存在，请检查EXCEL表格！");
                    } else {
                        jie2 = String.valueOf(stu3.getJie());
                    }
                    classNum2 = String.valueOf(stu3.getClassNum());
                    stuId2 = stu3.getStudentId();
                    if (!stu3.getStudentName().equals(stuName2)) {
                        throwException("学生姓名为：" + stuName2 + "学生与学生名单不匹配，请检查EXCEL表格！");
                    }
                    Map args21 = new HashMap();
                    args21.put(Const.EXPORTREPORT_examNum, examNum);
                    args21.put(Const.EXPORTREPORT_studentId, getStuId2(stuId2));
                    int stuCount2 = this.dao._queryInt("SELECT count(1) FROM examinationnum WHERE examNum={examNum} AND studentId={studentId} ", args21).intValue();
                    if (stuCount2 > 0) {
                        String checkStuIdSql1 = "SELECT s.schoolName ext1,g.gradeName ext2 FROM examinationnum ex left join school s on s.id = ex.schoolNum left join grade g on g.gradeNum = ex.gradeNum WHERE ex.examNum={examNum}  AND ex.studentId={studentId} " + getStuId2(stuId2) + "' ";
                        Map args22 = new HashMap();
                        args22.put(Const.EXPORTREPORT_examNum, examNum);
                        args22.put(Const.EXPORTREPORT_studentId, getStuId2(stuId2));
                        Student stuerr2 = (Student) this.dao._queryBean(checkStuIdSql1, Student.class, args22);
                        throwException("学号： " + stuNum2 + " 已经被  " + stuerr2.getExt1() + " " + stuerr2.getExt2() + " 分配过考号了！请确认");
                    }
                } else {
                    HSSFCell studentId2 = hssfrow.getCell(5);
                    stuId2 = getCellValue(studentId2, 5, y2);
                    classNum2 = getStuOneClassNum(stuId2);
                    Map args23 = new HashMap();
                    args23.put("stuId", stuId2);
                    Student stu4 = (Student) this.dao._queryBean("select gradeNum,schoolNum,studentName,jie from student where studentId={stuId} ", Student.class, args23);
                    if (stu4 == null) {
                        gradeNum2 = "";
                        schoolNum2 = "";
                    } else {
                        gradeNum2 = String.valueOf(stu4.getGradeNum());
                        schoolNum2 = String.valueOf(stu4.getSchoolNum());
                        String testingCentreId4 = getTcId(examNum, tcNum2);
                        if (Upchecked.equals("1") && index.add(testingCentreId4 + "--" + gradeNum2)) {
                            Map args24 = new HashMap();
                            args24.put(Const.EXPORTREPORT_examNum, examNum);
                            args24.put("testingCentreId", testingCentreId4);
                            args24.put(Const.EXPORTREPORT_gradeNum, gradeNum2);
                            this.dao._execute("DELETE FROM examinationroom WHERE examNum={examNum}  AND testingCentreId={testingCentreId} AND gradeNum={gradeNum}  ", args24);
                            this.dao._execute("DELETE FROM examinationnum  WHERE examNum={examNum}  AND testingCentreId={testingCentreId} AND gradeNum={gradeNum} ", args24);
                        }
                    }
                    if (null == gradeNum2 || gradeNum2.equals("") || gradeNum2.equals("null") || null == schoolNum2 || schoolNum2.equals("") || schoolNum2.equals("null")) {
                        throwException("ID号为：" + stuId2 + "的学生在学生基础信息中不存在，请检查该学生的基础信息！");
                    }
                    if (Upchecked.equals("0")) {
                        String sql5 = "SELECT r.id,r.examineeNum FROM (select er.id ,ex.examineeNum from examinationnum ex LEFT JOIN examinationroom  er ON er.id = ex.examinationRoomNum  where ex.schoolNum = {schoolNum}  AND ex.examNum = {examNum}  and  ex.gradeNum={gradeNum} )r";
                        Map args25 = new HashMap();
                        args25.put(Const.EXPORTREPORT_schoolNum, schoolNum2);
                        args25.put(Const.EXPORTREPORT_examNum, examNum);
                        args25.put(Const.EXPORTREPORT_gradeNum, gradeNum2);
                        examNumList = this.dao._queryBeanList(sql5, Pjbdata.class, args25);
                    }
                    if (!stu4.getStudentName().equals(stuName2)) {
                        throwException("学生姓名为：" + stuName2 + "学生与学生名单不匹配，请检查EXCEL表格！");
                    }
                    jie2 = String.valueOf(stu4.getJie());
                }
                Map args26 = new HashMap();
                args26.put(Const.EXPORTREPORT_schoolNum, schoolNum2);
                args26.put(Const.EXPORTREPORT_classNum, classNum2);
                args26.put(Const.EXPORTREPORT_examNum, examNum);
                args26.put(Const.EXPORTREPORT_gradeNum, gradeNum2);
                args26.put("jie", jie2);
                String ischeckep2 = this.dao._queryStr("select * from exampaper ep inner join classexam clex on clex.examPaperNum = ep.examPaperNum where clex.schoolNum = {schoolNum} and clex.classNum ={classNum}  and ep.examNum = {examNum} and ep.gradeNum = {gradeNum}  and ep.jie = {jie} ", args26);
                if (null == ischeckep2 || ischeckep2.equals("") || ischeckep2.equals("null")) {
                    throwException("第" + (j2 + 1) + "行所在的学校、年级或班级并没有参加考试，请在编辑考试中检查！");
                }
                String sqlTTC2 = "select id from temp_testCenter" + tname + " where testingCentreNum={tcNum}  and examNum={examNum} ";
                Map args27 = new HashMap();
                args27.put("tcNum", tcNum2);
                args27.put(Const.EXPORTREPORT_examNum, examNum);
                String tcId2 = this.dao._queryStr(sqlTTC2, args27);
                if (null == tcId2 || tcId2.equals("") || tcId2.equals("null")) {
                    tcId2 = GUID.getGUIDStr();
                    String sql6 = "INSERT INTO temp_testCenter" + tname + "(id,examNum,testingCentreNum,testingCentreName,insertUser,insertDate) VALUES({tcId},{examNum},{tcNum},{tcLocation},{user},{date})";
                    Map args28 = new HashMap();
                    args28.put("tcId", tcId2);
                    args28.put(Const.EXPORTREPORT_examNum, examNum);
                    args28.put("tcNum", tcNum2);
                    args28.put("tcLocation", tcLocation2);
                    args28.put("user", Integer.valueOf(user));
                    args28.put("date", date);
                    this.dao._execute(sql6, args28);
                }
                Map args29 = new HashMap();
                args29.put("tcId", tcId2);
                args29.put(Const.EXPORTREPORT_examNum, examNum);
                args29.put("tcNum", tcNum2);
                args29.put("tcLocation", tcLocation2);
                args29.put("user", Integer.valueOf(user));
                args29.put("date", date);
                this.dao._execute("insert into testingcentre(id,examNum,testingCentreNum,testingCentreName,insertUser,insertDate)\tVALUES({tcId},{examNum},{tcNum},{tcLocation},{user},{date}) on DUPLICATE KEY UPDATE testingCentreNum={tcNum},testingCentreName={tcLocation} ,examNum={examNum} ", args29);
                String sql32 = "select id from temp_examroom" + tname + " where examinationRoomNum={examroomNum}  and testingCentreId={tcId}  and gradeNum={gradeNum}  and examNum={examNum} ";
                Map args30 = new HashMap();
                args30.put("examroomNum", examroomNum2);
                args30.put("tcId", tcId2);
                args30.put(Const.EXPORTREPORT_gradeNum, gradeNum2);
                args30.put(Const.EXPORTREPORT_examNum, examNum);
                String guid2 = this.dao._queryStr(sql32, args30);
                if (null == guid2 || guid2.equals("") || guid2.equals("null")) {
                    guid2 = GUID.getGUIDStr();
                    String sql7 = "INSERT INTO temp_examroom" + tname + "(id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,isDelete,examNum,testingCentreId,testLocation) VALUES({guid},{examroomNum},{examroomNum2},{gradeNum},{user},{date} ,'F',{examNum},{tcId},{tLocation})";
                    Map args31 = new HashMap();
                    args31.put("guid", guid2);
                    args31.put("examroomNum", examroomNum2);
                    args31.put("examroomNum2", examroomNum2 + "考场");
                    args31.put(Const.EXPORTREPORT_gradeNum, gradeNum2);
                    args31.put("user", Integer.valueOf(user));
                    args31.put("date", date);
                    args31.put(Const.EXPORTREPORT_examNum, examNum);
                    args31.put("tcId", tcId2);
                    args31.put("tLocation", tLocation4);
                    this.dao._execute(sql7, args31);
                }
                Map args32 = new HashMap();
                args32.put("guid", guid2);
                args32.put("examroomNum", examroomNum2);
                args32.put("examroomNum2", examroomNum2 + "考场");
                args32.put(Const.EXPORTREPORT_gradeNum, gradeNum2);
                args32.put("user", Integer.valueOf(user));
                args32.put("date", date);
                args32.put(Const.EXPORTREPORT_examNum, examNum);
                args32.put("tcId", tcId2);
                args32.put("tLocation", tLocation4);
                this.dao._execute("insert into examinationroom(id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,isDelete,examNum,testingCentreId,testLocation)\tVALUES({guid},{examroomNum},{examroomNum2},{gradeNum},{user},{date},'F',{examNum},{tcId},{tLocation}) on DUPLICATE KEY UPDATE examinationRoomNum={examroomNum},examinationRoomName={examroomNum2},gradeNum={gradeNum},examNum= {examNum},testingCentreId={tcId},testLocation={tLocation} ", args32);
                for (int i12 = 0; i12 < stuIdExamroomExamineeList.size(); i12++) {
                    List stuIdExamroomExaminee12 = (List) stuIdExamroomExamineeList.get(i12);
                    if (guid2.equals(stuIdExamroomExaminee12.get(0)) && examineeNum2.equals(stuIdExamroomExaminee12.get(1))) {
                        throwException("考号：" + examineeNum2 + "重复，请检查Excel表格。。。。。。");
                    }
                }
                if (Upchecked.equals("0")) {
                    for (int m2 = 0; m2 < examNumList.size(); m2++) {
                        Pjbdata pdata2 = (Pjbdata) examNumList.get(m2);
                        if (guid2.equals(pdata2.getId()) && examineeNum2.equals(pdata2.getExamineeNum())) {
                            throwException("考号：" + examineeNum2 + "重复，请检查Excel表格。。。。。。");
                        }
                    }
                }
                arrayList2.add(guid2);
                arrayList2.add(examineeNum2);
                stuIdExamroomExamineeList.add(stuIdExamroomExamineeList.size(), arrayList2);
                String stuId4 = getStuId2(stuId2);
                Map args33 = new HashMap();
                args33.put("stuId", stuId4);
                args33.put("guid", guid2);
                args33.put(Const.EXPORTREPORT_examNum, examNum);
                Map args34 = new HashMap();
                args34.put("stuId", stuId4);
                args34.put("guid", guid2);
                args34.put("examineeNum", examineeNum2);
                args34.put(Const.EXPORTREPORT_examNum, examNum);
                int stuexamCount3 = this.dao._queryInt("SELECT count(1) FROM examinationnum WHERE studentId={stuId}  and examinationRoomNum={guid}   AND examNum={examNum} ", args33).intValue();
                int stuexamCount22 = this.dao._queryInt("SELECT count(1) FROM examinationnum WHERE studentId={stuId}  and examinationRoomNum={guid}  AND examineeNum={examineeNum}  AND examNum={examNum} ", args34).intValue();
                if (stuexamCount3 == 0 || stuexamCount22 == 0) {
                    Map args35 = new HashMap();
                    args35.put(Const.EXPORTREPORT_examNum, examNum);
                    args35.put(Const.EXPORTREPORT_schoolNum, schoolNum2);
                    args35.put(Const.EXPORTREPORT_gradeNum, gradeNum2);
                    args35.put("examineeNum", examineeNum2);
                    String oldStuId2 = this.dao._queryStr("SELECT studentId FROM examinationnum WHERE examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum} AND examineeNum={examineeNum} ", args35);
                    if (null != oldStuId2 && !"".equals(oldStuId2) && !"null".equals(oldStuId2)) {
                        Map args36 = new HashMap();
                        args36.put("oldStuId", oldStuId2);
                        args36.put(Const.EXPORTREPORT_schoolNum, schoolNum2);
                        args36.put(Const.EXPORTREPORT_gradeNum, gradeNum2);
                        args36.put(Const.EXPORTREPORT_examNum, examNum);
                        int stuScoreCount2 = this.dao._queryInt("SELECT count(s.id) FROM (SELECT examPaperNum,id FROM score WHERE studentId={oldStuId}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum} \tUNION ALL SELECT exampaperNum,id FROM objectivescore WHERE studentId={oldStuId}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum}  ) s\tLEFT JOIN exampaper ex ON s.exampaperNum = ex.examPaperNum WHERE ex.examNum={examNum} ", args36).intValue();
                        if (stuScoreCount2 > 0) {
                            updatestuexamroom(String.valueOf(stuId4), gradeNum2, schoolNum2, examNum, guid2, examineeNum2, oldStuId2);
                        }
                    }
                }
                Map args37 = new HashMap();
                args37.put("guid", guid2);
                args37.put("examineeNum", examineeNum2);
                args37.put("stuId", stuId4);
                args37.put("user", Integer.valueOf(user));
                args37.put("date", date);
                args37.put(Const.EXPORTREPORT_examNum, examNum);
                args37.put(Const.EXPORTREPORT_schoolNum, schoolNum2);
                args37.put(Const.EXPORTREPORT_gradeNum, gradeNum2);
                args37.put(Const.EXPORTREPORT_classNum, classNum2);
                args37.put("tcId", tcId2);
                try {
                    this.dao._execute("insert into examinationnum (examinationRoomNum,examineeNum,studentID,insertUser,insertDate,isDelete,examNum,schoolNum,gradeNum,classNum,testingCentreId) VALUES ({guid},{examineeNum},{stuId},{user},{date},'F',{examNum},{schoolNum},{gradeNum},{classNum},{tcId}) on DUPLICATE KEY UPDATE ' examinationRoomNum={guid} ',examineeNum={examineeNum}  ,studentID={stuId}  ,examNum={examNum}  ,schoolNum={schoolNum}  ,gradeNum={gradeNum} ,classNum={classNum},testingCentreId={tcId} ", args37);
                } catch (Exception e3) {
                }
                j2++;
            }
        }
        input.close();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void importexamStudentLeq(String examNum, File file, String filename, String user, String date, String type, String che, String Upchecked) {
        String tname;
        Map args7;
        if (filename == null) {
            throwException("请选择Excel表之后再导入...");
        }
        boolean isExcelXls = filename.toLowerCase().endsWith("xls");
        HSSFWorkbook hSSFWorkbook = null;
        try {
            if (isExcelXls) {
                hSSFWorkbook = new HSSFWorkbook(new FileInputStream(file));
            } else {
                hSSFWorkbook = new XSSFWorkbook(new FileInputStream(file));
            }
        } catch (Exception e) {
        }
        if (hSSFWorkbook == null) {
            throwException("Excel表没有数据，请检查...");
        }
        if ("-1".equals(user)) {
            tname = "admin";
        } else {
            String tname2 = String.valueOf(user);
            tname = tname2.replace("-", "");
        }
        String deltsql2 = "DROP TABLE IF EXISTS temp_testCenter" + tname;
        this.dao.execute(deltsql2);
        Set<String> indexS = new HashSet<>();
        Set<String> indexTC = new HashSet<>();
        for (int i = 0; i < hSSFWorkbook.getNumberOfSheets(); i++) {
            Sheet sheet = hSSFWorkbook.getSheetAt(i);
            int j = 1;
            while (true) {
                if (j >= sheet.getPhysicalNumberOfRows()) {
                    break;
                }
                Row Row = sheet.getRow(j);
                if (Row == null) {
                    throwException("这里已没有数据，第" + i + "个sheet第" + j + "行");
                    break;
                }
                int y = Row.getRowNum();
                Cell examroomNum1 = Row.getCell(6);
                String examroomNum = getCellValue(examroomNum1, 6, y, i);
                Cell examineeNum1 = Row.getCell(9);
                String examineeNum = getCellValue(examineeNum1, 9, y, i);
                if (che.equals("1")) {
                    Map args = new HashMap();
                    args.put(Const.EXPORTREPORT_examNum, examNum);
                    Exam eLength = (Exam) this.dao._queryBean("SELECT examinationRoomLength,examineeLength FROM exam  WHERE examnum={examNum} ", Exam.class, args);
                    if (examroomNum.length() != eLength.getExaminationRoomLength()) {
                        throwException("请检查Excel表中第 " + (i + 1) + " 个sheet第 " + (j + 1) + " 行第 7 列的考场位数是否正确！");
                        break;
                    } else if (examineeNum.length() != eLength.getExamineeLength()) {
                        throwException("请检查Excel表中第" + (i + 1) + " 个sheet第 " + (j + 1) + " 行第 10 列的考号位数是否正确！");
                        break;
                    }
                }
                Cell testingCentreName = Row.getCell(0);
                String tcName = getCellValue(testingCentreName, 0, y, i).trim().replace(" ", "");
                String testingCentreId = getTcId(examNum, tcName);
                Cell schoolName = Row.getCell(1);
                String schName = getCellValue(schoolName, 1, y, i).trim();
                String schoolNum = getschNum(schName.replace(" ", ""));
                Cell gradeName = Row.getCell(2);
                String gName = getCellValue(gradeName, 2, y, i).trim();
                String gradeNum = getgNum(gName.replace(" ", ""));
                Cell subjectName = Row.getCell(10);
                String subName = getCellValue(subjectName, 10, y, i).trim();
                String subjectNum = getSubNum(subName.replace(" ", ""));
                Cell className = Row.getCell(3);
                String claName = getCellValue(className, 3, y, i);
                String claName2 = claName.replace(" ", "");
                Cell studentNumOrId = Row.getCell(4);
                String stuNumOrId = getCellValue(studentNumOrId, 4, y, i).replace(" ", "");
                Cell studentName = Row.getCell(5);
                String stuName = getCellValue(studentName, 5, y, i).replace(" ", "");
                ServletContext context = ServletActionContext.getServletContext();
                String levelclass = Configuration.getInstance().getLevelclass();
                String stuNote = "ID号";
                String stuWhereSql = Const.EXPORTREPORT_studentId;
                if (type.equals("1")) {
                    stuWhereSql = "studentNum";
                    stuNote = "学号";
                }
                String sSql = "select s.id,s.schoolNum,s.gradeNum,s.classNum,c.className,s.studentName from student s left join class c on c.id = s.classNum and c.gradeNum = s.gradeNum and c.schoolNum = s.schoolNum and c.jie = s.jie where s." + stuWhereSql + " = '" + stuNumOrId + "'";
                if ("T".equals(levelclass)) {
                    sSql = "select s.id,s.schoolNum,s.gradeNum,s.classNum,lc.className,s.studentName from student s LEFT JOIN levelstudent ls ON ls.sid = s.id left join levelclass lc on lc.id = ls.classNum and lc.gradeNum = s.gradeNum and lc.schoolNum = s.schoolNum and lc.jie = s.jie where s." + stuWhereSql + " = '" + stuNumOrId + "' AND lc.subjectNum = {subjectNum} ";
                }
                Map args2 = new HashMap();
                args2.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                List<?> _queryBeanList = this.dao._queryBeanList(sSql, Student.class, args2);
                if (type.equals("1") && _queryBeanList.size() > 1) {
                    throwException("第 " + (i + 1) + " 个sheet中的学生学号不唯一，请按ID号的方式导入！");
                }
                Student stu = null;
                if (_queryBeanList.size() > 0) {
                    stu = (Student) _queryBeanList.get(0);
                }
                if (stu == null) {
                    throwException("第 " + (i + 1) + " 个sheet第 " + (j + 1) + " 行" + stuNote + "为： " + stuNumOrId + " 的学生在学生基础信息中不存在，请检查该学生的" + stuNumOrId + "！");
                } else if (!schoolNum.equals(String.valueOf(stu.getSchoolNum())) || !gradeNum.equals(String.valueOf(stu.getGradeNum())) || !claName2.equals(String.valueOf(stu.getClassName())) || !stuName.equals(String.valueOf(stu.getStudentName()))) {
                    throwException("第 " + (i + 1) + " 个sheet第 " + (j + 1) + " 行" + stuNote + "为： " + stuNumOrId + " 的学生与其基础信息不对应，请检查该学生的基础信息！");
                }
                String studentId = stu.getId();
                String classNum = stu.getClassNum();
                String jie = getjie(gradeNum, schoolNum);
                Map args3 = new HashMap();
                args3.put(Const.EXPORTREPORT_examNum, examNum);
                args3.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args3.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                args3.put("jie", jie);
                String ischeckep = this.dao._queryStr("select examPaperNum from exampaper where examNum = {examNum} and gradeNum = {gradeNum}  and subjectNum = {subjectNum}  and jie = {jie}  and isHidden = 'F'", args3);
                if (null == ischeckep || "".equals(ischeckep) || "null".equals(ischeckep)) {
                    throwException("第 " + (i + 1) + " 个sheet第 " + (j + 1) + " 行所在的年级、科目并没有参加考试，请在编辑考试中检查！");
                }
                if (Upchecked.equals("1") && indexS.add(testingCentreId + "--" + gradeNum + "--" + subjectNum)) {
                    Map args4 = new HashMap();
                    args4.put(Const.EXPORTREPORT_examNum, examNum);
                    args4.put("testingCentreId", testingCentreId);
                    args4.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                    args4.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                    this.dao._execute("DELETE FROM examinationnum  WHERE examNum={examNum} AND testingCentreId={testingCentreId} AND gradeNum={gradeNum}  AND subjectNum={subjectNum} ", args4);
                    this.dao._execute("DELETE FROM examinationroom WHERE examNum={examNum} AND testingCentreId={testingCentreId} AND gradeNum={gradeNum} AND subjectNum={subjectNum} ", args4);
                }
                String sameExamineeNum = (String) context.getAttribute(Const.sameExamineeNum);
                if ("T".equals(sameExamineeNum)) {
                    Map args5 = new HashMap();
                    args5.put(Const.EXPORTREPORT_studentId, studentId);
                    args5.put(Const.EXPORTREPORT_examNum, examNum);
                    String enNum = this.dao._queryStr("SELECT examineeNum FROM examinationnum WHERE studentId = {studentId} AND examNum ={examNum} limit 1", args5);
                    if (!enNum.equals(examineeNum)) {
                        throwException("第 " + (i + 1) + " 个sheet第 " + (j + 1) + " 行  " + stuName + " 学生的考号与其已经导入过学科的考号不一致，请检查！");
                    }
                }
                Cell seatNum = Row.getCell(8);
                String sNum = getCellValue(seatNum, 8, y, i).trim().replace(" ", "");
                if (null == testingCentreId || testingCentreId.equals("") || testingCentreId.equals("null")) {
                    testingCentreId = GUID.getGUIDStr();
                    if (indexTC.add(tcName + "--" + subjectNum)) {
                        int max = 1;
                        Map args6 = new HashMap();
                        args6.put(Const.EXPORTREPORT_examNum, examNum);
                        args6.put("tcName", tcName);
                        String maxer = this.dao._queryStr("SELECT id FROM testingcentre where examNum = {examNum} and testingCentreName = {tcName}  LIMIT 1", args6);
                        if (null != maxer && !"null".equals(maxer) && !"".equals(maxer)) {
                            testingCentreId = maxer;
                        } else {
                            do {
                                max++;
                                args7 = new HashMap();
                                args7.put(Const.EXPORTREPORT_examNum, examNum);
                                args7.put("max", Integer.valueOf(max));
                            } while (null != this.dao._queryObject("SELECT testingCentreNum FROM testingcentre where examNUm = {examNum} and testingCentreNum = {max} ", args7));
                            Map args8 = new HashMap();
                            args8.put("testingCentreId", testingCentreId);
                            args8.put(Const.EXPORTREPORT_examNum, examNum);
                            args8.put("max", Integer.valueOf(max));
                            args8.put("tcName", tcName);
                            args8.put("user", user);
                            args8.put("date", date);
                            this.dao._execute("insert into testingcentre(id,examNum,testingCentreNum,testingCentreName,insertUser,insertDate)\tVALUES({testingCentreId},{examNum},{max},{tcName},{user},{date}) \ton DUPLICATE KEY UPDATE testingCentreNum={max}  ,testingCentreName={tcName}  ,insertUser={user}  ,insertDate={date}  ,examNum={examNum} ", args8);
                        }
                    }
                }
                String sql3 = "select id from examinationroom where examNum={examNum} " + examNum + " and testingCentreId={testingCentreId}  and gradeNum={gradeNum}  and subjectNum={subjectNum}  and examinationRoomNum={examroomNum} ";
                Map args9 = new HashMap();
                args9.put(Const.EXPORTREPORT_examNum, examNum);
                args9.put("testingCentreId", testingCentreId);
                args9.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args9.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                args9.put("examroomNum", examroomNum);
                String examroomId = this.dao._queryStr(sql3, args9);
                if (null == examroomId || examroomId.equals("") || examroomId.equals("null")) {
                    examroomId = GUID.getGUIDStr();
                }
                Map args10 = new HashMap();
                args10.put(Const.EXPORTREPORT_studentId, studentId);
                args10.put(Const.EXPORTREPORT_examNum, examNum);
                args10.put("testingCentreId", testingCentreId);
                args10.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                args10.put("examroomId", examroomId);
                args10.put(Const.EXPORTREPORT_sNum, sNum);
                String ischeckseat = this.dao._queryStr("SELECT id FROM examinationnum WHERE studentId <> {studentId} AND examNum = {examNum} AND testingCentreId = {testingCentreId} AND subjectNum = {subjectNum}  AND examinationRoomNum ={examroomId}  AND seatNum = {sNum} ", args10);
                if (null != ischeckseat && !"".equals(ischeckseat) && !"null".equals(ischeckseat)) {
                    throwException("第 " + (i + 1) + " 个sheet第 " + (j + 1) + " 行座位号 " + sNum + " 已经存在，请检查！");
                }
                Map args11 = new HashMap();
                args11.put(Const.EXPORTREPORT_examNum, examNum);
                args11.put("testingCentreId", testingCentreId);
                args11.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args11.put("examineeNum", examineeNum);
                args11.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                String ischecken = this.dao._queryStr("SELECT studentId FROM examinationnum WHERE examNum={examNum}  AND testingCentreId={testingCentreId}  AND gradeNum={gradeNum}  AND examineeNum={examineeNum}  AND subjectNum={subjectNum} ", args11);
                if (null != ischecken && !"".equals(ischecken) && !"null".equals(ischecken)) {
                    throwException("第 " + (i + 1) + " 个sheet第 " + (j + 1) + " 行考号 " + examineeNum + " 已经存在，请检查！");
                }
                Cell tLocationName = Row.getCell(7);
                String tLocation = "";
                if (null != tLocationName && !tLocationName.equals("") && !tLocationName.equals("null") && tLocationName.getCellType() != CellType.BLANK) {
                    if (tLocationName.getCellType() == CellType.NUMERIC) {
                        tLocation = this.df.format(tLocationName.getNumericCellValue());
                    } else if (tLocationName.getCellType() == CellType.STRING) {
                        tLocation = tLocationName.getStringCellValue().trim();
                    }
                }
                Map args12 = new HashMap();
                args12.put("examroomId", examroomId);
                args12.put("examroomNum", examroomNum);
                args12.put("examroomNum2", examroomNum + "考场");
                args12.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args12.put("user", user);
                args12.put("date", date);
                args12.put(Const.EXPORTREPORT_examNum, examNum);
                args12.put("testingCentreId", testingCentreId);
                args12.put("tLocation", tLocation);
                args12.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                this.dao._execute("insert into examinationroom(id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,isDelete,examNum,testingCentreId,testLocation,subjectNum)\tVALUES({examroomId},{examroomNum},{examroomNum2},{gradeNum},{user},{date},'F',{examNum},{testingCentreId},{tLocation},{subjectNum}) \ton DUPLICATE KEY UPDATE examinationRoomNum={examroomNum}  ,examinationRoomName={examroomNum2}  ,gradeNum={gradeNum}  ,insertUser={user}  ,insertDate={date}  ,examNum={examNum}  ,testingCentreId={testingCentreId}  ,testLocation={tLocation}  ,subjectNum={subjectNum} ", args12);
                String checkStuexamSql = "SELECT count(1) FROM examinationnum WHERE studentId={studentId} " + studentId + "  AND examNum={examNum}   AND subjectNum={subjectNum}  and examinationRoomNum={examroomId} ";
                Map args13 = new HashMap();
                args13.put(Const.EXPORTREPORT_studentId, studentId);
                args13.put(Const.EXPORTREPORT_examNum, examNum);
                args13.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                args13.put("examroomId", examroomId);
                Map args14 = new HashMap();
                args14.put(Const.EXPORTREPORT_studentId, studentId);
                args14.put(Const.EXPORTREPORT_examNum, examNum);
                args14.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                args14.put("examroomId", examroomId);
                args14.put("examineeNum", examineeNum);
                int stuexamCount = this.dao._queryInt(checkStuexamSql, args13).intValue();
                int stuexamCount2 = this.dao._queryInt("SELECT count(1) FROM examinationnum WHERE studentId={studentId}   AND examNum={examNum}    AND subjectNum={subjectNum}   and examinationRoomNum={examroomId}   AND examineeNum={examineeNum} ", args14).intValue();
                if (stuexamCount == 0 || stuexamCount2 == 0) {
                    Map args15 = new HashMap();
                    args15.put(Const.EXPORTREPORT_examNum, examNum);
                    args15.put("testingCentreId", testingCentreId);
                    args15.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                    args15.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                    args15.put("examineeNum", examineeNum);
                    String oldStuId = this.dao._queryStr("SELECT studentId FROM examinationnum WHERE examNum={examNum} AND testingCentreId={testingCentreId} AND gradeNum={gradeNum} AND subjectNum={subjectNum} AND examineeNum={examineeNum} ", args15);
                    if (null != oldStuId && !"".equals(oldStuId) && !"null".equals(oldStuId)) {
                        String checkStuScoreSql = "SELECT count(s.id) FROM (SELECT examPaperNum,id FROM score WHERE studentId={oldStuId} " + oldStuId + " AND schoolNum={schoolNum}   AND gradeNum={gradeNum}  UNION ALL SELECT exampaperNum,id FROM objectivescore WHERE studentId={oldStuId} s  AND schoolNum={schoolNum}   AND gradeNum={gradeNum}  ) s LEFT JOIN exampaper ex ON s.exampaperNum = ex.examPaperNum WHERE ex.examNum={examNum} ";
                        Map args16 = new HashMap();
                        args16.put("oldStuId", oldStuId);
                        args16.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                        args16.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                        args16.put(Const.EXPORTREPORT_examNum, examNum);
                        int stuScoreCount = this.dao._queryInt(checkStuScoreSql, args16).intValue();
                        if (stuScoreCount > 0) {
                            updatestuexamroom(String.valueOf(studentId), gradeNum, schoolNum, examNum, examroomId, examineeNum, oldStuId);
                        }
                    }
                }
                Map args17 = new HashMap();
                args17.put("examroomId", examroomId);
                args17.put("examineeNum", examineeNum);
                args17.put(Const.EXPORTREPORT_studentId, studentId);
                args17.put("user", user);
                args17.put("date", date);
                args17.put(Const.EXPORTREPORT_examNum, examNum);
                args17.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                args17.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args17.put(Const.EXPORTREPORT_classNum, classNum);
                args17.put("testingCentreId", testingCentreId);
                args17.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                args17.put(Const.EXPORTREPORT_sNum, sNum);
                try {
                    this.dao._execute("insert into examinationnum (examinationRoomNum,examineeNum,studentID,insertUser,insertDate,isDelete,examNum,schoolNum,gradeNum,classNum,testingCentreId,subjectNum,seatNum) VALUES ({examroomId},{examineeNum},{studentId},{user},{date},'F',{examNum},{schoolNum},{gradeNum},{classNum},{testingCentreId},{subjectNum},{sNum}') on DUPLICATE KEY UPDATE examinationRoomNum={examroomId}  ,examineeNum={examineeNum}  ,studentID={studentId}  ,insertUser={user}  ,insertDate={date}  ,examNum={examNum}  ,schoolNum={schoolNum} ,gradeNum={gradeNum},classNum={classNum}  ,testingCentreId={testingCentreId} ,subjectNum={subjectNum} ,seatNum={sNum} ", args17);
                } catch (Exception e2) {
                }
                j++;
            }
        }
    }

    public void updatestuexamroom(String studentId, String gradeNum, String schoolNum, String examNum, String examroom, String examineeNum, String oldStudentId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        Student student = (Student) this.dao._queryBean("SELECT st.schoolNum schoolNum,st.gradeNum gradeNum,st.classNum classNum,st.jie jie ,cl.studentType ext1 FROM student st LEFT JOIN class cl ON  st.classNum=cl.id  WHERE st.id={studentId} ", Student.class, args);
        new ArrayList();
        Map args2 = new HashMap();
        args2.put("oldStudentId", oldStudentId);
        args2.put(Const.EXPORTREPORT_examNum, examNum);
        args2.put("examroom", examroom);
        args2.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        GUID.getGUID();
        this.dao._execute("UPDATE cantrecognized can LEFT JOIN (SELECT id,examPaperNum FROM regexaminee WHERE studentId={oldStudentId} )reg  ON can.regId=reg.id  LEFT JOIN(SELECT examPaperNum,examNum FROM exampaper WHERE examNum={examNum} ) ex ON ex.examPaperNum=reg.examPaperNum SET can.examinationRoomNum={examroom}  ,can.schoolNum={schoolNum}  ", args2);
        stu(studentId, examNum);
        String sql05 = "UPDATE correctlog cg LEFT JOIN exampaper ep ON cg.examPaperNum=ep.examPaperNum SET cg.studentId={studentId} " + studentId + " ,cg.schoolNum={schoolNum}  ,cg.examinationRoomNum={examroom}  ,cg.classNum={classNum}   WHERE ep.examNum={examNum}   AND cg.studentId={oldStudentId}  ";
        Map args3 = new HashMap();
        args3.put(Const.EXPORTREPORT_studentId, studentId);
        args3.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args3.put("examroom", examroom);
        args3.put(Const.EXPORTREPORT_classNum, student.getClassNum());
        args3.put(Const.EXPORTREPORT_examNum, examNum);
        args3.put("oldStudentId", oldStudentId);
        long guid = GUID.getGUID();
        Map args4 = new HashMap();
        args4.put("guid", Long.valueOf(guid));
        args4.put(Const.EXPORTREPORT_studentId, studentId);
        args4.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE correctlog c LEFT JOIN exampaper ep ON c.exampaperNum=ep.exampaperNum SET c.studentId={guid}   WHERE c.studentId={studentId}   AND ep.examNum={examNum} ", args4);
        this.dao._execute(sql05, args3);
        Student oldstu = stu(oldStudentId, examNum);
        Map args5 = new HashMap();
        args5.put("oldStudentId", oldStudentId);
        args5.put(Const.EXPORTREPORT_schoolNum, oldstu.getSchoolNum());
        args5.put("examinationRoomNum", oldstu.getExt2());
        args5.put(Const.EXPORTREPORT_classNum, oldstu.getClassNum());
        args5.put(Const.EXPORTREPORT_examNum, examNum);
        args5.put("guid", Long.valueOf(guid));
        this.dao._execute("UPDATE correctlog cg LEFT JOIN exampaper ep ON cg.examPaperNum=ep.examPaperNum SET cg.studentId={oldStudentId}  ,cg.schoolNum={schoolNum}  ,cg.examinationRoomNum={examinationRoomNum}   ,cg.classNum={classNum}   WHERE ep.examNum={examNum}   AND cg.studentId={guid} ", args5);
        Map args6 = new HashMap();
        args6.put(Const.EXPORTREPORT_studentId, studentId);
        args6.put(Const.EXPORTREPORT_classNum, student.getClassNum());
        args6.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args6.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args6.put("oldStudentId", oldStudentId);
        args6.put(Const.EXPORTREPORT_examNum, examNum);
        long guid2 = GUID.getGUID();
        Map args7 = new HashMap();
        args7.put("guid", Long.valueOf(guid2));
        args7.put(Const.EXPORTREPORT_studentId, studentId);
        args7.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE ctb_error_reason c LEFT JOIN exampaper ep ON c.exampaperNum = ep.examPaperNum SET c.studentId={guid}   WHERE c.studentId={studentId}   AND ep.examNum={examNum} ", args7);
        this.dao._execute("UPDATE ctb_error_reason cr LEFT JOIN exampaper ep ON cr.examPaperNum=ep.examPaperNum SET cr.studentId={studentId}  ,cr.classNum={classNum}  ,cr.schoolNum={schoolNum}  ,cr.gradeNum={gradeNum}   WHERE cr.studentId={oldStudentId}   AND ep.examNum={examNum} ", args6);
        Student oldstu2 = stu(oldStudentId, examNum);
        Map args8 = new HashMap();
        args8.put("oldStudentId", oldStudentId);
        args8.put(Const.EXPORTREPORT_classNum, oldstu2.getClassNum());
        args8.put(Const.EXPORTREPORT_schoolNum, oldstu2.getSchoolNum());
        args8.put(Const.EXPORTREPORT_gradeNum, oldstu2.getGradeNum());
        args8.put("guid", Long.valueOf(guid2));
        args8.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE ctb_error_reason cr LEFT JOIN exampaper ep ON cr.examPaperNum=ep.examPaperNum SET cr.studentId={oldStudentId}  ,cr.classNum={classNum}  ,cr.schoolNum={schoolNum}  ,cr.gradeNum={gradeNum}   WHERE cr.studentId={guid}   AND ep.examNum={examNum} ", args8);
        Map args9 = new HashMap();
        args9.put(Const.EXPORTREPORT_studentId, studentId);
        args9.put("oldStudentId", oldStudentId);
        args9.put(Const.EXPORTREPORT_examNum, examNum);
        long guid3 = GUID.getGUID();
        Map args10 = new HashMap();
        args10.put("guid", Long.valueOf(guid3));
        args10.put(Const.EXPORTREPORT_studentId, studentId);
        args10.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE ctb_evaluate_log c LEFT JOIN exampaper ep ON c.exampaperNum=ep.exampaperNum SET c.studentId={guid}  WHERE c.studentId={studentId}  AND ep.examNum={examNum} ", args10);
        this.dao._execute("UPDATE ctb_evaluate_log cl LEFT JOIN exampaper ep ON cl.examPaperNum=ep.examPaperNum SET cl.studentId={studentId} ' WHERE cl.studentId={oldStudentId} ' AND ep.examNum={examNum} ", args9);
        Map args11 = new HashMap();
        args11.put("oldStudentId", oldStudentId);
        args11.put("guid", Long.valueOf(guid3));
        args11.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE ctb_evaluate_log cl LEFT JOIN exampaper ep ON cl.examPaperNum=ep.examPaperNum SET cl.studentId={oldStudentId}   WHERE cl.studentId={guid}   AND ep.examNum={examNum} ", args11);
        Map args12 = new HashMap();
        args12.put(Const.EXPORTREPORT_studentId, studentId);
        args12.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args12.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args12.put(Const.EXPORTREPORT_classNum, student.getClassNum());
        args12.put("oldStudentId", oldStudentId);
        args12.put(Const.EXPORTREPORT_examNum, examNum);
        long guid4 = GUID.getGUID();
        Map args13 = new HashMap();
        args13.put("guid", Long.valueOf(guid4));
        args13.put(Const.EXPORTREPORT_studentId, studentId);
        args13.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE ctb_experience c LEFT JOIN exampaper ep ON c.exampaperNum = ep.examPaperNum SET c.studentId={guid}   WHERE c.studentId={studentId}   AND ep.examNum={examNum} ", args13);
        this.dao._execute("UPDATE ctb_experience ce LEFT JOIN exampaper ep ON ce.examPaperNum=ep.examPaperNum SET ce.studentId={studentId}  ,ce.schoolNum={schoolNum}  ,ce.gradeNum={gradeNum}  ,ce.classNum={classNum}   WHERE ce.studentId={oldStudentId}   AND ep.examNum={examNum} ", args12);
        Student oldstu3 = stu(oldStudentId, examNum);
        Map args14 = new HashMap();
        args14.put("oldStudentId", oldStudentId);
        args14.put(Const.EXPORTREPORT_schoolNum, oldstu3.getSchoolNum());
        args14.put(Const.EXPORTREPORT_gradeNum, oldstu3.getGradeNum());
        args14.put(Const.EXPORTREPORT_classNum, oldstu3.getClassNum());
        args14.put(Const.EXPORTREPORT_studentId, studentId);
        args14.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE ctb_experience ce LEFT JOIN exampaper ep ON ce.examPaperNum=ep.examPaperNum SET ce.studentId={oldStudentId}  ,ce.schoolNum={schoolNum}  ,ce.gradeNum={gradeNum}  ,ce.classNum={classNum}   WHERE ce.studentId={studentId}   AND ep.examNum={examNum} ", args14);
        Map args15 = new HashMap();
        args15.put(Const.EXPORTREPORT_studentId, studentId);
        args15.put("oldStudentId", oldStudentId);
        args15.put(Const.EXPORTREPORT_examNum, examNum);
        long guid5 = GUID.getGUID();
        Map args16 = new HashMap();
        args16.put("guid", Long.valueOf(guid5));
        args16.put(Const.EXPORTREPORT_studentId, studentId);
        args16.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE ctb_mastery c LEFT JOIN exampaper ep ON c.exampaperNum = ep.examPaperNum SET c.studentId={guid}   WHERE c.studentId={studentId}   AND ep.examNum={examNum} ", args16);
        this.dao._execute("UPDATE ctb_mastery cm LEFT JOIN exampaper ep ON cm.examPaperNum=ep.examPaperNum SET cm.studentId={studentId}  WHERE cm.studentId={oldStudentId}  AND ep.examNum={examNum} ", args15);
        stu(oldStudentId, examNum);
        Map args17 = new HashMap();
        args17.put("oldStudentId", oldStudentId);
        args17.put("guid", Long.valueOf(guid5));
        args17.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE ctb_mastery cm LEFT JOIN exampaper ep ON cm.examPaperNum=ep.examPaperNum SET cm.studentId={oldStudentId}  WHERE cm.studentId={guid} ' AND ep.examNum={examNum} ", args17);
        Map args18 = new HashMap();
        args18.put(Const.EXPORTREPORT_studentId, studentId);
        args18.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args18.put("examroom", examroom);
        args18.put("examineeNum", examineeNum);
        args18.put(Const.EXPORTREPORT_examNum, examNum);
        long guid6 = GUID.getGUID();
        Map args19 = new HashMap();
        args19.put("guid", Long.valueOf(guid6));
        args19.put(Const.EXPORTREPORT_studentId, studentId);
        args19.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE examineenumerror e LEFT JOIN exampaper ep ON e.exampaperNum = ep.examPaperNum SET e.studentId={guid}   WHERE e.studentId={studentId}   AND ep.examNum={examNum} ", args19);
        this.dao._execute("UPDATE examineenumerror er LEFT JOIN exampaper ep ON er.examPaperNum=ep.examPaperNum SET er.studentId={studentId}  ,er.schoolNum={schoolNum}  ,er.examinationRoomNum={examroom}  ,er.examineeNum={examineeNum}   WHERE er.studentId={examineeNum}   AND ep.examNum={examNum} ", args18);
        Student oldstu4 = stu(oldStudentId, examNum);
        Map args20 = new HashMap();
        args20.put("oldStudentId", oldStudentId);
        args20.put(Const.EXPORTREPORT_schoolNum, oldstu4.getSchoolNum());
        args20.put("examinationRoomNum", oldstu4.getExt2());
        args20.put("examineeNum", oldstu4.getExt3());
        args20.put("guid", Long.valueOf(guid6));
        args20.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE examineenumerror er LEFT JOIN exampaper ep ON er.examPaperNum=ep.examPaperNum SET er.studentId={oldStudentId}  ,er.schoolNum={schoolNum}  ,er.examinationRoomNum={examinationRoomNum}  ,er.examineeNum={examineeNum}   WHERE er.studentId={guid}   AND ep.examNum={examNum} ", args20);
        Map args21 = new HashMap();
        args21.put(Const.EXPORTREPORT_studentId, studentId);
        args21.put(Const.EXPORTREPORT_classNum, student.getClassNum());
        args21.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args21.put("examroom", examroom);
        args21.put("oldStudentId", oldStudentId);
        args21.put(Const.EXPORTREPORT_examNum, examNum);
        long guid7 = GUID.getGUID();
        Map args22 = new HashMap();
        args22.put("guid", Long.valueOf(guid7));
        args22.put(Const.EXPORTREPORT_studentId, studentId);
        args22.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE examlog SET studentId={guid}  WHERE studentId={studentId}and examNum={examNum} ", args22);
        this.dao._execute("UPDATE examlog SET studentId={studentId}  ,classNum={classNum} ,gradeNum={gradeNum}  ,examinationRoomNum={examroom}  WHERE studentId={oldStudentId}  AND examNum={examNum} ", args21);
        Student oldstu5 = stu(oldStudentId, examNum);
        Map args23 = new HashMap();
        args23.put("oldStudentId", oldStudentId);
        args23.put(Const.EXPORTREPORT_classNum, oldstu5.getClassNum());
        args23.put(Const.EXPORTREPORT_gradeNum, oldstu5.getGradeNum());
        args23.put("examinationRoomNum", oldstu5.getExt2());
        args23.put("guid", Long.valueOf(guid7));
        args23.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE examlog SET studentId={oldStudentId} ,classNum={classNum} ,gradeNum={gradeNum} ,examinationRoomNum={examinationRoomNum}  WHERE studentId={guid}  AND examNum={examNum}", args23);
        Map args24 = new HashMap();
        args24.put(Const.EXPORTREPORT_studentId, studentId);
        args24.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args24.put("examroom", examroom);
        args24.put("oldStudentId", oldStudentId);
        args24.put(Const.EXPORTREPORT_examNum, examNum);
        long guid8 = GUID.getGUID();
        Map args25 = new HashMap();
        args25.put("guid", Long.valueOf(guid8));
        args25.put(Const.EXPORTREPORT_studentId, studentId);
        args25.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE illegal ill LEFT JOIN exampaper ep ON ill.exampaperNum=ep.exampaperNum SET ill.studentId={guid}   WHERE ill.studentId={studentId}   AND ep.examNum={examNum} ", args25);
        this.dao._execute("UPDATE illegal ill LEFT JOIN exampaper ep ON ill.examPaperNum=ep.examPaperNum SET ill.studentId={studentId}  ,ill.schoolNum={schoolNum}  ,ill.examinationRoomNum={examroom}   WHERE ill.studentId={oldStudentId}   AND ep.examNum={examNum} ", args24);
        Student oldstu6 = stu(oldStudentId, examNum);
        Map args26 = new HashMap();
        args26.put("oldStudentId", oldStudentId);
        args26.put(Const.EXPORTREPORT_schoolNum, oldstu6.getSchoolNum());
        args26.put("examinationRoomNum", oldstu6.getExt2());
        args26.put("guid", Long.valueOf(guid8));
        args26.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE illegal ill LEFT JOIN exampaper ep ON ill.examPaperNum=ep.examPaperNum SET ill.studentId={oldStudentId}  ,ill.schoolNum={schoolNum}  ,ill.examinationRoomNum={examinationRoomNum}   WHERE ill.studentId={guid}   AND ep.examNum={examNum} ", args26);
        Map args27 = new HashMap();
        args27.put(Const.EXPORTREPORT_studentId, studentId);
        args27.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args27.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args27.put(Const.EXPORTREPORT_classNum, student.getClassNum());
        args27.put("examroom", examroom);
        args27.put("oldStudentId", oldStudentId);
        args27.put(Const.EXPORTREPORT_examNum, examNum);
        long guid9 = GUID.getGUID();
        Map args28 = new HashMap();
        args28.put("guid", Long.valueOf(guid9));
        args28.put(Const.EXPORTREPORT_studentId, studentId);
        args28.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE objectivescore ob LEFT JOIN exampaper ep ON ob.examPaperNum = ep.exampaperNum SET ob.studentId={guid}   WHERE ob.studentId={studentId}   AND ep.examNum={examNum} ", args28);
        this.dao._execute("UPDATE objectivescore ob LEFT JOIN exampaper ep ON ob.examPaperNum=ep.examPaperNum SET ob.studentId={studentId}  ,ob.schoolNum={schoolNum}  ,ob.gradeNum={gradeNum}  ,ob.classNum={classNum}  ,ob.examinationRoomNum={examroom}   WHERE ob.studentId={oldStudentId}   AND ep.examNum={examNum} ", args27);
        Student oldstu7 = stu(oldStudentId, examNum);
        Map args29 = new HashMap();
        args29.put("oldStudentId", oldStudentId);
        args29.put(Const.EXPORTREPORT_schoolNum, oldstu7.getSchoolNum());
        args29.put(Const.EXPORTREPORT_gradeNum, oldstu7.getGradeNum());
        args29.put(Const.EXPORTREPORT_classNum, oldstu7.getClassNum());
        args29.put("examinationRoomNum", oldstu7.getExt2());
        args29.put("guid", Long.valueOf(guid9));
        args29.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE objectivescore ob LEFT JOIN exampaper ep ON ob.examPaperNum=ep.examPaperNum SET ob.studentId={oldStudentId} ',ob.schoolNum={schoolNum} ',ob.gradeNum={gradeNum} ',ob.classNum={classNum} ',ob.examinationRoomNum={examinationRoomNum} ' WHERE ob.studentId={guid} ' AND ep.examNum={examNum} ", args29);
        Map args30 = new HashMap();
        args30.put(Const.EXPORTREPORT_studentId, studentId);
        args30.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args30.put(Const.EXPORTREPORT_classNum, student.getClassNum());
        args30.put("examroom", examroom);
        args30.put("oldStudentId", oldStudentId);
        args30.put(Const.EXPORTREPORT_examNum, examNum);
        long guid10 = GUID.getGUID();
        Map args31 = new HashMap();
        args31.put("guid", Long.valueOf(guid10));
        args31.put(Const.EXPORTREPORT_studentId, studentId);
        args31.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE regexaminee reg LEFT JOIN exampaper ep ON reg.examPaperNum = ep.examPaperNum SET reg.studentId={guid}   WHERE reg.studentId={studentId}   AND ep.examNum={examNum} ", args31);
        this.dao._execute("UPDATE regexaminee reg LEFT JOIN exampaper ep ON reg.exampaperNum=ep.examPaperNum SET reg.studentId={studentId}  ,reg.schoolNum={schoolNum}  ,reg.classNum={classNum}  ,reg.examinationRoomNum={examroom}   WHERE reg.studentId={oldStudentId}   AND ep.examNum={examNum}  ", args30);
        Student oldstu8 = stu(oldStudentId, examNum);
        Map args32 = new HashMap();
        args32.put("oldStudentId", oldStudentId);
        args32.put(Const.EXPORTREPORT_schoolNum, oldstu8.getSchoolNum());
        args32.put(Const.EXPORTREPORT_classNum, oldstu8.getClassNum());
        args32.put("examinationRoomNum", oldstu8.getExt2());
        args32.put("guid", Long.valueOf(guid10));
        args32.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE regexaminee reg LEFT JOIN exampaper ep ON reg.exampaperNum=ep.examPaperNum SET reg.studentId={oldStudentId}  ,reg.schoolNum={schoolNum}  ,reg.classNum={classNum}  ,reg.examinationRoomNum={examinationRoomNum}   WHERE reg.studentId={guid}   AND ep.examNum={examNum} ", args32);
        Map args33 = new HashMap();
        args33.put(Const.EXPORTREPORT_studentId, studentId);
        args33.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args33.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args33.put(Const.EXPORTREPORT_classNum, student.getClassNum());
        args33.put("examroom", examroom);
        args33.put("oldStudentId", oldStudentId);
        args33.put(Const.EXPORTREPORT_examNum, examNum);
        long guid11 = GUID.getGUID();
        Map args34 = new HashMap();
        args34.put("guid", Long.valueOf(guid11));
        args34.put(Const.EXPORTREPORT_studentId, studentId);
        args34.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE score s LEFT JOIN exampaper ep ON s.examPaperNum = ep.exampaperNum SET s.studentId={guid}  WHERE s.studentId={studentId}  AND ep.examNum={examNum} ", args34);
        this.dao._execute("UPDATE score s LEFT JOIN exampaper ep ON s.examPaperNum=ep.examPaperNum SET s.studentId={studentId}  ,s.schoolNum={schoolNum}  ,s.gradeNum={gradeNum}  ,s.classNum={classNum}  ,s.examinationRoomNum={examroom}   WHERE s.studentId={oldStudentId}   AND ep.examNum={examNum} ", args33);
        Student oldstu9 = stu(oldStudentId, examNum);
        Map args35 = new HashMap();
        args35.put("oldStudentId", oldStudentId);
        args35.put(Const.EXPORTREPORT_schoolNum, oldstu9.getSchoolNum());
        args35.put(Const.EXPORTREPORT_gradeNum, oldstu9.getGradeNum());
        args35.put(Const.EXPORTREPORT_classNum, oldstu9.getClassNum());
        args35.put("examinationRoomNum", oldstu9.getExt2());
        args35.put("guid", Long.valueOf(guid11));
        args35.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("UPDATE score s LEFT JOIN exampaper ep ON s.examPaperNum=ep.examPaperNum SET s.studentId={oldStudentId}  ,s.schoolNum={schoolNum}  ,s.gradeNum={gradeNum}  ,s.classNum={classNum}  ,s.examinationRoomNum={examinationRoomNum}   WHERE s.studentId={guid}   AND ep.examNum={examNum}", args35);
    }

    public Student stu(String studentId, String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return (Student) this.dao._queryBean("SELECT st.schoolNum schoolNum,st.gradeNum gradeNum,st.classNum classNum,st.jie jie ,cl.studentType ext1,ex.examinationRoomNum ext2,ex.examineeNum ext3 FROM student st LEFT JOIN class cl ON  st.classNum=cl.id  LEFT JOIN examinationnum ex ON ex.studentId=st.id WHERE st.id={studentId}  AND ex.examNum={examNum} ", Student.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getAllSchoolByExamNum(String examNum, String schoolNum) {
        return this.examManageDAO.getAllSchoolByExamNum(examNum, schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getSjtByNum(String subjectNum) {
        return this.examManageDAO.getSjtByNum(subjectNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getClassById(String classNum, String levelclass, String subjectNum) {
        return this.examManageDAO.getClassById(classNum, levelclass, subjectNum);
    }

    public String getCellValue(XSSFCell cell, int s, int y) {
        if (cell == null) {
            throw new RuntimeException("第 " + (y + 1) + " 行 " + (s + 1) + " 列单元格为空！");
        }
        String c = cell.toString();
        if (c.trim().equals("")) {
            throw new RuntimeException("第 " + (cell.getRowIndex() + 1) + " 行 " + (cell.getColumnIndex() + 1) + " 列单元格为空！");
        }
        String returnv = "";
        if (cell.getCellType() == CellType.NUMERIC) {
            returnv = this.df.format(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            returnv = cell.getStringCellValue().trim();
        }
        return returnv;
    }

    public String getCellValue(HSSFCell cell, int s, int y) {
        if (cell == null) {
            throw new RuntimeException("第 " + (y + 1) + " 行 " + (s + 1) + " 列单元格为空！");
        }
        String c = cell.toString();
        if (c.trim().equals("")) {
            throw new RuntimeException("第 " + (cell.getRowIndex() + 1) + " 行 " + (cell.getColumnIndex() + 1) + " 列单元格为空！");
        }
        String returnv = "";
        if (cell.getCellType() == CellType.NUMERIC) {
            returnv = this.df.format(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            returnv = cell.getStringCellValue().trim();
        }
        return returnv;
    }

    public String getCellValue(Cell cell, int s, int y, int i) {
        if (cell == null) {
            throw new RuntimeException("第 " + (i + 1) + " 个sheet第 " + (y + 1) + " 行 " + (s + 1) + " 列单元格为空！");
        }
        String c = cell.toString();
        if (c.trim().equals("")) {
            throw new RuntimeException("第 " + (i + 1) + " 个sheet第 " + (cell.getRowIndex() + 1) + " 行 " + (cell.getColumnIndex() + 1) + " 列单元格为空！");
        }
        String returnv = "";
        if (cell.getCellType() == CellType.NUMERIC) {
            returnv = this.df.format(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            returnv = cell.getStringCellValue().trim();
        }
        return returnv;
    }

    private void throwException(String e) {
        throw new SubException(e);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void addOneExamsetting(int examNum, Object beforeExamNum, String user, String date) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, Integer.valueOf(examNum));
        args.put("beforeExamNum", beforeExamNum);
        Object _id = this.dao._queryObject("SELECT id FROM examsetting WHERE examNum={examNum} limit 1", args);
        if (null == _id) {
            Examsetting beforeES = (Examsetting) this.dao._queryBean("select * from examsetting where examNum={beforeExamNum}", Examsetting.class, args);
            if (null != beforeES) {
                beforeES.setExamNum(Integer.valueOf(examNum));
                beforeES.setInsertUser(user);
                beforeES.setInsertDate(date);
            } else {
                beforeES = new Examsetting();
                beforeES.setExamNum(Integer.valueOf(examNum));
                beforeES.setInsertUser(user);
                beforeES.setInsertDate(date);
                beforeES.setLevera(0.9f);
                beforeES.setLeverb(0.8f);
                beforeES.setLeverc(0.7f);
                beforeES.setLeverd(0.6f);
                beforeES.setLevere(0.0f);
                beforeES.setHighScore(0.9f);
                beforeES.setExcellence(0.8f);
                beforeES.setWellrate(0.7f);
                beforeES.setLowScore(0.3f);
                beforeES.setMissingExam("0");
                beforeES.setDiscipline("0");
                beforeES.setLingfe("0");
                beforeES.setZonfen("1");
            }
            this.dao._execute("INSERT INTO examsetting(examNum,levera,leverb,leverc,leverd,levere,highScore,excellence,wellrate,lowScore,RSRw_highScore,RSRw_excellence,RSRw_wellrate,RSRw_lowScore,RSRw_average,missingExam,discipline,lingfe,zonfen,insertUser,insertDate) VALUES({examNum},{levera},{leverb},{leverc},{leverd},{levere},{highScore},{excellence},{wellrate},{lowScore},{RSRw_highScore},{RSRw_excellence},{RSRw_wellrate},{RSRw_lowScore},{RSRw_average},{missingExam},{discipline},{lingfe},{zonfen},{insertUser},{insertDate})", beforeES);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void addOneSourcesetting(int examNum, Object beforeExamNum, String user, String date) {
        List<?> queryBeanList = this.dao.queryBeanList("select * from data where type=26 and isDelete='F' ", Data.class);
        List essList = new ArrayList();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, Integer.valueOf(examNum));
        args.put("beforeExamNum", beforeExamNum);
        Object _id = this.dao._queryObject("SELECT id FROM examsourceset WHERE examNum={examNum} limit 1", args);
        if (null == _id) {
            essList = this.dao._queryBeanList("SELECT * FROM examsourceset WHERE examNum={beforeExamNum}", ExamSourceSet.class, args);
        }
        List<Map> mapList = new ArrayList<>();
        for (int i = 0; i < queryBeanList.size(); i++) {
            String source = ((Data) queryBeanList.get(i)).getValue();
            Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) Integer.valueOf(examNum)).put("source", (Object) source).put("insertUser", (Object) user).put("insertDate", (Object) date);
            if (null != _id) {
                args2.put("isJoin", "0");
            } else {
                Optional<ExamSourceSet> res = essList.stream().filter(m -> {
                    return source.equals(m.getSource());
                }).findAny();
                if (res.isPresent()) {
                    args2.put("isJoin", res.get().getIsJoin());
                } else {
                    args2.put("isJoin", "0");
                }
            }
            mapList.add(args2);
        }
        this.dao._batchExecute("INSERT INTO examsourceset(examNum,source,isJoin,insertUser,insertdate) VALUES ({examNum},{source},{isJoin},{insertUser},{insertDate}) ON DUPLICATE KEY UPDATE updateUser={insertUser} ,updateDate={insertDate}", mapList);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer examsetcount(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryInt("SELECT count(1) FROM examsetting WHERE examNum={examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void delexamset(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        this.dao._execute("DELETE FROM examsetting WHERE examNum={examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getSchools() {
        return this.examManageDAO.getSchools();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getGrades(String schoolNum) {
        return this.examManageDAO.getGrades(schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getsubjects(String examNum, String gradeNum) {
        return this.examManageDAO.getsubjects(examNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getClasses(String schoolNum, String examNum, String gradeNum, String subjectNum, String examPaperNum) {
        return this.examManageDAO.getClasses(schoolNum, examNum, gradeNum, subjectNum, examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Define> getExcelFileNameByNum(String examNum, String gradeNum, String subjectNum) {
        return this.examManageDAO.getExcelFileNameByNum(examNum, gradeNum, subjectNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object getGradeNum(String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao._queryObject("select DISTINCT gradeName from grade where gradeNum={gradeNum}", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object getSubjectNum(String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao._queryObject("select subjectName FROM `subject` WHERE subjectNum={subjectNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void addMarkError(String scoreId, Integer exampaperNum, String questionNum, Double questionScore, String type, String insertUser, String insertDate) {
        this.examManageDAO.addMarkError(scoreId, exampaperNum, questionNum, questionScore, type, insertUser, insertDate);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getMarkError(String exampaperNum, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum, String examinationRoomNum, String scoreId) {
        return this.examManageDAO.getMarkError(exampaperNum, questionNum, studentId, schoolNum, classNum, gradeNum, examinationRoomNum, scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getpageNum(String examPaperNum, String questionNum) {
        return this.examManageDAO.getpageNum(examPaperNum, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getLeakFromScore_modified(String examNum, String subjectNum, String gradeNum, String testCenter, String roomNum) {
        return this.examManageDAO.getLeakFromScore_modified(examNum, subjectNum, gradeNum, testCenter, roomNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getExpQuestionImg_info_modified(String schoolNum, String gradeNum, String examPaperNum, String questionNum, String examroomNum, String expMark, String examNum) {
        return this.examManageDAO.getExpQuestionImg_info_modified(schoolNum, gradeNum, examPaperNum, questionNum, examroomNum, expMark, examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Exam getexamBean(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return (Exam) this.dao._queryBean("SELECT examName,examinationRoomLength,examineeLength FROM exam WHERE examnum={examNum} ", Exam.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Grade> getgradeNumByexam(String exam) {
        Map args = StreamMap.create().put("exam", (Object) exam);
        return this.dao._queryBeanList("SELECT DISTINCT \tgr.gradeNum gradeNum,\tgr.gradeName gradeName FROM exampaper expa LEFT JOIN basegrade gr ON gr.gradeNum = expa.gradeNum AND expa.examNum = {exam}  WHERE expa.isHidden='F' and gr.gradeNum IS NOT NULL ORDER BY \tgr.gradeNum", Grade.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<ExamSourceSet> getSourceSet(String exam) {
        Map args = StreamMap.create().put("exam", (Object) exam);
        return this.dao._queryBeanList("SELECT d.name ext1,d.value ext2,et.source,et.examNum,et.isJoin from data d  LEFT JOIN examsourceset et on et.source=d.value and et.examNum={exam}   where  d.type=26 and d.isDelete='F' ", ExamSourceSet.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Subject> getSubByExamGrade(String exam, int grade) {
        Map args = StreamMap.create().put("grade", (Object) Integer.valueOf(grade)).put("exam", (Object) exam);
        return this.dao._queryBeanList("SELECT DISTINCT su.subjectNum subjectNum,su.subjectName subjectName, gr.gradeNum gradeNum,expa.examPaperNum ,IFNULL(expa.type,'') type,expa.status status,expa.abtype ext1,expa.templateType ext2,expam.`value` ext3,expa.multipagestuinfo ext4,expa.type ext5,expa.paintMode ext6,expa.fenzuyuejuan ext7 FROM exampaper expa  LEFT JOIN `subject` su ON su.subjectNum = expa.subjectNum  LEFT JOIN exampaperparameter expam ON expa.examPaperNum=expam.examPaperNum  LEFT JOIN grade gr ON expa.gradeNum = gr.gradeNum  WHERE gr.gradeNum={grade}  and expa.examNum = {exam}  and expa.isHidden='F' ORDER BY su.id", Subject.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Subject> getSubByExamGrade_SetOE(String exam, int grade) {
        Map args = StreamMap.create().put("grade", (Object) Integer.valueOf(grade)).put("exam", (Object) exam);
        return this.dao._queryBeanList("SELECT DISTINCT su.subjectNum subjectNum,su.subjectName subjectName, gr.gradeNum gradeNum,expa.examPaperNum ,IFNULL(expa.type,'') type,expa.status status,expa.abtype ext1,expa.templateType ext2,expam.`value` ext3,expa.multipagestuinfo ext4,expa.type ext5 FROM exampaper expa  LEFT JOIN `subject` su ON su.subjectNum = expa.subjectNum  LEFT JOIN exampaperparameter expam ON expa.examPaperNum=expam.examPaperNum  LEFT JOIN grade gr ON expa.gradeNum = gr.gradeNum  WHERE gr.gradeNum={grade}  and expa.examNum = {exam}  and expa.isHidden='F' and expa.type='3' ORDER BY su.id", Subject.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, Object> getOEByExampaperNum(String exampaperNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao._querySimpleMap("select examPaperNum,DATE_FORMAT(startTime,'%Y-%d-%m %h:%i') startTime,DATE_FORMAT(overTime,'%Y-%d-%m %h:%i') overTime,examMinute,TkzujuanNum,TkzujuanName from onlineexamsetting where examPaperNum = {exampaperNum}  AND isDelete='F'", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int insertOE(String examPaperNum, String insertUser) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("insertUser", (Object) insertUser);
        return this.dao._execute("INSERT into onLineExamSetting (id,examPaperNum,examStatus,isDelete,insertUser,insertDate,TKzujuanNum,TkzujuanName) value(UUID_SHORT(),{examPaperNum},'1','F',{insertUser},NOW(),'000000','请选择考卷')", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int updateOE(String exanpaperNum, String value, int tag) {
        String sql = "update onLineExamSetting set ";
        Map args = new HashMap();
        if (tag == 1) {
            sql = sql + " startTime={value} ";
            args.put("value", value);
        } else if (tag == 2) {
            sql = sql + " overTime={value} ";
            args.put("value", value);
        } else if (tag == 3) {
            sql = sql + " examMinute={value} ";
            args.put("value", value);
        } else if (tag == 4) {
            sql = sql + " TKzujuanNum={value}";
            args.put("value", value);
        } else if (tag == 5) {
            String[] values = value.split(Const.STRING_SEPERATOR);
            sql = sql + " TKzujuanNum={TKzujuanNum} ,TkzujuanName={TkzujuanName} ";
            args.put("TKzujuanNum", values[0]);
            args.put("TkzujuanName", values[1]);
        } else if (tag == 6) {
            sql = sql + " exampaperPath={value} ";
            args.put("value", value);
        } else if (tag == 7) {
            sql = sql + " exampaperDetailPath={value} ";
            args.put("value", value);
        }
        String sql2 = sql + " where examPaperNum = {exanpaperNum} ";
        args.put("exanpaperNum", exanpaperNum);
        return this.dao._execute(sql2, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int setOEDefine(List<Map<String, String>> defineList, String examPaperNum, String insertUser) {
        if (defineList.size() < 1) {
            return 0;
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        this.dao._execute("delete  from define where examPaperNum = {examPaperNum}", args);
        this.dao._execute("delete  from knowdetail where examPaperNum = {examPaperNum} ", args);
        this.dao._execute("delete  from abilitydetail where examPaperNum = {examPaperNum} ", args);
        String[] fields = {"orderNum", "examPaperNum", "questionNum", "fullScore", "questionType", "Answer", "difficult", "inspectionlevel", "one1", "one2", "one3", "one4", "one5", "one6", "one7", "one8", "one9", "one10", "one11", "one12", "one13", "one14", "one15", "one16", "one17", "one18", "one19", "one20", "one21", "one22", "one23", "one24", "one25", "one26", "one1b", "one2b", "one3b", "one4b", "one5b", "one6b", "one7b", "one8b", "one9b", "one10b", "one11b", "one12b", "one13b", "one14b", "one15b", "one16b", "one17b", "one18b", "one19b", "one20b", "one21b", "one22b", "one23b", "one24b", "one25b", "one26b", "multiple", "errorRate", "hasErrorSection", "lengout", "category", "optionCount", "choosename", "deduction", "page", "cross_page", "mn", "merge", "isDelete", "insertUser", "insertDate", "isParent"};
        List<Object[]> argList = new ArrayList<>();
        BigDecimal totalScore = new BigDecimal("0");
        for (Map<String, String> oneDefine : defineList) {
            String[] duoxuanscore = new String[26];
            duoxuanscore[0] = "0";
            duoxuanscore[1] = "0";
            duoxuanscore[2] = "0";
            duoxuanscore[3] = "0";
            duoxuanscore[4] = "0";
            duoxuanscore[5] = "0";
            duoxuanscore[6] = "0";
            duoxuanscore[7] = "0";
            duoxuanscore[8] = "0";
            duoxuanscore[9] = "0";
            duoxuanscore[10] = "0";
            duoxuanscore[11] = "0";
            duoxuanscore[12] = "0";
            duoxuanscore[13] = "0";
            duoxuanscore[14] = "0";
            duoxuanscore[15] = "0";
            duoxuanscore[16] = "0";
            duoxuanscore[17] = "0";
            duoxuanscore[18] = "0";
            duoxuanscore[19] = "0";
            duoxuanscore[20] = "0";
            duoxuanscore[21] = "0";
            duoxuanscore[22] = "0";
            duoxuanscore[23] = "0";
            duoxuanscore[24] = "0";
            duoxuanscore[25] = "0";
            String tkAnswer = oneDefine.get("tkAnswer").replaceAll(Const.STRING_SEPERATOR, "");
            String multiple = "0";
            if (tkAnswer.length() > 2) {
                multiple = "1";
            }
            duoxuanscore[tkAnswer.length() - 1] = String.valueOf(oneDefine.get("tkscore"));
            Object[] rowArg = {oneDefine.get("tkQuestionNumber"), examPaperNum, oneDefine.get("tkQuestionNum"), oneDefine.get("tkscore"), '0', oneDefine.get("tkAnswer"), '0', '0', duoxuanscore[0], duoxuanscore[1], duoxuanscore[2], duoxuanscore[3], duoxuanscore[4], duoxuanscore[5], duoxuanscore[6], duoxuanscore[7], duoxuanscore[8], duoxuanscore[9], duoxuanscore[10], duoxuanscore[11], duoxuanscore[12], duoxuanscore[13], duoxuanscore[14], duoxuanscore[15], duoxuanscore[16], duoxuanscore[17], duoxuanscore[18], duoxuanscore[19], duoxuanscore[20], duoxuanscore[21], duoxuanscore[22], duoxuanscore[23], duoxuanscore[24], duoxuanscore[25], '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', multiple, String.valueOf(oneDefine.get("tkscore")), '0', '0', examPaperNum, String.valueOf(oneDefine.get("tkAnswerSpace")), 's', '0', '1', 'F', '1', '0', 'F', insertUser, MonthDay.now(), '0'};
            argList.add(rowArg);
            BigDecimal fullScore = new BigDecimal(String.valueOf(oneDefine.get("tkscore")));
            totalScore = totalScore.add(fullScore);
        }
        this.dao.batchInsert("define", fields, argList);
        StringBuffer epSql = new StringBuffer();
        epSql.append("update exampaper set totalScore={totalScore} where examPaperNum={examPaperNum} ");
        BigDecimal finalTotalScore = totalScore;
        Map args3 = StreamMap.create().put("totalScore", (Object) finalTotalScore).put("examPaperNum", (Object) examPaperNum);
        this.dao._execute(epSql.toString(), args3);
        String[] knoSqlfields = {"konwNum", "questionNum", "examPaperNum", "insertUser", "insertDate"};
        String[] anlSqlfields = {"abilityNum", "questionNum", "examPaperNum", "insertUser", "insertDate"};
        Map args4 = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        List<Map<String, ?>> _queryMapList = this.dao._queryMapList("select id,questionNum from define where examPaperNum  = {examPaperNum} ORDER BY questionNum", TypeEnum.StringString, args4);
        List<Object[]> argList2 = new ArrayList<>();
        List<Object[]> argList3 = new ArrayList<>();
        for (Map<String, ?> map : _queryMapList) {
            for (Map<String, String> oneDefine2 : defineList) {
                if (((String) map.get("questionNum")).equals(String.valueOf(oneDefine2.get("tkQuestionNum")))) {
                    Object[] objArr = {String.valueOf(oneDefine2.get("tkknowledgeNum")), String.valueOf(map.get("id")), examPaperNum, insertUser, MonthDay.now()};
                    argList2.add(knoSqlfields);
                    Object[] anlSqlrowArg = {String.valueOf(oneDefine2.get("tkabilityNum")), String.valueOf(map.get("id")), examPaperNum, insertUser, MonthDay.now()};
                    argList3.add(anlSqlrowArg);
                }
            }
        }
        this.dao.batchInsert("knowdetail", knoSqlfields, argList2);
        this.dao.batchInsert("abilitydetail", anlSqlfields, argList3);
        return 0;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getSubByExamGrade2(String exam, int grade) {
        Map args = StreamMap.create().put("grade", (Object) Integer.valueOf(grade)).put("exam", (Object) exam);
        return this.dao._queryMapList("SELECT DISTINCT su.subjectNum subjectNum,su.subjectName subjectName, gr.gradeNum gradeNum,expa.examPaperNum ,IFNULL(expa.type,'') type,expa.status status,expa.abtype ext1,expa.templateType ext2,expam.`value` ext3,expa.multipagestuinfo ext4,expa.type ext5,if(max(stl.insertDate) is null,'',max(stl.insertDate)) insertDate,if(expa.appealDate is null,'',expa.appealDate) appealDate,if(expa.appealDealDate is null,'',expa.appealDealDate) appealDealDate,expa.jisuanType  FROM exampaper expa  LEFT JOIN `subject` su ON su.subjectNum = expa.subjectNum  LEFT JOIN exampaperparameter expam ON expa.examPaperNum=expam.examPaperNum  LEFT JOIN grade gr ON expa.gradeNum = gr.gradeNum LEFT JOIN (SELECT exampaperNum, insertDate from gradelevel where gradeNum={grade} and examNum ={exam}  GROUP BY exampaperNum) stl on stl.exampaperNum = expa.examPaperNum  WHERE gr.gradeNum={grade}  and expa.examNum = {exam}  and expa.isHidden='F' GROUP BY expa.exampaperNum ORDER BY su.id", null, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int insertAstrict(String exam, int grade, String userId, String scoreReleased) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) Integer.valueOf(grade));
        String count = this.dao._queryStr("SELECT count(1) from astrict where examNum={exam} and gradeNum={grade}  and partType=5", args);
        int aa = 0;
        if ("0".equals(count)) {
            Map args1 = StreamMap.create().put("grade", (Object) Integer.valueOf(grade)).put("exam", (Object) exam).put(Const.SCORE_RELEASED, (Object) scoreReleased).put("userId", (Object) userId);
            aa = this.dao._execute("insert into astrict(gradeNum,examNum,partType,userType,status,insertUser,insertDate) VALUES ({grade},{exam},5,1,{scoreReleased},{userId},NOW()),({grade},{exam},5,23,{scoreReleased},{userId},NOW())", args1);
        }
        return aa;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getStatus(String exam, String subject, String grade) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade).put("subject", (Object) subject);
        return this.dao._queryStr("SELECT status FROM exampaper WHERE examNum={exam}  AND gradeNum={grade} AND subjectNum={subject} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getListByNum(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryStr("SELECT examName FROM exam WHERE examNum={examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List parentExamPaperNum(String examNum, String subjectNum, String gradeNum) {
        return this.examManageDAO.parentExamPaperNum(examNum, subjectNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, String> getschNumByExam(String exam) {
        Map args = StreamMap.create().put("exam", (Object) exam);
        return this.dao._queryOrderMap("select DISTINCT sch.id schoolNum,sch.shortname schoolName from school sch \tLEFT JOIN grade gra on sch.id=gra.schoolNum\tLEFT JOIN exampaper exam on gra.gradeNum=exam.gradeNum\twhere exam.examNum={exam} ", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Class> getclassBygraSchExam(String exam, int grade, String school, String stuType) {
        String stuTypeStr = "";
        if (!stuType.equals("F")) {
            stuTypeStr = " AND cl.studentType={stuType} ";
        }
        String sql = "SELECT DISTINCT clex.classNum classNum , cl.className className,cl.gradeNum gradeNum  FROM classexam clex LEFT JOIN exampaper ex ON clex.examPaperNum=ex.examPaperNum AND examnum='" + exam + "' LEFT JOIN class cl ON cl.id=clex.classNum   LEFT JOIN groupclass grcl ON grcl.examNum=ex.examNum  AND grcl.classNum=cl.id WHERE ex.examNum={exam}   AND cl.gradeNum={grade}   AND clex.schoolNum={school}   AND grcl.classNum is  NULL " + stuTypeStr + " ORDER BY clex.classNum*1";
        Map args = StreamMap.create().put("stuType", (Object) stuType).put("exam", (Object) exam).put("grade", (Object) Integer.valueOf(grade)).put(License.SCHOOL, (Object) school);
        return this.dao._queryBeanList(sql, Class.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getSchoolNameByNum(String school) {
        Map args = StreamMap.create().put(License.SCHOOL, (Object) school);
        return this.dao._queryStr("SELECT schoolName,id FROM school WHERE id={school} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getTestCenterNameByNum(String testCenterId) {
        Map args = new HashMap();
        args.put("testCenterId", testCenterId);
        return this.dao._queryStr("SELECT testingCentreName FROM testingcentre WHERE id={testCenterId}", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getShortNameByNum(String schoolId) {
        Map args = StreamMap.create().put("schoolId", (Object) schoolId);
        return this.dao._queryStr("SELECT shortname FROM school WHERE id={schoolId} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<ExamineeNumGroup> getGroupList(String exam, String school, int grade) {
        Map args = StreamMap.create().put("exam", (Object) exam).put(License.SCHOOL, (Object) school).put("grade", (Object) Integer.valueOf(grade));
        return this.dao._queryBeanList("SELECT DISTINCT exgr.groupNum groupNum,exgr.groupName groupName,exgr.gradeNum gradeNum ,gr.gradeName gradeName  FROM examineenumgroup exgr LEFT JOIN grade gr ON exgr.gradeNum=gr.gradeNum WHERE exgr.examNum={exam}   AND exgr.schoolNum={school}   AND exgr.gradeNum={grade} ", ExamineeNumGroup.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void submitAddGroup(String exam, String school, String grade, String[] classNums, String groupNum, String jie, String user, String date, String type, String groupName) {
        if (type.equals("1")) {
            long group = GUID.getGUID();
            Map args = new HashMap();
            args.put("exam", exam);
            args.put(License.SCHOOL, school);
            args.put("group", Long.valueOf(group));
            args.put("groupName", groupName);
            args.put("grade", grade);
            args.put("user", user);
            args.put("date", date);
            for (String classNum : classNums) {
                Map args2 = StreamMap.create().put("exam", (Object) exam).put("group", (Object) Long.valueOf(group)).put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie).put("user", (Object) user).put("date", (Object) date);
                this.dao._execute("INSERT INTO groupclass(examNum,groupNum,schoolNum,gradeNum,classNum,jie,insertUser,insertDate) VALUES({exam},{group},{school},{grade},{classNum},{jie},{user},{date} ) ON DUPLICATE KEY UPDATE examNum={exam}  ,schoolNum={school}  ,groupNum={group}  ,gradeNum={grade}  ,classNum={classNum}  ,jie={jie}", args2);
            }
            this.dao._execute("INSERT INTO examineenumgroup(examNum,schoolNum,groupNum,groupName,gradeNum,insertUser,insertDate) VALUES({exam},{school},{group},{groupName},{grade},{user},{date}) ON DUPLICATE KEY UPDATE examNum={exam},schoolNum={school},gradeNum={grade} ", args);
            return;
        }
        for (String classNum2 : classNums) {
            Map args22 = StreamMap.create().put("exam", (Object) exam).put("groupNum", (Object) groupNum).put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put(Const.EXPORTREPORT_classNum, (Object) classNum2).put("jie", (Object) jie).put("user", (Object) user).put("date", (Object) date);
            this.dao._execute("INSERT INTO groupclass(examNum,groupNum,schoolNum,gradeNum,classNum,jie,insertUser,insertDate) VALUES({exam},{groupNum},{school},{grade},{classNum},{jie},{user},{date} ) ON DUPLICATE KEY UPDATE examNum={exam}  ,schoolNum={school}  ,groupNum={groupNum}  ,gradeNum={grade}  ,classNum={classNum}  ,jie={jie} ", args22);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer namecount(String exam, String school, String grade, String groupName) {
        Map args = StreamMap.create().put("exam", (Object) exam).put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put("groupName", (Object) groupName);
        return this.dao._queryInt("SELECT count(1) FROM examineenumgroup WHERE examNum={exam}  AND schoolNum={school}  AND gradeNum={grade}  AND groupName={groupName} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<GroupClass> getgroupClassList(String exam, int grade, String school, String groupNum) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) Integer.valueOf(grade)).put(License.SCHOOL, (Object) school).put("groupNum", (Object) groupNum);
        return this.dao._queryBeanList("SELECT cl.className className,grcl.classNum classNum,grcl.groupNum groupNum,grcl.gradeNum gradeNum,gr.gradeName gradeName FROM groupclass grcl LEFT JOIN examineenumgroup exgr ON grcl.examNum=exgr.examNum  AND grcl.schoolNum=exgr.schoolNum AND grcl.gradeNum=exgr.gradeNum AND grcl.groupNum=exgr.groupNum LEFT JOIN class cl ON cl.id=grcl.classNum  LEFT JOIN grade gr ON gr.gradeNum=grcl.gradeNum AND gr.schoolNum=grcl.schoolNum WHERE grcl.examNum={exam}   AND grcl.gradeNum={grade}   AND grcl.schoolNum={school}  AND grcl.groupNum={groupNum}   order by cl.classNum*1", GroupClass.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void delgroup(String groupNum) {
        Map args = StreamMap.create().put("groupNum", (Object) groupNum);
        this.dao._execute("DELETE FROM examineenumgroup WHERE groupNum={groupNum} ", args);
        this.dao._execute("DELETE FROM groupclass WHERE groupNum={groupNum}", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void delgroupCla(String exam, String grade, String school, String group, String classNum) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("group", (Object) group).put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        this.dao._execute("DELETE FROM groupclass WHERE examNum={exam}  AND groupNum={group}  AND schoolNum={school}  AND gradeNum={grade}  AND classNum={classNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getGradeNameByNum(String school, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(License.SCHOOL, (Object) school);
        return this.dao._queryStr("SELECT gradeName FROM grade WHERE gradeNum={gradeNum}  AND schoolNum={school}", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getGradeNameByGradeNum(String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao._queryStr("SELECT DISTINCT gradeName FROM basegrade WHERE gradeNum={gradeNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<ExamineeNumGroup> getexamineeGroupList(String exam, String school, String grade) {
        Map args = StreamMap.create().put("exam", (Object) exam).put(License.SCHOOL, (Object) school).put("grade", (Object) grade);
        return this.dao._queryBeanList("SELECT groupNum,groupName FROM examineenumgroup WHERE examNum={exam}   AND schoolNum={school}   AND gradeNum={grade} ", ExamineeNumGroup.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void submitAddToGroup(String exam, String school, String grade, String classNum, String groupNum, String jie, String user, String date) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("groupNum", (Object) groupNum).put(License.SCHOOL, (Object) school).put("grade", (Object) grade).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("jie", (Object) jie).put("user", (Object) user).put("date", (Object) date);
        this.dao._execute("INSERT INTO groupclass(examNum,groupNum,schoolNum,gradeNum,classNum,jie,insertUser,insertDate) VALUES({exam},{groupNum},{school},{grade},{classNum},{jie},{user},{date}  ) ON DUPLICATE KEY UPDATE examNum={exam}  ,schoolNum={school}  ,groupNum={groupNum}  ,gradeNum={grade}  ,classNum={classNum}  ,jie={jie} ", args);
    }

    public void insertTestingcentreDis(String examNum, String gradeNum, String subjectNum, String testingCentreId, String user, String function) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        String exampaperNum = this.dao._queryStr("select exampaperNum from exampaper where examNum={examNum}  and gradeNum={gradeNum}  and subjectNum={subjectNum} ", args);
        Map args2 = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("testingCentreId", (Object) testingCentreId);
        String isExist = this.dao._queryStr("select id from testingcentreDis where examPaperNum={exampaperNum} and testingCentreId={testingCentreId} ", args2);
        if (null == isExist || "" == isExist || "null" == isExist) {
            this.log.info("----" + function + "----" + examNum + "----" + gradeNum + "----" + subjectNum + "-----开发阅卷的考点：" + testingCentreId);
            ServletContext context = ServletActionContext.getServletContext();
            new Conffig();
            String filePath = context.getRealPath("/");
            String testingcentrediss = Conffig.getParameter(filePath, Configuration.getInstance().getTestingcentredis());
            Map args3 = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("testingCentreId", (Object) testingCentreId).put("testingcentrediss", (Object) testingcentrediss).put("user", (Object) user);
            this.dao._execute("insert into testingcentreDis (examPaperNum,testingCentreId,isDis,insertUser,insertDate) values({exampaperNum},{testingCentreId},{testingcentrediss},{user},now())", args3);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void divideExamRoomByGradeNoGroup(String exam, String stuType, int grade, List<Class> classListt, String user, String roomCount, String elength, String idornum, JSONArray examinationJsonArray, JSONArray jsonArray, String classType) {
        DecimalFormat examroomDf = new DecimalFormat(creatStr(3, '0'));
        new DecimalFormat(creatStr(-1, '0'));
        Set<String> set1 = new HashSet<>();
        int stuNo = 1;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject examDataJsonObject = (JSONObject) jsonArray.get(i);
            String classString = examDataJsonObject.get("classStr").toString();
            String gradeStr = examDataJsonObject.get("gradeStr").toString();
            String schoolStr = examDataJsonObject.get("schoolStr").toString();
            String subjectStr = examDataJsonObject.get("subjectStr").toString();
            String classtypeStr = examDataJsonObject.get("classtypeStr").toString();
            String testingCentreId = examDataJsonObject.get("testingCentreStr").toString();
            Map args = StreamMap.create().put("classString", (Object) classString).put("exam", (Object) exam).put("schoolStr", (Object) schoolStr).put("gradeStr", (Object) gradeStr).put("stuType", (Object) stuType).put("subjectStr", (Object) subjectStr);
            if (set1.add(subjectStr + gradeStr + schoolStr)) {
                autoDeleteEn(exam, schoolStr, gradeStr, subjectStr, "examinationnum");
            }
            String studentListSql = classType.equals("1") ? "SELECT sid id,examinationNumber studentId,classNum FROM student_examinationNumber stul  WHERE stul.examNum = {exam}  and  stul.schoolNum={schoolStr}   AND stul.gradeNum={gradeStr} and stul.classNum in ({classString[]})  and stul.type in ({stuType[]}) ORDER BY RAND()" : "SELECT stu.sid id,stu.examinationNumber studentId,stu.classNum FROM student_examinationNumber stu  LEFT JOIN levelstudent stul ON stul.sid = stu.sid where stu.examNum = {exam}  and    stul.schoolNum={schoolStr}  AND stul.gradeNum={gradeStr} and stul.classNum in ({classString[]})  and stul.subjectNum={subjectStr}  and stu.type in ({stuType[]}) ORDER BY RAND()";
            List<?> _queryBeanList = this.dao._queryBeanList(studentListSql, Student.class, args);
            int stuListIndex = 0;
            for (int j = 0; j < examinationJsonArray.size(); j++) {
                JSONObject examinationJson = (JSONObject) examinationJsonArray.get(j);
                if (examinationJson.get("subject").equals(subjectStr) && examinationJson.get("grade").equals(gradeStr) && examinationJson.get(License.SCHOOL).equals(schoolStr) && examinationJson.get("classtype").equals(classtypeStr)) {
                    examinationJson.get("schoolName").toString();
                    String schoolNum = examinationJson.get(License.SCHOOL).toString();
                    String gradeNum = examinationJson.get("grade").toString();
                    int examinationRoomNum = Integer.parseInt(examinationJson.get("roomNum").toString());
                    String subjectNum = examinationJson.get("subject").toString();
                    String thisRoomNum = examinationJson.get("thisRoomNum").toString();
                    Map args2 = StreamMap.create().put("exam", (Object) exam).put("testingCentreId", (Object) testingCentreId).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum)).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
                    String examinationRoomId = this.dao._queryStr("select  id from examinationroom where examNum = {exam} and testingCentreId ={testingCentreId}  and examinationRoomNum = {examinationRoomNum} and  subjectNum ={subjectNum}  and gradeNum ={gradeNum}  and isDelete ='F'", args2);
                    if (null == examinationRoomId || "" == examinationRoomId || "null" == examinationRoomId) {
                        String examinationroomid = GUID.getGUIDStr();
                        Map args3 = StreamMap.create().put("examinationroomid", (Object) examinationroomid).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum)).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("user", (Object) user).put("exam", (Object) exam).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
                        this.dao._execute("insert into examinationroom (id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,examNum,isDelete,testingCentreId,subjectNum) values({examinationroomid},{examinationRoomNum},CONCAT({examinationRoomNum},' 考场'),{gradeNum},{user},now(),{exam},'F',{testingCentreId},{subjectNum})", args3);
                        examinationRoomId = this.dao._queryStr("select  id from examinationroom where examNum = {exam} and testingCentreId ={testingCentreId}  and examinationRoomNum = {examinationRoomNum} and  subjectNum ={subjectNum}  and gradeNum ={gradeNum}  and isDelete ='F'", args3);
                    }
                    String finalExaminationRoomId = examinationRoomId;
                    Map args4 = StreamMap.create().put("exam", (Object) exam).put("testingCentreId", (Object) testingCentreId).put("examinationRoomId", (Object) finalExaminationRoomId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
                    this.dao._execute("delete from examinationnum where examNum ={exam} and  testingCentreId ={testingCentreId}  and examinationroomNum = {examinationRoomId}  and subjectNum={subjectNum} and gradeNum={gradeNum}  and isDelete ='F'", args4);
                    List<Object[]> list = new ArrayList<>();
                    String[] arg = {"examinationRoomNum", "examineeNum", "studentID", "insertUser", "insertDate", Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_classNum, "testingCentreId", Const.EXPORTREPORT_subjectNum, "seatNum"};
                    int seatNumLen = thisRoomNum.length();
                    for (int h = 0; h < Integer.parseInt(thisRoomNum); h++) {
                        if (_queryBeanList.size() != 0 && _queryBeanList.size() > stuListIndex) {
                            String stuId = ((Student) _queryBeanList.get(stuListIndex)).getId();
                            String studentId = ((Student) _queryBeanList.get(stuListIndex)).getStudentId();
                            String classNum = ((Student) _queryBeanList.get(stuListIndex)).getClassNum();
                            String currentTime = DateUtil.getCurrentTime();
                            Object[] fileds = {examinationRoomId, studentId, stuId, user, currentTime, exam, schoolNum, gradeNum, classNum, testingCentreId, subjectStr, StrUtil.fillBefore(Convert.toStr(Integer.valueOf(h + 1)), '0', seatNumLen)};
                            list.add(fileds);
                            stuNo++;
                            stuListIndex++;
                        }
                    }
                    if (!CollUtil.isEmpty(list)) {
                        this.dao.batchInsert("examinationnum", arg, list);
                    }
                }
            }
        }
        deleteExaminationNumber(exam);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void divideExamRoomByGradeNoGroupOfGdkc(String exam, String stuType, int grade, List<Class> classListt, String user, String roomCount, String elength, String idornum, JSONArray examinationJsonArray, JSONArray jsonArray, JSONArray jsonArrayCopy, String classType) {
        DecimalFormat examroomDf = new DecimalFormat(creatStr(3, '0'));
        Set<String> set1 = new HashSet<>();
        new HashSet();
        Map<JSONObject, String> map1 = new LinkedHashMap<>();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject examDataJsonObject = (JSONObject) jsonArray.get(i);
            String classString = examDataJsonObject.get("classStr").toString();
            String gradeStr = examDataJsonObject.get("gradeStr").toString();
            String schoolStr = examDataJsonObject.get("schoolStr").toString();
            String subjectStr = examDataJsonObject.get("subjectStr").toString();
            String classtypeStr = examDataJsonObject.get("classtypeStr").toString();
            String testingCentreId = examDataJsonObject.get("testingCentreStr").toString();
            if ("1".equals(classType)) {
                examDataJsonObject.remove("subjectStr");
                examDataJsonObject.remove("classStr");
                if (map1.containsKey(examDataJsonObject)) {
                    classString = classString + Const.STRING_SEPERATOR + map1.get(examDataJsonObject);
                }
                map1.put(examDataJsonObject, classString);
            } else {
                List<Student> examStuList = getExamStuByCheckedOfGdkcFc(subjectStr, gradeStr, schoolStr, classString, stuType, exam);
                examDataJsonObject.remove("subjectStr");
                examDataJsonObject.remove("classStr");
                if (map1.containsKey(examDataJsonObject)) {
                    String cla = map1.get(examDataJsonObject);
                    classString = classString + Const.STRING_SEPERATOR + cla;
                    List<Student> allList = (List) linkedHashMap.get(cla);
                    allList.addAll(examStuList);
                    linkedHashMap.put(classString, allList);
                }
                map1.put(examDataJsonObject, classString);
                linkedHashMap.put(classString, examStuList);
            }
            if (set1.add(subjectStr + gradeStr + schoolStr)) {
                autoDeleteEn(exam, schoolStr, gradeStr, subjectStr, "examinationroom");
            }
            for (int j = 0; j < examinationJsonArray.size(); j++) {
                JSONObject examinationJson = (JSONObject) examinationJsonArray.get(j);
                if (examinationJson.get("grade").equals(gradeStr) && examinationJson.get(License.SCHOOL).equals(schoolStr) && examinationJson.get("classtype").equals(classtypeStr)) {
                    examinationJson.get("schoolName").toString();
                    String gradeNum = examinationJson.get("grade").toString();
                    int examinationRoomNum = Integer.parseInt(examinationJson.get("roomNum").toString());
                    Map args2 = StreamMap.create().put("exam", (Object) exam).put("testingCentreId", (Object) testingCentreId).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum)).put(Const.EXPORTREPORT_subjectNum, (Object) subjectStr).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
                    String examinationRoomId = this.dao._queryStr("select  id from examinationroom where examNum = {exam}  and testingCentreId ={testingCentreId}  and examinationRoomNum ={examinationRoomNum} and  subjectNum ={subjectNum} and gradeNum ={gradeNum} and isDelete ='F'", args2);
                    if (null == examinationRoomId || "" == examinationRoomId || "null" == examinationRoomId) {
                        String examinationroomid = GUID.getGUIDStr();
                        Map args3 = StreamMap.create().put("examinationroomid", (Object) examinationroomid).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum)).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("user", (Object) user).put("exam", (Object) exam).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectStr);
                        this.dao._execute("insert into examinationroom (id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,examNum,isDelete,testingCentreId,subjectNum) values({examinationroomid},{examinationRoomNum},CONCAT({examinationRoomNum},' 考场'),{gradeNum},{user},,now(),{exam},'F',{testingCentreId},{subjectNum})", args3);
                    }
                }
            }
        }
        if ("1".equals(classType)) {
            for (Map.Entry<JSONObject, String> entry : map1.entrySet()) {
                JSONObject obj = entry.getKey();
                String classStr = entry.getValue();
                Map args4 = StreamMap.create().put("exam", (Object) exam).put("schoolStr", (Object) obj.getString("schoolStr")).put("gradeStr", (Object) obj.getString("gradeStr")).put("classStr", (Object) classStr).put("stuType", (Object) stuType);
                linkedHashMap.put(classStr, this.dao._queryBeanList("SELECT sid id,examinationNumber studentId,classNum,classNum ext6 FROM student_examinationNumber stul  WHERE stul.examNum = {exam} and stul.schoolNum={schoolStr}  AND stul.gradeNum={gradeStr}  and stul.classNum in ({classStr[]}) and stul.type in ({stuType[]}) ORDER BY RAND() ", Student.class, args4));
            }
        }
        for (int i2 = 0; i2 < jsonArrayCopy.size(); i2++) {
            JSONObject examDataJsonObject2 = (JSONObject) jsonArrayCopy.get(i2);
            String classString2 = examDataJsonObject2.get("classStr").toString();
            String gradeStr2 = examDataJsonObject2.get("gradeStr").toString();
            String schoolStr2 = examDataJsonObject2.get("schoolStr").toString();
            String subjectStr2 = examDataJsonObject2.get("subjectStr").toString();
            String classtypeStr2 = examDataJsonObject2.get("classtypeStr").toString();
            Iterator it = linkedHashMap.entrySet().iterator();
            while (true) {
                if (it.hasNext()) {
                    Map.Entry<String, List<Student>> entry2 = (Map.Entry) it.next();
                    if (entry2.getKey().indexOf(classString2) != -1) {
                        List<Student> stuList = entry2.getValue();
                        int stuListIndex = 0;
                        for (int j2 = 0; j2 < examinationJsonArray.size(); j2++) {
                            JSONObject examinationJson2 = (JSONObject) examinationJsonArray.get(j2);
                            if (examinationJson2.get("grade").equals(gradeStr2) && examinationJson2.get(License.SCHOOL).equals(schoolStr2) && examinationJson2.get("classtype").equals(classtypeStr2)) {
                                examinationJson2.get("centerNum").toString();
                                String testingCentreNum = String.format("%3s", schoolStr2).replaceAll("\\s", "0");
                                int examinationRoomNum2 = Integer.parseInt(examinationJson2.get("roomNum").toString());
                                Map args5 = new HashMap();
                                args5.put("exam", exam);
                                args5.put("testingCentreNum", testingCentreNum);
                                String testingCentreId2 = this.dao._queryStr("select id from testingcentre where examNum = {exam}  and testingCentreNum ={testingCentreNum} and isDelete ='F'", args5);
                                Map args6 = StreamMap.create().put("exam", (Object) exam).put("testingCentreId", (Object) testingCentreId2).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum2)).put("subjectStr", (Object) subjectStr2).put("gradeStr", (Object) gradeStr2);
                                String examinationRoomId2 = this.dao._queryStr("select id from examinationroom where examNum = {exam} and testingCentreId ={testingCentreId} and examinationRoomNum = {examinationRoomNum} and  subjectNum ={subjectStr} and gradeNum ={gradeStr} and isDelete ='F'", args6);
                                Map args7 = StreamMap.create().put("exam", (Object) exam).put("testingCentreId", (Object) testingCentreId2).put("examinationRoomId", (Object) examinationRoomId2).put("subjectStr", (Object) subjectStr2).put("gradeStr", (Object) gradeStr2);
                                this.dao._execute("delete from examinationnum where examNum ={exam}  and  testingCentreId = {testingCentreId}  and examinationroomNum ={examinationRoomId} and subjectNum={subjectStr}  and gradeNum={gradeStr}  and isDelete ='F'", args7);
                                List<Object[]> objectList = new ArrayList<>();
                                String[] arg = {"examinationRoomNum", "examineeNum", "studentID", "insertUser", "insertDate", Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_classNum, "testingCentreId", Const.EXPORTREPORT_subjectNum, "seatNum"};
                                String thisRoomNum = examinationJson2.get("thisRoomNum").toString();
                                int seatNumLen = thisRoomNum.length();
                                for (int h = 0; h < Integer.parseInt(thisRoomNum); h++) {
                                    if (stuList.size() != 0 && stuList.size() > stuListIndex) {
                                        String stuId = stuList.get(stuListIndex).getId();
                                        String studentId = stuList.get(stuListIndex).getStudentId();
                                        String classNum = stuList.get(stuListIndex).getClassNum();
                                        String xzbclassNum = stuList.get(stuListIndex).getExt6();
                                        if (classString2.indexOf(classNum) == -1) {
                                            stuListIndex++;
                                        } else {
                                            String currentTime = DateUtil.getCurrentTime();
                                            Object[] fileds = {examinationRoomId2, studentId, stuId, user, currentTime, exam, schoolStr2, gradeStr2, xzbclassNum, testingCentreId2, subjectStr2, StrUtil.fillBefore(Convert.toStr(Integer.valueOf(h + 1)), '0', seatNumLen)};
                                            objectList.add(fileds);
                                            stuListIndex++;
                                        }
                                    }
                                }
                                if (!CollUtil.isEmpty(objectList)) {
                                    this.dao.batchInsert("examinationnum", arg, objectList);
                                }
                            }
                        }
                    }
                }
            }
        }
        deleteExaminationNumber(exam);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void clearExamineeNumRoom(String exam) {
        Map args = StreamMap.create().put("exam", (Object) exam);
        this.dao._execute("delete from examinationnum where examNum={exam} ", args);
        this.dao._execute("delete from examinationroom where examNum={exam} ", args);
        this.dao._execute("DELETE from testingcentre where examNum = {exam} ", args);
        this.dao._execute("delete from testingcentre_school where examNum = {exam} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<String> getGroupNumList(String exam, String school, String grade) {
        Map args = StreamMap.create().put("exam", (Object) exam).put(License.SCHOOL, (Object) school).put("grade", (Object) grade);
        return this.dao._queryColList("SELECT groupNum FROM examineenumgroup WHERE examNum={exam}  AND schoolNum={school} AND gradenum={grade} ", String.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getschNumListByExam(String exam) {
        Map args = StreamMap.create().put("exam", (Object) exam);
        return this.dao._queryColList("select DISTINCT sch.id schoolNum,sch.shortname schoolName from school sch  LEFT JOIN grade gra on sch.id=gra.schoolNum LEFT JOIN exampaper exam on gra.gradeNum=exam.gradeNum where exam.examNum={exam} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getGradeList(String exam, String school) {
        Map args = StreamMap.create().put("exam", (Object) exam).put(License.SCHOOL, (Object) school);
        return this.dao._queryColList("SELECT distinct gradeNum FROM examineenumgroup WHERE examNum={exam}  AND schoolNum={school} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getsentence(String examNum, String subjectNum, String gradeNum, String schoolNum, String roomNum) {
        return this.examManageDAO.getsentence(examNum, subjectNum, gradeNum, schoolNum, roomNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getStuIdByNumOrName(String numOrname, String examNum, String schoolNum, String gradeNum, String cla, String radioValue, String testCenter, String subject) {
        return this.examManageDAO.getStuIdByNumOrName(numOrname, examNum, schoolNum, gradeNum, cla, radioValue, testCenter, subject);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List sentenceModified(String examNum, String subjectNum, String gradeNum, String schoolNum, String roomNum) {
        return this.examManageDAO.sentenceModified(examNum, subjectNum, gradeNum, schoolNum, roomNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getSentenImg(String schoolNum, String gradeNum, String examPaperNum, String questionNum, String examroomNum, String expMark, String ddoub, String examNum) {
        return this.examManageDAO.getSentenImg(schoolNum, gradeNum, examPaperNum, questionNum, examroomNum, expMark, ddoub, examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer updateSentenceScore(String scoreId, String questionScore) {
        return this.examManageDAO.updateSentenceScore(scoreId, questionScore);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void batchExportSql(String table_propertiesFile, String sqlFilePath, String exam, String grade, String school, String commandSet, String basic_check_flag, String examPaperImage_check_flag, String questionImage_check_flag, String scoreImage_check_flag, String examineeNumImage_check_flag) {
        Runtime runTime = Runtime.getRuntime();
        Properties properties = new Properties();
        try {
            InputStream inS = new FileInputStream(table_propertiesFile);
            properties.load(inS);
            String examNum_table = properties.getProperty(Const.EXPORTREPORT_examNum);
            String examPaperNum_table = properties.getProperty("exampaperNum");
            String paperImage_table = properties.getProperty("paperImage");
            String questionImage_table = properties.getProperty("questionImage");
            String scoreImage_table = properties.getProperty("scoreImage");
            String examineeNumImage_table = properties.getProperty("examineeNumImage");
            String basicTable_table = properties.getProperty("basicTable");
            String otherTable_table = properties.getProperty("otherTable");
            File sqlFolder = new File(sqlFilePath);
            if (!sqlFolder.exists()) {
                sqlFolder.mkdirs();
            }
            List<Exampaper> paperNumList = this.examDAO.getExamPaperNums(exam, grade);
            StringBuffer commandBuffer = new StringBuffer();
            if (paperNumList != null && paperNumList.size() > 0) {
                for (int i = 0; i < paperNumList.size(); i++) {
                    String subjectNum = paperNumList.get(i).getSubjectNum().toString();
                    String[] examNum_tableArray = examNum_table.split(Const.STRING_SEPERATOR);
                    for (String tableName : examNum_tableArray) {
                        commandBuffer.append(commandSet);
                        commandBuffer.append(tableName + " -w \"examNum='" + exam + "'  ");
                        File sqlFile = new File(sqlFilePath + tableName + "_" + school + ".sql");
                        if (!sqlFile.exists()) {
                            sqlFile.createNewFile();
                        }
                        commandBuffer.append("\"   --default-character-set=utf8 --hex-blob -r " + sqlFile);
                        runTime.exec(commandBuffer.toString());
                        commandBuffer = new StringBuffer();
                    }
                    String[] examPaperNum_tableArray = examPaperNum_table.split(Const.STRING_SEPERATOR);
                    for (String tableName2 : examPaperNum_tableArray) {
                        commandBuffer.append(commandSet);
                        commandBuffer.append(tableName2 + " -w \"examPaperNum='" + paperNumList.get(i).getExamPaperNum() + "'  ");
                        File sqlFile2 = new File(sqlFilePath + tableName2 + "_" + school + "_" + grade + "_" + subjectNum + ".sql");
                        if (!sqlFile2.exists()) {
                            sqlFile2.createNewFile();
                        }
                        commandBuffer.append("\"   --default-character-set=utf8  --hex-blob  -r " + sqlFile2);
                        runTime.exec(commandBuffer.toString());
                        commandBuffer = new StringBuffer();
                    }
                    String[] paperImage_tableArray = paperImage_table.split(Const.STRING_SEPERATOR);
                    if (examPaperImage_check_flag != null && examPaperImage_check_flag.equals("T")) {
                        for (String tableName3 : paperImage_tableArray) {
                            commandBuffer.append(commandSet);
                            commandBuffer.append(tableName3 + " -w \"examPaperNum='" + paperNumList.get(i).getExamPaperNum() + "'  ");
                            if (school != null && !"-1".equals(school)) {
                                commandBuffer.append("and schoolNum='" + school + "'");
                            }
                            File sqlFile3 = new File(sqlFilePath + tableName3 + "_" + school + "_" + grade + "_" + subjectNum + ".sql");
                            if (!sqlFile3.exists()) {
                                sqlFile3.createNewFile();
                            }
                            commandBuffer.append("\"   --default-character-set=utf8  --hex-blob  -r " + sqlFile3);
                            runTime.exec(commandBuffer.toString());
                            commandBuffer = new StringBuffer();
                        }
                    }
                    String[] questionImage_tableArray = questionImage_table.split(Const.STRING_SEPERATOR);
                    if (questionImage_check_flag != null && questionImage_check_flag.equals("T")) {
                        for (String tableName4 : questionImage_tableArray) {
                            commandBuffer.append(commandSet);
                            commandBuffer.append(tableName4 + " -w \"examPaperNum='" + paperNumList.get(i).getExamPaperNum() + "'  ");
                            if (school != null && !"-1".equals(school)) {
                                commandBuffer.append("and schoolNum='" + school + "'");
                            }
                            File sqlFile4 = new File(sqlFilePath + tableName4 + "_" + school + "_" + grade + "_" + subjectNum + ".sql");
                            if (!sqlFile4.exists()) {
                                sqlFile4.createNewFile();
                            }
                            commandBuffer.append("\"   --default-character-set=utf8  --hex-blob  -r " + sqlFile4);
                            runTime.exec(commandBuffer.toString());
                            commandBuffer = new StringBuffer();
                        }
                    }
                    String[] scoreImage_tableArray = scoreImage_table.split(Const.STRING_SEPERATOR);
                    if (scoreImage_check_flag != null && scoreImage_check_flag.equals("T")) {
                        for (String tableName5 : scoreImage_tableArray) {
                            commandBuffer.append(commandSet);
                            commandBuffer.append(tableName5 + " -w \"examPaperNum='" + paperNumList.get(i).getExamPaperNum() + "'  ");
                            if (school != null && !"-1".equals(school)) {
                                commandBuffer.append("and schoolNum='" + school + "'");
                            }
                            File sqlFile5 = new File(sqlFilePath + tableName5 + "_" + school + "_" + grade + "_" + subjectNum + ".sql");
                            if (!sqlFile5.exists()) {
                                sqlFile5.createNewFile();
                            }
                            commandBuffer.append("\"   --default-character-set=utf8  --hex-blob  -r " + sqlFile5);
                            runTime.exec(commandBuffer.toString());
                            commandBuffer = new StringBuffer();
                        }
                    }
                    String[] examineeNumImage_tableArray = examineeNumImage_table.split(Const.STRING_SEPERATOR);
                    if (examineeNumImage_check_flag != null && examineeNumImage_check_flag.equals("T")) {
                        for (String tableName6 : examineeNumImage_tableArray) {
                            commandBuffer.append(commandSet);
                            commandBuffer.append(tableName6 + " -w \"examPaperNum='" + paperNumList.get(i).getExamPaperNum() + "'  ");
                            if (school != null && !"-1".equals(school)) {
                                commandBuffer.append("and schoolNum='" + school + "'");
                            }
                            File sqlFile6 = new File(sqlFilePath + tableName6 + "_" + school + "_" + grade + "_" + subjectNum + ".sql");
                            if (!sqlFile6.exists()) {
                                sqlFile6.createNewFile();
                            }
                            commandBuffer.append("\"   --default-character-set=utf8  --hex-blob  -r " + sqlFile6);
                            runTime.exec(commandBuffer.toString());
                            commandBuffer = new StringBuffer();
                        }
                    }
                }
                String[] basicTable_tableArray = basicTable_table.split(Const.STRING_SEPERATOR);
                if (basic_check_flag != null && basic_check_flag.equals("T")) {
                    for (String tableName7 : basicTable_tableArray) {
                        commandBuffer.append(commandSet);
                        commandBuffer.append(tableName7 + "  ");
                        File sqlFile7 = new File(sqlFilePath + tableName7 + "_" + school + ".sql");
                        if (!sqlFile7.exists()) {
                            sqlFile7.createNewFile();
                        }
                        commandBuffer.append("  --default-character-set=utf8  --hex-blob -r " + sqlFile7);
                        runTime.exec(commandBuffer.toString());
                        commandBuffer = new StringBuffer();
                    }
                }
                String[] otherTable_tableArray = otherTable_table.split(Const.STRING_SEPERATOR);
                for (String tableName8 : otherTable_tableArray) {
                    commandBuffer.append(commandSet);
                    commandBuffer.append(tableName8 + "  ");
                    File sqlFile8 = new File(sqlFilePath + tableName8 + "_" + school + ".sql");
                    if (!sqlFile8.exists()) {
                        sqlFile8.createNewFile();
                    }
                    commandBuffer.append("  --default-character-set=utf8  --hex-blob -r " + sqlFile8);
                    runTime.exec(commandBuffer.toString());
                    commandBuffer = new StringBuffer();
                }
            }
        } catch (Exception e) {
            this.log.info("sql导出：", e);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void batchImportSql(String indexFile, String loginCommd, String switchCommd) {
        try {
            File file = new File(indexFile);
            new FileOutputStream(file);
            String importCommdStr = "";
            File fff = new File(indexFile.substring(0, indexFile.lastIndexOf(92)));
            if (null != fff.listFiles() && fff.listFiles().length > 0) {
                for (File f : fff.listFiles()) {
                    if (null != f.listFiles() && f.listFiles().length > 0) {
                        for (File ff : f.listFiles()) {
                            if (ff.exists()) {
                                String ssff = ff.toString();
                                importCommdStr = importCommdStr + "source " + ssff.replace("\\", "/") + ";\r\n";
                            }
                        }
                    }
                }
            }
            importCommdStr.split(";");
            RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
            randomFile.write(importCommdStr.getBytes());
            randomFile.close();
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(loginCommd);
            OutputStream os = process.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            String importCommd = "source " + file.toString().replace("\\", "/") + ";";
            writer.write(switchCommd + "\r\n" + importCommd);
            writer.flush();
            writer.close();
            os.close();
        } catch (Exception e) {
            this.log.info("批量导入sql文件：：", e);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer getexamid(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryInt("SELECT examNum FROM exam where examNum={examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer checksReg(String examNum, String subjectNum, String gradeNum, String schoolNum) {
        String gradeNumStr = "";
        String schoolNumStr = "";
        String subjectNumStr = "";
        Map args = new HashMap();
        if (null != gradeNum && !"".equals(gradeNum) && !"null".equals(gradeNum)) {
            gradeNumStr = "  AND gradeNum={gradeNum} ";
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        }
        if (null != schoolNum && !"".equals(schoolNum) && !"null".equals(schoolNum)) {
            schoolNumStr = " AND schoolNum={schoolNum} ";
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        }
        if (null != subjectNum && !"".equals(subjectNum) && !"null".equals(subjectNum)) {
            subjectNumStr = "AND subjectNum={subjectNum} ";
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        }
        String examPaperNumListsql = "SELECT exampaperNum FROM exampaper WHERE examNum={examNum} " + subjectNumStr + gradeNumStr;
        args.put(Const.EXPORTREPORT_examNum, examNum);
        List exampaperList = this.dao._queryColList(examPaperNumListsql, args);
        int cou = 0;
        for (int i = 0; i < exampaperList.size(); i++) {
            String examPaperNum = String.valueOf(exampaperList.get(i));
            String sql = "SELECT count(1) FROM regexaminee WHERE examPaperNum={examPaperNum} " + schoolNumStr;
            Map args2 = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
            if (null != this.dao._queryStr(sql, args2) && !"".equals(this.dao._queryStr(sql, args2)) && !"null".equals(this.dao._queryStr(sql, args2))) {
                cou = this.dao._queryInt(sql, args2).intValue();
            }
            if (cou > 0) {
                break;
            }
        }
        return Integer.valueOf(cou);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg checkExamData(String examNum, String subjectNum, String gradeNum) {
        String graStr = "".equals(gradeNum) ? "" : " and gradeNum={gradeNum}  ";
        StringBuffer epSql = new StringBuffer();
        epSql.append("select pexamPaperNum examPaperNum,gradeNum,subjectNum from exampaper ");
        epSql.append("where examNum={examNum}  ");
        epSql.append(graStr);
        epSql.append(" and subjectNum={subjectNum} ");
        StringBuffer sql = new StringBuffer();
        sql.append("select ep.examPaperNum,ep.gradeNum,bg.gradeName,ep.subjectNum,sub.subjectName from (");
        sql.append(epSql);
        sql.append(") ep ");
        sql.append("left join basegrade bg on bg.gradeNum=ep.gradeNum ");
        sql.append("left join `subject` sub on sub.subjectNum=ep.subjectNum");
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        List<Map<String, Object>> epList = this.dao._queryMapList(sql.toString(), null, args);
        StringBuffer msgStr = new StringBuffer();
        epList.forEach(ep -> {
            Object examPaperNum = ep.get("examPaperNum");
            Object gradeName = ep.get("gradeName");
            Object subjectName = ep.get("subjectName");
            StringBuffer msg = new StringBuffer();
            StringBuffer regSql = new StringBuffer();
            regSql.append("select id from regexaminee ");
            regSql.append("where examPaperNum={examPaperNum} limit 1");
            Map args2 = StreamMap.create().put("examPaperNum", examPaperNum);
            if (null != this.dao._queryObject(regSql.toString(), args2)) {
                msg.append(gradeName).append(subjectName).append("的扫描数据");
            }
            StringBuffer defSql = new StringBuffer();
            defSql.append("select id from define ");
            defSql.append(" where examPaperNum={examPaperNum} limit 1");
            if (null != this.dao._queryObject(defSql.toString(), args2)) {
                if (msg.length() > 0) {
                    msg.append("，双向细目表");
                } else {
                    msg.append(gradeName).append(subjectName).append("的双向细目表");
                }
            }
            StringBuffer enSql = new StringBuffer();
            enSql.append("select id from examinationnum ");
            enSql.append("where examNum={examNum}  and gradeNum={gradeNum}  and subjectNum={subjectNum} limit 1");
            Map args3 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, ep.get(Const.EXPORTREPORT_gradeNum)).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
            if (null != this.dao._queryObject(enSql.toString(), args3)) {
                if (msg.length() > 0) {
                    msg.append("，考号");
                } else {
                    msg.append(gradeName).append(subjectName).append("的考号");
                }
            }
            Map args4 = new HashMap();
            args4.put(Const.EXPORTREPORT_examNum, examNum);
            args4.put("examPaperNum", examPaperNum);
            String roleNum = this.dao._queryStr("select roleNum from role where examNum={examNum} and examPaperNum={examPaperNum} and type='4' ", args4);
            if (StrUtil.isNotEmpty(roleNum)) {
                args4.put("roleNum", roleNum);
                String userroleId = this.dao._queryStr("select id from userrole where roleNum={roleNum} limit 1", args4);
                if (StrUtil.isNotEmpty(userroleId)) {
                    if (msg.length() > 0) {
                        msg.append("，已上报的阅卷教师（删除前先导出备用）。");
                    } else {
                        msg.append(gradeName).append(subjectName).append("的已上报的阅卷教师（删除前先导出备用）。");
                    }
                }
            }
            if (msg.length() > 0) {
                msgStr.append(msg.toString()).append("\n");
            }
        });
        if (msgStr.length() > 0) {
            msgStr.insert(0, "先删除");
            return new RspMsg(Const.height_500, msgStr.toString(), null);
        }
        return new RspMsg(200, "", null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void del(String examNum, String subjectNum, String gradeNum, String schoolNum, String classNum, String stuType) throws SQLException {
        String gradeNumStr = "";
        String schoolNumStr = "";
        String stuTypeStr = "";
        String classNumstr = "";
        if (null != gradeNum && !"".equals(gradeNum) && !"null".equals(gradeNum)) {
            gradeNumStr = "  AND gradeNum={gradeNum} ";
        }
        if (null != schoolNum && !"".equals(schoolNum) && !"null".equals(schoolNum)) {
            schoolNumStr = " AND c.schoolNum={schoolNum} ";
        }
        if (null != stuType && !"".equals(stuType) && !"null".equals(stuType)) {
            stuTypeStr = "  AND c.studentType={stuType} ";
        }
        if (null != classNum && !"null".equals(classNum) && !"".equals(classNum)) {
            classNumstr = " AND c.id={classNum} ";
        }
        String examPaperNumListsql = "SELECT exampaperNum FROM exampaper WHERE examNum={examNum}  AND subjectNum={subjectNum} " + gradeNumStr;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        List exampaperList = this.dao._queryColList(examPaperNumListsql, args);
        for (int i = 0; i < exampaperList.size(); i++) {
            String paperNum = String.valueOf(exampaperList.get(i));
            String classlistSql = "SELECT DISTINCT cl.classNum FROM classexam  cl LEFT JOIN class c ON cl.classNum=c.id WHERE cl.exampaperNum={paperNum} " + classNumstr + schoolNumStr + stuTypeStr;
            Map args2 = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("stuType", (Object) stuType).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("paperNum", (Object) paperNum);
            List classList = this.dao._queryColList(classlistSql, args2);
            for (int j = 0; j < classList.size(); j++) {
                String cNum = classList.get(j).toString();
                Map args3 = StreamMap.create().put("paperNum", (Object) paperNum).put("cNum", (Object) cNum);
                this.dao._execute("CALL del_Exam_Data({paperNum},{cNum})", args3);
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getShuangXiang1(String exampaperNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao._querySimpleMap("select 100*a.num/b.numb num  from   ( select  count(DISTINCT d.exampaperNum) num, ex.examnum,d.exampaperNum from define d  LEFT JOIN  exampaper  ex ON d.examPaperNum=ex.examPaperNum and ex.examPaperNum={exampaperNum}  where  ex.examNum is NOT NULL )a,   (select count(1) numb from exampaper where exampaperNum={exampaperNum}  and isHidden='F')b", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getCaiQie1(String exampaperNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao._querySimpleMap("select 100*a.num/b.numb  num from   ( select  COUNT(1) num from template t LEFT JOIN exampaper ex ON t.examPaperNum = ex.examPaperNum AND ex.examPaperNum={exampaperNum}  WHERE ex.examPaperNum IS NOT NULL  )a ,  (select COUNT(1) numb from exampaper where exampaperNum={exampaperNum}  and isHidden='F')b", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getSaoMiao1(String examPaperNum, String examNum, String gradeNum) {
        String sql;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        String scantype = this.dao._queryStr("SELECT scantype FROM exam WHERE examNum={examNum} ", args);
        if (scantype.equals("1")) {
            sql = "SELECT 100*count(DISTINCT examPaperNum) num FROM correctstatus WHERE exampaperNum={examPaperNum} ";
        } else {
            sql = "SELECT 100*a.num/b.num num FROM (SELECT count(examnitionRoom) num  FROM correctstatus WHERE exampaperNum={examPaperNum} )a,  (SELECT count(DISTINCT cl.examinationRoomNum) num FROM examinationnum cl LEFT JOIN student st ON st.classNum=cl.classNum AND st.schoolNum=cl.schoolNum  LEFT JOIN exampaper expa ON cl.subjectNum = expa.subjectNum AND cl.examNum = expa.examNum AND cl.gradeNum=expa.gradeNum  WHERE expa.exampaperNum={examPaperNum}  AND cl.examinationRoomNum IS NOT NULL)b ";
        }
        Map args2 = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao._querySimpleMap(sql, args2);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getHeDuiNum(String exampaperNum, String examNum, String gradeNum) {
        String sql;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        String scantype = this.dao._queryStr("SELECT scantype FROM exam WHERE examNum={examNum} ", args);
        if (scantype.equals("1")) {
            sql = "SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum,testingCentreId) num FROM correctstatus WHERE exampaperNum={exampaperNum}  )a, (SELECT count(DISTINCT exampaperNum,testingCentreId) num FROM correctstatus WHERE exampaperNum={exampaperNum}  AND numStatus='2')b";
        } else {
            sql = "SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE exampaperNum={exampaperNum}  )a, (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE exampaperNum={exampaperNum}  AND numStatus='2')b";
        }
        Map args2 = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        return this.dao._querySimpleMap(sql, args2);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getbulu(String examNum, String examPaperNum, String gradeNum) {
        String sql;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        String scantype = this.dao._queryStr("SELECT scantype FROM exam WHERE examNum={examNum} ", args);
        if (scantype.equals("1")) {
            sql = "SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum) num FROM correctstatus WHERE exampaperNum={examPaperNum} )a, (SELECT count(DISTINCT exampaperNum) num FROM correctstatus WHERE exampaperNum={examPaperNum}  AND appendStatus='2')b";
        } else {
            sql = "SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE exampaperNum={examPaperNum} )a, (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE exampaperNum={examPaperNum}  AND appendStatus='2')b";
        }
        Map args2 = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao._querySimpleMap(sql, args2);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getScore(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao._querySimpleMap("SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE exampaperNum={examPaperNum} )a, (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE exampaperNum={examPaperNum} AND status='2' )b", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getScore2(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._querySimpleMap("SELECT 100*b.num/a.num num FROM (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE examNum={examNum} )a, (SELECT count(DISTINCT exampaperNum,examnitionRoom) num FROM correctstatus WHERE examNum={examNum}  AND status='2' )b", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer getException(String examPaperNum, String mark1) {
        String sql;
        if (mark1.equals("1")) {
            sql = "SELECT count(DISTINCT  s.studentId,s.questionNum)  FROM  (SELECT exampaperNum,studentId,questionNum FROM score WHERE (isException='0' OR isException='1')AND CONTINUEd='F')s LEFT JOIN  illegal i  ON s.studentId = i.studentId AND s.examPaperNum=i.examPaperNum   WHERE s.examPaperNum={examPaperNum}  AND i.type ='2'";
        } else {
            sql = "SELECT count(DISTINCT questionNum,examPaperNum )  FROM  remark WHERE exampaperNum={examPaperNum}  AND `status`='F'";
        }
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao._queryInt(sql, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getpanfen(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao._querySimpleMap("select 100*a.num/b.num num from  (select count(1) num from task where exampaperNum={examPaperNum}   ) b,  (select count(1) num from task where exampaperNum={examPaperNum}  and status='T' )a", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getpanfen2(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._querySimpleMap("select 100*a.num/b.num num from  (  select count(1) num from task t  inner join exampaper e on t.exampaperNum=e.examPaperNum  where examNum= {examNum}   ) b,   (  select count(1) num from task t  LEFT join exampaper e on t.exampaperNum=e.examPaperNum  where examNum= {examNum} and t.status='T'   )a ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<QuestionGroup> getGroupNumList(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao._queryBeanList("SELECT groupNum FROM questiongroup WHERE exampaperNum={examPaperNum} ", QuestionGroup.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void addtoCompleteExam(String[] examNums, String user, String type) {
        String date = DateUtil.getCurrentDay();
        for (String examNum : examNums) {
            String status = "";
            Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
            List<Object> epNums = this.dao._queryColList("select examPaperNum from exampaper where examNum={examNum} ", args);
            if (type.equals("1")) {
                status = "9";
                for (Object obj : epNums) {
                    String epNum = String.valueOf(obj);
                    Map args2 = StreamMap.create().put("epNum", (Object) epNum);
                    this.dao._execute("insert into taskhistory (select * from task where exampaperNum={epNum} ) ", args2);
                    this.dao._execute("delete from task where exampaperNum={epNum} ", args2);
                }
                String[] gudingRoleNumArr = {"4", "3"};
                this.his.deleteUserroleByExam(examNum, gudingRoleNumArr);
            } else if (type.equals("2")) {
                status = "0";
                for (Object obj2 : epNums) {
                    String epNum2 = String.valueOf(obj2);
                    Map args22 = StreamMap.create().put("epNum", (Object) epNum2);
                    this.dao._execute("insert into task (select * from taskhistory where exampaperNum={epNum} ) ", args22);
                    this.dao._execute("delete from taskhistory where exampaperNum={epNum} ", args22);
                    this.dao._execute("update questiongroup q   inner JOIN  (select count(id) tnum,groupNum from task where exampaperNum={epNum} group by groupNum)t on t.groupNum=q.groupNum  set q.totalnum=t.tnum  where q.exampaperNum={epNum} ", args22);
                }
            }
            String finalStatus = status;
            Map args3 = StreamMap.create().put(Const.CORRECT_SCORECORRECT, (Object) finalStatus).put("date", (Object) date).put(Const.EXPORTREPORT_examNum, (Object) examNum);
            this.dao._execute("UPDATE exam SET `status`={status},updateDate={date}  where examNum={examNum} ", args3);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String checkExamstatus(String[] examNums) {
        return this.examManageDAO.checkExamstatus(examNums);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List querySchoolByExam(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryBeanList("select id,schoolName from ( select DISTINCT schoolNum from examinationnum where examNum = {examNum}  and isDelete='F') as en left join school s on s.id=en.schoolNum and s.isDelete='F'", School.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<AjaxData> queryGradeBySchoolAndExam(String schoolNum, String examNum) {
        String schoolStr = " and schoolNum={schoolNum} ";
        if ("-1".equals(schoolNum)) {
            schoolStr = "";
        }
        String sql = "select DISTINCT g.gradeNum as num,g.gradeName as name from (select DISTINCT gradeNum from examinationnum where examNum = {examNum} " + schoolStr + " and isDelete='F') as en left join grade g on g.gradeNum=en.gradeNum and g.isDelete='F'";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List queryschool() {
        return this.dao.queryBeanList(" select * from school", School.class);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List queryexam(String schoolNum, String mark) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryBeanList("select exam.examNum,exam.examName from exam left join (select examPaper.examNum from school sc left join  examinationnum en on sc.schoolNum=en.schoolNum LEFT join exampaper on  exampaper.examNum=en.examNum AND exampaper.gradeNum=en.gradeNum AND exampaper.subjectNum=en.subjectNum where sc.schoolNum={schoolNum}  GROUP BY  examPaper.examNum) ex2 on exam.examNum=ex2.examnum where exam.isDelete='F' ORDER BY exam.examDate desc,exam.insertDate DESC", Exam.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<AjaxData> querygrade(String examNum) {
        String sql;
        if ("".equals(examNum)) {
            sql = "SELECT distinct g.gradeNum num ,g.gradeName name from  grade g  order by g.gradeNum desc";
        } else {
            sql = "SELECT distinct e.gradeNum num ,g.gradeName name from exampaper e  LEFT JOIN `grade` g on e.gradeNum = g.gradeNum and e.jie=g.jie  WHERE e.examNum = {examNum}   AND e.isHidden={FALSE}  order by g.gradeNum desc";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("FALSE", (Object) "F");
        return this.dao._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<AjaxData> querysubject(String gradeNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryBeanList("select sub.subjectNum num,sub.subjectname name from (select * from levelclass where gradeNum={gradeNum}  and schoolNum={schoolNum}  group by subjectnum)lc  left join subject sub on sub.subjectNum=lc.subjectNum ", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object[] queryneirong(String schoolNum, String examNum, String gradeNum, String jie, String sxtype) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        List list = this.dao._queryBeanList("(SELECT ep.examPaperNum exampaperNum,s.subjectNum subjectNum,s.subjectName FROM (select en.subjectNum  subjectNum1,emp.exampaperNum exampaperNum,emp.subjectNum subjectNum from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum where c.studentType = '1' and en.examNum={examNum} and en.schoolNum={schoolNum} group by en.subjectNum) aa LEFT JOIN exampaper ep ON ep.pexamPaperNum = aa.exampaperNum LEFT JOIN subject s ON s.subjectNum = ep.subjectNum) UNION ALL (select DISTINCT sl.examPaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from gradelevel sl LEFT JOIN subject sub on sub.subjectNum = sl.subjectNum where sl.schoolNum = {schoolNum} and sl.examNum = {examNum} and sl.gradeNum = {gradeNum}  and sl.studentType = '1' and (sl.subjectNum = 4 or (sl.subjectNum BETWEEN 20 AND 99)))", Exampaper.class, args);
        List list11 = this.dao._queryBeanList("(SELECT ep.examPaperNum exampaperNum,s.subjectNum subjectNum,s.subjectName FROM (select en.subjectNum  subjectNum1,emp.exampaperNum exampaperNum,emp.subjectNum subjectNum from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum where c.studentType = '2' and en.examNum={examNum}  and en.schoolNum={schoolNum}  group by en.subjectNum) aa LEFT JOIN exampaper ep ON ep.pexamPaperNum = aa.exampaperNum LEFT JOIN subject s ON s.subjectNum = ep.subjectNum) UNION ALL (select DISTINCT sl.examPaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from gradelevel sl LEFT JOIN subject sub on sub.subjectNum = sl.subjectNum where sl.schoolNum = {schoolNum}  and sl.examNum = {examNum}  and sl.gradeNum = {gradeNum}  and sl.studentType = '2' and (sl.subjectNum = 4 or (sl.subjectNum BETWEEN 20 AND 99)))", Exampaper.class, args);
        List list111 = this.dao._queryBeanList("(SELECT ep.examPaperNum exampaperNum,s.subjectNum subjectNum,s.subjectName FROM (select en.subjectNum  subjectNum1,emp.exampaperNum exampaperNum,emp.subjectNum subjectNum from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum where c.studentType = '0' and en.examNum={examNum} and en.schoolNum={schoolNum}  group by en.subjectNum) aa LEFT JOIN exampaper ep ON ep.pexamPaperNum = aa.exampaperNum LEFT JOIN subject s ON s.subjectNum = ep.subjectNum) UNION ALL (select DISTINCT sl.examPaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from gradelevel sl LEFT JOIN subject sub on sub.subjectNum = sl.subjectNum where sl.schoolNum = {schoolNum}  and sl.examNum = {examNum}  and sl.gradeNum = {gradeNum}  and sl.studentType = '0' and (sl.subjectNum = 4 or (sl.subjectNum BETWEEN 20 AND 99)))", Exampaper.class, args);
        Integer listsize = Integer.valueOf(list.size());
        Integer listsize11 = Integer.valueOf(list11.size());
        Integer.valueOf(list111.size());
        List list2 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.id classNum,c.className className from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum where c.studentType = '1' and en.examNum={examNum}  and en.schoolNum={schoolNum} group by en.classNum order by length(en.classNum),en.classNum ", Exampaper.class, args);
        List list22 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.id classNum,c.className className from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum where c.studentType = '2' and en.examNum={examNum}  and en.schoolNum={schoolNum}  group by en.classNum order by length(en.classNum),en.classNum ", Exampaper.class, args);
        List list222 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.id classNum,c.className className from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum where c.studentType = '0' and en.examNum={examNum} and en.schoolNum={schoolNum}  group by en.classNum order by length(en.classNum),en.classNum ", Exampaper.class, args);
        Integer list2size = Integer.valueOf(list2.size());
        Integer list22size = Integer.valueOf(list22.size());
        Integer list222size = Integer.valueOf(list222.size());
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie);
        String subjecttype = this.dao._queryStr(" select count(1) from class cla where cla.studenttype!='0' and gradeNum={gradeNum}  and schoolNum={schoolNum}  and jie={jie} ", args2);
        String gradetype = this.dao._queryStr(" select gradename from grade where gradeNum={gradeNum}  and jie={jie} ", args2);
        return new Object[]{list, list11, listsize, listsize11, list2, list22, list2size, list22size, subjecttype, gradetype, list111, list222, list222size};
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object[] queryneirong_fc(String schoolNum, String examNum, String gradeNum, String subjectNum, String jie, String sxtype) {
        String subjectsql = "";
        if (!subjectNum.equals("-1")) {
            subjectsql = subjectsql + " and subjectNum={subjectNum} ";
        }
        String sql = "select emp.exampaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from   ( select subjectNum,gradeNum,jie,schoolNum from levelclass where  studentType='1' " + subjectsql + " and gradeNum={gradeNum} GROUP BY subjectNum)c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum}  " + subjectsql + " and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum left join subject sub on sub.subjectNum=emp.subjectNum where emp.examNum={examNum}  and c.schoolNum={schoolNum}  group by sub.subjectNum";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        List list = this.dao._queryBeanList(sql, Exampaper.class, args);
        String sql11 = "select emp.exampaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from   ( select subjectNum,gradeNum,jie,schoolNum from levelclass where  studentType='2' " + subjectsql + " and gradeNum={gradeNum}  GROUP BY subjectNum)c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum} " + subjectsql + " and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum left join subject sub on sub.subjectNum=emp.subjectNum where emp.examNum={examNum}  and c.schoolNum={schoolNum}  group by sub.subjectNum";
        List list11 = this.dao._queryBeanList(sql11, Exampaper.class, args);
        String sql111 = "select emp.exampaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from   ( select subjectNum,gradeNum,jie,schoolNum from levelclass where  studentType='0' " + subjectsql + " and gradeNum={gradeNum}  GROUP BY subjectNum)c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum} " + subjectsql + " and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum left join subject sub on sub.subjectNum=emp.subjectNum where emp.examNum={examNum}  and c.schoolNum={schoolNum}  group by sub.subjectNum";
        List list111 = this.dao._queryBeanList(sql111, Exampaper.class, args);
        Integer listsize = Integer.valueOf(list.size());
        Integer listsize11 = Integer.valueOf(list11.size());
        Integer.valueOf(list111.size());
        String sql2 = "select emp.exampaperNum exampaperNum,c.id classNum,c.className className from  ( select id,subjectNum,gradeNum,jie,schoolNum,classNum,className from levelclass where  studentType='1' " + subjectsql + " and gradeNum={gradeNum} )c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum} " + subjectsql + " and gradeNum={gradeNum}) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum where emp.examNum={examNum}  and c.schoolNum={schoolNum}  group by c.classNum  order by length(c.id),c.id ";
        List list2 = this.dao._queryBeanList(sql2, Exampaper.class, args);
        String sql22 = "select emp.exampaperNum exampaperNum,c.id classNum,c.className className from  ( select id,subjectNum,gradeNum,jie,schoolNum,classNum,className from levelclass where  studentType='2' " + subjectsql + " and gradeNum={gradeNum} )c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum}  " + subjectsql + " and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum where emp.examNum={examNum}  and c.schoolNum={schoolNum}  group by c.classNum  order by length(c.id),c.id ";
        List list22 = this.dao._queryBeanList(sql22, Exampaper.class, args);
        String sql222 = "select emp.exampaperNum exampaperNum,c.id classNum,c.className className from  ( select id,subjectNum,gradeNum,jie,schoolNum,classNum,className from levelclass where  studentType='0' " + subjectsql + " and gradeNum={gradeNum} )c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum} " + subjectsql + " and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum where emp.examNum={examNum}  and c.schoolNum={schoolNum}  group by c.classNum  order by length(c.id),c.id ";
        List list222 = this.dao._queryBeanList(sql222, Exampaper.class, args);
        Integer list2size = Integer.valueOf(list2.size());
        Integer list22size = Integer.valueOf(list22.size());
        Integer list222size = Integer.valueOf(list222.size());
        String sql3 = " select count(1) from levelclass cla where cla.studenttype!='0' and gradeNum={gradeNum}  and schoolNum={schoolNum}  and jie={jie} " + subjectsql + " ";
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie);
        String subjecttype = this.dao._queryStr(sql3, args2);
        String gradetype = this.dao._queryStr(" select gradename from grade where gradeNum={gradeNum} and jie={jie} ", args2);
        return new Object[]{list, list11, listsize, listsize11, list2, list22, list2size, list22size, subjecttype, gradetype, list111, list222, list222size};
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object[] queryneirong_q(String examNum, String gradeNum, String jie, String sxtype) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        List list = this.dao._queryBeanList("(SELECT ep.examPaperNum exampaperNum,s.subjectNum subjectNum,s.subjectName FROM (select en.subjectNum  subjectNum1,emp.exampaperNum exampaperNum,emp.subjectNum subjectNum from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum where c.studentType = '1' and en.examNum={examNum}  group by en.subjectNum) aa LEFT JOIN exampaper ep ON ep.pexamPaperNum = aa.exampaperNum LEFT JOIN subject s ON s.subjectNum = ep.subjectNum) UNION ALL (select DISTINCT sl.examPaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from gradelevel sl LEFT JOIN subject sub on sub.subjectNum = sl.subjectNum where sl.examNum = {examNum} and sl.gradeNum = {gradeNum} and sl.studentType = '1' and (sl.subjectNum = 4 or (sl.subjectNum BETWEEN 20 AND 99)))", Exampaper.class, args);
        List list11 = this.dao._queryBeanList("(SELECT ep.examPaperNum exampaperNum,s.subjectNum subjectNum,s.subjectName FROM (select en.subjectNum  subjectNum1,emp.exampaperNum exampaperNum,emp.subjectNum subjectNum from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum where c.studentType = '2' and en.examNum={examNum}  group by en.subjectNum) aa LEFT JOIN exampaper ep ON ep.pexamPaperNum = aa.exampaperNum LEFT JOIN subject s ON s.subjectNum = ep.subjectNum) UNION ALL (select DISTINCT sl.examPaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from gradelevel sl LEFT JOIN subject sub on sub.subjectNum = sl.subjectNum where sl.examNum = {examNum}and sl.gradeNum = {gradeNum}  and sl.studentType = '2' and (sl.subjectNum = 4 or (sl.subjectNum BETWEEN 20 AND 99)))", Exampaper.class, args);
        List list111 = this.dao._queryBeanList("(SELECT ep.examPaperNum exampaperNum,s.subjectNum subjectNum,s.subjectName FROM (select en.subjectNum  subjectNum1,emp.exampaperNum exampaperNum,emp.subjectNum subjectNum from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum where c.studentType = '0' and en.examNum={examNum}  group by en.subjectNum) aa LEFT JOIN exampaper ep ON ep.pexamPaperNum = aa.exampaperNum LEFT JOIN subject s ON s.subjectNum = ep.subjectNum) UNION ALL (select DISTINCT sl.examPaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from gradelevel sl LEFT JOIN subject sub on sub.subjectNum = sl.subjectNum where sl.examNum ={examNum} and sl.gradeNum = {gradeNum}  and sl.studentType = '0' and (sl.subjectNum = 4 or (sl.subjectNum BETWEEN 20 AND 99)))", Exampaper.class, args);
        Integer listsize = Integer.valueOf(list.size());
        Integer listsize11 = Integer.valueOf(list11.size());
        Integer.valueOf(list111.size());
        List list2 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,en.schoolNum,sch.shortname schoolName from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum  left join school sch on en.schoolNum=sch.id where c.studentType = '1' and en.examNum={examNum}  group by en.schoolNum ", Exampaper.class, args);
        List list22 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,en.schoolNum,sch.shortname schoolName from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum  left join school sch on en.schoolNum=sch.id where c.studentType = '2' and en.examNum={examNum} group by en.schoolNum ", Exampaper.class, args);
        List list222 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,en.schoolNum,sch.shortname schoolName from examinationnum en left join class c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum  left join school sch on en.schoolNum=sch.id where c.studentType = '0' and en.examNum={examNum}  group by en.schoolNum ", Exampaper.class, args);
        Integer list2size = Integer.valueOf(list2.size());
        Integer list22size = Integer.valueOf(list22.size());
        Integer list222size = Integer.valueOf(list222.size());
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        String gradetype = this.dao._queryStr(" select gradename from grade where gradeNum={gradeNum}  and jie={jie} ", args2);
        return new Object[]{list, list11, listsize, listsize11, list2, list22, list2size, list22size, "", gradetype, list111, list222, list222size};
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object[] queryneirong_q_fc(String examNum, String gradeNum, String subjectNum, String jie, String sxtype) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        List list = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from  ( select subjectNum,gradeNum,jie,schoolNum from levelclass where  studentType='1' and subjectNum={subjectNum}  and gradeNum={gradeNum}  GROUP BY subjectNum)c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum}  and subjectNum={subjectNum} and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum  left join subject sub on sub.subjectNum=emp.subjectNum where emp.examNum={examNum} ' group by sub.subjectNum", Exampaper.class, args);
        List list11 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from   ( select subjectNum,gradeNum,jie,schoolNum from levelclass where  studentType='2' and subjectNum={subjectNum}  and gradeNum={gradeNum}  GROUP BY subjectNum)c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum} and subjectNum={subjectNum}  and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum  left join subject sub on sub.subjectNum=emp.subjectNum where emp.examNum={examNum}  group by sub.subjectNum", Exampaper.class, args);
        List list111 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from   ( select subjectNum,gradeNum,jie,schoolNum from levelclass where  studentType='0' and subjectNum={subjectNum} and gradeNum={gradeNum}  GROUP BY subjectNum)c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum}  and subjectNum={subjectNum}  and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum  left join subject sub on sub.subjectNum=emp.subjectNum where emp.examNum={examNum}  group by sub.subjectNum", Exampaper.class, args);
        Integer listsize = Integer.valueOf(list.size());
        Integer listsize11 = Integer.valueOf(list11.size());
        Integer.valueOf(list111.size());
        List list2 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.schoolNum,sch.shortname schoolName from  ( select subjectNum,gradeNum,jie,schoolNum from levelclass where  studentType='1' and subjectNum={subjectNum} and gradeNum={gradeNum}  GROUP BY subjectNum)c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum}  and subjectNum={subjectNum}  and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum  left join school sch on c.schoolNum=sch.id where emp.examNum={examNum}  group by c.schoolNum", Exampaper.class, args);
        List list22 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.schoolNum,sch.shortname schoolName from  ( select subjectNum,gradeNum,jie,schoolNum from levelclass where  studentType='2' and subjectNum={subjectNum}  and gradeNum={gradeNum}  GROUP BY subjectNum)c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum} and subjectNum={subjectNum}  and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum  left join school sch on c.schoolNum=sch.id where emp.examNum={examNum}  group by c.schoolNum", Exampaper.class, args);
        List list222 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.schoolNum,sch.shortname schoolName from  ( select subjectNum,gradeNum,jie,schoolNum from levelclass where  studentType='0' and subjectNum={subjectNum}  and gradeNum={gradeNum} GROUP BY subjectNum)c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum} and subjectNum={subjectNum}  and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum  left join school sch on c.schoolNum=sch.id where emp.examNum={examNum}  group by c.schoolNum", Exampaper.class, args);
        Integer list2size = Integer.valueOf(list2.size());
        Integer list22size = Integer.valueOf(list22.size());
        Integer list222size = Integer.valueOf(list222.size());
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        String gradetype = this.dao._queryStr(" select gradename from grade where gradeNum={gradeNum}  and jie={jie} ", args2);
        return new Object[]{list, list11, listsize, listsize11, list2, list22, list2size, list22size, "", gradetype, list111, list222, list222size};
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer createAndUpdatevalue(OnlineIndicator oid, String countclasstx) {
        String s;
        if ("-1".equals(oid.getClassNum()) && !"-1".equals(countclasstx)) {
            Map args = StreamMap.create().put("examnum", (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put("subjectnum", (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
            s = this.dao._queryStr("\tselect IFNULL(SUM(num),0) from OnlineIndicator   where examnum={examnum} and schoolNum={schoolNum} and   gradeNum={gradeNum}    and  subjectnum={subjectnum} and  classNum!='-1' and studentType={studentType}  and type={type}  and sxtype={sxtype}  and showLevel={showLevel} ", args);
        } else {
            s = oid.getNum() + "";
        }
        String finalS = s;
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_classNum, (Object) oid.getClassNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("s", (Object) finalS).put("insertUser", (Object) oid.getInsertUser()).put("insertDate", (Object) oid.getInsertDate()).put("updateUser", (Object) oid.getUpdateUser()).put("updateDate", (Object) oid.getUpdateDate()).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
        this.dao._execute("INSERT INTO OnlineIndicator  (examNum,schoolNum,gradeNum  ,subjectNum,classNum,studentType ,type,num,score ,insertUser,insertDate,updateUser,updateDate,sxtype,showLevel )  VALUES ({examNum},{schoolNum},{gradeNum},{subjectNum},{classNum},{studentType} ,{type},{s},0 ,{insertUser},{insertDate},{updateUser},{updateDate},{sxtype},{showLevel} )  ON DUPLICATE KEY UPDATE num={s} ", args2);
        if ("1".equals(oid.getExt7())) {
            forcreate(oid, countclasstx, "0", "create");
            return null;
        }
        return null;
    }

    public void forcreate(OnlineIndicator oid, String countclasstx, String scoretype, String updatetype) {
        List list2 = new ArrayList();
        Map arg = new HashMap();
        if (oid.getSchoolNum().intValue() == -1) {
            if (oid.getFc().equals("T") && "T".equals(oid.getShowLevel())) {
                if (oid.getSubjectNum().intValue() != -1) {
                    arg.put(Const.EXPORTREPORT_studentType, oid.getStudentType());
                    arg.put(Const.EXPORTREPORT_gradeNum, oid.getGradeNum());
                    arg.put("examnum", oid.getExamNum());
                    arg.put(Const.EXPORTREPORT_subjectNum, oid.getSubjectNum());
                } else {
                    Map args = StreamMap.create().put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum());
                    list2 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,en.schoolNum classNum,sch.shortname schoolName from examinationnum en left join class c on en.classNum=c.id and c.studentType={studentType}  and c.gradeNum={gradeNum} left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum and emp.gradeNum=c.gradeNum and emp.jie=c.jie left join school sch on en.schoolNum=sch.id where emp.examNum={examNum} group by en.schoolNum", Exampaper.class, args);
                }
            } else {
                Map args2 = StreamMap.create().put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum());
                list2 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,en.schoolNum classNum,sch.shortname schoolName from examinationnum en left join class c on en.classNum=c.id and c.studentType={studentType}  and c.gradeNum={gradeNum}  left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum and emp.gradeNum=c.gradeNum and emp.jie=c.jie left join school sch on en.schoolNum=sch.id where emp.examNum={examNum} group by en.schoolNum", Exampaper.class, args2);
            }
        } else if (oid.getFc().equals("T") && "T".equals(oid.getShowLevel())) {
            if (oid.getSubjectNum().intValue() != -1) {
                String subjectsql = " and subjectNum={subjectNum} ";
                String sql2 = "select emp.exampaperNum exampaperNum,c.id classNum,c.className className from  ( select id,subjectNum,gradeNum,jie,schoolNum,classNum,className from levelclass where  studentType={studentType} " + subjectsql + " and gradeNum={gradeNum} )c left join (select examNum,examPaperNum,gradeNum,subjectNum,jie from exampaper where examnum={examNum} " + subjectsql + " and gradeNum={gradeNum} ) emp  on emp.gradeNum=c.gradeNum and emp.jie=c.jie  and emp.subjectnum=c.subjectnum where emp.examNum={examNum}  and c.schoolNum={schoolNum}  group by c.classNum  order by length(c.id),c.id ";
                Map args3 = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum());
                list2 = this.dao._queryBeanList(sql2, Exampaper.class, args3);
            } else {
                Map args4 = StreamMap.create().put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum());
                list2 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.id classNum,c.className className from examinationnum en  left join class c on en.classNum=c.id and c.studentType={studentType}  and c.gradeNum={gradeNum}   left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum and emp.gradeNum=c.gradeNum and emp.jie=c.jie  where emp.examNum={examNum}  and en.schoolNum={schoolNum}  group by c.classNum  order by length(c.id),c.id ", Exampaper.class, args4);
            }
        } else {
            Map args5 = StreamMap.create().put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum());
            list2 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.id classNum,c.className className from examinationnum en  left join class c on en.classNum=c.id and c.studentType={studentType} and c.gradeNum={gradeNum}   left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum and emp.gradeNum=c.gradeNum and emp.jie=c.jie  where emp.examNum={examNum}  and en.schoolNum={schoolNum}  group by c.classNum  order by length(c.id),c.id ", Exampaper.class, args5);
        }
        new Exampaper();
        for (int i = 0; i < list2.size(); i++) {
            Exampaper ep = (Exampaper) list2.get(i);
            Map args6 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_classNum, (Object) ep.getClassNum()).put("studenttype", (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
            String querycount = this.dao._queryStr("select count(1) from OnlineIndicator where examNum={examNum} and  schoolNum={schoolNum}  and gradeNum={gradeNum}  and subjectNum={subjectNum} and classNum={classNum}  and studenttype={studenttype} and type={type}  and  sxtype={sxtype}  and showLevel={showLevel} ", args6);
            if ("create".equals(updatetype)) {
                if (querycount.equals("0")) {
                    String sql = "INSERT INTO OnlineIndicator  (examNum,schoolNum,gradeNum  ,subjectNum,classNum,studentType ,type,num,score ,insertUser,insertDate,updateUser,updateDate,sxtype,scoretype,showLevel )  VALUES ({examNum},{schoolNum},{gradeNum},{subjectNum},{classNum},{studentType} ,{type},0,0 ,{insertUser},{insertDate},{updateUser},{updateDate},{sxtype},1,{showLevel}) ";
                    Map args7 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_classNum, (Object) ep.getClassNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("insertUser", (Object) oid.getInsertUser()).put("insertDate", (Object) oid.getInsertDate()).put("updateUser", (Object) oid.getUpdateUser()).put("updateDate", (Object) oid.getUpdateDate()).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
                    if ("0".equals(scoretype)) {
                        sql = sql + " ON DUPLICATE KEY UPDATE num='0' ";
                    } else if ("1".equals(scoretype)) {
                        sql = sql + " ON DUPLICATE KEY UPDATE score='0',scoretype='1'";
                    }
                    this.dao._execute(sql, args7);
                }
            } else if ("delete".equals(updatetype) && !querycount.equals("0")) {
                Map args8 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_classNum, (Object) ep.getClassNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
                this.dao._execute("\tdelete from OnlineIndicator   where examnum={examnum}  and schoolNum={schoolNum}  and   gradeNum={gradeNum}   and  subjectnum={subjectnum} and  classNum={classNum}and studentType={studentType}  and type={type} and sxtype={sxtype}  and showLevel={showLevel} ", args8);
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer deletessx(OnlineIndicator oid, String countclasstx) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_classNum, (Object) oid.getClassNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
        this.dao._execute("\tdelete from OnlineIndicator   where examnum={examnum}  and schoolNum={schoolNum}   and   gradeNum={gradeNum}    and  subjectnum={subjectnum} and  classNum={classNum} and studentType={studentType}  and type={type}  and sxtype={sxtype}  and showLevel={showLevel} ", args);
        if ("1".equals(oid.getExt7())) {
            forcreate(oid, countclasstx, "0", "delete");
            return null;
        }
        return null;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer createAndUpdatevalueScore(OnlineIndicator oid, String countclasstx) {
        String s;
        if ("-1".equals(oid.getClassNum()) && !"-1".equals(countclasstx)) {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
            s = this.dao._queryStr("\tselect IFNULL(SUM(score),0) from OnlineIndicator   where examnum={examnum}  and schoolNum={schoolNum}   and   gradeNum={gradeNum}   and  subjectnum={subjectnum} and  classNum!='-1' and studentType={studentType}  and type={type} and sxtype={sxtype}  and showLevel = {showLevel} ", args);
        } else {
            s = oid.getScore() + "";
        }
        if (!s.equals("0")) {
            String finalS = s;
            Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_classNum, (Object) oid.getClassNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("score", (Object) oid.getScore()).put("insertUser", (Object) oid.getInsertUser()).put("insertDate", (Object) oid.getInsertDate()).put("updateUser", (Object) oid.getUpdateUser()).put("updateDate", (Object) oid.getUpdateDate()).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel()).put(" s", (Object) finalS);
            this.dao._execute("INSERT INTO OnlineIndicator  (examNum,schoolNum,gradeNum  ,subjectNum,classNum,studentType ,type,num,score ,insertUser,insertDate,updateUser,updateDate,sxtype,scoretype,showLevel )  VALUES ({examNum},{schoolNum},{gradeNum},{subjectNum},{classNum},{studentType} ,{type},0,{score} ,{insertUser},{insertDate},{updateUser},{updateDate},{sxtype},1,{showLevel}'') ON DUPLICATE KEY UPDATE score={s} ,scoretype='1'", args2);
        }
        if ("1".equals(oid.getExt7())) {
            forcreate(oid, countclasstx, "1", "create");
            return null;
        }
        return null;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String queryclasscountvalue(OnlineIndicator oid) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
        String s = this.dao._queryStr("\tselect IFNULL(SUM(num),0) from OnlineIndicator   where examnum={examnum}  and schoolNum={schoolNum}   and   gradeNum={gradeNum}   and  subjectnum={subjectnum}  and   classNum='-1' and studentType={studentType}  and type={type}  and sxtype={sxtype}  and showLevel={showLevel} ", args);
        return s;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String queryclasscountvalueScore(OnlineIndicator oid) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
        String s = this.dao._queryStr("\tselect IFNULL(SUM(score),0) from OnlineIndicator   where examnum={examnum} and schoolNum={schoolNum}   and   gradeNum={gradeNum}  and  subjectnum={subjectnum}  and   classNum='-1' and studentType={studentType}  and type={type}  and sxtype={sxtype} and showLevel={showLevel} ", args);
        return s;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List queryall(String schoolNum, String examNum, String gradeNum, String sxtype, String showLevel) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("sxtype", (Object) sxtype).put("showLevel", (Object) showLevel);
        return this.dao._queryBeanList("select oid.* from onlineIndicator oid  left join exampaper emp on emp.examNum=oid.examNum  and emp.gradeNum=oid.gradeNum and emp.subjectNum=oid.subjectNum  where oid.examNum={examNum} and oid.schoolNum={schoolNum}  and oid.gradeNum={gradeNum}  and oid.sxtype={sxtype} and oid.showLevel={showLevel} ", OnlineIndicator.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List zqexam(String examNum, String gradeNum, String schoolNum, String jie, String sxtype) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("sxtype", (Object) sxtype).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie);
        return this.dao._queryBeanList("select e.examNum examNum,e.examName examName,e.examDate insertdate from   (select examNum,subjectNum from OnlineIndicator where examNum!={examNum}   and gradeNum={gradeNum} and sxtype={sxtype}  and schoolNum={schoolNum}  GROUP BY subjectNum,examNum)oid     left join (select examNum,subjectNum from exampaper where examNum!={examNum}  and jie={jie}    ) examp on oid.examNum=examp.examnum and oid.subjectNum=examp.subjectNum left join exam e on oid.examNum=e.examNum group by e.examNum", OnlineIndicator.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<OnlineIndicator> zqexam_q(String examNum, String gradeNum, String schoolNum, String jie, String sxtype) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("sxtype", (Object) sxtype).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("jie", (Object) jie);
        return this.dao._queryBeanList("select e.examNum examNum,e.examName examName,e.examDate insertdate from   (select examNum,subjectNum from OnlineIndicator where examNum!={examNum}   and gradeNum={gradeNum}  and sxtype={sxtype} and schoolNum={schoolNum}  GROUP BY subjectNum,examNum)oid     left join (select examNum,subjectNum from exampaper where examNum!={examNum}  and jie={jie}    ) examp on oid.examNum=examp.examnum and oid.subjectNum=examp.subjectNum left join exam e on oid.examNum=e.examNum group by e.examNum", OnlineIndicator.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void fzzqexam(String examNum) {
        for (int sxtype = 0; sxtype < 2; sxtype++) {
            int finalSxtype = sxtype;
            Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("sxtype", (Object) Integer.valueOf(finalSxtype));
            List<Exam> list = this.dao._queryBeanList("select e.examNum ,examp.gradeNum ext1 from   (select examNum from OnlineIndicator where examNum!={examNum}   and sxtype={sxtype} )oid left join (select examNum,gradeNum from exampaper where examNum!={examNum}     and isDelete='F'   group by examNum) examp  on oid.examNum=examp.examnum  left join (select examNum,examName,examDate from exam where isDelete='F') e on examp.examNum=e.examNum left join (select gradeName,gradeNum from basegrade where isGraduate='T' ) bg on bg.gradeNum=examp.gradeNum  where bg.gradeNum=examp.gradeNum  group by e.examNum order by e.examDate LIMIT 1 ", Exam.class, args);
            for (Exam ex : list) {
                copyvalue(examNum, String.valueOf(ex.getExamNum()), String.valueOf(ex.getExt1()), String.valueOf(sxtype));
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String copyvalue(String examNumNew, String examNumOld, String gradeNum, String sxtype) {
        String sql2;
        Map args = StreamMap.create().put("examNumOld", (Object) examNumOld);
        String countzai = this.dao._queryStr("select count(1) from his_exam where examNum={examNumOld} ", args);
        String sql22 = " select '" + examNumNew + "' examNum,oid.schoolNum,oid.gradeNum ,oid.subjectNum,oid.classNum,oid.studentType  ,oid.type,oid.num ,oid.insertUser,oid.insertDate,oid.updateUser,oid.updateDate,oid.sxtype  ";
        if (countzai.equals("0")) {
            sql2 = sql22 + "  from OnlineIndicator oid ";
        } else {
            sql2 = sql22 + "  from his_OnlineIndicator oid ";
        }
        Map args2 = StreamMap.create().put("examNumOld", (Object) examNumOld).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("sxtype", (Object) sxtype);
        List<OnlineIndicator> oidlist = this.dao._queryBeanList(sql2 + " left join exampaper emp on emp.examNum=oid.examNum  and emp.gradeNum=oid.gradeNum and emp.subjectNum=oid.subjectNum  where oid.examNum={examNumOld}  and oid.gradeNum={gradeNum}   and oid.sxtype={sxtype} ", OnlineIndicator.class, args2);
        if (oidlist.size() != 0) {
            Map args3 = StreamMap.create().put("examNumNew", (Object) examNumNew).put("sxtype", (Object) sxtype);
            this.dao._execute("delete from OnlineIndicator  where examNum={examNumNew}  and sxtype={sxtype} ", args3);
            for (OnlineIndicator oid : oidlist) {
                Map args4 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_classNum, (Object) oid.getClassNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("num", (Object) oid.getNum()).put("insertUser", (Object) oid.getInsertUser()).put("insertDate", (Object) oid.getInsertDate()).put("updateUser", (Object) oid.getUpdateUser()).put("updateDate", (Object) oid.getUpdateDate()).put("sxtype", (Object) oid.getSxtype());
                this.dao._execute("insert into OnlineIndicator (examNum,schoolNum,gradeNum  ,subjectNum,classNum,studentType ,type,num ,insertUser,insertDate,updateUser,updateDate,sxtype )  values({examNum},{schoolNum},{gradeNum},{subjectNum},{classNum},{studentType} ,{type},{num},{insertUser},{insertDate},{updateUser},{updateDate},{sxtype})", args4);
            }
        }
        Integer size = Integer.valueOf(oidlist.size());
        String oidsize = size.toString();
        return oidsize;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List querysubjectL(String schoolNum, String examNum, String gradeNum, String studenttype) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryBeanList("select emp.exampaperNum exampaperNum,sub.subjectNum subjectNum,sub.subjectName from examinationnum en left join (select * from class where studentType='1' and class.gradeNum={gradeNum}  ) c on en.classNum=c.id left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum and emp.gradeNum=c.gradeNum and emp.jie=c.jie  left join subject sub on sub.subjectNum=emp.subjectNum where emp.examNum={examNum}  and en.schoolNum={schoolNum}  group by sub.subjectNum", Exampaper.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> studentLengthList(String user, String examNum, String school) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        List li = this.dao._queryBeanList("SELECT jie FROM exampaper WHERE examNum={examNum}  LIMIT 1", Exampaper.class, args);
        if (null != li && li.size() > 0) {
            ((Exampaper) li.get(0)).getJie();
        }
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(License.SCHOOL, (Object) school);
        return this.dao._queryBeanList("select  distinct c.id ,c.classNum,g.gradeNum,g.gradeName     from  ( select    distinct   gradeNum    from  examinationnum     where  examNum={examNum} ' )  a   left  join  ( select *  from  grade  where schoolNum={school} '  group  by  gradeNum  ) g   on    a.gradeNum=g.gradeNum   left  join  (SELECT DISTINCT cc.id,cc.classNum,cc.className,clex.schoolNum,cc.gradeNum FROM examinationnum  en LEFT JOIN exampaper ep ON ep.examNum=en.examNum AND ep.gradeNum=en.gradeNum AND ep.subjectNum=en.subjectNum LEFT JOIN exam e ON ep.examNum=e.examNum LEFT JOIN class cc ON en.classNum = cc.id WHERE clex.schoolNum={school}  and e.examNum={examNum}  ) c   on  g.gradeNum=c.gradeNum  left  join (select schoolNum   from  school  where schoolNum={school} ') s  on  c.schoolNum=s.schoolNum  where  c.id  is  not  null  order  by  g.gradeNum*1,  c.classNum*1 ", StuExamInfo.class, args2);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> exporStudentList(String gradeNum, String classNum, String school, String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        List exporStudentList = this.dao._queryBeanList("  select  c.studentName,c.gradeNum,b.classNum ,d.examinationRoomNum,b.examineeNum  from    ( select  studentName,gradeNum,classNum,schoolNum,id from  student  WHERE  gradeNum={gradeNum}   and  schoolNum={school} ) c  LEFT  JOIN (  select  distinct  schoolNum,gradeNum,examinationRoomNum,studentId,examineeNum,classNum  from  examinationnum   WHERE  examNum={examNum}  AND  schoolNum={school}  AND  gradeNum={gradeNum}  AND classNum ={classNum}  ) b  ON  b.schoolNum=c.schoolNum  and  b.gradeNum=c.gradeNum   and  b.studentId=c.id   LEFT  JOIN   ( select  distinct  id,examinationRoomNum    from  examinationRoom   WHERE  examNum={examNum}   AND  schoolNum={school}   AND  gradeNum={gradeNum}   ) d   ON   b.examinationRoomNum=d.id  where    b.examineeNum   is  not  null   order by b.examineeNum asc  ", StuExamInfo.class, args);
        return exporStudentList;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> exporStudentList1(String gradeNum, String classNum, String testCenter, String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testCenter", (Object) testCenter).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        List exporStudentList = this.dao._queryBeanList("  select  c.studentName,c.gradeNum,b.classNum ,d.examinationRoomNum,b.examineeNum,f.examinationRoomLength,f.examineeLength,f.examName,\t SUBSTRING(b.examineeNum,  f.examinationRoomLength+1 ,f.examineeLength)as zuohao \tfrom  \t ( select  studentName,gradeNum,classNum,schoolNum,id  from  student  WHERE  gradeNum={gradeNum} ) c \t  INNER  JOIN (  select  distinct  schoolNum,gradeNum,examinationRoomNum,studentId,examineeNum ,classNum from  examinationnum  \t WHERE  examNum={examNum}  AND  testingCentreId={testCenter}  AND  gradeNum={gradeNum}  AND  classNum= {classNum} ) b  ON  b.schoolNum=c.schoolNum  and  b.gradeNum=c.gradeNum \t and  b.studentId=c.id   LEFT  JOIN   ( select  distinct  id,examinationRoomNum,examNum    from  examinationRoom \t  WHERE  examNum={examNum}  AND  testingCentreId={testCenter}   AND  gradeNum={gradeNum}   ) d  \t ON   b.examinationRoomNum=d.id   LEFT  JOIN (SELECT  *  FROM  EXAM  WHERE examNum={examNum}) f \ton  d.examNum=f.examNum \t where    b.examineeNum   is  not  null   order by b.examineeNum asc  ", StuExamInfo.class, args);
        return exporStudentList;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> exportAllStudentList(String school, String examNum, String gradeNum) {
        String jie = "";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        List li = this.dao._queryBeanList("SELECT jie FROM exampaper WHERE examNum={examNum}  AND gradeNum={gradeNum}  LIMIT 1", Exampaper.class, args);
        if (null != li && li.size() > 0) {
            jie = ((Exampaper) li.get(0)).getJie();
        }
        String finalJie = jie;
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) finalJie);
        return this.dao._queryBeanList("SELECT DISTINCT st.studentName studentName, gr.gradeNum gradeNum, cl.classNum classNum, cl.id id, exnum.examinationRoomNum examinationRoomNum,\texnum.examineeNum examineeNum, gr.gradeName gradeName FROM ( SELECT DISTINCT schoolNum, gradeNum, examinationRoomNum, studentId,examineeNum, examNum,  classNum FROM examinationnum WHERE examNum = {examNum}  AND schoolNum ={school}   AND gradeNum = {gradeNum}  ) exnum  LEFT JOIN examinationroom exroom ON exnum.examinationRoomNum = exroom.id  LEFT JOIN student st ON st.id = exnum.studentId  LEFT JOIN school sc ON sc.id = exnum.schoolNum  LEFT JOIN grade gr ON gr.gradeNum = exnum.gradeNum  AND gr.schoolNum = sc.id AND gr.jie = {jie}   LEFT JOIN class cl ON cl.id = exnum.classNum  WHERE exnum.examNum = {examNum}  AND exnum.schoolNum = {school}   AND exnum.gradeNum = {gradeNum} AND cl.jie = {jie}   ORDER BY exnum.examinationRoomNum * 1, exnum.examineeNum * 1", StuExamInfo.class, args2);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> studentroomList(String user, String examNum, String testCenterId, String gradeNum, String subjectNum) {
        String subSql = "";
        if (!"-1".equals(subjectNum)) {
            subSql = " and subjectNum = {subjectNum} ";
        }
        String sql = "select  distinct  a.gradeNum,d.examinationRoomNum,d.id,b.gradeName,d.subjectNum,sub.subjectName  from (select  distinct  schoolNum,gradeNum,examinationRoomNum,studentId,examineeNum,subjectNum  from  examinationnum  WHERE  examNum={examNum}   AND  testingCentreId={testCenterId} " + subSql + ")a left  join  grade  b  on  a.schoolNum=b.schoolNum  and  a.gradeNum=b.gradeNum left join ( select  distinct  id,examinationRoomNum,gradeNum,subjectNum from  examinationRoom  WHERE  examNum={examNum}   AND  testingCentreId={testCenterId} " + subSql + " ) d   ON   a.examinationRoomNum=d.id and  d.gradeNum=b.gradeNum  LEFT JOIN subject sub on sub.subjectNum = d.subjectNum  order  by  a.gradeNum*1,d.subjectNum*1,d.examinationRoomNum*1 asc ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testCenterId", (Object) testCenterId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao._queryBeanList(sql, StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String schoolName(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryStr("select  schoolName  from  school  where  id={schoolNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String testCenterName(String testCenterId) {
        Map args = StreamMap.create().put("testCenterId", (Object) testCenterId);
        return this.dao._queryStr("select testingCentreName from testingcentre where id={testCenterId} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> studentroomList1(String user, String examNum, String testCenter, String gradeNum, String subjectNum) {
        String gradeNumsql = "";
        if (gradeNum != "") {
            gradeNumsql = " and gradeNum={gradeNum}";
        }
        String subSql = "";
        if (!"-1".equals(subjectNum)) {
            subSql = " and subjectNum = {subjectNum}  ";
        }
        String sql = "select  distinct  a.gradeNum,f.schoolName,d.examinationRoomNum,d.id,b.gradeName,d.examinationRoomName,e.examName,d.subjectNum,sub.subjectName    from (select  * from  school) f  left  join  (select  distinct  schoolNum,gradeNum,examinationRoomNum,studentId,examineeNum,subjectNum  from  examinationnum  WHERE  examNum={examNum}   AND  testingCentreId={testCenter} " + gradeNumsql + subSql + "  )a   on a.schoolNum=f.id left  join  grade  b  on  a.schoolNum=b.schoolNum  and  a.gradeNum=b.gradeNum left join ( select  distinct  id,examinationRoomNum,examinationRoomName,gradeNum,examNum,subjectNum  from  examinationRoom  WHERE  examNum={examNum}   AND  testingCentreId={testCenter} " + gradeNumsql + subSql + " ) d   ON   a.examinationRoomNum=d.id and  d.gradeNum=b.gradeNum left  join  (select  *  from  exam  where  examNum={examNum} )e  on  d.examNum=e.examNum LEFT JOIN subject sub on sub.subjectNum = d.subjectNum  where  d.id  is  not  null  order  by  a.gradeNum*1,d.subjectNum*1,d.examinationRoomNum*1 asc ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testCenter", (Object) testCenter);
        return this.dao._queryBeanList(sql, StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> exportstudentroomList(String gradeNum, String examNum, String testCenterId, String examinationRoomNum, String examRoomNumid, String levelclass) {
        String sql;
        String levelclass2 = "F";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        Object result0 = this.dao._queryObject("select getIsLevelClass({examNum},{gradeNum},'100',null)", args);
        if (null != result0) {
            levelclass2 = result0.toString();
        }
        String jie = "";
        List li = this.dao._queryBeanList("SELECT jie FROM exampaper WHERE examNum={examNum}  AND gradeNum={gradeNum}   LIMIT 1", Exampaper.class, args);
        if (null != li && li.size() > 0) {
            jie = ((Exampaper) li.get(0)).getJie();
        }
        String sql2 = "SELECT DISTINCT st.studentName studentName, gr.gradeNum gradeNum, cl.className classNum, cl.id id,exroom.examinationRoomNum examinationRoomNum,  exnum.examineeNum examineeNum, gr.gradeName gradeName,sch.schoolName schoolName,exnum.seatNum,ifnull(subc.subjectCombineName,'') subjectCombineName FROM examinationnum exnum LEFT JOIN examinationroom exroom ON exnum.examinationRoomNum = exroom.id  LEFT JOIN student st ON st.id = exnum.studentId  LEFT JOIN subjectcombine subc ON subc.subjectCombineNum=st.subjectCombineNum ";
        if ("T".equals(levelclass2)) {
            sql2 = sql2 + " LEFT JOIN levelstudent ls ON ls.sid=st.id AND ls.subjectNum=exnum.subjectNum ";
        }
        String sql3 = sql2 + " LEFT JOIN school sch ON sch.id = exnum.schoolNum  LEFT JOIN grade gr ON gr.gradeNum = exnum.gradeNum  AND gr.schoolNum = exnum.schoolNum AND gr.jie = {jie}  ";
        if ("T".equals(levelclass2)) {
            sql = sql3 + " LEFT JOIN levelclass cl ON cl.id=ls.classNum ";
        } else {
            sql = sql3 + " LEFT JOIN class cl ON cl.id=exnum.classNum ";
        }
        String sql4 = sql + " WHERE exnum.examNum ={examNum}   AND exnum.testingCentreId = {testCenterId}  AND exnum.gradeNum = {gradeNum}  and exnum.examinationRoomNum ={examRoomNumid}  AND cl.jie = {jie} ORDER BY convert(exroom.examinationRoomNum using gbk),convert(exnum.seatNum using gbk), convert(exnum.examineeNum using gbk)";
        String finalJie = jie;
        Map args2 = StreamMap.create().put("jie", (Object) finalJie).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testCenterId", (Object) testCenterId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("examRoomNumid", (Object) examRoomNumid);
        return this.dao._queryBeanList(sql4, StuExamInfo.class, args2);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> exportstudentroomList_tiaoma(String gradeNum, String examNum, String school, String examinationRoomNum, String examRoomNumid) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("examRoomNumid", (Object) examRoomNumid).put("examinationRoomNum", (Object) examinationRoomNum);
        return this.dao._queryBeanList("select DISTINCT c.studentName,c.gradeNum,g.classNum ,g.id,d.examinationRoomNum,b.examineeNum,a.gradeName,s.studentId,g.className ,sl.schoolName from   ( select  studentName,gradeNum,classNum,schoolNum,id   from  student  WHERE  gradeNum={gradeNum}  ) c   LEFT  JOIN (  select  distinct  schoolNum,gradeNum,examinationRoomNum,studentId,examineeNum,classNum  from  examinationnum    WHERE  examNum={examNum}    AND  gradeNum={gradeNum}    AND  testingCentreId={school}  and  examinationRoomNum={examRoomNumid}    ) b  ON  b.schoolNum=c.schoolNum  and  b.gradeNum=c.gradeNum   and  b.studentId=c.id    LEFT JOIN class g ON  g.id = b.classNum   LEFT JOIN grade a ON a.id = b.gradeNum  LEFT  JOIN   ( select  distinct  id,examinationRoomNum    from  examinationRoom  WHERE   examNum={examNum}    AND  testingCentreId={school}    AND  gradeNum={gradeNum}     and examinationRoomNum={examinationRoomNum}    ) d   ON   b.examinationRoomNum=d.id LEFT JOIN student s ON b.studentID=s.id left join school sl on s.schoolNum=sl.id where   d.examinationRoomNum  is  not  null  order by b.examineeNum asc ", StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> exportstudentroomList1(String gradeNum, String examNum, String testCenter, String examinationRoomNum, String examRoomNumid, String levelclass) {
        String levelclass2 = "F";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        Object result0 = this.dao._queryObject("select getIsLevelClass({examNum},{gradeNum},'100',null)", args);
        if (null != result0) {
            levelclass2 = result0.toString();
        }
        String str = "";
        if (!"-1".equals(testCenter)) {
            str = " and en.testingCentreId={testCenter} ";
        }
        String sql = "SELECT s.studentName AS studentName,en.examineeNum AS examineeNum,g.gradeName AS gradeName,SUBSTRING(en.examineeNum,e.examinationRoomLength + 1,e.examineeLength) AS zuohao,e.examName,er.examinationRoomName AS examinationRoomName,s.studentNum AS studentNum,sh.schoolName AS schoolName,en.subjectNum AS subjectNum,en.seatNum AS seatNum,er.examinationRoomNum as examinationRoomNum,ifnull(subc.subjectCombineName,'') subjectCombineName  from examinationnum en  LEFT JOIN exam e ON e.examNum = en.examNum  inner JOIN examinationroom er on en.examinationRoomNum=er.id AND er.testingCentreId = en.testingCentreId AND er.examNum=en.examNum AND er.gradeNum=en.gradeNum AND er.subjectNum=en.subjectNum  LEFT JOIN student s ON en.studentID=s.id  LEFT JOIN subjectcombine subc ON subc.subjectCombineNum=s.subjectCombineNum ";
        if ("T".equals(levelclass2)) {
            sql = sql + " LEFT JOIN levelstudent ls ON ls.sid=s.id AND ls.subjectNum=en.subjectNum ";
        }
        String sql2 = sql + " left JOIN school sh ON sh.id=en.schoolNum  LEFT JOIN basegrade g ON g.gradeNum=en.gradeNum  WHERE en.examNum={examNum} " + str + " AND en.gradeNum = {gradeNum} AND en.examinationRoomNum = {examRoomNumid}  order by en.seatNum*1 ASC,en.examineeNum ASC";
        Map args2 = StreamMap.create().put("testCenter", (Object) testCenter).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("examRoomNumid", (Object) examRoomNumid);
        return this.dao._queryBeanList(sql2, StuExamInfo.class, args2);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> exportALLRoomList(String examNum, String testCenter, String gradeNum, String levelclass, String subjectNum) {
        String sql;
        String levelclass2 = "F";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        Object result0 = this.dao._queryObject("select getIsLevelClass({examNum},{gradeNum},'100',null)", args);
        if (null != result0) {
            levelclass2 = result0.toString();
        }
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        String jie = "";
        List li = this.dao._queryBeanList("SELECT jie FROM exampaper WHERE examNum={examNum}  AND gradeNum={gradeNum}   LIMIT 1", Exampaper.class, args2);
        if (null != li && li.size() > 0) {
            jie = ((Exampaper) li.get(0)).getJie();
        }
        String subSql = !"-1".equals(subjectNum) ? " and exnum.subjectNum ={subjectNum}  " : "";
        if ("T".equals(levelclass2)) {
            sql = "SELECT DISTINCT st.studentName studentName, gr.gradeNum gradeNum, lc.className classNum, exroom.examinationRoomNum,  exnum.examineeNum examineeNum,sch.schoolName schoolName,sub.subjectName,exnum.seatNum,ifnull(subc.subjectCombineName,'')subjectCombineName FROM examinationnum exnum  LEFT JOIN examinationroom exroom ON exnum.examinationRoomNum = exroom.id  LEFT JOIN student st ON st.id = exnum.studentId  LEFT JOIN levelstudent ls ON ls.sid = st.id AND ls.subjectNum = exnum.subjectNum LEFT JOIN school sch ON sch.id = exnum.schoolNum  LEFT JOIN grade gr ON gr.gradeNum = exnum.gradeNum AND gr.schoolNum = exnum.schoolNum AND gr.jie ={jie}   LEFT JOIN subject sub ON sub.subjectNum = exnum.subjectNum  LEFT JOIN subjectcombine subc ON subc.subjectCombineNum=st.subjectCombineNum  LEFT JOIN levelclass lc ON lc.id = ls.classNum AND lc.subjectNum = ls.subjectNum  WHERE exnum.examNum ={examNum} AND exnum.testingCentreId = {testCenter} AND exnum.gradeNum = {gradeNum}  " + subSql + " AND lc.jie ={jie}   ORDER BY exnum.subjectNum * 1,convert(exroom.examinationRoomNum using gbk),convert(exnum.seatNum using gbk), convert(exnum.examineeNum using gbk)";
        } else {
            sql = "SELECT DISTINCT st.studentName studentName, gr.gradeNum gradeNum, cl.className classNum, exroom.examinationRoomNum,  exnum.examineeNum examineeNum,sch.schoolName schoolName,sub.subjectName,exnum.seatNum,ifnull(subc.subjectCombineName,'')subjectCombineName FROM examinationnum exnum  LEFT JOIN examinationroom exroom ON exnum.examinationRoomNum = exroom.id  LEFT JOIN student st ON st.id = exnum.studentId  LEFT JOIN subjectcombine subc ON subc.subjectCombineNum=st.subjectCombineNum  LEFT JOIN school sch ON sch.id = exnum.schoolNum  LEFT JOIN grade gr ON gr.gradeNum = exnum.gradeNum AND gr.schoolNum = exnum.schoolNum AND gr.jie ={jie}  LEFT JOIN subject sub ON sub.subjectNum = exnum.subjectNum LEFT JOIN class cl ON cl.id = exnum.classNum  WHERE exnum.examNum = {examNum}  AND exnum.testingCentreId ={testCenter}  AND exnum.gradeNum ={gradeNum}   " + subSql + " AND cl.jie = {jie} ORDER BY exnum.subjectNum * 1,convert(exroom.examinationRoomNum using gbk),convert(exnum.seatNum using gbk), convert(exnum.examineeNum using gbk)";
        }
        String finalJie = jie;
        Map args3 = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("jie", (Object) finalJie).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testCenter", (Object) testCenter).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao._queryBeanList(sql, StuExamInfo.class, args3);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> exportALLRoomListByClass(String examNum, String schoolNum, String gradeNum, String levelclass, String subjectNum) {
        String sql;
        String levelclass2 = "F";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        Object result0 = this.dao._queryObject("select getIsLevelClass({examNum},{gradeNum},'100',null)", args);
        if (null != result0) {
            levelclass2 = result0.toString();
        }
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        String jie = "";
        List li = this.dao._queryBeanList("SELECT jie FROM exampaper WHERE examNum={examNum} AND gradeNum={gradeNum}  LIMIT 1", Exampaper.class, args2);
        if (null != li && li.size() > 0) {
            jie = ((Exampaper) li.get(0)).getJie();
        }
        String subSql = !"-1".equals(subjectNum) ? " and exnum.subjectNum ={subjectNum} " : "";
        if ("T".equals(levelclass2)) {
            sql = "SELECT DISTINCT st.studentName studentName, gr.gradeNum gradeNum, lc.className classNum, exroom.examinationRoomNum,  exnum.examineeNum examineeNum,sch.schoolName schoolName,sub.subjectName,exnum.seatNum,ifnull(subc.subjectCombineName,'') subjectCombineName FROM  examinationnum exnum  LEFT JOIN examinationroom exroom ON exnum.examinationRoomNum = exroom.id  LEFT JOIN student st ON st.id = exnum.studentId  LEFT JOIN subjectcombine subc ON subc.subjectCombineNum=st.subjectCombineNum  LEFT JOIN levelstudent ls ON ls.sid = st.id AND ls.subjectNum = exnum.subjectNum LEFT JOIN school sch ON sch.id = exnum.schoolNum  LEFT JOIN grade gr ON gr.gradeNum = exnum.gradeNum AND gr.schoolNum = exnum.schoolNum AND gr.jie ={jie}   LEFT JOIN subject sub ON sub.subjectNum = exnum.subjectNum  LEFT JOIN levelclass lc ON lc.id = ls.classNum AND lc.subjectNum = ls.subjectNum  WHERE exnum.examNum = {examNum}  AND exnum.schoolNum ={schoolNum} AND exnum.gradeNum ={gradeNum}  " + subSql + " AND lc.jie ={jie}   ORDER BY exnum.subjectNum * 1,convert(lc.classNum using gbk),convert(exnum.seatNum using gbk), convert(exnum.examineeNum using gbk)";
        } else {
            sql = "SELECT DISTINCT st.studentName studentName, gr.gradeNum gradeNum, cl.className classNum, exroom.examinationRoomNum,  exnum.examineeNum examineeNum,sch.schoolName schoolName,sub.subjectName,exnum.seatNum,ifnull(subc.subjectCombineName,'') subjectCombineName FROM  examinationnum exnum  LEFT JOIN examinationroom exroom ON exnum.examinationRoomNum = exroom.id  LEFT JOIN student st ON st.id = exnum.studentId  LEFT JOIN subjectcombine subc ON subc.subjectCombineNum=st.subjectCombineNum  LEFT JOIN school sch ON sch.id = exnum.schoolNum  LEFT JOIN grade gr ON gr.gradeNum = exnum.gradeNum AND gr.schoolNum = exnum.schoolNum AND gr.jie = {jie}  LEFT JOIN subject sub ON sub.subjectNum = exnum.subjectNum LEFT JOIN class cl ON cl.id = exnum.classNum  WHERE exnum.examNum = {examNum}  AND exnum.schoolNum = {schoolNum}  AND exnum.gradeNum = {gradeNum} " + subSql + " AND cl.jie ={jie}   ORDER BY exnum.subjectNum * 1,convert(cl.classNum using gbk),convert(exnum.seatNum using gbk), convert(exnum.examineeNum using gbk)";
        }
        String finalJie = jie;
        Map args3 = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("jie", (Object) finalJie).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao._queryBeanList(sql, StuExamInfo.class, args3);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Examinationroom> getexamroomList(String examNum, String school) {
        String schoolStr = "";
        if (!school.equals("-1")) {
            schoolStr = " AND ex.schoolNum={school} ";
        }
        String sql = "SELECT DISTINCT  ex.id,ex.examinationRoomNum ,gr.gradeName,sc.schoolNum,sc.schoolName ext1,ex.gradeNum\tFROM examinationroom ex LEFT JOIN grade gr ON ex.gradeNum=gr.gradeNum AND ex.schoolNum=gr.schoolNum LEFT JOIN school sc ON sc.schoolNum=gr.schoolNum\tWHERE ex.examNum={examNum} " + schoolStr + "  ORDER BY gr.gradeNum*1,ex.examinationRoomNum*1";
        Map args = StreamMap.create().put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryBeanList(sql, Examinationroom.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> getStuByexamroom(String examNum, String school, String examroom, String gradeNum) {
        String schoolStr = "";
        String examroomStr = "";
        if (!examroom.equals("1")) {
            examroomStr = " AND en.examinationRoomNum={examroom}  ";
        }
        if (!school.equals("-1")) {
            schoolStr = " AND sh.schoolNum={school} ";
        }
        String sql = "SELECT er.examinationRoomNum as examinationRoomNum, en.examineeNum as examineeNum, en.studentID as studentID, s.studentName as studentName, c.className as className, g.gradeName as gradeName, sh.schoolName as schoolName from examinationnum en  LEFT JOIN examinationroom er on en.examinationRoomNum=er.id AND er.schoolNum=en.schoolNum AND er.examNum=en.examNum AND er.gradeNum=en.gradeNum LEFT JOIN student s ON en.studentID=s.studentId  left JOIN school sh ON sh.schoolNum=en.schoolNum  LEFT JOIN basegrade g ON g.gradeNum=en.gradeNum  LEFT JOIN class c ON c.classNum=s.classNum  and g.gradeNum=c.gradeNum  AND c.schoolNum=s.schoolNum and s.jie=c.jie WHERE en.examNum={examNum} " + examroomStr + schoolStr + " and en.gradeNum={gradeNum}  order by sh.schoolNum*1,g.gradeNum*1,er.examinationRoomNum*1,en.examineeNum*1";
        Map args = StreamMap.create().put("examroom", (Object) examroom).put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao._queryBeanList(sql, StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getexamgradeList(String examNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryColList("select distinct gradeNum from examinationnum where examNum ={examNum}  and testingCentreId = {schoolNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getStudentType(String examNum, String gradeNum, String schoolNum) {
        String schoolStr = "";
        if (!schoolNum.equals("-1")) {
            schoolStr = " AND cl.schoolNum={schoolNum}  ";
        }
        String sql = "SELECT DISTINCT c.studentType  FROM classexam cl left JOIN exampaper ex ON cl.examPaperNum=ex.examPaperNum LEFT JOIN class c ON cl.classNum=c.id WHERE ex.examNum={examNum}  AND ex.gradeNum={gradeNum} " + schoolStr + "  ORDER BY c.studentType*1 ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao._queryColList(sql, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getUpexamNum(String gradeNum, String schoolNum, String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryStr("SELECT  DISTINCT e.examNum  FROM exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum WHERE ex.gradeNum={gradeNum}   AND e.examNum!={examNum}   AND e.isDelete='F' ORDER BY (replace(e.examDate,'-',''))*1 DESC LIMIT 0,1", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Studentlevel> getstuScoreList(String examNum, String gradeNum, String studentType, String school, String newexam, List clasList, String classType, String subjectNum, String stuType) {
        String str;
        String claStr = "AND (";
        Map args = new HashMap();
        for (int i = 0; i < clasList.size(); i++) {
            Object classNum = clasList.get(i);
            String classNum2 = classNum.toString() + i;
            if (i == 0) {
                str = claStr + " st.classNum={" + classNum2 + "} ";
            } else {
                str = claStr + " or st.classNum={" + classNum2 + "} ";
            }
            claStr = str;
            args.put("" + classNum2 + "", classNum);
        }
        String claStr2 = claStr + " )";
        String schoolStr = "";
        if (!school.equals("-1")) {
            schoolStr = " and st.schoolNum={school}  ";
        }
        if (classType.equals("1")) {
            subjectNum = "-1";
        }
        args.put(License.SCHOOL, school);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("newexam", newexam);
        args.put("stuType", stuType);
        String sql = "SELECT DISTINCT st.sid id,stlev.totalScore,st.examinationNumber ext1, st.classNum ext2 FROM student_examinationNumber st LEFT JOIN studentlevel stlev ON stlev.studentId=st.sid AND stlev.examNum={examNum}  AND stlev.subjectNum={subjectNum} AND stlev.statisticType='0' AND stlev.source='0'  WHERE   st.examNum = {newexam}  " + schoolStr + claStr2 + " and st.type in ({stuType[]})  ORDER BY IFNULL(stlev.totalScore,0) DESC";
        return this.dao._queryBeanList(sql, Studentlevel.class, args);
    }

    public List<Studentlevel> getstuScoreListOfGdkc(String examNum, String gradeNum, String studentType, String school, String newexam, String clasList, String classType, String stuType, String sjt) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("sjt", (Object) sjt).put("newexam", (Object) newexam).put("clasList", (Object) clasList).put("stuType", (Object) stuType);
        return this.dao._queryBeanList("SELECT DISTINCT st.sid id,stlev.totalScore,st.examinationNumber ext1, st.classNum ext2,st.classNum ext6 FROM student_examinationNumber st LEFT JOIN studentlevel stlev ON stlev.studentId=st.sid and stlev.examNum = {examNum} and stlev.subjectNum ={sjt} AND stlev.statisticType='0' AND stlev.source='0' WHERE st.examNum ={newexam} and  st.classNum in ({clasList[]}) and  st.type in ({stuType[]}) ORDER BY IFNULL(stlev.totalScore,0) DESC", Studentlevel.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String divexamroomByScore(String exam, String grade, String school, String roomCount, String user, String oldexamNum, String stuType, String elength, String idornum, JSONArray examinationJsonArray, JSONArray jsonArray, String classType, String type) {
        List<Studentlevel> stuList;
        DecimalFormat examroomDf = new DecimalFormat(creatStr(3, '0'));
        new DecimalFormat(creatStr(-1, '0'));
        Set<String> set1 = new HashSet<>();
        int stuNo = 1;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject examDataJsonObject = (JSONObject) jsonArray.get(i);
            String classString = examDataJsonObject.get("classStr").toString();
            String[] arr = classString.split(Const.STRING_SEPERATOR);
            String gradeStr = examDataJsonObject.get("gradeStr").toString();
            String schoolStr = examDataJsonObject.get("schoolStr").toString();
            String subjectStr = examDataJsonObject.get("subjectStr").toString();
            String classtypeStr = examDataJsonObject.get("classtypeStr").toString();
            String testingCentreId = examDataJsonObject.get("testingCentreStr").toString();
            if (set1.add(subjectStr + gradeStr + schoolStr)) {
                autoDeleteEn(exam, schoolStr, gradeStr, subjectStr, "examinationnum");
            }
            Arrays.asList(arr);
            new ArrayList();
            String sjt = "6".equals(type) ? "-1" : subjectStr;
            if ("1".equals(classType)) {
                stuList = getstuScoreListOfGdkc(oldexamNum, gradeStr, stuType, schoolStr, exam, classString, classType, stuType, sjt);
            } else {
                stuList = getstuScoreListOfGdkcFc(subjectStr, oldexamNum, exam, classString, stuType, sjt);
            }
            if (stuList.size() == 0) {
                String gradeName = getGradeNameByNum(schoolStr, gradeStr);
                String schoolName = getSchoolNameByNum(schoolStr);
                return schoolName + gradeName + "成绩条件不足，分配失败！";
            }
            int stuListIndex = 0;
            for (int j = 0; j < examinationJsonArray.size(); j++) {
                JSONObject examinationJson = (JSONObject) examinationJsonArray.get(j);
                if (examinationJson.get("subject").equals(subjectStr) && examinationJson.get("grade").equals(gradeStr) && examinationJson.get(License.SCHOOL).equals(schoolStr) && examinationJson.get("classtype").equals(classtypeStr)) {
                    examinationJson.get("schoolName").toString();
                    String schoolNum = examinationJson.get(License.SCHOOL).toString();
                    String gradeNum = examinationJson.get("grade").toString();
                    int examinationRoomNum = Integer.parseInt(examinationJson.get("roomNum").toString());
                    String subjectNum = examinationJson.get("subject").toString();
                    String thisRoomNum = examinationJson.get("thisRoomNum").toString();
                    Map args = StreamMap.create().put("exam", (Object) exam).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum));
                    String examinationRoomId = this.dao._queryStr("select id from examinationroom where examNum = {exam}  and testingCentreId ={testingCentreId}  and gradeNum={gradeNum}  and subjectNum = {subjectNum}   and examinationRoomNum ={examinationRoomNum}   and isDelete ='F'", args);
                    if (null == examinationRoomId || "" == examinationRoomId || "null" == examinationRoomId) {
                        String examinationroomid = GUID.getGUIDStr();
                        Map args2 = StreamMap.create().put("examinationroomid", (Object) examinationroomid).put("examinationRoomNum", (Object) examroomDf.format(examinationRoomNum)).put("gradeStr", (Object) gradeStr).put("user", (Object) user).put("exam", (Object) exam).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
                        this.dao._execute("insert into examinationroom (id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,examNum,isDelete,testingCentreId,subjectNum) values({examinationroomid},{examinationRoomNum},CONCAT({examinationRoomNum},'考场'),{gradeStr},{user},now(),{exam},'F',{testingCentreId},{subjectNum})", args2);
                        examinationRoomId = this.dao._queryStr("select id from examinationroom where examNum = {exam}  and testingCentreId ={testingCentreId}  and gradeNum={gradeNum}  and subjectNum = {subjectNum}   and examinationRoomNum ={examinationRoomNum}   and isDelete ='F'", args);
                    }
                    String finalExaminationRoomId = examinationRoomId;
                    Map args3 = StreamMap.create().put("exam", (Object) exam).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("examinationRoomId", (Object) finalExaminationRoomId);
                    this.dao._execute("delete from examinationnum where examNum ={exam}  and testingCentreId = {testingCentreId}  and gradeNum={gradeNum}  and subjectNum={subjectNum}   and examinationroomNum = {examinationRoomId}  and isDelete ='F'", args3);
                    List<Object[]> objects = new ArrayList<>();
                    String[] arg = {"examinationRoomNum", "examineeNum", "studentID", "insertUser", "insertDate", Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_classNum, "testingCentreId", Const.EXPORTREPORT_subjectNum, "seatNum"};
                    int seatNumLen = thisRoomNum.length();
                    for (int h = 0; h < Integer.parseInt(thisRoomNum); h++) {
                        if (stuList.size() != 0 && stuList.size() > stuListIndex) {
                            String stuId = stuList.get(stuListIndex).getId();
                            String studentId = stuList.get(stuListIndex).getExt1();
                            String xzbClassNum = stuList.get(stuListIndex).getExt6();
                            String currentTime = DateUtil.getCurrentTime();
                            Object[] fileds = {examinationRoomId, studentId, stuId, user, currentTime, exam, schoolNum, gradeNum, xzbClassNum, testingCentreId, subjectNum, StrUtil.fillBefore(Convert.toStr(Integer.valueOf(h + 1)), '0', seatNumLen)};
                            objects.add(fileds);
                            stuNo++;
                            stuListIndex++;
                        }
                    }
                    if (CollUtil.isEmpty(objects)) {
                        return "成绩条件不足，分配失败！";
                    }
                    this.dao.batchInsert("examinationnum", arg, objects);
                }
            }
        }
        deleteExaminationNumber(exam);
        return "分配成功！";
    }

    /* JADX WARN: Code restructure failed: missing block: B:96:0x06ea, code lost:
    
        r32 = r32 + 1;
     */
    @Override // com.dmj.service.examManagement.ExamManageService
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String divexamroomByScoreOfGdkc(java.lang.String r12, java.lang.String r13, java.lang.String r14, java.lang.String r15, java.lang.String r16, java.lang.String r17, java.lang.String r18, java.lang.String r19, java.lang.String r20, net.sf.json.JSONArray r21, net.sf.json.JSONArray r22, net.sf.json.JSONArray r23, java.lang.String r24, java.lang.String r25) {
        /*
            Method dump skipped, instructions count: 1785
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.dmj.serviceimpl.examManagement.ExamManageServiceimpl.divexamroomByScoreOfGdkc(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, net.sf.json.JSONArray, net.sf.json.JSONArray, net.sf.json.JSONArray, java.lang.String, java.lang.String):java.lang.String");
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void deleteByGrade(String exam, String grade, String school) {
        Map args = StreamMap.create().put("exam", (Object) exam);
        this.dao._execute("DELETE FROM examinationnum WHERE examnum={exam} ", args);
        this.dao._execute("DELETE FROM examinationroom WHERE examnum={exam} ", args);
        this.dao._execute("DELETE FROM testingcentre WHERE examNum={exam} ", args);
        this.dao._execute("delete from testingcentre_school where examNum = {exam} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List querysubjectW(String schoolNum, String examNum, String gradeNum, String studenttype) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryBeanList("select emp.exampaperNum exampaperNum,sub.subjectnum,sub.subjectName from examinationnum en left join (select * from class where studentType='2' and class.gradeNum={gradeNum} ) c on en.classNum=c.classNum left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum and emp.gradeNum=c.gradeNum and emp.jie=c.jie  left join subject sub on sub.subjectNum=emp.subjectNum where emp.examNum={examNum} and en.schoolNum={schoolNum}  group by sub.subjectNum", Exampaper.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List querysubject(String schoolNum, String examNum, String gradeNum, String studenttype) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryBeanList("select emp.exampaperNum exampaperNum,sub.subjectnum,sub.subjectName from examinationnum en left join (select * from class where studentType='0' and class.gradeNum={gradeNum}  ) c on en.classNum=c.classNum left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum and emp.gradeNum=c.gradeNum and emp.jie=c.jie  left join subject sub on sub.subjectNum=emp.subjectNum where emp.examNum={examNum}  and en.schoolNum={schoolNum}  group by sub.subjectNum", Exampaper.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List queryclassL(String schoolNum, String examNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        List list2 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.id classnum,c.className className from examinationnum en  left join (select * from class where studentType='1' and class.gradeNum={gradeNum} ) c on en.classNum=c.classNum left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum and emp.gradeNum=c.gradeNum and emp.jie=c.jie  where emp.examNum={examNum} and en.schoolNum={schoolNum}  group by c.classNum  order by length(c.classNum),c.classNum ", Exampaper.class, args);
        return list2;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List queryclassW(String schoolNum, String examNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        List list2 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.id classnum,c.className className from examinationnum en  left join (select * from class where studentType='2' and class.gradeNum={gradeNum}  ) c on en.classNum=c.classNum left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum and emp.gradeNum=c.gradeNum and emp.jie=c.jie  where emp.examNum={examNum} and en.schoolNum={schoolNum}  group by c.classNum  order by length(c.classNum),c.classNum ", Exampaper.class, args);
        return list2;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List queryclass(String schoolNum, String examNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        List list2 = this.dao._queryBeanList("select emp.exampaperNum exampaperNum,c.id classnum,c.className className from examinationnum en  left join (select * from class where studentType='0' and class.gradeNum={gradeNum}  ) c on en.classNum=c.classNum left join exampaper emp on emp.examNum=en.examNum AND emp.gradeNum=en.gradeNum AND emp.subjectNum=en.subjectNum and emp.gradeNum=c.gradeNum and emp.jie=c.jie  where emp.examNum={examNum}  and en.schoolNum={schoolNum}  group by c.classNum  order by length(c.classNum),c.classNum ", Exampaper.class, args);
        return list2;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer queryxiancount() {
        return this.dao.queryInt("select * from data where type='21'", null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map queryValFromDataByType(String type) {
        Map args = StreamMap.create().put("type", (Object) type);
        return this.dao._queryOrderMap("select value,name from data where type={type} ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getgrandeName(String gradeNum, String jie) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", (Object) jie);
        String gradetype = this.dao._queryStr(" select gradename from grade where gradeNum={gradeNum} and jie={jie} ", args);
        return gradetype;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List queryOnlineIndicator(String schoolNum, String examNum, String gradeNum, String studenttype, String sxtype) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("studenttype", (Object) studenttype).put("sxtype", (Object) sxtype);
        return this.dao._queryBeanList("select * from OnlineIndicator  where schoolNum={schoolNum}  and examNum={examNum}  and gradeNum={gradeNum}  and studenttype={studenttype}  and sxtype={sxtype}  order by length(classNum),classNum ", OnlineIndicator.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public OnlineIndicator queryOnlineIndicatorOne(String schoolNum, String examNum, String gradeNum, String studenttype, String classNum, String subjectNum, Integer type, String sxtype) {
        String sql = "select num,type from OnlineIndicator  where schoolNum={schoolNum}  and examNum={examNum}  and gradeNum={gradeNum}  and studenttype={studenttype}   and type={type}  and sxtype={sxtype} ";
        if (null != classNum && !"".equals(classNum)) {
            sql = sql + " and classNum={classNum} ";
        }
        if (null != subjectNum && !"".equals(subjectNum)) {
            sql = sql + " and subjectNum={subjectNum}  ";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("studenttype", (Object) studenttype).put("type", (Object) type).put("sxtype", (Object) sxtype).put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return (OnlineIndicator) this.dao._queryBean(sql, OnlineIndicator.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String maxcountyanzheng_q(OnlineIndicator oid) {
        String studentcountsql;
        String classNumsql = "";
        if (!"-1".equals(oid.getClassNum())) {
            classNumsql = "  and classNum={classNum} " + oid.getClassNum() + "'";
        }
        String sql = "\tselect IFNULL(SUM(num),0) from OnlineIndicator  where examnum={examnum} and schoolNum={schoolNum}  and   gradeNum={gradeNum}   and  subjectnum={subjectnum} " + classNumsql + " and studentType={studentType}  and type={type} and sxtype={sxtype} ";
        Map args = StreamMap.create().put("examnum", (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put("subjectnum", (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type", (Object) oid.getType()).put("sxtype", (Object) oid.getSxtype());
        Integer clasubcount = this.dao._queryInt(sql, args);
        Map args2 = StreamMap.create().put("examnum", (Object) oid.getExamNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum());
        this.dao._queryStr("select exampaperNum from exampaper where examnum={examnum}   and gradeNum={gradeNum}  limit 1", args2);
        String pSubNum = getPSubNum(oid.getExamNum(), oid.getGradeNum(), oid.getSubjectNum());
        String wSql = " examNum = {examNum} " + oid.getExamNum() + "' AND gradeNum = {gradeNum}  AND subjectNum ={pSubNum}  ";
        if (!"-1".equals(oid.getClassNum())) {
            studentcountsql = "select count(1) FROM (select ce.* ,c.className from (select distinct classNum,schoolNum from examinationnum where " + wSql + " and schoolNum={schoolNum} )ce  left join (select id,className,schoolNum,gradeNum from class where studentType={studentType}  and gradeNum={gradeNum}  and schoolNum={schoolNum} ) c  on  c.id=ce.classNum and c.schoolNum=ce.schoolNum where  c.id=ce.classNum and c.schoolNum=ce.schoolNum )clanum  left join (SELECT schoolNum,classNum from student where gradeNum={gradeNum}  and schoolNum={schoolNum} ) stu on stu.schoolNum=clanum.schoolNum and stu.classNum=clanum.classNum";
            Integer.valueOf(clasubcount.intValue() + oid.getNum().intValue());
        } else {
            studentcountsql = "select count(1) FROM (select ce.* ,c.className from (select distinct classNum,schoolNum from examinationnum where " + wSql + " )ce  left join (select id,className,schoolNum,gradeNum from class where studentType={studentType}  and gradeNum={gradeNum} ) c  on  c.id=ce.classNum and c.schoolNum=ce.schoolNum where  c.id=ce.classNum and c.schoolNum=ce.schoolNum )clanum  left join (SELECT schoolNum,classNum from student where gradeNum={gradeNum} ) stu on stu.schoolNum=clanum.schoolNum and stu.classNum=clanum.classNum";
        }
        Map args3 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put("pSubNum", (Object) pSubNum).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getClassNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType());
        String sstudentcount = this.dao._queryStr(studentcountsql, args3);
        Integer studentcount = Integer.valueOf(sstudentcount);
        if (oid.getNum().intValue() > studentcount.intValue() && String.valueOf(oid.getSubjectNum()).length() == 3) {
            return "1";
        }
        return "2";
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String maxcountyanzheng(OnlineIndicator oid) {
        String studentcountsql;
        String classNumsql = "";
        if (!"-1".equals(oid.getClassNum())) {
            classNumsql = "  and classNum={classNum} " + oid.getClassNum() + "'";
        }
        String pSubNum = getPSubNum(oid.getExamNum(), oid.getGradeNum(), oid.getSubjectNum());
        if (!"-1".equals(oid.getClassNum())) {
            if (oid.getFc().equals("T") && oid.getSubjectNum().intValue() != -1 && "T".equals(oid.getShowLevel())) {
                studentcountsql = "select count(1) from levelstudent where schoolNum={schoolNum}  and   gradeNum={gradeNum}  and subjectNum={subjectNum}  " + classNumsql + "";
            } else {
                studentcountsql = "select count(1) from student where schoolNum={schoolNum}   and   gradeNum={gradeNum}   " + classNumsql + "";
            }
        } else {
            Map args = StreamMap.create().put("examnum", (Object) oid.getExamNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum());
            this.dao._queryStr("select exampaperNum from exampaper where examnum={examnum} and gradeNum={gradeNum}  limit 1", args);
            studentcountsql = "select count(1) FROM (select ce.* ,c.className from (select distinct classNum,schoolNum from examinationnum where  examNum ={examNum} AND gradeNum ={gradeNum}  AND subjectNum ={pSubNum}    and schoolNum={schoolNum} )ce  left join (select id,className,schoolNum,gradeNum from class where studentType={studentType} and gradeNum={gradeNum}  and schoolNum={schoolNum} ) c  on  c.id=ce.classNum and c.schoolNum=ce.schoolNum where  c.id=ce.classNum and c.schoolNum=ce.schoolNum )clanum  left join (SELECT schoolNum,classNum from student where gradeNum={gradeNum} and schoolNum={schoolNum} ) stu on stu.schoolNum=clanum.schoolNum and stu.classNum=clanum.classNum";
        }
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put("pSubNum", (Object) pSubNum).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType());
        String sstudentcount = this.dao._queryStr(studentcountsql, args2);
        Integer studentcount = Integer.valueOf(sstudentcount);
        if (oid.getNum().intValue() > studentcount.intValue() && String.valueOf(oid.getSubjectNum()).length() == 3) {
            return "1";
        }
        return "2";
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String maxcountyanzhengScore(OnlineIndicator oid) {
        String scorecountsql;
        if (4 == oid.getSubjectNum().intValue() || (oid.getSubjectNum().intValue() >= 20 && oid.getSubjectNum().intValue() <= 99)) {
            scorecountsql = "select fullScore from studentlevel where examNum ={examNum}  and subjectNum ={subjectNum}  limit 1";
        } else if (0 == oid.getSubjectNum().intValue()) {
            scorecountsql = "select sum(ep.totalScore) from (select subjectNum,totalScore from exampaper where  examnum={examnum}   and gradenum={gradenum} )ep left join (select subjectNum from subject where  maintype='0')sub  on sub.subjectNum=ep.subjectNum where sub.subjectNum=ep.subjectNum";
        } else if (-1 == oid.getSubjectNum().intValue()) {
            scorecountsql = "select sum(totalScore) from exampaper where examNum={examNum}  and gradeNum={gradeNum}  and ishidden='F'";
        } else {
            scorecountsql = "select sum(totalScore) from exampaper where examNum={examNum}  and gradeNum={gradeNum}  and subjectNum={subjectNum} ";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) oid.getExamNum()).put(Const.EXPORTREPORT_subjectNum, (Object) oid.getSubjectNum()).put("examnum", (Object) oid.getExamNum()).put("gradenum", (Object) oid.getGradeNum());
        String sscorecount = this.dao._queryStr(scorecountsql, args);
        Double scorecount = Double.valueOf(sscorecount);
        if (Double.valueOf(oid.getScore()).doubleValue() > scorecount.doubleValue()) {
            return "1";
        }
        return "2";
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String typecountyanzheng(OnlineIndicator oid) {
        Integer typenum1 = oid.getNum();
        Integer type2 = Integer.valueOf(Integer.valueOf(oid.getType()).intValue() - 1);
        String sql2 = "\tselect num from OnlineIndicator   where examnum={examnum}  and schoolNum={schoolNum}  and   gradeNum={gradeNum}  and  subjectnum={subjectnum}   and classNum={classNum}  and studentType={studentType} and type={type2} and sxtype={sxtype}  and showLevel={showLevel} ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) oid.getClassNum()).put("examnum", (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put("subjectnum", (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type2", (Object) type2).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
        String stypenum = this.dao._queryStr(sql2, args);
        if (null == stypenum || "null".equals(stypenum) || "".equals(stypenum) || "0".equals(stypenum)) {
            return "2";
        }
        Integer typenum2 = Integer.valueOf(stypenum);
        if (typenum1.intValue() < typenum2.intValue()) {
            return "1";
        }
        return "2";
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String typecountyanzhengScore(OnlineIndicator oid) {
        Double typenum1 = Double.valueOf(oid.getScore());
        Double type2 = Double.valueOf(Double.valueOf(oid.getType()).doubleValue() - 1.0d);
        String sql2 = "\tselect Score from OnlineIndicator   where examnum={examnum}  and schoolNum={schoolNum}  and   gradeNum={gradeNum}   and  subjectnum={subjectnum}     and classNum={classNum}  and studentType={studentType} and type={type2}  and sxtype={sxtype} and showLevel={showLevel} ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) oid.getClassNum()).put("examnum", (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put("subjectnum", (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type2", (Object) type2).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
        String stypenum = this.dao._queryStr(sql2, args);
        if (null == stypenum || "null".equals(stypenum) || "".equals(stypenum) || "0".equals(stypenum)) {
            return "2";
        }
        Double typenum2 = Double.valueOf(stypenum);
        if (typenum1.doubleValue() >= typenum2.doubleValue()) {
            return "1";
        }
        return "2";
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String typecountyanzheng2(OnlineIndicator oid) {
        Integer typenum1 = oid.getNum();
        Integer type2 = Integer.valueOf(Integer.valueOf(oid.getType()).intValue() + 1);
        String sql2 = "\tselect num from OnlineIndicator   where examnum={examnum}  and schoolNum={schoolNum} and   gradeNum={gradeNum}   and  subjectnum={subjectnum}     and classNum={classNum}  and studentType={studentType} and type={type}  and sxtype={sxtype} and showLevel={showLevel} ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) oid.getClassNum()).put("examnum", (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put("subjectnum", (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type2", (Object) type2).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
        String stypenum = this.dao._queryStr(sql2, args);
        if (null == stypenum || "null".equals(stypenum) || "".equals(stypenum)) {
            return "2";
        }
        if ("0".equals(stypenum)) {
            return "3";
        }
        Integer typenum2 = Integer.valueOf(stypenum);
        if (typenum1.intValue() > typenum2.intValue()) {
            return "1";
        }
        return "2";
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String typecountyanzheng2Score(OnlineIndicator oid) {
        Double typenum1 = Double.valueOf(oid.getScore());
        Double type2 = Double.valueOf(Double.valueOf(oid.getType()).doubleValue() + 1.0d);
        String sql2 = "\tselect Score from OnlineIndicator   where examnum={examnum} and schoolNum={schoolNum}  and   gradeNum={gradeNum}    and  subjectnum={subjectnum}      and classNum={classNum}  and studentType={studentType} and type={type2}  and sxtype={sxtype}  and showLevel={showLevel} ";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_classNum, (Object) oid.getClassNum()).put("examnum", (Object) oid.getExamNum()).put(Const.EXPORTREPORT_schoolNum, (Object) oid.getSchoolNum()).put(Const.EXPORTREPORT_gradeNum, (Object) oid.getGradeNum()).put("subjectnum", (Object) oid.getSubjectNum()).put(Const.EXPORTREPORT_studentType, (Object) oid.getStudentType()).put("type2", (Object) type2).put("sxtype", (Object) oid.getSxtype()).put("showLevel", (Object) oid.getShowLevel());
        String stypenum = this.dao._queryStr(sql2, args);
        if (null == stypenum || "null".equals(stypenum) || "".equals(stypenum)) {
            return "2";
        }
        if ("0".equals(stypenum)) {
            return "3";
        }
        Double typenum2 = Double.valueOf(stypenum);
        if (typenum1.doubleValue() <= typenum2.doubleValue()) {
            return "1";
        }
        return "2";
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer getcountByOldexam(String oldexam, String jie, String schoolNum) {
        Map args = StreamMap.create().put("oldexam", (Object) oldexam).put("jie", (Object) jie).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryInt("SELECT count(id) FROM studentlevel WHERE examnum={oldexam}  AND jie={jie}  AND schoolNum={schoolNum}  LIMIT 0,1", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> gradeName(String testCenterId, String examNum) {
        Map args = StreamMap.create().put("testCenterId", (Object) testCenterId).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryBeanList("SELECT DISTINCT en.gradeNum,gra.gradeName FROM examinationnum en  LEFT JOIN basegrade gra ON gra.gradeNum = en.gradeNum  WHERE en.testingCentreId ={testCenterId}  AND en.examNum ={examNum}   order by en.gradeNum asc ", StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> gradeNameBySchool(String schoolNum, String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        return this.dao._queryBeanList("SELECT DISTINCT en.gradeNum,gra.gradeName FROM examinationnum en  LEFT JOIN basegrade gra ON gra.gradeNum = en.gradeNum  WHERE en.examNum = {examNum} and en.schoolNum = {schoolNum} order by en.gradeNum asc ", StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> gradeNameBySub(String testCenterId, String schoolNum, String examNum, String subjectNum) {
        String schSql = null == testCenterId ? " and en.schoolNum = {schoolNum} " : " and en.testingCentreId = {testCenterId} ";
        String subSql = "-1".equals(subjectNum) ? "" : " and en.subjectNum = {subjectNum} ";
        String sql = "SELECT DISTINCT en.gradeNum,gra.gradeName FROM examinationnum en  LEFT JOIN basegrade gra ON gra.gradeNum = en.gradeNum  WHERE en.examNum = {examNum} " + schSql + " " + subSql + " order by en.gradeNum asc ";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("testCenterId", testCenterId);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryBeanList(sql, StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getexamroomgradeList(String examNum, String school) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(License.SCHOOL, school);
        return this.dao._queryColList("SELECT DISTINCT gradeNum FROM examinationnum WHERE examNum={examNum} AND schoolNum={school} ORDER BY gradeNum*1", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String authorExampaperIlleagal(String examNum, String subjectNum, String gradeNum, String studentId, String schoolNum) {
        return this.examManageDAO.authorExampaperIlleagal(examNum, subjectNum, gradeNum, studentId, schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void addToCorrectstatus(String examNum, String subjectNum, String gradeNum, String examPaperNum, String schoolNum, String roomId) {
        this.examManageDAO.addToCorrectstatus(examNum, subjectNum, gradeNum, examPaperNum, schoolNum, roomId);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String querySubjectTypeName(String subjectType) {
        return this.examManageDAO.querySubjectTypeName(subjectType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Exam getexamroomAndexamineeLength(String exam) {
        Map args = new HashMap();
        args.put("exam", exam);
        return (Exam) this.dao._queryBean("SELECT examinationRoomLength,examineeLength FROM exam  WHERE examnum={exam} ", Exam.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List selectXianName() {
        return this.dao.queryBeanList("SELECT name,value,type FROM `data` WHERE type='21'", Data.class);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void updateXianName(String name, String value, String type) {
        Map args = new HashMap();
        args.put("name", name);
        args.put("value", value);
        args.put("type", type);
        this.dao._execute("UPDATE `data` SET name={name} WHERE `value`={value} AND type={type}", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer selectCountName(String name, String type) {
        Map args = new HashMap();
        args.put("name", name);
        args.put("type", type);
        return this.dao._queryInt("SELECT count(name) FROM `data` WHERE name={name} AND type={type} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> classList(String examNum, String gradeNum, String testCenter, int k, String jie) {
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("k", Integer.valueOf(k));
        return this.dao._queryBeanList("SELECT count(1) as count,c.classNum,className FROM examinationnum en LEFT JOIN class c ON en.classNum = c.id WHERE en.testingCentreId={testCenter} and en.examNum={examNum} and en.gradeNum = {gradeNum} group by c.classNum order  by c.classNum*1  asc limit {k},1", StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> gradeList(String examNum, String testCenter, String subjectNum) {
        String subSql = "-1".equals(subjectNum) ? "" : " and en.subjectNum = {subjectNum} ";
        String sql = "SELECT DISTINCT en.gradeNum,gra.gradeName,tc.testingCentreName FROM examinationnum en  LEFT JOIN grade gra ON gra.gradeNum = en.gradeNum  LEFT JOIN testingcentre tc ON tc.id = en.testingCentreId  WHERE en.examNum = {examNum} and en.testingCentreId = {testCenter} " + subSql;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("testCenter", testCenter);
        return this.dao._queryBeanList(sql, StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> classLengthList(String testCenter, String examNum, String gradeNum, String jie) {
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryBeanList("SELECT DISTINCT c.id,c.classNum FROM examinationnum en LEFT JOIN class c ON en.classNum = c.id WHERE en.testingCentreId={testCenter} and en.examNum={examNum} and en.gradeNum = {gradeNum} ", StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> classsheetList(String testCenter, String examNum, String gradeNum, int k, String jie) {
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("k", Integer.valueOf(k));
        return this.dao._queryBeanList("SELECT DISTINCT c.className FROM examinationnum en LEFT JOIN class c ON en.classNum = c.id WHERE en.testingCentreId={testCenter} and en.examNum={examNum} and en.gradeNum = {gradeNum} order  by c.classNum*1  asc limit {k} ,3", StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<StuExamInfo> zuoqianroomList(String user, String examNum, String school, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(License.SCHOOL, school);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryBeanList(" select  distinct  a.gradeNum,f.schoolName,d.examinationRoomNum,d.id,b.gradeName,d.examinationRoomName,e.examName    from  (select  * from  school) f  left  join  (select  distinct  schoolNum,gradeNum,examinationRoomNum,studentId,examineeNum  from  examinationnum  WHERE  examNum={examNum}  AND  schoolNum={school} and gradeNum={gradeNum} )a   on a.schoolNum=f.id  left  join  grade  b  on  a.schoolNum=b.schoolNum  and  a.gradeNum=b.gradeNum left join ( select  distinct  id,examinationRoomNum,examinationRoomName,gradeNum,examNum  from  examinationRoom  WHERE  examNum={examNum}  AND  schoolNum={school} and gradeNum={gradeNum}  ) d   ON   a.examinationRoomNum=d.id and  d.gradeNum=b.gradeNum left  join  (select  *  from  exam  where  examNum={examNum} )e  on  d.examNum=e.examNum order  by  a.gradeNum*1,d.examinationRoomNum*1 asc ", StuExamInfo.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getjie(String gradeNum, String schoolNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        return this.dao._queryStr("SELECT jie  FROM grade WHERE gradeNum={gradeNum} AND schoolNum={schoolNum} AND isDelete='F'", args);
    }

    public Student getStuId(String schoolNum, String gradeNum, String jie, String stuNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("jie", jie);
        args.put("stuNum", stuNum);
        return (Student) this.dao._queryBean("SELECT studentId,studentName FROM student WHERE schoolNum={schoolNum} AND gradeNum={gradeNum} AND jie={jie}  AND studentNum={stuNum} ", Student.class, args);
    }

    public Student getStuByNum(String stuNum) {
        Map args = new HashMap();
        args.put("stuNum", stuNum);
        return (Student) this.dao._queryBean("SELECT studentId,studentName,jie,classNum FROM student WHERE studentNum={stuNum} ", Student.class, args);
    }

    public String getStuId2(String stuId) {
        Map args = new HashMap();
        args.put("stuId", stuId);
        int aa = 0;
        if (null != this.dao._queryObject("SELECT id FROM student WHERE  studentId={stuId} ", args).toString() && !"".equals(this.dao._queryObject("SELECT id FROM student WHERE  studentId={stuId} ", args).toString()) && !"null".equals(this.dao._queryObject("SELECT id FROM student WHERE  studentId={stuId} ", args).toString())) {
            aa = this.dao._queryInt("SELECT id FROM student WHERE  studentId={stuId} ", args).intValue();
        }
        return String.valueOf(aa);
    }

    public String getStuOneClassNum(String stuId) {
        Map args = new HashMap();
        args.put("stuId", stuId);
        int aa = 0;
        if (null != this.dao._queryObject("SELECT classNum FROM student WHERE  studentId={stuId} ", args) && !"".equals(this.dao._queryObject("SELECT classNum FROM student WHERE  studentId={stuId} ", args)) && !"null".equals(this.dao._queryObject("SELECT classNum FROM student WHERE  studentId={stuId} ", args))) {
            aa = this.dao._queryInt("SELECT classNum FROM student WHERE  studentId={stuId} ", args).intValue();
        }
        return String.valueOf(aa);
    }

    public String getschNum(String schoolName) {
        Map args = new HashMap();
        args.put("schoolName", schoolName);
        return this.dao._queryStr("SELECT id FROM school WHERE schoolName={schoolName} ", args);
    }

    public String getgNum(String gradeName) {
        Map args = new HashMap();
        args.put("gradeName", gradeName);
        return this.dao._queryStr("SELECT gradeNum  FROM basegrade WHERE gradeName={gradeName} ", args);
    }

    public String getSubNum(String subjectName) {
        Map args = new HashMap();
        args.put("subjectName", subjectName);
        return this.dao._queryStr("SELECT subjectNum  FROM subject WHERE subjectName={subjectName} ", args);
    }

    public String getTcId(String examNum, String testingCentreName) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("testingCentreName", testingCentreName);
        return this.dao._queryStr("SELECT id  FROM testingcentre WHERE examNum={examNum}  AND testingCentreName = {testingCentreName} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void addOrEditExam1(Exam exam) {
        if (null == exam.getExamNum()) {
            this.dao.save(exam);
        } else {
            this.dao.update(exam);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String queryStuTypeName(String stuType) {
        return this.examManageDAO.queryStuTypeName(stuType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Exam getexamNum(String date) {
        Map args = new HashMap();
        args.put("date", date);
        return (Exam) this.dao._queryBean("SELECT * FROM exam WHERE insertDate={date} ", Exam.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void deleteAllByExamNum(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("DELETE FROM exampaper WHERE  examNum={examNum} ", args);
        this.dao._execute("DELETE FROM examlog WHERE examNum={examNum} ", args);
        this.dao._execute("DELETE FROM examsetting WHERE examNum={examNum} ", args);
        this.dao._execute("DELETE cexam FROM classexam cexam LEFT JOIN exampaper ex ON cexam.examPaperNum=ex.examPaperNum WHERE ex.examNum={examNum} ", args);
        this.dao._execute("DELETE FROM exam WHERE examNum={examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getresourceList(String userId) {
        String sql;
        if (String.valueOf(userId).equals("-1") || String.valueOf(userId).equals("-2")) {
            sql = "SELECT num resource FROM resource WHERE pnum='301'";
        } else {
            sql = "SELECT DISTINCT rr.resource  resource FROM (SELECT roleNum FROM userrole WHERE userNum={userId} ) r LEFT JOIN resourcerole rr ON r.roleNum=rr.roleNum  LEFT JOIN resource rs ON rr.resource=rs.num WHERE rs.pnum='301'  ORDER BY rr.resource*1";
        }
        Map args = new HashMap();
        args.put("userId", userId);
        return this.dao._queryBeanList(sql, Userrole.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void updateExamSourceSet(String examNum, String source, String isJoin, String user) {
        Map args = new HashMap();
        args.put("isJoin", isJoin);
        args.put("user", user);
        args.put("CurrentTime", DateUtil.getCurrentTime());
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("source", source);
        this.dao._execute("update examsourceset set isJoin={isJoin},updateUser={user} ,updateDate={CurrentTime}  where examNum={examNum} and source={source} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void dropT(String user) {
        String tname;
        if ("-1".equals(user)) {
            tname = "admin";
        } else {
            String tname2 = String.valueOf(user);
            tname = tname2.replace("-", "");
        }
        String dropTableSql = "DROP TABLE IF EXISTS temp_examroom" + tname;
        this.dao.execute(dropTableSql);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer getcountBybeforeexam(String oldexam, String gradeNum, String schoolNum) {
        Map args = new HashMap();
        args.put("oldexam", oldexam);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        int count1 = this.dao._queryInt("SELECT COUNT(1)  FROM examinationnum WHERE examNum={oldexam} AND schoolNum={schoolNum} AND gradeNum={gradeNum} LIMIT 0,1", args).intValue();
        int count = 0;
        if (count1 > 0) {
            count = 1;
        }
        return Integer.valueOf(count);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer getsuperfull(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("SCORE_EXCEPTION_UPFULLMARKS_update", "1");
        args.put("SCORE_EXCEPTION_UPFULLMARKS", "4");
        args.put("EXCEPTION_TYPE_default", "2");
        return this.dao._queryInt("SELECT COUNT(1) FROM score  s RIGHT JOIN illegal i ON s.examPaperNum={exampaperNum} AND i.examPaperNum={exampaperNum} AND i.studentId=s.studentId   WHERE s.examPaperNum={exampaperNum} AND (s.isException={SCORE_EXCEPTION_UPFULLMARKS_update} OR s.isException={SCORE_EXCEPTION_UPFULLMARKS} ) AND i.type={EXCEPTION_TYPE_default} AND continued='F' ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer getJudgeFinish(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        return this.dao._queryInt("SELECT count(1)  FROM  remark WHERE exampaperNum={exampaperNum} AND `status`='F'   AND `type`='1'", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer getChongPan(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        return this.dao._queryInt("SELECT count(DISTINCT questionNum,examPaperNum )  FROM  remark WHERE exampaperNum={exampaperNum} AND `status`='F'   AND `type`='2'", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void ywsetup(String exampaperNum, String type) {
        Map args = new HashMap();
        args.put("type", type);
        args.put("exampaperNum", exampaperNum);
        this.dao._execute("UPDATE exampaper SET type={type} WHERE (examPaperNum={exampaperNum} OR pexamPaperNum={exampaperNum} )", args);
        List examType = this.dao._queryArrayList("select DISTINCT type from exampaper where examnum = (select examNum from exampaper where examPaperNum = {exampaperNum} ) ORDER BY type ASC", args);
        if (1 == examType.size()) {
            this.dao._execute("UPDATE  exam set type ={type} where examnum = (select examNum from exampaper where examPaperNum = {exampaperNum}  )", args);
            return;
        }
        String lianjie = "";
        for (int i = 0; i < examType.size(); i++) {
            Object[] papertypeStr = examType.get(i);
            if (papertypeStr[0].equals("0")) {
                lianjie = "0";
            } else if (papertypeStr[0].equals("1")) {
                if ("0".equals(lianjie)) {
                }
                lianjie = lianjie + "1";
            } else if (papertypeStr[0].equals("3") && !"1".equals(lianjie) && !"0".equals(lianjie) && "01".equals(lianjie)) {
            }
        }
        this.dao._execute("UPDATE  exam set type ={type}  where examnum = (select examNum from exampaper where examPaperNum = {exampaperNum} )", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void subABsetup(String exampaperNum, String type) {
        Map args = new HashMap();
        args.put("type", type);
        args.put("exampaperNum", exampaperNum);
        this.dao._execute("UPDATE exampaper SET abtype={type} WHERE (examPaperNum={exampaperNum} OR pexamPaperNum={exampaperNum})", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void subMultisetup(String exampaperNum, String type) {
        Map args = new HashMap();
        args.put("type", type);
        args.put("exampaperNum", exampaperNum);
        this.dao._execute("UPDATE exampaper SET multipagestuinfo={type} WHERE (examPaperNum={exampaperNum} OR pexamPaperNum={exampaperNum} )", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void subABexamsetup(String exam, String type, String value) {
        Map args = new HashMap();
        args.put("value", value);
        args.put("exam", exam);
        String sql = "";
        if (type.equals("ab")) {
            sql = "UPDATE exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum SET ex.abtype={value}  WHERE ex.examNum={exam} ";
        } else if (type.equals("cal")) {
            sql = "UPDATE exampaperparameter expam LEFT JOIN exam ex ON expam.examNum=ex.examNum  SET expam.`value`={value}  WHERE  expam.examNum={exam}  ";
        } else if (type.equals("fenzu")) {
            sql = "UPDATE exampaper expam LEFT JOIN exam ex ON expam.examNum=ex.examNum  SET expam.fenzuyuejuan={value}  WHERE  expam.examNum={exam}  ";
            List list = this.dao._queryColList("SELECT expam.examPaperNum from exampaper expam LEFT JOIN exam ex ON expam.examNum=ex.examNum where expam.examNum={exam} ", args);
            for (int i = 0; i < list.size(); i++) {
                String sql2 = "update task set insertUser='-1' where examPaperNum ='" + list.get(i) + "' and `status`='F'";
                this.dao._execute(sql2, args);
            }
        } else if (type.equals("multi")) {
            sql = "UPDATE exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum SET ex.multipagestuinfo={value}  WHERE ex.examNum={exam} ";
        } else if (type.equals("tem")) {
            sql = "UPDATE exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum SET ex.templateType={value}  WHERE ex.examNum={exam} ";
        } else if (type.equals("yw")) {
            sql = "UPDATE exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum SET ex.type={value}  WHERE ex.examNum={exam} ";
            this.dao._execute("UPDATE  exam set type ={value}  where examnum = {exam} ", args);
        } else if ("en".equals(type)) {
            sql = "UPDATE exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum SET ex.paintMode={value}  WHERE ex.examNum={exam} ";
        }
        this.dao._execute(sql, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getgradeNumByName(String gradeName) {
        Map args = new HashMap();
        args.put("gradeName", gradeName);
        return this.dao._queryStr("SELECT gradeNum FROM basegrade WHERE gradeName={gradeName} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void subABgradesetup(String exam, String grade, String type, String value) {
        Map args = new HashMap();
        args.put("value", value);
        args.put("exam", exam);
        args.put("grade", grade);
        if (type.equals("ab")) {
            this.dao._execute("UPDATE exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum SET ex.abtype={value} WHERE ex.examNum={exam} AND ex.gradeNum={grade} ", args);
        }
        if (type.equals("tem")) {
            this.dao._execute("UPDATE exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum SET ex.templateType={value}  WHERE ex.examNum={exam}  AND ex.gradeNum={grade} ", args);
        }
        if (type.equals("cal")) {
            this.dao._execute("UPDATE exampaperparameter expam LEFT JOIN exam ex ON expam.examNum=ex.examNum  SET expam.`value`={value}  WHERE  expam.examNum={exam}   AND expam.gradeNum={grade} ", args);
        }
        if (type.equals("fenzu")) {
            List list = this.dao._queryColList("SELECT expam.examPaperNum from exampaper expam LEFT JOIN exam ex ON expam.examNum=ex.examNum where expam.examNum={exam}  AND expam.gradeNum={grade} ", args);
            for (int i = 0; i < list.size(); i++) {
                String sql2 = "update task set insertUser='-1' where examPaperNum ='" + list.get(i) + "' and `status`='F'";
                this.dao._execute(sql2, args);
            }
            this.dao._execute("UPDATE exampaper expam LEFT JOIN exam ex ON expam.examNum=ex.examNum  SET expam.fenzuyuejuan={value}  WHERE  expam.examNum={exam}   AND expam.gradeNum={grade} ", args);
        }
        if (type.equals("multi")) {
            this.dao._execute("UPDATE exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum SET ex.multipagestuinfo={value}  WHERE ex.examNum={exam}  AND ex.gradeNum={grade} ", args);
        }
        if (type.equals("ywg")) {
            this.dao._execute("UPDATE exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum SET ex.type={value}  WHERE ex.examNum={exam}  AND ex.gradeNum={grade} ", args);
            List examType = this.dao._queryArrayList("select DISTINCT type from exampaper where examnum = {exam} ", args);
            if (1 == examType.size()) {
                this.dao._execute("UPDATE  exam set type ={value}  where examnum = {exam} ", args);
            } else {
                this.dao._execute("UPDATE  exam set type ='2' where examnum = {exam} ", args);
            }
        }
        if ("en".equals(type)) {
            this.dao._execute("UPDATE exampaper ex LEFT JOIN exam e ON ex.examNum=e.examNum SET ex.paintMode={value}  WHERE ex.examNum={exam}  AND ex.gradeNum={grade} ", args);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void subtemplate_setup(String exampaperNum, String type) {
        Map args = new HashMap();
        args.put("type", type);
        args.put("exampaperNum", exampaperNum);
        this.dao._execute("UPDATE exampaper SET templateType={type}  WHERE examPaperNum={exampaperNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<String> getStuTypeByExamGradeSchNum(String examNum, String subjectNum, String schoolNum, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryColList("SELECT DISTINCT(d.`value`) from classexam ce   LEFT JOIN class c ON c.id = ce.classNum LEFT JOIN  examPaper ep on ce.examPaperNum=ep.examPaperNum  LEFT JOIN (SELECT `value`,`name` FROM `data` WHERE type='25')  d ON d.`value`= c.studentType WHERE ep.examNum={examNum} and ep.subjectNum={subjectNum} and ce.schoolNum={schoolNum} and ep.gradeNum={gradeNum} ", String.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void excuteExceptionNum(String examPaperNum) {
        Map args = new HashMap();
        args.put("SCORE_EXCEPTION_UPFULLMARKS_update", "1");
        args.put("examPaperNum", examPaperNum);
        args.put("SCORE_EXCEPTION_UPFULLMARKS", "4");
        this.dao._execute("UPDATE score SET isException={SCORE_EXCEPTION_UPFULLMARKS_update} WHERE examPaperNum={examPaperNum} AND isException={SCORE_EXCEPTION_UPFULLMARKS} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg jsssx(String examNum, String gradeNum, String sxtype, String showLevel) {
        if (sxtype.equals("0")) {
            try {
                Object[] params = {examNum, gradeNum, null, null, null, null, showLevel};
                this.dao.execute("CALL upperToLine_calScoreLine(?,?,?,?,?,?,?)", params);
                return new RspMsg(200, "计算成功！", null);
            } catch (Exception e) {
                e.printStackTrace();
                return new RspMsg(Const.height_500, "计算失败！", null);
            }
        }
        if (sxtype.equals("1")) {
            try {
                Object[] params2 = {examNum, gradeNum, null, null, null, null, showLevel};
                this.dao.execute("CALL doubleUpperToLine_calScoreLine(?,?,?,?,?,?,?)", params2);
                return new RspMsg(200, "计算成功！", null);
            } catch (Exception e2) {
                e2.printStackTrace();
                return new RspMsg(Const.height_500, "计算失败！", null);
            }
        }
        return new RspMsg(Const.height_500, "计算异常！", null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<String> getGradeByExamNum_new(String examNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao._queryColList("SELECT DISTINCT ex.gradeNum FROM exampaper ex LEFT JOIN grade gr ON ex.gradeNum=gr.gradeNum WHERE ex.examNum={examNum} AND ex.subjectNum={subjectNum} AND ex.isDelete='F' ", String.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<Integer, String> getGradeByExamNum_fp(String examNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao._queryOrderMap("SELECT DISTINCT gr.gradeNum,gr.gradeName FROM exampaper ex LEFT JOIN grade gr ON ex.gradeNum=gr.gradeNum WHERE ex.examNum={examNum} AND ex.subjectNum={subjectNum} AND ex.isDelete='F' AND gr.isDelete='F' order by gr.gradeNum desc", TypeEnum.IntegerString, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<String> getStuTypeByExamGradeSchNum_new(String examNum, String subjectNum, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryColList("SELECT DISTINCT(d.`value`) from classexam ce   LEFT JOIN class c ON c.id = ce.classNum LEFT JOIN  examPaper ep on ce.examPaperNum=ep.examPaperNum  LEFT JOIN (SELECT `value`,`name` FROM `data` WHERE type='25')  d ON d.`value`= c.studentType WHERE ep.examNum={examNum} and ep.subjectNum={subjectNum}  and ep.gradeNum={gradeNum} ", String.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<String> getSchoolByExamNum_new(String examNum, String subjectNum, String gradeNum, String stuType) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryColList("select DISTINCT sch.id schoolNum from school sch  LEFT JOIN grade gra on sch.id=gra.schoolNum LEFT JOIN exampaper exam on gra.gradeNum=exam.gradeNum where exam.examNum={examNum} ", String.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void subcal_setup(String exampaperNum, String type) {
        Map args = new HashMap();
        args.put("type", type);
        args.put("exampaperNum", exampaperNum);
        this.dao._execute("UPDATE exampaperparameter SET `value`={type}  WHERE examPaperNum={exampaperNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void subfenzu_setup(String exampaperNum, String type) {
        Map args = new HashMap();
        args.put("type", type);
        args.put("exampaperNum", exampaperNum);
        this.dao._execute("update task set insertUser='-1' where examPaperNum={exampaperNum}  and `status`='F'", args);
        this.dao._execute("UPDATE exampaper SET fenzuyuejuan={type} WHERE pexamPaperNum={exampaperNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void suben_setup(String exampaperNum, String type) {
        Map args = new HashMap();
        args.put("type", type);
        args.put("exampaperNum", exampaperNum);
        this.dao._execute("UPDATE exampaper SET paintMode={type}  WHERE (examPaperNum={exampaperNum}  OR pexamPaperNum={exampaperNum} )", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getexamjie(String examNum, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryStr("SELECT DISTINCT jie FROM exampaper WHERE examNum={examNum} AND gradeNum={gradeNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Exam> getallexam() {
        return this.dao.queryBeanList("select  examNum,examName,examDate,examType,isDelete,status,scoreModel,rule,examinationRoomLength,examineeLength,paperSize,paintMode,type,examineeInstructions,scanType,showTag,step,totalScore,insertUser,insertDate,updateUser,updateUser,updateDate,isMoreSchool from exam where isDelete ='F' ORDER BY examDate desc,insertDate DESC", Exam.class);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Subject> getallsubject() {
        return this.dao.queryBeanList("select id,subjectNum,subjectName,type,pid,isHidden,subjectType,mainType,insertUser,insertDate,updateUser,updateDate,isDelete,orderNum from subject where subjectNum  in(101,102,105,108,109) ", Subject.class);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Subject> getallsubject_zy(String exam) {
        Map args = new HashMap();
        args.put("exam", exam);
        return this.dao._queryBeanList("select sub.subjectNum,sub.subjectName,sub.type,sub.pid,sub.isHidden,sub.subjectType,sub.mainType,sub.orderNum from subject sub JOIN (  select DISTINCT subjectNum from arealevel where examNum ={exam}  )exa where sub.subjectNum = exa.subjectNum ORDER BY sub.orderNum", Subject.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Grade> getallgrade() {
        return this.dao.queryBeanList("select * from grade where schoolNum = '1'", Grade.class);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Grade> getallgradezy(String exam) {
        Map args = new HashMap();
        args.put("exam", exam);
        return this.dao._queryBeanList("select DISTINCT gra.gradeNum,gra.gradeName from grade gra JOIN ( SELECT DISTINCT gradeNum FROM studentlevel where examNum={exam}  )expa on gra.gradeNum = expa.gradeNum  ORDER BY gra.gradeNum", Grade.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<School> getSchoolScoreNum(String num) {
        Map args = new HashMap();
        args.put("num", num);
        return this.dao._queryBeanList("select sch.shortname schoolName,sch.id schoolNum from school sch JOIN( select DISTINCT schoolNum from gradelevel where examNum = {num}  )exa on sch.id=exa.schoolNum", School.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<ScoreCalden> getScoreCalden(String exam, String schoolId, String gradeNum) {
        Map args = new HashMap();
        args.put("schoolId", schoolId);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("exam", exam);
        List num = this.dao._queryBeanList("SELECT schoolNum from school where id={schoolId} ", School.class, args);
        ((School) num.get(0)).getSchoolNum();
        return this.dao._queryBeanList("SELECT sch.schoolName,exa.examinationRoomNum,exa.examineeNum,a.studentName ,a.classNum,a.studentId, stu2.subjectName, stu.totalScore,stu.fullscore fullScoreS  from (  select studentNum,id,studentName,classNum,studentId from student where schoolNum = {schoolId}   )a , (  select subjectNum,totalScore,studentId,fullscore ,examNUm,gradeNum,classNum  from studentlevel where gradeNum ={gradeNum}  and schoolNum = {schoolId} and examNum = {exam} and statisticType=0 and xuankezuhe='0' and source=0  ) stu ,(  select subjectNum,subjectName from subject where subjectNum in(101,102,105,108,109)  )stu2 ,(  select studentId,examinationRoomNum,examineeNum,examNUm,gradeNum,subjectNum,classNum from examinationnum where gradeNum ={gradeNum} and schoolNum = {schoolId} and examNum = {exam}  )exa,(  select schoolName from school where id ={schoolId}   )sch where  a.id = stu.studentId and stu2.subjectNum = stu.subjectNum and exa.studentId = stu.studentId  and exa.examNUm=stu.examNUm  and exa.gradeNum=stu.gradeNum and exa.subjectNum=stu.subjectNum  ORDER BY a.studentName", ScoreCalden.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getAllSubAnaly(String examNum, String gradeNum, String schoolNum, String subjectNum, String studentType, String type, String source, String sNum, String history) {
        List rtnlist = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ AllSubjectScoreAnaly_q1(?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, type);
                pstat.setString(6, studentType);
                pstat.setString(7, source);
                pstat.setString(8, sNum);
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

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg saveTestCenter(Testingcentre tc) {
        Map args = new HashMap();
        args.put("ExamNum", tc.getExamNum());
        args.put("TestingCentreNum", tc.getTestingCentreNum());
        args.put("TestingCentreName", tc.getTestingCentreName());
        args.put("Id", tc.getId());
        args.put("TestingCentreLocation", tc.getTestingCentreLocation());
        args.put("InsertUser", tc.getInsertUser());
        args.put("IsDelete", tc.getIsDelete());
        Object tcId = this.dao._queryObject("select id from testingcentre where examNum = {ExamNum} and testingCentreNum={TestingCentreNum} limit 1", args);
        if (null != tcId) {
            return new RspMsg(Const.height_500, "编号 " + tc.getTestingCentreNum() + " 的考点已经存在，请重新输入！", null);
        }
        Object tcid = this.dao._queryObject("SELECT id FROM testingcentre WHERE examNum = {ExamNum} AND testingCentreName = {TestingCentreName} limit 1", args);
        if (null != tcid) {
            return new RspMsg(Const.height_500, "名称 " + tc.getTestingCentreName() + " 的考点已经存在，请重新输入！", null);
        }
        this.dao._execute("insert into testingcentre(id,examNum,testingCentreNum,testingCentreName,testingCentreLocation,insertUser,insertDate,isDelete) VALUES({Id},{ExamNum},{TestingCentreNum},{TestingCentreName},{TestingCentreLocation},{InsertUser},now(),{IsDelete} ) on DUPLICATE KEY UPDATE examNum={ExamNum} ,testingCentreNum={TestingCentreNum} ,testingCentreName={TestingCentreName} ,testingCentreLocation={TestingCentreLocation} ,insertUser={InsertUser} ,insertDate=now(),isDelete={IsDelete} ", args);
        return new RspMsg(200, "添加成功！", null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg updateTestCenter(Testingcentre tc) {
        Map args = new HashMap();
        args.put("ExamNum", tc.getExamNum());
        args.put("TestingCentreName", tc.getTestingCentreName());
        args.put("Id", tc.getId());
        args.put("TestingCentreLocation", tc.getTestingCentreLocation());
        args.put("InsertUser", tc.getInsertUser());
        Object tcid = this.dao._queryObject("SELECT id FROM testingcentre WHERE examNum = {ExamNum} AND testingCentreName = {TestingCentreName} and id <> {Id} limit 1", args);
        if (null != tcid) {
            return new RspMsg(Const.height_500, "名称 " + tc.getTestingCentreName() + " 的考点已经存在，请重新输入！", null);
        }
        this.dao._execute("update testingcentre set testingCentreName={TestingCentreName},testingCentreLocation={TestingCentreLocation} ,insertUser={InsertUser} ,insertDate=now() where id={Id} ", args);
        return new RspMsg(200, "修改成功！", null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void saveexamRoom(String exam, String schoolNum, String gradeNum, String examroomNum, String testCenter, String examroomLocation, String subjectNum) {
        int aa = getexamNationLength(exam).intValue();
        DecimalFormat examroomDf = new DecimalFormat(creatStr(aa, '0'));
        int examroomNum2 = Integer.valueOf(examroomNum).intValue();
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("testCenter", testCenter);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        List examList = this.dao._queryBeanList("select examinationRoomNum from examinationroom where examNum = {exam} and testingCentreId={testCenter} and gradeNum={gradeNum} and subjectNum = {subjectNum} ", Examinationroom.class, args);
        for (int i = 0; i < examList.size(); i++) {
            Examinationroom room = (Examinationroom) examList.get(i);
            if (examroomDf.format(examroomNum2).equals(room.getExaminationRoomNum())) {
                throwException(examroomDf.format(examroomNum2) + "考场已经存在。请重新输入！");
            }
        }
        if ("".equals(gradeNum) || "null".equals(gradeNum) || gradeNum == null || "".equals(subjectNum) || "null".equals(subjectNum) || subjectNum == null) {
            throwException("当前考点下没有年级或者科目参加考试！");
        }
        String guid = GUID.getGUIDStr();
        Map args1 = new HashMap();
        args1.put("guid", guid);
        args1.put("examroomNum2", examroomDf.format(examroomNum2));
        args1.put("examroomNum2kaochang", examroomDf.format(examroomNum2) + "考场");
        args1.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args1.put("currentTimeMillis", Long.valueOf(System.currentTimeMillis()));
        args1.put("exam", exam);
        args1.put("testCenter", testCenter);
        args1.put("examroomLocation", examroomLocation);
        args1.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args1.put("examroomNum", examroomNum);
        args1.put("examroomNumkaochang", examroomNum + "考场");
        this.dao._execute("insert into examinationroom(id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,isDelete,examNum,testingCentreId,testLocation,subjectNum)\tVALUES({guid},{examroomNum2},{examroomNum2kaochang},{gradeNum},'1',{currentTimeMillis},'F',{exam},{testCenter},{examroomLocation},{subjectNum} )\ton DUPLICATE KEY UPDATE examinationRoomNum={examroomNum},examinationRoomName={examroomNumkaochang},gradeNum={gradeNum},examNum={exam} ,testingCentreId={testCenter} ,testLocation={examroomLocation} , subjectNum={subjectNum} ", args1);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer getexamNationLength(String exam) {
        Map args = new HashMap();
        args.put("exam", exam);
        Exam examSql = (Exam) this.dao._queryBean("SELECT examinationRoomLength FROM exam  WHERE examnum={exam} ", Exam.class, args);
        int uu = examSql.getExaminationRoomLength();
        return Integer.valueOf(uu);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getStatisticsData(String examNum, String subjectNum, String schoolNum, String gradeNum, String statisticType) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        return this.dao._queryArrayList("select sub.subjectName ,gra.numOfStudent,gra.average,FORMAT(gra.average/gra.fullScore*30,2) e1,highscoreNum,\tFORMAT(gra.highscoreNum/gra.numOfStudent,2) highscore,excellence,\tFORMAT(gra.excellence/gra.numOfStudent,2) excell,\tFORMAT(gra.excellence/gra.numOfStudent*30,2) e2,pass,\tFORMAT(gra.pass/gra.numOfStudent,2) passs,\tFORMAT(gra.pass/gra.numOfStudent*40,2) e3,lowScoreNum,\tFORMAT(gra.lowScoreNum/gra.numOfStudent,2) lowScore,\tgra.max,gra.min,\tFORMAT(gra.average/gra.fullScore,2) nandu,\tFORMAT(\tFORMAT(gra.average/gra.fullScore*30,2)+FORMAT(gra.excellence/gra.numOfStudent*30,2)+FORMAT(gra.pass/gra.numOfStudent*40,2)\t,2) e\tfrom gradelevel gra inner JOIN `subject` sub on sub.subjectNum = gra.subjectNum \twhere examNum  = {examNum}  and gradeNum ={gradeNum} and schoolNum ={schoolNum}  and statisticType = 0 and gra.xuankezuhe='0' and gra.xuankezuhe=0 and gra.source=0 order by sub.orderNum", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, String> getTestCenterMap(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryOrderMap("SELECT id,testingCentreName from testingcentre where examNum = {examNum} ORDER BY convert(testingCentreName using gbk)", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, String> getSchoolMap() {
        return this.dao._queryOrderMap("SELECT id,schoolName from school ", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getExampaperType(String examNum, String subjectNum, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryStr("select type from exampaper where examNum ={examNum} and subjectNum = {subjectNum} AND gradeNum={gradeNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getScanMsg(String examNum, String subjectNum, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryStr(" select  CONCAT('已扫描：',a.num,' 应扫描：',b.num*c.num,' 未裁切数：',d.num)  num from (\tselect COUNT(1) num from regexaminee sre LEFT JOIN exampaper expa on sre.examPaperNum = expa.examPaperNum\twhere  expa.examNum = {examNum} and  expa.subjectNum = {subjectNum} and expa.gradeNum = {gradeNum})a,(\tselect COUNT(1) num from examinationnum where examNum = {examNum} and subjectNum = {subjectNum} and gradeNum = {gradeNum})b,\t(select totalPage num  from exampaper where  examNum = {examNum} and  subjectNum = {subjectNum} and gradeNum = {gradeNum})c,\t(select COUNT(1) num from cantrecognized cat LEFT JOIN exampaper expa on cat.examPaperNum = expa.examPaperNum \twhere  expa.examNum = {examNum} and  expa.subjectNum = {subjectNum} and expa.gradeNum = {gradeNum})d", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object autoSetTesting(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String sqlString = " insert into testingcentre  (examNum,testingCentreNum,testingCentreName,testingCentreLocation,insertUser,InsertDate,isDelete)  select " + examNum + " examNum,LPAD(@i:=@i+1,3,0) testingCentreNum,concat('第',LPAD(@i,3,0),'考点') testingCentreName,testing.* from ( select DISTINCT sch.shortname testingCentreLocation,'-1' insertUser,NOW() InsertDate,'F' isDelete from exampaper examp LEFT JOIN classexam clase on examp.exampaperNum = clase.examPaperNum LEFT JOIN school sch on sch.id = clase.schoolNum where examp.examNum = {examNum} )testing";
        this.dao._execute("DELETE from testingcentre where examNum = {examNum} ", args);
        this.dao.execute(" set @i=00");
        return String.valueOf(this.dao._execute(sqlString, args));
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getScoreDetail(String examNum, String subjectNum, String classNum, String gradeNum, String schoolNum, String sType, String qType, String isJointStuType, String subCompose) {
        List list = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ c_export_subjectDetails(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, gradeNum);
                pstat.setString(3, subjectNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, sType);
                pstat.setString(7, qType);
                pstat.setString(8, isJointStuType);
                pstat.setString(9, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Object[] sc = new Object[16];
                    for (int i = 0; i < sc.length; i++) {
                        sc[i] = rs.getObject(i + 1);
                    }
                    list.add(sc);
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

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getScoreDetail2(String examNum, String subjectNum, String classNum, String gradeNum, String schoolNum, String sType, String qType, String isJointStuType, String subCompose, String source) {
        List list = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ c_export_subjectDetails2(?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, gradeNum);
                pstat.setString(3, subjectNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, sType);
                pstat.setString(7, qType);
                pstat.setString(8, isJointStuType);
                pstat.setString(9, subCompose);
                pstat.setString(10, source);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Object[] sc = new Object[17];
                    for (int i = 0; i < sc.length; i++) {
                        sc[i] = rs.getObject(i + 1);
                    }
                    list.add(sc);
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

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getScoreDetailQingda(String examNum, String subjectNum, String classNum, String gradeNum, String schoolNum, String sType, String qType, String isJointStuType, String subCompose) {
        List list = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ c_export_subjectDetails_qingda(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, gradeNum);
                pstat.setString(3, subjectNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, sType);
                pstat.setString(7, qType);
                pstat.setString(8, isJointStuType);
                pstat.setString(9, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Object[] sc = new Object[19];
                    for (int i = 0; i < sc.length; i++) {
                        sc[i] = rs.getObject(i + 1);
                    }
                    list.add(sc);
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

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getScoreDetailFc(String examNum, String subjectNum, String classNum, String gradeNum, String schoolNum, String sType, String qType, String isJointStuType, String subCompose) {
        List list = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ c_export_subjectDetails_level(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, gradeNum);
                pstat.setString(3, subjectNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, sType);
                pstat.setString(7, qType);
                pstat.setString(8, isJointStuType);
                pstat.setString(9, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Object[] sc = new Object[18];
                    for (int i = 0; i < sc.length; i++) {
                        sc[i] = rs.getObject(i + 1);
                    }
                    list.add(sc);
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

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getScoreDetailFc2(String examNum, String subjectNum, String classNum, String gradeNum, String schoolNum, String sType, String qType, String isJointStuType, String subCompose) {
        List list = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ c_export_subjectDetails_level2(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, gradeNum);
                pstat.setString(3, subjectNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, sType);
                pstat.setString(7, qType);
                pstat.setString(8, isJointStuType);
                pstat.setString(9, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Object[] sc = new Object[19];
                    for (int i = 0; i < sc.length; i++) {
                        sc[i] = rs.getObject(i + 1);
                    }
                    list.add(sc);
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

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getScoreDetailFcQingda(String examNum, String subjectNum, String classNum, String gradeNum, String schoolNum, String sType, String qType, String isJointStuType, String subCompose) {
        List list = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call /* shard_host_HG=Read */ c_export_subjectDetails_level_qingda(?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, gradeNum);
                pstat.setString(3, subjectNum);
                pstat.setString(4, schoolNum);
                pstat.setString(5, classNum);
                pstat.setString(6, sType);
                pstat.setString(7, qType);
                pstat.setString(8, isJointStuType);
                pstat.setString(9, subCompose);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                while (rs.next()) {
                    Object[] sc = new Object[21];
                    for (int i = 0; i < sc.length; i++) {
                        sc[i] = null == rs.getObject(i + 1) ? "" : rs.getObject(i + 1);
                    }
                    list.add(sc);
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

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Questionimage> getScoreLocation(String regId, String category, String rptSource) {
        StringBuffer epSql = new StringBuffer();
        epSql.append("select examNum,pexamPaperNum,IFNULL(jisuanType,'1') jisuanType from exampaper ");
        epSql.append("where examPaperNum={category}");
        Map args = new HashMap();
        args.put("category", category);
        Map<String, Object> epMap = this.dao._querySimpleMap(epSql.toString(), args);
        Object pexamPaperNum = epMap.get("pexamPaperNum");
        Object examNum = epMap.get(Const.EXPORTREPORT_examNum);
        String jisuanTypeVal = Convert.toStr(epMap.get("jisuanType"));
        String sql = "SELECT IFNULL(" + category + ",qci.exampaperNum) examPaperNum,d.id,d.pid questionNum,s.questionScore questionScore,d.fullScore fullScore,d.questionType questionType,IF(ill.exampaperType='B',d.answer_b,d.answer) answer,qi.scoreX ext1,qi.scoreY ext2,qi.scoreW scoreW,qi.scoreH scoreH,qi.questionX questionX,qi.questionY questionY,qi.questionW questionW,qi.questionH questionH,qci.img img,qi.page page,'0' type,d.questionNum questionName,s.id scoreId,qi.multiRects tag,s.studentId studentId,s.cross_page,qgms.makType num,if(e.examDate<bg.updateDate,'0',bg.studentReportShowItem) studentReportShowItem," + jisuanTypeVal + " jisuanType FROM questionimage qi INNER JOIN (SELECT id,questionNum,questionScore,studentId,examPaperNum,continued cross_page,gradeNum FROM score WHERE regId = {regId} UNION ALL SELECT id,questionNum,questionScore,studentId,examPaperNum,'F' cross_page,gradeNum FROM objectivescore WHERE regId = {regId} ) s ON qi.scoreId = s.id INNER JOIN (SELECT id,id pid,questionNum,fullScore,questionType,orderNum,answer,answer_b FROM define where (examPaperNum = {pexamPaperNum} ) UNION ALL SELECT id,pid,questionNum,fullScore,questionType,orderNum,answer,answer_b FROM subdefine where (examPaperNum = {pexamPaperNum} ) ) d ON d.id = s.questionNum LEFT JOIN (select studentId,examPaperNum,exampaperType from illegal where examPaperNum={pexamPaperNum} ) ill on ill.studentId = s.studentId and ill.examPaperNum = s.examPaperNum LEFT JOIN (select exampaperNum,scoreId,img from remarkimg where examPaperNum={pexamPaperNum} ) qci ON qci.scoreId = s.id LEFT JOIN (select groupNum,questionNum from questiongroup_question where exampaperNum={pexamPaperNum} ) qgq ON qgq.questionNum = s.questionNum LEFT JOIN (select groupNum,makType from questiongroup_mark_setting where exampaperNum={pexamPaperNum} ) qgms ON qgms.groupNum = qgq.groupNum LEFT JOIN basegrade bg ON bg.gradeNum = s.gradeNum LEFT JOIN exam e ON e.examNum={examNum} WHERE qi.questionX is not null AND qi.scoreX is not null AND qi.questionY is not null AND qi.scoreY is not null ";
        String tableStr = "butongji".equals(rptSource) ? "studentlevel_butongji" : "studentlevel";
        String sql2 = "SELECT ep.examPaperNum,ep.subjectNum,sub.subjectName,sl.totalScore ext3,sl.fullScore,sl.dengji,sl.oqts,sl.sqts,sum(if(alq.questionNum=-1,alq.fullScore,0)) oqtsFullScore,sum(if(alq.questionNum=200,alq.fullScore,0)) sqtsFullScore,reg.page page,if(ep.examPaperNum=ep.pexamPaperNum,0,1) ext2," + jisuanTypeVal + " jisuanType FROM (SELECT studentId,examPaperNum,page FROM regexaminee WHERE id = {regId} ) reg LEFT JOIN exampaper ep ON ep.pexamPaperNum = reg.examPaperNum LEFT JOIN " + tableStr + " sl ON sl.studentId = reg.studentId AND sl.examPaperNum = ep.examPaperNum and statisticType='0' and xuankezuhe='0' LEFT JOIN arealevel_question alq ON alq.examPaperNum = sl.examPaperNum AND alq.examNum = sl.examNum and (alq.questionNum=-1 or alq.questionNum=200) LEFT JOIN subject sub ON sub.subjectNum = ep.subjectNum group by ep.subjectNum ORDER BY ext2,sub.orderNum ";
        Map args1 = new HashMap();
        args1.put("regId", regId);
        args1.put("pexamPaperNum", pexamPaperNum);
        args1.put(Const.EXPORTREPORT_examNum, examNum);
        List<?> _queryBeanList = this.dao._queryBeanList(sql, Questionimage.class, args1);
        List<?> _queryBeanList2 = this.dao._queryBeanList(sql2, Questionimage.class, args1);
        Map<String, Object> regMap = this.dao._querySimpleMap("SELECT studentId,examPaperNum,page FROM regexaminee WHERE id = {regId}", args1);
        if (CollUtil.isNotEmpty(regMap)) {
            String page = Convert.toStr(regMap.get("page"));
            String examPaperNum = Convert.toStr(regMap.get("examPaperNum"));
            if ("1".equals(page)) {
                List<Map<String, Object>> scoreList = this.dao._queryMapList("select questionNum,questionScore from score where examPaperNum={examPaperNum} and studentId={studentId} and continued = 'F' ", TypeEnum.StringObject, regMap);
                List<Map<String, Object>> objectivescoreList = this.dao._queryMapList("select questionNum,questionScore from objectivescore where examPaperNum={examPaperNum} and studentId={studentId} ", TypeEnum.StringObject, regMap);
                List<Map<String, Object>> defineList = this.dao._queryMapList("select id,category from define where examPaperNum={examPaperNum} union all select id,category from subdefine where examPaperNum={examPaperNum} ", TypeEnum.StringObject, regMap);
                Map<String, List<Map<String, Object>>> epTodefineListMap = (Map) defineList.stream().collect(Collectors.groupingBy(m -> {
                    return Convert.toStr(m.get("category"));
                }));
                BigDecimal[] sqts_ep = {BigDecimal.valueOf(0L)};
                scoreList.forEach(obj -> {
                    sqts_ep[0] = sqts_ep[0].add(Convert.toBigDecimal(obj.get("questionScore"), BigDecimal.valueOf(0L)));
                });
                BigDecimal[] oqts_ep = {BigDecimal.valueOf(0L)};
                objectivescoreList.forEach(obj2 -> {
                    oqts_ep[0] = oqts_ep[0].add(Convert.toBigDecimal(obj2.get("questionScore"), BigDecimal.valueOf(0L)));
                });
                BigDecimal totalScore_ep = sqts_ep[0].add(oqts_ep[0]);
                _queryBeanList2.stream().filter(m2 -> {
                    return examPaperNum.equals(m2.getExamPaperNum());
                }).forEach(obj3 -> {
                    obj3.setSqts(Convert.toStr(sqts_ep[0]));
                    obj3.setOqts(Convert.toStr(oqts_ep[0]));
                    obj3.setExt3(Convert.toStr(totalScore_ep));
                });
                if (CollUtil.isNotEmpty(epTodefineListMap) && epTodefineListMap.size() > 1) {
                    epTodefineListMap.forEach((categoryKey, defineListOfOneCategory) -> {
                        BigDecimal[] sqts = {BigDecimal.valueOf(0L)};
                        BigDecimal[] oqts = {BigDecimal.valueOf(0L)};
                        defineListOfOneCategory.forEach(defineMap -> {
                            String id = Convert.toStr(defineMap.get("id"), "");
                            Optional<Map<String, Object>> res_s = scoreList.stream().filter(m3 -> {
                                return id.equals(Convert.toStr(m3.get("questionNum")));
                            }).findAny();
                            if (res_s.isPresent()) {
                                sqts[0] = sqts[0].add(Convert.toBigDecimal(res_s.get().get("questionScore"), BigDecimal.valueOf(0L)));
                                return;
                            }
                            Optional<Map<String, Object>> res_os = objectivescoreList.stream().filter(m4 -> {
                                return id.equals(Convert.toStr(m4.get("questionNum")));
                            }).findAny();
                            if (res_os.isPresent()) {
                                oqts[0] = oqts[0].add(Convert.toBigDecimal(res_os.get().get("questionScore"), BigDecimal.valueOf(0L)));
                            }
                        });
                        BigDecimal totalScore = sqts[0].add(oqts[0]);
                        _queryBeanList2.stream().filter(m3 -> {
                            return categoryKey.equals(m3.getExamPaperNum());
                        }).forEach(obj4 -> {
                            obj4.setSqts(Convert.toStr(sqts[0]));
                            obj4.setOqts(Convert.toStr(oqts[0]));
                            obj4.setExt3(Convert.toStr(totalScore));
                        });
                    });
                }
            }
        }
        List list = new ArrayList();
        list.addAll(_queryBeanList);
        list.addAll(_queryBeanList2);
        return list;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Questionimage> getQScoreLocation(String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        return this.dao._queryBeanList("SELECT s.exampaperNum examPaperNum ,s.questionScore questionScore,d.fullScore fullScore,d.questionType questionType,'-1' answer,(qi.scoreX-qi.questionX) ext1,0 ext2,qi.scoreW scoreW,qi.scoreH scoreH,0 questionX,0 questionY,qi.questionW questionW,qi.questionH questionH,qci.img img,'0' type,d.questionNum questionName,s.id scoreId,qi.multiRects tag,'F' cross_page,s.studentId studentId,d.id id,qgms.makType num,if(e.examDate<bg.updateDate,'0',bg.studentReportShowItem) studentReportShowItem,IFNULL(ep.jisuanType,'1') jisuanType FROM questionimage qi INNER JOIN (SELECT id,questionNum,questionScore,studentId,examPaperNum,gradeNum FROM score WHERE id = {scoreId} AND continued = 'F' UNION ALL SELECT id,questionNum,questionScore,studentId,examPaperNum,gradeNum FROM objectivescore WHERE id ={scoreId} ) s ON qi.scoreId = s.id INNER JOIN (SELECT id,questionNum,fullScore,questionType,orderNum FROM define UNION ALL SELECT id,questionNum,fullScore,questionType,orderNum FROM subdefine ) d ON d.id = s.questionNum LEFT JOIN remarkimg qci ON qci.scoreId = s.id LEFT JOIN questiongroup_question qgq ON qgq.questionNum = s.questionNum LEFT JOIN questiongroup_mark_setting qgms ON qgms.groupNum = qgq.groupNum LEFT JOIN basegrade bg ON bg.gradeNum = s.gradeNum LEFT JOIN exampaper ep ON ep.examPaperNum=s.examPaperNum LEFT JOIN exam e ON e.examNum=ep.examNum WHERE qi.questionX is not null AND qi.scoreX is not null AND qi.questionY is not null AND qi.scoreY is not null ", Questionimage.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Define> getHbcqQScoreLocation(String regId) {
        Map args = new HashMap();
        args.put("regId", regId);
        return this.dao._queryBeanList("SELECT subd.id from (SELECT id,examPaperNum FROM define where `merge` = '1') d LEFT JOIN (SELECT examPaperNum FROM regexaminee WHERE id = {regId}) reg on reg.examPaperNum = d.examPaperNum LEFT JOIN subdefine subd on subd.pid = d.id and subd.examPaperNum = reg.examPaperNum where subd.id is not null", Define.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getBulu(String examNum) {
        return this.examManageDAO.getBuLu(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, Object> getHeDuiNum(String exampaperNum) {
        return this.examManageDAO.getHeDuiNum(exampaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, Object> getShuangXiang(String examNum) {
        return this.examManageDAO.getShuangXiang(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, Object> getCaiQie(String examNum) {
        return this.examManageDAO.getCaiQie(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, Object> getSaoMiao(String examNum) {
        return this.examManageDAO.getSaoMiao(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int getExamMsgByChecked(String schoolNum, String gradeNum, String subjectNum, String classStr, int classType, String stuType) {
        return this.examManageDAO.getExamMsgByChecked(schoolNum, gradeNum, subjectNum, classStr, classType, stuType);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Student> getExamStuByChecked(String subjectNum, String gradeNum, String schoolNum, String classStr, String stuType) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("classStr", classStr);
        args.put("stuType", stuType);
        return this.dao._queryBeanList("SELECT stu.id,stu.gradeNum,stu.schoolNum,cla.studentType FROM levelstudent lstu LEFT JOIN student stu on lstu.sid = stu.id  left join class cla on cla.id = stu.classNum where lstu.schoolNum ={schoolNum} and lstu.gradeNum ={gradeNum} and lstu.subjectNum = {subjectNum} and lstu.classNum in ({classStr[]}) AND stu.type in ({stuType[]}) and stu.id is not null and cla.id is not null", Student.class, args);
    }

    public List<Student> getExamStuByCheckedOfGdkcFc(String subjectNum, String gradeNum, String schoolNum, String classStr, String stuType, String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("classStr", classStr);
        args.put("stuType", stuType);
        return this.dao._queryBeanList("SELECT stu.sid id,stu.examinationNumber studentId,stul.classNum,stu.classNum ext6 FROM student_examinationNumber stu  LEFT JOIN levelstudent stul ON stul.sid = stu.sid where stu.examNum = {examNum} and stul.schoolNum={schoolNum} AND stul.gradeNum={gradeNum} and stul.subjectNum={subjectNum} and stul.classNum in ({classStr[]}) and stu.type in ({stuType[]}) ORDER BY RAND()", Student.class, args);
    }

    public List<Studentlevel> getstuScoreListOfGdkcFc(String subjectNum, String examNum, String newexam, String classStr, String stuType, String sjt) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("sjt", sjt);
        args.put("newexam", newexam);
        args.put("stuType", stuType);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("classStr", classStr);
        return this.dao._queryBeanList("SELECT DISTINCT st.sid id,stlev.totalScore,st.examinationNumber ext1, lstu.classNum ext2,st.classNum ext6 FROM student_examinationNumber st left join levelstudent lstu on lstu.sid = st.sid LEFT JOIN studentlevel stlev ON stlev.studentId=st.sid and stlev.examNum = {examNum} and stlev.subjectNum = {sjt} AND stlev.statisticType='0' AND stlev.source='0' WHERE st.examNum = {newexam}  and st.type in ({stuType[]}) and lstu.subjectNum = {subjectNum} and lstu.classNum in ({classStr[]}) ORDER BY IFNULL(stlev.totalScore,0) DESC", Studentlevel.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Exam getExamStatus(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return (Exam) this.dao._queryBean("SELECT status FROM exam where examNum = {examNum} ", Exam.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getSubTotalScore(String examNum, String gradeNum, String subjectNum, String sType) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("sType", sType);
        return this.dao._queryStr("SELECT fullScore FROM gradelevel WHERE examNum = {examNum} AND gradeNum = {gradeNum} AND subjectNum = {subjectNum} AND studentType = {sType}  AND statisticType = '0' AND source = '0' and xuankezuhe='0' ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getFourRatiosData(String examNum, String gradeNum, String sType, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("sType", sType);
        return this.dao._queryBeanList("SELECT gfNum,gfScore,gfRatio,yxNum,yxScore,yxRatio,jgNum,jgScore,jgRatio,dfNum,dfScore,dfRatio FROM fourratios_setting WHERE examNum = {examNum} AND gradeNum = {gradeNum} AND subjectNum = {subjectNum} AND studentType = {sType} ", Fourratios_setting.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getNumAndRatio(String examNum, String gradeNum, String sType, String subjectNum, String score, String index) {
        String scoreSql;
        if ("df".equals(index)) {
            scoreSql = "AND totalScore <= {score} ";
        } else {
            scoreSql = "AND totalScore >= {score} ";
        }
        String sql = "SELECT COUNT(1) FROM studentlevel WHERE examNum = {examNum} AND gradeNum = {gradeNum} AND subjectNum = {subjectNum} AND studentType = {sType} AND statisticType = '0' AND source = '0' and xuankezuhe='0' " + scoreSql + "UNION ALL SELECT SUM(numOfStudent) FROM gradelevel WHERE examNum = {examNum} AND gradeNum = {gradeNum} AND subjectNum = {subjectNum} AND studentType = {sType} AND statisticType = '0' AND source = '0' and xuankezuhe='0' ";
        Map args = new HashMap();
        args.put("score", score);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("sType", sType);
        return this.dao._queryColList(sql, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int updateFourRatios(String examNum, String gradeNum, String sType, String subjectNum, String score, String num, String ratio, String index, String insertUser) {
        String scoreStr = index + "Score";
        String numStr = index + "Num";
        String ratioStr = index + "Ratio";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("sType", sType);
        args.put("score", score);
        args.put("num", num);
        args.put("ratio", ratio);
        args.put("insertUser", insertUser);
        if (null == this.dao._queryObject("SELECT id FROM fourratios_setting WHERE examNum = {examNum} AND gradeNum = {gradeNum} AND subjectNum = {subjectNum} AND studentType = {sType} ", args)) {
            String sql = "INSERT INTO fourratios_setting (examNum,gradeNum,subjectNum,studentType," + scoreStr + Const.STRING_SEPERATOR + numStr + Const.STRING_SEPERATOR + ratioStr + ",insertUser,insertDate,updateUser,updateDate) VALUES ({examNum},{gradeNum},{subjectNum},{sType},{score},{num},{ratio},{insertUser},now(),{insertUser},now())";
            this.dao._execute(sql, args);
        } else {
            String sql2 = "UPDATE fourratios_setting SET " + scoreStr + "={score}," + numStr + "={num}," + ratioStr + "={ratio} WHERE examNum = {examNum} AND gradeNum = {gradeNum} AND subjectNum = {subjectNum} AND studentType = {sType} ";
            this.dao._execute(sql2, args);
        }
        return this.dao.execute("DELETE FROM fourratios_setting WHERE gfScore is null AND yxScore is null AND jgScore is null AND dfScore is null");
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void calculateFourRatios(String examNum, String subjectNum, String gradeNum, String sType, String insertUser) {
        try {
            Object[] params = {examNum, subjectNum, gradeNum, sType, insertUser};
            this.dao.execute("CALL sub_count_fourratios_data(?,?,?,?,?)", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Exam> getExamList_leq(String userId) {
        String sql = "SELECT examNum,examName FROM exam WHERE isDelete = 'F' ORDER BY examDate DESC,insertDate DESC";
        Map args = new HashMap();
        args.put("userId", userId);
        if (!userId.equals("-1") && !userId.equals("-2")) {
            sql = "SELECT DISTINCT e.examNum,e.examName from exam e LEFT JOIN examschool es ON e.examNum=es.examNum INNER JOIN( select schoolNum from schauthormanage where userId = {userId} and isDelete = 'F' union select schoolNum from user where id = {userId} and isDelete = 'F' ) sam on CAST(sam.schoolNum AS CHAR) = CAST(es.schoolNum AS CHAR) WHERE e.isDelete='F' ORDER BY e.updateDate DESC";
        }
        return this.dao._queryBeanList(sql, Exam.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<AjaxData> getStypeList_leq(String examNum, String gradeNum) {
        Map args = new HashMap();
        args.put("data_subjectType", Const.data_subjectType);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryBeanList("SELECT DISTINCT g.studentType num,d.name name FROM gradelevel g LEFT JOIN data d ON d.type = {data_subjectType} AND d.value=g.studentType WHERE g.examNum = {examNum} and g.gradeNum ={gradeNum} ", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Subject> getSubjectList_leq(String examNum, String gradeNum, String sType) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("sType", sType);
        return this.dao._queryBeanList("SELECT DISTINCT gl.subjectNum,sub.subjectName FROM gradelevel gl LEFT JOIN subject sub ON sub.subjectNum = gl.subjectNum WHERE gl.examNum = {examNum}  AND gl.gradeNum = {gradeNum} AND studentType = {sType} ORDER BY sub.orderNum", Subject.class, args);
    }

    public String getPSubNum(Integer examNum, Integer gradeNum, Integer subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao._queryStr("SELECT ep1.subjectNum FROM exampaper ep1 INNER JOIN exampaper ep2 ON ep1.examPaperNum = ep2.pexampaperNum WHERE ep2.examNum = {examNum} AND ep2.gradeNum = {gradeNum} AND ep2.subjectNum = {subjectNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getQuoteExam(String schoolNum, String examNum, String gradeNum, int xiancount, String sxtype) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("sxtype", sxtype);
        return this.dao._queryArrayList("SELECT aa.examNum,aa.studentType,aa.subjectNum,aa.num,e.examName FROM (SELECT examNum,studentType,subjectNum,COUNT(classNum)-1 num FROM onlineindicator WHERE examNum <> {examNum} AND gradeNum = {gradeNum} AND schoolNum = {schoolNum} AND sxtype = {sxtype} AND type = '0' AND showLevel = 'F' GROUP BY examNum,studentType,subjectNum ORDER BY num DESC) aa LEFT JOIN exam e ON e.examNum = aa.examNum GROUP BY aa.examNum,aa.studentType ORDER BY e.examDate DESC,e.insertDate DESC", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getQuoteClass(String schoolNum, String examNum, String gradeNum, String subjectNum, String sxtype, String sType) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("sType", sType);
        args.put("sxtype", sxtype);
        return this.dao._queryColList("SELECT classNum FROM onlineindicator WHERE examNum = {examNum} AND subjectNum = {subjectNum} AND gradeNum = {gradeNum} AND schoolNum = {schoolNum} AND type = '0' AND studentType = {sType} AND sxtype = {sxtype} AND showLevel = 'F'", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void deleteSsxData(String schoolNum, String examNum, String gradeNum, int xiancount, String sxtype) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("xiancount", Integer.valueOf(xiancount));
        args.put("sxtype", sxtype);
        this.dao._execute("DELETE FROM onlineindicator WHERE examNum = {examNum} AND gradeNum = {gradeNum} AND schoolNum = {schoolNum} AND type < {xiancount} AND sxtype = {sxtype} AND showLevel = 'F'", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer quote(String checkExam, String examNum, Integer subjectNum, String gradeNum, String schoolNum, Integer xiancount, String sType, String sxtype, String user) {
        String sql = "INSERT INTO onlineindicator (examNum,schoolNum,gradeNum,subjectNum,classNum,studentType,type,num,insertUser,insertDate,updateUser,updateDate,sxtype,scoretype,showLevel,xuankezuhe,statisticType,source,fenshuyuan) SELECT " + examNum + ",schoolNum,gradeNum,subjectNum,classNum,studentType,type,num," + user + ",NOW()," + user + ",NOW(),sxtype,'0','F',xuankezuhe,statisticType,source,fenshuyuan FROM onlineindicator WHERE examNum = {checkExam} AND subjectNum = {subjectNum} AND gradeNum = {gradeNum} AND schoolNum = {schoolNum} AND type < {xiancount} AND studentType = {sType} AND sxtype = {sxtype} AND showLevel = 'F'";
        Map args = new HashMap();
        args.put("checkExam", checkExam);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("xiancount", xiancount);
        args.put("sType", sType);
        args.put("sxtype", sxtype);
        return Integer.valueOf(this.dao._execute(sql, args));
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getComputeTime(String exam, String grade, String subject) {
        String subjectStr;
        String examStr = "";
        if (exam != null && !exam.equals("")) {
            examStr = " and examNum = {exam} ";
        }
        String gradeStr = "";
        if (grade != null && !grade.equals("")) {
            gradeStr = " and gradeNum = {grade} ";
        }
        if (subject != null && !subject.equals("") && !subject.equals("-1")) {
            subjectStr = " and subjectNum = {subject} ";
        } else {
            subjectStr = " and isSub='0'";
        }
        String sql = "select if(insertDate is null,'',insertDate) insertDate from gradelevel where 1 = 1 " + examStr + gradeStr + subjectStr;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        List list = this.dao._queryColList(sql, args);
        String insertDate = "";
        if (list.size() > 0) {
            insertDate = list.get(0) + "";
        }
        if (insertDate.equals("") && subject != null && !subject.equals("") && !subject.equals("-1")) {
            String sql2 = "select subjectNum from exampaper where pexampaperNum=(select exampaperNum from exampaper where 1=1 " + examStr + gradeStr + subjectStr + ")";
            List<Map<String, Object>> list2 = this.dao._queryMapList(sql2, null, args);
            for (Map map : list2) {
                String sql3 = "select if(insertDate is null,'',insertDate) insertDate from gradelevel where subjectNum= " + map.get(Const.EXPORTREPORT_subjectNum) + gradeStr + examStr;
                String insertDate1 = this.dao._queryStr(sql3, args);
                if (StrUtil.isNotEmpty(insertDate1)) {
                    insertDate = insertDate1;
                }
            }
        }
        return insertDate;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String insertAppealDate(String exam, String grade, String subject, String deadline, String appealId) {
        String sql;
        String deadline2 = (null == deadline || "".equals(deadline.trim())) ? null : deadline.trim();
        if (null == deadline2) {
            sql = "update exampaper ep2 left join exampaper ep on ep.examPaperNum=ep2.pexamPaperNum set ep2." + appealId + "=NULL where ep.examNum={exam} and ep.gradeNum={grade} and ep.subjectNum={subject}";
        } else {
            sql = "update exampaper ep2 left join exampaper ep on ep.examPaperNum=ep2.pexamPaperNum set ep2." + appealId + "={deadline} where ep.examNum={exam} and ep.gradeNum={grade} and ep.subjectNum={subject}";
        }
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("deadline", deadline2);
        int count = this.dao._execute(sql, args);
        if (count > 0) {
            return "T";
        }
        return "F";
    }

    public List<RegExaminee> getBlankPaper_old(String exam, String subject, String testCenter, String grade, String examroom, String low_score, String height_score, String stuNumOrName, int currentPaper, int cacheStuNumber, String tab) {
        String tcOsSql = "";
        String tcSSql = "";
        String erOsSql = "";
        String erSSql = "";
        String stuSql = "";
        String scoreSql = "";
        if (!"-1".equals(testCenter)) {
            tcOsSql = " AND os.testingCentreId = {testCenter} ";
            tcSSql = " AND s.testingCentreId = {testCenter} ";
        }
        if (!"-1".equals(examroom)) {
            erOsSql = " AND os.examinationRoomNum = {examroom} ";
            erSSql = " AND s.examinationRoomNum = {examroom} ";
        }
        if (null != stuNumOrName && !"".equals(stuNumOrName)) {
            stuSql = " AND (stu.studentName LIKE {stuNumOrName} OR en.examineeNum = {stuNumOrName} OR stu.studentId = {stuNumOrName} ) ";
        }
        if (null != low_score && !"".equals(low_score) && null != height_score && !"".equals(height_score)) {
            scoreSql = " HAVING totalScore BETWEEN {low_score} AND {height_score} ";
        }
        String commonSql = "SELECT res2.examPaperNum,res2.studentId,res2.totalScore ext1,reg.id,res2.totalPage page,IFNULL(bpr.status,'0') status ,res2.studentName,res2.className as ext2,res2.examineeNum as ext3,res2.type as ext4 FROM (SELECT res1.examPaperNum,res1.studentId,SUM(res1.questionScore) totalScore,res1.totalPage ,res1.studentName,res1.className,res1.examineeNum,res1.type FROM (SELECT os.examPaperNum,os.studentId,SUM(os.questionScore) questionScore,ep.totalPage,stu.studentName,class.className,en.examineeNum,i.type FROM objectivescore os LEFT JOIN exampaper ep ON ep.examPaperNum = os.examPaperNum LEFT JOIN examinationnum en ON en.studentId = os.studentId AND en.examNum = ep.examNum AND en.gradeNum = ep.gradeNum AND en.subjectNum = ep.subjectNum LEFT JOIN student stu ON os.studentId = stu.id LEFT JOIN class ON stu.classNum = class.id LEFT JOIN illegal i ON i.studentId = stu.id and i.examPaperNum = ep.examPaperNum WHERE ep.examNum = {exam} AND ep.gradeNum = {grade} AND ep.subjectNum = {subject} " + tcOsSql + erOsSql + stuSql + "GROUP BY os.studentId UNION all SELECT s.examPaperNum,s.studentId,SUM(s.questionScore) questionScore,ep.totalPage,stu.studentName,class.className,en.examineeNum,i.type FROM score s LEFT JOIN exampaper ep ON ep.examPaperNum = s.examPaperNum LEFT JOIN examinationnum en ON en.studentId = s.studentId AND en.examNum = ep.examNum AND en.gradeNum = ep.gradeNum AND en.subjectNum = ep.subjectNum LEFT JOIN student stu ON s.studentId = stu.id LEFT JOIN class ON stu.classNum = class.id LEFT JOIN illegal i ON i.studentId = stu.id and i.examPaperNum = ep.examPaperNum WHERE ep.examNum = {exam} AND ep.gradeNum = {grade} AND ep.subjectNum = {subject} " + tcSSql + erSSql + stuSql + "GROUP BY s.studentId ) res1 GROUP BY res1.studentId " + scoreSql + ") res2 LEFT JOIN regexaminee reg ON reg.examPaperNum = res2.examPaperNum AND reg.studentId = res2.studentId LEFT JOIN illegal ill ON ill.examPaperNum = res2.examPaperNum AND ill.studentId = res2.studentId ";
        if ("0".equals(tab)) {
            commonSql = commonSql + "LEFT JOIN blankpaper_record bpr ON bpr.regId = reg.id AND bpr.status = '1' WHERE ill.type=2 and reg.scan_import='0' AND bpr.id IS NULL ";
        } else if ("1".equals(tab)) {
            commonSql = commonSql + "RIGHT JOIN blankpaper_record bpr ON bpr.regId = reg.id WHERE ill.type=2 and reg.scan_import='0' AND bpr.status = '1' ";
        } else if ("-1".equals(tab)) {
            commonSql = commonSql + "LEFT JOIN blankpaper_record bpr ON bpr.regId = reg.id WHERE ill.type=2 and reg.scan_import='0' ";
        }
        String countSql = "SELECT count(1) ext3,res3.page FROM (" + commonSql + " GROUP BY res2.studentId) res3 ";
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put("examroom", examroom);
        args.put("stuNumOrName", stuNumOrName);
        args.put("low_score", low_score);
        args.put("height_score", height_score);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("currentPaper", Integer.valueOf(currentPaper));
        args.put("cacheStuNumber", Integer.valueOf(cacheStuNumber));
        Object totalPage = this.dao._queryObject("select totalPage from exampaper where examNum={exam} and subjectNum={subject} and gradeNum={grade} ", args);
        int totalPage1 = totalPage == null ? 0 : Integer.valueOf(totalPage.toString()).intValue();
        int i = cacheStuNumber * totalPage1;
        List _queryBeanList = this.dao._queryBeanList(commonSql + "ORDER BY res2.totalScore LIMIT {currentPaper},{cacheStuNumber} ", RegExaminee.class, args);
        RegExaminee reg0 = (RegExaminee) this.dao._queryBean(countSql, RegExaminee.class, args);
        _queryBeanList.add(reg0);
        return _queryBeanList;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getBlankPaper(String exam, String subject, String testCenter, String grade, String examroom, String low_score, String height_score, String stuNumOrName, int currentPaper, int cacheStuNumber, String tab, String tiantubi) {
        String examPaperNum = getExamPaperNum(exam, grade, subject);
        String tcSql = "-1".equals(testCenter) ? "" : " AND testingCentreId = {testCenter} ";
        String erSql = "-1".equals(examroom) ? "" : " AND examinationRoomNum = {examroom} ";
        String scoreSql = "-1".equals(height_score) ? "" : " HAVING totalScore>0 and totalScore <= {height_score} and description<{tiantubi} ";
        String stuEnSql = "";
        String stuSql = "";
        if (null != stuNumOrName && !"".equals(stuNumOrName)) {
            stuEnSql = "LEFT JOIN student stu ON stu.id = os.studentId LEFT JOIN examinationnum en ON en.studentId = os.studentId AND en.examNum = ep.examNum AND en.gradeNum = ep.gradeNum AND en.subjectNum = ep.subjectNum ";
            stuSql = " AND (stu.studentName LIKE {stuNumOrName} OR en.examineeNum = {stuNumOrName} OR stu.studentId = {stuNumOrName} ) ";
        }
        String commonSql = "SELECT os.examPaperNum,os.studentId,os.totalScore,os.keguantiScore,os.zhuguantiScore,ep.totalPage page,i.type,GROUP_CONCAT(reg.id ORDER BY reg.page) id FROM ( SELECT s.examPaperNum,s.studentId,sum(s.questionScore) totalScore,sum(if(s.qType='0',s.questionScore,0)) keguantiScore,sum(if(s.qType='1',s.questionScore,0)) zhuguantiScore,description FROM ( SELECT examPaperNum,studentId,questionScore,'0' qType,description from objectivescore where examPaperNum={examPaperNum} " + tcSql + erSql + " UNION ALL SELECT examPaperNum,studentId,questionScore,'1' qType,description from score where examPaperNum={examPaperNum} " + tcSql + erSql + ") s GROUP BY s.studentId " + scoreSql + ") os LEFT JOIN exampaper ep ON ep.examPaperNum = os.examPaperNum LEFT JOIN regexaminee reg ON reg.studentId = os.studentId and reg.examPaperNum = os.examPaperNum LEFT JOIN illegal i ON i.studentId = os.studentId AND i.examPaperNum = os.examPaperNum LEFT JOIN blankpaper_record bpr ON bpr.regId = reg.id " + stuEnSql + "WHERE ep.examNum = {exam} AND ep.gradeNum = {grade} AND ep.subjectNum = {subject} and reg.scan_import='0' " + stuSql;
        if ("0".equals(tab)) {
            commonSql = commonSql + " and i.type=2 and bpr.id IS NULL ";
        } else if ("1".equals(tab)) {
            commonSql = commonSql + " and (i.type=2 or i.type=3) and bpr.status = '1' ";
        } else if ("2".equals(tab)) {
            commonSql = commonSql + " and i.type=3 ";
        }
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        args.put("examroom", examroom);
        args.put("height_score", height_score);
        args.put("stuNumOrName", stuNumOrName);
        args.put("examPaperNum", examPaperNum);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("tiantubi", tiantubi);
        List<Map<String, Object>> list = this.dao._queryMapList(commonSql + "GROUP BY os.studentId ORDER BY os.totalScore ", TypeEnum.StringObject, args);
        return list;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int saveRecord(String regId, String userId) {
        Map args = new HashMap();
        args.put("regId", regId);
        args.put("userId", userId);
        Object result = this.dao._queryObject("SELECT id FROM blankpaper_record WHERE regId = {regId} AND status = '1'", args);
        if (null == result) {
            return this.dao._execute("INSERT INTO blankpaper_record (regId,status,insertUser,insertDate) VALUES ({regId},'1',{userId} ,now())", args);
        }
        return 0;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int setIllStatus(String regId, String illStatus) {
        Map args = StreamMap.create().put("illStatus", (Object) illStatus).put("regId", (Object) regId);
        return this.dao._execute("UPDATE illegal SET type = {illStatus} WHERE regId ={regId} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Testingcentre> getTestingcenterList(String examNum, String inputStr) {
        String strSql = "".equals(inputStr) ? "" : " and (testingCentreNum={inputStr} or testingCentreName like {inputStr}) ";
        String sql = "select id,testingCentreNum,testingCentreName,testingCentreLocation from testingcentre where examNum = {examNum} " + strSql + "order by testingCentreNum*1";
        Map args = new HashMap();
        args.put("inputStr", "%" + inputStr + "%");
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryBeanList(sql, Testingcentre.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<School> getSchoolsByTc(String examNum, String testingCentreId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("testingCentreId", testingCentreId);
        return this.dao._queryBeanList("select tcs.schoolNum id,sch.schoolName from (select schoolNum from testingcentre_school where examNum = {examNum} and testingCentreId = {testingCentreId}) tcs left join school sch on sch.id = tcs.schoolNum order by sch.schoolNum*1", School.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int autoTestingcentre(String examNum, String userId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        List<?> _queryBeanList = this.dao._queryBeanList("select id from testingcentre where examNum = {examNum} ", Testingcentre.class, args);
        List delSqls = new ArrayList();
        for (int i = 0; i < _queryBeanList.size(); i++) {
            delSqls.add("delete from examinationnum where testingCentreId = '" + ((Testingcentre) _queryBeanList.get(i)).getId() + "'");
            delSqls.add("delete from examinationroom where testingCentreId = '" + ((Testingcentre) _queryBeanList.get(i)).getId() + "'");
            delSqls.add("delete from testingcentre_school where testingCentreId = '" + ((Testingcentre) _queryBeanList.get(i)).getId() + "'");
            delSqls.add("delete from testingcentre where id = '" + ((Testingcentre) _queryBeanList.get(i)).getId() + "'");
        }
        if (delSqls.size() > 0) {
            this.dao.batchExecuteByLimit(delSqls, 4);
        }
        String tcSql = "insert into testingcentre (id,examNum,testingCentreNum,testingCentreName,testingCentreLocation,insertUser,insertDate,isDelete) select UUID_SHORT()," + examNum + ",sch.schoolNum,sch.schoolName,sch.schoolAddress," + userId + ",now(),sch.isDelete from school sch  left join examschool es on sch.id=es.schoolNum  where sch.isDelete = 'F' and es.examNum={examNum} and es.schoolNum is not null ";
        this.dao._execute(tcSql, args);
        String tcsSql = "insert into testingcentre_school (id,examNum,testingCentreId,schoolNum,insertUser,insertDate,updateUser,updateDate,isDelete) select UUID_SHORT(),tc.examNum,tc.id,sch.id," + userId + ",now()," + userId + ",now(),tc.isDelete from (select id,examNum,testingCentreNum,isDelete from testingcentre where examNum = {examNum} ) tc left join school sch on sch.schoolNum = tc.testingCentreNum ";
        Map args1 = new HashMap();
        args1.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._execute(tcsSql, args1);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<School> getLeftAllSchool(String leicengId, String leftInputStr) {
        String sql;
        String schNameSql = "";
        String stNameSql = "";
        if (!"".equals(leftInputStr)) {
            schNameSql = " and schoolName like {leftInputStr} ";
            stNameSql = " and sItemName like {leftInputStr} ";
        }
        if (null == leicengId || "".equals(leicengId)) {
            sql = "select id,schoolName from school where isDelete='F' " + schNameSql;
        } else {
            sql = "select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leicengId} " + stNameSql + " group by sItemId";
        }
        Map args = new HashMap();
        args.put("leftInputStr", "%" + leftInputStr + "%");
        args.put("leicengId", leicengId);
        return this.dao._queryBeanList(sql, School.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<School> getUnassignedSchool(String leicengId, String leftInputStr, String examNum, String testingCentreId) {
        String sql;
        String schNameSql = "";
        String stNameSql = "";
        if (!"".equals(leftInputStr)) {
            schNameSql = " and schoolName like {leftInputStr} ";
            stNameSql = " and sItemName like {leftInputStr} ";
        }
        if (null == leicengId || "".equals(leicengId)) {
            sql = "select sch.id,sch.schoolName from (select id,schoolName from school where isDelete='F' " + schNameSql + ") sch left join (select id,schoolNum from testingcentre_school where examNum = {examNum} and testingCentreId = {testingCentreId}) tcs on tcs.schoolNum = sch.id where tcs.id is null";
        } else {
            sql = "select ss.id,ss.schoolName from (select sItemId id,sItemName schoolName from statisticitem_school where statisticItem='01' and topItemId={leicengId} " + stNameSql + " group by sItemId) ss left join (select id,schoolNum from testingcentre_school where examNum = {examNum} and testingCentreId = {testingCentreId} ) tcs on tcs.schoolNum = ss.id where tcs.id is null";
        }
        Map args = new HashMap();
        args.put("leftInputStr", "%" + leftInputStr + "%");
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("testingCentreId", testingCentreId);
        args.put("leicengId", leicengId);
        return this.dao._queryBeanList(sql, School.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int addTCSchool(Testingcentre_school tcs) {
        Map args = new HashMap();
        args.put("Id", tcs.getId());
        args.put("ExamNum", tcs.getExamNum());
        args.put("TestingCentreId", tcs.getTestingCentreId());
        args.put("SchoolNum", tcs.getSchoolNum());
        args.put("InsertUser", tcs.getInsertUser());
        args.put("UpdateUser", tcs.getUpdateUser());
        args.put("IsDelete", tcs.getIsDelete());
        return this.dao._execute("insert into testingcentre_school (id,examNum,testingCentreId,schoolNum,insertUser,insertDate,updateUser,updateDate,isDelete) VALUES ({Id},{ExamNum},{TestingCentreId},{SchoolNum},{InsertUser},now(),{UpdateUser},now(),{IsDelete}) ON DUPLICATE KEY UPDATE examNum={ExamNum} ,testingCentreId={TestingCentreId} ,schoolNum={SchoolNum} ,updateUser={UpdateUser},updateDate=now() ,isDelete={IsDelete} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int delTCSchool(Testingcentre_school tcs) {
        Map args = new HashMap();
        args.put("ExamNum", tcs.getExamNum());
        args.put("TestingCentreId", tcs.getTestingCentreId());
        args.put("SchoolNum", tcs.getSchoolNum());
        return this.dao._execute("delete from testingcentre_school where examNum={ExamNum} and testingCentreId={TestingCentreId} and schoolNum={SchoolNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void delTestingcentre(String examNum, String testingCentreId) {
        StringBuffer enSql = new StringBuffer();
        enSql.append("delete from examinationnum ");
        enSql.append("where examNum={examNum}  and testingCentreId={testingCentreId} ");
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("testingCentreId", testingCentreId);
        this.dao._execute(enSql.toString(), args);
        StringBuffer erSql = new StringBuffer();
        erSql.append("delete from examinationroom ");
        erSql.append("where examNum={examNum}  and testingCentreId={testingCentreId} ");
        this.dao._execute(erSql.toString(), args);
        StringBuffer tcsSql = new StringBuffer();
        tcsSql.append("delete from testingcentre_school ");
        tcsSql.append("where examNum={examNum}  and testingCentreId={testingCentreId} ");
        this.dao._execute(tcsSql.toString(), args);
        StringBuffer tcSql = new StringBuffer();
        tcSql.append("delete from testingcentre ");
        tcSql.append("where id={testingCentreId} ");
        this.dao._execute(tcSql.toString(), args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void batchDelTestingcentre(Object[][] tcsParam, Object[][] tcParam) {
        this.dao.batchExecute("delete from examinationnum where examNum=? and testingCentreId=?", tcsParam);
        this.dao.batchExecute("delete from examinationroom where examNum=? and testingCentreId=?", tcsParam);
        this.dao.batchExecute("delete from testingcentre_school where examNum=? and testingCentreId=?", tcsParam);
        this.dao.batchExecute("delete from testingcentre where id=?", tcParam);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Testingcentre> getTestCenterMap(String examNum, String userId, String schoolNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        return this.dao._queryBeanList("SELECT distinct tc.id,tc.testingCentreName from testingcentre_school tcs inner join testingcentre tc on tc.id = tcs.testingCentreId where tcs.examNum = {examNum} and tcs.schoolNum = {schoolNum} ORDER BY tc.testingCentreNum*1", Testingcentre.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Testingcentre> getAuthTestCenterMap(String examNum, String userId) {
        String sql;
        if ("-1".equals(userId) || "-2".equals(userId)) {
            sql = "SELECT id,testingCentreName from testingcentre where examNum = {examNum} ORDER BY testingCentreNum*1";
        } else {
            sql = "select tcs.testingCentreId id,tc.testingCentreName from (select testingCentreId,schoolNum from testingcentre_school where examNum={examNum} and isDelete='F') tcs left join (select schoolNum from schauthormanage where userId = {userId} and isDelete = 'F' union select schoolNum from user where id = {userId} and isDelete = 'F') sam on sam.schoolNum = tcs.schoolNum left join testingcentre tc on tc.id = tcs.testingCentreId where sam.schoolNum is not null order by tc.testingCentreNum*1";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("userId", userId);
        return this.dao._queryBeanList(sql, Testingcentre.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Testingcentre_school> getTcSchList(String examNum, String userId) {
        String schSql = "";
        String schWSql = "";
        if (!"-1".equals(userId) && !"-2".equals(userId)) {
            schSql = " left join (select schoolNum from schauthormanage where userId = {userId} and isDelete = 'F' union select schoolNum from user where id = {userId} and isDelete = 'F') sam on sam.schoolNum = tcs.schoolNum ";
            schWSql = " and sam.schoolNum is not null ";
        }
        String sql = "select tc.testingCentreNum,tc.testingCentreName,sch.schoolNum,sch.schoolName from testingcentre_school tcs " + schSql + "left join testingcentre tc on tc.id = tcs.testingCentreId left join school sch on sch.id = tcs.schoolNum where tcs.examNum = {examNum} and tc.id is not null and sch.id is not null " + schWSql + "order by tc.testingCentreNum*1,sch.schoolNum*1";
        Map args = new HashMap();
        args.put("userId", userId);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryBeanList(sql, Testingcentre_school.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg checkStuEnFile(String examNum, File file, String copyFileName, String userId, String currentTime, String levelClass, String stuIdorNumV, String che) {
        Row row;
        String levelClass2;
        String specialCharStr_enRoomLocation;
        this.errorFlag = false;
        ExcelHelper excelHelper = new ExcelHelper(file);
        try {
            Workbook workbook = excelHelper.creatWorkbook();
            Sheet sheet = workbook.getSheetAt(0);
            Row row1 = sheet.getRow(0);
            int columnLen = row1.getPhysicalNumberOfCells();
            if (null != row1 && columnLen < 11) {
                return new RspMsg(410, "excel文件第一行的表头列数与导入模板不符合，请检查！", null);
            }
            String[] biaotous = {"考点", "学校", "年级", "班级", "ID号", "姓名", "考场号", "考场地点", "座位号", "考号", "科目"};
            String[] biaotous2 = {"考点", "学校", "年级", "班级名称", "ID号", "姓名", "考场号", "考场地点", "座位号", "准考证号", "科目"};
            for (int i = 0; i < 11; i++) {
                String value = row1.getCell(i).getStringCellValue();
                if (!value.equals(biaotous[i]) && !value.equals(biaotous2[i])) {
                    return new RspMsg(410, "excel文件第一行的表头的第" + i + "1与模版表头不符合，应改为【" + biaotous[i] + "】或者【" + biaotous2[i] + "】请检查！！！", null);
                }
            }
            CellStyle errorRowStyle = workbook.createCellStyle();
            errorRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            errorRowStyle.setFillForegroundColor((short) 10);
            CellStyle errorCellStyle = workbook.createCellStyle();
            errorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            errorCellStyle.setFillForegroundColor((short) 13);
            Set<String> seatSet = new HashSet<>();
            Set<String> stuSet = new HashSet<>();
            Set<String> stuEnSet = new HashSet<>();
            Set<String> stuGraSubSet = new HashSet<>();
            Set<String> enSet = new HashSet<>();
            Map<String, String> testingCentreNameMap = new HashMap<>();
            Set<String> schoolNameSet = new HashSet<>();
            Set<String> graSubSet = new HashSet<>();
            Map<Integer, String> stuGraMap = new HashMap<>();
            Map<String, String> tcGraSubErMap = new HashMap<>();
            Map args = new HashMap();
            args.put(Const.EXPORTREPORT_examNum, examNum);
            args.put("userId", userId);
            Exam eLength = (Exam) this.dao._queryBean("SELECT examinationRoomLength,examineeLength FROM exam  WHERE examnum={examNum} ", Exam.class, args);
            Map<String, Object> ismanageMap = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
            for (int i2 = 1; i2 < sheet.getPhysicalNumberOfRows() && null != (row = sheet.getRow(i2)); i2++) {
                this.rowBgColor = false;
                String testingCentreId = "";
                Cell testingCentreNameCell = row.getCell(0);
                String testingCentreName = "";
                if (null == testingCentreNameCell) {
                    testingCentreNameCell = row.createCell(0);
                } else {
                    testingCentreName = CheckCellUtil.getCellValue(testingCentreNameCell);
                }
                if ("".equals(testingCentreName) || "ERROR".equals(testingCentreName)) {
                    setError(file, sheet, row, testingCentreNameCell, "考点不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else if (!testingCentreNameMap.containsKey(testingCentreName)) {
                    Map args1 = new HashMap();
                    args1.put(Const.EXPORTREPORT_examNum, examNum);
                    args1.put("testingCentreName", testingCentreName);
                    Object tcId = this.dao._queryObject("select id from testingcentre where examNum ={examNum} and testingCentreName = {testingCentreName}  limit 1", args1);
                    testingCentreId = String.valueOf(tcId);
                    testingCentreNameMap.put(testingCentreName, testingCentreId);
                    if (null == tcId) {
                        String pizhu = "系统中当前考试没有 " + testingCentreName + " 这个考点";
                        setError(file, sheet, row, testingCentreNameCell, pizhu, columnLen, errorRowStyle, errorCellStyle);
                    } else if (!"-1".equals(userId) && !"-2".equals(userId) && null == ismanageMap) {
                        Map args2 = new HashMap();
                        args2.put("examNu", examNum);
                        args2.put("testingCentreId", testingCentreId);
                        args2.put("userId", userId);
                        if (null == this.dao._queryObject("select tcs.id from (select id,schoolNum from testingcentre_school where examNum = {examNu} and testingCentreId = {testingCentreId}) tcs inner join (select schoolNum from schoolscanpermission where userNum = {userId} union select schoolNum from user where id = {userId} and isDelete = 'F') sam on sam.schoolNum = tcs.schoolNum limit 1", args2)) {
                            String pizhu2 = "您没有 " + testingCentreName + " 这个考点的操作权限";
                            setError(file, sheet, row, testingCentreNameCell, pizhu2, columnLen, errorRowStyle, errorCellStyle);
                        }
                    }
                } else {
                    testingCentreId = testingCentreNameMap.get(testingCentreName);
                }
                Cell studentIdCell = row.getCell(4);
                String studentId = "";
                if (null == studentIdCell) {
                    studentIdCell = row.createCell(4);
                } else {
                    studentId = CheckCellUtil.getCellValue(studentIdCell);
                }
                String nameStr = "ID号";
                String sqlStr = Const.EXPORTREPORT_studentId;
                if ("1".equals(stuIdorNumV)) {
                    nameStr = "学号";
                    sqlStr = "studentNum";
                }
                List<?> list = null;
                if ("".equals(studentId) || "ERROR".equals(studentId)) {
                    String pizhu3 = nameStr + "不能为空";
                    setError(file, sheet, row, studentIdCell, pizhu3, columnLen, errorRowStyle, errorCellStyle);
                } else {
                    String sql = "select s.id,s.schoolNum,sch.schoolName,s.gradeNum,gra.gradeName,s.studentName from student s left join school sch on sch.id = s.schoolNum left join basegrade gra on gra.gradeNum = s.gradeNum where s." + sqlStr + " = {studentId} and s.isDelete = 'F' ";
                    Map args3 = new HashMap();
                    args3.put(Const.EXPORTREPORT_studentId, studentId);
                    list = this.dao._queryBeanList(sql, Student.class, args3);
                    if ("1".equals(stuIdorNumV) && list.size() > 1) {
                        String pizhu4 = "系统中 " + studentId + " 这个学号有" + list.size() + "个，不能唯一确定学生身份，请选择ID号进行导入";
                        setError(file, sheet, row, studentIdCell, pizhu4, columnLen, errorRowStyle, errorCellStyle);
                    }
                    if (null == list || list.size() <= 0) {
                        String pizhu5 = "系统中不存在" + nameStr + " " + studentId + " 的基础信息";
                        setError(file, sheet, row, studentIdCell, pizhu5, columnLen, errorRowStyle, errorCellStyle);
                    }
                }
                Student s = null;
                String sId = "";
                String gradeNum = "";
                String jie = "";
                if (null != list && list.size() > 0) {
                    s = (Student) list.get(0);
                    sId = String.valueOf(s.getId());
                    gradeNum = String.valueOf(s.getGradeNum());
                    jie = getjie(String.valueOf(s.getGradeNum()), String.valueOf(s.getSchoolNum()));
                }
                Cell schoolNameCell = row.getCell(1);
                String schoolName = "";
                if (null == schoolNameCell) {
                    schoolNameCell = row.createCell(1);
                } else {
                    schoolName = CheckCellUtil.getCellValue(schoolNameCell);
                }
                if ("".equals(schoolName) || "ERROR".equals(schoolName)) {
                    setError(file, sheet, row, schoolNameCell, "学校不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else if (schoolNameSet.add(schoolName)) {
                    Map args4 = new HashMap();
                    args4.put("schoolName", schoolName);
                    args4.put("userId", userId);
                    args4.put(Const.EXPORTREPORT_examNum, examNum);
                    args4.put("testingCentreId", testingCentreId);
                    Object schId = this.dao._queryObject("select id from school where schoolName = {schoolName} limit 1", args4);
                    args4.put("sschId", String.valueOf(schId));
                    if (null == schId) {
                        String pizhu6 = "系统中没有 " + schoolName + " 这个学校";
                        setError(file, sheet, row, schoolNameCell, pizhu6, columnLen, errorRowStyle, errorCellStyle);
                    } else {
                        if (!"-1".equals(userId) && !"-2".equals(userId) && null == ismanageMap && null == this.dao._queryObject("select sam.schoolNum from (select schoolNum from schoolscanpermission where userNum = {userId} union select schoolNum from user where id = {userId} and isDelete = 'F') sam where sam.schoolNum = {sschId} ", args4)) {
                            String pizhu7 = "您没有 " + schoolName + " 这个学校的操作权限";
                            setError(file, sheet, row, schoolNameCell, pizhu7, columnLen, errorRowStyle, errorCellStyle);
                        }
                        if (!"".equals(testingCentreId) && null == this.dao._queryObject("select id from testingcentre_school where examNum = {examNum} and testingCentreId = {testingCentreId} and schoolNum = {sschId} limit 1", args4)) {
                            String pizhu8 = "考点 " + testingCentreName + " 下不包含 " + schoolName + " 这个学校，请检查";
                            setError(file, sheet, row, schoolNameCell, pizhu8, columnLen, errorRowStyle, errorCellStyle);
                        }
                        if (null == s) {
                            String pizhu9 = "请先核对" + nameStr + " " + studentId + " 的学生的基础信息，然后保持学校信息一致";
                            setError(file, sheet, row, schoolNameCell, pizhu9, columnLen, errorRowStyle, errorCellStyle);
                        } else if (!schoolName.equals(s.getSchoolName())) {
                            String pizhu10 = "学校 " + schoolName + " 与" + nameStr + " " + studentId + " 的学生的基础信息中的学校信息不一致，请检查";
                            setError(file, sheet, row, schoolNameCell, pizhu10, columnLen, errorRowStyle, errorCellStyle);
                        }
                    }
                }
                Cell gradeNameCell = row.getCell(2);
                String gradeName = "";
                if (null == gradeNameCell) {
                    gradeNameCell = row.createCell(2);
                } else {
                    gradeName = CheckCellUtil.getCellValue(gradeNameCell);
                }
                Boolean gradeF = true;
                if ("".equals(gradeName) || "ERROR".equals(gradeName)) {
                    gradeF = false;
                    setError(file, sheet, row, gradeNameCell, "年级不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else if (null == s) {
                    gradeF = false;
                    String pizhu11 = "请先核对" + nameStr + " " + studentId + " 的学生的基础信息，然后保持年级信息一致";
                    setError(file, sheet, row, gradeNameCell, pizhu11, columnLen, errorRowStyle, errorCellStyle);
                } else if (!gradeName.equals(s.getGradeName())) {
                    gradeF = false;
                    String pizhu12 = "年级 " + gradeName + " 与" + nameStr + " " + studentId + " 的学生的基础信息中的年级信息不一致，请检查";
                    setError(file, sheet, row, gradeNameCell, pizhu12, columnLen, errorRowStyle, errorCellStyle);
                }
                String subjectNum = "";
                Cell subjectNameCell = row.getCell(10);
                String subjectName = "";
                if (null == subjectNameCell) {
                    subjectNameCell = row.createCell(10);
                } else {
                    subjectName = CheckCellUtil.getCellValue(subjectNameCell);
                }
                if ("".equals(subjectName) || "ERROR".equals(subjectName)) {
                    setError(file, sheet, row, subjectNameCell, "科目不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else if (graSubSet.add(gradeName + "_" + subjectName)) {
                    Map args5 = new HashMap();
                    args5.put("subjectName", subjectName);
                    args5.put(Const.EXPORTREPORT_examNum, examNum);
                    args5.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                    args5.put("jie", jie);
                    Object res = this.dao._queryObject("select subjectNum from subject where subjectName = {subjectName} limit 1", args5);
                    if (null == res) {
                        String pizhu13 = "系统中没有 " + subjectName + " 这个科目";
                        setError(file, sheet, row, subjectNameCell, pizhu13, columnLen, errorRowStyle, errorCellStyle);
                    } else if (!"".equals(jie) && !"null".equals(jie)) {
                        subjectNum = String.valueOf(res);
                        args5.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                        String examJie = this.dao._queryStr("select jie from exampaper where examNum = {examNum} and gradeNum = {gradeNum} and subjectNum = {subjectNum} and isHidden = 'F'", args5);
                        if (StrUtil.isEmpty(examJie)) {
                            String pizhu14 = "年级【" + gradeName + "】科目【" + subjectName + "】没有参加考试，请在编辑考试中检查";
                            setError(file, sheet, row, subjectNameCell, pizhu14, columnLen, errorRowStyle, errorCellStyle);
                        } else if (!jie.equals(examJie)) {
                            String pizhu15 = "当前" + gradeName + "是" + jie + "级，创建考试时是" + examJie + "级，请联系管理员";
                            setError(file, sheet, row, gradeNameCell, pizhu15, columnLen, errorRowStyle, errorCellStyle);
                        }
                    }
                }
                Cell classNameCell = row.getCell(3);
                String className = "";
                if (null == classNameCell) {
                    classNameCell = row.createCell(3);
                } else {
                    className = CheckCellUtil.getCellValue(classNameCell);
                }
                if (null == s) {
                    String pizhu16 = "请先核对" + nameStr + " " + studentId + " 的学生的基础信息，然后保持班级信息一致";
                    setError(file, sheet, row, classNameCell, pizhu16, columnLen, errorRowStyle, errorCellStyle);
                } else {
                    if (!stuGraMap.containsKey(s.getGradeNum())) {
                        levelClass2 = "F";
                        Object result0 = this.dao.queryObject("select getIsLevelClass(?,?,?,?)", new Object[]{examNum, s.getGradeNum(), "100", null});
                        if (null != result0) {
                            levelClass2 = result0.toString();
                        }
                        stuGraMap.put(s.getGradeNum(), levelClass2);
                    } else {
                        levelClass2 = stuGraMap.get(s.getGradeNum());
                    }
                    if (!"T".equals(levelClass2)) {
                        if ("".equals(className) || "ERROR".equals(className)) {
                            setError(file, sheet, row, classNameCell, "班级名称不能为空", columnLen, errorRowStyle, errorCellStyle);
                        } else if (!gradeF.booleanValue()) {
                            setError(file, sheet, row, classNameCell, "请先检查年级信息", columnLen, errorRowStyle, errorCellStyle);
                        } else {
                            String sql2 = "select cla.className from student s left join class cla on cla.id = s.classNum and cla.gradeNum = s.gradeNum and cla.schoolNum = s.schoolNum where s." + sqlStr + " = {studentId} and s.isDelete = 'F' ";
                            Map args6 = new HashMap();
                            args6.put(Const.EXPORTREPORT_studentId, studentId);
                            Object cla = this.dao._queryObject(sql2, args6);
                            if (!className.equals(String.valueOf(cla))) {
                                String pizhu17 = "班级名称 " + className + " 与" + nameStr + " " + studentId + " 的学生的基础信息中的班级信息不一致，请检查";
                                setError(file, sheet, row, classNameCell, pizhu17, columnLen, errorRowStyle, errorCellStyle);
                            }
                        }
                    }
                }
                Cell studentNameCell = row.getCell(5);
                String studentName = "";
                if (null == studentNameCell) {
                    studentNameCell = row.createCell(5);
                } else {
                    studentName = CheckCellUtil.getCellValue(studentNameCell);
                }
                String studentName2 = studentName.replace(" ", "");
                if ("".equals(studentName2) || "ERROR".equals(studentName2)) {
                    setError(file, sheet, row, studentNameCell, "姓名不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else if (null == s) {
                    String pizhu18 = "请先核对" + nameStr + " " + studentId + " 的学生的基础信息，然后保持学生姓名一致";
                    setError(file, sheet, row, studentNameCell, pizhu18, columnLen, errorRowStyle, errorCellStyle);
                } else if (!studentName2.equals(s.getStudentName())) {
                    String pizhu19 = "姓名 " + studentName2 + " 与" + nameStr + " " + studentId + " 的学生的基础信息中的学生姓名不一致，请检查";
                    setError(file, sheet, row, studentNameCell, pizhu19, columnLen, errorRowStyle, errorCellStyle);
                }
                String enRoomId = "";
                Cell enRoomCell = row.getCell(6);
                String enRoom = "";
                if (null == enRoomCell) {
                    enRoomCell = row.createCell(6);
                } else {
                    enRoom = CheckCellUtil.getCellValue(enRoomCell);
                }
                if ("".equals(enRoom) || "ERROR".equals(enRoom)) {
                    setError(file, sheet, row, enRoomCell, "考场号不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else {
                    String specialCharStr_enRoom = CheckCellUtil.isSpecialChar(enRoom);
                    if (null != specialCharStr_enRoom) {
                        String pizhu20 = "考场号含有特殊字符（" + specialCharStr_enRoom + "），请检查";
                        setError(file, sheet, row, enRoomCell, pizhu20, columnLen, errorRowStyle, errorCellStyle);
                    }
                    if ("1".equals(che)) {
                        if (enRoom.length() != eLength.getExaminationRoomLength()) {
                            setError(file, sheet, row, enRoomCell, "考场位数与考试设置的考场位数不一致，请检查", columnLen, errorRowStyle, errorCellStyle);
                        }
                    } else if (enRoom.length() > 20) {
                        setError(file, sheet, row, enRoomCell, "考场号长度不超过20个字符，请检查", columnLen, errorRowStyle, errorCellStyle);
                    }
                    if (!tcGraSubErMap.containsKey(testingCentreId + gradeNum + subjectNum + enRoom)) {
                        Map args7 = new HashMap();
                        args7.put(Const.EXPORTREPORT_examNum, examNum);
                        args7.put("testingCentreId", testingCentreId);
                        args7.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                        args7.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                        args7.put("enRoom", enRoom);
                        Object res2 = this.dao._queryObject("select id from examinationroom where examNum={examNum} and testingCentreId={testingCentreId} and gradeNum={gradeNum}  and subjectNum={subjectNum} and examinationRoomNum={enRoom} ", args7);
                        enRoomId = null == res2 ? "" : String.valueOf(res2);
                        tcGraSubErMap.put(testingCentreId + gradeNum + subjectNum + enRoom, enRoomId);
                    } else {
                        enRoomId = tcGraSubErMap.get(testingCentreId + gradeNum + subjectNum + enRoom);
                    }
                }
                Cell enRoomLocationCell = row.getCell(7);
                String enRoomLocation = "";
                if (null == enRoomLocationCell) {
                    enRoomLocationCell = row.createCell(7);
                } else {
                    enRoomLocation = CheckCellUtil.getCellValue(enRoomLocationCell);
                }
                if (!"".equals(enRoomLocation) && !"ERROR".equals(enRoomLocation) && (null != (specialCharStr_enRoomLocation = CheckCellUtil.isSpecialChar(enRoomLocation)) || enRoomLocation.length() > 50)) {
                    String pizhu21 = "考场地点含有特殊字符（" + specialCharStr_enRoomLocation + "）或长度超过50个字符，请检查";
                    setError(file, sheet, row, enRoomLocationCell, pizhu21, columnLen, errorRowStyle, errorCellStyle);
                }
                Cell seatCell = row.getCell(8);
                String seat = "";
                if (null == seatCell) {
                    seatCell = row.createCell(8);
                } else {
                    seat = CheckCellUtil.getCellValue(seatCell);
                }
                if ("".equals(seat) || "ERROR".equals(seat)) {
                    setError(file, sheet, row, seatCell, "座位号不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else {
                    Map args8 = new HashMap();
                    args8.put("sId", sId);
                    args8.put(Const.EXPORTREPORT_examNum, examNum);
                    args8.put("testingCentreId", testingCentreId);
                    args8.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                    args8.put("enRoomId", enRoomId);
                    args8.put("seat", seat);
                    if (null != this.dao._queryObject("SELECT id FROM examinationnum WHERE studentId <> {sId} AND examNum = {examNum} AND testingCentreId = {testingCentreId} AND subjectNum = {subjectNum} AND examinationRoomNum = {enRoomId} AND seatNum = {seat} ", args8)) {
                        String pizhu22 = "系统中 " + seat + " 座位号已被其他人使用";
                        setError(file, sheet, row, seatCell, pizhu22, columnLen, errorRowStyle, errorCellStyle);
                    } else {
                        String specialCharStr_seat = CheckCellUtil.isSpecialChar(seat);
                        if (null != specialCharStr_seat || seat.length() > 32) {
                            String pizhu23 = "座位号含有特殊字符（" + specialCharStr_seat + "）或长度超过32个字符，请检查";
                            setError(file, sheet, row, seatCell, pizhu23, columnLen, errorRowStyle, errorCellStyle);
                        }
                        if (!seatSet.add(testingCentreName + "_" + gradeName + "_" + subjectName + "_" + enRoom + "_" + seat)) {
                            String pizhu24 = "excel表中座位号 " + seat + " 重复，请检查";
                            setError(file, sheet, row, seatCell, pizhu24, columnLen, errorRowStyle, errorCellStyle);
                        }
                    }
                }
                Cell enNumCell = row.getCell(9);
                String enNum = "";
                if (null == enNumCell) {
                    enNumCell = row.createCell(9);
                } else {
                    enNum = CheckCellUtil.getCellValue(enNumCell);
                }
                if ("".equals(enNum) || "ERROR".equals(enNum)) {
                    setError(file, sheet, row, enNumCell, "准考证号不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else {
                    String specialCharStr_enNum = CheckCellUtil.isSpecialChar(enNum);
                    if ("1".equals(che)) {
                        if (enNum.length() != eLength.getExamineeLength()) {
                            setError(file, sheet, row, enNumCell, "准考证号长度与考试设置的考号位数不一致，请检查", columnLen, errorRowStyle, errorCellStyle);
                        }
                    } else if (null != specialCharStr_enNum || enNum.length() > 20) {
                        String pizhu25 = "准考证号含有特殊字符（" + specialCharStr_enNum + "）或长度超过20个字符";
                        setError(file, sheet, row, enNumCell, pizhu25, columnLen, errorRowStyle, errorCellStyle);
                    }
                    ServletContext context = ServletActionContext.getServletContext();
                    String sameExamineeNum = (String) context.getAttribute(Const.sameExamineeNum);
                    if ("T".equals(sameExamineeNum)) {
                        Map args9 = new HashMap();
                        args9.put("sId", sId);
                        args9.put(Const.EXPORTREPORT_examNum, examNum);
                        if (!enNum.equals(this.dao._queryStr("SELECT examineeNum FROM examinationnum WHERE studentId = {sId} AND examNum = {examNum}  limit 1", args9))) {
                            setError(file, sheet, row, enNumCell, "准考证号与其已经导入过学科的准考证号不一致，请检查", columnLen, errorRowStyle, errorCellStyle);
                        }
                        if (!stuSet.add(studentId) && stuEnSet.add(studentId + "_" + enNum)) {
                            setError(file, sheet, row, enNumCell, "excel中当前学生的准考证号不唯一，请检查", columnLen, errorRowStyle, errorCellStyle);
                        }
                    }
                    Map args10 = new HashMap();
                    args10.put("enNum", enNum);
                    args10.put(Const.EXPORTREPORT_examNum, examNum);
                    args10.put("testingCentreId", testingCentreId);
                    args10.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                    args10.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                    args10.put("sId", sId);
                    if (null != this.dao._queryObject("SELECT id FROM examinationnum WHERE examineeNum = {enNum} and examNum={examNum} AND testingCentreId={testingCentreId} AND gradeNum={gradeNum} AND subjectNum={subjectNum} and studentId <> {sId} limit 1", args10)) {
                        String pizhu26 = "系统中准考证号 " + enNum + " 已被其他人使用";
                        setError(file, sheet, row, enNumCell, pizhu26, columnLen, errorRowStyle, errorCellStyle);
                    }
                    if (!enSet.add(enNum + "_" + testingCentreName + "_" + gradeName + "_" + subjectName)) {
                        String pizhu27 = "excel表中准考证号 " + enNum + " 重复，请检查";
                        setError(file, sheet, row, enNumCell, pizhu27, columnLen, errorRowStyle, errorCellStyle);
                    }
                    if (!"".equals(studentId) && !"ERROR".equals(studentId) && !stuGraSubSet.add(studentId + "_" + gradeName + "_" + subjectName)) {
                        String pizhu28 = "excel表中 " + gradeName + subjectName + "学生重复，请检查";
                        setError(file, sheet, row, studentIdCell, pizhu28, columnLen, errorRowStyle, errorCellStyle);
                    }
                }
            }
            if (this.errorFlag) {
                FileOutputStream fOut = new FileOutputStream(file);
                workbook.write(fOut);
                fOut.flush();
                fOut.close();
                return new RspMsg(Const.height_500, "导入文件信息错误", null);
            }
            return new RspMsg(200, "导入文件信息正确", null);
        } catch (Exception e) {
            return new RspMsg(410, "check error:" + e.getMessage(), null);
        }
    }

    /* JADX WARN: Type inference failed for: r0v50, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v58, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v66, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v74, types: [java.lang.Object[], java.lang.Object[][]] */
    @Override // com.dmj.service.examManagement.ExamManageService
    public void importStuEn(String examNum, File file, String copyFileName, String userId, String currentTime, String levelClass, String stuIdorNumV, String che) {
        Row row;
        String testingCentreId;
        String subjectNum;
        ExcelHelper excelHelper = new ExcelHelper(file);
        Workbook workbook = excelHelper.creatWorkbook();
        Sheet sheet = workbook.getSheetAt(0);
        List<Object[]> insertErParams = new ArrayList<>();
        List<Object[]> updateErParams = new ArrayList<>();
        List<Object[]> insertEnParams = new ArrayList<>();
        List<Object[]> updateEnParams = new ArrayList<>();
        Map<String, EDataParameters> erMap = new HashMap<>();
        Map<String, EDataParameters> enMap = new HashMap<>();
        Map<String, String> testingCentreNameMap = new HashMap<>();
        Map<String, String> subjectNameMap = new HashMap<>();
        String currentTime2 = DateUtil.getCurrentTime();
        int len = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < len && null != (row = sheet.getRow(i)); i++) {
            String erId = GUID.getGUIDStr();
            Cell testingCentreNameCell = row.getCell(0);
            String testingCentreName = CheckCellUtil.getCellValue(testingCentreNameCell);
            if (testingCentreNameMap.containsKey(testingCentreName)) {
                testingCentreId = testingCentreNameMap.get(testingCentreName);
            } else {
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                args.put("testingCentreName", testingCentreName);
                testingCentreId = this.dao._queryStr("select id from testingcentre where examNum = {examNum} and testingCentreName = {testingCentreName} ", args);
                testingCentreNameMap.put(testingCentreName, testingCentreId);
            }
            Cell schoolNameCell = row.getCell(1);
            CheckCellUtil.getCellValue(schoolNameCell);
            Cell gradeNameCell = row.getCell(2);
            CheckCellUtil.getCellValue(gradeNameCell);
            Cell classNameCell = row.getCell(3);
            CheckCellUtil.getCellValue(classNameCell);
            Cell studentIdCell = row.getCell(4);
            String studentId = CheckCellUtil.getCellValue(studentIdCell);
            String sqlStr = Const.EXPORTREPORT_studentId;
            if ("1".equals(stuIdorNumV)) {
                sqlStr = "studentNum";
            }
            String sql3 = "select s.id,s.schoolNum,sch.schoolName,s.gradeNum,gra.gradeName,s.classNum,cla.className,s.studentName from student s left join school sch on sch.id = s.schoolNum left join basegrade gra on gra.gradeNum = s.gradeNum left join class cla on cla.id = s.classNum and cla.gradeNum = gra.gradeNum and cla.schoolNum = sch.id where s." + sqlStr + " = {studentId} and s.isDelete = 'F' ";
            Map args1 = new HashMap();
            args1.put(Const.EXPORTREPORT_studentId, studentId);
            Student stu = (Student) this.dao._queryBean(sql3, Student.class, args1);
            String stuId = String.valueOf(stu.getId());
            String schoolNum = String.valueOf(stu.getSchoolNum());
            String gradeNum = String.valueOf(stu.getGradeNum());
            String classNum = String.valueOf(stu.getClassNum());
            getjie(gradeNum, schoolNum);
            Cell studentNameCell = row.getCell(5);
            CheckCellUtil.getCellValue(studentNameCell);
            Cell erNumCell = row.getCell(6);
            String erNum = CheckCellUtil.getCellValue(erNumCell);
            Cell erLocationCell = row.getCell(7);
            String erLocation = CheckCellUtil.getCellValue(erLocationCell);
            if ("".equals(erLocation) || "ERROR".equals(erLocation)) {
                erLocation = "";
            }
            Cell seatCell = row.getCell(8);
            String seat = CheckCellUtil.getCellValue(seatCell);
            Cell enNumCell = row.getCell(9);
            String enNum = CheckCellUtil.getCellValue(enNumCell);
            Cell subjectNameCell = row.getCell(10);
            String subjectName = CheckCellUtil.getCellValue(subjectNameCell);
            if (subjectNameMap.containsKey(subjectName)) {
                subjectNum = subjectNameMap.get(subjectName);
            } else {
                Map args2 = new HashMap();
                args2.put("subjectName", subjectName);
                subjectNum = this.dao._queryStr("select subjectNum from subject where subjectName = {subjectName} ", args2);
                subjectNameMap.put(subjectName, subjectNum);
            }
            String erName = erNum + "考场";
            EDataParameters eDataParameters = new EDataParameters(erId, erNum, erName, gradeNum, userId, currentTime2, "F", examNum, testingCentreId, erLocation, subjectNum, enNum, stuId, schoolNum, classNum, seat);
            erMap.put(examNum + testingCentreId + gradeNum + subjectNum + erNum, eDataParameters);
            enMap.put(stuId + examNum + gradeNum + subjectNum, eDataParameters);
        }
        for (Map.Entry<String, EDataParameters> next : erMap.entrySet()) {
            EDataParameters erData = next.getValue();
            Object[] erParam = {examNum, erData.getTestingCentreId(), erData.getGradeNum(), erData.getSubjectNum(), erData.getExaminationRoomNum()};
            Object er = this.dao.queryObject("select id from examinationroom where examNum=? and testingCentreId=? and gradeNum=? and subjectNum=? and examinationRoomNum=? ", erParam);
            if (null == er) {
                Object[] insertErParam = {erData.getExaminationRoomId(), erData.getExaminationRoomNum(), erData.getExaminationRoomName(), erData.getGradeNum(), erData.getInsertUser(), erData.getInsertDate(), erData.getIsDelete(), erData.getExamNum(), erData.getTestingCentreId(), erData.getTestLocation(), erData.getSubjectNum()};
                insertErParams.add(insertErParam);
            } else {
                erData.setExaminationRoomId(String.valueOf(er));
                Object[] updateErParam = {erData.getExaminationRoomName(), erData.getInsertUser(), erData.getInsertDate(), erData.getIsDelete(), erData.getTestLocation(), erData.getExaminationRoomId()};
                updateErParams.add(updateErParam);
            }
        }
        for (EDataParameters enData : enMap.values()) {
            String erKey = enData.getExamNum() + enData.getTestingCentreId() + enData.getGradeNum() + enData.getSubjectNum() + enData.getExaminationRoomNum();
            String erId2 = erMap.get(erKey).getExaminationRoomId();
            Object[] enParam = {enData.getStudentId(), enData.getExamNum(), enData.getGradeNum(), enData.getSubjectNum()};
            Object en = this.dao.queryObject("select id from examinationnum where studentId=? and examNum=? and gradeNum=? and subjectNum=? ", enParam);
            if (null == en) {
                Object[] insertEnParam = {erId2, enData.getExamineeNum(), enData.getStudentId(), enData.getInsertUser(), enData.getInsertDate(), enData.getIsDelete(), enData.getExamNum(), enData.getSchoolNum(), enData.getGradeNum(), enData.getClassNum(), enData.getTestingCentreId(), enData.getSubjectNum(), enData.getSeatNum()};
                insertEnParams.add(insertEnParam);
            } else {
                Object[] updateEnParam = {erId2, enData.getExamineeNum(), enData.getInsertUser(), enData.getInsertDate(), enData.getIsDelete(), enData.getSchoolNum(), enData.getClassNum(), enData.getTestingCentreId(), enData.getSeatNum(), String.valueOf(en)};
                updateEnParams.add(updateEnParam);
            }
        }
        this.log.info("【考号导入】数据入库开始：当前操作人：" + userId + "----" + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
        if (insertErParams.size() > 0) {
            ?? r0 = new Object[insertErParams.size()];
            insertErParams.toArray((Object[]) r0);
            this.dao.batchExecuteByLimit("insert into examinationroom (id,examinationRoomNum,examinationRoomName,gradeNum,insertUser,insertDate,isDelete,examNum,testingCentreId,testLocation,subjectNum) VALUES (?,?,?,?,?,?,?,?,?,?,?)", r0, 300);
        }
        if (updateErParams.size() > 0) {
            ?? r02 = new Object[updateErParams.size()];
            updateErParams.toArray((Object[]) r02);
            this.dao.batchExecuteByLimit("update examinationroom set examinationRoomName=?,insertUser=?,insertDate=?,isDelete=?,testLocation=? where id=? ", r02, 300);
        }
        if (insertEnParams.size() > 0) {
            ?? r03 = new Object[insertEnParams.size()];
            insertEnParams.toArray((Object[]) r03);
            this.dao.batchExecuteByLimit("INSERT INTO examinationnum (examinationRoomNum,examineeNum,studentId,insertUser,insertDate,isDelete,examNum,schoolNum,gradeNum,classNum,testingCentreId,subjectNum,seatNum)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ", r03, 300);
        }
        if (updateEnParams.size() > 0) {
            ?? r04 = new Object[updateEnParams.size()];
            updateEnParams.toArray((Object[]) r04);
            this.dao.batchExecuteByLimit("update examinationnum set examinationRoomNum=?,examineeNum=?,insertUser=?,insertDate=?,isDelete=?,schoolNum=?,classNum=?,testingCentreId=?,seatNum=? where id=?", r04, 300);
        }
        this.log.info("【考号导入】数据入库结束：当前操作人：" + userId + "----" + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
    }

    public void setError(File file, Sheet sheet, Row row, Cell cell, String pizhu, int columnLen, CellStyle errorRowStyle, CellStyle errorCellStyle) {
        if (!this.rowBgColor) {
            CheckCellUtil.setRowStyle(row, columnLen, errorRowStyle);
            this.rowBgColor = true;
            this.errorFlag = true;
        }
        CheckCellUtil.setCellStyle(file, sheet, cell, pizhu, errorCellStyle);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Object getStuOfInconformity(String xxbhCheck, String bjbhCheck) {
        String sql = "select stu.id from student stu ";
        String schSql = "";
        String claSql = "";
        if ("true".equals(xxbhCheck)) {
            sql = sql + "left join school sch on sch.id = stu.schoolNum ";
            schSql = "length(sch.schoolNum)>3 ";
        }
        if ("true".equals(bjbhCheck)) {
            sql = sql + "left join class cla on cla.id = stu.classNum ";
            String claSql2 = "".equals(schSql) ? "" : " or ";
            claSql = claSql2 + "length(cla.classNum)>2 ";
        }
        return this.dao.queryObject(sql + "where stu.isDelete = 'F' and stu.nodel = '0' and (" + schSql + claSql + ") limit 1");
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void createStuEn(Object[][] param, String examNum) {
        deleteExaminationNumber(examNum);
        this.dao.batchExecute("insert into student_examinationNumber (id,examNum,sid,studentId,examinationNumber,jie,schoolCode,studentType,classCode,schoolNum,gradeNum,classNum,type,insertUser,insertDate,isDelete) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", param);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void createStuEnList(List<Student_examinationNumber> paramList, String examNum) {
        deleteExaminationNumber(examNum);
        this.dao.batchSave(paramList, Const.height_500);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Student_examinationNumber> getAllStudent() {
        return this.dao.queryBeanList("SELECT stu.id sid,stu.studentId,bg.stage,stu.jie,sch.schoolNum schoolCode,cla.studentType,cla.classNum classCode,stu.schoolNum,stu.gradeNum,stu.classNum,stu.type,stu.isDelete from student stu LEFT JOIN school sch on sch.id = stu.schoolNum LEFT JOIN basegrade bg on bg.gradeNum = stu.gradeNum LEFT JOIN class cla on cla.id = stu.classNum where stu.isDelete = 'F' and stu.nodel = '0' and cla.classNum is not null ORDER BY sch.schoolNum,stu.gradeNum,cla.classNum,stu.studentId", Student_examinationNumber.class);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void deleteExaminationNumber(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        this.dao._execute("delete from student_examinationNumber where examNum = {examNum} ", args);
    }

    public void autoDeleteEn(String exam, String schoolStr, String gradeStr, String subjectStr, String table) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("schoolStr", schoolStr);
        args.put("gradeStr", gradeStr);
        args.put("subjectStr", subjectStr);
        List<Object> erList = this.dao._queryColList("select id from examinationroom where examNum = {exam} and gradeNum = {gradeStr} and subjectNum = {subjectStr} ", args);
        this.dao._execute("delete from examinationnum where examNum = {exam}  and schoolNum = {schoolStr}  and gradeNum ={gradeStr} and subjectNum = {subjectStr} ", args);
        for (int i = 0; i < erList.size(); i++) {
            String existSql = "select id from examinationnum where examinationRoomNum = '" + erList.get(i) + "' limit 1";
            if (null == this.dao.queryObject(existSql)) {
                String sql2 = "delete from examinationroom where id = '" + erList.get(i) + "'";
                this.dao.execute(sql2);
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int getMaxSchoolNumLen() {
        Object res = this.dao.queryObject("SELECT MAX(LENGTH(schoolNum)) FROM school WHERE isDelete = 'F'");
        if (null == res) {
            return 0;
        }
        return Integer.valueOf(String.valueOf(res)).intValue();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int getMaxClassNumLen() {
        Object res = this.dao.queryObject("SELECT MAX(LENGTH(classNum)) FROM class WHERE isDelete = 'F'");
        if (null == res) {
            return 0;
        }
        return Integer.valueOf(String.valueOf(res)).intValue();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Student_examinationNumber> getStudentList(String classString, String levelclass) {
        String sql;
        if ("T".equals(levelclass)) {
            sql = "SELECT stu.id sid,stu.studentId,bg.stage,stu.jie,sch.schoolNum schoolCode,cla.studentType,cla.classNum classCode,stu.schoolNum,stu.gradeNum,stu.classNum,stu.type,stu.isDelete,RIGHT(stu.studentNum,4) studentNum from student stu LEFT JOIN levelstudent ls on ls.sid = stu.id LEFT JOIN school sch on sch.id = stu.schoolNum LEFT JOIN basegrade bg on bg.gradeNum = stu.gradeNum LEFT JOIN class cla on cla.id = stu.classNum where stu.isDelete = 'F' and stu.nodel = '0' and fing_in_set(ls.classNum,{classString})   and ls.id is not null ";
        } else {
            sql = "SELECT stu.id sid,stu.studentId,bg.stage,stu.jie,sch.schoolNum schoolCode,cla.studentType,cla.classNum classCode,stu.schoolNum,stu.gradeNum,stu.classNum,stu.type,stu.isDelete,RIGHT(stu.studentNum,4) studentNum from student stu LEFT JOIN school sch on sch.id = stu.schoolNum LEFT JOIN basegrade bg on bg.gradeNum = stu.gradeNum LEFT JOIN class cla on cla.id = stu.classNum where stu.isDelete = 'F' and stu.nodel = '0' and fing_in_set(stu.classNum,{classString}) ";
        }
        Map args = new HashMap();
        args.put("classString", classString);
        return this.dao._queryBeanList(sql, Student_examinationNumber.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Object[]> getClassStuAndSubOneFile(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String isJointStuType, String fufen, String subCompose) throws Exception {
        return this.examManageDAO.getClassStuAndSubOneFile(examNum, gradeNum, classNum, schoolNum, studentType, graduationType, stuSourceType, viewRankInfo, isJointStuType, fufen, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Object[]> getClassleicengFile(String examNum, String gradeNum, String classNum, String schoolNum, String studentType, String graduationType, String stuSourceType, String viewRankInfo, String isJointStuType, String fufen, String subCompose, String topItemId) throws Exception {
        return this.examManageDAO.getClassleicengFile(examNum, gradeNum, classNum, schoolNum, studentType, graduationType, stuSourceType, viewRankInfo, isJointStuType, fufen, subCompose, topItemId);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String isexamleiceng(String sItemId, String examNum) {
        Map args = new HashMap();
        args.put("sItemId", sItemId);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryStr("SELECT count(1) from statisticlevel WHERE statisticId={sItemId} AND examNum={examNum}", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Student_examinationNumber> getStudentLenList(String idornum, String examineelength) {
        String stuSql = "1".equals(idornum) ? "stu.studentNum" : "stu.studentId";
        String sql = "SELECT stu.id sid,stu.studentId,bg.stage,stu.jie,sch.schoolNum schoolCode,cla.studentType,cla.classNum classCode,stu.schoolNum,stu.gradeNum,stu.classNum,stu.type,stu.isDelete,IFNULL(RIGHT(" + stuSql + Const.STRING_SEPERATOR + examineelength + "),'') studentNum from student stu LEFT JOIN school sch on sch.id = stu.schoolNum LEFT JOIN basegrade bg on bg.gradeNum = stu.gradeNum LEFT JOIN class cla on cla.id = stu.classNum where stu.isDelete = 'F' and stu.nodel = '0' and cla.classNum is not null ";
        return this.dao.queryBeanList(sql, Student_examinationNumber.class);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Student_examinationNumber> getStudentYuzhiList() {
        return this.dao.queryBeanList("SELECT stu.id sid,stu.studentId,bg.stage,stu.jie,sch.schoolNum schoolCode,cla.studentType,cla.classNum classCode,stu.schoolNum,stu.gradeNum,stu.classNum,stu.type,stu.isDelete,stu.yzexaminationnum examinationNumber from student stu LEFT JOIN school sch on sch.id = stu.schoolNum LEFT JOIN basegrade bg on bg.gradeNum = stu.gradeNum LEFT JOIN class cla on cla.id = stu.classNum where stu.isDelete = 'F' and stu.nodel = '0' and stu.yzexaminationnum<>'' and cla.classNum is not null ", Student_examinationNumber.class);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int updateAstrict(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeName = map.get("gradeName");
        String userType = map.get("userType");
        String status = map.get(Const.CORRECT_SCORECORRECT);
        Map args = new HashMap();
        args.put("gradeName", gradeName);
        args.put(Const.CORRECT_SCORECORRECT, status);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("userType", userType);
        String gradeNum = this.dao._queryStr("select gradeNum from basegrade where gradeName={gradeName} ", args);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        int aa = this.dao._execute("update astrict set status={status} where examNum={examNum} and gradeNum={gradeNum} and userType={userType} ", args);
        return aa;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int updateAstrict1(Map<String, String> map, String userId) {
        String status;
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String gradeName = map.get("gradeName");
        map.get("userType");
        Map args = new HashMap();
        args.put("gradeName", gradeName);
        String gradeNum = this.dao._queryStr("select gradeNum from basegrade where gradeName={gradeName} ", args);
        Object[] b = {examNum, gradeNum};
        String status2 = this.dao.queryStr("SELECT sum(`status`) FROM astrict where examNum=? and gradeNum=?", b);
        if (status2.equals("0.0")) {
            status = "1";
        } else {
            status = "0";
        }
        Object[] a = {status, examNum, gradeNum};
        int aa = this.dao.execute("update astrict set status= ? where examNum=? and gradeNum=?", a);
        return aa;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Astrict> queryAstrict(String examNum, int gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, Integer.valueOf(gradeNum));
        return this.dao._queryBeanList("SELECT gradeNum,userType,status from astrict where examNum={examNum} and gradeNum={gradeNum} and partType='5'", Astrict.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Shangxian> getAllFilterData(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryBeanList("SELECT en.schoolNum,sch.shortname schoolName,en.gradeNum,bg.gradeName,cla.studentType studentTypeNum,d.`name` studentTypeName,IFNULL(subc2.subjectCombineNum,'0') xuankezuheNum,IFNULL(subc2.subjectCombineName,'全部') xuankezuheName,IFNULL(subc.subjectCombineNum,'0') xuankezuheFlag,'0' statisticTypeNum,'全部' statisticTypeName,'0' sourceNum,'全部' sourceName from (SELECT schoolNum,gradeNum,classNum,studentId from examinationnum where examNum = {examNum} GROUP BY studentId ) en LEFT JOIN school sch on sch.id = en.schoolNum LEFT JOIN basegrade bg on bg.gradeNum = en.gradeNum LEFT JOIN `class` cla on cla.id = en.classNum LEFT JOIN `data` d on d.`value` = cla.studentType and d.type = '25' LEFT JOIN student stu on stu.id = en.studentId LEFT JOIN subjectcombine subc on subc.subjectCombineNum = stu.subjectCombineNum LEFT JOIN subjectcombine subc2 on subc2.subjectCombineNum = subc.pid GROUP BY en.schoolNum,en.gradeNum,cla.studentType,subc2.subjectCombineNum ORDER BY CONVERT(sch.shortname USING gbk),en.gradeNum desc,cla.studentType,subc2.orderNum", Shangxian.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, String>> getAllSubjectList(String examNum, String schoolNum, String gradeNum, String studentType, String xuankezuhe, String statisticType, String source) {
        String schoolStr = "-1".equals(schoolNum) ? " " : " and schoolNum={schoolNum} ";
        String tab = "(SELECT examNum,gradeNum,studentId,classNum,subjectNum from examinationnum where examNum={examNum} and gradeNum={gradeNum} " + schoolStr + "GROUP BY studentId,subjectNum ) en ";
        String classSql = "";
        String classStr = "";
        String studentSql = "";
        String studentStr = "";
        String statisticTypeStr = "1".equals(statisticType) ? " and stu.type='0' " : " ";
        String sourceStr = !"0".equals(source) ? " and stu.source={source} " : " ";
        if (!"0".equals(studentType)) {
            classSql = " LEFT JOIN class cla on cla.id = en.classNum ";
            classStr = " and cla.studentType = {studentType} ";
        }
        if (!"0".equals(xuankezuhe)) {
            studentSql = " LEFT JOIN  (SELECT subjectCombineNum from subjectcombine where pid={xuankezuhe} )  subc on subc.subjectCombineNum = stu.subjectCombineNum ";
            studentStr = " and subc.subjectCombineNum is not null ";
        }
        String sql = "SELECT DISTINCT ep2.subjectNum num,sub.subjectName name from " + tab + "LEFT JOIN exampaper ep on ep.examNum=en.examNum and ep.gradeNum = en.gradeNum and ep.subjectNum = en.subjectNum LEFT JOIN exampaper ep2 on ep2.pexamPaperNum = ep.examPaperNum LEFT JOIN `subject` sub on sub.subjectNum = ep2.subjectNum " + classSql + " LEFT JOIN student stu on stu.id = en.studentId " + studentSql + " where sub.subjectNum is not null " + classStr + statisticTypeStr + sourceStr + studentStr + " union all select zf.subjectNum num,zfsub.subjectName name from (SELECT -1 subjectNum UNION all SELECT DISTINCT psubjectNum subjectNum from totalscoremanagement where gradeNum = {gradeNum} and `status` = 'T') zf LEFT JOIN `subject` zfsub on zfsub.subjectNum = zf.subjectNum where zfsub.subjectNum is not null";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("source", source);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        return this.dao._queryMapList(sql, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<AjaxData> getPiciList(String type, String limitNum) {
        Map args = new HashMap();
        args.put("type", type);
        args.put("limitNum", limitNum);
        return this.dao._queryBeanList("select `value` num,`name` from data where type = {type} ORDER BY `value` LIMIT {limitNum} ", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getShangxianData(String examNum, String schoolNum, String gradeNum, String studentType, String xuankezuhe, String statisticType, String source, String fenshuyuan, String huaxianType, String limitNum) {
        List<Map<String, Object>> data = new ArrayList<>();
        if (schoolNum.equals("-1") && huaxianType.equals("0")) {
            Object[] param = {limitNum, examNum, gradeNum, studentType, xuankezuhe, statisticType, source, fenshuyuan};
            data.addAll(this.dao.queryMapList("select '-1' subjectNum,'总分' subjectName,d.`value` num,d.`name`,o.type,o.num renshu,o.score,0 scoretype,null totalScore from (select `value`,`name` from data where type = '21' LIMIT ?) d LEFT JOIN (select o.type,o.num,o.score from onlinesample o where o.examNum=? and o.gradeNum=? and o.studentType=? and o.xuankezuhe=? and o.statisticType=? and o.source=? and o.fenshuyuan=? ) o on o.type = d.`value` order by d.`value`", param));
        } else {
            Object[] param2 = {limitNum, examNum, schoolNum, gradeNum, studentType, xuankezuhe, statisticType, source, fenshuyuan, huaxianType, "-1"};
            data.addAll(this.dao.queryMapList("select '-1' subjectNum,'总分' subjectName,d.`value` num,d.`name`,o.type,o.num renshu,o.score,o.scoretype,null totalScore from (select `value`,`name` from data where type = '21' LIMIT ?) d LEFT JOIN (select o.type,o.num,o.score,o.scoretype from onlineindicator o where o.examNum=? and o.schoolNum=? and o.gradeNum=? and o.studentType=? and o.xuankezuhe=? and o.statisticType=? and o.source=? and o.fenshuyuan=? and o.scoretype=? and o.subjectNum=? ) o on o.type = d.`value` order by d.`value`", param2));
        }
        return data;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getonlinesample(String examNum, String gradeNum, String studentType, String xuankezuhe, String statisticType, String source, String fenshuyuan, String huaxianType, String limitNum) {
        return null;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void saveOnlineScore(OnlineIndicator o) {
        deleteAnotherOnline(o);
        deleteOnline(o);
        this.dao.save(o);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void saveOnlineNum(OnlineIndicator o) {
        deleteAnotherOnline(o);
        List<Map<String, String>> subjectList = getAllSubjectList(String.valueOf(o.getExamNum()), String.valueOf(o.getSchoolNum()), String.valueOf(o.getGradeNum()), String.valueOf(o.getStudentType()), String.valueOf(o.getXuankezuhe()), o.getStatisticType(), o.getSource());
        for (Map<String, String> sub : subjectList) {
            o.setSubjectNum(Integer.valueOf(String.valueOf(sub.get("num"))));
            deleteOnline(o);
            this.dao.save(o);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void deleteAnotherOnline(OnlineIndicator o) {
        String scoretype = "0".equals(o.getScoretype()) ? "1" : "0";
        Object[] param = {o.getExamNum(), o.getGradeNum(), o.getStudentType(), o.getXuankezuhe(), o.getStatisticType(), o.getSource(), o.getFenshuyuan(), scoretype};
        List ids = this.dao.queryColList("select id from onlineindicator where examNum=? and gradeNum=? and studentType=? and xuankezuhe=? and statisticType=? and source=? and fenshuyuan=? and scoretype=? ", param);
        if (null != ids) {
            for (Object id : ids) {
                Map args = new HashMap();
                args.put("id", id);
                this.dao._execute("delete from onlineindicator where id={id} ", args);
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void deleteOnline(OnlineIndicator o) {
        StringBuffer isExistSql = new StringBuffer();
        isExistSql.append("select id from onlineindicator ");
        isExistSql.append(StrUtil.format("where examNum={ExamNum}  ", new Object[]{o.getExamNum()}));
        if (-1 != o.getSchoolNum().intValue()) {
            isExistSql.append(StrUtil.format("and schoolNum={SchoolNum}  ", new Object[]{o.getSchoolNum()}));
        }
        isExistSql.append("and gradeNum={GradeNum}  and studentType={StudentType}  and xuankezuhe={Xuankezuhe}  and statisticType={StatisticType}  and source={Source}  and fenshuyuan={Fenshuyuan}  and scoretype={Scoretype}  and subjectNum={SubjectNum} ");
        Map args = new HashMap();
        args.put("ExamNum", o.getExamNum());
        args.put("SchoolNum", o.getSchoolNum());
        args.put("GradeNum", o.getGradeNum());
        args.put("StudentType", o.getStudentType());
        args.put("Xuankezuhe", o.getXuankezuhe());
        args.put("StatisticType", o.getStatisticType());
        args.put("Source", o.getSource());
        args.put("Fenshuyuan", o.getFenshuyuan());
        args.put("Scoretype", o.getScoretype());
        args.put("SubjectNum", o.getSubjectNum());
        List ids = this.dao._queryColList(isExistSql.toString(), args);
        if (null != ids) {
            for (Object id : ids) {
                Map args1 = new HashMap();
                args1.put("id", id);
                this.dao._execute("delete from onlineindicator where id={id} ", args1);
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg existShangxianData(OnlineIndicator o) {
        RspMsg msg = new RspMsg(200, "", null);
        String schoolStr = -1 == o.getSchoolNum().intValue() ? " and schoolNum<>-1 " : " and schoolNum=-1 ";
        String isExistSql = "select id from onlineindicator where examNum=? " + schoolStr + " and gradeNum=? and studentType=? and xuankezuhe=? and statisticType=? and source=? and fenshuyuan=? ";
        Object[] param = {o.getExamNum(), o.getGradeNum(), o.getStudentType(), o.getXuankezuhe(), o.getStatisticType(), o.getSource(), o.getFenshuyuan()};
        Object id = this.dao.queryObject(isExistSql, param);
        if (null != id) {
            if (-1 == o.getSchoolNum().intValue()) {
                msg = new RspMsg(401, "将会覆盖单校的设置数据！", null);
            } else {
                return new RspMsg(402, "全区已经设置了，不能再设定!", null);
            }
        }
        String scoretype = "0".equals(o.getScoretype()) ? "1" : "0";
        Object[] param2 = {o.getExamNum(), o.getSchoolNum(), o.getGradeNum(), o.getStudentType(), o.getXuankezuhe(), o.getStatisticType(), o.getSource(), o.getFenshuyuan(), scoretype};
        Object id2 = this.dao.queryObject("select id from onlineindicator where examNum=? and schoolNum=? and gradeNum=? and studentType=? and xuankezuhe=? and statisticType=? and source=? and fenshuyuan=? and scoretype=? ", param2);
        if (null != id2) {
            return new RspMsg(403, "已设定另外一种划线方法，是否清空之前数据，保存现有设定数据？", null);
        }
        Object[] param3 = {o.getExamNum(), o.getSchoolNum(), o.getGradeNum(), o.getStudentType(), o.getXuankezuhe(), o.getStatisticType(), o.getSource(), o.getFenshuyuan()};
        Object id3 = this.dao.queryObject("select id from onlineindicator where examNum=? and schoolNum=? and gradeNum=? and studentType=? and xuankezuhe=? and statisticType=? and source=? and fenshuyuan!=? ", param3);
        if (null != id3) {
            return new RspMsg(404, "已设定另外一种分数源的数据，是否清空另一种分数源的数据，保存现有设定数据？", null);
        }
        return msg;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer delShangxianDatabyfenshuyuan(OnlineIndicator onlineIndicator) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, onlineIndicator.getExamNum());
        args.put(Const.EXPORTREPORT_gradeNum, onlineIndicator.getGradeNum());
        args.put("xuankezuhe", onlineIndicator.getXuankezuhe());
        args.put("statisticType", onlineIndicator.getStatisticType());
        args.put("source", onlineIndicator.getStatisticType());
        args.put("fenshuyuan", onlineIndicator.getFenshuyuan());
        args.put(Const.EXPORTREPORT_schoolNum, onlineIndicator.getSchoolNum());
        args.put(Const.EXPORTREPORT_studentType, onlineIndicator.getStudentType());
        return Integer.valueOf(this.dao._execute("delete from onlineindicator WHERE examNum={examNum} and schoolNum={schoolNum} AND gradeNum={gradeNum} AND xuankezuhe={xuankezuhe} AND statisticType={statisticType} and studentType={studentType} AND source={source} AND fenshuyuan!={fenshuyuan} ", args));
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void saveOnline(OnlineIndicator onlineIndicator, JSONArray inputArr) {
        deleteAnotherOnline(onlineIndicator);
        Set<String> subjectSet = new HashSet<>();
        new ArrayList();
        List<Map<String, String>> subjectList = getAllSubjectList(String.valueOf(onlineIndicator.getExamNum()), String.valueOf(onlineIndicator.getSchoolNum()), String.valueOf(onlineIndicator.getGradeNum()), String.valueOf(onlineIndicator.getStudentType()), String.valueOf(onlineIndicator.getXuankezuhe()), onlineIndicator.getStatisticType(), onlineIndicator.getSource());
        for (int i = 0; i < inputArr.size(); i++) {
            JSONObject input = inputArr.getJSONObject(i);
            String[] id = String.valueOf(input.get("id")).split("_");
            String value = String.valueOf(input.get("value"));
            onlineIndicator.setType(id[1]);
            if ("0".equals(onlineIndicator.getScoretype())) {
                onlineIndicator.setNum(Integer.valueOf(value));
            }
            for (Map<String, String> sub : subjectList) {
                onlineIndicator.setSubjectNum(Integer.valueOf(String.valueOf(sub.get("num"))));
                if (!"0".equals(onlineIndicator.getScoretype())) {
                    if (onlineIndicator.getSubjectNum().intValue() == -1) {
                        onlineIndicator.setScore(value);
                    } else {
                        onlineIndicator.setScore(null);
                    }
                }
                if (subjectSet.add(String.valueOf(sub.get("num")))) {
                    deleteOnline(onlineIndicator);
                }
                this.dao.save(onlineIndicator);
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg jisuanOnline(String examNum, String gradeNum) {
        try {
            Object[] params = {examNum, gradeNum};
            this.dao.execute("CALL upperToLine_calScoreLine(?,?)", params);
            return new RspMsg(200, Const.INFO_SUCCESS, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspMsg(Const.height_500, "计算失败！", null);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getJisuanshijian(String examNum, String gradeNum, String subjectNum, String tableName) {
        String sql = "select IFNULL(insertDate,'') insertDate from " + tableName + " where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}  limit 1";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        Object insertDate = this.dao._queryObject(sql, args);
        return null == insertDate ? "" : insertDate.toString();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getExamPaperNum(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao._queryStr("select examPaperNum from exampaper where examNum={examNum}  and gradeNum={gradeNum} and subjectNum={subjectNum}  ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int getTishubuduiStuCount(String examNum, String gradeNum, String subjectNum) {
        String examPaperNum = getExamPaperNum(examNum, gradeNum, subjectNum);
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        int temp_questioncount = null == this.dao._queryObject("SELECT count(1) FROM define a LEFT JOIN (select id,choosename from define where exampapernum={examPaperNum} GROUP BY choosename ) b ON CAST(a.id AS CHAR)=b.choosename LEFT JOIN subdefine c on c.pid=a.id or c.pid=b.id WHERE a.exampapernum={examPaperNum}  and LENGTH(a.choosename) < 2", args) ? 0 : this.dao._queryInt("SELECT count(1) FROM define a LEFT JOIN (select id,choosename from define where exampapernum={examPaperNum} GROUP BY choosename ) b ON CAST(a.id AS CHAR)=b.choosename LEFT JOIN subdefine c on c.pid=a.id or c.pid=b.id WHERE a.exampapernum={examPaperNum}  and LENGTH(a.choosename) < 2", args).intValue();
        args.put("temp_questioncount", Integer.valueOf(temp_questioncount));
        if (null == this.dao._queryObject("select count(1) from (select a.studentid,count(1) tishu from (select studentid from objectivescore where exampapernum={examPaperNum}  UNION all select studentid from score where exampapernum={examPaperNum}  and continued='F' ) a GROUP BY a.studentid HAVING tishu <> {temp_questioncount} ) b", args)) {
            return 0;
        }
        return this.dao._queryInt("select count(1) from (select a.studentid,count(1) tishu from (select studentid from objectivescore where exampapernum={examPaperNum}  UNION all select studentid from score where exampapernum={examPaperNum}  and continued='F' ) a GROUP BY a.studentid HAVING tishu <> {temp_questioncount} ) b", args).intValue();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int getTishubuduiStuCountByKeguan(String examNum, String gradeNum, String subjectNum) {
        String examPaperNum = getExamPaperNum(examNum, gradeNum, subjectNum);
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        int temp_questioncount = null == this.dao._queryObject("SELECT count(1) FROM define a LEFT JOIN (select id,choosename from define where exampapernum={examPaperNum} GROUP BY choosename ) b ON CAST(a.id AS CHAR)=b.choosename LEFT JOIN subdefine c on c.pid=a.id or c.pid=b.id WHERE a.exampapernum={examPaperNum} and IFNULL(c.questiontype,a.questionType)='0' and LENGTH(a.choosename) < 2", args) ? 0 : this.dao._queryInt("SELECT count(1) FROM define a LEFT JOIN (select id,choosename from define where exampapernum={examPaperNum} GROUP BY choosename ) b ON CAST(a.id AS CHAR)=b.choosename LEFT JOIN subdefine c on c.pid=a.id or c.pid=b.id WHERE a.exampapernum={examPaperNum} and IFNULL(c.questiontype,a.questionType)='0' and LENGTH(a.choosename) < 2", args).intValue();
        args.put("temp_questioncount", Integer.valueOf(temp_questioncount));
        if (null == this.dao._queryObject("select count(1) from (select studentId,count(1) tishu from objectivescore where exampapernum={examPaperNum}  GROUP BY studentId HAVING tishu <> {temp_questioncount} ) b", args)) {
            return 0;
        }
        return this.dao._queryInt("select count(1) from (select studentId,count(1) tishu from objectivescore where exampapernum={examPaperNum}  GROUP BY studentId HAVING tishu <> {temp_questioncount} ) b", args).intValue();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getExamineeNum(String studentId, String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        Object examineeNum = this.dao._queryObject("select examineeNum from examinationnum where studentId={studentId} and examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        if (null == examineeNum) {
            return null;
        }
        return examineeNum.toString();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<RegExaminee> getDifferentRegList(String exam, String subject, String testCenter, String grade, String userId) {
        String examPaperNum = getExamPaperNum(exam, grade, subject);
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("testCenter", testCenter);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        List _queryBeanList = this.dao._queryBeanList("SELECT reg.id,reg.page,en.studentId,en.examinationRoomNum,en.classNum,en.schoolNum,reg.scannum,reg.examPaperNum,reg.testingCentreId,reg.cNum from regexaminee reg INNER JOIN examinationnum en on en.examineeNum = reg.scannum and en.testingCentreId = en.testingCentreId where reg.examPaperNum={examPaperNum} and reg.testingCentreId={testCenter} and en.studentId <> reg.studentId and en.examNum={exam}  and en.gradeNum={grade}  and en.subjectNum={subject} ", RegExaminee.class, args);
        if (null != _queryBeanList) {
            Examlog examlog = new Examlog();
            examlog.setOperate("考号校对-批量识别考号");
            examlog.setExamNum(Integer.valueOf(exam));
            examlog.setSubjectNum(Integer.valueOf(subject));
            examlog.setGradeNum(Integer.valueOf(grade));
            examlog.setExaminationRoomNum(testCenter);
            examlog.setInsertUser(userId);
            examlog.setInsertDate(DateUtil.getCurrentTime());
            this.dao.save(examlog);
        }
        return _queryBeanList;
    }

    public List<RegExaminee> getAllRegList_old(String examPaperNum, String testCenter, String userId) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("testCenter", testCenter);
        return this.dao._queryBeanList("select id,page,'-1' classNum,'0' schoolNum,examinationRoomNum,scannum,examPaperNum,testingCentreId,cNum from regexaminee where examPaperNum={examPaperNum} and testingCentreId={testCenter} ", RegExaminee.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<RegExaminee> getAllRegList(String examPaperNum, String testCenter) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("testCenter", testCenter);
        return this.dao._queryBeanList("select GROUP_CONCAT(reg.id) id,GROUP_CONCAT(reg.page) pageStr,'-1' classNum,'0' schoolNum,reg.examinationRoomNum,reg.examPaperNum,reg.testingCentreId,reg.scannum,reg.cNum,max(c.blackRatio) type,reg.examineeNum from regexaminee reg left join corner c on c.regid = reg.id where reg.examPaperNum={examPaperNum}  and reg.testingCentreId={testCenter}  GROUP BY reg.cNum ", RegExaminee.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Examinationnum> getEnList(String testCenter, String exam, String subject, String grade) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("testCenter", testCenter);
        args.put("grade", grade);
        args.put("subject", subject);
        return this.dao._queryBeanList("SELECT en.studentId,en.examinationRoomNum,en.classNum,en.schoolNum,en.examineeNum from examinationnum en where en.examNum={exam}  and en.testingCentreId={testCenter} and en.gradeNum={grade} and en.subjectNum={subject}  ", Examinationnum.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void deleteAllExamineenumerror(String examPaperNum, String testCenter) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("testCenter", testCenter);
        this.dao._execute("delete from examineenumerror where examPaperNum={examPaperNum}  and testingCentreId={testCenter} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<RegExaminee> existReglByStuId(RegExaminee reg) {
        Map args = new HashMap();
        args.put("ExamPaperNum", Integer.valueOf(reg.getExamPaperNum()));
        args.put("ExaminationRoomNum", reg.getExaminationRoomNum());
        args.put("StudentId", reg.getStudentId());
        return this.dao._queryBeanList("select GROUP_CONCAT(reg.id) id,GROUP_CONCAT(reg.page) pageStr,UUID_SHORT() studentId,reg.examinationRoomNum,'-1' classNum,'0' schoolNum,reg.examPaperNum,reg.testingCentreId,reg.cNum,max(c.blackRatio) type from regexaminee reg LEFT JOIN corner c on c.regid = reg.id where reg.examPaperNum={ExamPaperNum} and reg.examinationRoomNum={ExaminationRoomNum} and reg.studentId={StudentId} group by reg.cNum", RegExaminee.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void updateStudentIdByRegId(RegExaminee reg) {
        String regId = reg.getId();
        int pageNo = reg.getPage();
        String studentId = reg.getStudentId();
        String classNum = reg.getClassNum();
        int schoolNum = reg.getSchoolNum();
        String examRoomNum = reg.getExaminationRoomNum();
        List<RowArg> rowArgList = new ArrayList<>();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("examRoomNum", examRoomNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_schoolNum, Integer.valueOf(schoolNum));
        args.put("regId", regId);
        rowArgList.add(new RowArg("UPDATE regexaminee SET studentId={studentId},examinationRoomNum={examRoomNum} ,classNum={classNum} ,schoolNum={schoolNum}  WHERE id={regId} ", args));
        rowArgList.add(new RowArg("UPDATE scanrecord SET studentId={studentId} ,examinationRoomNum={examRoomNum} ,classNum={classNum} ,schoolNum={schoolNum}   WHERE id={regId} ", args));
        rowArgList.add(new RowArg("UPDATE score SET studentId={studentId} ,examinationRoomNum={examRoomNum} ,classNum={classNum} ,schoolNum={schoolNum}   WHERE regId={regId} ", args));
        rowArgList.add(new RowArg("UPDATE objectivescore SET studentId={studentId} ,examinationRoomNum={examRoomNum} ,classNum={classNum} ,schoolNum={schoolNum}   WHERE regId={regId} ", args));
        rowArgList.add(new RowArg("UPDATE objitem SET examinationRoomNum={examRoomNum}  where regId={regId} ", args));
        rowArgList.add(new RowArg("UPDATE cantrecognized  SET examinationRoomNum={examRoomNum} ,schoolNum={schoolNum}   WHERE regId={regId} ", args));
        List<Object> list = this.dao._queryColList("select id from score where regid = {regId} ", args);
        if (list != null) {
            for (int k = 0; k < list.size(); k++) {
                Map args1 = new HashMap();
                args1.put(Const.EXPORTREPORT_studentId, studentId);
                args1.put(Const.EXPORTREPORT_classNum, classNum);
                args1.put(Const.EXPORTREPORT_schoolNum, Integer.valueOf(schoolNum));
                rowArgList.add(new RowArg("UPDATE tag SET studentId={studentId} ,classNum={classNum} ,schoolNum={schoolNum}  where scoreid='" + list.get(k) + "'", args1));
                rowArgList.add(new RowArg("UPDATE task SET studentId={studentId} ,insertUser=if(`status`='F','-1',insertUser) where scoreid='" + list.get(k) + "'", args1));
            }
        }
        if (pageNo == 1) {
            rowArgList.add(new RowArg("UPDATE illegal SET studentId={studentId} ,examinationRoomNum={examRoomNum} ,schoolNum={schoolNum}  WHERE regId={regId} ", args));
        }
        this.dao._batchExecute(rowArgList);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer save(Object obj) {
        return Integer.valueOf(this.dao.save(obj));
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg existEnByTestCenter(String testCenter) {
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        Object res = this.dao._queryObject("SELECT id FROM examinationnum where testingCentreId={testCenter}  LIMIT 1", args);
        if (null == res) {
            return new RspMsg(200, "该考点未分配考场信息！", null);
        }
        return new RspMsg(Const.height_500, "该考点已分配考场信息，不能删除！", null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg existRegByTestCenter(String testCenter) {
        Map args = new HashMap();
        args.put("testCenter", testCenter);
        Object res = this.dao._queryObject("SELECT id FROM regexaminee where testingCentreId={testCenter}  LIMIT 1", args);
        if (null == res) {
            return new RspMsg(200, "该考点未处理图片！", null);
        }
        return new RspMsg(Const.height_500, "该考点已处理图片，不能删除！", null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg existEnByExam(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        Object res = this.dao._queryObject("SELECT en.id FROM (SELECT id FROM testingcentre where examNum={examNum}) tc INNER JOIN examinationnum en ON en.testingCentreId=tc.id LIMIT 1", args);
        if (null == res) {
            return new RspMsg(200, "当前考试所有考点未分配考场信息！", null);
        }
        return new RspMsg(Const.height_500, "当前考试已分配考场信息，不能使用该功能！", null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg existRegByExam(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        Object res = this.dao._queryObject("SELECT reg.id FROM (SELECT id FROM testingcentre where examNum={examNum} ) tc INNER JOIN regexaminee reg ON reg.testingCentreId=tc.id LIMIT 1", args);
        if (null == res) {
            return new RspMsg(200, "当前考试所有考点未处理图片！", null);
        }
        return new RspMsg(Const.height_500, "当前考试已处理图片，不能使用该功能！", null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map getFistQuestionCommentImg(String questionNum, String studentId, String scoreId) {
        Map res = new HashMap();
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        int questionHSum = 0;
        int questionHCurrent = 0;
        int index = 0;
        Iterator<?> it = this.dao._queryBeanList("select s.id,s.continued isException,s.page,qi.questionH,qi.multiRects tag from (select id,continued,page from score where questionNum={questionNum} and studentId={studentId} ) s inner join questionimage qi on qi.scoreId=s.id order by s.page", Questionimage.class, args).iterator();
        while (it.hasNext()) {
            Questionimage qi = (Questionimage) it.next();
            args.put("Id", qi.getId());
            if ("F".equals(qi.getIsException())) {
                Questionimage questionimage = (Questionimage) this.dao._queryBean("SELECT img FROM remarkimg where scoreId={Id}  order by insertDate desc limit 1", Questionimage.class, args);
                if (questionimage == null) {
                    res.put("img", null);
                } else {
                    res.put("img", questionimage.getImg());
                }
            }
            questionHSum += Integer.valueOf(qi.getQuestionH()).intValue();
            if (null != qi.getTag()) {
                List<Rectangle> qiList = com.alibaba.fastjson.JSONObject.parseArray(qi.getTag(), Rectangle.class);
                questionHSum += qiList.stream().mapToInt((v0) -> {
                    return v0.getHeight();
                }).sum();
            }
            if (scoreId.equals(qi.getId())) {
                index = -1;
            }
            if (index == 0) {
                questionHCurrent += Integer.valueOf(qi.getQuestionH()).intValue();
            }
        }
        res.put("questionHSum", Integer.valueOf(questionHSum));
        res.put("questionHCurrent", Integer.valueOf(questionHCurrent));
        return res;
    }

    public List<Map<String, Object>> getTestingcenterSchoolList(String examNum) {
        StringBuffer tcssql = new StringBuffer();
        tcssql.append("select testingCentreId,schoolNum from testingcentre_school ");
        tcssql.append("where examNum={examNum} ");
        StringBuffer sql = new StringBuffer();
        sql.append("select tc.testingCentreNum,tc.testingCentreName,tc.testingCentreLocation,sch.schoolName from (");
        sql.append(tcssql);
        sql.append(") tcs ");
        sql.append("left join testingcentre tc on tc.id=tcs.testingCentreId ");
        sql.append("left join school sch on sch.id=tcs.schoolNum ");
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void exportTestingcentre(String examNum, String filePath) {
        FileUtil.del(filePath);
        List<Map<String, Object>> data = getTestingcenterSchoolList(examNum);
        ExcelWriter writer = ExcelUtil.getWriter(filePath);
        writer.addHeaderAlias("testingCentreNum", "考点编号");
        writer.addHeaderAlias("testingCentreName", "考点名称");
        writer.addHeaderAlias("testingCentreLocation", "考点地址");
        writer.addHeaderAlias("schoolName", "学校名称");
        for (int i = 1; i < 4; i++) {
            writer.setColumnWidth(i, 20);
        }
        writer.write(data);
        writer.close();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public RspMsg checkTestingcentreFile(File file, String fileName, String userId, String examNum) {
        Row row;
        this.errorFlag = false;
        ExcelHelper excelHelper = new ExcelHelper(file);
        try {
            Workbook workbook = excelHelper.creatWorkbook();
            Sheet sheet = workbook.getSheetAt(0);
            Row row0 = sheet.getRow(0);
            int columnLen = row0.getPhysicalNumberOfCells();
            if (null != row0 && columnLen != 4) {
                return new RspMsg(410, "excel文件第一行的表头列数与导入模板不符合，请检查！", null);
            }
            CellStyle errorRowStyle = workbook.createCellStyle();
            errorRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            errorRowStyle.setFillForegroundColor((short) 10);
            errorRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            errorRowStyle.setAlignment(HorizontalAlignment.CENTER);
            CellStyle errorCellStyle = workbook.createCellStyle();
            errorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            errorCellStyle.setFillForegroundColor((short) 13);
            errorCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            errorCellStyle.setAlignment(HorizontalAlignment.CENTER);
            List<?> queryColList = this.dao.queryColList("select schoolName from school");
            Map<String, String> testingCentreNameMap = new HashMap<>();
            Set<String> tcsSet = new HashSet<>();
            for (int i = 1; i < sheet.getPhysicalNumberOfRows() && null != (row = sheet.getRow(i)); i++) {
                this.rowBgColor = false;
                Cell tcNumCell = row.getCell(0);
                String tcNum = "";
                if (null == tcNumCell) {
                    row.createCell(0);
                } else {
                    tcNum = CheckCellUtil.getCellValue(tcNumCell);
                }
                Cell tcNameCell = row.getCell(1);
                String tcName = "";
                if (null == tcNameCell) {
                    tcNameCell = row.createCell(1);
                } else {
                    tcName = CheckCellUtil.getCellValue(tcNameCell);
                }
                if (testingCentreNameMap.containsKey(tcName) && !tcNum.equals(testingCentreNameMap.get(tcName))) {
                    String pizhu = "Excel中考点名称 " + tcName + " 对应多个考点编号，请检查";
                    setError(file, sheet, row, tcNameCell, pizhu, columnLen, errorRowStyle, errorCellStyle);
                } else {
                    testingCentreNameMap.put(tcName, tcNum);
                }
                Cell schoolNameCell = row.getCell(3);
                String schoolName = "";
                if (null == schoolNameCell) {
                    schoolNameCell = row.createCell(3);
                } else {
                    schoolName = CheckCellUtil.getCellValue(schoolNameCell);
                }
                StringBuffer schPizhu = new StringBuffer();
                if (!queryColList.contains(schoolName)) {
                    schPizhu.append("系统中没有 " + schoolName + " 这个学校");
                }
                if (!tcsSet.add(tcNum + "_" + tcName + "_" + schoolName)) {
                    schPizhu.append("\nExcel中考点 " + tcName + " ，学校 " + schoolName + " 数据重复，请检查");
                }
                if (schPizhu.length() > 0) {
                    setError(file, sheet, row, schoolNameCell, schPizhu.toString(), columnLen, errorRowStyle, errorCellStyle);
                }
            }
            if (this.errorFlag) {
                FileOutputStream fOut = new FileOutputStream(file);
                workbook.write(fOut);
                fOut.flush();
                fOut.close();
                return new RspMsg(Const.height_500, "导入文件信息错误", null);
            }
            return new RspMsg(200, "导入文件信息正确", null);
        } catch (Exception e) {
            return new RspMsg(410, "check error:" + e.getMessage(), null);
        }
    }

    /* JADX WARN: Type inference failed for: r0v53, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v61, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v69, types: [java.lang.Object[], java.lang.Object[][]] */
    /* JADX WARN: Type inference failed for: r0v77, types: [java.lang.Object[], java.lang.Object[][]] */
    @Override // com.dmj.service.examManagement.ExamManageService
    public void importTestingcentre(File file, String fileName, String userId, String examNum) {
        Row row;
        Object testingCentreId;
        ExcelHelper excelHelper = new ExcelHelper(file);
        Workbook workbook = excelHelper.creatWorkbook();
        Sheet sheet = workbook.getSheetAt(0);
        List<Object[]> insertTcParams = new ArrayList<>();
        List<Object[]> updateTcParams = new ArrayList<>();
        List<Object[]> deleteTcsParams = new ArrayList<>();
        List<Object[]> insertTcsParams = new ArrayList<>();
        String currentTime = DateUtil.getCurrentTime();
        StringBuffer tcNumSql = new StringBuffer();
        tcNumSql.append("select testingCentreNum,id from testingcentre ");
        tcNumSql.append("where examNum={examNum}");
        Map args1 = new HashMap();
        args1.put(Const.EXPORTREPORT_examNum, examNum);
        Map<String, Object> tcMap = this.dao._queryOrderMap(tcNumSql.toString(), TypeEnum.StringObject, args1);
        StringBuffer tcsSql = new StringBuffer();
        tcsSql.append("select testingCentreId from testingcentre_school ");
        tcsSql.append("where examNum={examNum}");
        List<Object> tcsList = this.dao._queryColList(tcsSql.toString(), args1);
        Map<String, String> schMap = this.dao._queryOrderMap("select schoolName,id from school".toString(), TypeEnum.StringString, null);
        int len = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < len && null != (row = sheet.getRow(i)); i++) {
            Cell testingCentreNumCell = row.getCell(0);
            String testingCentreNum = CheckCellUtil.getCellValue(testingCentreNumCell);
            Cell testingCentreNameCell = row.getCell(1);
            String testingCentreName = CheckCellUtil.getCellValue(testingCentreNameCell);
            Cell testingCentreLocationCell = row.getCell(2);
            String testingCentreLocation = CheckCellUtil.getCellValue(testingCentreLocationCell);
            Cell schoolNameCell = row.getCell(3);
            String schoolName = CheckCellUtil.getCellValue(schoolNameCell);
            if (tcMap.containsKey(testingCentreNum)) {
                testingCentreId = tcMap.get(testingCentreNum);
                Object[] updateTcParam = {testingCentreName, testingCentreLocation, userId, currentTime, examNum, testingCentreNum};
                updateTcParams.add(updateTcParam);
            } else {
                testingCentreId = GUID.getGUIDStr();
                tcMap.put(testingCentreNum, testingCentreId);
                Object[] insertTcParam = {testingCentreId, examNum, testingCentreNum, testingCentreName, testingCentreLocation, userId, currentTime};
                insertTcParams.add(insertTcParam);
            }
            if (tcsList.contains(testingCentreId)) {
                Object[] deleteTcsParam = {examNum, testingCentreId};
                deleteTcsParams.add(deleteTcsParam);
            }
            Object[] insertTcsParam = {GUID.getGUIDStr(), examNum, testingCentreId, schMap.get(schoolName), userId, currentTime, userId, currentTime};
            insertTcsParams.add(insertTcsParam);
        }
        if (insertTcParams.size() > 0) {
            ?? r0 = new Object[insertTcParams.size()];
            insertTcParams.toArray((Object[]) r0);
            this.dao.batchExecuteByLimit("INSERT INTO testingcentre (id,examNum,testingCentreNum,testingCentreName,testingCentreLocation,insertUser,insertDate) VALUES (?,?,?,?,?,?,?)", r0, 100);
        }
        if (updateTcParams.size() > 0) {
            ?? r02 = new Object[updateTcParams.size()];
            updateTcParams.toArray((Object[]) r02);
            this.dao.batchExecuteByLimit("update testingcentre set testingCentreName=?,testingCentreLocation=?,insertUser=?,insertDate=? where examNum=? and testingCentreNum=? ", r02, 100);
        }
        if (deleteTcsParams.size() > 0) {
            ?? r03 = new Object[deleteTcsParams.size()];
            deleteTcsParams.toArray((Object[]) r03);
            this.dao.batchExecuteByLimit("delete from testingcentre_school where examNum=? and testingCentreId=? ", r03, 100);
        }
        if (insertTcsParams.size() > 0) {
            ?? r04 = new Object[insertTcsParams.size()];
            insertTcsParams.toArray((Object[]) r04);
            this.dao.batchExecuteByLimit("INSERT INTO testingcentre_school (id,examNum,testingCentreId,schoolNum,insertUser,insertDate,updateUser,updateDate) VALUES (?,?,?,?,?,?,?,?)", r04, 100);
        }
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getSubjectExceptionCheckLiandongData() {
        StringBuffer enSql = new StringBuffer();
        enSql.append("select examNum,gradeNum,schoolNum,subjectNum from examinationnum ");
        enSql.append("group by examNum,gradeNum,schoolNum,subjectNum");
        StringBuffer sql = new StringBuffer();
        sql.append("select en.examNum,e.examName,en.gradeNum,bg.gradeName,en.schoolNum,sch.schoolName,en.subjectNum,sub.subjectName ");
        sql.append("from examinationnum en ");
        sql.append(" inner join exam e on e.examNum=en.examNum ");
        sql.append(" inner join basegrade bg on bg.gradeNum=en.gradeNum ");
        sql.append(" inner join `subject` sub on sub.subjectNum=en.subjectNum ");
        sql.append(" inner join school sch on sch.id=en.schoolNum ");
        sql.append(" where e.isDelete='F' and e.status<>'9' ");
        sql.append(" group by en.examNum,en.gradeNum,en.schoolNum,en.subjectNum ");
        sql.append(" order by e.examDate desc,e.insertDate desc,bg.gradeNum,CONVERT(sch.schoolName using gbk),sub.orderNum,sub.subjectNum ");
        return this.dao.queryMapList(sql.toString());
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public int getSubjectExceptionCount(String examNum, String gradeNum, String schoolNum, String checkItem, String subjectNums, String subjectCount) {
        StringBuffer sql = new StringBuffer();
        StringBuffer regSql = new StringBuffer();
        Map args = new HashMap();
        String schStr = "-1".equals(schoolNum) ? "regexaminee" : "(select examPaperNum,studentId from regexaminee where schoolNum={schoolNum} )";
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("subjectNums", subjectNums);
        args.put("subjectCount", subjectCount);
        if ("0".equals(checkItem)) {
            int subCount = subjectNums.substring(0, subjectNums.length() - 1).split(Const.STRING_SEPERATOR).length;
            args.put("subCount", Integer.valueOf(subCount));
            StringBuffer epSql = new StringBuffer();
            epSql.append("select examPaperNum from exampaper ");
            epSql.append("where examNum={examNum}  and gradeNum={gradeNum}  and subjectNum in ({subjectNums[]}) ");
            regSql.append("select reg.studentId from ");
            regSql.append(schStr);
            regSql.append(" reg ");
            regSql.append("inner join (");
            regSql.append(epSql);
            regSql.append(") ep on ep.examPaperNum=reg.examPaperNum ");
            regSql.append("group by reg.studentId ");
            regSql.append("having count(distinct reg.examPaperNum)={subCount}");
        } else {
            StringBuffer epSql2 = new StringBuffer();
            epSql2.append("select examPaperNum from exampaper ");
            epSql2.append("where examNum={examNum}  and gradeNum={gradeNum}  ");
            regSql.append("select reg.studentId from ");
            regSql.append(schStr);
            regSql.append(" reg ");
            regSql.append("inner join (");
            regSql.append(epSql2);
            regSql.append(") ep on ep.examPaperNum=reg.examPaperNum ");
            regSql.append("group by reg.studentId ");
            regSql.append("having count(distinct reg.examPaperNum)>{subjectCount}");
        }
        sql.append("select count(1) from (");
        sql.append(regSql);
        sql.append(") stuReg ");
        return this.dao._queryInt(sql.toString(), args).intValue();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getSubjectExceptionData(String examNum, String gradeNum, String schoolNum, String checkItem, String subjectNums, String subjectCount, int limitCount) {
        StringBuffer sql = new StringBuffer();
        StringBuffer regSql = new StringBuffer();
        StringBuffer enSql = new StringBuffer();
        enSql.append("select distinct studentId,examineeNum from examinationnum ");
        enSql.append("where examNum={examNum}  and gradeNum={gradeNum} ");
        String schStr = "-1".equals(schoolNum) ? "regexaminee" : "(select examPaperNum,studentId from regexaminee where schoolNum={schoolNum} )";
        String limitStr = limitCount > 0 ? " limit {limitCount} " : "";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("limitCount", Integer.valueOf(limitCount));
        args.put("subjectNums", subjectNums);
        args.put("subjectCount", subjectCount);
        if ("0".equals(checkItem)) {
            int subCount = subjectNums.substring(0, subjectNums.length() - 1).split(Const.STRING_SEPERATOR).length;
            args.put("subCount", Integer.valueOf(subCount));
            StringBuffer epSql = new StringBuffer();
            epSql.append("select examPaperNum,subjectNum from exampaper ");
            epSql.append("where examNum={examNum}  and gradeNum={gradeNum}  and subjectNum in ({subjectNums[]}) ");
            regSql.append("select reg.studentId,GROUP_CONCAT(distinct sub.subjectName order by sub.orderNum) subjectName from ");
            regSql.append(schStr);
            regSql.append(" reg ");
            regSql.append("inner join (");
            regSql.append(epSql);
            regSql.append(") ep on ep.examPaperNum=reg.examPaperNum ");
            regSql.append("left join `subject` sub on sub.subjectNum=ep.subjectNum ");
            regSql.append("group by reg.studentId ");
            regSql.append("having count(distinct reg.examPaperNum)={subCount}");
        } else {
            StringBuffer epSql2 = new StringBuffer();
            epSql2.append("select examPaperNum,subjectNum from exampaper ");
            epSql2.append("where examNum={examNum}  and gradeNum={gradeNum}  ");
            regSql.append("select reg.studentId,GROUP_CONCAT(distinct sub.subjectName order by sub.orderNum) subjectName from ");
            regSql.append(schStr);
            regSql.append(" reg ");
            regSql.append("inner join (");
            regSql.append(epSql2);
            regSql.append(") ep on ep.examPaperNum=reg.examPaperNum ");
            regSql.append("left join `subject` sub on sub.subjectNum=ep.subjectNum ");
            regSql.append("group by reg.studentId ");
            regSql.append("having count(distinct reg.examPaperNum)>{subjectCount}");
        }
        sql.append("select sch.schoolName,bg.gradeName,cla.className,stu.studentName,stu.studentId,en.examineeNum,stuReg.subjectName from (");
        sql.append(regSql);
        sql.append(") stuReg ");
        sql.append("left join (");
        sql.append(enSql);
        sql.append(") en on en.studentId=stuReg.studentId ");
        sql.append("left join student stu on stu.id=stuReg.studentId ");
        sql.append("left join school sch on sch.id=stu.schoolNum ");
        sql.append("left join basegrade bg on bg.gradeNum=stu.gradeNum ");
        sql.append("left join class cla on cla.id=stu.classNum ");
        sql.append("group by stuReg.studentId ");
        sql.append("order by convert(sch.schoolName using gbk),convert(cla.className using gbk),convert(stu.studentName using gbk) ");
        sql.append(limitStr);
        return this.dao._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void exportSubjectExceptionData(String examNum, String gradeNum, String schoolNum, String checkItem, String subjectNums, String subjectCount, String dirPath) {
        FileUtil.del(dirPath);
        List<Map<String, Object>> dataList = getSubjectExceptionData(examNum, gradeNum, schoolNum, checkItem, subjectNums, subjectCount, -1);
        BigExcelWriter writer = ExcelUtil.getBigWriter(dirPath);
        writer.addHeaderAlias("schoolName", "学校");
        writer.addHeaderAlias("gradeName", "年级");
        writer.addHeaderAlias("className", "班级");
        writer.addHeaderAlias("studentName", "姓名");
        writer.addHeaderAlias(Const.EXPORTREPORT_studentId, "ID号");
        writer.addHeaderAlias("examineeNum", "考号");
        writer.addHeaderAlias("subjectName", "科目");
        writer.setColumnWidth(0, 20);
        writer.setColumnWidth(4, 20);
        writer.setColumnWidth(5, 20);
        writer.setColumnWidth(6, 20);
        writer.setOnlyAlias(true);
        writer.write(dataList);
        writer.close();
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void deleteShangxianData(String examNum, String schoolNum, String gradeNum, String studentType, String xuankezuhe, String statisticType, String source, String fenshuyuan, String huaxianType, String limitNum) {
        StringBuffer delSql = new StringBuffer();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("statisticType", statisticType);
        args.put("source", source);
        args.put("fenshuyuan", fenshuyuan);
        args.put("huaxianType", huaxianType);
        delSql.append("delete from onlineindicator ");
        delSql.append("where examNum={examNum} and schoolNum={schoolNum} and gradeNum={gradeNum} and studentType={studentType} and xuankezuhe={xuankezuhe} and statisticType={statisticType} and source={source} and fenshuyuan={fenshuyuan} and scoretype={huaxianType}");
        this.dao._execute(delSql.toString(), args);
        this.dao._execute("DELETE from onlinesample WHERE examNum={examNum} and gradeNum={gradeNum} and studentType={studentType} and xuankezuhe={xuankezuhe} and statisticType={statisticType}  and source={source} and fenshuyuan={fenshuyuan}", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getallschoolList(String examNum, String schoolName, String gradeNum, String studentType, String xuankezuhe, String statisticType, String source, String fenshuyuan, String insertuser, String limitNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("statisticType", statisticType);
        args.put("source", source);
        args.put("fenshuyuan", fenshuyuan);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("insertuser", insertuser);
        args.put("limitNum", limitNum);
        args.put("schoolName", "%" + schoolName + "%");
        Integer count = this.dao._queryInt("select count(1) from onlinesampleschool where examNum={examNum} and gradeNum={gradeNum} and studentType={studentType} and xuankezuhe={xuankezuhe} and statisticType={statisticType} and source={source} and fenshuyuan={fenshuyuan}", args);
        if (count.intValue() == 0) {
            this.dao._execute("INSERT INTO onlinesampleschool(`examNum`,`schoolNum`,`gradeNum`,`studentType`,`insertUser`,`insertDate`,`updateUser`,`updateDate`,`sxtype`,`showLevel`,`xuankezuhe`, `statisticType`,`source`,`fenshuyuan`)  SELECT DISTINCT {examNum},en.schoolNum,{gradeNum},{studentType},{insertuser},NOW(),{insertuser},NOW(),'0','F',{xuankezuhe},{statisticType},{source},{fenshuyuan} from  (SELECT DISTINCT schoolNum,examNum,gradeNum from examinationnum WHERE examNum={examNum} AND gradeNum={gradeNum}) en LEFT JOIN (SELECT * from  `data` WHERE type='21' limit {limitNum}) d ON 1=1 LEFT JOIN basegrade bs ON bs.gradeNum=en.gradeNum LEFT JOIN onlinesampleschoolall osa ON en.schoolNum=osa.schoolNum  AND osa.stageNum=bs.stage and osa.type=d.value and osa.xuankezuhe={xuankezuhe} where osa.num is not null", args);
        }
        String sql = "SELECT s.id schoolNum,s.schoolName,IF(os.schoolNum is null,0,1) ischecked,d.value,IFNULL(osa.num,'') num,d.name from (SELECT DISTINCT schoolNum,examNum,gradeNum from examinationnum WHERE examNum={examNum} AND gradeNum={gradeNum})en  INNER JOIN school s ON en.schoolNum=s.id ";
        if (null != schoolName && schoolName.length() > 0) {
            sql = sql + " and s.schoolName LIKE {schoolName} ";
        }
        return this.dao._queryMapList(sql + "LEFT JOIN  (SELECT * from  `data` WHERE type='21' limit {limitNum} ) d on 1=1 LEFT JOIN basegrade bs ON bs.gradeNum=en.gradeNum LEFT JOIN onlinesampleschoolall osa ON s.id=osa.schoolNum  AND osa.stageNum=bs.stage and osa.type=d.value and osa.xuankezuhe={xuankezuhe} and osa.studentType={studentType} LEFT JOIN (select * from onlinesampleschool os WHERE os.studentType={studentType} and os.xuankezuhe={xuankezuhe} and os.statisticType={statisticType} and os.source={source} and os.fenshuyuan={fenshuyuan}) os ON s.id=os.schoolNum AND os.examNum={examNum} AND os.gradeNum={gradeNum} ORDER BY s.schoolNum,d.value ", null, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer ischeck(String examNum, String schoolNum, String gradeNum, String studentType, String xuankezuhe, String statisticType, String source, String fenshuyuan, String insertuser, String ischeck) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("statisticType", statisticType);
        args.put("source", source);
        args.put("fenshuyuan", fenshuyuan);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("insertuser", insertuser);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        if (ischeck.equals("1") || ischeck.equals("true")) {
            Integer count = this.dao._queryInt("select count(1) from onlinesampleschool where examNum={examNum} and gradeNum={gradeNum} and studentType={studentType} and xuankezuhe={xuankezuhe} and statisticType={statisticType} and source={source} and fenshuyuan={fenshuyuan} and schoolNum={schoolNum}", args);
            if (count.intValue() == 0) {
                this.dao._execute("INSERT INTO onlinesampleschool(`examNum`,`schoolNum`,`gradeNum`,`studentType`,`insertUser`,`insertDate`,`updateUser`,`updateDate`,`sxtype`,`showLevel`,`xuankezuhe`, `statisticType`,`source`,`fenshuyuan`) \nVALUES ({examNum},{schoolNum},{gradeNum},{studentType},{insertuser},NOW(),{insertuser},NOW(),'0','F',{xuankezuhe},{statisticType},{source},{fenshuyuan}) ", args);
            }
        } else {
            this.dao._execute("DELETE FROM onlinesampleschool WHERE examNum={examNum} AND schoolNum={schoolNum} AND gradeNum={gradeNum} AND studentType={studentType} AND xuankezuhe={xuankezuhe} AND statisticType={statisticType} AND source={source} AND fenshuyuan={fenshuyuan} ", args);
        }
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getschoolsList(String gradeNum, String studentType, String xuankezuhe, String schoolName) {
        String sql = "SELECT s.id schoolNum,s.schoolName,d.name,d.value, osa.stageNum,IFNULL(osa.num,'') num,ifnull(osa.beizhu,'') beizhu,ifnull(osa.stunum,'') stunum  from school s LEFT JOIN (SELECT * from  `data` WHERE type='21') d ON 1=1 LEFT JOIN  (SELECT osa.* from onlinesampleschoolall osa  LEFT JOIN basegrade bg ON bg.stage=osa.stageNum  WHERE  bg.gradeNum={gradeNum} AND osa.studentType={studentType} and osa.xuankezuhe={xuankezuhe} ) osa ON osa.type=d.value and osa.schoolNum=s.id  where s.isDelete='F' ";
        if (null != schoolName && schoolName.length() > 0) {
            sql = sql + " and s.schoolName LIKE {schoolName} ";
        }
        String sql2 = sql + "ORDER BY s.schoolNum,d.value";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("schoolName", "%" + schoolName + "%");
        return this.dao._queryMapList(sql2, null, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getxueduanList() {
        Map args = new HashMap();
        args.put("type", "5");
        return this.dao._queryMapList("SELECT type,name,`value` num from `data` WHERE type={type}", null, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getxueduan(String gradeNum) {
        Map args = new HashMap();
        args.put("type", "5");
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao._queryMapList("SELECT d.value num,d.name from basegrade bg LEFT JOIN `data` d ON  bg.stage=d.value AND d.type={type} where bg.gradeNum={gradeNum} ", null, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getpicilist() {
        return this.dao._queryMapList("SELECT * from  `data` WHERE type='21' order by value", null, null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getschools() {
        return this.dao._queryMapList("SELECT id schoolNum,schoolName from school where isDelete='F' order by schoolNum ", null, null);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer updateschoolsList(String studentType, String xuankezuhe, String schoolNum, String picitype, String stageNum, String num, String insertuser) {
        String updateSql;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("picitype", picitype);
        args.put("xuankezuhe", xuankezuhe);
        args.put("stageNum", stageNum);
        args.put("num", num);
        args.put("insertuser", insertuser);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT ifnull(osa.num,'')num,count(1) count from onlinesampleschoolall osa  WHERE osa.schoolNum={schoolNum} AND osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}", args);
        String num2 = map.get("num").toString();
        Integer count = Integer.valueOf(Integer.parseInt(map.get("count").toString()));
        Integer rows = 0;
        if (count.intValue() == 0 && !num.equals("")) {
            rows = Integer.valueOf(this.dao._execute("INSERT INTO onlinesampleschoolall(`schoolNum`,`stageNum`,`studentType`,`type`,`num`,`insertUser`,`insertDate`,`updateUser`,`updateDate`,`sxtype`, `xuankezuhe`)  VALUES ({schoolNum},{stageNum},{studentType},{picitype},{num},{insertuser},NOW(),{insertuser},NOW(),'0',{xuankezuhe}) ", args));
        } else {
            if (num.equals("")) {
                updateSql = "UPDATE onlinesampleschoolall osa  SET osa.num=null,osa.updateUser={insertuser},osa.updateDate=NOW() WHERE osa.schoolNum={schoolNum} AND osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}";
            } else {
                updateSql = "UPDATE onlinesampleschoolall osa SET osa.num={num},osa.updateUser={insertuser},osa.updateDate=NOW() WHERE osa.schoolNum={schoolNum} AND osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}";
            }
            if (!num2.equals(num)) {
                rows = Integer.valueOf(this.dao._execute(updateSql, args));
            }
        }
        return rows;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer updateschoolsList2(String studentType, String xuankezuhe, String schoolNum, String picitype, String stageNum, String num, String insertuser, String stunum, String beizhu) {
        String updateSql;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("picitype", picitype);
        args.put("xuankezuhe", xuankezuhe);
        args.put("stageNum", stageNum);
        args.put("num", num);
        args.put("insertuser", insertuser);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT ifnull(osa.num,'')num,ifnull(osa.stunum,'') stunum,count(1) count from onlinesampleschoolall osa  WHERE osa.schoolNum={schoolNum} AND osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}", args);
        String num2 = map.get("num").toString();
        map.get("stunum").toString();
        Integer count = Integer.valueOf(Integer.parseInt(map.get("count").toString()));
        Integer rows = 0;
        if (count.intValue() == 0 && !num.equals("")) {
            rows = Integer.valueOf(this.dao._execute("INSERT INTO onlinesampleschoolall(`schoolNum`,`stageNum`,`studentType`,`type`,`num`,`insertUser`,`insertDate`,`updateUser`,`updateDate`,`sxtype`, `xuankezuhe`)  VALUES ({schoolNum},{stageNum},{studentType},{picitype},{num},{insertuser},NOW(),{insertuser},NOW(),'0',{xuankezuhe}) ", args));
        } else {
            if (num.equals("")) {
                updateSql = "UPDATE onlinesampleschoolall osa  SET osa.num=null,osa.updateUser={insertuser},osa.updateDate=NOW() WHERE osa.schoolNum={schoolNum} AND osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}";
            } else {
                updateSql = "UPDATE onlinesampleschoolall osa SET osa.num={num},osa.updateUser={insertuser},osa.updateDate=NOW() WHERE osa.schoolNum={schoolNum} AND osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}";
            }
            if (!num2.equals(num)) {
                rows = Integer.valueOf(this.dao._execute(updateSql, args));
            }
        }
        return rows;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer updateschoolsbeizhu(String studentType, String xuankezuhe, String schoolNum, String stageNum, String txt, String insertuser) {
        Integer valueOf;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("stageNum", stageNum);
        args.put("txt", txt);
        args.put("insertuser", insertuser);
        List<Map<String, Object>> typelist = getpicilist();
        for (int i = 0; i < typelist.size(); i++) {
            args.put("picitype", typelist.get(i).get("value"));
            Integer count = this.dao._queryInt("SELECT count(1) from onlinesampleschoolall osa WHERE osa.schoolNum={schoolNum} AND osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}", args);
            if (count.intValue() == 0 && !txt.equals("")) {
                valueOf = Integer.valueOf(this.dao._execute("INSERT INTO onlinesampleschoolall(`schoolNum`,`stageNum`,`studentType`,`type`,`insertUser`,`insertDate`,`updateUser`,`updateDate`,`sxtype`, `xuankezuhe`,beizhu)  VALUES ({schoolNum},{stageNum},{studentType},{picitype},{insertuser},NOW(),{insertuser},NOW(),'0',{xuankezuhe},{txt}) ", args));
            } else {
                valueOf = Integer.valueOf(this.dao._execute("UPDATE onlinesampleschoolall osa SET osa.beizhu={txt},osa.updateUser={insertuser},osa.updateDate=NOW() WHERE osa.schoolNum={schoolNum} AND  osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}", args));
            }
        }
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer updateschoolsstunum(String studentType, String xuankezuhe, String schoolNum, String stageNum, String stunum, String insertuser) {
        Integer valueOf;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("stageNum", stageNum);
        args.put("stunum", stunum);
        args.put("insertuser", insertuser);
        List<Map<String, Object>> typelist = getpicilist();
        for (int i = 0; i < typelist.size(); i++) {
            args.put("picitype", typelist.get(i).get("value"));
            Integer count = this.dao._queryInt("SELECT count(1) from onlinesampleschoolall osa  WHERE osa.schoolNum={schoolNum} AND osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}", args);
            if (count.intValue() == 0 && !stunum.equals("")) {
                valueOf = Integer.valueOf(this.dao._execute("INSERT INTO onlinesampleschoolall(`schoolNum`,`stageNum`,`studentType`,`type`,`insertUser`,`insertDate`,`updateUser`,`updateDate`,`sxtype`, `xuankezuhe`,stunum)  VALUES ({schoolNum},{stageNum},{studentType},{picitype},{insertuser},NOW(),{insertuser},NOW(),'0',{xuankezuhe},{stunum}) ", args));
            } else {
                String updateSql = "UPDATE onlinesampleschoolall osa SET osa.stunum={stunum},osa.updateUser={insertuser},osa.updateDate=NOW() WHERE osa.schoolNum={schoolNum} AND osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}";
                if (stunum.equals("")) {
                    updateSql = "UPDATE onlinesampleschoolall osa SET osa.stunum=null,osa.updateUser={insertuser},osa.updateDate=NOW() WHERE osa.schoolNum={schoolNum} AND osa.stageNum={stageNum} AND osa.studentType={studentType} AND osa.type={picitype} AND osa.xuankezuhe={xuankezuhe}";
                }
                valueOf = Integer.valueOf(this.dao._execute(updateSql, args));
            }
        }
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Integer updateonlinesample(String examNum, String gradeNum, String studentType, String xuankezuhe, String statisticType, String source, String fenshuyuan, String insertuser, JSONArray inputArr) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("statisticType", statisticType);
        args.put("source", source);
        args.put("fenshuyuan", fenshuyuan);
        args.put("insertUser", insertuser);
        for (int i = 0; i < inputArr.size(); i++) {
            JSONObject input = inputArr.getJSONObject(i);
            String[] id = String.valueOf(input.get("id")).split("_");
            String value = String.valueOf(input.get("value"));
            String picitype = id[1];
            args.put("picitype", picitype);
            args.put("num", value);
            Integer count = this.dao._queryInt("SELECT count(1) from onlinesample WHERE examNum={examNum} AND gradeNum={gradeNum} AND studentType={studentType} and type={picitype} and xuankezuhe={xuankezuhe} and statisticType={statisticType} and source={source} and fenshuyuan={fenshuyuan} ", args);
            if (count.intValue() > 0) {
                this.dao._execute("UPDATE onlinesample SET num={num} WHERE examNum={examNum} AND gradeNum={gradeNum} AND studentType={studentType} and type={picitype} and xuankezuhe={xuankezuhe} and statisticType={statisticType} and source={source} and fenshuyuan={fenshuyuan}", args);
            } else {
                this.dao._execute("INSERT INTO onlinesample(`examNum`,`gradeNum`,`studentType`, `type`, `num`,`insertUser`,`insertDate`,`updateUser`,`updateDate`,`sxtype`,`showLevel`,`xuankezuhe`,`statisticType`,source,`fenshuyuan`) \nVALUES ({examNum},{gradeNum},{studentType},{picitype},{num},{insertUser},NOW(),{insertUser},NOW(),'0','F',{xuankezuhe},{statisticType},{source},{fenshuyuan}) ", args);
            }
        }
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String getIsFufen(String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        if (null == this.dao._queryObject("select id from jisuanzhonglei where gradeNum={gradeNum} and jisuanzhonglei='1' and isEnabled='1' limit 1", args)) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getSettingParam(String examNum, String schoolNum, String gradeNum, String studentType, String xuankezuhe, String statisticType, String source, String fenshuyuan) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("xuankezuhe", xuankezuhe);
        args.put("statisticType", statisticType);
        args.put("source", source);
        args.put("fenshuyuan", fenshuyuan);
        return this.dao._queryMapList("select scoretype,max(type) maxType from onlineindicator where examNum={examNum} and schoolNum={schoolNum} and gradeNum={gradeNum} and studentType={studentType} and xuankezuhe={xuankezuhe} and statisticType={statisticType} and source={source} and fenshuyuan={fenshuyuan} group by scoretype", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String doimportschools(List<List<Object>> schoollist, String stageNum, String xuankezuhe, String studentType, String insertuser) {
        List<Map<String, Object>> picilist = getpicilist();
        Map args = new HashMap();
        Pattern pattern = Pattern.compile("^[0-9]*[1-9][0-9]*$");
        for (int i = 0; i < schoollist.size(); i++) {
            String schoolName = schoollist.get(i).get(0).toString();
            args.put("schoolName", schoolName);
            List<Map<String, Object>> schlist = this.dao._queryMapList("select DISTINCT ifnull(id,'')schoolNum from school where schoolName={schoolName}", null, args);
            if (schlist.size() == 0) {
                return "第" + (i + 2) + "行学校不存在，请检查后重新提交！";
            }
            String schoolNum = schlist.get(0).get(Const.EXPORTREPORT_schoolNum).toString();
            if (schoollist.get(i).size() > 1) {
                String stunum = schoollist.get(i).get(1).toString();
                if (!stunum.equals("") && !pattern.matcher(stunum).matches()) {
                    return "第" + (i + 2) + "行参考人数列必须为正整数，请检查后重新提交！";
                }
                for (int j = 0; j < picilist.size(); j++) {
                    if (schoollist.get(i).size() > j + 2) {
                        String picitype = picilist.get(j).get("value").toString();
                        String num = schoollist.get(i).get(j + 2).toString();
                        if (!num.equals("") && !pattern.matcher(num).matches()) {
                            return "第" + (i + 2) + "行,第" + (j + 3) + "列必须为正整数，请检查后重新提交！";
                        }
                        if (j < schoollist.get(i).size() - 4) {
                            String endnumStr = schoollist.get(i).get(j + 3).toString().equals("") ? "0" : schoollist.get(i).get(j + 3).toString();
                            String numStr = schoollist.get(i).get(j + 2).toString().equals("") ? "0" : schoollist.get(i).get(j + 2).toString();
                            if (Integer.parseInt(endnumStr) < Integer.parseInt(numStr)) {
                                return "第" + (i + 2) + "行的外线人数不得小于内线人数，请检查后重新提交！";
                            }
                        }
                        updateschoolsList(studentType, xuankezuhe, schoolNum, picitype, stageNum, num, insertuser);
                    }
                }
                String beizhu = "";
                if (schoollist.get(i).size() > 7) {
                    beizhu = schoollist.get(i).get(7).toString();
                }
                updateschoolsbeizhu(studentType, xuankezuhe, schoolNum, stageNum, beizhu, insertuser);
                updateschoolsstunum(studentType, xuankezuhe, schoolNum, stageNum, stunum, insertuser);
            }
        }
        return "导入成功！";
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> getShouxuanMapList(String loginUserId) {
        List<Map<String, Object>> sxkmMapList = this.dao._queryMapList("select id,subjectCombineName,autoCodeNum,orderNum from subjectcombine where isParent='1' ", TypeEnum.StringObject, null);
        if (CollUtil.isEmpty(sxkmMapList)) {
            return null;
        }
        List<Map<String, Object>> emptyMapList = (List) sxkmMapList.stream().filter(m -> {
            return StrUtil.isEmpty(Convert.toStr(m.get("autoCodeNum"), ""));
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(emptyMapList)) {
            OptionalInt res_maxNum = sxkmMapList.stream().mapToInt(m2 -> {
                return Convert.toInt(m2.get("autoCodeNum"), 0).intValue();
            }).max();
            int maxNum = res_maxNum.isPresent() ? res_maxNum.getAsInt() : 0;
            List<Map<String, Object>> emptyMapList2 = (List) emptyMapList.stream().sorted(Comparator.comparing(m3 -> {
                return Convert.toInt(m3.get("orderNum"), 0);
            })).collect(Collectors.toList());
            int len = emptyMapList2.size();
            for (int i = 0; i < len; i++) {
                maxNum++;
                Map<String, Object> obj = emptyMapList2.get(i);
                obj.put("autoCodeNum", Integer.valueOf(maxNum));
            }
            this.dao._batchExecute("update subjectcombine set autoCodeNum={autoCodeNum} where id={id}", emptyMapList2);
            Baseinfolog baseinfolog = new Baseinfolog();
            baseinfolog.setOperate("更新新增的首选科目代号");
            baseinfolog.setInsertUser(loginUserId);
            baseinfolog.setInsertDate(DateUtil.getCurrentTime());
            baseinfolog.setDescription("当首选科目代号为空时，创建（编辑）考试查看考号自动编排规则时会更新首先科目代号");
            baseinfolog.setIsDelete("F");
            this.dao.save(baseinfolog);
        }
        return (List) sxkmMapList.stream().sorted(Comparator.comparing(m4 -> {
            return Convert.toInt(m4.get("autoCodeNum"), 0);
        })).collect(Collectors.toList());
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void saveAutoRule(Map<String, Object> args) {
        Object _id = this.dao._queryObject("select id from autorule_examinationnumber where examNum={examNum}", args);
        if (null != _id) {
            args.put("_id", _id);
            this.dao._execute("delete from autorule_examinationnumber where id={_id}", args);
        }
        this.dao.save("autorule_examinationnumber", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public Map<String, Object> getAutoRuleByExam(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._querySimpleMap("select type,code_nianji,code_ruxuenianfen,code_xuexiao,code_kelei,code_shouxuan,code_shunxu,code_guding,yuzhikaohao from autorule_examinationnumber where examNum={examNum}", args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void divideExamRoomByClass(Map<String, Object> args) {
        String examNum = Convert.toStr(args.get(Const.EXPORTREPORT_examNum));
        String stuType = Convert.toStr(args.get("stuType"));
        String classType = Convert.toStr(args.get("classType"));
        String orderType = Convert.toStr(args.get("orderType"));
        String oldExamNum = Convert.toStr(args.get("oldExamNum"));
        String isXuanke = Convert.toStr(args.get("isXuanke"));
        String loginUserId = Convert.toStr(args.get("loginUserId"));
        String currentTime = Convert.toStr(args.get("currentTime"));
        Map<String, Object> autoRule = getAutoRuleByExam(examNum);
        int maxSchoolNumLen = getMaxSchoolNumLen();
        Map<String, Object> testingCentreIdToSchoolCount = getTestingCentreIdToSchoolCount(examNum);
        List<Map<String, Object>> subjectNumAndXuankezuheList = new ArrayList();
        if ("1".equals(isXuanke)) {
            subjectNumAndXuankezuheList = getSubjectNumAndXuankezuheList();
        }
        List<Map<String, Object>> jsonArray = (List) args.get("jsonArray");
        Map<String, List<Map<String, Object>>> gra_treeDataMap = (Map) jsonArray.stream().collect(Collectors.groupingBy(m -> {
            return Convert.toStr(m.get("gradeStr"));
        }));
        List<Map<String, Object>> finalSubjectNumAndXuankezuheList = subjectNumAndXuankezuheList;
        gra_treeDataMap.forEach((gradeNum, treeDataOfOneGrade) -> {
            Map<String, List<Map<String, Object>>> sch_treeDataMap = (Map) treeDataOfOneGrade.stream().sorted(Comparator.comparing(m2 -> {
                return Convert.toLong(m2.get("testingCentreStr"));
            })).collect(Collectors.groupingBy(m3 -> {
                return Convert.toStr(m3.get("schoolStr"));
            }));
            sch_treeDataMap.forEach((schoolNum, treeDataOfOneSchool) -> {
                int[] erNum = {1};
                String testingCentreId = Convert.toStr(((Map) treeDataOfOneSchool.get(0)).get("testingCentreStr"));
                int schoolCountOfTc = Convert.toInt(testingCentreIdToSchoolCount.get(testingCentreId), 1).intValue();
                Map args_stu = new HashMap();
                args_stu.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args_stu.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                args_stu.put("stuType", stuType);
                List<Map<String, Object>> stuList = this.dao._queryMapList("SELECT stu.id sid,stu.gradeNum,bg.stage,stu.jie,sch.schoolNum schoolCode,cla.studentType,stu.studentNum,stu.yzexaminationnum,sc2.autoCodeNum,stu.classNum,sch.schoolName,cla.className,bg.gradeName,stu.studentName,stu.subjectCombineNum from student stu LEFT JOIN subjectcombine sc on sc.subjectCombineNum = stu.subjectCombineNum LEFT JOIN subjectcombine sc2 on sc2.subjectCombineNum = sc.pid and sc2.isParent='1' LEFT JOIN school sch on sch.id = stu.schoolNum LEFT JOIN basegrade bg on bg.gradeNum = stu.gradeNum LEFT JOIN class cla on cla.id = stu.classNum where stu.gradeNum={gradeNum} and stu.schoolNum={schoolNum} and stu.isDelete = 'F' and stu.nodel = '0' and stu.type in ({stuType[]}) group by stu.id ", TypeEnum.StringObject, args_stu);
                Map<String, List<Map<String, Object>>> cla_stuDataMap = (Map) stuList.stream().collect(Collectors.groupingBy(m4 -> {
                    return Convert.toStr(m4.get(Const.EXPORTREPORT_classNum));
                }));
                validateExamineeNum(autoRule, stuList);
                AtomicInteger shunxu_sch = new AtomicInteger();
                int charLength_erNum = getCharLengthOfInteger(cla_stuDataMap.size(), 2);
                cla_stuDataMap.forEach((classNum, stuDataOfOneClass) -> {
                    if ("hunpai".equals(orderType)) {
                        Collections.shuffle(stuDataOfOneClass);
                    } else if ("shunpai".equals(orderType)) {
                        args_stu.put("oldExamNum", oldExamNum);
                        args_stu.put(Const.EXPORTREPORT_subjectNum, "-1");
                        args_stu.put(Const.EXPORTREPORT_classNum, classNum);
                        Map<Object, Object> stuTotalScoreDataOfOneClass = this.dao._queryOrderMap("select studentId,totalScore from studentlevel where examNum={oldExamNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and schoolNum={schoolNum} and classNum={classNum} and statisticType='0' and source='0' and xuankezuhe='0' ", TypeEnum.StringObject, args_stu);
                        stuDataOfOneClass.forEach(oneStuObj -> {
                            if (stuTotalScoreDataOfOneClass.containsKey(oneStuObj.get(Const.EXPORTREPORT_studentId))) {
                                oneStuObj.put("totalScore", stuTotalScoreDataOfOneClass.get(oneStuObj.get(Const.EXPORTREPORT_studentId)));
                            } else {
                                oneStuObj.put("totalScore", 0);
                            }
                        });
                        Comparator<Map<String, Object>> c1 = Comparator.comparing(m5 -> {
                            return Convert.toBigDecimal(m5.get("totalScore"));
                        });
                        stuDataOfOneClass = (List) stuDataOfOneClass.stream().sorted(c1.reversed()).collect(Collectors.toList());
                    }
                    int sLen = stuDataOfOneClass.size();
                    int charLength_seatNum = getCharLengthOfInteger(sLen, 2);
                    for (int s = 0; s < sLen; s++) {
                        shunxu_sch.getAndIncrement();
                        Map<String, Object> oneStuObj2 = (Map) stuDataOfOneClass.get(s);
                        Object[] objArr = new Object[2];
                        objArr[0] = schoolCountOfTc > 1 ? oneStuObj2.get("schoolCode") : "";
                        objArr[1] = StrUtil.fillBefore(Convert.toStr(Integer.valueOf(erNum[0])), '0', charLength_erNum);
                        oneStuObj2.put("examinationRoomNum", StrUtil.format("{}{}", objArr));
                        oneStuObj2.put("testLocation", StrUtil.format("{}{}", new Object[]{oneStuObj2.get("schoolName"), oneStuObj2.get("className")}));
                        oneStuObj2.put("seatNum", StrUtil.fillBefore(Convert.toStr(Integer.valueOf(s + 1)), '0', charLength_seatNum));
                        if ("1".equals(Convert.toStr(autoRule.get("type")))) {
                            Object[] objArr2 = new Object[8];
                            objArr2[0] = "1".equals(Convert.toStr(autoRule.get("code_nianji"))) ? oneStuObj2.get(Const.EXPORTREPORT_gradeNum) : "";
                            objArr2[1] = "1".equals(Convert.toStr(autoRule.get("code_ruxuenianfen"))) ? StrUtil.subSufByLength(Convert.toStr(oneStuObj2.get("stage")), 1) : "";
                            objArr2[2] = "1".equals(Convert.toStr(autoRule.get("code_ruxuenianfen"))) ? StrUtil.subSufByLength(Convert.toStr(oneStuObj2.get("jie")), 2) : "";
                            objArr2[3] = "1".equals(Convert.toStr(autoRule.get("code_xuexiao"))) ? StrUtil.fillBefore(Convert.toStr(oneStuObj2.get("schoolCode")), '0', maxSchoolNumLen) : "";
                            objArr2[4] = "1".equals(Convert.toStr(autoRule.get("code_kelei"))) ? oneStuObj2.get(Const.EXPORTREPORT_studentType) : "";
                            objArr2[5] = "1".equals(Convert.toStr(autoRule.get("code_shouxuan"))) ? Convert.toStr(oneStuObj2.get("autoCodeNum"), "0") : "";
                            objArr2[6] = "1".equals(Convert.toStr(autoRule.get("code_shunxu"))) ? StrUtil.fillBefore(Convert.toStr(Integer.valueOf(shunxu_sch.get())), '0', 4) : "";
                            objArr2[7] = "1".equals(Convert.toStr(autoRule.get("code_guding"))) ? StrUtil.subSufByLength(Convert.toStr(oneStuObj2.get("studentNum")), 4) : "";
                            String examineeNum = StrUtil.format("{}{}{}{}{}{}{}{}", objArr2);
                            oneStuObj2.put("examineeNum", examineeNum);
                        } else {
                            oneStuObj2.put("examineeNum", oneStuObj2.get("yzexaminationnum"));
                        }
                    }
                    erNum[0] = erNum[0] + 1;
                });
                Map<String, List<Map<String, Object>>> cla_treeDataMap = (Map) treeDataOfOneSchool.stream().collect(Collectors.groupingBy(m5 -> {
                    return Convert.toStr(m5.get("classStr"));
                }));
                Set<String> subSet = new HashSet<>();
                if ("1".equals(classType)) {
                    cla_treeDataMap.forEach((classNum2, treeDataOfOneClass) -> {
                        List<Map<String, Object>> stuDataOfOneClass2 = (List) cla_stuDataMap.get(classNum2);
                        if (CollUtil.isEmpty(stuDataOfOneClass2)) {
                            return;
                        }
                        Map<String, Object> oneClassObj = stuDataOfOneClass2.get(0);
                        treeDataOfOneClass.forEach(oneSubObj -> {
                            String subjectNum = Convert.toStr(oneSubObj.get("subjectStr"));
                            if (subSet.add(subjectNum)) {
                                autoDeleteEn(examNum, schoolNum, gradeNum, subjectNum, null);
                            }
                            List<Map<String, Object>> stuDataOfOneClass_filter = filterStuList(subjectNum, finalSubjectNumAndXuankezuheList, stuDataOfOneClass2);
                            if (CollUtil.isEmpty(stuDataOfOneClass_filter)) {
                                return;
                            }
                            Examinationroom examinationroom = new Examinationroom();
                            examinationroom.setId(GUID.getGUIDStr());
                            examinationroom.setExaminationRoomNum(Convert.toStr(oneClassObj.get("examinationRoomNum")));
                            examinationroom.setExaminationRoomName(StrUtil.format("{}考场", new Object[]{oneClassObj.get("examinationRoomNum")}));
                            examinationroom.setGradeNum(Convert.toInt(gradeNum));
                            examinationroom.setInsertUser(loginUserId);
                            examinationroom.setInsertDate(currentTime);
                            examinationroom.setIsDelete("F");
                            examinationroom.setExamNum(Convert.toInt(examNum));
                            examinationroom.setTestingCentreId(testingCentreId);
                            examinationroom.setTestLocation(Convert.toStr(oneClassObj.get("testLocation")));
                            examinationroom.setSubjectNum(subjectNum);
                            this.dao.save(examinationroom);
                            stuDataOfOneClass_filter.forEach(oneStuObj -> {
                                Examinationnum examinationnum = new Examinationnum();
                                examinationnum.setExaminationRoomNum(examinationroom.getId());
                                examinationnum.setExamineeNum(Convert.toStr(oneStuObj.get("examineeNum")));
                                examinationnum.setStudentId(Convert.toStr(oneStuObj.get("sid")));
                                examinationnum.setInsertUser(loginUserId);
                                examinationnum.setInsertDate(currentTime);
                                examinationnum.setIsDelete("F");
                                examinationnum.setExamNum(Convert.toInt(examNum));
                                examinationnum.setSchoolNum(schoolNum);
                                examinationnum.setGradeNum(Convert.toInt(gradeNum));
                                examinationnum.setClassNum(classNum2);
                                examinationnum.setTestingCentreId(testingCentreId);
                                examinationnum.setSubjectNum(subjectNum);
                                examinationnum.setSeatNum(Convert.toStr(oneStuObj.get("seatNum")));
                                this.dao.save(examinationnum);
                            });
                        });
                    });
                } else if ("2".equals(classType)) {
                    Map<String, String> erIdOfsubAndCla = new HashMap<>();
                    cla_treeDataMap.forEach((levelClassNum, treeDataOfOneLevelClass) -> {
                        Map args_level = new HashMap();
                        args_level.put("levelClassNum", levelClassNum);
                        List<Object> levelstuList = this.dao._queryColList("select ls.sid from levelstudent ls where ls.classNum={levelClassNum}", args_level);
                        List<Map<String, Object>> stuDataOfOneLevelClass = (List) stuList.stream().filter(m6 -> {
                            return levelstuList.contains(m6.get("sid"));
                        }).collect(Collectors.toList());
                        Map<String, List<Map<String, Object>>> cla_stuDataMap2 = (Map) stuDataOfOneLevelClass.stream().collect(Collectors.groupingBy(m7 -> {
                            return Convert.toStr(m7.get(Const.EXPORTREPORT_classNum));
                        }));
                        cla_stuDataMap2.forEach((classNum3, stuDataOfOneClass2) -> {
                            if (CollUtil.isEmpty(stuDataOfOneClass2)) {
                                return;
                            }
                            Map<String, Object> oneClassObj = (Map) stuDataOfOneClass2.get(0);
                            treeDataOfOneLevelClass.forEach(oneSubObj -> {
                                String subjectNum = Convert.toStr(oneSubObj.get("subjectStr"));
                                if (subSet.add(subjectNum)) {
                                    autoDeleteEn(examNum, schoolNum, gradeNum, subjectNum, null);
                                }
                                List<Map<String, Object>> stuDataOfOneClass_filter = filterStuList(subjectNum, finalSubjectNumAndXuankezuheList, stuDataOfOneClass2);
                                if (CollUtil.isEmpty(stuDataOfOneClass_filter)) {
                                    return;
                                }
                                String key = StrUtil.format("{}_{}", new Object[]{classNum3, subjectNum});
                                if (!erIdOfsubAndCla.containsKey(key)) {
                                    Examinationroom examinationroom = new Examinationroom();
                                    examinationroom.setId(GUID.getGUIDStr());
                                    examinationroom.setExaminationRoomNum(Convert.toStr(oneClassObj.get("examinationRoomNum")));
                                    examinationroom.setExaminationRoomName(StrUtil.format("{}考场", new Object[]{oneClassObj.get("examinationRoomNum")}));
                                    examinationroom.setGradeNum(Convert.toInt(gradeNum));
                                    examinationroom.setInsertUser(loginUserId);
                                    examinationroom.setInsertDate(currentTime);
                                    examinationroom.setIsDelete("F");
                                    examinationroom.setExamNum(Convert.toInt(examNum));
                                    examinationroom.setTestingCentreId(testingCentreId);
                                    examinationroom.setTestLocation(Convert.toStr(oneClassObj.get("testLocation")));
                                    examinationroom.setSubjectNum(subjectNum);
                                    this.dao.save(examinationroom);
                                    erIdOfsubAndCla.put(key, examinationroom.getId());
                                }
                                stuDataOfOneClass_filter.forEach(oneStuObj -> {
                                    Examinationnum examinationnum = new Examinationnum();
                                    examinationnum.setExaminationRoomNum((String) erIdOfsubAndCla.get(key));
                                    examinationnum.setExamineeNum(Convert.toStr(oneStuObj.get("examineeNum")));
                                    examinationnum.setStudentId(Convert.toStr(oneStuObj.get("sid")));
                                    examinationnum.setInsertUser(loginUserId);
                                    examinationnum.setInsertDate(currentTime);
                                    examinationnum.setIsDelete("F");
                                    examinationnum.setExamNum(Convert.toInt(examNum));
                                    examinationnum.setSchoolNum(schoolNum);
                                    examinationnum.setGradeNum(Convert.toInt(gradeNum));
                                    examinationnum.setClassNum(classNum3);
                                    examinationnum.setTestingCentreId(testingCentreId);
                                    examinationnum.setSubjectNum(subjectNum);
                                    examinationnum.setSeatNum(Convert.toStr(oneStuObj.get("seatNum")));
                                    this.dao.save(examinationnum);
                                });
                            });
                        });
                    });
                }
            });
        });
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List<Map<String, Object>> viewExamRoomTreeData(Map<String, Object> args) {
        String examNum = Convert.toStr(args.get(Const.EXPORTREPORT_examNum));
        String stuType = Convert.toStr(args.get("stuType"));
        String classType = Convert.toStr(args.get("classType"));
        String orderType = Convert.toStr(args.get("orderType"));
        String oldExamNum = Convert.toStr(args.get("oldExamNum"));
        int everyRoomNumCount = Convert.toInt(args.get("everyRoomNum"), 30).intValue();
        String isXuanke = Convert.toStr(args.get("isXuanke"));
        String toSchool = Convert.toStr(args.get("toSchool"));
        List<Map<String, Object>> jsonArray = (List) args.get("jsonArray");
        Map<String, Object> autoRule = getAutoRuleByExam(examNum);
        int maxSchoolNumLen = getMaxSchoolNumLen();
        List<Map<String, Object>> subjectNumAndXuankezuheList = getSubjectNumAndXuankezuheList();
        List<Map<String, Object>> viewExamRoomTreeData = new ArrayList<>();
        Map<String, List<Map<String, Object>>> gra_treeDataMap = (Map) jsonArray.stream().collect(Collectors.groupingBy(m -> {
            return Convert.toStr(m.get("gradeStr"));
        }));
        gra_treeDataMap.forEach((gradeNum, treeDataOfOneGrade) -> {
            Map<String, List<Map<String, Object>>> tc_treeDataMap = (Map) treeDataOfOneGrade.stream().collect(Collectors.groupingBy(m2 -> {
                return Convert.toStr(m2.get("testingCentreStr"));
            }));
            tc_treeDataMap.forEach((testingCentreId, treeDataOfOneTestingCentre) -> {
                String testingCentreName = Convert.toStr(((Map) treeDataOfOneTestingCentre.get(0)).get("testingCentreNameStr"));
                List<Map<String, Object>> stuList = new ArrayList<>();
                if ("1".equals(classType)) {
                    String classNumArr = (String) treeDataOfOneTestingCentre.stream().map(m3 -> {
                        return Convert.toStr(m3.get("classStr"));
                    }).distinct().collect(Collectors.joining(Const.STRING_SEPERATOR));
                    Map args_stu = new HashMap();
                    args_stu.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                    args_stu.put(Const.EXPORTREPORT_classNum, classNumArr);
                    args_stu.put("stuType", stuType);
                    stuList = this.dao._queryMapList("SELECT stu.id sid,stu.gradeNum,bg.gradeName,stu.studentNum,stu.yzexaminationnum,bg.stage,stu.jie,sch.schoolNum schoolCode,cla.studentType,sc2.autoCodeNum,stu.schoolNum,stu.classNum,sch.schoolName,stu.studentName,stu.subjectCombineNum,if(sc.subjectCombineNum is null || sc.subjectCombineNum='0','wenli','xuanke') unitType,if(sc.subjectCombineNum is null || sc.subjectCombineNum='0',cla.studentType,sc.subjectCombineNum) unitNum,if(sc.subjectCombineNum is null || sc.subjectCombineNum='0',d.name,sc.subjectCombineName) unitName from student stu LEFT JOIN class cla on cla.id = stu.classNum LEFT JOIN `data` d on d.`value` = cla.studentType and d.type=25 LEFT JOIN subjectcombine sc on sc.subjectCombineNum = stu.subjectCombineNum LEFT JOIN subjectcombine sc2 on sc2.subjectCombineNum = sc.pid and sc2.isParent='1' LEFT JOIN basegrade bg on bg.gradeNum = stu.gradeNum LEFT JOIN school sch on sch.id = stu.schoolNum where stu.gradeNum={gradeNum} and stu.isDelete = 'F' and stu.nodel = '0' and stu.classNum in ({classNum[]}) and stu.type in ({stuType[]}) group by stu.id ", TypeEnum.StringObject, args_stu);
                } else if ("2".equals(classType)) {
                    String classNumArr2 = (String) treeDataOfOneTestingCentre.stream().map(m4 -> {
                        return Convert.toStr(m4.get("classStr"));
                    }).distinct().collect(Collectors.joining(Const.STRING_SEPERATOR));
                    Map args_stu2 = new HashMap();
                    args_stu2.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                    args_stu2.put(Const.EXPORTREPORT_classNum, classNumArr2);
                    args_stu2.put("stuType", stuType);
                    stuList = this.dao._queryMapList("SELECT stu.id sid,stu.gradeNum,bg.gradeName,stu.studentNum,stu.yzexaminationnum,bg.stage,stu.jie,sch.schoolNum schoolCode,cla.studentType,sc2.autoCodeNum,stu.schoolNum,stu.classNum,sch.schoolName,stu.studentName,stu.subjectCombineNum,if(sc.subjectCombineNum is null || sc.subjectCombineNum='0','wenli','xuanke') unitType,if(sc.subjectCombineNum is null || sc.subjectCombineNum='0',cla.studentType,sc.subjectCombineNum) unitNum,if(sc.subjectCombineNum is null || sc.subjectCombineNum='0',d.name,sc.subjectCombineName) unitName from student stu LEFT JOIN levelstudent ls on ls.sid=stu.id LEFT JOIN class cla on cla.id = stu.classNum LEFT JOIN `data` d on d.`value` = cla.studentType and d.type=25 LEFT JOIN subjectcombine sc on sc.subjectCombineNum = stu.subjectCombineNum LEFT JOIN subjectcombine sc2 on sc2.subjectCombineNum = sc.pid and sc2.isParent='1' LEFT JOIN basegrade bg on bg.gradeNum = stu.gradeNum LEFT JOIN school sch on sch.id = stu.schoolNum where stu.gradeNum={gradeNum} and stu.isDelete = 'F' and stu.nodel = '0' and ls.classNum in ({classNum[]}) and stu.type in ({stuType[]}) group by stu.id ", TypeEnum.StringObject, args_stu2);
                }
                LinkedHashMap<String, List<Map<String, Object>>> school_treeDataMap = new LinkedHashMap<>();
                if ("1".equals(toSchool)) {
                    school_treeDataMap = (LinkedHashMap) stuList.stream().collect(Collectors.groupingBy(m5 -> {
                        return Convert.toStr(m5.get("schoolName"));
                    }, LinkedHashMap::new, Collectors.toList()));
                } else {
                    school_treeDataMap.put("按考点", stuList);
                }
                AtomicInteger erOrderNum = new AtomicInteger(1);
                school_treeDataMap.forEach((schoolName, stuListOfOneSchoolName) -> {
                    if ("1".equals(isXuanke) && CollUtil.isNotEmpty(subjectNumAndXuankezuheList)) {
                        List<String> subjectNumList = (List) treeDataOfOneTestingCentre.stream().map(m6 -> {
                            return Convert.toStr(m6.get("subjectStr"));
                        }).distinct().collect(Collectors.toList());
                        String isFilter = "1";
                        List<String> xuankezuheList = new ArrayList<>();
                        Iterator<String> it = subjectNumList.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            String subjectNum = it.next();
                            List<String> resList = (List) subjectNumAndXuankezuheList.stream().filter(m7 -> {
                                return subjectNum.equals(Convert.toStr(m7.get(Const.EXPORTREPORT_subjectNum))) || subjectNum.equals(Convert.toStr(m7.get("psubjectNum")));
                            }).map(m8 -> {
                                return Convert.toStr(m8.get("subjectCombineNum"));
                            }).distinct().collect(Collectors.toList());
                            if (CollUtil.isEmpty(resList)) {
                                isFilter = "0";
                                break;
                            }
                            xuankezuheList.addAll(resList);
                        }
                        if ("1".equals(isFilter)) {
                            stuListOfOneSchoolName = (List) stuListOfOneSchoolName.stream().filter(m9 -> {
                                return "xuanke".equals(m9.get("unitType")) && xuankezuheList.contains(Convert.toStr(m9.get("unitNum")));
                            }).collect(Collectors.toList());
                        }
                    }
                    if (CollUtil.isNotEmpty(subjectNumAndXuankezuheList)) {
                        stuListOfOneSchoolName.stream().filter(m10 -> {
                            return "xuanke".equals(m10.get("unitType"));
                        }).forEach(stuMap -> {
                            Optional<Map<String, Object>> res = subjectNumAndXuankezuheList.stream().filter(m11 -> {
                                return Convert.toStr(stuMap.get("unitNum")).equals(Convert.toStr(m11.get("subjectCombineNum")));
                            }).findAny();
                            if (res.isPresent()) {
                                Map<String, Object> resMap = res.get();
                                stuMap.put("zaixuanSubjectName", resMap.get("zaixuanSubjectName"));
                                stuMap.put("shouxuanId", resMap.get("shouxuanId"));
                            }
                        });
                    }
                    Comparator<Map<String, Object>> c3 = ChineseCharacterUtil.sortByPinyinOfMap("unitType");
                    Comparator<Map<String, Object>> c4 = ChineseCharacterUtil.sortByPinyinOfMap("zaixuanSubjectName");
                    Comparator<Map<String, Object>> c5 = Comparator.comparing(m11 -> {
                        return Convert.toBigDecimal(m11.get("shouxuanId"), BigDecimal.valueOf(0L));
                    });
                    LinkedHashMap<String, List<Map<String, Object>>> unit_treeDataMap = (LinkedHashMap) ((List) stuListOfOneSchoolName.stream().sorted(c3.thenComparing(c4).thenComparing(c5)).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(m12 -> {
                        return Convert.toStr(m12.get("unitNum"));
                    }, LinkedHashMap::new, Collectors.toList()));
                    unit_treeDataMap.forEach((unitNum, treeDataOfOneUnit) -> {
                        ArrayList arrayList = new ArrayList();
                        Map<String, List<Map<String, Object>>> sch_stuListMap = (Map) treeDataOfOneUnit.stream().collect(Collectors.groupingBy(m13 -> {
                            return Convert.toStr(m13.get(Const.EXPORTREPORT_schoolNum));
                        }));
                        sch_stuListMap.forEach((schoolNum, stuListOfOneSchool) -> {
                            validateExamineeNum(autoRule, stuListOfOneSchool);
                        });
                        sch_stuListMap.forEach((schoolNum2, stuListOfOneSchool2) -> {
                            if ("hunpai".equals(orderType)) {
                                Collections.shuffle(stuListOfOneSchool2);
                            } else if ("shunpai".equals(orderType)) {
                                Map args_stu3 = new HashMap();
                                args_stu3.put("oldExamNum", oldExamNum);
                                args_stu3.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                                args_stu3.put(Const.EXPORTREPORT_subjectNum, "-1");
                                args_stu3.put(Const.EXPORTREPORT_schoolNum, schoolNum2);
                                Map<Object, Object> stuTotalScoreDataOfOneSchool = this.dao._queryOrderMap("select studentId,totalScore from studentlevel where examNum={oldExamNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and schoolNum={schoolNum} and statisticType='0' and source='0' and xuankezuhe='0' ", TypeEnum.StringObject, args_stu3);
                                stuListOfOneSchool2.forEach(oneStuObj -> {
                                    if (stuTotalScoreDataOfOneSchool.containsKey(oneStuObj.get(Const.EXPORTREPORT_studentId))) {
                                        oneStuObj.put("totalScore", stuTotalScoreDataOfOneSchool.get(oneStuObj.get(Const.EXPORTREPORT_studentId)));
                                    } else {
                                        oneStuObj.put("totalScore", 0);
                                    }
                                });
                                Comparator<Map<String, Object>> c1 = Comparator.comparing(m14 -> {
                                    return Convert.toBigDecimal(m14.get("totalScore"));
                                });
                                stuListOfOneSchool2 = (List) stuListOfOneSchool2.stream().sorted(c1.reversed()).collect(Collectors.toList());
                            }
                            AtomicInteger shunxu_sch = new AtomicInteger();
                            stuListOfOneSchool2.forEach(oneStuObj2 -> {
                                shunxu_sch.getAndIncrement();
                                if ("1".equals(Convert.toStr(autoRule.get("type")))) {
                                    Object[] objArr = new Object[8];
                                    objArr[0] = "1".equals(Convert.toStr(autoRule.get("code_nianji"))) ? oneStuObj2.get(Const.EXPORTREPORT_gradeNum) : "";
                                    objArr[1] = "1".equals(Convert.toStr(autoRule.get("code_ruxuenianfen"))) ? StrUtil.subSufByLength(Convert.toStr(oneStuObj2.get("stage")), 1) : "";
                                    objArr[2] = "1".equals(Convert.toStr(autoRule.get("code_ruxuenianfen"))) ? StrUtil.subSufByLength(Convert.toStr(oneStuObj2.get("jie")), 2) : "";
                                    objArr[3] = "1".equals(Convert.toStr(autoRule.get("code_xuexiao"))) ? StrUtil.fillBefore(Convert.toStr(oneStuObj2.get("schoolCode")), '0', maxSchoolNumLen) : "";
                                    objArr[4] = "1".equals(Convert.toStr(autoRule.get("code_kelei"))) ? oneStuObj2.get(Const.EXPORTREPORT_studentType) : "";
                                    objArr[5] = "1".equals(Convert.toStr(autoRule.get("code_shouxuan"))) ? Convert.toStr(oneStuObj2.get("autoCodeNum"), "0") : "";
                                    objArr[6] = "1".equals(Convert.toStr(autoRule.get("code_shunxu"))) ? StrUtil.fillBefore(Convert.toStr(Integer.valueOf(shunxu_sch.get())), '0', 4) : "";
                                    objArr[7] = "1".equals(Convert.toStr(autoRule.get("code_guding"))) ? StrUtil.subSufByLength(Convert.toStr(oneStuObj2.get("studentNum")), 4) : "";
                                    String examineeNum = StrUtil.format("{}{}{}{}{}{}{}{}", objArr);
                                    oneStuObj2.put("examineeNum", examineeNum);
                                    return;
                                }
                                oneStuObj2.put("examineeNum", oneStuObj2.get("yzexaminationnum"));
                            });
                            arrayList.addAll(stuListOfOneSchool2);
                        });
                        Map<String, Object> firstObj = (Map) treeDataOfOneUnit.get(0);
                        int totalStudentCount = treeDataOfOneUnit.size();
                        String unitId = StrUtil.format("{}_{}_{}", new Object[]{gradeNum, testingCentreId, unitNum});
                        HashMap hashMap = new HashMap();
                        hashMap.put("id", unitId);
                        hashMap.put("parentId", "-1");
                        hashMap.put(Const.EXPORTREPORT_gradeNum, firstObj.get(Const.EXPORTREPORT_gradeNum));
                        hashMap.put("gradeName", firstObj.get("gradeName"));
                        hashMap.put("testingCentreId", testingCentreId);
                        hashMap.put("testingCentreName", testingCentreName);
                        hashMap.put("schoolName", schoolName);
                        hashMap.put("unitType", firstObj.get("unitType"));
                        hashMap.put("unitNum", unitNum);
                        hashMap.put("unitName", firstObj.get("unitName"));
                        hashMap.put("totalStuCount", Integer.valueOf(totalStudentCount));
                        hashMap.put("assignedStuCount", Integer.valueOf(totalStudentCount));
                        hashMap.put("stuListOfOneUnit", arrayList);
                        viewExamRoomTreeData.add(hashMap);
                        int examroomNumber = NumberUtil.ceilDiv(totalStudentCount, everyRoomNumCount);
                        int charLengthOfInteger = getCharLengthOfInteger(examroomNumber, 2);
                        int er = 0;
                        while (er < examroomNumber) {
                            HashMap hashMap2 = new HashMap();
                            hashMap2.put("id", GUID.getGUIDStr());
                            hashMap2.put("parentId", unitId);
                            hashMap2.put(Const.EXPORTREPORT_gradeNum, firstObj.get(Const.EXPORTREPORT_gradeNum));
                            hashMap2.put("gradeName", firstObj.get("gradeName"));
                            hashMap2.put("testingCentreId", testingCentreId);
                            hashMap2.put("testingCentreName", testingCentreName);
                            hashMap2.put("schoolName", schoolName);
                            hashMap2.put("unitType", firstObj.get("unitType"));
                            hashMap2.put("unitNum", unitNum);
                            hashMap2.put("unitName", firstObj.get("unitName"));
                            hashMap2.put("examinationRoomUUID", GUID.getGUIDStr());
                            hashMap2.put("examinationRoomNum", StrUtil.fillBefore(Convert.toStr(erOrderNum), '0', charLengthOfInteger));
                            hashMap2.put("examinationRoomType", "normal");
                            hashMap2.put("lastExaminationRoom", er == examroomNumber - 1 ? "1" : "0");
                            int examroomStuCount = er == examroomNumber - 1 ? totalStudentCount - ((examroomNumber - 1) * everyRoomNumCount) : everyRoomNumCount;
                            hashMap2.put("examroomStuCount", Integer.valueOf(examroomStuCount));
                            hashMap2.put("testLocation", "");
                            viewExamRoomTreeData.add(hashMap2);
                            erOrderNum.getAndIncrement();
                            er++;
                        }
                    });
                });
            });
        });
        if (CollUtil.isNotEmpty(viewExamRoomTreeData)) {
            Comparator<Map<String, Object>> c1 = ChineseCharacterUtil.sortByPinyinOfMap("gradeName");
            Comparator<Map<String, Object>> c2 = ChineseCharacterUtil.sortByPinyinOfMap("testingCentreName");
            Comparator<Map<String, Object>> c2_1 = ChineseCharacterUtil.sortByPinyinOfMap("schoolName");
            return (List) viewExamRoomTreeData.stream().sorted(c1.thenComparing(c2).thenComparing(c2_1)).collect(Collectors.toList());
        }
        return viewExamRoomTreeData;
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public void divideExamRoomByGrade(Map<String, Object> args) {
        List<Map<String, Object>> allStuList = new ArrayList<>();
        List<Map<String, Object>> viewExamRoomTreeDataList = (List) args.get("viewExamRoomTreeDataList");
        List<Map<String, Object>> tableTreeDataList_parent = (List) viewExamRoomTreeDataList.stream().filter(m -> {
            return "-1".equals(Convert.toStr(m.get("parentId")));
        }).collect(Collectors.toList());
        Map<String, Map<String, Object>> mixErMap = new HashMap<>();
        tableTreeDataList_parent.forEach(row_parent -> {
            String parentId = Convert.toStr(row_parent.get("id"));
            List<Map<String, Object>> stuListOfOneUnit = (List) row_parent.get("stuListOfOneUnit");
            List<Map<String, Object>> tableTreeDataList_children = (List) viewExamRoomTreeDataList.stream().filter(m2 -> {
                return parentId.equals(Convert.toStr(m2.get("parentId")));
            }).collect(Collectors.toList());
            int startIndex = 0;
            int iLen = tableTreeDataList_children.size();
            for (int i = 0; i < iLen; i++) {
                Map<String, Object> row_child = tableTreeDataList_children.get(i);
                int examroomStuCount = Convert.toInt(row_child.get("examroomStuCount"), 0).intValue();
                if (examroomStuCount > 0) {
                    String examinationRoomUUID_before = Convert.toStr(row_child.get("examinationRoomUUID"));
                    String examinationRoomType = Convert.toStr(row_child.get("examinationRoomType"));
                    int seatNumStartIndex = 0;
                    if ("mix".equals(examinationRoomType)) {
                        String examinationRoomNum = Convert.toStr(row_child.get("examinationRoomNum"));
                        if (!mixErMap.containsKey(examinationRoomNum)) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("examinationRoomUUID", GUID.getGUIDStr());
                            hashMap.put("seatNumStartIndex", 0);
                            mixErMap.put(examinationRoomNum, hashMap);
                        } else {
                            seatNumStartIndex = Convert.toInt(((Map) mixErMap.get(examinationRoomNum)).get("seatNumStartIndex"), 0).intValue();
                        }
                        row_child.put("examinationRoomUUID", ((Map) mixErMap.get(examinationRoomNum)).get("examinationRoomUUID"));
                    } else if (StrUtil.isEmpty(examinationRoomUUID_before)) {
                        row_child.put("examinationRoomUUID", GUID.getGUIDStr());
                    }
                    int endIndex = startIndex + examroomStuCount;
                    List<Map<String, Object>> stuListOfOneExamroom = ListUtil.sub(stuListOfOneUnit, startIndex, endIndex);
                    int sLen = stuListOfOneExamroom.size();
                    int charLength_seatNum = getCharLengthOfInteger(sLen, 2);
                    for (int s = 0; s < sLen; s++) {
                        Map<String, Object> oneStuObj = stuListOfOneExamroom.get(s);
                        oneStuObj.put("examinationRoomUUID", row_child.get("examinationRoomUUID"));
                        oneStuObj.put("examinationRoomNum", row_child.get("examinationRoomNum"));
                        oneStuObj.put("testLocation", row_child.get("testLocation"));
                        oneStuObj.put("seatNum", StrUtil.fillBefore(Convert.toStr(Integer.valueOf(seatNumStartIndex + s + 1)), '0', charLength_seatNum));
                    }
                    if ("mix".equals(examinationRoomType)) {
                        Map<String, Object> mixMap = (Map) mixErMap.get(Convert.toStr(row_child.get("examinationRoomNum")));
                        mixMap.put("seatNumStartIndex", Integer.valueOf(seatNumStartIndex + sLen));
                    }
                    allStuList.addAll(stuListOfOneExamroom);
                    startIndex = endIndex;
                }
            }
        });
        args.put("allStuList", allStuList);
        divideExamRoomOfCommon(args);
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public String ishaveSchool(List<List<Object>> list) {
        String schoolStr = "";
        String schoolNameStr = Const.STRING_SEPERATOR;
        for (int i = 0; i < list.size(); i++) {
            String schoolName = list.get(i).get(0).toString();
            if (schoolNameStr.indexOf(Const.STRING_SEPERATOR + schoolName + Const.STRING_SEPERATOR) >= 0) {
                String schoolStr2 = "第" + (i + 2) + "行" + schoolName + "重复，请检查后重新导入";
                return schoolStr2;
            }
            schoolNameStr = schoolNameStr + schoolName + Const.STRING_SEPERATOR;
            Map args = new HashMap();
            args.put("schoolName", schoolName);
            Integer count = this.dao._queryInt("select count(1) from school where schoolName = {schoolName}", args);
            if (count.intValue() == 0) {
                schoolStr = schoolStr + "第" + (i + 2) + "行,";
            }
        }
        if (!schoolStr.equals("")) {
            schoolStr = schoolStr + "的学校名称有误，请检查后重新导入";
        }
        return schoolStr;
    }

    public void divideExamRoomOfCommon(Map<String, Object> args) {
        String examNum = Convert.toStr(args.get(Const.EXPORTREPORT_examNum));
        Convert.toStr(args.get("stuType"));
        String classType = Convert.toStr(args.get("classType"));
        Convert.toStr(args.get("orderType"));
        Convert.toStr(args.get("oldExamNum"));
        String isXuanke = Convert.toStr(args.get("isXuanke"));
        Convert.toStr(args.get("toSchool"));
        String loginUserId = Convert.toStr(args.get("loginUserId"));
        String currentTime = Convert.toStr(args.get("currentTime"));
        List<Map<String, Object>> subjectNumAndXuankezuheList = new ArrayList();
        if ("1".equals(isXuanke)) {
            subjectNumAndXuankezuheList = getSubjectNumAndXuankezuheList();
        }
        List<Map<String, Object>> jsonArray = (List) args.get("jsonArray");
        List<Map<String, Object>> allStuList = (List) args.get("allStuList");
        getAutoRuleByExam(examNum);
        Map<String, List<Map<String, Object>>> gra_treeDataMap = (Map) jsonArray.stream().collect(Collectors.groupingBy(m -> {
            return Convert.toStr(m.get("gradeStr"));
        }));
        Map<String, String> erIdMap = new HashMap<>();
        List<Map<String, Object>> finalSubjectNumAndXuankezuheList = subjectNumAndXuankezuheList;
        gra_treeDataMap.forEach((gradeNum, treeDataOfOneGrade) -> {
            Map<String, List<Map<String, Object>>> sch_treeDataMap = (Map) treeDataOfOneGrade.stream().collect(Collectors.groupingBy(m2 -> {
                return Convert.toStr(m2.get("schoolStr"));
            }));
            sch_treeDataMap.forEach((schoolNum, treeDataOfOneSchool) -> {
                Map<String, List<Map<String, Object>>> cla_treeDataMap = (Map) treeDataOfOneSchool.stream().collect(Collectors.groupingBy(m3 -> {
                    return Convert.toStr(m3.get("classStr"));
                }));
                Set<String> subSet = new HashSet<>();
                if ("1".equals(classType)) {
                    cla_treeDataMap.forEach((classNum, treeDataOfOneClass) -> {
                        List<Map<String, Object>> stuDataOfOneClass = (List) allStuList.stream().filter(m4 -> {
                            return classNum.equals(Convert.toStr(m4.get(Const.EXPORTREPORT_classNum)));
                        }).collect(Collectors.toList());
                        Map<String, List<Map<String, Object>>> er_stuDataListMap = (Map) stuDataOfOneClass.stream().collect(Collectors.groupingBy(m5 -> {
                            return Convert.toStr(m5.get("examinationRoomUUID"));
                        }));
                        treeDataOfOneClass.forEach(oneSubObj -> {
                            String subjectNum = Convert.toStr(oneSubObj.get("subjectStr"));
                            String testingCentreId = Convert.toStr(oneSubObj.get("testingCentreStr"));
                            if (subSet.add(subjectNum)) {
                                autoDeleteEn(examNum, schoolNum, gradeNum, subjectNum, null);
                            }
                            er_stuDataListMap.forEach((erKey, stuDataOfOneExamroom) -> {
                                String examinationRoomId;
                                List<Map<String, Object>> stuDataOfOneExamroom_filter = filterStuList(subjectNum, finalSubjectNumAndXuankezuheList, stuDataOfOneExamroom);
                                if (CollUtil.isEmpty(stuDataOfOneExamroom_filter)) {
                                    return;
                                }
                                Map<String, Object> oneExamroomObj = stuDataOfOneExamroom_filter.get(0);
                                String key = StrUtil.format("{}_{}", new Object[]{subjectNum, oneExamroomObj.get("examinationRoomUUID")});
                                if (!erIdMap.containsKey(key)) {
                                    Examinationroom examinationroom = new Examinationroom();
                                    examinationroom.setId(GUID.getGUIDStr());
                                    examinationroom.setExaminationRoomNum(Convert.toStr(oneExamroomObj.get("examinationRoomNum")));
                                    examinationroom.setExaminationRoomName(StrUtil.format("{}考场", new Object[]{oneExamroomObj.get("examinationRoomNum")}));
                                    examinationroom.setGradeNum(Convert.toInt(gradeNum));
                                    examinationroom.setInsertUser(loginUserId);
                                    examinationroom.setInsertDate(currentTime);
                                    examinationroom.setIsDelete("F");
                                    examinationroom.setExamNum(Convert.toInt(examNum));
                                    examinationroom.setTestingCentreId(testingCentreId);
                                    examinationroom.setTestLocation(Convert.toStr(oneExamroomObj.get("testLocation")));
                                    examinationroom.setSubjectNum(subjectNum);
                                    this.dao.save(examinationroom);
                                    erIdMap.put(key, examinationroom.getId());
                                    examinationRoomId = examinationroom.getId();
                                } else {
                                    examinationRoomId = (String) erIdMap.get(key);
                                }
                                String finalExaminationRoomId = examinationRoomId;
                                stuDataOfOneExamroom_filter.forEach(oneStuObj -> {
                                    Examinationnum examinationnum = new Examinationnum();
                                    examinationnum.setExaminationRoomNum(finalExaminationRoomId);
                                    examinationnum.setExamineeNum(Convert.toStr(oneStuObj.get("examineeNum")));
                                    examinationnum.setStudentId(Convert.toStr(oneStuObj.get("sid")));
                                    examinationnum.setInsertUser(loginUserId);
                                    examinationnum.setInsertDate(currentTime);
                                    examinationnum.setIsDelete("F");
                                    examinationnum.setExamNum(Convert.toInt(examNum));
                                    examinationnum.setSchoolNum(schoolNum);
                                    examinationnum.setGradeNum(Convert.toInt(gradeNum));
                                    examinationnum.setClassNum(classNum);
                                    examinationnum.setTestingCentreId(testingCentreId);
                                    examinationnum.setSubjectNum(subjectNum);
                                    examinationnum.setSeatNum(Convert.toStr(oneStuObj.get("seatNum")));
                                    this.dao.save(examinationnum);
                                });
                            });
                        });
                    });
                } else if ("2".equals(classType)) {
                    new HashMap();
                    cla_treeDataMap.forEach((levelClassNum, treeDataOfOneLevelClass) -> {
                        Map args_level = new HashMap();
                        args_level.put("levelClassNum", levelClassNum);
                        List<Object> levelstuList = this.dao._queryColList("select ls.sid from levelstudent ls where ls.classNum={levelClassNum}", args_level);
                        List<Map<String, Object>> stuDataOfOneLevelClass = (List) allStuList.stream().filter(m4 -> {
                            return levelstuList.contains(Convert.toLong(m4.get("sid")));
                        }).collect(Collectors.toList());
                        Map<String, List<Map<String, Object>>> er_stuDataListMap = (Map) stuDataOfOneLevelClass.stream().collect(Collectors.groupingBy(m5 -> {
                            return Convert.toStr(m5.get("examinationRoomUUID"));
                        }));
                        treeDataOfOneLevelClass.forEach(oneSubObj -> {
                            String subjectNum = Convert.toStr(oneSubObj.get("subjectStr"));
                            String testingCentreId = Convert.toStr(oneSubObj.get("testingCentreStr"));
                            if (subSet.add(subjectNum)) {
                                autoDeleteEn(examNum, schoolNum, gradeNum, subjectNum, null);
                            }
                            er_stuDataListMap.forEach((erKey, stuDataOfOneExamroom) -> {
                                String examinationRoomId;
                                List<Map<String, Object>> stuDataOfOneExamroom_filter = filterStuList(subjectNum, finalSubjectNumAndXuankezuheList, stuDataOfOneExamroom);
                                if (CollUtil.isEmpty(stuDataOfOneExamroom_filter)) {
                                    return;
                                }
                                Map<String, Object> oneExamroomObj = stuDataOfOneExamroom_filter.get(0);
                                String key = StrUtil.format("{}_{}", new Object[]{subjectNum, oneExamroomObj.get("examinationRoomUUID")});
                                if (!erIdMap.containsKey(key)) {
                                    Examinationroom examinationroom = new Examinationroom();
                                    examinationroom.setId(GUID.getGUIDStr());
                                    examinationroom.setExaminationRoomNum(Convert.toStr(oneExamroomObj.get("examinationRoomNum")));
                                    examinationroom.setExaminationRoomName(StrUtil.format("{}考场", new Object[]{oneExamroomObj.get("examinationRoomNum")}));
                                    examinationroom.setGradeNum(Convert.toInt(gradeNum));
                                    examinationroom.setInsertUser(loginUserId);
                                    examinationroom.setInsertDate(currentTime);
                                    examinationroom.setIsDelete("F");
                                    examinationroom.setExamNum(Convert.toInt(examNum));
                                    examinationroom.setTestingCentreId(testingCentreId);
                                    examinationroom.setTestLocation(Convert.toStr(oneExamroomObj.get("testLocation")));
                                    examinationroom.setSubjectNum(subjectNum);
                                    this.dao.save(examinationroom);
                                    erIdMap.put(key, examinationroom.getId());
                                    examinationRoomId = examinationroom.getId();
                                } else {
                                    examinationRoomId = (String) erIdMap.get(key);
                                }
                                String finalExaminationRoomId = examinationRoomId;
                                stuDataOfOneExamroom_filter.forEach(oneStuObj -> {
                                    Examinationnum examinationnum = new Examinationnum();
                                    examinationnum.setExaminationRoomNum(finalExaminationRoomId);
                                    examinationnum.setExamineeNum(Convert.toStr(oneStuObj.get("examineeNum")));
                                    examinationnum.setStudentId(Convert.toStr(oneStuObj.get("sid")));
                                    examinationnum.setInsertUser(loginUserId);
                                    examinationnum.setInsertDate(currentTime);
                                    examinationnum.setIsDelete("F");
                                    examinationnum.setExamNum(Convert.toInt(examNum));
                                    examinationnum.setSchoolNum(schoolNum);
                                    examinationnum.setGradeNum(Convert.toInt(gradeNum));
                                    examinationnum.setClassNum(Convert.toStr(oneStuObj.get(Const.EXPORTREPORT_classNum)));
                                    examinationnum.setTestingCentreId(testingCentreId);
                                    examinationnum.setSubjectNum(subjectNum);
                                    examinationnum.setSeatNum(Convert.toStr(oneStuObj.get("seatNum")));
                                    this.dao.save(examinationnum);
                                });
                            });
                        });
                    });
                }
            });
        });
    }

    public void validateExamineeNum(Map<String, Object> autoRule, List<Map<String, Object>> stuListOfOneSchool) {
        String str;
        String str2;
        String str3;
        if (CollUtil.isEmpty(stuListOfOneSchool)) {
            return;
        }
        Map<String, Object> firstObj = stuListOfOneSchool.get(0);
        if ("1".equals(Convert.toStr(autoRule.get("type")))) {
            if ("1".equals(Convert.toStr(autoRule.get("code_shunxu")))) {
                if (stuListOfOneSchool.size() > 9999) {
                    throwException(StrUtil.format("{}{}学生数超过9999个，使用固定顺序编考号会重复，请联系管理员处理！", new Object[]{firstObj.get("gradeName"), firstObj.get("schoolName")}));
                    return;
                }
                return;
            }
            if ("1".equals(Convert.toStr(autoRule.get("code_guding")))) {
                List<String> emptyList = new ArrayList<>();
                List<String> repeatList = new ArrayList<>();
                Set<String> studentNumSet = new HashSet<>();
                stuListOfOneSchool.forEach(oneStuObj -> {
                    String studentNum = Convert.toStr(oneStuObj.get("studentNum"), "");
                    if (StrUtil.isEmpty(studentNum)) {
                        emptyList.add(Convert.toStr(oneStuObj.get("studentName"), ""));
                        return;
                    }
                    String lastFourStr = StrUtil.subSufByLength(studentNum, 4);
                    if (!studentNumSet.add(lastFourStr)) {
                        repeatList.add(studentNum);
                    }
                });
                if (CollUtil.isNotEmpty(emptyList)) {
                    Object[] objArr = new Object[2];
                    objArr[0] = String.join("，", ListUtil.sub(emptyList, 0, 10));
                    objArr[1] = emptyList.size() > 10 ? "..." : "";
                    str2 = StrUtil.format("学生{}{}的学号为空", objArr);
                } else {
                    str2 = "";
                }
                String emptyMsg = str2;
                if (CollUtil.isNotEmpty(repeatList)) {
                    Object[] objArr2 = new Object[3];
                    objArr2[0] = StrUtil.isEmpty(emptyMsg) ? "" : "，";
                    objArr2[1] = String.join("，", ListUtil.sub(repeatList, 0, 10));
                    objArr2[2] = repeatList.size() > 10 ? "..." : "";
                    str3 = StrUtil.format("{}学号{}{}的后四位与其他同学的学号后四位重复", objArr2);
                } else {
                    str3 = "";
                }
                String repeatMsg = str3;
                if (StrUtil.isNotEmpty(emptyMsg) || StrUtil.isNotEmpty(repeatMsg)) {
                    throwException(StrUtil.format("{}{}{}{}，请检查！", new Object[]{firstObj.get("gradeName"), firstObj.get("schoolName"), emptyMsg, repeatMsg}));
                    return;
                }
                return;
            }
            return;
        }
        List<String> emptyList2 = new ArrayList<>();
        new HashSet();
        stuListOfOneSchool.forEach(oneStuObj2 -> {
            String yzexaminationnum = Convert.toStr(oneStuObj2.get("yzexaminationnum"), "");
            if (StrUtil.isEmpty(yzexaminationnum)) {
                emptyList2.add(Convert.toStr(oneStuObj2.get("studentName"), ""));
            }
        });
        if (CollUtil.isNotEmpty(emptyList2)) {
            Object[] objArr3 = new Object[2];
            objArr3[0] = String.join("，", ListUtil.sub(emptyList2, 0, 10));
            objArr3[1] = emptyList2.size() > 10 ? "..." : "";
            str = StrUtil.format("学生{}{}的预置考号为空", objArr3);
        } else {
            str = "";
        }
        String emptyMsg2 = str;
        if (StrUtil.isNotEmpty(emptyMsg2)) {
            throwException(StrUtil.format("{}{}{}，请检查！", new Object[]{firstObj.get("gradeName"), firstObj.get("schoolName"), emptyMsg2}));
        }
    }

    public Map<String, Object> getTestingCentreIdToSchoolCount(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryOrderMap("select tcs.testingCentreId,count(distinct tcs.schoolNum) from testingcentre_school tcs left join school sch on sch.id=tcs.schoolNum where tcs.examNum={examNum} and tcs.isDelete='F' and sch.isDelete='F' group by tcs.testingCentreId", TypeEnum.StringObject, args);
    }

    public List<Map<String, Object>> getSubjectNumAndXuankezuheList() {
        List<Map<String, Object>> allDataList = this.dao._queryMapList("select sub.subjectNum,sub.subjectName,sub2.subjectNum psubjectNum,sub2.subjectName psubjectName,sub2.orderNum porderNum,scd.subjectCombineNum,sc.isParent,sc.pid shouxuanId from subjectcombinedetail scd left join subjectcombine sc on sc.subjectCombineNum=scd.subjectCombineNum left join `subject` sub on sub.subjectNum=scd.subjectNum and sub.xuankaoqufen='2' left join `subject` sub2 on sub2.subjectNum=sub.pid and sub2.xuankaoqufen='1' where scd.isDelete='F' and sc.isDelete='F' and sub.subjectNum is not null and sub2.subjectNum is not null ", TypeEnum.StringObject, null);
        List<Map<String, Object>> shouxuanList = (List) allDataList.stream().filter(m -> {
            return "1".equals(m.get("isParent")) && StrUtil.isEmptyIfStr(m.get("shouxuanId"));
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(shouxuanList)) {
            Map<String, List<Map<String, Object>>> xuankeToSubjectListMap = (Map) allDataList.stream().collect(Collectors.groupingBy(m2 -> {
                return Convert.toStr(m2.get("subjectCombineNum"));
            }));
            xuankeToSubjectListMap.forEach((subjectCombineNum, subjectList) -> {
                subjectList.forEach(obj -> {
                    Optional res = shouxuanList.stream().filter(m3 -> {
                        return Convert.toStr(obj.get(Const.EXPORTREPORT_subjectNum)).equals(Convert.toStr(m3.get(Const.EXPORTREPORT_subjectNum))) || Convert.toStr(obj.get("psubjectNum")).equals(Convert.toStr(m3.get("psubjectNum")));
                    }).findAny();
                    if (res.isPresent()) {
                        obj.put("shouxuanSub", "1");
                    } else {
                        obj.put("shouxuanSub", "0");
                    }
                });
                String zaixuanSubjectName = (String) subjectList.stream().filter(m3 -> {
                    return "0".equals(m3.get("shouxuanSub"));
                }).sorted(Comparator.comparing(m4 -> {
                    return Convert.toInt(m4.get("porderNum"), 0);
                })).map(m5 -> {
                    return Convert.toStr(m5.get("psubjectName"));
                }).collect(Collectors.joining());
                subjectList.forEach(obj2 -> {
                    obj2.put("zaixuanSubjectName", zaixuanSubjectName);
                });
            });
        }
        return allDataList;
    }

    public List<Map<String, Object>> filterStuList(String subjectNum, List<Map<String, Object>> subjectNumAndXuankezuheList, List<Map<String, Object>> stuDataOfOneClass) {
        List<Map<String, Object>> stuDataOfOneClass_filter = new ArrayList<>();
        String isFilterSub = "0";
        if (CollUtil.isNotEmpty(subjectNumAndXuankezuheList)) {
            List<Map<String, Object>> xuankezuheOfOneSubjectList = (List) subjectNumAndXuankezuheList.stream().filter(m -> {
                return subjectNum.equals(Convert.toStr(m.get(Const.EXPORTREPORT_subjectNum), "")) || subjectNum.equals(Convert.toStr(m.get("psubjectNum"), ""));
            }).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(xuankezuheOfOneSubjectList)) {
                isFilterSub = "1";
                int iLen = stuDataOfOneClass.size();
                for (int i = 0; i < iLen; i++) {
                    Map<String, Object> oneStuObj = stuDataOfOneClass.get(i);
                    String xuankezuhe = Convert.toStr(oneStuObj.get("subjectCombineNum"), "");
                    if (!StrUtil.isEmpty(xuankezuhe)) {
                        Optional res = xuankezuheOfOneSubjectList.stream().filter(m2 -> {
                            return xuankezuhe.equals(Convert.toStr(m2.get("subjectCombineNum")));
                        }).findAny();
                        if (res.isPresent()) {
                            stuDataOfOneClass_filter.add(oneStuObj);
                        }
                    }
                }
            }
        }
        if ("0".equals(isFilterSub)) {
            return stuDataOfOneClass;
        }
        return stuDataOfOneClass_filter;
    }

    public int getCharLengthOfInteger(int num, int defaultMinLength) {
        int charLength = Convert.toStr(Integer.valueOf(num)).length();
        return NumberUtil.max(new int[]{charLength, defaultMinLength});
    }

    @Override // com.dmj.service.examManagement.ExamManageService
    public List getAllSchool(String exam, String user) {
        String sql;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("user", user);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={user} and type=1 limit 1", args);
        if (exam == null || "".equals(exam)) {
            if (user.equals("-1") || user.equals("-2") || null != map) {
                sql = "select id schoolNum,'1' isCheck,schoolName from school where isDelete='F' ";
            } else {
                sql = "select DISTINCT sch.id schoolNum,'1' isCheck,sch.schoolName schoolName from ( select schoolNum from schoolscanpermission where userNum={user} )s  left join school sch on s.schoolNum=sch.id  where sch.id is not null order by isCheck desc,sch.id ";
            }
        } else if (user.equals("-1") || user.equals("-2") || null != map) {
            sql = "select s.id schoolNum,IF(es.schoolNum is null,0,1) isCheck,schoolName from school s left join examschool es on es.examNum={exam} and s.id=es.schoolNum where s.isDelete='F' order by isCheck desc,s.id ";
        } else {
            sql = "select DISTINCT sch.id schoolNum,IF(es.schoolNum is null,0,1) isCheck,sch.schoolName schoolName from ( select schoolNum from schoolscanpermission where userNum={user} )s  left join school sch on s.schoolNum=sch.id  left join examschool es on es.examNum={exam} and s.schoolNum=es.schoolNum where sch.id is not null order by isCheck desc,sch.id ";
        }
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }
}
