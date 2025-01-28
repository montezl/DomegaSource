package com.dmj.domain;

/* loaded from: AjaxData.class */
public class AjaxData {
    private String num;
    private String name;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String ext5;

    public AjaxData() {
    }

    public AjaxData(String num, String name) {
        this.num = num;
        this.name = name;
    }

    public AjaxData(String num, String name, String ext1, String ext2) {
        this.num = num;
        this.name = name;
        this.ext1 = ext1;
        this.ext2 = ext2;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getExt5() {
        return this.ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }
}
