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
      <td width="765">组队活动</td>
    </tr>
	<tr>
      <td><input name="team_acti_create" type="submit" id="team_acti_create" value="创建队伍">
        队伍类型
        ：
      <input name="team_acti_create1" type="text" id="team_acti_create1" size="10"></td>
    </tr>
	<tr>
      <td><input name="team_acti_join" type="submit" id="team_acti_join" value="加入队伍">
队伍编号
        ：
          <input name="team_acti_join1" type="text" id="team_acti_join1" size="10"></td>
    </tr>
	<tr>
      <td><input name="team_acti_kick" type="submit" id="team_acti_kick" value="踢出队伍">
队伍编号
        ：
  <input name="team_acti_kick1" type="text" id="team_acti_kick1" size="10">
  队员ID
        ：
  <input name="team_acti_kick2" type="text" id="team_acti_kick2" size="10"></td>
	</tr>
	<tr>
      <td><input name="team_acti_format" type="submit" id="team_acti_format" value="布阵">
队伍编号
        ：
  <input name="team_acti_format1" type="text" id="team_acti_format1" size="10">
阵型数据
        ：
<input name="team_acti_format2" type="text" id="team_acti_format2" size="10"></td>
	</tr>
	<tr>
      <td><input name="team_acti_beready" type="submit" id="team_acti_beready" value="准备">
队伍编号
        ：
  <input name="team_acti_beready1" type="text" id="team_acti_beready1" size="10"></td>
	</tr>
	<tr>
      <td><input name="team_acti_cancelready" type="submit" id="team_acti_cancelready" value="取消准备">
队伍编号
        ：
  <input name="team_acti_cancelready1" type="text" id="team_acti_cancelready1" size="10"></td>
	</tr>
	<tr>
      <td><input name="team_acti_battle" type="submit" id="team_acti_battle" value="战斗">
队伍编号
        ：
  <input name="team_acti_battle1" type="text" id="team_acti_battle1" size="10"></td>
	</tr>
	<tr>
      <td><input name="team_acti_getlist" type="submit" id="team_acti_getlist" value="获取队伍列表">
队伍类型
        ：
  <input name="team_acti_getlist1" type="text" id="team_acti_getlist1" size="10"></td>
	</tr>
		<tr>
      <td><input name="team_acti_getdata" type="submit" id="team_acti_getdata" value="获取数据"></td>
	</tr>
		<tr>
      <td><input name="team_acti_exit" type="submit" id="team_acti_exit" value="退出退伍">
队伍编号
        ：
  <input name="team_acti_exit1" type="text" id="team_acti_exit1" size="10"></td>
	</tr>
	</tr>
		<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><font color="#FF0000">调试功能</font></td>
    </tr>
	<tr>
      <td><input name="debug_team_acti_reset" type="submit" id="debug_team_acti_reset" value="重置获取奖励次数"></td>
	</tr>
 </table>
</form>
</body>

</html>