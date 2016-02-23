<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="游戏管理";
String perm="平台礼包码";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
String channel = request.getParameter("channel");
String serverId = request.getParameter("serverId");
String gived = request.getParameter("gived");
String giftcode = request.getParameter("giftcode");
String publish =  request.getParameter("publish");
String repeat = request.getParameter("repeat");
SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("礼包领取码","tab_platform_gift_code.code",new String[]{"等于"});
tmpWhereColumn.add("角色名","playerName",new String[]{"等于"});
tmpWhereColumn.add("手机号","tab_platform_gift_code.phonenumber",new String[]{"等于"});
WhereColumn[] theWhereColumn=tmpWhereColumn.getWhereColumns();

OrderColumn tmpOrderColumn=new OrderColumn();
tmpOrderColumn.add("录入顺序","tab_platform_gift_code.id");

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

PlatformGiftCodeBac platformGiftCodeBAC = PlatformGiftCodeBac.getInstance();
	
JSONObject xml=platformGiftCodeBAC.getPageList(pageContext);
//System.out.println(xml);
	
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
allValue.serverId="<%=serverId%>";
allValue.gived="<%=gived%>";
allValue.giftcode="<%=giftcode%>";
allValue.publish="<%=publish%>";
allValue.repeat="<%=repeat%>";
</script>
<script>
function add()
{
	var w=340,h=515,newwindow;
	var url="platform_gift_code_edit.jsp";
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function modify(id)
{
	var w=340,h=515,newwindow;
	var url="platform_gift_code_edit.jsp?id="+id;
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function del(id){
	if(confirm("确定删除吗？"))
	{		
		document.getElementById("hiddenFrame").src="platform_gift_code_del.jsp?id=" + id;
		wait();
	}
}
function batchAdd()
{
	var w=435,h=378,newwindow;
	var url="platform_gift_code_batchadd.jsp";
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function reloadPage()
{
document.forms[0].submit();
wait();
}
function delAll(){
	if(confirm("确定删除全部礼包码吗？"))
	{		
		document.getElementById("hiddenFrame").src="platform_gift_code_delall.jsp";
		wait();
	}
}

function getCode()
{
	if(document.getElementById("exportChannel").value=="")
	{
		alert("请选择渠道");
		document.getElementById("exportChannel").focus();
		return;
	}

	if(document.getElementById("exportGift").value=="")
	{
		alert("请选择礼包");
		document.getElementById("exportGift").focus();
		return;
	}

	if(document.getElementById("exportAmount").value=="")
	{
		alert("请输入数量");
		return;
	}
	if(confirm("确定获取"+document.getElementById("exportAmount").value+"个平台礼包激活码吗？"))
	{
	
		var link="platform_gift_code_export_excel.jsp?amount="+document.getElementById("exportAmount").value+"&channel="+document.getElementById("exportChannel").value+"&gift="+document.getElementById("exportGift").value;		
		location.href=link;
		
	}
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
                    <td nowrap background="../images/tab_midbak.gif">平台礼包码</td>
                    <td><img src="../images/tab_right.gif" width="6" height="22"></td>
                  </tr>
                </table></td>
                <td valign="bottom" > 
                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td></td>
                    </tr>
                  </table>
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
                      <td><%@ include file="inc_list_top.jsp"%>
                          <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                            <tr align="center" class="listtopbgc"> 
                              <td >No.</td>
                              <td >id</td>
                              <td align="center" nowrap>
								<select name="channel" id="channel" onChange="reloadPage()">
							  <option value="">联运渠道</option>
							  <%
							  DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
								while(channelRs.next()){
									  %>
							  <option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")%>(<%=channelRs.getString("code")%>)</option>
							  <%
							  }
							  %>
							</select></td>
								<td align="center" nowrap><%JSONObject serverListData = ServerBAC.getInstance().getJsonObjs("id,name",null,"id ASC");
										%>
                                <select name="serverId" id="serverId" onChange="reloadPage()">
                                  <option value="">游戏服</option>
                                  <%if(serverListData!=null){
										  JSONArray serverList = serverListData.optJSONArray("list");
										  for(int i=0;i<serverList.length();i++)
										  {
										  JSONObject lineData = serverList.optJSONObject(i);
										  %>
                                  <option value="<%=lineData.optInt("id")%>"><%=lineData.optString("name")%></option>
                                  <%}											
											}%>
                              </select></td>
								<td align="center" nowrap>礼包领取码</td>
								<td align="center" nowrap>
                                <select name="giftcode" id="giftcode" onChange="reloadPage()">
                                  <option value="">选择礼包</option>
                                  <%
                                   DBPsRs platformgiftRs = DBPool.getInst().pQueryS(TabStor.tab_platform_gift);
                                  while(platformgiftRs.next()){
										  %>
                                  <option value="<%=platformgiftRs.getInt("num")%>"><%=platformgiftRs.getString("name")%></option>
                                  <%
                                  }
                                  %>
                              </select></td>
								<td align="center" nowrap>角色</td>
								<td align="center" nowrap>手机号</td>
								<td align="center" nowrap><select name="gived" id="gived" class="select1" onChange="reloadPage()">
                                    <option value="">是否已领取</option>
                                    <option value="1">已领取</option>
                                    <option value="0">未领取</option>
                                </select></td>
								<td align="center" nowrap>领取时间</td>
								<td align="center" nowrap><select name="publish" id="publish" class="select1" onChange="reloadPage()">
                                    <option value="">是否已分发</option>
                                    <option value="1">已分发</option>
                                    <option value="0">未分发</option>
                                </select></td>
								<td align="center" nowrap>分发时间</td>
								
							    <td align="center" nowrap>过期日期</td>
							    <td align="center" nowrap><select name="repeat" id="repeat" class="select1" onChange="reloadPage()">
                                  <option value="">是否共用</option>
                                  <option value="1">是</option>
                                  <option value="0">否</option>
                                </select>
								</td>
								<td align="center" nowrap>操作者</td>
								<td align="center" nowrap>生成时间</td>
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
                              <td align="center" nowrap><%=line.optInt("id")%></td>
                              <td align="center" nowrap><%=line.optString("channelname")%>(<%=line.optString("platform")%>)</td>
							  <td align="center" nowrap><%=line.optString("servername")%></td>
							  <td align="center" nowrap><%=line.optString("code")%></td>
							<td align="center" nowrap><%=line.optString("giftname")%></td>
							<td align="center" nowrap><%=line.optString("playername")%></td>
							<td align="center" nowrap><%=line.optString("phonenumber")%></td>
							<td align="center" nowrap><%if(line.optInt("gived")==1){%><font color="#006633">是</font><%}else{%><font color="#FF0000">否</font><%}%></td>
							<td align="center" nowrap><%=line.optString("givetime")%></td>
							<td align="center" nowrap><%if(line.optInt("publish")==1){%><font color="#006633">是</font><%}else{%><font color="#FF0000">否</font><%}%></td>
							<td align="center" nowrap><%=line.optString("publishtime")%></td>
							
							<td align="center" nowrap><%=Tools.strdate2shortstr(line.optString("expiretime"))%></td>
							<td align="center" nowrap><%if(line.optInt("repeat")==1){%><font color="#006633">是</font><%}else{%><font color="#FF0000">否</font><%}%></td>
							<td align="center" nowrap><%=line.optString("createuser")%></td>
							<td align="center" nowrap><%=line.optString("createtime")%></td>
							<td align="center" nowrap><img src="../images/icon_modify.gif" width="16" height="16" alt="Modify" style="cursor:hand" onClick="modify(<%=id%>)"> <img src="../images/icon_del2.gif" width="16" height="16" alt="Delete" style="cursor:hand" onClick="del(<%=id%>)"></td>
                            </tr>
                            <%
				}								
				%>
                          </table>
                          <%@ include file="inc_list_bottom.jsp"%>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right"><table border="0" cellspacing="1" cellpadding="2">
                                <tr>
                                  <td align="right"></td>
                                  <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                      <tr>
                                        <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="batchAdd()" height="21"><img src="../images/icon_adddepart.gif" width="16" height="16" align="absmiddle"> 批量添加</td>
                                      </tr>
                                  </table></td>
                                  <td align="right">&nbsp;</td>
                                  <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                      <tr>
                                        <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="delAll()" height="21"><img src="../images/delall.png" align="absmiddle"> 全部删除</td>
                                      </tr>
                                  </table></td>
                                </tr>
                              </table></td>
                            </tr>
                            <tr>
                              <td align="right">	<table border="0" cellspacing="1" cellpadding="2">
                                                                        <tr>
																		
                                                                          <td><select name="exportChannel" id="exportChannel">
                                                                            <option value="">联运渠道</option>
                                                                            <%
                                                                            channelRs.beforeFirst();
																			while(channelRs.next()){
																		  %>
                                                                            <option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")%>(<%=channelRs.getString("code")%>)</option>
                                                                            <%
                                                                            }
                                                                            %>
                                                                          </select></td>
																		 
                                                                          <td><select name="exportGift" id="exportGift" >
                                                                            <option value="">选择礼包</option>
                                                                            <%
                                                                            platformgiftRs.beforeFirst();
																			  while(platformgiftRs.next())
																			  {
																			  %>
                                                                            <option value="<%=platformgiftRs.getInt("num")%>"><%=platformgiftRs.getString("name")%></option>
                                                                            <%
                                                                            }
                                                                            %>
                                                                          </select></td>
                                                                          <td>数量：
                                                                          <input name="exportAmount" type="text" id="exportAmount" size="3"></td>
                                                                          <td><table width="50" border="0" cellspacing="0" cellpadding="2">
                                                                            <tr>
                                                                              <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="getCode()" height="21"><img src="../images/icon_excel.gif" align="absmiddle"> 导出平台礼包激活码</td>
                                                                            </tr>
                                                                          </table></td>
                                                                        </tr>
                                                                      </table>
						      </td>
                            </tr>
                            <tr>
                              <td align="right"><input name="delid" type="hidden" id="delid">
                                <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="add()" height="21"><img src="../images/icon_adddepart.gif" width="16" height="16" align="absmiddle"> 
                                      添加</td>
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
<script>
autoChoose(allValue);
</script>
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
</html>
