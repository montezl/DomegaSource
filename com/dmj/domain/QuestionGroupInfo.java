package com.dmj.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* loaded from: QuestionGroupInfo.class */
public class QuestionGroupInfo {
    private String exampaperNum;
    private String groupNum;
    private String groupName;
    private String groupType;
    private int total;
    private int allTotal;
    private int total1;
    private String choosename;
    private int scanTotal;
    private int completedTotal;
    private double avgScore;
    private int avgTotal;
    private int threeToal;
    private int threeAllTotal;
    private int threewarn;
    private int teacherCount;
    private int studentCount;
    private int questionCount;
    private int step;
    private int judgetype;
    private String scanStatus;
    private boolean isSchoolGroup;
    private String enforce;
    private String ext1;
    private String upperFloat;
    private String schoolGroupNum;
    private String correctForbid;
    private Map<String, Object> userSchoolGroupMap;
    private Map<String, Object> questionAvgScoreMap;
    private Map<String, Map<String, Object>> schoolGroupQuestionAvgInfoMap;
    private Map<String, Map<String, Object>> userQuestionGroupInfoMap;
    private Map<String, Integer> yingPanWorkMap;

    public String getExampaperNum() {
        return this.exampaperNum;
    }

    public void setExampaperNum(String exampaperNum) {
        this.exampaperNum = exampaperNum;
    }

    public String getGroupNum() {
        return this.groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public int getTotal1() {
        return this.total1;
    }

    public void setTotal1(int total1) {
        this.total1 = total1;
    }

    public int getAllTotal() {
        return this.allTotal;
    }

    public int getThreeAllTotal() {
        return this.threeAllTotal;
    }

    public void setThreeAllTotal(int threeAllTotal) {
        this.threeAllTotal = threeAllTotal;
    }

    public void setAllTotal(int allTotal) {
        this.allTotal = allTotal;
    }

    public String getSchoolGroupNum() {
        return this.schoolGroupNum;
    }

    public void setSchoolGroupNum(String schoolGroupNum) {
        this.schoolGroupNum = schoolGroupNum;
    }

    public String getChoosename() {
        return this.choosename;
    }

    public void setChoosename(String choosename) {
        this.choosename = choosename;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return this.groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCompletedTotal() {
        return this.completedTotal;
    }

    public void setCompletedTotal(int completedTotal) {
        this.completedTotal = completedTotal;
    }

    public double getAvgScore() {
        return this.avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    public Map<String, Map<String, Object>> getSchoolGroupQuestionAvgInfoMap() {
        return this.schoolGroupQuestionAvgInfoMap;
    }

    public void setSchoolGroupQuestionAvgInfoMap(Map<String, Map<String, Object>> schoolGroupQuestionAvgInfoMap) {
        this.schoolGroupQuestionAvgInfoMap = schoolGroupQuestionAvgInfoMap;
    }

    public Map<String, Map<String, Object>> getUserQuestionGroupInfoMap() {
        return this.userQuestionGroupInfoMap;
    }

    public void setUserQuestionGroupInfoMap(Map<String, Map<String, Object>> userQuestionGroupInfoMap) {
        this.userQuestionGroupInfoMap = userQuestionGroupInfoMap;
    }

    public Map<String, Integer> getYingPanWorkMap() {
        return this.yingPanWorkMap;
    }

    public void setYingPanWorkMap(Map<String, Integer> yingPanWorkMap) {
        this.yingPanWorkMap = yingPanWorkMap;
    }

    public int getAvgTotal() {
        return this.avgTotal;
    }

    public void setAvgTotal(int avgTotal) {
        this.avgTotal = avgTotal;
    }

    public int getThreeToal() {
        return this.threeToal;
    }

    public void setThreeToal(int threeToal) {
        this.threeToal = threeToal;
    }

    public int getThreewarn() {
        return this.threewarn;
    }

    public void setThreewarn(int threewarn) {
        this.threewarn = threewarn;
    }

    public Map<String, Object> getUserSchoolGroupMap() {
        return this.userSchoolGroupMap;
    }

    public void setUserSchoolGroupMap(Map<String, Object> userSchoolGroupMap) {
        this.userSchoolGroupMap = userSchoolGroupMap;
    }

    public int getTeacherCount() {
        return this.teacherCount;
    }

    public void setTeacherCount(int techerCount) {
        this.teacherCount = techerCount;
    }

    public int getStudentCount() {
        return this.studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public int getStep() {
        return this.step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getJudgetype() {
        return this.judgetype;
    }

    public void setJudgetype(int judgetype) {
        this.judgetype = judgetype;
    }

    public int getScanTotal() {
        return this.scanTotal;
    }

    public void setScanTotal(int scanTotal) {
        this.scanTotal = scanTotal;
    }

    public int getQuestionCount() {
        return this.questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public Map<String, Object> getQuestionAvgScoreMap() {
        return this.questionAvgScoreMap;
    }

    public void setQuestionAvgScoreMap(Map<String, Object> questionAvgScoreMap) {
        this.questionAvgScoreMap = questionAvgScoreMap;
    }

    public String getScanStatus() {
        return this.scanStatus;
    }

    public void setScanStatus(String scanStatus) {
        this.scanStatus = scanStatus;
    }

    public boolean isSchoolGroup() {
        return this.isSchoolGroup;
    }

    public void setSchoolGroup(boolean isSchoolGroup) {
        this.isSchoolGroup = isSchoolGroup;
    }

    public String getEnforce() {
        return this.enforce;
    }

    public void setEnforce(String enforce) {
        this.enforce = enforce;
    }

    public String getExt1() {
        return this.ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getUpperFloat() {
        return this.upperFloat;
    }

    public void setUpperFloat(String upperFloat) {
        this.upperFloat = upperFloat;
    }

    public String getCorrectForbid() {
        return this.correctForbid;
    }

    public void setCorrectForbid(String correctForbid) {
        this.correctForbid = correctForbid;
    }

    public CurUserQuestionGroupInfo getCurUserQuestionGroupInfo(String insertUser) {
        Map<String, Object> m2;
        CurUserQuestionGroupInfo info = new CurUserQuestionGroupInfo();
        info.setExampaperNum(this.exampaperNum);
        info.setGroupNum(this.groupNum);
        info.setGroupName(this.groupName);
        info.setGroupType(this.groupType);
        info.setTotal(this.total);
        info.setCompletedTotal(this.completedTotal);
        info.setAvgScore(this.avgScore);
        info.setAvgTotal(this.avgTotal);
        info.setThreeToal(this.threeToal);
        info.setStep(this.step);
        info.setJudgetype(this.judgetype);
        info.setTeacherCount(this.teacherCount);
        info.setStudentCount(this.studentCount);
        info.setQuestionCount(this.questionCount);
        info.setCurUser(insertUser);
        info.setQuestionAvgScoreMap(this.questionAvgScoreMap);
        info.setScanStatus(this.scanStatus);
        info.setAllTotal(this.allTotal);
        info.setChoosename(this.choosename);
        info.setCorrectForbid(this.correctForbid);
        if (this.userSchoolGroupMap != null && this.userSchoolGroupMap.containsKey(String.valueOf(insertUser))) {
            Object schoolGroupObj = this.userSchoolGroupMap.get(String.valueOf(insertUser));
            Map<String, Object> m = new HashMap<>();
            if (schoolGroupObj != null) {
                if (this.questionCount == 1 && this.schoolGroupQuestionAvgInfoMap.containsKey(this.groupNum)) {
                    Map<String, Object> m22 = this.schoolGroupQuestionAvgInfoMap.get(this.groupNum);
                    if (m22 != null && m22.containsKey(schoolGroupObj.toString())) {
                        m.put(this.groupNum, m22.get(schoolGroupObj.toString()));
                    }
                } else if (this.questionCount > 1 && this.questionAvgScoreMap != null && this.questionAvgScoreMap.size() > 1) {
                    Set<Map.Entry<String, Object>> entrySet = this.questionAvgScoreMap.entrySet();
                    for (Map.Entry<String, Object> entry : entrySet) {
                        String key = entry.getKey();
                        if (this.schoolGroupQuestionAvgInfoMap != null && this.schoolGroupQuestionAvgInfoMap.containsKey(key) && (m2 = this.schoolGroupQuestionAvgInfoMap.get(key)) != null && m2.containsKey(schoolGroupObj.toString())) {
                            Object avg = m2.get(schoolGroupObj.toString());
                            m.put(key, avg);
                        }
                    }
                }
                info.setSchoolGroupAvgMap(m);
            }
        }
        Map<String, Map<String, Object>> usermap = getUserQuestionGroupInfoMap();
        if (usermap != null && usermap.containsKey(String.valueOf(insertUser))) {
            Map<String, Object> m3 = usermap.get(String.valueOf(insertUser));
            info.setCurUserCompletedTotal(Integer.valueOf(m3.get("yipanTotal").toString()).intValue());
            info.setCurUserCompletedChooseTotal(Integer.valueOf(m3.get("yipanChooseTotal").toString()).intValue());
            info.setCurUserAvgScore(Double.valueOf(m3.get("questionScoreavg").toString()).doubleValue());
            info.setCurUserThreeTotal((int) Math.ceil(info.getThreeToal() / info.getTeacherCount()));
            info.setCurUserAvgTotal(Integer.valueOf(m3.get("yingpanTotal").toString()).intValue() + info.getCurUserThreeTotal());
            info.setGroupInTotal(Integer.valueOf(m3.get("groupInTotal").toString()).intValue());
            info.setGroupInCompltedTotal(Integer.valueOf(m3.get("groupInCompltedTotal").toString()).intValue());
            info.setQuestionInfo(m3.get("questionInfo").toString());
            info.setAssisterType(m3.get("assisterType").toString());
        }
        return info;
    }
}
