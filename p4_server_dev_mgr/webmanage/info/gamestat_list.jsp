<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="游戏管理";
String perm="数据报表";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
DBPsRs gamestattypeRs = DBPool.getInst().pQueryS(GameStatBAC.tab_gamestat_type);
JSONArray typearr = new JSONArray();
JSONArray funcarr = new JSONArray();
JSONObject filterarr = new JSONObject();
while(gamestattypeRs.next()){
	String typename = gamestattypeRs.getString("typename");
	int index = typearr.indexOf(typename);
	if(index == -1){
		index = typearr.length();
		typearr.add(typename);
	}
	JSONArray sub_funcarr = funcarr.optJSONArray(index);
	if(sub_funcarr == null){
		sub_funcarr = new JSONArray();
		funcarr.add(sub_funcarr);
	}
	JSONArray one = new JSONArray();
	one.add(gamestattypeRs.getString("num"));
	one.add(gamestattypeRs.getString("funcname"));
	sub_funcarr.add(one);
	if(!gamestattypeRs.getString("customfilter").equals("0")){
		String[][] strs = Tools.splitStrToStrArr2(gamestattypeRs.getString("customfilter"), "|", ",");
		JSONArray tarr = new JSONArray();
		for(int k = 0; k < strs.length; k++){
			if(strs[k][2].equals("1")){
				tarr.add(strs[k][0]+"：<input name='"+strs[k][1]+"' type='text' id='"+strs[k][1]+"'>");
			} else 
			if(strs[k][2].equals("2")){
				StringBuffer sb = new StringBuffer();
				sb.append(strs[k][0]+"：<select name='"+strs[k][1]+"' id='"+strs[k][1]+"'>");
				sb.append("<option value=''>选择"+strs[k][0]+"</option>");
				DBPsRs tabRs = DBPool.getInst().pQueryS(strs[k][3]);
				while(tabRs.next()){
					sb.append("<option value='"+tabRs.getString(strs[k][5])+"'>"+tabRs.getString(strs[k][4])+"</option>");
				}
				sb.append("</select>");
				tarr.add(sb.toString());
			}
		}
		filterarr.put(gamestattypeRs.getString("num"), tarr);
	}
	}
//System.out.println(typearr);
//System.out.println(funcarr);

String typelist = request.getParameter("typelist");
int num = Tools.str2int(request.getParameter("funclist"));

//System.out.println("typelist:"+typelist+" tabname:"+tabname);

DBPaRs typeRs = DBPool.getInst().pQueryA(GameStatBAC.tab_gamestat_type, "num="+num);

JSONArray returnarr = null;

if(request.getParameter("query") != null){
	ReturnValue rv = GameStatBAC.getInstance().getReportData(pageContext);
	if(rv.success){
		returnarr = new JSONArray(rv.info);	
	} else {
%>
<script type="text/javascript">
alert('查询失败，错误：<%=rv.info.replace("\n","\\n").replace("\r\n","\\r\\n")%>');
</script>
<%
	}
}
//System.out.println("returnStr:"+returnStr);
%>

<script src="../js/meizzDate.js"></script>
<script src="../js/Calendar3.js"></script>

<script>
var allValue = new Object();
allValue.typelist = "<%=typelist%>";
<%
if(num != 0){
%>
allValue.funclist = "<%=num%>";
<%
}
%>
allValue.starttime = "<%=Tools.strNull(request.getParameter("starttime"))%>";
allValue.endtime = "<%=Tools.strNull(request.getParameter("endtime"))%>";
allValue.serverid = "<%=Tools.str2int(request.getParameter("serverid"))%>";
<%
if(typeRs.exist()){
	if(!typeRs.getString("customfilter").equals("0")){
		String[][] strs = Tools.splitStrToStrArr2(typeRs.getString("customfilter"), "|", ",");
		for(int k = 0; k < strs.length; k++){
		%>
<%="allValue."+strs[k][1]+" = '"+Tools.strNull(request.getParameter(strs[k][1]))+"'"%>;
		<%
		}	
	}	
}
%>

</script>

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>
<SCRIPT  SRC="../Charts/FusionCharts.js"></SCRIPT>

<script>
var data = <%=funcarr.toString()%>;
var data2 = <%=filterarr.toString()%>;
function stat(){
	var theForm = document.forms[0];	
	wait();
	theForm.submit();
}
function changeServer(obj,subobj){
	removeoption(subobj,0);
	var num = 0;
	if(obj.value!=-1){
		for(var k = 0; k < data[obj.value].length; k++){
			var json = data[obj.value][k];
			//alert(json[0]+","+json[1]);
			addoption(subobj,json[0],json[1]);
			if(num == 0){
				num = json[0];
			}
		}
	}
	changeCustomfilter(num);
}
function addoption(obj,value,text){
	var newOption = document.createElement("option");
	newOption.value = value;
	newOption.text = text;
	obj.options.add(newOption);
}
function removeoption(obj,index){
	while(obj.options.length>index){
		obj.options.remove(index);
	}
}
function changeCustomfilter(num){
	var tarr = data2[num];
	var str = "<span id='customfilter'>";
	if(tarr!=null){
		for(var i = 0; i < tarr.length; i++){
			str += tarr[i];
		}
		str += "<br>";
	}
	str += "</span>";
	document.getElementById("customfilter").outerHTML = str;
}
</script>

<link href="../css/style1.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EFEFEF">

<form name="form1" method="post" enctype="application/x-www-form-urlencoded" onSubmit="return stat()">
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
                    <td nowrap background="../images/tab_midbak.gif">游戏统计</td>
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
										 模块：
										   <select name="typelist" id="typelist" onChange="changeServer(this,document.getElementById('funclist'))">
										<%
										for(int i = 0; i < typearr.length(); i++){
										%>
										<option value="<%=i %>"><%=typearr.optString(i) %></option>
										<%
										}
										%>
										</select>
										报表：
										<select name="funclist" id="funclist" onChange="changeCustomfilter(document.getElementById('funclist').value)">
										</select>
										<br>
										起始时间：
										  <input name="starttime" type="text" id="starttime" onClick="new Calendar().show(this);">
										  终止时间：
										  <input name="endtime" type="text" id="endtime" onClick="new Calendar().show(this);">
										  游戏服务器：
										  <select name="serverid" id="serverid">
									      <option value="0">选择服务器</option>
										  <%
										  DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
										  while(serverRs.next()){
										  %>
										  <option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")%></option>
										  <%
										  }
										  %>
										  </select>
										  <br>
										  <span id="customfilter"></span>
										<input name="query" type="submit" id="query" value="查询">
									  </td>
									</tr>
								</table>
								<%
								if(returnarr != null){
								String[][] showpara = Tools.splitStrToStrArr2(typeRs.getString("showpara"), "|", ",");
								for(int i = 0; i < returnarr.length(); i++){
								String returnStr = returnarr.optString(i);
								if(showpara[i][0].equals("1")){
									JSONArray tabdataarr = new JSONArray(returnStr);
								%>
								<table width="100%" border="0" cellpadding="0" cellspacing="1" class="tbbgc1">
									<tr height="20" align="center">
									<%
									JSONArray colarr = tabdataarr.optJSONArray(0);
									for(int c = 0; c < colarr.length(); c++){
									%>
                                    <td class="listtopbgc"><%=colarr.getString(c) %></td>
									<%
									}
									%>
									</tr>
									
									<%
									for(int k = 1; k < tabdataarr.length(); k++){
									JSONArray one = tabdataarr.optJSONArray(k);
									%>
									<tr height="20" align="center" bgcolor="#EFEFEF">
									<%
									for(int c = 0; c < colarr.length(); c++){
									%>
                                    <td><%=one.optString(c) %></td>
                                    <%}%>
                                  	</tr>
									<%
									}
									%>									
                                </table>
								<%
									} else 
									if(showpara[i][0].equals("2") || showpara[i][0].equals("3")){
									%>
									<%=returnStr %>
									<%
									}
									%>
									<br>
									<%
									}
									}
								%>
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
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
<script>
autoChoose(allValue);
changeServer(document.getElementById('typelist'),document.getElementById('funclist'));
autoChoose(allValue);
changeCustomfilter(document.getElementById('funclist').value);
autoChoose(allValue);
</script>
</html>
