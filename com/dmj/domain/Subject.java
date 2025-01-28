package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Subject.class */
public class Subject implements Serializable {
    private int id;
    private int subjectNum;
    private String subjectName;
    private String type;
    private int pid;
    private String isHidden;
    private String subjectType;
    private String mainType;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String isDelete;
    private int orderNum;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String ext5;
    private String ext6;
    private String ext7;
    private String gradeNum;
    private String status;
    private String category;
    private String examPaperNum;
    private String xuankaoqufen;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Subject() {
    }

    public Subject(int subjectNum, String subjectName, String insertUser, String insertDate, String updateUser, String updateDate, String category) {
        this.subjectNum = subjectNum;
        this.subjectName = subjectName;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.category = category;
    }

    public Subject(int subjectNum, String subjectName, String insertUser, String insertDate, String updateUser, String updateDate, String isDelete, int pid, String isHidden, String subjectType, String mainType, int orderNum, String type) {
        this.subjectNum = subjectNum;
        this.subjectName = subjectName;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.isDelete = isDelete;
        this.pid = pid;
        this.isHidden = isHidden;
        this.subjectType = subjectType;
        this.mainType = mainType;
        this.orderNum = orderNum;
        this.type = type;
    }

    public Integer getId() {
        return Integer.valueOf(this.id);
    }

    public void setId(Integer id) {
        this.id = id.intValue();
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(String examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public String getSubjectType() {
        return this.subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getMainType() {
        return this.mainType;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }

    public Integer getOrderNum() {
        return Integer.valueOf(this.orderNum);
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum.intValue();
    }

    public String getIsHidden() {
        return this.isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden = isHidden;
    }

    public int getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(int subjectNum) {
        this.subjectNum = subjectNum;
    }

    public int getPid() {
        return this.pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
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

    public String getExt4() {
        return this.ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getExt5() {
        return this.ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }

    public String getExt6() {
        return this.ext6;
    }

    public void setExt6(String ext6) {
        this.ext6 = ext6;
    }

    public String getXuankaoqufen() {
        return this.xuankaoqufen;
    }

    public void setXuankaoqufen(String xuankaoqufen) {
        this.xuankaoqufen = xuankaoqufen;
    }

    public String getExt7() {
        return this.ext7;
    }

    public void setExt7(String ext7) {
        this.ext7 = ext7;
    }
}
