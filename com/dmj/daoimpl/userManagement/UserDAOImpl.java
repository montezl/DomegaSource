package com.dmj.daoimpl.userManagement;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.questionGroup.QuestionGroupDaoImpl;
import com.dmj.domain.Class;
import com.dmj.domain.Data;
import com.dmj.domain.Grade;
import com.dmj.domain.OnlineUser;
import com.dmj.domain.Role;
import com.dmj.domain.School;
import com.dmj.domain.Student;
import com.dmj.domain.User;
import com.dmj.domain.Userparent;
import com.dmj.domain.Userposition;
import com.dmj.util.Const;
import com.dmj.util.config.Configuration;
import com.zht.db.StreamMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;

/* loaded from: UserDAOImpl.class */
public class UserDAOImpl {
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());
    QuestionGroupDaoImpl qdi = new QuestionGroupDaoImpl();

    public User getUserByUsernameAndPwd(User user, String logtype) {
        return getUserByUsernameAndPwd(user, logtype, "0");
    }

    public User getUserByUsernameAndPwd(User user, String logtype, String singleLogin) {
        new User();
        Map args = new HashMap();
        args.put("username", user.getUsername());
        String passWhere = "";
        if (!"1".equals(singleLogin)) {
            args.put("password", user.getPassword().toLowerCase());
            passWhere = " and password= md5({password} ) ";
        }
        if ("T".equals(logtype) || Const.exampaper_doubleFaced_S.equals(logtype)) {
            String sql2 = "select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete   from user where ( username = {username})" + passWhere;
            User user2 = (User) this.dao._queryBean(sql2, User.class, args);
            if (null == user2 || "".equals(user2.getId())) {
                String sql = "select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete   from user where (loginname={username} )" + passWhere;
                User user22 = (User) this.dao._queryBean(sql, User.class, args);
                if (null == user22 || "".equals(user22.getId())) {
                    return null;
                }
                if ((user22.getUsertype().equals("1") && "T".equals(logtype)) || ((user22.getUsertype().equals("2") && Const.exampaper_doubleFaced_S.equals(logtype)) || user22.getUsertype().equals("0") || user22.getUsertype().equals("9"))) {
                    return user22;
                }
                return null;
            }
            if ((user2.getUsertype().equals("1") && "T".equals(logtype)) || ((user2.getUsertype().equals("2") && Const.exampaper_doubleFaced_S.equals(logtype)) || user2.getUsertype().equals("0") || user2.getUsertype().equals("9"))) {
                return user2;
            }
            return null;
        }
        if ("P".equals(logtype)) {
            String sql3 = "select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete from user where userid = (select userid from userparent where username = {username} " + passWhere + "  LIMIT 1) ";
            User user23 = (User) this.dao._queryBean(sql3, User.class, args);
            if ("".equals(user23) || null == user23 || "".equals(user23.getId())) {
                String sql21 = "select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete from user where userid = (select userid from userparent where loginname = {username}  " + passWhere + "  LIMIT 1) ";
                User user24 = (User) this.dao._queryBean(sql21, User.class, args);
                if ("".equals(user24) || null == user24 || "".equals(user24.getId())) {
                    return null;
                }
                return user24;
            }
            return user23;
        }
        return null;
    }

    public User getUserByMobile(User user) {
        String userType;
        new User();
        if (Const.exampaper_doubleFaced_S.equals(user.getExt1())) {
            userType = " and usertype = '2' ";
        } else {
            userType = " and usertype = '1' ";
        }
        String sql = "select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete   from user where 1=1 " + userType + "and mobile = {mobile} and password= md5({password}) limit 1 ";
        Map args = new HashMap();
        args.put("mobile", user.getMobile());
        args.put("password", user.getPassword().toLowerCase());
        User user2 = (User) this.dao._queryBean(sql, User.class, args);
        if (null == user2 || "".equals(user2.getId())) {
            return null;
        }
        return user2;
    }

    public User getUserByUsername(User user, String logtype) {
        Map args = new HashMap();
        args.put("username", user.getUsername());
        new User();
        User user2 = (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete   from user where (loginname={username} or username = {username})", User.class, args);
        if (Const.exampaper_doubleFaced_S.equals(logtype)) {
            User user22 = (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete   from user where (loginname={username} or username ={username} )", User.class, args);
            if (null == user22 || "".equals(user22.getId())) {
                return null;
            }
            if (user22.getUsertype().equals("2")) {
                return user22;
            }
            if (user22.getUsertype().equals("0")) {
                return user22;
            }
            if (user22.getUsertype().equals("9")) {
                return user22;
            }
            return null;
        }
        if ("T".equals(logtype)) {
            User user23 = (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete   from user where (loginname={username} or username = {username} )", User.class, args);
            if (null == user23 || "".equals(user23.getId())) {
                return null;
            }
            if (user23.getUsertype().equals("1")) {
                return user23;
            }
            if (user23.getUsertype().equals("0")) {
                return user23;
            }
            if (user23.getUsertype().equals("9")) {
                return user23;
            }
            return null;
        }
        if ("P".equals(logtype)) {
            User user24 = (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete from userparent where (username ={username}  or loginname ={username} ) limit 1 ", User.class, args);
            if ("".equals(user24) || null == user24 || "".equals(user24.getId())) {
                user24 = (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete   from userparent where loginname={username} limit 1", User.class, args);
            }
            if (null == user24) {
                User user25 = (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete   from user where (loginname={username}  or username = {username} ) limit 1", User.class, args);
                if (null == user25 || "".equals(user)) {
                    return null;
                }
                if (user25.getUsertype().equals("0")) {
                    return user25;
                }
                if (user25.getUsertype().equals("9")) {
                    return user25;
                }
                return null;
            }
            user2 = (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete from user where userid = (select userid from userparent where (username = {username} or loginname={username} ) limit 1)", User.class, args);
        }
        return user2;
    }

    public User getUserParentByUsernameAndPwd(User user) {
        Map args = new HashMap();
        args.put("username", user.getUsername());
        args.put("password", user.getPassword().toLowerCase());
        User userParent = (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,up.schoolNum,up.isUser,loginname,up.isdelete,roleName,email,mobile from userparent up left join role r on r.roleNum = up.usertype where username = {username}   and password= md5({password}) ", User.class, args);
        if ("".equals(userParent) || null == userParent || "".equals(userParent.getId())) {
            userParent = (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,up.schoolNum,up.isUser,loginname,up.isdelete,roleName   from userparent up left join role r on r.roleNum = up.usertype  where loginname={username} and password= md5({password}) ", User.class, args);
        }
        return userParent;
    }

    public List getAllUser(User user, int start, int pagesize, String examNum, String gradeNum) {
        StringBuffer sql = new StringBuffer();
        if ("3".equals(user.getUsertype())) {
            sql.append("select userparent.id,userparent.userid,userparent.studentRelation,user.username stuname,user.realName sturealname,userparent.username,userparent.realName,userparent.usertype,sch.schoolName schoolName,gra.gradeName gradeName,cla.className className from userparent ");
            sql.append(" left join user on  userparent.userid=user.userid ");
            sql.append(" left join student stu on  stu.id=user.userid ");
            sql.append(" left join class cla on stu.classNum=cla.id and stu.jie=cla.jie and stu.schoolNum=cla.schoolNum and user.schoolNum=cla.schoolNum ");
            sql.append(" left join grade gra on gra.gradeNum=stu.gradeNum and stu.jie=gra.jie  and stu.schoolNum=gra.schoolNum and user.schoolNum=gra.schoolNum ");
            sql.append(" left join school sch on sch.id=stu.schoolnum");
        } else {
            sql.append("select user.id,user.userid,user.username,user.realName,user.usertype,sch.schoolName schoolName ");
            if ("2".equals(user.getUsertype())) {
                sql.append(",cla.className className,gra.gradeName gradeName ");
            } else if ("1".equals(user.getUsertype())) {
                sql.append(",GROUP_CONCAT(r.roleName) ext1 ");
            }
            sql.append(" from user ");
        }
        if ("2".equals(user.getUsertype())) {
            sql.append(" left join student stu on  stu.id=user.userid ");
            sql.append(" left join class cla on stu.classNum=cla.id and stu.jie=cla.jie and stu.schoolNum=cla.schoolNum and user.schoolNum=cla.schoolNum ");
            sql.append(" left join grade gra on gra.gradeNum=stu.gradeNum and stu.jie=gra.jie  and stu.schoolNum=gra.schoolNum and user.schoolNum=gra.schoolNum ");
            sql.append(" left join school sch on sch.id=stu.schoolnum");
        } else if ("1".equals(user.getUsertype())) {
            sql.append(" left join school sch on sch.id=user.schoolnum ");
            sql.append(" INNER JOIN userrole ur on user.id=ur.usernum ");
            sql.append(" INNER JOIN role r on r.rolenum = ur.rolenum ");
        }
        if ("groupbj".equals(user.getGroupbj())) {
            sql.append(" left join userposition up on up.usernum=user.id");
        }
        if ("3".equals(user.getUsertype())) {
            sql.append(" where userparent.isdelete = 'F' ");
        } else {
            sql.append(" where user.isDelete = 'F' ");
        }
        if ("groupbj".equals(user.getGroupbj())) {
            if (!"-1".equals(user.getSchoolNumbj())) {
                sql.append(" and up.schoolNum={SchoolNumbj}   ");
            }
            if (!"-1".equals(user.getGradeNumbj())) {
                sql.append(" and up.gradeNum={GradeNumbj}   ");
            }
            if (!"-1".equals(user.getSubjectNumbj())) {
                String findSub = "select " + user.getSubjectNumbj() + " subjectNum union all select subjectNum from exampaper where pexamPaperNum = (select distinct pexamPaperNum from exampaper where examNum = {examNum}  and gradeNum = {gradeNum} and subjectNum = {SubjectNumbj} )";
                sql.append(" and up.subjectNum in (" + findSub + ")");
            }
        }
        Map args = new HashMap();
        args.put("SchoolNumbj", user.getSchoolNumbj());
        args.put("GradeNumbj", user.getGradeNumbj());
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("SubjectNumbj", user.getSubjectNumbj());
        if (null != user.getUsername() && !user.getUsername().equals("")) {
            if ("3".equals(user.getUsertype())) {
                sql.append(" and ( userparent.realname like {Username} ");
                sql.append(" or userparent.username like {Username}  )");
            } else {
                sql.append(" and ( user.realname like {Username}  ");
                sql.append(" or user.username like {Username}  )");
            }
        }
        args.put("Username", "%" + user.getUsername() + "%");
        if (null != user.getUsertype() && !user.getUsertype().equals("")) {
            if ("3".equals(user.getUsertype())) {
                sql.append(" and userparent.usertype = {Usertype}  ");
                if (null != user.getSchoolNum() && !user.getSchoolNum().equals("")) {
                    sql.append(" and stu.schoolNum = {SchoolNum}  ");
                }
                if (null != user.getClassNum() && !user.getClassNum().equals("")) {
                    sql.append(" and cla.id = {ClassNum}  ");
                }
                if (null != user.getGradeNum() && !user.getGradeNum().equals("")) {
                    sql.append(" and cla.gradeNum = {GradeNum}  ");
                }
            } else {
                sql.append(" and user.usertype = '" + user.getUsertype() + "' ");
                if ("2".equals(user.getUsertype())) {
                    sql.append(" and stu.jie = {Jie}  ");
                    sql.append(" and stu.gradeNum=" + gradeNum);
                    if (null != user.getSchoolNum() && !user.getSchoolNum().equals("")) {
                        sql.append(" and stu.schoolNum ={SchoolNum}  ");
                    }
                    if (null != user.getClassNum() && !user.getClassNum().equals("")) {
                        sql.append(" and cla.id = {ClassNum} ");
                    }
                    if (null != user.getGradeNum() && !user.getGradeNum().equals("")) {
                        sql.append(" and cla.gradeNum = {GradeNum} ");
                    }
                }
            }
        }
        args.put("Usertype", user.getUsertype());
        args.put("SchoolNum", user.getSchoolNum());
        args.put("ClassNum", user.getClassNum());
        args.put("GradeNum", user.getGradeNum());
        args.put("Jie", user.getJie());
        if ("1".equals(user.getUsertype())) {
            sql.append(" GROUP BY id   ORDER BY sch.id desc,length(user.id),user.id");
        } else if ("2".equals(user.getUsertype())) {
            sql.append(" ORDER BY length(user.id),user.id");
        } else if ("3".equals(user.getUsertype())) {
            sql.append(" ORDER BY length(userparent.id),userparent.id");
        }
        if (start != -1) {
            sql.append(" LIMIT {start} , {pagesize} ");
        }
        args.put("start", Integer.valueOf(start));
        args.put("pagesize", Integer.valueOf(pagesize));
        return this.dao._queryBeanList(sql.toString(), User.class, args);
    }

    public List getAllUser(User user, int start, int pagesize, String examNum, String gradeNum, String positionNum) {
        StringBuffer sql = new StringBuffer();
        if ("3".equals(user.getUsertype())) {
            sql.append("select userparent.id,userparent.userid,userparent.studentRelation,user.username stuname,user.realName sturealname,userparent.username,userparent.realName,userparent.usertype,sch.schoolName schoolName,gra.gradeName gradeName,cla.className className from userparent ");
            sql.append(" left join user on  userparent.userid=user.userid ");
            sql.append(" left join student stu on  stu.id=user.userid ");
            sql.append(" left join class cla on stu.classNum=cla.id and stu.jie=cla.jie and stu.schoolNum=cla.schoolNum and user.schoolNum=cla.schoolNum ");
            sql.append(" left join grade gra on gra.gradeNum=stu.gradeNum and stu.jie=gra.jie  and stu.schoolNum=gra.schoolNum and user.schoolNum=gra.schoolNum ");
            sql.append(" left join school sch on sch.id=stu.schoolnum");
        } else {
            sql.append("select user.id,user.userid,user.username,user.realName,user.usertype,sch.schoolName schoolName ");
            if ("2".equals(user.getUsertype())) {
                sql.append(",cla.className className,gra.gradeName gradeName ");
            } else if ("1".equals(user.getUsertype())) {
                sql.append(" ,GROUP_CONCAT(r.roleName) ext1 ");
            }
            sql.append(" from user ");
        }
        if ("2".equals(user.getUsertype())) {
            sql.append(" left join student stu on  stu.id=user.userid ");
            sql.append(" left join class cla on stu.classNum=cla.id and stu.jie=cla.jie and stu.schoolNum=cla.schoolNum and user.schoolNum=cla.schoolNum ");
            sql.append(" left join grade gra on gra.gradeNum=stu.gradeNum and stu.jie=gra.jie  and stu.schoolNum=gra.schoolNum and user.schoolNum=gra.schoolNum ");
            sql.append(" left join school sch on sch.id=stu.schoolnum ");
        } else if ("1".equals(user.getUsertype())) {
            sql.append(" left join school sch on sch.id=user.schoolnum ");
            sql.append(" INNER JOIN userrole ur on user.id=ur.usernum ");
            sql.append(" INNER JOIN role r on r.rolenum = ur.rolenum ");
            sql.append(" INNER JOIN teacher tea on tea.schoolNum=sch.id ");
            if (!positionNum.equals("-1")) {
                sql.append(" INNER JOIN userposition up on up.userNum=user.id and up.schoolnum=sch.id ");
            }
        }
        if ("groupbj".equals(user.getGroupbj())) {
            sql.append(" left join userposition up on up.usernum=user.id ");
        }
        if ("3".equals(user.getUsertype())) {
            sql.append(" where userparent.isdelete = 'F' ");
        } else {
            sql.append(" where user.isDelete = 'F' ");
        }
        Map args = new HashMap();
        args.put("SchoolNumbj", user.getSchoolNumbj());
        args.put("GradeNumbj", user.getGradeNumbj());
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("SubjectNumbj", user.getSubjectNumbj());
        if ("groupbj".equals(user.getGroupbj())) {
            if (!"-1".equals(user.getSchoolNumbj())) {
                sql.append(" and up.schoolNum={SchoolNumbj}  ");
            }
            if (!"-1".equals(user.getGradeNumbj())) {
                sql.append(" and up.gradeNum={GradeNumbj}  ");
            }
            if (!"-1".equals(user.getSubjectNumbj())) {
                String findSub = "select " + user.getSubjectNumbj() + " subjectNum union all select subjectNum from exampaper where pexamPaperNum = (select distinct pexamPaperNum from exampaper where examNum = {examNum} and gradeNum = {gradeNum} and subjectNum ={SubjectNumbj}   )";
                Map args2 = new HashMap();
                args2.put(Const.EXPORTREPORT_examNum, examNum);
                args2.put(Const.EXPORTREPORT_gradeNum, gradeNum);
                args2.put("SubjectNumbj", user.getSubjectNumbj());
                List<Object> objects = this.dao._queryColList(findSub, args2);
                args.put("object", objects);
                sql.append(" and up.subjectNum in ({object[]}) ");
            }
        }
        if (null != user.getUsername() && !user.getUsername().equals("")) {
            if ("3".equals(user.getUsertype())) {
                sql.append(" and ( userparent.realname like {Username} ");
                sql.append(" or userparent.username like {Username} )");
            } else {
                sql.append(" and ( user.realname like {Username}  ");
                sql.append(" or user.username like {Username} )");
            }
        }
        args.put("Username", "%" + user.getUsername() + "%");
        if (null != user.getUsertype() && !user.getUsertype().equals("")) {
            if ("3".equals(user.getUsertype())) {
                sql.append(" and userparent.usertype = {Usertype}  ");
                if (null != user.getSchoolNum() && !user.getSchoolNum().equals("")) {
                    sql.append(" and stu.schoolNum = {SchoolNum}  ");
                }
                if (null != user.getClassNum() && !user.getClassNum().equals("")) {
                    sql.append(" and cla.id = {ClassNum}  ");
                }
                if (null != user.getGradeNum() && !user.getGradeNum().equals("")) {
                    sql.append(" and cla.gradeNum = {GradeNum}  ");
                }
            } else {
                sql.append(" and user.usertype ={Usertype}  ");
                if ("2".equals(user.getUsertype())) {
                    if (!"000".equals(gradeNum)) {
                        sql.append(" and stu.jie ={Jie}   ");
                        sql.append(" and stu.gradeNum={gradeNum} ");
                    }
                    if (null != user.getSchoolNum() && !user.getSchoolNum().equals("")) {
                        sql.append(" and stu.schoolNum = {SchoolNum}  ");
                    }
                    if (null != user.getClassNum() && !user.getClassNum().equals("")) {
                        sql.append(" and cla.id = {ClassNum}  ");
                    }
                    if (null != user.getGradeNum() && !user.getGradeNum().equals("")) {
                        sql.append(" and cla.gradeNum = {GradeNum} ");
                    }
                }
                if ("1".equals(user.getUsertype()) && !positionNum.equals("-1")) {
                    sql.append(" and up.type={positionNum} ");
                }
            }
        }
        args.put("Usertype", user.getUsertype());
        args.put("SchoolNum", user.getSchoolNum());
        args.put("ClassNum", user.getClassNum());
        args.put("GradeNum", user.getGradeNum());
        args.put("Jie", user.getJie());
        args.put("positionNum", positionNum);
        if ("1".equals(user.getUsertype())) {
            sql.append(" GROUP BY id   ORDER BY sch.id desc,length(user.id),user.id");
        } else if ("2".equals(user.getUsertype())) {
            sql.append(" ORDER BY length(user.id),user.id");
        } else if ("3".equals(user.getUsertype())) {
            sql.append(" ORDER BY length(userparent.id),userparent.id");
        }
        if (start != -1) {
            sql.append(" LIMIT {start} , {pagesize}");
        }
        return this.dao._queryBeanList(sql.toString(), User.class, args);
    }

    public List getLimitUser(User user, int start, int pagesize, String examNum, String gradeNum, String userId) {
        StringBuffer sql = new StringBuffer();
        StringBuffer sql2 = new StringBuffer();
        StringBuffer sql3 = new StringBuffer();
        if ("3".equals(user.getUsertype())) {
            sql.append("select userparent.id,userparent.userid,userparent.studentRelation,user.username stuname,user.realName sturealname,userparent.username,userparent.realName,userparent.usertype,sch.schoolName schoolName,gra.gradeName gradeName,cla.className className from userparent ");
            sql.append(" left join user on  userparent.userid=user.userid ");
            sql.append(" left join student stu on  stu.id=user.userid ");
            sql.append(" left join class cla on stu.classNum=cla.id and stu.jie=cla.jie and stu.schoolNum=cla.schoolNum and user.schoolNum=cla.schoolNum ");
            sql.append(" left join grade gra on gra.gradeNum=stu.gradeNum and stu.jie=gra.jie  and stu.schoolNum=gra.schoolNum and user.schoolNum=gra.schoolNum ");
            sql.append(" left join school sch on sch.id=stu.schoolnum");
        } else {
            sql.append("select user.id,user.userid,user.username,user.realName,user.usertype,sch.schoolName schoolName ");
            if ("2".equals(user.getUsertype())) {
                sql.append(",cla.className className,gra.gradeName gradeName ");
            } else if ("1".equals(user.getUsertype())) {
                sql.append(",sp.type spType,GROUP_CONCAT(DISTINCT d.name) ext3 ");
            }
            sql.append(" from user ");
        }
        if ("2".equals(user.getUsertype())) {
            sql.append(" left join student stu on  stu.id=user.userid ");
            sql.append(" left join class cla on stu.classNum=cla.id and stu.jie=cla.jie and stu.schoolNum=cla.schoolNum and user.schoolNum=cla.schoolNum ");
            sql.append(" left join grade gra on gra.gradeNum=stu.gradeNum and stu.jie=gra.jie  and stu.schoolNum=gra.schoolNum and user.schoolNum=gra.schoolNum ");
            sql.append(" left join school sch on sch.id=stu.schoolnum");
        } else if ("1".equals(user.getUsertype())) {
            if (userId.equals("-2") || userId.equals("-1")) {
                sql.append(" left join school sch on sch.id=user.schoolnum ");
            } else {
                sql.append(" left join school sch on sch.id=user.schoolnum right join (select DISTINCT userId userId,schoolNum from ( select userId userId,schoolNum from schauthormanage where userId={userId} union all select  id userId,schoolNum from user where id={userId} )u ) s on s.schoolNum = user.schoolNum  ");
            }
            sql.append(" left join schoolscanpermission sp on sp.userNum =  user.id  ");
            sql.append(" left join userposition us on us.userNum = user.id ");
            sql.append(" left join data d on d.value = us.type and d.type = '31' ");
        }
        if ("groupbj".equals(user.getGroupbj())) {
            sql.append(" left join userposition up on up.usernum=user.id");
        }
        if ("3".equals(user.getUsertype())) {
            sql.append(" where userparent.isdelete = 'F' ");
        } else {
            sql.append(" where user.isDelete = 'F' ");
        }
        if ("groupbj".equals(user.getGroupbj())) {
            if (!"-1".equals(user.getSchoolNumbj())) {
                sql.append(" and up.schoolNum={SchoolNumbj}   ");
            }
            if (!"-1".equals(user.getGradeNumbj())) {
                sql.append(" and up.gradeNum={GradeNumbj}   ");
            }
            if (!"-1".equals(user.getSubjectNumbj())) {
                sql.append(" and up.subjectNum in (select subjectNum from exampaper where pexamPaperNum = (select distinct pexamPaperNum from exampaper where examNum = {examNum} and gradeNum = {gradeNum}  and subjectNum ={SubjectNumbj} ))");
            }
        }
        Map args = new HashMap();
        args.put("SchoolNumbj", user.getSchoolNumbj());
        args.put("GradeNumbj", user.getGradeNumbj());
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("SubjectNumbj", user.getSubjectNumbj());
        if (null != user.getUsername() && !user.getUsername().equals("")) {
            if ("3".equals(user.getUsertype())) {
                sql.append(" and ( userparent.realname like {Username}  ");
                sql.append(" or userparent.username like {Username}  )");
            } else {
                sql.append(" and ( user.realname like {Username} ");
                sql.append(" or user.username like {Username} )");
            }
        }
        args.put("Username", "%" + user.getUsername() + "%");
        if (null != user.getUsertype() && !user.getUsertype().equals("")) {
            if ("3".equals(user.getUsertype())) {
                sql.append(" and userparent.usertype = {Usertype}  ");
                if (null != user.getSchoolNum() && !user.getSchoolNum().equals("")) {
                    sql.append(" and stu.schoolNum = {SchoolNum}  ");
                }
                if (null != user.getClassNum() && !user.getClassNum().equals("")) {
                    sql.append(" and cla.id = {ClassNum} ");
                }
                if (null != user.getGradeNum() && !user.getGradeNum().equals("")) {
                    sql.append(" and cla.gradeNum = {GradeNum}  ");
                }
            } else {
                if ("2".equals(user.getUsertype()) || userId.equals("-2") || userId.equals("-1")) {
                    sql.append(" and user.usertype = {Usertype} ");
                } else {
                    sql.append(" and user.usertype = {Usertype}  and s.userId={userId} ");
                }
                if ("1".equals(user.getUsertype()) && !"-1".equals(user.getExt3())) {
                    sql.append("  and us.type ={Ext3}   ");
                }
                if ("2".equals(user.getUsertype())) {
                    sql.append(" and stu.jie = {Jie} ");
                    if (null != user.getSchoolNum() && !user.getSchoolNum().equals("")) {
                        sql.append(" and stu.schoolNum = {SchoolNum} ");
                    }
                    if (null != user.getClassNum() && !user.getClassNum().equals("")) {
                        sql.append(" and cla.id = {ClassNum}  ");
                    }
                    if (null != user.getGradeNum() && !user.getGradeNum().equals("")) {
                        sql.append(" and cla.gradeNum = {GradeNum} ");
                    }
                }
            }
        }
        args.put("Usertype", Convert.toInt(user.getUsertype()));
        args.put("SchoolNum", user.getSchoolNum());
        args.put("ClassNum", user.getClassNum());
        args.put("GradeNum", user.getGradeNum());
        args.put("userId", userId);
        args.put("Ext3", user.getExt3());
        args.put("Jie", user.getJie());
        if ("1".equals(user.getUsertype())) {
            sql.append(" GROUP BY id   ORDER BY sch.id desc,length(user.id),user.id");
        } else if ("2".equals(user.getUsertype())) {
            sql.append(" ORDER BY length(user.id),user.id");
        }
        if ("3".equals(user.getUsertype())) {
            sql2.append(" select userparent.id,userparent.userid,userparent.studentRelation,user.username stuname,");
            sql2.append("user.realName sturealname,userparent.username,userparent.realName,userparent.usertype,school.schoolName schoolName,");
            sql2.append("grade.gradeName gradeName,class.className className from userparent ,");
            sql2.append("(select * from student stu where isdelete = 'F' ");
            if (null != user.getSchoolNum() && !user.getSchoolNum().equals("")) {
                sql2.append(" and stu.schoolNum = {SchoolNum} ");
            }
            if (null != user.getClassNum() && !user.getClassNum().equals("")) {
                sql2.append(" and stu.classNum = {ClassNum}  ");
            }
            if (null != user.getGradeNum() && !user.getGradeNum().equals("")) {
                sql2.append(" and stu.gradeNum = {GradeNum} ");
            }
            if (null != user.getUsername() && !user.getUsername().equals("")) {
                sql2.append(" and (studentName like {Username} or studentId = {studentId} )");
                args.put(Const.EXPORTREPORT_studentId, user.getUsername());
            }
            sql2.append(")as stu2");
            sql2.append(" left join user on stu2.id = user.userid");
            if ("groupbj".equals(user.getGroupbj())) {
                sql.append(" left join userposition up on up.usernum=user.id");
            }
            sql2.append(" left join school on stu2.schoolNum = school.id and school.isDelete = 'F'");
            sql2.append(" left join grade on stu2.gradeNum = grade.gradeNum AND stu2.jie = grade.jie AND stu2.schoolNum = grade.schoolNum AND user.schoolNum = grade.schoolNum and grade.isDelete = 'F'");
            sql2.append(" left join class on stu2.classNum = class.id AND stu2.jie = class.jie AND stu2.schoolNum = class.schoolNum AND user.schoolNum = grade.schoolNum and class.isDelete = 'F' ");
            sql2.append(" where userparent.userid  = stu2.id and userparent.isdelete = 'F' ");
            sql3.append(" select * from ( ");
            sql3.append(sql.toString());
            sql3.append(" UNION ALL ");
            sql3.append(sql2.toString());
            sql3.append(" ) as up3 GROUP BY up3.id ORDER BY length(up3.id),up3.id ");
            sql3.append(" LIMIT {start} , {pagesize} ");
        }
        if (start != -1) {
            sql.append(" LIMIT {start}  , {pagesize}");
        }
        args.put("start", Integer.valueOf(start));
        args.put("pagesize", Integer.valueOf(pagesize));
        if ("3".equals(user.getUsertype())) {
            return this.dao._queryBeanList(sql3.toString(), User.class, args);
        }
        return this.dao._queryBeanList(sql.toString(), User.class, args);
    }

    public List getAllStudentUser(User user, int start, int pagesize) {
        StringBuffer sql = new StringBuffer("select u.id,s.studentid num,s.studentname username,usertype,isUser from user u,student s where u.userid = s.studentId and u.usertype='2' AND u.isDelete = 'F' ");
        if (start != -1) {
            sql.append(" LIMIT {start} , {pagesize}");
        }
        Map args = new HashMap();
        args.put("start", Integer.valueOf(start));
        args.put("pagesize", Integer.valueOf(pagesize));
        return this.dao._queryBeanList(sql.toString(), User.class, args);
    }

    public Integer getAllUserCount(User user, String userId) {
        StringBuffer sql = new StringBuffer();
        StringBuffer sql2 = new StringBuffer();
        StringBuffer sql3 = new StringBuffer();
        Map args = new HashMap();
        if ("3".equals(user.getUsertype())) {
            sql.append("select userparent.id from userparent ");
            sql.append(" left join user on  userparent.userid=user.userid ");
            sql.append(" left join student stu on  stu.id=user.userid ");
            sql.append(" left join class cla on stu.classNum=cla.id and stu.jie=cla.jie and stu.schoolNum=cla.schoolNum and user.schoolNum=cla.schoolNum ");
            sql.append(" left join grade gra on gra.gradeNum=stu.gradeNum and stu.jie=gra.jie  and stu.schoolNum=gra.schoolNum and user.schoolNum=gra.schoolNum ");
            sql.append(" where 1=1 ");
            if (null != user.getUsername() && !user.getUsername().equals("")) {
                sql.append(" and ( userparent.realname like {Username}  ");
                sql.append(" or userparent.username like {Username})");
                args.put("Username", "%" + user.getUsername() + "%");
            }
        } else {
            sql.append("select count(distinct user.id) from user ");
            if ("2".equals(user.getUsertype())) {
                sql.append(" left join student stu on  stu.id=user.userid ");
                sql.append(" left join class cla on stu.classNum=cla.id and stu.jie=cla.jie and stu.schoolNum=cla.schoolNum and user.schoolNum=cla.schoolNum ");
                sql.append(" left join grade gra on gra.gradeNum=stu.gradeNum and stu.jie=gra.jie  and stu.schoolNum=gra.schoolNum and user.schoolNum=gra.schoolNum ");
            }
            if ("1".equals(user.getUsertype()) && !userId.equals("-2") && !userId.equals("-1")) {
                sql.append(" inner join schauthormanage s on s.schoolNum = user.schoolNum ");
            }
            if ("1".equals(user.getUsertype())) {
                sql.append(" left join userposition us on us.userNum = user.id ");
                sql.append(" left join data d on d.value = us.type and d.type = '31' ");
            }
            sql.append(" where 1=1 ");
            if (null != user.getUsername() && !user.getUsername().equals("")) {
                sql.append(" and ( user.realname like {Username} ");
                sql.append(" or user.username like {Username}  )");
                args.put("Username", "%" + user.getUsername() + "%");
            }
        }
        if (null != user.getUsertype() && !user.getUsertype().equals("")) {
            if ("3".equals(user.getUsertype())) {
                sql.append(" and userparent.usertype = {Usertype}  ");
                sql.append(" and stu.jie = {Jie}  ");
                sql.append(" and stu.schoolNum ={SchoolNum}   ");
                sql.append(" and cla.id = {ClassNum} ");
                sql.append(" and cla.gradeNum ={GradeNum}  ");
            } else {
                sql.append(" and user.usertype = {Usertype} ");
                if ("2".equals(user.getUsertype())) {
                    sql.append(" and stu.jie = {Jie} ");
                    sql.append(" and stu.schoolNum = {SchoolNum}  ");
                    sql.append(" and cla.id = {ClassNum} ");
                    sql.append(" and cla.gradeNum = {GradeNum}  ");
                }
                if ("1".equals(user.getUsertype()) && !userId.equals("-2") && !userId.equals("-1")) {
                    sql.append("  and s.userId={userId} ");
                }
                if ("1".equals(user.getUsertype()) && !"-1".equals(user.getExt3())) {
                    sql.append("  and us.type={Ext3} ");
                }
            }
            args.put("Usertype", user.getUsertype());
            args.put("Jie", user.getJie());
            args.put("SchoolNum", user.getSchoolNum());
            args.put("ClassNum", user.getClassNum());
            args.put("GradeNum", user.getGradeNum());
            args.put("userId", userId);
            args.put("Ext3", user.getExt3());
        }
        if ("3".equals(user.getUsertype())) {
            sql2.append(" select userparent.id from userparent ,");
            sql2.append("(select * from student stu where isdelete = 'F' ");
            if (null != user.getSchoolNum() && !user.getSchoolNum().equals("")) {
                sql2.append(" and stu.schoolNum = {SchoolNum}  ");
            }
            if (null != user.getClassNum() && !user.getClassNum().equals("")) {
                sql2.append(" and stu.classNum = {ClassNum}  ");
            }
            if (null != user.getGradeNum() && !user.getGradeNum().equals("")) {
                sql2.append(" and stu.gradeNum = {GradeNum}  ");
            }
            if (null != user.getUsername() && !user.getUsername().equals("")) {
                sql2.append(" and (studentName like {Username}  or studentId = {studentId} )");
                args.put(Const.EXPORTREPORT_studentId, user.getUsername());
            }
            sql2.append(")as stu2");
            sql2.append(" where userparent.userid  = stu2.id and userparent.isdelete = 'F' ");
            sql3.append(" select count(DISTINCT(up3.id)) from ( ");
            sql3.append(sql.toString());
            sql3.append(" UNION ALL ");
            sql3.append(sql2.toString());
            sql3.append(" ) as up3 ");
        }
        if ("3".equals(user.getUsertype())) {
            return this.dao._queryInt(sql3.toString(), args);
        }
        return this.dao._queryInt(sql.toString(), args);
    }

    public User getUserById(String id) {
        Map args = new HashMap();
        args.put("id", id);
        return (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname from user where id = {id} ", User.class, args);
    }

    public User getUserByNum(String num) {
        Map args = new HashMap();
        args.put("num", num);
        return (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname from user where id = {num} ", User.class, args);
    }

    public User getUserByUserid(String num) {
        Map args = new HashMap();
        args.put("num", num);
        return (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname from user where userid = {num} ", User.class, args);
    }

    public Integer deleteOneByNum(String colum, String value, Class cla) throws Exception {
        if (colum == null || value == null) {
            return null;
        }
        String sql = "delete from " + cla.getSimpleName() + " where " + colum + " = {value}";
        Map args = new HashMap();
        args.put("value", value);
        return Integer.valueOf(this.dao._execute(sql, args));
    }

    public Integer deleteOneUser(String userId, String num, String tab) {
        String sql;
        if (num == null || num == null) {
            return null;
        }
        Map args = new HashMap();
        args.put("userId", userId);
        args.put("tab", tab);
        args.put("num", num);
        if ("3".equals(tab)) {
            sql = "DELETE FROM `userparent` WHERE id={userId}  and usertype={tab} ";
            this.dao._execute("DELETE FROM userrole WHERE userNum={userId}", args);
        } else {
            sql = "DELETE FROM `user` WHERE userid={num} and usertype={tab} ";
            Object useridobj = this.dao._queryObject("select id FROM `user` WHERE userid={num} and usertype={tab} ", args);
            String useridstr = String.valueOf(useridobj);
            if (tab.equals("1")) {
                Integer schoolNum = this.dao._queryInt("SELECT schoolNum FROM teacher WHERE id={num} ", args);
                this.qdi.deletetask(null, null, useridstr, "1", "");
                this.qdi.deleteGroupfz_user(null, null, "user", useridstr);
                if (schoolNum != null) {
                    args.put("userid", useridstr);
                    args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                    this.dao._execute("DELETE FROM userposition WHERE userNum={userid}  AND schoolNum={schoolNum} ", args);
                }
                this.dao._execute("DELETE FROM userrole WHERE userNum={userid} ", args);
                this.dao._execute("DELETE FROM teacher WHERE id={num} ", args);
            } else if (tab.equals("2")) {
                this.dao._queryInt("SELECT count(s.id) FROM ((SELECT studentId,classNum,id FROM score WHERE 1=1 AND studentId={num} LIMIT 0,1) UNION ALL (SELECT studentId ,classNum ,id FROM objectivescore WHERE 1=1 AND studentId={num} LIMIT 0,1))s   ", args).intValue();
                this.dao._queryColList("SELECT examNum FROM examinationnum WHERE studentId={num} ", args);
                this.dao._execute(" delete from student where id ={num} ", args);
                this.dao._execute(" delete from userparent where userid ={num} ", args);
            }
        }
        return Integer.valueOf(this.dao._execute(sql, args));
    }

    public Object getOneByNum(String colum, String value, Class cla) throws Exception {
        if (colum == null || value == null) {
            return null;
        }
        String sql = "select * from " + cla.getSimpleName() + " where " + colum + " = {value} ";
        Map args = new HashMap();
        args.put("value", value);
        return this.dao._queryBean(sql, cla, args);
    }

    public List getList(String colum, String value, Class cla) throws Exception {
        if (colum == null || value == null) {
            return null;
        }
        String sql = "select * from " + cla.getSimpleName() + " where " + colum + " = {value} ";
        Map args = new HashMap();
        args.put("value", value);
        return this.dao._queryBeanList(sql, cla, args);
    }

    public List<Role> getAllRole() {
        return this.dao.queryBeanList("select roleNum,roleName,type from role where   (type='0' or type='1') order by roleName ", Role.class);
    }

    public List<Role> getAllRole2() {
        return this.dao.queryBeanList("select roleNum,roleName,type from role where roleNum<>'-4' and roleNum<>'3' and roleNum<>'4' and (type='0' or type='1') order by roleName ", Role.class);
    }

    public List<Role> getRolesByUserNum(String usernum) {
        Map args = new HashMap();
        args.put("usernum", usernum);
        return this.dao._queryBeanList("select r.roleNum,r.roleName from userrole ur  left join role r on r.rolenum = ur.rolenum where ur.usernum ={usernum} GROUP BY r.roleName", Role.class, args);
    }

    public List getUsersByRoleNum(String roleNum) {
        String sql = "select u.id,u.userid,u.userName,u.realName,userType from userrole ur left join user u on u.id = ur.usernum where ur.rolenum ={roleNum}  AND u.usertype != '0' ORDER BY length(u.username),u.username";
        if (null != roleNum && roleNum.equals("2")) {
            sql = "select  u.id,u.userid,u.userName,u.realName,userType  from userrole ur,role r,user u where r.roleNum = ur.roleNum and u.id=ur.userNum and ur.roleNum ={roleNum} ORDER BY length(u.username),u.username";
        }
        Map args = new HashMap();
        args.put("roleNum", roleNum);
        return this.dao._queryBeanList(sql, User.class, args);
    }

    public List getUsersByType(String type, Integer examNum, Integer examPaperNum, String category) {
        String subSql = "";
        String zikemuStr = "";
        if (StrUtil.isNotEmpty(category) && !Convert.toStr(examPaperNum).equals(category)) {
            subSql = " left join userrole_sub us on us.userNum=ur.userNum ";
            zikemuStr = " and us.exampaperNum={category} ";
        }
        String sql = "select u.id,u.userid,u.userName,u.realName,userType,sch.schoolName schoolName from userrole ur " + subSql + " left join user u on u.id = ur.usernum   left join (select rolenum from role where type={type} and examPaperNum={examPaperNum}   and examNum={examNum} )rolen on rolen.roleNum=ur.roleNum  left join school sch on sch.id=u.schoolNum   where rolen.roleNum is not null " + zikemuStr + " ORDER BY convert(sch.schoolName using gbk),convert(u.realname using gbk)";
        Map args = new HashMap();
        args.put("type", type);
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("category", category);
        return this.dao._queryBeanList(sql, User.class, args);
    }

    public List getUsersByType2(String type, Integer examNum, Integer examPaperNum, String username, String category) {
        Map args = new HashMap();
        args.put("type", type);
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("category", category);
        String subSql = "";
        String zikemuStr = "";
        if (StrUtil.isNotEmpty(category) && !Convert.toStr(examPaperNum).equals(category)) {
            subSql = " left join userrole_sub us on us.userNum=ur.userNum ";
            zikemuStr = " and us.exampaperNum={category} ";
        }
        String sql = "select  u.id,u.userid,u.userName,u.realName,userType from userrole ur " + subSql + " left join user u on u.id = ur.usernum   left join (select rolenum from role where type={type} and examPaperNum={examPaperNum}   and examNum={examNum} )rolen on rolen.roleNum=ur.roleNum where rolen.roleNum=ur.roleNum " + zikemuStr;
        if (username != null) {
            sql = (sql + " and (u.realName like {username} ") + " or u.username like {username} ) ";
            args.put("username", "%" + username + "%");
        }
        return this.dao._queryBeanList(sql, User.class, args);
    }

    public Integer updatepasword(String num, String pwd) {
        Map args = new HashMap();
        args.put("pwd", pwd.toLowerCase());
        args.put("num", num);
        return Integer.valueOf(this.dao._execute("update user set password = md5({pwd}) where id = {num} ", args));
    }

    public List<String> getRoleNumsByUserNum(String usernum) {
        Map args = StreamMap.create().put("usernum", (Object) usernum);
        return this.dao._queryColList("select DISTINCT roleNum from userrole where userNum = {usernum} ", String.class, args);
    }

    public boolean hasPermission(String userNum, String url) {
        if (null != userNum && userNum.equals("-1")) {
            return true;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT count(1) from  ");
        buffer.append("(select distinct resource\tfrom resourcerole where roleNum in(select roleNum from userrole where userNum = {userNum} )) re ");
        buffer.append("LEFT join resource on re.resource = resource.num ");
        buffer.append("where resource.url = {url}  ");
        Map args = StreamMap.create().put("userNum", (Object) userNum).put("url", (Object) url);
        int count = this.dao._queryInt(buffer.toString(), args).intValue();
        return count != 0;
    }

    public Integer saveUser(User user) {
        Map args = StreamMap.create().put("id", (Object) user.getId()).put("username", (Object) user.getUsername()).put("password", (Object) user.getPassword()).put("usertype", (Object) user.getUsertype()).put("realname", (Object) user.getRealname()).put("schoolnum", (Object) user.getSchoolNum()).put("mobile", (Object) user.getMobile()).put("email", (Object) user.getEmail()).put("insertUser", (Object) user.getInsertUser()).put("insertDate", (Object) user.getInsertDate()).put("updateUser", (Object) user.getUpdateUser()).put("updateDate", (Object) user.getUpdateDate()).put("ext1", (Object) user.getExt1()).put("ext2", (Object) user.getExt2()).put("ext3", (Object) user.getExt3());
        return Integer.valueOf(this.dao._execute("insert into user(id,username,password,usertype,realname,schoolnum,mobile,email,insertUser,insertDate,updateUser,updateDate,ext1,ext2,ext3) values ({id},{username},{password},{usertype},{realname},{schoolnum},{mobile},{email},{insertUser},{insertDate},{updateUser},{updateDate},{ext1},{ext2},{ext3})", args));
    }

    public Object getUserByName(String name) {
        Map args = StreamMap.create().put("name", (Object) name);
        return this.dao._queryObject("select id from user where username={name}  or loginName={name} ", args);
    }

    public Object getIdByNameAndType(String name, String usertype) {
        Map args = StreamMap.create().put("name", (Object) name).put("usertype", (Object) usertype);
        return this.dao._queryObject("select id from user where (username={name} or loginName={name}) and usertype={usertype}  and isDelete='F'", args);
    }

    public Object getUserAutorByName(String name, String uId) {
        Map args = StreamMap.create().put("uId", (Object) uId).put("name", (Object) name);
        return this.dao._queryObject("select u.id from user u inner join schauthormanage s on s.schoolNum = u.schoolNum  where s.userId={uId} and (u.username={name}  or u.loginName={name} )", args);
    }

    public Object authenticationPasword(String num, String password) {
        Map args = StreamMap.create().put("password", (Object) password.toLowerCase()).put("num", (Object) num);
        return this.dao._queryObject("SELECT password=md5({password} ) FROM `user` where id = {num} ", args);
    }

    public OnlineUser authInOnlineUser(User user) {
        Map args = StreamMap.create().put("userName", (Object) user.getUsername()).put("userNum", (Object) user.getId()).put("usertype", (Object) user.getUsertype());
        return (OnlineUser) this.dao._queryBean("SELECT /* shard_host_HG=Write */  id,userName,userNum,sessionid,usertype FROM onlineuser   WHERE userName={userName} and userNum={userNum} and usertype={usertype}", OnlineUser.class, args);
    }

    public OnlineUser authInOnlineUser2(Userparent user) {
        Map args = StreamMap.create().put("userName", (Object) user.getUsername()).put("userNum", (Object) user.getId()).put("usertype", (Object) user.getUsertype());
        return (OnlineUser) this.dao._queryBean("SELECT /* shard_host_HG=Write */  id,userName,userNum,sessionid,usertype FROM onlineuser   WHERE userName={userName} and userNum={userNum} and usertype={usertype}", OnlineUser.class, args);
    }

    public Integer updateSessionIdInOnlineUser(OnlineUser onlineUser, String oldSessionId) {
        Map args = StreamMap.create().put("oldSessionId", (Object) oldSessionId).put("userName", (Object) onlineUser.getUserName()).put("usertype", (Object) onlineUser.getUsertype());
        return Integer.valueOf(this.dao._execute("UPDATE onlineuser SET sessionid={oldSessionId} ,insertTime=now() WHERE userName={userName}  and usertype={usertype} ", args));
    }

    public void deleteFromOnlineUser(User user, String currSessionID) {
        Map args = StreamMap.create().put("userNum", (Object) user.getId()).put("sessionid", (Object) currSessionID);
        this.dao._execute("DELETE FROM onlineuser WHERE userNum={userNum} AND sessionid={sessionid} ", args);
    }

    public void delOnlineuserBySessionid(String sessionid) {
        Map args = StreamMap.create().put("sessionid", (Object) sessionid);
        this.dao._execute("DELETE FROM onlineuser WHERE sessionid={sessionid} ", args);
    }

    public void deleteAllUserFromOnlineUser() {
        this.dao.queryColList("select userNum from onlineuser");
        this.dao.execute("DELETE FROM onlineuser ");
    }

    public String selectRole(String type, Integer examNum, Integer examPaperNum) {
        Map args = StreamMap.create().put("type", (Object) type).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("examPaperNum", (Object) examPaperNum);
        return this.dao._queryStr("SELECT /* shard_host_HG=Write */ roleNum FROM  role WHERE type={type} and examNum={examNum} and examPaperNum={examPaperNum} ", args);
    }

    public String haveUrl(String userNum, String url) {
        Map args = StreamMap.create().put("url", (Object) ("%" + url + "")).put("userNum", (Object) userNum);
        return this.dao._queryStr("select DISTINCT IF(LOCATE('/',rs.url)>0,right(rs.url,locate('/',reverse(rs.url))-1),rs.url)   from (select num,url  from resource where url like {url} ) rs  left join  resourcerole rr on rs.num=rr.resource  left join   (SELECT r.rolenum FROM (select rolenum from userrole where userNum={userNum} ) ur,role r  WHERE ur.roleNum=r.roleNum ) uu  on  rr.roleNum=uu.roleNum ", args);
    }

    public String haveUrl2(String uri) {
        Map args = StreamMap.create().put("uri", (Object) ("%" + uri + ""));
        return this.dao._queryStr("select IF(LOCATE('/',url)>0,right(url,locate('/',reverse(url))-1),url) from resource where url like {uri} ", args);
    }

    public List getuserNum(String subjectNum, String gradeNum, String jie) {
        Map args = StreamMap.create().put("jie", (Object) jie).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao._queryBeanList("select DISTINCT temp.userNum from (SELECT userNum,subjectNum,gradeNum  FROM userposition where jie={jie})temp   left join subject   sub on sub.subjectNum=temp.subjectNum WHERE (sub.subjectNum={subjectNum} or sub.pid={subjectNum}) and temp.gradeNum={gradeNum}  order by temp.userNum  ", Userposition.class, args);
    }

    public List<User> getUsersByuerNum(List userNumList) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < userNumList.size(); i++) {
            String userNum = ((Userposition) userNumList.get(i)).getUserNum();
            Map args = StreamMap.create().put("userNum", (Object) userNum);
            List<?> _queryBeanList = this.dao._queryBeanList("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname from user  where id={userNum} ", User.class, args);
            if ((_queryBeanList != null) & (_queryBeanList.size() > 0)) {
                arrayList.add(_queryBeanList.get(0));
            }
        }
        return arrayList;
    }

    public String getexamPaperNum(String examNum, String gradeNum, String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao._queryStr("select examPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}  ", args);
    }

    public int checkIfSubjectDefineExist(String exampaperNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        Integer inta = this.dao._queryInt("select count(1) from define where exampaperNum={exampaperNum}  ", args);
        return inta.intValue();
    }

    public int checkIfGradeDefineExist(String examNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        Integer inta = this.dao._queryInt("select count(1) from define d  left join exampaper ep on ep.examPaperNum=d.examPaperNum  left join exam e on e.examNum=ep.examNum  where e.examNum={examNum} and ep.gradeNum={gradeNum} ", args);
        return inta.intValue();
    }

    public int checkIfExamDefineExist(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        Integer inta = this.dao._queryInt("select count(1) from define d  left join exampaper ep on ep.examPaperNum=d.examPaperNum  left join exam e on e.examNum=ep.examNum  where e.examNum={examNum} ", args);
        return inta.intValue();
    }

    public String selectUserrole(String userNum, String roleNum) {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put("roleNum", (Object) roleNum);
        return this.dao._queryStr("select id from userrole where userNum={userNum} and roleNum={roleNum} ", args);
    }

    public String selectExamName(String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryStr("select examName from exam where  examNum={examNum} ", args);
    }

    public String selectGradeName(String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao._queryStr("select gradeName from grade where gradeNum={gradeNum} ", args);
    }

    public String selectSubjectName(String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao._queryStr("select subjectName from subject where subjectNum={subjectNum} ", args);
    }

    public void deleteRole(String examNum, String type, String examPaperNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("examPaperNum", (Object) examPaperNum);
        this.dao._execute("delete from role where examNum={examNum} and (type='3' or type='4') and examPaperNum={examPaperNum} ", args);
    }

    public void deleteUserrole(String userNum, String examPaperNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        args.put("examPaperNum", examPaperNum);
        Integer count3 = this.dao._queryInt("select count(1) from  userrole ur left join role r on r.roleNum=ur.roleNum where ur.userNum={userNum}  and r.type='3'", args);
        if (count3.intValue() >= 2) {
            this.dao._execute("delete from userrole  where roleNum = (select roleNum  from role where examPaperNum = {examPaperNum} and type='3') and userNum={userNum}   ", args);
        } else if (count3.intValue() < 2) {
            this.dao._execute("delete from userrole  where roleNum = (select roleNum  from role where examPaperNum = {examPaperNum} and type='3') and userNum={userNum} ", args);
            this.dao._execute("delete from userrole where  userNum ={userNum}  and roleNum = '3' ", args);
        }
        Integer count4 = this.dao._queryInt("select count(1) from  userrole ur left join role r on r.roleNum=ur.roleNum where ur.userNum={userNum} and r.type='4'", args);
        if (count4.intValue() >= 2) {
            this.dao._execute("delete from userrole  where roleNum = (select roleNum  from role where examPaperNum = {examPaperNum} and type='4') and userNum={userNum}  ", args);
        } else if (count4.intValue() < 2) {
            this.dao._execute("delete from userrole  where roleNum = (select roleNum  from role where examPaperNum = {examPaperNum} and type='4') and userNum={userNum}  ", args);
            this.dao._execute("delete from userrole where  userNum = {userNum} and roleNum = '4' ", args);
        }
    }

    public Integer getRolesYjy(String usernum) {
        Map args = StreamMap.create().put("usernum", (Object) usernum);
        return Convert.toInt(this.dao._queryLong("select count(1) from userrole where usernum={usernum} and rolenum='4' LIMIT 0,1", args), 0);
    }

    public String queryUserSex(User u) {
        String sex;
        Map args = StreamMap.create().put("Username", (Object) u.getUsername());
        if ("0".equals(u.getUsertype())) {
            sex = "男";
        } else if ("1".equals(u.getUsertype())) {
            sex = this.dao._queryStr("select sex from teacher where teacherNum={Username} ", args);
            if (null == sex || "null".equals(sex)) {
                sex = "男";
            }
        } else if ("2".equals(u.getUsertype())) {
            sex = this.dao._queryStr("select sex from student where studentId={Username}", args);
            if (null == sex || "null".equals(sex)) {
                sex = "男";
            }
        } else {
            sex = "男";
        }
        return sex;
    }

    public List querygrade(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryBeanList("select DISTINCT * from grade where schoolNum={schoolNum} and isDelete = 'F'", Grade.class, args);
    }

    public List queryclass(String gradeNum, String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.dao._queryBeanList("select  id,className from class where gradeNum={gradeNum} and schoolNum={schoolNum} and isDelete='F' group by classnum ORDER BY length(classNum), classNum", Class.class, args);
    }

    public List getRelation() {
        return this.dao.queryBeanList("select name,value from data where type = '30' ", Data.class);
    }

    public List getAllSchoolName() {
        return this.dao.queryBeanList("select schoolName,id from school", School.class);
    }

    public Userparent getUserByUsernameAndPwd(String name) {
        Map args = StreamMap.create().put("name", (Object) name);
        Userparent userparent = (Userparent) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname,isdelete,email,mobile from userparent where (username = {name}  or loginname = {name})", Userparent.class, args);
        return userparent;
    }

    public Integer getuseridBystudentNum(String studentnum) {
        Map args = StreamMap.create().put("studentnum", (Object) studentnum);
        Object result = this.dao._queryObject("select id from student where studentId={studentnum}", args);
        if (null == result) {
            return null;
        }
        return Integer.valueOf(result.toString());
    }

    public String getCount(String userid, String studentRelation) {
        Map args = StreamMap.create().put("userid", (Object) userid).put("studentRelation", (Object) studentRelation);
        return this.dao._queryStr("select username from userparent where userid={userid} and  studentRelation={studentRelation} ", args);
    }

    public Student getstudengByidByName(String userid, String name) {
        Map args = StreamMap.create().put("userid", (Object) userid).put("name", (Object) name);
        return (Student) this.dao._queryBean("select * from student where id={userid} and studentName={name} ", Student.class, args);
    }

    public Integer saveUserparent(Userparent userparent) {
        Map args = StreamMap.create().put("id", (Object) userparent.getId()).put(Const.EXPORTREPORT_schoolNum, (Object) userparent.getSchoolnum()).put("userid", (Object) userparent.getUserid()).put("studentRelation", (Object) userparent.getStudentRelation()).put("username", (Object) userparent.getUsername()).put("password", (Object) userparent.getPassword().toLowerCase()).put("usertype", (Object) userparent.getUsertype()).put("realname", (Object) userparent.getRealname()).put("mobile", (Object) userparent.getMobile()).put("email", (Object) userparent.getEmail()).put("insertUser", (Object) userparent.getInstertUser()).put("insertDate", (Object) userparent.getInsertDate()).put("isUser", (Object) userparent.getIsUser()).put("isdelete", (Object) userparent.getIsDelete()).put("autoreg", (Object) Integer.valueOf(userparent.getAutoreg()));
        int n = this.dao._execute("insert into userparent (id,schoolNum,userid,studentRelation,username,password,usertype,realname,mobile,email,insertUser,insertDate,isUser,isdelete,autoreg) values ({id},{schoolNum},{userid},{studentRelation},{username},md5({password}),{usertype},{realname},{mobile},{email},{insertUser},{insertDate},{isUser},{isdelete},{autoreg})", args);
        if (n > 0) {
            this.dao._execute("update userparent set realname={realname},password = md5({password}) where mobile ={mobile} ", args);
        }
        return Integer.valueOf(n);
    }

    public Integer updateUserparent(Userparent userparent) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, userparent.getSchoolnum());
        args.put("userid", userparent.getUserid());
        args.put("studentRelation", userparent.getStudentRelation());
        args.put("username", userparent.getUsername());
        args.put("realname", userparent.getRealname());
        args.put("mobile", userparent.getMobile());
        args.put("email", userparent.getEmail());
        args.put("insertUser", userparent.getInstertUser());
        args.put("insertDate", userparent.getInsertDate());
        args.put("password", userparent.getPassword().toLowerCase());
        args.put("id", userparent.getId());
        String sql = "update userparent set schoolNum={schoolNum} ,userid={userid} ,studentRelation={studentRelation} ,username={username} ,realname={realname},mobile={mobile} ,email={email},insertUser={insertUser} ,insertDate={insertDate} ";
        if (null != userparent.getPassword() && !"".equals(userparent.getPassword())) {
            sql = sql + ",password= md5({password} )";
        }
        int n = this.dao._execute(sql + " where id={id} ", args);
        if (n > 0) {
            this.dao._execute("update userparent set password = md5({password} ),realname={realname} ,email={email}  where mobile = {mobile}", args);
        }
        return Integer.valueOf(n);
    }

    public String getStudentidByid(int id) {
        Map args = StreamMap.create().put("id", (Object) Integer.valueOf(id));
        return this.dao._queryStr("select studentId from student where id={id} ", args);
    }

    public List getStudentNum(String userid) {
        Map args = StreamMap.create().put("userid", (Object) userid);
        return this.dao._queryBeanList("select studentId from student where id={userid} ", Student.class, args);
    }

    public Map<String, Object> getStudentById(String userid) {
        Map args = StreamMap.create().put("userid", (Object) userid);
        return this.dao._querySimpleMap("select st.id,studentId,studentName,schoolName from student st left join school s  on st.schoolNum=s.id where st.id={userid} ", args);
    }

    public Map<String, Object> getStudentByNum(String userid) {
        Map args = StreamMap.create().put("userid", (Object) userid);
        return this.dao._querySimpleMap("select st.id,studentId,studentName,schoolName from student st left join school s  on st.schoolNum=s.id where st.studentId={userid} ", args);
    }

    public User getUserByName1(String name) {
        Map args = StreamMap.create().put("name", (Object) name);
        return (User) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname from userparent where username = {name}  or loginname = {name}", User.class, args);
    }

    public Object authenticationPaswordByName(String name, String password) {
        Map args = StreamMap.create().put("password", (Object) password.toLowerCase()).put("name", (Object) name);
        return this.dao._queryObject("SELECT password=md5({password}) FROM `userparent` where username = {name} limit 1", args);
    }

    public Integer updatepaswordByName(String name, String pwd) {
        Map args = StreamMap.create().put("pwd", (Object) pwd.toLowerCase()).put("name", (Object) name);
        return Integer.valueOf(this.dao._execute("update userparent set password = md5({pwd}) where username = {name}  or loginname = {name} ", args));
    }

    public String getpublicip(String type) {
        Map args = StreamMap.create().put("type", (Object) type);
        return this.dao._queryStr("select para from config where type={type} ", args);
    }

    public Integer saveuserrole(Userparent userparent) {
        Map args = StreamMap.create().put("userNum", (Object) userparent.getId()).put("insertDate", (Object) userparent.getInsertDate());
        return Integer.valueOf(this.dao._execute("insert into userrole (userNum,roleNum,insertUser,insertDate,isDelete) values ({userNum},'-2','1',{insertDate},'F')", args));
    }

    public Integer updateTuijianren(String userId, String tuijianren) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("tuijianren", tuijianren);
        int i = this.dao._execute("update user set tuijianren={tuijianren} where userid={userId}", map);
        return Integer.valueOf(i);
    }

    public List getschool(String schoolnum) {
        Map args = StreamMap.create().put("schoolnum", (Object) schoolnum);
        return this.dao._queryBeanList("select schoolName,schoolAddress,schoolType from school where id = {schoolnum} ", School.class, args);
    }

    public Object getUserParentByName(String name) {
        Map args = StreamMap.create().put("name", (Object) name);
        return this.dao._queryObject("select id from userparent where username = {name} or loginname={name} ", args);
    }

    public Object getUserAutorPByName(String name, String uId) {
        Map args = StreamMap.create().put("uId", (Object) uId).put("name", (Object) name);
        return this.dao._queryObject("select u.id from userparent u inner join schauthormanage s on s.schoolNum = u.schoolNum where s.userId={uId} and (u.username={name}  or u.loginName={name})", args);
    }

    public Userparent getUserparentNum(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        return (Userparent) this.dao._queryBean("select id,userid,username,password,usertype,realname,schoolNum,isUser,loginname from userparent where id ={id} ", Userparent.class, args);
    }

    public Integer updateUserparentpw(String username, String password) {
        Map args = StreamMap.create().put("password", (Object) password.toLowerCase()).put("username", (Object) username);
        return Integer.valueOf(this.dao._execute("update userparent set password = md5({password}) where username = {username}", args));
    }

    public String getRank(String type) {
        Map args = StreamMap.create().put("type", (Object) type);
        return (String) this.dao._queryObject("select para from config where type={type}", args);
    }

    public List getTeacherinfo(String userNum, ServletContext context) {
        String sql;
        String level = Configuration.getInstance().getLevelclass();
        String sql2 = "SELECT CONCAT(IF (rr.type = 4,CONCAT('教研主任：',rr.gradeName,rr.description),''),IF (rr.type = 3,CONCAT('年级主任：', rr.gradeName),''),IF (rr.type = 2,CONCAT('班主任：',rr.gradeName,rr.className),''),\tIF (rr.type = 1,CONCAT(rr.description, ':',rr.gradeName,rr.className),'')) description ,rr.shortname,rr.gradeName,rr.type,rr.teacherName,rr.className FROM (select DISTINCT su.subjectName description,sc.shortname ,up.type,g.gradeName,te.teacherName,group_concat( DISTINCT cla.className) className from userposition  up  LEFT JOIN school sc ON sc.id = up.schoolnum  LEFT JOIN grade g ON g.gradeNum = up.gradeNum  LEFT JOIN user  u ON u.id = up.userNum  LEFT JOIN teacher te ON te.id = u.userid ";
        if (level.equals("T")) {
            sql = sql2 + " LEFT JOIN levelclass cla ON cla.id = up.classNum ";
        } else {
            sql = sql2 + " LEFT JOIN class cla ON cla.id = up.classNum ";
        }
        String sql3 = sql + "  AND cla.isDelete = 'F' LEFT JOIN `subject` su ON su.subjectNum = up.subjectNum  where  u.id = {userNum} )rr";
        Map args = StreamMap.create().put("userNum", (Object) userNum);
        return this.dao._queryBeanList(sql3, Userposition.class, args);
    }

    public List getAssignedUsers(String roleNum, String gradeNum, String userType, String userName) {
        String sql;
        Map args = new HashMap();
        String gradeStr = "";
        if (gradeNum != null && !gradeNum.equals("") && !gradeNum.equals("000")) {
            gradeStr = " and s.gradeNum={gradeNum} ";
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        }
        if (userType != null && !userType.equals("3")) {
            sql = "select u.id,u.userid,u.userName,u.realName,userType from userrole ur left join user u on u.id = ur.usernum  left join student s on u.userId = s.id where ur.rolenum ={roleNum}  AND u.usertype ={userType}   AND (u.userName like {userName}  OR u.realName like {userName} ) " + gradeStr + " ORDER BY length(u.username),u.username";
        } else {
            sql = "select u.id,u.userid,u.userName,u.realName,userType from userrole ur left join userparent u on u.id = ur.usernum  left join student s on u.userid = s.id where ur.rolenum ={roleNum}  AND u.usertype = {userType} AND (u.userName like {userName}  OR u.realName like {userName} ) " + gradeStr + " ORDER BY length(u.username),u.username";
        }
        args.put("roleNum", roleNum);
        args.put("userType", userType);
        args.put("userName", "%" + userName + "%");
        return this.dao._queryBeanList(sql, User.class, args);
    }
}
