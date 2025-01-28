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
	String isEnforceChangePassword = Configuration.getInstance().getIsEnforceChangePassword().toString();
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
 <head>
	<title>家长注册</title>	<base href="<%=basePath%>"/>
	<META http-equiv="Content-Type" content="text	/html; charset=utf-8">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<script type="text/javascript" src="<%= request.getContextPath() %>/common/js/jquery.js"></script>
  	<script type="text/javascript" src="<%=basePath%>common/js/layui/layui.all.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/common/js/common.js?v=1.0"></script>
	<script type="text/javascript"	src="<%=basePath%>common/js/register.js?v=1.1"></script>
	 <script type="text/javascript"	src="<%=basePath%>common/js/select2/select2.js?v=1.0"></script>
	<link rel="stylesheet" href="<%=basePath%>common/css/style.css" type="text/css" />
	<link rel="stylesheet" href="<%=basePath%>common/js/layui/css/layui.css">
	 <link rel="stylesheet" href="<%=basePath%>common/js/select2/select2.css" type="text/css" />
	 <script type="text/javascript" src="<%=basePath%>common/js/crypto-js/crypto-js.js"></script>
	 <script type="text/javascript" src="<%=basePath%>common/js/crypto-js/secrt.js"></script>
<!-- 		<script type="text/javascript">
	jQuery().ready(function() {
		
		//查找学校
		 $.ajax({
			type : "POST",//用POST方式传输
			cache : false,
			//async : true,
			async : false,
			dataType : "JSON",//数据格式:JSON
			url : 'appIndex!getAllSchool.action',//目标地址
		    success : function(data) {
// 				var str = JSON.stringify(data);
// 			alert(data[0].schoolName);
// 			alert(data[0].schoolNum);
// 			alert(data.length);
				for(var i = 0 ; i< data.length; i++){
						document.getElementById("schoolNum").options.add(new Option(data[i].text,data[i].value));
				}
			}
		});
		//查找年纪           根据学校num（不分权限的）
		 $.ajax({
			type : "POST",//用POST方式传输
			cache : false,
			async : false,
			data  : {
				schoolNum:$("#schoolNum").val()
			},
			dataType : "JSON",//数据格式:JSON
			url : 'appIndex!getAllGradeBySchoolNum.action',//目标地址
		    success : function(data) {
				for(var i = 0 ; i< data.length; i++){
						document.getElementById("gradeNum").options.add(new Option(data[i].text,data[i].value));
				}
			}
		});
		
		//查找班级           根据学校num和年级num （不分权限的）
		 $.ajax({
			type : "POST",//用POST方式传输
			cache : false,
			async : false,
			data  : {
				schoolNum:$("#schoolNum").val(),
				gradeNum:$("#gradeNum").val()
			},
			dataType : "JSON",//数据格式:JSON
			url : 'appIndex!getAllClassBySchoolNumAndGradeNum.action',//目标地址
		    success : function(data) {
				for(var i = 0 ; i< data.length; i++){
						document.getElementById("classNum").options.add(new Option(data[i].text,data[i].value));
				}
			}
		});
		
		//查找与学生的关系
		 $.ajax({
			type : "POST",//用POST方式传输
			cache : false,
			async : true,
			dataType : "JSON",//数据格式:JSON
			url : 'loginAction!studentRelation.action',//目标地址
		    success : function(data) {
		    	for(var i = 0 ; i< data.length; i++){
				document.getElementById("studentRelation").options.add(new Option(data[i].name,data[i].value));
				}
			}
		});
	
		$("#threelia").click(function() {
			$("#tab").val("2");
			$("#bm").hide();
			$("#mm").show();
			$(this).removeClass("noselect").addClass("select");
			$("#onelia").removeClass("select").addClass("noselect");
			$("#fourlia").removeClass("select").addClass("noselect");
		});
	});
	
	function uNameIsExist(obj){
		if($(obj).val() != ''){
			$.ajax( {
				 type : "POST",//用POST方式传输
				 cache : false,
				 async : false,
				 dataType : "json",//数据格式:JSON
				 url : 'updatePassowrd!isUserParentExist.action',//目标地址
				 data : {
				 	bname:$(obj).val()
				 },
				 success : function(data) {
					 if(data!="0"){
				    	alert("*该用户名已存在");
				        $(obj).val(''); 
		    		}
				 },
				 error : function(a,b,c){
		// 		 	alert(a+"---"+b+"---"+c);
				 }
			 });
	 	}

	}


	</script> -->
	<style type="text/css">
		.title4{
			text-align:right;
		}
		.miaoshu{
			margin-right:30px;
			text-align:right;
		}
		.shortInput{
			text-align: left;
		}
	</style>
 </head>
<body style="padding-top: 15px;padding-left: 15px" >
<form method="post"  id="form1" name="form1" action="login!register.action" onsubmit="return auth();">
<input type="hidden" value="2" id="tab">
<input type="hidden" value="<%=isEnforceChangePassword%>" id="isEnforceChangePassword">
	<input type="hidden" id="systemid" value="<%=request.getParameter("systemid")%>">
<%--	<input type="hidden" id="payUrl" value="<%=request.getParameter("payUrl")%>">--%>

<!-- <input type="hidden" id="userid" name="user.id" value="${user.id}" /> -->
<!-- <input type="hidden" id="userNum" name="user.usernum" value="${user.id}" /> -->
<div id="tabs">
				<ul>
					<li class="select" id="onelia" >
						家长注册
					</li>
					
				</ul>
				<p class="line" >
				</p>
			</div>
			<br>
<div id="gradem" style="margin-top:30px;margin-left:70px">

	<table id="mm" style="width:100%;font-size:13px;width:40%;text-align:center;border:2px inset #1b74fa;height:300px;background-color:#fff;font-weight:bold;color:#666666" >
	<tr style="">
		<td colspan="3" style="font-weight:bold; border-bottom:2px inset #00CCFF；text-align:center;background-color: #7699cb;color:#fff;">家&nbsp;&nbsp;长&nbsp;&nbsp;注&nbsp;&nbsp;册</td>
	</tr>
	<tr>
			   <td style="text-align: right;">&nbsp;</td>
			   <td></td>
    </tr>
    <tr>
    	<td colspan="2" style="height: 30px;"><span id="span"></span>
    		<span id = "spanSchoolNum"></span>
    		<span id ="spanGradeNum" ></span>
    		<span id ="spanClassNum" ></span>
    		<span id ="spanStudentName" ></span>
    		<span id ="spanMobile" ></span>
    		<span id="spanPassword"></span>
    		<span id ="spanConfirmPassword" ></span>
    		<span id="spanParentName"></span>
    		<span id="spanCode"></span>
    		<span id="spanSMInfo"></span>
    	 </td>
    </tr>
     <tr>
			   <td  class="miaoshu">学校<font color="red">*</font></td>
			   <td style="text-align: left"><select name="schoolNum" id="schoolNum" onchange="schoolChange()"></select> </td>
			   
			   <td class="shortInput"><input type="hidden" id="text4" name="text4" /></td>
    </tr>
    <tr>
			   <td  class="miaoshu">年级<font color="red">*</font></td>
			   <td class="shortInput"><select name="gradeNum" id="gradeNum" onchange="gradeChange()"></select> </td>
			   
			   <td class="shortInput"><input type="hidden" id="text4" name="text4" /></td>
    </tr>
    <tr>
			   <td  class="miaoshu">班级<font color="red">*</font></td>
			   <td class="shortInput"><select name="classNum" id="classNum" onchange="classNumChange()" ></select> </td>
			   
			   <td class="shortInput"><input type="hidden" id="text4" name="text4" /></td>
    </tr>
    <tr>
			   <td class="miaoshu">学生姓名<font color="red">*</font></td>
				<td class="shortInput"><select name="studentName" id="studentName" ></select> </td>
		        <td class="shortInput"><input type="hidden" id="text4" name="text4" /></td>
    </tr>
	<tr>
		<td class="miaoshu">家长姓名&nbsp;&nbsp;</td>
		<td class="shortInput"><input type="text" id="parentName" name="parentName" /></td>
	</tr>
    <tr>
			   <td  class="miaoshu">手机号<font color="red">*</font></td>
			   <td class="shortInput"><input type="text" id="mobile" name="mobile" /></td>
    </tr>
    <tr>
			   <td class="miaoshu">密码<font color="red">*</font></td>
			   <td class="shortInput"><input type="password" id="password" name="password" placeholder="6-20位字母和数字组合" /></td>
    </tr>
    <tr>
			   <td class="miaoshu">确认密码<font color="red">*</font></td>
			   <td class="shortInput"><input type="password" id="confirmPassword" name="confirmPassword" /></td>
    </tr>

    <tr>
			   <td  class="miaoshu">验证码:<font color="red">*</font></td>
			   <td class="shortInput"><input type="text" id="code" name="code" /></td>
<!-- 			   <td class="shortInput"><input type="text" id="code" name="code" onblur="checkCode(this)"/></td> -->
			   <td  style="text-align: left"><input onclick="sendCode(this)" class="button_default" onmouseOut="this.className='button_mouseOver'" 
				onmouseOver="this.className='button_default'" id="Code"  type="button"  value="获取验证码" style="width: 120px"/></td>
    
    </tr>
<!--     <tr>
			   <td style="margin-left:30px">姓名:<font color="red">*</font></td>
			   <td style="text-align: left"><input type="text" id="realname" name="realname"/></td>
    </tr>
    <tr>
			   <td style="margin-left:30px">与学生的关系:<font color="red">*</font></td>
			   <td style="text-align: left"><select name="studentRelation" id="studentRelation"></select>
			                              
    </tr>
    <tr>
			   <td  style="margin-left:30px">学籍号:<font color="red">*</font></td>
			   <td style="text-align: left"><input type="text" id="userid" name="userid" onblur="checkStuId();" /> </td>
    </tr>
     <tr>
			   <td  style="margin-left:30px">学生姓名:<font color="red">*</font></td>
			   <td style="text-align: left"><input type="text" id="studenttName" name="studenttName" onblur="checkStuName();"/></td>
    </tr>
    <tr>
			   <td  style="margin-left:30px">邮箱:</td>
			   <td style="text-align: left"><input type="text" id="email" name="email"/></td>
    </tr>
    <tr>
			   <td  style="margin-left:30px">联系电话:<font color="red">*</font></td>
			   <td style="text-align: left"><input type="text" id="mobile" name="mobile" onblur=" checkPhone(this)"/></td>
    			<td  style="text-align: left"><input onclick="sendCode(this)" class="button_default" onmouseOut="this.className='button_mouseOver'" 
				onmouseOver="this.className='button_default'" id="Code"  type="button"  value="获取验证码" style="width: 120px"/></td>
    
    </tr>
    <tr>
			   <td  style="margin-left:30px">验证码:<font color="red">*</font></td>
			   <td style="text-align: left"><input type="text" id="code" name="code" onblur=" checkCode(this)"/></td>
    
    </tr>
	<tr>
			   <td style="margin-left:30px">密码:<font color="red">*</font></td>
			   <td style="text-align: left"><input type="password" id="password" name="password" /></td>
    </tr>
	<tr>
			   <td  style="margin-left:30px">重复密码:<font color="red">*</font></td>
			   <td style="text-align: left"><input type="password" id="repwd" onblur="checkPas();"/></td>
    </tr> -->
   
<!--     <td style="height=24px line-height=24px;text-align: center"></td></tr> -->
<!--     <td style="height=24px line-height=24px"class="tongyi"><input type="checkbox" class="apply"  id="checkhttp"/><a href = "http://www.baidu.com">我同意《用户协议》</a></td></tr> -->
	<tr>
<%--		<td colspan="3" style="text-align: right;color:green" id="info"> <a>*手机号主要用于登录、身份验证和密码找回，请认真填写！</a></td>--%>
		<td colspan="2" style="text-align: center;color:red" id="info">
			<span>一个手机号每个年级只能绑定一个学生！</span>
			<br>
			<span>一个学生最多绑定两个手机号！</span>
		</td>
	</tr>
	
	<tr>
		<td>
		&nbsp;<input onclick="tiao()" class="button_default" onmouseOut="this.className='button_mouseOver'" 
		onmouseOver="this.className='button_default'"  type="button"  value="返&nbsp;回" style="width: 120px"/>
		</td>
		<td >&nbsp;<input onclick="tijiao()" class="button_default" onmouseOut="this.className='button_mouseOver'" 
		onmouseOver="this.className='button_default'"  type="button"  value="提&nbsp;交" style="width: 120px"/>
		</td>
	<tr>
	</table>

</div>
</form>
</body>
</html>
