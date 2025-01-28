package com.dmj.util;

import com.alibaba.fastjson.JSON;
import com.dmj.domain.User;
import com.dmj.service.userManagement.UserService;
import com.dmj.serviceimpl.userManagement.UserServiceImpl;
import com.dmj.util.msg.RspMsg;
import com.zht.db.ServiceFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/* loaded from: SfHelper.class */
public class SfHelper {
    UserService userService = (UserService) ServiceFactory.getObject(new UserServiceImpl());

    /* JADX WARN: Finally extract failed */
    public RspMsg verifyCharge(String openocs, String systemid, String user_id, String ziyuan_num) {
        RspMsg msg;
        if (openocs.equals("1")) {
            String payUrl = this.userService.getpublicip("10");
            String _url = payUrl + "/validate/isAuthority";
            String params = "systemid=" + systemid + "&user_id=" + user_id + "&ziyuan_num=" + ziyuan_num;
            HttpURLConnection connection = null;
            InputStream is = null;
            OutputStream os = null;
            BufferedReader br = null;
            try {
                try {
                    URL url = new URL(_url);
                    HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
                    connection2.setRequestMethod("POST");
                    connection2.setConnectTimeout(10000);
                    connection2.setReadTimeout(60000);
                    connection2.setDoOutput(true);
                    connection2.setDoInput(true);
                    connection2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    connection2.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
                    OutputStream os2 = connection2.getOutputStream();
                    os2.write(params.getBytes("UTF-8"));
                    if (connection2.getResponseCode() == 200) {
                        is = connection2.getInputStream();
                        br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        StringBuffer sbf = new StringBuffer();
                        while (true) {
                            String temp = br.readLine();
                            if (temp == null) {
                                break;
                            }
                            sbf.append(temp);
                            sbf.append("\r\n");
                        }
                        String result = sbf.toString();
                        msg = (RspMsg) JSON.parseObject(result, RspMsg.class);
                    } else if (connection2.getResponseCode() == 408) {
                        msg = new RspMsg(408, "网络过慢，请稍后重试！", null);
                    } else {
                        msg = new RspMsg(connection2.getResponseCode(), "收费服务器连接异常【" + connection2.getResponseMessage() + "】", null);
                    }
                    if (null != br) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != os2) {
                        try {
                            os2.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (null != is) {
                        try {
                            is.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    connection2.disconnect();
                } catch (Exception e4) {
                    e4.printStackTrace();
                    msg = new RspMsg(e4.hashCode(), "收费服务器异常【" + e4.getMessage() + "】", null);
                    if (0 != 0) {
                        try {
                            br.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                    if (0 != 0) {
                        try {
                            os.close();
                        } catch (IOException e6) {
                            e6.printStackTrace();
                        }
                    }
                    if (0 != 0) {
                        try {
                            is.close();
                        } catch (IOException e7) {
                            e7.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        br.close();
                    } catch (IOException e8) {
                        e8.printStackTrace();
                    }
                }
                if (0 != 0) {
                    try {
                        os.close();
                    } catch (IOException e9) {
                        e9.printStackTrace();
                    }
                }
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (IOException e10) {
                        e10.printStackTrace();
                    }
                }
                connection.disconnect();
                throw th;
            }
        } else {
            msg = new RspMsg(200, "不开放家长收费系统", null);
        }
        return msg;
    }

    public List<String> verifyChargeStudent(String openocs, String systemid, String studentList, String ziyuan_num) {
        List<String> tempList = new ArrayList<>();
        if (openocs.equals("1")) {
            String payUrl = this.userService.getpublicip("10");
            String _url = payUrl + "/validate/verifyChargeStudent";
            String params = "systemid=" + systemid + "&studentList=" + studentList + "&ziyuan_num=" + ziyuan_num;
            HttpURLConnection connection = null;
            InputStream is = null;
            OutputStream os = null;
            BufferedReader br = null;
            try {
                try {
                    URL url = new URL(_url);
                    HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
                    connection2.setRequestMethod("POST");
                    connection2.setConnectTimeout(2000);
                    connection2.setReadTimeout(60000);
                    connection2.setDoOutput(true);
                    connection2.setDoInput(true);
                    connection2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection2.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
                    OutputStream os2 = connection2.getOutputStream();
                    os2.write(params.getBytes());
                    if (connection2.getResponseCode() == 200) {
                        is = connection2.getInputStream();
                        br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        StringBuffer sbf = new StringBuffer();
                        while (true) {
                            String temp = br.readLine();
                            if (temp == null) {
                                break;
                            }
                            sbf.append(temp);
                        }
                        String result = sbf.toString();
                        String[] li = result.substring(1, result.length() - 1).split(Const.STRING_SEPERATOR);
                        for (String str : li) {
                            tempList.add(str);
                        }
                    } else if (connection2.getResponseCode() == 408) {
                        tempList.add("网络过慢，请稍后重试！");
                    } else {
                        tempList.add("收费服务器连接异常");
                    }
                    if (null != br) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != os2) {
                        try {
                            os2.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (null != is) {
                        try {
                            is.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    connection2.disconnect();
                } catch (Exception e4) {
                    e4.printStackTrace();
                    tempList.add("收费服务器连接异常");
                    if (0 != 0) {
                        try {
                            br.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                    if (0 != 0) {
                        try {
                            os.close();
                        } catch (IOException e6) {
                            e6.printStackTrace();
                        }
                    }
                    if (0 != 0) {
                        try {
                            is.close();
                        } catch (IOException e7) {
                            e7.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        br.close();
                    } catch (IOException e8) {
                        e8.printStackTrace();
                    }
                }
                if (0 != 0) {
                    try {
                        os.close();
                    } catch (IOException e9) {
                        e9.printStackTrace();
                    }
                }
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (IOException e10) {
                        e10.printStackTrace();
                    }
                }
                connection.disconnect();
                throw th;
            }
        }
        return tempList;
    }

    public RspMsg verifyStudentOrParent(HttpServletRequest request) {
        RspMsg msg;
        HttpSession session = request.getSession(true);
        String logType = (String) session.getAttribute("logType");
        User user = new CommonUtil().getLoginUser(request);
        String usertype = user.getUsertype();
        String stuImgFlag = String.valueOf(request.getAttribute("stuImgFlag"));
        if ("T".equals(stuImgFlag)) {
            msg = new RspMsg(200, "可查看图片", null);
        } else if ("P".equals(logType) || "2".equals(usertype)) {
            ServletContext servletContext = session.getServletContext();
            String openocs = (String) servletContext.getAttribute("openOcs");
            String systemid = (String) servletContext.getAttribute("systemId");
            String user_id = (String) session.getAttribute("xuejihao");
            String ziyuan_num = request.getParameter("ziyuan_num");
            if (ziyuan_num.equals("60100")) {
                msg = new RspMsg(200, "S0不收费！", null);
            } else {
                msg = verifyCharge(openocs, systemid, user_id, ziyuan_num);
            }
        } else {
            msg = new RspMsg(200, "非家长或学生用户", null);
        }
        return msg;
    }

    public RspMsg getActiveUserInfo(String systemid) {
        RspMsg msg;
        String payUrl = this.userService.getpublicip("10");
        String _url = payUrl + "/userInfo/active";
        String params = "systemId=" + systemid;
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        try {
            try {
                URL url = new URL(_url);
                HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
                connection2.setRequestMethod("POST");
                connection2.setConnectTimeout(30000);
                connection2.setReadTimeout(600000);
                connection2.setDoOutput(true);
                connection2.setDoInput(true);
                connection2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                connection2.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
                OutputStream os2 = connection2.getOutputStream();
                os2.write(params.getBytes("UTF-8"));
                if (connection2.getResponseCode() == 200) {
                    is = connection2.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuffer sbf = new StringBuffer();
                    while (true) {
                        String temp = br.readLine();
                        if (temp == null) {
                            break;
                        }
                        sbf.append(temp);
                        sbf.append("\r\n");
                    }
                    String result = sbf.toString();
                    msg = new RspMsg(200, "获取成功", result);
                } else if (connection2.getResponseCode() == 408) {
                    msg = new RspMsg(408, "网络过慢，请稍后重试！", null);
                } else {
                    msg = new RspMsg(connection2.getResponseCode(), "收费服务器连接异常【" + connection2.getResponseMessage() + "】", null);
                }
                if (null != br) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != os2) {
                    try {
                        os2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                connection2.disconnect();
            } catch (Exception e4) {
                e4.printStackTrace();
                msg = new RspMsg(e4.hashCode(), "收费服务器异常【" + e4.getMessage() + "】", null);
                if (0 != 0) {
                    try {
                        br.close();
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                }
                if (0 != 0) {
                    try {
                        os.close();
                    } catch (IOException e6) {
                        e6.printStackTrace();
                    }
                }
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (IOException e7) {
                        e7.printStackTrace();
                    }
                }
                connection.disconnect();
            }
            return msg;
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    br.close();
                } catch (IOException e8) {
                    e8.printStackTrace();
                }
            }
            if (0 != 0) {
                try {
                    os.close();
                } catch (IOException e9) {
                    e9.printStackTrace();
                }
            }
            if (0 != 0) {
                try {
                    is.close();
                } catch (IOException e10) {
                    e10.printStackTrace();
                }
            }
            connection.disconnect();
            throw th;
        }
    }

    public RspMsg getTried(String systemId, String studentId) {
        RspMsg msg;
        String payUrl = this.userService.getpublicip("10");
        String _url = payUrl + "/dingdan/Tried";
        String params = "systemId=" + systemId + "&userId=" + studentId;
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        try {
            try {
                URL url = new URL(_url);
                HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
                connection2.setRequestMethod("POST");
                connection2.setConnectTimeout(30000);
                connection2.setReadTimeout(600000);
                connection2.setDoOutput(true);
                connection2.setDoInput(true);
                connection2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                connection2.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
                OutputStream os2 = connection2.getOutputStream();
                os2.write(params.getBytes("UTF-8"));
                if (connection2.getResponseCode() == 200) {
                    is = connection2.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuffer sbf = new StringBuffer();
                    while (true) {
                        String temp = br.readLine();
                        if (temp == null) {
                            break;
                        }
                        sbf.append(temp);
                    }
                    String result = sbf.toString();
                    msg = new RspMsg(200, "获取成功", result);
                } else if (connection2.getResponseCode() == 408) {
                    msg = new RspMsg(408, "网络过慢，请稍后重试！", null);
                } else {
                    msg = new RspMsg(connection2.getResponseCode(), "收费服务器连接异常【" + connection2.getResponseMessage() + "】", null);
                }
                if (null != br) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != os2) {
                    try {
                        os2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                connection2.disconnect();
            } catch (Exception e4) {
                e4.printStackTrace();
                msg = new RspMsg(e4.hashCode(), "收费服务器异常【" + e4.getMessage() + "】", null);
                if (0 != 0) {
                    try {
                        br.close();
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                }
                if (0 != 0) {
                    try {
                        os.close();
                    } catch (IOException e6) {
                        e6.printStackTrace();
                    }
                }
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (IOException e7) {
                        e7.printStackTrace();
                    }
                }
                connection.disconnect();
            }
            return msg;
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    br.close();
                } catch (IOException e8) {
                    e8.printStackTrace();
                }
            }
            if (0 != 0) {
                try {
                    os.close();
                } catch (IOException e9) {
                    e9.printStackTrace();
                }
            }
            if (0 != 0) {
                try {
                    is.close();
                } catch (IOException e10) {
                    e10.printStackTrace();
                }
            }
            connection.disconnect();
            throw th;
        }
    }
}
