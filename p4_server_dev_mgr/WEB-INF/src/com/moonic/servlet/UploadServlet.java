package com.moonic.servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ehc.common.ReturnValue;
import com.moonic.bac.VersionBAC;

public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 4598035092703154800L;
	
	/**
	 * 上传程序包
	 */
	public static final byte ACT_UPLOAD_APK = 1;
	
	/**
	 * 上传资源列表文件CRC
	 */
	public static final byte ACT_UPLOAD_RES = 2;
	/**
	 * 上传apk升级补丁包
	 */
	public static final byte ACT_UPLOAD_PATCH = 4;
	
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		ReturnValue val = null;
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(request.getInputStream());
			byte act = dis.readByte();
			System.out.println("act="+act);
			if(act == ACT_UPLOAD_APK){
				val = VersionBAC.getInstance().uploadApk(dis);
			} 
			else 
			if(act == ACT_UPLOAD_PATCH){
				val = VersionBAC.getInstance().uploadPatch(dis);
			}
			else
			if(act == ACT_UPLOAD_RES){
				val = VersionBAC.getInstance().uploadRes(dis);
			} 			
			else 
			{
				new ReturnValue(false, "无效请求");
			}
		} catch (Exception e) {
			val = new ReturnValue(false, e.toString());
		} finally {
			try {
				dis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(response.getOutputStream());
			dos.writeByte(val.success?1:0);
			dos.write(val.info.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
