<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ page import="server.config.ServerConfig"%>
<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="权限分组";
%>
<%@ include file="inc_checkperm.jsp"%>
<%
//SysProperties theSysProp = SysProperties.getInstance();
String setupFilePath = ServerConfig.getPermissionXmlPath();

Permission.initPermission(setupFilePath);

int roleId = ToolFunc.str2int(request.getParameter("id"));
RoleBAC roleBAC = new RoleBAC();
String roleName=roleBAC.getValue("roleName","id="+roleId);

AimXML xml=roleBAC.getXMLObj("id="+ roleId);

Perm[] permArr = roleBAC.getPermissions(roleId);

%>
<html>
<head>
<title>permission setup</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script>
function saverolesetup()
{
gatherPermission();
 var theForm=document.forms[0];
 theForm.target="hiddenFrame";
 theForm.action="role_powersetup_upload.jsp";
 theForm.submit();
 wait();
 theForm.target="";
 theForm.action="";
}
function gatherPermission()
{
	var permStr="";
	if(document.all.actcode.length==null)
	{
		if(document.all.actcode.checked)
		{
			if(permStr!="")permStr += "|"
			permStr += document.all.actcode.getAttribute("module") + "|" + document.all.actcode.value;
		}
	}
	else
	{
		for(var i=0;document.all.actcode!=null && i<document.all.actcode.length;i++)
		{
			if(document.all.actcode[i].checked)
			{
				if(permStr!="")permStr += "|"
				permStr += document.all.actcode[i].getAttribute("module") + "|" + document.all.actcode[i].value;
			}
		}		
	}
	document.all.permStr.value=permStr;
	//alert(document.all.permStr.value);
}

function chooseAll(obj){
	var rowArr = obj.parentElement.parentElement.parentElement.children;
	for(var i=0;i<rowArr.length;i++){
		var tdArr = rowArr[i].children;
		for(var j=0; j<tdArr.length; j++){
			var inputArr = tdArr[j].children;
			for(var k=0; k<inputArr.length; k++){
				if(inputArr[k].tagName=="INPUT"){
					inputArr[k].checked=obj.checked;
				}
			}
		}	
	}
	setSelectValue();
}

function selectAll(){
	chooseObj = document.forms[0].chooseall;
	storageObj = document.forms[0].storage;
	actObj = document.forms[0].actcode;
	selectObj = document.forms[0].selectall;
	if(chooseObj.length==null)
	{
		chooseObj.checked=selectObj.checked;
	}else
	{
		for(i=0;i<chooseObj.length;i++){
			chooseObj[i].checked=selectObj.checked;
		}
	}
	if(actObj.length==null)
	{
		actObj.checked=selectObj.checked;
	}else
	{
		for(i=0;i<actObj.length;i++){
			actObj[i].checked=selectObj.checked;
		}
	}
	if(storageObj.length!=null)
	{
		storageObj.checked=selectObj.checked;
	}else
	{
		for(i=0;storageObj!=null && i<storageObj.length;i++){
			storageObj[i].checked=selectObj.checked;
		}
	}
	
}

function setSelectValue(){
if(document.forms[0].chooseall.length==null)
{
	if(!document.forms[0].chooseall.checked)
	{
		document.forms[0].selectall.checked = false;
		return;
	}
	document.forms[0].selectall.checked = true;
}else
{
	for(i=0;i<document.forms[0].chooseall.length;i++)
	{
		if(!document.forms[0].chooseall[i].checked)
		{
			document.forms[0].selectall.checked = false;
			return;
		}
	}
	document.forms[0].selectall.checked = true;
}
	
	
}

//obj 表示actcode或storage,flag表示是否进行检查selectall复选框
function changeCAbox(obj,flag){
	//如果没有传入参数，则表示进行检查selectall复选框
	if(flag == null){
		flag = true;
	}
	var chooseAllObj = obj.parentElement.parentElement.parentElement.children[0].children[0].children[0];
	var rowArr = obj.parentElement.parentElement.parentElement.children;
	var tag = true;
	for(var i=0;i<rowArr.length;i++){
		var tdArr = rowArr[i].children;
		for(var j=0; j<tdArr.length; j++){
			var inputArr = tdArr[j].children;
			for(var k=0; k<inputArr.length; k++){
				if(inputArr[k].tagName=="INPUT" && inputArr[k].name!="chooseall"){
					if(inputArr[k].checked==false){
						chooseAllObj.checked = false;
						document.forms[0].selectall.checked = false;
						tag = false;
						return;
					}
				}
			}
		}	
	}
	chooseAllObj.checked = true;
	if(flag)
	{
		setSelectValue();
	}
}

//var allValue=new Object();
//allValue.actcode="<%//=ToolFunc.combineStr(permArr)%>";
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
                    <td><img src="../images/tab_left.gif" width="6" height="22"></td>
                    <td nowrap background="../images/tab_midbak.gif">权限设置</td>
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
                        <td> <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr> 
                              <td align="right"> <table border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" height="21" onClick="location.replace('role_list.jsp')"><img src="../images/icon_return.gif" width="16" height="16" align="absmiddle"> 
                                      返回</td>
                                  </tr>
                                </table></td>
                            </tr>
                          </table>
                          组名:<font color="#FF0000"><strong><%=roleName%></strong></font> 
                          <input type="checkbox" name="selectall" onClick="selectAll()">
                          选择全部权限 
						  <%
							String[] nPermissions;
							String[] modules = Permission.getAllModule();					
							for(int i=0;modules!=null && i<modules.length; i++)
							{
							if(modules[i].equals("系统功能"))continue;
						  %>
						  <fieldset><legend><%=modules[i]%></legend>
						  <table module="<%=modules[i]%>">
						  	  <%
							  nPermissions = Permission.getPermissionsOfModule(modules[i]);
							  %>
							  <tr>
							  <td noWrap valign=top>
							  <%
							  for(int j=0; j<nPermissions.length; j++){
							 
							 
							  if(j==0){
							  %>
							  <input name="chooseall" type="checkbox" onClick="chooseAll(this)">全选--</td><td>
							  <%
							  }
							  %>
							  
							  <%							  
							  for(int k=0; k<nPermissions.length; k++){
							  boolean checked=false;
							  	for(int m=0;permArr!=null && m<permArr.length;m++)
								{
									if(permArr[m].module.equals(modules[i]) && permArr[m].permission.equals(nPermissions[j]))
									{
									checked=true;
									}
								}  		
							  %>							  
							  <input name="actcode" type="checkbox" module="<%=modules[i]%>" value="<%=nPermissions[j]%>" onClick="changeCAbox(this)" <%if(checked)out.print("checked");%>>
							  <%=nPermissions[j]%>
							  
							  <%
							  	j++;
							  	
							}
							  %></td>
							  </tr>
						  <%
						  }						  							
						  %>
						  </table>
						  </fieldset>
						  <%
						  }						  
						  %>						  
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right"><input name="roleId" type="hidden" value="<%=roleId%>">
							  <input name="permStr" type="hidden" value="">
                                <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="saverolesetup()" height="21"><input type=image src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 
                                      保存</td>
                                  </tr>
                                </table></td>
                            </tr>
                          </table>
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
<iframe name="hiddenFrame" width=0 height=0 ></iframe>
<script>
//autoChoose(allValue);
if(document.forms[0].actcode.length==null)
{
	changeCAbox(document.forms[0].actcode,false);
}else
{
	for(i=0; i<document.forms[0].actcode.length; i++){
		changeCAbox(document.forms[0].actcode[i],false);
	}
}


setSelectValue();
</script>
</body>
</html>
