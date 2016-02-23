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
      <td>家族</td>
    </tr>
	<tr>
      <td><input name="faction_create" type="submit" id="faction_create" value="创建家族">
        家族名：
        <input name="faction_create1" type="text" id="faction_create1"></td>
    </tr>
	<tr>
      <td><input name="faction_set_joincond" type="submit" id="faction_set_joincond" value="设置入帮条件">
        条件：
          <input name="faction_set_joincond1" type="text" id="faction_set_joincond1"></td>
    </tr>
	<tr>
      <td><input name="faction_join" type="submit" id="faction_join" value="加入帮派">
        家族ID：
        <input name="faction_join1" type="text" id="faction_join1"></td>
    </tr>
	<tr>
      <td><input name="faction_updinfo" type="submit" id="faction_updinfo" value="修改家族信息">
内容：
  <input name="faction_updinfo1" type="text" id="faction_updinfo1"></td>
    </tr>
	<tr>
      <td><input name="faction_adjustposition" type="submit" id="faction_adjustposition" value="调整职位">
        成员角色ID：
        <input name="faction_adjustposition1" type="text" id="faction_adjustposition1">
        职位：
        <input name="faction_adjustposition2" type="text" id="faction_adjustposition2" onMouseOver="showHelp(this,'0:成员<br>1:副族长<br>2:族长')" onMouseOut="hideHelp()"></td>
    </tr>
	<tr>
      <td><input name="faction_removemem" type="submit" id="faction_removemem" value="移除帮众">
        帮众角色ID：
        <input name="faction_removemem1" type="text" id="faction_removemem1" onMouseOver="showHelp(this,'多个以“,”分隔')" onMouseOut="hideHelp()"></td>
    </tr>
	<tr>
      <td><input name="faction_exit" type="submit" id="faction_exit" value="退出家族"></td>
    </tr>
	<tr>
      <td><input name="faction_shanrang" type="submit" id="faction_shanrang" value="帮主禅让">
        帮众角色ID：
      <input name="faction_shanrang1" type="text" id="faction_shanrang1"></td>
    </tr>
	<tr>
      <td><input name="faction_getlist" type="submit" id="faction_getlist" value="获取家族列表">
      页数：
      <input name="faction_getlist1" type="text" id="faction_getlist1"></td>
	</tr>
	<tr>
      <td><input name="faction_getdata" type="submit" id="faction_getdata" value="获取家族详细信息">
      家族ID：
          <input name="faction_getdata1" type="text" id="faction_getdata1"></td>
    </tr>
	<tr>
      <td><input name="faction_getranking" type="submit" id="faction_getranking" value="获取自己家族的排名"></td>
    </tr>
	<tr>
      <td><input name="faction_getranking2" type="submit" id="faction_getranking2" value="获取其他家族的排名">
        家族ID：
      <input name="faction_getranking21" type="text" id="faction_getranking21"></td>
    </tr>
	<tr>
      <td><input name="faction_search" type="submit" id="faction_search" value="搜索家族">
        家族名：
        <input name="faction_search1" type="text" id="faction_search1"></td>
    </tr>
	<tr>
      <td><input name="faction_impeach" type="submit" id="faction_impeach" value="弹劾帮主"></td>
    </tr>
	<tr>
		<td><input name="faction_uplevel" type="submit" id="faction_uplevel" value="升级帮派"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td><input name="faction_uptechnology" type="submit" id="faction_uptechnology" value="升级科技">
		  科技编号：
        <input name="faction_uptechnology1" type="text" id="faction_uptechnology1"></td>
	</tr>
	<tr>
		<td><input name="faction_getwelfare" type="submit" id="faction_getwelfare" value="领取福利"></td>
	</tr>
	<tr>
		<td><input name="faction_worship" type="submit" id="faction_worship" value="膜拜">
		  被膜拜玩家ID
		  ：
            <input name="faction_worship1" type="text" id="faction_worship1">
        膜拜编号：
        <input name="faction_worship2" type="text" id="faction_worship2"></td>
	</tr>
	<tr>
		<td><input name="faction_getbeworshipaward" type="submit" id="faction_getbeworshipaward" value="领取被膜拜奖励"></td>
	</tr>
	<tr>
      <td><input name="faction_revocationapply" type="submit" id="faction_revocationapply" value="撤销申请">
        家族ID：
        <input name="faction_revocationapply1" type="text" id="faction_revocationapply1"></td>
    </tr>
	<tr>
      <td><input name="faction_processapply" type="submit" id="faction_processapply" value="处理申请">
        角色ID：
        <input name="faction_processapply1" type="text" id="faction_processapply1">
        处理方式：
        <input name="faction_processapply2" type="text" id="faction_processapply2" onMouseOver="showHelp(this,'0.同意 1.拒绝')" onMouseOut="hideHelp()"></td>
  </tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
      <td><font color="#FF0000">调试功能</font></td>
    </tr>
	<tr>
		<td><input name="faction_addmoney" type="submit" id="faction_addmoney" value="加帮派资金">
		  数量
		  ：
            <input name="faction_addmoney1" type="text" id="faction_addmoney1"></td>
	</tr>
	<tr>
		<td><input name="faction_addcon" type="submit" id="faction_addcon" value="加个人功勋">
数量
		  ：
  <input name="faction_addcon1" type="text" id="faction_addcon1"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
 </table>
</form>
</body>

</html>