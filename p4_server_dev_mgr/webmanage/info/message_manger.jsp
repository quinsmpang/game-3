<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>
<%
	String model = "客服管理";
	String perm = "消息管理";
%>
<%@ include file="../system/inc_checkperm.jsp"%>
<%
	String[] picked = request.getParameterValues("server");
	int msg_type = Tools.str2int(request.getParameter("msg_type"));
	String push_expDate = request.getParameter("push_expDate");
	String push_content = request.getParameter("push_content");
	String title= request.getParameter("title");
	String channel = request.getParameter("channel");
	int repeat = Tools.str2int(request.getParameter("repeat")); 
	int pid = Tools.str2int(request.getParameter("pid"));
	String pName = request.getParameter("pName");
	int sid = Tools.str2int(request.getParameter("serverId"));
	int frequency =  Tools.str2int(request.getParameter("frequency"));
	int cls = Tools.str2int(request.getParameter("cls"));
	if(cls == 1){
		MessageManagerBac.getInstance().clernAllMsgTimer();
	}
	if(picked!=null){
		MessageManagerBac.getInstance().send(request,picked,msg_type,push_expDate,push_content,title,channel,repeat,pid,frequency);
	}
%>

 

<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link href="../css/style1.css" rel="stylesheet" type="text/css">
		<link rel="stylesheet" href="../kindeditor/themes/default/default.css" />
		<style type="text/css">
	 		.ke-icon-example1 {
				background-image: url(../kindeditor/themes/default/default.png);
				background-position: 0px -1020px;
				width: 16px;
				height: 16px;
				}
		 
			.cls {
				border : 1px solid #A0A0A0;
				margin : 10px;
				padding : 10px;
				cursor: pointer;
			}
			.on {
				border : 1px solid red;
				background-color : #F0F0F0;
			}
		</style>
		<script charset="utf-8" src="../kindeditor/kindeditor-min.js"></script>
		<script charset="utf-8" src="../kindeditor/lang/zh_CN.js"></script>
		<script src="../js/common.js"></script>
		<script language="javascript" type="text/javascript" src="../My97DatePicker/WdatePicker.js"></script>
 		<script>
 		function $(id){
 			return document.getElementById(id);
 		}
 		
 		function changeMsgType(obj){
 			var value=obj.value;
 			var i=parseInt(value);
 			switch(i){
 				case 0:
	 				break;
 				case 1:
 					$("titleid").style.display = "none";  
 					$("repeatid").style.display = "none"; 
 					$("channelid").style.display = "none"; 
 					$("serverid").style.display = ""; 
 					$("expdateid").style.display = ""; 
 					$("frequencyid").style.display = ""; 
					$("extendid").style.display = "none"; 
					$("isonlyonlineid").style.display = "none";
	 				break;
				case 2:
					$("titleid").style.display = "";  
					$("repeatid").style.display = ""; 
					$("channelid").style.display = "none"; 
					$("frequencyid").style.display = "none"; 
					$("serverid").style.display = ""; 
					$("expdateid").style.display = "";
				 	$("extendid").style.display = "none"; 
				 	$("isonlyonlineid").style.display = "none";
					
					break;
				case 3:
					$("titleid").style.display = "";  
					$("repeatid").style.display = "none"; 
					$("channelid").style.display = "none"; 
					$("frequencyid").style.display = "none"; 
					$("serverid").style.display = ""; 
					$("expdateid").style.display = ""; 
					$("extendid").style.display = ""; 
					$("isonlyonlineid").style.display = "";
					break;
				case 4:
					$("titleid").style.display = "none";  
					$("repeatid").style.display = "none"; 
					$("channelid").style.display = "none"; 
					$("expdateid").style.display = ""; 
					$("frequencyid").style.display = ""; 
					$("serverid").style.display = "";
					$("extendid").style.display = "none"; 
					$("isonlyonlineid").style.display = "none";
					break;
				case 5:
					$("titleid").style.display = "none";  
					$("repeatid").style.display = "none"; 
					$("expdateid").style.display = "none"; 
					$("serverid").style.display = ""; 
					$("channelid").style.display = "";
					$("extendid").style.display = "none";  
					$("isonlyonlineid").style.display = "none";
					break;
				case 6:
					$("titleid").style.display = "";  
					$("repeatid").style.display = "none"; 
					$("channelid").style.display = "none"; 
					$("frequencyid").style.display = "none"; 
					$("serverid").style.display = ""; 
					$("expdateid").style.display = ""; 
					$("extendid").style.display = "";  
					$("isonlyonlineid").style.display = "none";
 					break;
 				}
 			}
 			function clearMsg(){
 			var adds="message_manger.jsp?cls=1";
 			window.location.href=adds;
 		}
 		
 		String.prototype.replaceAll = function (str1,str2){
		  	return this.replace(new RegExp(str1,"gm"),str2);
		}
 
 		function coverHtml(msg){
			msg=msg.replaceAll("<span style=\"color:", "<");
			msg=msg.replaceAll("<p>", "");
	    	msg=msg.replaceAll("</p>", "|");
			msg=msg.replaceAll("</span>", "</>");
			msg=msg.replaceAll("<#", "<0xFF");
			msg=msg.replaceAll(";\">", ">");
			msg=msg.replaceAll("\t", "  ");
			msg=msg.replaceAll("</br>", "|");
			msg=msg.replaceAll("<br>", "|");
			msg=msg.replaceAll("<br />", "|");
			msg=msg.replaceAll("&nbsp;", " ");
			msg=msg.substring(0,msg.length-1);
 
 			return msg
 		}
 		
			var editor;
			KindEditor.ready(function(K) {
				editor = K.create('textarea[name="push_content"]', {
					resizeType : 1,
					pasteType : 1,		
					allowPreviewEmoticons : false,
					allowImageUpload : false,
					items : ['forecolor'] 
					
				});
				
			});
			
			function serverBoxFun(obj)
			{				
				sArr=document.getElementsByName("server");
				for(i=0;sArr!=null && i<sArr.length;i++) 
				{
					sArr[i].checked=obj.checked;
				}
			}
			function onCheckedChanged(obj){
				if(obj.checked==false&&$("serverBox").checked==true)
					$("serverBox").checked=false;
				else{
					sArr=document.getElementsByName("server");
					for(i=0;i<sArr.length;i++) 
						if(!sArr[i].checked)
							return ;
							$("serverBox").checked=true;
				}
				
			}
			function verifyCheckBox(elementName){
			
				sArr=document.getElementsByName(elementName);
					for(i=0;i<sArr!=null&&sArr.length;i++) 
						if(sArr[i].checked) 
							return true;
						return false;
						 
			}
			
			function trim(objValue){
			 return objValue.replace('/(^/s*)|(/s*$)/g',""); 
			}
			
		 
			function check(){
				var value = $("msg_type").value;
				if(0==value||''==value){
					alert('请选择消息种类');
					return false;
				}
				$("push_content").value=editor.html();
				switch(parseInt(value)){
					case 1:
						if($("push_content").value.length<=0){
							alert('请输入要发送的消息');
							return false;
						}
						if(!verifyCheckBox('server')){
							alert('请选择服务器');
							return false;
						}
						return true;
					case 2:
						var repeat= trim($("repeat").value);
						if(trim($("push_content").value).length<=0){
							alert('请输入要发送的消息');
							return false;
						}
						if(!verifyCheckBox('server')){
							alert('请选择服务器');
							return false;
						}
						
						if(trim($("title").value).length<=0){
							alert('请输入标题');
							return false;
						}
							return true;
					case 3:
						 
						if(!verifyCheckBox('server')){
							alert('请选择服务器');
							return false;
						}
						if(trim($("title").value).length<=0){
							alert('请输入标题');
							return false;
						}
						if(trim($("push_content").value).length<=0){
							alert('请输入要发送的消息');
							return false;
						}
							return true;
					case 4:
						if(!verifyCheckBox('server')){
							alert('请选择服务器');
							return false;
						}
					 
						if(trim($("push_content").value).length<=0){
							alert('请输入要发送的消息');
							return false;
						}
							return true;
					case 5:
						if(!verifyCheckBox('server')){
							alert('请选择服务器');
							return false;
						}
						 
						 
						if(trim($("push_content").value).length<=0){
							alert('请输入要发送的消息');
							return false;
						}
							return true;
							
						case 6:
						 
						if(!verifyCheckBox('server')){
							alert('请选择服务器');
							return false;
						}
						if(trim($("title").value).length<=0){
							alert('请输入标题');
							return false;
						}
						if(trim($("push_content").value).length<=0){
							alert('请输入要发送的消息');
							return false;
						}
							return true;
				}
			}
			
		  	function submits(){
			  	if(check()){
				
				  	theForm = document.forms[0];
					wait();
					theForm.submit();
			  	}
			}
		
		</script>
	
		
 

	</head>
	<body bgcolor="#EFEFEF">
		<form name="form1" method="post" action="message_manger.jsp">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="1" valign="bottom">
												<table width="100%" border="0" cellspacing="0"
													cellpadding="0">
													<tr>
														<td>
															<img height="22" width="6" src="../images/tab_left.gif">
														</td>
														<td nowrap background="../images/tab_midbak.gif"><%=perm%><br></td>
														<td>
															<img height="22" width="6" src="../images/tab_right.gif">
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
											<td rowspan="2" bgcolor="#FFFFFF" width="1">
												<img height="1" width="1" src="../images/spacer.gif">
											</td>
											<td valign="top" align="center">
												<table width="95%" border="0" cellspacing="1" cellpadding="2">
													<tr>
														<td  >
															<table width="95%" border="0" cellspacing="1"cellpadding="2">
																<tr>
																	<td>
																		 
																		 
																	</td>
																</tr>
															</table>
															</td>
													</tr>
													
													
													<tr  >
															<td valign="top" width="100%" align="center" >
																 <table width="100%" border="0" cellpadding="2" cellspacing="1"  >
																<tr class="listtopbgc"  >
																	<td align="center" nowrap>消息管理</td>
																</tr>
																<tr  >
																<td width="100%" >
																	<table   cellpadding="2" cellspacing="1" class="cls" width="100%" >
																	<tr class="class"  height="50"> 
													 			 	<td align="right" nowrap >
													 			 		消息类型													 			 	</td>
													 			 	<td>
													 			 		<select name="msg_type" onChange="changeMsgType(this)" id="msg_type" style="height: 30px;">
													 			 			<option value="0">消息类型</option>
													 			 			<option value="1">应用推送</option>
													 			 			<option value="2">公告消息</option>
													 			 			<option value="3">通知消息</option>
													 			 			<option value="4">系统消息</option>
													 			 			<option value="6"  <%if(pid>0){%>selected="selected"<%} %> >私人提示</option>
<!--													 			 			<option value="5">游戏推送</option>-->
													 			 		</select>
													 			 	</td>
																 	
																 	</tr>
																 	
																 	<tr id="frequencyid" class="cls" style="display:<%if(pid>0){%>none;<%}else{%>;<%}%>"   height="50">
													 			 	<td align="right" nowrap>
													 			 		相隔时间													 			 	</td>
													 			 	<td class="class">
													 			 		<input type="text" name="frequency" id="frequency" style="height: 30px;width: 50px;"><font color="red">*</font>分钟 
													 			 	</td>
																 	</tr>
																 	
																 	<tr id="player" class="cls"    style="display:<%if(pid<=0){%>none;<%}else{%>;<%}%>" height="50">
													 			 	<td align="right">
													 			 		玩家													 			 	</td>
													 			 	<input type="hidden" name="pName" id="pName" value="<%=pName %>"/>
													 			 	<td class="class">
													 			 		<input type="radio" name="pid" id="pid" checked="checked"  value="<%=pid%>" ><%=pName %> 
													 			 	</td>
																 	</tr>
																 	
															 	 	<tr id="expdateid" class="cls" height="50">
													 			 	<td align="right">
													 			 		到期时间													 			 	</td>
													 			 	<td class="class">
													 			 		<input type="text" name="push_expDate" id="expirateDate"  onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" style="height: 30px;width: 200px;"/><font color="red">*</font>不填代表永不过期 
													 			 	</td>
																 	</tr>	
																 	
																 	
															 	 	<tr id="repeatid" class="cls"  style="display:<%if(pid<=0){%>;<%}else{%>none;<%}%>" height="50">
													 			 	<td align="right">
													 			 		是否重复													 			 	</td>
													 			 	<td class="class">
													 			 	<input type="radio" name="repeat" id="repeat" checked="checked" value="0"  > 否
													 			 	<input type="radio" name="repeat" id="repeat" value="1"  > 是
													 			 	</td>
																 	</tr>	
																 	
													 	 	 		<tr    id="serverid"  class="cls" height="50">
													 			 	<td align="right">
													 			 		服务器													 			 	</td>
													 			 	<td class="class">
																			<input type="checkbox" name="serverBox" id="serverBox" onClick="serverBoxFun(this)" value="0" >全选<br/>
																			<% 
																			DBPsRs serverRs = DBPool.getInst().pQueryS(ServerBAC.tab_server, "usestate=1");
																			while(serverRs.next()) { 
																				int id=serverRs.getInt("id");
																			%> 
																			<input type="checkbox" name="server"  value="<%=id%>"   <%if(sid==id){%>checked="checked"<%} %> onChange="onCheckedChanged(this);" ><%=serverRs.getString("name")%>
																			<% 
																			} 
																			%> 
													 			 	</td>
																 	</tr>
																 	
													 	 	 		<tr id="channelid"  class="cls" height="50" style="display: none;">
													 			 	<td align="right">
													 			 		频道													 			 	</td>
													 			 	<td class="class">
													 			 	<select name="channel" id="channel" style="height: 30px;width: 100px;display: none;">
													 			 		<option value="0">世界频道</option>
													 			 		<option value="1">帮派频道</option>
													 			 		<option value="2">私语频道</option>
													 			 		<option value="3">系统频道</option>
													 			 	</select>
													 			 	</td>
																 	</tr>
																 	
															 		<tr id="titleid" class="cls" height="50">
														 			 	<td align="right">
														 			 		标题														 			 	</td>
														 			 	<td class="class">
														 			 		<input type="text" name="title" id="title" size="100"  style="height: 30px;"/> 
														 			 	</td>
																 	</tr>	
																 	<tr  id="contentid" class="class"  > 
													 			 	<td align="right">
													 			 		消息内容													 			 	</td>
													 			 	<td>
													 			 		<textarea name="push_content"  id="push_content" style="width:800px;height:150px;"></textarea>
													 			 	</td>
																 	
																 	</tr>
																 <!--  -->
																 
																 
																  	<tr  id="extendid" class="class"  > 
													 			 	<td align="right">
													 			 		扩展													 			 	</td>
													 			 	<td>
													 			 		 板块编号:<input type="text" id="fid" name="fid" style="width: 30px;" > 主题编号:<input type="text" id="tid" name="tid" style="width: 30px;" > URL<input type="text" id="url" name="url" style="width: 400px;" > 
													 			 	</td>
																 	</tr>
																 
																 
																 
																  	<tr  id="isonlyonlineid" class="class"  > 
													 			 	<td align="right" nowrap>
													 			 		是否只发在线用户													 			 	</td>
													 			 	<td>
													 			 		<input type="radio" name="isonlyonline" value="0" checked="checked">在线
													 			 		<input type="radio" name="isonlyonline" value="1">所有
													 			 	</td>
																 	</tr>
																 
																 <!--  -->
																 
																 	<tr    > 
																 		<td  align="right">																 		</td>
																 		<td   >
																 		<input type="button" name="stat" id="stat" onClick="submits();" value="  提交   "  style="height: 30px;width: 100px;"/>
																 		</td>
																 	</tr>
																	
																  </table>
																  </td>
																	 
																</tr>
																 
																 <tr  >
																<td width="100%" >
																	<table   cellpadding="2" cellspacing="1" class="cls" width="100%" >
																	<tr class="class"  height="50"> 
													 			 		<td >
													 			 			<input type="button" value="清除所有消息定时器任务"  name="clear" id="clear" onClick="clearMsg()">
													 			 		</td>
											 			 		 	</tr>
																	</table>
																	</td>
																	 
																</tr>
																 
															</table>
														 </td>
													</tr>

												</table>
 
											</td>
											<td rowspan="2" bgcolor="#848284" width="1">
												<img src="../images/spacer.gif" width="1" height="1">
											</td>
										</tr>
										<tr>
											<td bgcolor="#848284" height="1" colspan="2"></td>
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
	</body>
</html>
