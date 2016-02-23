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
AimXML role= roleBAC.getXMLObj("id=" + id);
if(role!=null)
{
role.openRs(RoleBAC.tbName);
role.next();
}
%>
<html>
<head>
<title>Group</title>
<%@ include file="inc_website_icon.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>
<script src="../js/patterns.js"></script>
<script src="../js/checkform.js"></script>
<script>
self.focus();
var haveSubmit=0;
function checkForm(){
var theForm=document.forms[0];
if(theForm.roleName.value=="" || trim(theForm.roleName.value)==""){
 alert("Please input group name");
 theForm.roleName.focus();
 return false;
 }
 if(theForm.roleType.selectedIndex==0){
 alert("请选择组类型");
 theForm.roleType.focus();
 return false;
 }
 theForm.roleName.value=trim(theForm.roleName.value);
 
 theForm.target="hiddenFrame";
 theForm.action="role_input_upload.jsp";
 theForm.submit();
 wait();	
 theForm.target="";
 theForm.action="";

 return true;
}
</script>
<%
if(id!=0){
%>
<script>
var allValue=new Object();
allValue.roleName="<%=role.getRsValue("roleName")%>";
allValue.roleType="<%=role.getRsValue("roleType")%>";
</script>
<%
}
%>
<link href="../css/style1.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="role_input_upload.jsp" onSubmit="return false">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
            <td > <table width="100%" border="0" cellspacing="1" cellpadding="2">
                <tr>
                  <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseout="this.className='btntd'" height="21" onClick="self.close()"><img src="../images/icon_closewindow1.gif" width="16" height="16" align="absmiddle"> 
                          关闭</td>
                      </tr>
                    </table></td>
                </tr>
              </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                  <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="../images/tab_left.gif"></td>
                      <td nowrap background="../images2/tab_midbak.gif"><%if(id==0){%>
                        添加组
                          <%}else{%>
                        修改组
                        <%}%>
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
                      <td>
                        
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr> 
                              <td width="70" align="right" nowrap>组名</td>
                              <td> <input name="roleName" type="text" class="input4" id="roleName" >                              </td>
                            </tr>
                            <tr>
                              <td align="right" nowrap>组类型</td>
                              <td><select name="roleType">
                                <option value="">选择</option>
                                <option value="1">普通用户组</option>
                                <option value="0">管理员组</option>
                              </select>
                                <font color="#FF0000">*</font></td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right"><input name="id" type="hidden" id="id" value="<%=id%>"> 
                                <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseout="this.className='btntd'" height="21" onClick="checkForm()"><input type=image class="submitimg" src="../images/icon_save2.gif" align="absmiddle" width="16" height="16"> 
                                      保存</td>
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
<iframe name="hiddenFrame" width=0 height=0 ></iframe>
</form>
<%if(id!=0){%>
<script>
autoChoose(allValue);
</script>
<%}%>
</body>
</html>
