<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="system/inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<html>
<head>
<title>口袋幻兽OL后台管理系统</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<%@ include file="system/inc_website_icon.jsp"%>
<script language="JavaScript">
<!--
focus();
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
// -->
</script>

</head>

<frameset cols="111,*,0" rows="*" frameborder="NO" border="0" framespacing="0"> 
  <frame src="menu/menu.jsp" name="left" scrolling="NO" frameborder="NO">  
  <frame src="right.jsp" name="mainwindow" frameborder="NO" scrolling="yes"> 
  <frame src="" name="exitwindow" frameborder="NO" scrolling="no"> 
</frameset>
<noframes><body bgcolor="#FFFFFF" text="#000000" oncontextmenu = "return false">

</body></noframes>
</html>
