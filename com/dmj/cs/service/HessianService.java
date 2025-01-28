package com.dmj.cs.service;

import com.dmj.cs.bean.CsDefine;
import com.dmj.cs.bean.MissingPaper;
import com.dmj.cs.bean.Question;
import com.dmj.cs.bean.Template;
import com.dmj.cs.bean.TemplateRecord;
import com.dmj.cs.bean.UploadPicFile;
import com.dmj.cs.bean.ZTreeNode;
import com.dmj.cs.util.IntoDbUtil2;
import com.zht.db.Transaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: HessianService.class */
public interface HessianService {
    boolean Conect();

    @Transaction
    boolean UpdatePicServerInfo(int[] iArr, String[] strArr, String str);

    Object[] GetLoginId(String str, String str2);

    List<Object[]> GetNoDoneExamList(String str);

    List<Object[]> GetPaperSizeList();

    Object GetExamPaperNum(String str, String str2, String str3);

    Object GetExamPaperNum(String str, String str2, String str3, Boolean bool);

    String GetQuestionDictionaryMap(String str, String str2);

    @Transaction
    boolean UpdateNotice(String str, String str2);

    List<Object[]> GetGradeList(String str, String str2, int i, boolean z, String str3);

    List<Object[]> GetSubjectList(String str, String str2, String str3, int i, boolean z, String str4);

    List<Object[]> GetSchoolList(String str, String str2, String str3, String str4);

    Map<String, Object> GetExamPaperDictionary(String[] strArr, String str, String str2, String str3, String str4);

    Map<String, Object> GetExamPaperDictionary(String[] strArr, String str);

    String GetPaperSizeName(String str);

    Object GetTemplateInfo(String str, String str2);

    String getNotExistTemplateInfo(String str, boolean z);

    List<Object[]> GetExtTemplateInfo(String str);

    List<Object[]> GetTemplateImage(String str, String str2);

    @Transaction
    boolean SaveExtTemplate(Template template) throws Throwable;

    Object GetExamineeNumLength(String str, String str2, String str3, String str4);

    @Transaction
    boolean DelTemplate(String str);

    boolean IsExistDefineData(String str);

    String GetExamRoomStr(String str);

    int[] GetStuCountInfo(String str, String str2, String str3, String str4, String str5, String str6);

    List<Object[]> GetScanBatchList(String str, String str2, String str3, String str4, String str5, String str6);

    List<Object[]> GetBatchList(String str, String str2, String str3, String str4);

    Map<String, Object> GetRegCountByScanBatch(String str, String str2);

    @Transaction
    boolean AddScanBatch(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    boolean DelScanBatch(String str);

    List<Object[]> GetFinishedExamRoomList(String str, String str2, String str3, String str4);

    List<Object[]> GetFinishedExamSubjectList(String str, String str2, String str3);

    List<Object[]> GetUploadList(String str, String str2, String str3, String str4, int i);

    Map<String, Object> GetUploadDictionary(String str);

    int GetExamStuCount(String str, String str2);

    Map<String, Object> GetImgPathConfigDictionary(String str);

    List<Object[]> GetExamRoomList(String str, String str2, String str3, String str4, boolean z);

    List<Object[]> GetTestingCenterList(String str, String str2, boolean z);

    String GetScannerNum(String str);

    @Transaction
    boolean DeleteByOneRegid(String str);

    @Transaction
    boolean DeleteByOneCNum(String str);

    Object GetObject(String str);

    List<Object> GetColList(String str);

    boolean Execute(String str);

    List<Object> GetScanRecord(String str, String str2, String str3, String str4, String str5);

    @Transaction
    boolean DelCorrectStatus(String str, String str2, String str3, String str4);

    String GetStuPaperImgIdList(String str, String str2);

    String GetImage(String str);

    Object[] GetOneUploadPicRecord(String str);

    @Transaction
    boolean UpdateOneUploadPicRecord(String str, String str2, String str3, String str4, String str5);

    @Transaction
    boolean SaveQuestionOrAnswerImage(Question question);

    List<Object[]> GetQuestionListByQuestionAnswer(String str);

    List<String> GetFinishedIntoDbQuestionOrAnswerList(String str, String str2);

    String GetMissingStuList(String str, String str2, String str3, String str4, String str5);

    Map<String, Object> GetOneMissStuImageInfo(String str, String str2, String str3, String str4, String str5, int i);

    String GetStudentId(String str, String str2, String str3, String str4);

    @Transaction
    boolean UpDateStudentId(String str, String str2, String str3, String str4, String str5);

    boolean IsAb(String str);

    String GetAbType(String str);

    Object[] GetExamPaperNumInfo(String str);

    int GetReClipCount(String str, String str2, String str3, String str4);

    Map<String, Object> GetOrUpdateOneClipRecord(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    String GetCantrecognizedId(String str);

    @Transaction
    boolean RecoveryCantrecognized(String str, String str2, String str3, String str4);

    @Transaction
    String[] SendIntoDb(IntoDbUtil2 intoDbUtil2);

    byte[] DownFile(String str);

    boolean DeleteFile(String str);

    boolean Upload(String str, byte[] bArr);

    boolean BatchUpload(String[] strArr, Map<Integer, byte[]> map);

    String[] GetDownFileList(String str);

    String DeleteDirectory(String str, String str2);

    List<Object> GetReClipIdList(String str, String str2, String str3, String str4);

    List<Object[]> GetChooseIndex(int i, String str, String str2, String str3, int i2);

    boolean ResetCorrectstatus(String str, String str2, String str3, String str4, String str5, boolean z);

    @Transaction
    String[] UpdateStudentId(String str);

    Map<String, Object> GetStudentInfoByExamineeNum(String str);

    String GetGroupNum(String str, String str2, int i);

    Object[] GetScanTest(String str, String str2, String str3, String str4, String str5);

    String GetOneUploadPicRecordId(String str, String str2, String str3);

    Map<String, Object> GetExamSchoolStuCountByEachSubject(String str);

    List<Object[]> GetUploadListGroupByTestCenter(String str, String str2, String str3);

    @Transaction
    boolean SaveOneUploadPicRecord(HashMap<String, Object> hashMap);

    @Transaction
    boolean SaveScanTest(HashMap<String, Object> hashMap) throws Throwable;

    String GetScoreModel(String str);

    List<Object[]> GetScoreList(String str, String str2);

    @Transaction
    boolean UpdateScore(String str, String str2, String str3, String str4);

    @Transaction
    boolean SaveBigImages(Map<String, String> map);

    boolean IsExistPageInfoInTemplate(String str, int i, int i2);

    boolean UpdateQuestionGroupTotalCount(String str);

    @Transaction
    boolean SubmitTemplateRequest(HashMap<String, Object> hashMap);

    Map<String, Object> GetTempateStatus(String str, String str2, String str3, String str4, String str5);

    TemplateRecord GetTempate(String str, String str2, String str3, String str4, String str5, int i);

    List<Map<String, Object>> GetUploadTemplateRecordList(String str, String str2);

    @Transaction
    boolean DeleteTemplate(String str, String str2, String str3, String str4, String str5);

    String GetTemplateTreeJson(String str, String str2);

    ZTreeNode GetMother(String str, String str2, String str3, String str4);

    @Transaction
    boolean UpdateTemplateStatus(String str, String str2, String str3, String str4);

    @Transaction
    boolean UpdateTemplate(Template template) throws Throwable;

    @Transaction
    boolean CopyTemplate(String str, String str2, String str3, String str4, String str5) throws Throwable;

    @Transaction
    boolean CreatLocalTemplate(String str, String str2, String str3, String str4, String str5, String str6) throws Throwable;

    boolean CheckTemplate(String str, String str2, String str3, String str4);

    boolean CheckMotherTemplate(String str, String str2, String str3);

    @Transaction
    boolean UpDateTemplateXml(String str, byte[] bArr, String str2, String str3) throws Throwable;

    @Transaction
    boolean UpDateTemplateXmlOrCreate(String str, byte[] bArr, String str2, String str3, String str4, String str5, String str6) throws Throwable;

    List<Map<String, Object>> GetUploadMapList(String str, String str2, String str3, String str4);

    @Transaction
    boolean SaveUploadPicFileList(List<UploadPicFile> list);

    List<UploadPicFile> GetUploadPicFileList(String str);

    List<Map<String, Object>> GetExamStuList(String str, String str2, String str3);

    List<Map<String, Object>> GetExamStuWithoutUploadedList(String str, String str2, String str3, String str4);

    boolean IsConfirmedMissingPaper(String str, String str2, String str3, String str4);

    @Transaction
    boolean SaveMissingPaper(MissingPaper missingPaper);

    boolean IsInheritMotherBySelf();

    Map<String, Object> GetClipPageMarkSampleIdMap(String str, String str2, String str3, String str4, String str5, int i);

    boolean canDelete(String str, String str2);

    List<CsDefine> getTemplateNeedCsDefineList(String str);

    Map<String, CsDefine> getTemplateNeedCsDefineMap(String str);

    Map<String, Map<String, Object>> GetQuestionSingleOrDoubleMarkInfo(String str);

    boolean IfScanComplated(String str);

    Map<String, Object> getReClipWaitUpdatePageList(String str, String str2, String str3, String str4, String str5);

    @Transaction
    boolean changePage(String str, String str2, String str3, String str4, boolean z);

    @Transaction
    boolean batchChangePage(Object[][] objArr);

    @Transaction
    Map<String, Object> getOneUploadRecord(String str, String str2, String str3);

    @Transaction
    Map<String, Object> getOneUploadRecordById(String str, String str2, String str3, String str4);

    List<Map<String, Object>> getReClipRecordList(String str, String str2, String str3);

    List<Map<String, Object>> getAllReClipImageList(String str, String str2, String str3);

    Map<String, Object> AutoVerifyTestScanImage(String str, String str2, String str3, String str4);

    List<Map<String, Object>> getStudentPaperImageByRedId(String str);

    Integer getKaoHaoJiaoDuiZhuangTai(String str, String str2, String str3, String str4);

    @Transaction
    boolean MoveSubject(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    boolean AutomaticAuditTestScanImage(String str, String str2, String str3, String str4);

    Map<String, String> AutomaticAuditTestScanImageSetting(String str, String str2, String str3, String str4);

    boolean IfCloseTestingCenterSecondaryPositioning(String str);

    List<Long> GetBatchReClipIdList(String str, List<String> list);

    Map<String, String> getScanProcess(String str, String str2);

    @Transaction
    boolean DeleteScanTest(String str);

    int getPairingErrorRecord(String str);

    @Transaction
    List<Map<String, Object>> doPairingErrorRecord(String str, List<Map<String, Object>> list);

    boolean ForceMakeSelfTemplate(String str);
}
