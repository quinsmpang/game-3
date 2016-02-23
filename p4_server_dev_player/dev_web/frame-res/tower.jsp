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
      <td width="765">轮回塔</td>
    </tr>
	<tr>
      <td><input name="tower_enter" type="submit" id="tower_enter" value="进入挑战">
        层数
        ：
          <input name="tower_enter1" type="text" id="tower_enter1" size="10">
        难度
        ：
        <input name="tower_enter2" type="text" id="tower_enter2" size="10">
        位置数据
        ：
        <input name="tower_enter3" type="text" id="tower_enter3" size="20"></td>
    </tr>
	<tr>
      <td><input name="tower_end" type="submit" id="tower_end" value="结束挑战">
        战斗记录
        ：
          <input name="tower_end1" type="text" id="tower_end1" size="20"></td>
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