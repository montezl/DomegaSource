package com.dmj.cs.bean;

import com.dmj.util.GUID;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;

/* loaded from: ZTreeNode.class */
public class ZTreeNode implements Serializable {
    private String id;
    private String gradeNum;
    private String gradeName;
    private String subjectNum;
    private String subjectName;
    private String uploadScannerNum;
    private String uploadScannerName;
    private String scannerNum;
    private String scannerName;
    private String abType;
    private boolean mother;
    private String uploadPicUrl;
    private String templatePicUrl;
    private String xmlUrl;
    private String uploadUser;
    private String uploadDate;
    private String createUser;
    private String createDate;
    private String templateStatus;
    private boolean ab;
    private String templateType;
    private int totalPage;
    private String realname;
    private String name;
    private boolean open;
    private boolean finished;
    private List<ZTreeNode> children;
    private int waittingUpdateTemplate;
    private String testScanTemplateRecordNum;
    private boolean onlyOneSchool;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateType() {
        return this.templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getScannerNum() {
        return this.scannerNum;
    }

    public void setScannerNum(String scannerNum) {
        this.scannerNum = scannerNum;
    }

    public String getScannerName() {
        return this.scannerName;
    }

    public String getUploadScannerNum() {
        return this.uploadScannerNum;
    }

    public void setUploadScannerNum(String uploadScannerNum) {
        this.uploadScannerNum = uploadScannerNum;
    }

    public String getUploadScannerName() {
        return this.uploadScannerName;
    }

    public void setUploadScannerName(String uploadScannerName) {
        this.uploadScannerName = uploadScannerName;
    }

    public void setScannerName(String scannerName) {
        this.scannerName = scannerName;
    }

    public String getAbType() {
        return this.abType;
    }

    public void setAbType(String abType) {
        this.abType = abType;
    }

    public String getTemplateStatus() {
        return this.templateStatus;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getUploadPicUrl() {
        return this.uploadPicUrl;
    }

    public void setUploadPicUrl(String uploadPicUrl) {
        this.uploadPicUrl = uploadPicUrl;
    }

    public String getTemplatePicUrl() {
        return this.templatePicUrl;
    }

    public void setTemplatePicUrl(String templatePicUrl) {
        this.templatePicUrl = templatePicUrl;
    }

    public void setTemplateStatus(String templateStatus) {
        this.templateStatus = templateStatus;
        if (templateStatus != null && templateStatus.equals(String.valueOf(3))) {
            this.finished = true;
        }
    }

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isMother() {
        return this.mother;
    }

    public void setMother(boolean mother) {
        this.mother = mother;
    }

    public boolean isAb() {
        return this.ab;
    }

    public void setAb(boolean ab) {
        this.ab = ab;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public List<ZTreeNode> getChildren() {
        return this.children;
    }

    public void setChildren(List<ZTreeNode> children) {
        this.children = children;
    }

    public String getXmlUrl() {
        return this.xmlUrl;
    }

    public void setXmlUrl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
    }

    public String getUploadUser() {
        return this.uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public String getUploadDate() {
        return this.uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getWaittingUpdateTemplate() {
        return this.waittingUpdateTemplate;
    }

    public void setWaittingUpdateTemplate(int waittingUpdateTemplate) {
        this.waittingUpdateTemplate = waittingUpdateTemplate;
    }

    public String getTestScanTemplateRecordNum() {
        return this.testScanTemplateRecordNum;
    }

    public void setTestScanTemplateRecordNum(String testScanTemplateRecordNum) {
        this.testScanTemplateRecordNum = testScanTemplateRecordNum;
    }

    public boolean isOnlyOneSchool() {
        return this.onlyOneSchool;
    }

    public void setOnlyOneSchool(boolean onlyOneSchool) {
        this.onlyOneSchool = onlyOneSchool;
    }

    public void clearGradeNodeUnnecessaryInfo() {
        this.id = null;
        this.subjectNum = null;
        this.subjectName = null;
        this.scannerNum = null;
        this.scannerName = null;
        this.uploadScannerNum = null;
        this.uploadScannerName = null;
        this.abType = null;
        this.templateStatus = null;
        this.open = true;
        this.finished = false;
        this.children = null;
        this.mother = false;
        this.templateType = "0";
        this.totalPage = 0;
        this.uploadPicUrl = null;
        this.templatePicUrl = null;
        this.uploadUser = null;
        this.uploadDate = null;
        this.createUser = null;
        this.createDate = null;
        this.realname = null;
        this.name = this.gradeName;
    }

    public void clearSubjectNodeUnnecessaryInfo() {
        this.open = true;
        this.finished = false;
        this.children = null;
        this.name = this.subjectName;
    }

    public void clearScannerNodeUnnecessaryInfo() {
        if (this.templateType == null || this.templateType.equals("")) {
            this.templateType = "0";
        }
        if (this.mother) {
            this.name = "母版";
        } else {
            this.name = this.uploadScannerName;
        }
        this.name += (this.abType.equals("N") ? "" : "-" + this.abType + "卷");
    }

    public static ZTreeNode creatMotherSubjectNode(ZTreeNode orig, String abType) {
        ZTreeNode dest = orig.m10clone();
        dest.setId(null);
        dest.setFinished(false);
        dest.setUploadUser(null);
        dest.setUploadScannerNum(null);
        dest.setUploadScannerName("母版");
        dest.setUploadPicUrl(null);
        dest.setCreateUser(null);
        dest.setTemplatePicUrl(null);
        dest.setXmlUrl(null);
        dest.setMother(true);
        dest.setAbType(abType);
        dest.setRealname(null);
        dest.setTestScanTemplateRecordNum(GUID.getGUIDStr());
        dest.setName(dest.getUploadScannerName() + (abType.equals("N") ? "" : "-" + abType + "卷"));
        return dest;
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public ZTreeNode m10clone() {
        ZTreeNode dest = new ZTreeNode();
        try {
            PropertyUtils.copyProperties(dest, this);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return dest;
    }
}
