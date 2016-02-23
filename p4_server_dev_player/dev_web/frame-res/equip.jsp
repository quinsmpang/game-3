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
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>物品</td>
    </tr>
	<tr>
      <td><input name="equip_stre" type="submit" id="equip_stre" value="强化">
物品ID
        ：
          <input name="equip_stre1" type="text" id="equip_stre1"></td>
    </tr>
	<tr>
      <td><input name="equip_upstar" type="submit" id="equip_upstar" value="升星">
物品ID
        ：
  <input name="equip_upstar1" type="text" id="equip_upstar1"></td>
	</tr>
	<tr>
      <td><input name="equip_dismantle" type="submit" id="equip_dismantle" value="拆解">
        物品ID
        ：
      <input name="equip_dismantle1" type="text" id="equip_dismantle1"></td>
    </tr>
	<tr>
      <td><input name="equip_smelt" type="submit" id="equip_smelt" value="熔炼">
        物品ID组
        ：
      <input name="equip_smelt1" type="text" id="equip_smelt1"></td>
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