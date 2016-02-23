<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<html>
<head>
<title>-</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="css/style1.css" rel="stylesheet" type="text/css">
<style type="text/css">
<!--
.style3 {
	color: #000000;
	font-size: 16px;
}
.style4 {font-size: 16px}
.style6 {
	color: #CC0000;
	font-size: 12px;
	font-weight: bold;
}
-->
</style>
</head>

<body>
<form id="form" name="form" method="post" action="dev_request.jsp" target="_self" enctype="multipart/form-data">
  <table width="1138" border="0">
    <tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
    <tr>
      <td bordercolor="#000000">----------------------消息管理------------------------</td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><span class="style6">发布公告</span></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><p>服务器ID： </p>
        <p>
          <input name="console_createnotice0" type="text" id="console_createnotice0">
          （多个服务器ID用“,”分隔） </p>
        <p>标题：<br>
        
          <input name="console_createnotice1" type="text" id="console_createnotice1">
          <br>
          <br>
        
        内容：
        
          <br>
          <textarea name="console_createnotice2" cols="45" rows="20" id="console_createnotice2"></textarea>
        
          <br>
          <br>
        过期时间（不填则永不过期）：
      
        <br>
        <input name="console_createnotice3" type="text" id="console_createnotice3" value="">
      
        <br>
        <br>
        重复显示：
      
        <br>
        <input name="console_createnotice4" type="text" id="console_createnotice4">
        <br>
        <br>
        <input name="console_createnotice" type="submit" id="console_createnotice" value="发布">      
        </p></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000">------</td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><span class="style6">发送通知</span></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><p>服务器ID：
          </p>
        <p>
          <input name="console_sendinform0" type="text" id="console_sendinform0">
          （不填则发送到所有游戏服）        </p>
        <p>标题：<br>
        
          <input name="console_sendinform1" type="text" id="console_sendinform1">
          <br>
          <br>
          内容：
          
          <br>
          <textarea name="console_sendinform2" cols="60" rows="3" id="console_sendinform2"></textarea>
          
          <br>
          <br>
          过期时间：
          
          <br>
          <input name="console_sendinform3" type="text" id="console_sendinform3">
        </p>
        <p><br>
扩展内容： <br>
<textarea name="console_sendinform4" cols="60" rows="3" id="console_sendinform4"></textarea>
</p>
        <p>给所有人： <br>
          <input name="console_sendinform5" type="text" id="console_sendinform5">
(0.仅在线 1.所有人)<br>
          <br>
          <input name="console_sendinform" type="submit" id="console_sendinform" value="发送">
        </p></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000">------</td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><span class="style6">发送系统消息</span></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><p>服务器ID：</p>
        <p>
          <input name="console_sendsysmsg0" type="text" id="console_sendsysmsg0">
          （不填则发送到所有游戏服）        </p>
        <p>内容：<br>
        
          <textarea name="console_sendsysmsg1" cols="60" rows="3" id="console_sendsysmsg1"></textarea>
          <br>
          <br>
          <input name="console_sendsysmsg" type="submit" id="console_sendsysmsg" value="发送">      
        </p></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><span class="style6">发送游戏推送</span></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><p>服务器ID：</p>
        <p>
          <input name="console_sendgamepush0" type="text" id="console_sendgamepush0">
        （不填则发送到所有游戏服）        </p>
        <p>内容：<br>
        
          <textarea name="console_sendgamepush2" cols="60" rows="3" id="console_sendgamepush2"></textarea>
          <br>
          <br>
          <input name="console_sendgamepush" type="submit" id="console_sendgamepush" value="发送">      
        </p></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><span class="style6">发送顶部推送</span></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><p>服务器ID：</p>
        <p>
          <input name="console_sendtop0" type="text" id="console_sendtop0">
        （不填则发送到所有游戏服）        </p>
        <p>内容：<br>
        
          <textarea name="console_sendtop1" cols="60" rows="3" id="console_sendtop1"></textarea>
          <br>
          <br>
          <input name="console_sendtop" type="submit" id="console_sendtop" value="发送">      
        </p></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000">------</td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><span class="style6">向单个玩家发送通知</span></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
	<tr>
      <td bordercolor="#000000"><p>服务器ID：
          </p>
        <p>
          <input name="console_sendinform_toone0" type="text" id="console_sendinform_toone0">
        </p>
        <p>玩家ID：</p>
        <p>
          <input name="console_sendinform_toone1" type="text" id="console_sendinform_toone1">
</p>
        <p>标题：<br>
        
          <input name="console_sendinform_toone2" type="text" id="console_sendinform_toone2">
          <br>
          <br>
          内容：
          
          <br>
          <textarea name="console_sendinform_toone3" cols="60" rows="3" id="console_sendinform_toone3"></textarea>
          
          <br>
          <br>
          过期时间：
          
          <br>
          <input name="console_sendinform_toone4" type="text" id="console_sendinform_toone4">
        </p>
        <p>扩展内容： <br>
          <textarea name="console_sendinform_toone5" cols="60" rows="3" id="console_sendinform_toone5"></textarea>
<br>
          <br>
          <input name="console_sendinform_toone" type="submit" id="console_sendinform_toone" value="发送">
        </p></td>
    </tr>
	<tr>
      <td bordercolor="#000000">&nbsp;</td>
    </tr>
  </table>
</form>
</body>

</html>