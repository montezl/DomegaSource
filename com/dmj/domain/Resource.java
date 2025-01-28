package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Resource.class */
public class Resource implements Serializable {
    private Integer id;
    private String num;
    private String pnum;
    private String type;
    private String name;
    private String url;
    private String isleaf;
    private String isdelete;
    private String inserttime;
    private String insertuser;
    private String ext1;
    private String ext2;
    private String ext3;
    private Integer ordernum;
    private String para;
    private String reportType;
    private String fn;
    private String openAllSchool;

    public Resource() {
    }

    public Resource(String num, String url, String pnum) {
        this.num = num;
        this.url = url;
        this.pnum = pnum;
    }

    public Resource(String num, String url, String pnum, String inserttime, String insertuser, String ext1, String ext2, String ext3, String isleaf, String isdelete) {
        this.num = num;
        this.url = url;
        this.pnum = pnum;
        this.inserttime = inserttime;
        this.insertuser = insertuser;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.isleaf = isleaf;
        this.isdelete = isdelete;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPnum() {
        return this.pnum;
    }

    public void setPnum(String pnum) {
        this.pnum = pnum;
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

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsleaf() {
        return this.isleaf;
    }

    public void setIsleaf(String isleaf) {
        this.isleaf = isleaf;
    }

    public String getIsdelete() {
        return this.isdelete;
    }

    public void setIsdelete(String isdelete) {
        this.isdelete = isdelete;
    }

    public String getInserttime() {
        return this.inserttime;
    }

    public void setInserttime(String inserttime) {
        this.inserttime = inserttime;
    }

    public String getInsertuser() {
        return this.insertuser;
    }

    public void setInsertuser(String insertuser) {
        this.insertuser = insertuser;
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

    public Integer getOrdernum() {
        return this.ordernum;
    }

    public void setOrdernum(Integer ordernum) {
        this.ordernum = ordernum;
    }

    public String getPara() {
        return this.para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getReportType() {
        return this.reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getFn() {
        return this.fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getOpenAllSchool() {
        return this.openAllSchool;
    }

    public void setOpenAllSchool(String openAllSchool) {
        this.openAllSchool = openAllSchool;
    }

    public String toString() {
        return "Resource [ext1=" + this.ext1 + ", ext2=" + this.ext2 + ", ext3=" + this.ext3 + ", fn=" + this.fn + ", id=" + this.id + ", inserttime=" + this.inserttime + ", insertuser=" + this.insertuser + ", isdelete=" + this.isdelete + ", isleaf=" + this.isleaf + ", name=" + this.name + ", num=" + this.num + ", ordernum=" + this.ordernum + ", para=" + this.para + ", pnum=" + this.pnum + ", reportType=" + this.reportType + ", type=" + this.type + ", url=" + this.url + "]";
    }
}
