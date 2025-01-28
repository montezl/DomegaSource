package com.dmj.domain;

import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.zht.db.ServiceFactory;
import java.io.Serializable;
import org.apache.log4j.Logger;

/* loaded from: Class.class */
public class Class implements Serializable {
    private String id;
    private String classNum;
    private String className;
    private Integer gradeNum;
    private Integer schoolNum;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String description;
    private String classType;
    private String levelsubject;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private Integer jie;
    private String checked;
    private String studentType;
    private String rkjs;
    private String islevel;
    Logger log = Logger.getLogger(getClass());
    public static SystemService dao = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    private String classTypeName;
    private String studentTypeName;
    private String gradeName;
    private String schoolName;
    private String subjectName;

    public String getStudentTypeName() {
        return this.studentTypeName;
    }

    public void setStudentTypeName(String studentTypeName) {
        this.studentTypeName = studentTypeName;
    }

    public String getClassTypeName() {
        return this.classTypeName;
    }

    public void setClassTypeName(String classTypeName) {
        this.classTypeName = classTypeName;
    }

    public Integer getJie() {
        return this.jie;
    }

    public void setJie(Integer jie) {
        this.jie = jie;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Class() {
    }

    public Class(String classNum, String className, Integer gradeNum, Integer schoolNum, String insertUser, String insertDate, String updateUser, String updateDate) {
        this.classNum = classNum;
        this.className = className;
        this.gradeNum = gradeNum;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public Class(String classNum, String className, Integer gradeNum, Integer schoolNum, String insertUser, String insertDate, String updateUser, String updateDate, String description, String classType, String levelsubject, String isDelete, String ext1, String ext2, String ext3, String studentType) {
        this.classNum = classNum;
        this.className = className;
        this.gradeNum = gradeNum;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.classType = classType;
        this.levelsubject = levelsubject;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.studentType = studentType;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
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

    public String getClassType() {
        return this.classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getLevelsubject() {
        return this.levelsubject;
    }

    public void setLevelsubject(String levelsubject) {
        this.levelsubject = levelsubject;
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

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getChecked() {
        return this.checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getStudentType() {
        return this.studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public String getRkjs() {
        return this.rkjs;
    }

    public void setRkjs(String rkjs) {
        this.rkjs = rkjs;
    }

    public String getIslevel() {
        return this.islevel;
    }

    public void setIslevel(String islevel) {
        this.islevel = islevel;
    }
}
