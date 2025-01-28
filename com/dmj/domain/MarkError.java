package com.dmj.domain;

import java.io.Serializable;

/* loaded from: MarkError.class */
public class MarkError implements Serializable {
    private Integer id;
    private String scoreId;
    private Integer exampaperNum;
    private String questionNum;
    private Double questionScore;
    private String type;
    private String userNum;
    private String insertUser;
    private String insertDate;
    private String ext1;
    private String ext2;
    private String ext3;
    private Integer page;

    public MarkError() {
    }

    public MarkError(Integer id, String scoreId, Integer exampaperNum, String questionNum, Double questionScore, String type, String userNum, String insertUser, String insertDate, String ext1, String ext2, String ext3) {
        this.id = id;
        this.scoreId = scoreId;
        this.exampaperNum = exampaperNum;
        this.questionNum = questionNum;
        this.questionScore = questionScore;
        this.type = type;
        this.userNum = userNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
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

    public String getScoreId() {
        return this.scoreId;
    }

    public void setScoreId(String scoreId) {
        this.scoreId = scoreId;
    }

    public Integer getExampaperNum() {
        return this.exampaperNum;
    }

    public void setExampaperNum(Integer exampaperNum) {
        this.exampaperNum = exampaperNum;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public Double getQuestionScore() {
        return this.questionScore;
    }

    public void setQuestionScore(Double questionScore) {
        this.questionScore = questionScore;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserNum() {
        return this.userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
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

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
