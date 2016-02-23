<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="游戏管理";
String perm="邮件管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("发送对象","tgr",new String[]{"等于"});
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

SysMailBAC sysMailBAC = SysMailBAC.getInstance();
	
JSONObject xml=sysMailBAC.getJsonPageList(sqlS.whereString(),showorder+" "+ordertype,ToolFunc.str2int(pagenum),ToolFunc.str2int(rpp,10));

	
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
	var w=650,h=680,newwindow;
	var url="sysmail_edit.jsp";
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function modify(id)
{
	var w=650,h=680,newwindow;
	var url="sysmail_edit.jsp?id="+id;
	var wName="";
	newwindow=window.open(url,wName,"width="+w+",height="+h+",scrollbars=yes");
	newwindow.moveTo((window.screen.width-w)/2,(window.screen.height-h)/2);
	newwindow.focus();
}
function del(id){
	if(confirm("确定删除吗？"))
	{		
		document.getElementById("hiddenFrame").src="sysmail_del.jsp?id=" + id;
		wait();
	}
}
function disenabled(id)
{
	if(confirm("确认停发这封邮件吗？"))
	{
		wait();		
		location.href="sysmail_disenabled.jsp?id="+id;
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
                    <td nowrap background="../images/tab_midbak.gif">系统邮件</td>
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
<td align="center" nowrap>邮件标题</td>
<td align="center" nowrap>邮件内容</td>
<td align="center" nowrap>附件内容</td>
<td align="center" nowrap>对象筛选条件</td>
<td align="center" nowrap>发送对象</td>
<td align="center" nowrap>失效时间</td>
<td align="center" nowrap>创建时间</td>
<td align="center" nowrap>停发邮件</td>

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
<td align="center" nowrap><%=line.optString("title")%></td>
<td align="center" nowrap><%=line.optString("content").length()<10?line.optString("content"):"<font color='#ff0000'>点击详细内容查看</font>"%></td>
<td align="center" nowrap><%=line.optString("adjunct").length()<10?line.optString("adjunct"):"<font color='#ff0000'>点击详细内容查看</font>"%></td>
<td align="center" nowrap><%=line.optString("filtercond").length()<10?line.optString("filtercond"):"<font color='#ff0000'>点击详细内容查看</font>"%></td>
<td align="center" nowrap><%=SysMailBAC.tgrType[line.optInt("tgr")]%></td>
<td align="center" nowrap><%="<font color='#"+(MyTools.checkSysTimeBeyondSqlDate(MyTools.getTimeLong(line.optString("endtime")))?"FF0000":"336633")+"'>"+line.optString("endtime")%></td>
<td align="center" nowrap><%=line.optString("createtime")%></td>
<td align="center" nowrap>
<%
if(!MyTools.checkSysTimeBeyondSqlDate(MyTools.getTimeLong(line.optString("endtime"))) && line.optInt("tgr")==1 && line.optInt("stopsend")==0){
%>
<a href="javascript:disenabled(<%=line.optInt("id")%>)">停发邮件</a>
<%
} else {
%>
<font color="#999999">停发邮件</font>
<%
}
%>
</td>

<td align="center" nowrap>
	<img src="../images/icon_modify.gif" width="16" height="16" alt="Modify" style="cursor:hand" onClick="modify(<%=id%>)">
</td>
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
<td class="btntd" style="cursor:hand" nowrap onselectstart="return false" onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onClick="add()" height="21">
	<img src="../images/icon_adddepart.gif" width="16" height="16" align="absmiddle"> 新建邮件
</td>
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
