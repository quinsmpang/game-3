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

</head>
<body>
<form id="form" name="form" method="post" action="dev_request.jsp" target="_self" enctype="multipart/form-data">
  <table width="755" border="0">
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td nowrap>--------------------------游戏下载--------------------------</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>APK版本列表：</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>
	  <%=VersionBAC.getInstance().debugGetApkList().info%>
	  </td>
    </tr>
	<tr>
      <td>&nbsp;</td>
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