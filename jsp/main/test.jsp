<%@ page language="java" import="java.util.*" pageEncoding="utf-8"
	contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "No-cache");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript"	src="../../common/js/jquery.js"></script>
<script type="text/javascript" src="../../common/js/lhgdialog/lhgdialog.min.js?skin=mac_cxj"></script>
<title>管理导航区域</title>
<script  type="text/javascript"  defer="defer">
var dg;
//tt();
function tt(){
	
	 alert("aaaaaa");
	 alert("cccc");
	 dg = new $.dialog({
				id : 'test',
				content : '112255455jkl;sdfk hklfjhkidfhhj4',
				title : 'kgjsldkjg',
		    	cancel: false /*为true等价于function(){}*/,
		    	width: '330px',
		    	height:'524px',
		    	top:0,
		    	left:0
			});
}
</script>
</head>
<body>

</body>
</html>
