<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../system/inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%String title = request.getParameter("title");%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
</head>

<body bgcolor="#EFEFEF" leftmargin="0" topmargin="0">
<table width="100%" border="0" cellspacing="0" cellpadding="2">
  <tr> 
    <td bgcolor="#1168a9"  background="../images/title_back.gif" > 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr style="color:#FFFFFF"> 
          <td><%=title%><%@ include file="../system/inc_topbar_username.jsp"%></td>
          <td align="right">&nbsp;</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr> 
    <td bgcolor="#000000" height="1" ></td>
  </tr>
</table>
</body>
</html>
