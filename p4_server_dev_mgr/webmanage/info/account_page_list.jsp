<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="玩家管理";
String perm="帐号管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("用户名","tab_user.username",new String[]{"包含","等于"});
tmpWhereColumn.add("用户id","tab_user.id",new String[]{"等于"});
tmpWhereColumn.add("角色id","tab_user.playerid",new String[]{"等于"});
tmpWhereColumn.add("MAC","mac",new String[]{"等于"});
tmpWhereColumn.add("IMEI","imei",new String[]{"等于"});
WhereColumn[] theWhereColumn=tmpWhereColumn.getWhereColumns();

OrderColumn tmpOrderColumn=new OrderColumn();
tmpOrderColumn.add("id","tab_user.id");

OrderColumn[] theOrderColumn=tmpOrderColumn.getOrderColumns();
%>
<%@include file="inc_list_getparameter.jsp"%>
<%
String channel=Tools.strNull(request.getParameter("channel"));
String platform=Tools.strNull(request.getParameter("platform"));
String devuser=Tools.strNull(request.getParameter("devuser"));
String enable= Tools.strNull(request.getParameter("enable"));
	
JSONObject xml = UserBAC.getInstance().getPageList(pageContext);

	
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
allValue.channel="<%=channel%>";
allValue.platform="<%=platform%>";
allValue.devuser="<%=devuser%>";
allValue.enable="<%=enable%>";
</script>
<script>
function modify(id)
{
	var w=650,h=620,newwindow;
	var url="account_edit.jsp?id="+id;
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function convertopook()
{
	var w=720,h=620,newwindow;
	var url="account_convertopook.jsp";
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
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
                    <td nowrap background="../images/tab_midbak.gif">帐号管理</td>
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
                              <td align="center" nowrap>用户id</td>
                              <td align="center" nowrap>用户名</td>
								<td align="center" nowrap>
								<select name="channel" id="channel"
									onChange="document.forms[0].submit()">
									<option value="">联运渠道</option>
									<%
									DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
									while(channelRs.next()) {
									%>
										<option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")%>(<%=channelRs.getString("code")%>)</option>
									<%
									}
									%>
									</select></td>
								<td align="center" nowrap>
								<select name="platform" id="platform"
									onChange="document.forms[0].submit()">
									<option value="">账户渠道</option>
									<%
									DBPsRs platformRs = DBPool.getInst().pQueryS(TabStor.tab_platform);
									while(platformRs.next()){
									%>
									<option value="<%=platformRs.getString("CODE")%>">
									<%=platformRs.getString("NAME")%>(<%=platformRs.getString("CODE")%>)
									</option>
									<%
									}
									%>
									</select></td>
								<td align="center" nowrap>游戏服务器</td>
								<td align="center" nowrap>游戏角色</td>
								<td align="center" nowrap>登录时间</td>
								<td align="center" nowrap>注册时间</td>
								<td align="center" nowrap><select name="devuser" id="devuser" onChange="document.forms[0].submit()">
									<option value="">开发人员</option>
									<option value="1">是</option>
									<option value="0">否</option>
									</select>									</td>
								<td align="center" nowrap><select name="enable" id="enable" onChange="document.forms[0].submit()">
								  <option value="">账号可用</option>
									<option value="1">是</option>
									<option value="0">否</option>
									</select></td>
								<td nowrap>操作</td>
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
<td align="center" nowrap><%=line.optInt("id")%></td>
<td align="center" nowrap><%=line.optString("username")%></td>
<td align="center" nowrap><%=TabStor.getListVal(TabStor.tab_channel, "code='"+line.optString("channel")+"'", "name")%>(<%=line.optString("channel")%>)</td>
<td align="center" nowrap><%=TabStor.getListVal(TabStor.tab_platform, "code='"+line.optString("platform")+"'", "name")%>(<%=line.optString("platform")%>)</td>
<td align="center" nowrap><%=ServerBAC.getInstance().getNameById(line.optInt("serverid"))%>(<%=line.optInt("serverid")%>)</td>
<td align="center" nowrap><%=PlayerBAC.getInstance().getNameById(line.optInt("playerid"))%>(<%=line.optInt("playerid")%>)</td>
<td align="center" nowrap><%=line.optString("logintime")%></td>
<td align="center" nowrap><%=line.optString("regtime")%></td>
<td align="center" nowrap><%if(line.optInt("devuser")==1){%><font color="#006633">是</font><%}else{%><font color="#FF0000">否</font><%}%></td>
<td align="center" nowrap><%if(line.optInt("enable")==1){%><font color="#006633">是</font><%}else{%><font color="#FF0000">否</font><%}%></td>
<td align="center" nowrap><img src="../images/icon_modify.gif" width="16" height="16" alt="Modify" style="cursor:hand" onClick="modify(<%=id%>)"></td>
                            </tr>
                            <%
				}								
				%>
                          </table>
                          <%@ include file="inc_list_bottom.jsp"%>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right"><input name="delid" type="hidden" id="delid">
<table width="50" border="0" cellspacing="0" cellpadding="2">
<tr> 
<td class="btntd" style="cursor:hand" nowrap 
onselectstart="return false" 
onMouseDown="this.className='btntd_mousedown'" 
onMouseUp="this.className='btntd'" 
onMouseOut="this.className='btntd'" onClick="convertopook()" height="21">
<img src="../images/icon_havedispense.gif" width="16" height="16" align="absmiddle">&nbsp;帐号转换
</td>
</tr>
</table>
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
<script>
autoChoose(allValue);
</script>
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
</html>
