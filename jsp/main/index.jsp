<%@ page contentType="text/html; charset=utf-8" language="java"	import="com.dmj.util.StaticClassResources,com.dmj.util.app.EdeiInfo"%>
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String sId=null;
	if(session!=null){
		sId=session.getId();
	}
	Map<String,String> skinSetting =StaticClassResources.SkinSetting;
	EdeiInfo edeiInfo = StaticClassResources.EdeiInfo;
	String systemTitle =skinSetting.get("systemTitle");
	String systemName = edeiInfo.getVendor();
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title><%=systemName%></title>

		<base href="<%=basePath%>" />
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
		<meta http-equiv="pragma" content="no-cache"/>
		<meta http-equiv="cache-control" content="no-cache"/>
		<meta http-equiv="expires" content="0"/>
		<script type="text/javascript"	src="<%=basePath%>common/js/jquery.js"></script>
		<%-- <script type="text/javascript" src="<%=basePath%>common/js/fst4-style.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/menu.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/menuG5/script/menuG5LoaderFSX.js"></script> --%>
		
		
		<link rel="shortcut icon" href="<%=path%>common/image/main/favicon.ico" />
<script type="text/javascript" src="<%=basePath%>common/js/lhgdialog/lhgdialog.min.js?skin=mac_cxj"></script>
<script type="text/javascript" src="<%=basePath%>common/js/jquery.form.js"></script>
<script type="text/javascript" src="<%=basePath%>common/js/lhgdialog/lhgdialog.min.js?skin=mac"></script>
<script type="text/javascript"	>
	
	$(function(){
// 		dg = new $.dialog({
// 		id : '11',
// 		content : 'url: teacherXindeAction!toTeacherXinde.action',
// 		title : '<span class="addGroup"></span>&nbsp;添加题组',
// 		cancelval: '关闭',

//     	width: '450px',
//     	height: '180px'
// 	});
// 	dg.ShowDialog();
});
//    	cancel: true ,
//     	lock : true,
//     	cache : false,
//     	max : false,
// 		min : false,
</script>
		<script type="text/javascript">
$().ready(function(){

// 		$.ajax({
// 			type : "POST",//用POST方式传输
// 			cache : false,
// 			async : false,
// 			dataType : "html",//数据格式:JSON
// 			url : 'loginAction!getRolesYjy.action', //目标地址
// 			success : function(data) {
// 			var mark=$("#mark").val();
// 				if(data!=0 && mark==0){
// 					var ifr = document.getElementById('manframes');
// 				ifr.src ="<%=basePath%>jsp/awardPoint/awardPointlist.jsp";
// 				}
// 			}
// 	});
});
</script>
<script language="javascript" type="text/javascript">


// 	---页面关闭时立即销毁session，而不等待session自动过期---------开始
//     window.onbeforeunload=function checkLeave(e){ 
//     	var session_Id="<%=sId%>";
//     	var evt = e ? e : (window.event ? window.event : null);  //此方法为了在firefox中的兼容  
    	
//     	if(session_Id!=null){
//     		if(true){
// 	        	alert("evt.returnValue:"+evt.returnValue);//---您已退出系统 ,是否关闭页面
// 				if(navigator.userAgent.indexOf("MSIE")>0){//判断浏览器是否属于ie
// 					alert("ie");
// 			    }else 
// 			    if(navigator.userAgent.indexOf("Firefox")>0){//判断浏览器是否属于ff
// 				    evt.returnValue="退出系统..";
// 			        var browser=navigator.appName;
// 			        var b_version=navigator.appVersion;
// 			        var version=b_version.split(";");
// 			        var trim_Version=version[1].replace(/[ ]/g,"");
// 			        if(browser=="Microsoft Internet Explorer" && trim_Version=="MSIE7.0"){
// 			        	alert("IE 7.0");
// 			        }else if(browser=="Microsoft Internet Explorer" && trim_Version=="MSIE6.0"){
// 			        	alert("IE 6.0");
// 			        }
// 			    }
// 	        	document.location="loginAction!loginOut.action";
// 	        }  
//     	}
	    
        
//     } ;
    
    
//     window.onbeforeunload=function checkLeave(e){ 
//     	var session_Id="<%=sId%>";
//     	var evt = e ? e : (window.event ? window.event : null);  //此方法为了在firefox中的兼容  
//     	if(session_Id!=null){
//     		if(true){
// 			    if(navigator.userAgent.indexOf("Firefox")>0){//判断浏览器是否属于ff
// 				    evt.returnValue="退出系统..";
// 			    }
// 			    alert("guan bi");
// 	        	document.location="loginAction!loginOut.action";
// 	        }  
//     	}
//     } ;
    // 	---页面关闭时立即销毁session，而不等待session自动过期---------结束

	


</script>

<%--		<script type="text/javascript" src="../../common/js/lhgdialog/lhgdialog.min.js?skin=mac_cxj"></script>--%>

<script  type="text/javascript"  defer="defer">
var   scriptPath = "../../common/js/menuG5";   
var   contentScript = "../../common/js/menu.js";   
var   styleScript = "../../common/js/fst4-style.js" ; 
// var dg;
//tt();
// function tt(){
// 	 dg = new $.dialog({
// 				id : 'test',
// 				content : '112255455jkl;ssklhjdfk hklfjhkidfhhj4',
// 				title : 'kgjsldkjg',
// 		    	cancel: false /*为true等价于function(){}*/,
// 		    	width: '330px',
// 		    	height:'524px',
// 		    	top:0,
// 		    	left:0
// 	});
// }
</script>

</head>
<input type="hidden" value="${sessionScope.mark}" id="mark"></input>
<input type="hidden" value="${sessionScope.logType}" id ="logType" />

<frameset id="parentFrame" name="parentFrame" rows="55,7,*" cols="*" frameborder="no" border="0" framespacing="0">
	  <frame src="jsp/main/topframe.jsp" name="topFrame"  scrolling="no" noresize="noresize" id="topFrame" title="topFrame" />
 		<frame  src="jsp/main/switchframetop.html" name="midFrametop" frameborder="no" scrolling="no" noresize="noresize" id="midFrametop" title="midFrametop" />
<!-- 	  jsp/main/teachleftMenu.jsp -->
	  <frameset name="myFrame" id="myFrame" cols="199,7,*" frameborder="no" border="0" framespacing="0">
		   
		    <frame  src="jsp/main/teachleftMenu.jsp" name="leftFrame" frameborder="no" scrolling="no" noresize="noresize" id="leftFrame" title="leftFrame" />
			<frame  src="jsp/main/switchframe.html" name="midFrame" frameborder="no" scrolling="no" noresize="noresize" id="midFrame" title="midFrame" />
		    <frameset  name="left" id="left" rows="40,*" cols="*" frameborder="no" border="0" framespacing="0">
		         <frame src="jsp/main/mainframe.jsp" name="mainFrame" frameborder="no" scrolling="no"  noresize="noresize" id="mainFrame" title="mainFrame" />
		         <frame src="jsp/main/manframe.jsp" id="manframes" name="manFrame" frameborder="no"  title="manFrame" />
		     </frameset>
	  </frameset>
	  
</frameset>

<noframes>
<body></body>

<frame src=""></frame>  
   

 <form id="form1" runat="server">

      <div>
      </div>
  </form>   


<%--<body>--%>
<%--<%@include file="/jsp/main/test.jsp" %>	--%>
<%--</body>--%>

</noframes>
  
</html>
