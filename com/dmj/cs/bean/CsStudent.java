package com.dmj.cs.bean;

import com.dmj.util.GUID;

/* loaded from: CsStudent.class */
public class CsStudent {
    private long studentId;
    private String cardNo;
    private String schoolNum;
    private String classNum;
    private boolean error;
    private String errorType;
    private String path;
    private long examRoomNum;
    private boolean scanByExamRoom;
    private String examineeNum;

    public CsStudent() {
        this.cardNo = "";
        this.schoolNum = "0";
        this.classNum = "-1";
        this.examRoomNum = -1L;
        this.examineeNum = "";
    }

    public CsStudent(String path, boolean scanByExamRoom, String examRoomNum) {
        this.cardNo = "";
        this.schoolNum = "0";
        this.classNum = "-1";
        this.examRoomNum = -1L;
        this.examineeNum = "";
        this.path = path;
        this.scanByExamRoom = scanByExamRoom;
        this.examRoomNum = Long.valueOf(examRoomNum).longValue();
    }

    public long getStudentId() {
        return this.studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public String getCardNo() {
        return this.cardNo == null ? "" : this.cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public boolean isError() {
        return this.error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorType() {
        return this.errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getExamRoomNum() {
        return this.examRoomNum;
    }

    public void setExamRoomNum(long examRoomNum) {
        this.examRoomNum = examRoomNum;
    }

    public String getExamineeNum() {
        return this.examineeNum;
    }

    public void setExamineeNum(String examineeNum) {
        this.examineeNum = examineeNum;
    }

    public void setInvalid() {
        this.studentId = GUID.getGUID();
        this.schoolNum = "0";
        this.classNum = "-1";
        this.error = true;
        if (!this.scanByExamRoom) {
            this.examRoomNum = -1L;
        }
        this.errorType = "0";
    }

    public CsStudent setRepeat() {
        this.studentId = GUID.getGUID();
        this.schoolNum = "0";
        this.classNum = "-1";
        this.error = true;
        if (!this.scanByExamRoom) {
            this.examRoomNum = -1L;
        }
        this.errorType = "1";
        return this;
    }

    public CsStudent resetCorrect() {
        this.error = false;
        this.errorType = null;
        return this;
    }
}
