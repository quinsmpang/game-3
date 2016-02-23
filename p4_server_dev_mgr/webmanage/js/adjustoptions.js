
/**
 * 移动项
 */
function chooseItems(source, target) 
{ 
	var choiceOptions = source.options;
	var selectedOptions = target.options;
	for (i = 0; i < (choiceOptions.length); i++) 
	{ 
		var temp = choiceOptions.item(i); 
		if (temp.selected == true) 
		{ 
			var oOption = document.createElement("OPTION"); 
			selectedOptions[selectedOptions.length] = new Option(temp.text, temp.value);
		} 
	} 
	
	for (j = (choiceOptions.length-1); j >= 0; j--) 
	{ 
		var temp = choiceOptions.item(j);
		if (temp.selected == true) 
		{ 
			choiceOptions[j] = null;
		} 
	} 
}

/**
 * 调整项顺序
 */
function adjustUp(itemOptions)
{
	var selectedOption;
	var count = 0;
	var index;
	for ( i = 0; i < itemOptions.length ; i++) 
	{
		var temp = itemOptions.item(i); 
		if (temp.selected == true) 
		{ 
			count++;
			if (count > 1) 
			{ 
				alert("只能选择一个列调整顺序！"); 
				return; 
			} else 
			if(count == 1) {
				index = i;
			}
		} 
	} 
	if (count == 0) 
	{ 
		alert("请选择要调整顺序的列！"); 
		return; 
	}
	if (index == 0) 
	return;
	
	selectedOption = itemOptions[index];
	var lastOption = itemOptions[index-1];
	var temp = new Option(selectedOption.text, selectedOption.value);
	selectedOption.text = lastOption.text;
	selectedOption.value = lastOption.value;
	selectedOption.selected = false;
	lastOption.text = temp.text;
	lastOption.value = temp.value;
	lastOption.selected = true;
} 

function adjustDown(itemOptions) 
{ 
	var selectedOption; 
	var count = 0; 
	var index; 
	for ( i = 0; i < itemOptions.length ; i++) 
	{ 
		var temp = itemOptions[i]; 
		if (temp.selected == true) 
		{ 
			count++; 
			if (count > 1) 
			{ 
				alert("只能选择一个列调整顺序！"); 
				return; 
			} else 
			if(count == 1) { 
				index = i; 
			} 
		}
	} 
	
	if (count == 0) 
	{ 
		alert("请选择要调整顺序的列！"); 
		return; 
	}
	if (index == itemOptions.length-1) 
	return; 
	selectedOption = itemOptions[index]; 
	var nextOption = itemOptions[index+1]; 
	var temp = new Option(selectedOption.text, selectedOption.value); 
	selectedOption.text = nextOption.text; 
	selectedOption.value = nextOption.value; 
	selectedOption.selected = false; 
	nextOption.text = temp.text; 
	nextOption.value = temp.value; 
	nextOption.selected = true; 
}