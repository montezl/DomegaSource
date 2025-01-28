package com.dmj.cs.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dmj.auth.bean.License;
import com.dmj.cs.bean.ClipRect;
import com.dmj.cs.bean.ClipRectWrapper;
import com.dmj.cs.bean.ClipRectWrapperCollection;
import com.dmj.cs.bean.Corner;
import com.dmj.cs.bean.CsDefine;
import com.dmj.cs.bean.CsStudent;
import com.dmj.cs.bean.Rectangle;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.ClipPageMark;
import com.dmj.domain.RegExaminee;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.JsonType;
import com.dmj.util.Util;
import com.dmj.util.msg.RspMsg;
import com.zht.db.Jsons;
import com.zht.db.RowArg;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/* loaded from: IntoDbUtil2.class */
public class IntoDbUtil2 {
    private String examNum;
    private String gradeNum;
    private String testingCentreId;
    private String subjectNum;
    private String paperNum;
    private String examRoomNum;
    private String studentId;
    private String batch;
    private String username;
    private String regid;
    private boolean scanType;
    private String isDouble;
    private String isOver;
    private int pageNo;
    private String groupNum;
    private String type;
    private boolean hasMarkOrNot;
    private boolean isBuLu;
    private boolean existStuInfo;
    private int paintPercent;
    private int minPaint;
    private int maxPaint;
    private String scantime;
    private String abValue;
    private String clipIntoDbId;
    private String uploadScannerNum;
    private String location;
    private int imgpathId;
    private String ftpPath;
    private Map<String, CsDefine> defineInfoMap;
    private Map<String, Map<String, Object>> questionSingleOrDoubleMarkInfoMap;
    private Map<String, Object> paperMap;
    private Map<String, ClipRect> picInfoMap;
    private ClipRect cornerClipRect;
    private boolean batchReClip;
    char[] charray = {'-', '_'};
    private List<Object[]> scoreList = new ArrayList();
    private List<Object[]> objectiveScoreList = new ArrayList();
    private List<Object[]> scoreImageList = new ArrayList();
    private List<Object[]> taskList = new ArrayList();
    private List<Object[]> questionImageList = new ArrayList();
    private List<Object[]> objOptionList = new ArrayList();
    private List<Object[]> imageBytesList = new ArrayList();
    private List<Object[]> scoringPointList = new ArrayList();
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    Dbutil dbutil = new Dbutil();
    private String time = DateUtil.getCurrentTime();

    public IntoDbUtil2(Map<String, Object> paperMap, String picInfoMapJson) {
        this.abValue = "N";
        this.clipIntoDbId = null;
        this.paperMap = paperMap;
        this.picInfoMap = (Map) JSONObject.parseObject(picInfoMapJson, JsonType.Map1_StringClipRect, new Feature[0]);
        if (paperMap.containsKey("clipIntoDbId")) {
            this.clipIntoDbId = CsUtils.getString(paperMap.get("clipIntoDbId"));
        }
        this.testingCentreId = CsUtils.getString(paperMap.get("testingCentreId"));
        this.paperNum = CsUtils.getString(paperMap.get("examPaperNum"));
        this.examRoomNum = CsUtils.getString(paperMap.get("examRoomNum"));
        this.studentId = CsUtils.getString(paperMap.get(Const.EXPORTREPORT_studentId));
        this.batch = CsUtils.getString(paperMap.get("batch"));
        this.username = CsUtils.getString(paperMap.get("username"));
        this.pageNo = CsUtils.getInt(paperMap.get("pageNo"));
        this.groupNum = CsUtils.getString(paperMap.get("groupNum"));
        this.type = CsUtils.getString(paperMap.get("type"));
        this.isBuLu = CsUtils.getBoolean(paperMap.get("isBuLu"));
        this.abValue = CsUtils.getString(paperMap.get("abValue"));
        this.paintPercent = CsUtils.getInt(paperMap.get("paintPercent"));
        this.minPaint = CsUtils.getInt(paperMap.get("minPaint"));
        this.maxPaint = CsUtils.getInt(paperMap.get("maxPaint"));
        this.examNum = CsUtils.getString(paperMap.get(Const.EXPORTREPORT_examNum));
        this.gradeNum = CsUtils.getString(paperMap.get(Const.EXPORTREPORT_gradeNum));
        this.subjectNum = CsUtils.getString(paperMap.get(Const.EXPORTREPORT_subjectNum));
        this.hasMarkOrNot = CsUtils.getBoolean(paperMap.get("hasMarkOrNot"));
        this.isOver = CsUtils.getString(paperMap.get("isOver"));
        this.regid = CsUtils.getString(paperMap.get("regid"));
        if (CsUtils.IsNullOrEmpty(this.regid)) {
            this.regid = GUID.getGUIDStr();
        }
        this.scanType = "0".equals(CsUtils.getString(paperMap.get("scanType")));
        this.existStuInfo = CsUtils.getBoolean(paperMap.get("existStuInfo"));
        this.isDouble = CsUtils.getString(paperMap.get("doubleFaced"));
        this.imgpathId = CsUtils.getInt(paperMap.get("ImgPathConfig_ID"));
        this.location = CsUtils.getString(paperMap.get("ImgPathConfig_Location"));
        this.ftpPath = CsUtils.getString(paperMap.get("ImgPathConfig_Filename")) + "|" + this.testingCentreId + this.gradeNum + this.subjectNum;
        this.scantime = CsUtils.getString(paperMap.get("scantime"));
        this.uploadScannerNum = CsUtils.getString(paperMap.get("uploadScannerNum"));
        this.cornerClipRect = getClipRect(ClipRectType.Corner);
        if (!this.hasMarkOrNot) {
            this.questionSingleOrDoubleMarkInfoMap = SingleDoubleMarkingCache.get(this.paperNum);
        }
        boolean removeCache = CsUtils.getBoolean(paperMap.get(ClipRectType.RemoveCache));
        this.defineInfoMap = DefineCache.get(this.paperNum, removeCache);
        if (paperMap.containsKey("batchReClip")) {
            this.batchReClip = CsUtils.getBoolean(paperMap.get("batchReClip"));
        }
    }

    private ClipRect getClipRect(String clipRectType) {
        if (this.picInfoMap != null && this.picInfoMap.containsKey(clipRectType)) {
            return this.picInfoMap.get(clipRectType);
        }
        return null;
    }

    public String[] intoDb() throws Throwable {
        if (isFirst()) {
            return callback("开始处理");
        }
        if (isOver()) {
            return callback("完成处理");
        }
        if (isDoDouble()) {
            return callback("重复处理");
        }
        if (this.isBuLu) {
            DeletePageInfo();
        }
        InsertCorner();
        if (isClipError()) {
            error();
            return callback("裁切失败");
        }
        return success();
    }

    public String[] callback(String msg) {
        String info = StrUtil.format("考试：{} 年级：{} 科目：{} 考点：{} 批次：{} 考场：{} CNum:{} clipIntoDbId:{} {} ...", new Object[]{this.examNum, this.gradeNum, this.subjectNum, this.testingCentreId, this.batch, this.examRoomNum, this.groupNum, this.clipIntoDbId, msg});
        return new String[]{"", "", "", "", "", Jsons.toJSONString(RspMsg.error(info))};
    }

    public void error() throws Throwable {
        int schoolNum = 0;
        String classNum = "-1";
        String cardNum = null;
        String examineeNum = "";
        RegExaminee reg = getRegExamineeByBrother();
        if (reg != null) {
            this.studentId = reg.getStudentId();
            schoolNum = reg.getSchoolNum();
            classNum = reg.getClassNum();
            cardNum = reg.getScannum();
            examineeNum = reg.getExamineeNum();
        }
        insertRegResult(classNum, cardNum, examineeNum, String.valueOf(schoolNum));
        insertBig2StudentImg(this.regid);
        insertBigErrorImg(this.regid, this.examRoomNum);
    }

    public String[] success() throws Throwable {
        String[] array = DoCardNo();
        String studentIdError = array[1];
        String classNum = array[2];
        String schoolNum = array[4];
        AbsentOrFaultAB(schoolNum);
        SaveClipPageMark();
        DoAllQuestion(schoolNum, classNum);
        FutureTask<Boolean> result = new FutureTask<>(() -> {
            try {
                CsUtils.batchWriteByteArrayToFile(this.location, this.imageBytesList);
                return true;
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
        new Thread(result).start();
        batchInsert();
        UpdateChooseQuestion2(this.studentId);
        insertBig2StudentImg(this.regid);
        modifyObjectieveSwithAB(this.abValue, this.studentId, this.regid);
        result.get();
        String msg = Jsons.toJSONString(RspMsg.success("成功", null));
        return new String[]{this.studentId, studentIdError, this.abValue, classNum, this.examRoomNum, msg};
    }

    public boolean isClipError() {
        return this.pageNo == 0 || CsUtils.getInt(this.paperMap.get("clipStatus")) != 0;
    }

    public boolean isBulu() {
        return this.isBuLu;
    }

    private boolean isDoDouble() {
        if (this.clipIntoDbId != null) {
            Map args = new HashMap();
            args.put("clipIntoDbId", this.clipIntoDbId);
            if (this.dao2._queryObject("SELECT id FROM regexaminee WHERE clipIntoDbId={clipIntoDbId}", args) != null) {
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean isFirst() {
        if (!"7".equals(this.isOver)) {
            return false;
        }
        updateUploadpicrecordStartStatus();
        return true;
    }

    private boolean isOver() {
        if (!"1".equals(this.isOver)) {
            return false;
        }
        InsertCorrectStatus(this.examRoomNum);
        return true;
    }

    public void SaveClipPageMark() throws Throwable {
        if (!this.picInfoMap.containsKey(ClipRectType.ClipPageMarkSampleImg)) {
            return;
        }
        ClipRect rect = this.picInfoMap.get(ClipRectType.ClipPageMarkSampleImg);
        String path = GetNewFileName(23);
        String sampleId = CsUtils.getClipSampleId(rect.getResult(), this.abValue, this.pageNo);
        String fillRatio = rect.getAnswer();
        CsUtils.writeByteArrayToFile(this.location, path, rect.getImage());
        ClipPageMark mark = new ClipPageMark();
        mark.setExamNum(this.examNum);
        mark.setGradeNum(this.gradeNum);
        mark.setSubjectNum(this.subjectNum);
        mark.setTestingCentreId(this.testingCentreId);
        mark.setAbType(this.abValue);
        mark.setId(GUID.getGUIDStr());
        mark.setImg(path);
        mark.setImgpath(String.valueOf(this.imgpathId));
        mark.setFillRatio(fillRatio);
        mark.setInsertDate(this.time);
        mark.setInsertUser(this.username);
        mark.setPage(String.valueOf(this.pageNo));
        mark.setRegid(this.regid);
        mark.setSampleId(sampleId);
        mark.setScannerNum(this.uploadScannerNum);
        this.dao2.save(mark);
    }

    public void AbsentOrFaultAB(String schoolNum) throws Throwable {
        Object oo;
        String abtype;
        String illegalid = GUID.getGUIDStr();
        if (this.pageNo == 1) {
            String type = "2";
            int illegalVal = 0;
            int abVal = 0;
            if (this.picInfoMap.containsKey(ClipRectType.Illegal)) {
                ClipRect rect = this.picInfoMap.get(ClipRectType.Illegal);
                String path = GetNewFileName(4);
                CsUtils.writeByteArrayToFile(this.location, path, rect.getImage());
                String[] fileds2 = {"regid", "img", "insertDate", "imgpath"};
                Object[] values2 = {this.regid, path, this.time, Integer.valueOf(this.imgpathId)};
                this.dao2.insert("illegalimage", fileds2, values2);
                String answer = rect.getAnswer();
                illegalVal = CsUtils.getMax(rect.getResult());
                boolean z = -1;
                switch (answer.hashCode()) {
                    case Const.clipError_failure /* 0 */:
                        if (answer.equals("")) {
                            z = false;
                            break;
                        }
                        break;
                    case 65:
                        if (answer.equals("A")) {
                            z = true;
                            break;
                        }
                        break;
                    case 66:
                        if (answer.equals("B")) {
                            z = 2;
                            break;
                        }
                        break;
                }
                switch (z) {
                    case Const.clipError_failure /* 0 */:
                        type = "2";
                        break;
                    case true:
                        type = "0";
                        break;
                    case true:
                        type = "1";
                        break;
                }
            }
            if (this.picInfoMap.containsKey(ClipRectType.PageAb)) {
                ClipRect rect2 = this.picInfoMap.get(ClipRectType.PageAb);
                abVal = CsUtils.getMax(rect2.getResult());
                String path2 = GetNewFileName(5);
                CsUtils.writeByteArrayToFile(this.location, path2, rect2.getImage());
                String[] fileds22 = {"regid", "illegalid", "img", "insertDate", "imgpath"};
                Object[] values22 = {this.regid, illegalid, path2, this.time, Integer.valueOf(this.imgpathId)};
                this.dao2.insert("exampapertypeimage", fileds22, values22);
                if (!this.isBuLu || this.batchReClip) {
                    this.abValue = rect2.getAnswer().equals("") ? "A" : rect2.getAnswer();
                }
                UpdateObjectiveScore(this.abValue, this.groupNum);
            }
            List<Object> list = this.dao2.queryColList("SELECT id from illegal WHERE examPaperNum=?  and studentId=?", new Object[]{this.paperNum, this.studentId});
            if (list != null && list.size() > 0) {
                for (Object id : list) {
                    this.dao2.execute("DELETE from illegal where id = ?", new Object[]{id});
                }
            }
            String[] fileds1 = {"id", "regid", "examPaperNum", Const.EXPORTREPORT_schoolNum, "examinationRoomNum", Const.EXPORTREPORT_studentId, "type", Const.CORRECT_SCORECORRECT, "insertUser", "insertDate", "updateUser", "updateDate", "exampaperType", "testingCentreId", "illegalVal", "abVal"};
            Object[] values1 = {illegalid, this.regid, this.paperNum, schoolNum, this.examRoomNum, this.studentId, type, "0", this.username, this.time, this.username, this.time, this.abValue, this.testingCentreId, Integer.valueOf(illegalVal), Integer.valueOf(abVal)};
            this.dao2.insert("illegal", fileds1, values1);
            return;
        }
        if (!this.isBuLu && (oo = this.dao2.queryObject("SELECT abtype from exampaper WHERE examPaperNum=?", new Object[]{this.paperNum})) != null && this.dbutil.equalsAb(CsUtils.getString(oo)) && (abtype = this.dbutil.GetAbType(this.groupNum)) != null && !abtype.equals(Const.sample_error_reRecognized)) {
            this.abValue = abtype;
        }
    }

    public void DoAllQuestion(String schooNum, String classNum) {
        ClipRectWrapperCollection clipRectWrapperCollection = new ClipRectWrapperCollection(this.picInfoMap, this.defineInfoMap);
        DoChoose(clipRectWrapperCollection);
        DoObjective(clipRectWrapperCollection, schooNum, classNum);
        DoSubjective(clipRectWrapperCollection, schooNum, classNum);
    }

    public void DoSubjective(ClipRectWrapperCollection clipRectWrapperCollection, String schoolNum, String classNum) {
        List<ClipRectWrapper> subjectiveClipRectWrapperList = clipRectWrapperCollection.getSubjective();
        if (subjectiveClipRectWrapperList == null || subjectiveClipRectWrapperList.size() == 0) {
            return;
        }
        Map<String, Map<String, Object>> questionImageMap = new HashMap();
        if (this.isBuLu) {
            questionImageMap = getSubjectiveQuestionImage();
            if (questionImageMap.size() > 0) {
                updateReClipSubjectiveStuInfoByRegId(schoolNum, classNum);
            }
        }
        if (this.hasMarkOrNot) {
            DoSubjective_Mark(clipRectWrapperCollection, schoolNum, classNum, questionImageMap);
        } else {
            DoSubjective_NoMark(clipRectWrapperCollection, schoolNum, classNum, questionImageMap);
        }
    }

    private void DoSubjective_Mark(ClipRectWrapperCollection clipRectWrapperCollection, String schoolNum, String classNum, Map<String, Map<String, Object>> questionImageMap) {
        List<ClipRectWrapper> subjectiveClipRectWrapperList = clipRectWrapperCollection.getSubjective();
        for (ClipRectWrapper c : subjectiveClipRectWrapperList) {
            ClipRect rect = c.getRect();
            List<CsDefine> items = getWaitForDoing(c);
            CsDefine define = items.get(0);
            String defineId = String.valueOf(define.getId());
            String scoreid = GUID.getGUIDStr();
            boolean isCrossPage = define.ifCrossPage();
            String crossInfo = "F";
            if (isCrossPage && define.getPage() != this.pageNo) {
                crossInfo = "T";
            }
            if (this.isBuLu) {
                String questionNum = null;
                if (define.ifChooseSon() || define.ifChooseGrandson()) {
                    List<String> questionList = (List) define.getItems().stream().map(sd -> {
                        return String.valueOf(sd.getId());
                    }).collect(Collectors.toList());
                    Optional<String> questionNumOptional = questionImageMap.keySet().stream().filter(question -> {
                        return questionList.contains(question);
                    }).findFirst();
                    if (questionNumOptional.isPresent()) {
                        questionNum = questionNumOptional.get();
                    }
                } else if (questionImageMap.containsKey(defineId)) {
                    questionNum = defineId;
                }
                if (StrUtil.isNotEmpty(questionNum)) {
                    Map<String, Object> questionInfo = questionImageMap.get(questionNum);
                    replaceQuestionImg(rect, questionInfo);
                }
            }
            if (c.getChildrens().size() > 0) {
                insertSmallScore(c.getChildrens().get(0).getRect(), define, scoreid, crossInfo, schoolNum, classNum);
            } else {
                Object[] values = {scoreid, this.regid, defineId, 0, 0, this.studentId, this.paperNum, schoolNum, classNum, this.gradeNum, this.username, this.time, this.examRoomNum, Integer.valueOf(this.pageNo), "-1", "F", "F", crossInfo, this.testingCentreId};
                this.scoreList.add(values);
            }
            String path = GetNewFileName(2);
            Rectangle r = rect.getRectangle().subjctiveRectangle();
            this.questionImageList.add(new Object[]{Long.valueOf(GUID.getGUID()), scoreid, path, Integer.valueOf(this.pageNo), this.time, Integer.valueOf(this.imgpathId), Integer.valueOf(r.getX()), Integer.valueOf(r.getY()), Integer.valueOf(r.getWidth()), Integer.valueOf(r.getHeight()), Integer.valueOf(r.getScoreX()), Integer.valueOf(r.getScoreY()), Integer.valueOf(r.getScoreWidth()), Integer.valueOf(r.getScoreHeight()), rect.getExtClipRectListByJson(), rect.getScoringPointListJson()});
            this.imageBytesList.add(new Object[]{path, rect.getImage()});
        }
    }

    private void replaceQuestionImg(ClipRect rect, String scoreId) {
        Object oo = this.dao2.queryObject("SELECT img from questionimage WHERE scoreid =?", new Object[]{scoreId});
        if (oo != null) {
            this.imageBytesList.add(new Object[]{oo, rect.getImage()});
            String scorePoint = rect.getScoringPointListJson();
            if (StrUtil.isNotEmpty(scorePoint)) {
                this.dao2.execute("update questionimage set scoringPoint=? where scoreId=?", new Object[]{scorePoint, scoreId});
            }
        }
    }

    private void DoSubjective_NoMark(ClipRectWrapperCollection clipRectWrapperCollection, String schoolNum, String classNum, Map<String, Map<String, Object>> questionImageMap) {
        List<Object> taskSingleOrDoubleList;
        int size;
        List<ClipRectWrapper> subjectiveClipRectWrapperList = clipRectWrapperCollection.getSubjective();
        for (ClipRectWrapper c : subjectiveClipRectWrapperList) {
            ClipRect rect = c.getRect();
            List<CsDefine> items = getWaitForDoing(c);
            for (CsDefine d : items) {
                String defineId = String.valueOf(d.getId());
                String scoreid = GUID.getGUIDStr();
                boolean isCrossPage = d.ifCrossPage();
                String crossInfo = "F";
                if (isCrossPage && d.getPage() != this.pageNo) {
                    crossInfo = "T";
                }
                if (!this.questionSingleOrDoubleMarkInfoMap.containsKey(defineId)) {
                    SingleDoubleMarkingCache.remove(this.paperNum);
                    this.questionSingleOrDoubleMarkInfoMap = SingleDoubleMarkingCache.get(this.paperNum);
                    if (!this.questionSingleOrDoubleMarkInfoMap.containsKey(defineId)) {
                        throw new RuntimeException("未查到define表id为" + defineId + "的单双评记录 !");
                    }
                }
                Map<String, Object> curSingleDoubleReviewMap = this.questionSingleOrDoubleMarkInfoMap.get(defineId);
                String groupNum = CsUtils.getString(curSingleDoubleReviewMap.get("groupNum"));
                String singleOrDoubleReview = CsUtils.getString(curSingleDoubleReviewMap.get("makType"));
                if (this.isBuLu) {
                    String questionNum = null;
                    if (d.ifChooseSon() || d.ifChooseGrandson()) {
                        List<String> questionList = (List) d.getItems().stream().map(sd -> {
                            return String.valueOf(sd.getId());
                        }).collect(Collectors.toList());
                        Optional<String> questionNumOptional = questionImageMap.keySet().stream().filter(question -> {
                            return questionList.contains(question);
                        }).findFirst();
                        if (questionNumOptional.isPresent()) {
                            questionNum = questionNumOptional.get();
                        }
                    } else if (questionImageMap.containsKey(defineId)) {
                        questionNum = defineId;
                    }
                    if (StrUtil.isNotEmpty(questionNum)) {
                        Map<String, Object> questionInfo = questionImageMap.get(questionNum);
                        String existScoreId = Convert.toStr(questionInfo.get("id"), "");
                        String continued = Convert.toStr(questionInfo.get("continued"), "");
                        replaceQuestionImg(rect, questionInfo);
                        if (!"T".equals(continued) && "1".equals(singleOrDoubleReview) && (size = CsUtils.size((taskSingleOrDoubleList = this.dao2.queryColList("select userNum from task where scoreId=?", new Object[]{existScoreId})))) > 0 && size < 2) {
                            int ping = Convert.toInt(taskSingleOrDoubleList.get(0)).intValue();
                            int ping2 = ping == 1 ? 2 : 1;
                            this.taskList.add(new Object[]{Long.valueOf(GUID.getGUID()), this.studentId, existScoreId, this.paperNum, defineId, "F", "F", "F", groupNum, "-1", this.time, "-1", null, "-1", Integer.valueOf(ping2), this.testingCentreId, 0});
                        }
                    }
                }
                insertScoreByNoMark(rect, scoreid, defineId, crossInfo, schoolNum, classNum);
                if (!"T".equals(crossInfo)) {
                    if ("0".equals(singleOrDoubleReview)) {
                        this.taskList.add(new Object[]{Long.valueOf(GUID.getGUID()), this.studentId, scoreid, this.paperNum, defineId, "F", "F", "F", groupNum, "-1", this.time, "-1", null, "-1", 1, this.testingCentreId, 0});
                    } else {
                        Object[] values = {Long.valueOf(GUID.getGUID()), this.studentId, scoreid, this.paperNum, defineId, "F", "F", "F", groupNum, "-1", this.time, "-1", null, "-1", 1, this.testingCentreId, 0};
                        this.taskList.add(values);
                        Object[] values2 = Arrays.copyOf(values, values.length);
                        values2[0] = Long.valueOf(GUID.getGUID());
                        values2[values2.length - 3] = 2;
                        this.taskList.add(values2);
                    }
                }
            }
        }
    }

    private void insertScoreByNoMark(ClipRect rect, String scoreid, String defineId, String crossInfo, String schoolNum, String classNum) {
        Object[] values = {scoreid, this.regid, defineId, 0, 0, this.studentId, this.paperNum, schoolNum, classNum, this.gradeNum, this.username, this.time, this.examRoomNum, Integer.valueOf(this.pageNo), "-1", "F", "F", crossInfo, this.testingCentreId};
        this.scoreList.add(values);
        String path = GetNewFileName(2);
        Rectangle r = rect.getRectangle().subjctiveRectangle();
        this.questionImageList.add(new Object[]{Long.valueOf(GUID.getGUID()), scoreid, path, Integer.valueOf(this.pageNo), this.time, Integer.valueOf(this.imgpathId), Integer.valueOf(r.getX()), Integer.valueOf(r.getY()), Integer.valueOf(r.getWidth()), Integer.valueOf(r.getHeight()), Integer.valueOf(r.getScoreX()), Integer.valueOf(r.getScoreY()), Integer.valueOf(r.getScoreWidth()), Integer.valueOf(r.getScoreHeight()), rect.getExtClipRectListByJson(), rect.getScoringPointListJson()});
        this.imageBytesList.add(new Object[]{path, rect.getImage()});
    }

    private void insertSmallScore(ClipRect rect, CsDefine define, String scoreid, String crossInfo, String schoolNum, String classNum) {
        String exception;
        Object[] examInfo = this.dao2._queryArray("select scoreModel,rule from exam where examNum={examNum}", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) this.examNum));
        String scoreModel = examInfo[0].toString();
        String rule = examInfo[1].toString();
        String defineId = String.valueOf(define.getId());
        String scoreStr = rect.getAnswer().replaceAll(Const.exampaper_doubleFaced_S, "");
        double regScore = 0.0d;
        double finalScore = 0.0d;
        double fullScore = define.getFullScore();
        if (scoreStr.equals(".")) {
            exception = "0";
            if (rule.equals("71")) {
            }
            if (rule.equals("72")) {
                finalScore = fullScore;
                regScore = fullScore;
            }
        } else {
            exception = "-1";
            int index = scoreStr.indexOf(46);
            if (index != -1) {
                String s = scoreStr.substring(index + 1);
                if (!CsUtils.IsNullOrEmpty(s) && CsUtils.getInt(s) != 5) {
                    scoreStr = scoreStr.substring(0, index);
                    if (CsUtils.IsNullOrEmpty(scoreStr)) {
                        scoreStr = "0";
                    }
                }
            }
            regScore = CsUtils.getDouble(scoreStr);
            if (regScore > fullScore) {
                exception = "4";
                finalScore = regScore;
            } else {
                if (scoreModel.equals(Const.score_plus)) {
                    finalScore = regScore;
                }
                if (scoreModel.equals(Const.score_reduces)) {
                    finalScore = fullScore - regScore;
                }
            }
        }
        Object[] values = {scoreid, this.regid, defineId, Double.valueOf(finalScore), Double.valueOf(regScore), this.studentId, this.paperNum, schoolNum, classNum, this.gradeNum, this.username, this.time, this.examRoomNum, Integer.valueOf(this.pageNo), exception, "F", "F", crossInfo, this.testingCentreId};
        this.scoreList.add(values);
        String path0 = GetNewFileName(3);
        this.scoreImageList.add(new Object[]{scoreid, path0, this.time, Integer.valueOf(this.imgpathId)});
        this.imageBytesList.add(new Object[]{path0, rect.getImage()});
    }

    private List<CsDefine> getWaitForDoing(ClipRectWrapper c) {
        List<CsDefine> items;
        CsDefine define = c.getDefine();
        if (define.ifChooseSon()) {
            CsDefine real = define.getItems().get(selectIndex(c));
            items = Arrays.asList(real);
        } else if (define.ifChooseGrandson()) {
            int selectIndex = selectIndex(c);
            if (define.getParent().ifMerge()) {
                int chooseOptionCount = define.getParent().getChildrens().size();
                items = define.getItems().subList(selectIndex * chooseOptionCount, (selectIndex + 1) * chooseOptionCount);
            } else {
                CsDefine real2 = define.getItems().get(selectIndex);
                items = Arrays.asList(real2);
            }
        } else if (define.getParent() != null && define.getParent().ifMerge()) {
            items = define.getItems();
        } else {
            items = Arrays.asList(define);
        }
        return items;
    }

    private int selectIndex(ClipRectWrapper c) {
        CsDefine define = c.getDefine();
        if (define.ifChooseSon() || define.ifChooseGrandson()) {
            String selectDefineId = c.selectDefineId();
            if (CsUtils.IsNullOrEmpty(selectDefineId)) {
                String dbSelectDefineId = dbSelectDefineId(define);
                selectDefineId = dbSelectDefineId;
                if (CsUtils.IsNullOrEmpty(dbSelectDefineId)) {
                    return 0;
                }
            }
            return selectIndexByDefineId(selectDefineId);
        }
        return -1;
    }

    private int selectIndexByDefineId(String defineId) {
        CsDefine parent = (CsDefine) ((List) this.defineInfoMap.values().stream().filter(d -> {
            return d.ifChoose() && d.getChildrens().parallelStream().anyMatch(sd -> {
                return String.valueOf(sd.getId()).equals(defineId);
            });
        }).collect(Collectors.toList())).get(0);
        List<CsDefine> childrens = parent.getChildrens();
        for (int i = 0; i < childrens.size(); i++) {
            CsDefine d2 = childrens.get(i);
            if (String.valueOf(d2.getId()).equals(defineId)) {
                return i;
            }
        }
        throw new RuntimeException("IntoDbUtil2->selectIndexByDefineId->defineId:" + defineId + "不存在...");
    }

    private void DoObjective(ClipRectWrapperCollection clipRectWrapperCollection, String schoolNum, String classNum) {
        List<ClipRectWrapper> objectiveClipRectWrapperList = clipRectWrapperCollection.getObjective();
        if (objectiveClipRectWrapperList == null || objectiveClipRectWrapperList.size() == 0) {
            return;
        }
        Map<String, Map<String, Object>> questionImageMap = new HashMap();
        if (this.isBuLu) {
            questionImageMap = getObjectiveQuestionImage();
            if (questionImageMap.size() > 0) {
                updateReClipObjectiveStuInfoByRegId(schoolNum, classNum);
            }
        }
        for (ClipRectWrapper c : objectiveClipRectWrapperList) {
            String scoreid = GUID.getGUIDStr();
            CsDefine define = c.getDefine();
            ClipRect rect = c.getRect();
            if (define == null) {
                throw new RuntimeException(StrUtil.format("{}对应的define不存在,请检查", new Object[]{rect.getQuestionNum()}));
            }
            int optionCount = define.getOptionCount();
            String defineId = String.valueOf(define.getId());
            String answer = rect.getAnswer();
            String result = rect.getResult();
            String regStr = CsUtils.reverseOrderRegResult(result);
            String regResult = CsUtils.reverseRegResult(result);
            String exception = rect.optionException(this.minPaint, this.maxPaint);
            double score = c.objectiveScore(this.abValue);
            String min = CsUtils.AddZero(rect.min());
            String max = CsUtils.AddZero(rect.max());
            int[] regMinMax = CsUtils.getRegMinOrMaxResult(answer, regStr);
            if (define.ifChooseSon() || define.ifChooseGrandson()) {
                int selectIndex = selectIndex(c);
                defineId = String.valueOf(define.getItems().get(selectIndex).getId());
            }
            if (this.isBuLu) {
                String questionNum = null;
                if (define.ifChooseSon() || define.ifChooseGrandson()) {
                    List<String> questionList = (List) define.getItems().stream().map(sd -> {
                        return String.valueOf(sd.getId());
                    }).collect(Collectors.toList());
                    Optional<String> questionNumOptional = questionImageMap.keySet().stream().filter(question -> {
                        return questionList.contains(question);
                    }).findFirst();
                    if (questionNumOptional.isPresent()) {
                        questionNum = questionNumOptional.get();
                    }
                } else if (questionImageMap.containsKey(defineId)) {
                    questionNum = defineId;
                }
                if (StrUtil.isNotEmpty(questionNum)) {
                }
            }
            if (define.ifMultiple()) {
                List<ClipRectWrapper> options = c.getChildrens();
                for (ClipRectWrapper option : options) {
                    ClipRect optionClipRect = option.getRect();
                    String item = optionClipRect.getResult().substring(0, 1);
                    String val = optionClipRect.getAnswer();
                    int selected = answer.indexOf(item) == -1 ? 0 : 1;
                    if (options.size() == 2) {
                        item = item.equals("A") ? "T" : "F";
                    }
                    String path2 = GetNewFileName(10);
                    this.objOptionList.add(new Object[]{Long.valueOf(GUID.getGUID()), this.paperNum, this.gradeNum, this.examRoomNum, this.regid, defineId, scoreid, Integer.valueOf(this.imgpathId), path2, item, val, 0, this.time, Integer.valueOf(selected), this.testingCentreId});
                    this.imageBytesList.add(new Object[]{path2, optionClipRect.getImage()});
                }
            }
            if (optionCount == 2) {
                answer = answer.replace('A', 'T').replace('B', 'F');
            }
            Object[] values = new Object[24];
            values[0] = scoreid;
            values[1] = this.regid;
            values[2] = answer;
            values[3] = defineId;
            values[4] = Double.valueOf(score);
            values[5] = regStr;
            values[6] = regResult;
            values[7] = this.studentId;
            values[8] = this.paperNum;
            values[9] = schoolNum;
            values[10] = classNum;
            values[11] = this.gradeNum;
            values[12] = this.username;
            values[13] = this.time;
            values[14] = this.examRoomNum;
            values[15] = Integer.valueOf(this.pageNo);
            values[16] = exception;
            values[17] = "F";
            values[18] = "F";
            values[19] = min;
            values[20] = max;
            values[21] = this.testingCentreId;
            values[22] = Integer.valueOf(regMinMax[1]);
            values[23] = Double.valueOf(regMinMax[1] == 0 ? 0.0d : regMinMax[0] / regMinMax[1]);
            this.objectiveScoreList.add(values);
            String path0 = GetNewFileName(2);
            Rectangle r = rect.getRectangle().objectiveRectangle();
            List<Rectangle> extRectList = rect.getExtRectList();
            String extRectWidth = null;
            if (CollUtil.isNotEmpty(extRectList)) {
                extRectWidth = Convert.toStr(Integer.valueOf(extRectList.get(0).getWidth()), (String) null);
            }
            this.questionImageList.add(new Object[]{Long.valueOf(GUID.getGUID()), scoreid, path0, Integer.valueOf(this.pageNo), this.time, Integer.valueOf(this.imgpathId), Integer.valueOf(r.getX()), Integer.valueOf(r.getY()), Integer.valueOf(r.getWidth()), Integer.valueOf(r.getHeight()), Integer.valueOf(r.getScoreX()), Integer.valueOf(r.getScoreY()), Integer.valueOf(r.getScoreWidth()), Integer.valueOf(r.getScoreHeight()), extRectWidth, null});
            this.imageBytesList.add(new Object[]{path0, rect.getImage()});
        }
    }

    private void replaceQuestionImg(ClipRect rect, Map<String, Object> questionInfo) {
        if (questionInfo != null && questionInfo.containsKey("img")) {
            String imgPath = Convert.toStr(questionInfo.get("img"), "");
            if (StrUtil.isNotEmpty(imgPath)) {
                this.imageBytesList.add(new Object[]{imgPath, rect.getImage()});
            }
            String scorePoint = rect.getScoringPointListJson();
            if (StrUtil.isNotEmpty(scorePoint)) {
                this.scoringPointList.add(new Object[]{scorePoint, questionInfo.get("id")});
            }
        }
    }

    private void updateReClipObjectiveStuInfoByRegId(String schoolNum, String classNum) {
        this.dao2._execute("update objectivescore set studentId={studentId},schoolNum={schoolNum},classNum={classNum},examinationRoomNum={examinationRoomNum} where regId={regId}", StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) this.studentId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("examinationRoomNum", (Object) this.examRoomNum).put("regId", (Object) this.regid));
    }

    private void updateReClipSubjectiveStuInfoByRegId(String schoolNum, String classNum) {
        if (this.hasMarkOrNot) {
            this.dao2._execute("update score set studentId={studentId},schoolNum={schoolNum},classNum={classNum},examinationRoomNum={examinationRoomNum} where regId={regId}", StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) this.studentId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("examinationRoomNum", (Object) this.examRoomNum).put("regId", (Object) this.regid));
        } else {
            this.dao2._execute("update task t INNER JOIN score s on s.id=t.scoreId set t.studentId={studentId},s.studentId={studentId},s.schoolNum={schoolNum},s.classNum={classNum},s.examinationRoomNum={examinationRoomNum} where s.regId={regId}", StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) this.studentId).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("examinationRoomNum", (Object) this.examRoomNum).put("regId", (Object) this.regid));
        }
    }

    private Map<String, Map<String, Object>> getObjectiveQuestionImage() {
        Object[] args = {this.regid, Integer.valueOf(this.pageNo)};
        Map<String, Map<String, Object>> map = this.dao2.query2Map("SELECT o.questionNum,i.img,o.id from objectivescore o LEFT JOIN questionimage i on o.id=i.scoreId where o.regId=? and o.page=?", "questionNum", args);
        return map;
    }

    private Map<String, Map<String, Object>> getSubjectiveQuestionImage() {
        Map args = new HashMap();
        args.put("regid", this.regid);
        args.put("pageNo", Integer.valueOf(this.pageNo));
        Map<String, Map<String, Object>> map = this.dao2._query2Map("SELECT o.questionNum,i.img,o.id,o.continued from score o LEFT JOIN questionimage i on o.id=i.scoreId where o.regId={regid} and o.page={pageNo}", "questionNum", args);
        return map;
    }

    private String dbSelectDefineId(CsDefine define) {
        String choosename = "";
        if (define.ifChooseSon()) {
            choosename = define.getParent().getQuestionNum();
        } else if (define.ifChooseGrandson()) {
            choosename = define.getParent().getParent().getQuestionNum();
        }
        Map args = new HashMap();
        args.put("choosename", choosename);
        args.put(Const.EXPORTREPORT_studentId, this.studentId);
        args.put("paperNum", this.paperNum);
        String defineId = this.dao2._queryStr("SELECT c.questionNum from choosenamerecord c left JOIN regexaminee r on c.regId=r.id WHERE c.choosename={choosename} and r.studentId={studentId} and r.examPaperNum={paperNum}", args);
        if (CsUtils.IsNullOrEmpty(defineId)) {
            return null;
        }
        return defineId;
    }

    private void DoChoose(ClipRectWrapperCollection clipRectWrapperCollection) {
        Object dbDefineId;
        List<ClipRectWrapper> chooseClipRectWrapperList = clipRectWrapperCollection.getChoose();
        if (chooseClipRectWrapperList == null || chooseClipRectWrapperList.size() == 0) {
            return;
        }
        for (ClipRectWrapper c : chooseClipRectWrapperList) {
            ClipRect rect = c.getRect();
            CsDefine define = c.getDefine();
            String path = GetNewFileName(2);
            this.imageBytesList.add(new Object[]{path, rect.getImage()});
            String regStr = rect.getResult().replaceAll("[a-zA-Z]+", "");
            int selectIndex = rect.getAnswerIndex();
            CsDefine selectCsDefine = define.getChildrens().get(selectIndex);
            String choosename = selectCsDefine.getChoosename();
            String defineId = String.valueOf(selectCsDefine.getId());
            int max = rect.max();
            Map args = new HashMap();
            args.put("regid", this.regid);
            args.put("choosename", choosename);
            Object oid = this.dao2._queryObject("SELECT id from choosenamerecord WHERE regId={regid} and choosename={choosename}", args);
            if (oid == null) {
                if (selectCsDefine.HasChildren()) {
                    StringJoiner sj = new StringJoiner(Const.STRING_SEPERATOR);
                    selectCsDefine.getChildrens().forEach(grandson -> {
                        String grandsonIds = String.join(Const.STRING_SEPERATOR, (Iterable<? extends CharSequence>) grandson.getItems().stream().map(sd -> {
                            return String.valueOf(sd.getId());
                        }).collect(Collectors.toList()));
                        sj.add(grandsonIds);
                    });
                    String ids = sj.toString();
                    Map args0 = new HashMap();
                    args0.put("regid", this.regid);
                    args0.put("ids", ids);
                    dbDefineId = this.dao2._queryObject("select questionNum from score where regId={regid} and questionNum in ({ids[]}) limit 1", args0);
                    if (dbDefineId != null) {
                        boolean flag = false;
                        for (CsDefine son : selectCsDefine.getItems()) {
                            Iterator<CsDefine> it = son.getChildrens().iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                CsDefine grandson2 = it.next();
                                if (grandson2.getId() == Convert.toLong(dbDefineId).longValue()) {
                                    dbDefineId = Long.valueOf(son.getId());
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag) {
                                break;
                            }
                        }
                    }
                } else {
                    String ids2 = String.join(Const.STRING_SEPERATOR, (Iterable<? extends CharSequence>) selectCsDefine.getItems().stream().map(sd -> {
                        return String.valueOf(sd.getId());
                    }).collect(Collectors.toList()));
                    Map args1 = new HashMap();
                    args1.put("regid", this.regid);
                    args1.put("ids", ids2);
                    dbDefineId = this.dao2._queryObject("select questionNum from score where regId={regid} and questionNum in ({ids[]}) limit 1", args1);
                }
                if (dbDefineId != null) {
                    defineId = String.valueOf(dbDefineId);
                }
                String id = GUID.getGUIDStr();
                this.dao2.insert("choosenamerecord", new String[]{"id", "regId", "choosename", "questionNum", "regStr", "max", "isModify", "insertDate", "insertUser"}, new Object[]{id, this.regid, choosename, defineId, regStr, Integer.valueOf(max), "0", this.time, this.username});
                this.questionImageList.add(new Object[]{Long.valueOf(GUID.getGUID()), id, path, Integer.valueOf(this.pageNo), this.time, Integer.valueOf(this.imgpathId), 0, 0, 0, 0, 0, 0, 0, 0, null, null});
            }
        }
    }

    public List<Object> getInsertUserList(List<Object> userNumList, List<Object> taskInsertUserList) {
        List<Object> list = new ArrayList<>(taskInsertUserList);
        List<Object> newlist = new ArrayList<>();
        for (int i = 0; i < taskInsertUserList.size(); i++) {
            Object o = taskInsertUserList.get(i);
            if (userNumList.contains(o)) {
                int index = list.indexOf(o);
                list.remove(index);
            }
        }
        for (int i2 = 0; i2 < 3; i2++) {
            if (list.size() > i2) {
                newlist.add(list.get(i2));
            } else {
                newlist.add("-1");
            }
        }
        return newlist;
    }

    public boolean InsertCorrectStatus(String examRoomNum) {
        Object o = this.dao2._queryObject("SELECT id from correctstatus where examNum={examNum} and subjectNum={subjectNum} and  gradeNum={gradeNum} and testingCentreId={testingCentreId}" + (null == examRoomNum ? "" : " and  examnitionRoom={examRoomNum}") + " limit 0,1", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) this.examNum).put(Const.EXPORTREPORT_subjectNum, (Object) this.subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) this.gradeNum).put("testingCentreId", (Object) this.testingCentreId).put("examRoomNum", (Object) examRoomNum));
        if (o == null || o.toString().equals("")) {
            String[] fileds = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_subjectNum, Const.EXPORTREPORT_gradeNum, "examnitionRoom", "examPaperNum", "testingCentreId", "insertUser", "insertDate", Const.CORRECT_SCORECORRECT, Const.CORRECT_NUMCORRECT, Const.CORRECT_APPENDCORRECT, Const.CORRECT_NUMERRORCORRECT};
            Object[] values = {this.examNum, this.subjectNum, this.gradeNum, examRoomNum, this.paperNum, this.testingCentreId, this.username, this.time, "0", "0", "0", "0"};
            this.dao2.insert("correctstatus", fileds, values);
        } else {
            this.dao2._execute("update correctstatus set status={status} , appendStatus={appendStatus},numStatus={numStatus},numErrorStatus={numErrorStatus} where id={o}", StreamMap.create().put(Const.CORRECT_SCORECORRECT, (Object) "0").put(Const.CORRECT_APPENDCORRECT, (Object) "0").put(Const.CORRECT_NUMCORRECT, (Object) "0").put(Const.CORRECT_NUMERRORCORRECT, (Object) "0").put("o", o));
        }
        updateUploadpicrecordDoneStatus();
        return true;
    }

    private void updateUploadpicrecordDoneStatus() {
        this.dao2._execute("update uploadpicrecord set done=1,doneTime=SEC_TO_TIME(TIMESTAMPDIFF(SECOND ,startTime,'" + DateUtil.getCurrentTime() + "')),doUser={username} WHERE batchNum={batch}", StreamMap.create().put("username", (Object) this.username).put("batch", (Object) this.batch));
    }

    private void updateUploadpicrecordStartStatus() {
        this.dao2._execute("update uploadpicrecord set startTime={time},doUser={username} WHERE batchNum={batch}", StreamMap.create().put("time", (Object) this.time).put("username", (Object) this.username).put("batch", (Object) this.batch));
    }

    /* JADX WARN: Type inference failed for: r0v37, types: [java.lang.Object[], java.lang.Object[][]] */
    private void batchInsert() {
        if (this.objectiveScoreList.size() > 0) {
            String[] fileds = {"id", "regId", "answer", "questionNum", "questionScore", "regScore", "regResult", Const.EXPORTREPORT_studentId, "examPaperNum", Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_classNum, Const.EXPORTREPORT_gradeNum, "insertUser", "insertDate", "examinationRoomNum", "page", "isException", "isAppend", "isModify", "batch", "description", "testingCentreId", "regMax", "regMinToMax"};
            this.dao2.batchInsert("Objectivescore", fileds, this.objectiveScoreList);
        }
        if (this.objOptionList.size() > 0) {
            String[] fileds2 = {"id", "examPaperNum", Const.EXPORTREPORT_gradeNum, "examinationRoomNum", "regId", "questionNum", "scoreId", "imgpath", "img", "item", License.VALUE, "isModify", "insertDate", "selected", "testingCentreId"};
            this.dao2.batchInsert("objitem", fileds2, this.objOptionList);
        }
        if (this.scoreList.size() > 0) {
            String[] fileds3 = {"id", "regid", "questionNum", "questionScore", "regScore", Const.EXPORTREPORT_studentId, "examPaperNum", Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_classNum, Const.EXPORTREPORT_gradeNum, "insertUser", "insertDate", "examinationRoomNum", "page", "isException", "isAppend", "isModify", "continued", "testingCentreId"};
            this.dao2.batchInsert("score", fileds3, this.scoreList);
        }
        if (this.scoringPointList.size() > 0) {
            ?? r0 = new Object[this.scoringPointList.size()];
            for (int i = 0; i < this.scoringPointList.size(); i++) {
                r0[i] = this.scoringPointList.get(i);
            }
            this.dao2.batchExecute("update questionimage set scoringPoint=? where scoreId=?", r0);
        }
        if (this.scoreImageList.size() > 0) {
            String[] fileds4 = {"scoreId", "scoreImg", "insertDate", "imgpath"};
            this.dao2.batchInsert("scoreImage", fileds4, this.scoreImageList);
        }
        if (this.taskList.size() > 0) {
            String[] fileds5 = {"id", Const.EXPORTREPORT_studentId, "scoreId", "examPaperNum", "questionNum", "isException", "isDelete", Const.CORRECT_SCORECORRECT, "groupNum", "insertUser", "insertDate", "updateUser", "updateTime", "rownum", "userNum", "testingCentreId", "questionScore"};
            this.dao2.batchInsert("task", fileds5, this.taskList);
        }
        if (this.questionImageList.size() > 0) {
            String[] fileds6 = {"id", "scoreId", "Img", "page", "insertDate", "imgpath", "questionX", "questionY", "questionW", "questionH", "scoreX", "scoreY", "scoreW", "scoreH", "multiRects", "scoringPoint"};
            this.dao2.batchInsert("questionimage", fileds6, this.questionImageList);
        }
    }

    private void UpdateChooseQuestion2(String studentid) {
        this.dao2._execute("call updatechoosequestionbystudentid({paperNum},{studentid})", StreamMap.create().put("paperNum", (Object) this.paperNum).put("studentid", (Object) studentid));
    }

    public void insertBig2StudentImg(String regid) throws Throwable {
        String path = GetNewFileName(6);
        String[] fileds = {"regid", "img", "insertDate", "imgpath"};
        Object[] values = {regid, path, this.time, Integer.valueOf(this.imgpathId)};
        ClipRect rect = this.picInfoMap.get(ClipRectType.BigImg);
        if (!this.isBuLu) {
            this.dao2.insert("studentPaperImage", fileds, values);
            CsUtils.writeByteArrayToFile(this.location, path, rect.getImage());
            if (this.picInfoMap.containsKey(ClipRectType.SrcBigImg)) {
                String srcFileName = path.substring(0, path.lastIndexOf(46)) + "_src" + path.substring(path.lastIndexOf(46));
                ClipRect srcRect = this.picInfoMap.get(ClipRectType.SrcBigImg);
                CsUtils.writeByteArrayToFile(this.location, srcFileName, srcRect.getImage());
                return;
            }
            return;
        }
        Object img = this.dao2._queryObject("SELECT img from studentpaperimage WHERE regId={regid}", StreamMap.create().put("regid", (Object) regid));
        if (img != null && !img.toString().equals("")) {
            path = img.toString();
        } else {
            this.dao2.insert("studentPaperImage", fileds, values);
        }
        CsUtils.writeByteArrayToFile(this.location, path, rect.getImage());
    }

    public void insertRegResult(String classNum, String cardNum, String examineeNum, String schoolNum) {
        if (CsUtils.IsNullOrEmpty(this.studentId)) {
            this.studentId = GUID.getGUIDStr();
        }
        if (CsUtils.IsNullOrEmpty(cardNum)) {
            cardNum = null;
        }
        String[] fileds4 = {"id", "examPaperNum", Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_studentId, "examinationRoomNum", "page", "insertUser", "insertDate", "cNum", "type", Const.EXPORTREPORT_classNum, "batch", "scannum", "isModify", "scantime", "testingCentreId", "clipIntoDbId", "uploadScannerNum", "examineeNum"};
        Object[] values4 = {this.regid, this.paperNum, schoolNum, this.studentId, this.examRoomNum, Integer.valueOf(this.pageNo), this.username, this.time, this.groupNum, this.type, classNum, this.batch, cardNum, "F", this.scantime, this.testingCentreId, this.clipIntoDbId, this.uploadScannerNum, cardNum};
        this.dao2.insert("regexaminee", fileds4, values4);
        this.dao2.insert("scanrecord", fileds4, values4);
    }

    public void UpdateRegResult(String classNum, String scannum, String examineeNum, String schoolNum) {
        String scannum0 = CsUtils.IsNullOrEmpty(scannum) ? null : scannum;
        Map<String, Object> map = StreamMap.create().put("page", (Object) Integer.valueOf(this.pageNo)).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put(Const.EXPORTREPORT_studentId, (Object) this.studentId).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("examinationRoomNum", (Object) this.examRoomNum).put("scannum", (Object) scannum0).put("examineeNum", (Object) examineeNum).put("regid", (Object) this.regid);
        this.dao2._execute(StrUtil.format("UPDATE {} SET page={page},schoolNum={schoolNum},studentId={studentId},classNum={classNum},examinationRoomNum={examinationRoomNum} , scannum={examineeNum} , examineeNum={examineeNum} WHERE id={regid}", new Object[]{"regexaminee"}), map);
        this.dao2._execute(StrUtil.format("UPDATE {} SET page={page},schoolNum={schoolNum},studentId={studentId},classNum={classNum},examinationRoomNum={examinationRoomNum} , scannum={examineeNum} , examineeNum={examineeNum} WHERE id={regid}", new Object[]{"scanrecord"}), map);
    }

    public void insertBigErrorImg(String regid, String examRoomNum) {
        if (!this.isBuLu) {
            String[] fileds = {"regid", "examPaperNum", Const.EXPORTREPORT_schoolNum, "examinationRoomNum", "insertUser", "insertDate", "isDelete", Const.CORRECT_SCORECORRECT, "stat", "testingCentreId"};
            Object[] values = {regid, this.paperNum, "0", examRoomNum, this.username, this.time, "F", "0", 0, this.testingCentreId};
            this.dao2.insert("cantrecognized", fileds, values);
        }
    }

    private String[] DoCardNo() throws Throwable {
        String path = GetNewFileName(1);
        CsStudent csStudent = new CsStudent(path, this.scanType, this.examRoomNum);
        if (this.existStuInfo) {
            ClipRect clipRect = this.picInfoMap.get(ClipRectType.CardNo);
            CsUtils.writeByteArrayToFile(this.location, path, clipRect.getImage());
            fillCsStudent(csStudent, clipRect);
            insertExaminationNumImg(path);
            this.studentId = String.valueOf(csStudent.getStudentId());
            if (csStudent.isError()) {
                insertExamineenumerror(csStudent);
            } else {
                csStudent.setExamRoomNum(getExamRoomNum());
                csStudent.setClassNum(getClassNum());
            }
            insertOrUpdateRegResult(csStudent);
            updateBrother(csStudent);
        } else {
            List<?> _queryBeanList = this.dao2._queryBeanList("SELECT r.studentId,r.scannum cardNo,r.schoolNum,r.classNum,r.examinationRoomNum examRoomNum,er.errorType,img.img path,r.examineeNum from regexaminee  r  LEFT JOIN examineenumerror er on r.id = er.regId LEFT JOIN examinationnumimg img on img.regId = r.id WHERE r.cNum={groupNum} and r.type!={type} order by r.page", CsStudent.class, StreamMap.create().put("groupNum", (Object) this.groupNum).put("type", (Object) this.type));
            if (CsUtils.IsNullOrEmpty(_queryBeanList)) {
                csStudent.setInvalid();
            } else if (CsUtils.IsNullOrEmpty((Collection) _queryBeanList.stream().filter(s -> {
                return !CsUtils.IsNullOrEmpty(s.getPath());
            }).collect(Collectors.toList()))) {
                csStudent = (CsStudent) _queryBeanList.get(0);
                csStudent.setError(true);
                csStudent.setErrorType("0");
                this.examRoomNum = String.valueOf(csStudent.getExamRoomNum());
                this.studentId = String.valueOf(csStudent.getStudentId());
            } else {
                csStudent = (CsStudent) _queryBeanList.get(0);
                this.examRoomNum = String.valueOf(csStudent.getExamRoomNum());
                this.studentId = String.valueOf(csStudent.getStudentId());
            }
            if (!CsUtils.IsNullOrEmpty(csStudent.getErrorType())) {
                insertExamineenumerror(csStudent);
            }
            String cardNoPath = csStudent.getPath();
            CsUtils.copyFile(this.location, cardNoPath, path);
            insertExaminationNumImg(path);
            insertOrUpdateRegResult(csStudent);
        }
        String[] strArr = new String[5];
        strArr[0] = this.studentId;
        strArr[1] = csStudent.isError() ? "1" : "0";
        strArr[2] = csStudent.getClassNum();
        strArr[3] = this.examRoomNum;
        strArr[4] = csStudent.getSchoolNum();
        return strArr;
    }

    private void insertOrUpdateRegResult(CsStudent csStudent) {
        if (!this.isBuLu) {
            insertRegResult(csStudent.getClassNum(), csStudent.getCardNo(), csStudent.getExamineeNum(), csStudent.getSchoolNum());
        } else {
            UpdateRegResult(csStudent.getClassNum(), csStudent.getCardNo(), csStudent.getExamineeNum(), csStudent.getSchoolNum());
        }
    }

    private RegExaminee getRegExamineeByBrother() {
        if (!this.existStuInfo) {
            RegExaminee reg = (RegExaminee) this.dao2._queryBean("SELECT * from regexaminee WHERE cNum={groupNum} and type!={type} ORDER BY page limit 1", RegExaminee.class, StreamMap.create().put("groupNum", (Object) this.groupNum).put("type", (Object) this.type));
            return reg;
        }
        return null;
    }

    private void insertExaminationNumImg(String path) {
        String[] fileds3 = {"regId", "img", "insertDate", "imgpath"};
        Object[] values3 = {this.regid, path, this.time, Integer.valueOf(this.imgpathId)};
        this.dao2.insert("examinationNumImg", fileds3, values3);
    }

    private void updateBrother(CsStudent csStudent) {
        List<?> _queryBeanList = this.dao2._queryBeanList("SELECT id regId,cNum,batch,scannum cardNo,schoolNum,testingCentreId,page,examineeNum from regexaminee WHERE cNum={groupNum} and id!={regid}", Corner.class, StreamMap.create().put("groupNum", (Object) this.groupNum).put("regid", (Object) this.regid));
        if (!CsUtils.IsNullOrEmpty(_queryBeanList)) {
            _queryBeanList.forEach(corner -> {
                updateBrotherCardNoError(corner, csStudent);
                updateStudentIdByReg(corner, csStudent, false);
            });
        }
    }

    private void updateBrotherCardNoError(Corner corner, CsStudent csStudent) {
        String regId = corner.getRegId();
        Object examineenumerrorId = this.dao2._queryObject("select id from examineenumerror where regId={regId}", StreamMap.create().put("regId", (Object) regId));
        if (examineenumerrorId != null) {
            Map args = StreamMap.create().put("examineenumerrorId", examineenumerrorId).put(Const.EXPORTREPORT_studentId, (Object) this.studentId).put(Const.EXPORTREPORT_schoolNum, (Object) csStudent.getSchoolNum()).put("errorType", (Object) csStudent.getErrorType()).put("cardNo", (Object) csStudent.getCardNo()).put("regId", (Object) regId);
            if (csStudent.getErrorType() == null) {
                this.dao2._execute("delete from examineenumerror where id = {examineenumerrorId}", args);
                return;
            } else {
                this.dao2._execute("update examineenumerror set studentId={studentId}, schoolNum={schoolNum},errorType={errorType},examineeNum={cardNo} where regId={regId} ", args);
                return;
            }
        }
        if (csStudent.getErrorType() != null) {
            insertRepeatCardNoError(corner, csStudent);
        }
    }

    private void updateStudentIdByReg(Corner corner, CsStudent csStudent, boolean doRepeat) {
        String regId = corner.getRegId();
        int pageNo = corner.getPage();
        long studentId = csStudent.getStudentId();
        String classNum = csStudent.getClassNum();
        String cardNo = csStudent.getCardNo();
        String examineeNum = csStudent.getExamineeNum();
        String schoolNum = csStudent.getSchoolNum();
        String path = csStudent.getPath();
        csStudent.getErrorType();
        long examRoomNum = csStudent.getExamRoomNum();
        if (StrUtil.isEmpty(cardNo)) {
            cardNo = null;
        }
        if (doRepeat) {
            this.dao2.execute("UPDATE regexaminee SET studentId=?,examinationRoomNum=?,classNum=?,schoolNum=?,scannum=? WHERE id=?", new Object[]{Long.valueOf(studentId), Long.valueOf(examRoomNum), classNum, schoolNum, cardNo, regId});
            this.dao2.execute("UPDATE scanrecord SET studentId=?,examinationRoomNum=?,classNum=?,schoolNum=?,scannum=?  WHERE id=?", new Object[]{Long.valueOf(studentId), Long.valueOf(examRoomNum), classNum, schoolNum, cardNo, regId});
        } else {
            this.dao2.execute("UPDATE regexaminee SET studentId=?,examinationRoomNum=?,classNum=?,schoolNum=?,scannum=?,examineeNum=?  WHERE id=?", new Object[]{Long.valueOf(studentId), Long.valueOf(examRoomNum), classNum, schoolNum, cardNo, examineeNum, regId});
            this.dao2.execute("UPDATE scanrecord SET studentId=?,examinationRoomNum=?,classNum=?,schoolNum=?,scannum=?,examineeNum=?  WHERE id=?", new Object[]{Long.valueOf(studentId), Long.valueOf(examRoomNum), classNum, schoolNum, cardNo, examineeNum, regId});
        }
        Map args = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) Long.valueOf(studentId)).put("examRoomNum", (Object) Long.valueOf(examRoomNum)).put(Const.EXPORTREPORT_classNum, (Object) classNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("regId", (Object) regId);
        this.dao2._execute("UPDATE score SET studentId={studentId},examinationRoomNum={examRoomNum},classNum={classNum},schoolNum={schoolNum}  WHERE regId={regId}", args);
        this.dao2._execute("UPDATE objectivescore SET studentId={studentId},examinationRoomNum={examRoomNum},classNum={classNum},schoolNum={schoolNum}  WHERE regId={regId}", args);
        this.dao2._execute("UPDATE objitem SET examinationRoomNum={examRoomNum} where regId={regId}", args);
        this.dao2._execute("UPDATE cantrecognized  SET examinationRoomNum={examRoomNum},schoolNum={schoolNum}  WHERE regId={regId}", args);
        if (pageNo == 1) {
            this.dao2._execute("UPDATE illegal SET studentId={studentId},examinationRoomNum={examRoomNum},schoolNum={schoolNum} WHERE regId={regId}", args);
        }
        List<Object> list = this.dao2._queryColList("select id from score where regid = {regId}", args);
        List<RowArg> rowArgList = new ArrayList<>();
        if (list != null) {
            for (int k = 0; k < list.size(); k++) {
                Map args0 = new HashMap();
                args0.put(Const.EXPORTREPORT_studentId, Long.valueOf(studentId));
                args0.put(Const.EXPORTREPORT_classNum, classNum);
                args0.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                args0.put("scoreid", list.get(k));
                rowArgList.add(new RowArg("UPDATE tag SET studentId={studentId},classNum={classNum},schoolNum={schoolNum} where scoreid={scoreid}", args0));
                rowArgList.add(new RowArg("UPDATE task SET studentId={studentId} where scoreid={scoreid}", args0));
            }
        }
        if (!doRepeat) {
            String path0 = this.dao2._queryStr("select img from examinationnumimg where regId={regId}", StreamMap.create().put("regId", (Object) regId));
            if (!CsUtils.IsNullOrEmpty(path0) && !CsUtils.IsNullOrEmpty(path) && !path.equals(path0)) {
                CsUtils.copyFile(this.location, path, path0);
            }
        }
        this.dao2._batchExecute(rowArgList);
    }

    private void insertExamineenumerror(CsStudent csStudent) {
        String[] fileds1 = {"regId", "examPaperNum", Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_studentId, "errorType", "examinationRoomNum", "examineeNum", "groupNum", "page", "insertUser", "insertDate", "isDelete", "batch", "testingCentreId"};
        Object[] values1 = {this.regid, this.paperNum, csStudent.getSchoolNum(), this.studentId, csStudent.getErrorType(), Long.valueOf(csStudent.getExamRoomNum()), csStudent.getCardNo(), this.groupNum, Integer.valueOf(this.pageNo), this.username, this.time, "F", this.batch, this.testingCentreId};
        this.dao2.insert("examineenumerror", fileds1, values1);
    }

    private long getExamRoomNum() {
        if (!this.scanType) {
            Object o = this.dao2._queryObject("select examinationRoomNum from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and  studentid={studentId} limit 1", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) this.examNum).put(Const.EXPORTREPORT_gradeNum, (Object) this.gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) this.subjectNum).put(Const.EXPORTREPORT_studentId, (Object) this.studentId));
            this.examRoomNum = null == o ? "-1" : o.toString();
        }
        return Long.valueOf(this.examRoomNum).longValue();
    }

    private String getClassNum() {
        Object classNumObj = this.dao2._queryObject("SELECT classNum from student where id={studentId} limit 1", StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) this.studentId));
        if (null != classNumObj) {
            return classNumObj.toString();
        }
        return "-1";
    }

    private Map<String, Object> getCornerMap() {
        return (Map) JSONObject.parseObject(this.cornerClipRect.getResult(), JsonType.Map1_StringObject, new Feature[0]);
    }

    private int getCornerRatio() {
        Map<String, Object> map = getCornerMap();
        int blackRatio = CsUtils.getInt(map.get("blackRatio"));
        return blackRatio;
    }

    private void fillCsStudent(CsStudent csStudent, ClipRect clipRect) {
        String cardNo = clipRect.getAnswer().replace("CardNo", "").replace("NotID", "");
        csStudent.setCardNo(cardNo);
        csStudent.setExamineeNum(cardNo);
        boolean needCheckCardNo = true;
        if (this.isBuLu) {
            Object[] stuArray = findStuInfoByRegId();
            if (!CsUtils.IsNullOrEmpty(stuArray)) {
                csStudent.setStudentId(((Long) stuArray[0]).longValue());
                csStudent.setCardNo(stuArray[1].toString());
                csStudent.setSchoolNum(stuArray[2].toString());
                csStudent.resetCorrect();
                this.studentId = String.valueOf(csStudent.getStudentId());
                needCheckCardNo = false;
            } else {
                csStudent.setInvalid();
                needCheckCardNo = true;
            }
        }
        if (needCheckCardNo) {
            if (CsUtils.IsNullOrEmpty(cardNo)) {
                csStudent.setInvalid();
                return;
            }
            Object[] stuArray2 = findStuInfoByCardNo(cardNo);
            if (!CsUtils.IsNullOrEmpty(stuArray2)) {
                csStudent.setStudentId(((Long) stuArray2[0]).longValue());
                csStudent.setSchoolNum(stuArray2[1].toString());
                csStudent.setCardNo(cardNo);
                csStudent.resetCorrect();
                this.studentId = String.valueOf(csStudent.getStudentId());
                doRepeat(csStudent);
                return;
            }
            csStudent.setInvalid();
        }
    }

    private void doRepeat(CsStudent csStudent) {
        if (existStu()) {
            boolean existScored = existScored(this.paperNum, String.valueOf(csStudent.getStudentId()));
            if (existScored) {
                csStudent.setRepeat();
                return;
            }
            int blackRatio = getCornerRatio();
            List<Corner> cornerList = getExistCorner();
            int existBlackRatio = getExistCornerBlackRatio(cornerList);
            if (existBlackRatio > blackRatio) {
                CsStudent csStudent0 = new CsStudent(csStudent.getPath(), this.scanType, this.examRoomNum).setRepeat();
                csStudent0.setCardNo(csStudent.getCardNo());
                cornerList.forEach(corner -> {
                    insertRepeatCardNoError(corner, csStudent0);
                    updateStudentIdByReg(corner, csStudent0, true);
                });
                csStudent.resetCorrect();
                return;
            }
            csStudent.setRepeat();
        }
    }

    private boolean existScored(String examPaperNum, String studentId) {
        Object o = this.dao2._queryObject("SELECT id from task where exampaperNum={examPaperNum} and studentId={studentId} and `status`='T'", StreamMap.create().put("examPaperNum", (Object) examPaperNum).put(Const.EXPORTREPORT_studentId, (Object) studentId));
        return o != null;
    }

    private void insertRepeatCardNoError(Corner corner, CsStudent errorCsStudent) {
        String[] fileds1 = {"regId", "examPaperNum", Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_studentId, "errorType", "examinationRoomNum", "examineeNum", "groupNum", "page", "insertUser", "insertDate", "isDelete", "batch", "testingCentreId"};
        Object[] values1 = {corner.getRegId(), this.paperNum, errorCsStudent.getSchoolNum(), Long.valueOf(errorCsStudent.getStudentId()), errorCsStudent.getErrorType(), Long.valueOf(errorCsStudent.getExamRoomNum()), errorCsStudent.getCardNo(), corner.getcNum(), Integer.valueOf(corner.getPage()), Long.valueOf(corner.getInsertUser()), DateUtil.getCurrentTime(), "F", corner.getBatch(), Long.valueOf(corner.getTestingCentreId())};
        this.dao2.insert("examineenumerror", fileds1, values1);
    }

    private Object[] findStuInfoByRegId() {
        Object[] array = this.dao2._queryArray("SELECT e.studentId , e.examineeNum , r.schoolNum from examinationnum e  LEFT JOIN exampaper p on e.examNum=p.examNum LEFT JOIN regexaminee r on e.studentId=r.studentId and p.examPaperNum = r.examPaperNum  where r.id={regid}", StreamMap.create().put("regid", (Object) this.regid));
        return array;
    }

    private Object[] findStuInfoByCardNo(String cardNo) {
        String examRoomInfo = "";
        if (this.scanType && StrUtil.isNotEmpty(this.examRoomNum) && !"-1".equals(this.examRoomNum) && !"0".equals(this.examRoomNum)) {
            examRoomInfo = " and  examinationRoomNum={examRoomNum}";
        }
        String sql = " SELECT studentId,schoolNum from examinationnum where examineeNum={cardNo} and  examNum={examNum} and testingCentreId={testingCentreId} and  gradeNum ={gradeNum} and subjectNum={subjectNum}" + examRoomInfo + "  LIMIT 0,1";
        Object[] array = this.dao2._queryArray(sql, StreamMap.create().put("examRoomNum", (Object) this.examRoomNum).put("cardNo", (Object) cardNo).put(Const.EXPORTREPORT_examNum, (Object) this.examNum).put("testingCentreId", (Object) this.testingCentreId).put(Const.EXPORTREPORT_gradeNum, (Object) this.gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) this.subjectNum));
        return array;
    }

    private List<Corner> getExistCorner() {
        return this.dao2._queryBeanList("SELECT r.id regId,n.blackRatio,r.cNum,r.batch,r.scannum cardNo,r.schoolNum,r.testingCentreId,r.page from regexaminee r left JOIN corner n on r.id = n.regid where r.examPaperNum={paperNum} and r.studentId={studentId} and r.cNum!={groupNum}", Corner.class, StreamMap.create().put("paperNum", (Object) this.paperNum).put(Const.EXPORTREPORT_studentId, (Object) this.studentId).put("groupNum", (Object) this.groupNum));
    }

    private int getExistCornerBlackRatio(List<Corner> cornerList) {
        if (CsUtils.IsNullOrEmpty(cornerList)) {
            return 0;
        }
        return ((Integer) cornerList.stream().map(c -> {
            return Integer.valueOf(c.getBlackRatio());
        }).max((v0, v1) -> {
            return Integer.compare(v0, v1);
        }).get()).intValue();
    }

    private boolean existStu() {
        Object stu = this.dao2._queryObject("SELECT studentId from regexaminee where examPaperNum={paperNum} and studentId={studentId}  and cnum !={groupNum} and page = {pageNo} LIMIT 0,1", StreamMap.create().put("paperNum", (Object) this.paperNum).put(Const.EXPORTREPORT_studentId, (Object) this.studentId).put("groupNum", (Object) this.groupNum).put("pageNo", (Object) Integer.valueOf(this.pageNo)));
        return stu != null;
    }

    public List<Map<String, Object>> getChildQuestionDefineInfo(String exampaperNum, String p_questionid, String questionType) {
        return this.dao2._queryMapList("select * from subdefine where pid ={p_questionid} and questionType={questionType} ORDER BY orderNum ", TypeEnum.StringObject, StreamMap.create().put("p_questionid", (Object) p_questionid).put("questionType", (Object) questionType));
    }

    public void InsertCorner() {
        Map<String, Object> map = getCornerMap();
        this.dao2.insert("corner", new String[]{"regid", "blackRatio", "blackBase", "isBlack", "whiteRatio", "whiteBase", "isWhite", "insertUser", "insertDate"}, new Object[]{this.regid, map.get("blackRatio"), map.get("blackBase"), map.get("isBlack"), map.get("whiteRatio"), map.get("whiteBase"), map.get("isWhite"), map.get("insertUser"), map.get("insertDate")});
    }

    private String GetNewFileName(int picType) {
        return this.ftpPath.replace("|", "/" + picType + "/").replace("\\\\", "/") + "/" + GUID.getGUID() + ".png";
    }

    private void UpdateObjectiveScore(String AB, String cNum) {
        LinkedHashMap<String, LinkedHashMap<String, Object>> oldmap;
        List<CsDefine> ObjectiveList = new ArrayList<>();
        for (CsDefine define : this.defineInfoMap.values()) {
            if (define.getQuestionType().equals("0") && define.getPage() > 1) {
                ObjectiveList.add(define);
            }
        }
        if (ObjectiveList.size() <= 0 || (oldmap = this.dao2._query2OrderMap("SELECT s.id,s.questionNum,s.questionScore,s.answer from objectivescore s LEFT JOIN regexaminee r on s.regId=r.id WHERE r.cNum={cNum} and r.page>1", "questionNum", StreamMap.create().put("cNum", (Object) cNum))) == null) {
            return;
        }
        List<RowArg> rowArgList = new ArrayList<>();
        for (int i = 0; i < ObjectiveList.size(); i++) {
            CsDefine define2 = ObjectiveList.get(i);
            String qnum = String.valueOf(define2.getId());
            if (oldmap.containsKey(qnum)) {
                String studentAnswer = CsUtils.getString(oldmap.get(qnum).get("answer"));
                String id = CsUtils.getString(oldmap.get(qnum).get("id"));
                Map<String, Object> map = BeanUtil.beanToMap(define2, new String[0]);
                ConvertMap(map, AB);
                double regScore = Util.suitAllObjSingleJudge(studentAnswer, map);
                rowArgList.add(new RowArg("update objectivescore set questionScore={regScore} where id = {id}", StreamMap.create().put("regScore", (Object) Double.valueOf(regScore)).put("id", (Object) id)));
            }
        }
        if (rowArgList.size() > 0) {
            this.dao2._batchExecute(rowArgList);
        }
    }

    private void DeletePageInfo() {
        if (!this.isBuLu) {
            return;
        }
        String regid = CsUtils.getString(this.paperMap.get("regid"));
        int oldPage = CsUtils.getInt(this.paperMap.get("oldPage"));
        if (this.pageNo != oldPage) {
            this.dbutil.DeleteByOneRegid(regid, true, true, false);
            Object otherRegid = this.dao2._queryObject("select id from regexaminee where cNum={groupNum} and id !={regid} and page={pageNo} limit 1", StreamMap.create().put("groupNum", (Object) this.groupNum).put("regid", (Object) regid).put("pageNo", (Object) Integer.valueOf(this.pageNo)));
            if (otherRegid != null) {
                this.dbutil.DeleteByOneRegid(CsUtils.getString(otherRegid), true, true, false);
                this.dao2._execute("update regexaminee set page=0 where id ={otherRegid}", StreamMap.create().put("otherRegid", otherRegid));
                this.dao2.insert("cantrecognized", new String[]{"examPaperNum", "regId", "examinationRoomNum", Const.EXPORTREPORT_schoolNum, Const.CORRECT_SCORECORRECT, "insertUser", "insertDate", "stat", "testingCentreId"}, new Object[]{this.paperNum, otherRegid, this.examRoomNum, "0", "0", this.username, this.time, 0, this.testingCentreId});
                return;
            }
            return;
        }
        this.dbutil.DeleteByOneRegid(regid, false, false, false);
    }

    private void ConvertMap(Map<String, Object> map, String AB) {
        if ("B".equals(AB.toUpperCase())) {
            for (int i = 1; i < 16; i++) {
                if (map.containsKey("one" + i + "b") && map.containsKey("one" + i)) {
                    map.put("one" + i, map.get("one" + i + "b"));
                }
            }
            map.put("answer", map.get("answer_b"));
        }
    }

    public boolean modifyObjectieveSwithAB(String AB, String stuId, String reg_id) {
        if (!this.isBuLu || this.pageNo != 1) {
            return false;
        }
        if (AB.equals("A") || AB.equals("B")) {
            return this.dbutil.modifyObjectieveSwithAB(this.paperNum, stuId, CsUtils.getString(Integer.valueOf(this.pageNo)), reg_id);
        }
        return false;
    }
}
