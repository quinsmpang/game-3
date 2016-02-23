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

<style type="text/css">
<!--
.style4 {
	color: #FF0000;
	font-weight: bold;
}
-->
</style>

<script>
function enter(info)
{
	return confirm(info);
}
</script>

</head>

<body>
<form id="form" name="form" method="post" action="dev_request.jsp" target="_self" enctype="multipart/form-data">
  <table width="1092" border="0">
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>----------------------数据管理------------------------</td>
    </tr>
	<tr>
      <td><input name="createsqlfile" type="submit" id="createsqlfile" value="生成SqlFile"><span class="style4">(用于正式服务器更新一键生成SQL执行文件)</span></td>
    </tr>
	<tr>
      <td><span class="style4">除开发版外均无DB权限，无DB权限时设为“0”，开启调试将输出过程信息</span></td>
    </tr>
    <tr>
      <td><label>数据表：</label></td>
    </tr>
	<!--
	<tr>
      <td>        <input name="list_init" type="submit" id="list_init" value="初始化数据表">      </td>
	</tr>
	<tr>
      <td>        <input name="txt_init" type="submit" id="txt_init" value="初始化文本数据">      </td>
	</tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	-->
	<tr>
      <td><input name="list_add" type="submit" id="list_add" value="补充数据表">
        DB权限：
        <input name="list_add1" type="text" id="list_add1" value="1" size="10">
        开启调试：
        <input name="list_add2" type="text" id="list_add2" value="0" size="10"></td>
    </tr>
    <tr>
      <td><input name="data_add" type="submit" id="data_add" value="补充应用表">
        DB权限：
        <input name="data_add1" type="text" id="data_add1" value="1" size="10">
        开启调试：
        <input name="data_add2" type="text" id="data_add2" value="0" size="10"></td>
    </tr>
	<tr>
      <td><input name="txt_add" type="submit" id="txt_add" value="补充文本数据"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="log_add" type="submit" id="log_add" value="补充日志库日志表">
        DB权限：
        <input name="log_add1" type="text" id="log_add1" value="1" size="10">
        开启调试：
        <input name="log_add2" type="text" id="log_add2" value="0" size="10"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="tidy_seq" type="submit" id="tidy_seq" value="整理SEQ">
        DB权限：
          <input name="tidy_seq1" type="text" id="tidy_seq1" value="0" size="10"></td>
    </tr>
	<tr>
      <td><input name="add_seq" type="submit" id="add_seq" value="补充SEQ">
        DB权限：
        <input name="add_seq1" type="text" id="add_seq1" value="1" size="10"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="check_datacolumn" type="submit" id="check_datacolumn" value="检查数据表字段"></td>
    </tr>
	<tr>
      <td><input name="check_log_datacolumn" type="submit" id="check_log_datacolumn" value="检查日志库数据表字段"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="out_tabs" type="submit" id="out_tabs" value="输出所有数据表" onClick="return enterTip('确定要输出所有数据表吗？')"></td>
    </tr>
	<tr>
      <td><input name="out_tabs2" type="submit" id="out_tabs2" value="输出不存在的数据表"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="file_check" type="submit" id="file_check" value="后台服文件检查">
        是否删除多余文件：
      <input name="file_check1" type="text" id="file_check1"></td>
    </tr>
	<tr>
      <td><input name="us_file_check" type="submit" id="us_file_check" value="用户服文件检查">
        是否删除多余文件：
      <input name="us_file_check1" type="text" id="us_file_check1"></td>
    </tr>
	<tr>
      <td><input name="gs_file_check" type="submit" id="gs_file_check" value="游戏服文件检查">
        是否删除多余文件：
      <input name="gs_file_check1" type="text" id="gs_file_check1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="supp_pla_all" type="submit" id="supp_pla_all" value="补充所有角色表数据"></td>
    </tr>
	<tr>
      <td><input name="supp_pla" type="submit" id="supp_pla" value="补充角色表数据">
        表：
          <input name="supp_pla1" type="text" id="supp_pla1" onMouseOver="showHelp(this,'多个数据表用“,”分隔')" onMouseOut="hideHelp()"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="search_col" type="submit" id="search_col" value="搜索字段">
        字段名：
        <input name="search_col1" type="text" id="search_col1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
  </table>
</form>
</body>

</html>