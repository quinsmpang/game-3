<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="数据维护";
String perm="SQL查询";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
SqlString sqlS=new SqlString();

String pagenum=request.getParameter("page");
if(pagenum==null || pagenum.equals(""))
{
pagenum="1";
}

String rpp=request.getParameter("rpp");
if(rpp==null || rpp.equals(""))
{
rpp="10";
}

String sql = Tools.strNull(request.getParameter("sql"));
sql = Tools.replace(sql,"\"","\\\"");
int useDB = Tools.str2int(request.getParameter("useDB"));
if(useDB==0)useDB=2;

SqlQueryBAC sqlQueryBAC = SqlQueryBAC.getInstance();
JSONObject xml=null;
if(sql!=null && !sql.equals(""))
{
	xml=sqlQueryBAC.queryBySql(pageContext);
}
boolean root = false;
if(userObj!=null) {
	root = userObj.optBoolean("root");
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
allValue.sql="<%=sql%>";
allValue.rpp="<%=rpp%>";
allValue.useDB="<%=useDB%>";
</script>
<script>
function checkForm()
{
	var theForm=document.forms[0];
	theForm.encoding="application/x-www-form-urlencoded";
	theForm.action="";
	document.forms[0].submit();
	wait();
}
function exportExcel()
{
	if(confirm("确定输出查询结果到excel文件吗？"))
	{
		var theForm=document.forms[0];
		theForm.encoding="application/x-www-form-urlencoded";
		theForm.target="";
		theForm.action="sqlquery_export_excel.jsp";
		theForm.submit();
		
		theForm.action="";
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
                    <td nowrap background="../images/tab_midbak.gif">表查询</td>
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
                    <td align="center"><table width="100%" border="0" cellspacing="1" cellpadding="2">
                      <tr>
                        <td valign="top" nowrap><strong>数据库选择：</strong><select name="useDB" id="useDB">
                                    <option value="1">主库</option>
									<option value="2" selected>备库</option>
                                    <option value="3">日志库</option>									
									<option value="4">报表库</option>
                                  </select>
                          <font color="#FF0000">(没有特殊需求时，禁止查主库)</font></td>
                        <td align="right" valign="top" nowrap>&nbsp;</td>
                      </tr>
                      <tr>
                        <td valign="top" nowrap>SQL查询语句：
                          <label>
                            <input name="sql" type="text" id="sql" size="110" value="">
                            <input name="Button" type="button" id="Button" onClick="checkForm()" value="查询">
                          </label></td>
                        <td align="right" valign="top" nowrap>
                        <%
                        if(root){
                        %>
                        <input name="Button2" type="button" id="Button2" onClick="exportExcel()" value="输出到excel文件">
                        <%
                        }
                        %>
                        </td>
                      </tr>
                    </table>
                      <table width="100%" border="0" cellspacing="1" cellpadding="2">
                        <tr>
                          <td nowrap>记录数:<font color="#FF0000"><%=xml!=null?xml.optInt("totalRecord"):0%></font></td>
                          <td align="right"></td>
                        </tr>
                      </table>
                      <table width="100%" border="0" cellspacing="1" cellpadding="2">
                        <tr>
                          <td nowrap>每页行数
                            <input name="rpp" type="text" class="input4right" id="rpp" value="" size="2">
                              <input name="Button3" type="button" class="btn1" value="刷新" onClick="checkForm()"></td>
                          </tr>
                      </table>
                      <%	
                      int count = 0;
						JSONArray list=null;
						JSONArray columns=null;
						if(xml!=null)
						{
							count=(xml.optInt("rsPageNO")-1)*ToolFunc.str2int(rpp)+1;
							list = xml.optJSONArray("list");
							columns=xml.optJSONArray("columns");
						}
						//System.out.println(list);
						//查列
						/*JSONArray colums=null;
						if(list!=null && list.size()>0)
						{
							JSONObject firstLine = list.optJSONObject(0);
							colums = firstLine.names();
						}
						*/					
						%>			 
                      <table width="100%" border="0" cellpadding="2" cellspacing="1" class="tbbgc1">
                        <tr align="center" class="listtopbgc">
                          <td nowrap >No.</td>
						  <td align="center" nowrap>id</td>
                          <%for(int i=0;columns!=null && i<columns.size();i++){%>
						  <%if(!columns.optString(i).toLowerCase().equals("id")){%>
                          <td align="center" nowrap><%=columns.optString(i)%></td>
                          <%}%>
                          <%}%>
                          </tr>
                        <%
						for(int i=0;list!=null && i<list.length();i++)
						{
							JSONObject line = (JSONObject)list.optJSONObject(i);
							int id = line.optInt("id");			
						%>
                        <tr class="nrbgc1">
                          <td align="center" nowrap><%=count++%></td>
                          <td align="center" nowrap><%=line.optInt("id")%></td>
                          <%for(int j=0;columns!=null && j<columns.size();j++){%>
                          <%if(!columns.optString(j).toLowerCase().equals("id")){%>
                          <td align="center" nowrap><%=line.optString(columns.optString(j))%></td>
                          <%}%>
                          <%}%>                          
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
