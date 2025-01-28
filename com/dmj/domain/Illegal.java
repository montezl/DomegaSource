package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Illegal.class */
public class Illegal implements Serializable {
    private String id;
    private String regId;
    private String errorPaperNum;
    private Integer examPaperNum;
    private String studentId;
    private String type;
    private Integer schoolNum;
    private String insertDate;
    private String insertUser;
    private String description;
    private String ext1;
    private String ext2;
    private String ext3;
    private String batch;
    private String score;
    private String updateUser;
    private String updateDate;
    private String exampaperType;
    private String tag;
    private String status;
    private String isModify;
    private String examinationRoomNum;
    private String testingCentreId;
    private String studentName;
    private String className;
    private String examineeNum;
    private String studentNum;
    private String subjectNum;
    private String subjectName;

    public Illegal() {
    }

    public Illegal(String errorPaperNum, String studentId, String type, String insertDate, String insertUser) {
        this.errorPaperNum = errorPaperNum;
        this.studentId = studentId;
        this.type = type;
        this.insertDate = insertDate;
        this.insertUser = insertUser;
    }

    public Illegal(String errorPaperNum, String studentId, String type, Integer schoolNum, String insertDate, String insertUser, String description, String ext1, String ext2, String ext3, Integer examPaperNum, String studentNum) {
        this.errorPaperNum = errorPaperNum;
        this.examPaperNum = examPaperNum;
        this.studentId = studentId;
        this.type = type;
        this.schoolNum = schoolNum;
        this.insertDate = insertDate;
        this.insertUser = insertUser;
        this.description = description;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.studentNum = studentNum;
    }

    public String getErrorPaperNum() {
        return this.errorPaperNum;
    }

    public void setErrorPaperNum(String errorPaperNum) {
        this.errorPaperNum = errorPaperNum;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(Integer examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public String getBatch() {
        return this.batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getScore() {
        return this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getExampaperType() {
        return this.exampaperType;
    }

    public void setExampaperType(String exampaperType) {
        this.exampaperType = exampaperType;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsModify() {
        return this.isModify;
    }

    public void setIsModify(String isModify) {
        this.isModify = isModify;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getExamineeNum() {
        return this.examineeNum;
    }

    public void setExamineeNum(String examineeNum) {
        this.examineeNum = examineeNum;
    }

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegId() {
        return this.regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getStudentNum() {
        return this.studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getTestingCentreId() {
        return this.testingCentreId;
    }

    public void setTestingCentreId(String testingCentreId) {
        this.testingCentreId = testingCentreId;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
