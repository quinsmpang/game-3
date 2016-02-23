<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<html>
<head>
<title>-</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="css/style1.css" rel="stylesheet" type="text/css">

<style type="text/css">
*{margin:0;padding:0;list-style-type:none;}
a,img{border:0;}
a{color:#5e5e5e;font-size:12px;text-decoration:none;}
a:hover{color:#3366cc;text-decoration:underline;}
/* nav_menu */
.nav_menu{width:150px;margin:10px auto;}
.nav_menu dl{background:url(images/verline.gif) repeat-y 4px 0;}
.nav_menu dl dt a{height:24px;line-height:24px;overflow:hidden;color:#3366cc;font-weight:800;}
.nav_menu dl dt a span{display:inline-block;width:13px;height:15px;overflow:hidden;float:left;margin:0 5px 0 0;}
.nav_menu dl dt a.minus span{background:url(images/showbtn.gif) no-repeat;}
.nav_menu dl dt a.plus span{background:url(images/hidebtn.gif) no-repeat;}
.nav_menu dl dd li{height:24px;line-height:24px;background:url(images/lineleft.gif) no-repeat 4px -6px;padding:0 0 0 18px}
</style>

<script type="text/javascript">

function getObject(objectId){
	if(document.getElementById && document.getElementById(objectId)){
		return document.getElementById(objectId);
	}else if(document.all && document.all(objectId)){
		return document.all(objectId);
	}else if(document.layers && document.layers[objectId]){
		return document.layers[objectId];
	}else{
		return false;
	}
}

function showHide(e,objname){
	var obj = getObject(objname);
	if(obj.style.display == "none"){
		obj.style.display = "block";
		e.className="minus";
	}else{
		obj.style.display = "none";
		e.className="plus";
	}
}

</script>

</head>
<body>

<%
String dir = new java.io.File(application.getRealPath(request.getServletPath())).getParent(); 

byte[] fileData = ToolFunc.getBytesFromFile(dir+"/dev_frame.txt");
String fileText = null;
try {
	fileText = new String(fileData, "UTF-8");
} catch (Exception e) {
	e.printStackTrace();
}

String[] titlearr = Tools.splitStr(Tools.getStrProperty(fileText, "title"), ",");
%>

<div class="nav_menu"> 
	<%
	for(int i = 0; titlearr != null && i < titlearr.length; i++){
		String[][] dataLib = Tools.getStrLineArrEx2(fileText, titlearr[i]+"START", titlearr[i]+"END");
		if(dataLib!=null && dataLib.length==1 && dataLib[0][0].equals(titlearr[i])){
			%>
	<dl>
	<dd>
	<ul>
	<li><a href="frame-res/<%=dataLib[0][1] %>" target="frame_func"><%=dataLib[0][0] %></a></li>
	</ul>
	</dd>
	</dl>		
			<%
		} else {
			%>
	<dl>
	<dt><a href="javascript:void(0);" class="minus" onClick="showHide(this,'items<%=i %>');"><span></span><%=titlearr[i] %></a></dt>
	<dd id="items<%=i %>"> 
	<ul>
	<%
	for(int j = 0; dataLib != null && j < dataLib.length; j++){
	%>
	<li><a href="frame-res/<%=dataLib[j][1] %>" target="frame_func"><%=dataLib[j][0] %></a></li>
	<%			
	}
	%>
	</ul>
	</dd>
	</dl>	
			<%
		}
	}
	%>
	
</div>

</body>
</html>
