package com.dmj.cs.bean;

/* loaded from: MissingPaper.class */
public class MissingPaper {
    private long id;
    private long testingCenterId;
    private int examNum;
    private int gradeNum;
    private int subjectNum;
    private long insertUser;
    private String insertDate;
    private String path;
    private int count;
    private int isDelete;
    private String location;
    private String filename;
    private String jsonStr;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestingCenterId() {
        return this.testingCenterId;
    }

    public void setTestingCenterId(long testingCenterId) {
        this.testingCenterId = testingCenterId;
    }

    public int getExamNum() {
        return this.examNum;
    }

    public void setExamNum(int examNum) {
        this.examNum = examNum;
    }

    public int getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(int gradeNum) {
        this.gradeNum = gradeNum;
    }

    public int getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(int subjectNum) {
        this.subjectNum = subjectNum;
    }

    public long getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(long insertUser) {
        this.insertUser = insertUser;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getJsonStr() {
        return this.jsonStr;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
