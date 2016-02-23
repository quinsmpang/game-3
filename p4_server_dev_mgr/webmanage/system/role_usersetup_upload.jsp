<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="权限分组";
%>
<%@ include file="inc_checkperm.jsp"%>

<script src="../js/common.js"></script>

<%
	int roleId = ToolFunc.str2int(request.getParameter("roleId"));
	int[] userIds = ToolFunc.strArr2IntArr(request.getParameterValues("rolestaff"));
	UserRoleBAC userRoleBAC = new UserRoleBAC();
	
	ArrayList<String> uesrlist = new ArrayList<String>();
	JSONObject uerroleObj = userRoleBAC.getJsonObjs("roleId="+ roleId, null);
	if(uerroleObj != null){
		JSONArray dataarr = uerroleObj.optJSONArray("list");
		if(dataarr != null){
			for(int i = 0; i < dataarr.length(); i++){
				JSONObject obj = dataarr.optJSONObject(i);
				uesrlist.add(obj.optString("userid"));
			}
		}
	}
	FormXML xml=new FormXML();
	xml.setAction(FormXML.ACTION_INSERT);
	
	RoleBAC roleBAC = new RoleBAC();
	JSONObject roleRs = roleBAC.getJsonObj("id=" + roleId);
	
	UserBAC userBAC = new UserBAC();
	
	StringBuffer noteSb = new StringBuffer("组 "+roleRs.getString("rolename")+"：");
	for(int i=0;userIds!=null && i<userIds.length;i++)
	{
		if(uesrlist.contains(String.valueOf(userIds[i]))){
			uesrlist.remove(String.valueOf(userIds[i]));
		} else {
			xml.add("roleId",roleId);
			xml.add("userId",userIds[i]);
			userRoleBAC.save(xml);
			xml.clear();
			noteSb.append("增加用户 "+ userBAC.getJsonObj("id=" + userIds[i]).optString("username")+" ");
		}
	}
	
	for(int i = 0; i < uesrlist.size(); i++){
		userRoleBAC.del("roleId="+ roleId+" and userid="+uesrlist.get(i));
		noteSb.append("删除用户 "+userBAC.getJsonObj("id=" + uesrlist.get(i)).optString("username")+" ");
	}
	String opusername = (String)pageContext.getSession().getAttribute("username");
	TBLogParameter  parameter=TBLogParameter.getInstance();
	parameter.addParameter("note", noteSb.toString());
	LogBAC.addLog(opusername,"权限分组",parameter.toString(),IPAddressUtil.getIp(request));
%>
<script>
alert("组用户设置成功");
parent.location.replace("role_list.jsp");
</script>

