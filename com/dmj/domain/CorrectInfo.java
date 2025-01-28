package com.dmj.domain;

import java.io.Serializable;

/* loaded from: CorrectInfo.class */
public class CorrectInfo implements Serializable {
    private String id;
    private String regId;
    private byte[] img;
    private String studentId;
    private String questionNum;
    private String isModify;
    private String isException;
    private String fullScore;
    private String exampaperNum;
    private String page;
    private String cNum;
    private String type;
    private String studentName;
    private String ext1;
    private String ext2;

    public CorrectInfo() {
    }

    public CorrectInfo(String id, byte[] img, String studentId, String questionNum, String isModify, String isException, String fullScore, String exampaperNum, String page, String ext1, String ext2, String cNum, String type) {
        this.id = id;
        this.img = img;
        this.studentId = studentId;
        this.questionNum = questionNum;
        this.isModify = isModify;
        this.isException = isException;
        this.fullScore = fullScore;
        this.exampaperNum = exampaperNum;
        this.page = page;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.cNum = cNum;
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getImg() {
        return this.img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getIsModify() {
        return this.isModify;
    }

    public void setIsModify(String isModify) {
        this.isModify = isModify;
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

    public String getFullScore() {
        return this.fullScore;
    }

    public void setFullScore(String fullScore) {
        this.fullScore = fullScore;
    }

    public String getIsException() {
        return this.isException;
    }

    public void setIsException(String isException) {
        this.isException = isException;
    }

    public String getExampaperNum() {
        return this.exampaperNum;
    }

    public void setExampaperNum(String exampaperNum) {
        this.exampaperNum = exampaperNum;
    }

    public String toString() {
        return " studentId;" + this.studentId + ", questionNum;" + this.questionNum + ", isModify:" + this.isModify;
    }

    public String getPage() {
        return this.page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getcNum() {
        return this.cNum;
    }

    public void setcNum(String cNum) {
        this.cNum = cNum;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegId() {
        return this.regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
