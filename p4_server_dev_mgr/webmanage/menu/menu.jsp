<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="server.common.*"%>
<%@ page import="org.dom4j.Document"%>
<%@ page import="org.dom4j.DocumentException"%>
<%@ page import="org.dom4j.Element"%>
<%@ page import="org.dom4j.io.SAXReader"%>
<%@ include file="../system/inc_import.jsp"%>
<%@ include file="../system/inc_getuser.jsp"%>

<script src="../js/common.js"></script>
<%
String username=null;
int userId =0;
int userType=0;
boolean root=false;
if(userObj!=null)
{
 username=userObj.optString("userName");
 userId=userObj.optInt("id");
 userType=userObj.optInt("userType"); 
 root=userObj.optBoolean("root"); 
}

%>
<HTML>
<HEAD>
<META http-equiv=Content-Type content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../css/style1.css" type="text/css">
<SCRIPT language=JavaScript src="../js/dynlayer.js">
</SCRIPT>

<STYLE type=text/css>
A:link {
	COLOR: #ffffff; TEXT-DECORATION: none
}
A:visited {
	COLOR: #ffffff; TEXT-DECORATION: none
}
A:active {
	COLOR: #ffffff; TEXT-DECORATION: none
}
A:hover {
	COLOR: #ffffff; TEXT-DECORATION: none
}
.menuBack {
	background-attachment: fixed;
	background-image: url(../images/bannerbg.gif);
	background-repeat: repeat;
}
</STYLE>

<script>
function menuitem(obj,acttype){
switch(acttype){
 case "over":
   obj.className="menuitem_over";
   break;
 case "out":
   obj.className="menuitem";
   break;
 case "up":
   obj.className="menuitem_up";
   break;
 case "down":
   obj.className="menuitem_down";
   break;
 }
}
function menubar(obj,acttype){
switch(acttype){
 case "out":
   obj.className="menubar";
   break;
 case "up":
   obj.className="menubar_up";
   break;
 case "down":
   obj.className="menubar_down";
   break;
 }
}
function exit()
{
	openWindow("exit.jsp?username=","退出系统",200,100);
}
function changeImg(imgObj,imgSrc)
{
imgObj.src=imgSrc;
}
</script>
</HEAD>

<BODY onResize="if (eval(window_onresize)) window_onresize()" onLoad="init()" bgcolor="#125460" oncontextmenu = "return false">
<%
// 构造菜单
MenuInfo theMenu = new MenuInfo();
// 添加菜单条及其菜单项
// addMenuBar(菜单条名字, 菜单条提示字符串);
// addMenuItem(菜单项名字，提升，图片链接，动作链接);

int id=0;
String name=null;
String altName=null;
String icon=null;
String iconOver=null;
String link=null;
String targetFrame=null;
String moduleName=null;
String permission=null;
int visible=1;
		
SAXReader saxReader = new SAXReader();
Document document = saxReader.read(ServerConfig.getWebInfPath() + "conf/system_menu.xml");
Element menu_root = document.getRootElement();
List<Element> menuBarList = menu_root.elements("menubar");
for(int i=0;menuBarList!=null && i<menuBarList.size();i++)
{
	Element menuBarElement = menuBarList.get(i);
	
	name=menuBarElement.attributeValue("name");
	
	
	boolean allow = false;
	
	if(name.equals("系统功能"))
	{
			if(root)
			{
				allow = true;
			}
			else
			{
				allow = false;
			}			
	}
	else
	{
		allow = true;
	}
	if(allow)
	{
		MenuBar menuBar = new MenuBar(name,altName);
				
		List<Element> menuItemList = menuBarElement.elements("menuitem");
		for(int j=0;menuItemList!=null && j<menuItemList.size();j++)
		{
			Element menuItemElement = menuItemList.get(j);
			
			name=menuItemElement.getText();		
			altName=name;
			icon=menuItemElement.attributeValue("icon");		
			if(iconOver==null || iconOver.equals(""))iconOver=icon;
			link=menuItemElement.attributeValue("link");		
			moduleName=menuItemElement.attributeValue("module");
			permission=menuItemElement.attributeValue("permission");
			visible = Tools.str2int(menuItemElement.attributeValue("visible"));
			//System.out.println("permission=" + permission);
			if(visible==1)
			{
				if(moduleName!=null && permission!=null && !moduleName.equals("") && !permission.equals(""))
				{
					//System.out.println("userId=" + userId + ",moduleName=" + moduleName + ",permission=" + permission + ",result=" + UserBAC.checkPermission(userId,moduleName,permission));
					if(UserBAC.checkPermission(userId,moduleName,permission))
					{							
						allow = true;
					}else
					{
						allow = false;
					}
				}else
				{
					allow = true;
				}
									
				
				//System.out.println("allow=" + allow + ",name=" + name);
				if(allow)
				{
					theMenu.addMenuBar(menuBar); //有子菜单才加menuBar
					if(targetFrame!=null && !targetFrame.equals(""))
					{
						theMenu.addMenuItem(new MenuItem(name,altName,"../images/"+icon,link,"../images/"+iconOver,targetFrame));
					}
					else
					{
						theMenu.addMenuItem(new MenuItem(name,altName,"../images/"+icon,link));
					}
				}
			}			
		}
	}
	
}
			

int nMenuBars=theMenu.getNumMenuBar();
int nMenuItems;
int i,j;
MenuBar theBar;
MenuItem theMenuItem;

for(i=0;i<nMenuBars;i++)
{
	theBar = theMenu.getMenuBar(i);
//Todo: 判断用户权限
	{
%>
<STYLE TYPE="text/css">
#menulayer<%=i%>Div 
{
	LEFT: 0px; CLIP: rect(0px 2000px 2000px 0px); POSITION: absolute; TOP: 0px; HEIGHT: 2000px; BACKGROUND-COLOR: #1c4b7d; layer-background-color: #1c4b7d
}
#iconlayer<%=i%>Div 
{
	LEFT: 0px; CLIP: rect(0px 2000px 2000px 0px); POSITION: absolute; TOP: 22px; HEIGHT: 2000px; BACKGROUND-COLOR: #1c4b7d; layer-background-color: #1c4b7d
}
#barlayer<%=i%>Div  
{
	LEFT: 0px; CLIP: rect(0px 2000px 22px 0px); POSITION: absolute; TOP: 0px; HEIGHT: 22px; BACKGROUND-COLOR: #1c4b7d; layer-background-color: #1c4b7d
}
#uplayer<%=i%>Div   
{
	LEFT: -20px; WIDTH: 16px; CLIP: rect(0px 16px 16px 0px); POSITION: absolute; TOP: 26px; HEIGHT: 16px; BACKGROUND-COLOR: #efefef; layer-background-color: #EFEFEF
}
#downlayer<%=i%>Div 
{
	LEFT: -20px; WIDTH: 16px; CLIP: rect(0px 16px 16px 0px); POSITION: absolute; TOP: 42px; HEIGHT: 16px; BACKGROUND-COLOR: #efefef; layer-background-color: #EFEFEF
}
</STYLE>
<DIV name="menulayer<%=i%>Div" id="menulayer<%=i%>Div">
<DIV name="iconlayer<%=i%>Div" id="iconlayer<%=i%>Div" class="menuBack">
<TABLE cellSpacing=2 cellPadding=2 width="100%" align=left border=0  > 
<%
		nMenuItems = theBar.getNumMenuItem();
		for (j=0;j<nMenuItems;j++)
		{
			theMenuItem = theBar.getMenuItem(j);
			//Todo: 判断用户权限
 			{
%>
  <TBODY>
  <TR>
    <TD align=middle>
      <TABLE 
      onmouseup="menuitem(this,'up')" 
      onmousedown="menuitem(this,'down')" 
      onmouseover="menuitem(this,'over')" 
      style="CURSOR: hand" 
      onmouseout="menuitem(this,'out')" 
	  class="menuitem"
      height=22 cellSpacing=0  cellPadding=0 border=0 >
        <TBODY>
        <TR >
          <TD ><A target="<%=theMenuItem.getStrTargetFrame()%>" href="<%=theMenuItem.getActionUrl()%>"><IMG 
            height=32 alt="<%=theMenuItem.getHint()%>" src="<%=theMenuItem.getImgUrl()%>" onMouseOver="changeImg(this,'<%=theMenuItem.getImgUrl_mouseover()%>')" onMouseOut="changeImg(this,'<%=theMenuItem.getImgUrl()%>')" width=32 align=middle 
            border=0></A></TD></TR></TBODY></TABLE></TD></TR>
  <TR>
        <TD align=middle><A target="mainwindow" href="<%=theMenuItem.getActionUrl()%>"><%=theMenuItem.getName()%></A></TD>
      </TR>
			<%}%>
		<%}%>
<tr><td>&nbsp;</td></tr></TBODY>
</table>
</DIV>

<DIV name="uplayer<%=i%>Div" id="uplayer<%=i%>Div">
<img src='../images/scrollup.gif' width=16 height=16 onMouseDown="javascript:this.src='../images/scrollup2.gif';menuscrollup()" onMouseUp="javascript:this.src='../images/scrollup.gif';menuscrollstop()" onMouseOut="javascript:this.src='../images/scrollup.gif';menuscrollstop()">
</DIV>
<DIV name="downlayer<%=i%>Div" id="downlayer<%=i%>Div">
<img src='../images/scrolldown.gif' width=16 height=16 onMouseDown="javascript:this.src='../images/scrolldown2.gif';menuscrolldown()" onMouseUp="javascript:this.src='../images/scrolldown.gif';menuscrollstop()" onMouseOut="javascript:this.src='../images/scrolldown.gif';menuscrollstop()">
</DIV>

<DIV name="barlayer<%=i%>Div" id="barlayer<%=i%>Div">
<TABLE 
onmouseup="menubar(this,'up')" 
onmousedown="menubar(this,'down')"
onmouseout="menubar(this,'out')" 
style="CURSOR: hand" 
onclick=javascript:menubarpush(<%=i%>) 
class="menubar" 
height=22 cellSpacing=0 cellPadding=0 width="100%" 
bgColor=#efefef border=0>
  <TBODY>
<TR>
    <TD noWrap align=middle 
    colSpan=0 rowSpan=0 onselectstart="return false" background="../images/menuback.gif"><FONT color=#000000 ><%=theBar.getName()%></FONT></TD></TR></TBODY></TABLE></DIV>
	
</DIV>

	<%}%>
<%}%>

<script language="JavaScript" src="../js/slidemenu2.js">
</script>
<SCRIPT ID=clientEventHandlersJS LANGUAGE=javascript>
function window_onresize() {
	menureload()
}
</SCRIPT>

<SCRIPT ID=clientEventHandlersJS LANGUAGE=javascript>

menubarsum =<%=nMenuBars%>;
<%
for (i=0;i<nMenuBars;i++)
{
%>
menuIconWidth[<%=i%>] = document.getElementById("iconlayer<%=i%>Div").scrollWidth + 0;
menuIconHeight[<%=i%>] = document.getElementById("iconlayer<%=i%>Div").scrollHeight + 0;
<%}%>
</SCRIPT>
</BODY>
</HTML>
