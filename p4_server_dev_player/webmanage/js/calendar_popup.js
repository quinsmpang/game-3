//定义全局变量
 //定义每个月的相应天数
 var G_daysInMonth = new Array(31, 28, 31, 30, 31, 30, 31, 31,30, 31, 30, 31);
 //定义日历月选择列表的值
 var G_months = new Array("一　月","二　月","三　月","四　月","五　月","六　月","七　月","八　月","九　月","十　月","十一月","十二月")
 //定义日历title英文星期的显示值
 var G_en_week= new Array("Su","Mo","Tu","We","Th","Fr","Sa");
 //定义日历title中文星期的显示值
 var G_cn_week= new Array("日","一","二","三","四","五","六");
 //定义全局日历对象句柄
 var G_Calendar = null;


//实现日历对象成员函数与日历对象的关联
calendar.prototype.construct=construct;
calendar.prototype.initialCalendar=initialCalendar;
calendar.prototype.updateSelectedDay=updateSelectedDay;
calendar.prototype.getToday=getToday;
calendar.prototype.changeYear=changeYear;
calendar.prototype.refresh=refresh;
calendar.prototype.inchYear=inchYear;
calendar.prototype.selectMonth=selectMonth;
calendar.prototype.setDay=setDay;
calendar.prototype.makeDaysGrid=makeDaysGrid;
calendar.prototype.show=show;
calendar.prototype.hide=hide;
calendar.prototype.setTitleBgColor=setTitleBgColor;
calendar.prototype.setTitleTxtColor=setTitleTxtColor;
calendar.prototype.setBodyBgColor=setBodyBgColor;
calendar.prototype.setBodyTxtColor=setBodyTxtColor;
calendar.prototype.setSelectedDayBgColor=setSelectedDayBgColor;
calendar.prototype.setSelectedDayTxtColor=setSelectedDayTxtColor;
calendar.prototype.setBodyBgFile=setBodyBgFile;
calendar.prototype.setHeadBgColor=setHeadBgColor;
calendar.prototype.setLanguage=setLanguage;
calendar.prototype.analyseDate=analyseDate;
calendar.prototype.outputBind=outputBind;
calendar.prototype.setTxtSize=setTxtSize;
calendar.prototype.makeHeadGrid=makeHeadGrid;
calendar.prototype.makeTitleGrid=makeTitleGrid;
calendar.prototype.makeBodyGrid=makeBodyGrid;
calendar.prototype.refreshbody=refreshbody;
calendar.prototype.setOutType=setOutType;


//创建日历对象函数
function calendar()
{
//创建日历对象
   //把当前对象句柄保存到全局变量中
    G_Calendar=this;
   //设置日历对象默认属性值
    this.constructed=false;
    this.stitlebgcolor ='\"#CCCCCC\"';
    this.sframecolor ='\"#000000\"';
    this.stitletxtcolor ='\"#000000\"';
    this.sheadbgcolor='\"CCCCCC\"'
    this.sbodytxtcolor ='\"#333333\"';
    this.sbodybgcolor='\"#FFFFFF\"';
    this.sSelecteddaytxtcolor ='\"#000000\"';
    this.sSelecteddaybgcolor ='\"#003366\"';
    this.slanguage='chinese';
    this.sbodybgfile='';
    this.outtype=0;
    this.TxtSize=1;
}

//日历对象成员函数
function construct(s_divname,s_boxname,arrhidedivs)
{  
//日历构造函数
	//判断是否有构造了的日历，有则隐藏该日历 
     if (this.constructed==false)
        this.constructed=true;
     else
        this.hide();
       //对需隐藏的其他<div>对象数组进行处理
      if (arguments.length!=3 && arguments.length!=2)
    {
      alert("create calendar object error!");
      return false; 
    }
     if (arguments.length==3)
    this.arrhidediv=arrhidedivs;
     else
    this.arrhidediv=null;
      //初始化日历
    this.initialCalendar(s_divname,s_boxname);
      //隐藏新构造的日历
    this.hide();
}

function initialCalendar(eltName,formElt)
{
//日历处始化函数
	//把<div>日历容器，<form>_<input>输出文本框绑定到全局日历
 	//var x = formElt.indexOf('.');
  	//var formName = formElt.substring(0,x);
  	//var formEltName = formElt.substring(x+1);
  	this.outbox=formElt;  //document.forms[formName].elements[formEltName];
  	this.handle=document.all[eltName];
        this.handlename=eltName; 
  	this.styleOfHandleOfDiv=document.all[eltName].style;
}

function hide()
{
//隐藏日历
	  //显示被隐藏的其他<div>对象，隐藏日历
   if (this.arrhidediv!=null)
  {
   for(var i=0;i<this.arrhidediv.length;i++)
   {
    var theName = this.arrhidediv[i];
    var theElt = document.all[theName].style 
    if(theElt == null) break;
    theElt.visibility = 'visible';
   }
  }
   this.styleOfHandleOfDiv.visibility = 'hidden'; 
   
}


function show() 
{
//显示日历
  //从输出文本框中解析日期
  this.analyseDate();
    //更新日历日期相关信息
     this.updateSelectedDay();
  //把当前(正确)的日期绑定到输出文本框    
  this.outputBind();
  //刷新日历
  this.refresh();
  //隐藏需隐藏的其他<div>对象，显示日历
  if (this.arrhidediv!=null)
  {
   for(var i=0;i<this.arrhidediv.length;i++)
   {
    var theName = this.arrhidediv[i];
    var theElt = document.all[theName].style 
    if(theElt == null) break;
    theElt.visibility = 'hidden';
   }
  }
   this.styleOfHandleOfDiv.visibility = 'visible'; 
}

function refresh()
{
//刷新日历
	//生成日历字符串
	var daysGrid = this.makeDaysGrid();
	//把该字符串附该日历容器的innerHTML属性
	this.handle.innerHTML=daysGrid;
	
}



function analyseDate()
{ 
//解析文本框中的日期  
     //根据输出文本的格式，解析文本框中的日期
     switch(this.outtype){
      case 0:
    var x = this.outbox.value.indexOf('\-');
	var year = this.outbox.value.substring(0,x);
	var substr = this.outbox.value.substring(x+1);
	x = substr.indexOf('\-');
   	var month = substr.substring(0,x);
    var day = substr.substring(x+1);
       break;
      default :
        var x = this.outbox.value.indexOf('\/');
	var month = this.outbox.value.substring(0,x);
	var substr = this.outbox.value.substring(x+1);
	x = substr.indexOf('\/');
   	var day = substr.substring(0,x);
        var year = substr.substring(x+1);
    }
        month=parseInt(month);
        month=month - 1;                       
        day=parseInt(day);
        year=parseInt(year); 
    //日期正确则把将该值设置为日历当前日期，否则取当天为日历当前日期    
   if ( month<=11 && month>=0 
      &&  day<=31 && day>=1 
      && year<2100 && year>1900)
   {  
     this.selectedDay=day;
     this.selectedMonth=month;
     this.selectedYear=year;
   }
   else
   {
     this.getToday();
   }
  
    return true;
}

function outputBind()
{
//绑定选择的日期到输出文本框
   //根据输出格式，绑定选择的日期到输出文本框
   switch(this.outtype)
   {
   	case 0:
        this.outbox.value = this.selectedYear + '\-' + (this.selectedMonth+1) + '\-'  +  this.selectedDay;
        break;
        default:
        this.outbox.value = (this.selectedMonth+1) + '\/' + this.selectedDay + '\/'  +  this.selectedYear;
		
   }
}

function getToday()
{
//获取当天的日期
   //获取当天的日期(年，月，日)
   var now = new Date();
   this.selectedYear = now.getFullYear();
   this.selectedMonth = now.getMonth();
   this.selectedDay = now.getDate();
}

function updateSelectedDay()
{
// 更新日历日期相关信息
     // 如果选择的日期月份为二月份，判断是否为闰年，并给相应的给该月的天数附29或28，不是二月份则从数组中得到该月的相应天数
     if (this.selectedMonth == 1)
     {
	 this.daysOfSelectedMonth=((0 == this.selectedYear % 4) && (0 != (this.selectedYear % 100))) ||
            (0 == this.selectedYear % 400) ? 29 : 28;
	  }
     else
	 {
       this.daysOfSelectedMonth=G_daysInMonth[this.selectedMonth];
	  }
    // 判断选中的日（注：年、月、日中的日）是否大于该月的天数，是则将该月的天数附给选中的日    
	if (this.selectedDay>this.daysOfSelectedMonth)
	   {
	    this.selectedDay=this.daysOfSelectedMonth;
	   }
	//获取该月1号是一星期的第几天  
	 var DayOfFirstMonth= new Date(this.selectedYear,this.selectedMonth,1);
	 var starDay=DayOfFirstMonth.getDay();
	 
	 this.selectedDayOfMonthOfFirstSunday=7-starDay+1;
}

function changeYear(year) 
{
//年改变后刷新日历
   //设置当前年
   this.selectedYear=parseInt(year);
   //绑定到输出，更新相关日期信息，并刷新日历
   this.outputBind();
   this.updateSelectedDay();
   this.refresh();
}

function inchYear(delta)
{
//年调整后刷新日历
   //计算新的年份
    this.selectedYear =this.selectedYear + delta;
    //绑定到输出，更新相关日期信息，并刷新日历
    this.outputBind();
    this.updateSelectedDay();	
    this.refresh();
}

function selectMonth(cdmonth)
{
//月份调整后刷新日历
   //设置新的月份
   this.selectedMonth=parseInt(cdmonth);
   //绑定到输出，更新相关日期信息，并刷新日历
   this.outputBind();
   this.updateSelectedDay();	
   this.refresh();
}

function setDay(day) 
{
//设置选中的日，并绑定到输出
   //设置选中的日，并绑定到输出
   this.selectedDay=day;
   this.outputBind();
   
      //更新日期相关信息，刷新日历
      //this.updateSelectedDay();
      this.refreshbody();
      //this.refresh();
   //this.hide();
   self.close();
}

	
function setTitleBgColor(color)
{  
//设置title背景颜色
   //设置title背景颜色
   if (color.length==7 && color.substring(0,1)=='#')
    {
	 this.stitlebgcolor= '\"'+ color + '\"';
   	 return true;
	 }
  
   return false ;
}

function setBodyBgColor(color)
{  
//设置body背景颜色
	//设置body背景颜色
   if (color.length==7 && color.substring(0,1)=='#')
    {
	 this.sbodybgcolor= '\"'+ color + '\"';
   	 return true;
    }
  
   return false ;
}

function setTitleTxtColor(color)
{ 
//设置titli文本颜色 
	//设置titli文本颜色
   if (color.length==7 && color.substring(0,1)=='#')
    {
	 this.stitletxtcolor= '\"'+ color + '\"';
   	 return true;
    }
  
   return false ;
}

function setBodyTxtColor(color)
{  
//设置body文本颜色
	//设置body文本颜色
   if (color.length==7 && color.substring(0,1)=='#')
    {
	 this.sbodytxtcolor= '\"'+ color + '\"';
   	 return true;
	 }
  
   return false ;
}

function setSelectedDayBgColor(color)
{
//设置当前日的背景颜色  
	//设置当前日的背景颜色
   if (color.length==7 && color.substring(0,1)=='#')
    {
	 this.sSelecteddaybgcolor= '\"'+ color + '\"';
   	 return true;
	 }
  
   return false ;
}

function setSelectedDayTxtColor(color)
{
//设置当前日的文本颜色 
	//设置当前日的文本颜色
   if (color.length==7 && color.substring(0,1)=='#')
    {
	 this.sSelecteddaytxtcolor= '\"'+ color + '\"';
   	 return true;
	 }
  
   return false ;
}

function setHeadBgColor(color)
{
//设置head的背景颜色
	//设置head的背景颜色
   if (color.length==7 && color.substring(0,1)=='#')
    {
	 this.sheadbgcolor= '\"'+ color + '\"';
   	 return true;
	 }
  
   return false ;
}

function setTxtSize(size)
{
//设置日历文本的字体大小
	//设置日历文本的字体大小
   if (size>=1 && size<=10)
   { 
     this.TxtSize=size;
     return true;
   }
   return false;
}
   

function setBodyBgFile(file)
{ 
//设置背景图片文件 
	//设置背景图片文件
    this.sbodybgfile= file;
}

function setOutType(type)
{
//设置输出日期的格式  
	//设置输出日期的格式
   if (type==1)
    this.outtype = 1;
   else
    this.outtype = 0;
}


function setLanguage(language)
{
//设置title的中文显示或英文显示
	//设置title的中文显示或英文显示
    if (language=='english'||language=='e'||language=='E'||language=='ENGLISH'||language=='English')
     {  this.slanguage='english'; }
	else
	 {  this.slanguage='chinese'; }
}

function makeHeadGrid()
{
 
   var daysGrid;

 //生成日历head的<table>对象，并设置日历head的背景颜色
    daysGrid='<table><tr>';
    //生成月份选择列表
    daysGrid=daysGrid + '<td>';
     var monthString='<select name="monthselect"   size="1" onchange=" ' + 'G_Calendar.selectMonth(this.options[this.selectedIndex].value)">';
	 for (var i=0;i<12;++i)
	 {
	 //并根据日历月份的当前值，来设置月份选择列表的选中值
	    if  (i==this.selectedMonth)
	    { monthString=monthString + '<option value=' + i + ' Selected> ' + G_months[i] + '</option>';}
	    else 
	    { monthString=monthString + '<option value=' + i + '  > ' + G_months[i] + '</option>';}
	 }
        monthString=monthString + '</Selected>'
	daysGrid=daysGrid + monthString;
	daysGrid=daysGrid + '</td>'
	
	//生成年左微调按钮
	  daysGrid=daysGrid + '<td height="28" width="16"> '
    daysGrid=daysGrid + '<img src="../images/btn_yearsub.gif" style="cursor:hand"  align="absmiddle" alt="减少年数" onclick=\'' + 'G_Calendar.inchYear(' +  -1  + ')\'>';
    daysGrid=daysGrid + '</td>'

    //生成年显示输入框
    daysGrid=daysGrid + '<td height="28" width="58"> '
    daysGrid=daysGrid + '<input type="text" name="yearinput" maxlength="4" size="8" value=' + this.selectedYear + ' onchange="' + 'G_Calendar.changeYear(this.value)">'
    daysGrid=daysGrid + '</td>'

    //生成年右微调按钮
    daysGrid=daysGrid + ' <td height="16" width="9"> '
    daysGrid=daysGrid + ' <img src="../images/btn_yearadd.gif" style="cursor:hand" class="hand" align="absmiddle" alt="增加年数" onclick=\'' + 'G_Calendar.inchYear( ' + 1  + ') \' >';
    daysGrid=daysGrid + ' </td>'

    //生成日历隐藏按钮
    daysGrid=daysGrid + ' <td height="16" width="9"><img onclick =\'' + 'self.close()\' src="../images/x.gif" style="cursor:hand" alt="关闭" align="absmiddle" class="hand" alt="关闭">'
    daysGrid=daysGrid + ' </td></tr></table>' 
   
    return daysGrid;	
}

function makeTitleGrid()
{
   /////////////////////
     //生成日历title的<table>对象，并设置日历title的背景颜色
   
   var myDaysGird = '';
   
    myDaysGird=myDaysGird +   '<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%"><tr bgcolor=' + this.stitlebgcolor + '>';
    //根据日历的属性值，生成中文日历title或英文日历title
   var weekString='';
    if (this.slanguage=='chinese')
	{
	  for (var i=0;i<7;++i)
	  {
	   weekString=weekString + '<td height="21">';
           weekString=weekString + '<div align="center"><font color='+ this.stitletxtcolor +'size=' + this.TxtSize + '>' +G_cn_week[i]+ '</font>';
           weekString=weekString + '</td>';
          }
    
         }
    else
	{      
	  for (var i=0;i<7;++i)
	  {
	    weekString=weekString + '<td height="21">';
            weekString=weekString + '<div align="center"><font color='+ this.stitletxtcolor +'size=' + this.TxtSize + '>' +G_en_week[i]+ '</font>';
            weekString=weekString + '</td>';
          }
	}
 
  myDaysGird = myDaysGird + weekString;
  myDaysGird = myDaysGird + '</tr></table>';
  
   return myDaysGird;
}


function makeBodyGrid()
{
   var daysGrid='';
   
       //如果存在日历背景图片文件，则设置其日历背景图片，否则设置日历body的背景颜色
        if (this.sbodybgfile=='')
		{daysGrid='<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%"  bgcolor='  +  this.sbodybgcolor + '  >';}
		else
		{daysGrid='<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%"  background=" ' + this.sbodybgfile + ' ">';}
		
		var dayOfMonth=0;
       for (var intWeek = 0; intWeek < 6; ++intWeek) 
	{
        //输出一行日期
           daysGrid=daysGrid + ' <tr>'
        for (var intDay = 0; intDay < 7; ++intDay) 
		{
	   //输出一个日期
	       //计算当前输出日期
            dayOfMonth = (intWeek * 7) + intDay + this.selectedDayOfMonthOfFirstSunday - 7;
    	       //输出日期dayOfMonth小于零，则输出空格
	       //输出日期dayOfMonth大于零，并且小于该月的天数，则输出dayOfMonth；否则没有输出
	       //输出日期dayOfMonth等于被选中的日期selectedday，则设置其背景颜色，输出dayOfMonth
    	       
    	       //输出日期dayOfMonth等于被选中的日期selectedday，则设置其背景颜色
    	      
    	       if ( this.selectedDay == dayOfMonth) 
	          { daysGrid=daysGrid + ' <td bgcolor='+ this.sSelecteddaybgcolor +'>';}
		    else 
			  { daysGrid=daysGrid + '<td>' ; }
		    if (dayOfMonth <= 0) 
		    //输出日期dayOfMonth小于等于零，则输出空格
		      { daysGrid += "&nbsp;&nbsp;"; }
		      //输出日期dayOfMonth大于零，并且小于该月的天数，则输出dayOfMonth；否则没有输出
		      else if (dayOfMonth <= this.daysOfSelectedMonth)
			  {
		         //生成输出日期的超链接，关联全局日历的setDay()处理函数
	                 //设置输出日期dayOfMonth的字体颜色、大小
		         if ( this.selectedDay == dayOfMonth)
				   {
		              daysGrid += '<a  href="javascript:G_Calendar.setDay(';
		              daysGrid += dayOfMonth + ' )\" style="text-decoration:none" > ' ;
		              daysGrid += '<div align="center"><font color=' + this.sSelecteddaytxtcolor + 'size=' + this.TxtSize + '>' + dayOfMonth + '</font></div></a>';
		            } 
				 else
				   { 
		              daysGrid += '<a href="javascript:G_Calendar.setDay(';
		              daysGrid += dayOfMonth + ' )\" style="text-decoration:none"> ' ;
		              daysGrid += '<div align="center"><font color=' + this.sbodytxtcolor + 'size=' + this.TxtSize + '>' + dayOfMonth + '</font></div></a>';
					}
	           }//elseif
		    daysGrid += '</td>';  
        }//forloop
        if (dayOfMonth <= this.daysOfSelectedMonth)
		{  daysGrid += "</tr>" }
    }//forloop                

    daysGrid=daysGrid + '              </table>'
	
   
    return daysGrid ;	
}

function makeDaysGrid()
{
   var daysGrid='';
   //生成为设置框架颜色的<table>对象
    daysGrid=daysGrid + '<table border=0 width="75" border="0"  cellspacing="0" bgcolor='+ this.sframecolor +'align="center">'
    daysGrid=daysGrid + '<tr><td >'
	
	//生成存放日历的head、title和body的<table>对象
    daysGrid=daysGrid + '<table border=0 width="75" border="0"  cellspacing="0" bgcolor='  +  this.sheadbgcolor + '  align="center"> '
	
	//生成日历head
   	daysGrid=daysGrid + '<tr ><td>'
    daysGrid=daysGrid + this.makeHeadGrid();
	daysGrid=daysGrid + '</td></tr>'
	
	//生成日历title
    daysGrid=daysGrid + '<tr ><td>'
    daysGrid=daysGrid + this.makeTitleGrid();
	daysGrid=daysGrid + '</td></tr>'
	
	//生成日历body
	daysGrid=daysGrid + '<tr ><td><div id="CB_' + this.handlename + '">'
    daysGrid=daysGrid + this.makeBodyGrid();
    daysGrid=daysGrid + '</div></td></tr>'
	daysGrid=daysGrid + '</table>'
	
    daysGrid=daysGrid + '</td></tr>'
	daysGrid=daysGrid + '</table>'

    
    return daysGrid;
  
}

function refreshbody()
{
//刷新日历body
   var strbody = 'CB_'+ this.handlename;
   document.all[strbody].innerHTML=this.makeBodyGrid();
   G_Calendar.hide();
}

var a= new calendar();
function getdate(obj)
{
     if (G_Calendar==null)
    { return false; }
  
        G_Calendar.construct('daysOfMonth',obj);
        G_Calendar.setTitleBgColor('#E3F7EB');
    	G_Calendar.setTitleTxtColor('#000000');
    	G_Calendar.setSelectedDayTxtColor('#FFFFFF');
    	G_Calendar.setSelectedDayBgColor('#224033');
	    G_Calendar.setBodyBgColor('#8DB597');
	    G_Calendar.setBodyTxtColor('#F4F4DC');
        G_Calendar.setHeadBgColor('#B6D5BE');
                
	    G_Calendar.setLanguage('chinese');
	    G_Calendar.setTxtSize(2);
	    G_Calendar.setBodyBgFile('../images/calendar_back.gif');

        G_Calendar.show();
}