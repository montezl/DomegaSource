package com.dmj.domain;

import java.io.Serializable;

/* loaded from: config.class */
public class config implements Serializable {
    private int id;
    private String type;
    private int operate;
    private String para;
    private String insertUser;
    private String insertDate;
    private String description;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOperate() {
        return this.operate;
    }

    public void setOperate(int operate) {
        this.operate = operate;
    }

    public String getPara() {
        return this.para;
    }

    public void setPara(String para) {
        this.para = para;
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
}
