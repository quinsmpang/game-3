<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
String model="数据维护";
String perm="计划任务";
%>
<%
ScheduleBAC scheduleBAC = ScheduleBAC.getInstance();
%>
<%@ include file="../system/inc_checkperm.jsp"%>

<script src="../js/meizzDate.js"></script>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta HTTP-EQUIV="expires" CONTENT="Tue, 1 Jan 1980 00:00 GMT">
<script src="../js/common.js"></script>

<script>
function del(id)
{
	if(confirm("确认删除吗？"))
	{
		wait();		
		document.getElementById("hiddenFrame").src="schedule_del.jsp?id=" + id;
	}
}
function modify(id)
{
openWindow("schedule_edit.jsp?id=" + id,"modify",800,470,true,true);
}
function add()
{
openWindow("schedule_edit.jsp","add",800,470,true,true);
}
function stopAll()
{
	if(confirm("确定停止并清除运行中的全部计时任务吗？"))
	{
		document.getElementById("hiddenFrame").src="schedule_stopall.jsp";
	}
}
function loadAll()
{
	if(confirm("确定重新载入全部计时任务吗？"))
	{
		document.getElementById("hiddenFrame").src="schedule_loadall.jsp";
	}
}
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
                    <td nowrap background="../images/tab_midbak.gif">计划任务</td>
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
                <td rowspan="2" bgcolor="#FFFFFF" width="1"><img src="images/spacer.gif" width="1" height="1"></td>
                <td valign="top" align="center"> 
                  <table width="95%" border="0" cellspacing="1" cellpadding="2">
							<tr>
							  <td>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                          <tr>
                                            <td height="21" nowrap class="btntd" style="cursor: hand"
													onMouseDown="this.className='btntd_mousedown'"
													onMouseUp="this.className='btntd'"
													onMouseOut="this.className='btntd'"
													onselectstart="return false" onClick="loadAll()"><img
													src="../images/icon_showall.gif"
													align="absmiddle"> 重载全部计时器</td>
                                          </tr>
                                        </table></td>
									</tr>
								</table>
								
								<table width="100%" border="0" cellpadding="2" cellspacing="1"
									class="tbbgc1">
									<tr class="listtopbgc">
										<td width="10" align="center" nowrap>No.</td>
										<td align="center" nowrap>id</td>
										<td align="center" nowrap>任务名</td>
<td align="center" nowrap>任务类型</td>
<td align="center" nowrap>开始时间</td>
<td align="center" nowrap>间隔时间</td>
<td align="center" nowrap>任务状态</td>
<td align="center" nowrap>操作</td>
<td align="center" nowrap>创建时间</td>
									
										<td align="center" nowrap>操作</td>
									</tr>
									
									<%
									
									int num=1;
								  JSONObject xml = scheduleBAC.getJsonObjs(null,"id");									
									JSONArray list=null;
									if(xml!=null)
									{									
										list = xml.optJSONArray("list");
									}
									for(int i=0;list!=null && i<list.length();i++)
									{
									JSONObject line = (JSONObject)list.opt(i);
									int id = line.optInt("id");
									ScheduleTask task = scheduleBAC.getTaskById(id);
									int state = line.optInt("state");
									int executeTimes=0;
									if(task!=null)
									{										
										executeTimes = task.getExecuteTimes();
									}
									%>
									<tr class="nrbgc1">
										<td align="center" nowrap><%=num++%></td>
										<td align="center" nowrap><%=line.optInt("id")%></td>
										<td align="center" nowrap><%=line.optString("name")%></td>
										<td align="center" nowrap><%=ScheduleTask.getTypeName(line.optInt("type"))%></td>
										<td align="center" nowrap><%=line.optString("starttime")%></td>
										<td align="center" nowrap><%=line.optInt("period")%>秒</td>
										<td align="center" nowrap><%if(state==-1){%>
										  <font color="#FF0000">任务对象不存在</font>
								        <%}else if(state==ScheduleTask.STATE_WAIT){%><font color="#FF6633">等待中</font><%}else if(state==ScheduleTask.STATE_TIMER){%><font color="#3366CC">计时中</font><%}else if(state==ScheduleTask.STATE_RUN){%><font color="#3366CC">已执行<%=executeTimes%>次</font><%}else if(state==ScheduleTask.STATE_COMPLETE){%>
								        <font color="#006633">执行完毕</font>
								        <%}%></td>
										<td align="center" nowrap><%if(state==-1 || state==ScheduleTask.STATE_WAIT || state==ScheduleTask.STATE_COMPLETE){%><a href="schedule_start.jsp?id=<%=id%>" target="hiddenFrame"><font color="#006633">启动计时器</font></a><%}else if(state==ScheduleTask.STATE_TIMER || state==ScheduleTask.STATE_RUN){%><a href="schedule_stop.jsp?id=<%=id%>" target="hiddenFrame"><font color="#FF0000">关闭计时器</font></a><%}%></td>
										<td align="center" nowrap><%=line.optString("savetime")%></td>
										
										<td align="center" nowrap><img
											src="../images/icon_modify.gif" alt="修改"
											align="absmiddle" style="cursor: hand"
											onClick="modify(<%=line.optInt("id")%>)">
											<img
											src="../images/icon_del2.gif" alt="删除" align="absmiddle"
											style="cursor: hand" onClick="del(<%=line.optInt("id")%>)"></td>
									</tr>
									<%
									}
									%>
								</table>
								<br>
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									<tr>
										<td align="right">
										<table width="50" border="0" cellspacing="0" cellpadding="2">
											<tr>
												<td height="21" nowrap class="btntd" style="cursor: hand"
													onMouseDown="this.className='btntd_mousedown'"
													onMouseUp="this.className='btntd'"
													onMouseOut="this.className='btntd'"
													onselectstart="return false" onClick="add()"><img
													src="../images/icon_adddepart.gif" width="16" height="16"
													align="absmiddle"> 添加</td>	
											</tr>
								</table>
								</td>
							</tr>
						</table>
						<%Vector taskVC = scheduleBAC.getTaskVC();%>
						        <table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
                                  <tr>
                                    <td colspan="6" align="center" class="listtopbgc">运行中的计时器对象</td>
                                  </tr>
                                  <tr>
                                    <td align="center" class="listtopbgc">id</td>
                                    <td align="center" class="listtopbgc">任务名</td>
                                    <td align="center" class="listtopbgc">任务类型</td>
                                    <td align="center" class="listtopbgc">开始时间</td>
                                    <td align="center" class="listtopbgc">间隔时间</td>
                                    <td align="center" class="listtopbgc">任务状态</td>
                                  </tr>
								  <%for(int i=0;taskVC!=null && i<taskVC.size();i++){								 
								  ScheduleTask task = (ScheduleTask)taskVC.elementAt(i);
								  int state = task.getState();
								  int executeTimes = task.getExecuteTimes();
								  %>
                                  <tr>
                                    <td align="center" bgcolor="#EFEFEF"><%=task.getId()%></td>
                                    <td align="center" bgcolor="#EFEFEF"><%=task.getName()%></td>
                                    <td align="center" bgcolor="#EFEFEF"><%=ScheduleTask.getTypeName(task.getType())%></td>
                                    <td align="center" bgcolor="#EFEFEF"><%=task.getStartTime()%></td>
                                    <td align="center" bgcolor="#EFEFEF"><%=task.getPeriod()%>秒</td>
                                    <td align="center" bgcolor="#EFEFEF"><%if(state==-1){%>
										  <font color="#FF0000">任务对象不存在</font>
								        <%}else if(state==ScheduleTask.STATE_WAIT){%><font color="#FF6633">等待中</font><%}else if(state==ScheduleTask.STATE_TIMER){%><font color="#3366CC">计时中</font><%}else if(state==ScheduleTask.STATE_RUN){%><font color="#3366CC">已执行<%=executeTimes%>次</font><%}else if(state==ScheduleTask.STATE_COMPLETE){%>
								        <font color="#006633">执行完毕</font>
							        <%}%></td>
                                  </tr>
								  <%}%>
                                </table>
						        <table width="100%" border="0" cellspacing="1" cellpadding="2">
                                  <tr>
                                    <td align="right"><table width="50" border="0" cellspacing="0" cellpadding="2">
                                        <tr>
                                          <td height="21" nowrap class="btntd" style="cursor: hand"
													onMouseDown="this.className='btntd_mousedown'"
													onMouseUp="this.className='btntd'"
													onMouseOut="this.className='btntd'"
													onselectstart="return false" onClick="stopAll()"><img
													src="../images/icon_btn_stop.gif" width="16" height="16"
													align="absmiddle"> 停止并清除运行中的全部计时任务</td>
                                        </tr>
                                    </table></td>
                                  </tr>
                                </table></td>						
					</tr>

				</table>			
				
                </td>
                <td rowspan="2" bgcolor="#848284" width="1"><img src="images/spacer.gif" width="1" height="1"></td>
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
</form>
<iframe id="hiddenFrame" name="hiddenFrame" width=0 height=0></iframe>
</body>
</html>
