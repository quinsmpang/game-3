package com.moonic.util;

import java.security.MessageDigest;

public class MD5 {
	public static String encode(String inStr)
	{
		return encode(inStr,"UTF-8");
	}
	public static String encode(String inStr,String encode)
	{
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] byteArray = inStr.getBytes(encode);
			byte[] md5Bytes = md5.digest(byteArray);
			StringBuffer hexValue = new StringBuffer();
			
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
}