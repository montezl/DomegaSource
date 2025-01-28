package com.dmj.cs.bean;

import java.io.Serializable;

/* loaded from: ScoringPointRect.class */
public class ScoringPointRect implements Serializable {
    private static final long serialVersionUID = 1;
    private int x;
    private int y;
    private int width;
    private int height;
    private int subjectiveWidth;
    private int subjectiveHeight;

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
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

    public int getSubjectiveWidth() {
        return this.subjectiveWidth;
    }

    public void setSubjectiveWidth(int subjectiveWidth) {
        this.subjectiveWidth = subjectiveWidth;
    }

    public int getSubjectiveHeight() {
        return this.subjectiveHeight;
    }

    public void setSubjectiveHeight(int subjectiveHeight) {
        this.subjectiveHeight = subjectiveHeight;
    }

    public ScoringPointRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public ScoringPointRect() {
    }
}
