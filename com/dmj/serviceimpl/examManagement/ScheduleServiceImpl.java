package com.dmj.serviceimpl.examManagement;

import com.dmj.daoimpl.examManagement.ScheduleDAOImpl;
import com.dmj.service.examManagement.ScheduleService;
import java.util.List;

/* loaded from: ScheduleServiceImpl.class */
public class ScheduleServiceImpl implements ScheduleService {
    ScheduleDAOImpl dao = new ScheduleDAOImpl();

    @Override // com.dmj.service.examManagement.ScheduleService
    public List<String> getOutDateOfExam() {
        return this.dao.getOutDateOfExam();
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public Integer moveData() throws Exception {
        return this.dao.moveData();
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public List<String> getExamPaperNumByExamnum(String examNum) {
        return this.dao.getExamPaperNumByExamnum(examNum);
    }

    public List<String> getTableNameByType(String type) {
        return this.dao.getExamPaperNumByExamnum(type);
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public String updateExamComplete() {
        return this.dao.updateExamComplete();
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public Integer optimizeTable(String path) {
        return this.dao.optimizeTable(path);
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public String deleteExamMessage(Integer days, Integer examNum) {
        return this.dao.deleteExamMessage(days, examNum);
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public String deleteExam(String examNum) {
        return this.dao.deleteExam(examNum);
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public boolean resetIdRecordValue() {
        return this.dao.resetIdRecordValue();
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public void deleteuserMessage() {
        this.dao.deleteuserMessage();
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public void deleteCornerInfo() {
        this.dao.deleteCornerInfo();
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public void deleteGenerateFile(String path) {
        this.dao.deleteGenerateFile(path);
    }

    @Override // com.dmj.service.examManagement.ScheduleService
    public void deleteLog(String path) {
        this.dao.deleteLog(path);
    }
}
