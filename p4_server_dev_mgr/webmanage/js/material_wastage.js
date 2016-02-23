
	//范围：领用材料
	//增加一条数据行
	//iLine : 当前插入一条数据位置,-1表示插在最下面
	function insertWastageMaterial(iLine)
	{
		var tbobj=document.all["wastagematerialtable"];
		
		if(iLine==-1){var trobj=tbobj.insertRow();}
		else{var trobj=tbobj.insertRow(iLine);}
		trobj.className="nrbgc1";
		n=trobj.rowIndex-1;
		
		tdobj=trobj.insertCell();
		tdobj.align="left";
		tdobj.innerHTML='<img src="../images/icon_mt_undifine.gif" id="mtflag" title="尚未填写材料">';
		tdobj=trobj.insertCell();
		tdobj.align="left";
		tdobj.innerHTML='<input name="isdevice" type="hidden" value="no"><input name="dv_code" type="hidden" value=""><input name="dv_code_old" type="hidden" value=""><input name="sc_id" type="text" value="" class="input4" id="sc_id" size="10" onBlur="checkWastageSC(this)"  must="true"  validator="M8">';
		tdobj=trobj.insertCell();
		tdobj.align="left";
		tdobj.innerHTML='<input name="sc_alias" type="text" value="" class="input4" id="sc_alias" size="8" onBlur="checkWastageSC(this)">';
		tdobj=trobj.insertCell();
		tdobj.align="left";
		tdobj.innerHTML='<input name="sc_name" type="text" value="" class="input4" id="sc_name" size="15" readonly >';
		tdobj=trobj.insertCell();
		tdobj.align="left";
		tdobj.innerHTML='<input name="sc_type" type="text" value="" class="input4" id="sc_type" size="10" readonly >';
		tdobj=trobj.insertCell();
		tdobj.align="left";
		tdobj.innerHTML='<input name="sc_unit" type="text" value="" class="input4" id="sc_unit" size="6" readonly >';
		tdobj=trobj.insertCell();
		tdobj.align="left";
		tdobj.innerHTML='<input name="wt_quantity" type="text" value="" class="input4" id="wt_quantity" size="5" onBlur="checkWastageQuantity(this)" must="true" validator="Dn">';
		tdobj=trobj.insertCell();
		tdobj.align="left";
		tdobj.innerHTML='<input name="wt_unitprice" type="text" value="" class="input4" id="wt_unitprice" size="8" readonly >';
		tdobj=trobj.insertCell();
		tdobj.align="left";
		tdobj.innerHTML='<input name="wt_amount" type="text" value="" class="input4" id="wt_amount" size="8" must="true" validator="Num">';
		tdobj=trobj.insertCell();
		tdobj.align="center";
		tdobj.innerHTML='<img src="../images/icon_adddepart.gif" style="cursor:hand" alt="在当前位置插入1条空白记录" border="0" onclick=insertWastageMaterial(this.parentElement.parentElement.rowIndex)>&nbsp;<img src="../images/icon_sub2.gif" style="cursor:hand" alt="删除当前记录" border="0" onclick=deleteWastageMaterial(this.parentElement.parentElement.rowIndex)>';
	}


	//范围：领用材料
	//删除指定标记的一条数据行
	//iLine : 当前需要删除一条数据位置,-1表示删除最下面一行
	function deleteWastageMaterial(iLine)
	{
		var tbobj=document.all["wastagematerialtable"];

		if(iLine==-1)
		{
			if(tbobj.rows.length>1){tbobj.deleteRow(tbobj.rows.length-1);}
		}
		else
		{
			tbobj.deleteRow(iLine);
		}
	}


	//范围：领用材料	
	//检测领用单的数量是否正确
	function checkWastageQuantity(obj){

             var strdv_code="";
             var strdv_code_old="";
             var strsql_where = "";
             
             if(obj.value==""){return;}
             var iMaxNum = obj.value;

      	    var rowCount = obj.parentElement.parentElement.parentElement.parentElement.rows.length-1
      	    var rowNum=obj.parentElement.parentElement.rowIndex-1;
      	    var isDevice = "";
      	    var sc_id = "";
      	    var wt_unitprice = 0;
      	    var wt_amount = 0;
      	    
      	    if(rowCount>1)
      	    {
      	      isDevice=document.forms[0].isdevice[rowNum].value;
      	      sc_id=document.forms[0].sc_id[rowNum].value;
      	      wt_unitprice=document.forms[0].wt_unitprice[rowNum].value;
      	      
      	    }
      	    else
      	    {
      	      isDevice=document.forms[0].isdevice.value;
      	      sc_id=document.forms[0].sc_id.value;
      	      wt_unitprice=document.forms[0].wt_unitprice.value;
      	    }
             if(sc_id==""){alert("请先输入材料分类号!");obj.value="";return;}   
             
             if(isType(obj)!=true){alert("输入值格式错误，请输入大于等于0的整数!");obj.focus();return;}
             else
             {
                
                
                if(obj.value==0){
                  if(isDevice=="yes"){
               	    if(rowCount>1)
               	    {
         					document.forms[0].dv_code[rowNum].value = "";
         					document.all.mtflag[rowNum].src = "../images/icon_mt_device.gif";
         					document.all.mtflag[rowNum].title = "设备材料,尚未选择设备编号";
               	    }
               	    else
               	    {
         					document.forms[0].dv_code.value = "";
         					document.all.mtflag.src = "../images/icon_mt_device.gif";
         					document.all.mtflag.title = "设备材料,尚未选择设备编号";
               	    }
                  }
                  return;
                }else{
                     
                     wt_amount = wt_unitprice*obj.value;

               	    if(rowCount>1)
               	    {
         					document.forms[0].wt_amount[rowNum].value = wt_amount;
               	    }
               	    else
               	    {
         					document.forms[0].wt_amount.value = wt_amount;
               	    }                     
                }
                

               
               //领用的如果是设备，必须进行如下处理
               if(isDevice=="yes")
               {
            	    if(rowCount>1)
            	    {
      					strdv_code = document.forms[0].dv_code[rowNum].value;
      					strdv_code_old = document.forms[0].dv_code_old[rowNum].value;
            	    }
            	    else
            	    {
      					strdv_code = document.forms[0].dv_code.value;
      					strdv_code_old = document.forms[0].dv_code_old.value;
            	    }                  
                  createImportForm(obj,1,'/fdms/common/importform_dvcode_wastage.jsp','选择设备',500,360,'yes','form1','dv_code',1,',',document.forms[0].se_id_src.value+','+sc_id,strdv_code,',',1,strdv_code_old,',',iMaxNum,'');
               }
             }             
	}
	
	
	//范围：领用材料	
	//检测材料分类号
	function checkWastageSC(obj)
	{
		var parameter=escape(obj.value);
		var rowCount = obj.parentElement.parentElement.parentElement.parentElement.rows.length-1
		var rowNum=obj.parentElement.parentElement.rowIndex-1;
		
		var param = document.forms[0].se_id_src.value;

		if(obj.value!="" && (param=="" || param==null)){
		   alert("请先填写预备室！");
		   obj.value = "";
		   return false;
		}
		
		if(rowCount>1){
			for(i=0; i<rowCount; i++){
				if(rowNum!=i && eval("document.forms[0]." + obj.name + "[" + i + "].value")!="" && eval("document.forms[0]." + obj.name + "[" + i + "].value")==obj.value){
					alert("该材料已经选择，请选择其他材料！");
					document.forms[0].sc_id[rowNum].value = "";
					document.forms[0].sc_alias[rowNum].value = "";
					document.forms[0].sc_name[rowNum].value = "";
					document.forms[0].sc_type[rowNum].value = "";
					document.forms[0].sc_unit[rowNum].value = "";
					document.forms[0].isdevice[rowNum].value = "no";
					document.forms[0].dv_code[rowNum].value = "";
					document.forms[0].wt_quantity[rowNum].value = "";
					document.forms[0].wt_unitprice[rowNum].value = "";
					document.forms[0].wt_amount[rowNum].value = "";
					document.all.mtflag[rowNum].src = "../images/icon_mt_undifine.gif";
					document.all.mtflag[rowNum].title = "尚未填写材料";
					return false;
				}
			}
		}	
		document.frames["getSC"].location.replace("/fdms/common/frameform_stuff.jsp?ctype=4&param="+ param +"&"+obj.name+"="+parameter+"&rowCount="+rowCount+"&rowNum="+rowNum);
	}		
	
	
	//范围：领用材料	
	//检测领用单的数量是否正确
	function checkWastageQuantityByCourse(obj,iLine){

             var strdv_code="";
             var strdv_code_old="";
             var sIndexValue = "";
             var iSingle = 1;
             var deviceObj = document.forms[0].isdevice;
             if(deviceObj!=null)
             {
               if(deviceObj.length!=null){iSingle = 2;}
             }
             
             if(obj.value==""){return;}
             var iMaxNum = obj.value;

      	    var isDevice = "";
      	    var sc_id = "";
      	    var se_quantity = "";//库存数量
      	    
      	    if(iSingle!=1)
      	    {
      	      isDevice=document.forms[0].isdevice[iLine].value;
      	      sc_id=document.forms[0].sc_id[iLine].value;
      	      se_quantity = document.forms[0].se_quantity[iLine].value;
      	    }
      	    else
      	    {
      	      isDevice=document.forms[0].isdevice.value;
      	      sc_id=document.forms[0].sc_id.value;
      	      se_quantity = document.forms[0].se_quantity.value;
      	    }

             if(se_quantity!=""){se_quantity = parseInt(se_quantity);}
             
             if(isType(obj)!=true){alert("输入值格式错误，请输入大于等于0的整数!");obj.focus();return;}
             else
             {
                if(obj.value==0){
                  if(isDevice=="yes"){
               	    if(iSingle!=1)
               	    {
         					document.forms[0].dv_code[iLine].value = "";
         					document.all.mtflag[iLine].src = "../images/icon_mt_device.gif";
         					document.all.mtflag[iLine].title = "设备材料,尚未选择设备编号";
               	    }
               	    else
               	    {
         					document.forms[0].dv_code.value = "";
         					document.all.mtflag.src = "../images/icon_mt_device.gif";
         					document.all.mtflag.title = "设备材料,尚未选择设备编号";
               	    }
                  }
                  return;
                }
                
                //库存数量为0
                if(se_quantity==0 && obj.value!=0){
                  alert("库存数量为0，领用数量必须为0！");
                  obj.value="0";
                  obj.focus();return;
                }

               //判断输入的领用数量是否超过了库存数量
               if(obj.value>se_quantity){
                  alert("领用数量超过了库存数量，请重新填写领用数量！");
                  obj.focus();return;
               }
               
               //领用的如果是设备，必须进行如下处理
               if(isDevice=="yes")
               {
            	    if(iSingle!=1)
            	    {
      					strdv_code = document.forms[0].dv_code[iLine].value;
      					strdv_code_old = document.forms[0].dv_code_old[iLine].value;
      					sIndexValue = "[" + iLine + "]";
            	    }
            	    else
            	    {
      					strdv_code = document.forms[0].dv_code.value;
      					strdv_code_old = document.forms[0].dv_code_old.value;
            	    }                  
                  //alert("请选择具体的设备编号！");
                  createImportForm(obj,-1,'/fdms/common/importform_dvcode_wastage.jsp','选择设备',500,360,'yes','form1','dv_code',1,',',document.forms[0].se_id_src.value+','+sc_id,strdv_code,',',1,strdv_code_old,',',iMaxNum,sIndexValue);
               }
             }             
	}	