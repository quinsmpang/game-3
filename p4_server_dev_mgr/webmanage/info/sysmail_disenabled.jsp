<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="游戏管理";
String perm="邮件管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>


<script src="../js/common.js"></script>

<%
int id = Tools.str2int(request.getParameter("id"));
ReturnValue rv = SysMailBAC.getInstance().disenabled(id);
%>
<%if(rv.success){%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
location.replace("sysmail_page_list.jsp");
</script>
<%}else{%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
history.back();
</script>
<%}%>
