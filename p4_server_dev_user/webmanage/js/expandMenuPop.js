//显示隐藏目录树节点
function showhide(srcobj,disp){
var liobj,imgobj;
if(srcobj.tagName=="IMG"){
liobj=srcobj.parentElement;
imgobj=srcobj;
}
if(srcobj.tagName=="LI"){
liobj=srcobj;
imgobj=liobj.children.item(0);
}

var ulobj=liobj.children.tags("ul").item(0);
if(disp!=null){
	if(disp==""){
	ulobj.style.display="";
	imgobj.src="../images/icon_sub.gif";
	}
	if(disp=="none"){
	ulobj.style.display="none";
	imgobj.src="../images/icon_add.gif";
	}
}else if(ulobj.style.display=="none"){
   ulobj.style.display="";
   imgobj.src="../images/icon_sub.gif";
   if(liobj.id!=null && liobj.id!=""){
   	eval("opener.menu."+liobj.id+"=''");  //将节点状态保存到外部框架页面中
   	}
  }else
  {
   ulobj.style.display="none";
   imgobj.src="../images/icon_add.gif";
   if(liobj.id!=null && liobj.id!=""){
   	eval("opener.menu."+liobj.id+"='none'");  //将节点状态保存到外部框架页面中
   	}
   }
}

//按前一次保存的目录树节点状态展开目录树
function expandMenu(){
var eleArr = document.all;
for(var i=0;i<eleArr.length;i++){
	if(eleArr[i].tagName=="LI"){
		if(eleArr[i].id!=null && eleArr[i].id!=""){
			if(eval("opener.menu."+eleArr[i].id)!=null)
				{
				showhide(eleArr[i],eval("opener.menu."+eleArr[i].id))
				}
				
			}
		}
	}
}
window.onload=expandMenu;
