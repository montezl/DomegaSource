package com.dmj.auth.util;

import com.dmj.auth.bean.License;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* loaded from: SystemUtil.class */
public class SystemUtil {
    public static Map getSystemInfo() {
        Map<String, String> map = new HashMap<>();
        String sMAC = "";
        try {
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            System.out.println("ip:" + ip);
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            ni.getInetAddresses().nextElement().getAddress();
            byte[] mac = ni.getHardwareAddress();
            System.out.println("mac:" + mac);
            if (null != mac) {
                Formatter formatter = new Formatter();
                int i = 0;
                while (i < mac.length) {
                    Locale locale = Locale.getDefault();
                    Object[] objArr = new Object[2];
                    objArr[0] = Byte.valueOf(mac[i]);
                    objArr[1] = i < mac.length - 1 ? "-" : "";
                    sMAC = formatter.format(locale, "%02X%s", objArr).toString();
                    i++;
                }
            }
            String serialNumber = getCPUSerial();
            if (null == serialNumber) {
                serialNumber = "";
            }
            map.put(License.IP, ip);
            map.put(License.MAC, sMAC);
            map.put(License.SERIALNUMBER, serialNumber);
            return map;
        } catch (SocketException e) {
            e.printStackTrace();
            return map;
        } catch (UnknownHostException e2) {
            e2.printStackTrace();
            return map;
        }
    }

    public static String getMotherboardSN() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);
            fw.write("Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\nSet colItems = objWMIService.ExecQuery _ \n   (\"Select * from Win32_BaseBoard\") \nFor Each objItem in colItems \n    Wscript.Echo objItem.SerialNumber \n    exit for  ' do the first cpu only! \nNext \n");
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (true) {
                String line = input.readLine();
                if (line != null) {
                    result = result + line;
                } else {
                    input.close();
                    return result.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getHardDiskSN(String drive) {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);
            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\nSet colDrives = objFSO.Drives\nSet objDrive = colDrives.item(\"" + drive + "\")\nWscript.Echo objDrive.SerialNumber";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (true) {
                String line = input.readLine();
                if (line != null) {
                    result = result + line;
                } else {
                    input.close();
                    return result.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCPUSerial() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return getCPUSerialByWindows();
        }
        return getCPUSerialByLinux();
    }

    public static String getCPUSerialByLinux() {
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "sudo dmidecode -t 4 | grep ID");
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                System.out.println("line:" + line);
                if (!line.trim().equals("")) {
                    String cpu = line.substring(line.indexOf("ID:")).replaceAll(" ", "");
                    System.out.println("cpu:" + cpu);
                    output.append(cpu);
                    break;
                }
            }
            reader.close();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("获取cpu序列号失败");
                return "";
            }
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (InterruptedException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public static String getCPUSerialByWindows() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);
            fw.write("Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\nSet colItems = objWMIService.ExecQuery _ \n   (\"Select * from Win32_Processor\") \nFor Each objItem in colItems \n    Wscript.Echo objItem.ProcessorId \n    exit for  ' do the first cpu only! \nNext \n");
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (true) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }
                result = result + line;
            }
            input.close();
            file.delete();
            if (result.trim().length() < 1 || result == null) {
                return null;
            }
            return result.trim();
        } catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
    }

    public static List<String> getMacAddressByDos() {
        List<String> macs = new ArrayList<>();
        try {
            Process start = Runtime.getRuntime().exec("cmd /c ipconfig /all");
            BufferedReader reader = new BufferedReader(new InputStreamReader(start.getInputStream(), "gb2312"));
            reader.readLine();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.toLowerCase().indexOf("physical address") > 0 || line.toLowerCase().indexOf("物理地址") > 0) {
                    int index = line.indexOf(":");
                    String macAddress = line.substring(index + 2);
                    macs.add(macAddress.replace('-', ':'));
                }
                reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < macs.size(); i++) {
        }
        return macs;
    }

    public static void main(String[] args) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isVirtual() && networkInterface.getHardwareAddress() != null) {
                    byte[] mac = networkInterface.getHardwareAddress();
                    StringBuilder sb = new StringBuilder();
                    int i = 0;
                    while (i < mac.length) {
                        Object[] objArr = new Object[2];
                        objArr[0] = Byte.valueOf(mac[i]);
                        objArr[1] = i < mac.length - 1 ? "-" : "";
                        sb.append(String.format("%02X%s", objArr));
                        i++;
                    }
                    System.out.println("MAC Address: " + sb.toString());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
