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
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>物品</td>
    </tr>
	<tr>
      <td><input name="item_discard" type="submit" id="item_discard" value="丢弃物品">
物品ID
        ：
  <input name="item_discard1" type="text" id="item_discard1"></td>
    </tr>
	<tr>
      <td><input name="item_opengift" type="submit" id="item_opengift" value="开启礼包">
物品ID
        ：
  <input name="item_opengift1" type="text" id="item_opengift1">
  开启数量：
  <input name="item_opengift2" type="text" id="item_opengift2"></td>
    </tr>
	<tr>
      <td><input name="item_move" type="submit" id="item_move" value="移动物品">
物品ID
        ：
  <input name="item_move1" type="text" id="item_move1">
  源空间
        ：
  <input name="item_move2" type="text" id="item_move2">
目标空间
        ：
<input name="item_move3" type="text" id="item_move3"></td>
    </tr>
	<tr>
      <td><input name="item_useconsume" type="submit" id="item_useconsume" value="使用消耗品">
        目标参数：
          <input name="item_useconsume1" type="text" id="item_useconsume1">
        物品ID
      ：
    <input name="item_useconsume2" type="text" id="item_useconsume2">
数量
      ：
<input name="item_useconsume3" type="text" id="item_useconsume3"></td>
    </tr>
	<tr>
      <td><input name="item_sellmoneyitem" type="submit" id="item_sellmoneyitem" value="卖出所有纯出售物品"></td>
    </tr>
	<tr>
      <td><input name="item_uselottery" type="submit" id="item_uselottery" value="使用抽奖物品">
物品ID
        ：
  <input name="item_uselottery1" type="text" id="item_uselottery1">
  使用数量：
  <input name="item_uselottery2" type="text" id="item_uselottery2"></td>
    </tr>
	<tr>
      <td><input name="item_comporb" type="submit" id="item_comporb" value="合灵珠">
        灵珠编号
        ：
          <input name="item_comporb1" type="text" id="item_comporb1"></td>
    </tr>
	<tr>
      <td><input name="item_compequip" type="submit" id="item_compequip" value="合装备">
        装备编号
        ：
          <input name="item_compequip1" type="text" id="item_compequip1"></td>
    </tr>
	<tr>
      <td><input name="item_sell" type="submit" id="item_sell" value="出售物品">
        物品ID
        ：
      <input name="item_sell1" type="text" id="item_sell1">
      出售数量
        ：
        <input name="item_sell2" type="text" id="item_sell2"></td>
    </tr>
	<tr>
      <td><input name="item_smelt" type="submit" id="item_smelt" value="融魂">
        物品ID
        ：
      <input name="item_smelt1" type="text" id="item_smelt1">
      融魂数量
        ：
        <input name="item_smelt2" type="text" id="item_smelt2"></td>
    </tr>
	<tr>
      <td><input name="item_buyexpitem" type="submit" id="item_buyexpitem" value="买经验药">
        编号
        ：
          <input name="item_buyexpitem1" type="text" id="item_buyexpitem1">
      数量
        ：
      <input name="item_buyexpitem2" type="text" id="item_buyexpitem2"></td>
    </tr>
	<tr>
      <td><input name="item_usechoosegift" type="submit" id="item_usechoosegift" value="使用选择型礼包">
物品ID
        ：
  <input name="item_usechoosegift1" type="text" id="item_usechoosegift1">
  选择物品下标
  ：
  <input name="item_usechoosegift2" type="text" id="item_usechoosegift2">
  使用数量：
  <input name="item_usechoosegift3" type="text" id="item_usechoosegift3"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><font color="#FF0000">调试功能</font></td>
    </tr>
	<tr>
      <td><input name="debug_additem" type="submit" id="debug_additem" value="加物品">
        类型：
          <input name="itemtype" type="text" id="itemtype" size="8">
          编号：
          <input name="itemnum" type="text" id="itemnum" size="8">
          数量：
          <input name="itemamount" type="text" id="itemamount" size="8">
          
        空间：
          <input name="itemzone" type="text" id="itemzone" value="0" size="5" onMouseOver="showHelp(this,'0.背包<br>1.仓库<br>2.任务<br>3.使用中<br>4.已售出<br>5.邮箱')" onMouseOut="hideHelp()">
        
        扩展参数：
        <input name="itemextend" type="text" id="itemextend" size="5" onMouseOver="showHelp(this,'加普通装备时必须填：1~6')" onMouseOut="hideHelp()"></td>
    </tr>
	<tr>
      <td><input name="debug_discarditem" type="submit" id="debug_discarditem" value="丢物品">
        物品ID：
        <input name="debug_discarditem1" type="text" id="debug_discarditem1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
 </table>
</form>
</body>

</html>