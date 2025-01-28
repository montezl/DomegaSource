package com.dmj.domain;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.dmj.util.JsonType;
import java.util.List;

/* loaded from: Answerexampaperimage.class */
public class Answerexampaperimage {
    private String id;
    private String examPaperNum;
    private String imgpath;
    private String img;
    private String html;
    private String imgtype;
    private String page;
    private String width;
    private String height;
    private String insertUser;
    private String insertDate;
    private String isDelete;
    private String description;
    private List<String> htmlList;

    public Answerexampaperimage fill() {
        try {
            if (StrUtil.isNotEmpty(this.img) && FileUtil.exist(this.img)) {
                String json = FileUtil.readString(this.img, CharsetUtil.CHARSET_UTF_8);
                if (StrUtil.isNotEmpty(json)) {
                    this.htmlList = (List) JSON.parseObject(json, JsonType.List1_String, new Feature[0]);
                }
            }
        } catch (Exception e) {
        }
        return this;
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

    public String getImgpath() {
        return this.imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImgtype() {
        return this.imgtype;
    }

    public void setImgtype(String imgtype) {
        this.imgtype = imgtype;
    }

    public String getPage() {
        return this.page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
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

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getHtmlList() {
        return this.htmlList;
    }

    public void setHtmlList(List<String> htmlList) {
        this.htmlList = htmlList;
    }

    public String getHtml() {
        return this.html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
