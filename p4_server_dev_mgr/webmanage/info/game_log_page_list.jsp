<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="客服管理";
String perm="玩家日志";
%>
<%//System.out.println("game_log_page_list页面开始------------------------------"+Tools.getCurrentDateTimeStr());%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
String[] actStrArr = request.getParameterValues("act");
String[] moduleArr = request.getParameterValues("module");
 
String pagenum = request.getParameter("page");
if(pagenum==null)
{
	pagenum="1";
}
String rpp = request.getParameter("rpp");
if(rpp == null)
{
	rpp = "10";
}
String ordertype = request.getParameter("ordertype");
if(ordertype==null || ordertype.equals(""))
{
	ordertype="DESC";
}
String showorder=request.getParameter("showorder");

String serverId = Tools.strNull(request.getParameter("serverId"));	
String playerName = Tools.strNull(request.getParameter("playerName"));
playerName = Tools.replace(playerName,"\"","\\\"");

String search_act = Tools.strNull(request.getParameter("search_act"));
String startTime = Tools.strNull(request.getParameter("startTime"));
String endTime = Tools.strNull(request.getParameter("endTime"));
String consume = Tools.strNull(request.getParameter("consume"));
String obtain = Tools.strNull(request.getParameter("obtain"));
String remark = Tools.strNull(request.getParameter("remark"));
String haveSearch = request.getParameter("haveSearch");

String[] changeArr = request.getParameterValues("otherchange");//变化删选
String[] showArr = request.getParameterValues("showchange");//列显示

if(startTime==null || startTime.equals(""))
{
	startTime = Tools.getCurrentDateStr();
}

JSONObject xml = null;

if(haveSearch!=null && haveSearch.equals("1"))
{
	xml = GameLogBAC.getInstance().getPageList(pageContext);
}
int amount = 0;
int count = 0;
JSONArray list = null;
if(xml != null) {
	count = (Tools.str2int(pagenum)-1)*ToolFunc.str2int(rpp)+1;
	list = xml.optJSONArray("list");
	amount = list.size();
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
<script src="../js/Calendar3.js"></script>
<script>
var allValue = new Object();
allValue.serverId="<%=serverId%>";
allValue.playerName="<%=playerName%>";
allValue.search_act="<%=search_act%>";
allValue.startTime="<%=startTime%>";
allValue.endTime="<%=endTime%>";

allValue.consume="<%=consume%>";
allValue.obtain="<%=obtain%>";
allValue.remark="<%=remark%>";

allValue.haveSearch="<%=haveSearch%>";

function check()
{
	if(document.getElementById("startTime").value=="")
	{
		alert("必须填写开始时间");
		document.getElementById("startTime").focus();
		return false;
	}
	if(!isDate(document.getElementById("startTime").value))
	{
		document.getElementById("startTime").focus();
		return false;
	}	
	if(document.getElementById("endTime").value!="" && !isDate(document.getElementById("endTime").value))
	{
		alert("注意填写正确的时间格式yyyy-mm-dd hh:mm:ss");
		document.getElementById("endTime").focus();
		return false;
	}
	wait();
	return true;
}
function checkForm()
{
	if(document.getElementById("startTime").value=="")
	{
		alert("必须填写开始时间");
		document.getElementById("startTime").focus();
		return false;
	}
	if(!isDate(document.getElementById("startTime").value))
	{
		document.getElementById("startTime").focus();
		return false;
	}	
	if(document.getElementById("endTime").value!="" && !isDate(document.getElementById("endTime").value))
	{
		alert("注意填写正确的时间格式yyyy-mm-dd hh:mm:ss");
		document.getElementById("endTime").focus();
		return false;
	}
	document.forms[0].submit();	
	wait();	
}
function reloadPage()
{
document.getElementById("haveSearch").value="0";
document.forms[0].submit();
wait();
}
function changeShow(dtype,amount)
{
	var isShow = "";
	if(document.getElementById("showa"+dtype).style.display==""){
		isShow = "none";
	}
	document.getElementById("showa"+dtype).style.display=isShow;
	document.getElementById("showb"+dtype).style.display=isShow;
	for(var i = 0; i < amount; i++){
		document.getElementById("showc"+dtype+"_"+i).style.display=isShow;
		document.getElementById("showd"+dtype+"_"+i).style.display=isShow;
	}
}
</script>
</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="" enctype="application/x-www-form-urlencoded" onSubmit="return check()">
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
                    <td nowrap background="../images/tab_midbak.gif">玩家日志</td>
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
                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><table width="100%" border="0" cellpadding="0" cellspacing="0">
                          
                          <tr>
                            <td width="78" align="right" nowrap><strong>模块筛选：</strong></td>
                            <td colspan="3" valign="top">
                            <%
                            DBPsRs sysRs = DBPool.getInst().pQueryS(TabStor.tab_game_sys);
                            while(sysRs.next()){
							%>
<input name="module" type="checkbox" id="module" value="<%=sysRs.getInt("code")%>" <%if(Tools.strArrContain(moduleArr, sysRs.getString("code"))){%>checked<%}%> onClick="reloadPage()">
							<%=sysRs.getString("name")%>
							<%
							}
							%>
							<br/>
                                <font color="#FF3300">注意：改变筛选条件自动刷新后会清除之前的查询结果，需要再次点击查询按钮才能获得所需的查询结果。</font></td>
                          </tr>
                          <tr>
                            <td align="right" nowrap></td>
                            <td colspan="3" valign="top"><hr size="1" noshade></td>
                          </tr>
                          <tr>
                            <td align="right" nowrap><strong>指令筛选：</strong></td>
                            <td colspan="3" valign="top">
                            <%
                            for(int i=0;moduleArr!=null && i<moduleArr.length;i++){
							%>
                            <b><%=TabStor.getListVal(TabStor.tab_game_sys, "code="+moduleArr[i], "name")%></b>-
                            <%
                            	DBPsRs funcRs = DBPool.getInst().pQueryS(TabStor.tab_game_func, "module="+moduleArr[i]);
                            					while(funcRs.next()){
                            %>
                            <input name="act" type="checkbox" id="act" value="<%=funcRs.getString("code")%>" <%if(Tools.strArrContain(actStrArr, funcRs.getString("code"))){%>checked<%}%> >
                            <%=funcRs.getString("name")%>
                            <%
                            	}
                            %>
                            <br/>
							<%
								}
							%>
							</td>
                          </tr>
                          <tr>
                            <td align="right" nowrap></td>
                            <td colspan="3" valign="top"><hr size="1" noshade></td>
                          </tr>
                          <tr>
                            <td align="right" nowrap><strong>其他筛选：</strong></td>
<td colspan="3">
<%
	DBPsRs glRs = DBPool.getInst().pQueryS(GameLogBAC.tab_game_log_datatype);
while(glRs.next()){
	String checked = MyTools.checkInStrArr(changeArr, glRs.getString("dtype"))?"checked":"";
%>
<input name="otherchange" type="checkbox" id="otherchange" value="<%=glRs.getInt("dtype")%>" <%=checked%>><%=glRs.getString("name")+"变化"%>
<%
	}
%>
</td>
                          </tr>
                          <tr>
                            <td align="right" nowrap>&nbsp;</td>
                            <td colspan="3"><label></label></td>
                          </tr>
                          <tr>
                            <td align="right" nowrap><strong>游戏服务器：</strong></td>
                            <td colspan="3">
                                <select name="serverId" id="serverId">
                                  <option value="">选择</option>
                                  <%
                                  	DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server, "usestate=1");
                                  						  while(serverRs.next()) {
                                  %>
                                  <option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%></option>
                                  <%
                                  	}
                                  %>
                              </select></td>
                          </tr>
                          <tr>
                            <td align="right" nowrap><strong>角色名：</strong></td>
                            <td width="182"><input name="playerName" type="text" class="input1" id="playerName"></td>
                            <td align="right"><strong>消耗描述：</strong></td>
                            <td width="617"><input name="consume" type="text" class="input1" id="consume"></td>
                          </tr>
                          <tr>
                            <td align="right" nowrap><strong>指令编号：</strong></td>
                            <td><input name="search_act" type="text" class="input1" id="search_act"></td>
                            <td align="right"><strong>获取描述：</strong></td>
                            <td><input name="obtain" type="text" class="input1" id="obtain"></td>
                          </tr>
                          <tr>
                            <td align="right" nowrap><strong>起始时间：</strong></td>
                            <td ><input name="startTime" type="text" class="input1" id="startTime" onClick="new Calendar().show(this)">
                                <font color="#FF0000">*</font></td>
                            <td align="right" ><strong>备注：</strong></td>
                            <td ><input name="remark" type="text" class="input1" id="remark"></td>
                          </tr>
                          <tr>
                            <td align="right" nowrap><strong>结束时间：</strong></td>
                            <td ><input name="endTime" type="text" class="input1" id="endTime" onClick="new Calendar().show(this)"></td>
                            <td >&nbsp;</td>
                            <td >&nbsp;</td>
                          </tr>
                          <tr>
                            <td align="right" nowrap>&nbsp;</td>
                            <td colspan="3"><input name="btnSubmit" type="submit" id="btnSubmit"  value="查询" onClick="document.getElementById('haveSearch').value=1">
                              <input name="haveSearch" type="hidden" id="haveSearch"></td>
                          </tr>
                      </table></td>
                    </tr>
                  </table>
                  <table width="90%" border="0" cellspacing="1" cellpadding="2">
                    <tr> 
                      <td><table width="100%" border="0" cellspacing="1" cellpadding="2">
							<tr>
							  <td nowrap>记录数:<font color="#FF0000"><%=xml!=null?xml.optInt("totalRecord"):0%></font></td>
							  <td align="right"></td>
							</tr>
						  </table>
                        <table width="100%" border="0" cellspacing="1" cellpadding="2">
							<tr> 
							  <td nowrap>排序：
								<select name="showorder" id="showorder" onChange="document.forms[0].submit()">
								 <option value="log.id" >id</option>								  
								</select>
								<input name="ordertype" type="radio" value="DESC" <%if(ordertype.equals("DESC"))out.print("checked");%> onClick="checkForm()">
								逆序
							<input type="radio" name="ordertype" value="ASC" <%if(ordertype.equals("ASC"))out.print("checked");%> onClick="checkForm()">
								顺序  </td>
							  <td align="right">每页行数
                                <input name="rpp" type="text" class="input4right" id="rpp" value="<%=rpp%>" size="2">
                                <input name="Button" type="button" class="btn1" value="刷新" onClick="checkForm()"></td>
							</tr>
							<tr>
							  <td colspan="2" nowrap><strong>显示可选列：</strong>
							  <%
							  	glRs.beforeFirst();
							  	while(glRs.next()){
							  		String checked = MyTools.checkInStrArr(showArr, glRs.getString("dtype"))?"checked":"";
							  %>
							  <input name="showchange" type="checkbox" id="showchange" value="<%=glRs.getInt("dtype")%>" <%=checked%> onClick="changeShow(<%=glRs.getInt("dtype")%>,<%=amount%>)"><%=glRs.getString("name")%>
							  <%
							  	}
							  %>
							  </td>
						    </tr>
						  </table>
                          <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                            <tr align="center" class="listtopbgc"> 
                              <td >No.</td>
                              <td align="center" nowrap>id</td>
                              <td align="center" nowrap>游戏服务器</td>
								<td align="center" nowrap>游戏角色</td>
								<td align="center" nowrap>帮派ID</td>
								<td align="center" nowrap>指令</td>
								<%
									glRs.beforeFirst();
									while(glRs.next()){
										String style = MyTools.checkInStrArr(showArr, glRs.getString("dtype"))?"":"none";
								%>
								<td align="center" nowrap id="showa<%=glRs.getInt("dtype")%>"style="display:<%=style%>"><%=glRs.getString("name")+"变化"%></td>
								<td align="center" nowrap id="showb<%=glRs.getInt("dtype")%>"style="display:<%=style%>"><%=glRs.getString("name")%></td>
								<%
									}
								%>
								<td align="center" nowrap>消耗描述</td>								
								<td align="center" nowrap>获取描述</td>								
								<td align="center" nowrap>备注</td>
								<td align="center" nowrap>时间</td>
                            </tr>
                            <%
                            for(int i=0; list!=null && i<list.length(); i++) {
                            	JSONObject line = (JSONObject)list.opt(i);
                            	int id = line.optInt("id");
                            %>
                            <tr class="nrbgc1">
                            <td align="center" nowrap><%=count++%></td>
                            <td align="center" nowrap><%=line.optString("id")%></td>
                            <td align="center" nowrap><%=ServerBAC.getInstance().getNameById(line.optInt("serverid"))%><br/>(<%=line.optInt("serverid")%>)</td>
							<td align="center" nowrap>
							<%
							String pName = PlayerBAC.getInstance().getNameById(line.optInt("playerid"));
							%>
							<a href="player_data.jsp?playerName=<%=pName%>&serverId=<%=line.optInt("serverid")%>"><%=pName%></a><br/>(<%=line.optInt("playerid")%>)</td>
							<td align="center" nowrap><%=line.optInt("factionid")%></td>
							<td align="center" nowrap><%=TabStor.getListVal(TabStor.tab_game_func, "code="+line.optInt("act"), "name")%><br/>(<%=line.optInt("act")%>)</td>
							<%
							glRs.beforeFirst();
							while(glRs.next()){
								long chaVal = line.optLong(glRs.getString("chacol"));
								long nowVal = line.optLong(glRs.getString("nowcol"));
								String style = MyTools.checkInStrArr(showArr, glRs.getString("dtype"))?"":"none";
							%>
							<td align="center" nowrap id="showc<%=glRs.getInt("dtype")+"_"+i%>" style="display:<%=style%>"><%=(chaVal>=0?"<font color='#336633'>":"<font color='#ff0000'>")+chaVal+"</font>"%></td>
							<td align="center" nowrap id="showd<%=glRs.getInt("dtype")+"_"+i%>" style="display:<%=style%>"><%="<font color='#0066FF'>"+nowVal+"</font>"%></td>
							<%
							}
							%>
							<td><%=Tools.replace(line.optString("consume"),"\n","<br/>")%></td>
							<td><%=Tools.replace(line.optString("obtain"),"\n","<br/>")%></td>
							<td><%=Tools.replace(line.optString("remark"),"\n","<br/>")%></td>
							<td align="center" nowrap><%=line.optString("createtime")%></td>
                            </tr>
                            <%
							}								
							%>
                          </table>
                          <%@ include file="inc_list_bottom.jsp"%>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right"></td>
                            </tr>
                          </table>                      </td>
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
<%//System.out.println("页面生成完成"+Tools.getCurrentDateTimeStr());%>