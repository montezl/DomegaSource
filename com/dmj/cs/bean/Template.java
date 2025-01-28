package com.dmj.cs.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: Template.class */
public class Template implements Serializable {
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
    private String realname;
    private String examPaperNum;
    private boolean isDouble;
    private int pageCount;
    private String paperSize;
    private Map<String, CsDefine> defineInfo;
    private List<Map<String, Object>> questionInfo;
    private Map<String, byte[]> temImageMap;
    private Map<String, byte[]> srcImageMap;
    private Map<Integer, byte[]> clipPageMarkImgMap;
    private byte[] xml;
    private String location;
    private String filename;
    private int imgPath;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(String examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public boolean isDouble() {
        return this.isDouble;
    }

    public void setDouble(boolean isDouble) {
        this.isDouble = isDouble;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getPaperSize() {
        return this.paperSize;
    }

    public void setPaperSize(String paperSize) {
        this.paperSize = paperSize;
    }

    public Map<String, CsDefine> getDefineInfo() {
        return this.defineInfo;
    }

    public void setDefineInfo(Map<String, CsDefine> defineInfo) {
        this.defineInfo = defineInfo;
    }

    public List<Map<String, Object>> getQuestionInfo() {
        return this.questionInfo;
    }

    public void setQuestionInfo(List<Map<String, Object>> questionInfo) {
        this.questionInfo = questionInfo;
    }

    public Map<String, byte[]> getTemImageMap() {
        return this.temImageMap;
    }

    public void setTemImageMap(Map<String, byte[]> temImageMap) {
        this.temImageMap = temImageMap;
    }

    public Map<String, byte[]> getSrcImageMap() {
        return this.srcImageMap;
    }

    public void setSrcImageMap(HashMap<String, byte[]> srcImageMap) {
        this.srcImageMap = srcImageMap;
    }

    public String getAbType() {
        return this.abType;
    }

    public void setAbType(String abType) {
        this.abType = abType;
    }

    public byte[] getXml() {
        return this.xml;
    }

    public void setXml(byte[] xml) {
        this.xml = xml;
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

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getUploadPicUrl() {
        return this.uploadPicUrl;
    }

    public void setUploadPicUrl(String uploadPicUrl) {
        this.uploadPicUrl = uploadPicUrl;
    }

    public Map<Integer, byte[]> getClipPageMarkImgMap() {
        return this.clipPageMarkImgMap;
    }

    public void setClipPageMarkImgMap(HashMap<Integer, byte[]> clipPageMarkImgMap) {
        this.clipPageMarkImgMap = clipPageMarkImgMap;
    }

    public int getImgPath() {
        return this.imgPath;
    }

    public void setImgPath(int imgPath) {
        this.imgPath = imgPath;
    }
}
