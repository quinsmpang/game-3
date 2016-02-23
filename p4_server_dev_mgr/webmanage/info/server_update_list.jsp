<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="系统功能";
String perm="游戏服升级";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

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
		document.getElementById("hiddenFrame").src="server_update_del.jsp?id="+id;
	}
}

function add(type)
{
openWindow("server_update_edit.jsp?type="+type,"add",800,276,true,true);
}

function clearAll()
{
	if(confirm("确认清除全部更新日志吗？"))
	{
		wait();		
		document.getElementById("hiddenFrame").src="server_update_clear.jsp";
	}
}
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
                    <td nowrap background="../images/tab_midbak.gif">服务器更新管理</td>
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
										<td>&nbsp;</td>
									</tr>
								</table>
								
								<table width="100%" border="0" cellpadding="2" cellspacing="1"
									class="tbbgc1">
									<tr class="listtopbgc">
										<td width="10" align="center" nowrap>No.</td>
										<td align="center" nowrap>服务器</td>
										<td align="center" nowrap>升级文件名</td>
										<td align="center" nowrap>上传时间</td>
										<td align="center" nowrap>文件大小</td>
									
										<td align="center" nowrap>操作</td>
									</tr>
									
									<%
										ServerBAC serverBAC = ServerBAC.getInstance();
										ServerUpdateBAC gameServerUpdateBAC = ServerUpdateBAC.getInstance();
										int num=1;
										JSONObject xml = gameServerUpdateBAC.getJsonObjs(null,"id");
										if(xml!=null)
										{									
											JSONArray list = xml.optJSONArray("list");									
											for(int i=0;list!=null && i<list.length();i++)
											{
											JSONObject line = (JSONObject)list.opt(i);
																		%>
									<tr class="nrbgc1">
										<td align="center" nowrap><%=num++%></td>
										<td align="center" nowrap><%=line.optString("server")%></td>
										<td align="center" nowrap><%=line.optString("updfile")%></td>
										<td align="center" nowrap><%=line.optString("savetime")%></td>
										<td align="center" nowrap><%=line.optInt("filesize")%></td>
										
										<td align="center" nowrap><img
											src="../images/icon_del2.gif" alt="删除" align="absmiddle"
											style="cursor: hand" onClick="del(<%=line.optInt("id")%>)"></td>
									</tr>
									<%
									}
									}
									%>
								</table>
								<br>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td align="right">
										
										  <table border="0" cellspacing="1" cellpadding="2">
                                            <tr>
                                              <td><table width="50" border="0" cellspacing="0" cellpadding="2">
                                                <tr>
                                                  <td height="21" nowrap class="btntd" style="cursor: hand"
													onMouseDown="this.className='btntd_mousedown'"
													onMouseUp="this.className='btntd'"
													onMouseOut="this.className='btntd'"
													onselectstart="return false" onClick="add(0)"><img
													src="../images/icon_putup.gif" width="16" height="16"
													align="absmiddle"> 更新管理服</td>
                                                </tr>
                                              </table></td>
											  <td><table width="50" border="0" cellspacing="0" cellpadding="2">
                                                <tr>
                                                  <td height="21" nowrap class="btntd" style="cursor: hand"
													onMouseDown="this.className='btntd_mousedown'"
													onMouseUp="this.className='btntd'"
													onMouseOut="this.className='btntd'"
													onselectstart="return false" onClick="add(1)"><img
													src="../images/icon_putup.gif" width="16" height="16"
													align="absmiddle"> 更新用户服</td>
                                                </tr>
                                              </table></td>
											  <td><table width="50" border="0" cellspacing="0" cellpadding="2">
                                                <tr>
                                                  <td height="21" nowrap class="btntd" style="cursor: hand"
													onMouseDown="this.className='btntd_mousedown'"
													onMouseUp="this.className='btntd'"
													onMouseOut="this.className='btntd'"
													onselectstart="return false" onClick="add(2)"><img
													src="../images/icon_putup.gif" width="16" height="16"
													align="absmiddle"> 更新游戏服</td>
                                                </tr>
                                              </table></td>
                                            </tr>
                                          </table>
										  
										  <table border="0" cellspacing="1" cellpadding="2">
                                            <tr>
                                              <td><table width="50" border="0" cellspacing="0" cellpadding="2">
                                                <tr>
                                                  <td height="21" nowrap class="btntd" style="cursor: hand"
													onMouseDown="this.className='btntd_mousedown'"
													onMouseUp="this.className='btntd'"
													onMouseOut="this.className='btntd'"
													onselectstart="return false" onClick="clearAll()"><img
													src="../images/delall.png" 
													align="absmiddle"> 清除更新日志</td>
                                                </tr>
                                              </table></td>
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
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
</html>
