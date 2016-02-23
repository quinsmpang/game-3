var menubarsum = 0;
var menusmallicon = false;
var bodywidth = document.body.clientWidth;
var bodywidth2 = 0;
var menubarheight = 0;
var menuspeed = 10;
var menuinc = 100;
var scrollspeed = 100;
var scrollinc = 60;
var menuchoose = 0;
var iconX = new Array(menubarsum);
var menuIconWidth = new Array(menubarsum);
var menuIconHeight = new Array(menubarsum);
var menuscroll = 0;
var iconareaheight = 0;
var iconrightpos = 0;
var maxscroll = 0;
var scrolling = false;
var scrollTimerID = 0;
var resizeTimerID = 0;

function init() {
	window.status = 'Slide menu init';
	if (menubarsum > 0) {
		menubarheight = 22
		menulayer = new Array(menubarsum)
		window.status = 'Menulayer created';
		iconlayer = new Array(menubarsum)
		window.status = 'Iconlayer created';
		barlayer = new Array(menubarsum)
		window.status = 'Barlayer created';
		uplayer = new Array(menubarsum)
		window.status = 'Uplayer created';
		downlayer = new Array(menubarsum)
		window.status = 'Downlayer created';
		for (var i=0; i<menubarsum; i++) {
			window.status = 'Layer' + i + ' init';
			menulayer[i] = new DynLayer("menulayer" + i + "Div")
			menulayer[i].slideInit()
			iconlayer[i] = new DynLayer("iconlayer" + i + "Div", "menulayer" + i + "Div")
			iconlayer[i].slideInit()
			if (menuIconWidth[i] > document.body.clientWidth) {
				iconlayer[i].setWidth(menuIconWidth[i])
				if (menusmallicon) 
					iconX[i] = 0
				else
					iconX[i] = (document.body.clientWidth-menuIconWidth[i])/2
			} else {
				iconlayer[i].setWidth(document.body.clientWidth)
				iconX[i] = 0
			}
			iconlayer[i].moveTo(iconX[i], menubarheight)
			barlayer[i] = new DynLayer("barlayer" + i + "Div", "menulayer" + i + "Div")
			barlayer[i].slideInit()
			uplayer[i] = new DynLayer("uplayer" + i + "Div", "menulayer" + i + "Div")
			uplayer[i].slideInit()
			downlayer[i] = new DynLayer("downlayer" + i + "Div", "menulayer" + i + "Div")
			downlayer[i].slideInit()
		}
		menureload()
		window.status = '';
	}
}

function menubarpush(num) {
	if (num != menuchoose && num >= 0 && num < menubarsum) {
	
		iconlayer[menuchoose].moveTo(iconX[menuchoose],menubarheight)
		menuscroll = 0
		scrolling = false
	
		for (var i=0; i <=num; i++) {
			menulayer[i].slideTo(0, i*menubarheight, menuinc, menuspeed)
		}
		nAdCornerOriginY = document.body.clientHeight;
		nAdCornerOriginY += document.body.scrollTop;
		for (var i=menubarsum-1; i>num; i--) {
			nAdCornerOriginY -= menubarheight
			menulayer[i].slideTo(0,nAdCornerOriginY, menuinc, menuspeed)
		}
		menuchoose = num
		menuscrollbar()
	}
}


function menureload() {
	window.status = 'Menu reloaded';
	nAdCornerOriginY = document.body.clientHeight;
	nAdCornerOriginY += document.body.scrollTop;
	for (var i=menubarsum-1; i>menuchoose; i--) {
		nAdCornerOriginY -= menubarheight
		menulayer[i].moveTo(0, nAdCornerOriginY)
	}
	for (var i=0; i<menubarsum; i++) {
		barlayer[i].setWidth(document.body.clientWidth)
		if (menuIconWidth[i] > document.body.clientWidth) {
			iconlayer[i].setWidth(menuIconWidth[i])
			if (menusmallicon) 
				iconX[i] = 0
			else
				iconX[i] = (document.body.clientWidth-menuIconWidth[i])/2
		} else {
			iconlayer[i].setWidth(document.body.clientWidth)
			iconX[i] = 0
		}
		iconlayer[i].moveX(iconX[i], menubarheight)
	}
	
	
	menuscrollbar()
	window.status = '';
}


function menuscrollbar() {
	iconareaheight = document.body.clientHeight-menubarheight*(menubarsum);
	iconrightpos = document.body.clientWidth-16-4;
	maxscroll = menuIconHeight[menuchoose] - iconareaheight
	
	
	
	if (maxscroll > 0) {
		if (menuscroll > 0) {
			uplayer[menuchoose].moveTo(iconrightpos, menubarheight+4) 
		} else {
			uplayer[menuchoose].moveTo(-20, 0)
		}
		if (menuscroll < maxscroll) {
			downlayer[menuchoose].moveTo(iconrightpos, iconareaheight+2)
		} else {
			downlayer[menuchoose].moveTo(-20, 0)
		}
	} else {
		if (menuscroll <= 0) 
			uplayer[menuchoose].moveTo(-20, 0)
		downlayer[menuchoose].moveTo(-20, 0)
	}
}


function menuscrollup() {
	if (menuscroll > 0) {
		scrolling = true
		menuscroll -= scrollinc
		iconlayer[menuchoose].moveTo(iconX[menuchoose], menubarheight-menuscroll)
		
		scrollTimerID = setTimeout("menuscrollup()", scrollspeed)
	} else {
		menuscrollstop()	
	}
	menuscrollbar()
	
}

function menuscrolldown() {
	if (menuscroll < maxscroll) {
		scrolling = true
		menuscroll += scrollinc
		if (menuscroll < maxscroll) {
			iconlayer[menuchoose].moveTo(iconX[menuchoose], menubarheight-menuscroll)
		} else {
			iconlayer[menuchoose].moveTo(iconX[menuchoose], menubarheight-maxscroll)
		}
		
		scrollTimerID = setTimeout("menuscrolldown()", scrollspeed)
	} else {
		menuscrollstop()	
	}

	menuscrollbar()
	
}

function menuscrollstop() {
	scrolling = false
	if (scrollTimerID) {
		clearTimeout(scrollTimerID)
		scrollTimerID = 0;
	}
	
}

