package com.dmj.service.examManagement;

import com.dmj.domain.AjaxData;
import com.dmj.domain.Astrict;
import com.dmj.domain.Class;
import com.dmj.domain.Classlevel;
import com.dmj.domain.CorrectStatus;
import com.dmj.domain.Define;
import com.dmj.domain.Exam;
import com.dmj.domain.ExamSourceSet;
import com.dmj.domain.Examinationnum;
import com.dmj.domain.Examinationroom;
import com.dmj.domain.ExamineeNumError;
import com.dmj.domain.ExamineeNumGroup;
import com.dmj.domain.Examsetting;
import com.dmj.domain.Grade;
import com.dmj.domain.GroupClass;
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
import com.dmj.domain.leq.Student_examinationNumber;
import com.dmj.domain.vo.OnlineIndicator;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.util.msg.RspMsg;
import com.zht.db.Transaction;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;

/* loaded from: ExamManageService.class */
public interface ExamManageService {
    List<Map<String, Object>> gettopItem();

    List<Integer> getSubjectByExamNum(String str);

    List<Map<String, Object>> getAllSubjectByExamNum(String str);

    Map<Integer, String> getSubject(String str);

    List<String> getSchoolByExamNum(String str, String str2);

    List<String> getGradeByExamNum(String str, String str2, String str3);

    List<String> getClassByExamNumSchoolNum(String str, String str2, String str3, String str4, String str5);

    List<String> getStuTypeByExamGradeSchNum(String str, String str2, String str3, String str4);

    void delExamRoomInfo(String str, String str2);

    Exam getExamInfo(String str);

    void addOrEditExam1(Exam exam);

    Exam getexamNum(String str);

    @Transaction
    void addOrEditExam(Exam exam, String[] strArr, String[] strArr2, String[] strArr3, String[] strArr4, Map map, String str, String str2, String str3, int i, String[] strArr5);

    @Transaction
    void addOrEditExamSchool(Exam exam, String[] strArr);

    Object[] getExamList(Map<String, String> map);

    @Transaction
    void delOrRevokeExam(int i, String str, String str2);

    @Transaction
    void batchDelOrRevokeExam(String[] strArr, String str, String str2);

    @Transaction
    void addtoCompleteExam(String[] strArr, String str, String str2);

    String checkExamstatus(String[] strArr);

    String getListByNum(String str);

    List getPaperQuestion(String str, String str2, String str3, String str4);

    List getStudentsTotalScoreList(String str, String str2, String str3, String str4, String str5);

    List getStudentsTotalScoreList_levelcla(String str, String str2, String str3, String str4, String str5);

    List getStudentsTotalScoreListByExaminationRoom(String str, String str2, String str3, String str4, String str5, String str6);

    List getQuestionInfo(String str, String str2, String str3, String str4, String str5, String str6);

    List getQuestionInfo_levelcla(String str, String str2, String str3, String str4, String str5, String str6);

    List getQuestionInfoByExaminationRoom(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    void clearStudentLevel();

    Integer getCreateTableData(String str, String str2, String str3, String str4);

    List getCTDExamPapeerCount(String str);

    List getCTDStudentCount(String str);

    List getSingleSubjectData_levcla(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List<Studentlevel> getSingleSubjectData_levcla_fufen(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List getSingleSubjectData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, boolean z, String str10, String str11);

    List<Studentlevel> getSingleSubjectData_fufen(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, boolean z, String str10, String str11);

    List getSingleSubjectDataExaminationRoom(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List getSingleSubjectDataPage(String str, String str2, String str3, String str4, String str5, int i, int i2);

    List getAllSubjectName(String str, String str2, String str3);

    List getTotalAndStudentId(String str, String str2, String str3, String str4);

    List getClassStuAndSub(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10) throws Exception;

    List<Object[]> getClassStuAndSub_new(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11) throws Exception;

    List<List<?>> getClassStuAndSub_fufen(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10) throws Exception;

    List<List<?>> getClassStuAndSub_bzf(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11) throws Exception;

    List<Object[]> getClassStuAndSub_bzf_new(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11) throws Exception;

    List getClassStuAndSub_NoClaGraRank(String str, String str2, String str3, String str4, String str5) throws Exception;

    List getAvgScoreAndStuCount(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List<Classlevel> getAvgScoreAndStuCount_fufen(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List getClassType(String str, String str2, String str3, String str4) throws Exception;

    List<Studentlevel> getClassType_fufen(String str, String str2, String str3, String str4) throws Exception;

    List getClassStuAndSubByExaminationRoom(String str, String str2, String str3, String str4, String str5, String str6) throws Exception;

    List<Object[]> getClassStuAndSubPage(String str, String str2, String str3, String str4, int i, int i2);

    Integer getScoreImageCount(Scoreimage scoreimage);

    List getLeakScoreImageById(Scoreimage scoreimage, int i);

    List getLeakScoreImages(Scoreimage scoreimage);

    List getStudentIdErrCount(ExamineeNumError examineeNumError, String str);

    List getFirstStudent(ExamineeNumError examineeNumError, String str, String str2);

    List getErrStusFromExamineeNumError(ExamineeNumError examineeNumError, int i, int i2, String str);

    @Transaction
    void divideExamRoom(String str, String str2, String str3, String str4, String str5, String str6, String str7, JSONArray jSONArray, String str8);

    @Transaction
    void divideExamRoomOfGdkc(String str, String str2, String str3, String str4, String str5, String str6, String str7, JSONArray jSONArray, String str8);

    @Transaction
    void setClassNum(String str);

    List<Object[]> getBeforeExamList(String str);

    @Transaction
    void copyExamRoomInfo(String str, String str2);

    List<StuExamInfo> printStuExamID(String str, String str2, String str3, String str4, String str5, String str6);

    List<Examinationroom> getexamroomList(String str, String str2);

    List getexamroomgradeList(String str, String str2);

    List<StuExamInfo> getStuByexamroom(String str, String str2, String str3, String str4);

    List getexamclass(String str, String str2, String str3);

    List getexamgrade(String str, String str2);

    List getexamschool(String str);

    List getExamTC(String str);

    List getexamallschool1(String str);

    List<StuExamInfo> getexamallschool(String str, String str2);

    Object getschnum(String str);

    Object getclanum(String str);

    Map<String, String> getschool(String str);

    Map<String, String> getTestCenter(String str);

    Map<String, String> getAuthTestCenter(String str, String str2);

    Map<String, String> getAuthSchool(String str, String str2);

    Map<String, String> getEnSubject(String str);

    Object getexamName(String str);

    String areaAllComplete(String str, int i, String str2) throws Exception;

    String fufenComplete(String str, int i, String str2) throws Exception;

    String completeExam(String str, int i, String[] strArr) throws Exception;

    List allCompleteIfUpGrade(int i);

    List getStudentPaperById(String str);

    @Transaction
    void modifyError(String str, String str2, Student student, String str3, int i);

    @Transaction
    Integer modifyExamroomAndExamineeNum(String str, String str2, String str3, String str4, String str5, int i, String str6, String str7, String str8, String str9, String str10);

    @Transaction
    void modifyError2(String str, String str2, Student student, String str3, ExamineeNumError examineeNumError, String str4, int i);

    List getStudentInfoFromStudent(String str);

    Examinationnum getExaminationnumObj(String str, String str2, String str3, String str4, String str5, String str6);

    Score findOneFromScore(String str, String str2, String str3, int i);

    RegExaminee authDataExistsFromRegExaminee(String str, String str2, String str3, String str4, int i, String str5, String str6, List<RegExaminee> list, String str7, String str8);

    List isExistInExamineeNumError(ExamineeNumError examineeNumError);

    List getOneFromExamineeNumError(String str, String str2, String str3, String str4);

    ExamineeNumError getExamineeNumErrorObjById(String str);

    @Transaction
    Integer deleteByExamineeNumErrorObj(ExamineeNumError examineeNumError);

    Integer addOne(Object obj);

    @Transaction
    void addObjList(List<Object> list);

    String getExcelFileNameByNum(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    String getExcelFileNameByNum2(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    String getExcelFileNameByNum3(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    Integer insertIntoClassLevel(String str);

    Integer insertIntogradeLevel(String str);

    List<Studentlevel> getCheckStuPaperList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List<Studentlevel> getCheckStuPaperList_score(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    List<Studentlevel> getCheckStuPaperListPage(String str, String str2, String str3, String str4, String str5, String str6, int i, int i2, String str7, String str8, String str9);

    List<Studentlevel> getCheckStuPaperListPage_score(String str, String str2, String str3, String str4, String str5, String str6, int i, int i2, String str7, String str8, String str9, String str10, String str11);

    List<Studentlevel> getCheckStuPaperListPage_Class(String str, String str2, String str3, String str4, String str5, String str6, int i, int i2);

    List getStuIdByNumOrName(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    Studentpaperimage getspiObj(int i);

    List<Studentlevel> getCheckStuPaperDetail(String str, String str2, String str3, String str4, String str5, String str6, int i, int i2, String str7, String str8, String str9);

    List<Studentlevel> getCheckStuPaperDetail_score(String str, String str2, String str3, String str4, String str5, String str6, int i, int i2, String str7, String str8, String str9, String str10, String str11);

    List getLeakFromScore(String str, String str2, String str3, String str4, String str5);

    List getLeakFromScoreForScoreModify(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List getExpQuestionImg(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    byte[] getImage(String str, String str2, String str3);

    String getexamNumByName(String str);

    @Transaction
    void subexamset(Examsetting examsetting, String str, String str2);

    void deleteByexamNum(String str);

    Examsetting getone(String str);

    @Transaction
    Integer updateScore(String str, String str2, String str3, double d, double d2, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    Integer updateScore_modified(String str, String str2, String str3, double d, double d2, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List getRoomAndExamineeLength(String str);

    List isExistTheExaminee(String str, String str2, String str3, String str4, String str5, String str6, int i, String str7, String str8, String str9, String str10);

    List searchCreateExam(String str);

    List searchDefine(String str);

    List viewScanExaminationRoom(String str);

    List getScanRoomSuject(String str, String str2);

    List getSchoolGrade();

    List getGradeDefineDetail(String str, String str2);

    CorrectStatus getScoreCheckCount(String str);

    List getCertainCheckDetail(String str, String str2);

    CorrectStatus getExamineeNumCheckCount(String str);

    List getExamineeNumCheckDetail(String str, String str2);

    CorrectStatus getNumErrorCount(String str);

    List getNumErrorDetail(String str, String str2);

    CorrectStatus getScoreExceptionCount(String str);

    List getScoreExceptionDetail(String str, String str2);

    void importexamStudent(String str, File file, String str2, int i, String str3, String str4, String str5, String str6) throws IOException;

    @Transaction
    void importexamStudentLeq(String str, File file, String str2, String str3, String str4, String str5, String str6, String str7);

    void dropT(String str);

    List getAllSchoolByExamNum(String str, String str2);

    List getSjtByNum(String str);

    List getClassById(String str, String str2, String str3);

    void addOneExamsetting(int i, Object obj, String str, String str2);

    Integer examsetcount(String str);

    void delexamset(String str);

    List getSchools();

    List getGrades(String str);

    List getClasses(String str, String str2, String str3, String str4, String str5);

    List getsubjects(String str, String str2);

    void addMarkError(String str, Integer num, String str2, Double d, String str3, String str4, String str5);

    List<Define> getExcelFileNameByNum(String str, String str2, String str3);

    Map getpageNum(String str, String str2);

    List getLeakFromScore_modified(String str, String str2, String str3, String str4, String str5);

    List getExpQuestionImg_info_modified(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    Object getGradeNum(String str);

    Object getSubjectNum(String str);

    Exam getexamBean(String str);

    String getSchoolNameByNum(String str);

    String getTestCenterNameByNum(String str);

    String getShortNameByNum(String str);

    Integer getcountByOldexam(String str, String str2, String str3);

    String getjie(String str, String str2);

    String getexamjie(String str, String str2);

    Integer getcountBybeforeexam(String str, String str2, String str3);

    List<Grade> getgradeNumByexam(String str);

    List<ExamSourceSet> getSourceSet(String str);

    List<Class> getclassBygraSchExam(String str, int i, String str2, String str3);

    List<ExamineeNumGroup> getGroupList(String str, String str2, int i);

    List<Subject> getSubByExamGrade(String str, int i);

    List<Subject> getSubByExamGrade_SetOE(String str, int i);

    Map<String, Object> getOEByExampaperNum(String str);

    int insertOE(String str, String str2);

    int updateOE(String str, String str2, int i);

    int setOEDefine(List<Map<String, String>> list, String str, String str2);

    void ywsetup(String str, String str2);

    void subABsetup(String str, String str2);

    void subMultisetup(String str, String str2);

    @Transaction
    void subABexamsetup(String str, String str2, String str3);

    @Transaction
    void subABgradesetup(String str, String str2, String str3, String str4);

    String getStatus(String str, String str2, String str3);

    List parentExamPaperNum(String str, String str2, String str3);

    Map<String, String> getschNumByExam(String str);

    void submitAddGroup(String str, String str2, String str3, String[] strArr, String str4, String str5, String str6, String str7, String str8, String str9);

    Integer namecount(String str, String str2, String str3, String str4);

    List<GroupClass> getgroupClassList(String str, int i, String str2, String str3);

    void delgroup(String str);

    void delgroupCla(String str, String str2, String str3, String str4, String str5);

    String getGradeNameByNum(String str, String str2);

    String getGradeNameByGradeNum(String str);

    List<ExamineeNumGroup> getexamineeGroupList(String str, String str2, String str3);

    void submitAddToGroup(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    void clearExamineeNumRoom(String str);

    @Transaction
    void divideExamRoomByGradeNoGroup(String str, String str2, int i, List<Class> list, String str3, String str4, String str5, String str6, JSONArray jSONArray, JSONArray jSONArray2, String str7);

    @Transaction
    void divideExamRoomByGradeNoGroupOfGdkc(String str, String str2, int i, List<Class> list, String str3, String str4, String str5, String str6, JSONArray jSONArray, JSONArray jSONArray2, JSONArray jSONArray3, String str7);

    List<String> getGroupNumList(String str, String str2, String str3);

    List getschNumListByExam(String str);

    List getGradeList(String str, String str2);

    List getMarkError(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List getsentence(String str, String str2, String str3, String str4, String str5);

    @Transaction
    void batchExportSql(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    @Transaction
    void batchImportSql(String str, String str2, String str3);

    List sentenceModified(String str, String str2, String str3, String str4, String str5);

    List getSentenImg(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    Integer updateSentenceScore(String str, String str2);

    Integer getexamid(String str);

    Integer checksReg(String str, String str2, String str3, String str4);

    RspMsg checkExamData(String str, String str2, String str3);

    @Transaction
    void del(String str, String str2, String str3, String str4, String str5, String str6) throws SQLException;

    Map getShuangXiang1(String str);

    Map getCaiQie1(String str);

    Map getSaoMiao1(String str, String str2, String str3);

    Map getHeDuiNum(String str, String str2, String str3);

    Map getbulu(String str, String str2, String str3);

    Map getBulu(String str);

    Map getScore(String str);

    Map getScore2(String str);

    Integer getException(String str, String str2);

    Map getpanfen(String str);

    Map getpanfen2(String str);

    List<QuestionGroup> getGroupNumList(String str);

    List querySchoolByExam(String str);

    List<AjaxData> queryGradeBySchoolAndExam(String str, String str2);

    List queryschool();

    List queryexam(String str, String str2);

    List<AjaxData> querygrade(String str);

    List<AjaxData> querysubject(String str, String str2);

    Object[] queryneirong(String str, String str2, String str3, String str4, String str5);

    Object[] queryneirong_fc(String str, String str2, String str3, String str4, String str5, String str6);

    Object[] queryneirong_q(String str, String str2, String str3, String str4);

    Object[] queryneirong_q_fc(String str, String str2, String str3, String str4, String str5);

    Integer createAndUpdatevalue(OnlineIndicator onlineIndicator, String str);

    Integer deletessx(OnlineIndicator onlineIndicator, String str);

    Integer createAndUpdatevalueScore(OnlineIndicator onlineIndicator, String str);

    String queryclasscountvalue(OnlineIndicator onlineIndicator);

    String queryclasscountvalueScore(OnlineIndicator onlineIndicator);

    List queryall(String str, String str2, String str3, String str4, String str5);

    List zqexam(String str, String str2, String str3, String str4, String str5);

    List<OnlineIndicator> zqexam_q(String str, String str2, String str3, String str4, String str5);

    void fzzqexam(String str);

    @Transaction
    String copyvalue(String str, String str2, String str3, String str4);

    List querysubject(String str, String str2, String str3, String str4);

    List querysubjectL(String str, String str2, String str3, String str4);

    List querysubjectW(String str, String str2, String str3, String str4);

    List queryclassL(String str, String str2, String str3);

    List queryclassW(String str, String str2, String str3);

    List queryclass(String str, String str2, String str3);

    String getgrandeName(String str, String str2);

    List queryOnlineIndicator(String str, String str2, String str3, String str4, String str5);

    OnlineIndicator queryOnlineIndicatorOne(String str, String str2, String str3, String str4, String str5, String str6, Integer num, String str7);

    Integer queryxiancount();

    String maxcountyanzheng(OnlineIndicator onlineIndicator);

    String maxcountyanzheng_q(OnlineIndicator onlineIndicator);

    String maxcountyanzhengScore(OnlineIndicator onlineIndicator);

    String typecountyanzheng(OnlineIndicator onlineIndicator);

    String typecountyanzhengScore(OnlineIndicator onlineIndicator);

    String typecountyanzheng2(OnlineIndicator onlineIndicator);

    String typecountyanzheng2Score(OnlineIndicator onlineIndicator);

    List getexamgradeList(String str, String str2);

    List getStudentType(String str, String str2, String str3);

    String getUpexamNum(String str, String str2, String str3);

    List<Studentlevel> getstuScoreList(String str, String str2, String str3, String str4, String str5, List list, String str6, String str7, String str8);

    @Transaction
    String divexamroomByScore(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, JSONArray jSONArray, JSONArray jSONArray2, String str10, String str11);

    @Transaction
    String divexamroomByScoreOfGdkc(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, JSONArray jSONArray, JSONArray jSONArray2, JSONArray jSONArray3, String str10, String str11);

    void deleteByGrade(String str, String str2, String str3);

    List<StuExamInfo> studentLengthList(String str, String str2, String str3);

    List<StuExamInfo> exporStudentList(String str, String str2, String str3, String str4);

    List<StuExamInfo> exporStudentList1(String str, String str2, String str3, String str4);

    List<StuExamInfo> exportAllStudentList(String str, String str2, String str3);

    List<StuExamInfo> studentroomList(String str, String str2, String str3, String str4, String str5);

    String schoolName(String str);

    String testCenterName(String str);

    List<StuExamInfo> studentroomList1(String str, String str2, String str3, String str4, String str5);

    List<StuExamInfo> exportstudentroomList(String str, String str2, String str3, String str4, String str5, String str6);

    List<StuExamInfo> exportstudentroomList_tiaoma(String str, String str2, String str3, String str4, String str5);

    List<StuExamInfo> exportstudentroomList1(String str, String str2, String str3, String str4, String str5, String str6);

    List<StuExamInfo> exportALLRoomList(String str, String str2, String str3, String str4, String str5);

    List<StuExamInfo> exportALLRoomListByClass(String str, String str2, String str3, String str4, String str5);

    List<StuExamInfo> gradeName(String str, String str2);

    List<StuExamInfo> gradeNameBySchool(String str, String str2);

    List<StuExamInfo> gradeNameBySub(String str, String str2, String str3, String str4);

    String authorExampaperIlleagal(String str, String str2, String str3, String str4, String str5);

    void addToCorrectstatus(String str, String str2, String str3, String str4, String str5, String str6);

    Exam getexamroomAndexamineeLength(String str);

    String querySubjectTypeName(String str);

    List selectXianName();

    void updateXianName(String str, String str2, String str3);

    Integer selectCountName(String str, String str2);

    List<StuExamInfo> classList(String str, String str2, String str3, int i, String str4);

    List<StuExamInfo> gradeList(String str, String str2, String str3);

    List<StuExamInfo> classLengthList(String str, String str2, String str3, String str4);

    List<StuExamInfo> classsheetList(String str, String str2, String str3, int i, String str4);

    List<StuExamInfo> zuoqianroomList(String str, String str2, String str3, String str4);

    @Transaction
    void deleteAllByExamNum(String str);

    String queryStuTypeName(String str);

    List getresourceList(String str);

    void updateExamSourceSet(String str, String str2, String str3, String str4);

    Integer getsuperfull(String str);

    Integer getJudgeFinish(String str);

    Integer getChongPan(String str);

    void excuteExceptionNum(String str);

    String getgradeNumByName(String str);

    void subtemplate_setup(String str, String str2);

    void subcal_setup(String str, String str2);

    @Transaction
    void subfenzu_setup(String str, String str2);

    void suben_setup(String str, String str2);

    RspMsg jsssx(String str, String str2, String str3, String str4);

    List<String> getGradeByExamNum_new(String str, String str2);

    Map<Integer, String> getGradeByExamNum_fp(String str, String str2);

    List<String> getStuTypeByExamGradeSchNum_new(String str, String str2, String str3);

    List<String> getSchoolByExamNum_new(String str, String str2, String str3, String str4);

    List<Exam> getallexam();

    List<Grade> getallgrade();

    List<Grade> getallgradezy(String str);

    List<School> getSchoolScoreNum(String str);

    List<ScoreCalden> getScoreCalden(String str, String str2, String str3);

    List<Subject> getallsubject();

    List<Subject> getallsubject_zy(String str);

    List getAllSubAnaly(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    RspMsg saveTestCenter(Testingcentre testingcentre);

    RspMsg updateTestCenter(Testingcentre testingcentre);

    void saveexamRoom(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    Integer getexamNationLength(String str);

    List getStatisticsData(String str, String str2, String str3, String str4, String str5);

    Map<String, String> getTestCenterMap(String str);

    Map<String, String> getSchoolMap();

    Object getExampaperType(String str, String str2, String str3);

    String getScanMsg(String str, String str2, String str3);

    Object autoSetTesting(String str);

    List getScoreDetail(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List getScoreDetail2(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List getScoreDetailQingda(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List getScoreDetailFc(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List getScoreDetailFc2(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List getScoreDetailFcQingda(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List<Questionimage> getScoreLocation(String str, String str2, String str3);

    List<Questionimage> getQScoreLocation(String str);

    List<Define> getHbcqQScoreLocation(String str);

    Map<String, Object> getHeDuiNum(String str);

    Map<String, Object> getShuangXiang(String str);

    Map<String, Object> getCaiQie(String str);

    Map<String, Object> getSaoMiao(String str);

    int getExamMsgByChecked(String str, String str2, String str3, String str4, int i, String str5);

    List<Student> getExamStuByChecked(String str, String str2, String str3, String str4, String str5);

    Map queryValFromDataByType(String str);

    Exam getExamStatus(String str);

    String getSubTotalScore(String str, String str2, String str3, String str4);

    List getFourRatiosData(String str, String str2, String str3, String str4);

    List getNumAndRatio(String str, String str2, String str3, String str4, String str5, String str6);

    int updateFourRatios(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    void calculateFourRatios(String str, String str2, String str3, String str4, String str5);

    List<Exam> getExamList_leq(String str);

    List<AjaxData> getStypeList_leq(String str, String str2);

    List<Subject> getSubjectList_leq(String str, String str2, String str3);

    List getQuoteExam(String str, String str2, String str3, int i, String str4);

    List getQuoteClass(String str, String str2, String str3, String str4, String str5, String str6);

    void deleteSsxData(String str, String str2, String str3, int i, String str4);

    Integer quote(String str, String str2, Integer num, String str3, String str4, Integer num2, String str5, String str6, String str7);

    String getComputeTime(String str, String str2, String str3);

    String insertAppealDate(String str, String str2, String str3, String str4, String str5);

    List<Map<String, Object>> getSubByExamGrade2(String str, int i);

    int insertAstrict(String str, int i, String str2, String str3);

    List getBlankPaper(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i, int i2, String str9, String str10);

    int saveRecord(String str, String str2);

    int setIllStatus(String str, String str2);

    List<Testingcentre> getTestingcenterList(String str, String str2);

    List<School> getSchoolsByTc(String str, String str2);

    @Transaction
    int autoTestingcentre(String str, String str2);

    List<School> getLeftAllSchool(String str, String str2);

    List<School> getUnassignedSchool(String str, String str2, String str3, String str4);

    int addTCSchool(Testingcentre_school testingcentre_school);

    int delTCSchool(Testingcentre_school testingcentre_school);

    @Transaction
    void delTestingcentre(String str, String str2);

    @Transaction
    void batchDelTestingcentre(Object[][] objArr, Object[][] objArr2);

    List<Testingcentre> getTestCenterMap(String str, String str2, String str3);

    List<Testingcentre> getAuthTestCenterMap(String str, String str2);

    List<Testingcentre_school> getTcSchList(String str, String str2);

    RspMsg checkStuEnFile(String str, File file, String str2, String str3, String str4, String str5, String str6, String str7);

    @Transaction
    void importStuEn(String str, File file, String str2, String str3, String str4, String str5, String str6, String str7);

    Object getStuOfInconformity(String str, String str2);

    @Transaction
    void createStuEn(Object[][] objArr, String str);

    @Transaction
    void createStuEnList(List<Student_examinationNumber> list, String str);

    List<Student_examinationNumber> getAllStudent();

    void deleteExaminationNumber(String str);

    int getMaxSchoolNumLen();

    int getMaxClassNumLen();

    List<Student_examinationNumber> getStudentList(String str, String str2);

    List<Object[]> getClassStuAndSubOneFile(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11) throws Exception;

    List<Object[]> getClassleicengFile(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12) throws Exception;

    String isexamleiceng(String str, String str2);

    List<Student_examinationNumber> getStudentLenList(String str, String str2);

    List<Student_examinationNumber> getStudentYuzhiList();

    int updateAstrict(Map<String, String> map, String str);

    @Transaction
    int updateAstrict1(Map<String, String> map, String str);

    List<Astrict> queryAstrict(String str, int i);

    List<Shangxian> getAllFilterData(String str);

    List<Map<String, String>> getAllSubjectList(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<AjaxData> getPiciList(String str, String str2);

    List<Map<String, Object>> getShangxianData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List<Map<String, Object>> getonlinesample(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Transaction
    void saveOnlineScore(OnlineIndicator onlineIndicator);

    @Transaction
    void saveOnlineNum(OnlineIndicator onlineIndicator);

    RspMsg existShangxianData(OnlineIndicator onlineIndicator);

    Integer delShangxianDatabyfenshuyuan(OnlineIndicator onlineIndicator);

    void deleteAnotherOnline(OnlineIndicator onlineIndicator);

    void deleteOnline(OnlineIndicator onlineIndicator);

    @Transaction
    void saveOnline(OnlineIndicator onlineIndicator, JSONArray jSONArray);

    @Transaction
    RspMsg jisuanOnline(String str, String str2);

    String getJisuanshijian(String str, String str2, String str3, String str4);

    int getTishubuduiStuCount(String str, String str2, String str3);

    int getTishubuduiStuCountByKeguan(String str, String str2, String str3);

    String getExamineeNum(String str, String str2, String str3, String str4);

    List<RegExaminee> getDifferentRegList(String str, String str2, String str3, String str4, String str5);

    List<RegExaminee> getAllRegList(String str, String str2);

    List<Examinationnum> getEnList(String str, String str2, String str3, String str4);

    @Transaction
    void updateStudentIdByRegId(RegExaminee regExaminee);

    List<RegExaminee> existReglByStuId(RegExaminee regExaminee);

    @Transaction
    void deleteAllExamineenumerror(String str, String str2);

    String getExamPaperNum(String str, String str2, String str3);

    Integer save(Object obj);

    RspMsg existEnByTestCenter(String str);

    RspMsg existRegByTestCenter(String str);

    RspMsg existEnByExam(String str);

    RspMsg existRegByExam(String str);

    Map getFistQuestionCommentImg(String str, String str2, String str3);

    void exportTestingcentre(String str, String str2);

    RspMsg checkTestingcentreFile(File file, String str, String str2, String str3);

    @Transaction
    void importTestingcentre(File file, String str, String str2, String str3);

    List<Map<String, Object>> getSubjectExceptionCheckLiandongData();

    int getSubjectExceptionCount(String str, String str2, String str3, String str4, String str5, String str6);

    List<Map<String, Object>> getSubjectExceptionData(String str, String str2, String str3, String str4, String str5, String str6, int i);

    void exportSubjectExceptionData(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Transaction
    void deleteShangxianData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Transaction
    List<Map<String, Object>> getallschoolList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Transaction
    Integer ischeck(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List<Map<String, Object>> getschoolsList(String str, String str2, String str3, String str4);

    List<Map<String, Object>> getxueduanList();

    List<Map<String, Object>> getxueduan(String str);

    List<Map<String, Object>> getpicilist();

    List<Map<String, Object>> getschools();

    @Transaction
    Integer updateschoolsList(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Transaction
    Integer updateschoolsList2(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Transaction
    Integer updateschoolsbeizhu(String str, String str2, String str3, String str4, String str5, String str6);

    @Transaction
    Integer updateschoolsstunum(String str, String str2, String str3, String str4, String str5, String str6);

    @Transaction
    Integer updateonlinesample(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, JSONArray jSONArray);

    String getIsFufen(String str);

    List<Map<String, Object>> getSettingParam(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    String doimportschools(List<List<Object>> list, String str, String str2, String str3, String str4);

    @Transaction
    List<Map<String, Object>> getShouxuanMapList(String str);

    @Transaction
    void saveAutoRule(Map<String, Object> map);

    Map<String, Object> getAutoRuleByExam(String str);

    @Transaction
    void divideExamRoomByClass(Map<String, Object> map);

    List<Map<String, Object>> viewExamRoomTreeData(Map<String, Object> map);

    @Transaction
    void divideExamRoomByGrade(Map<String, Object> map);

    String ishaveSchool(List<List<Object>> list);

    List getAllSchool(String str, String str2);
}
