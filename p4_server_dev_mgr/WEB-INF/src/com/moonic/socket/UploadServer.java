package com.moonic.socket;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import server.common.Tools;
import server.config.ServerConfig;

import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 上传服务
 * @author John
 */
public class UploadServer {
	private ServerSocket serversocket;
	private boolean isRun;

	private int port = Conf.uploadServerPort;
	private ScheduledExecutorService timer;
	/**
	 * 构造
	 */
	private UploadServer() {}
	
	/**
	 * 启动服务
	 */
	public void start() {
		if (!isRun) {
			try {
				isRun = true;
				(new UploadThread()).start();
				System.out.println(Tools.getCurrentDateTimeStr()+"--" +Conf.stsKey+"启动上传服务完成,开始监听" + port + "端口");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			System.out.println(Tools.getCurrentDateTimeStr()+"--" +Conf.stsKey+"对" + port + "端口的TCP上传监听服务还在运行中，请先停止。");
		}
	}
	
	/**
	 * 停止服务
	 */
	public void stop() {
		isRun = false;
		MyTools.cancelTimer(timer);
		timer=null;
		if (serversocket != null) {
			try {				
				serversocket.close();
				serversocket=null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			instance=null;
		}
	}
	
	//--------------内部类--------------
	
	/**
	 * 连接现成
	 */
	class UploadThread extends Thread {
		public void run() {
			try {
				serversocket = new ServerSocket(port);
				while (isRun) {
					try {
						Socket socket = serversocket.accept();
						System.out.println(Tools.getCurrentDateTimeStr()+"--" +Conf.stsKey+"成功建立来自" + socket.getRemoteSocketAddress() + "的上传TCP连接");
						DoUploadThread doUploadThread = new DoUploadThread(socket);
						(new Thread(doUploadThread)).start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				UploadServer.this.stop();
				System.out.println(Tools.getCurrentDateTimeStr()+"--" +Conf.stsKey+"端口" + port + "的上传服务已停止");
			}
		}
	}
	
	/**
	 * 上传线程
	 */
	class DoUploadThread implements Runnable {
		private Socket socket;
		

		public DoUploadThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				SessionidTT sessionidTT = new SessionidTT(dis);
				
				timer.schedule(sessionidTT, 10 * 60 * 1000, TimeUnit.MILLISECONDS);
				byte[] uploadBytes = Tools.getBytesFromInputstream(dis);
				sessionidTT.cancel();
				int splitIndex = -1;
				for (int i = 0; i < uploadBytes.length; i++) {
					if (uploadBytes[i] == ';') {
						splitIndex = i;
						break;
					}
				}
				if (splitIndex > 0) {
					String fileInfo = new String(uploadBytes, 0, splitIndex);
					int pathSplit = fileInfo.indexOf('#');
					if (pathSplit != -1) {
						String subpath = fileInfo.substring(0, pathSplit);
						String filename = fileInfo.substring(pathSplit + 1);
						String fullPath = ServerConfig.getAppRootPath() + "download/" + subpath;
						File folder = new File(fullPath);
						if (!folder.exists()) {
							folder.mkdirs();
						}
						File file = new File(fullPath + filename);
						System.out.println(Tools.getCurrentDateTimeStr()+"--" +Conf.stsKey + "写文件路径=" + (fullPath + filename));
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(uploadBytes, splitIndex + 1, uploadBytes.length - (splitIndex + 1));
						fos.close();
						System.out.println(Tools.getCurrentDateTimeStr()+"--" +Conf.stsKey+ "上传文件成功,写入" + (uploadBytes.length - (splitIndex + 1)) + "字节");
					}
				} else {
					System.out.println(Tools.getCurrentDateTimeStr()+"--" +Conf.stsKey+ "未找到文件分隔符;");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 超时控制
	 */
	class SessionidTT extends MyTimerTask {
		public DataInputStream dis;

		public SessionidTT(DataInputStream dis) {
			this.dis = dis;
		}

		public void run2() {
			try {
				System.out.println(Tools.getCurrentDateTimeStr()+"--" +Conf.stsKey+ "上传超时10分钟，自动断开");
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//--------------静态区--------------
	
	private static UploadServer instance;
	
	/**
	 * 获取实例
	 */
	public static UploadServer getInstance() 
	{
		if(instance==null)
		{
			instance = new UploadServer();
			instance.timer = MyTools.createTimer(1);
		}
		return instance;
	}
}
