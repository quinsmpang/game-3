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
.style3 {color: #FF0000}
-->
</style>
</head>
<body>
<script>
		function addPic(p1,p2){
			obj = document.getElementById(p2);
			var str = "";
			str+="文件：";
			str+="<span>";
			str+="<input name=\""+p1+"\" type=\"file\" size=\"50\">";
			str+="<input type=\"button\" value=\"删除\" onclick=\"delPic(this.parentNode)\"><br>";
			str+="</span>";
			str+="<span id=\""+p2+"\"/>";
			obj.outerHTML = str;
		}
		function delPic(obj){									
			obj.outerHTML = "";
		}
</script>
<form id="form" name="form" method="post" action="dev_request.jsp" target="_self" enctype="multipart/form-data">
  <table width="755" border="0">
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td nowrap>--------------------------数据更新--------------------------</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>更新<span class="style3">列表</span>数据(以tab_为头命名的数据文件)</td>
    </tr>
	<tr>
      <td nowrap>&nbsp;</td>
    </tr>
	<tr>
      <td nowrap><label>更新至：
          <select name="update_list2" id="update_list2" onMouseOver="showHelp(this,'选择数据库则改变数据库数据，其他则仅更新资源版本备份')" onMouseOut="hideHelp()">
          <option value="" selected>数据库</option>
          <option value="_内测版">内测版文件夹</option>
          <option value="_体验版">体验版文件夹</option>
          <option value="_联运版">安卓版文件夹</option>
		  <option value="_苹果版">苹果版文件夹</option>
          </select>
      </label></td>
    </tr>
	<tr>
      <td nowrap>&nbsp;</td>
    </tr>
	<tr>
      <td nowrap>创建新表：
        <input name="update_list1" type="text" id="update_list1" value="0" onMouseOver="showHelp(this,'创建新表需要有DB权限，除开发版外都必须填“0”，开发版需要重新建表时请填“1”')" onMouseOut="hideHelp()"></td>
    </tr>
	<tr>
      <td nowrap>&nbsp;</td>
    </tr>
    <tr>
      <td>
	  	文件：
	  	  <input name="update_listfile" type="file" id="update_listfile" size="50">
	  	<br>
	  	<span id="fileExpand1"></span>
	  	<input type="button" name="button" value="添加" onClick="addPic('update_listfile','fileExpand1')">  	  </td>
    </tr>
	<tr>
      <td nowrap>&nbsp;</td>
    </tr>
	<tr>
      <td><input name="update_list" type="submit" id="update_list" value="提交"></td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
     <td>-------------</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>更新<span class="style3">文本</span>数据(参考：&quot;服务端数据结构&quot;文件 &quot;文本&quot;部分)</td>
	</tr>
	<tr>
      <td>文件：
      <input name="update_txtfile" type="file" id="update_txtfile" size="50">
	  <br>
	  	<span id="fileExpand2"></span>
	  	<input type="button" name="button" value="添加" onClick="addPic('update_txtfile','fileExpand2')">  	  </td>
	</tr>
	<tr>
      <td>&nbsp;</td>
	</tr>
	<tr>
      <td><input name="update_txt" type="submit" id="update_txt" value="提交"></td>
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