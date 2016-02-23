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
      <td width="765">日常</td>
    </tr>
    <tr>
      <td><input name="welfare_task" type="submit" id="welfare_task" value="领取任务奖励">
        任务编号
        ：
          <input name="welfare_task1" type="text" id="welfare_task1" size="10"></td>
    </tr>
    <tr>
      <td><input name="welfare_achieve" type="submit" id="welfare_achieve" value="领取成就奖励">
        成就编号
        ：
      <input name="welfare_achieve1" type="text" id="welfare_achieve1" size="10"></td>
    </tr>
    <tr>
      <td><input name="welfare_checkin" type="submit" id="welfare_checkin" value="签到"></td>
    </tr>
	<tr>
      <td><input name="welfare_checkin_aw" type="submit" id="welfare_checkin_aw" value="领取累积签到奖励">
        奖励编号
        ：
          <input name="welfare_checkin_aw1" type="text" id="welfare_checkin_aw1" size="10"></td>
    </tr>
	<tr>
      <td><input name="welfare_task_aw_ok" type="submit" id="welfare_task_aw_ok" value="一键领取日常任务奖励">
        任务编号数据
        ：
      <input name="welfare_task_aw_ok1" type="text" id="welfare_task_aw_ok1" size="20"></td>
    </tr>
	<tr>
      <td><input name="welfare_achieve_aw_ok" type="submit" id="welfare_achieve_aw_ok" value="一键领取成就奖励">
        成就编号数据
        ：
      <input name="welfare_achieve_aw_ok1" type="text" id="welfare_achieve_aw_ok1" size="20"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><font color="#FF0000">调试功能</font></td>
    </tr>
	<tr>
      <td><input name="debug_welfare_finish_task" type="submit" id="debug_welfare_finish_task" value="调试完成任务">
任务编号
        ：
  <input name="debug_welfare_finish_task1" type="text" id="debug_welfare_finish_task1" size="10">
  数量
        ：
      <input name="debug_welfare_finish_task2" type="text" id="debug_welfare_finish_task2" size="10"></td>
    </tr>
	<tr>
      <td><input name="debug_welfare_add_checkin" type="submit" id="debug_welfare_add_checkin" value="调试加签到天数">
天数
        ：
          <input name="debug_welfare_add_checkin1" type="text" id="debug_welfare_add_checkin1" size="10"></td>
    </tr>
	<tr>
      <td><input name="debug_welfare_reset_achieve" type="submit" id="debug_welfare_reset_achieve" value="调试重置成就领取"></td>
    </tr>
	<tr>
      <td><input name="debug_welfare_reset_check" type="submit" id="debug_welfare_reset_check" value="调试重置签到日期"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
 </table>
</form>
</body>

</html>