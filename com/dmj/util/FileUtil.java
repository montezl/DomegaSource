package com.dmj.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/* loaded from: FileUtil.class */
public class FileUtil {
    public static byte[] getFileByteArray(String filePath) {
        byte[] data = new byte[0];
        try {
            InputStream input = new FileInputStream(filePath);
            int i = input.available();
            data = new byte[i];
            input.read(data);
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static byte[] splitimgurl(String oneurl, String endurl) {
        String file = oneurl + "/" + endurl;
        byte[] data = new byte[0];
        try {
            InputStream input = new FileInputStream(file);
            int i = input.available();
            data = new byte[i];
            input.read(data);
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delAllFile(String path) {
        File temp;
        boolean flag = false;
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            String[] tempList = file.list();
            for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    delAllFile(path + "/" + tempList[i]);
                    delFolder(path + "/" + tempList[i]);
                    flag = true;
                }
            }
            return flag;
        }
        return false;
    }

    public static byte[] toByteArray(InputStream input) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (true) {
                int n = input.read(buffer);
                if (-1 != n) {
                    output.write(buffer, 0, n);
                } else {
                    return output.toByteArray();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (true) {
            int len = inputStream.read(buffer);
            if (len != -1) {
                bos.write(buffer, 0, len);
            } else {
                bos.close();
                return bos.toByteArray();
            }
        }
    }

    public static void compressAllZip(String dirPath, String srcName, String targetName) {
        if (null == dirPath || "".equals(dirPath)) {
            return;
        }
        File baseDir = new File(dirPath);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return;
        }
        String basicRootDir = baseDir.getAbsolutePath();
        File targetFile = new File(targetName);
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetFile));
            out.setEncoding("gbk");
            if (srcName.equals("*")) {
                compressDirToZip(basicRootDir, baseDir, out);
            } else {
                File file = new File(baseDir, srcName);
                if (file.isFile()) {
                    compressFileToZip(basicRootDir, file, out);
                } else {
                    compressDirToZip(basicRootDir, file, out);
                }
            }
            out.close();
        } catch (IOException e) {
        }
    }

    private static void compressDirToZip(String basicRootDir, File dir, ZipOutputStream out) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files.length == 0) {
                ZipEntry entry = new ZipEntry(getFileName(basicRootDir, dir));
                try {
                    out.putNextEntry(entry);
                    out.closeEntry();
                    return;
                } catch (IOException e) {
                    return;
                }
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    compressFileToZip(basicRootDir, files[i], out);
                } else {
                    compressDirToZip(basicRootDir, files[i], out);
                }
            }
        }
    }

    private static void compressFileToZip(String basicRootDir, File file, ZipOutputStream out) {
        byte[] buffer = new byte[4096];
        if (file.isFile()) {
            try {
                FileInputStream in = new FileInputStream(file);
                ZipEntry entry = new ZipEntry(getFileName(basicRootDir, file));
                out.putNextEntry(entry);
                while (true) {
                    int bytes_read = in.read(buffer);
                    if (bytes_read != -1) {
                        out.write(buffer, 0, bytes_read);
                    } else {
                        out.closeEntry();
                        in.close();
                        return;
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    private static String getFileName(String basicRootDir, File file) {
        if (!basicRootDir.endsWith(File.separator)) {
            basicRootDir = basicRootDir + File.separator;
        }
        String filePath = file.getAbsolutePath();
        if (file.isDirectory()) {
            filePath = filePath + "/";
        }
        int index = filePath.indexOf(basicRootDir);
        return filePath.substring(index + basicRootDir.length());
    }

    public static void main(String[] args) {
        delFolder("C:/Users/Administrator/Desktop/小小题 - 副本");
    }
}
