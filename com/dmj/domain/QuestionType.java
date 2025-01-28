package com.dmj.domain;

import java.io.Serializable;

/* loaded from: QuestionType.class */
public class QuestionType implements Serializable {
    private Integer id;
    private String num;
    private String pnum;
    private String name;
    private String stage;
    private Integer subjectNum;
    private Integer level;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private Integer schoolNum;
    private Integer vnum;
    private String questiontype;

    public QuestionType() {
    }

    public QuestionType(String insertUser, String insertDate) {
        this.insertUser = insertUser;
        this.insertDate = insertDate;
    }

    public QuestionType(String num, String name, String insertUser, String insertDate, String isDelete, String ext1, String ext2, String ext3) {
        this.num = num;
        this.name = name;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.isDelete = isDelete;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
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

    public String getStage() {
        return this.stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getQuestiontype() {
        return this.questiontype;
    }

    public void setQuestiontype(String questiontype) {
        this.questiontype = questiontype;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPnum() {
        return this.pnum;
    }

    public void setPnum(String pnum) {
        this.pnum = pnum;
    }

    public Integer getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(Integer subjectNum) {
        this.subjectNum = subjectNum;
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
    }

    public Integer getVnum() {
        return this.vnum;
    }

    public void setVnum(Integer vnum) {
        this.vnum = vnum;
    }
}
