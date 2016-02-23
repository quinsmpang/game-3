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
      <td width="765">召唤</td>
    </tr>
    <tr>
      <td><input name="summon_ordinary" type="submit" id="summon_ordinary" value="普通召唤">
        召唤次数
        ：
      <input name="summon_ordinary1" type="text" id="summon_ordinary1" size="10" onMouseOver="showHelp(this,'1:单次<br>2:十连')" onMouseOut="hideHelp()"></td>
    </tr>
    <tr>
      <td><input name="summon_advanced" type="submit" id="summon_advanced" value="至尊召唤">
        召唤次数
        ：
          <input name="summon_advanced1" type="text" id="summon_advanced1" size="10" onMouseOver="showHelp(this,'1:单次<br>2:十连')" onMouseOut="hideHelp()"></td>
    </tr>
    <tr>
      <td><input name="summon_mystery" type="submit" id="summon_mystery" value="神秘召唤"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
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