package com.dmj.domain;

/* loaded from: ExamineeNumError.class */
public class ExamineeNumError {
    private Integer id;
    private String regId;
    private Integer examPaperNum;
    private Integer schoolNum;
    private String studentId;
    private String errorType;
    private String examinationRoomNum;
    private String examineeNum;
    private String groupNum;
    private int page;
    private String insertUser;
    private String insertDate;
    private String batch;
    private String isDelete;
    private String description;
    private String ext1;
    private String ext2;
    private String ext3;
    private String examName;
    private String examNum;
    private String subjectName;
    private String subjectNum;
    private String schoolName;
    private String gradeName;
    private String gradeNum;
    private String examinationRoomName;
    private String eRoomNum;
    private String testingCentreId;

    public ExamineeNumError() {
    }

    public ExamineeNumError(Integer id, String regId, Integer examPaperNum, Integer schoolNum, String studentId, String errorType, String examinationRoomNum, String examineeNum, String groupNum, int page, String insertUser, String insertDate, String batch, String isDelete, String description, String ext1, String ext2, String ext3, String examName, String examNum, String subjectName, String subjectNum, String schoolName, String gradeName, String gradeNum, String examinationRoomName, String eRoomNum, String testingCentreId) {
        this.id = id;
        this.regId = regId;
        this.examPaperNum = examPaperNum;
        this.schoolNum = schoolNum;
        this.studentId = studentId;
        this.errorType = errorType;
        this.examinationRoomNum = examinationRoomNum;
        this.examineeNum = examineeNum;
        this.groupNum = groupNum;
        this.page = page;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.batch = batch;
        this.isDelete = isDelete;
        this.description = description;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.examName = examName;
        this.examNum = examNum;
        this.subjectName = subjectName;
        this.subjectNum = subjectNum;
        this.schoolName = schoolName;
        this.gradeName = gradeName;
        this.gradeNum = gradeNum;
        this.examinationRoomName = examinationRoomName;
        this.eRoomNum = eRoomNum;
        this.testingCentreId = testingCentreId;
    }

    public ExamineeNumError(String regId, Integer examPaperNum, Integer schoolNum, String studentId, String errorType, String examinationRoomNum, String examineeNum, String groupNum, int page, String insertUser, String insertDate, String testingCentreId) {
        this.regId = regId;
        this.examPaperNum = examPaperNum;
        this.schoolNum = schoolNum;
        this.studentId = studentId;
        this.errorType = errorType;
        this.examinationRoomNum = examinationRoomNum;
        this.examineeNum = examineeNum;
        this.groupNum = groupNum;
        this.page = page;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.testingCentreId = testingCentreId;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(Integer examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getErrorType() {
        return this.errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
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

    public String getBatch() {
        return this.batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
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

    public String getExamName() {
        return this.examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamNum() {
        return this.examNum;
    }

    public void setExamNum(String examNum) {
        this.examNum = examNum;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
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

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getExaminationRoomName() {
        return this.examinationRoomName;
    }

    public void setExaminationRoomName(String examinationRoomName) {
        this.examinationRoomName = examinationRoomName;
    }

    public String geteRoomNum() {
        return this.eRoomNum;
    }

    public void seteRoomNum(String eRoomNum) {
        this.eRoomNum = eRoomNum;
    }

    public String getGroupNum() {
        return this.groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public String getRegId() {
        return this.regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getTestingCentreId() {
        return this.testingCentreId;
    }

    public void setTestingCentreId(String testingCentreId) {
        this.testingCentreId = testingCentreId;
    }
}
