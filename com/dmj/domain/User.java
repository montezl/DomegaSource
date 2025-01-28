package com.dmj.domain;

import java.io.Serializable;

/* loaded from: User.class */
public class User implements Serializable {
    private String id;
    private String userid;
    private String usernum;
    private String username;
    private String password;
    private String usertype;
    private String realname;
    private String stuname;
    private String sturealname;
    private String studentRelation;
    private String schoolNum;
    private String isUser;
    private String isDelete;
    private String description;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String mobile;
    private String email;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String ext5;
    private String loginname;
    private String ip;
    private String roleName;
    private String schoolName;
    private String roleNum;
    private String classNum;
    private String className;
    private String gradeNum;
    private String gradeName;
    private String subjectName;
    private String jie;
    private String groupbj;
    private String gradeNumbj;
    private String subjectNumbj;
    private String schoolNumbj;
    private String spType;
    private String loginType;
    private String systemName;
    private String sy_count;
    private String jf_count;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsertype() {
        return this.usertype;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getSchoolNum() {
        if (null == this.schoolNum) {
            return "";
        }
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getIsUser() {
        return this.isUser;
    }

    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getDescription() {
        if (null == this.description) {
            return "";
        }
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getExt1() {
        if (null == this.ext1) {
            return "";
        }
        return this.ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        if (null == this.ext2) {
            return "";
        }
        return this.ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        if (null == this.ext3) {
            return "";
        }
        return this.ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getMobile() {
        if (null == this.mobile) {
            return "";
        }
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        if (null == this.email) {
            return "";
        }
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getRoleNum() {
        return this.roleNum;
    }

    public void setRoleNum(String roleNum) {
        this.roleNum = roleNum;
    }

    public String getJie() {
        return this.jie;
    }

    public void setJie(String jie) {
        this.jie = jie;
    }

    public String getClassNum() {
        return this.classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getLoginname() {
        return this.loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.className == null ? 0 : this.className.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.classNum == null ? 0 : this.classNum.hashCode()))) + (this.description == null ? 0 : this.description.hashCode()))) + (this.email == null ? 0 : this.email.hashCode()))) + (this.ext1 == null ? 0 : this.ext1.hashCode()))) + (this.ext2 == null ? 0 : this.ext2.hashCode()))) + (this.ext3 == null ? 0 : this.ext3.hashCode()))) + (this.gradeName == null ? 0 : this.gradeName.hashCode()))) + (this.id == null ? 0 : this.id.hashCode()))) + (this.insertDate == null ? 0 : this.insertDate.hashCode()))) + (this.insertUser == null ? 0 : this.insertUser.hashCode()))) + (this.ip == null ? 0 : this.ip.hashCode()))) + (this.isDelete == null ? 0 : this.isDelete.hashCode()))) + (this.isUser == null ? 0 : this.isUser.hashCode()))) + (this.jie == null ? 0 : this.jie.hashCode()))) + (this.loginname == null ? 0 : this.loginname.hashCode()))) + (this.mobile == null ? 0 : this.mobile.hashCode()))) + (this.password == null ? 0 : this.password.hashCode()))) + (this.realname == null ? 0 : this.realname.hashCode()))) + (this.roleName == null ? 0 : this.roleName.hashCode()))) + (this.roleNum == null ? 0 : this.roleNum.hashCode()))) + (this.schoolName == null ? 0 : this.schoolName.hashCode()))) + (this.schoolNum == null ? 0 : this.schoolNum.hashCode()))) + (this.updateDate == null ? 0 : this.updateDate.hashCode()))) + (this.updateUser == null ? 0 : this.updateUser.hashCode()))) + (this.username == null ? 0 : this.username.hashCode()))) + (this.usertype == null ? 0 : this.usertype.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        if (this.className == null) {
            if (other.className != null) {
                return false;
            }
        } else if (!this.className.equals(other.className)) {
            return false;
        }
        if (this.classNum == null) {
            if (other.classNum != null) {
                return false;
            }
        } else if (!this.classNum.equals(other.classNum)) {
            return false;
        }
        if (this.description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!this.description.equals(other.description)) {
            return false;
        }
        if (this.email == null) {
            if (other.email != null) {
                return false;
            }
        } else if (!this.email.equals(other.email)) {
            return false;
        }
        if (this.ext1 == null) {
            if (other.ext1 != null) {
                return false;
            }
        } else if (!this.ext1.equals(other.ext1)) {
            return false;
        }
        if (this.ext2 == null) {
            if (other.ext2 != null) {
                return false;
            }
        } else if (!this.ext2.equals(other.ext2)) {
            return false;
        }
        if (this.ext3 == null) {
            if (other.ext3 != null) {
                return false;
            }
        } else if (!this.ext3.equals(other.ext3)) {
            return false;
        }
        if (this.gradeName == null) {
            if (other.gradeName != null) {
                return false;
            }
        } else if (!this.gradeName.equals(other.gradeName)) {
            return false;
        }
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.insertDate == null) {
            if (other.insertDate != null) {
                return false;
            }
        } else if (!this.insertDate.equals(other.insertDate)) {
            return false;
        }
        if (this.insertUser == null) {
            if (other.insertUser != null) {
                return false;
            }
        } else if (!this.insertUser.equals(other.insertUser)) {
            return false;
        }
        if (this.ip == null) {
            if (other.ip != null) {
                return false;
            }
        } else if (!this.ip.equals(other.ip)) {
            return false;
        }
        if (this.isDelete == null) {
            if (other.isDelete != null) {
                return false;
            }
        } else if (!this.isDelete.equals(other.isDelete)) {
            return false;
        }
        if (this.isUser == null) {
            if (other.isUser != null) {
                return false;
            }
        } else if (!this.isUser.equals(other.isUser)) {
            return false;
        }
        if (this.jie == null) {
            if (other.jie != null) {
                return false;
            }
        } else if (!this.jie.equals(other.jie)) {
            return false;
        }
        if (this.loginname == null) {
            if (other.loginname != null) {
                return false;
            }
        } else if (!this.loginname.equals(other.loginname)) {
            return false;
        }
        if (this.mobile == null) {
            if (other.mobile != null) {
                return false;
            }
        } else if (!this.mobile.equals(other.mobile)) {
            return false;
        }
        if (this.password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!this.password.equals(other.password)) {
            return false;
        }
        if (this.realname == null) {
            if (other.realname != null) {
                return false;
            }
        } else if (!this.realname.equals(other.realname)) {
            return false;
        }
        if (this.roleName == null) {
            if (other.roleName != null) {
                return false;
            }
        } else if (!this.roleName.equals(other.roleName)) {
            return false;
        }
        if (this.roleNum == null) {
            if (other.roleNum != null) {
                return false;
            }
        } else if (!this.roleNum.equals(other.roleNum)) {
            return false;
        }
        if (this.schoolName == null) {
            if (other.schoolName != null) {
                return false;
            }
        } else if (!this.schoolName.equals(other.schoolName)) {
            return false;
        }
        if (this.schoolNum == null) {
            if (other.schoolNum != null) {
                return false;
            }
        } else if (!this.schoolNum.equals(other.schoolNum)) {
            return false;
        }
        if (this.updateDate == null) {
            if (other.updateDate != null) {
                return false;
            }
        } else if (!this.updateDate.equals(other.updateDate)) {
            return false;
        }
        if (this.updateUser == null) {
            if (other.updateUser != null) {
                return false;
            }
        } else if (!this.updateUser.equals(other.updateUser)) {
            return false;
        }
        if (this.username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!this.username.equals(other.username)) {
            return false;
        }
        if (this.usertype == null) {
            if (other.usertype != null) {
                return false;
            }
            return true;
        }
        if (!this.usertype.equals(other.usertype)) {
            return false;
        }
        return true;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getGroupbj() {
        return this.groupbj;
    }

    public void setGroupbj(String groupbj) {
        this.groupbj = groupbj;
    }

    public String getGradeNumbj() {
        return this.gradeNumbj;
    }

    public void setGradeNumbj(String gradeNumbj) {
        this.gradeNumbj = gradeNumbj;
    }

    public String getSubjectNumbj() {
        return this.subjectNumbj;
    }

    public void setSubjectNumbj(String subjectNumbj) {
        this.subjectNumbj = subjectNumbj;
    }

    public String getSchoolNumbj() {
        return this.schoolNumbj;
    }

    public void setSchoolNumbj(String schoolNumbj) {
        this.schoolNumbj = schoolNumbj;
    }

    public String getUsernum() {
        return this.usernum;
    }

    public void setUsernum(String usernum) {
        this.usernum = usernum;
    }

    public String getStuname() {
        return this.stuname;
    }

    public void setStuname(String stuname) {
        this.stuname = stuname;
    }

    public String getSturealname() {
        return this.sturealname;
    }

    public void setSturealname(String sturealname) {
        this.sturealname = sturealname;
    }

    public String getStudentRelation() {
        return this.studentRelation;
    }

    public void setStudentRelation(String studentRelation) {
        this.studentRelation = studentRelation;
    }

    public String getSpType() {
        return this.spType;
    }

    public void setSpType(String spType) {
        this.spType = spType;
    }

    public String getLoginType() {
        return this.loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getSystemName() {
        return this.systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSy_count() {
        return this.sy_count;
    }

    public void setSy_count(String sy_count) {
        this.sy_count = sy_count;
    }

    public String getJf_count() {
        return this.jf_count;
    }

    public void setJf_count(String jf_count) {
        this.jf_count = jf_count;
    }

    public String getExt4() {
        return this.ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getExt5() {
        return this.ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
