package com.moonic.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.moonic.bac.SaveFileBAC;

public class STSServlet extends HttpServlet {
	private static final long serialVersionUID = 4598035092703154800L;
	
	//-----------------资源服-----------------
	
	/**
	 * 存文件
	 */
	public static final short R_SAVE_FILE = 101;
	
	/**
	 * service
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InputStream is = request.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[4096];
		int readLen = -1;
		while ((readLen = is.read(buff)) != -1) {
			baos.write(buff, 0, readLen);
		}
		buff = baos.toByteArray();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buff));
		DataOutputStream dos = new DataOutputStream(response.getOutputStream());
		
		try {
			//String ip = IPAddressUtil.getIp(request);
			
			ReturnValue val = null;
			if (buff.length == 0) {
				val = new ReturnValue(false, "无效请求");
			} else {
				try {
					val = processingReq(request, response, dis, dos);		
				} catch (EOFException e) {
					DataInputStream edis = new DataInputStream(new ByteArrayInputStream(buff));
					int act = edis.readShort();
					System.out.println(e.toString()+"(act="+act+")");
					e.printStackTrace();
					val = new ReturnValue(false, e.toString());
				} catch (Exception e) {
					e.printStackTrace();
					val = new ReturnValue(false, e.toString());
				}
			}
			dis.close();
			byte[] responseData = null;
			if(val.getDataType()==ReturnValue.TYPE_STR) {
				responseData = Tools.strNull(val.info).getBytes("UTF-8");
			} else 
			if(val.getDataType()==ReturnValue.TYPE_BINARY) {
				responseData = val.binaryData;
			}
			dos.writeByte(val.success ? 1 : 0);
			dos.write(responseData);
		} catch (Exception e) {
			e.printStackTrace();
			dos.writeByte(0);
			dos.write(e.toString().getBytes("UTF-8"));
		}
		finally
		{
			dos.close();
		}
	}
	
	/**
	 * 处理请求
	 */
	private ReturnValue processingReq(HttpServletRequest request, HttpServletResponse response, DataInputStream dis, DataOutputStream dos) throws Exception{
		short act = dis.readShort();
		dis.readUTF();
		
		if(act == R_SAVE_FILE){
			String savepath = dis.readUTF();
			int fileLen = dis.readInt();
			byte[] filedata = new byte[fileLen];
			dis.read(filedata);
			return SaveFileBAC.getInstance().saveFile(savepath, filedata);
		} else 
		{
			return new ReturnValue(false, "无效请求 " + act);
		}
	}
}
