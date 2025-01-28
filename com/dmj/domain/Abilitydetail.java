package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Abilitydetail.class */
public class Abilitydetail implements Serializable {
    private String id;
    private String abilityNum;
    private String questionNum;
    private Integer examPaperNum;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;

    public Abilitydetail() {
    }

    public Abilitydetail(String abilityNum, String questionNum, Integer examPaperNum, String insertUser, String insertDate) {
        this.abilityNum = abilityNum;
        this.questionNum = questionNum;
        this.examPaperNum = examPaperNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
    }

    public Abilitydetail(String abilityNum, String questionNum, Integer examPaperNum, String insertUser, String insertDate, String isDelete, String ext1, String ext2, String ext3) {
        this.abilityNum = abilityNum;
        this.questionNum = questionNum;
        this.examPaperNum = examPaperNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbilityNum() {
        return this.abilityNum;
    }

    public void setAbilityNum(String abilityNum) {
        this.abilityNum = abilityNum;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public Integer getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(Integer examPaperNum) {
        this.examPaperNum = examPaperNum;
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
}
