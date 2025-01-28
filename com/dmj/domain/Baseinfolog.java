package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Baseinfolog.class */
public class Baseinfolog implements Serializable {
    private Integer id;
    private String operate;
    private String studentId;
    private String classNum;
    private Integer gradeNum;
    private Integer schoolNum;
    private String insertUser;
    private String insertDate;
    private String description;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String realname;
    private int pageStart;
    private int pageSize;
    private String studentName;
    private String gradeName;
    private String schoolName;
    private String className;
    private String studentNum;
    private String inputSchNum;

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Baseinfolog() {
    }

    public Baseinfolog(String insertUser, String insertDate) {
        this.insertUser = insertUser;
        this.insertDate = insertDate;
    }

    public Baseinfolog(String operate, String studentId, String classNum, Integer gradeNum, Integer schoolNum, String insertUser, String insertDate, String description, String isDelete, String ext1, String ext2, String ext3, String gradeName, String studentName, int pageStart, int pageSize, String studentNum, String className) {
        this.operate = operate;
        this.studentId = studentId;
        this.classNum = classNum;
        this.gradeNum = gradeNum;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.studentNum = studentNum;
        this.className = className;
    }

    public String getOperate() {
        return this.operate;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public Integer getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }

    public int getPageStart() {
        return this.pageStart;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getStudentNum() {
        return this.studentNum;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public String getInputSchNum() {
        return this.inputSchNum;
    }

    public void setInputSchNum(String inputSchNum) {
        this.inputSchNum = inputSchNum;
    }
}
