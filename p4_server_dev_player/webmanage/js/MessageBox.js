var BTN_OK_CANCEL	 = 0;
var BTN_OK_ONLY		 = 1;
var IMG_CRITICAL	 = 16;
var IMG_QUESTION	 = 32;
var IMG_EXCLAMATION  = 48;
var IMG_INFORMATION  = 64;

var BTN_OK		= 1;
var BTN_CANCEL	= 2;

function MessageBox(strPrompt, strTitle, nButtons)
{
	var nLen = arguments.length;
	//alert("arguments.length="+nLen);
	if(nLen < 1) return;
	
	var strUrl = "/smemics/js/MessageBox.jsp?prompt="+strPrompt;
	if(nLen >= 2)
	{
		strUrl += ("&title="+strTitle);
		if(nLen == 3) strUrl += ("&buttons="+nButtons);
	}

	var width=200;
	var height=90+Math.ceil(strPrompt.length/14) * 12;
	var strFeatures = "dialogWidth:"+width+"px;"+"dialogHeight:"+height+"px;";
	strFeatures += "center:yes;help:no;resizeable:no;status:no;scroll:no";

	return window.showModalDialog(strUrl, "", strFeatures)
}