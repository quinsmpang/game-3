<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="服务器管理";
String perm="游戏服务器";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String name="";
String http="";
String tcp="";
int maxplayer=2000;
String opentime = "";
int reslv=1;
int state=0;
int tip=0;
String note = "";
int usestate=0;
String usenote="";
String shownote="";

int id = ToolFunc.str2int(request.getParameter("id"));
String serverid="";
ServerBAC serverBAC = ServerBAC.getInstance();


JSONObject xml = serverBAC.getJsonObj("id="+id);
if(xml!=null)
{
serverid = xml.optString("id");
name=xml.optString("name");
http=xml.optString("http");
tcp=xml.optString("tcp");
maxplayer= xml.optInt("maxplayer");
opentime=xml.optString("opentime");
reslv=xml.optInt("reslv");
state = xml.optInt("state");
tip = xml.optInt("tip");
note = xml.optString("note");
usestate = xml.optInt("usestate");
usenote = xml.optString("usenote");
shownote = xml.optString("shownote");
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
allValue.serverid="<%=serverid%>";
allValue.name="<%=name%>";
allValue.http="<%=http%>";
allValue.tcp="<%=tcp%>";
allValue.maxplayer="<%=maxplayer%>";
allValue.opentime="<%=opentime%>";
allValue.reslv="<%=reslv%>";
allValue.state="<%=state%>";
allValue.tip="<%=tip%>";
allValue.note="<%=note%>";
allValue.usestate="<%=usestate%>";
allValue.usenote="<%=usenote%>";
allValue.shownote="<%=shownote%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];	
	
	if(theForm.serverid.value=="")
	{
		alert("请输入服务器id");
		theForm.serverid.focus();
		return false;
	}
	if(theForm.name.value=="")
	{
		alert("请输入服务器名");
		theForm.name.focus();
		return false;
	}
	
	if(theForm.http.value=="")
	{
		alert("请输入http连接地址");
		theForm.http.focus();
		return false;
	}
	if(theForm.tcp.value=="")
	{
		alert("请输入tcp连接地址");
		theForm.tcp.focus();
		return false;
	}
	if(theForm.maxplayer.value=="")
	{
		alert("请输入最大人数");
		theForm.maxplayer.focus();
		return false;
	}
	if(isNaN(theForm.maxplayer.value))
	{
		alert("最大人数请输入数值");
		theForm.maxplayer.focus();
		return false;
	}	
	if(theForm.opentime.value=="")
	{
		alert("请输入全局开服时间");
		theForm.opentime.focus();
		return false;
	}
	if(theForm.reslv.value=="")
	{
		alert("请输入资源等级");
		theForm.reslv.focus();
		return false;
	}
	
	
	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="server_upload.jsp";
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
                    <td nowrap background="../images/tab_midbak.gif">游戏服务器管理</td>
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
                              <td align="right" nowrap class="nrbgc1"><strong>服务器ID</strong></td>
                              <td class="nrbgc1"><input name="serverid" type="text" id="serverid" value="">
                              <font color="#FF0000">*</font></td>
                            </tr>
                            <tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>服务器名</strong></td><td width="93%" class="nrbgc1"><input name="name" type="text" value=""> <font color="#FF0000">*</font></td></tr>
                            
                            
                            

<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>http连接地址</strong></td><td width="93%" class="nrbgc1"><input name="http" type="text" value="" size="50">
  <font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>tcp连接地址</strong></td><td width="93%" class="nrbgc1"><input name="tcp" type="text" value="" size="50">
  <font color="#FF0000">*</font></td></tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>最大人数</strong></td>
  <td class="nrbgc1"><input name="maxplayer" type="text" id="maxplayer" value="">
    <font color="#FF0000">*</font></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>开服时间</strong></td>
  <td class="nrbgc1"><input name="opentime" type="text" id="opentime" value="" onClick="setday(this,true)">
    <font color="#FF0000">*</font></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>资源等级</strong></td>
  <td class="nrbgc1"><input name="reslv" type="text" id="reslv" value="">
    <font color="#FF0000">*</font></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>状态</strong></td>
  <td class="nrbgc1"><select name="state" id="state">
    <option value="0">正常</option>
    <option value="1">维护</option>
    </select></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>标签</strong></td>
  <td class="nrbgc1"><select name="tip" id="tip">
    <option value="0">无 </option>
    <option value="1">推荐</option>
	<option value="2">火爆</option>    
  </select></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>自定维护描述语</strong></td>
  <td class="nrbgc1"><input name="note" type="text" id="note" value=""></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>启用</strong></td>
  <td class="nrbgc1">
  <select name="usestate">
  <option value="0">停用</option>
  <option value="1">启用</option>
  </select>
  </td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>启用说明</strong></td>
  <td class="nrbgc1"><input name="usenote" type="text" id="usenote" value=""></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>弹出说明</strong></td>
  <td class="nrbgc1"><input name="shownote" type="text" id="shownote" value=""></td>
</tr>
<%if(id>0){%>
<%}%>
                            </table>
				
                            <table width="100%"  border="0" cellspacing="1" cellpadding="2">
                              <tr>
                                <td align="right"><input name="id" type="hidden" id="id">
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