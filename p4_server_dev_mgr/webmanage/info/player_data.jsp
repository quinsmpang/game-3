<%@page import="com.moonic.mgr.TabStor"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="客服管理";
String perm="玩家数据";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%

int serverId = Tools.str2int(request.getParameter("serverId"));
String playerName = Tools.strNull(request.getParameter("playerName"));

JSONObject dataobj  = null;

ReturnValue rv = null;

if(serverId > 0 && !playerName.equals("")) {
	int playerId = PlayerBAC.getInstance().getIntValue("id", "serverid="+serverId+" and name='"+playerName+"'");
	rv = PlayerBAC.getInstance().getAllData(playerId, serverId);
	if(rv.success) {
		dataobj = new JSONObject(rv.info);
	}
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
<script>
var allValue=new Object();
allValue.playerName="<%=playerName%>";
allValue.serverId="<%=serverId%>";
</script>
<script>
function checkForm() {
	if(document.getElementById("serverId").selectedIndex==0) {
		alert("请选择游戏服务器");
		document.getElementById("serverId").focus();
		return;
	}
	if(document.getElementById("playerName").value=="") {
		alert("请输入要查询的玩家名");
		document.getElementById("playerName").focus();
		return;
	}
	document.forms[0].submit();
}
</script>
</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="player_data.jsp" enctype="application/x-www-form-urlencoded">
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
                    <td nowrap background="../images/tab_midbak.gif">玩家数据</td>
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
                                <td nowrap>游戏服务器：
                                		<select name="serverId" id="serverId">
										<option value="0">选择</option>
									  	<%
									  	DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
									  	while(serverRs.next()) {
									  	%>
									    <option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%></option>
										<%
										}											
										%>
								      	</select>
                                  	玩家名：
                                  <input name="playerName" type="text" class="input1" id="playerName">
                                  <input type="button" name="Button2" value="查询" onClick="checkForm()">
                                  </td>
                                </tr>
                          </table>
						  <%
						  if(dataobj != null){
							JSONArray plaarr = dataobj.optJSONArray("player");
							JSONArray plarolearr = dataobj.optJSONArray("plarole");
							JSONArray petarr = dataobj.optJSONArray("pet");
							JSONObject propobj = dataobj.optJSONObject("prop");
							JSONArray plafac = dataobj.optJSONArray("plafac");
							JSONArray jjcarr = dataobj.optJSONArray("jjc");
							JSONArray placonst = dataobj.optJSONArray("placonst");
							
							DBPaRs roleRs = DBPool.getInst().pQueryA(TabStor.tab_role, "num="+plaarr.optInt(1));
							DBPaRs rolebasepropRs = DBPool.getInst().pQueryA(TabStor.tab_role_base_prop, "proftype="+roleRs.getInt("proftype"));
							
							String tqname = TabStor.getListVal(TabStor.tab_prerogative, "num="+plaarr.optInt(14), "name");
                  	    	long tqduetime = plaarr.optLong(15);
                  	    	boolean miss = MyTools.checkSysTimeBeyondSqlDate(tqduetime);
							
							JSONObject form_name = new JSONObject();
							
							form_name.put("0", plaarr.optString(3));
							
							Object[][] basedata = {
								{"角色名", plaarr.optString(3)+"(ID:"+plaarr.optInt(0)+")"}, 
								{"职业", roleRs.getString("name")+"("+(roleRs.getInt("sex")==1?"男":"女")+")"}, 
                                {"等级", plaarr.optInt(9)}, 
                                {"经验", plaarr.optInt(10)}, 
                                {"在线", plaarr.optInt(22)==1?"<font color='#006633'>是</font>":"<font color='#FF0000'>否</font>"}, 
                                {"最后登录时间", MyTools.getTimeStr(plaarr.optLong(21))},
                                {"金币", plaarr.optInt(5)},
                                {"绑钻", plaarr.optInt(7)},
                                {"钻石", plaarr.optInt(6)},
								{"",""},
								{"功勋",plafac.optInt(2)},
                                {"天梯积分", jjcarr.optInt(0)},
                                {"强化点",plarolearr.optInt(18)},
								{"星魂",placonst.optInt(0)},
								{"星辰碎片",placonst.optInt(1)},
                                {"VIP等级", plaarr.optInt(11)},
                              	{"充值钻石总数", plaarr.optInt(12)},
                              	{"特权", tqname.equals("0")?"无":tqname},
{"特权到期时间", tqname.equals("0")?"-":(MyTools.getTimeStr(tqduetime)+(miss?"<font color='#FF0000'>（已过期）</font>":""))},
								{"",""},
							};
						  %>
                            <table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
                              <tr>
                                <td colspan="5" align="center" class="listtopbgc"><strong>基本数据</strong></td>
                              </tr>
                              <%
                              for(int i = 0; i < basedata.length; i++){
                              	if(i % 5 == 0){
                       			%>
                       			<tr>
                       			<%
                              	}
                              	%>
                              	<td class="nrbgc1"><strong><%=!basedata[i][0].equals("")?basedata[i][0]+"：":"" %></strong><%=basedata[i][1] %></td>
                              	<%
                              	if(i % 5 == 4){
                              	%>
                       			</tr>
                       			<%	
                              	}
                              }
                              %>
                            </table>
                            <br>
                            <%
                            int[][] pointarr = new int[5][];
                            for(int i = 0; i < pointarr.length; i++){
                            	int p1 = plarolearr.optInt(i);
                              	int p2 = rolebasepropRs.getInt("prop"+(i+1));
                              	int p3 = p1 + p2;
                              	pointarr[i] = new int[]{p3, p2};
                            }
                            
                            JSONArray pla_proparr = propobj.optJSONArray("0");
                            JSONArray pla_srcarr = pla_proparr.optJSONArray(1);
							JSONArray pla_gem = dataobj.optJSONArray("plagem");
							JSONArray consteq = dataobj.optJSONArray("consteq");
							JSONArray conststor = dataobj.optJSONArray("conststor");
                            JSONArray itemarr = dataobj.optJSONArray("item");
							JSONArray useitemarr = itemarr.optJSONArray(3);
							
							JSONArray pla_consteq = PlayerBAC.getInstance().getConstellationEquipData(consteq, 0);
							
                            Object[][] pladata = {
								{"基础属性"},
								{"力量", pointarr[0][0]+"("+pointarr[0][1]+")"}, 
                                {"体质", pointarr[1][0]+"("+pointarr[1][1]+")"},
                                {"强度", pointarr[2][0]+"("+pointarr[2][1]+")"},
                                {"敏捷", pointarr[3][0]+"("+pointarr[3][1]+")"},
                                {"魔力", pointarr[4][0]+"("+pointarr[4][1]+")"},
                                {""},
                                {"能力值"},
                                {"物攻", pla_srcarr.optInt(2)},
                                {"物防", pla_srcarr.optInt(3)},
                                {"魔攻", pla_srcarr.optInt(4)},
                              	{"魔防", pla_srcarr.optInt(5)},
                                {"速度", pla_srcarr.optInt(6)},
                                {"命中", pla_srcarr.optInt(7)},
                                {"continue"},
                                {"回避", pla_srcarr.optInt(8)},
                                {"暴击", pla_srcarr.optInt(9)},
                                {"眩晕", pla_srcarr.optInt(12)},
                                {"石化", pla_srcarr.optInt(13)},
                                {"中毒", pla_srcarr.optInt(14)},
                                {"混乱", pla_srcarr.optInt(15)},
                                {"生命法力"},
                                {"生命", pla_srcarr.optInt(0)},
                                {"法力", pla_srcarr.optInt(1)},
                                {""},
								{""},
								{""},
                                {""},
                                {"装备"},
                               	{PlayerBAC.getInstance().getEquipShowStr(useitemarr, plarolearr.optInt(6))},
                                {PlayerBAC.getInstance().getEquipShowStr(useitemarr, plarolearr.optInt(7))},
                                {PlayerBAC.getInstance().getEquipShowStr(useitemarr, plarolearr.optInt(8))},
                                {PlayerBAC.getInstance().getEquipShowStr(useitemarr, plarolearr.optInt(9))},
                                {PlayerBAC.getInstance().getEquipShowStr(useitemarr, plarolearr.optInt(10))},
                                {""},
                                {"continue"},
                                {PlayerBAC.getInstance().getEquipShowStr(useitemarr, plarolearr.optInt(11))},
                                {PlayerBAC.getInstance().getEquipShowStr(useitemarr, plarolearr.optInt(12))},
                                {PlayerBAC.getInstance().getEquipShowStr(useitemarr, plarolearr.optInt(13))},
                                {PlayerBAC.getInstance().getEquipShowStr(useitemarr, plarolearr.optInt(14))},
                                {PlayerBAC.getInstance().getEquipShowStr(useitemarr, plarolearr.optInt(15))},
                                {""},
								{"宝石"},
								{PlayerBAC.getInstance().getGemShowStr(pla_gem.optJSONArray(0))},
                                {PlayerBAC.getInstance().getGemShowStr(pla_gem.optJSONArray(1))},
                                {PlayerBAC.getInstance().getGemShowStr(pla_gem.optJSONArray(2))},
                                {PlayerBAC.getInstance().getGemShowStr(pla_gem.optJSONArray(3))},
                                {""},
								{""},
								{"continue"},
                                {PlayerBAC.getInstance().getGemShowStr(pla_gem.optJSONArray(4))},
                                {PlayerBAC.getInstance().getGemShowStr(pla_gem.optJSONArray(5))},
                                {PlayerBAC.getInstance().getGemShowStr(pla_gem.optJSONArray(6))},
                                {PlayerBAC.getInstance().getGemShowStr(pla_gem.optJSONArray(7))},
								{""},
								{""},
								{"星座"},
								{PlayerBAC.getInstance().getConstellationData(conststor, pla_consteq.optInt(1))},
                                {PlayerBAC.getInstance().getConstellationData(conststor, pla_consteq.optInt(2))},
                                {PlayerBAC.getInstance().getConstellationData(conststor, pla_consteq.optInt(3))},
                                {PlayerBAC.getInstance().getConstellationData(conststor, pla_consteq.optInt(4))},
                                {""},
								{""},
								{"continue"},
                                {PlayerBAC.getInstance().getConstellationData(conststor, pla_consteq.optInt(5))},
                                {PlayerBAC.getInstance().getConstellationData(conststor, pla_consteq.optInt(6))},
                                {PlayerBAC.getInstance().getConstellationData(conststor, pla_consteq.optInt(7))},
                                {PlayerBAC.getInstance().getConstellationData(conststor, pla_consteq.optInt(8))},
								{""},
								{""},
							};
                            
                            %>
                            <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor="#000000">
                              <tr>
                                <td colspan="7" align="center" class="listtopbgc"><strong>人物数据</strong></td>
                              </tr>
                              <%
                              for(int i = 0; i < pladata.length; i++){
                              	if(pladata[i].length >= 2){
                              		pladata[i][0] = "<strong>"+pladata[i][0]+"</strong>：<font color=\"#ff0000\">"+pladata[i][1]+"</font>";
                              	}
                              	if(pladata[i][0].equals("continue")){
                              		continue;
                              	}
                              	String classStr = "<td class=\"nrbgc1\">"+pladata[i][0]+"</td>";
                              	if(i % 7 == 0){
                              		String rowspan = "";
	                              	if(pladata[i][0].equals("能力值")
									  || pladata[i][0].equals("装备")
									  || pladata[i][0].equals("宝石")
									  || pladata[i][0].equals("星座")
									  ){
	                              		rowspan = "rowspan=\"2\"";
	                              	}
                              		classStr = "<td bgcolor=\"#076c88\" "+rowspan+"><font color=\"#ffffff\">"+pladata[i][0]+"</font></td>";
                       			%>
                       			<tr>
                       			<%
                              	}
                              	%>
                              	<%=classStr %>
                              	<%
                              	if(i % 7 == 6){
                              	%>
                       			</tr>
                       			<%	
                              	}
                              }
                              %>
                            </table>
                            <br>
                            
                            <%
                             
                            %>
                            <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor="#000000">
                              <tr>
                                <td colspan="7" align="center" class="listtopbgc"><strong>宠物数据</strong></td>
                              </tr>
                              <%
							 
							  
String[] phase_color = new String[]{"33cc33", "33cc33", "33cc33", "2396e7", "2396e7", "ff5bd1", "ff5bd1", "ff3333", "ff3333", "ff0000", "ff0000"};
                              for(int k = 0; k < petarr.length(); k++){
                              		JSONArray arr = petarr.optJSONArray(k);
                              		
                              		JSONArray pet_proparr = propobj.optJSONArray(arr.optString(0));
                            		JSONArray pet_srcarr = pet_proparr.optJSONArray(1);
									JSONArray pet_consteq = PlayerBAC.getInstance().getConstellationEquipData(consteq, arr.optInt(0));
									if(pet_consteq == null){
										pet_consteq = new JSONArray();
									}
									
									form_name.put(arr.optString(0), arr.optString(2)+"("+arr.optInt(0)+")");
									
	                              Object[][] petdata = {
									{"基本数据"},
									{"<font color="+phase_color[arr.optInt(8)]+">"+arr.optInt(8)+"阶"+arr.optString(2)+"+"+arr.optInt(6)+"</font>"},
	                                {"编号", arr.optInt(1)},
	                                {"宠物ID", arr.optInt(0)}, 
	                                {"等级", arr.optInt(5)},
	                                {"经验", arr.optInt(4)},
	                                {""},
	                                {"能力值"},
									{"物攻", pet_srcarr.optInt(2)},
									{"物防", pet_srcarr.optInt(3)},
									{"魔攻", pet_srcarr.optInt(4)},
									{"魔防", pet_srcarr.optInt(5)},
									{"速度", pet_srcarr.optInt(6)},
									{"命中", pet_srcarr.optInt(7)},
									{"continue"},
									{"回避", pet_srcarr.optInt(8)},
									{"暴击", pet_srcarr.optInt(9)},
									{"眩晕", pet_srcarr.optInt(12)},
									{"石化", pet_srcarr.optInt(13)},
									{"中毒", pet_srcarr.optInt(14)},
									{"混乱", pet_srcarr.optInt(15)},
									{"生命法力"},
									{"生命", pet_srcarr.optInt(0)},
									{"法力", pet_srcarr.optInt(1)},
									{""},
									{""},
									{""},
									{""},
									{"星座"},
									{PlayerBAC.getInstance().getConstellationData(conststor, pet_consteq.optInt(1))},
									{PlayerBAC.getInstance().getConstellationData(conststor, pet_consteq.optInt(2))},
									{PlayerBAC.getInstance().getConstellationData(conststor, pet_consteq.optInt(3))},
									{PlayerBAC.getInstance().getConstellationData(conststor, pet_consteq.optInt(4))},
									{""},
									{""},
									{"continue"},
									{PlayerBAC.getInstance().getConstellationData(conststor, pet_consteq.optInt(5))},
									{PlayerBAC.getInstance().getConstellationData(conststor, pet_consteq.optInt(6))},
									{PlayerBAC.getInstance().getConstellationData(conststor, pet_consteq.optInt(7))},
									{PlayerBAC.getInstance().getConstellationData(conststor, pet_consteq.optInt(8))},
									{""},
									{""},
								};
                              
                              for(int i = 0; i < petdata.length; i++){
                              	if(petdata[i].length >= 2){
                              		petdata[i][0] = "<strong>"+petdata[i][0]+"</strong>：<font color=\"#ff0000\">"+petdata[i][1]+"</font>";
                              	}
                              	if(petdata[i][0].equals("continue")){
                              		continue;
                              	}
                              	String classStr = "<td class=\"nrbgc1\">"+petdata[i][0]+"</td>";
                              	if(i % 7 == 0){
                              		String rowspan = "";
	                              	if(petdata[i][0].equals("能力值") 
									|| petdata[i][0].equals("星座")){
	                              		rowspan = "rowspan=\"2\"";
	                              	}
                              		classStr = "<td bgcolor=\"#076c88\" "+rowspan+"><font color=\"#ffffff\">"+petdata[i][0]+"</font></td>";
                       			%>
                       			<tr>
                       			<%
                              	}
                              	%>
                              	<%=classStr %>
                              	<%
                              	if(i % 7 == 6){
                              	%>
                       			</tr>
                       			<%	
                              	}
                              }
                              	if(k < petarr.length()-1){
                              %>
                              <tr><td class="nrbgc1" colspan="7">&nbsp;</td></tr>
                              <%
                              	}
                              }
                              %>
                            </table>
                            <br>
                            <%
                            JSONArray formarr = dataobj.optJSONArray("form");
                            %>
                            <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor="#000000">
                              <tr>
                                <td colspan="7" align="center" class="listtopbgc"><strong>布阵数据</strong></td>
                              </tr>
                              <%
							  form_name.put("-1", "空");
							  
                              String[] formname = {"单人布阵", "多人布阵"};
                              for(int k = 0; k < formarr.length(); k++){
                              	JSONArray arr = formarr.optJSONArray(k);
                              	JSONArray a1 = arr.optJSONArray(0);
                              	JSONArray a2 = arr.optJSONArray(1);
	                              	for(int x = 0; x < arr.length(); x++){
	                              		JSONArray a = arr.optJSONArray(x);
		                              	if(a.length() == 0){
			                              	a.add(0, -1);
			                      			a.add(0, -1);
			                      			a.add(0, -1);
			                      			a.add(0, -1);
			                      			a.add(0, -1);	
		                              	} else 
		                              	if(a.length() == 1){
		                              		a.add(0, -1);
			                      			a.add(0, -1);
			                      			a.add(-1);
			                      			a.add(-1);
		                              	} else 
		                              	if(a.length() == 2){
		                              		a.add(0, -1);
			                      			a.add(-1);
			                      			a.add(-1);
		                              	} else 
		                              	if(a.length() == 3){
		                              		a.add(0, -1);
		                      				a.add(-1);
		                              	} else 
		                              	if(a.length() == 4){
		                              		a.add(-1);
		                              	}
	                              	}
                              %>
                              <tr>
                              	<td class="nrbgc1" colspan="6" align="center"><strong><%=formname[k] %></strong></td>
                              </tr>
                              <tr>
                              	<td bgcolor="#076c88"><font color="#ffffff">前排</font></td>
                              	<td class="nrbgc1"><%=form_name.optString(a1.optString(0)) %></td>
                              	<td class="nrbgc1"><%=form_name.optString(a1.optString(1)) %></td>
                              	<td class="nrbgc1"><%=form_name.optString(a1.optString(2)) %></td>
                              	<td class="nrbgc1"><%=form_name.optString(a1.optString(3)) %></td>
                              	<td class="nrbgc1"><%=form_name.optString(a1.optString(4)) %></td>
                              </tr>
                              <tr>
                              	<td bgcolor="#076c88"><font color="#ffffff">后排</font></td>
                              	<td class="nrbgc1"><%=form_name.optString(a2.optString(0)) %></td>
                              	<td class="nrbgc1"><%=form_name.optString(a2.optString(1)) %></td>
                              	<td class="nrbgc1"><%=form_name.optString(a2.optString(2)) %></td>
                              	<td class="nrbgc1"><%=form_name.optString(a2.optString(3)) %></td>
                              	<td class="nrbgc1"><%=form_name.optString(a2.optString(4)) %></td>
                              </tr>
                              <%
                              }
                              %>
                            </table>
                            <br>
							<%
							}
							%>
                            <br>
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
                <td bgcolor="#848284" height="1"> </td>
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
if(rv != null && !rv.success){
%>
alert("<%=rv.info%>");
<%
}
%>
</script>
</body>

</html>
