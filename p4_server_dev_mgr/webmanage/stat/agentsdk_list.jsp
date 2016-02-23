<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="财务报表";
String perm="SDK渠道充值";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<script src="../js/meizzDate.js"></script>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>

<script>
//查询
function query(){
	var theForm = document.forms[0];
	wait();
	theForm.submit();
}
//保存修改
function saveedit(iaa_id){
	var theForm = document.forms[0];
	theForm.edit_iaa_id.value=iaa_id;
	wait();
	theForm.submit();
}
/*
function del(id)
{
	if(confirm("确认删除吗？"))
	{
		wait();		
		document.getElementById("hiddenFrame").src="agentsdk_del.jsp?id=" + id;
	}
}
*/
/*
function modify(id)
{
openWindow("agentsdk_edit.jsp?id=" + id,"modify",500,500,true,true);
}
*/
/*
function add()
{
openWindow("agentsdk_edit.jsp","add",500,500,true,true);
}
*/
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
                    <td nowrap background="../images/tab_midbak.gif">SDK渠道充值</td>
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
										<td><%
										//System.out.print("MyTools.getTimeStr().substring(0,5):"+MyTools.getTimeStr().substring(0,4));
										int curryear = Tools.str2int(MyTools.getTimeStr().substring(0,4));
										//System.out.println("MyTools.getTimeStr().substring(6,9):"+MyTools.getTimeStr().substring(5,7));
										int currmoonth = Tools.str2int(MyTools.getTimeStr().substring(5,7));
										
										String sel_yearStr = request.getParameter("yearlist");
										String sel_moonthStr = request.getParameter("moonthlist");
										String sel_channel = request.getParameter("channellist");
										if(sel_yearStr == null){
											sel_yearStr = ""+curryear;
											sel_moonthStr = (currmoonth<10?"0":"")+currmoonth;
											sel_channel = "";
										}
										int edit_iaa_id = Tools.str2int(request.getParameter("edit_iaa_id"));
										if(edit_iaa_id != 0){
											AgtentSDKBAC.getInstance().save(pageContext);
										}
										%>
										<label>
										  <select name="yearlist">
										  <%
										  for(int i = 0; i < 10; i++){
										  String yearStr = ""+(curryear-i);
										  String select = yearStr.equals(sel_yearStr)?"selected='selected'":"";
										  %>
										  <option value="<%=yearStr%>" select><%=yearStr%></option>
										  <%
										  }
										  %>
									    </select>
										</label>
										年
										<label>
										  <select name="moonthlist">
										  <%
										  for(int i = 1; i <= 12; i++){
										  String moonthStr = (i<10?"0":"")+i;
										  //System.out.println(currmoonth+" "+moonthStr);
										  String select = moonthStr.equals(sel_moonthStr)?"selected='selected'":"";
										  //System.out.println("select:"+select);
										  %>
										  <option value="<%=moonthStr%>" <%=select%>><%=moonthStr%></option>
										  <%
										  }
										  %>
									    </select>
										</label>
										月
										<label>
										<select name="channellist" >
										<option value="" <%=sel_channel.equals("")?"selected='selected'":""%>>选择联运渠道</option>
										<%
										DBPsRs channelRs = DBPool.getInst().pQueryS("tab_channel");
										while(channelRs.next()){
										%>
<option value="<%=channelRs.getString("name")%>" <%=sel_channel.equals(channelRs.getString("name"))?"selected='selected'":""%>><%=channelRs.getString("code")+"-"+channelRs.getString("name")%></option>
										<%
										}
										%>
									    </select>
										</label>
										<label>
										<input type="submit" name="query" value="查询" onClick="query()">
										<input type="hidden" name="edit_iaa_id">
										<input type="hidden" name="operator_name" value="<%=userObj.getString("username")%>">
										</label></td>
									</tr>
								</table>
								
								<table width="100%" border="0" cellpadding="2" cellspacing="1"
									class="tbbgc1">
									<tr class="listtopbgc">
										<td width="10" align="center" nowrap>No.</td>
										<td align="center" nowrap>渠道名称</td>
<td align="center" nowrap>时间</td>
<td align="center" nowrap>后台总金额</td>
<td align="center" nowrap>渠道后台金额</td>
<td align="center" nowrap>渠道费</td>
<td align="center" nowrap>税费</td>
<td align="center" nowrap>渠道分成比例</td>
<td align="center" nowrap>世熠收入</td>
<td align="center" nowrap>操作者</td>
<td align="center" nowrap>最后更新时间</td>
										<td align="center" nowrap>操作</td>
									</tr>
									
									<%
									//System.out.println("time:"+time);
									int num=1;
									String where = "infull_date='"+sel_yearStr+"-"+sel_moonthStr+"'";
									if(!sel_channel.equals("")){
										where += " and agent_name='"+sel_channel+"'";
									}
									//System.out.println(where);
								  	JSONObject xml = AgtentSDKBAC.getInstance().getJsonObjs(where,"iaa_id");
									JSONArray list=null;
									if(xml!=null)
									{									
										list = xml.optJSONArray("list");
									}
									for(int i=0;list!=null && i<list.length();i++)
									{
									JSONObject line = (JSONObject)list.opt(i);
									%>
									<tr class="nrbgc1">
										<td align="center" nowrap><%=num++%></td>
										<td align="center" nowrap><%=line.optString("agent_name")%></td>
<td align="center" nowrap><%=line.optString("infull_date")%></td>
<td align="center" nowrap><%=line.optString("total_infull")%></td>
<td align="center" nowrap><input name="agent_infull<%=line.getInt("iaa_id")%>" type="text" size="5" value="<%=line.optString("agent_infull")%>"></td>
<td align="center" nowrap><input name="agent_rate<%=line.getInt("iaa_id")%>" type="text" size="5" value="<%=line.optString("agent_rate")%>"></td>
<td align="center" nowrap><input name="suidian<%=line.getInt("iaa_id")%>" type="text" size="5" value="<%=line.optString("suidian")%>"></td>
<td align="center" nowrap><input name="agent_cps<%=line.getInt("iaa_id")%>" type="text" size="5" value="<%=line.optString("agent_cps")%>"></td>
<td align="center" nowrap><%=line.optString("sy_infull")%></td>
<td align="center" nowrap><%=line.optString("operator_name")%></td>
<td align="center" nowrap><%=line.optString("last_time")%></td>
										
										<td align="center" nowrap>
								<table width="50" border="0" cellspacing="0" cellpadding="2">
                                    <tr>
                                      <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="saveedit(<%=line.getInt("iaa_id")%>)"><img src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 保存</td>
                                    </tr>
                                  </table>
											</td>
									</tr>
									<%
									}
									%>
								</table>
								<br>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td align="right">&nbsp;
										
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
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
</html>
