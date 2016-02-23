<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="系统功能";
String perm="游戏服升级";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<title></title>

<link href="../css/style1.css" rel="stylesheet" type="text/css">
<script src="../js/common.js"></script>
<%
int type = Tools.str2int(request.getParameter("type"));
%>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.file.value=="")
	{
	alert("请输入文件名");
	theForm.file.focus();
	return false;
	}
	
	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="server_update_upload.jsp";
	theForm.submit();
	
	wait();
}

</script>
</head>
<body bgcolor="#EFEFEF" >

<form action="" method="post" enctype="multipart/form-data" name="form1"> 
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td ><table width="100%"  border="0" cellspacing="1" cellpadding="2">
    <tr>
      <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
          <tr>
            <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="self.close()"> <img src="../images/icon_closewindow1.gif" width="16" height="16" align="absmiddle"> 关闭</td>
          </tr>
      </table></td>
    </tr>
  </table><table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif" width="6" height="22"></td>
                    <td nowrap background="../images/tab_midbak.gif">服务器升级管理</td>
                    <td><img src="../images/tab_right.gif" width="6" height="22"></td>
                  </tr>
                </table> 
                </td>
                <td valign="bottom" > <table width="100%" border="0" cellspacing="0" cellpadding="0" height="22">
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
                  </table></td>
              </tr>
            </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td rowspan="3" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
                  <td colspan="2" align="center" valign="top"><table width="95%" border="0" cellspacing="1" cellpadding="2">
                      <tr> 
                        <td><fieldset><legend>编辑<input name="updtype" type="hidden" id="updtype" value="<%=type%>">
                        </legend>
                            
                            <table width="100%" border="0" cellspacing="1" cellpadding="2">    
<%
	JSONObject serverJson = null;
	if(type == 1){
		serverJson = UserServerBAC.getInstance().getJsonObjs(null, "id");
	} else 
	if(type == 2){
		serverJson = ServerBAC.getInstance().getJsonObjs("usestate=1", "id");
	}
	if(serverJson!=null) {
%>
<tr>
    <td align="right" nowrap class="nrbgc1"><strong>选择服务器</strong></td>
    <td class="nrbgc1">
	<script>
	function chooseAllItem(name)
	{
		var choose=true;
		if(document.getElementById("chooseAll").checked)
		{
			choose=true;
		}
		else
		{
			choose=false;
		}																		
		for(var i=0;i<document.getElementsByName(name).length;i++)
		{
			document.getElementsByName(name)[i].checked=choose;
		}																			
	}
	function updateChooseAll(obj)
	{
		if(!obj.checked && document.getElementById("chooseAll").checked)
		{
		document.getElementById("chooseAll").checked=false;
		}
	}
	</script>
	<input name="chooseAll" type="checkbox" id="chooseAll" onClick="chooseAllItem('serverId')" value="1" checked>全选
<%
  		JSONArray array = serverJson.optJSONArray("list");
  		for(int i=0;array!=null && i<array.length();i++) {
  			JSONObject line = array.optJSONObject(i);
%>
			<input name="serverId" type="checkbox" id="serverId" onClick="updateChooseAll(this)" value="<%=line.optInt("id")%>" checked >
            <%=line.optString("name")+"("+line.optInt("id")+")"%>
<%
		}
%>
	</td>
</tr>
<%
	}
%>

                            <tr>
                              <td width="10%" align="right" nowrap class="nrbgc1"><strong>升级文件包</strong></td>
                              <td width="93%" class="nrbgc1"><label>
                                <input name="file" type="file" size="80">
                                <br>
                              <font color="#FF0000"> (zip格式的升级包)</font></label></td>
                            </tr>
                            </table>
				
                            <table width="100%"  border="0" cellspacing="1" cellpadding="2">
                              <tr>
                                <td align="right">
				<input type="hidden" name="id"/>
                                  <table width="50" border="0" cellspacing="0" cellpadding="2">
                                    <tr>
                                      <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="checkForm()"><img src="../images/icon_putup.gif" width="16" height="16" align="absmiddle"> 上传更新</td>
                                    </tr>
                                  </table></td>
                              </tr>
                            </table>
                        </fieldset><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                              <tr>
                                <td height="25"> <font color="#FF0000">*</font> 必填项</td>
                              </tr>
                            </table></td>
                      </tr>
                  </table></td>
                  <td rowspan="3" bgcolor="#848284" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
                </tr>
                
                <tr> 
                  <td height="1" colspan="2" bgcolor="#848284"></td>
                </tr>
              </table></td>
        </tr>
      </table></td>
  </tr>
</table>
</form>
<iframe name="hiddenFrame" width=10 height=10></iframe>
</body>
</html>