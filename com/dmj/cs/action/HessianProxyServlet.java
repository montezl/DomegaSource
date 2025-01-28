package com.dmj.cs.action;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.server.HessianServlet;
import com.dmj.cs.bean.CsDefine;
import com.dmj.cs.bean.MissingPaper;
import com.dmj.cs.bean.Question;
import com.dmj.cs.bean.Template;
import com.dmj.cs.bean.TemplateRecord;
import com.dmj.cs.bean.UploadPicFile;
import com.dmj.cs.bean.ZTreeNode;
import com.dmj.cs.service.HessianService;
import com.dmj.cs.serviceimpl.HessianServiceImpl;
import com.dmj.cs.util.CsUtils;
import com.dmj.cs.util.DefineCache;
import com.dmj.cs.util.IntoDbUtil2;
import com.dmj.util.StaticClassResources;
import com.dmj.util.app.EdeiInfo;
import com.dmj.util.config.Configuration;
import com.zht.db.ServiceFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import org.apache.log4j.Logger;

/* loaded from: HessianProxyServlet.class */
public class HessianProxyServlet extends HessianServlet {
    private static final long serialVersionUID = 1;
    private HessianService hessianService = (HessianService) ServiceFactory.getObject(new HessianServiceImpl());
    private final StampedLock templateSl = new StampedLock();
    private final StampedLock templateS2 = new StampedLock();
    private final StampedLock spiritS = new StampedLock();
    private static final Logger log = Logger.getLogger(HessianProxyServlet.class);
    private static volatile ConcurrentHashMap<String, Object> intoDbCNumMap = new ConcurrentHashMap<>();

    public boolean Conect() {
        Object isImageServer = Configuration.getInstance().getIsImageServer();
        Object useImageServer = Configuration.getInstance().getUseImageServer();
        if (useImageServer == null || !useImageServer.toString().equals("0") || isImageServer == null || !isImageServer.toString().equals("1")) {
            return false;
        }
        return this.hessianService.Conect();
    }

    public boolean UpdatePicServerInfo(int[] fileds, String[] values, String user) {
        return this.hessianService.UpdatePicServerInfo(fileds, values, user);
    }

    public Object[] GetLoginId(String username, String password) {
        return this.hessianService.GetLoginId(username, password);
    }

    public List<Object[]> GetNoDoneExamList(String userNum) {
        return this.hessianService.GetNoDoneExamList(userNum);
    }

    public List<Object[]> GetPaperSizeList() {
        return this.hessianService.GetPaperSizeList();
    }

    public Object GetExamPaperNum(String examNum, String gradeNum, String subjectNum) {
        return this.hessianService.GetExamPaperNum(examNum, gradeNum, subjectNum);
    }

    public String GetQuestionDictionaryMap(String paperNum, String key) {
        return this.hessianService.GetQuestionDictionaryMap(paperNum, key);
    }

    public boolean UpdateNotice(String examPaperNum, String examineeInstructions) {
        return this.hessianService.UpdateNotice(examPaperNum, examineeInstructions);
    }

    public List<Object[]> GetGradeList(String examNum, String testingCentreId, int pageDoType, boolean isBl, String loginUser) {
        return this.hessianService.GetGradeList(examNum, testingCentreId, pageDoType, isBl, loginUser);
    }

    public List<Object[]> GetSubjectList(String examNum, String gradeNum, String schoolNum, int pageDoType, boolean isBl, String loginUser) {
        return this.hessianService.GetSubjectList(examNum, gradeNum, schoolNum, pageDoType, isBl, loginUser);
    }

    public List<Object[]> GetSchoolList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.hessianService.GetSchoolList(examNum, gradeNum, subjectNum, testingCentreId);
    }

    public Map<String, Object> GetExamPaperDictionary(String[] fields, String examNum, String gradeNum, String subjectNum, String templateType) {
        return this.hessianService.GetExamPaperDictionary(fields, examNum, gradeNum, subjectNum, templateType);
    }

    public Map<String, Object> GetExamPaperDictionary(String[] fields, String examPaperNum) {
        return this.hessianService.GetExamPaperDictionary(fields, examPaperNum);
    }

    public String GetPaperSizeName(String paperSizeNum) {
        return this.hessianService.GetPaperSizeName(paperSizeNum);
    }

    public Object GetTemplateInfo(String examPaperNum, String abtype) {
        return this.hessianService.GetTemplateInfo(examPaperNum, abtype);
    }

    public String getNotExistTemplateInfo(String examPaperNum, boolean isAb) {
        return this.hessianService.getNotExistTemplateInfo(examPaperNum, isAb);
    }

    public List<Object[]> GetTemplateImage(String examPaperNum, String abtype) {
        return this.hessianService.GetTemplateImage(examPaperNum, abtype);
    }

    public Object GetExamPaperNum(String examNum, String gradeNum, String subjectNum, Boolean isCheckSelfOrOut) {
        return this.hessianService.GetExamPaperNum(examNum, gradeNum, subjectNum, isCheckSelfOrOut);
    }

    public boolean SaveExtTemplate(Template template) throws Throwable {
        long stamp = this.templateS2.writeLock();
        try {
            boolean SaveExtTemplate = this.hessianService.SaveExtTemplate(template);
            this.templateS2.unlockWrite(stamp);
            return SaveExtTemplate;
        } catch (Throwable th) {
            this.templateS2.unlockWrite(stamp);
            throw th;
        }
    }

    public Object GetExamineeNumLength(String examNum, String testingCentreId, String gradeNum, String subjectNum) {
        return this.hessianService.GetExamineeNumLength(examNum, testingCentreId, gradeNum, subjectNum);
    }

    public List<Object[]> GetExtTemplateInfo(String examPaperNum) {
        return this.hessianService.GetExtTemplateInfo(examPaperNum);
    }

    public boolean DelTemplate(String examPaperNum) {
        return this.hessianService.DelTemplate(examPaperNum);
    }

    public boolean IsExistDefineData(String examPaperNum) {
        return this.hessianService.IsExistDefineData(examPaperNum);
    }

    public String GetExamRoomStr(String examRoomNum) {
        return this.hessianService.GetExamRoomStr(examRoomNum);
    }

    public int[] GetStuCountInfo(String scanType, String examNum, String paperNum, String testingCentreId, String gradeNum, String examRoomNum) {
        return this.hessianService.GetStuCountInfo(scanType, examNum, paperNum, testingCentreId, gradeNum, examRoomNum);
    }

    public List<Object[]> GetScanBatchList(String examNum, String gradeNum, String testingCentreId, String subjectNum, String scannerNum, String loginUser) {
        return this.hessianService.GetScanBatchList(examNum, gradeNum, testingCentreId, subjectNum, scannerNum, loginUser);
    }

    public List<Object[]> GetBatchList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.hessianService.GetBatchList(examNum, gradeNum, subjectNum, testingCentreId);
    }

    public Map<String, Object> GetRegCountByScanBatch(String examPaperNum, String testingCentreId) {
        return this.hessianService.GetRegCountByScanBatch(examPaperNum, testingCentreId);
    }

    public boolean AddScanBatch(String examNum, String gradeNum, String testingCentreId, String subjectNum, String scannerNum, String batchNo, String loginUser, String picBatchNum) {
        return this.hessianService.AddScanBatch(examNum, gradeNum, testingCentreId, subjectNum, scannerNum, batchNo, loginUser, picBatchNum);
    }

    public boolean DelScanBatch(String batch) {
        return this.hessianService.DelScanBatch(batch);
    }

    public List<Object[]> GetFinishedExamRoomList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.hessianService.GetFinishedExamRoomList(examNum, gradeNum, subjectNum, testingCentreId);
    }

    public List<Object[]> GetFinishedExamSubjectList(String examNum, String gradeNum, String testingCentreId) {
        return this.hessianService.GetFinishedExamSubjectList(examNum, gradeNum, testingCentreId);
    }

    public List<Object[]> GetUploadList(String examNum, String gradeNum, String subjectNum, String testingCentreId, int count) {
        return this.hessianService.GetUploadList(examNum, gradeNum, subjectNum, testingCentreId, count);
    }

    public Map<String, Object> GetUploadDictionary(String id) {
        return this.hessianService.GetUploadDictionary(id);
    }

    public int GetExamStuCount(String examPaperNum, String testingCentreId) {
        return this.hessianService.GetExamStuCount(examPaperNum, testingCentreId);
    }

    public Map<String, Object> GetImgPathConfigDictionary(String examNum) {
        return this.hessianService.GetImgPathConfigDictionary(examNum);
    }

    public List<Object[]> GetExamRoomList(String examNum, String gradeNum, String testingCentreId, String subjectNum, boolean isBl) {
        return this.hessianService.GetExamRoomList(examNum, gradeNum, testingCentreId, subjectNum, isBl);
    }

    public List<Object[]> GetTestingCenterList(String examNum, String loginUser, boolean isBl) {
        return this.hessianService.GetTestingCenterList(examNum, loginUser, isBl);
    }

    public String GetScannerNum(String batchNo) {
        return this.hessianService.GetScannerNum(batchNo);
    }

    public boolean DeleteByOneRegid(String regid) {
        return this.hessianService.DeleteByOneRegid(regid);
    }

    public boolean DeleteByOneCNum(String CNum) {
        return this.hessianService.DeleteByOneCNum(CNum);
    }

    public Object GetObject(String sql) {
        return this.hessianService.GetObject(sql);
    }

    public List<Object> GetColList(String sql) {
        return this.hessianService.GetColList(sql);
    }

    public boolean Execute(String sql) {
        return this.hessianService.Execute(sql);
    }

    public List<Object> GetScanRecord(String examPaperNum, String testingCentreId, String examRoomNum, String batch, String scanType) {
        return this.hessianService.GetScanRecord(examPaperNum, testingCentreId, examRoomNum, batch, scanType);
    }

    public boolean DelCorrectStatus(String examPaperNum, String testingCentreId, String examRoomNum, String scanType) {
        return this.hessianService.DelCorrectStatus(examPaperNum, testingCentreId, examRoomNum, scanType);
    }

    public String GetStuPaperImgIdList(String examPaperNum, String testingCentreId) {
        return this.hessianService.GetStuPaperImgIdList(examPaperNum, testingCentreId);
    }

    public String GetImage(String id) {
        return this.hessianService.GetImage(id);
    }

    public Object[] GetOneUploadPicRecord(String id) {
        return this.hessianService.GetOneUploadPicRecord(id);
    }

    public boolean UpdateOneUploadPicRecord(String id, String state, String scannerNum, String count, String userId) {
        long stamp = this.spiritS.writeLock();
        try {
            boolean UpdateOneUploadPicRecord = this.hessianService.UpdateOneUploadPicRecord(id, state, scannerNum, count, userId);
            this.spiritS.unlockWrite(stamp);
            return UpdateOneUploadPicRecord;
        } catch (Throwable th) {
            this.spiritS.unlockWrite(stamp);
            throw th;
        }
    }

    public boolean SaveQuestionOrAnswerImage(Question q) {
        return this.hessianService.SaveQuestionOrAnswerImage(q);
    }

    public List<Object[]> GetQuestionListByQuestionAnswer(String examPaperNum) {
        return this.hessianService.GetQuestionListByQuestionAnswer(examPaperNum);
    }

    public List<String> GetFinishedIntoDbQuestionOrAnswerList(String examPaperNum, String table) {
        return this.hessianService.GetFinishedIntoDbQuestionOrAnswerList(examPaperNum, table);
    }

    public String GetMissingStuList(String testingCentreId, String gradeNum, String subjectNum, String examNum, String examRoomNum) {
        return this.hessianService.GetMissingStuList(testingCentreId, gradeNum, subjectNum, examNum, examRoomNum);
    }

    public Map<String, Object> GetOneMissStuImageInfo(String examNum, String gradeNum, String subjectNum, String examRoomNum, String testingCentreId, int curPage) {
        return this.hessianService.GetOneMissStuImageInfo(examNum, gradeNum, subjectNum, examRoomNum, testingCentreId, curPage);
    }

    public String GetStudentId(String testingCentreId, String examNum, String gradeNum, String examineeNum) {
        return this.hessianService.GetStudentId(testingCentreId, examNum, gradeNum, examineeNum);
    }

    public boolean UpDateStudentId(String studentId, String cNum, String regId, String loginUser, String page) {
        return this.hessianService.UpDateStudentId(studentId, cNum, regId, loginUser, page);
    }

    public boolean IsAb(String curPaperNum) {
        return this.hessianService.IsAb(curPaperNum);
    }

    public String GetAbType(String cNum) {
        return this.hessianService.GetAbType(cNum);
    }

    public Object[] GetExamPaperNumInfo(String examPaperNum) {
        return this.hessianService.GetExamPaperNumInfo(examPaperNum);
    }

    public int GetReClipCount(String examPaperNum, String testingCentreId, String examRoomNum) {
        return this.hessianService.GetReClipCount(examPaperNum, testingCentreId, examRoomNum, "");
    }

    public int GetReClipCount(String examPaperNum, String testingCentreId, String examRoomNum, String page) {
        return this.hessianService.GetReClipCount(examPaperNum, testingCentreId, examRoomNum, page);
    }

    public Map<String, Object> GetOrUpdateOneClipRecord(String exampaperNum, String testingCentreId, String examRoomNum, String regid, String type, String id) {
        return this.hessianService.GetOrUpdateOneClipRecord(exampaperNum, testingCentreId, examRoomNum, "", regid, type, id);
    }

    public Map<String, Object> GetOrUpdateOneClipRecord(String exampaperNum, String testingCentreId, String examRoomNum, String page, String regid, String type, String id) {
        return this.hessianService.GetOrUpdateOneClipRecord(exampaperNum, testingCentreId, examRoomNum, page, regid, type, id);
    }

    public String GetCantrecognizedId(String regId) {
        return this.hessianService.GetCantrecognizedId(regId);
    }

    public boolean RecoveryCantrecognized(String gradeNum, String subjectNum, String testingCentreId, String examRoomNum) {
        return this.hessianService.RecoveryCantrecognized(gradeNum, subjectNum, testingCentreId, examRoomNum);
    }

    public String[] SendIntoDb(Map<String, Object> paperMap, String picInfoMapJson) {
        String cNum = CsUtils.getString(paperMap.get("groupNum"));
        Object lock = null;
        try {
            if (intoDbCNumMap.containsKey(cNum)) {
                lock = intoDbCNumMap.get(cNum);
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                lock = new Object();
            }
            intoDbCNumMap.put(cNum, lock);
            IntoDbUtil2 intoDbUtil = new IntoDbUtil2(paperMap, picInfoMapJson);
            try {
                String[] rtn = this.hessianService.SendIntoDb(intoDbUtil);
                synchronized (lock) {
                    lock.notifyAll();
                    intoDbCNumMap.remove(cNum);
                }
                return rtn;
            } catch (Exception e2) {
                String errorMsg = e2.getMessage();
                if ((errorMsg != null && errorMsg.indexOf("Deadlock") > -1) || errorMsg.indexOf("Lock wait timeout") > -1) {
                    throw e2;
                }
                if (!intoDbUtil.isClipError() && !intoDbUtil.isBulu()) {
                    log.error("因为异常进入重裁", e2);
                    paperMap.put("clipStatus", "2");
                    String[] SendIntoDb = this.hessianService.SendIntoDb(new IntoDbUtil2(paperMap, picInfoMapJson));
                    synchronized (lock) {
                        lock.notifyAll();
                        intoDbCNumMap.remove(cNum);
                        return SendIntoDb;
                    }
                }
                throw e2;
            }
        } catch (Throwable th) {
            synchronized (lock) {
                lock.notifyAll();
                intoDbCNumMap.remove(cNum);
                throw th;
            }
        }
    }

    public byte[] DownFile(String remoteFile) {
        return this.hessianService.DownFile(remoteFile);
    }

    public boolean DeleteFile(String remoteFile) {
        return this.hessianService.DeleteFile(remoteFile);
    }

    public String[] GetDownFileList(String fileName) {
        return this.hessianService.GetDownFileList(fileName);
    }

    public String DeleteDirectory(String id, String fileName) {
        return this.hessianService.DeleteDirectory(id, fileName);
    }

    public List<Object> GetReClipIdList(String examPaperNum, String testingCentreId, String examRoomNum) {
        return this.hessianService.GetReClipIdList(examPaperNum, testingCentreId, examRoomNum, "");
    }

    public List<Object> GetReClipIdList(String examPaperNum, String testingCentreId, String examRoomNum, String page) {
        return this.hessianService.GetReClipIdList(examPaperNum, testingCentreId, examRoomNum, page);
    }

    public List<Object[]> GetChooseIndex(int length, String pnum, String defineid, String studentId, int page) {
        return this.hessianService.GetChooseIndex(length, pnum, defineid, studentId, page);
    }

    public boolean ResetCorrectstatus(String examNum, String testingCentreId, String gradeNum, String subjectNum, String examRoomNum, boolean scanType) {
        return this.hessianService.ResetCorrectstatus(examNum, testingCentreId, gradeNum, subjectNum, examRoomNum, scanType);
    }

    public String[] UpdateStudentId(String infoMapJsonStr) {
        return this.hessianService.UpdateStudentId(infoMapJsonStr);
    }

    public Map<String, Object> GetStudentInfoByExamineeNum(String infoMapJsonStr) {
        return this.hessianService.GetStudentInfoByExamineeNum(infoMapJsonStr);
    }

    public String GetGroupNum(String examPaperNum, String studentId, int pageNo) {
        return this.hessianService.GetGroupNum(examPaperNum, studentId, pageNo);
    }

    public boolean Upload(String remoteFile, byte[] bytes) {
        return this.hessianService.Upload(remoteFile, bytes);
    }

    public boolean BatchUpload(String[] fileNames, Map<Integer, byte[]> ht) {
        return this.hessianService.BatchUpload(fileNames, ht);
    }

    public Object[] GetScanTest(String examNum, String gradeNum, String subjectNum, String scannerNo, String testingCentreId) {
        return this.hessianService.GetScanTest(examNum, gradeNum, subjectNum, scannerNo, testingCentreId);
    }

    public String GetOneUploadPicRecordId(String paperNum, String testingCentreId, String scannerNum) {
        return this.hessianService.GetOneUploadPicRecordId(paperNum, testingCentreId, scannerNum);
    }

    public Map<String, Object> GetExamSchoolStuCountByEachSubject(String examPaperNum) {
        return this.hessianService.GetExamSchoolStuCountByEachSubject(examPaperNum);
    }

    public List<Object[]> GetUploadListGroupByTestCenter(String examNum, String gradeNum, String subjectNum) {
        return this.hessianService.GetUploadListGroupByTestCenter(examNum, gradeNum, subjectNum);
    }

    public boolean SaveOneUploadPicRecord(HashMap<String, Object> ht) {
        return this.hessianService.SaveOneUploadPicRecord(ht);
    }

    public boolean SaveScanTest(HashMap<String, Object> ht) throws Throwable {
        return this.hessianService.SaveScanTest(ht);
    }

    public String GetScoreModel(String examNum) {
        return this.hessianService.GetScoreModel(examNum);
    }

    public List<Object[]> GetScoreList(String examPaperNum, String regId) {
        return this.hessianService.GetScoreList(examPaperNum, regId);
    }

    public boolean UpdateScore(String scoreId, String regScore, String questionScore, String isException) {
        return this.hessianService.UpdateScore(scoreId, regScore, questionScore, isException);
    }

    public boolean SaveBigImages(Map<String, String> bigImageInfoMap) {
        return this.hessianService.SaveBigImages(bigImageInfoMap);
    }

    public boolean IsExistPageInfoInTemplate(String examPaperNum, int extPageType, int selfType) {
        return this.hessianService.IsExistPageInfoInTemplate(examPaperNum, extPageType, selfType);
    }

    public boolean UpdateQuestionGroupTotalCount(String examPaperNum) {
        return this.hessianService.UpdateQuestionGroupTotalCount(examPaperNum);
    }

    public boolean SubmitTemplateRequest(HashMap<String, Object> ht) {
        return this.hessianService.SubmitTemplateRequest(ht);
    }

    public Map<String, Object> GetTempateStatus(String examNum, String gradeNum, String subjectNum, String scannerNo, String ABNNum) {
        if (!this.templateSl.validate(this.templateSl.tryOptimisticRead())) {
            long stamp = this.templateSl.readLock();
            try {
                Map<String, Object> GetTempateStatus = this.hessianService.GetTempateStatus(examNum, gradeNum, subjectNum, scannerNo, ABNNum);
                this.templateSl.unlockRead(stamp);
                return GetTempateStatus;
            } catch (Throwable th) {
                this.templateSl.unlockRead(stamp);
                throw th;
            }
        }
        return this.hessianService.GetTempateStatus(examNum, gradeNum, subjectNum, scannerNo, ABNNum);
    }

    public TemplateRecord GetTempate(String examNum, String gradeNum, String subjectNum, String scannerNo, String ABNNum, int mother) {
        return this.hessianService.GetTempate(examNum, gradeNum, subjectNum, scannerNo, ABNNum, mother);
    }

    public List<Map<String, Object>> GetUploadTemplateRecordList(String examNum, String scannerNo) {
        return this.hessianService.GetUploadTemplateRecordList(examNum, scannerNo);
    }

    public boolean DeleteTemplate(String examNum, String gradeNum, String subjectNum, String scannerNo, String ABNNum) {
        long stamp = this.templateSl.writeLock();
        try {
            boolean DeleteTemplate = this.hessianService.DeleteTemplate(examNum, gradeNum, subjectNum, scannerNo, ABNNum);
            this.templateSl.unlockWrite(stamp);
            return DeleteTemplate;
        } catch (Throwable th) {
            this.templateSl.unlockWrite(stamp);
            throw th;
        }
    }

    public ZTreeNode GetMother(String examNum, String gradeNum, String subjectNum, String abType) {
        if (!this.templateSl.validate(this.templateSl.tryOptimisticRead())) {
            long stamp = this.templateSl.readLock();
            try {
                ZTreeNode GetMother = this.hessianService.GetMother(examNum, gradeNum, subjectNum, abType);
                this.templateSl.unlockRead(stamp);
                return GetMother;
            } catch (Throwable th) {
                this.templateSl.unlockRead(stamp);
                throw th;
            }
        }
        return this.hessianService.GetMother(examNum, gradeNum, subjectNum, abType);
    }

    public boolean UpdateTemplateStatus(String id, String status, String description, String createUser) {
        long stamp = this.templateSl.writeLock();
        try {
            boolean UpdateTemplateStatus = this.hessianService.UpdateTemplateStatus(id, status, description, createUser);
            this.templateSl.unlockWrite(stamp);
            return UpdateTemplateStatus;
        } catch (Throwable th) {
            this.templateSl.unlockWrite(stamp);
            throw th;
        }
    }

    public boolean UpdateTemplate(Template template) throws Throwable {
        long stamp = this.templateS2.writeLock();
        try {
            boolean UpdateTemplate = this.hessianService.UpdateTemplate(template);
            this.templateS2.unlockWrite(stamp);
            return UpdateTemplate;
        } catch (Throwable th) {
            this.templateS2.unlockWrite(stamp);
            throw th;
        }
    }

    public boolean CopyTemplate(String id, String motherId, String createUser, String location, String filename) throws Throwable {
        long stamp = this.templateS2.writeLock();
        try {
            boolean CopyTemplate = this.hessianService.CopyTemplate(id, motherId, createUser, location, filename);
            this.templateS2.unlockWrite(stamp);
            return CopyTemplate;
        } catch (Throwable th) {
            this.templateS2.unlockWrite(stamp);
            throw th;
        }
    }

    public boolean CreatLocalTemplate(String motherId, String createUser, String location, String filename, String scannerNum, String scannerName) throws Throwable {
        return this.hessianService.CreatLocalTemplate(motherId, createUser, location, filename, scannerNum, scannerName);
    }

    public boolean CheckTemplate(String examNum, String gradeNum, String subjectNum, String scannerNum) {
        return this.hessianService.CheckTemplate(examNum, gradeNum, subjectNum, scannerNum);
    }

    public boolean CheckMotherTemplate(String examNum, String gradeNum, String subjectNum) {
        return this.hessianService.CheckMotherTemplate(examNum, gradeNum, subjectNum);
    }

    public boolean UpDateTemplateXml(String id, byte[] bytes, String location, String updateUser) throws Throwable {
        return this.hessianService.UpDateTemplateXml(id, bytes, location, updateUser);
    }

    public boolean UpDateTemplateXmlOrCreate(String id, byte[] bytes, String location, String filename, String scannerNum, String scannerName, String updateUser) throws Throwable {
        return this.hessianService.UpDateTemplateXmlOrCreate(id, bytes, location, filename, scannerNum, scannerName, updateUser);
    }

    public List<Map<String, Object>> GetUploadMapList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.hessianService.GetUploadMapList(examNum, gradeNum, subjectNum, testingCentreId);
    }

    public boolean SaveUploadPicFileList(String jsonList) {
        List<UploadPicFile> list = JSONObject.parseArray(jsonList, UploadPicFile.class);
        return this.hessianService.SaveUploadPicFileList(list);
    }

    public List<UploadPicFile> GetUploadPicFileList(String batchNum) {
        return this.hessianService.GetUploadPicFileList(batchNum);
    }

    public List<Map<String, Object>> GetExamStuList(String testingCenterId, String gradeNum, String subjectNum) {
        return this.hessianService.GetExamStuList(testingCenterId, gradeNum, subjectNum);
    }

    public List<Map<String, Object>> GetExamStuWithoutUploadedList(String examNum, String testingCenterId, String gradeNum, String subjectNum) {
        return this.hessianService.GetExamStuWithoutUploadedList(examNum, testingCenterId, gradeNum, subjectNum);
    }

    public boolean IsConfirmedMissingPaper(String examNum, String testingCenterId, String gradeNum, String subjectNum) {
        return this.hessianService.IsConfirmedMissingPaper(examNum, testingCenterId, gradeNum, subjectNum);
    }

    public boolean SaveMissingPaper(MissingPaper missingPaper) {
        return this.hessianService.SaveMissingPaper(missingPaper);
    }

    public boolean IsInheritMotherBySelf() {
        return this.hessianService.IsInheritMotherBySelf();
    }

    public EdeiInfo GetEdeiInfo() {
        return StaticClassResources.EdeiInfo;
    }

    public Map<String, Object> GetClipPageMarkSampleIdMap(String examNum, String gradeNum, String subjectNum, String scannerNum, String abType, int pageCount) {
        return this.hessianService.GetClipPageMarkSampleIdMap(examNum, gradeNum, subjectNum, scannerNum, abType, pageCount);
    }

    public boolean canDelete(String examPaperNum, String testingCentreId) {
        return this.hessianService.canDelete(examPaperNum, testingCentreId);
    }

    public List<CsDefine> GetTemplateNeedCsDefineList(String examPaperNum) {
        return this.hessianService.getTemplateNeedCsDefineList(examPaperNum);
    }

    public void ClearDefineCache(String examPaperNum) {
        DefineCache.remove(examPaperNum);
    }

    public boolean IfScanComplated(String exampaperNum) {
        return this.hessianService.IfScanComplated(exampaperNum);
    }

    public Map<String, Object> GetOneUploadRecord(String examNum, String user, String machineGuid) {
        long stamp = this.spiritS.writeLock();
        try {
            Map<String, Object> oneUploadRecord = this.hessianService.getOneUploadRecord(examNum, user, machineGuid);
            this.spiritS.unlockWrite(stamp);
            return oneUploadRecord;
        } catch (Throwable th) {
            this.spiritS.unlockWrite(stamp);
            throw th;
        }
    }

    public Map<String, Object> GetOneUploadRecordById(String id, String examNum, String user, String machineGuid) {
        long stamp = this.spiritS.writeLock();
        try {
            Map<String, Object> oneUploadRecordById = this.hessianService.getOneUploadRecordById(id, examNum, user, machineGuid);
            this.spiritS.unlockWrite(stamp);
            return oneUploadRecordById;
        } catch (Throwable th) {
            this.spiritS.unlockWrite(stamp);
            throw th;
        }
    }

    public Map<String, Object> AutoVerifyTestScanImage(String examPaperNum, String testingCenterId, String user, String machineGuid) {
        return this.hessianService.AutoVerifyTestScanImage(examPaperNum, testingCenterId, user, machineGuid);
    }

    public boolean OpenKaoHaoJiaoDui() {
        return "1".equals(Configuration.getInstance().getOpenKaoHaoJiaoDui());
    }

    public Integer GetKaoHaoJiaoDuiZhuangTai(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.hessianService.getKaoHaoJiaoDuiZhuangTai(examNum, gradeNum, subjectNum, testingCentreId);
    }

    public Map<String, Object> GetConfiguration() {
        return BeanUtil.beanToMap(Configuration.getInstance(), new String[0]);
    }

    public boolean MoveSubject(String examNum, String testingCentreId, String gradeNum, String subjectNum, String batch, String cNum, String user) {
        return this.hessianService.MoveSubject(examNum, testingCentreId, gradeNum, subjectNum, batch, cNum, user);
    }

    public boolean AutomaticAuditTestScanImage(String examNum, String gradeNum, String subjectNum, String testingCenterId) {
        return this.hessianService.AutomaticAuditTestScanImage(examNum, gradeNum, subjectNum, testingCenterId);
    }

    public Map<String, String> AutomaticAuditTestScanImageSetting(String examNum, String gradeNum, String subjectNum, String testingCenterId) {
        return this.hessianService.AutomaticAuditTestScanImageSetting(examNum, gradeNum, subjectNum, testingCenterId);
    }

    public boolean IfCloseTestingCenterSecondaryPositioning(String testingCenterId) {
        return this.hessianService.IfCloseTestingCenterSecondaryPositioning(testingCenterId);
    }

    public List<Long> GetBatchReClipIdList(String examPaperNum, List<String> testingCenterIdList) {
        return this.hessianService.GetBatchReClipIdList(examPaperNum, testingCenterIdList);
    }

    public boolean DeleteScanTest(String id) {
        return this.hessianService.DeleteScanTest(id);
    }

    public boolean ForceMakeSelfTemplate(String testingCenterId) {
        return this.hessianService.ForceMakeSelfTemplate(testingCenterId);
    }
}
