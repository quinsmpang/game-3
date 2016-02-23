function createExcel(excelArea,excelTitle,resultTb,excelOutput) 
{
resultTb.border=1;
createExcelTitle2(excelArea,excelTitle,resultTb);

excelOutput.value=excelArea.innerHTML;

//alert(excelOutput.value)
document.forms[0].target="_blank";
document.forms[0].action="../common/html2Excel.jsp";
document.forms[0].submit();
document.forms[0].target="";
document.forms[0].action="";
resultTb.border=0;
deleteExcelTitle2(excelArea,excelTitle,resultTb);
}

function createExcelTitle(excelArea,excelTitle,resultTb)
{
var maxCellLength=0;
for(var i=0;i<resultTb.rows.length;i++)
{
if(maxCellLength<resultTb.rows[i].cells.length)maxCellLength=resultTb.rows[i].cells.length;
}
var tb=document.createElement("TABLE");
tb.width="100%";
var tr=tb.insertRow();
var td=tr.insertCell();
td.colSpan=maxCellLength;
td.align="center";
td.innerHTML="<strong>"+excelTitle+"</strong>";
tr=tb.insertRow();
for(var i=0;i<maxCellLength;i++)
{
tr.insertCell();
}
excelArea.innerHTML=tb.outerHTML+excelArea.innerHTML;
}

function deleteExcelTitle(excelArea,excelTitle,resultTb)
{
	excelArea.children.tags("TABLE").item(0).outerHTML="";
}

function createExcelTitle2(excelArea,excelTitle,resultTb)
{
var tb=excelArea.children.item(0);
var maxCellLength=0;
for(var i=0;i<tb.rows.length;i++)
{
if(maxCellLength<tb.rows[i].cells.length)maxCellLength=tb.rows[i].cells.length;
}
var tr=tb.insertRow(0);
var td=tr.insertCell();
td.colSpan=maxCellLength;
td.align="center";
td.innerHTML="<strong>"+excelTitle+"</strong>";
}
function deleteExcelTitle2(excelArea,excelTitle,resultTb)
{
	excelArea.children.tags("TABLE").item(0).deleteRow(0);
}




