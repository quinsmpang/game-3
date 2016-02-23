package conf;

/**
 * 系统配置
 * @author John
 */
public class Conf {
	/**
	 * 验证服务器地址
	 */
	public static String ms_url;
	/**
	 * 网页地址文件夹
	 */
	public static String web_dir;
	/**
	 * 资源服务器地址
	 */
	public static String res_url;
	
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
	 * 测试版本跳转到的用户服url
	 */
	public static String testRedir;
}
