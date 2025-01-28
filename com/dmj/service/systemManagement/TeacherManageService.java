package com.dmj.service.systemManagement;

import com.dmj.domain.Class;
import com.dmj.domain.Grade;
import com.dmj.domain.School;
import com.dmj.domain.Subject;
import com.dmj.domain.Teacher;
import com.dmj.domain.User;
import com.dmj.domain.Userposition;
import com.dmj.util.msg.RspMsg;
import com.zht.db.Transaction;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/* loaded from: TeacherManageService.class */
public interface TeacherManageService {
    List<Teacher> listTeacher(String str, String str2, String str3, String str4, String str5, int i, int i2, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    @Transaction
    RspMsg addTeacher(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11) throws ParseException;

    @Transaction
    RspMsg register(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12) throws ParseException;

    Map<String, String> getSchoolMap();

    Map<String, String> getLimitSchoolMap(String str);

    Map<String, String> getLimitSchoolMap2(String str);

    Map<String, String> getUserSchoolMap(String str, String str2, String str3);

    Map<String, String> getUserSchoolMap2(String str, String str2, String str3);

    @Transaction
    void deleteT(String str, String str2, String str3);

    List CheckIsTeacherQu(String str);

    Teacher getAllByid(String str, String str2);

    RspMsg editTeacher(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    void subUserSchAuth(String str, String str2, String str3, String str4, String str5, String str6);

    void subUserSchAuthScan(String str, String str2, String str3, String str4, String str5, String str6);

    List<School> getUserManageSch(String str);

    List<School> getUserManageSchScan(String str);

    Integer count(String str, String str2);

    @Transaction
    void bacthdelteacher(String str, String str2, String str3, String[] strArr, String[] strArr2) throws Exception;

    List<Grade> getgradeByschool(String str);

    List<Class> getclassBygrade(int i, String str);

    List<Class> getlevelclassBygrade(String str, int i, String str2);

    boolean isLevelClass(String str, String str2);

    List<Subject> getsubjectlist();

    @Transaction
    void suposition(String str, String str2, String[] strArr, String[] strArr2, String str3, String str4, String str5);

    @Transaction
    void supositionnew(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    void tSubmit(String str, String str2, String str3, String str4, String str5, String[] strArr, String[] strArr2);

    @Transaction
    void tSubmitnew(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    Map<String, String> getGrBySch(String str);

    void gSubmit(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    Map<String, String> getsubjectMap();

    List<Userposition> infoGrByNum(String str);

    void studySubmit(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    void schSubmit(String str, String str2, String str3, String str4, String str5, String str6);

    List<Userposition> getByNum(String str);

    List<Userposition> getByNumSub(String str, String str2);

    Integer getcountByNum(String str);

    List<Userposition> getStudyByNum(String str);

    List<Userposition> getSchByNum(String str);

    void deleteByNumSub(String str, String str2);

    void deleteByNumT(String str, String str2);

    void deleteByNumG(String str, String str2);

    void deleteByNumStudy(String str, String str2);

    void deleteByNumSch(String str, String str2);

    List<Userposition> getType(String str, String str2);

    String getTid(String str, String str2);

    String getTname(String str, String str2);

    String getCName(String str, String str2, String str3, String str4);

    String getlevelCName(String str);

    String getSubName(String str);

    String getGradeName(String str, String str2);

    @Transaction
    void importT(File file, String str, String str2, String str3, Map map, String str4, String str5) throws IOException, ParseException;

    String getSubNum(String str, String str2);

    List<Userposition> getcNumList(String str, String str2, String str3);

    List<Userposition> getlevelcNumList(String str, String str2, String str3);

    Integer isExist(String str, String str2, String str3, String str4, String str5);

    List isExistRecord(String str, String str2, String str3, String str4, String str5, String str6);

    Integer isExistT(String str, String str2, String str3, String str4);

    Integer isExistG(String str, String str2, String str3);

    Integer isExistStudy(String str, String str2, String str3, String str4);

    Integer isExistSch(String str, String str2, String str3);

    Integer tcount(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    List<Teacher> listTeacher_out(String str);

    List<Teacher> listLimitTeacher_out(String str, String str2);

    List<Teacher> schoolList(String str);

    List<Teacher> schoolLimitList(String str, String str2, String str3);

    void delAndaddT(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    void delAndAddGrade(String str, String str2, String str3, String str4, String str5, String str6);

    void delAndAddStudy(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    void delAndAddSch(String str, String str2, String str3, String str4, String str5, String str6);

    void delAndAddClass(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<Userposition> teacher_List(String str, String str2, String str3, String str4, String str5);

    List<Userposition> teacher_newList(String str, String str2, String str3);

    List<Userposition> teacher_List_al(String str, String str2, String str3, String str4, String str5);

    List<Userposition> teacher_class_List(String str, String str2, String str3, String str4, String str5);

    List<Userposition> teacher_class(String str, String str2, String str3, String str4);

    String getStage(String str);

    Teacher getclassLeader(String str, int i);

    void delclassBySub(String str, String str2, String str3);

    Map getschoolList();

    void deleteTBySch(String str);

    void deleteLimitTBySch(String str, String str2);

    List querySchoolList();

    @Transaction
    Integer insertUserposition(Userposition userposition, int i, int i2, String str, String str2);

    String getNameByTypeAndValue(String str, String str2);

    @Transaction
    Integer delTeaPos(String str, String str2, String str3, String str4, String str5);

    boolean checkMobile(String str, String str2, String str3);

    Map<String, Object> getTeacherSchoolNum(String str);

    List<Map<String, Object>> getSchoolNumByUser(String str);

    String changeSchool_teacher(Teacher teacher, String str);

    String cancelUserPosition(String str);

    @Transaction
    void addNotice(String str, String str2, String str3, String str4);

    String getNoticeInfo(String str, String str2);

    String getNoteInfo(String str);

    void deleteNotice(String str, String str2);

    void deleteNote();

    List<Grade> getGradeListBySchool(String str);

    boolean isSchoolManager(String str);

    List<School> getAuthSchools(String str);

    List<String> getAuthSchoolIdList(String str);

    List<String> getschAuthSchoolIdList(String str);

    List<Map<String, Object>> getBeixuanTeacher(String str, String str2, String str3);

    List<Teacher> getTeacherList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    RspMsg checkTeacherFile(File file, String str, String str2, String str3, Map<String, String> map, String str4);

    @Transaction
    void importTeacher(File file, String str, String str2, String str3, Map<String, String> map, String str4) throws Exception;

    List<Grade> getAuthGradeListBySchool(String str, String str2);

    User getTeacherSchoolNumByUser(String str);

    List<Object[]> getRepeatedMobileList();

    String exportOutTeacherLeq(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15);

    String exportOutTeacherPost(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15);

    void addUserRole(String str, String str2, String str3);

    List<Map<String, Object>> getSchoolInfo(String str, String str2, String str3);

    List<Map<String, Object>> getLoginCountList(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    List<Map<String, Object>> getClassInfo(String str, String str2, String str3);

    List<Map<String, Object>> getGradeInfo(String str, String str2);

    void deleteUserposition(String str, String str2);

    @Transaction
    void resetTeaPw(List<Teacher> list, String[] strArr, String str);

    @Transaction
    void bacthdeluserposition(List<Teacher> list, String[] strArr, String str, String str2, String str3);

    Object getChangeSchool(String str);

    String getIsSchoolScanper(String str);

    String getUserName(String str);
}
