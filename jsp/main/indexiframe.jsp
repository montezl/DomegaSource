<%@ page contentType="text/html; charset=utf-8" language="java" import="cn.hutool.core.convert.Convert,com.dmj.util.StaticClassResources,com.dmj.util.app.EdeiInfo" %>
<%@ page contentType="text/html; charset=utf-8" language="java"
         import="com.dmj.util.config.Configuration,java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
    String sId = null;
    String isTruePassword = "0";
    if (session != null) {
        sId = session.getId();
        isTruePassword = Convert.toStr(session.getAttribute("isTruePassword"));
    }
    Map<String, String> skinSetting = StaticClassResources.SkinSetting;
    EdeiInfo edeiInfo = StaticClassResources.EdeiInfo;
    String systemTitle = skinSetting.get("systemTitle");
    String systemName = edeiInfo.getVendor();
    String isEnforceChangePassword=Configuration.getInstance().getIsEnforceChangePassword();

%>
<html onResize="">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><%=systemName%>
    </title>

    <link rel="shortcut icon" href="<%=path%>/common/image/main/favicon.ico"/>
    <script type="text/javascript" src="<%=basePath%>common/js/jquery.js"></script>

    <script language="javascript" type="text/javascript">
        if (<%=isTruePassword%>=='0'&& <%=isEnforceChangePassword%>=='1')
        {
            window.location = "loginAction!toOneupdatepass.action";
        }


        window.onbeforeunload = function checkLeave(e) {
            var session_Id = "<%=sId%>";
            var evt = e ? e : (window.event ? window.event : null);  //此方法为了在firefox中的兼容
            if (session_Id != null) {
                if (true) {
                    if (navigator.userAgent.indexOf("Firefox") > 0) {//判断浏览器是否属于ff
                        evt.returnValue = "退出系统..";
                    }
                    document.location = "loginAction!loginOut.action";
                }
            }
        };
        // ---页面关闭时立即销毁session，而不等待session自动过期---------结束


    </script>
    <script language="javascript" type="text/javascript">
        function updateheight() {

            var divheight = window.screen.height;
            var divwidth = window.screen.width;
// 					 $("#zhegai").css("height",divheight+"px");
// 					 $("#zhegai").css("width",divwidth+"px");
            $("#zhegai").css("display", "inline");
        }

        function hidediv() {
            $("#zhegai").css("display", "none");
        }

    </script>
    <style type="text/css">
        .html {
            overflow-y: hidden;
        }

        .body {
            overflow-x: hidden;
            overflow-y: hidden;
        }

        .opacity {
            filter: alpha(opacity=50);
            /* IE */
            -moz-opacity: 0.5;
            /* 版Mozilla */
            -khtml-opacity: 0.5;
            /* 版Safari */
            opacity: 0.5;
            /* 支持opacity浏览器*/
        }

        .teacherReportTaoCanNav {
            display: flex;
            display: -webkit-flex;
            flex-direction: row;
            justify-content: center; /* 子元素水平居中 */
            align-items: center; /* 子元素垂直居中 */
            position: absolute;
            left: 630px;
            top: 104px;
            height: 40px;
            line-height: 40px;
            background-color: #c2f8ed; /* 背景色 */
            border-radius: 5px;

            font-family: "Microsoft YaHei";
            font-stretch: normal;
            display: none;
        }

        .teacherReportTaoCanNav span {
            color: #ffffff;
            padding: 2px 10px;
            border-radius: 10px;
            cursor: pointer;
            margin: 0 5px;
        }

        .teacherReportTaoCanNav span:hover {
            color: #f70; /* 文字颜色 */
            margin: 0 5px;

        }

        .teacherReportTaoCanNavShow{
            display: flex;
            display: -webkit-flex;
        }
        .teacherReportTaoCanNav span.select{
            color: #f70; /* 文字颜色 */
            margin: 0 5px;
        }

    </style>

</head>
<body style="margin:0;padding:0;overflow-y: hidden;overflow-x: hidden;">
<div id="MarRight">
    <iframe id="ifm" name="ifm" scrolling="auto" frameborder="1" scrolling="no" frameborder="no"
            style="overflow-x: hidden;overflow-y: hidden;width:100%;height:100%"
            src="<%=path %>/jsp/main/index.jsp"></iframe>
</div>
<div id="zhegai"
     style="display:none;filter: Alpha(opacity=40);-moz-opacity: 0.4;  -khtml-opacity:0.5;opacity: 0.5;  top: 0px;left: 0px;background:#000;width:100%;height:100%;z-index: 33; position:fixed;_position:absolute;">
</div>
<div class="teacherReportTaoCanNav">
    <span hid="10" reportType="qu" name="区级报告">区级报告</span>
    <span hid="10" reportType="xiao" name="校级报告">校级报告</span>
    <span hid="10" reportType="ban" name="班级报告">班级报告</span>
</div>

<script>
    $(function () {
        $(".teacherReportTaoCanNav span").click(function () {
            $(this).addClass("select").siblings().removeClass("select");
        });
    })
</script>
</body>
</html>