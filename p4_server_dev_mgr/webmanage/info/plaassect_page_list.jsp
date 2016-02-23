<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="玩家管理";
String perm="玩家财产管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
String recovermsg = null;
String takerecover = request.getParameter("takerecover");
if(takerecover!=null){
	ReturnValue recoverRv = PlaAssectBAC.getInstance().recover(pageContext);
	recovermsg = recoverRv.info;
}

SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("玩家ID","playerid",new String[]{"包含","等于"});
WhereColumn[] theWhereColumn=tmpWhereColumn.getWhereColumns();

OrderColumn tmpOrderColumn=new OrderColumn();
tmpOrderColumn.add("录入顺序","id");

OrderColumn[] theOrderColumn=tmpOrderColumn.getOrderColumns();
%>
<%@include file="inc_list_getparameter.jsp"%>
<%//解析数据
if(colvalue!=null && !colvalue.equals("")){
if(colname!=null){
if(operator.equals("等于")){
sqlS.add(colname,colvalue);
}else{
sqlS.add(colname,colvalue,"like");}
}
}
	
JSONObject xml = PlaAssectBAC.getInstance().getJsonPageList(sqlS.whereString(),showorder+" "+ordertype,ToolFunc.str2int(pagenum),ToolFunc.str2int(rpp,10));

%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script src="../js/chooseAll.js"></script>
<script>
function enter_recover(){
	var theForm=document.forms[0];
	
	theForm.encoding="application/x-www-form-urlencoded";
	theForm.action="plaassect_page_list.jsp";
	theForm.submit();
	
	wait();
}
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
                    <td nowrap background="../images/tab_midbak.gif">玩家财产管理</td>
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
                              <td align="center" nowrap>玩家</td>
                              <td align="center" nowrap>服务器</td>
<td align="center" nowrap>财产类型</td>
<td align="center" nowrap>财产数据</td>
<td align="center" nowrap>丢失时间</td>

                              <td >操作</td>
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
<td align="center" nowrap><%=PlayerBAC.getInstance().getNameById(line.optInt("playerid"))+"("+line.optInt("playerid")+")"%></td>
<td align="center" nowrap><%=ServerBAC.getInstance().getNameById(line.optInt("serverid"))+"("+line.optString("serverid")+")"%></td>
<td align="center" nowrap><%=PlaAssectBAC.ASSECT_TYPE[line.optInt("type")-1]%></td>
<td align="center" nowrap>
<%
JSONArray itemarr = new JSONArray(line.optString("info")).optJSONArray(0);
JSONArray colarr = itemarr.optJSONArray(1);
JSONArray valarr = itemarr.optJSONArray(3);
JSONObject itemobj = new JSONObject();
for(int k = 0; colarr!=null && k < colarr.length(); k++){
	itemobj.put(colarr.optString(k), valarr.optString(k));
}
int theid = 0;
String thename = null;
if(line.optInt("type")==1){
	theid = itemobj.optInt("itemid");
	thename=ItemBAC.getInstance().getListRs(ItemBAC.TYPE_EQUIP_ORDINARY, itemobj.optInt("num")).getString("name");
} else 
if(line.optInt("type")==2){
	theid = itemobj.optInt("itemid");
	thename=ItemBAC.getInstance().getListRs(ItemBAC.TYPE_EQUIP_DRESS, itemobj.optInt("num")).getString("name");
} else 
if(line.optInt("type")==3){
	theid = itemobj.optInt("itemid");
	thename=ItemBAC.getInstance().getListRs(ItemBAC.TYPE_EQUIP_JADE, itemobj.optInt("num")).getString("name");
} else 
if(line.optInt("type")==5){
	theid = itemobj.optInt("id");
	thename=TabStor.getListVal(TabStor.tab_pet, "num="+itemobj.optInt("petnum"), "name");
}
%>
<%=thename+"("+theid+")"%></td>
<td align="center" nowrap><%=line.optString("createtime")%></td>

                              <td align="center" nowrap>
							  <%
							  if(line.optString("recovertime").equals("")){
							  %>
							  <input name="recoverbox" type="checkbox" id="recoverbox" value="<%=line.optInt("id")%>" onClick="updateChooseAll(this)">恢复
							  <%
							  } else {
							  %>
							  恢复时间：<%=line.optString("recovertime")%>
							  <%
							  }
							  %>
							  </td>
                            </tr>
                            <%
				}								
				%>
				           <tr align="center" class="nrbgc1"> 
                            <td colspan="6" >&nbsp;</td>
                            <td ><input name="chooseAll" type="checkbox" id="chooseAll" value="1" onClick="chooseAllCate('recoverbox')">全选<br></td>
                            </tr>
                          </table>
                          <%@ include file="inc_list_bottom.jsp"%>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right">
							  <input name="takerecover" type="submit" id="takerecover" value="恢复选中物品" onClick="enter_recover()">
							  </td>
                            </tr>
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
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
<%
if(recovermsg!=null){
%>
<script>
alert("<%=recovermsg%>");
</script>
<%
}
%>
</html>
