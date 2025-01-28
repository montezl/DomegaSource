package com.dmj.serviceimpl.examManagement;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.awardPoint.AwardPointDaoImpl;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.examManagement.ExamDAOImpl;
import com.dmj.daoimpl.examManagement.ExamManageDAOImpl;
import com.dmj.daoimpl.teacherApp.TeacherAppScoreDaoImpl;
import com.dmj.domain.Abilitydetail;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Averagescore;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.Class;
import com.dmj.domain.Classexam;
import com.dmj.domain.CorrectInfo;
import com.dmj.domain.Define;
import com.dmj.domain.Exam;
import com.dmj.domain.ExamData;
import com.dmj.domain.Examinationnum;
import com.dmj.domain.Examinationnumimg;
import com.dmj.domain.ExamineeStuRecord;
import com.dmj.domain.Examlog;
import com.dmj.domain.Exampaper;
import com.dmj.domain.GeneralCorrectData;
import com.dmj.domain.Knowdetail;
import com.dmj.domain.MarkError;
import com.dmj.domain.QuestionType;
import com.dmj.domain.Questiontypedetail;
import com.dmj.domain.RegExaminee;
import com.dmj.domain.Reg_Th_Log;
import com.dmj.domain.Remark;
import com.dmj.domain.Score;
import com.dmj.domain.Student;
import com.dmj.domain.Studentpaperimage;
import com.dmj.domain.Subject;
import com.dmj.domain.Task;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.domain.vo.QuestionGroup_question;
import com.dmj.domain.vo.Questiongroup_mark_setting;
import com.dmj.service.examManagement.ExamService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.service.test.StestService;
import com.dmj.serviceimpl.awardPoint.AwardPointServiceImpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.serviceimpl.test.StestServiceimpl;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.ImageUtil;
import com.dmj.util.ItemThresholdRegUtil;
import com.dmj.util.Util;
import com.zht.db.RowArg;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;

/* loaded from: ExamServiceImpl.class */
public class ExamServiceImpl implements ExamService {
    private ExamDAOImpl dao = new ExamDAOImpl();
    ExamManageDAOImpl mdao = new ExamManageDAOImpl();
    private SystemService systemService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    private StestService stestService = (StestService) ServiceFactory.getObject(new StestServiceimpl());
    TeacherAppScoreDaoImpl teacherAppScoreDao = new TeacherAppScoreDaoImpl();
    AwardPointDaoImpl awardPointDao = new AwardPointDaoImpl();
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());

    @Override // com.dmj.service.examManagement.ExamService
    public Integer deleteOneByNum(String colum, String valule, Class cla) {
        return this.dao.deleteOneByNum(colum, valule, cla);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getOneByNum(String colum, String valule, Class cla) {
        return this.dao.getOneByNum(colum, valule, cla);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getExampaperNumBySubjectAndGradeAndExam(String examNum, String subjectNum, String gradeNum) {
        return this.dao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String judgeYouOrWang(String examNum, String subjectNum, String gradeNum) {
        return this.dao.judgeYouOrWang(examNum, subjectNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String checkIfHaveCalculate(String examNum, String subjectNum, String gradeNum) {
        return this.dao.checkIfHaveCalculate(examNum, subjectNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getExampaperNum(String examNum, String subjectNum, String gradeNum) {
        return this.dao.getExampaperNum(examNum, subjectNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getpExampaperNum(String examNum, String subjectNum, String gradeNum) {
        return this.dao.getpExampaperNum(examNum, subjectNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Exampaper getExampaper(String examNum, String subjectNum, String gradeNum) {
        return this.dao.getExampaper(examNum, subjectNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Exampaper> getExampaperNum_threeSjt(String examNum, String subjectNum, String gradeNum, String pexamPaperNum) {
        return this.dao.getExampaperNum_threeSjt(examNum, subjectNum, gradeNum, pexamPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Subject> getSubjectName(String subjectNum) {
        return this.dao.getSubjectName(subjectNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer save(List<Define> defines, String Pnum, List<Knowdetail> knows) throws Exception {
        if (null != defines && defines.size() > 0) {
            this.dao2.batchSave(defines);
        }
        if (null != knows && knows.size() > 0) {
            this.dao2.batchSave(knows);
        }
        saveA(Pnum);
        saveCat(Pnum);
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer save_Abilitydetail(List<Define> defines, String Pnum, List<Abilitydetail> abilitys) throws Exception {
        if (null != defines && defines.size() > 0) {
            this.dao2.batchSave(defines);
        }
        if (null != abilitys && abilitys.size() > 0) {
            this.dao2.batchSave(abilitys);
        }
        saveA(Pnum);
        saveCat(Pnum);
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer save_know_ability(List<Define> defines, String Pnum, List<Knowdetail> knows, List<Abilitydetail> abilitys) throws Exception {
        if (null != defines && defines.size() > 0) {
            this.dao2.batchSave(defines);
        }
        if (null != knows && knows.size() > 0) {
            this.dao2.batchSave(knows);
        }
        if (null != abilitys && abilitys.size() > 0) {
            this.dao2.batchSave(abilitys);
        }
        saveA(Pnum);
        saveCat(Pnum);
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer save_know_ability_questiontype(List<Define> defines, String Pnum, List<Knowdetail> knows, List<Abilitydetail> abilitys, List<Questiontypedetail> questiondetail) throws Exception {
        if (null != defines && defines.size() > 0) {
            this.dao2.batchSave(defines);
        }
        if (null != knows && knows.size() > 0) {
            this.dao2.batchSave(knows);
        }
        if (null != abilitys && abilitys.size() > 0) {
            this.dao2.batchSave(abilitys);
        }
        if (null != questiondetail && questiondetail.size() > 0) {
            this.dao2.batchSave(questiondetail);
        }
        saveA(Pnum);
        saveCat(Pnum);
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer save_all(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        this.dao2._execute("UPDATE exampaper  SET totalScore ='0' WHERE examPaperNum={examPaperNum} ", args);
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer saveA(String Pnum) {
        try {
            Float fullscore = Float.valueOf(0.0f);
            Map args = StreamMap.create().put("Pnum", (Object) Pnum);
            List li2 = this.dao2._queryBeanList("SELECT  SUM(def.fullScore) fullScore FROM (SELECT id,fullScore ,questionType,questionNum,category,orderNum,choosename,isParent FROM define WHERE examPaperNum = {Pnum} AND choosename='s'   UNION   SELECT id,fullScore ,questionType,questionNum,category,orderNum,choosename,isParent FROM define WHERE examPaperNum = {Pnum} AND choosename ='T'   AND category<>'-1'   ) def", Define.class, args);
            Float fullscore2 = Float.valueOf(fullscore.floatValue() + ((Define) li2.get(0)).getFullScore().floatValue());
            List li3 = this.dao2._queryBeanList("SELECT  id,fullScore ,questionType,questionNum,category,orderNum,choosename,isParent FROM define WHERE examPaperNum = {Pnum} AND choosename ='T'  AND category='-1'  ", Define.class, args);
            if (null != li3 && li3.size() > 0) {
                for (int i = 0; i < li3.size(); i++) {
                    Map args1 = new HashMap();
                    args1.put("li3Id", ((Define) li3.get(i)).getId());
                    List li4 = this.dao2._queryBeanList("SELECT  SUM(def.fullScore) fullScore FROM (SELECT id,fullScore ,questionType,questionNum,category,orderNum,choosename,isParent FROM define WHERE choosename={li3Id}  ) def", Define.class, args1);
                    fullscore2 = Float.valueOf(fullscore2.floatValue() + ((Define) li4.get(0)).getFullScore().floatValue());
                }
            }
            Map args2 = new HashMap();
            args2.put("fullscore", fullscore2);
            args2.put("Pnum", Pnum);
            this.dao2._execute("update exampaper e SET totalScore ={fullscore} WHERE examPaperNum={Pnum} ", args2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer saveCat(String Pnum) throws Exception {
        Float su;
        Float su2;
        Float valueOf;
        Float su3;
        Float valueOf2;
        List<RowArg> rowArgList = new ArrayList<>();
        Map args = StreamMap.create().put("Pnum", (Object) Pnum);
        List sql_li = this.dao2._queryBeanList("SELECT  DISTINCT def.category category FROM ((SELECT fullScore ,questionType,questionNum,category FROM define WHERE examPaperNum = {Pnum} AND choosename='s' )UNION (SELECT fullScore ,questionType,questionNum,category FROM define WHERE examPaperNum =  {Pnum}   AND choosename ='T')) def order by if(def.category= {Pnum}  ,0,1)", Define.class, args);
        if (sql_li.size() > 1) {
            Float pFull = Float.valueOf(0.0f);
            for (int i = 0; i < sql_li.size(); i++) {
                Map args1 = new HashMap();
                args1.put("Pnum", Pnum);
                args1.put("Category", ((Define) sql_li.get(i)).getCategory());
                args1.put("Id", ((Define) sql_li.get(i)).getId());
                Float full = Float.valueOf(0.0f);
                if (((Define) sql_li.get(i)).getCategory().intValue() != -1) {
                    Float.valueOf(0.0f);
                    List li2 = this.dao2._queryBeanList("SELECT  SUM(def.fullScore) fullScore FROM (SELECT id,fullScore,questionnum,chooseName,category FROM define WHERE examPaperNum= {Pnum}    AND choosename='T'  AND category={Category}    )def", Define.class, args1);
                    if (null != li2 && li2.size() > 0 && null != ((Define) li2.get(0)).getFullScore() && !((Define) li2.get(0)).getFullScore().equals("")) {
                        su = ((Define) li2.get(0)).getFullScore();
                    } else {
                        su = Float.valueOf(0.0f);
                    }
                    Float full2 = Float.valueOf(full.floatValue() + su.floatValue());
                    List li3 = this.dao2._queryBeanList("SELECT  id,fullScore,questionnum,chooseName,category FROM define WHERE examPaperNum= {Pnum}   AND choosename='T' AND category='-1' ORDER BY orderNum ", Define.class, args1);
                    if (li3.size() > 0) {
                        for (int s = 0; s < li3.size(); s++) {
                            Map args2 = new HashMap();
                            args2.put("Pnum", Pnum);
                            args2.put("Id", ((Define) li3.get(s)).getId());
                            args2.put("Category", ((Define) li3.get(s)).getCategory());
                            List li4 = this.dao2._queryBeanList("SELECT  SUM(def.fullScore) fullScore FROM (SELECT id,fullScore,questionnum,chooseName,category,isParent  FROM define  WHERE examPaperNum= {Pnum}    AND choosename={Id}  AND category={Category}   )def", Define.class, args2);
                            if (null != ((Define) li4.get(0)).getFullScore() && !((Define) li4.get(0)).getFullScore().equals("")) {
                                su3 = ((Define) li4.get(0)).getFullScore();
                            } else {
                                su3 = Float.valueOf(0.0f);
                            }
                            full2 = Float.valueOf(full2.floatValue() + su3.floatValue());
                            List li5 = this.dao2._queryBeanList("SELECT  id,fullScore,questionnum,chooseName,category,isParent  FROM define  WHERE examPaperNum= {Pnum}    AND choosename={Id}  AND category='-1'   ", Define.class, args2);
                            if (null != li5 && li5.size() > 0) {
                                for (int j = 0; j < li5.size(); j++) {
                                    Map args3 = new HashMap();
                                    args3.put("Pnum", Pnum);
                                    args3.put("Category", ((Define) sql_li.get(i)).getCategory());
                                    args3.put("Id", ((Define) sql_li.get(i)).getId());
                                    List li8 = this.dao2._queryBeanList("SELECT  SUM(def.fullScore) fullScore FROM (SELECT id,fullScore,questionnum,chooseName,category  FROM subdefine WHERE examPaperNum= {Pnum}   AND category={Category}    AND pid={Id} )def", Define.class, args3);
                                    if (null != ((Define) li8.get(0)).getFullScore() && !((Define) li8.get(0)).getFullScore().equals("")) {
                                        valueOf2 = ((Define) li8.get(0)).getFullScore();
                                    } else {
                                        valueOf2 = Float.valueOf(0.0f);
                                    }
                                    Float su4 = valueOf2;
                                    full2 = Float.valueOf(full2.floatValue() + su4.floatValue());
                                }
                            }
                        }
                    }
                    List li9 = this.dao2._queryBeanList("SELECT  id,fullScore,questionnum,chooseName,category FROM define WHERE examPaperNum= {Pnum}   AND choosename='s' AND category='-1' ORDER BY orderNum ", Define.class, args1);
                    if (null != li9 && li9.size() > 0) {
                        for (int j2 = 0; j2 < li9.size(); j2++) {
                            Map args4 = new HashMap();
                            args4.put("Pnum", Pnum);
                            args4.put("Category", ((Define) sql_li.get(i)).getCategory());
                            args4.put("Id", ((Define) sql_li.get(i)).getId());
                            List li10 = this.dao2._queryBeanList("SELECT  SUM(def.fullScore) fullScore FROM (SELECT id,fullScore,questionnum,chooseName,category  FROM subdefine WHERE examPaperNum= {Pnum}   AND category={Category}    AND pid={Id} )def", Define.class, args4);
                            if (null != ((Define) li10.get(0)).getFullScore() && !((Define) li10.get(0)).getFullScore().equals("")) {
                                valueOf = ((Define) li10.get(0)).getFullScore();
                            } else {
                                valueOf = Float.valueOf(0.0f);
                            }
                            Float su5 = valueOf;
                            full2 = Float.valueOf(full2.floatValue() + su5.floatValue());
                        }
                    }
                    List li6 = this.dao2._queryBeanList("SELECT  SUM(def.fullScore) fullScore FROM ((SELECT id,fullScore,questionnum,chooseName FROM define WHERE examPaperNum= {Pnum}   AND category={Category}  AND choosename='s' ))def", Define.class, args1);
                    if (null != ((Define) li6.get(0)).getFullScore() && !((Define) li6.get(0)).getFullScore().equals("")) {
                        su2 = ((Define) li6.get(0)).getFullScore();
                    } else {
                        su2 = Float.valueOf(0.0f);
                    }
                    full = Float.valueOf(full2.floatValue() + su2.floatValue());
                }
                if (i == 0 && Pnum.equals(String.valueOf(((Define) sql_li.get(i)).getCategory()))) {
                    pFull = full;
                } else {
                    full = Float.valueOf(full.floatValue() + pFull.floatValue());
                }
                args1.put("full", full);
                rowArgList.add(new RowArg("update exampaper e SET totalScore ={full}  WHERE  exampaperNum={Category} ", args1));
            }
        }
        this.dao2._batchExecute(rowArgList);
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer delA(String examPaperNum, String category) {
        try {
            Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
            List li = this.dao2._queryBeanList("select DISTINCT examPaperNum from exampaper where examPaperNum<>{examPaperNum} and pexamPaperNum={examPaperNum} ", Exampaper.class, args);
            if (li != null && li.size() > 0) {
                for (int i = 0; i < li.size(); i++) {
                    Map args1 = new HashMap();
                    args1.put("examPaperNum", examPaperNum);
                    args1.put("ExamPaperNum", ((Exampaper) li.get(i)).getExamPaperNum());
                    List li_F = this.dao2._queryBeanList("select sum(fullScore) fullScore from define where exampaperNum={examPaperNum}  and category={ExamPaperNum} and p_questionNum='0'", Define.class, args1);
                    args1.put("FullScore", ((Define) li_F.get(0)).getFullScore());
                    this.dao2._execute("UPDATE exampaper set totalScore={FullScore} where examPaperNum={ExamPaperNum} ", args1);
                }
            }
            List list_f = this.dao2._queryBeanList("select sum(fullScore) fullScore from define where exampaperNum={examPaperNum}   and  p_questionNum='0'", Define.class, args);
            args.put("FullScore", ((Define) list_f.get(0)).getFullScore());
            this.dao2._execute("UPDATE exampaper set totalScore={FullScore} where exampaperNum={examPaperNum} ", args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer delOptionCount(String examPaperNum, String questionNum, String p_questionNum_id) throws Exception {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("p_questionNum_id", p_questionNum_id);
        List li = this.dao2._queryBeanList("select optionCount from define where exampaperNum={examPaperNum}  and id={p_questionNum_id} ", Define.class, args);
        args.put("OptionCount", ((Define) li.get(0)).getOptionCount());
        this.dao2._execute("update define d SET optionCount = {OptionCount} -1 where exampaperNum={examPaperNum} and id={p_questionNum_id} ", args);
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer saveCat_cate(String Pnum) throws Exception {
        Map args = new HashMap();
        args.put("Pnum", Pnum);
        List<RowArg> rowArgList = new ArrayList<>();
        new ArrayList();
        List sql2_li = this.dao2._queryBeanList("SELECT  DISTINCT examPaperNum FROM exampaper WHERE pexamPaperNum= {Pnum}    and examPaperNum<> {Pnum}  ", Exampaper.class, args);
        for (int i = 0; i < sql2_li.size(); i++) {
            Map args1 = new HashMap();
            args1.put("Pnum", Pnum);
            args1.put("ExamPaperNum", ((Exampaper) sql2_li.get(i)).getExamPaperNum());
            List list_f = this.dao2._queryBeanList("SELECT  SUM(def.fullScore) fullScore FROM ((SELECT SUM(fullScore) fullScore FROM define WHERE examPaperNum= {Pnum}   AND category={ExamPaperNum} AND choosename='s' ) UNION(SELECT fullScore FROM define WHERE examPaperNum= {Pnum}   AND category={ExamPaperNum}  AND choosename<>'s' GROUP BY choosename))def", Define.class, args1);
            args1.put("FullScore", ((Define) list_f.get(0)).getFullScore());
            String sql = new StringBuilder().append("update exampaper e SET totalScore =").append(((Define) list_f.get(0)).getFullScore()).toString() == null ? "0" : " {FullScore}  WHERE  exampaperNum={ExamPaperNum} ";
            rowArgList.add(new RowArg(sql, args1));
        }
        this.dao2._batchExecute(rowArgList);
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxClassList(String exam, String school, String subject, String grade) {
        return this.dao.getAjaxClassList(exam, school, subject, grade);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxExamList(String systemType) {
        return this.dao.getAjaxExamList(systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxExamList_zy(String systemType, String userId, String oneOrMore) {
        return this.dao.getAjaxExamList_zy(systemType, userId, oneOrMore);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxGradeList(String exam, String subject, String school) {
        return this.dao.getAjaxGradeList(exam, subject, school);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxSchoolList(String exam, String subject) {
        return this.dao.getAjaxSchoolList(exam, subject);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxSubjectList(Integer exam, Integer systemType, Integer gradeNum) {
        return this.dao.getAjaxSubjectList(exam, systemType, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxSubjectList_zy(Integer exam, Integer systemType, Integer gradeNum) {
        return this.dao.getAjaxSubjectList_zy(exam, systemType, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxSubjectListForOE_zy(Integer exam, Integer systemType, Integer gradeNum) {
        return this.dao.getAjaxSubjectListForOE_zy(exam, systemType, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getUserPositionSubjectList(String exam, String userNum, String mark) {
        return this.dao.getUserPositionSubjectList(exam, userNum, mark);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List getUserroleNum(String userId) {
        return this.dao.getUserroleNum(userId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getUserPositionGradeList(String examNum, String userNum, String subject, String mark) {
        return this.dao.getUserPositionGradeList(examNum, userNum, subject, mark);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxGradeList2(String exam, String subject, String systemType) {
        return this.dao.getAjaxGradeList2(exam, subject, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxGradeList2_zy(String exam, String subject, String systemType) {
        return this.dao.getAjaxGradeList2_zy(exam, subject, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getAjaxQuestionNumList(Integer exampaperNum, String groupnum) {
        return this.dao.getAjaxQuestionNumList(exampaperNum, groupnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getAjaxQuestionNumList2(Integer exampaperNum, String groupnum) {
        return this.dao.getAjaxQuestionNumList2(exampaperNum, groupnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Define> getAjaxQuestionNum(String exampaperNum) {
        return this.dao.getAjaxQuestionNum(exampaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Define> getqNumList(String exampaperNum, String qType) {
        return this.dao.getqNumList(exampaperNum, qType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getAjaxTaskList(Integer exampaperNum, String questionNum) {
        return this.dao.getAjaxTaskList(exampaperNum, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getAjaxTaskList2(Integer exampaperNum, String questionNum) {
        Map<String, String> infoMap = new HashMap<>();
        infoMap.put("examPaperNum", exampaperNum + "");
        infoMap.put("groupNum", questionNum);
        return this.teacherAppScoreDao.getGroupDetail(infoMap, "", null);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getTeacherAcgScore(Integer exampaperNum, String questionNum) {
        return this.dao.getTeacherAcgScore(exampaperNum, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<MarkError> getMarkError(String examNum, String schoolNum, String gradeNum, String subjectNum) {
        return this.dao.getMarkError(examNum, schoolNum, gradeNum, subjectNum);
    }

    public List<AjaxData> getAjaxExaminationRoomList(String exam, String subject, String grade) {
        return this.dao.getAjaxExaminationRoomList(exam, subject, grade);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getAjaxFullScoreList(String exampaperNum, String questionNum) {
        return this.dao.getAjaxFullScoreList(exampaperNum, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<CorrectInfo> getCorrectList(Score s) {
        return this.dao.getCorrectList(s);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public byte[] getQuesionImage(String scoreId) {
        return this.dao.getQuesionImage(scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Object> getCrossPageScoreIdList(String scoreId) {
        return this.dao.getCrossPageScoreIdList(scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public byte[] getQuesionImage(String examPaperNum, String studentID, String questionNum, String scoreId) {
        return this.dao.getQuesionImage(examPaperNum, studentID, questionNum, scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<String> getQuesionImage_scoreIdList(String examPaperNum, String studentID, String questionNum, String scoreId, String cross_page) {
        return this.dao.getQuesionImage_scoreIdList(examPaperNum, studentID, questionNum, scoreId, cross_page);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getScoreId(String examPaperNum, String studentID) {
        return this.dao.getScoreId(examPaperNum, studentID);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public byte[] getBigImage(String regId) {
        return this.dao.getBigImage(regId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Integer> getScoreList(String exam, String subject, String testCenter, String grade, String examinationRoom, String studentId, String page, String correctscorestatus) {
        return this.dao.getScoreList(exam, subject, testCenter, grade, examinationRoom, studentId, page, correctscorestatus);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer updateScore(String examPaperNum, String studentID, String questionNum, String score) {
        return this.dao.updateScore(examPaperNum, studentID, questionNum, score);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectExamList(String stat, String type, String systemType, String userId, String oneOrMore) {
        return this.dao.getCorrectExamList(stat, type, systemType, userId, oneOrMore);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectExamList_zy(String stat, String type, String systemType, String userId, String oneOrMore) {
        return this.dao.getCorrectExamList_zy(stat, type, systemType, userId, oneOrMore);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectExamList_zyyh(String stat, String type, String systemType, String userId, String oneOrMore) {
        return this.dao.getCorrectExamList_zyyh(stat, type, systemType, userId, oneOrMore);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectExaminationRoomList(String exam, String school, String subject, String grade, String stat, String type, String systemType) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.dao.getCorrectExaminationRoomList_online(exam, school, subject, grade, stat, systemType);
        }
        return this.dao.getCorrectExaminationRoomList(exam, school, subject, grade, stat, type, "");
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectExaminationRoomList_zy(String exam, String school, String subject, String grade, String stat, String type, String systemType, String testCenter) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.dao.getCorrectExaminationRoomList_online_zy(exam, school, subject, grade, stat, systemType, testCenter);
        }
        return this.dao.getCorrectExaminationRoomList(exam, school, subject, grade, stat, type, testCenter);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectExaminationRoomList_EP(String exam, String school, String subject, String grade, String stat, String type, String systemType, String examplace) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.dao.getCorrectExaminationRoomList_online_ep(exam, school, subject, grade, stat, systemType, examplace);
        }
        return this.dao.getCorrectExaminationRoomList_ep(exam, school, subject, grade, stat, type, examplace);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectAuthExaminationRoomList_EP(String exam, String school, String subject, String grade, String stat, String type, String userId, String examplace) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.dao.getCorrectAuthExaminationRoomList_online_ep(exam, school, subject, grade, stat, userId, examplace);
        }
        return this.dao.getCorrectAuthExaminationRoomList_ep(exam, school, subject, grade, stat, type, examplace, userId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectClassList(String exam, String school, String subject, String grade, String stat, String type, String systemType, String testCenter) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null == ep) {
            return null;
        }
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.dao.getCorrectClassList_online(exam, school, subject, grade, stat, type, testCenter);
        }
        return this.dao.getCorrectClassList_online(exam, school, subject, grade, stat, type, testCenter);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectGradeList(String exam, String subject, String school, String stat, String type, String systemType) {
        return this.dao.getCorrectGradeList(exam, subject, school, stat, type, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectGradeList_zy(String exam, String subject, String school, String stat, String type, String systemType, String testCenter) {
        return this.dao.getCorrectGradeList_zy(exam, subject, school, stat, type, systemType, testCenter);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getExamPlace(String exam, String subject, String grade, String stat, String type, String systemType) {
        return this.dao.getExamPlace(exam, subject, grade, stat, type, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAuthExamPlace(String exam, String subject, String grade, String stat, String type, String userId) {
        return this.dao.getAuthExamPlace(exam, subject, grade, stat, type, userId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List getCorrectSubjectList(String exam, String subject, String school, String stat, String type, String systemType) {
        return this.dao.getCorrectSubjectList(exam, subject, school, stat, type, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectSchoolList(String exam, String subject, String stat, String type, String systemType) {
        return this.dao.getCorrectSchoolList(exam, subject, stat, type, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrctTCList(String exam, String subject, String stat, String type, String systemType) {
        return this.dao.getCorrctTCList(exam, subject, stat, type, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrctTCList2(String exam, String subject, String grade, String stat, String type, String systemType) {
        return this.dao.getCorrctTCList2(exam, subject, grade, stat, type, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrctAuthTCList(String exam, String subject, String stat, String type, String userId) {
        return this.dao.getCorrctAuthTCList(exam, subject, stat, type, userId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrctAuthTCList2(String exam, String subject, String grade, String stat, String type, String userId) {
        return this.dao.getCorrctAuthTCList2(exam, subject, grade, stat, type, userId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public boolean isManager(String userId) {
        return this.dao.isManager(userId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectSchoolList_zy(String exam, String subject, String stat, String type, String systemType) {
        return this.dao.getCorrectSchoolList_zy(exam, subject, stat, type, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrctSchoolList_EP(String exam, String subject, String stat, String type, String systemType, String examplace, String grade) {
        return this.dao.getCorrctSchoolList_EP(exam, subject, stat, type, systemType, examplace, grade);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrctAuthSchoolList_EP(String exam, String subject, String stat, String type, String userId, String examplace, String grade) {
        return this.dao.getCorrctAuthSchoolList_EP(exam, subject, stat, type, userId, examplace, grade);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Define> getSQuestionNum(String exampaperNum, String gradeNum, String classnum) {
        return this.dao.getSQuestionNum(exampaperNum, gradeNum, classnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getSTeacherNum(String exampaperNum, String gradeNum, String classnum, String questionNum) {
        return this.dao.getSTeacherNum(exampaperNum, gradeNum, classnum, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getSTeacherNum2(String exampaperNum, String gradeNum, String classnum, String questionNum, String groupType) {
        return this.dao.getSTeacherNum2(exampaperNum, gradeNum, classnum, questionNum, groupType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Student> getSpotCheck_stuInfo(String scoreId, String examPaperNum, String questionNum) {
        return this.dao.getSpotCheck_stuInfo(scoreId, examPaperNum, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getSpotCheck(Integer examnum, Integer gradeNum, Integer subjectnum, String questionNum, Integer updateUser, Integer snumber, Integer pagestart, Integer pageSize, String examPaperNum) {
        return this.dao.getSpotCheck(examnum, gradeNum, subjectnum, questionNum, updateUser, snumber, pagestart, pageSize, examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getSpotCheck3(Integer examnum, Integer gradeNum, Integer subjectnum, String questionNum, String updateUser, Integer snumber, Integer pagestart, Integer pageSize, String examPaperNum, String fenshuduan1, String fenshuduan2, String examineestatu) {
        return this.dao.getSpotCheck3(examnum, gradeNum, subjectnum, questionNum, updateUser, snumber, pagestart, pageSize, examPaperNum, fenshuduan1, fenshuduan2, examineestatu);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getSpotCheck2(Integer exampaperNum, String questionNum, Integer studentId, String scoreId) {
        return this.dao.getSpotCheck2(exampaperNum, questionNum, studentId, scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getSpotCheck2_detail(Integer exampaperNum, String questionNum, Integer studentId, String scoreId) {
        return this.dao.getSpotCheck2_detail(exampaperNum, questionNum, studentId, scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getOneTwo(String examnum, String gradeNum, String subjectnum, String questionNum) {
        return this.dao.getOneTwo(examnum, gradeNum, subjectnum, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Define> getSpotCheckFullScore(Integer examnum, Integer gradeNum, Integer subjectnum, String questionNum) {
        return this.dao.getSpotCheckFullScore(examnum, gradeNum, subjectnum, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Score> getSpotCheckScore(Integer examnum, Integer gradeNum, Integer subjectnum, String scoreId, String questionnum) {
        return this.dao.getSpotCheckScore(examnum, gradeNum, subjectnum, scoreId, questionnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<ExamineeStuRecord> getExamineeRecord(Integer examnum, Integer gradeNum, Integer subjectnum, String scoreId, String questionnum) {
        return this.dao.getExamineeRecord(examnum, gradeNum, subjectnum, scoreId, questionnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer addExamineeRecord(ExamineeStuRecord examineeStuRecord) {
        return this.dao.addExamineeRecord(examineeStuRecord);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Remark> getSpotCheckRemark(Integer examnum, Integer gradeNum, Integer subjectnum, String scoreId, String questionnum) {
        return this.dao.getSpotCheckRemark(examnum, gradeNum, subjectnum, scoreId, questionnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Exam> getExamNameByNum(String examNum) {
        return this.dao.getExamNameByNum(examNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Subject> getSubjectNameByNum(String subjectNum) {
        return this.dao.getSubjectNameByNum(subjectNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getClassNameByNum(String classNum, String gradeNum) {
        return this.dao.getClassNameByNum(classNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getGradeNameByNum(String schoolNum, String gradeNum) {
        return this.dao.getGradeNameByNum(schoolNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getSchoolNameByNum(String schoolNum) {
        return this.dao.getSchoolNameByNum(schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Class> gets_pclassNum(String schoolNum, String gradeNum) {
        return this.dao.gets_pclassNum(schoolNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Remark> getSpotC(String exampaperNum, String questionNum, String schoolNum, String classNum, String gradeNum, String userNum, String scoreId) {
        return this.dao.getSpotC(exampaperNum, questionNum, schoolNum, classNum, gradeNum, userNum, scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<MarkError> getSpotC_MarkError(String exampaperNum, String questionNum, String schoolNum, String classNum, String gradeNum, String userNum, String scoreId) {
        return this.dao.getSpotC_MarkError(exampaperNum, questionNum, schoolNum, classNum, gradeNum, userNum, scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public int yanz(String exampaperNum, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum, String type, String userNum) {
        return this.dao.yanz(exampaperNum, questionNum, studentId, schoolNum, classNum, gradeNum, type, userNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public int yanz1(Integer exampaperNum, String questionNum, String scoreId, Integer type, String userNum) {
        return this.dao.yanz1(exampaperNum, questionNum, scoreId, type, userNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Student> getStudent(String StudentId, String schoolNum, String gradeNum, String classNum) {
        return this.dao.getStudent(StudentId, schoolNum, gradeNum, classNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<QuestionGroup_question> getQuestionGroupScheDule(Integer examnum, Integer gradeNum, Integer subjectnum, Integer examPaperNum) {
        return this.dao.getQuestionGroupScheDule(examnum, gradeNum, subjectnum, examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<QuestionGroup_question> getQuestionGroupScheDule1(String examnum, String gradeNum, String subjectnum) {
        return this.dao.getQuestionGroupScheDule1(examnum, gradeNum, subjectnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<QuestionGroup_question> getQuestionGroupSD(Integer examnum, Integer gradeNum, Integer subjectnum) {
        return this.dao.getQuestionGroupSD(examnum, gradeNum, subjectnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List jiancha(Integer examnum, Integer gradeNum, Integer subjectnum, Integer examPaperNum) {
        return this.dao.jiancha(examnum, gradeNum, subjectnum, examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getGroupNameBynum(String groupNum) {
        return this.dao.getGroupNameBynum(groupNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<QuestionGroup_question> getGroupSize(String exampaperNum, String groupNum) {
        return this.dao.getGroupSize(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<QuestionGroup_question> getQuesTask(Integer examnum, Integer gradeNum, Integer subjectnum, Integer examPaperNum) {
        return this.dao.getQuesTask(examnum, gradeNum, subjectnum, examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getStudentSize(String exampaperNum, String questionNum, String schoolNum, String gradeNum) {
        return this.dao.getStudentSize(exampaperNum, questionNum, schoolNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Questiongroup_mark_setting> getMarkTypeSche(String exampaperNum, String questionGroupNum) {
        return this.dao.getMarkTypeSche(exampaperNum, questionGroupNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getPaperNum(String examPaperNum, String questionNum, String schoolNum, String gradeNum) {
        return this.dao.getPaperNum(examPaperNum, questionNum, schoolNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getRemarkNum(String examPaperNum, String questionNum, String schoolNum, String gradeNum, int type) {
        return this.dao.getRemarkNum(examPaperNum, questionNum, schoolNum, gradeNum, type);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectSubjectList(String exam, String stat, String type, String systemType) {
        return this.dao.getCorrectSubjectList(exam, stat, type, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectSubjectList_zy(String exam, String stat, String type, String systemType) {
        return this.dao.getCorrectSubjectList_zy(exam, stat, type, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectSubjectList_zyyh(String exam, String stat, String type, String systemType) {
        return this.dao.getCorrectSubjectList_zyyh(exam, stat, type, systemType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectExaminationRoomStudent(String exam, String testCenter, String grade, String examRoomNum, String exampaperNum, String examroomornot, String subject) {
        return this.dao.getCorrectExaminationRoomStudent(exam, testCenter, grade, examRoomNum, exampaperNum, examroomornot, subject);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getCorrectClassStudent(String exam, String school, String grade, String classNum, String exampaperNum) {
        return this.dao.getCorrectClassStudent(exam, school, grade, classNum, exampaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer updateScore(String[] id, double score, boolean moreThanFullMarks) {
        return this.dao.updateScore(id, score, moreThanFullMarks);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer updateCorrectStatus(String status, String examination, String exam, String subject, String grade, String loginUserId, String testCenter) {
        return this.dao.updateCorrectStatus(status, examination, exam, subject, grade, loginUserId, testCenter);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer SYNCCorrectingInfo() {
        return this.dao.SYNCCorrectingInfo();
    }

    @Override // com.dmj.service.examManagement.ExamService
    public CorrectInfo imageDetailInfo(CorrectInfo c) {
        return this.dao.imageDetailInfo(c);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<CorrectInfo> getImageNotToScoreList(String examPaperNum, String school, String examinationRoom) {
        return this.dao.getImageNotToScoreList(examPaperNum, school, examinationRoom);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public double getFullScore(String exampaperNum, String questionNum) {
        return this.dao.getFullScore(exampaperNum, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getCorrectStatus(String examination, String exam, String subject, String grade, String type) {
        Object obj = this.dao.getCorrectStatus(examination, exam, subject, grade, type, "");
        if (null != obj) {
            return obj.toString();
        }
        return null;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String commbinPreNum(String id) {
        return this.dao.commbinPreNum(id);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer updateExamineeNum(String preNum, String[] ids, String[] nums, String exam, String grade, String examRoom, String subject) throws Exception {
        String exampaperNum = this.dao.getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
        updateData(preNum, ids, nums, exam, grade, examRoom, exampaperNum);
        FoundRepeat(exampaperNum, examRoom);
        if (getCountOfExceptionData(exampaperNum, examRoom).intValue() == 0) {
            updateImageAndScore(exampaperNum, examRoom);
            return 1;
        }
        return 2;
    }

    public void updateData(String preNum, String[] ids, String[] nums, String exam, String grade, String examRoom, String exampaperNum) throws Exception {
        List<String> sqls = new ArrayList<>();
        Integer num = getNumsOfStudentByExamroomId(examRoom);
        String str = "0";
        if (null != num && num.intValue() >= 100) {
            str = "";
        }
        for (int i = 0; i < ids.length; i++) {
            String sql = "";
            Map args = new HashMap();
            String id = ids[i].trim();
            nums[i].trim();
            if (null != nums[i] && !nums[i].equals("")) {
                String examineeNum = preNum.trim() + str + nums[i].trim();
                args.put("examineeNum", examineeNum);
                sql = !authexamineeNumExists(exam, grade, examRoom, examineeNum) ? "UPDATE regexaminee SET isModify= 'T', cNum = studentId , type = {TYPE_EXAMINEENUM_ERROR_INVALID}  WHERE id = {id} " : "UPDATE regexaminee SET isModify= 'T' " + str + " , cNum = (SELECT studentID FROM examinationnum WHERE examNum={exam}  AND gradeNum={grade} AND examineeNum={examineeNum} ) WHERE id = {id} ";
            }
            args.put("TYPE_EXAMINEENUM_ERROR_INVALID", "0");
            args.put("id", id);
            args.put("exam", exam);
            args.put("grade", grade);
            if (!sql.equals("")) {
                this.dao2._execute(sql, args);
            }
        }
        Map args1 = StreamMap.create().put("examPaperNum", (Object) exampaperNum).put("examRoom", (Object) examRoom);
        this.dao2._execute("UPDATE regexaminee SET cNum = studentId WHERE  exampaperNum={examPaperNum}  AND examinationRoomNum={examRoom}  AND type is  NULL and isModify is null ", args1);
        if (sqls.size() > 0) {
        }
        sqls.clear();
    }

    public void FoundRepeat(String exampaperNum, String examroom) throws Exception {
        Map args = new HashMap();
        args.put("examPaperNum", exampaperNum);
        args.put("examroom", examroom);
        args.put("TYPE_EXAMINEENUM_ERROR_REPEAT", "1");
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT id,examPaperNum,studentId,cNum,examinationRoomNum,page FROM  (SELECT id,examPaperNum,studentId,cNum,examinationRoomNum,page  FROM regexaminee \tWHERE exampaperNum={examPaperNum}  AND examinationRoomNum={examroom} and cNum is not null) s GROUP BY examPaperNum,cNum,page HAVING COUNT(1)>1", Examinationnumimg.class, args);
        if (null == _queryBeanList || _queryBeanList.size() == 0) {
            return;
        }
        Iterator<?> it = _queryBeanList.iterator();
        while (it.hasNext()) {
            Examinationnumimg img = (Examinationnumimg) it.next();
            args.put("cNum", img.getcNum());
            this.dao2._execute("update regexaminee set type={TYPE_EXAMINEENUM_ERROR_REPEAT}  WHERE exampaperNum={examPaperNum}  AND examinationRoomNum={examroom} and cNum={cNum} and page={cNum}  ", args);
        }
    }

    public Integer updateImageAndScore(String exampaperNum, String examroom) throws Exception {
        Map args = StreamMap.create().put("examPaperNum", (Object) exampaperNum).put("examroom", (Object) examroom);
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT  studentId,cNum,page FROM regexaminee WHERE isModify is not NULL and type is null AND exampaperNum={examPaperNum}  AND examinationRoomNum={examroom} ", Examinationnumimg.class, args);
        if (null == _queryBeanList) {
            return 1;
        }
        Iterator<?> it = _queryBeanList.iterator();
        while (it.hasNext()) {
            Examinationnumimg img = (Examinationnumimg) it.next();
            Student s = (Student) getOneByNum(Const.EXPORTREPORT_studentId, img.getcNum(), Student.class);
            this.mdao.modifyError(null, img.getStudentId(), s, exampaperNum, img.getPage());
        }
        return null;
    }

    public Integer getCountOfExceptionData(String exampaperNum, String examroom) {
        Map args = StreamMap.create().put("examPaperNum", (Object) exampaperNum).put("examroom", (Object) examroom);
        Object obj = this.dao2._queryObject("SELECT  COUNT(1)  FROM  regexaminee WHERE   exampaperNum={examPaperNum}  AND examinationRoomNum={examroom} AND type is not NULL", args);
        if (null == obj) {
            return 0;
        }
        return Integer.valueOf(Integer.parseInt(obj.toString()));
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer getNumsOfStudentByExamroomId(String id) throws Exception {
        return this.dao.getNumsOfStudentByExamroomId(id);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public boolean authexamineeNumExists(String exam, String grade, String examroom, String examineeNum) throws Exception {
        return this.dao.authexamineeNumExists(exam, grade, examroom, examineeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public boolean authScoreExists(String exampaperNum, String studentId, String page) throws Exception {
        return this.dao.authScoreExists(exampaperNum, studentId, page);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String authError(String exampaperNum, String examroom, String school, String studentId, String page, String id) throws Exception {
        Object re;
        Map args = new HashMap();
        args.put("examPaperNum", exampaperNum);
        args.put(License.SCHOOL, school);
        args.put("examroom", examroom);
        args.put("page", page);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("id", id);
        Object obj = this.dao2._queryObject("SELECT id from examineenumerror WHERE exampaperNum={examPaperNum}  AND schoolNum={school} AND examinationRoomNum={examroom} AND page={page} AND studentId={studentId} ", args);
        if (null != obj) {
            String ss = obj.toString();
            return ss;
        }
        Integer returnCode = Integer.valueOf(this.dao2._execute("INSERT INTO examineenumerror(examPaperNum,schoolNum,examinationRoomNum,page,studentId,insertUser,insertDate,errorType,examineeNum)SELECT examPaperNum,schoolNum,examinationRoomNum,page,studentId,insertUser,now(),'1','' FROM regexaminee WHERE id = {id} ", args));
        if (null != returnCode && null != (re = this.dao2._queryObject("SELECT e.id FROM regexaminee r LEFT join examineenumerror e ON e.examPaperNum= r.examPaperNum AND r.schoolNum=e.schoolNum AND r.examinationRoomNum=e.examinationRoomNum AND r.page=e.page AND r.studentId=e.studentId WHERE r.id={id} ", args))) {
            return re.toString();
        }
        return null;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer updateRegStudentIdById(String id, String examineeNum, String exam, String grade, String examroom, String type) throws Exception {
        String sql = "UPDATE regexaminee SET studentId = (SELECT studentID FROM examinationnum WHERE examNum={exam} AND gradeNum={grade} AND examinationRoomNum={examroom} AND examineeNum={examineeNum}) where id={id} ";
        if (null != type) {
            sql = "UPDATE regexaminee SET studentId ={examineeNum}  where id={id} ";
        }
        this.log.info("updateRegStudentIdById 修改考号相关记录之后 修改识别考号结果表 studentId  sql" + sql);
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("examroom", examroom);
        args.put("examineeNum", examineeNum);
        args.put("id", id);
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer updateRegStudentId(String oldStudentId, String newStudentId, String exampaperNum, String examroom, String page) throws Exception {
        Map args = StreamMap.create().put("newStudentId", (Object) newStudentId).put("examPaperNum", (Object) exampaperNum).put("examroom", (Object) examroom).put("oldStudentId", (Object) oldStudentId).put("page", (Object) page);
        this.log.info("updateRegStudentId 修改考号相关记录之后 修改识别考号结果表 studentId  sqlUPDATE regexaminee SET studentId= {newStudentId} WHERE exampaperNum={examPaperNum}   AND examinationRoomNum={examroom} AND studentId={oldStudentId} AND page={page} ");
        return Integer.valueOf(this.dao2._execute("UPDATE regexaminee SET studentId= {newStudentId} WHERE exampaperNum={examPaperNum}   AND examinationRoomNum={examroom} AND studentId={oldStudentId} AND page={page} ", args));
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer updateCorrectNumsStat(String status, String examination, String exam, String subject, String grade, String loginUserId, String testCenter, String examroomornot) {
        return this.dao.updateCorrectNumsStat(status, examination, exam, subject, grade, loginUserId, testCenter, examroomornot);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Define getQuesionDefineByExampaperNumAndQuesionNum(String epNum, String questionNum) {
        try {
            Map args = StreamMap.create().put("epNum", (Object) epNum).put("questionNum", (Object) questionNum);
            return (Define) this.dao2._queryBean("SELECT   DISTINCT   d.id,d.examPaperNum,d.questionNum,d.fullScore,d.questionType,d.difficult,d.inspectionlevel,d.one1,d.one2,d.one3,d.one4,d.one5,d.one6,d.one7,d.one8,d.one9,d.one10,d.one11,d.one12,d.one13,d.one14,d.one15,d.one16,d.one17,d.one18,d.one19,d.one20,d.one21,d.one22,d.one23,d.one24,d.one25,d.one26,d.multiple,d.errorRate,d.hasErrorSection,d.lengout,d.category,d.optionCount,d.category, d.deduction,CASE WHEN i.exampaperType='B' THEN d.answer_b  ELSE d.answer  END AS answer FROM (SELECT id,examPaperNum,questionNum,fullScore,questionType,difficult,inspectionlevel,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26, multiple,errorRate,hasErrorSection,lengout,category,optionCount,deduction,answer,answer_b  FROM define WHERE examPaperNum={epNum} AND id={questionNum} AND isParent!='1' AND choosename!='T' UNION ALL SELECT id,examPaperNum,questionNum,fullScore,questionType,difficult,inspectionlevel,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26, multiple,errorRate,hasErrorSection,lengout,category,optionCount,deduction,answer,answer_b  FROM subdefine WHERE examPaperNum={epNum} AND id={questionNum} ) d LEFT JOIN illegal i ON  d.examPaperNum=i.examPaperNum WHERE d.examPaperNum={epNum} AND d.id={questionNum} ", Define.class, args);
        } catch (Exception e) {
            this.log.info("获取试题定义 getQuesionDefineByExampaperNumAndQuesionNum() ", e);
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getQuesionDefineByExampaperNumAndQuesionNum1(String epNum, String questionNum, String stuid) {
        try {
            Map args = new HashMap();
            args.put("epNum", epNum);
            args.put("questionNum", questionNum);
            args.put("stuid", stuid);
            return this.dao2._querySimpleMap("SELECT   DISTINCT   d.id,d.examPaperNum,d.questionNum,d.fullScore,d.questionType, d.difficult,d.inspectionlevel, CASE WHEN i.exampaperType='B' THEN d.one1b ELSE d.one1 END AS one1, CASE WHEN i.exampaperType='B' THEN d.one2b ELSE d.one2 END AS one2, CASE WHEN i.exampaperType='B' THEN d.one3b ELSE d.one3 END AS one3, CASE WHEN i.exampaperType='B' THEN d.one4b ELSE d.one4 END AS one4, CASE WHEN i.exampaperType='B' THEN d.one5b ELSE d.one5 END AS one5, CASE WHEN i.exampaperType='B' THEN d.one6b ELSE d.one6 END AS one6, CASE WHEN i.exampaperType='B' THEN d.one7b ELSE d.one7 END AS one7, CASE WHEN i.exampaperType='B' THEN d.one8b ELSE d.one8 END AS one8, CASE WHEN i.exampaperType='B' THEN d.one9b ELSE d.one9 END AS one9, CASE WHEN i.exampaperType='B' THEN d.one10b ELSE d.one10 END AS one10, CASE WHEN i.exampaperType='B' THEN d.one11b ELSE d.one11 END AS one11, CASE WHEN i.exampaperType='B' THEN d.one12b ELSE d.one12 END AS one12, CASE WHEN i.exampaperType='B' THEN d.one13b ELSE d.one13 END AS one13, CASE WHEN i.exampaperType='B' THEN d.one14b ELSE d.one14 END AS one14, CASE WHEN i.exampaperType='B' THEN d.one15b ELSE d.one15 END AS one15, CASE WHEN i.exampaperType='B' THEN d.one16b ELSE d.one16 END AS one16, CASE WHEN i.exampaperType='B' THEN d.one17b ELSE d.one17 END AS one17, CASE WHEN i.exampaperType='B' THEN d.one18b ELSE d.one18 END AS one18, CASE WHEN i.exampaperType='B' THEN d.one19b ELSE d.one19 END AS one19, CASE WHEN i.exampaperType='B' THEN d.one20b ELSE d.one20 END AS one20, CASE WHEN i.exampaperType='B' THEN d.one21b ELSE d.one21 END AS one21, CASE WHEN i.exampaperType='B' THEN d.one22b ELSE d.one22 END AS one22, CASE WHEN i.exampaperType='B' THEN d.one23b ELSE d.one23 END AS one23, CASE WHEN i.exampaperType='B' THEN d.one24b ELSE d.one24 END AS one24, CASE WHEN i.exampaperType='B' THEN d.one25b ELSE d.one25 END AS one25, CASE WHEN i.exampaperType='B' THEN d.one26b ELSE d.one26 END AS one26, d.multiple, d.errorRate,d.hasErrorSection,d.lengout,d.category,d.optionCount,d.category, d.deduction,CASE WHEN i.exampaperType='B' THEN d.answer_b  ELSE d.answer  END AS answer FROM (SELECT id,examPaperNum,questionNum,fullScore,questionType,difficult,inspectionlevel,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26,one1b,one2b,one3b,one4b,one5b,one6b,one7b,one8b,one9b,one10b,one11b,one12b,one13b,one14b,one15b,one16b,one17b,one18b,one19b,one20b,one21b,one22b,one23b,one24b,one25b,one26b, multiple,errorRate,hasErrorSection,lengout,category,optionCount,deduction,answer,answer_b  FROM define WHERE examPaperNum={epNum} AND id={questionNum} AND isParent!='1' AND choosename!='T' UNION ALL SELECT id,examPaperNum,questionNum,fullScore,questionType,difficult,inspectionlevel,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26,one1b,one2b,one3b,one4b,one5b,one6b,one7b,one8b,one9b,one10b,one11b,one12b,one13b,one14b,one15b,one16b,one17b,one18b,one19b,one20b,one21b,one22b,one23b,one24b,one25b,one26b, multiple,errorRate,hasErrorSection,lengout,category,optionCount,deduction,answer,answer_b  FROM subdefine WHERE examPaperNum={epNum} AND id={questionNum}) d LEFT JOIN illegal i ON  d.examPaperNum=i.examPaperNum AND i.studentId={stuid}   WHERE d.examPaperNum={epNum}  AND d.id={questionNum} ", args);
        } catch (Exception e) {
            this.log.info("获取试题定义 getQuesionDefineByExampaperNumAndQuesionNum() ", e);
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String updateAnswer(String epNum, String questionNum, String answer, String studentId, String scoreId, int val) {
        if (answer.equals(Const.WHITE_CHAR)) {
            answer = "";
        }
        Double.valueOf(0.0d);
        Map<String, Object> defineVal = getQuesionDefineByExampaperNumAndQuesionNum1(epNum, questionNum, studentId);
        Double d = Double.valueOf(Util.suitAllObjSingleJudge(answer, defineVal));
        Integer returnCode = updateAnswerAndScoreToDB(epNum, questionNum, answer, studentId, d.doubleValue(), scoreId, val);
        updateStudentTotalScore(scoreId);
        if (null == returnCode) {
            return null;
        }
        return String.valueOf(d);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public double countScore(String answer, String questionNum, String examPaperNum, String scoreId) {
        Double.valueOf(0.0d);
        Map args = StreamMap.create().put("questionNum", (Object) questionNum).put("scoreId", (Object) scoreId);
        String stuid = this.dao2._queryStr("select studentId from objectivescore where questionNum = {questionNum} and id= {scoreId} ", args);
        Map<String, Object> defineVal = getQuesionDefineByExampaperNumAndQuesionNum1(examPaperNum, questionNum, stuid);
        Double d = Double.valueOf(Util.suitAllObjSingleJudge(answer, defineVal));
        return d.doubleValue();
    }

    @Override // com.dmj.service.examManagement.ExamService
    public double countScore1(String answer, String questionNum, String examPaperNum, String stuid) {
        Double.valueOf(0.0d);
        Map<String, Object> defineVal = getQuesionDefineByExampaperNumAndQuesionNum1(examPaperNum, questionNum, stuid);
        Double d = Double.valueOf(Util.suitAllObjSingleJudge(answer, defineVal));
        return d.doubleValue();
    }

    public Integer updateAnswerAndScoreToDB(String epNum, String questionNum, String answer, String studentId, double score, String scoreId, int val) {
        return this.dao.updateAnswerAndScoreToDB(epNum, questionNum, answer, studentId, score, scoreId, val);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<GeneralCorrectData> getGeneralCorrectData(String exam, String subject, String grade, String school) {
        return this.dao.getGeneralCorrectData(exam, subject, grade, school);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getScoreModelByExam(String exam, String type) {
        return this.dao.getScoreModelByExam(exam, type);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public double getFullScoreByExampaperAndQuestionNum(String exampaperNum, String questionNum) {
        return this.dao.getFullScoreByExampaperAndQuestionNum(exampaperNum, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Define getDefineByExampaperNumAndQuestionNum(String exampaperNum, String questionNum) {
        return this.dao.getDefineByExampaperNumAndQuestionNum(null == exampaperNum ? 0 : Integer.parseInt(exampaperNum), questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getQuestionScoreByQuestionInfo(String epNum, String questionNum, String score) {
        return this.dao.getQuestionScoreByQuestionInfo(null == epNum ? 0 : Integer.parseInt(epNum), questionNum, score);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List getSpotCheckChild(String exampaperNum, String questionnum, String scoreId) {
        return this.dao.getSpotCheckChild(exampaperNum, questionnum, scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public RegExaminee changePage(String schoolNum, String exampaperNum, String studentId, String oldPage, String newPage, String xmlPath, String rotation, String loginUser, String paperType, String typeAB, String regId) {
        if (null == regId || "".equals(regId)) {
            return null;
        }
        RegExaminee regOld = getRegExaminee(regId);
        if (null != rotation) {
            rotation.replace("-", "a");
        }
        RegExaminee regObj = this.mdao.authDataExistsFromRegExaminee(paperType, exampaperNum, studentId, schoolNum, Integer.parseInt(oldPage), null, null, null, null, null, regId);
        try {
            if (null != (regObj.getcNum() + "") && !"".equals(regObj.getcNum() + "")) {
                openExe(studentId, loginUser, xmlPath, schoolNum, exampaperNum, oldPage, newPage, rotation, regObj.getcNum() + "", regObj.getType() + "", typeAB);
            } else {
                openExe(studentId, loginUser, xmlPath, schoolNum, exampaperNum, oldPage, newPage, rotation, "null", regObj.getType() + "", typeAB);
            }
        } catch (Exception e) {
            this.log.info("调用exe 修改页码", e);
        }
        boolean result = authExeStatus(regId);
        String rel = "F";
        RegExaminee regNew = getRegExaminee(regId);
        if (result) {
            rel = "T";
            Examlog examlog = new Examlog();
            examlog.setOperate("修改页码-得分表中查到记录" + regNew.getStudentId());
            examlog.setExampaperNum(Integer.valueOf(Integer.parseInt(exampaperNum)));
            examlog.setStudentId(studentId);
            examlog.setExaminationRoomNum(regOld.getExaminationRoomNum());
            examlog.setInsertUser(loginUser);
            examlog.setInsertDate(DateUtil.getCurrentTime());
            this.dao2.save(examlog);
        }
        regNew.setExt4(rel);
        regNew.setOldStudentId(regOld.getStudentId() + "");
        regNew.setOldExamroomId(regOld.getExaminationRoomNum() + "");
        return regNew;
    }

    private RegExaminee getRegExaminee(String regId) {
        Map args = StreamMap.create().put("regId", (Object) regId);
        return (RegExaminee) this.dao2._queryBean("SELECT r.id,r.examPaperNum,r.schoolNum,r.studentId,r.examinationRoomNum,r.page,r.cNum,r.type,em.examineeNum ext1,s.studentName ext2,room.examinationRoomName ext3,e.examNum exam,e.subjectNum subject,e.gradeNum grade,exam.examinationRoomLength,exam.examineeLength FROM (SELECT * FROM regexaminee WHERE id={regId} ) r LEFT JOIN exampaper e ON e.examPaperNum = r.examPaperNum LEFT JOIN examinationnum  em ON em.studentID = r.studentId  AND e.examNum = em.examNum and  em.subjectNum=e.subjectnum LEFT JOIN examinationroom room ON room.id = em.examinationRoomNum LEFT JOIN student s ON s.id = r.studentId LEFT JOIN exam ON exam.examNum = e.examNum ", RegExaminee.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Examinationnum getExaminationnumInfoByExamNumAndExaminee(String exam, String examroom, String examineeNum) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("examineeNum", (Object) examineeNum).put("examroom", (Object) examroom);
        return (Examinationnum) this.dao2._queryBean("SELECT r.studentID,r.examinationRoomNum id,r.schoolNum,s.studentName,em.examinationRoomName FROM ( SELECT studentID,examinationRoomNum,schoolNum FROM examinationnum WHERE examNum={exam} AND examineeNum={examineeNum}  AND examinationRoomNum={examroom} )r LEFT JOIN student s ON r.studentID = s.id LEFT JOIN examinationroom em ON em.id = r.examinationRoomNum ", Examinationnum.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Object> getExamRoomPageRank(String epNum, String examroom, String school) {
        Exampaper ep = (Exampaper) this.dao.getOneByNum("exampaperNum", epNum, Exampaper.class);
        try {
            String sql = (ep.getDoubleFaced() == null || !ep.getDoubleFaced().equals("F")) ? "SELECT CONCAT('[第',CONVERT(page,char),'页：',CONVERT(COUNT(1),char),'张]') pageStr FROM ( SELECT r.page,r.studentId,r.cNum FROM (SELECT page,studentId,cNum,exampaperNum,schoolNum,id  from regexaminee WHERE exampaperNum={epNum} AND examinationRoomNum={examroom} and schoolNum={school} )r LEFT JOIN student s ON r.studentId = s.id AND r.schoolNum = s.schoolNum LEFT JOIN exampaper e ON e.examPaperNum =  r.examPaperNum  LEFT JOIN cantrecognized c ON c.regId = r.id" : "SELECT CONCAT('[第',CONVERT(page,char),'页：',CONVERT(COUNT(1),char),'张]') pageStr FROM ( SELECT r.page,r.studentId,r.cNum FROM (SELECT page,studentId,cNum,exampaperNum,schoolNum,id  from regexaminee WHERE exampaperNum={epNum} AND examinationRoomNum={examroom} and schoolNum={school} )r LEFT JOIN student s ON r.studentId = s.id AND r.schoolNum = s.schoolNum LEFT JOIN exampaper e ON e.examPaperNum =  r.examPaperNum  LEFT JOIN cantrecognized c ON c.regId = r.id  ";
            String sql2 = sql + " WHERE c.id IS  NULL AND r.studentId IS NOT NULL ) re GROUP BY re.page having re.page>0 ";
            Map args = StreamMap.create().put("epNum", (Object) epNum).put("examroom", (Object) examroom).put(License.SCHOOL, (Object) school);
            return this.dao2._queryColList(sql2, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer openExe(String stuId, String username, String settingPath, String schoolNum, String examPaperNum, String oldPage, String newPage, String rotation, String cNum, String type, String typeAB) {
        String filePath = "";
        String exeCommand = "ReClipIntoDB.exe " + stuId + " " + username + " " + schoolNum + " " + examPaperNum + " " + oldPage + " " + newPage + " " + rotation + " " + cNum + " " + type + " " + (settingPath + "/WEB-INF/classes/c3p0.properties") + " " + typeAB;
        try {
            filePath = createfile(settingPath, exeCommand);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Runtime rn = Runtime.getRuntime();
        Thread b = null;
        Thread c = null;
        try {
            try {
                Process p = rn.exec(filePath);
                BufferedInputStream is1 = new BufferedInputStream(p.getInputStream());
                BufferedInputStream is2 = new BufferedInputStream(p.getErrorStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is1));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(is1));
                1 r0 = new 1(this, br);
                r0.start();
                2 r02 = new 2(this, br2);
                r02.start();
                if (p.waitFor() != 0) {
                    if (p.exitValue() == 1) {
                        this.log.error("命令执行失败!");
                    }
                    File f = new File(filePath);
                    f.delete();
                    try {
                        r0.stop();
                        r02.stop();
                        return null;
                    } catch (Exception e) {
                        this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e);
                        e.printStackTrace();
                        return null;
                    }
                }
                is1.close();
                is2.close();
                br.close();
                br2.close();
                File f2 = new File(filePath);
                f2.delete();
                Integer valueOf = Integer.valueOf(p.exitValue());
                try {
                    r0.stop();
                    r02.stop();
                    return valueOf;
                } catch (Exception e2) {
                    this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e2);
                    e2.printStackTrace();
                    return null;
                }
            } catch (Exception e3) {
                this.log.error("重置页码后调用exe文件重新识别图像，执行报错", e3);
                try {
                    b.stop();
                    c.stop();
                    return null;
                } catch (Exception e4) {
                    this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e4);
                    e4.printStackTrace();
                    return null;
                }
            }
        } catch (Throwable th) {
            try {
                b.stop();
                c.stop();
                throw th;
            } catch (Exception e5) {
                this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e5);
                e5.printStackTrace();
                return null;
            }
        }
    }

    private String getRegId(String exampaperNum, String studentId, String oldPage, String newPage, String schoolNum, String paperType) {
        String sql = "select id from regexaminee WHERE exampaperNum={examPaperNum}  AND studentId={studentId} AND page={oldPage}    ";
        if (schoolNum != null && !"-1".equals(schoolNum)) {
            sql = sql + "AND schoolNum={schoolNum}  ";
        }
        Map args = StreamMap.create().put("examPaperNum", (Object) exampaperNum).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("oldPage", (Object) oldPage).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("paperType", (Object) paperType);
        Object obj = this.dao2._queryObject(sql + " and  type={paperType} ", args);
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    private boolean authExeStatus(String regId) {
        Map args = StreamMap.create().put("regId", (Object) regId);
        Object obj = this.dao2._queryObject("SELECT COUNT(c.questionNum)   FROM (  SELECT s.questionNum   FROM (SELECT examPaperNum,studentId,page FROM regexaminee  WHERE id={regId} )r    LEFT JOIN  score s  ON s.regId = r.id      WHERE s.questionNum IS not NULL    UNION  ALL   SELECT s.questionNum     FROM (SELECT examPaperNum,studentId,page FROM regexaminee  WHERE id={regId} )r    LEFT JOIN  objectivescore s  ON s.regId = r.id      WHERE s.questionNum IS not NULL) c", args);
        int count = (obj == null ? null : Integer.valueOf(Integer.parseInt(obj.toString()))).intValue();
        if (count == 0) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer deletePageErrorInfo(String schoolNum, String exampaperNum, String studentId, String page) {
        List<String> sqls = new ArrayList<>();
        if (page.equals("1")) {
            sqls.add("DELETE FROM illegal WHERE exampaperNum={examPaperNum}  AND studentId={studentId}   AND schoolNum={schoolNum} ");
        }
        Map args = new HashMap();
        args.put("examPaperNum", exampaperNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("page", page);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        sqls.add("DELETE FROM score WHERE exampaperNum={examPaperNum}  AND studentId={studentId}  AND page={page} AND schoolNum={schoolNum} ");
        sqls.add("DELETE FROM examinationnumimg WHERE exampaperNum={examPaperNum}  AND studentId={studentId}  AND page={page}  AND schoolNum={schoolNum} ");
        sqls.add("DELETE FROM questionimage WHERE exampaperNum={examPaperNum}  AND studentId={studentId}  AND page={page}  AND schoolNum={schoolNum} ");
        sqls.add("DELETE FROM scoreimage WHERE exampaperNum={examPaperNum}  AND studentId={studentId}  AND page={page}  AND schoolNum={schoolNum} ");
        sqls.add("DELETE FROM examineenumerror WHERE exampaperNum={examPaperNum}  AND studentId={studentId}  AND page={page}  AND schoolNum={schoolNum} ");
        this.dao2._batchExecute(sqls, args);
        return 1;
    }

    private void insertIntoCannotrecognized(String exampaperNum, String studentId, String page, String schoolNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) exampaperNum).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("page", (Object) page).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        this.dao2._execute("INSERT INTO cantrecognized (studentId,examinationRoomNum,examNum,subjectNum,gradeNum,page,insertUser,insertDate) SELECT reg.studentId,reg.examinationRoomNum,e.examNum,e.subjectNum,e.gradeNum,reg.page,'',NOW() FROM ( \tSELECT * FROM regexaminee  WHERE exampaperNum={examPaperNum}  AND studentId={studentId}  AND page={page}  AND schoolNum={schoolNum}  )reg LEFT JOIN exampaper e ON reg.examPaperNum = e.examPaperNum", args);
    }

    private String createfile(String path, String exeCommand) {
        File file = null;
        FileOutputStream out = null;
        try {
            try {
                long l = System.currentTimeMillis();
                file = new File(path + File.separator + l + ".bat");
                out = new FileOutputStream(file, true);
                StringBuffer buffer = new StringBuffer();
                buffer.append("cd " + path + " ");
                buffer.append("\r\n");
                buffer.append(exeCommand);
                byte[] b = buffer.toString().getBytes();
                out.write(b);
                try {
                    out.close();
                } catch (IOException e) {
                    this.log.error(" 创建批处理文件 ", e);
                    e.printStackTrace();
                }
            } catch (Exception e2) {
                this.log.error(" 创建批处理文件 ", e2);
                e2.printStackTrace();
                try {
                    out.close();
                } catch (IOException e3) {
                    this.log.error(" 创建批处理文件 ", e3);
                    e3.printStackTrace();
                }
            }
            return file.getAbsolutePath();
        } catch (Throwable th) {
            try {
                out.close();
            } catch (IOException e4) {
                this.log.error(" 创建批处理文件 ", e4);
                e4.printStackTrace();
            }
            throw th;
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer save(Object obj) {
        return Integer.valueOf(this.dao2.save(obj));
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void changeR(String exam, String grade, String subject, String answer, String questionNum, String fullScore, String one1, String one2, String one3, String one4, String one5, String one6) {
        Map args = new HashMap();
        args.put("answer", answer);
        args.put("fullScore", fullScore);
        args.put("one1", one1);
        args.put("one2", one2);
        args.put("one3", one3);
        args.put("one4", one4);
        args.put("one5", one5);
        args.put("one6", one6);
        args.put("questionNum", questionNum);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        this.dao2._execute("UPDATE define set answer={answer},fullScore={fullScore} ,one1={one1},one2={one2},one3={one3} ,one4={one4} ,one5={one5} ,one6={one6}  where questionNum={questionNum}  and examPaperNum=(select examPaperNum from exampaper  where examNum={exam} and gradeNum={grade} and subjectNum={subject} )", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void updateSc(double questionScore, String id) {
        Map args = StreamMap.create().put("questionScore", (Object) Double.valueOf(questionScore)).put("id", (Object) id);
        this.dao2._execute("UPDATE objectivescore set questionScore={questionScore} where id={id} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void updateScz(String exam, String grade, String subject, String questionNum, String fullScore, String id) {
        String examPaperNum = getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
        Map args = new HashMap();
        args.put("SCORE_EXCEPTION_DAFAULT", "-1");
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("fullScore", fullScore);
        args.put("id", id);
        args.put("SCORE_EXCEPTION_NORECOGNIZED", "2");
        args.put("SCORE_EXCEPTION_UPFULLMARKS", "4");
        this.dao2._execute("UPDATE score SET isException={SCORE_EXCEPTION_UPFULLMARKS} WHERE examPaperNum={examPaperNum}  AND questionNum={questionNum}    AND questionScore > {fullScore} and id={id} AND isException!={SCORE_EXCEPTION_NORECOGNIZED} ", args);
        this.dao2._execute("UPDATE score SET isException={SCORE_EXCEPTION_DAFAULT} WHERE examPaperNum={examPaperNum} AND questionNum={questionNum}   AND questionScore <= {fullScore} and id={id} AND isException!={SCORE_EXCEPTION_NORECOGNIZED} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List zscoreList(String exam, String grade, String subject, String questionNum) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade).put("subject", (Object) subject).put("questionNum", (Object) questionNum);
        return this.dao2._queryColList("select id from score where examPaperNum=(select examPaperNum from exampaper  where examNum={exam} and gradeNum={grade} and subjectNum={subject}  AND isHidden='F'  ) and questionNum={questionNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getschool(String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryStr("select schoolNum from student where studentId={studentId} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getoneNote(String examPaperNum, String studentId, String questionNum, String type) {
        String sql = "select  schoolNum,page  from studentpaperimage  WHERE exampaperNum={examPaperNum}  AND studentID={studentId}  AND page = (select page from define where exampaperNum={examPaperNum}  and questionNum = {questionNum} )";
        if (null != type && type.equals("F")) {
            sql = "select img from studentpaperimage  WHERE exampaperNum={examPaperNum}  AND studentID={studentId}  AND page = {questionNum} ";
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put("questionNum", questionNum);
        return this.dao2._queryBean(sql, Studentpaperimage.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer deleteK(String ext1, String questionNum) {
        Map args = StreamMap.create().put("questionNum", (Object) questionNum);
        return Integer.valueOf(this.dao2._execute("delete from knowdetail where  questionNum={questionNum} ", args));
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Float getfullScore(String exampaperNum, String questionNum1) {
        Map args = new HashMap();
        args.put("examPaperNum", exampaperNum);
        args.put("questionNum1", questionNum1);
        return this.dao2._queryFloat("select fullScore from define where exampaperNum={examPaperNum}  and questionnum={questionNum1}  union all  select fullScore from subdefine where exampaperNum={examPaperNum}  and questionnum={questionNum1} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getEx(String exampaperNum) throws Exception {
        Map args = new HashMap();
        args.put("examPaperNum", exampaperNum);
        List li = this.dao2._queryBeanList("select category from define where exampaperNum={examPaperNum}  union all select category from subdefine where exampaperNum={examPaperNum} ", Define.class, args);
        args.put("Category", ((Define) li.get(0)).getCategory());
        return this.dao2._queryStr("SELECT r.examPaperNum examPaperNum FROM(SELECT * FROM exampaper WHERE examPaperNum={Category} )r LEFT JOIN subject sjt ON sjt.subjectNum=r.subjectNum ORDER BY sjt.id desc", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void update_score(String subExamPaperNum, String gradeExamNum, String questionNum1) {
        this.log.info("update_score   sql：UPDATE score SET subExamPaperNum={subExamPaperNum} WHERE examPaperNum={gradeExamNum} and questionNum={questionNum1} ");
        Map args = new HashMap();
        args.put("subExamPaperNum", subExamPaperNum);
        args.put("gradeExamNum", gradeExamNum);
        args.put("questionNum1", questionNum1);
        this.dao2._execute("UPDATE score SET subExamPaperNum={subExamPaperNum} WHERE examPaperNum={gradeExamNum} and questionNum={questionNum1} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void update_objectivescore(String subExamPaperNum, String gradeExamNum, String questionNum1) {
        Map args = StreamMap.create().put("subExamPaperNum", (Object) subExamPaperNum).put("gradeExamNum", (Object) gradeExamNum).put("questionNum1", (Object) questionNum1);
        this.dao2._execute("UPDATE objectivescore SET subExamPaperNum={subExamPaperNum} WHERE examPaperNum={gradeExamNum} AND questionNum={questionNum1} ", args);
    }

    public void update_Parent(String gradeExamNum, String questionNum1, String answer, String parent_question, String child_question, String p_questionNum) {
        Map args = new HashMap();
        args.put("answer", answer);
        args.put("parent_question", parent_question);
        args.put("child_question", child_question);
        args.put("p_questionNum", p_questionNum);
        args.put("gradeExamNum", gradeExamNum);
        args.put("questionNum1", questionNum1);
        this.dao2._execute("UPDATE define set answer={answer} and parent_question={parent_question} and  child_question={child_question} and p_questionNum={p_questionNum} where exampaperNum={gradeExamNum} and questionNum={questionNum1} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void getUpdate_Chosen_update(String examPaperNum, String questionNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum);
        this.dao2._execute("UPDATE define set multiple='0', parent_question='T', child_question='T' ,p_questionNum='0' where exampaperNum={examPaperNum}  and questionNum={questionNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List questionType_Out(String examPaperNum, String questionNum, String subjectNum) throws Exception {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryBeanList("SELECT name FROM questiontype WHERE num=(SELECT questiontype FROM questiontypedetail WHERE exampaperNum={examPaperNum}  AND questionNum={questionNum} ) AND subjectNum = {subjectNum} ", QuestionType.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String questiontypeIn(String questionName, String subjectNum) {
        Map args = new HashMap();
        args.put("questionName", questionName);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryStr("SELECT num FROM questiontype WHERE name={questionName} AND subjectNum={subjectNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getOptioncount(String examPaperNum) throws Exception {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        List li = this.dao2._queryBeanList("select category from define where exampaperNum={examPaperNum} ", Define.class, args);
        args.put("Category", ((Define) li.get(0)).getCategory());
        return this.dao2._queryStr("select MAX(optioncount)  from define where exampaperNum={Category} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getDefine(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return this.dao2._queryStr("select * from define where exampaperNum={examPaperNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Classexam> getCorrectSchoolList_export(String exam, String subject, String stat, String type) {
        return this.dao.getCorrectSchoolList_export(exam, "", "", "");
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Exampaper> getExamPaperNums(String examNum, String gradeNum) {
        return this.dao.getExamPaperNums(examNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String addObjectiveQuesionImageSample(String examPaperNum, String studentId, String questionNum, String sampleVlue, String xmlPath, String loginUser, String scoreId, String regScore) {
        return this.dao.addObjectiveQuesionImageSample(examPaperNum, studentId, questionNum, sampleVlue, xmlPath, loginUser, scoreId, regScore);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List getObjectiveSamples() {
        return this.dao.getObjectiveSamples();
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getTemplateType(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao2._queryObject("SELECT templateType FROM exampaper WHERE examPaperNum={examPaperNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public byte[] getSampleImage(String id) {
        return this.dao.getSampleImage(id);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer deleteFromRegSample(String id) {
        return this.dao.deleteFromRegSample(id);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer re_Recognized_Objective(String examNum, String subjectNum, String gradeNum, String schoolNum, String examRoomNum, String examroomornot, String studentId, String optionType, String questionNum, String optioncount, String optionvalue, String reg_type, String option_val, String exePath, String loginUser, String examplace, String userId) {
        List<Score> list;
        String examPaperNum = this.dao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        if (this.dao.isManager(userId) || !"-1".equals(examplace)) {
            list = this.stestService.list("0", Integer.valueOf(examPaperNum).intValue(), examRoomNum, 0, 0, studentId, schoolNum, optioncount, optionvalue, optionType, examroomornot, questionNum, reg_type, "1", "F", examplace);
        } else {
            list = this.stestService.list_auth("0", Integer.valueOf(examPaperNum).intValue(), examRoomNum, 0, 0, studentId, schoolNum, optioncount, optionvalue, optionType, examroomornot, questionNum, reg_type, "1", "F", examplace, userId);
        }
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String curRegscore = list.get(i).getExt6();
                String curScoreId = list.get(i).getExt3();
                String curStudentId = String.valueOf(list.get(i).getExt8());
                String answer = "";
                String multiple = list.get(i).getExt7();
                if (null != multiple && multiple.equals("0")) {
                    answer = ItemThresholdRegUtil.getRegAnswerString(curRegscore, Integer.valueOf(option_val).intValue());
                }
                if (null != multiple && multiple.equals("1")) {
                    answer = ItemThresholdRegUtil.getRegAnswerString(curRegscore, Integer.valueOf(option_val).intValue());
                }
                int val = 0;
                if (null != answer && !"".equals(answer) && !"null".equals(answer)) {
                    val = ItemThresholdRegUtil.getThreshold(curRegscore, answer);
                    answer = Util.sort(answer);
                }
                updateAnswer(examPaperNum, list.get(i).getExt2(), answer, curStudentId, curScoreId, val);
            }
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        String templateType = this.dao2._queryStr("SELECT templateType FROM exampaper WHERE examNum={examNum} AND subjectNum={subjectNum} AND gradeNum={gradeNum} ", args);
        Reg_Th_Log reg_Th_Log = new Reg_Th_Log();
        if (null != templateType && "1".equals(templateType)) {
            reg_Th_Log.setTemplateType(templateType);
        } else {
            reg_Th_Log.setTemplateType("0");
        }
        reg_Th_Log.setVal(option_val);
        reg_Th_Log.setType("1");
        reg_Th_Log.setInsertUser(loginUser);
        reg_Th_Log.setInsertDate(DateUtil.getCurrentTime());
        save(reg_Th_Log);
        return 1;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List getRegSampleById(String id) {
        return this.dao.getRegSampleById(id);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Reg_Th_Log> getRe_th_log(int size) {
        return this.dao.getRe_th_log(size);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer saveRe_th_log_description(String re_th_log_id, String des) {
        return this.dao.saveRe_th_log_description(re_th_log_id, des);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List authIfExistUpFullScoreException(String examPaperNum, String school, String examinationRoom, String studentId, String page) {
        String sql = " select distinct regScore from score where examPaperNum = {examPaperNum} and qtype= {QUESTION_TYPE_SUBJECTIVE} and schoolNum = {school} and  isException = {SCORE_EXCEPTION_UPFULLMARKS} ";
        if (null != examinationRoom && !examinationRoom.equals("-1")) {
            sql = sql + "and  examinationRoomNum= {examinationRoom} ";
        }
        if (null != studentId && !studentId.equals("")) {
            sql = sql + "and  studentId= {studentId}  ";
        }
        if (null != page && !page.equals("")) {
            sql = sql + "and  page= {page}  ";
        }
        String sql2 = sql + "order by regScore";
        this.log.info("[" + getClass().getSimpleName() + "] : authIfExistUpFullScoreException()  sql: " + sql2);
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("QUESTION_TYPE_SUBJECTIVE", (Object) "1").put(License.SCHOOL, (Object) school).put("SCORE_EXCEPTION_UPFULLMARKS", (Object) "4").put("examinationRoom", (Object) examinationRoom).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("page", (Object) page);
        return this.dao2._queryColList(sql2, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public int[] batchSave(List<Object> list) {
        return this.dao2.batchSave(list);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getExamPaperMark(String examPaperNum) {
        try {
            Map args = new HashMap();
            args.put("examPaperNum", examPaperNum);
            List list = this.dao2._queryBeanList("SELECT DISTINCT id,examPaperNum,examNum,gradeNum,subjectNum,pexamPaperNum,type,jie,isHidden     FROM exampaper   WHERE 1=1 AND exampaperNum={examPaperNum}   and isHidden='F'", Exampaper.class, args);
            if (list != null && list.size() == 1) {
                return ((Exampaper) list.get(0)).getType();
            }
            return "0";
        } catch (Exception e) {
            this.log.info("获取当前试卷编号是有痕还是无痕", e);
            return "0";
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getQuesGroup(Integer exampaperNum, String groupnum) {
        return this.dao.getQuesGroup(exampaperNum, groupnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> panfendetail(Integer examPaperNum, String groupnum) {
        return this.dao.panfendetail(examPaperNum, groupnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getGroupInfo(Integer examPaperNum, String groupnum) {
        return this.dao.getGroupInfo(examPaperNum, groupnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> qscoreta(Integer examPaperNum, String groupnum) {
        return this.dao.qscoreta(examPaperNum, groupnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> qscoreta2(Integer examPaperNum, String groupnum) {
        return this.dao.qscoreta2(examPaperNum, groupnum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer clearUserQues(Integer examPaperNum, String groupNum, String userNum, String quesNum, String loginUserId, String rwCount) {
        return this.dao.clearUserQues(examPaperNum, groupNum, userNum, quesNum, loginUserId, rwCount);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void saveRemarkAndMarkError(Remark remark, MarkError markError, String updateSql, String updateSql2) {
        this.dao2.save(markError);
        MarkError newMarkError = new MarkError();
        BeanUtil.copyProperties(markError, newMarkError, new String[0]);
        markError.setType("1");
        this.dao2.save(markError);
        this.awardPointDao.panfenjilu(markError.getScoreId(), markError.getQuestionNum(), markError.getUserNum(), markError.getInsertUser());
        deleteScoreBiaoji(null, markError.getScoreId());
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List searchInQuesImage(String examPaperNum, String studentId, String questionNum, String school, String page, String examinationRoomNum) {
        return this.dao.searchInQuesImage(examPaperNum, studentId, questionNum, school, page, examinationRoomNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer addToClipErrorMethod(String scoreId, String regId, String examPaperNum, String loginUserNum) {
        return this.dao.addToClipErrorMethod(scoreId, regId, examPaperNum, loginUserNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer removeFromClipErrorMethod(String regId, String examPaperNum, String loginUserNum) {
        return this.dao.removeFromClipErrorMethod(regId, examPaperNum, loginUserNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List reClipExamPaper(String xmlPath, String examPaperNum, String examRoom, String schoolNum, String objVal, String subVal, String illegal_misVal, String typeAB, String loginUserNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        this.dao2._execute("DELETE  FROM cliperror WHERE exampaperNum={examPaperNum}   AND `status`='1'", args);
        openExeClipExamPaper(xmlPath, examPaperNum, examRoom, schoolNum, objVal, subVal, illegal_misVal, typeAB, loginUserNum);
        List list = searchClipSuccess_failure_page(examPaperNum, examRoom, schoolNum);
        return list;
    }

    public Integer openExeClipExamPaper(String settingPath, String examPaperNum, String examRoom, String schoolNum, String objVal, String subVal, String illegal_misVal, String typeAB, String loginUserNum) {
        String filePath = "";
        String exeCommand = "ReClip.exe " + loginUserNum + " " + (settingPath + "/WEB-INF/classes/c3p0.properties") + " " + examPaperNum + " " + examRoom + " " + objVal + " " + subVal + " " + illegal_misVal + " " + typeAB;
        try {
            filePath = createfile(settingPath, exeCommand);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Runtime rn = Runtime.getRuntime();
        Thread b = null;
        Thread c = null;
        try {
            try {
                Process p = rn.exec(filePath);
                BufferedInputStream is1 = new BufferedInputStream(p.getInputStream());
                BufferedInputStream is2 = new BufferedInputStream(p.getErrorStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is1));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(is1));
                3 r0 = new 3(this, br);
                r0.start();
                4 r02 = new 4(this, br2);
                r02.start();
                if (p.waitFor() != 0) {
                    if (p.exitValue() == 1) {
                        this.log.error("命令执行失败!");
                    }
                    File f = new File(filePath);
                    f.delete();
                    try {
                        r0.stop();
                        r02.stop();
                        return null;
                    } catch (Exception e) {
                        this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e);
                        e.printStackTrace();
                        return null;
                    }
                }
                is1.close();
                is2.close();
                br.close();
                br2.close();
                File f2 = new File(filePath);
                f2.delete();
                Integer valueOf = Integer.valueOf(p.exitValue());
                try {
                    r0.stop();
                    r02.stop();
                    return valueOf;
                } catch (Exception e2) {
                    this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e2);
                    e2.printStackTrace();
                    return null;
                }
            } catch (Exception e3) {
                this.log.error("重置页码后调用exe文件重新识别图像，执行报错", e3);
                try {
                    b.stop();
                    c.stop();
                    return null;
                } catch (Exception e4) {
                    this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e4);
                    e4.printStackTrace();
                    return null;
                }
            }
        } catch (Throwable th) {
            try {
                b.stop();
                c.stop();
                throw th;
            } catch (Exception e5) {
                this.log.error("重置页码后调用exe文件重新识别图像，关闭句柄报错", e5);
                e5.printStackTrace();
                return null;
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List searchClipStuMethod(String examPaperNum, String examRoom, String schoolNum, String gradeNum, String loginUserNum) {
        return this.dao.searchClipStuMethod(examPaperNum, examRoom, schoolNum, gradeNum, loginUserNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List searchClipSuccess_failure_page(String examPaperNum, String examRoom, String schoolNum) {
        return this.dao.searchClipSuccess_failure_page(examPaperNum, examRoom, schoolNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer batchAddClipError(String examNum, String subjectNum, String gradeNum, String examRoomNum, String schoolNum, String loginUserNum) {
        return this.dao.batchAddClipError(examNum, subjectNum, gradeNum, examRoomNum, schoolNum, loginUserNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer batchRemoveClipError(String examNum, String subjectNum, String gradeNum, String examRoomNum, String schoolNum, String loginUserNum) {
        return this.dao.batchRemoveClipError(examNum, subjectNum, gradeNum, examRoomNum, schoolNum, loginUserNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Exampaper getTotalScoreCount(String exam, String subject, String grade) {
        return this.dao.getTotalScoreCount(exam, subject, grade);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void getAnalysisscoreValue(String examPaperNum) {
        this.dao.getAnalysisscoreValue(examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getClassList(String exam, String school, String subject, String grade, String stat, String type, String systemType, String subjectType) {
        Exam ep = (Exam) getOneByNum(Const.EXPORTREPORT_examNum, exam, Exam.class);
        if (null == ep) {
            return null;
        }
        if (null != ep.getScanType() && ep.getScanType().equals("1")) {
            return this.dao.getClassList_online(exam, school, subject, grade, stat, type, subjectType);
        }
        return this.dao.getClassList_online(exam, school, subject, grade, stat, type, subjectType);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getExportStudentClassList(String exam, String school, String grade, String subjectType, String jie) {
        return this.dao.getExportStudentClassList(exam, school, grade, subjectType, jie);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void updateTaskData(String examPaperNum) {
        this.dao.updateTaskData(examPaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List gets_pques(String examnum, String subjectnum, String gradeNum, String schoolNum, String classnum, String type, String stype, String stuSource, float rate, String islevel, String subCompose) {
        return this.dao.gets_pques(examnum, subjectnum, gradeNum, schoolNum, classnum, type, stype, stuSource, rate, islevel, subCompose);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void updateExampaper_countScore(String totalScore, String analysisscore, String examPaperNum) {
        Map args = StreamMap.create().put("analysisscore", (Object) analysisscore).put("examPaperNum", (Object) examPaperNum);
        this.dao2._execute("UPDATE exampaper SET analysisscore = {analysisscore}  WHERE exampaperNum={examPaperNum}  ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void clipTwo(String scoreId, String examPaperNum, String user, String date, String examroomornot, String qtype) throws Exception {
        List<RowArg> rowArgList = new ArrayList<>();
        long stuId = GUID.getGUID();
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("scoreId", (Object) scoreId);
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT regId,examinationRoomNum,schoolNum,testingCentreId FROM score WHERE  exampaperNum={examPaperNum}  AND  id={scoreId} ", Score.class, args);
        for (int i = 0; i < _queryBeanList.size(); i++) {
            String regId = ((Score) _queryBeanList.get(i)).getRegId();
            String examinationRoomNum = ((Score) _queryBeanList.get(i)).getExaminationRoomNum();
            int schoolNum = ((Score) _queryBeanList.get(i)).getSchoolNum().intValue();
            String testingCentreId = ((Score) _queryBeanList.get(i)).getTestingCentreId();
            Map args1 = new HashMap();
            args1.put("stuId", Long.valueOf(stuId));
            args1.put("examPaperNum", examPaperNum);
            args1.put("regId", regId);
            args1.put("examinationRoomNum", examinationRoomNum);
            args.put(Const.EXPORTREPORT_schoolNum, Integer.valueOf(schoolNum));
            args.put("testingCentreId", testingCentreId);
            args.put("user", user);
            args.put("date", date);
            String idlistSql = "";
            if (qtype.equals("1")) {
                idlistSql = "SELECT id FROM score WHERE exampaperNum={examPaperNum}  AND  regId={regId} ";
            }
            if (qtype.equals("0")) {
                idlistSql = "SELECT id FROM objectivescore WHERE exampaperNum={examPaperNum}  AND  regId={regId} ";
            }
            if (qtype.equals("-1")) {
                idlistSql = "SELECT id FROM score WHERE exampaperNum={examPaperNum}  AND  regId={regId}  UNION ALL SELECT id FROM objectivescore WHERE exampaperNum={examPaperNum}  AND regId={regId} ";
            }
            List idlist = this.dao2._queryColList(idlistSql, args1);
            for (int j = 0; j < idlist.size(); j++) {
                String sid = String.valueOf(idlist.get(j));
                Map args2 = new HashMap();
                args2.put("examPaperNum", examPaperNum);
                args2.put("sid", sid);
                int count = this.dao2._queryInt("SELECT COUNT(1) FROM task WHERE exampaperNum={examPaperNum}  AND scoreId={sid} AND status!='F' ", args2).intValue();
                if (count == 0) {
                    if (qtype.equals("1") || qtype.equals("-1")) {
                        rowArgList.add(new RowArg("DELETE FROM score WHERE exampaperNum={examPaperNum}  AND  id={sid}  ", args2));
                    }
                    if (qtype.equals("0") || qtype.equals("-1")) {
                        rowArgList.add(new RowArg("DELETE FROM objectivescore WHERE exampaperNum={examPaperNum}  AND  id={sid}  ", args2));
                    }
                    rowArgList.add(new RowArg("DELETE  FROM scoreimage WHERE scoreId={sid}  ", args2));
                    rowArgList.add(new RowArg("DELETE  FROM questionimage   WHERE scoreId={sid} ", args2));
                    rowArgList.add(new RowArg("DELETE FROM markerror WHERE exampaperNum={examPaperNum}  AND scoreId={sid} ", args2));
                    rowArgList.add(new RowArg("DELETE FROM task WHERE exampaperNum={examPaperNum}  AND scoreId={sid} ", args2));
                }
            }
            String examroomStr1 = "";
            String examroomStr2 = "";
            if (examroomornot.equals("0")) {
                examroomStr1 = ",examinationRoomNum";
                examroomStr2 = " ,{examinationRoomNum} ";
            }
            String statStr = "";
            String stat1 = "";
            if (qtype.equals("0")) {
                statStr = " AND stat!='1'";
                stat1 = "1";
            }
            if (qtype.equals("1")) {
                statStr = " AND stat!='2'";
                stat1 = "2";
            }
            if (qtype.equals("-1")) {
                stat1 = "0";
            }
            args.put("stat1", stat1);
            String countSql = "SELECT count(1) FROM cantrecognized  WHERE exampaperNum={examPaperNum}  AND  regId={regId}  " + statStr + "";
            int count2 = this.dao2._queryInt(countSql, args1).intValue();
            if (count2 == 0) {
                String sql_17 = "INSERT into cantrecognized (examPaperNum,regId" + examroomStr1 + ",schoolNum,testingCentreId,insertUser,insertDate,isDelete,status,stat) VALUES({examPaperNum} ,{regId} " + examroomStr2 + ",{schoolNum} ,{testingCentreId},{user},{date},'F','0',{stat1})";
                rowArgList.add(new RowArg(sql_17, args1));
            } else {
                rowArgList.add(new RowArg("UPDATE cantrecognized SET stat='0' WHERE exampaperNum={examPaperNum}  AND regId={regId} ", args1));
            }
            if (qtype.equals("-1")) {
                rowArgList.add(new RowArg("DELETE FROM examinationnumimg WHERE  regId={regId}   ", args1));
                rowArgList.add(new RowArg("DELETE FROM examineenumerror WHERE  exampaperNum={examPaperNum}  AND  regId={regId}   ", args1));
                rowArgList.add(new RowArg("DELETE FROM illegal WHERE  exampaperNum={examPaperNum}  AND  regId={regId}   ", args1));
                rowArgList.add(new RowArg("DELETE FROM illegalimage WHERE  regId={regId}   ", args1));
                rowArgList.add(new RowArg("DELETE FROM exampapertypeimage WHERE  regId={regId}   ", args1));
            }
            rowArgList.add(new RowArg("UPDATE regexaminee SET studentId={stuId}  WHERE exampaperNum={examPaperNum} AND id={regId} ", args1));
            this.log.info("sql---UPDATE regexaminee SET studentId={stuId}  WHERE exampaperNum={examPaperNum} AND id={regId} ");
            this.log.info("sql4---DELETE FROM examinationnumimg WHERE  regId={regId}   ");
        }
        if (rowArgList.size() > 0) {
            this.dao2._batchExecute(rowArgList);
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void missJudge(String scoreId, String examPaperNum, String user, String date, String examroomornot) throws Exception {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId);
        this.dao2._execute("update score set isException = '0' WHERE id = {scoreId} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getscantype(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao2._queryStr("SELECT scanType FROM exampaper WHERE exampaperNum={examPaperNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public boolean updateScoreIsModify(String ScoreId, String status) {
        Map args = StreamMap.create().put(Const.CORRECT_SCORECORRECT, (Object) status).put("ScoreId", (Object) ScoreId);
        Integer aInteger = Integer.valueOf(this.dao2._execute("update score set isModify = {status} WHERE id = {ScoreId} ", args));
        if (null != aInteger && aInteger.intValue() > 0) {
            return true;
        }
        return false;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer getCorrectListCount(Score s) {
        return this.dao.getCorrectListCount(s);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public byte[] getobjectimg(String url, String img) {
        return this.dao.getobjectimg(url, img);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getQuesScoreTask(String exampaperNum, String questionNum, String insertUser, String scoreId) {
        return this.dao.getQuesScoreTask(exampaperNum, questionNum, insertUser, scoreId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Integer recognizeWrong(String[] ids) {
        List<RowArg> rowArgList = new ArrayList<>();
        for (String id : ids) {
            Map args = new HashMap();
            args.put("id", id);
            rowArgList.add(new RowArg("update score set isException=5 where id = {id} ", args));
        }
        try {
            this.dao2._batchExecute(rowArgList);
            return 1;
        } catch (Exception e) {
            return null;
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List objecterranly(String examNum, String subjectNum, String gradeNum, String schoolNum, String classNum, String studentType, String type, String source) {
        return this.dao.objecterranly(examNum, subjectNum, gradeNum, schoolNum, classNum, studentType, type, source);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List geterrorStudentList(String examNum, String subjectNum, String gradeNum, String schoolNum, String classNum, String type, String studentType, String source, String questionNum, String sign, String answer) {
        return this.dao.geterrorStudentList(examNum, subjectNum, gradeNum, schoolNum, classNum, type, studentType, source, questionNum, sign, answer);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getStuExam(String studentId) {
        return this.dao.getStuExam(studentId);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public int editExamineeNum(String examNum, String studentId, String schoolNum, String classNum) {
        return this.dao.editExamineeNum(examNum, studentId, schoolNum, classNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String setStuExampaperType(String studentId, String exampaperNum, String type) {
        return this.dao.setStuExampaperType(studentId, exampaperNum, type);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public byte[] getobjectscore(String studnetId, String questionNum) {
        return this.dao.getobjectscore(studnetId, questionNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Exampaper getExampaperInfo(String examNum, String subjectNum, String gradeNum) {
        return this.dao.getExampaperInfo(examNum, subjectNum, gradeNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getQuestionScoreByQuestionInfo(int epNum, String questionNum, String score) {
        return this.dao.getQuestionScoreByQuestionInfo(epNum, questionNum, score);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String isMerge(String exampaperNum, String questionNum) {
        Map args = StreamMap.create().put("questionNum", (Object) questionNum);
        return this.dao2._queryStr("SELECT `merge` FROM define df LEFT JOIN subdefine sdf ON df.examPaperNum = sdf.examPaperNum AND df.id = sdf.pid WHERE sdf.id = {questionNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<MarkError> getQueList(String exampaperNum, String questionNum, String scoreId) {
        try {
            Map args = StreamMap.create().put("questionNum", (Object) questionNum).put("scoreId", (Object) scoreId).put("examPaperNum", (Object) exampaperNum);
            return this.dao2._queryBeanList("SELECT s.id scoreId,s.questionNum questionNum from subdefine sdf INNER JOIN (SELECT pid from subdefine where id = {questionNum} ) sdf2 on sdf2.pid = sdf.pid INNER JOIN score s on s.questionNum = sdf.id INNER JOIN (SELECT studentId from score where id = {scoreId} ) s2 on s2.studentId = s.studentId where sdf.examPaperNum = {examPaperNum} ", MarkError.class, args);
        } catch (Exception e) {
            return null;
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getGroupNumByQueNum(String questionNum) {
        Map args = StreamMap.create().put("questionNum", (Object) questionNum);
        return this.dao2._queryStr("SELECT groupNum FROM questiongroup_question WHERE questionNum = {questionNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Map<String, Object>> getCaijueGroupList(String exampaperNum, String groupNum) {
        return this.dao.getCaijueGroupList(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Map<String, Object>> getNotCaijue(String exampaperNum, String groupNum) {
        return this.dao.getNotCaijue(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Map<String, Object>> getCaijueInfo(String exampaperNum, String groupNum) {
        return this.dao.getCaijueInfo(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String deleteExamineeNumOrNot(String classNum, String oldClassNum) {
        String flag = "0";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("oldClassNum", oldClassNum);
        if (!this.dao2._queryObject("select studentType from class where id={classNum} ", args).equals(this.dao2._queryObject("select studentType from class where id={classNum} ", args))) {
            flag = "1";
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public int deleteExamineeNum(String studentId, String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String id = this.dao2._queryStr("select id from student where studentId={studentId} ", args);
        args.put("id", id);
        return this.dao2._execute("delete from examinationnum where studentId={id} and examNum={examNum} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getweicaiqieNum1(String exampaperNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        String weicaiqie = this.dao2._queryStr("select count(1) weicaiqie from cantrecognized where exampaperNum= {exampaperNum} ", args);
        return weicaiqie;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getweicaiqieNum(String exam, String subject, String grade) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject} ", args);
        args.put("exampaperNum", exampaperNum);
        String weicaijue = this.dao2._queryStr("select count(1) weicaiqie from cantrecognized where exampaperNum= {exampaperNum} ", args);
        return weicaijue;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getStatistic(String exam, String subject, String grade, String questionNum, String updateUser, String fenshuduan1, String fenshuduan2, String panfenType) {
        String sql;
        String sql2;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("questionNum", questionNum);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("updateUser", "%" + updateUser + "%");
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject} ", args);
        args.put("examPaperNum", exampaperNum);
        String sanpingStr = "0".equals(panfenType) ? " and max(t.userNum)=3 " : "";
        String sql3 = "SELECT aa.id groupNum,aa.groupNum ext4,aa.questionNum,count(DISTINCT aa.scoreId) ext1,count(DISTINCT aa.yishenhe) ext2,aa.fullScore ext3 FROM (SELECT t.questionNum id,t.groupNum,IFNULL(subd.questionNum,d.questionNum) questionNum,t.scoreId scoreId,t.studentId studentId,esr.scoreId yishenhe,GROUP_CONCAT(distinct t.insertUser) insertUser,IFNULL(subd.fullScore,d.fullScore) fullScore  FROM (select * from task WHERE exampaperNum={examPaperNum}  and questionNum={questionNum} ) t LEFT JOIN define d on d.id = t.questionNum LEFT JOIN subdefine subd on subd.id = t.questionNum LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId AND esr.`status` = 'T' GROUP BY t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 " + sanpingStr + ") aa left join remark r on r.scoreId=aa.scoreId and r.type='1' ";
        if ("".equals(fenshuduan1) && "".equals(fenshuduan2)) {
            sql = sql3 + "where 1=1 ";
        } else {
            sql = sql3 + " inner JOIN score s ON s.id = aa.scoreId where s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
        }
        if (!"-1".equals(updateUser)) {
            sql = sql + " and aa.insertUser like {updateUser} ";
        }
        if ("1".equals(panfenType)) {
            sql2 = sql + " and r.status='T' ";
        } else {
            sql2 = sql + " and IFNULL(r.status,'T')='T' ";
        }
        String sql4 = sql2 + "GROUP BY aa.id ";
        this.log.info("--判分审核统计普通子题【getStatistic】sql--" + sql4);
        return this.dao2._queryBeanList(sql4, Task.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getHbcqGStatistic(String exam, String subject, String grade, String questionNum, String updateUser, String fenshuduan1, String fenshuduan2, String panfenType) {
        String sql;
        String sql2;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("questionNum", questionNum);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("updateUser", "%" + updateUser + "%");
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject} ", args);
        args.put("examPaperNum", exampaperNum);
        String subdCount = this.dao2._queryStr("select count(1) from subdefine where pid = {questionNum} ", args);
        String sSql = "";
        String sSql2 = "";
        if (!"".equals(fenshuduan1) || !"".equals(fenshuduan2)) {
            sSql = " LEFT JOIN score s ON s.id = t.scoreId ";
            sSql2 = " and sum(s.questionScore) BETWEEN {fenshuduan1} AND {fenshuduan2} ";
        }
        String sanpingStr = "0".equals(panfenType) ? " and max(t.userNum)=3 " : "";
        String sql3 = "SELECT aa.id groupNum,aa.groupNum ext4,aa.questionNum,count(DISTINCT aa.scoreId) ext1,sum(aa.yishenhe) ext2,aa.fullScore ext3 FROM (SELECT t.groupNum id,t.groupNum,d.questionNum questionNum,t.scoreId scoreId,t.studentId studentId,IF(COUNT(distinct esr.scoreId)=" + subdCount + ",1,0) yishenhe,GROUP_CONCAT(distinct t.insertUser) insertUser,d.fullScore fullScore  FROM (select * from task where exampaperNum={examPaperNum}  and groupNum={questionNum} ) t LEFT JOIN define d ON d.id = t.groupNum LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId AND esr.`status` = 'T' " + sSql + "GROUP BY t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 " + sanpingStr + sSql2 + ") aa left join remark r on r.scoreId=aa.scoreId and r.type='1' ";
        if (!"-1".equals(updateUser)) {
            sql = sql3 + " where aa.insertUser like {updateUser} ";
        } else {
            sql = sql3 + " where 1=1 ";
        }
        if ("1".equals(panfenType)) {
            sql2 = sql + " and r.status='T' ";
        } else {
            sql2 = sql + " and IFNULL(r.status,'T')='T' ";
        }
        String sql4 = sql2 + "GROUP BY aa.id ";
        this.log.info("--判分审核统计合并裁切主题【getHbcqGStatistic】sql--" + sql4);
        return this.dao2._queryBeanList(sql4, Task.class, args);
    }

    public List<Task> getAllStatistic_old(String exam, String subject, String grade, String questionNum, String updateUser, String fenshuduan1, String fenshuduan2) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("updateUser", "%" + updateUser + "%");
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject} ", args);
        args.put("examPaperNum", exampaperNum);
        String sql = "SELECT aa.id groupNum,aa.questionNum,count(DISTINCT aa.scoreId) ext1,count(DISTINCT aa.yishenhe) ext2,aa.fullScore ext3 FROM (SELECT t.questionNum id,IFNULL(subd.questionNum,d.questionNum) questionNum,t.scoreId scoreId,t.studentId studentId,esr.scoreId yishenhe,GROUP_CONCAT(distinct t.insertUser) insertUser,IFNULL(d2.orderNum, d.orderNum) orderNum1,subd.orderNum orderNum2,IFNULL(subd.fullScore,d.fullScore) fullScore  FROM task t LEFT JOIN subdefine subd on subd.id = t.questionNum LEFT JOIN define d on d.id = t.questionNum LEFT JOIN define d2 ON d2.id = subd.pid LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId AND esr.`status` = 'T' WHERE t.exampaperNum = {examPaperNum}  GROUP BY t.questionNum,t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 ) aa ";
        if ("".equals(fenshuduan1) && "".equals(fenshuduan2)) {
            if (!"-1".equals(updateUser)) {
                sql = sql + " where aa.insertUser like {updateUser} ";
            }
        } else {
            sql = sql + " inner JOIN score s ON s.id = aa.scoreId where s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
            if (!"-1".equals(updateUser)) {
                sql = sql + " and aa.insertUser like {updateUser} ";
            }
        }
        String sql2 = sql + "GROUP BY aa.id ORDER BY aa.orderNum1,aa.orderNum2 ";
        this.log.info("--判分审核统计全部试题【getAllStatistic】sql--" + sql2);
        return this.dao2._queryBeanList(sql2, Task.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getAllStatistic(String exam, String subject, String grade, String questionNum, String updateUser, String fenshuduan1, String fenshuduan2, String panfenType) {
        String sql;
        String sql2;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("questionNum", questionNum);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("updateUser", "%" + updateUser + "%");
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject} ", args);
        args.put("examPaperNum", exampaperNum);
        String sanpingStr = "0".equals(panfenType) ? " and max(t.userNum)=3 " : "";
        String sql3 = "SELECT aa.id groupNum,aa.groupNum ext4,aa.questionNum,count(DISTINCT aa.scoreId) ext1,count(DISTINCT aa.yishenhe) ext2,aa.fullScore ext3 FROM (SELECT t.questionNum id,t.groupNum,d.questionNum questionNum,t.scoreId scoreId,t.studentId studentId,esr.scoreId yishenhe,GROUP_CONCAT(distinct t.insertUser) insertUser,d.orderNum orderNum,d.fullScore fullScore  FROM (select * from task where exampaperNum={examPaperNum} ) t LEFT JOIN (select questionNum,id,fullScore,orderNum*1000 orderNum from define where exampaperNum={examPaperNum}  and id in ({questionNum[]}) union all select subd.questionNum,subd.id,subd.fullScore,(d.orderNum*1000+subd.orderNum) orderNum from subdefine subd left join define d on d.id = subd.pid where subd.exampaperNum={examPaperNum}  and subd.id in ({questionNum[]}) ) d ON d.id = t.questionNum LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId AND esr.`status` = 'T' WHERE d.id is not null GROUP BY t.questionNum,t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 " + sanpingStr + ") aa left join remark r on r.scoreId=aa.scoreId and r.type='1' ";
        if ("".equals(fenshuduan1) && "".equals(fenshuduan2)) {
            sql = sql3 + "where 1=1 ";
        } else {
            sql = sql3 + " inner JOIN score s ON s.id = aa.scoreId where s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
        }
        if (!"-1".equals(updateUser)) {
            sql = sql + " and aa.insertUser like {updateUser} ";
        }
        if ("1".equals(panfenType)) {
            sql2 = sql + " and r.status='T' ";
        } else {
            sql2 = sql + " and IFNULL(r.status,'T')='T' ";
        }
        String sql4 = sql2 + "GROUP BY aa.id ORDER BY aa.orderNum ";
        this.log.info("--判分审核统计全部试题【getAllStatistic】sql--" + sql4);
        return this.dao2._queryBeanList(sql4, Task.class, args);
    }

    public List<Task> getStatistic_old(String exam, String subject, String grade, String questionNum, String updateUser, String fenshuduan1, String fenshuduan2) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("questionNum", questionNum);
        args.put("updateUser", "%" + updateUser + "%");
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        String exampaperNum = this.dao2._queryStr("select exampaperNum from exampaper where examNum={exam} and gradeNum={grade} and subjectNum={subject} ", args);
        args.put("examPaperNum", exampaperNum);
        String queSql = "-1".equals(questionNum) ? "" : " and t.groupNum = {questionNum}  ";
        String sql = "SELECT aa.id groupNum,aa.questionNum,count(DISTINCT aa.scoreId) ext1,count(DISTINCT aa.yishenhe) ext2,aa.fullScore ext3 FROM (SELECT t.groupNum id,IFNULL(subd.questionNum,d.questionNum) questionNum,t.scoreId scoreId,t.studentId studentId,esr.scoreId yishenhe,GROUP_CONCAT(distinct t.insertUser) insertUser,IFNULL(d2.orderNum, d.orderNum) orderNum1,subd.orderNum orderNum2,IFNULL(subd.fullScore,d.fullScore) fullScore  FROM task t LEFT JOIN subdefine subd ON subd.id = t.groupNum LEFT JOIN define d ON d.id = t.groupNum LEFT JOIN define d2 ON d2.id = subd.pid LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId AND esr.`status` = 'T' WHERE t.exampaperNum = {examPaperNum}  " + queSql + "GROUP BY t.groupNum,t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 ) aa ";
        if ("".equals(fenshuduan1) && "".equals(fenshuduan2)) {
            if (!"-1".equals(updateUser)) {
                sql = sql + " where aa.insertUser like {updateUser} ";
            }
        } else {
            sql = sql + " inner JOIN score s ON s.id = aa.scoreId where s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
            if (!"-1".equals(updateUser)) {
                sql = sql + " and aa.insertUser like {updateUser} ";
            }
        }
        return this.dao2._queryBeanList(sql + "GROUP BY aa.id ORDER BY aa.orderNum1,aa.orderNum2 ", Task.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List getSpotCheckSChild(String groupnum, String updateUser, String fenshuduan1, String fenshuduan2, int pageSize, String panfenType) {
        Map args = new HashMap();
        args.put("updateUser", updateUser);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("groupnum", groupnum);
        args.put("pageSize", Integer.valueOf(pageSize));
        String insSql = "-1".equals(updateUser) ? "" : " and instr(GROUP_CONCAT(distinct t.insertUser),{updateUser} ) ";
        String scoreSql = ("".equals(fenshuduan1) && "".equals(fenshuduan2)) ? "" : " and questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
        String sanpingStr = "";
        String caijueStr = " and IFNULL(rm.`status`,'T')='T' ";
        if ("0".equals(panfenType)) {
            sanpingStr = " and max(t.userNum)=3 ";
        } else if ("1".equals(panfenType)) {
            caijueStr = " and rm.`status`='T' ";
        }
        String sql = "SELECT t.exampaperNum,t.studentId,u.id insertUser,s.regId ext5,t.questionNum questionNum,t.scoreId scoreId,s.questionScore questionScore,GROUP_CONCAT(u.realname ORDER BY t.userNum) ext1,GROUP_CONCAT(u.username ORDER BY t.userNum) ext2,GROUP_CONCAT(t.questionScore ORDER BY t.userNum) ext3,GROUP_CONCAT(distinct t.userNum ORDER BY t.userNum) userNum,IFNULL(u2.realname,'') ext4,IFNULL(u2.username,'') teacherName,IFNULL(rm.insertUser,'') updateUser,IFNULL(rm.questionScore,'--') remarkScore from  (select * from task where questionNum={groupnum} ) t LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId LEFT JOIN score s ON s.id = t.scoreId LEFT JOIN remark rm on rm.scoreId = t.scoreId and rm.type='1' LEFT JOIN `user` u ON u.id = t.insertUser LEFT JOIN `user` u2 ON u2.id = rm.insertUser WHERE esr.id is null " + caijueStr + "GROUP BY t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 " + sanpingStr + insSql + scoreSql + "ORDER BY rand() limit {pageSize} ";
        this.log.info("--判分未审核详情【getSpotCheckSChild】sql--" + sql);
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List getHbcqGSpotCheckSChild(String groupnum, String updateUser, String fenshuduan1, String fenshuduan2, int pageSize, String panfenType) {
        Map args = new HashMap();
        args.put("updateUser", updateUser);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("groupnum", groupnum);
        args.put("pageSize", Integer.valueOf(pageSize));
        String insSql = "-1".equals(updateUser) ? "" : " and instr(GROUP_CONCAT(distinct aa.insertUser),{updateUser} ) ";
        String scoreSql = ("".equals(fenshuduan1) && "".equals(fenshuduan2)) ? "" : " and questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
        String subdCount = this.dao2._queryStr("select count(1) from subdefine where pid = {groupnum} ", args);
        String sanpingStr = "";
        String caijueStr = " where IFNULL(rm.`status`,'T')='T' ";
        if ("0".equals(panfenType)) {
            sanpingStr = " and max(aa.maxUserNum)=3 ";
        } else if ("1".equals(panfenType)) {
            caijueStr = " where rm.`status`='T' ";
        }
        String sql = "SELECT aa.exampaperNum,aa.studentId,aa.insertUser,aa.ext5,aa.userNum,GROUP_CONCAT(distinct aa.questionNum ORDER BY aa.orderNum) questionNum,GROUP_CONCAT(distinct aa.scoreId ORDER BY aa.orderNum) scoreId,GROUP_CONCAT(aa.questionNum2 ORDER BY aa.orderNum) questionNum2,GROUP_CONCAT(aa.ext4 ORDER BY aa.orderNum) ext4,GROUP_CONCAT(aa.fullScore2 ORDER BY aa.orderNum) fullScore2,GROUP_CONCAT(aa.ext1 ORDER BY aa.orderNum) ext1,GROUP_CONCAT(aa.ext2 ORDER BY aa.orderNum) ext2,GROUP_CONCAT(aa.ext3 ORDER BY aa.orderNum) ext3,GROUP_CONCAT(aa.ext0 ORDER BY aa.orderNum) ext0,GROUP_CONCAT(aa.teacherName ORDER BY aa.orderNum) teacherName,GROUP_CONCAT(aa.updateUser ORDER BY aa.orderNum) updateUser,GROUP_CONCAT(aa.remarkScore ORDER BY aa.orderNum) remarkScore,sum(aa.questionScore) questionScore from (select t.exampaperNum,t.studentId,u.id insertUser,s.regId ext5,t.questionNum,t.scoreId,subd.questionNum questionNum2,s.questionScore ext4,subd.fullScore fullScore2,GROUP_CONCAT(u.realname ORDER BY t.userNum separator '_') ext1,GROUP_CONCAT(u.username ORDER BY t.userNum separator '_') ext2,GROUP_CONCAT(t.questionScore ORDER BY t.userNum separator '_') ext3,GROUP_CONCAT(distinct t.userNum ORDER BY t.userNum) userNum,IFNULL(u2.realname,'') ext0,IFNULL(u2.username,'') teacherName,IFNULL(rm.insertUser,'') updateUser,IFNULL(rm.questionScore,'--') remarkScore,s.questionScore questionScore,subd.orderNum,t.`status`,max(t.userNum) maxUserNum,esr.scoreId esrScoreId from (select * from task where groupNum='" + groupnum + "') t LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId LEFT JOIN score s ON s.id = t.scoreId LEFT JOIN subdefine subd ON subd.id = t.questionNum LEFT JOIN remark rm on rm.scoreId = t.scoreId and rm.type='1' LEFT JOIN `user` u ON u.id = t.insertUser LEFT JOIN `user` u2 ON u2.id = rm.insertUser " + caijueStr + "GROUP BY t.studentId,t.questionNum ) aa GROUP BY aa.studentId HAVING MIN(IF(aa.`status` = 'F', 0, 1)) = 1 and count(distinct aa.esrScoreId) <> " + subdCount + sanpingStr + insSql + scoreSql + " ORDER BY rand() limit {pageSize} ";
        this.log.info("--判分未审核详情合并裁切主题【getHbcqGSpotCheckSChild】sql--" + sql);
        return this.dao2._queryMapList(sql, null, args);
    }

    public List<Task> getSpotCheckSChild_old(String groupnum, String updateUser, String fenshuduan1, String fenshuduan2, int pageSize) {
        String insSql = "-1".equals(updateUser) ? "" : " and instr(GROUP_CONCAT(distinct t.insertUser),{updateUser}) ";
        String scoreSql = ("".equals(fenshuduan1) && "".equals(fenshuduan2)) ? "" : " and questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
        String sql = "SELECT t.exampaperNum,t.studentId,u.id insertUser,s.regId ext5,GROUP_CONCAT(distinct t.questionNum ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) questionNum,GROUP_CONCAT(distinct t.scoreId ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) scoreId,GROUP_CONCAT(IFNULL(subd.questionNum,d.questionNum) ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) questionNum2,GROUP_CONCAT(s.questionScore ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) ext4,GROUP_CONCAT(IFNULL(subd.fullScore,d.fullScore) ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) fullScore2,GROUP_CONCAT(tea.teacherName ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) ext1,GROUP_CONCAT(u.username ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) ext2,GROUP_CONCAT(t.questionScore ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) ext3,GROUP_CONCAT(distinct t.userNum ORDER BY t.userNum) userNum,s.questionScore questionScore from task t LEFT JOIN examineesturecord esr ON esr.scoreId = t.scoreId LEFT JOIN score s ON s.id = t.scoreId LEFT JOIN subdefine subd ON subd.id = t.questionNum LEFT JOIN define d ON d.id = t.questionNum LEFT JOIN `user` u ON u.id = t.insertUser LEFT JOIN teacher tea ON tea.id = u.userid WHERE esr.id is null and t.groupNum = {groupnum} GROUP BY t.studentId HAVING MIN(IF(t.`status` = 'F', 0, 1)) = 1 " + insSql + scoreSql + "ORDER BY rand() limit {pageSize} ";
        Map args = new HashMap();
        args.put("updateUser", updateUser);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("groupnum", groupnum);
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao2._queryBeanList(sql, Task.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public int getSpotCheckSCheckedCount(String groupnum, String updateUser, String fenshuduan1, String fenshuduan2, String panfenType) {
        String insSql = "-1".equals(updateUser) ? "" : " and t.insertUser = {updateUser} ";
        String scoreSql = "";
        String scoreSql2 = "";
        if (!"".equals(fenshuduan1) || !"".equals(fenshuduan2)) {
            scoreSql = " and s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
            scoreSql2 = " LEFT JOIN score s ON s.id = esr.scoreId  ";
        }
        String panfenStr = " and IFNULL(rm.`status`,'T')='T' ";
        if ("0".equals(panfenType)) {
            panfenStr = " and t.userNum=3 ";
        } else if ("1".equals(panfenType)) {
            panfenStr = " and rm.`status`='T' ";
        }
        String sql = "SELECT count(distinct t.studentId) num from examineesturecord esr LEFT JOIN task t ON t.scoreId = esr.scoreId LEFT JOIN remark rm ON rm.scoreId = esr.scoreId and rm.type='1' " + scoreSql2 + " WHERE esr.`status` = 'T' and t.questionNum = {groupnum} " + panfenStr + insSql + scoreSql;
        Map args = new HashMap();
        args.put("updateUser", updateUser);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("groupnum", groupnum);
        Object res = this.dao2._queryObject(sql, args);
        this.log.info("--判分已审核详情总数【getSpotCheckSCheckedCount】sql--" + sql);
        if (null == res) {
            return 0;
        }
        return Integer.valueOf(res.toString()).intValue();
    }

    @Override // com.dmj.service.examManagement.ExamService
    public int getHbcqGSpotCheckSCheckedCount(String groupnum, String updateUser, String fenshuduan1, String fenshuduan2, String panfenType) {
        Map args = new HashMap();
        args.put("groupnum", groupnum);
        args.put("updateUser", updateUser);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        String subdCount = this.dao2._queryStr("select count(1) from subdefine where pid = {groupnum} ", args);
        String insSql = "-1".equals(updateUser) ? "" : " and t.insertUser = {updateUser} ";
        String scoreSql = "";
        String scoreSql2 = "";
        if (!"".equals(fenshuduan1) || !"".equals(fenshuduan2)) {
            scoreSql = " and sum(s.questionScore) BETWEEN {fenshuduan1} AND {fenshuduan2} ";
            scoreSql2 = " LEFT JOIN score s ON s.id = esr.scoreId  ";
        }
        String sanpingStr = "";
        String caijueStr = " and IFNULL(rm.`status`,'T')='T' ";
        if ("0".equals(panfenType)) {
            sanpingStr = " and max(t.userNum)=3 ";
        } else if ("1".equals(panfenType)) {
            caijueStr = " and rm.`status`='T' ";
        }
        String sql = "SELECT count(1) from (SELECT t.studentId from examineesturecord esr LEFT JOIN task t ON t.scoreId = esr.scoreId LEFT JOIN remark rm on rm.scoreId = esr.scoreId and rm.type='1' " + scoreSql2 + " WHERE esr.`status` = 'T' and t.groupNum = {groupnum}  " + caijueStr + insSql + " group by t.studentId  having count(distinct t.scoreId) = " + subdCount + sanpingStr + scoreSql + ") tt";
        this.log.info("--判分已审核详情总数合并裁切主题【getHbcqGSpotCheckSCheckedCount】sql--" + sql);
        Object res = this.dao2._queryObject(sql, args);
        if (null == res) {
            return 0;
        }
        return Integer.valueOf(res.toString()).intValue();
    }

    public int getSpotCheckSCheckedCount_old(String groupnum, String updateUser, String fenshuduan1, String fenshuduan2) {
        String insSql = "-1".equals(updateUser) ? "" : " and t.insertUser = {updateUser} ";
        String scoreSql = "";
        String scoreSql2 = "";
        if (!"".equals(fenshuduan1) || !"".equals(fenshuduan2)) {
            scoreSql = " and s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
            scoreSql2 = " LEFT JOIN score s ON s.id = esr.scoreId  ";
        }
        String sql = "SELECT count(distinct t.studentId) num from examineesturecord esr LEFT JOIN task t ON t.scoreId = esr.scoreId " + scoreSql2 + "WHERE esr.`status` = 'T' and t.groupNum = {groupnum}  " + insSql + scoreSql;
        Map args = new HashMap();
        args.put("updateUser", updateUser);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("groupnum", groupnum);
        return this.dao2._queryInt(sql, args).intValue();
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List getSpotCheckSChecked(String groupnum, String updateUser, String fenshuduan1, String fenshuduan2, int pagestart, int pageSize, String panfenType) {
        String insSql = "-1".equals(updateUser) ? "" : " having instr({updateUser},GROUP_CONCAT(distinct t.insertUser)) ";
        String scoreSql = ("".equals(fenshuduan1) && "".equals(fenshuduan2)) ? "" : " and s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
        String sanpingStr = "";
        String caijueStr = " and IFNULL(rm.`status`,'T')='T' ";
        if ("0".equals(panfenType)) {
            sanpingStr = "".equals(insSql) ? " having max(t.userNum)=3 " : " and max(t.userNum)=3 ";
        } else if ("1".equals(panfenType)) {
            caijueStr = " and rm.`status`='T' ";
        }
        String sql = "SELECT t.exampaperNum,t.studentId,u.id insertUser,s.regId ext5,t.questionNum questionNum,t.scoreId scoreId,s.questionScore questionScore,GROUP_CONCAT(u.realname ORDER BY t.userNum) ext1,GROUP_CONCAT(u.username ORDER BY t.userNum) ext2,GROUP_CONCAT(t.questionScore ORDER BY t.userNum) ext3,GROUP_CONCAT(distinct t.userNum ORDER BY t.userNum) userNum, IFNULL(u2.realname,'') ext4,IFNULL(u2.username,'') teacherName,IFNULL(rm.insertUser,'') updateUser,IFNULL(rm.questionScore,'--') remarkScore from examineesturecord esr LEFT JOIN task t ON t.scoreId = esr.scoreId LEFT JOIN score s ON s.id = esr.scoreId LEFT JOIN remark rm on rm.scoreId = t.scoreId and rm.type='1' LEFT JOIN `user` u ON u.id = t.insertUser LEFT JOIN `user` u2 ON u2.id = rm.insertUser WHERE esr.`status` = 'T' and t.questionNum = {groupnum} " + caijueStr + scoreSql + " GROUP BY t.studentId " + insSql + sanpingStr + " order by esr.insertDate desc  limit {pagestart},{pageSize} ";
        this.log.info("--判分已审核详情【getSpotCheckSChecked】sql--" + sql);
        Map args = new HashMap();
        args.put("updateUser", updateUser);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("groupnum", groupnum);
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List getHbcqGSpotCheckSChecked(String groupnum, String updateUser, String fenshuduan1, String fenshuduan2, int pagestart, int pageSize, String panfenType) {
        Map args = new HashMap();
        args.put("groupnum", groupnum);
        args.put("updateUser", updateUser);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pageSize", Integer.valueOf(pageSize));
        String subdCount = this.dao2._queryStr("select count(1) from subdefine where pid = {groupnum} ", args);
        String insSql = "-1".equals(updateUser) ? "" : " and instr({updateUser} ,GROUP_CONCAT(distinct aa.insertUser)) ";
        String scoreSql = ("".equals(fenshuduan1) && "".equals(fenshuduan2)) ? "" : " and sum(aa.ext4) BETWEEN {fenshuduan1} AND {fenshuduan2} ";
        String sanpingStr = "";
        String caijueStr = " and IFNULL(rm.`status`,'T')='T' ";
        if ("0".equals(panfenType)) {
            sanpingStr = " and max(aa.maxUserNum)=3 ";
        } else if ("1".equals(panfenType)) {
            caijueStr = " and rm.`status`='T' ";
        }
        String sql = "SELECT aa.exampaperNum,aa.studentId,aa.insertUser,aa.ext5,aa.userNum,GROUP_CONCAT(distinct aa.questionNum ORDER BY aa.orderNum) questionNum,GROUP_CONCAT(distinct aa.scoreId ORDER BY aa.orderNum) scoreId,GROUP_CONCAT(aa.questionNum2 ORDER BY aa.orderNum) questionNum2,GROUP_CONCAT(aa.ext4 ORDER BY aa.orderNum) ext4,GROUP_CONCAT(aa.fullScore2 ORDER BY aa.orderNum) fullScore2,GROUP_CONCAT(aa.ext1 ORDER BY aa.orderNum) ext1,GROUP_CONCAT(aa.ext2 ORDER BY aa.orderNum) ext2,GROUP_CONCAT(aa.ext3 ORDER BY aa.orderNum) ext3,GROUP_CONCAT(aa.ext0 ORDER BY aa.orderNum) ext0,GROUP_CONCAT(aa.teacherName ORDER BY aa.orderNum) teacherName,GROUP_CONCAT(aa.updateUser ORDER BY aa.orderNum) updateUser, GROUP_CONCAT(aa.remarkScore ORDER BY aa.orderNum) remarkScore from (select t.exampaperNum,t.studentId,u.id insertUser,s.regId ext5,t.questionNum,t.scoreId,subd.questionNum questionNum2,s.questionScore ext4,subd.fullScore fullScore2,GROUP_CONCAT(u.realname ORDER BY t.userNum separator '_') ext1,GROUP_CONCAT(u.username ORDER BY t.userNum separator '_') ext2,GROUP_CONCAT(t.questionScore ORDER BY t.userNum separator '_') ext3,GROUP_CONCAT(distinct t.userNum ORDER BY t.userNum) userNum,IFNULL(u2.realname,'') ext0,IFNULL(u2.username,'') teacherName,IFNULL(rm.insertUser,'') updateUser, IFNULL(rm.questionScore,'--') remarkScore,t.userNum maxUserNum,esr.insertDate esrInsertDate,subd.orderNum from examineesturecord esr LEFT JOIN task t ON t.scoreId = esr.scoreId LEFT JOIN score s ON s.id = esr.scoreId LEFT JOIN subdefine subd ON subd.id = t.questionNum LEFT JOIN remark rm on rm.scoreId = t.scoreId and rm.type='1' LEFT JOIN `user` u ON u.id = t.insertUser LEFT JOIN `user` u2 ON u2.id = rm.insertUser WHERE esr.`status` = 'T' and t.groupNum = {groupnum} " + caijueStr + "GROUP BY t.studentId,t.questionNum )aa GROUP BY aa.studentId having count(distinct aa.scoreId) = " + subdCount + sanpingStr + insSql + scoreSql + " order by aa.esrInsertDate desc  limit {pagestart},{pageSize} ";
        this.log.info("--判分已审核详情合并裁切主题【getHbcqGSpotCheckSChecked】sql--" + sql);
        return this.dao2._queryMapList(sql, null, args);
    }

    public List<Task> getSpotCheckSChecked_old(String groupnum, String updateUser, String fenshuduan1, String fenshuduan2, int pagestart, int pageSize) {
        String insSql = "-1".equals(updateUser) ? "" : " having instr(GROUP_CONCAT(distinct t.insertUser),{updateUser}) ";
        String scoreSql = ("".equals(fenshuduan1) && "".equals(fenshuduan2)) ? "" : " and s.questionScore BETWEEN {fenshuduan1} AND {fenshuduan2} ";
        String sql = "SELECT t.exampaperNum,t.studentId,u.id insertUser,s.regId ext5,GROUP_CONCAT(distinct t.questionNum ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) questionNum,GROUP_CONCAT(distinct t.scoreId ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) scoreId,GROUP_CONCAT(IFNULL(subd.questionNum,d.questionNum) ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) questionNum2,GROUP_CONCAT(s.questionScore ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) ext4,GROUP_CONCAT(IFNULL(subd.fullScore,d.fullScore) ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) fullScore2,GROUP_CONCAT(tea.teacherName ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) ext1,GROUP_CONCAT(u.username ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) ext2,GROUP_CONCAT(t.questionScore ORDER BY t.userNum,IFNULL(subd.orderNum,d.orderNum)) ext3,GROUP_CONCAT(distinct t.userNum ORDER BY t.userNum) userNum from examineesturecord esr LEFT JOIN task t ON t.scoreId = esr.scoreId LEFT JOIN score s ON s.id = esr.scoreId LEFT JOIN subdefine subd ON subd.id = t.questionNum LEFT JOIN define d ON d.id = t.questionNum LEFT JOIN `user` u ON u.id = t.insertUser LEFT JOIN teacher tea ON tea.id = u.userid WHERE esr.`status` = 'T' and t.groupNum = {groupnum} " + scoreSql + "GROUP BY t.studentId " + insSql + "limit {pagestart},{pageSize} ";
        Map args = new HashMap();
        args.put("updateUser", updateUser);
        args.put("fenshuduan1", fenshuduan1);
        args.put("fenshuduan2", fenshuduan2);
        args.put("groupnum", groupnum);
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao2._queryBeanList(sql, Task.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void deleteCheckedRecord(String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        this.dao2._execute("delete from examineesturecord where scoreId = {scoreId} ", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void directUpdateScoreLeq(String[] scoreArr, String exampaperNum, String loginUserId, String time, String isShenhe, String xxtFlag) {
        String remQuestionNum = "";
        String remScoreId = "";
        for (String str : scoreArr) {
            String[] oneScore = str.split(Const.STRING_SEPERATOR);
            String scoreId = oneScore[0];
            if (!"".equals(scoreId)) {
                String questionNum = oneScore[1];
                String score = oneScore[2];
                remQuestionNum = questionNum;
                remScoreId = scoreId;
                deleteScoreBiaoji(score, scoreId);
                this.awardPointDao.updates(scoreId, score, loginUserId, time);
                updateStudentTotalScore(scoreId);
                AwardPoint awardPoint = new AwardPoint();
                awardPoint.setQuestionNum(questionNum);
                awardPoint.setInsertUser(loginUserId);
                awardPoint.setInsertDate(time);
                awardPoint.setExampaperNum(Integer.parseInt(exampaperNum));
                awardPoint.setType("1");
                awardPoint.setUserNum(loginUserId);
                awardPoint.setQuestionScore(score);
                awardPoint.setScoreId(scoreId);
                awardPoint.setIsException("F");
                deleteMarkError(awardPoint);
            }
        }
        addRemark(remScoreId, remQuestionNum, exampaperNum, loginUserId, time, isShenhe);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Task> getAllQuestionNumList(String exampaperNum) {
        return this.dao.getAllQuestionNumList(exampaperNum);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getTeachSubjectList(String examNum, String userId) {
        String sql;
        Object userroleId = getUserroleId(userId, Const.ROLE_YUEJUANGUANLIYUAN);
        if ("-2".equals(userId) || "-1".equals(userId) || null != userroleId) {
            sql = "SELECT distinct ep.subjectNum num,sub.subjectName name FROM exampaper ep INNER JOIN `subject` sub on sub.subjectNum = ep.subjectNum LEFT JOIN exampaper ep2 on ep2.pexamPaperNum = ep.examPaperNum and ep2.isHidden='T' WHERE ep.examNum = {examNum} and ep2.examPaperNum is null order by sub.orderNum,sub.subjectNum";
        } else {
            sql = "SELECT distinct ep.subjectNum num,sub.subjectName name FROM exampaper ep INNER JOIN `subject` sub on sub.subjectNum = ep.subjectNum INNER JOIN userposition up on up.subjectNum = ep.subjectNum LEFT JOIN exampaper ep2 on ep2.pexamPaperNum = ep.examPaperNum and ep2.isHidden='T' WHERE ep.examNum = {examNum}  and up.userNum = {userId}  and up.type = {teacher_common} and ep2.examPaperNum is null order by sub.orderNum,sub.subjectNum";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("userId", userId);
        args.put("teacher_common", "1");
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getTeachGradeList(String examNum, String subjectNum, String userId) {
        String sql;
        Object userroleId = getUserroleId(userId, Const.ROLE_YUEJUANGUANLIYUAN);
        if ("-2".equals(userId) || "-1".equals(userId) || null != userroleId) {
            sql = "SELECT DISTINCT ep.gradeNum num,bg.gradeName name,ep.totalScore ext1,ep.jie ext2 FROM exampaper ep INNER JOIN basegrade bg on bg.gradeNum = ep.gradeNum WHERE ep.examNum = {examNum} and ep.subjectNum = {subjectNum} order by ep.gradeNum";
        } else {
            sql = "SELECT DISTINCT ep.gradeNum num,bg.gradeName name,ep.totalScore ext1,ep.jie ext2 FROM exampaper ep INNER JOIN userposition up on up.gradeNum = ep.gradeNum and up.subjectNum = ep.subjectNum INNER JOIN basegrade bg on bg.gradeNum = ep.gradeNum WHERE ep.examNum = {examNum} and ep.subjectNum = {subjectNum} and up.userNum = {userId}  and up.type = {teacher_common} order by ep.gradeNum";
        }
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("userId", userId);
        args.put("teacher_common", "1");
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAjaxSchoolList(String examNum, String subjectNum, String gradeNum) {
        Object pSub = getPSub(examNum, gradeNum, subjectNum);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("pSub", pSub);
        return this.dao2._queryBeanList("SELECT DISTINCT en.schoolNum num,sch.schoolName name FROM examinationnum en INNER JOIN school sch on sch.id = en.schoolNum WHERE en.examNum = {examNum} and en.gradeNum = {gradeNum} and en.subjectNum = {pSub} ORDER BY convert(sch.schoolName using gbk)", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getTeachSchool(String userId) {
        Map args = new HashMap();
        args.put("userId", userId);
        return this.dao2._queryBeanList("SELECT u.schoolnum num,sch.schoolName name FROM `user` u INNER JOIN school sch on sch.id = u.schoolnum WHERE u.id = {userId} ", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Averagescore> getAllAveScoreSet(String examNum, String subjectNum, String gradeNum, String jie, String schoolNum, String userId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        Object pSub = getPSub(examNum, gradeNum, subjectNum);
        args.put("pSub", pSub);
        new ArrayList();
        List _queryBeanList = this.dao2._queryBeanList("SELECT DISTINCT en.classNum classNum,cla.className className,ave.average,ave.mindev,ave.maxdev,'' ext1,ave.status,ave.isModify  FROM examinationnum en LEFT JOIN averagescore ave on ave.examNum = en.examNum and ave.subjectNum = {subjectNum} and ave.classNum = en.classNum LEFT JOIN class cla on cla.id = en.classNum WHERE en.examNum = {examNum} and en.gradeNum = {gradeNum} and en.subjectNum = {pSub} and en.schoolNum = {schoolNum}  and cla.id is not null ORDER BY length(cla.className),convert(cla.className using gbk)", Averagescore.class, args);
        Averagescore allclass = (Averagescore) this.dao2._queryBean("SELECT classNum,'全年级' className,average,mindev,maxdev,'' ext1,status,isModify from averagescore where examNum = {examNum} and gradeNum = {gradeNum} and subjectNum = {subjectNum} and schoolNum = {schoolNum}  and classNum = '-1'", Averagescore.class, args);
        if (null == allclass) {
            allclass = new Averagescore("-1", "全年级", null, Const.mindev, Const.maxdev, "", "0", "0");
        }
        _queryBeanList.add(allclass);
        return _queryBeanList;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Averagescore> getClaAveScoreSet(String examNum, String subjectNum, String gradeNum, String jie, String schoolNum, String userId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("userId", userId);
        args.put("teacher_common", "1");
        Object pSub = getPSub(examNum, gradeNum, subjectNum);
        args.put("pSub", pSub);
        new ArrayList();
        String isModify = "0";
        String ext1Str = "if(ep.status='8' or ep.status='9','disabled','') ext1";
        Averagescore allclass = (Averagescore) this.dao2._queryBean("SELECT classNum,'全年级' className,average,mindev,maxdev,'disabled' ext1,status,isModify from averagescore where examNum = {examNum} and gradeNum = {gradeNum} and subjectNum = {subjectNum} and schoolNum = {schoolNum}  and classNum = '-1'", Averagescore.class, args);
        if (null == allclass) {
            allclass = new Averagescore("-1", "全年级", null, Const.mindev, Const.maxdev, "disabled", "0", "0");
        } else {
            isModify = allclass.getIsModify();
        }
        if (!"1".equals(isModify) && null != this.dao2._queryObject("SELECT t.id from exampaper ep left JOIN task t on t.exampaperNum = ep.pexamPaperNum where ep.examNum = {examNum} and ep.gradeNum = {gradeNum} and ep.subjectNum = {subjectNum} and t.insertUser = {userId} and t.`status` = 'T' LIMIT 1", args)) {
            ext1Str = "'disabled' ext1";
        }
        String classSql = "SELECT DISTINCT en.classNum,cla.className,ave.average,ave.mindev,ave.maxdev," + ext1Str + ",ave.status FROM examinationnum en LEFT JOIN exampaper ep on ep.examNum = en.examNum and ep.gradeNum = en.gradeNum and ep.subjectNum = en.subjectNum LEFT JOIN userposition up on up.classNum = en.classNum and up.subjectNum = {subjectNum} LEFT JOIN averagescore ave on ave.examNum = en.examNum and ave.subjectNum = {subjectNum} and ave.classNum = en.classNum LEFT JOIN class cla on cla.id = en.classNum WHERE en.examNum = {examNum} and en.gradeNum = {gradeNum} and en.subjectNum = {pSub} and en.schoolNum = {schoolNum}  and up.userNum = {userId} and up.type = {teacher_common} and cla.id is not null ORDER BY length(cla.className),convert(cla.className using gbk)";
        List _queryBeanList = this.dao2._queryBeanList(classSql, Averagescore.class, args);
        _queryBeanList.add(allclass);
        return _queryBeanList;
    }

    public Object getPSub(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryObject("SELECT subjectNum from exampaper where examPaperNum = (SELECT pexamPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} )", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void submitAveScoreSet(String examNum, String subjectNum, String gradeNum, String jie, String schoolNum, String classNum, String average, String mindev, String maxdev, String status, String isModify, String userId) {
        String currentTime = DateUtil.getCurrentTime();
        for (int i = 0; i < 2; i++) {
            Object claId = insertCommon(examNum, subjectNum, gradeNum, jie, schoolNum, classNum, average, mindev, maxdev, status, isModify, userId, currentTime);
            if (null != claId) {
                Object userroleId = getUserroleId(userId, Const.ROLE_YUEJUANGUANLIYUAN);
                if (!"-1".equals(classNum) || "-1".equals(userId) || "-2".equals(userId) || null != userroleId) {
                    Object[] params = {average, mindev, maxdev, status, isModify, userId, currentTime, jie, examNum, subjectNum, gradeNum, schoolNum, classNum};
                    this.dao2.execute("update averagescore set average=?,mindev=?,maxdev=?,status=?,isModify=?,updateUser=?,updateDate=?,jie=? where examNum=? and subjectNum=? and gradeNum=? and schoolNum=? and classNum=?", params);
                }
            }
            if (!"-1".equals(classNum)) {
                classNum = "-1";
                average = null;
                mindev = Const.mindev.toString();
                maxdev = Const.maxdev.toString();
            } else {
                return;
            }
        }
    }

    public Object insertCommon(String examNum, String subjectNum, String gradeNum, String jie, String schoolNum, String classNum, String average, String mindev, String maxdev, String status, String isModify, String userId, String currentTime) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        Object id = this.dao2._queryObject("SELECT  id from averagescore where examNum = {examNum} and gradeNum = {gradeNum} and subjectNum = {subjectNum} and schoolNum = {schoolNum}  and classNum = {classNum}", args);
        if (null == id) {
            Object[] params = {examNum, subjectNum, gradeNum, jie, schoolNum, classNum, average, mindev, maxdev, status, isModify, userId, currentTime, userId, currentTime};
            this.dao2.execute("insert into averagescore (examNum,subjectNum,gradeNum,jie,schoolNum,classNum,average,mindev,maxdev,status,isModify,insertUser,insertDate,updateUser,updateDate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", params);
        }
        return id;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void updateStatus(String examNum, String subjectNum, String gradeNum, String jie, String schoolNum, String status, String isModify, String userId) {
        String currentTime = DateUtil.getCurrentTime();
        Object id = insertCommon(examNum, subjectNum, gradeNum, jie, schoolNum, "-1", null, Const.mindev.toString(), Const.maxdev.toString(), status, isModify, userId, currentTime);
        if (null != id) {
            Object[] params = {status, isModify, userId, currentTime, examNum, subjectNum, gradeNum, schoolNum, "-1"};
            this.dao2.execute("update averagescore set status=?,isModify=?,updateUser=?,updateDate=? where examNum=? and subjectNum=? and gradeNum=? and schoolNum=? and classNum=?", params);
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAveSetSchool(String userId) {
        if ("-2".equals(userId) || "-1".equals(userId)) {
            return this.dao2.queryBeanList("SELECT DISTINCT ave.schoolNum num,sch.schoolName name from averagescore ave INNER JOIN school sch on sch.id = ave.schoolNum ORDER BY convert(sch.schoolName using gbk)", AjaxData.class);
        }
        Map args = new HashMap();
        args.put("userId", userId);
        return this.dao2._queryBeanList("SELECT DISTINCT ave.schoolNum num,sch.schoolName name from averagescore ave INNER JOIN school sch on sch.id = ave.schoolNum LEFT JOIN schauthormanage h on h.schoolNum = sch.id and h.userId={userId} LEFT JOIN user u on u.schoolNum = sch.id and u.id = {userId}  and u.usertype=1 where h.schoolNum is not null or u.schoolNum is not null ORDER BY convert(sch.schoolName using gbk)", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAllAveSetGrade(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList("SELECT DISTINCT ave.gradeNum num,bg.gradeName name from averagescore ave INNER JOIN basegrade bg on bg.gradeNum = ave.gradeNum where ave.schoolNum={schoolNum}  and ave.status='0' ORDER BY ave.gradeNum", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAveSetGrade(String userId, String schoolNum) {
        Map args = new HashMap();
        args.put("userId", userId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        return this.dao2._queryBeanList("SELECT DISTINCT ave.gradeNum num,bg.gradeName name from averagescore ave INNER JOIN basegrade bg on bg.gradeNum = ave.gradeNum INNER JOIN (SELECT DISTINCT gradeNum FROM userposition WHERE userNum={userId} and schoolNum={schoolNum}  UNION SELECT DISTINCT gradeNum FROM userposition_record WHERE userNum= {userId}  and schoolNum={schoolNum}  ) u on u.gradeNum = ave.gradeNum where ave.schoolNum={schoolNum}  and ave.status='0' ORDER BY ave.gradeNum", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAveSetJie(String schoolNum, String gradeNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        return this.dao2._queryBeanList("SELECT DISTINCT jie num,CONCAT(jie,'级') name from grade where schoolNum = {schoolNum}  and gradeNum = {gradeNum} ORDER BY jie desc", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getAveSetExam(String schoolNum, String gradeNum, String jie) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("jie", jie);
        return this.dao2._queryBeanList("SELECT DISTINCT ave.examNum num,e.examName name from averagescore ave INNER JOIN exam e on e.examNum = ave.examNum INNER JOIN arealevel al on al.examNum = ave.examNum where ave.gradeNum = {gradeNum} and ave.schoolNum = {schoolNum}  and ave.jie = {jie} and ave.status='0' ORDER BY e.examDate desc,e.insertDate desc", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Object getUserroleId(String userNum, String roleNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("roleNum", roleNum);
        return this.dao2._queryObject("select id from userrole where userNum={userNum} and roleNum={roleNum} limit 1", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Map<String, ExamData> getSubjectProgress() {
        Map<String, ExamData> subjectProgressMap = new HashMap<>();
        try {
            List examList = this.dao2.queryBeanList("SELECT DISTINCT e.examNum,ep.gradeNum,ep.subjectNum,ep.examPaperNum from examPaper ep INNER JOIN( \tSELECT examNum,examDate from exam where status <>9 and isDelete='F' \t)e on ep.examNum=e.examNum ORDER BY e.examDate desc,ep.gradeNum ASC,ep.subjectNum asc ", Exampaper.class);
            for (int h = 0; h < examList.size(); h++) {
                Exampaper e = (Exampaper) examList.get(h);
                String examNum = String.valueOf(e.getExamNum());
                String gradeNum = String.valueOf(e.getGradeNum());
                String subjectNum = String.valueOf(e.getSubjectNum());
                String examPaperNum = String.valueOf(e.getExamPaperNum());
                List<QuestionGroup_question> list = getQuestionGroupSD2(Integer.valueOf(Integer.parseInt(examNum)), Integer.valueOf(Integer.parseInt(gradeNum)), Integer.valueOf(Integer.parseInt(subjectNum)), examPaperNum);
                for (int k = 0; k < list.size(); k++) {
                    QuestionGroup_question qq = list.get(k);
                    BigDecimal totalAll = Convert.toBigDecimal(qq.getExt3(), BigDecimal.valueOf(0L));
                    BigDecimal studentsizeAll = Convert.toBigDecimal(qq.getExt4(), BigDecimal.valueOf(0L));
                    BigDecimal all = BigDecimal.valueOf(0L);
                    if (totalAll.compareTo(BigDecimal.valueOf(0L)) != 0) {
                        all = studentsizeAll.multiply(BigDecimal.valueOf(100L)).divide(totalAll, 0, RoundingMode.FLOOR);
                    }
                    if (all.compareTo(BigDecimal.valueOf(100L)) > 0) {
                        all = BigDecimal.valueOf(100L);
                    }
                    int num = Convert.toInt(all, 0).intValue();
                    String num1 = num + "";
                    ExamData examData = new ExamData();
                    examData.setExt3(totalAll + "");
                    examData.setExt4(studentsizeAll + "");
                    examData.setExt5(num1);
                    subjectProgressMap.put(qq.getExampaperNum() + "", examData);
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return subjectProgressMap;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Map<String, Map<String, Object>> getYuejuanProgress(ServletContext context) {
        int yipanCount;
        Map<String, Map<String, Map<String, Integer>>> bigTableMapReg = (Map) context.getAttribute(Const.bigTableMapReg);
        Map<String, Map<String, Map<String, Integer>>> bigTableMapExamina = (Map) context.getAttribute(Const.bigTableMapExamina);
        Map<String, Map<String, Map<String, Integer>>> bigTableMapTaskChoose = (Map) context.getAttribute(Const.bigTableMapTaskChoose);
        String weifenzuNum = "0";
        String bufenzuNum = "-1";
        Map<String, Map<String, Object>> yuejuanProgressMap = new HashMap<>();
        try {
            List examList = this.dao2.queryBeanList("SELECT DISTINCT e.examNum,ep.gradeNum,ep.subjectNum,ep.examPaperNum from examPaper ep INNER JOIN( \tSELECT examNum,examDate from exam where status <>9 and isDelete='F' )e on ep.examNum=e.examNum where ep.isHidden='F' and ep.type='0' ORDER BY e.examDate desc,ep.gradeNum ASC,ep.subjectNum asc ", Exampaper.class);
            for (int h = 0; h < examList.size(); h++) {
                Exampaper e = (Exampaper) examList.get(h);
                String.valueOf(e.getExamNum());
                String.valueOf(e.getGradeNum());
                String.valueOf(e.getSubjectNum());
                String examPaperNum = String.valueOf(e.getExamPaperNum());
                String isFenzu = this.systemService.fenzu(examPaperNum);
                Map<String, Object> oneSubjectResDataMap = new HashMap<>();
                List<Map<String, Object>> questionGroupInfoList = this.mdao.getQuestionGroupInfoList(examPaperNum);
                if (!CollUtil.isEmpty(questionGroupInfoList)) {
                    List<Map<String, Object>> tizuResDataMapList = new ArrayList<>();
                    int qLen = questionGroupInfoList.size();
                    for (int q = 0; q < qLen; q++) {
                        Map<String, Object> oneGroupMap = questionGroupInfoList.get(q);
                        oneGroupMap.put("examPaperNum", examPaperNum);
                        String groupNum = Convert.toStr(oneGroupMap.get("groupNum"));
                        int hebingshu = Convert.toInt(oneGroupMap.get("hebingshu"), 1).intValue();
                        String makType = Convert.toStr(oneGroupMap.get("makType"));
                        String judgetype = Convert.toStr(oneGroupMap.get("judgetype"));
                        Convert.toStr(oneGroupMap.get("scancompleted"));
                        String xuankaoqufen = Convert.toStr(oneGroupMap.get("xuankaoqufen"));
                        String xuazuoti = Convert.toStr(oneGroupMap.get("xuazuoti"));
                        int totalCount = 0;
                        if ("1".equals(xuazuoti)) {
                            if (CollUtil.isNotEmpty(bigTableMapTaskChoose)) {
                                totalCount = Convert.toInt(bigTableMapTaskChoose.get("taskChooseTotal").get(groupNum).get("-1"), 0).intValue();
                            }
                        } else if (CollUtil.isNotEmpty(bigTableMapReg)) {
                            totalCount = "2".equals(xuankaoqufen) ? Convert.toInt(bigTableMapReg.get("regXuan").get(examPaperNum).get("-1"), 0).intValue() : "3".equals(xuankaoqufen) ? Convert.toInt(bigTableMapReg.get("regXue").get(examPaperNum).get("-1"), 0).intValue() : Convert.toInt(bigTableMapReg.get("regTotal").get(examPaperNum).get("-1"), 0).intValue();
                        }
                        if ("1".equals(makType)) {
                            totalCount *= 2;
                            if ("1".equals(judgetype) || "2".equals(judgetype)) {
                                if ("1".equals(isFenzu)) {
                                    List<Map<String, Object>> schGroupNum_sanpingCountMapList = this.mdao.getSanpingCount_fenzu(examPaperNum, groupNum, hebingshu);
                                    int sanpingCount_total = schGroupNum_sanpingCountMapList.stream().mapToInt(m -> {
                                        return Convert.toInt(m.get("sanpingCount"), 0).intValue();
                                    }).sum();
                                    totalCount += sanpingCount_total;
                                    oneGroupMap.put("sanpingCount", Integer.valueOf(sanpingCount_total));
                                    oneGroupMap.put("schGroupNumSanpingCountMapList", schGroupNum_sanpingCountMapList);
                                } else {
                                    int sanpingCount = this.mdao.getSanpingCount(examPaperNum, groupNum, hebingshu);
                                    totalCount += sanpingCount;
                                    oneGroupMap.put("sanpingCount", Integer.valueOf(sanpingCount));
                                }
                            }
                            if ("0".equals(judgetype) || "2".equals(judgetype)) {
                                List<Map<String, Object>> caijueList = this.mdao.getWeicaijueList(examPaperNum, groupNum, hebingshu);
                                int zongcaijueCount = 0;
                                int yicaijueCount = 0;
                                int weicaijueCount = 0;
                                if (CollUtil.isNotEmpty(caijueList)) {
                                    zongcaijueCount = caijueList.size() / hebingshu;
                                    yicaijueCount = Convert.toInt(Long.valueOf(caijueList.stream().filter(m2 -> {
                                        return "T".equals(Convert.toStr(m2.get(Const.CORRECT_SCORECORRECT)));
                                    }).count()), 0).intValue() / hebingshu;
                                    weicaijueCount = zongcaijueCount - yicaijueCount;
                                }
                                oneGroupMap.put("zongcaijueCount", Integer.valueOf(zongcaijueCount));
                                oneGroupMap.put("yicaijueCount", Integer.valueOf(yicaijueCount));
                                oneGroupMap.put("weicaijueCount", Integer.valueOf(weicaijueCount));
                            }
                        }
                        if ("1".equals(isFenzu)) {
                            List<Map<String, Object>> schGroupNum_yipanCountMapList = this.mdao.getYipanCount_fenzu(examPaperNum, groupNum, hebingshu);
                            yipanCount = schGroupNum_yipanCountMapList.stream().mapToInt(m3 -> {
                                return Convert.toInt(m3.get("yipanCount"), 0).intValue();
                            }).sum();
                            oneGroupMap.put("schGroupNumYipanCountMapList", schGroupNum_yipanCountMapList);
                        } else {
                            yipanCount = this.mdao.getYipanCount(examPaperNum, groupNum, hebingshu);
                        }
                        int weipanCount = totalCount - yipanCount;
                        if (!"1".equals(xuazuoti) && hebingshu > 1) {
                            String questionName = Convert.toStr(oneGroupMap.get("questionName"));
                            oneGroupMap.put("questionName", StrUtil.addSuffixIfNot(questionName, "(合)"));
                        }
                        oneGroupMap.put("totalCount", Integer.valueOf(totalCount));
                        oneGroupMap.put("yipanCount", Integer.valueOf(yipanCount));
                        String baifenbi = "--";
                        if (totalCount > 0) {
                            baifenbi = Convert.toStr(Integer.valueOf((yipanCount * 100) / totalCount));
                        }
                        oneGroupMap.put("baifenbi", baifenbi);
                        oneGroupMap.put("weipanCount", Integer.valueOf(weipanCount));
                        int chongpanCount = this.mdao.getChongpanCount(examPaperNum, groupNum, hebingshu);
                        oneGroupMap.put("chongpanCount", Integer.valueOf(chongpanCount));
                        if ("1".equals(isFenzu)) {
                            List<Map<String, Object>> schGroupNumWorkloadMapList = new ArrayList<>();
                            Map<String, Integer> schGroupNumTotalCountMap = new HashMap<>();
                            if ("1".equals(xuazuoti)) {
                                if (CollUtil.isNotEmpty(bigTableMapTaskChoose)) {
                                    schGroupNumTotalCountMap = bigTableMapTaskChoose.get("taskChooseTotalIn").get(groupNum);
                                }
                            } else if (CollUtil.isNotEmpty(bigTableMapReg)) {
                                schGroupNumTotalCountMap = "2".equals(xuankaoqufen) ? bigTableMapReg.get("regXuanIn").get(examPaperNum) : "3".equals(xuankaoqufen) ? bigTableMapReg.get("regXueIn").get(examPaperNum) : bigTableMapReg.get("regTotalIn").get(examPaperNum);
                            }
                            if (CollUtil.isNotEmpty(schGroupNumTotalCountMap)) {
                                schGroupNumTotalCountMap.forEach((schoolGroupNum, totalCount_onegroup) -> {
                                    if ("1".equals(makType)) {
                                        totalCount_onegroup = Integer.valueOf(totalCount_onegroup.intValue() * 2);
                                        if ("1".equals(judgetype) || "2".equals(judgetype)) {
                                            int sanpingCount_total2 = Convert.toInt(oneGroupMap.get("sanpingCount"), 0).intValue();
                                            totalCount_onegroup = Integer.valueOf(totalCount_onegroup.intValue() + sanpingCount_total2);
                                        }
                                    }
                                    List<Map<String, Object>> schGroupNumYipanCountMapList = (List) oneGroupMap.get("schGroupNumYipanCountMapList");
                                    int yipanCount_onegroup = 0;
                                    if (CollUtil.isNotEmpty(schGroupNumYipanCountMapList)) {
                                        Optional<Map<String, Object>> res = schGroupNumYipanCountMapList.stream().filter(m4 -> {
                                            return schoolGroupNum.equals(Convert.toStr(m4.get("schoolGroupNum")));
                                        }).findAny();
                                        if (res.isPresent()) {
                                            yipanCount_onegroup = Convert.toInt(res.get().get("yipanCount"), 0).intValue();
                                        }
                                    }
                                    int weipanCount_onegroup = totalCount_onegroup.intValue() - yipanCount_onegroup;
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("schoolGroupNum", schoolGroupNum);
                                    hashMap.put("totalCount_fenzu", totalCount_onegroup);
                                    hashMap.put("yipanCount_fenzu", Integer.valueOf(yipanCount_onegroup));
                                    hashMap.put("weipanCount_fenzu", Integer.valueOf(weipanCount_onegroup));
                                    schGroupNumWorkloadMapList.add(hashMap);
                                });
                            }
                            oneGroupMap.put("schGroupNumWorkloadMapList", schGroupNumWorkloadMapList);
                        }
                    }
                    Map<String, List<Map<String, Object>>> xuanzuotiGroup = (Map) questionGroupInfoList.stream().filter(m4 -> {
                        return "1".equals(Convert.toStr(m4.get("xuazuoti")));
                    }).collect(Collectors.groupingBy(m5 -> {
                        return Convert.toStr(m5.get("xuazuotifenzu"));
                    }));
                    List<Map<String, Object>> xuanzuotiResDataMapList = new ArrayList<>();
                    xuanzuotiGroup.forEach((key, oneGroupXuanzuoti) -> {
                        int suborderNum;
                        Map<String, Object> oneGroupResDataMap = (Map) ObjectUtil.cloneByStream(oneGroupXuanzuoti.get(0));
                        int totalCount_all = 0;
                        int yipanCount_all = 0;
                        int weipanCount_all = 0;
                        List<Map<String, Object>> schGroupNumWorkloadMapList_all = new ArrayList<>();
                        int zongcaijueCount_all = 0;
                        int yicaijueCount_all = 0;
                        int weicaijueCount_all = 0;
                        int sanpingCount_all = 0;
                        List<Map<String, Object>> schGroupNumSanpingCountMapList_all = new ArrayList<>();
                        int chongpanCount_all = 0;
                        String groupNum_all = "";
                        String groupName = "";
                        String zitiNameStr = "";
                        String questionNameArr = "";
                        int xLen = oneGroupXuanzuoti.size();
                        for (int x = 0; x < xLen; x++) {
                            Map<String, Object> oneXuanzuotiDataMap = (Map) oneGroupXuanzuoti.get(x);
                            if (x == 0 && (suborderNum = Convert.toInt(oneXuanzuotiDataMap.get("suborderNum"), 0).intValue()) > 0) {
                                zitiNameStr = "_" + Convert.toStr(Integer.valueOf(suborderNum));
                            }
                            String groupNum2 = Convert.toStr(oneXuanzuotiDataMap.get("groupNum"));
                            groupNum_all = groupNum_all + groupNum2 + Const.STRING_SEPERATOR;
                            String questionName2 = Convert.toStr(oneXuanzuotiDataMap.get("questionName"));
                            groupName = groupName + questionName2 + "-";
                            questionNameArr = questionNameArr + questionName2 + zitiNameStr + Const.STRING_SEPERATOR;
                            int totalCount2 = Convert.toInt(oneXuanzuotiDataMap.get("totalCount"), 0).intValue();
                            totalCount_all += totalCount2;
                            int yipanCount2 = Convert.toInt(oneXuanzuotiDataMap.get("yipanCount"), 0).intValue();
                            yipanCount_all += yipanCount2;
                            int weipanCount2 = Convert.toInt(oneXuanzuotiDataMap.get("weipanCount"), 0).intValue();
                            weipanCount_all += weipanCount2;
                            List<Map<String, Object>> schGroupNumWorkloadMapList2 = (List) oneXuanzuotiDataMap.get("schGroupNumWorkloadMapList");
                            if (CollUtil.isNotEmpty(schGroupNumWorkloadMapList2)) {
                                schGroupNumWorkloadMapList_all.addAll(schGroupNumWorkloadMapList2);
                            }
                            int zongcaijueCount2 = Convert.toInt(oneXuanzuotiDataMap.get("zongcaijueCount"), 0).intValue();
                            zongcaijueCount_all += zongcaijueCount2;
                            int yicaijueCount2 = Convert.toInt(oneXuanzuotiDataMap.get("yicaijueCount"), 0).intValue();
                            yicaijueCount_all += yicaijueCount2;
                            int weicaijueCount2 = Convert.toInt(oneXuanzuotiDataMap.get("weicaijueCount"), 0).intValue();
                            weicaijueCount_all += weicaijueCount2;
                            int sanpingCount2 = Convert.toInt(oneXuanzuotiDataMap.get("sanpingCount"), 0).intValue();
                            sanpingCount_all += sanpingCount2;
                            List<Map<String, Object>> schGroupNumSanpingCountMapList = (List) oneXuanzuotiDataMap.get("schGroupNumSanpingCountMapList");
                            if (CollUtil.isNotEmpty(schGroupNumSanpingCountMapList)) {
                                schGroupNumSanpingCountMapList_all.addAll(schGroupNumSanpingCountMapList);
                            }
                            int chongpanCount2 = Convert.toInt(oneXuanzuotiDataMap.get("chongpanCount"), 0).intValue();
                            chongpanCount_all += chongpanCount2;
                        }
                        String groupName2 = StrUtil.removeSuffix(groupName, "-") + zitiNameStr + "(选)";
                        int hebingshu2 = Convert.toInt(oneGroupResDataMap.get("hebingshu"), 1).intValue();
                        if (hebingshu2 > 1) {
                            groupName2 = groupName2 + "(合)";
                        }
                        oneGroupResDataMap.put("groupNum", StrUtil.removeSuffix(groupNum_all, Const.STRING_SEPERATOR));
                        oneGroupResDataMap.put("questionNameArr", StrUtil.removeSuffix(questionNameArr, Const.STRING_SEPERATOR));
                        oneGroupResDataMap.put("questionName", groupName2);
                        oneGroupResDataMap.put("totalCount", Integer.valueOf(totalCount_all));
                        oneGroupResDataMap.put("yipanCount", Integer.valueOf(yipanCount_all));
                        String baifenbi_all = "--";
                        if (totalCount_all > 0) {
                            baifenbi_all = Convert.toStr(Integer.valueOf((yipanCount_all * 100) / totalCount_all));
                        }
                        oneGroupResDataMap.put("baifenbi", baifenbi_all);
                        oneGroupResDataMap.put("weipanCount", Integer.valueOf(weipanCount_all));
                        if (CollUtil.isNotEmpty(schGroupNumWorkloadMapList_all)) {
                            oneGroupResDataMap.put("schGroupNumWorkloadMapList", (List) ((Map) schGroupNumWorkloadMapList_all.stream().collect(Collectors.groupingBy(m1 -> {
                                return m1.get("schoolGroupNum");
                            }))).values().stream().map(m22 -> {
                                Map<String, Object> map = (Map) m22.get(0);
                                map.put("totalCount_fenzu", Integer.valueOf(m22.stream().mapToInt(m32 -> {
                                    return Convert.toInt(m32.get("totalCount_fenzu"), 0).intValue();
                                }).sum()));
                                map.put("yipanCount_fenzu", Integer.valueOf(m22.stream().mapToInt(m33 -> {
                                    return Convert.toInt(m33.get("yipanCount_fenzu"), 0).intValue();
                                }).sum()));
                                map.put("weipanCount_fenzu", Integer.valueOf(m22.stream().mapToInt(m34 -> {
                                    return Convert.toInt(m34.get("weipanCount_fenzu"), 0).intValue();
                                }).sum()));
                                return map;
                            }).collect(Collectors.toList()));
                        }
                        oneGroupResDataMap.put("zongcaijueCount", Integer.valueOf(zongcaijueCount_all));
                        oneGroupResDataMap.put("yicaijueCount", Integer.valueOf(yicaijueCount_all));
                        oneGroupResDataMap.put("weicaijueCount", Integer.valueOf(weicaijueCount_all));
                        oneGroupResDataMap.put("sanpingCount", Integer.valueOf(sanpingCount_all));
                        if (CollUtil.isNotEmpty(schGroupNumSanpingCountMapList_all)) {
                            oneGroupResDataMap.put("schGroupNumSanpingCountMapList", (List) ((Map) schGroupNumSanpingCountMapList_all.stream().collect(Collectors.groupingBy(m12 -> {
                                return m12.get("schoolGroupNum");
                            }))).values().stream().map(m23 -> {
                                Map<String, Object> map = (Map) m23.get(0);
                                map.put("sanpingCount", Integer.valueOf(m23.stream().mapToInt(m32 -> {
                                    return Convert.toInt(m32.get("sanpingCount"), 0).intValue();
                                }).sum()));
                                return map;
                            }).collect(Collectors.toList()));
                        }
                        oneGroupResDataMap.put("chongpanCount", Integer.valueOf(chongpanCount_all));
                        xuanzuotiResDataMapList.add(oneGroupResDataMap);
                    });
                    if (CollUtil.isNotEmpty(xuanzuotiResDataMapList)) {
                        tizuResDataMapList.addAll(xuanzuotiResDataMapList);
                    }
                    List<Map<String, Object>> putongtiResDataMapList = (List) questionGroupInfoList.stream().filter(m6 -> {
                        return !"1".equals(Convert.toStr(m6.get("xuazuoti")));
                    }).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(putongtiResDataMapList)) {
                        tizuResDataMapList.addAll(putongtiResDataMapList);
                    }
                    oneSubjectResDataMap.put("questionGroupProgress", tizuResDataMapList);
                    Map<String, Object> kemuResDataMap = new HashMap<>();
                    kemuResDataMap.put("examPaperNum", examPaperNum);
                    int totalCount_sub = 0;
                    int yipanCount_sub = 0;
                    int weipanCount_sub = 0;
                    int weicaijueCount_sub = 0;
                    int gLen = tizuResDataMapList.size();
                    for (int g = 0; g < gLen; g++) {
                        Map<String, Object> oneGroupDataMap = tizuResDataMapList.get(g);
                        totalCount_sub += Convert.toInt(oneGroupDataMap.get("totalCount"), 0).intValue();
                        int yipanCount2 = Convert.toInt(oneGroupDataMap.get("yipanCount"), 0).intValue();
                        yipanCount_sub += yipanCount2;
                        int weipanCount2 = Convert.toInt(oneGroupDataMap.get("weipanCount"), 0).intValue();
                        weipanCount_sub += weipanCount2;
                        int weicaijueCount2 = Convert.toInt(oneGroupDataMap.get("weicaijueCount"), 0).intValue();
                        weicaijueCount_sub += weicaijueCount2;
                        kemuResDataMap.put("totalCount", Integer.valueOf(totalCount_sub));
                        kemuResDataMap.put("yipanCount", Integer.valueOf(yipanCount_sub));
                        String baifenbi_sub = "--";
                        if (totalCount_sub > 0) {
                            baifenbi_sub = Convert.toStr(Integer.valueOf((yipanCount_sub * 100) / totalCount_sub));
                        }
                        kemuResDataMap.put("baifenbi", baifenbi_sub);
                        kemuResDataMap.put("weipanCount", Integer.valueOf(weipanCount_sub));
                        kemuResDataMap.put("weicaijueCount", Integer.valueOf(weicaijueCount_sub));
                    }
                    int weicaiqieCount = this.mdao.getWeicaiqieCount(examPaperNum);
                    kemuResDataMap.put("weicaiqieCount", Integer.valueOf(weicaiqieCount));
                    int weikaifangTizuCount = this.mdao.getWeikaifangTizuCount(examPaperNum);
                    kemuResDataMap.put("weikaifangTizuCount", Integer.valueOf(weikaifangTizuCount));
                    int weikaifangKaodianCount = this.mdao.getWeikaifangKaodianCount(examPaperNum);
                    kemuResDataMap.put("weikaifangKaodianCount", Integer.valueOf(weikaifangKaodianCount));
                    int KaodianCount = this.mdao.getKaodianCount(examPaperNum);
                    kemuResDataMap.put("KaodianCount", Integer.valueOf(KaodianCount));
                    oneSubjectResDataMap.put("subjectProgress", kemuResDataMap);
                    Map<String, List<Map<String, Object>>> teaWorkloadResDataMap = new HashMap<>();
                    Set<String> kemuYipanTeaSet = new HashSet<>();
                    tizuResDataMapList.forEach(oneTizuObj -> {
                        String groupNum2 = Convert.toStr(oneTizuObj.get("groupNum"));
                        String xuankaoqufen2 = Convert.toStr(oneTizuObj.get("xuankaoqufen"));
                        String makType2 = Convert.toStr(oneTizuObj.get("makType"));
                        String judgetype2 = Convert.toStr(oneTizuObj.get("judgetype"));
                        List<Map<String, Object>> teacherWorkloadInfoList = this.mdao.getTeacherWorkloadInfoList(groupNum2);
                        if (CollUtil.isEmpty(teacherWorkloadInfoList)) {
                            return;
                        }
                        Map<String, List<Map<String, Object>>> haveWorkloadTeaListMap = new HashMap<>();
                        List<Map<String, Object>> oneGroupHaveWorkloadTeaList = (List) teacherWorkloadInfoList.stream().filter(m7 -> {
                            return StrUtil.isNotEmpty(Convert.toStr(m7.get("num")));
                        }).collect(Collectors.toList());
                        if ("1".equals(isFenzu)) {
                            haveWorkloadTeaListMap = (Map) oneGroupHaveWorkloadTeaList.stream().collect(Collectors.groupingBy(m22 -> {
                                return Convert.toStr(m22.get("schoolGroupNum"), weifenzuNum);
                            }));
                        } else {
                            haveWorkloadTeaListMap.put(bufenzuNum, oneGroupHaveWorkloadTeaList);
                        }
                        Map<String, Integer> zhidingCountMap = new HashMap<>();
                        haveWorkloadTeaListMap.forEach((schoolGroupNum2, haveWorkloadTeaList) -> {
                            int num_all = 0;
                            int t1Len = haveWorkloadTeaList.size();
                            for (int t1 = 0; t1 < t1Len; t1++) {
                                Map<String, Object> oneTeaDataMap1 = (Map) haveWorkloadTeaList.get(t1);
                                String teacherNum = Convert.toStr(oneTeaDataMap1.get("teacherNum"));
                                int yudingCount = Convert.toInt(oneTeaDataMap1.get("num"), 0).intValue();
                                num_all += yudingCount;
                                int yipanCount3 = Convert.toInt(oneTeaDataMap1.get("yipanCount"), 0).intValue();
                                int weipanCount3 = yudingCount - yipanCount3;
                                oneTeaDataMap1.put("yudingCount", Integer.valueOf(yudingCount));
                                oneTeaDataMap1.put("weipanCount", Integer.valueOf(weipanCount3));
                                oneTeaDataMap1.put("weipanCount_paixu", Integer.valueOf(weipanCount3));
                                oneTeaDataMap1.put("yipanCount", Integer.valueOf(yipanCount3));
                                oneTeaDataMap1.put("yipanCount_sub", Integer.valueOf(yipanCount3));
                                if (yipanCount3 > 0) {
                                    kemuYipanTeaSet.add(teacherNum);
                                }
                            }
                            zhidingCountMap.put(schoolGroupNum2, Integer.valueOf(num_all));
                        });
                        Map<String, List<Map<String, Object>>> noWorkloadTeaListMap = new HashMap<>();
                        List<Map<String, Object>> oneGroupNoWorkloadTeaList = (List) teacherWorkloadInfoList.stream().filter(m8 -> {
                            return StrUtil.isEmpty(Convert.toStr(m8.get("num")));
                        }).collect(Collectors.toList());
                        if ("1".equals(isFenzu)) {
                            noWorkloadTeaListMap = (Map) oneGroupNoWorkloadTeaList.stream().collect(Collectors.groupingBy(m23 -> {
                                return Convert.toStr(m23.get("schoolGroupNum"), weifenzuNum);
                            }));
                        } else {
                            noWorkloadTeaListMap.put(bufenzuNum, oneGroupNoWorkloadTeaList);
                        }
                        if (CollUtil.isNotEmpty(noWorkloadTeaListMap)) {
                            noWorkloadTeaListMap.forEach((schoolGroupNum3, noWorkloadTeaList) -> {
                                int noWorkloadTeaCount = noWorkloadTeaList.size();
                                for (int t2 = 0; t2 < noWorkloadTeaCount; t2++) {
                                    Map<String, Object> oneTeaDataMap2 = (Map) noWorkloadTeaList.get(t2);
                                    String teacherNum = Convert.toStr(oneTeaDataMap2.get("teacherNum"));
                                    int cankaoCount = 0;
                                    if (CollUtil.isNotEmpty(bigTableMapExamina)) {
                                        if ("1".equals(isFenzu)) {
                                            if ("2".equals(xuankaoqufen2)) {
                                                cankaoCount = Convert.toInt(((Map) ((Map) bigTableMapExamina.get("examinaXuanIn")).get(examPaperNum)).get(schoolGroupNum3), 0).intValue();
                                            } else if ("3".equals(xuankaoqufen2)) {
                                                cankaoCount = Convert.toInt(((Map) ((Map) bigTableMapExamina.get("examinaXueIn")).get(examPaperNum)).get(schoolGroupNum3), 0).intValue();
                                            } else {
                                                cankaoCount = Convert.toInt(((Map) ((Map) bigTableMapExamina.get("examinaTotalIn")).get(examPaperNum)).get(schoolGroupNum3), 0).intValue();
                                            }
                                        } else if ("2".equals(xuankaoqufen2)) {
                                            cankaoCount = Convert.toInt(((Map) ((Map) bigTableMapExamina.get("examinaXuan")).get(examPaperNum)).get(bufenzuNum), 0).intValue();
                                        } else if ("3".equals(xuankaoqufen2)) {
                                            cankaoCount = Convert.toInt(((Map) ((Map) bigTableMapExamina.get("examinaXue")).get(examPaperNum)).get(bufenzuNum), 0).intValue();
                                        } else {
                                            cankaoCount = Convert.toInt(((Map) ((Map) bigTableMapExamina.get("examinaTotal")).get(examPaperNum)).get(bufenzuNum), 0).intValue();
                                        }
                                    }
                                    if ("1".equals(makType2)) {
                                        cankaoCount *= 2;
                                        if ("1".equals(judgetype2) || "2".equals(judgetype2)) {
                                            if ("1".equals(isFenzu)) {
                                                List<Map<String, Object>> schGroupNumSanpingCountMapList = (List) oneTizuObj.get("schGroupNumSanpingCountMapList");
                                                if (CollUtil.isNotEmpty(schGroupNumSanpingCountMapList)) {
                                                    Optional<Map<String, Object>> res = schGroupNumSanpingCountMapList.stream().filter(m9 -> {
                                                        return schoolGroupNum3.equals(Convert.toStr(m9.get("schoolGroupNum"), weifenzuNum));
                                                    }).findAny();
                                                    if (res.isPresent()) {
                                                        Map<String, Object> dataMap = res.get();
                                                        int sanpingCount2 = Convert.toInt(dataMap.get("sanpingCount"), 0).intValue();
                                                        cankaoCount += sanpingCount2;
                                                    }
                                                }
                                            } else {
                                                int sanpingCount3 = Convert.toInt(oneTizuObj.get("sanpingCount"), 0).intValue();
                                                cankaoCount += sanpingCount3;
                                            }
                                        }
                                    }
                                    int zhidingCount = 0;
                                    if (CollUtil.isNotEmpty(zhidingCountMap) && zhidingCountMap.containsKey(schoolGroupNum3)) {
                                        zhidingCount = ((Integer) zhidingCountMap.get(schoolGroupNum3)).intValue();
                                    }
                                    int shengyuCount = cankaoCount - zhidingCount;
                                    int yudingCount_avg = NumberUtil.ceilDiv(shengyuCount, noWorkloadTeaCount);
                                    int yipanCount3 = Convert.toInt(oneTeaDataMap2.get("yipanCount"), 0).intValue();
                                    int weipanCount3 = yudingCount_avg - yipanCount3;
                                    oneTeaDataMap2.put("yudingCount", Integer.valueOf(yudingCount_avg));
                                    oneTeaDataMap2.put("weipanCount", Integer.valueOf(weipanCount3));
                                    oneTeaDataMap2.put("weipanCount_paixu", Integer.valueOf(weipanCount3));
                                    oneTeaDataMap2.put("yipanCount", Integer.valueOf(yipanCount3));
                                    oneTeaDataMap2.put("yipanCount_sub", Integer.valueOf(yipanCount3));
                                    if (yipanCount3 > 0) {
                                        kemuYipanTeaSet.add(teacherNum);
                                    }
                                }
                            });
                        }
                        Optional<Map<String, Object>> eee = teacherWorkloadInfoList.stream().filter(m9 -> {
                            return StrUtil.isEmptyIfStr(m9.get("schoolnum"));
                        }).findAny();
                        if (eee.isPresent()) {
                        }
                        Map<String, List<Map<String, Object>>> sch_teaListMap = (Map) teacherWorkloadInfoList.stream().collect(Collectors.groupingBy(m10 -> {
                            return Convert.toStr(m10.get("schoolnum"));
                        }));
                        sch_teaListMap.forEach((schoolnum, oneSchTeaList) -> {
                            int weipanCount_sch = oneSchTeaList.stream().mapToInt(m11 -> {
                                return Convert.toInt(m11.get("weipanCount"), 0).intValue();
                            }).sum();
                            oneSchTeaList.forEach(oneSchOneTeaMap -> {
                                oneSchOneTeaMap.put("weipanCount_sch", Integer.valueOf(weipanCount_sch));
                            });
                        });
                        Map<String, List<Map<String, Object>>> groupTeaWorkloadTeaListMap = new HashMap<>();
                        if ("1".equals(isFenzu)) {
                            groupTeaWorkloadTeaListMap = (Map) teacherWorkloadInfoList.stream().collect(Collectors.groupingBy(m11 -> {
                                return Convert.toStr(m11.get("schoolGroupNum"), weifenzuNum);
                            }));
                        } else {
                            groupTeaWorkloadTeaListMap.put(bufenzuNum, teacherWorkloadInfoList);
                        }
                        groupTeaWorkloadTeaListMap.forEach((schoolGroupNum4, oneGroupTeaList) -> {
                            int totalCount_reg = 0;
                            if (CollUtil.isNotEmpty(bigTableMapReg)) {
                                if ("1".equals(isFenzu)) {
                                    if ("2".equals(xuankaoqufen2)) {
                                        totalCount_reg = Convert.toInt(((Map) ((Map) bigTableMapReg.get("regXuanIn")).get(examPaperNum)).get(schoolGroupNum4), 0).intValue();
                                    } else if ("3".equals(xuankaoqufen2)) {
                                        totalCount_reg = Convert.toInt(((Map) ((Map) bigTableMapReg.get("regXueIn")).get(examPaperNum)).get(schoolGroupNum4), 0).intValue();
                                    } else {
                                        totalCount_reg = Convert.toInt(((Map) ((Map) bigTableMapReg.get("regTotalIn")).get(examPaperNum)).get(schoolGroupNum4), 0).intValue();
                                    }
                                } else if ("2".equals(xuankaoqufen2)) {
                                    totalCount_reg = Convert.toInt(((Map) ((Map) bigTableMapReg.get("regXuan")).get(examPaperNum)).get(bufenzuNum), 0).intValue();
                                } else if ("3".equals(xuankaoqufen2)) {
                                    totalCount_reg = Convert.toInt(((Map) ((Map) bigTableMapReg.get("regXue")).get(examPaperNum)).get(bufenzuNum), 0).intValue();
                                } else {
                                    totalCount_reg = Convert.toInt(((Map) ((Map) bigTableMapReg.get("regTotal")).get(examPaperNum)).get(bufenzuNum), 0).intValue();
                                }
                            }
                            if ("1".equals(makType2)) {
                                totalCount_reg *= 2;
                                if ("1".equals(judgetype2) || "2".equals(judgetype2)) {
                                    if ("1".equals(isFenzu)) {
                                        List<Map<String, Object>> schGroupNumSanpingCountMapList = (List) oneTizuObj.get("schGroupNumSanpingCountMapList");
                                        if (CollUtil.isNotEmpty(schGroupNumSanpingCountMapList)) {
                                            Optional<Map<String, Object>> res = schGroupNumSanpingCountMapList.stream().filter(m12 -> {
                                                return schoolGroupNum4.equals(Convert.toStr(m12.get("schoolGroupNum"), weifenzuNum));
                                            }).findAny();
                                            if (res.isPresent()) {
                                                Map<String, Object> dataMap = res.get();
                                                int sanpingCount2 = Convert.toInt(dataMap.get("sanpingCount"), 0).intValue();
                                                totalCount_reg += sanpingCount2;
                                            }
                                        }
                                    } else {
                                        int sanpingCount3 = Convert.toInt(oneTizuObj.get("sanpingCount"), 0).intValue();
                                        totalCount_reg += sanpingCount3;
                                    }
                                }
                            }
                            int yipanCount_schoolgroup = oneGroupTeaList.stream().mapToInt(m13 -> {
                                return Convert.toInt(m13.get("yipanCount")).intValue();
                            }).sum();
                            int weipanCount_schoolgroup = totalCount_reg - yipanCount_schoolgroup;
                            oneGroupTeaList.forEach(oneTeaDataMap -> {
                                int yudingCount = Convert.toInt(oneTeaDataMap.get("yudingCount")).intValue();
                                int weipanCount3 = Convert.toInt(oneTeaDataMap.get("weipanCount")).intValue();
                                int weipanCount_sch = Convert.toInt(oneTeaDataMap.get("weipanCount_sch")).intValue();
                                String assister = Convert.toStr(oneTeaDataMap.get("assister"));
                                if (weipanCount_schoolgroup <= 0 && weipanCount3 > 0) {
                                    oneTeaDataMap.put("weipanCount_paixu", -99999);
                                }
                                if ("1".equals(assister)) {
                                    oneTeaDataMap.put("yudingCount", "----");
                                    oneTeaDataMap.put("weipanCount", "----");
                                    return;
                                }
                                if (yudingCount < 0) {
                                    oneTeaDataMap.put("yudingCount", "0");
                                    oneTeaDataMap.put("weipanCount", "0");
                                    return;
                                }
                                if ((weipanCount_schoolgroup <= 0 || weipanCount_sch <= 0) && weipanCount3 > 0 && "1".equals(isFenzu)) {
                                    oneTeaDataMap.put("weipanCount", "----");
                                }
                                if (weipanCount_schoolgroup <= 0 && weipanCount_sch > 0 && "1".equals(isFenzu)) {
                                    oneTeaDataMap.put("weipanCount_sch", "----");
                                }
                            });
                        });
                        teaWorkloadResDataMap.put(groupNum2, teacherWorkloadInfoList);
                    });
                    teaWorkloadResDataMap.forEach((groupNum2, teacherWorkloadInfoList) -> {
                        teacherWorkloadInfoList.stream().filter(m7 -> {
                            return Convert.toInt(m7.get("yipanCount_sub")).intValue() <= 0;
                        }).forEach(oneTeaDataMap -> {
                            String teacherNum = Convert.toStr(oneTeaDataMap.get("teacherNum"));
                            if (kemuYipanTeaSet.contains(teacherNum)) {
                                oneTeaDataMap.put("yipanCount_sub", 1);
                            }
                        });
                    });
                    oneSubjectResDataMap.put("teaWorkloadProgress", teaWorkloadResDataMap);
                    yuejuanProgressMap.put(examPaperNum, oneSubjectResDataMap);
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return yuejuanProgressMap;
    }

    public List<QuestionGroup_question> getQuestionGroupSD2(Integer examnum, Integer gradeNum, Integer subjectNum, String examPaperNum) {
        try {
            Map args = new HashMap();
            args.put("examPaperNum", examPaperNum);
            return this.dao2._queryBeanList("SELECT t.*,ifnull(q.count,0) ext5,ifNUll(t1.count,0) ext6 from(SELECT hh.exampaperNum,hh.ext1,hh.ext2,hh.ext10,sum(hh.ext3) ext3,sum(hh.ext4) ext4,hh.questionName from( SELECT z.exampaperNum,su.subjectNum ext1,su.subjectName ext2,su.orderNum ext10,   case     when d.choosename<>'s' then IFNULL(tt.count,0)     when d.choosename='s' and z.makType=1 then IFNULL(r.count*2,0)+IFNULL(x.count,0)     when d.choosename='s' and z.makType=0 then IFNULL(r.count,0)      end ext3,  IFNULL(y.d_num ,0) ext4,   IF(t.groupNum is NULL ,'T','F') questionName FROM(   \t\t\tSELECT s.groupNum,s.groupName,s.groupType,s.exampaperNum,m.makType from questiongroup s LEFT JOIN questiongroup_mark_setting m on m.groupNum=s.groupNum  \t\t\t where s.exampaperNum={examPaperNum}   )z    LEFT JOIN (  \t\t\tSELECT t.groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num     \t\t\tFROM task t   WHERE t.exampapernum={examPaperNum}  AND t.status='T'  GROUP BY groupNum    )y ON z.groupNum = y.groupNum LEFT JOIN (   \t\t\tSELECT groupNum,count(distinct studentId) count FROM task t  WHERE t.exampapernum={examPaperNum}   and t.userNum=3  GROUP BY groupNum   )x ON z.groupNum = x.groupNum    LEFT JOIN (  \t\tSELECT id,choosename from define d  WHERE d.exampapernum={examPaperNum}     UNION      SELECT sb.id,d.choosename from define d LEFT JOIN subdefine sb on sb.pid=d.id  WHERE d.exampapernum={examPaperNum}  \t) d on z.groupNum=d.id     LEFT JOIN (  \t\tselect d.id,r.dd count from(  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d  \t\t\tINNER JOIN exampaper e on d.category=e.examPaperNum where d.exampapernum={examPaperNum}  and d.questionType=1   \t\t\tUNION\t  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category   \t\t\tfrom subdefine d  INNER JOIN exampaper e on d.category=e.examPaperNum   \t\t\twhere d.exampapernum={examPaperNum}  and d.questionType=1  \t\t)d LEFT JOIN(  \t\t\tSELECT count(DISTINCT r.studentId) dd,r.exampaperNum ext1 from regexaminee r        WHERE  r.exampapernum={examPaperNum}   and   r.scan_import=0  GROUP BY r.exampaperNum  \t\t\tunion  \t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampapernum from regexaminee r  \t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE r.exampaperNum={examPaperNum}   and r.scan_import=0  \t\t\tUNION\t  \t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampaperNum from regexaminee r  LEFT JOIN(  \t\t\t\tSELECT r.studentId,r.exampapernum from regexaminee r  \t\t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \t\t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE r.exampaperNum={examPaperNum}   and r.scan_import=0  \t\t\t)r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum  \t\t\tINNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3  \t\t\tWHERE r.exampaperNum={examPaperNum}  and r.scan_import=0 and r1.studentId is null  \t\t)r on d.category=r.ext1   ) r on z.groupNum=r.id LEFT JOIN ( \t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed) count from task t       WHERE t.exampaperNum={examPaperNum}   GROUP BY groupNum )tt on z.groupNum=tt.groupNum   LEFT JOIN (  \t\tselect  DISTINCT q.groupNum FROM (  \t\t\tSELECT groupNum,questionNum FROM questiongroup_question qq  WHERE qq.exampaperNum={examPaperNum}  GROUP BY groupNum    \t)q LEFT JOIN (   \t\tselect DISTINCT questionNum  FROM remark r  where r.type='1' AND r.STATUS='F' AND r.exampaperNum={examPaperNum}  \t\t) tt ON q.questionNum = tt.questionNum where tt.questionNum is not null   )t  ON z.groupNum = t.groupNum LEFT JOIN exampaper epp on z.examPaperNum=epp.examPaperNum LEFT JOIN subject su on epp.subjectnum=su.subjectNum WHERE z.groupNum IS NOT NULL)hh GROUP BY hh.examPaperNum)t LEFT JOIN ( \t\tSELECT q.examPapernum,count(1) count from questiongroup q \t\twhere q.examPapernum={examPaperNum}  and q.stat=0 GROUP BY q.examPaperNum )q on t.examPaperNum=q.exampapernum LEFT JOIN( \tSELECT t.examPaperNum,count(1) count from testingcentredis t LEFT JOIN exampaper e on t.examPapernum=e.examPaperNum INNER join testingcentre tc on t.testingCentreId=tc.id and e.examNum=tc.examNum \t\twhere t.examPapernum={examPaperNum}  and t.isDis=0 GROUP BY t.examPaperNum )t1 on t.examPaperNum=t1.exampapernum order by t.ext10*1", QuestionGroup_question.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<QuestionGroup_question> getQuestionGroupSD1(Integer examnum, Integer gradeNum) {
        try {
            Map args = new HashMap();
            args.put("examnum", examnum);
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            return this.dao2._queryBeanList("SELECT t.*,ifnull(q.count,0) ext5,ifNUll(t1.count,0) ext6 from(SELECT hh.exampaperNum,hh.ext1,hh.ext2,hh.ext10,sum(hh.ext3) ext3,sum(hh.ext4) ext4,hh.questionName from( SELECT z.exampaperNum,su.subjectNum ext1,su.subjectName ext2,su.orderNum ext10,   case     when d.choosename<>'s' then IFNULL(tt.count,0)     when d.choosename='s' and z.makType=1 then IFNULL(r.count*2,0)+IFNULL(x.count,0)     when d.choosename='s' and z.makType=0 then IFNULL(r.count,0)      end ext3,  IFNULL(y.d_num ,0) ext4,   IF(t.groupNum is NULL ,'T','F') questionName FROM(   \t\t\tSELECT s.groupNum,s.groupName,s.groupType,s.exampaperNum,m.makType from questiongroup s LEFT JOIN questiongroup_mark_setting m on m.groupNum=s.groupNum  \t\t\tLEFT JOIN exampaper e on s.exampapernum=e.exampapernum where e.examNum={examnum}  and e.gradeNum={gradeNum}   )z    LEFT JOIN (  \t\t\tSELECT t.groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num     \t\t\tFROM task t LEFT JOIN exampaper e on t.exampapernum=e.exampapernum WHERE e.examNum={examnum}  and e.gradeNum={gradeNum} AND t.status='T'  GROUP BY groupNum    )y ON z.groupNum = y.groupNum LEFT JOIN (   \t\t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as count FROM task t LEFT JOIN exampaper e on t.exampapernum=e.exampapernum WHERE e.examNum={examnum}  and e.gradeNum={gradeNum} and t.userNum=3  GROUP BY groupNum   )x ON z.groupNum = x.groupNum    LEFT JOIN (  \t\tSELECT id,choosename from define d LEFT JOIN exampaper e on d.exampapernum=e.exampapernum WHERE e.examNum={examnum}  and e.gradeNum={gradeNum}     UNION      SELECT sb.id,d.choosename from define d LEFT JOIN exampaper e on d.exampapernum=e.exampapernum LEFT JOIN subdefine sb on sb.pid=d.id  WHERE e.examNum={examnum}  and e.gradeNum={gradeNum}  \t) d on z.groupNum=d.id     LEFT JOIN (  \t\tselect d.id,r.dd count from(  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category from define d  \t\t\tLEFT JOIN exampaper ep on d.exampapernum=ep.exampapernum  \t\t\tINNER JOIN exampaper e on d.category=e.examPaperNum where ep.examNum={examnum}  and ep.gradeNum={gradeNum} and d.questionType=1   \t\t\tUNION\t  \t\t\tSELECT d.id,d.examPaperNum,CASE WHEN (e.xuankaoqufen=2 or e.xuankaoqufen=3) then d.category else d.examPaperNum end category   \t\t\tfrom subdefine d LEFT JOIN exampaper ep on d.exampapernum=ep.exampapernum INNER JOIN exampaper e on d.category=e.examPaperNum   \t\t\twhere ep.examNum={examnum}  and ep.gradeNum={gradeNum} and d.questionType=1  \t\t)d LEFT JOIN(  \t\t\tSELECT count(DISTINCT r.studentId) dd,r.exampaperNum ext1 from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum       WHERE  e.examNum={examnum}  and e.gradeNum={gradeNum}  and   r.scan_import=0  GROUP BY r.exampaperNum  \t\t\tunion  \t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampapernum from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum \t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE e.examNum={examnum}  and e.gradeNum={gradeNum}  and r.scan_import=0 GROUP BY e1.exampaperNum  \t\t\tUNION\t  \t\t\tSELECT count(DISTINCT r.studentId) dd,e1.exampaperNum from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum LEFT JOIN(  \t\t\t\tSELECT r.studentId,r.exampapernum from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum \t\t\t\tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \t\t\t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE e.examNum={examnum}  and e.gradeNum={gradeNum}  and r.scan_import=0  \t\t\t)r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum  \t\t\tINNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3  \t\t\tWHERE e.examNum={examnum}  and e.gradeNum={gradeNum}  and r.scan_import=0 and r1.studentId is null GROUP BY e1.exampaperNum  \t\t)r on d.category=r.ext1   ) r on z.groupNum=r.id LEFT JOIN ( \t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed) count from task t LEFT JOIN exampaper e on t.exampapernum=e.exampapernum      WHERE e.examNum={examnum}  and e.gradeNum={gradeNum} GROUP BY groupNum )tt on z.groupNum=tt.groupNum   LEFT JOIN (  \t\tselect  DISTINCT q.groupNum FROM (  \t\t\tSELECT groupNum,questionNum FROM questiongroup_question qq LEFT JOIN exampaper e on qq.exampapernum=e.exampapernum WHERE e.examNum={examnum}  and e.gradeNum={gradeNum} GROUP BY groupNum    \t)q LEFT JOIN (   \t\tselect DISTINCT questionNum  FROM remark r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum where r.type='1' AND r.STATUS='F' AND e.examNum={examnum}  and e.gradeNum={gradeNum}  \t\t) tt ON q.questionNum = tt.questionNum where tt.questionNum is not null   )t  ON z.groupNum = t.groupNum LEFT JOIN exampaper epp on z.examPaperNum=epp.examPaperNum LEFT JOIN subject su on epp.subjectnum=su.subjectNum WHERE z.groupNum IS NOT NULL)hh GROUP BY hh.examPaperNum)t LEFT JOIN ( \t\tSELECT q.examPapernum,count(1) count from questiongroup q LEFT JOIN exampaper e on q.examPapernum=e.examPaperNum \t\twhere e.examNum={examnum}  and e.gradeNum={gradeNum} and q.stat=0 GROUP BY q.examPaperNum )q on t.examPaperNum=q.exampapernum LEFT JOIN( \tSELECT t.examPaperNum,count(1) count from testingcentredis t LEFT JOIN exampaper e on t.examPapernum=e.examPaperNum INNER join testingcentre tc on t.testingCentreId=tc.id and e.examNum=tc.examNum \t\twhere e.examNum={examnum}  and e.gradeNum={gradeNum} and t.isDis=0 GROUP BY t.examPaperNum )t1 on t.examPaperNum=t1.exampapernum order by t.ext10*1", QuestionGroup_question.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getSpotCheckExam(String userId) {
        if ("-2".equals(userId) || "-1".equals(userId)) {
            return this.dao2.queryBeanList("select distinct e.examNum num,e.examName name FROM exam e left join exampaper ep on ep.examNum = e.examNum where e.isDelete='F' and e.status<>'9' and ep.type='0' and ep.isHidden='F' order by e.examDate desc,e.insertDate desc", AjaxData.class);
        }
        Map args = StreamMap.create().put("userId", (Object) userId);
        return this.dao2._queryBeanList("select distinct e.examNum num,e.examName name FROM exam e left join exampaper ep on ep.examNum = e.examNum left join questiongroup_user qgu on qgu.exampaperNum = ep.examPaperNum where e.isDelete='F' and e.status<>'9' and ep.type='0' and qgu.userNum={userId}  and qgu.userType <> '0' order by e.examDate desc,e.insertDate desc", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getSpotCheckSubject(String examNum, String userId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("userId", userId);
        if ("-2".equals(userId) || "-1".equals(userId)) {
            return this.dao2._queryBeanList("select distinct sub.subjectNum num,sub.subjectName name FROM `subject` sub left join exampaper ep on ep.subjectNum = sub.subjectNum where ep.examNum={examNum} and ep.type='0' and ep.isHidden='F'  order by sub.orderNum,sub.subjectNum", AjaxData.class, args);
        }
        return this.dao2._queryBeanList("select distinct sub.subjectNum num,sub.subjectName name FROM exampaper ep left join questiongroup_user qgu on qgu.exampaperNum = ep.examPaperNum left join exampaper ep2 on ep2.examPaperNum = ep.pexamPaperNum left join `subject` sub on sub.subjectNum = ep2.subjectNum where ep.examNum={examNum} and ep.type='0' and qgu.userNum={userId}  and qgu.userType <> '0' and ep2.examPaperNum is not null and sub.subjectNum is not null order by sub.orderNum,sub.subjectNum", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getSpotCheckGrade(String examNum, String subjectNum, String userId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("userId", userId);
        if ("-2".equals(userId) || "-1".equals(userId)) {
            return this.dao2._queryBeanList("select distinct bg.gradeNum num,bg.gradeName name FROM basegrade bg left join exampaper ep on ep.gradeNum = bg.gradeNum where ep.examNum={examNum} and ep.subjectNum={subjectNum} and ep.type='0' and ep.isHidden='F' order by bg.gradeNum", AjaxData.class, args);
        }
        return this.dao2._queryBeanList("select distinct bg.gradeNum num,bg.gradeName name FROM basegrade bg left join exampaper ep on ep.gradeNum = bg.gradeNum left join questiongroup_user qgu on qgu.exampaperNum = ep.examPaperNum left join exampaper ep2 on ep2.examPaperNum = ep.pexamPaperNum where ep.examNum={examNum} and ep2.subjectNum={subjectNum} and ep.type='0' and qgu.userNum={userId}  and qgu.userType <> '0' order by bg.gradeNum", AjaxData.class, args);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getSpotCheckQuestion(String examNum, String subjectNum, String gradeNum, String userId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("userId", userId);
        Object pexamPaperNum = this.dao2._queryObject("select ep.pexamPaperNum from exampaper ep where ep.examNum={examNum} and ep.gradeNum={gradeNum} and ep.subjectNum={subjectNum} ", args);
        args.put("pexamPaperNum", pexamPaperNum);
        String userType = this.dao2._queryStr("select userType from questiongroup_user where userNum={userId}  and exampaperNum={pexamPaperNum} order by usertype desc limit 1", args);
        List list = new ArrayList();
        if (userId.equals("-2") || userId.equals("-1") || "2".equals(userType)) {
            list = this.dao2._queryBeanList("(SELECT IFNULL(subd.id,d.id) num,IFNULL(subd.questionNum,d.questionNum) name,'1' ext1,qg.groupNum ext3 from questiongroup_question qg LEFT JOIN define d on d.id = qg.questionNum LEFT JOIN subdefine subd on subd.id = qg.questionNum LEFT JOIN define d2 on d2.id = subd.pid where qg.exampaperNum = {pexamPaperNum} ORDER BY IFNULL(d.orderNum,d2.orderNum),subd.orderNum) UNION all (SELECT d.id num,d.questionNum name,'2' ext1,d.id ext3 from questiongroup qg LEFT JOIN define d on d.id = qg.groupNum where qg.exampaperNum = {pexamPaperNum} and qg.groupType = '2' ORDER BY d.orderNum) ORDER BY ext1,1", AjaxData.class, args);
        } else {
            list.addAll(this.dao2._queryBeanList("select distinct qq.questionNum num,t.qNum name,'1' ext1,t.orderNum ext2,qq.groupNum ext3 from questiongroup_user qu LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id ,CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum ,CASE WHEN d.isParent ='0' THEN d.category ELSE s.category  END AS category ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.pid  END AS groupNum from define d LEFT JOIN subdefine s ON d.id = s.pid where d.exampaperNum={pexamPaperNum} ) t on t.category = qu.exampaperNum LEFT JOIN questiongroup_question qq on qq.questionNum = t.id where qq.exampaperNum={pexamPaperNum} and qu.userType='2' and qu.userNum={userId}  group by qq.questionNum ORDER BY t.orderNum", AjaxData.class, args));
            list.addAll(this.dao2._queryBeanList("SELECT d.id num,d.questionNum name,'2' ext1,d.orderNum*1000 ext2,d.id ext3 from questiongroup qg inner JOIN define d on d.id = qg.groupNum inner JOIN questiongroup_user qgu on qgu.exampaperNum = d.category where qg.exampaperNum = {pexamPaperNum} and qg.groupType = '2' and qgu.userNum={userId}  and qgu.userType='2' ORDER BY d.orderNum ", AjaxData.class, args));
            if ("1".equals(userType)) {
                list.addAll(this.dao2._queryBeanList("select distinct qq.questionNum num,t.qNum name,'1' ext1,t.orderNum ext2,qq.groupNum ext3 from questiongroup_user qu LEFT JOIN questiongroup_question qq on qu.groupNum = qq.groupNum LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id ,CASE WHEN d.isParent ='0' THEN d.orderNum*1000 ELSE d.orderNum*1000+s.orderNum  END AS orderNum ,CASE WHEN d.isParent ='0' THEN d.id ELSE s.pid  END AS groupNum from define d LEFT JOIN subdefine s ON d.id = s.pid where d.exampaperNum={pexamPaperNum}) t on qq.questionNum = t.id where qq.exampaperNum={pexamPaperNum} and qu.userType='1' and qu.userNum={userId}  group by qq.questionNum ORDER BY t.orderNum", AjaxData.class, args));
                list.addAll(this.dao2._queryBeanList("SELECT d.id num,d.questionNum name,'2' ext1,d.orderNum*1000 ext2,d.id ext3 from questiongroup qg inner JOIN define d on d.id = qg.groupNum inner JOIN questiongroup_user qgu on qgu.groupNum = qg.groupNum where qg.exampaperNum = {pexamPaperNum} and qg.groupType = '2' and qgu.userNum={userId}  and qgu.userType='1' ORDER BY d.orderNum ", AjaxData.class, args));
                list.sort(Comparator.comparing((v0) -> {
                    return v0.getExt1();
                }).thenComparing((v0) -> {
                    return v0.getExt2();
                }));
            }
        }
        return list;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getSpotCheckTeacher(String groupNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        return this.dao2._queryBeanList("select distinct qgu.userNum num,u.realname name from questiongroup_user qgu left join `user` u on u.id = qgu.userNum where qgu.groupNum in ({groupNum[]}) and u.id is not null order by convert(u.realname using gbk)", AjaxData.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Map<String, Map<String, Map<String, Integer>>> getDataFormBigTableReg() {
        Map<String, Map<String, Map<String, Integer>>> map = new HashMap<>();
        Map<String, Map<String, Integer>> regTotal_map = new HashMap<>();
        Map<String, Map<String, Integer>> regXuan_map = new HashMap<>();
        Map<String, Map<String, Integer>> regXue_map = new HashMap<>();
        Map<String, Map<String, Integer>> regTotalIn_map = new HashMap<>();
        Map<String, Map<String, Integer>> regXuanIn_map = new HashMap<>();
        Map<String, Map<String, Integer>> regXueIn_map = new HashMap<>();
        try {
            List examList = this.dao2.queryColList("SELECT examNum from exam where status <>9 and isDelete='F' ");
            for (int h = 0; h < examList.size(); h++) {
                String examNum = String.valueOf(examList.get(h));
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.exampaperNum,-1 ext1,count( DISTINCT r.studentId) ext2 from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum  WHERE  e.examnum={examNum}  and   r.scan_import=0  GROUP BY r.exampaperNum )p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList.size() > 0) {
                    Map<String, Integer> reg_map = new HashMap<>();
                    int examPaperNum = ((RegExaminee) _queryBeanList.get(0)).getExamPaperNum();
                    for (int i = 0; i < _queryBeanList.size(); i++) {
                        RegExaminee reg = (RegExaminee) _queryBeanList.get(i);
                        if (examPaperNum != reg.getExamPaperNum()) {
                            regTotal_map.put(String.valueOf(examPaperNum), reg_map);
                            reg_map = new HashMap<>();
                            examPaperNum = reg.getExamPaperNum();
                        }
                        reg_map.put(reg.getExt1(), Integer.valueOf(Integer.parseInt(reg.getExt2())));
                        if (i == _queryBeanList.size() - 1) {
                            regTotal_map.put(String.valueOf(examPaperNum), reg_map);
                        }
                    }
                }
                List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.exampaperNum,-1 ext1,count(DISTINCT r.studentId) ext2 from regexaminee r  INNER JOIN student s on r.studentId=s.id   INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  INNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum  WHERE  e1.examnum={examNum}  and  r.scan_import=0  GROUP BY r.exampaperNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList2.size() > 0) {
                    Map<String, Integer> reg_map2 = new HashMap<>();
                    int examPaperNum2 = ((RegExaminee) _queryBeanList2.get(0)).getExamPaperNum();
                    for (int i2 = 0; i2 < _queryBeanList2.size(); i2++) {
                        RegExaminee reg2 = (RegExaminee) _queryBeanList2.get(i2);
                        if (examPaperNum2 != reg2.getExamPaperNum()) {
                            regXuan_map.put(String.valueOf(examPaperNum2), reg_map2);
                            reg_map2 = new HashMap<>();
                            examPaperNum2 = reg2.getExamPaperNum();
                        }
                        reg_map2.put(reg2.getExt1(), Integer.valueOf(Integer.parseInt(reg2.getExt2())));
                        if (i2 == _queryBeanList2.size() - 1) {
                            regXuan_map.put(String.valueOf(examPaperNum2), reg_map2);
                        }
                    }
                }
                List<?> _queryBeanList3 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.exampaperNum,-1 ext1,count( DISTINCT r.studentId) ext2 from regexaminee r LEFT JOIN( \tSELECT r.studentId,r.exampapernum from regexaminee r  \tINNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum \tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE e1.examnum={examNum}  and r.scan_import=0 )r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum  INNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3 WHERE e1.examnum={examNum}  and r.scan_import=0 and r1.studentId is null GROUP BY r.exampaperNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList3.size() > 0) {
                    Map<String, Integer> reg_map3 = new HashMap<>();
                    int examPaperNum3 = ((RegExaminee) _queryBeanList3.get(0)).getExamPaperNum();
                    for (int i3 = 0; i3 < _queryBeanList3.size(); i3++) {
                        RegExaminee reg3 = (RegExaminee) _queryBeanList3.get(i3);
                        if (examPaperNum3 != reg3.getExamPaperNum()) {
                            regXue_map.put(String.valueOf(examPaperNum3), reg_map3);
                            reg_map3 = new HashMap<>();
                            examPaperNum3 = reg3.getExamPaperNum();
                        }
                        reg_map3.put(reg3.getExt1(), Integer.valueOf(Integer.parseInt(reg3.getExt2())));
                        if (i3 == _queryBeanList3.size() - 1) {
                            regXue_map.put(String.valueOf(examPaperNum3), reg_map3);
                        }
                    }
                }
                List<?> _queryBeanList4 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.exampaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(DISTINCT r.studentId),0) ext2 from regexaminee r LEFT JOIN exampaper e on r.exampapernum=e.exampapernum  INNER JOIN schoolgroup slg on r.schoolNum=slg.schoolNum  WHERE e.examnum={examNum}  and r.scan_import=0  GROUP BY r.exampaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList4.size() > 0) {
                    Map<String, Integer> reg_map4 = new HashMap<>();
                    int examPaperNum4 = ((RegExaminee) _queryBeanList4.get(0)).getExamPaperNum();
                    for (int i4 = 0; i4 < _queryBeanList4.size(); i4++) {
                        RegExaminee reg4 = (RegExaminee) _queryBeanList4.get(i4);
                        if (examPaperNum4 != reg4.getExamPaperNum()) {
                            regTotalIn_map.put(String.valueOf(examPaperNum4), reg_map4);
                            reg_map4 = new HashMap<>();
                            examPaperNum4 = reg4.getExamPaperNum();
                        }
                        reg_map4.put(reg4.getExt1(), Integer.valueOf(Integer.parseInt(reg4.getExt2())));
                        if (i4 == _queryBeanList4.size() - 1) {
                            regTotalIn_map.put(String.valueOf(examPaperNum4), reg_map4);
                        }
                    }
                }
                List<?> _queryBeanList5 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.exampaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(DISTINCT r.studentId),0) ext2 from regexaminee r  INNER JOIN student s on r.studentId=s.id   INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  INNER JOIN schoolgroup slg on r.schoolNum=slg.schoolNum   INNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum  WHERE e1.examnum={examNum} and r.scan_import=0 GROUP BY r.exampaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList5.size() > 0) {
                    Map<String, Integer> reg_map5 = new HashMap<>();
                    int examPaperNum5 = ((RegExaminee) _queryBeanList5.get(0)).getExamPaperNum();
                    for (int i5 = 0; i5 < _queryBeanList5.size(); i5++) {
                        RegExaminee reg5 = (RegExaminee) _queryBeanList5.get(i5);
                        if (examPaperNum5 != reg5.getExamPaperNum()) {
                            regXuanIn_map.put(String.valueOf(examPaperNum5), reg_map5);
                            reg_map5 = new HashMap<>();
                            examPaperNum5 = reg5.getExamPaperNum();
                        }
                        reg_map5.put(reg5.getExt1(), Integer.valueOf(Integer.parseInt(reg5.getExt2())));
                        if (i5 == _queryBeanList5.size() - 1) {
                            regXuanIn_map.put(String.valueOf(examPaperNum5), reg_map5);
                        }
                    }
                }
                List<?> _queryBeanList6 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT r.examPaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(DISTINCT r.studentId),0) ext2 from( \tSELECT r.examPaperNum,r.schoolNum,r.studentId from regexaminee r  \tLEFT JOIN( \t\tSELECT r.studentId,r.exampapernum from regexaminee r INNER JOIN student s on r.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum \t\tINNER JOIN exampaper e1 on sd.subjectNum=e1.subjectNum and r.examPaperNum=e1.pexampaperNum WHERE e1.examNum={examNum}  and r.scan_import=0 \t)r1 on r.studentId=r1.studentId and r.exampapernum=r1.exampapernum  \tINNER JOIN exampaper e1 on r.examPaperNum=e1.pexamPaperNum and e1.xuankaoqufen=3 \tWHERE e1.examNum={examNum}   and r.scan_import=0 and r1.studentId is null )r INNER JOIN schoolgroup slg on r.schoolNum=slg.schoolNum  GROUP BY r.examPaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList6.size() > 0) {
                    Map<String, Integer> reg_map6 = new HashMap<>();
                    int examPaperNum6 = ((RegExaminee) _queryBeanList6.get(0)).getExamPaperNum();
                    for (int i6 = 0; i6 < _queryBeanList6.size(); i6++) {
                        RegExaminee reg6 = (RegExaminee) _queryBeanList6.get(i6);
                        if (examPaperNum6 != reg6.getExamPaperNum()) {
                            regXueIn_map.put(String.valueOf(examPaperNum6), reg_map6);
                            reg_map6 = new HashMap<>();
                            examPaperNum6 = reg6.getExamPaperNum();
                        }
                        reg_map6.put(reg6.getExt1(), Integer.valueOf(Integer.parseInt(reg6.getExt2())));
                        if (i6 == _queryBeanList6.size() - 1) {
                            regXueIn_map.put(String.valueOf(examPaperNum6), reg_map6);
                        }
                    }
                }
            }
            map.put("regTotal", regTotal_map);
            map.put("regTotalIn", regTotalIn_map);
            map.put("regXuan", regXuan_map);
            map.put("regXuanIn", regXuanIn_map);
            map.put("regXue", regXue_map);
            map.put("regXueIn", regXueIn_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Map<String, Map<String, Integer>> getDataFormBigTableRegChoose() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> regChooseTotal_map = new HashMap<>();
        Map<String, Integer> regChooseTotalIn_map = new HashMap<>();
        Map<String, Integer> regChooseXuan_map = new HashMap<>();
        Map<String, Integer> regChooseXuanIn_map = new HashMap<>();
        Map<String, Integer> regChooseXue_map = new HashMap<>();
        Map<String, Integer> regChooseXueIn_map = new HashMap<>();
        try {
            List examList = this.dao2.queryColList("SELECT examNum from exam where status <>9 and isDelete='F'");
            for (int h = 0; h < examList.size(); h++) {
                String examNum = String.valueOf(examList.get(h));
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList = this.dao2._queryBeanList("SELECT d.id,d.choosename,d.questionnum,d.examPaperNum from exam e LEFT JOIN exampaper ep on e.examNum=ep.examNum  LEFT JOIN define d on ep.examPaperNum=d.examPaperNum  where e.examNum={examNum}  and d.choosename='T' ", Define.class, args);
                for (int i = 0; i < _queryBeanList.size(); i++) {
                    Define d = (Define) _queryBeanList.get(i);
                    Map args1 = new HashMap();
                    args1.put("Id", d.getId());
                    args1.put("ExamPaperNum", Integer.valueOf(d.getExamPaperNum()));
                    Object[] qinfo = this.dao2._queryArray("SELECT isParent,merge from define where choosename={Id} ", args1);
                    String isParent = String.valueOf(qinfo[0]);
                    String merge = String.valueOf(qinfo[1]);
                    if ("1".equals(merge)) {
                        isParent = "0";
                    }
                    if ("0".equals(isParent)) {
                        List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',d.id) id,cast(count(1)/count(DISTINCT(t.questionNum)) as signed) ext1 from define d LEFT JOIN task t on d.id=t.groupNum where d.examPaperNum={ExamPaperNum} and d.choosename={Id} GROUP BY d.id", Task.class, args1);
                        for (int j = 0; j < _queryBeanList2.size(); j++) {
                            Task t = (Task) _queryBeanList2.get(j);
                            String ext1 = t.getExt1() == null ? "0" : t.getExt1();
                            regChooseTotal_map.put(t.getId(), Integer.valueOf(Integer.parseInt(ext1)));
                        }
                        List<?> _queryBeanList3 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',d.id) id,cast(count(1)/count(DISTINCT(t.questionNum)) as signed) ext1 from define d LEFT JOIN task t on d.id=t.groupNum INNER JOIN student s ON t.studentId = s.id  INNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum  INNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum  where d.examPaperNum={ExamPaperNum} and d.choosename={Id} GROUP BY d.id ", Task.class, args1);
                        for (int j2 = 0; j2 < _queryBeanList3.size(); j2++) {
                            Task t2 = (Task) _queryBeanList3.get(j2);
                            String ext12 = t2.getExt1() == null ? "0" : t2.getExt1();
                            regChooseXuan_map.put(t2.getId(), Integer.valueOf(Integer.parseInt(ext12)));
                        }
                        List<?> _queryBeanList4 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',d.id) id,cast(count(1)/count(DISTINCT(t.questionNum)) as signed) ext1 from define d LEFT JOIN ( \tSELECT t.exampaperNum,t.groupNum,t.questionNum FROM task t LEFT JOIN ( \t\t\tSELECT t.id FROM task t INNER JOIN student s ON t.studentId = s.id \t\t\tINNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum \t\tINNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum \t\tWHERE t.exampaperNum = {ExamPaperNum} \t) t1 ON t.id = t1.id WHERE t.exampaperNum = {ExamPaperNum}  AND t1.id IS NULL ) t on d.id=t.groupNum where d.examPaperNum={ExamPaperNum}  and d.choosename={Id} GROUP BY d.id", Task.class, args1);
                        for (int j3 = 0; j3 < _queryBeanList4.size(); j3++) {
                            Task t3 = (Task) _queryBeanList4.get(j3);
                            String ext13 = t3.getExt1() == null ? "0" : t3.getExt1();
                            regChooseXue_map.put(t3.getId(), Integer.valueOf(Integer.parseInt(ext13)));
                        }
                        List<?> _queryBeanList5 = this.dao2._queryBeanList("SELECT CONCAT(sg.schoolGroupNum,'-',d.id) id,cast(count(1)/count(DISTINCT(t.questionNum)) as signed) ext1 from define d LEFT JOIN task t on d.id=t.groupNum INNER JOIN student s ON t.studentId = s.id  INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum where d.examPaperNum={ExamPaperNum}  and d.choosename={Id} GROUP BY d.id,sg.schoolGroupNum", Task.class, args1);
                        for (int j4 = 0; j4 < _queryBeanList5.size(); j4++) {
                            Task t4 = (Task) _queryBeanList5.get(j4);
                            String ext14 = t4.getExt1() == null ? "0" : t4.getExt1();
                            regChooseTotalIn_map.put(t4.getId(), Integer.valueOf(Integer.parseInt(ext14)));
                        }
                        List<?> _queryBeanList6 = this.dao2._queryBeanList("SELECT CONCAT(sg.schoolGroupNum,'-',d.id) id,cast(count(1)/count(DISTINCT(t.questionNum)) as signed) ext1 from define d LEFT JOIN task t on d.id=t.groupNum  INNER JOIN student s ON t.studentId = s.id  INNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum  INNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum  INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum  where d.examPaperNum={ExamPaperNum}  and d.choosename={Id} GROUP BY d.id,sg.schoolGroupNum", Task.class, args1);
                        for (int j5 = 0; j5 < _queryBeanList6.size(); j5++) {
                            Task t5 = (Task) _queryBeanList6.get(j5);
                            String ext15 = t5.getExt1() == null ? "0" : t5.getExt1();
                            regChooseXuanIn_map.put(t5.getId(), Integer.valueOf(Integer.parseInt(ext15)));
                        }
                        List<?> _queryBeanList7 = this.dao2._queryBeanList("SELECT CONCAT(t.schoolGroupNum,'-',d.id) id,cast(count(1)/count(DISTINCT(t.questionNum)) as signed) ext1 from define d LEFT JOIN ( \tSELECT t.exampaperNum,t.groupNum,t.questionNum,sg.schoolGroupNum FROM task t LEFT JOIN ( \t\t\tSELECT t.id FROM task t \t\t\tINNER JOIN student s ON t.studentId = s.id \t\t\tINNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum \t\t\tINNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum \t\t\tWHERE t.exampaperNum ={ExamPaperNum} \t\t) t1 ON t.id = t1.id  INNER JOIN student s ON t.studentId = s.id  INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum WHERE t.exampaperNum = {ExamPaperNum}  AND t1.id IS NULL \t) t on d.id=t.groupNum where d.examPaperNum={ExamPaperNum}  and d.choosename={Id} GROUP BY d.id,t.schoolGroupNum", Task.class, args1);
                        for (int j6 = 0; j6 < _queryBeanList7.size(); j6++) {
                            Task t6 = (Task) _queryBeanList7.get(j6);
                            String ext16 = t6.getExt1() == null ? "0" : t6.getExt1();
                            regChooseXueIn_map.put(t6.getId(), Integer.valueOf(Integer.parseInt(ext16)));
                        }
                    } else {
                        List<?> _queryBeanList8 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN task t on sd.id=t.groupNum where d.examPaperNum={ExamPaperNum}  and d.choosename={Id} GROUP BY sd.id", Task.class, args1);
                        for (int j7 = 0; j7 < _queryBeanList8.size(); j7++) {
                            Task t7 = (Task) _queryBeanList8.get(j7);
                            String ext17 = t7.getExt1() == null ? "0" : t7.getExt1();
                            regChooseTotal_map.put(t7.getId(), Integer.valueOf(Integer.parseInt(ext17)));
                        }
                        List<?> _queryBeanList9 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN task t on sd.id=t.groupNum  INNER JOIN student s ON t.studentId = s.id  INNER JOIN subjectcombinedetail sdb ON s.subjectCombineNum = sdb.subjectCombineNum  INNER JOIN exampaper e ON sdb.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum  where d.examPaperNum={ExamPaperNum}  and d.choosename={Id} GROUP BY sd.id ", Task.class, args1);
                        for (int j8 = 0; j8 < _queryBeanList9.size(); j8++) {
                            Task t8 = (Task) _queryBeanList9.get(j8);
                            String ext18 = t8.getExt1() == null ? "0" : t8.getExt1();
                            regChooseXuan_map.put(t8.getId(), Integer.valueOf(Integer.parseInt(ext18)));
                        }
                        List<?> _queryBeanList10 = this.dao2._queryBeanList("SELECT CONCAT(-1,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN ( \tSELECT t.exampaperNum,t.groupNum,count(DISTINCT t.studentId) count FROM task t LEFT JOIN ( \t\t\tSELECT t.id FROM task t \t\t\tINNER JOIN student s ON t.studentId = s.id \t\t\tINNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum \t\t\tINNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum \t\t\tWHERE t.exampaperNum ={ExamPaperNum} \t\t) t1 ON t.id = t1.id WHERE t.exampaperNum = {ExamPaperNum}  AND t1.id IS NULL \t) t on sd.id=t.groupNum where d.examPaperNum={ExamPaperNum}  and d.choosename={Id} GROUP BY sd.id ", Task.class, args1);
                        for (int j9 = 0; j9 < _queryBeanList10.size(); j9++) {
                            Task t9 = (Task) _queryBeanList10.get(j9);
                            String ext19 = t9.getExt1() == null ? "0" : t9.getExt1();
                            regChooseXue_map.put(t9.getId(), Integer.valueOf(Integer.parseInt(ext19)));
                        }
                        List<?> _queryBeanList11 = this.dao2._queryBeanList("SELECT CONCAT(sg.schoolGroupNum,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN task t on sd.id=t.groupNum  INNER JOIN student s ON t.studentId = s.id INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum  where d.examPaperNum={ExamPaperNum}  and d.choosename={Id} GROUP BY sd.id,sg.schoolGroupNum", Task.class, args1);
                        for (int j10 = 0; j10 < _queryBeanList11.size(); j10++) {
                            Task t10 = (Task) _queryBeanList11.get(j10);
                            String ext110 = t10.getExt1() == null ? "0" : t10.getExt1();
                            regChooseTotalIn_map.put(t10.getId(), Integer.valueOf(Integer.parseInt(ext110)));
                        }
                        List<?> _queryBeanList12 = this.dao2._queryBeanList("SELECT CONCAT(sg.schoolGroupNum,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN task t on sd.id=t.groupNum  INNER JOIN student s ON t.studentId = s.id  INNER JOIN subjectcombinedetail sdb ON s.subjectCombineNum = sdb.subjectCombineNum  INNER JOIN exampaper e ON sdb.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum  INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum   where d.examPaperNum={ExamPaperNum}  and d.choosename={Id} GROUP BY sd.id,sg.schoolGroupNum", Task.class, args1);
                        for (int j11 = 0; j11 < _queryBeanList12.size(); j11++) {
                            Task t11 = (Task) _queryBeanList12.get(j11);
                            String ext111 = t11.getExt1() == null ? "0" : t11.getExt1();
                            regChooseXuanIn_map.put(t11.getId(), Integer.valueOf(Integer.parseInt(ext111)));
                        }
                        List<?> _queryBeanList13 = this.dao2._queryBeanList("SELECT CONCAT(t.schoolGroupNum,'-',sd.id) id,count(1) ext1 from define d LEFT JOIN subdefine sd on sd.pid=d.id LEFT JOIN ( \tSELECT t.exampaperNum,t.groupNum,sg.schoolGroupNum,count(DISTINCT t.studentId) count FROM task t LEFT JOIN ( \t\t\tSELECT t.id FROM task t \t\t\tINNER JOIN student s ON t.studentId = s.id \t\t\tINNER JOIN subjectcombinedetail sd ON s.subjectCombineNum = sd.subjectCombineNum \t\t\tINNER JOIN exampaper e ON sd.subjectNum = e.subjectNum AND t.examPaperNum = e.pexampaperNum \t\t\tWHERE t.exampaperNum ={ExamPaperNum} \t\t) t1 ON t.id = t1.id  INNER JOIN student s ON t.studentId = s.id  INNER JOIN schoolgroup sg ON s.schoolNum = sg.schoolNum WHERE t.exampaperNum = {ExamPaperNum}  AND t1.id IS NULL \t) t on sd.id=t.groupNum where d.examPaperNum={ExamPaperNum}  and d.choosename={Id} GROUP BY sd.id,t.schoolGroupNum", Task.class, args1);
                        for (int j12 = 0; j12 < _queryBeanList13.size(); j12++) {
                            Task t12 = (Task) _queryBeanList13.get(j12);
                            String ext112 = t12.getExt1() == null ? "0" : t12.getExt1();
                            regChooseXueIn_map.put(t12.getId(), Integer.valueOf(Integer.parseInt(ext112)));
                        }
                    }
                }
            }
            map.put("regChooseTotal", regChooseTotal_map);
            map.put("regChooseTotalIn", regChooseTotalIn_map);
            map.put("regChooseXuan", regChooseXuan_map);
            map.put("regChooseXuanIn", regChooseXuanIn_map);
            map.put("regChooseXue", regChooseXue_map);
            map.put("regChooseXueIn", regChooseXueIn_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Map<String, Map<String, Integer>> getDataFormBigTableExaminaChoose() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> examinaChooseTotal_map = new HashMap<>();
        Map<String, Integer> examinaChooseTotalIn_map = new HashMap<>();
        Map<String, Integer> examinaChooseXuan_map = new HashMap<>();
        Map<String, Integer> examinaChooseXuanIn_map = new HashMap<>();
        Map<String, Integer> examinaChooseXue_map = new HashMap<>();
        Map<String, Integer> examinaChooseXueIn_map = new HashMap<>();
        try {
            List examList = this.dao2.queryColList("SELECT examNum from exam where status <>9 and isDelete='F'");
            for (int h = 0; h < examList.size(); h++) {
                String examNum = String.valueOf(examList.get(h));
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList = this.dao2._queryBeanList("SELECT  CONCAT(-1,'-',p.examPaperNum) id,count(n.studentId) ext1 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  where n.examNum={examNum}  GROUP BY examPaperNum", Task.class, args);
                for (int j = 0; j < _queryBeanList.size(); j++) {
                    Task t = (Task) _queryBeanList.get(j);
                    examinaChooseTotal_map.put(t.getId(), Integer.valueOf(Integer.parseInt(t.getExt1())));
                }
                List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT  CONCAT('-1','-',p.examPaperNum) id,count(n.studentId) ext1 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and p.examPaperNum=e2.pexampaperNum  where n.examNum={examNum}  GROUP BY p.examPaperNum", Task.class, args);
                for (int j2 = 0; j2 < _queryBeanList2.size(); j2++) {
                    Task t2 = (Task) _queryBeanList2.get(j2);
                    examinaChooseXuan_map.put(t2.getId(), Integer.valueOf(Integer.parseInt(t2.getExt1())));
                }
                List<?> _queryBeanList3 = this.dao2._queryBeanList("SELECT CONCAT('-1','-',p.examPaperNum) id,count(n.studentId) ext1  from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  LEFT JOIN (  \tSELECT n.studentId,e2.pexamPaperNum from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  \tINNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   \tINNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and p.examPaperNum=e2.pexampaperNum WHERE n.examNum={examNum}  )n1 on n1.pexamPaperNum=p.examPaperNum and n.studentId=n1.studentId   INNER JOIN exampaper e2 on p.examPaperNum=e2.pexamPaperNum and e2.xuankaoqufen=3  WHERE n.examNum={examNum}  and n1.studentId is null GROUP BY p.examPaperNum  ", Task.class, args);
                for (int j3 = 0; j3 < _queryBeanList3.size(); j3++) {
                    Task t3 = (Task) _queryBeanList3.get(j3);
                    examinaChooseXue_map.put(t3.getId(), Integer.valueOf(Integer.parseInt(t3.getExt1())));
                }
                List<?> _queryBeanList4 = this.dao2._queryBeanList("SELECT  CONCAT(sg.schoolGroupNum,'-',p.examPaperNum) id,count(n.studentId) ext1 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  INNER JOIN schoolgroup sg ON n.schoolNum = sg.schoolNum where n.examNum={examNum}  GROUP BY examPaperNum,sg.schoolGroupNum", Task.class, args);
                for (int j4 = 0; j4 < _queryBeanList4.size(); j4++) {
                    Task t4 = (Task) _queryBeanList4.get(j4);
                    examinaChooseTotalIn_map.put(t4.getId(), Integer.valueOf(Integer.parseInt(t4.getExt1())));
                }
                List<?> _queryBeanList5 = this.dao2._queryBeanList("SELECT  CONCAT(sg.schoolGroupNum,'-',p.examPaperNum) id,count(n.studentId) ext1 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  INNER JOIN schoolgroup sg ON n.schoolNum = sg.schoolNum  INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and p.examPaperNum=e2.pexampaperNum  where n.examNum={examNum}  GROUP BY p.examPaperNum,sg.schoolGroupNum", Task.class, args);
                for (int j5 = 0; j5 < _queryBeanList5.size(); j5++) {
                    Task t5 = (Task) _queryBeanList5.get(j5);
                    examinaChooseXuanIn_map.put(t5.getId(), Integer.valueOf(Integer.parseInt(t5.getExt1())));
                }
                List<?> _queryBeanList6 = this.dao2._queryBeanList("SELECT CONCAT(sg.schoolGroupNum,'-',p.examPaperNum) id,count(n.studentId) ext1  from examinationnum n INNER JOIN schoolgroup sg ON n.schoolNum = sg.schoolNum  INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  LEFT JOIN (  \tSELECT n.studentId,e2.pexamPaperNum from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  \tINNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   \tINNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and p.examPaperNum=e2.pexampaperNum WHERE n.examNum={examNum}  )n1 on n1.pexamPaperNum=p.examPaperNum and n.studentId=n1.studentId   INNER JOIN exampaper e2 on p.examPaperNum=e2.pexamPaperNum and e2.xuankaoqufen=3   WHERE n.examNum={examNum}  and n1.studentId is null GROUP BY p.examPaperNum,sg.schoolGroupNum", Task.class, args);
                for (int j6 = 0; j6 < _queryBeanList6.size(); j6++) {
                    Task t6 = (Task) _queryBeanList6.get(j6);
                    examinaChooseXueIn_map.put(t6.getId(), Integer.valueOf(Integer.parseInt(t6.getExt1())));
                }
            }
            map.put("examinaChooseTotal", examinaChooseTotal_map);
            map.put("examinaChooseTotalIn", examinaChooseTotalIn_map);
            map.put("examinaChooseXuan", examinaChooseXuan_map);
            map.put("examinaChooseXuanIn", examinaChooseXuanIn_map);
            map.put("examinaChooseXue", examinaChooseXue_map);
            map.put("examinaChooseXueIn", examinaChooseXueIn_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Map<String, Map<String, String>>> getDataFormBigTableTaskChoose_old() {
        String sql;
        Map<String, Map<String, Map<String, String>>> map = new HashMap<>();
        Map<String, Map<String, String>> taskChooseTotal_map = new HashMap<>();
        new HashMap();
        new HashMap();
        try {
            List examList = this.dao2.queryColList("SELECT examNum from exam where status <>9 and isDelete='F'");
            for (int h = 0; h < examList.size(); h++) {
                String examNum = String.valueOf(examList.get(h));
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList = this.dao2._queryBeanList("SELECT d.id,d.choosename,d.questionnum,d.examPaperNum from exam e LEFT JOIN exampaper ep on e.examNum=ep.examNum  LEFT JOIN define d on ep.examPaperNum=d.examPaperNum  where e.examNum={examNum}  and d.choosename='T' ", Define.class, args);
                for (int i = 0; i < _queryBeanList.size(); i++) {
                    Define d = (Define) _queryBeanList.get(i);
                    Map args1 = new HashMap();
                    args1.put("Id", d.getId());
                    Object[] qinfo = this.dao2._queryArray("SELECT isParent,merge from define where choosename={Id} ", args1);
                    String isParent = String.valueOf(qinfo[0]);
                    String merge = String.valueOf(qinfo[1]);
                    if ("1".equals(merge)) {
                        isParent = "0";
                    }
                    if ("0".equals(isParent)) {
                        sql = "SELECT id,examPaperNum,questionNum,orderNum from define where choosename={Id}  ";
                    } else {
                        sql = "SELECT sb.id,sb.examPaperNum,sb.questionNum,sb.orderNum from subdefine sb LEFT JOIN define d on sb.pid=d.id  where d.choosename={Id}  ";
                    }
                    List<?> _queryBeanList2 = this.dao2._queryBeanList(sql, Define.class, args1);
                    for (int z = 0; z < _queryBeanList2.size(); z++) {
                        Define df = (Define) _queryBeanList2.get(z);
                        Map args2 = new HashMap();
                        args2.put("Id", df.getId());
                        args2.put("ExamPaperNum", Integer.valueOf(df.getExamPaperNum()));
                        String tishu = this.dao2._queryStr("select count(1) from questiongroup_question where groupNum={Id}  ", args2);
                        String xuankaoqufen = this.dao2._queryStr("SELECT IFNULL(e.xuankaoqufen,1) ext1 from(SELECT d.id,d.category from define d where d.id={Id}  union SELECT sd.id,sd.category from subdefine sd where sd.id={Id} )d INNER JOIN exampaper e on d.category=e.examPaperNum where d.id={Id} ", args2);
                        args2.put("xuankaoqufen", xuankaoqufen);
                        String xuankaoqufenStr = "";
                        if (!"0".equals(xuankaoqufen)) {
                            xuankaoqufenStr = " and t.xuankaoqufen={xuankaoqufen} ";
                        }
                        String sql2 = "SELECT t.insertUser,cast(count(1)/" + tishu + " as signed) from task t INNER JOIN( \tSELECT d2.* from ( \t\t\tSELECT id,chooseName,0 ext1,-1 orderNum from define where id={Id}   \t\t\tunion   \t\t\tSELECT sb.id,d.choosename,1 ext1,sb.orderNum from define d LEFT JOIN subdefine sb on sb.pid=d.id   \t\t\twhere sb.id={Id}  \t\t)d \t\tleft JOIN ( \t\t\tSELECT id,examPaperNum,questionNum,-1 orderNum,choosename from define where examPaperNum={ExamPaperNum} \t\t\tUNION \t\t\tSELECT sb.id,sb.examPaperNum,sb.questionNum,sb.orderNum,d.choosename from subdefine sb LEFT JOIN define d on sb.pid=d.id  \t\t\twhere sb.examPaperNum={ExamPaperNum} \t\t)d2 on d.chooseName=d2.chooseName and d.orderNum=d2.orderNum \t)d on t.groupNum=d.id  \tINNER JOIN user u on t.insertUser=u.id where t.status='T' " + xuankaoqufenStr + " GROUP BY t.insertUser ";
                        taskChooseTotal_map.put(df.getId(), this.dao2._queryOrderMap(sql2, TypeEnum.StringString, args2));
                    }
                }
            }
            System.out.println("taskChooseTotal_map:" + taskChooseTotal_map);
            map.put("taskChooseTotal", taskChooseTotal_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Map<String, Map<String, Map<String, Integer>>> getDataFormBigTableTaskChoose() {
        String sql;
        Map<String, Map<String, Map<String, Integer>>> map = new HashMap<>();
        Map<String, Map<String, Integer>> taskChooseTotal_map = new HashMap<>();
        Map<String, Map<String, Integer>> taskChooseTotalIn_map = new HashMap<>();
        try {
            List examList = this.dao2.queryColList("SELECT examNum from exam where status <>9 and isDelete='F'");
            for (int h = 0; h < examList.size(); h++) {
                String examNum = String.valueOf(examList.get(h));
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList = this.dao2._queryBeanList("SELECT d.id,d.choosename,d.questionnum,d.examPaperNum from exam e LEFT JOIN exampaper ep on e.examNum=ep.examNum  LEFT JOIN define d on ep.examPaperNum=d.examPaperNum  where e.examNum={examNum}  and d.choosename='T' ", Define.class, args);
                for (int i = 0; i < _queryBeanList.size(); i++) {
                    Define d = (Define) _queryBeanList.get(i);
                    Map args1 = new HashMap();
                    args1.put("Id", d.getId());
                    Object[] qinfo = this.dao2._queryArray("SELECT isParent,merge from define where choosename={Id} ", args1);
                    String isParent = String.valueOf(qinfo[0]);
                    String merge = String.valueOf(qinfo[1]);
                    if ("1".equals(merge)) {
                        isParent = "0";
                    }
                    if ("0".equals(isParent)) {
                        sql = "SELECT id,examPaperNum,questionNum,orderNum from define where choosename={Id} ";
                    } else {
                        sql = "SELECT sb.id,sb.examPaperNum,sb.questionNum,sb.orderNum from subdefine sb LEFT JOIN define d on sb.pid=d.id  where d.choosename={Id} ";
                    }
                    List<?> _queryBeanList2 = this.dao2._queryBeanList(sql, Define.class, args1);
                    for (int z = 0; z < _queryBeanList2.size(); z++) {
                        Define df = (Define) _queryBeanList2.get(z);
                        Map args2 = new HashMap();
                        args2.put("Id", df.getId());
                        String tishu = this.dao2._queryStr("select count(1) from questiongroup_question where groupNum={Id}  ", args2);
                        String makTyp = this.dao2._queryStr("select makType from questiongroup_mark_setting where groupNum={Id}  ", args2);
                        String pingshu = "1";
                        if ("1".equals(makTyp)) {
                            pingshu = "2";
                        }
                        String sql2 = "SELECT IFNULL(e.xuankaoqufen,1) ext1 from(SELECT d.id,d.category from define d where d.id={Id}  union SELECT sd.id,sd.category from subdefine sd where sd.id={Id} )d INNER JOIN exampaper e on d.category=e.examPaperNum where d.id=" + df.getId();
                        String xuankaoqufen = this.dao2._queryStr(sql2, args2);
                        args2.put("xuankaoqufen", xuankaoqufen);
                        args1.put("xuankaoqufen", xuankaoqufen);
                        String xuankaoqufenStr = "";
                        if (!"0".equals(xuankaoqufen)) {
                            xuankaoqufenStr = " and t.xuankaoqufen={xuankaoqufen} ";
                        }
                        String sql3 = "SELECT -1,ifnull(cast(count(1)/" + tishu + "/" + pingshu + " as signed),0) from task t where t.groupNum={Id} and t.userNum<>'3' " + xuankaoqufenStr;
                        taskChooseTotal_map.put(df.getId(), this.dao2._queryOrderMap(sql3, TypeEnum.StringInteger, args2));
                    }
                    for (int z2 = 0; z2 < _queryBeanList2.size(); z2++) {
                        Define df2 = (Define) _queryBeanList2.get(z2);
                        Map args3 = new HashMap();
                        args3.put("Id", df2.getId());
                        String tishu2 = this.dao2._queryStr("select count(1) from questiongroup_question where groupNum={Id}  ", args3);
                        String makTyp2 = this.dao2._queryStr("select makType from questiongroup_mark_setting where groupNum={Id}  ", args3);
                        String pingshu2 = "1";
                        if ("1".equals(makTyp2)) {
                            pingshu2 = "2";
                        }
                        String xuankaoqufen2 = this.dao2._queryStr("SELECT IFNULL(e.xuankaoqufen,1) ext1 from(SELECT d.id,d.category from define d where d.id={Id}  union SELECT sd.id,sd.category from subdefine sd where sd.id={Id} )d INNER JOIN exampaper e on d.category=e.examPaperNum where d.id={Id} ", args3);
                        args3.put("xuankaoqufen", xuankaoqufen2);
                        String xuankaoqufenStr2 = "";
                        if (!"0".equals(xuankaoqufen2)) {
                            xuankaoqufenStr2 = " and t.xuankaoqufen={xuankaoqufen} ";
                        }
                        String sql4 = "SELECT slg.schoolGroupNum,ifnull(cast(count(1)/" + tishu2 + "/" + pingshu2 + " as signed),0) from task t LEFT JOIN student s on t.studentId=s.id LEFT JOIN schoolgroup slg on s.schoolnum=slg.schoolNum \twhere t.groupNum={Id} and t.userNum<>'3' " + xuankaoqufenStr2 + "\tGROUP BY slg.schoolGroupNum";
                        taskChooseTotalIn_map.put(df2.getId(), this.dao2._queryOrderMap(sql4, TypeEnum.StringInteger, args3));
                    }
                }
            }
            map.put("taskChooseTotal", taskChooseTotal_map);
            map.put("taskChooseTotalIn", taskChooseTotalIn_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Map<String, Map<String, Map<String, Integer>>> getDataFormBigTableTaskChoose2() {
        Map<String, Map<String, Map<String, Integer>>> map = new HashMap<>();
        return map;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public Map<String, Map<String, Map<String, Integer>>> getDataFormBigTableExamina() {
        Map<String, Map<String, Map<String, Integer>>> map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaTotal_map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaXuan_map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaXue_map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaTotalIn_map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaXuanIn_map = new HashMap<>();
        Map<String, Map<String, Integer>> examinaXueIn_map = new HashMap<>();
        try {
            List examList = this.dao2.queryColList("SELECT examNum from exam where status <>9 and isDelete='F' ");
            for (int h = 0; h < examList.size(); h++) {
                String examNum = String.valueOf(examList.get(h));
                Map args = new HashMap();
                args.put(Const.EXPORTREPORT_examNum, examNum);
                List<?> _queryBeanList = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT p.examPaperNum,-1 ext1,count(n.studentId) ext2 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  where n.examNum={examNum}  GROUP BY examPaperNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList.size() > 0) {
                    Map<String, Integer> examina_map = new HashMap<>();
                    int examPaperNum = ((RegExaminee) _queryBeanList.get(0)).getExamPaperNum();
                    for (int i = 0; i < _queryBeanList.size(); i++) {
                        RegExaminee reg = (RegExaminee) _queryBeanList.get(i);
                        if (examPaperNum != reg.getExamPaperNum()) {
                            examinaTotal_map.put(String.valueOf(examPaperNum), examina_map);
                            examina_map = new HashMap<>();
                            examPaperNum = reg.getExamPaperNum();
                        }
                        examina_map.put(reg.getExt1(), Integer.valueOf(Integer.parseInt(reg.getExt2())));
                        if (i == _queryBeanList.size() - 1) {
                            examinaTotal_map.put(String.valueOf(examPaperNum), examina_map);
                        }
                    }
                }
                List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT e1.examPaperNum,-1 ext1,count(n.studentId) ext2 from examinationnum n INNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum  INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and e1.examPaperNum=e2.pexampaperNum WHERE n.examNum={examNum}  GROUP BY e1.examPaperNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList2.size() > 0) {
                    Map<String, Integer> examina_map2 = new HashMap<>();
                    int examPaperNum2 = ((RegExaminee) _queryBeanList2.get(0)).getExamPaperNum();
                    for (int i2 = 0; i2 < _queryBeanList2.size(); i2++) {
                        RegExaminee reg2 = (RegExaminee) _queryBeanList2.get(i2);
                        if (examPaperNum2 != reg2.getExamPaperNum()) {
                            examinaXuan_map.put(String.valueOf(examPaperNum2), examina_map2);
                            examina_map2 = new HashMap<>();
                            examPaperNum2 = reg2.getExamPaperNum();
                        }
                        examina_map2.put(reg2.getExt1(), Integer.valueOf(Integer.parseInt(reg2.getExt2())));
                        if (i2 == _queryBeanList2.size() - 1) {
                            examinaXuan_map.put(String.valueOf(examPaperNum2), examina_map2);
                        }
                    }
                }
                List<?> _queryBeanList3 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT e1.examPaperNum,-1 ext1,count(n.studentId) ext2 from examinationnum n INNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum LEFT JOIN ( \tSELECT n.studentId,e2.pexamPaperNum from examinationnum n INNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum \tINNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum \tand e1.examPaperNum=e2.pexampaperNum WHERE n.examNum={examNum}  )n1 on n1.pexamPaperNum=e1.examPaperNum and n.studentId=n1.studentId INNER JOIN exampaper e2 on e1.examPaperNum=e2.pexamPaperNum and e2.xuankaoqufen=3 WHERE n.examNum={examNum}  and n1.studentId is null GROUP BY e1.examPaperNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList3.size() > 0) {
                    Map<String, Integer> examina_map3 = new HashMap<>();
                    int examPaperNum3 = ((RegExaminee) _queryBeanList3.get(0)).getExamPaperNum();
                    for (int i3 = 0; i3 < _queryBeanList3.size(); i3++) {
                        RegExaminee reg3 = (RegExaminee) _queryBeanList3.get(i3);
                        if (examPaperNum3 != reg3.getExamPaperNum()) {
                            examinaXue_map.put(String.valueOf(examPaperNum3), examina_map3);
                            examina_map3 = new HashMap<>();
                            examPaperNum3 = reg3.getExamPaperNum();
                        }
                        examina_map3.put(reg3.getExt1(), Integer.valueOf(Integer.parseInt(reg3.getExt2())));
                        if (i3 == _queryBeanList3.size() - 1) {
                            examinaXue_map.put(String.valueOf(examPaperNum3), examina_map3);
                        }
                    }
                }
                List<?> _queryBeanList4 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT p.examPaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(1),0) ext2 from examinationnum n INNER join exampaper p on n.examNum=p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum  INNER JOIN schoolgroup slg on n.schoolNum=slg.schoolNum WHERE n.examNum={examNum}   GROUP BY p.examPaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList4.size() > 0) {
                    Map<String, Integer> examina_map4 = new HashMap<>();
                    int examPaperNum4 = ((RegExaminee) _queryBeanList4.get(0)).getExamPaperNum();
                    for (int i4 = 0; i4 < _queryBeanList4.size(); i4++) {
                        RegExaminee reg4 = (RegExaminee) _queryBeanList4.get(i4);
                        if (examPaperNum4 != reg4.getExamPaperNum()) {
                            examinaTotalIn_map.put(String.valueOf(examPaperNum4), examina_map4);
                            examina_map4 = new HashMap<>();
                            examPaperNum4 = reg4.getExamPaperNum();
                        }
                        examina_map4.put(reg4.getExt1(), Integer.valueOf(Integer.parseInt(reg4.getExt2())));
                        if (i4 == _queryBeanList4.size() - 1) {
                            examinaTotalIn_map.put(String.valueOf(examPaperNum4), examina_map4);
                        }
                    }
                }
                List<?> _queryBeanList5 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT e1.examPaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(1),0) ext2  from examinationnum n INNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum  INNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum   INNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and e1.examPaperNum=e2.pexampaperNum   INNER JOIN schoolgroup slg on n.schoolNum=slg.schoolNum   WHERE n.examNum={examNum}  GROUP BY e1.examPaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList5.size() > 0) {
                    Map<String, Integer> examina_map5 = new HashMap<>();
                    int examPaperNum5 = ((RegExaminee) _queryBeanList5.get(0)).getExamPaperNum();
                    for (int i5 = 0; i5 < _queryBeanList5.size(); i5++) {
                        RegExaminee reg5 = (RegExaminee) _queryBeanList5.get(i5);
                        if (examPaperNum5 != reg5.getExamPaperNum()) {
                            examinaXuanIn_map.put(String.valueOf(examPaperNum5), examina_map5);
                            examina_map5 = new HashMap<>();
                            examPaperNum5 = reg5.getExamPaperNum();
                        }
                        examina_map5.put(reg5.getExt1(), Integer.valueOf(Integer.parseInt(reg5.getExt2())));
                        if (i5 == _queryBeanList5.size() - 1) {
                            examinaXuanIn_map.put(String.valueOf(examPaperNum5), examina_map5);
                        }
                    }
                }
                List<?> _queryBeanList6 = this.dao2._queryBeanList("SELECT e.examPaperNum,IFNULL(p.ext1,-1) ext1,IFNULL(p.ext2,0) ext2 from exampaper e LEFT JOIN(SELECT e1.examPaperNum,IFNULL(slg.schoolGroupNum,0) ext1,IFNULL(count(1),0) ext2 from examinationnum n INNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum  LEFT JOIN (  \tSELECT n.studentId,e2.pexamPaperNum from examinationnum n  \tINNER join exampaper e1 on n.examNum=e1.examNum and n.gradeNum=e1.gradeNum and n.subjectNum=e1.subjectNum  \tINNER JOIN student s on n.studentId=s.id INNER JOIN subjectcombinedetail  sd on s.subjectCombineNum=sd.subjectCombineNum  \tINNER JOIN exampaper e2 on sd.subjectNum=e2.subjectNum and e1.examPaperNum=e2.pexampaperNum WHERE n.examNum={examNum}   )n1 on n1.pexamPaperNum=e1.examPaperNum and n.studentId=n1.studentId INNER JOIN exampaper e2 on e1.examPaperNum=e2.pexamPaperNum and e2.xuankaoqufen=3  INNER JOIN schoolgroup slg on n.schoolNum=slg.schoolNum WHERE n.examNum={examNum}  and n1.studentId is null GROUP BY e1.examPaperNum,slg.schoolGroupNum)p on e.examPaperNum=p.examPaperNum where e.examNum={examNum} ", RegExaminee.class, args);
                if (_queryBeanList6.size() > 0) {
                    Map<String, Integer> examina_map6 = new HashMap<>();
                    int examPaperNum6 = ((RegExaminee) _queryBeanList6.get(0)).getExamPaperNum();
                    for (int i6 = 0; i6 < _queryBeanList6.size(); i6++) {
                        RegExaminee reg6 = (RegExaminee) _queryBeanList6.get(i6);
                        if (examPaperNum6 != reg6.getExamPaperNum()) {
                            examinaXueIn_map.put(String.valueOf(examPaperNum6), examina_map6);
                            examina_map6 = new HashMap<>();
                            examPaperNum6 = reg6.getExamPaperNum();
                        }
                        examina_map6.put(reg6.getExt1(), Integer.valueOf(Integer.parseInt(reg6.getExt2())));
                        if (i6 == _queryBeanList6.size() - 1) {
                            examinaXueIn_map.put(String.valueOf(examPaperNum6), examina_map6);
                        }
                    }
                }
            }
            map.put("examinaTotal", examinaTotal_map);
            map.put("examinaTotalIn", examinaTotalIn_map);
            map.put("examinaXuan", examinaXuan_map);
            map.put("examinaXuanIn", examinaXuanIn_map);
            map.put("examinaXue", examinaXue_map);
            map.put("examinaXueIn", examinaXueIn_map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public byte[] getImage(String location, String img) {
        return this.dao.splitimgurl(location, img);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String getLocation(String examNum) {
        Map<String, String> args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryStr("select location from imgpath where examNum={examNum} and selected=1", args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public byte[] getImage2(String location, String img) {
        return this.dao.splitimgurl2(location, img);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public byte[] getImageByFullUrl(String fullUrl) {
        String extName = FileUtil.extName(fullUrl);
        if ("tif".equals(extName) || "tiff".equals(extName) || "TIF".equals(extName) || "TIFF".equals(extName)) {
            return ImageUtil.tiffToPng(fullUrl);
        }
        return FileUtil.readBytes(fullUrl);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public boolean getYueStatusFromGroup(String groupNum) {
        boolean flag = true;
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        Object info = this.dao2._queryObject("SELECT  examPaperNum from questiongroup where groupNum={groupNum} and scancompleted=1 ", args);
        if (info == null) {
            flag = false;
        } else {
            String examPaperNum = String.valueOf(info);
            args.put("examPaperNum", examPaperNum);
            String caCount = this.dao2._queryStr("SELECT  IFNULL(count(1),0) from cantrecognized where examPaperNum={examPaperNum} ", args);
            if ("null" != caCount && !"0".equals(caCount)) {
                flag = false;
            } else if ("0".equals(caCount)) {
                String tCount = this.dao2._queryStr("SELECT  count(1) count from task where groupNum={groupNum} and STATUS='F' ", args);
                if ("null" != tCount && !"0".equals(tCount)) {
                    flag = false;
                } else if ("0".equals(tCount)) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public boolean getDeleteStatusGroup(String groupNum) {
        boolean flag = false;
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        Object info = this.dao2._queryObject("SELECT ifnull(count(1),0) from questiongroup qp INNER JOIN exampaper ep on qp.exampaperNum=ep.exampaperNum INNER JOIN exam e on ep.examNum=e.examNum where qp.groupNum={groupNum} and (e.status<>0 or e.isDelete='T')", args);
        if (info != null && "1".equals(String.valueOf(info))) {
            flag = true;
        }
        return flag;
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<QuestionGroup> ajaxQuestionList(String exampaperNum) {
        String sql = "SELECT GROUP_CONCAT( qg.groupNum SEPARATOR '_') groupNum,IFNULL(sd_d.questionNum,IFNULL(dd.questionNum, qg.groupName)) groupName,IFNULL(subd.category,d.category) category,ep.subjectNum ," + exampaperNum + " exampaperNum,IFNULL(dd.orderNum,IFNULL(d.orderNum,IFNULL(sd_d.orderNum,IFNULL( sd.orderNum, subd.orderNum )))) ext1,IFNULL( sd.choosename, d.choosename ) mainName,IFNULL( subd.orderNum, '' ) orderNum FROM questiongroup qg LEFT JOIN subdefine subd ON subd.id = qg.groupNum LEFT JOIN define sd ON sd.id = subd.pid LEFT JOIN define sd_d ON sd_d.id = sd.choosename LEFT JOIN define d ON d.id = qg.groupNum LEFT JOIN define dd ON dd.id = d.choosename LEFT JOIN exampaper ep ON ep.examPaperNum = ifnull(IFNULL(subd.category,d.category),{exampaperNum}) WHERE qg.exampaperNum={examPaperNum}  GROUP BY IF( mainName = 's', qg.groupNum, mainName ),orderNum ORDER BY ext1,IFNULL(subd.orderNum,0)";
        Map args = new HashMap();
        args.put("examPaperNum", exampaperNum);
        return this.dao2._queryBeanList(sql, QuestionGroup.class, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void deleteScoreBiaoji(String newQuestionScore, String scoreId) {
        List<String> sqls = new ArrayList<>();
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        if (null == newQuestionScore) {
            sqls.add("delete from questionstepscore where scoreId={scoreId} ");
            sqls.add("delete from remarkimg where scoreId={scoreId} ");
        } else {
            String oldQuestionScore = this.dao2._queryStr("select questionScore from score where id={scoreId} ", args);
            if (oldQuestionScore.compareTo(newQuestionScore) != 0) {
                sqls.add("delete from questionstepscore where scoreId={scoreId} ");
                sqls.add("delete from remarkimg where scoreId={scoreId} ");
            }
        }
        this.dao2._batchExecute(sqls, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void updateStudentTotalScore(String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        StringBuffer sSql = new StringBuffer();
        sSql.append("select studentId,examPaperNum from score ");
        sSql.append(" where id={scoreId}  ");
        sSql.append(" union ");
        sSql.append("select studentId,examPaperNum from objectivescore ");
        sSql.append(" where id={scoreId}  ");
        Map<String, Object> scoreMap = this.dao2._querySimpleMap(sSql.toString(), args);
        args.put(Const.EXPORTREPORT_studentId, scoreMap.get(Const.EXPORTREPORT_studentId));
        args.put("examPaperNum", scoreMap.get("examPaperNum"));
        StringBuffer sqtsSql = new StringBuffer();
        sqtsSql.append("select IFNULL(sum(questionScore),0) from score ");
        sqtsSql.append("where studentId={studentId}  and examPaperNum={examPaperNum} ");
        Object sqts = this.dao2._queryObject(sqtsSql.toString(), args);
        StringBuffer oqtsSql = new StringBuffer();
        oqtsSql.append("select IFNULL(sum(questionScore),0) from objectivescore ");
        oqtsSql.append("where studentId={studentId}  and examPaperNum={examPaperNum} ");
        Object oqts = this.dao2._queryObject(oqtsSql.toString(), args);
        BigDecimal totalScore = new BigDecimal(sqts.toString()).add(new BigDecimal(oqts.toString()));
        StringBuffer slSql = new StringBuffer();
        args.put("sqts", sqts);
        args.put("oqts", oqts);
        args.put("totalScore", totalScore);
        slSql.append("update studentlevel ");
        slSql.append("set sqts={sqts} ,oqts={oqts} ,totalScore={totalScore}  ");
        slSql.append("where examPaperNum={examPaperNum}  and studentId={studentId} ");
        this.dao2._execute(slSql.toString(), args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void deleteMarkError(AwardPoint awardPoint) {
        Map args = new HashMap();
        args.put("QuestionNum", awardPoint.getQuestionNum());
        args.put("ScoreId", awardPoint.getScoreId());
        String makType = this.dao2._queryStr("select makType from questiongroup_mark_setting where groupNum={QuestionNum} ", args);
        if ("0".equals(makType)) {
            this.awardPointDao.deletemarkerror(awardPoint.getScoreId());
            String insertUser = this.dao2._queryStr("select insertUser from task where scoreId={ScoreId} ", args);
            awardPoint.setUserNum(insertUser);
            this.awardPointDao.insertmarkError(awardPoint);
            return;
        }
        AwardPointServiceImpl awardPointService = new AwardPointServiceImpl();
        List scorePingList = awardPointService.scorePingList(awardPoint.getScoreId());
        String score = awardPoint.getQuestionScore();
        List<AwardPoint> inValidList = new ArrayList<>();
        if (scorePingList.size() == 2) {
            double maxDiffScore = 0.0d;
            for (int i = 0; i < scorePingList.size(); i++) {
                AwardPoint scbean = (AwardPoint) scorePingList.get(i);
                if (Math.abs(Double.parseDouble(score) - Double.parseDouble(scbean.getQuestionScore())) > maxDiffScore) {
                    maxDiffScore = Math.abs(Double.parseDouble(score) - Double.parseDouble(scbean.getQuestionScore()));
                }
            }
            for (int i2 = 0; i2 < scorePingList.size(); i2++) {
                AwardPoint scbean2 = (AwardPoint) scorePingList.get(i2);
                if (Math.abs(Double.parseDouble(score) - Double.parseDouble(scbean2.getQuestionScore())) == maxDiffScore) {
                    AwardPoint newAward = new AwardPoint();
                    awardPoint.setQuestionScore(String.valueOf(scbean2.getQuestionScore()));
                    awardPoint.setUserNum(scbean2.getInsertUser());
                    BeanUtil.copyProperties(awardPoint, newAward, new String[0]);
                    inValidList.add(newAward);
                }
            }
        } else if (scorePingList.size() == 3) {
            double minDiffScore = Math.abs(Double.parseDouble(score) - Double.parseDouble(((AwardPoint) scorePingList.get(0)).getQuestionScore()));
            for (int i3 = 0; i3 < scorePingList.size(); i3++) {
                AwardPoint scbean3 = (AwardPoint) scorePingList.get(i3);
                if (Math.abs(Double.parseDouble(score) - Double.parseDouble(scbean3.getQuestionScore())) < minDiffScore) {
                    minDiffScore = Math.abs(Double.parseDouble(score) - Double.parseDouble(scbean3.getQuestionScore()));
                }
            }
            for (int i4 = 0; i4 < scorePingList.size(); i4++) {
                AwardPoint scbean4 = (AwardPoint) scorePingList.get(i4);
                if (Math.abs(Double.parseDouble(score) - Double.parseDouble(scbean4.getQuestionScore())) != minDiffScore) {
                    AwardPoint newAward2 = new AwardPoint();
                    awardPoint.setQuestionScore(String.valueOf(scbean4.getQuestionScore()));
                    awardPoint.setUserNum(scbean4.getInsertUser());
                    BeanUtil.copyProperties(awardPoint, newAward2, new String[0]);
                    inValidList.add(newAward2);
                }
            }
        }
        awardPointService.deletemarkerror(awardPoint.getScoreId());
        if (awardPoint.getIsException().equals("F")) {
            for (int r = 0; r < inValidList.size(); r++) {
                awardPointService.insertmarkError(inValidList.get(r));
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Map<String, Object>> getYuejuanZhiliangQuestionNum(String examNum, String gradeNum, String subjectNum) {
        String examPaperNum = getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        StringBuffer dSql = new StringBuffer();
        dSql.append("select id,questionNum from define ");
        dSql.append("where examPaperNum={examPaperNum}  and questionType='1' and (choosename='s' or choosename='T') ");
        StringBuffer subdSql = new StringBuffer();
        subdSql.append("select id,questionNum,pid from subdefine ");
        subdSql.append("where examPaperNum={examPaperNum}  and questionType='1' ");
        StringBuffer sql = new StringBuffer();
        sql.append("select IFNULL(subd.id,d.id) id,IFNULL(subd.questionNum,d.questionNum) questionNum from (");
        sql.append(dSql);
        sql.append(") d ");
        sql.append("left join (");
        sql.append(subdSql);
        sql.append(") subd on subd.pid = d.id");
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        return this.dao2._queryMapList(sql.toString(), null, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<String> getZongheSubjectList(String examNum, String gradeNum) {
        StringBuffer sql = new StringBuffer();
        sql.append("select subjectNum,examPaperNum,pexamPaperNum,isHidden from exampaper ");
        sql.append("where examNum={examNum}  and gradeNum={gradeNum} ");
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        List<Map<String, Object>> ep = this.dao2._queryMapList(sql.toString(), null, args);
        List<String> pexamPaperNumList = (List) ep.stream().filter(sub -> {
            return "T".equals(sub.get("isHidden"));
        }).map(sub2 -> {
            return String.valueOf(sub2.get("pexamPaperNum"));
        }).distinct().collect(Collectors.toList());
        List<String> zongheKemuList = (List) ep.stream().filter(sub3 -> {
            return pexamPaperNumList.contains(String.valueOf(sub3.get("examPaperNum")));
        }).map(sub4 -> {
            return String.valueOf(sub4.get(Const.EXPORTREPORT_subjectNum));
        }).distinct().collect(Collectors.toList());
        return zongheKemuList;
    }

    public void insertRemark(String scoreId, String insertUser, String insertDate, String updateTime) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("insertUser", insertUser);
        args.put("insertDate", insertDate);
        args.put("updateTime", updateTime);
        StringBuffer delRemSql = new StringBuffer();
        delRemSql.append("delete from remark ");
        delRemSql.append("where scoreId={scoreId}  and type='1' ");
        this.dao2._execute(delRemSql.toString(), args);
        StringBuffer sSql = new StringBuffer();
        sSql.append("select id,examPaperNum,questionNum,questionScore,'1','-1','F','-1','T' ");
        sSql.append(",{insertUser} ,{insertDate} ,{updateTime}  ");
        sSql.append("from score ");
        sSql.append("where id={scoreId}  limit 1 ");
        StringBuffer insRemSql = new StringBuffer();
        insRemSql.append("insert into remark ");
        insRemSql.append("(scoreId,exampaperNum,questionNum,questionScore,type,userNum,isException,rownum,status,insertUser,insertDate,updateTime) ");
        insRemSql.append(sSql);
        this.dao2._execute(insRemSql.toString(), args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public void addRemark(String remScoreId, String remQuestionNum, String exampaperNum, String loginUserId, String time, String isShenhe) {
        if (!"".equals(remScoreId)) {
            String updateTime = DateUtil.getCurrentTime2();
            StringBuffer merSql = new StringBuffer();
            merSql.append("select d.`merge` from subdefine subd ");
            merSql.append("left join define d on d.id=subd.pid ");
            merSql.append("where subd.id={remQuestionNum}  limit 1");
            Map args = new HashMap();
            args.put("remQuestionNum", remQuestionNum);
            String xxtFlag = this.dao2._queryStr(merSql.toString(), args);
            if ("1".equals(xxtFlag)) {
                List<MarkError> queList = getQueList(exampaperNum, remQuestionNum, remScoreId);
                queList.forEach(ques -> {
                    insertRemark(ques.getScoreId(), loginUserId, time, updateTime);
                    if ("0".equals(isShenhe)) {
                        ExamineeStuRecord examineeStuRecord = new ExamineeStuRecord();
                        examineeStuRecord.setScoreId(ques.getScoreId());
                        examineeStuRecord.setUserId(loginUserId);
                        examineeStuRecord.setInsertDate(time);
                        examineeStuRecord.setStatus("T");
                        addExamineeRecord(examineeStuRecord);
                    }
                });
                return;
            }
            insertRemark(remScoreId, loginUserId, time, updateTime);
            if ("0".equals(isShenhe)) {
                ExamineeStuRecord examineeStuRecord = new ExamineeStuRecord();
                examineeStuRecord.setScoreId(remScoreId);
                examineeStuRecord.setUserId(loginUserId);
                examineeStuRecord.setInsertDate(time);
                examineeStuRecord.setStatus("T");
                addExamineeRecord(examineeStuRecord);
            }
        }
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<Map<String, Object>> getAllGradeByExamSubject(String examNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return this.dao2._queryMapList("select gradeNum,examPaperNum,pexamPaperNum,xuankaoqufen,isHidden,fenzuyuejuan,type from exampaper where examNum={examNum} and subjectNum={subjectNum}", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.examManagement.ExamService
    public int getGroupYipancount(String groupNum, String userNum) {
        String userNumStr = "";
        if (!"-1".equals(userNum)) {
            userNumStr = " and updateUser={userNum} ";
        }
        String sql = "select count(1)/count(DISTINCT questionNum) from task where groupNum={groupNum} and status='T' " + userNumStr;
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("groupNum", groupNum);
        Object yipanCount = this.dao2._queryObject(sql, args);
        return Convert.toInt(yipanCount, 0).intValue();
    }

    @Override // com.dmj.service.examManagement.ExamService
    public String existZhuguanQues(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        if (null == this.dao2._queryObject("select id from define where examPaperNum={examPaperNum} and questionType='1' and choosename!='T' limit 1", args)) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.examManagement.ExamService
    public List<AjaxData> getUserNoPerExam(String userId) {
        return this.dao.getUserNoPerExam(userId);
    }
}
