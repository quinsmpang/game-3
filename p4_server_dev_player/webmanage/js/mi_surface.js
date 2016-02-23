function setGf(){
	if(document.forms[0].gf_selected[1].checked==true){	
		
		document.forms[0].gf_nickel_ply.value = "";
		document.forms[0].gf_max_nickel_ply.value = "";
		document.forms[0].gf_gold_ply.value = "";
		document.forms[0].gf_max_gold_ply.value = "";

		//document.forms[0].gf_nickel_ply.nocheck = "true";
		//document.forms[0].gf_gold_ply.nocheck = "true";
		
		document.forms[0].gf_nickel_ply.readOnly = true;
		document.forms[0].gf_max_nickel_ply.readOnly = true;
		document.forms[0].gf_gold_ply.readOnly = true;
		document.forms[0].gf_max_gold_ply.readOnly = true;
	}
	else{
		document.forms[0].gf_nickel_ply.nocheck = "false";
		document.forms[0].gf_gold_ply.nocheck = "false";

		document.forms[0].gf_nickel_ply.readOnly = false;
		document.forms[0].gf_max_nickel_ply.readOnly = false;
		document.forms[0].gf_gold_ply.readOnly = false;
		document.forms[0].gf_max_gold_ply.readOnly = false;
	}	
}

function loseControl(obj){
	for(i=0; i<document.forms[0].hal_hole_tin_pearl.length; i++){
		document.forms[0].hal_hole_tin_pearl[i].checked = false;
	}
}
//设置Hal
function setHal(stat){
	if(stat==true){
		document.forms[0].hal_line_tin_ply.nocheck = "false";
		document.forms[0].hal_hole_tin_ply.nocheck = "false";
		document.forms[0].hal_smd_tin_ply.nocheck = "false";
		document.forms[0].hal_max_tin_ply.nocheck = "false";
		
		document.forms[0].hal_line_tin_ply.readOnly = false;
		document.forms[0].hal_hole_tin_ply.readOnly = false;
		document.forms[0].hal_smd_tin_ply.readOnly = false;
		document.forms[0].hal_max_tin_ply.readOnly = false;
		
		for(i=0; i<document.forms[0].hal_hole_tin_pearl.length; i++){			
			document.forms[0].hal_hole_tin_pearl[i].detachEvent("onclick",loseControl);
		}		
	}
	else{
		document.forms[0].hal_line_tin_ply.value = "";
		document.forms[0].hal_hole_tin_ply.value = "";
		document.forms[0].hal_smd_tin_ply.value = "";
		document.forms[0].hal_max_tin_ply.value = "";
		
		/*
		document.forms[0].hal_line_tin_ply.nocheck = "true";
		document.forms[0].hal_hole_tin_ply.nocheck = "true";
		document.forms[0].hal_smd_tin_ply.nocheck = "true";
		document.forms[0].hal_max_tin_ply.nocheck = "true";
		*/

		document.forms[0].hal_line_tin_ply.readOnly = true;
		document.forms[0].hal_hole_tin_ply.readOnly = true;
		document.forms[0].hal_smd_tin_ply.readOnly = true;
		document.forms[0].hal_max_tin_ply.readOnly = true;
		document.forms[0].hal_hole_tin_pearl.readOnly = true;
		
		for(i=0; i<document.forms[0].hal_hole_tin_pearl.length; i++){
			document.forms[0].hal_hole_tin_pearl[i].checked = false;
			document.forms[0].hal_hole_tin_pearl[i].attachEvent("onclick",loseControl);
		}
	}
}
//设置Eneg
function setEneg(stat){
	if(stat==true){
		
		document.forms[0].eneg_nickel_ply.nocheck = "false";
		document.forms[0].eneg_gold_ply.nocheck = "false";
		
		document.forms[0].eneg_nickel_ply.readOnly = false;
		document.forms[0].eneg_max_nickel_ply.readOnly = false;
		document.forms[0].eneg_gold_ply.readOnly = false;
		document.forms[0].eneg_max_gold_ply.readOnly = false;
	}
	else{
		document.forms[0].eneg_nickel_ply.value = "";
		document.forms[0].eneg_max_nickel_ply.value = "";
		document.forms[0].eneg_gold_ply.value = "";
		document.forms[0].eneg_max_gold_ply.value = "";
		
		//document.forms[0].eneg_nickel_ply.nocheck = "true";
		//document.forms[0].eneg_gold_ply.nocheck = "true";
		
		document.forms[0].eneg_nickel_ply.readOnly = true;
		document.forms[0].eneg_max_nickel_ply.readOnly = true;
		document.forms[0].eneg_gold_ply.readOnly = true;
		document.forms[0].eneg_max_gold_ply.readOnly = true;
	}
}

//设置选择性
function setSelect(stat){
	if(stat==true){
		
		document.forms[0].select_nickel_ply.nocheck = "false";
		document.forms[0].select_gold_ply.nocheck = "false";
		
		document.forms[0].select_nickel_ply.readOnly = false;
		document.forms[0].select_max_nickel_ply.readOnly = false;
		document.forms[0].select_gold_ply.readOnly = false;
		document.forms[0].select_max_gold_ply.readOnly = false;
	}
	else{
		document.forms[0].select_nickel_ply.value = "";
		document.forms[0].select_max_nickel_ply.value = "";
		document.forms[0].select_gold_ply.value = "";
		document.forms[0].select_max_gold_ply.value = "";
		
		//document.forms[0].select_nickel_ply.nocheck = "true";
		//document.forms[0].select_gold_ply.nocheck = "true";
		
		document.forms[0].select_nickel_ply.readOnly = true;
		document.forms[0].select_max_nickel_ply.readOnly = true;
		document.forms[0].select_gold_ply.readOnly = true;
		document.forms[0].select_max_gold_ply.readOnly = true;
	}
}

//设置Imsl
function setImsl(stat){
	if(stat==true){
		
		document.forms[0].imsl_nickel_ply.nocheck = "false";
		document.forms[0].imsl_siller_ply.nocheck = "false";
		
		document.forms[0].imsl_nickel_ply.readOnly = false;
		document.forms[0].imsl_max_nickel_ply.readOnly = false;
		document.forms[0].imsl_siller_ply.readOnly = false;
		document.forms[0].imsl_max_siller_ply.readOnly = false;
	}
	else{
		document.forms[0].imsl_nickel_ply.value = "";
		document.forms[0].imsl_max_nickel_ply.value = "";
		document.forms[0].imsl_siller_ply.value = "";
		document.forms[0].imsl_max_siller_ply.value = "";
		
		//document.forms[0].imsl_nickel_ply.nocheck = "true";
		//document.forms[0].imsl_siller_ply.nocheck = "true";
		
		document.forms[0].imsl_nickel_ply.readOnly = true;
		document.forms[0].imsl_max_nickel_ply.readOnly = true;
		document.forms[0].imsl_siller_ply.readOnly = true;
		document.forms[0].imsl_max_siller_ply.readOnly = true;
	}
}

//设置Entek
function setEntek(stat){
	if(stat==true){
		
		document.forms[0].entek_type.readOnly = false;
		document.forms[0].entek_ply.readOnly = false;

	}
	else{
		document.forms[0].entek_type.value = "";
		document.forms[0].entek_ply.value = "";

		document.forms[0].entek_type.readOnly = true;
		document.forms[0].entek_ply.readOnly = true;
	}
}

//根据选择的处理方式，决定其他方式的输入状态
function setDeal_mode(){
	
	if(document.forms[0].deal_mode[0].checked==true){
		setHal(true);		
		setEneg(false);
		setSelect(false);
		setImsl(false);
		setEntek(false);		
	}	
	if(document.forms[0].deal_mode[1].checked==true){
		setHal(false);
		setEneg(true);
		setSelect(false);
		setImsl(false);
		setEntek(false);
	}
	if(document.forms[0].deal_mode[2].checked==true){
		setHal(false);
		setEneg(false);
		setSelect(true);
		setImsl(false);
		setEntek(false);
	}
	if(document.forms[0].deal_mode[3].checked==true){
		setHal(false);
		setEneg(false);
		setSelect(false);
		setImsl(true);
		setEntek(false);
	}
	if(document.forms[0].deal_mode[4].checked==true){
		setHal(false);
		setEneg(false);
		setSelect(false);
		setImsl(false);
		setEntek(true);
	}
}