package com.dmj.domain;

import java.io.Serializable;
import java.util.List;

/* loaded from: Remark.class */
public class Remark implements Serializable {
    private String id;
    private String scoreId;
    private Integer exampaperNum;
    private String questionNum;
    private String questionScore;
    private String type;
    private String userNum;
    private String isException;
    private Integer rownum;
    private String status;
    private String insertUser;
    private String insertDate;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String ext5;
    private String ext6;
    private String isModify;
    private String description;
    private int chongpan;
    private int caijue;
    private String studentNum;
    private String studentId;
    private Integer schoolNum;
    private String classNum;
    private Integer gradeNum;
    private String examinationRoomNum;
    private String testMark;
    private String cross_page;
    private String count;
    private String count1;
    private String count2;
    private String teacherName;
    private String updateTime;
    private String orderNum;
    private String groupName;
    private String score;
    private String fullScore;
    private String index;
    private List aList;

    public Remark() {
    }

    public Remark(String id, String scoreId, Integer exampaperNum, String questionNum, String questionScore, String type, String userNum, String isException, Integer rownum, String status, String insertUser, String insertDate, String ext1, String ext2, String ext3, String isModify, String description, int chongpan, int caijue, String studentNum, String studentId, Integer schoolNum, String classNum, Integer gradeNum, String examinationRoomNum, String testMark, String cross_page) {
        this.id = id;
        this.scoreId = scoreId;
        this.exampaperNum = exampaperNum;
        this.questionNum = questionNum;
        this.questionScore = questionScore;
        this.type = type;
        this.userNum = userNum;
        this.isException = isException;
        this.rownum = rownum;
        this.status = status;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.isModify = isModify;
        this.description = description;
        this.chongpan = chongpan;
        this.caijue = caijue;
        this.studentNum = studentNum;
        this.studentId = studentId;
        this.schoolNum = schoolNum;
        this.classNum = classNum;
        this.gradeNum = gradeNum;
        this.examinationRoomNum = examinationRoomNum;
        this.testMark = testMark;
        this.cross_page = cross_page;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScoreId() {
        return this.scoreId;
    }

    public void setScoreId(String scoreId) {
        this.scoreId = scoreId;
    }

    public Integer getExampaperNum() {
        return this.exampaperNum;
    }

    public void setExampaperNum(Integer exampaperNum) {
        this.exampaperNum = exampaperNum;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getQuestionScore() {
        return this.questionScore;
    }

    public void setQuestionScore(String questionScore) {
        this.questionScore = questionScore;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserNum() {
        return this.userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getIsException() {
        return this.isException;
    }

    public void setIsException(String isException) {
        this.isException = isException;
    }

    public Integer getRownum() {
        return this.rownum;
    }

    public void setRownum(Integer rownum) {
        this.rownum = rownum;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getIsModify() {
        return this.isModify;
    }

    public void setIsModify(String isModify) {
        this.isModify = isModify;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getChongpan() {
        return this.chongpan;
    }

    public void setChongpan(int chongpan) {
        this.chongpan = chongpan;
    }

    public int getCaijue() {
        return this.caijue;
    }

    public void setCaijue(int caijue) {
        this.caijue = caijue;
    }

    public String getStudentNum() {
        return this.studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public Integer getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public String getTestMark() {
        return this.testMark;
    }

    public void setTestMark(String testMark) {
        this.testMark = testMark;
    }

    public String getCross_page() {
        return this.cross_page;
    }

    public void setCross_page(String cross_page) {
        this.cross_page = cross_page;
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

    public String getExt6() {
        return this.ext6;
    }

    public void setExt6(String ext6) {
        this.ext6 = ext6;
    }

    public String getCount() {
        return this.count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCount1() {
        return this.count1;
    }

    public void setCount1(String count1) {
        this.count1 = count1;
    }

    public String getCount2() {
        return this.count2;
    }

    public void setCount2(String count2) {
        this.count2 = count2;
    }

    public String getTeacherName() {
        return this.teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOrderNum() {
        return this.orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getScore() {
        return this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getFullScore() {
        return this.fullScore;
    }

    public void setFullScore(String fullScore) {
        this.fullScore = fullScore;
    }

    public String getIndex() {
        return this.index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public List getaList() {
        return this.aList;
    }

    public void setaList(List aList) {
        this.aList = aList;
    }
}
