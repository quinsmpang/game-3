<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="充值管理";
String perm="发货单查询";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("订单号","TAB_CHARGE_SEND.orderNo",new String[]{"等于"});
WhereColumn[] theWhereColumn=tmpWhereColumn.getWhereColumns();

OrderColumn tmpOrderColumn=new OrderColumn();
tmpOrderColumn.add("时间顺序","TAB_CHARGE_SEND.savetime");

OrderColumn[] theOrderColumn=tmpOrderColumn.getOrderColumns();
%>
<%@include file="inc_list_getparameter.jsp"%>
<%//解析数据
if(colvalue!=null && !colvalue.equals("")){
if(colname!=null){
if(operator.equals("等于")){
sqlS.add(colname,colvalue);
}else{
sqlS.add(colname,colvalue,"like");}
}
}

ChargeSendBAC chargeSendBAC = ChargeSendBAC.getInstance();
	
JSONObject xml=chargeSendBAC.getJsonPageList(pageContext);
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script>
function add()
{
	var w=340,h=290,newwindow;
	var url="charge_send_edit.jsp";
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function modify(id)
{
	var w=340,h=290,newwindow;
	var url="charge_send_edit.jsp?id="+id;
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function del(id){
	if(confirm("确定删除吗？"))
	{		
		document.getElementById("hiddenFrame").src="charge_send_del.jsp?id=" + id;
		wait();
	}
}
</script>
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
                    <td nowrap background="../images/tab_midbak.gif">发货单查询</td>
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
                        
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td>&nbsp;</td>
                            </tr>
                          </table>
						  
                          <%@ include file="inc_list_top.jsp"%>
                          <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                            <tr align="center" class="listtopbgc"> 
                              <td >No.</td>
							  <td align="center" nowrap>联运渠道</td>
                              <td align="center" nowrap>游戏服</td>							
							<td align="center" nowrap>订单号</td>
							<td align="center" nowrap>角色名</td>
							<td align="center" nowrap>支付金额</td>
							<td align="center" nowrap>获得钻石</td>
							<td align="center" nowrap>获得特权</td>
							<td align="center" nowrap>发货结果</td>
							<td align="center" nowrap>保存时间</td>
                            <td align="center" nowrap>操作</td>
                            </tr>
                            <%
							int count = 0;
							JSONArray list=null;
							if(xml!=null)
							{
								count=(xml.optInt("rsPageNO")-1)*ToolFunc.str2int(rpp)+1;
								list = xml.optJSONArray("list");
							}
							
							for(int i=0;list!=null && i<list.length();i++)
							{
								JSONObject line = (JSONObject)list.opt(i);
								int id = line.optInt("id");			
							%>
                            <tr class="nrbgc1">
                              <td align="center" nowrap><%=count++%></td>
							  <td align="center" nowrap><%=line.optString("channelname")%>(<%=line.optString("channel")%>)</td>
                              <td align="center" nowrap><%=line.optString("servername")%>(<%=line.optInt("serverid")%>)</td>							
							<td align="center" nowrap><%=line.optString("orderno")%></td>
							<td align="center" nowrap><%=line.optString("playername")%></td>
							<td align="center" nowrap><%=line.optInt("price")%></td>
							<td align="center" nowrap><%=line.optInt("getcoin")%></td>
							<td align="center" nowrap><%=line.optInt("getpower")%></td>
							<td align="center" nowrap><%if(line.optInt("result")==1){%><font color="#006633">成功</font><%}else if(line.optInt("result")==-1){%><font color="#FF0000">失败</font><%}%></td>
							<td align="center" nowrap><%=line.optString("savetime")%></td>
                            <td align="center" nowrap><img src="../images/icon_del2.gif" width="16" height="16" alt="Delete" style="cursor:hand" onClick="del(<%=id%>)"></td>
                            </tr>
                            <%
				}								
				%>
                          </table>
                          <%@ include file="inc_list_bottom.jsp"%>
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
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
</html>
