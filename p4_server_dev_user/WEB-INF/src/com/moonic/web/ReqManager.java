package com.moonic.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.json.JSONArray;

import server.common.Tools;
import util.IPAddressUtil;

import com.ehc.common.ReturnValue;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.moonic.bac.ActivateCodeBAC;
import com.moonic.bac.PlatformGiftBAC;
import com.moonic.bac.PlayerBAC;
import com.moonic.bac.RanNameBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.bac.SysNoticeBAC;
import com.moonic.bac.UserBAC;
import com.moonic.bac.VersionBAC;
import com.moonic.mode.User;
import com.moonic.platform.P;
import com.moonic.platform.P001;
import com.moonic.servlet.GameServlet;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;
import com.moonic.util.NetResult;
import com.moonic.util.NetSender;

import conf.Conf;

/**
 * 请求管理
 * @author John
 */
public class ReqManager {
	
	/**
	 * 处理web请求
	 * @param context
	 * @return 请求结果
	 */
	public static ReturnValue processingReq(PageContext context){
		/*try {
			InputStream is = context.getRequest().getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[4*1024];
			int len = 0;
			while((len = is.read(buffer)) != -1){
				baos.write(buffer, 0, len);
			}
			String str = new String(baos.toByteArray(), "gbk");
			System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		SmartUpload smartUpload = new SmartUpload();
		try {
			smartUpload.initialize(context);
			smartUpload.setEncode("UTF-8");
			smartUpload.upload();
			Request request = smartUpload.getRequest();
			
			String ip = IPAddressUtil.getIp((HttpServletRequest)context.getRequest());
			//---------------WEB----------------
			if(check(request, "web_acti_get_code")){
				String phone = request.getParameter("web_acti_get_code1");
				byte from = 1;
				String punlishuser = request.getParameter("web_acti_get_code2");
				return ActivateCodeBAC.getInstance().getActivateCode(phone, ip, from, punlishuser);
			} else 
			if(check(request, "getserverlist")){
				String channel = request.getParameter("getserverlist1");
				return ServerBAC.getInstance().webGetServerList(channel);
			} else 
			if(check(request, "getplainfo")){
				String username = request.getParameter("getplainfo1");
				String channel = request.getParameter("getplainfo2");
				int vsid = Tools.str2int(request.getParameter("getplainfo3"));
				return PlayerBAC.getInstance().webGetPlayerInfo(username, channel, vsid);
			} else 
			if(check(request, "getgiftlist")){
				return PlatformGiftBAC.getInstance().webGetGiftList();
			} else 
			if(check(request, "getgift")){
				int playerid = Tools.str2int(request.getParameter("getgift1"));
				int vsid = Tools.str2int(request.getParameter("getgift2"));
				String num = request.getParameter("getgift3");
				return PlatformGiftBAC.getInstance().webGetPlatformGift(playerid, vsid, num);
			} else 
			if(check(request, "getuserjudgmentdata")){
				String username = request.getParameter("getuserjudgmentdata1");
				String imei = request.getParameter("getuserjudgmentdata2");
				return UserBAC.getInstance().webGetJudgmentData(username, imei);
			} else 
			if(check(request, "userexist")){
				String platform = request.getParameter("userexist1");
				String username = request.getParameter("userexist2");
				return UserBAC.getInstance().webUserExist(platform, username);
			} else 
			if(check(request, "getuserserverlist")){
				String platform = request.getParameter("getuserserverlist1");
				String username = request.getParameter("getuserserverlist2");
				return ServerBAC.getInstance().webGetUserServerList(platform, username);
			}
			//---------------游戏服务----------------
			if(check(request, "ver_apk_check")){
				return VersionBAC.getInstance().checkApkVer(1,"0", request.getParameter("ver_apk_check1"),"",false,false,"","");
			} else  
			/* 
			if(check(request, "ver_res_check")){
				return VersionBAC.getInstance().getResCRCFileList(phonePlatform,channel,serverId); //暂时屏蔽
			} else
			*/ 
			if(check(request, "ver_version_res")){
				return VersionBAC.getInstance().checkResVer(request.getParameter("ver_version_res1"), Tools.str2byte(request.getParameter("ver_version_res2")));
			} else if(check(request, "getsysnotice")){
				String channel = request.getParameter("getsysnotice2");
				return SysNoticeBAC.getInstance().getSysNotice(channel, MyTools.getTimeLong(request.getParameter("getsysnotice1")));
			} else 
			if(check(request, "user_reg")){
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				String channel = request.getParameter("user_reg1");
				return UserBAC.getInstance().register(username, password, password, ip, channel, UserBAC.getInstance().webGetLogdata(ip));
			} else 
			if(check(request, "user_login")) {
				//System.out.println("newSessionId:"+newSessionId);
				String username = request.getParameter("username2");
				String password = request.getParameter("password2");
				String channel = request.getParameter("user_login1");
				String extend = request.getParameter("user_login2");
				ReturnValue rv = UserBAC.getInstance().login(username, password, ip, 1, UserBAC.getInstance().webGetLogdata(ip), channel, extend);
				if(rv.success) {
					CookieUtil.save(context, "dev_sessionid", new JSONArray(rv.info).optString(0)); //保存sessionId到客户端cookie
					context.getSession().setMaxInactiveInterval((int)(MyTools.long_day/1000));
					((HttpServletResponse)context.getResponse()).sendRedirect("dev_index.jsp");
				}
				return rv;
			} else 
			if(check(request, "user_shortcut_game")){
				String channel = request.getParameter("user_shortcut_game1");
				return UserBAC.getInstance().shortcutGame(ip, channel, UserBAC.getInstance().webGetLogdata(ip));
			} else 
			if(check(request, "user_mobilefindpwd")){
				String username = request.getParameter("user_mobilefindpwd1");
				String phone = request.getParameter("user_mobilefindpwd2");
				return ((P001)P.getInstance("001")).mobileFindPwd(username, phone, ip);
			} else 
			if(check(request, "user_emailfindpwd")){
				String username = request.getParameter("user_emailfindpwd1");
				String email = request.getParameter("user_emailfindpwd2");
				return ((P001)P.getInstance("001")).emailFindPwd(username, email, ip);
			} else 
			if(check(request, "jump_check")){
				String targetip = request.getParameter("jump_check1");
				if(!MyTools.checkInStrArr(GameServlet.jump_check_ip, targetip)){
					GameServlet.jump_check_ip = Tools.addToStrArr(GameServlet.jump_check_ip, targetip);
				}
				return new ReturnValue(true);
			} else 
			if(check(request, "user_getactivatecode")){
				return ActivateCodeBAC.getInstance().getActivateCode(""+System.currentTimeMillis(), ip, 3, "后台用户");
			}
			String sessionid = CookieUtil.get(context, "dev_sessionid");
			User user = UserBAC.getInstance().loadUser(sessionid);
			if(user == null){
				return new ReturnValue(false, "尚未登录帐号");
			}
			int uid = user.uid;
			if(check(request, "user_user_activate")){
				String code = request.getParameter("user_user_activate1");
				return ActivateCodeBAC.getInstance().activate(user.channel, user.username, code, ip);
			} else 
			if(check(request, "user_logout")){
				ReturnValue rv = UserBAC.getInstance().logout(uid, "WEB用户注销");
				if(rv.success){
					CookieUtil.save(context, "dev_sessionid", "");//清空保存的cookie
					((HttpServletResponse)context.getResponse()).sendRedirect("dev_index.jsp");
				}
				return rv;
			} else 
			if(check(request, "server_list")){
				return ServerBAC.getInstance().getServerList(uid, user.channel);
			} else 
			if(check(request, "player_ranname")){
				int vsid = Tools.str2int(request.getParameter("vsid"));
				DBPsRs channelServerRs = ServerBAC.getInstance().getChannelServer(user.channel, vsid);
				channelServerRs.next();
				return RanNameBAC.getInstance().getRandomName(channelServerRs.getInt("serverid"), Tools.str2byte(request.getParameter("ranamount")));
			} else 
			if(check(request, "player_create")){
				int vsid = Tools.str2int(request.getParameter("vsid"));
				DBPsRs channelServerRs = ServerBAC.getInstance().getChannelServer(user.channel, vsid);
				channelServerRs.next();
				NetSender sender = new NetSender((short)301);
				sender.dos.writeLong(System.currentTimeMillis());
				sender.dos.writeUTF(sessionid);
				sender.dos.writeInt(vsid);
				sender.dos.writeUTF(request.getParameter("playername"));
				sender.dos.writeByte(Tools.str2byte(request.getParameter("playernum")));
				sender.dos.writeInt(Tools.str2int(request.getParameter("partnernum")));
				sender.encryption = true;
				NetResult nr = ServerBAC.getInstance().sendReqToOne(sender, channelServerRs.getInt("serverid"), "game.do");
				return nr.rv;
			} else 
			if(check(request, "player_logininfo")){
				int vsid = Tools.str2int(request.getParameter("vsid"));
				DBPsRs channelServerRs = ServerBAC.getInstance().getChannelServer(user.channel, vsid);
				channelServerRs.next();
				NetSender sender = new NetSender((short)302);
				sender.dos.writeLong(System.currentTimeMillis());
				sender.dos.writeUTF(sessionid);
				sender.dos.writeInt(vsid);
				sender.encryption = true;
				NetResult nr = ServerBAC.getInstance().sendReqToOne(sender, channelServerRs.getInt("serverid"), "game.do");
				return nr.rv;
			} else 
			if(check(request, "player_login")){
				int vsid = Tools.str2int(request.getParameter("vsid"));
				DBPsRs channelServerRs = ServerBAC.getInstance().getChannelServer(user.channel, vsid);
				channelServerRs.next();
				NetSender sender = new NetSender((short)302);
				sender.dos.writeLong(System.currentTimeMillis());
				sender.dos.writeUTF(sessionid);
				sender.dos.writeInt(vsid);
				sender.encryption = true;
				NetResult nr = ServerBAC.getInstance().sendReqToOne(sender, channelServerRs.getInt("serverid"), "game.do");
				if(nr.rv.success){
					((HttpServletResponse)context.getResponse()).sendRedirect(nr.urlStr.substring(0, nr.urlStr.lastIndexOf('/')+1)+Conf.web_dir+"dev_login.jsp?sessionid="+sessionid);
				}
				return nr.rv;
			} else {
				return new ReturnValue(false, "非法请求");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	private static boolean check(Request request, String str){
		return request.getParameter(str)!=null;
	}
}
