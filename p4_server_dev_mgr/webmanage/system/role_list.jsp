<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="权限分组";
%>
<%@ include file="inc_checkperm.jsp"%>
<%
RoleBAC roleBAC = new RoleBAC();
UserRoleBAC userRoleBAC = new UserRoleBAC();
RolePermissionBAC permissionBAC = new RolePermissionBAC();
AimXML xml=roleBAC.getXMLObjs("","id DESC");

	
%>
<html>
<head>
<title>Group manage</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script>
function add(){
var w=250,h=180,newwindow;
var url="role_input.jsp";
var wName="addgroup";
newwindow=window.open(url,wName,"width="+w+",height="+h);
newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
newwindow.focus();
}
function modify(id){
var w=250,h=180,newwindow;
var url="role_input.jsp?id="+id;
var wName="modifygroup";
newwindow=window.open(url,wName,"width="+w+",height="+h);
newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
newwindow.focus();
}
function del(id){
	if(confirm("确定删除该组吗？"))
	{		
		document.getElementById("hiddenFrame").src="role_del.jsp?id="+id;
		wait();
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
                    <td nowrap background="../images/tab_midbak.gif">权限分组</td>
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
                          <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                            <tr align="center" class="listtopbgc"> 
                              <td >No.</td>
                              <td >组名</td>
                              <td >类型</td>
                              <td >权限</td>
                              <td >用户</td>
                              <td >操作</td>
                            </tr>
                            <%
							int i=1;
							if(xml!=null)
							{
							xml.openRs(RoleBAC.tbName);
							while(xml.next())
							{
							String roleName=xml.getRsValue("roleName");
							int id=xml.getRsIntValue("id");
							int roleType=xml.getRsIntValue("roleType");
							%>
                            <tr class="nrbgc1"> 
                              <td align="center" nowrap><%=i++%></td>
                              <td align="center" nowrap><%=roleName%></td>
                              <td align="center" nowrap>
                              <%
                              switch(roleType){
								  case UserBAC.TYPE_MANAGE:
								  	out.print("普通管理员");
								  	break;
								  case UserBAC.TYPE_COMMON:
								  	out.print("普通用户");
								  	break;
							  }
							  %>
							  </td>
                              <td align="center" nowrap title="设置角色权限" style="cursor:hand" onClick="location.replace('role_powersetup.jsp?id=<%=id%>')">
								<%
								int permissionCount = permissionBAC.getCount("roleId="+ id);								
								if(permissionCount<=0){
								%>
                                <font color="#FF0000" style="cursor:hand">未设置</font>
                                <%
								} else {
								%>
                                <font color="#1168a9" style="cursor:hand">已设置</font>
                                <%
								}
								%>
								</td>
                              <td align="center" nowrap title="设置相关人员" style="cursor:hand" onClick="location.replace('role_usersetup.jsp?id=<%=id%>')">
                                <%
								int userCount = userRoleBAC.getCount("roleId="+ id);
								if(userCount > 0){								
								%>
                                <font color="#356C5A" style="cursor:hand"><%=userCount%></font> 
                                <%
								} else {
								%>
                                <font color="#FF0000" style="cursor:hand">0</font> 
                                <%
								}
								%>
							  </td>
                              <td align="center">
                              	<img src="../images/icon_modifydepart.gif" width="16" height="16" alt="修改角色名" style="cursor:hand" onClick="modify(<%=id%>)">
                              	<img src="../images/icon_deldepart.gif" width="16" height="16" alt="删除角色" style="cursor:hand" onClick="del(<%=id%>)">
                              </td>
                            </tr>
                            <%
							}
							}
							%>
                          </table>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right">
                                <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="add()" height="21"><img src="../images/icon_adddepart.gif" width="16" height="16" align="absmiddle"> 
                                      添加组</td>
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
