package com.dmj.service.awardPoint;

import com.dmj.cs.bean.ScoringPointRect;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.QuestionGroupInfo;
import com.dmj.domain.Remark;
import com.dmj.domain.RemarkImg;
import com.dmj.domain.Schoolgroup;
import com.dmj.domain.Task;
import com.dmj.domain.Teacher;
import com.dmj.domain.Userposition;
import com.dmj.domain.leq.Teacherappeal;
import com.dmj.domain.vo.Imgpath;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.domain.vo.Question_scorerule;
import com.dmj.domain.vo.TempAnswer;
import com.zht.db.Transaction;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;

/* loaded from: AwardPointService.class */
public interface AwardPointService {
    AwardPoint minquestionNum(AwardPoint awardPoint);

    Object[] tizhu(int i, String str);

    Object[] caijue(int i, String str);

    int GCCaiJue(String str, String str2, String str3);

    int skipQue(String str, String str2, String str3, String str4);

    int GCCaiJueCount(String str, String str2, String str3, String str4);

    List caiquestionNum(AwardPoint awardPoint);

    List chongquestionNum(AwardPoint awardPoint);

    List GetInsertUser(AwardPoint awardPoint);

    String caiImage(String str, AwardPoint awardPoint, String[] strArr);

    byte[] caitu(String str, String str2);

    byte[] caituPaperComment(String str);

    byte[] caituchong(String str, AwardPoint awardPoint, String str2);

    byte[] getScoreImage1(String str, int i);

    byte[] getScoreImage2(String str, AwardPoint awardPoint);

    List queryTag(AwardPoint awardPoint);

    Integer update(AwardPoint awardPoint);

    Integer updatet(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    Integer updates(String str, String str2, String str3, String str4);

    Integer updateTaskyi(String str, String str2, String str3, String str4, int i, AwardPoint awardPoint);

    Integer updateTask(String str, String str2, String str3, String str4, int i, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z);

    Integer updatescore(String str, String str2, String str3, String str4, String str5, AwardPoint awardPoint);

    Integer updateTag(String str, String str2, AwardPoint awardPoint);

    Integer insertTag(AwardPoint awardPoint);

    Integer tagCount(AwardPoint awardPoint);

    Integer biaoTag(AwardPoint awardPoint, String str);

    Integer delteTag(AwardPoint awardPoint, String str);

    Map<String, Object> ttizhuzb1(AwardPoint awardPoint, String str);

    List ttizhuzb(AwardPoint awardPoint, int i);

    List ttizhuzbzj(AwardPoint awardPoint, String str);

    Integer sumscore(AwardPoint awardPoint);

    List<AwardPoint> questionNum(int i, String str);

    AwardPoint adminTi(AwardPoint awardPoint, String str);

    Integer finsishping(AwardPoint awardPoint, String str) throws Exception;

    Integer countqueryping(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9) throws Exception;

    Integer countYibiao(String str, int i, String str2, String str3) throws Exception;

    List yibiao(AwardPoint awardPoint, String str, int i, String str2, String str3) throws Exception;

    Integer countchaYi(AwardPoint awardPoint, String str, int i, String str2, String str3) throws Exception;

    List chayi(AwardPoint awardPoint, String str, int i, String str2, String str3) throws Exception;

    AwardPoint completeTask2(AwardPoint awardPoint);

    List usercompleteTask2(AwardPoint awardPoint);

    List listusercompleteTask2(AwardPoint awardPoint);

    List gaiquestNumList(AwardPoint awardPoint);

    List groupavgcount(AwardPoint awardPoint);

    List groupavgmarkrrate(AwardPoint awardPoint);

    List groupmarkrrate(AwardPoint awardPoint);

    List useravgscore(AwardPoint awardPoint);

    AwardPoint groupsumcount(AwardPoint awardPoint);

    AwardPoint groupcompletecount(AwardPoint awardPoint);

    List groupavgscore(AwardPoint awardPoint);

    AwardPoint userNumbean(AwardPoint awardPoint);

    AwardPoint sumusercompleteTask(AwardPoint awardPoint);

    AwardPoint sumgroupavgcount(AwardPoint awardPoint);

    AwardPoint sumgroupavgmarkrrate(AwardPoint awardPoint);

    AwardPoint sumgroupmarkrrate(AwardPoint awardPoint);

    AwardPoint sumgroupavgscore(AwardPoint awardPoint);

    AwardPoint sumuseravgscore(AwardPoint awardPoint);

    AwardPoint completeTime(AwardPoint awardPoint);

    List questinNumList(AwardPoint awardPoint);

    List getTaskHaveJusityByUpdateTime(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10) throws Exception;

    List getTaskHaveJusity(int i, String str, String str2, String str3) throws Exception;

    List getGoTaskHaveJusity(int i, String str, String str2, String str3, String str4, int i2, String str5, String str6, String str7, String str8) throws Exception;

    List finishTaskById(int i, String str, String str2, String str3, String str4, String str5) throws Exception;

    Integer getTaskNo(int i, String str, String str2, String str3, String str4) throws Exception;

    Integer otherTeacherHasQum(int i, String str, String str2, String str3, String str4) throws Exception;

    Integer openTestDisQum(int i, String str, String str2, String str3, String str4) throws Exception;

    Integer closeTestDisQum(int i, String str, String str2, String str3, String str4) throws Exception;

    String getRestTask(int i, String str, String str2, String str3, String str4) throws Exception;

    List getTaskByUpdateTime(int i, String str, String str2, String str3, String str4, String str5) throws Exception;

    List getTask(int i, String str, String str2, String str3) throws Exception;

    Integer getTaskPersonYiPan(int i, String str, String str2, String str3) throws Exception;

    String scanStatus(int i, String str, String str2, String str3) throws Exception;

    Integer getShengXiaoTask(int i, String str, String str2, String str3) throws Exception;

    String cantrecognized(int i, String str, String str2, String str3) throws Exception;

    String getChooseNameType(String str) throws Exception;

    String getOtherChooseCount(String str, String str2, String str3) throws Exception;

    @Transaction
    List getRemark(Map<String, String> map) throws Exception;

    List getRemarkByUpdateTime(Map<String, String> map) throws Exception;

    Integer otherTeacherHasRemarkQum(Map<String, String> map) throws Exception;

    Remark getRemarkProgress(Map<String, String> map) throws Exception;

    List getRemarkTeacherInfo(Map<String, String> map, List list) throws Exception;

    Integer updateReamrkScore(Map<String, String> map) throws Exception;

    List getRemarkHaveJusity(Map<String, String> map) throws Exception;

    List getRemarkyipanscore(Map<String, String> map) throws Exception;

    List getRemarkHaveJusityByUpdateTime(Map<String, String> map) throws Exception;

    List getGoRemarkHaveJusity(Map<String, String> map) throws Exception;

    List finishRemarkById(Map<String, String> map) throws Exception;

    Integer countRemark(Map<String, String> map) throws Exception;

    Integer getTaskNOJustify(Map<String, String> map) throws Exception;

    Integer GCRemark(Map<String, String> map) throws Exception;

    String getTeacherReamarkStatus(Map<String, String> map) throws Exception;

    String getQuestionMode(Map<String, String> map) throws Exception;

    Integer GCRemarkAll() throws Exception;

    String queryremarkyipanllcount(Map<String, String> map) throws Exception;

    List ping1(int i, String str, String str2, int i2, String str3) throws Exception;

    List ping2(AwardPoint awardPoint, String str, String str2) throws Exception;

    List ping(AwardPoint awardPoint, String str, String str2) throws Exception;

    List ping3(AwardPoint awardPoint, String str, String str2) throws Exception;

    List noping(AwardPoint awardPoint, String str, String str2) throws Exception;

    AwardPoint markSetting(AwardPoint awardPoint);

    List groupNumList();

    int countchongpanfen(AwardPoint awardPoint);

    List scorePingList(String str);

    List listGroupNum(AwardPoint awardPoint);

    List taskuserList(AwardPoint awardPoint, String str);

    Integer biaoremarkTag(AwardPoint awardPoint);

    Integer delteremarkTag(AwardPoint awardPoint);

    Integer biaoremarkYi(AwardPoint awardPoint, String str);

    Integer delteremarkYi(AwardPoint awardPoint, String str);

    List caipan(AwardPoint awardPoint);

    List nocaipan(AwardPoint awardPoint);

    List caipan1(AwardPoint awardPoint);

    List yiscorePingList(AwardPoint awardPoint);

    List secondscorePingList(AwardPoint awardPoint);

    List yiscorePingList1(AwardPoint awardPoint);

    List secondscorePingList1(AwardPoint awardPoint);

    List finishcaipan(AwardPoint awardPoint);

    Integer finishcaipanCount(AwardPoint awardPoint);

    List finishchongpan(AwardPoint awardPoint);

    Integer finishchongpanCount(AwardPoint awardPoint);

    List yichongpan(AwardPoint awardPoint);

    Integer yichongpanCount(AwardPoint awardPoint);

    Integer countyichachong(AwardPoint awardPoint);

    List yichongchapan(AwardPoint awardPoint);

    Integer countyichacai(AwardPoint awardPoint);

    List yicaichapan(AwardPoint awardPoint);

    Integer caichong(AwardPoint awardPoint);

    Integer questionNumcai(AwardPoint awardPoint);

    List yicaipan(AwardPoint awardPoint);

    Integer yicaipanCount(AwardPoint awardPoint);

    List chongpanfen(AwardPoint awardPoint);

    List weichongpanfen(AwardPoint awardPoint);

    List chongpanfen1(AwardPoint awardPoint);

    Integer exam(int i, int i2, int i3);

    Integer updateChooseQuestion(AwardPoint awardPoint, String str);

    List choosequestion(AwardPoint awardPoint, String str);

    Integer updatechoosetask(AwardPoint awardPoint);

    Integer updatechooseremark(AwardPoint awardPoint);

    Integer updatechoosechongremark(AwardPoint awardPoint);

    @Transaction
    Integer updateremark(AwardPoint awardPoint);

    @Transaction
    void updateremarkdb(String str, String str2, String str3, String str4, int i, String str5, String str6, String str7, AwardPoint awardPoint);

    @Transaction
    Integer updateremarkexception(String str, String str2, String str3, String str4, String str5, AwardPoint awardPoint);

    Integer updatenewremark(AwardPoint awardPoint);

    @Transaction
    Integer insertmarkError(AwardPoint awardPoint);

    List countzb(AwardPoint awardPoint, String str);

    List countcai(AwardPoint awardPoint);

    List countcaicount2(AwardPoint awardPoint);

    List countchong(AwardPoint awardPoint);

    AwardPoint maxupdatetime(AwardPoint awardPoint);

    AwardPoint maxupdateremarktime(AwardPoint awardPoint);

    AwardPoint dangmaxTime(AwardPoint awardPoint);

    AwardPoint remarkmaxTime(AwardPoint awardPoint);

    List questinNumList1(AwardPoint awardPoint);

    AwardPoint sumList(AwardPoint awardPoint);

    AwardPoint quesrow(AwardPoint awardPoint);

    AwardPoint sumchongpan(AwardPoint awardPoint);

    List quertyquestion(AwardPoint awardPoint);

    List markerrorsize(AwardPoint awardPoint);

    @Transaction
    Integer deletemarkerror(String str);

    List tulist(AwardPoint awardPoint, String str);

    List tulistcai(AwardPoint awardPoint, String str);

    Integer deletecaitulist(AwardPoint awardPoint);

    Integer deletetulist(AwardPoint awardPoint);

    List<AwardPoint> getExam(String str);

    String getSubjectCount2(Integer num, Integer num2, String str);

    List<AwardPoint> getSubject(int i, int i2, String str);

    List<AwardPoint> getSubject2(Integer num, Integer num2, String str);

    List<Userposition> jzth(String str, String str2, int i);

    List<AwardPoint> getGrade(int i, String str);

    List<AwardPoint> getGrade2(Integer num, String str);

    List<AwardPoint> getYuejuanExam(String str);

    List<AwardPoint> getRemarkExam(String str);

    List<AwardPoint> getYuejuanGrade(Integer num, String str);

    List<AwardPoint> getRemarkGrade(Integer num, String str);

    List<AwardPoint> getYuejuanSubject(Integer num, Integer num2, String str);

    List<AwardPoint> getRemarkSubject(Integer num, Integer num2, String str);

    Integer saveimage(AwardPoint awardPoint);

    Integer listminrownum(int i, String str, String str2);

    List yichangzb(AwardPoint awardPoint);

    List yichangcaidu(AwardPoint awardPoint);

    List yichangchongdu(AwardPoint awardPoint);

    List teachList(AwardPoint awardPoint);

    List chooseQuestionNumList(String str, String str2, String str3, String str4, String str5, String str6);

    Integer firstrownumss(String str, AwardPoint awardPoint, String str2, int i);

    Integer rownumrecord(int i, String str, String str2, int i2, String str3, String str4);

    List delTarecord(int i, String str, String str2, int i2, String str3, String str4);

    Integer completeList(AwardPoint awardPoint);

    Integer completecount(AwardPoint awardPoint);

    Integer complettaskecount(AwardPoint awardPoint);

    List notaskList(AwardPoint awardPoint);

    List notaskdoubleList(AwardPoint awardPoint);

    Integer updateTscore(AwardPoint awardPoint);

    Integer onlineuser(AwardPoint awardPoint);

    List<AwardPoint> questionNumList(AwardPoint awardPoint);

    Integer deletenocomplTask(AwardPoint awardPoint, String str);

    void insertTask(AwardPoint awardPoint, String str);

    void doublefentask(int i, String str, String str2);

    List doubleList(AwardPoint awardPoint);

    List<AwardPoint> shitag(int i, int i2, int i3, AwardPoint awardPoint);

    List<AwardPoint> totalNumList(int i);

    void updategroupNum(String str, AwardPoint awardPoint);

    void updategroupNumexecute(AwardPoint awardPoint, String str);

    void insertquestionstepscore(AwardPoint awardPoint, String str, String str2, String str3);

    List insertquestionstepscoreList(AwardPoint awardPoint, String str);

    String querymaxstep(String str, String str2);

    void updatequestionstepscore(String str, String str2);

    void deletequestionstepscore(String str, String str2, String str3);

    void somedeletequestionstepscore(String str, String str2);

    void updateupquestionstepscore(String str, String str2, String str3);

    List checkstep(String str);

    List quertyaxis_xList(String str, String str2, String str3);

    List fullscoreList(int i, String str);

    String maxrownum(int i, String str, String str2);

    int usercomplete(String str, String str2, int i);

    List usercompleteList(String str, int i);

    int markType(int i, String str);

    List insertworkrecordList(int i, String str, String str2);

    int insertworkrecord(int i, String str, String str2, String str3, String str4);

    int updateworkrecord(int i, String str, String str2, String str3, String str4, String str5, String str6);

    int updateworkrecord1(int i, String str, String str2, int i2, String str3, String str4);

    List totalNumsession(int i, String str);

    List groupNumList(String str);

    void updategroupNum(String str, int i, int i2);

    Integer groupNummarkType(String str, int i);

    Integer onlineuser(String str);

    List<AwardPoint> score(String str, String str2);

    String scanType(String str);

    Integer shiping(String str);

    void updatefullscore(String str, String str2, String str3, String str4);

    void updateerrate(String str, String str2, String str3, String str4, String str5);

    @Transaction
    void updateerrateLeq(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, boolean z);

    @Transaction
    void addThreeData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, boolean z);

    Integer groupcount(String str, String str2, String str3);

    String querytaskwork(String str, String str2, String str3);

    String groupworkcount(String str, String str2, String str3);

    List groupscorecount(String str, String str2, String str3);

    Map allgroupworkcount(String str, String str2);

    Map groupavgcount(String str, String str2);

    Object[] queryworklv(String str, String str2, String str3, Double d);

    Object[] workjindu(String str);

    List queryquestion(String str, String str2);

    String questioncount(String str, String str2);

    void csljc(String str, String str2);

    String getweifenfacount(String str, String str2);

    String getszwork(String str, String str2, String str3);

    List querybjda(String str, String str2, String str3, int i);

    String querybjdacount(String str, String str2, String str3, int i);

    String queryquota(String str, String str2, String str3);

    Imgpath queryimgurl(String str);

    void createtempanswer(TempAnswer tempAnswer);

    List getyipanscore(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10) throws SQLException;

    List finishTasklist(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10) throws SQLException;

    List getQuestionNumList(String str);

    String queryyipanllcount(String str, String str2);

    String queryquestionNumhb(String str);

    String queryteacherteakcount(String str, String str2);

    String queryjudgetype(String str);

    void resettingWorkrecord(String str, String str2);

    Integer deleteCpMarkerror(String str);

    List<Question_scorerule> getScoreRule(String str);

    String getYueJuanWay(String str, String str2);

    String getGroupTel(String str, String str2, String str3);

    String getGroupInfo(String str, String str2);

    String getQuestionStat(String str, String str2);

    QuestionGroup getQuestionAutoCommitInfo(String str, String str2);

    QuestionGroup groupInfo(String str, String str2);

    boolean getYueJuanStatus(String str);

    List getLeader(String str, String str2);

    String getEnforce();

    List<Task> getPanFenTableList(Map<String, String> map, String str);

    Map getReferenceNum(Map<String, String> map, String str);

    List getNoOpenQuestionGroupList(Map<String, String> map, String str);

    List getNoOpenTestingCentreList(Map<String, String> map, String str);

    List<Map<String, Object>> getTeacherWorkStatus(Map<String, String> map, String str);

    List<Map<String, Object>> getTeacherWorkStatus2(ServletContext servletContext, Map<String, String> map, String str);

    String exportTeacherWorkStatus(Map<String, String> map, String str, String str2, String str3, String str4);

    String exportTeacherWorkStatus2(ServletContext servletContext, Map<String, String> map, String str, String str2, String str3, String str4);

    List<Map<String, Object>> getSubjectInfo_wy(String str, String str2);

    List<Map<String, Object>> getGradeInfo_wy(String str);

    List<Map<String, Object>> getGroupNumInfo(String str, String str2, String str3);

    List<Map<String, Object>> getSchoolInfo(String str, String str2, String str3, String str4, String str5, String str6);

    List<Map<String, Object>> getSchoolInfo2(String str, String str2, String str3, String str4, String str5, String str6);

    List remarksize(String str);

    Integer deleteremark(int i);

    Integer reMark2(String str, String str2, String str3, String str4, String str5);

    QuestionGroupInfo getQuestionGroupInfo(String str, String str2, ServletContext servletContext);

    QuestionGroupInfo getYingPanGroupInfo(String str, String str2);

    List<Object[]> getRunningGroupListInfo();

    @Transaction
    void stealQuestion(String str, String str2, String str3, String str4, String str5);

    @Transaction
    void dispatcherTask(Map<String, Object> map);

    List<Teacherappeal> getAppealList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<Map<String, String>> getShenSuDataList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    String exportAppealList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List<Teacherappeal> getAppealDetailData(String str, String str2, String str3, String str4);

    List<Map<String, String>> getShenSuDetailData(String str, String str2);

    String getSchoolNumByUserId(String str);

    String getSchoolGroup();

    List<Schoolgroup> getSchoolGroupList(String str, String str2, String str3);

    String getCurrGradeNumStatus(String str, String str2);

    String getRestrict(String str, String str2, String str3, String str4);

    String getStudentType(String str, String str2, String str3, String str4);

    String getEsAverageScore(String str, String str2, String str3, String str4, String str5);

    String getUserPosition(String str, String str2, String str3, String str4);

    List<Teacher> getMoreTeacherList(String str, String str2, String str3);

    Map getScoreRuleList(Map<String, String> map, String str);

    List<ScoringPointRect> getScoringPointList(List<AwardPoint> list);

    List<RemarkImg> getRemarkImgJson(String str, String str2);

    @Transaction
    String updateScoreArr(Map<String, String> map);

    @Transaction
    String updateRemarkByArr(Map<String, String> map);

    @Transaction
    void updateIsFinished(String str, String str2, String str3);

    @Transaction
    Integer yiPanIsXiaoyuQuota(String str, String str2, String str3);
}
