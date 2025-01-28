<%@ page language="java" import="java.util.*,com.dmj.util.Const,com.dmj.util.QRCode.QRUtil" pageEncoding="utf-8"%>
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.util.Map,com.dmj.util.StaticClassResources" %>
<%@ page contentType="text/html; charset=utf-8"%>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","No-cache");

	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%
	Map<String,String> skinSetting =StaticClassResources.SkinSetting;
	String systemTitle =skinSetting.get("systemTitle");
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
 <head>
	<title>欢迎界面</title>
	<base href="<%=basePath%>"/>
	<META http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<script type="text/javascript" src="<%=basePath%>common/js/jquery.js"></script>
	<link rel="stylesheet" href="<%=basePath%>common/css/style.css" type="text/css" />
	<script type="text/javascript"> 
	</script> 
	<style type="text/css">

	</style>
 </head>
<body >
	<div id="man_zone" style="margin: 15px">
	  <table width="99%" border="0" align="center"  cellpadding="3" cellspacing="1" class="table_style">
	    <tr>
	      <td style="font-size:20px;color:green;">欢迎使用<%=systemTitle%></td>
	    </tr>
	  </table>
	 </div>

</body>
</html>


