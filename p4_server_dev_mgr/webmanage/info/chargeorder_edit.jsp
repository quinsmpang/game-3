<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="充值管理";
String perm="充值记录";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String chargecenter="";
String corderno="";
int cordertype=0;
String orderno="";
int ordertype=0;
int price=0;
String extend="";
int result=0;
String username="";
String ordertime="";
String savetime="";
String ip="";
int serverid=0;
int playerid=0;
String channel="";
int getcoin=0;
String buytype="";
int getpower=0;
String platform="";
String gived="";
String note="";
String fromwhere="";


int id = ToolFunc.str2int(request.getParameter("id"));

ChargeOrderBAC chargeOrderBAC = ChargeOrderBAC.getInstance();
JSONObject xml = chargeOrderBAC.getJsonObj("id="+id);
if(xml!=null)
{
chargecenter=xml.optString("chargecenter");
corderno=xml.optString("corderno");
cordertype=xml.optInt("cordertype");
orderno=xml.optString("orderno");
ordertype=xml.optInt("ordertype");
price=xml.optInt("price");
extend=Tools.replace(xml.optString("extend"),"\"","\\\"");
result=xml.optInt("result");
username=xml.optString("username");
ordertime=xml.optString("ordertime");
savetime=xml.optString("savetime");
ip=xml.optString("ip");
serverid=xml.optInt("serverid");
playerid=xml.optInt("playerid");
channel=xml.optString("channel");
getcoin=xml.optInt("getcoin");
buytype=xml.optString("buytype");
getpower=xml.optInt("getpower");
platform=xml.optString("platform");
gived=xml.optString("gived");
note=xml.optString("note");
fromwhere=xml.optString("fromwhere");

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
allValue.chargecenter="<%=chargecenter%>";
allValue.corderno="<%=corderno%>";
allValue.cordertype="<%=cordertype%>";
allValue.orderno="<%=orderno%>";
allValue.ordertype="<%=ordertype%>";
allValue.price="<%=price%>";
allValue.extend="<%=extend%>";
allValue.result="<%=result%>";
allValue.username="<%=username%>";
allValue.ordertime="<%=ordertime%>";
allValue.savetime="<%=savetime%>";
allValue.ip="<%=ip%>";
allValue.serverid="<%=serverid%>";
allValue.playerid="<%=playerid%>";
allValue.channel="<%=channel%>";
allValue.getcoin="<%=getcoin%>";
allValue.buytype="<%=buytype%>";
allValue.getpower="<%=getpower%>";
allValue.platform="<%=platform%>";
allValue.gived="<%=gived%>";
allValue.note="<%=note%>";
allValue.fromwhere="<%=fromwhere%>";

</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.cordertype.value!="" && isNaN(theForm.cordertype.value))
	{
	alert("中心订单类型请输入数值");
	theForm.cordertype.focus();
	theForm.cordertype.select();
	return false;
	}
	if(theForm.orderno.value=="")
	{
	alert("请输入子平台订单号");
	theForm.orderno.focus();
	return false;
	}
	if(theForm.ordertype.value=="")
	{
	alert("请输入子平台订单类型");
	theForm.ordertype.focus();
	return false;
	}
	if(isNaN(theForm.ordertype.value))
	{
	alert("子平台订单类型请输入数值");
	theForm.ordertype.focus();
	theForm.ordertype.select();
	return false;
	}
	if(theForm.price.value=="")
	{
	alert("请输入价格");
	theForm.price.focus();
	return false;
	}
	if(isNaN(theForm.price.value))
	{
	alert("价格请输入数值");
	theForm.price.focus();
	theForm.price.select();
	return false;
	}
	if(theForm.result.value=="")
	{
	alert("请输入结果");
	theForm.result.focus();
	return false;
	}
	if(isNaN(theForm.result.value))
	{
	alert("结果请输入数值");
	theForm.result.focus();
	theForm.result.select();
	return false;
	}
	if(theForm.ordertime.value=="")
	{
	alert("请输入下单时间");
	theForm.ordertime.focus();
	return false;
	}
	if(!isDate(theForm.ordertime.value) && !isTime(theForm.ordertime.value))
	{
	alert("下单时间请输入日期格式");
	theForm.ordertime.focus();
	theForm.ordertime.select();
	return false;
	}
	if(theForm.savetime.value=="")
	{
	alert("请输入通知时间");
	theForm.savetime.focus();
	return false;
	}
	if(!isDate(theForm.savetime.value) && !isTime(theForm.savetime.value))
	{
	alert("通知时间请输入日期格式");
	theForm.savetime.focus();
	theForm.savetime.select();
	return false;
	}
	if(theForm.serverid.value=="")
	{
	alert("请输入游戏服");
	theForm.serverid.focus();
	return false;
	}
	if(isNaN(theForm.serverid.value))
	{
	alert("游戏服请输入数值");
	theForm.serverid.focus();
	theForm.serverid.select();
	return false;
	}
	if(theForm.playerid.value=="")
	{
	alert("请输入角色id");
	theForm.playerid.focus();
	return false;
	}
	if(isNaN(theForm.playerid.value))
	{
	alert("角色id请输入数值");
	theForm.playerid.focus();
	theForm.playerid.select();
	return false;
	}
	if(theForm.channel.value=="")
	{
	alert("请输入渠道");
	theForm.channel.focus();
	return false;
	}
	if(theForm.getcoin.value=="")
	{
	alert("请输入获取钻石");
	theForm.getcoin.focus();
	return false;
	}
	if(isNaN(theForm.getcoin.value))
	{
	alert("获取钻石请输入数值");
	theForm.getcoin.focus();
	theForm.getcoin.select();
	return false;
	}
	if(theForm.buytype.selectedIndex==0)
	{
	alert("请选择购买类型");
	theForm.buytype.focus();
	return false;
	}
	if(theForm.getpower.value!="" && isNaN(theForm.getpower.value))
	{
	alert("购买特权请输入数值");
	theForm.getpower.focus();
	theForm.getpower.select();
	return false;
	}
	if(theForm.gived.selectedIndex==0)
	{
	alert("请选择是否已发货");
	theForm.gived.focus();
	return false;
	}
	if(theForm.fromwhere.selectedIndex==0)
	{
	alert("请选择来源");
	theForm.fromwhere.focus();
	return false;
	}


	
	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="chargeorder_upload.jsp";
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
                    <td nowrap background="../images/tab_midbak.gif">充值管理</td>
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
                            <tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>是否中心订单</strong></td><td width="93%" class="nrbgc1"><select name="chargecenter">
                                  <option value="1">是</option>
                                  <option value="0">否</option>
                            </select></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>中心订单号</strong></td><td width="93%" class="nrbgc1"><input name="corderno" type="text" value="" size="30"></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>中心订单类型</strong></td><td width="93%" class="nrbgc1"><input name="cordertype" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>子平台订单号</strong></td><td width="93%" class="nrbgc1"><input name="orderno" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>子平台订单类型</strong></td><td width="93%" class="nrbgc1"><select name="ordertype" id="ordertype">
																			<option value="">订单类型</option>
																	<%
																		DBPsRs chargetypeRs = DBPool.getInst().pQueryS(TabStor.tab_charge_type);
																		while(chargetypeRs.next()){
																	 %>
																		<option value="<%=chargetypeRs.getInt("NUM")%>"><%=chargetypeRs.getString("NAME")%></option>
																	 <%
																	 }
																	 %>
																		</select></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>价格</strong></td><td width="93%" class="nrbgc1"><input name="price" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>扩展参数</strong></td><td width="93%" class="nrbgc1"><input name="extend" type="text" value="" size="70"></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>用户名</strong></td><td width="93%" class="nrbgc1"><input name="username" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>下单时间</strong></td><td width="93%" class="nrbgc1"><input name="ordertime" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>通知时间</strong></td><td width="93%" class="nrbgc1"><input name="savetime" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>IP</strong></td><td width="93%" class="nrbgc1"><input name="ip" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>游戏服</strong></td><td width="93%" class="nrbgc1"><input name="serverid" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>角色id</strong></td><td width="93%" class="nrbgc1"><input name="playerid" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>渠道</strong></td><td width="93%" class="nrbgc1"><input name="channel" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>获取钻石</strong></td><td width="93%" class="nrbgc1"><input name="getcoin" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>购买类型</strong></td><td width="93%" class="nrbgc1"><select name="buytype"><option value="">选择</option>
      <option value="1">钻石</option>
      <option value="2">特权</option>
</select><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>购买特权</strong></td><td width="93%" class="nrbgc1"><input name="getpower" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>账号平台</strong></td><td width="93%" class="nrbgc1"><input name="platform" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>是否已发货</strong></td><td width="93%" class="nrbgc1"><select name="gived"><option value="">选择</option>
      <option value="0">未发货</option>
      <option value="1">已发货</option>
      <option value="-1">发货失败</option>
</select><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>描述</strong></td><td width="93%" class="nrbgc1"><input name="note" type="text" value=""></td></tr>

<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>来源</strong></td><td width="93%" class="nrbgc1"><select name="fromwhere"><option value="">选择</option>
      <option value="1">手机</option>
      <option value="2">网站</option>
</select><font color="#FF0000">*</font></td></tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>结果</strong></td>
  <td class="nrbgc1"><font color="#FF0000">
    <select name="result" id="result">
      <option value="">选择</option>
      <option value="0">待处理</option>
      <option value="1">成功</option>
      <option value="-1">失败</option>
    </select>
    *</font></td>
</tr>
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