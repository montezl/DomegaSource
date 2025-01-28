<%@ page language="java" import="java.util.*" pageEncoding="utf-8" contentType="text/html; charset=utf-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%

	String path = request.getContextPath();

	String basePath = request.getScheme() + "://"

			+ request.getServerName() + ":" + request.getServerPort()

			+ path + "/";

%>

<%

response.setHeader("Pragma","No-cache");

response.setHeader("Cache-Control","No-cache");

%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">



<html>

  <head>

	<META http-equiv="Content-Type" content="text/html; charset=utf-8">

	<meta http-equiv="pragma" content="no-cache"/>

	<meta http-equiv="cache-control" content="no-cache"/>

	<meta http-equiv="expires" content="0"/>    

	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>

	<meta http-equiv="description" content="This is my page"/>

	<!--

	<link rel="stylesheet" type="text/css" href="styles.css"/>

	-->

	<script type="text/javascript" src="<%= basePath %>/common/js/jquery.js"></script>

<link rel="stylesheet" href="../../common/css/common.css" type="text/css" />

</head>

<script language="javascript" type="text/javascript">

	$().ready(function(){

		

		$('#right_main_nav li').click(function(){

			var path = $('#path').val();

			

			var url = $(this).attr('id');

			window.top.frames['manFrame'].location=url;

		});

		

		

		$('.list_detail ul').click(function(){

			

			$(this).css("display","inline-block").css("width","100px").css("background-color", "#C6E1FB").css("border","1px solid #eaeae8");

            $('.list_detail ul').not(this).css("display","inline-block").css("width","100px").css("background-color","").css("border",""); 

	  });

	

	});

	

	

	function hideorshow(divid,subsortid){

	    if(getObject(subsortid).style.display=="none"){

		   getObject(subsortid).style.display="block";

		   getObject(divid).className="list_tilte";

		}else{

		   getObject(subsortid).style.display="none";

		   getObject(divid).className="list_tilte_onclick";

		}

	}

	function getObject(objectId) {

	    if(document.getElementById && document.getElementById(objectId)) {

		// W3C DOM

		return document.getElementById(objectId);

	    } else if (document.all && document.all(objectId)) {

		// MSIE 4 DOM

		return document.all(objectId);

	    } else if (document.layers && document.layers[objectId]) {

		// NN 4 DOM.. note: this won't find nested layers

		return document.layers[objectId];

	    } else {

		return false;

	 }

}



</script>

<body >

<input type="hidden" id="path" value="<%=path %>"/>

<div id="left_content">

     <div id="user_info"><strong><c:out value="${sessionScope.loginuser.username}" /></strong><br />[<a href="#"><c:out value="${sessionScope.loginuser.roleName}" /></a>]</div>

	 <div id="main_nav">

	     <div id="left_main_nav">

	     	<ul>

				<li id="aa" onclick="list_sub_detail('aa','111111')" class=left_back>系统管理</li>

			</ul>

	     </div>  <!--  竖条标题 -->

	     

	     

		 <div id="right_main_nav"> <!--  最外层div  -->

		 

		 	<!-- *************      初始化数据      ****************  -->

		 

		 	<div class="list_tilte" id="init"  onclick="hideorshow('init','init_content')">

				<span>系统管理</span>

			</div> <!-- 二级菜单标题 -->

			

			<div class=list_detail id="init_content">

			

				<ul >

					<li id="areaAction!default.action" >

						<a id="aa" href=#>区信息设置</a>

					</li>

				</ul>

				<ul >

					<li id="schoolAction!listSchool.action" >

						<a id="bb" href=#>学校管理</a>

					</li>

				</ul>

				<ul >

					<li id="basicblogAction!execute.action" >

						<a id="cc" href=#>详细日志</a>

					</li>

				</ul>

				<ul >

					<li id="logAction!default.action" >

						<a id="dd" href=#>访问日志</a>

					</li>

				</ul>

				<ul >

					<li id="examSetAction!default.action" >

						<a id="dd" href=#>考试设置</a>

					</li>

				</ul>

				<ul >

					<li id="teacherAction!teacherinfo.action" >

						<a id="dd" href=#>教师管理</a>

					</li>

				</ul>

				<ul >

					<li id="../../jsp/systemManagement/exportSql.jsp" >

						<a id="dd" href=#>数据导出\导入</a>

					</li>

				</ul>

<!--				<ul >-->

<!--					<li id="../../jsp/systemManagement/dbBackup.jsp" >-->

<!--						<a id="dd" href=#>数据库备份</a>-->

<!--					</li>-->

<!--				</ul>-->

				

			</div> <!-- 装详细菜单div  -->

			

			

			

		 </div>

	 </div>

</div>

</body>

</html>

