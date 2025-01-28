package com.dmj.service.systemManagement;

import com.dmj.domain.AjaxData;
import com.dmj.domain.Area;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.Baseinfolog;
import com.dmj.domain.Data;
import com.dmj.domain.Examlog;
import com.dmj.domain.Log;
import com.dmj.domain.QuestionManger;
import com.dmj.domain.Subject;
import com.dmj.domain.TreeData;
import com.dmj.domain.User;
import com.dmj.domain.config;
import com.zht.db.Transaction;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;

/* loaded from: SystemService.class */
public interface SystemService {
    Integer saveOrUpdate(Area area) throws Exception;

    Integer save(Object obj) throws Exception;

    Integer update(Object obj) throws Exception;

    Object getOneByNum(String str, String str2, Class cls) throws Exception;

    List getObjectsByParam(Map<String, String> map, Class cls);

    Object deleteOneByNum(String str, String str2, Class cls) throws Exception;

    Area getAeraInfo() throws Exception;

    List<Data> getDataByType(String str);

    List getSchools();

    List getSchools(String str);

    String getexportschool(String str, String str2, String str3);

    List getexportschool2(String str);

    List getSchoolQuotaList(String str, String str2);

    @Transaction
    Integer addschoolquota(String str, String str2, String str3, String str4);

    @Transaction
    Integer delschoolquota(String str, String str2, String str3, String str4);

    @Transaction
    void updateEnforce(String str);

    String querySchoolIfDivide();

    String fenzu(String str);

    String checkgroupSchoolGroupInfo(String str);

    Map<String, Object> checkteacherquota(String str);

    Map<String, Object> checkteacherquotaGroup(String str, String str2);

    Map checkteachercount(String str);

    Map<String, Object> checkteachercountGroup(String str, String str2);

    Map checktizuzhang(String str);

    Map checktizuzhangGroup(String str, String str2);

    @Transaction
    void updateschoolquota(String str, String str2, String str3, String str4, String str5, String str6);

    @Transaction
    Integer divideequalquota(String str, String str2, String str3, String str4);

    List getdivideequalquota(String str, String str2, String str3, String str4);

    @Transaction
    void divideequalquotaGroup(String str, String str2, String str3, String str4);

    List getdivideequalquotaGroup(String str, String str2, String str3, String str4);

    List<Map<String, Object>> autoJisuanYingpanliang(String str, String str2, String str3, String str4);

    @Transaction
    String batchTeacherZhidingquota(String str, String str2, String str3, String str4, String str5, String str6);

    @Transaction
    void batchTeacherZhidingquotaGroup(String str, String str2, String str3, String str4);

    Map getSchoolGroupAllQuota(String str);

    String getEnforce();

    List getBasicLog(Baseinfolog baseinfolog, int i, int i2);

    Integer getAllBaseLogRowCount(Baseinfolog baseinfolog);

    List getExamLog(Examlog examlog, int i, int i2);

    Integer getAllExamLogRowCount(Examlog examlog);

    @Transaction
    Integer deleteSchool(String str);

    Map<String, String> getCurrentJie();

    List<Log> getAllLogs(Log log);

    int getAllLogsCount(Log log);

    Map<Data, List<Data>> getExamSetList(String str);

    @Transaction
    void updateDataTable(int i, String str, String str2);

    void updateImg_Set(int i, String str);

    String[] getImgIsDeleteMark();

    Map<Data, List<Data>> getExamSet8KList(String str);

    Integer setDBPath(String str);

    @Transaction
    void deleteTableImg(String str, String str2, String str3, String str4, String str5);

    Integer checkStuCount(String str);

    Integer checkStuNameCount(String str, String str2, String str3);

    Integer loadedei(String str, String str2, String[] strArr);

    void savePath(String str, String str2, String str3, String str4);

    void savetimesplit(String str, String str2, String str3, String str4);

    void savedeletetime(String str, String str2, String str3, String str4);

    Integer savebegintime(String str, String str2, String str3, String str4);

    void filesplitdelete(String str);

    String deletetimeList();

    String timesplitList();

    String begintimesplitList();

    String filepath(AwardPoint awardPoint);

    List begintimeList(AwardPoint awardPoint);

    List fileexits();

    Integer updatepath(String str, String str2, String str3);

    List savetimesplitList();

    Integer updatesavetimesplit(String str, String str2, String str3);

    List deleteFileList();

    Integer updatedeleteFile(String str, String str2, String str3);

    Object[] questionMangerList(QuestionManger questionManger, int i, String str);

    int getAllquestionMangerCount(QuestionManger questionManger, String str);

    List subjectList(QuestionManger questionManger);

    void delquestion(String[] strArr, String str, String str2);

    void addQuestiontype(String str, String str2, String str3, String str4, String str5);

    Integer exitsQuestiontype(String str, String str2, String str3);

    List statgeList(QuestionManger questionManger);

    List questionupList(String str, QuestionManger questionManger);

    void updateQuestiontype(String str, String str2, String str3, String str4, String str5, QuestionManger questionManger);

    String examNumexecute(String str, String str2, String str3);

    List examNumexecute2(String str, String str2, String str3);

    List<AwardPoint> getExam();

    List<AwardPoint> getSchool(String str);

    List<AwardPoint> getGrade(String str, String str2);

    List<AwardPoint> getSubject(String str, String str2);

    void deleteImg(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    String dataBasecpu(String str);

    void createsaveurl(String str);

    @Transaction
    void cteateimgepath(String str);

    String cteateimgepathyz();

    void deleteoneimg(String str, int i);

    void copyFolder(String str, String str2, String str3, String str4);

    String getParaFromConfig(String str, String str2);

    config getOpeFromConfig(String str);

    String getImageServerUri();

    String getIsLevelClass(String str, String str2, String str3, String str4);

    String getIsUpgrade(String str, String str2, String str3, String str4);

    List<AjaxData> getTopStasticItem() throws Exception;

    Integer delTopStasticItem(String str, String str2) throws Exception;

    @Transaction
    Integer changeTopName(String str, String str2, String str3) throws Exception;

    String getStasticMax();

    Integer stasticName_exist(String str);

    Integer addStasticTop(String str, String str2, String str3, int i);

    String getNewStatisticId();

    List<TreeData> getStasticItem(String str);

    Integer getNewSonNum(String str);

    @Transaction
    Integer saveStatistic(String[] strArr, String[] strArr2, String[] strArr3, String[] strArr4, String[] strArr5, String str, String str2) throws Exception;

    String isLeafOrNot(String str, String str2);

    List<AjaxData> getAllSchool2(String str) throws Exception;

    List<AjaxData> getAllSchool(String str, String str2) throws Exception;

    List<TreeData> checkLeaf(String str) throws Exception;

    String checkDuplicateAdd(String str, String str2, String str3, String str4, String str5);

    List getuserInfo(String str, String str2, ServletContext servletContext);

    @Transaction
    Integer changeStasticNodeName(String str, String str2, String str3, String str4, String str5, String str6) throws Exception;

    Map<String, String> getGradeMap();

    List<Subject> getTMList(String str) throws Exception;

    String getNewTotalId();

    Integer totalName_exist(String str);

    @Transaction
    String createTotalSubject(String str, String str2, String[] strArr, String str3);

    List<Subject> getOneTotalSubject(String str, String str2);

    void deleteOneTotalSub(String str, String str2, String str3);

    void updateTotalSubject(String str, String str2, String[] strArr);

    void updateTotalName(String str, String str2);

    @Transaction
    void delTotalItem(String str);

    Integer totalItemUsedOrNot(String str);

    @Transaction
    void delTotalItemAll(String str);

    void hideOrShowTotal(String str, String str2, String str3);

    byte[] splitimgurl(String str, String str2);

    String setAsBase(String str, String str2);

    List<String> getBaseLeiceng();

    List<Map<String, Object>> getLimbNode(String str);

    List<Map<String, Object>> getLimbNode2(String str);

    List<Map<String, Object>> getLimbNode2(String str, String str2);

    List<List> getteachUnitInfo_old(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    List<List> getteachUnitInfo(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, List<Map<String, Object>> list);

    List<List> getteachUnitInfo_AppShenSu(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, List<Map<String, Object>> list, String str10);

    Integer getUserPosition(String str, String str2, String str3, String str4);

    Integer getUserPositions(String str);

    List<Map<String, Object>> getSchoolByLeiceng(String str, String str2);

    String cancelBase(String str);

    String isBaseLeiceng(String str);

    boolean maxMACcheck(String str, String str2);

    boolean insertTeacherRecord(String str, String str2, String str3, String str4, String str5, String str6) throws SQLException;

    boolean delTeacherRecord(String str);

    String removeYudingSet(String str, String str2);

    String setLevel(String str, String str2, String str3);

    String cancelLevel(String str, String str2, String str3);

    @Transaction
    void deleteUserroleByExam(String str, String[] strArr);

    String getDengji(String str, String str2, String str3, String str4);

    @Transaction
    List<Map<String, Object>> getSchoolSetting(String str);

    @Transaction
    List<Map<String, Object>> getSchoolLoginSetting(String str);

    @Transaction
    Integer updateschoolsetting(int i, String str, int i2, String str2);

    @Transaction
    Integer updateschoolLoginsetting(int i, String str, int i2, String str2, String str3, String str4);

    @Transaction
    Integer addschoolsetting(int i, String str, int i2, String str2);

    @Transaction
    Integer addschoolLoginsetting(int i, String str, int i2, String str2, String str3, String str4);

    @Transaction
    void uploadLogo(String str, String str2);

    @Transaction
    void changeAll(String str, String str2, String str3);

    @Transaction
    void changeAllLogin(String str, String str2, String str3, String str4, String str5);

    @Transaction
    void updateYuguSanpinglv(String str, String str2, User user);

    List<Object> getUserAssignedSchoolList(String str);

    List<Map<String, Object>> getExamSchList();

    Integer getTeachUnitShowNum(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    Integer getTotalPage(String str, String str2, String str3);

    Integer submitSch(String str, String str2, String str3, String str4, String str5, String str6);

    Map<String, String> getschoolpermissionMap(String str, String str2);
}
