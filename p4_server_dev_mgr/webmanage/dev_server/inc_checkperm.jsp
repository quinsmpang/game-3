<%@ page contentType="text/html; charset=UTF-8"%>
<%
boolean isManager=false;

if(userObj!=null)
{
	int userId = userObj.optInt("id");	
	isManager = com.ehc.system.UserBAC.isManager(userId);
	if(!com.ehc.system.UserBAC.checkPermission(userId,model,perm))
	{
		out.print("<script>alert(\"没有权限\");</script>");	
		return;
	}	
}
else
{
	out.print("<script>alert(\"没有该用户\");</script>");		
	return;
}	
%>