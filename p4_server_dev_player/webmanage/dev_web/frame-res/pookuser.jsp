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
      <td>波克用户管理</td>
    </tr>
	<tr>
      <td><input name="user_bindcard" type="submit" id="user_bindcard" value="绑定身份证">
        身份证：
          <input name="user_bindcard1" type="text" id="user_bindcard1">
      姓名：
      <input name="user_bindcard2" type="text" id="user_bindcard2"></td>
    </tr>
	<tr>
      <td><input name="user_getmobilevaildnum" type="submit" id="user_getmobilevaildnum" value="获取手机验证码">
        身份证：
          <input name="user_getmobilevaildnum2" type="text" id="user_getmobilevaildnum2">
        手机号：
    <input name="user_getmobilevaildnum1" type="text" id="user_getmobilevaildnum1"></td>
    </tr>
	<tr>
      <td><input name="user_bindmobile" type="submit" id="user_bindmobile" value="绑定手机">
        身份证：
          <input name="user_bindmobile3" type="text" id="user_bindmobile3">
        手机号：
        <input name="user_bindmobile1" type="text" id="user_bindmobile1">
          手机验证码
          ：
      <input name="user_bindmobile2" type="text" id="user_bindmobile2"></td>
    </tr>
	<tr>
      <td><input name="user_bindemail" type="submit" id="user_bindemail" value="绑定邮箱">
        身份证：
          <input name="user_bindemail2" type="text" id="user_bindemail2">
        邮箱：
    <input name="user_bindemail1" type="text" id="user_bindemail1"></td>
    </tr>
	<tr>
      <td><input name="user_modifypwd" type="submit" id="user_modifypwd" value="修改密码">
        原密码：
        <input name="user_modifypwd1" type="text" id="user_modifypwd1">
        新密码：
        <input name="user_modifypwd2" type="text" id="user_modifypwd2"></td>
    </tr>
	<tr>
      <td><input name="user_getsafetybindstate" type="submit" id="user_getsafetybindstate" value="获取用户安全信息绑定状态"></td>
    </tr>
 </table>
</form>
</body>

</html>