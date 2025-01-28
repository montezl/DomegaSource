package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Examinationnumimg.class */
public class Examinationnumimg implements Serializable {
    private String id;
    private String examPaperNum;
    private String schoolNum;
    private String studentId;
    private String examinationRoomNum;
    private byte[] img;
    private int page;
    private String cNum;
    private String type;
    private String insertUser;
    private String insertDate;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String examineeNum;
    private String studentName;
    private String know;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(String examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public byte[] getImg() {
        return this.img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getcNum() {
        return this.cNum;
    }

    public void setcNum(String cNum) {
        this.cNum = cNum;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getKnow() {
        return this.know;
    }

    public void setKnow(String know) {
        this.know = know;
    }
}
