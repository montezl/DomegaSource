<%@ page contentType="text/html; charset=utf-8" language="java"
         import="com.dmj.util.Const,com.dmj.util.StaticClassResources,com.dmj.util.app.EdeiInfo,com.dmj.util.singleLogin.SingleLoginUtil" %>
<%@ page import="com.dmj.util.singleLogin.ZkhySingleLoginUtil" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.dmj.util.config.Configuration" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";

    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragrma", "no-cache");
    response.setDateHeader("Expires", 0);
    //读配置文件  版本号
    String version = getServletContext().getAttribute(Const.version).toString();

    String appQRcode_show = Configuration.getInstance().getAppQRcode_show();

    EdeiInfo edeiInfo = StaticClassResources.EdeiInfo;
    Map<String,String> skinSetting = StaticClassResources.SkinSetting;
    //读配置文件  appServer
    String asUrl = edeiInfo.getAsUrl();
    String studentApp_android = " ";
    String studentApp_ios = " ";
    String schoolUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    String studentUrl = asUrl + "/app/appUrl/student/" + version.split("\\s+")[0];
    String teacherUrl = asUrl + "/app/appUrl/teacher/" + version.split("\\s+")[0];
    String skin =skinSetting.get("skin");
    String systemTitle =skinSetting.get("systemTitle");
    String systemId = edeiInfo.getSystemId();
    String bindUrl = asUrl + "/server/getEdeiUrl/" + systemId;
    String serverkey = skin;
    String systemName = edeiInfo.getVendor();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <title><%=systemName%>-登录</title>
    <meta name="renderer" content="webkit"/>
    <base href="<%=basePath%>"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <meta name="renderer" content="webkit"/>
    <link href="<%=path%>/common/css/alogin.css" rel="stylesheet" type="text/css"/>
    <link href="<%=path%>/common/css/idangerous.swiper.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" href="skin/style.css"/>
    <script type="text/javascript" src="<%=basePath%>common/js/jquery.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/idangerous.swiper.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/common.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/login.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/fingerprint/fingerprint.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/jquery.cookie.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/lhgdialog/lhgdialog.min.js?skin=mac_cxj"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/alogin/jquery.qrcode.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/alogin/utf.js"></script>
    <script type="text/javascript" src="http://51baxue.com/config/edei.js" charset="UTF-8"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/alogin/createBarCode.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/crypto-js/crypto-js.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/crypto-js/secrt.js"></script>
    <script type="text/javascript">
        var sss = "<%=systemId%>";
        var appQRcode_show = "<%=appQRcode_show %>";
        var schoolUrl = "<%= schoolUrl%>";
        var bindUrl = "<%= bindUrl %>";
        var studentUrl = "<%= studentUrl %>";
        var teacherUrl = "<%= teacherUrl %>";
        var serverkey = "<%= serverkey %>";
        var zkhyPcShow = <%=ZkhySingleLoginUtil.pcShow()%>;
        var showThreeLogin = <%=SingleLoginUtil.showThreeLogin()%>;
        //document.getElementById("content").style.height=window.screen.height;
        jQuery().ready(function () {
            //根据IE版本隐藏或者显示二维码，目前不知道有啥用 暂时保留
            showHideBarCode();
            createSchoolBarCode(serverkey, bindUrl);
            createAppBarCode(serverkey, "teacherAndroidBarcode", "teacher", "android");
            createAppBarCode(serverkey, "teacherIosBarcode", "teacher", "ios");
            createAppBarCode(serverkey, "studentAndroidBarcode", "student", "android");
            createAppBarCode(serverkey, "studentIosBarcode", "student", "ios");
            //初始化页面给用户名密码赋值
            setValue();
            loadIe();
            //获取公告
            getNoticeInfo();

            var marks = GetCookie("marks");
            var fp = new Fingerprint();
            $("#fp").val(fp.get());
            $("#wos").val(detectOS());
            if (marks == null) {
                marks = 1;
            }
            var Constqueryusername = $("#Constqueryusername").val();
            if (Constqueryusername == 1) {
                $("#queryusername").show();
            } else {
                // 			不显示
                $("#queryusername").hide();
            }

            $("div.logType input[name='logType']").on("change", function () {
                if ($(this).val() == "P") {
                    $(".jiazhangzhuce").show();
                    $(".changeDeng").hide();
                } else {
                    $(".jiazhangzhuce").hide();
                    $(".changeDeng").show();
                }
            })
            //第三方登录控制
            if(showThreeLogin){
                $(".three").show();
            }else{
                $(".three").hide();
            }
            if(zkhyPcShow){
                $("#zkhyLogin").show();
            }else{
                $("#zkhyLogin").hide();
            }

        });

        // var i=0;
        var i = $("#loginvalue").val();

        function auth() {
            // 	reSize();
            // 	var username = document.getElementById("username").value();
            // 	var pwd = document.getElementById("pwd").value();
            var loginType = $("#loginType").val();
            var username = $("#username").val();
            var mobile = $("#mobile").val();
            var pwd = $("#pwd").val();
            username = $.trim(username);
            // 	 alert(username);
            if (loginType == '0') {
                if (username == '') {
                    alert("用户名不能为空！");
                    i = 0;
                    return false;
                }
            } else if (loginType == '1') {
                if (mobile == '') {
                    alert("手机号不能为空！");
                    i = 0;
                    return false;
                } else if (!(/^1[3456789]\d{9}$/.test(mobile))) {
                    alert("手机号格式错误！");
                    i = 0;
                    return false;
                }
            }
            return true;
        }

        function checkParent() {
            document.getElementById("username").focus();
            if (window.parent.length > 0) {
                window.parent.location = "<%=basePath%>/alogin.jsp";
            }
        }

        //验证码
        function changeValidateCode(obj) {
            // 	获取当前的时间作为参数，无具体意义username
            var timenow = new Date().getTime();
            // 	每次请求需要一个不同的参数，否则可能会返回同样的验证码
            // 	这和浏览器的缓存机制有关系，也可以把页面设置为不缓存，这样就不用这个参数了
            obj.src = "randPic.action?d=" + timenow;
            $("#Text3").val("");
            $("#Text3").focus();
        }

        /**  **/
        //看不清楚超链接点击事件
        function changeValidate(obj) {
            $("#Text3").val("");
            var imgcvc = $("#cvc");
            var timenow = new Date().getTime();
            imgcvc.src = "randPic.action?d=" + timenow;
            $(imgcvc).attr("src", "randPic.action?d=" + timenow);
        }

        function validate() {
            SetPwdAndChk();
            if (i > 0) {
                dis();
            }
            $('form1').action = 'loginAction.action';
            var pwdbyesc=$('#pwd').val()
            $('#pwd2').val(Encrypt(pwdbyesc))
            var username=$('#username').val()
            $('#username2').val(Encrypt(username))
            $('form1').submit();
            i++;
            return true;
        }

        //从后台得到sessionCode的值
        function changAjax() {
            // 	alert("changAjax...");
            $.ajax({
                url: "randPic!getS.action",
                dataType: "html",
                cache: false,
                async: false,
                success: function (data) {
                    $("#sessionCode").val(data);
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    // 			alert("error:"+errorThrown);
                }
            });
        }

        function dis() {
            $("#sub").attr("disabled", true);
        }

        function clearxinx() {
            $("#errorxx").html('');
        }

        function yz() {
            var username = $("#username").val();
        }

        function setcook() {
            // alert($.cookie('mark',"null"));
            // $.cookie('mark',mark);
            // $.cookie('mark','',{ expires: 30,path: '/', domain: 'dmj.com'});
            var aaa = $('input:radio[name="mark"]:checked').val();
            // , {expires: 7, path: ‘/’, domain: ‘jquery.com’, secure: true});
            // var date = new Date();
            // date.setTime(date.getTime() + (1 * 24 * 60 * 60 * 1000));
            // alert(date);
            // var date=1 * 24 * 60 * 60 * 1000;
            // alert(date );

            //    var MyCookie=$.cookie('marks',aaa,{ expires: 30,secure:false,raw:true});
        }

        function adsf() {
            $.ajax({
                url: "questionGroupAction!aaaa.action",
                dataType: "html",
                success: function (data) {
                    if (data == "1") {
                        $("#mark-yh").attr("checked", true);
                        $("peizyhwh").hide();
                    }
                }
            });
        }

        //======================================================================================================================//

        //点击登录时触发客户端事件
        function SetPwdAndChk() {
            //取当前登录类型
            var loginType = document.getElementById('loginType').value;
            //取手机号
            var mobile_name = document.getElementById('mobile').value;
            //取用户名
            var user_name = document.getElementById('username').value;
            //取密码
            var pwd_name = document.getElementById('pwd').value;
            //取用户类型
// 				var logType = document.getElementById('logType').value;
            var logType = $("div.logType input[name='logType']:checked").val();

            //复选框选是否选中
            var test = document.getElementById('rememberPW').checked;
            document.cookie = "loginType=" + escape(loginType);
            if (test) {
                document.cookie = "mobile=" + escape(mobile_name);
                document.cookie = "userName=" + escape(user_name);
                document.cookie = "username=" + escape(pwd_name);
                document.cookie = "logType=" + escape(logType);
                document.cookie = "checked=" + escape(test);
            } else {
                document.cookie = "mobile=";
                document.cookie = "userName=";
                document.cookie = "username=";
                document.cookie = "checked=";
                document.cookie = "logType=";
            }
        }

        //取Cookie的值
        function GetCookie(name) {
            var arg = name + "=";
            var alen = arg.length;
            var clen = document.cookie.length;
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

        //初始化页面给用户名密码赋值
        function setValue() {
            $("#rememberPW").attr("checked", false);
            $('#mobile').val("");
            $('#username').val("");
            $('#pwd').val("");

            var checkeds = GetCookie("checked");
            if (checkeds) {
                var loginType = GetCookie("loginType");
                var curLoginType = $("#loginType").val();

                if (loginType == curLoginType) {
                    $("#rememberPW").attr("checked", true);
                    var mobile = GetCookie("mobile");
                    var username = GetCookie("userName");
                    var pwd = GetCookie("username");
                    $('#mobile').val(mobile);
                    $('#username').val(username);
                    $('#pwd').val(pwd);
                }
            } else {
                $("#rememberPW").attr("checked", false);
            }
            $('#rememberPW').val(checkeds);
        }

        function loadIe() {
            var flag = false;
            if (navigator.userAgent.indexOf("MSIE") > 0) {
                var browser = navigator.appName;
                var b_version = navigator.appVersion;
                var version = b_version.split(";");
                var trim_Version = version[1].replace(/[ ]/g, "");
                if (browser == navigator.userAgent.indexOf("MSIE 7.0") > 0 && trim_Version == "MSIE7.0") {
                    flag = true;
                } else if (navigator.userAgent.indexOf("MSIE 6.0") > 0 && trim_Version == "MSIE6.0") {
                    flag = true;
                } else {
                    flag = false;

                }

            }
            if (isFirefox = navigator.userAgent.indexOf("Firefox") > 0) {
                flag = true;
            }
            if (flag) {
                load();
            }
        }

        function load() {
            document.getElementById("loadie").style.display = "inline";
        }

        function download() {
            document.getElementById("loadImg").style.display = "block";
            $.ajax({
                type: "POST", //用POST方式传输
                cache: false,
                async: true,
                dataType: "JSON", //数据格式:JSON
                url: 'loadDateDbAction!loadIe.action', //目标地址
                success: function (data) {

                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    //alert(XMLHttpRequest.responseText);
                    document.location = XMLHttpRequest.responseText;
                    document.getElementById("loadImg").style.display = "none";

                }
            });

        }

        //到查询登录名调用
        function tologinQueryUserName() {
            dg = new $.dialog({
                id: '11',
                content: 'url: userAction!tologinQueryUserName.action',
                title: '<span class="addGroup"></span>&nbsp;帐号查询',
                cancelval: '关闭',
                width: '850px',
                height: '480px'
            });
            dg.ShowDialog();
        }

        function resetinput() {
            $("#loginvalue").val("0");
            $("#mobile").val("");
            $("#username").val("");
            $("#pwd").val("");

        }

        function resetPw() {
            var logType = $("div.logType input[name='logType']:checked").val();
            window.open('resetPw.jsp?logType='+logType);
        }

        //家长注册界面
        function register() {
            $.ajax({
                type: "POST", //用POST方式传输
                cache: false,
                async: true,
                dataType: "JSON", //数据格式:JSON
                url: 'appIndex!getPayUrl.action', //目标地址
                success: function (data) {
                    // 对参数进行编码
                    let encodedPayUrl = encodeURIComponent(data.payUrl);
                    let encodedSystemId = encodeURIComponent(data.systemid);

                    // 构建编码后的 URL
                    let encodedUrl = "register.jsp?payUrl="+encodedPayUrl+"&systemid="+encodedSystemId;

                    window.open(encodedUrl);
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    //alert(XMLHttpRequest.responseText);
                    document.location = XMLHttpRequest.responseText;
                    document.getElementById("loadImg").style.display = "none";

                }
            })

        }

        //版本更新日志界面
        function updateLog() {
            window.open('updateLog.jsp');
        }

        //切换登录方式
        function changeLogin() {
            var currentLoginType = $("#loginType").val();
            if (currentLoginType == '0') { //用户名登录
                $("#loginType").val("1");
                $("div.logType input[value='P']").parent().css("visibility", "hidden");

                $(".changeDeng").show();
                $(".jiazhangzhuce").css("display", "none");

                $("#usernameTr").hide();
                $("#mobileTr").show();

                /* $(".allparent").eq(0).attr("tabindex","-1");
                $(".jiaoshi").eq(0).attr("tabindex","1"); */

                $(".shoujihao").eq(0).attr("tabindex", "-1");
                $(".yonghuming").eq(0).attr("tabindex", "5");

                $("#mobileTr .txt").attr("tabindex", "2")
                $("#usernameTr .txt").attr("tabindex", "-1");

                //初始化页面赋值
                setValue();
            } else if (currentLoginType == '1') {
                $("#loginType").val("0");
// 					$("#logType").find("option[value='P']").css("display", "block");
                $("div.logType input[value='P']").parent().css("visibility", "");
                /* $("#yhmyhlxTr").show();
                $("#sjhyhlxTr").hide(); */

                $("#usernameTr").show();
                $("#mobileTr").hide();

                /* $(".allparent").eq(0).attr("tabindex","1");
                $(".jiaoshi").eq(0).attr("tabindex","-1"); */

                $(".shoujihao").eq(0).attr("tabindex", "5");
                $(".yonghuming").eq(0).attr("tabindex", "-1");

                $("#usernameTr .txt").attr("tabindex", "2");
                $("#mobileTr .txt").attr("tabindex", "-1")
                //初始化页面赋值
                setValue();
            }
        }
    </script>

    <style type="text/css">
        .erweima {
            font-size: 15px;
            text-align: center;
            -webkit-transform: scale(0.8);
            color: #5942d6;
            font-weight: 900;
            line-height: 15px;
            margin: 0px;
            margin-top: 5px;
        }

        .jiazhangzhuce {
            display: none;
        }

        .swiper-container {
            height: 42px;
            width: 360px;
            height: 30px;
        }

        .swiper-container .swiper-slide {
            height: 30px;
            line-height: 30px;
            text-align: center;
            width: 360px;
            height: 30px;
            color: #e99e3a;
            cursor: pointer;
        }
        .logo-title{
            height: 30px;
            text-align: center;
            font-size: 18px;
            margin: -18px 0 12px 0;
            color: #0B61A4;
            text-shadow: 5px 5px 5px #c0def6 ;
        }
        .three{
            height: 30px;
            display:table-cell;
            vertical-align: middle;
        }
        .three span{
            padding-right: 10px;
            vertical-align: middle;
        }
        .three img{
            height: 20px;
            width: 20px;
            vertical-align:middle;
        }

        .downScan{
            position: absolute;
            top:10px;
            right:10px;
            font-size: 20px;
        }
        .downScan a{
            /*color: red;*/
        }
    </style>

</head>

<body onload="checkParent();" style="background: url('skin/<%=skin%>/img/login-page-bg.png') 0 0/cover no-repeat;">

<input type="hidden" value="${Configuration.getInstance().getViewRankOfScoreInfo() }" id="viewRankOfScoreInfo" />

<input type="hidden" id="marktype" value="<c:out value=" ${Configuration.getInstance().getType()} " /> "/>
<input type="hidden" id="Constauthcode" value="<c:out value=" ${Configuration.getInstance().getAuthcode()} " /> "/>
<input type="hidden" id="Constqueryusername" value="<c:out value=" ${Configuration.getInstance().getQueryusername()} " /> "/>
<input type="hidden" id="openocs" value="${sessionScope.openocs}"/>
<input type="hidden" id="loginvalue" value="0"/>
<div class="downScan">
    <a href="download/扫描端.exe">下载扫描端</a>
</div>
<div class="login-contain">
    <div class="login-box">
        <p class="logo-title"><%=systemTitle%></p>
        <div class="logo">
            <img src="skin/<%=skin%>/img/login-page-logo.png" alt="">
        </div>

        <form class="cd-form" id="form1" method="post" name="form1" action="loginAction.action"
              onsubmit="javascript:return auth();">
            <input type="hidden" value="1" id="marks"></input>
            <input type="hidden" value="" name="authcode_val" id="authcode_val"></input>
            <input type="hidden" value="" name="fp" id="fp"></input>
            <input type="hidden" value="" name="wos" id="wos"></input>
            <input type="hidden" value="0" name="user.loginType" id="loginType"/>
            <input type="hidden" id="pwd2"  name="user.password" value="${user.password}"/>
            <input type="hidden" id="username2" name="user.username" value="${user.username}"/>
            <div class="role-line logType">
                我是：
                <span class="pdright">
                    <input id="option-one" type="radio" name="logType" value="T" checked/>
                        <label for="option-one">教师</label>
                    </span>
                <span class="pdright">
                    <input id="option-two" type="radio" name="logType" value="S"/>
                    <label for="option-two">学生</label>
                </span>
                <span class="pdright">
                    <input id="option-three" type="radio" name="logType" value="P"/>
                    <label for="option-three">家长</label>
                </span>
            </div>
            <div class="fieldset" id="usernameTr">
                <label class="image-replace cd-username" for="username"></label>
                <input onclick="clearxinx()" id="username" type="text"
                       class="txt" placeholder="用户名/手机号" tabindex="1">
                <span class="changeDeng">
<%--                    <a class="tel-a shoujihao" href="javascript:changeLogin();">手机号登录</a>--%>
                </span>
            </div>
            <div class="fieldset" id="mobileTr" style="display: none">
                <label class="image-replace cd-tel" for="mobile"></label>
                <input onclick="clearxinx()" id="mobile" name="user.mobile" type="text"
                       class="txt" value="${user.mobile}" placeholder="手机号" tabindex="1"/>
                <span>
                <a class="tel-a yonghuming" href="javascript:changeLogin();"><span>用户名登录</span></a>
			</span>
            </div>
            <div class="fieldset">
                <label class="image-replace cd-password" for="pwd">密码</label>
                <input onfocus="clearxinx()" id="pwd" type="password"
                       class="txt" placeholder="密码" tabindex="2"/>
                <div class="forget-box">
                    <input type="checkbox" id="rememberPW"/>
                    <label for="rememberPW" class="forget-a">记住密码</label>
                </div>
            </div>
            <div class="login-btn">
                <input class="btn" id="sub" type="submit" value="登录" onclick="return validate();" tabindex="3"/>
                <p class="three"><span>第三方登录</span><img id="zkhy" src="common/image/three/zkhyLogo.png" title="中科宏一统一登录" onclick="location.href='login!toZkhyLogin.action'"/></p>
            </div>
            <div class="get-pwd">
                <div class="version"><%=version %>
                </div>
                <a class="register jiazhangzhuce" href="javascript:register();">家长注册</a>
                <a class="get-pwd-a" href="javascript:resetPw();">找回密码</a>
            </div>
            <div style="font-size:12px;width: 100%;text-align: center;color: red;" id="errorxx">${info}</div>
            <div class="swiper-container">
                <div class="swiper-wrapper" style="color: #e99e3a!important;">

                </div>
            </div>
            <center>
                <img id="loadImg" name="loadImg" style="margin-top:10px;display:none;"
                     src="<%=basePath%>common/image/load.gif"/>
                <div id="jz"></div>
            </center>
            <span id="loadie" style="display:none;"></span>
        </form>
    </div>
    <div class="ewm-block">
        <div class="moduler">
            <div id="teacherAndroidBarcode"></div>
            <p id="teacherAndroidBarcodeTxt">下载教师端App</p>
            <p>Android/iOS</p>
        </div>
<%--        <div class="moduler">--%>
<%--            <div id="teacherIosBarcode"></div>--%>
<%--            <p id="teacherIosBarcodeTxt">下载教师端App</p>--%>
<%--            <p>iOS</p>--%>
<%--        </div>--%>
        <div class="moduler">
            <div id="studentAndroidBarcode"></div>
            <p id="studentAndroidBarcodeTxt">下载学生端App</p>
            <p>Android/iOS</p>
        </div>
<%--        <div class="moduler">--%>
<%--            <div id="studentIosBarcode"></div>--%>
<%--            <p id="studentIosBarcodeTxt">下载学生端App</p>--%>
<%--            <p>iOS</p>--%>
<%--        </div>--%>
<%--        <div class="moduler">--%>
<%--            <div id="schoolBarcode"></div>--%>
<%--            <p>App扫码</p>--%>
<%--            <p>绑定平台</p>--%>
<%--        </div>--%>
    </div>
</div>
</body>

</html>