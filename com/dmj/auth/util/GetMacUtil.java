package com.dmj.auth.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: GetMacUtil.class */
public final class GetMacUtil {
    private static Pattern macPattern = Pattern.compile(".*(([0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*", 2);
    static final String[] windowsCommand = {"ipconfig", "/all"};
    static final String[] linuxCommand = {"/sbin/ifconfig", "-a"};

    public static final List getMacAddresses() throws IOException {
        List macAddressList = new ArrayList();
        BufferedReader reader = getMacAddressesReader();
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                Matcher matcher = macPattern.matcher(line);
                if (matcher.matches()) {
                    macAddressList.add(matcher.group(1).replaceAll("[-:]", ""));
                }
            } else {
                reader.close();
                return macAddressList;
            }
        }
    }

    public static final String getMacAddress() throws IOException {
        return getMacAddress(0);
    }

    public static final String getMacAddress(int nicIndex) throws IOException {
        BufferedReader reader = getMacAddressesReader();
        int nicCount = 0;
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                Matcher matcher = macPattern.matcher(line);
                if (matcher.matches()) {
                    if (nicCount == nicIndex) {
                        reader.close();
                        return matcher.group(1).replaceAll("[-:]", "");
                    }
                    nicCount++;
                }
            } else {
                reader.close();
                return null;
            }
        }
    }

    private static BufferedReader getMacAddressesReader() throws IOException {
        String[] command;
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            command = windowsCommand;
        } else if (os.startsWith("Linux")) {
            command = linuxCommand;
        } else {
            throw new IOException("Unknown operating system: " + os);
        }
        Process process = Runtime.getRuntime().exec(command);
        new 1(process).start();
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    public static List<String> getMacList(int index) {
        List addressList = new ArrayList();
        for (int i = 0; i < index; i++) {
            try {
                addressList.add(getMacAddress(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return addressList;
    }

    public static void main(String[] args) {
        try {
            List<String> list = getMacList(2);
            for (int i = 0; i < list.size(); i++) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
