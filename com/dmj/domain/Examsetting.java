package com.dmj.domain;

/* loaded from: Examsetting.class */
public class Examsetting {
    private Integer id;
    private Integer examNum;
    private float levera;
    private float leverb;
    private float leverc;
    private float leverd;
    private float levere;
    private float leverf;
    private float leverg;
    private float excellence;
    private String missingExam;
    private String discipline;
    private String transients;
    private String re_read;
    private String overtop;
    private String below;
    private String insertUser;
    private String insertDate;
    private String ext1;
    private String ext2;
    private String ext3;
    private float highScore;
    private float lowScore;
    private float wellrate;
    private String lingfe;
    private String zonfen;
    private String totalpeople;
    private String countscore;
    private String maxscore;
    private String minscore;
    private String avgscore;
    private String stddval;
    private String bianxsval;
    private String hegel;
    private String difficultyval;
    private String studentNum;
    private String studentName;
    private String heightScorel;
    private String qufendu;
    private String excellences;
    private String classNum;
    private String subjectNum;
    private float RSRw_highScore = 0.1f;
    private float RSRw_excellence = 0.15f;
    private float RSRw_lowScore = 0.2f;
    private float RSRw_wellrate = 0.35f;
    private float RSRw_average = 0.2f;

    public float getWellrate() {
        return this.wellrate;
    }

    public void setWellrate(float wellrate) {
        this.wellrate = wellrate;
    }

    public float getLowScore() {
        return this.lowScore;
    }

    public void setLowScore(float lowScore) {
        this.lowScore = lowScore;
    }

    public Examsetting() {
    }

    public Examsetting(Integer id, Integer examNum, float levera, float leverb, float leverc, float leverd, float levere, float leverf, float leverg, float excellence, String missingExam, String discipline, String transients, String reRead, String overtop, String below, float lowScore) {
        this.id = id;
        this.examNum = examNum;
        this.levera = levera;
        this.leverb = leverb;
        this.leverc = leverc;
        this.leverd = leverd;
        this.levere = levere;
        this.leverf = leverf;
        this.leverg = leverg;
        this.excellence = excellence;
        this.missingExam = missingExam;
        this.discipline = discipline;
        this.transients = transients;
        this.re_read = reRead;
        this.overtop = overtop;
        this.below = below;
        this.lowScore = lowScore;
    }

    public Examsetting(Integer id, Integer examNum, float levera, float leverb, float leverc, float leverd, float levere, float leverf, float leverg, float excellence, String missingExam, String discipline, String transients, String reRead, String overtop, String below, String insertUser, String insertDate, String ext1, String ext2, String ext3, float lowScore) {
        this.id = id;
        this.examNum = examNum;
        this.levera = levera;
        this.leverb = leverb;
        this.leverc = leverc;
        this.leverd = leverd;
        this.levere = levere;
        this.leverf = leverf;
        this.leverg = leverg;
        this.excellence = excellence;
        this.missingExam = missingExam;
        this.discipline = discipline;
        this.transients = transients;
        this.re_read = reRead;
        this.overtop = overtop;
        this.below = below;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.lowScore = lowScore;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExamNum() {
        return this.examNum;
    }

    public void setExamNum(Integer examNum) {
        this.examNum = examNum;
    }

    public float getLevera() {
        return this.levera;
    }

    public void setLevera(float levera) {
        this.levera = levera;
    }

    public float getLeverb() {
        return this.leverb;
    }

    public void setLeverb(float leverb) {
        this.leverb = leverb;
    }

    public float getLeverc() {
        return this.leverc;
    }

    public void setLeverc(float leverc) {
        this.leverc = leverc;
    }

    public float getLeverd() {
        return this.leverd;
    }

    public void setLeverd(float leverd) {
        this.leverd = leverd;
    }

    public float getLevere() {
        return this.levere;
    }

    public void setLevere(float levere) {
        this.levere = levere;
    }

    public float getLeverf() {
        return this.leverf;
    }

    public void setLeverf(float leverf) {
        this.leverf = leverf;
    }

    public float getLeverg() {
        return this.leverg;
    }

    public void setLeverg(float leverg) {
        this.leverg = leverg;
    }

    public float getExcellence() {
        return this.excellence;
    }

    public void setExcellence(float excellence) {
        this.excellence = excellence;
    }

    public String getMissingExam() {
        return this.missingExam;
    }

    public void setMissingExam(String missingExam) {
        this.missingExam = missingExam;
    }

    public String getDiscipline() {
        return this.discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getTransients() {
        return this.transients;
    }

    public void setTransients(String transients) {
        this.transients = transients;
    }

    public String getRe_read() {
        return this.re_read;
    }

    public void setRe_read(String reRead) {
        this.re_read = reRead;
    }

    public String getOvertop() {
        return this.overtop;
    }

    public void setOvertop(String overtop) {
        this.overtop = overtop;
    }

    public String getBelow() {
        return this.below;
    }

    public void setBelow(String below) {
        this.below = below;
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

    public float getHighScore() {
        return this.highScore;
    }

    public void setHighScore(float highScore) {
        this.highScore = highScore;
    }

    public String getTotalpeople() {
        return this.totalpeople;
    }

    public void setTotalpeople(String totalpeople) {
        this.totalpeople = totalpeople;
    }

    public String getCountscore() {
        return this.countscore;
    }

    public void setCountscore(String countscore) {
        this.countscore = countscore;
    }

    public String getMaxscore() {
        return this.maxscore;
    }

    public void setMaxscore(String maxscore) {
        this.maxscore = maxscore;
    }

    public String getMinscore() {
        return this.minscore;
    }

    public void setMinscore(String minscore) {
        this.minscore = minscore;
    }

    public String getAvgscore() {
        return this.avgscore;
    }

    public void setAvgscore(String avgscore) {
        this.avgscore = avgscore;
    }

    public String getStddval() {
        return this.stddval;
    }

    public void setStddval(String stddval) {
        this.stddval = stddval;
    }

    public String getBianxsval() {
        return this.bianxsval;
    }

    public void setBianxsval(String bianxsval) {
        this.bianxsval = bianxsval;
    }

    public String getDifficultyval() {
        return this.difficultyval;
    }

    public void setDifficultyval(String difficultyval) {
        this.difficultyval = difficultyval;
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

    public String getHeightScorel() {
        return this.heightScorel;
    }

    public void setHeightScorel(String heightScorel) {
        this.heightScorel = heightScorel;
    }

    public String getHegel() {
        return this.hegel;
    }

    public void setHegel(String hegel) {
        this.hegel = hegel;
    }

    public String getExcellences() {
        return this.excellences;
    }

    public void setExcellences(String excellences) {
        this.excellences = excellences;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getQufendu() {
        return this.qufendu;
    }

    public void setQufendu(String qufendu) {
        this.qufendu = qufendu;
    }

    public String getLingfe() {
        return this.lingfe;
    }

    public void setLingfe(String lingfe) {
        this.lingfe = lingfe;
    }

    public String getZonfen() {
        return this.zonfen;
    }

    public void setZonfen(String zonfen) {
        this.zonfen = zonfen;
    }

    public float getRSRw_highScore() {
        return this.RSRw_highScore;
    }

    public void setRSRw_highScore(float RSRw_highScore) {
        this.RSRw_highScore = RSRw_highScore;
    }

    public float getRSRw_excellence() {
        return this.RSRw_excellence;
    }

    public void setRSRw_excellence(float RSRw_excellence) {
        this.RSRw_excellence = RSRw_excellence;
    }

    public float getRSRw_lowScore() {
        return this.RSRw_lowScore;
    }

    public void setRSRw_lowScore(float RSRw_lowScore) {
        this.RSRw_lowScore = RSRw_lowScore;
    }

    public float getRSRw_wellrate() {
        return this.RSRw_wellrate;
    }

    public void setRSRw_wellrate(float RSRw_wellrate) {
        this.RSRw_wellrate = RSRw_wellrate;
    }

    public float getRSRw_average() {
        return this.RSRw_average;
    }

    public void setRSRw_average(float RSRw_average) {
        this.RSRw_average = RSRw_average;
    }
}
