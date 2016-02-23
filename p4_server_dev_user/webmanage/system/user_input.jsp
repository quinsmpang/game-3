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

//获取user数据
String userName="";
String pwd="";
String pwd2="";
String trueName="";
String isEnable="1";
String userType="1";
String channel="";

UserBAC userBAC = new UserBAC();
AimXML user= userBAC.getXMLObj("id=" + id);

if(user!=null)
{
	user.openRs(UserBAC.tbName);
	user.next();
	userName = user.getRsValue("userName");
	pwd="******";
	pwd2="******";
	trueName=user.getRsValue("trueName");
	isEnable=user.getRsValue("isEnable");
	userType=user.getRsValue("userType");
	channel=user.getRsValue("channel");
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

function checkForm(){
var theForm=document.forms[0];

if(theForm.userName.value==""){
 alert("请输入用户名");
 theForm.userName.focus();
 return false;
 }
if(theForm.pwd.value==""){
 alert("请输入密码");
 theForm.pwd.focus();
 return false;
 }
 if(theForm.pwd2.value==""){
 alert("请输入确认密码");
 theForm.pwd2.focus();
 return false;
 }
 if(theForm.pwd.value!=theForm.pwd2.value){
 alert("两次输入密码不一致");
 theForm.pwd2.focus();
 return false;
 }
 theForm.target="hiddenFrame";
 theForm.action="user_input_upload.jsp";
 theForm.submit();
 wait();	
 theForm.target="";
 theForm.action="";

 return true;
}
</script>

<script>
var allValue=new Object();
allValue.userName="<%=userName%>";
allValue.pwd="<%=pwd%>";
allValue.pwd2="<%=pwd2%>";
allValue.trueName="<%=trueName%>";
allValue.isEnable="<%=isEnable%>";
allValue.userType="<%=userType%>";
allValue.channel="<%=channel%>";
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
                      <td nowrap background="../images2/tab_midbak.gif"><%if(id==0){%>
                        添加用户
                        <%}else{%>
                        修改用户
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
                      <td><table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr> 
                              <td width="70" align="right">用户名</td>
                              <td> <input name="userName" type="text" class="input4" >
                                <font color="#FF0000">*</font> </td>
                            </tr>
                            <tr> 
                              <td width="70" align="right">密码</td>
                              <td> <input name="pwd" type="password" class="input4" >
                                <font color="#FF0000">*</font> </td>
                            </tr>
                            <tr> 
                              <td width="70" align="right">确认密码</td>
                              <td> <input name="pwd2" type="password" class="input4" >
                                <font color="#FF0000">*</font> </td>
                            </tr>
                            <tr>
                              <td align="right">真名</td>
                              <td><input name="trueName" type="text" class="input4" id="trueName" ></td>
                            </tr>
                            <tr>
                              <td align="right">用户类型</td>
                              <td>
                                  <select name="userType" id="userType">
                                    <option value="1">普通用户</option>																		
                                    <option value="0">管理员</option>									
                                  </select>
                              <font color="#FF0000">*</font> </td>
                            </tr>
                            <tr>
                              <td align="right">渠道</td>
                              <td>
                              
                              <select name="channel" id="channel" onChange="reloadPage()">
                              <option value="">选择渠道</option>
							  <%
							  DBPsRs channelRs = DBPool.getInst().pQueryS("tab_channel");
							  while(channelRs.next()){
							  %>
							  <option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")%>(<%=channelRs.getString("code")%>)</option>
							  <%
							  }
							  %>
							</select></td>
                            </tr>
                            
                            
                            <tr> 
                              <td align="right">状态</td>
                              <td>
                              <input type="radio" name="isEnable" value="1">开通
                              <input type="radio" name="isEnable" value="0">关闭
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right">
                                <input name="id" type="hidden" value="<%=id%>">
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
