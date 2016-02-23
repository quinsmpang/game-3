<%@ page contentType="text/html; charset=UTF-8"%>
<%
Cookie[] cookies = request.getCookies();
String username = "";
String pwd = "";
String saveUsername="";
String savePwd="";
if(cookies!=null)
{
	//System.out.println("cookies.len="+cookies.length);
	for(int i=0;i<cookies.length;i++)
	{
		//System.out.println(cookies[i].getName()+"="+cookies[i].getValue());
		if(cookies[i].getName().equals("userName")) 
		{
			username = cookies[i].getValue();
		}else
		if(cookies[i].getName().equals("pwd")) 
		{
			pwd = cookies[i].getValue();
		}else
		if(cookies[i].getName().equals("saveUsername")) 
		{
			saveUsername = cookies[i].getValue();
		}
		else
		if(cookies[i].getName().equals("savePwd")) 
		{
			savePwd = cookies[i].getValue();
		}
	}
}
%>
<html>
<head>
<title>口袋幻兽OL后台管理系统</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="system/inc_website_icon.jsp"%>
<script src="js/common.js"></script>
<SCRIPT TYPE="text/javascript">
var havesubmit=0;
function newImage(arg) {
	if (document.images) {	
		rslt = new Image();
		rslt.src = arg;
		return rslt;
	}
}

function changeImages() {
	if (document.images && (preloadFlag == true)) {
		for (var i=0; i<changeImages.arguments.length; i+=2) {
			document[changeImages.arguments[i]].src = changeImages.arguments[i+1];
		}
	}
}

var preloadFlag = false;
function preloadImages() {
	if (document.images) {
		login_submit_down = newImage("images/login_0801.png");
		preloadFlag = true;
	}
}
var havesubmit=false;
function checkForm(){
	if(!havesubmit)
	{
		var theForm=document.forms[0];
		if(trim(theForm.userName.value)==""){
			alert("请输入用户名");
			theForm.userName.focus();
			return false;
			}
		if(trim(theForm.pwd.value)==""){
			alert("请输入密码");
			theForm.pwd.focus();
			return false;
		}
		
		havesubmit = true;
		return true;
	}else
	{
		return false;
	}
}

</SCRIPT>
<style type="text/css">
<!--
input {
	border-top-width: thin;
	border-right-width: thin;
	border-bottom-width: thin;
	border-left-width: thin;
	border-top-style: none;
	border-right-style: none;
	border-bottom-style: none;
	border-left-style: none;
}
.input1 {
	background-color: #FFFFFF;
}
-->
</style>
</head>
<body bgcolor="#8d8d8d" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="images/login_gradback.png" onLoad="preloadImages()">
<form name="form1" method="post" action="login.jsp" onSubmit="return checkForm()">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="189">&nbsp;</td>
  </tr>
  <tr>
    <td align="center"><table id="Table_01" width="756" height="575" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td colspan="9"><img src="images/index_01.png" width="756" height="268" alt=""></td>
        </tr>
        <tr>
          <td colspan="3" rowspan="4"><img src="images/index_02.png" width="313" height="82" alt=""></td>
          <td width="184" height="24" colspan="5" bgcolor="#FFFFFF"><input name="userName" type="text" class="input1" onMouseOver="this.select()" value="<%=username%>" size="25"></td>
          <td rowspan="8"><img src="images/index_04.png" width="259" height="306" alt=""></td>
        </tr>
        <tr>
          <td colspan="5"><img src="images/index_05.png" width="184" height="16" alt=""></td>
        </tr>
        <tr>
          <td width="184" height="24" colspan="5" bgcolor="#FFFFFF"><input name="pwd" type="password" class="input1" id="pwd" onMouseOver="this.select()" value="<%=pwd%>" size="25"></td>
        </tr>
        <tr>
          <td colspan="5"><img src="images/index_07.png" width="184" height="18" alt=""></td>
        </tr>
        <tr>
          <td rowspan="4"><img src="images/index_08.png" width="251" height="224" alt=""></td>
          <td width="21" height="20" background="images/index_09.png"><input name="saveUsername" type="checkbox" id="saveUsername" value="1" <%if(saveUsername.equals("1"))out.print("checked");%>></td>
          <td colspan="3" rowspan="2"><img src="images/index_10.png" width="106" height="35" alt=""></td>
          <td width="22" height="20" background="images/index_11.png"><input name="savePwd" type="checkbox" id="savePwd" value="1" <%if(savePwd.equals("1"))out.print("checked");%>></td>
          <td colspan="2" rowspan="2"><img src="images/index_12.png" width="97" height="35" alt=""></td>
        </tr>
        <tr>
          <td rowspan="3"><img src="images/index_13.png" width="21" height="204" alt=""></td>
          <td><img src="images/index_14.png" width="22" height="15" alt=""></td>
        </tr>
        <tr>
          <td colspan="2" rowspan="2"><img src="images/index_15.png" width="68" height="189" alt=""></td>
          <td width="85" height="32" colspan="3" background="images/index_16.png"> 
		  <input NAME="login_submit" type=image
				 SRC="images/login_btn.png" alt="登录系统" BORDER=0 onMouseOut="this.src='images/login_btn.png'" onMouseDown="this.src='images/login_btn_over.png'"  onMouseOver="this.src='images/login_btn_over.png'"></td>
          <td rowspan="2"><img src="images/index_17.png" width="72" height="189" alt=""></td>
        </tr>
        <tr>
          <td colspan="3"><img src="images/index_18.png" width="85" height="157" alt=""></td>
        </tr>
        <tr>
          <td><img src="images/spacer.gif" width="251" height="1" alt=""></td>
          <td><img src="images/spacer.gif" width="21" height="1" alt=""></td>
          <td><img src="images/spacer.gif" width="41" height="1" alt=""></td>
          <td><img src="images/spacer.gif" width="27" height="1" alt=""></td>
          <td><img src="images/spacer.gif" width="38" height="1" alt=""></td>
          <td><img src="images/spacer.gif" width="22" height="1" alt=""></td>
          <td><img src="images/spacer.gif" width="25" height="1" alt=""></td>
          <td><img src="images/spacer.gif" width="72" height="1" alt=""></td>
          <td><img src="images/spacer.gif" width="259" height="1" alt=""></td>
        </tr>
      </table></td>
  </tr>
</table>
</form>
</body>

</html>