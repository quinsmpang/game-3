<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="系统功能";
String perm="SQL执行";
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<script src="../js/common.js"></script>
<script>
function checkForm()
{
	wait();
	document.forms[0].submit();
}
</script>
</head>

<body bgcolor="#EFEFEF">
<form name="form1" method="post" action="sqlexecute_upload.jsp" target="hiddenFrame" enctype="multipart/form-data">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td > 
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif" width="6" height="22"></td>
                    <td nowrap background="../images/tab_midbak.gif">表执行</td>
                    <td><img src="../images/tab_right.gif" width="6" height="22"></td>
                  </tr>
                </table></td>
                <td valign="bottom" > 
                  <table width="100%" border="0" cellspacing="0" cellpadding="0" height="22">
                    <tr> 
                      <td></td>
                      <td width=1></td>
                    </tr>
                    <tr > 
                      <td bgcolor="#FFFFFF" colspan="2" height=1></td>
                    </tr>
                    <tr > 
                      <td height=3></td>
                      <td bgcolor="#848284"  height=3 width="1"></td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td rowspan="2" bgcolor="#FFFFFF" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
                <td valign="top" align="center"><table width="95%" border="0" cellspacing="1" cellpadding="2">
                  <tr>
                    <td align="center"><table width="100%" border="0" cellspacing="1" cellpadding="2">
					  <tr>
                        <td valign="top"><strong>数据库选择：</strong>
								<select name="useDB" id="useDB">
                                    <option value="1">主库</option>
                                    <!--
									<option value="2">日志库</option>
									<option value="3">报表库</option>
									-->
								</select>
						</td>
                      </tr>
					  <!-- 
                      <tr>
                        <td valign="top">SQL语句示例：<br>
                          增加字段：alter table 表名 add 字段名 字段类型(如number(10),varchar2(255))<br>
                          删除字段：alter table 表名 drop column 字段名<br>
                          增加序列对象：create sequence 序列名 start with 1<br>
                          删除序列对象：drop sequence 序列名  </td>
                      </tr>
					  -->
                      <tr>
                        <td valign="top">SQL执行语句：<br>
                          <label>
                            <textarea name="sqlLines" cols="130" rows="10" wrap="off" id="sqlLines"></textarea>
                            <br>
                            按文件执行：
                            <input name="exesqlfile" type="file" id="exesqlfile" size="50">
<%
File demofile = new File(Conf.logRoot+"exesql/demo.txt");
if(!demofile.exists()){
StringBuffer sb = new StringBuffer();
sb.append("\r\n");
sb.append("data:\r\n");
sb.append("update tab_player set money=0 where id=251\r\n");
sb.append("dataEnd");
FileUtil fileutil = new FileUtil();
fileutil.writeNewToTxt(Conf.logRoot+"exesql/demo.txt", sb.toString());
}
%>
<a href="../../download.do?path=<%=Conf.logRoot+"exesql/demo.txt"%>">下载DEMO</a><font color="#ff0000">（请保证上传文件的内容编码为UTF8）</font>
                            <br>
                            <input name="Button" type="button" id="Button" onClick="checkForm()" value="执行">
                          </label></td>
                      </tr>
                    </table>
                      <table width="100%" border="0" cellspacing="1" cellpadding="2">
                        <tr>
                          <td>执行结果：<br>
                             <textarea name="result" cols="130" rows="10" id="result"></textarea>                             
                            </td>
                        </tr>
                      </table></td>
                  </tr>
                </table>
                  </td>
                <td rowspan="2" bgcolor="#848284" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
              </tr>
              <tr> 
                <td bgcolor="#848284" height="1"></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<iframe id="hiddenFrame" name="hiddenFrame" width="0" height="0"></iframe>
</form>
</body>
</html>
