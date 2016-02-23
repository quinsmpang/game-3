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
function modify(id)
{
openWindow("server_edit.jsp?id=" + id,"modify",550,590,true,true);
}
function add()
{
openWindow("server_edit.jsp","add",550,590,true,true);
}
function exportTxt()
{
	location.href="server_export_txt.jsp";
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
								<td><table width="100%" border="0" cellspacing="1" cellpadding="2">
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
									  <td><strong>数据库使用中连接数：</strong><%=ServerConfig.getDataBase().getNumActive()%></td>
								  </tr>
									<tr>
									  <td><strong>数据库待机连接数：</strong><%=ServerConfig.getDataBase().getNumIdle()%></td>
								  </tr>
								  <%if(root){
								  File file = null;
								  String stdLogPathHf="/home/gcweb/usr/local/hfresinlogin/log/all.log";
								  String jvmLogPathHf="/home/gcweb/usr/local/hfresinlogin/log/jvm-default.log";
								  String stdLogPath="/home/gcweb/usr/local/bkresinlogin/log/all.log";
								  String jvmLogPath="/home/gcweb/usr/local/bkresinlogin/log/jvm-default.log";
								  %>									
									<tr>
									  <td><%if(Conf.ms_url.equals("http://hflogin.xm.pook.com/xianmo_user/")){%><strong>下载联运服resin标准日志：</strong><a href="../../download.do?path=<%=URLEncoder.encode(stdLogPathHf,"UTF-8")%>"><%=stdLogPathHf%></a> <%file = new File(stdLogPathHf);%><%=file.length()%>字节　<strong>下载联运服resinJVM日志：</strong><a href="../../download.do?path=<%=URLEncoder.encode(jvmLogPathHf,"UTF-8")%>"><%=jvmLogPathHf%></a> <%file = new File(jvmLogPathHf);%><%=file.length()%>字节<%}else if(Conf.ms_url.equals("http://xmlogin.pook.com/xianmo_user/")){%><strong>下载体验服resin标准日志：</strong><a href="../../download.do?path=<%=URLEncoder.encode(stdLogPath,"UTF-8")%>"><%=stdLogPath%></a> <%file = new File(stdLogPath);%><%=file.length()%>字节　<strong>下载体验服resinJVM日志：</strong><a href="../../download.do?path=<%=URLEncoder.encode(jvmLogPath,"UTF-8")%>"><%=jvmLogPath%></a> <%file = new File(jvmLogPath);%><%=file.length()%>字节<%}%></td>
								  </tr>									
								  <%}%>
									<tr>
									  <td><table width="97%" border="0" cellspacing="2" cellpadding="0">
                                          <tr>
                                            <td colspan="2" align="center" class="listtopbgc">配置文件</td>
                                          </tr>
                                          <tr>
                                            <td align="center"><strong>download.xml</strong></td>
                                            <td><%
												byte[] xmlByte = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/conf/download.xml");
												String xmlStr = new String(xmlByte,"UTF-8");												
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
                                            <td align="center"><strong>memcached.properties</strong></td>
                                            <td><%
												xmlByte = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/conf/memcached.properties");
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
<script>
autoChoose(allValue);
</script>
</body>
</html>
