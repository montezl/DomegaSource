package com.dmj.domain;

/* loaded from: Averagescore.class */
public class Averagescore {
    private String id;
    private String examNum;
    private String subjectNum;
    private String gradeNum;
    private String schoolNum;
    private String classNum;
    private Double average;
    private Double mindev;
    private Double maxdev;
    private String status;
    private String isModify;
    private String insertUser;
    private String insertDate;
    private String className;
    private String ext1;

    public Averagescore() {
    }

    public Averagescore(String classNum, String className, Double average, Double mindev, Double maxdev, String ext1, String status, String isModify) {
        this.classNum = classNum;
        this.className = className;
        this.average = average;
        this.mindev = mindev;
        this.maxdev = maxdev;
        this.ext1 = ext1;
        this.status = status;
        this.isModify = isModify;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExamNum() {
        return this.examNum;
    }

    public void setExamNum(String examNum) {
        this.examNum = examNum;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public Double getAverage() {
        return this.average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Double getMindev() {
        return this.mindev;
    }

    public void setMindev(Double mindev) {
        this.mindev = mindev;
    }

    public Double getMaxdev() {
        return this.maxdev;
    }

    public void setMaxdev(Double maxdev) {
        this.maxdev = maxdev;
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

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getExt1() {
        return this.ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsModify() {
        return this.isModify;
    }

    public void setIsModify(String isModify) {
        this.isModify = isModify;
    }
}
