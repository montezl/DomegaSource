package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Classexam.class */
public class Classexam implements Serializable {
    private Integer id;
    private Integer classNum;
    private Integer examPaperNum;
    private Integer schoolNum;
    private Integer insertUser;
    private String insertDate;
    private Integer updateUser;
    private String updateDate;
    private String description;
    private String isCompelt;
    private String isDelete;
    private String correctstatus;
    private String ext1;
    private String ext2;
    private String ext3;
    private String schoolName;

    public Classexam() {
    }

    public Classexam(Integer classNum, Integer examPaperNum, Integer schoolNum, Integer insertUser, String insertDate, Integer updateUser, String updateDate) {
        this.classNum = classNum;
        this.examPaperNum = examPaperNum;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public Classexam(Integer classNum, Integer examPaperNum, Integer schoolNum, Integer insertUser, String insertDate, Integer updateUser, String updateDate, String description, String isCompelt, String isDelete, String correctstatus, String ext1, String ext2, String ext3, String schoolName) {
        this.classNum = classNum;
        this.examPaperNum = examPaperNum;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.isCompelt = isCompelt;
        this.isDelete = isDelete;
        this.correctstatus = correctstatus;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.schoolName = schoolName;
    }

    public Integer getClassNum() {
        return this.classNum;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setClassNum(Integer classNum) {
        this.classNum = classNum;
    }

    public Integer getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(Integer examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
    }

    public Integer getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(Integer insertUser) {
        this.insertUser = insertUser;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public Integer getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(Integer updateUser) {
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

    public String getIsCompelt() {
        return this.isCompelt;
    }

    public void setIsCompelt(String isCompelt) {
        this.isCompelt = isCompelt;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getCorrectstatus() {
        return this.correctstatus;
    }

    public void setCorrectstatus(String correctstatus) {
        this.correctstatus = correctstatus;
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

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
