

var isIE4 = (navigator.appVersion.indexOf("MSIE 4")>0)
var isIE55 = (navigator.appVersion.indexOf("MSIE 5.5")>0)
var tempSz
var sImgRatio  // height/width
sImgRatio=1
var idMenuHeight
idMenuHeight=130
var x,y
var bMenu
bMenu=false
var idEditbox
/*if(isIE55)
{
	alert("对不起，在本版本中，IE5.5不支持右键菜单属性!")
}*/

function getNoScriptHTML()
{
	var szHTML,szScript
	szHTML=getHTML()
	for(var i=0;i<idEditbox.document.scripts.length;i++)
	{
		szScript=idEditbox.document.scripts.item(i).innerHTML
		szScript=trim(szScript)
		
		//alert(szScript)
		//alert(szScript.length)
		if(szScript.length>6)szHTML=replace(szHTML,szScript,"//去掉")	
	}
	return szHTML
	
	
	
	
}

function setHTML(szHTML) 
{
	if (szHTML=="") szHTML = L_DEFAULTHTML_TEXT
	if (g_state.bMode){
		//if(g_bHTML)idEditbox.document.getElementsByTagName("HTML").item(0).innerHTML=szHTML
		//idEditbox.document.body.innerHTML = szHTML
	}	
	else
		idEditbox.document.body.innerText = szHTML
	if (g_state.bLoaded)
		setSelection(true)
}

function setTitle(szHTML) {
	document.title = szHTML
}

function getHTML() 
{
	//过滤空链接
	var j
	var El
	var aLinks=idEditbox.document.getElementsByTagName("A")
	var str
	for(var i=0;i<aLinks.length;i++){
		El=aLinks.item(i)
		
		if(El.childNodes.length==1&&El.childNodes(0).tagName==null){   //去掉空的链接
			str=trim(El.innerText)
			if(str.length<1)El.parentElement.removeChild(El)			
		}
		
	}
	
	
	var ElScripts=idEditbox.document.getElementsByTagName("SCRIPT")
	
	
	if(!g_bHTML){    //把js脚本去掉
	
		for(i=0;i<ElScripts.length;i++){
			El=ElScripts.item(i)
			El.parentElement.removeChild(El)			
		}
	}
		
	if(!g_state.bMode){
			setMode(true);
			g_state.bMode=true;
	}

	
	
	var szRet
	//if(g_bHTML)szRet = (g_state.bMode ? ("<HTML>"+idEditbox.document.getElementsByTagName("HTML").item(0).innerHTML+"</HTML>") : (idEditbox.document.body.innerText))
	szRet = (g_state.bMode ?  (_CUtil_CleanHTML()) :  (idEditbox.document.body.innerText))
	//var szRet = (g_state.bMode ?  (_CUtil_CleanHTML()) :  (idEditbox.document.getElementsByTagName("HTML").innerText))
	
	return szRet
}

function getText() {
	var szRet = ""
	if (g_state.bMode)
		szRet = idEditbox.document.body.innerText
	else {
		setMode(true)
		szRet = idEditbox.document.body.innerText
		setMode(false)
	}
	return szRet
}

function getBody() 
{
	var oRet = idEditbox.document.body
	return oRet
}

function getWidth() 
{
	var nRet = document.body.offsetWidth
	return nRet
}

function getHeight() 
{
	var nRet = document.body.offsetHeight
	return nRet
}

function insertHTML(szHTML) 
{
	var sType

	var sel = g_state.GetSelection()
	if(sel.offsetLeft!=1&&sel.offsetLeft!=0){
		
	sType = sel.type
	if (g_state.bMode) {
		if (sType=="Control")
			sel.item(0).outerHTML = szHTML
		else 
			sel.pasteHTML(szHTML)
	}
	else
		sel.text = szHTML
	}
	else{
		alert("请在编辑窗口内进行操作！")
	}		
		
	g_state.selection = null
}

function setFocus() {
	idEditbox.focus()
}

function appendHTML(szHTML) {
	if (g_state.bMode) 
		idEditbox.document.body.insertAdjacentHTML("beforeEnd",szHTML)
	else
		idEditbox.document.body.insertAdjacentText("beforeEnd",szHTML)
}

function setBGColor(szValue) 
{
	
	
	g_state.bgColor = szValue

	if (g_state.bMode)
		idEditbox.document.body.bgColor = g_state.bgColor
}

function setSelection(bDir) {
	var tr = idEditbox.document.body.createTextRange()
	tr.collapse(bDir)
	tr.select()
	g_state.SaveSelection()
}

function getBGColor() 
{
	var szRet = g_state.bgColor
	return szRet
}

function setDefaultStyle(szValue) 
{
	g_state.css = szValue
	if (g_state.bMode)
		idEditbox.document.body.style.cssText = g_state.css
}

function getDefaultStyle() 
{
	var oRet = g_state.css
	return oRet
}

function setSkin(szSkin) 
{
	if (szSkin == null)
		document.styleSheets.skin.cssText = g_state.defaultSkin
	else
		document.styleSheets.skin.cssText = szSkin
	document.styleSheets.skin.disabled = false	
}

function appendSkin(szSkin)
{
	document.styleSheets.appendSkin.cssText = szSkin
}

function setPopupSkin(szSkin) 
{
	if (szSkin == null)
		document.styleSheets.popupSkin.cssText = g_state.popupSkin
	else
		document.styleSheets.popupSkin.cssText = szSkin

	_CPopup_Init()
}

function setToolbar(id,g_state) 
{
	var el = document.all[id]
	if (el)
		el.style.display = (g_state) ? "" : "none"	
	if (id=="tbmode") {
		_setSize()
	}
}

function setLinks(arLinks) 
{
	g_state.aLinks = arLinks
}

function setBindings(aBindings) 
{
	if ((aBindings) && (aBindings.length>0)) 
	{
		g_state.aBindings = aBindings

		for (var iField = DBSelect.length-1; iField > 0; iField--)
			DBSelect[iField] = null

		for (var iField = 0; iField < g_state.aBindings.length; iField++)
			DBSelect.add(new Option(g_state.aBindings[iField]))

		tbDBSelect.style.display = "inline"
	}
	else
		tbDBSelect.style.display = ""
}

function _onEditFocus()
{
	if(bMenu)
	{
		return false;
	}
	else
	{
		_CPopup_Hide()
		_fireFocus()
	}
}

function setMode(bMode) 
{
	if (bMode!=g_state.bMode) {
		g_state.bMode = bMode
	
		var objBody = idEditbox.document.body
		if (!bMode&& !g_state.bMode) 
		{
			_CPopup_Hide()
			objBody.bgColor = objBody.style.cssText = ""
			if (g_state.customButtons)
				idStandardBar.style.display = "none"
			else
				idToolbar.style.display = "none"
	
			//if(g_bHTML)	objBody.innerText =idEditbox.document.getElementsByTagName("HTML").item(0).innerHTML 
			objBody.innerText =idEditbox.document.body.innerHTML
			//objBody.innerText = idEditbox.document.getElementsByTagName("HTML").innerText
			objBody.className = "textMode"
		}
		if ((bMode) && (g_state.bMode)) 
		{
			setDefaultStyle(g_state.css)
			setBGColor(g_state.bgColor)
			objBody.className = idStandardBar.style.display = idToolbar.style.display = ""
			objBody.innerHTML = idEditbox.document.body.innerText
			
		}
		_setSize()
		cbMode.checked = !bMode
		setSelection(true)
		setFocus()
	}	
	return bMode
}



function addButton(sID,sButton) 
{
	if (!sID)
		tbButtons.insertAdjacentHTML("beforeEnd","<BR>")
	else	
		tbButtons.insertAdjacentHTML("beforeEnd","<BUTTON TYPE=\"button\" ONCLICK=\"_userButtonClick(this)\" CLASS=\"userButton\" ID=\"" + sID + "\">" + sButton + "\</BUTTON>&nbsp;")
	
	g_state.customButtons = true
	return tbButtons.all[sID]
}


//  EDITOR PRIVATE

function _Format(szHow, szValue) {
	var oSel	= g_state.GetSelection()
	var sType   = oSel.type 
	var oTarget = (sType == "None" ? idEditbox.document : oSel)
	var oBlock  = (oSel.parentElement != null ? _CUtil_GetBlock(oSel.parentElement()) : oSel.item(0))
	setFocus()
	switch(szHow)
	{
		case "BackColor":
			var el = null
			if (oSel.parentElement != null) {
				el =  _CUtil_GetElement(oSel.parentElement(),"TD")
				if (!el) el =  _CUtil_GetElement(oSel.parentElement(),"TH")
				if (!el) el =  _CUtil_GetElement(oSel.parentElement(),"TR")
				if (!el) el =  _CUtil_GetElement(oSel.parentElement(),"TABLE")
			}
			else 
				el = _CUtil_GetElement(oSel.item(0),"TABLE")
			if (el)
				el.bgColor = szValue
			else
				setBGColor(szValue)
			break;
		case "Justify":
			if (oBlock) 
			{
				oBlock.style.textAlign = ""
				if (((oBlock.tagName=="TABLE") || (oBlock.tagName=="IMG")) && (("left"==oBlock.align) && ("Left"==szValue))) {
					oBlock.align = ""
					break;
				}	
				oBlock.align = szValue
				if ((oBlock.tagName=="HR") || ((oBlock.tagName=="IMG") && szValue!="Center")) break;
			}
			szHow=szHow+szValue
			szValue=""
			// Fall through
		default:
			oTarget.execCommand(szHow, false, szValue)
			break
	}
	g_state.RestoreSelection()
	setFocus()
	return true
}

function _fireFocus() {
	if (self.parent.RTEFocus)
		self.parent.RTEFocus(self)
}

function _initEditor() {
	//addButton("test","dff")
	g_state = new _CState()
	window.onresize = _setSize

	var sz  =   ""
	sz  +=  ""
	+   "<STYLE>"
	+	   ".DataBound{border:1 solid #999999;margin:1;font-family:Courier;background:#F1F1F1}\n"
	+	   ".textMode {border-top: 1px black solid;font: 10pt courier}\n.NOBORDER TD {border:1px gray solid}"
	+	   "BODY {border: 1px black solid;border-top: none;}"
	+   "</STYLE>"
	+   "<BODY >"	
	+   "</BODY>"
	//_CPopup_Init()
	//_CMenu_Init() 
	
	idEditbox.document.designMode = "on"
	idEditbox.document.open("text/html","replace")
	idEditbox.document.write(sz)
	idEditbox.document.close()
	idEditbox.document.body.onblur = idEditbox.onblur = g_state.SaveSelection	
	idEditbox.document.onkeydown = _Editor_KeyDownHandler
	idEditbox.document.onmousedown = _Editor_ClickHandler
	idEditbox.document.ondblclick = _Editor_DblClickHandler
	setTimeout("_pageReady()",0)
	setTimeout("setHTML(window.parent.strHtml)",0)
	
		
	


}

function _Editor_KeyDownHandler() {
	var ev = this.parentWindow.event
	if ((ev.keyCode==78 || ev.keyCode==72) && ev.ctrlKey) {
		if (ev.keyCode==78)
			window.open(parent.location)
		ev.keyCode=0;ev.returnValue=false
	}

	if (ev.keyCode==9)
		g_state.SaveSelection() 
	else 
		g_state.selection=null
}

function _Editor_ClickHandler() {
	
	
	g_state.selection = null
	//g_state.selection = g_state.GetSelection
	if(idEditbox.window.event.button==2)
	{
		
		if(!isIE55)
		{
			y=idEditbox.window.event.clientY+30
			x=idEditbox.window.event.clientX+10
			bMenu=true
			_CPopup_Show("MENU")
		}
		
	}
	else
	{
		_CPopup_Hide()
	}
}

function toggleMenu() {   
     //as the mouse moves over the menuItems, highlight them
     el=event.srcElement;
     if (el.className=="menuItem") {
        el.className="highlightItem";
     } else if (el.className=="highlightItem") {
        el.className="menuItem";
     }
  }

  function clickMenu() {
     //when the custom menu is visible (and capturing events),
     //this handler runs after a click event.  if one of the
     //menu items is clicked, it takes appropriate action.  
     //otherwise, it just hides the menu.
     menu1.style.display="none";
     el=event.srcElement;
     if (el.id=="mnuNavy") {
        whichDiv.style.backgroundColor="navy";
     } else if (el.id=="mnuPink") {
        whichDiv.style.backgroundColor="pink";   
     }
     menu1.releaseCapture();
  }

  function contextTwice() {
     //this handles the case in div#5 when you might right-click, and then you ctrl-right-click in the same div.
     if (event.srcElement==whichDiv)
       showMenu();
  }

function _Editor_DblClickHandler() {
	// Shortcuts
	var el = this.parentWindow.event.srcElement
	if (el.tagName=="IMG")  {
		el.removeAttribute("width")
		el.removeAttribute("height")
		el.style.removeAttribute("width")
		el.style.removeAttribute("height")
		el.width = el.width
		el.height = el.height
	}
	//if (el.tagName=="TABLE") 
	//	_openwin('TableEdit');
	
		//_CPopup_Show('Table')
}

function _setSize() {
	

		
}

function _pageReady() 
{
    //idEditbox.document.body.oncontextmenu = new Function("return false");
	
	if (self.parent.RTELoaded)
		self.parent.RTELoaded(self)
	if (document.styleSheets.skin.disabled) 
		setSkin(null)
	_setSize()
	
	g_state.bLoaded = true
	var tr = idEditbox.document.body.createTextRange()
	tr.select()
	g_state.SaveSelection()
	setSelection(false)
	
}

function _userButtonClick(el) 
{
	if (parent.RTEButton) parent.RTEButton(self, el.id)
}


function _drawToolbar()
{
	
	
	var sz = "<DIV ID=idStandardBar><NOBR>", iLeft=0, iHeight=24
	
	

	sz +="<select ID=\"FontName\" class=\"TBGen\" TITLE=\"字体名\"  onchange=\"format2('fontname',this[this.selectedIndex].value);\" > "
      + "    <option class=\"heading\">字体"
      + "    <option value=\"宋体\">宋体"
      + "    <option value=\"黑体\">黑体"
      + "    <option value=\"楷体_GB2312\">楷体"
      + "    <option value=\"仿宋_GB2312\">仿宋"
      + "    <option value=\"MS Song\">MS宋体"
      + "    <option value=\"MS Hei\">MS黑体"
      + "    <option value=\"MingLiU\">MingLiU"
      + "    <option value=\"Arial\">Arial"
      + "    <option value=\"Arial Black\">Arial Black"
      + "    <option value=\"Arial Narrow\">Arial Narrow"
      + "    <option value=\"Bradley Hand ITC\">Bradley Hand ITC"
      + "    <option value=\"Brush Script MT\">Brush Script MT"
      + "    <option value=\"Century Gothic\">Century Gothic"
      + "    <option value=\"Comic Sans MS\">Comic Sans MS"
      + "    <option value=\"Courier\">Courier"
      + "    <option value=\"Courier New\">Courier New"
      + "    <option value=\"MS Sans Serif\">MS Sans Serif"
      + "    <option value=\"Script\">Script"
      + "    <option value=\"System\">System"
      + "    <option value=\"Times New Roman\">Times New Roman"
      + "    <option value=\"Viner Hand ITC\">Viner Hand ITC"
      + "    <option value=\"Verdana\">Verdana"
      + "    <option value=\"Wide Latin\">Wide Latin"
      + "    <option value=\"Wingdings\">Wingdings"
      + "  </select>"
	  
	  
	  sz  +=""
	  +"<select ID=\"FontSize\" class=\"TBGen\" TITLE=\"字号大小\"  onchange=\"format2('fontsize',this[this.selectedIndex].value);\">"
      + "    <option class=\"heading\">字号"
      + "    <option value=\"1\">一号"
      + "    <option value=\"2\">二号"
      + "    <option value=\"3\">三号"
      + "    <option value=\"4\">四号"
      + "    <option value=\"5\">五号"
      + "    <option value=\"6\">六号"
      + "    <option value=\"7\">七号"
      + "  </select>"


	for (var i = 0 ; i < aSizes.length; i++) 
	{
		sz	+=  ""
		+   "<SPAN CLASS=tbButton ONKEYPRESS=\"if (event.keyCode==13) {" + aCommand[i] + ";event.keyCode=0}\" "
		+	   "ID=\"tb" + aIds[i] + "\" "
		+	   "STYLE=\"width: " + aSizes[i] + ";height:" + iHeight
		+	   "\""
		+   ">" 
		+	   "<SPAN " + (aTips[i]=="" ? "" : "TABINDEX=" + (i+2))
		+		   " STYLE=\""
		+			   "position:absolute;"
		+			   "width:" + aSizes[i] + ";height:" + iHeight + ";"
		+			   "clip: rect(0 " + aSizes[i] + " " + iHeight + " 0)"
		+		   "\""
		+	   ">"
		+		   "<IMG ALT=\"" + aTips[i] + "\" "
		+			   "TITLE=\"" + aTips[i] + "\" "
		+			   "ONCLICK=\"" + aCommand[i] + "; event.cancelBubble=true\" "
		+			   "ONMOUSEDOWN=\"if (event.button==1) this.style.pixelTop=-" + (iHeight*2) + "\" "
		+			   "ONMOUSEOVER=\"this.style.pixelTop=-" + iHeight + "\" "
		+			   "ONMOUSEOUT=\"this.style.pixelTop=0\" "
		+			   "ONMOUSEUP=\"this.style.pixelTop=-" + iHeight + "\" "
		+			   "SRC=\"" + L_TOOLBARGIF_TEXT + "\" "
		+			   "STYLE=\"position:absolute;top:0;left:-" + iLeft + "\""
		+		   ">"
		+	   "</SPAN>"
		+   "</SPAN>" 
		+  (aTips[i]=="" ?  "</NOBR><NOBR>" : "")
		iLeft += aSizes[i]
	}
	
	for ( i = 0 ; i < aTbSizes.length; i++) 
	{
		   sz	+=  ""
		+   "<SPAN CLASS=tbButton ONKEYPRESS=\"if (event.keyCode==13) {" + aTbCommand[i] + ";event.keyCode=0}\" "
		+	   "ID=\"tb" + aTbIds[i] + "\" "
		+	   "STYLE=\"width: " + aTbSizes[i] + ";height:" + iHeight
		+	   "\""
		+   ">" 
		+		   "<IMG style=\"disabled:false\" CLASS=\"tbMenuIcon\"  ALT=\"" + aTbTips[i] + "\" "
		+			   "TITLE=\"" + aTbTips[i] + "\" "
		+			   "ONCLICK=\"" + aTbCommand[i] + ";event.cancelBubble=true\" "
		//+			   "ONMOUSEOVER=\"this.\class=tbMenuIconCheckedMouseOver" + iHeight + "\" "
		+			   " ONMOUSEOVER=\"this.className='tbMenuIconMouseOver'\""
		+			   " ONMOUSEOUT=\"this.className='tbMenuIcon'\""
		+			   " ONMOUSEDOWN=\"this.className='tbButtonDown'\""
		+			   "SRC=\"images/" + aTbSrc[i] + "\" "
		+		   ">"
		+   "</SPAN>" 
		
		//+			   "ONMOUSEDOWN=\"if (event.button==1) this.style.pixelTop=-" + (iHeight*2) + "\" "
		//+			   "ONMOUSEOUT=\"this.style.pixelTop=0\" "
		//+			   "ONMOUSEUP=\"this.style.pixelTop=-" + iHeight + "\" "
		//+			   "STYLE=\"position:absolute;top:0;left:-" + iLeft + "\""
		
		
		
	}
	
	
	sz  +=  ""
	+   "</NOBR>"
	+   "<SPAN CLASS=tbButton ID=\"tbDBSelect\"><NOBR>&nbsp;&nbsp;" 
	+	L_TBDATALABEL_TEXT + " <SELECT "
	+		   "ID=DBSelect "
	+		   "ONCLICK='event.cancelBubble=true;' "
	+		   "ONCHANGE='_CPopup_InsertDatabound(this)' "
	+	   ">"
	+		   "<OPTION>"
	+			   "- " + L_TBDATABINDING_TEXT + " -"
	+		   "</OPTION>"
	+	   "</SELECT></NOBR></SPAN>"
	
	
	
	document.write(sz + "</DIV>")
}



function _drawModeSelect() {
	var sz = "<TABLE CELLSPACING=0 CELLPADDING=0 ID=idMode>"
	+	"<TR><TD><INPUT TYPE=checkbox ID=cbMode ONCLICK=\"setMode(!this.checked)\"></TD>"
	+   "<TD><LABEL FOR=cbMode TITLE=\""+L_MODETITLE_TEXT+"\">" + L_MODETITLE_TEXT + "</LABEL>" + L_MODEDESC_TEXT

	+   "</TD></TR></TABLE>"

	document.write(sz)
	cbMode.checked = false
}


//  _CState 

function _CState()
{
	this.selection		= null
	this.bMode			= true
	this.customButtons 	= false
	this.css = this.bgColor	= ""
	this.defaultSkin	= document.styleSheets.skin.cssText
	this.popupSkin		= document.styleSheets.popupSkin.cssText
	this.aLinks			= new Array()
	this.szSearch		= location.search.substring(1)
	this.aBindings		= new Array()
	this.aListPopups	= new Object()
	this.aCache			= new Object()
	this.bLoaded = false
	this.oPhoto = null
	
	this.RestoreSelection	= _CState_RestoreSelection
	this.GetSelection	= _CState_GetSelection
	this.SaveSelection	= _CState_SaveSelection
}

function _CState_RestoreSelection() 
{
	if (this.selection) this.selection.select()
}

function _CState_GetSelection() 
{
	var oSel = this.selection

	if (!oSel) {
		oSel = idEditbox.document.selection.createRange()
		oSel.type = idEditbox.document.selection.type
	}
	return oSel
}

function _CState_SaveSelection() 
{
	g_state.selection = idEditbox.document.selection.createRange()

	if (!g_state.selection ||
        (g_state.selection.parentElement && g_state.selection.parentElement() && 
         !(g_state.selection.parentElement() == idEditbox.document.body || idEditbox.document.body.contains(g_state.selection.parentElement()))))
    {
	    g_state.selection = idEditbox.document.body.createTextRange()
	    g_state.selection.collapse(false)
        g_state.selection.type = "None"
    }
    else
    {
	    g_state.selection.type = idEditbox.document.selection.type
    }
}


//  POPUP (Link, table and image popup need to be worked on)
function displayIE4PhotoPage() {
	if ((g_state.oPhoto) && (!g_state.oPhoto.closed))
		g_state.oPhoto.close()
	g_state.oPhoto = window.open(PHOTO_URL + ((g_state.szSearch!="") ? ("?" + g_state.szSearch) : ""),"photos","location=no,menubar=no,resizable=no,toolbar=no,width=500,height=450")
}

/*function _CModal_Show(szType) {
	_CPopup_Hide()
	if (szType=="Image") {
		if (isIE4) {
			setTimeout("displayIE4PhotoPage()",0)
		}
		else {
			var rValue = showModalDialog(PHOTO_URL + ((g_state.szSearch!="") ? ("?" + g_state.szSearch) : ""),true,"scroll:no;center:yes;help:no;")
			if (rValue!="")
				insertHTML(rValue)
			else
				setFocus()
		}
	}
}*/

function _CModal_Show(szType) {
	_CPopup_Hide2()
	
	var sel = g_state.GetSelection()
	if(sel.offsetLeft!=1&&sel.offsetLeft!=0){
	
			var AtWnd = window.open( PHOTO_URL,"AttachingWindow","width=400,height=300,resizable=yes,scrollbars=no,menubar=no,status=0");

			if ( !AtWnd.opener )
				AtWnd.opener = document.postit.window ;  //.AttachForm.window;
			AtWnd.focus();
	}
	else alert("请在编辑窗口进行操作！");
}

function IMAGESelect(w,sHTML) {
	if (sHTML!="") 
		insertHTML(sHTML)
	window.setFocus()
}



function _CPopup_Init() 
{
	var sz  =   ""
	+   "<HTML ID=popup>"
	+	   "<STYLE>" 
	+		   document.styleSheets.defPopupSkin.cssText 
	+		   "\n" 
	+		   document.styleSheets.popupSkin.cssText 
	+	   "</STYLE>"
	+	   "<BODY  style=\"font-size:9pt\""
	+		   "ONCONTEXTMENU=\"return false\" "
	+		   "ONSCROLL=\"return false\" SCROLL=no TABINDEX=-1 "
	+		   "ONSELECTSTART=\"return event.srcElement.tagName=='INPUT'\" "
	+	   "><DIV ID=puRegion>"
	+		   "<TABLE ID=header>"
	+			   "<TR>"
	+				   "<TH NOWRAP ID=caption></TH>"
	+				   "<TH VALIGN=middle ALIGN=RIGHT><DIV ID=close ONCLICK=\"parent._CPopup_Hide()\">"
	+					   L_CLOSEBUTTON_TEXT
	+				   "</DIV></TH>"
	+			   "</TR>"
	+		   "</TABLE>"
	+		   "<DIV ALIGN=CENTER ID=content></DIV>"
	+	   "</DIV></BODY>"
	+   "</HTML>"

	
}

function _CMenu_Init() 
{
	var sz  =   ""
	+   "<HTML ID=menu>"
	+	   "<BODY "
	+		   "ONCONTEXTMENU=\"return false\" "
	+		   "ONSCROLL=\"return false\" SCROLL=no TABINDEX=-1 "
	+		   "ONSELECTSTART=\"return event.srcElement.tagName=='INPUT'\" "
	+			" topmargin=\"0\" leftmargin=\"0\" bgcolor=\"#D3D0C9\" >"
	+		   "<DIV ALIGN=left ID=content></DIV>"
	+	   "</BODY>"
	+   "</HTML>"
	
	
}

function _CMenu_ShowPro()
{
	
	_openwin("ImgEdit") 
	_CMenu_Hide()
}

function _CMenu_Execute(sCommand)
{
	
	idEditbox.document.execCommand (sCommand)
	_CPopup_Hide()	
	
}

function _CPopup_InsertDatabound(eSelect)
{
	if (eSelect.selectedIndex != 0)
	{
		var sElemName = eSelect.options[eSelect.selectedIndex].text;
		var iLen = sElemName.length
		sElemName = sElemName.replace(/"/g, '&#034;')
		insertHTML('<INPUT CLASS=DataBound SIZE=' + (iLen + 2) + ' NAME="' + sElemName +'" VALUE=" ' + sElemName + ' ">')
		eSelect.selectedIndex = 0;
		idEditbox.focus()	
	}
}

function _CPopup_Hide() 
{

	//if(!bMenu){
	
	idEditbox.focus()
	//}
	bMenu=false
}

function _CMenu_Hide() 
{

}

function _CPopup_Hide2()
{
	bMenu=false
	
	
}

function _CPopup_Show(szType) 
{
	
	if (!isIE4)
		g_state.SaveSelection()
		
	var sel = g_state.GetSelection()
	if(sel.offsetLeft!=1&&sel.offsetLeft!=0){
	
	if(szType!="MENU")_CPopup_Init()
	else _CMenu_Init()
	
	var  bImg = false
	if(szType=="Emoticon")
	{
		var oSel = g_state.GetSelection()
	
		//判断是否是文字还是控件，图片标志bImg
		if (!oSel.parentElement)  		
		{
			bImg = oSel.item(0).tagName=="IMG"	
		}
	}
	else
	{
		bImg=true
	}	

	if(bImg)
	{
	var oRenderer, szCacheKey = "PopupRenderer." + szType
	if (!isIE4)
		g_state.SaveSelection()
   	
	}
	else
	{
		alert("请选择图片！")
	}
	}
	else alert("请在编辑窗口操作！");
}

function _CPopupRenderer_Display(szType) {
	var oRenderer, szCacheKey = "PopupRenderer." + szType
	oRenderer = g_state.aCache[szCacheKey]
	if(szType!="MENU")
	{
	if (oRenderer.autoSize) {	


		
	}
	else { 
		
	}
	}
	else
	{
		
		
	}
	
}

//cMenu function
function _CMenu_Show() 
{
	
	
	if (!isIE4)
		g_state.SaveSelection()
	var x,y,height
	idMenu.document.all.content.innerHTML	= _CMenuPopupRenderer_PrepareHTML()
	height=replace(document.all.idEditbox.style.height,"px","")
	
	if((y+idMenuHeight)>(0+height))y=height-idMenuHeight-2
	document.all.idMenu.style.posLeft=x
	document.all.idMenu.style.posTop=y
	document.all.idMenu.style.visibility = ""
	document.all.idMenu.style.height=idMenuHeight+5			
	document.all.idMenu.style.zIndex=2	
	
	idMenu.focus()
	
	
	

}




function _CPopupRenderer(szType)
{
	this.szType		=  szType
	this.elCurrent	=  this.oDocument  = null

	this.ResetContext   =  _CPopupRenderer_ResetContext
	this.GetCaption	= _CPopupRenderer_GetCaption	
	this.GetHTML	= _CPopupRenderer_GetHTML
	this.autoSize	= true
	this.OnMouseOver = new Function()
	this.OnKeyDown	= _CListPopupRenderer_GenericOnKeyDown
	switch(szType)
	{
		case "formatblock":
		case "font":
		case "fontsize":
			this.OnMouseOver= _CListPopupRenderer_OnMouseOver
			this.OnKeyDown  = _CListPopupRenderer_OnKeyDown
		case "BackColor": 
		case "ForeColor":
			this.OnClick	= _CListPopupRenderer_OnClick
			this.Highlight  = _CListPopupRenderer_Highlight
			this.Select		= _CListPopupRenderer_Select
			break
		default:
			this.OnClick	= new Function()
			break	
	}

	switch(szType)
	{
		case "formatblock":
			this.szCaption		= L_PUTITLEPARAGRAPHSTYLE_TEXT
			this.PrepareHTML	= _CFormatBlockPopupRenderer_PrepareHTML
			this.szHTML			= this.PrepareHTML()
			break
		case "font":
			this.szCaption		= L_PUTITLEFONTFACE_TEXT
			this.PrepareHTML	= _CFontFacesPopupRenderer_PrepareHTML
			this.szHTML			= this.PrepareHTML()
			break
		case "fontsize":
			this.szCaption		= L_PUTITLEFONTSIZE_TEXT
			this.PrepareHTML	=_CFontSizesPopupRenderer_PrepareHTML
			this.szHTML			= this.PrepareHTML()
			break
		case "Link":
			this.szCaption		= L_PUTITLELINK_TEXT
			this.PrepareHTML	= _CLinkPopupRenderer_PrepareHTML
			this.szHTML			= this.PrepareHTML()
			break
		case "Table": 
			this.szCaption		= L_PUTITLENEWTABLE_TEXT
			this.PrepareHTML	= _CTablePopupRenderer_PrepareHTML
			this.szHTML			= this.PrepareHTML()
			break
		case "BackColor": 
			this.szCaption		= L_PUTITLEBGCOLOR_TEXT
			this.szHTML			= "<DIV ID=ColorPopup ALIGN=CENTER>" + _CUtil_BuildColorTable("") + "</DIV>"
			break
		case "ForeColor":
			this.szCaption		= L_PUTITLETEXTCOLOR_TEXT
			this.szHTML			= "<DIV ID=ColorPopup ALIGN=CENTER>" + _CUtil_BuildColorTable("") + "</DIV>"
			break
		case "MENU":
			this.szCaption		= "菜单"
			this.PrepareHTML	= _CMenuPopupRenderer_PrepareHTML
			this.szHTML			= this.PrepareHTML()
			break
		case "Emoticon":
			//this.szCaption		= L_PUTITLEEMOTICON_TEXT
			this.szCaption		= L_IMGCHANGE_TEXT
			this.PrepareHTML	= _CImgPopupRenderer_PrepareHTML
			//this.PrepareHTML	= _CEmoticonRenderer_PrepareHTML
			this.szHTML			= this.PrepareHTML()
			this.GetHTML	= _CImgPopupRenderer_PrepareHTML
			break
		default:
			this.szCaption		= ""
			break
	}
}


function _CPopupRenderer_ResetContext(oDoc)
{
	this.oDocument  = oDoc
	this.elCurrent  = null

	if (this.szType=="Table") {
			var oSel	= idEditbox.document.selection.createRange() 
		var oBlock  = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TABLE") : _CUtil_GetElement(oSel.item(0),"TABLE"))
		if (oBlock!=null) {
			oDoc.all.tabEdit.className=""
			oDoc.all.tabEditBodytxtPadding.value = oBlock.cellPadding
			oDoc.all.tabEditBodytxtSpacing.value = oBlock.cellSpacing
			oDoc.all.tabEditBodytxtBorder.value = oBlock.border
			oDoc.all.tabEditBodytxtBorderColor.value = oBlock.borderColor
			oDoc.all.tabEditBodytxtBackgroundImage.value = oBlock.background
			oDoc.all.tabEditBodytxtBackgroundColor.value = oBlock.bgColor
		}
		oDoc.elCurrent = oBlock
	}
}

function _CPopupRenderer_GetCaption()
{
	return this.szCaption
}

function _CPopupRenderer_GetHTML()
{
	return this.szHTML
}

function _CEmoticonRenderer_PrepareHTML() {
	var sz = "<TABLE><TR><TD>"
	
	for (var i=0; i < arEmoticons.length; i++) 
	{
		sz  +=  "<IMG TABINDEX=" + (i+1) + " ONCLICK=\"parent._CEmoticonRenderer_Select(this)\" CLASS=emoticon SRC=\"" + L_EMOTICONPATH_TEXT + arEmoticons[i] + "\" WIDTH=12 HEIGHT=12 HSPACE=3 VSPACE=3>"
		if ((i+1)%8==0) sz+="<BR>"
	}			
	if (i%8!=0) sz+="<BR>"
	for (var i=0; i < arBigEmoticons.length; i++) 
	{
		sz  +=  "<IMG TABINDEX=" + (i+arEmoticons.length) + " ONCLICK=\"parent._CEmoticonRenderer_Select(this)\" CLASS=emoticon SRC=\"" + L_EMOTICONPATH_TEXT + arBigEmoticons[i] + "\" WIDTH=16 HEIGHT=16 HSPACE=4 VSPACE=4>"
		if ((i+1)%6==0) sz+="<BR>"
	}			
	
	return sz + "</TD></TR></TABLE>"
}

function _CEmoticonRenderer_Select(elImg) {
	insertHTML("<IMG SRC=\"" + elImg.src + "\" WIDTH=" + elImg.width + " HEIGHT=" + elImg.height + ">")
	g_state.RestoreSelection()
	_CPopup_Hide()
}

function _CFontSizesPopupRenderer_PrepareHTML()
{   
	var sz  =  "<TABLE ALIGN=center ID=idList CELLSPACING=0 CELLPADDING=0>"
	for (var i=1; i <= 7; i++) 
	{
		sz  +=  ""
		+   "<TR>"
		+	   "<TD NOWRAP "
		+		   "_item=" + i + " "
		+		   "ALIGN=center "
		+		   "STYLE=\"margin:0pt;padding:0pt\""
		+	   ">"
		+		   "<FONT SIZE=" + i + ">" 
		+			   L_STYLESAMPLE_TEXT + "(" + i + ")"
		+		   "</FONT>"
		+	   "</TD>"
		+   "</TR>"
	}			
	sz  +=  "</TABLE>"
	return sz
}

function _CFontFacesPopupRenderer_PrepareHTML()
{   
	var sz  =  "<TABLE ALIGN=center ID=idList CELLSPACING=0 CELLPADDING=0>"
	for (var i=0; i < defaultFonts.length; i++) 
	{
		sz  +=  ""
		+   "<TR>"
		+	   "<TD NOWRAP "
		+		   "_item=" + i + " "
		+		   "ALIGN=center "
		+		   "STYLE=\"margin:0pt;padding:0pt\""
		+	   ">"
		+		   "<FONT FACE=\"" + defaultFonts[i][0] + "\">" 
		+			   defaultFonts[i][1] 
		+		   "</FONT>"
		+			(defaultFonts[i][2] ? ("(" + defaultFonts[i][1] + ")") : "")
		+	   "</TD>"
		+   "</TR>"
	}
	sz += "<TR><TD ONCLICK=\"parent._CFontFacesPopupRenderer_InsertOther(this)\"  ALIGN=center _item=\"custom\" STYLE=\"margin:0pt;padding:0pt\" NOWRAP ID=customFont>" + L_CUSTOMFONT_TEXT + "</TD></TR>"
	sz  +=  "</TABLE>"

	return sz
}

function _CFontFacesPopupRenderer_InsertOther() {
	var szFont = prompt(L_CUSTOMFONTENTRY_TEXT,L_SAMPLEFONTENTRY_TEXT)
	if ((szFont!=null) && (szFont!=""))
		_Format("FontName",szFont)
	_CPopup_Hide()
}


function _TableDoFormat(objTableSelect)
{
+ "    <option value=\"Nothing\" style=\"height:1;\">————"
	  + "    <option value=\"TrAdd\" style=\"color: #C0C0C0\" >插入行"      
	  + "    <option value=\"TdAdd\" style=\"color: #C0C0C0\" >插入列"      
	  + "    <option value=\"Nothing\" style=\"height:1;\">————"
	  + "    <option value=\"TdEdit\" style=\"color: #C0C0C0\" >单元格属性"      
	  + "    <option value=\"TableEdit\" style=\"color: #C0C0C0\" >表格属性"      	  
	  
	  	
	if(_bTdEdit())
	{
		objTableSelect.options(3).style.color="#000000"
		objTableSelect.options(4).style.color="#000000"
		objTableSelect.options(6).style.color="#000000"		
		
		objTableSelect.options(3).value="TrAdd"
		objTableSelect.options(4).value="TdAdd"
		objTableSelect.options(6).value="TdEdit"
	}
	else
	{
		objTableSelect.options(3).style.color="#C0C0C0"
		objTableSelect.options(4).style.color="#C0C0C0"
		objTableSelect.options(6).style.color="#C0C0C0"
		
		objTableSelect.options(3).value="Nothing"
		objTableSelect.options(4).value="Nothing"
		objTableSelect.options(6).value="Nothing"

	}

	if(_bTableEdit())
	{
		objTableSelect.options(7).style.color="#000000"		
		objTableSelect.options(7).value="TableEdit"
	}
	else
	{
		objTableSelect.options(7).style.color="#C0C0C0"		
		objTableSelect.options(7).value="TableEdit"
	}
	//alert("1118 pass")
	

	
}


function _TableDo(szValue)
{
	switch(szValue)
	{
		case "TableAdd":
			_openwin('TableAdd')
			break
		case "TrAdd":
			if(_bTdEdit())
			{
			var oRow
			oSel	= idEditbox.document.selection.createRange() 
			oRow = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TR") : _CUtil_GetElement(oSel.item(0),"TR"))
			_CTablePopupRenderer_AddRow(oRow.rowIndex)
			}
			break
		case "TdAdd":
			if(_bTdEdit())
			{
				_CTablePopupRenderer_AddCell()
			}
			break
		case "TableEdit":
			if(_bTableEdit())
			{
				_openwin('TableEdit')
			}
			break
		case "TdEdit":
			if(_bTdEdit())
			{
				_openwin('TdEdit')
			}
			break		
	}

	document.all.TableDo.options(0).selected=true

}



function _CMenuPopupRenderer_PrepareHTML()
{   
	idMenuHeight=134
	var  bImg = false
	var oSel = g_state.GetSelection()			
	//判断是否是文字还是控件，图片标志bImg
	if (!oSel.parentElement)  		
	{
		bImg = oSel.item(0).tagName=="IMG"	
	}
	

	var sz	=""
	     sz +="<table border=\"0\" width=\"90\"  style=\"font-size: 9pt; color: #000000\" bgcolor=\"#D3D0C9\" >"
      + "  <tr>"
      + "    <td width=\"93\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._CMenu_Execute('Copy')\" style=\"color: #000000\" height=\"17\">"
      + "    复制&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"
      + "  </tr>"
      + "  <tr>"
      + "    <td width=\"93\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._CMenu_Execute('Cut')\" style=\"color: #000000\" height=\"17\">"
      + "	剪切&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"
      + "  </tr>"
      + "  <tr>"
      + "    <td width=\"93\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._CMenu_Execute('Paste')\" style=\"color: #000000\" height=\"14\">"
      + "	粘贴&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"
      + "  </tr>"
      + "  <tr>"
      + "    <td width=\"93\" align=\"center\" height=\"1\" style=\"color: #000000\" >"
      + "    ———————</td>"
      + "  </tr>"
      + "  <tr>"
      + "    <td width=\"93\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._openwin('Image')\" style=\"color: #000000\" height=\"14\">"
      + "    插入图片&nbsp;&nbsp;&nbsp;</td>"
      + "  </tr>"
      + "  <tr>"
      + "    <td width=\"93\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._CPopup_Show('Link')\" style=\"color: #000000\" height=\"17\">"
      + "    插入链接&nbsp;&nbsp;&nbsp;</td>"
      + "  </tr>"
      + "  <tr>"
      + "    <td width=\"93\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._openwin('TableAdd')\" style=\"color: #000000\" height=\"17\">"
      + "    插入表格&nbsp;&nbsp;&nbsp;</td>"
      + "  </tr>"
      
     if(_bTdEdit())
	{
		
		var oRow
		oSel	= idEditbox.document.selection.createRange() 
		oRow = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TR") : _CUtil_GetElement(oSel.item(0),"TR"))
		
		
		sz+=""	
		 + "  <tr>"
		+ "    <td width=\"93\" align=\"center\" height=\"1\" style=\"color: #000000\" >"
		+ "    ———————</td>"
		+ "  </tr>"
		+ "  <tr>"
		+ "    <td width=\"93\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._CTablePopupRenderer_AddRow("+oRow.rowIndex+")\" style=\"color: #000000\" height=\"14\">"
		+ "    插入行</td>"
		+ "  </tr>"
		+ "  <tr>"
		+ "    <td width=\"93\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._CTablePopupRenderer_AddCell()\" style=\"color: #000000\" height=\"14\">"
		+ "    插入列</td>"
		+ "  </tr>"
		 + "  <tr>"
      + "    <td width=\"93\" align=\"center\" height=\"1\" style=\"color: #000000\" >"
      + "    ———————</td>"
      + "  </tr>"
      idMenuHeight=idMenuHeight+70
		
	} 
    
    if(_bTdEdit())
	{
		sz+=""	
		+ "  <tr>"
		+ "    <td width=\"93\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._openwin('TdEdit')\" style=\"color: #000000\" height=\"14\">"
		+ "    单元格属性..</td>"
		+ "  </tr>"
		idMenuHeight=idMenuHeight+20
		
	}
	
	
	if(_bTableEdit())
	{
		sz+="	<tr>"	
		+ "    <td width=\"93\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._openwin('TableEdit')\" style=\"color: #000000\" height=\"14\">"
		+ "    表格属性..</td>"
		+ "  </tr>"
		idMenuHeight=idMenuHeight+20
	}
	
	
	      
      
	if(bImg)
	{
		sz+="	<tr>"	
		+"	<td width=\"100%\" align=\"left\" onmouseover=\"this.bgColor='#000080'\" onmouseout=\"this.bgColor='#D3D0C9'\"  onclick=\"parent._openwin('ImgEdit')\" style=\"color: #000000\">"
		+"	属性</td>"
		+"	</tr>"
		idMenuHeight=idMenuHeight+20
	}
	
	sz+="	</table>"


	return sz
}


function _CImgPopupRenderer_PrepareHTML() 
{
	var d = this.oDocument
	var oSel = g_state.GetSelection()
	var oEl, sType = oSel.type, bImg = false, szURL = sz = ""

	var szHspace,szVspace,szBorder,szHeight,szWidth,szAlign,szSrc
	var itemTemp
	itemTemp=oSel.item(0)
	szHspace=itemTemp.hspace
	szVspace=itemTemp.vspace
	szBorder=itemTemp.border
	szWidth=itemTemp.width
	szHeight=itemTemp.height
	szAlign=itemTemp.align
	szSrc=itemTemp.src

	sImgRatio=parseFloat(eval(szHeight+"/"+szWidth))


	//判断是否是文字还是控件，图片标志bImg
	if (oSel.parentElement)  
	{
		oEl = _CUtil_GetElement(oSel.parentElement(),"A")	
	}
	else 
	{

		oEl = _CUtil_GetElement(oSel.item(0),"A")
		bImg = oSel.item(0).tagName=="IMG"	
	}

	if (oEl)
		szURL = oEl.href
	
	sz  ="<table border=\"0\" width=\"400\" bgcolor=\"#000000\" cellspacing=\"1\"  style=\"font-size:9pt\">"
		+"<tr>"
		+"<td width=\"435\" colspan=\"2\" height=\"18\" bordercolor=\"#FFFFFF\" bgcolor=\"#FFFFFF\">"
		+"<p align=\"center\">"+L_IMGCHANGE_TEXT+"</td>"
		+"</tr>"

	sz  +=  ""	
	+"<tr>"
	+"<td  colspan=\"2\" height=\"18\" align=left bordercolor=\"#FFFFFF\" bgcolor=\"#FFFFFF\">"
	+"图片源："+szSrc
	+ "</td>"
	+ "</tr>"
	

	var arAlignN=new Array("左对齐","右对齐","顶页对齐","文本上方","相对垂直居中","绝对垂直居中","基线","相对底边对齐","绝对底边对齐","水平居中")
	var arAlignV=new Array("left","right","top","texttop","middle","absmiddle","baseline","bottom","absbottom","center")
	

	sz  +=" <tr>"
	+	"<td width=\"211\" height=\"17\" bordercolor=\"#FFFFFF\" bgcolor=\"#FFFFFF\">"
	+   "<NOBR>对齐方式：<SELECT ID=AlignPro>"
	
	for (var i=0;i<arAlignN.length;i++) {
		sz+= "<OPTION VALUE='" + arAlignV[i] + "' "
		+	(arAlignV[i]==szAlign.toLowerCase()? " SELECTED " : "")
		+ ">" + arAlignN[i]
	}
	sz += "</SELECT>"
	sz += "</TD><td width=\"218\" height=\"17\" bordercolor=\"#FFFFFF\" bgcolor=\"#FFFFFF\">"
	+	"边框大小：<INPUT ID=BorderPro SIZE=10 VALUE=\"" + szBorder + "\" TYPE=text></NOBR>"
	+	"</TD></TR>"

	sz  +=" <tr>"
	+	"<td width=\"211\" height=\"17\" bordercolor=\"#FFFFFF\" bgcolor=\"#FFFFFF\">"
	sz +="垂直间距：<INPUT ID=HspacePro SIZE=10 VALUE=\"" + szHspace + "\" TYPE=text></NOBR>"
	+	"</TD>"
	+	"<td width=\"211\" height=\"17\" bordercolor=\"#FFFFFF\" bgcolor=\"#FFFFFF\">"
	sz +="水平间距：<INPUT ID=VspacePro SIZE=10 VALUE=\"" + szVspace + "\" TYPE=text></NOBR>"
	+	"</TD></TR>"
	
	sz  +=" <tr>"
		+"<td width=\"435\" colspan=\"2\"  bordercolor=\"#FFFFFF\" bgcolor=\"#FFFFFF\">"
sz +="    <table border=\"0\" width=\"435\" style=\"font-size: 9pt\" bordercolor=\"#000000\" bgcolor=\"#000000\" cellspacing=\"1\">"
   + "      <tr>"
   + "        <td width=\"212\" colspan=\"2\" bgcolor=\"#FFFFFF\">"
   + " <input type=\"checkbox\" name=\"OneRatio\" value=\"1\" checked onclick=\"\">保持纵横比</td>"
   + "      </tr>"
   + "      <tr>"
   + "        <td width=\"118\" bgcolor=\"#FFFFFF\">高度：<input type=\"text\" name=\"HeightPro\" VALUE=\"" + szHeight + "\"  size=\"2\" maxlength=3  id=\"HeightPro\" onfocus=\"opener._Height_Focus(document)\">  </td>"
   + "        <td width=\"118\" bgcolor=\"#FFFFFF\">宽度：<input type=\"text\" name=\"WidthPro\" VALUE=\"" + szWidth + "\"  size=\"2\" maxlength=3  id=\"WidthPro\"  onfocus=\"opener._Width_Focus(document)\">"
   + "        </td>"
   + "      </tr>"
   + "      "
   + "    </table>"
   +	"</TD></TR>"
	


	var arTarget= new Array("_self","_blank","_parent","_top","_search")
	var arTypes = new Array("http","ftp","mailto")
	var arText = new Array("http://","ftp://","mailto:")
	var szType = szURL.substring(0,szURL.indexOf(":"))
	var szTarget=""

	if (oEl)
	{
		szURL = oEl.href
		szTarget=oEl.target
	}
	
	if (("http"==szType) || ("ftp"==szType)) 
			szURL = szURL.substring(szURL.indexOf("//")+2)
	else
			szURL = szURL.substring(szURL.indexOf(":")+1)

	sz  +=  ""	
	+"<tr>"
	+"<td width=\"435\" colspan=\"2\" height=\"18\" align=center bordercolor=\"#FFFFFF\" bgcolor=\"#FFFFFF\">"

	+   "链接目标:" 
	+"</td></tr>"
	+"<tr>"
	+"<td width=\"435\" colspan=\"2\" height=\"18\" align=left bordercolor=\"#FFFFFF\" bgcolor=\"#FFFFFF\">"
	+   "位置:" 
	+   "<SELECT ID=urlType>"
	
	for (var i=0;i<arTypes.length;i++) {
		sz+= "<OPTION VALUE='" + arTypes[i] + "' "
		+	(arTypes[i]==szType ? " SELECTED " : "")
		+ ">" + arText[i]
	}
	sz += "</SELECT><INPUT ID=urlValue SIZE=30 VALUE=\"" + szURL + "\" TYPE=text><br>"
	+		"目标框架："
	+   "<SELECT ID=targetPro>"
	
	for (var i=0;i<arTarget.length;i++) {
		sz+= "<OPTION VALUE='" + arTarget[i] + "' "
		+	(arTarget[i]==szTarget ? " SELECTED " : "")
		+ ">" +arTarget[i]
	}
	sz +=		   "</TD>"
	+	   "</TR>"


	sz  +=  ""	
	+"<tr>"
	+"<td width=\"435\" colspan=\"2\" height=\"18\" align=center bordercolor=\"#FFFFFF\" bgcolor=\"#FFFFFF\">"
	+			   "<INPUT ONCLICK=\"opener._CImgPopupRenderer_Edit(document,window);window.close()\" TYPE=submit ID=idSave VALUE=\"修改\"> <INPUT ONCLICK=\"window.close();opener.focus()\" TYPE=reset ID=idCancel VALUE=\"取消\">"
	+		   "</TD>"
	+	   "</TR>"
	+   "</TABLE>"
	return sz
	


}

	function _Height_Focus(d)
	{
	    
	    d.all.HeightPro.removeExpression("value"); 
	    d.all.WidthPro.removeExpression("value");  	     	    
		if(_bUnit1(d))
	    {
	    	d.all.WidthPro.setExpression("value","parseInt(this.document.all.HeightPro.value/"+sImgRatio+")","");  	    
	    }
	    
	 } 
	 
 	function _Width_Focus(d)
	{
	    d.all.HeightPro.removeExpression("value"); 
	    d.all.WidthPro.removeExpression("value");  	     	    
	    if(_bUnit1(d))
	    {
	    	d.all.HeightPro.setExpression("value","parseInt(this.document.all.WidthPro.value*"+sImgRatio+")","");  	    
	    }
	
	 } 
	 
	 function _bUnit1(d)
	 {
	 	if(d.all.OneRatio.checked)return true;
	 	return false;
	 }	 


function _CImgPopupRenderer_Edit(d,w)
{
	var szBorder=d.all.BorderPro.value
	var szHspace=d.all.HspacePro.value
	var szVspace=d.all.VspacePro.value	
	var szHeight=d.all.HeightPro.value
	var szWidth=d.all.WidthPro.value
	
	
	var szAlign=d.all.AlignPro[d.all.AlignPro.selectedIndex].value
	
	var oSel = w.elCurrent
	var sType = oSel.type
	
	oSel.width = szWidth
	if(ValidNumber(szHeight))	oSel.height = szHeight	
	if(ValidNumber(szBorder))   oSel.border = szBorder
	oSel.align = szAlign
	if(ValidNumber(szHspace))	oSel.hspace = szHspace
	if(ValidNumber(szHspace))	oSel.vspace = szVspace
	

	_CLinkPopupRenderer_AddLink(d)

	if(ValidNumber(szBorder)) oSel.border = szBorder
	
	var oEl, sType = oSel.type, bImg = false
	var szTarget=d.all.targetPro.value

	

	//判断是否是文字还是控件，图片标志bImg
	if (oSel.parentElement)  
	{
		oEl = _CUtil_GetElement(oSel.parentElement,"A")	
	}
	else 
	{

		oEl = _CUtil_GetElement(oSel,"A")	
	}

	if (oEl)
	{
		oEl.target=szTarget
	}

	setFocus()	
}



function _CFormatBlockPopupRenderer_PrepareHTML()
{   
	var sz, defaultParagraphs   = new Array()

	defaultParagraphs[0] = new Array("<P>", L_STYLENORMAL_TEXT + " (P)")	
	for (var i=1; i <= 6; i++) 
		defaultParagraphs[i] = new Array("<H"+i+">", L_STYLEHEADING_TEXT + i + " (H" + i + ")")	
	defaultParagraphs[7] = new Array("<PRE>", L_STYLEFORMATTED_TEXT + "(PRE)")

	sz  =  "<TABLE CLASS=block ALIGN=center ID=idList CELLSPACING=0 CELLPADDING=0>"
	for (var i=0; i < defaultParagraphs.length; i++) 
	{
		sz  +=  ""
		+   "<TR>"
		+	   "<TD NOWRAP "
		+		   "_item=" + i + " "
		+		   "ALIGN=center "
		+		   "STYLE=\"margin:0pt;padding:0pt\""
		+	   ">"
		+		   defaultParagraphs[i][0] 
		+		   defaultParagraphs[i][1] 
		+		   "</" + defaultParagraphs[i][0].substring(1) 
		+	   "</TD>"
		+   "</TR>"
	}
	sz  +=  "</TABLE>"
	return sz
}


function _CTablePopupRenderer_PrepareHTMLPage(szID,bDisplay) {
	var sz=""
	+   "<TABLE height=100% " + ((!bDisplay) ? " style=\"display: none\"" : "") + " width=100% CELLSPACING=0 CELLPADDING=0 ID=" + szID + ">"
	+	   "<TR ID=tableContents>"
	+		   "<TD ID=tableOptions VALIGN=TOP NOWRAP WIDTH=150 ROWSPAN=2>"
	+			   "<A HREF=\"javascript:parent._CTablePopupRenderer_Select(this,'" + szID + "','prop1')\">"
	+				   L_TABLEROWSANDCOLS_TEXT
	+			   "</A>"
	+			   "<BR>"
	+			   "<A HREF=\"javascript:parent._CTablePopupRenderer_Select(this,'" + szID + "','prop2')\">"
	+				   L_TABLEPADDINGANDSPACING_TEXT
	+			   "</A>"
	+			   "<BR>"
	+			   "<A HREF=\"javascript:parent._CTablePopupRenderer_Select(this,'" + szID + "','prop3')\">"
	+				   L_TABLEBORDERS_TEXT
	+			   "</A>"
	+			   "<BR>"
	+			   "<A HREF=\"javascript:parent._CTablePopupRenderer_Select(this,'" + szID + "','prop4')\">"
	+				   L_TABLEBG_TEXT
	+			   "</A>"
	+			   "<BR>"
	+		   "</TD>"
	+		   "<TD BGCOLOR=black ID=puDivider ROWSPAN=2>"
	+		   "</TD>"
	+		   "<TD ID=tableProps VALIGN=TOP>"
	if (szID=="tabNewBody") {
		sz+= "<DIV ID='" + szID + "prop1'>"
		+	"<P CLASS=tablePropsTitle>" + L_TABLEROWSANDCOLS_TEXT + "</P>"
		+  "<TABLE><TR><TD>"
		+				   L_TABLEINPUTROWS_TEXT
		+				   "</TD><TD><INPUT SIZE=2 MAXLENGTH=2 TYPE=text ID=" + szID + "txtRows VALUE=2 >"
		+				   "</TD></TR><TR><TD>"
		+				   L_TABLEINPUTCOLUMNS_TEXT
		+				   "</TD><TD><INPUT SIZE=2 MAXLENGTH=2 TYPE=text ID=" + szID + "txtColumns VALUE=2 >"
		+			   "</TD></TR></TABLE></DIV>" 
	} 
	else  {
		sz+= "<DIV ID='" + szID + "prop1'>"
			+	"<P CLASS=tablePropsTitle>" + L_TABLEROWSANDCOLS_TEXT + "</P>"	
			+   "<INPUT type=button ID=" + szID + "txtRows VALUE=\"" + L_TABLEINSERTROW_TEXT + "\" ONCLICK=\"parent._CTablePopupRenderer_AddRow(this)\"><P>"
			+   "<INPUT type=button ID=" + szID + "txtCells VALUE=\"" + L_TABLEINSERTCELL_TEXT + "\" ONCLICK=\"parent._CTablePopupRenderer_AddCell(this)\"><BR>"
			+	"</DIV>" 

	}

	sz +=			   "<DIV ID='" + szID + "prop2' STYLE=\"display: none\">"
	+					"<P CLASS=tablePropsTitle>" + L_TABLEPADDINGANDSPACING_TEXT + "</P>"
	+				   L_TABLEINPUTCELLPADDING_TEXT
	+				   "<INPUT SIZE=2 TYPE=text ID=" + szID + "txtPadding VALUE=1>"
	+				   "<BR>"
	+				   L_TABLEINPUTCELLSPACING_TEXT
	+				   "<INPUT SIZE=2 TYPE=text ID=" + szID + "txtSpacing VALUE=1>"
	+			   "</DIV>"
	+			   "<DIV ID=" + szID + "prop3 STYLE=\"display: none\">"
	+					"<P CLASS=tablePropsTitle>" + L_TABLEBORDERS_TEXT + "</P>"
	+				   L_TABLEINPUTBORDER_TEXT
	+				   "<INPUT SIZE=2 TYPE=text ID=" + szID + "txtBorder VALUE=1>"
	+				   "<BR>"
	+				   L_TABLEINPUTBORDERCOLOR_TEXT
	+				   "<INPUT SIZE=4 TYPE=text ID=" + szID + "txtBorderColor value=#000000><BR>" 
	+				   _CUtil_BuildColorTable("idBorder"+szID, "", "parent._CTablePopupRenderer_ColorSelect(this,'" + szID + "txtBorderColor')") 
	+			   "</DIV>"
	+			   "<DIV ID=" + szID + "prop4 SIZE=12 STYLE=\"display: none\">"
	+					"<P CLASS=tablePropsTitle>" + L_TABLEBG_TEXT + "</P>"
	+				   L_TABLEINPUTBGIMGURL_TEXT
	+				   "<INPUT TYPE=text ID=" + szID + "txtBackgroundImage SIZE=15>"
	+				   "<BR>"
	+				   L_TABLEINPUTBGCOLOR_TEXT	
	+				   "<INPUT TYPE=text SIZE=4 ID=" + szID + "txtBackgroundColor><BR>" 
	+				   _CUtil_BuildColorTable("idBackground"+szID, "", "parent._CTablePopupRenderer_ColorSelect(this,'" + szID + "txtBackgroundColor')") 
	+			   "</DIV>"
	+		   "</TD>"
	+	   "</TR><TR><TD align=center ID=tableButtons valign=bottom>"
	if (szID=="tabNewBody") {
		sz +=	"<INPUT TYPE=submit ONCLICK=\"parent._CTablePopupRenderer_BuildTable('" + szID + "',this.document)\" VALUE=\"" + L_TABLEINSERT_TEXT + "\">"
			+   " <INPUT TYPE=reset VALUE=\"" + L_CANCEL_TEXT + "\" ONCLICK=\"parent._CPopup_Hide()\">"
	} else {
		sz +=	"<INPUT TYPE=submit ONCLICK=\"parent._CTablePopupRenderer_BuildTable('" + szID + "',this.document)\" VALUE=\"" + L_TABLEUPDATE_TEXT + "\">"
			+   " <INPUT TYPE=reset VALUE=\"" + L_CANCEL_TEXT + "\" ONCLICK=\"parent._CPopup_Hide()\">"
	}
	sz+=   "</TD></TR></TABLE>"
	return sz
}

function _CTablePopupRenderer_PrepareHTML()
{   
	var sz  = "<TABLE CLASS=tabBox ID=\"tabSelect\" CELLSPACING=0 CELLPADDING=0 WIDTH=95%><TR HEIGHT=15><TD CLASS=tabItem STYLE=\"border-bottom: none\" NOWRAP><DIV ONCLICK=\"if (tabEdit.className!='disabled') {this.className='selected';this.parentElement.style.borderBottom = tabEdit.className=tabNewBody.style.display='';tabEditBody.style.display='none';tabEdit.parentElement.style.borderBottom='1px black solid'}\" CLASS=selected ID=tabNew>" + L_TABLENEW_TEXT + "</DIV></TD>"
	+   "<TD CLASS=tabItem NOWRAP><DIV ONCLICK=\"if (this.className!='disabled') {this.className='selected';this.parentElement.style.borderBottom = tabNew.className=tabEditBody.style.display='';tabNew.parentElement.style.borderBottom='1px black solid';tabNewBody.style.display='none'}\" CLASS=disabled ID=tabEdit>" + L_TABLEEDIT_TEXT + "</DIV></TD><TD CLASS=tabSpace WIDTH=100%>&nbsp;</TD></TR><TR><TD VALIGN=TOP CLASS=tabBody COLSPAN=3>"
	+   _CTablePopupRenderer_PrepareHTMLPage("tabNewBody",true)
	+   _CTablePopupRenderer_PrepareHTMLPage("tabEditBody",false)
	+	"</TD></TR></TABLE>"
	return sz
}

function _CTablePopupRenderer_Select(el,szID, id) 
{
	var d = el.document

	for (var i = 1; i < 5; i++)
		d.all[szID + "prop" + i].style.display = "none"

	d.all[szID + id].style.display = ""
}


function _CTablePopupRenderer_ColorSelect(el,id) 
{
	el.document.all[id].value = el.bgColor
}	


/*function _CTablePopupRenderer_AddRow(el) {
	var elRow = el.document.elCurrent.insertRow()
	for (var i=0;i<el.document.elCurrent.rows[0].cells.length;i++) {
		var elCell = elRow.insertCell()
		elCell.innerHTML = "&nbsp;"
	}
}*/

function _CTable_AddRow() {
		var oRow
		var oSel	= idEditbox.document.selection.createRange() 
		oRow = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TR") : _CUtil_GetElement(oSel.item(0),"TR"))
		if(oRow==null)
		{
			alert("请选择行！");
			return false;	
		}	
		else 
		{
			var nRowIndex=oRow.rowIndex		
			_CTablePopupRenderer_AddRow(nRowIndex)
		}	
		
}		

function _CTable_DelRow() {
		var oRow
		var oSel	= idEditbox.document.selection.createRange() 
		var oTable = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TABLE") : _CUtil_GetElement(oSel.item(0),"TABLE"))
		if(oTable==null)
		{
			alert("请指定表格！");
			return false;	
		}	
		else 
		{
			oRow = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TR") : _CUtil_GetElement(oSel.item(0),"TR"))
			if(oTable==null)
			{
				alert("请选择行");
				return false;
			}
			else
			{
				var nRowIndex=oRow.rowIndex
				oTable.deleteRow(nRowIndex)
			}
		}		
		
		
		
}		


function _CTable_AddCell() {
		var oRow
		var oSel	= idEditbox.document.selection.createRange() 
		oRow = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TR") : _CUtil_GetElement(oSel.item(0),"TR"))
		if(oRow==null)
		{
			alert("请选择行！");
			return false;	
		}	
		else 
		{
			var oCell = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TD") : _CUtil_GetElement(oSel.item(0),"TD"))
			if(oCell==null)oRow.insertCell();				
			else oRow.insertCell(oCell.cellIndex)					
		}	
		
}		

function _CTable_DelCell() {
		var oRow
		var oSel	= idEditbox.document.selection.createRange() 
		oRow = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TR") : _CUtil_GetElement(oSel.item(0),"TR"))
		if(oRow==null)
		{
			alert("请选择行！");
			return false;	
		}	
		else 
		{
			var oCell = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TD") : _CUtil_GetElement(oSel.item(0),"TD"));
			if(oCell==null)
			{	
				alert("请选择单元格！");
			}				
			else oRow.deleteCell(oCell.cellIndex);				
		}	
		
}		


function _CTable_SpltCel() {
		var oRow
		var oSel	= idEditbox.document.selection.createRange() 
		oRow = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TR") : _CUtil_GetElement(oSel.item(0),"TR"))
		oTable = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TABLE") : _CUtil_GetElement(oSel.item(0),"TABLE"))
		if(oRow==null)
		{
			alert("请选择行！");
			return false;	
		}	
		else 
		{
			var oCell = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TD") : _CUtil_GetElement(oSel.item(0),"TD"));
			if(oCell==null)
			{	
				alert("请选择单元格！");
			}				
			else
			{
				var oCellOther
				for (var i=0;i<oTable.rows.length;i++) {
					alert(oTable.rows[i].cells.length)
					/*oCellOther=oTable.rows[i].cells[oCell]
					var elCell = elRow.insertCell()
					elCell.innerHTML = "&nbsp;"*/
				}
			}	
		}	
		
}		



function _CTable_AddCol() {
		
	var oSel,oTable
	oSel	= idEditbox.document.selection.createRange() 
	oTable = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TABLE") : _CUtil_GetElement(oSel.item(0),"TABLE"))
	if(oTable==null)
	{
		alert("请选择表格！")
		return false;	
	}	
	else _CTablePopupRenderer_AddCell()
		
}		

function _CTable_DelCol() {
		var oRow,nCellIndex
		var oSel	= idEditbox.document.selection.createRange() 
		oRow = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TR") : _CUtil_GetElement(oSel.item(0),"TR"))
		if(oRow==null)
		{
			alert("请选择行！");
			return false;	
		}	
		else 
		{
			var oCell = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TD") : _CUtil_GetElement(oSel.item(0),"TD"));
			if(oCell==null)
			{	
				alert("请选择单元格！");
			}				
			else{
				
				var oTable
				nCellIndex=oCell.cellIndex
				oTable = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TABLE") : _CUtil_GetElement(oSel.item(0),"TABLE"))
				if(oTable==null)return false;
	
				for (var i=0;i<oTable.rows.length;i++) {
						oRow = oTable.rows[i]
						oRow.deleteCell((nCellIndex+1)<oRow.cells.length?nCellIndex:(oRow.cells.length-1));						
				}
				
			} 
		}	
		
}		

function _CTablePopupRenderer_AddRow(nRowIndex) {
	_CMenu_Hide()
	var oSel,oTable
	oSel	= idEditbox.document.selection.createRange() 
	oTable = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TABLE") : _CUtil_GetElement(oSel.item(0),"TABLE"))
	if(oTable==null)return false;

	//oTable.height=oTable.height+oTable.rows[nRowIndex].height
	
	var elRow = oTable.insertRow(nRowIndex+1)
	for (var i=0;i<oTable.rows[0].cells.length;i++) {
		var elCell = elRow.insertCell()
		elCell.innerHTML = "&nbsp;"
	}
	//_CMenu_Hide()
	
}

function _CTablePopupRenderer_AddCell() {
	
	_CMenu_Hide()
	var oSel,oTable
	oSel	= idEditbox.document.selection.createRange() 
	oTable = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TABLE") : _CUtil_GetElement(oSel.item(0),"TABLE"))
	if(oTable==null)return false;
	
	for (var i=0;i<oTable.rows.length;i++) {
		var elCell = oTable.rows[i].insertCell()
		elCell.innerHTML = "&nbsp;"
	}
}

function _CTablePopupRenderer_BuildTable(szID, d) 
{
	if (szID=="tabNewBody") {
		var sz =   ""
		+   "<TABLE "
		+  (((d.all[szID + "txtBorder"].value=="") || (d.all[szID + "txtBorder"].value=="0")) ? "class=\"NOBORDER\"" : "")
		+	   (d.all[szID + "txtPadding"].value != "" ? "cellPadding=\"" + d.all[szID + "txtPadding"].value + "\" " : "")
		+	   (d.all[szID + "txtSpacing"].value != "" ? "cellSpacing=\"" + d.all[szID + "txtSpacing"].value + "\" " : "")

		+	   (d.all[szID + "txtBorder"].value != "" ? "border=\"" + d.all[szID + "txtBorder"].value + "\" " : "")
		+	   (d.all[szID + "txtBorderColor"].value != "" ? "bordercolor=\"" + d.all[szID + "txtBorderColor"].value + "\" " : "")
		+	   (d.all[szID + "txtBackgroundImage"].value != "" ? "background=\"" + d.all[szID + "txtBackgroundImage"].value + "\" " : "")

		+	   (d.all[szID + "txtBackgroundColor"].value != "" ? "bgColor=\"" + d.all[szID + "txtBackgroundColor"].value + "\" " : "")
		+   ">"
		if (d.all[szID + "txtRows"].value>99) d.all[szID + "txtRows"].value=99
		if (d.all[szID + "txtColumns"].value>99) d.all[szID + "txtColumns"].value=50

		for (var r=0; r < parseInt(d.all[szID + "txtRows"].value); r++) 
		{
			sz +=  "<TR>"
			
			for (var c=0; c < parseInt(d.all[szID + "txtColumns"].value); c++)
				sz +=  "<TD>&nbsp;</TD>"
			
			sz +=  "</TR>"
		}

		sz +=  "</TABLE>"
		insertHTML(sz)
   } else
		if (d.elCurrent) {
			d.elCurrent.cellPadding = d.all.tabEditBodytxtPadding.value
			d.elCurrent.cellSpacing = d.all.tabEditBodytxtSpacing.value
			d.elCurrent.border = d.all.tabEditBodytxtBorder.value
			d.elCurrent.className = (d.elCurrent.border=="" || d.elCurrent.border==0) ? "NOBORDER" : ""
 			d.elCurrent.borderColor = d.all.tabEditBodytxtBorderColor.value
			d.elCurrent.bgColor = d.all.tabEditBodytxtBackgroundColor.value
			d.elCurrent.background = d.all.tabEditBodytxtBackgroundImage.value
   }
   g_state.RestoreSelection()
	_CPopup_Hide()	
}

function _CListPopupRenderer_OnClick() 
{
	var elTD = _CUtil_GetElement(this.oDocument.parentWindow.event.srcElement, "TD") 

	if (elTD && elTD._item) this.Select(elTD)
}

function _CListPopupRenderer_GenericOnKeyDown() {
	var ev		= this.oDocument.parentWindow.event
	if (ev.keyCode==27) _CPopup_Hide()
}

function _CListPopupRenderer_OnKeyDown() 
{
	var el
	var iRow = iCell	= 0
	var ev		= this.oDocument.parentWindow.event
	var idList  = this.oDocument.all.idList
	var elTR	= _CUtil_GetElement(this.elCurrent,"TR")
	var elTD	= _CUtil_GetElement(this.elCurrent,"TD")
	
	
	if (elTR != null) 
	{
		iRow	= elTR.rowIndex
		iCell   = elTD.cellIndex
	}
	switch (ev.keyCode) 
	{
		case 37:
			iCell--
			if (iCell < 0) 
				iCell = idList.rows[iRow].cells.length-1
			break
		case 38:
			iRow--
			if (iRow < 0) 
				iRow = idList.rows.length-1
			break
		case 39:
			iCell++
			if (iCell > idList.rows[iRow].cells.length-1) 
				iCell = 0
			break
		case 40:
			iRow++
			if (iRow > idList.rows.length-1) 
				iRow = 0
			break
		case 13:
			break;
		case 27:
			_CPopup_Hide()
			break;
		default:
			return;
	}

	el = idList.rows[iRow].cells[iCell]
	if (el && el._item)
		if (13 == ev.keyCode) {
			ev.keyCode=0		
			this.Select(el)
		}
		else
			this.Highlight(el)
}

function _CListPopupRenderer_OnMouseOver() 
{
	var el = _CUtil_GetElement(this.oDocument.parentWindow.event.srcElement, "TD") 

	if (el && el._item && el != this.elCurrent)
		this.Highlight(el)
}

function _CListPopupRenderer_Highlight(el) 
{
	var elC = this.elCurrent
	if (elC) elC.style.borderWidth =  elC.style.borderColor = elC.style.borderStyle  =   ""
	el.style.borderWidth	=   "1px"
	el.style.borderColor	=   "green"
	el.style.borderStyle	=   "solid"
	this.elCurrent			=   el
}

function _CListPopupRenderer_Select(elTD) 
{
	g_state.RestoreSelection()

	var el = elTD.children[0]
	switch (this.szType) 
	{
		case "font":
			if (!el)
				parent._CFontFacesPopupRenderer_InsertOther(this)
			else
				_Format("FontName",el.face)
			break
		case "fontsize":
			_Format("FontSize",el.size)			
			break
		case "formatblock":
			_Format("FormatBlock","<" + el.tagName + ">")
			break
		case "ForeColor":
			_Format("ForeColor", elTD.bgColor)
			break
		case "BackColor":
			_Format("BackColor",elTD.bgColor)
			break
	}
	_CPopup_Hide()
}

function _CLinkPopupRenderer_AddLink(d) 
{
	var szURL = d.all.urlValue.value
	var szType = d.all.urlType[d.all.urlType.selectedIndex].text
	var oSel = g_state.GetSelection()
	var sType = oSel.type
	
	szURL = ((0 == szURL.indexOf("mailto:") || 0 == szURL.indexOf("http://") || 0 == szURL.indexOf("ftp://")) ? "" : szType) + szURL
	if (szURL!="") 
	{
		if ((oSel.parentElement) && (oSel.text==""))
		{
			oSel.expand("word")
			if (oSel.text=="") 
			{
				var sText = ""
				var oStore = oSel.duplicate()
				if (d.all.pageList) {
					var idx = d.all.pageList.selectedIndex
					if (d.all.pageList[idx].value==szURL)
						sText = d.all.pageList[idx].text
					else
						sText = szURL
				}
				else
						sText = szURL
				oSel.pasteHTML('<A HREF="' + szURL + '">' + sText + '</A>')
				oSel.setEndPoint("StartToStart",oStore)
				oSel.select()
				setFocus()
				return
			} 
			oSel.select()
		}
		else
			if ((oSel.item) && (oSel.item(0).tagName=="IMG")) 
			{
					oSel.item(0).width = oSel.item(0).offsetWidth
					oSel.item(0).height = oSel.item(0).offsetHeight
					oSel.item(0).border = 1 //(d.all.displayBorder.checked) ? 1 : ""
			}
		
		if (d.all.urlValue.value!="")
			oSel.execCommand("CreateLink",false,szURL)	
		else
			oSel.execCommand("UnLink",false,szURL)			
	}

	setFocus()	
}

function _CLinkPopupRenderer__UpdateURL(oDoc,szURL) {
	var szType = szURL.substring(0,szURL.indexOf(":"))
	for (var i=0;i<oDoc.all.urlType.length;i++) 
		if (oDoc.all.urlType[i].value==szType)
			oDoc.all.urlType.selectedIndex = i
	if (("http"==szType) || ("ftp"==szType)) 
			szURL = szURL.substring(szURL.indexOf("//")+2)
	else
			szURL = szURL.substring(szURL.indexOf(":")+1)
	oDoc.all.urlValue.value = szURL
}

function _CLinkPopupRenderer_PrepareHTML() 
{
	var d = this.oDocument
	var oSel = g_state.GetSelection()
	var oEl, sType = oSel.type, bImg = false, szURL = sz = ""
	if (oSel.parentElement)  
	{
		oEl = _CUtil_GetElement(oSel.parentElement(),"A")
	}
	else 
	{

		oEl = _CUtil_GetElement(oSel.item(0),"A")
		bImg = oSel.item(0).tagName=="IMG"
	}

	if (oEl)
		szURL = oEl.href
	
	sz  ="<TABLE ALIGN=center>" 
	
	if (g_state.aLinks.length>0) 
	{
		sz  +=  ""
		+   "<TR>"
		+	   "<TD>" 
		+		   L_LINKSELECT_TEXT 
		+		   "<SELECT ID=pageList ONCHANGE=\"parent._CLinkPopupRenderer__UpdateURL(this.document,this[this.selectedIndex].value)\">"
		+			   "<OPTION VALUE=''>" 
		+				   "=="
		+				   L_LINKSELECTPAGE_TEXT
		+				   "=="
		+			   "</OPTION>"
		
		for (var i = 0; i < g_state.aLinks.length; i++) 
		{
			sz  +=  ""
			+   "<OPTION VALUE=\"" + g_state.aLinks[i][0] + "\" "
			+	   (oEl && (g_state.aLinks[i][0]==oEl.href) ? "SELECTED" : "")
			+   ">"
			+	   g_state.aLinks[i][1]
			+   "</OPTION>"
		}

		sz  +=  "</SELECT>"
	}
	var arTypes = new Array("http","ftp","mailto")
	var arText = new Array("http://","ftp://","mailto:")
	var szType = szURL.substring(0,szURL.indexOf(":"))
	if (("http"==szType) || ("ftp"==szType)) 
			szURL = szURL.substring(szURL.indexOf("//")+2)
	else
			szURL = szURL.substring(szURL.indexOf(":")+1)

	sz  += ""
	+   "<BR>" 
	+   L_LINKWEB_TEXT 
	+   "<NOBR><SELECT ID=urlType>"
	
	for (var i=0;i<arTypes.length;i++) {
		sz+= "<OPTION VALUE='" + arTypes[i] + "' "
		+	(arTypes[i]==szType ? " SELECTED " : "")
		+ ">" + arText[i]
	}
	sz += "</SELECT><INPUT ID=urlValue SIZE=45 VALUE=\"" + szURL + "\" TYPE=text></NOBR>"

	if (bImg)
	{
		sz  +=  ""
		+   "<BR>"
		+   "<INPUT TYPE=checkbox ID=displayBorder " + ((oSel.item(0).border!=0) ? " checked " : "") + ">" 
		+   L_LINKIMGBORDER_TEXT
	}

	sz  +=  ""
	+		   "</TD>"
	+	   "</TR>"
	+	   "<TR>"
	+		   "<TD ALIGN=center>"
	+			   "<INPUT ONCLICK=\"parent._CLinkPopupRenderer_AddLink(this.document)\" TYPE=submit ID=idSave VALUE=\"" + L_INSERT_TEXT + "\"> <INPUT ONCLICK=\"parent._CPopup_Hide()\" TYPE=reset ID=idCancel VALUE=\"" + L_CANCEL_TEXT + "\">"
	+		   "</TD>"
	+	   "</TR>"
	+   "</TABLE>"
	return sz
}

//  UTIL

function _CUtil_GetElement(oEl,sTag) 
{
	while (oEl!=null && oEl.tagName!=sTag)
		oEl = oEl.parentElement
	return oEl
}

function _CUtil_BuildColorTable(sID,fmt,szClick) 
{
	var sz, cPick = new Array("00","33","66","99","CC","FF"), iCnt=2
	var iColors = cPick.length, szColor = ""


	sz = "<TABLE CELLSPACING=0 CELLPADDING=0><TR><TD VALIGN=middle><DIV CLASS=currentColor ID=\"" + sID + "Current\">&nbsp;</DIV></TD><TD>"
	+   "<TABLE ONMOUSEOUT=\"document.all." + sID + "Current.style.backgroundColor = ''\" ONMOUSEOVER=\"document.all." + sID + "Current.style.backgroundColor = event.srcElement.bgColor\" CLASS=colorTable CELLSPACING=0 CELLPADDING=0 ID=\"" + sID + "\">"
	for (var r=0;r<iColors;r++) {
		sz+="<TR>"
		for (var g=iColors-1;g>=0;g--)
			for (var b=iColors-1;b>=0;b--) {
				szColor = cPick[r]+cPick[g]+cPick[b] 
				sz+="<TD"
						+ " BGCOLOR=\"#" + szColor + "\""
						+ "_item=\"" + szColor + "\" "
						+ "TITLE=\"#" + szColor + "\" "
						+ (szClick ? "ONCLICK=\"" + szClick + "\" " : "")
						+ ">&nbsp;</TD>"
			}
		sz+="</TR>"
	}
	sz+="</TABLE></TD></TR></TABLE>"
	return sz
}


function replace(str, replace_what, replace_with)
{
  var ndx = str.indexOf(replace_what);
  var delta = replace_with.length - replace_what.length;

  while (ndx >= 0)
  {
    str = str.substring(0,ndx) + replace_with + str.substring(ndx + replace_what.length);
    ndx = str.indexOf(replace_what, ndx + delta + 1);
  }
  return str;
}

function trim(s)
{
    var iFirst = 0;
    var iLast = s.length - 1;
    var sTrimChars = ' \t';
    while ( (iFirst < iLast) && (sTrimChars.indexOf(s.charAt(iFirst)) != -1) ) iFirst++;
    while ( (iLast >= iFirst) && (sTrimChars.indexOf(s.charAt(iLast)) != -1) ) iLast--;
    return s.substring(iFirst, iLast + 1);
}

/*function trim(str)
{
  
  if(str.length>0)
  {
  	var chTemp = str.charAt(0)
  	while(chTemp==" ")
  	{  		
  		if(str.length==1)
  		{
  			chTemp="1"
  			return ""
  			break 
  		}
  		else
  		{
  			str=str.slice(1)
  			chTemp = str.charAt(0)
  		}
  		
  	}
  	
  	var nLen=str.length
  	if(nLen==0)return ""
  	chTemp = str.charAt(nLen-1)
  	while(chTemp==" ")
  	{  		
  		if(nLen==1)
  		{
  			chTemp="1"
  			return ""
  			break 
  		}
  		else
  		{
  			str=str.slice(0,nLen-1)
  			nLen=str.length
  			chTemp = str.charAt(nLen-1)
  		}
  		
  	}  	
	return str
}
else	
{
	return ""
}
}*/


function _CUtil_TrimCR(sValue) {
	return replace(sValue,"\r\n", " ")
}

function _CUtil_GetBlock(oEl) 
{
	var sBlocks = "|H1|H2|H3|H4|H5|H6|P|PRE|LI|TD|DIV|BLOCKQUOTE|DT|DD|TABLE|HR|IMG|"

	while ((oEl!=null) && (sBlocks.indexOf("|"+oEl.tagName+"|")==-1))
		oEl = oEl.parentElement
	return oEl
}

function _CUtil_DomainSuffix(szHost) {
	var idx = szHost.indexOf("commun")
	if (idx>=0) {
		idx = szHost.indexOf(".",idx)
		return szHost.substring(idx+1)
	} else
	{
		idx = szHost.lastIndexOf(".",szHost.length-5)
		return szHost.substring(idx+1)
	}
	return szHost	
}

function _CUtil_CleanHTML() {
	var bBindings = (g_state.aBindings.length>0) 
	var elAll = idEditbox.document.all
	var iCount = elAll.length
	for (var i=iCount-1;i>=0;i--) {
		if (elAll[i].tagName=="IMG") {
			elAll[i].width = elAll[i].offsetWidth
			elAll[i].height = elAll[i].offsetHeight
		}
		if ((elAll[i].tagName=="INPUT") && (bBindings))
			elAll[i].outerHTML = '[' + elAll[i].name + ']';

	}
	return idEditbox.document.body.innerHTML
}


//添加
function _openwin(szType)
{
	_CPopup_Hide2()
	var sel = g_state.GetSelection();
	if(sel.offsetLeft!=1&&sel.offsetLeft!=0){
	var AtWnd,oBlock,url,oSel
	AtWnd=null
	
	if(szType=="TableEdit")
	{
		oSel	= idEditbox.document.selection.createRange() 
		oBlock = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TABLE") : _CUtil_GetElement(oSel.item(0),"TABLE"))
		if(oBlock==null)
		{
			alert("请选择表格！")
			return false
		}
	}
	
	if(szType=="TdEdit")
	{
		oSel	= idEditbox.document.selection.createRange() 
		oBlock = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TD") : _CUtil_GetElement(oSel.item(0),"TD"))
		if(oBlock==null)
		{
			alert("请选择单元格！")
			return false
		}
	}
	
	if(szType=="ImgEdit")
	{
		oSel	= idEditbox.document.selection.createRange() 
		oBlock = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"IMG") : _CUtil_GetElement(oSel.item(0),"IMG"))
		if(oBlock==null)
		{
			alert("请选择图片！")
			return false
		}
	}
	
	url=""
	if(szType=="Image")
	{
		url=PHOTO_URL;		
		AtWnd = window.open(url,"AttachingWindow","width=350,height=300,resizable=yes,scrollbars=no,menubar=no,status=0")
	}
	else if(szType=="TableAdd")
	{
		AtWnd = window.open(url,"AttachingWindow","width=320,height=450,left=10,top=10,resizable=yes,scrollbars=no,menubar=no,status=0")
	}
	
	else if(szType=="ImgEdit")
	{
		AtWnd = window.open(url,"AttachingWindow","width=460,height=280,left=10,top=10,resizable=yes,scrollbars=no,menubar=no,status=0")
	}
	else
	{
		AtWnd = window.open(url,"AttachingWindow","width=320,height=380,left=10,top=10,resizable=yes,scrollbars=no,menubar=no,status=0")
	}	
	
	
	
	switch(szType)
	{
		case "TableAdd":
			var sz=_PrepareHtmlTableAdd();
			AtWnd.document.title =L_TABLEINSERT_TEXT;
			AtWnd.document.write(sz)			
			AtWnd.document.close()
			
			
			break;
		case "TableEdit":
			var sz=_PrepareHtmlTableEdit(oBlock)
			AtWnd.document.write(sz)
			AtWnd.document.title =L_TABLEUPDATE_TEXT			
			AtWnd.document.close()
			break
		case "TdEdit":
			oSel	= idEditbox.document.selection.createRange() 
			oBlock = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TD") : _CUtil_GetElement(oSel.item(0),"TD"))
			AtWnd.document.title ="单元格属性"			
			AtWnd.document.write(_PrepareHtmlTdEdit(oBlock))
			AtWnd.document.close()
			break
		case "ImgEdit":
			var sz= _CImgPopupRenderer_PrepareHTML()
			AtWnd.document.write(sz)			
			AtWnd.document.title= L_IMGCHANGE_TEXT			
			AtWnd.document.close()
			break;
		default:
			break;	
	}
	
	AtWnd.elCurrent=oBlock	
	if ( !AtWnd.opener )
			AtWnd.opener = idEditbox;  //.AttachForm.window;
	AtWnd.focus();
	}
	else alert("请在编辑窗口中操作！");
	
	
}     

function _TableAddDone(w,d)
{
		
		var sz =   ""
		+   "<TABLE "
		+  (((d.all["borderPro"].value=="") || (d.all["borderPro"].value=="0")) ? "class=\"NOBORDER\"" : "")
		+	   (d.all["cellpaddingPro"].value != "" ? "cellPadding=\"" + d.all["cellpaddingPro"].value + "\" " : "")
		+	   (d.all["cellspacingPro"].value != "" ? "cellSpacing=\"" + d.all["cellspacingPro"].value + "\" " : "")
		+	   (d.all["bordercolorPro"].value != "" ? "bordercolor=\"" + d.all["bordercolorPro"].value + "\" " : "")
		+	   (d.all["bgcolorPro"].value != "" ? "bgColor=\"" + d.all["bgcolorPro"].value + "\" " : "")		
		+	   (d.all["borderPro"].value != "" ? "border=\"" + d.all["borderPro"].value + "\" " : "")
		+	   (d.all["heightPro"].value != "" ? "height=\"" + d.all["heightPro"].value + "\" " : "")
		+	   (d.all["widthPro"].value != "" ? "width=\"" + d.all["widthPro"].value + "\" " : "")
		+	   (d.all["alignPro"].value != "" ? "align=\"" + d.all["alignPro"].value + "\" " : "")
		+   ">"
		
		
		if (d.all["rowsPro"].value>99) d.all["rowsPro"].value=99
		if (d.all["columnsPro"].value>99) d.all["columnsPro"].value=50

		for (var r=0; r < parseInt(d.all["rowsPro"].value); r++) 
		{
			sz +=  "<TR>"
			
			for (var c=0; c < parseInt(d.all["columnsPro"].value); c++)
				sz +=  "<TD>&nbsp;</TD>"
			
			sz +=  "</TR>"
		}
		sz +=  "</TABLE>"
		w.close()
		insertHTML(sz)
		
} 

function _TableEditDone(w,d)
{
		var oBlock=w.elCurrent
		if(ValidNumber(d.all["widthPro"].value)) oBlock.width=d.all["widthPro"].value
		if(ValidNumber(d.all["heightPro"].value)) oBlock.height=d.all["heightPro"].value
		if(ValidNumber(d.all["borderPro"].value)) oBlock.border=d.all["borderPro"].value
		if(ValidNumber(d.all["cellpaddingPro"].value)) oBlock.cellPadding=d.all["cellpaddingPro"].value
		if(ValidNumber(d.all["cellspacingPro"].value)) oBlock.cellSpacing=d.all["cellspacingPro"].value
		
		oBlock.borderColor=d.all["bordercolorPro"].value
		oBlock.bgColor=d.all["bgcolorPro"].value
		oBlock.background=d.all["backgroundPro"].value		
		if(d.all["alignPro"].value.length>1)oBlock.align=d.all["alignPro"].value		
		
		
		w.close()
		
		
} 
function _TdEditDone(w,d)
{
		var oBlock=w.elCurrent
		oBlock.width=d.all["widthPro"].value
		oBlock.height=d.all["heightPro"].value
		oBlock.rowSpan=d.all["rowspanPro"].value
		oBlock.colSpan=d.all["colspanPro"].value
		if(d.all["bordercolorPro"].value.length>1) oBlock.borderColor=d.all["bordercolorPro"].value
		if(d.all["bgcolorPro"].value.length>1)oBlock.bgColor=d.all["bgcolorPro"].value
		if(d.all["backgroundPro"].value.length>1)oBlock.background=d.all["backgroundPro"].value		
		if(d.all["alignPro"].value.length>1)oBlock.align=d.all["alignPro"].value		
		
		w.close()
		
		
} 


function _CloseWindow(w)
{
			
		w.close()
		setFocus()
		
		
} 

//判断是否可以编辑单元格属性
function _bTdEdit()
{
	var oSel,oBlock
	oSel	= idEditbox.document.selection.createRange() 
	oBlock = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TD") : _CUtil_GetElement(oSel.item(0),"TD"))
	if(oBlock==null)return false;
	else return true;
}

//判断是否可以编辑表格属性
function _bTableEdit()
{
	var oSel,oBlock
	oSel	= idEditbox.document.selection.createRange() 
	oBlock = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TABLE") : _CUtil_GetElement(oSel.item(0),"TABLE"))
	if(oBlock==null)return false;
	else return true;
}

//判断是否可以编辑图片属性
function _bImgEdit()
{
	var oSel,oBlock
	oSel	= idEditbox.document.selection.createRange() 
	oBlock = (oSel.parentElement != null ? _CUtil_GetElement(oSel.parentElement(),"TABLE") : _CUtil_GetElement(oSel.item(0),"TABLE"))
	if(oBlock==null)return false;
	else return true;
}




function _PrepareHtmlTableAdd()
{
	var szAlign	=""

	szAlign+= "        <tr> "
      + "          <td align=\"right\">对齐方式</td>"
      + "          <td> <SELECT ID=alignPro style=\"font-size:9pt\">"		
	for (var i=0;i<arAlignText.length;i++) {
		szAlign+= "<OPTION VALUE='" + arAlignPro[i] + "' "
		+ ">" + arAlignText[i]+"</option>"
	}	
	
	szAlign+= " </SELECT></td>"
    + "        </tr>"	
	
	var sz="";
   sz +="<table width=\"300\" border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">"
      + "  <tr>"
      + "    <td>"
      + "      <table border=\"0\" style=\"font-size: 9pt\" cellpadding=\"2\" cellspacing=\"0\" bgcolor=\"#FFFFFF\" width=\"100%\">"
      + "        <tr bgcolor=\"#CCCCCC\"> "
      + "          <td colspan=\"2\">大小</td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td width=\"65\" align=\"right\">行数</td>"
      + "          <td> "
      + "            <input type=text name=rowsPro size=4 value=\"2\">"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">列数 </td>"
      + "          <td> "
      + "            <input type=text name=columnsPro size=4 value=\"3\">"
      + "          </td>"
      + "        </tr>"
      
      + "        <tr bgcolor=\"#CCCCCC\"> "
      + "          <td colspan=\"2\">布局</td>"
      + "        </tr>"
      sz+=szAlign
      sz+= "        <tr> "
      + "          <td align=\"right\"> 宽度</td>"
      + "          <td> "
      + "            <input type=text name=widthPro size=4 value=\"600\" >（像素）"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">高度</td>"
      + "          <td> "
      + "            <input type=text name=heightPro size=4>（像素）"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">单元格边距</td>"
      + "          <td> "
      + "            <input type=text name=cellpaddingPro size=4 value=\"1\">"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">单元格间距</td>"
      + "          <td> "
      + "            <input type=text name=cellspacingPro size=4 value=\"2\">"
      + "          </td>"
      + "        </tr>"
      + "        <tr bgcolor=\"#CCCCCC\"> "
      + "          <td colspan=\"2\">边框</td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">边框大小</td>"
      + "          <td> "
      + "            <input type=text name=borderPro size=4>"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">边框颜色</td>"
      + "          <td> "
      + "            <input type=text name=bordercolorPro size=4>"
      + "          </td>"
      + "        </tr>"
      + "        <tr bgcolor=\"#CCCCCC\"> "
      + "          <td colspan=\"2\">背景</td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">颜色</td>"
      + "          <td> "
      + "            <input type=text name=bgcolorPro size=4>"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">背景图片</td>"
      + "          <td> "
      + "            <input type=text name=backgroundPro size=9>"
      + "          </td>"
      + "        </tr>"      
      + "        <tr> "
      + "          <td colspan=\"2\" align=\"center\"> "
      + "            <input type=\"button\" value=\"确定\" name=\"B3\" onClick=\"window.opener._TableAddDone(window,document);\">"
      + "            <input type=\"button\" value=\"取消\" name=\"B3\" onClick=\"window.opener._CloseWindow(window);\">"
      + "          </td>"
      + "        </tr>"
      + "</table></td></tr></table>"
      return sz;

}

function _PrepareHtmlTableEdit(oBlock)
{
	var szAlign=""	

	szAlign+= "<tr> "
      + "          <td align=\"right\">对齐方式</td>"
      + "          <td> <SELECT ID=alignPro style=\"font-size:9pt\">"		
	
	for (var i=0;i<arAlignText.length;i++) {
		szAlign+= "<OPTION VALUE='" + arAlignPro[i] + "' "
		+	(arAlignPro[i]==oBlock.align.toLowerCase()? " SELECTED " : "")
		+ ">" + arAlignText[i]
	}
	
	
	szAlign+= "          </select></td>"
    + "        </tr>"	
		
	var sz=""                  
      sz +="<table width=\"300\" border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">"
      + "  <tr>"
      + "    <td>"
      + "      <table border=\"0\" style=\"font-size: 9pt\" cellpadding=\"2\" cellspacing=\"0\" bgcolor=\"#FFFFFF\" width=\"100%\">"
      + "        <tr bgcolor=\"#CCCCCC\"> "
      + "          <td colspan=\"2\">布局</td>"
      + "        </tr>"
      sz+=szAlign      
      sz+= "        <tr> "
      + "          <td align=\"right\"> 宽度</td>"
      + "          <td> "
      + "            <input type=text name=widthPro size=4 value='"+(oBlock.style.width==""?oBlock.width : oBlock.style.width)+"' >(像素)"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">高度</td>"
      + "          <td> "
      + "            <input type=text name=heightPro size=4 value='"+(oBlock.style.height==""?oBlock.height : oBlock.style.height)+"'>(像素)"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">单元格边距</td>"
      + "          <td> "
      + "            <input type=text name=cellpaddingPro size=4 value='"+oBlock.cellPadding+"'>"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">单元格间距</td>"
      + "          <td> "
      + "            <input type=text name=cellspacingPro size=4 value='"+oBlock.cellSpacing+"'>"
      + "          </td>"
      + "        </tr>"
      + "        <tr bgcolor=\"#CCCCCC\"> "
      + "          <td colspan=\"2\">边框</td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">边框大小</td>"
      + "          <td> "
      + "            <input type=text name=borderPro size=4   value='"+oBlock.border+"'>"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">边框颜色</td>"
      + "          <td> "
      + "            <input type=text name=bordercolorPro size=4   value='"+oBlock.borderColor+"'>"
      + "          </td>"
      + "        </tr>"
      + "        <tr bgcolor=\"#CCCCCC\"> "
      + "          <td colspan=\"2\">背景</td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">颜色</td>"
      + "          <td> "
      + "            <input type=text name=bgcolorPro size=4   value='"+oBlock.bgColor+"'>"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">背景图片</td>"
      + "          <td> "
      + "            <input type=text name=backgroundPro size=9   value='"+oBlock.background +"'>"
      + "          </td>"
      + "        </tr>"      
      + "        <tr> "
      + "          <td colspan=\"2\" align=\"center\"> "
      + "            <input type=\"button\" value=\"确定\" name=\"B3\" onClick=\"window.opener._TableEditDone(window,document);\">"
      + "            <input type=\"button\" value=\"取消\" name=\"B3\" onClick=\"window.opener._CloseWindow(window);\">"
      + "          </td>"
      + "        </tr>"
      + "</table></td></tr></table>"
      return sz;
      

}



function _PrepareHtmlTdEdit(oBlock)
{
	var szAlign=""

	szAlign+= "        <tr> "
      + "          <td align=\"right\">对齐方式</td>"
      + "          <td> <SELECT ID=alignPro style=\"font-size:9pt\">"		
	
	for (var i=0;i<arAlignText.length;i++) {
		szAlign+= "<OPTION VALUE='" + arAlignPro[i] + "' "
		+	(arAlignPro[i]==oBlock.align.toLowerCase()? " SELECTED " : "")
		+ ">" + arAlignText[i]
	}
	
	
	szAlign+= "          </select></td>"
    + "        </tr>"	
		
	var sz=""
                  
      sz +="<table width=\"300\" border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">"
      + "  <tr>"
      + "    <td>"
      + "      <table border=\"0\" style=\"font-size: 9pt\" cellpadding=\"2\" cellspacing=\"0\" bgcolor=\"#FFFFFF\" width=\"100%\">"
      + "        <tr bgcolor=\"#CCCCCC\"> "
      + "          <td colspan=\"2\">布局</td>"
      + "        </tr>"
      +szAlign
      + "        <tr> "
      + "          <td align=\"right\"> 宽度</td>"
      + "          <td> "
      + "            <input type=text name=widthPro size=4 value='"+(oBlock.style.width==""?oBlock.width : oBlock.style.width)+"' >"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">高度</td>"
      + "          <td> "
      + "            <input type=text name=heightPro size=4 value='"+(oBlock.style.height==""?oBlock.height : oBlock.style.height)+"'>"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">行跨度</td>"
      + "          <td> "
      + "            <input type=text name=rowspanPro size=4 value='"+oBlock.rowSpan+"'>"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">列跨度</td>"
      + "          <td> "
      + "            <input type=text name=colspanPro size=4 value='"+oBlock.colSpan+"'>"
      + "          </td>"
      + "        </tr>"
      + "        <tr bgcolor=\"#CCCCCC\"> "
      + "          <td colspan=\"2\">边框</td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">边框颜色</td>"
      + "          <td> "
      + "            <input type=text name=bordercolorPro size=4   value='"+oBlock.borderColor+"'>"
      + "          </td>"
      + "        </tr>"
      + "        <tr bgcolor=\"#CCCCCC\"> "
      + "          <td colspan=\"2\">背景</td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">颜色</td>"
      + "          <td> "
      + "            <input type=text name=bgcolorPro size=4   value='"+oBlock.bgColor+"'>"
      + "          </td>"
      + "        </tr>"
      + "        <tr> "
      + "          <td align=\"right\">背景图片</td>"
      + "          <td> "
      + "            <input type=text name=backgroundPro size=9   value='"+oBlock.background +"'>"
      + "          </td>"
      + "        </tr>"      
      + "        <tr> "
      + "          <td colspan=\"2\" align=\"center\"> "
      + "            <input type=\"button\" value=\"确定\" name=\"B3\" onClick=\"window.opener._TdEditDone(window,document);\">"
      + "            <input type=\"button\" value=\"取消\" name=\"B3\" onClick=\"window.opener._CloseWindow(window);\">"
      + "          </td>"
      + "        </tr>"
      +"</table>"
      return sz;

}


function format2(what,opt) {
  //if (!validateMode()) return;
  
  if (opt=="removeFormat") {
    what=opt;
    opt=null;
  }

  if (opt==null) idEditbox.document.execCommand(what);
  else idEditbox.document.execCommand(what,"",opt);
  
  //pureText = false;
  idEditbox.focus();
}

// Check if toolbar is being used when in text mode
function validateMode() {
  if (! bTextMode) return true;
  alert("Please uncheck the \"View HTML source\" checkbox to use the toolbars");
  idEditbox.focus();
  return false;
}


	function ValidNumber(item)
   {
       var length=item.length;
       var cc='';
       var dd='';
       
       var temp=false;
       var str='1234567890.';
       if(item==null||item=="")return true;
       
       for(var i=0;i<length;i++)
       {
           temp=false;
           cc=item.charAt(i);
           for(var j=0;j<str.length;j++)
           {
               dd=str.charAt(j);
               if(cc==dd)
               {
                  temp=true;
                  break;
               }
           }
           if(temp)continue;
           else 
           {
              return false;
           }
        }
        
        return true;
   }