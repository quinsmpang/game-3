<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
	String model = "市场统计";
	String perm = "渠道注册比例分析";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
	String startDate = Tools.strNull(request.getParameter("startDate"));
	String endDate = Tools.strNull(request.getParameter("endDate"));
	String statBtn = request.getParameter("statBtn");
			
	JSONArray returnarr = null;
	if(statBtn != null)
	{
		returnarr = MarketStatBAC.getInstance().statChannelReg(request, response);
	}
%>

<script>
var allValue=new Object();
allValue.startDate="<%=startDate%>";
allValue.endDate="<%=endDate%>";
</script>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="../css/style1.css" type="text/css">
		<SCRIPT  SRC="../Charts/FusionCharts.js"></SCRIPT>
		<script src="../js/common.js"></script>
		<script language="javascript" type="text/javascript" src="../My97DatePicker/WdatePicker.js"></script>
		<script language="javascript" type="text/javascript" src="../js/jquery-1.9.1.min.js"></script>
		<script> 
		function stat(){
			var theForm = document.forms[0];
			wait();
			theForm.submit();
		}
	 </script>
	</head>
	<body bgcolor="#EFEFEF">
		<form name="form1" method="post" action="" onSubmit="return stat()">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td valign="top">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="1" valign="bottom">
											<table width="100%" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td>
														<img src="../images/tab_left.gif" width="6" height="22">
													</td>
													<td nowrap background="../images/tab_midbak.gif"><%=perm%></td>
													<td>
														<img src="../images/tab_right.gif" width="6" height="22">
													</td>
												</tr>
											</table>
										</td>
										<td valign="bottom">
											<table width="100%" border="0" cellspacing="0" cellpadding="0" height="22">
												<tr>
													<td></td>
													<td width=1></td>
												</tr>
												<tr>
													<td bgcolor="#FFFFFF" colspan="2" height=1></td>
												</tr>
												<tr>
													<td height=3></td>
													<td bgcolor="#848284" height=3 width="1"></td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td rowspan="2" bgcolor="#FFFFFF" width="1">
											<img src="../images/spacer.gif" width="1" height="1">
										</td>
										<td valign="top" align="center">
											<table width="99%" border="0" cellspacing="1" cellpadding="2">
												<tr>
												  <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                    <tr>
                                                      <td><table width="100%" border="0" cellspacing="1" cellpadding="2">
                                                          <tr>
                                                            <td> 
起始日期:<input type="text" name="startDate" id="startDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" style="width: 120px;"/>
结束日期:<input type="text" name="endDate" id="endDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" style="width: 120px;"/>
<input name="statBtn" type="submit" id="statBtn" value="查询">
                                                              </td>
                                                          </tr>
                                                        </table>
                                                          <table width="100%" border="0" cellspacing="1" cellpadding="2">
                                                            <tr>
                                                              <td><table width="100%" border="0" cellpadding="2" cellspacing="1" bgcolor="#000000">
                                                                  <tr>
                                                                    <td align="left" class="listtopbgc" colspan="3">渠道注册比例分析</td>
                                                                  </tr>
                                                                  <tr>
                                                                    <td align="left" class="whitebgc"  >
                                                                      <%=returnarr!=null?returnarr.optString(0):"&nbsp;" %>
                                                                    </td>
                                                                    <td align="left" class="whitebgc"  >
                                                                      <%=returnarr!=null?returnarr.optString(1):"&nbsp;" %>
                                                                    </td>
                                                                  </tr>
                                                                  <tr>
                                                                    <td colspan="2"   align="left" class="whitebgc"  >&nbsp;</td>
                                                                  </tr>
                                                              </table></td>
                                                            </tr>
                                                            <tr>
                                                              <td>&nbsp;</td>
                                                            </tr>
                                                            <tr>
                                                              <td>&nbsp;</td>
                                                            </tr>
                                                        </table></td>
                                                    </tr>
                                                  </table></td>
												</tr>
											</table>
										</td>
										<td rowspan="2" bgcolor="#848284" width="1">
											<img src="../images/spacer.gif" width="1" height="1">
										</td>
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
<iframe name=hiddenFrame width=0 height=0></iframe>
<script>
autoChoose(allValue);
</script>
</body>

</html>
