
	function chooseAllCate(key)
	{
		chooseAllCate2('chooseAll',key);
	}
	function chooseAllCate2(chckname,key)
	{
		var choose=true;
		if(document.getElementById(chckname).checked)
		{
			choose=true;
		}
		else
		{
			choose=false;
		}																		
		for(var i=0;i<document.getElementsByName(key).length;i++)
		{
			document.getElementsByName(key)[i].checked=choose;
		}																			
	}
	
	function updateChooseAll(obj)
	{
		updateChooseAll2('chooseAll',obj);
	}
	function updateChooseAll2(chckname,obj)
	{
		if(!obj.checked && document.getElementById(chckname).checked)
		{
		document.getElementById(chckname).checked=false;
		}
	}