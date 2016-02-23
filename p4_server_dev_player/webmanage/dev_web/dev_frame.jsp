<%@ page contentType="text/html; charset=UTF-8"%>

<frameset rows="40,*" border= "0" frameborder= "1" framespacing= "0" bordercolor= "#FFFFFF">
	<frame noresize="noresize" src="dev_frame_top.jsp" name="frame_top" scrolling="no">
   	
	<frameset cols="200,*">
		<frame noresize="noresize" src="dev_frame_dir.jsp" name="frame_dir">
	   
		<frameset rows="*,150">
		<frame noresize="noresize" src="frame-res/player.jsp" name="frame_func">
		<frame noresize="noresize" src="dev_request.jsp" name="frame_result">
		</frameset>
	</frameset>
   
   	<noframes>
	<body>您的浏览器无法处理框架！</body>
	</noframes>
</frameset>