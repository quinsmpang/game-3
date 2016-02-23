<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="版本更新";
String perm="升级资源包";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String version="";
String updfile="";
String savetime="";
int mustupdate=0;
int filesize=0;
int platform=0;
String subfolder="";

int id = ToolFunc.str2int(request.getParameter("id"));
ResVerBAC resVerBAC = ResVerBAC.getInstance();
JSONObject xml = resVerBAC.getJsonObj("id="+id);
if(xml!=null)
{
version=xml.optString("version");
updfile=xml.optString("updfile");
savetime=xml.optString("savetime");
mustupdate=xml.optInt("mustupdate");
filesize=xml.optInt("filesize");
platform = xml.optInt("platform");
subfolder = xml.optString("subfolder");
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<title></title>

<link href="../css/style1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="skins/aspsky_1.css" >
<link rel="STYLESHEET" type="text/css" href="images/post/edit.css">
<script src="../js/common.js"></script>
<script src="../js/patterns.js"></script>
<script src="../js/meizzDate.js"></script>
<script src="../js/DhtmlEdit.js"></script>
<script>
var allValue=new Object();
allValue.id="<%=id%>";
allValue.version="<%=version%>";
allValue.updfile="<%=updfile%>";
allValue.savetime="<%=savetime%>";
allValue.mustupdate="<%=mustupdate%>";
allValue.filesize="<%=filesize%>";
allValue.platform="<%=platform%>";
allValue.subfolder="<%=subfolder%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.platform.value=="")
	{
		alert("请选择渠道");
		theForm.platform.focus();
		return false;
	}
	if(theForm.version.value=="")
	{
		alert("请输入版本");
		theForm.version.focus();
		return false;
	}
	if(theForm.updfile.value=="")
	{
		alert("请输入文件名");
		theForm.updfile.focus();
		return false;
	}
	if(theForm.savetime.value=="")
	{
		alert("请输入时间");
		theForm.savetime.focus();
		return false;
	}
	if(theForm.mustupdate.value!="" && isNaN(theForm.mustupdate.value))
	{
		alert("必须升级请输入数值");
		theForm.mustupdate.focus();
		theForm.mustupdate.select();
		return false;
	}
	if(theForm.filesize.value!="" && isNaN(theForm.filesize.value))
	{
		alert("文件大小请输入数值");
		theForm.filesize.focus();
		theForm.filesize.select();
		return false;
	}


	
	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="res_ver_upload.jsp";
	theForm.submit();
	
	wait();
}

</script>
</head>
<body bgcolor="#EFEFEF" >

<form action="" method="post" enctype="multipart/form-data" name="form1"> 
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td ><table width="100%"  border="0" cellspacing="1" cellpadding="2">
    <tr>
      <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
          <tr>
            <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="self.close()"> <img src="../images/icon_closewindow1.gif" width="16" height="16" align="absmiddle"> 关闭</td>
          </tr>
      </table></td>
    </tr>
  </table><table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif" width="6" height="22"></td>
                    <td nowrap background="../images/tab_midbak.gif">升级资源包管理</td>
                    <td><img src="../images/tab_right.gif" width="6" height="22"></td>
                  </tr>
                </table> 
                </td>
                <td valign="bottom" > <table width="100%" border="0" cellspacing="0" cellpadding="0" height="22">
                    <tr> 
                      <td></td>
                      <td width=1></td>
                    </tr>
                    <tr > 
                      <td bgcolor="#FFFFFF" colspan="2" height=1></td>
                    </tr>
                    <tr > 
                      <td height=3></td>
                      <td bgcolor="#848284"  height=3 width="1"></td>
                    </tr>
                  </table></td>
              </tr>
            </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td rowspan="3" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
                  <td colspan="2" align="center" valign="top"><table width="95%" border="0" cellspacing="1" cellpadding="2">
                      <tr> 
                        <td><fieldset><legend>编辑</legend>
                            
                            <table width="100%" border="0" cellspacing="1" cellpadding="2">   
							<tr>
                              <td align="right" nowrap class="nrbgc1"><strong>渠道</strong></td>
                              <td class="nrbgc1">
                              
                                <select name="platform" id="platform">
										<option value="0">选择</option>
										<option value="1">android</option>
										<option value="2">ios</option>
										<option value="3">pc</option>
								</select> <font color="#FF0000">*</font></td>
                            </tr>
                            <tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>版本</strong></td><td width="93%" class="nrbgc1"><input name="version" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>文件名</strong></td><td width="93%" class="nrbgc1"><input name="updfile" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>时间</strong></td><td width="93%" class="nrbgc1"><input name="savetime" type="text" value="" onClick="setday(this,true)"><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>必须升级</strong></td><td width="93%" class="nrbgc1"><input name="mustupdate" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>文件大小</strong></td><td width="93%" class="nrbgc1"><input name="filesize" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>子目录</strong></td><td width="93%" class="nrbgc1"><input name="subfolder" type="text" value=""></td></tr>
                            </table>
				
                            <table width="100%"  border="0" cellspacing="1" cellpadding="2">
                              <tr>
                                <td align="right">
				<input type="hidden" name="id"/>
                                  <table width="50" border="0" cellspacing="0" cellpadding="2">
                                    <tr>
                                      <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="checkForm()"><img src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 保存</td>
                                    </tr>
                                  </table></td>
                              </tr>
                            </table>
                        </fieldset><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                              <tr>
                                <td height="25"> <font color="#FF0000">*</font> 必填项</td>
                              </tr>
                            </table></td>
                      </tr>
                  </table></td>
                  <td rowspan="3" bgcolor="#848284" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
                </tr>
                
                <tr> 
                  <td height="1" colspan="2" bgcolor="#848284"></td>
                </tr>
              </table></td>
        </tr>
      </table></td>
  </tr>
</table>
</form>
<iframe name="hiddenFrame" width=0 height=0></iframe>
<script>
autoChoose(allValue);
</script>
</body>
</html>