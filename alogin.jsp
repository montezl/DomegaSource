<%@ page contentType="text/html; charset=utf-8" language="java"
         import="com.dmj.util.Const,com.dmj.util.StaticClassResources,com.dmj.util.app.EdeiInfo,com.dmj.util.singleLogin.ZkhySingleLoginUtil" %>
<%@ page import="java.util.*" %>
<%@ page import="com.dmj.util.singleLogin.SingleLoginUtil" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
    String lgoinUrl = SingleLoginUtil.getLoginUrl();
    String url = lgoinUrl+"?info="+request.getAttribute("info");

    request.getRequestDispatcher(url).forward(request,response);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <title>登录</title>
    <meta name="renderer" content="webkit"/>
    <base href="<%=basePath%>"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <meta name="renderer" content="webkit"/>

</head>

<body>

</body>
</html>