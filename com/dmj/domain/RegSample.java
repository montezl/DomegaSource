package com.dmj.domain;

import java.io.Serializable;

/* loaded from: RegSample.class */
public class RegSample implements Serializable {
    private String id;
    private byte[] img;
    private String section;
    private String templateType;
    private String val;
    private String insertUser;
    private String insertDate;
    private String isDelete;
    private String one6;
    private String one5;
    private String one4;
    private String one3;
    private String one2;
    private String one1;

    public RegSample() {
    }

    public String getInsertUser() {
        return this.insertUser;
    }

    public RegSample(String id, byte[] img, String section, String templateType, String val, String insertUser, String insertDate, String isDelete, String one6, String one5, String one4, String one3, String one2, String one1) {
        this.id = id;
        this.img = img;
        this.section = section;
        this.templateType = templateType;
        this.val = val;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.isDelete = isDelete;
        this.one6 = one6;
        this.one5 = one5;
        this.one4 = one4;
        this.one3 = one3;
        this.one2 = one2;
        this.one1 = one1;
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

    public String getOne6() {
        return this.one6;
    }

    public void setOne6(String one6) {
        this.one6 = one6;
    }

    public String getOne5() {
        return this.one5;
    }

    public void setOne5(String one5) {
        this.one5 = one5;
    }

    public String getOne4() {
        return this.one4;
    }

    public void setOne4(String one4) {
        this.one4 = one4;
    }

    public String getOne3() {
        return this.one3;
    }

    public void setOne3(String one3) {
        this.one3 = one3;
    }

    public String getOne2() {
        return this.one2;
    }

    public void setOne2(String one2) {
        this.one2 = one2;
    }

    public String getOne1() {
        return this.one1;
    }

    public void setOne1(String one1) {
        this.one1 = one1;
    }

    public byte[] getImg() {
        return this.img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getSection() {
        return this.section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getVal() {
        return this.val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getTemplateType() {
        return this.templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
}
