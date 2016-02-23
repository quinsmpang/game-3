<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="用户管理";
%>
<%@ include file="inc_checkperm.jsp"%>

<script src="../js/common.js"></script>

<%
	int userId = ToolFunc.str2int(request.getParameter("userId"));
	int[] roleIds = ToolFunc.strArr2IntArr(request.getParameterValues("userroles"));
	
	UserRoleBAC userRoleBAC = new UserRoleBAC();
	
	ArrayList<String> rolelist = new ArrayList<String>();
	
	JSONObject uerroleObj = userRoleBAC.getJsonObjs("userId="+ userId, null);
	if(uerroleObj != null){
		JSONArray dataarr = uerroleObj.optJSONArray("list");
		if(dataarr != null){
			for(int i = 0; i < dataarr.length(); i++){
				JSONObject obj = dataarr.optJSONObject(i);
				rolelist.add(obj.optString("roleid"));
			}
		}
	}
	
	UserBAC userBAC = new UserBAC();
	JSONObject userRs = userBAC.getJsonObj("id=" + userId);
	
	RoleBAC roleBAC = new RoleBAC();
	
	FormXML xml=new FormXML();
	xml.setAction(FormXML.ACTION_INSERT);
	
	StringBuffer noteSb = new StringBuffer("用户 "+userRs.getString("username")+"：");
	for(int i=0;roleIds!=null && i<roleIds.length;i++)
	{
		if(rolelist.contains(String.valueOf(roleIds[i]))){
			rolelist.remove(String.valueOf(roleIds[i]));
		} else {
			xml.add("userId",userId);
			xml.add("roleId",roleIds[i]);
			userRoleBAC.save(xml);
			xml.clear();
			noteSb.append("加入组 "+ roleBAC.getJsonObj("id=" + roleIds[i]).optString("rolename")+" ");
		}
	}
	
	for(int i = 0; i < rolelist.size(); i++){
		userRoleBAC.del("userId="+ userId+" and roleid="+rolelist.get(i));
		noteSb.append("移出组 "+roleBAC.getJsonObj("id=" + rolelist.get(i)).optString("rolename")+" ");
	}
	
	String opusername = (String)pageContext.getSession().getAttribute("username");
	TBLogParameter  parameter=TBLogParameter.getInstance();
	parameter.addParameter("note", noteSb.toString());
	LogBAC.addLog(opusername,"权限分组",parameter.toString(),IPAddressUtil.getIp(request));
%>
<script>
alert("用户组设置成功");
parent.location.replace("user_list.jsp");
</script>