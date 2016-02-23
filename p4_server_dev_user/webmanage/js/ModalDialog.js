function ModalDialog(strUrl, strTitle, nWidth, nHeight, strScroll)
{
	var nLen = arguments.length;
	if(nLen < 4)
	{
		alert("Dialog function must have 4 parameters!");
		return;
	}
	
	LPIChildUrl = strUrl;
	var strUrl = "/fdms/js/ModalDialog.jsp?lpichildurl="+strUrl+"&title="+strTitle+"&scroll=";
	if(nLen == 5)
	{
		strUrl += strScroll;
	}
	else strUrl += "no";

	var strFeatures = "dialogWidth:"+nWidth+"px;"+"dialogHeight:"+nHeight+"px;";
	strFeatures += "center:yes;help:no;resizeable:no;status:no";

	return window.showModalDialog(strUrl, window, strFeatures)
}

function ModalDialogTemp(strUrl, strTitle, nWidth, nHeight, strScroll)
{
	var nLen = arguments.length;
	if(nLen < 5)
	{
		alert("Dialog function must have 4 parameters!");
		return;
	}

	var strFeatures = "dialogWidth:"+nWidth+"px;"+"dialogHeight:"+nHeight+"px;";
	strFeatures += "center:yes;help:no;resizeable:no;status:no;scroll" + strScroll;

	return window.showModalDialog(strUrl, window, strFeatures)
}