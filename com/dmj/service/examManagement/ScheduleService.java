package com.dmj.service.examManagement;

import com.zht.db.Transaction;
import java.util.List;

/* loaded from: ScheduleService.class */
public interface ScheduleService {
    List<String> getOutDateOfExam();

    Integer moveData() throws Exception;

    List<String> getExamPaperNumByExamnum(String str);

    String updateExamComplete();

    Integer optimizeTable(String str);

    @Transaction
    String deleteExamMessage(Integer num, Integer num2);

    @Transaction
    String deleteExam(String str);

    boolean resetIdRecordValue();

    void deleteuserMessage();

    void deleteCornerInfo();

    void deleteGenerateFile(String str);

    void deleteLog(String str);
}
