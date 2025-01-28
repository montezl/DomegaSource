package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Exam.class */
public class Exam implements Serializable {
    private Integer examNum;
    private String examName;
    private String examDate;
    private String examType;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String description;
    private Integer step;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String examTypeName;
    private String status;
    private String scoreModel;
    private String rule;
    private int examinationRoomLength;
    private int examineeLength;
    private String paperSize;
    private String paintMode;
    private String type;
    private String scanType;
    private String isMoreSchool;
    private String examineeInstructions;
    private String jieIschange;

    public String getJieIschange() {
        return this.jieIschange;
    }

    public void setJieIschange(String jieIschange) {
        this.jieIschange = jieIschange;
    }

    public String getExamineeInstructions() {
        return this.examineeInstructions;
    }

    public void setExamineeInstructions(String examineeInstructions) {
        this.examineeInstructions = examineeInstructions;
    }

    public String getScanType() {
        return this.scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Exam() {
    }

    public Exam(Integer examNum, String examName, String examDate, String examType, String insertUser, String insertDate, String updateUser, String updateDate) {
        this.examNum = examNum;
        this.examName = examName;
        this.examDate = examDate;
        this.examType = examType;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public Exam(Integer examNum, String examName, String examDate, String examType, String insertUser, String insertDate, String updateUser, String updateDate, String description, Integer step, String isDelete, String ext1, String ext2, String ext3) {
        this.examNum = examNum;
        this.examName = examName;
        this.examDate = examDate;
        this.examType = examType;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.step = step;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
    }

    public int getExaminationRoomLength() {
        return this.examinationRoomLength;
    }

    public void setExaminationRoomLength(int examinationRoomLength) {
        this.examinationRoomLength = examinationRoomLength;
    }

    public int getExamineeLength() {
        return this.examineeLength;
    }

    public void setExamineeLength(int examineeLength) {
        this.examineeLength = examineeLength;
    }

    public String getPaperSize() {
        return this.paperSize;
    }

    public void setPaperSize(String paperSize) {
        this.paperSize = paperSize;
    }

    public String getScoreModel() {
        return this.scoreModel;
    }

    public void setScoreModel(String scoreModel) {
        this.scoreModel = scoreModel;
    }

    public String getRule() {
        return this.rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Integer getExamNum() {
        return this.examNum;
    }

    public void setExamNum(Integer examNum) {
        this.examNum = examNum;
    }

    public String getExamName() {
        return this.examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamDate() {
        return this.examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getExamType() {
        return this.examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStep() {
        return this.step;
    }

    public void setStep(Integer step) {
        this.step = step;
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

    public String getExamTypeName() {
        return this.examTypeName;
    }

    public void setExamTypeName(String examTypeName) {
        this.examTypeName = examTypeName;
    }

    public String getPaintMode() {
        return this.paintMode;
    }

    public void setPaintMode(String paintMode) {
        this.paintMode = paintMode;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsMoreSchool() {
        return this.isMoreSchool;
    }

    public void setIsMoreSchool(String isMoreSchool) {
        this.isMoreSchool = isMoreSchool;
    }
}
