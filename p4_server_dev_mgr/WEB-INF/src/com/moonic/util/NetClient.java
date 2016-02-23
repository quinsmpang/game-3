package com.moonic.util;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.ehc.common.ReturnValue;


/**
 * 联网客户端
 * @author alexhy
 *
 */
public class NetClient
{

	private boolean allowWatch;

	private static String proxyStr;
	private static int port;


	private String address;
	private int act; // 发送动作
	private NetListener listener; // 监听者
	private byte[] sendBytes; // 发送数据流
	private List<NameValuePair> params; //form
	private List<NameValuePair> httpHeadParams; //http头额外参数
	
	private boolean success; //联网是否成功
	private byte[] returnBytes;//联网返回的数据
	private String contentType="application/octet-stream";

	public NetClient()
	{

	}

	/**
	 * 设置http body内容类型
	 * @param type
	 */
	public void setContentType(String type)
	{
		contentType = type;
	}
	/**
	 * 设置http提交地址
	 * @param theAddress
	 */
	public void setAddress(String theAddress)
	{
		address = theAddress;
	}	
	/**
	 * 设置业务act参数
	 * @param act
	 */
	public void setAct(int act)
	{
		this.act = act;
	}	

	/**
	 * 设置http body二进制内容
	 * @param sendBytes
	 */
	public void setSendBytes(byte[] sendBytes)
	{
		this.sendBytes = sendBytes;
	}

	/**
	 * 添加http body表单参数
	 * @param name
	 * @param value
	 */
	public void addParameter(String name, String value)
	{
		if (params == null)
		{
			params = new ArrayList<NameValuePair>();
		}
		params.add(new BasicNameValuePair(name, value));
	}

	/**
	 * 添加http body表单参数
	 * @param name
	 * @param value
	 */
	public void addParameter(String name, int value)
	{
		if (params == null)
		{
			params = new ArrayList<NameValuePair>();
		}
		params.add(new BasicNameValuePair(name, String.valueOf(value)));
	}
	/**
	 * 添加http head参数
	 * @param name
	 * @param value
	 */
	public void addHttpHead(String name, String value)
	{
		if (httpHeadParams == null)
		{
			httpHeadParams = new ArrayList<NameValuePair>();
		}
		httpHeadParams.add(new BasicNameValuePair(name, value));
	}
	/**
	 * 添加http head参数
	 * @param name
	 * @param value
	 */
	public void addHttpHead(String name, int value)
	{
		if (httpHeadParams == null)
		{
			httpHeadParams = new ArrayList<NameValuePair>();
		}
		httpHeadParams.add(new BasicNameValuePair(name, String.valueOf(value)));
	}
	/**
	 * 同步发送方式
	 * @return
	 */
	public ReturnValue send()
	{
		if (sendBytes != null)
		{				
			HttpURLConnection httpPost=null;
			try {
				
				ByteArrayOutputStream byteArrayOut = null;
				URL url = new URL(address);
				httpPost = (HttpURLConnection)url.openConnection();
				httpPost.setReadTimeout(10000);
				httpPost.setConnectTimeout(10000);
				OutputStream out = null;
				InputStream in = null;
				
				httpPost.setRequestMethod("POST");
				httpPost.setDoInput(true);
				httpPost.setDoOutput(true);
				httpPost.setUseCaches(false);
				
				//httpPost.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				//httpPost.setRequestProperty("Content-Type", "application/octet-stream");
				httpPost.setRequestProperty("Content-Type", contentType);
				for(int i=0;httpHeadParams!=null && i<httpHeadParams.size();i++)
				{
					NameValuePair nameValue = httpHeadParams.get(i);
					httpPost.setRequestProperty(nameValue.getName(), nameValue.getValue());
				}
				
				out = httpPost.getOutputStream();
				out.write(sendBytes);
				out.flush();
				out.close();	
				int statusCode = httpPost.getResponseCode();
				if(statusCode==200)
				{
					in = httpPost.getInputStream();
					byteArrayOut = new ByteArrayOutputStream();
					byte[] buf = new byte[4096];
					int len = 0;
					while ((len = in.read(buf)) != -1) 
					{
						byteArrayOut.write(buf, 0, len);
					}
					byte[] bytes = byteArrayOut.toByteArray();
					in.close();
					if(bytes!=null && bytes.length>0)
					{
						return new ReturnValue(true,bytes);
					}
					else
					{
						return new ReturnValue(false,"没有数据");
					}
				}
				else
				{
					return new ReturnValue(false,"联网失败,statusCode="+statusCode);					
				}					
			} 
			catch (Exception e) {
				//e.printStackTrace();
				System.out.println("连接"+address+"发生异常"+e.toString());
				return new ReturnValue(false,"联网失败"+e.toString());
			} 
			finally 
			{					
				if (httpPost != null) 
				{
					httpPost.disconnect();
				}
			}				
		}
		else
		if (params != null)
		{
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			HttpConnectionParams.setSoTimeout(httpParams, 10000);
			
			// 新建HttpClient对象
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			
			if (proxyStr != null && port > 0)
			{
				HttpHost proxy = new HttpHost(proxyStr, port);
				httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			//System.out.println("向"+address+"发送数据");
			HttpPost post = new HttpPost(address);
			/*if (sendBytes != null)
			{
				ContentProducer cp = new ContentProducer()
				{
					public void writeTo(OutputStream outstream) throws IOException
					{
						outstream.write(sendBytes);
						//System.out.println("发送数据"+new String(sendBytes,"UTF-8"));
					}
				};
				HttpEntity entity = new EntityTemplate(cp);
				post.setEntity(entity);			
			}
			else if (params != null)*/
			{
				try
				{
					post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
					post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}
				catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
			}
			for(int i=0;httpHeadParams!=null && i<httpHeadParams.size();i++)
			{
				NameValuePair nameValue = httpHeadParams.get(i);
				post.addHeader(nameValue.getName(), nameValue.getValue());
			}

			HttpResponse response = null;

			try
			{
				response = httpClient.execute(post);
				int statusCode = response.getStatusLine().getStatusCode();
				//System.out.println("statusCode="+statusCode);
				if (statusCode == 200)
				{
					/** 
					 * 因为直接调用toString可能会导致某些中文字符出现乱码的情况。所以此处使用toByteArray 
					 * 如果需要转成String对象，可以先调用EntityUtils.toByteArray()方法将消息实体转成byte数组， 
					 * 在由new String(byte[] bArray)转换成字符串。 
					 */
					byte[] buff = EntityUtils.toByteArray(response.getEntity());
					if (buff != null)
					{					
						return new ReturnValue(true,buff);					
					}
					else
					{
						return new ReturnValue(false,"无有效数据");		
					}
				}
				else
				{	
					return new ReturnValue(false,"联网失败,statusCode="+statusCode);
				}
			}		
			catch (Exception e)
			{				
				//e.printStackTrace();
				System.out.println("连接"+address+"发生异常"+e.toString());
				return new ReturnValue(false,"联网失败"+e.toString());
			}	
		}
		else //get发送方式
		{
			HttpURLConnection httpPost=null;
			try {
				ByteArrayOutputStream byteArrayOut = null;
				URL url = new URL(address);
				httpPost = (HttpURLConnection)url.openConnection();
				httpPost.setReadTimeout(10000);
				httpPost.setConnectTimeout(10000);
				InputStream in = null;
				
				httpPost.setRequestMethod("GET");
				httpPost.setDoInput(true);
				httpPost.setDoOutput(false);
				httpPost.setUseCaches(false);
				
				//httpPost.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				//httpPost.setRequestProperty("Content-Type", "application/octet-stream");
				httpPost.setRequestProperty("Content-Type", contentType);
				for(int i=0;httpHeadParams!=null && i<httpHeadParams.size();i++)
				{
					NameValuePair nameValue = httpHeadParams.get(i);
					httpPost.setRequestProperty(nameValue.getName(), nameValue.getValue());
				}
				
				int statusCode = httpPost.getResponseCode();
				if(statusCode==200)
				{
					in = httpPost.getInputStream();
					byteArrayOut = new ByteArrayOutputStream();
					byte[] buf = new byte[4096];
					int len = 0;
					while ((len = in.read(buf)) != -1) 
					{
						byteArrayOut.write(buf, 0, len);
					}
					byte[] bytes = byteArrayOut.toByteArray();
					in.close();
					if(bytes!=null && bytes.length>0)
					{
						return new ReturnValue(true,bytes);
					}
					else
					{
						return new ReturnValue(false,"没有数据");
					}
				}
				else
				{
					return new ReturnValue(false,"联网失败,statusCode="+statusCode);					
				}					
			} 
			catch (Exception e) {
				//e.printStackTrace();
				System.out.println("连接"+address+"发生异常"+e.toString());
				return new ReturnValue(false,"联网失败"+e.toString());
			} 
			finally 
			{					
				if (httpPost != null) 
				{
					httpPost.disconnect();
				}
			}
		}
	}
	/**
	 * 异步发送方式
	 * @param listener 监听器
	 */
	public void send(NetListener listener)
	{
		this.listener = listener;
		(new Thread(new NetSender())).start();
	}
	class NetSender implements Runnable
	{
		public void run()
		{
			if (sendBytes != null)
			{				
				HttpURLConnection httpPost=null;
				try {
					ByteArrayOutputStream byteArrayOut = null;
					URL url = new URL(address);
					httpPost = (HttpURLConnection)url.openConnection();
					httpPost.setConnectTimeout(10000);
					httpPost.setReadTimeout(10000);
					OutputStream out = null;
					InputStream in = null;
					
					httpPost.setRequestMethod("POST");
					httpPost.setDoInput(true);
					httpPost.setDoOutput(true);
					httpPost.setUseCaches(false);
					
					//httpPost.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					//httpPost.setRequestProperty("Content-Type", "application/octet-stream");
					httpPost.setRequestProperty("Content-Type", contentType);
					for(int i=0;httpHeadParams!=null && i<httpHeadParams.size();i++)
					{
						NameValuePair nameValue = httpHeadParams.get(i);
						httpPost.setRequestProperty(nameValue.getName(), nameValue.getValue());
					}
					
					out = httpPost.getOutputStream();
					out.write(sendBytes);
					out.flush();
					out.close();	
					int statusCode = httpPost.getResponseCode();
					if(statusCode==200)
					{
						in = httpPost.getInputStream();
						byteArrayOut = new ByteArrayOutputStream();
						byte[] buf = new byte[4096];
						int len = 0;
						while ((len = in.read(buf)) != -1) 
						{
							byteArrayOut.write(buf, 0, len);
						}
						byte[] bytes = byteArrayOut.toByteArray();
						in.close();
						if(bytes!=null && bytes.length>0)
						{
							listener.callBack(act, NetListener.RESULT_SUCCESS, bytes);
						}
						else
						{
							listener.callBack(act, NetListener.RESULT_FAIL, "没有数据");
						}
					}
					else
					{
						listener.callBack(act, NetListener.RESULT_NETFAILURE, "联网失败,statusCode="+statusCode);
					}					
				} 
				catch (Exception e) {
					e.printStackTrace();
					listener.callBack(act, NetListener.RESULT_NETFAILURE, "联网失败"+e.toString());
				} 
				finally 
				{					
					if (httpPost != null) 
					{
						httpPost.disconnect();
					}
				}				
			}
			else
			if (params != null)
			{
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
				HttpConnectionParams.setSoTimeout(httpParams, 10000);
				
				// 新建HttpClient对象
				HttpClient httpClient = new DefaultHttpClient(httpParams);
				
				if (proxyStr != null && port > 0)
				{
					HttpHost proxy = new HttpHost(proxyStr, port);
					httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				}
				
				HttpPost post = new HttpPost(address);
				/*if (sendBytes != null)
				{
					ContentProducer cp = new ContentProducer()
					{
						public void writeTo(OutputStream outstream) throws IOException
						{
							outstream.write(sendBytes);
						}
					};
					HttpEntity entity = new EntityTemplate(cp);
					post.setEntity(entity);
				}
				else 
				if (params != null)*/
				{
					try
					{
						post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
						post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					}
					catch (UnsupportedEncodingException e)
					{
						e.printStackTrace();
					}
				}
				for(int i=0;httpHeadParams!=null && i<httpHeadParams.size();i++)
				{
					NameValuePair nameValue = httpHeadParams.get(i);
					post.addHeader(nameValue.getName(), nameValue.getValue());
				}
				
				HttpResponse response = null;

				try
				{
					response = httpClient.execute(post);
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode == 200)
					{
						/** 
						 * 因为直接调用toString可能会导致某些中文字符出现乱码的情况。所以此处使用toByteArray 
						 * 如果需要转成String对象，可以先调用EntityUtils.toByteArray()方法将消息实体转成byte数组， 
						 * 在由new String(byte[] bArray)转换成字符串。 
						 */
						byte[] buff = EntityUtils.toByteArray(response.getEntity());
						if (buff != null)
						{						
							listener.callBack(act, NetListener.RESULT_SUCCESS,buff);
						}
						else
						{
							listener.callBack(act, NetListener.RESULT_FAIL, "没有数据");
						}					
						
					}
					else
					{						
						listener.callBack(act, NetListener.RESULT_NETFAILURE, "联网失败statusCode="+statusCode);
					}
				}			
				catch (Exception e)
				{
					e.printStackTrace();
					listener.callBack(act, NetListener.RESULT_NETFAILURE, "联网失败"+e.toString());
				}	
			}
			else
			{
				HttpURLConnection httpPost=null;
				try {
					ByteArrayOutputStream byteArrayOut = null;
					URL url = new URL(address);
					httpPost = (HttpURLConnection)url.openConnection();
					httpPost.setConnectTimeout(10000);
					httpPost.setReadTimeout(10000);
					
					InputStream in = null;
					
					httpPost.setRequestMethod("GET");
					httpPost.setDoInput(true);
					httpPost.setDoOutput(false);
					httpPost.setUseCaches(false);
					
					//httpPost.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					//httpPost.setRequestProperty("Content-Type", "application/octet-stream");
					httpPost.setRequestProperty("Content-Type", contentType);
					for(int i=0;httpHeadParams!=null && i<httpHeadParams.size();i++)
					{
						NameValuePair nameValue = httpHeadParams.get(i);
						httpPost.setRequestProperty(nameValue.getName(), nameValue.getValue());
					}
					
					int statusCode = httpPost.getResponseCode();
					if(statusCode==200)
					{
						in = httpPost.getInputStream();
						byteArrayOut = new ByteArrayOutputStream();
						byte[] buf = new byte[4096];
						int len = 0;
						while ((len = in.read(buf)) != -1) 
						{
							byteArrayOut.write(buf, 0, len);
						}
						byte[] bytes = byteArrayOut.toByteArray();
						in.close();
						if(bytes!=null && bytes.length>0)
						{
							listener.callBack(act, NetListener.RESULT_SUCCESS, bytes);
						}
						else
						{
							listener.callBack(act, NetListener.RESULT_FAIL, "没有数据");
						}
					}
					else
					{
						listener.callBack(act, NetListener.RESULT_NETFAILURE, "联网失败,statusCode="+statusCode);
					}					
				} 
				catch (Exception e) {
					e.printStackTrace();
					listener.callBack(act, NetListener.RESULT_NETFAILURE, "联网失败"+e.toString());
				} 
				finally 
				{					
					if (httpPost != null) 
					{
						httpPost.disconnect();
					}
				}	
			}
		}
	}

	
	public void ignoreSSL()
	{			
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs,
						String authType) {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs,
						String authType) {
				}
			} };
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String string, SSLSession ssls) {
							return true;
						}
					});				
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public static void main(String[] args)
	{
		/*NetClient.getGiftList(new NetListener()
		{
			public void callBack(int act, int result, String strData)
			{
				System.out.println(strData);
			}
		});*/
		/*NetClient.orderCallBack(new NetListener()
		{
			public void callBack(int act, int result, String strData)
			{
				System.out.println(strData);
			}
		});*/
		/*try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs,
						String authType) {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs,
						String authType) {
				}
			} };
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String string, SSLSession ssls) {
							return true;
						}
					});
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			URL url = new URL(createSessionURL);
			HttpURLConnection connect = (HttpURLConnection) url .openConnection();
			connect.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(connect .getOutputStream());
			out.writeBytes(postData);
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader( connect.getInputStream()));

			String line;

			while ((line = in.readLine()) != null) {
				// sessionId += "n" + line;
				System.out.println("page info ===> " + line);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
}
