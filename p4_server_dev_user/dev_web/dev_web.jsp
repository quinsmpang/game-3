<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<html>
<head>
<title>-</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="css/style1.css" rel="stylesheet" type="text/css">

</head>

<body>
<form id="form" name="form" method="post" action="dev_request.jsp" target="_self" enctype="multipart/form-data">
  <table width="1138" border="0">
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td width="762">----------------------web------------------------</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><label>
        <input name="web_acti_get_code" type="submit" id="web_acti_get_code" value="领取激活码">
        手机号：
        <input name="web_acti_get_code1" type="text" id="web_acti_get_code1">
        领取用户：
        <input name="web_acti_get_code2" type="text" id="web_acti_get_code2">
      </label></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="getserverlist" type="submit" id="getserverlist" value="获取服务器列表">
        联运渠道：
        <input name="getserverlist1" type="text" id="getserverlist1"></td>
    </tr>
	<tr>
      <td><input name="getplainfo" type="submit" id="getplainfo" value="获取玩家信息">
        用户名：
          <input name="getplainfo1" type="text" id="getplainfo1">
        联运渠道：
        <input name="getplainfo2" type="text" id="getplainfo2">
        VSID：
        <input name="getplainfo3" type="text" id="getplainfo3"></td>
    </tr>
	<tr>
      <td><input name="getgiftlist" type="submit" id="getgiftlist" value="官网平台礼包列表"></td>
    </tr>
	<tr>
      <td><input name="getgift" type="submit" id="getgift" value="领取平台礼包">
        角色ID：
          <input name="getgift1" type="text" id="getgift1">
        VSID：
        <input name="getgift2" type="text" id="getgift2">
        礼包编号：
        <input name="getgift3" type="text" id="getgift3"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="cbt_exchange_check" type="submit" id="cbt_exchange_check" value="封测兑换检查">
        手机号：
        <input name="cbt_exchange_check1" type="text" id="cbt_exchange_check1">
        兑换码：
        <input name="cbt_exchange_check2" type="text" id="cbt_exchange_check2"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="phonechargeexchange" type="submit" id="phonechargeexchange" value="话费活动兑换">
角色名：
  <input name="phonechargeexchange1" type="text" id="phonechargeexchange1">
  手机号：
  <input name="phonechargeexchange2" type="text" id="phonechargeexchange2">
兑换码：
<input name="phonechargeexchange3" type="text" id="phonechargeexchange3">
奖励类型：
<input name="phonechargeexchange4" type="text" id="phonechargeexchange4"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="getuserjudgmentdata" type="submit" id="getuserjudgmentdata" value="获取用户申诉判断数据">
        用户名：
          <input name="getuserjudgmentdata1" type="text" id="getuserjudgmentdata1">
IMEI：
<input name="getuserjudgmentdata2" type="text" id="getuserjudgmentdata2"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="userexist" type="submit" id="userexist" value="判断用户是否存在">
        帐号渠道
        ：
          <input name="userexist1" type="text" id="userexist1">
          用户名
          ：
      <input name="userexist2" type="text" id="userexist2"></td>
    </tr>
	<tr>
      <td><input name="getuserserverlist" type="submit" id="getuserserverlist" value="获取指定用户的服务器列表">
帐号渠道
        ：
  <input name="getuserserverlist1" type="text" id="getuserserverlist1">
用户名
          ：
<input name="getuserserverlist2" type="text" id="getuserserverlist2"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
  </table>
</form>
</body>

</html>