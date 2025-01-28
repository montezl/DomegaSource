package com.dmj.service.questionGroup;

import com.dmj.domain.AjaxData;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Extragroupuser;
import com.dmj.domain.School;
import com.dmj.domain.Schoolgroup;
import com.dmj.domain.User;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.domain.vo.QuestionGroup_question;
import com.dmj.domain.vo.QuestionGroup_user;
import com.dmj.domain.vo.TestingcentreDis;
import com.dmj.util.msg.RspMsg;
import com.zht.db.Transaction;
import java.io.File;
import java.util.List;
import java.util.Map;

/* loaded from: QuestionGroupService.class */
public interface QuestionGroupService {
    List<AjaxData> getExam(String str, String str2);

    String getSubjectCount(String str, String str2);

    List<AjaxData> getSubject(String str, String str2);

    List<AjaxData> getSubject1(String str, String str2);

    List<AjaxData> getGrade(String str);

    Exampaper getexamPaperNum(String str, String str2, String str3, String str4);

    List getGroup(String str, String str2);

    List getAllQuestion(String str, String str2);

    int insertTestingDis(String str, String str2, String str3);

    List<QuestionGroup> getChooseQueandteacher(String str);

    List getQuestionNum(QuestionGroup_question questionGroup_question);

    List getXztQuestionNum(QuestionGroup_question questionGroup_question);

    List errorRateandfullsore(String str);

    int updateThreeWarn(String str, String str2);

    List restQuestion(String str, String str2, String str3, String str4, String str5, String str6);

    List getYuZhiList(String str, String str2, String str3, String str4, String str5);

    List<TestingcentreDis> getTestingcentreDis(String str);

    TestingcentreDis getTestingcentreDisOne(String str, String str2, String str3, String str4);

    void resetSaoMiaoStatus(String str);

    @Transaction
    String updateTestingcentreDis(String str, String str2, String str3, String str4);

    @Transaction
    void updateProofreadingStatus(String str, String str2, String str3, String str4, String str5);

    void updateChoose(String str);

    @Transaction
    List getrestQuestionNum(String str, String str2, int i);

    String getgroupDownCount(String str, String str2, String str3);

    @Transaction
    Integer updateGroupQuestion(QuestionGroup questionGroup, QuestionGroup_question questionGroup_question, String str);

    @Transaction
    Integer removeGroupQuestion(Integer num, String str, String str2, String str3, String str4);

    @Transaction
    Integer moveRestQuestion(QuestionGroup questionGroup, QuestionGroup_question questionGroup_question);

    @Transaction
    Integer deletegroup(QuestionGroup_question questionGroup_question);

    @Transaction
    void createGroup(QuestionGroup questionGroup, List<String> list, QuestionGroup_question questionGroup_question);

    List getGroupByName(Integer num, String str);

    String getGroupCount(String str, String str2);

    String getGroupNumber(String str, String str2);

    String getTaskNumber(String str, String str2, String str3);

    @Transaction
    void updateGroupName(String str, String str2, String str3);

    String queryUpdateGroupName(String str, String str2);

    String queryquestionNameone(Integer num, String str);

    List getPjUserList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    List getPjUserList2(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List<User> getUsersByRoleNum(String str, String str2);

    List<User> getUsersByGroupNum(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<User> querygroupuserz(String str, String str2, String str3, String str4, String str5);

    List<User> querygroupuserzByGroup(String str, String str2, String str3, String str4);

    @Transaction
    Integer addAndDeleteUser(List<String> list, List<String> list2, QuestionGroup_user questionGroup_user);

    @Transaction
    Integer addAndDeleteUserNew(List<String> list, QuestionGroup_user questionGroup_user, String str);

    String ishavenum(String str, String str2);

    List queryQuestionUser(QuestionGroup_user questionGroup_user);

    Map querychooselist(QuestionGroup_user questionGroup_user);

    @Transaction
    Integer removeGroupUser(String str, String str2, String str3);

    @Transaction
    Integer updateGroupUser(QuestionGroup_user questionGroup_user, String str);

    String queryMoveOne(Integer num, String str, String str2);

    @Transaction
    void questionLeader(String str, String str2, String str3, String str4, String str5);

    @Transaction
    Integer subjectLeader(String str, String str2, String str3, String str4, String str5);

    List getSchAndUserList(String str, String str2, String str3, String str4);

    List getGroupAndUserList(String str, String str2, String str3);

    String getMarkTypeByGroupNum(String str);

    @Transaction
    Integer addSubjectLeader(String str, String str2, String str3, String str4);

    @Transaction
    Integer cancelSubjectLeader(String str, String str2, String str3) throws Exception;

    @Transaction
    Integer delSubjectLeader(String str, String str2, String str3) throws Exception;

    QuestionGroup_user querySubjectLeader(String str, String str2);

    List queryQuestionLeader(String str, String str2, String str3, String str4);

    @Transaction
    Integer insertQuestiongroup_user(String str, String str2, String str3);

    List fetchTiNum(Integer num);

    Map<String, Map<String, String>> fetchTiNumchoose(String[] strArr);

    @Transaction
    String switchChoose(Map<String, String> map, String str);

    List queryQuestionLeader2(String str, String str2, String str3, String str4);

    List queryQuestionLeader2ByGroup(String str, String str2, String str3);

    @Transaction
    Integer updateMarkType(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Transaction
    Integer updateAssess(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    String gettaskCount(Integer num, String str, String str2);

    Integer createqms(Integer num, String str, String str2, String str3);

    @Transaction
    Integer createGroupfz(Integer num, String str, String str2, String str3, String str4);

    @Transaction
    Integer deleteGroupfz(Integer num, String str, String str2, String str3, String str4);

    @Transaction
    Integer updateGroupfz(Integer num, String str, String str2);

    void deletetask(Integer num, String str, String str2, String str3, String str4);

    @Transaction
    Integer deleteGroupfz_user(Integer num, String str, String str2, String str3);

    List getkgQuestionNum(String str);

    String getGCount(String str);

    List getTeacher(String str);

    List getfpzt(String str, String str2);

    @Transaction
    List getexampaperList(String str);

    List getsubjetTeacher(Integer num);

    List getGroupNumZ(String str);

    List bfExportQuestionUser(String str, String str2);

    List getMaxTitle(Integer num, Integer num2, String str);

    List<QuestionGroup_question> getQroupTeacherList(String str, String str2, String str3);

    List<QuestionGroup_question> getQroupSchoolList(String str, String str2, String str3);

    Map getQuestionGroupList(String str, String str2, String str3);

    List getGradeM(String str);

    String querySchoolcount();

    void importG(File file, String str, String str2);

    @Transaction
    String uploadQuUser(String str, String str2, String str3, String str4, File file, String str5, String str6) throws Throwable;

    @Transaction
    String uploadQuSchool(String str, String str2, String str3, String str4, File file, String str5, String str6) throws Throwable;

    String checkTaskHasCount(String str);

    List<Schoolgroup> assignRecordCheck(String str);

    @Transaction
    void deleteDistribute(String str);

    void updatequestiontotalnum(Integer num, String str, String str2, String str3, String str4);

    void deleteteacherimport(String str);

    void deletesessiontimetask(String str);

    @Transaction
    void xxthb(Integer num, String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Transaction
    void qxhb(Integer num, String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Transaction
    void daoruxxt(Integer num, String str, String str2, String str3, String str4, String str5);

    @Transaction
    void recoverytask(String str, String str2, String str3, String str4);

    @Transaction
    void resetPanfenByTi(String str, String str2);

    @Transaction
    void szpanfencount(String str, String str2, String str3, String str4, String str5);

    int szpanfencount1(String str, String str2, String str3, String str4, String str5, String str6);

    void updateUpperFloat(String str, String str2, String str3, String str4, String str5);

    List checkTotalNum(String str);

    List queryschool(String str, String str2, String str3);

    List querySubject(String str);

    List queryschoolByGroup(String str);

    List querygrade();

    List querysubject();

    List<Extragroupuser> querynote(String str);

    void updateforbidden(String str, String str2, String str3, String str4);

    @Transaction
    List<Map<String, String>> updateShensuStatus(String str, String str2, String str3, String str4);

    @Transaction
    void updateautoCommitforbidden(String str, String str2, String str3, String str4);

    @Transaction
    void updateautoCommitdefault(String str, String str2, String str3, String str4);

    @Transaction
    void updatecorrectForbid(String str, String str2, String str3, String str4);

    void updateYuejuanWay(String str, String str2, String str3);

    void updatejudgetype(String str, String str2);

    void updatejudgetype2(String str, String str2);

    Integer saomiao(String str, String str2);

    void updateChooseQuota(String str, String str2, String str3, String str4);

    void deleteChooseQuota(String str, String str2, String str3, String str4);

    Object huixiansaomiao(String str);

    List<School> getLeftSchool(String str, String str2);

    int addGroupSchool(Schoolgroup schoolgroup);

    int delGroupSchool(Schoolgroup schoolgroup);

    List<Schoolgroup> getSchoolGroupData();

    List<School> getGroupSchoolData(String str, String str2, String str3);

    int deleteSchoolGroup(String str);

    Object isExistSchGroupName(String str, String str2);

    int updateSchGroupName(String str, String str2);

    List<Map<String, List<Map<String, Object>>>> getYuejuan(String str, String str2, String str3);

    List<Map<String, Object>> getQuestionGroupSetList(String str, String str2, String str3);

    @Transaction
    String autoDistribute(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List<Map<String, Object>> getExamListInfo(String str, String str2);

    List<Map<String, Object>> getYuejuan(String str, String str2);

    Map<String, Object> getSchoolBeixuan(String str, String str2, String str3, String str4);

    List<Map<String, Object>> getSchoolByConditions(String str, String str2);

    @Transaction
    List<String> changeYuejuanyuan(String str, String str2, String str3, String str4, String str5, String str6);

    String deleteMember(String str, String str2, String str3, String str4);

    List<Map<String, Object>> getMemberSetTeacher(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<Map<String, Object>> getMemberSetTeacher(String str, String str2, String str3);

    String addOrRemoveMember(String str, String str2, String str3, String str4);

    List<Map<String, Object>> getGradeBySchool(String str, String str2);

    List<Map<String, Object>> getSubjectByGrade(String str, String str2, String str3);

    List<Map<String, Object>> getAutoSubjectInfo(String str, String str2, String str3);

    AjaxData getLimitTime(String str, String str2);

    String getAutoDistribuitType(String str, String str2, String str3);

    String yuchuliAutoDistribiutInfo(String str, String str2, String str3, String str4);

    String rewrightCount(String str, String str2);

    List<Map<String, Object>> getYifenpeiSchoolInfo(String str, String str2, String str3);

    List<Map<String, Object>> getYifenpeiListInfo(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<Map<String, Object>> getLeicengInfo(String str);

    List<Object> getYuejuanCountList(String str, String str2, String str3, String str4);

    String setYuding(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    String autoSetYuding(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    String exportYuejuanCountList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List<Map<String, Object>> getLeicengInfoByLeiceng(String str);

    String getPLeicengYudingCount(String str, String str2, String str3, String str4);

    String getPLeicengYuding(String str, String str2, String str3, String str4);

    String rewrightCount2(String str, String str2, String str3, String str4);

    String isManYuan(String str, String str2);

    String isManYuan2(String str, String str2);

    Map<String, String> getTaskCountByUser(Map<String, String> map, String str);

    Map<String, Map<String, String>> getTaskCountByUser1(Map<String, String> map, String str);

    List<AjaxData> querySubjectLeaderList(String str, String str2);

    File getRptExcelFile(String str, String str2, String str3);

    RspMsg existYuejuanyuan(String str, String str2, String str3);

    void updateShensuYuzhi(String str, String str2);

    Map<String, String> getSjtAndGrade(String str, String str2);

    @Transaction
    void resetAllSjt(String str, String str2);

    void exportYifenpeiYueJuan(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    void updateTempleteDisabled(String str, String str2, String str3, int i);

    List<Map<String, Object>> getSchoollevelData(String str);

    void insertInitSchoollevelData(String str, String str2);

    @Transaction
    void submitSchoollevelData(String str, String str2, List<Map<String, Object>> list);

    List<Map<String, Object>> getSchoollevelExamData(String str);

    @Transaction
    void quoteSchoollevelData(String str, String str2, String str3);

    void insertInitBuyuejuanschoolData(String str, String str2, String str3, String str4, String str5);

    List<Map<String, Object>> getBuyuejuanschoolData(String str, String str2, String str3, String str4);

    @Transaction
    void submitBuyuejuanschoolData(String str, String str2, String str3, String str4, List<Map<String, Object>> list);

    List<Map<String, Object>> getWorkReduceTableData(String str, String str2, String str3);

    @Transaction
    void submitWorkReduceTableData(String str, List<Map<String, Object>> list);

    String isOpenYuejuan(String str, String str2, String str3);

    List<Map<String, Object>> getQuestionGroupTableData(String str, String str2, String str3);

    @Transaction
    void submitQuestionGroupTableData(String str, List<Map<String, Object>> list, String str2);

    @Transaction
    String autoDistributeTeacherWork(String str, String str2, String str3, String str4, List<Map<String, Object>> list, List<Map<String, Object>> list2, String str5, double d, String str6, String str7, String str8) throws Exception;

    @Transaction
    void adjustSchoolTeacherWork(String str, String str2, String str3, String str4, String str5, List<Map<String, Object>> list, String str6);

    List<Map<String, Object>> getAllSubjects(String str, String str2);

    List<Map<String, Object>> getAdjustSchoolList(String str, String str2, String str3);

    List<Map<String, Object>> getSchoolQuestionGroupTableData(String str, String str2, String str3, String str4);

    List<Map<String, Object>> ajaxSchool(String str, String str2, String str3, String str4);

    List<Map<String, Object>> ajaxSchoolGroup(String str, String str2, String str3, String str4);

    List<Map<String, Object>> ajaxSchoolList(String str, String str2, String str3, String str4, String str5);

    int ajaxSchoolGroupStudent(String str, String str2, String str3, String str4);

    List<Map<String, Object>> getExamSchoolgroupSetTableData(String str, String str2, String str3, String str4, String str5);

    String isFenzuyuejuan(String str, String str2, String str3);

    @Transaction
    void submitExamSchoolgroupSetTableData(String str, String str2, String str3, List<Map<String, Object>> list, String str4);

    List<Map<String, Object>> getLeiCengSchool(String str, String str2);

    List<Map<String, Object>> getTeacherLeiCengSchool(String str, String str2, String str3);

    List<Map<String, Object>> getTeacherLeiCengSchoolScan(String str, String str2, String str3, String str4);

    @Transaction
    String importyuejuanteacher(List<List<Object>> list, String str, String str2);

    @Transaction
    void updateGroupWorknum(String str, String str2, String str3, String str4);

    List<Map<String, Object>> getSubjectLeaderList(int i, int i2);

    List<Map<String, Object>> getQuestionGroupDataList(int i, int i2);

    List<Map<String, Object>> getQuestionLeaderList(String str, String str2);

    List<Map<String, Object>> getQuestionLeaderListOfSchool(String str, String str2, String str3, String str4);

    RspMsg getBestgroupInfoMap(String str, String str2, String str3);

    String existBsetgroupLeader(String str, String str2);

    Integer getTestingcentre(String str);

    @Transaction
    String importYuejuanCountList(String str, String str2, String str3, String str4, List<List<Object>> list);
}
