<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="系统功能";
String perm="表管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
JSONArray tableArr = SystemTableBAC.getInstance().getIndexJsonArray(pageContext);
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
    <td valign="top" > 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td > 
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif" width="6" height="22"></td>
                    <td nowrap background="../images/tab_midbak.gif">表管理</td>
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
                              <td align="right"> <table border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" height="21" onClick="history.back()"><img src="../images/icon_return.gif" width="16" height="16" align="absmiddle"> 
                                      返回</td>
                                  </tr>
                                </table></td>
                            </tr>
                    </table>
				<table width="95%" border="0" cellspacing="1" cellpadding="2">
                  <tr>
                    <td align="center"><table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                        <tr align="center" class="listtopbgc">
                          <td width="15" nowrap >No.</td>
						  <td align="center" nowrap>索引名</td>                         
                          <td align="center" nowrap>字段名</td>                         
                          </tr>    
						<%
						int num=0;
					
						for(int i=0;tableArr!=null && i<tableArr.length();i++)
						{
						JSONObject line = tableArr.optJSONObject(i);
						num++;
						%>                   
                        <tr class="nrbgc1">
                          <td align="center" nowrap><%=num%></td>
                          <td align="center" nowrap><%=line.optString("index_name")%></td>                        
                          <td align="center" nowrap><%=line.optString("column_name")%></td>                                                
                          </tr> 
                        <%}                        
                        %>                     
                      </table>
                      <table width="100%" border="0" cellspacing="1" cellpadding="2">
                        <tr>
                          <td></td>
                        </tr>
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
