package com.dmj.util.config;

import cn.hutool.core.util.StrUtil;
import com.dmj.domain.configration.ConfigField;
import com.dmj.service.sysConfig.sysConfigService;
import com.dmj.serviceimpl.sysConfig.sysConfigServiceImpl;
import com.dmj.util.Const;
import com.dmj.util.msg.RspMsg;
import com.zht.db.ServiceFactory;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/* loaded from: Configuration.class */
public class Configuration implements Serializable {
    private static final Logger log = Logger.getLogger(Configuration.class);

    @ConfigFieldAnnotation(confKey = "type", confName = "有痕无痕设置", confValue = "2", description = "有痕无痕设置", check = "checkType", errorMsg = "值必须是1或者2！")
    private String type;

    @ConfigFieldAnnotation(confKey = Const.queryUserName, confName = "是否显示查询登录账号", confValue = "2", description = "是否显示查询登录账号功能，1，是隐藏，2是显示", check = "checkQueryusername", errorMsg = "值必须是1或者2！！！")
    private String queryusername;

    @ConfigFieldAnnotation(confKey = Const.authcode, confName = "是否有显示验证码", confValue = "0", description = "0是隐藏，1是显示，没有配置是隐藏", check = "checkAuthcode", errorMsg = "错误！！！！")
    private String authcode;

    @ConfigFieldAnnotation(confKey = Const.MoreSchool, confName = "是否是多校", confValue = "1", description = "单个科目主客观成绩导出时单/多校的判断，0是单校，1是多校，不填、其他值、没有配置是单校", check = "checkMoreSchool", errorMsg = "错误！！！！")
    private String MoreSchool;

    @ConfigFieldAnnotation(confKey = Const.deleteexam, confName = "保留考试的天数", confValue = "1000", description = "未启用", check = "checkDeleteexam", errorMsg = "错误！！！")
    private String deleteexam;

    @ConfigFieldAnnotation(confKey = Const.moveexam, confName = "移动到历史表的天数", confValue = "1000", description = "当距离创建考试时间超过当前设置的天数时，将会把过期的考试数据移动到历史表（每天定时检查）", check = "checkMoveexam", errorMsg = "错误！！！")
    private String moveexam;

    @ConfigFieldAnnotation(confKey = Const.deleteexammessage, confName = "保留考试相关信息的天数", confValue = "1000", description = "当距离创建考试时间超过当前设置的天数时，将会删除考试的部分相关信息（每天定时检查）", check = "checkDeleteexammessage", errorMsg = "错误！！！")
    private String deleteexammessage;

    @ConfigFieldAnnotation(confKey = "historytable", confName = "每张历史表中数据条数", confValue = "200", description = "", check = "checkHistorytable", errorMsg = "错误！！！")
    private String historytable;

    @ConfigFieldAnnotation(confKey = "ncount", confName = "学生总数", confValue = "10000", description = "", check = "checkNcount", errorMsg = "错误！！！！")
    private String ncount;

    @ConfigFieldAnnotation(confKey = Const.licPath, confName = "授权文件路径", confValue = "d\\://license.lic", description = "授权文件的物理路径", check = "checkLicPath", errorMsg = "错误！！")
    private String licPath;

    @ConfigFieldAnnotation(confKey = Const.levelclass, confName = "是否是教学班", confValue = "F", description = "是否是教学班系统，T:教学班，F:行政班", check = "checkLevelclass", errorMsg = "错误！！！！)")
    private String levelclass;

    @ConfigFieldAnnotation(confKey = "deleteNames", confName = "删除项目中下载的临时文件名", confValue = "reportExcel,export_excel,\\u6210\\u7EE9,upload,ExportFolder", description = "每天定时删除项目中下载的临时文件的名称", check = "checkDeleteNames", errorMsg = "错误！！！！")
    private String deleteNames;

    @ConfigFieldAnnotation(confKey = "insertIntoDefine", confName = "是否导向双向细目表", confValue = "0", description = "导入小题分时一种是导入双向细目表数据，一种是导入分数，0:是导入分数，1是导入双向细目表，不填写时表示导入分数", check = "checkInsertIntoDefine", errorMsg = "错误！！")
    private String insertIntoDefine;

    @ConfigFieldAnnotation(confKey = Const.zeroScoreInCount, confName = "是否统计零分", confValue = "0", description = "数据计算时是否统计零分，0是不统计，1是统计,不填写时表示不统计", check = "checkZeroScoreInCount", errorMsg = "错误！！！")
    private String zeroScoreInCount;

    @ConfigFieldAnnotation(confKey = Const.ObjectItemCorrectClick, confName = "是否可以双击打开大图", confValue = "1", description = "客观题校对页面，选项图片是都开放打开大图功能，0是不显示，1是显示,不填写时表示不显示", check = "checkObjectItemCorrectClick", errorMsg = "错误！！")
    private String ObjectItemCorrectClick;

    @ConfigFieldAnnotation(confKey = "Ac01", confName = "dmj账号密码", confValue = "b350d88e5fd1e071174377ea7de90620aebc6d191764211792120ffa718cb89efc8e7a1d96d10de53f0eb60fba16f8628deecdb23beec1c9b3afe8a932f3ec5a", description = "dmj账号加密后的密码", check = "checkAc01", errorMsg = "错误！！")
    private String Ac01;

    @ConfigFieldAnnotation(confKey = Const.useImageServer, confName = "是否启用图片服务器", confValue = "0", description = "本服务器是都作为图片服务器，0是作为图片服务器，1是不使用本服务器作为图片服务器，图片服务器为外网，2是不使用本服务器作为图片服务器，图片服务器为局域网", check = "checkUseImageServer", errorMsg = "值必须为0或者1或者2！！！")
    private String useImageServer;

    @ConfigFieldAnnotation(confKey = Const.isImageServer, confName = "本服务是否是图片服务器", confValue = "1", description = "本服务是否是图片服务器，0是不是，1是是", check = "checkIsImageServer", errorMsg = "值必须为0或者1！！！")
    private String isImageServer;

    @ConfigFieldAnnotation(confKey = Const.stuExamImg, confName = "是否显示学生答题图片", confValue = "1", description = "用户是否可查看学生的答题卡或者小题图片，1是教师可看-学生可看（默认），2是教师可看-学生不可看，3是教师不可看-学生可看，4是教师不可看-学生不可看", check = "checkStuExamImg", errorMsg = "必须是1或者2或者3或者4！！！")
    private String stuExamImg;

    @ConfigFieldAnnotation(confKey = Const.markDetail, confName = "是否显示查看判分", confValue = "1", description = "学生答题查询及申诉处理页面：网阅考试&主观题的查询结果操作一栏是否显示‘查看判分’，0是隐藏，1是显示，不填时表示隐藏", check = "checkMarkDetail", errorMsg = "错误！！！")
    private String markDetail;

    @ConfigFieldAnnotation(confKey = Const.examInitType, confName = "默认考试类型", confValue = "0", description = "创建考试-科目中默认选中有痕还是网阅，0是网阅，1是有痕，2是有痕&网阅，3是在线测评", check = "checkExamInitType", errorMsg = "值必须为0或者1！！！")
    private String examInitType;

    @ConfigFieldAnnotation(confKey = Const.zhengshiSubNum, confName = "政史科目编号", confValue = "", description = "定制报表C19专用：初三等级排名顺序中需要政史科目编号，1.填写政史科目编号：C19显示政史科目数据,2.不填：C19不显示政史科目数据", check = "checkZhengshiSubNum", errorMsg = "错误！！！！")
    private String zhengshiSubNum;

    @ConfigFieldAnnotation(confKey = Const.markAveScore, confName = "是否显示判分平均分", confValue = "0", description = "判分页面是否显示平均分、总平均分、工作量总结题组进度-题组详情页面是否显示平均分，0.隐藏,1.显示，不填表示显示", check = "checkMarkAveScore", errorMsg = "错误！！！")
    private String markAveScore;

    @ConfigFieldAnnotation(confKey = Const.appQRcode_show, confName = "是否显示家长端APP二维码", confValue = "1", description = "web端登录页面是否显示‘学情达（家长端）二维码’，0.隐藏,1.显示，不填表示显示，没有配置隐藏", check = "checKAppQRcode_show", errorMsg = "错误！！！")
    private String appQRcode_show;

    @ConfigFieldAnnotation(confKey = Const.online_show, confName = "上线情况显示", confValue = "0", description = "学业评价报告页面：第二部分评价分析中上线情况显示的是否达到上线设置的分数还是四率设置的分数’，0.以四率设置为标准,1.以上线设置为标准，不填表示一上线设置为标准", check = "checkOnline_show", errorMsg = "错误！！！")
    private String online_show;

    @ConfigFieldAnnotation(confKey = Const.markAutocommit, confName = "是否显示自动提交", confValue = "1", description = "判分页面是否显示自动提交功能，0.隐藏,1.显示，不填表示显示，填写其他值为隐藏", check = "checkMarkAutocommit", errorMsg = "错误！！！")
    private String markAutocommit;

    @ConfigFieldAnnotation(confKey = Const.export57_show, confName = "是否显示导出57中", confValue = "0", description = "教学管理-学生管理页面、教师管理页面是否显示‘导出57中学生信息、‘导出57中教师’功能，0.隐藏,1.显示，不填或其他值表示显示，没有配置为隐藏", check = "checkExport57_show", errorMsg = "错误！！！")
    private String export57_show;

    @ConfigFieldAnnotation(confKey = Const.TK_url, confName = "题库地址", confValue = "http://192.168.1.110:26603/edei_tk/", description = "考试管理-在线测评设置页面 1.从题库系统获取试卷 2.选择试卷后自动生成双向细目表", check = "checkTK_url", errorMsg = "错误！！！！")
    private String TK_url;

    @ConfigFieldAnnotation(confKey = "fenfaTimeout", confName = "默认分发超时时间", confValue = Const.fenfaTimeout, description = "防止阅卷时发生分发卡死现象，填写数字，不填写时默认30", check = "checkFenfaTimeout", errorMsg = "错误！！！！")
    private String fenfaTimeout;

    @ConfigFieldAnnotation(confKey = Const.deductScore, confName = "是否显示扣分", confValue = "0", description = "普通判分页面是否显示扣分功能,0是隐藏，1是显示，不填、其他值、没有配置是隐藏", check = "checkDeductScore", errorMsg = "错误！！！")
    private String deductScore;

    @ConfigFieldAnnotation(confKey = Const.viewAllReports, confName = "是否可以查看所有报表数据", confValue = "0", description = "诊断检测下是否允许查看所有报表数据,0是只能看自己权限范围的，1是可以查看所有的，不填、其他值、没有配置是1", check = "checkViewAllReports", errorMsg = "错误！！！")
    private String viewAllReports;

    @ConfigFieldAnnotation(confKey = Const.g1QuanquLabel, confName = "G1报表是否显示查看全区数据的按钮“综合指标-全区”", confValue = "1", description = "G1报表是否显示查看全区数据的按钮“综合指标-全区”，0是不显示，1是显示，", check = "checkG1QuanquLabel", errorMsg = "必须是0或者1！！！！")
    private String g1QuanquLabel;

    @ConfigFieldAnnotation(confKey = Const.viewRankInfo, confName = "报表是否显示排名信息", confValue = "1", description = "部分报表是否显示排名信息，0是不显示，1是显示，", check = "checkViewRankInfo", errorMsg = "必须是0或者1！！！！")
    private String viewRankInfo;

    @ConfigFieldAnnotation(confKey = Const.astrictLogin, confName = "学生（或家长）用户是否限制登录", confValue = "1", description = "学生（或家长）用户是否限制登录，1是限制登录，不填、其他值、没有配置是不限制", check = "checkAstrictLogin", errorMsg = "错误！！！！")
    private String astrictLogin;

    @ConfigFieldAnnotation(confKey = Const.progressAllOrPer, confName = "判分进度条显示全部进度或者个人进度", confValue = "1", description = "判分进度条显示当前判分用户已判总量在当前科目需要判分总量中的比例或者当前判分用户已判总量在当前科目个人需要判分总量中的比例，0是显示在全部工作量中的进度，1是显示在个人工作量中的进度（默认）", check = "checkProgressAllOrPer", errorMsg = "必须是0或者1！！！！")
    private String progressAllOrPer;

    @ConfigFieldAnnotation(confKey = Const.isStartDistribute, confName = "是否启动分发线程", confValue = "1", description = "是否启动分发线程，0是不启动，1是启动（默认）", check = "checkIsStartDistribute", errorMsg = "必须是0或者1！！！")
    private String isStartDistribute;

    @ConfigFieldAnnotation(confKey = Const.isJointStuType, confName = "是否拼接学生类型", confValue = "1", description = "统计输出-单科目、总科目、总科目（标准分）、科目详情中学生姓名后是否拼接学生类型，0：不拼接，1：拼接（默认）", check = "checkIsJointStuType", errorMsg = "必须是0或者1！！！")
    private String isJointStuType;

    @ConfigFieldAnnotation(confKey = Const.isShowG1Quanqu, confName = "G1类层全区是否加限制", confValue = "0", description = "G1类层全区显示是否加限制，0是加限制（默认），1是不加限制", check = "checkIsShowG1Quanqu", errorMsg = "必须是0或者1！！！")
    private String isShowG1Quanqu;

    @ConfigFieldAnnotation(confKey = "userposition", confName = "用户职务", confValue = "0", description = "未启用", check = "checkUserposition", errorMsg = "错误！！！")
    private String userposition;

    @ConfigFieldAnnotation(confKey = Const.REPORT_PERMISSION_String, confName = "报表科目权限", confValue = "0", description = "未启用", check = "checkReportpermission", errorMsg = "错误！！！")
    private String reportpermission;

    @ConfigFieldAnnotation(confKey = Const.REPORT_PERMISSION_c_String, confName = "报表班级权限", confValue = "0", description = "未启用", check = "checkReportpermissionclass", errorMsg = "错误！！！")
    private String reportpermissionclass;

    @ConfigFieldAnnotation(confKey = "maxpanfenff", confName = "判分最大分发数", confValue = "600", description = "未启用", check = "checkMaxpanfenff", errorMsg = "错误！！！")
    private String maxpanfenff;

    @ConfigFieldAnnotation(confKey = "minpanfenff", confName = "判分最小分发数", confValue = "50", description = "未启用", check = "checkMinpanfenff", errorMsg = "错误！！！")
    private String minpanfenff;

    @ConfigFieldAnnotation(confKey = Const.judgerule, confName = "裁决得分方式", confValue = "1", description = "未启用", check = "checkJudgerule", errorMsg = "错误！！！")
    private String judgerule;

    @ConfigFieldAnnotation(confKey = "version_date", confName = "版本日期", confValue = "2018_01_08", description = "未启用", check = "checkVersion_date", errorMsg = "错误！！！")
    private String version_date;

    @ConfigFieldAnnotation(confKey = Const.day, confName = "授权即将到期提示时间", confValue = Const.fenfaTimeout, description = "未启用", check = "checkDay", errorMsg = "错误！！！")
    private String day;

    @ConfigFieldAnnotation(confKey = "testingcentredis", confName = "按考点分发题", confValue = "1", description = "阅卷分发时，是否要通过考点来控制分发，1是关闭（不通过考点控制）题组管理-考点分发隐藏，0是开启", check = "checkTestingcentredis", errorMsg = "必须是0或者1！！！")
    private String testingcentredis;

    @ConfigFieldAnnotation(confKey = Const.L2Allsubpassrait, confName = "L2报表是否显示全科目及格率", confValue = "0", description = "L2报表是否显示全科目及格率，0是不显示（默认），1是显示", check = "checkL2Allsubpassrait", errorMsg = "必须是0或者1！！！")
    private String L2Allsubpassrait;

    @ConfigFieldAnnotation(confKey = Const.viewRankOfScoreInfo, confName = "报表排名隐藏", confValue = "1", description = "所有涉及到分数排名的报表是否显示排名信息，1：所有排名不隐藏（默认）  2：学生相关排名隐藏   3：所有排名都隐藏", check = "checkViewRankOfScoreInfo", errorMsg = "必须是1或者2或者3！！！")
    private String viewRankOfScoreInfo;

    @ConfigFieldAnnotation(confKey = "IsInheritMotherBySelf", confName = "是否允许自己套模板", confValue = "0", description = "老师，在申请模板的时候，同时自己也可以套用模板进行裁切，0是不可以（默认），1是可以", check = "checkIsInheritMotherBySelf", errorMsg = "必须是0或者1！！！")
    private String IsInheritMotherBySelf;

    @ConfigFieldAnnotation(confKey = Const.isMultipleTeachers, confName = "同一职务是否允许存在多个教师设定", confValue = "0", description = "班主任及某个班的任课教师是否可以多人老师当担，0是只能一个（默认），1是允许多个", check = "checkIsMultipleTeachers", errorMsg = "必须是0或者1！！！")
    private String isMultipleTeachers;

    @ConfigFieldAnnotation(confKey = Const.numOfbindStudent, confName = "家长绑定学生数", confValue = "1", description = "一个家长一个年级可以绑定几个学生", check = "checkNumOfbindStudent", errorMsg = "错误！！！！")
    private String numOfbindStudent;

    @ConfigFieldAnnotation(confKey = Const.esAverageScore, confName = "设置预估平均分才能开放阅卷", confValue = "0", description = "0是关闭，1是开启", check = "checkEsAverageScore", errorMsg = "必须是0或者1！！！")
    private String esAverageScore;

    @ConfigFieldAnnotation(confKey = Const.classAverage, confName = "设置班级平均分", confValue = "1", description = "仅针对S0,S1,S2,SR，0是隐藏平均分，1是显示平均分（默认）", check = "checkClassAverage", errorMsg = "必须是0或者1！！！")
    private String classAverage;

    @ConfigFieldAnnotation(confKey = Const.gradeAverage, confName = "设置年级平均分", confValue = "1", description = "仅针对S0,S1,S2,SR，0是隐藏平均分，1是显示平均分（默认）", check = "checkGradeAverage", errorMsg = "必须是0或者1！！！")
    private String gradeAverage;

    @ConfigFieldAnnotation(confKey = Const.areaAverage, confName = "设置区平均分", confValue = "1", description = "仅针对S0,S1,S2,SR，0是隐藏平均分，1是显示平均分（默认）", check = "checkAreaAverage", errorMsg = "必须是0或者1！！！")
    private String areaAverage;

    @ConfigFieldAnnotation(confKey = Const.questionGroup, confName = "题组管理默认重置状态为开启，扫描状态完成", confValue = "0", description = "0是阅卷开启关闭状态 关闭，扫描状态 未完成（默认），1是阅卷开启关闭状态 开启，扫描状态 完成", check = "checkQuestionGroup", errorMsg = "必须是0或者1！！！")
    private String questionGroup;

    @ConfigFieldAnnotation(confKey = Const.isStartBigTable, confName = "进度条-是否从reg等大表缓存数据", confValue = "1", description = "主要针对图片服务器不开启缓存,0是不开启缓存，默认开启 图片服务器不开启，1是开启缓存", check = "checkIsStartBigTable", errorMsg = "必须是0或者1！！！")
    private String isStartBigTable;

    @ConfigFieldAnnotation(confKey = Const.showFreePaper, confName = "免费查看各科答题卡", confValue = "0", description = "web端S0是否允许免费查看单科答题卡家长app学情页面单科成绩下是否免费显示答题卡,0是不免费，1是免费", check = "checkShowFreePaper", errorMsg = "必须是0或者1！！！")
    private String showFreePaper;

    @ConfigFieldAnnotation(confKey = Const.defaultFufen, confName = "报表分数源默认显示赋分", confValue = "1", description = "报表分数源默认优先显示赋分还是原始分,0是优先原始分，1是优先赋分", check = "checkDefaultFufen", errorMsg = "必须是0或者1！！！")
    private String defaultFufen;

    @ConfigFieldAnnotation(confKey = Const.yuejuanMode, confName = "阅卷界面的模式", confValue = "1", description = "报表分数源默认优先显示赋分还是原始分,0是优先原始分，1是优先赋分", check = "checkYuejuanMode", errorMsg = "必须是0或者1！！！")
    private String yuejuanMode;

    @ConfigFieldAnnotation(confKey = Const.pizhushow, confName = "阅卷界面批注的显示隐藏", confValue = "", description = "0是隐藏（默认），1是显示", check = "checkPizhushow", errorMsg = "必须是0或者1！！！")
    private String pizhushow;

    @ConfigFieldAnnotation(confKey = Const.showcalculateondetail, confName = "双向细目表的计算显示", confValue = "", description = "0是隐藏，1是显示", check = "checkShowcalculateondetail", errorMsg = "必须是0或者1！！！")
    private String showcalculateondetail;

    @ConfigFieldAnnotation(confKey = "exportCepingThreadCount", confName = "测评报告线程数", confValue = "8", description = "导出测评报告开启的线程数，不填默认是8", check = "checkExportCepingThreadCount", errorMsg = "错误！！！")
    private String exportCepingThreadCount;

    @ConfigFieldAnnotation(confKey = Const.DENGJI_SORT_RULE, confName = "等级排序规则", confValue = "0", description = "定制报表-班级对照中数据按那种规则排序，0是原始排序规则（默认），1是南宁十四中制定排序规则", check = "checkDengjiSortRule", errorMsg = "必须是0或者1！！！")
    private String dengjiSortRule;

    @ConfigFieldAnnotation(confKey = Const.IS_ALLSCHOOL_MANAGER, confName = "有所有学校权限", confValue = "1", description = "添加编辑考点功能权限，0是校级管理员可以使用，1是分配所有学校权限的人才可以使用", check = "checkIsAllSchoolManager", errorMsg = "必须是0或者1！！！")
    private String isAllSchoolManager;

    @ConfigFieldAnnotation(confKey = Const.SCORE_RELEASED, confName = "成绩发布", confValue = "1", description = "数据计算页面成绩默认已发布还是未发布，0是未发布，1是已发布（默认）", check = "checkScoreReleased", errorMsg = "必须是0或者1！！！")
    private String scoreReleased;

    @ConfigFieldAnnotation(confKey = Const.scoreAvgOrRound, confName = "阅卷双评模式分数是否四舍五入", confValue = "1", description = "0是avg，1是round（默认）", check = "checkScoreAvgOrRound", errorMsg = "必须是0或者1！！！")
    private String scoreAvgOrRound;

    @ConfigFieldAnnotation(confKey = Const.baogandaoxiao, confName = "包干到校", confValue = "", description = "0是关闭（默认），1是开启", check = "checkBaogandaoxiao", errorMsg = "必须是0或者1！！！")
    private String baogandaoxiao;

    @ConfigFieldAnnotation(confKey = Const.IS_OPEN_ALL_SCHOOL, confName = "是否开放所有学校权限", confValue = "1", description = "目前只控制C1-试卷讲评中的查找全部学生，0是查找教师学校权限范围内的学生，1是查找全区学生", check = "checkIsOpenAllSchool", errorMsg = "必须是0或者1！！！")
    private String isOpenAllSchool;

    @ConfigFieldAnnotation(confKey = "showRateOfStudentPaper", confName = "学生答题卡是否显示正确率", confValue = "1", description = "当学生成绩显示项设置的是等级时，学生答题卡上小题是否显示正确率：0是不显示，1是显示（默认）", check = "checkShowRateOfStudentPaper", errorMsg = "必须是0或者1！！！")
    private String showRateOfStudentPaper;

    @ConfigFieldAnnotation(confKey = "exportExcel", confName = "报表导出", confValue = "1", description = "控制报表导出按钮显示或隐藏，0是不显示，1是显示（默认）", check = "checkExportExcel", errorMsg = "必须是0或者1！！！")
    private String exportExcel;

    @ConfigFieldAnnotation(confKey = "isshowDatu", confName = "是否查看大图", confValue = "1", description = "控制web端A1报表考号是否有链接点击查看大图和App端成绩单，0是不显示，1是显示（默认）", check = "checkIsshowDatu", errorMsg = "必须是0或者1！！！")
    private String isshowDatu;

    @ConfigFieldAnnotation(confKey = "isshowAllPhone", confName = "是否显示全教师手机号", confValue = "1", description = "控制是否显示全教师手机号，0是不显示，1是显示（默认）", check = "checkIsshowAllPhone", errorMsg = "必须是0或者1！！！")
    private String isshowAllPhone;

    @ConfigFieldAnnotation(confKey = "openKaoHaoJiaoDui", confName = "扫描上传界面是否开启考号校对", confValue = "0", description = "扫描上传界面是否开启考号校对，0是不开启（默认），1是开启", check = "checkOpenKaoHaoJiaoDui", errorMsg = "必须是0或者1！！！")
    private String openKaoHaoJiaoDui;

    @ConfigFieldAnnotation(confKey = "showYouXiuDaAnForStudent", confName = "学生app优秀答案、高分答案导出是否显示学生信息", confValue = "2", description = "学生app优秀答案、高分答案导出是否显示学生信息，0是不显示，1是只显示答题卡，不显示学生信息，2是答题卡和学生信息都显示", check = "checkShowYouXiuDaAnForStudent", errorMsg = "必须是0、1、2 ！！！")
    private String showYouXiuDaAnForStudent;

    @ConfigFieldAnnotation(confKey = "isShowAllOneScoreOneSection", confName = "app端一分一段显示全部或者只显示本段", confValue = "1", description = "家长端app一分一段显示全部或者只显示本段，0是只显示本段，1是全部", check = "checkIsShowAllOneScoreOneSection", errorMsg = "必须是0、1 ！！！")
    private String isShowAllOneScoreOneSection;

    @ConfigFieldAnnotation(confKey = "isEnforceChangePassword", confName = "是否强制修改密码", confValue = "1", description = "web端与app端是否强制修改密码，0是不强制修改，1是强制修改密码", check = "checkIsEnforceChangePassword", errorMsg = "必须是0、1 ！！！")
    private String isEnforceChangePassword;

    @ConfigFieldAnnotation(confKey = "generateSchoolReportThreadCount", confName = "生成质量分析报告【校级报告】多线程数", confValue = "5", description = "用于生成质量分析报告【校级报告】，控制几个线程同时生成文件", check = "checkGenerateSchoolReportThreadCount", errorMsg = "请填写1~5之间的数字！")
    private String generateSchoolReportThreadCount;

    @ConfigFieldAnnotation(confKey = "objectiveSecondaryPositioning", confName = "是否开启客观题二次定位", confValue = "1", description = "是否开启客观题二次定位，1代表开启，0代表关闭，下一次裁切处理时生效。", check = "checkObjectiveSecondaryPositioning", errorMsg = "必须是0、1 ！！！")
    private String objectiveSecondaryPositioning;

    @ConfigFieldAnnotation(confKey = "absentFaultSecondaryPositioning", confName = "是否开启缺考违纪二次定位", confValue = "1", description = "是否开启缺考违纪二次定位，1代表开启，0代表关闭，下一次裁切处理时生效。", check = "checkAbsentFaultSecondaryPositioning", errorMsg = "必须是0、1 ！！！")
    private String absentFaultSecondaryPositioning;

    @ConfigFieldAnnotation(confKey = "chooseSecondaryPositioning", confName = "是否开启选做题二次定位", confValue = "1", description = "是否开启选做题二次定位，1代表开启，0代表关闭，下一次裁切处理时生效。", check = "checkChooseSecondaryPositioning", errorMsg = "必须是0、1 ！！！")
    private String chooseSecondaryPositioning;

    @ConfigFieldAnnotation(confKey = "paintCardNoSecondaryPositioning", confName = "是否开启填涂考号二次定", confValue = "1", description = "是否开启填涂考号二次定位（只外购有效），1代表开启，0代表关闭，下一次裁切处理时生效。", check = "checkPaintCardNoSecondaryPositioning", errorMsg = "必须是0、1 ！！！")
    private String paintCardNoSecondaryPositioning;

    @ConfigFieldAnnotation(confKey = "forceMakeSelfTemplate", confName = "是否强制要求做自己的模板", confValue = "1", description = "是否强制要求做自己的模板，1代表强制要求制作自己的模板，0代表不强制要求制作自己的模板，可以使用母板或自己的模板，修改后扫描端需重新登录才能生效。", check = "checkForceMakeSelfTemplate", errorMsg = "必须是0、1 ！！！")
    private String forceMakeSelfTemplate;

    @ConfigFieldAnnotation(confKey = "xuankezuhejisuan", confName = "学生的选科组合是否计算", confValue = "0", description = "学生的选科组合是否计算（不包括首选科目） 0：计算（默认） 1：不计算。", check = "checkXuankezuhejisuan", errorMsg = "必须是0、1 ！！！")
    private String xuankezuhejisuan;

    @ConfigFieldAnnotation(confKey = "dengjijisuan", confName = "等级是否计算", confValue = "0", description = "等级是否计算  0：不计算（默认） 1：计算。", check = "checkDengjijisuan", errorMsg = "必须是0、1 ！！！")
    private String dengjijisuan;

    @ConfigFieldAnnotation(confKey = "taskexaminationnumcheck", confName = "判分分发是否需要检查考号校对", confValue = "0", description = "判分分发是否需要检查考号校对 0：已识别考号分发（默认）  1：不进行检查。", check = "checkTaskexaminationnumcheck", errorMsg = "必须是0、1 ！！！")
    private String taskexaminationnumcheck;

    @ConfigFieldAnnotation(confKey = "kanbanIsRefurbish", confName = "看板是否实时刷新", confValue = "1", description = "看板是否实时刷新 0：不刷新  1：刷新（默认）.", check = "checkKanbanIsRefurbish", errorMsg = "必须是0、1 ！！！")
    private String kanbanIsRefurbish;

    @ConfigFieldAnnotation(confKey = "isShowReferenceRate", confName = "E3报表是否显示参考率", confValue = "1", description = "E3报表是否显示参考率 0：不显示  1：显示（默认）.", check = "checkIsShowReferenceRate", errorMsg = "必须是0、1 ！！！")
    private String isShowReferenceRate;

    @ConfigFieldAnnotation(confKey = "schoolid", confName = "单点登录学科网时是否配置schoolid", confValue = "999", description = "单点登录学科网时是否配置schoolid, 999：不配置（默认）  schoolid：配置。（如配置的话直接输入学科网分配的schoolid）", check = "checkSchoolid", errorMsg = "必须是整数！！！")
    private String schoolid;

    @ConfigFieldAnnotation(confKey = "isContinue", confName = "是否继续阅卷（剩下题阅过一次）", confValue = "1", description = "是否可以 继续阅卷：0：本题为双评。剩余试卷您已经阅过一次，请通知题组其他老师上线判分，或者继续阅卷（点击确定按钮即可）！1（默认）：本题为双评。剩余试卷您已经一评过，请通知题组其他老师进行二评判分！ ", check = "checkIsContinue", errorMsg = "必须是0、1 ！！！")
    private String isContinue;

    @ConfigFieldAnnotation(confKey = "isShowGroup", confName = "看板是否显示组别", confValue = "1", description = "看板是否实时显示组别 0：不显示  1：显示（默认） ", check = "checkIsShowGroup", errorMsg = "必须是0、1 ！！！")
    private String isShowGroup;

    @ConfigFieldAnnotation(confKey = "isDealOwnSchoolAppeal", confName = "教师能否处理本校的申诉", confValue = "0", description = "教师能否处理本校的申诉 0：不能处理本校申诉，但是可以处理其他学校的申诉（默认）  1：可以处理本校申诉 ", check = "checkIsDealOwnSchoolAppeal", errorMsg = "必须是0、1 ！！！")
    private String isDealOwnSchoolAppeal;

    @ConfigFieldAnnotation(confKey = "errorNum", confName = "限制登录密码错误次数", confValue = "0", description = "密码错误超过设定的次数则禁止登录 0：不限制密码登录错误次数 （默认）", check = "checkIsErrorNum", errorMsg = "必须为整数！！！")
    private String errorNum;

    @ConfigFieldAnnotation(confKey = "lockTime", confName = "限制登录的时长", confValue = "0", description = "密码错误超过设定的次数则禁止登录的时长(单位：分钟) 0：不限制登录 ", check = "checkIsLockTime", errorMsg = "必须为整数！！！")
    private String lockTime;

    @ConfigFieldAnnotation(confKey = "appZhiyuantianbao", confName = "app志愿填报合作方", confValue = "0", description = "app志愿填报合作方配置 0：优志愿 ；1：金榜路", check = "checkAppZhiyuantianbao", errorMsg = "必须为整数！！！")
    private String appZhiyuantianbao;

    @ConfigFieldAnnotation(confKey = "isShowGaoFenDaAn", confName = "是否可以查看高分答案", confValue = "0", description = "教师是否可以查看高分答案 0：可以 1：不可以 ", check = "checkIsShowGaoFenDaAn", errorMsg = "必须为整数！！！")
    private String isShowGaoFenDaAn;
    private static volatile Configuration instance;
    private final ConcurrentHashMap<String, Field> FieldMap = new ConcurrentHashMap<>();
    private sysConfigService sysConfigService = (sysConfigService) ServiceFactory.getObject(new sysConfigServiceImpl());

    public Configuration() {
        init();
    }

    public static final Configuration getInstance() {
        if (instance == null) {
            synchronized (Configuration.class) {
                if (instance == null) {
                    instance = new Configuration();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        getInstance();
    }

    public static void reload() {
        getInstance().init();
    }

    public void init() {
        initFields();
        fillValueToConfig();
    }

    public void initFields() {
        Class<?> objClass = getClass();
        Field[] fields = objClass.getDeclaredFields();
        this.FieldMap.clear();
        for (Field field : fields) {
            field.setAccessible(true);
            ConfigFieldAnnotation info = field.getAnnotation(ConfigFieldAnnotation.class);
            if (info != null) {
                this.FieldMap.put(field.getName(), field);
            }
        }
    }

    public void fillValueToConfig() {
        List<ConfigField> list = this.sysConfigService.getConfig();
        List<ConfigField> addList = new ArrayList<>();
        for (Map.Entry<String, Field> item : this.FieldMap.entrySet()) {
            String key = item.getKey();
            Field field = item.getValue();
            Optional<ConfigField> first = list.stream().filter(config -> {
                return config.getConfKey().equals(key);
            }).findFirst();
            if (first.isPresent()) {
                setField(field, first.get().getConfValue());
            } else {
                ConfigFieldAnnotation info = (ConfigFieldAnnotation) field.getAnnotation(ConfigFieldAnnotation.class);
                setField(field, info.confValue());
                ConfigField config2 = cvtConfigFieldToConfigration(info);
                addList.add(config2);
            }
        }
        insertDb(addList);
    }

    private void insertDb(List<ConfigField> addList) {
        if (addList.size() > 0) {
            for (int i = 0; i < 3; i++) {
                try {
                    this.sysConfigService.insertConfig(addList);
                    return;
                } catch (Exception e) {
                }
            }
        }
    }

    private void setField(Field field, String value) {
        try {
            field.set(this, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private ConfigField cvtConfigFieldToConfigration(ConfigFieldAnnotation info) {
        ConfigField config = new ConfigField();
        config.setConfKey(info.confKey());
        config.setConfName(info.confName());
        config.setConfValue(info.confValue());
        config.setDescription(info.description());
        config.setUpdateDate("");
        config.setUpdateUser(-1L);
        config.setInsertDate("");
        config.setInsertUser(-1L);
        return config;
    }

    public RspMsg check(String key, String value) {
        try {
            if (this.FieldMap.containsKey(key)) {
                Field field = this.FieldMap.get(key);
                ConfigFieldAnnotation info = field.getAnnotation(ConfigFieldAnnotation.class);
                String checkMethod = info.check();
                if (checkMethod != "") {
                    Method method = getClass().getMethod(checkMethod, String.class);
                    boolean flag = ((Boolean) method.invoke(this, value)).booleanValue();
                    if (!flag) {
                        return RspMsg.error(key + ":" + info.errorMsg());
                    }
                }
            }
            return RspMsg.success("恭喜检查通过", null);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(StrUtil.format("K:{}--V:{}", new Object[]{key, value}), e);
            return RspMsg.error("发生未知异常，请重试！");
        }
    }

    public boolean checkType(String type) {
        if (type.equals("1") || type.equals("2")) {
            return true;
        }
        return false;
    }

    public boolean checkQueryusername(String queryusername) {
        if (queryusername.equals("1") || queryusername.equals("2")) {
            return true;
        }
        return false;
    }

    public boolean checkAuthcode(String authcode) {
        return true;
    }

    public boolean checkMoreSchool(String MoreSchool) {
        return true;
    }

    public boolean checkDeleteexam(String deleteexam) {
        return true;
    }

    public boolean checkMoveexam(String moveexam) {
        return true;
    }

    public boolean checkDeleteexammessage(String deleteexammessage) {
        return true;
    }

    public boolean checkHistorytable(String historytable) {
        return true;
    }

    public boolean checkNcount(String ncount) {
        return true;
    }

    public boolean checkLicPath(String licPath) {
        return true;
    }

    public boolean checkLevelclass(String levelclass) {
        return true;
    }

    public boolean checkDeleteNames(String deleteNames) {
        return true;
    }

    public boolean checkInsertIntoDefine(String insertIntoDefine) {
        return true;
    }

    public boolean checkZeroScoreInCount(String zeroScoreInCount) {
        return true;
    }

    public boolean checkObjectItemCorrectClick(String ObjectItemCorrectClick) {
        return true;
    }

    public boolean checkAc01(String Ac01) {
        return true;
    }

    public boolean checkUseImageServer(String useImageServer) {
        if (useImageServer.equals("0") || useImageServer.equals("1") || useImageServer.equals("2")) {
            return true;
        }
        return false;
    }

    public boolean checkIsImageServer(String isImageServer) {
        if (isImageServer.equals("1") || isImageServer.equals("2")) {
            return true;
        }
        return false;
    }

    public boolean checkStuExamImg(String stuExamImg) {
        if ("1".equals(stuExamImg) || "2".equals(stuExamImg) || "3".equals(stuExamImg) || "4".equals(stuExamImg)) {
            return true;
        }
        return false;
    }

    public boolean checkMarkDetail(String markDetail) {
        return true;
    }

    public boolean checkExamInitType(String examInitType) {
        return true;
    }

    public boolean checkZhengshiSubNum(String zhengshiSubNum) {
        return true;
    }

    public boolean checkMarkAveScore(String markAveScore) {
        return true;
    }

    public boolean checKAppQRcode_show(String appQRcodeshow) {
        return true;
    }

    public boolean checkOnline_show(String online_show) {
        return true;
    }

    public boolean checkMarkAutocommit(String markAutocommit) {
        return true;
    }

    public boolean checkExport57_show(String export57_show) {
        return true;
    }

    public boolean checkTK_url(String TK_url) {
        return true;
    }

    public boolean checkFenfaTimeout(String fenfaTimeout) {
        return true;
    }

    public boolean checkDeductScore(String deductScore) {
        return true;
    }

    public boolean checkViewAllReports(String viewAllReports) {
        return true;
    }

    public boolean checkG1QuanquLabel(String g1QuanquLabel) {
        if (g1QuanquLabel.equals("0") || g1QuanquLabel.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkViewRankInfo(String viewRankInfo) {
        if (viewRankInfo.equals("0") || viewRankInfo.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkAstrictLogin(String astrictLogin) {
        return true;
    }

    public boolean checkProgressAllOrPer(String progressAllOrPer) {
        if (progressAllOrPer.equals("0") || progressAllOrPer.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkIsStartDistribute(String isStartDistribute) {
        if (isStartDistribute.equals("0") || isStartDistribute.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkIsJointStuType(String isJointStuType) {
        if (isJointStuType.equals("0") || isJointStuType.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkIsShowG1Quanqu(String isShowG1Quanqu) {
        if (isShowG1Quanqu.equals("0") || isShowG1Quanqu.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkUserposition(String userposition) {
        return true;
    }

    public boolean checkReportpermission(String reportpermission) {
        return true;
    }

    public boolean checkReportpermissionclass(String reportpermissionclass) {
        return true;
    }

    public boolean checkMaxpanfenff(String maxpanfenff) {
        return true;
    }

    public boolean checkMinpanfenff(String minpanfenff) {
        return true;
    }

    public boolean checkJudgerule(String judgerule) {
        return true;
    }

    public boolean checkVersion_date(String version_date) {
        return true;
    }

    public boolean checkDay(String day) {
        return true;
    }

    public boolean checkTestingcentredis(String testingcentredis) {
        if (testingcentredis.equals("0") || testingcentredis.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkL2Allsubpassrait(String L2Allsubpassrait) {
        if (L2Allsubpassrait.equals("0") || L2Allsubpassrait.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkViewRankOfScoreInfo(String viewRankOfScoreInfo) {
        if (viewRankOfScoreInfo.equals("1") || viewRankOfScoreInfo.equals("2") || viewRankOfScoreInfo.equals("3")) {
            return true;
        }
        return false;
    }

    public boolean checkIsInheritMotherBySelf(String IsInheritMotherBySelf) {
        if (IsInheritMotherBySelf.equals("0") || IsInheritMotherBySelf.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkIsMultipleTeachers(String isMultipleTeachers) {
        if (isMultipleTeachers.equals("0") || isMultipleTeachers.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkNumOfbindStudent(String numOfbindStudent) {
        return true;
    }

    public boolean checkEsAverageScore(String esAverageScore) {
        if (esAverageScore.equals("0") || esAverageScore.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkClassAverage(String classAverage) {
        if (classAverage.equals("0") || classAverage.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkGradeAverage(String gradeAverage) {
        if (gradeAverage.equals("0") || gradeAverage.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkAreaAverage(String areaAverage) {
        if (areaAverage.equals("0") || areaAverage.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkQuestionGroup(String questionGroup) {
        if (questionGroup.equals("0") || questionGroup.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkIsStartBigTable(String isStartBigTable) {
        if (isStartBigTable.equals("0") || isStartBigTable.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkShowFreePaper(String showFreePaper) {
        if (showFreePaper.equals("0") || showFreePaper.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkDefaultFufen(String defaultFufen) {
        if (defaultFufen.equals("0") || defaultFufen.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkYuejuanMode(String yuejuanMode) {
        if (yuejuanMode.equals("0") || yuejuanMode.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkPizhushow(String pizhushow) {
        return true;
    }

    public boolean checkShowcalculateondetail(String showcalculateondetail) {
        return true;
    }

    public boolean checkExportCepingThreadCount(String exportCepingThreadCount) {
        return true;
    }

    public boolean checkDengjiSortRule(String dengjiSortRule) {
        if (dengjiSortRule.equals("0") || dengjiSortRule.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkIsAllSchoolManager(String isAllSchoolManager) {
        if (isAllSchoolManager.equals("0") || isAllSchoolManager.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkScoreReleased(String scoreReleased) {
        if (scoreReleased.equals("0") || scoreReleased.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkScoreAvgOrRound(String scoreAvgOrRound) {
        if (scoreAvgOrRound.equals("0") || scoreAvgOrRound.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkBaogandaoxiao(String baogandaoxiao) {
        return true;
    }

    public boolean checkIsOpenAllSchool(String isOpenAllSchool) {
        if (isOpenAllSchool.equals("0") || isOpenAllSchool.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean checkShowRateOfStudentPaper(String showRateOfStudentPaper) {
        if ("0".equals(showRateOfStudentPaper) || "1".equals(showRateOfStudentPaper)) {
            return true;
        }
        return false;
    }

    public boolean checkExportExcel(String exportExcel) {
        if ("0".equals(exportExcel) || "1".equals(exportExcel)) {
            return true;
        }
        return false;
    }

    public boolean checkIsshowDatu(String isshowDatu) {
        if ("0".equals(isshowDatu) || "1".equals(isshowDatu)) {
            return true;
        }
        return false;
    }

    public boolean checkIsshowAllPhone(String isshowAllPhone) {
        if ("0".equals(isshowAllPhone) || "1".equals(isshowAllPhone)) {
            return true;
        }
        return false;
    }

    public boolean checkOpenKaoHaoJiaoDui(String openKaoHaoJiaoDui) {
        if ("0".equals(openKaoHaoJiaoDui) || "1".equals(openKaoHaoJiaoDui)) {
            return true;
        }
        return false;
    }

    public boolean checkShowYouXiuDaAnForStudent(String showYouXiuDaAnForStudent) {
        boolean z = -1;
        switch (showYouXiuDaAnForStudent.hashCode()) {
            case 48:
                if (showYouXiuDaAnForStudent.equals("0")) {
                    z = false;
                    break;
                }
                break;
            case 49:
                if (showYouXiuDaAnForStudent.equals("1")) {
                    z = true;
                    break;
                }
                break;
            case 50:
                if (showYouXiuDaAnForStudent.equals("2")) {
                    z = 2;
                    break;
                }
                break;
        }
        switch (z) {
            case Const.clipError_failure /* 0 */:
            case true:
            case true:
                return true;
            default:
                return false;
        }
    }

    public boolean checkIsShowAllOneScoreOneSection(String isShowAllOneScoreOneSection) {
        if ("0".equals(isShowAllOneScoreOneSection) || "1".equals(isShowAllOneScoreOneSection)) {
            return true;
        }
        return false;
    }

    public boolean checkIsEnforceChangePassword(String isEnforceChangePassword) {
        if ("0".equals(isEnforceChangePassword) || "1".equals(isEnforceChangePassword)) {
            return true;
        }
        return false;
    }

    public boolean checkGenerateSchoolReportThreadCount(String generateSchoolReportThreadCount) {
        return true;
    }

    public boolean checkObjectiveSecondaryPositioning(String objectiveSecondaryPositioning) {
        if ("0".equals(objectiveSecondaryPositioning) || "1".equals(objectiveSecondaryPositioning)) {
            return true;
        }
        return false;
    }

    public boolean checkAbsentFaultSecondaryPositioning(String absentFaultSecondaryPositioning) {
        if ("0".equals(absentFaultSecondaryPositioning) || "1".equals(absentFaultSecondaryPositioning)) {
            return true;
        }
        return false;
    }

    public boolean checkChooseSecondaryPositioning(String chooseSecondaryPositioning) {
        if ("0".equals(chooseSecondaryPositioning) || "1".equals(chooseSecondaryPositioning)) {
            return true;
        }
        return false;
    }

    public boolean checkPaintCardNoSecondaryPositioning(String paintCardNoSecondaryPositioning) {
        if ("0".equals(paintCardNoSecondaryPositioning) || "1".equals(paintCardNoSecondaryPositioning)) {
            return true;
        }
        return false;
    }

    public boolean checkForceMakeSelfTemplate(String forceMakeSelfTemplate) {
        if ("0".equals(forceMakeSelfTemplate) || "1".equals(forceMakeSelfTemplate)) {
            return true;
        }
        return false;
    }

    public boolean checkXuankezuhejisuan(String xuankezuhejisuan) {
        if ("0".equals(xuankezuhejisuan) || "1".equals(xuankezuhejisuan)) {
            return true;
        }
        return false;
    }

    public boolean checkDengjijisuan(String dengjijisuan) {
        if ("0".equals(dengjijisuan) || "1".equals(dengjijisuan)) {
            return true;
        }
        return false;
    }

    public boolean checkTaskexaminationnumcheck(String taskexaminationnumcheck) {
        if ("0".equals(taskexaminationnumcheck) || "1".equals(taskexaminationnumcheck)) {
            return true;
        }
        return false;
    }

    public boolean checkSchoolid(String schoolid) {
        Pattern pattern = Pattern.compile("^[0-9]*[1-9][0-9]*$");
        if (pattern.matcher(schoolid).matches()) {
            return true;
        }
        return false;
    }

    public boolean checkKanbanIsRefurbish(String kanbanIsRefurbish) {
        if ("0".equals(kanbanIsRefurbish) || "1".equals(kanbanIsRefurbish)) {
            return true;
        }
        return false;
    }

    public boolean checkIsShowReferenceRate(String isShowReferenceRate) {
        if ("0".equals(isShowReferenceRate) || "1".equals(isShowReferenceRate)) {
            return true;
        }
        return false;
    }

    public boolean checkIsContinue(String isContinue) {
        if ("0".equals(isContinue) || "1".equals(isContinue)) {
            return true;
        }
        return false;
    }

    public boolean checkIsShowGroup(String isShowGroup) {
        if ("0".equals(isShowGroup) || "1".equals(isShowGroup)) {
            return true;
        }
        return false;
    }

    public boolean checkIsDealOwnSchoolAppeal(String isDealOwnSchoolAppeal) {
        if ("0".equals(isDealOwnSchoolAppeal) || "1".equals(isDealOwnSchoolAppeal)) {
            return true;
        }
        return false;
    }

    public boolean checkIsErrorNum(String errorNum) {
        if (errorNum.matches("[0-9]+")) {
            return true;
        }
        return false;
    }

    public boolean checkIsLockTime(String lockTime) {
        if (this.errorNum.matches("[0-9]+")) {
            return true;
        }
        return false;
    }

    public boolean checkAppZhiyuantianbao(String appZhiyuantianbao) {
        if (this.errorNum.matches("[0-9]+")) {
            return true;
        }
        return false;
    }

    public boolean checkIsShowGaoFenDaAn(String isShowGaoFenDaAn) {
        if ("0".equals(isShowGaoFenDaAn) || "1".equals(isShowGaoFenDaAn)) {
            return true;
        }
        return false;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQueryusername() {
        return this.queryusername;
    }

    public void setQueryusername(String queryusername) {
        this.queryusername = queryusername;
    }

    public String getAuthcode() {
        return this.authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }

    public String getMoreSchool() {
        return this.MoreSchool;
    }

    public void setMoreSchool(String moreSchool) {
        this.MoreSchool = moreSchool;
    }

    public String getDeleteexam() {
        return this.deleteexam;
    }

    public void setDeleteexam(String deleteexam) {
        this.deleteexam = deleteexam;
    }

    public String getMoveexam() {
        return this.moveexam;
    }

    public void setMoveexam(String moveexam) {
        this.moveexam = moveexam;
    }

    public String getDeleteexammessage() {
        return this.deleteexammessage;
    }

    public void setDeleteexammessage(String deleteexammessage) {
        this.deleteexammessage = deleteexammessage;
    }

    public String getHistorytable() {
        return this.historytable;
    }

    public void setHistorytable(String historytable) {
        this.historytable = historytable;
    }

    public String getNcount() {
        return this.ncount;
    }

    public void setNcount(String ncount) {
        this.ncount = ncount;
    }

    public String getLicPath() {
        return this.licPath;
    }

    public void setLicPath(String licPath) {
        this.licPath = licPath;
    }

    public String getLevelclass() {
        return this.levelclass;
    }

    public void setLevelclass(String levelclass) {
        this.levelclass = levelclass;
    }

    public String getDeleteNames() {
        return this.deleteNames;
    }

    public void setDeleteNames(String deleteNames) {
        this.deleteNames = deleteNames;
    }

    public String getInsertIntoDefine() {
        return this.insertIntoDefine;
    }

    public void setInsertIntoDefine(String insertIntoDefine) {
        this.insertIntoDefine = insertIntoDefine;
    }

    public String getZeroScoreInCount() {
        return this.zeroScoreInCount;
    }

    public void setZeroScoreInCount(String zeroScoreInCount) {
        this.zeroScoreInCount = zeroScoreInCount;
    }

    public String getObjectItemCorrectClick() {
        return this.ObjectItemCorrectClick;
    }

    public void setObjectItemCorrectClick(String objectItemCorrectClick) {
        this.ObjectItemCorrectClick = objectItemCorrectClick;
    }

    public String getAc01() {
        return this.Ac01;
    }

    public void setAc01(String ac01) {
        this.Ac01 = ac01;
    }

    public String getUseImageServer() {
        return this.useImageServer;
    }

    public void setUseImageServer(String useImageServer) {
        this.useImageServer = useImageServer;
    }

    public String getIsImageServer() {
        return this.isImageServer;
    }

    public void setIsImageServer(String isImageServer) {
        this.isImageServer = isImageServer;
    }

    public String getStuExamImg() {
        return this.stuExamImg;
    }

    public void setStuExamImg(String stuExamImg) {
        this.stuExamImg = stuExamImg;
    }

    public String getMarkDetail() {
        return this.markDetail;
    }

    public void setMarkDetail(String markDetail) {
        this.markDetail = markDetail;
    }

    public String getExamInitType() {
        return this.examInitType;
    }

    public void setExamInitType(String examInitType) {
        this.examInitType = examInitType;
    }

    public String getZhengshiSubNum() {
        return this.zhengshiSubNum;
    }

    public void setZhengshiSubNum(String zhengshiSubNum) {
        this.zhengshiSubNum = zhengshiSubNum;
    }

    public String getMarkAveScore() {
        return this.markAveScore;
    }

    public void setMarkAveScore(String markAveScore) {
        this.markAveScore = markAveScore;
    }

    public String getAppQRcode_show() {
        return this.appQRcode_show;
    }

    public void setAppQRcode_show(String appQRcode_show) {
        this.appQRcode_show = appQRcode_show;
    }

    public String getOnline_show() {
        return this.online_show;
    }

    public void setOnline_show(String online_show) {
        this.online_show = online_show;
    }

    public String getMarkAutocommit() {
        return this.markAutocommit;
    }

    public void setMarkAutocommit(String markAutocommit) {
        this.markAutocommit = markAutocommit;
    }

    public String getExport57_show() {
        return this.export57_show;
    }

    public void setExport57_show(String export57_show) {
        this.export57_show = export57_show;
    }

    public String getTK_url() {
        return this.TK_url;
    }

    public void setTK_url(String TK_url) {
        this.TK_url = TK_url;
    }

    public String getFenfaTimeout() {
        return this.fenfaTimeout;
    }

    public void setFenfaTimeout(String fenfaTimeout) {
        this.fenfaTimeout = fenfaTimeout;
    }

    public String getDeductScore() {
        return this.deductScore;
    }

    public void setDeductScore(String deductScore) {
        this.deductScore = deductScore;
    }

    public String getViewAllReports() {
        return this.viewAllReports;
    }

    public void setViewAllReports(String viewAllReports) {
        this.viewAllReports = viewAllReports;
    }

    public String getG1QuanquLabel() {
        return this.g1QuanquLabel;
    }

    public void setG1QuanquLabel(String g1QuanquLabel) {
        this.g1QuanquLabel = g1QuanquLabel;
    }

    public String getViewRankInfo() {
        return this.viewRankInfo;
    }

    public void setViewRankInfo(String viewRankInfo) {
        this.viewRankInfo = viewRankInfo;
    }

    public String getAstrictLogin() {
        return this.astrictLogin;
    }

    public void setAstrictLogin(String astrictLogin) {
        this.astrictLogin = astrictLogin;
    }

    public String getProgressAllOrPer() {
        return this.progressAllOrPer;
    }

    public void setProgressAllOrPer(String progressAllOrPer) {
        this.progressAllOrPer = progressAllOrPer;
    }

    public String getIsStartDistribute() {
        return this.isStartDistribute;
    }

    public void setIsStartDistribute(String isStartDistribute) {
        this.isStartDistribute = isStartDistribute;
    }

    public String getIsJointStuType() {
        return this.isJointStuType;
    }

    public void setIsJointStuType(String isJointStuType) {
        this.isJointStuType = isJointStuType;
    }

    public String getIsShowG1Quanqu() {
        return this.isShowG1Quanqu;
    }

    public void setIsShowG1Quanqu(String isShowG1Quanqu) {
        this.isShowG1Quanqu = isShowG1Quanqu;
    }

    public String getUserposition() {
        return this.userposition;
    }

    public void setUserposition(String userposition) {
        this.userposition = userposition;
    }

    public String getReportpermission() {
        return this.reportpermission;
    }

    public void setReportpermission(String reportpermission) {
        this.reportpermission = reportpermission;
    }

    public String getReportpermissionclass() {
        return this.reportpermissionclass;
    }

    public void setReportpermissionclass(String reportpermissionclass) {
        this.reportpermissionclass = reportpermissionclass;
    }

    public String getMaxpanfenff() {
        return this.maxpanfenff;
    }

    public void setMaxpanfenff(String maxpanfenff) {
        this.maxpanfenff = maxpanfenff;
    }

    public String getMinpanfenff() {
        return this.minpanfenff;
    }

    public void setMinpanfenff(String minpanfenff) {
        this.minpanfenff = minpanfenff;
    }

    public String getJudgerule() {
        return this.judgerule;
    }

    public void setJudgerule(String judgerule) {
        this.judgerule = judgerule;
    }

    public String getVersion_date() {
        return this.version_date;
    }

    public void setVersion_date(String version_date) {
        this.version_date = version_date;
    }

    public String getDay() {
        return this.day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTestingcentredis() {
        return this.testingcentredis;
    }

    public void setTestingcentredis(String testingcentredis) {
        this.testingcentredis = testingcentredis;
    }

    public String getL2Allsubpassrait() {
        return this.L2Allsubpassrait;
    }

    public void setL2Allsubpassrait(String l2Allsubpassrait) {
        this.L2Allsubpassrait = l2Allsubpassrait;
    }

    public String getViewRankOfScoreInfo() {
        return this.viewRankOfScoreInfo;
    }

    public void setViewRankOfScoreInfo(String viewRankOfScoreInfo) {
        this.viewRankOfScoreInfo = viewRankOfScoreInfo;
    }

    public String getIsInheritMotherBySelf() {
        return this.IsInheritMotherBySelf;
    }

    public void setIsInheritMotherBySelf(String isInheritMotherBySelf) {
        this.IsInheritMotherBySelf = isInheritMotherBySelf;
    }

    public String getIsMultipleTeachers() {
        return this.isMultipleTeachers;
    }

    public void setIsMultipleTeachers(String isMultipleTeachers) {
        this.isMultipleTeachers = isMultipleTeachers;
    }

    public String getNumOfbindStudent() {
        return this.numOfbindStudent;
    }

    public void setNumOfbindStudent(String numOfbindStudent) {
        this.numOfbindStudent = numOfbindStudent;
    }

    public String getEsAverageScore() {
        return this.esAverageScore;
    }

    public void setEsAverageScore(String esAverageScore) {
        this.esAverageScore = esAverageScore;
    }

    public String getClassAverage() {
        return this.classAverage;
    }

    public void setClassAverage(String classAverage) {
        this.classAverage = classAverage;
    }

    public String getGradeAverage() {
        return this.gradeAverage;
    }

    public void setGradeAverage(String gradeAverage) {
        this.gradeAverage = gradeAverage;
    }

    public String getAreaAverage() {
        return this.areaAverage;
    }

    public void setAreaAverage(String areaAverage) {
        this.areaAverage = areaAverage;
    }

    public String getQuestionGroup() {
        return this.questionGroup;
    }

    public void setQuestionGroup(String questionGroup) {
        this.questionGroup = questionGroup;
    }

    public String getIsStartBigTable() {
        return this.isStartBigTable;
    }

    public void setIsStartBigTable(String isStartBigTable) {
        this.isStartBigTable = isStartBigTable;
    }

    public String getShowFreePaper() {
        return this.showFreePaper;
    }

    public void setShowFreePaper(String showFreePaper) {
        this.showFreePaper = showFreePaper;
    }

    public String getDefaultFufen() {
        return this.defaultFufen;
    }

    public void setDefaultFufen(String defaultFufen) {
        this.defaultFufen = defaultFufen;
    }

    public String getYuejuanMode() {
        return this.yuejuanMode;
    }

    public void setYuejuanMode(String yuejuanMode) {
        this.yuejuanMode = yuejuanMode;
    }

    public String getPizhushow() {
        return this.pizhushow;
    }

    public void setPizhushow(String pizhushow) {
        this.pizhushow = pizhushow;
    }

    public String getShowcalculateondetail() {
        return this.showcalculateondetail;
    }

    public void setShowcalculateondetail(String showcalculateondetail) {
        this.showcalculateondetail = showcalculateondetail;
    }

    public String getExportCepingThreadCount() {
        return this.exportCepingThreadCount;
    }

    public void setExportCepingThreadCount(String exportCepingThreadCount) {
        this.exportCepingThreadCount = exportCepingThreadCount;
    }

    public String getDengjiSortRule() {
        return this.dengjiSortRule;
    }

    public void setDengjiSortRule(String dengjiSortRule) {
        this.dengjiSortRule = dengjiSortRule;
    }

    public String getIsAllSchoolManager() {
        return this.isAllSchoolManager;
    }

    public void setIsAllSchoolManager(String isAllSchoolManager) {
        this.isAllSchoolManager = isAllSchoolManager;
    }

    public String getScoreReleased() {
        return this.scoreReleased;
    }

    public void setScoreReleased(String scoreReleased) {
        this.scoreReleased = scoreReleased;
    }

    public String getScoreAvgOrRound() {
        return this.scoreAvgOrRound;
    }

    public void setScoreAvgOrRound(String scoreAvgOrRound) {
        this.scoreAvgOrRound = scoreAvgOrRound;
    }

    public String getBaogandaoxiao() {
        return this.baogandaoxiao;
    }

    public void setBaogandaoxiao(String baogandaoxiao) {
        this.baogandaoxiao = baogandaoxiao;
    }

    public String getIsOpenAllSchool() {
        return this.isOpenAllSchool;
    }

    public void setIsOpenAllSchool(String isOpenAllSchool) {
        this.isOpenAllSchool = isOpenAllSchool;
    }

    public String getShowRateOfStudentPaper() {
        return this.showRateOfStudentPaper;
    }

    public void setShowRateOfStudentPaper(String showRateOfStudentPaper) {
        this.showRateOfStudentPaper = showRateOfStudentPaper;
    }

    public String getExportExcel() {
        return this.exportExcel;
    }

    public void setExportExcel(String exportExcel) {
        this.exportExcel = exportExcel;
    }

    public String getIsshowDatu() {
        return this.isshowDatu;
    }

    public void setIsshowDatu(String isshowDatu) {
        this.isshowDatu = isshowDatu;
    }

    public String getIsshowAllPhone() {
        return this.isshowAllPhone;
    }

    public void setIsshowAllPhone(String isshowAllPhone) {
        this.isshowAllPhone = isshowAllPhone;
    }

    public String getOpenKaoHaoJiaoDui() {
        return this.openKaoHaoJiaoDui;
    }

    public void setOpenKaoHaoJiaoDui(String openKaoHaoJiaoDui) {
        this.openKaoHaoJiaoDui = openKaoHaoJiaoDui;
    }

    public String getShowYouXiuDaAnForStudent() {
        return this.showYouXiuDaAnForStudent;
    }

    public void setShowYouXiuDaAnForStudent(String showYouXiuDaAnForStudent) {
        this.showYouXiuDaAnForStudent = showYouXiuDaAnForStudent;
    }

    public String getIsShowAllOneScoreOneSection() {
        return this.isShowAllOneScoreOneSection;
    }

    public void setIsShowAllOneScoreOneSection(String isShowAllOneScoreOneSection) {
        this.isShowAllOneScoreOneSection = isShowAllOneScoreOneSection;
    }

    public String getIsEnforceChangePassword() {
        return this.isEnforceChangePassword;
    }

    public void setIsEnforceChangePassword(String isEnforceChangePassword) {
        this.isEnforceChangePassword = isEnforceChangePassword;
    }

    public String getGenerateSchoolReportThreadCount() {
        return this.generateSchoolReportThreadCount;
    }

    public void setGenerateSchoolReportThreadCount(String generateSchoolReportThreadCount) {
        this.generateSchoolReportThreadCount = generateSchoolReportThreadCount;
    }

    public String getObjectiveSecondaryPositioning() {
        return this.objectiveSecondaryPositioning;
    }

    public void setObjectiveSecondaryPositioning(String objectiveSecondaryPositioning) {
        this.objectiveSecondaryPositioning = objectiveSecondaryPositioning;
    }

    public String getForceMakeSelfTemplate() {
        return this.forceMakeSelfTemplate;
    }

    public void setForceMakeSelfTemplate(String forceMakeSelfTemplate) {
        this.forceMakeSelfTemplate = forceMakeSelfTemplate;
    }

    public String getAbsentFaultSecondaryPositioning() {
        return this.absentFaultSecondaryPositioning;
    }

    public void setAbsentFaultSecondaryPositioning(String absentFaultSecondaryPositioning) {
        this.absentFaultSecondaryPositioning = absentFaultSecondaryPositioning;
    }

    public String getChooseSecondaryPositioning() {
        return this.chooseSecondaryPositioning;
    }

    public void setChooseSecondaryPositioning(String chooseSecondaryPositioning) {
        this.chooseSecondaryPositioning = chooseSecondaryPositioning;
    }

    public String getXuankezuhejisuan() {
        return this.xuankezuhejisuan;
    }

    public void setXuankezuhejisuan(String xuankezuhejisuan) {
        this.xuankezuhejisuan = xuankezuhejisuan;
    }

    public String getDengjijisuan() {
        return this.dengjijisuan;
    }

    public void setDengjijisuan(String dengjijisuan) {
        this.dengjijisuan = dengjijisuan;
    }

    public String getPaintCardNoSecondaryPositioning() {
        return this.paintCardNoSecondaryPositioning;
    }

    public void setPaintCardNoSecondaryPositioning(String paintCardNoSecondaryPositioning) {
        this.paintCardNoSecondaryPositioning = paintCardNoSecondaryPositioning;
    }

    public String getTaskexaminationnumcheck() {
        return this.taskexaminationnumcheck;
    }

    public void setTaskexaminationnumcheck(String taskexaminationnumcheck) {
        this.taskexaminationnumcheck = taskexaminationnumcheck;
    }

    public String getSchoolid() {
        return this.schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getKanbanIsRefurbish() {
        return this.kanbanIsRefurbish;
    }

    public void setKanbanIsRefurbish(String kanbanIsRefurbish) {
        this.kanbanIsRefurbish = kanbanIsRefurbish;
    }

    public String getIsShowReferenceRate() {
        return this.isShowReferenceRate;
    }

    public void setIsShowReferenceRate(String isShowReferenceRate) {
        this.isShowReferenceRate = isShowReferenceRate;
    }

    public String getIsContinue() {
        return this.isContinue;
    }

    public void setIsContinue(String isContinue) {
        this.isContinue = isContinue;
    }

    public String getIsShowGroup() {
        return this.isShowGroup;
    }

    public void setIsShowGroup(String isShowGroup) {
        this.isShowGroup = isShowGroup;
    }

    public String getIsDealOwnSchoolAppeal() {
        return this.isDealOwnSchoolAppeal;
    }

    public void setIsDealOwnSchoolAppeal(String isDealOwnSchoolAppeal) {
        this.isDealOwnSchoolAppeal = isDealOwnSchoolAppeal;
    }

    public String getErrorNum() {
        return this.errorNum;
    }

    public void setErrorNum(String errorNum) {
        this.errorNum = errorNum;
    }

    public String getLockTime() {
        return this.lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public String getAppZhiyuantianbao() {
        return this.appZhiyuantianbao;
    }

    public void setAppZhiyuantianbao(String appZhiyuantianbao) {
        this.appZhiyuantianbao = appZhiyuantianbao;
    }

    public String getIsShowGaoFenDaAn() {
        return this.isShowGaoFenDaAn;
    }

    public void setIsShowGaoFenDaAn(String isShowGaoFenDaAn) {
        this.isShowGaoFenDaAn = isShowGaoFenDaAn;
    }
}
