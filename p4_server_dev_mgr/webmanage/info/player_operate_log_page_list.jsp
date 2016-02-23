<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="玩家管理";
String perm="玩家操作日志";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
	int defaultRow = Tools.str2int(request.getParameter("rows"));
	if (defaultRow <= 0){
		defaultRow = 10;
	}
	int showpage = Tools.str2int(request.getParameter("page"));
	String act = Tools.strNull(request.getParameter("act"));
	String pname = Tools.strNull(request.getParameter("pname"));
	//System.out.println("act:"+act+" pname:"+pname);
	SqlString whereSqlStr = new SqlString();
	if(!act.equals("")){
		whereSqlStr.add("act", act);
	}
	if(!pname.equals("")){
		String playerid = TabStor.getDataVal(PlayerBAC.tab_player, "name='"+pname+"'", "id");
		whereSqlStr.add("playerid", playerid);
	}
	String where = null;
	if(whereSqlStr.getColCount() > 0){
		where = whereSqlStr.whereString();
	}
	//System.out.println("where:"+where);
	JSONObject xml = PlayerOperateLogBAC.getInstance().getJsonPageList(where, "savetime desc", showpage, defaultRow);
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
</head>

<script>
var allValue=new Object();
allValue.rows="<%=defaultRow%>";
allValue.act="<%=act%>";
allValue.pname="<%=pname%>";
</script>

<script>
function stat() {
	var theForm = document.forms[0];
	wait();
	theForm.submit();
}
</script>

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
                    <td nowrap background="../images/tab_midbak.gif">操作日志</td>
                    <td><img src="../images/tab_right.gif" width="6" height="22"></td>
                  </tr>
                </table></td>
                <td valign="bottom" > 
                  <table width="100%" border="0" cellspacing="1" cellpadding="2">
					<tr>
						<td>&nbsp;
						
						</td>
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
                        <table width="100%" border="0" cellspacing="1" cellpadding="2">
					<tr>
						<td>
						操作:<select name="act" id="act">
							<option value="">选择操作</option>
							<%
							for(int i = 0; i < PlayerOperateLogBAC.actStr.length; i++){
							%>
							<option value="<%=i%>"><%=PlayerOperateLogBAC.actStr[i] %></option>
							<%
							}
							%>
						</select> 
						角色名: <input name="pname" id="pname"> 
						<input name="statBtn" type="button" id="statBtn" value="查询" onClick="stat()">
						</td>
					</tr>
				  </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td>&nbsp;</td>
                            </tr>
                          </table>
						  
                          <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                            <tr align="center" class="listtopbgc"> 
                              <td >No.</td>
                              <td align="center" nowrap>角色名</td>
<td align="center" nowrap>服务器</td>
<td align="center" nowrap>动作类型</td>
<td align="center" nowrap>有效期</td>
<td align="center" nowrap>操作说明</td>
<td align="center" nowrap>操作者</td>
<td align="center" nowrap>操作者IP</td>
<td align="center" nowrap>操作时间</td>
                            </tr>
                            <%							
							
							int count = 0;
							JSONArray list=null;
							if(xml!=null)
							{
								count=(xml.optInt("rsPageNO")-1)*10+1;
								list = xml.optJSONArray("list");
							}
							
							for(int i=0;list!=null && i<list.length();i++)
							{
								JSONObject line = (JSONObject)list.opt(i);
								int id = line.optInt("id");			
							%>
                            <tr class="nrbgc1">
                              <td align="center" nowrap><%=count++%></td>
<td align="center" nowrap><%=PlayerBAC.getInstance().getNameById(line.optInt("playerid")) %></td>
<td align="center" nowrap><%=ServerBAC.getInstance().getNameById(line.optInt("serverid")) %></td>
<td align="center" nowrap><%=PlayerOperateLogBAC.actStr[line.optInt("act")] %></td>
<td align="center" nowrap><%=line.optString("actnote")%></td>
<td align="center" nowrap><%=line.optString("note")%></td>
<td align="center" nowrap><%=line.optString("username")%></td>
<td align="center" nowrap><%=line.optString("ip")%></td>
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
                              &nbsp;
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
