<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="游戏管理";
String perm="邮件管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
int tgr=1;
String title="";
String content="";
String adjunct="";
String endtime=MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day*7);
String createtime="";
String filtercond="";

String server="";
String channel="";
String chooseAll="";
String chooseAll_channel="";

int id = ToolFunc.str2int(request.getParameter("id"));

SysMailBAC sysMailBAC = SysMailBAC.getInstance();
JSONObject xml = sysMailBAC.getJsonObj("id="+id);
if(xml!=null)
{
tgr=xml.optInt("tgr");
title=xml.optString("title");
content=xml.optString("content");
adjunct=xml.optString("adjunct");
endtime=xml.optString("endtime");
createtime=xml.optString("createtime");
filtercond=xml.optString("filtercond");
server=xml.optString("server");
channel=xml.optString("channel");
}

if(server.equals("0")){
	chooseAll="1";
	DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server, "usestate=1");
	StringBuffer sb = new StringBuffer("|");
	while(serverRs.next()){
		sb.append(serverRs.getInt("id"));
		sb.append("|");
	}
	server=sb.toString();
}

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
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<title></title>

<link href="../css/style1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="skins/aspsky_1.css" >
<link rel="STYLESHEET" type="text/css" href="images/post/edit.css">
<link href="../css/bubbletips.css" type="text/css" rel="stylesheet" />
<script src="../js/common.js"></script>
<script src="../js/patterns.js"></script>
<script src="../js/meizzDate.js"></script>
<script src="../js/DhtmlEdit.js"></script>
<script src="../js/Calendar3.js"></script>
<script src="../js/chooseAll.js"></script>
<script type="text/javascript" src="http://www.jq-school.com/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="../js/bubbletips.js"></script>
<script>
var allValue=new Object();
allValue.id="<%=id%>";
allValue.tgr="<%=tgr%>";
allValue.title="<%=title%>";
allValue.adjunct="<%=adjunct%>";
allValue.endtime="<%=endtime%>";
allValue.createtime="<%=createtime%>";
allValue.filtercond="<%=filtercond%>";
allValue.server="<%=server%>";
allValue.chooseAll="<%=chooseAll%>";
allValue.channel="<%=channel%>";
allValue.chooseAll_channel="<%=chooseAll_channel%>";
</script>
<script>
self.focus();

var tgrlen=<%=SysMailBAC.tgrType.length%>

function checkForm()
{
	var theForm=document.forms[0];
	
	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="sysmail_upload.jsp";
	theForm.submit();
	
	wait();
}

function changeTgr(){
	var tgrtype = document.getElementById("tgr").value;
	for(var i = 0; i < tgrlen; i++){
		fobj = document.getElementById("tgr"+i);
		fobj.style.display="none";
		if(i==tgrtype){
			fobj.style.display="";
		}
	}
}

</script>
</head>
<body bgcolor="#EFEFEF" >

<form action="" method="post" enctype="multipart/form-data" name="form1"> 
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > 
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td >
          <table width="100%"  border="0" cellspacing="1" cellpadding="2">
    		<tr>
      <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
          <tr>
            <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="self.close()"> <img src="../images/icon_closewindow1.gif" width="16" height="16" align="absmiddle"> 关闭</td>
          </tr>
      </table></td>
    </tr>
  </table>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif" width="6" height="22"></td>
                    <td nowrap background="../images/tab_midbak.gif">系统邮件</td>
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
                        <td><fieldset><legend><%=xml!=null?"查看":"编辑" %></legend>
                            
                            <table width="100%" border="0" cellspacing="1" cellpadding="2">    
                            
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>邮件标题</strong></td>
<td width="93%" class="nrbgc1"><input name="title" type="text" value="" size="40"></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>邮件内容</strong></td>
<td width="93%" class="nrbgc1"><textarea name="content" cols="40" rows="10"><%=content%></textarea></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>附件内容</strong></td>
<td width="93%" class="nrbgc1"><input id="adjunct" name="adjunct" type="text" value="" onMouseOver="showHelp(this,'参考奖励规范')" onMouseOut="hideHelp()"><font color="#FF0000">（除物品外不要填重复类型的附件）</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>对象筛选条件</strong></td>
<td width="93%" class="nrbgc1"><input id="filtercond" name="filtercond" type="text" value="" onMouseOver="showHelp(this,'1.创建角色时间小于等于指定值<br>2.角色等级大于等于指定值<br>3.VIP等级大于等于指定值<br>-1.指定虚拟服务器ID（例：-1,1,2,3）<br>注：多个条件以“|”分隔')" onMouseOut="hideHelp()">
</td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>失效时间</strong></td>
<td width="93%" class="nrbgc1"><input name="endtime" type="text" value="" onClick="new Calendar().show(this);"></td>
</tr>
<%
if(xml!=null){
%>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>创建时间</strong></td>
<td width="93%" class="nrbgc1"><input name="createtime" type="text" value=""></td>
</tr>
<%
}
%>
<tr>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1">
<strong>发送对象</strong></td>
<td width="93%" class="nrbgc1">
<select name="tgr" id="tgr" onpropertychange="changeTgr()">
<%
for(int i = 0; i < SysMailBAC.tgrType.length; i++){
%>
<option value="<%=i%>"><%=SysMailBAC.tgrType[i] %></option>
<%
}
%>
</select>
</td>
</tr>

<tr id="tgr0" style="display:none">
<td width="10%" align="right" nowrap class="nrbgc1"><strong>发送目标清单</strong></td>
<td width="93%" class="nrbgc1">
<%
if(xml != null){
%>
<a href="../../download.do?path=<%=Conf.logRoot+"mail/"+xml.getString("createmark")+".txt"%>" target="_blank">下载清单</a>
<%
} else {
%>
<input name="tgrfile" type="file" id="tgrfile" size="60">
<%
File demofile = new File(Conf.logRoot+"mail/demo.txt");
if(!demofile.exists()){
	StringBuffer sb = new StringBuffer();
	sb.append("\r\n");
	sb.append("data:\r\n");
	sb.append("2（服务器ID）\t251（角色ID）\r\n");
	sb.append("dataEnd");
	FileUtil fileutil = new FileUtil();
	fileutil.writeNewToTxt(Conf.logRoot+"mail/demo.txt", sb.toString());
}
%>
<a href="../../download.do?path=<%=Conf.logRoot+"mail/demo.txt"%>">下载DEMO</a><font color="#ff0000">（请保证上传文件的内容编码为UTF8）</font>
<%
}
%>
</td>
</tr>

<tr id="tgr1" style="display:none">
<td width="93%" class="nrbgc1" colspan="2">
<table width="100%" border="0" cellspacing="0" cellpadding="0">

<tr valign="top"><td width="10%" align="right" nowrap class="nrbgc1"><strong>选择服务器</strong>
</td>
<td width="93%" class="nrbgc1">
	<table>
	<tr>
	<td colspan="4">
	<input name="chooseAll" type="checkbox" id="chooseAll" value="1" onClick="chooseAllCate('server')">&nbsp;&nbsp;全选<br>
	</td>
	</tr>
	<%
	DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server, "usestate=1");
	while(serverRs.next()){
		String key = serverRs.getString("id");
		String val = serverRs.getString("name");
	%>
	<%
	if(serverRs.getRow() % 4 == 1){
	%>
	<tr>
	<%
	}
	%>
	<td>
	<input name="server" type="checkbox" id="server" value="<%=key%>" onClick="updateChooseAll(this)"><%=val+(serverRs.getRow()%4==0?"<br>":"")%>
	</td>
	<%
	if(serverRs.getRow() % 4 == 0){
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

<tr valign="top"><td width="10%" align="right" nowrap class="nrbgc1"><strong>选择渠道</strong>
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
</td>
</tr>

<tr id="tgr2" style="display:none">
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>

                            </table>
				
                            <table width="100%"  border="0" cellspacing="1" cellpadding="2">
                              <tr>
                                <td align="right">
								<input type="hidden" name="id"/>
								<%
								if(xml==null){
								%>
								<table width="50" border="0" cellspacing="0" cellpadding="2">
                                    <tr>
<td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="checkForm()">
<img src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 发送
</td>
                                    </tr>
                                </table>
								<%
								}
								%>
                                  </td>
                              </tr>
                            </table>
                        </fieldset>
                        <%
                        if(xml==null){
                        %>
                        <table width="100%"  border="0" cellspacing="0" cellpadding="0">
                           <tr>
                             <td height="25"><font color="#FF0000">*</font> 必填项</td>
                           </tr>
                        </table>
                        <%
                        }
                        %>
                        </td>
                      </tr>
                  </table>
                  </td>
                  <td rowspan="3" bgcolor="#848284" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
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
changeTgr();
</script>
</body>
</html>