<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="版本更新";
String perm="升级补丁";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String channel="";
String packagename="";
String fromversion="";
String toversion="";
String patchfile="";
int filesize=0;
String savetime="";
String platform="1";
String crc="";

int id = ToolFunc.str2int(request.getParameter("id"));

PatchVerBAC patchVerBAC = PatchVerBAC.getInstance();
JSONObject xml = patchVerBAC.getJsonObj("id="+id);
if(xml!=null)
{
channel=xml.optString("channel");
packagename=xml.optString("packagename");
fromversion=xml.optString("fromversion");
toversion=xml.optString("toversion");
patchfile=xml.optString("patchfile");
filesize=xml.optInt("filesize");
savetime=xml.optString("savetime");
platform = xml.optString("platform");
crc = xml.optString("crc");
}
else
{
savetime=Tools.getCurrentDateTimeStr();
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
allValue.channel="<%=channel%>";
allValue.packagename="<%=packagename%>";
allValue.fromversion="<%=fromversion%>";
allValue.toversion="<%=toversion%>";
allValue.patchfile="<%=patchfile%>";
allValue.filesize="<%=filesize%>";
allValue.savetime="<%=savetime%>";
allValue.platform="<%=platform%>";
allValue.crc="<%=crc%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.channel.value=="")
	{
		alert("请选择渠道");
		theForm.channel.focus();
		return false;
	}
	if(theForm.fromversion.value=="")
	{
	alert("请输入原版本号");
	theForm.fromversion.focus();
	return false;
	}
	if(theForm.toversion.value=="")
	{
	alert("请输入新版本号");
	theForm.toversion.focus();
	return false;
	}
	if(theForm.patchfile.value=="")
	{
	alert("请输入补丁文件名");
	theForm.patchfile.focus();
	return false;
	}
	if(theForm.filesize.value=="")
	{
	alert("请输入文件大小");
	theForm.filesize.focus();
	return false;
	}
	if(isNaN(theForm.filesize.value))
	{
	alert("文件大小请输入数值");
	theForm.filesize.focus();
	theForm.filesize.select();
	return false;
	}
	if(theForm.crc.value=="")
	{
	alert("请输入文件CRC值");
	theForm.crc.focus();
	return false;
	}
	if(theForm.savetime.value!="" && !isDate(theForm.savetime.value) && !isTime(theForm.savetime.value))
	{
	alert("保存时间请输入日期格式");
	theForm.savetime.focus();
	theForm.savetime.select();
	return false;
	}


	
	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="patch_ver_upload.jsp";
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
                    <td nowrap background="../images/tab_midbak.gif">升级补丁</td>
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
                            <tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>渠道</strong></td><td width="93%" class="nrbgc1">
								<select name="channel" id="channel">
									<option value="">联运渠道</option>
									<%
									DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
									while(channelRs.next()) {
									%>
										<option value="<%=channelRs.getString("CODE")%>">
											<%=channelRs.getString("NAME")%>(<%=channelRs.getString("CODE")%>)</option>
										<%
									}
										%>
								</select><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>包名</strong></td><td width="93%" class="nrbgc1"><input name="packagename" type="text" value="">
如com.moonic.xianmo</td>
</tr>

                            <tr>
                              <td align="right" nowrap class="nrbgc1"><strong>平台</strong></td>
                              <td class="nrbgc1"><select name="platform" id="platform">
                                  <option value="1">android</option>
                                  <option value="2">ios</option>
                                </select>                              </td>
                            </tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>原版本号</strong></td><td width="93%" class="nrbgc1"><input name="fromversion" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>新版本号</strong></td><td width="93%" class="nrbgc1"><input name="toversion" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>补丁文件名</strong></td><td width="93%" class="nrbgc1"><input name="patchfile" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>文件大小</strong></td><td width="93%" class="nrbgc1"><input name="filesize" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>文件CRC</strong></td>
  <td class="nrbgc1"><input name="crc" type="text" id="crc" value="">
    <font color="#FF0000">*</font></td>
</tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>保存时间</strong></td><td width="93%" class="nrbgc1"><input name="savetime" type="text" value=""></td></tr>
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