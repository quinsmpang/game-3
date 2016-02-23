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
      <td>排位竞技场</td>
    </tr>
	<tr>
      <td><input name="jjcranking_getinfo" type="submit" id="jjcranking_getinfo" value="获取竞技场信息"></td>
	</tr>
	<tr>
      <td><input name="jjcranking_getopps" type="submit" id="jjcranking_getopps" value="获取对手信息"></td>
	</tr>
	<tr>
      <td><input name="jjcranking_battle" type="submit" id="jjcranking_battle" value="挑战">
        对手角色ID
        ：
          <input name="jjcranking_battle1" type="text" id="jjcranking_battle1">
          我的排名
          ：
          <input name="jjcranking_battle2" type="text" id="jjcranking_battle2">
          对手排名
          ：
          <input name="jjcranking_battle3" type="text" id="jjcranking_battle3">
          我的攻击阵型
          ：
          <input name="jjcranking_battle4" type="text" id="jjcranking_battle4"></td>
    </tr>
	<tr>
      <td><input name="jjcranking_getrankingdata" type="submit" id="jjcranking_getrankingdata" value="获取排名榜单"></td>
	</tr>
	<tr>
      <td><input name="jjcranking_refreshopps" type="submit" id="jjcranking_refreshopps" value="刷新对手"></td>
	</tr>
	<tr>
      <td><input name="jjcranking_clear_cd" type="submit" id="jjcranking_clear_cd" value="清除挑战CD"></td>
    </tr>
	<tr>
      <td><input name="jjcranking_reset_cha_am" type="submit" id="jjcranking_reset_cha_am" value="重置挑战次数"></td>
    </tr>
	<tr>
      <td><input name="jjcranking_set_defform" type="submit" id="jjcranking_set_defform" value="设置防守阵型">
        阵型：
        <input name="jjcranking_set_defform1" type="text" id="jjcranking_set_defform1"></td>
    </tr>
	<tr>
      <td><input name="jjcranking_get_oppdefdata" type="submit" id="jjcranking_get_oppdefdata" value="获取对手防守阵型数据">
        对手角色ID
        ：
      <input name="jjcranking_get_oppdefdata1" type="text" id="jjcranking_get_oppdefdata1"></td>
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
	<tr>
      <td>&nbsp;</td>
    </tr>
 </table>
</form>
</body>

</html>