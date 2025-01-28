package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Classlevel.class */
public class Classlevel implements Serializable {
    private String id;
    private String studentName;
    private String schoolNum;
    private String gradeNum;
    private String classNum;
    private String examPaperNum;
    private Double averageScore;
    private Double totalScore;
    private String insertUser;
    private String insertDate;
    private String description;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String examNum;
    private String subjectNum;
    private Double average;
    private Double variance;
    private Double sd;
    private Double standardScore;
    private Double standardScore_q;
    private int numOfStudent;
    private int rank;
    private String ext4;
    private Double gradeaverage;
    private String subjectName;
    private String gradeName;
    private String className;
    private String teacherName;

    public Classlevel() {
    }

    public Classlevel(String studentName, String gradeNum, String classNum, String examPaperNum, String insertUser, String insertDate) {
        this.studentName = studentName;
        this.gradeNum = gradeNum;
        this.classNum = classNum;
        this.examPaperNum = examPaperNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
    }

    public Classlevel(String studentName, String schoolNum, String gradeNum, String classNum, String examPaperNum, Double averageScore, Double totalScore, String insertUser, String insertDate, String description, String isDelete, String ext1, String ext2, String ext3) {
        this.studentName = studentName;
        this.schoolNum = schoolNum;
        this.gradeNum = gradeNum;
        this.classNum = classNum;
        this.examPaperNum = examPaperNum;
        this.averageScore = averageScore;
        this.totalScore = totalScore;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(String examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public Double getAverageScore() {
        return this.averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getTotalScore() {
        return this.totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
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

    public Double getAverage() {
        return this.average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Double getVariance() {
        return this.variance;
    }

    public void setVariance(Double variance) {
        this.variance = variance;
    }

    public Double getSd() {
        return this.sd;
    }

    public void setSd(Double sd) {
        this.sd = sd;
    }

    public Double getStandardScore() {
        return this.standardScore;
    }

    public void setStandardScore(Double standardScore) {
        this.standardScore = standardScore;
    }

    public int getNumOfStudent() {
        return this.numOfStudent;
    }

    public void setNumOfStudent(int numOfStudent) {
        this.numOfStudent = numOfStudent;
    }

    public Double getGradeaverage() {
        return this.gradeaverage;
    }

    public void setGradeaverage(Double gradeaverage) {
        this.gradeaverage = gradeaverage;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getExt4() {
        return this.ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherName() {
        return this.teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Double getStandardScore_q() {
        return this.standardScore_q;
    }

    public void setStandardScore_q(Double standardScore_q) {
        this.standardScore_q = standardScore_q;
    }
}
