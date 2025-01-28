package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Examinationnum.class */
public class Examinationnum implements Serializable {
    private Integer id;
    private String examinationRoomNum;
    private String examineeNum;
    private String studentID;
    private String studentId;
    private String insertUser;
    private String insertDate;
    private String description;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String ext5;
    private String ext6;
    private String ext7;
    private String ext8;
    private Integer examNum;
    private String schoolNum;
    private Integer gradeNum;
    private String studentName;
    private String examinationRoomName;
    private String eRoomNum;
    private String exists;
    private String schoolName;
    private String gradeName;
    private String className;
    private String classNum;
    private String stuId;
    private String testingCentreName;
    private String subjectName;
    private String subjectNum;
    private String seatNum;
    private String testingCentreId;
    private String subjectCombineNum;

    public Examinationnum() {
    }

    public Examinationnum(String examinationRoomNum, String examineeNum, String studentID, String insertUser, String insertDate) {
        this.examinationRoomNum = examinationRoomNum;
        this.examineeNum = examineeNum;
        setStudentID(studentID);
        this.insertUser = insertUser;
        this.insertDate = insertDate;
    }

    public Examinationnum(String examinationRoomNum, String examineeNum, String studentID, String insertUser, String insertDate, String description, String isDelete, String ext1, String ext2, String ext3, Integer examNum, String schoolNum, Integer gradeNum, String studentName, String examinationRoomName, String eRoomNum, String ext4) {
        this.examinationRoomNum = examinationRoomNum;
        this.examineeNum = examineeNum;
        setStudentID(studentID);
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.examNum = examNum;
        this.schoolNum = schoolNum;
        this.gradeNum = gradeNum;
        this.studentName = studentName;
        this.examinationRoomName = examinationRoomName;
        this.eRoomNum = eRoomNum;
        this.ext4 = ext4;
    }

    public String getSubjectCombineNum() {
        return this.subjectCombineNum;
    }

    public void setSubjectCombineNum(String subjectCombineNum) {
        this.subjectCombineNum = subjectCombineNum;
    }

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public String getExamineeNum() {
        return this.examineeNum;
    }

    public void setExamineeNum(String examineeNum) {
        this.examineeNum = examineeNum;
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

    public Integer getExamNum() {
        return this.examNum;
    }

    public void setExamNum(Integer examNum) {
        this.examNum = examNum;
    }

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public Integer getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentID() {
        return this.studentID;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setExaminationRoomName(String examinationRoomName) {
        this.examinationRoomName = examinationRoomName;
    }

    public String getExaminationRoomName() {
        return this.examinationRoomName;
    }

    public void seteRoomNum(String eRoomNum) {
        this.eRoomNum = eRoomNum;
    }

    public String geteRoomNum() {
        return this.eRoomNum;
    }

    public String getExists() {
        return this.exists;
    }

    public void setExists(String exists) {
        this.exists = exists;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getExt4() {
        return this.ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getStuId() {
        return this.stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
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

    public String getExt7() {
        return this.ext7;
    }

    public void setExt7(String ext7) {
        this.ext7 = ext7;
    }

    public String getExt8() {
        return this.ext8;
    }

    public void setExt8(String ext8) {
        this.ext8 = ext8;
    }

    public String getTestingCentreName() {
        return this.testingCentreName;
    }

    public void setTestingCentreName(String testingCentreName) {
        this.testingCentreName = testingCentreName;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSeatNum() {
        return this.seatNum;
    }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getTestingCentreId() {
        return this.testingCentreId;
    }

    public void setTestingCentreId(String testingCentreId) {
        this.testingCentreId = testingCentreId;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
