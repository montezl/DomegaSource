package com.dmj.domain;

import java.io.Serializable;

/* loaded from: ClipError.class */
public class ClipError implements Serializable {
    private Integer id;
    private Integer examPaperNum;
    private String regId;
    private Integer newPage;
    private Double angle;
    private String status;
    private String batch;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String ext1;
    private String ext2;
    private String ext3;
    private String answer;
    private String numOfStudent;
    private String questionNum;

    public ClipError() {
    }

    public ClipError(Integer id, Integer examPaperNum, String regId, Integer newPage, Double angle, String status, String batch, String insertUser, String insertDate, String updateUser, String updateDate, String ext1, String ext2, String ext3) {
        this.id = id;
        this.examPaperNum = examPaperNum;
        this.regId = regId;
        this.newPage = newPage;
        this.angle = angle;
        this.status = status;
        this.batch = batch;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(Integer examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public Integer getNewPage() {
        return this.newPage;
    }

    public void setNewPage(Integer newPage) {
        this.newPage = newPage;
    }

    public Double getAngle() {
        return this.angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBatch() {
        return this.batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
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

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getNumOfStudent() {
        return this.numOfStudent;
    }

    public void setNumOfStudent(String numOfStudent) {
        this.numOfStudent = numOfStudent;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getRegId() {
        return this.regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }
}
