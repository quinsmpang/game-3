<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="系统功能";
String perm="表管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
JSONArray tableArr = null;

int useDB = Tools.str2int(request.getParameter("useDB"));
if(useDB==0)useDB=1;
String statBtn = request.getParameter("statBtn");
int showTotal = Tools.str2int(request.getParameter("showTotal"));
int showMinId = Tools.str2int(request.getParameter("showMinId"));
int showMaxId = Tools.str2int(request.getParameter("showMaxId"));
if(statBtn!=null)
{
tableArr = SystemTableBAC.getInstance().getTableList(pageContext);
}
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
allValue.useDB="<%=useDB%>";
allValue.showTotal="<%=showTotal%>";
allValue.showMinId="<%=showMinId%>";
allValue.showMaxId="<%=showMaxId%>";
</script>
<script>

function checkForm()
{
	wait();
	//document.forms[0].submit();
	return true;
}
</script>
</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="" onSubmit="return checkForm()">
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
                <td valign="top" align="center"><table width="95%" border="0" cellspacing="1" cellpadding="2">
                  <tr>
                    <td><strong>数据库选择：</strong>
                      <select name="useDB" id="useDB">
                        <option value="1">主库</option>
						<option value="2">备库</option>
                        <option value="3">日志库</option>						
                        <option value="4">报表库</option>
                      </select>
                      <input name="showTotal" type="checkbox" id="showTotal" value="1">
                                  记录数
                                  <input name="showMinId" type="checkbox" id="showMinId" value="1">
                                  最小id
                                  <input name="showMaxId" type="checkbox" id="showMaxId" value="1">
最大id                                                            
                      <input name="statBtn" type="submit" id="statBtn" value="查询">                      </tr>
                </table>
                <table width="95%" border="0" cellspacing="1" cellpadding="2">
                  <tr>
                    <td align="center"><%//<a href="system_table_clearplayer.jsp?serverId=-1" target="hiddenFrame" onClick="return confirm('确定清除全部玩家数据表并重建id序列吗？')">清除全部玩家数据表</a>%> <a href="system_table_createseq.jsp?useDB=<%=useDB%>" target="hiddenFrame" onClick="return confirm('确定批量建立不存在的sequence吗？')">批量建立不存在的sequence</a></td>
                    <td align="center"><a href="system_table_clearseq.jsp?useDB=<%=useDB%>" target="hiddenFrame" onClick="return confirm('确定删除全部sequence吗？')">删除全部sequence</a></td>
                  </tr>
                </table>
                <table width="95%" border="0" cellspacing="1" cellpadding="2">
                  <tr>
                    <td align="center"><table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                        <tr align="center" class="listtopbgc">
                          <td width="15" nowrap >No.</td>
						  <td align="center" nowrap>表名</td>                         
                          <td align="center" nowrap>记录数</td>                         
                          <td align="center" nowrap>最小id</td>
                          <td align="center" nowrap>最大id</td>
                          <td align="center" nowrap>专用sequence</td>
                          <td align="center" nowrap>min_value</td>
                          <td align="center" nowrap>currval</td>
                          <td align="center" nowrap>last_number</td>
                          <td align="center" nowrap>索引</td>
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
                          <td align="center" nowrap><%=line.optString("table_name")%></td>                        
                          <td align="center" nowrap><%=line.optString("maxrows")%></td>                                                
                          <td align="center" nowrap><%=line.optString("minId")%></td>
                          <td align="center" nowrap><%=line.optString("maxId")%></td>
                          <td align="center" nowrap>
                          <%if(!line.optString("seqName").equals("")){%><%=line.optString("seqName")%> <a href="system_table_delseq.jsp?useDB=<%=useDB%>&seqName=<%=line.optString("seqName")%>" target="hiddenFrame" onClick="return confirm('确定删除序列发生器<%=line.optString("seqName")%>吗？')">删除</a><%}else{%><a href="system_table_addseq.jsp?useDB=<%=useDB%>&tbName=<%=line.optString("table_name")%>" target="hiddenFrame">创建</a><%}%></td>
                          <td align="center" nowrap><%if(!line.optString("seqName").equals("")){%><%=line.optInt("min_value")%><%}%></td>
                          <td align="center" nowrap><%if(!line.optString("seqName").equals("")){%><%=line.optInt("currVal")%><%}%></td>
                          <td align="center" nowrap><%if(!line.optString("seqName").equals("")){%><%=line.optInt("last_number")%><%}%></td>
                          <td align="center" nowrap><%int count=SystemTableBAC.getInstance().getIndexCount(useDB,line.optString("table_name"));%><%if(count>0){%><a href="system_table_indexlist.jsp?useDB=<%=useDB%>&tbName=<%=line.optString("table_name")%>"><%=count%></a><%}else{%><%=count%><%}%></td>
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

</form>
<script>
autoChoose(allValue);
</script>
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</body>
</html>
