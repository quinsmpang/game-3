<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="游戏管理";
String perm="兑换码";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String code="";
String open_date="";
int published=0;
String publish_time="";
String phone="";
int exchanged=0;
String exchange_time="";
String create_time="";


int id = ToolFunc.str2int(request.getParameter("id"));

ExchangeCodeBAC exchangeCodeBAC = ExchangeCodeBAC.getInstance();
JSONObject xml = exchangeCodeBAC.getJsonObj("id="+id);
if(xml!=null)
{
code=xml.optString("code");
open_date=xml.optString("open_date");
published=xml.optInt("published");
publish_time=xml.optString("publish_time");
phone=xml.optString("phone");
exchanged=xml.optInt("exchanged");
exchange_time=xml.optString("exchange_time");
create_time=xml.optString("create_time");

}
%>
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
<script src="../js/DhtmlEdit.js"></script>
<script src="../js/Calendar3.js"></script>
<script>
var allValue=new Object();
allValue.id="<%=id%>";
allValue.code="<%=code%>";
allValue.open_date="<%=open_date%>";
allValue.published="<%=published%>";
allValue.publish_time="<%=publish_time%>";
allValue.phone="<%=phone%>";
allValue.exchanged="<%=exchanged%>";
allValue.exchange_time="<%=exchange_time%>";
allValue.create_time="<%=create_time%>";

</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.code.value=="")
{
alert("请输入兑换码");
theForm.code.focus();
return false;
}
if(theForm.open_date.value!="" && !isDate(theForm.open_date.value) && !isTime(theForm.open_date.value))
{
alert("开放日期请输入日期格式");
theForm.open_date.focus();
theForm.open_date.select();
return false;
}
if(theForm.published.value!="" && isNaN(theForm.published.value))
{
alert("已分发请输入数值");
theForm.published.focus();
theForm.published.select();
return false;
}
if(theForm.publish_time.value!="" && !isDate(theForm.publish_time.value) && !isTime(theForm.publish_time.value))
{
alert("分发时间请输入日期格式");
theForm.publish_time.focus();
theForm.publish_time.select();
return false;
}
if(theForm.exchanged.value!="" && isNaN(theForm.exchanged.value))
{
alert("已兑换请输入数值");
theForm.exchanged.focus();
theForm.exchanged.select();
return false;
}
if(theForm.exchange_time.value!="" && !isDate(theForm.exchange_time.value) && !isTime(theForm.exchange_time.value))
{
alert("兑换时间请输入日期格式");
theForm.exchange_time.focus();
theForm.exchange_time.select();
return false;
}
if(theForm.create_time.value!="" && !isDate(theForm.create_time.value) && !isTime(theForm.create_time.value))
{
alert("生成时间请输入日期格式");
theForm.create_time.focus();
theForm.create_time.select();
return false;
}


	
	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="exchangecode_upload.jsp";
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
                    <td nowrap background="../images/tab_midbak.gif">兑换码列表</td>
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
                        <td><fieldset><legend>编辑</legend>
                            
                            <table width="100%" border="0" cellspacing="1" cellpadding="2">    
                            <tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>兑换码</strong></td><td width="93%" class="nrbgc1"><input name="code" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>开放日期</strong></td><td width="93%" class="nrbgc1"><input name="open_date" type="text" value="" onClick="new Calendar().show(this)"></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>已分发</strong></td><td width="93%" class="nrbgc1"><input name="published" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>分发时间</strong></td><td width="93%" class="nrbgc1"><input name="publish_time" type="text" value="" onClick="new Calendar().show(this)"></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>手机号码</strong></td><td width="93%" class="nrbgc1"><input name="phone" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>已兑换</strong></td><td width="93%" class="nrbgc1"><input name="exchanged" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>兑换时间</strong></td><td width="93%" class="nrbgc1"><input name="exchange_time" type="text" value="" onClick="new Calendar().show(this)"></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>生成时间</strong></td><td width="93%" class="nrbgc1"><input name="create_time" type="text" value="" onClick="new Calendar().show(this)"></td></tr>

                            </table>
				
                            <table width="100%"  border="0" cellspacing="1" cellpadding="2">
                              <tr>
                                <td align="right">
				<input type="hidden" name="id"/>
                                  <table width="50" border="0" cellspacing="0" cellpadding="2">
                                    <tr>
                                      <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="checkForm()"><img src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 保存</td>
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
<iframe name="hiddenFrame" width=0 height=0></iframe>
<script>
autoChoose(allValue);
</script>
</body>
</html>