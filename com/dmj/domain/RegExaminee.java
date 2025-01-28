package com.dmj.domain;

/* loaded from: RegExaminee.class */
public class RegExaminee {
    private String id;
    private int examPaperNum;
    private int schoolNum;
    private String studentId;
    private String examinationRoomNum;
    private int page;
    private String insertUser;
    private String insertDate;
    private String cNum;
    private int type;
    private String batch;
    private String isModify;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String classNum;
    private String pageStr;
    private String exam;
    private String subject;
    private String grade;
    private int examinationRoomLength;
    private int examineeLength;
    private String doubleFaced;
    private String oldStudentId;
    private String oldExamroomId;
    private String examinationRoomName;
    private String status;
    private String studentName;
    private String regId;
    private String realStudentId;
    private String testingCentreId;
    private String scan_import;
    private String scannum;
    private String examineeNum = "";

    public RegExaminee() {
    }

    public RegExaminee(String id, int examPaperNum, int schoolNum, String studentId, String examinationRoomNum, int page, String insertUser, String insertDate, String cNum, int type, String batch, String isModify, String ext1, String ext2, String ext3, String ext4, String classNum, String pageStr, String exam, String subject, String grade, int examinationRoomLength, int examineeLength, String doubleFaced, String oldStudentId, String oldExamroomId, String examinationRoomName, String status, String studentName, String regId, String testingCentreId) {
        this.id = id;
        this.examPaperNum = examPaperNum;
        this.schoolNum = schoolNum;
        this.studentId = studentId;
        this.examinationRoomNum = examinationRoomNum;
        this.page = page;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.cNum = cNum;
        this.type = type;
        this.batch = batch;
        this.isModify = isModify;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.ext4 = ext4;
        this.classNum = classNum;
        this.pageStr = pageStr;
        this.exam = exam;
        this.subject = subject;
        this.grade = grade;
        this.examinationRoomLength = examinationRoomLength;
        this.examineeLength = examineeLength;
        this.doubleFaced = doubleFaced;
        this.oldStudentId = oldStudentId;
        this.oldExamroomId = oldExamroomId;
        this.examinationRoomName = examinationRoomName;
        this.status = status;
        this.studentName = studentName;
        this.regId = regId;
        this.testingCentreId = testingCentreId;
    }

    public String toString() {
        return "RegExaminee [cNum=" + this.cNum + ", exam=" + this.exam + ", examPaperNum=" + this.examPaperNum + ", examinationRoomLength=" + this.examinationRoomLength + ", examinationRoomNum=" + this.examinationRoomNum + ", examineeLength=" + this.examineeLength + ", ext1=" + this.ext1 + ", ext2=" + this.ext2 + ", ext3=" + this.ext3 + ", ext4=" + this.ext4 + ", grade=" + this.grade + ", id=" + this.id + ", insertDate=" + this.insertDate + ", insertUser=" + this.insertUser + ", isModify=" + this.isModify + ", page=" + this.page + ", schoolNum=" + this.schoolNum + ", studentId=" + this.studentId + ", subject=" + this.subject + ", type=" + this.type + "]";
    }

    public int getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(int examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public int getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(int schoolNum) {
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

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
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

    public String getcNum() {
        return this.cNum;
    }

    public void setcNum(String cNum) {
        this.cNum = cNum;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBatch() {
        return this.batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getIsModify() {
        return this.isModify;
    }

    public void setIsModify(String isModify) {
        this.isModify = isModify;
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

    public String getExt4() {
        return this.ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getPageStr() {
        return this.pageStr;
    }

    public void setPageStr(String pageStr) {
        this.pageStr = pageStr;
    }

    public String getExam() {
        return this.exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return this.grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getExaminationRoomLength() {
        return this.examinationRoomLength;
    }

    public void setExaminationRoomLength(int examinationRoomLength) {
        this.examinationRoomLength = examinationRoomLength;
    }

    public int getExamineeLength() {
        return this.examineeLength;
    }

    public void setExamineeLength(int examineeLength) {
        this.examineeLength = examineeLength;
    }

    public String getDoubleFaced() {
        return this.doubleFaced;
    }

    public void setDoubleFaced(String doubleFaced) {
        this.doubleFaced = doubleFaced;
    }

    public String getOldStudentId() {
        return this.oldStudentId;
    }

    public void setOldStudentId(String oldStudentId) {
        this.oldStudentId = oldStudentId;
    }

    public String getOldExamroomId() {
        return this.oldExamroomId;
    }

    public void setOldExamroomId(String oldExamroomId) {
        this.oldExamroomId = oldExamroomId;
    }

    public String getExaminationRoomName() {
        return this.examinationRoomName;
    }

    public void setExaminationRoomName(String examinationRoomName) {
        this.examinationRoomName = examinationRoomName;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegId() {
        return this.regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealStudentId() {
        return this.realStudentId;
    }

    public void setRealStudentId(String realStudentId) {
        this.realStudentId = realStudentId;
    }

    public String getTestingCentreId() {
        return this.testingCentreId;
    }

    public void setTestingCentreId(String testingCentreId) {
        this.testingCentreId = testingCentreId;
    }

    public String getScan_import() {
        return this.scan_import;
    }

    public void setScan_import(String scan_import) {
        this.scan_import = scan_import;
    }

    public String getScannum() {
        return this.scannum;
    }

    public void setScannum(String scannum) {
        this.scannum = scannum;
    }

    public String getExamineeNum() {
        return this.examineeNum;
    }

    public void setExamineeNum(String examineeNum) {
        this.examineeNum = examineeNum;
    }
}
