package com.dmj.serviceimpl.userManagement;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.userManagement.UserDAOImpl;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Exam;
import com.dmj.domain.Grade;
import com.dmj.domain.OnlineUser;
import com.dmj.domain.Pjbdata;
import com.dmj.domain.Resource;
import com.dmj.domain.Role;
import com.dmj.domain.School;
import com.dmj.domain.Schoolscanpermission;
import com.dmj.domain.Student;
import com.dmj.domain.Studentlevel;
import com.dmj.domain.Teacher;
import com.dmj.domain.User;
import com.dmj.domain.Userparent;
import com.dmj.domain.Userrole;
import com.dmj.service.examManagement.UtilSystemService;
import com.dmj.service.questionGroup.QuestionGroupService;
import com.dmj.service.userManagement.UserService;
import com.dmj.serviceimpl.examManagement.UtilSystemServiceimpl;
import com.dmj.serviceimpl.questionGroup.QuestionGroupImpl;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.excel.CheckCellUtil;
import com.dmj.util.excel.ExcelHelper;
import com.dmj.util.msg.RspMsg;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.struts2.ServletActionContext;

/* loaded from: UserServiceImpl.class */
public class UserServiceImpl implements UserService {
    private boolean errorFlag = false;
    private boolean rowBgColor = false;
    UserDAOImpl dao = new UserDAOImpl();
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    private UtilSystemService uss = (UtilSystemService) ServiceFactory.getObject(new UtilSystemServiceimpl());
    private QuestionGroupService qgs = (QuestionGroupService) ServiceFactory.getObject(new QuestionGroupImpl());

    @Override // com.dmj.service.userManagement.UserService
    public List<User> getAllUser(User user, int start, int pagesize, String examNum, String gradeNum) {
        List<User> list;
        if (null == user || null == (list = this.dao.getAllUser(user, start, pagesize, examNum, gradeNum)) || list.size() == 0) {
            return null;
        }
        return list;
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<User> getAllUser(User user, int start, int pagesize, String examNum, String gradeNum, String positionNum) {
        List<User> list;
        if (null == user || null == (list = this.dao.getAllUser(user, start, pagesize, examNum, gradeNum, positionNum)) || list.size() == 0) {
            return null;
        }
        return list;
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<User> getLimitUser(User user, int start, int pagesize, String examNum, String gradeNum, String userId) {
        List<User> list;
        if (null == user || null == (list = this.dao.getLimitUser(user, start, pagesize, examNum, gradeNum, userId)) || list.size() == 0) {
            return null;
        }
        for (User u : list) {
            StringBuffer buffer = new StringBuffer();
            List<Role> roles = this.dao.getRolesByUserNum(u.getId());
            if (null != roles && roles.size() > 0) {
                for (Role r : roles) {
                    if (buffer.length() == 0) {
                        buffer.append(r.getRoleName());
                    } else {
                        buffer.append(Const.STRING_SEPERATOR + r.getRoleName());
                    }
                }
                u.setExt1(buffer.toString());
            }
        }
        return list;
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserById(String id) {
        if (null == id || id.equals("")) {
            return null;
        }
        return this.dao.getUserById(id);
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserByUsernameAndPwd(User user, String logType) {
        return getUserByUsernameAndPwd(user, logType, "");
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserByUsernameAndPwd(User user, String logType, String singleLogin) {
        if (null == user) {
            return null;
        }
        return this.dao.getUserByUsernameAndPwd(user, logType, singleLogin);
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserByMobile(User user) {
        if (null == user) {
            return null;
        }
        return this.dao.getUserByMobile(user);
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserByUsername(User user, String logType) {
        if (null == user) {
            return null;
        }
        return this.dao.getUserByUsername(user, logType);
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserParentByUsernameAndPwd(User user) {
        if (null == user) {
            return null;
        }
        return this.dao.getUserParentByUsernameAndPwd(user);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer save(User user) {
        if (null == user) {
            return null;
        }
        return this.dao.saveUser(user);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer update(User user) {
        return this.dao2.update(user);
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserByNum(String num) {
        return this.dao.getUserByNum(num);
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserByUserid(String num) {
        return this.dao.getUserByUserid(num);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Object getOneByNum(String colum, String valule, Class cla) throws Exception {
        return this.dao.getOneByNum(colum, valule, cla);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Role> getAllRole() {
        return this.dao.getAllRole();
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Role> getAllRole2() {
        return this.dao.getAllRole2();
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Role> getRolesByUserNum(String usernum) {
        if (null == usernum || usernum.equals("")) {
            return null;
        }
        return this.dao.getRolesByUserNum(usernum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<User> getUsersByRoleNum(String roleNum) {
        if (null == roleNum || roleNum.equals("")) {
            return null;
        }
        return this.dao.getUsersByRoleNum(roleNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer addAndDeleteUser(List<String> add, List<String> delete, Userrole ur) {
        if (null == ur || null == ur.getRoleNum() || ur.getRoleNum().equals("")) {
            return null;
        }
        if (add.size() > 0) {
            for (String uNum : add) {
                String flag = this.dao.selectUserrole(uNum, ur.getRoleNum());
                if (StrUtil.isEmpty(flag)) {
                    Userrole urole = new Userrole();
                    urole.setInsertDate(DateUtil.getCurrentDay());
                    urole.setInsertUser(ur.getInsertUser());
                    urole.setRoleNum(ur.getRoleNum());
                    urole.setSchoolNum(ur.getSchoolNum());
                    urole.setUserNum(uNum);
                    this.dao2.save(urole);
                }
            }
        }
        if (delete.size() > 0) {
            Iterator<String> it = delete.iterator();
            while (it.hasNext()) {
                Map args = StreamMap.create().put("uNum", (Object) it.next()).put("roleNum", (Object) ur.getRoleNum());
                this.dao2._execute("delete from userrole where  userNum = {uNum} and roleNum = {roleNum} ", args);
            }
        }
        return 1;
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer addAndDeleteUser2(List<String> add, List<String> delete, Userrole ur, Integer examPaperNum) {
        if (null == ur || null == ur.getRoleNum() || ur.getRoleNum().equals("")) {
            return null;
        }
        if (add.size() > 0) {
            for (String uNum : add) {
                String flag = this.dao.selectUserrole(uNum, ur.getRoleNum());
                if (StrUtil.isEmpty(flag)) {
                    Userrole urole = new Userrole();
                    urole.setInsertDate(DateUtil.getCurrentDay());
                    urole.setInsertUser(ur.getInsertUser());
                    urole.setRoleNum(ur.getRoleNum());
                    urole.setSchoolNum(ur.getSchoolNum());
                    urole.setUserNum(uNum);
                    this.dao2.save(urole);
                }
            }
        }
        if (delete.size() > 0) {
            Map args_roleNumsql = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
            Object roleNumobj = this.dao2._queryObject("select roleNum  from role where examPaperNum = {examPaperNum} and type='4'", args_roleNumsql);
            String roleNumstr = String.valueOf(roleNumobj);
            for (String uNum2 : delete) {
                this.qgs.deletetask(examPaperNum, null, uNum2, "1", "");
                Map args_sql = StreamMap.create().put("roleNumstr", (Object) roleNumstr).put("uNum", (Object) uNum2);
                this.dao2._execute("delete from userrole where roleNum={roleNumstr} and userNum={uNum}", args_sql);
            }
        }
        return 1;
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer addAndDeleteUser2New(List<String> addAnddelete, Userrole ur, Userrole ur1, Integer examPaperNum, String operatetype, String category) {
        if (null == ur || null == ur.getRoleNum() || ur.getRoleNum().equals("")) {
            return null;
        }
        if (addAnddelete != null && addAnddelete.size() != 0 && operatetype.equals("add")) {
            for (String uNum : addAnddelete) {
                String flag = this.dao.selectUserrole(uNum, ur1.getRoleNum());
                if (StrUtil.isEmpty(flag)) {
                    ur1.setUserNum(uNum);
                    this.dao2.save(ur1);
                }
                String flag2 = this.dao.selectUserrole(uNum, ur.getRoleNum());
                if (StrUtil.isEmpty(flag2)) {
                    ur.setUserNum(uNum);
                    this.dao2.save(ur);
                }
                if (StrUtil.isNotEmpty(category) && !Convert.toStr(examPaperNum).equals(category)) {
                    Map args = new HashMap();
                    args.put("category", category);
                    args.put("uNum", uNum);
                    if (null == this.dao2._queryObject("select exampaperNum from userrole_sub where exampaperNum={category} and userNum={uNum} limit 1", args)) {
                        this.dao2._execute("insert into userrole_sub (exampaperNum,userNum) values ({category},{uNum})", args);
                    }
                }
            }
        }
        if (null != addAnddelete && addAnddelete.size() != 0 && operatetype.equals("clear")) {
            Map args_roleNumsql = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
            Object roleNumobj = this.dao2._queryObject("select roleNum  from role where examPaperNum = {examPaperNum} and type='4'", args_roleNumsql);
            String roleNumstr = String.valueOf(roleNumobj);
            String zikemu = this.dao2._queryStr("select GROUP_CONCAT(examPaperNum) from exampaper where pexamPaperNum={examPaperNum}", args_roleNumsql);
            Iterator<String> it = addAnddelete.iterator();
            while (it.hasNext()) {
                Map args2 = StreamMap.create().put("roleNumstr", (Object) roleNumstr).put("uNum", (Object) it.next()).put("examPaperNum", (Object) examPaperNum);
                this.dao2._execute("delete from userrole where roleNum={roleNumstr} and userNum={uNum}", args2);
                if (StrUtil.isNotEmpty(zikemu)) {
                    args2.put("zikemu", zikemu);
                    this.dao2._execute("delete from userrole_sub where userNum={uNum} and exampaperNum in ({zikemu[]}) ", args2);
                }
                this.dao2._execute("delete from questiongroup_user where exampaperNum={examPaperNum} and userNum={uNum} ", args2);
                this.dao2._execute("delete from assistyuejuan where examPaperNum={examPaperNum} and assister={uNum} ", args2);
                this.dao2._execute("delete from quota where exampaperNum={examPaperNum} and insertUser={uNum}", args2);
            }
        }
        return 1;
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer addAndDeleteRole(List<String> add, List<String> delete, Userrole ur) {
        if (null == ur || null == ur.getUserNum() || ur.getUserNum().equals("")) {
            return null;
        }
        if (add.size() > 0) {
            for (String roleNum : add) {
                Userrole urole = new Userrole();
                urole.setInsertDate(DateUtil.getCurrentDay());
                urole.setInsertUser(ur.getInsertUser());
                urole.setRoleNum(roleNum);
                urole.setSchoolNum(ur.getSchoolNum());
                urole.setUserNum(ur.getUserNum());
                this.dao2.save(urole);
            }
        }
        if (delete.size() > 0) {
            for (String roleNum2 : delete) {
                String sql = "delete from userrole where  userNum = '" + ur.getUserNum() + "' and roleNum = '" + roleNum2 + "' ";
                this.dao2.execute(sql);
            }
        }
        return 1;
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer getAllUserCount(User user, String userId) {
        if (null == user) {
            return null;
        }
        return this.dao.getAllUserCount(user, userId);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer deleteUsers(String[] userId, String[] userNum, String tab) {
        if (null == userId || userId.length <= 0) {
            return null;
        }
        for (int i = 0; i < userId.length; i++) {
            this.dao.deleteOneUser(userId[i], userNum[i], tab);
        }
        return 1;
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer deleteOneUser(String userId, String num, String tab) {
        if (null == num || num.equals("")) {
            return null;
        }
        return this.dao.deleteOneUser(userId, num, tab);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer updatepasword(String num, String password) {
        if (null == num || null == password) {
            return null;
        }
        return this.dao.updatepasword(num, password);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String getUserByName(String name) {
        Object obj = this.dao.getUserByName(name);
        if (null == obj) {
            Object obj2 = this.dao.getUserParentByName(name);
            if (null == obj2) {
                return null;
            }
            return obj2.toString();
        }
        return obj.toString();
    }

    @Override // com.dmj.service.userManagement.UserService
    public boolean authenticationPasword(String num, String password) {
        Object obj = this.dao.authenticationPasword(num, password);
        if (null == obj || obj.toString().equals("0")) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.userManagement.UserService
    public void addRole(List<Role> list) throws Exception {
        this.dao2.batchSave(list);
    }

    @Override // com.dmj.service.userManagement.UserService
    public void editRole(String Rnum, String roleName, String description) {
        Map args = StreamMap.create().put("roleName", (Object) roleName).put("Rnum", (Object) Rnum);
        this.dao2._execute("UPDATE role set roleName={roleName} where roleNum={Rnum}", args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Map<String, String> rolelist(String num, String openocs) throws Exception {
        String uType;
        String sql;
        Map args = StreamMap.create().put("num", (Object) num);
        if ("-2".equals(num)) {
            uType = "0";
        } else {
            uType = (String) this.dao2._queryObject("SELECT usertype from user WHERE id ={num}", args);
        }
        if (uType != null && uType.equals("0")) {
            sql = "SELECT DISTINCT num ,name from resource where pnum='0' and pnum != 7 and num !=7 ORDER BY ordernum";
        } else if ("1".equals(openocs)) {
            sql = "SELECT DISTINCT r.num num, r.name name  FROM (SELECT roleNum FROM userrole WHERE userNum={num}) ur LEFT JOIN resourcerole rr ON ur.roleNum=rr.roleNum LEFT JOIN resource r ON rr.resource = r.num  WHERE r.pnum = '0' ORDER BY r.ordernum";
        } else {
            sql = "SELECT DISTINCT r.num num, r.name name  FROM (SELECT roleNum FROM userrole WHERE userNum={num}) ur LEFT JOIN resourcerole rr ON ur.roleNum=rr.roleNum LEFT JOIN resource r ON rr.resource = r.num  WHERE r.pnum = '0' and r.num!=7 ORDER BY r.ordernum";
        }
        return this.dao2._queryOrderMap(sql, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Map<String, String> rolelist11(String num, String Id, String type) throws Exception {
        String sql;
        String uType = "";
        String sysVersionsql = "";
        ServletContext context = ServletActionContext.getServletContext();
        String sysVersionval = String.valueOf(context.getAttribute("sysVersion"));
        if (!"1".equals(sysVersionval)) {
            sysVersionsql = " and r.isdelete='0' ";
        }
        Map args = StreamMap.create().put("num", (Object) num).put("Id", (Object) Id).put("type", (Object) type);
        if (!"-2".equals(num)) {
            uType = (String) this.dao2._queryObject("SELECT usertype from user WHERE id ={num}", args);
        } else if ("-2".equals(num)) {
            uType = "0";
        }
        if (uType != null && uType.equals("0")) {
            sql = "SELECT DISTINCT r.url ,r.name,r.para from resource r  where r.pnum={Id} and (r.type={type} or r.type='2') " + sysVersionsql + "   ORDER BY length(r.ordernum),r.ordernum";
        } else {
            sql = "SELECT DISTINCT r.url url,r.name name,r.para para FROM (SELECT roleNum FROM userrole WHERE userNum={num}) ur LEFT JOIN resourcerole rr ON ur.roleNum=rr.roleNum  LEFT JOIN resource r ON rr.resource = r.num   WHERE  r.pnum = {Id} and (r.type={type} or r.type='2')  " + sysVersionsql + "  ORDER BY length(r.ordernum),r.ordernum";
        }
        return this.dao2._queryOrderMap(sql, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List rolelist22(String num, String Id, String type, List now) throws Exception {
        List list;
        String uType = "";
        String sysVersionsql = "";
        ServletContext context = ServletActionContext.getServletContext();
        String sysVersionval = String.valueOf(context.getAttribute("sysVersion"));
        if (!"1".equals(sysVersionval)) {
            sysVersionsql = " and r.isdelete='0' ";
        }
        if (!"-2".equals(num)) {
            Map args_usertypesql = StreamMap.create().put("num", (Object) num);
            uType = (String) this.dao2._queryObject("SELECT usertype from user WHERE id ={num}", args_usertypesql);
        } else if ("-2".equals(num)) {
            uType = "0";
        }
        new ArrayList();
        if (uType != null && uType.equals("0")) {
            Map args_sql = StreamMap.create().put("Id", (Object) Id).put("type", (Object) type);
            String keyNumsql = "";
            if (now != null && now.size() != 0) {
                keyNumsql = "and ( ";
                for (int i = 0; i < now.size(); i++) {
                    String keyNum = (String) now.get(i);
                    String argsKey = "keyNum" + i;
                    args_sql.put(argsKey, keyNum);
                    if (now.size() - 1 != i) {
                        keyNumsql = keyNumsql + " keyNum={" + argsKey + "}  or ";
                    } else if (now.size() - 1 == i) {
                        keyNumsql = keyNumsql + "  keyNum={" + argsKey + "}  ) ";
                    }
                }
            }
            String sql = "SELECT  r.id,r.num ,r.pnum,r.name,r.url,r.type,r.isleaf,r.isdelete,r.ordernum,r.para,r.reportType,r.fn,r.openAllSchool  ,rg.`name` ext1,IFNULL(rg.num,'') ext2  from resource r  LEFT JOIN resourcegroup rg on rg.snum=r.num ";
            if (!"".equals(keyNumsql)) {
                sql = sql + " left join report_r_keyword rrk on rrk.resourceNum=r.num  ";
            }
            String sql2 = sql + " WHERE  r.pnum = {Id} and (r.type={type} or r.type='2') ";
            if (!"".equals(keyNumsql)) {
                sql2 = sql2 + " and rrk.resourceNum=r.num  " + keyNumsql + " ";
            }
            list = this.dao2._queryBeanList(sql2 + " " + sysVersionsql + " ORDER BY rg.ordernum,length(r.ordernum),r.ordernum", Resource.class, args_sql);
        } else {
            Map args_sql2 = StreamMap.create().put("num", (Object) num).put("Id", (Object) Id).put("type", (Object) type);
            String keyNumsql2 = "";
            if (now != null && now.size() != 0) {
                keyNumsql2 = "and ( ";
                for (int i2 = 0; i2 < now.size(); i2++) {
                    String keyNum2 = (String) now.get(i2);
                    String argsKey2 = "keyNum" + i2;
                    args_sql2.put(argsKey2, keyNum2);
                    if (now.size() - 1 != i2) {
                        keyNumsql2 = keyNumsql2 + " keyNum={" + argsKey2 + "}  or ";
                    } else if (now.size() - 1 == i2) {
                        keyNumsql2 = keyNumsql2 + "  keyNum={" + argsKey2 + "}  ) ";
                    }
                }
            }
            String sql3 = "SELECT DISTINCT r.id,r.num ,r.pnum,r.name,r.url,r.type,r.isleaf,r.isdelete,r.ordernum,r.para,r.reportType,r.fn,r.openAllSchool,rg.`name` ext1,IFNULL(rg.num,'') ext2  FROM (SELECT roleNum FROM userrole WHERE userNum={num}) ur LEFT JOIN resourcerole rr ON ur.roleNum=rr.roleNum  LEFT JOIN resource r ON rr.resource = r.num   LEFT JOIN resourcegroup rg on rg.snum=r.num ";
            if (!"".equals(keyNumsql2)) {
                sql3 = sql3 + " left join report_r_keyword rrk on rrk.resourceNum=rr.resource and rrk.resourceNum=r.num ";
            }
            String sql4 = sql3 + " WHERE  r.pnum = {Id} and (r.type={type} or r.type='2') ";
            if (!"".equals(keyNumsql2)) {
                sql4 = sql4 + " and rrk.resourceNum=rr.resource and rrk.resourceNum=r.num " + keyNumsql2 + " ";
            }
            list = this.dao2._queryBeanList(sql4 + "  " + sysVersionsql + " ORDER BY rg.ordernum,r.reportType,length(r.ordernum),r.ordernum", Resource.class, args_sql2);
        }
        return list;
    }

    @Override // com.dmj.service.userManagement.UserService
    public Object[] querySchoolAndClass(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        return this.dao2._queryArray("select s.schoolName,bg.gradeName,c.className,st.studentName,st.studentId,st.gradeNum,st.studentName from student st left join school s on s.id=st.schoolNum left join basegrade bg on st.gradeNum=bg.gradeNum left join class  c on c.id=st.classNum where st.id ={id}", args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List queryreporthideAndshow(String num, String Id, String type, List now) throws Exception {
        List list;
        Map args_usertypesql = StreamMap.create().put("num", (Object) num);
        String uType = (String) this.dao2._queryObject("SELECT usertype from user WHERE id ={num}", args_usertypesql);
        new ArrayList();
        if (uType != null && uType.equals("0")) {
            String keyNumsql = "";
            Map args_sql = StreamMap.create().put("type", (Object) type);
            if (now != null && now.size() != 0) {
                keyNumsql = "and ( ";
                for (int i = 0; i < now.size(); i++) {
                    String keyNum = (String) now.get(i);
                    String argsKey = "keyNum" + i;
                    args_sql.put(argsKey, keyNum);
                    if (now.size() - 1 != i) {
                        keyNumsql = keyNumsql + " keyNum={" + argsKey + "}  or ";
                    } else if (now.size() - 1 == i) {
                        keyNumsql = keyNumsql + "  keyNum={" + argsKey + "}  ) ";
                    }
                }
            }
            String sql = "SELECT  r.id,r.num ,r.pnum,r.name,r.url,r.type,r.isleaf,r.isdelete,r.ordernum,r.para,r.reportType,r.fn from resource r";
            if (!"".equals(keyNumsql)) {
                sql = sql + " left join report_r_keyword rrk on  rrk.resourceNum=r.num ";
            }
            String sql2 = sql + " WHERE  (r.type={type} or r.type='2') ";
            if (!"".equals(keyNumsql)) {
                sql2 = sql2 + " and rrk.resourceNum=r.num " + keyNumsql + " ";
            }
            list = this.dao2._queryBeanList(sql2 + " group by r.pnum ORDER BY length(r.reportType),r.reportType,length(r.ordernum),r.ordernum", Resource.class, args_sql);
        } else {
            Map args_sql2 = StreamMap.create().put("num", (Object) num).put("type", (Object) type);
            String keyNumsql2 = "";
            if (now != null && now.size() != 0) {
                keyNumsql2 = "and ( ";
                for (int i2 = 0; i2 < now.size(); i2++) {
                    String keyNum2 = (String) now.get(i2);
                    String argsKey2 = "keyNum" + i2;
                    args_sql2.put(argsKey2, keyNum2);
                    if (now.size() - 1 != i2) {
                        keyNumsql2 = keyNumsql2 + " keyNum={" + argsKey2 + "}  or ";
                    } else if (now.size() - 1 == i2) {
                        keyNumsql2 = keyNumsql2 + "  keyNum={" + argsKey2 + "}  ) ";
                    }
                }
            }
            String sql3 = "SELECT DISTINCT r.id,r.num ,r.pnum,r.name,r.url,r.type,r.isleaf,r.isdelete,r.ordernum,r.para,r.reportType,r.fn  FROM (SELECT roleNum FROM userrole WHERE userNum={num}) ur LEFT JOIN resourcerole rr ON ur.roleNum=rr.roleNum  LEFT JOIN resource r ON rr.resource = r.num  ";
            if (!"".equals(keyNumsql2)) {
                sql3 = sql3 + " left join report_r_keyword rrk on rrk.resourceNum=rr.resource and rrk.resourceNum=r.num ";
            }
            String sql4 = sql3 + " WHERE  (r.type={type} or r.type='2') ";
            if (!"".equals(keyNumsql2)) {
                sql4 = sql4 + " and rrk.resourceNum=rr.resource and rrk.resourceNum=r.num " + keyNumsql2 + " ";
            }
            list = this.dao2._queryBeanList(sql4 + " group by r.pnum ORDER BY length(r.reportType),r.reportType,length(r.ordernum),r.ordernum", Resource.class, args_sql2);
        }
        List list2 = new ArrayList();
        for (int i3 = 0; i3 < list.size(); i3++) {
            Resource res = (Resource) list.get(i3);
            String pnum = res.getPnum();
            Map args_urlsql = StreamMap.create().put("pnum", (Object) pnum);
            String urlvalue = (String) this.dao2._queryObject(" select url from resource where num={pnum}", args_urlsql);
            if (!"".equals(urlvalue) && null != urlvalue) {
                list2.add(urlvalue);
            }
        }
        return list2;
    }

    @Override // com.dmj.service.userManagement.UserService
    public String username(String name) {
        Map args = StreamMap.create().put("name", (Object) name);
        Object inta = this.dao2._queryObject("select id from user where username={name}", args);
        String userid = String.valueOf(inta);
        return userid;
    }

    @Override // com.dmj.service.userManagement.UserService
    public void delOnlineuserBySessionid(String sessionid) {
        this.dao.delOnlineuserBySessionid(sessionid);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String selectRole(String type, Integer examNum, Integer examPaperNum) {
        return this.dao.selectRole(type, examNum, examPaperNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getuserNum(String subjectNum, String gradeNum, String jie) {
        List list = this.dao.getuserNum(subjectNum, gradeNum, jie);
        return list;
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<User> getUsersByuerNum(List userNumList) {
        return this.dao.getUsersByuerNum(userNumList);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String getexamPaperNum(String examNum, String gradeNum, String subjectNum) {
        return this.dao.getexamPaperNum(examNum, gradeNum, subjectNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public int checkIfSubjectDefineExist(String exampaperNum) {
        return this.dao.checkIfSubjectDefineExist(exampaperNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public int checkIfGradeDefineExist(String examNum, String gradeNum) {
        return this.dao.checkIfGradeDefineExist(examNum, gradeNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public int checkIfExamDefineExist(String examNum) {
        return this.dao.checkIfExamDefineExist(examNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<User> getUsersByType(String type, Integer examNum, Integer examPaperNum, String category) {
        if (null == type || type.equals("")) {
            return null;
        }
        return this.dao.getUsersByType(type, examNum, examPaperNum, category);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<User> getUsersByType2(String type, Integer examNum, Integer examPaperNum, String username, String category) {
        if (null == type || type.equals("")) {
            return null;
        }
        return this.dao.getUsersByType2(type, examNum, examPaperNum, username, category);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String selectExamName(String examNum) {
        return this.dao.selectExamName(examNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String selectGradeName(String gradeNum) {
        return this.dao.selectGradeName(gradeNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String selectSubjectName(String subjectNum) {
        return this.dao.selectSubjectName(subjectNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer getRolesYjy(String usernum) {
        return this.dao.getRolesYjy(usernum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String queryUserSex(User u) {
        return this.dao.queryUserSex(u);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List querygrade(String schoolNum) {
        return this.dao.querygrade(schoolNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List queryclass(String gradeNum, String schoolNum) {
        return this.dao.queryclass(gradeNum, schoolNum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String selectuserRole(String teacherNum) {
        Map args = StreamMap.create().put("teacherNum", (Object) teacherNum);
        return String.valueOf(this.dao2._queryObject("select count(1) from userrole where  userrole.roleNum='4' and userNum={teacherNum}", args));
    }

    @Override // com.dmj.service.userManagement.UserService
    public void updateusername(User user, String loginType) {
        String sql = "update user set loginname={loginname} where id={id}";
        if ("P".equals(loginType)) {
            sql = "update userparent set loginname={loginname} where username={username}";
        }
        this.dao2._execute(sql, user);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String queryuseryes(String name) {
        Map args = StreamMap.create().put("name", (Object) name);
        String usercount = this.dao2._queryObject("select count(1) from user where username={name} ", args).toString();
        if (usercount.equals("0")) {
            usercount = this.dao2._queryObject("select count(1) from user where  loginname={name}", args).toString();
        }
        return usercount;
    }

    @Override // com.dmj.service.userManagement.UserService
    public String queryuserparentyes(String name) {
        Map args = StreamMap.create().put("name", (Object) name);
        String usercount = this.dao2._queryObject("select count(1) from userparent where username={name} ", args).toString();
        if (usercount.equals("0")) {
            usercount = this.dao2._queryObject("select count(1) from userparent where  loginname={name}", args).toString();
        }
        return usercount;
    }

    @Override // com.dmj.service.userManagement.UserService
    public String searchUserPhone(String userNameString, String userType) {
        String sqlString = "select mobile from userparent where (username = {userNameString} or loginname = {userNameString}) and usertype='3'";
        if (userType.equals("T")) {
            sqlString = "select mobile from user where (username = {userNameString} or loginname = {userNameString}) and usertype='1' ";
        } else if (userType.equals("P")) {
            sqlString = "select mobile from userparent  where (username = {userNameString} or loginname = {userNameString}) ";
        } else if (userType.equals(Const.exampaper_doubleFaced_S)) {
            sqlString = "select mobile from user  where (username = {userNameString} or loginname = {userNameString}) and usertype='2' ";
        }
        Map args = StreamMap.create().put("userNameString", (Object) userNameString);
        try {
            String phoneNumber = String.valueOf(this.dao2._queryObject(sqlString, args));
            return phoneNumber;
        } catch (Exception e) {
            return "null";
        }
    }

    @Override // com.dmj.service.userManagement.UserService
    public List queryresource(String type, Integer userNum) {
        if (type.equals("0")) {
            return this.dao2._queryBeanList(" select url from resource ", Resource.class, null);
        }
        Map args = StreamMap.create().put("userNum", (Object) userNum);
        return this.dao2._queryBeanList("SELECT DISTINCT r.url  FROM (SELECT roleNum FROM userrole WHERE userNum={userNum}) ur LEFT JOIN resourcerole rr ON ur.roleNum=rr.roleNum LEFT JOIN resource r ON rr.resource = r.num  ", Resource.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List loginQueryUserName(String schoolNum, String userName, String usertype, int start, int pagesize) {
        if (usertype.equals("1")) {
            String sql = "select teacherNum,teacherName,s.schoolName from teacher  left join school s on teacher.schoolNum=s.id where 1=1  ";
            if (!"-1".equals(schoolNum)) {
                sql = sql + " and teacher.schoolNum={schoolNum} ";
            }
            if (!"".equals(userName) && null != userName && !"null".equals(userName)) {
                sql = sql + " and teacherName like {userName} ";
            }
            String sql2 = sql + " ORDER BY convert(teacherName using gbk)LIMIT {start},{pagesize}";
            Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("userName", (Object) ("%" + userName + "%")).put("start", (Object) Integer.valueOf(start)).put("pagesize", (Object) Integer.valueOf(pagesize));
            return this.dao2._queryBeanList(sql2, Teacher.class, args);
        }
        String sql3 = "select student.studentid,studentNum,studentName,s.schoolName ext1,g.gradeName ext2,c.className ext3 from student  left join school s on student.schoolNum=s.id left join grade g on student.gradeNum=g.gradeNum  and student.schoolNum=g.schoolNum left join class c on c.id=student.classNum and student.schoolNum=c.schoolNum where  1=1 ";
        if (!"-1".equals(schoolNum)) {
            sql3 = sql3 + " and student.schoolNum={schoolNum} ";
        }
        if (!"".equals(userName) && null != userName && !"null".equals(userName)) {
            sql3 = sql3 + " and studentName like {userName} ";
        }
        String sql4 = sql3 + " ORDER BY convert(studentName using gbk)LIMIT {start},{pagesize}";
        Map args2 = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("userName", (Object) ("%" + userName + "%")).put("start", (Object) Integer.valueOf(start)).put("pagesize", (Object) Integer.valueOf(pagesize));
        return this.dao2._queryBeanList(sql4, Student.class, args2);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer loginQueryUserNameCount(String schoolNum, String userName, String usertype) {
        String sql;
        if (usertype.equals("1")) {
            sql = "select count(1) from teacher where 1=1 ";
            if (!"-1".equals(schoolNum)) {
                sql = sql + " and schoolNum={schoolNum} ";
            }
            if (!"".equals(userName) && null != userName && !"null".equals(userName)) {
                sql = sql + " and teacherName like {userName} or teacherNum like {userName} ";
            }
        } else {
            sql = "select count(1) from student where 1=1 ";
            if (!"-1".equals(schoolNum)) {
                sql = sql + " and schoolNum={schoolNum} ";
            }
            if (!"".equals(userName) && null != userName && !"null".equals(userName)) {
                sql = sql + " and studentName like {userName} or studentId like {userName} ";
            }
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("userName", (Object) ("%" + userName + "%"));
        return Integer.valueOf(Integer.parseInt(String.valueOf(this.dao2._queryObject(sql.toString(), args))));
    }

    @Override // com.dmj.service.userManagement.UserService
    public String resname(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        String s = (String) this.dao2._queryObject("select name from resource where num={id}", args);
        if (null == s) {
            return "没有任何操作";
        }
        return s;
    }

    @Override // com.dmj.service.userManagement.UserService
    public void deleterole(String roleNum) {
        Map args = StreamMap.create().put("roleNum", (Object) roleNum);
        this.dao2._execute("delete from role where roleNum={roleNum}", args);
        this.dao2._execute("delete from userrole where roleNum={roleNum}", args);
        this.dao2._execute("delete from resourcerole where roleNum={roleNum}", args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public int getSchoolNum() {
        return Integer.parseInt(String.valueOf(this.dao2._queryObject("select count(1) from school ", null)));
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<OnlineUser> getOnlineUserList() {
        List<OnlineUser> list = getonlineuserList();
        return (List) list.stream().filter(o -> {
            o.loadLastRequestVisitor();
            if (null != o.getLastRequestVisitor() && cn.hutool.core.date.DateUtil.between(cn.hutool.core.date.DateUtil.parse(o.getLastRequestVisitor().getLastVisitorTime()), DateTime.now(), DateUnit.SECOND) < 180) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<OnlineUser> getonlineuserList() {
        String sql = "SELECT DISTINCT u.realname userName,ou.insertTime ext1 ,ou.sessionId,u.usertype,sch.shortname schoolName,g.gradeName,cla.className,ou.userNum,stu.studentName FROM onlineuser ou  LEFT JOIN (select id ,schoolnum,userid ,realname,usertype from user ";
        return this.dao2.queryBeanList((((((sql + "\tUNION ALL SELECT id ,schoolnum,userid,realname,usertype from userparent)   u ON ou.userNum=u.id ") + " LEFT JOIN school sch ON  sch.id = u.schoolnum ") + "  LEFT JOIN student stu ON stu.id = u.userid and (u.usertype=2 or u.usertype=3)") + "  LEFT JOIN grade g ON g.gradeNum = stu.gradeNum ") + "  LEFT JOIN class cla ON cla.id = stu.classNum") + "  WHERE u.id IS NOT NULL ORDER BY u.usertype,CONVERT(sch.shortname USING GBK) ,g.gradeNum,CONVERT(cla.className USING GBK),ou.insertTime desc", OnlineUser.class);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getRelation() {
        return this.dao.getRelation();
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getallSchoolName() {
        return this.dao.getAllSchoolName();
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer save(Userparent userparent) {
        if (null == userparent) {
            return null;
        }
        return this.dao.saveUserparent(userparent);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String getStudentidByid(int id) {
        return this.dao.getStudentidByid(id);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getStudentNum(String userid) {
        return this.dao.getStudentNum(userid);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer update(Userparent userparent) {
        if (null == userparent) {
            return null;
        }
        return this.dao.updateUserparent(userparent);
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserByName1(String name) {
        return this.dao.getUserByName1(name);
    }

    @Override // com.dmj.service.userManagement.UserService
    public boolean authenticationPaswordByName(String name, String password) {
        Object obj = this.dao.authenticationPaswordByName(name, password);
        if (null == obj || obj.toString().equals("0")) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer updatepaswordByName(String name, String password) {
        if (null == name || null == password) {
            return null;
        }
        return this.dao.updatepaswordByName(name, password);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String getpublicip(String type) {
        if (null == type) {
            return null;
        }
        return this.dao.getpublicip(type);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer saveuserrole(Userparent userparent) {
        if (null == userparent) {
            return null;
        }
        return this.dao.saveuserrole(userparent);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getSchool(String schoolnum) {
        if (null == schoolnum) {
            return null;
        }
        return this.dao.getschool(schoolnum);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer updateUserparentpw(String username, String password) {
        if (null == username || null == password) {
            return null;
        }
        return this.dao.updateUserparentpw(username, password);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String getRank(String type) {
        if (null == type) {
            return null;
        }
        return this.dao.getRank(type);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getTeacherInfo(String userNum, ServletContext context) {
        if (userNum == null) {
            return null;
        }
        return this.dao.getTeacherinfo(userNum, context);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Object[] getStudentBaseIofo(Map<String, String> map, Map jie_map, String userId) {
        StringBuffer sql = new StringBuffer();
        StringBuffer rows = new StringBuffer();
        String jieInfo = "";
        String sch = "";
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String classNum = map.get(Const.EXPORTREPORT_classNum);
        String sex = map.get("sex");
        String stuID = map.get("stuID");
        String note = map.get("description");
        if (map.get("level") == null || map.get("level").toString().equals("false")) {
            sql.append("SELECT st.id as id , sl.schoolName as schoolName,st.schoolNum as schoolNum ,g.gradeName as gradeName ,g.gradeNum ,c.className as className,st.studentName,st.studentID ,st.studentNum,st.id ,st.description,st.note ,st.jie,da.name,daaa.name source from student st  LEFT JOIN school sl ON st.schoolNum=sl.id ");
            if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2")) {
                sql.append(" left join schauthormanage s on s.schoolNum = sl.id and s.userId={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ");
                sch = "  and (s.schoolNum is not null or t.schoolNum is not null) ";
            }
            sql.append(" LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie  LEFT JOIN class c ON st.classNum=c.id    LEFT JOIN data da ON da.value= st.type AND da.type=22 LEFT JOIN data daaa ON daaa.`value` = st.source AND daaa.type=26  where st.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' " + sch + " ");
            rows.append("SELECT count(st.id) from student st  LEFT JOIN school sl ON st.schoolNum=sl.id ");
            if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2")) {
                rows.append(" left join schauthormanage s on s.schoolNum = sl.id and s.userId={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ");
                sch = "  and (s.schoolNum is not null or t.schoolNum is not null) ";
            }
            rows.append(" LEFT JOIN grade g on  g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie   LEFT JOIN class c ON st.classNum=c.id  LEFT JOIN data da ON da.value= st.type AND da.type=22 LEFT JOIN data daaa ON daaa.`value` = st.source AND daaa.type=26   where st.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' " + sch + " ");
            if (!"-1".equals(schoolNum)) {
                sql.append(" and st.schoolNum={schoolNum}");
                rows.append(" and st.schoolNum={schoolNum}");
            }
            if (!"".equals(gradeNum)) {
                sql.append(" and lc.gradeNum={gradeNum}");
                rows.append(" and lc.gradeNum={gradeNum}");
                jieInfo = " and st.jie={jie}";
            }
            if (!"".equals(classNum)) {
                sql.append(" and lc.levelclassNum={classNum}");
                rows.append(" and lc.levelclassNum={classNum}");
            }
            if (!"".equals(sex)) {
                sql.append(" and st.sex={sex} ");
                rows.append(" and st.sex={sex} ");
            }
            map.get("studentName");
            if (null != stuID && !"".equals(stuID)) {
                sql.append(" and st.id ={stuID}");
                rows.append(" and \tst.id ={stuID} ");
            }
            if (null != note && !"".equals(note)) {
                sql.append(" and st.description like {note}");
                rows.append(" and st.description like {note}");
            }
            sql.append(" order by st.schoolNum*1, st.gradeNum*0.1");
        } else {
            sql.append("SELECT  lc.id as id ,sl.schoolName as schoolName,st.schoolNum as schoolNum  ,g.gradeName as gradeNum,c.className as classNum,st.studentName,lc.studentID,st.studentNum,st.note,st.jie,da.name,daaa.name source from levelclass lc LEFT JOIN school sl ON lc.schoolNum=sl.id ");
            if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2")) {
                sql.append(" left join schauthormanage s on s.schoolNum = sl.id and s.userId={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ");
                sch = "  and (s.schoolNum is not null or t.schoolNum is not null) ";
            }
            sql.append("LEFT join student st ON lc.studentID=st.studentID  LEFT JOIN grade g on g.gradeNum=st.gradeNum and st.schoolNum=g.schoolNum AND st.jie = g.jie LEFT JOIN class c ON lc.levelClassNum=c.classNum AND  st.schoolNum=c.schoolNum   AND st.gradeNum = c.gradeNum AND st.jie=c.jie   LEFT JOIN data da ON da.value= st.type AND da.type=22 LEFT JOIN data daaa ON daaa.`value` = st.source AND daaa.type=26  WHERE lc.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' " + sch + " ");
            rows.append("SELECT count(lc.id) from levelclass lc LEFT JOIN school sl ON lc.schoolNum=sl.id ");
            if ("-1".equals(schoolNum) && !userId.equals("-1") && !userId.equals("-2")) {
                rows.append(" left join schauthormanage s on s.schoolNum = sl.id and s.userId={userId} left join user t on t.schoolNum = sl.id  and t.id = {userId} and t.usertype=1 ");
                sch = "  and (s.schoolNum is not null or t.schoolNum is not null) ";
            }
            rows.append("LEFT join student st ON lc.studentID=st.studentID  LEFT JOIN grade g on g.gradeNum=st.gradeNum  and st.schoolNum=g.schoolNum AND st.jie = g.jie LEFT JOIN class c ON lc.levelClassNum=c.classNum  AND  st.schoolNum=c.schoolNum   AND st.gradeNum = c.gradeNum AND st.jie=c.jie  LEFT JOIN data da ON da.value= st.type AND da.type=22 LEFT JOIN data daaa ON daaa.`value` = st.source AND daaa.type=26 WHERE lc.isDelete='F' and sl.isDelete='F' and g.isDelete='F' and c.isDelete='F' " + sch + " ");
            if (!"".equals(schoolNum)) {
                sql.append(" and lc.schoolNum={schoolNum}");
                rows.append(" and lc.schoolNum={schoolNum}");
            }
            if (!"".equals(gradeNum)) {
                sql.append(" and lc.gradeNum={gradeNum}");
                rows.append(" and lc.gradeNum={gradeNum}");
                jieInfo = " and st.jie={jie}";
            }
            if (!"".equals(classNum)) {
                sql.append(" and lc.levelclassNum={classNum}");
                rows.append(" and lc.levelclassNum={classNum}");
            }
            if (!"".equals(sex)) {
                sql.append(" and st.sex={sex}");
                rows.append(" and st.sex={sex}");
            }
            map.get("studentName");
            if (null != stuID && !"".equals(stuID)) {
                sql.append(" and st.id ={stuID}");
                rows.append(" and \tst.id ={stuID} ");
            }
            if (null != note && !"".equals(note)) {
                sql.append(" and st.description like {note}");
                rows.append(" and st.description like {note}");
            }
            sql.append(" order by  st.schoolNum*1,st.gradeNum*0.1");
        }
        Map args = StreamMap.create().put("userId", (Object) userId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("jie", jie_map.get(gradeNum)).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("sex", (Object) sex).put("stuID", (Object) stuID).put("note", (Object) ("%" + note + "%"));
        String count = "0";
        if ("0".equals(map.get("count"))) {
            Object o = this.dao2._queryObject(rows.toString(), args);
            if (null != o) {
                count = o.toString();
            }
        } else {
            count = map.get("count");
        }
        Integer.valueOf(map.get("index")).intValue();
        Integer.valueOf(map.get("pageSize")).intValue();
        List list = this.dao2._queryBeanList(sql.toString() + jieInfo, Student.class, args, Integer.valueOf(map.get("index")).intValue(), Integer.valueOf(map.get("pageSize")).intValue());
        return new Object[]{count, list};
    }

    @Override // com.dmj.service.userManagement.UserService
    public Map<String, String> getStuType() {
        return this.dao2._queryOrderMap("SELECT VALUE,NAME FROM `data` WHERE type='22'", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Student getStuInfo(String id, String userb) {
        Map args = StreamMap.create().put("id", (Object) id);
        return (Student) this.dao2._queryBean("SELECT s.studentId ,s.studentName studentName,s.gradeNum gradeNum,s.source source ,s.studentNum studentNum,s.type type,s.oldName oldName,s.note note,c.id classNum,s.schoolNum schoolNum,s.id id,s.sex FROM student s LEFT JOIN class c ON c.id=s.classNum where s.id={id}", Student.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Map<String, String> getStuNoteType() {
        return this.dao2._queryOrderMap("SELECT VALUE,NAME FROM `data` WHERE type='28'", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Map<String, String> getStuSource() {
        return this.dao2._queryOrderMap("SELECT `value`,`name` FROM `data` WHERE type='26' AND isDelete='F' ORDER BY orderNum*1", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<School> getbaseSchool() {
        return this.dao2._queryBeanList("select DISTINCT id schoolNum ,schoolName from school where isDelete ='F'", School.class, null);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Grade> getBaseGrade(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList("select DISTINCT gradeNum,gradeName from grade  where schoolNum ={schoolNum} AND isDelete ='F' order by gradeNum", Grade.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getBaseClass(String school, String grade) {
        String jie = getJIe(grade, school);
        Map args = StreamMap.create().put("grade", (Object) grade).put(License.SCHOOL, (Object) school).put("jie", (Object) jie);
        return this.dao2._queryBeanList("select DISTINCT id,className from class WHERE gradeNum ={grade} and schoolNum = {school} and jie = {jie} order by classNum", Pjbdata.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Grade> getBaseGradeBySchoolNum(String schoolNum, String userid) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT DISTINCT g.gradeNum ,g.gradeName FROM ");
        sb.append("(SELECT DISTINCT gradeNum FROM userposition WHERE userNum={userid} AND schoolNum={schoolNum} ");
        sb.append(" UNION ALL SELECT DISTINCT gradeNum FROM userposition_record WHERE userNum= {userid} AND schoolNum={schoolNum})u ");
        sb.append("LEFT JOIN basegrade g ON g.gradeNum=u.gradeNum  WHERE u.gradeNum IS NOT NULL order by g.gradeNum");
        Map args = StreamMap.create().put("userid", (Object) userid).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList(sb.toString(), Grade.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getBaseClassByGrade(String schoolNum, String gradeNum, String id) {
        StringBuffer sb = new StringBuffer();
        getJIe(gradeNum, schoolNum);
        sb.append("SELECT  DISTINCT cla.id id,cla.className className  FROM ");
        sb.append("(SELECT DISTINCT classNum FROM userposition_record WHERE schoolNum={schoolNum} and gradeNum={gradeNum} ");
        sb.append(" AND userNum={id} ");
        sb.append(" UNION ALL SELECT DISTINCT classNum FROM userposition WHERE schoolNum={schoolNum} and gradeNum={gradeNum} ");
        sb.append(" AND userNum={id} ");
        sb.append(") u ");
        sb.append("\tLEFT JOIN class cla ON u.classNum=cla.id ");
        sb.append(" WHERE cla.id IS NOT NULL ORDER BY LENGTH(cla.classNum),cla.classNum ");
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("id", (Object) id);
        return this.dao2._queryBeanList(sb.toString(), Pjbdata.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getBaseStudent(String schoolNum, String graderNum, String classNum) {
        Map args = StreamMap.create().put("graderNum", (Object) graderNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_classNum, (Object) classNum);
        return this.dao2._queryBeanList("select DISTINCT id,studentName from student WHERE gradeNum ={graderNum} and schoolNum = {schoolNum} and classNum = {classNum} AND isDelete ='F'", Student.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getBaseExamByStudentId(String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryBeanList("select s.examNum,e.examName from (select * from studentlevel where studentId = {studentId} and isDelete='F' GROUP BY examNum)as s left join exam e on s.examNum = e.examNum and e.isDelete='F' ", Exam.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<School> getbaseSchoolById(String id) {
        String sql;
        if (id.equals("-1") || id.equals("-2")) {
            sql = "select id,schoolName from school where isDelete='F'";
        } else {
            sql = " select c.id,c.schoolName from school c   left join schauthormanage s on s.schoolNum = c.id and s.userId={id}  left join user t on t.schoolNum = c.id and t.id = {id} and t.usertype=1  where c.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null)  ";
        }
        Map args = StreamMap.create().put("id", (Object) id);
        return this.dao2._queryBeanList(sql, School.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Studentlevel getStudentDetails(String id) {
        StringBuffer sb = new StringBuffer();
        sb.append("select ssg.*,class.className from");
        sb.append("(select ss.*,grade.gradeName from");
        sb.append("(select s.*,school.schoolName from (select id as studentId,studentName,schoolNum,gradeNum,classNum from student where id = {id} and isDelete = 'F')as s left join school on s.schoolNum = school.id and school.isDelete='F')");
        sb.append("as ss left join grade on ss.gradeNum = grade.gradeNum and ss.schoolNum = grade.schoolNum and grade.isDelete = 'F')");
        sb.append("as ssg left join class on ssg.classNum = class.id and class.isDelete = 'F'");
        String sql = sb.toString();
        Map args = StreamMap.create().put("id", (Object) id);
        return (Studentlevel) this.dao2._queryBean(sql, Studentlevel.class, args);
    }

    public String getJIe(String grade, String school) {
        Map args = StreamMap.create().put("grade", (Object) grade).put(License.SCHOOL, (Object) school);
        return String.valueOf(this.dao2._queryObject("SELECT jie from grade where gradeNum ={grade} AND schoolNum = {school} AND isDelete ='F'", args));
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Userparent> getBaseUp(String userid) {
        if (userid == null) {
            return null;
        }
        Map args = StreamMap.create().put("userid", (Object) userid);
        return this.dao2._queryBeanList("select studentRelation,username,realname,mobile,email from userparent WHERE userid = {userid}", Userparent.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Teacher> listTeacher(String schoolNum, String searchNum, String searchName, String searchsex, String searchtitle, int pageStart, int pageSize, String subjectName, String result) {
        String schStr = "";
        if (!schoolNum.equals("-1")) {
            schStr = " rrr.schoolNum={schoolNum} and ";
        }
        String subStr2 = "";
        if (null != subjectName && !"".equals(subjectName) && !"-1".equals(subjectName)) {
            subStr2 = "  rrr.subjectNum={subjectName} and   ";
        }
        String levelstr = "";
        String levelstr2 = "";
        String levelstr3 = "";
        String levelstr4 = "";
        String levelstr5 = " rr.className ";
        if (result.equals("T")) {
            levelstr = " LEFT JOIN levelclass cl ON cl.id = u.classNum AND cl.isDelete='F' ";
            levelstr2 = " ,cl.className levelclassName ";
            levelstr3 = " ,e.levelclassName ";
            levelstr4 = " ,GROUP_CONCAT(r.levelclassName) levelclassName";
            levelstr5 = " rr.levelclassName ";
        }
        String sql2 = "SELECT rrr.teacherNum teacherNum  ,GROUP_CONCAT(rrr.name) ext5 ,rrr.schoolNum,rrr.teacherName,rrr.id , rrr.title,rrr.sex,rrr.userNum ext4,rrr.subjectNum,rrr.schoolName,rrr.mobile,rrr.email  FROM ( SELECT  rr.teacherNum, CONCAT(IF (rr.type=4 ,CONCAT('教研主任：',rr.gradeName,rr.subjectName),''), IF (rr.type=3,CONCAT('年级主任：',rr.gradeName),''), IF(rr.type=2,CONCAT('班主任：',rr.gradeName,rr.className),''),IF(rr.type=1,CONCAT(rr.subjectName,':',rr.gradeName," + levelstr5 + "),'')) name,rr.schoolNum,rr.teacherName,rr.id,rr.title,rr.sex,rr.userNum,rr.subjectNum,rr.schoolName,rr.mobile,rr.email FROM ( SELECT  r.teacherNum,r.subjectName,r.gradeName,r.type,GROUP_CONCAT(r.className) className,r.schoolNum,r.teacherName,r.id,r.title,r.sex,r.userNum,r.subjectNum" + levelstr4 + " ,r.schoolName,r.mobile,r.email FROM (\tSELECT  e.teacherNum,e.gradeNum,e.subjectNum,e.classNum,e.type,e.gradeName,e.subjectName,e.className,e.schoolNum,e.teacherName,e.id,e.title,e.sex,e.userNum" + levelstr3 + ", e.schoolName,e.mobile,e.email FROM\t(\tSELECT t.teacherNum,t.teacherName,u.gradeNum,u.subjectNum,u.classNum,u.type,t.id,t.title,t.sex, us.id userNum \t,g.gradeName,s.subjectName,c.className,t.schoolNum " + levelstr2 + " , \tsch.schoolName,t.mobile,t.email\tFROM\tteacher t LEFT JOIN\t( SELECT id,isDelete,userid FROM `user` WHERE usertype='1')us ON t.id = us.userid AND  t.isDelete='F' \tLEFT JOIN  userposition u  ON us.id = u.userNum\tLEFT JOIN grade g ON u.gradeNum = g.gradeNum AND g.schoolNum = u.schoolNum AND g.isDelete='F' \tLEFT JOIN `subject` s ON s.subjectNum = u.subjectNum LEFT JOIN class c ON c.id = u.classNum AND c.isDelete='F' " + levelstr + "\tLEFT JOIN school  sch ON sch.id  = t.schoolNum \t)e  ) r  GROUP BY  r.teacherNum , r.subjectNum,r.gradeNum,r.type  )rr )rrr  Where " + schStr + subStr2 + "  rrr.teacherNum LIKE {searchNum} and rrr.teacherName LIKE {searchName} and rrr.sex LIKE {searchsex} and rrr.title LIKE {searchtitle}  GROUP BY rrr.teacherNum ORDER BY rrr.teacherNum*1  LIMIT {pageStart},{pageSize}";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("subjectName", (Object) subjectName).put("searchNum", (Object) ("%" + searchNum + "%")).put("searchName", (Object) ("%" + searchName + "%")).put("searchsex", (Object) ("%" + searchsex + "%")).put("searchtitle", (Object) ("%" + searchtitle + "%")).put("pageStart", (Object) Integer.valueOf(pageStart)).put("pageSize", (Object) Integer.valueOf(pageSize));
        return this.dao2._queryBeanList(sql2, Teacher.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Map<String, String> getUserSchoolMap(String uid, String userType) {
        String sql = " select c.id,c.schoolName from school c   left join schauthormanage s on s.schoolNum = c.id and s.userId={uid}  left join user t on t.schoolNum = c.id  and t.id = {uid} and t.usertype=1  where c.isDelete='F' and (s.schoolNum is not null or t.schoolNum is not null)  ";
        if (uid.equals("-2") || uid.equals("-1") || userType.equals("0")) {
            sql = "select id,schoolName from school where isDelete='F'";
        }
        Map args = StreamMap.create().put("uid", (Object) uid);
        return this.dao2._queryOrderMap(sql, TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Map<String, Object>> getTeaMaxPermission(String uid) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM userposition ");
        sql.append("WHERE userNum={uid} ");
        sql.append("order by permission_grade desc,permission_subject desc,permission_class desc ");
        Map args = StreamMap.create().put("uid", (Object) uid);
        return this.dao2._queryMapList(sql.toString(), TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public void addScanManager(Schoolscanpermission sp) {
        Map args = new HashMap();
        args.put("userNum", sp.getUserNum());
        args.put("type", sp.getType());
        args.put(Const.EXPORTREPORT_schoolNum, sp.getSchoolNum());
        args.put("insertUser", sp.getInsertUser());
        this.dao2._execute("INSERT INTO `schoolscanpermission`( `userNum`, `type`, `schoolNum`, `insertUser`, `insertDate`)  VALUES (  {userNum}, {type}, {schoolNum}, {insertUser}, now()) ON DUPLICATE KEY UPDATE userNum = {userNum},type = {type}, schoolNum = {schoolNum},insertUser = {insertUser},insertDate = now()", args);
        addUserroleByRolename(sp.getUserNum(), "扫描管理员", sp.getInsertUser());
    }

    @Override // com.dmj.service.userManagement.UserService
    public void delScanManager(Schoolscanpermission sp) {
        String tcStr = "";
        if (null != sp.getSchoolNum() && !"".equals(sp.getSchoolNum()) && !"-1".equals(sp.getSchoolNum())) {
            tcStr = " AND schoolNum = {schoolNum} ";
        }
        String sql = "DELETE FROM schoolscanpermission WHERE userNum = {userNum} AND type = {type} " + tcStr;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, sp.getSchoolNum());
        args.put("userNum", sp.getUserNum());
        args.put("type", sp.getType());
        this.dao2._execute(sql, args);
        if (null == this.dao2._queryObject("select id from schoolscanpermission where userNum = {userNum} limit 1", args)) {
            delUserroleByRolename(sp.getUserNum(), "扫描管理员");
        }
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getLeftScanUsers(String user, String schoolNum, String leftInputStr) {
        String userStr = "";
        String userStr2 = "";
        String schStr = "";
        String inputStr = "";
        Map args = StreamMap.create().put("user", (Object) user).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("leftInputStr", (Object) ("%" + leftInputStr + "%"));
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={user} and type=1 limit 1", args);
        if (!"-2".equals(user) && !"-1".equals(user) && null == map) {
            userStr2 = " LEFT JOIN (select schoolNum from schoolscanpermission where userNum={user} union select schoolNum from user where id={user}) schm on CAST(schm.schoolNum as char) = CAST(u.schoolNum as char) ";
            userStr = " AND schm.schoolNum is not null ";
        }
        if (null != schoolNum && !"".equals(schoolNum) && !"-1".equals(schoolNum)) {
            schStr = " AND u.schoolNum = {schoolNum} ";
        }
        if (null != leftInputStr && !"".equals(leftInputStr)) {
            inputStr = " AND (u.realName like {leftInputStr} OR u.username like {leftInputStr} ) ";
        }
        String sql = "SELECT u.id,u.userid,u.username,u.realname FROM user u LEFT JOIN schoolscanpermission sp ON sp.userNum = u.id " + userStr2 + "WHERE u.usertype = '1' " + userStr + schStr + inputStr + " AND (sp.type IS NULL OR sp.type = 2) GROUP BY u.id ORDER BY length(u.username),convert(u.username using gbk) ";
        return this.dao2._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getRightScanUsers(String schoolNum, String schoolNum2, String rightInputStr, String user) {
        String userStr = "";
        String schStr = "";
        String inputStr = "";
        Map args = StreamMap.create().put("user", (Object) user).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("rightInputStr", (Object) ("%" + rightInputStr + "%")).put("schoolNum2", (Object) schoolNum2);
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={user} and type=1 limit 1", args);
        if (!"-2".equals(user) && !"-1".equals(user) && null == map) {
            userStr = "inner JOIN (select schoolNum from schoolscanpermission where userNum={user} union select schoolNum from user where id={user}) schm ON CAST(schm.schoolNum as char) = CAST(u.schoolNum as char) ";
        }
        if (null != schoolNum2 && !"".equals(schoolNum2) && !"-1".equals(schoolNum2)) {
            schStr = " AND u.schoolNum = {schoolNum2} ";
        }
        if (null != rightInputStr && !"".equals(rightInputStr)) {
            inputStr = " AND (u.realName like {rightInputStr} OR u.username like {rightInputStr} ) ";
        }
        String sql = "SELECT sp.userNum id,u.userid,u.username,u.realname FROM schoolscanpermission sp LEFT JOIN user u ON u.id = sp.userNum " + userStr + "WHERE sp.schoolNum={schoolNum} " + schStr + inputStr + " ORDER BY length(u.username),convert(u.username using gbk) ";
        return this.dao2._queryBeanList(sql, User.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getTcScaners(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._queryBeanList("SELECT userNum,type FROM schoolscanpermission WHERE schoolNum = {schoolNum}", Schoolscanpermission.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public int delScanManagers(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao2._execute("DELETE FROM schoolscanpermission WHERE schoolNum = {schoolNum}", args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public int addTeacherPhoneNum(String teacherNum, String phoneNum) {
        Map args = StreamMap.create().put("phoneNum", (Object) phoneNum).put("teacherNum", (Object) teacherNum);
        return this.dao2._execute("UPDATE teacher set mobile = {phoneNum} where id = {teacherNum}", args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List getSchByStuId(String studentId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId);
        return this.dao2._queryArrayList("SELECT sch.id,sch.schoolName FROM school sch LEFT JOIN student stu ON sch.id = stu.schoolNum WHERE stu.studentId = {studentId}", args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Object checkStuidAndStuname(String studentId, String studentName) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId).put("studentName", (Object) studentName);
        return this.dao2._queryObject("SELECT id FROM student WHERE studentId = {studentId} AND studentName = {studentName}", args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Student> getParentsStudent(String mobile) {
        Map args = StreamMap.create().put("mobile", (Object) mobile);
        return this.dao2._queryBeanList("select userp.userid id,stu.studentName,stu.studentId,sch.schoolName,bg.gradeName,c.className from userparent userp LEFT JOIN student stu on userp.userid = stu.id LEFT JOIN school sch on sch.id=stu.schoolNum LEFT JOIN basegrade bg on bg.gradeNum=stu.gradeNum LEFT JOIN class c on c.id=stu.classNum where userp.mobile={mobile} ", Student.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Map<String, Object>> get57UserListInfoByUserId(String userids, int type) {
        String sql;
        Map args = StreamMap.create().put("userids", (Object) userids);
        if (type == 1) {
            sql = "";
        } else if (type == 2) {
            sql = "select s.studentId,s.studentNum, s.studentName,s.schoolNum,s.gradeNum,s.classNum,b.gradeName,c.className from (select st.studentId,st.studentNum,st.studentName,st.schoolNum,st.gradeNum,st.classNum from student st where st.studentId in ({userids[]}) and st.isDelete='F')as s  left join basegrade b on b.gradeNum = s.gradeNum left join class c on c.schoolNum = s.schoolNum and c.gradeNum = s.gradeNum and c.id = s.classNum ";
        } else {
            return null;
        }
        return this.dao2._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<User> getAssignedUsers(String roleNum, String gradeNum, String userType, String userName) {
        return this.dao.getAssignedUsers(roleNum, gradeNum, userType, userName);
    }

    @Override // com.dmj.service.userManagement.UserService
    public boolean couldUseOnBind(String mobile, String userType) {
        String sql = "";
        if ("T".equals(userType)) {
            sql = "select count(id) from teacher where mobile = {mobile}";
        } else if (Const.exampaper_doubleFaced_S.equals(userType)) {
            sql = "select count(id) from user where usertype='2' and mobile = {mobile}";
        }
        Map args = StreamMap.create().put("mobile", (Object) mobile);
        int n = Integer.parseInt(this.dao2._queryObject(sql, args).toString());
        if (n > 0) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.userManagement.UserService
    public int updateUserMobile(String userid, String usertype, String mobile) {
        Map args = StreamMap.create().put("mobile", (Object) mobile).put("userid", (Object) userid).put("usertype", (Object) usertype);
        return this.dao2._execute("update user set mobile = {mobile} where userid = {userid} and usertype = {usertype}", args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String getParentPasswordByMobile(String mobile) {
        Map args = StreamMap.create().put("mobile", (Object) mobile);
        Object obj = this.dao2._queryObject("select password from userparent where mobile = {mobile} and isdelete='F' limit 1 ", args);
        if (null == obj) {
            return "";
        }
        return obj.toString();
    }

    @Override // com.dmj.service.userManagement.UserService
    public String ifYueJuanPrivileged(String user, String num) {
        String flag = "F";
        if (user.equals("-2") || user.equals("-1")) {
            return "T";
        }
        Map args = StreamMap.create().put("num", (Object) num).put("user", (Object) user);
        List<Map<String, Object>> list1 = this.dao2._queryMapList("select distinct roleNum from resourcerole where resource={num}", TypeEnum.StringObject, args);
        List<Map<String, Object>> list2 = this.dao2._queryMapList("select distinct roleNum from userrole where userNum={user}", TypeEnum.StringObject, args);
        List<Object> list11 = new ArrayList<>();
        List<Object> list22 = new ArrayList<>();
        for (Map<String, Object> map : list1) {
            list11.add(map.get("roleNum"));
        }
        for (Map<String, Object> map2 : list2) {
            list22.add(map2.get("roleNum"));
        }
        Iterator<Object> it = list22.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object object = it.next();
            int i = list11.indexOf(object);
            if (i != -1) {
                flag = "T";
                break;
            }
        }
        return flag;
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserByTeacherNum(String teacherNum, String userType) {
        String sql = "select u.* from user u ";
        if ("104".equals(userType)) {
            sql = sql + "  where u.id = '-1' ";
        } else if ("101".equals(userType) || "105".equals(userType)) {
            sql = sql + " left join teacher t on u.userId = t.id where t.teacherNum = {teacherNum}";
        } else if (userType.equals("102")) {
            sql = sql + " left join student s on u.userId = s.id where s.studentId = {teacherNum}";
        }
        Map args = StreamMap.create().put("teacherNum", (Object) teacherNum);
        User user = (User) this.dao2._queryBean(sql, User.class, args);
        return user;
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserByAccount(String account) {
        Map args = StreamMap.create().put("account", (Object) account);
        User user = (User) this.dao2._queryBean("select u.* from user u left join teacher t on u.userid=t.id where t.teacherNum= {account}", User.class, args);
        if (user.getId() == null || user.getId().equals("")) {
            user = (User) this.dao2._queryBean("select u.* from user u left join student s on u.userid=s.id where s.studentId= {account}", User.class, args);
        }
        return user;
    }

    @Override // com.dmj.service.userManagement.UserService
    public User getUserByUsername(String username, String usertype) {
        String sql;
        if ("3".equals(usertype)) {
            sql = "select id,schoolNum schoolNum,usertype,username from userparent where username = {username} or loginname = {username}";
        } else {
            sql = "select id,schoolnum schoolNum,usertype,username from user where (username = {username} or loginname = {username}) and usertype = {usertype}";
        }
        Map args = StreamMap.create().put("username", (Object) username).put("usertype", (Object) usertype);
        return (User) this.dao2._queryBean(sql, User.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public boolean isAuthSchool(String uId, String schoolNum) {
        Map args = StreamMap.create().put("uId", (Object) uId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        if (null == this.dao2._queryObject("(SELECT id from user WHERE id = {uId} and schoolnum = {schoolNum}) UNION (SELECT id from schauthormanage where userId = {uId} and schoolNum = {schoolNum})", args)) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.userManagement.UserService
    public User queryUserById(String userId) {
        User u = new User();
        if ("-1".equals(userId) || "-2".equals(userId)) {
            u.setId(userId);
            u.setUsername("超级管理员");
            u.setRealname("超级管理员");
        } else {
            Map args = StreamMap.create().put("userId", (Object) userId);
            u = (User) this.dao2._queryBean("SELECT id,username,realname from user where id={userId}", User.class, args);
        }
        return u;
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<User> getRoleSjtLeader(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao2._queryBeanList("SELECT u.id,u.realname,u.username,sl.schoolName from questionGroup_user qu  INNER JOIN user u on qu.userNum=u.id  inner join school sl on u.schoolNum=sl.id  where qu.examPaperNum={examPaperNum} and qu.userType='2' ", User.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<User> getAllRoleSjtLeader(User user, int start, int pagesize, String examNum, String gradeNum) {
        List<User> list;
        if (null == user || null == (list = this.dao.getAllUser(user, start, pagesize, examNum, gradeNum)) || list.size() == 0) {
            return null;
        }
        return list;
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Resource> getUserResource(String userId) {
        Map args = StreamMap.create().put("userId", (Object) userId);
        return this.dao2._queryBeanList("SELECT DISTINCT res.num,res.openAllSchool from resource res LEFT JOIN resourcerole resr on resr.resource = res.num LEFT JOIN userrole ur on ur.roleNum = resr.roleNum where ur.userNum = {userId} and res.type = '2'", Resource.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<AjaxData> getTestCenterScannerList(String examNum, String userId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryBeanList("SELECT tc.id num,tc.testingCentreName name, closeSecondaryPositioning ext4 ,ismusttemplate ext5  FROM testingcentre tc WHERE tc.examNum = {examNum} ORDER BY convert(tc.testingCentreName using gbk)", AjaxData.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<AjaxData> getSchoolScannerList(String userId) {
        Map args = new HashMap();
        args.put("userId", userId);
        String sql = "SELECT sch.id num,sch.schoolName name, sum(IF(u.username is null,0,1)) ext1, GROUP_CONCAT(IFNULL(u.username,'') ORDER BY CONVERT (concat(u.realname,u.username) USING gbk)) ext2, GROUP_CONCAT(IFNULL(u.realname,'') ORDER BY CONVERT (concat(u.realname,u.username) USING gbk)) ext3 FROM school sch LEFT JOIN schoolscanpermission sp on sp.schoolNum = sch.id LEFT JOIN user u ON u.id = sp.userNum  where sch.isdelete='F' GROUP BY sch.id ORDER BY convert(sch.schoolName using gbk)";
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if (!userId.equals("-2") && !userId.equals("-1") && null == map) {
            sql = "SELECT sch.id num,sch.schoolName name, sum(IF(u.username is null,0,1)) ext1, GROUP_CONCAT(IFNULL(u.username,'') ORDER BY CONVERT (concat(u.realname,u.username) USING gbk)) ext2, GROUP_CONCAT(IFNULL(u.realname,'') ORDER BY CONVERT (concat(u.realname,u.username) USING gbk)) ext3 FROM school sch LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} union select schoolNum from user where id={userId}) sp on CAST(sp.schoolNum as char) = CAST(sch.id as char) LEFT JOIN schoolscanpermission sp2 on sp2.schoolNum = sp.schoolNum LEFT JOIN user u ON u.id = sp2.userNum LEFT JOIN (select schoolNum from schoolscanpermission where userNum={userId} union select schoolNum from user where id={userId}) sp3 on CAST(sp3.schoolNum as char) = CAST(u.schoolNum as char) WHERE sp.schoolNum is not null and sp3.schoolNum is not null and sch.isdelete='F' GROUP BY sch.id ORDER BY convert(sch.schoolName using gbk)";
        }
        return this.dao2._queryBeanList(sql, AjaxData.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer updateCloseSecondaryPositioning(String testingcentreId, String closeOrOpenSecondaryPositioning, String userId) {
        Map args = new HashMap();
        args.put("closeOrOpenSecondaryPositioning", closeOrOpenSecondaryPositioning);
        args.put("userId", userId);
        args.put("insertDate", DateUtil.getCurrentTime());
        args.put("testingcentreId", testingcentreId);
        Integer isSuccess = 0;
        try {
            this.dao2._execute("update testingcentre set closeSecondaryPositioning={closeOrOpenSecondaryPositioning},insertUser={userId},insertDate={insertDate} where id={testingcentreId}", args);
            isSuccess = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer updateismusttemplate(String testingcentreId, String ismusttemplate, String userId, String examNum) {
        Map args = new HashMap();
        args.put("ismusttemplate", ismusttemplate);
        args.put("userId", userId);
        args.put("insertDate", DateUtil.getCurrentTime());
        args.put("testingcentreId", testingcentreId);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        Integer isSuccess = 0;
        try {
            this.dao2._execute("update testingcentre set ismusttemplate={ismusttemplate},insertUser={userId},insertDate={insertDate} where id={testingcentreId} and examNum={examNum}", args);
            isSuccess = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Exam> getAssignedExamList(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao2._queryBeanList("select DISTINCT sp.examNum,e.examName from scanpermission sp LEFT JOIN exam e on e.examNum = sp.examNum where e.examNum <> {examNum} ORDER BY e.examDate desc,e.insertDate desc", Exam.class, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public void quoteExamScanners(String examNum, String assignedExamNum, String userId) {
        deleteScannersByExam();
        String sql = "INSERT INTO schoolscanpermission (userNum,type,examNum,testingCentreId,insertUser,insertDate) SELECT sp.userNum,sp.type," + examNum + ",tc.id," + userId + ",now() from (SELECT id,testingCentreName from testingcentre where examNum = {examNum}) tc INNER JOIN (SELECT id,testingCentreName from testingcentre where examNum = {assignedExamNum}) tc2 on tc2.testingCentreName = tc.testingCentreName INNER JOIN (select testingCentreId,type,userNum from scanpermission where examNum = {assignedExamNum}) sp on sp.testingCentreId = tc2.id";
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("assignedExamNum", (Object) assignedExamNum);
        this.dao2._execute(sql, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public void deleteScannersByExam() {
        this.dao2._execute("delete from schoolscanpermission", null);
    }

    @Override // com.dmj.service.userManagement.UserService
    public String exportExamScanners(String examNum, String examName, String userId, String dirPath) {
        String folderPath = "ExportFolder/exportExamScannersExcel/" + userId;
        File excelFile = this.qgs.getRptExcelFile("导出学校扫描员信息", dirPath, folderPath);
        try {
            WritableWorkbook wwBook = Workbook.createWorkbook(excelFile);
            try {
                WritableSheet sheet = wwBook.createSheet("学校扫描员信息", 0);
                try {
                    List<AjaxData> dataList = getSchoolScannerList(userId);
                    String[] excelTitle = {"学校名称", "扫描员用户名", "扫描员姓名"};
                    for (int i = 0; i < excelTitle.length; i++) {
                        Label titleCell = new Label(i, 0, excelTitle[i]);
                        sheet.addCell(titleCell);
                        sheet.setColumnView(i, 15);
                    }
                    int index = 0;
                    for (int j = 0; j < dataList.size(); j++) {
                        AjaxData tc = dataList.get(j);
                        int tcScannersCount = Integer.valueOf(tc.getExt1()).intValue();
                        String[] scannersUsername = tc.getExt2().split(Const.STRING_SEPERATOR);
                        String[] scannersRealname = tc.getExt3().split(Const.STRING_SEPERATOR);
                        if (!"".equals(tc.getExt2())) {
                            for (int z = 0; z < scannersUsername.length; z++) {
                                Label tcCell = new Label(0, z + 1 + index, tc.getName());
                                sheet.addCell(tcCell);
                                Label userNameCell = new Label(1, z + 1 + index, scannersUsername[z]);
                                sheet.addCell(userNameCell);
                                Label realNameCell = new Label(2, z + 1 + index, scannersRealname[z]);
                                sheet.addCell(realNameCell);
                            }
                        } else {
                            Label tcCell2 = new Label(0, 1 + index, tc.getName());
                            sheet.addCell(tcCell2);
                            tcScannersCount = 1;
                        }
                        index += tcScannersCount;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wwBook.write();
                if (wwBook != null) {
                    try {
                        wwBook.close();
                    } catch (WriteException e2) {
                        e2.printStackTrace();
                    }
                }
            } catch (Exception e3) {
                if (wwBook != null) {
                    try {
                        wwBook.close();
                    } catch (WriteException e4) {
                        e4.printStackTrace();
                    }
                }
            } catch (Throwable th) {
                if (wwBook != null) {
                    try {
                        wwBook.close();
                    } catch (WriteException e5) {
                        e5.printStackTrace();
                        throw th;
                    }
                }
                throw th;
            }
        } catch (Exception e6) {
            e6.printStackTrace();
        }
        return excelFile.getName();
    }

    @Override // com.dmj.service.userManagement.UserService
    public RspMsg checkScannerFile(File file, String fileName, String userId) {
        Row row;
        this.errorFlag = false;
        ExcelHelper excelHelper = new ExcelHelper(file);
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = excelHelper.creatWorkbook();
            Sheet sheet = workbook.getSheetAt(0);
            Row row0 = sheet.getRow(0);
            int columnLen = row0.getPhysicalNumberOfCells();
            if (null != row0 && columnLen > 3) {
                return new RspMsg(410, "excel文件第一行的表头列数与导入模板不符合，请检查！", null);
            }
            CellStyle errorRowStyle = workbook.createCellStyle();
            errorRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            errorRowStyle.setFillForegroundColor((short) 10);
            CellStyle errorCellStyle = workbook.createCellStyle();
            errorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            errorCellStyle.setFillForegroundColor((short) 13);
            Set<String> tc_userSet = new HashSet<>();
            for (int i = 1; i < sheet.getPhysicalNumberOfRows() && null != (row = sheet.getRow(i)); i++) {
                this.rowBgColor = false;
                Map<String, Object> schoolmap = null;
                Cell tcNameCell = row.getCell(0);
                String tcName = "";
                if (null == tcNameCell) {
                    tcNameCell = row.createCell(0);
                } else {
                    tcName = CheckCellUtil.getCellValue(tcNameCell);
                }
                if ("".equals(tcName) || "ERROR".equals(tcName)) {
                    setError(file, sheet, row, tcNameCell, "学校名称不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else {
                    String finalTcName = tcName;
                    Map args_sql = StreamMap.create().put("tcName", (Object) finalTcName);
                    if (null == this.dao2._queryObject("SELECT id from school where schoolName = {tcName}", args_sql)) {
                        String pizhu = "系统中当前考试没有 " + tcName + " 这个学校";
                        setError(file, sheet, row, tcNameCell, pizhu, columnLen, errorRowStyle, errorCellStyle);
                    }
                    Map args3 = new HashMap();
                    args3.put("userNum", userId);
                    args3.put(License.SCHOOL, this.dao2._queryObject("SELECT id from school where schoolName = {tcName}", args_sql));
                    schoolmap = this.dao2._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args3);
                    if (null == schoolmap && !userId.equals("-1") && !userId.equals("-2") && null == this.dao2._queryObject("select schoolNum from schoolscanpermission where userNum={userNum} and schoolNum={school} ", args3)) {
                        String pizhu2 = "您没有 " + tcName + " 这个学校的操作权限";
                        setError(file, sheet, row, tcNameCell, pizhu2, columnLen, errorRowStyle, errorCellStyle);
                    }
                }
                Cell usernameCell = row.getCell(1);
                String username = "";
                if (null == usernameCell) {
                    usernameCell = row.createCell(1);
                } else {
                    username = CheckCellUtil.getCellValue(usernameCell);
                }
                if ("".equals(username) || "ERROR".equals(username)) {
                    setError(file, sheet, row, usernameCell, "扫描员用户名不能为空", columnLen, errorRowStyle, errorCellStyle);
                } else {
                    String finalUsername = username;
                    Map args_sql2 = StreamMap.create().put("username", (Object) finalUsername).put("userNum", (Object) userId);
                    if (null == this.dao2._queryObject("SELECT realname from user where username = {username} and usertype = '1'", args_sql2)) {
                        String pizhu3 = "系统中没有 " + username + " 这个教师用户";
                        setError(file, sheet, row, usernameCell, pizhu3, columnLen, errorRowStyle, errorCellStyle);
                    } else {
                        if (null == this.dao2._queryStr("SELECT u.id from user u LEFT JOIN  (SELECT schoolNum FROM schoolscanpermission where userNum={userNum} UNION SELECT schoolNum from `user` WHERE id={userNum}) schm ON u.schoolNum=schm.schoolNum WHERE u.username ={username} AND schm.schoolNum is not null", args_sql2) && schoolmap == null && !userId.equals("-1") && !userId.equals("-2")) {
                            String pizhu4 = "你没有 " + username + " 这个教师用户的操作权限";
                            setError(file, sheet, row, usernameCell, pizhu4, columnLen, errorRowStyle, errorCellStyle);
                        }
                        if (!"".equals(tcName) && !"ERROR".equals(tcName) && !tc_userSet.add(tcName + "_" + username)) {
                            String pizhu5 = "excel中学校 " + tcName + "，扫描员用户 " + username + " 数据重复，请检查";
                            setError(file, sheet, row, usernameCell, pizhu5, columnLen, errorRowStyle, errorCellStyle);
                        }
                        String userRealname = this.dao2._queryObject("SELECT realname from user where username = {username} and usertype = '1'", args_sql2).toString();
                        Cell realnameCell = row.getCell(2);
                        String realname = "";
                        if (null == realnameCell) {
                            realnameCell = row.createCell(2);
                        } else {
                            realname = CheckCellUtil.getCellValue(realnameCell);
                        }
                        if (!"".equals(realname) && !"ERROR".equals(realname) && !realname.equals(userRealname)) {
                            setError(file, sheet, row, realnameCell, "扫描员用户名与姓名信息不匹配，请检查", columnLen, errorRowStyle, errorCellStyle);
                        }
                    }
                }
            }
            if (this.errorFlag) {
                FileOutputStream fOut = new FileOutputStream(file);
                workbook.write(fOut);
                fOut.flush();
                fOut.close();
                return new RspMsg(Const.height_500, "导入文件信息错误", null);
            }
            return new RspMsg(200, "导入文件信息正确", null);
        } catch (Exception e) {
            return new RspMsg(410, "check error:" + e.getMessage(), null);
        }
    }

    public void setError(File file, Sheet sheet, Row row, Cell cell, String pizhu, int columnLen, CellStyle errorRowStyle, CellStyle errorCellStyle) {
        if (!this.rowBgColor) {
            CheckCellUtil.setRowStyle(row, columnLen, errorRowStyle);
            this.rowBgColor = true;
            this.errorFlag = true;
        }
        CheckCellUtil.setCellStyle(file, sheet, cell, pizhu, errorCellStyle);
    }

    @Override // com.dmj.service.userManagement.UserService
    public void importExamScanners(File file, String fileName, String userId, String clearInput) {
        Row row;
        String schoolNum;
        String scan_userId;
        if ("1".equals(clearInput)) {
            deleteScannersByExam();
        }
        ExcelHelper excelHelper = new ExcelHelper(file);
        org.apache.poi.ss.usermodel.Workbook workbook = excelHelper.creatWorkbook();
        Sheet sheet = workbook.getSheetAt(0);
        List<Map<String, Object>> insertSpParams = new ArrayList<>();
        Map<String, String> testingCentreNameMap = new HashMap<>();
        Map<String, String> usernameMap = new HashMap<>();
        String currentTime = DateUtil.getCurrentTime();
        int len = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < len && null != (row = sheet.getRow(i)); i++) {
            Cell schoolNameCell = row.getCell(0);
            String schoolName = CheckCellUtil.getCellValue(schoolNameCell);
            if (testingCentreNameMap.containsKey(schoolName)) {
                schoolNum = testingCentreNameMap.get(schoolName);
            } else {
                Map args_sql = StreamMap.create().put("schoolName", (Object) schoolName);
                schoolNum = String.valueOf(this.dao2._queryObject("select id from school where schoolName = {schoolName}", args_sql));
                testingCentreNameMap.put(schoolName, schoolNum);
            }
            Cell usernameCell = row.getCell(1);
            String username = CheckCellUtil.getCellValue(usernameCell);
            if (usernameMap.containsKey(username)) {
                scan_userId = usernameMap.get(username);
            } else {
                Map args_sql2 = StreamMap.create().put("username", (Object) username);
                scan_userId = String.valueOf(this.dao2._queryObject("SELECT id from user where username = {username} and usertype = '1'", args_sql2));
                usernameMap.put(username, scan_userId);
            }
            Object id = null;
            if (!"1".equals(clearInput)) {
                String finalSchoolNum = schoolNum;
                String finalScan_userId = scan_userId;
                Map args_selectSpSql = StreamMap.create().put("type", (Object) "2").put(Const.EXPORTREPORT_schoolNum, (Object) finalSchoolNum).put("userNum", (Object) finalScan_userId);
                id = this.dao2._queryObject("select id from schoolscanpermission where type={type} and schoolNum={schoolNum} and userNum={userNum} ", args_selectSpSql);
            }
            if (null == id) {
                String finalScan_userId1 = scan_userId;
                String finalSchoolNum1 = schoolNum;
                Map args_insertSpSql = StreamMap.create().put("userNum", (Object) finalScan_userId1).put("type", (Object) "2").put(Const.EXPORTREPORT_schoolNum, (Object) finalSchoolNum1).put("insertUser", (Object) userId).put("insertDate", (Object) currentTime);
                insertSpParams.add(args_insertSpSql);
            }
            addUserroleByRolename(scan_userId, "扫描管理员", userId);
        }
        if (insertSpParams.size() > 0) {
            this.dao2._batchUpdate("INSERT INTO schoolscanpermission (`userNum`, `type`, `schoolNum`, `insertUser`, `insertDate`) VALUES ({userNum},{type},{schoolNum},{insertUser},{insertDate})", insertSpParams, 100);
        }
    }

    @Override // com.dmj.service.userManagement.UserService
    public String getStudentIdByUserId(String userId) {
        Map args = StreamMap.create().put("userId", (Object) userId);
        return String.valueOf(this.dao2._queryObject("SELECT s.studentId from student s LEFT JOIN `user` u on u.userid=s.id where u.id={userId}", args));
    }

    @Override // com.dmj.service.userManagement.UserService
    public void deleteAllUserFromOnlineUser() {
        this.dao.deleteAllUserFromOnlineUser();
    }

    public void addUserroleByRolename(String userId, String roleName, String loginUserId) {
        Map args = new HashMap();
        args.put("roleName", roleName);
        Object roleNum = this.dao2._queryObject("select roleNum from role where roleName={roleName} limit 1", args);
        if (null != roleNum) {
            args.put("userNum", userId);
            args.put("roleNum", roleNum);
            if (null == this.dao2._queryObject("select id from userrole where userNum={userNum} and roleNum={roleNum} limit 1", args)) {
                args.put("insertUser", loginUserId);
                args.put("insertDate", DateUtil.getCurrentTime());
                this.dao2.save("userrole", args);
            }
        }
    }

    public void delUserroleByRolename(String userId, String roleName) {
        Map args = new HashMap();
        args.put("roleName", roleName);
        Object roleNum = this.dao2._queryObject("select roleNum from role where roleName={roleName} limit 1", args);
        if (null != roleNum) {
            args.put("userNum", userId);
            args.put("roleNum", roleNum);
            if (null != this.dao2._queryObject("select id from userrole where userNum={userNum} and roleNum={roleNum} limit 1", args)) {
                this.dao2._execute("delete from userrole where userNum={userNum} and roleNum={roleNum}", args);
            }
        }
    }

    @Override // com.dmj.service.userManagement.UserService
    public List<Map<String, Object>> getZikemuListByPexamPaperNum(String pexamPaperNum) {
        Map args = new HashMap();
        args.put("pexamPaperNum", pexamPaperNum);
        return this.dao2._queryMapList("select ep.examPaperNum,ep.subjectNum,sub.subjectName from exampaper ep left join subject sub on sub.subjectNum=ep.subjectNum where ep.pexamPaperNum={pexamPaperNum} and ep.isHidden='T' order by sub.orderNum", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Map findTuiJianRen(String studentId) {
        return this.dao2.querySimpleMap("SELECT s.*,sl.schoolName,IFNULL(up.username,'') tel from student s JOIN school sl on s.schoolNum = sl.id JOIN user u on u.tuijianren = s.studentId  LEFT JOIN userparent up on up.userid=s.id WHERE u.username=? LIMIT 1", new Object[]{studentId});
    }

    @Override // com.dmj.service.userManagement.UserService
    public Map findZhuceTuijianren(String studentId) {
        return this.dao2.querySimpleMap("select s.*,IFNULL(u.username,'') tel  from student s  left join userparent u on s.id=u.userid where s.studentId=? limit 1", new Object[]{studentId});
    }

    @Override // com.dmj.service.userManagement.UserService
    public Map<String, Object> getlockMap(String userid, String userType) {
        String sql;
        if (userType.equals(Const.exampaper_doubleFaced_S)) {
            sql = "SELECT u.id,ifnull(u.errornum,0) errornum,ifnull(u.locktime,'') locktime from user u LEFT JOIN student s ON u.userId=s.id WHERE s.studentId= {userid} and u.isdelete='F' ";
        } else if (userType.equals("P")) {
            sql = "SELECT id,ifnull(errornum,0) errornum,ifnull(locktime,'') locktime from userparent u  WHERE username= {userid} and isdelete='F' ";
        } else {
            sql = "SELECT id,ifnull(errornum,0) errornum,ifnull(locktime,'') locktime from user WHERE username= {userid} and isdelete='F' ";
        }
        Map args = new HashMap();
        args.put("userid", userid);
        return this.dao2._querySimpleMap(sql, args);
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer updateErrornum(String userid, Integer errornum, String userType) {
        String sql;
        if (userType.equals("P")) {
            sql = " update userparent set errornum={num} WHERE id={userid} ";
        } else {
            sql = " update user set errornum={num} WHERE id={userid} ";
        }
        Map args = new HashMap();
        args.put("num", errornum);
        args.put("userid", userid);
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    @Override // com.dmj.service.userManagement.UserService
    public Integer updateLocktime(String userid, String locktime, String userType) {
        String sql;
        if (userType.equals("P")) {
            sql = "update userparent set errornum=0 , locktime={locktime} WHERE id={userid}";
        } else {
            sql = "update user set errornum=0 , locktime={locktime} WHERE id={userid}";
        }
        Map args = new HashMap();
        args.put("locktime", locktime);
        args.put("userid", userid);
        return Integer.valueOf(this.dao2._execute(sql, args));
    }

    @Override // com.dmj.service.userManagement.UserService
    public String logoutparent(String usernum) {
        Map args = new HashMap();
        args.put("usernum", usernum);
        return Convert.toStr(Integer.valueOf(this.dao2._execute("delete from userparent where username = {usernum}", args)));
    }
}
