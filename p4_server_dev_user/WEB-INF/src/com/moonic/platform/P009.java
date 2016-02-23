package com.moonic.platform;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.*;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import server.common.Tools;
import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.moonic.mgr.PookNet;
import com.moonic.util.BACException;
import com.moonic.util.Base64;
import com.moonic.util.Base64Amigo;
import com.moonic.util.NetClient;

/**
 * 金立
 * @author 
 */
public class P009 extends P
{
	static String apiKey = "DC106C2AF5A24188B6A9DA5111FA4953";
	static String secretKey = "410F8AA5069B439186BCF119F553313B"; //商户私钥
	static String payPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALROUjgOxUy9F93ewilulnPaLgeBCBQl6fd4TzD3RvvYRZJ/loXpnniqpkn+h++S5WaIr0keXVltmgUg9Hr4LXL46P1JHZf7qb0kGCmeaHtOIohLcywi1p14Qcxpr2wzC1boC1TXq6shXfPrU3HCFDJwegrVWS+UX7a+b/WKktxNAgMBAAECgYA4Mjj589CWjFEF+8m0NB18WkICYLrt3KtBUCQOdOOQ1uVINa/qaNfVcAqIAWbIypXpESzgszAREVHxy0WnlfZD2XZfGwpVdry3hsXtdNCClCGdJ+26SDw4z9eQgKo8PEdrF6h3BxF+Yi2VTLzL/k6GLOFrL5b4o+4xo940XD8eAQJBAP3/VNwJbG2CUvJax1NsN/dCvdh00vk6JyXybv2p3heEsbEVgIAkUxGnARwwCpWKvcCJCT/F07Ki58YDkGFkuEECQQC1ukA1af14a12eFQ/xBgvilxVAjKvBevX1DzdrPENanySP4BBBdsg8e6x4O6H9gad6h9pksdyI5JGVME8SbUENAkEA4T18/kepDI+miN6xpivwkWdxPTOUJ4lHUCJmBLKPjaGRhe0AcCJGPAAyPtHngOdxD/0144TGD8Lg8DsW3RdZQQJAFZv1IKTe2B/kPPUCNUXjyejvRQK1NceaUa6ih2TeGXuNDHK3XCF0xARsyMtGgu+U67QV2x1vPPP4/8WD0YweTQJBAIsI31BttXrmH+nIQwjzuDhexZJ2CTtG1yEIN/XI6J4EKo0ima4KS6qdxnjPscwV0hFiVMv0CgPvWeGhEDLotlk="; //支付私钥

	public ReturnValue checkLogin(String username, String extend, String ip) throws Exception
	{
		if (extend == null || extend.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少扩展参数platform=" + platform + ",username=" + username + ",ip=" + ip);
			BACException.throwInstance("帐号渠道" + platform + ",登录缺少扩展参数");
		}
		JSONObject extendJson = null;
		try
		{
			extendJson = new JSONObject(extend);
		}
		catch (Exception ex)
		{
			LogBAC.logout("login_error", "platform=" + platform + ",扩展参数异常extend=" + extend);
			BACException.throwInstance(platform + "渠道,登录扩展参数异常");
		}

		String token = extendJson.optString("token");
		//int guest = extendJson.optInt("guest");

		if (username == null || username.equals(""))
		{
			BACException.throwInstance("用户名不能为空");
		}

//		if (guest == 0) //正式用户才验证
//		{
		if (token.equals(""))
		{
			LogBAC.logout("login_error", "platform=" + platform + ",缺少参数username=" + username + ",token=" + token + ",extend=" + extend);
			BACException.throwInstance(platform + "渠道,缺少参数");
		}

		//return new ReturnValue(true, username);

		String BASE_URL = "https://id.gionee.com";

		String uri = "/account/verify.do";

		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		String host = "id.gionee.com";
		String port = "443";
		String method = "POST";

		Long ts = System.currentTimeMillis() / 1000;
		String nonce = StringUtil.randomStr().substring(0, 8);
		String mac = CryptoUtility.macSig(host, port, secretKey, ts.toString(), nonce, method, uri);
		mac = mac.replace("\n", "");
		StringBuilder authStr = new StringBuilder();
		authStr.append("MAC ");
		authStr.append(String.format("id=\"%s\"", apiKey));
		authStr.append(String.format(",ts=\"%s\"", ts));
		authStr.append(String.format(",nonce=\"%s\"", nonce));
		authStr.append(String.format(",mac=\"%s\"", mac));

		String url = BASE_URL + uri;//请求地址+接口名
		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/json");

		// 设置请求头		
		netClient.addHttpHead("Authorization", authStr.toString());

		netClient.setSendBytes(token.getBytes());
		ReturnValue rv = netClient.send();

		if (rv.success)
		{
			if (rv.dataType == ReturnValue.TYPE_BINARY)
			{
				try
				{
					String result = new String(rv.binaryData, "UTF-8");
					JSONObject resultJson = new JSONObject(result);
					String r = resultJson.optString("r");
					if (r == null || r.equals(""))
					{
						//String usergameid = resultJson.optString("u");
						String usergameid = username;
						return new ReturnValue(true, usergameid);
					}
					else
					{
						LogBAC.logout("login_error", "2232 platform=" + platform + ",金立返回=" + result + ",金立用户验证失败,错误码=" + r);
						LogBAC.logout("login_error", "2233 username=" + username + ",token=" + token + ",extend=" + extend);
						LogBAC.logout("login_error", "2234 Authorization=" + authStr.toString());
						return new ReturnValue(false, "金立用户验证失败,错误码=" + r);
					}
				}
				catch (Exception ex)
				{
					LogBAC.logout("login_error", "2240 platform=" + platform + ",用户验证失败" + ex.toString() + ",str=" + new String(rv.binaryData, "UTF-8"));
					LogBAC.logout("login_error", "2241 username=" + username + ",token=" + token + ",extend=" + extend);
					LogBAC.logout("login_error", "2242 Authorization=" + authStr.toString());
					return new ReturnValue(false, "用户验证失败" + ex.toString());
				}
			}
			else
			{
				LogBAC.logout("login_error", "2248 platform=" + platform + ",用户验证失败,数据格式异常");
				LogBAC.logout("login_error", "2249 username=" + username + ",token=" + token + ",extend=" + extend);
				LogBAC.logout("login_error", "2250 Authorization=" + authStr.toString());
				return new ReturnValue(false, "用户验证失败,数据格式异常");
			}
		}
		else
		{
			LogBAC.logout("login_error", "2256 platform=" + platform + ",用户验证失败," + rv.info);
			LogBAC.logout("login_error", "2257 username=" + username + ",token=" + token + ",extend=" + extend);
			LogBAC.logout("login_error", "2258 Authorization=" + authStr.toString());
			return new ReturnValue(false, "用户验证失败," + rv.info);
		}
	}

	String url = "https://pay.gionee.com/order/create";

	public ReturnValue getOrderInfo(int playerId, String out_order_no, int orderAmount, String subject)
	{
		String submit_time = Tools.date2str(new Date(), "yyyyMMddHHmmss");
		//String subject = "buydiamond";		
		String notifyUrl = PookNet.gioneeNotify;

		String deal_price = String.valueOf(orderAmount);
		String deliver_type = "1"; //付款方式：1为立即付款，2为货到付款
		String total_fee = deal_price;

		JSONObject jsonReq = new JSONObject();

		StringBuilder signContent = new StringBuilder();
		signContent.append(apiKey);
		jsonReq.put("api_key", apiKey);

		signContent.append(deal_price);
		jsonReq.put("deal_price", deal_price);
		signContent.append(deliver_type);
		jsonReq.put("deliver_type", deliver_type);

		if (!StringUtils.isBlank(notifyUrl))
		{
			signContent.append(notifyUrl);
			jsonReq.put("notify_url", notifyUrl);
		}

		signContent.append(out_order_no);
		jsonReq.put("out_order_no", out_order_no);
		signContent.append(subject);
		jsonReq.put("subject", subject);
		signContent.append(submit_time);
		jsonReq.put("submit_time", submit_time);
		signContent.append(total_fee);
		jsonReq.put("total_fee", total_fee);

		String sign = null;
		try
		{
			sign = RSASignature.sign(signContent.toString(), payPrivateKey, CharEncoding.UTF_8);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ReturnValue(false, "订单签名失败:" + e.toString());
		}
		jsonReq.put("sign", sign);

		// player_id不参与签名
		jsonReq.put("player_id", String.valueOf(playerId));

		LogBAC.logout("chargecenter/009", "jsonReq=" + jsonReq.toString());

		NetClient netClient = new NetClient();
		netClient.setAddress(url);
		netClient.setContentType("application/x-www-form-urlencoded");

		netClient.ignoreSSL();
		netClient.setSendBytes(jsonReq.toString().getBytes());
		ReturnValue rv = netClient.send();
		if (rv.success)
		{
			try
			{
				String result = new String(rv.binaryData, "UTF-8");

				/*{"status":"200010000","description":"成功创建订单","out_order_no":"201311290000005",
					"api_key":"DDFDAEC3DBF544DD99EB9F508B429905","submit_time":"20131210154013
					","order_no":"52a690d10cf223aa543968e0"}
				*/
				LogBAC.logout("chargecenter/009", "result=" + result);
				JSONObject resultJson = new JSONObject(result);
				String status = resultJson.optString("status");
				submit_time = resultJson.optString("submit_time");
				String description = resultJson.optString("description");
				String order_no = resultJson.optString("order_no");
				if (status.equals("200010000"))
				{
					StringBuffer sb = new StringBuffer();
					sb.append(order_no);
					sb.append(",");
					sb.append(submit_time);
					return new ReturnValue(true, sb.toString());
				}
				else
				{
					LogBAC.logout("chargecenter/009", "result=" + result);
					return new ReturnValue(false, "订单创建失败:状态码" + status + "(" + description + ")");
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return new ReturnValue(false, "订单创建失败:" + ex.toString());
			}
		}
		else
		{
			return new ReturnValue(false, "订单创建失败:" + rv.info);
		}
	}

	static class RSASignature
	{

		private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

		/**
		 * RSA签名
		 * 
		 * @param content
		 *            待签名数据
		 * @param privateKey
		 *            商户私钥
		 * @param encode
		 *            字符集编码
		 * @return 签名值
		 * @throws IOException
		 * @throws NoSuchAlgorithmException
		 * @throws InvalidKeySpecException
		 * @throws SignatureException
		 * @throws InvalidKeyException
		 */
		public static String sign(String content, String privateKey, String encode) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException
		{
			String charset = CharEncoding.UTF_8;
			if (!StringUtils.isBlank(encode))
			{
				charset = encode;
			}
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
			signature.initSign(priKey);
			signature.update(content.getBytes(charset));
			byte[] signed = signature.sign();
			return Base64.encode(signed);
		}

		/**
		 * <pre>
		 * <p>函数功能说明:RSA验签名检查</p>
		 * <p>修改者名字:guocl</p>
		 * <p>修改日期:2012-11-30</p>
		 * <p>修改内容:抛异常</p>
		 * </pre>
		 * 
		 * @param content
		 *            待签名数据
		 * @param sign
		 *            签名值
		 * @param publicKey
		 *            支付宝公钥
		 * @param encode
		 *            字符集编码
		 * @return 布尔值
		 * @throws NoSuchAlgorithmException
		 * @throws IOException
		 * @throws InvalidKeySpecException
		 * @throws InvalidKeyException
		 * @throws SignatureException
		 */
		public static boolean doCheck(String content, String sign, String publicKey, String encode) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, SignatureException
		{
			String charset = CharEncoding.UTF_8;
			if (!StringUtils.isBlank(encode))
			{
				charset = encode;
			}
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decode(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(charset));

			boolean bverify = signature.verify(Base64.decode(sign));
			return bverify;

		}
	}

	static class CryptoUtility
	{

		private static final String MAC_NAME = "HmacSHA1";

		public static String macSig(String host, String port, String macKey, String timestamp, String nonce, String method, String uri)
		{
			// 1. build mac string
			// 2. hmac-sha1
			// 3. base64-encoded

			StringBuffer buffer = new StringBuffer();
			buffer.append(timestamp).append("\n");
			buffer.append(nonce).append("\n");
			buffer.append(method.toUpperCase()).append("\n");
			buffer.append(uri).append("\n");
			buffer.append(host.toLowerCase()).append("\n");
			buffer.append(port).append("\n");
			buffer.append("\n");
			String text = buffer.toString();

			byte[] ciphertext = null;
			try
			{
				ciphertext = hmacSHA1Encrypt(macKey, text);
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				return null;
			}

			String sigString = Base64Amigo.encodeToString(ciphertext, Base64Amigo.DEFAULT);
			return sigString;
		}

		public static byte[] hmacSHA1Encrypt(String encryptKey, String encryptText) throws InvalidKeyException, NoSuchAlgorithmException
		{
			Mac mac = Mac.getInstance(MAC_NAME);
			mac.init(new SecretKeySpec(StringUtil.getBytes(encryptKey), MAC_NAME));
			return mac.doFinal(StringUtil.getBytes(encryptText));
		}

	}

	static class StringUtil
	{
		public static final String UTF8 = "UTF-8";
		private static final byte[] BYTEARRAY = new byte[0];

		public static boolean isNullOrEmpty(String s)
		{
			if (s == null || s.isEmpty() || s.trim().isEmpty())
				return true;
			return false;
		}

		public static String randomStr()
		{
			return CamelUtility.uuidToString(UUID.randomUUID());
		}

		public static byte[] getBytes(String value)
		{
			return getBytes(value, UTF8);
		}

		public static byte[] getBytes(String value, String charset)
		{
			if (isNullOrEmpty(value))
				return BYTEARRAY;
			if (isNullOrEmpty(charset))
				charset = UTF8;
			try
			{
				return value.getBytes(charset);
			}
			catch (UnsupportedEncodingException e)
			{
				return BYTEARRAY;
			}
		}
	}

	static class CamelUtility
	{
		public static final int SizeOfUUID = 16;
		private static final int SizeOfLong = 8;
		private static final int BitsOfByte = 8;
		private static final int MBLShift = (SizeOfLong - 1) * BitsOfByte;

		private static final char[] HEX_CHAR_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

		public static String uuidToString(UUID uuid)
		{
			long[] ll = { uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() };
			StringBuilder str = new StringBuilder(SizeOfUUID * 2);
			for (int m = 0; m < ll.length; ++m)
			{
				for (int i = MBLShift; i > 0; i -= BitsOfByte)
					formatAsHex((byte) (ll[m] >>> i), str);
				formatAsHex((byte) (ll[m]), str);
			}
			return str.toString();
		}

		public static void formatAsHex(byte b, StringBuilder s)
		{
			s.append(HEX_CHAR_TABLE[(b >>> 4) & 0x0F]);
			s.append(HEX_CHAR_TABLE[b & 0x0F]);
		}

	}

}
