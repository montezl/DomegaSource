package com.dmj.service.examManagement;

import com.dmj.domain.Define;
import com.dmj.domain.Exampaper;
import com.dmj.domain.RegExaminee;
import com.dmj.domain.Score;
import com.zht.db.Transaction;
import java.util.List;
import java.util.Map;

/* loaded from: QuestionNumListService.class */
public interface QuestionNumListService {
    List<Map<String, Object>> list(String str, int i, String str2, int i2, int i3, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18);

    List<Score> list2(String str, String str2, String str3, int i, int i2, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List<RegExaminee> getStuRegList(String str, String str2, String str3, String str4, String str5);

    List<String> list2dt(String str, String str2);

    List<Score> getstudent(String str, int i, String str2, int i2, int i3, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List<Score> his_getstudent(String str, String str2, String str3, String str4, String str5, String str6);

    String getsubjectName(String str);

    List<Score> list_clipError(String str, String str2, String str3, int i, int i2, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    int getexampapernum(String str, String str2, String str3);

    List<Define> getqNumList(int i, String str);

    @Transaction
    void updateScore(String str, String str2);

    Integer getCount(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18);

    Integer getCountClipError(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List<Score> his_list2(String str, String str2, String str3, int i, int i2, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    List<Define> getSwitchQues(String str, String str2, String str3, String str4, String str5);

    @Transaction
    int modifyQuesNum(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    int modifyQuesNum_allques(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, Integer num, String str9, boolean z, boolean z2, String str10);

    int modifyQuesNum_small(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, Integer num, String str12, boolean z, boolean z2, String str13);

    Integer updateChooseRecord(String str, Integer num, String str2, String str3, String str4);

    boolean ModifyChooseNameInfo(Map<String, String> map);

    List isCrossPage(String str, Integer num);

    List updateAllChildQues(String str, String str2, String str3, Integer num);

    Integer modifyObjectieveSwithAB(String str, String str2, Integer num, String str3);

    List<Map<String, Object>> list_Teacher(String str, int i, String str2, int i2, int i3, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17);

    String appeal(Map map);

    String reject(String str, String str2, String str3);

    String modify(String str, String str2, String str3);

    String cancelAppeal(String str);

    String getLoginRealName(String str);

    List<Map<String, Object>> getAppealDealList();

    List<Map<String, Object>> getSubjectInfo(String str, String str2);

    List<Map<String, Object>> getSubjectInfo3(String str, String str2, String str3);

    List<Map<String, Object>> getSubjectInfo4(String str, String str2);

    List<Map<String, Object>> getSubjectInfo1(String str, String str2);

    List<Map<String, Object>> getExamInfo(String str);

    List<Map<String, Object>> getExamInfo1(String str);

    List<Map<String, Object>> getExamInfo3(String str, String str2);

    List<Map<String, Object>> getExamInfo4(String str, String str2);

    List<Map<String, Object>> getGradeInfo(String str, String str2, String str3);

    List<Map<String, Object>> getGradeInfo1(String str, String str2, String str3);

    List<Map<String, Object>> getGradeInfo3(String str, String str2);

    List<Map<String, Object>> getGradeInfo4(String str, String str2, String str3);

    List<Map<String, Object>> getListInfo3(String str, String str2, String str3, String str4);

    String appealAssist(String str, String str2, String str3, String str4, String str5);

    String cancelAssist(String str, String str2, String str3, String str4, String str5);

    List<Map<String, Object>> getQNumInfo(String str, String str2, String str3, String str4, String str5);

    List<Map<String, Object>> getLeaderQNumInfo(String str, String str2, String str3, String str4);

    List<Map<String, Object>> appealDealListInfo(String str, String str2, String str3, String str4, int i, int i2, String str5, String str6, String str7);

    List appealDealListCount(String str, String str2, String str3, String str4, String str5, String str6);

    String ifExistAppealData(String str, String str2, String str3);

    List<Map<String, Object>> getSchoolInfo(String str, String str2, String str3, String str4, String str5);

    List<Map<String, Object>> getClassInfo(String str, String str2, String str3, String str4, String str5);

    List<Map<String, Object>> getStudentInfo(String str, String str2, String str3, String str4, String str5, String str6);

    List<Map<String, Object>> getQuestionInfo(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<Map<String, Object>> appealListInfo(String str, String str2, String str3, String str4, int i, int i2, String str5, String str6, String str7, String str8, String str9, String str10);

    List<Map<String, Object>> assistListInfo(String str, String str2, String str3, String str4, String str5);

    Integer getTeacherAppealCount(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List<Map<String, Object>> getTeacherAppealCountInfo(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<Map<String, Object>> getDetailTeacherAppealCountInfo(String str, String str2, String str3, String str4);

    String exportTeacherAppealCountInfo(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Transaction
    String agreeAssist(String str, String str2, String str3, String str4, String str5, String str6);

    @Transaction
    String rejectAssist(String str, String str2, String str3);

    String getAppealDealDate(String str, String str2, String str3);

    List<Map<String, String>> getExamAndGraAndSubData();

    List<Map<String, Object>> getexamList();

    List<Map<String, Object>> getgradeList(String str);

    List<Map<String, Object>> getstudentTypeList(String str, String str2);

    List<Map<String, Object>> getxuankezuheList(String str, String str2, String str3);

    List<Map<String, Object>> getsubjectList(String str, String str2, String str3, String str4);

    List<Map<String, String>> getQNumData(String str, String str2, String str3);

    List<Map<String, String>> getTcAndErList(String str, String str2, String str3);

    List<Map<String, Object>> getStuRegDataByTeachUnit(Exampaper exampaper, String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<Map<String, Object>> getStuRegDataByExamroom(Exampaper exampaper, String str, String str2, String str3);

    List<Map<String, Object>> getStuQnumDataByTeachUnit(Exampaper exampaper, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<Map<String, Object>> getStuQnumDataByExamroom(Exampaper exampaper, String str, String str2, String str3, String str4);

    List<Map<String, Object>> getQueAndAnsList(String str);

    String getShensuYuzhi(String str);

    int getQuesTotalCount(int i);

    List<Map<String, Object>> getTishuBuquanData(int i, String str, int i2);

    List<Map<String, Object>> getDuotiData(int i, String str);

    List<Map<String, Object>> getTiChongfuData(int i, String str);

    List<Map<String, Object>> getStuAllQuesList(String str, String str2);

    @Transaction
    void deleteStuOneQuestion(String str, String str2);
}
