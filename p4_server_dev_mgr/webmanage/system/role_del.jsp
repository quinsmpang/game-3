<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="权限分组";
%>
<%@ include file="inc_checkperm.jsp"%>
<%
int id=ToolFunc.str2int(request.getParameter("id"));

RoleBAC roleBAC = new RoleBAC();
UserRoleBAC userRoleBAC = new UserRoleBAC();
JSONObject roleRs = roleBAC.getJsonObj("id=" + id);
ReturnValue rv = roleBAC.del("id=" + id);
userRoleBAC.del("roleId=" + id);
String opusername = (String)pageContext.getSession().getAttribute("username");
TBLogParameter  parameter=TBLogParameter.getInstance();
parameter.addParameter("note", "删除组："+roleRs.getString("rolename"));
LogBAC.addLog(opusername,"权限分组",parameter.toString(),IPAddressUtil.getIp(request));
%>
<script>
	alert("<%=rv.info%>");
	parent.document.forms[0].submit();
</script>