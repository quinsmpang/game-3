<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="系统功能";
String perm="SQL执行";
%>
<%@ include file="../system/inc_checkperm.jsp"%>


<script src="../js/common.js"></script>

<%
SqlQueryBAC sqlQueryBAC = new SqlQueryBAC();
ReturnValue rv =sqlQueryBAC.executeBySql(pageContext);
%>
<%if(rv.success){%>
<script>
wait_end(parent);
parent.document.getElementById("result").value="<%=Tools.replace(rv.info,"\"","\\\"")%>";
</script>
<%=rv.info%>
<%}else{%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
</script>
<%}%>
