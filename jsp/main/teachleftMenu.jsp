<%@ page language="java" import="com.dmj.util.StaticClassResources" pageEncoding="utf-8"
         contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
    Map<String, String> skinSetting = StaticClassResources.SkinSetting;
    String skin = skinSetting.get("skin");
    String systemTitle = skinSetting.get("systemTitle");
    String color = skinSetting.get("color");
%>
<%
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "No-cache");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
    <title>信息提取</title>
    <meta http-equiv="Content-Type" content="ie=8"/>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>
    <meta http-equiv="description" content="This is my page"/>

    <script type="text/javascript" src="<%=basePath%>/common/js/jquery.js"></script>
    <link rel="stylesheet" href="<%=basePath%>colors/<%=color%>.css" type="text/css"/>
</head>
<script language="javascript" type="text/javascript">
    var datas = "";
    $().ready(function () {
        //点击切换菜单 颜色
        $('li[name="menu"]').click(function () {
            $('li[name="menu"]').removeClass().addClass("list_detail_default");
            $(this).removeClass().addClass("list_detail_selected");
        });

        $.ajax({
            type: "POST",//用POST方式传输
            cache: false,
            async: false,
            dataType: "html",//数据格式:JSON
            url: 'loginAction!getRolesYjy.action', //目标地址
            success: function (data) {
                datas = data;
                var mark = $("#mark").val();
                if (data != 0 && mark == 0) {
// 					var ifr = document.getElementById('manframes');
                    url = "<%=basePath%>jsp/awardPoint/awardPointlist.jsp";
                    window.parent.frames['manFrame'].location = url;
// 					window.top.frames['manFrame'].location = url;
// 				ifr.src ="<%=basePath%>jsp/awardPoint/awardPointlist.jsp";
                }
            }
        });
        var userid = $("#userid").val();
        if (userid == -2) {

            $("#ctbyc").show();
        }
        titlexxtq();
        firstResource();
        showYueJuan();
        id = $("#hideid").val();
        seximg();
        setAutoHeight();
        var logtype = $("#logtype").val();
        if (logtype == 'P') {
            document.getElementById("user_info").style.display = "none";
            document.getElementById("userparent").style.display = "block";
        } else {
            document.getElementById("user_info").style.display = "block";
            document.getElementById("userparent").style.display = "none";
        }

    });

    // function hideorshow(divid, subsortid) {
    // 	if (getObject(subsortid).style.display == "none") {
    // 		getObject(subsortid).style.display = "block";
    // 		getObject(divid).className = "list_detail_default";
    // 	} else {
    // 		getObject(subsortid).style.display = "none";
    // 		getObject(divid).className = "list_detail_selected";
    // 	}
    // }
    function getObject(objectId) {
        if (document.getElementById && document.getElementById(objectId)) {
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

    function getResource(pnum, divid, url2) {
        var mark = $("#mark").val();
        var reportType = "";
        $.ajax({
            type: "POST",//用POST方式传输
            cache: false,
            async: false,
            dataType: "json",//数据格式:JSON
            url: 'loginAction!getResource.action?pnum=' + pnum, //目标地址
            success: function (data) {
                /** */
                var con = $('#' + divid);
                con.html('');
                var html = '';
                $.each(data, function (index, obj) {
// 						if(reportType!=obj.reportType && reportType!=""){
// 						html +='<br/>';
// 					}
// 					if(obj.fn==0){
                    //action
                    // 					html +='<span class="success" style="height: 5px" onclick="addCollect(\''+obj.num+'\')"></span>';
// 	onclick="change(\''+obj.url+'\','+obj.para+')"
// 					onMouseOver="showgn(this,\''+ obj.num+ '\');" onMouseOut="hidegn(this,\''+ obj.num + '\')"
                    if (datas != "" && mark == 0 && obj.num == "304") {
                        html += '<li name="rpt" class="rpt_detail_selected"  id="' + obj.url + '" >';
                    } else {
                        html += '<li name="rpt" class="rpt_detail_default"  id="' + obj.url + '" >';
                    }

                    html += '<a id="aa"  href=#>' + obj.name + '</a>&nbsp;&nbsp;';
// 						html +='<img id="'+obj.num+'" onclick="addCollect(\''+obj.num+'\')" src="/edei/common/image/jia.gif"  style="display:none;width:10px;height:10px"/>';
                    html += '</li>';
// 					}else{
                    //jsp
                    // 					html +='<span class="add" onclick="addCollect(\''+obj.num+'\')"></span>';
// 						html += '<li name="rpt"  class="rpt_detail_default"  id=""  onMouseOver="showgn(this,\''+ obj.num+ '\');" onMouseOut="hidegn(this,\''+ obj.num + '\')">';
// 						html += '<a id="aa" onclick="change2(\''+obj.url+'\')" href=#>'+obj.name+'</a>&nbsp;&nbsp;';
// 						html +='<img id="'+obj.num+'" onclick="addCollect(\''+obj.num+'\')" src="/edei/common/image/jia.gif"  style="display:none;width:10px;height:10px"/>';
// 						html += '</li>';
// 					}
                    // 				alert("obj.reportType="+obj.reportType);
// 					reportType=obj.reportType;
                });
                $(html).appendTo(con);

                addEvent();
            } // end success

        }); //end ajax
        $('#init_content li[name="rpt"]').click(function () {
            var path = $('#path').val();
            var url = $(this).attr('id');
            var basePath = $("#basePath").val();
            window.parent.frames['manFrame'].location = basePath + url;
//		window.parent.frames['manFrame'].location =url;
        });
    }//end fun
    /**
     小菜单切换
     */
    function addEvent() {
        $('li[name="rpt"]').click(function () {
            $('li[name="rpt"]').removeClass().addClass("rpt_detail_default");
            $(this).removeClass().addClass("rpt_detail_selected");
        });
    }

    //信息提取 有痕 无痕  标签名字改变
    function titlexxtq() {

// 	var mark=$("#mark").val();
// 	if(mark==1){
// 		$("#31hide").html('信息提取');
// 		$("#32hide").html('分数校对流程监控（有痕）');
// 	}
    }

    //得到第一个标签下的资源
    function firstResource() {
        var firstnum = $("#one").val();
        var firstid = $("#hideid").val();
        getResource(firstnum, firstid);
    }

    // 	var id= "";
    function hideorshow(divid, subsortid) {

        if (getObject(subsortid).style.display == "none") {
            getObject(subsortid).style.display = "block";
            if (id == subsortid) {
                id = "";
            }
            if (id != null && id != "") {
                getObject(id).style.display = "none";
            }
            id = subsortid;

        } else {
// 			id="";
            getObject(subsortid).style.display = "none";
        }

    }


    //sex 图片
    function seximg() {
        var sex = $("#sex").val();
        if (sex == "男") {
            var url = "<%=basePath%>/common/image/main/left/person_man-01.png";
            $(".seximg").attr("src", url);
        }
    }


    function setAutoHeight() {
        var height = document.documentElement.clientHeight;
// 	alert(height/2);
// 	var height= document.body.clientHeight; 

//  	var heightb=height/2;
        $(".rpt_detail_content").css("height", height / 2 + "px");
    }

    function tocreatehistory() {
        var basePath = $("#basePath").val();
        var url = basePath + "/jsp/historyTable/historyTable.jsp";

//	window.open(url);
        //openoOneWindow(url) ;

//    function openoOneWindow(url) {
        //判断是否打开
//         if (objWin == null || objWin.closed) {
//            window.document.location.href=url;
        window.parent.frames['manFrame'].location = url;
//         } else {
//             objWin.location.replace(url);
//         }
//         objWin.focus();
//    }


    }

    //查看当前登录人是否有阅卷任务这一资源的查看权限，有就展示这个页面
    function showYueJuan() {
        $.ajax({
            type: "POST",//用POST方式传输
            cache: false,
            async: false,
            dataType: "html",//数据格式:JSON
            url: 'loginAction!showYueJuan.action?', //目标地址
            success: function (data) {
                if (data == 'T') {
                    //change2('阅卷任务','awardPointAction!execute.action',null,'3502');
                    //将JSON字符串转化为JSON对象
                    var para = '';

                    //根据参数 判断是否显示前--名 输入框
                    /* 	if(toBoolean(para.isShowRank)){
                            //显示显示前--名 输入框
                            window.parent.frames['mainFrame'].document.getElementById("selectrankspan").style.display='';
                        }else{ */
                    //隐藏显示前--名 输入框
                    window.parent.frames['mainFrame'].document.getElementById("selectrankspan").style.display = 'none';
                    //};
//				alert(path.indexOf("TeacherScoreAnaly"));
                    // 如果是教师成绩统计表 则隐藏科类，其他报表显示科类项
                    //if(path.indexOf("TeacherScoreAnaly")== -1){
                    window.parent.frames['mainFrame'].document.getElementById("subjectTypespan").style.display = '';
                    /* }else{
                        window.parent.frames['mainFrame'].document.getElementById("subjectTypespan").style.display='none';
                    } */

                    //修改主界面url的value
                    window.parent.frames['mainFrame'].document.getElementById("rpt_url").value = 'awardPointAction!execute.action';
                    //修改主界面rpt_name的value
                    window.parent.frames['mainFrame'].document.getElementById("rpt_name").value = '阅卷任务';
                    window.parent.frames['mainFrame'].document.getElementById("num").value = '3502';
                    window.parent.frames['mainFrame'].toExampaper('awardPointAction!execute.action', '', '阅卷任务', '3502');
                }
            }
        });
    }
</script>
<body class="left left-bg-color">
<input type="hidden" id="path" value="<%=basePath%>"/>
<input type="hidden" id="sex" value="${sessionScope.sex}"/>
<input type="hidden" id="mark" value="${sessionScope.mark}"/>
<input type="hidden" id="loginUsername" value="${sessionScope.loginuser.username}"/>
<input type="hidden" id="userid" value="${sessionScope.loginuser.id}"/>
<input type="hidden" id="basePath" value="<%=basePath%>">
<input type="hidden" id="logtype" value="${sessionScope.logType }">
<div>
    <div id="userparent" style="display: none">
		<div class="username">
			<span><c:out value="${sessionScope.loginparentuser.realname}"/></span>
		</div>
		<div class="rolename">
			<c:out value="${sessionScope.loginparentuser.roleName}"/>
		</div>
    </div>
    <div id="user_info">
        <div class="username">
            <span title="${sessionScope.loginuser.realname}"><c:out value="${sessionScope.loginuser.realname}"/></span>
        </div>
        <div class="rolename">
            <c:out value="${sessionScope.loginuser.roleName}"/>
        </div>
    </div>
</div>
<div id="left_content">
    <div class="list_tilte" id="init">
        <div align="center" style="margin-left:20px;height:30px;line-height:30px;float:left;">
            <img id="tabIdimg" style="width:20px;margin-top:5px;"
                 src="<%=basePath%>/common/image/main/left/teacheringInfo-01.png">
        </div>
        <div align="center" style="height:30px;line-height:30px;margin-left:20px;float:left;">
            <c:if test="${empty sessionScope.list2}">没有任何操作</c:if>
            <c:if test="${!empty sessionScope.list2}"><c:out value="${sortname}"></c:out></c:if>
        </div>
    </div>

    <!-- 内容-->
    <ul class="list_content" id="init_content">


        <c:forEach var="bb" items="${sessionScope.list2}" varStatus="status">

            <li name="menu" class="list_detail_default" id="${bb.num}"
                onclick="hideorshow('${bb.num}','${bb.para}');getResource('${bb.num}','${bb.para}','${bb.url}')">
		   		 		<span class="strip">
                                <%--			    			<img  style="width:20px;margin-top:6px;margin-left:5px;" src="<%=basePath%>/common/image/main/left/white_j.png" >--%>
                        </span>
                <span id="${bb.num}hide" class="text">${bb.name}</span>
            </li>
            <c:if test="${status.count == sessionScope.flagNum}">
                <input type="hidden" value="${bb.num}" id="one">
                <input type="hidden" value="${bb.para}" id="hideid">
                <li id="${bb.para}" class="rpt_detail_content">

                </li>
            </c:if>
            <li id="${bb.para}" style="display: none;" class="rpt_detail_content">

            </li>

        </c:forEach>
        <!-- 					<li onclick="tocreatehistory()" style="display:none" id="ctbyc" name="menu" class="list_detail_default" > -->
        <!-- 							<span class="strip" > -->
        <!-- 				    			<img  style="width:20px;margin-top:6px;margin-left:5px;" src="<%=basePath%>common/image/main/left/white_j.png" > -->
        <!-- 					    	</span> -->
        <!-- 							<span  class="text">创建历史表</span> -->
        <!-- 				</li> -->
    </ul>


</div>
</body>
</html>
