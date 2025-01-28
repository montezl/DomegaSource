package com.dmj.domain;

import java.io.Serializable;
import java.util.Date;

/* loaded from: Student.class */
public class Student implements Serializable {
    private String id;
    private String studentNum;
    private String studentName;
    private Integer gradeNum;
    private String classNum;
    private String classNum_num;
    private Integer schoolNum;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String description;
    private String note;
    private String studentId;
    private String oldName;
    private String sex;
    private Date birthday;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String ext5;
    private String ext6;
    private String ext7;
    private String type;
    private String nodel;
    private String answer;
    private String answer1;
    private String yzexaminationnum;
    private String subjectCombineNum;
    private String subjectCombineName;
    private String classType;
    private String scoreId;
    private String score;
    private String fullScore;
    private String xuankezuhe;
    private String xuejiSchool;
    private String xuejiClass;
    private String homeAddress;
    private String yuzhiPassword;
    private String examineeNum;
    private String examinationRoomNum;
    private String source;
    private int c_gRanking;
    public String subjectName;
    private Integer jie;
    private String schoolName;
    private String gradeName;
    private String className;
    private String studentType;
    private String studentTypeName;
    private String name;
    private String usertype;

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSubjectCombineNum() {
        return this.subjectCombineNum;
    }

    public void setSubjectCombineNum(String subjectCombineNum) {
        this.subjectCombineNum = subjectCombineNum;
    }

    public String getSubjectCombineName() {
        return this.subjectCombineName;
    }

    public void setSubjectCombineName(String subjectCombineName) {
        this.subjectCombineName = subjectCombineName;
    }

    public String getAnswer1() {
        return this.answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getC_gRanking() {
        return this.c_gRanking;
    }

    public void setC_gRanking(int c_gRanking) {
        this.c_gRanking = c_gRanking;
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

    public String getExt7() {
        return this.ext7;
    }

    public void setExt7(String ext7) {
        this.ext7 = ext7;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentType() {
        return this.studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public String getStudentTypeName() {
        return this.studentTypeName;
    }

    public void setStudentTypeName(String studentTypeName) {
        this.studentTypeName = studentTypeName;
    }

    public String getUsertype() {
        return this.usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public Student() {
    }

    public Student(String studentNum, String studentName, Integer gradeNum, String classNum, String insertUser, String insertDate, String updateUser, String updateDate, String schoolName) {
        this.studentNum = studentNum;
        this.studentName = studentName;
        this.gradeNum = gradeNum;
        this.classNum = classNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public Student(String studentNum, String studentName, Integer gradeNum, String classNum, Integer schoolNum, String insertUser, String insertDate, String updateUser, String updateDate, String description, String note, String studentId, String oldName, String sex, Date birthday, String isDelete, String ext1, String ext2, String ext3) {
        this.studentNum = studentNum;
        this.studentName = studentName;
        this.gradeNum = gradeNum;
        this.classNum = classNum;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.note = note;
        this.studentId = studentId;
        this.oldName = oldName;
        this.sex = sex;
        this.birthday = birthday;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
    }

    public Integer getJie() {
        return this.jie;
    }

    public void setJie(Integer jie) {
        this.jie = jie;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentNum() {
        return this.studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
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

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getOldName() {
        return this.oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClassNum_num() {
        return this.classNum_num;
    }

    public void setClassNum_num(String classNum_num) {
        this.classNum_num = classNum_num;
    }

    public String getNodel() {
        return this.nodel;
    }

    public void setNodel(String nodel) {
        this.nodel = nodel;
    }

    public String getYzexaminationnum() {
        return this.yzexaminationnum;
    }

    public void setYzexaminationnum(String yzexaminationnum) {
        this.yzexaminationnum = yzexaminationnum;
    }

    public String getClassType() {
        return this.classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getScoreId() {
        return this.scoreId;
    }

    public void setScoreId(String scoreId) {
        this.scoreId = scoreId;
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

    public String getXuankezuhe() {
        return this.xuankezuhe;
    }

    public void setXuankezuhe(String xuankezuhe) {
        this.xuankezuhe = xuankezuhe;
    }

    public String getXuejiSchool() {
        return this.xuejiSchool;
    }

    public void setXuejiSchool(String xuejiSchool) {
        this.xuejiSchool = xuejiSchool;
    }

    public String getXuejiClass() {
        return this.xuejiClass;
    }

    public void setXuejiClass(String xuejiClass) {
        this.xuejiClass = xuejiClass;
    }

    public String getHomeAddress() {
        return this.homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getYuzhiPassword() {
        return this.yuzhiPassword;
    }

    public void setYuzhiPassword(String yuzhiPassword) {
        this.yuzhiPassword = yuzhiPassword;
    }
}
