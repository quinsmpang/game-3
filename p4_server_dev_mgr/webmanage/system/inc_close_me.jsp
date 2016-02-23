<%@ page contentType="text/html; charset=UTF-8"%>
<script src="../js/common.js"></script>
<script>
alert("你没有权限");
//alert(parent.frames[parent.frames.length-1].name);

if(parent.frames[parent.frames.length-1]!=null && parent.frames[parent.frames.length-1].name=="hiddenFrame")
{
self.location.replace("");
}
else if(opener!=null)
{
	self.close();
}
else
{
history.back();
}
wait_end(parent);
</script>
