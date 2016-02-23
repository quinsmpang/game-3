<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="用户管理";
%>
<%@ include file="inc_checkperm.jsp"%>
<%
String username=request.getParameter("username");

//获取user数据
UserBAC userBAC = new UserBAC();

String[] subChannels = userBAC.getSubChannels(username);
String subChannelsStr = "";
if(subChannels!=null)
{
	subChannelsStr = Tools.strArr2Str(subChannels, "|");
}

%>
<html>
<head>
<title>用户管理</title>
<%@ include file="inc_website_icon.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>
<script src="../js/patterns.js"></script>
<script src="../js/checkform.js"></script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];

	theForm.target="hiddenFrame";
	theForm.action="user_set_subchannel_upload.jsp";
	theForm.submit();
	wait();	
	theForm.target="";
	theForm.action="";
	return true;
}
</script>

<script>
var allValue=new Object();
allValue.channel = "<%=subChannelsStr%>";
</script>

<link href="../css/style1.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="" onSubmit="return false">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
            <td > <table width="100%" border="0" cellspacing="1" cellpadding="2">
                <tr>
                  <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" height="21" onClick="self.close()"><img src="../images/icon_closewindow1.gif" width="16" height="16" align="absmiddle"> 
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
                      <td nowrap background="../images2/tab_midbak.gif">
                        设置用户子渠道                      
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
                      <td><table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr> 
                              <td width="70" align="right">用户名</td>
                              <td><%=username%></td>
                            </tr>
                            <tr> 
                              <td width="70" align="right">选择子渠道</td>
                              <td><%
							  JSONArray channelArr = userBAC.getAvailableSubChannels(username);
							  for(int i=0;channelArr!=null && i<channelArr.length();i++)
							  {
							  	JSONObject channelJson = channelArr.optJSONObject(i);
								String code = channelJson.optString("code");
								String name = channelJson.optString("name");
							  %>
                                <label><input name="channel" type="checkbox" id="channel" value="<%=code%>">
                              <%=name%>(<%=code%>)</label>
                              <%}%></td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right">
                                <input name="username" type="hidden" value="<%=username%>">
                                <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" height="21" onClick="checkForm()"><img src="../images/icon_save2.gif" align="absmiddle"> 
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
</form>
<script>
autoChoose(allValue);
</script>
<iframe name="hiddenFrame" width=0 height=0 ></iframe>
</body>
</html>
