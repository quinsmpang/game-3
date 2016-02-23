<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="玩家管理";
String perm="玩家处理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
String playerName = Tools.strNull(request.getParameter("playerName"));
int serverId = Tools.str2int(request.getParameter("serverId"));
String columnname = request.getParameter("columnname");
JSONObject plaRs = null;
if(serverId!=0 && !playerName.equals("")) {
	plaRs = PlayerBAC.getInstance().getJsonObj("serverid="+serverId+" and name='"+playerName+"'");
}
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script src="../js/meizzDate.js"></script>
<script language="javascript" type="text/javascript" src="../My97DatePicker/WdatePicker.js"></script>
<script>
var allValue = new Object();
allValue.playerName="<%=playerName%>";
allValue.serverId="<%=serverId%>";
</script>
<script>
function checkForm()
{
	document.forms[0].action="";
	document.forms[0].target="";
	document.forms[0].submit();
}
function operate(act)
{
	document.forms[0].target="hiddenFrame";
	document.forms[0].action="player_change_act.jsp";
	document.forms[0].submit();
	document.forms[0].action="";
	document.forms[0].target="";
}
function showLog(serverId,playerId,column)
{
	openWindow("player_change_log.jsp?serverId="+serverId+"&playerId=" + playerId+"&column="+column,"查看更改历史记录",500,500,true,true);
}
</script>
</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="" enctype="application/x-www-form-urlencoded">
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
                    <td nowrap background="../images/tab_midbak.gif">玩家处理</td>
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
                      <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td><table width="100%" border="0" cellpadding="2" cellspacing="1">                              
                              <tr>
                                <td nowrap>
                                	游戏服务器：
									<select name="serverId" id="serverId">
									<option value="0">选择</option>
									  <%
									  DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server, "usestate=1");
									  while(serverRs.next()) {
									  %>
									  <option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%></option>
									  <%
									  }
									  %>
									 </select>
                                  	玩家名：
                                  <input name="playerName" type="text" class="input1" id="playerName">
                                  <input type="button" name="Button2" value="查询" onClick="checkForm()"></td>
                                </tr>
                              
                          </table>
						  	<%
							if(plaRs != null){
							  	DBPsRs pctRs = DBPool.getInst().pQueryS(TabStor.tab_player_change_type);
							  	int playerid = plaRs.getInt("id");
							%>
                            <input name="playerId" type="hidden" id="playerId" value="<%=playerid%>">
                            <table width="100%" border="0" cellpadding="2" cellspacing="1">
                              <%
							  while(pctRs.next()) {
							  %>
                              <tr>
                              <%
                              	String tabname = pctRs.getString("tabname");
                              	String colname = pctRs.getString("columnname");
                              	String keycol = tabname.equals("tab_player")?"id":"playerid";
                                String value = TabStor.getDataVal(tabname, keycol+"="+playerid, colname);
                              %>
<td width="15%" nowrap class="nrbgc1">
<strong><%=pctRs.getString("name")%>：</strong><%=value%>
</td>
<td width="240" valign="middle" nowrap class="nrbgc1">
<a href="javascript:showLog(<%=serverId%>,<%=playerid%>,'<%=pctRs.getString("columnname")%>')">更改历史记录</a>(<font color="#FF0000"><%=PlayerChangeLogBAC.getInstance().getCount("serverid="+serverId+" and playerid="+playerid+" and columnname='"+pctRs.getString("columnname")+"'")%></font>)
</td>
</tr>
								<%
								}
								%>
                              <tr>
                                <td class="nrbgc1">&nbsp;</td>
                                <td class="nrbgc1">&nbsp;</td>
                               </tr>
								
								<tr>
                                <td colspan="2" nowrap class="nrbgc1">
								
								<strong>选择调整项：</strong>
								<select name="columnname" id="columnname">
								  <%
								  	pctRs.beforeFirst();
									while(pctRs.next()) {
									String noteStr = null;
									if(pctRs.getInt("changetype") == 1){
										noteStr = "累加值";
									} else 
									if(pctRs.getInt("changetype") == 2){
										noteStr = "设置值";
									} else 
									if(pctRs.getInt("changetype") == 3){
										noteStr = "设置时间";
									}
									%>
								  <option value="<%=pctRs.getString("columnname")%>"><%=pctRs.getString("name")+"（"+noteStr+"）"%></option>
								  <%											
									}
								  %>
								  </select>
								<strong>调整值：</strong>
								<input name="changeValue" type="text" id="changeValue">
                                <strong>调整理由：</strong>
                                <input name="reason" type="text" id="reason" size="30">
                                <input name="btnChange" type="button" id="btnChange" value="提交调整" onClick="operate()"></td>
                                
								</tr>		
                            </table>
                            <br>
                            <%}%>
                            <br></td>
                        </tr>
                      </table>
                        </td>
                    </tr>
                  </table>
                </td>
                <td rowspan="2" bgcolor="#848284" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
              </tr>
              <tr> 
                <td    bgcolor="#848284" height="1"> </td>
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
<script>
<%
if(!playerName.equals("") && plaRs==null){
%>
alert("在\"<%=ServerBAC.getInstance().getValue("name","id="+serverId)%>\"没有查到叫\"<%=playerName%>\"的玩家");
<%
}
%>
</script>
</body>

</html>
