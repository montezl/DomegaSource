package com.dmj.service.reportManagement;

import com.dmj.domain.AjaxData;
import com.dmj.domain.Class;
import com.dmj.domain.Examsetting;
import com.dmj.domain.Gradelevel;
import com.dmj.domain.IndexIntegral;
import com.dmj.domain.MyReport;
import com.dmj.domain.ReportParameter;
import com.dmj.domain.Resource;
import com.dmj.domain.RptHeader;
import com.dmj.domain.Student;
import com.dmj.domain.User;
import com.zht.db.Cache;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: ReportService.class */
public interface ReportService {
    Map<String, Object> getClassRankCompareHeader(ReportParameter reportParameter);

    List<Object> getClassRankCompareHeader2(ReportParameter reportParameter);

    @Cache(put = true)
    List maxClaScore(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12) throws Exception;

    List<Class> getStudentScoreClass(String str, String str2, String str3, String str4) throws Exception;

    List getStudentScore(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    Integer addCollect(MyReport myReport);

    List queryMyReport(String str, List list);

    void deleteMyReport(String str, MyReport myReport);

    Integer queryOne(MyReport myReport);

    Integer queryNull(String str, List list);

    List queryreportselect();

    void runReportEngineAndExport(String str, String str2, String str3, String[] strArr, String[] strArr2, String str4, String str5, String str6);

    List<Resource> getRptTypeName(String str);

    List<AjaxData> getSubject(String str, String str2);

    Map<String, String> getSubject1(String str, String str2);

    String getSchauthor(String str);

    @Cache(put = true)
    List getClaTotalRankList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<Student> selectStudent(String str, String str2, String str3, String str4, String str5, String str6);

    List getExpStudentScore(String str, String str2, String str3, String str4, String str5, String str6, String str7) throws Exception;

    List getUpperLineData_old(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Cache(put = true)
    List getUpperLineData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Cache(put = true)
    List getUpperLineData2(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List getDoubleLineData_old(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Cache(put = true)
    List getDoubleLineData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Cache(put = true)
    List getDoubleLineData2(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Cache(put = true)
    List getScoreList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Cache(put = true)
    List getStuQuesAnaly(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Cache(put = true)
    LinkedHashMap<Object, List<Map<String, Object>>> getNanDuDengJi(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Cache(put = true)
    LinkedHashMap<Object, List<Map<String, Object>>> getQuFenDuDengJi(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    @Cache(put = true)
    List<List<Object>> getshitinandufenbu(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    @Cache(put = true)
    List getQuesScore(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    String getjisuantype(String str, String str2, String str3);

    String idhavechoose(String str, String str2, String str3);

    @Cache(put = true)
    List getObjOption(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Cache(put = true)
    List getKnledgScore(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Cache(put = true)
    List getKnowScoreRank(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Cache(put = true)
    List getAblityScore(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Cache(put = true)
    List getAblityScoreRank(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Cache(put = true)
    List getQtypeScore(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Cache(put = true)
    List getQtypeScoreRank(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List getScoreSectionRank(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Cache(put = true)
    List getScoreSectionRank2(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18);

    List gettopStepHeader(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    @Cache(put = true)
    List gettopStepData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Cache(put = true)
    List gettopStepB10Data(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    @Cache(put = true)
    List getExamScoreView(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Cache(put = true)
    List getAllSubAvgView(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    List getMoreExamOnesjtScoreView(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Cache(put = true)
    List getStudentImproveFallAnaly(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    @Cache(put = true)
    List getTeachertopStudent(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    @Cache(put = true)
    List getTeacherScoreAnaly(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15);

    List getTeacherScoreAnaly2(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Cache(put = true)
    List getOneSubAnaly(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    @Cache(put = true)
    List<List<Gradelevel>> getAllSubAnaly(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15);

    @Cache(put = true)
    List getQtypeScoreDetail(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List getQtypeScoreDetail_excel(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    String rptFileFolder(String str, String str2, String str3, String str4, String str5);

    List getReportClass(String str, String str2, String str3, String str4, String str5, User user, String str6, String str7);

    List getReportGrade(String str, String str2, String str3, String str4, User user);

    List getReportClass1(String str, String str2, String str3, String str4, String str5, User user, String str6, String str7);

    List getObjOptionItem(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    List<RptHeader> getOptionStudentList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    void deleteExportFiles(String str, String[] strArr);

    void getNamesFromConff(String str);

    List authRole(String str, String str2, String str3);

    List getReportSutdent(String str, String str2, String str3, String str4, String str5, User user, String str6, String str7, String str8);

    List getTitle(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    int getAllT3Stu(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    int getAllT3StuFc(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    List getT3StuList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i, int i2, String str9, String str10);

    List getT3StuListFc(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i, int i2, String str9, String str10);

    List<Map<String, Object>> getStudent(String str, String str2, String str3, String str4, String str5, String str6);

    List getDefine(String str, String str2, String str3, String str4);

    Map<String, String> getchooseDefine(String str, String str2, String str3, String str4);

    Examsetting getExamsettingData(String str);

    String isYufabu(String str, String str2, String str3);

    @Cache(put = true)
    List getTeacherIntegral(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17);

    List getTeacherIndexIntegral(String str, String str2);

    Integer updateIndexIntegral(String str, String str2, String str3, String str4, String str5);

    List getTeacherIndexIntegralData(String str, String str2);

    List getTeacherIndexGrade(String str, String str2);

    IndexIntegral getTeacherIndex(String str, String str2);

    @Cache(put = true)
    Map<String, Object> getSankeyData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    Map<String, Object> getappealDate(String str, String str2, String str3);
}
