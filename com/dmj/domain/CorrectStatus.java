package com.dmj.domain;

import java.io.Serializable;

/* loaded from: CorrectStatus.class */
public class CorrectStatus implements Serializable {
    private String id;
    private String examPaperNum;
    private String examNum;
    private String subjectNum;
    private String gradeNum;
    private String schoolNum;
    private String examnitionRoom;
    private String description;
    private String insertUser;
    private String insertDate;
    private String ext1;
    private String ext2;
    private String ext3;
    private String status;
    private String classNum;
    private String numStatus;
    private String appendStatus;
    private String numErrorStatus;
    private int notScan;
    private int nowScan;
    private int finishScan;
    private String gradeName;
    private String subjectName;
    private String examinationRoomNum;
    private String examinationRoomName;

    public CorrectStatus() {
    }

    public CorrectStatus(String id, String examPaperNum, String examNum, String subjectNum, String gradeNum, String schoolNum, String examnitionRoom, String description, String insertUser, String insertDate, String ext1, String ext2, String ext3, String status, String classNum, String numStatus, String appendStatus, String numErrorStatus, int notScan, int nowScan, int finishScan, String subjectName, String examinationRoomNum, String examinationRoomName, String gradeName) {
        this.id = id;
        this.examPaperNum = examPaperNum;
        this.examNum = examNum;
        this.subjectNum = subjectNum;
        this.gradeNum = gradeNum;
        this.schoolNum = schoolNum;
        this.examnitionRoom = examnitionRoom;
        this.description = description;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.status = status;
        this.classNum = classNum;
        this.numStatus = numStatus;
        this.appendStatus = appendStatus;
        this.numErrorStatus = numErrorStatus;
        this.notScan = notScan;
        this.nowScan = nowScan;
        this.finishScan = finishScan;
        this.subjectName = subjectName;
        this.examinationRoomNum = examinationRoomNum;
        this.examinationRoomName = examinationRoomName;
        this.gradeName = gradeName;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(String examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public String getExamNum() {
        return this.examNum;
    }

    public void setExamNum(String examNum) {
        this.examNum = examNum;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getExamnitionRoom() {
        return this.examnitionRoom;
    }

    public void setExamnitionRoom(String examnitionRoom) {
        this.examnitionRoom = examnitionRoom;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getExt1() {
        return this.ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return this.ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return this.ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getNumStatus() {
        return this.numStatus;
    }

    public void setNumStatus(String numStatus) {
        this.numStatus = numStatus;
    }

    public String getAppendStatus() {
        return this.appendStatus;
    }

    public void setAppendStatus(String appendStatus) {
        this.appendStatus = appendStatus;
    }

    public String getNumErrorStatus() {
        return this.numErrorStatus;
    }

    public void setNumErrorStatus(String numErrorStatus) {
        this.numErrorStatus = numErrorStatus;
    }

    public void setNotScan(int notScan) {
        this.notScan = notScan;
    }

    public int getNotScan() {
        return this.notScan;
    }

    public void setNowScan(int nowScan) {
        this.nowScan = nowScan;
    }

    public int getNowScan() {
        return this.nowScan;
    }

    public void setFinishScan(int finishScan) {
        this.finishScan = finishScan;
    }

    public int getFinishScan() {
        return this.finishScan;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public void setExaminationRoomName(String examinationRoomName) {
        this.examinationRoomName = examinationRoomName;
    }

    public String getExaminationRoomName() {
        return this.examinationRoomName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getGradeName() {
        return this.gradeName;
    }
}
