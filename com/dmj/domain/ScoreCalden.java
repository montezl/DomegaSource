package com.dmj.domain;

/* loaded from: ScoreCalden.class */
public class ScoreCalden {
    private String schoolName;
    private String examinationRoomNum;
    private String examineeNum;
    private String studentName;
    private String classNum;
    private String studentId;
    private String subjectName;
    private double fullScore = 0.0d;
    private double fullScoreS = 0.0d;
    private double totalScore;

    public double getFullScoreS() {
        return this.fullScoreS;
    }

    public void setFullScoreS(double fullScoreS) {
        this.fullScoreS = fullScoreS;
    }

    public double getFullScore() {
        return this.fullScore;
    }

    public void setFullScore(double scoreNum) {
        this.fullScore = scoreNum;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public double getTotalScore() {
        return this.totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
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

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
