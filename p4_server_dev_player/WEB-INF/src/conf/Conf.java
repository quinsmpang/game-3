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
	 * 游戏服务器ID
	 */
	public static int sid;
	/**
	 * HTTP地址
	 */
	public static String http_url;
	/**
	 * SOCKET地址
	 */
	public static String socket_url;
	/**
	 * SOCKET连接端口号
	 */
	public static int socket_port;
	/**
	 * 最大承载
	 */
	public static int max_player = 2000;
	
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
	 * 加入帮派间隔时间
	 */
	public static int joinfacspacetime = 1440;
	/**
	 * 调试模式
	 */
	public static boolean debug = false;
	/**
	 * 启用清理过期战斗录像计时器
	 */
	public static boolean useClearReplayTT = false;
	/**
	 * 初始VIP等级
	 */
	public static int initvip = 0;
	/**
	 * 世界等级
	 */
	public static int worldLevel = 1;

    public static int restart=1; //用来重启游戏服的假变量
    public static int currentAct; //当前响应的act
    public static int currentActTime; //当前响应时间
}
