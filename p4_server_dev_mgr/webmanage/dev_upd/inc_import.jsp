<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="com.ehc.common.*"%>
<%@ page import="server.common.*"%>
<%@ page import="server.database.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="com.jspsmart.upload.*"%>
<%@ page import="org.json.*"%>
<%@ page import="server.config.*"%>
<%@ page import="com.moonic.bac.*"%>
<%@ page import="com.moonic.web.*"%>

<%@ include file="inc_getuser.jsp"%>

<%
String model="数据维护";
String perm="数据更新";
%>
<%@ include file="inc_checkperm.jsp"%>

