package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Score.class */
public class Score implements Serializable {
    private String id;
    private String questionNum;
    private double questionScore;
    private String studentId;
    private Integer examPaperNum;
    private String answer;
    private String tag;
    private String notToScore;
    private String isModify;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String description;
    private Integer schoolNum;
    private String classNum;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String scoreall;
    private String isAppend;
    private String yanswer;
    private String qtype;
    private String ext4;
    private String ext5;
    private String ext6;
    private String ext7;
    private String ext8;
    private int page;
    private String type;
    private String studentNum;
    private Integer gradeNum;
    private String gradeName;
    private String schoolName;
    private String studentName;
    private double totalScore;
    private String objItems;
    private String examinationRoomNum;
    private String examinationRoomName;
    private String examNum;
    private String subjectNum;
    private String className;
    private double fullScore;
    private double step;
    private double frequency;
    private int noscore;
    private int upfullscore;
    private int norecognized;
    private String isException;
    private double regScore;
    private double avgScore;
    private String subExamPaperNum;
    private String examDate;
    private String regId;
    private String clipRegId;
    private String cRegId;
    private String ceStatus;
    private double quesScorePer;
    private Integer totalPage;
    private String realQuestionNum;
    private String realStudentId;
    private String continued;
    private String questionName;
    private String cross_page;
    private String batch;
    private String choosename;
    private String isParent;
    private String defId;
    private String testingCentreId;
    private String regResult;

    public String getBatch() {
        return this.batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Score() {
    }

    public Score(String questionNum, double questionScore, String studentId, String insertUser, String insertDate, String updateUser, String updateDate) {
        this.questionNum = questionNum;
        this.questionScore = questionScore;
        this.studentId = studentId;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public Score(String id, String questionNum, double questionScore, String studentId, Integer examPaperNum, String answer, String qtype, String tag, String notToScore, String isModify, String insertUser, String insertDate, String updateUser, String updateDate, String description, Integer schoolNum, String classNum, String isDelete, String ext1, String ext2, String ext3, String isAppend, String yanswer, int page, Integer gradeNum, String schoolName, String studentName, double totalScore, String objItems, String examinationRoomNum, String examNum, String subjectNum, String className, double fullScore, double step, double frequency, int noscore, int upfullscore, int norecognized, String isException, double regScore, double avgScore, String regId, String clipRegId, String ceStatus, double quesScorePer, String cRegId, String realQuestionNum, String choosename, String isParent, String defId, String testingCentreId) {
        this.id = id;
        this.questionNum = questionNum;
        this.questionScore = questionScore;
        this.studentId = studentId;
        this.examPaperNum = examPaperNum;
        this.answer = answer;
        this.qtype = qtype;
        this.tag = tag;
        this.notToScore = notToScore;
        this.isModify = isModify;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.schoolNum = schoolNum;
        this.classNum = classNum;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.isAppend = isAppend;
        this.yanswer = yanswer;
        this.page = page;
        this.gradeNum = gradeNum;
        this.schoolName = schoolName;
        this.studentName = studentName;
        this.totalScore = totalScore;
        this.objItems = objItems;
        this.examinationRoomNum = examinationRoomNum;
        this.examNum = examNum;
        this.subjectNum = subjectNum;
        this.className = className;
        this.fullScore = fullScore;
        this.step = step;
        this.frequency = frequency;
        this.noscore = noscore;
        this.upfullscore = upfullscore;
        this.norecognized = norecognized;
        this.isException = isException;
        this.regScore = regScore;
        this.avgScore = avgScore;
        this.regId = regId;
        this.clipRegId = clipRegId;
        this.ceStatus = ceStatus;
        this.quesScorePer = quesScorePer;
        this.cRegId = cRegId;
        this.realQuestionNum = realQuestionNum;
        this.choosename = choosename;
        this.isParent = isParent;
        this.defId = defId;
        this.testingCentreId = testingCentreId;
    }

    public String getScoreall() {
        return this.scoreall;
    }

    public void setScoreall(String scoreall) {
        this.scoreall = scoreall;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public Double getQuestionScore() {
        return Double.valueOf(this.questionScore);
    }

    public void setQuestionScore(Double questionScore) {
        this.questionScore = questionScore.doubleValue();
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(Integer examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQtype() {
        return this.qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNotToScore() {
        return this.notToScore;
    }

    public void setNotToScore(String notToScore) {
        this.notToScore = notToScore;
    }

    public String getIsModify() {
        return this.isModify;
    }

    public void setIsModify(String isModify) {
        this.isModify = isModify;
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

    public Integer getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public double getTotalScore() {
        return this.totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public String getObjItems() {
        return this.objItems;
    }

    public void setObjItems(String objItems) {
        this.objItems = objItems;
    }

    public void setQuestionScore(double questionScore) {
        this.questionScore = questionScore;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return this.page;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public String getExamNum() {
        return this.examNum;
    }

    public void setExamNum(String examNum) {
        this.examNum = examNum;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getIsAppend() {
        return this.isAppend;
    }

    public void setIsAppend(String isAppend) {
        this.isAppend = isAppend;
    }

    public String getYanswer() {
        return this.yanswer;
    }

    public void setYanswer(String yanswer) {
        this.yanswer = yanswer;
    }

    public void setFullScore(double fullScore) {
        this.fullScore = fullScore;
    }

    public double getFullScore() {
        return this.fullScore;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public double getStep() {
        return this.step;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getFrequency() {
        return this.frequency;
    }

    public void setNoscore(int noscore) {
        this.noscore = noscore;
    }

    public int getNoscore() {
        return this.noscore;
    }

    public void setUpfullscore(int upfullscore) {
        this.upfullscore = upfullscore;
    }

    public int getUpfullscore() {
        return this.upfullscore;
    }

    public void setNorecognized(int norecognized) {
        this.norecognized = norecognized;
    }

    public int getNorecognized() {
        return this.norecognized;
    }

    public void setIsException(String isException) {
        this.isException = isException;
    }

    public String getIsException() {
        return this.isException;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRegScore() {
        return this.regScore;
    }

    public void setRegScore(double regScore) {
        this.regScore = regScore;
    }

    public double getAvgScore() {
        return this.avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    public String getSubExamPaperNum() {
        return this.subExamPaperNum;
    }

    public void setSubExamPaperNum(String subExamPaperNum) {
        this.subExamPaperNum = subExamPaperNum;
    }

    public String getExamDate() {
        return this.examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getCeStatus() {
        return this.ceStatus;
    }

    public void setCeStatus(String ceStatus) {
        this.ceStatus = ceStatus;
    }

    public double getQuesScorePer() {
        return this.quesScorePer;
    }

    public void setQuesScorePer(double quesScorePer) {
        this.quesScorePer = quesScorePer;
    }

    public String getExaminationRoomName() {
        return this.examinationRoomName;
    }

    public void setExaminationRoomName(String examinationRoomName) {
        this.examinationRoomName = examinationRoomName;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public Integer getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public String getClipRegId() {
        return this.clipRegId;
    }

    public void setClipRegId(String clipRegId) {
        this.clipRegId = clipRegId;
    }

    public String getcRegId() {
        return this.cRegId;
    }

    public void setcRegId(String cRegId) {
        this.cRegId = cRegId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegId() {
        return this.regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getRealQuestionNum() {
        return this.realQuestionNum;
    }

    public void setRealQuestionNum(String realQuestionNum) {
        this.realQuestionNum = realQuestionNum;
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

    public String getRealStudentId() {
        return this.realStudentId;
    }

    public void setRealStudentId(String realStudentId) {
        this.realStudentId = realStudentId;
    }

    public String getContinued() {
        return this.continued;
    }

    public void setContinued(String continued) {
        this.continued = continued;
    }

    public String getQuestionName() {
        return this.questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getStudentNum() {
        return this.studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getCross_page() {
        return this.cross_page;
    }

    public void setCross_page(String cross_page) {
        this.cross_page = cross_page;
    }

    public String getChoosename() {
        return this.choosename;
    }

    public void setChoosename(String choosename) {
        this.choosename = choosename;
    }

    public String getIsParent() {
        return this.isParent;
    }

    public void setIsParent(String isParent) {
        this.isParent = isParent;
    }

    public String getDefId() {
        return this.defId;
    }

    public void setDefId(String defId) {
        this.defId = defId;
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

    public String getTestingCentreId() {
        return this.testingCentreId;
    }

    public void setTestingCentreId(String testingCentreId) {
        this.testingCentreId = testingCentreId;
    }

    public String getRegResult() {
        return this.regResult;
    }

    public void setRegResult(String regResult) {
        this.regResult = regResult;
    }
}
