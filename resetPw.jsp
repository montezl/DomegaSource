<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.dmj.util.config.Configuration" %>
<%@ page contentType="text/html; charset=utf-8"%>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","No-cache");
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String isEnforceChangePassword= Configuration.getInstance().getIsEnforceChangePassword().toString();
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
 <head>
	<title>密码找回</title>	<base href="<%=basePath%>"/>
	<META http-equiv="Content-Type" content="text	/html; charset=utf-8">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="密码找回">
	<meta http-equiv="description" content="密码找回">
	<script type="text/javascript" src="<%= request.getContextPath() %>/common/js/jquery.js"></script>
	 <script type="text/javascript" src="<%= request.getContextPath() %>/common/js/common.js?v=1.0"></script>
<!-- BeAlert.js使用教程	http://www.jq22.com/jquery-info10917 -->
	<script type="text/javascript"  src="<%=basePath%>common/js/alert/BeAlert.js"></script>
	<link rel="stylesheet" href="<%=basePath%>common/css/style.css" type="text/css" />
	<link rel="stylesheet" href="<%=basePath%>common/css/alert/BeAlert.css" type="text/css" />
		<script type="text/javascript">
	var wait = 60;
	jQuery().ready(function() {
		var logType = window.location.search.substring(logType);
		var logType2= logType.substring(logType.indexOf("=")+1);
		console.log(logType2);
		// var userType = document.getElementById("userType");
		// for(var i=0;i<userType.length;i++){
		// 	if(userType[i].value==logType2){
		// 		userType[i].selected=true;
		// 	}
		// }
		if(logType2=='T'){ //教师
			$("#userType option[value='T']").attr("selected",true);
		}else if(logType2=='S'){//学生
			$("#userType option[value='S']").attr("selected",true);
		}else{ //家长
			$("#userType option[value='P']").attr("selected",true);
		}

	});
	function searchUserPhone(o){
		
		var userName = $("#user").val();
		$("#span").html("<font color='red'></font></br>");
		if(userName==''){
		 	$("#span").html("<font color='red'>用户名不能为空！</font></br>");
		 	return;
		 }
		var userType = $("#userType").val();
		 $.post("updatePassowrd!searchUserPhone.action",{"bname":userName,"bname2":userType},function(data)
				{ 
					if(data != "null" && data !=""){
					 		confirm(data, "短信将发送至上面的号码，请确认您可以收到！",
					 		 function (isConfirm) {
					                if (isConfirm) {
					                    //after click the confirm
					                    sendCode(o);
					                } else {
					                    //after click the cancel
					                     alert("很遗憾！", "若用户名检查无误，请联系管理员", function () {
						            			}, {type: 'success', confirmButtonText: '确定'});
					                }
					            },
					             {
						             confirmButtonText: '发送', 
						             cancelButtonText: '这不是我的手机号', 
						             width: 400
					             }
					             );
					}else{
						//alert("该用户名不存在！");
						alert("很遗憾！", "该用户名不存在或该用户未绑定手机号！", function () {
	            			}, {type: 'success', confirmButtonText: '确定'});
					}
      		  });
    };
    function sendCode(o){
    	time(o);
    	 $.post("updatePassowrd!phoneCheck.action",function(data)
		{ 
					if(data != "发送成功"){
						$("#span").html("<font color='red'>验证码发送失败，请稍后重试</font>");
						wait = 0 ;
					}
					});
    }
    function time(o) {
    if (wait == 0) {
        o.removeAttribute("disabled");           
        o.value="再次获取";
        wait = 60;
    } else { 
        o.setAttribute("disabled", true);
        o.value=wait + "秒后可重发";
        wait--;
        setTimeout(function() {
            time(o);
        },
        1000)
    }
}
function tijiao(){
		$("#span").html(" ");
    	var pwd = $("#pwd").val();
    	var repwd = $("#repwd").val();
    	var code = $("#code").val();
    	if(pwd=='' || repwd==''){
		 	$("#span").html("<font color='red'>新密码不能为空！</font></br>");
		 	return;
		}

		if(!isPasswd2(pwd)){
			if(pwd.length<6){
				$("#span").html("<font color='red'>密码小于6位！</font></br>");
			}else if(pwd.length>20){
				$("#span").html("<font color='red'>密码大于20位！</font></br>");
			}else{
				$("#span").html("<font color='red'>密码不可以设置特殊字符！</font></br>");
			}
			//$("#span").html("<font color='red'>密码小于6位！</font></br>");
			return;
		}

		var isEnforceChangePassword2=<%=isEnforceChangePassword%>;
		if(isEnforceChangePassword2=='1'&&!isPasswd(pwd)){
			$("#span").html("<font color='red'>新密码格式不正确，请修改为6-20位字母和数字组合的密码！</font></br>");
			return;
		}

    	if(pwd != repwd){
		 	$("#span").html("<font color='red'>两次密码不一致！</font></br>");
		 	return;
		 }
		 if(code==''){
		 	$("#span").html("<font color='red'>请填入验证码！</font></br>");
		 	return;
		 }
    	$.ajax( {
		type : "POST",//用POST方式传输
		cache : false,
		async : false,
		dataType : "html",//数据格式:JSON
		url:'updatePassowrd!resetPw.action',
		data:{
			pwd:pwd,
			code:code
		},
		success : function(data) {
			if(data == "0"){
				$("#span").html("<font color='red'>密码修改失败，请重试</font>");
				wait = 0 ;
			}else if(data == "8"){
				$("#span").html("<font color='red'>验证码已过期，请重新获取</font>");
				wait = 0 ;
			}else if(data == "6"){738178
				$("#span").html("<font color='red'>验证码不正确，请重新填写</font>");
				wait = 0 ;
			}else{
				alert("恭喜！", "密码修改成功!!!!", function () {
					window.close();
					}, {type: 'success', confirmButtonText: '确定'});




			}
		},
		error:function(){
			$("#span").html("<font color='red'>密码修改失败，请重试</font>");
			wait = 0 ;
		}
	});

    }
    function tiao(){
    	window.close();
    }
</script>

	<style type="text/css">
		.title4{
			text-align:right;
		}
	</style>
 </head>
<body style="margin: 15px" >
<form method="post"  id="form1" name="form1" action="login!register.action" >
<input type="hidden" value="2" id="tab">
<div id="tabs">
				<ul>
					<li class="select" id="onelia" >
						密码找回
					</li>
					
				</ul>
				<p class="line" >
				</p>
			</div>
			<br>
<div id="gradem" style="margin-top:30px;margin-left:70px">

	<table id="mm" style="width:100%;font-size:13px;width:30%;text-align:center;border:2px inset #1b74fa;height:300px;background-color:#fff;font-weight:bold;color:#666666" >
	<tr style="">
		<td colspan="2" style="font-weight:bold; border-bottom:2px inset #00CCFF;text-align:center;background-color: #7699cb;color:#fff;">密&nbsp;&nbsp;码&nbsp;&nbsp;找&nbsp;&nbsp;回</td>
	</tr>
<!-- 	<tr> -->
<!-- 			   <td style="text-align: right;">&nbsp;</td> -->
<!-- 			   <td></td> -->
<!--     </tr> -->
     <tr>
    	<td colspan="2" style="height: 30px;"><span id="span"></span>
    	 </td>
    </tr>
    <tr>
			   <td  style="margin-left:30px">用户名:<font color="red">*</font></td>
			   <td style="text-align: left"><input type="text" id="user" name="user"/></td>
    </tr>
    <tr>
			   <td  style="margin-left:30px">用户类型:<font color="red">*</font></td>
			   <td style="text-align: left">
			   <select name='userType' id='userType'  class="txt" >
							<option value='T' selected>教师</option>
							<option value='S'>学生</option>
							<option value='P'>家长</option>
						</select>
			</td>
    </tr>
    <tr>
			   
			   <td style="margin-left:30px"><input type="text" style="width:100px;border-radius:5px;margin-left:20px"id="code" name="code" placeholder="输入验证码" /></td>
    			<td  style="text-align: left"><input onclick="searchUserPhone(this)" class="button_default" onmouseOut="this.className='button_mouseOver'" 
				onmouseOver="this.className='button_default'" id="Code"  type="button"  value="获取验证码" style="width: 120px"/></td>
    </tr>
	<tr>
			   <td style="margin-left:30px">密码:<font color="red">*</font></td>
			   <td style="text-align: left"><input type="password" id="pwd" placeholder="6-20位字母和数字组合" /></td>
    </tr>
	<tr>
			   <td  style="margin-left:30px">重复密码:<font color="red">*</font></td>
			   <td style="text-align: left"><input type="password" id="repwd" /></td>
    </tr>
   
    <tr>
    <td></td>
	<tr>
		<td colspan="2" style="text-align: center	;color:green" id="info"> <a>*系统会发送验证码到用户绑定的手机号，未绑定手机的用户请联系管理员</a></td>
	</tr>	   
	
	<tr>
		<td>
		&nbsp;<input onclick="tiao()" id = "back" class="button_default" onmouseOut="this.className='button_mouseOver'" 
		onmouseOver="this.className='button_default'"  type="button"  value="返&nbsp;回" style="width: 120px"/>
		</td>
		<td >&nbsp;<input onclick="tijiao()" id="submitt" class="button_default" onmouseOut="this.className='button_mouseOver'" 
		onmouseOver="this.className='button_default'"  type="button"  value="提&nbsp;交" style="width: 120px"/>
		</td>
		
	</table>

</div>
</form>
</body>
</html>
