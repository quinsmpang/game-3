<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="游戏管理";
String perm="系统公告管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String title="";
String content="";
String writer="";
String starttime="";
String overtime="";
String channel="";
String chooseAll_channel="";


int id = ToolFunc.str2int(request.getParameter("id"));

SysNoticeBAC sysNoticeBAC = SysNoticeBAC.getInstance();
JSONObject xml = sysNoticeBAC.getJsonObj("id="+id);
if(xml!=null)
{
title=xml.optString("title");
content=xml.optString("content");
writer=xml.optString("writer");
starttime=xml.optString("starttime");
overtime=xml.optString("overtime");
channel=xml.optString("channel");

if(channel.equals("0")){
	chooseAll_channel="1";
	DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
	StringBuffer sb = new StringBuffer("|");
	while(channelRs.next()){
		sb.append(channelRs.getString("code"));
		sb.append("|");
	}
	channel=sb.toString();
}
} else {
writer=userObj.optString("username");
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
<script src="../js/Calendar3.js"></script>
<script src="../js/chooseAll.js"></script>
<script>
var allValue=new Object();
allValue.id="<%=id%>";
allValue.title="<%=title%>";
allValue.writer="<%=writer%>";
allValue.starttime="<%=starttime%>";
allValue.overtime="<%=overtime%>";
allValue.channel="<%=channel%>";
allValue.chooseAll_channel="<%=chooseAll_channel%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.title.value=="")
{
alert("请输入标题");
theForm.title.focus();
return false;
}
if(theForm.content.value=="")
{
alert("请输入内容");
theForm.content.focus();
return false;
}
if(theForm.writer.value=="")
{
alert("请输入作者");
theForm.writer.focus();
return false;
}
if(theForm.starttime.value=="")
{
alert("请输入发布时间");
theForm.starttime.focus();
return false;
}
if(!isDate(theForm.starttime.value) && !isTime(theForm.starttime.value))
{
alert("发布时间请输入日期格式");
theForm.starttime.focus();
theForm.starttime.select();
return false;
}
if(theForm.overtime.value=="")
{
alert("请输入过期时间");
theForm.overtime.focus();
return false;
}
if(!isDate(theForm.overtime.value) && !isTime(theForm.overtime.value))
{
alert("过期时间请输入日期格式");
theForm.overtime.focus();
theForm.overtime.select();
return false;
}


	
theForm.encoding="multipart/form-data";
theForm.target="hiddenFrame";
theForm.action="sysnotice_upload.jsp";
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
                    <td nowrap background="../images/tab_midbak.gif">系统公告管理</td>
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
<td width="10%" align="right" nowrap class="nrbgc1"><strong>标题</strong></td>
<td width="93%" class="nrbgc1"><input name="title" type="text" value=""><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>作者</strong></td>
<td width="93%" class="nrbgc1"><input name="writer" type="text" value="" readonly="true"><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>内容</strong></td>
<td width="93%" class="nrbgc1"><font color="#FF0000"><textarea name="content" cols="60" rows="10" id="content"><%=content%></textarea></font><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>发布时间</strong></td>
<td width="93%" class="nrbgc1"><input name="starttime" type="text" value="" onClick="new Calendar().show(this);"><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>过期时间</strong></td>
<td width="93%" class="nrbgc1"><input name="overtime" type="text" value="" onClick="new Calendar().show(this);"><font color="#FF0000">*</font></td>
</tr>
<tr valign="top"><td width="10%" align="right" nowrap class="nrbgc1"><strong>发布渠道</strong>
</td>
<td width="93%" class="nrbgc1">
	<table>
	<tr>
	<td colspan="4">
	<input name="chooseAll_channel" type="checkbox" id="chooseAll_channel" value="1" onClick="chooseAllCate2('chooseAll_channel','channel')">&nbsp;&nbsp;全选<br>
	</td>
	</tr>
	<%
	DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
	while(channelRs.next()){
		String key = channelRs.getString("code");
		String val = channelRs.getString("name");
	%>
	<%
	if(channelRs.getRow() % 4 == 1){
	%>
	<tr>
	<%
	}
	%>
	<td>
	<input name="channel" type="checkbox" id="channel" value="<%=key%>" onClick="updateChooseAll2('chooseAll_channel',this)"><%=val+(channelRs.getRow()%4==0?"<br>":"")%>
	</td>
	<%
	if(channelRs.getRow() % 4 == 0){
	%>
	</tr>
	<%
	}
	%>
	<%
	}
	%>
	</table>
</td>
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