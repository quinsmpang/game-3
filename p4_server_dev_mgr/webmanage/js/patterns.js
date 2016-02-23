var PatternsDict = new Object();

PatternsDict.L=/^\D{1}$/;   //匹配L
PatternsDict.B_Z=/^[b-zB-Z]$/;   //匹配L
PatternsDict.Sn=/^(\W|\w|\s)+$/;   //匹配若干个非空白字符
PatternsDict.Dn=/^\d+$/;   //匹配若干个D
PatternsDict.LDD=/^\D\d{2}$/;   //匹配LDD
PatternsDict.Num=/(^\d+\.\d+$)|(^\d*$)/;   //匹配正数
PatternsDict.numeric =/(^\d+\.\d+$)|(^\d*$)|(^-(\d+\.\d+$))|(^-(\d*$))/;   //匹配数值
PatternsDict.P0Int=/^[0-9]\d*$/;   //匹配非负整数
PatternsDict.PInt=/^[1-9]\d*$/;   //匹配正整数
PatternsDict.PInt_4=/^[1-9]\d{3}$/;   //匹配4位正整数
PatternsDict.Dnz=/^[1-9]\d*$/;

PatternsDict.L2D6L = /^\D{2}\d{6}\D$/;	// 匹配LLDDDDDDL, added by whj, for mi_cover
PatternsDict.L2D6LD = /^\D{2}\d{6}\D\d$/;	// 匹配LLDDDDDDLD, 特征码必须输， added by whj, for mi_cover
PatternsDict.L2D6L2 = /^\D{2}\d{6}\D{2}$/;	// 匹配LLDDDDDDLL, 特征码必须输， added by whj, for 工具指示编号
PatternsDict.L02D6LD = /^\D{0,2}\d{6}\D\d$/;	// 匹配LLDDDDDDLD, 特征码可以不输， added by whj, for 根据指示编号查询

PatternsDict.D1_1=/(^\d{1}\.\d{1}$)|(^\d{1}$)/;  //匹配D.D
PatternsDict.D1_2=/(^\d{1}\.(\d{1}|\d{2})$)|(^\d{1}$)/;  //匹配D.DD
PatternsDict.D1_3=/(^\d{1}\.(\d{1}|\d{2}|\d{3})$)|(^\d{1}$)/;  //匹配D.DDD

PatternsDict.D2_1=/(^(\d{1}|\d{2})\.\d{1}$)|(^(\d{1}|\d{2})$)/;   //匹配DD.D
PatternsDict.D2_2=/(^(\d{1}|\d{2})\.(\d{1}|\d{2})$)|(^(\d{1}|\d{2})$)/;	  //匹配DD.DD
PatternsDict.D2_3=/(^(\d{1}|\d{2})\.(\d{1}|\d{2}|\d{3})$)|(^(\d{1}|\d{2})$)/;  //匹配DD.DDD

PatternsDict.D3_1=/(^(\d{1}|\d{2}|\d{3})\.\d{1}$)|(^(\d{1}|\d{2}|\d{3})$)/;	//匹配DDD.D
PatternsDict.D3_2=/(^(\d{1}|\d{2}|\d{3})\.(\d{1}|\d{2})$)|(^(\d{1}|\d{2}|\d{3})$)/;   //匹配DDD.DD
PatternsDict.D3_3=/(^(\d{1}|\d{2}|\d{3})\.(\d{1}|\d{2}|\d{3})$)|(^(\d{1}|\d{2}|\d{3})$)/;	//匹配DDD.DDD

PatternsDict.D2=/(^(\d{2})$)/;   //匹配DD
PatternsDict.D3=/(^(\d{3})$)/;   //匹配DDD
PatternsDict.D4_1=/(^(\d{1}|\d{2}|\d{3}|\d{4})\.\d{1}$)|(^(\d{1}|\d{2}|\d{3}|\d{4})$)/;   //匹配DDDD.D
PatternsDict.D4=/(^(\d{4})$)/;   //匹配DDDD

PatternsDict.D1=/^\d{1}$/;	//匹配D
PatternsDict.D1_D2=/^(\d{1}|\d{2})$/;//匹配DD
PatternsDict.D1_D3=/^(\d{1}|\d{2}|\d{3})$/;	//匹配DDD
PatternsDict.D1_D4=/^(\d{1}|\d{2}|\d{3}|\d{4})$/;   //匹配D到DDDD
PatternsDict.D1_D5=/^(\d{1}|\d{2}|\d{3}|\d{4}|\d{5})$/;   //匹配D到DDDDD

PatternsDict.fD3_2=/(^-(\d{1}|\d{2}|\d{3})\.(\d{1}|\d{2})$)|(^-(\d{1}|\d{2}|\d{3})$)/;   //匹配-DDD.DD
PatternsDict.fD3_4=/(^-(\d{1}|\d{2}|\d{3})\.(\d{1}|\d{2}|\d{3}|\d{4})$)|(^-(\d{1}|\d{2}|\d{3})$)/;   //匹配-DDD.DDDD

PatternsDict.C08=/^(\w|\W){0,8}$/;	//匹配最多8个C, added by whj, for mi_cover
PatternsDict.C02=/^\w{0,2}$/;		//匹配最多2个C, added by whj, for mi_cover
PatternsDict.email=/^[_a-zA-Z0-9]+@([_a-zA-Z0-9]+\.)+[a-zA-Z0-9]{2,3}$/

PatternsDict.C1=/^\w{1}$/;		//匹配C
PatternsDict.C1_C4=/^\w{1,4}$/;		//匹配C到CCCC
PatternsDict.C2=/^\w{2}$/;		//匹配CC
PatternsDict.C9=/^\w{9}$/;		//匹配CCCCCCCCC
PatternsDict.C1_C12=/^(\w|\W){1,12}$/;		//匹配C到CCCCCCCCCCCC
PatternsDict.LD=/^\D\d{1}$/;	//匹配LD
PatternsDict.D6 = /^\d{6}/;	// 匹配D6

PatternsDict.T10=/^(\W|\w|\s){1,10}$/;   //匹配10个非空白字符
PatternsDict.T12=/^(\W{1,12}|\w{1,12}|\s{1,12})$/;   //匹配12个非空白字符
PatternsDict.DateTime=/^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$/