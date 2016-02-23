<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="玩家管理";
String perm="玩家处理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
int playerId = Tools.str2int(request.getParameter("playerId"));
int serverId = Tools.str2int(request.getParameter("serverId"));
String column = request.getParameter("column");

JSONObject changeLogJsonObj = null;

if(playerId>0 && serverId>0) {
	changeLogJsonObj = PlayerChangeLogBAC.getInstance().getJsonObjs("columnname='"+column+"' and serverid="+serverId+" and playerid="+playerId,"savedate ASC");
}


%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
</head>

<body bgcolor="#EFEFEF">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > <table width="100%"  border="0" cellspacing="1" cellpadding="2">
    <tr>
      <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
          <tr>
            <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="self.close()"> <img src="../images/icon_closewindow1.gif" width="16" height="16" align="absmiddle"> 关闭</td>
          </tr>
      </table></td>
    </tr>
  </table>
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td > 
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif" width="6" height="22"></td>
                    <td nowrap background="../images/tab_midbak.gif">玩家处理记录</td>
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
                  <table width="90%" border="0" cellspacing="1" cellpadding="2">
                    <tr> 
                      <td>
                      <table width="100%" border="0" cellspacing="1" cellpadding="2">
                        <tr>
                          <td>
                          	游戏服：<%=ServerBAC.getInstance().getNameById(serverId)%><br>
							玩家名：<%=PlayerBAC.getInstance().getNameById(playerId)%><br>
							修改项：<%=TabStor.getListVal(TabStor.tab_player_change_type, "columnname='"+column+"'", "name")%>
							</td>
                        </tr>
                      </table>
                        <table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
                        <tr>
                          <td align="center" class="listtopbgc">No</td>
                          <td align="center" class="listtopbgc">变化值</td>
                          <td align="center" class="listtopbgc">更改理由</td>
                          <td align="center" class="listtopbgc">操作人</td>
                          <td align="center" class="listtopbgc">操作时间</td>
                        </tr>
						<%
						if(changeLogJsonObj!=null)
						{
						int num=0;
						JSONArray list = changeLogJsonObj.optJSONArray("list");
						for(int j=0;j<list.length();j++)
						{
						num++;
						JSONObject line = list.optJSONObject(j);
						
						%>
                        <tr>
                          <td align="center" class="nrbgc1"><%=num%></td>
                          <td align="center" class="nrbgc1"><%=line.optString("changevalue")%></td>
                          <td align="center" class="nrbgc1"><%=line.optString("reason")%></td>
                          <td align="center" class="nrbgc1"><%=line.optString("operatername")%></td>
                          <td align="center" class="nrbgc1"><%=line.optString("savedate")%></td>
                        </tr>
						<%}
						}
						%>                       
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

</body>

</html>
