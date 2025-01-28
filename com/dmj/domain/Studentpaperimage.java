package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Studentpaperimage.class */
public class Studentpaperimage implements Serializable {
    private String id;
    private byte[] img;
    private String studentId;
    private String examPaperNum;
    private Integer page;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String description;
    private String schoolNum;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String cNum;

    public Studentpaperimage() {
    }

    public Studentpaperimage(byte[] img, String studentId, String examPaperNum, String insertUser, String insertDate, String updateUser, String updateDate) {
        this.img = img;
        this.studentId = studentId;
        this.examPaperNum = examPaperNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public Studentpaperimage(byte[] img, String studentId, String examPaperNum, Integer page, String insertUser, String insertDate, String updateUser, String updateDate, String description, String schoolNum, String isDelete, String ext1, String ext2, String ext3, String cNum) {
        this.img = img;
        this.studentId = studentId;
        this.examPaperNum = examPaperNum;
        this.page = page;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.schoolNum = schoolNum;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.cNum = cNum;
    }

    public byte[] getImg() {
        return this.img;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(String examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
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

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
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

    public String getcNum() {
        return this.cNum;
    }

    public void setcNum(String cNum) {
        this.cNum = cNum;
    }
}
