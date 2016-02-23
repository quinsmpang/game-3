<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="基本数据";
String perm="服务器管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>


<script src="../js/common.js"></script>

<%
ReturnValue rv=null;
String act = request.getParameter("act");
if(act==null)
{
	rv = new ReturnValue(false,"缺少参数");
}
else
{
	if(SocketServer.getInstance()!=null)
	{
		if(act.equals("start"))
		{
				rv = SocketServer.getInstance().start();
		}
		else
		if(act.equals("stop"))
		{
				rv = SocketServer.getInstance().stop();
		}	
	}
	else
	{
		rv = new ReturnValue(false,"TCP监听对象不存在");
	}
}
%>
<%if(rv.success){%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
parent.location.reload();
</script>
<%}else{%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
</script>
<%}%>
