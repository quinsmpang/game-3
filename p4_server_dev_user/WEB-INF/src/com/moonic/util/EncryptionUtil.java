package com.moonic.util;

/**
 * 数据加密解密
 * @author huangyan
 *
 */
public class EncryptionUtil
{
	public static String Encrypt_Key = "9A33B313F0485A5448080B99C1576CD4";  //秘钥

	/**
	 * PC4算法加解密，加密和解密是同一个方法
	 * @param input
	 * @return
	 */
	public static byte[] RC4(byte[] input) 
	{
		if(input==null)return null;
		if(input.length==0)return input;
		
		byte[] result = new byte[input.length];
		int x, y, j = 0;
		int[] box = new int[256];

		for (int i = 0; i < 256; i++) {
			box[i] = i;
		}

		for (int i = 0; i < 256; i++) {
			char ascii = Encrypt_Key.charAt(i % Encrypt_Key.length());
			j = (ascii + box[i] + j) % 256;
			x = box[i];
			box[i] = box[j];
			box[j] = x;
		}

		for (int i = 0; i < input.length; i++) {
			y = i % 256;
			j = (box[y] + j) % 256;
			x = box[y];
			box[y] = box[j];
			box[j] = x;

			result[i] = (byte) (input[i] ^ box[(box[y] + box[j]) % 256]);
		}
		return result;
	}

}
