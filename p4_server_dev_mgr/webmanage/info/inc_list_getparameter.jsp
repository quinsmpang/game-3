<%@ page contentType="text/html; charset=UTF-8"%>
<script>
var operator=new Array(<%for(int i=0;i<theWhereColumn.length;i++){if(i>0)out.print(",");%>new Array(<%for(int j=0;j<theWhereColumn[i].strOperator.length;j++){if(j>0)out.print(",");%>"<%=theWhereColumn[i].strOperator[j]%>"<%}%>)<%}%>);
function changeOperator(selectObj){
var index=selectObj.selectedIndex;
var operatorObj=document.forms[0].operator;
for(var i=operatorObj.length-1;i>=0;i--){
	operatorObj.remove(i);
	}	
for(var i=0;i<operator[index].length;i++){
 var optionObj=document.createElement("option");
 optionObj.text=operator[index][i];
 optionObj.value=operator[index][i];
 operatorObj.add(optionObj);
 }
}
</script>
<%String pagenum=request.getParameter("page");
if(pagenum==null || pagenum.equals("")){
pagenum="1";
}
String ordertype=request.getParameter("ordertype");
if(ordertype==null || ordertype.equals("")){
ordertype="DESC";
}
String showorder=request.getParameter("showorder");
if(showorder==null || showorder.equals("")){
showorder=theOrderColumn[0].strFieldName;
}
String rpp=request.getParameter("rpp");
if(rpp==null || rpp.equals("")){
rpp="10";
}
String colname=request.getParameter("colname");
if(colname==null){colname="";}
String operator=request.getParameter("operator");
if(operator==null){operator="";}
String colvalue=request.getParameter("colvalue");
if(colvalue==null){colvalue="";}
colvalue=Tools.replace(colvalue,"\"","\\\"");
%>