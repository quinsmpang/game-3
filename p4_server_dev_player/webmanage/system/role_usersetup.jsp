<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="权限分组";
%>
<%@ include file="inc_checkperm.jsp"%>
<%
int id=ToolFunc.str2int(request.getParameter("id"));
RoleBAC roleBAC = new RoleBAC();
UserBAC userBAC = new UserBAC();
UserRoleBAC userRoleBAC = new UserRoleBAC();

String roleName=roleBAC.getValue("roleName","id="+id);

AimXML roleUserXml=userRoleBAC.getXMLObjs("roleId="+ id,"id ASC");
AimXML userXml=userBAC.getXMLObjs("id not in (select userId from tb_baUserRole where roleId="+ id +")","id ASC");
%>
<html>
<head>
<title>Group user</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script>
function addstaff(){
var form1=document.forms[0];
var i;
	if(form1.allstaff.selectedIndex>=0)
	{
	  for(i=0;i<form1.allstaff.length;i++)
		{
		 if (form1.allstaff.options[i].selected)
		 {
			var ooption=document.createElement("option");
			ooption.text=form1.allstaff[i].text;
			ooption.value=form1.allstaff[i].value;
			form1.selectedstaff.add(ooption,form1.selectedstaff.length);
			}
		}
		for (i=form1.allstaff.length-1;i>=0;i--)
		{
			if (form1.allstaff.options[i].selected)
			{
			   form1.allstaff.remove(i)
			}
		}		
	}
	else
	{
	if(form1.allstaff.length>0){
	alert("请选择要添加的人员！")
	form1.allstaff.focus();
	}
	}
	if(form1.selectedstaff.length>0)
	 {
	 //document.all.subbutton.disabled=false;
	 }
	if(form1.allstaff.length<=0)
	 {
	 //document.all.addbutton.disabled=true;
	 }
}
function delstaff(){
	if (form1.selectedstaff.selectedIndex>=0)
	{
		for (i=0;i<form1.selectedstaff.length;i++)
		{
			if (form1.selectedstaff.options[i].selected)
			{				
				var ooption=document.createElement("option")
				ooption.text=form1.selectedstaff[i].text
				ooption.value=form1.selectedstaff[i].value
				form1.allstaff.add(ooption,form1.allstaff.length)
			}
		}
		for (i=form1.selectedstaff.length-1;i>=0;i--)
		{
			if (form1.selectedstaff.options[i].selected)
			{				
				form1.selectedstaff.remove(i)
			}
		}		
	}
	else
	{ if(form1.selectedstaff.length>0){
	  alert("请选择要删除的人员！")
	  form1.selectedstaff.focus();
	  }
	}
	if(form1.selectedstaff.length<=0)
	 {
	 //document.all.subbutton.disabled=true;
	 }
	if(form1.allstaff.length>0)
	 {
	 //document.all.addbutton.disabled=false;
	 }
}
function saveRoleUserSetup(){
var theForm=document.forms[0];
staffInput();

theForm.target="hiddenFrame";
theForm.action="role_usersetup_upload.jsp";
theForm.submit();
wait();	
theForm.target="";
theForm.action="";
}
function staffInput(){
var form1=document.forms[0];
 for(var i=0;i<form1.selectedstaff.length;i++){
  document.all.staffstore.innerHTML=document.all.staffstore.innerHTML+"<input type=hidden name=rolestaff value="+form1.selectedstaff.options[i].value+">"
 } 
 return;
}
</script>

</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="" onSubmit="return false">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
            <td > 
			<table width="100%" border="0" cellspacing="2" cellpadding="1">
  <tr>
    <td align="right"><table border="0" cellspacing="0" cellpadding="2">
      <tr>
        <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" height="21" onClick="location.replace('role_list.jsp')"><img src="../images/icon_return.gif" width="16" height="16" align="absmiddle"> 返回</td>
      </tr>
    </table></td>
  </tr>
</table>

              <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif"></td>
                    <td nowrap background="../images2/tab_midbak.gif">
                     组用户
                    
                    </td>
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
                        <td align="center"> 组名:<font color="#FF0000"><strong><%=roleName%></strong></font> 
                          
                          <table border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td>
							  <fieldset><legend>已选人员</legend>
                                <select name="selectedstaff" size="12" multiple class="rolelist" id="selectedstaff" onDblClick="delstaff()">
                                <%
								if(roleUserXml!=null)
								{
								roleUserXml.openRs(UserRoleBAC.tbName);
								while(roleUserXml.next())
								{      
								int userId =  roleUserXml.getRsIntValue("userId");
								String userName = userBAC.getValue("userName","id="+userId);
								String trueName = Tools.strNull(userBAC.getValue("trueName","id="+userId));
                                %>
								<option value="<%=userId%>"><%=userName%>(<%=trueName%>)</option>
								<%
								}
								}							
								%>
								</select>
                                </fieldset></td>
                              <td width="80" align="center"><table border="0" cellspacing="1" cellpadding="2">
                                  <tr> 
                                    <td align="center"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                        <tr> 
                                          <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="addstaff()" height="21"><img src="../images/icon_prevstep.gif" width="16" height="16" align="absmiddle"> 
                                            加入</td>
                                        </tr>
                                      </table></td>
                                  </tr>
                                  <tr>
                                    <td align="center">&nbsp;</td>
                                  </tr>
                                  <tr> 
                                    <td align="center"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                        <tr> 
                                          <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="delstaff()" height="21"><img src="../images/icon_nextstep.gif" width="16" height="16" align="absmiddle"> 
                                            删除</td>
                                        </tr>
                                      </table></td>
                                  </tr>
                                </table></td>
                              <td>			
							  <fieldset><legend>备选人员</legend>
                                <select name="allstaff" size="12" multiple class="rolelist" id="allstaff" onDblClick="addstaff()">
								<%
								if(userXml!=null)
								{
								userXml.openRs(UserBAC.tbName);
								while(userXml.next())
								{                         
                                %>
								<option value="<%=userXml.getRsIntValue("id")%>"><%=userBAC.getValue("userName","id="+userXml.getRsIntValue("id"))%></option><%
								}
								}								
								%>
                                </select>
                                </fieldset></td>
                            </tr>
                          </table>                           
						  <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="center"><span id=staffstore></span><input name="roleId" type="hidden" id="roleId" value="<%=id%>">
                                <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="saveRoleUserSetup()" height="21"><input type=image src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 
                                      保存</td>
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
<iframe name="hiddenFrame" width="0" height="0"></iframe>
</body>
</html>
