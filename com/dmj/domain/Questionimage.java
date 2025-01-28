package com.dmj.domain;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.math.BigDecimal;

/* loaded from: Questionimage.class */
public class Questionimage implements Serializable {
    private String id;
    private Integer examPaperNum;
    private String scoreId;
    private String studentId;
    private String questionNum;
    private String groupNum;
    private String questionName;
    private String img;
    private String img1;
    private String img3;
    private String img2;
    private Integer page;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String description;
    private Integer schoolNum;
    private String schoolName;
    private Integer gradeNum;
    private String gradeName;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String questionType;
    private String studentName;
    private BigDecimal questionScore;
    private String answer;
    private BigDecimal fullScore;
    private String stdAnswer;
    private String isException;
    private String classNum;
    private String className;
    private String examinationRoomNum;
    private String examinationRoomName;
    private String shortName;
    private String tag;
    private String subjectNum;
    private String subjectName;
    private String num;
    private String name;
    private String orderNum;
    private String realStudentId;
    private String imgpath;
    private String cross_page;
    private String questionW;
    private String questionH;
    private String scoreW;
    private String scoreH;
    private String questionX;
    private String questionY;
    private String oqts;
    private String sqts;
    private String type;
    private String dengji;
    private BigDecimal oqtsFullScore;
    private BigDecimal sqtsFullScore;
    private String studentReportShowItem;
    private String jisuanType;
    private byte[] imgByte;
    private Integer questionHCurrent;
    private Integer questionHSum;

    public Questionimage() {
    }

    public Questionimage fill() {
        try {
            if (StrUtil.isNotEmpty(this.img) && FileUtil.exist(this.img)) {
                byte[] imgBytes = FileUtil.readBytes(this.img);
                this.imgByte = imgBytes;
            }
        } catch (Exception e) {
        }
        return this;
    }

    public Questionimage(String id, Integer examPaperNum, String scoreId, String studentId, String questionNum, String img, String img1, String img3, String img2, Integer page, String insertUser, String insertDate, String updateUser, String updateDate, String description, Integer schoolNum, String schoolName, Integer gradeNum, String gradeName, String isDelete, String ext1, String ext2, String ext3, String questionType, String studentName, BigDecimal questionScore, String answer, BigDecimal fullScore, String stdAnswer, String isException, String classNum, String className, String examinationRoomNum, String examinationRoomName, String tag, String subjectNum, String subjectName, String num, String name, String orderNum, String realStudentId, String imgpath, String questionW, String questionH, String groupNum) {
        this.id = id;
        this.examPaperNum = examPaperNum;
        this.scoreId = scoreId;
        this.studentId = studentId;
        this.questionNum = questionNum;
        this.img = img;
        this.img1 = img1;
        this.img3 = img3;
        this.img2 = img2;
        this.page = page;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.schoolNum = schoolNum;
        this.schoolName = schoolName;
        this.gradeNum = gradeNum;
        this.gradeName = gradeName;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.questionType = questionType;
        this.studentName = studentName;
        this.questionScore = questionScore;
        this.answer = answer;
        this.fullScore = fullScore;
        this.stdAnswer = stdAnswer;
        this.isException = isException;
        this.classNum = classNum;
        this.className = className;
        this.examinationRoomNum = examinationRoomNum;
        this.examinationRoomName = examinationRoomName;
        this.tag = tag;
        this.subjectNum = subjectNum;
        this.subjectName = subjectName;
        this.num = num;
        this.name = name;
        this.orderNum = orderNum;
        this.realStudentId = realStudentId;
        this.imgpath = imgpath;
        this.questionW = questionW;
        this.questionH = questionH;
        this.groupNum = groupNum;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(Integer examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public Integer getQuestionHCurrent() {
        return this.questionHCurrent;
    }

    public void setQuestionHCurrent(Integer questionHCurrent) {
        this.questionHCurrent = questionHCurrent;
    }

    public Integer getQuestionHSum() {
        return this.questionHSum;
    }

    public void setQuestionHSum(Integer questionHSum) {
        this.questionHSum = questionHSum;
    }

    public String getScoreId() {
        return this.scoreId;
    }

    public void setScoreId(String scoreId) {
        this.scoreId = scoreId;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg1() {
        return this.img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg3() {
        return this.img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg2() {
        return this.img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
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

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Integer getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
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

    public String getQuestionType() {
        return this.questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public BigDecimal getQuestionScore() {
        return this.questionScore;
    }

    public void setQuestionScore(BigDecimal questionScore) {
        this.questionScore = questionScore;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public BigDecimal getFullScore() {
        return this.fullScore;
    }

    public void setFullScore(BigDecimal fullScore) {
        this.fullScore = fullScore;
    }

    public String getStdAnswer() {
        return this.stdAnswer;
    }

    public void setStdAnswer(String stdAnswer) {
        this.stdAnswer = stdAnswer;
    }

    public String getIsException() {
        return this.isException;
    }

    public void setIsException(String isException) {
        this.isException = isException;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public String getExaminationRoomName() {
        return this.examinationRoomName;
    }

    public void setExaminationRoomName(String examinationRoomName) {
        this.examinationRoomName = examinationRoomName;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderNum() {
        return this.orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getRealStudentId() {
        return this.realStudentId;
    }

    public void setRealStudentId(String realStudentId) {
        this.realStudentId = realStudentId;
    }

    public String getQuestionName() {
        return this.questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getImgpath() {
        return this.imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getCross_page() {
        return this.cross_page;
    }

    public void setCross_page(String cross_page) {
        this.cross_page = cross_page;
    }

    public String getQuestionW() {
        return this.questionW;
    }

    public void setQuestionW(String questionW) {
        this.questionW = questionW;
    }

    public String getQuestionH() {
        return this.questionH;
    }

    public void setQuestionH(String questionH) {
        this.questionH = questionH;
    }

    public String getScoreW() {
        return this.scoreW;
    }

    public void setScoreW(String scoreW) {
        this.scoreW = scoreW;
    }

    public String getScoreH() {
        return this.scoreH;
    }

    public void setScoreH(String scoreH) {
        this.scoreH = scoreH;
    }

    public String getQuestionX() {
        return this.questionX;
    }

    public void setQuestionX(String questionX) {
        this.questionX = questionX;
    }

    public String getQuestionY() {
        return this.questionY;
    }

    public void setQuestionY(String questionY) {
        this.questionY = questionY;
    }

    public String getOqts() {
        return this.oqts;
    }

    public void setOqts(String oqts) {
        this.oqts = oqts;
    }

    public String getSqts() {
        return this.sqts;
    }

    public void setSqts(String sqts) {
        this.sqts = sqts;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupNum() {
        return this.groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public BigDecimal getOqtsFullScore() {
        return this.oqtsFullScore;
    }

    public void setOqtsFullScore(BigDecimal oqtsFullScore) {
        this.oqtsFullScore = oqtsFullScore;
    }

    public BigDecimal getSqtsFullScore() {
        return this.sqtsFullScore;
    }

    public void setSqtsFullScore(BigDecimal sqtsFullScore) {
        this.sqtsFullScore = sqtsFullScore;
    }

    public String getStudentReportShowItem() {
        return this.studentReportShowItem;
    }

    public void setStudentReportShowItem(String studentReportShowItem) {
        this.studentReportShowItem = studentReportShowItem;
    }

    public String getDengji() {
        return this.dengji;
    }

    public void setDengji(String dengji) {
        this.dengji = dengji;
    }

    public String getJisuanType() {
        return this.jisuanType;
    }

    public void setJisuanType(String jisuanType) {
        this.jisuanType = jisuanType;
    }

    public byte[] getImgByte() {
        return this.imgByte;
    }

    public void setImgByte(byte[] imgByte) {
        this.imgByte = imgByte;
    }
}
