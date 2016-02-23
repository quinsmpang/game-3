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

import org.json.JSONArray;

import server.common.Tools;
import server.config.LogBAC;
import server.config.ServerConfig;
import util.IPAddressUtil;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.bac.ActivateCodeBAC;
import com.moonic.bac.ChannelBAC;
import com.moonic.bac.ChargeOrderBAC;
import com.moonic.bac.ConfigBAC;
import com.moonic.bac.PlatformBAC;
import com.moonic.bac.RanNameBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.bac.SysNoticeBAC;
import com.moonic.bac.UserBAC;
import com.moonic.bac.VersionBAC;
import com.moonic.mode.User;
import com.moonic.platform.P;
import com.moonic.platform.P001;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPool;
import com.moonic.util.EncryptionUtil;
import com.moonic.util.MyTools;

import conf.LogTbName;

public class GameServlet extends HttpServlet {
	private static final long serialVersionUID = 4598035092703154800L;
	
	public static boolean useHTTPEncrypt=true; //对http数据进行加密的功能开关
	
	/**
	 * 检查程序版本
	 */
	public static final short ACT_VER_APK_CHECK = 101;	
	/**
	 * 检查资源版本
	 */
	public static final short ACT_VER_RES_CHECK = 102;
	
	/**
	 * 打开游戏
	 */
	public static final short ACT_PLATFROM_OPENGAME = 104;
	/**
	 * 获取系统公告
	 */
	public static final short ACT_GET_SYS_NOTICE = 122;
	/**
	 * 注册
	 */
	public static final short ACT_USER_REG = 141;
	/**
	 * 登录
	 */
	public static final short ACT_USER_LOGIN = 142;
	/**
	 * 快速游戏
	 */
	public static final short ACT_SHORTCUT_GAME = 143;
	/**
	 * 手机找回密码
	 */
	public static final short ACT_USER_MOBILE_FIND_PWD = 144;
	/**
	 * 邮箱找回密码
	 */
	public static final short ACT_USER_EMAIL_FIND_PWD = 145;
	/**
	 * 申请跳过维护检查
	 */
	public static final short ACT_JUMP_CHECK = 146;
	/**
	 * 打包工具获取渠道列表
	 */
	public static final short ACT_GET_CHANNLE_LIST = 174;
	/**
	 * 激活帐号
	 */
	public static final short ACT_USER_ACTIVATE = 201;
	/**
	 * 注销
	 */
	public static final short ACT_USER_LOGOUT = 202;
	/**
	 * 获取服务器列表
	 */
	public static final short ACT_SERVER_LIST = 203;
	/**
	 * 获取随机角色名
	 */
	public static final short ACT_PLAYER_RANNAME = 204;
	/**
	 * 获取波克卡面额
	 */
	public static final short ACT_GET_CARDVALUE= 208;
	/**
	 * 创建充值订单
	 */
	public static final short ACT_CREATE_ORDER = 301;
	
	/**
	 * service
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long t1= System.currentTimeMillis();
		/*Enumeration enu = request.getHeaderNames();
		System.out.println("http请求head-----------------");
		while(enu.hasMoreElements())
		{
			String key = (String)enu.nextElement();
			System.out.println(key+"="+request.getHeader(key));
		}*/
		//System.out.println(Tools.getCurrentDateTimeStr()+"--请求");
		String ip = IPAddressUtil.getIp(request);
		//System.out.println("ip="+ip);
		//过滤机器蜘蛛访问
		String agent = request.getHeader("User-Agent");
		if(agent!=null && (agent.indexOf("spider")!=-1
		|| agent.indexOf("roboo")!=-1			
		|| agent.toLowerCase().indexOf("bot")!=-1			
		))
		{
			return;
		}
		InputStream is = request.getInputStream();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[4096];
		int readLen = -1;
		while ((readLen = is.read(buff)) != -1) {
			baos.write(buff, 0, readLen);
		}
		buff = baos.toByteArray();
		
		if(useHTTPEncrypt)
		{			
			buff = EncryptionUtil.RC4(buff);  //数据解密
		}
		/*if(buff.length==0)
		{
			System.out.println(Tools.getCurrentDateTimeStr()+"--接收到来自"+ip+"的http请求,数据长度为0");
		}*/
		
		long t2= System.currentTimeMillis();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buff));
		DataOutputStream dos = new DataOutputStream(response.getOutputStream());		
		try {
			ReturnValue val = null;
			SqlString reqSqlStr = null;
			if(ConfigBAC.getBoolean("http_log"))
			{
				reqSqlStr = new SqlString();
				reqSqlStr.addDateTime("reqtime", Tools.getCurrentDateTimeStr());
				reqSqlStr.add("reqflow", buff.length);	
			}
			JSONArray processarr = new JSONArray();
			
			int currentAct=0;
			
			if (buff.length == 0) {
				val = new ReturnValue(false, "无效请求");
			} 
			/*
			else if(DBHelper.connectionAmount >= 100){
				val = new ReturnValue(false, "服务器繁忙");
			}
			*/
			else {				
				
				if(ConfigBAC.getBoolean("logout_http_ex"))
				{
					DataInputStream edis = new DataInputStream(new ByteArrayInputStream(buff));
					currentAct = edis.readShort();
					LogBAC.logout("http", request.getSession().getId()+"\tip="+ip+"\tact="+currentAct+"\tstart\tactive="+ServerConfig.getDataBase().getNumActive()+"\tidle="+ServerConfig.getDataBase().getNumIdle());
				}
				
				
				try {
					val = processingReq(request, response, dis, dos, reqSqlStr, processarr);					
				} catch (EOFException e) {
					DataInputStream edis = new DataInputStream(new ByteArrayInputStream(buff));
					int act = edis.readShort();
					System.out.println(e.toString()+"(act="+act+")");
					if(act <= 10000){//TODO 提示：动作号超出此范围时应及时调整
						e.printStackTrace();	
					}
					val = new ReturnValue(false, e.toString());
				} catch (Exception ex1) {
					ex1.printStackTrace();
					val = new ReturnValue(false, ex1.toString());
				}
				if(processarr.length() >= 2){
					short act = (short)processarr.getInt(0);
					User user = (User)processarr.get(1);
					user.removeReqing(act, val);
				}
			}
			dis.close();
			byte[] responseData = null;
			if(val.getDataType()==ReturnValue.TYPE_STR) 
			{				
				responseData = Tools.strNull(val.info).getBytes("UTF-8");
			} 
			else 
			{
				responseData = val.binaryData;
			}
			long t3= System.currentTimeMillis();
			
			//获得加密后的字节流
			byte[] outputBytes = getOutputBytes(val.success,responseData);
			if(outputBytes!=null)
			{				
				dos.write(outputBytes);
			}	
			
			long t4= System.currentTimeMillis();
						
			if(!"无效请求".equals(val.info))
			{
				if(ConfigBAC.getBoolean("logout_http_ex"))
				{
					LogBAC.logout("http", request.getSession().getId()+"\tip="+ip+"\tact="+currentAct+"\tend\tactive="+ServerConfig.getDataBase().getNumActive()+"\tidle="+ServerConfig.getDataBase().getNumIdle()+"\t"+(t3-t2)+"ms");
				}
				
				if(ConfigBAC.getBoolean("http_log"))
				{
					if(ConfigBAC.getInt("logout_http_threshold")<(t3-t2))
					{
						reqSqlStr.addDateTime("resptime", Tools.getCurrentDateTimeStr());
						reqSqlStr.add("respflow", responseData.length);
						reqSqlStr.add("respresult", val.success ? 1 : 0);
						reqSqlStr.add("respdatatype", val.getDataType());
						reqSqlStr.add("usedtime", t3-t2);
						reqSqlStr.add("uploadtime", t2-t1);
						reqSqlStr.add("downloadtime", t4 - t3);
						reqSqlStr.add("ip", ip);
						DBHelper.logInsert(LogTbName.TAB_HTTP_LOG(), reqSqlStr);
					}						
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			byte[] outputBytes = getOutputBytes(false,e.toString().getBytes("UTF-8"));
			if(outputBytes!=null)
			{
				dos.write(outputBytes);
			}
		}
		finally
		{
			dos.close();
		}
	}
	
	/**
	 * 获得返回的数据bytes
	 * @param success 是否成功的结果
	 * @param dataBytes 原始数据bytes
	 * @return
	 */
	private static byte[] getOutputBytes(boolean success,byte[] dataBytes)
	{
		try 
		{
			ByteArrayOutputStream outputBaos = new ByteArrayOutputStream();	
			DataOutputStream outputDos = new DataOutputStream(outputBaos);
			if(success)
			{
				outputDos.writeByte(1);				
			}
			else
			{
				outputDos.writeByte(0);
			}
			outputDos.write(dataBytes);
			outputDos.close();
			
			if(useHTTPEncrypt)  //使用加密机制
			{	
				dataBytes = EncryptionUtil.RC4(outputBaos.toByteArray());  //数据加密
				return dataBytes;			
			}
			else
			{
				return outputBaos.toByteArray();
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String[] jump_check_ip;
	
	public static short ACT_APPDATA = -101;
	
	/**
	 * 处理请求
	 */
	private ReturnValue processingReq(HttpServletRequest request, HttpServletResponse response, DataInputStream dis, DataOutputStream dos, SqlString reqSqlStr, JSONArray processarr) throws Exception{
		short act = dis.readShort();	
		processarr.add(act);
		
		long time = dis.readLong();
		String ip = IPAddressUtil.getIp(request);		
		
		//获取系统公告
		if(act == ACT_GET_SYS_NOTICE){
			long tagtime = dis.readLong();
			String channel = "001";
			try {
				channel = dis.readUTF();	
			} catch (Exception e) {
			}
			return SysNoticeBAC.getInstance().getSysNotice(channel, tagtime);
		}
		//申请跳过维护检查
		if(act == ACT_JUMP_CHECK){
			if(!MyTools.checkInStrArr(jump_check_ip, ip)){
				jump_check_ip = Tools.addToStrArr(jump_check_ip, ip);
			}
			return new ReturnValue(true);
		}
		//获取测试端信息
		if(act == ACT_APPDATA){
			JSONArray actarr = new JSONArray();
			actarr.add(ACT_USER_LOGIN);
			actarr.add(ACT_SERVER_LIST);
			actarr.add(302);
			actarr.add(304);
			actarr.add(1);
			actarr.add(1);
			return new ReturnValue(true, actarr.toString());
		}
		//判断跳过维护检查
		if(!ConfigBAC.getBoolean("openlogin")) {
			if(!MyTools.checkInStrArr(jump_check_ip,ip)) {
				return new ReturnValue(false, ConfigBAC.getString("closeloginnote")+"#1");
			}
		}
		if(reqSqlStr!=null)reqSqlStr.add("act", act);
		
		if(act == ACT_VER_APK_CHECK)
		{
			byte platform = dis.readByte(); //1安卓2ios
			String ver = dis.readUTF();
			String channel = dis.readUTF();	
					
			String packageName = dis.readUTF();	
			boolean isBigApk =  dis.readBoolean();
			boolean needPatch = dis.readBoolean();
			String imei = dis.readUTF();
			String mac = dis.readUTF();			
			
			/*if(Conf.ms_url.equals("http://xmlogin.pook.com/xianmo_user/") || Conf.ms_url.equals("http://192.168.1.29:82/xianmo_user/"))
			{
				needPatch = true;  //体验服强制使用补丁
			}*/
			return VersionBAC.getInstance().checkApkVer(platform,ver, channel,packageName,isBigApk,needPatch,imei,mac);
		} 
		/*else 
		if(act == ACT_GET_RES_CRC_FILELIST){
			byte phonePlatform = dis.readByte(); //手机平台类型1安卓2ios
			String channel = dis.readUTF();			
			return VersionBAC.getInstance().getResCRCFileList(phonePlatform,channel);
		} */
		else if(act == ACT_VER_RES_CHECK){
			String ver = dis.readUTF();
			byte platform = dis.readByte();
			return VersionBAC.getInstance().checkResVer(ver, platform);
		} else 
		if(act == ACT_PLATFROM_OPENGAME){
			String data = dis.readUTF();
			return PlatformBAC.getInstance().createtOpenGameLog(data);
		} else 
		if(act == ACT_USER_REG){
			String usernmae = dis.readUTF();
			String password = dis.readUTF();
			String channel = dis.readUTF();
			String logdata = dis.readUTF();
			return UserBAC.getInstance().register(usernmae, password, password, ip, channel, new JSONArray(logdata));//String userAgent = request.getHeader("User-Agent");
		} else 
		if(act == ACT_USER_LOGIN){
			String username = dis.readUTF();
			String password = dis.readUTF();
			String logdata = dis.readUTF();
			String channel = dis.readUTF();
			String extend = dis.readUTF();//扩展参数，jsonObject格式			
			return UserBAC.getInstance().login(username, password, ip, 0, new JSONArray(logdata), channel, extend);
		} else 
		if(act == ACT_SHORTCUT_GAME){
			String channel = dis.readUTF();
			String logdata = dis.readUTF();
			return UserBAC.getInstance().shortcutGame(ip, channel, new JSONArray(logdata));
		} else 
		if(act == ACT_USER_MOBILE_FIND_PWD){
			String username = dis.readUTF();
			String phone = dis.readUTF();
			return ((P001)P.getInstance("001")).mobileFindPwd(username, phone, ip);
		} else 
		if(act == ACT_USER_EMAIL_FIND_PWD){
			String username = dis.readUTF();
			String email = dis.readUTF();
			return ((P001)P.getInstance("001")).emailFindPwd(username, email, ip);
		} else 
		if(act == ACT_GET_CHANNLE_LIST) {
			JSONArray array = DBPool.getInst().pQueryS(ChannelBAC.tab_channel, null, "code").getJsonarr();
			return new ReturnValue(true, array.toString());
		}
		
		String sessionid = dis.readUTF();
		User user = UserBAC.getInstance().loadUser(sessionid);
		if(user == null){
			return new ReturnValue(false, "尚未登录帐号");
		}
		ReturnValue addReqRv = user.addReqing(act, time);
		//非历史结果
		if(addReqRv.parameter==null && !addReqRv.success){
			return addReqRv;
		}
		processarr.add(user);
		//历史结果
		if(addReqRv.parameter!=null){
			return addReqRv;
		}
		int uid = user.uid;
		if(reqSqlStr!=null)reqSqlStr.add("userid", uid);
		if(act == ACT_USER_ACTIVATE){
			String code = dis.readUTF();
			return ActivateCodeBAC.getInstance().activate(user.channel, user.username, code, ip);
		} else 
		if(act == ACT_USER_LOGOUT){
			return UserBAC.getInstance().logout(uid, "HTTP用户注销");
		} else 
		if(act == ACT_SERVER_LIST){
			return ServerBAC.getInstance().getServerList(uid, user.channel);
		} else 
		if(act == ACT_PLAYER_RANNAME){
			int serverid = dis.readInt();
			byte amount = dis.readByte();
			return RanNameBAC.getInstance().getRandomName(serverid, amount);
		} else 
		if(act == ACT_GET_CARDVALUE) {
			String cardNum = dis.readUTF();			
			return ChargeOrderBAC.getInstance().getCardValue(cardNum);
		} else 
		if(act == ACT_CREATE_ORDER)
		{
			String channel = dis.readUTF();
			String extend = dis.readUTF();
			LogBAC.logout("charge/"+channel, "收到申请新订单请求extend="+extend);
			return ChargeOrderBAC.getInstance().getChargeOrderno(channel,extend);
		} else 
		{
			return new ReturnValue(false, "无效请求");
		}
	}
}
