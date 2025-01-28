package com.dmj.domain;

/* loaded from: Userposition.class */
public class Userposition {
    private Integer id;
    private String userNum;
    private String type;
    private String insertDate;
    private String description;
    private String schoolNum;
    private String insertUser;
    private String ext1;
    private String ext2;
    private String ext3;
    private String gradeNum;
    private String gradeName;
    private String subjectNum;
    private String subjectName;
    private String classNum;
    private String shortName;
    private String teacherName;
    private String stage;
    private String userName;
    private String className;
    private String permissionClass;
    private String permissionGrade;
    private String permissionSubject;

    public String getTeacherName() {
        return this.teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Userposition() {
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserNum() {
        return this.userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
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

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public Userposition(Integer id, String userNum, String type, String insertDate, String description, String schoolNum, String insertUser, String ext1, String ext2, String ext3, String gradeNum, String gradeName, String subjectNum, String classNum, String className) {
        this.id = id;
        this.userNum = userNum;
        this.type = type;
        this.insertDate = insertDate;
        this.description = description;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.gradeNum = gradeNum;
        this.gradeName = gradeName;
        this.subjectNum = subjectNum;
        this.classNum = classNum;
        this.className = className;
    }

    public String getStage() {
        return this.stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPermissionClass() {
        return this.permissionClass;
    }

    public void setPermissionClass(String permissionClass) {
        this.permissionClass = permissionClass;
    }

    public String getPermissionGrade() {
        return this.permissionGrade;
    }

    public void setPermissionGrade(String permissionGrade) {
        this.permissionGrade = permissionGrade;
    }

    public String getPermissionSubject() {
        return this.permissionSubject;
    }

    public void setPermissionSubject(String permissionSubject) {
        this.permissionSubject = permissionSubject;
    }
}
