package com.dmj.domain;

/* loaded from: TreeData.class */
public class TreeData {
    private String id;
    private String pid;
    private String name;
    private String stage;
    private String isLeaf;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TreeData(String id, String pid, String name, String stage) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.stage = stage;
    }

    public TreeData(String id, String pid, String name, String stage, String isLeaf) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.stage = stage;
        this.isLeaf = isLeaf;
    }

    public String getStage() {
        return this.stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public TreeData() {
    }

    public String toString() {
        return "TreeData [id=" + this.id + ", name=" + this.name + ", pid=" + this.pid + "]";
    }

    public String getIsLeaf() {
        return this.isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }
}
