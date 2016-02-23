<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="游戏管理";
String perm="激活码";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String code="";
int activated=0;
int publish=0;
String publish_time="";
String activate_user="";
String activate_time="";
String create_time="";
String starttime="";
String phone="";
String channel="";
int method=3;
int lottery=0;
String lottery_time="";
String mark="";

int id = ToolFunc.str2int(request.getParameter("id"));

ActivateCodeBAC activateCodeBAC = ActivateCodeBAC.getInstance();
JSONObject xml = activateCodeBAC.getJsonObj("id="+id);
//System.out.println(xml.toString());
if(xml!=null)
{
code=xml.optString("code");
activated=xml.optInt("activated");
publish=xml.optInt("publish");
publish_time=xml.optString("publish_time");
activate_user=xml.optString("activate_user");
activate_time=xml.optString("activate_time");
create_time=xml.optString("create_time");
starttime=Tools.toShortDateStr(xml.optString("starttime"));
phone=xml.optString("phone");
method=xml.optInt("method");
lottery = xml.optInt("lottery");
lottery_time=xml.optString("lottery_time");
mark=xml.optString("mark");
}
else
{
create_time = Tools.getCurrentDateTimeStr();
publish_time = Tools.getCurrentDateTimeStr();
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
<script>
var allValue=new Object();
allValue.id="<%=id%>";
allValue.code="<%=code%>";
allValue.starttime="<%=starttime%>";
allValue.publish="<%=publish%>";
allValue.publish_time="<%=publish_time%>";
allValue.activate_user="<%=activate_user%>";
allValue.activated="<%=activated%>";
allValue.activate_time="<%=activate_time%>";
allValue.phone="<%=phone%>";
allValue.method="<%=method%>";
allValue.lottery="<%=lottery%>";
allValue.lottery_time="<%=lottery_time%>";
allValue.mark="<%=mark%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.code.value=="")
{
alert("请输入激活码");
theForm.code.focus();
return false;
}

	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="activatecode_upload.jsp";
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
                    <td nowrap background="../images/tab_midbak.gif">激活码管理</td>
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
                            <tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>激活码</strong></td><td width="93%" class="nrbgc1"><input name="code" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>激活状态</strong></td><td width="93%" class="nrbgc1">
<select name="activated" id="activated">
  <option value="0">未激活</option>
  <option value="1">已激活</option>
</select></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>分发状态</strong></td><td width="93%" class="nrbgc1">
  <select name="publish">
     <option value="0">未分发</option>
    <option value="1">已分发</option>   
  </select>
 </td></tr>

<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>分发时间</strong></td><td width="93%" class="nrbgc1"><input name="publish_time" type="text" id="publish_time" value="" onClick="setday(this)"></td></tr>

<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>激活用户</strong></td><td width="93%" class="nrbgc1"><input name="activate_user" type="text" id="activate_user" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>激活时间</strong></td><td width="93%" class="nrbgc1"><input name="activate_time" type="text" id="activate_time" value="" onClick="setday(this)"></td></tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>激活手机号</strong></td>
  <td class="nrbgc1"><input name="phone" type="text" id="phone" value=""></td>
</tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>预约开启日期</strong></td><td width="93%" class="nrbgc1"><input name="starttime" type="text" value="" onClick="setday(this)"></td></tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>已参加抽奖</strong></td>
  <td class="nrbgc1"><select name="lottery" id="lottery">
    <option value="1">是</option>
    <option value="0">否</option>
      </select></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>参加时间</strong></td>
  <td class="nrbgc1"><input name="lottery_time" type="text" id="lottery_time" onClick="setday(this)" value=""></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>获取方式</strong></td>
  <td class="nrbgc1"><select name="method" id="method">
  	<option value="0">选择</option>
    <option value="3">内部</option>
    <option value="1">短信</option>
    <option value="2">网站</option>
      </select></td>
</tr>

<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>生成时间</strong></td><td width="93%" class="nrbgc1"><%=create_time%></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>标签</strong></td><td width="93%" class="nrbgc1"><input name="mark" type="text" id="mark" value=""></td></tr>
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