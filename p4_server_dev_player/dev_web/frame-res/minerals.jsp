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
      <td width="765">挖矿</td>
    </tr>
	<tr>
      <td><input name="minerals_clockin" type="submit" id="minerals_clockin" value="占矿">
        坑位编号
        ：
          <input name="minerals_clockin1" type="text" id="minerals_clockin1" size="10"></td>
    </tr>
	<tr>
      <td><input name="minerals_condent" type="submit" id="minerals_condent" value="抢矿">
对手ID
        ：
          <input name="minerals_condent1" type="text" id="minerals_condent1" size="10">
          坑位编号
        ：
          <input name="minerals_condent2" type="text" id="minerals_condent2" size="10">
          出战阵型
        ：
          <input name="minerals_condent3" type="text" id="minerals_condent3" size="10"></td>
    </tr>
	<tr>
      <td><input name="minerals_setdef" type="submit" id="minerals_setdef" value="设置防守阵型">
阵型
        ：
          <input name="minerals_setdef1" type="text" id="minerals_setdef1" size="10"></td>
	</tr>
	<tr>
      <td><input name="minerals_getinfo" type="submit" id="minerals_getinfo" value="获取个人挖矿信息"></td>
	</tr>
	<tr>
      <td><input name="minerals_getposdata" type="submit" id="minerals_getposdata" value="获取坑位数据"></td>
	</tr>
	<tr>
      <td><input name="minerals_getownerdata" type="submit" id="minerals_getownerdata" value="获取坑位拥有者数据">
目标玩家ID
        ：
          <input name="minerals_getownerdata1" type="text" id="minerals_getownerdata1" size="10"></td>
	</tr>
	</tr>
		<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><font color="#FF0000">调试功能</font></td>
    </tr>
 </table>
</form>
</body>

</html>