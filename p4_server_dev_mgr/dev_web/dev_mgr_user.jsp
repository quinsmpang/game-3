<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<html>
<head>
<title>-</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="css/style1.css" rel="stylesheet" type="text/css">
<link href="css/bubbletips.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="http://www.jq-school.com/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="js/bubbletips.js"></script>
<script type="text/javascript" src="js/mytools.js"></script>

</head>

<body>
<form id="form" name="form" method="post" action="dev_request.jsp" target="_self" enctype="multipart/form-data">
  <table width="1138" border="0">
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td width="762">----------------------用户管理------------------------</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_openlogin" type="submit" id="console_openlogin" value="开启用户登录"></td>
    </tr>
	<tr>
      <td><input name="console_closelogin" type="submit" id="console_closelogin" value="关闭用户登录">
说明：
  <input name="console_closelogin1" type="text" id="console_closelogin1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_getpushdata" type="submit" id="console_getpushdata" value="获取推送队列数据"></td>
    </tr>
	<tr>
      <td><input name="console_clearpushdata" type="submit" id="console_clearpushdata" value="清除推送队列"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_startsocket" type="submit" id="console_startsocket" value="启动SOCKET服务器"></td>
    </tr>
	<tr>
      <td><input name="console_stopsocket" type="submit" id="console_stopsocket" value="停止SOCKET服务器"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_getsocketstate" type="submit" id="console_getsocketstate" value="获取SOCKET服务器运行状态"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="socket_run_info" type="submit" id="socket_run_info" value="获取SOCKET运行信息">
        服务器ID：
      <input name="socket_run_info1" type="text" id="socket_run_info1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_serverallmaintain" type="submit" id="console_serverallmaintain" value="维护所有游戏服务器">
      说明：
      <input name="console_serverallmaintain1" type="text" id="console_serverallmaintain1" size="5">
      推送消息：
      <input name="console_serverallmaintain2" type="text" id="console_serverallmaintain2" value="服务器连接被断开">
      弹出说明：
      <input name="console_serverallmaintain3" type="text" id="console_serverallmaintain3" value="看公告">
      下线方式：
      <input name="console_serverallmaintain4" type="text" id="console_serverallmaintain4" onMouseOver="showHelp(this,'0.退出游戏<br>1.退到公告界面')" onMouseOut="hideHelp()" size="5"></td>
    </tr>
	<tr>
      <td><input name="console_serverallopen" type="submit" id="console_serverallopen" value="开启所有游戏服务器"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_serveronemaintain" type="submit" id="console_serveronemaintain" value="维护指定游戏服务器">
        服务器ID：
        <input name="console_serveronemaintain1" type="text" id="console_serveronemaintain1" size="5">
        说明：
        <input name="console_serveronemaintain2" type="text" id="console_serveronemaintain2">
        推送消息：
        <input name="console_serveronemaintain3" type="text" id="console_serveronemaintain3" value="服务器连接被断开">
        弹出说明：
        <input name="console_serveronemaintain4" type="text" id="console_serverallmaintain222" value="看公告">
        下线方式：
        <input name="console_serveronemaintain5" type="text" id="console_serveronemaintain5" onMouseOver="showHelp(this,'0.退出游戏<br>1.退到公告界面')" onMouseOut="hideHelp()" size="5"></td>
    </tr>
	<tr>
      <td><input name="console_serveroneopen" type="submit" id="console_serveroneopen" value="开启指定游戏服务器">
      服务器ID：
      <input name="console_serveroneopen1" type="text" id="console_serveroneopen1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_breakonepla" type="submit" id="console_breakonepla" value="断开指定角色">
服务器ID：
  <input name="console_breakonepla1" type="text" id="console_breakonepla1">
玩家ID：
<input name="console_breakonepla2" type="text" id="console_breakonepla2">
推送消息：
<input name="console_breakonepla3" type="text" id="console_breakonepla3" value="服务器连接被断开"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="del_serverdata" type="submit" id="del_serverdata" value="删除服务器数据" onClick="return enterTip('确定要删除此服务器的所有角色吗？')">
        服务器ID：
        <input name="del_serverdata1" type="text" id="del_serverdata1"></td>
    </tr>
	<tr>
      <td><input name="del_plaarrdata" type="submit" id="del_plaarrdata" value="删除玩家数据" onClick="return enterTip('确定要删除这些角色吗？')">
        玩家ID：
        <input name="del_plaarrdata1" type="text" id="del_plaarrdata1" onMouseOver="showHelp(this,'多个玩家以“,”分隔')" onMouseOut="hideHelp()"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="export_pladata" type="submit" id="export_pladata" value="导出玩家数据">
玩家ID：
  <input name="export_pladata1" type="text" id="export_pladata1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="player_recharge" type="submit" id="player_recharge" value="补发-充值">
        服务器ID：
      <input name="player_recharge1" type="text" id="player_recharge1" size="15">
      渠道：
      <input name="player_recharge2" type="text" id="player_recharge2" size="15">
      玩家ID
      ：
      <input name="player_recharge3" type="text" id="player_recharge3" size="15">
      充值类型：
      <input name="player_recharge4" type="text" id="player_recharge4" size="15">
      充值RMB：
      <input name="player_recharge5" type="text" id="player_recharge5" size="15"></td>
    </tr>
	<tr>
      <td><input name="player_buytq" type="submit" id="player_buytq" value="补发-特权">
        服务器ID：
      <input name="player_buytq1" type="text" id="player_buytq1" size="15">
      渠道：
      <input name="player_buytq2" type="text" id="player_buytq2" size="15">
      玩家ID：
      <input name="player_buytq3" type="text" id="player_buytq3" size="15">
      特权编号：
      <input name="player_buytq4" type="text" id="player_buytq4" size="15"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_clear_game_pool" type="submit" id="console_clear_game_pool" value="清理游戏缓存">
        
        服务器ID：
        <input name="console_clear_game_pool1" type="text" id="console_clear_game_pool1">
        缓存类型：
        <input name="console_clear_game_pool2" type="text" id="console_clear_game_pool2"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="server_open_ready" type="submit" id="server_open_ready" value="开服准备">
服务器ID：
  <input name="server_open_ready1" type="text" id="server_open_ready1"></td>
    </tr>
	<tr>
      <td><input name="db_adjust_idle" type="submit" id="db_adjust_idle" value="调整数据库待机连接数">
服务器ID：
  <input name="db_adjust_idle1" type="text" id="db_adjust_idle1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="merger_server" type="submit" id="merger_server" value="合并服务器" onClick="return enterTip('确定要进行服务器合并操作吗？')">
        被合区：      
          <input name="merger_server1" type="text" id="merger_server1">
        目标区：
        <input name="merger_server2" type="text" id="merger_server2"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
  </table>
</form>
</body>

</html>