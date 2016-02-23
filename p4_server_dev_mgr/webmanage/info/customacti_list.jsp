<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="游戏管理";
String perm="游戏活动管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<script src="../js/meizzDate.js"></script>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>
<script src="../js/draw_img.js"></script>
<script>
function del(id)
{
	if(confirm("确认删除吗？"))
	{
		wait();		
		document.getElementById("hiddenFrame").src="customacti_del.jsp?id=" + id;
	}
}
function modify(id)
{
openWindow("customacti_edit.jsp?id=" + id,"modify",1000,800,true,true);
}
function add()
{
openWindow("customacti_edit.jsp","add",1000,800,true,true);
}
function stat()
{
	
	var theForm = document.forms[0];
	wait();
	theForm.submit();
}
</script>
<style type="text/css">
<!--
.style1 {color: #FF0000}
-->
</style>
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
                    <td nowrap background="../images/tab_midbak.gif">游戏活动</td>
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
                <td rowspan="2" bgcolor="#FFFFFF" width="1"><img src="images/spacer.gif" width="1" height="1"></td>
                <td valign="top" align="center"> 
                  <table width="95%" border="0" cellspacing="1" cellpadding="2">
							<tr>
								<td>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td>&nbsp;</td>
									</tr>
								</table>
<%
String actiname = Tools.strNull(request.getParameter("actiname"));
String useserver = Tools.strNull(request.getParameter("useserver"));
String usechannel = Tools.strNull(request.getParameter("usechannel"));
String actistate = Tools.strNull(request.getParameter("actistate"));

//System.out.println(actiname);

JSONObject xml = CustomActivityBAC.getInstance().getJsonObjs(null, "id");
JSONArray list=null;
if(xml!=null) {									
	list = xml.optJSONArray("list");
}
%>
<table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
<tr class="listtopbgc">
<td width="10" align="center" nowrap>No.</td>
<td align="center" nowrap>
<select id="actiname" name="actiname" onChange="stat()">
<option value="">活动名称</option>
<%
JSONArray usenamearr = new JSONArray();
for(int i=0;list!=null && i<list.length();i++) {
	JSONObject line = (JSONObject)list.opt(i);
	if(usenamearr.contains(line.optString("name"))){
		continue;
	}
	usenamearr.add(line.optString("name"));
%>
<option value="<%=line.optString("name")%>"><%=line.optString("name")%></option>
<%
}
%>
</select>
</td>
<td align="center" nowrap>显示时间</td>
<td align="center" nowrap>开始时间</td>
<td align="center" nowrap>结束时间</td>
<td align="center" nowrap>隐藏时间</td>
<td align="center" nowrap>
<select id="useserver" name="useserver" onChange="stat()">
<option value="">举办服务器</option>
<%
DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server);
while(serverRs.next()){
%>
<option value="<%=serverRs.getInt("id")%>"><%=serverRs.getString("name")+"("+serverRs.getInt("id")+")"%></option>
<%
}
%>
</select>
</td>
<td align="center" nowrap>
<select id="usechannel" name="usechannel" onChange="stat()">
<option value="">举办渠道</option>
<%
DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
while(channelRs.next()){
%>
<option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")+"("+channelRs.getString("code")+")"%></option>
<%
}
%>
</select>
</td>
<td align="center" nowrap>
<select id="actistate" name="actistate" onChange="stat()">
<option value="">活动状态</option>
<option value="未显示">未显示</option>
<option value="未开始">未开始</option>
<option value="进行中">进行中</option>
<option value="已结束">已结束</option>
<option value="已隐藏">已隐藏</option>
</select>
</td>
<td align="center" nowrap>界面预览</td>
<td align="center" nowrap>操作</td>
</tr>
<script>
document.forms[0].actiname.value = "<%=actiname%>";
document.forms[0].useserver.value = "<%=useserver%>";
document.forms[0].usechannel.value = "<%=usechannel%>";
document.forms[0].actistate.value = "<%=actistate%>";
</script>
<%
int no=1;
for(int i=0;list!=null && i<list.length();i++) {
	JSONObject line = (JSONObject)list.opt(i);

String stateStr = null;
long t1 = MyTools.getTimeLong(line.optString("showtime").replace('/', '-'));
long t2 = MyTools.getTimeLong(line.optString("starttime").replace('/', '-'));
long t3 = MyTools.getTimeLong(line.optString("endtime").replace('/', '-'));
long t4 = MyTools.getTimeLong(line.optString("hidetime").replace('/', '-'));
long currtime=System.currentTimeMillis();
if(currtime<t1){
	stateStr="未显示";
} else 
if(currtime<t2){
	stateStr="未开始";
} else 
{
	if(t4!=0){
		if(currtime<t3){
			stateStr="进行中";
		} else 
		if(currtime<t4){
			stateStr="已结束";
		} else 
		{
			stateStr="已隐藏";
		}
	} else {
		stateStr="进行中";
	}
}

	if(!actiname.equals("") && !actiname.equals(line.optString("name"))){
		continue;
	}
	if(!useserver.equals("") && !line.optString("server").equals("0") && !line.optString("server").contains("|"+useserver+"|")){
		continue;
	}
	if(!usechannel.equals("") && !line.optString("channel").equals("0") && !line.optString("channel").contains("|"+usechannel+"|")){
		continue;
	}
	if(!actistate.equals("") && !actistate.equals(stateStr)){
		continue;
	}
%>
<tr class="nrbgc1">
<td align="center" nowrap><%=no++%></td>
<td align="center" nowrap><%=line.optString("name")%></td>
<td align="center" nowrap><%=line.optString("showtime")%></td>
<td align="center" nowrap><%=line.optString("starttime")%></td>
<td align="center" nowrap><%=line.optString("endtime")%></td>
<td align="center" nowrap><%=line.optString("hidetime")%></td>
<td align="center" nowrap>
<%
String serverStr = null;
if(line.optString("server").equals("0")){
	serverStr = "所有服务器";
} else 
if(Tools.splitStr(line.optString("server"), "|").length > 1){
	serverStr = "多个服务器";
} else 
{
	serverStr = TabStor.getListVal(ServerBAC.tab_server, "id="+Tools.splitStr(line.optString("server"), "|")[0], "name");
}
%>
<%=serverStr%>
</td>
<td align="center" nowrap>
<%
String channelStr = null;
if(line.optString("channel").equals("0")){
	channelStr = "所有渠道";
} else 
if(Tools.splitStr(line.optString("channel"), "|").length > 1){
	channelStr = "多个渠道";
} else 
{
	channelStr = TabStor.getListVal(TabStor.tab_channel, "code="+Tools.splitStr(line.optString("channel"), "|")[0], "name");
}
%>
<%=channelStr%>
</td>
<td align="center" nowrap>
<%=stateStr%>
</td>
<td align="center" nowrap>查看</td>
<td align="center" nowrap>
<img src="../images/icon_modify.gif" alt="修改" align="absmiddle" style="cursor: hand" onClick="modify(<%=line.optInt("id")%>)">
<img src="../images/icon_del2.gif" alt="删除" align="absmiddle" style="cursor: hand" onClick="del(<%=line.optInt("id")%>)">
</td>
</tr>
<%
}
%>
</table>
								<br>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td align="right">
										<table width="50" border="0" cellspacing="0" cellpadding="2">
											<tr>
												<td height="21" nowrap class="btntd" style="cursor: hand"
													onMouseDown="this.className='btntd_mousedown'"
													onMouseUp="this.className='btntd'"
													onMouseOut="this.className='btntd'"
													onselectstart="return false" onClick="add()"><img
													src="../images/icon_adddepart.gif" width="16" height="16"
													align="absmiddle"> 添加</td>	
											</tr>
								</table>
								</td>
							</tr>
						</table>
						</td>						
					</tr>

				</table>			
				
                </td>
                <td rowspan="2" bgcolor="#848284" width="1"><img src="images/spacer.gif" width="1" height="1"></td>
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
