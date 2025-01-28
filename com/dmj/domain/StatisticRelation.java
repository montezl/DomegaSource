package com.dmj.domain;

import java.io.Serializable;

/* loaded from: StatisticRelation.class */
public class StatisticRelation implements Serializable {
    private Integer id;
    private String statisticId;
    private String statisticName;
    private String statisticItem;
    private String sonStatisticId;
    private String sonStatisticName;
    private String isLeaf;
    private String description;
    private String isDelete;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String ext1;
    private String ext2;
    private String ext3;

    public StatisticRelation() {
    }

    public StatisticRelation(String statisticId, String statisticName, String statisticItem, String sonStatisticId, String sonStatisticName, String isLeaf, String description, String insertUser, String insertDate, String updateUser, String updateDate) {
        this.statisticId = statisticId;
        this.statisticName = statisticName;
        this.statisticItem = statisticItem;
        this.sonStatisticId = sonStatisticId;
        this.sonStatisticName = sonStatisticName;
        this.isLeaf = isLeaf;
        this.description = description;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public StatisticRelation(String statisticId, String statisticName, String statisticItem, String sonStatisticId, String sonStatisticName, String isLeaf, String description, String insertUser, String insertDate, String updateUser, String updateDate, String isDelete, String ext1, String ext2, String ext3) {
        this.statisticId = statisticId;
        this.statisticName = statisticName;
        this.statisticItem = statisticItem;
        this.sonStatisticId = sonStatisticId;
        this.sonStatisticName = sonStatisticName;
        this.isLeaf = isLeaf;
        this.description = description;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsLeaf() {
        return this.isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
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

    public String getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getStatisticId() {
        return this.statisticId;
    }

    public void setStatisticId(String statisticId) {
        this.statisticId = statisticId;
    }

    public String getStatisticName() {
        return this.statisticName;
    }

    public void setStatisticName(String statisticName) {
        this.statisticName = statisticName;
    }

    public String getStatisticItem() {
        return this.statisticItem;
    }

    public void setStatisticItem(String statisticItem) {
        this.statisticItem = statisticItem;
    }

    public String getSonStatisticId() {
        return this.sonStatisticId;
    }

    public void setSonStatisticId(String sonStatisticId) {
        this.sonStatisticId = sonStatisticId;
    }

    public String getSonStatisticName() {
        return this.sonStatisticName;
    }

    public void setSonStatisticName(String sonStatisticName) {
        this.sonStatisticName = sonStatisticName;
    }
}
