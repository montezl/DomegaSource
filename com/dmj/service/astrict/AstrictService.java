package com.dmj.service.astrict;

import com.dmj.domain.Astrict;
import com.zht.db.Transaction;
import java.util.List;
import java.util.Map;

/* loaded from: AstrictService.class */
public interface AstrictService {
    boolean isAstrictByPartNum(String str);

    Boolean isAstrictExam(String str, String str2);

    boolean isCoreManager(String str);

    List<Astrict> getDataByPartType(String str);

    List<Astrict> getDataByPartType1(String str);

    List<String> getAstrictGradeDataNowByPartType(String str);

    List<String> getAstrictExamDataNowByPartType(String str);

    List<Map<String, Object>> getAstrictLoginDataNowByPartType(String str);

    List<Map<String, Object>> getAstrictLoginDataNowByPartType2(String str, String str2);

    List<Map<String, Object>> getAstrictLoginDataNowByPartType3(String str, String str2);

    List<String> getAstrictGradeDataByPartType(String str);

    List<Map<String, Object>> getAstrictDataByPartType(String str);

    List<String> getAstrictExamDataByPartType(String str);

    List<Map<String, Object>> getAstrictLoginDataByPartType(String str);

    @Transaction
    void submitAstrict(String str, String str2, List<Map<String, Object>> list);

    @Transaction
    void submitAstrict2(String str, String str2, List<Map<String, Object>> list);

    int delAstrictById(String str);

    List<Map<String, Object>> getAllLimitedGrade();

    Object getUnfinishedExamByGrade(String str);

    String getGradeLastUpdateDate(String str);

    @Transaction
    String updatestudent(String str, String str2);
}
