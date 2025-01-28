package com.dmj.service.teachingInformation;

import com.alibaba.fastjson.JSONArray;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Basegrade;
import com.dmj.domain.Baseinfolog;
import com.dmj.domain.Class;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Grade;
import com.dmj.domain.Levelclass;
import com.dmj.domain.School;
import com.dmj.domain.Student;
import com.dmj.domain.Subject;
import com.dmj.domain.SubjectCombine;
import com.dmj.domain.Teacher;
import com.dmj.domain.User;
import com.dmj.domain.Userposition;
import com.dmj.domain.Userrole;
import com.dmj.util.msg.RspMsg;
import com.zht.db.Transaction;
import java.util.List;
import java.util.Map;

/* loaded from: StudentService.class */
public interface StudentService {
    Object[] getStudent(Map<String, String> map, Map map2, String str);

    @Transaction
    void resetStuPw(List<Student> list, String str);

    Object[] getStudentLevel(Map<String, String> map, Map map2, String str);

    String getGradeIslevel(Map<String, String> map, Map map2, String str);

    Map<String, String> getGradesBySchool(String str);

    Map<String, String> getGradesBySchool1(String str, String str2);

    boolean isSchoolManager(String str);

    Map<String, String> getStuTypeByGradeNum(String str, String str2, String str3);

    Map<String, String> getSubjectMap(String str, String str2);

    Integer getLevelClassNumCount(String str, String str2, String str3, String str4);

    Integer getLevelClassNameCount(String str, String str2, String str3, String str4);

    String IsExistclass(String str, String str2, String[] strArr);

    String IsExistlevelclass(String str, String str2, String[] strArr, String str3, String str4);

    Map<String, String> getClassByGrade(String str, String str2, String str3, String str4);

    Map<String, String> getClassByGrade2(String str, String str2, String str3, String str4, String str5);

    Map<String, String> getClassByGrade1(String str, String str2, String str3, String str4, String str5);

    List<Object[]> getClassByGradeLeq(String str, String str2, String str3, String str4, String str5);

    Map<String, String> getLevelClassByGrade(String str, String str2, String str3, String str4, String str5);

    List<Object[]> getLevelClassByGradeLeq(String str, String str2, String str3, String str4, String str5);

    Map<String, String> getLevelClassByGrade1(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    Map<String, String> getStudentBySchoolAndGradeAndClass(String str, String str2, String str3);

    Map<String, String> getLevelClassBySubject(String str, String str2, String str3, String str4);

    Map<Integer, String> getSubject();

    Map<Integer, String> getGrade(String str);

    Map<String, String> getStuType();

    Map<String, String> getStuNoteType();

    Map<String, String> getStuSource();

    Map<String, String> getLevelClass(String str, String str2, String str3);

    String stuIsExist(String str, String str2);

    Student yzexaminationnumIsExist(String str, String str2);

    @Transaction
    void addStudent(Student student, User user, List<Levelclass> list, List<Baseinfolog> list2, Userrole userrole);

    Integer getstumaxcount();

    void updateid_record(int i);

    String getclasId(String str, String str2, String str3, String str4);

    Map<String, String> getLevelInfo(String str);

    Student getSchoolGradeInfo(String str);

    @Transaction
    void submitEditLevelStudent(String[] strArr, Map<String, String> map, List<Levelclass> list, String str, String str2, List<Baseinfolog> list2);

    @Transaction
    void editStudent(Student student, Baseinfolog baseinfolog);

    @Transaction
    void delLevelClassStu(String str, String str2, String str3);

    @Transaction
    void delLevelClassStudent(String str, String str2, String str3);

    @Transaction
    void delCommonClassStu(String str, String str2, String str3, String str4);

    Integer checkStuScoreCount(String str);

    RspMsg checkStuEnInfo(String str);

    @Transaction
    RspMsg batchDelStu(String str, String[] strArr, String str2, String str3, String str4, String str5, Map map, String str6, String str7, String str8);

    void deleteLevelStudent(String[] strArr, String str, String str2, String str3, String str4, String str5, Map map, String str6, String str7);

    Student getStuInfo(String str, String str2);

    Map<String, String> getExamType();

    Map<String, String> getSchoolMap();

    Map<String, String> getLimitSchoolMap(String str);

    Map<String, String> getLimitSchoolMapOfAll(String str);

    List<Subject> getXuankaoSubject();

    List<Integer> isOnlySubjectCombine(String str, String str2);

    List<Integer> isOnlySubjectCombineEdit(String str, String str2, String str3);

    List<Integer> isExistSubjectCombineByClass(String str);

    List<Integer> isExistDataBySubjectCombine(String str);

    List<SubjectCombine> getSubjectCombineList();

    List<SubjectCombine> getSubjectCombineisFirstList();

    List<SubjectCombine> getSubjectCombineNotFirstList();

    List<SubjectCombine> getSubjectCombineByParam(Map<String, String> map);

    @Transaction
    List editSubjectCombine(String str);

    @Transaction
    void insertFirstSubject(Map<String, String> map, String str);

    @Transaction
    void insertSubjectCombine(Map<String, String> map, String str);

    @Transaction
    void updateSubjectCombine(Map<String, String> map, String str);

    @Transaction
    void updateSubjectCombineByClass(Map<String, String> map, String str);

    @Transaction
    void delSubjectCombine(Map<String, String> map, String str);

    @Transaction
    void batchdelSubjectCombine(Map<String, String> map, String str);

    @Transaction
    void reSortSubjectCombine(Map<String, String> map, String str);

    Map<String, String> getSchoolMapByNum(String str);

    List<Grade> getGradeList(String str);

    List<Grade> getAllGradeList();

    List<Class> getClassList(String str, String str2, String str3, String str4);

    List<Levelclass> getLevelClassList(String str, String str2, String str3, String str4, String str5);

    Map<String, String> getOtherGrade(String str);

    @Transaction
    void addGrade(List<Grade> list);

    Integer checkjie(String str, String str2, String str3);

    List<AjaxData> checkjie2(String str, String str2);

    List<AjaxData> checkGradeJie(String str, String str2);

    Integer beforedelcheck(String str, String str2, String str3);

    void delGrade(String str, String str2, String str3, String str4, String str5);

    @Transaction
    void batchDelGrade(String str, String[] strArr, String str2, String str3, String[] strArr2);

    @Transaction
    void addClass(List<Class> list);

    @Transaction
    void addlevelclass(List<Levelclass> list);

    @Transaction
    void addClasslog(List<Baseinfolog> list);

    void delClass(String str, String str2, String str3, String str4, String str5, String str6);

    String delClassIsExistScore(String str, String str2, String str3, String str4, String str5, String str6);

    String delLevelClassIsExistScore(String str, String str2, String str3, String str4, String str5, String str6);

    String delClassIsExistStu(String str, String str2, String str3, String str4, String str5, String str6);

    String delLevelClassIsExistStu(String str, String str2, String str3, String str4, String str5, String str6);

    void delLevelClass(String str, String str2, String str3, String str4, String str5, String str6);

    void editClass(Class r1);

    Class editclassinfo(String str);

    Levelclass editLevelClassinfo(String str);

    @Transaction
    void batchDelClass(String str, String[] strArr, String str2, String str3, String str4, String str5);

    @Transaction
    String delClassIsExistScoreBatch(String str, String[] strArr, String str2, String str3, String str4, String str5);

    @Transaction
    String delClassIsExistStuBatch(String str, String[] strArr, String str2, String str3, String str4, String str5);

    @Transaction
    void batchDelLevelClsss(String[] strArr);

    @Transaction
    String delLevelClassIsExistScoreBatch(String str, String[] strArr, String str2, String str3, String str4, String str5);

    @Transaction
    String delLevelClassIsExistStuBatch(String str, String[] strArr, String str2, String str3, String str4, String str5);

    void exportExcel();

    String schoolNameByNum(String str);

    String gradeNameByNum(String str, String str2);

    Map<String, String> getClaTypeMap();

    Map<String, String> getstudentTypeMap();

    String getclaTypeName(String str);

    String getclaTypeNum(String str);

    @Transaction
    void upgrade(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Transaction
    void upgrade2(String str, String str2, String str3, String str4, String str5, String str6);

    List<Basegrade> upgradeList(String str, String str2, String str3);

    List<Basegrade> upgradeList2(String str, String str2);

    Integer checkallHighGrade(String str, String str2);

    Integer checkHighGrade(String str, String str2, String str3);

    List<Class> getClassListBySchGr(String str, String str2, String str3);

    @Transaction
    void submovecla(String str, String str2, String str3);

    String getgradeName(String str);

    @Transaction
    void ugrade(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Transaction
    void ugrade2(String str, String str2, String str3, String str4, String str5, String str6);

    int getStuOldGradeNum(int i, String str);

    List ExcelOut(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String[] strArr, String str11, String str12);

    List<Map<String, Object>> ExcelOut2(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String[] strArr, String str11, String str12);

    List ExcelOutLevel(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String[] strArr, String str13);

    List getDataSubjectType(String str, String str2);

    Map<String, String> getStudentlevelSubject(String str, String str2, String str3);

    List getSourceData(String str, String str2, String str3, String str4, String str5);

    List getSourceDataLevel(String str, String str2, String str3, String str4, String str5);

    Integer Student_count(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String[] strArr, String str12);

    List classNum_count(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String[] strArr);

    List classNum_countLevel(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String[] strArr);

    List studentType(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String[] strArr);

    List studentTypeLevel(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String[] strArr);

    List student_School();

    List studentLimitSchool(String str, String str2);

    Integer studentType_count(String str, String str2, String str3);

    List student_grade(String str, String str2, String str3, String str4, String str5, String[] strArr);

    List student_gradeLevel(String str, String str2, String str3, String str4, String str5, String[] strArr);

    Integer studentType_count_all(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String[] strArr);

    String getStuIdOldCNum(int i, String str, String str2);

    String getStuIdOldSchoolNum(String str);

    @Transaction
    void updatecNum(int i, String str, String str2, String str3, String str4);

    @Transaction
    void updatecschoolNum(String str, int i, String str2, String str3, String str4, String str5);

    @Transaction
    void updateschoolNumInfo(String str, int i, String str2, String str3, String str4, String str5);

    void dellevelstudent(String str);

    List getgradeListByschool(String str);

    List<Teacher> getteacherList(String str, String str2);

    void addGradeLeader(String str, String str2, String str3, String str4, String str5, String str6);

    Integer isExistGradeLeader(String str, String str2, String str3);

    String getclassNameByNum(String str, String str2, String str3);

    Integer isExistClassLeader(String str, String str2, String str3, String str4);

    void addClassLeader(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    String getTid(String str, String str2);

    Integer isOtherTeacher(String str, String str2, String str3, String str4);

    Teacher getTname(String str, String str2, String str3, String str4);

    String getClassNamebyTNum(String str, String str2, String str3);

    List<Subject> getsubList();

    List<Map<String, Object>> getsubRkjsList(String str, String str2, String str3);

    List<Map<String, Object>> getRkjsClassInfo(String str, String str2, String str3);

    List<Object> getTeacherList(String str, String str2, String str3, String str4);

    void addsubjectTeacher(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    Integer isExistsubjectTeacher(String str, String str2, String str3, String str4, String str5);

    String getsubjectName(String str);

    String getTNameBysubNum(String str, String str2, String str3, String str4, String str5);

    List<Teacher> getTnameBysubNumAndCNum(String str, String str2, String str3, String str4);

    String getTname1(String str, String str2, String str3);

    List<Teacher> gettNameBysubNumAndClassNum(String str, String str2, String str3, String str4, String str5);

    String getGradeLeaderNum(String str, String str2);

    Integer isExistStudyLeader(String str, String str2, String str3, String str4);

    Userposition getSubNameByTNum(String str, String str2, String str3, String str4);

    void addStudyLeader(String str, String str2, String str3, String str4, String str5, String str6);

    Teacher getStudyLeaderInfo(String str, String str2);

    Teacher getStudyLeaderBySubNumChange(String str, String str2, String str3);

    String checkSuperManager(String str);

    String getStuTypeName(String str);

    List studentSchoolList(String str, String str2);

    List<AjaxData> getClassList(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<Exampaper> getExamListStudent(String str, String str2);

    List<AjaxData> getStudentClassList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    @Transaction
    void addschool(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    String getschid(String str);

    int checkSchNum();

    Map<String, String> getLevelStudent(String str, String str2, String str3, String str4);

    Integer addStudentLevel(String str, String str2, String str3, String str4, String str5, Map map, String str6, int i, String str7);

    Integer delStuAndUserById(String str);

    Integer getStudentLevelCount(String str, String str2, String str3, String str4, String str5, Map map, String str6);

    Integer getStudentLevelCount2(String str, String str2, String str3, String str4, String str5, Map map, String str6, String str7, String str8);

    void updateLevelStudent(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    void updateLevelClass(String str, String str2, String str3, String str4, String str5, String str6);

    Integer getDelStudentSelLevelStudent(String[] strArr, int i, String str);

    Map<String, String> getStuTypeByGradeNum_new(String str, String str2);

    Map<String, String> getSchoolMap_new(String str, String str2);

    List<School> getUserSchoolMap_new(String str, String str2, String str3);

    List<Object> checkexamNum(String str, String str2, String str3, String str4, String str5, String str6);

    Map<String, String> getXuanKaoStatus();

    String getXuankaoStatusBySubject(String str);

    List<Map<String, Object>> getStudentByGradeAndClass(String str, String str2, String str3, String str4);

    Object bijiaoStudentId(String str);

    String getExamNameOne(String str);

    List<User> getStudentListJiaZhang(Map<String, String> map, String str);

    List<User> getJiaZhang_statis(Map<String, String> map, String str);

    String getJiaZhang_statisCount(Map<String, String> map, String str);

    String getStudentListJiaZhangCount(Map<String, String> map, String str);

    List<Map<String, Object>> getstudentInfoJiaZhang(String str, String str2);

    List<Map<String, Object>> ifStudentRelevance(String str);

    List<Map<String, Object>> ifStudentisExits(String str, String str2);

    String insertStudentJiaZhang(Map<String, Object> map);

    String updateStudentJiaZhang(Map<String, String> map);

    String deleteStudentJiaZhang(String[] strArr, String[] strArr2);

    String getIdFromUserparent(String str, String str2);

    String insertUserrole(String str, String[] strArr, Object[] objArr);

    int selectUseridsByUsername(String str, String str2);

    List selectIdFromStudent(String str);

    List<Map<String, Object>> exportStudentListJiaZhang(Map<String, String> map, String str);

    String updateJiazhang(String str, String str2, String str3);

    String changeSchool_student(Map<String, String> map, String str);

    String cancelLevelStudentInfo(String str);

    Object[] getChangeSchoolList(Map<String, String> map, String str);

    @Transaction
    String passChangeSchoolApply(String str, String str2, Map map, String str3) throws Exception;

    String rejectChangeSchoolApply(String str, String str2, String str3);

    List<Map<String, Object>> getApplySchoolInfo(String str, String str2);

    List<String> getPowerSchoolInfo(String str);

    String delChangeSchoolInfo(String str, String str2);

    @Transaction
    String batchDelChangeSchoolInfo(String[] strArr, String str);

    String getClassNumByClassId(String str);

    @Transaction
    String setRkjs(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    @Transaction
    String batchSetRkjs(String str, String str2, String str3, String str4, String str5, String str6);

    String deleteRkjs(String str, String str2, String str3, String str4);

    String deleteTeacherLeader(String str, String str2, String str3);

    String getGradeIslevel(String str, String str2);

    Object[] getStuExcepSub(Map<String, String> map, Map map2, String str);

    Object[] getStudentDetail(Map<String, String> map, String str);

    String changeSchoolDirectly(Map<String, String> map, String str);

    void delStuByScore(String str, String str2);

    @Transaction
    void updateShoufeiStudent(JSONArray jSONArray, String str, String str2);

    String getTheLatestTime();

    List<Map<String, Object>> getschools();

    List<Map<String, Object>> getsubjectTypes();

    List<Map<String, Object>> getfirstSubject();

    @Transaction
    String importgaokaoyifenyiduan(String str, String str2, String str3, String str4, List<List<Object>> list, String str5);

    Integer setgaokaoshangxian(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    Integer isExsit(String str, String str2, String str3, String str4, String str5);

    Integer delgaokaoyifenyiduan(String str, String str2, String str3, String str4, String str5);

    List<Map<String, Object>> getyifenyiduanData(String str, String str2, String str3, String str4, String str5);

    Integer resetYuzhi(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    Integer resetYuzhi2(String[] strArr);

    List<Map<String, Object>> getpiciData();
}
