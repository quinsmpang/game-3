<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="系统功能";
String perm="指令统计";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String startTime = Tools.strNull(request.getParameter("startTime"));
String endTime = Tools.strNull(request.getParameter("endTime"));
String submit = request.getParameter("Submit");
if(startTime==null || startTime.equals("")) {
startTime = Tools.getCurrentDateStr();
}
%>

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link href="../css/style1.css" rel="stylesheet" type="text/css">
<script src="../js/common.js"></script>
<script language="javascript" type="text/javascript" src="../My97DatePicker/WdatePicker.js"></script>
<script>
var allValue=new Object();
allValue.startTime="<%=startTime%>";
allValue.endTime="<%=endTime%>";
</script>
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
                    <td nowrap background="../images/tab_midbak.gif">游戏指令统计</td>
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
									  <td><label>
									    开始日期
									        <input type="text" name="startTime"  id="startTime" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" >
									        <img onclick="WdatePicker({el:'startTime',dateFmt:'yyyy-MM-dd'})" src="../My97DatePicker/skin/datePicker.gif" width="16" height="22" align="absmiddle">
									  结束日期
									  		<input type="text" name="endTime"   id="endTime" 	onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" >
									  		<img onclick="WdatePicker({el:'endTime',dateFmt:'yyyy-MM-dd'})" src="../My97DatePicker/skin/datePicker.gif" width="16" height="22" align="absmiddle">
									  <input type="submit" name="Submit" value="统计">
									  </label></td>
									</tr>
								</table>								
								<table width="100%" border="0" cellpadding="2" cellspacing="1"
									class="tbbgc1">
									<tr class="listtopbgc">
										<td width="10" align="center" nowrap>No.</td>
										<td align="center" nowrap>指令编号</td>
										<td align="center" nowrap>指令名</td>
										<td align="center" nowrap>总耗时(毫秒)</td>
										<td align="center" nowrap>执行次数</td>
										<td align="center" nowrap>平均耗时(毫秒)</td>
									</tr>
									
									<%
									if(submit!=null)
									{
									JSONArray list = ActStatBAC.getInstance().statAct(pageContext);
									for(int i=0;list!=null && i<list.length();i++)
									{
									JSONObject line = (JSONObject)list.opt(i);
									%>
									<tr class="nrbgc1">
										<td align="center" nowrap><%=i+1%></td>
										<td align="center" nowrap><%=line.optString("act")%></td>
										<td align="center" nowrap><%=line.optString("actname")%></td>
										<td align="center" nowrap><%=line.optInt("totaltime")%></td>
										<td align="center" nowrap><%=line.optInt("times")%></td>
										<td align="center" nowrap><%if(line.optInt("times")!=0){%><%=line.optInt("totaltime")/line.optInt("times")%><%}%></td>
									</tr>
									<%
									}									
									}
									%>
								</table>
								<br>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
									  <td align="right">&nbsp;</td>
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
<script>
autoChoose(allValue);
</script>
</body>
</html>
