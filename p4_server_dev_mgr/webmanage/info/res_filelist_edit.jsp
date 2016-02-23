<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="版本更新";
String perm="资源列表CRC";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
int platform=1;
String crc="";
String savetime=Tools.getCurrentDateTimeStr();
int enable=1;
String subfolder="";

int id = ToolFunc.str2int(request.getParameter("id"));

ResFilelistBAC resFilelistBAC = ResFilelistBAC.getInstance();
JSONObject xml = resFilelistBAC.getJsonObj("id="+id);
if(xml!=null)
{
platform=xml.optInt("platform");
crc=xml.optString("crc");
enable=xml.optInt("enable");
subfolder=xml.optString("subfolder");
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
allValue.platform="<%=platform%>";
allValue.crc="<%=crc%>";
allValue.enable="<%=enable%>";
allValue.savetime="<%=savetime%>";
allValue.subfolder="<%=subfolder%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];		
	if(theForm.crc.value=="")
	{
		alert("请输出资源列表文件的CRC值");
		theForm.crc.focus();
		return;
	}	
	
	theForm.target="hiddenFrame";
	theForm.action="res_filelist_upload.jsp";
	theForm.submit();
	
	wait();
}

</script>
</head>
<body bgcolor="#EFEFEF" >

<form action="" method="post" enctype="application/x-www-form-urlencoded" name="form1"> 
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
                    <td nowrap background="../images/tab_midbak.gif">资源列表文件CRC设置</td>
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
                            <tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>平台类型</strong></td>
                              <td width="93%" class="nrbgc1"><font color="#FF0000">
                                <label>
                              <select name="platform" id="platform">
                                <option value="1">安卓</option>
                                <option value="2">IOS</option>
								<option value="3">PC</option>
                              </select>
                              </label>
                            </font></td>
                            </tr>
							
							<tr>
							  <td align="right" nowrap class="nrbgc1"><strong>资源列表文件CRC</strong></td>
							  <td class="nrbgc1"><input name="crc" type="text" id="crc" value=""></td>
							  </tr>
							<tr>
							  <td align="right" nowrap class="nrbgc1"><strong>资源下载子目录</strong></td>
							  <td class="nrbgc1"><input name="subfolder" type="text" id="subfolder" value=""></td>
							  </tr>
							<tr>
							  <td align="right" nowrap class="nrbgc1"><strong>生效</strong></td>
							  <td class="nrbgc1">
							    <select name="enable" id="enable">
							      <option value="1">是</option>
							      <option value="0">否</option>
						        </select>
							    <font color="#FF0000">（生效后客户端才会自动更新资源）</font></td>
							  </tr>
							<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>时间</strong></td><td width="93%" class="nrbgc1"><input name="savetime" type="text" value=""></td></tr>
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