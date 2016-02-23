package com.moonic.util;

import org.json.JSONArray;

/**
 * 群发网络请求
 * @author John
 */
public class MassNetSender {
	private JSONArray types = new JSONArray();
	private JSONArray serverids = new JSONArray();
	private JSONArray names = new JSONArray();
	private JSONArray urls = new JSONArray();
	
	public NetResult[] results;
	
	/**
	 * 加发请求地址
	 */
	public void addURL(byte type, int serverid, String name, String url){
		types.add(type);
		serverids.add(serverid);
		names.add(name);
		urls.add(url);
	}
	
	private NetSender sender;
	
	/**
	 * 发送
	 */
	public NetResult[] send(NetSender sender){
		this.sender = sender;
		results = new NetResult[urls.size()];
		ReqSender[] reqSenders = new ReqSender[urls.size()];
		for(int i = 0; i < reqSenders.length; i++){
			reqSenders[i] = new ReqSender(i, (byte)types.optInt(i), serverids.optInt(i), names.optString(i), urls.optString(i));
			reqSenders[i].start();
		}
		while(true){
			boolean end = true;
			for(int i = 0; i < reqSenders.length; i++){
				if(!reqSenders[i].end){
					end = false;
					break;
				}
			}
			if(end){
				break;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
	
	/**
	 * 发送者
	 * @author John
	 */
	class ReqSender extends Thread {
		private int index;
		private byte servertype;
		private int serverid;
		private String name;
		private String urlStr;
		private boolean end;
		public ReqSender(int index, byte servertype, int serverid, String name, String urlStr){
			this.index = index;
			this.servertype = servertype;
			this.serverid = serverid;
			this.name = name;
			this.urlStr = urlStr;
		}
		public void run() {
			NetResult nr = sender.send(servertype, serverid, name, urlStr);
			results[index] = nr;
			end = true;
		}
	}
}
