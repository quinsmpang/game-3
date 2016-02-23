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
      <td width="765">社交</td>
    </tr>
	<tr>
      <td><input name="friends_add" type="submit" id="friends_add" value="加好友">
        好友ID
        ：
          <input name="friends_add1" type="text" id="friends_add1" size="20">
        类型
        ：
        <input name="friends_add2" type="text" id="friends_add2" onMouseOver="showHelp(this,'1:好友<br>2:黑名单')" onMouseOut="hideHelp()" size="10"></td>
    </tr>
	<tr>
      <td><input name="friends_delete" type="submit" id="friends_delete" value="删好友">
好友ID
        ：
  <input name="friends_delete1" type="text" id="friends_delete1" size="20">
类型
        ：
<input name="friends_delete2" type="text" id="friends_delete2" onMouseOver="showHelp(this,'1:好友<br>2:黑名单')" onMouseOut="hideHelp() size=" size="10"20"></td>
    </tr>
	<tr>
      <td><input name="friends_search" type="submit" id="friends_search" value="搜索玩家">
        条件
        ：
      <input name="friends_search1" type="text" id="friends_search1" size="20"></td>
    </tr>
	<tr>
      <td><input name="friends_search_quick" type="submit" id="friends_search_quick" value="快速查找"></td>
	</tr>
	<tr>
      <td><input name="friends_present" type="submit" id="friends_present" value="赠送好友体力">
好友ID
        ：
  <input name="friends_present1" type="text" id="friends_present1" size="20"></td>
    </tr>
	<tr>
      <td><input name="friends_present_ok" type="submit" id="friends_present_ok" value="一键赠送">
好友数据
        ：
  <input name="friends_present_ok1" type="text" id="friends_present_ok1" size="20"></td>
    </tr>
	<tr>
      <td><input name="friends_geten" type="submit" id="friends_geten" value="领取好友体力">
好友ID
        ：
  <input name="friends_geten1" type="text" id="friends_geten1" size="20"></td>
    </tr>
	<tr>
      <td><input name="friends_geten_ok" type="submit" id="friends_geten_ok" value="一键领取"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><font color="#FF0000">调试功能</font></td>
    </tr>
	<tr>
      <td><input name="debug_friends_add" type="submit" id="debug_friends_add" value="加好友">
数量
        ：
  <input name="debug_friends_add1" type="text" id="debug_friends_add1" size="10">
类型
        ：
<input name="debug_friends_add2" type="text" id="debug_friends_add2" onMouseOver="showHelp(this,'1:好友<br>2:黑名单')" onMouseOut="hideHelp()" size="10"></td>
    </tr>
	<tr>
      <td><input name="debug_friends_delete" type="submit" id="debug_friends_delete" value="清空好友">

类型
        ：
<input name="debug_friends_delete2" type="text" id="debug_friends_delete2" onMouseOver="showHelp(this,'1:好友<br>2:黑名单')" onMouseOut="hideHelp()" size="10"></td>
    </tr>
	<tr>
      <td><input name="debug_friends_reset_pre" type="submit" id="debug_friends_reset_pre" value="清空好友赠送"></td>
    </tr>
 </table>
</form>
</body>

</html>