package com.dmj.cs.bean;

/* loaded from: Corner.class */
public class Corner {
    private String regId;
    private String cNum;
    private String batch;
    private int blackRatio;
    private String schoolNum;
    private String cardNo;
    private int page;
    private long examinationRoomNum;
    private long insertUser;
    private long testingCentreId;
    private String examineeNum = "";

    public String getRegId() {
        return this.regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public int getBlackRatio() {
        return this.blackRatio;
    }

    public void setBlackRatio(int blackRatio) {
        this.blackRatio = blackRatio;
    }

    public String getcNum() {
        return this.cNum;
    }

    public void setcNum(String cNum) {
        this.cNum = cNum;
    }

    public String getBatch() {
        return this.batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getCardNo() {
        return this.cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public void setExaminationRoomNum(long examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public long getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(long insertUser) {
        this.insertUser = insertUser;
    }

    public long getTestingCentreId() {
        return this.testingCentreId;
    }

    public void setTestingCentreId(long testingCentreId) {
        this.testingCentreId = testingCentreId;
    }

    public String getExamineeNum() {
        return this.examineeNum;
    }

    public void setExamineeNum(String examineeNum) {
        this.examineeNum = examineeNum;
    }
}
