<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<script src="../js/common.js"></script>
<%
String oldpwd = request.getParameter("oldpwd").trim();
String newpwd = request.getParameter("newpwd").trim();
%>

<%
int userId=0;

if(userObj!=null)
{
	userId=userObj.optInt("id");
}
UserBAC userBAC = new UserBAC();
ReturnValue rv = UserBAC.changePwd(request,userId,oldpwd,newpwd);
%>
<%
if(rv.success){
	String ip = IPAddressUtil.getIp(request);
	LogBAC.addLog((String)session.getAttribute("username"),"修改密码","",ip);
%>
<script>
parent.location.replace("pwd_change_complete.jsp?pwd=<%=newpwd%>");
</script>
<%}else{%>
<script>
wait_end(parent);
alert("<%=rv.info%>");
</script>
<%}%>


