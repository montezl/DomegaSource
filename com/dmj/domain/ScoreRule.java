package com.dmj.domain;

import java.io.Serializable;

/* loaded from: ScoreRule.class */
public class ScoreRule implements Serializable {
    private String questionNum;
    private String exampaperNum;
    private String groupNum;
    private String groupname;
    private String questionname;
    private String questionRule;
    private String questionbuchang;
    private String scoreRuleType;
    private String insertUser;
    private String insertDate;
    private String fullScore;
    private String autoCommitForbid;
    private String aotoCommitDefault;

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getExampaperNum() {
        return this.exampaperNum;
    }

    public void setExampaperNum(String exampaperNum) {
        this.exampaperNum = exampaperNum;
    }

    public String getGroupNum() {
        return this.groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public String getGroupname() {
        return this.groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getQuestionname() {
        return this.questionname;
    }

    public void setQuestionname(String questionname) {
        this.questionname = questionname;
    }

    public String getQuestionRule() {
        return this.questionRule;
    }

    public void setQuestionRule(String questionRule) {
        this.questionRule = questionRule;
    }

    public String getQuestionbuchang() {
        return this.questionbuchang;
    }

    public void setQuestionbuchang(String questionbuchang) {
        this.questionbuchang = questionbuchang;
    }

    public String getScoreRuleType() {
        return this.scoreRuleType;
    }

    public void setScoreRuleType(String scoreRuleType) {
        this.scoreRuleType = scoreRuleType;
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

    public String getFullScore() {
        return this.fullScore;
    }

    public void setFullScore(String fullScore) {
        this.fullScore = fullScore;
    }

    public String getAutoCommitForbid() {
        return this.autoCommitForbid;
    }

    public void setAutoCommitForbid(String autoCommitForbid) {
        this.autoCommitForbid = autoCommitForbid;
    }

    public String getAotoCommitDefault() {
        return this.aotoCommitDefault;
    }

    public void setAotoCommitDefault(String aotoCommitDefault) {
        this.aotoCommitDefault = aotoCommitDefault;
    }
}
