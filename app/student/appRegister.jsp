<%@ page import="com.dmj.util.StaticClassResources" %><%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2023/9/14
  Time: 9:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
    String systemName=StaticClassResources.EdeiInfo.getVendor();
    String tuijianren =request.getParameter("tuijianren");
    tuijianren=tuijianren==null?"":tuijianren;
%>
<html>
<head>
    <base href="<%=basePath%>"/>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <title></title>
    <link rel="stylesheet" type="text/css" href="common/css/app/mui.min.css" />
    <link rel="stylesheet" type="text/css" href="common/css/app/mui.extend.css" />
    <link rel="stylesheet" type="text/css" href="common/css/app/cube.min.css"/>
    <link rel="stylesheet" type="text/css" href="common/css/app/mui.poppicker.css" />
    <link rel="stylesheet" type="text/css" href="common/css/app/mui.picker.css" />
    <link rel="stylesheet" type="text/css" href="common/css/app/overhang.min.css">
    <link rel="stylesheet" type="text/css" href="common/js/app/need/layer.css" />
    <link rel="stylesheet" type="text/css" href="common/css/app/common.css" />
    <link rel="stylesheet" href="common/css/app/common1.css" />
    <link rel="stylesheet" type="text/css" href="common/css/app/student/register.css">
    <script src="common/js/app/jquery.min.js" type="text/javascript" charset="utf-8"></script>
    <script src="common/js/app/htmlFont.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript" src="common/js/app/layer.js"></script>

    <style>
        .mui-bar-nav~.mui-content {
            padding-top: 0.44rem;
        }
        .mui-scroll-wrapper{
            margin-top: 0.5rem;
        }
        .box {
            margin-bottom: 0.15rem;
        }

        .button {
            margin-top: 0.30rem
        }

        #code {
            padding-right: 1.2rem;
            text-align: left;
            font-size: 0.12rem;
        }

        #codeButton {
            height: 0.37rem;
            line-height: 0.32rem;
            color: white;
            font-size: 0.12rem;
        }

        #codeBox {
            background: #00C9D9;
            width: 1.20rem;
            height: 0.38rem;
            position: absolute;
            right: 0.25rem;
            top: 0rem
        }

    </style>
</head>
<body>
<input id="basepath" value="<%=basePath%>" type="hidden">
<input id="tuijianren" value="<%=tuijianren%>" type="hidden">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">注册</h1>
</header>
<div class="mui-content">
    <div class="mui-scroll-wrapper">
        <div class="mui-scroll">
            <div id="app">
                <div class="box">
                    <div class="labeltxt">平台</div>
                    <div id="systemName"><%=systemName%></div>
                </div>

                <div class="box">
                    <div class="labeltxt">学校</div>
                    <input type="hidden" id="schoolText" :value="schoolval" />
                    <span @click="showSchoolPicker()"><input type="text" id="schoolNum" :value="selectedSchoolText?selectedSchoolText:'点击选择学校'" disabled></span>
                </div>

                <div class="box">
                    <div class="labeltxt">年级</div>
                    <input type="hidden" id="gradeText" :value="gradeval" />
                    <span @click="showGradePicker()"><input type="text" id="gradeNum" :value="selectedGradeText?selectedGradeText:'点击选择你年级'" disabled></span>
                </div>

                <div class="box">
                    <div class="labeltxt">班级</div>
                    <input type="hidden" id="classText" :value="classval" />
                    <span @click="showClassPicker()"><input type="text" id="classNum" :value="selectedClassText?selectedClassText:'点击选择班级'" disabled></span>
                </div>
                <div class="box" style="margin-bottom: 0.60rem;">
                    <div class="labeltxt">学生姓名</div>
                    <span @click="showSystemPicker()"><input type="text" id="studentName" :value="selectedSystemText?selectedSystemText:'点击搜索学生姓名'" disabled></span>
                </div>
            </div>


            <div class="box">
                <div class="labeltxt">家长姓名</div>
                <input type="text" id="parentName" placeholder="请输入家长姓名">
            </div>

            <div class="box">
                <div class="labeltxt">手机号</div>
                <input type="text" id="mobile" placeholder="请输入手机号">
            </div>

            <div class="box">
                <div class="labeltxt">密码</div>
                <input type="password" id="password" placeholder="请输入6-20位字母数字组合密码">
            </div>

            <div class="box">
                <div class="labeltxt">确认密码</div>
                <input type="password" id="confirmPassword" placeholder="请再次输入密码">
            </div>

            <div class="box">
                <div class="labeltxt">验证码</div>
                <input type="text" id="code" placeholder="请输入验证码">
                <div id="codeBox"> <input id="codeButton" type="button" value="获取验证码" /></div>
            </div>

            <div class="box">
                <div style="color: red;">
                    <div>同一个年级一个手机号只能绑定一个学生</div>
                    <div>一个学生最多绑定两个手机号</div>
                </div>
            </div>

            <div class="button" id="submitButton" href="#popover">
                提交
            </div>

            <nav id="beianhao" style="width:100%;background: transparent;text-align: center;color: #FFFFFF;font-size: 0.12rem;margin-top:4vh">
                备案号：京ICP备12032504号
            </nav>
        </div>
    </div>
</div>
</body>
<script src="common/js/app/jquery-ui.min.js"></script>
<script src="common/js/app/mui.min.js"></script>
<script src="common/js/app/mui.extend.js"></script>
<script src="common/js/app/vue-2.6.12.js" type="text/javascript" charset="utf-8"></script>
<script src="common/js/app/cube.min.js" type="text/javascript" charset="utf-8"></script>
<script src="common/js/app/mui.poppicker.js" type="text/javascript" charset="utf-8"></script>
<script src="common/js/app/mui.picker.js" type="text/javascript" charset="utf-8"></script>
<script src="common/js/app/listpicker.js" type="text/javascript" charset="utf-8"></script>
<script src="common/js/app/aesutils/aes.js"></script>
<script src="common/js/app/aesutils/pbkdf2.js"></script>
<script src="common/js/app/overhang.min.js"></script>
<script src="common/js/app/student/register.js"></script>
<script src="common/js/app/student/sendCode.js"></script>

<script>
    var payUrl='';
    var systemid='';
    var hefa=1;
    $(function() {
            getPayUrl()
            /*$("body").css("height", plus.screen.resolutionHeight + "px");
            $("html").css("height", plus.screen.resolutionHeight + "px");*/

            setSchoolPicker();

            $("#openPopover").on("tap", function() {
                $("#test").dialog();
            });

            //提交
            $("#submitButton").on("tap", function() {
                var tuijianren=$('#tuijianren').val()
                if (tuijianren!=''&&null!=tuijianren){
                   checkTuijianren()
                    if (hefa==0){
                        return;
                    }
                }else {
                    alert("此链接非法，请从正规渠道跳转！")
                    return;
                }
                var vue02=app;
                document.activeElement.blur();
                if (!getCode) {
                    $('body').overhang({
                        type: 'warn',
                        message: '请先获取验证码'
                    });
                    return;
                }
                //1、判断验证码格式
                if (!checkCode()) {
                    return;
                }
                //2、判断通过验证
                if (!isContinue()) {
                    return;
                }
                $.ajax({
                    url: "<%=basePath%>appIndex!addTuijianUserParent.action",
                    data: {
                        schoolNum: vue02.schoolval,
                        gradeNum: vue02.gradeval,
                        classNum: vue02.classval,
                        studentName: $("#studentName").val(),
                        mobile: $("#mobile").val(),
                        password: $("#password").val(),
                        parentName: $("#parentName").val(),
                        code: $("#code").val(),
                        userId: userId,
                        tuijianren:$('#tuijianren').val()
                        // tuijianren:'DE20220821010112'
                    },
                    dataType:'json',
                    async: false,
                    success: function(data) {
                        if (data.code == 200) {
                            $.ajax({
                                url: payUrl+"/youhuiquan/add/addYaoqingUserYouhuiquan",
                                data:{
                                    systemid:systemid,
                                    schoolName:data.data[0]["schoolName"],
                                    studentId:data.data[0]["studentId"],
                                    studentName:data.data[0]["studentName"],
                                    tuijianrenSchoolName:data.data[1]["schoolName"],
                                    tuijianrenStudentId:data.data[1]["studentId"],
                                    tuijianrenName:data.data[1]["studentName"],
                                    tuijianrenId:data.data[1]["id"],
                                    mobile: $("#mobile").val(),
                                },
                                async:false,
                                dataType:'json',
                                success:function(data2){
                                    if(data.code==200&&data2.code==200){
                                        mui.toast(data.msg+" "+data2.msg);

                                        $("input").val("");
                                        // resetPicker(school_picker, grade_picker, class_picker);
                                        nums = 0;
                                        var huanxingurl = "<%=request.getParameter("huanxingurl")%>";
                                        mui.openWindow({
                                            url: '<%=basePath%>app/huanxing/student/index.jsp?huanxingurl='+encodeURIComponent(huanxingurl),
                                            id: 'huanxing'
                                        });
                                    }
                                }
                            })

                        } else {
                            mui.alert(data.msg);
                        }
                    },
                    error: function(XMLHttpRequest, statusText, errorThrown) {
                        if (XMLHttpRequest.status == 0) {
                            mui.alert($.getUrl() + "服务器未打开或者已关闭！");
                            return;
                        }
                        mui.toast("未知异常，请稍后重试！");
                    }
                });

            });

            //获取验证码
            $("#codeButton").on("tap", function() {
                var vue01=app;
                var schoolNum =vue01.schoolval;
                var gradeNum = vue01.gradeval;
                var classNum = vue01.classval;
                var studentName = $("#studentName").val();
                var mobile = $("#mobile").val();
                document.activeElement.blur();
                //如果前台js验证不通过
                if (!validateForm()) {
                    return;
                }
                //验证学生姓名和手机号组合是否通过
                var stuFlag = checkUserAndMobile();
                if (stuFlag == '0') {
                    return;
                } else if (stuFlag == '1') {
                    var stuContent = '<div style="text-align:left;">';
                    $.each(stuData, function(index, obj) {
                        stuContent += '<input type="radio" name="sameNameStu" value="' + obj.id + '" style="margin:0.10rem;">' +
                            obj.studentName + '-' + obj.studentId + '<br>';
                    });
                    stuContent += '</div>';

                    layer.open({
                        title: ["选择学生", 'background-color:#8DCE16; color:#fff;'],
                        content: stuContent,
                        btn: ['确定', '取消'],
                        shadeClose: false,
                        yes: function(index) {
                            var uId = $("input[name='sameNameStu']:checked").val();
                            userId = uId;
                            if (uId != undefined) {
                                $.ajax({ //验证
                                    url: "<%=basePath%>appIndex!checkStudentIsOk.action",
                                    data: {
                                        "id": uId,
                                        "mobile": $('#mobile').val()
                                    },
                                    async: false,
                                    dataType:'json',
                                    success: function(data) {
                                        if (data.code == 200) {
                                            comRegisterMethod();
                                            layer.close(index);
                                        } else {
                                            mui.alert(data.msg);
                                        }
                                    }
                                });
                            } else {
                                layer.close(index);
                            }
                        }
                    });
                } else if (stuFlag == '2') {
                    comRegisterMethod();
                }

            });
    });

    function getPayUrl() {
        $.ajax({
            url: "<%=basePath%>appIndex!getPayUrl2.action",
            async:false,
            dataType:'json',
            success:function(data){
                payUrl=data.payUrl
                systemid=data.systemid
            }
        })
    }
    //设置对应的学校列表
    function setSchoolPicker() {
        var vue3=app;
        $.ajax({
            url:  "<%=basePath%>appIndex!getAllSchool.action",
            data: {},
            success: function(d) {
                d=eval(d)
                if (d && d.length > 0) {
                    vue3.selectedSchoolText=d[0].text;
                    vue3.schoolval=d[0].value;
                    vue3.schoolData=d;
                    vue3.schoolPicker.setData(d);
                } else {
                    vue3.schoolPicker.setData([]);
                }
                setGradePicker();
            },
            error: function(d) {
                vue3.schoolPicker.setData([]);
            },complete:function(){
                document.querySelector(".schoolInput").value='';
            }
        });
    };
    //设置对应的年级列表
    function setGradePicker() {
        var vue1=app;
        $.ajax({
            url:  "<%=basePath%>appIndex!getAllGradeBySchoolNum.action",
            data: {
                schoolNum:vue1.schoolval,
            },
            success: function(d) {
                d=eval(d)
                if (d && d.length > 0) {
                    vue1.gradeData=d;
                    vue1.gradePicker.setData(d);
                    vue1.selectedGradeText=d[0].text;
                    vue1.gradeval=d[0].value;
                } else {
                    vue1.gradePicker.setData([]);
                    vue1.gradeval='';
                }
                setClassPicker()
            },
            error: function(d) {
                vue1.gradePicker.setData([]);
            },complete:function(){
                document.querySelector(".gradeInput").value='';
            }
        });
    };
    //设置对应的班级列表
    function setClassPicker() {
        var vue2=app;
        $.ajax({
            url:  "<%=basePath%>appIndex!getAllClassBySchoolNumAndGradeNum.action",
            data: {
                schoolNum:vue2.schoolval,
                gradeNum:vue2.gradeval,
            },
            success: function(d) {
                d=eval(d)
                if (d && d.length > 0) {
                    vue2.classData=d;
                    vue2.classPicker.setData(d);
                    vue2.selectedClassText=d[0].text;
                    vue2.classval=d[0].value;
                } else {
                    vue2.classPicker.setData([]);
                    vue2.classval='';
                }
                setSystemPicker()
            },
            error: function(d) {
                vue2.classPicker.setData([]);
            },complete:function(){
                document.querySelector(".classInput").value='';
            }
        });
    };
    //设置对应的学生列表
    function setSystemPicker() {
        var vue0=app;
        vue0.selectedSystemText='';
        $.ajax({ //设置进行查找对应的单位
            url: "<%=basePath%>appIndex!getAllstudentByclass.action",
            data: {
                schoolNum:vue0.schoolval,
                gradeNum:vue0.gradeval,
                classNum:vue0.classval
            },
            success: function(d) {
                d=eval(d)
                if (d && d.length > 0) {
                    vue0.systemData=d;
                    if(app.searchSystemWord!=""){
                        vue0.systemPicker.setData([]);
                    }else{
                        vue0.systemPicker.setData([]);
                    }
                } else {
                    vue0.systemPicker.setData([]);
                }
            },
            error: function(d) {
                vue0.systemPicker.setData([]);
                // app.showSystemPicker();
                app.systemUrl = '';
            },complete:function(){
                document.querySelector(".systemInput").value='';
            }
        });
    };
    //搜索学生
    function seachSystem(systemName) {
        var data = [];
        if(systemName==""){
            return [];
        }
        for (var i in app.systemData) {
            var s = app.systemData[i];
            if (null!=s&&s.text.indexOf(systemName) > -1) {
                data.push(s);
            }
        }
        return data;
    }
    //搜索班级
    function seachClass(className) {
        var data = [];
        for (var i in app.classData) {
            var s = app.classData[i];
            if (null!=s&&s.text.indexOf(className) > -1) {
                data.push(s);
            }
        }
        return data;
    }
    //搜索年级
    function seachGrade(gradeName) {
        var data = [];
        for (var i in app.gradeData) {
            var s = app.gradeData[i];
            if (null!=s&&s.text.indexOf(gradeName) > -1) {
                data.push(s);
            }
        }
        return data;
    }
    //搜索年级
    function seachSchool(schoolName) {
        var data = [];
        for (var i in app.schoolData) {
            var s = app.schoolData[i];
            if (null!=s&&s.text.indexOf(schoolName) > -1) {
                data.push(s);
            }
        }
        return data;
    }
    function comRegisterMethod() {
        //验证都通过了，将值先保存起来
        pushOldValue();

        //获取验证码的状态改为true  已获取
        getCode = true;

        sendCode($("#codeButton"));

        $.ajax({ //发送验证码
            url: "<%=basePath%>appIndex!addPhone.action",
            data: {
                "mobile": $('#mobile').val(),
                "smsTemplate": 4,
                "type": "bind",
                "userType": "3"
            },
            async: false,
            dataType:'json',
            success: function(data) {
                mui.toast(data.msg);
                return;
            },
            error: function(XMLHttpRequest, statusText, errorThrown) {
                nums = 0;
                mui.alert("请重试");
            }
        });
    }

    $("#back").click(function() {
        mui.openWindow({
            url: 'login.html',
            id: 'loginttt.html'
        });
    });

    //检查推荐人是否合法
    function checkTuijianren(){
        var tuijianren =$('#tuijianren').val()
        //推荐人是当前登录的学生
        $.ajax({
            url: "<%=basePath%>appIndex!getStudentIdByUserId.action",
            data: {
                userId: userId,
            },
            async: false,
            dataType:'html',
            success: function(d) {
                if (tuijianren==d){
                    alert("推荐人与被推荐人不能是同一个学生！")
                    hefa=0;
                }
            }
        })
        if (hefa==0){
            return;
        }
        $.ajax({
            url: "<%=basePath%>appIndex!isExistStudent.action",
            data: {
                user_id: tuijianren,
            },
            async: false,
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(XMLHttpRequest.status);
            },
            dataType:'json',
            success: function(d) {
                if(d.code==200){//该推荐人存在
                    //推荐人是否是第一次购买
                    $.ajax({
                        url: payUrl+ "/dingdan/getUserIsBuyTaocan",
                        data: {
                            systemid: systemid,
                            user_id: tuijianren,
                        },
                        async: false,
                        error: function(XMLHttpRequest, textStatus, errorThrown) {
                            console.log(XMLHttpRequest.status);
                        },
                        dataType:'json',
                        success: function(d) {
                            if(d.code==200){
                                if(d.data=='1'){//之前购买过套餐，符合推荐标准
                                    isLegal = '1';
                                }else {
                                    hefa=0;
                                    alert("该推荐人不符合推荐标准！")
                                }
                            }
                        },
                    });
                }else {
                    hefa=0;
                    alert("该推荐人不存在！")
                }
            },
        });
    }

    function resetPicker(school_picker, grade_picker, class_picker) {
        school_picker.pickers[0].setSelectedIndex(0);
        grade_picker.pickers[0].setSelectedIndex(0);
        class_picker.pickers[0].setSelectedIndex(0);

        $("#schoolNum").val(school_picker.pickers[0].getSelectedItem().value);
        $("#gradeNum").val(grade_picker.pickers[0].getSelectedItem().value);
        $("#classNum").val(class_picker.pickers[0].getSelectedItem().value);

        $("#schoolText").val(school_picker.pickers[0].getSelectedItem().text);
        $("#gradeText").val(grade_picker.pickers[0].getSelectedItem().text);
        $("#classText").val(class_picker.pickers[0].getSelectedItem().text);
    }
    //修改平台的搜索词
    function inputSystem(value) {
        app.searchSystemWord = value;
    }
    function inputClass(value) {
        app.searchClassWord = value;
    }
    function inputGrade(value) {
        app.searchGradeWord = value;
    }
    function inputSchool(value) {
        app.searchSchoolWord = value;
    }
    //初始化vue对象
    var app = new Vue({
        el: "#app",
        data:{
            searchSystemWord: '',
            systemPicker: [],
            selectedSystemText: '',
            selectedSystemIndex: [0, 0],
            systemData: [],
            systemUrl: '',

            searchClassWord:'',
            classPicker: [],
            selectedClassText:'',
            selectedClassIndex: [0, 0],
            classData:[],
            classval:'',

            searchGradeWord:'',
            gradePicker: [],
            selectedGradeText:'',
            selectedGradeIndex: [0, 0],
            gradeData:[],
            gradeval:'',

            searchSchoolWord:'',
            schoolPicker: [],
            selectedSchoolText:'',
            selectedSchoolIndex: [0, 0],
            schoolData:[],
            schoolval:''
        },
        mounted:function() {
            this.systemPicker = this.$createCascadePicker({
                title: '<input placeholder="请输学生姓名搜索" type="text" style="text-align:center;width:60%;height:30px;border:1px solid #aaa!important;border-radius:10px;font-size:12px;" class="systemInput"  oninput="inputSystem(this.value)"/>',
                data: this.systemData,
                onSelect: this.selectSystemHandle
            });
            this.classPicker = this.$createCascadePicker({
                title: '<input placeholder="请输班级名称搜索" type="text" style="text-align:center;width:60%;height:30px;border:1px solid #aaa!important;border-radius:10px;font-size:12px;" class="classInput"  oninput="inputClass(this.value)"/>',
                data: this.classData,
                onSelect: this.selectClassHandle
            });
            this.gradePicker = this.$createCascadePicker({
                title: '<input placeholder="请输年级名称搜索" type="text" style="text-align:center;width:60%;height:30px;border:1px solid #aaa!important;border-radius:10px;font-size:12px;" class="gradeInput"  oninput="inputGrade(this.value)"/>',
                data: this.gradeData,
                onSelect: this.selectGradeHandle
            });
            this.schoolPicker = this.$createCascadePicker({
                title: '<input placeholder="请输学校名称搜索" type="text" style="text-align:center;width:60%;height:30px;border:1px solid #aaa!important;border-radius:10px;font-size:12px;" class="schoolInput"  oninput="inputSchool(this.value)"/>',
                data: this.schoolData,
                onSelect: this.selectSchoolHandle
            });

        },
        methods: {
            showSystemPicker:function() {
                this.systemPicker.show()
            },
            showClassPicker:function() {
                this.classPicker.show()
            },
            showGradePicker:function() {
                this.gradePicker.show()
            },
            showSchoolPicker:function() {
                this.schoolPicker.show()
            },
            selectSystemHandle:function(selectedVal, selectedIndex, selectedText) {
                this.selectedSystemText = selectedText.join(' ');
                this.selectedSystemIndex = selectedIndex;
                this.systemUrl = selectedVal.toString();
            },
            selectClassHandle:function(selectedVal, selectedIndex, selectedText){
                this.selectedClassText = selectedText.join(' ');
                this.selectedClassIndex = selectedIndex;
                this.classval=selectedVal.toString();
            },
            selectGradeHandle:function(selectedVal, selectedIndex, selectedText){
                this.selectedGradeText = selectedText.join(' ');
                this.selectedGradeIndex = selectedIndex;
                this.gradeval=selectedVal.toString();
            },
            selectSchoolHandle:function(selectedVal, selectedIndex, selectedText){
                this.selectedSchoolText = selectedText.join(' ');
                this.selectedSchoolIndex = selectedIndex;
                this.schoolval=selectedVal.toString();
            }
        },
        watch: {
            searchSystemWord:function(newVal, oldVal) {
                if (oldVal != newVal) {
                    var data = seachSystem(newVal);
                    this.systemPicker.setData(data);
                }
            },
            searchClassWord:function(newVal, oldVal) {
                if (oldVal != newVal) {
                    var data = seachClass(newVal);
                    this.classPicker.setData(data);
                }
            },
            searchGradeWord:function(newVal, oldVal) {
                if (oldVal != newVal) {
                    var data = seachGrade(newVal);
                    this.gradePicker.setData(data);
                }
            },
            searchSchoolWord:function(newVal, oldVal) {
                if (oldVal != newVal) {
                    var data = seachSchool(newVal);
                    this.schoolPicker.setData(data);
                }
            },
            schoolval:function(newVal, oldVal){
                if (oldVal != newVal) {
                    setGradePicker();
                }
            },
            gradeval:function(newVal, oldVal){
                if (oldVal != newVal) {
                    setClassPicker();
                }
            },
            classval:function(newVal, oldVal){
                if (oldVal != newVal) {
                    setSystemPicker();
                }
            },

        }
    })
</script>

</html>
