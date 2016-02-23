<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="游戏管理";
String perm="激活码";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>

<script>
function checkForm()
{
	if(document.getElementById("file").value=="")
	{
		alert("请选择excel文件");
		return false;
	}	
	
	document.forms[0].submit();
}
</script>
</head>

<body bgcolor="#EFEFEF">
<form action="activatecode_import_excel_upload.jsp" method="post" enctype="multipart/form-data" name="form1" target="hiddenFrame">
  
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
                    <td nowrap background="../images/tab_midbak.gif">导入渠道手机号码</td>
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
                   <td width="150" align="right" nowrap><strong>渠道手机号码excel文件：</strong></td>
                   <td nowrap>
                     <input type="file" name="file"></td>
                 </tr>
                 <tr>
                   <td nowrap>&nbsp;</td>
                   <td nowrap>
                     <input type="button" name="Button" value="导入" onClick="checkForm()">                     </td>
                 </tr>
                 <tr>
                   <td nowrap>&nbsp;</td>
                   <td nowrap>注意：excel里第一列必须是手机号码，第三列是激活码。</td>
                 </tr>
                </table>
                <table width="95%" border="0" cellspacing="1" cellpadding="2">
                  <tr>
                    <td align="center"><table width="100%" border="0" cellspacing="1" cellpadding="2">
                          <tr>
                            <td>&nbsp;</td>
                          </tr>
                          
                        </table>
						
                      <table width="100%" border="0" cellspacing="1" cellpadding="2">
                        <tr>
                          <td></td>
                        </tr>
                      </table></td>
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
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</form>
</body>
</html>
