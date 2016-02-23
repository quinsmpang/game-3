<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="服务器管理";
String perm="游戏服务器";
%>
<%
boolean root = false;
if(userObj!=null) {
	root = userObj.optBoolean("root");
}

int showOnline = Tools.str2int(request.getParameter("showOnline"));
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
var allValue=new Object();
allValue.showOnline="<%=showOnline%>";
</script>
<script>
function del(id)
{
	if(confirm("确认删除吗？"))
	{
		wait();		
		document.getElementById("hiddenFrame").src="server_del.jsp?id=" + id;
	}
}
function clear(serverName,serverId)
{
	if(confirm("确认删除"+serverName+"的全部玩家数据吗？"))
	{
		wait();		
		//alert(serverName);
		//alert(serverId);
		location.href="server_clearAllData.jsp?serverId=" + serverId+"&serverName="+serverName;
		//document.getElementById("hiddenFrame").src="server_clearAllData.jsp?serverId=" + serverId+"&serverName="+serverName;
	}
}
function importopenserveracti(serverName,serverId)
{
	if(confirm("确认向“"+serverName+"”导入开服活动吗？"))
	{
		wait();		
		//alert(serverName);
		//alert(serverId);
		location.href="server_importopenserveracti.jsp?serverId=" + serverId;
		//document.getElementById("hiddenFrame").src="server_clearAllData.jsp?serverId=" + serverId+"&serverName="+serverName;
	}
}
function createrobot(serverName,serverId)
{
	if(confirm("确认向“"+serverName+"”创建机器人吗？"))
	{
		wait();
		//alert(serverName);
		//alert(serverId);
		location.href="server_createrobot.jsp?serverId=" + serverId;
		//document.getElementById("hiddenFrame").src="server_clearAllData.jsp?serverId=" + serverId+"&serverName="+serverName;
	}
}
function openready(serverName,serverId)
{
	if(confirm("确认对“"+serverName+"”执行开服准备吗？"))
	{
		wait();		
		//alert(serverName);
		//alert(serverId);
		location.href="server_openready.jsp?serverId=" + serverId;
		//document.getElementById("hiddenFrame").src="server_clearAllData.jsp?serverId=" + serverId+"&serverName="+serverName;
	}
}
function modify(id)
{
	openWindow("server_edit.jsp?id=" + id,"modify",780,590,true,true);
}
function add()
{
	openWindow("server_edit.jsp","add",780,590,true,true);
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
										<td>
										<label>
<input name="showOnline" type="checkbox" id="showOnline" value="1" onClick="document.forms[0].submit()">显示玩家数和在线数
										</label>
									    </td>
									</tr>
								</table>
								
								<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
									<tr class="listtopbgc">
										<td width="10" align="center" nowrap>No.</td>
										<td align="center" nowrap>id</td>
										<td align="center" nowrap>服务器名</td>
										<%
										if(showOnline==1){
										%>
										<td align="center" nowrap>玩家数</td>
										<td align="center" nowrap>在线数</td>
										<%
										}
										%>
										<td align="center" nowrap>标签</td>
										<td align="center" nowrap>状态</td>
										<td align="center" nowrap>http连接地址</td>
										<td align="center" nowrap>tcp连接地址</td>
										<td align="center" nowrap>开服时间</td>
										<td align="center" nowrap>资源等级</td>
										<td align="center" nowrap>启用</td>
										<td align="center" nowrap>初始化</td>
										<td align="center" nowrap>开服准备</td>
										<td align="center" nowrap>导入开服活动</td>
										<td align="center" nowrap>创建机器人</td>
										<td align="center" nowrap>操作</td>
									</tr>
									
									<%									
									DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
									JSONObject serverplayerObj = null;
									if(showOnline == 1){
										serverplayerObj = ServerBAC.getInstance().getServerPlayerAmount();
									}
									while(serverRs.next()){
										int serverId = serverRs.getInt("id");
										int playerAmount = 0;
										int onlineAmount = 0;
										if(showOnline==1){
											playerAmount = serverplayerObj.optInt("t"+serverId);
											onlineAmount = serverplayerObj.optInt("o"+serverId);
										}
										int tip = serverRs.getInt("tip");
									%>
									<tr class="nrbgc1">
										<td align="center" nowrap><%=serverRs.getRow()%></td>
										<td align="center" nowrap><%=serverId%></td>
										<td align="center" nowrap><%=serverRs.getString("name")%></td>
										<%
										if(showOnline==1){
										%>
										<td align="center" nowrap><%=playerAmount%></td>
										<td align="center" nowrap><%=onlineAmount%></td>
										<%
										}
										%>
										<td align="center" nowrap><%if(tip==1){%>推荐<%}else if(tip==2){%>火爆<%}%></td>
										<td align="center" nowrap><%if(serverRs.getInt("state")==0){%><font color="#336633">正常</font><%}else if(serverRs.getInt("state")==1){%><font color="#FF0000">维护</font><%}%></td>
										<td align="center" nowrap><%=serverRs.getString("http")%></td>
										<td align="center" nowrap><%=serverRs.getString("tcp")%></td>
										<td align="center" nowrap><%=serverRs.getString("opentime")%></td>
										<td align="center" nowrap><%=serverRs.getString("reslv")%></td>
										<td align="center" nowrap><%=serverRs.getInt("usestate")==1?"<font color='#336633'>已启用":"<font color='#FF0000'>"+serverRs.getString("usenote")%></font></td>
										<td align="center" nowrap>
										<%
										if(!MyTools.checkSysTimeBeyondSqlDate(serverRs.getTime("opentime")) && playerAmount>0){%>
											<a href="javascript:clear('<%=serverRs.getString("name")%>',<%=serverId%>)">清除玩家数据</a>
										<%}else{%>
											<font color="#999999">清除玩家数据</font>
										<%}%>
										</td>
										<td align="center" nowrap>
										<a href="javascript:openready('<%=serverRs.getString("name")%>',<%=serverId%>)">开服准备</a>
										</td>
										<td align="center" nowrap>
										<a href="javascript:importopenserveracti('<%=serverRs.getString("name")%>',<%=serverId%>)">导入开服活动</a>
										</td>
										<td align="center" nowrap>
										<a href="javascript:createrobot('<%=serverRs.getString("name")%>',<%=serverId%>)">创建机器人</a>
										</td>
										<td align="center" nowrap>
										<img src="../images/icon_modify.gif" alt="修改" align="absmiddle" style="cursor: hand" onClick="modify(<%=serverRs.getInt("id")%>)">
										<img src="../images/icon_del2.gif" alt="删除" align="absmiddle" style="cursor: hand" onClick="del(<%=serverRs.getInt("id")%>)">
										</td>
									</tr>
									<%
									}
									%>
								</table>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
                                  <tr>
                                    <td align="right">
                                    <table width="50" border="0" cellspacing="0" cellpadding="2">
                                        <tr>
                                          <td height="21" nowrap class="btntd" style="cursor: hand"
													onMouseDown="this.className='btntd_mousedown'"
													onMouseUp="this.className='btntd'"
													onMouseOut="this.className='btntd'"
													onselectstart="return false" onClick="add()"><img
													src="../images/icon_adddepart.gif" width="16" height="16"
													align="absmiddle"> 添加</td>
                                        </tr>
                                    </table>
                                    </td>
                                  </tr>
                                </table>								
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
								  <tr>
								  <td>
								  <table>
								  <tr>
                                    <td align="left" nowrap><strong>服务器当前时间：</strong></td>
                                    <td align="left"><font color=red><%=MyTools.getTimeStr()%></font></td>
                                  </tr>
                                  <tr>
									  <td align="left"><strong>下载游戏配置表打包文件：</strong></td>
									  <td align="left"><a href="server_download_data.jsp">res.zip</a></td>
								  </tr>
								  <%if(root){%>
                                  <tr>
                                    <td align="left"><strong>处理器数：</strong></td>
                                    <td align="left"><%=Runtime.getRuntime().availableProcessors()%>个</td>
                                  </tr>
                                  <tr>
                                    <td align="left"><strong>Java剩余内存：</strong></td>
                                    <td align="left"><%=Runtime.getRuntime().freeMemory()%>字节 | <%=Tools.formatMemory(Runtime.getRuntime().freeMemory())%></td>
                                  </tr>
                                  <tr>
                                    <td align="left"><strong>Java总共内存：</strong></td>
                                    <td align="left"><%=Runtime.getRuntime().totalMemory()%>字节 | <%=Tools.formatMemory(Runtime.getRuntime().totalMemory())%></td>
                                  </tr>
                                  <tr>
                                    <td align="left"><strong>Java最大内存：</strong></td>
                                    <td align="left"><%=Runtime.getRuntime().maxMemory()%>字节 | <%=Tools.formatMemory(Runtime.getRuntime().maxMemory())%></td>
                                  </tr>
								  <tr>
									  <td><strong>数据库使用中连接数：</strong></td>
									  <td><%=ServerConfig.getDataBase().getNumActive()%></td>
								  </tr>
								  <tr>
									  <td><strong>数据库待机连接数：</strong></td>
									  <td><%=ServerConfig.getDataBase().getNumIdle()%></td>
								  </tr>
								  <%}%>
								  </table>
								  </td>
								  </tr>
									<tr>
									  <td colspan="2">
									  <%if(root){%>
									  <table width="97%" border="0" cellspacing="2" cellpadding="0">
                                          <tr>
                                            <td colspan="2" align="center" class="listtopbgc">xml配置文件</td>
                                          </tr>
										  <tr>
                                            <td align="center"><strong>conf.xml</strong></td>
                                            <td>
                                            <%
												byte[] xmlByte = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/conf/conf.xml");
												String xmlStr = new String(xmlByte,"UTF-8");
											%>
											<%=Tools.replace(Tools.htmlEncode(xmlStr),"\n","<br/>")%></td>
                                          </tr>										  
                                          <tr>
                                            <td height="1" colspan="2" align="center" bgcolor="#000000"></td>
                                          </tr>
										  <tr>
                                            <td align="center"><strong>download.xml</strong></td>
                                            <td>
                                            <%
												xmlByte = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/conf/download.xml");
												xmlStr = new String(xmlByte,"UTF-8");
											%>
											<%=Tools.replace(Tools.htmlEncode(xmlStr),"\n","<br/>")%></td>
                                          </tr>										  
                                          <tr>
                                            <td height="1" colspan="2" align="center" bgcolor="#000000"></td>
                                          </tr>
                                          <tr>
                                            <td align="center"><strong>db.xm</strong></td>
                                            <td>
                                            <%
												xmlByte = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/conf/db.xm");
												xmlByte = Tools.decodeBin(xmlByte);
												xmlStr = new String(xmlByte,"UTF-8");												
											%>
											<%=Tools.replace(Tools.htmlEncode(xmlStr),"\n","<br/>")%></td>
                                          </tr>
                                        </table>
                                        <%}%>
                                     </td>
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
<script>
autoChoose(allValue);
</script>
</body>
</html>
