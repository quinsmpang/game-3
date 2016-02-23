package com.moonic.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.servlet.STSServlet;

import conf.Conf;
import conf.LogTbName;

/**
 * 发请求
 * @author John
 */
public class NetSender {
	public static final byte RV_STR = 1;
	public static final byte RV_BYTE = 2;
	
	private ByteArrayOutputStream baos;
	public DataOutputStream dos;
	
	public short act;
	public byte rvtype;
	
	public boolean encryption;
	
	/**
	 * 构造
	 */
	public NetSender(short act) throws Exception {
		this(act, RV_STR);
	}
	
	/**
	 * 构造
	 */
	public NetSender(short act, byte rvtype) throws Exception {
		baos = new ByteArrayOutputStream();
		dos = new DataOutputStream(baos);
		
		this.rvtype = rvtype;
		this.act = act;
		dos.writeShort(act);
	}
	
	/**
	 * 发送数据
	 * @param urlStr 地址
	 * @param nsd 数据
	 * @return JSONARR：0.结果 1.数据
	 */
	public NetResult send(String urlStr) {
		return send((byte)-1, 0, null, urlStr);
	}
	
	/**
	 * 发送数据
	 * @param urlStr 地址
	 * @param nsd 数据
	 * @return JSONARR：0.结果 1.数据
	 */
	public NetResult send(byte type, int serverid, String name, String urlStr) {
		NetResult nr = new NetResult();
		nr.servertype = type;
		nr.serverid = serverid;
		nr.name = name;
		nr.urlStr = urlStr;
		try {
			URL url = new URL(urlStr);			
			HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();	
			urlCon.setConnectTimeout(10000);
			urlCon.setReadTimeout(10000);
			urlCon.setRequestMethod("POST");
			urlCon.setDoInput(true);
			urlCon.setDoOutput(true);
			urlCon.setUseCaches(false);
			
			DataOutputStream dos = new DataOutputStream(urlCon.getOutputStream());
			dos.write(encryption?EncryptionUtil.RC4(baos.toByteArray()):baos.toByteArray());
			dos.close();
			int statusCode = urlCon.getResponseCode();
			if(statusCode!=200){
				if(rvtype == RV_STR){
					nr.strData = "statusCode="+statusCode;
				} else 
				if(rvtype == RV_BYTE){
					nr.buff = null;
				}
				BACException.throwInstance("statusCode="+statusCode);
			}
			DataInputStream dis = new DataInputStream(urlCon.getInputStream());
			byte[] buffer = new byte[4096];
			int len = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while((len = dis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			buffer = baos.toByteArray();
			dis.close();
			if(encryption){
				buffer = EncryptionUtil.RC4(buffer);
			}
			nr.result = buffer[0];
			byte[] tmp = new byte[buffer.length-1];
			System.arraycopy(buffer, 1, tmp, 0, buffer.length-1);
			buffer = tmp;
			if(rvtype == RV_STR){
				nr.strData = new String(buffer, "UTF-8");
				nr.rv = new ReturnValue(nr.result==1?true:false, nr.strData);
			} else 
			if(rvtype == RV_BYTE){
				nr.buff = buffer;
				nr.rv = new ReturnValue(nr.result==1?true:false, nr.buff);
			}
		} catch (Exception e) {
			SqlString sqlStr = new SqlString();
			sqlStr.add("sendreqserver", Conf.stsKey);
			sqlStr.add("accessurl", urlStr);
			sqlStr.add("reqinfo", "ACT="+act);
			sqlStr.add("excinfo", e.toString());
			sqlStr.addDateTime("createtime", MyTools.getTimeStr());
			DBHelper.logInsert(LogTbName.TAB_ACCESS_SERVER_EXC_LOG(), sqlStr);
			//e.printStackTrace();
			STSServlet.stslog.d(e.toString());
			nr.rv = new ReturnValue(false, e.toString());
		}
		STSServlet.stslog.d("向 " + urlStr + " 发送请求 " + act + " 结果：" + nr.result);
		return nr;
	}
	
	//---------测试----------
	
	public static int amount = 1;
	
	/**
	 * 测试
	 */
	public static void main(String[] args) throws Exception {
		for(int i=0; i < 500; i++) {
			(new Thread(new Runnable() {
				public void run() {
					while(true) {
						//String urlStr = "http://xm.s1.pook.com/xianmo_player/xianmo.do";
						//String urlStr = "http://xmlogin.pook.com/xianmo_user/xianmo.do";
						//String urlStr = "http://xmlogintest.pook.com:82/xianmo_user/xianmo.do";
						String urlStr = "http://192.168.12.4/xianmo_user/xianmo.do";
						NetSender sender;
						try {
							long t1=System.currentTimeMillis();
							sender = new NetSender((short)101);
							sender.dos.writeUTF("0");
							sender.dos.writeUTF("001");
							NetResult nr = sender.send((byte)0, 3, "开发用户服", urlStr);
							long t2=System.currentTimeMillis();
							System.gc();
							System.out.println("第"+(amount++)+"次请求 "+nr.result+","+nr.strData+" 消耗"+(t2-t1)+"毫秒");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							Thread.sleep(4000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			})).start();
			Thread.sleep(100);
		}		
	}
}
