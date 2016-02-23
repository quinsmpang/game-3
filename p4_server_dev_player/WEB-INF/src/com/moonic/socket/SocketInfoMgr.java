package com.moonic.socket;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import server.common.Tools;

import com.ehc.common.ReturnValue;

/**
 * Socket信息管理
 * @author John
 */
public class SocketInfoMgr {
	
	/**
	 * 获取SOCKET运行信息
	 */
	public ReturnValue getSocketRunData(){
		try {
			System.gc();
			Hashtable<Integer, Player> plamap = SocketServer.getInstance().plamap;
			Enumeration<Integer> enum1 = plamap.keys();
			StringBuffer sb1 = new StringBuffer();
			while(enum1.hasMoreElements()){
				Integer key = enum1.nextElement();
				Player pla = plamap.get(key);
				sb1.append("KEY="+key+" VAL="+pla.pname+"("+pla.pid+")\r\n");
			}
			Hashtable<String, Player> session_plamap = SocketServer.getInstance().session_plamap;
			Enumeration<String> enum2 = session_plamap.keys();
			StringBuffer sb2 = new StringBuffer();
			while(enum2.hasMoreElements()){
				String key = enum2.nextElement();
				Player pla = session_plamap.get(key);
				sb2.append("KEY="+key+" VAL="+pla.pname+"("+pla.pid+")\r\n");
			}
			ArrayList<String> plainfolist = SocketServer.getInstance().plainfolist;
			StringBuffer sb3 = new StringBuffer();
			for(int i = 0; i < plainfolist.size(); i++){
				String tag = plainfolist.get(i);
				sb3.append(tag);
				String[] data = Tools.splitStr(tag, "=");
				Player pla = plamap.get(Integer.valueOf(data[0]));
				if(pla == null || !pla.tag.equals(tag)){
					sb3.append(" 未回收的对象");
				}
				sb3.append("\r\n");
			}
			StringBuffer sb = new StringBuffer();
			sb.append("--plamap size:"+plamap.size()+"\r\n");
			sb.append("--session_plamap size:"+session_plamap.size()+"\r\n");
			sb.append("--plainfomap size:"+plainfolist.size()+"\r\n\r\n");
			sb.append("--plamap content:\r\n");
			sb.append(sb1.toString());
			sb.append("\r\n--session_plamap content:\r\n");
			sb.append(sb2.toString());
			sb.append("\r\n--plainfomap content:\r\n");
			sb.append(sb3.toString());
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	//----------------静态区-----------------
	
	private static SocketInfoMgr instance = new SocketInfoMgr();
	
	/**
	 * 获取实例
	 */
	public static SocketInfoMgr getInstance(){
		return instance;
	}
}
