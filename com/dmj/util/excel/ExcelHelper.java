package com.dmj.util.excel;

import cn.hutool.core.convert.Convert;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* loaded from: ExcelHelper.class */
public class ExcelHelper {
    private File file;
    private boolean isExcelXls;

    public ExcelHelper(File file) {
        this.file = file;
        this.isExcelXls = file.getName().toLowerCase().endsWith("xls");
    }

    public Workbook creatWorkbook() {
        InputStream input = null;
        try {
            try {
                InputStream input2 = new FileInputStream(this.file);
                if (this.isExcelXls) {
                    HSSFWorkbook book = new HSSFWorkbook(input2);
                    if (input2 != null) {
                        try {
                            input2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return book;
                }
                XSSFWorkbook book2 = new XSSFWorkbook(input2);
                if (input2 != null) {
                    try {
                        input2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                return book2;
            } catch (Exception e3) {
                throw new RuntimeException(e3);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    input.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public ClientAnchor creatClientAnchor(Cell cell) {
        int cellColumnIndex = cell.getColumnIndex();
        int cellRowIndex = cell.getRowIndex();
        if (this.isExcelXls) {
            return new HSSFClientAnchor(0, 0, 0, 0, (short) cellColumnIndex, cellRowIndex, (short) (cellColumnIndex + 5), cellRowIndex + 4);
        }
        return new XSSFClientAnchor(0, 0, 0, 0, (short) cellColumnIndex, cellRowIndex, (short) (cellColumnIndex + 5), cellRowIndex + 4);
    }

    public RichTextString creatRichTextString(String content) {
        if (this.isExcelXls) {
            return new HSSFRichTextString(content);
        }
        return new XSSFRichTextString(content);
    }

    public static void setCellValue(Cell cell, Object value) {
        if (null == value) {
            cell.setCellValue("");
            return;
        }
        if (value instanceof String) {
            cell.setCellValue(Convert.toStr(value, ""));
            return;
        }
        if (value instanceof Integer) {
            cell.setCellValue(Convert.toInt(value, 0).intValue());
            return;
        }
        if (value instanceof Double) {
            cell.setCellValue(Convert.toDouble(value, Double.valueOf(0.0d)).doubleValue());
            return;
        }
        if (value instanceof Float) {
            cell.setCellValue(Convert.toFloat(value, Float.valueOf(0.0f)).floatValue());
        } else if (value instanceof Date) {
            cell.setCellValue(Convert.toDate(value, new Date()));
        } else {
            cell.setCellValue("--");
        }
    }

    public static String formatPercent(Object fenzi, Object fenmu, DecimalFormat df, String defaultVal) {
        if (Convert.toInt(fenmu, 0).intValue() == 0) {
            return defaultVal;
        }
        if ((fenzi instanceof Double) || (fenmu instanceof Double)) {
            return df.format((Convert.toDouble(fenzi, Double.valueOf(0.0d)).doubleValue() / Convert.toDouble(fenmu).doubleValue()) * 100.0d) + "%";
        }
        if ((fenzi instanceof Integer) || (fenmu instanceof Integer)) {
            return df.format((Convert.toInt(fenzi, 0).intValue() / Convert.toInt(fenmu).intValue()) * 100) + "%";
        }
        if ((fenzi instanceof Float) || (fenmu instanceof Float)) {
            return df.format((Convert.toFloat(fenzi, Float.valueOf(0.0f)).floatValue() / Convert.toFloat(fenmu).floatValue()) * 100.0f) + "%";
        }
        if ((fenzi instanceof BigDecimal) || (fenmu instanceof BigDecimal)) {
            int scale = df.getMaximumFractionDigits();
            return df.format(Convert.toBigDecimal(fenzi, BigDecimal.valueOf(0L)).multiply(BigDecimal.valueOf(100L)).divide(Convert.toBigDecimal(fenmu), scale, 4)) + "%";
        }
        return defaultVal;
    }
}
