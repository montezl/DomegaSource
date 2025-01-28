<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath%>">
<link rel="icon" href="jsp/ctb2/img/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="jsp/ctb2/img/favicon.ico" type="image/x-icon" />
<link rel="bookmark" href="jsp/ctb2/img/favicon.ico" type="image/x-icon" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE"> 
<META HTTP-EQUIV="Expires" CONTENT="-1">
<title>错题本</title>
<script type="text/javascript"  src="jsp/ctb2/jquery.js" ></script>
<script type="text/javascript" src="jsp/ctb2/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="jsp/ctb2/zTree_v3-master/js/jquery.ztree.core.js"></script>
<link rel="stylesheet" type="text/css" href="jsp/ctb2/ctb.css" />
<link rel="stylesheet" type="text/css" href="jsp/ctb2/components.css" />
<!-- <link rel="stylesheet" href="jsp/ctb2/css/example.css"> -->
  <!-- This is what you need -->
 
 <link rel="stylesheet" href="jsp/ctb2/css/sweet-alert.css">
<link rel="stylesheet" href="jsp/ctb2/zTree_v3-master/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script src="jsp/ctb2/js/sweet-alert.min.js"></script>
<script type="text/javascript" src="jsp/ctb2/ctb.js" async defer></script>
<script type="text/javascript" src="jsp/ctb2/ctb_data.js" ></script>
<script type="text/javascript" src="jsp/ctb2/ctb_mainDown.js" ></script>
<script type="text/javascript"	src="<%=basePath%>common/js/page.js"></script> 
<script type="text/javascript"  src="<%=basePath%>common/js/kindeditor/kindeditor.js"></script>
<script type="text/javascript"  src="<%=basePath%>common/js/jquery.blockUI.min.js"></script>
<script type="text/javascript" src="<%=basePath%>common/js/lhgdialog/lhgdialog.min.js?skin=mac"></script>
<!-- <script type="text/javascript" src="jsp/ctb2/knowledgezTree.js"></script> -->
<script type="text/javascript" async="async">
//获得阶段
	function stages(){
		var stage;
		var subject="";
		var graNum="";
		student_user();
		subject=document.getElementById("onesubject").getAttributeNode("data").value;
		graNum=document.getElementById("onegrade").getAttributeNode("data").value;
		imgLoading();
		$.ajax({
			type : "POST",
			cache : false,
			async : false,
			dataType : "json",//数据格式:JSON
			url : "questionBookAction!getKnowNum.action",//目标地址
			data : {
				gradeNum : graNum,
				subject : subject
			},
			success : function(data) {
			setTimeout($("#one").val(""), 100000)
				
					$.ajax({
						type : "POST",
						cache : false,
						async : false,
						dataType : "text",//数据格式:JSON
						url : "questionBookAction!knowledge.action?subject="+subject+"&grade="+graNum,//目标地址
						data : {},
						success : function(data) {
						},
						error : function(){
						}
					});
				if(data.length){
					stage=data[0].stage;
					$("#treeNode_stages").val(stage);
// 					alert(stage);
// 					$.fn.zTree.init($(".ztree"), setting, data);
				}
			},
			error : function(){
				alert("error");
			}
		});
	}
</script>
<script type="text/javascript" src="jsp/ctb2/ctb_data.js" ></script>
<input type="hidden" value="0" id="treeNode_idQ"/>
</head>

<body oncontextmenu="return false" onselectstart="return false">
<input type="hidden" id="n"></input> 
<input type="hidden" value="" id="treeNode_stages"/>
<input type="hidden" value="10" id="treeNode_id"/>
<input type="hidden" id="n"></input> 
<input type="hidden" id="examPaperNums"></input> 
<input type="hidden" id="questionNums" value="status1"></input>
<input type="hidden" value="" id="schoolNum">
<input type="hidden" id="classNum"></input>
<input type="hidden" id="gradeNum"></input>
<input type="hidden" value="" id="studentId"></input>
<input type="hidden" value="" id="exid"></input>
<input type="hidden" value="10" id="scoreId"></input>
<input type="hidden" id="tabtype"></input>
<input type="hidden" id="tabname"></input>
<input type="hidden" id="counts"></input>
<input type="hidden" id="qtype"></input>
<input type="hidden" id="examDate"></input>
<input type="hidden" value="<%=basePath%>" id="basePath">
<!-- <input type="hidden" value="${applicationScope.imageAccessPath}" id="imageAccessPath"> -->
<input type="hidden" id="usertype" value="${sessionScope.loginuser.usertype}" >
<input type="hidden" id="userid" value="${sessionScope.loginuser.userid}" >
<input type="hidden" value="${sessionScope.loginuser.realname}"  id="userNa"/>
<input type="hidden" value="${applicationScope.imageAccessPath}"  id="imageAccessPath"/>
<input type="hidden" value="0" id="start"></input>
<input type="hidden" value="0" id="start2"></input>
<input type="hidden" value="0" id="firstClick"></input>
<input type="hidden" value="true" id="firstselectstudent"></input>
<input type="hidden" value="0" id="mastery"></input>
<input type="hidden" value="0" id="difficult"></input>
<input type="hidden" value="0" id="reason"></input>
<input type="hidden"  id="isHistory"/>
<!-- 下面是分页需要的参数 -->
<input type="hidden" value="${page}" id="page_val"/>
<input type="hidden" id="pageSize" name="pageSize" value="${pageSize}"/>
<input type="hidden" id="pagestart" name="pagestart" value="${pagestart}"/>
<input type="hidden" id="currenpage" name="currenpage" value="${currenpage}"/>
<input type="hidden" id="psize" name="psize" value="1"/>
<input type="hidden" id="count" name="count" value="${count}"/>
<div class="add_test_img">
<div class="top">
  <div class="logo"> <img src="jsp/ctb2/img/logo.png" /> </div>
  <div class="nav">
    <ul>
    <li index="3"><a >学生</a>
    <ul id="studentUll" class="nav-sub-ul" name="student">
    </ul>
    </li>
    <li index="0"> <a>科目</a>
        <ul id="subject" class="nav-sub-ul">
        </ul>
    </li>
    <li index="1"> <a  >年级</a>
        <ul id="grade" class="nav-sub-ul">
        </ul> 
      </li>
    </ul>
  </div>
</div>
<div class="bottom">
  <div class="left" >
  <div class="ztree"  id="one" style="position: clear;left:0px;"></div>
  </div>
  <div class="right">
  <div class="botton">
    <div id="search" class="search" >搜索</div>
    <div id="search2" style="display:none">搜索</div>
  </div>
    <div class="right-top" id="right-base-info">
      <div class="right-top-info"> 
	      <span class="info">基本信息： </span> 
	       <span>姓名：</span> 
	      <span id="student" data="000" index="3">请选择学生</span> 
	      <span class="spanmargin"></span> 
	      <span>科目：</span> 
	      <span id="onesubject" data="101" index="0">**</span> 
	      <span class="spanmargin"></span> 
	      <span>年级：</span> 
	      <span id="onegrade" data="11111" index="1">**</span> 
	      <span class="spanmargin"></span> 
	      <span>班级：</span> 
	      <span id="oneclass" data="1" index="2">**</span> 
	     
      </div>
      <div class="right-top"> 
	      <span class="info">考试信息： </span> 
	      <span>考试时间：</span>
	        <input id="startExamDate" value="" readonly="readonly"  onClick="starTime();" />
	        <span> - </span>
	        <input id="endExamDate" value="" readonly="readonly" onClick="endTime();" />
	        <span class="spanmargin"> </span> 
	        <span>考试类型：</span> 
	        <span class="span-wrapper">
		        <input id="examType" status="0" value="全部" readonly="readonly" data="-1" />
		        <ul id="examTypeUl" class="input-ul">
		        </ul>
	        </span> 
	        <span class="spanmargin"></span> 
	        <span>考试名称：</span> 
	        <span class="span-wrapper">
		        <input id="examName" value="请选择考试时间和考试类型" readonly="readonly" style="width:180px;" data="" />
		        <ul id="examNameUl" class="input-ul">
		          <li data="1">请选择考试时间和考试类型</li>
		        </ul>
	        </span> 
        </div>
      <div class="right-top-info"> 
	      <span class="info">试题信息： </span> 
	      <span style="padding-left:14px;">得分率：</span>
	        <input id="startScorePer" value="0"/>
	        <span style="margin-left:-15px;">% </span> 
	        <span> - </span>
	        <input id="endScorePer" value="60"/>
	        <span style="margin-left:-15px;">% </span> 
	        <span class="spanmargin"></span> 
	        <span>主客观题：</span> 
	        <span class="span-wrapper">
		        <input id="questionType" status="0" value="全部" readonly="readonly" data="-1"/>
		        <ul id="questionTypeUl" class="input-ul">
		        </ul>
	        </span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
	        <span class="spanmargin"></span> 
<!-- 	        <span>错误原因：</span>  -->
<!-- 	        <span class="span-wrapper"> -->
<!-- 		        <input id="errorReason" status="0" value="全部" readonly="readonly" style="width:180px;" data="-1"/> -->
<!-- 		        <ul id="errorReasonUl" class="input-ul"> -->
<!-- 		        </ul> -->
<!-- 		        </span>  -->
        </div>
      <div class="right-top-info"  style="border-bottom:none;"> 
      <span class="info">完成复习： </span> 
<!--       <span class="spanmargin"></span>  -->
	        <span>错误原因：</span> 
	        <span class="span-wrapper">
		        <input id="errorReason" status="0" value="全部" readonly="readonly" style="width:170px;" data="-1"/>
		        <ul id="errorReasonUl" class="input-ul">
		        </ul>
		        </span>
		        <span class="spanmargin"></span>
<!--       <span style="moz-margin-start: -4px !important; -moz-margin-end: -4px !important;margin-left:335px;"> -->
   <span>   难易程度：</span> <span class="span-wrapper">
        <input id="difficultyLevel" status="0" value="全部" readonly="readonly" data="-1"/>
        <ul id="difficultyLevelUl" class="input-ul">
        </ul>
        </span> <span class="spanmargin"></span> <span>掌握程度：</span> <span class="span-wrapper">
        <input id="masterLevel" status="0" value="全部" readonly="readonly" style="width:180px;" data="-1"/>
        <ul id="masterLevelUl" class="input-ul">
        </ul>
        </span> </div>
    </div>
    <div class="right-main">
	<div  class="content">
<!-- 	    这是个分页 -->
	    <div id="page" style="padding-top:15px"></div>
	    <div id="loadImgSpan"></div>
	    	<span  id="tabCon1" style="display: block;"></span>
	    	<span  id="tabCon2" style="display: block;"></span>
	    	<span  id="tabCon3" style="display: block;"></span>
	    	<span  id="tabCon4" style="display: block;"></span>
	    	<span  id="tabCon5" style="display: block;width:100%;"></span>
	    	<span  id="tabCon6" style="display: block;"></span>
	    	<span  id="tabCon7" style="display: block;height:200px;width:100%"></span>
	    	<span  id="tabCon8" style="display: none;margin-top:20px; height:200px;width:100%">导出速度受题量和网速影响，请耐心等候...</br><a href="javascript:void(0);" onclick="exWord()">点此导出</a></span>
	 	
	 	</div>
	 	
    </div>
    <div class="tabmenu">
			 <ul>
			 <li id="tab1" data="tabCon1" class="tabmenu-ul-li">试题原题</li>
			 <li id="tab2" data="tabCon2" class="tabmenu-ul-li">我的答案</li>
			 <li id="tab3" data="tabCon3" class="tabmenu-ul-li">正确答案</li>
			 <li id="tab4" data="tabCon4" class="tabmenu-ul-li">优秀答题</li>
			 <li id="tab5" data="tabCon5" class="tabmenu-ul-li">我的心得</li>
			 <li id="tab6" data="tabCon6" class="tabmenu-ul-li">心得交流</li>
			 <li id="tab7" data="tabCon7" class="tabmenu-ul-li">完成复习</li>
			 <li id="tab8" data="tabCon8" class="tabmenu-ul-li">导出</li>
			 <li id="tab9" data="tabCon9" class="tabmenu-ul-li">举一反三</li>
			 </ul>
	 </div>
</div>
</div>
</div>
</body>
</html>
