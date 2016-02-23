<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="玩家管理";
String perm="帐号管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
if(request.getParameter("isSubmit")!=null){
	ReturnValue rv = UserBAC.getInstance().converToPook(pageContext);
	%>
	<script type="text/javascript">
alert("<%=rv.info%>");
parent.location.reload();
	</script>
	<%
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
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.converfile.value==""){
		alert("请选择要上传的文件");
		return;
	}
	if(theForm.channel.value==""){
		alert("请选择联运渠道");
		return;
	}
	
	theForm.target="hiddenFrame";
	theForm.action="account_convertopook.jsp?isSubmit=1";
	theForm.submit();
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
                    <td nowrap background="../images/tab_midbak.gif">帐号管理</td>
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
<td align="right" nowrap class="nrbgc1"><strong>要转换的帐号清单</strong></td>
<td colspan="3" class="nrbgc1">
<input type="file" name="converfile">
<%
File demofile = new File(Conf.logRoot+"convertopook/demo.txt");
if(!demofile.exists()){
	StringBuffer sb = new StringBuffer();
	sb.append("\r\n");
	sb.append("data:\r\n");
	sb.append("username（帐号）\t000（帐号所属联运渠道）\r\n");
	sb.append("dataEnd");
	FileUtil fileutil = new FileUtil();
	fileutil.writeNewToTxt(Conf.logRoot+"convertopook/demo.txt", sb.toString());
}
%>
<a href="../../download.do?path=<%=Conf.logRoot+"convertopook/demo.txt"%>">下载DEMO</a><font color="#ff0000">（请保证上传文件的内容编码为UTF8）</font></td>
</tr>
<tr>
<td align="right" nowrap class="nrbgc1"><strong>转换后的联运渠道</strong></td>
<td colspan="3" class="nrbgc1">
<select name="channel", id="channel">
<option value="">选择联运渠道</option>
<%
DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
while(channelRs.next()){
	if(!channelRs.getString("platform").equals("001")){
		continue;	
	}
%>
<option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")+"（"+channelRs.getString("code")+"）"%></option>
<%
}
%>
</select>
</td>
</tr>
<tr>
<td colspan="4">
已转换列表<font color="#FF0000">（点击下载）</font>：<br>
<%
File file = new File(Conf.logRoot+"convertopook");
File[] files = file.listFiles();
for(int i = 0; files != null && i < files.length; i++){
	if(files[i].getName().indexOf("（已转换）")==-1){
		continue;
	}
%>
<img src="../images/file.png" align="absmiddle"><a href="../../download.do?path=<%=URLEncoder.encode(files[i].getAbsolutePath(),"UTF-8")%>"><%=files[i].getName()%></a><br>
<%
}
%>
</td>
</tr>
                            </table>
							
                            <table width="100%"  border="0" cellspacing="1" cellpadding="2">
                              <tr>
                                <td align="right">
				<input type="hidden" name="id"/>
                                  <table width="50" border="0" cellspacing="0" cellpadding="2">
                                    <tr>
                                      <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="checkForm()"><img src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 转换</td>
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
</body>
</html>