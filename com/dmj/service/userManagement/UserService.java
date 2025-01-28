package com.dmj.service.userManagement;

import com.dmj.domain.AjaxData;
import com.dmj.domain.Exam;
import com.dmj.domain.Grade;
import com.dmj.domain.OnlineUser;
import com.dmj.domain.Resource;
import com.dmj.domain.Role;
import com.dmj.domain.School;
import com.dmj.domain.Schoolscanpermission;
import com.dmj.domain.Student;
import com.dmj.domain.Studentlevel;
import com.dmj.domain.Teacher;
import com.dmj.domain.User;
import com.dmj.domain.Userparent;
import com.dmj.domain.Userrole;
import com.dmj.util.msg.RspMsg;
import com.zht.db.Transaction;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;

/* loaded from: UserService.class */
public interface UserService {
    Integer update(User user);

    User getUserById(String str);

    Integer save(User user);

    User getUserByUsernameAndPwd(User user, String str);

    User getUserByUsernameAndPwd(User user, String str, String str2);

    User getUserByMobile(User user);

    User getUserByUsername(User user, String str);

    User getUserParentByUsernameAndPwd(User user);

    User getUserByNum(String str);

    User getUserByUserid(String str);

    String getUserByName(String str);

    List<User> getAllUser(User user, int i, int i2, String str, String str2);

    List<User> getAllUser(User user, int i, int i2, String str, String str2, String str3);

    List<User> getLimitUser(User user, int i, int i2, String str, String str2, String str3);

    Integer getAllUserCount(User user, String str);

    Object getOneByNum(String str, String str2, Class cls) throws Exception;

    List<Role> getAllRole();

    List<Role> getAllRole2();

    List<User> getUsersByRoleNum(String str);

    List<User> getAssignedUsers(String str, String str2, String str3, String str4);

    List<User> getUsersByType(String str, Integer num, Integer num2, String str2);

    List<User> getUsersByType2(String str, Integer num, Integer num2, String str2, String str3);

    List<Role> getRolesByUserNum(String str);

    Integer getRolesYjy(String str);

    @Transaction
    Integer addAndDeleteUser(List<String> list, List<String> list2, Userrole userrole);

    @Transaction
    Integer addAndDeleteUser2(List<String> list, List<String> list2, Userrole userrole, Integer num);

    @Transaction
    Integer addAndDeleteRole(List<String> list, List<String> list2, Userrole userrole);

    @Transaction
    Integer deleteUsers(String[] strArr, String[] strArr2, String str);

    Integer deleteOneUser(String str, String str2, String str3);

    Integer updatepasword(String str, String str2);

    boolean authenticationPasword(String str, String str2);

    void addRole(List<Role> list) throws Exception;

    void editRole(String str, String str2, String str3);

    Map<String, String> rolelist(String str, String str2) throws Exception;

    String username(String str);

    Map<String, String> rolelist11(String str, String str2, String str3) throws Exception;

    List rolelist22(String str, String str2, String str3, List list) throws Exception;

    String resname(String str);

    List queryreporthideAndshow(String str, String str2, String str3, List list) throws Exception;

    Object[] querySchoolAndClass(String str);

    void delOnlineuserBySessionid(String str);

    String selectRole(String str, Integer num, Integer num2);

    String selectExamName(String str);

    String selectGradeName(String str);

    String selectSubjectName(String str);

    String getexamPaperNum(String str, String str2, String str3);

    int checkIfSubjectDefineExist(String str);

    int checkIfGradeDefineExist(String str, String str2);

    int checkIfExamDefineExist(String str);

    List<String> getuserNum(String str, String str2, String str3);

    List<User> getUsersByuerNum(List list);

    String queryUserSex(User user);

    List querygrade(String str);

    List queryclass(String str, String str2);

    String selectuserRole(String str);

    void updateusername(User user, String str);

    String queryuseryes(String str);

    String queryuserparentyes(String str);

    String searchUserPhone(String str, String str2);

    List queryresource(String str, Integer num);

    List loginQueryUserName(String str, String str2, String str3, int i, int i2);

    Integer loginQueryUserNameCount(String str, String str2, String str3);

    @Transaction
    void deleterole(String str);

    int getSchoolNum();

    List<OnlineUser> getOnlineUserList();

    List<OnlineUser> getonlineuserList();

    List getRelation();

    List getallSchoolName();

    Integer save(Userparent userparent);

    Integer update(Userparent userparent);

    String getStudentidByid(int i);

    List getStudentNum(String str);

    User getUserByName1(String str);

    boolean authenticationPaswordByName(String str, String str2);

    Integer updatepaswordByName(String str, String str2);

    String getpublicip(String str);

    Integer saveuserrole(Userparent userparent);

    List getSchool(String str);

    Integer updateUserparentpw(String str, String str2);

    String getRank(String str);

    List getTeacherInfo(String str, ServletContext servletContext);

    Object[] getStudentBaseIofo(Map<String, String> map, Map map2, String str);

    Map<String, String> getStuType();

    Map<String, String> getStuNoteType();

    Student getStuInfo(String str, String str2);

    Map<String, String> getStuSource();

    List<School> getbaseSchool();

    List<Grade> getBaseGrade(String str);

    List getBaseClass(String str, String str2);

    List<Grade> getBaseGradeBySchoolNum(String str, String str2);

    List getBaseClassByGrade(String str, String str2, String str3);

    List getBaseStudent(String str, String str2, String str3);

    List getBaseExamByStudentId(String str);

    List<School> getbaseSchoolById(String str);

    Studentlevel getStudentDetails(String str);

    List<Userparent> getBaseUp(String str);

    List<Teacher> listTeacher(String str, String str2, String str3, String str4, String str5, int i, int i2, String str6, String str7);

    Map<String, String> getUserSchoolMap(String str, String str2);

    List<Map<String, Object>> getTeaMaxPermission(String str);

    @Transaction
    void addScanManager(Schoolscanpermission schoolscanpermission);

    @Transaction
    void delScanManager(Schoolscanpermission schoolscanpermission);

    List getLeftScanUsers(String str, String str2, String str3);

    List getRightScanUsers(String str, String str2, String str3, String str4);

    List getTcScaners(String str);

    int delScanManagers(String str);

    int addTeacherPhoneNum(String str, String str2);

    List getSchByStuId(String str);

    Object checkStuidAndStuname(String str, String str2);

    Integer addAndDeleteUser2New(List<String> list, Userrole userrole, Userrole userrole2, Integer num, String str, String str2);

    List<Student> getParentsStudent(String str);

    List<Map<String, Object>> get57UserListInfoByUserId(String str, int i);

    boolean couldUseOnBind(String str, String str2);

    int updateUserMobile(String str, String str2, String str3);

    String getParentPasswordByMobile(String str);

    String ifYueJuanPrivileged(String str, String str2);

    User getUserByTeacherNum(String str, String str2);

    User getUserByAccount(String str);

    User getUserByUsername(String str, String str2);

    boolean isAuthSchool(String str, String str2);

    User queryUserById(String str);

    List<User> getAllRoleSjtLeader(User user, int i, int i2, String str, String str2);

    List<User> getRoleSjtLeader(String str);

    List<Resource> getUserResource(String str);

    List<AjaxData> getTestCenterScannerList(String str, String str2);

    List<AjaxData> getSchoolScannerList(String str);

    Integer updateCloseSecondaryPositioning(String str, String str2, String str3);

    Integer updateismusttemplate(String str, String str2, String str3, String str4);

    List<Exam> getAssignedExamList(String str);

    @Transaction
    void quoteExamScanners(String str, String str2, String str3);

    String exportExamScanners(String str, String str2, String str3, String str4);

    void deleteScannersByExam();

    RspMsg checkScannerFile(File file, String str, String str2);

    @Transaction
    void importExamScanners(File file, String str, String str2, String str3);

    String getStudentIdByUserId(String str);

    void deleteAllUserFromOnlineUser();

    List<Map<String, Object>> getZikemuListByPexamPaperNum(String str);

    Map findTuiJianRen(String str);

    Map findZhuceTuijianren(String str);

    Map<String, Object> getlockMap(String str, String str2);

    Integer updateErrornum(String str, Integer num, String str2);

    Integer updateLocktime(String str, String str2, String str3);

    String logoutparent(String str);
}
