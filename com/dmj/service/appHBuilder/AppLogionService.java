package com.dmj.service.appHBuilder;

import com.dmj.domain.Student;
import com.dmj.domain.User;
import com.dmj.domain.Userparent;
import com.dmj.util.app.AppUserInfo;
import com.dmj.util.app.MobileInfo;
import com.dmj.util.msg.RspMsg;
import com.zht.db.Transaction;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/* loaded from: AppLogionService.class */
public interface AppLogionService {
    Map<String, String> queryUser(String str, String str2, String str3, String str4) throws ParseException;

    Map<String, String> querySidUserFreePwd(String str, String str2, String str3, String str4);

    Map<String, String> querySidUser(String str, String str2, String str3, String str4) throws ParseException;

    Map<String, String> querySMSUser(String str, String str2, MobileInfo mobileInfo, String str3, String str4);

    Integer bindPhone(String str, String str2, MobileInfo mobileInfo, AppUserInfo appUserInfo);

    String getConfig(String str, String str2);

    Integer addUserParent(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, MobileInfo mobileInfo);

    @Transaction
    RspMsg register(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, MobileInfo mobileInfo);

    @Transaction
    RspMsg Tuijianrenregister(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, MobileInfo mobileInfo, String str10);

    Object isExistUser(String str, String str2, String str3);

    Integer findPassWord(String str, String str2, String str3, String str4, String str5, MobileInfo mobileInfo);

    Integer updatePassWord(String str, String str2, String str3, MobileInfo mobileInfo, AppUserInfo appUserInfo);

    RspMsg setNewPassword(String str, String str2, String str3, MobileInfo mobileInfo, String str4);

    String findMobileByUserName(String str, String str2);

    User getCodeUser(String str, String str2);

    List<Map<String, Object>> getStudentListByUsername(String str);

    List<Map<String, Object>> getAllSchool();

    List<Map<String, Object>> getAllGradeBySchoolNum(String str);

    List<Map<String, Object>> getAllClassBySchoolNumAndGradeNum(String str, String str2);

    List<Map<String, Object>> getAllstudentByclass(String str, String str2, String str3);

    List<Student> findStudentListByParam(String str, String str2, String str3, String str4);

    int findStudentListByPhone(String str, String str2, String str3);

    List<Userparent> findUserParentListByUserId(String str);

    Userparent findUserParentInfo(String str);

    Integer bindUserparent(Userparent userparent);

    void unBindStudent(List<Userparent> list);

    List<Userparent> findUserParentListByMobileAndIds(String str, String str2);

    List<Student> getParentsStudent(String str, String str2);

    RspMsg checkStudentStatus(String str);

    RspMsg isExistStudent(String str);

    void getPayUrl();
}
