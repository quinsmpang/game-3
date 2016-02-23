<%@ page contentType="text/html; charset=UTF-8"%>
<table width="100%" border="0" cellspacing="1" cellpadding="2">
<tr>
  <td nowrap>记录数:<font color="#FF0000"><%=xml!=null?xml.optInt("totalRecord"):0%></font></td>
  <td align="right"> <select name="colname" class="input1" id="colname" onchange="changeOperator(this)">
    <%
	int nSelected = 0;
	for(int i=0;i<theWhereColumn.length;i++){%>
	  <option value="<%=theWhereColumn[i].strFieldName%>" <%if(colname.equals(theWhereColumn[i].strFieldName)){out.print("selected"); nSelected=i;}%>><%=theWhereColumn[i].strDisplayName%></option>
	  <%}%>
	</select>
	<select name="operator" class="input1" id="operator">
     <%for(int i=0;i<theWhereColumn[nSelected].strOperator.length;i++){%>
	  <option value="<%=theWhereColumn[nSelected].strOperator[i]%>" <%if(operator.equals(theWhereColumn[nSelected].strOperator[i])){out.print("selected");}%>><%=theWhereColumn[nSelected].strOperator[i]%></option>
	  <%}%>	  
	</select> <input name="colvalue" type="text" class="input1" id="colvalue" value="<%=colvalue%>" size="15">
<input type=image src="../images/icon_search16.gif" align="absmiddle"  alt="Find"> <img src="../images/icon_showall.gif" alt="Show all" align="absmiddle" style="cursor:hand" onclick="document.forms[0].colvalue.value='';document.forms[0].submit()"></td>
</tr>
</table><table width="100%" border="0" cellspacing="1" cellpadding="2">
<tr> 
  <td nowrap>排序：
	<select name="showorder" id="showorder" onchange="document.forms[0].submit()">
	 <%for(int i=0;i<theOrderColumn.length;i++){%>
	  <option value="<%=theOrderColumn[i].strFieldName%>" <%if(showorder.equals(theOrderColumn[i].strFieldName))out.print("selected");%>><%=theOrderColumn[i].strDisplayName%></option>
	  <%}%>	  
	</select>
	<input name="ordertype" type="radio" value="DESC" <%if(ordertype.equals("DESC"))out.print("checked");%> onclick="document.forms[0].submit()">
	逆序
<input type="radio" name="ordertype" value="ASC" <%if(ordertype.equals("ASC"))out.print("checked");%> onclick="document.forms[0].submit()">
	顺序</td>
  <td align="right">每页行数
	<input name="rpp" type="text" class="input4right" id="rpp" value="<%=rpp%>" size="2">
	
	<input name="Button" type="button" class="btn1" value="刷新" onclick="document.forms[0].submit()"> 
  </td>
</tr>
</table>
