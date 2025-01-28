package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Data.class */
public class Data implements Serializable {
    private Integer id;
    private String category;
    private String isDefault;
    private String type;
    private String name;
    private String value;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String description;
    private Integer orderNum;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String isLock;

    public Data() {
    }

    public Data(String type, String insertUser, String insertDate, String updateUser, String updateDate) {
        this.type = type;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public Data(String type, String name, String value, String insertUser, String insertDate, String updateUser, String updateDate, String description, Integer orderNum, String isDelete, String ext1, String ext2, String ext3, String category, String isDefault, String isLock) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.orderNum = orderNum;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.category = category;
        this.isDefault = isDefault;
        this.isLock = isLock;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderNum() {
        return this.orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
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

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return this.category;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getIsDefault() {
        return this.isDefault;
    }

    public void setIsLock(String isLock) {
        this.isLock = isLock;
    }

    public String getIsLock() {
        return this.isLock;
    }
}
