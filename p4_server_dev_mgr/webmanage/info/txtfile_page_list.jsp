<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="玩家管理";
String perm="TXT管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
SqlString sqlS=new SqlString();
//设置查询条件
WhereColumn tmpWhereColumn=new WhereColumn();
tmpWhereColumn.add("服务器","serverid",new String[]{"等于"});
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
	
JSONObject xml = TxtFileBAC.getInstance().getJsonPageList(sqlS.whereString(),showorder+" "+ordertype,ToolFunc.str2int(pagenum),ToolFunc.str2int(rpp,10));

ReturnValue typeRv =  TxtFileBAC.getInstance().getFileTypeData();
JSONObject typeobj = null;
if(typeRv.success){
	typeobj = new JSONObject(typeRv.info);
} else {
	typeobj = new JSONObject();
}

int serverid = Tools.str2int(request.getParameter("serverid"));
int fileid = Tools.str2int(request.getParameter("fileid"));

String filecontent = "";

if(fileid>0){
ReturnValue contRv = TxtFileBAC.getInstance().reqGetFileContent(serverid,fileid);
if(contRv.success){
	filecontent = contRv.info;
} else {
	filecontent = contRv.info;
}
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
                    <td nowrap background="../images/tab_midbak.gif">TXT</td>
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
                              <td align="center" nowrap>服务器</td>
<td align="center" nowrap>文件类型</td>
<td align="center" nowrap>文件</td>
<td align="center" nowrap>创建时间</td>
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
                              <td align="center" nowrap><%=ServerBAC.getInstance().getNameById(line.optInt("serverid"))+"("+line.optString("serverid")+")"%></td>
<td align="center" nowrap><%=typeobj.getString(line.optString("type"))%></td>
<td align="center" nowrap>
<%="文件编号："+line.optInt("id")%><a href="?serverid=<%=line.optInt("serverid")%>&fileid=<%=line.optInt("id")%>&page=<%=pagenum%>&rpp=<%=rpp%>">&nbsp;(查看内容)</a></td>
<td align="center" nowrap><%=line.optString("createtime")%></td>
                            </tr>
                            <%
				}								
				%>
                          </table>
                          <%@ include file="inc_list_bottom.jsp"%>
                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr>
                              <td align="right"><input name="delid" type="hidden" id="delid"></td>
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
<%
if(filecontent!=null&&!filecontent.equals("")&&filecontent.startsWith("[")){
	JSONArray jsonarr = new JSONArray(filecontent);
	JSONArray headarr = jsonarr.getJSONArray(0);
	JSONArray dataarr = jsonarr.getJSONArray(1);
%>
<table border="1" align="center" cellpadding="1" cellspacing="1">
  <tr bgcolor="#00FF00">
	  <td width="100" bgcolor="#CCCCCC">文件编号：<%=fileid%></td>
  </tr>
  <tr bgcolor="#FFFF00">
  	<%
  	for(int i = 0; i < headarr.length(); i++){
	%>
    	<td width="100"><%=headarr.getString(i)%></td>
	<%
	}
	%>
  </tr>
  <%
  for(int i = 0; i < dataarr.length(); i++){
  %>
  <tr>
  <%
    JSONArray arr = dataarr.getJSONArray(i);
	for(int k = 0; k < arr.length(); k++){
  %>
   <td width="100"><%=arr.getString(k)%></td>
  <%
  	}
  %>
  </tr>
  <%
  }
}
  %>
</table>
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
</html>
