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
<style type="text/css">
<!--
.style1 {
	color: #FF0000;
	font-weight: bold;
}
-->
</style>
</head>
<body>

<form id="form" name="form" method="post" action="../dev_request.jsp" target="frame_result" enctype="multipart/form-data">
  <table width="1024" border="0">
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>伙伴</td>
    </tr>
	<tr>
      <td><input name="partner_puton_equip" type="submit" id="partner_puton_equip" value="穿装备">
伙伴ID
        ：
          <input name="partner_puton_equip1" type="text" id="partner_puton_equip1">
          脱装备伙伴ID
        ：
          <input name="partner_puton_equip2" type="text" id="partner_puton_equip2">
物品ID
        ：
<input name="partner_puton_equip3" type="text" id="partner_puton_equip3"></td>
    </tr>
	<tr>
      <td><input name="partner_shotcutputon_equip" type="submit" id="partner_shotcutputon_equip" value="一键换装备">
伙伴ID
        ：
  <input name="partner_shotcutputon_equip1" type="text" id="partner_shotcutputon_equip1"></td>
    </tr>
	<tr>
      <td><input name="partner_putoff_equip" type="submit" id="partner_putoff_equip" value="脱装备">
伙伴ID
        ：
  <input name="partner_putoff_equip1" type="text" id="partner_putoff_equip1">
  部位
        ：
        <input name="partner_putoff_equip2" type="text" id="partner_putoff_equip2" onMouseOver="showHelp(this,'1.武器<br>2.头盔<br>3.衣服<br>4.鞋子<br>5.戒指<br>6.宝物')" onMouseOut="hideHelp()"></td>
    </tr>
	<tr>
      <td><input name="partner_puton_orb" type="submit" id="partner_puton_orb" value="穿灵珠">
        伙伴ID
        ：
        <input name="partner_puton_orb1" type="text" id="partner_puton_orb1">
部位
        ：
<input name="partner_puton_orb2" type="text" id="partner_puton_orb2"></td>
    </tr>
	<tr>
      <td><input name="partner_upphase" type="submit" id="partner_upphase" value="升阶">
        伙伴ID
        ：
      <input name="partner_upphase1" type="text" id="partner_upphase1"></td>
    </tr>
	<tr>
      <td><input name="partner_upstar" type="submit" id="partner_upstar" value="升星">
伙伴ID
        ：
  <input name="partner_upstar1" type="text" id="partner_upstar1"></td>
    </tr>
	<tr>
      <td><input name="partner_awaken" type="submit" id="partner_awaken" value="觉醒">
伙伴ID
        ：
  <input name="partner_awaken1" type="text" id="partner_awaken1"></td>
    </tr>
	<tr>
      <td><input name="partner_upskilllv" type="submit" id="partner_upskilllv" value="升级技能">
        伙伴ID
        ：
      <input name="partner_upskilllv1" type="text" id="partner_upskilllv1">
      技能坑号：
      <input name="partner_upskilllv2" type="text" id="partner_upskilllv2"></td>
    </tr>
	<tr>
      <td><input name="partner_shortcutstreequip" type="submit" id="partner_shortcutstreequip" value="一键强化装备">
伙伴ID
        ：
  <input name="partner_shortcutstreequip1" type="text" id="partner_shortcutstreequip1"></td>
    </tr>
	<tr>
      <td><input name="partner_exchange" type="submit" id="partner_exchange" value="兑换伙伴">
        伙伴编号：
        <input name="partner_exchange1" type="text" id="partner_exchange1"></td>
    </tr>
	<tr>
      <td><input name="partner_shortcutputonorb" type="submit" id="partner_shortcutputonorb" value="一键穿灵珠">
        伙伴ID
        ：
      <input name="partner_shortcutputonorb1" type="text" id="partner_shortcutputonorb1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><font color="#FF0000">调试功能</font></td>
    </tr>
	<tr>
      <td><input name="debug_partner_add" type="submit" id="debug_partner_add" value="调试加伙伴">
伙伴编号
        ：
  <input name="debug_partner_add1" type="text" id="debug_partner_add1">
阶段
        ：
<input name="debug_partner_add2" type="text" id="debug_partner_add2" value="1">
星级
        ：
<input name="debug_partner_add3" type="text" id="debug_partner_add3" value="1"></td>
    </tr>
	<tr>
      <td><input name="debug_partner_del" type="submit" id="debug_partner_del" value="调试删伙伴">
伙伴编号
        ：
  <input name="debug_partner_del1" type="text" id="debug_partner_del1">
  <span class="style1">（请先确保其他地方没有引用此伙伴）</span></td>
    </tr>
	<tr>
      <td><input name="debug_partner_addphase" type="submit" id="debug_partner_addphase" value="调试加伙伴阶级">
伙伴ID
        ：
  <input name="debug_partner_addphase1" type="text" id="debug_partner_addphase1">
  增量：
  <input name="debug_partner_addphase2" type="text" id="debug_partner_addphase2"></td>
    </tr>
	<tr>
      <td><input name="debug_partner_addstar" type="submit" id="debug_partner_addstar" value="调试加伙伴星级">
伙伴ID
        ：
  <input name="debug_partner_addstar1" type="text" id="debug_partner_addstar1">
增量：
<input name="debug_partner_addstar2" type="text" id="debug_partner_addstar2"></td>
    </tr>
	<tr>
      <td><input name="debug_partner_addexp" type="submit" id="debug_partner_addexp" value="调试加伙伴经验">
伙伴ID
        ：
  <input name="debug_partner_addexp1" type="text" id="debug_partner_addexp1">
增量：
<input name="debug_partner_addexp2" type="text" id="debug_partner_addexp2"></td>
    </tr>
	<tr>
      <td><input name="debug_partner_reset" type="submit" id="debug_partner_reset" value="调试还原伙伴">
伙伴编号
        ：
  <input name="debug_partner_reset1" type="text" id="debug_partner_reset1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
 </table>
</form>
</body>

</html>