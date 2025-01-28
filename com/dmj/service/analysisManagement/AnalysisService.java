package com.dmj.service.analysisManagement;

import com.dmj.domain.AjaxData;
import com.dmj.domain.Define;
import com.dmj.domain.Examlog;
import com.dmj.domain.Grade;
import com.zht.db.Cache;
import java.util.List;
import java.util.Map;

/* loaded from: AnalysisService.class */
public interface AnalysisService {
    String countIMIT(int i, String str, String[] strArr) throws Exception;

    boolean countSingleSubjectUploadIMIT(Integer num, Integer num2, Integer num3, String str);

    @Cache(deleteAll = true)
    String countSubIMIT(Integer num, Integer[] numArr, Integer[] numArr2, String str, String[] strArr, List<Examlog> list, String str2);

    boolean countSingleSubjectUploadIMIT_sub(Integer num, Integer num2, Integer num3, String str);

    boolean countKnowledgeData(Integer num, Integer num2, Integer num3, String str) throws Exception;

    boolean countAbilityData(Integer num, Integer num2, Integer num3, String str) throws Exception;

    boolean countQuestionTypeData(Integer num, Integer num2, Integer num3, String str) throws Exception;

    boolean countQuestionData(Integer num, Integer num2, Integer num3, String str) throws Exception;

    @Cache(deleteAll = true)
    String countAllSubjectInfo(Integer num, Integer num2, Integer num3, String str);

    boolean countFufen(Integer num, Integer num2, Integer num3, String str) throws Exception;

    List<AjaxData> getExam(String str, String str2, String str3);

    List<AjaxData> getHisoryExam(String str, String str2);

    List<AjaxData> getSubjectType(String str, String str2, String str3, String str4);

    List<AjaxData> getHistorySubjectType(String str, String str2, String str3, String str4);

    List<AjaxData> getSubjectTypePer_F(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getHistorySubjectTypePer_F(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getSubCompose(String str, String str2, String str3, String str4);

    List<AjaxData> getCompareExam(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getHistoryCompareExam(String str, String str2, String str3, String str4);

    List<AjaxData> getSubject(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<AjaxData> getSubject1(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<AjaxData> getHistorySubject(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getHistorySubject1(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getSubjectPer_F(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getSubjectPer_F1(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getHistorySubjectPer_F(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getHistorySubjectPer_F1(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getSchool(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getHistorySchool(String str, String str2, String str3);

    List<AjaxData> getHistorySchool2(String str, String str2, String str3, String str4);

    List<AjaxData> getArea(String str, String str2);

    List<AjaxData> getSchool2(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getSchoolPer_F(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getHistorySchoolPer_F(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getGrade(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getHistoryGrade(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> checkSchManage(String str);

    List<AjaxData> checkSchManageByUidAndSid(String str, String str2);

    List<AjaxData> getGradePer_F(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getHistoryGradePer_F(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getClass(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getClass1(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getHistoryClass(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getHistoryClass1(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getClassPer_F(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List<AjaxData> getHistoryClassPer_F(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<AjaxData> getStudent(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getHistoryStudent(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getGraduationType(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getHistoryGraduationType(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<AjaxData> getStuSourceType(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List<AjaxData> getHistoryStuSourceType(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<AjaxData> getStudent_Student(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getExam_Student(String str, String str2, String str3);

    List<AjaxData> getSubjectType_Student(String str, String str2, String str3, String str4);

    List<AjaxData> getSubject_Student(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getSubject_Student1(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getSchool_Student(String str, String str2, String str3, String str4);

    List<AjaxData> getGrade_Student(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getClass_Student(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List<AjaxData> getIslevel(String str, String str2);

    List<AjaxData> getHistoryStudent_Student(String str, String str2, String str3, String str4, String str5, String str6);

    List<AjaxData> getHistoryExam_Student(String str, String str2, String str3);

    List<AjaxData> getHistorySubjectType_Student(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getHistorySubject_Student(String str, String str2, String str3, String str4);

    List<AjaxData> getHistorySubject_Student1(String str, String str2, String str3, String str4);

    List<AjaxData> getHistorySchool_Student(String str, String str2, String str3, String str4);

    List<AjaxData> getHistoryGrade_Student(String str, String str2, String str3, String str4, String str5);

    List<AjaxData> getHistoryClass_Student(String str, String str2, String str3, String str4, String str5, String str6);

    void updateType(String str, String str2, String str3, String str4);

    String getExamIsHistory(String str);

    String getExamIsMoreSchool(String str);

    String[] gettype(String str);

    String authDefineAnswer(String str);

    Define authStudentAnswer(String str, String str2, String str3, String str4);

    List authQuestionAVGScore(String str, String str2, String str3, String str4);

    String getAllSchoolCounts(String str, String str2, String str3);

    String getReportIsFufen(String str, String str2, String str3);

    List<AjaxData> getReportComparativeExam(String str, String str2, String str3, String str4, String str5, String str6);

    List<Grade> getGradeByStage(String str);

    List<Map<String, Object>> getexportData(String str, String str2);

    List<Map<String, Object>> getpiciData(String str, String str2, String str3);
}
