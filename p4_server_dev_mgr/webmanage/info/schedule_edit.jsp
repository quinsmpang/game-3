<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<%
String model="数据维护";
String perm="计划任务";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
String name="";
int type=3;
String starttime="";
int period=0;
String sql="";
int state=0;
String savetime=Tools.getCurrentDateTimeStr();
String word1="";
String word2="";
String word3="";

int id = ToolFunc.str2int(request.getParameter("id"));

ScheduleBAC scheduleBAC = ScheduleBAC.getInstance();
JSONObject xml = scheduleBAC.getJsonObj("id="+id);
if(xml!=null)
{
name=xml.optString("name");
type=xml.optInt("type");
starttime=xml.optString("starttime");
period=xml.optInt("period");
sql=xml.optString("sql");
savetime=xml.optString("savetime");
state=xml.optInt("state");
word1=xml.optString("word1");
word2=xml.optString("word2");
word3=xml.optString("word3");
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<title></title>

<link href="../css/style1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="skins/aspsky_1.css" >
<link rel="STYLESHEET" type="text/css" href="images/post/edit.css">
<script src="../js/common.js"></script>
<script src="../js/patterns.js"></script>
<script src="../js/meizzDate.js"></script>
<script src="../js/DhtmlEdit.js"></script>
<script>
var allValue=new Object();
allValue.id="<%=id%>";
allValue.name="<%=name%>";
allValue.type="<%=type%>";
allValue.starttime="<%=starttime%>";
allValue.period="<%=period%>";
allValue.sql="<%=sql%>";
allValue.savetime="<%=savetime%>";
allValue.state="<%=state%>";
allValue.word1="<%=word1%>";
allValue.word2="<%=word2%>";
allValue.word3="<%=word3%>";
</script>
<script>
self.focus();

function checkForm()
{
	var theForm=document.forms[0];
	
	if(theForm.name.value=="")
	{
	alert("请输入任务名");
	theForm.name.focus();
	return false;
	}
	if(theForm.type.value=="")
	{
	alert("请输入任务类型");
	theForm.type.focus();
	return false;
	}
	if(isNaN(theForm.type.value))
	{
	alert("任务类型请输入数值");
	theForm.type.focus();
	theForm.type.select();
	return false;
	}
	if(theForm.starttime.value=="")
	{
		alert("请输入开始时间");
		theForm.starttime.focus();
		return false;
	}
	if(theForm.starttime.value!="" && !isTime(theForm.starttime.value))
	{
		alert("开始时间请输入yyyy-mm-dd hh:mm:ss时间格式");
		theForm.starttime.focus();
		theForm.starttime.select();
		return false;
	}
	if(theForm.period.value!="" && isNaN(theForm.period.value))
	{
		alert("间隔时间请输入数值");
		theForm.period.focus();
		theForm.period.select();
		return false;
	}
	if(theForm.savetime.value!="" && !isDate(theForm.savetime.value) && !isTime(theForm.savetime.value))
	{
		alert("保存时间请输入日期格式");
		theForm.savetime.focus();
		theForm.savetime.select();
		return false;
	}


	
	theForm.encoding="multipart/form-data";
	theForm.target="hiddenFrame";
	theForm.action="schedule_upload.jsp";
	theForm.submit();
	
	wait();
}
function switchSelect(obj)
{
	if(obj.selectedIndex==0) //关服
	{
		document.getElementById("rowWord1").style.display="block";
		document.getElementById("rowWord2").style.display="block";
		document.getElementById("rowWord3").style.display="block";
		
		document.getElementById("rowSql").style.display="none";
		document.getElementById("sql").value="";		
	}
	else
	if(obj.selectedIndex==1) //开服
	{
		document.getElementById("rowWord1").style.display="none";
		document.getElementById("rowWord2").style.display="none";
		document.getElementById("rowWord3").style.display="none";
		document.getElementById("rowSql").style.display="none";
		
		document.getElementById("word1").value="";
		document.getElementById("word2").value="";
		document.getElementById("word3").value="";
		document.getElementById("sql").value="";
	}
	else
	if(obj.selectedIndex==2) //执行sql
	{
		document.getElementById("rowWord1").style.display="none";
		document.getElementById("rowWord2").style.display="none";
		document.getElementById("rowWord3").style.display="none";
		document.getElementById("rowSql").style.display="block";
		
		document.getElementById("word1").value="";
		document.getElementById("word2").value="";
		document.getElementById("word3").value="";
	}
}
</script>
</head>
<body bgcolor="#EFEFEF" >

<form action="" method="post" enctype="multipart/form-data" name="form1"> 
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td valign="top" > <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td ><table width="100%"  border="0" cellspacing="1" cellpadding="2">
    <tr>
      <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
          <tr>
            <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="self.close()"> <img src="../images/icon_closewindow1.gif" width="16" height="16" align="absmiddle"> 关闭</td>
          </tr>
      </table></td>
    </tr>
  </table><table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="1" valign="bottom" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="../images/tab_left.gif" width="6" height="22"></td>
                    <td nowrap background="../images/tab_midbak.gif">计划任务</td>
                    <td><img src="../images/tab_right.gif" width="6" height="22"></td>
                  </tr>
                </table> 
                </td>
                <td valign="bottom" > <table width="100%" border="0" cellspacing="0" cellpadding="0" height="22">
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
                  </table></td>
              </tr>
            </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td rowspan="3" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
                  <td colspan="2" align="center" valign="top"><table width="95%" border="0" cellspacing="1" cellpadding="2">
                      <tr> 
                        <td><fieldset><legend>编辑</legend>
                            
                            <table width="100%" border="0" cellspacing="1" cellpadding="2">    
                            <tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>任务名</strong></td><td width="93%" class="nrbgc1"><input name="name" type="text" value=""><font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>任务类型</strong></td>
  <td width="93%" class="nrbgc1"><font color="#FF0000">
<label>
  <select name="type" id="type" onChange="switchSelect(this)">
	<option value="1">关服</option>    
	<option value="2">开服</option>	
    <option value="3">执行SQL</option>
  </select>
  </label>
  *</font></td>
</tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>开始时间</strong></td><td width="93%" class="nrbgc1"><input name="starttime" type="text" value="" onClick="setday(this,true)">
  <font color="#FF0000">*</font></td></tr>
<tr><td width="10%" align="right" nowrap class="nrbgc1"><strong>间隔时间</strong></td><td width="93%" class="nrbgc1"><input name="period" type="text" value="">
(秒为单位，0表示不重复执行)</td>
</tr>
<tr id="rowSql" style="display:none"><td width="10%" align="right" nowrap class="nrbgc1"><strong>SQL语句</strong></td><td width="93%" class="nrbgc1"><input name="sql" type="text" value="" size="100"></td></tr>
<tr id="rowWord1" style="display:none">
  <td align="right" nowrap class="nrbgc1"><strong>下线提示语句</strong></td>
  <td class="nrbgc1"><input name="word1" type="text" id="word1" value="" size="100"></td>
</tr>
<tr id="rowWord2" style="display:none">
  <td align="right" nowrap class="nrbgc1"><strong>维护中提示语句</strong></td>
  <td class="nrbgc1"><input name="word2" type="text" id="word2" value="" size="100"></td>
</tr>
<tr id="rowWord3" style="display:none">
  <td align="right" nowrap class="nrbgc1"><strong>游戏服维护弹出提示</strong></td>
  <td class="nrbgc1"><input name="word3" type="text" id="word3" value="" size="100"></td>
</tr>
<tr>
  <td align="right" nowrap class="nrbgc1"><strong>状态</strong></td>
  <td class="nrbgc1"><font color="#FF0000">
    <select name="state" id="state">
      <option value="0">等待中</option>
      <option value="1">计时中</option>
      <option value="2">执行过</option>
	  <option value="3">执行完毕</option>
        </select>
  </font></td>
</tr>
<tr>
  <td width="10%" align="right" nowrap class="nrbgc1"><strong>创建时间</strong></td><td width="93%" class="nrbgc1"><input name="savetime" type="text" value=""></td></tr>
                            </table>
				
                            <table width="100%"  border="0" cellspacing="1" cellpadding="2">
                              <tr>
                                <td align="right">
				<input type="hidden" name="id"/>
                                  <table width="50" border="0" cellspacing="0" cellpadding="2">
                                    <tr>
                                      <td height="21" nowrap class="btntd" style="cursor:hand"  onMouseDown="this.className='btntd_mousedown'" onMouseUp="this.className='btntd'" onMouseOut="this.className='btntd'" onselectstart="return false" onClick="checkForm()"><img src="../images/icon_save2.gif" width="16" height="16" align="absmiddle"> 保存</td>
                                    </tr>
                                  </table></td>
                              </tr>
                            </table>
                        </fieldset><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                              <tr>
                                <td height="25"> <font color="#FF0000">*</font> 必填项</td>
                              </tr>
                            </table></td>
                      </tr>
                  </table></td>
                  <td rowspan="3" bgcolor="#848284" width="1"><img src="../images/spacer.gif" width="1" height="1"></td>
                </tr>
                
                <tr> 
                  <td height="1" colspan="2" bgcolor="#848284"></td>
                </tr>
              </table></td>
        </tr>
      </table></td>
  </tr>
</table>
</form>
<iframe name="hiddenFrame" width=0 height=0></iframe>

<script>
autoChoose(allValue);
switchSelect(document.getElementById("type"));
</script>
</body>
</html>