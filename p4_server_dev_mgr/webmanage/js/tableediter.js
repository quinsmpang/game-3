
/*
 * 获取值
 */
function getTableValue(tbodyID,startrow,startcol,endcol){
	 var table = document.getElementById(tbodyID);
	 var rows = table.rows.length;
	 var str = "";
	 for(var i = startrow; i < rows; i++){
	 		if(i > startrow){
	 			str += "_";
	 		}
	 		for(var j = startcol; j <= endcol; j++){
	 			if(j > startcol){
	 				str += "|";
	 			}
	 			str += table.rows[i].cells[j].innerText;
	 		}
	 }
	 //alert(str);
	 return str;
}

/*
 * 插入行
 */
function addTableRow(tbodyID){
	var bodyObj=document.getElementById(tbodyID);
	if(bodyObj==null) return;
	var rowCount = bodyObj.rows.length;
	var cellCount = bodyObj.rows[0].cells.length;
	var newRow = bodyObj.insertRow(rowCount++);
	for(var i=0; i<cellCount; i++){
		 newRow.insertCell(i).innerHTML="&nbsp;";
	}
	return newRow;
}

/*
 * 删除行
 */
function removeTableRow(inputobj){
	if(inputobj==null) return;
	var parentTR = inputobj.parentNode;
	var parentTBODY = parentTR.parentNode;
	parentTBODY.removeChild(parentTR);
}

/*
 * 设置内容
 */
function setTableCellValue(inputobj, content){
  if(inputobj==null) return;
  inputobj.innerHTML = content;
}

/*
 * 双击切换单元格输入框和文本之间的状态
 */
function changeTotext(obj) {
		var ele = document.getElementById("_text_000000000_")
		if(ele == null){
			var tdValue = obj.innerText;
	    obj.innerText = "";
	    var txt = document.createElement("input");
	    txt.type = "text";
	    txt.value = tdValue;
	    txt.id = "_text_000000000_";
	    txt.setAttribute("className","text");
	    txt.attachEvent("ondblclick", function(){changeTotext(event.srcElement.parentNode);});
	    obj.appendChild(txt);
	    txt.select();
		} else 
		if(ele.parentNode==obj){
			obj.innerText = ele.value;
		}
}

/*
document.ondblclick = function() { 
    if (event.srcElement.tagName.toLowerCase() == "td") {
        changeTotext(event.srcElement);
    }
}

document.onmouseup = function() { 
    if (document.getElementById("_text_000000000_") && event.srcElement.id != "_text_000000000_") { 
        var obj = document.getElementById("_text_000000000_").parentElement;
        cancel(obj);
    } 
}
*/