package com.dmj.domain;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.log4j.Logger;

/* loaded from: ExampaperQuestionimage.class */
public class ExampaperQuestionimage {
    private String id;
    private String examPaperNum;
    private String questionNum;
    private String img;
    private String html;
    private String page;
    private String insertUser;
    private String insertDate;
    private String description;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private int width;
    private int height;
    private String imgpath;
    private String htmlJson;
    Logger log = Logger.getLogger(getClass());

    public ExampaperQuestionimage fill() {
        try {
            this.log.info("fill()方法：---" + this.img);
            this.log.info("fill()方法img路径是否是空：---" + StrUtil.isNotEmpty(this.img));
            this.log.info("fill()方法判断改路径文件是否存在：---" + FileUtil.exist(this.img));
            if (StrUtil.isNotEmpty(this.img) && FileUtil.exist(this.img)) {
                this.htmlJson = FileUtil.readString(this.img, CharsetUtil.CHARSET_UTF_8);
                this.log.info("fill()方法img路径查询HTML：---" + this.htmlJson);
            }
        } catch (Exception e) {
            this.log.info("原题小切图json还原出错：---" + e.getMessage());
        }
        return this;
    }

    public ExampaperQuestionimage() {
    }

    public ExampaperQuestionimage(String id, String examPaperNum, String questionNum, String img, String page, String insertUser, String insertDate, String description, String isDelete, String ext1, String ext2, String ext3, int width, int height) {
        this.id = id;
        this.examPaperNum = examPaperNum;
        this.questionNum = questionNum;
        this.img = img;
        this.page = page;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.width = width;
        this.height = height;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(String examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPage() {
        return this.page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
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

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return this.ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String toString() {
        return "ExampaperQuestionimage [description=" + this.description + ", examPaperNum=" + this.examPaperNum + ", ext1=" + this.ext1 + ", ext2=" + this.ext2 + ", ext3=" + this.ext3 + ", id=" + this.id + ", img=" + this.img + ", insertDate=" + this.insertDate + ", insertUser=" + this.insertUser + ", isDelete=" + this.isDelete + ", page=" + this.page + ", questionNum=" + this.questionNum + "]";
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImgpath() {
        return this.imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getHtmlJson() {
        return this.htmlJson;
    }

    public void setHtmlJson(String htmlJson) {
        this.htmlJson = htmlJson;
    }

    public String getHtml() {
        return this.html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
