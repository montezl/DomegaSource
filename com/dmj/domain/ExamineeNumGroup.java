package com.dmj.domain;

/* loaded from: ExamineeNumGroup.class */
public class ExamineeNumGroup {
    private String id;
    private String examNum;
    private String schoolNum;
    private String groupNum;
    private String groupName;
    private String gradeNum;
    private String groupType;
    private String insertUser;
    private String insertDate;
    private String description;
    private String ext1;
    private String ext2;
    private String ext3;
    private String gradeName;

    public ExamineeNumGroup() {
    }

    public ExamineeNumGroup(String id, String examNum, String schoolNum, String groupNum, String groupName, String gradeNum, String groupType, String insertUser, String insertDate, String description, String ext1, String ext2, String ext3) {
        this.id = id;
        this.examNum = examNum;
        this.schoolNum = schoolNum;
        this.groupNum = groupNum;
        this.groupName = groupName;
        this.gradeNum = gradeNum;
        this.groupType = groupType;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.description = description;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
    }

    public ExamineeNumGroup(String id, String examNum, String schoolNum, String groupNum, String groupName, String groupType, String insertUser, String insertDate, String description, String ext1, String ext2, String ext3) {
        this.id = id;
        this.examNum = examNum;
        this.schoolNum = schoolNum;
        this.groupNum = groupNum;
        this.groupName = groupName;
        this.groupType = groupType;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.description = description;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
    }

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

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getGroupNum() {
        return this.groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return this.groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
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

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }
}
