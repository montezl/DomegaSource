package com.dmj.serviceimpl.appHBuilder;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.dmj.daoimpl.appHBuilder.AppLogionDaoImpl;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.userManagement.UserDAOImpl;
import com.dmj.domain.Student;
import com.dmj.domain.User;
import com.dmj.domain.Userparent;
import com.dmj.service.appHBuilder.AppLogionService;
import com.dmj.service.astrict.AstrictService;
import com.dmj.service.examManagement.UtilSystemService;
import com.dmj.service.userManagement.UserService;
import com.dmj.serviceimpl.astrict.AstrictServiceImpl;
import com.dmj.serviceimpl.examManagement.UtilSystemServiceimpl;
import com.dmj.serviceimpl.userManagement.UserServiceImpl;
import com.dmj.util.Conffig;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.app.AppUserInfo;
import com.dmj.util.app.MobileInfo;
import com.dmj.util.config.Configuration;
import com.dmj.util.msg.RspMsg;
import com.zht.db.ServiceFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.struts2.ServletActionContext;

/* loaded from: AppLogionServiceImpl.class */
public class AppLogionServiceImpl implements AppLogionService {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    AppLogionDaoImpl appLogiondao = new AppLogionDaoImpl();
    UserDAOImpl userDao = new UserDAOImpl();
    private UtilSystemService uss = (UtilSystemService) ServiceFactory.getObject(new UtilSystemServiceimpl());
    private AstrictService astrictService = (AstrictService) ServiceFactory.getObject(new AstrictServiceImpl());
    UserService userService = (UserService) ServiceFactory.getObject(new UserServiceImpl());

    public Map<String, String> queryUserOld(String username, String password, String usertype, String token) {
        Map<String, String> map = new HashMap<>();
        if (usertype.equals("3")) {
            Object[] userData = this.appLogiondao.queryParent(username, password);
            if (userData == null || userData.length == 0) {
                map.put("code", "1");
            } else {
                Object studentId = this.appLogiondao.queryStudentIdByParentId(userData[2].toString());
                Object[] sname = this.appLogiondao.querySchoolAndClass(studentId.toString());
                if (userData[1].toString() == null && "".equals(userData[1].toString())) {
                    map.put("code", "2");
                    map.put("AccessToken", token);
                    map.put("id", userData[0].toString());
                    map.put("userType", "3");
                    map.put("password", password);
                    if (sname != null) {
                        map.put("schoolName", sname[0].toString());
                        map.put("className", sname[1].toString());
                        map.put("studentName", sname[2].toString());
                    }
                } else {
                    map.put("code", "3");
                    map.put("AccessToken", token);
                    map.put("id", userData[0].toString());
                    map.put("userType", "3");
                    map.put("password", password);
                    map.put("mobile", userData[1].toString());
                    if (sname != null) {
                        map.put("schoolName", sname[0].toString());
                        map.put("className", sname[1].toString());
                        map.put("studentName", sname[2].toString());
                    }
                }
            }
        } else {
            Object[] userData2 = this.appLogiondao.queryUser(username, password);
            if (userData2 == null || userData2.length == 0) {
                map.put("code", "4");
            } else {
                Object[] sname2 = this.appLogiondao.querySchoolAndClass(userData2[0].toString());
                if (userData2[1] == null || "".equals(userData2[1].toString())) {
                    map.put("code", "5");
                    map.put("AccessToken", token);
                    map.put("id", userData2[0].toString());
                    map.put("userType", "2");
                    map.put("password", password);
                    if (sname2 != null) {
                        map.put("schoolName", sname2[0].toString());
                        map.put("className", sname2[1].toString());
                        map.put("studentName", sname2[2].toString());
                    }
                } else {
                    map.put("code", "6");
                    map.put("AccessToken", token);
                    map.put("id", userData2[0].toString());
                    map.put("userType", "2");
                    map.put("password", password);
                    map.put("mobile", userData2[1].toString());
                    if (sname2 != null) {
                        map.put("schoolName", sname2[0].toString());
                        map.put("className", sname2[1].toString());
                        map.put("studentName", sname2[2].toString());
                    }
                }
            }
        }
        return map;
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Map<String, String> queryUser(String username, String password, String usertype, String token) throws ParseException {
        boolean continueT;
        Map<String, String> map = new HashMap<>();
        Object existParent = this.appLogiondao.isExistParent(username, usertype, (String) null);
        if (null == existParent) {
            map.put("code", "0");
        } else {
            boolean continueT2 = true;
            String userid = "";
            Integer errornum1 = 0;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String errornumSet = Configuration.getInstance().getErrorNum();
            Integer errornumSet1 = Convert.toInt(errornumSet);
            String lockTimeSet = Configuration.getInstance().getLockTime();
            Integer lockTimeSet1 = Convert.toInt(lockTimeSet);
            Map<String, Object> map2 = this.userService.getlockMap(username, "P");
            if (null != map2) {
                userid = map2.get("id").toString();
                String errornum = map2.get("errornum").toString();
                errornum1 = Convert.toInt(errornum);
                String locktime = map2.get("locktime").toString();
                if (!locktime.equals("")) {
                    new SimpleDateFormat(lockTimeSet);
                    Date locktime1 = dateFormat.parse(locktime);
                    Long locktime2 = Long.valueOf(locktime1.getTime() + (lockTimeSet1.intValue() * 60000));
                    String timenow = DateUtil.getCurrentTime();
                    Date timenow1 = dateFormat.parse(timenow);
                    Long timenow2 = Long.valueOf(timenow1.getTime());
                    if (timenow2.longValue() <= locktime2.longValue() && errornumSet1.intValue() != 0 && lockTimeSet1.intValue() != 0) {
                        String endtime = dateFormat.format(locktime2);
                        map.put("code", "4");
                        map.put("info", "密码错误次数已超限制，账号已经被锁定,请在" + endtime + "后重试！");
                        continueT2 = false;
                    } else {
                        this.userService.updateErrornum(userid, 0, "P");
                    }
                }
            }
            Object[] userData = this.appLogiondao.queryParent(username, password);
            if ((null == userData || userData.length == 0) && continueT2) {
                map.put("code", "1");
                this.userService.updateErrornum(userid, Integer.valueOf(errornum1.intValue() + 1), "P");
                if (errornum1.intValue() + 1 >= errornumSet1.intValue()) {
                    this.userService.updateLocktime(userid, DateUtil.getCurrentTime(), "P");
                }
            } else if (continueT2) {
                ServletContext context = ServletActionContext.getServletContext();
                new Conffig();
                String filePath = context.getRealPath("/");
                String astrictLogin = Conffig.getParameter(filePath, Const.astrictLogin);
                if ("1".equals(astrictLogin)) {
                    List<Map<String, Object>> mapList = this.astrictService.getAstrictLoginDataNowByPartType2("4", username);
                    if (null != mapList && !"".equals(mapList) && mapList.size() != 0) {
                        String shuoming = Convert.toStr(mapList.get(0).get("shuoming"));
                        String info = "暂不开放学生/家长查询";
                        if (null != shuoming && !shuoming.equals("") && !shuoming.equals("null")) {
                            info = shuoming;
                        }
                        map.put("code", "4");
                        map.put("info", info);
                        return map;
                    }
                    continueT = true;
                } else {
                    continueT = true;
                }
                if (continueT) {
                    Object[] sname = this.appLogiondao.querySchoolAndClass(String.valueOf(userData[0]));
                    map.put("code", "2");
                    map.put("AccessToken", token);
                    map.put("id", String.valueOf(userData[0]));
                    map.put("userType", usertype);
                    map.put("password", password);
                    map.put("mobile", username);
                    if (null != sname && sname.length > 0) {
                        map.put("schoolName", String.valueOf(sname[0]));
                        map.put("className", String.valueOf(sname[1]));
                        map.put("studentName", String.valueOf(sname[2]));
                        map.put(Const.EXPORTREPORT_studentId, String.valueOf(sname[3]));
                        map.put("xuejihao", String.valueOf(sname[4]));
                        map.put("gradeName", String.valueOf(sname[5]));
                        map.put(Const.EXPORTREPORT_schoolNum, String.valueOf(sname[6]));
                        map.put(Const.EXPORTREPORT_gradeNum, String.valueOf(sname[7]));
                        map.put(Const.EXPORTREPORT_classNum, String.valueOf(sname[8]));
                    }
                    this.userService.updateErrornum(userid, 0, "P");
                }
            }
        }
        return map;
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Map<String, String> querySMSUser(String mobile, String code, MobileInfo mobileInfo, String token, String usertype) {
        List<Map<String, Object>> mapList;
        Map<String, String> map = new HashMap<>();
        Object existParent = this.appLogiondao.isExistParent(mobile, usertype, (String) null);
        if (null == existParent) {
            map.put("code", "0");
            return map;
        }
        if (null == mobileInfo) {
            map.put("code", "1");
            return map;
        }
        long sendTime = mobileInfo.getSendTime();
        float uTime = (float) new Date().getTime();
        float gqtime = uTime - ((float) sendTime);
        if (gqtime > 300000.0f) {
            map.put("code", "2");
            return map;
        }
        if (!mobileInfo.getCode().equals(code)) {
            map.put("code", "1");
            return map;
        }
        ServletContext context = ServletActionContext.getServletContext();
        new Conffig();
        String filePath = context.getRealPath("/");
        String astrictLogin = Conffig.getParameter(filePath, Const.astrictLogin);
        if ("1".equals(astrictLogin) && null != (mapList = this.astrictService.getAstrictLoginDataNowByPartType2("4", mobile)) && !"".equals(mapList) && mapList.size() != 0) {
            String shuoming = Convert.toStr(mapList.get(0).get("shuoming"));
            String info = "暂不开放学生/家长查询";
            if (!shuoming.equals("")) {
                info = shuoming;
            }
            map.put("code", "4");
            map.put("info", info);
            return map;
        }
        Object[] sname = this.appLogiondao.querySchoolAndClass(String.valueOf(existParent));
        map.put("code", "3");
        map.put("AccessToken", token);
        map.put("id", String.valueOf(existParent));
        map.put("userType", "3");
        map.put("mobile", mobile);
        if (null != sname && sname.length > 0) {
            map.put("schoolName", String.valueOf(sname[0]));
            map.put("className", String.valueOf(sname[1]));
            map.put("studentName", String.valueOf(sname[2]));
            map.put(Const.EXPORTREPORT_studentId, String.valueOf(sname[3]));
            map.put("xuejihao", String.valueOf(sname[4]));
            map.put("gradeName", String.valueOf(sname[5]));
            map.put(Const.EXPORTREPORT_schoolNum, String.valueOf(sname[6]));
            map.put(Const.EXPORTREPORT_gradeNum, String.valueOf(sname[7]));
            map.put(Const.EXPORTREPORT_classNum, String.valueOf(sname[8]));
        }
        return map;
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Map<String, String> querySidUser(String studentId, String password, String usertype, String token) throws ParseException {
        boolean continueT;
        Map<String, String> map = new HashMap<>();
        Object existStudent = this.appLogiondao.isExistStudent(studentId, usertype);
        if (null == existStudent) {
            map.put("code", "0");
        } else {
            boolean continueT2 = true;
            String userid = "";
            Integer errornum1 = 0;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String errornumSet = Configuration.getInstance().getErrorNum();
            Integer errornumSet1 = Convert.toInt(errornumSet);
            String lockTimeSet = Configuration.getInstance().getLockTime();
            Integer lockTimeSet1 = Convert.toInt(lockTimeSet);
            Map<String, Object> map2 = this.userService.getlockMap(studentId, Const.exampaper_doubleFaced_S);
            if (null != map2) {
                userid = map2.get("id").toString();
                String errornum = map2.get("errornum").toString();
                errornum1 = Convert.toInt(errornum);
                String locktime = map2.get("locktime").toString();
                if (!locktime.equals("")) {
                    new SimpleDateFormat(lockTimeSet);
                    Date locktime1 = dateFormat.parse(locktime);
                    Long locktime2 = Long.valueOf(locktime1.getTime() + (lockTimeSet1.intValue() * 60000));
                    String timenow = DateUtil.getCurrentTime();
                    Date timenow1 = dateFormat.parse(timenow);
                    Long timenow2 = Long.valueOf(timenow1.getTime());
                    if (timenow2.longValue() <= locktime2.longValue() && errornumSet1.intValue() != 0 && lockTimeSet1.intValue() != 0) {
                        String endtime = dateFormat.format(locktime2);
                        String info = "密码错误次数已超限制，账号已经被锁定,请在" + endtime + "后重试！";
                        map.put("code", "4");
                        map.put("info", info);
                        continueT2 = false;
                    } else {
                        this.userService.updateErrornum(userid, 0, Const.exampaper_doubleFaced_S);
                    }
                } else {
                    continueT2 = true;
                }
            }
            Object[] userData = this.appLogiondao.queryStudentByPassword(studentId, password);
            if ((null == userData || userData.length == 0) && continueT2) {
                map.put("code", "1");
                if (null != map2) {
                    this.userService.updateErrornum(userid, Integer.valueOf(errornum1.intValue() + 1), Const.exampaper_doubleFaced_S);
                    if (errornum1.intValue() + 1 >= errornumSet1.intValue()) {
                        this.userService.updateLocktime(userid, DateUtil.getCurrentTime(), Const.exampaper_doubleFaced_S);
                    }
                }
            } else if (continueT2) {
                ServletContext context = ServletActionContext.getServletContext();
                new Conffig();
                String filePath = context.getRealPath("/");
                String astrictLogin = Conffig.getParameter(filePath, Const.astrictLogin);
                if ("1".equals(astrictLogin)) {
                    List<Map<String, Object>> mapList = this.astrictService.getAstrictLoginDataNowByPartType3("4", studentId);
                    if (null != mapList && !"".equals(mapList) && mapList.size() != 0) {
                        String shuoming = Convert.toStr(mapList.get(0).get("shuoming"));
                        String info2 = "暂不开放学生/家长查询";
                        if (!shuoming.equals("")) {
                            info2 = shuoming;
                        }
                        map.put("code", "4");
                        map.put("info", info2);
                        return map;
                    }
                    continueT = true;
                } else {
                    continueT = true;
                }
                if (continueT) {
                    Object[] sname = this.appLogiondao.querySchoolAndClass(String.valueOf(userData[0]));
                    map.put("code", "2");
                    map.put("AccessToken", token);
                    map.put("id", String.valueOf(userData[0]));
                    map.put("userType", usertype);
                    map.put("password", password);
                    map.put("mobile", studentId);
                    if (null != sname && sname.length > 0) {
                        map.put("schoolName", String.valueOf(sname[0]));
                        map.put("className", String.valueOf(sname[1]));
                        map.put("studentName", String.valueOf(sname[2]));
                        map.put(Const.EXPORTREPORT_studentId, String.valueOf(sname[3]));
                        map.put("xuejihao", String.valueOf(sname[4]));
                        map.put("gradeName", String.valueOf(sname[5]));
                        map.put(Const.EXPORTREPORT_schoolNum, String.valueOf(sname[6]));
                        map.put(Const.EXPORTREPORT_gradeNum, String.valueOf(sname[7]));
                        map.put(Const.EXPORTREPORT_classNum, String.valueOf(sname[8]));
                    }
                    this.userService.updateErrornum(userid, 0, Const.exampaper_doubleFaced_S);
                }
            }
        }
        return map;
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Map<String, String> querySidUserFreePwd(String studentId, String password, String usertype, String token) {
        Map<String, String> map = new HashMap<>();
        Object existStudent = this.appLogiondao.isExistStudent(studentId, usertype);
        if (null == existStudent) {
            map.put("code", "0");
        } else {
            Object[] userData = this.appLogiondao.queryStudentFreePwd(studentId);
            if (null == userData || userData.length == 0) {
                map.put("code", "1");
            } else {
                ServletContext context = ServletActionContext.getServletContext();
                new Conffig();
                String filePath = context.getRealPath("/");
                String astrictLogin = Conffig.getParameter(filePath, Const.astrictLogin);
                boolean continueT = false;
                if ("1".equals(astrictLogin)) {
                    List<Map<String, Object>> mapList = this.astrictService.getAstrictLoginDataNowByPartType3("4", studentId);
                    if (null == mapList || "".equals(mapList) || mapList.size() == 0) {
                        continueT = true;
                    } else {
                        String shuoming = Convert.toStr(mapList.get(0).get("shuoming"));
                        String info = "暂不开放学生/家长查询";
                        if (!shuoming.equals("")) {
                            info = shuoming;
                        }
                        map.put("code", "4");
                        map.put("info", info);
                    }
                } else {
                    continueT = true;
                }
                if (continueT) {
                    Object[] sname = this.appLogiondao.querySchoolAndClass(String.valueOf(userData[0]));
                    map.put("code", "2");
                    map.put("AccessToken", token);
                    map.put("id", String.valueOf(userData[0]));
                    map.put("userType", usertype);
                    map.put("password", password);
                    map.put("mobile", studentId);
                    if (null != sname && sname.length > 0) {
                        map.put("schoolName", String.valueOf(sname[0]));
                        map.put("className", String.valueOf(sname[1]));
                        map.put("studentName", String.valueOf(sname[2]));
                        map.put(Const.EXPORTREPORT_studentId, String.valueOf(sname[3]));
                        map.put("xuejihao", String.valueOf(sname[4]));
                        map.put("gradeName", String.valueOf(sname[5]));
                        map.put(Const.EXPORTREPORT_schoolNum, String.valueOf(sname[6]));
                        map.put(Const.EXPORTREPORT_gradeNum, String.valueOf(sname[7]));
                        map.put(Const.EXPORTREPORT_classNum, String.valueOf(sname[8]));
                    }
                }
            }
        }
        return map;
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Integer bindPhone(String mobile, String smscode, MobileInfo mobileInfo, AppUserInfo info) {
        if (mobileInfo == null) {
            return 5;
        }
        long sendTime = mobileInfo.getSendTime();
        float uTime = (float) new Date().getTime();
        float gqtime = uTime - ((float) sendTime);
        if (gqtime > 300000.0f) {
            return 6;
        }
        if (!mobileInfo.getCode().equals(smscode)) {
            return 5;
        }
        Integer result = this.appLogiondao.updateParentMobile(info.getUserName(), mobile, info.getUserType());
        return result;
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public String getConfig(String type, String operate) {
        return this.appLogiondao.getConfig(type, operate);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Integer addUserParent(String username, String realname, String studentId, String studentName, String email, String password, String mobile, String smscode, String userpassword, MobileInfo mobileinfo) {
        if (mobileinfo == null) {
            return 5;
        }
        long sendTime = mobileinfo.getSendTime();
        float uTime = (float) new Date().getTime();
        float gqtime = uTime - ((float) sendTime);
        if (gqtime > 300000.0f) {
            return 6;
        }
        if (!mobileinfo.getCode().equals(smscode)) {
            return 5;
        }
        Object student = this.appLogiondao.queryStudent(studentId, studentName);
        if (student == null) {
            return 1;
        }
        Object userByPwd = this.appLogiondao.queryUserByPwd(Integer.valueOf(student.toString()), userpassword);
        if (userByPwd == null) {
            return 4;
        }
        List<String> userParent = this.appLogiondao.queryUserParent(Integer.valueOf(student.toString()));
        if (userParent != null && userParent.size() > 1) {
            return 7;
        }
        Object userNameResult = this.appLogiondao.queryUserName(username);
        if (userNameResult != null) {
            return 9;
        }
        Userparent userparent = new Userparent();
        userparent.setUserid(student.toString());
        userparent.setStudentRelation("1");
        userparent.setUsername(username);
        userparent.setPassword(this.appLogiondao.getPassword(password));
        userparent.setUsertype("3");
        userparent.setRealname(realname);
        userparent.setEmail(email);
        userparent.setMobile(mobile);
        return Integer.valueOf(this.appLogiondao.addParent(userparent));
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public RspMsg register(String schoolNum, String gradeNum, String classNum, String studentName, String mobile, String password, String parentName, String code, String userId, MobileInfo mobileInfo) {
        RspMsg rspMsg;
        if (null == mobileInfo) {
            RspMsg rspMsg2 = new RspMsg(Const.height_400, "验证码错误", null);
            return rspMsg2;
        }
        long sendTime = mobileInfo.getSendTime();
        float uTime = (float) new Date().getTime();
        float gqtime = uTime - ((float) sendTime);
        if (gqtime > 300000.0f) {
            RspMsg rspMsg3 = new RspMsg(401, "验证码已过期", null);
            return rspMsg3;
        }
        if (!mobileInfo.getCode().equals(code)) {
            RspMsg rspMsg4 = new RspMsg(Const.height_400, "验证码错误", null);
            return rspMsg4;
        }
        Userparent userparent = new Userparent();
        userparent.setId(GUID.getGUIDStr());
        userparent.setSchoolnum(Integer.valueOf(schoolNum));
        userparent.setUserid(userId);
        userparent.setStudentRelation("");
        userparent.setUsername(mobile);
        userparent.setPassword(password);
        userparent.setUsertype("3");
        userparent.setRealname(parentName);
        userparent.setMobile(mobile);
        userparent.setEmail("");
        userparent.setInstertUser("0");
        userparent.setInsertDate(DateUtil.getCurrentTime());
        userparent.setIsUser("F");
        userparent.setIsDelete("F");
        userparent.setAutoreg(0);
        int result1 = this.userDao.saveUserparent(userparent).intValue();
        int result2 = this.userDao.saveuserrole(userparent).intValue();
        if (result1 > 0 && result2 > 0) {
            new HashMap();
            Map<String, Object> student = this.userDao.getStudentById(userId);
            rspMsg = new RspMsg(200, "注册成功", student);
        } else {
            rspMsg = new RspMsg(Const.height_500, "注册失败", null);
        }
        return rspMsg;
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public RspMsg Tuijianrenregister(String schoolNum, String gradeNum, String classNum, String studentName, String mobile, String password, String parentName, String code, String userId, MobileInfo mobileInfo, String tuijianren) {
        RspMsg rspMsg;
        if (null == mobileInfo) {
            RspMsg rspMsg2 = new RspMsg(Const.height_400, "验证码错误", null);
            return rspMsg2;
        }
        long sendTime = mobileInfo.getSendTime();
        float uTime = (float) new Date().getTime();
        float gqtime = uTime - ((float) sendTime);
        if (gqtime > 300000.0f) {
            RspMsg rspMsg3 = new RspMsg(401, "验证码已过期", null);
            return rspMsg3;
        }
        if (!mobileInfo.getCode().equals(code)) {
            RspMsg rspMsg4 = new RspMsg(Const.height_400, "验证码错误", null);
            return rspMsg4;
        }
        Userparent userparent = new Userparent();
        userparent.setId(GUID.getGUIDStr());
        userparent.setSchoolnum(Integer.valueOf(schoolNum));
        userparent.setUserid(userId);
        userparent.setStudentRelation("");
        userparent.setUsername(mobile);
        userparent.setPassword(password);
        userparent.setUsertype("3");
        userparent.setRealname(parentName);
        userparent.setMobile(mobile);
        userparent.setEmail("");
        userparent.setInstertUser("0");
        userparent.setInsertDate(DateUtil.getCurrentTime());
        userparent.setIsUser("F");
        userparent.setIsDelete("F");
        userparent.setAutoreg(0);
        int result1 = this.userDao.saveUserparent(userparent).intValue();
        int result2 = this.userDao.saveuserrole(userparent).intValue();
        if (result1 > 0 && result2 > 0) {
            new HashMap();
            Map<String, Object> student = this.userDao.getStudentById(userId);
            Map<String, Object> tuijianrenMap = this.userDao.getStudentByNum(tuijianren);
            List<Map<String, Object>> mapList = new ArrayList<>();
            mapList.add(student);
            mapList.add(tuijianrenMap);
            Integer integer = this.userDao.updateTuijianren(userId, tuijianren);
            if (integer.intValue() > 0) {
                rspMsg = new RspMsg(200, "注册成功", mapList);
            } else {
                rspMsg = new RspMsg(200, "注册成功,推荐人更新失败，请联系管理员", mapList);
            }
        } else {
            rspMsg = new RspMsg(Const.height_500, "注册失败", null);
        }
        return rspMsg;
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Object isExistUser(String mobile, String userType, String gradeNum) {
        return this.appLogiondao.isExistParent(mobile, userType, gradeNum);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Integer updatePassWord(String password, String mobile, String smscode, MobileInfo mobileinfo, AppUserInfo info) {
        info.getPassword();
        String username = info.getUserName();
        return this.appLogiondao.findPwd(username, password, info.getUserType());
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public RspMsg setNewPassword(String password, String mobile, String smscode, MobileInfo mobileinfo, String userType) {
        RspMsg rspMsg;
        if (null == mobileinfo) {
            RspMsg rspMsg2 = new RspMsg(Const.height_400, "验证码错误", null);
            return rspMsg2;
        }
        long sendTime = mobileinfo.getSendTime();
        float uTime = (float) new Date().getTime();
        float gqtime = uTime - ((float) sendTime);
        if (gqtime > 300000.0f) {
            RspMsg rspMsg3 = new RspMsg(401, "验证码已过期", null);
            return rspMsg3;
        }
        if (!mobileinfo.getCode().equals(smscode)) {
            RspMsg rspMsg4 = new RspMsg(Const.height_400, "验证码错误", null);
            return rspMsg4;
        }
        int count = this.appLogiondao.findPwd(mobile, password, userType).intValue();
        if (count > 0) {
            rspMsg = new RspMsg(200, "更新成功", null);
        } else {
            rspMsg = new RspMsg(Const.height_500, "更新失败", null);
        }
        return rspMsg;
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Integer findPassWord(String username, String password, String mobile, String userType, String smscode, MobileInfo mobileInfo) {
        if (mobileInfo == null) {
            return 1;
        }
        long sendTime = mobileInfo.getSendTime();
        float uTime = (float) new Date().getTime();
        float gqtime = uTime - ((float) sendTime);
        if (gqtime > 300000.0f) {
            return 2;
        }
        if (!mobileInfo.getCode().equals(smscode)) {
            return 1;
        }
        if (userType.equals("3")) {
            return this.appLogiondao.findPwd(username, password, userType);
        }
        if (!userType.equals("2")) {
            return null;
        }
        return this.appLogiondao.findUserPwd(username, password, mobile);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public String findMobileByUserName(String username, String userType) {
        Object phone = this.appLogiondao.findMobile(username, userType);
        return phone == null ? "" : phone.toString();
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public User getCodeUser(String mobile, String userType) {
        return this.appLogiondao.getCodeUser(mobile, userType);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public List<Map<String, Object>> getStudentListByUsername(String username) {
        return this.appLogiondao.getStudentListByUsername(username);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public List<Map<String, Object>> getAllSchool() {
        return this.appLogiondao.getAllSchool();
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public List<Map<String, Object>> getAllGradeBySchoolNum(String schoolNum) {
        return this.appLogiondao.getAllGradeBySchoolNum(schoolNum);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public List<Map<String, Object>> getAllClassBySchoolNumAndGradeNum(String schoolNum, String gradeNum) {
        return this.appLogiondao.getAllClassBySchoolNumAndGradeNum(schoolNum, gradeNum);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public List<Map<String, Object>> getAllstudentByclass(String schoolNum, String gradeNum, String classNum) {
        return this.appLogiondao.getAllstudentByclass(schoolNum, gradeNum, classNum);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public List<Student> findStudentListByParam(String schoolNum, String gradeNum, String classNum, String studentName) {
        return this.appLogiondao.findStudentListByParam(schoolNum, gradeNum, classNum, studentName);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public int findStudentListByPhone(String schoolNum, String mobile, String gradeNum) {
        return this.appLogiondao.findStudentListByPhone(schoolNum, mobile, gradeNum);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public List<Userparent> findUserParentListByUserId(String userid) {
        return this.appLogiondao.findUserParentListByUserId(userid);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Userparent findUserParentInfo(String mobile) {
        return this.appLogiondao.findUserParentInfo(mobile);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public Integer bindUserparent(Userparent userparent) {
        if (null == userparent) {
            return null;
        }
        return this.appLogiondao.bindUserparent(userparent);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public void unBindStudent(List<Userparent> userParentList) {
        String str;
        List<String> sqls = new ArrayList<>();
        String mobile = userParentList.get(0).getMobile();
        String ids = "";
        String userids = "";
        for (int i = 0; i < userParentList.size(); i++) {
            if (i == userParentList.size() - 1) {
                ids = ids + userParentList.get(i).getId();
                str = userids + userParentList.get(i).getUserid();
            } else {
                ids = ids + userParentList.get(i).getId() + Const.STRING_SEPERATOR;
                str = userids + userParentList.get(i).getUserid() + Const.STRING_SEPERATOR;
            }
            userids = str;
        }
        Map args = new HashMap();
        args.put("mobile", mobile);
        args.put("userids", userids);
        args.put("ids", ids);
        sqls.add("delete from userparent where mobile ={mobile} and isDelete='F' and userid in ({ids[]}) ");
        sqls.add("delete from userrole where roleNum ='-2' and isDelete='F' and userNum in ({ids[]}) ");
        this.dao2._batchExecute(sqls, args);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public List<Userparent> findUserParentListByMobileAndIds(String mobile, String ids) {
        return this.appLogiondao.findUserParentListByMobileAndIds(mobile, ids);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public List<Student> getParentsStudent(String mobile, String userid) {
        String sql = "select userp.userid id,stu.studentName,stu.studentId from userparent userp LEFT JOIN student stu on userp.userid = stu.id where userp.mobile={mobile} ";
        Map args = new HashMap();
        args.put("mobile", mobile);
        return this.dao2._queryBeanList(sql, Student.class, args);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public RspMsg checkStudentStatus(String studentId) {
        return this.appLogiondao.checkStudentStatus(studentId);
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public RspMsg isExistStudent(String studentId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_studentId, studentId);
        String sId = this.dao2._queryStr("select id from student where studentId={studentId} limit 1", args);
        if (StrUtil.isEmpty(sId)) {
            return new RspMsg(404, "该学生不存在", "0");
        }
        return new RspMsg(200, "该学生存在", "1");
    }

    @Override // com.dmj.service.appHBuilder.AppLogionService
    public void getPayUrl() {
    }
}
