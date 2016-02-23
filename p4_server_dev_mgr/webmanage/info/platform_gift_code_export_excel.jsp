<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="游戏管理";
String perm="平台礼包码";
%>
<%@ include file="../system/inc_checkperm.jsp"%>


<script src="../js/common.js"></script>

<%
//response.setHeader("Content-Disposition", new String(("attachment;Filename=1.xls").getBytes("GBK"),"ISO8859-1"));
						
PlatformGiftCodeBac  giftCodeBAC = PlatformGiftCodeBac.getInstance();
System.out.println(Tools.getCurrentDateTimeStr()+"--调用exportCodeToExcel");
giftCodeBAC.exportCodeToExcel(pageContext);
%>

