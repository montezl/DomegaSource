package com.dmj.service.ctb;

import com.dmj.domain.AnswerQuestionImage;
import com.dmj.domain.Answerexampaperimage;
import com.dmj.domain.ExampaperQuestionimage;
import java.util.List;

/* loaded from: Ctb_StandardAndFine_img.class */
public interface Ctb_StandardAndFine_img {
    byte[] QuestionImage(String str, String str2);

    List getStdAnswerImageSize(String str, String str2, String str3, String str4, String str5);

    List getExamPaperQuesImageSize(String str, String str2, String str3, String str4, String str5);

    byte[] rightQuestionImgGrade(String str, String str2);

    byte[] rightQuestionImg(String str, String str2, String str3);

    byte[] questionOrAnswerBigImg(String str);

    byte[] getillimg(String str);

    byte[] getExamineeImage(String str);

    byte[] getScoreImage(String str);

    byte[] history_getQuesionImage(String str);

    byte[] history_getQuesionImage(Integer num, String str, String str2, String str3);

    List<String> history_getQuesionImage_scoreIdList(String str, String str2, String str3, String str4, String str5);

    byte[] history_getBigImage(String str);

    byte[] history_QuestionImage(String str, String str2);

    String isZhuTi(String str, String str2);

    String getImgPath(String str, String str2);

    ExampaperQuestionimage getExampaperQuestionImagePath(String str, String str2);

    AnswerQuestionImage getAnswerQuestionImageImagePath(String str, String str2);

    Answerexampaperimage getAnswerexampaperimagePath(String str, String str2);

    byte[] history_rightQuestionImg(String str, String str2);

    byte[] abimage(String str, String str2);

    List getimgurl(String str, String str2);

    byte[] imageC(String str, String str2);
}
