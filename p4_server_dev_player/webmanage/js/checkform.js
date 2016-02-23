function getBytesLen(str)
{
	if(str == null) return 0;

	var theValue = str.toString();
	var nBytes = 0;
	for(i=0; i<theValue.length; i++)
	{
		if(theValue.charCodeAt(i) >= 0x1000)
		{
			nBytes++;
		}
		nBytes++;
	}
	return nBytes;
}

/**判断是否是合法的日期格式   -alex*/
function isDate(dateValue){
var tian=new Array();
var splitStr=null;
if(dateValue.indexOf("-")!=-1){
	splitStr="-";
}else{
return false;
}
var nianIndex=dateValue.indexOf(splitStr);
var nianStr=dateValue.substring(0,nianIndex);
if(nianStr.length!=4 || isNaN(nianStr)){return false;}
var yueIndex=dateValue.indexOf(splitStr,nianIndex+1);
var yueStr=dateValue.substring(nianIndex+1,yueIndex);
if((yueStr.length!=1 && yueStr.length!=2) || isNaN(yueStr)){return false;}

var dayStr=dateValue.substring(yueIndex+1,dateValue.length);
if((dayStr.length!=1 && dayStr.length!=2) || isNaN(dayStr)){return false;}

var nian=nianStr*1;
var yue=yueStr*1;
var day=dayStr*1;
//alert("年="+nian+"\r\n"+"月="+yue+"\r\n"+"日="+day+"\r\n");
if(nian % 100==0){
 if(nian % 400==0){
  tian[2]=29;
  niantag="闰年";}
 else
  {tian[2]=28;
  niantag="平年";}
 }else{
 if(nian % 4==0){
  tian[2]=29;
  niantag="闰年";}
 else
  {tian[2]=28;
  niantag="平年";}
 }
tian[1]=31;
tian[3]=31;
tian[4]=30;
tian[5]=31;
tian[6]=30;
tian[7]=31;
tian[8]=31;
tian[9]=30;
tian[10]=31;
tian[11]=30;
tian[12]=31;
if(nian<1){return false;}
if(yue>12 || yue<1){return false;}
if(day>tian[yue] || day<1){return false;}
return true;
}

function validateElement(obj){
if(obj.nocheck!=null && obj.nocheck=="true")return true;
var v = obj.validator;    // 获取其validator属性

var thePat=null;
if(v!=null){
thePat=PatternsDict[v];  // 选择验证用的正则表达式
}
var theValue=null;


//取值
 if(obj.tagName=="INPUT" && obj.type=="text"){
  theValue=trim(obj.value);
 }
 if(obj.tagName=="TEXTAREA"){
  theValue=trim(obj.value);
 }
 if(obj.tagName=="SELECT"){ 	 
  theValue=trim(obj.options[obj.selectedIndex].value);  
 }
if(theValue==null)return true;



//检测maxbytes属性
var maxbytes=obj.maxbytes;

 if(maxbytes==null && v!=null && v.substr(0,1)=="M"){   //没有maxbytes属性则取v的值
 maxbytes=v.substr(1,v.length-1);
 } 
 if(maxbytes!=null){   //验证字节长度 
   if(!isNaN(maxbytes)){	   
	   len=maxbytes*1;
			
		if(getBytesLen(theValue)>len)
		{if(obj.className!="errorinput"){
		  obj.orgclassName=obj.className;
		  obj.className="errorinput";
		}

		 obj.title="最大长度"+len+"字节";  
		 		 
		 return false;		
	}
	else
	{
		if(obj.tagName=="INPUT" && obj.type=="text")
		{
			if(obj.orgclassName!=null)
			{
			obj.className=obj.orgclassName;
			}
		}
		if(obj.tagName=="TEXTAREA")
		{
			if(obj.orgclassName!=null)
			{
			obj.className=obj.orgclassName;
			}
		}
		if(obj.tagName=="SELECT")
		{
		  obj.options[obj.selectedIndex].style.color="#000000";
		}
	}
  }
   
 }



 //检测must属性
 var must=obj.must;
 if(must!=null && must=="true"){
 if(theValue!=null && theValue==""){
	    if(obj.className!="errorinput"){
		  obj.orgclassName=obj.className;
		  obj.className="errorinput";
		}
		if(v!=null){
			obj.title=v;
		}else{
			obj.title="此项必填";
		}		
		if(obj.tagName=="SELECT"){
		obj.options[obj.selectedIndex].style.color="#FF0000";
	   }	   
	   return false;
	}else{
		
		if(obj.orgclassName!=null){
			  obj.className=obj.orgclassName;		  
		}		
		
		if(obj.tagName=="SELECT"){
		obj.options[obj.selectedIndex].style.color="#000000";
	   }
	}	
 }

 if(theValue==null || theValue==""){
   if(obj.orgclassName!=null){
		obj.className=obj.orgclassName;		  
	 }
	return true;
	}
 
 if(v=="date"){
 if(theValue!=null && theValue!=null){
	if(!isDate(theValue)){
		if(obj.className!="errorinput"){
		  obj.orgclassName=obj.className;
		  obj.className="errorinput";
		}
		obj.title="年-月-日";		
		
		if(obj.tagName=="SELECT"){
		obj.options[obj.selectedIndex].style.color="#FF0000";
		}
		return false;  
		}else{
		if(obj.orgclassName!=null){
			obj.className=obj.orgclassName;		  
		  }	
		
		if(obj.tagName=="SELECT"){
			obj.options[obj.selectedIndex].style.color="#000000";
			}
		}
		return true;
	}
 }


 //检测validator的正则表达式
 if(thePat==null)return true; 
 
 var gotIt = thePat.exec(theValue); // 用正则表达式验证elArr[i]的值 
  
 if(!gotIt){
	 if(obj.className!="errorinput"){
		  obj.orgclassName=obj.className;
		  obj.className="errorinput";
		}

   if(v!=null){
   obj.title=v
   }
   
   if(obj.tagName=="SELECT"){
   obj.options[obj.selectedIndex].style.color="#FF0000";
   }
   return false;

	}
	else
	{	
		if(obj.tagName=="INPUT" && obj.type=="text")
		{
			if(obj.orgclassName!=null)
			{
				obj.className=obj.orgclassName;		  
			}
		}
		if(obj.tagName=="TEXTAREA")
		{
			if(obj.orgclassName!=null)
			{
				obj.className=obj.orgclassName;		  
			}
		}
		 if(obj.tagName=="SELECT"){
		  obj.options[obj.selectedIndex].style.color="#000000"
		 }
		 return true;
   }
 return true; 
 }



function validateForm(theForm){
var falseTag=0;
var i;
var focusObj=null;
var len;
var elArr = theForm.elements;    // 将表单中的所有元素放入数组
for(i = 0; i < elArr.length; i++)	// 对于表单中的每一个元素...
 {    

 
 //检测nocheck属性
 if(elArr[i].nocheck=="true") continue;

 //取validator属性值及正则表达式
 var thePat=null;
 var v = elArr[i].validator;    // 获取其validator属性
 if(v!=null){
 thePat = PatternsDict[v];   // 选择验证用的正则表达式
 }

 //取值
 if(elArr[i].tagName=="INPUT" && elArr[i].type=="text"){
 var theValue=elArr[i].value;
 }
 if(elArr[i].tagName=="TEXTAREA"){
 var theValue=elArr[i].value;
 }
 if(elArr[i].tagName=="SELECT")
{
	if(elArr[i].selectedIndex<0)
	{
		var theValue="";	 
	}
	else
	{
		var theValue=elArr[i].options[elArr[i].selectedIndex].value;  
	}
 
 }
 if(theValue==null)continue;
//检测maxbytes属性
 var maxbytes=elArr[i].maxbytes;

 if(maxbytes==null && v!=null && v.substr(0,1)=="M"){   //没有maxbytes属性则取v的值
 maxbytes=v.substr(1,v.length-1);
 } 
 if(maxbytes!=null){   //验证字节长度 
   if(!isNaN(maxbytes)){	   
	   len=maxbytes*1;
			
		if(getBytesLen(theValue)>len)
		{if(elArr[i].className!="errorinput"){
		  elArr[i].orgclassName=elArr[i].className;
		  elArr[i].className="errorinput";
		}

		  elArr[i].title="最大长度"+len+"字节";
		  scrollObj=elArr[i];
		 if(focusObj==null){focusObj=elArr[i];}
		 if(elArr[i].tagName=="SELECT"){
			elArr[i].options[elArr[i].selectedIndex].style.color="#FF0000";
			}   
		 falseTag=1;
		continue;		
		}
		else
		{
		if(elArr[i].tagName=="INPUT" && elArr[i].type=="text")
		{
			if(elArr[i].orgclassName!=null)
			{
				elArr[i].className=elArr[i].orgclassName;		  
			}
		}
		if(elArr[i].tagName=="TEXTAREA")
		{
			if(elArr[i].orgclassName!=null)
			{
				elArr[i].className=elArr[i].orgclassName;		  
			}
		}
		if(elArr[i].tagName=="SELECT")
		{
			elArr[i].options[elArr[i].selectedIndex].style.color="#000000"
		}
	 }    
   }
   
 }

 //检测must属性
 var must=elArr[i].must;
 if(must!=null && must=="true"){
 if(theValue!=null && theValue=="" || (elArr[i].tagName=="SELECT" && elArr[i].selectedIndex==0 && elArr[i].options[elArr[i].selectedIndex].value=="0") ){
	    if(elArr[i].className!="errorinput"){
		  elArr[i].orgclassName=elArr[i].className;
		  elArr[i].className="errorinput";
		}
		if(v!=null){
			elArr[i].title=v;
		}else{
			elArr[i].title="此项必填";
		}
		scrollObj=elArr[i];
		if(focusObj==null){focusObj=elArr[i];}
		if(elArr[i].tagName=="SELECT"){
		elArr[i].options[elArr[i].selectedIndex].style.color="#FF0000";
	   }	   
	   falseTag=1;
	   continue;
	}else{
		
		if(elArr[i].orgclassName!=null){
			  elArr[i].className=elArr[i].orgclassName;		  
		}
		
		
		if(elArr[i].tagName=="SELECT"){
		elArr[i].options[elArr[i].selectedIndex].style.color="#387C8B";
	   }
	}	
 }

if(theValue==null || theValue==""){
   if(elArr[i].orgclassName!=null){
			  elArr[i].className=elArr[i].orgclassName;		  
	 }
	continue;
	}
 
 if(v=="date"){
 if(theValue!=null && theValue!=null){
	if(!isDate(theValue)){
		if(elArr[i].className!="errorinput"){
		  elArr[i].orgclassName=elArr[i].className;
		  elArr[i].className="errorinput";
		}
		elArr[i].title="年-月-日";
		
		scrollObj=elArr[i];
		if(focusObj==null){focusObj=elArr[i];}
		if(elArr[i].tagName=="SELECT"){
		elArr[i].options[elArr[i].selectedIndex].style.color="#FF0000";
	   }	   
	   falseTag=1;	   
		}else{
		if(elArr[i].orgclassName!=null){
			elArr[i].className=elArr[i].orgclassName;		  
		  }	
		
		if(elArr[i].tagName=="SELECT"){
			elArr[i].options[elArr[i].selectedIndex].style.color="#387C8B";
			}
		}
		continue;
	}
 }
 //检测validator的正则表达式
 if(thePat==null)continue; 
 
 var gotIt = thePat.exec(theValue); // 用正则表达式验证elArr[i]的值 
  
 if(!gotIt){
	 if(elArr[i].className!="errorinput"){
		  elArr[i].orgclassName=elArr[i].className;
		  elArr[i].className="errorinput";
		}

   if(v!=null){
   elArr[i].title=v
   }
   scrollObj=elArr[i];
   if(focusObj==null){focusObj=elArr[i];}
   if(elArr[i].tagName=="SELECT"){
   elArr[i].options[elArr[i].selectedIndex].style.color="#FF0000";
   }   
   falseTag=1;
   continue;
	}
	else
	{
		if(elArr[i].tagName=="INPUT" && elArr[i].type=="text")
		{
			if(elArr[i].orgclassName!=null)
			{
				elArr[i].className=elArr[i].orgclassName;
			}
		}
		if(elArr[i].tagName=="TEXTAREA")
		{
			if(elArr[i].orgclassName!=null)
			{
				elArr[i].className=elArr[i].orgclassName;
			}
		}
		if(elArr[i].tagName=="SELECT")
		{
			elArr[i].options[elArr[i].selectedIndex].style.color="#000000"
		} 
	continue;
   }
  
 }
 if(falseTag==1){
	scrollObj.scrollIntoView(false);
	alert("输入值不合规范");	
	if(focusObj.type!="hidden")
	{	
		focusObj.focus();
	}
	return false;
	}else{
	return true;
	}
}

function change2ErrorInput(theObj)
{
	if(theObj.className!="errorinput")
	{
		theObj.orgclassName=theObj.className;
		theObj.className="errorinput";
	}
}