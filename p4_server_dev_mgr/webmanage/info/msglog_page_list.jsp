<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="客服管理";
String perm="玩家聊天";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn = new WhereColumn();
tmpWhereColumn.add("角色名", "playerid", new String[]{"等于"});
tmpWhereColumn.add("帮派名", "factionid", new String[]{"等于"});
tmpWhereColumn.add("内容", "content", new String[]{"包含"});
WhereColumn[] theWhereColumn = tmpWhereColumn.getWhereColumns();

OrderColumn tmpOrderColumn = new OrderColumn();
tmpOrderColumn.add("时间", "savetime");

OrderColumn[] theOrderColumn = tmpOrderColumn.getOrderColumns();

String channel = Tools.strNull(request.getParameter("channel"));
String serverId = Tools.strNull(request.getParameter("serverId"));

String startTime = request.getParameter("startTime");
String endTime = Tools.strNull(request.getParameter("endTime"));
if(startTime == null){
	startTime = MyTools.getDateStr();
}
JSONObject xml = MsgLogBAC.getInstance().getPageList(pageContext);			
%>
<%@include file="inc_list_getparameter.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script src="../js/Calendar3.js"></script>
<script>
function checkForm() {
	var theForm=document.forms[0];
	theForm.encoding="application/x-www-form-urlencoded";
	theForm.action="";	
	theForm.submit();
}
</script>
<script>
var allValue=new Object();
allValue.channel="<%=channel%>";
allValue.serverId="<%=serverId%>";
allValue.startTime="<%=startTime%>";
allValue.endTime="<%=endTime%>";
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
                    <td nowrap background="../images/tab_midbak.gif">玩家聊天</td>
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
						  
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
<tr>
<td nowrap>记录数:<font color="#FF0000"><%=xml != null ? xml.optInt("totalRecord") : 0%></font></td>
<td align="right">
起始时间
<input name="startTime" type="text" class="input1" id="startTime" value="" size="15" onClick="new Calendar().show(this)">
结束时间 
<input name="endTime" type="text" class="input1" id="endTime" value="" size="15" onClick="new Calendar().show(this)">
<select name="colname" class="input1" id="colname" onchange="changeOperator(this)">
		<%
			int nSelected = 0;
			for (int i = 0; i < theWhereColumn.length; i++) {
		%>
		<option value="<%=theWhereColumn[i].strFieldName%>" <%if (colname.equals(theWhereColumn[i].strFieldName)) {out.print("selected");nSelected = i;}%>>
		<%=theWhereColumn[i].strDisplayName%>
		</option>
		<%
			}
		%>
</select> 
<select name="operator" class="input1" id="operator">
		<%
			for (int i = 0; i < theWhereColumn[nSelected].strOperator.length; i++) {
		%>
		<option value="<%=theWhereColumn[nSelected].strOperator[i]%>" <%if(operator.equals(theWhereColumn[nSelected].strOperator[i])){out.print("selected");}%>>
		<%=theWhereColumn[nSelected].strOperator[i]%>
		</option>
		<%
		}
		%>
</select> 
<input name="colvalue" type="text" class="input1" id="colvalue" value="" size="15">
<script>
document.getElementById("colvalue").value="<%=colvalue%>";
</script>
<input type=image src="../images/icon_search16.gif" align="absmiddle" alt="Find"> 
<img src="../images/icon_showall.gif" alt="Show all" align="absmiddle" style="cursor:hand" onclick="document.forms[0].colvalue.value='';document.forms[0].submit()">
</td>
</tr>
</table><table width="100%" border="0" cellspacing="1" cellpadding="2">
<tr> 
  <td nowrap>排序：
	<select name="showorder" id="showorder" onchange="document.forms[0].submit()">
	 <%for(int i=0;i<theOrderColumn.length;i++){%>
	  <option value="<%=theOrderColumn[i].strFieldName%>" <%if(showorder.equals(theOrderColumn[i].strFieldName))out.print("selected");%>><%=theOrderColumn[i].strDisplayName%></option>
	  <%}%>	  
	</select>
	<input name="ordertype" type="radio" value="DESC" <%if(ordertype.equals("DESC"))out.print("checked");%> onclick="document.forms[0].submit()">
	逆序
<input type="radio" name="ordertype" value="ASC" <%if(ordertype.equals("ASC"))out.print("checked");%> onclick="document.forms[0].submit()">
	顺序</td>
  <td align="right">每页行数
	<input name="rpp" type="text" class="input4right" id="rpp" value="<%=rpp%>" size="2">
	
	<input name="Button" type="button" class="btn1" value="刷新" onclick="document.forms[0].submit()"> 
  </td>
</tr>
</table>
                          <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                            <tr align="center" class="listtopbgc"> 
                              <td >No.</td>
                              <td >id</td>
                              <td align="center" nowrap>
                              <select name="serverId" id="serverId" onChange="document.forms[0].submit()">
								<option value="">游戏服</option>
								<%
								DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
								while(serverRs.next()) {
								%>
								<option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%></option>
								<%
								}
								%>
								</select>
								</td>
                              <td align="center" nowrap><select name="channel" id="channel" onChange="checkForm()">
							   <option value="">选择频道</option>
							   <%
							   for(int i = 0; i < MsgLogBAC.CHA_NAME.length; i++){
							   	%>
							   	<option value="<%=(i+1) %>"><%=MsgLogBAC.CHA_NAME[i] %></option>
							   	<%
							   }
								%>
                              </select></td>
								<td align="center" nowrap>角色名</td>
								<td align="center" nowrap>密语对象</td>
								<td align="center" nowrap>聊天内容</td>
								<td align="center" nowrap>帮派</td>
								<td align="center" nowrap>语音文件</td>
								<td align="center" nowrap>语音秒数</td>
								<td align="center" nowrap>发送时间</td>
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
								String playerName = PlayerBAC.getInstance().getNameById(line.optInt("playerid"));
								String friendName = PlayerBAC.getInstance().getNameById(line.optInt("friendid"));
							%>
                            <tr class="nrbgc1">
                              <td align="center" nowrap><%=count++%></td>
                              <td align="center" nowrap><%=line.optInt("id")%></td>
                              <td align="center" nowrap><%=ServerBAC.getInstance().getNameById(line.optInt("serverid"))%></td>
                              <td align="center" nowrap><%=MsgLogBAC.CHA_NAME[line.optInt("channel")-1]%></td>
							  <td align="center" nowrap><a href="player_data.jsp?playerName=<%=playerName%>&serverId=<%=line.optInt("serverid")%>"><%=playerName%></a></td>
							  <td align="center" nowrap><a href="player_data.jsp?playerName=<%=friendName%>&serverId=<%=line.optInt("serverid")%>"><%=friendName%></a></td>
							  <%
							  String p1 = "";
							  String p2 = "";
							  String p3 = "";
							  JSONArray contentarr = new JSONArray(line.optString("content"));
							  if(line.optInt("type") == 1){
							  	p1 = contentarr.optString(0);
							  } else 
							  if(line.optInt("type") == 2){
							  	p2 = contentarr.optString(0);
							  	p3 = contentarr.optString(1);
							  }
							  %>
							  <td align="center"><%=MsgLogBAC.getInstance().replaceEmotionTagToImg(p1)%></td>
							  <td align="center" nowrap><%=TabStor.getDataName(TabStor.tab_faction_stor, line.optInt("factionid"))%></td> 
							  <td align="center" nowrap><a href="msglog_getwav.jsp?playerId=<%=line.optInt("playerid")%>&filename=<%=p2%>"><%=p2%></a></td>
							  <td align="center" nowrap><%=p3%></td>
							  <td align="center" nowrap><%=line.optString("savetime")%></td>
                            </tr>
                            <%
							}								
							%>
                          </table>
                          <%@ include file="inc_list_bottom.jsp"%>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right">&nbsp;</td>
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
