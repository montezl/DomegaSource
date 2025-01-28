package com.dmj.cs.bean;

import java.io.Serializable;

/* loaded from: UploadPicFile.class */
public class UploadPicFile implements Serializable {
    private long id;
    private String fileName;
    private String stuBarCodeNum;
    private int page;
    private int sheetOrder;
    private boolean rotate180;
    private String groupNum;
    private String batchNum;
    private long insertUser;
    private String insertDate;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStuBarCodeNum() {
        return this.stuBarCodeNum;
    }

    public void setStuBarCodeNum(String stuBarCodeNum) {
        this.stuBarCodeNum = stuBarCodeNum;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSheetOrder() {
        return this.sheetOrder;
    }

    public void setSheetOrder(int sheetOrder) {
        this.sheetOrder = sheetOrder;
    }

    public boolean isRotate180() {
        return this.rotate180;
    }

    public void setRotate180(boolean rotate180) {
        this.rotate180 = rotate180;
    }

    public String getGroupNum() {
        return this.groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public String getBatchNum() {
        return this.batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
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
}
