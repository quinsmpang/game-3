var strSelectValue="";
var nTotalPage = 0;
var nCurrPage = 1;

function listtop(obj,acttype)
{
	switch(acttype){
	 case "down":
	   obj.className="listTopCellDown";
	   break;
	 case "up":
	   obj.className="listTopCell";
	   break;
	}
}

function listcontent(obj,acttype)
{
	switch(acttype){
	  case "over":
		obj.className="listbgc_over";
		break;
	  case "out":
		obj.className="";
		break;
	}
}

function refresh()
{
	var nFormNo = 0;
	if(arguments.length >= 1) nFormNo = arguments[0];

	document.forms(nFormNo).submit();
}

function gotoPage(nPage)
{
	var theForm = document.forms(0);
	if(nPage == nCurrPage) return;
	if(nPage <= 0 || nPage > nTotalPage)
	{
		showDialog("请输入1～"+nTotalPage+"之间的页码！");
		return;
	}
	theForm.page.value = nPage;
	theForm.submit();
}

function getSelectedItems(n, ch)
{
	var nFormNo = 0;
	if(arguments.length >= 1) nFormNo = arguments[0];
	var chSeperator = ",";
	if(arguments.length >= 2) chSeperator = arguments[1];

	if(document.forms(nFormNo).del==null)
	{
		return -1;
	}
	var delCheckBoxes = document.forms(nFormNo).del;
	var nDelAccount = delCheckBoxes.length;
	var nSelected = 0;
	strSelectValue = "";

	if(nDelAccount == null)  // 当只有一个checkbox时，会返回对象本身，而不是一个数组
	{
		if(delCheckBoxes.checked)
		{
			strSelectValue = delCheckBoxes.value;
			nSelected = 1;
		}

		return nSelected;
	}

	for(i = 0; i < nDelAccount; i++)
	{
		if(delCheckBoxes[i].checked)
		{
			if(nSelected >= 1) strSelectValue += chSeperator+delCheckBoxes[i].value;
			else strSelectValue = delCheckBoxes[i].value;
			nSelected++;
		}
	}

	return nSelected;
}

function chooseall()
{
	var nFormNo = 0;
	if(arguments.length >= 1) nFormNo = arguments[0];
	var theForm = document.forms(nFormNo);

	var src=document.all.chooseimg.src;
	var index=src.lastIndexOf("/");
	var imgname=src.substring(index+1,src.length);

	if(imgname=="icon_chooseall1.gif")
	{
		for(i=0;i<theForm.del.length;i++)
		{
			theForm.del[i].checked=true;
			document.all.chooseimg.src="../images/list/icon_choosenone1.gif";
		}
		document.all.chooseimg.alt="不选";
	 }
	 else
	 {
		for(i=0;i<theForm.del.length;i++)
		{
			theForm.del[i].checked=false;
			document.all.chooseimg.src="../images/list/icon_chooseall1.gif";
		}
		document.all.chooseimg.alt="全选";
	 }
}

function changeorderimg(obj)
{
	var nFormNo = 0;
	if(arguments.length >= 2) nFormNo = arguments[1];
	var theForm = document.forms(nFormNo);

	imgobj=obj.children.tags("IMG").item(0);
	var src=imgobj.src;
	var index=src.lastIndexOf("/");
	var imgname=src.substring(index+1,src.length);
	
	theForm.page.value = 1;
	if(imgname=="icon_orderdown.gif")
	{
		theForm.order.value = obj.id + " asc";
	}
	else
	{
		theForm.order.value = obj.id + " desc";
	}

	theForm.submit();
}

var theLineNo=-1;

function lineClick(index)
{
	if(index<0)
	{
		temp = "line" + theLineNo;
		document.all[temp].className='';
		theLineNo = index;
		return;
	}
	var temp = "line"+ index;
	if(index!=theLineNo)
	{
		document.all[temp].className='listbgc_click'
		if(theLineNo>=0)
		{
			temp = "line" + theLineNo;
			document.all[temp].className=''
		}
		theLineNo = index;
	}
}
