package com.dmj.cs.bean;

import com.alibaba.fastjson.JSON;
import com.dmj.cs.util.ClipRectType;
import com.dmj.cs.util.CsUtils;
import java.io.Serializable;
import java.util.List;

/* loaded from: ClipRect.class */
public class ClipRect implements Serializable {
    private static final long serialVersionUID = 1;
    private String questionNum;
    private String parentQuestionNum;
    private String clipRectType;
    private String result;
    private String answer;
    private String fileName;
    private Rectangle rectangle;
    private byte[] image;
    private List<Rectangle> extRectList;
    private List<ScoringPointRect> ScoringPointRectList;

    public String getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getParentQuestionNum() {
        return this.parentQuestionNum;
    }

    public void setParentQuestionNum(String parentQuestionNum) {
        this.parentQuestionNum = parentQuestionNum;
    }

    public String getClipRectType() {
        return this.clipRectType;
    }

    public void setClipRectType(String clipRectType) {
        this.clipRectType = clipRectType;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public List<Rectangle> getExtRectList() {
        return this.extRectList;
    }

    public void setExtRectList(List<Rectangle> extRectList) {
        this.extRectList = extRectList;
    }

    public List<ScoringPointRect> getScoringPointRectList() {
        return this.ScoringPointRectList;
    }

    public void setScoringPointRectList(List<ScoringPointRect> scoringPointRectList) {
        this.ScoringPointRectList = scoringPointRectList;
    }

    public ClipRect(String questionNum, String parentQuestionNum, String clipRectType, String result, String answer, String fileName, Rectangle rectangle, byte[] image, List<Rectangle> extRectList) {
        this.questionNum = questionNum;
        this.parentQuestionNum = parentQuestionNum;
        this.clipRectType = clipRectType;
        this.result = result;
        this.answer = answer;
        this.fileName = fileName;
        this.rectangle = rectangle;
        this.image = image;
        this.extRectList = extRectList;
    }

    public ClipRect() {
    }

    public boolean isChoose() {
        return ClipRectType.Choose.equals(this.clipRectType);
    }

    public boolean isSubjective() {
        return ClipRectType.Subjective.equals(this.clipRectType);
    }

    public boolean isObjective() {
        return ClipRectType.Objective.equals(this.clipRectType);
    }

    public boolean isScore() {
        return ClipRectType.Score.equals(this.clipRectType);
    }

    public boolean isObjectiveOption() {
        return ClipRectType.ObjectiveOption.equals(this.clipRectType);
    }

    public int getAnswerIndex() {
        if (this.answer == null || this.answer.equals("")) {
            return 0;
        }
        char c = this.answer.toCharArray()[0];
        return c - 'A';
    }

    public int max() {
        String regStr = this.result.replaceAll("[a-zA-Z]+", "");
        char[] array = regStr.toCharArray();
        int max = 0;
        for (int i = 0; i < array.length; i += 2) {
            int one = Integer.valueOf(array[i] + "" + array[i + 1]).intValue();
            if (one > max) {
                max = one;
            }
        }
        return max;
    }

    public int min() {
        String regStr = this.result.replaceAll("[a-zA-Z]+", "");
        char[] array = regStr.toCharArray();
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; i += 2) {
            int one = Integer.valueOf(array[i] + "" + array[i + 1]).intValue();
            if (one < min) {
                min = one;
            }
        }
        return min;
    }

    public String optionException(int minPaint, int maxPaint) {
        if (CsUtils.IsNullOrEmpty(this.answer)) {
            return "0";
        }
        String regStr = this.result.replaceAll("[a-zA-Z]+", "");
        char[] array = regStr.toCharArray();
        for (int i = 0; i < array.length; i += 2) {
            int paint = Integer.valueOf(array[i] + "" + array[i + 1]).intValue();
            if (paint >= minPaint && paint <= maxPaint) {
                return "2";
            }
        }
        return "1";
    }

    public String getExtClipRectListByJson() {
        if (this.extRectList != null && this.extRectList.size() > 0) {
            return JSON.toJSONString(this.extRectList);
        }
        return null;
    }

    public String getScoringPointListJson() {
        if (this.ScoringPointRectList != null && this.ScoringPointRectList.size() > 0) {
            return JSON.toJSONString(this.ScoringPointRectList);
        }
        return null;
    }
}
