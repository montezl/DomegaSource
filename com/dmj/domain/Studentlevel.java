package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Studentlevel.class */
public class Studentlevel implements Serializable {
    private String id;
    private Integer examNum;
    private Integer examPaperNum;
    private Integer schoolNum;
    private Integer gradeNum;
    private String classNum;
    private String examinationRoomNum;
    private String source;
    private String isJoin;
    private Double totalScore;
    private String dengji;
    private String dengjixiao;
    private String studentId;
    private String isSub;
    private String insertUser;
    private String insertDate;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String ext5;
    private String ext6;
    private String statisticType;
    private String studentType;
    private double sqts;
    private double oqts;
    private int classRanking;
    private int gradeRanking;
    private String rank;
    private double standardScore;
    private double nss;
    private double jinbudu;
    private String xuankezuhe;
    private String num;
    private String studentReportShowItem;
    private int classRanking_st;
    private int gradeRanking_st;
    private int gradeRanking_stq;
    private int areaRanking_stq;
    private String schoolName;
    private String studentName;
    private String subjectName;
    private Integer subjectNum;
    private String studentNum;
    private String examinationRoomName;
    private String eRoomNum;
    private String gradeName;
    private String className;
    private String examineeNum;
    private int page;
    private int mainType;
    private String cNum;
    private String questionNum;
    private String qtype;
    private String answer;
    private double questionScore;
    private String stdAanswer;
    private double fullScore;
    private String jie;
    private String type;
    private String possessPage;
    private String totalPage;
    private String realQuestionNum;
    private String realStudentId;
    private String levelClassNum;
    private String levelClassName;
    private int areaRanking;
    private double classAverage;
    private String classAverageDengji;
    private double classAverageBaifendengji;
    private double classAverageBaifendengji_q;
    private double classMax;
    private String classMaxDengji;
    private double classMaxBaifendengji;
    private double classMaxBaifendengji_q;
    private double gradeAverage;
    private String gradeAverageDengji;
    private double gradeAverageBaifendengji;
    private double gradeMax;
    private String gradeMaxDengji;
    private double gradeMaxBaifendengji;
    private double ssdt;
    private double schoolRank;
    private String examDate;
    private double standardScore_q;
    private double areaMax;
    private String areaMaxDengji;
    private double areaMaxBaifendengji;
    private double areaAvg;
    private String areaAvgDengji;
    private double areaAvgBaifendengji;
    private double areaRank;
    private int c_gradeRanking;
    private String regScore;
    private String jisuanType;

    public double getJinbudu() {
        return this.jinbudu;
    }

    public void setJinbudu(double jinbudu) {
        this.jinbudu = jinbudu;
    }

    public int getC_gradeRanking() {
        return this.c_gradeRanking;
    }

    public void setC_gradeRanking(int c_gradeRanking) {
        this.c_gradeRanking = c_gradeRanking;
    }

    public String getExamDate() {
        return this.examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getId() {
        return this.id;
    }

    public Studentlevel() {
    }

    public Studentlevel(String id, Integer examNum, Integer examPaperNum, Integer schoolNum, Integer gradeNum, String classNum, String examinationRoomNum, Double totalScore, String studentId, String isSub, String insertUser, String insertDate, String isDelete, String ext1, String ext2, String ext3, String ext4, String ext5, String ext6, String statisticType, String studentType, double sqts, double oqts, int classRanking, int gradeRanking, String rank, double standardScore, double nss, String schoolName, String studentName, String subjectName, Integer subjectNum, String studentNum, String examinationRoomName, String eRoomNum, String gradeName, String className, String examineeNum, int page, String cNum, String questionNum, String qtype, String answer, double questionScore, String stdAanswer, double fullScore, String jie, String type, String possessPage, String totalPage, String realQuestionNum, String realStudentId, int areaRanking, String levelClassNum, String levelClassName, String regScore, int mainType) {
        this.id = id;
        this.examNum = examNum;
        this.examPaperNum = examPaperNum;
        this.schoolNum = schoolNum;
        this.gradeNum = gradeNum;
        this.classNum = classNum;
        this.examinationRoomNum = examinationRoomNum;
        this.totalScore = totalScore;
        this.studentId = studentId;
        this.isSub = isSub;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.ext4 = ext4;
        this.ext5 = ext5;
        this.ext6 = ext6;
        this.statisticType = statisticType;
        this.studentType = studentType;
        this.sqts = sqts;
        this.oqts = oqts;
        this.classRanking = classRanking;
        this.gradeRanking = gradeRanking;
        this.rank = rank;
        this.standardScore = standardScore;
        this.nss = nss;
        this.schoolName = schoolName;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.subjectNum = subjectNum;
        this.studentNum = studentNum;
        this.examinationRoomName = examinationRoomName;
        this.eRoomNum = eRoomNum;
        this.gradeName = gradeName;
        this.className = className;
        this.examineeNum = examineeNum;
        this.page = page;
        this.cNum = cNum;
        this.questionNum = questionNum;
        this.qtype = qtype;
        this.answer = answer;
        this.questionScore = questionScore;
        this.stdAanswer = stdAanswer;
        this.fullScore = fullScore;
        this.jie = jie;
        this.type = type;
        this.possessPage = possessPage;
        this.totalPage = totalPage;
        this.realQuestionNum = realQuestionNum;
        this.realStudentId = realStudentId;
        this.areaRanking = areaRanking;
        this.levelClassNum = levelClassNum;
        this.levelClassName = levelClassName;
        this.regScore = regScore;
        this.mainType = mainType;
    }

    public int getMainType() {
        return this.mainType;
    }

    public void setMainType(int mainType) {
        this.mainType = mainType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getExamNum() {
        return this.examNum;
    }

    public void setExamNum(Integer examNum) {
        this.examNum = examNum;
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

    public String getExaminationRoomNum() {
        return this.examinationRoomNum;
    }

    public void setExaminationRoomNum(String examinationRoomNum) {
        this.examinationRoomNum = examinationRoomNum;
    }

    public Double getTotalScore() {
        return this.totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public String getIsSub() {
        return this.isSub;
    }

    public void setIsSub(String isSub) {
        this.isSub = isSub;
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

    public String getStatisticType() {
        return this.statisticType;
    }

    public void setStatisticType(String statisticType) {
        this.statisticType = statisticType;
    }

    public String getStudentType() {
        return this.studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public double getSqts() {
        return this.sqts;
    }

    public void setSqts(double sqts) {
        this.sqts = sqts;
    }

    public double getOqts() {
        return this.oqts;
    }

    public void setOqts(double oqts) {
        this.oqts = oqts;
    }

    public int getClassRanking() {
        return this.classRanking;
    }

    public void setClassRanking(int classRanking) {
        this.classRanking = classRanking;
    }

    public int getGradeRanking() {
        return this.gradeRanking;
    }

    public void setGradeRanking(int gradeRanking) {
        this.gradeRanking = gradeRanking;
    }

    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public double getStandardScore() {
        return this.standardScore;
    }

    public void setStandardScore(double standardScore) {
        this.standardScore = standardScore;
    }

    public double getNss() {
        return this.nss;
    }

    public void setNss(double nss) {
        this.nss = nss;
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

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getStudentNum() {
        return this.studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
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

    public String getcNum() {
        return this.cNum;
    }

    public void setcNum(String cNum) {
        this.cNum = cNum;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getQtype() {
        return this.qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getQuestionScore() {
        return this.questionScore;
    }

    public void setQuestionScore(double questionScore) {
        this.questionScore = questionScore;
    }

    public String getStdAanswer() {
        return this.stdAanswer;
    }

    public void setStdAanswer(String stdAanswer) {
        this.stdAanswer = stdAanswer;
    }

    public double getFullScore() {
        return this.fullScore;
    }

    public void setFullScore(double fullScore) {
        this.fullScore = fullScore;
    }

    public String getJie() {
        return this.jie;
    }

    public void setJie(String jie) {
        this.jie = jie;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPossessPage() {
        return this.possessPage;
    }

    public void setPossessPage(String possessPage) {
        this.possessPage = possessPage;
    }

    public String getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(Integer subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getRealQuestionNum() {
        return this.realQuestionNum;
    }

    public void setRealQuestionNum(String realQuestionNum) {
        this.realQuestionNum = realQuestionNum;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRealStudentId() {
        return this.realStudentId;
    }

    public void setRealStudentId(String realStudentId) {
        this.realStudentId = realStudentId;
    }

    public int getAreaRanking() {
        return this.areaRanking;
    }

    public void setAreaRanking(int areaRanking) {
        this.areaRanking = areaRanking;
    }

    public double getClassAverage() {
        return this.classAverage;
    }

    public void setClassAverage(double classAverage) {
        this.classAverage = classAverage;
    }

    public double getClassMax() {
        return this.classMax;
    }

    public void setClassMax(double classMax) {
        this.classMax = classMax;
    }

    public double getGradeAverage() {
        return this.gradeAverage;
    }

    public void setGradeAverage(double gradeAverage) {
        this.gradeAverage = gradeAverage;
    }

    public double getGradeMax() {
        return this.gradeMax;
    }

    public void setGradeMax(double gradeMax) {
        this.gradeMax = gradeMax;
    }

    public double getSsdt() {
        return this.ssdt;
    }

    public void setSsdt(double ssdt) {
        this.ssdt = ssdt;
    }

    public double getSchoolRank() {
        return this.schoolRank;
    }

    public void setSchoolRank(double schoolRank) {
        this.schoolRank = schoolRank;
    }

    public double getAreaMax() {
        return this.areaMax;
    }

    public void setAreaMax(double areaMax) {
        this.areaMax = areaMax;
    }

    public double getAreaAvg() {
        return this.areaAvg;
    }

    public void setAreaAvg(double areaAvg) {
        this.areaAvg = areaAvg;
    }

    public double getAreaRank() {
        return this.areaRank;
    }

    public void setAreaRank(double areaRank) {
        this.areaRank = areaRank;
    }

    public double getStandardScore_q() {
        return this.standardScore_q;
    }

    public void setStandardScore_q(double standardScore_q) {
        this.standardScore_q = standardScore_q;
    }

    public String getLevelClassNum() {
        return this.levelClassNum;
    }

    public void setLevelClassNum(String levelClassNum) {
        this.levelClassNum = levelClassNum;
    }

    public String getLevelClassName() {
        return this.levelClassName;
    }

    public void setLevelClassName(String levelClassName) {
        this.levelClassName = levelClassName;
    }

    public String getRegScore() {
        return this.regScore;
    }

    public void setRegScore(String regScore) {
        this.regScore = regScore;
    }

    public int getAreaRanking_stq() {
        return this.areaRanking_stq;
    }

    public void setAreaRanking_stq(int areaRanking_stq) {
        this.areaRanking_stq = areaRanking_stq;
    }

    public String getXuankezuhe() {
        return this.xuankezuhe;
    }

    public void setXuankezuhe(String xuankezuhe) {
        this.xuankezuhe = xuankezuhe;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getDengji() {
        return this.dengji;
    }

    public void setDengji(String dengji) {
        this.dengji = dengji;
    }

    public String getClassAverageDengji() {
        return this.classAverageDengji;
    }

    public void setClassAverageDengji(String classAverageDengji) {
        this.classAverageDengji = classAverageDengji;
    }

    public String getClassMaxDengji() {
        return this.classMaxDengji;
    }

    public void setClassMaxDengji(String classMaxDengji) {
        this.classMaxDengji = classMaxDengji;
    }

    public String getGradeAverageDengji() {
        return this.gradeAverageDengji;
    }

    public void setGradeAverageDengji(String gradeAverageDengji) {
        this.gradeAverageDengji = gradeAverageDengji;
    }

    public String getGradeMaxDengji() {
        return this.gradeMaxDengji;
    }

    public void setGradeMaxDengji(String gradeMaxDengji) {
        this.gradeMaxDengji = gradeMaxDengji;
    }

    public String getAreaMaxDengji() {
        return this.areaMaxDengji;
    }

    public void setAreaMaxDengji(String areaMaxDengji) {
        this.areaMaxDengji = areaMaxDengji;
    }

    public String getAreaAvgDengji() {
        return this.areaAvgDengji;
    }

    public void setAreaAvgDengji(String areaAvgDengji) {
        this.areaAvgDengji = areaAvgDengji;
    }

    public int getClassRanking_st() {
        return this.classRanking_st;
    }

    public void setClassRanking_st(int classRanking_st) {
        this.classRanking_st = classRanking_st;
    }

    public int getGradeRanking_st() {
        return this.gradeRanking_st;
    }

    public void setGradeRanking_st(int gradeRanking_st) {
        this.gradeRanking_st = gradeRanking_st;
    }

    public int getGradeRanking_stq() {
        return this.gradeRanking_stq;
    }

    public void setGradeRanking_stq(int gradeRanking_stq) {
        this.gradeRanking_stq = gradeRanking_stq;
    }

    public String getStudentReportShowItem() {
        return this.studentReportShowItem;
    }

    public void setStudentReportShowItem(String studentReportShowItem) {
        this.studentReportShowItem = studentReportShowItem;
    }

    public double getClassAverageBaifendengji() {
        return this.classAverageBaifendengji;
    }

    public void setClassAverageBaifendengji(double classAverageBaifendengji) {
        this.classAverageBaifendengji = classAverageBaifendengji;
    }

    public double getClassAverageBaifendengji_q() {
        return this.classAverageBaifendengji_q;
    }

    public void setClassAverageBaifendengji_q(double classAverageBaifendengji_q) {
        this.classAverageBaifendengji_q = classAverageBaifendengji_q;
    }

    public double getClassMaxBaifendengji() {
        return this.classMaxBaifendengji;
    }

    public void setClassMaxBaifendengji(double classMaxBaifendengji) {
        this.classMaxBaifendengji = classMaxBaifendengji;
    }

    public double getClassMaxBaifendengji_q() {
        return this.classMaxBaifendengji_q;
    }

    public void setClassMaxBaifendengji_q(double classMaxBaifendengji_q) {
        this.classMaxBaifendengji_q = classMaxBaifendengji_q;
    }

    public double getGradeAverageBaifendengji() {
        return this.gradeAverageBaifendengji;
    }

    public void setGradeAverageBaifendengji(double gradeAverageBaifendengji) {
        this.gradeAverageBaifendengji = gradeAverageBaifendengji;
    }

    public double getGradeMaxBaifendengji() {
        return this.gradeMaxBaifendengji;
    }

    public void setGradeMaxBaifendengji(double gradeMaxBaifendengji) {
        this.gradeMaxBaifendengji = gradeMaxBaifendengji;
    }

    public double getAreaMaxBaifendengji() {
        return this.areaMaxBaifendengji;
    }

    public void setAreaMaxBaifendengji(double areaMaxBaifendengji) {
        this.areaMaxBaifendengji = areaMaxBaifendengji;
    }

    public double getAreaAvgBaifendengji() {
        return this.areaAvgBaifendengji;
    }

    public void setAreaAvgBaifendengji(double areaAvgBaifendengji) {
        this.areaAvgBaifendengji = areaAvgBaifendengji;
    }

    public String getDengjixiao() {
        return this.dengjixiao;
    }

    public void setDengjixiao(String dengjixiao) {
        this.dengjixiao = dengjixiao;
    }

    public String getJisuanType() {
        return this.jisuanType;
    }

    public void setJisuanType(String jisuanType) {
        this.jisuanType = jisuanType;
    }

    public String getIsJoin() {
        return this.isJoin;
    }

    public void setIsJoin(String isJoin) {
        this.isJoin = isJoin;
    }
}
