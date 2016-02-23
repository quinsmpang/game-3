
	//version: 1.0
	//Time   : 2002-11-19
	//Author : zjq
	//Object : 创建导入页面
	//Usages :  
	//       obj         : 调用本方法的对象
	//       iNoUseLine  : 在本Table中不记入循环的记录行
	//       sFile       : 文件名(包含访问该页面全路径,ip地址和端口号不写)
	//       sTitle      : 显示对话框的标题
	//       iWidth      : 显示对话框的宽度
	//       iHeight     : 显示对话框的高度
	//       sControl    : 显示对话框是否需要滚动条,yes:需要 no:不需要
	//       sFormName   : 父窗口需要操作的Form的Name属性
	//       sInputs     : 父窗口需要操作的Form的返回INPUT 名字,如果有多个字段,请用","分隔
	//       iMultiSelect: 显示框内的元素能否多选,必须是整数,1:可以多选(选择多个元素的返回值用逗号分隔),非1:不能多选
	//       chSplit     : 选择多个元素的返回值的分隔符号,可以为空字符或者多个字符,example:",","","|||",etc.
	//       sParam      : 传入的特殊参数,如果有多个值,用逗号分隔,string
	//       sDefault    : 待选择项的初始值,string
	//       sDefaultSplit    : 待选择项的初始值之间的分隔符号,string
	//       iList       : 是否要将传入的已经选择的选项值重新列出来,必须是int,1=需要列出来,非1=不需要列出来
	//       sHasSelected: 已经选择的选项值,string
	//       sSelectedSplit: 已经选择的选项值之间的分隔符号,string
	//       iMaxNum     : 最多选择的数量,必须是int,如果为0,则没有限制
	//       sIndexValue : 对象所在行在Form中的纪录数组的下标,string类型,必须当iNoUseLine=-1时候生效
	function createImportForm(obj,iNoUseLine,sFile,sTitle,iWidth,iHeight,sControl,sFormName,sInputs,iMultiSelect,chSplit,sParam,sDefault,sDefaultSplit,iList,sHasSelected,sSelectedSplit,iMaxNum,sIndexValue)
	{
		if(obj!=null)
		{
   		var sindex="";
   		if(iNoUseLine==-1)
   		{
   		   sindex = sIndexValue;
   		}else
   		{
      		var rowCount = obj.parentElement.parentElement.parentElement.parentElement.rows.length-iNoUseLine;
      		var rowNum=obj.parentElement.parentElement.rowIndex-1;
      		if(rowCount>1){sindex = "[" + rowNum + "]";}	
   	   }
		   ModalDialog(sFile + '?imaxnum='+ iMaxNum +'&sssplit='+ sSelectedSplit +'&sselected='+ sHasSelected +'&ilist='+ iList +'&sdsplit='+sDefaultSplit+'&sdefault='+ sDefault +'&param='+ sParam +'&sindex='+ sindex +'&split='+ chSplit +'&multiselect='+ iMultiSelect +'&formname='+sFormName+'&inputs='+sInputs,sTitle,iWidth,iHeight,sControl);
		}
		else
		{alert("对象不能为空！");}
	}




	//version: 1.0
	//Time   : 2002-11-19
	//Author : zjq
	//Object : 创建普通模态页面
	//Usages :  
	//       sFile       : 文件名(包含访问该页面全路径,ip地址和端口号不写)
	//       sTitle      : 显示对话框的标题
	//       iWidth      : 显示对话框的宽度
	//       iHeight     : 显示对话框的高度
	//       sControl    : 显示对话框是否需要滚动条,yes:需要 no:不需要
	//       sParam      : 传入的特殊参数,如果有多个值,用逗号分隔,string
	
	
	function createNormalForm(sFile,sTitle,iWidth,iHeight,sControl,sParam)
	{
      ModalDialog(sFile + '?param=' + sParam,sTitle,iWidth,iHeight,sControl);
	}

	//version: 1.0
	//Time   : 2002-11-19
	//Author : zjq
	//Object : 创建普通模态页面
	//Usages :  
	//       sFile       : 文件名(包含访问该页面全路径,ip地址和端口号不写)
	//       sTitle      : 显示对话框的标题
	//       iWidth      : 显示对话框的宽度
	//       iHeight     : 显示对话框的高度
	//       sControl    : 显示对话框是否需要滚动条,yes:需要 no:不需要
	//       sParam      : 传入的特殊参数，跟在文件名之后的所有参数
	
	
	function createModelForm(sFile,sTitle,iWidth,iHeight,sControl,sParam)
	{
      ModalDialog(sFile + sParam,sTitle,iWidth,iHeight,sControl);
	}	

	//version: 1.0
	//Time   : 2002-11-19	
	//Author : zjq
	//Object : 创建导入页面
	//Usages :  
	function createImportFormNoInput(sFile,sTitle,iWidth,iHeight,sControl)
	{
		ModalDialog(sFile,sTitle,iWidth,iHeight,sControl);
	}


	//version: 1.0
	//Time   : 2002-11-19
	//Author : zjq
	//Object : 判断传入的对象是否是由对象的validator指定的类型
	//Usages :  
	//       obj      : 等待判断的对象
	//Return :
	//       true     : 是
	//       false    : 否
	function isType(obj)
	{
       var thePat=null;
       var v = obj.validator;    
       if(v!=null)
       {
         thePat = PatternsDict[v];
       }
       var gotIt = thePat.exec(obj.value); 
       if(gotIt){
         return (true);
       }else{
         return (false);
       }	
		
	}	
	