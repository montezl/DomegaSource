package com.dmj.domain.configration;

import java.io.Serializable;

/* loaded from: ConfigField.class */
public class ConfigField implements Serializable {
    private int id;
    private String confKey;
    private String confName;
    private String confValue;
    private String description;
    private String insertDate;
    private Long insertUser;
    private String updateDate;
    private Long updateUser;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConfKey() {
        return this.confKey;
    }

    public void setConfKey(String confKey) {
        this.confKey = confKey;
    }

    public String getConfName() {
        return this.confName;
    }

    public void setConfName(String confName) {
        this.confName = confName;
    }

    public String getConfValue() {
        return this.confValue;
    }

    public void setConfValue(String confValue) {
        this.confValue = confValue;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public Long getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(Long insertUser) {
        this.insertUser = insertUser;
    }

    public String getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public Long getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }
}
