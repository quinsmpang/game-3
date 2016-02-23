<%@ page contentType="text/html; charset=UTF-8"%>
<table width="95%" border="0" cellspacing="1" cellpadding="2">
<tr> 
  <td width="100" height="27" nowrap>跳到第 
	<select name="select" onChange="document.forms[0].page.value=this.options[this.selectedIndex].value;document.forms[0].submit()">
	  <%for(int i=1;xml!=null && i<=xml.rsPageTotal;i++){%>
	  <option value="<%=i%>" <%if(pagenum.equals(String.valueOf(i)))out.print("selected");%>><%=i%></option>
	  <%}%>
	</select>页
	
	<input name="page" type="hidden" id="page" value="<%=xml!=null?xml.rsPageNO:1%>"></td>
  <td align="center">
	<%if(xml!=null && xml.rsPageNO>1){%>
	<a href="javascript:first()">首页</a>
	<%}else{out.print("首页");}%>
	　
	<%if(xml!=null && xml.rsPageNO>1){%>
	<a href="javascript:prev()">上一页</a>
	<%}else{out.print("上一页");}%>
	<font color="#FF0000"><%=xml!=null?xml.rsPageNO:1%></font>/<font color="#FF0000"><%=xml!=null?xml.rsPageTotal:1%></font>
	<%if(xml!=null && xml.rsPageNO<xml.rsPageTotal){%>
	<a href="javascript:next()">下一页</a>
	<%}else{out.print("下一页");}%>
	　
	<%if(xml!=null && xml.rsPageNO<xml.rsPageTotal){%>
	<a href="javascript:last()">末页</a>
	<%}else{out.print("末页");}%>
  </td>
  <td width="100" align="right" nowrap>&nbsp;</td>
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
document.forms[0].page.value=<%=xml!=null?xml.rsPageTotal:1%>;
document.forms[0].action=location.pathname;
document.forms[0].submit();
}
</script>
