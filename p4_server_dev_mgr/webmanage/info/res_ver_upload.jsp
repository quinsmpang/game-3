<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="版本更新";
String perm="升级资源包";
%>
<%@ include file="../system/inc_checkperm.jsp"%>


<script src="../js/common.js"></script>

<%

ResVerBAC resVerBAC = ResVerBAC.getInstance();
ReturnValue rv = resVerBAC.save(pageContext);
%>
<%if(rv.success){%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
parent.opener.document.location.reload();
top.close();
</script>
<%}else{%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
</script>
<%}%>
