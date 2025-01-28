package com.dmj.util.excel;

import com.dmj.util.Const;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/* loaded from: CheckCellUtil.class */
public class CheckCellUtil {
    static final short cellBackground_red = 10;
    static final short cellBackground_yellow = 13;

    public static String getCellValue(Cell cell) {
        String _value;
        if (null == cell) {
            return "";
        }
        CellType cellType = cell.getCellType();
        switch (1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()]) {
            case 1:
                _value = cell.getStringCellValue();
                break;
            case 2:
                _value = String.valueOf(cell.getBooleanCellValue());
                break;
            case Const.Pic_Score /* 3 */:
                try {
                    _value = String.valueOf(cell.getNumericCellValue());
                    break;
                } catch (IllegalStateException e) {
                    _value = String.valueOf(cell.getRichStringCellValue());
                    break;
                }
            case 4:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    _value = sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue())).toString();
                    break;
                } else {
                    _value = new DecimalFormat("#.######").format(cell.getNumericCellValue());
                    break;
                }
            case Const.Pic_AbPage /* 5 */:
                _value = "";
                break;
            case Const.Pic_Big /* 6 */:
                _value = "ERROR";
                break;
            default:
                _value = cell.toString();
                break;
        }
        return _value.replace("\u3000", " ").trim();
    }

    public static void setRowStyle(Row row, int columnLen, CellStyle cellStyle) {
        for (int i = 0; i < columnLen; i++) {
            Cell cell = row.getCell(i);
            if (null == cell) {
                cell = row.createCell(i);
            }
            cell.setCellStyle(cellStyle);
        }
        Cell cell17 = row.createCell(columnLen);
        cell17.setCellValue("NG");
    }

    public static void setCellStyle(File file, Sheet sheet, Cell cell, String pizhu, CellStyle cellStyle) {
        ExcelHelper excelHelper = new ExcelHelper(file);
        Comment cellComment = cell.getCellComment();
        if (null != cellComment) {
            String cmStr = cellComment.getString().toString();
            cellComment.setString(excelHelper.creatRichTextString(cmStr + "\n" + pizhu));
        } else {
            ClientAnchor clientAnchor = excelHelper.creatClientAnchor(cell);
            Drawing draw = sheet.createDrawingPatriarch();
            cellComment = draw.createCellComment(clientAnchor);
            cellComment.setString(excelHelper.creatRichTextString(pizhu));
        }
        cell.setCellStyle(cellStyle);
        cell.setCellComment(cellComment);
    }

    public static String isSpecialChar(String str) {
        Pattern p = Pattern.compile("[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?]|\n|\r|\t");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return " _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?\\n回车\\r空格\\t跳格";
        }
        return null;
    }

    public static boolean isDate(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Pattern pat = Pattern.compile("^(?:(?!0000)[0-9]{4}[-/.](?:(?:0[1-9]|1[0-2])[-/.](?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])[-/.](?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$");
        Matcher mat = pat.matcher(date);
        boolean res = mat.matches();
        if (res) {
            Date now = new Date();
            try {
                Date d = df.parse(date.replace("/", "-").replace(".", "-"));
                if (d.getTime() > now.getTime()) {
                    res = false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static boolean isMobile(String mobile) {
        Pattern pat = Pattern.compile("1[3456789]\\d{9}|\\d{3}-?\\d{8}|\\d{4}-\\{7,8}");
        Matcher mat = pat.matcher(mobile);
        return mat.matches();
    }

    public static boolean isEMail(String eMail) {
        Pattern pat = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");
        Matcher mat = pat.matcher(eMail);
        return mat.matches();
    }

    public static void main(String[] args) {
    }
}
