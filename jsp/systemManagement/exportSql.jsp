<%@ page contentType="text/html; charset=utf-8"  language="java" import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<%

String path = request.getContextPath();

String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>

  <head>

    <base href="<%=basePath%>">

    

    <title>表/sql</title>

    

	<meta http-equiv="pragma" content="no-cache">

	<meta http-equiv="cache-control" content="no-cache">

	<meta http-equiv="expires" content="0">    

	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">

	<meta http-equiv="description" content="This is my page">

	<!--

	<link rel="stylesheet" type="text/css" href="styles.css">

	-->

	<link rel="stylesheet" href="common/js/uploadify/uploadify.css" type="text/css"></link>

	<link rel="stylesheet" href="common/css/style.css" type="text/css" />

	<script type="text/javascript"	src="common/js/jquery.js"></script>

		<script type="text/javascript" src="common/js/uploadify/jquery.uploadify.v2.1.4.min.js"></script>

	<script type="text/javascript" src="common/js/uploadify/swfobject.js"></script>

<script type="text/javascript" src="common/js/selectCorrect.js"></script>

	

	<script type="text/javascript" src="common/js/lhgdialog/lhgdialog.min.js?skin=mac"></script>

	<script language="javascript" type="text/javascript">

		

/* 		function readFile(fileBrowser) {  

			alert("1"); 

		   　　　 if (navigator.userAgent.indexOf("MSIE")!=-1) {

	  	 		alert("11"); 

	    　　　　　　	readFileIE(fileBrowser);   

		   　　　 } else　if (navigator.userAgent.indexOf("Firefox")!=-1　|| navigator.userAgent.indexOf("Mozilla")!=-1)    {

		  　	 	alert("111"); 　　　　　

		   		readFileFirefox(fileBrowser);   

		   　　　 } else {  

		  　　　　　　　return;   

		     }

      	}

     

	     function readFileFirefox(fileBrowser) {

	     	 

	       try {    　

	       	alert("22:"+fileBrowser.value);   　　　　　　 

	        netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");   

	       } catch (e) { 

	       	alert("22:"+e);   　

	        return;  　　　

	       }

	       var fileName=fileBrowser.value;   

	  　　　   alert(fileName); 

	    } */

		

		

		$(document).ready(function() {

// 			init('1',true,'',true,function(){});



//		$("#uploadifySql").uploadify({

//			'auto' : false,

//			'swf' : 'common/js/uploadify/uploadify.swf',

//			'uploader' : '<%=basePath%>exportSqlAction.action;JSESSIONID=<%=session.getId()%>',

//			'debug'    : true,

//			'fileSizeLimit' : '4096*4096KB',

//			'successTimeout' : 300,

//			'onUploadSuccess' : function(file, data, response) {

//           	 	alert('The file ' + file.name + ' was successfully uploaded with a response of ' 

//           	 	+ response + ':' + data);

//        	},

//         	'onUploadError' : function(file, errorCode, errorMsg, errorString) {

//            	alert('The file ' + file.name + ' could not be uploaded: ' + errorCode);

//        	}

//		});



		init('1',true,'',true,function(){

			abc();

			school_unchecked();

		});

		



		//导入数据库文件						

		$("#btn_import_sql").click(function(){

			//$("#uploadifySql").uploadifyUpload();

// 			alert("im");

			var fp=document.getElementById("upload").value;

			var url="exportSqlAction!importSql.action?hid_upload="+fp;

			document.getElementById("top_div").style.display="none";

			document.getElementById("bottom_div").style.display="none";

			document.getElementById("loadImg").style.display="";

			document.getElementById("note_span").style.display="";

			$.ajax( {

					type : "POST",//用POST方式传输

					cache : false,

					async : false,

					dataType : "html",//数据格式:JSON

					url :url,//目标地址

					data : {

					},

					success : function(data) {

						alert("文件加载完毕");

						document.getElementById("top_div").style.display="";

						document.getElementById("bottom_div").style.display="";

						document.getElementById("loadImg").style.display="none";

						document.getElementById("note_span").style.display="none";

					},

					error : function(XMLHttpRequest, textStatus, errorThrown) {

						alert("sql文件加载失败");

						document.getElementById("top_div").style.display="";

						document.getElementById("bottom_div").style.display="";

						document.getElementById("loadImg").style.display="none";

						document.getElementById("note_span").style.display="none";

						

					}

			});

		});

		

		

		//导出数据库文件

		$("#btn_export_sql").click(function(){

			//在生成这次考试的所有文件前，删除原来的目录及文件，生成新的目录和 文件

			delSqlFileDir();

			var url="exportSqlAction!doExportSql.action?";

// 			--选中的学校

			$("input[name='school_checkbox']:checked").each(function(si){  //索引i从零开始的，传参加1

				var sch_checkid=$(this).attr("id");

// 				alert(sch_checkid);

// 				--当前学校有哪些年级被选中要导出single_school_examNum

				$("input[name='grade_checkbox']:checked").each(function(gi){  //索引i从零开始的，传参加1

					var gra_checkid=$(this).attr("id");

					var a=gra_checkid.indexOf("_",0);

// 					alert(gra_checkid+","+gra_checkid.substring(a+1,gra_checkid.length));

// 					return;

					

					var exam_num=$(this).parent().parent().find(":hidden:eq(0)").val();

					var school_num=sch_checkid;

					var grade_num=gra_checkid.substring(a+1,gra_checkid.length);

// 					alert("导出----年级编号："+grade_num+",学校编号："+school_num+",考试："+exam_num);



					var basic_check_obj=document.getElementById("check_basic");

					var examPaperImage_check_obj=document.getElementById("check_examPaperImage");

					var questionImage_check_obj=document.getElementById("check_questionImage");

					var scoreImage_check_obj=document.getElementById("check_scoreImage");

					var examineeNumImage_check_obj=document.getElementById("check_examineeNumImage");

					

					

					var basic_check_flag="F";

					var examPaperImage_check_flag="F";

					var questionImage_check_flag="F";

					var scoreImage_check_flag="F";

					var examineeNumImage_check_flag="F";

					

// 					alert(basic_check_obj.checked);

					if(basic_check_obj.checked){

						basic_check_flag="T";

					}

					if(examPaperImage_check_obj.checked){

						examPaperImage_check_flag="T";

					}

					if(questionImage_check_obj.checked){

						questionImage_check_flag="T";

					}

					if(scoreImage_check_obj.checked){

						scoreImage_check_flag="T";

					}

					if(examineeNumImage_check_obj.checked){

						examineeNumImage_check_flag="T";

					}

					

// 					return ;





					var str="exam="+exam_num+"&school="+school_num+"&grade="+grade_num

							+"&basic_check_flag="+basic_check_flag

							+"&examPaperImage_check_flag="+examPaperImage_check_flag

							+"&questionImage_check_flag="+questionImage_check_flag

							+"&scoreImage_check_flag="+scoreImage_check_flag

							+"&examineeNumImage_check_flag="+examineeNumImage_check_flag;

// 					---$.ajax--开始

					$.ajax( {

						type : "POST",//用POST方式传输

						cache : false,

						async : false,

						dataType : "html",//数据格式:JSON

						url :url+str,//目标地址

						data : {

						},

						success : function(data) {

// 							alert("success");

						},

						complete : function(data) {

// 							alert("complete");

						},

						error : function(XMLHttpRequest, textStatus, errorThrown) {

// 							alert("error");

						}

					});//提交每个年级的$.ajax-结束		

				 });

			 });

			 alert("sql文件生成完成");

		});//导出按钮结束

		

				//是否导出图片复选框被点击的时候

		$("input[type='checkbox'][name='parent_checkbox']").live('click',function(){

			var parent_checkbox_id=$(this).attr("id");

// 			alert(parent_checkbox_id);

        	var tr_obj=$(this).parent().parent();

			var check_obj_checked=document.getElementById(parent_checkbox_id);

// 			alert("点击是否导出图片checkbox.checked:"+check_obj_checked.checked);

			if(check_obj_checked.checked==false){

				$(tr_obj).find("input[type='checkbox'][name='child_checkbox']").removeAttr("checked");

			}

			if(check_obj_checked.checked==true){

				$(tr_obj).find("input[type='checkbox'][name='child_checkbox']").attr("checked",'true');

			}

		});

		

		//是否导出图片后面的子复选框被点击的时候

		$("input[type='checkbox'][name='child_checkbox']").live('click',function(){

			var child_checkbox_id=$(this).attr("id");

// 			alert(gra_ch_id);

        	var tr_obj=$(this).parent().parent();

// 			var check_obj_checked=document.getElementById(gra_ch_id);

			var child_checkbox_len_total=$(tr_obj).find("input[type='checkbox'][name='child_checkbox']").length;

			var child_checkbox_len_checked=$(tr_obj).find("input[type='checkbox'][name='child_checkbox']:checked").length;

// 			alert("点击子图片复选框child_checkbox_len_total:"+child_checkbox_len_total+",child_checkbox_len_checked:"+child_checkbox_len_checked);

			if(child_checkbox_len_checked==0){

				$(tr_obj).find("input[type='checkbox'][name='parent_checkbox']").removeAttr("checked");

			}

			if(child_checkbox_len_checked>0){

				$(tr_obj).find("input[type='checkbox'][name='parent_checkbox']").attr("checked",'true');

			}

			

		});

		

		

		

			

		//学校复选框被点击的时候

		$("input[type='checkbox'][name='school_checkbox']").live('click',function(){

			var sch_ch_id=$(this).attr("id");

// 			alert(sch_ch_id);

        	var tr_obj=$(this).parent().parent();

			var check_obj_checked=document.getElementById(sch_ch_id);

// 			alert("点击学校checkbox.checked:"+check_obj_checked.checked);

			if(check_obj_checked.checked==false){

				$(tr_obj).find("input[type='checkbox'][name='grade_checkbox']").removeAttr("checked");

			}

			if(check_obj_checked.checked==true){

				$(tr_obj).find("input[type='checkbox'][name='grade_checkbox']").attr("checked",'true');

			}

		});

		

		//年级复选框被点击的时候

		$("input[type='checkbox'][name='grade_checkbox']").live('click',function(){

			var gra_ch_id=$(this).attr("id");

// 			alert(gra_ch_id);

        	var tr_obj=$(this).parent().parent();

// 			var check_obj_checked=document.getElementById(gra_ch_id);

			var grade_checkbox_len_total=$(tr_obj).find("input[type='checkbox'][name='grade_checkbox']").length;

			var grade_checkbox_len_checked=$(tr_obj).find("input[type='checkbox'][name='grade_checkbox']:checked").length;

// 			alert("点击年级grade_checkbox_len_total:"+grade_checkbox_len_total+",grade_checkbox_len_checked:"+grade_checkbox_len_checked);

			if(grade_checkbox_len_checked==0){

				$(tr_obj).find("input[type='checkbox'][name='school_checkbox']").removeAttr("checked");

			}

			if(grade_checkbox_len_checked>0){

				$(tr_obj).find("input[type='checkbox'][name='school_checkbox']").attr("checked",'true');

			}

			

		});

		

	});//ready function结束

	

	

	function delSqlFileDir(){

		var deleteFileUrl="exportSqlAction!deleteExportSqlDir.action";

		$.ajax( {

			type : "POST",//用POST方式传输

			cache : false,

			async : false,

			dataType : "html",//数据格式:JSON

			url :deleteFileUrl,//目标地址

			data : {

			},

			success : function(data) {

// 				alert("success:"+data);

			},

			error : function(data) {

				alert("生成sql文件目录error:"+data);

			}

		});

		

	}

	

	

	

	

	// 		---学校未选中，处理后面的年级复选框----开始

		function school_unchecked(){

			$("input[type='checkbox'][name='school_checkbox']").each(function (i) {

         		var sch_ch_id=$(this).attr("id");

         		var tr_obj=$(this).parent().parent();

//          		alert("tr_obj.html:"+$(tr_obj).html());

				var check_obj_checked=document.getElementById(sch_ch_id);

// 				alert("check_obj_checked.checked:"+check_obj_checked.checked);

				if(check_obj_checked.checked==false){

					$(tr_obj).find("input[type='checkbox'][name='grade_checkbox']").removeAttr("checked");

				}

			});

		}



// 		---学校未选中，处理后面的年级复选框----结束

	

		function abc(){

			if($("#exam option:selected").length==0){

				alert("您还没有选择考试");

				return;

			}

			var spanStr='<span style="padding: 7px;"></span>';

			var str='<tr class="trtitle">'+

						'<td style="width:30%;">学校名称</td><td>年级</td>'+

					'</tr>';

			$("#school_grade_table").empty();

			$("#top_one").empty();

			$(str).appendTo($("#school_grade_table"));

			$("#exam option:selected").each(function (i) {

			var this_examNum=$(this).val();

// 				alert(i+",考试编号："+$(this).val());

				var url="exportSqlAction!getSchools.action?exam="+this_examNum;

				$.ajax({

					type : "POST",//用POST方式传输

					cache : false,

					async : false,

					dataType : "json",//数据格式:JSON

					url :url,//目标地址

					data : {

					},

					success : function(data) {

// 						alert("success---data:"+data);

						var temp="";

						$.each(data,function(i) {

							temp += '<tr>'+

// 										'<td>'+ data[i].schoolNum+'</td>'+ 

// 										'<td></td>'+

										'<td>'+data[i].schoolName+'<input type="checkbox" checked="cheked"  name="school_checkbox" id="'+data[i].schoolNum+'"/>'+

										'<input type="hidden" name="single_school_examNum" value="'+this_examNum+'" />'+

										'</td>'; 

// 									'</tr>';

// 						-------------循环参加考试的学校结束，根据学校编号查询参加考试的年级-----开始

							var gradeUrl="ajaxAction!getCorrctGradeList.action?exam="+this_examNum+"&school="+data[i].schoolNum;

// 							var gradeStr=abcGrade(gradeUrl);

// 							temp +=gradeStr;

// 							alert("temp:"+temp);

							$.ajax({

								type : "POST",//用POST方式传输

								cache : false,

								async : false,

								dataType : "json",//数据格式:JSON

								url :gradeUrl,//目标地址

								data : {

								},

								success : function(gradeData) {

// 									----------每个学校对应的所有年级是一个td

									temp +='<td>';

									$.each(gradeData,function(j){

// 										alert(data[i].schoolName+",---,"+gradeData[j].num+",---,"+gradeData[j].name);

										temp +=gradeData[j].name+

										'<input type="checkbox" checked="cheked" name="grade_checkbox" id="'+data[i].schoolNum+'_'+gradeData[j].num+'" />'+

										spanStr;

									});

									temp +='</td>';

								},

								error : function(dara) {

									alert("根据学校编号查询考试的年级   ---异常");

									temp +='<td>此学校暂无年级信息</td>';

								}

							});

// 						-------------循环参加考试的学校并根据学校编号查询参加考试的年级------------------结束

						});

// 						---拼每一行的最后一个</tr>

						temp +='</tr>';//------循环拼所有学校及对应的年级结束

						temp +='<tr><td colspan="2"><div style="height: 35px;padding-top:5px;">'+

									'是否导出基础数据：<input type="checkbox" checked="cheked" id="check_basic" />'+

								'</div>';

						temp +='<div style="height: 35px;">是否导出图片数据：<input type="checkbox" style="display:none;" checked="cheked" id="check_image" name="parent_checkbox" />'+

									'[答题卡<input type="checkbox" checked="cheked" id="check_examPaperImage" name="child_checkbox" />'+

									spanStr+'试题图片<input type="checkbox" checked="cheked" id="check_questionImage" name="child_checkbox" />'+

									spanStr+'分数图片<input type="checkbox" checked="cheked" id="check_scoreImage" name="child_checkbox" />'+

									spanStr+'考号图片<input type="checkbox" checked="cheked" id="check_examineeNumImage" name="child_checkbox" />]'+

									'</div></td></tr>';

// 						$(top_one_str).appendTo($("#top_one"));

						$(temp).appendTo($("#school_grade_table"));

// 						alert($("#school_grade_table tr:eq(1)").find(":checkbox:eq(0)").attr("id"));

					},//--查询学校success结束

					error : function(XMLHttpRequest, textStatus, errorThrown) {

						alert("根据考试编号 查询参加考试的学校---异常");

					}

				});//$.ajax结束

				

         	});//$("#exam option:selected").each结束

		}//function  abc 结束

		

		function abcGrade(graUrl){

			var gradeStr="";

			$.ajax({

				type : "POST",//用POST方式传输

				cache : false,

				async : false,

				dataType : "json",//数据格式:JSON

				url :gradeUrl,//目标地址

				data : {

				},

				success : function(gradeData) {

// 					----------每个学校对应的所有年级是一个td

					gradeStr +='<td>';

					$.each(gradeData,function(j){

// 						alert(data[i].schoolName+",---,"+gradeData[j].num+",---,"+gradeData[j].name);

						gradeStr +=gradeData[j].name+

						'<input type="checkbox" checked="cheked" name="grade_checkbox" id="'+data[i].schoolNum+'_'+gradeData[j].num+'" />'+

						spanStr;

					});

					gradeStr +='</td>';

				},

				error : function(dara) {

					alert("根据学校编号查询考试的年级   ---异常");

					gradeStr +='<td>此学校暂无年级信息</td>';

				}

			});

			alert(gradeStr);

			return gradeStr;

		

		}

	

	

	</script>

	<style type="text/css">

		body{

			font-size:13px;

		}

	</style>

	

  </head>

  

  <body>

  	<form action="exportSqlAction.action" method="post" 

  	enctype="multipart/form-data"   name="form1">

  		

  		<div id="top_div" style="padding-left:30px;padding-top: 10px;padding-bottom: 20px;margin-top: 5px;background-color: #F8F8FF;border: 1px #F8F8FF dotted;">

	    	<input type="hidden" value="${exam}" id="exam_val"/>

		  	<input type="hidden"  value="${subject}" id="subject_val"/>

		  	<input type="hidden"  value="${grade}" id="grade_val"/>

		  	<input type="hidden" value="${school}" id="school_val"/>

			<span style="height: 35px;">

				考试：<select id="exam" name="exam" style="width: 260px;"></select><span style="padding:3px"></span>

<!-- 				科目： -->

				<select id="subject" name="subject" style="display: none;"></select><span style="padding:3px"></span>

<!-- 				学校： -->

				<select id="school" name="school" style="display: none;"></select><span style="padding:3px"></span>

<!-- 				年级： -->

				<select id="grade" name="grade" style="width: 60px;display: none;"></select><span style="padding:3px"></span>

			</span>

			<table class="list_table" id="school_grade_table" style="margin-bottom: 20px;width:70%;">

	  			<tr class="trtitle">

	  				<td style="width:30%;">学校名称</td>

	  				<td style="width:30%;">年级</td>

	  			</tr>

	  		</table>

	  		<div>

	  			<input type="button" value="导出符合以上条件的数据文件"  id="btn_export_sql" style="height: 25px" />

	  		</div>

  		</div>

  		

  		<div id="bottom_div" style="padding-left:30px;padding-top: 10px;padding-bottom: 20px;background-color:#F8F8FF;border: 1px #F8F8FF dotted;margin-top: 20px;">

  			<input type="hidden" id="hid_upload"/>

	  		<input type="file" id="upload" name="upload" size="50" style="height: 25px;" />

	  		<input type="button" value="数据导入"  id="btn_import_sql" style="height: 25px" />

	  		<span id="spanSWFUploadButton"></span>

  		</div>

  		

  		<img id="loadImg" style="display:none;margin-top: 100px;margin-left: 200px; " src="common/image/load.gif"/>

		<span style="display:none;font-size: 14px;color: green;padding-left: 100px;" id="note_span">正生成文件,数据量大 ,请等待。。。</span>

	</form>

  </body>

</html>

