package com.dmj.service.examManagement;

import com.dmj.domain.AjaxData;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jxl.write.WritableSheet;

/* loaded from: ExportService.class */
public interface ExportService {
    void deleteGenerateFile(Map<String, String> map);

    void deleteGenerateFilebysubject(Map<String, String> map);

    void generateFile(Map<String, String> map);

    String download(Map<String, String> map);

    String downloadAllsub(Map<String, String> map);

    List<AjaxData> getExam(Map<String, String> map, String str);

    List<AjaxData> getGrade(Map<String, String> map, String str);

    List<AjaxData> getGrade_F(Map<String, String> map, String str);

    List<AjaxData> getSubject(Map<String, String> map, String str);

    List<AjaxData> getSubject_F(Map<String, String> map, String str);

    List<AjaxData> getFufen(Map<String, String> map, String str);

    List<AjaxData> getStudentType(Map<String, String> map);

    List<AjaxData> getXuanKeZuHe(Map<String, String> map);

    List<AjaxData> getSchool(Map<String, String> map);

    void removeExcelColumn(WritableSheet writableSheet, List<Integer> list, int i, Set set, Set set2);

    void removeOneSubExcelColumn(WritableSheet writableSheet, List<Integer> list, Set set, Set set2);

    void removeDetailExcelColumn(WritableSheet writableSheet, Set set, int i, Set set2, int i2);

    Integer getBaseStatistic();
}
