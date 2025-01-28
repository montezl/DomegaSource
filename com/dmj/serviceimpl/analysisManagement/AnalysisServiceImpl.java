package com.dmj.serviceimpl.analysisManagement;

import cn.hutool.core.util.StrUtil;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.analysisManagement.AnalysisDAOImpl;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Define;
import com.dmj.domain.Examlog;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Grade;
import com.dmj.service.analysisManagement.AnalysisService;
import com.dmj.service.examManagement.ExamService;
import com.dmj.serviceimpl.examManagement.ExamServiceImpl;
import com.dmj.util.Conffig;
import com.dmj.util.Const;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

/* loaded from: AnalysisServiceImpl.class */
public class AnalysisServiceImpl implements AnalysisService {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    AnalysisDAOImpl dao = new AnalysisDAOImpl();
    private ExamService examService = (ExamService) ServiceFactory.getObject(new ExamServiceImpl());
    Logger log = Logger.getLogger(getClass());
    public String EXAMNUM = "";
    public String LOGIN_USER_NUM = "";

    public boolean countIMITData(String examNum, String loginUser) {
        return true;
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public String countIMIT(int examNum, String loginUser, String[] types) throws Exception {
        List<Exampaper> eps = this.dao.getExampaperInfoByExamNum(Integer.valueOf(examNum));
        if (null == eps) {
            this.log.info(" 计算考试 ： " + examNum + " 试卷信息为空！！！！");
            return "false";
        }
        Set<Integer> set = new HashSet<>();
        try {
            for (Exampaper ep : eps) {
                countSubIMIT_sub(ep.getExamNum(), ep.getSubjectNum(), ep.getGradeNum(), loginUser, types, "1");
                set.add(ep.getGradeNum());
            }
            for (Integer gd : set) {
                countAllSubjectInfo(Integer.valueOf(examNum), 0, gd, loginUser);
            }
            return "true";
        } catch (Exception e) {
            this.log.error(" 分科目计算中间表 error： ", e);
            return e.toString();
        }
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public String countSubIMIT(Integer examNum, Integer[] subjectNum, Integer[] gradeNum, String loginUser, String[] types, List<Examlog> list, String jisuanType) {
        if (null == subjectNum || subjectNum.length <= 0) {
            return "false";
        }
        Set<Integer> set = new HashSet<>();
        try {
            for (Integer i = 0; i.intValue() < subjectNum.length; i = Integer.valueOf(i.intValue() + 1)) {
                countSubIMIT_sub(examNum, subjectNum[i.intValue()], gradeNum[i.intValue()], loginUser, types, jisuanType);
                set.add(gradeNum[i.intValue()]);
            }
            Arrays.asList(types);
            if (list.contains("0")) {
                for (Integer gd : set) {
                    countAllSubjectInfo(examNum, 0, gd, loginUser);
                }
            }
            this.dao2.batchSave(list);
            return "true";
        } catch (Exception e) {
            this.log.error(" 分科目计算中间表 error： ", e);
            return e.toString();
        }
    }

    public boolean countSubIMIT_sub(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser, String[] types, String jisuanType) throws Exception {
        String path = ServletActionContext.getServletContext().getRealPath("");
        Conffig.getParameter(path, Const.zeroScoreInCount);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jisuanType", (Object) jisuanType);
        Object pexamPaperNum = this.dao2._queryObject("select pexamPaperNum from exampaper where examNum={examNum} AND gradeNum={gradeNum} AND subjectNum={subjectNum}", args);
        args.put("pexamPaperNum", pexamPaperNum);
        this.dao2._execute("UPDATE exampaper SET status='8',jisuanType={jisuanType} WHERE pexamPaperNum={pexamPaperNum} ", args);
        if (types == null || types.length == 0) {
            boolean r1 = this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_student_data(?,?,?,?)");
            boolean r2 = this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_subject_data(?,?,?,?)");
            if (r1 && r2 && 1 != 0) {
                return true;
            }
            return false;
        }
        boolean result = true;
        if ("0".equals(jisuanType)) {
            this.log.info("【计算考试】科目选择题得分计算 .............." + loginUser + "----" + examNum + "---" + gradeNum + "---" + subjectNum);
            result = 1 != 0 && this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_student_objdata(?,?,?,?)");
        } else {
            for (String type : types) {
                if (type.equals("1")) {
                    this.log.info("【计算考试】科目得分计算 .............." + loginUser + "----" + examNum + "---" + gradeNum + "---" + subjectNum);
                    boolean z = result && this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_student_data(?,?,?,?)");
                    result = this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_subject_data(?,?,?,?)");
                }
                if (type.equals("2")) {
                    this.log.info("【计算考试】知识点得分计算 .............." + loginUser + "----" + examNum + "---" + gradeNum + "---" + subjectNum);
                    result = countKnowledgeData(examNum, subjectNum, gradeNum, loginUser) && result;
                }
                if (type.equals("3")) {
                    this.log.info("【计算考试】能力点得分计算 .............." + loginUser + "----" + examNum + "---" + gradeNum + "---" + subjectNum);
                    result = countAbilityData(examNum, subjectNum, gradeNum, loginUser) && result;
                }
                if (type.equals("4")) {
                    this.log.info("【计算考试】题型得分计算 .............." + loginUser + "----" + examNum + "---" + gradeNum + "---" + subjectNum);
                    result = countQuestionTypeData(examNum, subjectNum, gradeNum, loginUser) && result;
                }
                if (type.equals("5")) {
                    this.log.info("【计算考试】小题得分计算 .............." + loginUser + "----" + examNum + "---" + gradeNum + "---" + subjectNum);
                    result = countQuestionData(examNum, subjectNum, gradeNum, loginUser) && result;
                }
                if (type.equals("6")) {
                    this.log.info("【计算考试】赋分计算 .............." + loginUser + "----" + examNum + "---" + gradeNum + "---" + subjectNum);
                    result = countFufen(examNum, subjectNum, gradeNum, loginUser) && result;
                }
                if (type.equals("7")) {
                    this.log.info("【计算考试】题块分计算 .............." + loginUser + "----" + examNum + "---" + gradeNum + "---" + subjectNum);
                    result = countTiKuai(examNum, subjectNum, gradeNum, loginUser) && result;
                }
            }
        }
        this.dao2._execute("UPDATE exampaper SET status='9',jisuanType={jisuanType} WHERE pexamPaperNum={pexamPaperNum} ", args);
        return result;
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public String countAllSubjectInfo(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser) {
        try {
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_allsubject_data(?,?,?,?)");
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_countAllRank(?,?,?,?)");
            return "true";
        } catch (Exception e) {
            this.log.info("【计算考试-总分】" + loginUser + "----" + examNum + "---" + gradeNum + " --- 计算出错:" + e);
            return e.toString();
        }
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public boolean countSingleSubjectUploadIMIT(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser) {
        try {
            countSingleSubjectUploadIMIT_sub(examNum, subjectNum, gradeNum, loginUser);
            return true;
        } catch (Exception e) {
            this.log.error(" 分科目计算中间表 error： ", e);
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public boolean countSingleSubjectUploadIMIT_sub(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser) {
        Map args = new HashMap();
        args.put("STATUS_EXAM_SUBJECT_COMPLETEING", "8");
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("STATUS_EXAM_SUBJECT_COMPLETED", "9");
        this.dao2._execute("UPDATE exampaper SET status={STATUS_EXAM_SUBJECT_COMPLETEING} WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum} ", args);
        try {
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_countRank(?,?,?,?)");
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_subject_data(?,?,?,?)");
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_allsubject_data(?,?,?,?)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.dao2._execute("UPDATE exampaper SET status={STATUS_EXAM_SUBJECT_COMPLETED} ' WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum} ", args);
        return true;
    }

    public boolean countSingleSubjectUploadIMIT_sub_detail(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser) {
        Map args = new HashMap();
        args.put("STATUS_EXAM_SUBJECT_COMPLETEING", "8");
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        this.dao2._execute("UPDATE exampaper SET status={STATUS_EXAM_SUBJECT_COMPLETEING} WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum} ", args);
        try {
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_subject_data(?,?,?,?)");
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_countRank(?,?,?,?)");
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_allsubject_data(?,?,?,?)");
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_countAllRank(?,?,?,?)");
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_knowlege_data(?,?,?,?)");
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_abilitypoint_middle_data(?,?,?,?)");
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_classlevel_question_middle_data(?,?,?,?)");
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_questiontype_middle_data(?,?,?,?)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.dao2._execute("UPDATE exampaper SET status='9' WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum} ", args);
        return true;
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public boolean countKnowledgeData(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser) throws Exception {
        try {
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_count_knowlege_data(?,?,?,?)");
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public boolean countAbilityData(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser) throws Exception {
        try {
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_abilitypoint_middle_data(?,?,?,?)");
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public boolean countQuestionTypeData(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser) throws Exception {
        try {
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_questiontype_middle_data(?,?,?,?)");
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public boolean countQuestionData(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser) throws Exception {
        try {
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_classlevel_question_middle_data(?,?,?,?)");
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public boolean countFufen(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser) throws Exception {
        try {
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_fufen(?,?)");
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean countTiKuai(Integer examNum, Integer subjectNum, Integer gradeNum, String loginUser) throws Exception {
        try {
            this.dao.callPro(examNum, subjectNum, gradeNum, loginUser, "sub_tikuai_middle_data(?,?,?,?)");
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public void updateType(String type, String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put("type", type);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        this.dao2._execute("update exampaper set type={type} where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getExam(String startTime, String endTime, String userId) {
        if (null != startTime && !startTime.equals("")) {
            startTime = startTime + " AND e.examDate>{startTime} ";
        }
        if (null != endTime && !endTime.equals("")) {
            startTime = startTime + " AND e.examDate<{endTime} ";
        }
        String sql = ("-2".equals(userId) || "-1".equals(userId)) ? "SELECT distinct e.examNum num,e.examName name FROM exam e left join exampaper ep on ep.examNum=e.examNum where e.isDelete='F'  and ep.status<>'0' ORDER BY e.examDate desc,e.insertDate DESC" : "SELECT distinct e.examNum num,e.examName name FROM exam e left join exampaper ep on ep.examNum=e.examNum LEFT JOIN astrict a on a.examNum=e.examNum where e.isDelete='F'  and ep.status<>'0' and a.partType='5' and a.userType='1' and a.`status`='1' ORDER BY e.examDate desc,e.insertDate DESC";
        Map args = new HashMap();
        args.put("startTime", startTime);
        args.put("endTime", endTime);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHisoryExam(String startTime, String endTime) {
        if (null != startTime && !startTime.equals("")) {
            startTime = startTime + " AND examDate>{startTime} ";
        }
        if (null != endTime && !endTime.equals("")) {
            startTime = startTime + " AND examDate<{endTime} ";
        }
        String sql = "SELECT examNum num,examName name FROM exam WHERE 1=1   and isDelete ='F' ORDER BY examDate desc,insertDate DESC";
        Map args = new HashMap();
        args.put("startTime", startTime);
        args.put("endTime", endTime);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getCompareExam(String exam, String grade, String subject, String schoolNum, String classNum, String studentId, String studentType, String teachUnit_statistic) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        String schoolStr = "";
        if (schoolNum != null && !schoolNum.equals("") && !schoolNum.equals("allschool") && schoolNum.indexOf(Const.STRING_SEPERATOR) == -1 && null != teachUnit_statistic && !teachUnit_statistic.equals("00")) {
            schoolStr = "and schoolNum=" + schoolNum + " ";
        }
        String subjectStr = "";
        if (subject != null && !subject.equals("") && !subject.equals("null")) {
            subjectStr = "and subjectNum=" + subject + " ";
        }
        String sTypeStr = "";
        if (studentType != null && !studentType.equals("") && !studentType.equals("null")) {
            sTypeStr = "and studentType=" + studentType + " ";
        }
        List<Map<String, Object>> list = this.dao2.queryMapList("select distinct eg.examtypeGroupNum,ifnull(eg.maxNumber,10)maxNumber from exam e inner join examtypegroup eg on eg.examtypeNum=e.examType  where e.isDelete='F' ");
        if (list.size() <= 0) {
            return null;
        }
        String examtypeGroupNum = list.get(0).get("examtypeGroupNum") + "";
        if (StrUtil.isNullOrUndefined(examtypeGroupNum)) {
            return null;
        }
        args.put("examtypeGroupNum", examtypeGroupNum);
        String maxNumber = list.get(0).get("maxNumber") + "";
        String jie = this.dao2._queryStr("select e.jie from exampaper e where e.examNum ={exam} and e.gradeNum={grade} limit 0,1", args);
        args.put("jie", jie);
        String jieStr = "";
        if (jie != null && !jie.equals("") && !jie.equals("null")) {
            jieStr = "and jie={jie} ";
        }
        Object stage = this.dao2._queryObject("select stage from basegrade where gradeNum={grade}", args);
        args.put("stage", stage);
        if (!examtypeGroupNum.equals("") && !examtypeGroupNum.equals("null")) {
            String aa = "select e.examNum num,e.examName name from examtypegroup eg left join exam e on  eg.examtypeNum=e.examType left join (select DISTINCT examNum,gradeNum from gradelevel where xuankezuhe='0' " + schoolStr + subjectStr + sTypeStr + jieStr + ") gl on e.examNum = gl.examNum inner join basegrade bg on bg.gradeNum=gl.gradeNum and stage={stage} where gl.examNum is not null and eg.examtypeGroupNum = {examtypeGroupNum} and e.isDelete='F' and e.examNum!={exam} AND e.examDate<=(SELECT examDate FROM exam WHERE examNum={exam} ) order by e.examDate desc limit 0," + maxNumber;
            return this.dao2._queryBeanList(aa, AjaxData.class, args);
        }
        return null;
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryCompareExam(String startTime, String endTime, String exam, String grade) {
        if (null != startTime && !startTime.equals("")) {
            startTime = startTime + " AND ee.examDate>{startTime} ";
        }
        if (null != endTime && !endTime.equals("")) {
            startTime = startTime + " AND ee.examDate<{endTime} ";
        }
        Map args = new HashMap();
        args.put("startTime", startTime);
        args.put("endTime", endTime);
        args.put("exam", exam);
        args.put("grade", grade);
        String sql = "SELECT distinct ee.examNum num,ee.examName name  FROM   (  \tSELECT r.examNum,r.gradeNum,r.jie,e.examDate,bb.stage   \tFROM  \t(SELECT examNum,gradeNum,jie FROM his_exampaper where examNum={exam}  AND gradeNum={grade} LIMIT 0,1)r  \tLEFT JOIN his_exam e ON e.examNum={exam}  and isDelete='F' \tleft join basegrade bb on bb.gradeNum = {grade} )r  \tLEFT JOIN  \t( \t\tselect s.*,bc.stage from his_exampaper s \t\tleft join basegrade bc on bc.gradeNum=s.gradeNum \t)e  ON e.jie=r.jie  AND e.examNum!={exam} and e.stage=r.stage  LEFT JOIN his_exam ee ON ee.examNum=e.examNum AND ee.examDate<r.examDate   and e.isDelete='F'  WHERE ee.examNum IS NOT NULL  ORDER BY ee.insertDate DESC LIMIT 0,5  ";
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSubject(String exam, String school, String grade, String type, String showZSubject, String fufen, String subCompose) {
        String subComposeStr;
        String sql = "SELECT  DISTINCT g.subjectNum num,g.subjectName name  FROM (SELECT ep.examPaperNum,ep.gradeNum,ep.jie,ep.subjectNum FROM (SELECT examPaperNum,subjectNum,gradeNum,jie FROM exampaper WHERE examNum={exam} and gradeNum={grade} ) ep ";
        String str = sql + ") r  LEFT JOIN classexam c ON c.examPaperNum=r.examPaperNum  LEFT JOIN (select id,classNum,schoolNum,jie from class cla where cla.gradeNum={grade} and cla.studentType={type} ) cla ON c.classNum=cla.id  LEFT JOIN subject g ON g.subjectNum = r.subjectNum where c.schoolNum is not null  and cla.classNum is not null order by g.orderNum";
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("type", type);
        args.put("subCompose", subCompose);
        String showZSubjectString = "";
        if (null != showZSubject && !showZSubject.equals("true")) {
            showZSubjectString = " and issub != '0' ";
        }
        String tableName = "0".equals(fufen) ? "gradelevel" : "gradelevel_fufen";
        if (subCompose != null && !subCompose.equals("")) {
            subComposeStr = " and xuankezuhe={subCompose} ";
        } else {
            subComposeStr = " and xuankezuhe='0' ";
        }
        String sql2 = "SELECT g.subjectNum num ,sjt.subjectName name   FROM  (SELECT DISTINCT subjectNum FROM " + tableName + " WHERE examNum={exam} AND gradeNum={grade} AND studentType={type}  AND statisticType='0'  " + subComposeStr + " " + showZSubjectString + " ) g   LEFT JOIN `subject` sjt ON g.subjectNum=sjt.subjectNum  where g.subjectNum!={subject_totalScore} order by sjt.orderNum ";
        args.put("subject_totalScore", "-1");
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySubject(String exam, String school, String grade, String type, String showZSubject, String subjectNumm) {
        String sql2;
        String sql = "SELECT  DISTINCT g.subjectNum num,g.subjectName name  FROM (SELECT ep.examPaperNum,ep.gradeNum,ep.jie,ep.subjectNum FROM (SELECT examPaperNum,subjectNum,gradeNum,jie FROM exampaper WHERE examNum={exam} and gradeNum={grade}) ep ";
        String str = sql + ") r  LEFT JOIN classexam c ON c.examPaperNum=r.examPaperNum AND c.schoolNum={school}  LEFT JOIN (select id,classNum,schoolNum,jie from class cla where cla.gradeNum={grade} and cla.studentType={type} AND cla.schoolNum={school} ) cla ON c.classNum=cla.id  LEFT JOIN subject g ON g.subjectNum = r.subjectNum where c.schoolNum is not null  and cla.classNum is not null order by g.orderNum";
        String showZSubjectString = "";
        if (null != showZSubject && !showZSubject.equals("true")) {
            showZSubjectString = " and issub != '0' ";
        }
        if (subjectNumm != null || !subjectNumm.equals("null")) {
            sql2 = "SELECT g.subjectNum num ,sjt.subjectName name   FROM  (SELECT DISTINCT subjectNum FROM his_gradelevel WHERE examNum={exam} AND gradeNum={grade} AND studentType={type} AND statisticType='0' and subjectNum ={subjectNumm} and source='0' " + showZSubjectString + " ) g   LEFT JOIN `subject` sjt ON g.subjectNum=sjt.subjectNum  where g.subjectNum!={subject_totalScore} order by sjt.orderNum";
        } else {
            sql2 = "SELECT g.subjectNum num ,sjt.subjectName name   FROM  (SELECT DISTINCT subjectNum FROM his_gradelevel WHERE examNum={exam} AND gradeNum={grade} AND studentType={type} AND statisticType='0' and source='0' " + showZSubjectString + " ) g   LEFT JOIN `subject` sjt ON g.subjectNum=sjt.subjectNum  where g.subjectNum!={subject_totalScore}  order by sjt.orderNum";
        }
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(License.SCHOOL, school);
        args.put("type", type);
        args.put("subjectNumm", subjectNumm);
        args.put("subject_totalScore", "-1");
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSchool(String exam, String subject, String type, String grade, String fufen) {
        String sjtStr = "";
        if (null != subject && subject.length() < 3) {
            subject = null;
        }
        if (null != subject && !subject.equals("")) {
            sjtStr = " AND g.subjectNum={subject} ";
        }
        String tableName = "0".equals(fufen) ? "gradelevel" : "gradelevel_fufen";
        String sql = "select  DISTINCT g.schoolNum num,s.shortname name from " + tableName + " g INNER join school  s on s.id = g.schoolNum and g.examNum={exam} " + sjtStr + "  where g.gradeNum={grade} and g.source = '0' and g.statisticType = '0' and g.studenttype=  {type}  ORDER BY convert(s.schoolName using gbk)";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("type", type);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSchool2(String exam, String subject, String type, String grade, String uid, String subCompose) {
        String subComposeStr;
        String sql;
        String sjtStr = "";
        if (null != subject && subject.length() < 3) {
            subject = null;
        }
        if (null != subject && !subject.equals("")) {
            sjtStr = " AND g.subjectNum={subject}  ";
        }
        if (subCompose != null && !subCompose.equals("")) {
            subComposeStr = " and g.xuankezuhe={subCompose} ";
        } else {
            subComposeStr = " and g.xuankezuhe='0' ";
        }
        if (uid.equals("-1") || uid.equals("-2")) {
            sql = "select  DISTINCT g.schoolNum num,s.shortname name from gradelevel g INNER join school  s on s.id = g.schoolNum and g.examNum={exam} " + sjtStr + "  where g.gradeNum={grade} and g.statisticType = '0' and g.studenttype= {type} " + subComposeStr + " ORDER BY convert(s.schoolName using gbk)";
        } else {
            sql = "select  DISTINCT g.schoolNum num,s.shortname name from gradelevel g  INNER join school  s on s.id = g.schoolNum and g.examNum={exam} " + sjtStr + "  left join schauthormanage h on h.schoolNum = s.id and h.userId={uid}   left join user t on t.schoolNum = s.id  and t.id = {uid} and t.usertype=1   where g.gradeNum={grade} and g.statisticType = '0' and g.studenttype= {type} " + subComposeStr + "  and (h.schoolNum is not null or t.schoolNum is not null) ORDER BY convert(s.schoolName using gbk)";
        }
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("subCompose", subCompose);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("type", type);
        args.put("uid", uid);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getArea(String exam, String uid) {
        String sql;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("uid", uid);
        if (uid.equals("-1") || uid.equals("-2")) {
            sql = "select  DISTINCT g.topItemId num,g.topItemName name from statisticitem g   where g.examNum = {exam} ";
        } else {
            sql = "select  DISTINCT g.topItemId num,g.topItemName name from statisticitem g  where g.examNum = {exam} ";
            if (this.dao2._queryArrayList(sql, args).size() != 0) {
                sql = "select DISTINCT s.topItemId num,s.topItemName name  from   statisticitem s  left join   (  select g.topItemId  from statisticitem g  left join schauthormanage h on h.schoolNum = g.sItemId and h.userId={uid}  left join user u on u.schoolNum = g.sItemId  and u.id = {uid} and u.usertype=1  where g.examNum = {exam} and g.statisticItem='01' and h.teacherName is null and u.id is null  ) q on q.topItemId =s.topItemId  where s.examNum = {exam} and q.topItemId is null ";
            }
        }
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySchool(String exam, String subject, String type) {
        String sjtStr = "";
        if (null != subject && subject.length() < 3) {
            subject = null;
        }
        if (null != subject && !subject.equals("")) {
            sjtStr = " AND subjectNum='" + subject + "' ";
        }
        if (null == subject || !subject.equals("")) {
        }
        String sql = "SELECT  DISTINCT c.schoolNum num,s.shortname name  FROM ( SELECT ep.examPaperNum,ep.gradeNum ,ep.jie FROM (SELECT examPaperNum,subjectNum,gradeNum,jie FROM his_exampaper WHERE examNum={exam} " + sjtStr + "  ) ep ";
        if (null == subject || !subject.equals("")) {
        }
        String sql2 = sql + ") r LEFT JOIN his_classexam c ON c.examPaperNum=r.examPaperNum LEFT JOIN school s ON s.id = c.schoolNum where s.schoolNum is not null  ORDER BY convert(s.schoolName using gbk)";
        Map args = StreamMap.create().put("exam", (Object) exam);
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySchool2(String exam, String subject, String type, String uid) {
        String sjtStr = "";
        if (null != subject && subject.length() < 3) {
            subject = null;
        }
        if (null != subject && !subject.equals("")) {
            sjtStr = " AND subjectNum={subject} ";
        }
        String sql = "SELECT  DISTINCT c.schoolNum num,s.shortname name  FROM ( SELECT ep.examPaperNum,ep.gradeNum ,ep.jie FROM (SELECT examPaperNum,subjectNum,gradeNum,jie FROM his_exampaper WHERE examNum={exam}" + sjtStr + "  ) ep ) r LEFT JOIN his_classexam c ON c.examPaperNum=r.examPaperNum LEFT JOIN school s ON s.id = c.schoolNum  left join schauthormanage h on h.schoolNum = s.id and h.userId={uid}  left join user t on t.schoolNum = s.id  and t.id = {uid} and t.usertype=1  where s.schoolNum is not null and (h.schoolNum is not null or t.schoolNum is not null) ORDER BY convert(s.schoolName using gbk)";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("exam", exam);
        args.put("uid", uid);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getGrade(String exam, String subject, String school, String type, String userId) {
        String sql;
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("exam", exam);
        if (null == subject || !subject.equals("")) {
        }
        if (null == subject || subject.length() < 3) {
        }
        if ("-2".equals(userId) || "-1".equals(userId)) {
            sql = "SELECT  DISTINCT g.gradeNum num,g.gradeName name,if(e.examDate<g.updateDate,'0',g.studentReportShowItem) ext1  FROM exampaper ep  LEFT JOIN basegrade g ON g.gradeNum = ep.gradeNum LEFT JOIN exam e ON e.examNum={exam} WHERE ep.examNum={exam} ";
        } else {
            sql = "SELECT  DISTINCT g.gradeNum num,g.gradeName name,if(e.examDate<g.updateDate,'0',g.studentReportShowItem) ext1  FROM exampaper ep  LEFT JOIN basegrade g ON g.gradeNum = ep.gradeNum LEFT JOIN exam e ON e.examNum={exam} LEFT JOIN astrict a on ep.examNum=a.examNum and ep.gradeNum=a.gradeNum WHERE ep.examNum={exam} and a.partType=5  and a.userType=1 and a.`status`=1";
        }
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> checkSchManage(String uid) {
        Map args = StreamMap.create().put("uid", (Object) uid);
        return this.dao2._queryBeanList("SELECT  id num from schauthormanage where userId={uid} ", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> checkSchManageByUidAndSid(String uid, String sid) {
        Map args = StreamMap.create().put("uid", (Object) uid).put("sid", (Object) sid);
        return this.dao2._queryBeanList("SELECT  id num from schauthormanage where userId={uid} and schoolNum={sid} and isDelete='F'", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryGrade(String exam, String subject, String school, String type, String userId) {
        String sql;
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("exam", exam);
        if (null == subject || !subject.equals("")) {
        }
        if (null != subject && subject.length() < 3) {
            subject = null;
        }
        if ("-2".equals(userId) || "-1".equals(userId)) {
            if (null == subject || !subject.equals("")) {
            }
            String sql2 = "SELECT  DISTINCT g.gradeNum num,g.gradeName name  FROM (SELECT ep.examPaperNum,ep.gradeNum,ep.jie FROM (SELECT examPaperNum,subjectNum,gradeNum,jie FROM his_exampaper WHERE examNum={exam} ) ep ";
            if (null == subject || !subject.equals("")) {
            }
            sql = sql2 + ") r  LEFT JOIN his_classexam c ON c.examPaperNum=r.examPaperNum  LEFT JOIN class cla ON c.classNum=cla.id  LEFT JOIN basegrade g ON g.gradeNum = cla.gradeNum where g.gradeNum is not null ";
        } else {
            sql = "SELECT  DISTINCT g.gradeNum num,g.gradeName name  FROM ( SELECT ep.examPaperNum,ep.gradeNum,ep.jie FROM (SELECT examPaperNum,subjectNum,gradeNum,jie,examNum FROM his_exampaper WHERE examNum={exam} ) ep ) r  LEFT JOIN his_classexam c ON c.examPaperNum=r.examPaperNum  LEFT JOIN class cla ON c.classNum=cla.id LEFT JOIN basegrade g ON g.gradeNum = cla.gradeNum  LEFT JOIN astrict a on ep.examNum=a.examNum and ep.gradeNum=a.gradeNum where ep.examNum={exam} and a.partType=5  and a.userType=1 and a.`status`=1 and g.gradeNum is not null ";
        }
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSubjectType(String exam, String subject, String school, String grade) {
        String gradeStr = "";
        String examStr = "";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("grade", grade);
        args.put("exam", exam);
        if (null == subject || !subject.equals("")) {
        }
        if (null != grade && !grade.equals("")) {
            gradeStr = " AND g.gradeNum={grade} ";
        }
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            examStr = " AND g.examNum = {exam}  ";
        }
        String sql = "select g.num,d1.name name from  (select DISTINCT g.studentType num FROM arealevel g where  1=1  " + examStr + gradeStr + " ) g LEFT join ( select * from `data` d1 where d1.type='25' ) d1 on d1.`value`=g.num ";
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySubjectType(String exam, String subject, String school, String grade) {
        String gradeStr = "";
        String examStr = "";
        if (null == subject || !subject.equals("")) {
        }
        if (null != subject && subject.length() < 3) {
            subject = null;
        }
        if (null != grade && !grade.equals("")) {
            gradeStr = " AND gradeNum={grade} ";
        }
        if (null != exam && !exam.equals("") && !exam.equals("-1")) {
            examStr = " AND examNum = {exam}  ";
        }
        if (null == subject || !subject.equals("")) {
        }
        String sql = "SELECT  DISTINCT cla.studentType num,d1.name name  FROM (SELECT ep.examPaperNum,ep.gradeNum ,ep.jie FROM (SELECT examPaperNum,subjectNum,gradeNum,jie FROM his_exampaper WHERE 1=1" + examStr + gradeStr + "  ) ep ";
        if (null == subject || !subject.equals("")) {
        }
        String sql2 = sql + ") r  LEFT JOIN his_classexam c ON c.examPaperNum=r.examPaperNum  LEFT JOIN class cla ON c.classNum=cla.id   LEFT JOIN `data` d1 ON d1.type='25' AND d1.`value`=cla.studentType  where c.examPaperNum=r.examPaperNum and cla.classNum is not null ";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("grade", grade);
        args.put("exam", exam);
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getClass(String exam, String subject, String school, String grade, String type, String level, String islevel, String subCompose) {
        String subComposeStr;
        if (null == subject || !subject.equals("")) {
        }
        if (school.indexOf(44) != -1 || "allschool".equals(school)) {
            return new ArrayList();
        }
        if (subCompose != null && !"".equals(subCompose)) {
            subComposeStr = " and s.xuankezuhe={subCompose} ";
        } else {
            subComposeStr = " and s.xuankezuhe='0' ";
        }
        String sql = ((("SELECT   s.classNum num,lc.className name   FROM(   ") + "SELECT DISTINCT classNum   ") + "FROM classlevel  s   ") + "WHERE  s.examNum={exam} ";
        if (subject != null && !"".equals(subject)) {
            sql = sql + "  AND s.subjectNum=" + subject;
        }
        String sql2 = ((sql + "  AND s.gradeNum={grade} AND  s.studentType={type} " + subComposeStr + " AND s.statisticType='0'  AND schoolNum={school}   ") + ")s   ") + "LEFT JOIN class lc ON s.classNum = lc.id  ORDER BY LENGTH(lc.classNum),lc.classNum ";
        if (null != level && level.equals("T")) {
            if ((islevel == null || islevel.equals("1")) && subject != null && !"".equals(subject)) {
                return getLevelClass(exam, subject, school, grade, type, subCompose);
            }
            String sql3 = ((("SELECT   s.classNum num,lc.className name   FROM(   ") + "SELECT DISTINCT classNum   ") + "FROM classlevel_fc  s   ") + "WHERE  s.examNum={exam} ";
            if (null != subject && !subject.equals("")) {
                sql3 = sql3 + "  AND s.subjectNum=" + subject;
            }
            sql2 = ((sql3 + "  AND s.gradeNum={grade} AND  s.studentType={type} " + subComposeStr + " AND s.statisticType='0'  AND schoolNum={school}   ") + ")s   ") + "LEFT JOIN class lc ON s.classNum = lc.id  ORDER BY LENGTH(lc.className),convert(lc.className using gbk) ";
        }
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("subCompose", subCompose);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("type", type);
        args.put(License.SCHOOL, school);
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getClass1(String exam, String subject, String school, String grade, String type, String level) {
        if (null == subject || !subject.equals("")) {
        }
        if (null != subject && (subject.length() < 3 || subject.equals("-1"))) {
            subject = null;
        }
        if (school.indexOf(44) != -1) {
            return new ArrayList();
        }
        if (null != level && level.equals("T") && subject != null) {
            return getLevelClass(exam, subject, school, grade, type, "0");
        }
        String schoolStr1 = "";
        String schoolStr2 = "";
        String schoolStr = "";
        if (!"allschool".equals(school)) {
            schoolStr1 = " AND c.schoolNum={school}  ";
            schoolStr2 = " AND cla.schoolNum={school} ";
            schoolStr = " AND sl.schoolNum={school} ";
        }
        String sql = "SELECT  DISTINCT cla.id num,cla.className name  FROM ";
        if (null != subject && !subject.equals("")) {
            sql = sql + "(SELECT distinct pid  subjectNum FROM subject WHERE  subjectNum={subject} UNION SELECT '" + subject + "'  subjectNum )r LEFT JOIN ";
        }
        String sql2 = sql + " exampaper  ep ";
        if (null != subject && !subject.equals("")) {
            sql2 = sql2 + "ON ep.subjectNum=r.subjectNum ";
        }
        String str = sql2 + "LEFT JOIN classexam c ON c.examPaperNum=ep.pexamPaperNum " + schoolStr1 + "LEFT JOIN class cla ON  cla.gradeNum={grade}  " + schoolStr2 + "WHERE  ep.examNum={exam} and ep. gradeNum={grade}  and cla.id=c.classNum AND cla.studentType={type}  ORDER BY LENGTH(cla.classNum),cla.classNum ";
        String sql3 = "SELECT  DISTINCT cla.id num,cla.className name FROM studentlevel sl  LEFT JOIN class cla ON cla.id = sl.classNum ";
        String subStr = "";
        if (null != subject && !subject.equals("")) {
            sql3 = sql3 + " LEFT JOIN `subject` sub ON sub.pid = sl.subjectNum ";
            subStr = " AND sl.subjectNum = {subject} ";
        }
        String sql4 = sql3 + " WHERE sl.examNum = {exam} AND sl.gradeNum = {grade} " + schoolStr + subStr + " AND cla.studentType={type}  ORDER BY LENGTH(cla.classNum),cla.classNum ";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put(License.SCHOOL, school);
        args.put("grade", grade);
        args.put("exam", exam);
        args.put("type", type);
        return this.dao2._queryBeanList(sql4, AjaxData.class, args);
    }

    public List<AjaxData> getLevelClass(String exam, String subject, String school, String grade, String type, String subCompose) {
        String subComposeStr;
        if (subCompose != null && !subCompose.equals("")) {
            subComposeStr = " and s.xuankezuhe={subCompose} ";
        } else {
            subComposeStr = " and s.xuankezuhe='0' ";
        }
        StringBuffer sql = new StringBuffer("");
        sql.append("SELECT   s.classNum num,if(s.issub='0',c.className,lc.className) name   ");
        sql.append("FROM(   ");
        sql.append("SELECT DISTINCT classNum  ,issub ");
        sql.append("FROM studentlevel  s   ");
        sql.append("WHERE  s.examNum={exam} ");
        if (subject != null) {
            sql.append("  AND s.subjectNum={subject} ");
        }
        sql.append("  AND s.gradeNum={grade} AND  s.studentType={type} AND s.statisticType='0'  AND schoolNum={school} " + subComposeStr + " ");
        sql.append(")s   ");
        sql.append("LEFT JOIN levelclass lc ON s.classNum = lc.id  ");
        sql.append("LEFT JOIN class c ON s.classNum = c.id   ");
        Map args = new HashMap();
        args.put("subCompose", subCompose);
        args.put("exam", exam);
        args.put("subject", subject);
        args.put("grade", grade);
        args.put("type", type);
        args.put(License.SCHOOL, school);
        return this.dao2._queryBeanList(sql.toString(), AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryClass(String exam, String subject, String school, String grade, String type, String level) {
        if (null == subject || !subject.equals("")) {
        }
        if (null != subject && (subject.length() < 3 || subject.equals("-1"))) {
            subject = null;
        }
        String sql = "SELECT  DISTINCT cla.id num,cla.className name  FROM (SELECT ep.examPaperNum,ep.gradeNum,ep.jie FROM ";
        if (null != subject && !subject.equals("")) {
            sql = sql + "(SELECT distinct pid  subjectNum FROM subject WHERE  subjectNum={subject} UNION SELECT '" + subject + "'  subjectNum )r LEFT JOIN ";
        }
        String sql2 = sql + "(SELECT examPaperNum,subjectNum,gradeNum,jie FROM his_exampaper WHERE examNum={exam} and gradeNum={grade} ) ep ";
        if (null != subject && !subject.equals("")) {
            sql2 = sql2 + "ON ep.subjectNum=r.subjectNum ";
        }
        String sql3 = sql2 + ") r  LEFT JOIN his_classexam c ON c.examPaperNum=r.examPaperNum AND c.schoolNum={school}  LEFT JOIN class cla ON c.classNum=cla.id AND cla.studentType={type}  WHERE cla.classNum IS NOT NULL ORDER BY LENGTH(cla.classNum),cla.classNum ";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(License.SCHOOL, school);
        args.put("type", type);
        return this.dao2._queryBeanList(sql3, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryClass1(String exam, String subject, String school, String grade, String type, String level) {
        if (null == subject || !subject.equals("")) {
        }
        if (null != subject && (subject.length() < 3 || subject.equals("-1"))) {
            subject = null;
        }
        String schoolStr = "";
        if (!school.equals("allschool")) {
            schoolStr = " AND c.schoolNum={school} ";
        }
        String sql = "SELECT  DISTINCT cla.id num,cla.className name  FROM (SELECT ep.examPaperNum,ep.gradeNum,ep.jie FROM ";
        if (null != subject && !subject.equals("")) {
            sql = sql + "(SELECT distinct pid  subjectNum FROM subject WHERE  subjectNum={subject} UNION SELECT '" + subject + "'  subjectNum )r LEFT JOIN ";
        }
        String sql2 = sql + "(SELECT examPaperNum,subjectNum,gradeNum,jie FROM his_exampaper WHERE examNum={exam} and gradeNum={grade} ) ep ";
        if (null != subject && !subject.equals("")) {
            sql2 = sql2 + "ON ep.subjectNum=r.subjectNum ";
        }
        String sql3 = sql2 + ") r  LEFT JOIN his_classexam c ON c.examPaperNum=r.examPaperNum  " + schoolStr + "LEFT JOIN class cla ON c.classNum=cla.id AND cla.studentType={type}  WHERE cla.classNum IS NOT NULL ORDER BY LENGTH(cla.classNum),cla.classNum ";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put(License.SCHOOL, school);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("type", type);
        return this.dao2._queryBeanList(sql3, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getStudent(String exam, String subject, String school, String grade, String classNum, String levelclass, String islevel, String subCompose) {
        String subComposeStr;
        String sjtStr = "";
        if (null != subject && !subject.equals("")) {
            sjtStr = " AND r.subjectNum={subject} ";
        }
        if (subCompose != null && !subCompose.equals("")) {
            subComposeStr = " and r.xuankezuhe={subCompose} ";
        } else {
            subComposeStr = " and r.xuankezuhe='0' ";
        }
        String sql = "SELECT distinct r.studentId num,s.studentName name  FROM  studentlevel r LEFT JOIN student s ON r.studentId=s.id   WHERE r.examNum={exam} " + sjtStr + " AND r.gradeNum={grade} AND r.schoolNum={school} AND r.classNum={classNum} " + subComposeStr + " ORDER BY convert(s.studentName using gbk) ";
        if (levelclass != null && levelclass.equals("T") && islevel != null && islevel.equals("0")) {
            sql = "SELECT distinct  r.studentId num,s.studentName name  FROM  studentlevel_fc r LEFT JOIN student s ON r.studentId=s.id   WHERE r.examNum={exam} " + sjtStr + " AND r.gradeNum={grade} AND r.schoolNum={school} AND r.classNum={classNum} " + subComposeStr + " ORDER BY convert(s.studentName using gbk) ";
        }
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("subCompose", subCompose);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(License.SCHOOL, school);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryStudent(String exam, String subject, String school, String grade, String classNum) {
        String sjtStr;
        if (null != subject && !subject.equals("")) {
            sjtStr = " AND subjectNum={subject} ";
        } else {
            sjtStr = " ";
        }
        String sql = "SELECT  r.studentId num,s.studentName name  FROM (SELECT studentId,schoolNum,gradeNum FROM his_studentlevel WHERE examNum={exam} " + sjtStr + " AND gradeNum={grade} AND schoolNum={school} AND classNum={classNum}   AND statisticType='0' and source='0'  ) r  LEFT JOIN student s ON r.studentId=s.id   ORDER BY convert(s.studentName using gbk) ";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(License.SCHOOL, school);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getStudent_Student(String exam, String subject, String school, String grade, String classNum, String studentId) {
        String sjtStr = "";
        if (null != subject && !subject.equals("")) {
            sjtStr = " AND subjectNum={subject} ";
        }
        String str = "SELECT  r.studentId num,s.studentName name  FROM (SELECT studentId,schoolNum,gradeNum FROM studentlevel WHERE studentId={studentId} AND examNum={exam} " + sjtStr + " AND gradeNum={grade} AND schoolNum={school} AND classNum={classNum}  AND statisticType='0' and source='0'   ) r  LEFT JOIN student s ON r.studentId=s.id   ORDER BY convert(s.studentName using gbk) ";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(License.SCHOOL, school);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        return this.dao2._queryBeanList("select id num,studentName name from student where id = {studentId} ", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryStudent_Student(String exam, String subject, String school, String grade, String classNum, String studentId) {
        String sjtStr = "";
        if (null != subject && !subject.equals("")) {
            sjtStr = " AND subjectNum={subject} ";
        }
        String sql = "SELECT  r.studentId num,s.studentName name  FROM (SELECT studentId,schoolNum,gradeNum FROM his_studentlevel WHERE studentId={studentId} AND examNum={exam} " + sjtStr + " AND gradeNum={grade} AND schoolNum={school} AND classNum={classNum}  AND statisticType='0' and source='0'   ) r  LEFT JOIN student s ON r.studentId=s.id   ORDER BY convert(s.studentName using gbk) ";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(License.SCHOOL, school);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSchool_Student(String exam, String subject, String type, String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryBeanList("SELECT sch.id num,sch.shortname  name FROM (SELECT studentId,classNum,gradeNum,schoolNum,jie FROM student WHERE id={studentId} )s LEFT JOIN school sch ON s.schoolNum=sch.id", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySchool_Student(String exam, String subject, String type, String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryBeanList("SELECT sch.id num,sch.shortname  name FROM (SELECT studentId,classNum,gradeNum,schoolNum,jie FROM student WHERE id={studentId} )s LEFT JOIN school sch ON s.schoolNum=sch.id", AjaxData.class, args);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getGrade_Student(String exam, String subject, String school, String type, String studentId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        new ArrayList();
        List _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT s.gradeNum num,g.gradeName name,if(e.examDate<g.updateDate,'0',g.studentReportShowItem) ext1 FROM studentlevel s LEFT JOIN basegrade g ON  s.studentId = {studentId}  AND s.examNum = {exam} AND g.gradeNum=s.gradeNum LEFT JOIN exam e ON e.examNum={exam} LEFT JOIN astrict a on s.examNum=a.examNum and s.gradeNum=a.gradeNum  WHERE s.studentId={studentId} AND  s.examNum = {exam} and a.partType=5  and a.userType=23 and a.`status`=1 ", AjaxData.class, args);
        if (_queryBeanList.size() == 0) {
            _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT s.gradeNum num,g.gradeName name,if(e.examDate<g.updateDate,'0',g.studentReportShowItem) ext1 FROM studentlevel_butongji s LEFT JOIN basegrade g ON  s.studentId = {studentId}  AND s.examNum = {exam} AND g.gradeNum=s.gradeNum LEFT JOIN exam e ON e.examNum={exam} LEFT JOIN astrict a on s.examNum=a.examNum and s.gradeNum=a.gradeNum  WHERE s.studentId={studentId} AND  s.examNum = {exam} and a.partType=5  and a.userType=23 and a.`status`=1 ", AjaxData.class, args);
        }
        return _queryBeanList;
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryGrade_Student(String exam, String subject, String school, String type, String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId).put("exam", (Object) exam);
        return this.dao2._queryBeanList("SELECT DISTINCT s.gradeNum num,e.gradeName name FROM his_studentlevel s LEFT JOIN  basegrade e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.gradeNum=s.gradeNum LEFT JOIN astrict a on s.examNum=a.examNum and s.gradeNum=a.gradeNum WHERE s.studentId={studentId} AND  s.examNum = {exam} and a.partType=5  and a.userType=23 and a.`status`=1 ", AjaxData.class, args);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getClass_Student(String exam, String subject, String school, String grade, String type, String studentId, String levelclass, String islevel, String subCompose) {
        String subString;
        String sql;
        String sql2;
        if (null == subject || "".equals(subject)) {
            subString = "";
        } else {
            subString = " and s.subjectNum={subject}  ";
        }
        if (null != levelclass && levelclass.equals("T") && subject != null && !"".equals(subject)) {
            sql = "SELECT DISTINCT s.classNum num,e.className name FROM studentlevel s LEFT JOIN  levelclass e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND  e.id=s.classNum WHERE s.studentId={studentId} AND  s.examNum = {exam+subString}  ";
            if (islevel != null && islevel.equals("0")) {
                sql = "SELECT DISTINCT s.classNum num,e.className name FROM studentlevel_fc s LEFT JOIN  class e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND  e.id=s.classNum WHERE s.studentId={studentId} AND  s.examNum = {exam} " + subString;
            }
        } else {
            sql = "SELECT DISTINCT s.classNum num,e.className name FROM studentlevel s LEFT JOIN  class e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND  e.id=s.classNum WHERE s.studentId={studentId} AND  s.examNum = {exam} " + subString + "";
        }
        Map args = new HashMap();
        args.put("subject", subject);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        List _queryBeanList = this.dao2._queryBeanList(sql, AjaxData.class, args);
        if (_queryBeanList.size() == 0) {
            if (null != levelclass && levelclass.equals("T") && subject != null && !"".equals(subject)) {
                sql2 = "SELECT DISTINCT s.classNum num,e.className name FROM studentlevel_butongji s LEFT JOIN  levelclass e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND  e.id=s.classNum WHERE s.studentId={studentId} AND  s.examNum = {exam+subString}  ";
                if (islevel != null && islevel.equals("0")) {
                    sql2 = "SELECT DISTINCT s.classNum num,e.className name FROM studentlevel_butongji s LEFT JOIN  class e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND  e.id=s.classNum WHERE s.studentId={studentId} AND  s.examNum = {exam} " + subString;
                }
            } else {
                sql2 = "SELECT DISTINCT s.classNum num,e.className name FROM studentlevel_butongji s LEFT JOIN  class e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND  e.id=s.classNum WHERE s.studentId={studentId} AND  s.examNum = {exam} " + subString + "";
            }
            _queryBeanList = this.dao2._queryBeanList(sql2, AjaxData.class, args);
        }
        return _queryBeanList;
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryClass_Student(String exam, String subject, String school, String grade, String type, String studentId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        return this.dao2._queryBeanList("SELECT DISTINCT s.classNum num,e.className name FROM his_studentlevel s LEFT JOIN  class e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND  e.id=s.classNum WHERE s.studentId={studentId} AND  s.examNum = {exam}  ", AjaxData.class, args);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSubject_Student(String exam, String type, String studentId, String SubjectNum, String subCompose) {
        String subComposeStr;
        String sql;
        String sql2;
        if (subCompose != null && !subCompose.equals("")) {
            subComposeStr = " and s.xuankezuhe={subCompose} ";
        } else {
            subComposeStr = " and s.xuankezuhe=0";
        }
        if (SubjectNum != null) {
            sql = "SELECT DISTINCT s.subjectNum num,e.subjectName name FROM studentlevel s LEFT JOIN  subject e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.subjectNum=s.subjectNum WHERE s.studentId={studentId} AND  s.examNum = {exam} and e.isHidden='F' and s.subjectNum ={SubjectNum} " + subComposeStr;
        } else {
            sql = "SELECT DISTINCT s.subjectNum num,e.subjectName name FROM studentlevel s LEFT JOIN  subject e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.subjectNum=s.subjectNum WHERE s.studentId={studentId} AND  s.examNum = {exam} and e.isHidden='F'" + subComposeStr;
        }
        Map args = new HashMap();
        args.put("subCompose", subCompose);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        args.put("SubjectNum", SubjectNum);
        List _queryBeanList = this.dao2._queryBeanList(sql, AjaxData.class, args);
        if (_queryBeanList.size() == 0) {
            if (SubjectNum != null) {
                sql2 = "SELECT DISTINCT s.subjectNum num,e.subjectName name FROM studentlevel_butongji s LEFT JOIN  subject e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.subjectNum=s.subjectNum WHERE s.studentId={studentId} AND  s.examNum = {exam} and e.isHidden='F' and s.subjectNum ={SubjectNum} " + subComposeStr;
            } else {
                sql2 = "SELECT DISTINCT s.subjectNum num,e.subjectName name FROM studentlevel_butongji s LEFT JOIN  subject e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.subjectNum=s.subjectNum WHERE s.studentId={studentId} AND  s.examNum = {exam} and e.isHidden='F'" + subComposeStr;
            }
            _queryBeanList = this.dao2._queryBeanList(sql2, AjaxData.class, args);
        }
        return _queryBeanList;
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySubject_Student(String exam, String type, String studentId, String subjectNum) {
        String sql;
        if (subjectNum != null) {
            sql = "SELECT DISTINCT s.subjectNum num,e.subjectName name FROM his_studentlevel s LEFT JOIN  subject e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.subjectNum=s.subjectNum WHERE s.studentId={studentId} AND  s.examNum = {exam} and e.isHidden='F' and s.subjectNum={subjectNum} ";
        } else {
            sql = "SELECT DISTINCT s.subjectNum num,e.subjectName name FROM his_studentlevel s LEFT JOIN  subject e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.subjectNum=s.subjectNum WHERE s.studentId={studentId} AND  s.examNum = {exam} and e.isHidden='F'";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSubjectType_Student(String exam, String subject, String school, String studentId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        List _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT s.studentType num,e.name  FROM studentlevel s LEFT JOIN  `data` e ON   s.studentId = {studentId}  AND s.examNum = {exam}  AND e.type='25' AND e.`value`=s.studentType   WHERE s.studentId={studentId} AND  s.examNum ={exam}  ", AjaxData.class, args);
        if (_queryBeanList.size() == 0) {
            _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT s.studentType num,e.name  FROM studentlevel_butongji s LEFT JOIN  `data` e ON   s.studentId = {studentId}  AND s.examNum = {exam}  AND e.type='25' AND e.`value`=s.studentType   WHERE s.studentId={studentId} AND  s.examNum ={exam}  ", AjaxData.class, args);
        }
        return _queryBeanList;
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySubjectType_Student(String exam, String subject, String school, String grade, String studentId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        return this.dao2._queryBeanList("SELECT DISTINCT s.studentType num,e.name  FROM his_studentlevel s LEFT JOIN  `data` e ON   s.studentId = {studentId}  AND s.examNum = {exam}  AND e.type='25' AND e.`value`=s.studentType   WHERE s.studentId={studentId} AND  s.examNum = {exam}  ", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getExam_Student(String startTime, String endTime, String studentId) {
        if (null != startTime && !startTime.equals("")) {
            startTime = startTime + " AND examDate>'" + startTime + "' ";
        }
        if (null != endTime && !endTime.equals("")) {
            String str = startTime + " AND examDate<'" + endTime + "' ";
        }
        String sql = "SELECT examNum ,examName  FROM exam WHERE 1=1   and isDelete ='F' ORDER BY insertDate DESC";
        String str2 = "SELECT DISTINCT r.examNum num,r.examName name FROM (SELECT studentId,classNum,gradeNum,schoolNum,jie FROM student WHERE id={studentId} )s LEFT JOIN exampaper ep ON ep.jie=s.jie LEFT JOIN (" + sql + ")r on  r.examNum=ep.examNum WHERE   r.examNum IS NOT NULL ";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        return this.dao2._queryBeanList(" select num,name from ( SELECT DISTINCT s.examNum num,e.examName name,e.examDate  FROM studentlevel s   LEFT JOIN  exam e ON  s.studentId = {studentId} AND s.examNum = e.examNum INNER JOIN astrict a on s.examNum=a.examNum  and s.gradeNum=a.gradeNum  and a.partType=5  and a.userType=23 and a.`status`=1  WHERE s.studentId={studentId} AND  s.examNum = e.examNum  union all  select DISTINCT s.examNum num,e.examName name,e.examDate from studentlevel_butongji s inner join (select es.examNum,es.source from examsourceset es left join (select * from data where type=26 and value<>0) d on es.source=d.value where  es.isJoin=1) da on s.examNum=da.examNum and s.source=da.source  LEFT JOIN  exam e ON  s.studentId = {studentId} AND s.examNum = e.examNum INNER JOIN astrict a on s.examNum=a.examNum  and s.gradeNum=a.gradeNum  and a.partType=5  and a.userType=23 and a.`status`  WHERE s.studentId={studentId} AND  s.examNum = e.examNum  )a    order by examDate desc", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryExam_Student(String startTime, String endTime, String studentId) {
        if (null != startTime && !startTime.equals("")) {
            startTime = startTime + " AND examDate>{startTime} ";
        }
        if (null != endTime && !endTime.equals("")) {
            startTime = startTime + " AND examDate<{endTime} ";
        }
        String sql = "SELECT examNum ,examName  FROM his_exam WHERE 1=1   and isDelete ='F' ORDER BY insertDate DESC";
        String sqll = "SELECT DISTINCT r.examNum num,r.examName name FROM (SELECT studentId,classNum,gradeNum,schoolNum,jie FROM student WHERE id={studentId})s LEFT JOIN his_exampaper ep ON ep.jie=s.jie LEFT JOIN (" + sql + ")r on  r.examNum=ep.examNum WHERE   r.examNum IS NOT NULL ";
        Map args = new HashMap();
        args.put("startTime", startTime);
        args.put("endTime", endTime);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        return this.dao2._queryBeanList(sqll, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getGraduationType(String exam, String subject, String school, String grade, String StudentType, String classNum, String StudentId, String subCompose) {
        Map args = new HashMap();
        args.put("Data_graduationType", Const.Data_graduationType);
        args.put("exam", exam);
        args.put("subject", subject);
        args.put(License.SCHOOL, school);
        args.put("grade", grade);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("subCompose", subCompose);
        String query = "";
        if (null != exam && !exam.equals("")) {
            query = query + "  examNum = {exam} and ";
        }
        if (null != subject && !subject.equals("")) {
            query = query + "  subjectNum = {subject} and ";
        }
        if (null != school && !school.equals("") && !school.equals("allschool") && !school.equals("-1")) {
            query = query + "  schoolNum = {school} and ";
        }
        if (null != grade && !grade.equals("")) {
            query = query + "  gradeNum = {grade} and ";
        }
        if (null != classNum && !classNum.equals("") && !classNum.equals("-1")) {
            query = query + "  classNum = {classNum} and ";
        }
        if (null != subCompose && !subCompose.equals("")) {
            query = query + "  xuankezuhe = {subCompose} and ";
        }
        if (!query.equals("")) {
            query = " where " + query.substring(0, query.length() - 4);
        }
        String sql = "select r.statisticType num, d.name name  from ( \tselect distinct statisticType  from gradelevel " + query + ") r left join (SELECT `value` num,`name` name  FROM `data` WHERE type={Data_graduationType} ) d on d.num=r.statisticType ";
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryGraduationType(String exam, String subject, String school, String grade, String StudentType, String classNum, String StudentId) {
        String query = "";
        Map args = new HashMap();
        args.put("Data_graduationType", Const.Data_graduationType);
        args.put("exam", exam);
        args.put("subject", subject);
        args.put(License.SCHOOL, school);
        args.put("grade", grade);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        if (null != exam && !exam.equals("")) {
            query = query + "  examNum = {exam} and ";
        }
        if (null != subject && !subject.equals("")) {
            query = query + "  subjectNum = {subject} and ";
        }
        if (null != school && !school.equals("")) {
            query = query + "  schoolNum = {school} and ";
        }
        if (null != grade && !grade.equals("")) {
            query = query + "  gradeNum = {grade} and ";
        }
        if (null != classNum && !classNum.equals("")) {
            query = query + "  classNum = {classNum} and ";
        }
        if (!query.equals("")) {
            query = " where " + query.substring(0, query.length() - 4);
        }
        String sql = "select r.statisticType num, d.name name  from ( \tselect distinct statisticType  from his_gradelevel " + query + ") r left join (SELECT `value` num,`name` name  FROM `data` WHERE type={Data_graduationType} ) d on d.num=r.statisticType ";
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getStuSourceType(String exam, String subject, String school, String grade, String StudentType, String classNum, String StudentId, String statisticType, String subCompose) {
        String query = "";
        if (null != exam && !exam.equals("")) {
            query = query + "  examNum = {exam} and ";
        }
        if (null != subject && !subject.equals("")) {
            query = query + "  subjectNum = {subject} and ";
        }
        if (null != school && !school.equals("") && !school.equals("allschool") && !school.equals("-1")) {
            query = query + "  schoolNum = {school} and ";
        }
        if (null != grade && !grade.equals("")) {
            query = query + "  gradeNum = {grade} and ";
        }
        if (null != classNum && !classNum.equals("") && !classNum.equals("-1")) {
            query = query + "  classNum = {classNum} and ";
        }
        if (null != statisticType && !statisticType.equals("")) {
            query = query + "  statisticType = {statisticType} and ";
        }
        if (null != StudentId && !StudentId.equals("") && !StudentId.equals("-1")) {
            query = query + "  studentId = {StudentId} and ";
        }
        if (null != subCompose && !subCompose.equals("")) {
            query = query + "  xuankezuhe = {subCompose} and ";
        }
        if (!query.equals("")) {
            query = " where " + query.substring(0, query.length() - 4);
        }
        String sql = "select r.source num, d.name name  from ( \tselect distinct source  from gradelevel " + query + ") r left join (SELECT `value` num,`name` name  FROM `data` WHERE type={Data_stuSource} ) d on d.num=r.source ";
        Map args = new HashMap();
        args.put("Data_stuSource", Const.Data_stuSource);
        args.put("exam", exam);
        args.put("subject", subject);
        args.put(License.SCHOOL, school);
        args.put("grade", grade);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("statisticType", statisticType);
        args.put("StudentId", StudentId);
        args.put("subCompose", subCompose);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryStuSourceType(String exam, String subject, String school, String grade, String StudentType, String classNum, String StudentId, String statisticType) {
        String query = "";
        if (null != exam && !exam.equals("")) {
            query = query + "  examNum = {exam} and ";
        }
        if (null != subject && !subject.equals("")) {
            query = query + "  subjectNum = {subject} and ";
        }
        if (null != school && !school.equals("")) {
            query = query + "  schoolNum = {school} and ";
        }
        if (null != grade && !grade.equals("")) {
            query = query + "  gradeNum = {grade} and ";
        }
        if (null != classNum && !classNum.equals("")) {
            query = query + "  classNum = {classNum} and ";
        }
        if (null != statisticType && !statisticType.equals("")) {
            query = query + "  statisticType = {statisticType} and ";
        }
        if (!query.equals("")) {
            query = " where " + query.substring(0, query.length() - 4);
        }
        String sql = "select r.source num, d.name name  from ( \tselect distinct source  from his_gradelevel " + query + ") r left join (SELECT `value` num,`name` name  FROM `data` WHERE type={Data_stuSource} ) d on d.num=r.source ";
        Map args = new HashMap();
        args.put("Data_stuSource", Const.Data_stuSource);
        args.put("exam", exam);
        args.put("subject", subject);
        args.put(License.SCHOOL, school);
        args.put("grade", grade);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("statisticType", statisticType);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    public static void main(String[] args) {
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public String getExamIsHistory(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        Object obj = this.dao2._queryObject("SELECT 'F'  FROM exam WHERE examNum={examNum}  union all SELECT 'T'  FROM his_exam WHERE examNum={examNum} ", args);
        if (obj != null) {
            String str = String.valueOf(obj);
            return str;
        }
        return "F";
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public String getExamIsMoreSchool(String examNum) {
        if (null == examNum || "".equals(examNum)) {
            return "F";
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        Object obj = this.dao2._queryObject("SELECT if(IFNULL(count(DISTINCT schoolNum),0)>1,'T','F') from gradelevel where examNum={examNum} ", args);
        if (obj != null) {
            String str = String.valueOf(obj);
            return str;
        }
        return "F";
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSubjectPer_F(String exam, String school, String grade, String type, String uid, String subCompose) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subCompose", subCompose);
        args.put("uid", uid);
        return this.dao2._queryBeanList("SELECT DISTINCT al.subjectNum num,s.subjectName name FROM (select DISTINCT subjectNum from arealevel where examNum = {exam} and gradeNum = {grade} and xuankezuhe= {subCompose}) al inner join (SELECT DISTINCT subjectNum FROM userposition WHERE  userNum={uid} and gradeNum={grade} AND subjectNum!='999' UNION ALL SELECT DISTINCT subjectNum FROM userposition_record WHERE examNum={exam} AND userNum={uid} and gradeNum={grade} AND subjectNum!='999' )u on u.subjectNum = al.subjectNum LEFT JOIN `subject` s ON s.subjectNum=u.subjectNum where s.subjectNum is not null", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySubjectPer_F(String exam, String school, String grade, String type, String uid) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade).put("uid", (Object) uid);
        return this.dao2._queryBeanList("SELECT DISTINCT al.subjectNum num,s.subjectName name FROM (select DISTINCT subjectNum from his_arealevel where examNum = {exam} and gradeNum = {grade} ) al inner join (SELECT DISTINCT subjectNum FROM userposition WHERE  userNum={uid} AND subjectNum!='999' UNION ALL SELECT DISTINCT subjectNum FROM his_userposition_record WHERE examNum={exam} AND userNum={uid} AND subjectNum!='999' )u on u.subjectNum = al.subjectNum LEFT JOIN `subject` s ON s.subjectNum=u.subjectNum where s.subjectNum is not null", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getGradePer_F(String exam, String subject, String school, String type, String uid) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("uid", uid);
        return this.dao2._queryBeanList("SELECT DISTINCT g.gradeNum num ,g.gradeName name,if(e.examDate<g.updateDate,'0',g.studentReportShowItem) ext1 FROM (select DISTINCT gradeNum from arealevel where examNum = {exam}) al inner join (SELECT DISTINCT gradeNum FROM userposition WHERE userNum={uid}  UNION ALL SELECT DISTINCT gradeNum FROM userposition_record WHERE userNum= {uid} AND examNum={exam} ) u on u.gradeNum = al.gradeNum LEFT JOIN basegrade g ON g.gradeNum=al.gradeNum  LEFT JOIN exam e ON e.examNum={exam} LEFT JOIN astrict a on  al.gradeNum=a.gradeNum where a.examNum={exam} and a.partType=5  and a.userType=1 and a.`status`=1 and g.gradeNum is not null", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryGradePer_F(String exam, String subject, String school, String type, String uid) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("uid", uid);
        return this.dao2._queryBeanList("SELECT DISTINCT g.gradeNum num ,g.gradeName name FROM (select DISTINCT gradeNum from his_arealevel where examNum = {exam} ) al inner join (SELECT DISTINCT gradeNum FROM userposition WHERE userNum={uid}  UNION ALL SELECT DISTINCT gradeNum FROM his_userposition_record WHERE userNum= {uid} AND examNum={exam} ) u on u.gradeNum = al.gradeNum LEFT JOIN basegrade g ON g.gradeNum=al.gradeNum  LEFT JOIN astrict a on  al.gradeNum=a.gradeNum where a.examNum={exam} and a.partType=5  and a.userType=1 and a.`status`=1 and g.gradeNum is not null", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSubjectTypePer_F(String exam, String subject, String school, String grade, String uid) {
        String gradeStr = "";
        if (null != grade && !grade.equals("")) {
            gradeStr = " AND gradeNum={grade} ";
        }
        String sql = "SELECT DISTINCT cla.studentType num ,d.name name  FROM classlevel cl inner join  (SELECT DISTINCT classNum FROM userposition WHERE userNum={uid}  " + gradeStr + " UNION ALL SELECT DISTINCT classNum FROM userposition_record WHERE userNum={uid}  " + gradeStr + " AND examNum={exam} ) u on u.classNum = cl.classNum LEFT JOIN class cla ON u.classNum=cla.id LEFT JOIN `data` d ON d.type='25' AND d.`value`=cla.studentType WHERE cl.examNum = {exam} and cl.gradeNum = {grade} and cla.id is not null";
        Map args = new HashMap();
        args.put("grade", grade);
        args.put("uid", uid);
        args.put("exam", exam);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySubjectTypePer_F(String exam, String subject, String school, String grade, String uid) {
        String gradeStr = "";
        if (null != grade && !grade.equals("")) {
            gradeStr = " AND gradeNum={grade} ";
        }
        String sql = "SELECT DISTINCT cla.studentType num ,d.name name  FROM his_classlevel cl inner join  (SELECT DISTINCT classNum FROM userposition WHERE userNum={uid}  " + gradeStr + "   AND subjectNum!='999'   UNION ALL SELECT DISTINCT classNum FROM his_userposition_record WHERE userNum={uid}  " + gradeStr + "   AND subjectNum!='999' AND examNum={exam} ) u on u.classNum = cl.classNum LEFT JOIN class cla ON u.classNum=cla.id LEFT JOIN `data` d ON d.type='25' AND d.`value`=cla.studentType WHERE cl.examNum = {exam} and cl.gradeNum = {grade} and cla.id is not null";
        Map args = new HashMap();
        args.put("grade", grade);
        args.put("uid", uid);
        args.put("exam", exam);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getClassPer_F(String exam, String subject, String school, String grade, String type, String uid, String levelclass, String islevel, String subCompose) {
        if (null != subject && (subject.length() < 3 || subject.equals("-1"))) {
            subject = null;
        }
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("uid", uid);
        args.put("subject", subject);
        args.put("type", type);
        args.put(License.SCHOOL, school);
        args.put("subCompose", subCompose);
        if (school.indexOf(44) != -1) {
            return new ArrayList();
        }
        if (null != levelclass && levelclass.equals("T") && subject != null) {
            String sql = "SELECT  DISTINCT cla.id num,cla.className name  FROM classlevel cl inner join (SELECT DISTINCT classNum FROM userposition_record WHERE examNum={exam} and gradeNum={grade}  AND userNum={uid} AND subjectNum={subject}  UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={grade} AND userNum={uid} AND subjectNum={subject} ) u on u.classNum = cl.classNum LEFT JOIN levelclass cla ON u.classNum=cla.id  AND cla.studentType={type} AND cla.subjectNum={subject}  WHERE cl.examNum={exam} and cl.schoolNum={school} and cl.gradeNum={grade} and cl.subjectNum={subject} and cl.xuankezuhe={subCompose} and cla.id IS NOT NULL ORDER BY LENGTH(cla.className),convert(cla.className using gbk) ";
            if (islevel != null && islevel.equals("0")) {
                sql = "SELECT  DISTINCT cla.id num,cla.className name  FROM classlevel_fc cl inner join (SELECT DISTINCT classNum FROM userposition_record WHERE examNum={exam} and gradeNum={grade}  AND userNum={uid} AND subjectNum={subject}  UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={grade} AND userNum={uid} AND subjectNum={subject} ) u on u.classNum = cl.classNum LEFT JOIN class cla ON u.classNum=cla.id  AND cla.studentType={type} AND cla.subjectNum={subject}  WHERE cl.examNum={exam} and cl.schoolNum={school} and cl.gradeNum={grade} and cl.subjectNum={subject} cl.xuankezuhe={subCompose} and cla.id IS NOT NULL ORDER BY LENGTH(cla.className),convert(cla.className using gbk) ";
            }
            return this.dao2._queryBeanList(sql, AjaxData.class, args);
        }
        String subjectStr = "";
        if (null != subject && !"".equals(subject)) {
            subjectStr = " and cl.subjectNum={subject} ";
        }
        return this.dao2._queryBeanList("SELECT  DISTINCT cla.id num,cla.className name FROM classlevel cl inner join (SELECT DISTINCT classNum FROM userposition_record WHERE examNum={exam} and gradeNum={grade}   AND userNum={uid} AND (type='2' OR (type='1' AND subjectNum={subject})) UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={grade}   AND userNum={uid} AND (type='2' OR (type='1' AND subjectNum={subject} ))) u on u.classNum = cl.classNum  LEFT JOIN class cla ON u.classNum=cla.id  AND cla.studentType={type}   WHERE cl.examNum={exam} and cl.schoolNum={school} and cl.gradeNum={grade} " + subjectStr + " and cl.xuankezuhe={subCompose} and cla.id IS NOT NULL ORDER BY LENGTH(cla.className),convert(cla.className using gbk) ", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistoryClassPer_F(String exam, String subject, String school, String grade, String type, String uid, String levelclass) {
        if (null != subject && (subject.length() < 3 || subject.equals("-1"))) {
            subject = null;
        }
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("uid", uid);
        args.put("subject", subject);
        args.put("type", type);
        args.put(License.SCHOOL, school);
        if (school.indexOf(44) != -1) {
            return new ArrayList();
        }
        if (null != levelclass && levelclass.equals("T") && subject != null) {
            return this.dao2._queryBeanList("SELECT  DISTINCT cla.id num,cla.className name  FROM his_classlevel cl inner join (SELECT DISTINCT classNum FROM his_userposition_record WHERE examNum={exam} and gradeNum={grade}   AND userNum={uid} AND subjectNum={subject} UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={grade} AND userNum={uid} AND subjectNum={subject} ) u on u.classNum = cl.classNum LEFT JOIN levelclass cla ON u.classNum=cla.id  AND cla.studentType={type} AND subjectNum={subject}  WHERE cl.examNum={exam} and cl.schoolNum={school} and cl.gradeNum={grade} and cl.subjectNum={subject} and cla.id IS NOT NULL ORDER BY LENGTH(cla.classNum),cla.classNum ", AjaxData.class, args);
        }
        if (null == subject) {
        }
        return this.dao2._queryBeanList("SELECT  DISTINCT cla.id num,cla.className name FROM his_classlevel cl inner join (SELECT DISTINCT classNum FROM his_userposition_record WHERE examNum={exam} and gradeNum={grade}  AND userNum={uid} AND (type='2' OR (type='1' AND subjectNum={subject})) UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={grade}  AND userNum={uid} AND (type='2' OR (type='1' AND subjectNum={subject}))) u on u.classNum = cl.classNum  LEFT JOIN class cla ON u.classNum=cla.id  AND cla.studentType={type}   WHERE cl.examNum={exam} and cl.schoolNum={school} and cl.gradeNum={grade} and cl.subjectNum={subject} and cla.id IS NOT NULL ORDER BY LENGTH(cla.classNum),cla.classNum ", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSchoolPer_F(String exam, String subject, String type, String grade, String uid) {
        Map args = StreamMap.create().put("uid", (Object) uid);
        return this.dao2._queryBeanList("SELECT  DISTINCT s.id num,s.shortname name   FROM  (SELECT t.schoolnum from  teacher t LEFT JOIN `user` u ON t.id=u.userid WHERE u.id={uid}) u  LEFT JOIN school s ON u.schoolnum=s.id  where s.id is not null  ORDER BY convert(s.schoolName using gbk)", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySchoolPer_F(String exam, String subject, String type, String grade, String uid) {
        Map args = StreamMap.create().put("uid", (Object) uid);
        return this.dao2._queryBeanList("SELECT  DISTINCT u.schoolNum num,s.shortname name   FROM  (SELECT t.schoolnum from  teacher t LEFT JOIN `user` u ON t.id=u.userid WHERE u.id={uid}) u  LEFT JOIN school s ON u.schoolnum=s.id  where u.schoolNum is not null  ORDER BY convert(s.schoolName using gbk)", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public String[] gettype(String uid) {
        Map args = StreamMap.create().put("uid", (Object) uid);
        String types = this.dao2._queryStr("SELECT GROUP_CONCAT(type) FROM userposition WHERE userNum = {uid} ", args);
        if (StrUtil.isEmpty(types)) {
            return null;
        }
        return types.split(Const.STRING_SEPERATOR);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public String authDefineAnswer(String epNum) {
        if (null == epNum || epNum.equals("")) {
            return null;
        }
        Map args = StreamMap.create().put("epNum", (Object) epNum);
        return this.dao2._queryStr("select GROUP_CONCAT(questionNum order by questionNum) from(SELECT questionNum FROM define     WHERE examPaperNum = {epNum}  AND questionType='0' AND answer=''  and choosename!='T' and isParent!=1 UNION ALL SELECT questionNum FROM subdefine  WHERE examPaperNum = {epNum}  AND questionType='0' AND answer='') r ", args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public Define authStudentAnswer(String epNum, String exam, String grade, String subject) {
        if (null == epNum || epNum.equals("")) {
            return null;
        }
        Map args = new HashMap();
        args.put("epNum", epNum);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        Integer schNum = this.dao2._queryInt(" SELECT COUNT( DISTINCT schoolNum) FROM examinationnum WHERE examNum = {exam} and gradeNum = {grade} and subjectNum = {subject} ", args);
        if (schNum.intValue() == 1) {
            return (Define) this.dao2._queryBean("SELECT GROUP_CONCAT( DISTINCT d.questionNum ORDER BY d.questionNum) questionNum,CASE  WHEN d.questionNum is NULL THEN NULL  WHEN c.para is NULL THEN 0.3  ELSE c.para  END  errorRate  FROM gradelevel_objitem go LEFT JOIN  gradelevel g ON go.examPaperNum={epNum}  AND g.examPaperNum ={epNum}  AND g.statisticType='0' AND g.source='0' AND g.xuankezuhe='0' AND g.studentType=go.studentType AND go.type='0' LEFT JOIN  config c ON c.type='7' LEFT JOIN ( SELECT id,questionNum FROM define WHERE examPaperNum={epNum}  AND questionType='0' UNION ALL SELECT id,questionNum FROM subdefine WHERE examPaperNum={epNum}  AND questionType='0' ) d ON go.questionNum = d.id WHERE go.statisticType='0' AND go.source='0' AND go.xuankezuhe='0' AND  go.examPaperNum = g.examPaperNum and go.answer = ''  AND go.numOfStudent/g.numOfStudent>IFNULL(c.para,0.3)", Define.class, args);
        }
        return (Define) this.dao2._queryBean("SELECT GROUP_CONCAT( DISTINCT d.questionNum ORDER BY d.questionNum) questionNum,CASE  WHEN d.questionNum is NULL THEN NULL  WHEN c.para is NULL THEN 0.3  ELSE c.para  END  errorRate  FROM arealevel_objitem go LEFT JOIN  arealevel g ON go.examPaperNum={epNum}  AND g.examPaperNum ={epNum} AND g.statisticType='0' AND g.source='0' AND g.xuankezuhe='0' AND g.studentType=go.studentType AND go.type='0' LEFT JOIN  config c ON c.type='7' LEFT JOIN ( SELECT id,questionNum FROM define WHERE examPaperNum={epNum} AND questionType='0' UNION ALL SELECT id,questionNum FROM subdefine WHERE examPaperNum={epNum} AND questionType='0' ) d ON go.questionNum = d.id WHERE go.statisticType='0' AND go.source='0' AND go.xuankezuhe='0' AND go.examPaperNum = g.examPaperNum and go.answer = ''  AND go.numOfStudent/g.numOfStudent>IFNULL(c.para,0.3)", Define.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List authQuestionAVGScore(String epNum, String exam, String grade, String subject) {
        if (null == epNum || epNum.equals("")) {
            return null;
        }
        Map args = new HashMap();
        args.put("epNum", epNum);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        Integer schNum = this.dao2._queryInt(" SELECT COUNT( DISTINCT schoolNum) FROM examinationnum WHERE examNum = {exam} and gradeNum = {grade} and subjectNum = {subject} ", args);
        if (schNum.intValue() == 1) {
            return this.dao2._queryBeanList("SELECT d.questionType,GROUP_CONCAT(DISTINCT d.questionNum ORDER BY d.questionNum) questionNum FROM gradelevel_question g LEFT JOIN  ( SELECT id,questionNum,questionType FROM define WHERE examPaperNum={epNum} UNION ALL SELECT id,questionNum,questionType FROM subdefine WHERE  examPaperNum={epNum} )d ON g.examPaperNum={epNum} AND d.id = g.questionNum AND g.statisticType='0' AND g.source='0' AND g.xuankezuhe='0' WHERE g.examPaperNum={epNum} AND  d.id = g.questionNum  AND g.average = 0 GROUP BY d.questionType ", Define.class, args);
        }
        return this.dao2._queryBeanList("SELECT d.questionType,GROUP_CONCAT(DISTINCT d.questionNum ORDER BY d.questionNum) questionNum FROM arealevel_question g LEFT JOIN  ( SELECT id,questionNum,questionType FROM define WHERE examPaperNum={epNum} UNION ALL SELECT id,questionNum,questionType FROM subdefine WHERE  examPaperNum={epNum} )d ON g.examPaperNum={epNum} AND d.id = g.questionNum AND g.statisticType='0' AND g.source='0' AND g.xuankezuhe='0' WHERE g.examPaperNum={epNum}   AND  d.id = g.questionNum  AND g.average = 0 GROUP BY d.questionType ", Define.class, args);
    }

    public void deleteZeroData(Integer exam, Integer subject, Integer grade) {
        String exampString = this.examService.getExampaperNum(String.valueOf(exam), String.valueOf(subject), String.valueOf(grade));
        if (null == exampString || exampString.equals("")) {
            return;
        }
        Map args = new HashMap();
        args.put("exampString", exampString);
        try {
            Integer.valueOf(this.dao2._execute(" UPDATE illegal i  LEFT JOIN studentlevel s ON i.examPaperNum={exampString} AND s.examPaperNum={exampString} AND i.studentId=s.studentId AND s.totalScore=0  SET i.type = '0',score='0'  WHERE i.examPaperNum=s.examPaperNum AND s.studentId = i.studentId", args));
            List<Exampaper> list = this.examService.getExampaperNum_threeSjt(String.valueOf(exam), String.valueOf(subject), String.valueOf(grade), exampString);
            if (null == list) {
                list = new ArrayList();
            }
            Exampaper epExampaper = new Exampaper();
            epExampaper.setExamPaperNum(Integer.valueOf(Integer.parseInt(exampString)));
            list.add(epExampaper);
            for (int j = 0; j < list.size(); j++) {
                int epNum = list.get(j).getExamPaperNum().intValue();
                args.put("epNum", Integer.valueOf(epNum));
                List rList = this.dao2._queryColList("select id from studentlevel where examPaperNum={epNum} AND totalscore=0 ", args);
                if (null != rList && rList.size() > 0) {
                    for (int i = 0; i < rList.size(); i++) {
                        args.put("rList", rList.get(i));
                        this.dao2._execute("delete from studentlevel where id = {rList} ", args);
                    }
                }
            }
        } catch (Exception e) {
            this.log.error("0分不参与计算，删除0分数据报错  exampaperNum:" + exampString);
            e.printStackTrace();
        }
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public String getAllSchoolCounts(String exam, String grade, String userId) {
        String flag = "T";
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("userId", userId);
        if (!userId.equals("-1") && !userId.equals("-2")) {
            List<Map<String, Object>> list1 = this.dao2._queryMapList("select distinct schoolNum from gradelevel where examNum={exam} and gradeNum={grade} ", null, args);
            List<Map<String, Object>> list2 = this.dao2._queryMapList("select schoolNum from schauthormanage where userId={userId} union select schoolNum from user where id={userId} ", null, args);
            String schoolNums = "";
            for (Map<String, Object> map : list2) {
                schoolNums = schoolNums + map.get(Const.EXPORTREPORT_schoolNum) + Const.STRING_SEPERATOR;
            }
            for (Map<String, Object> map2 : list1) {
                if (schoolNums.indexOf(map2.get(Const.EXPORTREPORT_schoolNum) + "") < 0) {
                    flag = "F";
                }
            }
        }
        return flag;
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSubCompose(String exam, String grade, String subjectType, String onlyShowShouxuan) {
        String gradeStr = "";
        if (grade != null && !grade.equals("")) {
            gradeStr = "and al.gradeNum={grade} ";
        }
        String subjectTypeStr = "";
        if (subjectType != null && !subjectType.equals("")) {
            subjectTypeStr = "and al.studentType={subjectType} ";
        }
        String shouxuanStr = "true".equals(onlyShowShouxuan) ? " and sc.isParent='1' " : "";
        String sql = "select distinct al.xuankezuhe num,sc.subjectCombineName name,ifnull(sc.isParent,0) ext1 from arealevel al inner join subjectcombine sc on al.xuankezuhe = sc.subjectCombineNum where al.examNum={exam} " + gradeStr + subjectTypeStr + shouxuanStr + " order by sc.orderNum  ";
        Map args = new HashMap();
        args.put("grade", grade);
        args.put("subjectType", subjectType);
        args.put("exam", exam);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getIslevel(String exam, String grade) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        String jie = this.dao2._queryStr("select jie from exampaper where examNum={exam} and gradeNUm={grade} limit 0,1", args);
        args.put("jie", jie);
        String jieStr = "";
        if (!StrUtil.isBlankOrUndefined(jie)) {
            jieStr = " and jie={jie} ";
        }
        String sql = "select distinct islevel ext1 from grade where  gradeNum={grade} " + jieStr;
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public String getReportIsFufen(String exam, String grade, String subject) {
        String subStr = (null == subject || "".equals(subject)) ? "" : " and subjectNum = {subject} ";
        String sql = "select id from arealevel_fufen where examNum = {exam} and gradeNum = {grade} " + subStr + "limit 1";
        Map args = new HashMap();
        args.put("subject", subject);
        args.put("exam", exam);
        args.put("grade", grade);
        return null == this.dao2._queryObject(sql, args) ? "F" : "T";
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getReportComparativeExam(String exam, String grade, String subjectType, String subject, String teachUnit, String teachUnitStatistic) {
        Map obj = this.dao2.querySimpleMap("select distinct eg.examtypeGroupNum,ifnull(eg.maxNumber,10) maxNumber from examtypegroup eg left join exam e on e.examType=eg.examtypeNum where e.isDelete='F' ");
        if (null == obj) {
            return null;
        }
        Map args = new HashMap();
        args.put("teachUnit", teachUnit);
        args.put("subject", subject);
        args.put("subjectType", subjectType);
        args.put("exam", exam);
        args.put("grade", grade);
        String tableStr = " statisticlevel ";
        String nameStr = " statisticId ";
        if ("01".equals(teachUnitStatistic)) {
            tableStr = " gradelevel ";
            nameStr = " schoolNum ";
        }
        String teachUnitStr = ("02".equals(teachUnitStatistic) || null == teachUnit || "".equals(teachUnit) || "null".equals(teachUnit)) ? "" : " and" + nameStr + "={teachUnit} ";
        String subjectStr = (null == subject || "".equals(subject) || "null".equals(subject)) ? "" : " and subjectNum={subject} ";
        String studentTypeStr = (null == subjectType || "".equals(subjectType) || "null".equals(subjectType)) ? "" : " and studentType={subjectType} ";
        Object jieObj = this.dao2._queryObject("select jie from exampaper where examNum={exam} and gradeNum={grade} limit 1", args);
        String jieStr = null == jieObj ? "" : " and jie={jieObj} ";
        args.put("jieObj", jieObj.toString());
        Object stage = this.dao2._queryObject("select stage from basegrade where gradeNum={grade}", args);
        args.put("stage", stage);
        String sql = "select DISTINCT e.examNum num,e.examName name from examtypegroup eg inner join exam e on eg.examtypeNum=e.examType inner join (select DISTINCT examNum,gradeNum from " + tableStr + " where xuankezuhe='0' " + teachUnitStr + subjectStr + studentTypeStr + jieStr + ") gl on e.examNum = gl.examNum inner join basegrade bg on bg.gradeNum=gl.gradeNum and stage={stage} where e.isDelete='F' and e.examNum!={exam} AND DATE(e.examDate)<=(SELECT examDate FROM exam WHERE examNum={exam} ) order by e.examDate desc limit 0," + obj.get("maxNumber");
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<Grade> getGradeByStage(String stage) {
        Map args = StreamMap.create().put("stage", (Object) stage);
        return this.dao2._queryBeanList("SELECT gradeNum,gradeName from basegrade where stage={stage} order by id asc", Grade.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<Map<String, Object>> getexportData(String examNum, String userId) {
        String str;
        List resultlist = new ArrayList();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        List<Map<String, Object>> gradelist = this.dao2._queryMapList("SELECT DISTINCT b.gradeNum,b.gradeName from exampaper ep LEFT JOIN basegrade b ON ep.gradeNum=b.gradeNum WHERE ep.examNum={examNum}", null, args);
        for (int i = 0; i < gradelist.size(); i++) {
            HashMap hashMap = new HashMap();
            String gradeNum = gradelist.get(i).get(Const.EXPORTREPORT_gradeNum).toString();
            String gradeName = gradelist.get(i).get("gradeName").toString();
            hashMap.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            hashMap.put("gradeName", gradeName);
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            if (!userId.equals("-1") && !userId.equals("-2")) {
                str = "select * from (SELECT s.subjectNum,s.subjectName,IF(m.status is null,-1,m.status) subjectStatus,IF(m.status =9,m.insertDate,'') subjectTime,m.insertuser subjectuser, IF(m1.status is null,-1,m1.status)questionStatus,IF(m1.status =9,m1.insertDate,'') questionTime,m1.insertuser questionuser,s.orderNum from exampaper ep  LEFT JOIN exam e ON ep.examNum=e.examNum LEFT JOIN `subject` s ON s.subjectNum=ep.subjectNum LEFT JOIN astrict a on ep.examNum=a.examNum and ep.gradeNum=a.gradeNum LEFT JOIN managefile m ON m.examNum=ep.examNum AND m.gradeNum=ep.gradeNum AND m.subjectNum=s.subjectNum AND m.resourceType=0 LEFT JOIN managefile m1 ON m1.examNum=ep.examNum AND m1.gradeNum=ep.gradeNum AND m1.subjectNum=s.subjectNum AND m1.resourceType=2 WHERE ep.examNum={examNum} and ep.gradeNum={gradeNum} AND  a.partType=5  and a.userType=1 and a.`status`=1 union select s.subjectNum,s.subjectName ,IF(m.status is null,-1,m.status) subjectStatus,IF(m.status =9,m.insertDate,'') subjectTime,m.insertuser subjectuser,'','','',999 orderNum from (select '-8' subjectNum,'全科目' subjectName)s left join managefile m on 1=1  and m.examNum={examNum} and m.gradeNum={gradeNum} and m.subjectNum='-8' and m.resourceType=1 )aa order by orderNum ";
            } else {
                str = "select * from (SELECT s.subjectNum,s.subjectName,IF(m.status is null,-1,m.status) subjectStatus,IF(m.status =9,m.insertDate,'') subjectTime,m.insertuser subjectuser,IF(m1.status is null,-1,m1.status) questionStatus,IF(m1.status =9,m1.insertDate,'') questionTime,m1.insertuser questionuser,s.orderNum from exampaper ep  LEFT JOIN exam e ON ep.examNum=e.examNum LEFT JOIN `subject` s ON s.subjectNum=ep.subjectNum LEFT JOIN managefile m ON m.examNum=ep.examNum AND m.gradeNum=ep.gradeNum AND m.subjectNum=s.subjectNum AND m.resourceType=0 LEFT JOIN managefile m1 ON m1.examNum=ep.examNum AND m1.gradeNum=ep.gradeNum AND m1.subjectNum=s.subjectNum AND m1.resourceType=2 WHERE ep.examNum={examNum} and ep.gradeNum={gradeNum} union select s.subjectNum,s.subjectName ,IF(m.status is null,-1,m.status) subjectStatus,IF(m.status =9,m.insertDate,'') subjectTime,m.insertuser subjectuser,'','','',999 orderNum from (select '-8' subjectNum,'全科目' subjectName)s left join managefile m on 1=1 and m.examNum={examNum} and m.gradeNum={gradeNum} and m.subjectNum='-8' and m.resourceType=1 )aa order by orderNum  ";
            }
            String sql = str;
            List subjectslist = this.dao2._queryMapList(sql, null, args);
            hashMap.put("subjects", subjectslist);
            resultlist.add(hashMap);
        }
        return resultlist;
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<Map<String, Object>> getpiciData(String examNum, String gradeNum, String subCompose) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("subCompose", subCompose);
        return this.dao2._queryMapList("SELECT o.type num,d.name from onlineindicator o  LEFT JOIN `data` d ON d.type='21' AND d.`value`=o.type  WHERE o.examNum={examNum} AND o.gradeNum={gradeNum} AND o.xuankezuhe={subCompose} GROUP BY o.type", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySubject1(String exam, String school, String grade, String type, String showZSubject, String subjectNumm) {
        String sql2;
        String sql = "SELECT  DISTINCT g.subjectNum num,g.subjectName name  FROM (SELECT ep.examPaperNum,ep.gradeNum,ep.jie,ep.subjectNum FROM (SELECT examPaperNum,subjectNum,gradeNum,jie FROM exampaper WHERE examNum={exam} and gradeNum={grade}) ep ";
        String str = sql + ") r  LEFT JOIN classexam c ON c.examPaperNum=r.examPaperNum AND c.schoolNum={school}  LEFT JOIN (select id,classNum,schoolNum,jie from class cla where cla.gradeNum={grade} and cla.studentType={type} AND cla.schoolNum={school} ) cla ON c.classNum=cla.id  LEFT JOIN subject g ON g.subjectNum = r.subjectNum  RIGHT JOIN  (SELECT subjectNum from jisuanzhonglei WHERE  gradeNum={grade} AND jisuanzhonglei='1' AND (qufen='1' OR qufen='5') GROUP BY subjectNum) js ON g.subjectNum=js.subjectNum where c.schoolNum is not null  and cla.classNum is not null order by g.orderNum";
        String showZSubjectString = "";
        if (null != showZSubject && !showZSubject.equals("true")) {
            showZSubjectString = " and issub != '0' ";
        }
        if (subjectNumm != null || !subjectNumm.equals("null")) {
            sql2 = "SELECT g.subjectNum num ,sjt.subjectName name   FROM  (SELECT DISTINCT subjectNum FROM his_gradelevel WHERE examNum={exam} AND gradeNum={grade} AND studentType={type} AND statisticType='0' and subjectNum ={subjectNumm} and source='0' " + showZSubjectString + " ) g   LEFT JOIN `subject` sjt ON g.subjectNum=sjt.subjectNum  where g.subjectNum!={subject_totalScore} order by sjt.orderNum";
        } else {
            sql2 = "SELECT g.subjectNum num ,sjt.subjectName name   FROM  (SELECT DISTINCT subjectNum FROM his_gradelevel WHERE examNum={exam} AND gradeNum={grade} AND studentType={type} AND statisticType='0' and source='0' " + showZSubjectString + " ) g   LEFT JOIN `subject` sjt ON g.subjectNum=sjt.subjectNum  RIGHT JOIN  (SELECT subjectNum from jisuanzhonglei WHERE  gradeNum={grade} AND jisuanzhonglei='1' AND (qufen='1' OR qufen='5') GROUP BY subjectNum) js ON g.subjectNum=js.subjectNum  where g.subjectNum!={subject_totalScore}  order by sjt.orderNum";
        }
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(License.SCHOOL, school);
        args.put("type", type);
        args.put("subjectNumm", subjectNumm);
        args.put("subject_totalScore", "-1");
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySubjectPer_F1(String exam, String school, String grade, String type, String uid) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade).put("uid", (Object) uid);
        return this.dao2._queryBeanList("SELECT DISTINCT al.subjectNum num,s.subjectName name FROM (select DISTINCT subjectNum from his_arealevel where examNum = {exam} and gradeNum = {grade} ) al inner join (SELECT DISTINCT subjectNum FROM userposition WHERE  userNum={uid} AND subjectNum!='999' UNION ALL SELECT DISTINCT subjectNum FROM his_userposition_record WHERE examNum={exam} AND userNum={uid} AND subjectNum!='999' )u on u.subjectNum = al.subjectNum LEFT JOIN `subject` s ON s.subjectNum=u.subjectNum  RIGHT JOIN  (SELECT subjectNum from jisuanzhonglei WHERE  gradeNum={grade} AND jisuanzhonglei='1' AND (qufen='1' OR qufen='5') GROUP BY subjectNum) js ON s.subjectNum=js.subjectNum  where s.subjectNum is not null", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getHistorySubject_Student1(String exam, String type, String studentId, String subjectNum) {
        String sql;
        if (subjectNum != null) {
            sql = "SELECT DISTINCT s.subjectNum num,e.subjectName name FROM his_studentlevel s LEFT JOIN  subject e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.subjectNum=s.subjectNum  RIGHT JOIN  (SELECT subjectNum from jisuanzhonglei WHERE  gradeNum={grade} AND jisuanzhonglei='1' AND (qufen='1' OR qufen='5') GROUP BY subjectNum) js ON e.subjectNum=js.subjectNum  WHERE s.studentId={studentId} AND  s.examNum = {exam} and e.isHidden='F' and s.subjectNum={subjectNum} ";
        } else {
            sql = "SELECT DISTINCT s.subjectNum num,e.subjectName name FROM his_studentlevel s LEFT JOIN  subject e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.subjectNum=s.subjectNum  RIGHT JOIN  (SELECT subjectNum from jisuanzhonglei WHERE  gradeNum={grade} AND jisuanzhonglei='1' AND (qufen='1' OR qufen='5') GROUP BY subjectNum) js ON e.subjectNum=js.subjectNum  WHERE s.studentId={studentId} AND  s.examNum = {exam} and e.isHidden='F'";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSubject1(String exam, String school, String grade, String type, String showZSubject, String fufen, String subCompose) {
        String subComposeStr;
        String sql = "SELECT  DISTINCT g.subjectNum num,g.subjectName name  FROM (SELECT ep.examPaperNum,ep.gradeNum,ep.jie,ep.subjectNum FROM (SELECT examPaperNum,subjectNum,gradeNum,jie FROM exampaper WHERE examNum={exam} and gradeNum={grade} ) ep ";
        String str = sql + ") r  LEFT JOIN classexam c ON c.examPaperNum=r.examPaperNum  LEFT JOIN (select id,classNum,schoolNum,jie from class cla where cla.gradeNum={grade} and cla.studentType={type} ) cla ON c.classNum=cla.id  LEFT JOIN subject g ON g.subjectNum = r.subjectNum  RIGHT JOIN  (SELECT subjectNum from jisuanzhonglei WHERE  gradeNum={grade} AND jisuanzhonglei='1' AND (qufen='1' OR qufen='5') GROUP BY subjectNum) js ON g.subjectNum=js.subjectNum  where c.schoolNum is not null  and cla.classNum is not null order by g.orderNum";
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("type", type);
        args.put("subCompose", subCompose);
        String showZSubjectString = "";
        if (null != showZSubject && !showZSubject.equals("true")) {
            showZSubjectString = " and issub != '0' ";
        }
        String tableName = "0".equals(fufen) ? "gradelevel" : "gradelevel_fufen";
        if (subCompose != null && !subCompose.equals("")) {
            subComposeStr = " and xuankezuhe={subCompose} ";
        } else {
            subComposeStr = " and xuankezuhe='0' ";
        }
        String sql2 = "SELECT g.subjectNum num ,sjt.subjectName name   FROM  (SELECT DISTINCT subjectNum FROM " + tableName + " WHERE examNum={exam} AND gradeNum={grade} AND studentType={type}  AND statisticType='0'  " + subComposeStr + " " + showZSubjectString + " ) g   LEFT JOIN `subject` sjt ON g.subjectNum=sjt.subjectNum  RIGHT JOIN  (SELECT subjectNum from jisuanzhonglei WHERE  gradeNum={grade} AND jisuanzhonglei='1' AND (qufen='1' OR qufen='5') GROUP BY subjectNum) js ON g.subjectNum=js.subjectNum   where g.subjectNum!={subject_totalScore} order by sjt.orderNum ";
        args.put("subject_totalScore", "-1");
        return this.dao2._queryBeanList(sql2, AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSubjectPer_F1(String exam, String school, String grade, String type, String uid, String subCompose) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subCompose", subCompose);
        args.put("uid", uid);
        return this.dao2._queryBeanList("SELECT DISTINCT al.subjectNum num,s.subjectName name FROM (select DISTINCT subjectNum from arealevel where examNum = {exam} and gradeNum = {grade} and xuankezuhe= {subCompose}) al inner join (SELECT DISTINCT subjectNum FROM userposition WHERE  userNum={uid} and gradeNum={grade} AND subjectNum!='999' UNION ALL SELECT DISTINCT subjectNum FROM userposition_record WHERE examNum={exam} AND userNum={uid} and gradeNum={grade} AND subjectNum!='999' )u on u.subjectNum = al.subjectNum LEFT JOIN `subject` s ON s.subjectNum=u.subjectNum  RIGHT JOIN  (SELECT subjectNum from jisuanzhonglei WHERE  gradeNum={grade} AND jisuanzhonglei='1' AND (qufen='1' OR qufen='5') GROUP BY subjectNum) js ON s.subjectNum=js.subjectNum  where s.subjectNum is not null", AjaxData.class, args);
    }

    @Override // com.dmj.service.analysisManagement.AnalysisService
    public List<AjaxData> getSubject_Student1(String exam, String type, String studentId, String SubjectNum, String subCompose) {
        String subComposeStr;
        String sql;
        if (subCompose != null && !subCompose.equals("")) {
            subComposeStr = " and s.xuankezuhe={subCompose} ";
        } else {
            subComposeStr = " and s.xuankezuhe=0";
        }
        if (SubjectNum != null) {
            sql = "SELECT DISTINCT s.subjectNum num,e.subjectName name FROM studentlevel s LEFT JOIN  subject e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.subjectNum=s.subjectNum WHERE s.studentId={studentId} AND  s.examNum = {exam} and e.isHidden='F' and s.subjectNum ={SubjectNum} " + subComposeStr;
        } else {
            sql = "SELECT DISTINCT s.subjectNum num,e.subjectName name FROM studentlevel s LEFT JOIN  subject e ON  s.studentId = {studentId}  AND s.examNum = {exam} AND e.subjectNum=s.subjectNum  RIGHT JOIN  (SELECT subjectNum from jisuanzhonglei WHERE  gradeNum={grade} AND jisuanzhonglei='1' AND (qufen='1' OR qufen='5') GROUP BY subjectNum) js ON e.subjectNum=js.subjectNum  WHERE s.studentId={studentId} AND  s.examNum = {exam} and e.isHidden='F'" + subComposeStr;
        }
        Map args = new HashMap();
        args.put("subCompose", subCompose);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("exam", exam);
        args.put("SubjectNum", SubjectNum);
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }
}
