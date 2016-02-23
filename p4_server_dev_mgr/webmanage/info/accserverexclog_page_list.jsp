<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="服务器管理";
String perm="访问服务器异常日志";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
int pagenum = ToolFunc.str2int(request.getParameter("page"), 1);
int rpp = ToolFunc.str2int(request.getParameter("rpp"), 10);
String starttime = request.getParameter("starttime");
String endtime = request.getParameter("endtime");
if(starttime == null || endtime == null || MyTools.getTimeLong(starttime)>=MyTools.getTimeLong(endtime)){
	starttime = MyTools.getDateStr();
	endtime = MyTools.getDateStr(MyTools.getCurrentDateLong()+MyTools.long_day);
}
JSONObject xml = AccServerExcBAC.getInstance().getJsonPageList("createtime>="+starttime+" and createtime<="+endtime, "id desc", pagenum, rpp);
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script src="../js/Calendar3.js"></script>

</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="">
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
                    <td nowrap background="../images/tab_midbak.gif">访问服务器异常日志</td>
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
                  <table width="100%" border="0" cellspacing="1" cellpadding="2">
                    <tr> 
                      <td>
                        
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr> 
                              <td><table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                  <td>                                
								  起始时间：
                                <input name="starttime" type="text" id="starttime" value="<%=starttime %>" onClick="new Calendar().show(this);">
                              	终止时间：
                              <input name="endtime" type="text" id="endtime" value="<%=endtime %>" onClick="new Calendar().show(this);">
                              	每页行数
                              <input name="rpp" type="text" id="rpp" value="<%=rpp%>" size="2">
								<input name="Button" type="button" class="btn1" value="刷新" onClick="document.forms[0].submit()">
							  </td>
                                </tr>
                              </table>
                              </td>
                            </tr>
                          </table>
						  
<table width="100%" border="0" cellspacing="1" cellpadding="2">
<tr>
  <td nowrap>记录数:<font color="#FF0000"><%=xml!=null?xml.optInt("totalRecord"):0%></font></td>
</tr>
</table>						  
                          <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                            <tr align="center" class="listtopbgc"> 
                              <td >No.</td>
                              <td align="center" nowrap>发请求服务器</td>
<td align="center" nowrap>访问地址</td>
<td align="center" nowrap>请求信息</td>
<td align="center" nowrap>异常信息</td>
<td align="center" nowrap>异常时间</td>

                              <td >操作</td>
                            </tr>
                            <%							
							
							int count = 0;
							JSONArray list=null;
							if(xml!=null)
							{
								count=(xml.optInt("rsPageNO")-1)*rpp+1;
								list = xml.optJSONArray("list");
							}
							
							for(int i=0;list!=null && i<list.length();i++)
							{
								JSONObject line = (JSONObject)list.opt(i);
								int id = line.optInt("id");			
							%>
                            <tr class="nrbgc1">
                              <td align="center" nowrap><%=count++%></td>
                              <td align="center" nowrap><%=line.optString("sendreqserver")%></td>
<td align="center" nowrap><%=line.optString("accessurl")%></td>
<td align="center" nowrap><%=line.optString("reqinfo")%></td>
<td align="center" nowrap><%=line.optString("excinfo")%></td>
<td align="center" nowrap><%=line.optString("createtime")%></td>

                              <td align="center" nowrap><img src="../images/icon_modify.gif" width="16" height="16" alt="Modify" style="cursor:hand" onClick="modify(<%=id%>)"> <img src="../images/icon_del2.gif" width="16" height="16" alt="Delete" style="cursor:hand" onClick="del(<%=id%>)"></td>
                            </tr>
                            <%
				}								
				%>
                          </table>
                          <%@ include file="inc_list_bottom.jsp"%>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right"><input name="delid" type="hidden" id="delid">
                                <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="add()" height="21"><img src="../images/icon_adddepart.gif" width="16" height="16" align="absmiddle"> 
                                      添加</td>
                                  </tr>
                                </table></td>
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
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
</html>
