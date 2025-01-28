package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Examinationroom.class */
public class Examinationroom implements Serializable {
    private String id;
    private String examinationRoomNum;
    private String examinationRoomName;
    private Integer gradeNum;
    private String gradeName;
    private Integer schoolNum;
    private String insertUser;
    private String insertDate;
    private String description;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private Integer examNum;
    private String status;
    private String eRoomNum;
    private String subjectNum;
    private String subjectName;
    private String preexamineeNum;
    private String testingCentreId;
    private String testLocation;

    public Examinationroom() {
    }

    public Examinationroom(String examinationRoomNum, String examinationRoomName, Integer gradeNum, Integer schoolNum, String insertUser, String insertDate) {
        this.examinationRoomNum = examinationRoomNum;
        this.examinationRoomName = examinationRoomName;
        this.gradeNum = gradeNum;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
    }

    public Examinationroom(String id, String examinationRoomNum, String examinationRoomName, Integer gradeNum, String gradeName, Integer schoolNum, String insertUser, String insertDate, String description, String isDelete, String ext1, String ext2, String ext3, Integer examNum, String status, String eRoomNum, String subjectNum, String subjectName) {
        this.id = id;
        this.examinationRoomNum = examinationRoomNum;
        this.examinationRoomName = examinationRoomName;
        this.gradeNum = gradeNum;
        this.gradeName = gradeName;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.examNum = examNum;
        this.status = status;
        this.eRoomNum = eRoomNum;
        this.subjectNum = subjectNum;
        this.subjectName = subjectName;
    }

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public String getExaminationRoomName() {
        return this.examinationRoomName;
    }

    public void setExaminationRoomName(String examinationRoomName) {
        this.examinationRoomName = examinationRoomName;
    }

    public Integer getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
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

    public Integer getExamNum() {
        return this.examNum;
    }

    public void setExamNum(Integer examNum) {
        this.examNum = examNum;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void seteRoomNum(String eRoomNum) {
        this.eRoomNum = eRoomNum;
    }

    public String geteRoomNum() {
        return this.eRoomNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public String getPreexamineeNum() {
        return this.preexamineeNum;
    }

    public void setPreexamineeNum(String preexamineeNum) {
        this.preexamineeNum = preexamineeNum;
    }

    public String getTestingCentreId() {
        return this.testingCentreId;
    }

    public void setTestingCentreId(String testingCentreId) {
        this.testingCentreId = testingCentreId;
    }

    public String getTestLocation() {
        return this.testLocation;
    }

    public void setTestLocation(String testLocation) {
        this.testLocation = testLocation;
    }
}
