package com.dmj.util.app;

/* loaded from: AppUserInfo.class */
public class AppUserInfo {
    private String userName;
    private String password;
    private String token;
    private long expirationTime;
    private long lastTime;
    private String id;
    private String userType;
    private String mobile;
    private String schoolName;
    private String className;
    private String studentNum;
    private String studnetName;
    private String studentId;
    private String xuejihao;
    private String gradeName;
    private String schoolNum;
    private String gradeNum;
    private String classNum;

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudnetName() {
        return this.studnetName;
    }

    public void setStudnetName(String studnetName) {
        this.studnetName = studnetName;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStudentNum() {
        return this.studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserType() {
        return this.userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getXuejihao() {
        return this.xuejihao;
    }

    public void setXuejihao(String xuejihao) {
        this.xuejihao = xuejihao;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
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

    public AppUserInfo(String userName, String password, String token, long expirationTime, long lastTime, String id, String userType, String mobile, String schoolName, String className, String studentNum, String studnetName, String gradeName, String schoolNum, String gradeNum, String classNum) {
        this.userName = userName;
        this.password = password;
        this.token = token;
        this.expirationTime = expirationTime;
        this.lastTime = lastTime;
        this.id = id;
        this.userType = userType;
        this.mobile = mobile;
        this.schoolName = schoolName;
        this.className = className;
        this.studentNum = studentNum;
        this.studnetName = studnetName;
        this.gradeName = gradeName;
        this.schoolNum = schoolNum;
        this.gradeNum = gradeNum;
        this.classNum = classNum;
    }

    public AppUserInfo() {
    }
}
