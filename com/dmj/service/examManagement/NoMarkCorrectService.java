package com.dmj.service.examManagement;

import com.dmj.domain.AjaxData;
import com.dmj.domain.ExamineeNumError;
import com.dmj.domain.Score;
import com.dmj.domain.TestData;
import com.dmj.domain.User;
import com.zht.db.Transaction;
import java.util.List;

/* loaded from: NoMarkCorrectService.class */
public interface NoMarkCorrectService {
    List<TestData> getNoMarkCorrectList(String str, int i, int i2, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12);

    Integer updateCorrectNumsStat(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List<Object> getExamRoomPageRank(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    Integer count(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16);

    Integer countOfCorner(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    @Transaction
    Integer modifyExamroomAndExamineeNum(String str, String str2, String str3, String str4, String str5, int i, String str6, String str7, String str8);

    void updateNumStat(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    Integer getNcount(String str, String str2, String str3, String str4);

    void setNoCorrectStat(String str, String str2, String str3, int i, String str4, String str5, String str6, String str7);

    @Transaction
    Integer deleteImg(ExamineeNumError examineeNumError, String str) throws Exception;

    boolean UpdateQuestionGroupTotalCount(String str);

    @Transaction
    void deleteTeaAppeal(String str, String str2, String str3) throws Exception;

    @Transaction
    void cliptwo(ExamineeNumError examineeNumError, String str);

    @Transaction
    void cliptwo_qtype(ExamineeNumError examineeNumError, String str, String str2, String str3, String str4, String str5);

    String gettemplateType(String str);

    String getdoubleFaced(String str);

    Integer checkclip(String str);

    List isExistTheExaminee(String str, String str2, String str3, String str4, String str5, String str6, int i, String str7, String str8, String str9, String str10, String str11);

    String getpaintMode(String str, String str2, String str3);

    List<AjaxData> getsubType(String str);

    String getstuType(String str, String str2, String str3, String str4);

    String getexamroomOrNot(String str, String str2, String str3);

    String getexamScanType(String str);

    String getScantype(String str);

    String getcrosspage(String str);

    String getpagelist(String str, String str2, String str3);

    List<Score> stulist(String str, int i, String str2, String str3, String str4, String str5);

    List getregIdList(String str, String str2, String str3, String str4);

    @Transaction
    void batchclip(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11);

    @Transaction
    void clip_objitem(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    List getregIdlist(String str, String str2);

    List getregIdlist2(String str, String str2);

    List getPanFenData(String str, String str2);

    List getCaiJueData(String str, String str2);

    int getCantrecognizedCount(String str, String str2, String str3, String str4);

    int getQueyeCount(String str, String str2, String str3, String str4);

    User getUserById(String str);
}
