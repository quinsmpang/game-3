<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="玩家管理";
String perm="帐号管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String username="";
String channel="";
String ip="";
String regtime="";
String logintime="";
int enable=1;
int onlinestate=0;
String sessionid="";
String serverid="";
String playerid="";
String resolution="";
String netname="";
int nettype=0;
int wifi=0;
String imei="";
String mac="";
String phonevendor="";
String phonemodel="";
int phonesdklv=0;
String phonesdkver="";
int phonefreemem=0;
int phonetotalmem=0;
int devuser=0;
int enforcementlogin=0;
String platform="";
int playerId=0;
int serverId=0;
String phonenum="";

int id = ToolFunc.str2int(request.getParameter("id"));

UserBAC userBAC = UserBAC.getInstance();
JSONObject xml = userBAC.getJsonObj("id="+id);

if(xml!=null)
{
username=xml.optString("username");
channel=xml.optString("channel");
ip=xml.optString("ip");
regtime=xml.optString("regtime");
logintime=xml.optString("logintime");
enable=xml.optInt("enable");
onlinestate=xml.optInt("onlinestate");
sessionid=xml.optString("sessionid");
serverid=xml.optString("serverid");
playerid=xml.optString("playerid");
resolution=xml.optString("resolution");
netname=xml.optString("netname");
nettype=xml.optInt("nettype");
wifi=xml.optInt("wifi");
imei=xml.optString("imei");
mac=xml.optString("mac");
phonevendor=xml.optString("phonevendor");
phonemodel=xml.optString("phonemodel");
phonesdklv=xml.optInt("phonesdklv");
phonesdkver=xml.optString("phonesdkver");
phonefreemem=xml.optInt("phonefreemem");
phonetotalmem=xml.optInt("phonetotalmem");
devuser=xml.optInt("devuser");
enforcementlogin=xml.optString("enforcementlogin").equals("")?0:1;
platform=xml.optString("platform");
playerId = xml.optInt("playerId");
serverId = xml.optInt("serverId");
phonenum = xml.optString("phonenum");
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
allValue.devuser="<%=devuser%>";
allValue.enforcementlogin="<%=enforcementlogin%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	theForm.target="hiddenFrame";
	theForm.action="account_upload.jsp";
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
                    <td nowrap background="../images/tab_midbak.gif">帐号管理</td>
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
<td width="10%" align="right" nowrap class="nrbgc1"><strong>用户名</strong></td>
<td colspan="3" class="nrbgc1"><%=username %></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>联运渠道</strong></td>
<td colspan="3" class="nrbgc1"><%=TabStor.getListVal(TabStor.tab_channel, "code='"+channel+"'", "name") %></td>
</tr>
<tr>
<td align="right" nowrap class="nrbgc1"><strong>账户渠道</strong></td>
<td colspan="3" nowrap class="nrbgc1"><%=TabStor.getListVal(TabStor.tab_platform, "code='"+platform+"'", "name") %></td>
</tr>
<tr>
<td align="right" nowrap class="nrbgc1"><strong>当前登录的游戏服</strong></td>
<td colspan="3" nowrap class="nrbgc1"><%=ServerBAC.getInstance().getNameById(Tools.str2int(serverid))+"("+serverid+")" %></td>
</tr>
<tr>
<td align="right" nowrap class="nrbgc1"><strong>当前登录的角色</strong></td>
<td colspan="3" nowrap class="nrbgc1"><%=PlayerBAC.getInstance().getNameById(Tools.str2int(playerid))+"("+playerid+")" %></td>
</tr>
<tr>
<td width="10%" align="right" nowrap class="nrbgc1"><strong>帐号可用</strong></td>
<td colspan="3" class="nrbgc1"><%=enable==1?"可用":"禁用" %></td>
</tr>
<tr>
<td align="right" nowrap class="nrbgc1"><strong>开发人员</strong></td>
<td colspan="3" class="nrbgc1">
	<select name="devuser" id="devuser">
	<option value="1">是</option>
	<option value="0">否</option>
	</select>
</td>
</tr>
<tr>
<td align="right" nowrap class="nrbgc1"><strong>强制登录</strong></td>
<td colspan="3" class="nrbgc1">
	<select name="enforcementlogin" id="enforcementlogin">
	<option value="1">是</option>
	<option value="0">否</option>
	</select>
</td>
</tr>

<tr>
  <td align="right" nowrap class="nrbgc1"><strong>注册时间</strong></td>
  <td class="nrbgc1"><%=regtime%></td>
  <td align="right" nowrap class="nrbgc1"><strong>登录时间</strong></td>
  <td class="nrbgc1"><%=logintime%></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>在线</strong></td>
  <td class="nrbgc1"><%=onlinestate%></td>
  <td align="right" nowrap class="nrbgc1"><strong>ip</strong></td>
  <td class="nrbgc1"><%=ip%></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>游戏服务器</strong></td>
  <td class="nrbgc1"><%=serverid%></td>
  <td align="right" nowrap class="nrbgc1"><strong>游戏角色id</strong></td>
  <td class="nrbgc1"><%=playerid%></td>
</tr>

<tr>
  <td align="right" nowrap class="nrbgc1"><strong>分辨率</strong></td>
  <td class="nrbgc1"><%=resolution%></td>
  <td align="right" nowrap class="nrbgc1"><strong>移动运营商</strong></td>
  <td class="nrbgc1"><%=netname%></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>网络类型</strong></td>
  <td class="nrbgc1"><%=nettype%></td>
  <td align="right" nowrap class="nrbgc1"><strong>使用WIFI</strong></td>
  <td class="nrbgc1"><%=wifi%></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>手机串号</strong></td>
  <td class="nrbgc1"><%=imei%></td>
  <td align="right" nowrap class="nrbgc1"><strong>MAC地址</strong></td>
  <td class="nrbgc1"><%=mac%></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>手机厂商</strong></td>
  <td class="nrbgc1"><%=phonevendor%></td>
  <td align="right" nowrap class="nrbgc1"><strong>手机型号</strong></td>
  <td class="nrbgc1"><%=phonemodel%></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>操作系统sdk级别</strong></td>
  <td class="nrbgc1"><%=phonesdklv%></td>
  <td align="right" nowrap class="nrbgc1"><strong>操作系统版本号</strong></td>
  <td class="nrbgc1"><%=phonesdkver%></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>手机剩余内存</strong></td>
  <td class="nrbgc1"><%=phonefreemem%>MB</td>
  <td align="right" nowrap class="nrbgc1"><strong>手机总共内存</strong></td>
  <td class="nrbgc1"><%=phonetotalmem%>MB</td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>手机号</strong></td>
  <td class="nrbgc1"><%=phonenum%></td>
  <td align="right" nowrap class="nrbgc1">&nbsp;</td>
  <td class="nrbgc1">&nbsp;</td>
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