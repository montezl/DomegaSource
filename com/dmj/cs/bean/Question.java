package com.dmj.cs.bean;

/* loaded from: Question.class */
public class Question {
    private String examNum;
    private String tableName;
    private String examPaperNum;
    private String img;
    private String questionNum;
    private String insertUser;
    private String insertDate;
    private int page;
    private int width;
    private int height;
    private int imgpath;
    private byte[] file;

    public Question(String tableName, String examNum, String examPaperNum, String img, String questionNum, String insertUser, String insertDate, int page, int width, int height, int imgpath, byte[] file) {
        this.tableName = tableName;
        this.examNum = examNum;
        this.examPaperNum = examPaperNum;
        this.img = img;
        this.questionNum = questionNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.page = page;
        this.width = width;
        this.height = height;
        this.imgpath = imgpath;
        this.file = file;
    }

    public Question() {
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getExamNum() {
        return this.examNum;
    }

    public void setExamNum(String examNum) {
        this.examNum = examNum;
    }

    public String getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(String examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
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

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getImgpath() {
        return this.imgpath;
    }

    public void setImgpath(int imgpath) {
        this.imgpath = imgpath;
    }

    public byte[] getFile() {
        return this.file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
