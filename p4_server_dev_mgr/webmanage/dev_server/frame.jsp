<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
</head>
<%
String title=request.getParameter("title");
String link=request.getParameter("link");
%>
<frameset rows="20,*" border="0" framespacing="0" frameborder="NO"> 
  <frame src="frame_up.jsp?title=<%=title%>" scrolling="NO">
  <frame src="<%=link%>">
</frameset>
<noframes><body bgcolor="#FFFFFF" text="#000000">

</body></noframes>
</html>
