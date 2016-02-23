<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="游戏管理";
String perm="游戏活动管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String name="";
int actitype=0;
String showtime="";
String hidetime="";
String starttime="";
String endtime="";
String note="";
String award="";
String imgurl="";
int layout=1;
String server="";
String channel="";
String chooseAll="";
String chooseAll_channel="";
String opentime="";
int expirationlen=0;

int id = ToolFunc.str2int(request.getParameter("id"));

int selectmodel = ToolFunc.str2int(request.getParameter("selectmodel"));

//System.out.println("selectmodel:"+selectmodel);

JSONObject xml = CustomActivityBAC.getInstance().getJsonObj("id="+id);
if(xml!=null)
{
	name=xml.optString("name");
	actitype=xml.optInt("actitype");
	showtime=xml.optString("showtime");
	hidetime=xml.optString("hidetime");
	starttime=xml.optString("starttime");
	endtime=xml.optString("endtime");
	note=xml.optString("note");
	award=Tools.strNull(xml.optString("award"));
	imgurl=Tools.strNull(xml.optString("imgurl"));
	layout=xml.optInt("layout");
	server=xml.optString("server");
	channel=xml.optString("channel");
	opentime=xml.optString("opentime");
	expirationlen=xml.optInt("expirationlen");
} else 
if(selectmodel != 0){
	DBPaRs osactiRs2 = DBPool.getInst().pQueryA(CustomActivityBAC.tab_openserver_activity, "id="+selectmodel);
	if(osactiRs2.exist()){
		name=osactiRs2.getString("name");
		actitype=osactiRs2.getInt("actitype");
		showtime=MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day*osactiRs2.getInt("showtimeoffset")) ;
		starttime=MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day*osactiRs2.getInt("starttimeoffset")) ;
		if(osactiRs2.getInt("endtimeoffset") != -1){
			endtime=MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day*osactiRs2.getInt("endtimeoffset")) ;
		}
		if(osactiRs2.getInt("hidetimeoffset") != -1){
			hidetime=MyTools.getTimeStr(MyTools.getCurrentDateLong()+MyTools.long_day*osactiRs2.getInt("hidetimeoffset")) ;
		}
		note=osactiRs2.getString("note");
		award=Tools.strNull(osactiRs2.getString("award"));
		if(!osactiRs2.getString("imgurl").equals("-1")){
			imgurl=Tools.strNull(osactiRs2.getString("imgurl"));		
		}
		layout=osactiRs2.getInt("layout");
		//server=xml.optString("server");
		channel=osactiRs2.getString("channel");
		expirationlen=osactiRs2.getInt("expirationlen");
	}
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
<script src="../js/draw_img.js"></script>
<script src="../js/chooseAll.js"></script>
<script type="text/javascript" src="http://www.jq-school.com/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="../js/bubbletips.js"></script>
<script>
var allValue=new Object();
allValue.id="<%=id%>";
allValue.name="<%=name%>";
allValue.actitype="<%=actitype%>";
allValue.showtime="<%=showtime%>";
allValue.hidetime="<%=hidetime%>";
allValue.starttime="<%=starttime%>";
allValue.endtime="<%=endtime%>";
allValue.note="<%=note%>";
allValue.award="<%=award%>";
allValue.imgurl="<%=imgurl%>";
allValue.layout="<%=layout%>";
allValue.server="<%=server%>";
allValue.chooseAll="<%=chooseAll%>";
allValue.channel="<%=channel%>";
allValue.chooseAll_channel="<%=chooseAll_channel%>";
allValue.modellist="<%=selectmodel%>";
allValue.opentime="<%=opentime%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.name.value=="")
	{
		alert("请输入名称");
		theForm.name.focus();
		return false;
	}
	if(theForm.note.value=="")
	{
		alert("请输入活动说明");
		theForm.note.focus();
		return false;
	}
	if(theForm.showtime.value=="")
	{
		alert("请输入显示时间");
		theForm.showtime.focus();
		return false;
	}
	if(!isDate(theForm.showtime.value) && !isTime(theForm.showtime.value))
	{
		alert("显示时间请输入日期格式");
		theForm.showtime.focus();
		theForm.showtime.select();
		return false;
	}
	if(theForm.starttime.value=="")
	{
		alert("请输入开始时间");
		theForm.starttime.focus();
		return false;
	}
	if(!isDate(theForm.starttime.value) && !isTime(theForm.starttime.value))
	{
		alert("开始时间请输入日期格式");
		theForm.starttime.focus();
		theForm.starttime.select();
		return false;
	}
	
	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="customacti_upload.jsp";
	theForm.submit();
	
	wait();
}

function changeModel()
{
	var theForm=document.forms[0];
	//alert("choose:"+theForm.modellist.value);
	theForm.action="customacti_edit.jsp?selectmodel="+theForm.modellist.value;
	theForm.submit();
}

</script>
<style type="text/css">
<!--
.style1 {color: #FF0000}
-->
</style>
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
                    <td nowrap background="../images/tab_midbak.gif">游戏活动</td>
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
<table height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td>
	<table width="100%" border="0" cellspacing="1" cellpadding="2">    
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>活动模版</strong></td>
<td width="93%" class="nrbgc1">


<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  	<td>
  		<select name="modellist" onChange="changeModel()">
	  	<option value="0">自定义</option>
	  	<%
	  	DBPsRs osactiRs = DBPool.getInst().pQueryS(CustomActivityBAC.tab_openserver_activity);
	  	while(osactiRs.next()){
	  	%>
	  	<option value="<%=osactiRs.getInt("id") %>"><%=osactiRs.getString("name") %></option>
	  	<%	
	  	}
	  	%>
		</select>
  	</td>
	<td align="right">
		<input type="hidden" name="id"/>
		<table width="50" border="0" cellspacing="0" cellpadding="2">
		<tr>
		<td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="checkForm()"><img src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 保存</td>
		</tr>
		</table>
	</td>
	<td width="15">&nbsp;
	
	</td>
  </tr>
</table>

</td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>名称</strong></td>
<td width="93%" class="nrbgc1"><input name="name" type="text" value="" onpropertychange="changeSpan('s_name', 'name')"><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>活动类型</strong></td>
<td width="93%" class="nrbgc1"><input name="actitype" type="text" value=""><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>显示时间</strong></td>
<td width="93%" class="nrbgc1"><input name="showtime" onClick="new Calendar().show(this);" type="text" value=""><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>开始时间</strong></td>
<td width="93%" class="nrbgc1"><input name="starttime" onClick="new Calendar().show(this);" type="text" value=""><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>结束时间</strong></td>
<td width="93%" class="nrbgc1"><input name="endtime" onClick="new Calendar().show(this);" onpropertychange="changeSpan('s_endtime', 'endtime')" type="text" value=""><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>隐藏时间</strong></td>
<td width="93%" class="nrbgc1"><input name="hidetime" onClick="new Calendar().show(this);" type="text" value=""><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>活动内容</strong></td>
<td width="93%" class="nrbgc1"><textarea name="note" cols="40" rows="5" onpropertychange="changeSpan('s_note', 'note')"></textarea><font color="#FF0000">*</font></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>条件与奖励</strong></td>
<td width="93%" class="nrbgc1"><textarea id="award" name="award" cols="40" rows="5" onpropertychange="changeSpan('s_award', 'award')" onMouseOver="showHelp(this,'注1：“条件#奖励#职业奖励#游戏推送”为一组<br>注2：单组条件参考条件规范<br>注3：单组奖励参考奖励规范<br>注4：推送格式|推送编号,参数1,参数2..<br>注5：有多组以“##”分隔<br>注7：缺省值均为-1，职业奖励或游戏推送在末位且值为-1时可省略')" onMouseOut="hideHelp()"></textarea><br>
</td>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>配图URL</strong></td>
<td width="93%" class="nrbgc1"><input name="imgurl" type="text" value=""></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>活动布局</strong></td>
<td width="93%" class="nrbgc1"><input name="layout" type="text" value=""></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>限定开服时间</strong></td>
<td width="93%" class="nrbgc1"><input name="opentime" type="text" value="" onClick="new Calendar().show(this);"></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>过期时长（小时）</strong></td>
<td width="93%" class="nrbgc1"><input name="expirationlen" type="text" value=""></td>
</tr>
<tr valign="top"><td width="10%" align="right" nowrap class="nrbgc1"><strong>举办服务器</strong>
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

<tr valign="top"><td width="10%" align="right" nowrap class="nrbgc1"><strong>举办渠道</strong>
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
    <td valign="top">
      <table height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td align="left" valign="top">
	预览：
	<script>
	function changeSpan(span_name, score_name){
		var	obj = document.getElementById(span_name);
		var str = "<span id=\""+span_name+"\">"+document.getElementById(score_name).value+"</span>";
		obj.outerHTML = str;
	}
	</script>
	  <table width="191" height="230" border="0" cellpadding="2" cellspacing="1" bgcolor="#333333" style="word-break:break-all">
      <tr>
        <td height="10" align="center" bgcolor="#EFEFEF"><span id="s_name"></span></td>
      </tr>
      <tr>
        <td height="10" valign="top" bgcolor="#EFEFEF">活动内容<br>
          <span id="s_note"></span></td>
      </tr>
      <tr>
        <td height="10" valign="top" bgcolor="#EFEFEF">活动结束时间：<span id="s_endtime"></span><br>
          活动剩余时间：23:59:59</td>
      </tr>
      <tr>
        <td valign="top" bgcolor="#EFEFEF">活动奖励<br>
        	<span id="s_award"></span>
		  </td>
      </tr>
      
    </table></td>
  </tr>
  <tr>
    <td valign="bottom">&nbsp;
	  
	</td>
  </tr>
</table>
	</td>
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
                  <td height="1" colspan="2" bgcolor="#848284">=</td>
                </tr>
              </table>
		</td>
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