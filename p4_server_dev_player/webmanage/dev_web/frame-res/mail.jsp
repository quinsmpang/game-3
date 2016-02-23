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
      <td>邮件</td>
    </tr>
	<tr>
      <td><input name="mail_send" type="submit" id="mail_send" value="发邮件">
        接邮件玩家ID
        ：
          <input name="mail_send1" type="text" id="mail_send1" onMouseOver="showHelp(this,'多个用“,”分隔')" onMouseOut="hideHelp()">
      邮件标题：
      <input name="mail_send2" type="text" id="mail_send2">
      邮件内容：
      <input name="mail_send3" type="text" id="mail_send3"></td>
    </tr>
	<tr>
      <td><input name="mail_getcontent" type="submit" id="mail_getcontent" value="获取邮件内容">
        邮件ID：
        <input name="mail_getcontent1" type="text" id="mail_getcontent1"></td>
    </tr>
	<tr>
      <td><input name="mail_extractadjunct" type="submit" id="mail_extractadjunct" value="提取附件">
        邮件ID：
      <input name="mail_extractadjunct1" type="text" id="mail_extractadjunct1"></td>
    </tr>
	<tr>
      <td><input name="mail_del" type="submit" id="mail_del" value="删除邮件">
        邮件ID：
      <input name="mail_del1" type="text" id="mail_del1"></td>
    </tr>
	<tr>
      <td><input name="mail_shortcut_del" type="submit" id="mail_shortcut_del" value="一键删除邮件"></td>
    </tr>
	<tr>
      <td><input name="mail_shortcut_extractadjunct" type="submit" id="mail_shortcut_extractadjunct" value="一键提取附件"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
 </table>
</form>
</body>

</html>