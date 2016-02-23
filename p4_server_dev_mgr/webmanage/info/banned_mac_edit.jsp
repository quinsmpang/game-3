<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="客服管理";
String perm="mac禁言";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String mac="";
String savetime="";
String imei="";
String reason="";
String opuser="";
int id = ToolFunc.str2int(request.getParameter("id"));

BannedMacBAC bannedMacBAC = BannedMacBAC.getInstance();
JSONObject xml = bannedMacBAC.getJsonObj("id="+id);
if(xml!=null)
{
mac=xml.optString("mac");
imei=xml.optString("imei");
reason=xml.optString("reason");
savetime=xml.optString("savetime");
opuser=xml.optString("opuser");
}
else
{
savetime = Tools.getCurrentDateTimeStr();
opuser=userObj.optString("username");
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
allValue.mac="<%=mac%>";
allValue.imei="<%=imei%>";
allValue.savetime="<%=savetime%>";
allValue.reason="<%=reason%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.mac.value=="" && theForm.imei.value=="")
	{
		alert("请输入mac地址或imei串号");
		return false;
	}
	if(theForm.reason.value=="")
	{
		alert("请输入禁言理由");
		theForm.reason.focus();
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
	theForm.action="banned_mac_upload.jsp";
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
                    <td nowrap background="../images/tab_midbak.gif">MAC禁言</td>
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
                            <tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>MAC地址</strong></td><td width="93%" class="nrbgc1"><input name="mac" type="text" value=""></td>
                            </tr>
                            <tr>
                              <td align="right" nowrap class="nrbgc1"><strong>IMEI串号</strong></td>
                              <td class="nrbgc1"><input name="imei" type="text" id="imei" value=""></td>
                            </tr>
                            <tr>
                              <td align="right" nowrap class="nrbgc1"><strong>理由</strong></td>
                              <td class="nrbgc1"><input name="reason" type="text" id="reason" value="" size="50">
                                <font color="#FF0000">*</font></td>
                            </tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>保存时间</strong></td><td width="93%" class="nrbgc1"><input name="savetime" type="text" value=""></td></tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>操作人</strong></td>
  <td class="nrbgc1"><%=opuser%></td>
</tr>
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