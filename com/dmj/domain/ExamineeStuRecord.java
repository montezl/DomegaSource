package com.dmj.domain;

/* loaded from: ExamineeStuRecord.class */
public class ExamineeStuRecord {
    private String id;
    private String scoreId;
    private String userId;
    private String insertDate;
    private String status;
    private String ext1;
    private String ext2;
    private String ext3;

    public ExamineeStuRecord() {
    }

    public String toString() {
        return "ExamineeStuRecord [id=" + this.id + ", scoreId=" + this.scoreId + ", userId=" + this.userId + ", insertDate=" + this.insertDate + ", status=" + this.status + ", ext1=" + this.ext1 + ", ext2=" + this.ext2 + ", ext3=" + this.ext3 + "]";
    }

    public ExamineeStuRecord(String id, String scoreId, String userId, String insertDate, String status, String ext1, String ext2, String ext3) {
        this.id = id;
        this.scoreId = scoreId;
        this.userId = userId;
        this.insertDate = insertDate;
        this.status = status;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScoreId() {
        return this.scoreId;
    }

    public void setScoreId(String scoreId) {
        this.scoreId = scoreId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
