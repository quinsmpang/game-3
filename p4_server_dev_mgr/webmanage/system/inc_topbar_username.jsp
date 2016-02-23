<%@ page contentType="text/html; charset=UTF-8"%>
 -- 当前用户: <%
if(userObj!=null)
{
		out.print(userObj.optString("userName"));
}
%>