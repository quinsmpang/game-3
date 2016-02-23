<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="游戏管理";
String perm="激活码";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("标签","mark",new String[]{"等于","包含"});
tmpWhereColumn.add("手机号码","phone",new String[]{"等于"});
tmpWhereColumn.add("激活用户","activate_user",new String[]{"等于"});
tmpWhereColumn.add("激活码","code",new String[]{"等于"});
tmpWhereColumn.add("id","id",new String[]{"等于"});
WhereColumn[] theWhereColumn=tmpWhereColumn.getWhereColumns();

OrderColumn tmpOrderColumn=new OrderColumn();
tmpOrderColumn.add("分发时间","publish_time");
tmpOrderColumn.add("id","id");

OrderColumn[] theOrderColumn=tmpOrderColumn.getOrderColumns();

ActivateCodeBAC activateCodeBAC = ActivateCodeBAC.getInstance();

JSONObject xml=activateCodeBAC.getPageList(pageContext);
//System.out.println(xml);
%>
<%@include file="inc_list_getparameter.jsp"%>
<%
String publish = Tools.strNull(request.getParameter("publish"));
String activated = Tools.strNull(request.getParameter("activated"));
String lottery = Tools.strNull(request.getParameter("lottery"));
int method = Tools.str2int(request.getParameter("method"));

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
allValue.publish="<%=publish%>";
allValue.activated="<%=activated%>";
allValue.method="<%=method%>";
allValue.lottery="<%=lottery%>";
</script>
<script>
function batchAdd()
{
	var w=340,h=290,newwindow;
	var url="activatecode_batchadd.jsp";
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function add()
{
	var w=355,h=502,newwindow;
	var url="activatecode_edit.jsp";
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function modify(id)
{
	var w=355,h=502,newwindow;
	var url="activatecode_edit.jsp?id="+id;
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h);
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function del(id){
	if(confirm("确定删除吗？"))
	{		
		document.getElementById("hiddenFrame").src="activatecode_del.jsp?id="+id;
		wait();
	}
}
function delAll(){
	if(confirm("确定删除全部激活码吗？"))
	{		
		document.getElementById("hiddenFrame").src="activatecode_delall.jsp";
		wait();
	}
}
function exportPhone()
{
	var theForm=document.forms[0];
	theForm.encoding="application/x-www-form-urlencoded";
	theForm.target="";
	theForm.action="activatecode_export_phone_excel.jsp";
	theForm.submit();	
	
	theForm.action="";
}
function checkForm()
{
	var theForm=document.forms[0];
	theForm.encoding="application/x-www-form-urlencoded";
	theForm.action="";	
	theForm.submit();
}
function importChannelPhone()
{
	var w=490,h=158,newwindow;
	var url="activatecode_import_excel.jsp";
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function getCode()
{
	if(document.getElementById("mark").value=="")
	{
		alert("请填写标签");
		return;
	}
	if(document.getElementById("amount").value=="")
	{
		alert("请填写数量");
		return;
	}
	if(confirm("确定获取"+document.getElementById("amount").value+"个渠道激活码吗？"))
	{
		location.href="activatecode_export_code_excel.jsp?amount="+document.getElementById("amount").value;
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
                    <td nowrap background="../images/tab_midbak.gif">激活码管理</td>
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
                      <td align="right">
                        
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td>&nbsp;</td>
                            </tr>
                          </table>
						  
                          <%@ include file="inc_list_top.jsp"%>
                          <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                            <tr align="center" class="listtopbgc"> 
                              <td >No.</td>
                              <td align="center" nowrap>id</td>
                              <td align="center" nowrap>激活码</td>
							  <td align="center" nowrap>标签</td>
							  <td align="center" nowrap>
							    <select name="publish" id="publish" class="select1" onChange="document.forms[0].submit()">
							      <option value="">分发状态</option>
							      <option value="1">已分发</option>
							      <option value="0">未分发</option>
						        </select>
								</td>
								<td align="center" nowrap>分发时间</td>
								<td align="center" nowrap>分发用户</td>
								<td align="center" nowrap><select name="method" id="method" class="select1" onChange="document.forms[0].submit()">
                                    <option value="0">获取方式</option>
                                    <option value="1">短信</option>
                                    <option value="2">网站</option>
									<option value="3">内部</option>
									<option value="4">渠道</option>
                                  </select></td>
								<td align="center" nowrap>
								  <select name="activated" id="activated" class="select1" onChange="document.forms[0].submit()">
                                    <option value="">激活状态</option>
                                    <option value="1">已激活</option>
                                    <option value="0">未激活</option>
                                  </select></td>
								<td align="center" nowrap>激活用户</td>
								<td align="center" nowrap>手机号</td>
								<td align="center" nowrap>激活时间</td>
								<td align="center" nowrap>预约开启日期</td>
								<td align="center" nowrap><select name="lottery" id="lottery" class="select1" onChange="document.forms[0].submit()">
                                    <option value="">是否参加抽奖</option>
                                    <option value="1">已参加</option>
                                    <option value="0">未参加</option>
                                  </select></td>
								<td align="center" nowrap>参加时间</td>
								<td align="center" nowrap>操作者</td>
								<td align="center" nowrap>生成时间</td>
								
                              <td nowrap >操作</td>
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
                              <td align="center" nowrap><%=line.optString("id")%></td>
                              <td align="center" nowrap><%=line.optString("code")%></td>
							  <td align="center" nowrap><%=line.optString("mark")%></td>
							  <td align="center" nowrap><%=line.optInt("publish")==1?"<font color='#336633'>已分发</font>":"<font color='#FF0000'>未分发</font>"%></td>
                            <td align="center" nowrap><%=line.optString("publish_time")%></td>	
							<td align="center" nowrap><%=line.optString("publish_user")%></td>
							<td align="center" nowrap><%if(line.optInt("method")==1){%>短信<%}else if(line.optInt("method")==2){%>网站<%}else if(line.optInt("method")==3){%>内部<%}else if(line.optInt("method")==4){%>渠道<%}%></td>
							<td align="center" nowrap><%=line.optInt("activated")==1?"<font color='#336633'>已激活</font>":"<font color='#FF0000'>未激活</font>"%></td>
							<td align="center" nowrap><%=line.optString("activate_user")%></td>
							<td align="center" nowrap><%=line.optString("phone")%></td>
							<td align="center" nowrap><%=line.optString("activate_time")%></td>
							<td align="center" nowrap><%=Tools.toShortDateStr(line.optString("starttime"))%></td>
							<td align="center" nowrap><%=line.optInt("lottery")==1?"<font color='#336633'>已参加</font>":"<font color='#FF0000'>未参加</font>"%></td>
							<td align="center" nowrap><%=line.optString("lottery_time")%></td>
							<td align="center" nowrap><%=line.optString("createuser")%></td>
							<td align="center" nowrap><%=line.optString("create_time")%></td>
							

                              <td align="center" nowrap><img src="../images/icon_modify.gif" width="16" height="16" alt="Modify" style="cursor:hand" onClick="modify(<%=id%>)"> <img src="../images/icon_del2.gif" width="16" height="16" alt="Delete" style="cursor:hand" onClick="del(<%=id%>)"></td>
                            </tr>
                            <%
							}		
							%>
                          </table>
                          <%@ include file="inc_list_bottom.jsp"%>
                          <table border="0" cellspacing="1" cellpadding="2">
                            <tr>
							<td align="right">
							  </td>
							<td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="batchAdd()" height="21"><img src="../images/icon_adddepart.gif" width="16" height="16" align="absmiddle"> 
                                      批量添加</td>
                                  </tr>
                                </table>
							  </td>
                              <td align="right">
							  <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="add()" height="21"><img src="../images/icon_adddepart.gif" width="16" height="16" align="absmiddle"> 
                                      添加</td>
                                  </tr>
                                </table>
							  </td>
							  <td align="right">
							  <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr> 
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="delAll()" height="21"><img src="../images/delall.png" align="absmiddle"> 全部删除</td>
                                  </tr>
                                </table>
							  </td>
                            </tr>
                          </table>
                        
                          <table border="0" cellspacing="1" cellpadding="2">
                            <tr>
							<td align="right">标签：							  
							  <input name="mark" type="text" id="mark">
							</td>
							<td align="right">数量：							  
							  <input name="amount" type="text" id="amount" size="5">
							  </td>
                              <td align="right">
                              <table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr>
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="getCode()" height="21"><img src="../images/icon_excel.gif" align="absmiddle"> 获取渠道激活码</td>
                                  </tr>
                              </table>
                              </td> 							                              
                            </tr>
                          </table>                        
                          <table border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right">注意：获取的渠道激活码会自动设置为已分发状态</td>
                            </tr>
                          </table>
                          <table border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right" nowrap>
                              激活条件：                             
                                <select name="activate_condition" id="activate_condition">
                                  <option value="">全部</option>
                                  <option value="1">已激活</option>
                                  <option value="0">未激活</option>
                                </select>
                              获取方式：
							<input name="method1_condition" type="checkbox" id="method1_condition" value="1">短信
							<input name="method2_condition" type="checkbox" id="method2_condition" value="1">网站
							<input name="method3_condition" type="checkbox" id="method3_condition" value="1">内部
							<input name="method4_condition" type="checkbox" id="method4_condition" value="1">渠道
							</td>
                              <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                      <tr>
                                        <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="exportPhone()" height="21"><img src="../images/icon_excel.gif" align="absmiddle"> 导出手机号码</td>
                                      </tr>
                                  </table></td>
                            </tr>
                          </table>
                          <table border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right" nowrap></td>
                              <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                  <tr>
                                    <td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="importChannelPhone()" height="21"><img src="../images/icon_excel.gif" align="absmiddle"> 导入激活码的手机号码</td>
                                  </tr>
                              </table></td>
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
</form>
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
<script>
autoChoose(allValue);
</script>
</html>
