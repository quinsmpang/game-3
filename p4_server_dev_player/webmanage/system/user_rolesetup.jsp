<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="用户管理";
%>
<%@ include file="inc_checkperm.jsp"%>
<%
int id=ToolFunc.str2int(request.getParameter("id"));
RoleBAC roleBAC = new RoleBAC();
UserBAC userBAC = new UserBAC();
UserRoleBAC userRoleBAC = new UserRoleBAC();

String userName=userBAC.getValue("userName","id="+id);

AimXML roleUserXml=userRoleBAC.getXMLObjs("userId="+ id,"id ASC");
AimXML roleXml=roleBAC.getXMLObjs("id not in (select roleId from tb_baUserRole where userId="+ id +")","id ASC");
%>

<html>
<head>
<title>User group</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script>
function addrole(){
var form1=document.forms[0];
var i;
	if(form1.allrole.selectedIndex>=0)
	{
	  for(i=0;i<form1.allrole.length;i++)
		{
		 if (form1.allrole.options[i].selected)
		 {
			var ooption=document.createElement("option");
			ooption.text=form1.allrole[i].text;
			ooption.value=form1.allrole[i].value;
			form1.selectedrole.add(ooption,form1.selectedrole.length);
			}
		}
		for (i=form1.allrole.length-1;i>=0;i--)
		{
			if (form1.allrole.options[i].selected)
			{
			   form1.allrole.remove(i)
			}
		}		
	}
	else
	{
	if(form1.allrole.length>0){
	alert("请选择要添加的人员！")
	form1.allrole.focus();
	}
	}
	if(form1.selectedrole.length>0)
	 {
	 //document.all.subbutton.disabled=false;
	 }
	if(form1.allrole.length<=0)
	 {
	 //document.all.addbutton.disabled=true;
	 }
}
function delrole(){
	if (form1.selectedrole.selectedIndex>=0)
	{
		for (i=0;i<form1.selectedrole.length;i++)
		{
			if (form1.selectedrole.options[i].selected)
			{				
				var ooption=document.createElement("option")
				ooption.text=form1.selectedrole[i].text
				ooption.value=form1.selectedrole[i].value
				form1.allrole.add(ooption,form1.allrole.length)
			}
		}
		for (i=form1.selectedrole.length-1;i>=0;i--)
		{
			if (form1.selectedrole.options[i].selected)
			{				
				form1.selectedrole.remove(i)
			}
		}		
	}
	else
	{ if(form1.selectedrole.length>0){
	  alert("请选择要删除的人员！")
	  form1.selectedrole.focus();
	  }
	}
	if(form1.selectedrole.length<=0)
	 {
	 //document.all.subbutton.disabled=true;
	 }
	if(form1.allrole.length>0)
	 {
	 //document.all.addbutton.disabled=false;
	 }
}
function saveUserRoleSetup(){
roleInput();
var theForm=document.forms[0];
theForm.target="hiddenFrame";
theForm.action="user_rolesetup_upload.jsp";
theForm.submit();
wait();	
theForm.target="";
theForm.action="";
}

function roleInput(){
var form1=document.forms[0];
 for(var i=0;i<form1.selectedrole.length;i++){
  document.all.rolestore.innerHTML=document.all.rolestore.innerHTML+"<input type=hidden name=userroles value="+form1.selectedrole.options[i].value+">"
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
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif"></td>
                    <td nowrap background="../images2/tab_midbak.gif">组管理</td>
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
                        <td align="center"> <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr> 
                              <td align="right"> <table border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" height="21" onClick="location.replace('user_list.jsp')"><img src="../images/icon_return.gif" width="16" height="16" align="absmiddle"> 
                                      返回</td>
                                  </tr>
                                </table></td>
                            </tr>
                          </table>
                          <table border="0" cellspacing="1" cellpadding="2">
                            <tr> 
                              <td colspan="3">用户名:<font color="#FF0000"><strong><%=userName%></strong></font></td>
                            </tr>
                            <tr> 
                              <td> 
                                
                                <fieldset>
                                <legend>所在组</legend>
                                <select name="selectedrole" size="12" multiple class="rolelist" id="selectedrole" ondblclick="delrole()">
                                 <%
								if(roleUserXml!=null)
								{
								roleUserXml.openRs(UserRoleBAC.tbName);
								while(roleUserXml.next())
								{                         
                                %>
                                  <option value="<%=roleUserXml.getRsIntValue("roleId")%>"><%=roleBAC.getValue("roleName","id="+roleUserXml.getRsIntValue("roleId"))%></option>
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
                                          <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="addrole()" height="21"><img src="../images/icon_prevstep.gif" width="16" height="16" align="absmiddle"> 
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
                                          <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="delrole()" height="21"><img src="../images/icon_nextstep.gif" width="16" height="16" align="absmiddle"> 
                                            删除</td>
                                        </tr>
                                      </table></td>
                                  </tr>
                                </table></td>
                              <td> 
                                
                                <fieldset>
                                <legend>其他组</legend>
                                <select name="allrole" size="12" multiple class="rolelist" id="allrole" onDblClick="addrole()">
                                 <%
								if(roleXml!=null)
								{
								roleXml.openRs(RoleBAC.tbName);
								while(roleXml.next())
								{                         
                                %>
                                  <option value="<%=roleXml.getRsIntValue("id")%>"><%=roleBAC.getValue("roleName","id="+roleXml.getRsIntValue("id"))%></option>
                                  <%
								  	}
								  }							
								  %>
                                </select>
                                </fieldset></td>
                            </tr>
                          </table>                           
						  <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="center"><span id=rolestore></span><input name="userId" type="hidden" value="<%=id%>">
                                <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="saveUserRoleSetup()" height="21"><input type=image src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 
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
