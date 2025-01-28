package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Define.class */
public class Define implements Serializable {
    private String id;
    private Integer orderNum;
    private int examPaperNum;
    private String questionNum;
    private Float fullScore;
    private String questionType;
    private String Dqtype;
    private String answer;
    private String answer_b;
    private Double difficult;
    private Double inspectionlevel;
    private Double one26;
    private Double one25;
    private Double one24;
    private Double one23;
    private Double one22;
    private Double one21;
    private Double one20;
    private Double one19;
    private Double one18;
    private Double one17;
    private Double one16;
    private Double one15;
    private Double one14;
    private Double one13;
    private Double one12;
    private Double one11;
    private Double one10;
    private Double one9;
    private Double one8;
    private Double one7;
    private Double one6;
    private Double one5;
    private Double one4;
    private Double one3;
    private Double one2;
    private Double one1;
    private Double one26b;
    private Double one25b;
    private Double one24b;
    private Double one23b;
    private Double one22b;
    private Double one21b;
    private Double one20b;
    private Double one19b;
    private Double one18b;
    private Double one17b;
    private Double one16b;
    private Double one15b;
    private Double one14b;
    private Double one13b;
    private Double one12b;
    private Double one11b;
    private Double one10b;
    private Double one9b;
    private Double one8b;
    private Double one7b;
    private Double one6b;
    private Double one5b;
    private Double one4b;
    private Double one3b;
    private Double one2b;
    private Double one1b;
    private String mn;
    private int count;
    private String insertUser;
    private String answerinsertUser;
    private String answerinsertDate;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private int isParent;
    private String isDelete;
    private String multiple;
    private Float errorRate;
    private String hasErrorSection;
    private String lengout;
    private Integer category;
    private String subjectName;
    private Double step;
    private Double frequency;
    private String start;
    private String end;
    private String full;
    private String qtype;
    private String split;
    private String single;
    private String abli;
    private Integer optionCount;
    private String choosename;
    private String cross_page;
    private String merge;
    private Float deduction;
    private String num;
    private String name;
    private String fullScore1;
    private String know;
    private String knowId;
    private String[] names;
    private String[] values;
    private String ability;
    private String abilityId;
    private String questiontypes;
    private String questiontypeId;
    private String tikuais;
    private String tikuaiId;
    private int page;
    private String jie;
    private String description;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String pid;
    private String average;
    private String numofStudent;

    public String getAnswerinsertUser() {
        return this.answerinsertUser;
    }

    public void setAnswerinsertUser(String answerinsertUser) {
        this.answerinsertUser = answerinsertUser;
    }

    public String getAnswerinsertDate() {
        return this.answerinsertDate;
    }

    public void setAnswerinsertDate(String answerinsertDate) {
        this.answerinsertDate = answerinsertDate;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNumofStudent() {
        return this.numofStudent;
    }

    public void setNumofStudent(String numofStudent) {
        this.numofStudent = numofStudent;
    }

    public String getAverage() {
        return this.average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public Define() {
    }

    public Define(Integer orderNum, Integer examPaperNum, String questionNum, Float fullScore, String questionType, String Dqtype, String insertUser, String insertDate, String updateUser, String updateDate) {
        this.orderNum = orderNum;
        this.examPaperNum = examPaperNum.intValue();
        this.questionNum = questionNum;
        this.fullScore = fullScore;
        this.questionType = questionType;
        this.Dqtype = Dqtype;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public Define(Integer orderNum, Integer examPaperNum, String questionNum, Float fullScore, String questionType, String Dqtype, Integer optionCount, String answer, String answer_b, Double difficult, Double inspectionlevel, Double one4, Double one3, Double one2, Double one1, String insertUser, String insertDate, String updateUser, String updateDate, String description, String isDelete, String ext1, String ext2, String ext3, Double step, Double frequency, String jie) {
        this.orderNum = orderNum;
        this.examPaperNum = examPaperNum.intValue();
        this.questionNum = questionNum;
        this.fullScore = fullScore;
        this.questionType = questionType;
        this.Dqtype = Dqtype;
        this.optionCount = optionCount;
        this.answer = answer;
        this.answer_b = answer_b;
        this.difficult = difficult;
        this.inspectionlevel = inspectionlevel;
        this.one2 = one2;
        this.one3 = one3;
        this.one4 = one4;
        this.one1 = one1;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.step = step;
        this.frequency = frequency;
        this.jie = jie;
    }

    public Define(Integer orderNum, Integer examPaperNum, String questionNum, Float fullScore, String questionType, String Dqtype, Integer optionCount, String answer, String answer_b, Double difficult, Double inspectionlevel, Double one8, Double one7, Double one6, Double one5, Double one4, Double one3, Double one2, Double one1, Double one8b, Double one7b, Double one6b, Double one5b, Double one4b, Double one3b, Double one2b, Double one1b, String insertUser, String insertDate, String updateUser, Float deduction, String lengout, String updateDate, String description, String isDelete, String hasErrorSection, String ext1, String ext2, String ext3, Double step, Double frequency, String jie) {
        this.orderNum = orderNum;
        this.examPaperNum = examPaperNum.intValue();
        this.questionNum = questionNum;
        this.fullScore = fullScore;
        this.questionType = questionType;
        this.Dqtype = Dqtype;
        this.optionCount = optionCount;
        this.answer = answer;
        this.answer_b = answer_b;
        this.difficult = difficult;
        this.inspectionlevel = inspectionlevel;
        this.one8 = one8;
        this.one7 = one7;
        this.one6 = one6;
        this.one5 = one5;
        this.one4 = one4;
        this.one3 = one3;
        this.one2 = one2;
        this.one1 = one1;
        this.one8b = one8b;
        this.one7b = one7b;
        this.one6b = one6b;
        this.one5b = one5b;
        this.one4b = one4b;
        this.one3b = one3b;
        this.one2b = one2b;
        this.one1b = one1b;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.step = step;
        this.frequency = frequency;
        this.jie = jie;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(int examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public Float getFullScore() {
        return this.fullScore;
    }

    public void setFullScore(Float fullScore) {
        this.fullScore = fullScore;
    }

    public String getQuestionType() {
        return this.questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getDqtype() {
        return this.Dqtype;
    }

    public void setDqtype(String dqtype) {
        this.Dqtype = dqtype;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer_b() {
        return this.answer_b;
    }

    public void setAnswer_b(String answer_b) {
        this.answer_b = answer_b;
    }

    public Double getDifficult() {
        return this.difficult;
    }

    public void setDifficult(Double difficult) {
        this.difficult = difficult;
    }

    public Double getInspectionlevel() {
        return this.inspectionlevel;
    }

    public void setInspectionlevel(Double inspectionlevel) {
        this.inspectionlevel = inspectionlevel;
    }

    public Double getOne8() {
        return this.one8;
    }

    public void setOne8(Double one8) {
        this.one8 = one8;
    }

    public Double getOne7() {
        return this.one7;
    }

    public void setOne7(Double one7) {
        this.one7 = one7;
    }

    public Double getOne6() {
        return this.one6;
    }

    public void setOne6(Double one6) {
        this.one6 = one6;
    }

    public Double getOne5() {
        return this.one5;
    }

    public void setOne5(Double one5) {
        this.one5 = one5;
    }

    public Double getOne4() {
        return this.one4;
    }

    public void setOne4(Double one4) {
        this.one4 = one4;
    }

    public Double getOne3() {
        return this.one3;
    }

    public void setOne3(Double one3) {
        this.one3 = one3;
    }

    public Double getOne2() {
        return this.one2;
    }

    public void setOne2(Double one2) {
        this.one2 = one2;
    }

    public Double getOne1() {
        return this.one1;
    }

    public void setOne1(Double one1) {
        this.one1 = one1;
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

    public String getMultiple() {
        return this.multiple;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public Float getErrorRate() {
        return this.errorRate;
    }

    public void setErrorRate(Float errorRate) {
        this.errorRate = errorRate;
    }

    public String getHasErrorSection() {
        return this.hasErrorSection;
    }

    public void setHasErrorSection(String hasErrorSection) {
        this.hasErrorSection = hasErrorSection;
    }

    public String getLengout() {
        return this.lengout;
    }

    public void setLengout(String lengout) {
        this.lengout = lengout;
    }

    public Integer getCategory() {
        return this.category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Double getStep() {
        return this.step;
    }

    public void setStep(Double step) {
        this.step = step;
    }

    public Double getFrequency() {
        return this.frequency;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }

    public String getStart() {
        return this.start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return this.end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getFull() {
        return this.full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getQtype() {
        return this.qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

    public String getSplit() {
        return this.split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getSingle() {
        return this.single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public String getAbli() {
        return this.abli;
    }

    public void setAbli(String abli) {
        this.abli = abli;
    }

    public Integer getOptionCount() {
        return this.optionCount;
    }

    public void setOptionCount(Integer optionCount) {
        this.optionCount = optionCount;
    }

    public Float getDeduction() {
        return this.deduction;
    }

    public void setDeduction(Float deduction) {
        this.deduction = deduction;
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

    public String getKnow() {
        return this.know;
    }

    public void setKnow(String know) {
        this.know = know;
    }

    public String getKnowId() {
        return this.knowId;
    }

    public void setKnowId(String knowId) {
        this.knowId = knowId;
    }

    public String[] getNames() {
        return this.names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public String[] getValues() {
        return this.values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String getAbility() {
        return this.ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getAbilityId() {
        return this.abilityId;
    }

    public void setAbilityId(String abilityId) {
        this.abilityId = abilityId;
    }

    public String getQuestiontypes() {
        return this.questiontypes;
    }

    public void setQuestiontypes(String questiontypes) {
        this.questiontypes = questiontypes;
    }

    public String getQuestiontypeId() {
        return this.questiontypeId;
    }

    public void setQuestiontypeId(String questiontypeId) {
        this.questiontypeId = questiontypeId;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getJie() {
        return this.jie;
    }

    public void setJie(String jie) {
        this.jie = jie;
    }

    public String getMn() {
        return this.mn;
    }

    public void setMn(String mn) {
        this.mn = mn;
    }

    public Integer getOrderNum() {
        return this.orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getChoosename() {
        return this.choosename;
    }

    public void setChoosename(String choosename) {
        this.choosename = choosename;
    }

    public String getCross_page() {
        return this.cross_page;
    }

    public void setCross_page(String cross_page) {
        this.cross_page = cross_page;
    }

    public String getMerge() {
        return this.merge;
    }

    public void setMerge(String merge) {
        this.merge = merge;
    }

    public int getIsParent() {
        return this.isParent;
    }

    public void setIsParent(int isParent) {
        this.isParent = isParent;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Double getOne8b() {
        return this.one8b;
    }

    public void setOne8b(Double one8b) {
        this.one8b = one8b;
    }

    public Double getOne7b() {
        return this.one7b;
    }

    public void setOne7b(Double one7b) {
        this.one7b = one7b;
    }

    public Double getOne6b() {
        return this.one6b;
    }

    public void setOne6b(Double one6b) {
        this.one6b = one6b;
    }

    public Double getOne5b() {
        return this.one5b;
    }

    public void setOne5b(Double one5b) {
        this.one5b = one5b;
    }

    public Double getOne4b() {
        return this.one4b;
    }

    public void setOne4b(Double one4b) {
        this.one4b = one4b;
    }

    public Double getOne3b() {
        return this.one3b;
    }

    public void setOne3b(Double one3b) {
        this.one3b = one3b;
    }

    public Double getOne2b() {
        return this.one2b;
    }

    public void setOne2b(Double one2b) {
        this.one2b = one2b;
    }

    public Double getOne1b() {
        return this.one1b;
    }

    public void setOne1b(Double one1b) {
        this.one1b = one1b;
    }

    public String getTikuaiId() {
        return this.tikuaiId;
    }

    public void setTikuaiId(String tikuaiId) {
        this.tikuaiId = tikuaiId;
    }

    public String getTikuais() {
        return this.tikuais;
    }

    public void setTikuais(String tikuais) {
        this.tikuais = tikuais;
    }

    public Double getOne16() {
        return this.one16;
    }

    public void setOne16(Double one16) {
        this.one16 = one16;
    }

    public Double getOne15() {
        return this.one15;
    }

    public void setOne15(Double one15) {
        this.one15 = one15;
    }

    public Double getOne14() {
        return this.one14;
    }

    public void setOne14(Double one14) {
        this.one14 = one14;
    }

    public Double getOne13() {
        return this.one13;
    }

    public void setOne13(Double one13) {
        this.one13 = one13;
    }

    public Double getOne12() {
        return this.one12;
    }

    public void setOne12(Double one12) {
        this.one12 = one12;
    }

    public Double getOne11() {
        return this.one11;
    }

    public void setOne11(Double one11) {
        this.one11 = one11;
    }

    public Double getOne10() {
        return this.one10;
    }

    public void setOne10(Double one10) {
        this.one10 = one10;
    }

    public Double getOne9() {
        return this.one9;
    }

    public void setOne9(Double one9) {
        this.one9 = one9;
    }

    public Double getOne16b() {
        return this.one16b;
    }

    public void setOne16b(Double one16b) {
        this.one16b = one16b;
    }

    public Double getOne15b() {
        return this.one15b;
    }

    public void setOne15b(Double one15b) {
        this.one15b = one15b;
    }

    public Double getOne14b() {
        return this.one14b;
    }

    public void setOne14b(Double one14b) {
        this.one14b = one14b;
    }

    public Double getOne13b() {
        return this.one13b;
    }

    public void setOne13b(Double one13b) {
        this.one13b = one13b;
    }

    public Double getOne12b() {
        return this.one12b;
    }

    public void setOne12b(Double one12b) {
        this.one12b = one12b;
    }

    public Double getOne11b() {
        return this.one11b;
    }

    public void setOne11b(Double one11b) {
        this.one11b = one11b;
    }

    public Double getOne10b() {
        return this.one10b;
    }

    public void setOne10b(Double one10b) {
        this.one10b = one10b;
    }

    public Double getOne9b() {
        return this.one9b;
    }

    public void setOne9b(Double one9b) {
        this.one9b = one9b;
    }

    public String getFullScore1() {
        return this.fullScore1;
    }

    public void setFullScore1(String fullScore1) {
        this.fullScore1 = fullScore1;
    }

    public Double getOne26() {
        return this.one26;
    }

    public void setOne26(Double one26) {
        this.one26 = one26;
    }

    public Double getOne25() {
        return this.one25;
    }

    public void setOne25(Double one25) {
        this.one25 = one25;
    }

    public Double getOne24() {
        return this.one24;
    }

    public void setOne24(Double one24) {
        this.one24 = one24;
    }

    public Double getOne23() {
        return this.one23;
    }

    public void setOne23(Double one23) {
        this.one23 = one23;
    }

    public Double getOne22() {
        return this.one22;
    }

    public void setOne22(Double one22) {
        this.one22 = one22;
    }

    public Double getOne21() {
        return this.one21;
    }

    public void setOne21(Double one21) {
        this.one21 = one21;
    }

    public Double getOne20() {
        return this.one20;
    }

    public void setOne20(Double one20) {
        this.one20 = one20;
    }

    public Double getOne19() {
        return this.one19;
    }

    public void setOne19(Double one19) {
        this.one19 = one19;
    }

    public Double getOne18() {
        return this.one18;
    }

    public void setOne18(Double one18) {
        this.one18 = one18;
    }

    public Double getOne17() {
        return this.one17;
    }

    public void setOne17(Double one17) {
        this.one17 = one17;
    }

    public Double getOne26b() {
        return this.one26b;
    }

    public void setOne26b(Double one26b) {
        this.one26b = one26b;
    }

    public Double getOne25b() {
        return this.one25b;
    }

    public void setOne25b(Double one25b) {
        this.one25b = one25b;
    }

    public Double getOne24b() {
        return this.one24b;
    }

    public void setOne24b(Double one24b) {
        this.one24b = one24b;
    }

    public Double getOne23b() {
        return this.one23b;
    }

    public void setOne23b(Double one23b) {
        this.one23b = one23b;
    }

    public Double getOne22b() {
        return this.one22b;
    }

    public void setOne22b(Double one22b) {
        this.one22b = one22b;
    }

    public Double getOne21b() {
        return this.one21b;
    }

    public void setOne21b(Double one21b) {
        this.one21b = one21b;
    }

    public Double getOne20b() {
        return this.one20b;
    }

    public void setOne20b(Double one20b) {
        this.one20b = one20b;
    }

    public Double getOne19b() {
        return this.one19b;
    }

    public void setOne19b(Double one19b) {
        this.one19b = one19b;
    }

    public Double getOne18b() {
        return this.one18b;
    }

    public void setOne18b(Double one18b) {
        this.one18b = one18b;
    }

    public Double getOne17b() {
        return this.one17b;
    }

    public void setOne17b(Double one17b) {
        this.one17b = one17b;
    }
}
