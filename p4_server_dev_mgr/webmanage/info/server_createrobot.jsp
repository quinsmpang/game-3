<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="服务器管理";
String perm="游戏服务器";
%>
<%@ include file="../system/inc_checkperm.jsp"%>


<script src="../js/common.js"></script>

<%
int serverId = Tools.str2int(request.getParameter("serverId"));
ReturnValue rv = ServerBAC.getInstance().createRobot(serverId);
%>
<%if(rv.success){%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
location.replace("server_list.jsp");
</script>
<%}else{%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
history.back();
</script>
<%}%>
