<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="inc_import.jsp"%>

<%@ include file="inc_getuser.jsp"%>
<%
String model="系统设置";
String perm="权限分组";
%>
<%@ include file="inc_checkperm.jsp"%>
<%
	int roleId = ToolFunc.str2int(request.getParameter("roleId"));
	String permStr=request.getParameter("permStr");
	String[] permArr=ToolFunc.splitStr(permStr);
	//String[] actCode = request.getParameterValues("actcode");
	RolePermissionBAC rolePermBAC = new RolePermissionBAC();
	
	ArrayList<Perm> perm_list = new ArrayList<Perm>();
	JSONObject queryobj = rolePermBAC.getJsonObjs("roleId="+ roleId, null);
	//System.out.println("queryobj:"+queryobj);
	if(queryobj != null){
		JSONArray dataarr = queryobj.optJSONArray("list");
		if(dataarr != null){
			for(int i = 0; i < dataarr.length(); i++){
				JSONObject obj = dataarr.optJSONObject(i);
				if(Permission.isExistPermission(obj.optString("moduleid"), obj.optString("permission"))){
					perm_list.add(new Perm(obj.optString("moduleid"), obj.optString("permission")));					
				}
			}
		}	
	}
	
	FormXML xml=new FormXML();
	xml.setAction(FormXML.ACTION_INSERT);
	
	RoleBAC roleBAC = new RoleBAC();
	JSONObject roleRs = roleBAC.getJsonObj("id=" + roleId);
	
	StringBuffer noteSb = new StringBuffer("组"+roleRs.getString("rolename")+"：");
	for(int i=0;permArr!=null && i<permArr.length-1;i+=2) {	
		boolean have = false;
		for(int j = 0; j < perm_list.size(); j++){
			Perm inst = perm_list.get(j);
			if(inst.module.equals(permArr[i]) && inst.permission.equals(permArr[i+1])){
				perm_list.remove(j);
				have = true;
				break;
			}
		}
		if(!have){
			xml.add("roleId",roleId);
			xml.add("ModuleId",permArr[i]);
			xml.add("Permission",permArr[i+1]);
			rolePermBAC.save(xml);
			xml.clear();
			noteSb.append("增加权限 "+permArr[i]+"/"+permArr[i+1]+" ");
			//System.out.println("add:"+permArr[i]+" "+permArr[i+1]);
		}
	}
	for(int i = 0; i < perm_list.size(); i++){
		Perm inst = perm_list.get(i);
		rolePermBAC.del("roleId="+ roleId +" and moduleid='"+inst.module+"' and permission='"+inst.permission+"'");
		noteSb.append("删除权限 "+inst.module+"/"+inst.permission+" ");
		//System.out.println("del:"+inst.module+" "+inst.permission);
	}
	String opusername = (String)pageContext.getSession().getAttribute("username");
	TBLogParameter  parameter=TBLogParameter.getInstance();
	parameter.addParameter("note", noteSb.toString());
	LogBAC.addLog(opusername,"权限分组",parameter.toString(),IPAddressUtil.getIp(request));
%>
<script>
alert("权限设置成功");
parent.location.replace("role_list.jsp");
</script>