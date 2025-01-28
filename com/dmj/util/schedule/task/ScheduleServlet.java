package com.dmj.util.schedule.task;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.dmj.auth.CreateFile;
import com.dmj.auth.Modify;
import com.dmj.auth.bean.License;
import com.dmj.service.examManagement.ScheduleService;
import com.dmj.service.historyTable.HistoryTableService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.service.userManagement.UserService;
import com.dmj.serviceimpl.examManagement.ScheduleServiceImpl;
import com.dmj.serviceimpl.historyTable.HistoryTableServiceImpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.serviceimpl.userManagement.UserServiceImpl;
import com.dmj.util.Conffig;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.OnOffUtil;
import com.dmj.util.StaticClassResources;
import com.dmj.util.Util;
import com.dmj.util.app.EdeiInfo;
import com.dmj.util.quartz.QuartzManager;
import com.dmj.util.schedule.ExcuteScheduleTask;
import com.zht.db.ServiceFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;

/* loaded from: ScheduleServlet.class */
public class ScheduleServlet extends HttpServlet {
    private ServletConfig config;
    public static ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(1);
    Logger log = Logger.getLogger(getClass());
    private Conffig conff = new Conffig();
    UserService userService = (UserService) ServiceFactory.getObject(new UserServiceImpl());
    SystemService systemService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    HistoryTableService histablesService = (HistoryTableService) ServiceFactory.getObject(new HistoryTableServiceImpl());
    ScheduleService sch = (ScheduleService) ServiceFactory.getObject(new ScheduleServiceImpl());

    public void init(ServletConfig config) throws ServletException {
        String school_tiltle;
        String useImageServer;
        this.config = config;
        ServletContext context = config.getServletContext();
        context.getSessionCookieConfig().setName("cookie_" + GUID.getGUIDStr());
        String filePath = context.getRealPath("/");
        String licPath = "";
        String licPath1 = System.getProperty("user.home") + File.separator + "dmjtemplicense.txt";
        System.err.println(licPath1);
        context.setAttribute(Const.project_path, filePath);
        String info = "";
        int school_limit = 0;
        License licBean = null;
        try {
            licPath = CreateFile.getLicPath(filePath);
            context.setAttribute(Const.licPath, licPath);
            System.err.println("inithistablecount...");
            this.histablesService.inithistablecount(filePath);
            System.err.println("inithistablecount end...");
            licBean = CreateFile.getLicense(licPath);
            context.setAttribute(Const.licBean, licBean);
            school_limit = getSchoolLimit(licPath);
            if (info.length() > 1) {
                info = authSchoolCount(school_limit);
            }
            if (!DateUtil.authV(filePath)) {
                info = "license信息错误！！！！！";
            }
            if (DateUtil.authone(filePath)) {
                info = "License过期！！！！！";
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("验证license信息发生异常", e);
            context.setAttribute(Const.result_tag, "T");
            context.setAttribute(Const.result_tag_info, "license信息异常");
            System.err.println("验证license信息发生异常");
        }
        if (DateUtil.autlicense(licPath, licPath1)) {
            context.setAttribute(Const.result_tag, "T");
            context.setAttribute(Const.result_tag_info, "多个服务使用同一个License，请联系产品供应商！");
            this.log.error("License验证：多个服务使用同一个License，请联系产品供应商！");
            System.err.println("多个服务使用同一个License，请联系产品供应商！");
            return;
        }
        String aa = Util.auth(licPath);
        if (null != aa) {
            this.log.error("License验证：" + aa);
            context.setAttribute(Const.result_tag, "T");
            context.setAttribute(Const.result_tag_info, aa);
            System.err.println(aa);
            return;
        }
        if (null == info || info.equals("")) {
            Modify.modifyAuth(context, 0, "系统启动");
            Modify.modifyAuth1(context, licPath1, "系统启动");
        }
        String v_str = "";
        try {
            school_tiltle = DateUtil.getTitle(filePath);
            v_str = String.valueOf(countv(licPath));
        } catch (Exception e2) {
            school_tiltle = "";
            e2.printStackTrace();
        }
        String access_path = "";
        try {
            System.err.println("获取图片访问路径...");
            access_path = this.systemService.getImageServerUri();
            System.out.println("################ access_path: " + access_path);
            System.out.println("######### 重置自增id值 start");
            System.out.println("######### 重置自增id值 end");
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        if ("".length() > 2) {
            context.setAttribute(Const.result_tag, "T");
            context.setAttribute(Const.result_tag_info, "");
            this.log.error("License验证：");
            System.out.println("验证license信息发生异常:");
            return;
        }
        context.setAttribute(Const.result_tag, "F");
        context.setAttribute(Const.result_tag_info, "");
        try {
            System.err.println("加载配置文件...");
            String marktype = CreateFile.getLicense(licPath).getSystemType();
            if (null == marktype || marktype.equals("")) {
                Conffig conffig = this.conff;
                marktype = Conffig.getParameter(filePath, "type");
            }
            Conffig conffig2 = this.conff;
            String appQRcode_show = Conffig.getParameter(filePath, Const.appQRcode_show);
            Conffig conffig3 = this.conff;
            String fenfaTimeout = Conffig.getParameter(filePath, Const.fenfaTimeout);
            if (fenfaTimeout == null || !Pattern.compile("[0-9]*").matcher(fenfaTimeout).matches()) {
                fenfaTimeout = Const.fenfaTimeout;
            }
            Conffig conffig4 = this.conff;
            String queryUserName = Conffig.getParameter(filePath, Const.queryUserName);
            Conffig conffig5 = this.conff;
            String isImageServer = Conffig.getParameter(filePath, Const.isImageServer);
            Conffig conffig6 = this.conff;
            String reportpermission = Conffig.getParameter(filePath, Const.REPORT_PERMISSION_String);
            Conffig conffig7 = this.conff;
            String reportpermissionclass = Conffig.getParameter(filePath, Const.REPORT_PERMISSION_c_String);
            Conffig conffig8 = this.conff;
            String levelclass = Conffig.getParameter(filePath, Const.levelclass);
            Conffig conffig9 = this.conff;
            String export57_show = Conffig.getParameter(filePath, Const.export57_show);
            Conffig conffig10 = this.conff;
            String judgerule = Conffig.getParameter(filePath, Const.judgerule);
            Conffig conffig11 = this.conff;
            String showStudentInfo = Conffig.getParameter(filePath, Const.showStudentInfo);
            Conffig conffig12 = this.conff;
            String useImageServer2 = Conffig.getParameter(filePath, Const.useImageServer);
            Conffig conffig13 = this.conff;
            String ObjectItemCorrectClick = Conffig.getParameter(filePath, Const.ObjectItemCorrectClick);
            Conffig conffig14 = this.conff;
            String authcode = Conffig.getParameter(filePath, Const.authcode);
            Conffig conffig15 = this.conff;
            String allowedToLogin = Conffig.getParameter(filePath, Const.allowedToLogin);
            Conffig conffig16 = this.conff;
            String allowedToViewReport = Conffig.getParameter(filePath, Const.allowedToViewReport);
            String sysVersion = CreateFile.getLicense(licPath).getSysVersion();
            String showAnalyiseImage = CreateFile.getLicense(licPath).getShowAnalyiseImage();
            Date expiredDate = CreateFile.getLicense(licPath).getExpiredDate();
            String numberOfTestsAllowedPerMonth = CreateFile.getLicense(licPath).getNumberOfTestsAllowedPerMonth();
            String systemId = CreateFile.getLicense(licPath).getSystemId();
            Conffig conffig17 = this.conff;
            String stuExamImg = Conffig.getParameter(filePath, Const.stuExamImg);
            Conffig conffig18 = this.conff;
            String markDetail = Conffig.getParameter(filePath, Const.markDetail);
            Conffig conffig19 = this.conff;
            String examInitType = Conffig.getParameter(filePath, Const.examInitType);
            Conffig conffig20 = this.conff;
            String zhengshiSubNum = Conffig.getParameter(filePath, Const.zhengshiSubNum);
            Conffig conffig21 = this.conff;
            String markAveScore = Conffig.getParameter(filePath, Const.markAveScore);
            Conffig conffig22 = this.conff;
            String online_show = Conffig.getParameter(filePath, Const.online_show);
            Conffig conffig23 = this.conff;
            String markAutocommit = Conffig.getParameter(filePath, Const.markAutocommit);
            Conffig conffig24 = this.conff;
            String deductScore = Conffig.getParameter(filePath, Const.deductScore);
            Conffig conffig25 = this.conff;
            String viewAllReports = Conffig.getParameter(filePath, Const.viewAllReports);
            Conffig conffig26 = this.conff;
            String g1QuanquLabel = Conffig.getParameter(filePath, Const.g1QuanquLabel);
            Conffig conffig27 = this.conff;
            String L2Allsubpassrait = Conffig.getParameter(filePath, Const.L2Allsubpassrait);
            Conffig conffig28 = this.conff;
            String viewRankInfo = Conffig.getParameter(filePath, Const.viewRankInfo);
            Conffig conffig29 = this.conff;
            String viewRankOfScoreInfo = Conffig.getParameter(filePath, Const.viewRankOfScoreInfo);
            Conffig conffig30 = this.conff;
            String version = Conffig.getSysconffParameter(filePath, "version");
            Conffig conffig31 = this.conff;
            String asUrl = Conffig.getSysconffParameter(filePath, Const.asUrl);
            Conffig conffig32 = this.conff;
            String arthasUrl = Conffig.getSysconffParameter(filePath, Const.arthasUrl);
            Conffig conffig33 = this.conff;
            String progressAllOrPer = Conffig.getParameter(filePath, Const.progressAllOrPer);
            Conffig conffig34 = this.conff;
            String isStartDistribute = Conffig.getParameter(filePath, Const.isStartDistribute);
            Conffig conffig35 = this.conff;
            String isStartBigTable = Conffig.getParameter(filePath, Const.isStartBigTable);
            Conffig conffig36 = this.conff;
            String yuejuanMode = Conffig.getParameter(filePath, Const.yuejuanMode);
            Conffig conffig37 = this.conff;
            String scoreAvgOrRound = Conffig.getParameter(filePath, Const.scoreAvgOrRound);
            Conffig conffig38 = this.conff;
            String baogandaoxiao = Conffig.getParameter(filePath, Const.baogandaoxiao);
            Conffig conffig39 = this.conff;
            String showcalculateondetail = Conffig.getParameter(filePath, Const.showcalculateondetail);
            Conffig conffig40 = this.conff;
            String pizhushow = Conffig.getParameter(filePath, Const.pizhushow);
            Conffig conffig41 = this.conff;
            Conffig.getParameter(filePath, "testingcentredis");
            Conffig conffig42 = this.conff;
            String MoreSchool = Conffig.getParameter(filePath, Const.MoreSchool);
            Conffig conffig43 = this.conff;
            String isJointStuType = Conffig.getParameter(filePath, Const.isJointStuType);
            Conffig conffig44 = this.conff;
            String isMultipleTeachers = Conffig.getParameter(filePath, Const.isMultipleTeachers);
            Conffig conffig45 = this.conff;
            String esAverageScore = Conffig.getParameter(filePath, Const.esAverageScore);
            Conffig conffig46 = this.conff;
            String classAverage = Conffig.getParameter(filePath, Const.classAverage);
            Conffig conffig47 = this.conff;
            String gradeAverage = Conffig.getParameter(filePath, Const.gradeAverage);
            Conffig conffig48 = this.conff;
            String areaAverage = Conffig.getParameter(filePath, Const.areaAverage);
            Conffig conffig49 = this.conff;
            String questionGroup = Conffig.getParameter(filePath, Const.questionGroup);
            Conffig conffig50 = this.conff;
            String showFreePaper = Conffig.getParameter(filePath, Const.showFreePaper);
            Conffig conffig51 = this.conff;
            String defaultFufen = Conffig.getParameter(filePath, Const.defaultFufen);
            Conffig conffig52 = this.conff;
            String dengjiSortRule = Conffig.getParameter(filePath, Const.DENGJI_SORT_RULE);
            Conffig conffig53 = this.conff;
            String isAllSchoolManager = Conffig.getParameter(filePath, Const.IS_ALLSCHOOL_MANAGER);
            Conffig conffig54 = this.conff;
            String scoreReleased = Conffig.getParameter(filePath, Const.SCORE_RELEASED);
            Conffig conffig55 = this.conff;
            String isOpenAllSchool = Conffig.getParameter(filePath, Const.IS_OPEN_ALL_SCHOOL);
            System.err.println("设置 useImageServer...");
            if (null == useImageServer2 || useImageServer2.equals("0")) {
                useImageServer = "0";
                context.setAttribute(Const.IMAGEACCESSPATH, "");
            } else if (useImageServer2.equals("2")) {
                useImageServer = "2";
                context.setAttribute(Const.IMAGEACCESSPATH, "");
                context.setAttribute(Const.IMAGEACCESSPATH_D, access_path);
            } else {
                useImageServer = "1";
                context.setAttribute(Const.IMAGEACCESSPATH, access_path);
            }
            System.err.println("初始化isImageServer：" + isImageServer + "...");
            context.setAttribute(Const.isImageServer, isImageServer);
            System.err.println("初始化useImageServer：" + useImageServer + "...");
            context.setAttribute(Const.useImageServer, useImageServer);
            context.setAttribute(Const.newImageServer, access_path);
            context.setAttribute(Const.queryUserName, queryUserName);
            context.setAttribute(Const.REPORT_PERMISSION_String, null == reportpermission ? "0" : reportpermission);
            context.setAttribute(Const.REPORT_PERMISSION_c_String, null == reportpermissionclass ? "0" : reportpermissionclass);
            context.setAttribute(Const.levelclass, null == levelclass ? "F" : levelclass.toUpperCase());
            context.setAttribute(Const.export57_show, (null == export57_show || "0".equals(export57_show.trim())) ? "0" : "1");
            context.setAttribute(Const.judgerule, null == judgerule ? "1" : judgerule);
            context.setAttribute(Const.showStudentInfo, null == showStudentInfo ? "0" : showStudentInfo);
            context.setAttribute(Const.ObjectItemCorrectClick, null == ObjectItemCorrectClick ? "0" : ObjectItemCorrectClick);
            context.setAttribute(Const.authcode, null == authcode ? "0" : authcode);
            context.setAttribute(Const.allowedToLogin, null == allowedToLogin ? "0" : allowedToLogin);
            context.setAttribute(Const.allowedToViewReport, null == allowedToViewReport ? "0" : allowedToViewReport);
            context.setAttribute("showAnalyiseImage", null == showAnalyiseImage ? "1" : showAnalyiseImage);
            context.setAttribute(Const.school_tiltle, school_tiltle);
            context.setAttribute(Const.school_limit, Integer.valueOf(school_limit));
            context.setAttribute("type", (null == marktype || "".equals(marktype)) ? "2" : marktype);
            context.setAttribute(Const.system_version, v_str);
            context.setAttribute("sysVersion", null == sysVersion ? "1" : sysVersion);
            context.setAttribute("expiredDate", (null == expiredDate || expiredDate.equals("")) ? "0" : expiredDate);
            context.setAttribute(Const.allowed_create_num, (null == numberOfTestsAllowedPerMonth || numberOfTestsAllowedPerMonth.equals("")) ? "99" : numberOfTestsAllowedPerMonth);
            context.setAttribute("systemId", null == systemId ? "" : systemId);
            context.setAttribute(Const.stuExamImg, (null == stuExamImg || !"0".equals(stuExamImg.trim())) ? "1" : "0");
            context.setAttribute(Const.markDetail, (null == markDetail || "".equals(markDetail)) ? "0" : markDetail);
            context.setAttribute(Const.examInitType, (null == examInitType || "".equals(examInitType)) ? "0" : examInitType);
            context.setAttribute(Const.zhengshiSubNum, (null == zhengshiSubNum || "".equals(zhengshiSubNum.trim())) ? "" : zhengshiSubNum);
            context.setAttribute(Const.markAveScore, (null == markAveScore || "".equals(markAveScore)) ? "1" : markAveScore);
            context.setAttribute(Const.appQRcode_show, (null == appQRcode_show || "0".equals(appQRcode_show.trim())) ? "none" : "block");
            context.setAttribute(Const.fenfaTimeout, fenfaTimeout);
            context.setAttribute(Const.online_show, (null == online_show || "".equals(online_show.trim())) ? "1" : online_show);
            context.setAttribute(Const.markAutocommit, (null == markAutocommit || "".equals(markAutocommit.trim())) ? "1" : markAutocommit);
            context.setAttribute(Const.deductScore, (null == deductScore || !"1".equals(deductScore.trim())) ? "0" : "1");
            context.setAttribute(Const.viewAllReports, (null == viewAllReports || !"0".equals(viewAllReports.trim())) ? "1" : "0");
            context.setAttribute(Const.progressAllOrPer, (null == progressAllOrPer || !"0".equals(progressAllOrPer.trim())) ? "1" : "0");
            context.setAttribute(Const.yuejuanMode, (null == yuejuanMode || !"0".equals(yuejuanMode.trim())) ? "1" : "0");
            context.setAttribute(Const.scoreAvgOrRound, (null == scoreAvgOrRound || !"0".equals(scoreAvgOrRound.trim())) ? "1" : "0");
            context.setAttribute(Const.baogandaoxiao, (null == baogandaoxiao || !"1".equals(baogandaoxiao.trim())) ? "0" : "1");
            context.setAttribute(Const.showcalculateondetail, (null == showcalculateondetail || !"0".equals(showcalculateondetail.trim())) ? "1" : "0");
            context.setAttribute(Const.pizhushow, (null == pizhushow || !"1".equals(pizhushow.trim())) ? "0" : "1");
            String isStartDistribute2 = (isStartDistribute == null || "".equals(isStartDistribute.trim()) || "1".equals(isStartDistribute.trim())) ? "1" : "0";
            String isStartBigTable2 = (isStartBigTable == null || "".equals(isStartBigTable.trim()) || "1".equals(isStartBigTable.trim())) ? "1" : "0";
            context.setAttribute(Const.MoreSchool, (null == MoreSchool || !"1".equals(MoreSchool.trim())) ? "0" : "1");
            context.setAttribute(Const.numOfbindStudent, (0 == "1" || "".equals("1".trim()) || "1".equals("1".trim())) ? "1" : "1");
            context.setAttribute(Const.esAverageScore, (null == esAverageScore || "".equals(esAverageScore.trim()) || "0".equals(esAverageScore.trim())) ? "0" : "1");
            context.setAttribute(Const.classAverage, (null == classAverage || "".equals(classAverage.trim()) || "1".equals(classAverage.trim())) ? "1" : "0");
            context.setAttribute(Const.gradeAverage, (null == gradeAverage || "".equals(gradeAverage.trim()) || "1".equals(gradeAverage.trim())) ? "1" : "0");
            context.setAttribute(Const.areaAverage, (null == areaAverage || "".equals(areaAverage.trim()) || "1".equals(areaAverage.trim())) ? "1" : "0");
            context.setAttribute(Const.questionGroup, (null == questionGroup || "".equals(questionGroup.trim()) || "0".equals(questionGroup.trim())) ? "0" : "1");
            context.setAttribute(Const.g1QuanquLabel, (null == g1QuanquLabel || !"0".equals(g1QuanquLabel.trim())) ? "1" : "0");
            context.setAttribute(Const.L2Allsubpassrait, (null == L2Allsubpassrait || !"0".equals(L2Allsubpassrait.trim())) ? "1" : "0");
            context.setAttribute(Const.viewRankInfo, (null == viewRankInfo || !"0".equals(viewRankInfo.trim())) ? "1" : "0");
            context.setAttribute(Const.viewRankOfScoreInfo, (null == viewRankOfScoreInfo || "".equals(viewRankOfScoreInfo.trim())) ? "1" : viewRankOfScoreInfo);
            context.setAttribute(Const.isJointStuType, (null == isJointStuType || !"0".equals(isJointStuType.trim())) ? "1" : "0");
            context.setAttribute("version", (null == version || "".equals(version.trim())) ? "" : version);
            context.setAttribute(Const.asUrl, asUrl);
            context.setAttribute(Const.isMultipleTeachers, (null == isMultipleTeachers || !"1".equals(isMultipleTeachers.trim())) ? "0" : "1");
            context.setAttribute(Const.checkMinThreshold, "1");
            context.setAttribute(Const.showFreePaper, (null == showFreePaper || !"1".equals(showFreePaper.trim())) ? "0" : "1");
            context.setAttribute(Const.defaultFufen, (null == defaultFufen || !"0".equals(defaultFufen.trim())) ? "1" : "0");
            context.setAttribute(Const.DENGJI_SORT_RULE, (null == dengjiSortRule || !"1".equals(dengjiSortRule.trim())) ? "0" : "1");
            context.setAttribute(Const.IS_ALLSCHOOL_MANAGER, (null == isAllSchoolManager || !"0".equals(isAllSchoolManager.trim())) ? "1" : "0");
            context.setAttribute(Const.SCORE_RELEASED, (null == scoreReleased || !"0".equals(scoreReleased.trim())) ? "1" : "0");
            context.setAttribute(Const.IS_OPEN_ALL_SCHOOL, (null == isOpenAllSchool || !"0".equals(isOpenAllSchool.trim())) ? "1" : "0");
            if (school_limit < 5) {
                context.setAttribute(Const.allowed_create_num, "99");
            } else {
                context.setAttribute(Const.allowed_create_num, "4");
            }
            System.out.println("################ mark_type: 333" + context.getAttribute("type") + "###");
            System.out.println("################ showAnalyiseImage: 333" + context.getAttribute("showAnalyiseImage") + "###");
            System.out.println("################ sysVersion: 333" + context.getAttribute("sysVersion") + "###");
            System.out.println("################ access_path: " + context.getAttribute(Const.IMAGEACCESSPATH) + "###");
            StaticClassResources.EdeiInfo.setEdeiVersion(version).setProjectPath(filePath).setSystemId(licBean.getSystemId()).setAsUrl(asUrl).setVendor(licBean.getVendor()).setMoreSchool(school_limit > 1).setPid(EdeiInfo.getProccessPid()).setArthasUrl(arthasUrl);
            StartLoader.init();
            new ExcuteScheduleTask().executeScheduleTask(context, filePath);
            QuartzManager.addOpenOcsJob(30, context);
            OnOffUtil.cacheOpenOcsInfo(context, asUrl, licBean.getSystemId());
            String signnl = this.userService.getRank("11");
            context.setAttribute("signnl", signnl);
            System.out.println("################ 启动连接池监控日志   ################");
            QuartzManager.addHikariMonitorJob(5);
            System.out.println("################ 启动扫描端进度   ################");
            QuartzManager.addScanProcessJob(15);
            if ("1".equals(isStartDistribute2)) {
                System.out.println("################   启动分发进程          ################");
                QuartzManager.addDistributeQuestionGroupJob();
                QuartzManager.removeCompletedGroup(10, context);
                context.setAttribute(Const.wait_remove_groupNum, new ArrayList());
            }
            System.out.println("################ 启动查询试扫未审核数的线程   ################");
            QuartzManager.addImageCheckJob(3, context);
            System.out.println("################ 启动查询在线人数的线程   ################");
            QuartzManager.addOnlineUserJob(5, context);
            if ("1".equals(isStartBigTable2)) {
                QuartzManager.addBigTableDataJob(1, context);
            }
            QuartzManager.addYuejuanProgressJob(2, context);
            OnOffUtil.cacheAppEnabledInfo(context, asUrl, licBean.getSystemId(), "student");
            OnOffUtil.cacheAppEnabledInfo(context, asUrl, licBean.getSystemId(), "teacher");
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        SerializeConfig.getGlobalInstance().put(Long.class, new 1(this));
    }

    public boolean countv(String licPath) throws Exception {
        License lic = CreateFile.getLicense(licPath);
        String version = lic.getVersion();
        String s = version.substring(0, 1);
        if (s.equals(Const.VERSION_tag)) {
            this.log.debug(" 区版登陆......");
            return true;
        }
        this.log.debug(" 学校版登陆......");
        return false;
    }

    public String authSchoolCount(int count_limit) throws Exception {
        String info = "";
        int count = this.userService.getSchoolNum();
        if (count > count_limit) {
            info = "学校个数超限，应为" + count_limit + "个，目前为" + count + "个。";
        }
        return info;
    }

    public int getSchoolLimit(String filePath) throws Exception {
        new CreateFile();
        License lic = CreateFile.getLicense(filePath);
        if (null == lic) {
            return 0;
        }
        String limit = lic.getSchools();
        if (null == limit) {
            limit = "0";
        }
        int count_limit = Integer.parseInt(limit);
        return count_limit;
    }
}
