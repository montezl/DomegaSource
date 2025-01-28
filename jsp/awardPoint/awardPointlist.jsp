<%@ page contentType="text/html; charset=utf-8" language="java"
	import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
<title>判分管理</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<script type="text/javascript" src="<%=basePath%>common/js/jquery.js"></script>
<!--<script type="text/javascript"	src="<%=basePath%>common/js/auto.js"></script>-->
<script type="text/javascript"	src="<%=basePath%>common/js/storage/myStorage.js"></script>
<script type="text/javascript"	src="<%=basePath%>/jsp/awardPoint/awardPointlist.js?skin=mac47"></script>
<script type="text/javascript"	src="<%=basePath%>common/js/selectCorrectyh.js"></script>
<script type="text/javascript" src="<%=basePath%>common/js/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet" href="<%=basePath%>common/css/style.css"
	type="text/css" />
<style type="text/css">
body {
	font-size: 12px;
}
.qiehuan{
	HEIGHT:30PX;
	FONT-SIZE:13PX;
	FONT-WEIGHT:BOLD;
	COLOR:#4d6a6d;
	background-color:white
}
	.qiehuan2{
	HEIGHT:30PX;
	FONT-SIZE:13PX;
	FONT-WEIGHT:BOLD;
	COLOR:#4d6a6d;
	background-color:#F3C1A7;
}
select {
	height: 20px;
	margin: 0px;
	padding: 0px;
}

input {
	width: 50px;
}

img {
	width: 50px;
	height: 50px;
}

.select_line {
	height: 60px;
	line-height: 60px;
	color: black;
	background-color: #F5F5F5;
	font-weight: bold;
}

.title3 {
	width: 100%;
	height: 30px;
	color: #fff;
	/*background-color:#fac47e;*/
	/*background-color:#f8d8ae;*/
	background-color: #e0dfe2;
	line-height: 30px;
	font-size: 15px;
	font-weight: bold;
	border: 0px solid #c6c0b9;
}
.menu_pre{
		/*position: fixed; 
		left:10%; 
		top:240px;
		font-size:12px;
		width:100px;
		height:120px;*/
	    opacity:60;
		filter:alpha(opacity=60);
		position:fixed; 
		top:80; 
		left:20%; 
		_position:absolute; 
		_top:expression((offsetParent.scrollTop)+140); 
	    _left:expression((offsetParent.scrollLeft)+250); 
		_background-attachment: fixed;
		}
ul li {
	margin: 0;
	padding: 0;
}

#tabs ul {
	list-style: none;
}

#tabs ul li {
	float: left;
	text-align: center;
	height: 27px;
	width: 77px;
	line-height: 27px;
	font-size: 14px;
	cursor: pointer;
	overflow: hidden;
}

#tabs1 ul {
	list-style: none;
}

#tabs1 ul li {
	float: left;
	text-align: center;
	height: 27px;
	width: 77px;
	line-height: 27px;
	font-size: 14px;
	cursor: pointer;
	overflow: hidden;
}

#again ul {
	list-style: none;
}

#again ul li {
	float: left;
	text-align: center;
	height: 27px;
	width: 77px;
	line-height: 27px;
	font-size: 14px;
	cursor: pointer;
	overflow: hidden;
}

a:link {
	text-decoration: none;
	color: #EA5A02;
}

a:visited {
	text-decoration: none;
	color: #EA5A02;
}

a:hover {
	text-decoration: none;
	color: blue;
}

a:active {
	text-decoration: none;
	color：white;
}

.blue {
	font-size: 12px;
}
.btn,.btn-primary {
	display: inline-block;
	padding: 3px 8px;
	margin-bottom: 0;
	font-weight: 400;
	line-height: 1.42857143;
	text-align: center;
	white-space: nowrap;
	vertical-align: middle;
	-ms-touch-action: manipulation;
	touch-action: manipulation;
	cursor: pointer;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
	background-image: none;
	border: 1px solid transparent;
	border-radius: 4px;
	background-color: #ccc;
}
.btn a{
	text-decoration:none;
}
</style>
<style type="text/css">
	.header{
		height:30px;
		line-height:30px;
	}
	.header div{
		float:left;
		text-align:center;
		font-size:13px;
		font-weight:bold;
		color:#4d6a6d
	}
	
	.question{
		height:30px;
		line-height:20px;
		margin:5px 0px;
	}
	.question2{
		height:30px;
		line-height:20px;
		margin:5px 0px;
		background-color:#F3C1A7
	}
	.question div{
		float:left;
		text-align:center;
		font-weight:bold;
		color:#EA5A02;
	}
	.question2 div{
		float:left;
		text-align:center;
		font-weight:bold;
		color:#EA5A02;
	}
	.questionSplit{
		border: 2px solid #63b2ee;
		margin:10px 2px;
	}

	.wenziClass{
		/*文字显示不全显示...*/
		white-space:nowrap;
		overflow:hidden;
		text-overflow:ellipsis;
	}
</style>
</head>
<body onunload="closea()">
	<form name="form1" action="awardPointAction.action"  method="post">
		<input type="hidden" id="userType" name="userType" value="" /> <input
			type="hidden" id="SearchType" name="searchType" value="${searchType}" />
		<input type="hidden" id="count" value="" /> <input type="hidden"
			id="exampaperNum1" name="exampaperNum1" value=""/> <input
			type="hidden" id="tabye" name="tabye" value="1"/> <input
			type="hidden" id="exam1" name="exam1" value="${exam}" /> <input
			type="hidden" id="grade1" name="grade1" value="${grade}" /> <input
			type="hidden" id="subject1" name="subject1" value="${subject}" />
			<input  type="hidden" id="tabye1" name="tabye1" value="${tabye1}" />
		    <input  type="hidden" id="tag" name="tag" value="${tag}" />
		    <input type="hidden" id="p_questionNum" name="p_questionNum" value=""/>
		     <input type="hidden" id="isParent" name="isParent" value=""/>
		     <input type="hidden" id="subType" name="subType" value="0"/>
		     <input type="hidden" id="preexamscore" name="preexamscore" value=""/>
		<div id="tabs">
			<ul>
				<li class="select" id="examALL">判卷</li>
			</ul>
		</div>
		<div id="tabs1" style="display:none;">
			<ul>
				<li style="width: 16px; height: 27px; float: left;"></li>
				<li class="noselect" id="ex">裁决</li>
			</ul>
		</div>
		<div id="again" style="display:none;">
			<ul>
				<li style="width: 16px; height: 27px; float: left;"></li>
				<li class="noselect" id="againpan">重判</li>
			</ul>
		</div>
		<div style="clear: both"></div>
		<p class="line" style="margin-left:5px;width:100%;"></p>
		<div style="padding:10px" style="background-color;">
			<span id="auto"> 考试：<select name="exams" id="exams" class="examSelect">

			</select><span style="padding:0 10px"></span> 年级：<select name="gradeNum"
				id="grade">
			</select><span style="padding:0 10px"></span> 科目：<select name="subjectNum"
				id="subject">

			</select> <span style="padding:0 10px"></span></span>

		</div>
		<br>
<!-- <div id="jz"></div> -->
		<!-- <table
			style="margin-left:10px; width:70%;  border: 2px #C9885B solid;"
			id="table1">
			<tbody id="examscore"></tbody>
		</table> -->
		<div style="margin-left:10px; width:60%;  border: 2px #C9885B solid;" id="table1">
			<div class="header">
				<!-- <div style="width:10%">题号</div>
				<div style="width:28%">总工作量</div>
				<div style="width:15%;">&nbsp;</div>
				<div style="width:28%">个人工作量</div>
				<div style="width:15%">&nbsp;</div>
				<div style="width:4%">&nbsp;</div> -->
			</div>
			<div class="list">
				<%-- <div class="question">
					<div style="width:10%">T1_1</div>
					<div style="width:28%">
						<div style="border:1px black  solid;width:100%;height:20px;" id="inner2">
							<span id="inner'0'" style="width:0.040160642570281124%;line-height:20px;visibility:visible;display:-moz-inline-stack;display: inline-block;height:20px;background-color:#63b544;"></span>
						</div>
					</div>
					<div style="width:15%">1/200</div>
					<div style="width:28%">
						<div style="border:1px black  solid;width:100%;height:20px;" id="inner2">
							<span id="inner'0'" style="width:0.040160642570281124%;line-height:20px;visibility:visible;display:-moz-inline-stack;display: inline-block;height:20px;background-color:#63b544;"></span>
						</div>
					</div>
					<div style="width:15%">500/2000</div>
					<div style="width:4%">去阅卷</div>
				</div>
				<div class="question">
					<div style="width:10%">T1_1</div>
					<div style="width:28%">
						<div style="border:1px black  solid;width:100%;height:20px;" id="inner2">
							<span id="inner'0'" style="width:0.040160642570281124%;line-height:20px;visibility:visible;display:-moz-inline-stack;display: inline-block;height:20px;background-color:#63b544;"></span>
						</div>
					</div>
					<div style="width:15%">1/200</div>
					<div style="width:28%">
						<div style="border:1px black  solid;width:100%;height:20px;" id="inner2">
							<span id="inner'0'" style="width:0.040160642570281124%;line-height:20px;visibility:visible;display:-moz-inline-stack;display: inline-block;height:20px;background-color:#63b544;"></span>
						</div>
					</div>
					<div style="width:15%">500/2000</div>
					<div style="width:4%">去阅卷</div>
				</div>
				<div class="question">
					<div style="width:10%">T1_1</div>
					<div style="width:28%">
						<div style="border:1px black  solid;width:100%;height:20px;" id="inner2">
							<span id="inner'0'" style="width:0.040160642570281124%;line-height:20px;visibility:visible;display:-moz-inline-stack;display: inline-block;height:20px;background-color:#63b544;"></span>
						</div>
					</div>
					<div style="width:15%">1/200</div>
					<div style="width:28%">
						<div style="border:1px black  solid;width:100%;height:20px;" id="inner2">
							<span id="inner'0'" style="width:0.040160642570281124%;line-height:20px;visibility:visible;display:-moz-inline-stack;display: inline-block;height:20px;background-color:#63b544;"></span>
						</div>
					</div>
					<div style="width:15%">500/2000</div>
					<div style="width:4%">去阅卷</div>
				</div> --%>
			</div>
		</div>
		 <img id="loadImg" style="display:none;" class="menu_pre" src="<%=basePath%>common/image/load.gif"/>
		<center>
 	  </center>
 </form>
  </body>
</html>
</form>
