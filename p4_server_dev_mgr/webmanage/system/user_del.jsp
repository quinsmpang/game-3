<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="用户管理";
%>
<%@ include file="inc_checkperm.jsp"%>
<%
int id=ToolFunc.str2int(request.getParameter("id"));

UserBAC userBAC = new UserBAC();
UserRoleBAC userRoleBAC = new UserRoleBAC();
JSONObject userRs = userBAC.getJsonObj("id=" + id);
ReturnValue rv = userBAC.del("id=" + id);
userRoleBAC.del("userId=" + id);
String opusername = (String)pageContext.getSession().getAttribute("username");
TBLogParameter  parameter=TBLogParameter.getInstance();
parameter.addParameter("note", "删除用户："+userRs.getString("username"));
LogBAC.addLog(opusername,"系统用户",parameter.toString(),IPAddressUtil.getIp(request));
%>
<script>
	alert("<%=rv.info%>");
	parent.document.forms[0].submit();
</script>
