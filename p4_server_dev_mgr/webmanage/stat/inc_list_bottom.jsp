<%@ page contentType="text/html; charset=UTF-8"%>
<table width="95%" border="0" cellspacing="1" cellpadding="2">
<tr> 
  <td width="200" height="27" nowrap>跳到第<input name="page" type="text" id="page" value="<%=xml!=null?xml.optInt("rsPageNO"):1%>" size="4"/>页
    <label>
    <input type="submit" name="Submit" value="跳转" />
    </label></td>
  <td align="center">
	<%if(xml!=null && xml.optInt("rsPageNO")>1){%>
	<a href="javascript:first()">首页</a>
	<%}else{out.print("首页");}%>
	　
	<%if(xml!=null && xml.optInt("rsPageNO")>1){%>
	<a href="javascript:prev()">上一页</a>
	<%}else{out.print("上一页");}%>
	　<font color="#FF0000"><%=xml!=null?xml.optInt("rsPageNO"):1%></font>/<font color="#FF0000"><%=xml!=null?xml.optInt("rsPageTotal"):1%></font>
	<%if(xml!=null && xml.optInt("rsPageNO")<xml.optInt("rsPageTotal")){%>
	<a href="javascript:next()">下一页</a>
	<%}else{out.print("下一页");}%>
	　
	<%if(xml!=null && xml.optInt("rsPageNO")<xml.optInt("rsPageTotal")){%>
	<a href="javascript:last()">末页</a>
	<%}else{out.print("末页");}%>
  </td>
  <td width="200" align="right" nowrap> </td>
</tr>
</table>
<script>
function next(){
document.forms[0].page.value=document.forms[0].page.value*1+1
document.forms[0].action=location.pathname;
document.forms[0].submit();
}
function prev(){
document.forms[0].page.value=document.forms[0].page.value*1-1
document.forms[0].action=location.pathname;
document.forms[0].submit();
}
function first(){
document.forms[0].page.value=1;
document.forms[0].action=location.pathname;
document.forms[0].submit();
}
function last(){
document.forms[0].page.value=<%=xml!=null?xml.optInt("rsPageTotal"):1%>;
document.forms[0].action=location.pathname;
document.forms[0].submit();
}
</script>
