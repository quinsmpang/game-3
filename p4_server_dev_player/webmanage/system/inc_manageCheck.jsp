<%@ page contentType="text/html; charset=UTF-8"%>
<%if(user==null || !User.isManager(user.getUserId())){%>
<script>
alert("你没有管理员权限");
if(opener!=null){
	self.close();
}else{
history.back();
}
</script>
<%
return;	
}
%>