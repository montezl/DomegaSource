<%@ page contentType="text/html; charset=utf-8" language="java"
		 import="com.dmj.util.Const,com.dmj.util.StaticClassResources,com.dmj.util.app.EdeiInfo" %>
<%@ page import="java.util.*,java.util.Properties" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	Map<String,String> skinSetting =StaticClassResources.SkinSetting;
	EdeiInfo edeiInfo = StaticClassResources.EdeiInfo;
	String skin =skinSetting.get("skin");
	String systemTitle = skinSetting.get("systemTitle");
	String color = skinSetting.get("color");
	String systemName = edeiInfo.getVendor();
%>
<%
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "No-cache");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head> 
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript"	src="<%=basePath%>common/js/jquery.js"></script>
 <script type="text/javascript"	src="<%=basePath%>common/js/common.js"></script>
  <script type="text/javascript"	src="<%=basePath%>common/js/jquery.cookie.js"></script>
<title><%=systemName%></title>
<link rel="stylesheet" href="<%=basePath%>colors/<%=color%>.css" type="text/css" />

<style>
a{
	color: #fff;
	text-decoration:line;
}
a:hover {
	color:#000;
}
</style> 
<script type="text/javascript" src="<%=basePath%>common/js/lhgdialog/lhgdialog.min.js?skin=mac_cxj"></script>
<script language="javascript" type="text/javascript"  defer="defer">
var userType_cc = "";
jQuery().ready(function(){
	getImageCheckNum();
	getonlineuser();
// 	var marks=$("#mark").val();
	//var marks = GetCookie("marks");
	var marks = GetCookie("marks");
// 	var marks=$.cookie('marks');
// 	alert(marks)
	if(marks=="0"){
		$("#mark-yh").attr("checked",false);
		$("#mark-wh").attr("checked",true);
	}else if(marks=="1"){
		$("#mark-yh").attr("checked",true);
		$("#mark-wh").attr("checked",false);
	}else{
		$("#mark-yh").attr("checked",true);
		$("#mark-wh").attr("checked",false);
	}
	hiddenFun();
	var  usertype = $("#usertype").val();
	var openocs = $("#openocs").val();
// 	getParentsStudent();
// 	alert($("#parentLogin").val()!=2 && $("#parentLogin").val()!=1)
	if($("#parentLogin").val()!=2 && $("#parentLogin").val()!=1){
		document.getElementById("changeStu").style.display = "none";
	}
	if("1"==openocs){
		if(usertype==2 || usertype==3){
			document.getElementById("resources").style.display = "none";
			document.getElementById("resourcest").style.display = "none";
			document.getElementById("peizyhwh").style.display = "none";
			document.getElementById("onlineNum").style.display = "none";
			
		}else{
			//document.getElementById("chargeinfo").style.display = "none";
// 			document.getElementById("changeStuSelect").style.display = "none";
			document.getElementById("changeStu").style.display = "none";
		}
	
	}else{
	if(usertype==2 || usertype==3){
			document.getElementById("resources").style.display = "none";
			document.getElementById("resourcest").style.display = "none";
			document.getElementById("peizyhwh").style.display = "none";
			document.getElementById("onlineNum").style.display = "none";
	}
// 		document.getElementById("peizyhwh").style.display = "none";
// 		document.getElementById("onlineNum").style.display = "none";
		//document.getElementById("chargeinfo").style.display = "none";
	}
	
	
		//区别有痕无痕页面
	$.ajax( {
					type : "POST",//用POST方式传输
					cache : false,
					async : false,
					dataType : "html",//数据格式:JSON
					url : 'conffig!gettypeValue.action',//目标地址
					data : {
						typeName:'type'
					},
					success : function(data) {
					//有痕
						if(data=="1"){
							$("#peizyhwh").hide();
							$("#mark-wh").attr("checked",false);
							$("#mark-yh").attr("checked",true);
						}else if(data=="0"){
							//无痕
							$("#peizyhwh").hide();
							$("#mark-wh").attr("checked",true);
							$("#mark-yh").attr("checked",false);
						}else{
							//有痕和无痕
							
						}
					},
					error:function(aa,bb,cc){
						
					}
				});
		if($("#parentLogin").val() == '1')showChangeStu();
		var	topTabs=$("#topTabs").val();
		if(topTabs==""){
			//if((window.parent.frames['mainFrame']).length>0){
				/* var ttab=window.parent.frames['mainFrame'].gettabId();
				$("#topTabs").val(ttab); */
			//}
				
			}
		setcook();
		//aas();
// 		if($("#parentLogin").val() == '1')showChangeStu();
});


window.setInterval(function(){
	if($("#imageCheckSpan").is(":hidden")){
		return;
	}
	getImageCheckNum();
},2000);		
window.setInterval("getonlineuser()",5000);


//退出系统
function loginOut(){
	var aa = confirm("确认退出该系统吗？");
	if(aa){
		top.window.location="loginAction!loginOut.action";
	}
}
//修改密码
function updatepassword(){
	//top.window.myFrame.window.left.window.manFrame="updatePassowrd!default.action";
	window.parent.frames['manFrame'].location="updatePassowrd!default.action";
}

function help(){
	window.open("../help/help.html");
}
	
function download(){
	window.open("../main/download.jsp");
}

function connection(){
	userType_cc = $("#usertype").val();
	dialogs("teacherAction!toConnections.action", "联系方式")
}
function dialogs(url, title) {
	var api = $.dialog({
		id: "testID2",
		okVal: "确定",
		min: true,
		max: true,
		fixed: true,
		left: "55%",
		top: "50%",
		width: "623px",
		height: "425px",
		content: 'url:' + url,
		cache: false,
		title: title
	})
}
//获取未审核数
function getImageCheckNum(){
	$.ajax( {
		type : "POST",//用POST方式传输
		cache : false,
		async : false,
		dataType : "json",//数据格式:JSON
		url : 'imgCheck!getImageCheckCount.action',//目标地址
		success : function(data) {
			if(data.code==200){
				$("#imageCheckSpan").show();
				$("#imageCheckCount").html(data.msg);
				return;
			}else if(data.code==404){
				$("#imageCheckSpan").hide();
			}
			$("#imageCheckCount").html('');
		},
		error:function(jqXHR, textStatus, errorThrown){
			if(jqXHR.responseText=='301'&&jqXHR.status==500){
				window.location.href="../../alogin.jsp";
			}

			$("#imageCheckCount").html('');
		}
	});
}
// 获取在线人数

function getonlineuser(){

	$.ajax( {
					type : "POST",//用POST方式传输
					cache : false,
					async : false,
					dataType : "html",//数据格式:JSON
					url : 'loginAction!getonlineuser.action',//目标地址
					success : function(data) {
					if(data.indexOf("DOCTYPE")!=-1){
				
						return false;
					}
					var d = data;
					if(null!=d&&"null"!=d){
					$("#onlineuser").html(d);
				
					}
					
					},
					error:function(){
						
					}
				});


}
// 获取当前用户下的学生

// function getParentsStudent(){
// 	$.ajax( {
// 			type : "POST",//用POST方式传输
// 			cache : false,
// 			async : false,
// 			dataType : "json",//数据格式:JSON
// 			url : 'loginAction!getParentsStudent.action',//目标地址
// 			success : function(data) {
// 				for(var i=0;i<data.length;i++){
// 					if($("#userid").val() == data[i].id){
// 						$("#changeStusapn").append("<input type='radio' name='studentra' value='"+data[i].id+"' checked='checked' />"+data[i].studentName)
// 					}else{
// 						$("#changeStusapn").append("<input type='radio' name='studentra' value='"+data[i].id+"' />"+data[i].studentName);
// 					}
					
// 				}
// 				$("#changeStuSelect").change(function(){
// 					var studentid = $(this).val();
// 					top.window.location="loginAction!parentChangeStu.action?thisStuId="+studentid;
// 				})
// 			},
// 			error:function(){
// 				alert("serror");
// 			}
// 		});
// }

//链接到图片审核页面
function toImageCheck(){
	var url = "<%=basePath%>jsp/viewScore/imageCheck.jsp";
	window.open(url);
}

function opennewjsp(){
var url ="loginAction!getonlineuserinfo.action";
		window.open(url);
}

function aas(){
// 		window.parent.frames['leftFrame'].aaas();

// session.setAttribute("mark","1"); 
// url="<%=basePath%>jsp/main/teachleftMenu2.jsp";
// if(){
	
// }
// window.top.frames['manFrame'].location = url
var mark=$("#mark").val();
var marks=$('input:radio[name="mark"]:checked').val(); 
// if(mark==marks){
// 	return;
// }
			hiddenFun();
			$.ajax({
			type : "POST",//用POST方式传输
			cache : false,
			async : false,
			dataType : "html",//数据格式:JSON
			url : 'loginAction!yhAndwh.action?mark='+marks, //目标地址
			success : function(data) {
		var	topTabs=$("#topTabs").val();
		/* if(!topTabs){
			topTabs = window.parent.frames['mainFrame'].$(".menu_selected").attr("id");
		} */
// 		alert(topTabs)
			//1：有痕  0：无痕
			var lefturl="";
			var mainurl="";
// 			var manurl="";
// 			manurl="<%=basePath%>jsp/main/manframe.jsp";
			if(topTabs=="3"){
				mainurl="<%=basePath%>jsp/main/mainframe.jsp";
				lefturl="<%=basePath%>jsp/main/teachleftMenu.jsp";
				window.parent.frames['mainFrame'].location = mainurl;
// 				if(marks=="0"){
// 					manurl="<%=basePath%>jsp/awardPoint/awardPointlist.jsp";
// 				}
				}else if(topTabs=="6"){
// 				alert("06?");
// 					mainurl="<%=basePath%>jsp/main/manframe.jsp";
					var logType =$("#logType").val();
					if("P"==logType){						
						lefturl="<%=basePath%>jsp/main/teachleftMenu5.jsp";
					}else{
						lefturl="<%=basePath%>jsp/main/leftMenu.jsp";
					}
				 	
				}else if(topTabs=="5"){
						mainurl="<%=basePath%>jsp/main/manframe.jsp";
						lefturl="<%=basePath%>jsp/main/teachleftMenu3.jsp";
						window.parent.frames['manFrame'].location = mainurl;
// 						window.parent.frames['mainFrame'].location = mainurl;
				
				}else{
					mainurl="<%=basePath%>jsp/main/manframe.jsp";
					lefturl="<%=basePath%>jsp/main/teachleftMenu2.jsp";
					window.parent.frames['manFrame'].location = mainurl;
				}
			
				window.parent.frames['leftFrame'].location = lefturl;
// 				window.top.frames['manFrame'].location = manurl;
			}
	});
	}	
	
	// 单点登录功能控制		
function hiddenFun(){	
	var logType = document.getElementById("logtype").value;	
	if (logType == "student" || logType == "teacher") {//学生需要隐藏的功能	
		document.getElementById("peizyhwh").style.display = "none";
		document.getElementById("changePwd").style.display = "none";
		document.getElementById("changePwdt").style.display = "none";
		document.getElementById("resources").style.display = "none";
		document.getElementById("resourcest").style.display = "none";
		document.getElementById("signOut").style.display = "none";
		document.getElementById("signOutt").style.display = "none";
	} else if(logType == "teacher"){//教师需要隐藏的功能	
		document.getElementById("changePwd").style.display = "none";
		document.getElementById("changePwdt").style.display = "none";
		document.getElementById("signOut").style.display = "none";
		document.getElementById("signOutt").style.display = "none";
		document.getElementById("changeStu").style.display = "none";
		
	}	
		
}		

	function topTab(Id){
		$("#topTabs").val(Id);
	}	
	function setcook(){
// alert($.cookie('mark',"null"));
// $.cookie('mark',mark);
// $.cookie('mark','',{ expires: 30,path: '/', domain: 'dmj.com'});
var aaa=$('input:radio[name="mark"]:checked').val(); 

// , {expires: 7, path: ‘/’, domain: ‘jquery.com’, secure: true});
// var date = new Date(); 
// date.setTime(date.getTime() + (1 * 24 * 60 * 60 * 1000)); 
// alert(date);
// var date=1 * 24 * 60 * 60 * 1000;
// alert(date );

   var MyCookie=$.cookie('marks',aaa,{ expires: 30,path:'/',secure: false,raw:false});
}
function updatevalue(Id){
$("#"+Id).attr("checked",true);   
}
//产品包购买
function charge(){
// 	var aa = confirm("您还没有购买套餐？是否购买?");
// 	if(aa){
		window.open ('package.jsp','newwindow','height=800,width=1100,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no') ;
		waring();
// 	}

}
function waring(){
	window.parent.parent.updateheight();
	dg = new $.dialog({
			lock : true,
			background: '#000',
			opacity:0.5,
			id : '0000',
			content : 'url: loginAction!continue_harge.action',
			title : '&nbsp;网上支付提示',
	    	width: '350px',
	    	height: '150px',
			max : false,
			min : false,
			cache : false,
			cancel:false,
			esc:false
		});
	}

//取Cookie的值
function GetCookie(name) {
	var arg = name + "=";
	var alen = arg.length;
	var clen = document.cookie.length;
// 	var clen = getCookie;
	var i = 0;
	while (i < clen) {
		var j = i + alen;
		if (document.cookie.substring(i, j) == arg) return getCookieVal(j);
		i = document.cookie.indexOf(" ", i) + 1;
		if (i == 0) break;
	}
	return null;
}
function getCookieVal(offset) {
	var endstr = document.cookie.indexOf(";", offset);
	if (endstr == -1) endstr = document.cookie.length;
	return unescape(document.cookie.substring(offset, endstr));
}
function showChangeStu(){
// 	window.parent.parent.updateheight();
	var dialog = $.dialog({
		lock:'true',
		title:'绑定的学生信息',
		width:'380px',
		height:'180px',
		content: 'url:<%=basePath%>jsp/login/changeStu.jsp'
	});
}
function toNewStu(){
	var studentid = $("#userid").val();
	top.window.location="loginAction!parentChangeStu.action?thisStuId="+studentid;
}

</script>
</head>
<body class="top top-bg-color">
<input type="hidden" value="${sessionScope.mark}" id="mark"></input>
<input type="hidden" value="${sessionScope.logtype}" id="logtype"></input>
<input type="hidden" value="${sessionScope.logType}" id="logType"></input>
<input type="hidden" id="usertype" value="${sessionScope.loginuser.usertype}" />
<input type="hidden" id="userid" value="${sessionScope.loginuser.userid}" />
<input type="hidden" value="${sessionScope.openocs}" id="openocs"></input>
<input type="hidden" value="${sessionScope.parentLogin}" id="parentLogin"></input>
<input type="hidden" id="topTabs"></input>
<!--<body style="background:url(../../common/image/main/b3.jpg);float:left;background-repeat:repeat-x">-->
<!-- <span style="height:100%;"> -->
<span hidden id = "changeStusapn">

</span>
<table style="color:#fff">
	<tr>
		<!--
		<td style="width:2%;"><img src="../../common/image/main/top/logo3.png" style="height:45px;margin-bottom:8px;;" /></td>
		-->
		<td style="width:2%;"><img src="../../skin/<%= skin %>/img/topframe-left-logo.png"  style="margin-left: 5px;height:45px;margin-bottom:8px;" /></td>
		<td style="vertical-align:middle;width:40%;">
			<span style="margin-left:20px;font-size:14pt;font-weight:bold;color:#fff;padding-top:33px;"><%= systemTitle %>[${sessionScope.loginuser.schoolName}]</span>
		</td>
		<td style="width:40%;text-align:right;padding-right:10px;">
	     
	     <table style="width:100%;">
	     	<tr id="fun" class="top-fun">
	     		<td style="width">
	     		</td>
	     		<td>
	     		</td>
	     		<td>
	     		<span id="peizyhwh" style="cursor:hand;">
<!-- 	     		<input type="radio" id="mark-yh" name="mark" value="1" onclick="setcook();aas();"  /><label for="mark-yh"><font style="color:#000;">有痕阅卷</font></label> -->
<!-- 				<input type="radio" id="mark-wh" name="mark" value="0" onclick="setcook();aas();"/><label for="mark-wh"><font style="color:#000;">网上阅卷</font></label> -->
				<span style="padding:0 6px;"></span>
				</span> 
					<span id="imageCheckSpan" style="display: none;">未审核数（<a href="javascript:void(0)"  onclick ="toImageCheck()"><span id="imageCheckCount"></span></a>）</span>
					<span style="padding:0 6px;"></span>  
					<span id = "onlineNum" >在线人数（<a href="javascript:void(0)"  onclick ="opennewjsp()"><span id="onlineuser"></span></a>）</span>
					<span style="padding:0 6px;"></span>
					<!-- <span id="chargeinfo"><img src="../../common/image/main/top/charge.png" style="height:20px;"/>
	     				<a href="javascript:charge()">产品包购买</a> 
	     			</span> -->
	     			<span id = "changeStu"><img src="../../common/image/main/top/changestu.png" alt="切换学生" style="height:20px;" />
					<a id = "changeStua" href="javascript:showChangeStu()">切换学生</a>
					</span>
					<span style="padding:0 6px;"></span>
	     			<span id = "changePwd"><img src="../../common/image/main/top/password-01.png" alt="密码修改" style="height:20px;" /></span>   
					<a id = "changePwdt" href="javascript:updatepassword();" >密码修改</a>
					<span style="padding:0 6px;"></span>
<%--	     		</td>--%>
<%--	     		<td>--%>
		     		<span id = "resources"><img src="../../common/image/main/top/download.png" alt="资源下载" style="height:20px;" /> </span> 
					<a id ="resourcest"  href="javascript:download();">资源下载</a>
					<span style="padding:0 6px;"></span>
					
					<span id = "resources"><img src="../../common/image/main/top/telphone-01.png" alt="联系方式" style="height:20px;" /> </span> 
					<a id ="resourcest"  href="javascript:connection();">联系我们</a>
					<span style="padding:0 6px;"></span>
					
	     		</td>
	     		<td id = "signOut">
		     		<span id = "signOutt" ><img src="../../common/image/main/top/loginout.png" alt="退出" style="height:20px;" /> </span> 
					<a  href="javascript:loginOut();">退出</a>
	     		</td>
	     	</tr>
	     </table>
	     
	     
	     	
	     	
	     	<!-- 			[<a href="javascript:help();">帮助</a>] -->
     	</td>
	</tr>
</table>



</body>
</html>
