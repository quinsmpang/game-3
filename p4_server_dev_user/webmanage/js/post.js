var UbbUsed=0;
var Dvbbs_bIsIE5		= document.all;
var text_enter_url      = "请输入连接网址";			var error_no_useuserName= "请输入定员用户名";				var error_no_points		= "您必须输入数值,如：1000";		var text_enter_usemoney	= "请输入数值,如：1000 (即只有用户支付了1000点金钱才能浏览该内容!)";
var text_enter_txt		= "请输入连接说明";			var text_enter_image	= "请输入图片网址";				var text_enter_sound	= "请输入声音文件网址";			var text_enter_money	= "请输入数值,如：1000 (即限制金钱在1000点以下的用户不能浏览该内容!)";
var text_enter_swf		= "请输入FLASH动画网址";		var text_enter_ra		= "请输入Real音乐网址";			var text_enter_rm		= "请输入Real影片网址";			var text_enter_points	= "请输入数值,如：1000 (即限制积分在1000点以下的用户不能浏览该内容!)";
var text_enter_wmv		= "请输入Media影片网址";		var text_enter_wma		= "请输入Media音乐网址";			var text_enter_mov		= "请输入QuickTime音乐网址";		var text_enter_usercp	= "请输入数值,如：1000 (即限制魅力值在1000点以下的用户不能浏览该内容!)";
var text_enter_sw		= "请输入shockwave音乐网址";	var text_enter_email    = "请输入邮件网址";				var error_no_url		= "您必须输入网址";				var text_enter_power	= "请输入数值,如：1000 (即限制威望在1000点以下的用户不能浏览该内容!)";
var error_no_txt        = "您必须连接说明";			var error_no_title      = "您必须输入首页标题";			var error_no_email		= "您必须输入邮件网址";			var text_enter_post		= "请输入数值,如：1000 (即限制发表文章在1000点以下的用户不能浏览该内容!)";
var error_no_gtxt		= "必须输入文字！";			var text_enter_guang1	= "文字的长度、颜色和边界大小";		var text_enter_guang2	= "要产生效果的文字！";			var text_enter_useuserName	= "请输入定员用户名(多个的用英文豆号分开)";
var error_no_gset		= "必须正确按照各式输入！";

function commentWrite(NewCode) {
IframeID.document.body.innerHTML+=NewCode;
IframeID.focus();
return;
}

function storeCaret(text) { 
	if (text.createTextRange) {
		text.caretPos = document.selection.createRange().duplicate();
	}
        if(event.ctrlKey && window.event.keyCode==13){i++;if (i>1) {alert('帖子正在发出，请耐心等待！');return false;}this.document.form.submit();}
}
function AddText(text) {
	if (IframeID.document.body.createTextRange && IframeID.document.body.caretPos) {      
		var caretPos = IframeID.document.body.caretPos;      
		caretPos.text = caretPos.text.charAt(caretPos.text.length - 1) == ' ' ?
		text + ' ' : text;
	}
	else IframeID.document.body.innerHTML += text;
	IframeID.focus(caretPos);
}

function Curl() {
	var FoundErrors = '';
	var enterURL   = prompt(text_enter_url, "http://");
	var enterTxT   = prompt(text_enter_txt, enterURL);
	if (!enterURL)    {
	FoundErrors += "\n" + error_no_url;
	}
	if (!enterTxT)    {
	FoundErrors += "\n" + error_no_txt;
	}
	if (FoundErrors)  {
	alert("错误！"+FoundErrors);
	return;
	}
	var ToAdd = "[URL="+enterURL+"]"+enterTxT+"[/URL]";
	IframeID.document.body.innerHTML+=ToAdd;
	IframeID.focus();
}

//ubb标记
function Dv_ubb(code,value) {
	if (value)
	{
		fontbegin="["+code+"="+value+"]";
	}else{
		fontbegin="["+code+"]";
	}
	fontend="[/"+code+"]";
	fontchuli();
}

//特殊帖
function Dv_Signal(code,addtype) {
	var promptinfo='';
	var error_info='';
	var default_info='1000';
	var addtype=0;
	switch (code)
	{
		case 'Point':
			promptinfo=text_enter_points;
			error_info=error_no_points;
			break;
		case 'Money':
			promptinfo=text_enter_money;
			error_info=error_no_points;
			break;
		case 'UseMoney':
			promptinfo=text_enter_usemoney;
			error_info=error_no_points;
			break;
		case 'UserName':
			promptinfo=text_enter_useuserName;
			error_info=error_no_useuserName;
			default_info="";
			UbbUsed=UbbUsed+1
			break;
		case 'UserCP':
			promptinfo=text_enter_usercp;
			error_info=error_no_points;
			break;
		case 'Power':
			promptinfo=text_enter_power;
			error_info=error_no_points;
			break;
		case 'Post':
			promptinfo=text_enter_post;
			error_info=error_no_points;
			break;
		case 'SOUND':
			addtype=1;
			promptinfo=text_enter_sound;
			error_info=error_no_url;
			default_info="http://";
			break;
		case 'IMG':
			addtype=1;
			promptinfo=text_enter_image;
			error_info=error_no_url;
			default_info="http://";
			break;
		case 'EMAIL':
			addtype=1;
			promptinfo=text_enter_email;
			error_info=error_no_email;
			default_info="";
			break;
	}
	if (code=="UserName"&&UbbUsed>1)
	{
		alert("用户定员帖限制只能出现1次！");
		ischeck=false;
	}else{
	var enter_prompt  =prompt(promptinfo,default_info);
	if (!enter_prompt) {
		alert("错误！" + "\n" + error_info);
		return;
	}
	if (addtype==1)
	{
		var ToAdd = "[" + code + "]" + enter_prompt + "[\/" + code + "]";
		IframeID.document.body.innerHTML+=ToAdd;
	}else{
		var ToAdd = "[" + code + "=" + enter_prompt + "]" + IframeID.document.body.innerHTML + "[\/" + code + "]";
		IframeID.document.body.innerHTML=ToAdd;
		//Dvbbs_specialtype("[" + code + "=" + enter_prompt + "]","[\/" + code + "]");
	}
	}
	IframeID.focus();
}

function Cswf() {    
	txt2=prompt("flash宽度，高度","500,350"); 
	if (txt2!=null) {
		txt=prompt("Flash 文件的地址","http://");
		if (txt!=null) {
			if (txt2=="") {             
			AddTxt="[flash=500,350]"+txt;
			AddText(AddTxt);
			AddTxt="[/flash]";
			AddText(AddTxt);
			} else {
			AddTxt="[flash="+txt2+"]"+txt;
			AddText(AddTxt);
			AddTxt="[/flash]";
			AddText(AddTxt);
			}
	    }
    }
}

function Crm() {
	txt2=prompt("视频的宽度，高度，播放参数\r(播放参数：0＝手动播放，1＝自动播放)","500,350,1"); 
	if (txt2!=null) {
		txt=prompt("视频文件的地址","请输入");
		if (txt!=null) {
			if (txt2=="") {
				AddTxt="[rm=500,350,0]"+txt;
				AddText(AddTxt);
				AddTxt="[/rm]";
				AddText(AddTxt);
			} else {
				AddTxt="[rm="+txt2+"]"+txt;
				AddText(AddTxt);
				AddTxt="[/rm]";
				AddText(AddTxt);
			}
		}
	}
}

function Cwmv() {
	txt2=prompt("视频的宽度，高度，播放参数\r(播放参数：0＝手动播放，1＝自动播放)","500,350,1"); 
	if (txt2!=null) {
		txt=prompt("视频文件的地址","请输入");
		if (txt!=null) {
			if (txt2=="") {
			AddTxt="[mp=500,350,0]"+txt;
			AddText(AddTxt);
			AddTxt="[/mp]";
			AddText(AddTxt);
			} else {
			AddTxt="[mp="+txt2+"]"+txt;
			AddText(AddTxt);
			AddTxt="[/mp]";
			AddText(AddTxt);
			}
		}
	}
}

function Cmov() {
	txt2=prompt("视频的宽度，高度","500,350"); 
	if (txt2!=null) {
		txt=prompt("视频文件的地址","请输入");
		if (txt!=null) {
			if (txt2=="") {
			AddTxt="[qt=500,350]"+txt;
			AddText(AddTxt);
			AddTxt="[/qt]";
			AddText(AddTxt);
			} else {
			AddTxt="[qt="+txt2+"]"+txt;
			AddText(AddTxt);
			AddTxt="[/qt]";
			AddText(AddTxt);
			}
		}
	}
}

function Cdir() {
	txt2=prompt("Shockwave文件的宽度，高度","500,350"); 
	if (txt2!=null) {
		txt=prompt("Shockwave文件的地址","请输入地址");
		if (txt!=null) {
			if (txt2=="") {
			AddTxt="[dir=500,350]"+txt;
			AddText(AddTxt);
			AddTxt="[/dir]";
			AddText(AddTxt);
			} else {
			AddTxt="[dir="+txt2+"]"+txt;
			AddText(AddTxt);
			AddTxt="[/dir]";
			AddText(AddTxt);
			}
		}
	}
}

function Cfanzi() {
fontbegin="[xray]";
fontend="[/xray]";
fontchuli();
}

function paste(text) {
	if (opener.IframeID.document.body.createTextRange && opener.IframeID.document.body.caretPos) {      
		var caretPos = opener.IframeID.document.body.caretPos;      
		caretPos.text = caretPos.text.charAt(caretPos.text.length - 1) == ' ' ?
		text + ' ' : text;
	}
	else opener.IframeID.document.body.innerHTML += text;
	opener.IframeID.document.body.focus(caretPos);
}

function fontchuli(){
	if ((IframeID.document.selection)&&(IframeID.document.selection.type == "Text")) {
	var range = IframeID.document.selection.createRange();
	var ch_text=range.text;
	range.text = fontbegin + ch_text + fontend;
	} 
	else {
	IframeID.document.body.innerHTML=fontbegin+IframeID.document.body.innerHTML+fontend;
	IframeID.focus();
	}
}

function Cguang() {
	var FoundErrors = '';
	var enterSET   = prompt(text_enter_guang1, "255,red,2");
	var enterTxT   = prompt(text_enter_guang2, "文字");
	if (!enterSET)    {
	FoundErrors += "\n" + error_no_gset;
	}
	if (!enterTxT)    {
	FoundErrors += "\n" + error_no_gtxt;
	}
	if (FoundErrors)  {
	alert("错误！"+FoundErrors);
	return;
	}
	var ToAdd = "[glow="+enterSET+"]"+enterTxT+"[/glow]";
	IframeID.document.body.innerHTML+=ToAdd;
	IframeID.focus();
}
function Cying() {
	var FoundErrors = '';
	var enterSET   = prompt(text_enter_guang1, "255,blue,1");
	var enterTxT   = prompt(text_enter_guang2, "文字");
	if (!enterSET)    {
	FoundErrors += "\n" + error_no_gset;
	}
	if (!enterTxT)    {
	FoundErrors += "\n" + error_no_gtxt;
	}
	if (FoundErrors)  {
	alert("错误！"+FoundErrors);
	return;
	}
	var ToAdd = "[SHADOW="+enterSET+"]"+enterTxT+"[/SHADOW]";
	IframeID.document.body.innerHTML+=ToAdd;
	IframeID.focus();
}

//清除表单
function ClearReset()
{
	IframeID.document.body.innerHTML='';
	IframeID.focus();
}

//检查UBB使用次数
function CheckUbbUse(code,n,content){
	var tempstr=0;
	var strubbcode=/\[\/username\]/gi;
	chktemp=content.match(strubbcode);
	if (chktemp!=null)
	{
		tempstr=tempstr+chktemp.length;
	}
	if (tempstr>n)
	{
		ischeck=false;
		UbbUsed=tempstr;
		alert("用户定员帖限制只能出现"+n+"次！");
	}
}

//ChkPostMoney
function ChkPostMoney(n)
{
	var DivInfo = document.getElementById('PostMoneyInfo');
	var DivBuy_Setting = document.getElementById('Buy_setting');
	Dvform.ToMoney.value = "";
	Dvform.ToMoney.disabled = false;
	if (n!="")
	{
		switch (n)
		{
			case '0':
				Dvform.ToMoney.value = 1;
				DivInfo.innerHTML="自定义送金币数，发帖后暂时扣除该用户相应金币，用户可对不同回复用户在金币范围内分别赠送，赠送完毕可结帖，结帖后剩余金币(未送出)还入用户数据中";
				DivBuy_Setting.style.display="none";
				break;
			case '1':
				Dvform.ToMoney.disabled = true;
				DivInfo.innerHTML="回复用户可自定数量金币赠送给帖主。";
				DivBuy_Setting.style.display="none";
				break;
			case '2':
				Dvform.ToMoney.value = 1;
				DivInfo.innerHTML="发帖者可以定义帖子出售金币数量，浏览者需要支付金币购买才可以查看帖子全部内容。";
				if (DivBuy_Setting)
				{
					DivBuy_Setting.style.display="";
				}
				break;
		}
	}else{
		DivInfo.innerHTML="";
	}
}