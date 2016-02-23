<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="基本数据";
String perm="服务器管理";
%>
<%
boolean root=false;
if(userObj!=null)
{ 
 root=userObj.optBoolean("root"); 
}

ServerBAC serverBAC = ServerBAC.getInstance();
PlayerBAC playerBAC = PlayerBAC.getInstance();
UserBAC userBAC = UserBAC.getInstance();
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<script src="../js/meizzDate.js"></script>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>

<script>
function del(id)
{
	if(confirm("确认删除吗？"))
	{
		wait();		
		document.getElementById("hiddenFrame").src="server_del.jsp?id=" + id;
	}
}
function modify(id)
{
openWindow("server_edit.jsp?id=" + id,"modify",500,500,true,true);
}
function add()
{
openWindow("server_edit.jsp","add",500,500,true,true);
}
function startTcpServer()
{
	wait();		
	document.getElementById("hiddenFrame").src="server_tcp_operate.jsp?act=start";	
}
function stopTcpServer()
{
	if(confirm("确认停止TCP监听服务吗？"))
	{
		wait();		
		document.getElementById("hiddenFrame").src="server_tcp_operate.jsp?act=stop";
	}	
}
</script>

<link href="../css/style1.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EFEFEF">

<form name="form1" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td > 
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif" width="6" height="22"></td>
                    <td nowrap background="../images/tab_midbak.gif">游戏服务器管理</td>
                    <td><img src="../images/tab_right.gif" width="6" height="22"></td>
                  </tr>
                </table></td>
                <td valign="bottom" > 
                  <table width="100%" border="0" cellspacing="0" cellpadding="0" height="22">
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
                  </table>
                </td>
              </tr>
            </table>
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td rowspan="2" bgcolor="#FFFFFF" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
                <td valign="top" align="center"> 
                  <table width="95%" border="0" cellspacing="1" cellpadding="2">
							<tr>
								<td>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td>&nbsp;</td>
									</tr>
								</table>
								
								<table width="100%" border="0" cellpadding="2" cellspacing="1"
									class="tbbgc1">
									<tr class="listtopbgc">
										<td width="10" align="center" nowrap>No.</td>
										<td align="center" nowrap>id</td>
										<td align="center" nowrap>服务器名</td>
										<td align="center" nowrap>玩家数</td>
										<td align="center" nowrap>在线数</td>
										<td align="center" nowrap>在线人数限制</td>
										<td align="center" nowrap>状态</td>
										<td align="center" nowrap>标签</td>
										<td align="center" nowrap>http连接地址</td>
										<td align="center" nowrap>tcp连接地址</td>
										<td align="center" nowrap>下次团购刷新时间</td>
									</tr>
									
									<%
										int num = 1;
										JSONObject serverobj = serverBAC.getServerData();
										int serverId = serverobj.optInt("id");
										int maxplayer = serverobj.optInt("maxplayer");
										int playerAmount = serverobj.optInt("pam");
										int onlineAmount = serverobj.optInt("pamol");
									%>
									<tr class="nrbgc1">
										<td align="center" nowrap><%=num++%></td>
										<td align="center" nowrap><%=serverId%></td>
										<td align="center" nowrap><%=serverobj.optString("name")%></td>
										<td align="center" nowrap><%=playerAmount%></td>
										<td align="center" nowrap><%=onlineAmount%></td>
										<td align="center" nowrap><%=maxplayer%></td>
										<td align="center" nowrap><%if(serverobj.optInt("state")==0){%><font color="#336633">正常</font><%}else if(serverobj.optInt("state")==1){%><font color="#FF0000">维护</font><%}%></td>
										<td align="center" nowrap><%if(serverobj.optInt("tip")==0){%>无<%}else if(serverobj.optInt("tip")==1){%>火爆<%}else if(serverobj.optInt("tip")==2){%>推荐<%}%></td>
										<td align="center" nowrap><%=serverobj.optString("http")%></td>
										<td align="center" nowrap><%=serverobj.optString("tcp")%></td>
										<td align="center" nowrap><%=ConfFile.getFileValue(MallGrouponAcountTT.TIME_MALL_GROUPON_ACCOUNT)%></td>
									</tr>
								</table>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
                                  <tr>
                                    <td></td>
                                  </tr>
                                  <tr>
                                    <td align="right">&nbsp;</td>
                                  </tr>
                                </table>								
								<table width="100%" border="0" cellspacing="1" cellpadding="2">									
									
									<tr>
									  <td><table border="0" cellspacing="1" cellpadding="2">
                                        <tr>
                                          <td align="right"><strong>处理器数：</strong></td>
                                          <td><%=Runtime.getRuntime().availableProcessors()%>个</td>
                                        </tr>
                                        <tr>
                                          <td align="right"><strong>Java剩余内存：</strong></td>
                                          <td align="right"><%=Runtime.getRuntime().freeMemory()%>字节 | <%=Tools.formatMemory(Runtime.getRuntime().freeMemory())%></td>
                                        </tr>
                                        <tr>
                                          <td align="right"><strong>Java总共内存：</strong></td>
                                          <td align="right"><%=Runtime.getRuntime().totalMemory()%>字节 | <%=Tools.formatMemory(Runtime.getRuntime().totalMemory())%></td>
                                        </tr>
                                        <tr>
                                          <td align="right"><strong>Java最大内存：</strong></td>
                                          <td align="right"><%=Runtime.getRuntime().maxMemory()%>字节 | <%=Tools.formatMemory(Runtime.getRuntime().maxMemory())%></td>
                                        </tr>
                                        <tr>
                                          <td align="right" nowrap><strong>服务器当前时间：</strong></td>
                                          <td align="left"><font color=red><%=Tools.getCurrentDateTimeStr()%></font></td>
                                        </tr>
                                      </table></td>
							      </tr>
									
									<tr>
									  <td><table border="0" cellspacing="1" cellpadding="2">
                                        <tr>
										<td nowrap><strong>TCP监听端口：</strong><%=Conf.socket_port%></td>
                                          <td nowrap><strong>当前TCP连接数：</strong><%=SocketServer.getInstance()!=null?SocketServer.getInstance().getOnlinePlayerAmount():0%></td>
                                          <td nowrap><strong>TCP监听状态：</strong>
                                              <%if(SocketServer.getInstance()!=null){
										  ReturnValue rv = SocketServer.getInstance().getRunState();
										if(rv.success){%>
                                            <font color="#006633">监听中</font>
                                            <%}else{%>
                                            <font color="#FF0000">已停止</font>
                                            <%}}else{%>
                                            <font color="#006699">监听服务对象不存在</font>
                                          <%}%></td>
                                          <td nowrap><strong>TCP监听服务开关：</strong>
                                              <%if(SocketServer.getInstance()!=null){
										  ReturnValue rv = SocketServer.getInstance().getRunState();
										if(rv.success){%>
                                            <a href="javascript:stopTcpServer()"><font color="#FF0000"><u>停止监听服务</u></font></a>
                                            <%}else{%>
                                            <a href="javascript:startTcpServer()"><font color="#006633"><u>启动监听服务</u></font></a>
                                            <%}}else{%>
                                            <font color="#006699">监听服务对象不存在</font>
                                            <%}%></td>
                                        </tr>
                                      </table></td>
								  </tr>
									<tr>
									  <td><strong>数据库使用中连接数：</strong><%=ServerConfig.getDataBase().getNumActive()%></td>
							      </tr>
									<tr>
									  <td><strong>数据库待机连接数：</strong><%=ServerConfig.getDataBase().getNumIdle()%></td>
							      </tr>
									<tr>
									  <td><strong>当前处理指令ACT：</strong><font color="#FF0000"><%=Conf.currentAct%></font> <strong>处理时长：</strong><font color="#FF0000"><%=Conf.currentActTime%></font>毫秒</td>
								  </tr>
									<tr>
									  <td><table width="100%" border="0" cellspacing="1" cellpadding="2">
                                          <tr>
                                            <td colspan="2" align="center" class="listtopbgc">xml配置文件</td>
                                          </tr>
                                          <tr>
                                            <td align="center"><strong>server.xml</strong></td>
                                            <td><%
												byte[] xmlByte = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/conf/server.xml");
												String xmlStr = new String(xmlByte,"UTF-8");												
											%><%=Tools.replace(Tools.htmlEncode(xmlStr),"\n","<br/>")%></td>
                                          </tr>
                                          <tr>
                                            <td height="1" colspan="2" align="center" bgcolor="#000000"></td>
                                          </tr>
                                          <tr>
                                            <td align="center"><strong>mserver.xml</strong></td>
                                            <td><%
												xmlByte = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/conf/mserver.xml");
												xmlStr = new String(xmlByte,"UTF-8");												
											%><%=Tools.replace(Tools.htmlEncode(xmlStr),"\n","<br/>")%></td>
                                          </tr>
										  <tr>
                                            <td height="1" colspan="2" align="center" bgcolor="#000000"></td>
                                          </tr>
                                          <tr>
                                            <td align="center"><strong>gameconf.xml</strong></td>
                                            <td><%
												xmlByte = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/conf/gameconf.xml");
												xmlStr = new String(xmlByte,"UTF-8");												
											%><%=Tools.replace(Tools.htmlEncode(xmlStr),"\n","<br/>")%></td>
                                          </tr>
										  <tr>
                                            <td height="1" colspan="2" align="center" bgcolor="#000000"></td>
                                          </tr>
										  <tr>
                                            <td align="center"><strong>poker_req.xml</strong></td>
                                            <td><%
												xmlByte = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/conf/poker_req.xml");
												xmlStr = new String(xmlByte,"UTF-8");												
											%><%=Tools.replace(Tools.htmlEncode(xmlStr),"\n","<br/>")%></td>
                                          </tr>
										  <tr>
                                            <td height="1" colspan="2" align="center" bgcolor="#000000"></td>
                                          </tr>
										  <%if(root){%>
                                          <tr>
                                            <td align="center"><strong>db.xm</strong></td>
                                            <td><%
												xmlByte = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/conf/db.xm");
												xmlByte = Tools.decodeBin(xmlByte);
												xmlStr = new String(xmlByte,"UTF-8");												
											%><%=Tools.replace(Tools.htmlEncode(xmlStr),"\n","<br/>")%></td>
                                          </tr>
										  <%}%>
                                        </table></td>
							      </tr>
								</table>
								</td>
							</tr>

				</table>			
				
                </td>
                <td rowspan="2" bgcolor="#848284" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
              </tr>
              <tr> 
                <td bgcolor="#848284" height="1"></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</form>
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
</html>
