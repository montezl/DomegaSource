package com.dmj.service.historyTable;

import com.dmj.domain.AjaxData;
import com.dmj.domain.vo.Imgpath;
import com.zht.db.Transaction;
import java.util.List;

/* loaded from: HistoryTableService.class */
public interface HistoryTableService {
    @Transaction
    void createHistoryTable(Long l, String str);

    @Transaction
    void moveHistory(String str, String str2) throws Exception;

    List<AjaxData> queryExam();

    @Transaction
    Integer moveData(String str, String str2) throws Exception;

    @Transaction
    void inserthistablecount(Long l, String str);

    String queryhisexam(String str);

    @Transaction
    void updatetablename(String str, String str2, String str3) throws Exception;

    void moveHistory2(String str, String str2) throws Exception;

    String imgsavehuixian();

    List configimglist(String str);

    void inithistablecount(String str);

    Imgpath GetImgpath(String str);

    String tableNum(String str, String str2);

    @Transaction
    void convertImgToHtml();
}
