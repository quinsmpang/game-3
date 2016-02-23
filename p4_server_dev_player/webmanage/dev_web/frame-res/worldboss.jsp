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
      <td width="765">世界BOSS</td>
    </tr>
	<tr>
      <td><input name="wb_join" type="submit" id="wb_join" value="加入世界BOSS"></td>
    </tr>
	<tr>
      <td><input name="wb_tobattle" type="submit" id="wb_tobattle" value="开始挑战">
阵型数据
        ：
          <input name="wb_tobattle1" type="text" id="wb_tobattle1" size="20"></td>
    </tr>
	<tr>
      <td><input name="wb_getdate" type="submit" id="wb_getdate" value="获取数据"></td>
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