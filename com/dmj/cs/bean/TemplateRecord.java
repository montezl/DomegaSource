package com.dmj.cs.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;

/* loaded from: TemplateRecord.class */
public class TemplateRecord implements Serializable {
    private static final long serialVersionUID = 1;
    private Integer id;
    private String examNum;
    private String gradeNum;
    private String subjectNum;
    private String abType;
    private String uploadScannerNum;
    private String uploadScannerName;
    private String scannerNum;
    private String scannerName;
    private String templatePicUrl;
    private String uploadPicUrl;
    private String xmlUrl;
    private boolean mother;
    private String uploadUser;
    private String uploadDate;
    private String createUser;
    private String createDate;
    private int templateStatus;
    private int waittingUpdateTemplate;
    private String testScanTemplateRecordNum;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAbType() {
        return this.abType;
    }

    public void setAbType(String abType) {
        this.abType = abType;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getExamNum() {
        return this.examNum;
    }

    public void setExamNum(String examNum) {
        this.examNum = examNum;
    }

    public String getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getSubjectNum() {
        return this.subjectNum;
    }

    public void setSubjectNum(String subjectNum) {
        this.subjectNum = subjectNum;
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

    public void setScannerName(String scannerName) {
        this.scannerName = scannerName;
    }

    public boolean isMother() {
        return this.mother;
    }

    public void setMother(boolean mother) {
        this.mother = mother;
    }

    public String getTemplatePicUrl() {
        return this.templatePicUrl;
    }

    public void setTemplatePicUrl(String templatePicUrl) {
        this.templatePicUrl = templatePicUrl;
    }

    public String getXmlUrl() {
        return this.xmlUrl;
    }

    public void setXmlUrl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
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

    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getTemplateStatus() {
        return this.templateStatus;
    }

    public void setTemplateStatus(int templateStatus) {
        this.templateStatus = templateStatus;
    }

    public String getUploadPicUrl() {
        return this.uploadPicUrl;
    }

    public void setUploadPicUrl(String uploadPicUrl) {
        this.uploadPicUrl = uploadPicUrl;
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

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public TemplateRecord m9clone() {
        TemplateRecord dest = new TemplateRecord();
        try {
            PropertyUtils.copyProperties(dest, this);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return dest;
    }
}
