package com.dmj.service.examManagement;

import com.dmj.domain.AjaxData;
import com.dmj.domain.Grade;
import com.dmj.domain.School;
import com.dmj.domain.Student;
import com.dmj.domain.Subject;
import java.util.List;
import java.util.Map;

/* loaded from: AjaxQueryService.class */
public interface AjaxQueryService {
    List<Student> queryStudentInfo(String str, String str2, String str3, String str4, String str5, String str6);

    List<Grade> getGradeBymoreExam(Map<String, String> map, String str);

    List<Subject> getSubjectByGrade(Map<String, String> map, String str);

    List<School> getSchoolBySubject(Map<String, String> map, String str);

    List<AjaxData> getGraduationType(Map<String, String> map, String str);

    List<AjaxData> getSourceType(Map<String, String> map, String str);
}
