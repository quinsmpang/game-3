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
      <td>国战</td>
    </tr>
	<tr>
      <td><input name="cb_war" type="submit" id="cb_war" value="宣战">
        目标城市编号
        ：
          <input name="cb_war1" type="text" id="cb_war1">
          出战队伍：
          <input name="cb_war2" type="text" id="cb_war2"></td>
    </tr>
	<tr>
		<td><input name="cb_addteam" type="submit" id="cb_addteam" value="将队伍放入队伍池">
		  阵型：
	      <input name="cb_addteam1" type="text" id="cb_addteam1"></td>
	</tr>
	<tr>
		<td><input name="cb_removeteam" type="submit" id="cb_removeteam" value="移除队伍池中的队伍">
		  队伍ID：
	      <input name="cb_removeteam1" type="text" id="cb_removeteam1"></td>
	</tr>
	<tr>
		<td><input name="cb_joinwar" type="submit" id="cb_joinwar" value="加入城战">
		  mapkey：
            <input name="cb_joinwar1" type="text" id="cb_joinwar1">
            加入势力
            ：
            <input name="cb_joinwar2" type="text" id="cb_joinwar2">
            出战队伍
            ：
        <input name="cb_joinwar3" type="text" id="cb_joinwar3"></td>
	</tr>
	<tr>
		<td><input name="cb_relivepartner" type="submit" id="cb_relivepartner" value="复活伙伴">
		  伙伴ID：
        <input name="cb_relivepartner1" type="text" id="cb_relivepartner1"></td>
	</tr>
	<tr>
		<td><input name="cb_contendleader" type="submit" id="cb_contendleader" value="争夺太守">
		  城市编号
		  ：
            <input name="cb_contendleader1" type="text" id="cb_contendleader1">
            出战队伍
            ：
        <input name="cb_contendleader2" type="text" id="cb_contendleader2"></td>
	</tr>
	<tr>
		<td><input name="cb_setleaderdefform" type="submit" id="cb_setleaderdefform" value="设置太守防守阵型">
		  
出战队伍
            ：
<input name="cb_setleaderdefform2" type="text" id="cb_setleaderdefform2"></td>
	</tr>
	<tr>
		<td><input name="cb_giveup_leader" type="submit" id="cb_giveup_leader" value="放弃太守"></td>
	</tr>
	<tr>
		<td><input name="cb_getteampooldata" type="submit" id="cb_getteampooldata" value="获取队伍池数据"></td>
	</tr>
	<tr>
		<td><input name="cb_getpartnerstate" type="submit" id="cb_getpartnerstate" value="获取伙伴状态"></td>
	</tr>
	<tr>
		<td><input name="cb_getcitybattledata" type="submit" id="cb_getcitybattledata" value="获取城池战斗数据">
		  mapkey：
        <input name="cb_getcitybattledata1" type="text" id="cb_getcitybattledata1"></td>
	</tr>
	<tr>
		<td><input name="cb_getleaderdata" type="submit" id="cb_getleaderdata" value="获取太守数据">
		  城市编号
		  ：
        <input name="cb_getleaderdata1" type="text" id="cb_getleaderdata1"></td>
	</tr>
	<tr>
		<td><input name="cb_getkillranking" type="submit" id="cb_getkillranking" value="获取击杀排行">
mapkey：
  <input name="cb_getkillranking1" type="text" id="cb_getkillranking1">
阵营
            ：
        <input name="cb_getkillranking2" type="text" id="cb_getkillranking2"></td>
	</tr>
	<tr>
		<td><input name="cb_getbattlerlist" type="submit" id="cb_getbattlerlist" value="获取出场顺序">
mapkey：
  <input name="cb_getbattlerlist1" type="text" id="cb_getbattlerlist1"></td>
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