package conf;

public class LogTbName {
	private static String logtableUsername = "";
	private static String logtableDatalink = "";
	
	/**
	 * 设置链接用户名
	 */
	public static void setUsername(String username) {
		if (username != null && !username.equals("")) {
			logtableUsername = username + ".";
		} else {
			logtableUsername = "";
		}
	}
	
	/**
	 * 设置数据链接
	 */
	public static void setDatalink(String datalink) {
		if (datalink != null && !datalink.equals("")) {
			logtableDatalink = "@"+datalink;
		} else {
			logtableDatalink = "";
		}
	}
	
	public static String TAB_HTTP_LOG() {return getTBName("tab_http_log");}
	public static String TAB_STS_HTTP_LOG() {return getTBName("tab_sts_http_log");}
	public static String TAB_USER_LOGIN_LOG() {return getTBName("tab_user_login_log");}
	public static String TAB_ACCESS_SERVER_EXC_LOG() {return getTBName("tab_access_server_exc_log");}
	public static String TAB_OPENGAME_LOG() {return getTBName("tab_opengame_log");}
	public static String tb_log() {return getTBName("tb_log");}
	
	/**
	 * 拼接日志表名
	 */
	private static String getTBName(String table){
		StringBuffer sb = new StringBuffer();
		sb.append(logtableUsername);		
		sb.append(table);
		//sb.append(logtableDatalink);
		return sb.toString();
	}
}
