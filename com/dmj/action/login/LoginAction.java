package com.dmj.action.login;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.dmj.action.base.BaseAction;
import com.dmj.auth.CreateFile;
import com.dmj.auth.bean.License;
import com.dmj.auth.util.Base64;
import com.dmj.daoimpl.userManagement.UserDAOImpl;
import com.dmj.domain.OnlineUser;
import com.dmj.domain.Resource;
import com.dmj.domain.Role;
import com.dmj.domain.SongDaSSO;
import com.dmj.domain.SongDaSSOUser;
import com.dmj.domain.Student;
import com.dmj.domain.User;
import com.dmj.domain.Userparent;
import com.dmj.service.appHBuilder.AppLogionService;
import com.dmj.service.astrict.AstrictService;
import com.dmj.service.permission.PermissionService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.service.systemManagement.TeacherManageService;
import com.dmj.service.teachingInformation.StudentService;
import com.dmj.service.userManagement.UserService;
import com.dmj.serviceimpl.appHBuilder.AppLogionServiceImpl;
import com.dmj.serviceimpl.astrict.AstrictServiceImpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.serviceimpl.systemManagement.TeacherManageServiceimpl;
import com.dmj.serviceimpl.teachingInformation.StudentServiceimpl;
import com.dmj.serviceimpl.userManagement.UserServiceImpl;
import com.dmj.util.CommonUtil;
import com.dmj.util.Conffig;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.HttpRequestUtil1;
import com.dmj.util.SecretUtil;
import com.dmj.util.SfHelper;
import com.dmj.util.SongDaSSOUtil;
import com.dmj.util.StaticClassResources;
import com.dmj.util.Util;
import com.dmj.util.WebUtil;
import com.dmj.util.config.Configuration;
import com.dmj.util.msg.RspMsg;
import com.dmj.util.singleLogin.SingleLoginUtil;
import com.dmj.util.singleLogin.ZkhySingleLoginUtil;
import com.opensymphony.xwork2.ActionContext;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.jasig.cas.client.validation.Assertion;

/* loaded from: LoginAction.class */
public class LoginAction extends BaseAction {
    public static final String Info_error = "用户名和密码不匹配，请检查！";
    public static final String Ocs_error = "支付系统验证失败，请联系供应商！";
    public static final String license_error = "license过期，请联系供应商！";
    public static final String connfig_error = "请更换最新的conff文件";
    public static final String hession_error = "当前系统连接失败！请于供应商联系！";
    private String info;
    private String mark;
    private String code;
    private String sessionCode;
    private String authcode_val;
    private String idStr;
    private String id;
    private String logtype;
    private String name;
    private String email;
    private String token;
    private String timestamp;
    private static final SfHelper sfHelper = new SfHelper();
    private User user = new User();
    private int count = 0;
    private int pageSize = 100;
    private int pagestart = 0;
    private int currenpage = 1;
    private int signle = 9;
    Logger log = Logger.getLogger(getClass());
    UserService userService = (UserService) ServiceFactory.getObject(new UserServiceImpl());
    public TeacherManageService tt = (TeacherManageService) ServiceFactory.getObject(new TeacherManageServiceimpl());
    SystemService sysService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    private StudentService sgs = (StudentService) ServiceFactory.getObject(new StudentServiceimpl());
    private AppLogionService appLogin = (AppLogionService) ServiceFactory.getObject(new AppLogionServiceImpl());
    private AstrictService astrictService = (AstrictService) ServiceFactory.getObject(new AstrictServiceImpl());
    UserDAOImpl userDao = new UserDAOImpl();

    public String doDefault() throws Exception {
        return "input";
    }

    public String infoLogin() throws Exception {
        User uu;
        String num;
        String Id = this.request.getParameter("Id");
        String flagNum = "1";
        String name = new CommonUtil().getLoginUserName(this.request);
        HttpSession session = this.request.getSession(true);
        this.request.getParameter("logtype");
        if ("dmj".equals(name)) {
            num = "-2";
            flagNum = "3";
        } else {
            new User();
            if ("P".equals(session.getAttribute("logType"))) {
                uu = (User) session.getAttribute(Const.LOGIN_PARENTUSER);
            } else {
                uu = (User) session.getAttribute(Const.LOGIN_USER);
            }
            num = uu.getId();
            String user = uu.getId();
            if (Id.equals("3")) {
                String flag = this.userService.ifYueJuanPrivileged(user, "3502");
                if (flag.equals("T")) {
                    flagNum = "3";
                }
            }
        }
        String sortnames = this.request.getParameter("sortname");
        URLDecoder.decode(sortnames, "UTF-8");
        String sortname = this.userService.resname(Id);
        this.request.setAttribute("tabId", Id);
        session.setAttribute("sortname", sortname);
        this.mark = (String) session.getAttribute(Const.SYSTEM_TYPE);
        if (Id.equals("6")) {
            List list2 = this.userService.rolelist22(num, Id, this.mark, null);
            session.setAttribute("list2", list2);
            if ("P".equals(session.getAttribute("logType"))) {
                return "info6";
            }
            return "report";
        }
        if (Id.equals("3")) {
            List list22 = this.userService.rolelist22(num, Id, this.mark, null);
            session.setAttribute("list2", list22);
            session.setAttribute("flagNum", flagNum);
            if ("P".equals(session.getAttribute("logType"))) {
                return "info5";
            }
            return "info";
        }
        if (Id.equals("5")) {
            List list23 = this.userService.rolelist22(num, Id, this.mark, null);
            session.setAttribute("list2", list23);
            if ("P".equals(session.getAttribute("logType"))) {
                return "info5";
            }
            return "info3";
        }
        if (Id.equals("1") || "11".equals(Id)) {
            List list24 = this.userService.rolelist22(num, Id, this.mark, null);
            session.setAttribute("list2", list24);
            if ("P".equals(session.getAttribute("logType"))) {
                return "info5";
            }
            return "info4";
        }
        if (Id.equals("7")) {
            List list25 = this.userService.rolelist22(num, Id, "3", null);
            session.setAttribute("list2", list25);
            if ("P".equals(session.getAttribute("logType"))) {
                return "info5";
            }
            return "info7";
        }
        if (Id.equals("8")) {
            List list26 = this.userService.rolelist22(num, Id, this.mark, null);
            session.setAttribute("list2", list26);
            if ("P".equals(session.getAttribute("logType"))) {
                return "info9";
            }
            return "info8";
        }
        Map<String, String> list1 = this.userService.rolelist11(num, Id, this.mark);
        session.setAttribute("list1", list1);
        if ("P".equals(session.getAttribute("logType"))) {
            return "info10";
        }
        return "info2";
    }

    public void showYueJuan() {
        HttpSession session = this.request.getSession(true);
        User u = (User) session.getAttribute(Const.LOGIN_USER);
        String user = u.getId();
        String flag = this.userService.ifYueJuanPrivileged(user, "3502");
        this.out.write(flag);
    }

    public void getResource() throws Exception {
        List<String> now;
        List<Resource> list2;
        this.request.getParameter("Id");
        String name = new CommonUtil().getLoginUserName(this.request);
        this.userService.username(name);
        this.request.getParameter("sortname");
        HttpSession session = this.request.getSession(true);
        session.getServletContext();
        User u = (User) session.getAttribute(Const.LOGIN_USER);
        User userParent = (User) session.getAttribute(Const.LOGIN_PARENTUSER);
        String pnum = this.request.getParameter("pnum");
        this.mark = (String) session.getAttribute(Const.SYSTEM_TYPE);
        new ArrayList();
        if (null != this.idStr && !this.idStr.equals("")) {
            now = Arrays.asList(this.idStr.split(Const.STRING_SEPERATOR));
        } else {
            now = new ArrayList<>();
        }
        if ("P".equals(session.getAttribute("logType"))) {
            list2 = this.userService.rolelist22(userParent.getId(), pnum, this.mark, now);
        } else {
            list2 = this.userService.rolelist22(u.getId(), pnum, this.mark, now);
        }
        session.setAttribute("num", "60107");
        String baogandaoxiao = Configuration.getInstance().getBaogandaoxiao();
        if (!"1".equals(baogandaoxiao)) {
            list2 = (List) list2.stream().filter(res -> {
                return !"3510".equals(res.getNum());
            }).collect(Collectors.toList());
        }
        String esAverageScore = Configuration.getInstance().getEsAverageScore();
        if (!"1".equals(esAverageScore)) {
            list2 = (List) list2.stream().filter(res2 -> {
                return ("3500".equals(res2.getNum()) || "63328".equals(res2.getNum())) ? false : true;
            }).collect(Collectors.toList());
        }
        String str = JSONArray.fromObject(list2).toString();
        this.out.write(str);
    }

    public void queryreporthideAndshow() throws Exception {
        List<String> now;
        this.request.getParameter("Id");
        String name = new CommonUtil().getLoginUserName(this.request);
        this.userService.username(name);
        this.request.getParameter("sortname");
        HttpSession session = this.request.getSession(true);
        User u = (User) session.getAttribute(Const.LOGIN_USER);
        String pnum = this.request.getParameter("pnum");
        this.mark = (String) session.getAttribute(Const.SYSTEM_TYPE);
        new ArrayList();
        if (null != this.idStr && !this.idStr.equals("")) {
            now = Arrays.asList(this.idStr.split(Const.STRING_SEPERATOR));
        } else {
            now = new ArrayList<>();
        }
        List list2 = this.userService.queryreporthideAndshow(u.getId(), pnum, this.mark, now);
        String str = JSONArray.fromObject(list2).toString();
        this.out.write(str);
    }

    public void getCurrentUserInfo() {
        HttpSession session = this.request.getSession(true);
        User u = (User) session.getAttribute(Const.LOGIN_USER);
        Object[] obj = this.userService.querySchoolAndClass(u.getUserid());
        this.out.write(JSON.toJSONString(obj));
    }

    public String parentChangeStu() throws Exception {
        HttpSession session = this.request.getSession(true);
        new User();
        new User();
        ServletContext context = ServletActionContext.getServletContext();
        String student = String.valueOf(this.request.getParameter("thisStuId"));
        User u = this.userService.getUserByUserid(student);
        u.setRoleName(combinRoleName(u.getId()));
        u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
        queryUserSex(u);
        User parentUser = (User) session.getAttribute(Const.LOGIN_PARENTUSER);
        parentUser.setUserid(student);
        session.setAttribute(Const.LOGIN_PARENTUSER, parentUser);
        session.setAttribute(Const.LOGIN_USER, u);
        session.setAttribute("parentLogin", 2);
        session.setAttribute("username", parentUser.getUsername());
        session.setAttribute("xuejihao", this.sgs.getStuInfo(u.getUserid().toString(), "").getStudentId());
        session.setAttribute("studentname", this.sgs.getStuInfo(u.getUserid().toString(), "").getStudentName());
        new CommonUtil().addLog(this.request.getRequestURI(), "切换学生", parentUser.getId(), this.request.getRemoteAddr(), "");
        return Const.SUCCESS;
    }

    public String loginExecute() throws Exception {
        List<Map<String, Object>> mapList;
        Map<String, String> list;
        String sessionCode;
        ServletContext context = ServletActionContext.getServletContext();
        HttpSession session = this.request.getSession(true);
        String systemId = String.valueOf(context.getAttribute("systemId"));
        String id = this.request.getParameter("id");
        String logtype = this.request.getParameter("logtype");
        String timestamp = this.request.getParameter("timestamp");
        String token = this.request.getParameter("token");
        this.user.setUsername(id.trim());
        this.user.setLoginType("0");
        session.setAttribute("logType", logtype);
        if (!logtype.equals(Const.exampaper_doubleFaced_S) && !logtype.equals("T")) {
            this.info = "登录验证失败，登陆类型有误！";
            return "xdfLoginError";
        }
        long currentStamp = System.currentTimeMillis() / 1000;
        long a = (currentStamp * 1) - (Long.parseLong(timestamp) * 1);
        if ((currentStamp < Long.parseLong(timestamp) || a > 600) && !systemId.equals("1450200010") && !systemId.equals("1102206013")) {
            this.info = "登录验证失败，时间已过期！";
            return "xdfLoginError";
        }
        String data = systemId + id + logtype + timestamp;
        String token2 = getMHA512(data);
        if (!token2.equals(token)) {
            this.log.error("data===" + data);
            this.log.error("达美嘉单点登录======loginid=" + id + ",name=" + this.name + ",email=" + this.email + ",time=" + timestamp + ",domain=neworiental,insetdate=" + DateUtil.getCurrentTime());
            this.info = "登录验证失败，请重新验证！";
            return "xdfLoginError";
        }
        new Conffig();
        String filePath = context.getRealPath("/");
        String result = (String) context.getAttribute(Const.result_tag);
        String result_info = (String) context.getAttribute(Const.result_tag_info);
        String userlogintype = String.valueOf(context.getAttribute(Const.allowedToLogin));
        if (userlogintype.equals("1")) {
            this.info = "暂时未开放用户登录权限！";
            return "xdfLoginError";
        }
        if (null != result && result.equals("T")) {
            this.info = result_info;
            return "xdfLoginError";
        }
        String viewRankOfScoreInfo = Configuration.getInstance().getViewRankOfScoreInfo();
        if (viewRankOfScoreInfo == null || viewRankOfScoreInfo.equals("")) {
            viewRankOfScoreInfo = "1";
        }
        String marktype = String.valueOf(context.getAttribute("type"));
        session.setAttribute(Const.viewRankOfScoreInfo, viewRankOfScoreInfo);
        String fp = this.request.getParameter("fp");
        String wos = this.request.getParameter("wos");
        Map tiaoZhuanMap = null;
        if (context.getAttribute("openOcs").equals("1")) {
            Enumeration<?> enumeration = session.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                if ("tiaoZhuanMap".equals(enumeration.nextElement().toString())) {
                    tiaoZhuanMap = (Map) session.getAttribute("tiaoZhuanMap");
                }
            }
            if ((this.user.getUsername() == null || "".equals(this.user.getUsername())) && ((this.user.getPassword() == null || "".equals(this.user.getPassword())) && ((this.user.getLoginType() == null || "".equals(this.user.getLoginType())) && null != tiaoZhuanMap))) {
                this.user = (User) tiaoZhuanMap.get("user");
                this.authcode_val = (String) tiaoZhuanMap.get("authcode_val");
                fp = (String) tiaoZhuanMap.get("fp");
                wos = (String) tiaoZhuanMap.get("wos");
                String logType = (String) tiaoZhuanMap.get("logType");
                session.setAttribute("logType", logType);
            }
        }
        ServletContext servletContext = ServletActionContext.getServletContext();
        if ("1".equals(marktype) || "0".equals(marktype)) {
            this.request.setAttribute("marks", marktype);
            session.setAttribute(Const.SYSTEM_TYPE, marktype);
        } else {
            String markpd = String.valueOf(context.getAttribute("type"));
            Cookie[] cookies = this.request.getCookies();
            if ((null == markpd || "".equals(markpd)) && null != cookies) {
                for (Cookie c : cookies) {
                    if ("marks".equals(c.getName()) && null != c.getValue() && !"".equals(c.getValue())) {
                        this.request.setAttribute("marks", c.getValue());
                        session.setAttribute(Const.SYSTEM_TYPE, c.getValue());
                    }
                }
            }
        }
        String info_error = Info_error;
        try {
            if (Const.exampaper_doubleFaced_S.equals(logtype) || "P".equals(logtype)) {
                String astrictLogin = Conffig.getParameter(filePath, Const.astrictLogin);
                if ("1".equals(astrictLogin) && null != (mapList = this.astrictService.getAstrictLoginDataNowByPartType("4")) && !"".equals(mapList) && mapList.size() != 0) {
                    this.info = "暂时不能登录，不能登录的时间范围为: ";
                    for (int i = 0; i < mapList.size(); i++) {
                        this.info += mapList.get(i).get("startTime") + "至" + mapList.get(i).get("endTime") + "  ";
                    }
                    return "xdfLoginError";
                }
            }
            if ("0".equals(this.user.getLoginType())) {
                if (null == this.user.getUsername() || this.user.getUsername().equals("")) {
                    return "xdfLoginError";
                }
                this.user.setUsername(this.user.getUsername().trim());
            } else if ("1".equals(this.user.getLoginType())) {
                info_error = "手机号与密码不匹配，请检查后重新登录！";
                if (null == this.user.getMobile() || this.user.getMobile().equals("")) {
                    return "xdfLoginError";
                }
                this.user.setMobile(this.user.getMobile().trim());
            }
            this.user.setExt1((String) session.getAttribute("logType"));
            User parentUser = new User();
            session.setAttribute(Const.task_rownum, "T");
            User u = new User();
            String dmjpassword = Configuration.getInstance().getAc01();
            if (dmjpassword.equals("null")) {
                dmjpassword = "dmj";
            }
            session.setAttribute("parentLogin", 3);
            if ("dmj".equals(this.user.getUsername()) && dmjpassword.equals(getMHA512(this.user.getPassword()))) {
                u.setId("-2");
                u.setUserid("0");
                u.setUsername("dmj");
                u.setPassword("dmj");
                u.setUsertype("0");
                u.setLoginname("dmj");
                u.setRealname("dmj");
                u.setIsUser("T");
                u.setIsDelete("F");
                session.setAttribute(Const.teacher_permission_cla, "1");
                session.setAttribute(Const.teacher_permission_gra, "1");
                session.setAttribute(Const.teacher_permission_sub, "1");
            } else {
                if ("0".equals(this.user.getLoginType())) {
                    u = this.userService.getUserByUsernameAndPwd(this.user, (String) session.getAttribute("logType"), "1");
                } else if ("1".equals(this.user.getLoginType())) {
                    u = this.userService.getUserByMobile(this.user);
                }
                if ("P".equals(session.getAttribute("logType"))) {
                    parentUser = this.userService.getUserParentByUsernameAndPwd(this.user);
                    session.setAttribute("parentLogin", 1);
                } else if ("T".equals(session.getAttribute("logType")) && u != null) {
                    if ("0".equals(u.getUsertype())) {
                        session.setAttribute(Const.teacher_permission_cla, "1");
                        session.setAttribute(Const.teacher_permission_gra, "1");
                        session.setAttribute(Const.teacher_permission_sub, "1");
                    } else {
                        List<Map<String, Object>> positionList = this.userService.getTeaMaxPermission(u.getId());
                        session.setAttribute("loginTeaPositionList", positionList);
                        if (CollUtil.isEmpty(positionList)) {
                            String viewAllReports = Configuration.getInstance().getViewAllReports();
                            session.setAttribute(Const.teacher_permission_cla, viewAllReports);
                            session.setAttribute(Const.teacher_permission_gra, viewAllReports);
                            session.setAttribute(Const.teacher_permission_sub, viewAllReports);
                        } else {
                            OptionalInt res_permission_class = positionList.stream().mapToInt(m -> {
                                return Convert.toInt(m.get("permission_class"), 0).intValue();
                            }).max();
                            int max_permission_class = res_permission_class.isPresent() ? res_permission_class.getAsInt() : 0;
                            OptionalInt res_permission_grade = positionList.stream().mapToInt(m2 -> {
                                return Convert.toInt(m2.get("permission_grade"), 0).intValue();
                            }).max();
                            int max_permission_grade = res_permission_grade.isPresent() ? res_permission_grade.getAsInt() : 0;
                            OptionalInt res_permission_subject = positionList.stream().mapToInt(m3 -> {
                                return Convert.toInt(m3.get("permission_subject"), 0).intValue();
                            }).max();
                            int max_permission_subject = res_permission_subject.isPresent() ? res_permission_subject.getAsInt() : 0;
                            session.setAttribute(Const.teacher_permission_cla, Integer.valueOf(max_permission_class));
                            session.setAttribute(Const.teacher_permission_gra, Integer.valueOf(max_permission_grade));
                            session.setAttribute(Const.teacher_permission_sub, Integer.valueOf(max_permission_subject));
                        }
                    }
                }
            }
            session.setAttribute("userpassword", this.user.getPassword());
            session.setAttribute(Const.LOGIN_PARENTUSER, parentUser);
            if (null == u) {
                session.removeAttribute(Const.LOGIN_USER);
                this.info = info_error;
                return "xdfLoginError";
            }
            if ("T".equals(session.getAttribute("logType")) && u.getIsDelete().equals("T")) {
                this.info = info_error;
                session.removeAttribute(Const.LOGIN_USER);
                return "xdfLoginError";
            }
            String openocs = (String) context.getAttribute("openOcs");
            Date expiredDate = (Date) context.getAttribute("expiredDate");
            session.setAttribute("openocs", openocs);
            int surplusdays = DateUtil.authonelicense_zy(expiredDate);
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            session.setAttribute("licensedays", Integer.valueOf(surplusdays));
            session.setAttribute("licensedate", dateFormater.format(expiredDate));
            if (openocs.equals("1")) {
                if ("T".equals(session.getAttribute("logType")) && u.getUsertype().equals("1")) {
                    session.setAttribute("fp", fp);
                    session.setAttribute("wos", wos);
                    this.user.getMobile();
                    if ("0".equals(this.user.getLoginType())) {
                        this.userService.searchUserPhone(this.user.getUsername(), "T");
                    }
                    session.setAttribute("tcuser", u);
                    session.setAttribute("tcUsername", u.getUsername());
                    if (tiaoZhuanMap == null) {
                        Map tiaoZhuanMap2 = new HashMap();
                        User urt = new User();
                        urt.setUsername(this.user.getUsername());
                        urt.setMobile(this.user.getMobile());
                        urt.setPassword(this.user.getPassword());
                        urt.setLoginType(this.user.getLoginType());
                        tiaoZhuanMap2.put("logType", this.request.getParameter("logType"));
                        tiaoZhuanMap2.put("authcode_val", this.authcode_val);
                        tiaoZhuanMap2.put("user", urt);
                        tiaoZhuanMap2.put("isRt", "true");
                        tiaoZhuanMap2.put("fp", fp);
                        tiaoZhuanMap2.put("wos", wos);
                        session.setAttribute("tiaoZhuanMap", tiaoZhuanMap2);
                    }
                    session.removeAttribute("tiaoZhuanMap");
                } else if (Const.exampaper_doubleFaced_S.equals(session.getAttribute("logType"))) {
                    this.user.getMobile();
                    if ("0".equals(this.user.getLoginType())) {
                        this.userService.searchUserPhone(this.user.getUsername(), Const.exampaper_doubleFaced_S);
                    }
                    session.setAttribute("tcuser", u);
                    session.setAttribute("tcUsername", u.getUsername());
                    String areasystemid = (String) context.getAttribute("systemId");
                    session.setAttribute("areasystemid", areasystemid);
                    session.setAttribute("userid", u.getUserid());
                    this.sgs.getStuInfo(u.getUserid().toString(), "");
                    session.setAttribute("username", u.getUsername().trim());
                    session.setAttribute("xuejihao", this.sgs.getStuInfo(u.getUserid().toString(), "").getStudentId());
                    session.setAttribute("studentname", this.sgs.getStuInfo(u.getUserid().toString(), "").getStudentName());
                    String publicip = this.userService.getpublicip("10");
                    session.setAttribute("publicip", publicip);
                } else if ("P".equals(session.getAttribute("logType")) || u.getUsertype().equals("2")) {
                    String areasystemid2 = (String) context.getAttribute("systemId");
                    session.setAttribute("areasystemid", areasystemid2);
                    session.setAttribute("userid", u.getUserid());
                    this.sgs.getStuInfo(u.getUserid().toString(), "");
                    session.setAttribute("username", this.user.getUsername().trim());
                    session.setAttribute("xuejihao", this.sgs.getStuInfo(u.getUserid().toString(), "").getStudentId());
                    session.setAttribute("studentname", this.sgs.getStuInfo(u.getUserid().toString(), "").getStudentName());
                    String day = Conffig.getParameter(filePath, Const.day);
                    if (null == day || "".equals(day)) {
                        day = Const.fenfaTimeout;
                    }
                    Integer.valueOf(day).intValue();
                    String publicip2 = this.userService.getpublicip("10");
                    session.setAttribute("publicip", publicip2);
                }
            }
            session.setAttribute("signnl", servletContext.getAttribute("signnl"));
            if ("P".equals(session.getAttribute("logType"))) {
                list = this.userService.rolelist(parentUser.getId(), openocs);
            } else {
                list = this.userService.rolelist(u.getId(), openocs);
            }
            u.setRoleName(combinRoleName(u.getId()));
            if ("P".equals(session.getAttribute("logType"))) {
                parentUser.setIp(this.request.getRemoteAddr());
                parentUser.setRoleName(combinRoleName(parentUser.getId()));
            } else {
                u.setIp(this.request.getRemoteAddr());
            }
            u.setRoleName(combinRoleName(u.getId()));
            u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
            session.setAttribute(Const.LOGIN_USER, u);
            session.setAttribute("list", list);
            session.setAttribute("version", String.valueOf(context.getAttribute(Const.school_limit)));
            session.setAttribute(Const.CURRENT_JIE, this.sysService.getCurrentJie());
            queryUserSex(u);
            if (!"0".equals(this.authcode_val) && null != (sessionCode = (String) ActionContext.getContext().getSession().get("sessionCode")) && !sessionCode.equals(getCode())) {
                session.removeAttribute(Const.LOGIN_USER);
                return "xdfLoginError";
            }
            session.setAttribute("startVal", 1);
            session.setAttribute("numVal", "");
            session.setAttribute("logName", u.getUsername());
            if ("P".equals(session.getAttribute("logType"))) {
                new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", parentUser.getId(), this.request.getRemoteAddr(), "");
            } else {
                new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", u.getId(), this.request.getRemoteAddr(), "");
            }
            String logType2 = (String) session.getAttribute("logType");
            MySessionContext.InsertOnlineUser(session);
            if ("P".equals(logType2)) {
                this.userService.getUserParentByUsernameAndPwd(this.user);
                String isEnforceChangePassword = Configuration.getInstance().getIsEnforceChangePassword();
                if (!CommonUtil.isPassword(this.user.getPassword()) && isEnforceChangePassword.equals("1")) {
                    session.setAttribute("isTruePassword", "0");
                    return "oneupdatepass";
                }
                return Const.SUCCESS;
            }
            return Const.SUCCESS;
        } catch (Exception e) {
            if (this.signle == 0) {
                this.info = hession_error;
                return "xdfLoginError";
            }
            this.log.error("登陆：", e);
            session.removeAttribute(Const.LOGIN_USER);
            this.info = info_error;
            return "xdfLoginError";
        }
    }

    public String execute() throws Exception {
        List<Map<String, Object>> mapList;
        Map<String, String> list;
        String sessionCode;
        ServletContext context = ServletActionContext.getServletContext();
        this.user.setUsername(SecretUtil.desEncrypt(this.user.getUsername()));
        this.user.setPassword(SecretUtil.desEncrypt(this.user.getPassword()));
        new Conffig();
        String filePath = context.getRealPath("/");
        String result = (String) context.getAttribute(Const.result_tag);
        String result_info = (String) context.getAttribute(Const.result_tag_info);
        String userlogintype = String.valueOf(context.getAttribute(Const.allowedToLogin));
        if (userlogintype.equals("1")) {
            this.info = "暂时未开放用户登录权限！";
            return Const.sample_error_reRecognized;
        }
        if (null != result && result.equals("T")) {
            this.info = result_info;
            return Const.sample_error_reRecognized;
        }
        String nameorphone = this.userService.getUserByName(this.user.getUsername());
        if (null != nameorphone && !nameorphone.equals("")) {
            this.user.setLoginType("0");
        } else {
            this.user.setLoginType("1");
            this.user.setMobile(this.user.getUsername());
        }
        String viewRankOfScoreInfo = Configuration.getInstance().getViewRankOfScoreInfo();
        if (viewRankOfScoreInfo == null || viewRankOfScoreInfo.equals("")) {
            viewRankOfScoreInfo = "1";
        }
        String marktype = String.valueOf(context.getAttribute("type"));
        HttpSession session = this.request.getSession(true);
        session.setAttribute("logType", this.request.getParameter("logType"));
        session.setAttribute(Const.viewRankOfScoreInfo, viewRankOfScoreInfo);
        String fp = this.request.getParameter("fp");
        String wos = this.request.getParameter("wos");
        Map tiaoZhuanMap = null;
        if (context.getAttribute("openOcs").equals("1")) {
            Enumeration<?> enumeration = session.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                if ("tiaoZhuanMap".equals(enumeration.nextElement().toString())) {
                    tiaoZhuanMap = (Map) session.getAttribute("tiaoZhuanMap");
                }
            }
            if ((this.user.getUsername() == null || "".equals(this.user.getUsername())) && ((this.user.getPassword() == null || "".equals(this.user.getPassword())) && ((this.user.getLoginType() == null || "".equals(this.user.getLoginType())) && null != tiaoZhuanMap))) {
                this.user = (User) tiaoZhuanMap.get("user");
                this.authcode_val = (String) tiaoZhuanMap.get("authcode_val");
                fp = (String) tiaoZhuanMap.get("fp");
                wos = (String) tiaoZhuanMap.get("wos");
                String logType = (String) tiaoZhuanMap.get("logType");
                session.setAttribute("logType", logType);
            }
        }
        ServletContext servletContext = ServletActionContext.getServletContext();
        if ("1".equals(marktype) || "0".equals(marktype)) {
            this.request.setAttribute("marks", marktype);
            session.setAttribute(Const.SYSTEM_TYPE, marktype);
        } else {
            String markpd = String.valueOf(context.getAttribute("type"));
            Cookie[] cookies = this.request.getCookies();
            if ((null == markpd || "".equals(markpd)) && null != cookies) {
                for (Cookie c : cookies) {
                    if ("marks".equals(c.getName()) && null != c.getValue() && !"".equals(c.getValue())) {
                        this.request.setAttribute("marks", c.getValue());
                        session.setAttribute(Const.SYSTEM_TYPE, c.getValue());
                    }
                }
            }
        }
        String userid = "";
        Integer errornum1 = 0;
        Integer errornumSet1 = 0;
        Map<String, Object> map = new HashMap<>();
        if (!"dmj".equals(this.user.getUsername())) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String errornumSet = Configuration.getInstance().getErrorNum();
            errornumSet1 = Convert.toInt(errornumSet);
            String lockTimeSet = Configuration.getInstance().getLockTime();
            Integer lockTimeSet1 = Convert.toInt(lockTimeSet);
            map = this.userService.getlockMap(this.user.getUsername(), this.request.getParameter("logType"));
            if (null != map) {
                userid = map.get("id").toString();
                String errornum = map.get("errornum").toString();
                errornum1 = Convert.toInt(errornum);
                String locktime = map.get("locktime").toString();
                if (!locktime.equals("")) {
                    new SimpleDateFormat(lockTimeSet);
                    Date locktime1 = dateFormat.parse(locktime);
                    Long locktime2 = Long.valueOf(locktime1.getTime() + (lockTimeSet1.intValue() * 60000));
                    String timenow = DateUtil.getCurrentTime();
                    Date timenow1 = dateFormat.parse(timenow);
                    Long timenow2 = Long.valueOf(timenow1.getTime());
                    if (timenow2.longValue() <= locktime2.longValue() && errornumSet1.intValue() != 0 && lockTimeSet1.intValue() != 0) {
                        String endtime = dateFormat.format(locktime2);
                        this.info = "密码错误次数已超限制，账号已经被锁定,请在" + endtime + "后重试！";
                        return Const.sample_error_reRecognized;
                    }
                    this.userService.updateErrornum(userid, 0, this.request.getParameter("logType"));
                }
            }
        }
        String info_error = Info_error;
        try {
            if (Const.exampaper_doubleFaced_S.equals(this.request.getParameter("logType")) || "P".equals(this.request.getParameter("logType"))) {
                String astrictLogin = Conffig.getParameter(filePath, Const.astrictLogin);
                new ArrayList();
                if ("1".equals(astrictLogin)) {
                    if (Const.exampaper_doubleFaced_S.equals(this.request.getParameter("logType"))) {
                        mapList = this.astrictService.getAstrictLoginDataNowByPartType3("4", this.user.getUsername());
                    } else {
                        mapList = this.astrictService.getAstrictLoginDataNowByPartType2("4", this.user.getUsername());
                    }
                    if (null != mapList && !"".equals(mapList) && mapList.size() != 0) {
                        String shuoming = Convert.toStr(mapList.get(0).get("shuoming"));
                        this.info = "暂不开放学生/家长查询";
                        if (!shuoming.equals("")) {
                            this.info = shuoming;
                            return Const.sample_error_reRecognized;
                        }
                        return Const.sample_error_reRecognized;
                    }
                }
            }
            if ("0".equals(this.user.getLoginType())) {
                if (null == this.user.getUsername() || this.user.getUsername().equals("")) {
                    return Const.sample_error_reRecognized;
                }
                this.user.setUsername(this.user.getUsername().trim());
            } else if ("1".equals(this.user.getLoginType())) {
                info_error = "手机号与密码不匹配，请检查后重新登录！";
                if (null == this.user.getMobile() || this.user.getMobile().equals("")) {
                    return Const.sample_error_reRecognized;
                }
                this.user.setMobile(this.user.getMobile().trim());
            }
            this.user.setExt1((String) session.getAttribute("logType"));
            User parentUser = new User();
            session.setAttribute(Const.task_rownum, "T");
            User u = new User();
            String dmjpassword = Configuration.getInstance().getAc01();
            if (dmjpassword.equals("null")) {
                dmjpassword = "dmj";
            }
            session.setAttribute("parentLogin", 3);
            if ("dmj".equals(this.user.getUsername()) && dmjpassword.equals(getMHA512(this.user.getPassword()))) {
                u.setId("-2");
                u.setUserid("0");
                u.setUsername("dmj");
                u.setPassword("dmj");
                u.setUsertype("0");
                u.setLoginname("dmj");
                u.setRealname("dmj");
                u.setIsUser("T");
                u.setIsDelete("F");
                session.setAttribute(Const.teacher_permission_cla, "1");
                session.setAttribute(Const.teacher_permission_gra, "1");
                session.setAttribute(Const.teacher_permission_sub, "1");
            } else {
                if ("0".equals(this.user.getLoginType())) {
                    u = this.userService.getUserByUsernameAndPwd(this.user, (String) session.getAttribute("logType"), Convert.toStr(session.getAttribute("singleLogin")));
                } else if ("1".equals(this.user.getLoginType())) {
                    u = this.userService.getUserByMobile(this.user);
                }
                if ("P".equals(session.getAttribute("logType"))) {
                    parentUser = this.userService.getUserParentByUsernameAndPwd(this.user);
                    session.setAttribute("parentLogin", 1);
                } else if ("T".equals(session.getAttribute("logType")) && u != null) {
                    if ("0".equals(u.getUsertype())) {
                        session.setAttribute(Const.teacher_permission_cla, "1");
                        session.setAttribute(Const.teacher_permission_gra, "1");
                        session.setAttribute(Const.teacher_permission_sub, "1");
                    } else {
                        List<Map<String, Object>> positionList = this.userService.getTeaMaxPermission(u.getId());
                        session.setAttribute("loginTeaPositionList", positionList);
                        if (CollUtil.isEmpty(positionList)) {
                            String viewAllReports = Configuration.getInstance().getViewAllReports();
                            session.setAttribute(Const.teacher_permission_cla, viewAllReports);
                            session.setAttribute(Const.teacher_permission_gra, viewAllReports);
                            session.setAttribute(Const.teacher_permission_sub, viewAllReports);
                        } else {
                            OptionalInt res_permission_class = positionList.stream().mapToInt(m -> {
                                return Convert.toInt(m.get("permission_class"), 0).intValue();
                            }).max();
                            int max_permission_class = res_permission_class.isPresent() ? res_permission_class.getAsInt() : 0;
                            OptionalInt res_permission_grade = positionList.stream().mapToInt(m2 -> {
                                return Convert.toInt(m2.get("permission_grade"), 0).intValue();
                            }).max();
                            int max_permission_grade = res_permission_grade.isPresent() ? res_permission_grade.getAsInt() : 0;
                            OptionalInt res_permission_subject = positionList.stream().mapToInt(m3 -> {
                                return Convert.toInt(m3.get("permission_subject"), 0).intValue();
                            }).max();
                            int max_permission_subject = res_permission_subject.isPresent() ? res_permission_subject.getAsInt() : 0;
                            session.setAttribute(Const.teacher_permission_cla, Integer.valueOf(max_permission_class));
                            session.setAttribute(Const.teacher_permission_gra, Integer.valueOf(max_permission_grade));
                            session.setAttribute(Const.teacher_permission_sub, Integer.valueOf(max_permission_subject));
                        }
                    }
                }
            }
            session.setAttribute("userpassword", this.user.getPassword());
            session.setAttribute(Const.LOGIN_PARENTUSER, parentUser);
            if (null == u || ("-2".equals(u.getId()) && !"T".equals(session.getAttribute("logType")))) {
                if (!"dmj".equals(this.user.getUsername()) && null != map) {
                    this.userService.updateErrornum(userid, Integer.valueOf(errornum1.intValue() + 1), this.request.getParameter("logType"));
                    if (errornum1.intValue() + 1 >= errornumSet1.intValue()) {
                        this.userService.updateLocktime(userid, DateUtil.getCurrentTime(), this.request.getParameter("logType"));
                    }
                }
                session.removeAttribute(Const.LOGIN_USER);
                this.info = info_error;
                return Const.sample_error_reRecognized;
            }
            if ("T".equals(session.getAttribute("logType")) && u.getIsDelete().equals("T")) {
                this.info = info_error;
                session.removeAttribute(Const.LOGIN_USER);
                return Const.sample_error_reRecognized;
            }
            String openocs = (String) context.getAttribute("openOcs");
            Date expiredDate = (Date) context.getAttribute("expiredDate");
            session.setAttribute("openocs", openocs);
            int surplusdays = DateUtil.authonelicense_zy(expiredDate);
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            session.setAttribute("licensedays", Integer.valueOf(surplusdays));
            session.setAttribute("licensedate", dateFormater.format(expiredDate));
            if (openocs.equals("1")) {
                if ("T".equals(session.getAttribute("logType")) && u.getUsertype().equals("1")) {
                    session.setAttribute("fp", fp);
                    session.setAttribute("wos", wos);
                    this.user.getMobile();
                    if ("0".equals(this.user.getLoginType())) {
                        this.userService.searchUserPhone(this.user.getUsername(), "T");
                    }
                    session.setAttribute("tcuser", u);
                    session.setAttribute("tcUsername", u.getUsername());
                    if (tiaoZhuanMap == null) {
                        Map tiaoZhuanMap2 = new HashMap();
                        User urt = new User();
                        urt.setUsername(this.user.getUsername());
                        urt.setMobile(this.user.getMobile());
                        urt.setPassword(this.user.getPassword());
                        urt.setLoginType(this.user.getLoginType());
                        tiaoZhuanMap2.put("logType", this.request.getParameter("logType"));
                        tiaoZhuanMap2.put("authcode_val", this.authcode_val);
                        tiaoZhuanMap2.put("user", urt);
                        tiaoZhuanMap2.put("isRt", "true");
                        tiaoZhuanMap2.put("fp", fp);
                        tiaoZhuanMap2.put("wos", wos);
                        session.setAttribute("tiaoZhuanMap", tiaoZhuanMap2);
                    }
                    session.removeAttribute("tiaoZhuanMap");
                } else if (Const.exampaper_doubleFaced_S.equals(session.getAttribute("logType"))) {
                    this.user.getMobile();
                    if ("0".equals(this.user.getLoginType())) {
                        this.userService.searchUserPhone(this.user.getUsername(), Const.exampaper_doubleFaced_S);
                    }
                    session.setAttribute("tcuser", u);
                    session.setAttribute("tcUsername", u.getUsername());
                    String areasystemid = (String) context.getAttribute("systemId");
                    session.setAttribute("areasystemid", areasystemid);
                    session.setAttribute("userid", u.getUserid());
                    this.sgs.getStuInfo(u.getUserid().toString(), "");
                    session.setAttribute("username", u.getUsername().trim());
                    session.setAttribute("xuejihao", this.sgs.getStuInfo(u.getUserid().toString(), "").getStudentId());
                    session.setAttribute("studentname", this.sgs.getStuInfo(u.getUserid().toString(), "").getStudentName());
                    String publicip = this.userService.getpublicip("10");
                    session.setAttribute("publicip", publicip);
                } else if ("P".equals(session.getAttribute("logType")) || u.getUsertype().equals("2")) {
                    String areasystemid2 = (String) context.getAttribute("systemId");
                    session.setAttribute("areasystemid", areasystemid2);
                    session.setAttribute("userid", u.getUserid());
                    this.sgs.getStuInfo(u.getUserid().toString(), "");
                    session.setAttribute("username", this.user.getUsername().trim());
                    session.setAttribute("xuejihao", this.sgs.getStuInfo(u.getUserid().toString(), "").getStudentId());
                    session.setAttribute("studentname", this.sgs.getStuInfo(u.getUserid().toString(), "").getStudentName());
                    String day = Conffig.getParameter(filePath, Const.day);
                    if (null == day || "".equals(day)) {
                        day = Const.fenfaTimeout;
                    }
                    Integer.valueOf(day).intValue();
                    String publicip2 = this.userService.getpublicip("10");
                    session.setAttribute("publicip", publicip2);
                }
            }
            session.setAttribute("signnl", servletContext.getAttribute("signnl"));
            if ("P".equals(session.getAttribute("logType"))) {
                list = this.userService.rolelist(parentUser.getId(), openocs);
            } else {
                list = this.userService.rolelist(u.getId(), openocs);
            }
            u.setRoleName(combinRoleName(u.getId()));
            if ("P".equals(session.getAttribute("logType"))) {
                parentUser.setIp(this.request.getRemoteAddr());
                parentUser.setRoleName(combinRoleName(parentUser.getId()));
            } else {
                u.setIp(this.request.getRemoteAddr());
            }
            u.setRoleName(combinRoleName(u.getId()));
            u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
            session.setAttribute(Const.LOGIN_USER, u);
            session.setAttribute("list", list);
            session.setAttribute("version", String.valueOf(context.getAttribute(Const.school_limit)));
            session.setAttribute(Const.CURRENT_JIE, this.sysService.getCurrentJie());
            queryUserSex(u);
            if (!"0".equals(this.authcode_val) && null != (sessionCode = (String) ActionContext.getContext().getSession().get("sessionCode")) && !sessionCode.equals(getCode())) {
                session.removeAttribute(Const.LOGIN_USER);
                return Const.sample_error_reRecognized;
            }
            session.setAttribute("startVal", 1);
            session.setAttribute("numVal", "");
            session.setAttribute("logName", u.getUsername());
            if ("P".equals(session.getAttribute("logType"))) {
                new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", parentUser.getId(), this.request.getRemoteAddr(), "");
            } else {
                new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", u.getId(), this.request.getRemoteAddr(), "");
            }
            User us2 = (User) session.getAttribute(Const.LOGIN_USER);
            String logType2 = (String) session.getAttribute("logType");
            MySessionContext.InsertOnlineUser(session);
            String isEnforceChangePassword = Configuration.getInstance().getIsEnforceChangePassword();
            if ("P".equals(logType2)) {
                this.userService.getUserParentByUsernameAndPwd(this.user);
                if (!CommonUtil.isPassword(this.user.getPassword()) && isEnforceChangePassword.equals("1")) {
                    session.setAttribute("isTruePassword", "0");
                    return "oneupdatepass";
                }
                if (!"dmj".equals(this.user.getUsername())) {
                    this.userService.updateErrornum(userid, 0, this.request.getParameter("logType"));
                    return Const.SUCCESS;
                }
                return Const.SUCCESS;
            }
            if (!"-2".equals(us2.getId()) && !CommonUtil.isPassword(this.user.getPassword()) && isEnforceChangePassword.equals("1")) {
                session.setAttribute("isTruePassword", "0");
                return "oneupdatepass";
            }
            if (!"dmj".equals(this.user.getUsername())) {
                this.userService.updateErrornum(userid, 0, this.request.getParameter("logType"));
                return Const.SUCCESS;
            }
            return Const.SUCCESS;
        } catch (Exception e) {
            if (this.signle == 0) {
                this.info = hession_error;
                return Const.sample_error_reRecognized;
            }
            this.log.error("登陆：", e);
            session.removeAttribute(Const.LOGIN_USER);
            this.info = info_error;
            return Const.sample_error_reRecognized;
        }
    }

    public String loginstart() {
        return Const.SUCCESS;
    }

    public void getRolesYjy() {
        String loginNum = new CommonUtil().getLoginUserNum(this.request);
        Integer yjyrole = this.userService.getRolesYjy(loginNum);
        String yjyrole2 = String.valueOf(yjyrole);
        this.out.write(yjyrole2);
    }

    public String execute2() throws Exception {
        Map<String, String> list;
        HttpSession session = this.request.getSession(true);
        ServletContext context = ServletActionContext.getServletContext();
        User u = (User) session.getAttribute(Const.LOGIN_USER);
        User parentUser = (User) session.getAttribute(Const.LOGIN_PARENTUSER);
        this.request.setAttribute("user", u);
        try {
            if (u.getIsDelete().equals("T")) {
                this.info = Info_error;
                return Const.sample_error_reRecognized;
            }
            if ("P".equals(session.getAttribute("logType"))) {
                list = this.userService.rolelist(parentUser.getId(), (String) session.getAttribute("openocs"));
            } else {
                list = this.userService.rolelist(u.getId(), (String) session.getAttribute("openocs"));
            }
            u.setRoleName(combinRoleName(u.getId()));
            if ("P".equals(session.getAttribute("logType"))) {
                parentUser.setIp(this.request.getRemoteAddr());
                parentUser.setRoleName(combinRoleName(parentUser.getId()));
            } else {
                u.setIp(this.request.getRemoteAddr());
            }
            u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
            parentUser.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
            String openocs = (String) context.getAttribute("openOcs");
            if ("1".equals(openocs) && ("P".equals(session.getAttribute("logType")) || u.getUsertype().equals("2"))) {
            }
            this.request.getContextPath();
            session.setAttribute(Const.LOGIN_USER, u);
            session.setAttribute("list", list);
            session.setAttribute("version", String.valueOf(context.getAttribute(Const.school_limit)));
            session.setAttribute(Const.CURRENT_JIE, this.sysService.getCurrentJie());
            new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", u.getId(), this.request.getRemoteAddr(), "");
            String autoreg = String.valueOf(session.getAttribute("autoreg"));
            String logtype = (String) session.getAttribute("logType");
            MySessionContext.InsertOnlineUser(session);
            String isEnforceChangePassword = Configuration.getInstance().getIsEnforceChangePassword();
            if ("P".equals(logtype)) {
                return Const.SUCCESS;
            }
            if (!"1".equals(autoreg)) {
                if (u.getPassword().equals(Const.STU_PASSWORD) || (u.getPassword().equals(Const.USER_PASSWORD) && isEnforceChangePassword.equals("1"))) {
                    session.setAttribute("isTruePassword", "0");
                    return "oneupdatepass";
                }
                return Const.SUCCESS;
            }
            return Const.SUCCESS;
        } catch (Exception e) {
            if (this.signle == 0) {
                this.info = hession_error;
                return Const.sample_error_reRecognized;
            }
            this.log.error("登陆：", e);
            this.info = Info_error;
            return Const.sample_error_reRecognized;
        }
    }

    public String copy_loginExecute() throws Exception {
        this.logtype = this.request.getParameter("logtype");
        HttpSession session = this.request.getSession();
        session.setAttribute("logType", this.logtype);
        if (this.logtype.equals("student")) {
            session.setAttribute("logType", Const.exampaper_doubleFaced_S);
        } else if (this.logtype.equals("teacher")) {
            session.setAttribute("logType", "T");
        } else {
            this.info = "登录验证失败，登陆类型有误！";
            return "xdfLoginError";
        }
        String data = this.id + this.email + this.timestamp + "neworiental" + Const.Auto_login_key;
        String token2 = getMHA512(data);
        if (!token2.equals(this.token)) {
            this.log.error("data===" + data);
            this.log.error("新东方单点登录======loginid=" + this.id + ",name=" + this.name + ",email=" + this.email + ",time=" + this.timestamp + ",domain=neworiental,insetdate=" + DateUtil.getCurrentTime());
            this.info = "登录验证失败，请重新验证！";
            return "xdfLoginError";
        }
        ServletContext context = ServletActionContext.getServletContext();
        new Conffig();
        context.getRealPath("/");
        String marktype = String.valueOf(context.getAttribute("type"));
        session.setAttribute("autoreg", "1");
        if ("1".equals(marktype) || "0".equals(marktype)) {
            this.request.setAttribute("marks", marktype);
            session.setAttribute(Const.SYSTEM_TYPE, marktype);
        } else {
            String markpd = String.valueOf(context.getAttribute("type"));
            Cookie[] cookies = this.request.getCookies();
            if ((null == markpd || "".equals(markpd)) && null != cookies) {
                for (Cookie c : cookies) {
                    if ("marks".equals(c.getName()) && null != c.getValue() && !"".equals(c.getValue())) {
                        this.request.setAttribute("marks", c.getValue());
                        session.setAttribute(Const.SYSTEM_TYPE, c.getValue());
                    }
                }
            }
        }
        try {
            Date expiredDate = (Date) context.getAttribute("expiredDate");
            int surplusdays = DateUtil.authonelicense_zy(expiredDate);
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            session.setAttribute("licensedays", Integer.valueOf(surplusdays));
            session.setAttribute("licensedate", dateFormater.format(expiredDate));
            session.setAttribute(Const.task_rownum, "T");
            this.user.setUsername(this.id.trim());
            this.user.setRealname(this.name);
            if (this.logtype.equals("student")) {
                this.user.setPassword("111111");
            } else if (this.logtype.equals("teacher")) {
                this.user.setPassword("000000");
            }
            User u = new User();
            String dmjpassword = Configuration.getInstance().getAc01();
            if (dmjpassword.equals("null")) {
                dmjpassword = "dmj";
            }
            if ("dmj".equals(this.user.getUsername()) && dmjpassword.equals(getMHA512(this.user.getPassword()))) {
                u.setId("-2");
                u.setUserid("0");
                u.setUsername("dmj");
                u.setPassword("dmj");
                u.setUsertype("0");
                u.setLoginname("dmj");
                u.setRealname("dmj");
                u.setIsUser("T");
                u.setIsDelete("F");
            } else {
                u = this.userService.getUserByUsername(this.user, (String) session.getAttribute("logType"));
                if ("".equals(u) || null == u || "".equals(u.getId())) {
                    if (this.logtype.equals("student")) {
                        this.info = "未查询到此学生信息";
                        session.removeAttribute(Const.LOGIN_USER);
                        return "xdfLoginError";
                    }
                    if (this.logtype.equals("teacher")) {
                        this.tt.addTeacher(this.id, this.name, "", "", "", "", "-1", DateUtil.getCurrentTime(), "-1", "", this.email);
                        u = this.userService.getUserByUsernameAndPwd(this.user, (String) session.getAttribute("logType"));
                    }
                }
            }
            Map<String, String> list = this.userService.rolelist(u.getId(), (String) session.getAttribute("openocs"));
            u.setRoleName(combinRoleName(u.getId()));
            u.setIp(this.request.getRemoteAddr());
            u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
            this.request.getContextPath();
            session.setAttribute(Const.LOGIN_USER, u);
            session.setAttribute("list", list);
            session.setAttribute("version", String.valueOf(context.getAttribute(Const.school_limit)));
            session.setAttribute(Const.CURRENT_JIE, this.sysService.getCurrentJie());
            queryUserSex(u);
            session.setAttribute("startVal", 1);
            session.setAttribute("numVal", "");
            new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", u.getId(), this.request.getRemoteAddr(), "");
            MySessionContext.InsertOnlineUser(session);
            return Const.SUCCESS;
        } catch (Exception e) {
            session.removeAttribute(Const.LOGIN_USER);
            this.info = "请求超时，请重新登录！";
            return "xdfLoginError";
        }
    }

    private String getMHA512(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(data.getBytes());
            return new HexBinaryAdapter().marshal(digest).toLowerCase().trim();
        } catch (Exception e) {
            this.log.error("shimo sha512:" + e.getMessage());
            return "";
        }
    }

    private List<Map<String, String>> parseStringToList(String str) throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        if (str == null || str.equals("")) {
            return list;
        }
        String[] array = decode(str).split("-");
        for (String subArray : array) {
            String[] keyResult = subArray.split(Const.STRING_SEPERATOR);
            Map<String, String> map = new HashMap<>();
            for (String subResult : keyResult) {
                String[] value = subResult.split(":");
                map.put(value[0], value[1]);
            }
            list.add(map);
        }
        return list;
    }

    private String decode(String str) throws Exception {
        if (str != null) {
            str = URLDecoder.decode(str, "UTF-8");
        }
        return str;
    }

    public String loginExecute2() throws Exception {
        Assertion assertion = (Assertion) this.request.getSession().getAttribute("_const_cas_assertion_");
        Map<String, Object> map = assertion.getPrincipal().getAttributes();
        String userType = decode((String) map.get("comsys_usertype"));
        String teachingNumber = decode((String) map.get("comsys_cardid"));
        String studentNumber = decode((String) map.get("comsys_student_number"));
        if ("2".equals(userType)) {
            this.logtype = "teacher";
            this.id = teachingNumber;
        } else if ("1".equals(userType)) {
            this.logtype = "student";
            this.id = studentNumber;
        }
        HttpSession session = this.request.getSession();
        if (this.logtype.equals("student")) {
            session.setAttribute("logType", Const.exampaper_doubleFaced_S);
        } else if (this.logtype.equals("teacher")) {
            session.setAttribute("logType", "T");
        } else {
            this.info = "登录验证失败，登陆类型有误！";
            return "xdfLoginError";
        }
        ServletContext context = ServletActionContext.getServletContext();
        new Conffig();
        context.getRealPath("/");
        String marktype = String.valueOf(context.getAttribute("type"));
        session.setAttribute("autoreg", "1");
        if ("1".equals(marktype) || "0".equals(marktype)) {
            this.request.setAttribute("marks", marktype);
            session.setAttribute(Const.SYSTEM_TYPE, marktype);
        } else {
            String markpd = String.valueOf(context.getAttribute("type"));
            Cookie[] cookies = this.request.getCookies();
            if ((null == markpd || "".equals(markpd)) && null != cookies) {
                for (Cookie c : cookies) {
                    if ("marks".equals(c.getName()) && null != c.getValue() && !"".equals(c.getValue())) {
                        this.request.setAttribute("marks", c.getValue());
                        session.setAttribute(Const.SYSTEM_TYPE, c.getValue());
                    }
                }
            }
        }
        try {
            Date expiredDate = (Date) context.getAttribute("expiredDate");
            int surplusdays = DateUtil.authonelicense_zy(expiredDate);
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            session.setAttribute("licensedays", Integer.valueOf(surplusdays));
            session.setAttribute("licensedate", dateFormater.format(expiredDate));
            session.setAttribute(Const.task_rownum, "T");
            this.user.setUsername(this.id.trim());
            this.user.setRealname(this.name);
            if (this.logtype.equals("student")) {
                this.user.setPassword("111111");
            } else if (this.logtype.equals("teacher")) {
                this.user.setPassword("000000");
            }
            User u = new User();
            String dmjpassword = Configuration.getInstance().getAc01();
            if (dmjpassword.equals("null")) {
                dmjpassword = "dmj";
            }
            if ("dmj".equals(this.user.getUsername()) && dmjpassword.equals(getMHA512(this.user.getPassword()))) {
                u.setId("-2");
                u.setUserid("0");
                u.setUsername("dmj");
                u.setPassword("dmj");
                u.setUsertype("0");
                u.setLoginname("dmj");
                u.setRealname("dmj");
                u.setIsUser("T");
                u.setIsDelete("F");
            } else {
                u = this.userService.getUserByUsername(this.user, (String) session.getAttribute("logType"));
                if ("".equals(u) || null == u || "".equals(u.getId())) {
                    if (this.logtype.equals("student")) {
                        this.info = "未查询到此学生信息";
                        session.removeAttribute(Const.LOGIN_USER);
                        return "xdfLoginError";
                    }
                    if (this.logtype.equals("teacher")) {
                        this.tt.addTeacher(this.id, this.name, "", "", "", "", "-1", DateUtil.getCurrentTime(), "-1", "", this.email);
                        u = this.userService.getUserByUsernameAndPwd(this.user, (String) session.getAttribute("logType"));
                    }
                }
            }
            Map<String, String> list = this.userService.rolelist(u.getId(), (String) session.getAttribute("openocs"));
            u.setRoleName(combinRoleName(u.getId()));
            u.setIp(this.request.getRemoteAddr());
            u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
            this.request.getContextPath();
            session.setAttribute(Const.LOGIN_USER, u);
            boolean vv = Boolean.valueOf(String.valueOf(context.getAttribute(Const.school_tiltle))).booleanValue();
            session.setAttribute("list", list);
            session.setAttribute("version", Integer.valueOf(vv ? 0 : 1));
            session.setAttribute(Const.CURRENT_JIE, this.sysService.getCurrentJie());
            queryUserSex(u);
            session.setAttribute("startVal", 1);
            session.setAttribute("numVal", "");
            new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", u.getId(), this.request.getRemoteAddr(), "");
            MySessionContext.InsertOnlineUser(session);
            return Const.SUCCESS;
        } catch (Exception e) {
            session.removeAttribute(Const.LOGIN_USER);
            this.info = "请求超时，请重新登录！";
            return "xdfLoginError";
        }
    }

    public String loginExecute3() throws Exception {
        Assertion assertion = (Assertion) this.request.getSession().getAttribute("_const_cas_assertion_");
        assertion.getPrincipal().getAttributes();
        HttpSession session = this.request.getSession(true);
        String userType = session.getAttribute("userType").toString();
        String teachingNumber = session.getAttribute("teachingNumber").toString();
        String studentNumber = session.getAttribute("studentNumber").toString();
        if ("1".equals(userType)) {
            this.logtype = "teacher";
            this.id = teachingNumber;
        } else {
            if ("2".equals(userType) || "3".equals(userType)) {
                this.logtype = "student";
                this.id = studentNumber;
                this.info = "当前用户不可登陆，请联系管理员";
                return Const.sample_error_reRecognized;
            }
            if ("-26".equals(userType)) {
                this.info = "当前用户不可登陆，请联系管理员";
                return "xdfLoginError";
            }
            if ("-27".equals(userType)) {
                this.info = "用户信息同步不完全，请联系管理员";
                return "xdfLoginError";
            }
        }
        if (this.logtype.equals("student")) {
            session.setAttribute("logType", Const.exampaper_doubleFaced_S);
        } else if (this.logtype.equals("teacher")) {
            session.setAttribute("logType", "T");
        } else {
            this.info = "登录验证失败，登陆类型有误！";
            return "xdfLoginError";
        }
        ServletContext context = ServletActionContext.getServletContext();
        new Conffig();
        context.getRealPath("/");
        String marktype = String.valueOf(context.getAttribute("type"));
        session.setAttribute("autoreg", "1");
        if ("1".equals(marktype) || "0".equals(marktype)) {
            this.request.setAttribute("marks", marktype);
            session.setAttribute(Const.SYSTEM_TYPE, marktype);
        } else {
            String markpd = String.valueOf(context.getAttribute("type"));
            Cookie[] cookies = this.request.getCookies();
            if ((null == markpd || "".equals(markpd)) && null != cookies) {
                for (Cookie c : cookies) {
                    if ("marks".equals(c.getName()) && null != c.getValue() && !"".equals(c.getValue())) {
                        this.request.setAttribute("marks", c.getValue());
                        session.setAttribute(Const.SYSTEM_TYPE, c.getValue());
                    }
                }
            }
        }
        try {
            Date expiredDate = (Date) context.getAttribute("expiredDate");
            int surplusdays = DateUtil.authonelicense_zy(expiredDate);
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            session.setAttribute("licensedays", Integer.valueOf(surplusdays));
            session.setAttribute("licensedate", dateFormater.format(expiredDate));
            session.setAttribute(Const.task_rownum, "T");
            this.user.setUsername(this.id.trim());
            this.user.setRealname(this.name);
            if (this.logtype.equals("student")) {
                this.user.setPassword("111111");
            } else if (this.logtype.equals("teacher")) {
                this.user.setPassword("dmjzyleq");
            }
            new User();
            String dmjpassword = Configuration.getInstance().getAc01();
            if (dmjpassword.equals("null")) {
            }
            User u = this.userService.getUserByUsername(this.user, (String) session.getAttribute("logType"));
            Map<String, String> list = this.userService.rolelist(u.getId(), (String) session.getAttribute("openocs"));
            u.setRoleName(combinRoleName(u.getId()));
            u.setIp(this.request.getRemoteAddr());
            u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
            this.request.getContextPath();
            session.setAttribute(Const.LOGIN_USER, u);
            boolean vv = Boolean.valueOf(String.valueOf(context.getAttribute(Const.school_tiltle))).booleanValue();
            session.setAttribute("list", list);
            session.setAttribute("version", Integer.valueOf(vv ? 0 : 1));
            session.setAttribute(Const.CURRENT_JIE, this.sysService.getCurrentJie());
            queryUserSex(u);
            session.setAttribute("startVal", 1);
            session.setAttribute("numVal", "");
            new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", u.getId(), this.request.getRemoteAddr(), "");
            String openocs = (String) context.getAttribute("openOcs");
            session.setAttribute("openocs", openocs);
            MySessionContext.InsertOnlineUser(session);
            return Const.SUCCESS;
        } catch (Exception e) {
            session.removeAttribute(Const.LOGIN_USER);
            this.info = "请求超时，请重新登录！";
            return "xdfLoginError";
        }
    }

    public String loginExecute4() throws Exception {
        String codeString = this.request.getParameter("code");
        if (null == codeString) {
            this.response.sendRedirect("http://cloud.57class.net/schoolcloud-oauthserver/oauth/authorize?response_type=code&client_id=jxzdyzljc&redirect_uri=http://58.118.228.6:8888/edei/loginAction!loginExecute4.action");
            return null;
        }
        String parm = "&grant_type=authorization_code&code=" + codeString + "&redirect_uri=http://58.118.228.6:8888/edei/loginAction!loginExecute4.action&client_id=jxzdyzljc&client_secret=865c73cd99b34c0a8184d59565661953";
        JSONObject codejson = HttpRequestUtil1.getRequestByAPI("http://cloud.57class.net/schoolcloud-oauthserver/oauth/token", parm);
        if (codejson == null) {
            this.response.sendRedirect("http://cloud.57class.net/schoolcloud-oauthserver/oauth/authorize?response_type=code&client_id=jxzdyzljc&redirect_uri=http://58.118.228.6:8888/edei/loginAction!loginExecute4.action");
            return null;
        }
        String access_token = codejson.get("access_token").toString();
        String parm2 = "&access_token=" + access_token + "";
        JSONObject codejson2 = HttpRequestUtil1.getRequestByAPI("http://cloud.57class.net/schoolcloud-oauthserver//api/userinfo", parm2);
        this.logtype = codejson2.get("userType").toString();
        this.id = codejson2.get("username").toString();
        this.name = codejson2.get("name").toString();
        HttpSession session = this.request.getSession();
        session.setAttribute("logType", this.logtype);
        if ("教师".equals(this.logtype)) {
            this.logtype = "teacher";
        } else if ("学生".equals(this.logtype)) {
            this.logtype = "student";
        } else {
            this.info = "登录验证失败，登陆类型有误！";
            return "wqLoginError";
        }
        if (this.logtype.equals("student")) {
            session.setAttribute("logType", Const.exampaper_doubleFaced_S);
        } else if (this.logtype.equals("teacher")) {
            session.setAttribute("logType", "T");
        } else {
            this.info = "登录验证失败，登陆类型有误！";
            return "wqLoginError";
        }
        ServletContext context = ServletActionContext.getServletContext();
        new Conffig();
        context.getRealPath("/");
        String marktype = String.valueOf(context.getAttribute("type"));
        session.setAttribute("autoreg", "1");
        if ("1".equals(marktype) || "0".equals(marktype)) {
            this.request.setAttribute("marks", marktype);
            session.setAttribute(Const.SYSTEM_TYPE, marktype);
        } else {
            String markpd = String.valueOf(context.getAttribute("type"));
            Cookie[] cookies = this.request.getCookies();
            if ((null == markpd || "".equals(markpd)) && null != cookies) {
                for (Cookie c : cookies) {
                    if ("marks".equals(c.getName()) && null != c.getValue() && !"".equals(c.getValue())) {
                        this.request.setAttribute("marks", c.getValue());
                        session.setAttribute(Const.SYSTEM_TYPE, c.getValue());
                    }
                }
            }
        }
        try {
            Date expiredDate = (Date) context.getAttribute("expiredDate");
            int surplusdays = DateUtil.authonelicense_zy(expiredDate);
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            session.setAttribute("licensedays", Integer.valueOf(surplusdays));
            session.setAttribute("licensedate", dateFormater.format(expiredDate));
            session.setAttribute(Const.task_rownum, "T");
            this.user.setUsername(this.id.trim());
            this.user.setRealname(this.name);
            if (this.logtype.equals("student")) {
                this.user.setPassword("111111");
            } else if (this.logtype.equals("teacher")) {
                this.user.setPassword("000000");
            }
            User u = new User();
            String dmjpassword = Configuration.getInstance().getAc01();
            if (dmjpassword.equals("null")) {
                dmjpassword = "dmj";
            }
            if ("dmj".equals(this.user.getUsername()) && dmjpassword.equals(getMHA512(this.user.getPassword()))) {
                u.setId("-2");
                u.setUserid("0");
                u.setUsername("dmj");
                u.setPassword("dmj");
                u.setUsertype("0");
                u.setLoginname("dmj");
                u.setRealname("dmj");
                u.setIsUser("T");
                u.setIsDelete("F");
            } else {
                u = this.userService.getUserByUsername(this.user, (String) session.getAttribute("logType"));
                if ("".equals(u) || null == u || "".equals(u.getId())) {
                    if (this.logtype.equals("student")) {
                        this.info = "未查询到此学生信息";
                        session.removeAttribute(Const.LOGIN_USER);
                        return "wqLoginError";
                    }
                    if (this.logtype.equals("teacher")) {
                        this.tt.addTeacher(this.id, this.name, "", "", "", "", "-1", DateUtil.getCurrentTime(), "-1", "", this.email);
                        u = this.userService.getUserByUsernameAndPwd(this.user, (String) session.getAttribute("logType"));
                    }
                }
            }
            Map<String, String> list = this.userService.rolelist(u.getId(), (String) session.getAttribute("openocs"));
            u.setRoleName(combinRoleName(u.getId()));
            u.setIp(this.request.getRemoteAddr());
            u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
            this.request.getContextPath();
            session.setAttribute(Const.LOGIN_USER, u);
            session.setAttribute("list", list);
            session.setAttribute("version", String.valueOf(context.getAttribute(Const.school_limit)));
            session.setAttribute(Const.CURRENT_JIE, this.sysService.getCurrentJie());
            queryUserSex(u);
            session.setAttribute("startVal", 1);
            session.setAttribute("numVal", "");
            new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", u.getId(), this.request.getRemoteAddr(), "");
            MySessionContext.InsertOnlineUser(session);
            return Const.SUCCESS;
        } catch (Exception e) {
            session.removeAttribute(Const.LOGIN_USER);
            this.info = "请求超时，请重新登录！";
            return "wqLoginError";
        }
    }

    public String loginExecute5() throws Exception {
        String serverName = this.request.getServerName();
        String contextPath = this.request.getContextPath();
        int port = this.request.getServerPort();
        String codeString = this.request.getParameter("code");
        if (null == codeString) {
            this.response.sendRedirect("http://" + serverName + ":" + port + contextPath + "/loginAction.action");
            return null;
        }
        String parm = "key=" + codeString + "";
        JSONObject codejson = HttpRequestUtil1.getRequestByAPI("http://www.k6kt.com/enshi/user/sso/check.do", parm);
        this.id = codejson.get("code").toString();
        this.name = codejson.get("name").toString();
        if (this.id.equals("500")) {
            this.response.sendRedirect("http://" + serverName + ":" + port + contextPath + "/loginAction.action");
            return null;
        }
        JSONObject messageJson = codejson.getJSONObject("message");
        this.logtype = messageJson.get("r").toString();
        this.id = messageJson.get("code").toString();
        if (this.id.equals("")) {
            this.response.sendRedirect("http://" + serverName + ":" + port + contextPath + "/loginAction.action");
            return null;
        }
        HttpSession session = this.request.getSession();
        session.setAttribute("logType", this.logtype);
        if ("2".equals(this.logtype)) {
            this.logtype = "teacher";
            session.setAttribute("logType", "T");
        } else if ("1".equals(this.logtype)) {
            this.logtype = "student";
            session.setAttribute("logType", Const.exampaper_doubleFaced_S);
        } else {
            this.info = "登录验证失败，登陆类型有误！";
            return "wqLoginError";
        }
        ServletContext context = ServletActionContext.getServletContext();
        new Conffig();
        context.getRealPath("/");
        String marktype = String.valueOf(context.getAttribute("type"));
        session.setAttribute("autoreg", "1");
        if ("1".equals(marktype) || "0".equals(marktype)) {
            this.request.setAttribute("marks", marktype);
            session.setAttribute(Const.SYSTEM_TYPE, marktype);
        } else {
            String markpd = String.valueOf(context.getAttribute("type"));
            Cookie[] cookies = this.request.getCookies();
            if ((null == markpd || "".equals(markpd)) && null != cookies) {
                for (Cookie c : cookies) {
                    if ("marks".equals(c.getName()) && null != c.getValue() && !"".equals(c.getValue())) {
                        this.request.setAttribute("marks", c.getValue());
                        session.setAttribute(Const.SYSTEM_TYPE, c.getValue());
                    }
                }
            }
        }
        try {
            Date expiredDate = (Date) context.getAttribute("expiredDate");
            int surplusdays = DateUtil.authonelicense_zy(expiredDate);
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            session.setAttribute("licensedays", Integer.valueOf(surplusdays));
            session.setAttribute("licensedate", dateFormater.format(expiredDate));
            session.setAttribute(Const.task_rownum, "T");
            this.user.setUsername(this.id.trim());
            this.user.setRealname(this.name);
            if (this.logtype.equals("student")) {
                this.user.setPassword("111111");
            } else if (this.logtype.equals("teacher")) {
                this.user.setPassword("000000");
            }
            User u = new User();
            String dmjpassword = Configuration.getInstance().getAc01();
            if (dmjpassword.equals("null")) {
                dmjpassword = "dmj";
            }
            if ("dmj".equals(this.user.getUsername()) && dmjpassword.equals(getMHA512(this.user.getPassword()))) {
                u.setId("-2");
                u.setUserid("0");
                u.setUsername("dmj");
                u.setPassword("dmj");
                u.setUsertype("0");
                u.setLoginname("dmj");
                u.setRealname("dmj");
                u.setIsUser("T");
                u.setIsDelete("F");
            } else {
                u = this.userService.getUserByUsername(this.user, (String) session.getAttribute("logType"));
                if ("".equals(u) || null == u || "".equals(u.getId())) {
                    if (this.logtype.equals("student")) {
                        this.info = "未查询到此学生信息";
                        session.removeAttribute(Const.LOGIN_USER);
                        return "wqLoginError";
                    }
                    if (this.logtype.equals("teacher")) {
                        this.info = "未查询到此教师信息";
                        session.removeAttribute(Const.LOGIN_USER);
                        return "wqLoginError";
                    }
                }
            }
            Map<String, String> list = this.userService.rolelist(u.getId(), (String) session.getAttribute("openocs"));
            u.setRoleName(combinRoleName(u.getId()));
            u.setIp(this.request.getRemoteAddr());
            u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
            this.request.getContextPath();
            session.setAttribute(Const.LOGIN_USER, u);
            session.setAttribute("list", list);
            session.setAttribute("version", String.valueOf(context.getAttribute(Const.school_limit)));
            session.setAttribute(Const.CURRENT_JIE, this.sysService.getCurrentJie());
            queryUserSex(u);
            session.setAttribute("startVal", 1);
            session.setAttribute("numVal", "");
            new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", u.getId(), this.request.getRemoteAddr(), "");
            MySessionContext.InsertOnlineUser(session);
            return Const.SUCCESS;
        } catch (Exception e) {
            session.removeAttribute(Const.LOGIN_USER);
            this.info = "请求超时，请重新登录！";
            return "wqLoginError";
        }
    }

    public String loginExecute6() {
        String accessToken = this.request.getParameter("accessToken");
        Map<String, String> form = new TreeMap<>();
        form.put("appKey", "491243957158674432");
        form.put("timestamp", String.valueOf(System.currentTimeMillis()));
        form.put("accessToken", accessToken);
        SongDaSSOUtil songDaSSOUtil = new SongDaSSOUtil();
        String sign = songDaSSOUtil.sign(form, "eb06b16c9da54fdfbfddf47cd1245ef3");
        form.put("sign", sign);
        String responses = songDaSSOUtil.doPostJson("http://192.168.30.14:8071/api/auth/m/user/info", form);
        SongDaSSO s = (SongDaSSO) JSON.parseObject(responses, SongDaSSO.class);
        if (s.getCode() != null && s.getCode().equals("200")) {
            HttpSession session = this.request.getSession();
            SongDaSSOUser songDaSSOUser = s.getData();
            String userId = songDaSSOUser.getUserId();
            String userNo = songDaSSOUser.getUserNo();
            String state = songDaSSOUser.getState();
            String identity = songDaSSOUser.getIdentity();
            if (userId == null || userId.equals("") || userNo == null || userNo.equals("")) {
                this.info = "登录验证失败，用户名密码错误！";
                return "wqLoginError";
            }
            if (state != null && state.equals("0")) {
                this.info = "登录验证失败，该用户已被禁用！";
                return "wqLoginError";
            }
            if ("101".equals(identity) || "104".equals(identity) || "105".equals(identity)) {
                this.logtype = "teacher";
                session.setAttribute("logType", "T");
            } else if ("102".equals(identity)) {
                this.logtype = "student";
                session.setAttribute("logType", Const.exampaper_doubleFaced_S);
            } else {
                this.info = "登录验证失败，登陆类型有误！";
                return "wqLoginError";
            }
            User user = this.userService.getUserByTeacherNum(userNo, identity);
            if (user == null) {
                this.info = "登录验证失败，查无此用户！";
                return "wqLoginError";
            }
            ServletContext context = ServletActionContext.getServletContext();
            new Conffig();
            context.getRealPath("/");
            String marktype = String.valueOf(context.getAttribute("type"));
            session.setAttribute("autoreg", "1");
            if ("1".equals(marktype) || "0".equals(marktype)) {
                this.request.setAttribute("marks", marktype);
                session.setAttribute(Const.SYSTEM_TYPE, marktype);
            } else {
                String markpd = String.valueOf(context.getAttribute("type"));
                Cookie[] cookies = this.request.getCookies();
                if ((null == markpd || "".equals(markpd)) && null != cookies) {
                    for (Cookie c : cookies) {
                        if ("marks".equals(c.getName()) && null != c.getValue() && !"".equals(c.getValue())) {
                            this.request.setAttribute("marks", c.getValue());
                            session.setAttribute(Const.SYSTEM_TYPE, c.getValue());
                        }
                    }
                }
            }
            try {
                Date expiredDate = (Date) context.getAttribute("expiredDate");
                int surplusdays = DateUtil.authonelicense_zy(expiredDate);
                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
                session.setAttribute("licensedays", Integer.valueOf(surplusdays));
                session.setAttribute("licensedate", dateFormater.format(expiredDate));
                session.setAttribute(Const.task_rownum, "T");
                String id = user.getId() + "";
                String name = user.getRealname();
                String email = user.getEmail();
                if (this.logtype.equals("student")) {
                    user.setPassword("111111");
                } else if (this.logtype.equals("teacher")) {
                    user.setPassword("000000");
                }
                User u = new User();
                String dmjpassword = Configuration.getInstance().getAc01();
                if (dmjpassword.equals("null")) {
                    dmjpassword = "dmj";
                }
                if ("dmj".equals(user.getUsername()) && dmjpassword.equals(getMHA512(user.getPassword()))) {
                    u.setId("-2");
                    u.setUserid("0");
                    u.setUsername("dmj");
                    u.setPassword("dmj");
                    u.setUsertype("0");
                    u.setLoginname("dmj");
                    u.setRealname("dmj");
                    u.setIsUser("T");
                    u.setIsDelete("F");
                } else {
                    u = this.userService.getUserByUsername(user, (String) session.getAttribute("logType"));
                    if ("".equals(u) || null == u || "".equals(u.getId())) {
                        if (this.logtype.equals("student")) {
                            this.info = "未查询到此学生信息";
                            session.removeAttribute(Const.LOGIN_USER);
                            return "xdfLoginError";
                        }
                        if (this.logtype.equals("teacher")) {
                            this.tt.addTeacher(id, name, "", "", "", "", "-1", DateUtil.getCurrentTime(), "-1", "", email);
                            u = this.userService.getUserByUsernameAndPwd(user, (String) session.getAttribute("logType"));
                        }
                    }
                }
                Map<String, String> list = this.userService.rolelist(u.getId(), (String) session.getAttribute("openocs"));
                u.setRoleName(combinRoleName(u.getId()));
                u.setIp(this.request.getRemoteAddr());
                u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
                this.request.getContextPath();
                session.setAttribute(Const.LOGIN_USER, u);
                boolean vv = Boolean.valueOf(String.valueOf(context.getAttribute(Const.school_tiltle))).booleanValue();
                session.setAttribute("list", list);
                session.setAttribute("version", Integer.valueOf(vv ? 0 : 1));
                session.setAttribute(Const.CURRENT_JIE, this.sysService.getCurrentJie());
                queryUserSex(u);
                session.setAttribute("startVal", 1);
                session.setAttribute("numVal", "");
                new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", u.getId(), this.request.getRemoteAddr(), "");
                MySessionContext.InsertOnlineUser(session);
                return Const.SUCCESS;
            } catch (Exception e) {
                session.removeAttribute(Const.LOGIN_USER);
                this.info = "请求超时，请重新登录！";
                return "xdfLoginError";
            }
        }
        this.info = "登录验证失败，状态码错误！";
        return "wqLoginError";
    }

    public String loginExecute7() {
        String path = this.request.getContextPath();
        String basePath = this.request.getScheme() + "://" + this.request.getServerName() + ":" + this.request.getServerPort() + path + "/";
        String code = this.request.getParameter("code");
        String redirect_url = URLEncoder.encode(basePath + "loginAction!loginExecute7.action");
        if (code != null && !code.equals("")) {
            String authorization = Base64.encode("86071e04cad14ff1bbc93c9aef781ae6zJhu:ca9bbcecd1754fa4985f79f52fe70a83zJhugPUHap4mxmHS");
            String param = "&grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirect_url;
            String[] header = {"Authorization", authorization};
            String access_token = HttpRequestUtil1.getRequestByAPI2("http://www.essz.cn:60003/core/connect/token", param, header, "1").get("access_token") + "";
            if (access_token == null || access_token.equals("") || access_token.equals("null")) {
                this.info = "access_token获取失败，未知错误发生！";
                return "wqLoginError";
            }
            header[1] = access_token;
            HttpSession session = this.request.getSession();
            session.setAttribute("logType", this.logtype);
            JSONObject codejson = HttpRequestUtil1.getRequestByAPI2("http://www.essz.cn:60003/core/connect/userinfo", "", header, "2");
            String str = codejson.get("uniqueid") + "";
            String account = codejson.get("account").toString();
            User user = this.userService.getUserByAccount(account);
            String userType = user.getUsertype();
            if (user == null) {
                this.info = "登录验证失败，查无此用户！";
                return "wqLoginError";
            }
            if (userType.equals("1") || userType.equals("0")) {
                this.logtype = "teacher";
                session.setAttribute("logType", "T");
            } else if (userType.equals("2")) {
                this.logtype = "student";
                session.setAttribute("logType", Const.exampaper_doubleFaced_S);
            } else {
                this.info = "登录验证失败，登陆类型有误！";
                return "wqLoginError";
            }
            ServletContext context = ServletActionContext.getServletContext();
            new Conffig();
            context.getRealPath("/");
            String marktype = String.valueOf(context.getAttribute("type"));
            session.setAttribute("autoreg", "1");
            if ("1".equals(marktype) || "0".equals(marktype)) {
                this.request.setAttribute("marks", marktype);
                session.setAttribute(Const.SYSTEM_TYPE, marktype);
            } else {
                String markpd = String.valueOf(context.getAttribute("type"));
                Cookie[] cookies = this.request.getCookies();
                if ((null == markpd || "".equals(markpd)) && null != cookies) {
                    for (Cookie c : cookies) {
                        if ("marks".equals(c.getName()) && null != c.getValue() && !"".equals(c.getValue())) {
                            this.request.setAttribute("marks", c.getValue());
                            session.setAttribute(Const.SYSTEM_TYPE, c.getValue());
                        }
                    }
                }
            }
            try {
                Date expiredDate = (Date) context.getAttribute("expiredDate");
                int surplusdays = DateUtil.authonelicense_zy(expiredDate);
                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
                session.setAttribute("licensedays", Integer.valueOf(surplusdays));
                session.setAttribute("licensedate", dateFormater.format(expiredDate));
                session.setAttribute(Const.task_rownum, "T");
                String id = user.getId() + "";
                String name = user.getRealname();
                String email = user.getEmail();
                if (this.logtype.equals("student")) {
                    user.setPassword("111111");
                } else if (this.logtype.equals("teacher")) {
                    user.setPassword("000000");
                }
                User u = new User();
                String dmjpassword = Configuration.getInstance().getAc01();
                if (dmjpassword.equals("null")) {
                    dmjpassword = "dmj";
                }
                if ("dmj".equals(user.getUsername()) && dmjpassword.equals(getMHA512(user.getPassword()))) {
                    u.setId("-2");
                    u.setUserid("0");
                    u.setUsername("dmj");
                    u.setPassword("dmj");
                    u.setUsertype("0");
                    u.setLoginname("dmj");
                    u.setRealname("dmj");
                    u.setIsUser("T");
                    u.setIsDelete("F");
                } else {
                    u = this.userService.getUserByUsername(user, (String) session.getAttribute("logType"));
                    if ("".equals(u) || null == u || "".equals(u.getId())) {
                        if (this.logtype.equals("student")) {
                            this.info = "未查询到此学生信息";
                            session.removeAttribute(Const.LOGIN_USER);
                            return "xdfLoginError";
                        }
                        if (this.logtype.equals("teacher")) {
                            this.tt.addTeacher(id, name, "", "", "", "", "-1", DateUtil.getCurrentTime(), "-1", "", email);
                            u = this.userService.getUserByUsernameAndPwd(user, (String) session.getAttribute("logType"));
                        }
                    }
                }
                Map<String, String> list = this.userService.rolelist(u.getId(), (String) session.getAttribute("openocs"));
                u.setRoleName(combinRoleName(u.getId()));
                u.setIp(this.request.getRemoteAddr());
                u.setSchoolName(String.valueOf(context.getAttribute(Const.school_tiltle)));
                this.request.getContextPath();
                session.setAttribute(Const.LOGIN_USER, u);
                boolean vv = Boolean.valueOf(String.valueOf(context.getAttribute(Const.school_tiltle))).booleanValue();
                session.setAttribute("list", list);
                session.setAttribute("version", Integer.valueOf(vv ? 0 : 1));
                session.setAttribute(Const.CURRENT_JIE, this.sysService.getCurrentJie());
                queryUserSex(u);
                session.setAttribute("startVal", 1);
                session.setAttribute("numVal", "");
                new CommonUtil().addLog(this.request.getRequestURI(), "登录系统", u.getId(), this.request.getRemoteAddr(), "");
                MySessionContext.InsertOnlineUser(session);
                return Const.SUCCESS;
            } catch (Exception e) {
                session.removeAttribute(Const.LOGIN_USER);
                this.info = "请求超时，请重新登录！";
                return "xdfLoginError";
            }
        }
        try {
            this.response.sendRedirect("http://www.essz.cn:60003/core/connect/authorize?client_id=86071e04cad14ff1bbc93c9aef781ae6zJhu&scope=openid profile&response_type=code&redirect_uri=" + redirect_url);
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            this.info = "重定向失败，未知错误发生！";
            return "wqLoginError";
        }
    }

    public String doLoginOut() throws IOException {
        String loginName = new CommonUtil().getLoginUserName(this.request);
        String dirPath = this.request.getRealPath("/");
        String srcName = "images_" + loginName + "/";
        deleteFileAndDirect(dirPath, srcName);
        deleteZipAllImage();
        HttpSession session = this.request.getSession(false);
        if (null != session) {
            session.invalidate();
        }
        String logoutUrl = this.base + SingleLoginUtil.getLogoutUrl();
        this.response.sendRedirect(logoutUrl);
        return null;
    }

    public void queryUserSex(User u) {
        HttpSession session = this.request.getSession(false);
        String sex = this.userService.queryUserSex(u);
        session.setAttribute("sex", sex);
    }

    public void yhAndwh() {
        HttpSession session = this.request.getSession(false);
        String markpd = (String) session.getAttribute(Const.SYSTEM_TYPE);
        if (null != markpd && !"".equals(markpd)) {
            session.setAttribute(Const.SYSTEM_TYPE, this.mark);
            this.out.write(this.mark);
        }
        this.out.write(this.mark);
    }

    public void sessionCreated(HttpSessionEvent se) {
    }

    public void sessionDestroyed(HttpSessionEvent se) {
    }

    public String combinRoleName(String userNum) {
        String name = "";
        if ("-1".equals(userNum)) {
            name = "超级管理员";
        } else {
            List<Role> roles = this.userService.getRolesByUserNum(userNum);
            HttpSession session = this.request.getSession(true);
            session.setAttribute("rolelist", roles);
            if (null == roles || roles.size() <= 0) {
                return "普通用户";
            }
            for (int i = 0; i < roles.size(); i++) {
                Role rolea = roles.get(i);
                name = name + rolea.getRoleName();
                if (i < roles.size() - 1) {
                    name = name + ", ";
                }
            }
        }
        return name;
    }

    public void deleteZipAllImage() {
        String loginName = new CommonUtil().getLoginUserName(this.request);
        String aa = this.request.getRealPath("/");
        String zipFileName = aa.replace('\\', '/') + "图片_" + loginName + ".zip";
        File zipFile = new File(zipFileName);
        if (zipFile.exists()) {
            zipFile.delete();
        }
    }

    public void deleteFileAndDirect(String basicDir, String srcName) {
        File dir = new File(basicDir + srcName);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files.length == 0) {
                deleteEmptyDir(dir);
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    deleteSingleFile_file(files[i].toString());
                } else {
                    deleteDirect(files[i]);
                }
            }
            deleteEmptyDir(dir);
        }
    }

    public void deleteDirect(File dird) {
        if (dird.isDirectory()) {
            File[] files = dird.listFiles();
            if (files.length == 0) {
                deleteEmptyDir(dird);
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    deleteSingleFile_file(files[i].toString());
                } else {
                    deleteDirect(files[i]);
                }
            }
            deleteEmptyDir(dird);
        }
    }

    public void deleteSingleFile_file(String srcName) {
        File file = new File(srcName);
        file.delete();
    }

    public void deleteEmptyDir(File dir) {
        if (dir.isDirectory() && dir.listFiles().length == 0) {
            dir.delete();
        }
    }

    public void queryMarktype() {
        String marktype;
        HttpSession session = this.request.getSession(false);
        String marktype2 = (String) session.getAttribute(Const.SYSTEM_TYPE);
        String logtype = (String) session.getAttribute("logType");
        if (null == marktype2 && "T".equals(logtype)) {
            session.setAttribute(Const.SYSTEM_TYPE, "1");
            marktype = "0";
        } else {
            marktype = "1";
        }
        this.out.write(marktype);
    }

    public void query() {
        String marktype;
        HttpSession session = this.request.getSession(false);
        String marktype2 = (String) session.getAttribute("mark2");
        String logtype = (String) session.getAttribute("logType");
        if (null == marktype2 && "T".equals(logtype)) {
            session.setAttribute("mark2", "1");
            marktype = "0";
        } else {
            marktype = "1";
        }
        this.out.write(marktype);
    }

    public String toMarkHtml() {
        return Const.SYSTEM_TYPE;
    }

    public String getonlineuserinfo() {
        return "onlineuserinfo";
    }

    public void getonlineuser() {
        ServletContext context = this.request.getSession().getServletContext();
        List<OnlineUser> list = (List) context.getAttribute(Const.onLineUserList);
        int count = 0;
        if (list != null) {
            count = list.size();
        }
        this.out.write(Convert.toStr(Integer.valueOf(count), "0"));
    }

    public void getonlineuserlist() {
        this.request.getParameter(" ");
        String index = this.request.getParameter("index");
        if (index == null) {
            index = "0";
        }
        ServletContext context = this.request.getSession().getServletContext();
        List<OnlineUser> list = (List) context.getAttribute(Const.onLineUserList);
        int start = Convert.toInt(index, 0).intValue();
        if (start > list.size() - 1) {
            start = list.size() - 1;
        }
        String json = JSONArray.fromObject(list.subList(start, Math.min(list.size(), start + this.pageSize))).toString();
        this.out.write(json);
    }

    public void studentRelation() {
        new ArrayList();
        List relation = this.userService.getRelation();
        String str = JSONArray.fromObject(relation).toString();
        this.out.write(str);
    }

    public void schoolname() {
        new ArrayList();
        List schoolname = this.userService.getallSchoolName();
        String str = JSONArray.fromObject(schoolname).toString();
        this.out.write(str);
    }

    public void setRegisterCodeSession() {
        String mobile = this.request.getParameter("mobile").trim();
        String code = this.request.getParameter("code").trim();
        HttpSession session = this.request.getSession(true);
        session.setAttribute("mobile", mobile);
        session.setAttribute("code", code);
        session.setAttribute("sendTime", Long.valueOf(new Date().getTime()));
        RspMsg rspMsg = new RspMsg(200, "存放验证码成功！", null);
        this.out.write(net.sf.json.JSONObject.fromObject(rspMsg).toString());
    }

    public void register() {
        RspMsg rspMsg;
        new Student();
        String schoolNum = this.request.getParameter(Const.EXPORTREPORT_schoolNum).trim();
        String mobile = this.request.getParameter("mobile").trim();
        String password = this.request.getParameter("password").trim();
        String parentName = this.request.getParameter("parentName").trim();
        String uCode = this.request.getParameter("code").trim();
        String userId = this.request.getParameter("userId").trim();
        HttpSession session = this.request.getSession(true);
        String smobile = String.valueOf(session.getAttribute("mobile"));
        String sCode = String.valueOf(session.getAttribute("code"));
        if ("null".equals(sCode)) {
            RspMsg rspMsg2 = new RspMsg(491, "请先获取验证码！", null);
            this.out.write(net.sf.json.JSONObject.fromObject(rspMsg2).toString());
            return;
        }
        float sendTime = Float.parseFloat(String.valueOf(session.getAttribute("sendTime")));
        float uTime = (float) new Date().getTime();
        float gqtime = uTime - sendTime;
        if (gqtime > 300000.0f) {
            RspMsg rspMsg3 = new RspMsg(492, "验证码已过期，请重新获取！", null);
            this.out.write(net.sf.json.JSONObject.fromObject(rspMsg3).toString());
            return;
        }
        if (!sCode.equals(uCode)) {
            RspMsg rspMsg4 = new RspMsg(493, "验证码输入错误，请重新输入或获取验证码！", null);
            this.out.write(net.sf.json.JSONObject.fromObject(rspMsg4).toString());
            return;
        }
        session.removeAttribute("mobile");
        session.removeAttribute("code");
        session.removeAttribute("sendTime");
        if (!smobile.equals(mobile)) {
            RspMsg rspMsg5 = new RspMsg(494, "当前手机号与验证码不匹配，请使用当前手机号重新获取验证码！", null);
            this.out.write(net.sf.json.JSONObject.fromObject(rspMsg5).toString());
            return;
        }
        Userparent userparent1 = new Userparent();
        userparent1.setId(GUID.getGUIDStr());
        userparent1.setSchoolnum(Integer.valueOf(schoolNum));
        userparent1.setUserid(userId);
        userparent1.setStudentRelation("");
        userparent1.setUsername(mobile);
        userparent1.setPassword(password);
        userparent1.setUsertype("3");
        userparent1.setRealname(parentName);
        userparent1.setMobile(mobile);
        userparent1.setEmail("");
        userparent1.setInstertUser(GUID.getGUIDStr());
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormater.format(new Date());
        userparent1.setInsertDate(date);
        userparent1.setIsUser("F");
        userparent1.setIsDelete("F");
        userparent1.setAutoreg(0);
        int m = this.userService.save(userparent1).intValue();
        if (m > 0) {
            int n = this.userService.saveuserrole(userparent1).intValue();
            if (n > 0) {
                new HashMap();
                Map<String, Object> student = this.userDao.getStudentById(userId);
                rspMsg = new RspMsg(200, "注册成功！", student);
            } else {
                rspMsg = new RspMsg(490, "注册失败！", null);
            }
        } else {
            rspMsg = new RspMsg(490, "注册失败！", null);
        }
        this.out.write(net.sf.json.JSONObject.fromObject(rspMsg).toString());
    }

    public void checkUserMobileGroup() {
        RspMsg rspMsg;
        String userId = this.request.getParameter("userId");
        String mobile = this.request.getParameter("mobile");
        new ArrayList();
        List<Userparent> upList = this.appLogin.findUserParentListByUserId(userId);
        if (null != upList && upList.size() > 1) {
            rspMsg = new RspMsg(490, "该学生所绑定的家长数量已达到最大值2个了，不能再绑定了", null);
        } else if (null != upList && upList.size() == 1) {
            if (upList.get(0).getMobile().equals(mobile)) {
                rspMsg = new RspMsg(490, "手机号" + mobile + "已和该学生绑定过了,不能多次绑定注册", null);
            } else {
                rspMsg = new RspMsg(200, "", null);
            }
        } else {
            rspMsg = new RspMsg(200, "", null);
        }
        this.out.write(net.sf.json.JSONObject.fromObject(rspMsg).toString());
    }

    public void checkUserMobileGroupOnEdit() {
        String userId = this.request.getParameter("userId");
        String oldMobile = this.request.getParameter("oldMobile");
        String mobile = this.request.getParameter("mobile");
        RspMsg rspMsg = new RspMsg(200, "", null);
        new ArrayList();
        List<Userparent> upList = this.appLogin.findUserParentListByUserId(userId);
        if (null != upList && upList.size() > 1) {
            if (!oldMobile.equals(mobile)) {
                int i = 0;
                while (true) {
                    if (i < upList.size()) {
                        if (mobile.equals(upList.get(i).getMobile())) {
                            rspMsg = new RspMsg(490, "手机号" + mobile + "已和该学生绑定过了,不能多次绑定注册", null);
                            break;
                        } else if (!oldMobile.equals(upList.get(i).getMobile()) || upList.size() - 1 <= 1) {
                            i++;
                        } else {
                            rspMsg = new RspMsg(490, "修改后，该学生所绑定的家长数量将超过最大值2个，不能绑定", null);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            } else {
                rspMsg = new RspMsg(490, "修改后，该学生所绑定的家长数量将超过最大值2个，不能绑定", null);
            }
        } else if (null != upList && upList.size() == 1) {
            if (!oldMobile.equals(mobile)) {
                rspMsg = upList.get(0).getMobile().equals(mobile) ? new RspMsg(490, "手机号" + mobile + "已和该学生绑定过了,不能多次绑定注册", null) : new RspMsg(200, "", null);
            }
        } else {
            rspMsg = new RspMsg(200, "", null);
        }
        this.out.write(net.sf.json.JSONObject.fromObject(rspMsg).toString());
    }

    public void addParent() {
        RspMsg rspMsg;
        HttpSession session = this.request.getSession(true);
        User u = (User) session.getAttribute(Const.LOGIN_USER);
        String schoolNum = this.request.getParameter(Const.EXPORTREPORT_schoolNum);
        String userId = this.request.getParameter("userId");
        String parentName = this.request.getParameter("parentName");
        String mobile = this.request.getParameter("mobile");
        String password = this.request.getParameter("password");
        String email = this.request.getParameter("email");
        String studentRelation = this.request.getParameter("studentRelation");
        Userparent userparent = new Userparent();
        userparent.setId(GUID.getGUIDStr());
        userparent.setSchoolnum(Integer.valueOf(schoolNum));
        userparent.setUserid(userId);
        userparent.setStudentRelation(studentRelation);
        userparent.setUsername(mobile);
        if (null == password || "".equals(password)) {
            String password2 = this.userService.getParentPasswordByMobile(mobile);
            if ("".equals(password2)) {
                userparent.setPassword("111111");
            } else {
                userparent.setPassword(password2);
            }
        } else {
            userparent.setPassword(password);
        }
        userparent.setUsertype("3");
        userparent.setRealname(parentName);
        userparent.setMobile(mobile);
        userparent.setEmail(email);
        userparent.setInstertUser(u.getUserid());
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormater.format(new Date());
        userparent.setInsertDate(date);
        userparent.setIsUser("F");
        userparent.setIsDelete("F");
        userparent.setAutoreg(0);
        int m = this.userService.save(userparent).intValue();
        if (m > 0) {
            int n = this.userService.saveuserrole(userparent).intValue();
            if (n > 0) {
                rspMsg = new RspMsg(200, "添加成功！", null);
            } else {
                rspMsg = new RspMsg(490, "添加失败！", null);
            }
        } else {
            rspMsg = new RspMsg(490, "添加失败！", null);
        }
        this.out.write(net.sf.json.JSONObject.fromObject(rspMsg).toString());
    }

    public void updateParent() {
        RspMsg rspMsg;
        HttpSession session = this.request.getSession(true);
        User u = (User) session.getAttribute(Const.LOGIN_USER);
        String id = this.request.getParameter("userParentId");
        String schoolNum = this.request.getParameter(Const.EXPORTREPORT_schoolNum);
        String userId = this.request.getParameter("userId");
        String parentName = this.request.getParameter("parentName");
        String mobile = this.request.getParameter("mobile");
        String password = this.request.getParameter("password");
        String email = this.request.getParameter("email");
        String studentRelation = this.request.getParameter("studentRelation");
        Userparent userparent = new Userparent();
        userparent.setId(id);
        userparent.setSchoolnum(Integer.valueOf(schoolNum));
        userparent.setUserid(userId);
        userparent.setStudentRelation(studentRelation);
        userparent.setUsername(mobile);
        userparent.setPassword(password);
        userparent.setUsertype("3");
        userparent.setRealname(parentName);
        userparent.setMobile(mobile);
        userparent.setEmail(email);
        userparent.setInstertUser(u.getUserid());
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormater.format(new Date());
        userparent.setInsertDate(date);
        userparent.setIsUser("F");
        userparent.setIsDelete("F");
        userparent.setAutoreg(0);
        int n = this.userService.update(userparent).intValue();
        if (n > 0) {
            rspMsg = new RspMsg(200, "修改成功！", null);
            this.log.info("success: userid:" + u.getUserid() + "于 " + new Date() + " 修改一位家长信息成功，家长id为：" + id);
        } else {
            rspMsg = new RspMsg(490, "修改成功！", null);
            this.log.info("fail: userid:" + u.getUserid() + "于 " + new Date() + " 修改一位家长信息失败，家长id为：" + id);
        }
        this.out.write(net.sf.json.JSONObject.fromObject(rspMsg).toString());
    }

    public void testHessian(int id, String areasystemid) throws Exception {
        HttpSession serssion = this.request.getSession(true);
        String publicip = (String) serssion.getAttribute("publicip");
        String url = publicip + "/ocst/hessian/service";
        HessianProxyFactory factory = new HessianProxyFactory();
        PermissionService basic = (PermissionService) factory.create(PermissionService.class, url);
        String studentid = this.userService.getStudentidByid(id);
        List<String> jurisdiction = basic.getStudentPermission(studentid, areasystemid);
        if (null == jurisdiction) {
            this.signle = 0;
        } else {
            this.signle = 1;
        }
        HttpSession session = this.request.getSession(true);
        session.setAttribute("jurisdiction", jurisdiction);
    }

    public void getorderinfowarning(int id, String areasystemid, int day) throws Exception {
        HttpSession serssion = this.request.getSession(true);
        String publicip = (String) serssion.getAttribute("publicip");
        String url = publicip + "/ocst/hessian/service";
        HessianProxyFactory factory = new HessianProxyFactory();
        PermissionService basic = (PermissionService) factory.create(PermissionService.class, url);
        String studentid = this.userService.getStudentidByid(id);
        Object orderinfo = basic.get_studentTime_warn(areasystemid, studentid, Integer.valueOf(day));
        HttpSession session = this.request.getSession(true);
        session.setAttribute("orderinfo", orderinfo);
    }

    public void getorderinfo() throws Exception {
        ServletContext context = ServletActionContext.getServletContext();
        HttpSession serssion = this.request.getSession(true);
        String publicip = (String) serssion.getAttribute("publicip");
        String url = publicip + "/ocst/hessian/service";
        context.getRealPath("/");
        String start = this.request.getParameter("start");
        String end = this.request.getParameter("end");
        HessianProxyFactory factory = new HessianProxyFactory();
        String id = this.request.getParameter("id");
        int id1 = Integer.valueOf(id).intValue();
        PermissionService basic = (PermissionService) factory.create(PermissionService.class, url);
        String studentid = this.userService.getStudentidByid(id1);
        HttpSession session = this.request.getSession(true);
        List allorderinfo = basic.getClientOrders((String) session.getAttribute("areasystemid"), studentid, start, end, "0");
        String str = JSONArray.fromObject(allorderinfo).toString();
        this.out.write(str);
    }

    public void getstudentNum() {
        try {
            String userid = this.request.getParameter("userid");
            List studentnum = this.userService.getStudentNum(userid);
            String str = JSONArray.fromObject(studentnum).toString();
            this.out.write(str);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public String warning() {
        return "warn";
    }

    public String continue_harge() {
        return "continue_charge";
    }

    public String conn_charge() {
        return "conn_charge";
    }

    public String conn_charge2() {
        return "conn_charge2";
    }

    public String conn_charge3() {
        return "conn_charge3";
    }

    public String topcharge() {
        return "conn_charge4";
    }

    public String sfPrompt() {
        return "sfPrompt";
    }

    public void gethession() throws Exception {
        ServletContext context = ServletActionContext.getServletContext();
        HttpSession session = this.request.getSession(true);
        List<String> hh = (List) session.getAttribute("jurisdiction");
        String hhh = JSONArray.fromObject(hh).toString();
        this.out.write(hhh);
    }

    public void getschoolname() {
        try {
            String schoolnum = this.request.getParameter("schoolnum");
            new ArrayList();
            List getschool = this.userService.getSchool(schoolnum);
            String str = JSONArray.fromObject(getschool).toString();
            this.out.write(str);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void getlicensedays(int surplusdays) {
        try {
            ServletContext context = ServletActionContext.getServletContext();
            HttpSession session = this.request.getSession(true);
            String filePathli = (String) context.getAttribute(Const.project_path);
            License lic = CreateFile.getLicense(CreateFile.getLicPath(filePathli));
            Date date = lic.getExpiredDate();
            session.setAttribute("licensedays", Integer.valueOf(surplusdays));
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            String expiredDate = dateFormater.format(date);
            session.setAttribute("licensedate", expiredDate);
        } catch (Exception e) {
        }
    }

    public String licensewarning() {
        return "licensewarming";
    }

    public void getteacherInfo() {
        ServletContext context = ServletActionContext.getServletContext();
        String userNum = this.request.getParameter("userNum");
        try {
            List teacherList = this.userService.getTeacherInfo(userNum, context);
            String str = JSONArray.fromObject(teacherList).toString();
            this.out.write(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void couldUseOnBind() {
        RspMsg rspMsg;
        String mobile = this.request.getParameter("mobile");
        String userType = this.request.getParameter("userType");
        String userTypeInfo = "";
        if ("T".equals(userType)) {
            userTypeInfo = "教师";
        } else if (Const.exampaper_doubleFaced_S.equals(userType)) {
            userTypeInfo = "学生";
        }
        boolean flag = this.userService.couldUseOnBind(mobile, userType);
        if (flag) {
            rspMsg = new RspMsg(200, "true", null);
        } else {
            rspMsg = new RspMsg(Const.height_500, "该手机号已被其他" + userTypeInfo + "用户绑定，请换一个手机号", null);
        }
        this.out.write(net.sf.json.JSONObject.fromObject(rspMsg).toString());
    }

    public void phoneCheck() {
        HttpSession session = this.request.getSession(true);
        Object mobile = this.request.getParameter("mobile");
        Object basePath = this.request.getScheme() + "://" + this.request.getServerName() + ":" + this.request.getServerPort() + this.request.getContextPath() + "/";
        Map<String, Object> map = new HashMap<>();
        map.put("phone", mobile);
        map.put("type", "家长");
        map.put("templateNum", "4");
        map.put("server", basePath);
        try {
            String url = StaticClassResources.EdeiInfo.getAsUrl() + "/sms/get";
            RspMsg msg = WebUtil.sendSmsByPost(url, JSONObject.toJSONString(map));
            if (msg.getCode() == 200) {
                session.setAttribute("mobile", mobile);
                session.setAttribute("code", msg.getData());
                session.setAttribute("sendTime", Long.valueOf(new Date().getTime()));
            }
            this.out.write(msg.getMsg());
        } catch (Exception e) {
            this.out.write("0");
            e.printStackTrace();
        }
    }

    public void teacherLoginCode() {
        HttpSession session = this.request.getSession(true);
        Object mobile = String.valueOf(session.getAttribute("tcmoble"));
        if ("".equals(mobile) || "null".equals(mobile)) {
            mobile = this.request.getParameter("phoneNum");
        }
        Object basePath = this.request.getScheme() + "://" + this.request.getServerName() + ":" + this.request.getServerPort() + this.request.getContextPath() + "/";
        Map<String, Object> map = new HashMap<>();
        map.put("phone", mobile);
        map.put("type", "教师");
        map.put("templateNum", "1");
        map.put("server", basePath);
        try {
            String url = StaticClassResources.EdeiInfo.getAsUrl() + "/sms/get";
            RspMsg msg = WebUtil.sendSmsByPost(url, net.sf.json.JSONObject.fromObject(map).toString());
            session.setAttribute("mobile", mobile);
            session.setAttribute("tcode", msg.getData());
            session.setAttribute("sendTime", Long.valueOf(new Date().getTime()));
            this.out.write(msg.getCode() == 200 ? "发送成功" : msg.getMsg());
        } catch (Exception e) {
            this.out.write("0");
            e.printStackTrace();
        }
    }

    public void teacherCodeCheck() throws SQLException {
        HttpSession session = this.request.getSession(true);
        String sCode = String.valueOf(session.getAttribute("tcode"));
        String fp = String.valueOf(session.getAttribute("fp"));
        String wos = String.valueOf(session.getAttribute("wos"));
        User u = (User) session.getAttribute("tcuser");
        String uCode = String.valueOf(this.request.getParameter("uCode"));
        String type = String.valueOf(this.request.getParameter("type"));
        if (null == sCode) {
            this.out.write("8");
            return;
        }
        float sendTime = Float.parseFloat(String.valueOf(session.getAttribute("sendTime")));
        float uTime = (float) new Date().getTime();
        float gqtime = uTime - sendTime;
        String mobile = String.valueOf(session.getAttribute("mobile"));
        if (gqtime > 300000.0f) {
            this.out.write("8");
            return;
        }
        if (!sCode.equals(uCode)) {
            this.out.write("6");
            return;
        }
        session.removeAttribute("mobile");
        session.removeAttribute("tccode");
        session.removeAttribute("sendTime");
        session.removeAttribute("tcdisplymoble");
        this.sysService.delTeacherRecord(String.valueOf(u.getId()));
        this.sysService.insertTeacherRecord(String.valueOf(u.getId()), u.getUsername(), u.getUsertype(), wos, fp, "");
        if ("1".equals(type)) {
            this.userService.addTeacherPhoneNum(String.valueOf(u.getUserid()), mobile);
            this.userService.updateUserMobile(String.valueOf(u.getUserid()), type, mobile);
        } else if ("2".equals(type)) {
            this.userService.updateUserMobile(String.valueOf(u.getUserid()), type, mobile);
        }
    }

    public void getParentsStudent() {
        HttpSession session = this.request.getSession(true);
        new User();
        User parentUser = (User) session.getAttribute(Const.LOGIN_PARENTUSER);
        List<Student> stu = this.userService.getParentsStudent(parentUser.getMobile());
        this.out.write(JSONArray.fromObject(stu).toString());
    }

    public String zkLogin() throws IOException {
        String code = this.request.getParameter("code");
        String basePath = this.request.getScheme() + "://" + this.request.getServerName() + ":" + this.request.getServerPort() + this.request.getContextPath() + "/";
        String casServer = ZkhySingleLoginUtil.getServer();
        String client_id = ZkhySingleLoginUtil.getClintId();
        String client_secret = ZkhySingleLoginUtil.getClintSecret();
        String redirect_uri = basePath + "loginAction.action";
        String tokenUrl = StrUtil.format("{}/oauth2.0/accessToken", new Object[]{casServer});
        Map<String, Object> paramMap = StreamMap.create().put("client_id", (Object) client_id).put("client_secret", (Object) client_secret).put("grant_type", (Object) "authorization_code").put("redirect_uri", (Object) redirect_uri).put("code", (Object) code);
        String tokenBody = HttpUtil.post(tokenUrl, paramMap);
        Map<String, Object> paramMap2 = (Map) JSON.parseObject(tokenBody, new HashMap().getClass());
        String profileUrl = StrUtil.format("{}/oauth2.0/profile", new Object[]{casServer});
        String body = HttpUtil.post(profileUrl, paramMap2);
        Map<String, Object> map = (Map) JSON.parseObject(body, new HashMap().getClass());
        String username = map.get("id").toString();
        String callbackUrl = map.get("service").toString();
        String logType = "";
        switch (2) {
            case 1:
                logType = Const.exampaper_doubleFaced_S;
                break;
            case 2:
                logType = "T";
                break;
            case Const.Pic_Score /* 3 */:
                logType = "P";
                break;
        }
        String url = StrUtil.format("{}?user.username={}&user.password=xxxxxx&logType={}&user.loginType={}", new Object[]{callbackUrl, username, logType, "0"});
        this.request.getSession().setAttribute("singleLogin", "1");
        this.response.sendRedirect(url);
        return null;
    }

    public String toZkhyLogin() throws IOException {
        String callbackUrl = this.base + "login!zkLogin.action";
        String url = StrUtil.format("{}/oauth2.0/authorize?response_type=code&&client_id={}&redirect_uri={}", new Object[]{ZkhySingleLoginUtil.getServer(), ZkhySingleLoginUtil.getClintId(), callbackUrl});
        this.response.sendRedirect(url);
        return null;
    }

    public String toZkhyLogout() throws IOException {
        String callbackUrl = this.base + SingleLoginUtil.getBaseLoginUrl();
        this.request.getSession(false).invalidate();
        String url = StrUtil.format("{}/logout?service={}", new Object[]{ZkhySingleLoginUtil.getServer(), callbackUrl});
        this.response.sendRedirect(url);
        return null;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMark() {
        return this.mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSessionCode() {
        return this.sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getAuthcode_val() {
        return this.authcode_val;
    }

    public void setAuthcode_val(String authcode_val) {
        this.authcode_val = authcode_val;
    }

    public String getIdStr() {
        return this.idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPagestart() {
        return this.pagestart;
    }

    public void setPagestart(int pagestart) {
        this.pagestart = pagestart;
    }

    public int getCurrenpage() {
        return this.currenpage;
    }

    public void setCurrenpage(int currenpage) {
        this.currenpage = currenpage;
    }

    public boolean countv() throws Exception {
        License lic = CreateFile.getLicense(Util.getPath());
        String version = lic.getVersion();
        String s = version.substring(0, 1);
        if (s.equals(Const.VERSION_tag)) {
            this.log.debug(" 区版登陆......");
            return true;
        }
        this.log.debug(" 学校版登陆......");
        return false;
    }

    public String stuzhenduan() {
        String num = this.request.getParameter("num");
        HttpSession session = this.request.getSession(true);
        session.setAttribute("num", num);
        return "stuzhenduan";
    }

    public String subzhenduan() {
        String sigggg = this.request.getParameter("sigggg");
        String num = this.request.getParameter("num");
        HttpSession session = this.request.getSession(true);
        session.setAttribute("num", num);
        session.setAttribute("sigggg", sigggg);
        return "subzhenduan";
    }

    public String stufanwenyu() {
        String subjectNum = this.request.getParameter(Const.EXPORTREPORT_subjectNum);
        HttpSession session = this.request.getSession(true);
        String num = this.request.getParameter("num");
        session.setAttribute(Const.EXPORTREPORT_subjectNum, subjectNum);
        session.setAttribute("num", num);
        return "stufanwenyu";
    }

    public String stufanwenEn() {
        String subjectNum = this.request.getParameter(Const.EXPORTREPORT_subjectNum);
        HttpSession session = this.request.getSession(true);
        String num = this.request.getParameter("num");
        session.setAttribute(Const.EXPORTREPORT_subjectNum, subjectNum);
        session.setAttribute("num", num);
        return "stufanwenEn";
    }

    public String stushua() {
        return "stushua";
    }

    public String stuTest() {
        return "stutest";
    }

    public String toMobileLogin() {
        return "mobileLogin";
    }

    public void getTried() {
        ServletContext servletContext = this.request.getSession().getServletContext();
        String systemid = (String) servletContext.getAttribute("systemId");
        String userId = ((User) this.session.get(Const.LOGIN_USER)).getId();
        String studentId = this.userService.getStudentIdByUserId(userId);
        RspMsg msg = sfHelper.getTried(systemid, studentId);
        this.out.write(JSON.toJSONString(msg));
    }

    public String toOneupdatepass() {
        User user;
        new User();
        HttpSession session = this.request.getSession(true);
        if ("P".equals(session.getAttribute("logType"))) {
            user = (User) session.getAttribute(Const.LOGIN_PARENTUSER);
        } else {
            user = (User) session.getAttribute(Const.LOGIN_USER);
        }
        this.request.setAttribute("user", user);
        return "oneupdatepass";
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getSignle() {
        return this.signle;
    }

    public void setSignle(int signle) {
        this.signle = signle;
    }
}
