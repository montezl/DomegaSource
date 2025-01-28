package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Reg_Th_Log.class */
public class Reg_Th_Log implements Serializable {
    private String id;
    private String val;
    private String templateType;
    private String type;
    private String insertUser;
    private String insertDate;
    private String description;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;

    public Reg_Th_Log() {
    }

    public Reg_Th_Log(String id, String val, String templateType, String type, String insertUser, String insertDate, String description, String ext1, String ext2, String ext3, String ext4) {
        this.id = id;
        this.val = val;
        this.templateType = templateType;
        this.type = type;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.description = description;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.ext4 = ext4;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVal() {
        return this.val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getExt4() {
        return this.ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getTemplateType() {
        return this.templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
}
