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
.style1 {color: #FF0000}
-->
</style>
</head>

<body>
<form id="form" name="form" method="post" action="dev_request.jsp" target="_self" enctype="multipart/form-data">
  <table width="1138" border="0">
	<tr>
      <td width="762">----------------------游戏准备------------------------</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>版本</td>
    </tr>
	<tr>
      <td><input name="ver_apk_check" type="submit" id="ver_apk_check" value="检测程序版本">
        渠道：
      <input name="ver_apk_check1" type="text" id="ver_apk_check1" value="000"></td>
	</tr>
	<tr>
      <td><input name="ver_res_check" type="submit" id="ver_res_check" value="检测资源版本">
        渠道：
      <input name="ver_res_check1" type="text" id="ver_res_check1" value="000"></td>
    </tr>
    <tr>
      <td><input name="ver_version_res" type="submit" id="ver_version_res" value="获取资源版本">
      版本号：
      <input name="ver_version_res1" type="text" id="ver_version_res1">
      平台：
      <input name="ver_version_res2" type="text" id="ver_version_res2" value="1" onMouseOver="showHelp(this,'1.android<br>2.ios<br>3.pc')" onMouseOut="hideHelp()"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>游戏公告</td>
    </tr>
	<tr>
      <td><input name="getsysnotice" type="submit" id="getsysnotice" value="获取系统公告">
        联运渠道：
          <input name="getsysnotice2" type="text" id="getsysnotice2" value="000">
        起始时间：
        <input name="getsysnotice1" type="text" id="getsysnotice1" value="2013-01-01 00:00:00"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td><span class="style1">波克</span>密码找回</td>
    </tr>
	<tr>
      <td><input name="user_mobilefindpwd" type="submit" id="user_mobilefindpwd" value="手机找回密码">
        帐号：
          <input name="user_mobilefindpwd1" type="text" id="user_mobilefindpwd1">
手机：
<input name="user_mobilefindpwd2" type="text" id="user_mobilefindpwd2"></td>
    </tr>
	<tr>
      <td><input name="user_emailfindpwd" type="submit" id="user_emailfindpwd" value="邮箱找回密码">
        帐号：
        <input name="user_emailfindpwd1" type="text" id="user_emailfindpwd1">
邮箱：
<input name="user_emailfindpwd2" type="text" id="user_emailfindpwd2"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>维护</td>
    </tr>
	<tr>
      <td><input name="jump_check" type="submit" id="jump_check" value="申请跳过维护">
申请IP
        ：
  <input name="jump_check1" type="text" id="jump_check1"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>用户注册</td>
    </tr>
	<tr>
      <td><input name="user_reg" type="submit" id="user_reg" value="注册">
      帐号：
        <label>
        <input name="username" type="text" id="username">
        密码：
        <input name="password" type="text" id="password">
        联运渠道：
        <input name="user_reg1" type="text" id="user_reg1" value="000" onMouseOver="showHelp(this,'000.测试渠道<br>001.波克城市')" onMouseOut="hideHelp()">
        </label></td>
    </tr>
	<tr>
      <td><input name="user_shortcut_game" type="submit" id="user_shortcut_game" value="快速注册">
      联运渠道：
      <input name="user_shortcut_game1" type="text" id="user_shortcut_game1" value="000"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>用户登录</td>
    </tr>
	<tr>
    <td>
	<%
	String sessionid = CookieUtil.get(pageContext, "dev_sessionid");
	com.moonic.mode.User user = null;
	if(sessionid != null) {
		user = UserBAC.getInstance().loadUser(sessionid);
	}
	if(user != null) {
	%>
		已登录：<font color=red><%=user.username%></font><strong>&nbsp;|&nbsp;</strong>
		<input name="user_logout" type="submit" id="user_logout" value="注销登录">
	<%
	} else {
	%>	  
	  	<input name="user_login" type="submit" id="user_login" value="登录">
        帐号：        
        <input name="username2" type="text" id="username2">
		密码：
		<input name="password2" type="text" id="password2">
        联运渠道：
        <input name="user_login1" type="text" id="user_login1" value="000">
		扩展参数：
        <input name="user_login2" type="text" id="user_login2">
	<%
		return;
	}
	%>		</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>用户</td>
    </tr>
	<tr>
     <td><input name="user_user_activate" type="submit" id="user_user_activate" value="帐号激活">
        激活码：
          <label>
        <input name="user_user_activate1" type="text" id="user_user_activate1">
      </label></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>游戏服务器</td>
    </tr>
	<tr>
      <td><input name="server_list" type="submit" id="server_list" value="获取服务器列表"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>角色</td>
    </tr>
	<tr>
      <td>选择服务器：
        <label>
        <select name="vsid">
		<%
		DBPsRs channelServerRs = ServerBAC.getInstance().getChannelServerList(user.channel);
		while(channelServerRs.next()){
		%>
			<option value="<%=channelServerRs.getInt("vsid")%>"><%=channelServerRs.getString("servername")%></option>
		<%
		}
		%>
        </select>
      </label></td>
    </tr>
	<tr>
      <td><input name="player_ranname" type="submit" id="player_ranname" value="随机姓名">
      数量：
      <input name="ranamount" type="text" id="ranamount"></td>
    </tr>
	<tr>
      <td><input name="player_create" type="submit" id="player_create" value="创建角色">
        角色名：
      <input name="playername" type="text" id="playername">
        列表数据编号：
        <input name="playernum" type="text" id="playernum">
        初始伙伴编号
        ：
        <input name="partnernum" type="text" id="partnernum"></td>
    </tr>
	<tr>
      <td><input name="player_logininfo" type="submit" id="player_logininfo" value="查看角色信息"></td>
    </tr>
	<tr>
      <td><input name="player_login" type="submit" id="player_login" value="登录游戏服务器"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
  </table>
  <hr>
</form>
</body>

</html>