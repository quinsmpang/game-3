<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%@ page import="java.io.*"%>
<%
String model="数据维护";
String perm="文件管理";
//request.setCharacterEncoding("UTF-8");
String parentFile=request.getParameter("parentFile");
//System.out.println("parentFile="+parentFile);
File parentFolder=null;;
if(parentFile!=null)
{
	parentFolder = new File(parentFile);
}
else
{
	parentFolder = new File(ServerConfig.getAppRootPath());
	parentFile = parentFolder.getAbsolutePath();
}
boolean root = false;
if(userObj!=null) {
	root = userObj.optBoolean("root");
}
%>
<%@ include file="../system/inc_checkperm.jsp"%>


<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>
<script>
function gotoFolder(folder)
{
	//alert(folder)
	var theForm = document.forms[0];
	document.getElementById("parentFile").value=folder;
	//alert("theForm="+theForm);
	//alert(document.getElementById("parentFile").value);
	//alert("document.getElementById("parentFile").value="+document.getElementById("parentFile").value);
	theForm.submit();
	
}

//.alert("ok")
</script>
<link href="../css/style1.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EFEFEF">

<form name="form1" method="post">
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
                    <td nowrap background="../images/tab_midbak.gif">文件管理</td>
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
                    <td align="center">
					
					
					
					<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
									  <td>
										<a href="javascript:location.reload()">刷新</a><br/>
										当前目录：<%=parentFolder.getAbsolutePath()%><br/>
										<%
										if(parentFolder.getParentFile()!=null && parentFolder.getParentFile().exists()){
										%>
										<img src="../images/parentfolder.png" align="absmiddle"> <a href="javascript:gotoFolder('<%=Tools.replace(parentFolder.getParent(),"\\","\\\\")%>')">上一级目录</a><br/>
										<%
										}										
										File[] subFiles = SystemFolderBAC.getInstance().getSubFiles(parentFolder);										
										%>
										<table border="0" cellspacing="2" cellpadding="2">
										  <tr>
											<td>文件名</td>
											<td>大小</td>
											<td>修改时间</td>
											<td>操作</td>
										  </tr>
										<%
										for(int i=0; subFiles!=null && i<subFiles.length; i++) {
										%>
										<tr>
											<td>
											<%
											if(subFiles[i].isFile()){
											%>
											<img src="../images/file.png" align="absmiddle"> 
											<a href="../../download.do?path=<%=URLEncoder.encode(subFiles[i].getAbsolutePath(),"UTF-8")%>"><%=subFiles[i].getName()%></a>												
											<%
											} else {
											%>
											<img src="../images/folder.png" align="absmiddle"> <a href="javascript:gotoFolder('<%=Tools.replace(subFiles[i].getAbsolutePath(),"\\","\\\\")%>')"><%=subFiles[i].getName()%></a>
											<%
											}
											%>
											</td>
											<td>
											<%
											if(subFiles[i].isFile()){
											%>
											<%=subFiles[i].length()%>字节
											<%
											}
											%>
											</td>
											<td><%=Tools.millisecond2DateTimeStr(subFiles[i].lastModified())%></td>
											<td>
											<%
											if(root){
												if(subFiles[i].isFile()){
												%>
												<a onClick="return confirm('确定删除<%=subFiles[i].getName()%>文件吗？')" target="hiddenFrame" href="system_folder_opfile.jsp?path=<%=URLEncoder.encode(subFiles[i].getAbsolutePath(),"UTF-8")%>">删除文件</a>
												<%
												} else {
													if(subFiles[i].listFiles()==null || subFiles[i].listFiles().length==0) {
													%>
													<a onClick="return confirm('确定删除<%=subFiles[i].getName()%>目录吗？')" target="hiddenFrame" href="system_folder_opfile.jsp?path=<%=URLEncoder.encode(subFiles[i].getAbsolutePath(),"UTF-8")%>">删除目录</a>
													<%
													} else {
													%>
													<a onClick="return confirm('确定删除<%=subFiles[i].getName()%>目录下全部文件吗？')" target="hiddenFrame" href="system_folder_opfile.jsp?path=<%=URLEncoder.encode(subFiles[i].getAbsolutePath(),"UTF-8")%>">删除目录下全部文件</a>
													| 
													<a href="../../download.do?path=<%=URLEncoder.encode(subFiles[i].getAbsolutePath(),"UTF-8")%>&pack=1">目录打包下载</a>
													<%
													}
												}
											}
											%>
											</td>
										  </tr>
										  <%
										  }
										  %>								
								</table>
								        </td>
							</tr>

				</table>
									
					
					
					</td>
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
<input name="parentFile" type="hidden" id="parentFile">
</form>
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
</html>
