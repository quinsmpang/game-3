<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="数据维护";
String perm="文件管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<%
SystemFolderBAC systemFolderBAC = new SystemFolderBAC();
systemFolderBAC.getFile(pageContext);
//response.flushBuffer();  
//out.clear();  
//out = pageContext.pushBody();  
%>

