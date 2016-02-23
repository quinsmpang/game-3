<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>

<html>
<head>
<title>Change password</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>


<script>
function checkForm()
{
var theForm=document.forms[0];
if(theForm.oldpwd.value==""){
 alert("请输入旧密码");
 theForm.oldpwd.focus();
 return false;
 }
 if(theForm.newpwd.value==""){
 alert("请输入新密码");
 theForm.newpwd.focus();
 return false;
 }
 if(theForm.newpwd2.value==""){
 alert("请再次输入新密码");
 theForm.newpwd2.focus();
 return false;
 }
 if(theForm.newpwd.value!=theForm.newpwd2.value){
 alert("两次密码不一致请重输");
 theForm.newpwd2.focus();
 theForm.newpwd2.select();
 return false;
 }
 
 theForm.target="hiddenFrame";
 theForm.action="pwd_change_upload.jsp";
 theForm.submit();
 wait();	
 theForm.target="";
 theForm.action="";

}


</script>
</head>

<body bgcolor="#EFEFEF">
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
                    <td nowrap background="../images/tab_midbak.gif">更改密码</td>
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
                <td valign="top" align="center"><table width="95%" border="0" cellspacing="1" cellpadding="2">
                    <tr>
                      <td><form name="form1" method="post" action="" onSubmit="return false">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td > 
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              
              <tr> 
                <td width="1" valign="bottom" ></td>
                <td align="center" valign="bottom" > 
               <br>
               <table width="90" border="0" cellspacing="1" cellpadding="2">
                      <tr> 
                        <td align="right" nowrap>旧密码</td>
                        <td><input name="oldpwd" type="password" class="input4" id="oldpwd" must="true"></td>
                      </tr>
                      <tr>
                        <td align="right" nowrap>新密码</td>
                        <td><input name="newpwd" type="password" class="input4" id="newpwd" must="true"></td>
                      </tr>
                      <tr>
                        <td align="right" nowrap>确认新密码</td>
                        <td><input name="newpwd2" type="password" class="input4" id="newpwd2" must="true"></td>
                      </tr>
                      <tr> 
                        <td colspan="2" align="right"> <table width="50" border="0" cellspacing="0" cellpadding="2">
                            <tr> 
                              <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="checkForm(document.forms[0])" height="21">
							  <input type=image class="submitimg" src="../images/icon_save4.gif" align="absmiddle" width="16" height="16"> 
                                保存</td>
                            </tr>
                          </table></td>
                      </tr>
                    </table>                
               <br></td>
              </tr>
            </table>
            
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</form></td>
                    </tr>
                  </table></td>
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
</body>
</html>
