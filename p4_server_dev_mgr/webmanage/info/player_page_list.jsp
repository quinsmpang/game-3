<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="玩家管理";
String perm="角色管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("角色名","tab_player.name",new String[]{"等于", "包含"});
tmpWhereColumn.add("角色id","tab_player.id",new String[]{"等于"});
tmpWhereColumn.add("用户名","tab_user.username",new String[]{"等于", "包含"});
tmpWhereColumn.add("用户id","tab_user.id",new String[]{"等于"});
WhereColumn[] theWhereColumn=tmpWhereColumn.getWhereColumns();

OrderColumn tmpOrderColumn=new OrderColumn();
tmpOrderColumn.add("录入顺序","a.id");

OrderColumn[] theOrderColumn=tmpOrderColumn.getOrderColumns();
%>
<%@include file="inc_list_getparameter.jsp"%>
<%//解析数据

String serverId = Tools.strNull(request.getParameter("serverId"));
String channel = Tools.strNull(request.getParameter("channel"));
String onlinestate = Tools.strNull(request.getParameter("onlinestate"));
String vsid = Tools.strNull(request.getParameter("vsid"));

JSONObject xml = PlayerBAC.getInstance().getPageList(pageContext);
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script>
var allValue=new Object();
allValue.serverId="<%=serverId%>";
allValue.channel="<%=channel%>";
allValue.onlinestate="<%=onlinestate%>";
allValue.vsid="<%=vsid%>";
</script>
</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="">
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
                    <td nowrap background="../images/tab_midbak.gif">角色管理</td>
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
                  <table width="90%" border="0" cellspacing="1" cellpadding="2">
                    <tr> 
                      <td>                        
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td>&nbsp;</td>
                            </tr>
                          </table>
                          <%@ include file="inc_list_top.jsp"%>
                          <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                            <tr align="center" class="listtopbgc"> 
                              <td >No.</td>
                              <td align="center" nowrap>
								<select name="channel" id="channel"
									onChange="document.forms[0].submit()">
									<option value="">选择渠道</option>
									<%
									DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
									while(channelRs.next()) {
									%>
										<option value="<%=channelRs.getString("code")%>">
											<%=channelRs.getString("name")%>(<%=channelRs.getString("code")%>)</option>
										<%
											}
										%>
									</select></td>
                              <td align="center" nowrap>
                                <select name="serverId" id="serverId" onChange="document.forms[0].submit()">
                                  <option value="">选择游戏服</option>
                                  <%
                                  DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
								  while(serverRs.next()){
								  %>
                                  <option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%>(<%=serverRs.getInt("id")%>)</option>
                                  <%
                                  }
                                  %>
                                </select></td>
									  <td align="center" nowrap><select name="vsid" id="vsid" onChange="document.forms[0].submit()">
                                        <option value="">选择虚拟游戏服</option>
                                        <%
                                        DBPsRs channelServerRs = DBPool.getInst().pQueryS(ServerBAC.tab_channel_server);
										while(channelServerRs.next()) {
										%>
                                        <option value="<%=channelServerRs.getInt("vsid")%>"><%=channelServerRs.getString("servername")%>(<%=channelServerRs.getInt("vsid")%>)</option>
                                        <%
                                        }
                                        %>
                                      </select></td>
									  <td align="center" nowrap>角色id</td>
									  <td align="center" nowrap>角色名</td>
									<td align="center" nowrap>性别</td>
									<td align="center" nowrap>用户id</td>
									<td align="center" nowrap>用户名</td>
									<td align="center" nowrap>等级</td>
									<td align="center" nowrap>职业</td>
									<td align="center" nowrap>
									<select name="onlinestate" id="onlinestate" onChange="document.forms[0].submit()">
									  <option value="">在线状态</option>
									  <option value="1">在线</option>
									  <option value="0">不在线</option>
									  </select>
									</td>
									<td align="center" nowrap>登录时间</td>
									<td align="center" nowrap>建立时间</td>
                            </tr>
                            <%							
							
							int count = 0;
							JSONArray list=null;
							if(xml!=null)
							{
								count=(xml.optInt("rsPageNO")-1)*ToolFunc.str2int(rpp)+1;
								list = xml.optJSONArray("list");
							}
							
							for(int i=0;list!=null && i<list.length();i++)
							{
								JSONObject line = (JSONObject)list.opt(i);
								int id = line.optInt("id");			
							%>
                            <tr class="nrbgc1">
                              <td align="center" nowrap><%=count++%></td>
                              <td align="center" nowrap><%=TabStor.getListVal(TabStor.tab_channel, "code='"+line.optString("channel")+"'", "name") %>(<%=line.optString("channel")%>)</td>
                              <td align="center" nowrap><%=TabStor.getListVal(ServerBAC.tab_server, "id="+line.optString("serverid"), "name")%>(<%=line.optInt("serverid")%>)</td>
                              <td align="center" nowrap><%=TabStor.getListVal(ServerBAC.tab_channel_server, "vsid="+line.optString("vsid"), "servername")%>(<%=line.optInt("vsid")%>)</td>
                              <td align="center" nowrap><%=line.optInt("id")%></td>
                              <td align="center" nowrap><%=line.optString("name")%></td>
							<td align="center" nowrap><%=line.optInt("sex")==1?"男":"女"%></td>
							<td align="center" nowrap><%=line.optInt("userid")%></td>
							<td align="center" nowrap><%=line.optString("username")%></td>
							<td align="center" nowrap><%=line.optInt("lv")%></td>
							<td align="center" nowrap><%=TabStor.getListVal(TabStor.tab_role, "num="+line.optInt("num"), "name")+"("+line.optInt("num")+")"%></td>
							<td align="center" nowrap><%=line.optInt("onlinestate")==1?"<font color=\"#006633\">在线</font>":"<font color=\"#FF0000\">不在线</font>"%></td>
							<td align="center" nowrap><%=line.optString("logintime")%></td>
							<td align="center" nowrap><%=line.optString("savetime")%></td>
							</tr>
                            <%
				}								
				%>
                          </table>
                          <%@ include file="inc_list_bottom.jsp"%>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right"><input name="delid" type="hidden" id="delid">
                                &nbsp;</td>
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
<script>
autoChoose(allValue);
</script>
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
</html>
