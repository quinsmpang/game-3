<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%
String sessionid = request.getParameter("sessionid");
if(sessionid != null) {
	CookieUtil.save(pageContext, "dev_sessionid", sessionid);
	out.print("<script language='javascript' type='text/javascript'>window.location.href='dev_frame.jsp';</script>");
} else {
	out.print("<script language='javascript' type='text/javascript'>window.close();</script>");
}
%>