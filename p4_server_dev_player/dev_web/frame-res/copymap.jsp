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
      <td width="765">副本</td>
    </tr>
    <tr>
      <td><input name="copymap_enter" type="submit" id="copymap_enter" value="进入副本">
      副本编号：
        <input name="copymap_enter1" type="text" id="copymap_enter1" size="10">
        位置数据
        ：
        <input name="copymap_enter2" type="text" id="copymap_enter2" size="20"></td>
    </tr>
    <tr>
      <td><input name="copymap_end" type="submit" id="copymap_end" value="挑战完成">
        副本编号：
      <input name="copymap_end1" type="text" id="copymap_end1" size="10">
战斗记录：
<input name="copymap_end2" type="text" id="copymap_end2"></td>
    </tr>
    <tr>
      <td><input name="copymap_buy" type="submit" id="copymap_buy" value="购买挑战次数">
        副本编号：
          <input name="copymap_buy1" type="text" id="copymap_buy1" size="10"></td>
    </tr>
	<tr>
      <td><input name="copymap_sweep" type="submit" id="copymap_sweep" value="扫荡副本">
副本编号：
  <input name="copymap_sweep1" type="text" id="copymap_sweep1" size="10">
  扫荡次数：
  <input name="copymap_sweep2" type="text" id="copymap_sweep2" size="10"></td>
    </tr>
	<tr>
      <td><input name="copymap_getaward" type="submit" id="copymap_getaward" value="领取副本星级奖励">
地图编号：
  <input name="copymap_getaward1" type="text" id="copymap_getaward1" size="10">
  奖励编号：
  <input name="copymap_getaward2" type="text" id="copymap_getaward2" size="10"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><font color="#FF0000">调试功能</font></td>
    </tr>
	<tr>
      <td><input name="copymap_onekey" type="submit" id="copymap_onekey" value="一键通过副本区间">
副本开始编号：
  <input name="copymap_onekey1" type="text" id="copymap_onekey1" size="10">
副本结束编号：
<input name="copymap_onekey2" type="text" id="copymap_onekey2" size="10"></td>
    </tr>
	<tr>
      <td><input name="copymap_clear" type="submit" id="copymap_clear" value="清空副本记录"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
 </table>
</form>
</body>

</html>