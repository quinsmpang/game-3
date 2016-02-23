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
      <td>角色</td>
    </tr>
	<tr>
      <td height="29"><input name="player_upd_olstate" type="submit" id="player_upd_olstate" value="发送心跳包"></td>
    </tr>
	<tr>
      <td><input name="player_get_systime" type="submit" id="player_get_systime" value="获取系统时间"></td>
    </tr>
	<tr>
      <td><input name="player_getdata" type="submit" id="player_getdata" value="获取指定玩家数据">
        目标玩家ID：
        <input name="player_getdata1" type="text" id="player_getdata1"></td>
    </tr>
	<tr>
      <td><input name="debug_openallfunc" type="submit" id="debug_openallfunc" value="开启所有可开启的功能"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="role_recoverenergy" type="submit" id="role_recoverenergy" value="恢复体力"></td>
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
      <td><input name="debug_pla_change_exp" type="submit" id="debug_pla_change_exp" value="加经验">
        数量：
        <input name="debug_pla_change_exp1" type="text" id="debug_pla_change_exp1"></td>
    </tr>
	<tr>
      <td><input name="debug_pla_change_money" type="submit" id="debug_pla_change_money" value="加金币">
        数量：
        <input name="debug_pla_change_money1" type="text" id="debug_pla_change_money1"></td>
    </tr>
	<tr>
      <td><input name="debug_pla_change_coin" type="submit" id="debug_pla_change_coin" value="加钻石">
        数量：
        <input name="debug_pla_change_coin1" type="text" id="debug_pla_change_coin1"></td>
    </tr>
	<tr>
      <td><input name="debug_pla_charge" type="submit" id="debug_pla_charge" value="充值">
        RMB：
        <input name="debug_pla_charge1" type="text" id="debug_pla_charge1">
        充值中心订单号：
        <input name="debug_pla_charge2" type="text" id="debug_pla_charge2"></td>
    </tr>
	<tr>
      <td><input name="debug_pla_buytq" type="submit" id="debug_pla_buytq" value="购买特权">
        特权编号：
        <input name="debug_pla_buytq1" type="text" id="debug_pla_buytq1">
        充值中心订单号：
        <input name="debug_pla_buytq2" type="text" id="debug_pla_buytq2"></td>
    </tr>
	<tr>
      <td><input name="debug_pla_resetdaydata" type="submit" id="debug_pla_resetdaydata" value="重置日常数据"></td>
    </tr>
	<tr>
      <td><input name="debug_pla_shortcut_grow" type="submit" id="debug_pla_shortcut_grow" value="一键成长">
        编号
        ：
          <input name="debug_pla_shortcut_grow1" type="text" id="debug_pla_shortcut_grow1"></td>
    </tr>
	<tr>
      <td><input name="debug_closefunc" type="submit" id="debug_closefunc" value="关闭功能">
      功能编号
      ：
        <input name="debug_closefunc1" type="text" id="debug_closefunc1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="debug_role_addenergy" type="submit" id="debug_role_addenergy" value="加体力">
        数量：
      <input name="debug_role_addenergy1" type="text" id="debug_role_addenergy1"></td>
    </tr>
	<tr>
      <td><input name="debug_role_addsp" type="submit" id="debug_role_addsp" value="加魂点">
数量：
  <input name="debug_role_addsp1" type="text" id="debug_role_addsp1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
 </table>
</form>
</body>

</html>