package com.dmj.cs.serviceimpl;

import com.alibaba.fastjson.JSONObject;
import com.dmj.cs.bean.CsDefine;
import com.dmj.cs.bean.MissingPaper;
import com.dmj.cs.bean.Question;
import com.dmj.cs.bean.Template;
import com.dmj.cs.bean.TemplateRecord;
import com.dmj.cs.bean.UploadPicFile;
import com.dmj.cs.bean.ZTreeNode;
import com.dmj.cs.service.HessianService;
import com.dmj.cs.util.CsUtils;
import com.dmj.cs.util.Dbutil;
import com.dmj.cs.util.IntoDbUtil2;
import com.dmj.util.Const;
import com.dmj.util.GUID;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/* loaded from: HessianServiceImpl.class */
public class HessianServiceImpl implements HessianService {
    Dbutil dbutil = new Dbutil();
    private static final Logger log = Logger.getLogger(HessianServiceImpl.class);
    static final Byte o = (byte) 0;

    @Override // com.dmj.cs.service.HessianService
    public boolean Conect() {
        return true;
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean UpdatePicServerInfo(int[] fileds, String[] values, String user) {
        return this.dbutil.UpdatePicServerInfo(fileds, values, user);
    }

    @Override // com.dmj.cs.service.HessianService
    public Object[] GetLoginId(String username, String password) {
        return this.dbutil.GetLoginId(username, password);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetNoDoneExamList(String userNum) {
        return this.dbutil.GetNoDoneExamList(userNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetPaperSizeList() {
        return this.dbutil.GetPaperSizeList();
    }

    @Override // com.dmj.cs.service.HessianService
    public Object GetExamPaperNum(String examNum, String gradeNum, String subjectNum) {
        return this.dbutil.GetExamPaperNum(examNum, gradeNum, subjectNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetQuestionDictionaryMap(String paperNum, String key) {
        LinkedHashMap<String, LinkedHashMap<String, Object>> map = this.dbutil.GetQuestionDictionaryMap(paperNum, key);
        return JSONObject.toJSONString(map);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean UpdateNotice(String examPaperNum, String examineeInstructions) {
        return this.dbutil.UpdateNotice(examPaperNum, examineeInstructions);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetGradeList(String examNum, String testingCentreId, int pageDoType, boolean isBl, String loginUser) {
        return this.dbutil.GetGradeList(examNum, testingCentreId, pageDoType, isBl, loginUser);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetSubjectList(String examNum, String gradeNum, String testingCentreId, int pageDoType, boolean isBl, String loginUser) {
        return this.dbutil.GetSubjectList(examNum, gradeNum, testingCentreId, pageDoType, isBl, loginUser);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetSchoolList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.dbutil.GetSchoolList(examNum, gradeNum, subjectNum, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetExamPaperDictionary(String[] fields, String examNum, String gradeNum, String subjectNum, String templateType) {
        return this.dbutil.GetExamPaperDictionary(fields, examNum, gradeNum, subjectNum, templateType);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetExamPaperDictionary(String[] fields, String examPaperNum) {
        return this.dbutil.GetExamPaperDictionary(fields, examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetPaperSizeName(String paperSizeNum) {
        return this.dbutil.GetPaperSizeName(paperSizeNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public Object GetTemplateInfo(String examPaperNum, String abtype) {
        return this.dbutil.GetTemplateInfo(examPaperNum, abtype);
    }

    @Override // com.dmj.cs.service.HessianService
    public String getNotExistTemplateInfo(String examPaperNum, boolean isAb) {
        return this.dbutil.getNotExistTemplateInfo(examPaperNum, isAb);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetTemplateImage(String examPaperNum, String abtype) {
        return this.dbutil.GetTemplateImage(examPaperNum, abtype);
    }

    @Override // com.dmj.cs.service.HessianService
    public Object GetExamPaperNum(String examNum, String gradeNum, String subjectNum, Boolean isCheckSelfOrOut) {
        return this.dbutil.GetExamPaperNum(examNum, gradeNum, subjectNum, isCheckSelfOrOut);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean SaveExtTemplate(Template template) throws Throwable {
        return this.dbutil.SaveExtTemplate(template);
    }

    @Override // com.dmj.cs.service.HessianService
    public Object GetExamineeNumLength(String examNum, String testingCentreId, String gradeNum, String subjectNum) {
        return this.dbutil.GetExamineeNumLength(examNum, testingCentreId, gradeNum, subjectNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetExtTemplateInfo(String examPaperNum) {
        return this.dbutil.GetExtTemplateInfo(examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean DelTemplate(String examPaperNum) {
        return this.dbutil.DelTemplate(examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean IsExistDefineData(String examPaperNum) {
        return this.dbutil.IsExistDefineData(examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetExamRoomStr(String examRoomNum) {
        return this.dbutil.GetExamRoomStr(examRoomNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public int[] GetStuCountInfo(String scanType, String examNum, String paperNum, String testingCentreId, String gradeNum, String examRoomNum) {
        return this.dbutil.GetStuCountInfo(scanType, examNum, paperNum, testingCentreId, gradeNum, examRoomNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetScanBatchList(String examNum, String gradeNum, String testingCentreId, String subjectNum, String scannerNum, String loginUser) {
        return this.dbutil.GetScanBatchList(examNum, gradeNum, testingCentreId, subjectNum, scannerNum, loginUser);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetBatchList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.dbutil.GetBatchList(examNum, gradeNum, subjectNum, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetRegCountByScanBatch(String examPaperNum, String testingCentreId) {
        return this.dbutil.GetRegCountByScanBatch(examPaperNum, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean AddScanBatch(String examNum, String gradeNum, String testingCentreId, String subjectNum, String scannerNum, String batchNo, String loginUser, String picBatchNum) {
        return this.dbutil.AddScanBatch(examNum, gradeNum, testingCentreId, subjectNum, scannerNum, batchNo, loginUser, picBatchNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean DelScanBatch(String batch) {
        return this.dbutil.DelScanBatch(batch);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetFinishedExamRoomList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.dbutil.GetFinishedExamRoomList(examNum, gradeNum, subjectNum, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetFinishedExamSubjectList(String examNum, String gradeNum, String testingCentreId) {
        return this.dbutil.GetFinishedExamSubjectList(examNum, gradeNum, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetUploadList(String examNum, String gradeNum, String subjectNum, String testingCentreId, int count) {
        return this.dbutil.GetUploadList(examNum, gradeNum, subjectNum, testingCentreId, count);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetUploadDictionary(String id) {
        return this.dbutil.GetUploadDictionary(id);
    }

    @Override // com.dmj.cs.service.HessianService
    public int GetExamStuCount(String examPaperNum, String testingCentreId) {
        return this.dbutil.GetExamStuCount(examPaperNum, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetImgPathConfigDictionary(String examNum) {
        return this.dbutil.GetImgPathConfigDictionary(examNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetExamRoomList(String examNum, String gradeNum, String testingCentreId, String subjectNum, boolean isBl) {
        return this.dbutil.GetExamRoomList(examNum, gradeNum, testingCentreId, subjectNum, isBl);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetTestingCenterList(String examNum, String loginUser, boolean isBl) {
        return this.dbutil.GetTestingCenterList(examNum, loginUser, isBl);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetScannerNum(String batchNo) {
        return this.dbutil.GetScannerNum(batchNo);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean DeleteByOneRegid(String regid) {
        return this.dbutil.DeleteByOneRegid(regid, true, true, true);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean DeleteByOneCNum(String CNum) {
        List<Object> list = this.dbutil.GetRegIdListByCNum(CNum);
        if (list != null && list.size() > 0) {
            for (Object regId : list) {
                this.dbutil.DeleteByOneRegid(regId.toString(), true, true, true);
            }
            return true;
        }
        return true;
    }

    @Override // com.dmj.cs.service.HessianService
    public Object GetObject(String sql) {
        return this.dbutil.GetObject(sql);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object> GetColList(String sql) {
        return this.dbutil.GetColList(sql);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean Execute(String sql) {
        return this.dbutil.Execute(sql);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object> GetScanRecord(String examPaperNum, String testingCentreId, String examRoomNum, String batch, String scanType) {
        return this.dbutil.GetScanRecord(examPaperNum, testingCentreId, examRoomNum, batch, scanType);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean DelCorrectStatus(String examPaperNum, String testingCentreId, String examRoomNum, String scanType) {
        return this.dbutil.DelCorrectStatus(examPaperNum, testingCentreId, examRoomNum, scanType);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetStuPaperImgIdList(String examPaperNum, String testingCentreId) {
        return this.dbutil.GetStuPaperImgIdList(examPaperNum, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetImage(String id) {
        return this.dbutil.GetImage(id);
    }

    @Override // com.dmj.cs.service.HessianService
    public Object[] GetOneUploadPicRecord(String id) {
        return this.dbutil.GetOneUploadPicRecord(id);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean UpdateOneUploadPicRecord(String id, String state, String scannerNum, String count, String userId) {
        return this.dbutil.UpdateOneUploadPicRecord(id, state, scannerNum, count, userId);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean SaveQuestionOrAnswerImage(Question q) {
        return this.dbutil.SaveQuestionOrAnswerImage(q);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetQuestionListByQuestionAnswer(String examPaperNum) {
        return this.dbutil.GetQuestionListByQuestionAnswer(examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<String> GetFinishedIntoDbQuestionOrAnswerList(String examPaperNum, String table) {
        return this.dbutil.GetFinishedIntoDbQuestionOrAnswerList(examPaperNum, table);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetMissingStuList(String testingCentreId, String gradeNum, String subjectNum, String examNum, String examRoomNum) {
        return this.dbutil.GetMissingStuList(testingCentreId, gradeNum, subjectNum, examNum, examRoomNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetOneMissStuImageInfo(String examNum, String gradeNum, String subjectNum, String examRoomNum, String testingCentreId, int curPage) {
        return this.dbutil.GetOneMissStuImageInfo(examNum, gradeNum, subjectNum, examRoomNum, testingCentreId, curPage);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetStudentId(String testingCentreId, String examNum, String gradeNum, String examineeNum) {
        return this.dbutil.GetStudentId(testingCentreId, examNum, gradeNum, examineeNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean UpDateStudentId(String studentId, String cNum, String regId, String loginUser, String page) {
        return this.dbutil.UpDateStudentId(studentId, cNum, regId, loginUser, page);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean IsAb(String curPaperNum) {
        return this.dbutil.IsAb(curPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetAbType(String cNum) {
        return this.dbutil.GetAbType(cNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public Object[] GetExamPaperNumInfo(String examPaperNum) {
        return this.dbutil.GetExamPaperNumInfo(examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public int GetReClipCount(String examPaperNum, String testingCentreId, String examRoomNum, String page) {
        return this.dbutil.GetReClipCount(examPaperNum, testingCentreId, examRoomNum, page);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetOrUpdateOneClipRecord(String exampaperNum, String testingCentreId, String examRoomNum, String page, String regid, String type, String id) {
        Map<String, Object> GetOrUpdateOneClipRecord;
        synchronized (o) {
            GetOrUpdateOneClipRecord = this.dbutil.GetOrUpdateOneClipRecord(exampaperNum, testingCentreId, examRoomNum, page, regid, type, id);
        }
        return GetOrUpdateOneClipRecord;
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetCantrecognizedId(String regId) {
        return this.dbutil.GetCantrecognizedId(regId);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean RecoveryCantrecognized(String gradeNum, String subjectNum, String testingCentreId, String examRoomNum) {
        return this.dbutil.RecoveryCantrecognized(gradeNum, subjectNum, testingCentreId, examRoomNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public String[] SendIntoDb(IntoDbUtil2 intoDbUtil) {
        try {
            return intoDbUtil.intoDb();
        } catch (Throwable e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public String getAllExceptioninformation(Throwable e) {
        String sOut = "";
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement s : trace) {
            sOut = sOut + "\tat " + s.toString() + "\r\n";
        }
        return e.getCause() + "\r\n" + e.getMessage() + sOut;
    }

    @Override // com.dmj.cs.service.HessianService
    public byte[] DownFile(String remoteFile) {
        try {
            return FileUtils.readFileToByteArray(new File(remoteFile));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(getAllExceptioninformation(e));
        }
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean DeleteFile(String remoteFile) {
        return CsUtils.deleteFile(remoteFile);
    }

    @Override // com.dmj.cs.service.HessianService
    public String[] GetDownFileList(String fileName) {
        return CsUtils.GetDownFileList(fileName);
    }

    @Override // com.dmj.cs.service.HessianService
    public String DeleteDirectory(String id, String fileName) {
        try {
            String pathString = this.dbutil.GetPath(id);
            File newFile = new File(fileName + pathString);
            File newFilebakRom = new File(fileName + pathString + "-bak-" + GUID.getGUID());
            try {
                FileUtils.copyDirectory(newFile, newFilebakRom);
            } catch (Exception e) {
            }
            File[] files = newFile.listFiles();
            if (files == null || files.length == 0) {
                this.dbutil.UpdaeFilesCount(id, 0);
                return "没有要删除的文件";
            }
            for (File file : files) {
                file.delete();
                this.dbutil.UpdaeFilesCount(id, newFile.listFiles().length);
            }
            return Const.INFO_SUCCESS;
        } catch (Exception e2) {
            e2.printStackTrace();
            return "删除文件异常：文件被占用或未知异常！";
        }
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object> GetReClipIdList(String examPaperNum, String testingCentreId, String examRoomNum, String page) {
        return this.dbutil.GetReClipIdList(examPaperNum, testingCentreId, examRoomNum, page);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetChooseIndex(int length, String pnum, String defineid, String studentId, int page) {
        return this.dbutil.GetChooseIndex(length, pnum, defineid, studentId, page);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean ResetCorrectstatus(String examNum, String testingCentreId, String gradeNum, String subjectNum, String examRoomNum, boolean scanType) {
        return this.dbutil.ResetCorrectstatus(examNum, testingCentreId, gradeNum, subjectNum, examRoomNum, scanType);
    }

    @Override // com.dmj.cs.service.HessianService
    public String[] UpdateStudentId(String infoMapJsonStr) {
        return this.dbutil.UpdateStudentId(infoMapJsonStr);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetStudentInfoByExamineeNum(String infoMapJsonStr) {
        return this.dbutil.GetStudentInfoByExamineeNum(infoMapJsonStr);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetGroupNum(String examPaperNum, String studentId, int pageNo) {
        return this.dbutil.GetGroupNum(examPaperNum, studentId, pageNo);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean Upload(String remoteFile, byte[] bytes) {
        try {
            CsUtils.writeByteArrayToFile(remoteFile, bytes);
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage());
            throw new RuntimeException(getAllExceptioninformation(e));
        }
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean BatchUpload(String[] fileNames, Map<Integer, byte[]> ht) {
        try {
            for (Map.Entry<Integer, byte[]> entry : ht.entrySet()) {
                CsUtils.writeByteArrayToFile(fileNames[entry.getKey().intValue()], entry.getValue());
            }
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage());
            throw new RuntimeException(getAllExceptioninformation(e));
        }
    }

    @Override // com.dmj.cs.service.HessianService
    public Object[] GetScanTest(String examNum, String gradeNum, String subjectNum, String scannerNo, String testingCentreId) {
        return this.dbutil.GetScanTest(examNum, gradeNum, subjectNum, scannerNo, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetOneUploadPicRecordId(String paperNum, String testingCentreId, String scannerNum) {
        return this.dbutil.GetOneUploadPicRecordId(paperNum, testingCentreId, scannerNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetExamSchoolStuCountByEachSubject(String examPaperNum) {
        return this.dbutil.GetExamSchoolStuCountByEachSubject(examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetUploadListGroupByTestCenter(String examNum, String gradeNum, String subjectNum) {
        return this.dbutil.GetUploadListGroupByTestCenter(examNum, gradeNum, subjectNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean SaveOneUploadPicRecord(HashMap<String, Object> ht) {
        return this.dbutil.SaveOneUploadPicRecord(ht);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean SaveScanTest(HashMap<String, Object> ht) throws Throwable {
        return this.dbutil.SaveScanTest(ht);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetScoreModel(String examNum) {
        return this.dbutil.GetScoreModel(examNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Object[]> GetScoreList(String examPaperNum, String regId) {
        return this.dbutil.GetScoreList(examPaperNum, regId);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean UpdateScore(String scoreId, String regScore, String questionScore, String isException) {
        return this.dbutil.UpdateScore(scoreId, regScore, questionScore, isException);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean SaveBigImages(Map<String, String> bigImageInfoMap) {
        return this.dbutil.SaveBigImages(bigImageInfoMap);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean IsExistPageInfoInTemplate(String examPaperNum, int extPageType, int selfType) {
        return this.dbutil.IsExistPageInfoInTemplate(examPaperNum, extPageType, selfType);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean UpdateQuestionGroupTotalCount(String examPaperNum) {
        return this.dbutil.UpdateQuestionGroupTotalCount(examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean SubmitTemplateRequest(HashMap<String, Object> ht) {
        return this.dbutil.SubmitTemplateRequest(ht);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetTempateStatus(String examNum, String gradeNum, String subjectNum, String scannerNo, String ABNNum) {
        return this.dbutil.GetTempateStatus(examNum, gradeNum, subjectNum, scannerNo, ABNNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public TemplateRecord GetTempate(String examNum, String gradeNum, String subjectNum, String scannerNo, String ABNNum, int mother) {
        return this.dbutil.GetTempate(examNum, gradeNum, subjectNum, scannerNo, ABNNum, mother);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Map<String, Object>> GetUploadTemplateRecordList(String examNum, String scannerNo) {
        return this.dbutil.GetUploadTemplateRecordList(examNum, scannerNo);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean DeleteTemplate(String examNum, String gradeNum, String subjectNum, String scannerNo, String ABNNum) {
        return this.dbutil.DeleteTemplate(examNum, gradeNum, subjectNum, scannerNo, ABNNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public String GetTemplateTreeJson(String examNum, String user) {
        return this.dbutil.GetTemplateTreeJson(examNum, user);
    }

    @Override // com.dmj.cs.service.HessianService
    public ZTreeNode GetMother(String examNum, String gradeNum, String subjectNum, String abType) {
        return this.dbutil.GetMother(examNum, gradeNum, subjectNum, abType);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean UpdateTemplateStatus(String id, String status, String description, String createUser) {
        return this.dbutil.UpdateTemplateStatus(id, status, description, createUser);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean UpdateTemplate(Template template) throws Throwable {
        return this.dbutil.UpdateTemplate(template);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean CopyTemplate(String id, String motherId, String createUser, String location, String filename) throws Throwable {
        return this.dbutil.CopyTemplate(id, motherId, createUser, location, filename);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean CreatLocalTemplate(String motherId, String createUser, String location, String filename, String scannerNum, String scannerName) throws Throwable {
        return this.dbutil.CreatLocalTemplate(motherId, createUser, location, filename, scannerNum, scannerName);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean CheckTemplate(String examNum, String gradeNum, String subjectNum, String scannerNum) {
        return this.dbutil.CheckTemplate(examNum, gradeNum, subjectNum, scannerNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean CheckMotherTemplate(String examNum, String gradeNum, String subjectNum) {
        return this.dbutil.CheckMotherTemplate(examNum, gradeNum, subjectNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean UpDateTemplateXml(String id, byte[] bytes, String location, String updateUser) throws Throwable {
        return this.dbutil.UpDateTemplateXml(id, bytes, location, updateUser);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean UpDateTemplateXmlOrCreate(String id, byte[] bytes, String location, String filename, String scannerNum, String scannerName, String updateUser) throws Throwable {
        return this.dbutil.UpDateTemplateXmlOrCreate(id, bytes, location, filename, scannerNum, scannerName, updateUser);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Map<String, Object>> GetUploadMapList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.dbutil.GetUploadMapList(examNum, gradeNum, subjectNum, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean SaveUploadPicFileList(List<UploadPicFile> list) {
        return this.dbutil.SaveUploadPicFileList(list);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<UploadPicFile> GetUploadPicFileList(String batchNum) {
        return this.dbutil.GetUploadPicFileList(batchNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Map<String, Object>> GetExamStuList(String testingCenterId, String gradeNum, String subjectNum) {
        return this.dbutil.GetExamStuList(testingCenterId, gradeNum, subjectNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Map<String, Object>> GetExamStuWithoutUploadedList(String examNum, String testingCenterId, String gradeNum, String subjectNum) {
        return this.dbutil.GetExamStuWithoutUploadedList(examNum, testingCenterId, gradeNum, subjectNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean IsConfirmedMissingPaper(String examNum, String testingCenterId, String gradeNum, String subjectNum) {
        return this.dbutil.IsConfirmedMissingPaper(examNum, testingCenterId, gradeNum, subjectNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean SaveMissingPaper(MissingPaper missingPaper) {
        return this.dbutil.SaveMissingPaper(missingPaper);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean IsInheritMotherBySelf() {
        ResourceBundle conff = ResourceBundle.getBundle("conff");
        if (!conff.containsKey("IsInheritMotherBySelf")) {
            return false;
        }
        String value = conff.getString("IsInheritMotherBySelf");
        return "1".equals(value);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> GetClipPageMarkSampleIdMap(String examNum, String gradeNum, String subjectNum, String scannerNum, String abType, int pageCount) {
        return this.dbutil.GetClipPageMarkSampleIdMap(examNum, gradeNum, subjectNum, scannerNum, abType, pageCount);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean canDelete(String examPaperNum, String testingCentreId) {
        return this.dbutil.canDelete(examPaperNum, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<CsDefine> getTemplateNeedCsDefineList(String examPaperNum) {
        return this.dbutil.getTemplateNeedCsDefineList(examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, CsDefine> getTemplateNeedCsDefineMap(String examPaperNum) {
        return this.dbutil.getTemplateNeedCsDefineMap(examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Map<String, Object>> GetQuestionSingleOrDoubleMarkInfo(String examPaperNum) {
        return this.dbutil.GetQuestionSingleOrDoubleMarkInfo(examPaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean IfScanComplated(String exampaperNum) {
        return this.dbutil.IfScanComplated(exampaperNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> getReClipWaitUpdatePageList(String examPaperNum, String testingCentreId, String examinationRoomNum, String page, String checkPage) {
        return this.dbutil.getReClipWaitUpdatePageList(examPaperNum, testingCentreId, examinationRoomNum, page, checkPage);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean changePage(String regId, String cNum, String totalPage, String newPage, boolean updateOther) {
        return this.dbutil.changePage(regId, cNum, totalPage, newPage, updateOther);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean batchChangePage(Object[][] args) {
        return this.dbutil.batchChangePage(args);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> getOneUploadRecord(String examNum, String user, String machineGuid) {
        return this.dbutil.getOneUploadRecord(examNum, user, machineGuid);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> getOneUploadRecordById(String id, String examNum, String user, String machineGuid) {
        return this.dbutil.getOneUploadRecordById(id, examNum, user, machineGuid);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Map<String, Object>> getReClipRecordList(String examPaperNum, String testingCenterId, String examRoomNum) {
        return this.dbutil.getReClipRecordList(examPaperNum, testingCenterId, examRoomNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Map<String, Object>> getAllReClipImageList(String examPaperNum, String testingCenterId, String examRoomNum) {
        return this.dbutil.getAllReClipImageList(examPaperNum, testingCenterId, examRoomNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, Object> AutoVerifyTestScanImage(String examPaperNum, String testingCenterId, String user, String machineGuid) {
        return this.dbutil.AutoVerifyTestScanImage(examPaperNum, testingCenterId, user, machineGuid);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Map<String, Object>> getStudentPaperImageByRedId(String regIds) {
        return this.dbutil.getStudentPaperImageByRedId(regIds);
    }

    @Override // com.dmj.cs.service.HessianService
    public Integer getKaoHaoJiaoDuiZhuangTai(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.dbutil.getKaoHaoJiaoDuiZhuangTai(examNum, gradeNum, subjectNum, testingCentreId);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean MoveSubject(String examNum, String testingCentreId, String gradeNum, String subjectNum, String batch, String cNum, String user) {
        return this.dbutil.MoveSubject(examNum, testingCentreId, gradeNum, subjectNum, batch, cNum, user);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean AutomaticAuditTestScanImage(String examNum, String gradeNum, String subjectNum, String testingCenterId) {
        return this.dbutil.AutomaticAuditTestScanImage(examNum, gradeNum, subjectNum, testingCenterId);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, String> AutomaticAuditTestScanImageSetting(String examNum, String gradeNum, String subjectNum, String testingCenterId) {
        return this.dbutil.AutomaticAuditTestScanImageSetting(examNum, gradeNum, subjectNum, testingCenterId);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean IfCloseTestingCenterSecondaryPositioning(String testingCenterId) {
        return this.dbutil.IfCloseTestingCenterSecondaryPositioning(testingCenterId);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Long> GetBatchReClipIdList(String examPaperNum, List<String> testingCenterIdList) {
        return this.dbutil.GetBatchReClipIdList(examPaperNum, testingCenterIdList);
    }

    @Override // com.dmj.cs.service.HessianService
    public Map<String, String> getScanProcess(String examNum, String gradeNum) {
        return this.dbutil.getScanProcess(examNum, gradeNum);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean DeleteScanTest(String id) {
        return this.dbutil.DeleteScanTest(id);
    }

    @Override // com.dmj.cs.service.HessianService
    public int getPairingErrorRecord(String batch) {
        return this.dbutil.getPairingErrorRecord(batch);
    }

    @Override // com.dmj.cs.service.HessianService
    public List<Map<String, Object>> doPairingErrorRecord(String batch, List<Map<String, Object>> list) {
        return this.dbutil.doPairingErrorRecord(batch, list);
    }

    @Override // com.dmj.cs.service.HessianService
    public boolean ForceMakeSelfTemplate(String testingCenterId) {
        return this.dbutil.ForceMakeSelfTemplate(testingCenterId);
    }
}
