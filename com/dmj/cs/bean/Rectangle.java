package com.dmj.cs.bean;

import java.io.Serializable;
import org.apache.commons.beanutils.BeanUtils;

/* loaded from: Rectangle.class */
public class Rectangle implements Serializable {
    private static final long serialVersionUID = 1;
    private int x;
    private int y;
    private int width;
    private int height;
    private int scoreX;
    private int scoreY;
    private int scoreWidth;
    private int scoreHeight;

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

    public int getScoreX() {
        return this.scoreX;
    }

    public void setScoreX(int scoreX) {
        this.scoreX = scoreX;
    }

    public int getScoreY() {
        return this.scoreY;
    }

    public void setScoreY(int scoreY) {
        this.scoreY = scoreY;
    }

    public int getScoreWidth() {
        return this.scoreWidth;
    }

    public void setScoreWidth(int scoreWidth) {
        this.scoreWidth = scoreWidth;
    }

    public int getScoreHeight() {
        return this.scoreHeight;
    }

    public void setScoreHeight(int scoreHeight) {
        this.scoreHeight = scoreHeight;
    }

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle() {
    }

    public Rectangle objectiveRectangle() {
        Rectangle objective = new Rectangle();
        try {
            BeanUtils.copyProperties(objective, this);
            if (objective.width > objective.height) {
                objective.scoreX = objective.x + objective.width;
                objective.scoreY = objective.y;
            } else {
                objective.scoreX = objective.x;
                objective.scoreY = objective.y + objective.height;
            }
            objective.scoreWidth = 16;
            objective.scoreWidth = 16;
            return objective;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Rectangle subjctiveRectangle() {
        Rectangle subjective = new Rectangle();
        try {
            BeanUtils.copyProperties(subjective, this);
            subjective.scoreWidth = 200;
            subjective.scoreWidth = 200;
            subjective.scoreX = Math.max((subjective.x + subjective.width) - subjective.scoreWidth, 0);
            subjective.scoreY = Math.max((subjective.y + subjective.height) - subjective.scoreHeight, 0);
            return subjective;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
