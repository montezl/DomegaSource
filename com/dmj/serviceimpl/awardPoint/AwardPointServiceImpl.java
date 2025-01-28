package com.dmj.serviceimpl.awardPoint;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dmj.auth.bean.License;
import com.dmj.cs.bean.ScoringPointRect;
import com.dmj.cs.util.CsUtils;
import com.dmj.daoimpl.awardPoint.AwardPointDaoImpl;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.examManagement.ExamDAOImpl;
import com.dmj.daoimpl.questionGroup.QuestionGroupDaoImpl;
import com.dmj.daoimpl.tagManage.TagManageDAOImpl;
import com.dmj.domain.Answerquestionimagebean;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.Define;
import com.dmj.domain.Grade;
import com.dmj.domain.QuestionGroupInfo;
import com.dmj.domain.QuestionGroupTemp;
import com.dmj.domain.Remark;
import com.dmj.domain.RemarkImg;
import com.dmj.domain.Schoolgroup;
import com.dmj.domain.Subject;
import com.dmj.domain.Task;
import com.dmj.domain.Teacher;
import com.dmj.domain.User;
import com.dmj.domain.Userposition;
import com.dmj.domain.leq.Teacherappeal;
import com.dmj.domain.vo.Imgpath;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.domain.vo.Question_scorerule;
import com.dmj.domain.vo.TempAnswer;
import com.dmj.domain.vo.viewScore.Scorefd;
import com.dmj.service.awardPoint.AwardPointService;
import com.dmj.service.awardPoint.SaveTaskService;
import com.dmj.service.examManagement.ExamService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.examManagement.ExamServiceImpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.ChineseCharacterUtil;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.FileUtil;
import com.dmj.util.GUID;
import com.dmj.util.JsonType;
import com.dmj.util.StealQuestionHelper;
import com.dmj.util.UuidUtil;
import com.dmj.util.config.Configuration;
import com.dmj.util.quartz.QuartzManager;
import com.google.common.collect.Lists;
import com.zht.db.DbUtils;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;

/* loaded from: AwardPointServiceImpl.class */
public class AwardPointServiceImpl implements AwardPointService {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());
    AwardPointDaoImpl dao = new AwardPointDaoImpl();
    QuestionGroupDaoImpl qgd = new QuestionGroupDaoImpl();
    ExamDAOImpl examDao = new ExamDAOImpl();
    private TagManageDAOImpl tagManageDAO = new TagManageDAOImpl();
    private SaveTaskService taskservice = (SaveTaskService) ServiceFactory.getObject(new SaveTakServiceImpl());
    private ExamService examService = (ExamService) ServiceFactory.getObject(new ExamServiceImpl());
    private static final Byte[] lock = new Byte[0];
    public static SystemService systemService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    public static SystemService system = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    private static final Logger logF = Logger.getLogger("stealLogger");

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Object[] tizhu(int exampaperNum, String insertUser) {
        return this.dao.gettizhu(exampaperNum, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Object[] caijue(int exampaperNum, String userNum) {
        Object[] list = this.dao.caijue(exampaperNum, userNum);
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public int GCCaiJue(String exampaperNum, String scoreId, String questionNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("questionNum", (Object) questionNum).put("scoreId", (Object) scoreId);
        return this.dao2._execute("update remark set insertUser='-1' where exampaperNum={exampaperNum} and questionNum={questionNum} and scoreId={scoreId} and status='F'", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public int skipQue(String exampaperNum, String groupNum, String studentId, String id) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) groupNum).put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._execute("update task set insertUser='-1',porder=0,fenfaDate=0 where exampaperNum={exampaperNum} and groupNum={groupNum} and studentId={studentId} and status='F' ", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public int GCCaiJueCount(String exampaperNum, String count, String questionNum, String userNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        args.put("userNum", userNum);
        args.put("count", count);
        String ids = this.dao2._queryStr("SELECT GROUP_CONCAT(id separator ',')  FROM (SELECT * FROM remark where exampaperNum={exampaperNum} and questionNum={questionNum}  and insertUser={userNum} and `status`='F' ORDER BY id ASC LIMIT 0,{count} )AS tt", args);
        args.put("ids", ids);
        return this.dao2._execute("UPDATE remark SET insertUser='-1' WHERE id in ({ids[]}) ", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public byte[] getScoreImage1(String questionNum, int exampaperNum) {
        return this.dao.getScoreImage1(questionNum, exampaperNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public byte[] getScoreImage2(String questionNum, AwardPoint aw) {
        return this.dao.getScoreImage2(questionNum, aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List queryTag(AwardPoint aw) {
        return this.dao.queryTag(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer update(AwardPoint awardPoint) {
        return this.dao.update(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updatet(String id, String score, String insertUser, String isException, String time, String scoreId, String taskUserNum, String makeType, String judgerule) {
        return this.dao.updatet(id, score, insertUser, isException, time, scoreId, taskUserNum, makeType, judgerule);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updates(String scoreId, String score, String insertUser, String time) {
        return this.dao.updates(scoreId, score, insertUser, time);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updateTaskyi(String questionNum, String time, String insertUser, String score, int id, AwardPoint awardPoint) {
        return this.dao.updateTaskyi(questionNum, time, insertUser, score, id, awardPoint);
    }

    public static double avg(double a, double b) {
        double score = (a + b) / 2.0d;
        return score;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void updateremarkdb(String time, String insertUser, String score, String id, int exampaperNum, String scoreId, String isException, String questionNum, AwardPoint awardPoint) {
        String score1 = "0";
        String score2 = "0";
        String scorePing = "";
        this.dao.updatenewremark(awardPoint);
        List scorePingList = this.dao.scorePingList(scoreId);
        for (int i = 0; i < scorePingList.size(); i++) {
            AwardPoint scbean = (AwardPoint) scorePingList.get(i);
            if (scorePing.equals("")) {
                scorePing = scbean.getQuestionScore();
                score1 = scorePing;
            } else {
                scorePing = scorePing + Const.STRING_SEPERATOR + scbean.getQuestionScore();
                String[] realScore = scorePing.split(Const.STRING_SEPERATOR);
                score1 = realScore[0];
                score2 = realScore[1];
            }
        }
        List errorRateList = this.dao.errorRateList(questionNum);
        AwardPoint awardPoint2 = new AwardPoint();
        if (errorRateList != null && errorRateList.size() > 0) {
            awardPoint2 = (AwardPoint) errorRateList.get(0);
        }
        String errorRate = awardPoint2.getErrorRate();
        if (scorePingList.size() < 2) {
            this.dao.updates(scoreId, score, insertUser, time);
        }
        if (scorePingList.size() == 2) {
            double cha = Math.abs(Double.parseDouble(score1) - Double.parseDouble(score2));
            if (cha <= Double.parseDouble(errorRate)) {
                double a = Double.parseDouble(score1);
                double b = Double.parseDouble(score2);
                double realscore1 = avg(a, b);
                int dd = String.valueOf(realscore1).indexOf(".");
                String score3 = String.valueOf(realscore1).substring(dd + 1, dd + 2);
                String newscore = "";
                if (Double.parseDouble(score3) > 5.0d) {
                    newscore = String.valueOf(Math.ceil(realscore1));
                }
                if (Double.parseDouble(score3) < 5.0d) {
                    newscore = String.valueOf(Math.floor(realscore1));
                }
                if (Double.parseDouble(score3) == 5.0d) {
                    newscore = String.valueOf(realscore1);
                }
                this.dao.updates(scoreId, newscore, insertUser, time);
                return;
            }
            int sumsize = 0;
            AwardPoint sumList = this.dao.sumremark(exampaperNum, questionNum);
            if (sumList != null) {
                int rownum = sumList.getRownum();
                sumsize = rownum + 1;
            }
            List remarksize = this.dao.remarksize(scoreId);
            if (remarksize.size() > 0) {
                for (int i2 = 0; i2 < remarksize.size(); i2++) {
                    AwardPoint ik = (AwardPoint) remarksize.get(i2);
                    int remark_id = Integer.parseInt(ik.getId());
                    this.dao.deleteremark(remark_id);
                }
            }
            this.dao.reMark(scoreId, id, insertUser, questionNum, exampaperNum, sumsize);
        }
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updateTask(String time, String insertUser, String score, String id, int exampaperNum, String scoreId, String isException, String questionNum, String taskuserNum, String makeType, String groupNum, String studentId, String groupType, boolean flag) {
        String scoreuserNum;
        String newavg;
        String newavg2;
        String str;
        String judgerule = this.dao.queryjudgetype(questionNum);
        String score1 = "0";
        String score2 = "0";
        String scorePing = "";
        String userNumping = "";
        List remarksize1 = this.dao.remarksize(scoreId);
        if (remarksize1.size() > 0) {
            for (int i = 0; i < remarksize1.size(); i++) {
                if (((AwardPoint) remarksize1.get(i)).getStatus().equals("T")) {
                    this.dao.updatet(id, score, insertUser, "F", time, scoreId, taskuserNum, makeType, judgerule);
                    return -2;
                }
            }
        }
        int length = this.dao.updatet(id, score, insertUser, "F", time, scoreId, taskuserNum, makeType, judgerule).intValue();
        ServletActionContext.getServletContext();
        String scoreAvgOrRound = Configuration.getInstance().getScoreAvgOrRound();
        if (taskuserNum.equals("1") || taskuserNum.equals("2")) {
            List scorePingList = this.dao.scorePingList(scoreId);
            for (int i2 = 0; i2 < scorePingList.size(); i2++) {
                AwardPoint scbean = (AwardPoint) scorePingList.get(i2);
                if (scorePing.equals("")) {
                    scorePing = scbean.getQuestionScore();
                    userNumping = String.valueOf(scbean.getInsertUser());
                    score1 = scorePing;
                } else {
                    scorePing = scorePing + Const.STRING_SEPERATOR + scbean.getQuestionScore();
                    userNumping = userNumping + Const.STRING_SEPERATOR + String.valueOf(scbean.getInsertUser());
                    String[] realScore = scorePing.split(Const.STRING_SEPERATOR);
                    String[] realuserNum = userNumping.split(Const.STRING_SEPERATOR);
                    String str2 = realuserNum[0];
                    String str3 = realuserNum[1];
                    score1 = realScore[0];
                    score2 = realScore[1];
                }
            }
            List errorRateList = this.dao.errorRateList(questionNum);
            AwardPoint awardPoint2 = new AwardPoint();
            if (errorRateList != null && errorRateList.size() > 0) {
                awardPoint2 = (AwardPoint) errorRateList.get(0);
            }
            String errorRate = awardPoint2.getErrorRate();
            if (scorePingList.size() < 2) {
                this.dao.updates(scoreId, score, insertUser, time);
            }
            if (scorePingList.size() >= 2) {
                double cha = Math.abs(Double.parseDouble(score1) - Double.parseDouble(score2));
                if (cha < Double.parseDouble(errorRate) || cha == 0.0d) {
                    List remarksize = this.dao.remarksize(scoreId);
                    if (remarksize.size() > 0) {
                        for (int i3 = 0; i3 < remarksize.size(); i3++) {
                            AwardPoint ik = (AwardPoint) remarksize.get(i3);
                            if (ik.getStatus().equals("T")) {
                                return -2;
                            }
                            int remark_id = Integer.parseInt(ik.getId());
                            this.dao.deleteremark(remark_id);
                        }
                    }
                    double a = Double.parseDouble(score1);
                    double b = Double.parseDouble(score2);
                    double realscore1 = avg(a, b);
                    int dd = String.valueOf(realscore1).indexOf(".");
                    String score3 = String.valueOf(realscore1).substring(dd + 1, dd + 2);
                    String newscore = "";
                    if (Double.parseDouble(score3) > 5.0d) {
                        newscore = String.valueOf(Math.ceil(realscore1));
                    }
                    if (Double.parseDouble(score3) < 5.0d) {
                        newscore = String.valueOf(Math.floor(realscore1));
                    }
                    if (Double.parseDouble(score3) == 5.0d) {
                        newscore = String.valueOf(realscore1);
                    }
                    this.dao.deletemarkerror(scoreId);
                    this.dao.deletetaskuserNum3(scoreId);
                    if ("0".equals(scoreAvgOrRound)) {
                        this.dao.updates(scoreId, realscore1 + "", insertUser, time);
                    } else {
                        this.dao.updates(scoreId, newscore, insertUser, time);
                    }
                } else {
                    double a2 = Double.parseDouble(score1);
                    double b2 = Double.parseDouble(score2);
                    double realscore12 = avg(a2, b2);
                    int dd2 = String.valueOf(realscore12).indexOf(".");
                    String score4 = String.valueOf(realscore12).substring(dd2 + 1, dd2 + 2);
                    String newscore2 = "";
                    if (Double.parseDouble(score4) > 5.0d) {
                        newscore2 = String.valueOf(Math.ceil(realscore12));
                    }
                    if (Double.parseDouble(score4) < 5.0d) {
                        newscore2 = String.valueOf(Math.floor(realscore12));
                    }
                    if (Double.parseDouble(score4) == 5.0d) {
                        newscore2 = String.valueOf(realscore12);
                    }
                    if ("0".equals(scoreAvgOrRound)) {
                        String str4 = realscore12 + "";
                    }
                    if (judgerule.equals("0")) {
                        List remarksize2 = this.dao.remarksize(scoreId);
                        if (remarksize2.size() > 0) {
                            for (int i4 = 0; i4 < remarksize2.size(); i4++) {
                                AwardPoint ik2 = (AwardPoint) remarksize2.get(i4);
                                if (ik2.getStatus().equals("T")) {
                                    return -2;
                                }
                                int remark_id2 = Integer.parseInt(ik2.getId());
                                this.dao.deleteremark(remark_id2);
                            }
                        }
                        if ("0".equals(scoreAvgOrRound)) {
                            this.dao.updates(scoreId, realscore12 + "", insertUser, time);
                        } else {
                            this.dao.updates(scoreId, newscore2, insertUser, time);
                        }
                        int sumsize = 0;
                        AwardPoint sumList = this.dao.sumremark(exampaperNum, questionNum);
                        if (sumList != null) {
                            int rownum = sumList.getRownum();
                            sumsize = rownum + 1;
                        }
                        this.dao.reMarkF(scoreId, "", "", questionNum, exampaperNum, sumsize, groupNum, studentId);
                    } else if (judgerule.equals("1") || judgerule.equals("2")) {
                        List remarksize3 = this.dao.remarksize(scoreId);
                        if (remarksize3.size() > 0) {
                            for (int i5 = 0; i5 < remarksize3.size(); i5++) {
                                AwardPoint ik3 = (AwardPoint) remarksize3.get(i5);
                                if (ik3.getStatus().equals("T")) {
                                    return -2;
                                }
                                int remark_id3 = Integer.parseInt(ik3.getId());
                                this.dao.deleteremark(remark_id3);
                            }
                        }
                        this.dao.deletemarkerror(scoreId);
                        this.dao.inserttask3(scoreId, questionNum);
                    }
                }
            }
        } else if (taskuserNum.equals("3") && isException.equals("F") && !score.equals("稍后再判")) {
            AwardPoint awardPoint = new AwardPoint();
            awardPoint.setId(id);
            awardPoint.setScoreId(scoreId);
            awardPoint.setQuestionScore(score);
            awardPoint.setInsertUser(insertUser);
            awardPoint.setInsertDate(time);
            awardPoint.setExampaperNum(exampaperNum);
            awardPoint.setQuestionNum(questionNum);
            awardPoint.setType("1");
            awardPoint.setIsModify("T");
            awardPoint.setStatus("T");
            List scorePingList2 = this.dao.scorePingList(scoreId);
            if (scorePingList2.size() >= 2) {
                for (int i6 = 0; i6 < 2; i6++) {
                    AwardPoint scbean2 = (AwardPoint) scorePingList2.get(i6);
                    if (scorePing.equals("")) {
                        scorePing = scbean2.getQuestionScore();
                        str = String.valueOf(scbean2.getInsertUser());
                    } else {
                        scorePing = scorePing + Const.STRING_SEPERATOR + scbean2.getQuestionScore();
                        str = userNumping + Const.STRING_SEPERATOR + String.valueOf(scbean2.getInsertUser());
                    }
                    userNumping = str;
                }
                String[] realScore2 = scorePing.split(Const.STRING_SEPERATOR);
                String[] realuserNum2 = userNumping.split(Const.STRING_SEPERATOR);
                Map<String, Double> number = new TreeMap<>();
                number.put(realuserNum2[0] + "0", Double.valueOf(Double.parseDouble(realScore2[0])));
                number.put(realuserNum2[1] + "1", Double.valueOf(Double.parseDouble(realScore2[1])));
                number.put(insertUser + "2", Double.valueOf(Double.parseDouble(score)));
                1 r0 = new 1(this);
                List<Map.Entry<String, Double>> list = new ArrayList<>(number.entrySet());
                Collections.sort(list, r0);
                String userNum1 = String.valueOf(list.get(0).getKey().substring(0, list.get(0).getKey().length() - 1));
                String userNum2 = String.valueOf(list.get(2).getKey().substring(0, list.get(2).getKey().length() - 1));
                String.valueOf(list.get(1).getKey().substring(0, list.get(1).getKey().length() - 1));
                double a3 = Double.parseDouble(String.valueOf(list.get(0).getValue()));
                double b3 = Double.parseDouble(String.valueOf(list.get(2).getValue()));
                double c = Double.parseDouble(String.valueOf(list.get(1).getValue()));
                String score5 = String.valueOf(c);
                double a1 = Math.abs(a3 - c);
                double b1 = Math.abs(b3 - c);
                List<AwardPoint> inValidList = new ArrayList<>();
                if (a1 < b1) {
                    scoreuserNum = String.valueOf(a3);
                    AwardPoint newAward = new AwardPoint();
                    awardPoint.setQuestionScore(String.valueOf(b3));
                    awardPoint.setUserNum(userNum2);
                    BeanUtil.copyProperties(awardPoint, newAward, new String[0]);
                    inValidList.add(newAward);
                } else if (a1 == b1) {
                    scoreuserNum = String.valueOf(a3);
                    AwardPoint newAward2 = new AwardPoint();
                    awardPoint.setQuestionScore(String.valueOf(a3));
                    awardPoint.setUserNum(userNum1);
                    BeanUtil.copyProperties(awardPoint, newAward2, new String[0]);
                    inValidList.add(newAward2);
                    AwardPoint newAward22 = new AwardPoint();
                    awardPoint.setQuestionScore(String.valueOf(b3));
                    awardPoint.setUserNum(userNum2);
                    BeanUtil.copyProperties(awardPoint, newAward22, new String[0]);
                    inValidList.add(newAward22);
                } else {
                    scoreuserNum = String.valueOf(b3);
                    AwardPoint newAward3 = new AwardPoint();
                    awardPoint.setQuestionScore(String.valueOf(a3));
                    awardPoint.setUserNum(userNum1);
                    BeanUtil.copyProperties(awardPoint, newAward3, new String[0]);
                    inValidList.add(newAward3);
                }
                if (a1 == b1) {
                    int xiaoshuwei = String.valueOf(score5).indexOf(".");
                    if ("0".equals(scoreAvgOrRound)) {
                        awardPoint.setQuestionScore(score5);
                    } else if (xiaoshuwei == -1) {
                        awardPoint.setQuestionScore(score5);
                    } else {
                        String xiaoshu = String.valueOf(score5).substring(xiaoshuwei + 1, xiaoshuwei + 2);
                        if (Double.parseDouble(xiaoshu) > 5.0d) {
                            newavg2 = String.valueOf(Math.ceil(Double.valueOf(score5).doubleValue()));
                        } else if (Double.parseDouble(xiaoshu) < 5.0d) {
                            newavg2 = String.valueOf(Math.floor(Double.valueOf(score5).doubleValue()));
                        } else {
                            newavg2 = String.valueOf(Double.valueOf(score5));
                        }
                        awardPoint.setQuestionScore(newavg2);
                    }
                } else {
                    Double avgscore = Double.valueOf((Double.valueOf(scoreuserNum).doubleValue() + Double.valueOf(score5).doubleValue()) / 2.0d);
                    if ("0".equals(scoreAvgOrRound)) {
                        awardPoint.setQuestionScore(avgscore + "");
                    } else {
                        int xiaoshuwei2 = String.valueOf(avgscore).indexOf(".");
                        if (xiaoshuwei2 == -1) {
                            awardPoint.setQuestionScore(avgscore + "");
                        } else {
                            String xiaoshu2 = String.valueOf(avgscore).substring(xiaoshuwei2 + 1, xiaoshuwei2 + 2);
                            if (Double.parseDouble(xiaoshu2) > 5.0d) {
                                newavg = String.valueOf(Math.ceil(avgscore.doubleValue()));
                            } else if (Double.parseDouble(xiaoshu2) < 5.0d) {
                                newavg = String.valueOf(Math.floor(avgscore.doubleValue()));
                            } else {
                                newavg = String.valueOf(avgscore);
                            }
                            awardPoint.setQuestionScore(newavg);
                        }
                    }
                }
                List errorRateList2 = this.dao.errorRateList(questionNum);
                AwardPoint awardPoint22 = new AwardPoint();
                if (errorRateList2 != null && errorRateList2.size() > 0) {
                    awardPoint22 = (AwardPoint) errorRateList2.get(0);
                }
                String errorRate2 = awardPoint22.getErrorRate();
                double cha2 = Math.abs(Double.parseDouble(score5) - Double.parseDouble(scoreuserNum));
                if (isException == null || isException.equals("") || isException.equals("0") || isException.equals("null")) {
                    awardPoint.setIsException("F");
                }
                List remarksize4 = this.dao.remarksize(scoreId);
                if (remarksize4.size() > 0) {
                    for (int i7 = 0; i7 < remarksize4.size(); i7++) {
                        AwardPoint ik4 = (AwardPoint) remarksize4.get(i7);
                        if (ik4.getStatus().equals("T")) {
                            return -2;
                        }
                        int remark_id4 = Integer.parseInt(ik4.getId());
                        this.dao.deleteremark(remark_id4);
                    }
                }
                if ("0".equals(scoreAvgOrRound)) {
                    this.dao.updates(scoreId, awardPoint.getQuestionScore(), insertUser, time);
                } else {
                    this.dao.updates(scoreId, awardPoint.getQuestionScore(), insertUser, time);
                }
                if (judgerule.equals("1") || (judgerule.equals("2") && cha2 < Double.parseDouble(errorRate2))) {
                    this.dao.deletemarkerror(scoreId);
                    for (int r = 0; r < inValidList.size(); r++) {
                        this.dao.insertmarkError(inValidList.get(r));
                    }
                }
                if (judgerule.equals("2") && cha2 >= Double.parseDouble(errorRate2)) {
                    int sumsize2 = 0;
                    AwardPoint sumList2 = this.dao.sumremark(exampaperNum, questionNum);
                    if (sumList2 != null) {
                        int rownum2 = sumList2.getRownum();
                        sumsize2 = rownum2 + 1;
                    }
                    this.dao.reMarkF(scoreId, "", "", questionNum, exampaperNum, sumsize2, groupNum, studentId);
                }
            }
        }
        if (flag) {
            this.dao.supplyRemarkDataByStudent(groupNum, studentId);
            this.dao.supplyTaskDataByStudent(groupNum, studentId);
        }
        return Integer.valueOf(length);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updateTag(String tagvalue, String questionNum, AwardPoint awardPoint) {
        return this.dao.updateTag(tagvalue, questionNum, awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer insertTag(AwardPoint awardPoint) {
        return this.dao.insertTag(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer tagCount(AwardPoint awardPoint) {
        return this.dao.tagCount(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer biaoTag(AwardPoint awardPoint, String id) {
        return this.dao.biaoTag(awardPoint, id);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer biaoremarkTag(AwardPoint aw) {
        return this.dao.biaoremarkTag(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer delteremarkTag(AwardPoint aw) {
        return this.dao.delteremarkTag(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer delteTag(AwardPoint awardPoint, String id) {
        return this.dao.delteTag(awardPoint, id);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer biaoremarkYi(AwardPoint aw, String id) {
        return this.dao.biaoremarkYi(aw, id);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer delteremarkYi(AwardPoint aw, String id) {
        return this.dao.delteremarkYi(aw, id);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Map<String, Object> ttizhuzb1(AwardPoint aw, String makType) {
        return this.dao.ttizhuzb1(aw, makType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List ttizhuzb(AwardPoint aw, int makType) {
        return this.dao.ttizhuzb(aw, makType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List ttizhuzbzj(AwardPoint aw, String makType) {
        return this.dao.ttizhuzbzj(aw, makType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer sumscore(AwardPoint aw) {
        return this.dao.sumscore(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> questionNum(int exampaperNum, String groupNum) {
        return this.dao.questionNum(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint adminTi(AwardPoint aw, String insertNum) {
        return this.dao.adminTi(aw, insertNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer finsishping(AwardPoint aw, String questionNum) throws Exception {
        return this.dao.finsishping(aw, questionNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer countqueryping(String tag, int exampaperNum, String insertNum, String groupNum, String questionNum, String starttime, String endtime, String startscore, String endscore, String groupType) throws Exception {
        return this.dao.countqueryping(tag, exampaperNum, insertNum, groupNum, questionNum, starttime, endtime, startscore, endscore, groupType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer countYibiao(String tag, int exampaperNum, String insertNum, String groupNum) throws Exception {
        return this.dao.countYibiao(tag, exampaperNum, insertNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List yibiao(AwardPoint aw, String tag, int exampaperNum, String insertNum, String groupNum) throws Exception {
        return this.dao.yibiao(aw, tag, exampaperNum, insertNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer countchaYi(AwardPoint aw, String tag, int exampaperNum, String insertNum, String groupNum) throws Exception {
        return this.dao.countchaYi(aw, tag, exampaperNum, insertNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List chayi(AwardPoint aw, String tag, int exampaperNum, String insertNum, String groupNum) throws Exception {
        return this.dao.chayi(aw, tag, exampaperNum, insertNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updatescore(String questionNum, String time, String insertUser, String score, String id, AwardPoint awardPoint) {
        Integer list = this.dao.updatescore(time, insertUser, score, id, awardPoint);
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint markSetting(AwardPoint aw) {
        AwardPoint list = this.dao.markSetting(aw);
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List scorePingList(String id) {
        return this.dao.scorePingList(id);
    }

    public AwardPoint finishstudentIdaw(AwardPoint aw) {
        return this.dao.finishstudentIdaw(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public synchronized List caipan(AwardPoint aw) {
        List<?> _queryBeanList;
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        args.put("PageStart", Integer.valueOf(aw.getPageStart()));
        args.put("Rownum", Integer.valueOf(aw.getRownum()));
        synchronized (lock) {
            StringBuffer buffer = new StringBuffer();
            try {
                Object oid = this.dao2._queryObject("select id from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum}  and type='1'  and insertUser={InsertUser} and rownum={PageStart} limit 1", args);
                args.put("oid", oid);
                if (null == oid) {
                    args.put("oid", this.dao2._queryObject("select id from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum} and status='F' and type='1'  and insertUser='-1' order by rownum limit 1", args));
                    Object rowNums = this.dao2._queryObject("select max(rownum) from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum}  and type='1'  and insertUser={InsertUser} ", args);
                    if (null == rowNums) {
                        rowNums = 0;
                    }
                    Integer rowNumint = Integer.valueOf(rowNums + "");
                    args.put("rowNumint", Integer.valueOf(rowNumint.intValue() + 1));
                    this.dao2._execute("update remark set insertuser={InsertUser} ,rowNum={rowNumint} where id={oid} ", args);
                }
                buffer.append("select   a.questionNum,a.status,a.scoreId,a.isException,a.questionScore,a.rownum, a.id,  a.examPaperNum,a.insertUser ,c.fullScore from  remark    a     left  join  ((select id,questionnum,fullScore,examPaperNum from define where exampaperNum={ExampaperNum} and id={QuestionNum}) union all (select id,questionnum,fullScore,examPaperNum  from subdefine WHERE exampaperNum={ExampaperNum} and id={QuestionNum})) c  on   a.questionNum=c.id  AND  a.exampaperNum=c.exampaperNum  where a.id={oid} and a.type ='1'  and   a.examPaperNum={ExampaperNum}  and   a.questionNum={QuestionNum}  and   a.rownum={Rownum}   and a.insertuser={InsertUser}  group  by  a.questionNum  order by  a.rownum asc ");
            } catch (Exception e) {
                e.printStackTrace();
            }
            _queryBeanList = this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
        }
        return _queryBeanList;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List nocaipan(AwardPoint aw) {
        return this.dao.nocaipan(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public synchronized List caipan1(AwardPoint aw) {
        List<?> _queryBeanList;
        Map args = new HashMap();
        args.put("ExampaperNum", Integer.valueOf(aw.getExampaperNum()));
        args.put("QuestionNum", aw.getQuestionNum());
        args.put("InsertUser", aw.getInsertUser());
        synchronized (lock) {
            StringBuffer buffer = new StringBuffer();
            try {
                Object oid = this.dao2._queryObject("select id from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum} and status='F' and type='1'  and insertUser={InsertUser} limit 1", args);
                args.put("oid", oid);
                if (null == oid) {
                    args.put("oid", this.dao2._queryObject("select id from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum} and status='F' and type='1'  and insertUser='-1' order by rownum limit 1", args));
                    Object rowNums = this.dao2._queryObject("select max(rownum) from remark where exampaperNum={ExampaperNum}  and questionNum={QuestionNum}  and type='1'  and insertUser={InsertUser} ", args);
                    if (null == rowNums) {
                        rowNums = 0;
                    }
                    Integer rowNumint = Integer.valueOf(rowNums + "");
                    args.put("rowNumint", rowNumint);
                    this.dao2._execute("update remark set insertuser={InsertUser} ,rowNum={rowNumint} where id={oid} ", args);
                }
                buffer.append("  select  a.questionNum,a.status,a.id,a.isException, a.questionScore,a.rownum,a.scoreId,  a.examPaperNum,a.insertUser ,c.fullScore from  remark    a     left  join  ( (select id,exampaperNum,fullScore from define where examPaperNum={ExampaperNum} ) union all (select id,exampaperNum,fullScore  from subdefine WHERE exampaperNum={ExampaperNum} )) c  on   a.questionNum=c.id  AND  a.exampaperNum=c.exampaperNum  where a.id={oid} and  a.status='F'   and   a.type ='1'    and  a.examPaperNum={ExampaperNum}  and   a.questionNum={QuestionNum}  order by  a.rownum asc ");
            } catch (Exception e) {
                e.printStackTrace();
            }
            _queryBeanList = this.dao2._queryBeanList(buffer.toString(), AwardPoint.class, args);
        }
        return _queryBeanList;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List yiscorePingList(AwardPoint aw) {
        return this.dao.yiscorePingList(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List secondscorePingList(AwardPoint aw) {
        return this.dao.secondscorePingList(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List yiscorePingList1(AwardPoint aw) {
        return this.dao.yiscorePingList1(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List secondscorePingList1(AwardPoint aw) {
        return this.dao.secondscorePingList1(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List finishcaipan(AwardPoint aw) {
        return this.dao.finishcaipan(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer finishcaipanCount(AwardPoint aw) {
        return this.dao.finishcaipanCount(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List yicaipan(AwardPoint aw) {
        return this.dao.yicaipan(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer countyichacai(AwardPoint aw) {
        return this.dao.countyichacai(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List yicaichapan(AwardPoint aw) {
        return this.dao.yicaichapan(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer yicaipanCount(AwardPoint aw) {
        return this.dao.yicaipanCount(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List finishchongpan(AwardPoint aw) {
        return this.dao.finishchongpan(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer caichong(AwardPoint aw) {
        return this.dao.caichong(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer questionNumcai(AwardPoint aw) {
        return this.dao.questionNumcai(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer finishchongpanCount(AwardPoint aw) {
        return this.dao.finishchongpanCount(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List yichongpan(AwardPoint aw) {
        return this.dao.yichongpan(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer yichongpanCount(AwardPoint aw) {
        return this.dao.yichongpanCount(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List chongpanfen(AwardPoint aw) {
        return this.dao.chongpanfen(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List weichongpanfen(AwardPoint aw) {
        return this.dao.weichongpanfen(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List chongpanfen1(AwardPoint aw) {
        return this.dao.chongpanfen1(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String caiImage(String questionNum, AwardPoint aw, String[] studentId) {
        return this.dao.caiImage(questionNum, aw, studentId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public byte[] caitu(String insertNum, String scoreId) {
        return this.dao.caitu(insertNum, scoreId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public byte[] caituPaperComment(String scoreId) {
        return this.dao.caituPaperComment(scoreId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public byte[] caituchong(String questionNum, AwardPoint aw, String studentId) {
        return this.dao.caituchong(questionNum, aw, studentId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint completeTask2(AwardPoint aw) {
        return this.dao.completeTask2(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint sumusercompleteTask(AwardPoint aw) {
        return this.dao.sumusercompleteTask(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List usercompleteTask2(AwardPoint aw) {
        return this.dao.usercompleteTask2(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List listusercompleteTask2(AwardPoint aw) {
        return this.dao.listusercompleteTask2(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List groupavgcount(AwardPoint aw) {
        return this.dao.groupavgcount(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List groupavgmarkrrate(AwardPoint aw) {
        return this.dao.groupavgmarkrrate(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List groupmarkrrate(AwardPoint aw) {
        return this.dao.groupmarkrrate(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List useravgscore(AwardPoint aw) {
        return this.dao.useravgscore(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List gaiquestNumList(AwardPoint aw) {
        return this.dao.gaiquestNumList(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List groupavgscore(AwardPoint aw) {
        return this.dao.groupavgscore(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint sumgroupavgcount(AwardPoint aw) {
        return this.dao.sumgroupavgcount(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint sumgroupavgmarkrrate(AwardPoint aw) {
        return this.dao.sumgroupavgmarkrrate(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint sumgroupmarkrrate(AwardPoint aw) {
        return this.dao.sumgroupmarkrrate(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint sumgroupavgscore(AwardPoint aw) {
        return this.dao.sumgroupavgscore(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint sumuseravgscore(AwardPoint aw) {
        return this.dao.sumuseravgscore(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint userNumbean(AwardPoint aw) {
        return this.dao.userNumbean(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint completeTime(AwardPoint aw) {
        return this.dao.completeTime(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer exam(int exam, int grade, int subject) {
        return this.dao.exam(exam, grade, subject);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List groupNumList() {
        return this.dao.groupNumList();
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public int countchongpanfen(AwardPoint aw) {
        return this.dao.countchongpanfen(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List questinNumList(AwardPoint aw) {
        return this.dao.questinNumList(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updateremark(AwardPoint aw) {
        return this.dao.updateremark(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updateChooseQuestion(AwardPoint aw, String tagquestionNum) {
        return this.dao.updateChooseQuestion(aw, tagquestionNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List choosequestion(AwardPoint aw, String tagquestionNum) {
        return this.dao.choosequestion(aw, tagquestionNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updatechoosetask(AwardPoint aw) {
        return this.dao.updatechoosetask(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updatechooseremark(AwardPoint aw) {
        return this.dao.updatechooseremark(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updatechoosechongremark(AwardPoint aw) {
        return this.dao.updatechoosechongremark(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updateremarkexception(String questionNum, String time, String insertUser, String score, String id, AwardPoint awardPoint) {
        return this.dao.updateremarkexception(questionNum, time, insertUser, score, id, awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updatenewremark(AwardPoint aw) {
        return this.dao.updatenewremark(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer insertmarkError(AwardPoint aw) {
        return this.dao.insertmarkError(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List countzb(AwardPoint aw, String makType) {
        return this.dao.countzb(aw, makType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint maxupdatetime(AwardPoint aw) {
        return this.dao.maxupdatetime(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint dangmaxTime(AwardPoint aw) {
        return this.dao.dangmaxTime(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint remarkmaxTime(AwardPoint aw) {
        return this.dao.remarkmaxTime(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint maxupdateremarktime(AwardPoint aw) {
        return this.dao.maxupdateremarktime(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List questinNumList1(AwardPoint aw) {
        return this.dao.questinNumList1(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getTaskHaveJusityByUpdateTime(int exampaperNum, String insertUser, String groupNum, String questionNum, String updateTime, String str, String groupType, String starttime, String endtime, String startscore, String endscore) throws Exception {
        return this.dao.getTaskHaveJusityByUpdateTime(exampaperNum, insertUser, groupNum, questionNum, updateTime, str, groupType, starttime, endtime, startscore, endscore);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getTaskByUpdateTime(int exampaperNum, String insertUser, String groupNum, String updateTime, String str, String groupType) throws Exception {
        return this.dao.getTaskByUpdateTime(exampaperNum, insertUser, groupNum, updateTime, str, groupType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String scanStatus(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        return this.dao.scanStatus(exampaperNum, insertUser, groupNum, groupType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer getShengXiaoTask(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        return this.dao.getShengXiaoTask(exampaperNum, insertUser, groupNum, groupType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String cantrecognized(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        return this.dao.cantrecognized(exampaperNum, insertUser, groupNum, groupType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getTask(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        return this.dao.getTask(exampaperNum, insertUser, groupNum, groupType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer getTaskPersonYiPan(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        return this.dao.getTaskPersonYiPan(exampaperNum, insertUser, groupNum, groupType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer getTaskNo(int exampaperNum, String insertUser, String groupNum, String groupType, String makType) throws Exception {
        return this.dao.getTaskNo(exampaperNum, insertUser, groupNum, groupType, makType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer otherTeacherHasQum(int exampaperNum, String insertUser, String groupNum, String groupType, String makType) throws Exception {
        return this.dao.otherTeacherHasQum(exampaperNum, insertUser, groupNum, groupType, makType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer openTestDisQum(int exampaperNum, String insertUser, String groupNum, String groupType, String makType) throws Exception {
        return this.dao.openTestDisQum(exampaperNum, insertUser, groupNum, groupType, makType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer closeTestDisQum(int exampaperNum, String insertUser, String groupNum, String groupType, String makType) throws Exception {
        return this.dao.closeTestDisQum(exampaperNum, insertUser, groupNum, groupType, makType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getChooseNameType(String groupNum) throws Exception {
        return this.dao.getChooseNameType(groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getOtherChooseCount(String exampaperNum, String groupNum, String insertUser) throws Exception {
        return this.dao.getOtherChooseCount(exampaperNum, groupNum, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getRestTask(int exampaperNum, String insertUser, String groupNum, String groupType, String makType) throws Exception {
        return this.dao.getRestTask(exampaperNum, insertUser, groupNum, groupType, makType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getGoTaskHaveJusity(int exampaperNum, String insertUser, String groupNum, String questionNum, String groupType, int goNum, String starttime, String endtime, String startscore, String endscore) throws Exception {
        return this.dao.getGoTaskHaveJusity(exampaperNum, insertUser, groupNum, questionNum, groupType, goNum, starttime, endtime, startscore, endscore);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List finishTaskById(int exampaperNum, String insertUser, String groupNum, String questionNum, String groupType, String id) throws Exception {
        return this.dao.finishTaskById(exampaperNum, insertUser, groupNum, questionNum, groupType, id);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getTaskHaveJusity(int exampaperNum, String insertUser, String groupNum, String groupType) throws Exception {
        return this.dao.getTaskHaveJusity(exampaperNum, insertUser, groupNum, groupType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getRemark(Map<String, String> map) throws Exception {
        return this.dao.getRemark(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getRemarkByUpdateTime(Map<String, String> map) throws Exception {
        return this.dao.getRemarkByUpdateTime(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getRemarkHaveJusity(Map<String, String> map) throws Exception {
        return this.dao.getRemarkHaveJusity(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getRemarkyipanscore(Map<String, String> map) throws Exception {
        return this.dao.getRemarkyipanscore(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getRemarkHaveJusityByUpdateTime(Map<String, String> map) throws Exception {
        return this.dao.getRemarkHaveJusityByUpdateTime(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getGoRemarkHaveJusity(Map<String, String> map) throws Exception {
        return this.dao.getGoRemarkHaveJusity(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List finishRemarkById(Map<String, String> map) throws Exception {
        return this.dao.finishRemarkById(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer otherTeacherHasRemarkQum(Map<String, String> map) throws Exception {
        return this.dao.otherTeacherHasRemarkQum(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer countRemark(Map<String, String> map) throws Exception {
        return this.dao.countRemark(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer getTaskNOJustify(Map<String, String> map) throws Exception {
        return this.dao.getTaskNOJustify(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer GCRemark(Map<String, String> map) throws Exception {
        return this.dao.GCRemark(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getTeacherReamarkStatus(Map<String, String> map) throws Exception {
        return this.dao.getTeacherReamarkStatus(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getQuestionMode(Map<String, String> map) throws Exception {
        return this.dao.getQuestionMode(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer GCRemarkAll() throws Exception {
        return this.dao.GCRemarkAll();
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String queryremarkyipanllcount(Map<String, String> map) throws Exception {
        return this.dao.queryremarkyipanllcount(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Remark getRemarkProgress(Map<String, String> map) throws Exception {
        return this.dao.getRemarkProgress(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getRemarkTeacherInfo(Map<String, String> map, List remarkList) throws Exception {
        return this.dao.getRemarkTeacherInfo(map, remarkList);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updateReamrkScore(Map<String, String> map) throws Exception {
        return this.dao.updateReamrkScore(map);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List ping1(int exampaperNum, String insertUser, String groupNum, int rownum, String groupType) throws Exception {
        return this.dao.ping1(exampaperNum, insertUser, groupNum, rownum, groupType);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List ping2(AwardPoint aw, String questionNum, String str) throws Exception {
        return this.dao.ping2(aw, questionNum, str);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List ping(AwardPoint aw, String questionNum, String str) throws Exception {
        return this.dao.ping(aw, questionNum, str);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint sumList(AwardPoint aw) {
        return this.dao.sumList(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint quesrow(AwardPoint aw) {
        return this.dao.quesrow(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint sumchongpan(AwardPoint aw) {
        return this.dao.sumchongpan(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List quertyquestion(AwardPoint aw) {
        return this.dao.quertyquestion(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List ping3(AwardPoint aw, String questionNum, String str) throws Exception {
        return this.dao.ping3(aw, questionNum, str);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List noping(AwardPoint aw, String questionNum, String str) throws Exception {
        return this.dao.noping(aw, questionNum, str);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer deletemarkerror(String remarkId) {
        return this.dao.deletemarkerror(remarkId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List markerrorsize(AwardPoint aw) {
        return this.dao.markerrorsize(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List tulist(AwardPoint aw, String id) {
        return this.dao.tulist(aw, id);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List tulistcai(AwardPoint aw, String scoreId) {
        return this.dao.tulistcai(aw, scoreId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer deletecaitulist(AwardPoint aw) {
        return this.dao.deletecaitulist(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer deletetulist(AwardPoint aw) {
        return this.dao.deletetulist(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List countcai(AwardPoint aw) {
        return this.dao.countcai(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List countchong(AwardPoint aw) {
        return this.dao.countchong(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List caiquestionNum(AwardPoint aw) {
        return this.dao.caiquestionNum(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List countcaicount2(AwardPoint aw) {
        return this.dao.countcaicount2(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List chongquestionNum(AwardPoint aw) {
        return this.dao.chongquestionNum(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List GetInsertUser(AwardPoint aw) {
        return this.dao.GetInsertUser(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer countyichachong(AwardPoint aw) {
        return this.dao.countyichachong(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List yichongchapan(AwardPoint aw) {
        return this.dao.yichongchapan(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getExam(String userNum) {
        return this.dao.getExam(userNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getSubjectCount2(Integer exam, Integer grade, String userNum) {
        return this.dao.getSubjectCount2(exam, grade, userNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getSubject(int exam, int grade, String insertUser) {
        return this.dao.getSubject(exam, grade, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getSubject2(Integer exam, Integer grade, String insertUser) {
        return this.dao.getSubject2(exam, grade, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Userposition> jzth(String jie, String num, int examNum) {
        return this.dao.jzth(jie, num, examNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getGrade(int exam, String userNum) {
        return this.dao.getGrade(exam, userNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getGrade2(Integer exam, String insertUser) {
        return this.dao.getGrade2(exam, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getYuejuanExam(String insertUser) {
        return this.dao.getYuejuanExam(insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getRemarkExam(String insertUser) {
        return this.dao.getRemarkExam(insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getYuejuanGrade(Integer exam, String insertUser) {
        return this.dao.getYuejuanGrade(exam, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getRemarkGrade(Integer exam, String insertUser) {
        return this.dao.getRemarkGrade(exam, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getYuejuanSubject(Integer exam, Integer grade, String insertUser) {
        return this.dao.getYuejuanSubject(exam, grade, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> getRemarkSubject(Integer exam, Integer grade, String insertUser) {
        return this.dao.getRemarkSubject(exam, grade, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint groupsumcount(AwardPoint aw) {
        return this.dao.groupsumcount(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint groupcompletecount(AwardPoint aw) {
        return this.dao.groupcompletecount(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public AwardPoint minquestionNum(AwardPoint aw) {
        return this.dao.minquestionNum(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer saveimage(AwardPoint aw) {
        return this.dao.saveimage(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer listminrownum(int exampaperNum, String insertUser, String groupNum) {
        return this.dao.listminrownum(exampaperNum, insertUser, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List listGroupNum(AwardPoint aw) {
        return this.dao.listGroupNum(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List taskuserList(AwardPoint aw, String scoreId) {
        return this.dao.taskuserList(aw, scoreId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List yichangzb(AwardPoint aw) {
        return this.dao.yichangzb(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List yichangcaidu(AwardPoint aw) {
        return this.dao.yichangcaidu(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List yichangchongdu(AwardPoint aw) {
        return this.dao.yichangchongdu(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List teachList(AwardPoint aw) {
        return this.dao.teachList(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List chooseQuestionNumList(String exampaperNum, String groupNum, String questionNum, String p_questionNum, String groupType, String isParent) {
        return this.dao.chooseQuestionNumList(exampaperNum, groupNum, questionNum, p_questionNum, groupType, isParent);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer firstrownumss(String tag, AwardPoint aw, String groupNum, int exampaperNum) {
        return this.dao.firstrownumss(tag, aw, groupNum, exampaperNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer rownumrecord(int exampaperNum, String insertNum, String groupNum, int rownum, String starttime, String endtime) {
        return this.dao.rownumrecord(exampaperNum, insertNum, groupNum, rownum, starttime, endtime);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List delTarecord(int exampaperNum, String insertNum, String groupNum, int rownumrecord, String starttime, String endtime) {
        return this.dao.delTarecord(exampaperNum, insertNum, groupNum, rownumrecord, starttime, endtime);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer completeList(AwardPoint awardPoint) {
        return this.dao.completeList(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer completecount(AwardPoint awardPoint) {
        return this.dao.completecount(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer complettaskecount(AwardPoint aw) {
        return this.dao.complettaskecount(aw);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List notaskList(AwardPoint awardPoint) {
        return this.dao.notaskList(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List notaskdoubleList(AwardPoint awardPoint) {
        return this.dao.notaskdoubleList(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer updateTscore(AwardPoint awardPoint) {
        return this.dao.updateTscore(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer onlineuser(AwardPoint awardPoint) {
        return this.dao.onlineuser(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> questionNumList(AwardPoint awardPoint) {
        return this.dao.questinNumList(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer deletenocomplTask(AwardPoint awardPoint, String huishouteype) {
        return this.dao.deletenocomplTask(awardPoint, huishouteype);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void insertTask(AwardPoint awardPoint, String insertUser) {
        this.dao.insertTask(awardPoint, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void doublefentask(int exampaperNum, String insertUser, String groupNum) {
        this.dao.doublefentask(exampaperNum, insertUser, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List doubleList(AwardPoint awardPoint) {
        return this.dao.doubleList(awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> shitag(int exam, int grade, int subject, AwardPoint awardPoint) {
        return this.dao.shitag(exam, grade, subject, awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> totalNumList(int exampaperNum) {
        return this.dao.totalNumList(exampaperNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void updategroupNum(String groupNum, AwardPoint awardPoint) {
        this.dao.updategroupNum(groupNum, awardPoint);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void updategroupNumexecute(AwardPoint awardPoint, String tagquestionNum) {
        this.dao.updategroupNumexecute(awardPoint, tagquestionNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void insertquestionstepscore(AwardPoint awardPoint, String width, String height, String scoreId) {
        this.dao.insertquestionstepscore(awardPoint, width, height, scoreId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List insertquestionstepscoreList(AwardPoint awardPoint, String scoreId) {
        return this.dao.insertquestionstepscoreList(awardPoint, scoreId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String querymaxstep(String scoreid, String insertUser) {
        Map args = StreamMap.create().put("scoreid", (Object) scoreid).put("insertUser", (Object) insertUser);
        return this.dao2._queryStr("select axis_y from questionstepscore where scoreId={scoreid}  and  insertuser={insertUser}   ORDER BY step desc LIMIT 1", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void updatequestionstepscore(String id, String score) {
        this.dao.updatequestionstepscore(id, score);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void deletequestionstepscore(String id, String step, String insertUser) {
        this.dao.deletequestionstepscore(id, step, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void somedeletequestionstepscore(String scoreid, String insertUser) {
        Map args = StreamMap.create().put("scoreid", (Object) scoreid);
        this.dao2._execute("delete  from  questionstepscore  where scoreid={scoreid} ", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void updateupquestionstepscore(String scoreId, String step, String userNum) {
        this.dao.updateupquestionstepscore(scoreId, step, userNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List checkstep(String scoreId) {
        return this.dao.checkstep(scoreId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List quertyaxis_xList(String step, String insertUser, String scoreId) {
        return this.dao.quertyaxis_xList(step, insertUser, scoreId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List fullscoreList(int exampaperNum, String groupNum) {
        return this.dao.fullscoreList(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String maxrownum(int exampaperNum, String insertUser, String groupNum) {
        return this.dao.maxrownum(exampaperNum, insertUser, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public int usercomplete(String insertUser, String groupNum, int exampaperNum) {
        return this.dao.usercomplete(insertUser, groupNum, exampaperNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<QuestionGroupTemp> usercompleteList(String groupNum, int exampaperNum) {
        return this.dao.usercompleteList(groupNum, exampaperNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public int markType(int exampaperNum, String questionNum) {
        return this.dao.markType(exampaperNum, questionNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List insertworkrecordList(int exampaperNum, String groupNum, String insertUser) {
        return this.dao.insertworkrecordList(exampaperNum, groupNum, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public int insertworkrecord(int exampaperNum, String groupNum, String insertUser, String insertDate, String totalscore) {
        return this.dao.insertworkrecord(exampaperNum, groupNum, insertUser, insertDate, totalscore);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public int updateworkrecord(int exampaperNum, String groupNum, String insertUser, String insertDate, String totalscore, String chayi_id, String sequence) {
        return this.dao.updateworkrecord(exampaperNum, groupNum, insertUser, insertDate, totalscore, chayi_id, sequence);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public int updateworkrecord1(int exampaperNum, String groupNum, String insertUser, int count, String insertDate, String totalscore) {
        return this.dao.updateworkrecord1(exampaperNum, groupNum, insertUser, count, insertDate, totalscore);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List totalNumsession(int exampaperNum, String groupNum) {
        return this.dao.totalNumsession(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List groupNumList(String exampaperNum) {
        return this.dao.groupNumList(exampaperNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void updategroupNum(String groupNum, int exampaperNum, int totalnum) {
        this.dao.updategroupNum(groupNum, exampaperNum, totalnum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer groupNummarkType(String groupNum, int exampaperNum) {
        return this.dao.groupNummarkType(groupNum, exampaperNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer onlineuser(String insertUser) {
        return this.dao.onlineuser(insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<AwardPoint> score(String groupNum, String scoreId) {
        return this.dao.score(groupNum, scoreId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String scanType(String exampaperNum) {
        return this.dao.scanType(exampaperNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer shiping(String exampaperNum) {
        return this.dao.shiping(exampaperNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void updatefullscore(String exam, String grade, String subject, String questionNum) {
        int exampaperNum = 0;
        if (grade != null && !grade.equals("") && !grade.equals("undefined") && subject != null && !subject.equals("") && !subject.equals("undefined")) {
            exampaperNum = this.dao.exam(Integer.parseInt(exam), Integer.parseInt(grade), Integer.parseInt(subject)).intValue();
        }
        String groupNum = this.dao.querygroupNum(String.valueOf(exampaperNum), questionNum);
        this.dao.updatefullscore(String.valueOf(exampaperNum), groupNum, questionNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void updateerrate(String exam, String grade, String subject, String questionNum, String errorRate) {
        int exampaperNum = 0;
        if (grade != null && !grade.equals("") && !grade.equals("undefined") && subject != null && !subject.equals("") && !subject.equals("undefined")) {
            exampaperNum = this.dao.exam(Integer.parseInt(exam), Integer.parseInt(grade), Integer.parseInt(subject)).intValue();
        }
        String groupNum = this.dao.querygroupNum(String.valueOf(exampaperNum), questionNum);
        List<AwardPoint> questionscoreList = this.dao.questionscoreList(String.valueOf(exampaperNum), groupNum, questionNum);
        if (questionscoreList != null && questionscoreList.size() > 0) {
            double score1 = 0.0d;
            double score2 = 0.0d;
            for (int i = 0; i < questionscoreList.size(); i++) {
                String scoreId = questionscoreList.get(i).getScoreId();
                List<AwardPoint> scorePingList = this.dao.scorePingList(scoreId);
                if (scorePingList != null && scorePingList.size() == 2) {
                    if (i == 0) {
                        score1 = Double.parseDouble(scorePingList.get(i).getQuestionScore());
                    }
                    if (i == 1) {
                        score2 = Double.parseDouble(scorePingList.get(i).getQuestionScore());
                    }
                    double avgscore = avg(score1, score2);
                    if (avgscore < Double.parseDouble(errorRate)) {
                        this.dao.deleteremark(scoreId, String.valueOf(exampaperNum), questionNum);
                    }
                }
            }
        }
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void updateerrateLeq(String exam, String grade, String subject, String questionNum, String errorRate, String uid, String patternType, String isParent, String groupType, String groupNum2, boolean flag) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("questionNum", questionNum);
        args.put("errorRate", errorRate);
        String examPaperNum = this.dao2._queryStr("select examPaperNum from exampaper where examNum = {exam} and gradeNum = {grade} and subjectNum = {subject} ", args);
        String groupNum = this.dao.querygroupNum(String.valueOf(examPaperNum), questionNum);
        if ("0".equals(isParent)) {
            updateErrorval(questionNum, errorRate);
        } else {
            updateErrorval_sub(questionNum, errorRate);
        }
        if (null != this.dao2._queryStr("SELECT id from task where questionNum = {questionNum}  and userNum = '2' and `status` = 'T' limit 1", args)) {
            List<Object[]> scoreList3 = this.dao2._queryArrayList("select rr.id,rr.scoreId,t.maxScore,t.minScore from remark rr INNER JOIN (SELECT scoreId,MAX(IFNULL(questionScore,0)) maxScore,MIN(IFNULL(questionScore,0)) minScore FROM task WHERE questionNum = {questionNum} and `status` = 'T' and userNum < 3 GROUP BY scoreId HAVING (max(IFNULL(questionScore,0))-min(IFNULL(questionScore,0))) < {errorRate} ) t on t.scoreId = rr.scoreId where rr.questionNum = {questionNum} and rr.type = '1'", args);
            for (int i = 0; i < scoreList3.size(); i++) {
                Object[] obj = scoreList3.get(i);
                String scoreId = String.valueOf(obj[1]);
                Double maxScore = Double.valueOf(String.valueOf(obj[2]));
                Double minScore = Double.valueOf(String.valueOf(obj[3]));
                String newscore = getAvgScore(maxScore, minScore);
                this.dao.updateScoreLeq(scoreId, newscore, uid);
                this.dao.deleteRemarkLeq(scoreId);
            }
            List<Object[]> scoreList4 = this.dao2._queryArrayList("select t2.scoreId,t2.maxScore,t2.minScore from (select scoreId from task where questionNum = {questionNum} and userNum = '3') t1 INNER JOIN (SELECT scoreId,MAX(IFNULL(questionScore,0)) maxScore,MIN(IFNULL(questionScore,0)) minScore FROM task WHERE questionNum = {questionNum} and `status` = 'T' and userNum < 3 GROUP BY scoreId HAVING (max(IFNULL(questionScore,0))-min(IFNULL(questionScore,0))) < {errorRate} ) t2 on t2.scoreId = t1.scoreId ", args);
            for (int i2 = 0; i2 < scoreList4.size(); i2++) {
                Object[] obj2 = scoreList4.get(i2);
                String scoreId2 = String.valueOf(obj2[0]);
                Double maxScore2 = Double.valueOf(String.valueOf(obj2[1]));
                Double minScore2 = Double.valueOf(String.valueOf(obj2[2]));
                String newscore2 = getAvgScore(maxScore2, minScore2);
                this.dao.updateScoreLeq(scoreId2, newscore2, uid);
                this.dao.deleteTaskUserNum3Leq(scoreId2);
            }
            this.dao.resettingWorkrecord(examPaperNum, groupNum);
            this.log.info("从题组管理界面修改阈值，删除多余的三评和裁决数据【questionNum:" + questionNum + ",subject:" + subject + ",grade:" + grade + ",exam:" + exam + "】，修改人【" + uid + "】");
        }
        if (flag) {
            this.dao.supplyRemarkData(groupNum2);
            this.dao.supplyTaskData(groupNum2);
        }
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void addThreeData(String exam, String grade, String subject, String questionNum, String errorRate, String uid, String patternType, String isParent, String groupType, String groupNum2, boolean flag) {
        String newscore;
        Double minDV;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("subject", subject);
        args.put("questionNum", questionNum);
        args.put("errorRate", errorRate);
        String examPaperNum = this.dao2._queryStr("select examPaperNum from exampaper where examNum = {exam} and gradeNum = {grade} and subjectNum = {subject} ", args);
        String groupNum = this.dao.querygroupNum(String.valueOf(examPaperNum), questionNum);
        if ("0".equals(isParent)) {
            updateErrorval(questionNum, errorRate);
        } else {
            updateErrorval_sub(questionNum, errorRate);
        }
        if ("0".equals(patternType)) {
            List<Object> taskUserNum3List = this.dao2._queryColList("select id from task where questionNum = {questionNum} and userNum = '3'", args);
            for (int i = 0; i < taskUserNum3List.size(); i++) {
                args.put("taskUserNum3i", taskUserNum3List.get(i));
                this.dao2._execute("delete from task where id = {taskUserNum3i} ", args);
            }
            this.dao2._execute("delete from remark where questionNum={questionNum} and isException='W'", args);
            List<?> _queryBeanList = this.dao2._queryBeanList("SELECT /* shard_host_HG=Write */ scoreId,exampaperNum,groupNum,questionNum,studentId,max(IFNULL(questionScore,0)) ext1,min(IFNULL(questionScore,0)) ext2 FROM task WHERE questionNum = {questionNum} AND `status` = 'T' GROUP BY scoreId HAVING COUNT(1)=2 AND (max(IFNULL(questionScore,0))-min(IFNULL(questionScore,0))) >= {errorRate} ", Task.class, args);
            for (int i2 = 0; i2 < _queryBeanList.size(); i2++) {
                Task t = (Task) _queryBeanList.get(i2);
                args.put("ExampaperNum", t.getExampaperNum());
                args.put("GroupNum", t.getGroupNum());
                args.put("QuestionNum", t.getQuestionNum());
                args.put("StudentId", t.getStudentId());
                args.put("TestingCentreId", t.getTestingCentreId());
                args.put("ScoreId", t.getScoreId());
                if (null == this.dao2._queryObject("select id from remark where scoreId = {ScoreId} and type='1' limit 1", args)) {
                    int rownum = getRemarkMaxRownum(String.valueOf(t.getExampaperNum()), t.getQuestionNum(), "1") + 1;
                    this.dao.reMarkF(t.getScoreId(), "", "-1", t.getQuestionNum(), t.getExampaperNum().intValue(), rownum, groupNum, t.getStudentId());
                    String newscore2 = getAvgScore(Double.valueOf(t.getExt1()), Double.valueOf(t.getExt2()));
                    this.dao.updateScoreLeq(t.getScoreId(), newscore2, uid);
                }
            }
        } else {
            List<Object> remark1List = this.dao2._queryColList("select id from remark where questionNum = {questionNum} and type = '1'", args);
            for (int i3 = 0; i3 < remark1List.size(); i3++) {
                args.put("remark1i", remark1List.get(i3));
                this.dao2._execute("delete from remark where id = {remark1i} ", args);
            }
            List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT scoreId,exampaperNum,groupNum,questionNum,studentId,max(IFNULL(questionScore,0)) ext1,min(IFNULL(questionScore,0)) ext2,testingCentreId FROM task WHERE questionNum = {questionNum} AND `status` = 'T' AND userNum < 3 GROUP BY scoreId HAVING COUNT(1)=2 AND (max(IFNULL(questionScore,0))-min(IFNULL(questionScore,0))) >= {errorRate} ", Task.class, args);
            for (int i4 = 0; i4 < _queryBeanList2.size(); i4++) {
                Task t2 = (Task) _queryBeanList2.get(i4);
                args.put("ExampaperNum", t2.getExampaperNum());
                args.put("GroupNum", t2.getGroupNum());
                args.put("QuestionNum", t2.getQuestionNum());
                args.put("StudentId", t2.getStudentId());
                args.put("TestingCentreId", t2.getTestingCentreId());
                args.put("ScoreId", t2.getScoreId());
                Object[] userNum3 = this.dao2._queryArray("select status,questionScore from task where scoreId = {ScoreId} and userNum = '3'", args);
                if (null == userNum3) {
                    args.put("GUIDStr", GUID.getGUIDStr());
                    this.dao2._execute("insert into task (id,scoreId,exampaperNum,groupNum,questionNum,questionScore,studentId,userNum,isException,isDelete,status,porder,insertUser,insertDate,updateUser,updateTime,testingCentreId) values ({GUIDStr},{ScoreId},{ExampaperNum},{GroupNum},{QuestionNum},'0',{StudentId},'3','F','F','F',1,'-1',now(),'-1',now(),{TestingCentreId} )", args);
                } else if ("T".equals(String.valueOf(userNum3[0]))) {
                    Double userNum3Score = Double.valueOf(String.valueOf(userNum3[1]));
                    Double maxScore = Double.valueOf(t2.getExt1());
                    Double minScore = Double.valueOf(t2.getExt2());
                    Double maxDvalue = Double.valueOf(Math.abs(userNum3Score.doubleValue() - maxScore.doubleValue()));
                    Double minDvalue = Double.valueOf(Math.abs(userNum3Score.doubleValue() - minScore.doubleValue()));
                    Double.valueOf(0.0d);
                    if (maxDvalue == minDvalue) {
                        newscore = String.valueOf(userNum3Score);
                        minDV = maxDvalue;
                    } else if (maxDvalue.doubleValue() < minDvalue.doubleValue()) {
                        newscore = getAvgScore(maxScore, userNum3Score);
                        minDV = maxDvalue;
                    } else {
                        newscore = getAvgScore(minScore, userNum3Score);
                        minDV = minDvalue;
                    }
                    this.dao.updateScoreLeq(t2.getScoreId(), newscore, uid);
                    if ("2".equals(patternType) && minDV.doubleValue() >= Double.valueOf(errorRate).doubleValue()) {
                        int rownum2 = getRemarkMaxRownum(String.valueOf(t2.getExampaperNum()), t2.getQuestionNum(), "1") + 1;
                        this.dao.reMarkF(t2.getScoreId(), "", "-1", t2.getQuestionNum(), t2.getExampaperNum().intValue(), rownum2, groupNum, t2.getStudentId());
                    }
                }
            }
        }
        this.log.info("从题组管理界面修改阈值，增加三评和裁决数据【questionNum:" + questionNum + ",subject:" + subject + ",grade:" + grade + ",exam:" + exam + "】，修改人【" + uid + "】");
        if (flag) {
            this.dao.supplyRemarkData(groupNum2);
            this.dao.supplyTaskData(groupNum2);
        }
    }

    public int updateErrorval(String questionNum, String errorRate) {
        Map args = new HashMap();
        args.put("errorRate", errorRate);
        args.put("questionNum", questionNum);
        this.log.info("updateErrorval sql：UPDATE define set errorRate = {errorRate} where id = {questionNum} ");
        return this.dao2._execute("UPDATE define set errorRate = {errorRate} where id = {questionNum} ", args);
    }

    public int updateErrorval_sub(String questionNum, String errorRate) {
        Map args = new HashMap();
        args.put("errorRate", errorRate);
        args.put("questionNum", questionNum);
        this.log.info("updateErrorval_sub sql：UPDATE subDefine set errorRate = {errorRate} where id = {questionNum}");
        return this.dao2._execute("UPDATE subDefine set errorRate = {errorRate} where id = {questionNum}", args);
    }

    public int getRemarkMaxRownum(String exampaperNum, String questionNum, String type) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        args.put("type", type);
        Object res = this.dao2._queryObject("select max(IFNULL(rownum,0)) as rownum from remark where examPaperNum={exampaperNum}  and questionNum={questionNum} and type ={type} ", args);
        return Integer.valueOf(null == res ? "0" : String.valueOf(res)).intValue();
    }

    public String getAvgScore(Double score1, Double score2) {
        double realscore1 = avg(score1.doubleValue(), score2.doubleValue());
        ServletActionContext.getServletContext();
        String scoreAvgOrRound = Configuration.getInstance().getScoreAvgOrRound();
        if ("0".equals(scoreAvgOrRound)) {
            return String.valueOf(realscore1);
        }
        int dd = String.valueOf(realscore1).indexOf(".");
        String score = String.valueOf(realscore1).substring(dd + 1, dd + 2);
        if (Double.parseDouble(score) > 5.0d) {
            return String.valueOf(Math.ceil(realscore1));
        }
        if (Double.parseDouble(score) < 5.0d) {
            return String.valueOf(Math.floor(realscore1));
        }
        return String.valueOf(realscore1);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer groupcount(String groupNum, String exampaperNum, String insertUser) {
        return this.dao.groupcount(groupNum, exampaperNum, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String querytaskwork(String examNum, String gradeNum, String subject) {
        return this.dao.querytaskwork(examNum, gradeNum, subject);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String groupworkcount(String exampaperNum, String groupNum, String insertUser) {
        return this.dao.groupworkcount(exampaperNum, groupNum, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List groupscorecount(String exampaperNum, String groupNum, String insertUser) {
        return this.dao.groupscorecount(exampaperNum, groupNum, insertUser);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Map allgroupworkcount(String exampaperNum, String groupNum) {
        return this.dao.allgroupworkcount(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Map groupavgcount(String exampaperNum, String groupNum) {
        return this.dao.groupavgcount(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Object[] queryworklv(String exampaperNum, String questionNum, String teacherNum, Double buchang) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        args.put("teacherNum", teacherNum);
        args.put("buchang", buchang);
        String countstr = this.dao2._queryStr("select count(1) from task  where exampaperNum={exampaperNum} and questionNum={questionNum} and insertUser={teacherNum}   and `status`='T' ", args);
        String sql = "select ROUND(questionScore,0),ROUND( count(1)/" + countstr + ",1)ext1 from task  where exampaperNum={exampaperNum} and questionNum={questionNum} and insertUser={teacherNum} and `status`='T'  GROUP BY questionScore";
        Map findMapList = this.dao2._queryOrderMap(sql, TypeEnum.StringObject, args);
        String sqldy5 = "select * from (select ROUND( count(1)/" + countstr + ",1)score1,exampaperNum,questionScore from task   where exampaperNum={exampaperNum} and questionNum={questionNum} and insertUser={teacherNum} and `status`='T' and questionScore=0)t1 LEFT JOIN (select ROUND( count(1)/" + countstr + ",1)score2,exampaperNum from task  where exampaperNum={exampaperNum} and questionNum={questionNum} and insertUser={teacherNum}  and `status`='T' and questionScore>0   and questionScore<={buchang} )t2 on 1=1 LEFT JOIN  (select ROUND( count(1)/" + countstr + ",1)score3,exampaperNum from task   where exampaperNum={exampaperNum} and questionNum={questionNum} and insertUser={teacherNum}  and `status`='T' and questionScore>{buchang}  and questionScore<={buchang} *2)t3 on 1=1 LEFT JOIN  (select ROUND( count(1)/" + countstr + ",1)score4,exampaperNum from task  where exampaperNum={exampaperNum}  and questionNum={questionNum} and insertUser={teacherNum}  and `status`='T' and questionScore>{buchang} *2 and questionScore<={buchang} *3)t4 on 1=1 LEFT JOIN (select ROUND( count(1)/" + countstr + ",1)score5,exampaperNum from task  where exampaperNum={exampaperNum}  and questionNum={questionNum} and insertUser={teacherNum}  and `status`='T'  and questionScore>{buchang} *3 and questionScore<={buchang} *4)t5 on 1=1 LEFT JOIN (select ROUND( count(1)/" + countstr + ",1)score6 ,exampaperNum from task  where exampaperNum={exampaperNum}  and questionNum={questionNum} and insertUser={teacherNum}  and `status`='T'  and questionScore>{buchang} *4 and questionScore<={buchang} *5)t6 on 1=1";
        List list3 = this.dao2._queryBeanList(sqldy5, Scorefd.class, args);
        String sqldy6 = "select * from (select ROUND( count(1)/" + countstr + ",1)score1,exampaperNum,questionScore from task   where exampaperNum={exampaperNum}  and questionNum={questionNum}  and `status`='T' and questionScore=0)t1 LEFT JOIN (select ROUND( count(1)/" + countstr + ",1)score2,exampaperNum from task  where exampaperNum={exampaperNum}  and questionNum={questionNum}  and `status`='T' and questionScore>0   and questionScore<={buchang} )t2 on 1=1 LEFT JOIN  (select ROUND( count(1)/" + countstr + ",1)score3,exampaperNum from task   where exampaperNum={exampaperNum}  and questionNum={questionNum}  and `status`='T' and questionScore>{buchang}  and questionScore<={buchang} *2)t3 on 1=1 LEFT JOIN  (select ROUND( count(1)/" + countstr + ",1)score4,exampaperNum from task  where exampaperNum={exampaperNum}  and questionNum={questionNum}  and `status`='T' and questionScore>{buchang} *2 and questionScore<={buchang} *3)t4 on 1=1 LEFT JOIN (select ROUND( count(1)/" + countstr + ",1)score5,exampaperNum from task  where exampaperNum={exampaperNum}  and questionNum={questionNum}  and `status`='T'  and questionScore>{buchang} *3 and questionScore<={buchang} *4)t5 on 1=1 LEFT JOIN (select ROUND( count(1)/" + countstr + ",1)score6 ,exampaperNum from task  where exampaperNum={exampaperNum}  and questionNum={questionNum}  and `status`='T'  and questionScore>{buchang} *4 and questionScore<={buchang} *5)t6 on 1=1";
        List list4 = this.dao2._queryBeanList(sqldy6, Scorefd.class, args);
        String countstr2 = this.dao2._queryStr("select count(1) from task  where exampaperNum={exampaperNum}  and questionNum={questionNum}   and `status`='T' ", args);
        String sql2 = "select ROUND(questionScore,0),ROUND( count(1)/" + countstr2 + ",1)ext1 from task  where exampaperNum={exampaperNum}  and questionNum={questionNum}  and `status`='T'  GROUP BY questionScore";
        Map findMapList2 = this.dao2._queryOrderMap(sql2, TypeEnum.StringObject, args);
        return new Object[]{findMapList, findMapList2, list3, list4};
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Object[] workjindu(String exampaperNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        List list1 = this.dao2._queryBeanList("SELECT z.groupNum,z.groupName,z.groupType, z.exampaperNum,z.count2,IFNULL(z.count,0) count1,  convert(IFNULL(z.count/z.count2 ,0)*100,decimal(10,2)) zb from(  \t\tSELECT z.groupNum,z.groupName,z.groupType, z.exampaperNum,   \t\t\tCASE qe.makType when 1 then ifnull(r.count*2,0)+ifnull(x.count,0) else ifnull(r.count,0) end count2,y.d_num count  FROM (  \t\tSELECT examPaperNum,groupNum,totalnum as  t_num ,groupName,groupType   FROM questiongroup  WHERE exampaperNum={exampaperNum}   )z  LEFT JOIN (  \t\t\tSELECT groupNum,makType from questiongroup_mark_setting where exampaperNum={exampaperNum}   )qe on z.groupNum=qe.groupNum left join (  \t\tSELECT count(DISTINCT studentId) count,exampapernum from regexaminee WHERE exampaperNum={exampaperNum}    )r on z.exampaperNum=r.exampaperNum LEFT JOIN  (  \t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num,count(DISTINCT(questionNum)) as tempcount     \t\tFROM task   WHERE exampaperNum={exampaperNum}  AND status='T'   GROUP BY groupNum    )y  ON z.groupNum = y.groupNum LEFT JOIN ( \t\t\tSELECT groupNum,count(1) count FROM task   WHERE exampaperNum={exampaperNum}  and userNum=3  GROUP BY groupNum   )x ON z.groupNum = x.groupNum WHERE z.groupNum IS NOT NULL   ORDER BY CASE  WHEN LOCATE('-',z.groupName)>0 THEN CONCAT(SUBSTR(z.groupName,1,POSITION('-' IN z.groupName)-1),'.01')*1   WHEN LOCATE('_',z.groupName)>0 THEN CONCAT(SUBSTR(z.groupName,1,POSITION('_' IN z.groupName)-1),'.02')*1   ELSE CONCAT(z.groupName,'.1')*1 END  ASC ,REPLACE(z.groupName,'_','.')*1  )z", AwardPoint.class, args);
        List list2 = this.dao2._queryBeanList("SELECT z.exampaperNum,sum(z.count) count2,SUM(z.d_num) count1,convert(IFNULL(sum(z.d_num)/SUM(z.count) ,0)*100,decimal(10,2)) zb  from(  SELECT z.groupNum,z.groupName,z.groupType, z.exampaperNum,  \t\tCASE qe.makType when 1 then ifnull(r.count*2,0)+ifnull(x.count,0) else ifnull(r.count,0) end count,IFNULL(y.d_num,0) d_num FROM (  \t\t\t\tSELECT examPaperNum,groupNum,groupName,groupType FROM questiongroup  WHERE exampaperNum={exampaperNum}  and stat='1'  \t\t)z  LEFT JOIN (  \t\t\t\t\tSELECT groupNum,makType from questiongroup_mark_setting where exampaperNum={exampaperNum}   \t\t)qe on z.groupNum=qe.groupNum LEFT JOIN (  \t\t\t\tSELECT count(DISTINCT studentId) count,exampapernum from regexaminee WHERE exampaperNum={exampaperNum}   \t\t) r on z.exampapernum=r.exampapernum LEFT JOIN (  \t\t\t\t\tSELECT groupNum,cast(count(1)/count(DISTINCT(questionNum)) as signed)  as  d_num,count(DISTINCT(questionNum)) as tempcount    \t\t\t\t\tFROM task   WHERE exampaperNum={exampaperNum}  AND status='T'   GROUP BY groupNum    \t\t)y ON z.groupNum = y.groupNum LEFT JOIN (  \t\t\tSELECT groupNum,count(1) count FROM task   WHERE exampaperNum={exampaperNum}  and userNum=3  GROUP BY groupNum   \t\t)x ON z.groupNum = x.groupNum )z", AwardPoint.class, args);
        return new Object[]{list1, list2};
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List queryquestion(String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        return this.dao2._queryBeanList("select d.id groupNum,d.questionNum groupName from (select questionNum from questiongroup_question where exampaperNum={exampaperNum}   and groupNum={groupNum}')qq left join (select a.id,a.questionNum FROM ((select id,questionnum from define where exampaperNum={exampaperNum}  and questiontype='1') union all (select id,questionnum  from subdefine WHERE exampaperNum={exampaperNum}  ))a  )d on qq.questionNum=d.id", QuestionGroup.class, args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String questioncount(String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        return this.dao2._queryStr("select count(1) from questiongroup_question where exampaperNum={exampaperNum}  and groupNum={groupNum} ", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void csljc(String scoreId, String insertUser) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        args.put("insertUser", insertUser);
        this.dao2._execute("insert into csljc(scoreid,insertuser,insertdate)VALUES({scoreId},{insertUser},now()) ", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getweifenfacount(String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        return this.dao2._queryStr("select count(1) from task where exampapernum={exampaperNum}  and groupNum={groupNum}  and insertUser='-1' and status='F' LIMIT 1", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getszwork(String exampaperNum, String groupNum, String insertUser) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        return this.dao2._queryStr("select num from quota where exampapernum={exampaperNum}  and groupNum={groupNum}  and insertuser={insertUser} ", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List querybjda(String exampaperNum, String groupNum, String description, int pagestart) {
        String sql;
        if (pagestart == -1) {
            sql = " select id,exampaperNum,questionNum,img,imgpath,description from  answerquestionimage where exampaperNum={exampaperNum}  and  questionNum={groupNum}  group by  description";
        } else {
            sql = " select id,exampaperNum,questionNum,img,imgpath,description from  answerquestionimage where exampaperNum={exampaperNum}  and  questionNum={groupNum} and description={description} limit {pagestart},1";
        }
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        args.put("description", description);
        args.put("pagestart", Integer.valueOf(pagestart));
        return this.dao2._queryBeanList(sql, Answerquestionimagebean.class, args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String querybjdacount(String exampaperNum, String groupNum, String description, int pagestart) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        args.put("description", description);
        return this.dao2._queryStr(" select count(1) from  answerquestionimage where exampaperNum={exampaperNum}  and  questionNum={groupNum} and description={description} ", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String queryquota(String exampaperNum, String groupNum, String insertUser) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        return this.dao2._queryStr(" select count(1) from quota where exampaperNum={exampaperNum}  and groupNum={groupNum}  and insertUser={insertUser} ", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Imgpath queryimgurl(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select id imgpathid,location locationurl,filename tableimg  from imgpath where examNum={examNum} and selected='1'", Imgpath.class, args);
        return ip;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void createtempanswer(TempAnswer ta) {
        Map args = new HashMap();
        args.put("ExamPaperNum", ta.getExamPaperNum());
        args.put("QuestionNum", ta.getQuestionNum());
        args.put("Imgpath", ta.getImgpath());
        args.put("Img", ta.getImg());
        args.put("Width", ta.getWidth());
        args.put("Height", ta.getHeight());
        args.put("InsertUser", ta.getInsertUser());
        args.put("InsertDate", ta.getInsertDate());
        this.dao2._execute("insert into tempanswer(examPaperNum,questionNum,imgpath,img,width,height,insertUser,insertDate) values({ExamPaperNum},{QuestionNum},{Imgpath} ,{Img},{Width},{Height},{InsertUser},{InsertDate} )", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getyipanscore(String exampaperNum, String groupNum, String questionNum, String starttime, String endtime, String startscore, String endscore, String inserUser, String startval, String indexval) throws SQLException {
        return this.dao.getyipanscore(exampaperNum, groupNum, questionNum, starttime, endtime, startscore, endscore, inserUser, startval, indexval);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List finishTasklist(String exampaperNum, String groupNum, String questionNum, String starttime, String endtime, String startscore, String endscore, String inserUser, String startval, String indexval) throws SQLException {
        return this.dao.finishTasklist(exampaperNum, groupNum, questionNum, starttime, endtime, startscore, endscore, inserUser, startval, indexval);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getQuestionNumList(String groupNum) {
        return this.dao.getQuestionNumList(groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String queryyipanllcount(String groupNum, String insertUser) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("insertUser", insertUser);
        return this.dao2._queryStr("select count(1) from ( select id from task where groupNum={groupNum} and insertUser={insertUser} and `status`='T' GROUP BY studentId)t", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String queryquestionNumhb(String groupNum) {
        Map args = StreamMap.create().put("groupNum", (Object) groupNum);
        return this.dao2._queryStr("select questionNum from questiongroup_question where groupNum={groupNum} LIMIT 1", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String queryteacherteakcount(String insertUser, String groupNum) {
        Map args = StreamMap.create().put("groupNum", (Object) groupNum).put("insertUser", (Object) insertUser);
        return this.dao2._queryStr("select count(1) from task where groupNum={groupNum}  and insertUser={insertUser} and status='F'", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String queryjudgetype(String questionNum) {
        return this.dao.queryjudgetype(questionNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void resettingWorkrecord(String exampaperNum, String groupNum) {
        this.dao.resettingWorkrecord(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public QuestionGroupInfo getQuestionGroupInfo(String exampaperNum, String groupNum, ServletContext context) {
        return this.dao.getQuestionGroupInfo(exampaperNum, groupNum, context);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer deleteCpMarkerror(String scoreId) {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId);
        return Integer.valueOf(this.dao2._execute("delete from markerror where scoreId = {scoreId} and type = '2'", args));
    }

    public static String formatDouble(double d) {
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        double num = bg.doubleValue();
        if (Math.round(num) - num == 0.0d) {
            return String.valueOf((long) num);
        }
        return String.valueOf(num);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Question_scorerule> getScoreRule(String groupNum) {
        new ArrayList();
        new ArrayList();
        Map args = StreamMap.create().put("groupNum", (Object) groupNum);
        List _queryBeanList = this.dao2._queryBeanList("SELECT qgq.groupNum,CAST(qgq.questionNum as CHAR(23)) questionNum,ifnull(d.questionNum,sd.questionNum) groupName,ifnull(qs.scoreRuleType,1) scoreRuleType,qs.questionRule,qs.questionbuchang,case when qs.scoreRuleType=1 or qs.scoreRuleType is null then IFNULL(qs.questionbuchang,1) end buchang,scoreTime,ifnull(d.fullScore,sd.fullScore) fullScore from questiongroup_question qgq  LEFT JOIN question_scorerule qs  on qgq.questionNum = qs.questionNum  LEFT JOIN question_scoretime qsi  on qgq.groupNum = qsi.groupNum  LEFT JOIN define d on qgq.questionNum=d.id  left join subdefine sd on qgq.questionNum=sd.id WHERE qgq.groupNum = {groupNum} or qgq.questionNum={groupNum} ORDER BY d.orderNum,sd.orderNum ", Question_scorerule.class, args);
        for (int i = 0; i < _queryBeanList.size(); i++) {
            String stepStr = "";
            List<String> strlist = new ArrayList<>();
            Question_scorerule qs = (Question_scorerule) _queryBeanList.get(i);
            if ("2".equals(qs.getScoreRuleType())) {
                String[] arr = qs.getQuestionRule().split(Const.STRING_SEPERATOR);
                for (int h = 0; h < arr.length; h++) {
                    stepStr = stepStr + formatDouble(Double.parseDouble(arr[h])) + Const.STRING_SEPERATOR;
                    strlist.add(formatDouble(Double.parseDouble(arr[h])));
                }
            } else if ("1".equals(qs.getScoreRuleType()) && qs.getQuestionbuchang() != null) {
                double buchang = Double.parseDouble(qs.getQuestionbuchang());
                double fullScore = Double.parseDouble(qs.getFullScore());
                int multiple = (int) Math.floor(fullScore / buchang);
                int remainder = (int) (fullScore % buchang);
                double buchangVal = 0.0d;
                for (int h2 = 0; h2 <= multiple; h2++) {
                    stepStr = stepStr + formatDouble(buchangVal) + Const.STRING_SEPERATOR;
                    strlist.add(formatDouble(buchangVal));
                    buchangVal += buchang;
                }
                if (remainder != 0) {
                    strlist.add(formatDouble(fullScore));
                    stepStr = stepStr + formatDouble(fullScore) + Const.STRING_SEPERATOR;
                }
            }
            if (qs.getBuchang() != null) {
                qs.setBuchang(formatDouble(Double.parseDouble(qs.getBuchang())));
            }
            qs.setExt1(stepStr);
            qs.setqList1(strlist);
        }
        return _queryBeanList;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getYueJuanWay(String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("exampaperNum", exampaperNum);
        String s = this.dao2._queryStr("select yuejuanWay from questiongroup where exampaperNum={exampaperNum} and groupNum={groupNum}", args);
        return s;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getGroupTel(String exampaperNum, String groupNum, String insertUser) {
        Map args = new HashMap();
        args.put("insertUser", insertUser);
        args.put("groupNum", groupNum);
        args.put("exampaperNum", exampaperNum);
        User uu = (User) this.dao2._queryBean("select username from user where id={insertUser} ", User.class, args);
        String str = "【" + uu.getUsername() + "】&nbsp;&nbsp;&nbsp;&nbsp;";
        Define define = (Define) this.dao2._queryBean("SELECT /* shard_host_HG=Write */ d.category from questiongroup qp INNER JOIN ( \tSELECT id,examPaperNum,category from define where id= {groupNum}\tUNION  \tSELECT id,examPaperNum,category from subdefine where id= {groupNum}) d on qp.groupNum=d.id", Define.class, args);
        String finalExamPaper = exampaperNum;
        args.put("finalExamPaper", finalExamPaper);
        if (define != null) {
            int category = define.getCategory().intValue();
            args.put("category", Integer.valueOf(category));
            if (category != Integer.parseInt(exampaperNum)) {
                String count = this.dao2._queryStr("select count(1) from questiongroup_user q where q.exampaperNum={category} and q.userType='2'", args);
                if (!"0".equals(count)) {
                    finalExamPaper = category + "";
                    args.put("finalExamPaper", finalExamPaper);
                }
            }
        }
        List<?> _queryBeanList = this.dao2._queryBeanList("select u.username,u.realname,u.mobile,s.schoolName from questiongroup_user q   INNER JOIN user u on q.userNum=u.id   INNER JOIN school s on u.schoolnum=s.id   where q.exampaperNum={finalExamPaper} and q.userType='2'", User.class, args);
        String str2 = str + "科目组长：";
        int i = 0;
        while (true) {
            if (i >= _queryBeanList.size()) {
                break;
            }
            User u = (User) _queryBeanList.get(i);
            if (_queryBeanList.size() > 1) {
                str2 = str2 + "<a class='moreTeacher' name='subject' val='" + finalExamPaper + "'>【" + u.getUsername() + "--" + u.getRealname() + "--" + u.getSchoolName() + "--" + u.getMobile() + "】...</a>";
                break;
            }
            str2 = str2 + "【" + u.getUsername() + "--" + u.getRealname() + "--" + u.getSchoolName() + "--" + u.getMobile() + "】";
            i++;
        }
        List<?> _queryBeanList2 = this.dao2._queryBeanList("SELECT u.username,u.realname,u.mobile,s.schoolName from questiongroup_user q  INNER JOIN user u on q.userNum=u.id   INNER JOIN school s on u.schoolnum=s.id   where q.exampaperNum={exampaperNum}  and q.groupNum={groupNum} and q.userType='1'", User.class, args);
        String str3 = (str2 + "&nbsp;&nbsp;&nbsp;&nbsp;") + "题组长：";
        int i2 = 0;
        while (true) {
            if (i2 >= _queryBeanList2.size()) {
                break;
            }
            User u1 = (User) _queryBeanList2.get(i2);
            if (_queryBeanList2.size() > 1) {
                str3 = str3 + "<a class='moreTeacher' name='group' val='" + groupNum + "'>【" + u1.getUsername() + "--" + u1.getRealname() + "--" + u1.getSchoolName() + "--" + u1.getMobile() + "】...</a>";
                break;
            }
            str3 = str3 + "【" + u1.getUsername() + "--" + u1.getRealname() + "--" + u1.getSchoolName() + "--" + u1.getMobile() + "】";
            i2++;
        }
        return str3;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getGroupInfo(String exampaperNum, String groupNum) {
        Map args = StreamMap.create().put("groupNum", (Object) groupNum);
        Subject s = (Subject) this.dao2._queryBean("SELECT /* shard_host_HG=Write */ b.gradeName ext1,s.subjectName,q.groupName ext2 from questiongroup q   INNER JOIN exampaper e on q.examPaperNum=e.examPaperNum    INNER JOIN basegrade b on e.gradeNum=b.gradeNum   INNER JOIN subject s on e.subjectNum=s.subjectNum   where q.groupNum={groupNum} ", Subject.class, args);
        String str = "";
        if (s != null) {
            str = str + "[" + s.getExt1() + "--" + s.getSubjectName() + "--" + s.getExt2() + "]";
        }
        return str;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getQuestionStat(String exampaperNum, String groupNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) groupNum);
        String str = this.dao2._queryStr("SELECT /* shard_host_HG=Write */ stat from questiongroup where examPaperNum={exampaperNum}  and groupNum={groupNum} ", args);
        return str;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public QuestionGroup getQuestionAutoCommitInfo(String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        return (QuestionGroup) this.dao2._queryBean("SELECT ifnull(autoCommitForbid,0) autoCommitForbid,ifnull(aotoCommitDefault,1) aotoCommitDefault,ifnull(correctForbid,0) correctForbid from questiongroup where groupNum={groupNum} ", QuestionGroup.class, args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public QuestionGroup groupInfo(String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        return (QuestionGroup) this.dao2._queryBean("SELECT s.subjectName,bg.gradeName,q.groupNum,q.groupName,q.stat,q.scancompleted,ifnull(q.autoCommitForbid,0) autoCommitForbid,ifnull(q.aotoCommitDefault,1) aotoCommitDefault,ifnull(q.correctForbid,0) correctForbid,t.`status` ext1 from questiongroup q INNER JOIN exampaper e on q.exampaperNum=e.examPaperNum INNER JOIN subject s on e.subjectNum=s.subjectNum INNER JOIN basegrade bg on e.gradeNum=bg.gradeNum LEFT JOIN(  SELECT groupNum,`status` from testquestion_define where groupNum={groupNum}  limit 1 )t on q.groupNum=t.groupNum where q.groupNum={groupNum} ", QuestionGroup.class, args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public boolean getYueJuanStatus(String groupNum) {
        Map args = StreamMap.create().put("groupNum", (Object) groupNum);
        String s = this.dao2._queryStr("SELECT `status` from testquestion_define where groupNum={groupNum} limit 1", args);
        return s != null && this.dao2._queryStr("SELECT `status` from testquestion_define where groupNum={groupNum} limit 1", args).equals("T");
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getLeader(String exampaperNum, String groupNum) {
        List list = new ArrayList();
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        list.add(this.dao2._queryBeanList("select sj.subjectName,u.username,u.realname,u.mobile,s.schoolName from questiongroup_user q INNER JOIN user u on q.userNum=u.id INNER JOIN school s on u.schoolnum=s.id INNER JOIN exampaper e on q.exampaperNum=e.examPaperNum INNER JOIN subject sj on e.subjectNum=sj.subjectNum where e.pexamPaperNum={exampaperNum}  and q.userType='2'", User.class, args));
        list.add(this.dao2._queryBeanList("select sj.subjectName,u.username,u.realname,u.mobile,s.schoolName from questiongroup_user q INNER JOIN user u on q.userNum=u.id INNER JOIN school s on u.schoolnum=s.id INNER JOIN exampaper e on q.exampaperNum=e.examPaperNum INNER JOIN subject sj on e.subjectNum=sj.subjectNum where q.groupNum={groupNum} and q.userType='1'", User.class, args));
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getEnforce() {
        Object enforce = this.dao2.queryObject("SELECT MAX(enforce) from schoolgroup");
        String enforce1 = (enforce == null || "".equals(enforce)) ? "0" : String.valueOf(enforce);
        return enforce1;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Task> getPanFenTableList(Map<String, String> map, String userId) {
        return this.dao.getPanFenTableList(map, userId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Map getReferenceNum(Map<String, String> map, String userId) {
        return this.dao.getReferenceNum(map, userId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getNoOpenQuestionGroupList(Map<String, String> map, String userId) {
        return this.dao.getNoOpenQuestionGroupList(map, userId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List getNoOpenTestingCentreList(Map<String, String> map, String userId) {
        return this.dao.getNoOpenTestingCentreList(map, userId);
    }

    public CellStyle getStyleCell(ExcelWriter writer) {
        Workbook wb = writer.getWorkbook();
        CellStyle cellStyle = wb.createCellStyle();
        return cellStyle;
    }

    public void writeCellByColor(ExcelWriter writer, IndexedColors color, int x, int y) {
        Workbook wb = writer.getWorkbook();
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setColor(color.index);
        font.setBold(true);
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        writer.setStyle(cellStyle, x, y);
    }

    public void writeRowByColor(ExcelWriter writer, List redRowList, IndexedColors color, int column) {
        for (int i = 0; i < redRowList.size(); i++) {
            int x = Integer.parseInt(String.valueOf(redRowList.get(i)));
            for (int y = 0; y < column; y++) {
                writeCellByColor(writer, color, y, x);
            }
        }
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String exportTeacherWorkStatus(Map<String, String> map, String rootPath, String userId, String exportType, String isshowAllPhone) {
        this.log.info("---教师工作量状态-导出：--- " + userId + "---#######################查询数据-开始 ####################### ");
        List<Map<String, Object>> list = getTeacherWorkStatus(map, userId);
        this.log.info("---教师工作量状态-导出：--- " + userId + "---#######################查询数据-结束 ####################### ");
        String folderPath = "ExportFolder/teacherWorkStatus/" + userId + "/";
        String filePath = folderPath + "教师工作量状态表.xlsx";
        this.log.info("---教师工作量状态-导出：--- " + userId + "---文件【教师工作量状态表.xlsx】#######################开始导出 ####################### ");
        ExcelWriter writer = ExcelUtil.getWriter(rootPath + folderPath + "教师工作量状态表.xlsx");
        String[] header1 = new String[0];
        String[] header2 = new String[0];
        String[] header3 = {"", "", "", "", "", "", "", "", "应判人数", "阅卷人数", "未判总量", "预定量", "未判量", "未判率", "校未判"};
        List<String> rowHead1 = CollUtil.newArrayList(header1);
        List<String> rowHead2 = CollUtil.newArrayList(header2);
        List<String> rowHead3 = CollUtil.newArrayList(header3);
        writer.writeRow(rowHead1);
        writer.writeRow(rowHead2);
        writer.writeRow(rowHead3);
        writer.merge(0, 0, 0, 14, "教师工作量状态", false);
        writer.merge(1, 2, 0, 0, "年级", false);
        writer.merge(1, 2, 1, 1, "科目", false);
        writer.merge(1, 2, 2, 2, "题号", false);
        writer.merge(1, 2, 3, 3, "学校", false);
        writer.merge(1, 2, 4, 4, "教师ID", false);
        writer.merge(1, 2, 5, 5, "教师姓名", false);
        writer.merge(1, 2, 6, 6, "是否在阅", false);
        writer.merge(1, 2, 7, 7, "扫描状态", false);
        writer.merge(1, 1, 8, 10, "总工作量", false);
        writer.merge(1, 1, 11, 14, "个人工作量", false);
        List redRowList = new ArrayList();
        List YellowRowList = new ArrayList();
        List resultList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> aa = list.get(i);
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            linkedHashMap.put("gradeName", aa.get("gradeName"));
            linkedHashMap.put("subjectName", aa.get("subjectName"));
            linkedHashMap.put("groupName", aa.get("groupName"));
            linkedHashMap.put("schoolName", aa.get("schoolName"));
            String teacherNum = Convert.toStr(aa.get("teacherNum"), "");
            String teacherName = Convert.toStr(aa.get("teacherName"), "");
            if (isshowAllPhone.equals("0")) {
                if (ReUtil.isMatch("^1[3456789]\\d{9}$", teacherNum)) {
                    teacherNum = StrUtil.hide(teacherNum, 3, 7);
                }
                if (StrUtil.isNotEmpty(teacherName)) {
                    teacherName = teacherName.substring(0, 1) + "**";
                }
            }
            linkedHashMap.put("teacherNum", teacherNum);
            linkedHashMap.put("teacherName", teacherName);
            linkedHashMap.put("isYueJuan", aa.get("isYueJuan"));
            linkedHashMap.put("scancompleted", aa.get("scancompleted"));
            linkedHashMap.put("regcount", aa.get("regcount"));
            linkedHashMap.put("userzongcount", aa.get("userzongcount"));
            linkedHashMap.put("weipanzongshu", aa.get("weipanzongshu"));
            linkedHashMap.put("gerenyudingshu", aa.get("gerenyudingshu"));
            linkedHashMap.put("weipanshu", aa.get("weipanshu"));
            linkedHashMap.put("weipanlv", aa.get("weipanlv"));
            linkedHashMap.put("xuexiaoweipanshu", aa.get("xuexiaoweipanshu"));
            String weipanlv = String.valueOf(aa.get("weipanlv"));
            String exampaperyipan = String.valueOf(aa.get("exampaperyipan"));
            if (!"--".equals(weipanlv) && Double.parseDouble(weipanlv) >= 100.0d) {
                if ("0".equals(exampaperyipan)) {
                    redRowList.add(Integer.valueOf(i + 3));
                } else {
                    YellowRowList.add(Integer.valueOf(i + 3));
                }
            }
            resultList.add(linkedHashMap);
        }
        writer.setColumnWidth(3, 13);
        writer.setColumnWidth(4, 13);
        writer.write(resultList);
        writeRowByColor(writer, redRowList, IndexedColors.RED1, 15);
        writeRowByColor(writer, YellowRowList, IndexedColors.CORNFLOWER_BLUE, 15);
        writer.close();
        this.log.info("---教师工作量状态-导出：--- " + userId + "---文件【教师工作量状态表.xlsx】#######################导出结束 ####################### ");
        return filePath;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String exportTeacherWorkStatus2(ServletContext context, Map<String, String> map, String rootPath, String userId, String exportType, String isshowAllPhone) {
        this.log.info("---教师工作量状态-导出：--- " + userId + "---#######################查询数据-开始 ####################### ");
        List<Map<String, Object>> list = getTeacherWorkStatus2(context, map, userId);
        this.log.info("---教师工作量状态-导出：--- " + userId + "---#######################查询数据-结束 ####################### ");
        String folderPath = "ExportFolder/teacherWorkStatus/" + userId + "/";
        String filePath = folderPath + "教师工作量状态表.xlsx";
        this.log.info("---教师工作量状态-导出：--- " + userId + "---文件【教师工作量状态表.xlsx】#######################开始导出 ####################### ");
        ExcelWriter writer = ExcelUtil.getWriter(rootPath + folderPath + "教师工作量状态表.xlsx");
        String[] header1 = new String[0];
        String[] header2 = new String[0];
        String[] header3 = {"", "", "", "", "", "", "", "", "应判人数", "阅卷人数", "未判总量", "预定量", "未判量", "未判率", "校未判"};
        List<String> rowHead1 = CollUtil.newArrayList(header1);
        List<String> rowHead2 = CollUtil.newArrayList(header2);
        List<String> rowHead3 = CollUtil.newArrayList(header3);
        writer.writeRow(rowHead1);
        writer.writeRow(rowHead2);
        writer.writeRow(rowHead3);
        writer.merge(0, 0, 0, 14, "教师工作量状态", false);
        writer.merge(1, 2, 0, 0, "年级", false);
        writer.merge(1, 2, 1, 1, "科目", false);
        writer.merge(1, 2, 2, 2, "题号", false);
        writer.merge(1, 2, 3, 3, "学校", false);
        writer.merge(1, 2, 4, 4, "教师ID", false);
        writer.merge(1, 2, 5, 5, "教师姓名", false);
        writer.merge(1, 2, 6, 6, "是否在阅", false);
        writer.merge(1, 2, 7, 7, "扫描状态", false);
        writer.merge(1, 1, 8, 10, "总工作量", false);
        writer.merge(1, 1, 11, 14, "个人工作量", false);
        List redRowList = new ArrayList();
        List YellowRowList = new ArrayList();
        List resultList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> aa = list.get(i);
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            linkedHashMap.put("gradeName", aa.get("gradeName"));
            linkedHashMap.put("subjectName", aa.get("subjectName"));
            linkedHashMap.put("groupName", aa.get("groupName"));
            linkedHashMap.put("schoolName", aa.get("schoolName"));
            String teacherNum = Convert.toStr(aa.get("teacherNum"), "");
            String teacherName = Convert.toStr(aa.get("teacherName"), "");
            if (isshowAllPhone.equals("0")) {
                if (ReUtil.isMatch("^1[3456789]\\d{9}$", teacherNum)) {
                    teacherNum = StrUtil.hide(teacherNum, 3, 7);
                }
                if (StrUtil.isNotEmpty(teacherName)) {
                    teacherName = teacherName.substring(0, 1) + "**";
                }
            }
            linkedHashMap.put("teacherNum", teacherNum);
            linkedHashMap.put("teacherName", teacherName);
            linkedHashMap.put("isYueJuan", aa.get("isYueJuan"));
            linkedHashMap.put("scancompleted", aa.get("scancompleted"));
            linkedHashMap.put("regcount", aa.get("regcount"));
            linkedHashMap.put("userzongcount", aa.get("userzongcount"));
            linkedHashMap.put("weipanzongshu", aa.get("weipanzongshu"));
            linkedHashMap.put("gerenyudingshu", aa.get("gerenyudingshu"));
            linkedHashMap.put("weipanshu", aa.get("weipanshu"));
            linkedHashMap.put("weipanlv", aa.get("weipanlv"));
            linkedHashMap.put("xuexiaoweipanshu", aa.get("xuexiaoweipanshu"));
            String weipanlv = String.valueOf(aa.get("weipanlv"));
            String exampaperyipan = String.valueOf(aa.get("exampaperyipan"));
            if (!"--".equals(weipanlv) && Double.parseDouble(weipanlv) >= 100.0d) {
                if ("0".equals(exampaperyipan)) {
                    redRowList.add(Integer.valueOf(i + 3));
                } else {
                    YellowRowList.add(Integer.valueOf(i + 3));
                }
            }
            resultList.add(linkedHashMap);
        }
        writer.setColumnWidth(3, 13);
        writer.setColumnWidth(4, 13);
        writer.write(resultList);
        writeRowByColor(writer, redRowList, IndexedColors.RED1, 15);
        writeRowByColor(writer, YellowRowList, IndexedColors.CORNFLOWER_BLUE, 15);
        writer.close();
        this.log.info("---教师工作量状态-导出：--- " + userId + "---文件【教师工作量状态表.xlsx】#######################导出结束 ####################### ");
        return filePath;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Map<String, Object>> getTeacherWorkStatus(Map<String, String> map, String userId) {
        String examNum = map.get(Const.EXPORTREPORT_examNum);
        String subjectNum = map.get(Const.EXPORTREPORT_subjectNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String groupNum = map.get("groupNum");
        String completeStatus = map.get("completeStatus");
        String workStatus = map.get("workStatus");
        String isYueJuan = map.get("isYueJuan");
        String schoolGroupNum = map.get("schoolGroupNum");
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String orderType = map.get("orderType");
        List list = new ArrayList();
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        ResultSet rs = null;
        try {
            try {
                pstat = conn.prepareCall("{call teacherWorkStatus(?,?,?,?,?,?,?,?,?,?)}");
                pstat.setString(1, examNum);
                pstat.setString(2, subjectNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, groupNum);
                pstat.setString(5, completeStatus);
                pstat.setString(6, workStatus);
                pstat.setString(7, isYueJuan);
                pstat.setString(8, schoolGroupNum);
                pstat.setString(9, schoolNum);
                pstat.setString(10, orderType);
                pstat.executeQuery();
                rs = pstat.getResultSet();
                ResultSetMetaData rsm = rs.getMetaData();
                int col = rsm.getColumnCount();
                String[] colName = new String[col];
                for (int i = 0; i < col; i++) {
                    colName[i] = rsm.getColumnName(i + 1);
                }
                while (rs.next()) {
                    HashMap hashMap = new HashMap();
                    for (int j = 0; j < colName.length; j++) {
                        hashMap.put(colName[j], rs.getString(j + 1));
                    }
                    list.add(hashMap);
                }
                DbUtils.close(rs, pstat, conn);
                return list;
            } catch (SQLException e) {
                this.log.info("", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(rs, pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Map<String, Object>> getTeacherWorkStatus2(ServletContext context, Map<String, String> map, String userId) {
        String gerenyudingshu;
        String weipanshu;
        String weipanlv;
        String xuexiaoweipanshu;
        Map<String, Map<String, Object>> yuejuanProgressMap = (Map) context.getAttribute(Const.yuejuanProgressMap);
        new ArrayList();
        List<Map<String, Object>> teacherWorkProgressDataMapList = new ArrayList<>();
        Map<String, Object> teaWorkloadProgress = new HashMap<>();
        List<Map<String, Object>> questionGroupProgress = new ArrayList<>();
        if (CollUtil.isNotEmpty(yuejuanProgressMap)) {
            String gradeStr = "";
            String subjectStr = "";
            String groupStr = "";
            String schoolGroupNumStr = "";
            if (!"-1".equals(map.get(Const.EXPORTREPORT_gradeNum))) {
                gradeStr = " and ep.gradeNum={gradeNum} ";
            }
            if (!"-1".equals(map.get(Const.EXPORTREPORT_subjectNum))) {
                subjectStr = " and ep.subjectNum={subjectNum}";
            }
            if (!"-1".equals(map.get("groupNum"))) {
                groupStr = " where (xuanzuonum ={groupNum} or concat(xuanzuonum,xuanzuopaixu) ={groupNum})";
            }
            if (!"-1".equals(map.get("schoolGroupNum"))) {
                schoolGroupNumStr = " where sg.schoolGroupNum={schoolGroupNum}  ";
            }
            if (!"-1".equals(map.get(Const.EXPORTREPORT_schoolNum))) {
            }
            String examPaperNumSql = " select exampaperNum,gradeName,subjectName,GROUP_CONCAT(groupNum ORDER BY tipaixu) groupNum,GROUP_CONCAT(groupName ORDER BY tipaixu) groupName,scancompleted,userzongNum from   (select DISTINCT ep.exampaperNum,gradeName,subjectName,que.groupNum,groupName,scancompleted,userzongNum,if(d.choosename is null,if(LENGTH(d1.choosename)>1,d1.choosename,que.groupNum),if(LENGTH(d.choosename)>1,d.choosename,que.groupNum))  xuanzuonum ,if(d.choosename is null and LENGTH(d1.choosename)>1,sd.orderNum,0) xuanzuopaixu  ,if(d.id is null,d1.orderNum*1000+sd.orderNum,d.orderNum*1000) tipaixu  from exampaper ep  left join grade g on ep.gradeNum=g.gradeNum and g.isdelete='F'  left join subject sub on ep.subjectNum=sub.subjectNum and sub.isdelete='F'  left join questiongroup que on ep.examPaperNum=que.exampaperNum  left join  (  select qu.groupNum groupNum,qu.userNum userNum,u.schoolnum schoolnum,ifnull(sg.schoolGroupNum,-1) zubie,count(1) userzongNum from  questiongroup_user qu  INNER join user u on u.id=qu.userNum and u.usertype='1'  LEFT  join schoolgroup sg on sg.schoolNum=u.schoolNum " + schoolGroupNumStr + " group by groupNum  ) qu on  qu.groupNum=que.groupNum  LEFT join define d on que.groupNum=d.id  LEFT join subdefine sd on que.groupNum=sd.id  LEFT join define d1 on sd.pid=d1.id  where ep.examNum={examNum} " + gradeStr + subjectStr + " and ep.isDelete='F' order by ep.exampaperNum,qu.groupNum )a " + groupStr + " GROUP BY xuanzuonum,xuanzuopaixu order by groupNum";
            List<Map<String, Object>> examPaperNumList = this.dao2._queryMapList(examPaperNumSql, TypeEnum.StringObject, map);
            Map<String, Object> exampaperNumMap = (Map) examPaperNumList.stream().collect(Collectors.toMap(e -> {
                return e.get("exampaperNum").toString();
            }, Function.identity(), (key1, key2) -> {
                return key2;
            }));
            Map<String, Map<String, Object>> yuejuanProgressMap2 = (Map) yuejuanProgressMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap((v0) -> {
                return v0.getKey();
            }, (v0) -> {
                return v0.getValue();
            }, (oldValue, newValue) -> {
                return oldValue;
            }, LinkedHashMap::new));
            if (examPaperNumList.size() > 0) {
                for (String key : yuejuanProgressMap2.keySet()) {
                    for (String key22 : exampaperNumMap.keySet()) {
                        if (key.equals(key22)) {
                            teaWorkloadProgress.putAll((Map) yuejuanProgressMap2.get(key).get("teaWorkloadProgress"));
                            teaWorkloadProgress = (Map) teaWorkloadProgress.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap((v0) -> {
                                return v0.getKey();
                            }, (v0) -> {
                                return v0.getValue();
                            }, (oldValue2, newValue2) -> {
                                return oldValue2;
                            }, LinkedHashMap::new));
                            questionGroupProgress.addAll((List) yuejuanProgressMap2.get(key).get("questionGroupProgress"));
                            Comparator<Map<String, Object>> c1 = Comparator.comparing(m1 -> {
                                return Convert.toInt(m1.get("exampaperNum"), 0);
                            });
                            Comparator<Map<String, Object>> c2 = ChineseCharacterUtil.sortByPinyinOfMap("chooseName");
                            Comparator<Map<String, Object>> c3 = ChineseCharacterUtil.sortByPinyinOfMap("groupNum");
                            questionGroupProgress = (List) questionGroupProgress.stream().sorted(c1.thenComparing(c2).thenComparing(c3)).collect(Collectors.toList());
                        }
                    }
                }
                new ArrayList();
                if (!"-1".equals(map.get("groupNum"))) {
                    questionGroupProgress = (List) questionGroupProgress.stream().filter(questionGroupProgressmap -> {
                        return Convert.toStr(questionGroupProgressmap.get("groupNum")).equals(Convert.toStr(((Map) examPaperNumList.get(0)).get("groupNum")));
                    }).collect(Collectors.toList());
                }
                if (questionGroupProgress.size() > 0) {
                    for (int i = 0; i < examPaperNumList.size(); i++) {
                        String exampaperNum = Convert.toStr(examPaperNumList.get(i).get("exampaperNum"));
                        String gradeName = Convert.toStr(examPaperNumList.get(i).get("gradeName"));
                        String subjectName = Convert.toStr(examPaperNumList.get(i).get("subjectName"));
                        String groupNum = Convert.toStr(examPaperNumList.get(i).get("groupNum"));
                        String scancompleted = Convert.toStr(examPaperNumList.get(i).get("scancompleted"));
                        String userzongNum = Convert.toStr(examPaperNumList.get(i).get("userzongNum"));
                        String questionName = "";
                        String totalCount = "";
                        String weipanCount = "";
                        String totalCount_fenzu = "";
                        String weipanCount_fenzu = "";
                        List<Map<String, Object>> schGroupNumWorkloadMapList = new ArrayList<>();
                        String isfenzu = system.fenzu(exampaperNum);
                        for (int f = 0; f < questionGroupProgress.size(); f++) {
                            if (Convert.toStr(questionGroupProgress.get(f).get("groupNum")).equals(groupNum)) {
                                questionName = Convert.toStr(questionGroupProgress.get(f).get("questionName"));
                                totalCount = Convert.toStr(questionGroupProgress.get(f).get("totalCount"));
                                weipanCount = Convert.toStr(questionGroupProgress.get(f).get("weipanCount"));
                                if (isfenzu.equals("1")) {
                                    schGroupNumWorkloadMapList = (List) questionGroupProgress.get(f).get("schGroupNumWorkloadMapList");
                                    if (!map.get("schoolGroupNum").equals("-1")) {
                                        schGroupNumWorkloadMapList = (List) schGroupNumWorkloadMapList.stream().filter(schGroupNumWorkloadMap -> {
                                            return Convert.toStr(schGroupNumWorkloadMap.get("schoolGroupNum")).equals(map.get("schoolGroupNum"));
                                        }).collect(Collectors.toList());
                                    }
                                    totalCount_fenzu = Convert.toStr(schGroupNumWorkloadMapList.get(0).get("totalCount_fenzu"));
                                    weipanCount_fenzu = Convert.toStr(schGroupNumWorkloadMapList.get(0).get("weipanCount_fenzu"));
                                }
                            }
                        }
                        if (map.get("schoolGroupNum").equals("-1") || (!map.get("schoolGroupNum").equals("-1") && schGroupNumWorkloadMapList.size() > 0)) {
                            for (String key3 : teaWorkloadProgress.keySet()) {
                                if (key3.equals(groupNum)) {
                                    List<Map<String, Object>> teaWorkloadProgressMapList = (List) teaWorkloadProgress.get(key3);
                                    if (!isfenzu.equals("0") && !"-1".equals(map.get("schoolGroupNum"))) {
                                        teaWorkloadProgressMapList = (List) teaWorkloadProgressMapList.stream().filter(teaWorkloadProgressMap -> {
                                            return ((String) map.get("schoolGroupNum")).equals(Convert.toStr(teaWorkloadProgressMap.get("schoolGroupNum")));
                                        }).collect(Collectors.toList());
                                    }
                                    if (teaWorkloadProgressMapList.size() > 0) {
                                        for (int k = 0; k < teaWorkloadProgressMapList.size(); k++) {
                                            Map<String, Object> datamap = new HashMap<>();
                                            datamap.put("exampaperNum", exampaperNum);
                                            datamap.put("gradeName", gradeName);
                                            datamap.put("subjectName", subjectName);
                                            datamap.put("groupNum", groupNum);
                                            datamap.put("groupName", questionName);
                                            datamap.put(Const.EXPORTREPORT_schoolNum, teaWorkloadProgressMapList.get(k).get(Const.EXPORTREPORT_schoolNum));
                                            datamap.put("schoolName", teaWorkloadProgressMapList.get(k).get("schoolName"));
                                            datamap.put("teacherNum", teaWorkloadProgressMapList.get(k).get("teacherNum"));
                                            datamap.put("teacherName", teaWorkloadProgressMapList.get(k).get("teacherName"));
                                            datamap.put("isYueJuan", teaWorkloadProgressMapList.get(k).get("zaiyue"));
                                            datamap.put("scancompleted", scancompleted.equals("0") ? "未完成" : "完成");
                                            if ("0".equals(isfenzu) || map.get("schoolGroupNum").equals("-1")) {
                                                datamap.put("regcount", totalCount);
                                                datamap.put("weipanzongshu", weipanCount);
                                            } else {
                                                datamap.put("regcount", totalCount_fenzu);
                                                datamap.put("weipanzongshu", weipanCount_fenzu);
                                            }
                                            if (teaWorkloadProgressMapList.get(k).get("weipanCount").equals("----")) {
                                                gerenyudingshu = "--";
                                                weipanshu = "--";
                                                weipanlv = "--";
                                            } else if ((isfenzu.equals("1") && Convert.toInt(teaWorkloadProgressMapList.get(k).get("weipanCount")).intValue() > 0 && (Convert.toInt(weipanCount).intValue() <= 0 || Convert.toInt(teaWorkloadProgressMapList.get(k).get("weipanCount_sch")).intValue() <= 0)) || Convert.toStr(teaWorkloadProgressMapList.get(k).get("assister")).equals("1")) {
                                                gerenyudingshu = "--";
                                                weipanshu = "--";
                                                weipanlv = "--";
                                            } else {
                                                gerenyudingshu = Convert.toStr(teaWorkloadProgressMapList.get(k).get("yudingCount"));
                                                weipanshu = Convert.toStr(teaWorkloadProgressMapList.get(k).get("weipanCount"));
                                                if (Convert.toStr(teaWorkloadProgressMapList.get(k).get("yudingCount")).equals("0")) {
                                                    weipanlv = "--";
                                                } else if (Convert.toStr(teaWorkloadProgressMapList.get(k).get("weipanCount")).equals("0")) {
                                                    weipanlv = "0.00";
                                                } else {
                                                    Float weipanlv2 = Float.valueOf(Convert.toFloat(Float.valueOf(Convert.toFloat(weipanshu).floatValue() / Convert.toFloat(gerenyudingshu).floatValue())).floatValue() * 100.0f);
                                                    DecimalFormat df = new DecimalFormat("#0.00");
                                                    weipanlv = df.format(weipanlv2);
                                                }
                                            }
                                            if (teaWorkloadProgressMapList.get(k).get("weipanCount_sch").equals("----")) {
                                                xuexiaoweipanshu = Convert.toStr(teaWorkloadProgressMapList.get(k).get("weipanCount_sch"));
                                            } else if (isfenzu.equals("1") && Convert.toInt(teaWorkloadProgressMapList.get(k).get("weipanCount_sch")).intValue() > 0 && Convert.toInt(weipanCount).intValue() <= 0) {
                                                xuexiaoweipanshu = "--";
                                            } else {
                                                xuexiaoweipanshu = Convert.toStr(teaWorkloadProgressMapList.get(k).get("weipanCount_sch"));
                                            }
                                            String yipanshu = Convert.toStr(teaWorkloadProgressMapList.get(k).get("yipanCount"));
                                            datamap.put("userzongcount", userzongNum);
                                            datamap.put("gerenyudingshu", gerenyudingshu);
                                            datamap.put("yipanshu", yipanshu);
                                            datamap.put("weipanshu", weipanshu);
                                            datamap.put("weipanlv", weipanlv);
                                            datamap.put("xuexiaoweipanshu", xuexiaoweipanshu);
                                            String exampaperyipan = "0";
                                            String userNum = Convert.toStr(teaWorkloadProgressMapList.get(k).get("userNum"));
                                            int otherGroupYuejuan = 0;
                                            Map<String, List<Map<String, Object>>> subYuejuanMap = (Map) yuejuanProgressMap2.get(exampaperNum).get("teaWorkloadProgress");
                                            for (String subYuejuanKey : subYuejuanMap.keySet()) {
                                                if (!subYuejuanKey.equals(groupNum)) {
                                                    long count = subYuejuanMap.get(subYuejuanKey).stream().filter(subyuejuan -> {
                                                        return Convert.toStr(subyuejuan.get("userNum")).equals(userNum) && Convert.toInt(subyuejuan.get("yipanCount")).intValue() > 0;
                                                    }).count();
                                                    if (count > 0) {
                                                        otherGroupYuejuan++;
                                                    }
                                                }
                                            }
                                            if (!yipanshu.equals("--") && Convert.toInt(yipanshu).intValue() == 0) {
                                                exampaperyipan = otherGroupYuejuan > 0 ? "1" : "0";
                                            } else if (!yipanshu.equals("--") && Convert.toInt(yipanshu).intValue() > 0) {
                                                exampaperyipan = "1";
                                            }
                                            datamap.put("exampaperyipan", exampaperyipan);
                                            int m = 0;
                                            if ("1".equals(map.get("completeStatus")) && Convert.toInt(weipanCount).intValue() <= 0) {
                                                m = 0 + 1;
                                            } else if ("0".equals(map.get("completeStatus")) && Convert.toInt(weipanCount).intValue() > 0) {
                                                m = 0 + 1;
                                            } else if ("-1".equals(map.get("completeStatus"))) {
                                                m = 0 + 1;
                                            }
                                            if (gerenyudingshu.equals("--") || ("1".equals(map.get("workStatus")) && Convert.toInt(yipanshu).intValue() >= Convert.toInt(gerenyudingshu).intValue())) {
                                                m++;
                                            } else if ("0".equals(map.get("workStatus")) && Convert.toInt(yipanshu).intValue() < Convert.toInt(gerenyudingshu).intValue()) {
                                                m++;
                                            } else if ("-1".equals(map.get("workStatus"))) {
                                                m++;
                                            }
                                            if ("-1".equals(map.get("isYueJuan"))) {
                                                m++;
                                            } else if ("1".equals(map.get("isYueJuan")) && Convert.toStr(teaWorkloadProgressMapList.get(k).get("zaiyue")).equals("是")) {
                                                m++;
                                            } else if ("0".equals(map.get("isYueJuan")) && Convert.toStr(teaWorkloadProgressMapList.get(k).get("zaiyue")).equals("否")) {
                                                m++;
                                            }
                                            if (!"-1".equals(map.get(Const.EXPORTREPORT_schoolNum)) && Convert.toStr(teaWorkloadProgressMapList.get(k).get(Const.EXPORTREPORT_schoolNum)).equals(map.get(Const.EXPORTREPORT_schoolNum))) {
                                                m++;
                                            } else if ("-1".equals(map.get(Const.EXPORTREPORT_schoolNum))) {
                                                m++;
                                            }
                                            if (m == 4) {
                                                teacherWorkProgressDataMapList.add(datamap);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (teacherWorkProgressDataMapList.size() > 0) {
            teacherWorkProgressDataMapList = orderType(teacherWorkProgressDataMapList, map.get("orderType"));
        }
        return teacherWorkProgressDataMapList;
    }

    private List<Map<String, Object>> orderType(List<Map<String, Object>> teacherWorkProgressDataMapList, String orderType) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (orderType.equals("1")) {
            Comparator<Map<String, Object>> c1 = Comparator.comparing(m1 -> {
                return Convert.toInt(m1.get("exampaperNum"), 0);
            });
            Comparator<Map<String, Object>> c2 = Comparator.comparing(m12 -> {
                return Convert.toInt(m12.get("groupNum"), 0);
            });
            Comparator<Map<String, Object>> c3 = ChineseCharacterUtil.sortByPinyinOfMap("schoolName");
            Comparator<Map<String, Object>> c4 = ChineseCharacterUtil.sortByPinyinOfMap("teacherName");
            list = (List) teacherWorkProgressDataMapList.stream().sorted(c1.thenComparing(c2).thenComparing(c3).thenComparing(c4)).collect(Collectors.toList());
        } else if (orderType.equals("2")) {
            Comparator<Map<String, Object>> c12 = Comparator.comparing(m13 -> {
                return Convert.toInt(m13.get("exampaperNum"), 0);
            });
            Comparator<Map<String, Object>> c22 = ChineseCharacterUtil.sortByPinyinOfMap("schoolName");
            Comparator<Map<String, Object>> c32 = Comparator.comparing(m14 -> {
                return Convert.toInt(m14.get("groupNum"), 0);
            });
            Comparator<Map<String, Object>> c42 = ChineseCharacterUtil.sortByPinyinOfMap("teacherName");
            list = (List) teacherWorkProgressDataMapList.stream().sorted(c12.thenComparing(c22).thenComparing(c32).thenComparing(c42)).collect(Collectors.toList());
        } else if (orderType.equals("3")) {
            Comparator<Map<String, Object>> c13 = Comparator.comparing(m15 -> {
                return Convert.toInt(m15.get("exampaperNum"), 0);
            });
            Comparator<Map<String, Object>> c23 = ChineseCharacterUtil.sortByPinyinOfMap("isYueJuan");
            Comparator<Map<String, Object>> c33 = Comparator.comparing(m16 -> {
                return Convert.toInt(m16.get("groupNum"), 0);
            });
            Comparator<Map<String, Object>> c43 = ChineseCharacterUtil.sortByPinyinOfMap("schoolName");
            Comparator<Map<String, Object>> c5 = ChineseCharacterUtil.sortByPinyinOfMap("teacherName");
            list = (List) teacherWorkProgressDataMapList.stream().sorted(c13.thenComparing(c23).thenComparing(c33).thenComparing(c43).thenComparing(c5)).collect(Collectors.toList());
        } else if (orderType.equals("4")) {
            Comparator<Map<String, Object>> c14 = Comparator.comparing(m17 -> {
                return Convert.toInt(m17.get("weipanlv"), 0);
            });
            Comparator<Map<String, Object>> c24 = Comparator.comparing(m18 -> {
                return Convert.toInt(m18.get("exampaperNum"), 0);
            });
            Comparator<Map<String, Object>> c34 = Comparator.comparing(m19 -> {
                return Convert.toInt(m19.get("groupNum"), 0);
            });
            Comparator<Map<String, Object>> c44 = ChineseCharacterUtil.sortByPinyinOfMap("schoolName");
            ChineseCharacterUtil.sortByPinyinOfMap("teacherName");
            list = (List) teacherWorkProgressDataMapList.stream().sorted(c14.reversed().thenComparing(c24).thenComparing(c34).thenComparing(c44)).collect(Collectors.toList());
        }
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Map<String, Object>> getSubjectInfo_wy(String examNum, String gradeNum) {
        String subjectStr = "";
        if (gradeNum != null && !gradeNum.equals("") && !gradeNum.equals("-1")) {
            subjectStr = " and exp.gradeNum= {gradeNum} ";
        }
        String sql = "select exp.subjectNum num,s.subjectName name from questiongroup_user qu LEFT JOIN exampaper exp  on exp.exampaperNum = qu.exampaperNum left join subject s on exp.subjectNum = s.subjectNum where exp.type='0' and exp.examNum = {examNum} " + subjectStr + " AND exp.isHidden='F'  GROUP BY exp.subjectNum";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Map<String, Object>> getGradeInfo_wy(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        List<Map<String, Object>> list = this.dao2._queryMapList("select exp.gradeNum num ,g.gradeName name from questiongroup_user qu LEFT JOIN exampaper exp  on exp.exampaperNum = qu.exampaperNum  left join basegrade g on exp.gradeNum = g.gradeNum where exp.type='0' and exp.examNum = {examNum} GROUP BY exp.gradeNum", null, args);
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Map<String, Object>> getGroupNumInfo(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        String examPaperNum = this.dao2._queryStr("select examPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("examPaperNum", examPaperNum);
        List<Map<String, Object>> list = this.dao2._queryMapList("SELECT s.id num,questionNum name from( \tSELECT id,questionNum,orderNum a,0 b,0 c from define where examPaperNum={examPaperNum} and choosename='s' and (isParent=0 or `merge`=1) and questiontype=1 \t\tUNION \t\tSELECT sb.id,sb.questionNum,d.orderNum a,sb.orderNum b,0 c from subdefine sb left join define d on sb.pid=d.id \t\twhere sb.examPaperNum={examPaperNum} and d.choosename='s' and d.`merge`=0 and sb.questiontype=1 \t\tUNION \t\tSELECT choosename id,GROUP_CONCAT(questionNum separator '-') questionNum,orderNum a,0 b,0 c from define where examPaperNum={examPaperNum}  and LENGTH(choosename)>1 \t\tand (isParent=0 or `merge`=1) and questiontype=1 GROUP BY choosename \t\tunion \t\tSELECT d.num id,CONCAT(GROUP_CONCAT(questionNum separator '-'),'_',d.orderNum) questionNum,d.a,d.b,d.c from( \t\t\tSELECT sb.id,sb.orderNum,sb.questionNum yy,d.questionNum,CONCAT(d1.id,sb.orderNum) num,if(d1.orderNum is null,d.orderNum,d1.orderNum) a,sb.orderNum b, d.orderNum c \t\t\tfrom subdefine sb left join define d  on sb.pid=d.id LEFT JOIN define d1 on d.choosename=d1.id \t\t\twhere sb.examPaperNum={examPaperNum} and LENGTH(d.choosename)>1 and d.`merge`=0 and sb.questiontype=1 \t\t)d GROUP BY d.num \t) s ORDER BY s.a,s.b,s.c", null, args);
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Map<String, Object>> getSchoolInfo(String examNum, String gradeNum, String subjectNum, String leicengId, String loginUserNum, String schoolGroupNum) {
        String subjectStr = "";
        if (subjectNum != null && !subjectNum.equals("") && !subjectNum.equals("-1")) {
            subjectStr = " and ex.subjectNum= {subjectNum} ";
        }
        String gradeNumStr = "";
        if (gradeNum != null && !gradeNum.equals("") && !gradeNum.equals("-1")) {
            gradeNumStr = " and ex.gradeNum= {gradeNum} ";
        }
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leicengId != null && !leicengId.equals("")) {
            leicengStr1 = "LEFT JOIN (select DISTINCT sItemId schoolNum from statisticitem_school where topItemId={leicengId} and statisticItem='01') tt on u.schoolNum = tt.schoolNum ";
            leicengStr2 = " and tt.schoolNum is not null ";
        }
        String schoolGroupNumStr1 = "";
        String schoolGroupNumStr2 = "";
        if (!"-1".equals(schoolGroupNum) && schoolGroupNum != null && !schoolGroupNum.equals("")) {
            schoolGroupNumStr1 = " LEFT JOIN schoolgroup slg on u.schoolnum=slg.schoolNum ";
            schoolGroupNumStr2 = " and slg.schoolGroupNum={schoolGroupNum} ";
        }
        String sql = "select DISTINCT u.schoolnum num,s.schoolName name from questiongroup_user qu  LEFT JOIN exampaper ex on qu.exampaperNum = ex.examPaperNum LEFT JOIN user u on qu.userNum = u.id LEFT JOIN school s on u.schoolnum = s.id AND s.isDelete='F' " + leicengStr1 + schoolGroupNumStr1 + "where u.schoolNum <> '0' and ex.examNum = {examNum} " + gradeNumStr + subjectStr + schoolGroupNumStr2 + leicengStr2 + " ORDER BY CONVERT(s.schoolName USING gbk) asc ";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("leicengId", leicengId);
        args.put("schoolGroupNum", schoolGroupNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        List<Map<String, Object>> list = this.dao2._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Map<String, Object>> getSchoolInfo2(String examNum, String gradeNum, String subjectNum, String leicengId, String loginUserNum, String schoolGroupNum) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("value", "-1");
        map.put("text", "全部");
        list.add(map);
        String subjectStr = "";
        if (subjectNum != null && !subjectNum.equals("") && !subjectNum.equals("-1")) {
            subjectStr = " and ex.subjectNum= {subjectNum} ";
        }
        String gradeNumStr = "";
        if (gradeNum != null && !gradeNum.equals("") && !gradeNum.equals("-1")) {
            gradeNumStr = " and ex.gradeNum= {gradeNum} ";
        }
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leicengId != null && !leicengId.equals("")) {
            leicengStr1 = "LEFT JOIN (select DISTINCT sItemId schoolNum from statisticitem_school where topItemId={leicengId} and statisticItem='01') tt on u.schoolNum = tt.schoolNum ";
            leicengStr2 = " and tt.schoolNum is not null ";
        }
        String schoolGroupNumStr1 = "";
        String schoolGroupNumStr2 = "";
        if (!"-1".equals(schoolGroupNum) && schoolGroupNum != null && !schoolGroupNum.equals("")) {
            schoolGroupNumStr1 = " LEFT JOIN schoolgroup slg on u.schoolnum=slg.schoolNum ";
            schoolGroupNumStr2 = " and slg.schoolGroupNum={schoolGroupNum} ";
        }
        String sql = "select DISTINCT u.schoolnum value,s.schoolName text from questiongroup_user qu  LEFT JOIN exampaper ex on qu.exampaperNum = ex.examPaperNum LEFT JOIN user u on qu.userNum = u.id LEFT JOIN school s on u.schoolnum = s.id AND s.isDelete='F' " + leicengStr1 + schoolGroupNumStr1 + "where u.schoolNum <> '0' and ex.examNum = {examNum} " + gradeNumStr + subjectStr + schoolGroupNumStr2 + leicengStr2 + " ORDER BY CONVERT(s.schoolName USING gbk) asc ";
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("leicengId", leicengId);
        args.put("schoolGroupNum", schoolGroupNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        List<Map<String, Object>> list2 = this.dao2._queryMapList(sql, null, args);
        list.addAll(list2);
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List remarksize(String scoreId) {
        return this.dao.remarksize(scoreId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer deleteremark(int remark_id) {
        return this.dao.deleteremark(remark_id);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer reMark2(String scoreId, String questionScore, String insertUser, String questionNum, String exampaperNum) {
        return this.dao.reMark2(scoreId, questionScore, insertUser, questionNum, exampaperNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Object[]> getRunningGroupListInfo() {
        return this.dao.getRunningGroupListInfo();
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public QuestionGroupInfo getYingPanGroupInfo(String exampaperNum, String groupNum) {
        return this.dao.getYingPanGroupInfo(exampaperNum, groupNum);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void stealQuestion(String exampaperNum, String groupNum, String insertUser, String groupType, String isShuangPing) {
        logF.info(String.format("试卷：%s --- 题组：%s --- 用户：%s --- 类型：%s  开始偷题", exampaperNum, groupNum, insertUser, groupType));
        long begin = System.currentTimeMillis();
        ServletActionContext.getServletContext();
        int fenfaTimeout = Integer.valueOf(Configuration.getInstance().getFenfaTimeout()).intValue();
        StealQuestionHelper helper = new StealQuestionHelper(exampaperNum, groupNum, insertUser, groupType, QuartzManager.MAX_STEAL_QUENUM, fenfaTimeout, isShuangPing);
        helper.dispatcher();
        logF.info(String.format("试卷：%s --- 题组：%s --- 用户：%s --- 类型：%s  本次偷题用时：%s", exampaperNum, groupNum, insertUser, groupType, Long.valueOf(System.currentTimeMillis() - begin)));
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void dispatcherTask(Map<String, Object> eachInsertUserQuestionGroupMap) {
        this.dao.dispatcherTask(eachInsertUserQuestionGroupMap);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Teacherappeal> getAppealList(String examNum, String gradeNum, String subjectNum, String groupNum, String leicengId, String schoolNum, String orderType, String userId) {
        String userSql;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("groupNum", groupNum);
        args.put("leicengId", leicengId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("userId", userId);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        String orderStr = "";
        if ("1".equals(orderType)) {
            orderStr = "ORDER BY bg.gradeNum,sub.subjectNum,IFNULL(d2.orderNum,d.orderNum),subd.orderNum,wpl desc";
        } else if ("2".equals(orderType)) {
            orderStr = "ORDER BY wpl desc";
        }
        String graStr = "-1".equals(gradeNum) ? "" : " and ep.gradeNum = {gradeNum} ";
        String subStr = "-1".equals(subjectNum) ? "" : " and ep.subjectNum = {subjectNum} ";
        String queStr = "-1".equals(groupNum) ? "" : " and teaa.questionNum = {groupNum} ";
        String schStr = "";
        String leicengStr = "";
        if ("-1".equals(schoolNum)) {
            if (null != leicengId && !"".equals(leicengId)) {
                leicengStr = "LEFT JOIN (select DISTINCT sItemId schoolNum from statisticitem_school where topItemId={leicengId} and statisticItem='01') tt on tt.schoolNum = u.schoolnum ";
                schStr = " and tt.schoolNum is not null ";
            }
        } else {
            schStr = " and u.schoolnum = {schoolNum} ";
        }
        if (!"-1".equals(userId) && !"-2".equals(userId) && null == map) {
            userSql = "inner JOIN (select schoolNum from schoolscanpermission where userNum={userId} union select schoolNum from user where id={userId}) sch ON cast(sch.schoolNum as char) = cast(u.schoolnum as char)  left join school s on cast(sch.schoolNum as char)=cast(s.id as char) ";
        } else {
            userSql = "LEFT JOIN school sch ON cast(sch.id as char) = cast(u.schoolnum as char) ";
        }
        String sql = "SELECT bg.gradeName,sub.subjectName,IFNULL(subd.questionNum,d.questionNum) questionNum,sch.schoolName,u.username,u.realname,COUNT(t.id) spl,ta.wcl wcl,ROUND(IFNULL(ta.wcl/COUNT(t.id),0)*100,1) wpl,IFNULL(subd.id,d.id) qId,ta.marker,IFNULL(subd.fullScore,d.fullScore) fullScore from (SELECT ep.gradeNum, ep.subjectNum, teaa.exampaperNum, teaa.marker, teaa.questionNum, count(1) wcl FROM teacherappeal teaa LEFT JOIN exampaper ep ON ep.exampaperNum = teaa.exampaperNum WHERE ep.examNum = {examNum} " + graStr + subStr + queStr + " AND teaa. STATUS = '1' GROUP BY teaa.marker,teaa.questionNum ) ta LEFT JOIN subdefine subd ON subd.id = ta.questionNum LEFT JOIN define d ON d.id = ta.questionNum LEFT JOIN define d2 ON d2.id = subd.pid LEFT JOIN basegrade bg ON bg.gradeNum = ta.gradeNum LEFT JOIN `subject` sub ON sub.subjectNum = ta.subjectNum LEFT JOIN task t ON t.insertUser = ta.marker AND t.questionNum = ta.questionNum LEFT JOIN USER u ON u.id = ta.marker " + userSql + leicengStr + "where t.status = 'T' " + schStr + " GROUP BY ta.questionNum,ta.marker " + orderStr;
        this.log.info("--申诉处理统计数据sql--" + sql);
        return this.dao2._queryBeanList(sql, Teacherappeal.class, args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Map<String, String>> getShenSuDataList(String examNum, String gradeNum, String subjectNum, String groupNum, String leicengId, String schoolNum, String shensuOrderType, String userId) {
        String orderStr = "";
        if ("1".equals(shensuOrderType)) {
            orderStr = " order by gradeNum,subjectNum,teaQuestion,convert(sch.schoolName using gbk),appealer,bohuilv desc";
        } else if ("2".equals(shensuOrderType)) {
            orderStr = " ORDER BY bohuilv desc";
        }
        String graStr = "-1".equals(gradeNum) ? "" : " and e.gradeNum = {gradeNum} ";
        String subStr = "-1".equals(subjectNum) ? "" : " and e.subjectNum = {subjectNum} ";
        String queStr = "-1".equals(groupNum) ? "" : " and ta.questionNum = {groupNum} ";
        String schStr = " 1=1 ";
        String leicengStr = "";
        if ("-1".equals(schoolNum)) {
            if (null != leicengId && !"".equals(leicengId)) {
                leicengStr = "LEFT JOIN (select DISTINCT sItemId schoolNum from statisticitem_school where topItemId={leicengId} and statisticItem='01') tt on tt.schoolNum = u.schoolnum ";
                schStr = "  tt.schoolNum is not null ";
            }
        } else {
            schStr = " u.schoolnum = {schoolNum} ";
        }
        String sql = "select teaa.appealer,bg.gradeNum,bg.gradeName,sub.subjectNum,sub.subjectName,IFNULL( subd.questionNum, d.questionNum ) questionNum,teaa.questionNum teaQuestion,sch.schoolNum, sch.schoolName,u.username,u.realname,IFNULL(subd.fullScore,d.fullScore) fullScore,count(teaa.appealer) shensuCount, sum(if(teaa.status=2,1,0)) bohuiCount,ROUND( IFNULL( sum(if(teaa.status=2,1,0)) / count(teaa.appealer), 0 )* 100, 1) bohuilv   from (       SELECT   ta.appealer,   ta.questionNum,   ta.exampapernum, ta.status,   studentId   FROM   teacherappeal ta   LEFT JOIN exampaper e ON e.exampaperNum = ta.exampaperNum    WHERE   e.examNum = {examNum} " + graStr + subStr + queStr + " )teaa     LEFT JOIN exampaper ep ON ep.exampaperNum = teaa.exampaperNum      left join user u on teaa.appealer=u.id     LEFT JOIN subdefine subd ON subd.id = teaa.questionNum     LEFT JOIN define d ON d.id = teaa.questionNum     LEFT JOIN define d2 ON d2.id = subd.pid     LEFT JOIN basegrade bg ON bg.gradeNum = ep.gradeNum     LEFT JOIN `subject` sub ON sub.subjectNum = ep.subjectNum     LEFT JOIN school sch ON sch.id = u.schoolnum   " + leicengStr + "   where " + schStr + "    group by gradeNum,subjectNum,teaQuestion,teaa.appealer  " + orderStr;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("groupNum", groupNum);
        args.put("leicengId", leicengId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String exportAppealList(String examNum, String gradeNum, String subjectNum, String groupNum, String leicengId, String schoolNum, String orderType, String shensuOrderType, String userId, String xmlPath) {
        String path = "ExportFolder/teacherappeal/" + userId;
        String filePath = xmlPath + "/" + path;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File excelFile = new File(filePath + "/申诉处理统计表.xls");
            if (!excelFile.exists()) {
                excelFile.createNewFile();
            }
            WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormatTitle = new WritableCellFormat(titleFont);
            cFormatTitle.setAlignment(Alignment.CENTRE);
            cFormatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableFont title1 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
            WritableCellFormat cFormat = new WritableCellFormat(title1);
            cFormat.setAlignment(Alignment.LEFT);
            cFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            WritableFont font1 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormat1 = new WritableCellFormat(font1);
            cFormat1.setAlignment(Alignment.CENTRE);
            cFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);
            cFormat1.setWrap(true);
            WorkbookSettings wbs = new WorkbookSettings();
            wbs.setGCDisabled(true);
            WritableWorkbook wwBook = jxl.Workbook.createWorkbook(excelFile, wbs);
            WritableSheet sheet = wwBook.createSheet("阅卷处理统计", 0);
            WritableSheet sheet2 = wwBook.createSheet("教师申诉驳回统计", 1);
            List<Teacherappeal> list = getAppealList(examNum, gradeNum, subjectNum, groupNum, leicengId, schoolNum, orderType, userId);
            List<Map<String, String>> shenSuDataList = getShenSuDataList(examNum, gradeNum, subjectNum, groupNum, leicengId, schoolNum, shensuOrderType, userId);
            sheet.mergeCells(0, 0, 9, 0);
            sheet.setRowView(0, Const.height_400);
            sheet.setColumnView(4, 20);
            sheet.setColumnView(5, 20);
            sheet.setColumnView(6, 15);
            sheet.setColumnView(7, 15);
            sheet.setColumnView(8, 15);
            sheet2.mergeCells(0, 0, 9, 0);
            sheet2.setRowView(0, Const.height_400);
            sheet2.setColumnView(4, 20);
            sheet2.setColumnView(5, 20);
            sheet2.setColumnView(6, 15);
            sheet2.setColumnView(7, 15);
            sheet2.setColumnView(8, 15);
            Label cell0 = new Label(0, 0, "备注：误判率（%）= 误判量/实判量*100。", cFormat);
            sheet.addCell(cell0);
            Label shensucell0 = new Label(0, 0, "备注：驳回率（%）= 驳回量/申诉量*100。", cFormat);
            sheet2.addCell(shensucell0);
            String[] title = {"年级", "科目", "题号", "阅卷学校", "阅卷教师ID", "阅卷教师名", "实判量", "误判量", "误判率（%）"};
            for (int i = 0; i < title.length; i++) {
                Label cell = new Label(i, 1, title[i], cFormatTitle);
                sheet.addCell(cell);
            }
            String[] title2 = {"年级", "科目", "题号", "申诉学校", "申诉教师ID", "申诉教师名", "申诉量", "驳回量", "驳回率（%）"};
            for (int i2 = 0; i2 < title2.length; i2++) {
                Label cell2 = new Label(i2, 1, title2[i2], cFormatTitle);
                sheet2.addCell(cell2);
            }
            for (int j = 0; j < list.size(); j++) {
                Teacherappeal ta = list.get(j);
                Label cell1 = new Label(0, j + 2, ta.getGradeName(), cFormat1);
                sheet.addCell(cell1);
                Label cell22 = new Label(1, j + 2, ta.getSubjectName(), cFormat1);
                sheet.addCell(cell22);
                Label cell3 = new Label(2, j + 2, "T" + ta.getQuestionNum(), cFormat1);
                sheet.addCell(cell3);
                Label cell4 = new Label(3, j + 2, ta.getSchoolName(), cFormat1);
                sheet.addCell(cell4);
                Label cell5 = new Label(4, j + 2, ta.getUsername(), cFormat1);
                sheet.addCell(cell5);
                Label cell6 = new Label(5, j + 2, ta.getRealname(), cFormat1);
                sheet.addCell(cell6);
                Number cell7 = new Number(6, j + 2, ta.getSpl(), cFormat1);
                sheet.addCell(cell7);
                Number cell8 = new Number(7, j + 2, ta.getWcl(), cFormat1);
                sheet.addCell(cell8);
                Number cell9 = new Number(8, j + 2, ta.getWpl(), cFormat1);
                sheet.addCell(cell9);
            }
            int j2 = 0;
            for (Map map : shenSuDataList) {
                String gradeName = map.get("gradeName").toString();
                String subjectName = map.get("subjectName").toString();
                String questionNum = map.get("questionNum").toString();
                String schoolName = map.get("schoolName").toString();
                String username = map.get("username").toString();
                String realname = map.get("realname").toString();
                String shensuCount = map.get("shensuCount").toString();
                String bohuiCount = map.get("bohuiCount").toString();
                String bohuilv = map.get("bohuilv").toString();
                Label cell12 = new Label(0, j2 + 2, gradeName, cFormat1);
                sheet2.addCell(cell12);
                Label cell23 = new Label(1, j2 + 2, subjectName, cFormat1);
                sheet2.addCell(cell23);
                Label cell32 = new Label(2, j2 + 2, "T" + questionNum, cFormat1);
                sheet2.addCell(cell32);
                Label cell42 = new Label(3, j2 + 2, schoolName, cFormat1);
                sheet2.addCell(cell42);
                Label cell52 = new Label(4, j2 + 2, username, cFormat1);
                sheet2.addCell(cell52);
                Label cell62 = new Label(5, j2 + 2, realname, cFormat1);
                sheet2.addCell(cell62);
                Number cell72 = new Number(6, j2 + 2, Double.valueOf(shensuCount).doubleValue(), cFormat1);
                sheet2.addCell(cell72);
                Number cell82 = new Number(7, j2 + 2, Double.valueOf(bohuiCount).doubleValue(), cFormat1);
                sheet2.addCell(cell82);
                Number cell92 = new Number(8, j2 + 2, Double.valueOf(bohuilv).doubleValue(), cFormat1);
                sheet2.addCell(cell92);
                j2++;
            }
            wwBook.write();
            wwBook.close();
        } catch (Exception e) {
            this.log.info("--导出申诉处理统计" + userId + "失败--" + e.getMessage());
        }
        return path + "/申诉处理统计表.xls";
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Teacherappeal> getAppealDetailData(String qId, String marker, String fullScore, String item) {
        String orderSql = "1".equals(item) ? "order by wpl ASC" : "order by wpl DESC";
        String sql = "select scoreId,scorebeformodify,scoremodified,Round((scorebeformodify-scoremodified)/" + fullScore + "*100,1) wpl,reason,studentId,examPaperNum,questionNum from teacherappeal where questionNum = {qId} and marker = {marker} and status = '1' " + orderSql;
        Map args = new HashMap();
        args.put("qId", qId);
        args.put("marker", marker);
        this.log.info("--申诉处理详细内容sql--" + sql);
        return this.dao2._queryBeanList(sql, Teacherappeal.class, args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Map<String, String>> getShenSuDetailData(String questionNum, String appeal) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        args.put("appeal", appeal);
        return this.dao2._queryMapList("select scoreId,scorebeformodify,suggestScore,reason,studentId,exampaperNum,questionNum from teacherappeal where questionNum={questionNum} and appealer={appeal}  and status=2", TypeEnum.StringObject, args);
    }

    public byte[] httpImg(String examPaperNum, String studentId, String questionNum, String scoreId, String rootPath) {
        String _url = rootPath + "imageAction!fetchQuestionImage_subjective.action";
        String params = "examPaperNum=" + examPaperNum + "&studentId=" + studentId + "&questionNum=" + questionNum + "&scoreId=" + scoreId;
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            try {
                URL url = new URL(_url);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(60000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                connection.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
                OutputStream os = connection.getOutputStream();
                os.write(params.getBytes("UTF-8"));
                if (connection.getResponseCode() != 200) {
                    if (0 != 0) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    connection.disconnect();
                    return null;
                }
                is = connection.getInputStream();
                byte[] byteArray = FileUtil.toByteArray(is);
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                connection.disconnect();
                return byteArray;
            } catch (Throwable th) {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                connection.disconnect();
                throw th;
            }
        } catch (Exception e4) {
            this.log.error("--导出申诉处理详情图片发起http请求出现异常--:", e4);
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            connection.disconnect();
            return null;
        }
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getSchoolNumByUserId(String user) {
        Map args = new HashMap();
        args.put("user", user);
        String schoolNum = this.dao2._queryStr("select schoolNum from user where id={user} ", args);
        return schoolNum;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getSchoolGroup() {
        Object enforce = this.dao2.queryObject("SELECT MAX(enforce) from schoolgroup");
        String enforce1 = (enforce == null || "".equals(enforce)) ? "0" : String.valueOf(enforce);
        return enforce1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Schoolgroup> getSchoolGroupList(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        String exampaperNum = this.dao2._queryStr("select examPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        String enforce = systemService.fenzu(String.valueOf(exampaperNum));
        List list = new ArrayList();
        if (!"0".equals(enforce)) {
            list = this.dao2._queryBeanList("SELECT DISTINCT schoolGroupNum,schoolGroupName from schoolgroup order by schoolGroupName", Schoolgroup.class, args);
        }
        return list;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getCurrGradeNumStatus(String gradeNum, String userId) {
        String str = "1";
        Map args = new HashMap();
        args.put("userId", userId);
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT gradeNum from userposition where userNum={userId} and type!=0", Grade.class, args);
        int i = 0;
        while (true) {
            if (i >= _queryBeanList.size()) {
                break;
            }
            Grade g = (Grade) _queryBeanList.get(i);
            if (!gradeNum.equals(String.valueOf(g.getGradeNum()))) {
                i++;
            } else {
                str = "0";
                break;
            }
        }
        return str;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getRestrict(String examNum, String gradeNum, String subjectNum, String userNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("userNum", userNum);
        String jie = this.dao2._queryStr("SELECT  jie  FROM exampaper s WHERE s.examNum={examNum}  AND s.gradeNum={gradeNum}  LIMIT 0,1", args);
        args.put("jie", jie);
        Object obj = this.dao2._queryObject("SELECT ag.`status` from  ( \t\tselect classNum ,subjectNum from userposition  where gradeNum={gradeNum}  and jie= {jie} and userNum={userNum}\t)usp INNER JOIN ( \t\t\tselect e.subjectnum from exampaper pe left join exampaper e on pe.exampapernum = e.pexampapernum   \t where pe.examNum={examNum} and pe.gradeNum={gradeNum} and pe.subjectnum={subjectNum}\t) subj on usp.subjectNum=subj.subjectnum \tINNER JOIN  averagescore ag on  usp.subjectNum=ag.subjectNum and ag.classNum='-1' \twhere ag.examNum={examNum} and ag.gradeNum={gradeNum} ", args);
        if (obj == null) {
            return "0";
        }
        String status = String.valueOf(obj);
        return status;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getStudentType(String examNum, String gradeNum, String subjectNum, String userNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("userNum", userNum);
        String jie = this.dao2._queryStr("SELECT  jie  FROM exampaper s WHERE s.examNum={examNum}  AND s.gradeNum={gradeNum}  LIMIT 0,1", args);
        args.put("jie", jie);
        String str = "0";
        String subjectType = this.dao2._queryStr("select subjectType,subjectNum from subject where subjectNum={subjectNum} ", args);
        if ("1".equals(subjectType) || "2".equals(subjectType)) {
            Object stuType1 = this.dao2._queryObject("SELECT GROUP_CONCAT(u.studentType) from( \tselect c.studentType,u.subjectNum from userposition u \tINNER JOIN class c on u.gradeNum=c.gradeNum and u.classNum=c.id \twhere u.gradeNum={gradeNum}  and u.jie= {jie} and u.userNum={userNum} )u INNER JOIN( \tselect sj.subjectType,e.subjectNum from exampaper pe   left join exampaper e on pe.examPaperNum=e.pexamPaperNum  left join subject sj on e.subjectNum=sj.subjectNum   where pe.examNum={examNum} and pe.gradeNum={gradeNum} and pe.subjectNum={subjectNum} )s on u.subjectNum=s.subjectNum ", args);
            String stuType = String.valueOf(stuType1 == null ? "" : stuType1);
            if (stuType.indexOf("0") != -1) {
                str = "0";
            } else {
                String count = this.dao2._queryStr("SELECT IFNULL(count(1) ,0)from( \tselect c.studentType,u.subjectNum from userposition u \tINNER JOIN class c on u.gradeNum=c.gradeNum and u.classNum=c.id \twhere u.gradeNum={gradeNum}  and u.jie= {jie} and u.userNum={userNum})u INNER JOIN( \tselect sj.subjectType,e.subjectNum from exampaper pe   left join exampaper e on pe.examPaperNum=e.pexamPaperNum  left join subject sj on e.subjectNum=sj.subjectNum   where pe.examNum={examNum} and pe.gradeNum={gradeNum} and pe.subjectNum={subjectNum})s on u.subjectNum=s.subjectNum and u.studentType=s.subjectType", args);
                if ("0".equals(count)) {
                    str = "1";
                }
            }
        }
        return str;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getEsAverageScore(String examNum, String gradeNum, String subjectNum, String isPosition, String userId) {
        String type;
        String sql;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("userId", userId);
        String jie = this.dao2._queryStr("SELECT  jie  FROM exampaper s WHERE s.examNum={examNum}  AND s.gradeNum={gradeNum}  LIMIT 0,1", args);
        args.put("jie", jie);
        String str = "0";
        if ("0".equals(isPosition)) {
            str = "1";
        } else if ("1".equals(isPosition)) {
            Object stuType1 = this.dao2._queryObject("SELECT GROUP_CONCAT(u.studentType) from( \tselect c.studentType,u.subjectNum from userposition u \tINNER JOIN class c on u.gradeNum=c.gradeNum and u.classNum=c.id \twhere u.gradeNum={gradeNum}  and u.jie= {jie} and u.userNum={userId} )u INNER JOIN( \tselect sj.subjectType,e.subjectNum from exampaper pe   left join exampaper e on pe.examPaperNum=e.pexamPaperNum  left join subject sj on e.subjectNum=sj.subjectNum   where pe.examNum={examNum} and pe.gradeNum={gradeNum} and pe.subjectNum={subjectNum} )s on u.subjectNum=s.subjectNum ", args);
            String stuType = String.valueOf(stuType1 == null ? "" : stuType1);
            if (stuType.indexOf("0") != -1) {
                type = "0";
            } else {
                type = "1";
            }
            String sql2 = "select subjectType,subjectNum from subject where subjectNum=" + subjectNum;
            String subjectType = this.dao2._queryStr(sql2, args);
            if (("1".equals(subjectType) || "2".equals(subjectType)) && "1".equals(type)) {
                sql = "SELECT IFNULL(COUNT(1),0) from  ( \tselect u.classNum ,u.subjectNum from userposition u  INNER JOIN class c on u.gradeNum=c.gradeNum and u.classNum=c.id \tINNER JOIN subject sj on u.subjectNum=sj.subjectNum and c.studentType=sj.subjectType inner join examinationnum en on en.examNum={examNum} and en.gradeNum={gradeNum} and en.subjectNum={subjectNum} and en.classNum=u.classNum where u.gradeNum={gradeNum}  and u.jie= {jie} and u.userNum={userId}\t)usp INNER JOIN ( \t\tselect e.subjectnum from exampaper pe left join exampaper e on pe.exampapernum = e.pexampapernum   \t\twhere pe.examNum={examNum} and pe.gradeNum={gradeNum}\t) subj on usp.subjectNum=subj.subjectnum \tleft JOIN  averagescore ag on usp.classNum= ag.classNum and usp.subjectNum=ag.subjectNum and ag.examNum={examNum} and ag.gradeNum={gradeNum}\twhere ag.classNum is null";
            } else {
                sql = "SELECT IFNULL(COUNT(1),0) from  ( \tselect u.classNum ,u.subjectNum from userposition u inner join examinationnum en on en.examNum={examNum} and en.gradeNum={gradeNum} and en.subjectNum={subjectNum} and en.classNum=u.classNum where u.gradeNum={gradeNum}  and u.jie= {jie} and u.userNum={userId}\t)usp INNER JOIN ( \t\tselect e.subjectnum from exampaper pe left join exampaper e on pe.exampapernum = e.pexampapernum   \t\twhere pe.examNum={examNum} and pe.gradeNum={gradeNum}\t) subj on usp.subjectNum=subj.subjectnum \tleft JOIN  averagescore ag on usp.classNum= ag.classNum and usp.subjectNum=ag.subjectNum and ag.examNum={examNum} and ag.gradeNum={gradeNum}\twhere ag.classNum is null";
            }
            String count = this.dao2._queryStr(sql, args);
            if ("0".equals(count)) {
                str = "1";
            } else {
                str = "0";
            }
        }
        return str;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String getUserPosition(String examNum, String gradeNum, String subjectNum, String userId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("userId", (Object) userId).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        String jie = this.dao2._queryStr("SELECT  jie  FROM exampaper s WHERE s.examNum={examNum}  AND s.gradeNum={gradeNum}  LIMIT 0,1", args);
        String sql = "SELECT IFNULL(COUNT(1),0) from  ( \t\tselect subjectNum from userposition  where gradeNum={gradeNum}  and jie= " + jie + " and userNum=" + userId + "\t)usp INNER JOIN ( \t\t\tselect e.subjectnum from exampaper pe left join exampaper e on pe.exampapernum = e.pexampapernum   \t where pe.examNum={examNum} and pe.gradeNum={gradeNum} and pe.subjectnum={subjectNum}  \t) subj on usp.subjectNum=subj.subjectnum";
        String count = this.dao2._queryStr(sql, args);
        if (!"0".equals(count)) {
            count = "1";
        }
        return count;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<Teacher> getMoreTeacherList(String name, String val, String userId) {
        String sql = "";
        Map args = new HashMap();
        args.put(License.VALUE, val);
        if ("subject".equals(name)) {
            return this.dao2._queryBeanList("select sj.subjectName ext1,u.username ext2,u.realname ext3,u.mobile,s.schoolName from questiongroup_user q INNER JOIN user u on q.userNum=u.id INNER JOIN school s on u.schoolnum=s.id INNER JOIN exampaper e on q.exampaperNum=e.examPaperNum INNER JOIN subject sj on e.subjectNum=sj.subjectNum where e.pexamPaperNum={val} and q.userType='2'", Teacher.class, args);
        }
        if ("group".equals(name)) {
            sql = "select sj.subjectName ext1,u.username ext2,u.realname ext3,u.mobile,s.schoolName from questiongroup_user q INNER JOIN user u on q.userNum=u.id INNER JOIN school s on u.schoolnum=s.id INNER JOIN exampaper e on q.exampaperNum=e.examPaperNum INNER JOIN subject sj on e.subjectNum=sj.subjectNum where q.groupNum={val} and q.userType='1'";
        }
        return this.dao2._queryBeanList(sql, Teacher.class, args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Map getScoreRuleList(Map<String, String> map, String userId) {
        return this.dao.getScoreRuleList(map, userId);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<ScoringPointRect> getScoringPointList(List<AwardPoint> taskList) {
        if (CsUtils.IsNullOrEmpty(taskList)) {
            return null;
        }
        if (taskList.size() == 1) {
            String scoreId = taskList.get(0).getScoreId();
            List<Object> crossScoreIdList = this.examDao.getCrossPageScoreIdList(scoreId);
            new StringBuffer();
            List<Object> allScoreIdList = new ArrayList<>();
            allScoreIdList.add(scoreId);
            if (!CsUtils.IsNullOrEmpty(crossScoreIdList)) {
                allScoreIdList.addAll(crossScoreIdList);
            }
            Map<String, Object> scoringPointRectJsonMap = getScoringPointRectJsonByScoreIds(allScoreIdList);
            List<Object> scoringPointJsonValues = (List) scoringPointRectJsonMap.values().stream().collect(Collectors.toList());
            List<ScoringPointRect> rtn = toScoringPointRectList(scoringPointJsonValues);
            return rtn;
        }
        List<Object> scoreIdByTasks = (List) taskList.stream().map(a -> {
            return a.getScoreId();
        }).collect(Collectors.toList());
        Map<String, Object> mergeJsonMap = getScoringPointRectJsonByScoreIds(scoreIdByTasks);
        List<Object> scoringPointRectJsonList = new ArrayList<>();
        new ArrayList();
        List<Object> mergeJsonValues = (List) mergeJsonMap.values().stream().collect(Collectors.toList());
        if (mergeJsonValues.stream().distinct().count() == 1) {
            Map<String, Object> crossScoringPointRectJsonMap = getScoringPointRectJsonByScoreIds(this.examDao.getCrossPageScoreIdList(scoreIdByTasks.get(0).toString()));
            scoringPointRectJsonList.add(mergeJsonValues.get(0));
            if (crossScoringPointRectJsonMap != null && crossScoringPointRectJsonMap.size() > 0) {
                Object first = crossScoringPointRectJsonMap.values().toArray()[0];
                scoringPointRectJsonList.add(first);
            }
            List<ScoringPointRect> rtn2 = toScoringPointRectList(scoringPointRectJsonList);
            return rtn2;
        }
        Map<String, Map<String, Object>> crossPageScoreId2Map = this.examDao.getCrossPageScoreId2Map(scoreIdByTasks);
        if (crossPageScoreId2Map == null || crossPageScoreId2Map.size() == 0) {
            scoringPointRectJsonList.addAll(mergeJsonValues);
        } else {
            mergeJsonMap.forEach((scoreId2, json) -> {
                scoringPointRectJsonList.add(json);
                if (crossPageScoreId2Map.containsKey(scoreId2)) {
                    Object crossScoreId = ((Map) crossPageScoreId2Map.get(scoreId2)).get("id");
                    Map<String, Object> crossScoringPointRectJsonMap2 = getScoringPointRectJsonByScoreIds(Lists.newArrayList(new Object[]{crossScoreId}));
                    Object crossJson = crossScoringPointRectJsonMap2.get(crossScoreId.toString());
                    scoringPointRectJsonList.add(crossJson);
                }
            });
        }
        List<ScoringPointRect> rtn3 = toScoringPointRectList(scoringPointRectJsonList);
        return rtn3;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public List<RemarkImg> getRemarkImgJson(String scoreIdStr, String userNum) {
        Map args = StreamMap.create().put("scoreIdStr", (Object) scoreIdStr).put("userNum", (Object) userNum);
        return this.dao2._queryBeanList("select * from remarkimg where scoreId in ({scoreIdStr[]}) and insertUser={userNum} ", RemarkImg.class, args);
    }

    public List<ScoringPointRect> toScoringPointRectList(List<Object> scoringPointRectJsonList) {
        if (CsUtils.IsNullOrEmpty(scoringPointRectJsonList)) {
            return null;
        }
        List<ScoringPointRect> list = new ArrayList<>();
        int[] stackedHeight = {0};
        scoringPointRectJsonList.forEach(s -> {
            if (s != null && !s.toString().equals("")) {
                List<ScoringPointRect> rects = (List) JSON.parseObject(s.toString(), JsonType.List2Bean1_ScoringPointRect, new Feature[0]);
                int subjectiveHeight = rects.get(0).getSubjectiveHeight();
                rects.forEach(r -> {
                    r.setY(r.getY() + stackedHeight[0]);
                    list.add(r);
                });
                stackedHeight[0] = stackedHeight[0] + subjectiveHeight;
            }
        });
        return list;
    }

    public Map<String, Object> getScoringPointRectJsonByScoreIds(List<Object> scoreIds) {
        if (CsUtils.IsNullOrEmpty(scoreIds)) {
            return null;
        }
        String scoreIdsStr = StrUtil.join(Const.STRING_SEPERATOR, scoreIds);
        Map args = StreamMap.create().put("scoreIdsStr", (Object) scoreIdsStr);
        Map<String, Object> scoringPointMap = this.dao2._queryOrderMap("select scoreId,scoringPoint from questionimage where scoreId in ({scoreIdsStr[]}) order by field(scoreId, {scoreIdsStr})", TypeEnum.StringObject, args);
        if (scoringPointMap == null || scoringPointMap.size() == 0) {
            return null;
        }
        return scoringPointMap;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String updateRemarkByArr(Map<String, String> map) {
        JSONObject jsonObject = JSON.parseObject(map.get("arg"));
        String returnStr1 = updateRemarkArr(jsonObject.getString("tasklist"), map);
        String returnStr2 = updateRemarkimg2(jsonObject, map);
        this.tagManageDAO.saveTag(jsonObject, "F");
        if (returnStr1.equals("0") || returnStr2.equals("0")) {
            return "0";
        }
        return returnStr1;
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public void updateIsFinished(String insertUser, String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("insertUser", insertUser);
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        this.dao2._execute("update questiongroup_user set isFinished=1 where userNum={insertUser} and exampaperNum={exampaperNum} and groupNum={groupNum}", args);
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public Integer yiPanIsXiaoyuQuota(String insertUser, String exampaperNum, String groupNum) {
        Map args = new HashMap();
        args.put("insertUser", insertUser);
        args.put("exampaperNum", exampaperNum);
        args.put("groupNum", groupNum);
        Integer quotaInteger = this.dao2._queryInt("SELECT num count FROM quota  WHERE groupNum ={ groupNum } and insertUser={insertUser}", args);
        Integer num = 0;
        if (quotaInteger != null) {
            Integer yiPanInteger = Convert.toInt(this.dao2._queryInt("SELECT count( 1 ) count FROM task  WHERE groupNum ={ groupNum } and insertUser={insertUser} AND STATUS = 'T' group by questionNum LIMIT 1 ", args), 0);
            if (yiPanInteger.intValue() >= quotaInteger.intValue()) {
                num = 1;
            }
        } else {
            num = 1;
        }
        return num;
    }

    public String updateRemarkArr(String para, Map<String, String> map) {
        String returnStr;
        String insertUser = map.get("insertUser");
        List<Map<String, String>> listObject = (List) JSON.parseObject(para, JsonType.List2Map1_StringString, new Feature[0]);
        String returnStr2 = "";
        try {
            int i = 0;
            for (Map<String, String> mapList : listObject) {
                AwardPoint awardPoint = new AwardPoint();
                awardPoint.setId(mapList.get("id"));
                awardPoint.setScoreId(mapList.get("scoreId"));
                String score = URLDecoder.decode(mapList.get("score"), "utf-8");
                awardPoint.setQuestionScore(String.valueOf(Float.valueOf(score)));
                awardPoint.setInsertUser(insertUser);
                awardPoint.setInsertDate(DateUtil.getCurrentTime());
                awardPoint.setExampaperNum(Integer.parseInt(mapList.get("exampaperNum")));
                awardPoint.setQuestionNum(mapList.get("questionNum"));
                awardPoint.setType("1");
                awardPoint.setIsModify("T");
                awardPoint.setStatus("T");
                awardPoint.setIsException(mapList.get("isException"));
                long start1 = System.currentTimeMillis();
                returnStr2 = this.dao.updateremark(awardPoint) + "";
                this.examService.deleteMarkError(awardPoint);
                if (i == 1) {
                    returnStr2 = returnStr2 + "0,";
                }
                long end_taskAndScore = System.currentTimeMillis() - start1;
                if (end_taskAndScore > 100) {
                    this.log.error(Long.valueOf(end_taskAndScore));
                }
                i++;
            }
            if (returnStr2.indexOf("-2") != -1) {
                returnStr = "-2";
            } else if (returnStr2.indexOf("0") != -1) {
                returnStr = "0";
            } else {
                returnStr = "1";
            }
            return returnStr;
        } catch (Exception e) {
            this.log.error("修改分数错误:" + e);
            throw new RuntimeException(e);
        }
    }

    @Override // com.dmj.service.awardPoint.AwardPointService
    public String updateScoreArr(Map<String, String> map) {
        JSONObject jsonObject = JSON.parseObject(map.get("arg"));
        String returnStr1 = updateTaskAndScoreArr(jsonObject.getString("tasklist"), map);
        String returnStr2 = updateRemarkimg(jsonObject, map);
        this.tagManageDAO.saveTag(jsonObject, "F");
        if (returnStr1.equals("0") || returnStr2.equals("0")) {
            return "0";
        }
        return returnStr1;
    }

    public String updateTaskAndScoreArr(String para, Map<String, String> map) {
        String returnStr;
        String insertUser = map.get("insertUser");
        List<Map<String, String>> listObject = (List) JSON.parseObject(para, JsonType.List2Map1_StringString, new Feature[0]);
        String returnStr2 = "";
        try {
            int i = 0;
            for (Map<String, String> mapList : listObject) {
                String score = String.valueOf(Float.valueOf(URLDecoder.decode(mapList.get("score"), "utf-8")));
                String makType = mapList.get("makType");
                String id = mapList.get("id");
                String scoreId = mapList.get("scoreId");
                String taskuserNum = mapList.get("userNum");
                String questionNum = mapList.get("questionNum");
                String exampaperNum = mapList.get("exampaperNum");
                String checkObj_value = mapList.get("checkObj_value");
                String studentId = mapList.get(Const.EXPORTREPORT_studentId);
                String groupNum = mapList.get("groupNum");
                String groupType = mapList.get("groupType");
                String isException = mapList.get("isException");
                String yipanStr = DateUtil.getCurrentTime2();
                if ("1".equals(checkObj_value)) {
                    yipanStr = "checkObj_value";
                }
                long start1 = System.currentTimeMillis();
                if (makType != null && makType.equals("0")) {
                    returnStr2 = (returnStr2 + updatet(id, score, insertUser, "F", yipanStr, scoreId, taskuserNum, makType, "") + Const.STRING_SEPERATOR) + updates(scoreId, score, insertUser, yipanStr) + Const.STRING_SEPERATOR;
                } else if (makType.equals("1")) {
                    returnStr2 = returnStr2 + updateTask(yipanStr, insertUser, score, id, Integer.parseInt(exampaperNum), scoreId, isException, questionNum, taskuserNum, makType, groupNum, studentId, groupType, i == listObject.size() - 1) + Const.STRING_SEPERATOR;
                }
                if (i == 1) {
                    returnStr2 = returnStr2 + "0,";
                }
                long end_taskAndScore = System.currentTimeMillis() - start1;
                if (end_taskAndScore > 100) {
                    this.log.error(Long.valueOf(end_taskAndScore));
                }
                i++;
                deleteCpMarkerror(scoreId);
            }
            if (returnStr2.indexOf("-2") != -1) {
                returnStr = "-2";
            } else if (returnStr2.indexOf("0") != -1) {
                returnStr = "0";
            } else {
                returnStr = "1";
            }
            return returnStr;
        } catch (Exception e) {
            this.log.error("修改分数错误:" + e);
            throw new RuntimeException(e);
        }
    }

    public String updateRemarkimg(JSONObject jsonObject, Map<String, String> map) {
        String para = jsonObject.getString("tasklist");
        String json = jsonObject.getString("taskjson");
        String taskBase64 = jsonObject.getString("taskBase64");
        byte[] remarkImgBytes = null;
        if (!StrUtil.isEmpty(taskBase64)) {
            remarkImgBytes = Base64.decode(taskBase64.substring(taskBase64.indexOf(Const.STRING_SEPERATOR) + 1));
        }
        List<Map<String, String>> listObject = (List) JSON.parseObject(para, JsonType.List2Map1_StringString, new Feature[0]);
        Map paraMap = listObject.get(0);
        String str = paraMap.get("exampaperNum");
        Object scoreId = paraMap.get("scoreId");
        Object insertUser = paraMap.get("insertUser");
        Object questionNum = paraMap.get("questionNum");
        Map args_graSubSql = new HashMap();
        args_graSubSql.put("exampaperNum", str);
        Map<String, Object> graSubMap = this.dao2._querySimpleMap("select gradeNum,subjectNum from exampaper where examPaperNum={exampaperNum}", args_graSubSql);
        String path = "BiaoJi/" + graSubMap.get(Const.EXPORTREPORT_gradeNum) + graSubMap.get(Const.EXPORTREPORT_subjectNum) + "/";
        String fileName = StrUtil.format("{}/{}.txt", new Object[]{questionNum.toString(), Long.valueOf(DbUtils.getUuid())});
        String File = StrUtil.format("{}/{}/{}", new Object[]{map.get("imgpath"), path, fileName});
        String returnStr = "1";
        if (!"[]".equals(json)) {
            long start1 = System.currentTimeMillis();
            RemarkImg remarkImg = (RemarkImg) this.dao2.queryBean("select * from remarkimg where scoreId=? and insertUser=?", RemarkImg.class, new Object[]{scoreId, insertUser});
            if (remarkImg != null) {
                remarkImg.setImg(path + fileName);
                remarkImg.setRemarktext(json);
                remarkImg.setUpdateUser(insertUser.toString());
                remarkImg.setUpdateTime(DateUtil.getCurrentTime());
                this.dao2.update(remarkImg);
                cn.hutool.core.io.FileUtil.writeBytes(remarkImgBytes, File);
            } else {
                RemarkImg remarkImg2 = new RemarkImg();
                remarkImg2.setId(String.valueOf(UuidUtil.getUuid()));
                remarkImg2.setScoreId(scoreId.toString());
                remarkImg2.setExampaperNum(str.toString());
                remarkImg2.setQuestionNum(questionNum.toString());
                remarkImg2.setImg(path + fileName);
                remarkImg2.setRemarktext(json);
                remarkImg2.setInsertUser(insertUser.toString());
                remarkImg2.setInsertDate(DateUtil.getCurrentTime());
                this.dao2.save(remarkImg2);
                cn.hutool.core.io.FileUtil.writeBytes(remarkImgBytes, File);
            }
            long end_taskAndScore = System.currentTimeMillis() - start1;
            if (end_taskAndScore > 100) {
            }
        } else {
            Map args = new HashMap();
            args.put("scoreId", paraMap.get("scoreId"));
            args.put("insertUser", map.get("insertUser"));
            String count = this.dao2._queryStr("select count(1) from remarkimg where scoreId={scoreId} and insertUser={insertUser} ", args);
            if (!"0".equals(count)) {
                returnStr = String.valueOf(this.dao2._execute("delete from remarkimg where scoreId={scoreId} and insertUser={insertUser} ", args));
            }
        }
        return returnStr;
    }

    public String updateRemarkimg2(JSONObject jsonObject, Map<String, String> map) {
        String para = jsonObject.getString("tasklist");
        String json = jsonObject.getString("taskjson");
        String taskBase64 = jsonObject.getString("taskBase64");
        byte[] remarkImgBytes = null;
        if (!StrUtil.isEmpty(taskBase64)) {
            remarkImgBytes = Base64.decode(taskBase64.substring(taskBase64.indexOf(Const.STRING_SEPERATOR) + 1));
        }
        List<Map<String, String>> listObject = (List) JSON.parseObject(para, JsonType.List2Map1_StringString, new Feature[0]);
        Map paraMap = listObject.get(0);
        String str = paraMap.get("exampaperNum");
        Object scoreId = paraMap.get("scoreId");
        Object insertUser = paraMap.get("insertUser");
        Object questionNum = paraMap.get("questionNum");
        Map args_graSubSql = new HashMap();
        args_graSubSql.put("exampaperNum", str);
        Map<String, Object> graSubMap = this.dao2._querySimpleMap("select gradeNum,subjectNum from exampaper where examPaperNum={exampaperNum}", args_graSubSql);
        String path = "BiaoJi/" + graSubMap.get(Const.EXPORTREPORT_gradeNum) + graSubMap.get(Const.EXPORTREPORT_subjectNum) + "/";
        String fileName = StrUtil.format("{}/{}.txt", new Object[]{questionNum.toString(), Long.valueOf(DbUtils.getUuid())});
        String File = StrUtil.format("{}/{}/{}", new Object[]{map.get("imgpath"), path, fileName});
        String returnStr = "1";
        Map args = new HashMap();
        args.put("scoreId", paraMap.get("scoreId"));
        args.put("insertUser", map.get("insertUser"));
        if (!"[]".equals(json)) {
            long start1 = System.currentTimeMillis();
            RemarkImg remarkImg = (RemarkImg) this.dao2._queryBean("select * from remarkimg where scoreId={scoreId} and insertUser={insertUser}", RemarkImg.class, args);
            if (remarkImg != null) {
                remarkImg.setImg(path + fileName);
                remarkImg.setRemarktext(json);
                remarkImg.setUpdateUser(insertUser.toString());
                remarkImg.setUpdateTime(DateUtil.getCurrentTime());
                this.dao2.update(remarkImg);
                cn.hutool.core.io.FileUtil.writeBytes(remarkImgBytes, File);
            } else {
                RemarkImg remarkImg2 = new RemarkImg();
                remarkImg2.setId(String.valueOf(UuidUtil.getUuid()));
                remarkImg2.setScoreId(scoreId.toString());
                remarkImg2.setExampaperNum(str.toString());
                remarkImg2.setQuestionNum(questionNum.toString());
                remarkImg2.setImg(path + fileName);
                remarkImg2.setRemarktext(json);
                remarkImg2.setInsertUser(insertUser.toString());
                remarkImg2.setInsertDate(DateUtil.getCurrentTime());
                this.dao2.save(remarkImg2);
                cn.hutool.core.io.FileUtil.writeBytes(remarkImgBytes, File);
            }
            long end_taskAndScore = System.currentTimeMillis() - start1;
            if (end_taskAndScore > 100) {
            }
        } else {
            String checkObj_value = paraMap.get("checkObj_value");
            if ("1".equals(checkObj_value)) {
                returnStr = String.valueOf(this.dao2._execute("delete from remarkimg where scoreId={scoreId} and insertUser={insertUser} ", args));
            }
        }
        return returnStr;
    }
}
