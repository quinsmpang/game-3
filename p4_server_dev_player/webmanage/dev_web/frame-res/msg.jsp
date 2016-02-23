<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../inc_import.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="../css/style1.css" rel="stylesheet" type="text/css">
<link href="../css/bubbletips.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="http://www.jq-school.com/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="../js/bubbletips.js"></script>
</head>
<body>

<form id="form" name="form" method="post" action="../dev_request.jsp" target="frame_result" enctype="multipart/form-data">
  <table width="1024" border="0">
	<tr>
      <td>消息</td>
    </tr>
	<tr>
      <td><input name="msg_str_send" type="submit" id="msg_str_send" value="发消息">
        频道：
          <input name="msg_str_send1" type="text" id="msg_str_send1">
      密语对象：
      <input name="msg_str_send2" type="text" id="msg_str_send2">
      内容：
      <input name="msg_str_send3" type="text" id="msg_str_send3"></td>
    </tr>
	<tr>
      <td><input name="msg_voice_send" type="submit" id="msg_voice_send" value="发语音">
        频道：
        <input name="msg_voice_send1" type="text" id="msg_voice_send1">
密语对象：
<input name="msg_voice_send2" type="text" id="msg_voice_send2"></td>
    </tr>
	<tr>
      <td><input name="msg_set_rec_gamepush" type="submit" id="msg_set_rec_gamepush" value="设置是否接收游戏推送">
        接收：
      <input name="msg_set_rec_gamepush1" type="text" id="msg_set_rec_gamepush1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
 </table>
</form>
</body>

</html>