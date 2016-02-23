var Dvbbs_edit;
var Dvbbs_RangeType;
var Dvbbs_selection;
var Dvbbs_charset="UTF-8";
var Dvbbs_bLoad=false;
var Dvbbs_pureText=true;
var Dvbbs_bTextMode=1;
var Maxtitlelength=100;
var MaxConlength=16240;
var ispostnew=0;
var ischeck=false
//
function Dvbbs_InitDocument(hiddenid, charset)
{	
	if (charset!=null)
	Dvbbs_charset=charset;
	if (Dvbbs_bIsIE5){
		var Dvbbs_bodyTag="<style type=text/css>.quote{margin:5px 20px;border:1px solid #CCCCCC;padding:5px; background:#F3F3F3 }\nbody{boder:0px}.HtmlCode{margin:5px 20px;border:1px solid #CCCCCC;padding:5px;background:#FDFDDF;font-size:14px;font-family:Tahoma;font-style : oblique;line-height : normal ;font-weight:bold;}\nbody{boder:0px}</style></head><BODY bgcolor=\"#FFFFFF\" title=\"Ctrl+Enter直接提交贴子\" onkeydown=\"ctlent();\">";
	}else
	{
		var Dvbbs_bodyTag="<style type=text/css>.quote{margin:5px 20px;border:1px solid #CCCCCC;padding:5px; background:#F3F3F3 }\nbody{boder:0px}.HtmlCode{margin:5px 20px;border:1px solid #CCCCCC;padding:5px;background:#FDFDDF;font-size:14px;font-family:Tahoma;font-style : oblique;line-height : normal ;font-weight:bold;}\nbody{boder:0px}</style></head><BODY bgcolor=\"#FFFFFF\">";
	}
	var h=document.getElementById(hiddenid).value;
	
	if (navigator.appVersion.indexOf("MSIE 6.0",0)==-1){
	IframeID.document.designMode="On";	
	IframeID.contentEditable="on";
	}
	IframeID.document.open();
	IframeID.document.write ('<html><head>');
	if (Dvbbs_bIsIE5){
	IframeID.document.write ('<link href="../../style/baseStyleEN.css" rel="stylesheet" type="text/css">');
	
	IframeID.document.write ('<script language="javascript">');
	IframeID.document.write ('var ispost=0;');
	IframeID.document.write ('	function ctlent(eventobject)');
	IframeID.document.write ('	{');
	IframeID.document.write ('		if(event.ctrlKey && window.event.keyCode==13&&ispost==0)');
	IframeID.document.write ('		{');
	IframeID.document.write ('			parent.Dvbbs_CopyData("'+hiddenid+'"); ');
	IframeID.document.write ('			parent.Checkdata();');
	IframeID.document.write ('			if(parent.ischeck==true){');
	IframeID.document.write ('			parent.document.Dvform.Submit.disabled=true;');
	IframeID.document.write ('			parent.document.Dvform.Submit2.disabled=true;');
	IframeID.document.write ('			parent.document.Dvform.submit();}');
	IframeID.document.write ('		}');
	IframeID.document.write ('	}');
	IframeID.document.write ('<\/script>');
	}
	IframeID.document.write(Dvbbs_bodyTag);
	IframeID.document.write("</body>");
	IframeID.document.write("</html>");
	if (h!="")
	{
		IframeID.document.body.innerText=h;
	}
	IframeID.document.close();
	IframeID.document.body.contentEditable = "True";
	IframeID.document.charset=Dvbbs_charset;
	Dvbbs_bLoad=true;
	
	Dvbbs_setMode(1,1);
	IframeID.focus();
}

function ctlent(){
	var ispost=0;
	if (document.all)
	{
		if(event.ctrlKey && event.keyCode==13)
		{
		Dvbbs_CopyData('Body')
		Checkdata()
		if (ischeck==true){this.document.Dvform.submit();}
		}
	}
}

function Dvbbs_setMode(n,v)
{
	Dvbbs_setStyle();
	var cont;
	var Dvbbs_Toolbar0=document.getElementById("ExtToolbar0");
	var Dvbbs_Toolbar1=document.getElementById("ExtToolbar1");
	var Dvbbs_Toolbar2=document.getElementById("ExtToolbar2");
	var Dvbbs_Toolbar3=document.getElementById("old_Toolbars");
	var Dvbbs_Toolbar4=document.getElementById("imgToolbar");

	if (v&&Dvbbs_Mode!=3)
	{
		
		switch (Dvbbs_Mode){
			case 0:
				document.getElementById("Dvbbs_TabHtml").style.display="none";
				break;
			case 1:
				//document.getElementById("Dvbbs_TabOldTools").style.display="none";
				break;
			case 2:
				document.getElementById("Dvbbs_TabHtml").style.display="none";
				document.getElementById("Dvbbs_TabDesign").style.display="none";
				n=3;
				break;
		}
	}
	switch (n){
		case 1:
				Dvbbs_Toolbar0.style.display="";
				Dvbbs_Toolbar1.style.display="";
				Dvbbs_Toolbar2.style.display="";
				Dvbbs_Toolbar4.style.display="";
				Dvbbs_Toolbar3.style.display="none";		//ubb工具栏关闭
				if (Dvbbs_bIsIE5){
					cont=IframeID.document.body.innerText;
					IframeID.document.body.innerHTML=cont;
				}else{
					var html = IframeID.document.body.ownerDocument.createRange();
					html.selectNodeContents(IframeID.document.body);
					IframeID.document.body.innerHTML = html.toString();
				}
				break;
		case 2:
				Dvbbs_Toolbar0.style.display="none";	//关闭工具栏
				Dvbbs_Toolbar1.style.display="none";
				Dvbbs_Toolbar2.style.display="none";
				Dvbbs_Toolbar3.style.display="none";	//关闭UBB
				Dvbbs_Toolbar4.style.display="none";
				if(Dvbbs_bTextMode!=1){
					cont=IframeID.document.body.innerText;
				}
				else{
					cont=IframeID.document.body.innerHTML;
				}
				if (cont)
				{
					if (Dvbbs_bIsIE5){					//IE
						IframeID.document.body.innerText=cont;
					}else{								//Nc
						var html=document.createTextNode(cont);
						IframeID.document.body.innerHTML = "";
						IframeID.document.body.appendChild(html);
					}
				}
				break;

		case 3:
				Dvbbs_Toolbar0.style.display="";
				Dvbbs_Toolbar1.style.display="none";
				Dvbbs_Toolbar2.style.display="none";
				Dvbbs_Toolbar3.style.display="";		//打开UBB
				if(Dvbbs_bTextMode!=1){
					cont=IframeID.document.body.innerText;
				}
				else{
					cont=IframeID.document.body.innerHTML;
				}
				if (cont)
				{
					if (Dvbbs_bIsIE5){					//IE
						if (v)
						{
							IframeID.document.body.innerHtml=cont;
						}else{
							IframeID.document.body.innerText=cont;
						}
						
					}else{								//Nc
						var html=document.createTextNode(cont);
						IframeID.document.body.innerHTML = "";
						IframeID.document.body.appendChild(html);
					}
				}
				break;
	}
	Dvbbs_setTab(n);
	Dvbbs_bTextMode=n;
}

function Dvbbs_setTab(n)
{
	//html和design按钮的样式更改
	var mhtml=document.getElementById("Dvbbs_TabHtml");
	var mdesign=document.getElementById("Dvbbs_TabDesign");
	//var mOldTool=document.getElementById("Dvbbs_TabOldTools");
	if (n==1)
	{
		mhtml.className="Dvbbs_TabOff";
		mdesign.className="Dvbbs_TabOn";
		//mOldTool.className="Dvbbs_TabOff";
	}
	else if (n==2)
	{
		mhtml.className="Dvbbs_TabOn";
		mdesign.className="Dvbbs_TabOff";
		//mOldTool.className="Dvbbs_TabOff";

	}
	else if (n==3)
	{
		mhtml.className="Dvbbs_TabOff";
		mdesign.className="Dvbbs_TabOff";
		//mOldTool.className="Dvbbs_TabOn";
	}
}

function Dvbbs_setStyle()
{
	var bs = IframeID.document.body.style;	
	if (Dvbbs_bTextMode==2) {
		bs.fontFamily="Arial";
		bs.fontSize="10pt";
	}else{
		bs.fontFamily="Arial";
		bs.fontSize="10.5pt";
	}
	bs.scrollbar3dLightColor= '#D4D0C8';
	bs.scrollbarArrowColor= '#000000';
	bs.scrollbarBaseColor= '#D4D0C8';
	bs.scrollbarDarkShadowColor= '#D4D0C8';
	bs.scrollbarFaceColor= '#D4D0C8';
	bs.scrollbarHighlightColor= '#808080';
	bs.scrollbarShadowColor= '#808080';
	bs.scrollbarTrackColor= '#D4D0C8';
	bs.border='0';
}

function Dvbbs_validateMode()
{
	if (Dvbbs_bTextMode!=2) return true;
	alert("请取消“查看HTML源代码”选项再使用系统编辑功能或者提交!");
	IframeID.focus();
	return false;
}

function Dvbbs_CleanCode()
{
	var editor=IframeID;
	editor.focus();
	if (Dvbbs_bIsIE5){
	// 0bject based cleaning
		var body = editor.document.body;
		for (var index = 0; index < body.all.length; index++) {
			tag = body.all[index];
		//*if (tag.Attribute["className"].indexOf("mso") > -1)
			tag.removeAttribute("className","",0);
			tag.removeAttribute("style","",0);
		}
	// Regex based cleaning
		var html = editor.document.body.innerHTML;
		html = html.replace(/\<p>/gi,"[$p]");
		html = html.replace(/\<\/p>/gi,"[$\/p]");
		html = html.replace(/\<br>/gi,"[$br]");
		html = html.replace(/\<[^>]*>/g,"");        ///过滤其它所有"<...>"标签
		html = html.replace(/\[\$p\]/gi,"<p>");
		html = html.replace(/\[\$\/p\]/gi,"<\/p>");
		html = html.replace(/\[\$br\]/gi,"<br>");
		editor.document.body.innerHTML = html;
	}else
	{
		var html = IframeID.document.body.ownerDocument.createRange();
		html.selectNodeContents(IframeID.document.body);
		IframeID.document.body.innerHTML = html.toString();
	}
}

var colour
function FormatText(command, option)
{
	var codewrite
	if(Dvbbs_bTextMode==3){
		switch (command)
		{
			case 'fontsize':
				Dv_ubb("size",option)
				break;
			case 'fontname':
				Dv_ubb("face",option)
				break;
		}
	return
	}

	if (Dvbbs_bIsIE5){
		if(command=="clear all")
		{
		IframeID.document.body.innerHTML="";
		return;
		}
		if (option=="removeFormat"){
			command=option;
			option=null;
		}
		IframeID.focus();
	  	IframeID.document.execCommand(command, false, option);
		Dvbbs_pureText = false;
		IframeID.focus();
	}else{
		if ((command == 'forecolor') || (command == 'backcolor')) {
				parent.command = command;
				buttonElement = document.getElementById(command);
				document.getElementById("colourPalette").style.left = getOffsetLeft(buttonElement) + "px";
				document.getElementById("colourPalette").style.top = (getOffsetTop(buttonElement) + buttonElement.offsetHeight) + "px";
				if (document.getElementById("colourPalette").style.visibility=="hidden")
					{document.getElementById("colourPalette").style.visibility="visible";
				}else {
					document.getElementById("colourPalette").style.visibility="hidden";
				}
				//get current selected range
				//var sel = IframeID.document.selection;
				//if (sel != null) {
					//colour = sel.createRange();
				//}
		}
	}

}

function setColor(color)
{
	IframeID.document.execCommand(parent.command, false, color);
	IframeID.focus();
	document.getElementById("colourPalette").style.visibility="hidden";
}

function lookmagic()
{
	var obj=document.getElementById("magicframe");
	var buttonElement = document.getElementById("magicfacepic");
	if (obj.style.visibility=="hidden")
	{
		obj.style.top = (getOffsetTop(buttonElement) + buttonElement.offsetHeight)+"px";
		obj.style.left = (getOffsetLeft(buttonElement)-410)+"px";
		obj.style.visibility="visible";
	}else {
		obj.style.visibility="hidden";
	}
}
function closemagic()
{
	var cm=document.getElementById("magicframe");
	if (cm.style.visibility=="visible")
	{
		cm.style.visibility = "hidden";
	}
}