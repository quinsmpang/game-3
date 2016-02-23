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
	
	public static String TAB_GAME_LOG() {return getTBName("tab_game_log");}
	public static String TAB_STS_HTTP_LOG() {return getTBName("tab_sts_http_log");}
	public static String TAB_MSG_LOG() {return getTBName("tab_msg_log");}
	public static String TAB_SELL_LOG() {return getTBName("tab_sell_log");}
	public static String TAB_TXT_FILE() {return getTBName("tab_txt_file");}
	public static String TAB_PLA_ASSECT_DISCARD_LOG() {return getTBName("tab_pla_assect_discard_log");}
	public static String TAB_USER_LOGIN_LOG() {return getTBName("tab_user_login_log");}
	public static String TAB_PLAYER_LOGIN_LOG() {return getTBName("tab_player_login_log");}
	public static String TAB_PLAYER_CHANGELOG() {return getTBName("tab_player_changelog");}
	public static String TAB_SERVER_STATE_LOG() {return getTBName("tab_server_state_log");}
	public static String TAB_GIFT_CODE_EXC_LOG() {return getTBName("tab_gift_code_exc_log");}
	public static String TAB_ACCESS_SERVER_EXC_LOG() {return getTBName("tab_access_server_exc_log");}
	public static String TAB_PLAYER_OPERATE_LOG() {return getTBName("tab_player_operate_log");}
	public static String TAB_BATTLE_RECORD() {return getTBName("tab_battle_record");}
	public static String tb_log() {return getTBName("tb_log");};
	public static String tab_battle_replay(){return getTBName("tab_battle_replay");};
	
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
