<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="版本更新";
String perm="升级补丁";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String channel = request.getParameter("channel");
%>
<script src="../js/meizzDate.js"></script>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>

<script>
function del(id)
{
	if(confirm("确认删除吗？"))
	{
		wait();		
		document.getElementById("hiddenFrame").src="patch_ver_del.jsp?id=" + id;
	}
}
function modify(id)
{
openWindow("patch_ver_edit.jsp?id=" + id,"modify",430,400,true,true);
}
function add()
{
openWindow("patch_ver_edit.jsp","add",430,400,true,true);
}

</script>
<script>
var allValue=new Object();
allValue.channel="<%=channel%>";
</script>
<link href="../css/style1.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EFEFEF">

<form name="form1" method="post">
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
                    <td nowrap background="../images/tab_midbak.gif">升级补丁</td>
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
                <td rowspan="2" bgcolor="#FFFFFF" width="1"><img src="images/spacer.gif" width="1" height="1"></td>
                <td valign="top" align="center"> 
                  <table width="95%" border="0" cellspacing="1" cellpadding="2">
							<tr>
								<td>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td>
                                渠道筛选：
                                  <select name="channel" id="channel" onChange="document.forms[0].submit()">
											<option value="">全部渠道</option>
											<%
												DBPsRs channelRs = DBPool.getInst().pQueryS(TabStor.tab_channel);
												while (channelRs.next()) {
											%>
											<option value="<%=channelRs.getString("code")%>"><%=channelRs.getString("name")%>(<%=channelRs.getString("code")%>)
											</option>
											<%
												}
											%>
									</select>
									</td>
									</tr>
								</table>
								
								<table width="100%" border="0" cellpadding="2" cellspacing="1"
									class="tbbgc1">
									<tr class="listtopbgc">
										<td width="10" align="center" nowrap>No.</td>
										<td align="center" nowrap>渠道</td>
										<td align="center" nowrap>包名</td>
										<td align="center" nowrap>平台</td>
										<td align="center" nowrap>原版本号</td>
										<td align="center" nowrap>新版本号</td>
										<td align="center" nowrap>补丁文件名</td>
										<td align="center" nowrap>文件大小</td>
										<td align="center" nowrap>文件CRC</td>
										<td align="center" nowrap>时间</td>
									
										<td align="center" nowrap>下载地址</td>
										<td align="center" nowrap>操作</td>
									</tr>
									
									<%
									SqlString sqlS = new SqlString();
									if(channel!=null && !channel.equals(""))
									{
										sqlS.add("tab_version_patch.channel",channel);
									}
									
									PatchVerBAC patchVerBAC = PatchVerBAC.getInstance();
									int num=1;
								  //JSONObject xml = patchVerBAC.getJsonObjs(null,"id");	
								  JSONObject xml = patchVerBAC.getJsonObjs("select tab_version_patch.*,tab_channel.name as channelname from tab_version_patch left join tab_channel on tab_version_patch.channel=tab_channel.code "+sqlS.whereStringEx()+" order by tab_version_patch.channel asc,tab_version_patch.fromversion ASC");								
									JSONArray list=null;
									if(xml!=null)
									{									
										list = xml.optJSONArray("list");
									}
									String serverUrl = ServerConfig.dl_apk_url;									
									
									for(int i=0;list!=null && i<list.length();i++)
									{
										JSONObject line = (JSONObject)list.opt(i);									
										int platform = line.optInt("platform");
										String platformFolder="";
										if(platform==1)
										{
											platformFolder="android";
										}
										else
										if(platform==2)
										{
											platformFolder="ios";
										}
									%>
									<tr class="nrbgc1">
										<td align="center" nowrap><%=num++%></td>
										<td align="center" nowrap><%=line.optString("channelname")%>(<%=line.optString("channel")%>)</td>
										<td align="center" nowrap><%=line.optString("packagename")%></td>
										<td align="center" nowrap><%=platformFolder%></td>		
										<td align="center" nowrap><%=line.optString("fromversion")%></td>
										<td align="center" nowrap><%=line.optString("toversion")%></td>
										<td align="center" nowrap><%=line.optString("patchfile")%></td>
										<td align="center" nowrap><%=line.optInt("filesize")%></td>
										<td align="center" nowrap><%=line.optString("crc")%></td>
										<td align="center" nowrap><%=line.optString("savetime")%></td>
										
										<td align="center" nowrap><a href="<%=serverUrl%><%=platformFolder+"/"%><%=line.optString("channel")%>/<%=line.optString("patchfile")%>"><%=serverUrl%><%=platformFolder+"/"%><%=line.optString("channel")%>/<%=line.optString("patchfile")%></a></td>
										<td align="center" nowrap><img
											src="../images/icon_modify.gif" alt="修改"
											align="absmiddle" style="cursor: hand"
											onClick="modify(<%=line.optInt("id")%>)">
											<img
											src="../images/icon_del2.gif" alt="删除" align="absmiddle"
											style="cursor: hand" onClick="del(<%=line.optInt("id")%>)"></td>
									</tr>
									<%
									}
									%>
								</table>
								<br>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td align="right">
										<table width="50" border="0" cellspacing="0" cellpadding="2">
											<tr>
												<td height="21" nowrap class="btntd" style="cursor: hand"
													onMouseDown="this.className='btntd_mousedown'"
													onMouseUp="this.className='btntd'"
													onMouseOut="this.className='btntd'"
													onselectstart="return false" onClick="add()"><img
													src="../images/icon_adddepart.gif" width="16" height="16"
													align="absmiddle"> 添加</td>	
											</tr>
								</table>
								</td>
							</tr>
						</table>
						</td>						
					</tr>

				</table>			
				
                </td>
                <td rowspan="2" bgcolor="#848284" width="1"><img src="images/spacer.gif" width="1" height="1"></td>
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
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
</html>
