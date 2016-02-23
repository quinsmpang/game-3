<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="游戏管理";
String perm="兑换码";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<title></title>

<link href="../css/style1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="skins/aspsky_1.css" >
<link rel="STYLESHEET" type="text/css" href="images/post/edit.css">
<script src="../js/common.js"></script>
<script src="../js/patterns.js"></script>
<script src="../js/meizzDate.js"></script>
<script src="../js/Calendar3.js"></script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.len.value=="")
	{
	alert("请输入兑换码位数");
	theForm.len.focus();
	return false;
	}
	if(theForm.amount.value=="")
	{
	alert("请输入生成数量");
	theForm.amount.focus();
	return false;
	}

	//theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="exchangecode_batch_upload.jsp";
	theForm.submit();
	
	wait();
}

</script>
</head>
<body bgcolor="#EFEFEF" >
<form action="" method="post" enctype="application/x-www-form-urlencoded" name="form1"> 
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
                    <td nowrap background="../images/tab_midbak.gif">兑换码管理</td>
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
                        <td><fieldset><legend>批量生成兑换码</legend>
                            
                            <table width="100%" border="0" cellspacing="1" cellpadding="2">    
                            <tr>
                              <td width="10%" align="right" nowrap class="nrbgc1"><strong>兑换码位数</strong></td>
                              <td width="93%" class="nrbgc1"><input name="len" type="text" id="len" value=""><font color="#FF0000">*</font></td></tr>
<tr>
  <td width="10%" align="right" nowrap class="nrbgc1"><strong>兑换码格式</strong></td>
  <td width="93%" class="nrbgc1"><font color="#FF0000">
  <label>
  <select name="type" id="type">
    <option value="1">字母加数字</option>
    <option value="2">字母</option>
    <option value="3">数字</option>
  </select>
  </label>
</font></td></tr>
<tr>
  <td width="10%" align="right" nowrap class="nrbgc1"><strong>生成数量</strong></td>
  <td width="93%" class="nrbgc1"><font color="#FF0000">
  <label></label>
  <input name="amount" type="text" id="amount" value="">
  *</font></td>
</tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>开放日期</strong></td>
  <td width="93%" class="nrbgc1"><font color="#FF0000">
    <input name="opendate" type="text" id="opendate" value="" onFocus="new Calendar().show(this)">
  </font></td>
</tr>
                            </table>
				
                            <table width="100%"  border="0" cellspacing="1" cellpadding="2">
                              <tr>							 
                                <td align="right">
								<table width="50" border="0" cellspacing="0" cellpadding="2">
                                    <tr>
                                      <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="checkForm()"><img src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 批量生成</td>
                                    </tr>
                                  </table>
								  </td>
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
<iframe name="hiddenFrame" width=0 height=0></iframe>
</body>
</html>