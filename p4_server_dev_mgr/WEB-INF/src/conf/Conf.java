package conf;

import server.common.Tools;


/**
 * 系统配置
 * @author John
 */
public class Conf {
	public static boolean userUploadServer;
	public static int uploadServerPort;
	
	/**
	 * 资源服务器地址
	 */
	public static String res_url;
	/**
	 * 调试模式
	 */
	public static boolean debug = false;
	/**
	 * 输出SQL
	 */
	public static boolean out_sql = false;
	
	/**
	 * 记录|输出 调试数据日志
	 */
	public static boolean gdout;
	
	/**
	 * 服务器通讯识别头
	 */
	public static String stsKey;
	/**
	 * 日志文件存储根目录
	 */
	public static String logRoot;
	/**
	 * 发邮件地址
	 */
	public static String mailSender = "xianmo@pook.com";
	/**
	 * 发邮件帐号
	 */
	public static String mailUsername = "xianmo";
	/**
	 * 发邮件密码
	 */
	public static String mailPassword;
	static {
		try {
			mailPassword = new String(Tools.decodeBin("a^J>=U;`|}[J@9:U".getBytes("UTF-8")), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送服务器异常邮件
	 */
	public static boolean sendServerExcEmail = true;
}
