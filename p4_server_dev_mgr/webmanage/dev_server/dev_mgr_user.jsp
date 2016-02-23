<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<html>
<head>
<title>-</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="css/style1.css" rel="stylesheet" type="text/css">
<link href="css/bubbletips.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="http://www.jq-school.com/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="js/bubbletips.js"></script>
<script type="text/javascript" src="js/mytools.js"></script>

</head>

<body>
<form id="form" name="form" method="post" action="dev_request.jsp" target="_self" enctype="multipart/form-data">
  <table width="1138" border="0">
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td width="762">----------------------用户管理------------------------</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_openlogin" type="submit" id="console_openlogin" value="开启用户登录"></td>
    </tr>
	<tr>
      <td><input name="console_closelogin" type="submit" id="console_closelogin" value="关闭用户登录">
说明：
  <input name="console_closelogin1" type="text" id="console_closelogin1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_serverallmaintain" type="submit" id="console_serverallmaintain" value="维护所有游戏服务器">
      说明：
      <input name="console_serverallmaintain1" type="text" id="console_serverallmaintain1" size="5">
      推送消息：
      <input name="console_serverallmaintain2" type="text" id="console_serverallmaintain2" value="服务器连接被断开">
      弹出说明：
      <input name="console_serverallmaintain3" type="text" id="console_serverallmaintain3" value="看公告">
      下线方式：
      <input name="console_serverallmaintain4" type="text" id="console_serverallmaintain4" onMouseOver="showHelp(this,'0.退出游戏<br>1.退到公告界面')" onMouseOut="hideHelp()" size="5"></td>
    </tr>
	<tr>
      <td><input name="console_serverallopen" type="submit" id="console_serverallopen" value="开启所有游戏服务器"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_serveronemaintain" type="submit" id="console_serveronemaintain" value="维护指定游戏服务器">
        服务器ID：
        <input name="console_serveronemaintain1" type="text" id="console_serveronemaintain1" size="5">
        说明：
        <input name="console_serveronemaintain2" type="text" id="console_serveronemaintain2">
        推送消息：
        <input name="console_serveronemaintain3" type="text" id="console_serveronemaintain3" value="服务器连接被断开">
        弹出说明：
        <input name="console_serveronemaintain4" type="text" id="console_serverallmaintain222" value="看公告">
        下线方式：
        <input name="console_serveronemaintain5" type="text" id="console_serveronemaintain5" onMouseOver="showHelp(this,'0.退出游戏<br>1.退到公告界面')" onMouseOut="hideHelp()" size="5"></td>
    </tr>
	<tr>
      <td><input name="console_serveroneopen" type="submit" id="console_serveroneopen" value="开启指定游戏服务器">
      服务器ID：
      <input name="console_serveroneopen1" type="text" id="console_serveroneopen1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
  </table>
</form>
</body>

</html>