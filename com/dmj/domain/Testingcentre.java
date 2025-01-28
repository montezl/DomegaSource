package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Testingcentre.class */
public class Testingcentre implements Serializable {
    private String id;
    private String examNum;
    private String testingCentreNum;
    private String testingCentreName;
    private String testingCentreLocation;
    private String insertUser;
    private String insertDate;
    private String isDelete;
    private String status;
    private String name;
    private String value;

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

    public String getTestingCentreNum() {
        return this.testingCentreNum;
    }

    public void setTestingCentreNum(String testingCentreNum) {
        this.testingCentreNum = testingCentreNum;
    }

    public String getTestingCentreName() {
        return this.testingCentreName;
    }

    public void setTestingCentreName(String testingCentreName) {
        this.testingCentreName = testingCentreName;
    }

    public String getTestingCentreLocation() {
        return this.testingCentreLocation;
    }

    public void setTestingCentreLocation(String testingCentreLocation) {
        this.testingCentreLocation = testingCentreLocation;
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

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
