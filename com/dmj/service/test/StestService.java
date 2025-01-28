package com.dmj.service.test;

import com.dmj.domain.AjaxData;
import com.dmj.domain.Define;
import com.dmj.domain.Illegal;
import com.dmj.domain.Score;
import com.dmj.domain.Subdefine;
import java.util.List;
import java.util.Map;

/* loaded from: StestService.class */
public interface StestService {
    int getexampapernum(String str, String str2, String str3);

    List<Score> list(String str, int i, String str2, int i2, int i3, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13);

    List<Score> list_auth(String str, int i, String str2, int i2, int i3, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14);

    List<Map<String, Object>> list2(String str, int i, String str2, String str3, int i2, int i3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16);

    List<Map<String, Object>> list2_auth(String str, int i, String str2, String str3, int i2, int i3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17);

    List getoptionList(int i, String str, String str2, String str3, String str4);

    Integer getoptionCount(int i, String str, String str2);

    List<Define> getqNumList(int i, String str);

    List<Subdefine> getChooseQuestionList(int i, String str);

    String getNewQusetionNum(String str, int i, String str2, String str3, String str4);

    List<Score> getChooseQuestionDetailList(Map<String, String> map);

    List<Score> getAuthChooseQuestionDetailList(Map<String, String> map);

    Integer getChooseQuestionDetailNum(Map<String, String> map);

    Integer getAuthChooseQuestionDetailNum(Map<String, String> map);

    void che(String str);

    Integer co(int i);

    Integer zco(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15);

    Integer zco_auth(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16);

    void createImg(String str, List<byte[]> list) throws Exception;

    List<byte[]> allimg(String str);

    List<AjaxData> getChooseModifyItem(String str, String str2);

    void updateCdProofreadingStatus(String str);

    void updateObjsProofreadingStatus(String str);

    String updateExamplaceProofreadingStatus(String str, String str2, String str3, String str4);

    void updateIllProofreadingStatus1(String str);

    int getIllegalCollateCount(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    int getIllegalCollateCountall(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    int getAuthIllegalCollateCount(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10);

    int getAuthIllegalCollateCountall(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9);

    List<Illegal> getIllegalCollateData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, int i, int i2);

    List<Illegal> getIllegalCollateDataall(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i, int i2);

    List<Illegal> getAuthIllegalCollateData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, int i, int i2, String str10);

    List<Illegal> getAuthIllegalCollateDataall(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i, int i2, String str9);

    Integer getObjitemImgCount(int i, String str, String str2, String str3, String str4, String str5);

    List<Map<String, Object>> getObjitemImglist(int i, String str, String str2, String str3, String str4, int i2, int i3, String str5);

    Integer getObjitemImgCount_auth(int i, String str, String str2, String str3, String str4, String str5, String str6);

    List<Map<String, Object>> getObjitemImglist_auth(int i, String str, String str2, String str3, String str4, int i2, int i3, String str5, String str6);

    void updateSelected(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    void updateSelectedOfItem(String str, List<Map<String, Object>> list, String str2, String str3);

    Integer getillegalCollateIsproofreading(String str, String str2);

    void updateillegalCollateIsproofreading(String str, String str2);

    Integer getChooseQuestionIsproofreading(String str, String str2);

    void updateChooseQuestionIsproofreading(String str, String str2);

    Integer getXuanZeTiIsproofreading(String str, String str2, String str3, String str4);

    void updateXuanZeTiIsproofreadings(String str, String str2, String str3, String str4);
}
