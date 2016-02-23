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
      <td width="765">铜钱试炼</td>
    </tr>
    <tr>
      <td><input name="trial_money_start" type="submit" id="trial_money_start" value="试炼开始">
        编号
        ：
          <input name="trial_money_start1" type="text" id="trial_money_start1" size="10">
      位置数据
        ：
      <input name="trial_money_start2" type="text" id="trial_money_start2" size="20"></td>
    </tr>
    <tr>
      <td><input name="trial_money_end" type="submit" id="trial_money_end" value="试炼结束">
        编号
        ：
          <input name="trial_money_end1" type="text" id="trial_money_end1" size="10">
      战斗记录
        ：
      <input name="trial_money_end2" type="text" id="trial_money_end2" size="20"></td>
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