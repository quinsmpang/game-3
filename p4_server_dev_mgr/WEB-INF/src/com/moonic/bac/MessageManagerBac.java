package com.moonic.bac;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.moonic.servlet.STSServlet;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.STSNetSender;

public class MessageManagerBac {
	public static enum MessageType{ScrollMessage, AnnounceMessage, NoticeMessage, SystemMessage};
	
	private static Vector<MyTimerTask> container = new Vector<MyTimerTask>();
	
	/**
	 * 发信息
	 */
	public void send(HttpServletRequest request, String[] sid, int msg_type, final String date,  String content, String title, String channel, int repeat, int pid, int frequency){
		int singelSid=0;
		final int[] serverid= new int[sid.length];
		for (int i = 0; i < sid.length; i++) {
			serverid[i] = Integer.parseInt(sid[i]);
		}
		if(msg_type == 6 && pid > 0){
			for (int i = 0; i < sid.length; i++) {
				if(null!=sid[i]&&!"".equals(sid[i])){
					singelSid = Integer.parseInt(sid[i]);
				}
			}
		}
		switch(msg_type){
		case 1:
			if(frequency > 0){
				ScheduledExecutorService msgTimer = MyTools.createTimer(3);
				AppMsgTask msgTask = new AppMsgTask(serverid, coverHtml(content, MessageType.ScrollMessage), date, msgTimer);
				msgTimer.scheduleAtFixedRate(msgTask, 1, frequency*60*1000, TimeUnit.MILLISECONDS);
				container.add(msgTask);
			} else {
				pushAppMsg(serverid, coverHtml(content, MessageType.ScrollMessage), date);	
			}
			break;
		case 2:
			sendGameAnnounce(title, coverHtml(content, MessageType.AnnounceMessage), date, repeat,serverid);
			break;
		case 3:
			sendGameNotice(serverid, title, coverHtml(content, MessageType.NoticeMessage), date,request);
			break;
		case 4:
			if(frequency>0){
				ScheduledExecutorService sysMsgTimer = MyTools.createTimer(3);
				SysMsgTask sysTask=new SysMsgTask(serverid,coverHtml(content, MessageType.SystemMessage),date,sysMsgTimer);
				sysMsgTimer.scheduleAtFixedRate(sysTask, 1, frequency*60*1000, TimeUnit.MILLISECONDS);
				container.add(sysTask);
			}else{
				sendSysMsg(serverid,coverHtml(content, MessageType.SystemMessage));
			}
			break;
		case 5:
			//pushGameMsg(serverid, channel,  HTMLUtils.coverHtml(content, MessageType.NoticeMessage));
			break;
		case 6:
			sendGameNotice(singelSid, pid, title, coverHtml(content, MessageType.NoticeMessage), date, request);
			break;
		}
	}
	
	/**
	 * 发顶部消息
	 */
	public ReturnValue pushAppMsg(int[] serverid,String content,String date){
		if(null==content||"".equals(content))return new ReturnValue(false,"内容为空");
		try {
			STSNetSender sender = new STSNetSender(STSServlet.G_SEND_TOPMSG);
			sender.dos.writeUTF(content);
			ServerBAC.getInstance().sendReqToSome(ServerBAC.STS_GAME_SERVER, sender, serverid);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false,e.toString());
		}
	} 
	
	/**
	 * 发公告
	 */
	public void sendGameAnnounce(String title,String content,String date,int repeat,int[] serverid ){
		if(null==content||"".equals(content)){
			return;
		}
		ServerBAC.getInstance().createNotice(title, content, date, repeat, serverid);
	}
	
	/**
	 * 发系统消息
	 */
	public ReturnValue sendSysMsg(int[] serverid, String content){
		if(null==content||"".equals(content)){
			return new ReturnValue(false,"内容为空");
		}
		try {
			STSNetSender sender = new STSNetSender(STSServlet.G_SEND_SYSMSG);
			sender.dos.writeUTF(content);
			ServerBAC.getInstance().sendReqToSome(ServerBAC.STS_GAME_SERVER, sender, serverid);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false,e.toString());
		}
	}
	
	/**
	 * 发游戏推送
	 */
	public ReturnValue pushGameMsg(int[] serverid, String channel, String content){
		if(null==content||"".equals(content)){
			return new ReturnValue(false,"内容为空");
		}
		try {
			STSNetSender sender = new STSNetSender(STSServlet.G_SEND_GAMEPUSH);
			sender.dos.writeByte(Tools.str2byte(channel));
			sender.dos.writeUTF(content);
			ServerBAC.getInstance().sendReqToSome(ServerBAC.STS_GAME_SERVER, sender, serverid);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false,e.toString());
		}
	}
	
	/**
	 * 发公告
	 */
	public ReturnValue sendGameNotice(int[] serverid, String title, String content, String expDate, HttpServletRequest request){
		if(null==content||"".equals(content)){
			return new ReturnValue(false,"内容为空");
		}
		int tid=Tools.str2int(request.getParameter("tid"));
		int fid=Tools.str2int(request.getParameter("fid"));
		int isOnlineOnly=Tools.str2int(request.getParameter("isonlyonline"));
		String url=request.getParameter("url");
		//String ttitle=request.getParameter("ttitle");
		try {
			STSNetSender sender = new STSNetSender(STSServlet.G_SEND_INFORM);
			sender.dos.writeUTF(title);
			sender.dos.writeUTF(content);
			sender.dos.writeUTF(expDate);
			JSONObject obj=new JSONObject();
			obj.put("tid", tid);
			obj.put("fid", fid);
			obj.put("url", url);
			//obj.put("title", ttitle);
			sender.dos.writeUTF(obj.toString());
			sender.dos.write(isOnlineOnly);
			//extend
			//isAll 0 online 1 ALL
			ServerBAC.getInstance().sendReqToSome(ServerBAC.STS_GAME_SERVER, sender, serverid);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false,e.toString());
		}
	}
	
	/**
	 * 发公告
	 */
	public ReturnValue sendGameNotice(int serverid, int pid, String title, String content, String expDate, HttpServletRequest request){
		if(null==content||"".equals(content)){
			return new ReturnValue(false,"内容为空");
		}
		int tid=Tools.str2int(request.getParameter("tid"));
		int fid=Tools.str2int(request.getParameter("fid"));
		try {
			STSNetSender sender = new STSNetSender(STSServlet.G_SEND_INFORM_TOONE);
			sender.dos.writeInt(pid);
			sender.dos.writeUTF(title);
			sender.dos.writeUTF(content);
			sender.dos.writeUTF(expDate);
			JSONObject obj=new JSONObject();
			obj.put("tid", tid);
			obj.put("fid", fid);
			obj.put("url", "");
			//obj.put("title", ttitle);
			sender.dos.writeUTF(obj.toString());
			ServerBAC.getInstance().sendReqToOne(ServerBAC.STS_GAME_SERVER, sender, serverid);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false,e.toString());
		} 
	}
	
	/**
	 * 清除所有定时信息
	 */
	public void clernAllMsgTimer(){
		for (int i = 0; i < container.size(); i++) {
			MyTimerTask tmpTask=container.get(i);
			tmpTask.cancel();
			container.remove(tmpTask);
		}
	}
	
	//--------------内部类--------------
	
	/**
	 * 顶部消息
	 * @author 
	 */
	class AppMsgTask  extends MyTimerTask{
		int[] sid;
		String content;
		String date;
		ScheduledExecutorService t;
		public AppMsgTask(int[] sid,String content,String date,ScheduledExecutorService t) {
			this.sid=sid;
			this.content=content;
			this.date=date;
			this.t=t;
		}
		public void run2() {
			if(!MyTools.isDateBefore(date)) {
				this.cancel();
				MyTools.cancelTimer(t);
				container.remove(t);
				return;
			}
			pushAppMsg(sid,content, date);
		}
		public void cancel() {
			super.cancel();
			MyTools.cancelTimer(t);
		}
	}
	
	/**
	 * 系统消息
	 * @author 
	 */
	class SysMsgTask  extends MyTimerTask{
		int[] sid;
		String content;
		String date;
		ScheduledExecutorService t;
		public SysMsgTask(int[] sid,String content,String date,ScheduledExecutorService t) {
			this.sid=sid;
			this.content=content;
			this.date=date;
			this.t=t;
		}
		public void run2() {
			if(!MyTools.isDateBefore(date)) {
				this.cancel();
				MyTools.cancelTimer(t);
				container.remove(t);
				return;
			}
			sendSysMsg(sid,content);
		}
		public void cancel() {
			super.cancel();
			MyTools.cancelTimer(t);
		}
	}
	
	static final String BLANK="&nbsp;";
	public static String coverHtml(String htmlCode,MessageType type) {
		//System.out.println(htmlCode);
		StringBuffer sb = new StringBuffer();
		Document doc = Jsoup.parse(htmlCode);
		Elements elements=doc.getElementsByTag("body");
		parseElements(sb, elements, type);
		String text=sb.toString().replaceAll(BLANK, " ");
		//System.out.println(text);
		return text;
	}
	
	
	
	private static  void parseElements(StringBuffer sb,Elements content,MessageType type) {
		for (int i = 0; i < content.size(); i++) {
			Element e = content.get(i);
			List<Node> nodes = e.childNodes();
			parseNode(sb,nodes,type);
			switch (type) {
			case AnnounceMessage:
				sb.append("\r\n");
				break;
			case NoticeMessage:
				sb.append("|");
				break;
			case ScrollMessage:
				break;
			case SystemMessage:
				break;
			default:
				break;
			}
		}
	}

	private static  void parseNode(StringBuffer sb,List<Node> nodes,MessageType type) {
		for (Node node : nodes) {
			if (node.childNodeSize() > 0){
				parseNode(sb,node.childNodes(),type);
				if("p".equals(node.nodeName()))
				switch (type) {
				case AnnounceMessage:
					sb.append("\r\n");
					break;
				case NoticeMessage:
					sb.append("|");
					break;
				case ScrollMessage:
					break;
				case SystemMessage:
					break;
				default:
					break;
				}
			}else {
				if ("#text".equals(node.nodeName())) {
					Node parent_node = node.parent();
					String color = "";
					Attributes atts = parent_node.attributes();
					for (Attribute attribute : atts) {
						String parent_Attribute = attribute.getValue();
						if (parent_Attribute.startsWith("color")) {
							color = parent_Attribute.substring(7,parent_Attribute.length() - 1);
							break;
						}
					}
					String text = node.outerHtml();
					text = text.trim();
					if (null != color && !"".equals(color)) {
						switch (type) {
						case AnnounceMessage:
							text = "<0xFF" + color + ">" + text + "</>";
							break;
						case NoticeMessage:
							text = "<0xFF" + color + ">" + text + "</>";
							break;
						case ScrollMessage:
							text = "<0xFF" + color + ">" + text + "</>";
							break;
						case SystemMessage:
							text = "[" + color + "]" + text + "[-]";
							break;
						default:
							break;
						}
					
					}
					sb.append(text);
				} else{
					//System.out.println(node.nodeName());
					
				} 
			}
		}
	}
	
	//--------------静态区--------------
	
	private static MessageManagerBac instance = new MessageManagerBac();
	
	public static MessageManagerBac getInstance(){
		return instance;
	}
}
