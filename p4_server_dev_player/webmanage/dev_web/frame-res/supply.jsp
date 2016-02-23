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
      <td>国战</td>
    </tr>
	<tr>
      <td><input name="supply_buy_money" type="submit" id="supply_buy_money" value="买铜钱"></td>
    </tr>
	<tr>
      <td><input name="supply_buy_energy" type="submit" id="supply_buy_energy" value="买体力"></td>
    </tr>
	<tr>
      <td><input name="supply_gettqcoin" type="submit" id="supply_gettqcoin" value="领取月卡金锭"></td>
    </tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
      <td><font color="#FF0000">调试功能</font></td>
    </tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
 </table>
</form>
</body>

</html>