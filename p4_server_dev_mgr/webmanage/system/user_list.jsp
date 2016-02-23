<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="用户管理";
%>
<%@ include file="inc_checkperm.jsp"%>

<%
SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("用户名","userName",new String[]{"等于","包含"});
tmpWhereColumn.add("用户id","id",new String[]{"等于"});
tmpWhereColumn.add("真名","trueName",new String[]{"等于","包含"});
WhereColumn[] theWhereColumn=tmpWhereColumn.getWhereColumns();

OrderColumn tmpOrderColumn=new OrderColumn();
tmpOrderColumn.add("录入顺序","id");
tmpOrderColumn.add("用户名","userName");

OrderColumn[] theOrderColumn=tmpOrderColumn.getOrderColumns();
%>
<%@include file="inc_list_getparameter.jsp"%>
<%//解析数据
if(colvalue!=null && !colvalue.equals(""))
{
	if(colname!=null)
	{
		if(operator.equals("等于"))
		{
			sqlS.add(colname,colvalue);
		}
		else
		{
			sqlS.add(colname,colvalue,"like");
		}
	}
}

	UserBAC userBAC = new UserBAC();
	
	AimXML xml=userBAC.getXMLPageList(sqlS.whereString(),showorder+" "+ordertype,ToolFunc.str2int(pagenum),ToolFunc.str2int(rpp,10));

	
%>
<html>
<head>
<title>User manage</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script>
function add()
{
	var w=340,h=315,newwindow;
	var url="user_input.jsp";
	var wName="adduser";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function modify(id)
{
	var w=340,h=315,newwindow;
	var url="user_input.jsp?id="+id;
	var wName="modifyuser";
	newwindow=window.open(url,wName,"width="+w+",height="+h);
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function del(id){
	if(confirm("确定删除该用户吗？"))
	{		
		document.getElementById("hiddenFrame").src="user_del.jsp?id="+id;
		wait();
	}
}
function setSubChannel(username)
{
	var w=500,h=500,newwindow;
	var url="user_set_subchannel.jsp?username="+username;
	var wName="setSubChannel";
	newwindow=openWindow(url,wName,w,h,true,true);
	//newwindow=window.open(url,wName,"width="+w+",height="+h);
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
                    <td nowrap background="../images/tab_midbak.gif">系统用户</td>
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
						  <%if(xml!=null)xml.openRs(UserBAC.tbName);%>
                          <%@ include file="inc_list_top.jsp"%>
                          <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                            <tr align="center" class="listtopbgc"> 
                              <td >No.</td>
                              <td nowrap >用户名</td>
                              <td nowrap >真名</td>
                              <td nowrap >类型</td>
                              <td nowrap >状态</td>
                              <td nowrap >用户组</td>
                              <td nowrap >渠道</td>
                              <td nowrap >子渠道</td>
                              <td >操作</td>
                            </tr>
                            <%
							//out.print(xml.getPrettyXml());
							
							int count = 0;
							if(xml!=null)
							{
							count=(xml.rsPageNO-1)*ToolFunc.str2int(rpp)+1;
							}
							
							while(xml!=null && xml.next()){
								int id = xml.getRsIntValue("id");
								String userName = xml.getRsValue("userName");
								String trueName = xml.getRsValue("trueName");
								int userType=xml.getRsIntValue("userType");
								int IsEnable=xml.getRsIntValue("IsEnable");	
								String channel = xml.getRsValue("channel");					
							%>
                            <tr class="nrbgc1">
                              <td align="center" nowrap><%=count++%></td>
                              <td align="center" nowrap><%=userName%></td>
                              <td align="center" nowrap><%=trueName%></td>
                              <td align="center" nowrap><%=UserBAC.getTypeName(userType)%></td>
                              <td align="center"> 
                                <%
								String fontColor="";
								if(IsEnable==1){
									fontColor = "#336633";
								}
								else{
									fontColor = "#FF0000";
								}
								%>
                                <font color="<%=fontColor%>"><%=IsEnable==1? "开通" : "关闭"%></font>
                              </td>
                              <td align="center" style="cursor:hand" alt="Set group" onClick="location.replace('user_rolesetup.jsp?id=<%=id%>')">
                              <%
							  UserRoleBAC userRoleBAC = new UserRoleBAC();
							  int roleCount=userRoleBAC.getCount("userId="+ id);
							  out.print(roleCount);
							  %>
							  </td>
                              <td align="center" >
                              <%
                              if(channel!=null && !channel.equals("")){
                              %>
                              <%=Tools.strNull(TabStor.getListVal(TabStor.tab_channel, "code='"+channel+"'", "name")+"("+channel+")")%>
                              <%
                              }
                              %>
                              </td>
                              <td align="center" ><a href="javascript:setSubChannel('<%=userName%>')"><%=userBAC.getSubChannelCount(userName)%></a></td>
                              <td align="center">
                              	<%
                              	if(userType==UserBAC.TYPE_SYSTEM){
                              	%>
								<img src="../images/icon_modifydepart_disabled.gif">
								<%
								} else {
								%>
								<img src="../images/icon_modifydepart.gif" width="16" height="16" alt="Modify" style="cursor:hand" onClick="modify(<%=id%>)">
								<%
								}
								%>
                                <%if(userType==UserBAC.TYPE_SYSTEM){
                                %>
                                <img src="../images/icon_deldepart_disabled.gif">
                                <%
                                } else {
                                %>
                                <img src="../images/icon_deldepart.gif" width="16" height="16" alt="Delete" style="cursor:hand" onClick="del(<%=id%>)">
                                <%
                                }
                                %>
                                </td>
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
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="add()" height="21"><img src="../images/icon_adddepart.gif" width="16" height="16" align="absmiddle"> 
                                      添加用户</td>
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
</html>
