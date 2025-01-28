package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Examlog.class */
public class Examlog implements Serializable {
    private Integer id;
    private String operate;
    private Integer examNum;
    private Integer exampaperNum;
    private String studentId;
    private String inputStuId;
    private String classNum;
    private String insertUser;
    private String insertDate;
    private String description;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String examinationRoomNum;
    private Integer subjectNum;
    private String realname;
    private String studentName;
    private String className;
    private String examName;
    private String examDate;
    private String examType;
    private Integer gradeNum;
    private String gradeName;
    private String schoolName;
    private Integer schoolNum;
    private String inputSchNum;
    private String subjectName;
    private String examinationRoomName;

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Examlog() {
    }

    public Examlog(String insertUser, String insertDate) {
        this.insertUser = insertUser;
        this.insertDate = insertDate;
    }

    public Examlog(String operate, Integer examNum, Integer exampaperNum, String studentId, String classNum, String insertUser, String insertDate, String description, String isDelete, String ext1, String ext2, String ext3, String studentName, String className, String examName, String examDate, Integer gradeNum, String gradeName, String schoolName, String subjectName, String examType, String examinationRoomNum, String examinationRoomName, Integer subjectNum, Integer schoolNum) {
        this.operate = operate;
        this.examNum = examNum;
        this.exampaperNum = exampaperNum;
        this.studentId = studentId;
        this.classNum = classNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.studentName = studentName;
        this.className = className;
        this.examName = examName;
        this.examDate = examDate;
        this.gradeNum = gradeNum;
        this.gradeName = gradeName;
        this.schoolName = schoolName;
        this.subjectName = subjectName;
        this.examType = examType;
        this.examinationRoomNum = examinationRoomNum;
        this.examinationRoomName = examinationRoomName;
        this.subjectNum = subjectNum;
        this.schoolNum = schoolNum;
    }

    public String getOperate() {
        return this.operate;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
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

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamName() {
        return this.examName;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getExamDate() {
        return this.examDate;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public Integer getGradeNum() {
        return this.gradeNum;
    }

    public Integer getExamNum() {
        return this.examNum;
    }

    public void setExamNum(Integer examNum) {
        this.examNum = examNum;
    }

    public Integer getExampaperNum() {
        return this.exampaperNum;
    }

    public void setExampaperNum(Integer exampaperNum) {
        this.exampaperNum = exampaperNum;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getExamType() {
        return this.examType;
    }

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public void setExaminationRoomName(String examinationRoomName) {
        this.examinationRoomName = examinationRoomName;
    }

    public String getExaminationRoomName() {
        return this.examinationRoomName;
    }

    public void setSubjectNum(Integer subjectNum) {
        this.subjectNum = subjectNum;
    }

    public Integer getSubjectNum() {
        return this.subjectNum;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getInputStuId() {
        return this.inputStuId;
    }

    public void setInputStuId(String inputStuId) {
        this.inputStuId = inputStuId;
    }

    public String getInputSchNum() {
        return this.inputSchNum;
    }

    public void setInputSchNum(String inputSchNum) {
        this.inputSchNum = inputSchNum;
    }
}
