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
      <td>帮派副本</td>
    </tr>
	<tr>
      <td><input name="faccopymap_into" type="submit" id="faccopymap_into" value="进入副本">
        点位编号
        ：
          <input name="faccopymap_into1" type="text" id="faccopymap_into1">
          阵型：
          <input name="faccopymap_into2" type="text" id="faccopymap_into2"></td>
    </tr>
	<tr>
		<td><input name="faccopymap_end" type="submit" id="faccopymap_end" value="结束副本">
		  战斗回放：
	        <input name="faccopymap_end1" type="text" id="faccopymap_end1"></td>
	</tr>
	<tr>
		<td><input name="faccopymap_resetmap" type="submit" id="faccopymap_resetmap" value="重置地图">
		  地图编号
        ：
        <input name="faccopymap_resetmap1" type="text" id="faccopymap_resetmap1"></td>
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