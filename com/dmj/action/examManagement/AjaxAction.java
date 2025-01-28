package com.dmj.action.examManagement;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dmj.action.base.BaseAction;
import com.dmj.action.teachingInformation.LineChar;
import com.dmj.auth.bean.License;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Averagescore;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.Class;
import com.dmj.domain.Define;
import com.dmj.domain.Exam;
import com.dmj.domain.ExamineeStuRecord;
import com.dmj.domain.Examlog;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Grade;
import com.dmj.domain.MarkError;
import com.dmj.domain.Remark;
import com.dmj.domain.School;
import com.dmj.domain.Score;
import com.dmj.domain.Student;
import com.dmj.domain.Studentlevel;
import com.dmj.domain.Subject;
import com.dmj.domain.Task;
import com.dmj.domain.User;
import com.dmj.domain.Userrole;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.domain.vo.QuestionGroup_question;
import com.dmj.service.analysisManagement.AnalysisService;
import com.dmj.service.awardPoint.AwardPointService;
import com.dmj.service.examManagement.AjaxQueryService;
import com.dmj.service.examManagement.ExamService;
import com.dmj.service.examManagement.ExportService;
import com.dmj.service.examManagement.NoMarkCorrectService;
import com.dmj.service.examManagement.QuestionNumListService;
import com.dmj.service.questionGroup.QuestionGroupService;
import com.dmj.service.reportManagement.ReportExportService;
import com.dmj.service.reportManagement.ReportService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.service.teachingInformation.StudentService;
import com.dmj.service.userManagement.UserService;
import com.dmj.serviceimpl.analysisManagement.AnalysisServiceImpl;
import com.dmj.serviceimpl.awardPoint.AwardPointServiceImpl;
import com.dmj.serviceimpl.examManagement.AjaxQueryServiceImpl;
import com.dmj.serviceimpl.examManagement.ExamServiceImpl;
import com.dmj.serviceimpl.examManagement.ExportServiceImpl;
import com.dmj.serviceimpl.examManagement.NoMarkCorrectServiceImpl;
import com.dmj.serviceimpl.examManagement.QuestionNumListServiceImpl;
import com.dmj.serviceimpl.questionGroup.QuestionGroupImpl;
import com.dmj.serviceimpl.reportManagement.ReportExportServiceimpl;
import com.dmj.serviceimpl.reportManagement.ReportServiceImpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.serviceimpl.teachingInformation.StudentServiceimpl;
import com.dmj.serviceimpl.userManagement.UserServiceImpl;
import com.dmj.util.ChineseCharacterUtil;
import com.dmj.util.CommonUtil;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.SfHelper;
import com.dmj.util.config.Configuration;
import com.dmj.util.jfreechar.util.JFreeChartUtils;
import com.dmj.util.msg.RspMsg;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.zht.db.ServiceFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.ScriptStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;

/* loaded from: AjaxAction.class */
public class AjaxAction extends BaseAction {
    public static final String isHistory_true = "T";
    public static final String isHistory_False = "F";
    private String school;
    private String grade;
    private String subject;
    private String cla;
    private String exam;
    private String qtype;
    private String stat;
    private String showZSubject;
    private String showHekeSubject;
    private String showSubjectL;
    private String exampaperNum;
    private String questionNum;
    private String questionNum2;
    private int fullScore;
    private String subCompose;
    private String islevel;
    private String teachUnit;
    private String contrastObject;
    private String fullScore2;
    private List teacs;
    private String type;
    private String examinationRoom;
    private String startTime;
    private String endTime;
    private String schoolNum;
    private String gradeNum;
    private String updateUser;
    private String subjectNum;
    private String examNum;
    private String rpt_name;
    private String studentId;
    private String classNum;
    private String examinationRoomNum;
    private String questionScore;
    private int page;
    private String userNum;
    private String description;
    private String userpositionNum;
    private int snumber;
    private String subjectType;
    private String statisticType;
    private String studentType;
    private String taskId;
    private String groupnum;
    private String stuSource;
    private String scoreId;
    private String source;
    private String rwCount;
    private String correctscore;
    private String examPaperNum;
    private int a;
    private String questionNumCp;
    private String updateUserCp;
    private int snumberCp;
    private String rptTitle;
    private String isMoreSchool;
    private String isSaveWrite;
    private String testCenter;
    private String fufen;
    private String examineestatu;
    private String mark;
    public static AnalysisService analy = (AnalysisService) ServiceFactory.getObject(new AnalysisServiceImpl());
    public static SystemService system = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    public static ExamService esc = (ExamService) ServiceFactory.getObject(new ExamServiceImpl());
    public static StudentService o = (StudentService) ServiceFactory.getObject(new StudentServiceimpl());
    public static ReportService report = (ReportService) ServiceFactory.getObject(new ReportServiceImpl());
    private static final SfHelper sfHelper = new SfHelper();
    private String isHistory = "F";
    private List<Task> sc = new ArrayList();
    private int count = 0;
    private int pageSize = 15;
    private int pagestart = 0;
    private int currPage = 1;
    String level = Configuration.getInstance().getLevelclass();
    public LineChar lc = new LineChar();
    private ExportService exportService = (ExportService) ServiceFactory.getObject(new ExportServiceImpl());
    private QuestionGroupService questionGroupService = (QuestionGroupService) ServiceFactory.getObject(new QuestionGroupImpl());
    private ReportExportService reportExportService = (ReportExportService) ServiceFactory.getObject(new ReportExportServiceimpl());
    private AwardPointService awardPointService = (AwardPointService) ServiceFactory.getObject(new AwardPointServiceImpl());
    private AjaxQueryService ajaxQueryService = (AjaxQueryService) ServiceFactory.getObject(new AjaxQueryServiceImpl());
    private QuestionNumListService q = (QuestionNumListService) ServiceFactory.getObject(new QuestionNumListServiceImpl());
    DecimalFormat df = new DecimalFormat("#####0.00");
    private NoMarkCorrectService m = (NoMarkCorrectService) ServiceFactory.getObject(new NoMarkCorrectServiceImpl());
    private UserService userService = (UserService) ServiceFactory.getObject(new UserServiceImpl());
    private Logger log = Logger.getLogger(getClass());

    public String getSubCompose() {
        return this.subCompose;
    }

    public void setSubCompose(String subCompose) {
        this.subCompose = subCompose;
    }

    public String getIslevel() {
        return this.islevel;
    }

    public void setIslevel(String islevel) {
        this.islevel = islevel;
    }

    public String getTeachUnit() {
        return this.teachUnit;
    }

    public void setTeachUnit(String teachUnit) {
        this.teachUnit = teachUnit;
    }

    public String getContrastObject() {
        return this.contrastObject;
    }

    public void setContrastObject(String contrastObject) {
        this.contrastObject = contrastObject;
    }

    public String getFullScore2() {
        return this.fullScore2;
    }

    public void setFullScore2(String fullScore2) {
        this.fullScore2 = fullScore2;
    }

    public void getCorrctExamList() throws IOException {
        String systemType = this.request.getParameter(License.SYSTYPE);
        String str = "";
        String userId = String.valueOf(((User) this.session.get(Const.LOGIN_USER)).getId());
        String oneOrMore = this.request.getParameter("oneOrMore");
        List<AjaxData> list = esc.getCorrectExamList(this.stat, this.type, systemType, userId, oneOrMore);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctExamList_zy() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        String userId = String.valueOf(((User) this.session.get(Const.LOGIN_USER)).getId());
        String oneOrMore = this.request.getParameter("oneOrMore");
        List<AjaxData> list = esc.getCorrectExamList_zy(this.stat, this.type, systemType, userId, oneOrMore);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctExamList_zyyh() throws IOException {
        String systemType = this.request.getParameter(License.SYSTYPE);
        String str = "";
        String userId = String.valueOf(((User) this.session.get(Const.LOGIN_USER)).getId());
        String oneOrMore = this.request.getParameter("oneOrMore");
        List<AjaxData> list = esc.getCorrectExamList_zyyh(this.stat, this.type, systemType, userId, oneOrMore);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctSubjectList() throws IOException {
        String systemType = this.request.getParameter(License.SYSTYPE);
        String str = "";
        List<AjaxData> list = esc.getCorrectSubjectList(this.exam, this.stat, this.type, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctSubjectList_zy() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        List<AjaxData> list = esc.getCorrectSubjectList_zy(this.exam, this.stat, this.type, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctSubjectList_zyyh() throws IOException {
        String systemType = this.request.getParameter(License.SYSTYPE);
        String str = "";
        List<AjaxData> list = esc.getCorrectSubjectList(this.exam, this.stat, this.type, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctSchoolList() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        List<AjaxData> list = esc.getCorrectSchoolList(this.exam, this.subject, this.stat, this.type, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctTCList() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        List<AjaxData> list = esc.getCorrctTCList(this.exam, this.subject, this.stat, this.type, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctAuthTCList() throws IOException {
        List<AjaxData> list;
        String userId = new CommonUtil().getLoginUserNum(this.request);
        String str = "";
        if (esc.isManager(userId)) {
            list = esc.getCorrctTCList(this.exam, this.subject, this.stat, this.type, null);
        } else {
            list = esc.getCorrctAuthTCList(this.exam, this.subject, this.stat, this.type, userId);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctAuthTCList2() throws IOException {
        List<AjaxData> list;
        String userId = new CommonUtil().getLoginUserNum(this.request);
        String exam = this.request.getParameter(Const.EXPORTREPORT_examNum);
        String subject = this.request.getParameter(Const.EXPORTREPORT_subjectNum);
        String grade = this.request.getParameter(Const.EXPORTREPORT_gradeNum);
        String str = "";
        if (esc.isManager(userId)) {
            list = esc.getCorrctTCList2(exam, subject, grade, null, this.type, null);
        } else {
            list = esc.getCorrctAuthTCList2(exam, subject, grade, null, null, userId);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctSchoolList_zy() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        List<AjaxData> list = esc.getCorrectSchoolList_zy(this.exam, this.subject, this.stat, this.type, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctSchoolList_EP() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String examplace = this.request.getParameter("examplace");
        String str = "";
        List<AjaxData> list = esc.getCorrctSchoolList_EP(this.exam, this.subject, this.stat, this.type, systemType, examplace, this.grade);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctAuthSchoolList_EP() throws IOException {
        List<AjaxData> list;
        String userId = new CommonUtil().getLoginUserNum(this.request);
        String examplace = this.request.getParameter("examplace");
        String str = "";
        if (esc.isManager(userId) || !"-1".equals(examplace)) {
            list = esc.getCorrctSchoolList_EP(this.exam, this.subject, this.stat, this.type, null, examplace, this.grade);
        } else {
            list = esc.getCorrctAuthSchoolList_EP(this.exam, this.subject, this.stat, this.type, userId, examplace, this.grade);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctGradeList() throws IOException {
        String systemType = this.request.getParameter(License.SYSTYPE);
        String str = "";
        List<AjaxData> list = esc.getCorrectGradeList(this.exam, this.subject, this.school, this.stat, this.type, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctGradeList_zy() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        List<AjaxData> list = esc.getCorrectGradeList_zy(this.exam, this.subject, this.school, this.stat, this.type, systemType, this.testCenter);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getExamPlace() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        List<AjaxData> list = esc.getExamPlace(this.exam, this.subject, this.grade, this.stat, this.type, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAuthExamPlace() throws IOException {
        List<AjaxData> list;
        String userId = new CommonUtil().getLoginUserNum(this.request);
        String str = "";
        if (esc.isManager(userId)) {
            list = esc.getExamPlace(this.exam, this.subject, this.grade, this.stat, this.type, null);
        } else {
            list = esc.getAuthExamPlace(this.exam, this.subject, this.grade, this.stat, this.type, userId);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctSubjectListStudent() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        List<AjaxData> list = esc.getCorrectSubjectList(this.exam, this.subject, this.school, this.stat, this.type, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctExaminationRoom() throws IOException {
        List<AjaxData> list;
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        Exam ep = (Exam) esc.getOneByNum(Const.EXPORTREPORT_examNum, this.exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1") && this.correctscore.equals("correctscore")) {
            list = new ArrayList();
            AjaxData data = new AjaxData();
            data.setName("全部");
            data.setNum("-1");
            list.add(data);
        } else {
            list = esc.getCorrectExaminationRoomList(this.exam, this.school, this.subject, this.grade, this.stat, this.type, systemType);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctExaminationRoom_zy() throws IOException {
        List<AjaxData> list;
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        Exam ep = (Exam) esc.getOneByNum(Const.EXPORTREPORT_examNum, this.exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1") && this.correctscore.equals("correctscore")) {
            list = new ArrayList();
            AjaxData data = new AjaxData();
            data.setName("全部");
            data.setNum("-1");
            list.add(data);
        } else {
            list = esc.getCorrectExaminationRoomList_zy(this.exam, null, this.subject, this.grade, this.stat, this.type, systemType, this.testCenter);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctExaminationRoom_EP() throws IOException {
        List<AjaxData> list;
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        String examplace = this.request.getParameter("examplace");
        Exam ep = (Exam) esc.getOneByNum(Const.EXPORTREPORT_examNum, this.exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1") && this.correctscore.equals("correctscore")) {
            list = new ArrayList();
            AjaxData data = new AjaxData();
            data.setName("全部");
            data.setNum("-1");
            list.add(data);
        } else {
            list = esc.getCorrectExaminationRoomList_EP(this.exam, this.school, this.subject, this.grade, this.stat, this.type, systemType, examplace);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctAuthExaminationRoom_EP() throws IOException {
        List<AjaxData> list;
        String userId = new CommonUtil().getLoginUserNum(this.request);
        String str = "";
        String examplace = this.request.getParameter("examplace");
        Exam ep = (Exam) esc.getOneByNum(Const.EXPORTREPORT_examNum, this.exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1") && this.correctscore.equals("correctscore")) {
            list = new ArrayList();
            AjaxData data = new AjaxData();
            data.setName("全部");
            data.setNum("-1");
            list.add(data);
        } else if (esc.isManager(userId) || !"-1".equals(examplace)) {
            list = esc.getCorrectExaminationRoomList_EP(this.exam, this.school, this.subject, this.grade, this.stat, this.type, null, examplace);
        } else {
            list = esc.getCorrectAuthExaminationRoomList_EP(this.exam, this.school, this.subject, this.grade, this.stat, this.type, userId, examplace);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getCorrctClassList() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        List<AjaxData> list = esc.getCorrectClassList(this.exam, this.school, this.subject, this.grade, this.stat, this.type, systemType, null);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getClassList() throws IOException {
        String systemType = CommonUtil.getSystemType(this.request);
        String subjectType = this.request.getParameter("subjectType");
        String str = "";
        List<AjaxData> list = esc.getClassList(this.exam, this.school, this.subject, this.grade, this.stat, this.type, systemType, subjectType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getExportStudentClassList() throws IOException {
        CommonUtil.getSystemType(this.request);
        Map jie_map = (Map) this.session.get(Const.CURRENT_JIE);
        String jie = String.valueOf(jie_map.get(this.grade));
        String subjectType = this.request.getParameter("subjectType");
        String str = "";
        new ArrayList();
        new ArrayList();
        new ArrayList();
        new ArrayList();
        o.studentSchoolList(this.school, this.level);
        o.getExamListStudent(this.exam, jie);
        String subject = this.request.getParameter("subject");
        List<AjaxData> list = o.getStudentClassList(this.exam, this.school, this.grade, this.cla, subject, this.type, subjectType, this.level);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAppendStudent() throws IOException {
        String str = "";
        String exampaperNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
        String mark = this.request.getParameter(Const.SYSTEM_TYPE);
        if (null != exampaperNum && !exampaperNum.equals("")) {
            List<AjaxData> list = esc.getCorrectExaminationRoomStudent(this.exam, this.school, this.grade, this.examinationRoom, exampaperNum, mark, this.subject);
            if (null != list) {
                str = JSONArray.fromObject(list).toString();
            }
            this.out.write(str);
        }
    }

    public void getCorrectStudent() throws IOException {
        String str = "";
        String exampaperNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
        if (null != exampaperNum && !exampaperNum.equals("")) {
            List<AjaxData> list = esc.getCorrectClassStudent(this.exam, this.school, this.grade, this.cla, exampaperNum);
            if (null != list) {
                str = JSONArray.fromObject(list).toString();
            }
            this.out.write(str);
        }
    }

    public void getSQuestionNum() throws IOException {
        String str = "";
        List<Define> list = esc.getSQuestionNum(this.exam, this.grade, this.subject);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getSTeacherNum() throws IOException {
        String str = "";
        List<Task> list = esc.getSTeacherNum(this.exam, this.grade, this.subject, this.questionNum);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getSTeacherNum2() throws IOException {
        String str = "";
        String groupType = this.request.getParameter("groupType");
        List<Task> list = esc.getSTeacherNum2(this.exam, this.grade, this.subject, this.questionNum, groupType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getSpotCheck() throws IOException {
        if (this.snumber != -1) {
            this.pageSize = this.snumber;
        } else if (this.pageSize != -1) {
            this.pageSize = 15;
        }
        String fenshuduan1 = this.request.getParameter("fenshuduan1");
        String fenshuduan2 = this.request.getParameter("fenshuduan2");
        List li = new ArrayList();
        new ArrayList();
        new ArrayList();
        List totalCountList = esc.getSpotCheck3(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)), this.questionNum, this.updateUser, Integer.valueOf(this.snumber), Integer.valueOf(this.pagestart), -1, null, fenshuduan1, fenshuduan2, this.examineestatu);
        List<Task> list = esc.getSpotCheck3(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)), this.questionNum, this.updateUser, Integer.valueOf(this.snumber), Integer.valueOf((this.currPage - 1) * this.pageSize), Integer.valueOf(this.pageSize), null, fenshuduan1, fenshuduan2, this.examineestatu);
        if (null != totalCountList && totalCountList.size() > 0) {
            this.count = totalCountList.size();
        } else {
            this.count = 0;
        }
        li.add(Integer.valueOf(this.count));
        li.add(Integer.valueOf(this.currPage));
        li.add(Integer.valueOf(this.pageSize));
        if (this.count == 0) {
            li.add(0);
        } else if (this.count > 0) {
            li.add(Integer.valueOf(this.currPage * this.pageSize));
        }
        for (int i = 0; i < list.size(); i++) {
            ArrayList arrayList = new ArrayList();
            Task t = list.get(i);
            MarkError m = new MarkError();
            List<Task> t2 = esc.getSpotCheck2(t.getExampaperNum(), t.getQuestionNum(), 0, t.getScoreId());
            arrayList.add("T" + t.getRealQuestionNum());
            List<Define> fulllist = esc.getSpotCheckFullScore(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)), t.getQuestionNum());
            for (int j = 0; j < fulllist.size(); j++) {
                Define d = fulllist.get(j);
                arrayList.add(d.getFullScore());
            }
            for (int j2 = 0; j2 < t2.size(); j2++) {
                Task tl = t2.get(j2);
                arrayList.add(Double.valueOf(tl.getQuestionScore()));
            }
            if (t2.size() == 0) {
                arrayList.add("- -");
                arrayList.add("- -");
                arrayList.add("- -");
            }
            if (t2.size() == 1) {
                arrayList.add("- -");
                arrayList.add("- -");
            }
            if (t2.size() == 2) {
                arrayList.add("- -");
            }
            List<Remark> Remarklist = esc.getSpotCheckRemark(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)), t.getScoreId(), t.getQuestionNum());
            if (null != Remarklist && Remarklist.size() > 0) {
                for (int j3 = 0; j3 < Remarklist.size(); j3++) {
                    Remark rm = Remarklist.get(j3);
                    m.setQuestionScore(Double.valueOf(Double.parseDouble(rm.getQuestionScore())));
                    if (rm.getStatus().equals("T")) {
                        arrayList.add(rm.getQuestionScore());
                    } else {
                        arrayList.add("等待裁决");
                    }
                }
            } else {
                arrayList.add("--");
            }
            List<Score> Scorelist = esc.getSpotCheckScore(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)), t.getScoreId(), t.getQuestionNum());
            if (null != Scorelist && Scorelist.size() > 0) {
                for (int j4 = 0; j4 < Scorelist.size(); j4++) {
                    Score sc = Scorelist.get(j4);
                    m.setQuestionScore(sc.getQuestionScore());
                    arrayList.add(sc.getQuestionScore());
                }
            } else {
                arrayList.add("--");
            }
            List examineelist = esc.getExamineeRecord(0, 0, 0, t.getScoreId(), "0");
            if (examineelist != null && examineelist.size() > 0) {
                if ("T".equals(examineelist.get(0).getStatus())) {
                    arrayList.add("已审核");
                }
                arrayList.add(examineelist.get(0).getExt1());
            } else {
                arrayList.add("");
                arrayList.add("");
            }
            arrayList.add(t);
            li.add(arrayList);
        }
        this.out.write(JSONArray.fromObject(li).toString());
    }

    public void getSpotCheckOne() {
        String fenshuduan1 = this.request.getParameter("fenshuduan1");
        String fenshuduan2 = this.request.getParameter("fenshuduan2");
        String str = "[]";
        List<Task> list = esc.getSpotCheck3(0, 0, 0, this.questionNumCp, this.updateUserCp, Integer.valueOf(this.snumberCp), Integer.valueOf(this.a), 1, this.examPaperNum, fenshuduan1, fenshuduan2, this.examineestatu);
        if (null != list && list.size() > 0) {
            this.questionNum = list.get(0).getQuestionNum();
            this.examPaperNum = list.get(0).getExampaperNum().toString();
            this.scoreId = list.get(0).getScoreId();
            list.get(0).setExt1(String.valueOf(this.a));
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getstuQuesDetail() {
        List<Student> listu;
        String examPaperNum = this.request.getParameter("examPaperNum");
        String questionNum = this.request.getParameter("questionNum");
        this.request.getParameter(Const.EXPORTREPORT_studentId);
        String scoreId = this.request.getParameter("scoreId");
        String str = "";
        ServletContext context = ServletActionContext.getServletContext();
        String result = (String) context.getAttribute(Const.showStudentInfo);
        String stuInfo = "";
        if (null != result && result.equals("1") && null != (listu = esc.getSpotCheck_stuInfo(scoreId, examPaperNum, questionNum)) && listu.size() > 0) {
            stuInfo = "考生：" + listu.get(0).getStudentName() + "--学号" + listu.get(0).getStudentNum() + "</br>";
        }
        List<Define> fulllist = esc.getSpotCheckFullScore(0, 0, 0, questionNum);
        for (int j = 0; j < fulllist.size(); j++) {
            Define d = fulllist.get(j);
            str = ((str + "题号：" + d.getQuestionNum() + "</br>") + "满分：<span id='FullScore'>" + d.getFullScore() + "分</span></br>") + "<input type='hidden' id='FullScore2'value=" + d.getFullScore() + ">#";
        }
        List<Task> t2 = esc.getSpotCheck2_detail(Integer.valueOf(examPaperNum), questionNum, 0, scoreId);
        for (int j2 = 0; j2 < t2.size(); j2++) {
            Task tl = t2.get(j2);
            str = str + "阅卷员：" + tl.getExt2() + "--" + tl.getExt1() + "[" + tl.getQuestionScore() + "分" + (tl.getIsException().equals("Y") ? "(稍后再判)" : "") + "]<span style='font-size:10px;'>" + tl.getUserNum() + "</span></br>";
        }
        List<Score> Scorelist = esc.getSpotCheckScore(0, 0, 0, scoreId, questionNum);
        if (null != Scorelist && Scorelist.size() > 0) {
            for (int j3 = 0; j3 < Scorelist.size(); j3++) {
                Score sc = Scorelist.get(j3);
                str = str + "得分：" + sc.getQuestionScore() + "分</br>";
            }
        } else {
            str = str + "得分：0分</br>";
        }
        List<ExamineeStuRecord> examineelist = esc.getExamineeRecord(0, 0, 0, scoreId, "0");
        if (null != examineelist && examineelist.size() > 0) {
            str = (str + "审核人：" + examineelist.get(0).getExt1()) + "<input type='hidden' id='examineeStatus' value='" + examineelist.get(0).getStatus() + "'</br>";
        }
        this.out.write(str + stuInfo);
    }

    public void addExamineeRecord() throws IOException {
        String str = "0";
        String userId = new CommonUtil().getLoginUserNum(this.request);
        ExamineeStuRecord examineeStuRecord = new ExamineeStuRecord();
        examineeStuRecord.setScoreId(this.scoreId);
        examineeStuRecord.setUserId(userId);
        examineeStuRecord.setInsertDate(DateUtil.getCurrentTime());
        examineeStuRecord.setStatus("T");
        int a = esc.addExamineeRecord(examineeStuRecord).intValue();
        if (a > 0) {
            str = "1";
        }
        this.out.write(str);
    }

    public void addSpotCheck() throws IOException {
        String a = "1";
        int b = esc.yanz1(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.questionNum2, this.scoreId, 2, this.userNum);
        if (b > 0) {
            a = "0";
        }
        this.out.write(a);
    }

    public void addRemark() throws IOException {
        this.description = URLDecoder.decode(this.description, "UTF-8");
        new CommonUtil().getLoginUserNum(this.request);
        String str = "重判成功";
        Remark rm = new Remark();
        Date dt = new Date();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Task> li2 = esc.getQuesScoreTask(this.exampaperNum.toString(), this.questionNum2.toString(), this.userNum.toString(), this.scoreId.toString());
        if (null != li2 && li2.size() > 0) {
            this.questionScore = String.valueOf(li2.get(0).getQuestionScore());
        } else {
            this.questionScore = "0";
        }
        MarkError m = new MarkError();
        m.setScoreId(this.scoreId);
        m.setExampaperNum(Integer.valueOf(Integer.parseInt(this.exampaperNum)));
        m.setQuestionNum(this.questionNum2);
        m.setQuestionScore(Double.valueOf(Double.parseDouble(this.questionScore)));
        m.setType("2");
        m.setUserNum(this.userNum);
        m.setInsertUser(this.userNum);
        m.setInsertDate(s.format(dt));
        try {
            esc.saveRemarkAndMarkError(rm, m, "", "");
            esc.deleteCheckedRecord(this.scoreId);
            String groupNum = esc.getGroupNumByQueNum(this.questionNum2);
            this.awardPointService.resettingWorkrecord(this.exampaperNum, groupNum);
        } catch (Exception e) {
            e.printStackTrace();
            str = "此题已加入重判";
        }
        this.out.write(str);
    }

    public void addRemarkXxt() {
        String str = "重判成功";
        try {
            this.description = URLDecoder.decode(this.description, "UTF-8");
            Remark rm = new Remark();
            Date dt = new Date();
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<MarkError> queList = esc.getQueList(this.exampaperNum, this.questionNum2, this.scoreId);
            for (int i = 0; i < queList.size(); i++) {
                List<Task> li2 = esc.getQuesScoreTask(this.exampaperNum, queList.get(i).getQuestionNum(), this.userNum, queList.get(i).getScoreId());
                if (null != li2 && li2.size() > 0) {
                    this.questionScore = String.valueOf(li2.get(0).getQuestionScore());
                } else {
                    this.questionScore = "0";
                }
                MarkError m = new MarkError();
                m.setScoreId(queList.get(i).getScoreId());
                m.setExampaperNum(Integer.valueOf(Integer.parseInt(this.exampaperNum)));
                m.setQuestionNum(queList.get(i).getQuestionNum());
                m.setQuestionScore(Double.valueOf(Double.parseDouble(this.questionScore)));
                m.setType("2");
                m.setUserNum(this.userNum);
                m.setInsertUser(this.userNum);
                m.setInsertDate(s.format(dt));
                esc.saveRemarkAndMarkError(rm, m, "", "");
                esc.deleteCheckedRecord(queList.get(i).getScoreId());
            }
            String groupNum = esc.getGroupNumByQueNum(this.questionNum2);
            this.awardPointService.resettingWorkrecord(this.exampaperNum, groupNum);
        } catch (Exception e) {
            e.printStackTrace();
            str = "此题已加入重判";
        }
        this.out.write(str);
    }

    public void getSpotCheckChild() throws IOException {
        String str = "<table align='center'><tr><td>请选择判卷人：</td><td align='left'><select id='usernum'>";
        List<User> list = esc.getSpotCheckChild(this.exampaperNum, this.questionNum2, this.scoreId);
        if (null != list) {
            for (int i = 0; i < list.size(); i++) {
                User user = list.get(i);
                if (user.getUsertype().equals("1")) {
                    user.setUsername("组长");
                }
                str = str + " <option value='" + user.getId() + "'>" + user.getUsername() + "</option>";
            }
        }
        this.out.write(str + "</select></td></tr><tr><td>请输入理由：</td><td><textarea cols='30' rows='5' id='description'></textarea></td></tr></table>");
    }

    public void DirectUpdateScore() throws IOException {
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        String time = DateUtil.getCurrentTime();
        String inScore = this.request.getParameter("inScore");
        this.awardPointService.updates(this.scoreId, inScore, loginUserId, time);
        List remarksize = this.awardPointService.remarksize(this.scoreId);
        if (remarksize.size() > 0) {
            for (int i = 0; i < remarksize.size(); i++) {
                AwardPoint ik = (AwardPoint) remarksize.get(i);
                int remark_id = Integer.parseInt(ik.getId());
                this.awardPointService.deleteremark(remark_id);
            }
        }
        this.awardPointService.reMark2(this.scoreId, inScore, loginUserId, this.questionNum2, this.exampaperNum);
    }

    public void getQuestionGroupScheDule() throws IOException {
        String wanchenglv;
        new DecimalFormat("0.00");
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        String str2 = "<table style='width: 650px;margin-top: 1%;margin-left: 5%;'class='list_table' id='commontable' >  <tr><td bgcolor='#D8D8D8'>题组</td><td bgcolor='#D8D8D8'>单/双评 </td><td bgcolor='#D8D8D8'>总数/题</td><td bgcolor='#D8D8D8'>完成数/题</td><td bgcolor='#D8D8D8'>未完成数</td><td bgcolor='#D8D8D8'>完成率</td><td bgcolor='#D8D8D8'>重判数</td><td bgcolor='#D8D8D8'>已裁决数/总裁决数</td><td bgcolor='#D8D8D8'>三评数</td><td bgcolor='#D8D8D8'>操作</td></tr>";
        List<QuestionGroup_question> SD = esc.getQuestionGroupSD(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)));
        int chongPanAll = 0;
        int caiJueAll = 0;
        int totalAll = 0;
        int studentsizeAll = 0;
        String exampaperNum = "";
        String groupNums = "";
        if (null != SD) {
            for (int i = 0; i < SD.size(); i++) {
                String groupName = "";
                QuestionGroup_question SDqgroup = SD.get(i);
                if (!"s".equals(SDqgroup.getExt10())) {
                    groupName = "(选)";
                }
                if ("2".equals(SDqgroup.getGroupType())) {
                    groupName = groupName + "(合)";
                }
                if (i == 0) {
                    exampaperNum = SDqgroup.getExampaperNum().toString();
                }
                if (i == SD.size() - 1) {
                    groupNums = groupNums + SDqgroup.getGroupNum();
                } else {
                    groupNums = groupNums + SDqgroup.getGroupNum() + "_";
                }
                String two = "单评";
                if (SDqgroup.getMakType().toString().equals("1")) {
                    two = "双评";
                }
                int all = (int) Float.parseFloat(SDqgroup.getExt3());
                int wan = (int) Float.parseFloat(SDqgroup.getExt4());
                String colorstyle = "#33CCFF";
                if (all - wan < 0) {
                    wan = all;
                }
                if (all - wan > 0) {
                    colorstyle = "#FF9933";
                }
                String three = "<td>" + SDqgroup.getExt9() + "</td>";
                String groupNum2 = SDqgroup.getGroupNum();
                if (!"s".equals(SDqgroup.getExt10())) {
                    groupNum2 = SDqgroup.getExt10();
                }
                if (((int) Float.parseFloat(SDqgroup.getExt3())) == 0) {
                    wanchenglv = "--";
                } else {
                    wanchenglv = ((int) Math.floor((100.0f * wan) / all)) + "%";
                }
                str2 = str2 + "<tr><td><a  href=Teacher_work.jsp?exp=" + SDqgroup.getExampaperNum() + "&grp=" + SDqgroup.getExt11() + "&exa=" + this.exam + "&gra=" + this.grade + "&sub=" + this.subject + ">" + SDqgroup.getGroupName() + groupName + "</a></td><td>" + two + "</td><td>" + all + "</td><td>" + wan + "</td><td><a style= 'color:" + colorstyle + "' href='javascript:void(0)' onclick=javascript:qscoreta('" + SDqgroup.getExampaperNum() + "','" + groupNum2 + "','" + SDqgroup.getGroupNum() + "','" + SDqgroup.getMakType() + "')>" + (all - wan) + "</a></td><td>" + wanchenglv + "</td><td>" + SDqgroup.getExt8() + "</td><td style='background-color:" + (SDqgroup.getMakType().equals("F") ? "#FF9966;" : "") + "'><a href='javascript:void(0);' id='" + SDqgroup.getGroupNum() + "' onclick=javascript:getCaijueInfo('" + SDqgroup.getGroupNum() + "','" + SDqgroup.getExampaperNum() + "') exampaperNum='" + exampaperNum + "'>" + SDqgroup.getExt7() + "</a>/" + SDqgroup.getExt12() + "</td>" + three + "<td><a  href='javascript:void(0)' onclick=javascript:xiangqing('" + SDqgroup.getExampaperNum() + "','" + SDqgroup.getGroupNum() + "')>题组详情</a></td></tr>";
                chongPanAll += (int) Float.parseFloat(SDqgroup.getExt8());
                caiJueAll += ((int) Float.parseFloat(SDqgroup.getExt12())) - ((int) Float.parseFloat(SDqgroup.getExt7()));
                totalAll += (int) Float.parseFloat(SDqgroup.getExt3());
                studentsizeAll += (int) Float.parseFloat(SDqgroup.getExt4());
            }
        }
        String str22 = (str2 + "</table>") + "<div style='padding:30px 0 30px 620px'><a href='javascript:void(0)'onclick=javascript:resetAllGroups('" + exampaperNum + "','" + groupNums + "')>重置所有题组判分</a></div>";
        double all2 = 0.0d;
        if (totalAll != 0) {
            all2 = (100.0f * studentsizeAll) / totalAll;
        }
        List mylist = report.authRole(exampaperNum, null, loginUserId + "");
        String Ball = (Math.floor(all2 * 100.0d) / 100.0d) + "%";
        String str = " <img id='ScheDuleImg' style='margin-top: 1%;margin-left: 5%;' src='<%=basePath %>'> <br> <table style='margin-top: 1%;margin-left: 5%;' ><tr height='60px'><td><br><br>科目进度:</td><td colspan='3' align='left'><div style='float:left;width:200px;height:25px;z-index:0; position:absolute;background-color: #f2f2f2;'><div style='float:left;width:198px;height: px;border:2px solid;z-index:1; position:absolute;'> <span id='inner' style='width:" + all2 + "%;line-height:12px;visibility:visible;display:-moz-inline-stack;display: inline-block;;height: 25px;background-color:#7D7DFA;' >  </span> </div></div></td><td style=''><br><br>";
        if (null != mylist && mylist.size() > 0) {
            str = (str + "<span style='padding:5px;'></span>") + "<a href='javascript:void(0)'onclick=javascript:resetAllWorkRecord('" + exampaperNum + "','" + groupNums + "')>更新所有题组工作量</a>";
        }
        String weicaijue = esc.getweicaiqieNum(this.exam, this.subject, this.grade);
        String color = "";
        if (Integer.parseInt(weicaijue) > 0) {
            color = "red";
        }
        String str3 = (str + "</td></tr><tr><td>总数/题：" + totalAll + "&nbsp;&nbsp;&nbsp;</td><td>完成数/题：" + studentsizeAll + "&nbsp;&nbsp;&nbsp;</td><td>百分比:" + Ball + "&nbsp;&nbsp;&nbsp;</td><td><span>未裁决数:" + caiJueAll + "&nbsp;&nbsp;&nbsp;</span><span style='color:" + color + "'>待裁切页数:" + weicaijue + "&nbsp;&nbsp;&nbsp;</span></td></tr></table>") + str22;
        List resultlist = new ArrayList();
        resultlist.add(str3);
        resultlist.add(ScheDuleImage2(SD));
        this.out.write(JSON.toJSONString(resultlist));
    }

    public void getQuestionGroupScheDule_new() {
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        ServletContext context = this.request.getSession().getServletContext();
        String examPaperNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
        Map<String, Object> kemuResDataMap = new HashMap<>();
        List<Map<String, Object>> tizuResDataMapList = new ArrayList<>();
        Map<String, Map<String, Object>> yuejuanProgressMap = (Map) context.getAttribute(Const.yuejuanProgressMap);
        if (CollUtil.isNotEmpty(yuejuanProgressMap)) {
            Map<String, Object> oneSubjectResDataMap = yuejuanProgressMap.get(examPaperNum);
            if (CollUtil.isNotEmpty(oneSubjectResDataMap)) {
                kemuResDataMap = (Map) oneSubjectResDataMap.get("subjectProgress");
                List<Map<String, Object>> tizuResDataMapList2 = (List) oneSubjectResDataMap.get("questionGroupProgress");
                Comparator<Map<String, Object>> c1 = Comparator.comparing(m1 -> {
                    return Convert.toInt(m1.get("orderNum"), 0);
                });
                Comparator<Map<String, Object>> c2 = Comparator.comparing(m12 -> {
                    return Convert.toInt(m12.get("suborderNum"), 0);
                });
                tizuResDataMapList = (List) tizuResDataMapList2.stream().sorted(c1.thenComparing(c2)).collect(Collectors.toList());
            }
        }
        String groupNums = "";
        String chartBase64 = "";
        if (CollUtil.isNotEmpty(tizuResDataMapList)) {
            int tizuLen = tizuResDataMapList.size();
            String[] columnKeys = new String[tizuLen];
            String[] rowKeys = {"判阅进度"};
            double[][] data = new double[1][tizuLen];
            for (int g = 0; g < tizuLen; g++) {
                Map<String, Object> oneTizuDataMap = tizuResDataMapList.get(g);
                String[] groupNumArr = Convert.toStr(oneTizuDataMap.get("groupNum")).split(Const.STRING_SEPERATOR);
                for (String groupNum : groupNumArr) {
                    groupNums = groupNums + groupNum + "_";
                }
                String questionName = Convert.toStr(oneTizuDataMap.get("questionName"));
                double baifenbi = Convert.toDouble(oneTizuDataMap.get("baifenbi"), Double.valueOf(0.0d)).doubleValue();
                columnKeys[g] = questionName;
                data[0][g] = baifenbi;
            }
            int width = 600;
            if (columnKeys.length > 7) {
                width = 600 + ((columnKeys.length - 7) * 100);
            }
            if (width > 1200) {
                width = 1200;
            }
            JFreeChart chart = this.lc.makeBarChart4(data, rowKeys, columnKeys);
            String chartBase642 = JFreeChartUtils.ToBase64(chart, width, 450);
            chartBase64 = StrUtil.addPrefixIfNot(chartBase642, "data:image/jpeg;base64,");
        }
        kemuResDataMap.put("groupNums", StrUtil.removeSuffix(groupNums, "_"));
        List mylist = report.authRole(examPaperNum, null, loginUserId);
        int myLen = Convert.toInt(Integer.valueOf(mylist.size()), 0).intValue();
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("chartBase64", chartBase64);
        resMap.put("kemuResDataMap", kemuResDataMap);
        resMap.put("showUpdateAllQuesGroupWorkload", Boolean.valueOf(myLen > 0));
        resMap.put("tizuResDataMapList", tizuResDataMapList);
        this.out.write(JSON.toJSONString(resMap));
    }

    public void getQuestionGroupScheDule2() throws IOException {
        String str;
        new DecimalFormat("0.00");
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        String str2 = "<table style='width: 650px;margin-top: 1%;margin-left: 5%;'class='list_table' id='commontable' >  <tr><td bgcolor='#D8D8D8'>题组</td><td bgcolor='#D8D8D8'>单/双评 </td><td bgcolor='#D8D8D8'>总数/题</td><td bgcolor='#D8D8D8'>完成数/题</td><td bgcolor='#D8D8D8'>未完成数</td><td bgcolor='#D8D8D8'>完成率</td><td bgcolor='#D8D8D8'>重判数</td><td bgcolor='#D8D8D8'>已裁决数</td><td bgcolor='#D8D8D8'>三评(裁决)数</td><td bgcolor='#D8D8D8'>操作</td></tr>";
        List<QuestionGroup_question> SD = esc.getQuestionGroupSD(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)));
        int chongPanAll = 0;
        int caiJueAll = 0;
        int totalAll = 0;
        int studentsizeAll = 0;
        String exampaperNum = "";
        String groupNums = "";
        if (null != SD) {
            for (int i = 0; i < SD.size(); i++) {
                QuestionGroup_question SDqgroup = SD.get(i);
                if (i == 0) {
                    exampaperNum = SDqgroup.getExampaperNum().toString();
                }
                if (i == SD.size() - 1) {
                    groupNums = groupNums + SDqgroup.getGroupNum();
                } else {
                    groupNums = groupNums + SDqgroup.getGroupNum() + "_";
                }
                String two = "单评";
                if (SDqgroup.getMakType().toString().equals("1")) {
                    two = "双评";
                }
                int all = (int) Float.parseFloat(SDqgroup.getExt3());
                int wan = (int) Float.parseFloat(SDqgroup.getExt4());
                String colorstyle = "#33CCFF";
                if (all - wan < 0) {
                    wan = all;
                }
                if (all - wan > 0) {
                    colorstyle = "#FF9933";
                }
                String three = "<td>" + SDqgroup.getExt9() + "</td>";
                String groupNum2 = SDqgroup.getGroupNum();
                if (!"s".equals(SDqgroup.getExt10())) {
                    groupNum2 = SDqgroup.getExt10();
                }
                if (((int) Float.parseFloat(SDqgroup.getExt3())) == 0) {
                    str = "--";
                } else {
                    str = ((int) Math.floor((100.0f * wan) / all)) + "%";
                }
                String wanchenglv = str;
                str2 = str2 + "<tr><td>" + SDqgroup.getGroupName() + "</td><td>" + two + "</td><td>" + all + "</td><td>" + wan + "</td><td><a style= 'color:" + colorstyle + "' href='javascript:void(0)' onclick=javascript:qscoreta('" + SDqgroup.getExampaperNum() + "','" + groupNum2 + "','" + SDqgroup.getGroupNum() + "','" + SDqgroup.getMakType() + "')>" + (all - wan) + "</a></td><td>" + wanchenglv + "</td><td>" + SDqgroup.getExt8() + "</td><td>" + SDqgroup.getExt7() + "/" + SDqgroup.getExt12() + "</td>" + three + "<td><a  href='javascript:void(0)' onclick=javascript:xiangqing('" + SDqgroup.getExampaperNum() + "','" + SDqgroup.getGroupNum() + "')>题组详情</a></td></tr>";
                chongPanAll += (int) Float.parseFloat(SDqgroup.getExt8());
                caiJueAll += ((int) Float.parseFloat(SDqgroup.getExt12())) - ((int) Float.parseFloat(SDqgroup.getExt7()));
                totalAll += (int) Float.parseFloat(SDqgroup.getExt3());
                studentsizeAll += (int) Float.parseFloat(SDqgroup.getExt4());
            }
        }
        String str22 = str2 + "</table>";
        double all2 = 0.0d;
        if (totalAll != 0) {
            all2 = (100.0f * studentsizeAll) / totalAll;
        }
        report.authRole(exampaperNum, null, loginUserId + "");
        String Ball = (Math.floor(all2 * 100.0d) / 100.0d) + "%";
        String str3 = " <img id='ScheDuleImg' style='margin-top: 1%;margin-left: 5%;' src='<%=basePath %>'> <br> <table style='margin-top: 1%;margin-left: 5%;' ><tr height='60px'><td><br><br>科目进度:</td><td colspan='3' align='left'><div style='float:left;width:200px;height:25px;z-index:0; position:absolute;background-color: #f2f2f2;'><div style='float:left;width:198px;height: px;border:2px solid;z-index:1; position:absolute;'> <span id='inner' style='width:" + all2 + "%;line-height:12px;visibility:visible;display:-moz-inline-stack;display: inline-block;;height: 25px;background-color:#7D7DFA;' >  </span> </div></div></td><td style=''><br><br>";
        String weicaijue = esc.getweicaiqieNum(this.exam, this.subject, this.grade);
        String color = "";
        if (Integer.parseInt(weicaijue) > 0) {
            color = "red";
        }
        String str4 = (str3 + "</td></tr><tr><td>总数/题：" + totalAll + "&nbsp;&nbsp;&nbsp;</td><td>完成数/题：" + studentsizeAll + "&nbsp;&nbsp;&nbsp;</td><td>百分比:" + Ball + "&nbsp;&nbsp;&nbsp;</td><td>未裁决数:" + caiJueAll + "&nbsp;&nbsp;&nbsp;</td><td style='color:" + color + "'>待裁切页数:" + weicaijue + "&nbsp;&nbsp;&nbsp;</td></tr></table>") + str22;
        List resultlist = new ArrayList();
        resultlist.add(str4);
        resultlist.add(ScheDuleImage2(SD));
        this.out.write(JSON.toJSONString(resultlist));
    }

    public void getCaijueGroupList() {
        String exampaperNum = this.request.getParameter("exampaperNum");
        String groupNum = this.request.getParameter("groupnum");
        List<Map<String, Object>> list = esc.getCaijueGroupList(exampaperNum, groupNum);
        JSONArray json = JSONArray.fromObject(list);
        this.out.write(json.toString());
    }

    public void getNotCaijue() {
        String exampaperNum = this.request.getParameter("exampaperNum");
        String groupNum = this.request.getParameter("groupnum");
        List<Map<String, Object>> list = esc.getNotCaijue(exampaperNum, groupNum);
        JSONArray json = JSONArray.fromObject(list);
        this.out.write(json.toString());
    }

    public void getCaijueInfo() {
        String exampaperNum = this.request.getParameter("exampaperNum");
        String groupNum = this.request.getParameter("groupnum");
        List<Map<String, Object>> list = esc.getCaijueInfo(exampaperNum, groupNum);
        JSONArray json = JSONArray.fromObject(list);
        this.out.write(json.toString());
    }

    public void GCCaiJueCount() {
        new CommonUtil().getLoginUserNum(this.request);
        String count = this.request.getParameter("count");
        String questionNum = this.request.getParameter("questionNum");
        String examPaperNum = this.request.getParameter("examPaperNum");
        String userNum = this.request.getParameter("userNum");
        int rel = this.awardPointService.GCCaiJueCount(examPaperNum, count, questionNum, userNum);
        this.out.write(JSONArray.fromObject(Integer.valueOf(rel)).toString());
    }

    public String ScheDuleImage2(List<QuestionGroup_question> SD) {
        String[] columnKeys = new String[SD.size()];
        String[] rowKeys = {"判阅进度"};
        double[][] data = new double[1][SD.size()];
        if (null != SD) {
            for (int i = 0; i < SD.size(); i++) {
                QuestionGroup_question SDqgroup = SD.get(i);
                String str = SDqgroup.getExt1() + "%";
                int all = (int) Float.parseFloat(SDqgroup.getExt3());
                int wan = (int) Float.parseFloat(SDqgroup.getExt4());
                if (all - wan < 0) {
                    wan = all;
                }
                float a = 0.0f;
                if (all != 0) {
                    a = (float) Math.floor((100.0d * wan) / all);
                }
                columnKeys[i] = SDqgroup.getGroupName();
                data[0][i] = Double.parseDouble(a + "");
            }
        }
        int width = 600;
        if (columnKeys.length > 7) {
            width = 600 + ((columnKeys.length - 7) * 100);
        }
        if (width > 1200) {
            width = 1200;
        }
        JFreeChart chart = this.lc.makeBarChart4(data, rowKeys, columnKeys);
        return JFreeChartUtils.ToBase64(chart, width, 450);
    }

    public void ScheDuleImage() {
        List<QuestionGroup_question> SD = esc.getQuestionGroupSD(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)));
        String[] columnKeys = new String[SD.size()];
        String[] rowKeys = {"判阅进度"};
        double[][] data = new double[1][SD.size()];
        if (null != SD) {
            for (int i = 0; i < SD.size(); i++) {
                QuestionGroup_question SDqgroup = SD.get(i);
                String str = SDqgroup.getExt1() + "%";
                int all = (int) Float.parseFloat(SDqgroup.getExt3());
                int wan = (int) Float.parseFloat(SDqgroup.getExt4());
                if (all - wan < 0) {
                    wan = all;
                }
                float a = 0.0f;
                if (all != 0) {
                    a = (float) Math.floor((100.0d * wan) / all);
                }
                columnKeys[i] = SDqgroup.getGroupName();
                data[0][i] = Double.parseDouble(a + "");
            }
        }
        int width = 600;
        if (columnKeys.length > 7) {
            width = 600 + ((columnKeys.length - 7) * 100);
        }
        if (width > 1200) {
            width = 1200;
        }
        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.reset();
            response.setContentType("image/jpeg");
            JFreeChart chart = this.lc.makeBarChart4(data, rowKeys, columnKeys);
            ChartUtils.writeChartAsJPEG(response.getOutputStream(), chart, width, 450);
        } catch (Exception e) {
        }
    }

    public void getSubjectGroup1_old() {
        this.request.getParameter(License.SYSTYPE);
        List<QuestionGroup_question> list = esc.getQuestionGroupSD1(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)));
        ArrayList arrayList = new ArrayList();
        for (int k = 0; k < list.size(); k++) {
            ArrayList arrayList2 = new ArrayList();
            QuestionGroup_question qq = list.get(k);
            arrayList2.add(qq.getExt1());
            arrayList2.add(qq.getExt2());
            BigDecimal totalAll = Convert.toBigDecimal(qq.getExt3(), BigDecimal.valueOf(0L));
            BigDecimal studentsizeAll = Convert.toBigDecimal(qq.getExt4(), BigDecimal.valueOf(0L));
            this.log.info("【" + qq.getExampaperNum() + "】---" + studentsizeAll + "---" + totalAll);
            BigDecimal all = BigDecimal.valueOf(0L);
            if (totalAll.compareTo(BigDecimal.valueOf(0L)) != 0) {
                all = studentsizeAll.multiply(BigDecimal.valueOf(100L)).divide(totalAll, 2, RoundingMode.FLOOR);
            }
            if (all.compareTo(BigDecimal.valueOf(100L)) > 0) {
                all = BigDecimal.valueOf(100L);
            }
            String Ball = all.stripTrailingZeros().toPlainString() + "%";
            arrayList2.add(Ball);
            String weicaiqie = esc.getweicaiqieNum(this.exam, qq.getExt1(), this.grade);
            arrayList2.add(weicaiqie);
            arrayList2.add(qq.getExt5());
            arrayList2.add(qq.getExt6());
            arrayList.add(arrayList2);
        }
        List resultlist = new ArrayList();
        resultlist.add(arrayList);
        resultlist.add(SubjectGroupImage11(list));
        this.out.write(JSON.toJSONString(resultlist));
    }

    public void getSubjectGroup1() {
        if (StrUtil.isEmpty(this.exam) || StrUtil.isEmpty(this.grade)) {
            return;
        }
        List<AjaxData> list = esc.getAjaxSubjectList(Convert.toInt(this.exam), null, Convert.toInt(this.grade));
        if (CollUtil.isEmpty(list)) {
            return;
        }
        int sLen = list.size();
        ServletContext context = this.request.getSession().getServletContext();
        Map<String, Map<String, Object>> yuejuanProgressMap = (Map) context.getAttribute(Const.yuejuanProgressMap);
        ArrayList arrayList = new ArrayList();
        String[] columnKeys = new String[sLen];
        String[] rowKeys = {"科目判阅进度"};
        double[][] data = new double[1][sLen];
        for (int s = 0; s < sLen; s++) {
            AjaxData oneSubMap = list.get(s);
            String examPaperNum = oneSubMap.getExt1();
            HashMap hashMap = new HashMap();
            hashMap.put(Const.EXPORTREPORT_subjectNum, oneSubMap.getNum());
            hashMap.put("subjectName", oneSubMap.getName());
            System.currentTimeMillis();
            String appealDate = null == oneSubMap.getExt2() ? "" : "--至" + oneSubMap.getExt2();
            String shensunum = oneSubMap.getExt3();
            hashMap.put("appealDate", shensunum.equals("0") ? "未开启" + appealDate : "已开启" + appealDate);
            columnKeys[s] = oneSubMap.getName();
            if (CollUtil.isNotEmpty(yuejuanProgressMap)) {
                Map<String, Object> oneSubjectResDataMap = yuejuanProgressMap.get(examPaperNum);
                if (CollUtil.isNotEmpty(oneSubjectResDataMap)) {
                    Map<String, Object> kemuResDataMap = (Map) oneSubjectResDataMap.get("subjectProgress");
                    if (CollUtil.isNotEmpty(kemuResDataMap)) {
                        Object baifenbi = kemuResDataMap.get("baifenbi");
                        data[0][s] = Convert.toDouble(baifenbi, Double.valueOf(0.0d)).doubleValue();
                        hashMap.put("jindu", baifenbi + "%");
                        hashMap.put("weicaiqieCount", kemuResDataMap.get("weicaiqieCount"));
                        hashMap.put("weikaifangTizuCount", kemuResDataMap.get("weikaifangTizuCount"));
                        hashMap.put("weikaifangKaodianCount", kemuResDataMap.get("weikaifangKaodianCount"));
                        hashMap.put("KaodianCount", kemuResDataMap.get("KaodianCount"));
                    }
                }
            }
            if (!hashMap.containsKey("jindu")) {
                data[0][s] = 0.0d;
                hashMap.put("jindu", "--%");
                hashMap.put("weicaiqieCount", "--");
                hashMap.put("weikaifangTizuCount", "--");
                hashMap.put("weikaifangKaodianCount", "--");
                hashMap.put("KaodianCount", "--");
            }
            arrayList.add(hashMap);
        }
        int width = columnKeys.length * 150;
        JFreeChart chart = this.lc.makeBarChart5(data, rowKeys, columnKeys);
        Object chartBase64 = JFreeChartUtils.ToBase64(chart, width, 450);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("tabDataList", arrayList);
        resMap.put("chartBase64", chartBase64);
        this.out.write(JSON.toJSONString(resMap));
    }

    public void getSubjectGroup() throws IOException {
        String systemType = this.request.getParameter(License.SYSTYPE);
        List list = esc.getAjaxSubjectList(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(systemType)), Integer.valueOf(Integer.parseInt(this.grade)));
        List li = new ArrayList();
        for (int k = 0; k < list.size(); k++) {
            ArrayList arrayList = new ArrayList();
            AjaxData c = list.get(k);
            this.subject = c.getNum();
            arrayList.add(c.getNum());
            arrayList.add(c.getName());
            String examPaperNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
            List<QuestionGroup_question> Qlist = esc.getQuestionGroupSD(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)));
            int totalAll = 0;
            int studentsizeAll = 0;
            if (Qlist == null) {
                totalAll = 0;
            } else if (Qlist.size() > 0) {
                for (int i = 0; i < Qlist.size(); i++) {
                    QuestionGroup_question q = Qlist.get(i);
                    totalAll += (int) Float.parseFloat(q.getExt3());
                }
            }
            List<QuestionGroup_question> qT = esc.getQuesTask(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)), Integer.valueOf(Integer.parseInt(examPaperNum)));
            if (qT == null) {
                studentsizeAll = 0;
            } else if (qT.size() > 0) {
                QuestionGroup_question qtt = qT.get(0);
                if (qtt.getExt1() == null) {
                    studentsizeAll = 0;
                } else {
                    studentsizeAll = (int) Float.parseFloat(qtt.getExt1());
                }
            }
            this.log.info("【" + c.getName() + "】---" + studentsizeAll + "---" + totalAll);
            double all = 0.0d;
            if (totalAll != 0) {
                all = (100.0f * studentsizeAll) / totalAll;
            }
            if (all > 100.0d) {
                all = 100.0d;
            }
            String Ball = (Math.floor(all * 100.0d) / 100.0d) + "%";
            arrayList.add(Ball);
            String weicaiqie = esc.getweicaiqieNum(this.exam, this.subject, this.grade);
            arrayList.add(weicaiqie);
            li.add(arrayList);
        }
        this.out.write(JSONArray.fromObject(li).toString());
    }

    public void getweicaiqieNum() {
        String weicaiqie = esc.getweicaiqieNum(this.exam, this.subject, this.grade);
        this.out.write(weicaiqie);
    }

    public void getTopscoretit() throws Exception {
        String subTypeName = report.rptFileFolder("", "", "", this.studentType, "");
        List<Exam> ll = esc.getExamNameByNum(this.examNum);
        Exam e = new Exam();
        if (ll.size() > 0) {
            e = ll.get(0);
        }
        String str = "<b> 学校：" + esc.getSchoolNameByNum(this.schoolNum).toString() + "&nbsp;&nbsp;年级：" + esc.getGradeNameByNum(this.schoolNum, this.gradeNum).toString() + "&nbsp;&nbsp;科类：" + subTypeName + "&nbsp;&nbsp;考试类型：" + e.getExamName() + "&nbsp;&nbsp;时间：" + e.getExamDate() + "</b><br> ";
        this.out.write(str);
    }

    public void getTopscore() {
        this.classNum = Const.class_grade;
        try {
            List list = report.maxClaScore(this.examNum, this.subjectNum, this.gradeNum, this.schoolNum, this.classNum, this.type, this.studentType, this.source, "0", this.fufen, this.subCompose, this.islevel);
            this.out.write(JSONArray.fromObject(list).toString());
        } catch (Exception e) {
            this.out.write("[]");
        }
    }

    public void ScoreRanking() throws Exception {
        String subTypeName = report.rptFileFolder("", "", "", this.subjectType, "");
        List<Exam> ll = esc.getExamNameByNum(this.exam);
        Exam e = new Exam();
        if (ll.size() > 0) {
            e = ll.get(0);
        }
        String str = "<b> 学校：" + esc.getSchoolNameByNum(this.schoolNum).toString() + "&nbsp;&nbsp;年级：" + esc.getGradeNameByNum(this.schoolNum, this.gradeNum).toString() + "&nbsp;&nbsp;科类：" + subTypeName + "&nbsp;&nbsp;科目：" + esc.getSubjectNameByNum(this.subject).get(0).getSubjectName() + "&nbsp;&nbsp;考试类型：" + e.getExamName() + "&nbsp;&nbsp;时间：" + e.getExamDate() + "</b><br> ";
        this.out.write(str);
    }

    public void studentScore() throws Exception {
        List list = new ArrayList();
        String str = "数据不足......";
        List<Class> list1 = report.getStudentScoreClass(this.schoolNum, this.gradeNum, this.cla, this.exam);
        if (null != list1) {
            list.add(list1);
        } else {
            this.out.write(str);
        }
        for (int i = 0; i < list1.size(); i++) {
            Class c = list1.get(i);
            String cl = c.getClassNum().toString();
            Exampaper examPaperObj = esc.getExampaperInfo(this.exam, this.subject, this.gradeNum);
            String examPaperNum = "";
            if (null != examPaperObj) {
                examPaperNum = String.valueOf(examPaperObj.getExamPaperNum());
            }
            if (null == examPaperNum || "".equals(examPaperNum)) {
                if (this.subject.equals("-1")) {
                    examPaperNum = this.subject + "" + this.gradeNum;
                } else {
                    examPaperNum = this.gradeNum + "" + this.subject;
                }
            }
            List l = report.getStudentScore(this.exam, this.gradeNum, this.subject, this.schoolNum, cl, "0", this.subjectType, this.type, this.stuSource, examPaperNum);
            list.add(l);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void sptital() {
        String subTypeName = report.rptFileFolder("", "", "", this.subjectType, "");
        String subjectName = report.rptFileFolder("", "", "", "", this.subject);
        List<Exam> ll = esc.getExamNameByNum(this.exam);
        Exam e = new Exam();
        if (ll.size() > 0) {
            e = ll.get(0);
        }
        String str = "<div align='left'><b> 学校：" + esc.getSchoolNameByNum(this.schoolNum).toString() + "&nbsp;&nbsp;年级：" + esc.getGradeNameByNum(this.schoolNum, this.gradeNum).toString() + "&nbsp;&nbsp;科类：" + subTypeName + "&nbsp;&nbsp;学科：" + subjectName + "&nbsp;&nbsp;班级：" + esc.getClassNameByNum(this.cla, this.gradeNum).toString() + "&nbsp;&nbsp;考试类型：" + e.getExamName() + "&nbsp;&nbsp;时间：" + e.getExamDate() + "</div> ";
        this.out.write(str);
    }

    public void SubjectGroupImage() {
        CommonUtil.getSystemType(this.request);
        List list = esc.getAjaxSubjectList(Integer.valueOf(Integer.parseInt(this.exam)), 2, Integer.valueOf(Integer.parseInt(this.grade)));
        String[] columnKeys = new String[list.size()];
        String[] rowKeys = {"科目判阅进度"};
        double[][] data = new double[1][list.size()];
        for (int k = 0; k < list.size(); k++) {
            AjaxData c = list.get(k);
            this.subject = c.getNum();
            String examPaperNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
            List<QuestionGroup_question> Qlist = esc.getQuestionGroupSD(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)));
            int totalAll = 0;
            int studentsizeAll = 0;
            if (Qlist == null) {
                totalAll = 0;
            } else if (Qlist.size() > 0) {
                for (int i = 0; i < Qlist.size(); i++) {
                    QuestionGroup_question q = Qlist.get(i);
                    totalAll += (int) Float.parseFloat(q.getExt3());
                }
            }
            List<QuestionGroup_question> qT = esc.getQuesTask(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)), Integer.valueOf(Integer.parseInt(this.subject)), Integer.valueOf(Integer.parseInt(examPaperNum)));
            if (qT == null) {
                studentsizeAll = 0;
            } else if (qT.size() > 0) {
                QuestionGroup_question qtt = qT.get(0);
                if (qtt.getExt1() == null) {
                    studentsizeAll = 0;
                } else {
                    studentsizeAll = (int) Float.parseFloat(qtt.getExt1());
                }
            }
            columnKeys[k] = c.getName();
            double all = 0.0d;
            if (studentsizeAll != 0) {
                all = (100 * studentsizeAll) / totalAll;
            }
            data[0][k] = all;
        }
        int width = columnKeys.length * 110;
        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.reset();
            response.setContentType("image/jpeg");
            JFreeChart chart = this.lc.makeBarChart5(data, rowKeys, columnKeys);
            ChartUtils.writeChartAsJPEG(response.getOutputStream(), chart, width, 450);
        } catch (Exception e) {
        }
    }

    public String SubjectGroupImage11(List<QuestionGroup_question> list) {
        CommonUtil.getSystemType(this.request);
        String[] columnKeys = new String[list.size()];
        String[] rowKeys = {"科目判阅进度"};
        double[][] data = new double[1][list.size()];
        for (int k = 0; k < list.size(); k++) {
            QuestionGroup_question qq = list.get(k);
            BigDecimal totalAll = Convert.toBigDecimal(qq.getExt3(), BigDecimal.valueOf(0L));
            BigDecimal studentsizeAll = Convert.toBigDecimal(qq.getExt4(), BigDecimal.valueOf(0L));
            columnKeys[k] = qq.getExt2();
            BigDecimal all = BigDecimal.valueOf(0L);
            if (totalAll.compareTo(BigDecimal.valueOf(0L)) != 0) {
                all = studentsizeAll.multiply(BigDecimal.valueOf(100L)).divide(totalAll, 0, RoundingMode.FLOOR);
            }
            if (all.compareTo(BigDecimal.valueOf(100L)) > 0) {
                all = BigDecimal.valueOf(100L);
            }
            int num = Convert.toInt(all, 0).intValue();
            data[0][k] = num;
        }
        int width = columnKeys.length * 150;
        JFreeChart chart = this.lc.makeBarChart5(data, rowKeys, columnKeys);
        return JFreeChartUtils.ToBase64(chart, width, 450);
    }

    public void SubjectGroupImage1() {
        CommonUtil.getSystemType(this.request);
        List<QuestionGroup_question> list = esc.getQuestionGroupSD1(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(this.grade)));
        String[] columnKeys = new String[list.size()];
        String[] rowKeys = {"科目判阅进度"};
        double[][] data = new double[1][list.size()];
        for (int k = 0; k < list.size(); k++) {
            QuestionGroup_question qq = list.get(k);
            int totalAll = (int) Float.parseFloat(qq.getExt3());
            int studentsizeAll = (int) Float.parseFloat(qq.getExt4());
            columnKeys[k] = qq.getExt2();
            double all = 0.0d;
            if (totalAll != 0) {
                all = (100.0f * studentsizeAll) / totalAll;
            }
            if (all > 100.0d) {
                all = 100.0d;
            }
            double Ball = Math.floor(all * 100.0d) / 100.0d;
            int num = (int) Ball;
            data[0][k] = num;
        }
        int width = columnKeys.length * 150;
        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.reset();
            response.setContentType("image/jpeg");
            JFreeChart chart = this.lc.makeBarChart5(data, rowKeys, columnKeys);
            ChartUtils.writeChartAsJPEG(response.getOutputStream(), chart, width, 450);
        } catch (Exception e) {
        }
    }

    public void exportTopscore() throws Exception {
        this.classNum = Const.class_grade;
        String dirPath = CommonUtil.getRootPath(this.request);
        String uri = this.request.getRequestURL().toString();
        String loginUserNum = new CommonUtil().getLoginUserNum(this.request) + "";
        String agent = this.request.getHeader("User-Agent").toLowerCase();
        this.reportExportService.deleteFileAndDirect(dirPath, "ExportFolder/reportExcel" + loginUserNum);
        this.reportExportService.ajaxAction_exportTopscore(this.examNum, this.gradeNum, this.schoolNum, this.subjectNum, null, this.studentId, this.studentType, this.type, this.source, "10", "50", null, this.rpt_name, this.isMoreSchool, this.rptTitle, "F", this.isSaveWrite, dirPath, uri, loginUserNum, null, null, null, null, dirPath, agent, this.fufen, this.subCompose, this.islevel);
    }

    public void exportSubjectScoreRanking() throws Exception {
        ServletOutputStream outputStream;
        byte[] tempByte;
        FileInputStream in;
        ServletOutputStream outputStream2;
        byte[] tempByte2;
        FileInputStream in2;
        ServletOutputStream outputStream3;
        byte[] tempByte3;
        FileInputStream in3;
        List<Class> list1 = report.getStudentScoreClass(this.schoolNum, this.gradeNum, this.cla, this.exam);
        Exampaper examPaperObj = esc.getExampaperInfo(this.exam, this.subject, this.gradeNum);
        String examPaperNum = null != examPaperObj ? String.valueOf(examPaperObj.getExamPaperNum()) : "";
        if (null == examPaperNum || "".equals(examPaperNum)) {
            examPaperNum = this.subject.equals("-1") ? this.subject + "" + this.gradeNum : this.gradeNum + "" + this.subject;
        }
        int hebing = 0;
        String XLS = GUID.getGUID() + ".xls";
        String path = Define.class.getResource("/").getPath().toString() + XLS;
        File excelFile = new File(path);
        if (!excelFile.exists()) {
            excelFile.createNewFile();
        }
        WritableWorkbook wwBook = Workbook.createWorkbook(excelFile);
        WritableFont.FontName font = WritableFont.createFont("新宋体");
        WritableFont wf1 = new WritableFont(font, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.GRAY_50, ScriptStyle.NORMAL_SCRIPT);
        new WritableCellFormat(wf1);
        WritableFont wf2 = new WritableFont(font, 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK, ScriptStyle.NORMAL_SCRIPT);
        new WritableCellFormat(wf2);
        WritableFont biaotis = new WritableFont(WritableFont.ARIAL, 15, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableCellFormat biaotis1 = new WritableCellFormat(biaotis);
        biaotis1.setAlignment(Alignment.LEFT);
        WritableFont font1 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableFont font2 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableFont font3 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableCellFormat cFormat1 = new WritableCellFormat(font1);
        WritableCellFormat cFormat2 = new WritableCellFormat(font2);
        WritableCellFormat cFormat3 = new WritableCellFormat(font3);
        cFormat1.setAlignment(Alignment.LEFT);
        cFormat2.setAlignment(Alignment.CENTRE);
        cFormat2.setBackground(Colour.GRAY_25);
        cFormat3.setAlignment(Alignment.CENTRE);
        cFormat3.setBackground(Colour.WHITE);
        try {
            WritableSheet sheet = wwBook.createSheet("学科成绩班级排名", 0);
            if (0 == 0) {
                try {
                    Label biaoti = new Label(0, 0, "G5 学科成绩班级排名", biaotis1);
                    sheet.addCell(biaoti);
                    String subTypeName = report.rptFileFolder("", "", "", this.subjectType, "");
                    List<Exam> ll = esc.getExamNameByNum(this.exam);
                    Exam e = new Exam();
                    if (ll.size() > 0) {
                        e = ll.get(0);
                    }
                    String xinxi = "学校：" + esc.getSchoolNameByNum(this.schoolNum).toString() + "  年级：" + esc.getGradeNameByNum(this.schoolNum, this.gradeNum).toString() + " 科类：" + subTypeName + " 科目：" + esc.getSubjectNameByNum(this.subject).get(0).getSubjectName() + " 考试类型：" + e.getExamName() + " 时间：" + e.getExamDate();
                    Label xin_xi = new Label(0, 1, xinxi, cFormat1);
                    sheet.addCell(xin_xi);
                    for (int i = 0; i < list1.size(); i++) {
                        if (i % 2 == 0) {
                            Class c = list1.get(i);
                            String cl = c.getClassNum();
                            List<Studentlevel> l = report.getStudentScore(this.exam, this.gradeNum, this.subject, this.schoolNum, cl, "0", this.subjectType, this.type, this.stuSource, examPaperNum);
                            for (int j = 0; j < l.size(); j++) {
                                Label studentName_vlaue = new Label(4 * i, 4 + j, l.get(j).getStudentName(), cFormat3);
                                sheet.addCell(studentName_vlaue);
                                Label original_vlaue = new Label((4 * i) + 1, 4 + j, l.get(j).getTotalScore().toString(), cFormat3);
                                sheet.addCell(original_vlaue);
                                Label classRanking_vlaue = new Label((4 * i) + 2, 4 + j, String.valueOf(l.get(j).getClassRanking()), cFormat3);
                                sheet.addCell(classRanking_vlaue);
                                Label gradeRanking_vlaue = new Label((4 * i) + 3, 4 + j, String.valueOf(l.get(j).getGradeRanking()), cFormat3);
                                sheet.addCell(gradeRanking_vlaue);
                            }
                            Label className = new Label(4 * i, 2, list1.get(i).getClassName(), cFormat3);
                            sheet.addCell(className);
                            sheet.mergeCells(4 * i, 2, (4 * i) + 3, 2);
                            Label studentName = new Label(4 * i, 3, "学生姓名", cFormat3);
                            sheet.addCell(studentName);
                            Label original = new Label((4 * i) + 1, 3, "原始分", cFormat3);
                            sheet.addCell(original);
                            Label classRanking = new Label((4 * i) + 2, 3, "班级排名", cFormat3);
                            sheet.addCell(classRanking);
                            Label gradeRanking = new Label((4 * i) + 3, 3, "年级排名", cFormat3);
                            sheet.addCell(gradeRanking);
                        } else {
                            Class c2 = list1.get(i);
                            String cl2 = c2.getClassNum();
                            List<Studentlevel> l2 = report.getStudentScore(this.exam, this.gradeNum, this.subject, this.schoolNum, cl2, "0", this.subjectType, this.type, this.stuSource, examPaperNum);
                            for (int j2 = 0; j2 < l2.size(); j2++) {
                                Label studentName_vlaue2 = new Label(4 * i, 4 + j2, l2.get(j2).getStudentName(), cFormat2);
                                sheet.addCell(studentName_vlaue2);
                                Label original_vlaue2 = new Label((4 * i) + 1, 4 + j2, l2.get(j2).getTotalScore().toString(), cFormat2);
                                sheet.addCell(original_vlaue2);
                                Label classRanking_vlaue2 = new Label((4 * i) + 2, 4 + j2, String.valueOf(l2.get(j2).getClassRanking()), cFormat2);
                                sheet.addCell(classRanking_vlaue2);
                                Label gradeRanking_vlaue2 = new Label((4 * i) + 3, 4 + j2, String.valueOf(l2.get(j2).getGradeRanking()), cFormat2);
                                sheet.addCell(gradeRanking_vlaue2);
                            }
                            Label className2 = new Label(4 * i, 2, list1.get(i).getClassName(), cFormat2);
                            sheet.addCell(className2);
                            sheet.mergeCells(4 * i, 2, (4 * i) + 3, 2);
                            Label studentName2 = new Label(4 * i, 3, "学生姓名", cFormat2);
                            sheet.addCell(studentName2);
                            Label original2 = new Label((4 * i) + 1, 3, "原始分", cFormat2);
                            sheet.addCell(original2);
                            Label classRanking2 = new Label((4 * i) + 2, 3, "班级排名", cFormat2);
                            sheet.addCell(classRanking2);
                            Label gradeRanking2 = new Label((4 * i) + 3, 3, "年级排名", cFormat2);
                            sheet.addCell(gradeRanking2);
                        }
                        hebing = i;
                    }
                    sheet.mergeCells(0, 0, (4 * hebing) + 3, 0);
                    sheet.mergeCells(0, 1, (4 * hebing) + 3, 1);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            wwBook.write();
            if (wwBook != null) {
                try {
                    wwBook.close();
                    HttpServletResponse response = ServletActionContext.getResponse();
                    response.reset();
                    outputStream3 = response.getOutputStream();
                    String agent = this.request.getHeader("User-Agent").toLowerCase();
                    String loadFileName = agent.indexOf("firefox") == -1 ? URLEncoder.encode("导出_学科成绩班级排名", "UTF-8") : new String("导出_学科成绩班级排名".getBytes("UTF-8"), "ISO8859-1");
                    response.setContentType("Content-type: applicationnd.ms-excel");
                    response.setHeader("Content-disposition", "attachment;filename=" + loadFileName + ".xls");
                    response.setHeader("Content-length", String.valueOf(excelFile.length()));
                    tempByte3 = new byte[1024];
                    in3 = new FileInputStream(excelFile);
                } catch (WriteException e3) {
                    e3.printStackTrace();
                    return;
                }
                while (true) {
                    int n = in3.read(tempByte3);
                    if (n == -1) {
                        break;
                    }
                    try {
                        outputStream3.write(tempByte3, 0, n);
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                    e3.printStackTrace();
                    return;
                }
                outputStream3.flush();
                outputStream3.close();
                in3.close();
            }
        } catch (Exception e5) {
            if (wwBook != null) {
                try {
                    wwBook.close();
                    HttpServletResponse response2 = ServletActionContext.getResponse();
                    response2.reset();
                    outputStream2 = response2.getOutputStream();
                    String agent2 = this.request.getHeader("User-Agent").toLowerCase();
                    String loadFileName2 = agent2.indexOf("firefox") == -1 ? URLEncoder.encode("导出_学科成绩班级排名", "UTF-8") : new String("导出_学科成绩班级排名".getBytes("UTF-8"), "ISO8859-1");
                    response2.setContentType("Content-type: applicationnd.ms-excel");
                    response2.setHeader("Content-disposition", "attachment;filename=" + loadFileName2 + ".xls");
                    response2.setHeader("Content-length", String.valueOf(excelFile.length()));
                    tempByte2 = new byte[1024];
                    in2 = new FileInputStream(excelFile);
                } catch (WriteException e6) {
                    e6.printStackTrace();
                    return;
                }
                while (true) {
                    int n2 = in2.read(tempByte2);
                    if (n2 == -1) {
                        break;
                    }
                    try {
                        outputStream2.write(tempByte2, 0, n2);
                    } catch (Exception e7) {
                        e7.printStackTrace();
                    }
                    e6.printStackTrace();
                    return;
                }
                outputStream2.flush();
                outputStream2.close();
                in2.close();
            }
        } catch (Throwable th) {
            if (wwBook != null) {
                try {
                    wwBook.close();
                    HttpServletResponse response3 = ServletActionContext.getResponse();
                    response3.reset();
                    outputStream = response3.getOutputStream();
                    String agent3 = this.request.getHeader("User-Agent").toLowerCase();
                    String loadFileName3 = agent3.indexOf("firefox") == -1 ? URLEncoder.encode("导出_学科成绩班级排名", "UTF-8") : new String("导出_学科成绩班级排名".getBytes("UTF-8"), "ISO8859-1");
                    response3.setContentType("Content-type: applicationnd.ms-excel");
                    response3.setHeader("Content-disposition", "attachment;filename=" + loadFileName3 + ".xls");
                    response3.setHeader("Content-length", String.valueOf(excelFile.length()));
                    tempByte = new byte[1024];
                    in = new FileInputStream(excelFile);
                } catch (WriteException e8) {
                    e8.printStackTrace();
                    throw th;
                }
                while (true) {
                    int n3 = in.read(tempByte);
                    if (n3 == -1) {
                        break;
                    }
                    try {
                        outputStream.write(tempByte, 0, n3);
                    } catch (Exception e9) {
                        e9.printStackTrace();
                    }
                    e8.printStackTrace();
                    throw th;
                }
                outputStream.flush();
                outputStream.close();
                in.close();
            }
            throw th;
        }
    }

    public void getAjaxSchoolList() throws IOException {
        String str = "";
        List<AjaxData> list = esc.getAjaxSchoolList(this.exam, this.subject);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAjaxGradeList2() {
        String str = "";
        List<AjaxData> list = esc.getAjaxGradeList(this.exam, this.subject, this.school);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAjaxGradeList() {
        String str = "";
        String systemType = CommonUtil.getSystemType(this.request);
        List<AjaxData> list = esc.getAjaxGradeList2(this.exam, this.subject, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAjaxGradeList_zy() {
        String str = "";
        String systemType = this.request.getParameter("er");
        List<AjaxData> list = esc.getAjaxGradeList2_zy(this.exam, this.subject, systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void userpositionGradeList() {
        HttpSession session = this.request.getSession(true);
        this.mark = (String) session.getAttribute(Const.SYSTEM_TYPE);
        String uid = new CommonUtil().getLoginUserNum(this.request);
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "";
        List<AjaxData> list = esc.getUserPositionGradeList(this.exam, String.valueOf(uid), this.subject, this.mark);
        if (null != list && list.size() > 0) {
            str = JSONArray.fromObject(list).toString();
        } else {
            List<AjaxData> list_all = esc.getAjaxGradeList2(this.exam, this.subject, systemType);
            if (null != list_all) {
                str = JSONArray.fromObject(list_all).toString();
            }
        }
        this.out.write(str);
    }

    public void getAjaxClassList() {
        String str = "";
        List<AjaxData> list = esc.getAjaxClassList(this.exam, this.school, this.subject, this.grade);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAjaxSubjectList() {
        List<AjaxData> list;
        String str = "[]";
        String systemType = CommonUtil.getSystemType(this.request);
        new ArrayList();
        if (null != this.exam && !this.exam.equals("") && !this.exam.equals("null") && null != (list = esc.getAjaxSubjectList(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(systemType)), null))) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAjaxSubjectList_zy() {
        List<AjaxData> list;
        String str = "[]";
        String definType = this.request.getParameter("defineType");
        new ArrayList();
        if (null != this.exam && !this.exam.equals("") && !this.exam.equals("null")) {
            if (!"".equals(definType) && null != definType && definType.equals("3")) {
                list = esc.getAjaxSubjectListForOE_zy(Integer.valueOf(Integer.parseInt(this.exam)), null, null);
            } else {
                list = esc.getAjaxSubjectList_zy(Integer.valueOf(Integer.parseInt(this.exam)), null, null);
            }
            if (null != list) {
                str = JSONArray.fromObject(list).toString();
            }
        }
        this.out.write(str);
    }

    public void userpositionSubjectList() {
        HttpSession session = this.request.getSession(true);
        String uid = new CommonUtil().getLoginUserNum(this.request);
        new CommonUtil().getLoginUserName(this.request);
        this.mark = (String) session.getAttribute(Const.SYSTEM_TYPE);
        String systemType = CommonUtil.getSystemType(this.request);
        String str = "[]";
        if (null != this.exam && !this.exam.equals("") && !this.exam.equals("null")) {
            User user = new CommonUtil().getLoginUser(this.request);
            List roleNum = esc.getUserroleNum(String.valueOf(uid));
            String ro = "";
            if (roleNum.size() > 0) {
                for (int x = 0; x < roleNum.size(); x++) {
                    if ("-1".equals(((Userrole) roleNum.get(x)).getRoleNum())) {
                        ro = ((Userrole) roleNum.get(x)).getRoleNum() + "";
                    }
                }
            }
            if (user.getUsertype().equals("0") || ro.equals("-1")) {
                List<AjaxData> list2 = esc.getAjaxSubjectList(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(systemType)), null);
                if (null != list2) {
                    str = JSONArray.fromObject(list2).toString();
                }
            } else {
                List<AjaxData> list = esc.getUserPositionSubjectList(this.exam, String.valueOf(uid), this.mark);
                if (null != list && list.size() > 0) {
                    str = JSONArray.fromObject(list).toString();
                } else {
                    List<AjaxData> list22 = esc.getAjaxSubjectList(Integer.valueOf(Integer.parseInt(this.exam)), Integer.valueOf(Integer.parseInt(systemType)), null);
                    if (null != list22) {
                        str = JSONArray.fromObject(list22).toString();
                    }
                }
            }
        }
        this.out.write(str);
    }

    public void getAjaxExamList() {
        String str = "";
        String systemType = CommonUtil.getSystemType(this.request);
        List<AjaxData> list = esc.getAjaxExamList(systemType);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAjaxExamList_zy() {
        String str = "";
        String systemType = CommonUtil.getSystemType(this.request);
        String userId = String.valueOf(((User) this.session.get(Const.LOGIN_USER)).getId());
        String oneOrMore = this.request.getParameter("oneOrMore");
        List<AjaxData> list = esc.getAjaxExamList_zy(systemType, userId, oneOrMore);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAjaxTaskList() {
        String str;
        List<Task> list = esc.getAjaxTaskList(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.questionNum);
        float gongcha = Float.valueOf(this.fullScore2).floatValue() / 8.0f;
        float gongchaVal = 0.5f;
        int gongchazheng = (int) gongcha;
        float xiaoshu = gongcha - gongchazheng;
        if (xiaoshu > 0.5d) {
            gongchaVal = (float) Math.ceil(gongcha);
        } else if (xiaoshu <= 0.5d && xiaoshu > 0.0f) {
            gongchaVal = (float) (gongchazheng + 0.5d);
        } else if (xiaoshu == 0.0f) {
            gongchaVal = gongcha;
        }
        String str2 = "<tr>";
        String str3 = str2 + "<td bgcolor='#D8D8D8'>选择<br><input type='checkbox' name='checkAll' class='checkAll' value='all' checked onclick='checkAll()'/></td><td bgcolor='#D8D8D8'>学校</td><td bgcolor='#D8D8D8'>编号</td><td bgcolor='#D8D8D8'>评卷员</td>";
        int j = 0;
        while (true) {
            if (j == 0) {
                str = str3 + "<td bgcolor='#D8D8D8'>" + j + "分</td>";
            } else if ((0.5d * j) + (gongchaVal * j) < Float.valueOf(this.fullScore2).floatValue()) {
                str = str3 + "<td bgcolor='#D8D8D8'>" + ((0.5d * j) + (gongchaVal * (j - 1))) + "-" + ((0.5d * j) + (gongchaVal * j)) + "分</td>";
            } else if ((0.5d * j) + (gongchaVal * j) < Float.valueOf(this.fullScore2).floatValue() || (0.5d * j) + (gongchaVal * (j - 1)) >= Float.valueOf(this.fullScore2).floatValue()) {
                break;
            } else if (Float.valueOf(this.fullScore2).floatValue() - 0.5d != (0.5d * j) + (gongchaVal * (j - 1))) {
                str = str3 + "<td bgcolor='#D8D8D8'>" + ((0.5d * j) + (gongchaVal * (j - 1))) + "-" + (Float.valueOf(this.fullScore2).floatValue() - 0.5d) + "分</td>";
            } else {
                str = str3 + "<td bgcolor='#D8D8D8'>" + ((0.5d * j) + (gongchaVal * (j - 1))) + "分</td>";
            }
            str3 = str;
            j++;
        }
        String str4 = str3 + "<td bgcolor='#D8D8D8'>" + Float.valueOf(this.fullScore2) + "分</td></tr>";
        new ArrayList();
        Map<String, String> map = new HashMap<>();
        for (int j2 = 0; j2 < list.size(); j2++) {
            Task task = list.get(j2);
            map.put(task.getTeacherNum(), task.getUpdateUser());
        }
        String str22 = "";
        int num = 1;
        for (String key : map.keySet()) {
            String value = map.get(key);
            String s = "<tr><td><input type='checkbox' name='checkbox' value='" + key + "' checked/></td>";
            int m = 0;
            while (true) {
                if (m >= list.size()) {
                    break;
                }
                Task task2 = list.get(m);
                if (!task2.getTeacherNum().equals(key)) {
                    m++;
                } else {
                    s = s + "<td>" + task2.getSchoolName() + "</td>";
                    break;
                }
            }
            String s2 = (s + "<td>" + key + "</td>") + "<td>" + value + "</td>";
            int j3 = 0;
            while (true) {
                int total = 0;
                if (j3 == 0) {
                    for (int k = 0; k < list.size(); k++) {
                        Task task3 = list.get(k);
                        if (task3.getTeacherNum().equals(key) && task3.getQuestionScore() == 0.0d) {
                            total++;
                        }
                    }
                } else if ((0.5d * j3) + (gongchaVal * j3) < Float.valueOf(this.fullScore2).floatValue()) {
                    for (int k2 = 0; k2 < list.size(); k2++) {
                        Task task4 = list.get(k2);
                        if (task4.getTeacherNum().equals(key) && task4.getQuestionScore() <= (0.5d * j3) + (gongchaVal * j3) && task4.getQuestionScore() >= (0.5d * j3) + (gongchaVal * (j3 - 1))) {
                            total++;
                        }
                    }
                } else {
                    if ((0.5d * j3) + (gongchaVal * j3) < Float.valueOf(this.fullScore2).floatValue() || (0.5d * j3) + (gongchaVal * (j3 - 1)) >= Float.valueOf(this.fullScore2).floatValue()) {
                        break;
                    }
                    for (int k3 = 0; k3 < list.size(); k3++) {
                        Task task5 = list.get(k3);
                        if (task5.getTeacherNum().equals(key)) {
                            if (Float.valueOf(this.fullScore2).floatValue() - 0.5d != (0.5d * j3) + (gongchaVal * (j3 - 1))) {
                                if (task5.getQuestionScore() <= Float.valueOf(this.fullScore2).floatValue() - 0.5d && task5.getQuestionScore() >= (0.5d * j3) + (gongchaVal * (j3 - 1))) {
                                    total++;
                                }
                            } else if (task5.getQuestionScore() == (0.5d * j3) + (gongchaVal * (j3 - 1))) {
                                total++;
                            }
                        }
                    }
                }
                s2 = s2 + "<td>" + total + "</td>";
                num++;
                j3++;
            }
            int total2 = 0;
            for (int k4 = 0; k4 < list.size(); k4++) {
                Task task6 = list.get(k4);
                if (task6.getTeacherNum().equals(key) && task6.getQuestionScore() == Float.valueOf(this.fullScore2).floatValue()) {
                    total2++;
                }
            }
            str22 = str22 + ((s2 + "<td>" + total2 + "</td>") + "</tr>");
        }
        this.out.write(str4 + (str22 + "<tr><td align='left' colspan=" + num + "2><input type='button' value='对比显示' onclick='getTeacherScore()'/></td></tr>"));
    }

    public void getLineChar() {
        List<Task> list = esc.getAjaxTaskList(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.questionNum);
        float gongcha = Float.valueOf(this.fullScore2).floatValue() / 8.0f;
        float gongchaVal = 0.5f;
        int gongchazheng = (int) gongcha;
        float xiaoshu = gongcha - gongchazheng;
        if (xiaoshu > 0.5d) {
            gongchaVal = (float) Math.ceil(gongcha);
        } else if (xiaoshu <= 0.5d && xiaoshu > 0.0f) {
            gongchaVal = (float) (gongchazheng + 0.5d);
        } else if (xiaoshu == 0.0f) {
            gongchaVal = gongcha;
        }
        int k = 0;
        int j = 0;
        while (true) {
            if ((0.5d * j) + (gongchaVal * j) >= Float.valueOf(this.fullScore2).floatValue()) {
                if ((0.5d * j) + (gongchaVal * j) < Float.valueOf(this.fullScore2).floatValue() || (0.5d * j) + (gongchaVal * (j - 1)) >= Float.valueOf(this.fullScore2).floatValue()) {
                    break;
                } else if (Float.valueOf(this.fullScore2).floatValue() - 0.5d != (0.5d * j) + (gongchaVal * (j - 1))) {
                }
            }
            k++;
            j++;
        }
        String[] columnKeys = new String[k + 1];
        int k2 = 0;
        int j2 = 0;
        while (true) {
            if (j2 == 0) {
                columnKeys[k2] = "0";
            } else if ((0.5d * j2) + (gongchaVal * j2) < Float.valueOf(this.fullScore2).floatValue()) {
                columnKeys[k2] = ((0.5d * j2) + (gongchaVal * (j2 - 1))) + "-" + ((0.5d * j2) + (gongchaVal * j2));
            } else if ((0.5d * j2) + (gongchaVal * j2) < Float.valueOf(this.fullScore2).floatValue() || (0.5d * j2) + (gongchaVal * (j2 - 1)) >= Float.valueOf(this.fullScore2).floatValue()) {
                break;
            } else if (Float.valueOf(this.fullScore2).floatValue() - 0.5d != (0.5d * j2) + (gongchaVal * (j2 - 1))) {
                columnKeys[k2] = ((0.5d * j2) + (gongchaVal * (j2 - 1))) + "-" + (Float.valueOf(this.fullScore2).floatValue() - 0.5d);
            } else {
                columnKeys[k2] = ((0.5d * j2) + (gongchaVal * (j2 - 1))) + "";
            }
            k2++;
            j2++;
        }
        columnKeys[k2] = this.fullScore2;
        String s = this.teacs.get(0).toString();
        for (int j3 = 0; j3 < s.length(); j3++) {
            if (s.charAt(j3) == ',' && s.charAt(j3 + 1) == ',') {
                s = s.substring(0, j3) + s.substring(j3 + 1, s.length());
            }
            if (s.charAt(0) == ',') {
                s = s.substring(1);
            }
        }
        for (int j4 = 0; j4 < s.length(); j4++) {
            if (s.charAt(j4) == ',' && s.charAt(j4 + 1) == ',') {
                s = s.substring(0, j4) + ((Object) s.subSequence(j4 + 1, s.length()));
            }
        }
        String[] rowKeys = s.split(Const.STRING_SEPERATOR);
        double[][] data = new double[rowKeys.length][k2 + 1];
        Double.valueOf(0.0d);
        Double.valueOf(0.0d);
        List list_data = new ArrayList();
        if (null != list && list.size() > 0 && s.length() > 0) {
            for (int j22 = 0; j22 < rowKeys.length; j22++) {
                int a = 0;
                int fulltotal = 0;
                int all = 0;
                for (int j5 = 0; j5 < list.size(); j5++) {
                    if (list.get(j5).getTeacherNum().equals(rowKeys[j22])) {
                        all++;
                    }
                }
                int j32 = 0;
                while (true) {
                    int total = 0;
                    if (j32 == 0) {
                        for (int j6 = 0; j6 < list.size(); j6++) {
                            Task task = list.get(j6);
                            if (task.getTeacherNum().equals(rowKeys[j22]) && task.getQuestionScore() == 0.0d) {
                                total++;
                            }
                        }
                    } else if ((0.5d * j32) + (gongchaVal * j32) < Float.valueOf(this.fullScore2).floatValue()) {
                        for (int j7 = 0; j7 < list.size(); j7++) {
                            Task task2 = list.get(j7);
                            if (task2.getTeacherNum().equals(rowKeys[j22]) && task2.getQuestionScore() <= (0.5d * j32) + (gongchaVal * j32) && task2.getQuestionScore() >= (0.5d * j32) + (gongchaVal * (j32 - 1))) {
                                total++;
                            }
                        }
                    } else {
                        if ((0.5d * j32) + (gongchaVal * j32) < Float.valueOf(this.fullScore2).floatValue() || (0.5d * j32) + (gongchaVal * (j32 - 1)) >= Float.valueOf(this.fullScore2).floatValue()) {
                            break;
                        }
                        for (int j8 = 0; j8 < list.size(); j8++) {
                            Task task3 = list.get(j8);
                            if (task3.getTeacherNum().equals(rowKeys[j22])) {
                                if (Float.valueOf(this.fullScore2).floatValue() - 0.5d != (0.5d * j32) + (gongchaVal * (j32 - 1))) {
                                    if (task3.getQuestionScore() <= Float.valueOf(this.fullScore2).floatValue() - 0.5d && task3.getQuestionScore() >= (0.5d * j32) + (gongchaVal * (j32 - 1))) {
                                        total++;
                                    }
                                } else if (task3.getQuestionScore() == (0.5d * j32) + (gongchaVal * (j32 - 1))) {
                                    total++;
                                }
                            }
                        }
                    }
                    data[j22][a] = (total * 100) / all;
                    list_data.add(Integer.valueOf((total * 100) / all));
                    a++;
                    j32++;
                }
                for (int j9 = 0; j9 < list.size(); j9++) {
                    Task task4 = list.get(j9);
                    if (task4.getTeacherNum().equals(rowKeys[j22]) && task4.getQuestionScore() == Float.valueOf(this.fullScore2).floatValue()) {
                        fulltotal++;
                    }
                }
                data[j22][k2] = (fulltotal * 100) / all;
                list_data.add(Integer.valueOf((fulltotal * 100) / all));
            }
        }
        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.reset();
            response.setContentType("image/jpeg");
            JFreeChart chart = this.lc.getLineChar2(data, rowKeys, columnKeys, "#0.0", null, Integer.valueOf(rowKeys.length), "Y", "##.#");
            int chartHeight = (rowKeys.length / 5) * 26;
            ChartUtils.writeChartAsJPEG(response.getOutputStream(), chart, 1000, 450 + chartHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAvgScore() {
        List<Task> list = esc.getTeacherAcgScore(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.questionNum);
        String str = "";
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAvgScoreImage() {
        List<Task> list = esc.getTeacherAcgScore(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.questionNum);
        String[] rowKeys1 = {"教师平均分"};
        String[] columnKeys1 = new String[list.size()];
        double[][] data1 = new double[1][list.size()];
        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            if (task.getTeacherNum() != null && !task.getTeacherNum().equals("")) {
                columnKeys1[i] = task.getUpdateUser() + "(" + task.getTeacherNum() + ")";
            } else {
                columnKeys1[i] = task.getUpdateUser();
            }
            data1[0][i] = task.getQuestionScore();
        }
        int width = 600;
        if (columnKeys1.length > 10) {
            width = columnKeys1.length * 60;
        }
        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.reset();
            response.setContentType("image/jpeg");
            JFreeChart chart = this.lc.makeBarChart(data1, rowKeys1, columnKeys1);
            CategoryPlot plot = chart.getCategoryPlot();
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
            ChartUtils.writeChartAsJPEG(response.getOutputStream(), chart, width, 450);
        } catch (Exception e) {
        }
    }

    public void getLoad() {
        List<Task> list = esc.getAjaxTaskList2(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.questionNum);
        String str = "[]";
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getLoadImage() {
        List<Task> list = esc.getAjaxTaskList2(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.questionNum);
        String[] rowKeys1 = {"教师工作量"};
        String[] columnKeys1 = new String[list.size()];
        double[][] data1 = new double[1][list.size()];
        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            if (task.getTeacherNum() != null && !task.getTeacherNum().equals("")) {
                columnKeys1[i] = task.getTeacherName() + "(" + task.getTeacherNum() + ")";
            } else {
                columnKeys1[i] = task.getTeacherName();
            }
            data1[0][i] = (int) Float.parseFloat(task.getExt2());
        }
        int width = 600;
        if (columnKeys1.length > 7) {
            width = 600 + ((columnKeys1.length - 7) * 100);
        }
        if (width > 1200) {
            width = 1200;
        }
        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.reset();
            response.setContentType("image/jpeg");
            JFreeChart chart = this.lc.makeBarChart2(data1, rowKeys1, columnKeys1);
            CategoryPlot plot = chart.getCategoryPlot();
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
            ChartUtils.writeChartAsJPEG(response.getOutputStream(), chart, width, 450);
        } catch (Exception e) {
        }
    }

    public void getMarkError() {
        List<MarkError> list = esc.getMarkError(this.examNum, this.schoolNum, this.gradeNum, this.subjectNum);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getMarkErrorImage() {
        List<MarkError> list = esc.getMarkError(this.examNum, this.schoolNum, this.gradeNum, this.subjectNum);
        String[] rowKeys1 = {"教师判错量"};
        double[][] data1 = new double[1][list.size()];
        String[] columnKeys1 = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            MarkError m = list.get(i);
            for (int j = 0; j < columnKeys1.length; j++) {
                columnKeys1[i] = m.getExt1();
                data1[0][i] = m.getPage().intValue();
            }
        }
        int width = 600;
        if (columnKeys1.length > 7) {
            width = 600 + ((columnKeys1.length - 7) * 100);
        }
        if (width > 1200) {
            width = 1200;
        }
        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.reset();
            response.setContentType("image/jpeg");
            JFreeChart chart = this.lc.makeBarChart3(data1, rowKeys1, columnKeys1);
            ChartUtils.writeChartAsJPEG(response.getOutputStream(), chart, width, Const.height_500);
        } catch (Exception e) {
        }
    }

    public void getAjaxFullScoreList() {
        List<Task> list = esc.getAjaxFullScoreList(this.exampaperNum, this.questionNum);
        String str = "[]";
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getExamIsHistory() {
        try {
            String isHistory = analy.getExamIsHistory(this.exam);
            this.out.write(isHistory);
        } catch (Exception e) {
            e.printStackTrace();
            this.out.write(this.isHistory);
        }
    }

    public void getExamIsMoreSchool() {
        try {
            String isHistory = analy.getExamIsMoreSchool(this.exam);
            this.out.write(isHistory);
        } catch (Exception e) {
            e.printStackTrace();
            this.out.write(this.isHistory);
        }
    }

    public void getSfMsg() {
        RspMsg msg = sfHelper.verifyStudentOrParent(this.request);
        String str = JSON.toJSONString(msg);
        this.out.write(str);
    }

    public void isMoreSchool() {
        String isMoreSchool = analy.getExamIsMoreSchool(getParameter(Const.EXPORTREPORT_examNum));
        String str = JSON.toJSONString(isMoreSchool);
        this.out.write(str);
    }

    public void getExamByManageFile() {
        sfHelper.verifyStudentOrParent(this.request);
        String userId = ((User) this.session.get(Const.LOGIN_USER)).getId();
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_subjectNum, "fufen", "resourceType"};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        List<AjaxData> list = this.exportService.getExam(map, userId);
        String str = JSON.toJSONString(list);
        this.out.write(str);
    }

    public void getGradeByManageFile() {
        List<AjaxData> list;
        String userId = ((User) this.session.get(Const.LOGIN_USER)).getId();
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_subjectNum, "fufen", "resourceType"};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        HttpSession session = this.request.getSession(true);
        String gradePermission = String.valueOf(session.getAttribute(Const.teacher_permission_gra));
        String[] utype = analy.gettype(userId);
        String str = "[]";
        if (gradePermission.equals("0")) {
            if (null == utype || !ArrayUtil.contains(utype, "5")) {
                list = this.exportService.getGrade_F(map, userId);
            } else {
                list = this.exportService.getGrade(map, userId);
            }
            String viewAllReports = Configuration.getInstance().getViewAllReports();
            if ("1".equals(viewAllReports) && (null == list || list.size() == 0)) {
                list = this.exportService.getGrade(map, userId);
            }
        } else {
            list = this.exportService.getGrade(map, userId);
        }
        if (null != list) {
            str = JSON.toJSONString(list).toString();
        }
        this.out.write(str);
    }

    public void getSubjectByManageFile() {
        List<AjaxData> list;
        String userId = ((User) this.session.get(Const.LOGIN_USER)).getId();
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_subjectNum, "fufen", "resourceType", Const.EXPORTREPORT_studentType, "xuankezuhe"};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        String str = "[]";
        HttpSession session = this.request.getSession(true);
        String subjectPermission = String.valueOf(session.getAttribute(Const.teacher_permission_sub));
        if (subjectPermission.equals("0")) {
            String[] utype = analy.gettype(userId);
            if (null == utype || !ArrayUtil.contains(utype, "3") || !ArrayUtil.contains(utype, "5") || !ArrayUtil.contains(utype, "2")) {
                list = this.exportService.getSubject_F(map, userId);
            } else {
                list = this.exportService.getSubject(map, userId);
            }
            String viewAllReports = Configuration.getInstance().getViewAllReports();
            if ("1".equals(viewAllReports) && (null == list || list.size() == 0)) {
                list = this.exportService.getSubject(map, userId);
            }
        } else {
            list = this.exportService.getSubject(map, userId);
        }
        if (null != list) {
            str = JSON.toJSONString(list).toString();
        }
        this.out.write(str);
    }

    public void getFufenByManageFile() {
        String userId = ((User) this.session.get(Const.LOGIN_USER)).getId();
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_subjectNum, "fufen", "resourceType"};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        String str = "";
        List<AjaxData> list = this.exportService.getFufen(map, userId);
        if (null != list) {
            str = JSON.toJSONString(list).toString();
        }
        this.out.write(str);
    }

    public void getStudentTypeByManageFile() {
        ((User) this.session.get(Const.LOGIN_USER)).getId();
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_subjectNum, "fufen", "resourceType"};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        String str = "";
        List<AjaxData> list = this.exportService.getStudentType(map);
        if (null != list) {
            str = JSON.toJSONString(list).toString();
        }
        this.out.write(str);
    }

    public void getXuanKeZuHeByManageFile() {
        ((User) this.session.get(Const.LOGIN_USER)).getId();
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_subjectNum, "fufen", Const.EXPORTREPORT_studentType, "resourceType"};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        String str = "";
        List<AjaxData> list = this.exportService.getXuanKeZuHe(map);
        if (null != list) {
            str = JSON.toJSONString(list).toString();
        }
        this.out.write(str);
    }

    public void getSchoolByManageFile() {
        String userId = ((User) this.session.get(Const.LOGIN_USER)).getId();
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_subjectNum, "fufen", Const.EXPORTREPORT_studentType, "xuankezuhe", "resourceType"};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        map.put("userId", userId);
        String str = "";
        List<AjaxData> list = this.exportService.getSchool(map);
        if (null != list) {
            str = JSON.toJSONString(list).toString();
        }
        this.out.write(str);
    }

    public void getReportExam() {
        List<AjaxData> list;
        RspMsg msg = sfHelper.verifyStudentOrParent(this.request);
        String userId = ((User) this.session.get(Const.LOGIN_USER)).getId();
        if (msg.getCode() == 200) {
            String usertype = "1";
            String userNum = "";
            User user = new CommonUtil().getLoginUser(this.request);
            if (null != user) {
                usertype = user.getUsertype();
                userNum = user.getUserid().toString();
            }
            if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
                list = analy.getExam_Student(this.startTime, this.endTime, userNum);
            } else {
                list = analy.getExam(this.startTime, this.endTime, userId);
                if (!"-1".equals(userId) && !"-2".equals(userId)) {
                    List<Object> assignedSchoolList = system.getUserAssignedSchoolList(userId);
                    List<Map<String, Object>> examSchList = system.getExamSchList();
                    Map<String, List<Map<String, Object>>> assignedExamMap = (Map) examSchList.stream().filter(examSch -> {
                        return assignedSchoolList.contains(examSch.get(Const.EXPORTREPORT_schoolNum));
                    }).collect(Collectors.groupingBy(exam -> {
                        return Convert.toStr(exam.get(Const.EXPORTREPORT_examNum));
                    }));
                    list = (List) list.stream().filter(obj -> {
                        return assignedExamMap.containsKey(obj.getNum());
                    }).collect(Collectors.toList());
                }
            }
            msg.setData(list);
        }
        String str = JSON.toJSONString(msg);
        this.out.write(str);
    }

    public void getReportExam_compare() {
        List<AjaxData> list;
        String teachUnit_statistic = this.request.getParameter("teachUnit_statistic");
        String str = "[]";
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            list = analy.getCompareExam(this.exam, this.grade, this.subject, this.school, this.classNum, this.studentId, this.studentType, teachUnit_statistic);
        } else {
            list = analy.getHistoryCompareExam(this.startTime, this.endTime, this.exam, this.grade);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportSubject() {
        List<AjaxData> list;
        HttpSession session = this.request.getSession(true);
        String usertype = "1";
        String userNum = "";
        String uid = "";
        User user = new CommonUtil().getLoginUser(this.request);
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
            uid = String.valueOf(user.getId());
        }
        String str = "[]";
        String subjectPermission = String.valueOf(session.getAttribute(Const.teacher_permission_sub));
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
                list = analy.getSubject_Student(this.exam, this.type, userNum, null, this.subCompose);
            } else {
                if (subjectPermission.equals("0")) {
                    list = analy.getSubjectPer_F(this.exam, this.school, this.grade, this.type, uid, this.subCompose);
                } else {
                    list = analy.getSubject(this.exam, this.school, this.grade, this.type, this.showZSubject, "0", this.subCompose);
                    List<Map<String, Object>> loginTeaPositionList = (List) session.getAttribute("loginTeaPositionList");
                    if (CollUtil.isNotEmpty(loginTeaPositionList)) {
                        List<String> positionSubjectNumList = new ArrayList<>();
                        String isAllSubject = "0";
                        int i = 0;
                        int iLen = loginTeaPositionList.size();
                        while (true) {
                            if (i >= iLen) {
                                break;
                            }
                            Map<String, Object> onePositionMap = loginTeaPositionList.get(i);
                            String permission_grade = Convert.toStr(onePositionMap.get("permission_grade"), "0");
                            String permission_subject = Convert.toStr(onePositionMap.get("permission_subject"), "0");
                            String subjectNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_subjectNum));
                            String gradeNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_gradeNum));
                            if ("1".equals(permission_grade)) {
                                if ("1".equals(permission_subject)) {
                                    isAllSubject = "1";
                                    break;
                                } else {
                                    if (StrUtil.isNotEmpty(subjectNum)) {
                                        positionSubjectNumList.add(subjectNum);
                                    }
                                    i++;
                                }
                            } else {
                                if (null != this.grade && this.grade.equals(gradeNum)) {
                                    if ("1".equals(permission_subject)) {
                                        isAllSubject = "1";
                                        break;
                                    } else if (StrUtil.isNotEmpty(subjectNum)) {
                                        positionSubjectNumList.add(subjectNum);
                                    }
                                }
                                i++;
                            }
                        }
                        if ("0".equals(isAllSubject)) {
                            list = (List) list.stream().filter(m -> {
                                return positionSubjectNumList.contains(m.getNum());
                            }).collect(Collectors.toList());
                        }
                    }
                }
                String viewAllReports = Configuration.getInstance().getViewAllReports();
                if ("1".equals(viewAllReports) && (null == list || list.size() == 0)) {
                    list = analy.getSubject(this.exam, this.school, this.grade, this.type, this.showZSubject, "0", this.subCompose);
                }
            }
        } else if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
            list = analy.getHistorySubject_Student(this.exam, this.type, userNum, null);
        } else {
            if (!subjectPermission.equals("0")) {
                list = analy.getHistorySubject(this.exam, this.school, this.grade, this.type, this.showZSubject, null);
                List<Map<String, Object>> loginTeaPositionList2 = (List) session.getAttribute("loginTeaPositionList");
                if (CollUtil.isNotEmpty(loginTeaPositionList2)) {
                    List<String> positionSubjectNumList2 = new ArrayList<>();
                    String isAllSubject2 = "0";
                    int i2 = 0;
                    int iLen2 = loginTeaPositionList2.size();
                    while (true) {
                        if (i2 >= iLen2) {
                            break;
                        }
                        Map<String, Object> onePositionMap2 = loginTeaPositionList2.get(i2);
                        String permission_grade2 = Convert.toStr(onePositionMap2.get("permission_grade"), "0");
                        String permission_subject2 = Convert.toStr(onePositionMap2.get("permission_subject"), "0");
                        String subjectNum2 = Convert.toStr(onePositionMap2.get(Const.EXPORTREPORT_subjectNum));
                        String gradeNum2 = Convert.toStr(onePositionMap2.get(Const.EXPORTREPORT_gradeNum));
                        if ("1".equals(permission_grade2)) {
                            if ("1".equals(permission_subject2)) {
                                isAllSubject2 = "1";
                                break;
                            } else {
                                if (StrUtil.isNotEmpty(subjectNum2)) {
                                    positionSubjectNumList2.add(subjectNum2);
                                }
                                i2++;
                            }
                        } else {
                            if (null != this.grade && this.grade.equals(gradeNum2)) {
                                if ("1".equals(permission_subject2)) {
                                    isAllSubject2 = "1";
                                    break;
                                } else if (StrUtil.isNotEmpty(subjectNum2)) {
                                    positionSubjectNumList2.add(subjectNum2);
                                }
                            }
                            i2++;
                        }
                    }
                    if ("0".equals(isAllSubject2)) {
                        list = (List) list.stream().filter(m2 -> {
                            return positionSubjectNumList2.contains(m2.getNum());
                        }).collect(Collectors.toList());
                    }
                }
            } else {
                list = analy.getHistorySubjectPer_F(this.exam, this.school, this.grade, this.type, uid);
            }
            String viewAllReports2 = Configuration.getInstance().getViewAllReports();
            if ("1".equals(viewAllReports2) && (null == list || list.size() == 0)) {
                list = analy.getHistorySubject(this.exam, this.school, this.grade, this.type, this.showZSubject, null);
            }
        }
        if (null != list) {
            if (null != this.showHekeSubject && !"true".equals(this.showHekeSubject)) {
                List<String> zongheKemuList = esc.getZongheSubjectList(this.exam, this.grade);
                list = (List) list.stream().filter(m3 -> {
                    return !zongheKemuList.contains(m3.getNum());
                }).collect(Collectors.toList());
            }
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportSubjectfufen() {
        List<AjaxData> list;
        HttpSession session = this.request.getSession(true);
        String usertype = "1";
        String userNum = "";
        String uid = "";
        User user = new CommonUtil().getLoginUser(this.request);
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
            uid = String.valueOf(user.getId());
        }
        String str = "[]";
        String subjectPermission = String.valueOf(session.getAttribute(Const.teacher_permission_sub));
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
                list = analy.getSubject_Student1(this.exam, this.type, userNum, null, this.subCompose);
            } else {
                if (subjectPermission.equals("0")) {
                    list = analy.getSubjectPer_F1(this.exam, this.school, this.grade, this.type, uid, this.subCompose);
                } else {
                    list = analy.getSubject1(this.exam, this.school, this.grade, this.type, this.showZSubject, "0", this.subCompose);
                    List<Map<String, Object>> loginTeaPositionList = (List) session.getAttribute("loginTeaPositionList");
                    if (CollUtil.isNotEmpty(loginTeaPositionList)) {
                        List<String> positionSubjectNumList = new ArrayList<>();
                        String isAllSubject = "0";
                        int i = 0;
                        int iLen = loginTeaPositionList.size();
                        while (true) {
                            if (i >= iLen) {
                                break;
                            }
                            Map<String, Object> onePositionMap = loginTeaPositionList.get(i);
                            String permission_grade = Convert.toStr(onePositionMap.get("permission_grade"), "0");
                            String permission_subject = Convert.toStr(onePositionMap.get("permission_subject"), "0");
                            String subjectNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_subjectNum));
                            String gradeNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_gradeNum));
                            if ("1".equals(permission_grade)) {
                                if ("1".equals(permission_subject)) {
                                    isAllSubject = "1";
                                    break;
                                } else {
                                    if (StrUtil.isNotEmpty(subjectNum)) {
                                        positionSubjectNumList.add(subjectNum);
                                    }
                                    i++;
                                }
                            } else {
                                if (null != this.grade && this.grade.equals(gradeNum)) {
                                    if ("1".equals(permission_subject)) {
                                        isAllSubject = "1";
                                        break;
                                    } else if (StrUtil.isNotEmpty(subjectNum)) {
                                        positionSubjectNumList.add(subjectNum);
                                    }
                                }
                                i++;
                            }
                        }
                        if ("0".equals(isAllSubject)) {
                            list = (List) list.stream().filter(m -> {
                                return positionSubjectNumList.contains(m.getNum());
                            }).collect(Collectors.toList());
                        }
                    }
                }
                String viewAllReports = Configuration.getInstance().getViewAllReports();
                if ("1".equals(viewAllReports) && (null == list || list.size() == 0)) {
                    list = analy.getSubject1(this.exam, this.school, this.grade, this.type, this.showZSubject, "0", this.subCompose);
                }
            }
        } else if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
            list = analy.getHistorySubject_Student1(this.exam, this.type, userNum, null);
        } else {
            if (!subjectPermission.equals("0")) {
                list = analy.getHistorySubject1(this.exam, this.school, this.grade, this.type, this.showZSubject, null);
                List<Map<String, Object>> loginTeaPositionList2 = (List) session.getAttribute("loginTeaPositionList");
                if (CollUtil.isNotEmpty(loginTeaPositionList2)) {
                    List<String> positionSubjectNumList2 = new ArrayList<>();
                    String isAllSubject2 = "0";
                    int i2 = 0;
                    int iLen2 = loginTeaPositionList2.size();
                    while (true) {
                        if (i2 >= iLen2) {
                            break;
                        }
                        Map<String, Object> onePositionMap2 = loginTeaPositionList2.get(i2);
                        String permission_grade2 = Convert.toStr(onePositionMap2.get("permission_grade"), "0");
                        String permission_subject2 = Convert.toStr(onePositionMap2.get("permission_subject"), "0");
                        String subjectNum2 = Convert.toStr(onePositionMap2.get(Const.EXPORTREPORT_subjectNum));
                        String gradeNum2 = Convert.toStr(onePositionMap2.get(Const.EXPORTREPORT_gradeNum));
                        if ("1".equals(permission_grade2)) {
                            if ("1".equals(permission_subject2)) {
                                isAllSubject2 = "1";
                                break;
                            } else {
                                if (StrUtil.isNotEmpty(subjectNum2)) {
                                    positionSubjectNumList2.add(subjectNum2);
                                }
                                i2++;
                            }
                        } else {
                            if (null != this.grade && this.grade.equals(gradeNum2)) {
                                if ("1".equals(permission_subject2)) {
                                    isAllSubject2 = "1";
                                    break;
                                } else if (StrUtil.isNotEmpty(subjectNum2)) {
                                    positionSubjectNumList2.add(subjectNum2);
                                }
                            }
                            i2++;
                        }
                    }
                    if ("0".equals(isAllSubject2)) {
                        list = (List) list.stream().filter(m2 -> {
                            return positionSubjectNumList2.contains(m2.getNum());
                        }).collect(Collectors.toList());
                    }
                }
            } else {
                list = analy.getHistorySubjectPer_F1(this.exam, this.school, this.grade, this.type, uid);
            }
            String viewAllReports2 = Configuration.getInstance().getViewAllReports();
            if ("1".equals(viewAllReports2) && (null == list || list.size() == 0)) {
                list = analy.getHistorySubject1(this.exam, this.school, this.grade, this.type, this.showZSubject, null);
            }
        }
        if (null != list) {
            if (null != this.showHekeSubject && !"true".equals(this.showHekeSubject)) {
                List<String> zongheKemuList = esc.getZongheSubjectList(this.exam, this.grade);
                list = (List) list.stream().filter(m3 -> {
                    return !zongheKemuList.contains(m3.getNum());
                }).collect(Collectors.toList());
            }
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportIslevel() {
        List<AjaxData> list = analy.getIslevel(this.exam, this.grade);
        String str = "";
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportSchool() {
        List<AjaxData> list;
        String usertype = "1";
        String userNum = "";
        String uid = "";
        User user = new CommonUtil().getLoginUser(this.request);
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
            uid = String.valueOf(user.getId());
        }
        String str = "[]";
        Configuration.getInstance().getReportpermission();
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
                list = analy.getSchool_Student(this.exam, this.subject, this.type, userNum);
            } else {
                list = analy.getSchool2(this.exam, this.subject, this.type, this.grade, uid, this.subCompose);
            }
        } else if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
            list = analy.getHistorySchool_Student(this.exam, this.subject, this.type, userNum);
        } else {
            list = analy.getHistorySchool2(this.exam, this.subject, this.type, uid);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getAllReportSchool() {
        List<AjaxData> list;
        String usertype = "1";
        String userNum = "";
        User user = new CommonUtil().getLoginUser(this.request);
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
        }
        String str = "[]";
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
                list = analy.getSchool_Student(this.exam, this.subject, this.type, userNum);
            } else {
                list = analy.getSchool2(this.exam, this.subject, this.type, this.grade, "-1", this.subCompose);
            }
        } else if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
            list = analy.getHistorySchool_Student(this.exam, this.subject, this.type, userNum);
        } else {
            list = analy.getHistorySchool2(this.exam, this.subject, this.type, "-1");
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportArea() {
        String uid = "";
        User user = new CommonUtil().getLoginUser(this.request);
        if (null != user) {
            user.getUsertype();
            user.getUserid().toString();
            uid = String.valueOf(user.getId());
        }
        String str = "[]";
        List<AjaxData> list = analy.getArea(this.exam, uid);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getG1ReportArea() {
        String str = "[]";
        List<AjaxData> list = analy.getArea(this.exam, "-1");
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportGrade() {
        List<AjaxData> list;
        String userId = ((User) this.session.get(Const.LOGIN_USER)).getId();
        String usertype = "1";
        String userNum = "";
        String uid = "";
        User user = new CommonUtil().getLoginUser(this.request);
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
            uid = String.valueOf(user.getId());
        }
        HttpSession session = this.request.getSession(true);
        String gradePermission = String.valueOf(session.getAttribute(Const.teacher_permission_gra));
        String str = "[]";
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
                list = analy.getGrade_Student(this.exam, this.subject, this.school, this.type, userNum);
            } else if (gradePermission.equals("0")) {
                list = analy.getGradePer_F(this.exam, this.subject, this.school, this.type, uid);
                String viewAllReports = Configuration.getInstance().getViewAllReports();
                if ("1".equals(viewAllReports) && (null == list || list.size() == 0)) {
                    list = analy.getGrade(this.exam, this.subject, this.school, this.type, userId);
                }
            } else {
                list = analy.getGrade(this.exam, this.subject, this.school, this.type, userId);
            }
        } else if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
            list = analy.getHistoryGrade_Student(this.exam, this.subject, this.school, this.type, userNum);
        } else if (gradePermission.equals("0")) {
            list = analy.getHistoryGradePer_F(this.exam, this.subject, this.school, this.type, uid);
            String viewAllReports2 = Configuration.getInstance().getViewAllReports();
            if ("1".equals(viewAllReports2) && (null == list || list.size() == 0)) {
                list = analy.getHistoryGrade(this.exam, this.subject, this.school, this.type, userId);
            }
        } else {
            list = analy.getHistoryGrade(this.exam, this.subject, this.school, this.type, userId);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getexportData() {
        String examNum = this.request.getParameter(Const.EXPORTREPORT_examNum);
        String userId = new CommonUtil().getLoginUserNum(this.request);
        List list = analy.getexportData(examNum, userId);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getuserinfo() {
        String uuser;
        String userNum = this.request.getParameter("userId");
        if (userNum.equals("-2")) {
            uuser = "-2-dmj-达美嘉";
        } else {
            User u1 = this.userService.getUserById(userNum);
            uuser = u1.getId() + "-" + u1.getUsername() + "-" + u1.getRealname();
        }
        this.out.write(JSON.toJSONString(uuser));
    }

    public void getReportSubjectType() {
        List<AjaxData> list;
        String usertype = "1";
        String userNum = "";
        String uid = "";
        User user = new CommonUtil().getLoginUser(this.request);
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
            uid = String.valueOf(user.getId());
        }
        String str = "[]";
        HttpSession session = this.request.getSession(true);
        String gradePermission = String.valueOf(session.getAttribute(Const.teacher_permission_gra));
        String classPermission = String.valueOf(session.getAttribute(Const.teacher_permission_cla));
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
                list = analy.getSubjectType_Student(this.exam, this.subject, this.school, userNum);
            } else if (gradePermission.equals("0") && "0".equals(classPermission) && "F".equals(this.level)) {
                String[] utype = analy.gettype(uid);
                if (null == utype || !ArrayUtil.contains(utype, "3") || !ArrayUtil.contains(utype, "5") || !ArrayUtil.contains(utype, "4")) {
                    list = analy.getSubjectTypePer_F(this.exam, this.subject, this.school, this.grade, uid);
                } else {
                    list = analy.getSubjectType(this.exam, this.subject, this.school, this.grade);
                }
                String viewAllReports = Configuration.getInstance().getViewAllReports();
                if ("1".equals(viewAllReports) && (null == list || list.size() == 0)) {
                    list = analy.getSubjectType(this.exam, this.subject, this.school, this.grade);
                }
            } else {
                list = analy.getSubjectType(this.exam, this.subject, this.school, this.grade);
            }
        } else if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
            list = analy.getHistorySubjectType_Student(this.exam, this.subject, this.school, this.grade, userNum);
        } else if (gradePermission.equals("0") && "0".equals(classPermission)) {
            String[] utype2 = analy.gettype(uid);
            if (null == utype2 || !ArrayUtil.contains(utype2, "3") || !ArrayUtil.contains(utype2, "5") || !ArrayUtil.contains(utype2, "4") || !ArrayUtil.contains(utype2, "2")) {
                list = analy.getHistorySubjectTypePer_F(this.exam, this.subject, this.school, this.grade, uid);
            } else {
                list = analy.getHistorySubjectType(this.exam, this.subject, this.school, this.grade);
            }
            String viewAllReports2 = Configuration.getInstance().getViewAllReports();
            if ("1".equals(viewAllReports2) && (null == list || list.size() == 0)) {
                list = analy.getHistorySubjectType(this.exam, this.subject, this.school, this.grade);
            }
        } else {
            list = analy.getHistorySubjectType(this.exam, this.subject, this.school, this.grade);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportSubCompose() {
        String onlyShowShouxuan = this.request.getParameter("onlyShowShouxuan");
        List<AjaxData> list = analy.getSubCompose(this.exam, this.grade, this.subjectType, onlyShowShouxuan);
        String str = "";
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportClass() {
        List<AjaxData> list;
        String usertype = "1";
        String userNum = "";
        String uid = "";
        User user = new CommonUtil().getLoginUser(this.request);
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
            uid = String.valueOf(user.getId());
        }
        String str = "[]";
        HttpSession session = this.request.getSession(true);
        String classPermission = String.valueOf(session.getAttribute(Const.teacher_permission_cla));
        String levelclass = system.getIsLevelClass(this.exam, this.grade, this.subject, null);
        if (null == levelclass || levelclass.equals("")) {
            levelclass = "F";
        }
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
                list = analy.getClass_Student(this.exam, this.subject, this.school, this.grade, this.type, userNum, levelclass, this.islevel, this.subCompose);
            } else {
                list = analy.getClass(this.exam, this.subject, this.school, this.grade, this.type, levelclass, this.islevel, this.subCompose);
                List<Map<String, Object>> loginTeaPositionList = (List) session.getAttribute("loginTeaPositionList");
                if (CollUtil.isNotEmpty(loginTeaPositionList)) {
                    List<String> positionClassNumList = new ArrayList<>();
                    String isAllClass = "0";
                    int i = 0;
                    int iLen = loginTeaPositionList.size();
                    while (true) {
                        if (i >= iLen) {
                            break;
                        }
                        Map<String, Object> onePositionMap = loginTeaPositionList.get(i);
                        String permission_grade = Convert.toStr(onePositionMap.get("permission_grade"), "0");
                        String permission_subject = Convert.toStr(onePositionMap.get("permission_subject"), "0");
                        String permission_class = Convert.toStr(onePositionMap.get("permission_class"), "0");
                        String subjectNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_subjectNum));
                        String gradeNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_gradeNum));
                        String classNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_classNum));
                        if ("1".equals(permission_grade)) {
                            if ("1".equals(permission_subject)) {
                                if ("1".equals(permission_class)) {
                                    isAllClass = "1";
                                    break;
                                } else {
                                    if (StrUtil.isNotEmpty(classNum)) {
                                        positionClassNumList.add(classNum);
                                    }
                                    i++;
                                }
                            } else {
                                if (null != this.subject && this.subject.equals(subjectNum)) {
                                    if ("1".equals(permission_class)) {
                                        isAllClass = "1";
                                        break;
                                    } else if (StrUtil.isNotEmpty(classNum)) {
                                        positionClassNumList.add(classNum);
                                    }
                                }
                                i++;
                            }
                        } else {
                            if (null != this.grade && this.grade.equals(gradeNum)) {
                                if ("1".equals(permission_subject)) {
                                    if ("1".equals(permission_class)) {
                                        isAllClass = "1";
                                        break;
                                    } else if (StrUtil.isNotEmpty(classNum)) {
                                        positionClassNumList.add(classNum);
                                    }
                                } else if (null != this.subject && this.subject.equals(subjectNum)) {
                                    if ("1".equals(permission_class)) {
                                        isAllClass = "1";
                                        break;
                                    } else if (StrUtil.isNotEmpty(classNum)) {
                                        positionClassNumList.add(classNum);
                                    }
                                }
                            }
                            i++;
                        }
                    }
                    if ("0".equals(isAllClass)) {
                        list = (List) list.stream().filter(m -> {
                            return positionClassNumList.contains(m.getNum());
                        }).collect(Collectors.toList());
                    }
                }
                String viewAllReports = Configuration.getInstance().getViewAllReports();
                if ("1".equals(viewAllReports) && (null == list || list.size() == 0)) {
                    list = analy.getClass(this.exam, this.subject, this.school, this.grade, this.type, levelclass, this.islevel, this.subCompose);
                }
            }
        } else if (usertype != null && !usertype.equals("") && usertype.equals("2")) {
            list = analy.getHistoryClass_Student(this.exam, this.subject, this.school, this.grade, this.type, userNum);
        } else {
            if (classPermission.equals("0")) {
                list = analy.getHistoryClassPer_F(this.exam, this.subject, this.school, this.grade, this.type, uid, levelclass);
            } else {
                list = analy.getHistoryClass(this.exam, this.subject, this.school, this.grade, this.type, levelclass);
            }
            String viewAllReports2 = Configuration.getInstance().getViewAllReports();
            if ("1".equals(viewAllReports2) && (null == list || list.size() == 0)) {
                list = analy.getHistoryClass(this.exam, this.subject, this.school, this.grade, this.type, levelclass);
            }
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportStudent() {
        List<AjaxData> list;
        String usertype = "1";
        String userNum = "";
        User user = new CommonUtil().getLoginUser(this.request);
        if (null != user) {
            usertype = user.getUsertype();
            userNum = user.getUserid().toString();
        }
        String levelclass = system.getIsLevelClass(this.exam, this.grade, this.subject, null);
        if (null == levelclass || levelclass.equals("")) {
            levelclass = "F";
        }
        String str = "[]";
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
                list = analy.getStudent_Student(this.exam, this.subject, this.school, this.grade, this.cla, userNum);
            } else {
                list = analy.getStudent(this.exam, this.subject, this.school, this.grade, this.cla, levelclass, this.islevel, this.subCompose);
            }
        } else if (usertype != null && !usertype.equals("") && (usertype.equals("2") || usertype.equals("3"))) {
            list = analy.getHistoryStudent_Student(this.exam, this.subject, this.school, this.grade, this.cla, userNum);
        } else {
            list = analy.getHistoryStudent(this.exam, this.subject, this.school, this.grade, this.cla);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportGraduationType() {
        List<AjaxData> list;
        String str = "[]";
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            list = analy.getGraduationType(this.exam, this.subject, this.school, this.grade, this.subjectType, this.classNum, this.studentId, this.subCompose);
        } else {
            list = analy.getHistoryGraduationType(this.exam, this.subject, this.school, this.grade, this.subjectType, this.classNum, this.studentId);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getReportStuSourceType() {
        List<AjaxData> list;
        String str = "[]";
        if (this.isHistory == null || this.isHistory.equals("") || this.isHistory.equals("F")) {
            list = analy.getStuSourceType(this.exam, this.subject, this.school, this.grade, this.subjectType, this.classNum, this.studentId, this.statisticType, this.subCompose);
        } else {
            list = analy.getHistoryStuSourceType(this.exam, this.subject, this.school, this.grade, this.subjectType, this.classNum, this.studentId, this.statisticType);
        }
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getIsLevelClass() {
        if (null == this.subject || this.subject.equals("")) {
            this.subject = this.subjectNum;
        }
        if (null == this.exam || this.exam.equals("")) {
            this.exam = this.examNum;
        }
        if (null == this.grade || this.grade.equals("")) {
            this.grade = this.gradeNum;
        }
        String levelCla = system.getIsLevelClass(this.exam, this.grade, this.subject, null);
        if (null == levelCla || levelCla.equals("")) {
            levelCla = "F";
        }
        this.out.write(levelCla);
    }

    public void getIsUpgrade() {
        if (null == this.exam || this.exam.equals("")) {
            this.exam = this.examNum;
        }
        if (null == this.grade || this.grade.equals("")) {
            this.grade = this.gradeNum;
        }
        String upgrade = system.getIsUpgrade(this.exam, this.grade, null, this.isHistory);
        if (null == upgrade || upgrade.equals("")) {
            upgrade = "F";
        }
        this.out.write(upgrade);
    }

    public void getQuesGroup() {
        String str = "";
        List<Task> list = esc.getQuesGroup(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.groupnum);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void resetQues() {
        String uid = new CommonUtil().getLoginUserNum(this.request);
        this.questionGroupService.recoverytask(this.exampaperNum, this.groupnum, this.questionNum, this.userNum);
        this.m.UpdateQuestionGroupTotalCount(this.exampaperNum);
        Examlog examlog = new Examlog();
        examlog.setOperate("题组进度-重置阅卷员题目的判分：loginuser" + uid + ",阅卷员:" + this.userNum + ",题目:" + this.questionNum);
        examlog.setExampaperNum(Integer.valueOf(this.exampaperNum));
        examlog.setInsertUser(uid);
        examlog.setInsertDate(DateUtil.getCurrentTime());
        esc.save(examlog);
        this.out.write("[]");
    }

    public void resetPanfenByTi() {
        String uid = new CommonUtil().getLoginUserNum(this.request);
        this.questionGroupService.resetPanfenByTi(this.exampaperNum, this.groupnum);
        this.m.UpdateQuestionGroupTotalCount(this.exampaperNum);
        Examlog examlog = new Examlog();
        examlog.setOperate("题组进度-重置题组的判分：loginuser" + uid + ",题组:" + this.groupnum);
        examlog.setExampaperNum(Integer.valueOf(this.exampaperNum));
        examlog.setInsertUser(uid);
        examlog.setInsertDate(DateUtil.getCurrentTime());
        esc.save(examlog);
        this.out.write("[]");
    }

    public void authRoleMethod() {
        String str = "[]";
        String uid = new CommonUtil().getLoginUserNum(this.request);
        List list = report.authRole(this.exampaperNum, null, uid);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void resetAllGroups() {
        String uid = new CommonUtil().getLoginUserNum(this.request);
        String[] groupNumarry = this.groupnum.split("_");
        for (String str : groupNumarry) {
            this.questionGroupService.recoverytask(this.exampaperNum, str, this.questionNum, this.userNum);
        }
        Examlog examlog = new Examlog();
        examlog.setOperate("题组进度-重置所有题组的判分：，loginuser" + uid);
        examlog.setExampaperNum(Integer.valueOf(this.exampaperNum));
        examlog.setInsertUser(uid);
        examlog.setInsertDate(DateUtil.getCurrentTime());
        esc.save(examlog);
        this.out.write("[]");
    }

    public void resetWorkRecord() {
        String uid = new CommonUtil().getLoginUserNum(this.request);
        String[] groupNumarry = this.groupnum.split("_");
        for (String str : groupNumarry) {
            this.awardPointService.resettingWorkrecord(this.exampaperNum, str);
        }
        Examlog examlog = new Examlog();
        examlog.setOperate("题组进度-更新题组的工作量：，loginuser" + uid + "，题组：" + this.groupnum);
        examlog.setExampaperNum(Integer.valueOf(this.exampaperNum));
        examlog.setInsertUser(uid);
        examlog.setInsertDate(DateUtil.getCurrentTime());
        esc.save(examlog);
        this.out.write("[]");
    }

    public void resetAllWorkRecord() {
        String uid = new CommonUtil().getLoginUserNum(this.request);
        String[] groupNumarry = this.groupnum.split("_");
        for (String str : groupNumarry) {
            this.awardPointService.resettingWorkrecord(this.exampaperNum, str);
        }
        Examlog examlog = new Examlog();
        examlog.setOperate("题组进度-更新所有题组的工作量：，loginuser" + uid);
        examlog.setExampaperNum(Integer.valueOf(this.exampaperNum));
        examlog.setInsertUser(uid);
        examlog.setInsertDate(DateUtil.getCurrentTime());
        esc.save(examlog);
        this.out.write("[]");
    }

    public void getGroupInfo() {
        String str = esc.getGroupInfo(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.groupnum);
        if (null != str) {
            str = JSON.toJSONString(str);
        }
        this.out.write(str);
    }

    public void qscoreta() {
        String str = "";
        List list = new ArrayList();
        List<Task> list1 = esc.panfendetail(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.groupnum);
        String groupNum2 = this.request.getParameter("groupNum2");
        List<Task> list2 = esc.qscoreta2(Integer.valueOf(Integer.parseInt(this.exampaperNum)), groupNum2);
        String weicaiqie = esc.getweicaiqieNum1(this.exampaperNum);
        list.add(list1);
        list.add(list2);
        list.add(weicaiqie);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void qscoreta_new() {
        ServletContext context = this.request.getSession().getServletContext();
        String examPaperNum = getParameter("examPaperNum");
        String groupNum = getParameter("groupNum");
        String orderName = getParameter("orderName");
        List<Map<String, Object>> teaWorkloadResDataList = new ArrayList<>();
        Map<String, Map<String, Object>> yuejuanProgressMap = (Map) context.getAttribute(Const.yuejuanProgressMap);
        if (CollUtil.isNotEmpty(yuejuanProgressMap)) {
            Map<String, Object> oneSubjectResDataMap = yuejuanProgressMap.get(examPaperNum);
            if (CollUtil.isNotEmpty(oneSubjectResDataMap)) {
                Map<String, List<Map<String, Object>>> teaWorkloadResDataMap = (Map) oneSubjectResDataMap.get("teaWorkloadProgress");
                teaWorkloadResDataList = teaWorkloadResDataMap.get(groupNum);
                if (CollUtil.isNotEmpty(teaWorkloadResDataList)) {
                    String isFenzu = system.fenzu(examPaperNum);
                    if ("weipanDetail".equals(orderName)) {
                        Comparator<Map<String, Object>> c1 = ChineseCharacterUtil.sortByPinyinOfMap("schoolGroupName");
                        Comparator<Map<String, Object>> c2 = ChineseCharacterUtil.sortByPinyinOfMap("schoolName");
                        Comparator<Map<String, Object>> c3 = Comparator.comparing(m -> {
                            return Convert.toInt(m.get("weipanCount_paixu"), 0);
                        });
                        teaWorkloadResDataList = "1".equals(isFenzu) ? (List) teaWorkloadResDataList.stream().sorted(c1.thenComparing(c2).thenComparing(c3.reversed())).collect(Collectors.toList()) : (List) teaWorkloadResDataList.stream().sorted(c2.thenComparing(c3.reversed())).collect(Collectors.toList());
                    } else if ("tizuDetail".equals(orderName)) {
                        Comparator<Map<String, Object>> c12 = ChineseCharacterUtil.sortByPinyinOfMap("schoolGroupName");
                        Comparator<Map<String, Object>> c22 = ChineseCharacterUtil.sortByPinyinOfMap("schoolName");
                        Comparator<Map<String, Object>> c32 = Comparator.comparing(m2 -> {
                            return Convert.toInt(m2.get("yipanCount"), 0);
                        });
                        teaWorkloadResDataList = "1".equals(isFenzu) ? (List) teaWorkloadResDataList.stream().sorted(c12.thenComparing(c22).thenComparing(c32.reversed())).collect(Collectors.toList()) : (List) teaWorkloadResDataList.stream().sorted(c22.thenComparing(c32.reversed())).collect(Collectors.toList());
                    }
                }
            }
        }
        this.out.write(JSON.toJSONString(teaWorkloadResDataList));
    }

    public void qscoreta_new2() {
        String[] groupNums = this.request.getParameter("groupNum").split(Const.STRING_SEPERATOR);
        List list = new ArrayList();
        for (String groupNum : groupNums) {
            List<Task> list2 = esc.qscoreta2(Integer.valueOf(Integer.parseInt(this.examPaperNum)), groupNum);
            list.add(list2);
        }
        this.out.write(JSON.toJSONString(list));
    }

    public void clearUserQues() {
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        int aa = esc.clearUserQues(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.groupnum, this.userNum, this.questionNum, loginUserId, this.rwCount).intValue();
        this.out.write(aa + "");
    }

    public void getAjaxQuestionList() {
        String examPaperNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
        if (null == examPaperNum || "".equals(examPaperNum)) {
            examPaperNum = "";
        }
        this.out.write(examPaperNum);
    }

    public void AjaxQuestionNumList() {
        if (null == this.exampaperNum || "".equals(this.exampaperNum)) {
            this.exampaperNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
        }
        String str = "[]";
        List<Task> list = esc.getAjaxQuestionNumList(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.groupnum);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void ajaxQuestionList() {
        if (null == this.exampaperNum || "".equals(this.exampaperNum)) {
            this.exampaperNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
        }
        String str = "[]";
        List<QuestionGroup> list = esc.ajaxQuestionList(this.exampaperNum);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void AjaxQuestionNumList2() {
        String str = "[]";
        List<Task> list = esc.getAjaxQuestionNumList2(Integer.valueOf(Integer.parseInt(this.exampaperNum)), this.groupnum);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void AjaxQuestionNum() {
        String gradeExamNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
        String str = "[]";
        List<Define> list = esc.getAjaxQuestionNum(gradeExamNum);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getqNumList() {
        String examPaperNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
        String str = "[]";
        List<Define> list = esc.getqNumList(examPaperNum, "0");
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void deleteExamineeNumOrNot() {
        String classNum = this.request.getParameter(Const.EXPORTREPORT_classNum);
        String oldClassNum = this.request.getParameter("oldClassNum");
        String c = esc.deleteExamineeNumOrNot(classNum, oldClassNum);
        this.out.write(c);
    }

    public void getStuExam() {
        String str = "";
        List<AjaxData> list = esc.getStuExam(this.studentId);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void editExamineeNum() {
        int res = 0;
        String[] examNums = this.examNum.split(Const.STRING_SEPERATOR);
        String flagStr = this.request.getParameter("flagStr");
        if (flagStr.equals("1")) {
            for (String obj : examNums) {
                res = esc.deleteExamineeNum(this.studentId, obj);
            }
        } else {
            try {
                for (String obj2 : examNums) {
                    res = esc.editExamineeNum(obj2, this.studentId, this.schoolNum, this.classNum);
                }
            } catch (Exception e) {
                this.log.info("编辑学生信息同步考试异常：[studentId=" + this.studentId + ",schoolNum=" + this.schoolNum + ",classNum=" + this.classNum + "]" + e.getMessage());
            }
        }
        this.out.write(res);
    }

    public void isMerge() {
        String merge = esc.isMerge(this.exampaperNum, this.questionNum);
        this.out.write(merge);
    }

    public void verifyCharge() {
        RspMsg msg = sfHelper.verifyStudentOrParent(this.request);
        this.out.write(JSON.toJSONString(msg));
    }

    public String toCommonJsp() {
        String realUrl = "";
        try {
            realUrl = this.request.getParameter("realUrl");
            URLDecoder.decode(realUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.request.setAttribute("realUrl", realUrl.replace("../", ""));
        return "reportCommonJsp";
    }

    public void getStatistic() {
        String fenshuduan1 = this.request.getParameter("fenshuduan1");
        String fenshuduan2 = this.request.getParameter("fenshuduan2");
        String groupType = this.request.getParameter("groupType");
        String qNum = this.request.getParameter("qNum");
        String panfenType = this.request.getParameter("panfenType");
        List<Task> list = null;
        if ("-1".equals(groupType)) {
            list = esc.getAllStatistic(this.exam, this.subject, this.grade, qNum, this.updateUser, fenshuduan1, fenshuduan2, panfenType);
        } else if ("1".equals(groupType)) {
            list = esc.getStatistic(this.exam, this.subject, this.grade, this.questionNum, this.updateUser, fenshuduan1, fenshuduan2, panfenType);
        } else if ("2".equals(groupType)) {
            list = esc.getHbcqGStatistic(this.exam, this.subject, this.grade, this.questionNum, this.updateUser, fenshuduan1, fenshuduan2, panfenType);
        }
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getSpotCheckSChild() {
        String fenshuduan1 = this.request.getParameter("fenshuduan1");
        String fenshuduan2 = this.request.getParameter("fenshuduan2");
        String panfenType = this.request.getParameter("panfenType");
        List list = esc.getSpotCheckSChild(this.groupnum, this.updateUser, fenshuduan1, fenshuduan2, this.snumber, panfenType);
        this.out.write(JSON.toJSONString(list));
    }

    public void getHbcqGSpotCheckSChild() {
        String fenshuduan1 = this.request.getParameter("fenshuduan1");
        String fenshuduan2 = this.request.getParameter("fenshuduan2");
        String panfenType = this.request.getParameter("panfenType");
        List list = esc.getHbcqGSpotCheckSChild(this.groupnum, this.updateUser, fenshuduan1, fenshuduan2, this.snumber, panfenType);
        this.out.write(JSON.toJSONString(list));
    }

    public String toChoucha() {
        String fenshuduan1 = this.request.getParameter("fenshuduan1");
        String fenshuduan2 = this.request.getParameter("fenshuduan2");
        String groupType = this.request.getParameter("groupType");
        String panfenType = this.request.getParameter("panfenType");
        String trueGroupNum = this.request.getParameter("trueGroupNum");
        this.request.setAttribute("fenshuduan1", fenshuduan1);
        this.request.setAttribute("fenshuduan2", fenshuduan2);
        this.request.setAttribute("groupType", groupType);
        this.request.setAttribute("panfenType", panfenType);
        this.request.setAttribute("trueGroupNum", trueGroupNum);
        return "choucha";
    }

    public String toChecked() {
        String fenshuduan1 = this.request.getParameter("fenshuduan1");
        String fenshuduan2 = this.request.getParameter("fenshuduan2");
        String groupType = this.request.getParameter("groupType");
        String panfenType = this.request.getParameter("panfenType");
        String trueGroupNum = this.request.getParameter("trueGroupNum");
        this.request.setAttribute("fenshuduan1", fenshuduan1);
        this.request.setAttribute("fenshuduan2", fenshuduan2);
        this.request.setAttribute("groupType", groupType);
        this.request.setAttribute("panfenType", panfenType);
        this.request.setAttribute("trueGroupNum", trueGroupNum);
        return "checked";
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v25, types: [java.util.List] */
    public void getSpotCheckSChecked() {
        String fenshuduan1 = this.request.getParameter("fenshuduan1");
        String fenshuduan2 = this.request.getParameter("fenshuduan2");
        String panfenType = this.request.getParameter("panfenType");
        List data = new ArrayList();
        this.count = esc.getSpotCheckSCheckedCount(this.groupnum, this.updateUser, fenshuduan1, fenshuduan2, panfenType);
        data.add(Integer.valueOf(this.count));
        data.add(Integer.valueOf(this.currPage));
        data.add(Integer.valueOf(this.pageSize));
        ArrayList arrayList = new ArrayList();
        if (this.count > 0) {
            arrayList = esc.getSpotCheckSChecked(this.groupnum, this.updateUser, fenshuduan1, fenshuduan2, (this.currPage - 1) * this.pageSize, this.pageSize, panfenType);
        }
        data.add(arrayList);
        this.out.write(JSON.toJSONString(data));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v25, types: [java.util.List] */
    public void getHbcqGSpotCheckSChecked() {
        String fenshuduan1 = this.request.getParameter("fenshuduan1");
        String fenshuduan2 = this.request.getParameter("fenshuduan2");
        String panfenType = this.request.getParameter("panfenType");
        List data = new ArrayList();
        this.count = esc.getHbcqGSpotCheckSCheckedCount(this.groupnum, this.updateUser, fenshuduan1, fenshuduan2, panfenType);
        data.add(Integer.valueOf(this.count));
        data.add(Integer.valueOf(this.currPage));
        data.add(Integer.valueOf(this.pageSize));
        ArrayList arrayList = new ArrayList();
        if (this.count > 0) {
            arrayList = esc.getHbcqGSpotCheckSChecked(this.groupnum, this.updateUser, fenshuduan1, fenshuduan2, (this.currPage - 1) * this.pageSize, this.pageSize, panfenType);
        }
        data.add(arrayList);
        this.out.write(JSON.toJSONString(data));
    }

    public void addExamineeRecordLeq() {
        RspMsg msg;
        String[] scoreIdArr = this.request.getParameterValues("scoreIdArr")[0].split("@");
        String userId = new CommonUtil().getLoginUserNum(this.request);
        new RspMsg();
        for (String str : scoreIdArr) {
            try {
                ExamineeStuRecord examineeStuRecord = new ExamineeStuRecord();
                examineeStuRecord.setScoreId(str);
                examineeStuRecord.setUserId(userId);
                examineeStuRecord.setInsertDate(DateUtil.getCurrentTime());
                examineeStuRecord.setStatus("T");
                esc.addExamineeRecord(examineeStuRecord);
            } catch (Exception e) {
                e.printStackTrace();
                msg = new RspMsg(Const.height_500, "标记审核失败", null);
            }
        }
        msg = new RspMsg(200, "标记审核成功", null);
        this.out.write(JSON.toJSONString(msg));
    }

    public void directUpdateScoreLeq() {
        RspMsg msg;
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        String time = DateUtil.getCurrentTime();
        String isShenhe = this.request.getParameter("isShenhe");
        String[] scoreArr = this.request.getParameterValues("scoreArr");
        String[] scoreArr2 = scoreArr[0].split("@");
        String xxtFlag = this.request.getParameter("xxtFlag");
        new RspMsg();
        try {
            esc.directUpdateScoreLeq(scoreArr2, this.exampaperNum, loginUserId, time, isShenhe, xxtFlag);
            msg = new RspMsg(200, "修改分数成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            msg = new RspMsg(Const.height_500, "修改分数失败", null);
        }
        this.out.write(JSON.toJSONString(msg));
    }

    public void getAllSchoolCounts() {
        String exam = this.request.getParameter("exam");
        String grade = this.request.getParameter("grade");
        String userId = new CommonUtil().getLoginUserNum(this.request);
        String flag = analy.getAllSchoolCounts(exam, grade, userId);
        this.out.write(flag);
    }

    public void getAllQuestionNumList() {
        if (null == this.exampaperNum || "".equals(this.exampaperNum)) {
            this.exampaperNum = esc.getExampaperNumBySubjectAndGradeAndExam(this.exam, this.subject, this.grade);
        }
        String str = "[]";
        List<Task> list = esc.getAllQuestionNumList(this.exampaperNum);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getGradeBymoreExam() {
        String userId = String.valueOf(((User) this.session.get(Const.LOGIN_USER)).getId());
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        try {
            List<Grade> list = this.ajaxQueryService.getGradeBymoreExam(map, userId);
            JSONArray json = JSONArray.fromObject(list);
            this.out.write(json.toString());
        } catch (Exception e) {
        }
    }

    public void getSubjectByGrade() {
        String userId = String.valueOf(((User) this.session.get(Const.LOGIN_USER)).getId());
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        try {
            List<Subject> list = this.ajaxQueryService.getSubjectByGrade(map, userId);
            JSONArray json = JSONArray.fromObject(list);
            this.out.write(json.toString());
        } catch (Exception e) {
        }
    }

    public void getSchoolBySubject() {
        String userId = String.valueOf(((User) this.session.get(Const.LOGIN_USER)).getId());
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_subjectNum};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        try {
            List<School> list = this.ajaxQueryService.getSchoolBySubject(map, userId);
            JSONArray json = JSONArray.fromObject(list);
            this.out.write(json.toString());
        } catch (Exception e) {
        }
    }

    public void getGraduationType() {
        String userId = String.valueOf(((User) this.session.get(Const.LOGIN_USER)).getId());
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_subjectNum, Const.EXPORTREPORT_schoolNum};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        try {
            List<AjaxData> list = this.ajaxQueryService.getGraduationType(map, userId);
            JSONArray json = JSONArray.fromObject(list);
            this.out.write(json.toString());
        } catch (Exception e) {
        }
    }

    public void getSourceType() {
        String userId = String.valueOf(((User) this.session.get(Const.LOGIN_USER)).getId());
        Map<String, String> map = new HashMap<>();
        String[] key = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_gradeNum, Const.EXPORTREPORT_subjectNum, Const.EXPORTREPORT_schoolNum};
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], getParameter(key[i]));
        }
        try {
            List<AjaxData> list = this.ajaxQueryService.getSourceType(map, userId);
            JSONArray json = JSONArray.fromObject(list);
            this.out.write(json.toString());
        } catch (Exception e) {
        }
    }

    public void getTeachSubject() {
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        new ArrayList();
        List<AjaxData> list = esc.getTeachSubjectList(this.exam, loginUserId);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getTeachGrade() {
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        new ArrayList();
        List<AjaxData> list = esc.getTeachGradeList(this.exam, this.subject, loginUserId);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getTeachSchool() {
        List<AjaxData> list;
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        new ArrayList();
        Object userroleId = esc.getUserroleId(loginUserId, Const.ROLE_YUEJUANGUANLIYUAN);
        if ("-2".equals(loginUserId) || "-1".equals(loginUserId) || null != userroleId) {
            list = esc.getAjaxSchoolList(this.exam, this.subject, this.grade);
        } else {
            list = esc.getTeachSchool(loginUserId);
        }
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getAveScoreSet() {
        List<Averagescore> list;
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        String jie = this.request.getParameter("jie");
        new ArrayList();
        Object userroleId = esc.getUserroleId(loginUserId, Const.ROLE_YUEJUANGUANLIYUAN);
        if ("-2".equals(loginUserId) || "-1".equals(loginUserId) || null != userroleId) {
            list = esc.getAllAveScoreSet(this.exam, this.subject, this.grade, jie, this.school, loginUserId);
        } else {
            list = esc.getClaAveScoreSet(this.exam, this.subject, this.grade, jie, this.school, loginUserId);
        }
        this.out.write(com.alibaba.fastjson.JSONArray.toJSONString(list, new SerializerFeature[]{SerializerFeature.WriteMapNullValue}));
    }

    public void submitAveScoreSet() {
        RspMsg msg;
        String userId = new CommonUtil().getLoginUserNum(this.request);
        String jie = this.request.getParameter("jie");
        String status = this.request.getParameter(Const.CORRECT_SCORECORRECT);
        String isModify = this.request.getParameter("isModify");
        String average = this.request.getParameter("average");
        String average2 = "null".equals(average) ? null : average;
        String mindev = this.request.getParameter("mindev");
        String mindev2 = "null".equals(mindev) ? null : mindev;
        String maxdev = this.request.getParameter("maxdev");
        try {
            esc.submitAveScoreSet(this.exam, this.subject, this.grade, jie, this.school, this.classNum, average2, mindev2, "null".equals(maxdev) ? null : maxdev, status, isModify, userId);
            msg = new RspMsg(200, "设定成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            msg = new RspMsg(Const.height_500, "设定失败", null);
        }
        this.out.write(JSON.toJSONString(msg));
    }

    public void updateStatus() {
        RspMsg msg;
        String userId = new CommonUtil().getLoginUserNum(this.request);
        String jie = this.request.getParameter("jie");
        String status = this.request.getParameter(Const.CORRECT_SCORECORRECT);
        String isModify = this.request.getParameter("isModify");
        try {
            esc.updateStatus(this.exam, this.subject, this.grade, jie, this.school, status, isModify, userId);
            msg = new RspMsg(200, "修改成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            msg = new RspMsg(Const.height_500, "修改失败", null);
        }
        this.out.write(JSON.toJSONString(msg));
    }

    public void getAveSetSchool() {
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        String openAllSchool = this.request.getParameter("openAllSchool");
        List<AjaxData> list = esc.getAveSetSchool("1".equals(openAllSchool) ? "-1" : loginUserId);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getAveSetGrade() {
        List<AjaxData> list;
        HttpSession session = this.request.getSession(true);
        String gradePermission = String.valueOf(session.getAttribute(Const.teacher_permission_gra));
        new ArrayList();
        if ("0".equals(gradePermission)) {
            String loginUserId = new CommonUtil().getLoginUserNum(this.request);
            list = esc.getAveSetGrade(loginUserId, this.school);
            String viewAllReports = Configuration.getInstance().getViewAllReports();
            if ((null == list || list.size() == 0) && "1".equals(viewAllReports)) {
                list = esc.getAllAveSetGrade(this.school);
            }
        } else {
            list = esc.getAllAveSetGrade(this.school);
        }
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getAveSetJie() {
        List<AjaxData> list = esc.getAveSetJie(this.school, this.grade);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getAveSetExam() {
        String jie = this.request.getParameter("jie");
        List<AjaxData> list = esc.getAveSetExam(this.school, this.grade, jie);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getSpotCheckExam() {
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        List<AjaxData> list = esc.getSpotCheckExam(loginUserId);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getSpotCheckSubject() {
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        List<AjaxData> list = esc.getSpotCheckSubject(this.exam, loginUserId);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getSpotCheckGrade() {
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        List<AjaxData> list = esc.getSpotCheckGrade(this.exam, this.subject, loginUserId);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getSpotCheckQuesion() {
        String loginUserId = new CommonUtil().getLoginUserNum(this.request);
        List<AjaxData> list = esc.getSpotCheckQuestion(this.exam, this.subject, this.grade, loginUserId);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getSpotCheckTeacher() {
        String groupNum = this.request.getParameter("groupNum");
        List<AjaxData> list = esc.getSpotCheckTeacher(groupNum);
        this.out.write(JSONArray.fromObject(list).toString());
    }

    public void getReportIsFufen() {
        String defaultFufen = Configuration.getInstance().getDefaultFufen();
        String str = analy.getReportIsFufen(this.exam, this.grade, this.subject);
        this.out.write(str + "_" + defaultFufen);
    }

    public void getReportComparativeExam() {
        String str = "[]";
        String teachUnitStatistic = this.request.getParameter("teachUnitStatistic");
        List<AjaxData> list = analy.getReportComparativeExam(this.exam, this.grade, this.subjectType, this.subject, this.teachUnit, teachUnitStatistic);
        if (null != list) {
            str = JSONArray.fromObject(list).toString();
        }
        this.out.write(str);
    }

    public void getGradeByStage() {
        String str = "";
        new ArrayList();
        String stage = this.request.getParameter("stage");
        List list = analy.getGradeByStage(stage);
        if (null != list) {
            str = JSONObject.toJSONString(list);
        }
        this.out.write(str);
    }

    public void getYuejuanZhiliangQuestionNum() throws IOException {
        List<Map<String, Object>> list = esc.getYuejuanZhiliangQuestionNum(this.exam, this.grade, this.subject);
        this.out.write(JSON.toJSONString(list));
    }

    public void getXuanzuotiDetail() {
        String[] groupNumArr = this.request.getParameter("groupNums").split(Const.STRING_SEPERATOR);
        Object[] questionNameArr = this.request.getParameter("questionNameArr").split(Const.STRING_SEPERATOR);
        List<Map<String, Object>> allList = new ArrayList<>();
        int gLen = groupNumArr.length;
        for (int g = 0; g < gLen; g++) {
            Map<String, Object> oneGroupDataMap = new HashMap<>();
            oneGroupDataMap.put("groupNum", groupNumArr[g]);
            oneGroupDataMap.put("questionName", questionNameArr[g]);
            int yipanCount = esc.getGroupYipancount(groupNumArr[g], this.userNum);
            oneGroupDataMap.put("yipanCount", Integer.valueOf(yipanCount));
            allList.add(oneGroupDataMap);
        }
        this.out.write(JSON.toJSONString(allList));
    }

    public void getReportPici() {
        String examNum = this.request.getParameter("exam");
        String gradeNum = this.request.getParameter("grade");
        String subCompose = this.request.getParameter("subCompose");
        List<Map<String, Object>> list = analy.getpiciData(examNum, gradeNum, subCompose);
        this.out.write(JSON.toJSONString(list));
    }

    public String getParameter(String arg) {
        return this.request.getParameter(arg);
    }

    public String getSchool() {
        return this.school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGrade() {
        return this.grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCla() {
        return this.cla;
    }

    public void setCla(String cla) {
        this.cla = cla;
    }

    public String getExam() {
        return this.exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public String getQtype() {
        return this.qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

    public String getExaminationRoom() {
        return this.examinationRoom;
    }

    public void setExaminationRoom(String examinationRoom) {
        this.examinationRoom = examinationRoom;
    }

    public String getStat() {
        return this.stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getExampaperNum() {
        return this.exampaperNum;
    }

    public void setExampaperNum(String exampaperNum) {
        this.exampaperNum = exampaperNum;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public int getFullScore() {
        return this.fullScore;
    }

    public void setFullScore(int fullScore) {
        this.fullScore = fullScore;
    }

    public List getTeacs() {
        return this.teacs;
    }

    public void setTeacs(List teacs) {
        this.teacs = teacs;
    }

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public String getQuestionScore() {
        return this.questionScore;
    }

    public void setQuestionScore(String questionScore) {
        this.questionScore = questionScore;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getUserNum() {
        return this.userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSnumber() {
        return this.snumber;
    }

    public void setSnumber(int snumber) {
        this.snumber = snumber;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getSubjectType() {
        return this.subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStudentType() {
        return this.studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPagestart() {
        return this.pagestart;
    }

    public void setPagestart(int pagestart) {
        this.pagestart = pagestart;
    }

    public int getCurrPage() {
        return this.currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public String getGroupnum() {
        return this.groupnum;
    }

    public void setGroupnum(String groupnum) {
        this.groupnum = groupnum;
    }

    public String getQuestionNum2() {
        return this.questionNum2;
    }

    public void setQuestionNum2(String questionNum2) {
        this.questionNum2 = questionNum2;
    }

    public String getUserpositionNum() {
        return this.userpositionNum;
    }

    public void setUserpositionNum(String userpositionNum) {
        this.userpositionNum = userpositionNum;
    }

    public String getStuSource() {
        return this.stuSource;
    }

    public void setStuSource(String stuSource) {
        this.stuSource = stuSource;
    }

    public String getScoreId() {
        return this.scoreId;
    }

    public void setScoreId(String scoreId) {
        this.scoreId = scoreId;
    }

    public String getExamNum() {
        return this.examNum;
    }

    public void setExamNum(String examNum) {
        this.examNum = examNum;
    }

    public String getStatisticType() {
        return this.statisticType;
    }

    public void setStatisticType(String statisticType) {
        this.statisticType = statisticType;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRpt_name() {
        return this.rpt_name;
    }

    public void setRpt_name(String rpt_name) {
        this.rpt_name = rpt_name;
    }

    public String getIsHistory() {
        return this.isHistory;
    }

    public void setIsHistory(String isHistory) {
        this.isHistory = isHistory;
    }

    public String getRwCount() {
        return this.rwCount;
    }

    public void setRwCount(String rwCount) {
        this.rwCount = rwCount;
    }

    public String getRptTitle() {
        return this.rptTitle;
    }

    public void setRptTitle(String rptTitle) {
        this.rptTitle = rptTitle;
    }

    public String getIsMoreSchool() {
        return this.isMoreSchool;
    }

    public void setIsMoreSchool(String isMoreSchool) {
        this.isMoreSchool = isMoreSchool;
    }

    public String getIsSaveWrite() {
        return this.isSaveWrite;
    }

    public void setIsSaveWrite(String isSaveWrite) {
        this.isSaveWrite = isSaveWrite;
    }

    public String getCorrectscore() {
        return this.correctscore;
    }

    public void setCorrectscore(String correctscore) {
        this.correctscore = correctscore;
    }

    public String getShowZSubject() {
        return this.showZSubject;
    }

    public void setShowZSubject(String showZSubject) {
        this.showZSubject = showZSubject;
    }

    public String getShowSubjectL() {
        return this.showSubjectL;
    }

    public void setShowSubjectL(String showSubjectL) {
        this.showSubjectL = showSubjectL;
    }

    public String getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(String examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public int getA() {
        return this.a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public String getQuestionNumCp() {
        return this.questionNumCp;
    }

    public void setQuestionNumCp(String questionNumCp) {
        this.questionNumCp = questionNumCp;
    }

    public String getUpdateUserCp() {
        return this.updateUserCp;
    }

    public void setUpdateUserCp(String updateUserCp) {
        this.updateUserCp = updateUserCp;
    }

    public int getSnumberCp() {
        return this.snumberCp;
    }

    public void setSnumberCp(int snumberCp) {
        this.snumberCp = snumberCp;
    }

    public String getTestCenter() {
        return this.testCenter;
    }

    public void setTestCenter(String testCenter) {
        this.testCenter = testCenter;
    }

    public String getFufen() {
        return this.fufen;
    }

    public void setFufen(String fufen) {
        this.fufen = fufen;
    }

    public String getExamineestatu() {
        return this.examineestatu;
    }

    public void setExamineestatu(String examineestatu) {
        this.examineestatu = examineestatu;
    }

    public String getShowHekeSubject() {
        return this.showHekeSubject;
    }

    public void setShowHekeSubject(String showHekeSubject) {
        this.showHekeSubject = showHekeSubject;
    }
}
