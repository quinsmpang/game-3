<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="游戏管理";
String perm="查看当前战斗";
%>
<%
boolean root=false;
if(userObj!=null)
{ 
 root=userObj.optBoolean("root"); 
}

%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("battleId","battleId",new String[]{"等于"});
tmpWhereColumn.add("角色id","playerId",new String[]{"等于"});
tmpWhereColumn.add("角色名","playerName",new String[]{"等于"});
WhereColumn[] theWhereColumn=tmpWhereColumn.getWhereColumns();

OrderColumn tmpOrderColumn=new OrderColumn();
tmpOrderColumn.add("发起时间","battle_time");

OrderColumn[] theOrderColumn=tmpOrderColumn.getOrderColumns();

%>
<%@include file="inc_list_getparameter.jsp"%>
<%
JSONObject xml=BattleConsole.getPageList(pageContext);
%>
<script src="../js/meizzDate.js"></script>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>

<script>

function endBattle(battleId)
{
	if(confirm("确认强行停止并清除该场战斗吗？"))
	{
		//alert(battleId);
		wait();		
		document.getElementById("hiddenFrame").src="battle_del.jsp?id="+battleId;	
	}	
}
function viewLog(viewType,battleId)
{
	openWindow("battle_viewlog.jsp?viewType=" + viewType+"&battleId="+battleId,"view",800,600,true,true);
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
                    <td nowrap background="../images/tab_midbak.gif">进行中的战斗</td>
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
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
									  <td nowrap>记录数:<font color="#FF0000"><%=xml!=null?xml.optInt("totalRecord"):0%></font></td>
									  <td align="right"> <select name="colname" class="input1" id="colname" onChange="changeOperator(this)">
										<%
										int nSelected = 0;
										for(int i=0;i<theWhereColumn.length;i++){%>
										  <option value="<%=theWhereColumn[i].strFieldName%>" <%if(colname.equals(theWhereColumn[i].strFieldName)){out.print("selected"); nSelected=i;}%>><%=theWhereColumn[i].strDisplayName%></option>
										  <%}%>
										</select>
										<select name="operator" class="input1" id="operator">
										 <%for(int i=0;i<theWhereColumn[nSelected].strOperator.length;i++){%>
										  <option value="<%=theWhereColumn[nSelected].strOperator[i]%>" <%if(operator.equals(theWhereColumn[nSelected].strOperator[i])){out.print("selected");}%>><%=theWhereColumn[nSelected].strOperator[i]%></option>
										  <%}%>	  
										</select> <input name="colvalue" type="text" class="input1" id="colvalue" value="" size="15"><script>document.getElementById("colvalue").value="<%=colvalue%>";</script>
									<input type=image src="../images/icon_search16.gif" align="absmiddle"  alt="Find"> <img src="../images/icon_showall.gif" alt="Show all" align="absmiddle" style="cursor:hand" onClick="document.forms[0].colvalue.value='';document.forms[0].submit()"></td>
									</tr>
								  </table><table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr> 
									  <td nowrap>&nbsp;</td>
									  <td align="right">每页行数
										<input name="rpp" type="text" class="input4right" id="rpp" value="<%=rpp%>" size="2">
										
										<input name="Button" type="button" class="btn1" value="刷新" onClick="document.forms[0].submit()"> 
									  </td>
									</tr>
									</table>
								<table width="100%" border="0" cellpadding="2" cellspacing="1"
									class="tbbgc1">
									<tr class="listtopbgc">
										<td width="10" align="center" nowrap>No.</td>
										<td align="center" nowrap>battleId</td>
										<td align="center" nowrap>战斗类型</td>
										<td align="center" nowrap>红队</td>
										<td align="center" nowrap>蓝队</td>
										<td align="center" nowrap>当前回合数</td>
										<td align="center" nowrap>战斗状态</td>
										<td align="center" nowrap>发起时间</td>
									    <td align="center" nowrap>回放日志</td>
									    <td align="center" nowrap>详细日志</td>
									    <td align="center" nowrap>操作</td>
									</tr>
									
									<%							
							
									int num = 0;
									JSONArray list=null;
									if(xml!=null)
									{
										num=(xml.optInt("rsPageNO")-1)*ToolFunc.str2int(rpp)+1;
										list = xml.optJSONArray("list");
									}
		
									for(int i=0;list!=null && i<list.length();i++)
									{
										JSONObject line = (JSONObject)list.opt(i);
										long battleId = line.optLong("battleId");
										int turns= line.optInt("turns");
										String startTime = line.optString("startTime");
										String redteam = line.optString("redteam");
										String blueteam = line.optString("blueteam");
										int battleType = line.optInt("battleType");
										int battleState= line.optInt("battleState");
									%>
									<tr class="nrbgc1">
										<td align="center" nowrap><%=num++%></td>
										<td align="center" nowrap><%=battleId%></td>
										<td align="center" nowrap><%=BattleConsole.getBattleTypeName(battleType)%></td>
										<td align="center" nowrap><%=redteam%></td>
										<td align="center" nowrap><%=blueteam%></td>
										<td align="center" nowrap><%=turns%></td>
										<td align="center" nowrap><%=BattleConsole.getBattleStateName(battleState)%></td>
										<td align="center" nowrap><%=startTime%></td>
									    <td align="center" nowrap><img src="../images/icon_view.gif" style="cursor:hand" onClick="viewLog(1,<%=battleId%>)"></td>
									    <td align="center" nowrap><img src="../images/icon_view.gif" style="cursor:hand" onClick="viewLog(2,<%=battleId%>)"></td>
									    <td align="center" nowrap><img src="../images/del.gif" alt="强制结束并清理该场战斗" style="cursor:hand" onClick="endBattle(<%=battleId%>)"></td>
									</tr>
									<%}%>
								</table>
								<%@ include file="inc_list_bottom.jsp"%>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
                                  <tr>
                                    <td></td>
                                  </tr>
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
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
</html>
