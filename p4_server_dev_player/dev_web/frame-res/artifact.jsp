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
      <td>神器</td>
    </tr>
	<tr>
      <td><input name="artifact_eatitem" type="submit" id="artifact_eatitem" value="吃物品">
        编号
        ：
          <input name="artifact_eatitem1" type="text" id="artifact_eatitem1">
          要吃的物品
          ：
          <input name="artifact_eatitem2" type="text" id="artifact_eatitem2"></td>
    </tr>
	<tr>
	  <td><input name="artifact_coininput" type="submit" id="artifact_coininput" value="金锭注入">
		  编号
		  ：
	        <input name="artifact_coininput1" type="text" id="artifact_coininput1">
          提升等级数：
	        <input name="artifact_coininput2" type="text" id="artifact_coininput2"></td>
	</tr>
	<tr>
		<td><input name="artifact_comp" type="submit" id="artifact_comp" value="神器合成">
		  编号
		  ：
        <input name="artifact_comp1" type="text" id="artifact_comp1"></td>
	</tr>
	<tr>
		<td><input name="artifact_recoverrobtimes" type="submit" id="artifact_recoverrobtimes" value="恢复神器碎片抢夺次数"></td>
	</tr>
	<tr>
		<td><input type="submit" value="开启神器碎片保护"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
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