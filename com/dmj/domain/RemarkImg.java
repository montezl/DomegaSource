package com.dmj.domain;

import java.io.Serializable;

/* loaded from: RemarkImg.class */
public class RemarkImg implements Serializable {
    private String id;
    private String examPaperNum;
    private String questionNum;
    private String scoreId;
    private String img;
    private String remarktext;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateTime;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExampaperNum() {
        return this.examPaperNum;
    }

    public void setExampaperNum(String exampaperNum) {
        this.examPaperNum = exampaperNum;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getScoreId() {
        return this.scoreId;
    }

    public void setScoreId(String scoreId) {
        this.scoreId = scoreId;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getRemarktext() {
        return this.remarktext;
    }

    public void setRemarktext(String remarktext) {
        this.remarktext = remarktext;
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

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
