<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="游戏管理";
String perm="平台礼包码";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String platform="";
String code="";
int giftcode=0;
String playerid="";
int gived=0;
String givetime="";
int publish=0;
String publishtime="";
String phonenumber="";
int serverId=0;
int repeat=0;
String expiretime="";


int id = ToolFunc.str2int(request.getParameter("id"));

PlatformGiftCodeBac platformGiftCodeBAC = PlatformGiftCodeBac.getInstance();
JSONObject xml = platformGiftCodeBAC.getJsonObj("id="+id);
//System.out.println(platformGiftCodeBAC.getTbName());
if(xml!=null)
{
platform=xml.optString("platform");
code=xml.optString("code");
giftcode=xml.optInt("giftcode");
playerid=xml.optString("playerid");
gived=xml.optInt("gived");
givetime=xml.optString("givetime");
publish=xml.optInt("publish");
publishtime=xml.optString("publishtime");
phonenumber=xml.optString("phonenumber");
serverId=xml.optInt("serverId");
repeat=xml.optInt("repeat");
expiretime=Tools.strdate2shortstr(xml.optString("expiretime"));
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
allValue.platform="<%=platform%>";
allValue.code="<%=code%>";
allValue.giftcode="<%=giftcode%>";
allValue.playerid="<%=playerid%>";
allValue.gived="<%=gived%>";
allValue.givetime="<%=givetime%>";
allValue.publish="<%=publish%>";
allValue.publishtime="<%=publishtime%>";
allValue.phonenumber="<%=phonenumber%>";
allValue.serverId="<%=serverId%>";
allValue.repeat="<%=repeat%>";
allValue.expiretime="<%=expiretime%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.platform.selectedIndex==0)
	{
		alert("请选择联运渠道");
		theForm.platform.focus();
		return false;
	}	
	
	if(theForm.giftcode.selectedIndex==0)
	{
		alert("请选择礼包");
		theForm.giftcode.focus();
		return false;
	}
	if(theForm.code.value=="")
	{
		alert("请输入礼包领取码");
		theForm.code.focus();
		return false;
	}

	if(theForm.givetime.value!="" && !isDate(theForm.givetime.value) && !isTime(theForm.givetime.value))
	{
	alert("领取时间请输入日期格式");
	theForm.givetime.focus();
	theForm.givetime.select();
	return false;
	}

	if(theForm.publishtime.value!="" && !isDate(theForm.publishtime.value) && !isTime(theForm.publishtime.value))
	{
	alert("分发时间请输入日期格式");
	theForm.publishtime.focus();
	theForm.publishtime.select();
	return false;
	}
	
	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="platform_gift_code_upload.jsp";
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
                    <td nowrap background="../images/tab_midbak.gif">平台礼包码生成</td>
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
                            <tr>
                              <td width="10%" align="right" nowrap class="nrbgc1"><strong>联运渠道</strong></td>
                              <td width="93%" class="nrbgc1">
                              <select name="platform" id="platform">
							  <option value="">联运渠道</option>
							  <%
							  DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
								while(channelRs.next()){
								%>
							  <option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")%></option>
							  <%
							  }
							  %>
							</select>
                                <font color="#FF0000">*</font></td>
                            </tr>
							<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>游戏服</strong></td><td width="93%" class="nrbgc1"><%JSONObject serverListData = ServerBAC.getInstance().getJsonObjs("id,name",null,"id ASC");
										%>
                                <select name="serverId" id="serverId" onChange="reloadPage()">
                                  <option value="0">游戏服</option>
                                  <%if(serverListData!=null){
										  JSONArray serverList = serverListData.optJSONArray("list");
										  for(int i=0;i<serverList.length();i++)
										  {
										  JSONObject lineData = serverList.optJSONObject(i);
										  %>
                                  <option value="<%=lineData.optInt("id")%>"><%=lineData.optString("name")%></option>
                                  <%}											
											}%>
                              </select></td>
							</tr>
							  <tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>礼包名称</strong></td><td width="93%" class="nrbgc1">
                                <select name="giftcode" id="giftcode" onChange="reloadPage()">
                                  <option value="0">选择礼包</option>
                                  <%
                                  DBPsRs platformgiftRs = DBPool.getInst().pQueryS(TabStor.tab_platform_gift);
                                  while(platformgiftRs.next()){
										  %>
                                  <option value="<%=platformgiftRs.getInt("num")%>"><%=platformgiftRs.getString("name")%></option>
                                  <%
                                  }
                                  %>
                              </select>
                                <font color="#FF0000">*</font></td>
							  </tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>礼包领取码</strong></td><td width="93%" class="nrbgc1"><input name="code" type="text" value="">
  <font color="#FF0000">*</font></td>
</tr>

<tr>
  <td width="10%" align="right" nowrap class="nrbgc1"><strong>角色id</strong></td>
  <td width="93%" class="nrbgc1"><input name="playerid" type="text" value=""></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>是否已领取</strong></td><td width="93%" class="nrbgc1"><label>
  <select name="gived" id="gived">
    <option value="1">是</option>
    <option value="0">否</option>
  </select>
  </label></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>领取时间</strong></td><td width="93%" class="nrbgc1"><input name="givetime" type="text" value="" onClick="new Calendar().show(this)"></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>是否分发</strong></td><td width="93%" class="nrbgc1"><select name="publish" id="publish">
    <option value="1">是</option>
    <option value="0">否</option>
  </select></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>分发时间</strong></td><td width="93%" class="nrbgc1"><input name="publishtime" type="text" value="" onClick="new Calendar().show(this)"></td></tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>过期日期</strong></td>
  <td class="nrbgc1"><input name="expiretime" type="text" id="expiretime" onClick="new Calendar().show(this)" value=""></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>是否共用码</strong></td>
  <td class="nrbgc1"><select name="repeat" id="repeat">
    <option value="1">是</option>
    <option value="0">否</option>
  </select></td>
</tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>手机号</strong></td><td width="93%" class="nrbgc1"><input name="phonenumber" type="text" value=""></td></tr>
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