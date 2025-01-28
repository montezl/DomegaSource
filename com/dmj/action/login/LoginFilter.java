package com.dmj.action.login;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.dmj.action.base.BaseAction;
import com.dmj.cs.bean.RequestVisitor;
import com.dmj.domain.User;
import com.dmj.service.userManagement.PermissonService;
import com.dmj.serviceimpl.userManagement.PermissonServiceImpl;
import com.dmj.util.Const;
import com.zht.db.ServiceFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/* loaded from: LoginFilter.class */
public class LoginFilter extends BaseAction implements Filter {
    protected FilterConfig filterConfig;
    public static String LASTTIME = "lastTime";
    public static String NumCorrectByClient = "/numCorrect!numCorrectByClient.action";
    public static String LOGIN_EXECUTE2 = "/loginAction!execute2.action";
    public static String LOGIN_PAGE = "/alogin.jsp";
    public static String LOGIN_Action = "/login!";
    public static String DMJ_LOGIN_PAGE = "/dmjLogin.jsp";
    public static String REGISTER = "/register.jsp";
    public static String APPREGISTER = "/app/student/appRegister.jsp";
    public static String StudentAppHuanXing = "/app/huanxing/student/index.jsp";
    public static String APP_PAYURL = "appIndex!getPayUrl2.action";
    public static String RESETPW = "/resetPw.jsp";
    public static String LOGIN_REGISTER = "/loginAction!register.action";
    public static String LOGIN_PHONECHECK = "/loginAction!phoneCheck.action";
    public static String LOGIN_TEACHERLOGIN = "/loginAction!teacherLoginCode.action";
    public static String LOGIN_TEACHERCODE = "/loginAction!teacherCodeCheck.action";
    public static String LOGIN_COULDU_SER_ON_BIND = "/loginAction!couldUseOnBind.action";
    public static String LOGIN_SCHOOL = "/loginAction!schoolname.action";
    public static String LOGIN_Relation = "/loginAction!studentRelation.action";
    public static String LOGIN_INDEX = "/jsp/main/index.jsp";
    public static String NO_PERMISSION = "/index.jsp";
    public static String NO_PERMISSION2 = "/indexiframe.jsp";
    public static String SESSION_OUT = "/pages/error.jsp?str=session超时,请重新登录";
    public static String LOGIN_PAGE_FRAME = "/edei/";
    public static String TOPFRAME = "/jsp/main/topframe.jsp";
    public static String TEACHLEFTMENU = "/jsp/main/teachleftMenu.jsp";
    public static String MAINFRAME = "/jsp/main/mainframe.jsp";
    public static String LOGIN_ACTION = "/loginAction.action";
    public static String LOGOUT_ACTION = "/jsp/main/loginAction!loginOut.action";
    public static String LOGIN_ACTION_INFOLOGIN = "/jsp/main/loginAction!infoLogin.action";
    public static String LOGIN_ACTION_DEFAULT = "/loginAction!default.action";
    public static String RANDPIC = "/randPic.action";
    public static String RANDPIC_GETS = "/randPic!getS.action";
    public static String SAVE_IMG = "/awardPointAction!caitu.action";
    public static String TEST_STEAL_QUESTION = "/awardPointAction!testStealQuestion.action";
    public static String MASK_IMG = "awardPointAction!caituPaperCommt.action";
    public static String SAVE_PAPERCOMMTIMG = "/paperCommentActionTwo!exportPic.action";
    public static String GET_IMG = "/awardPointAction!getImage.action";
    public static String GET_FullIMG = "/imageAction!getImageByFullUrl.action";
    public static String confAction = "/conffig!gettypeValue.action";
    public static String ieload = "/loadDateDbAction!loadIe.action";
    public static String loginQueryUserName = "/userAction!loginQueryUserName.action";
    public static String tologinQueryUserName = "/userAction!tologinQueryUserName.action";
    public static String yhAndwh = "/loginAction!yhAndwh.action";
    public static String loginschool = "/userAction!queryschool.action";
    public static String oneupdatepwd = "/loginAction!loginstart.action";
    public static String allotActionquery = "/allotAction!doDefault2.action";
    public static String allotActionsub = "/allotAction!submit2.action";
    public static String zkhyLogin = "/login!zkLogin.action";
    public static String toZkhyLogin = "/login!toZkhyLogin.action";
    public static String loginExecute = "/loginAction!loginExecute.action";
    public static String loginExecute2 = "/loginAction!loginExecute2.action";
    public static String loginExecute3 = "/loginAction!loginExecute3.action";
    public static String loginExecute4 = "/loginAction!loginExecute4.action";
    public static String loginExecute5 = "/loginAction!loginExecute5.action";
    public static String loginExecute6 = "/loginAction!loginExecute6.action";
    public static String hefengSync = "/HFManage!syncHefeng.action";
    public static String loginExecute7 = "/loginAction!loginExecute7.action";
    public static String getOnlineUser = "/loginAction!getonlineuser.action";
    public static String ctb2 = "/jsp/ctb2/ctb.jsp";
    public static String visit_ImageServer = "imageAction";
    public static String visit_ImageWidthHeight = "stest";
    public static String visit_systemMonitor = "sysInfo";
    public static String DOWNLOADCSV = "downloadCSV";
    public static String DOWNLOADCSVSERVER = "downloadCSVServer";
    public static String HTTP_PIC = "HPAction";
    public static String APPWEX = "appIndexWeX";
    public static String DaTiKa = "datika!";
    public static String APP = "appIndex";
    public static String TeacherAPP = "teacherApp";
    public static String TeacherAction = "/teacherAction!register.action";
    public static String stupaperimg = "stupaperimg";
    public static String RESET_ISUSERPARENTEXIST = "updatePassowrd";
    public static String UPDATELOG_PAGE = "/updateLog.jsp";
    public static String CONTACT = "/teacherAction!getNoticeInfo.action";
    public static String CONTACT1 = "/teacherAction!gotoNotice.action";
    public static String ApplyTemplate = "/imgCheck!applyTemplate.action";
    public static String ImgCheck = "/imgCheck!getCheckedImageInfo.action";
    public static String GetMissingPaperList = "/imgCheck!getMissingPaperList.action";
    public static String test = "/test!updateBarCode.action";
    public static String ctbPayPageAction = "/ctbPayPageAction";
    public static String poolMonitor = "/poolMonitor";
    public static String edeiInfoMonitor = "/edeiInfoMonitor";
    public static String sysResourceMonitor = "/sysResourceMonitor";
    public static String replaceFiles = "/replaceFiles";
    public static String examCopy = "/examCopy";
    public static String scannerClient = "/scannerClient";
    public static String appAction = "/app!";
    public static String csJsp = "/jsp/cs/";
    public static String note = "/note!getContactPerson.action";
    public static String LOGIN_SET_REGISTER_CODE = "/loginAction!setRegisterCodeSession.action";
    public static String clip_qtype = "/nMark!clip_qtype.action";
    final String key = "RequestVisitorKey";
    Logger log = Logger.getLogger(getClass());
    PermissonService permission = (PermissonService) ServiceFactory.getObject(new PermissonServiceImpl());
    Const cont = new Const();
    List<String> excludeCheckSessionList = Arrays.asList("/jsp/main/imgCheck!getImageCheckCount.action", "/jsp/main/loginAction!getonlineuser.action");

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        HttpSession session = req.getSession();
        try {
            RequestVisitor visitor = (RequestVisitor) session.getAttribute("RequestVisitorKey");
            if (visitor != null && DateUtil.between(DateUtil.parse(visitor.getLastVisitorTime()), DateTime.now(), DateUnit.SECOND) > session.getMaxInactiveInterval()) {
                session.invalidate();
            }
        } catch (Exception e) {
        }
        if (!this.excludeCheckSessionList.contains(uri.replace(req.getContextPath(), ""))) {
            RequestVisitor.write((HttpServletRequest) request);
        }
        String loginType = req.getParameter("user.loginType");
        String useraa = "";
        if ("0".equals(loginType)) {
            useraa = req.getParameter("user.username");
            session.setAttribute("userName", useraa);
        } else if ("1".equals(loginType)) {
            useraa = req.getParameter("user.mobile");
            session.setAttribute("mobile", useraa);
        }
        String password = req.getParameter("user.password");
        String origin = req.getParameter("origin");
        session.setAttribute("password", password);
        User info = (User) session.getAttribute(Const.LOGIN_USER);
        String vmPath = ((HttpServletRequest) request).getContextPath();
        session.setAttribute("vmPath", vmPath);
        String webAppRootKey = ((HttpServletRequest) request).getRealPath("/");
        System.setProperty("webapp.root", webAppRootKey);
        if (((vmPath + LOGIN_PAGE).equals(uri) || (vmPath + LOGIN_ACTION).equals(uri) || (vmPath + LOGIN_EXECUTE2).equals(uri) || uri.equals(vmPath + "/")) && null != info) {
            resp.sendRedirect(vmPath + "/jsp/main/indexiframe.jsp");
            return;
        }
        if ((((vmPath + LOGIN_ACTION).equals(uri) && !"appServer".equals(origin)) || (vmPath + LOGIN_EXECUTE2).equals(uri)) && null == info && (useraa == null || useraa.trim().equals("") || password == null || password.trim().equals(""))) {
            resp.sendRedirect(vmPath + LOGIN_PAGE);
            return;
        }
        if ((vmPath + DMJ_LOGIN_PAGE).equals(uri) || (vmPath + LOGIN_PAGE).equals(uri) || vmPath.equals(uri) || (vmPath + LOGIN_INDEX).equals(uri) || uri.startsWith(vmPath + LOGIN_Action) || (vmPath + NO_PERMISSION2).equals(uri) || TOPFRAME.equals(uri) || (vmPath + LOGIN_ACTION).equals(uri) || (vmPath + TOPFRAME).equals(uri) || (vmPath + TEACHLEFTMENU).equals(uri) || (vmPath + MAINFRAME).equals(uri) || (vmPath + LOGOUT_ACTION).equals(uri) || (vmPath + RANDPIC).equals(uri) || (vmPath + RANDPIC_GETS).equals(uri) || (vmPath + SAVE_IMG).equals(uri) || (vmPath + SAVE_PAPERCOMMTIMG).equals(uri) || (vmPath + TEST_STEAL_QUESTION).equals(uri) || (vmPath + GET_FullIMG).equals(uri) || (vmPath + GET_IMG).equals(uri) || (vmPath + confAction).equals(uri) || (vmPath + ieload).equals(uri) || (vmPath + loginQueryUserName).equals(uri) || (vmPath + tologinQueryUserName).equals(uri) || (vmPath + yhAndwh).equals(uri) || (vmPath + loginschool).equals(uri) || (vmPath + allotActionquery).equals(uri) || (vmPath + allotActionsub).equals(uri) || (vmPath + loginExecute).equals(uri) || (vmPath + zkhyLogin).equals(uri) || (vmPath + toZkhyLogin).equals(uri) || (vmPath + loginExecute2).equals(uri) || (vmPath + loginExecute3).equals(uri) || (vmPath + loginExecute4).equals(uri) || (vmPath + loginExecute5).equals(uri) || (vmPath + loginExecute6).equals(uri) || (vmPath + loginExecute7).equals(uri) || uri.indexOf(visit_systemMonitor) != -1 || (vmPath + REGISTER).equals(uri) || (vmPath + RESETPW).equals(uri) || (vmPath + APPREGISTER).equals(uri) || (vmPath + StudentAppHuanXing).equals(uri) || (vmPath + APP_PAYURL).equals(uri) || (vmPath + CONTACT).equals(uri) || (vmPath + CONTACT1).equals(uri) || (vmPath + LOGIN_REGISTER).equals(uri) || (vmPath + LOGIN_PHONECHECK).equals(uri) || (vmPath + LOGIN_TEACHERLOGIN).equals(uri) || (vmPath + LOGIN_TEACHERCODE).equals(uri) || (vmPath + LOGIN_COULDU_SER_ON_BIND).equals(uri) || (vmPath + hefengSync).equals(uri) || (vmPath + LOGIN_SCHOOL).equals(uri) || (vmPath + LOGIN_Relation).equals(uri) || (vmPath + NumCorrectByClient).equals(uri) || (vmPath + UPDATELOG_PAGE).equals(uri) || (vmPath + note).equals(uri) || (vmPath + clip_qtype).equals(uri) || (vmPath + LOGIN_SET_REGISTER_CODE).equals(uri) || uri.indexOf(poolMonitor) != -1 || uri.indexOf(edeiInfoMonitor) != -1 || uri.indexOf(sysResourceMonitor) != -1 || uri.indexOf(replaceFiles) != -1 || uri.indexOf(examCopy) != -1 || uri.indexOf(scannerClient) != -1 || uri.indexOf(appAction) != -1 || uri.indexOf(DaTiKa) != -1 || uri.indexOf(ImgCheck) != -1 || uri.indexOf(ApplyTemplate) != -1 || uri.indexOf(GetMissingPaperList) != -1 || uri.indexOf(test) != -1 || uri.indexOf(stupaperimg) != -1 || uri.indexOf(ctbPayPageAction) != -1) {
            chain.doFilter(req, resp);
            return;
        }
        if (uri.indexOf(visit_ImageServer) != -1 || uri.indexOf(visit_ImageWidthHeight) != -1 || uri.indexOf(HTTP_PIC) != -1 || uri.indexOf(MASK_IMG) != -1 || uri.indexOf(ctb2) != -1 || uri.indexOf(DOWNLOADCSV) != -1 || uri.indexOf(DOWNLOADCSVSERVER) != -1 || uri.indexOf(loginExecute3) != -1 || uri.indexOf(loginExecute4) != -1 || uri.indexOf(loginExecute5) != -1 || uri.indexOf(LOGIN_PHONECHECK) != -1 || uri.indexOf(LOGIN_TEACHERLOGIN) != -1 || uri.indexOf(LOGIN_TEACHERCODE) != -1 || uri.indexOf(getOnlineUser) != -1 || uri.indexOf(csJsp) != -1 || uri.indexOf(APPWEX) != -1 || uri.indexOf(APP) != -1 || uri.indexOf(TeacherAPP) != -1 || uri.indexOf(RESET_ISUSERPARENTEXIST) != -1 || uri.indexOf(visit_systemMonitor) != -1 || uri.indexOf(TeacherAction) != -1) {
            chain.doFilter(req, resp);
            return;
        }
        if (null == session) {
            if (isAjax(req)) {
                resp.getWriter().write("401");
                resp.setStatus(Const.height_500);
                return;
            } else {
                resp.sendRedirect(vmPath + LOGIN_PAGE);
                return;
            }
        }
        if (null == info) {
            if (isAjax(req)) {
                resp.getWriter().write("301");
                resp.setStatus(Const.height_500);
                return;
            } else {
                resp.sendRedirect(vmPath + LOGIN_PAGE);
                return;
            }
        }
        if (null == info.getId()) {
            resp.sendRedirect(vmPath + LOGIN_PAGE);
            return;
        }
        Const r0 = this.cont;
        if ("-1".equals(info.getId())) {
            chain.doFilter(req, resp);
            return;
        }
        Const r02 = this.cont;
        if ("-2".equals(info.getId())) {
            chain.doFilter(req, resp);
        } else {
            chain.doFilter(req, resp);
        }
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void init(FilterConfig config) throws ServletException {
        this.filterConfig = config;
    }

    boolean isAjax(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("application/json") != -1) {
            return true;
        }
        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) {
            return true;
        }
        return false;
    }
}
