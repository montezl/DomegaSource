package com.dmj.domain;

import com.dmj.cs.bean.RequestVisitor;
import com.dmj.util.session.OnlineUserContext;
import java.io.Serializable;
import javax.servlet.http.HttpSession;

/* loaded from: OnlineUser.class */
public class OnlineUser implements Serializable {
    private String id;
    private String userName;
    private String userNum;
    private String insertTime;
    private String description;
    private String ext1;
    private String ext2;
    private String ext3;
    private String sessionid;
    private String ext4;
    private String ext5;
    private String ext6;
    private String ext7;
    private String roleName;
    private String schoolName;
    private String className;
    private String usertype;
    private String studentName;
    private String realName;
    private RequestVisitor lastRequestVisitor;
    private String gradeName;

    public RequestVisitor getLastRequestVisitor() {
        return this.lastRequestVisitor;
    }

    public void setLastRequestVisitor(RequestVisitor lastRequestVisitor) {
        this.lastRequestVisitor = lastRequestVisitor;
    }

    public void loadLastRequestVisitor() {
        RequestVisitor visitor;
        HttpSession session = OnlineUserContext.getSession(this.sessionid);
        if (session != null && (visitor = (RequestVisitor) session.getAttribute("RequestVisitorKey")) != null) {
            this.lastRequestVisitor = visitor;
        }
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

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNum() {
        return this.userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getInsertTime() {
        return this.insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getRealName() {
        return this.realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getExt1() {
        return this.ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return this.ext2;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return this.ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getUsertype() {
        return this.usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getSessionid() {
        return this.sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
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

    public String getExt6() {
        return this.ext6;
    }

    public void setExt6(String ext6) {
        this.ext6 = ext6;
    }

    public String getExt7() {
        return this.ext7;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setExt7(String ext7) {
        this.ext7 = ext7;
    }
}
