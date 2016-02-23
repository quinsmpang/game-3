<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="游戏管理";
String perm="兑换码";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("兑换码","code",new String[]{"包含","等于"});
tmpWhereColumn.add("手机号码","phone",new String[]{"包含","等于"});
tmpWhereColumn.add("开放日期","open_date",new String[]{"等于","大于","小于"});
WhereColumn[] theWhereColumn=tmpWhereColumn.getWhereColumns();

OrderColumn tmpOrderColumn=new OrderColumn();
tmpOrderColumn.add("录入顺序","id");

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
String published = Tools.strNull(request.getParameter("published"));
String exchanged = Tools.strNull(request.getParameter("exchanged"));

ExchangeCodeBAC exchangeCodeBAC = ExchangeCodeBAC.getInstance();
	
JSONObject xml=exchangeCodeBAC.getPageList(pageContext);
	
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script>
var allValue=new Object();
allValue.published="<%=published%>";
allValue.exchanged="<%=exchanged%>";
</script>
<script>
function add()
{
	var w=340,h=417,newwindow;
	var url="exchangecode_edit.jsp";
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function batchAdd()
{
	var w=340,h=290,newwindow;
	var url="exchangecode_batchadd.jsp";
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function modify(id)
{
	var w=340,h=417,newwindow;
	var url="exchangecode_edit.jsp?id="+id;
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function del(id){
	if(confirm("确定删除吗？"))
	{		
		document.getElementById("hiddenFrame").src="exchangecode_del.jsp?id=" + id;
		wait();
	}
}
function delAll()
{
	if(confirm("确定删除全部兑换码吗？"))
	{		
		document.getElementById("hiddenFrame").src="exchangecode_delall.jsp";
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
                    <td nowrap background="../images/tab_midbak.gif">兑换码列表</td>
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
                              <td align="center" nowrap>兑换码</td>
								<td align="center" nowrap>开放日期</td>
								<td align="center" nowrap><select name="published" id="published" class="select1" onChange="document.forms[0].submit()">
							      <option value="">分发状态</option>
							      <option value="1">已分发</option>
							      <option value="0">未分发</option>
						        </select></td>
								<td align="center" nowrap>分发时间</td>
								<td align="center" nowrap>手机号码</td>
								<td align="center" nowrap><select name="exchanged" id="exchanged" class="select1" onChange="document.forms[0].submit()">
                                  <option value="">兑换状态</option>
                                  <option value="1">已兑换</option>
                                  <option value="0">未兑换</option>
                                </select></td>
								<td align="center" nowrap>兑换时间</td>
								<td align="center" nowrap>操作者</td>
								<td align="center" nowrap>生成时间</td>

                              <td >操作</td>
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
                              <td align="center" nowrap><%=line.optString("code")%></td>
<td align="center" nowrap><%=line.optString("open_date")%></td>
<td align="center" nowrap><%=line.optInt("published")==1?"<font color='#336633'>已分发</font>":"<font color='#FF0000'>未分发</font>"%></td>
<td align="center" nowrap><%=line.optString("publish_time")%></td>
<td align="center" nowrap><%=line.optString("phone")%></td>
<td align="center" nowrap><%=line.optInt("exchanged")==1?"<font color='#336633'>已兑换</font>":"<font color='#FF0000'>未兑换</font>"%></td>
<td align="center" nowrap><%=line.optString("exchange_time")%></td>
<td align="center" nowrap><%=line.optString("createuser")%></td>
<td align="center" nowrap><%=line.optString("create_time")%></td>

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
                                <table border="0" cellspacing="1" cellpadding="2">
                                  <tr>
                                    <td align="right"></td>
                                    <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                        <tr>
                                          <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="batchAdd()" height="21"><img src="../images/icon_adddepart.gif" width="16" height="16" align="absmiddle"> 批量添加</td>
                                        </tr>
                                    </table></td>
                                    <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                        <tr>
                                          <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="add()" height="21"><img src="../images/icon_adddepart.gif" width="16" height="16" align="absmiddle"> 添加</td>
                                        </tr>
                                    </table></td>
                                    <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                        <tr>
                                          <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="delAll()" height="21"><img src="../images/delall.png" align="absmiddle"> 全部删除</td>
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
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
<script>
autoChoose(allValue);
</script>
</html>
