<%@ page language="java" import="java.util.*,com.dmj.util.*" pageEncoding="utf-8"
         contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.dmj.util.config.Configuration" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";

    response.setHeader("Cache-Control", "no-cache");

    response.setHeader("Pragrma", "no-cache");
    response.setDateHeader("Expires", -10);
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");

    Map<String, String> skinSetting = StaticClassResources.SkinSetting;
    String skin = skinSetting.get("skin");
    String systemTitle = skinSetting.get("systemTitle");
    String color = skinSetting.get("color");

    //读配置文件  G1类层全区显示是否加限制
// 	String isShowG1Quanqu = getServletContext().getAttribute(Const.isShowG1Quanqu).toString();

    String exportExcel = Configuration.getInstance().getExportExcel();

%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>导航</title>
    <%--<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />--%>
    <meta http-equiv="x-ua-compatible" content="ie=8"/>
    <meta HTTP-EQUIV="pragma" CONTENT="no-cache">
    <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate">
    <meta HTTP-EQUIV="expires" CONTENT="-10">


    <%--<link rel="stylesheet" href="<%=basePath%>common/css/main.css" type="text/css" />--%>
    <link rel="stylesheet" href="<%=basePath%>colors/<%=color%>.css" type="text/css"/>
    <link rel="stylesheet" href="<%=basePath%>common/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css"></link>

    <script type="text/javascript" src="<%=basePath%>common/js/jquery.js"></script>

    <script type="text/javascript" src="<%=basePath%>common/js/lhgdialog/lhgdialog.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/reportParameter.js?v=4.5"></script>
    <%-- <script type="text/javascript" src="<%=basePath%>common/js/lhgdialog/demo.js"></script> --%>
    <script type="text/javascript" src="<%=basePath%>common/js/reportMenu.js?v=1.3"></script>

    <script type="text/javascript" src="<%=basePath%>common/js/zTree/js/jquery.ztree.core.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/zTree/js/jquery.ztree.all.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/zTree/js/jquery.ztree.exhide.min.js"></script>

    <script type="text/javascript" src="<%=basePath%>common/js/TwoTree/commonTree1.js?v=2.7.2"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/TwoTree/commonTree2.js?v=2.7"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/TwoTree/TwoTree.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/TwoTree/commentJs.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/teacherReportTaocanNav.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/getUserPositions.js"></script>

    <style type="text/css">
        body{
            margin: 8px;
        }
        .line {
            height: auto;
            float: none;
        }

        .layui-layer-btn a {
            padding: 0 12px;
            height: auto;
            line-height: 18px;
        }
    </style>
</head>

<script type="text/javascript" defer="defer">

    $().ready(function () {

        bindTeacherReportTaoCanNavEvent($("#teacherReportTaocanNav"));


        var exportExcel =<%=exportExcel%>;
        // console.log("导出:---" + exportExcel);

        if (exportExcel == 0) {
            $("#export").css("visibility", "hidden");
        }

        // $('#checkedLevel').multipleSelect({
        //     addTitle: true, //鼠标点悬停在下拉框时是否显示被选中的值
        //     selectAll: false, //是否显示全部复选框，默认显示
        //     name: "质控级别",
        //     selectAllText: "选择全部", //选择全部的复选框的text值
        //     allSelected: "全部", //全部选中后显示的值
        //     //delimiter: ', ', //多个值直接的间隔符，默认是逗号
        //     placeholder: "质控级别" //不选择时下拉框显示的内容
        // });
        // $("#checkedLevel").multipleSelect('setSelects', [1001, 1002]);
        // $('#selectJcjb').multipleSelect("close");

    });

    /* $().ready(function(){
        jQuery('select').change(function(){
            //alert(this.id+" , "+this.value);
            saveSelectValue(this);
        });

        /* var examMenu = jQuery('span[id="3"]');
        if(examMenu){
            examMenu.click();
        }else{
            jQuery('span[name="menu"]:first').click();
        }


    // function test(aa,bb,para){
    // 	alert(aa);
    // 	alert(bb);
    // 	alert(para.type);

    // }

    // test('G1 成绩综合指标','teacher/3_1_allIndex.rptdesign',{"c_exam":"","isCheckSchool":"","isShowAllGrade":"false","isShowRank":"true","isShowSubjectType":"","isShowTotalScore":"true","ischeck":"false","showAllExaminationRoom":"","showStep":"","showSubject":"true","type":"1"});

    }); */
    function asd() {
        var ff = window.parent.window.document.getElementsByTagName("frameset");
        ff.updateheight();
    }

    function gettabId() {
        return $("#tabId").val();
    }

    function toteacherxd2() {
        var num = document.getElementById("num").value;
        $.ajax({
            type: "POST",//用POST方式传输
            cache: false,
            async: false,
            dataType: "json",//数据格式:JSON
            url: 'ajaxAction!verifyCharge.action',//目标地址
            data: {
                ziyuan_num: $("#num").val()
            },
            success: function (d) {
                if (d.code == 200) {
                    dg = new $.dialog({
                        id: '11',
                        content: 'url: teacherXindeAction!toTeacherXinde.action',
                        title: '<span class="addGroup"></span>&nbsp;教师评论',
                        cancelval: '关闭',
                        width: '850px',
                        height: '500px'
                    });
                    dg.ShowDialog();
                } else if (d.code == 501 || d.code == 502) {//已过期
// 					var r=confirm("您现在没有此报表使用权限，请购买相应内容。是否需要购买?");
// 					if(r==true){
// 						window.open('taocanbuy.jsp',"newwindow", "toolbar =no, menubar=no, scrollbars=no, resizable=no, location=no, status=no") ;
// 					}

                    dg = new $.dialog({
                        background: '#000',
                        opacity: 0.5,
                        id: '0000',
                        content: 'url: loginAction!sfPrompt.action',
                        title: '&nbsp;了解更多学情，请购买以下增值服务套餐！',
                        width: '500px',
                        height: '250px',
                        max: false,
                        min: false,
                        cache: false,
                        cancel: false,
                        esc: false,
                        lock: true
                    });
                    dg.show();
                    return;
                } else {
                    alert(d.msg);
                    return;
                }
            }
        });

// }
// var url="<%=basePath%>jsp/report/teacherXinde.jsp";
// var url="<%=basePath%>jsp/report/texty.jsp";
// var url="<%=basePath%>jsp/report/123456789.jsp";


    }

    function getstep(stepval) {
        if (stepval < 1 || stepval % 1 != 0) {
            alert("步长应为大于0的整数！")
            $('#step').val("");
        }

    }


</script>
<body>
<input type="hidden" id="rpt_name" value=""/>
<input type="hidden" id="rpt_url" value=""/>
<input type="hidden" id="type" value=""/>
<input type="hidden" id="show_sjt" value=""/>
<input type="hidden" id="check" value=""/>
<input type="hidden" id="isShowTotalScore" value=""/>
<input type="hidden" id="isShowAllGrade" value=""/>
<input type="hidden" id="isShowStep" value=""/>
<input type="hidden" id="isHistory" value=""/>
<input type="hidden" id="isMoreSchool" value=""/>
<input type="hidden" id="tabNum" value=""/>
<input type="hidden" id="tabId" value=""/>
<input type="hidden" id="num" value=""/>
<input type="hidden" id="openAllSchool" value=""/>
<input type="hidden" id="nameSpan" value=""/>
<%-- <input type="hidden" id="isShowG1Quanqu" value="<%=isShowG1Quanqu %>" /> --%>
<!-- 当前系统是否启用教学版  -->
<input type="hidden" id="levelclass" value="<c:out value="${Configuration.getInstance().getLevelclass()}" />"/>
<input type="hidden" id="marktype" value="<c:out value="${Configuration.getInstance().getType()}" />"/>
<input type="hidden" id="usertype" value="${sessionScope.loginuser.usertype}">
<input type="hidden" id="studentId" value="${sessionScope.studentId}">
<input type="hidden" id="orderinfo" value="${sessionScope.orderinfo}">
<input type="hidden" id="openocs" value="${sessionScope.openocs}">
<input type="hidden" id="logType" value="${sessionScope.logType}">
<input type="hidden" id="licensedays" value="${sessionScope.licensedays}">
<input type="hidden" id="reportType" value="">

<!-- 当前年纪是否是教学版  -->
<input type="hidden" id="islevelclass" value="<c:out value="F" /> "/>
<input type="hidden" id="basePath" value="<%=basePath%>"/>
<div id="nav" class="nav-bg-color" style="width:100%">

    <%--	<img src="../../common/image/main/menu/d.jpg"/>--%>
    <c:forEach var="aa" items="${sessionScope.list}">
        <c:choose>
            <c:when test='${aa.key == "6" && applicationScope.allowedToViewReport == "0"}'>
			<span name="menu" id="${aa.key}" onclick="changeMenu(id,'<c:out value="${aa.value}"></c:out>')"
                  class="menu_default">
                    ${aa.value}
            </span>
                <span>
		     	 <input type="hidden" id="dd" name="dd" value="${aa.key}"/>
		    	 <input type="hidden" id="nn" name="nn" value="${aa.value}"/>
	     	</span>
            </c:when>

            <c:when test='${aa.key == "10" && applicationScope.allowedToViewReport == "0"}'>
			<span id="teacherReportTaocanNav" name="menu" id="${aa.key}"
                  class="menu_default">
                    ${aa.value}
            </span>
                <span>
		     	 <input type="hidden" id="dd" name="dd" value="${aa.key}"/>
		    	 <input type="hidden" id="nn" name="nn" value="${aa.value}"/>
	     	</span>
            </c:when>

            <c:otherwise>
			<span name="menu" class="menu_default" id="${aa.key}"
                  onclick="tomarkhtml(id,'<c:out value="${aa.value}"></c:out>')">${aa.value}
			     <input type="hidden" id="dd" name="dd" value="${aa.key}"/>
			     <input type="hidden" id="nn" name="nn" value="${aa.value}"/>
		     </span>
            </c:otherwise>
        </c:choose>


<%--        <c:if test='${aa.key != "6"}'>--%>
<%--		     <span name="menu" class="menu_default" id="${aa.key}"--%>
<%--                   onclick="tomarkhtml(id,'<c:out value="${aa.value}"></c:out>')">${aa.value}--%>
<%--			     <input type="hidden" id="dd" name="dd" value="${aa.key}"/>--%>
<%--			     <input type="hidden" id="nn" name="nn" value="${aa.value}"/>--%>
<%--		     </span>--%>
<%--        </c:if>--%>
<%--        <c:else></c:else>--%>
<%--        <c:if test='${applicationScope.allowedToViewReport == "0"}'>--%>
<%--            <c:if test='${aa.key == "6"}'>--%>
<%--	     	<span name="menu" id="${aa.key}" onclick="changeMenu(id,'<c:out value="${aa.value}"></c:out>')"--%>
<%--                  class="menu_default">--%>
<%--&lt;%&ndash;	     	<span style="padding:0 9px;"></span>&ndash;%&gt;--%>
<%--	     	${aa.value}--%>
<%--&lt;%&ndash;	     	<span style="padding:0 9px;"></span>&ndash;%&gt;--%>
<%--	     	</span>--%>
<%--                <span>--%>
<%--		     	 <input type="hidden" id="dd" name="dd" value="${aa.key}"/>--%>
<%--		    	 <input type="hidden" id="nn" name="nn" value="${aa.value}"/>--%>
<%--	     	</span>--%>
<%--            </c:if>--%>

<%--            <c:if test='${aa.key == "10"}'>--%>
<%--	     	<span name="menu" id="${aa.key}111111" onclick="changeMenu(id,'<c:out value="${aa.value}"></c:out>')"--%>
<%--                  class="menu_default">--%>
<%--&lt;%&ndash;	     	<span style="padding:0 9px;"></span>&ndash;%&gt;--%>
<%--	     	${aa.value}--%>
<%--&lt;%&ndash;	     	<span style="padding:0 9px;"></span>&ndash;%&gt;--%>
<%--	     	</span>--%>
<%--                <span>--%>
<%--		     	 <input type="hidden" id="dd" name="dd" value="${aa.key}"/>--%>
<%--		    	 <input type="hidden" id="nn" name="nn" value="${aa.value}"/>--%>
<%--	     	</span>--%>
<%--            </c:if>--%>
<%--        </c:if>--%>
    </c:forEach>
    <!--	<li id="man_nav_1" onclick="toMenu(id,'教学信息','teachleftMenu.jsp')"  class="bg_image_onclick">教学信息</li>-->
    <!--	<li id="man_nav_2" onclick="toMenu(id,'考试管理','examleftMenu.jsp')"  class="bg_image">考试管理</li>-->
    <!--	<li id="man_nav_3" onclick="toMenu(id,'诊断分析','diagnoseMenu.jsp')"  class="bg_image">诊断分析</li>-->
    <!--	<li id="man_nav_4"  onclick="toMenu(id,'用户管理','userMenu.jsp')"  class="bg_image">用户管理</li>-->
    <!--	<li id="man_nav_5"  onclick="toMenu(id,'系统管理','systemMenu.jsp')"  class="bg_image">系统管理</li>-->
    <!--	<li id="man_nav_6"  onclick="changeMenu(id,'报表')"  class="bg_image">报表<span style="padding: 0 5px"></span></li>-->
    <li style="display:none" id="man_nav_6" onclick="list_sub_nav(id,'帮助')" class="bg_image">帮助</li>
    <!--
    -->
</div>
<%--<div style="height:5px;"></div>--%>
<%--<div id="split"></div>--%>
<input type="hidden" id="ii" name="ii" value="${sessionScope.loginuser.id}"/>


<div id="auto" style="display:none;border-bottom:2px solid #fff;">
    <%--<%@include file="/jsp/jsSelectBox.html" %>	--%>
    <div style="font-size:1pt;height:3px;width:100%"></div>
    <span style="padding: 0 4px"></span>

    <span style="display:" id="examspan">
              考试：<select id="exam" name="exam" style="width: 120px"></select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:none" id="gradespan">
	年级：<select id="grade" name="grade" style="width: 60px"></select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:" id="subjectTypespan">
	科类：<select id="subjectType" name="subjectType" style="width: 60px"></select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:none" id="subComposespan">
	<span id="subComposeWord">选科组合</span>：<select id="subCompose" name="subCompose" style="width: 60px">
	</select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:" id="subjectspan">
	科目：<select id="subject" name="subject" style="width: 60px"></select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:none" id="areaspan" class="spans">
	<!-- 类层：<select id="area" name="area" style="width: 100px"></select><span style="padding:0 5px"></span> -->
	类层：<input type="text" id="areaShow" name="areaShow" style="width: 60px;height:20px;"><input id="area" name="area"
                                                                                                style="width: 100px"
                                                                                                type="hidden"><span
            style="padding:0 5px"></span>
	</span>
    <span style="display:" id="schoolspan">
	学校：<select id="school" name="school" style="width: 100px"></select><span style="padding:0 5px"></span>
	</span>
    <span style="display:none" id="islevelspan">
	行政/教学班：<select id="islevel" name="islevel" style="width: 100px">
	</select><span style="padding:0 5px"></span>
	</span>
    <span style="display:" id="teachUnitspan">
	教学单位：<input type="text" autocomplete="off" id="teachUnitShow" name="teachUnitShow" placeholder="请选择"
                style="width: 100px;height:20px;"><input id="teachUnit" name="teachUnit" style="width: 100px"
                                                         type="hidden">
	<input id="teachParentUnit" name="teachParentUnit" style="width: 100px" type="hidden">
	<input id="teachUnit_statistic" name="teachUnit_statistic" style="width: 100px" type="hidden"><span
            style="padding:0 5px"></span>
	</span>
    <span style="display:none" id="classspan">
	班级：<select id="class" name="class" style="width: 60px"></select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:none" id="studentspan">
	学生：<select id="student" name="student" style="width: 100px"></select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:" id="contrastspan">
	对比对象：<input type="text" autocomplete="off" id="contrastShow" name="contrastShow" placeholder="请选择"
                style="width: 100px;height:20px;"><input id="contrast" name="contrast" style="width: 100px" type="hidden">
	<input id="contrastParent" name="contrastParent" style="width: 100px" type="hidden">
	<input id="contrast_statistic" name="contrast_statistic" style="width: 100px" type="hidden"><span
            style="padding:0 5px"></span>
	</span>
    <!-- <span style="display:none" id="qTypespan">
    题型：<select  id="qType" name="qType" style="width: 60px">
        <option value="-1">全部</option>
        <option value="0">客观题</option>
        <option value="1">主观题</option>
    </select><span style="padding: 0 5px"></span>
    </span> -->
    <span style="display:none" id="qNumspan">
	题号：<select id="qNum" name="qNum" style="width: 60px"><option value="-1">全部</option></select><span
            style="padding: 0 5px"></span>
	</span>
    <span style="display:none" id="c_examspan">
	参照考试：<select id="c_exam" name="c_exam" style="width: 100px"></select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:none" id="rangespan">
	由<input type="text" value="1" id="rangefrom" style="width: 40px;text-align:center"/>名~至
	<input type="text" value="50" id="rangeto" style="width: 40px;text-align:center"/>名<span
            style="padding: 0 5px"></span>
	</span>
    <span style="display:none" id="stepspan">
	步长：<input type="text" value="10" id="step" name="step" style="width: 40px;text-align:center"
              onblur="getstep(this.value)">
<%--		<input type="text" onkeyup="if(!/^[1-9]+$/.test(value)) value=value.replace(/\D/g,'');if(value<=0)value=null"  value="10" id="step" name="step" style="width: 40px;text-align:center">--%>
<%--		<input type="number" oninput="if(!/^[0-9]+$/.test(value)) value=value.replace(/\D/g,'');if(value>100)value=100;if(value<0)value=null" value="10" id="step" name="step" style="width: 40px;text-align:center">--%>
<%--		<input type="number" value="10" id="step" name="step" style="width: 40px;text-align:center" onchange="if(!/^[1-9]+$/.test(value)) value=value.replace(/\D/g,'')">--%>
		<span style="padding: 0 5px"></span>
	</span>
<%--  E5stepspan  E5-分段统计-实力对比报表使用的步长    --%>
    <span style="display:none" id="E5stepspan">
    步长：<select name="E5step" id="E5step">
        </select>
    <span style="padding: 0 5px"></span>
    </span>
    <span style="display:none" id="mingcistepspan">
	名次步长：<input type="text" value="100" id="mingcistep" name="mingcistep" style="width: 40px;text-align:center"
              onblur="getstep(this.value)">
		<span style="padding: 0 5px"></span>
	</span>

<%--        B3-名次分布 报表使用的名次步长--%>
    <span style="display:none" id="B3mingcistepspan">
    名次步长：<select name="B3mingcistep" id="B3mingcistep">
        </select>
    <span style="padding: 0 5px"></span>
    </span>

    <span style="display:none" id="topspan">
	前<input type="text" value="" id="top" name="top"
            style="width: 40px;text-align:center">人对比<span style="padding: 0 5px"></span>
	</span>

    <span style="display:none" id="selectrankspan">
	前<input type="text" value="2000" id="selectrank" style="width: 40px;text-align:center"/>名<span
            style="padding: 0 5px"></span>
	</span>
    <span style="display:none" id="avgspan">
	平均分<input type="text" value="50" id="avg" style="width: 40px;text-align:center"/><span
            style="padding: 0 5px"></span>
	</span>
    <span style="display:none" id="tagspan">
	标记：<select id="tag" name="tag" style="width: 60px"></select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:" id="graduationTypespan">
	在籍类型：<select id="graduationType" name="graduationType" style="width: 50px"></select><span
            style="padding: 0 5px"></span>
	</span>
    <span style="display:" id="stuSourcespan">
	生源：<select id="stuSourceType" name="stuSourceType" style="width: 50px"></select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:" id="fufenspan">
	分数源：<select id="fufen" name="fufen" style="width: 60px"></select><span style="padding: 0 5px"></span>
	</span>
    <span style="display:none" id="picispan">
	批次：<select id="pici" name="pici" style="width: 60px"></select><span style="padding: 0 5px"></span>
	</span>
    <input type="button" id="search_btn" value="查询" disabled="" style="width:80px"
           onclick='javascript:toReport3(document.getElementById("rpt_name").value,document.getElementById("type").value,document.getElementById("show_sjt").value,document.getElementById("check").value)'/>
    <span style="padding:0 6px"></span>
    <a id="export"
       href='javascript:toReport3_Export(document.getElementById("rpt_name").value,document.getElementById("type").value,document.getElementById("show_sjt").value,document.getElementById("check").value)'>
        导出</a>
    <span style="padding:0 10px"></span>
    <!-- 	<span>全部<input type="radio" checked="checked" name="type"  value="0"/>&nbsp;&nbsp;&nbsp;在籍学生<input type="radio" name="type" value="1" /></span> -->
    <c:if test='${applicationScope.sysVersion == "1"}'>
        <!-- <a id="jspl" href="javascript:toteacherxd2()" style="display:none;">教师评论</a> -->
    </c:if>

</div>
<div id="class_sel_val" style="display:none">
    <%-- 放置动态获取的班级内容 --%>
</div>
<div id="school_sel_val" style="display:none">
    <%-- 放置动态获取的班级内容 --%>
</div>
<div id="area_sel_val" style="display:none">
    <%-- 放置动态获取的区域内容 --%>
</div>
<div id="qNum_sel_val" style="display:none">
    <%-- 放置动态获取的题号内容 --%>
</div>
<div id="c_exam_sel_val" style="display:none">
    <%-- 放置动态获取的题号内容 --%>
</div>
<div class="zTreeDemoBackground left" style="display:none">
    <%-- 放置初始化教学单位树 --%>
    <ul id="commonTree1Div" class="ztree"></ul>
</div>
<div class="zTreeDemoBackground left" style="display:none">
    <%-- 放置初始化对比对象树 --%>
    <ul id="commonTree2Div" class="ztree"></ul>
</div>


<input type="hidden" id="exam_val" value="">
<input type="hidden" id="subject_val" value="">
<input type="hidden" id="area_val" value="">
<input type="hidden" id="areaName_val" value="">
<input type="hidden" id="school_val" value="">
<input type="hidden" id="subjectType_val" value="">
<input type="hidden" id="grade_val" value="">
<input type="hidden" id="class_val" value="">
<input type="hidden" id="qNum_val" value="">
<input type="hidden" id="student_val" value="">
<input type="hidden" id="c_exam_val" value="">
<input type="hidden" id="step_val" value="">
<input type="hidden" id="tag_val" value="">
<input type="hidden" id="reportNum">
<input type="hidden" id="multiple_class_val" value="">
<input type="hidden" id="multiple_qNum_val" value="">
<input type="hidden" id="multiple_school_val" value="">
<input type="hidden" id="multiple_area_val" value="">
<input type="hidden" id="multiple_c_exam_val" value="">

<input type="hidden" id="graduationType_val" value="">
<input type="hidden" id="stuSourceType_val" value="">

<input type="hidden" id="rangefrom_val" value="">
<input type="hidden" id="rangeto_val" value="">
<input type="hidden" id="fufen_val" value="">
<input type="hidden" id="pici_val" value="">

<input type="hidden" id="subCompose_val" value="">
<input type="hidden" id="islevel_val" value="">
<input type="hidden" id="teachUnit_val" value="">
<input type="hidden" id="teachUnitShow_val" value="">
<input type="hidden" id="teachParentUnit_val" value="">
<input type="hidden" id="teachUnit_statistic_val" value="">
<input type="hidden" id="teachUnitShowNum" value="">
<input type="hidden" id="contrast_val" value="">
<input type="hidden" id="contrastShow_val" value="">
<input type="hidden" id="contrast_statistic_val" value="">
<input type="hidden" id="contrastParent_val" value="">
<input type="hidden" id="contrastShowNum" value="">
<input type="hidden" id="numOfStudent" value="">
<div id="zhegai" style="background:#000;">
</body>
</html>

