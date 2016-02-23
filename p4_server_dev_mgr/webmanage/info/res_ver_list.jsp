<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="版本更新";
String perm="升级资源包";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
int platform = Tools.str2int(request.getParameter("platform"));
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
		document.getElementById("hiddenFrame").src="res_ver_del.jsp?id=" + id;
	}
}
function modify(id)
{
openWindow("res_ver_edit.jsp?id=" + id,"modify",500,500,true,true);
}
function add()
{
openWindow("res_ver_edit.jsp","add",500,500,true,true);
}

</script>
<script>
var allValue=new Object();
allValue.platform="<%=platform%>";
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
                    <td nowrap background="../images/tab_midbak.gif">升级资源包管理</td>
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
                  <table width="95%" border="0" cellspacing="1" cellpadding="2">
							<tr>
								<td>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td>
                                	渠道筛选：
                                  <select name="platform" id="platform" onChange="document.forms[0].submit()" >
                                    	<option value="0">平台</option>
                                    	<option value="1">android</option>
										<option value="2">ios</option>
										<option value="3">pc</option>
                                  </select></td>
									</tr>
								</table>
								<table width="100%" border="0" cellpadding="2" cellspacing="1"
									class="tbbgc1">
									<tr class="listtopbgc">
										<td width="10" align="center" nowrap>No.</td>
										<td align="center" nowrap>平台</td>
										<td align="center" nowrap>版本</td>
										<td align="center" nowrap>文件名</td>
										<td align="center" nowrap>时间</td>
										<td align="center" nowrap>必须升级</td>
										<td align="center" nowrap>文件大小</td>									
										<td align="center" nowrap>子目录</td>
										<td align="center" nowrap>下载链接</td>
										<td align="center" nowrap>操作</td>
									</tr>
									
									<%
									
									SqlString sqlS = new SqlString();
									if(platform != 0) {
										sqlS.add("TAB_VERSION_RES.platform",platform);
									}
									ResVerBAC resVerBAC = ResVerBAC.getInstance();
									int num=1;
								    JSONObject xml = resVerBAC.getJsonObjs("select TAB_VERSION_RES.* from TAB_VERSION_RES "+sqlS.whereStringEx()+" order by TAB_VERSION_RES.version ASC");
									JSONArray list=null;
									if(xml!=null)
									{									
										list = xml.optJSONArray("list");
									}
									String serverUrl = ServerConfig.dl_res_url;									
									
									for(int i=0;list!=null && i<list.length();i++)
									{
									JSONObject line = (JSONObject)list.opt(i);
									String subfolder = line.optString("subfolder");
									String platformStr = ResFilelistBAC.getPlatformFolderByPlatformNum(line.optInt("platform"));
									%>
									<tr class="nrbgc1">
										<td align="center" nowrap><%=num++%></td>
										<td align="center" nowrap><%=platformStr%>(<%=line.optInt("platform")%>)</td>
										<td align="center" nowrap><%=line.optString("version")%></td>
										<td align="center" nowrap><%=line.optString("updfile")%></td>
										<td align="center" nowrap><%=line.optString("savetime")%></td>
										<td align="center" nowrap><%=line.optInt("mustupdate")%></td>
										<td align="center" nowrap><%=line.optInt("filesize")%></td>
										
										<td align="center" nowrap><%=subfolder%></td>
										<td align="center" nowrap><a href="<%=serverUrl%><%=subfolder.equals("")?"":subfolder+"/"%><%=platformStr%>/<%=line.optString("updfile")%>"><%=serverUrl%><%=subfolder.equals("")?"":subfolder+"/"%><%=platformStr%>/<%=line.optString("updfile")%></a></td>
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
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
</html>