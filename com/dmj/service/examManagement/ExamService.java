package com.dmj.service.examManagement;

import com.dmj.domain.Abilitydetail;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Averagescore;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.Class;
import com.dmj.domain.Classexam;
import com.dmj.domain.CorrectInfo;
import com.dmj.domain.Define;
import com.dmj.domain.Exam;
import com.dmj.domain.ExamData;
import com.dmj.domain.Examinationnum;
import com.dmj.domain.ExamineeStuRecord;
import com.dmj.domain.Exampaper;
import com.dmj.domain.GeneralCorrectData;
import com.dmj.domain.Knowdetail;
import com.dmj.domain.MarkError;
import com.dmj.domain.Questiontypedetail;
import com.dmj.domain.RegExaminee;
import com.dmj.domain.Reg_Th_Log;
import com.dmj.domain.Remark;
import com.dmj.domain.Score;
import com.dmj.domain.Student;
import com.dmj.domain.Subject;
import com.dmj.domain.Task;
import com.dmj.domain.User;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.domain.vo.QuestionGroup_question;
import com.dmj.domain.vo.Questiongroup_mark_setting;
import com.zht.db.Cache;
import com.zht.db.Transaction;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;

/* loaded from: ExamService.class */
public interface ExamService {
    Integer save(Object obj);

    Object getOneByNum(String str, String str2, Class cls);

    int[] batchSave(List<Object> list);

    Integer saveA(String str);

    Integer save_all(String str);

    Integer delA(String str, String str2);

    Integer delOptionCount(String str, String str2, String str3) throws Exception;

    Integer saveCat(String str) throws Exception;

    Integer saveCat_cate(String str) throws Exception;

    String getEx(String str) throws Exception;

    Integer deleteOneByNum(String str, String str2, Class cls);

    @Transaction
    Integer save(List<Define> list, String str, List<Knowdetail> list2) throws Exception;

    @Transaction
    Integer save_Abilitydetail(List<Define> list, String str, List<Abilitydetail> list2) throws Exception;

    @Transaction
    Integer save_know_ability(List<Define> list, String str, List<Knowdetail> list2, List<Abilitydetail> list3) throws Exception;

    Integer save_know_ability_questiontype(List<Define> list, String str, List<Knowdetail> list2, List<Abilitydetail> list3, List<Questiontypedetail> list4) throws Exception;

    String getExampaperNumBySubjectAndGradeAndExam(String str, String str2, String str3);

    String getExampaperNum(String str, String str2, String str3);

    String getpExampaperNum(String str, String str2, String str3);

    Exampaper getExampaper(String str, String str2, String str3);

    List<Exampaper> getExampaperNum_threeSjt(String str, String str2, String str3, String str4);

    String judgeYouOrWang(String str, String str2, String str3);

    List<Subject> getSubjectName(String str);

    String getExamPaperMark(String str);

    String checkIfHaveCalculate(String str, String str2, String str3);

    List<Integer> getScoreList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    byte[] getQuesionImage(String str);

    List<Object> getCrossPageScoreIdList(String str);

    byte[] getobjectimg(String str, String str2);

    byte[] getQuesionImage(String str, String str2, String str3, String str4);

    List<String> getQuesionImage_scoreIdList(String str, String str2, String str3, String str4, String str5);

    byte[] getBigImage(String str);

    Object getoneNote(String str, String str2, String str3, String str4);

    String getschool(String str);

    List<CorrectInfo> getCorrectList(Score score);

    Integer getCorrectListCount(Score score);

    List<CorrectInfo> getImageNotToScoreList(String str, String str2, String str3);

    List authIfExistUpFullScoreException(String str, String str2, String str3, String str4, String str5);

    Integer updateScore(String str, String str2, String str3, String str4);

    @Transaction
    Integer updateScore(String[] strArr, double d, boolean z);

    @Transaction
    Integer recognizeWrong(String[] strArr);

    Integer updateCorrectStatus(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    Integer updateCorrectNumsStat(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    String getCorrectStatus(String str, String str2, String str3, String str4, String str5);

    Integer SYNCCorrectingInfo();

    CorrectInfo imageDetailInfo(CorrectInfo correctInfo);

    double getFullScore(String str, String str2);

    String commbinPreNum(String str);

    @Transaction
    Integer updateExamineeNum(String str, String[] strArr, String[] strArr2, String str2, String str3, String str4, String str5) throws Exception;

    Integer getNumsOfStudentByExamroomId(String str) throws Exception;

    boolean authexamineeNumExists(String str, String str2, String str3, String str4) throws Exception;

    boolean authScoreExists(String str, String str2, String str3) throws Exception;

    String authError(String str, String str2, String str3, String str4, String str5, String str6) throws Exception;

    Integer updateRegStudentIdById(String str, String str2, String str3, String str4, String str5, String str6) throws Exception;

    Integer updateRegStudentId(String str, String str2, String str3, String str4, String str5) throws Exception;

    Define getQuesionDefineByExampaperNumAndQuesionNum(String str, String str2);

    String updateAnswer(String str, String str2, String str3, String str4, String str5, int i);

    List<GeneralCorrectData> getGeneralCorrectData(String str, String str2, String str3, String str4);

    String getScoreModelByExam(String str, String str2);

    double getFullScoreByExampaperAndQuestionNum(String str, String str2);

    Define getDefineByExampaperNumAndQuestionNum(String str, String str2);

    String getQuestionScoreByQuestionInfo(String str, String str2, String str3);

    RegExaminee changePage(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    Examinationnum getExaminationnumInfoByExamNumAndExaminee(String str, String str2, String str3);

    @Transaction
    Integer deletePageErrorInfo(String str, String str2, String str3, String str4);

    List<Object> getExamRoomPageRank(String str, String str2, String str3);

    List<AjaxData> getCorrectExamList(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getCorrectExamList_zy(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getCorrectExamList_zyyh(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getCorrectSubjectList(String str, String str2, String str3, String str4);

    List<AjaxData> getCorrectSubjectList_zy(String str, String str2, String str3, String str4);

    List<AjaxData> getCorrectSubjectList_zyyh(String str, String str2, String str3, String str4);

    List<AjaxData> getCorrectSchoolList(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getCorrctTCList(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getCorrctTCList2(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getCorrctAuthTCList(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getCorrctAuthTCList2(String str, String str2, String str3, String str4, String str5, String str6);

    boolean isManager(String str);

    List<AjaxData> getCorrectSchoolList_zy(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getCorrctSchoolList_EP(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<AjaxData> getCorrctAuthSchoolList_EP(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<Define> getSQuestionNum(String str, String str2, String str3);

    List<Task> getSTeacherNum(String str, String str2, String str3, String str4);

    List<Task> getSTeacherNum2(String str, String str2, String str3, String str4, String str5);

    List<Student> getSpotCheck_stuInfo(String str, String str2, String str3);

    List<Task> getSpotCheck(Integer num, Integer num2, Integer num3, String str, Integer num4, Integer num5, Integer num6, Integer num7, String str2);

    List<Task> getSpotCheck3(Integer num, Integer num2, Integer num3, String str, String str2, Integer num4, Integer num5, Integer num6, String str3, String str4, String str5, String str6);

    List<Task> getSpotCheck2(Integer num, String str, Integer num2, String str2);

    List<Task> getSpotCheck2_detail(Integer num, String str, Integer num2, String str2);

    List<Define> getSpotCheckFullScore(Integer num, Integer num2, Integer num3, String str);

    List<Score> getSpotCheckScore(Integer num, Integer num2, Integer num3, String str, String str2);

    List<Remark> getSpotCheckRemark(Integer num, Integer num2, Integer num3, String str, String str2);

    List<Task> getOneTwo(String str, String str2, String str3, String str4);

    List<ExamineeStuRecord> getExamineeRecord(Integer num, Integer num2, Integer num3, String str, String str2);

    Integer addExamineeRecord(ExamineeStuRecord examineeStuRecord);

    List<QuestionGroup_question> getQuestionGroupScheDule(Integer num, Integer num2, Integer num3, Integer num4);

    List<QuestionGroup_question> getQuestionGroupScheDule1(String str, String str2, String str3);

    List<QuestionGroup_question> getQuestionGroupSD(Integer num, Integer num2, Integer num3);

    List<QuestionGroup_question> getQuestionGroupSD1(Integer num, Integer num2);

    Map<String, ExamData> getSubjectProgress();

    Map<String, Map<String, Object>> getYuejuanProgress(ServletContext servletContext);

    List jiancha(Integer num, Integer num2, Integer num3, Integer num4);

    Object getGroupNameBynum(String str);

    List<QuestionGroup_question> getGroupSize(String str, String str2);

    List<QuestionGroup_question> getQuesTask(Integer num, Integer num2, Integer num3, Integer num4);

    Object getStudentSize(String str, String str2, String str3, String str4);

    List<Questiongroup_mark_setting> getMarkTypeSche(String str, String str2);

    Object getPaperNum(String str, String str2, String str3, String str4);

    Object getRemarkNum(String str, String str2, String str3, String str4, int i);

    List<AjaxData> getCorrectGradeList(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getCorrectGradeList_zy(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<AjaxData> getExamPlace(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getAuthExamPlace(String str, String str2, String str3, String str4, String str5, String str6);

    List getCorrectSubjectList(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getCorrectExaminationRoomList(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<AjaxData> getCorrectExaminationRoomList_zy(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getCorrectExaminationRoomList_EP(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getCorrectAuthExaminationRoomList_EP(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getCorrectClassList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getClassList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getExportStudentClassList(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getCorrectExaminationRoomStudent(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<AjaxData> getCorrectClassStudent(String str, String str2, String str3, String str4, String str5);

    List<Remark> getSpotC(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<MarkError> getSpotC_MarkError(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<Exam> getExamNameByNum(String str);

    Object getSchoolNameByNum(String str);

    List<Subject> getSubjectNameByNum(String str);

    Object getClassNameByNum(String str, String str2);

    Object getGradeNameByNum(String str, String str2);

    List<Class> gets_pclassNum(String str, String str2);

    List<Student> getStudent(String str, String str2, String str3, String str4);

    List<AjaxData> getAjaxSchoolList(String str, String str2);

    List<AjaxData> getAjaxGradeList(String str, String str2, String str3);

    List<AjaxData> getAjaxGradeList2(String str, String str2, String str3);

    List<AjaxData> getAjaxGradeList2_zy(String str, String str2, String str3);

    List<AjaxData> getAjaxClassList(String str, String str2, String str3, String str4);

    List<AjaxData> getAjaxSubjectList(Integer num, Integer num2, Integer num3);

    List<AjaxData> getAjaxSubjectList_zy(Integer num, Integer num2, Integer num3);

    List<AjaxData> getAjaxSubjectListForOE_zy(Integer num, Integer num2, Integer num3);

    List<AjaxData> getUserPositionSubjectList(String str, String str2, String str3);

    List getUserroleNum(String str);

    List<AjaxData> getUserPositionGradeList(String str, String str2, String str3, String str4);

    List<AjaxData> getAjaxExamList(String str);

    List<AjaxData> getAjaxExamList_zy(String str, String str2, String str3);

    List<Task> getAjaxQuestionNumList(Integer num, String str);

    List<Task> getAjaxQuestionNumList2(Integer num, String str);

    List<Define> getAjaxQuestionNum(String str);

    List<Define> getqNumList(String str, String str2);

    List<Task> getAjaxTaskList(Integer num, String str);

    List<Task> getAjaxTaskList2(Integer num, String str);

    List<Task> getTeacherAcgScore(Integer num, String str);

    List<MarkError> getMarkError(String str, String str2, String str3, String str4);

    List<Task> getAjaxFullScoreList(String str, String str2);

    void changeR(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    Integer deleteK(String str, String str2);

    List zscoreList(String str, String str2, String str3, String str4);

    @Transaction
    void updateSc(double d, String str);

    void updateScz(String str, String str2, String str3, String str4, String str5, String str6);

    void update_score(String str, String str2, String str3);

    void update_objectivescore(String str, String str2, String str3);

    void getUpdate_Chosen_update(String str, String str2);

    Float getfullScore(String str, String str2);

    List questionType_Out(String str, String str2, String str3) throws Exception;

    String questiontypeIn(String str, String str2);

    String getOptioncount(String str) throws Exception;

    String getDefine(String str);

    double countScore(String str, String str2, String str3, String str4);

    double countScore1(String str, String str2, String str3, String str4);

    List<User> getSpotCheckChild(String str, String str2, String str3);

    List<Classexam> getCorrectSchoolList_export(String str, String str2, String str3, String str4);

    List<Exampaper> getExamPaperNums(String str, String str2);

    String addObjectiveQuesionImageSample(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List getObjectiveSamples();

    Object getTemplateType(String str);

    byte[] getSampleImage(String str);

    Integer deleteFromRegSample(String str);

    Integer re_Recognized_Objective(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17);

    List getRegSampleById(String str);

    List<Reg_Th_Log> getRe_th_log(int i);

    Integer saveRe_th_log_description(String str, String str2);

    int yanz(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    int yanz1(Integer num, String str, String str2, Integer num2, String str3);

    List<Task> getQuesGroup(Integer num, String str);

    List<Task> qscoreta(Integer num, String str);

    List<Task> qscoreta2(Integer num, String str);

    List<Task> panfendetail(Integer num, String str);

    String getGroupInfo(Integer num, String str);

    @Transaction
    Integer clearUserQues(Integer num, String str, String str2, String str3, String str4, String str5);

    @Transaction
    void saveRemarkAndMarkError(Remark remark, MarkError markError, String str, String str2);

    List searchInQuesImage(String str, String str2, String str3, String str4, String str5, String str6);

    Integer addToClipErrorMethod(String str, String str2, String str3, String str4);

    Integer removeFromClipErrorMethod(String str, String str2, String str3);

    Integer batchAddClipError(String str, String str2, String str3, String str4, String str5, String str6);

    Integer batchRemoveClipError(String str, String str2, String str3, String str4, String str5, String str6);

    List reClipExamPaper(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List searchClipStuMethod(String str, String str2, String str3, String str4, String str5);

    List searchClipSuccess_failure_page(String str, String str2, String str3);

    void updateExampaper_countScore(String str, String str2, String str3);

    Exampaper getTotalScoreCount(String str, String str2, String str3);

    void getAnalysisscoreValue(String str);

    void updateTaskData(String str);

    @Cache(put = true)
    List gets_pques(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, float f, String str9, String str10);

    void clipTwo(String str, String str2, String str3, String str4, String str5, String str6) throws Exception;

    void missJudge(String str, String str2, String str3, String str4, String str5) throws Exception;

    String getscantype(String str);

    boolean updateScoreIsModify(String str, String str2);

    List<Task> getQuesScoreTask(String str, String str2, String str3, String str4);

    String getScoreId(String str, String str2);

    @Cache(put = true)
    List objecterranly(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List geterrorStudentList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    List<AjaxData> getStuExam(String str);

    @Transaction
    int editExamineeNum(String str, String str2, String str3, String str4);

    String setStuExampaperType(String str, String str2, String str3);

    byte[] getobjectscore(String str, String str2);

    Exampaper getExampaperInfo(String str, String str2, String str3);

    String getQuestionScoreByQuestionInfo(int i, String str, String str2);

    String isMerge(String str, String str2);

    List<MarkError> getQueList(String str, String str2, String str3);

    String getGroupNumByQueNum(String str);

    List<Map<String, Object>> getCaijueGroupList(String str, String str2);

    List<Map<String, Object>> getNotCaijue(String str, String str2);

    List<Map<String, Object>> getCaijueInfo(String str, String str2);

    String deleteExamineeNumOrNot(String str, String str2);

    int deleteExamineeNum(String str, String str2);

    String getweicaiqieNum(String str, String str2, String str3);

    String getweicaiqieNum1(String str);

    List<Task> getStatistic(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<Task> getHbcqGStatistic(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<Task> getAllStatistic(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List getSpotCheckSChild(String str, String str2, String str3, String str4, int i, String str5);

    List getHbcqGSpotCheckSChild(String str, String str2, String str3, String str4, int i, String str5);

    int getSpotCheckSCheckedCount(String str, String str2, String str3, String str4, String str5);

    int getHbcqGSpotCheckSCheckedCount(String str, String str2, String str3, String str4, String str5);

    List<Task> getSpotCheckSChecked(String str, String str2, String str3, String str4, int i, int i2, String str5);

    List<Task> getHbcqGSpotCheckSChecked(String str, String str2, String str3, String str4, int i, int i2, String str5);

    void deleteCheckedRecord(String str);

    @Transaction
    void directUpdateScoreLeq(String[] strArr, String str, String str2, String str3, String str4, String str5);

    List<Task> getAllQuestionNumList(String str);

    List<AjaxData> getTeachSubjectList(String str, String str2);

    List<AjaxData> getTeachGradeList(String str, String str2, String str3);

    List<AjaxData> getAjaxSchoolList(String str, String str2, String str3);

    List<AjaxData> getTeachSchool(String str);

    List<Averagescore> getAllAveScoreSet(String str, String str2, String str3, String str4, String str5, String str6);

    List<Averagescore> getClaAveScoreSet(String str, String str2, String str3, String str4, String str5, String str6);

    @Transaction
    void submitAveScoreSet(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Transaction
    void updateStatus(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getAveSetSchool(String str);

    List<AjaxData> getAllAveSetGrade(String str);

    List<AjaxData> getAveSetGrade(String str, String str2);

    List<AjaxData> getAveSetJie(String str, String str2);

    List<AjaxData> getAveSetExam(String str, String str2, String str3);

    Object getUserroleId(String str, String str2);

    List<AjaxData> getSpotCheckExam(String str);

    List<AjaxData> getSpotCheckSubject(String str, String str2);

    List<AjaxData> getSpotCheckGrade(String str, String str2, String str3);

    List<AjaxData> getSpotCheckQuestion(String str, String str2, String str3, String str4);

    List<AjaxData> getSpotCheckTeacher(String str);

    Map<String, Map<String, Map<String, Integer>>> getDataFormBigTableReg();

    Map<String, Map<String, Map<String, Integer>>> getDataFormBigTableExamina();

    Map<String, Map<String, Integer>> getDataFormBigTableRegChoose();

    Map<String, Map<String, Integer>> getDataFormBigTableExaminaChoose();

    Map<String, Map<String, Map<String, Integer>>> getDataFormBigTableTaskChoose();

    Map<String, Map<String, Map<String, Integer>>> getDataFormBigTableTaskChoose2();

    boolean getYueStatusFromGroup(String str);

    boolean getDeleteStatusGroup(String str);

    byte[] getImage(String str, String str2);

    String getLocation(String str);

    byte[] getImage2(String str, String str2);

    byte[] getImageByFullUrl(String str);

    List<QuestionGroup> ajaxQuestionList(String str);

    @Transaction
    void deleteScoreBiaoji(String str, String str2);

    void updateStudentTotalScore(String str);

    @Transaction
    void deleteMarkError(AwardPoint awardPoint);

    List<Map<String, Object>> getYuejuanZhiliangQuestionNum(String str, String str2, String str3);

    List<String> getZongheSubjectList(String str, String str2);

    @Transaction
    void addRemark(String str, String str2, String str3, String str4, String str5, String str6);

    List<Map<String, Object>> getAllGradeByExamSubject(String str, String str2);

    int getGroupYipancount(String str, String str2);

    String existZhuguanQues(String str);

    List<AjaxData> getUserNoPerExam(String str);
}
