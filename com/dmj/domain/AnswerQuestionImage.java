package com.dmj.domain;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;

/* loaded from: AnswerQuestionImage.class */
public class AnswerQuestionImage implements Serializable {
    private Integer id;
    private Integer examPaperNum;
    private String img;
    private String html;
    private String img1;
    private String insertUser;
    private String insertDate;
    private String description;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private int page;
    private String questionNum;
    private String groupNum;
    private int width;
    private int height;
    private String imgpath;
    private String htmlJson;

    public AnswerQuestionImage() {
    }

    public AnswerQuestionImage(Integer id, Integer examPaperNum, String img, String insertUser, String insertDate, String description, String isDelete, String ext1, String ext2, String ext3, int page, String questionNum, int width, int height, String groupNum) {
        this.id = id;
        this.examPaperNum = examPaperNum;
        this.img = img;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.page = page;
        this.questionNum = questionNum;
        this.width = width;
        this.height = height;
        this.groupNum = groupNum;
    }

    public AnswerQuestionImage fill() {
        try {
            if (StrUtil.isNotEmpty(this.img) && FileUtil.exist(this.img)) {
                this.htmlJson = FileUtil.readString(this.img, CharsetUtil.CHARSET_UTF_8);
            }
        } catch (Exception e) {
        }
        return this;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExamPaperNum() {
        return this.examPaperNum;
    }

    public void setExamPaperNum(Integer examPaperNum) {
        this.examPaperNum = examPaperNum;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
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

    public String getImg1() {
        return this.img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getGroupNum() {
        return this.groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
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
