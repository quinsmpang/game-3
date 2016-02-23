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

<link rel="stylesheet" href="../webmanage/css/style1.css" type="text/css">
<script src="../webmanage/js/Calendar3.js"></script>

<style type="text/css">
<!--
.style1 {font-weight: bold}
.style2 {
	color: #FF0000;
	font-weight: bold;
}
-->
</style>
</head>

<body>
<form id="form" name="form" method="post" action="dev_request.jsp" target="_self" enctype="multipart/form-data">
  <table width="100%" border="0">
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td width="1178">----------------------模块管理------------------------</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="debug_refresh_game_ranking" type="submit" id="debug_refresh_game_ranking" value="刷新游戏排行">
刷新时间：
  <input name="debug_refresh_game_ranking1" type="text" id="debug_refresh_game_ranking1" value="<%=com.moonic.util.MyTools.getTimeStr()%>"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="coinreturn_add" type="submit" id="coinreturn_add" value="返钻加用户">
用户名：
  <input name="coinreturn_add1" type="text" id="coinreturn_add1">
  帐号渠道
  ：
  <input name="coinreturn_add2" type="text" id="coinreturn_add2" size="5">
  买钻充值金额：
  <input name="coinreturn_add3" type="text" id="coinreturn_add3" size="10">
  买最高特权编号
  ：
  <input name="coinreturn_add4" type="text" id="coinreturn_add4" size="5">
  帐号下角色最高等级
  ：
  <input name="coinreturn_add5" type="text" id="coinreturn_add5" size="10"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="partner_getspritebox" type="submit" id="partner_getspritebox" value="获取伙伴战斗数据">
        服务器ID：
          <input name="partner_getspritebox1" type="text" id="partner_getspritebox1">
          角色ID
          ：
      <input name="partner_getspritebox2" type="text" id="partner_getspritebox2"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><input name="jjc_create_user" type="submit" id="jjc_create_user" value="创建竞技场假人帐号">
        用户服ID：
      <input name="jjc_create_user1" type="text" id="jjc_create_user1"></td>
	</tr>
	<tr>
      <td><input name="jjc_create_pc" type="submit" id="jjc_create_pc" value="创建竞技场假人">
        服务器ID：
      <input name="jjc_create_pc1" type="text" id="jjc_create_pc1"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><input name="cb_npcinvade" type="submit" id="cb_npcinvade" value="国战-NPC入侵">
        服务器ID：
      <input name="cb_npcinvade1" type="text" id="cb_npcinvade1">
      入侵城市编号：
      <input name="cb_npcinvade2" type="text" id="cb_npcinvade2">
      入侵势力编号：
      <input name="cb_npcinvade3" type="text" id="cb_npcinvade3">
      入侵NPC数量：
      <input name="cb_npcinvade4" type="text" id="cb_npcinvade4"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><input name="wb_start" type="submit" id="wb_start" value="启动世界BOSS">
服务器ID：
  <input name="wb_start1" type="text" id="wb_start1" size="20">
  活动时长
  ：
  <input name="wb_start2" type="text" id="wb_start2" size="20">
  是否强制启动
  ：
  <input name="wb_start3" type="text" id="wb_start3" value="0" size="20"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><input name="team_acti_start" type="submit" id="team_acti_start" value="启动组队活动">
服务器ID：
  <input name="team_acti_start1" type="text" id="team_acti_start1" size="20">
活动时长
  ：
<input name="team_acti_start2" type="text" id="team_acti_start2" size="20">
是否强制启动
  ：
<input name="team_acti_start3" type="text" id="team_acti_start3" value="0" size="20"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><input name="get_pvp_battle_info" type="submit" id="get_pvp_battle_info" value="获取PVP战斗信息">
        服务器ID：
      <input name="get_pvp_battle_info1" type="text" id="get_pvp_battle_info1" size="20"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><input name="get_battle_replay" type="submit" id="get_battle_replay" value="获取战斗回放">
        战斗ID：
        <input name="get_battle_replay1" type="text" id="get_battle_replay1" size="20"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><input name="update_worldlevel" type="submit" id="update_worldlevel" value="更新世界等级">
服务器ID：
  <input name="update_worldlevel1" type="text" id="update_worldlevel1" size="20"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><input name="issue_jjcranking_award" type="submit" id="issue_jjcranking_award" value="发放竞技场奖励">
服务器ID：
  <input name="issue_jjcranking_award1" type="text" id="issue_jjcranking_award1" size="20"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><input name="minerals_start" type="submit" id="minerals_start" value="启动挖矿活动">
服务器ID：
  <input name="minerals_start1" type="text" id="minerals_start1" size="20"></td>
	</tr>
	<tr>
      <td><input name="minerals_end" type="submit" id="minerals_end" value="停止挖矿活动">
服务器ID：
  <input name="minerals_end1" type="text" id="minerals_end1" size="20"></td>
	</tr>
	<tr>
      <td><input name="minerals_getposdata" type="submit" id="minerals_getposdata" value="查看坑位数据">
服务器ID：
  <input name="minerals_getposdata1" type="text" id="minerals_getposdata1" size="20"></td>
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