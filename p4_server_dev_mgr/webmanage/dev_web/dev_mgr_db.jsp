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
<form id="form" name="form" method="post" action="dev_request.jsp" target="_self" enctype="multipart/form-data">
  <table width="1138" border="0">
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td width="762">----------------------数据库管理------------------------</td>
    </tr>
	<tr>
      <td><span class="style1">*.服务器类型：0.用户服 1.游戏服</span></td>
    </tr>
	<tr>
      <td><input name="console_getserverinfo" type="submit" id="console_getserverinfo" value="获取连接数量信息">
        服务器类型
          <input name="console_getserverinfo0" type="text" id="console_getserverinfo0" size="10">
          服务器ID：
          <input name="console_getserverinfo1" type="text" id="console_getserverinfo1"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_dbtesta" type="submit" id="console_dbtesta" value="列表缓存清单">
        服务器类型
          <input name="console_dbtesta0" type="text" id="console_dbtesta0" size="10">
服务器ID：
      <input name="console_dbtesta1" type="text" id="console_dbtesta1"></td>
    </tr>
	<tr>
      <td><input name="console_dbtestb" type="submit" id="console_dbtestb" value="文本缓存清单">
        服务器类型
          <input name="console_dbtestb0" type="text" id="console_dbtestb0" size="10">
        服务器ID：
    <input name="console_dbtestb1" type="text" id="console_dbtestb1"></td>
    </tr>
	<tr>
      <td><input name="console_dbtest1" type="submit" id="console_dbtest1" value="列表缓存测试">
        服务器类型
          <input name="console_dbtest10" type="text" id="console_dbtest10" size="10">
服务器ID：
          <input name="console_dbtest12" type="text" id="console_dbtest12">
        TAB：
      <input name="console_dbtest11" type="text" id="console_dbtest11"></td>
	</tr>
	<tr>
      <td><input name="console_dbtest2" type="submit" id="console_dbtest2" value="列表缓存清理">
        
        TAB：
      <input name="console_dbtest21" type="text" id="console_dbtest21"></td>
    </tr>
	<tr>
      <td><input name="console_dbtest3" type="submit" id="console_dbtest3" value="文本缓存测试">
        服务器类型
          <input name="console_dbtest30" type="text" id="console_dbtest30" size="10">
服务器ID：
        <input name="console_dbtest32" type="text" id="console_dbtest32">
        KEY：
        <input name="console_dbtest31" type="text" id="console_dbtest31"></td>
    </tr>
	<tr>
      <td><input name="console_dbtest4" type="submit" id="console_dbtest4" value="文本缓存清理">
        
        KEY：
      <input name="console_dbtest41" type="text" id="console_dbtest41"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="console_get_insertlog_state" type="submit" id="console_get_insertlog_state" value="获取插入日志现成运行状态">
        服务器类型
          <input name="console_get_insertlog_state0" type="text" id="console_get_insertlog_state0" size="10">
服务器ID：
        <input name="console_get_insertlog_state1" type="text" id="console_get_insertlog_state1"></td>
    </tr>
	<tr>
      <td><input name="console_reset_insertlog_timeoutam" type="submit" id="console_reset_insertlog_timeoutam" value="重置插入日志失败次数">
        服务器类型
          <input name="console_reset_insertlog_timeoutam0" type="text" id="console_reset_insertlog_timeoutam0" size="10">
服务器ID：
        <input name="console_reset_insertlog_timeoutam1" type="text" id="console_reset_insertlog_timeoutam1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="clear_log" type="submit" id="clear_log" value="清理日志">
TAB：
  <input name="clear_log1" type="text" id="clear_log1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>其他：</td>
    </tr>
	<tr>
      <td><input name="debug_sendmail" type="submit" id="debug_sendmail" value="发邮件">
        收件地址
        ：
          <input name="debug_sendmail1" type="text" id="debug_sendmail1">
          标题
          ：
          <input name="debug_sendmail2" type="text" id="debug_sendmail2">
内容：
<input name="debug_sendmail3" type="text" id="debug_sendmail3"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>镜像</td>
    </tr>
	<tr>
      <td><input name="debug_getplaallmirror" type="submit" id="debug_getplaallmirror" value="查看角色镜像">
      服务器ID：
      <input name="debug_getplaallmirror1" type="text" id="debug_getplaallmirror1" size="10">
      角色ID：
      <input name="debug_getplaallmirror2" type="text" id="debug_getplaallmirror2" size="10"></td>
    </tr>
	<tr>
      <td><input name="debug_getplamirror" type="submit" id="debug_getplamirror" value="查看角色镜像">
        服务器ID：
        <input name="debug_getplamirror1" type="text" id="debug_getplamirror1" size="10">
角色ID：
<input name="debug_getplamirror2" type="text" id="debug_getplamirror2" size="10">
TAB：
<input name="debug_getplamirror3" type="text" id="debug_getplamirror3" size="10"></td>
    </tr>
	<tr>
      <td><input name="debug_gettabmirror" type="submit" id="debug_gettabmirror" value="查看表镜像">
        服务器ID：
        <input name="debug_gettabmirror1" type="text" id="debug_gettabmirror1" size="10">

TAB：
<input name="debug_gettabmirror2" type="text" id="debug_gettabmirror2" size="10"></td>
    </tr>
	<tr>
      <td><input name="debug_mirrorcleartab" type="submit" id="debug_mirrorcleartab" value="清理表镜像">
        服务器ID：
        <input name="debug_mirrorcleartab1" type="text" id="debug_mirrorcleartab1" size="10">
TAB：
<input name="debug_mirrorcleartab2" type="text" id="debug_mirrorcleartab2" size="10"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>测试</td>
    </tr>
	<tr>
      <td><input name="temp_jsonquerytest" type="submit" id="temp_jsonquerytest" value="内存查询速度测试">
      参数：
      <input name="temp_jsonquerytest1" type="text" id="temp_jsonquerytest1" value="10000,20000,30000,40000,50000,60000,70000,80000,90000,100000"></td>
    </tr>
	<tr>
      <td><input name="temp_groovy_test" type="submit" id="temp_groovy_test" value="GROOVY测试"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
  </table>
</form>
</body>

</html>