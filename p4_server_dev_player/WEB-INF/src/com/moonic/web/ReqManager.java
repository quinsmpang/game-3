package com.moonic.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import server.common.Tools;
import util.IPAddressUtil;

import com.ehc.common.ReturnValue;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.moonic.bac.ArtifactBAC;
import com.moonic.bac.CBBAC;
import com.moonic.bac.ChargeBAC;
import com.moonic.bac.CopymapBAC;
import com.moonic.bac.EquipOrdinaryBAC;
import com.moonic.bac.FacCopymapBAC;
import com.moonic.bac.FacFuncBAC;
import com.moonic.bac.FactionBAC;
import com.moonic.bac.FriendBAC;
import com.moonic.bac.FunctionBAC;
import com.moonic.bac.ItemBAC;
import com.moonic.bac.MailBAC;
import com.moonic.bac.MsgBAC;
import com.moonic.bac.PartnerBAC;
import com.moonic.bac.PlaFacBAC;
import com.moonic.bac.PlaFactionShopBAC;
import com.moonic.bac.PlaJJCRankingBAC;
import com.moonic.bac.PlaJJShopBAC;
import com.moonic.bac.PlaMineralsBAC;
import com.moonic.bac.PlaMysteryShopBAC;
import com.moonic.bac.PlaOrdinaryShopBAC;
import com.moonic.bac.PlaRoleBAC;
import com.moonic.bac.PlaSpShopBAC;
import com.moonic.bac.PlaSummonBAC;
import com.moonic.bac.PlaSupplyBAC;
import com.moonic.bac.PlaTeamBAC;
import com.moonic.bac.PlaTowerBAC;
import com.moonic.bac.PlaTowerShopBAC;
import com.moonic.bac.PlaTrialExpBAC;
import com.moonic.bac.PlaTrialMoneyBAC;
import com.moonic.bac.PlaTrialPartnerBAC;
import com.moonic.bac.PlaWelfareBAC;
import com.moonic.bac.PlatformGiftCodeBAC;
import com.moonic.bac.PlayerBAC;
import com.moonic.bac.ServerFacBAC;
import com.moonic.bac.UserBAC;
import com.moonic.bac.WorldBossBAC;
import com.moonic.mode.User;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.Player;
import com.moonic.socket.SocketServer;
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
			//---------------游戏功能----------------
			String sessionid = CookieUtil.get(context, "dev_sessionid");
			
			User user = UserBAC.session_usermap.get(sessionid);
			if(user == null)
			{
				try {
					user = UserBAC.getInstance().createUser(sessionid);
				} catch (Exception e) {
					//System.out.println("创建USER对象发生异常("+sessionid+")("+ip+") "+e.toString());
					return new ReturnValue(false, e.toString());
				}
			}
			
			Player pla = SocketServer.getInstance().session_plamap.get(sessionid);
			if(pla == null){
				return new ReturnValue(false, "尚未登录角色");
			}
			
			pla.ip = ip;
			pla.channel = user.channel;
			
			int uid = pla.uid;
			int pid = pla.pid;
			ReturnValue funcRv = FunctionBAC.getInstance().checkFuncOpen(pla.openfunc, request);
			if(!funcRv.success){
				return funcRv;
			}
			ReturnValue facfuncRv = FacFuncBAC.getInstance().checkFuncOpen(pid, request);
			if(!facfuncRv.success){
				return facfuncRv;
			}
			if(check(request, "player_logout")) {
				ReturnValue rv = PlayerBAC.getInstance().logout(pid, "WEB主动注销");
				if(rv.success) {
					((HttpServletResponse)context.getResponse()).sendRedirect(Conf.ms_url+Conf.web_dir+"dev_index.jsp");
				}
				return rv;
			} else 
			if(check(request, "player_upd_olstate")){
				return PlayerBAC.getInstance().updateOnlineState(pid, (byte)0);
			} else 
			if(check(request, "player_get_systime")){
				return new ReturnValue(true, String.valueOf(System.currentTimeMillis()));
			} else
			if(check(request, "player_getdata")){
				return PlayerBAC.getInstance().getAllData(pid, Tools.str2int(request.getParameter("player_getdata1")));
			} else 
			if(check(request, "debug_openallfunc")){
				return FunctionBAC.getInstance().debugOpenAllFunc(pla.pid, pla.openfunc, null);
			} else 
			if(check(request, "debug_pla_change_exp")){
				return PlayerBAC.getInstance().debugAddExp(pid, Tools.str2long(request.getParameter("debug_pla_change_exp1")));
			} else
			if(check(request, "debug_pla_change_money")){
				return PlayerBAC.getInstance().debugChangeValue(pid, "money", Tools.str2long(request.getParameter("debug_pla_change_money1")), 0, 1000000000, "铜钱");
			} else
			if(check(request, "debug_pla_change_coin")){
				return PlayerBAC.getInstance().debugChangeValue(pid, "coin", Tools.str2long(request.getParameter("debug_pla_change_coin1")), 0, 10000000, "金锭");
			} else
			if(check(request, "debug_pla_charge")){
				int rmbam = Tools.str2int(request.getParameter("debug_pla_charge1"));
				String centerOrder = request.getParameter("debug_pla_charge2");
				return ChargeBAC.getInstance().recharge(pid, (byte)1, rmbam, (byte)1, "调试充值", ChargeBAC.FROM_CONSOLE, "000", (byte)0, centerOrder);
			} else 
			if(check(request, "debug_pla_buytq")){
				String centerOrder = request.getParameter("debug_pla_buytq1");
				return ChargeBAC.getInstance().buyTQ(pid, Tools.str2byte(request.getParameter("debug_pla_buytq1")), (byte)1, "调试充值", ChargeBAC.FROM_CONSOLE, "000", centerOrder);
			} else 
			if(check(request, "debug_pla_resetdaydata")){
				return PlayerBAC.getInstance().checkAndResetDayData(pid, true);
			} else 
			if(check(request, "debug_pla_shortcut_grow")){
				int num = Tools.str2int(request.getParameter("debug_pla_shortcut_grow1"));
				return PlayerBAC.getInstance().debugShortcutGrow(pla.pid, pla.openfunc, num);
			} else
			if(check(request, "debug_closefunc")){
				return FunctionBAC.getInstance().debugCloseFunc(pla.pid, pla.openfunc, Tools.str2int(request.getParameter("debug_closefunc1")));
			} else
			if(check(request, "msg_str_send")){
				byte channel = Tools.str2byte(request.getParameter("msg_str_send1"));
				int friendid = Tools.str2int(request.getParameter("msg_str_send2"));
				String content = request.getParameter("msg_str_send3");
				return MsgBAC.getInstance().sendchannelMsg(pla, channel, friendid, 1, content, (byte)0, 0, null, GameServlet.ACT_MESSAGE_STR_SEND);
			} else 
			if(check(request, "msg_voice_send")){
				byte channel = Tools.str2byte(request.getParameter("msg_voice_send1"));
				int friendid = Tools.str2int(request.getParameter("msg_voice_send2"));
				return MsgBAC.getInstance().sendchannelMsg(pla, channel, friendid, 2, null, (byte)1, 4, new byte[]{1,0,0,0}, GameServlet.ACT_MESSAGE_VOICE_SEND);
			} else 
			if(check(request, "msg_set_rec_gamepush")){
				boolean open = Tools.str2boolean(request.getParameter("msg_set_rec_gamepush1"));
				return MsgBAC.getInstance().setReceiveGamePush(pla, open);
			} else 
			if(check(request, "item_discard")) {
				int itemid = Tools.str2int(request.getParameter("item_discard1"));
				return ItemBAC.getInstance().discardItem(pid, itemid);
			} else
			if(check(request, "item_opengift")){
				int itemid = Tools.str2int(request.getParameter("item_opengift1"));
				int amount = Tools.str2int(request.getParameter("item_opengift2"));
				return ItemBAC.getInstance().openGift(pid, itemid, amount);
			} else 
			if(check(request, "item_move")) {
				int itemid = Tools.str2int(request.getParameter("item_move1"));
				byte srczone = Tools.str2byte(request.getParameter("item_move2"));
				byte targetzone = Tools.str2byte(request.getParameter("item_move3"));
				return ItemBAC.getInstance().moveToZone(pid, itemid, srczone, targetzone);
			} else 
			if(check(request, "item_useconsume")) {
				int target = Tools.str2int(request.getParameter("item_useconsume1"));
				int itemid = Tools.str2int(request.getParameter("item_useconsume2"));
				int useamount = Tools.str2int(request.getParameter("item_useconsume3"));
				return ItemBAC.getInstance().useConsumeProp(pid, target, itemid, useamount);
			} else 
			if(check(request, "item_sellmoneyitem")){
				return ItemBAC.getInstance().sellMoneyItem(pid);
			} else 
			if(check(request, "item_uselottery")){
				int itemid = Tools.str2int(request.getParameter("item_uselottery1"));
				int amount = Tools.str2int(request.getParameter("item_uselottery2"));
				return ItemBAC.getInstance().useLottery(pid, itemid, amount);
			} else 
			if(check(request, "item_comporb")){
				int num = Tools.str2int(request.getParameter("item_comporb1"));
				return ItemBAC.getInstance().compOrb(pid, num);
			} else 
			if(check(request, "item_compequip")){
				int num = Tools.str2int(request.getParameter("item_compequip1"));
				return ItemBAC.getInstance().compEquip(pid, num);
			} else 
			if(check(request, "item_sell")){
				int itemid = Tools.str2int(request.getParameter("item_sell1"));
				int sellamount = Tools.str2int(request.getParameter("item_sell2"));
				return ItemBAC.getInstance().sell(pid, itemid, sellamount);
			} else 
			if(check(request, "item_smelt")){
				int itemid = Tools.str2int(request.getParameter("item_smelt1"));
				int smeltamount = Tools.str2int(request.getParameter("item_smelt2"));
				return ItemBAC.getInstance().smelt(pid, itemid, smeltamount);
			} else 
			if(check(request, "item_buyexpitem")){
				int buynum = Tools.str2int(request.getParameter("item_buyexpitem1"));
				int amount = Tools.str2int(request.getParameter("item_buyexpitem2"));
				return ItemBAC.getInstance().buyExpItem(pid, buynum, amount);
			} else 
			if(check(request, "item_usechoosegift")){
				int itemid = Tools.str2int(request.getParameter("item_usechoosegift1"));
				int index = Tools.str2int(request.getParameter("item_usechoosegift2"));
				int amount = Tools.str2int(request.getParameter("item_usechoosegift3"));
				return ItemBAC.getInstance().useChooseGift(pid, itemid, index, amount);
			} else 
			if(check(request, "debug_additem")){
				int itemtype = Tools.str2int(request.getParameter("itemtype"));
				int itemnum = Tools.str2int(request.getParameter("itemnum"));
				int itemamount = Tools.str2int(request.getParameter("itemamount"));
				byte zone = Tools.str2byte(request.getParameter("itemzone"));
				String extend = request.getParameter("itemextend");
				return ItemBAC.getInstance().debugAddItem(pid, itemtype, itemnum, itemamount, zone, extend);
			} else  
			if(check(request, "debug_discarditem")){
				return ItemBAC.getInstance().debugDiscardItem(pid, Tools.str2int(request.getParameter("debug_discarditem1")));
			} else 
			if(check(request, "mail_send")){
				String receiveridStr = request.getParameter("mail_send1");
				String title = request.getParameter("mail_send2");
				String content = request.getParameter("mail_send3");
				return MailBAC.getInstance().sendMail(pid, receiveridStr, title, content);
			} else 
			if(check(request, "mail_getcontent")){
				int mailid = Tools.str2int(request.getParameter("mail_getcontent1"));
				return MailBAC.getInstance().getMailContent(pid, mailid);
			} else 
			if(check(request, "mail_extractadjunct")){
				int mailid = Tools.str2int(request.getParameter("mail_extractadjunct1"));
				return MailBAC.getInstance().extractAdjunct(pid, mailid);
			} else 
			if(check(request, "mail_del")){
				int mailid = Tools.str2int(request.getParameter("mail_del1"));
				return MailBAC.getInstance().delMail(pid, mailid);
			} else 
			if(check(request, "mail_shortcut_del")){
				return MailBAC.getInstance().shortcatDel(pid);
			} else 
			if(check(request, "mail_shortcut_extractadjunct")){
				return MailBAC.getInstance().shortcutExtractAdjunct(pid);
			} else 
			if(check(request, "faction_create")){
				return FactionBAC.getInstance().create(pid, request.getParameter("faction_create1"));
			} else 
			if(check(request, "faction_set_joincond")){
				return FactionBAC.getInstance().setJoinCond(pid, request.getParameter("faction_set_joincond1"));
			} else 
			if(check(request, "faction_join")){
				return FactionBAC.getInstance().join(pid, Tools.str2int(request.getParameter("faction_join1")));
			} else 
			if(check(request, "faction_updinfo")){
				return FactionBAC.getInstance().updInfo(pid, request.getParameter("faction_updinfo1"));
			} else 
			if(check(request, "faction_adjustposition")){
				return FactionBAC.getInstance().adjustPosition(pid, Tools.str2int(request.getParameter("faction_adjustposition1")), Tools.str2byte(request.getParameter("faction_adjustposition2")));
			} else 
			if(check(request, "faction_removemem")){
				return FactionBAC.getInstance().removeMember(pid, request.getParameter("faction_removemem1"));
			} else 
			if(check(request, "faction_exit")){
				return FactionBAC.getInstance().exitFaction(pid);
			} else 
			if(check(request, "faction_shanrang")){
				return FactionBAC.getInstance().shanrang(pid, Tools.str2int(request.getParameter("faction_shanrang1")));
			} else 
			if(check(request, "faction_getlist")){
				return ServerFacBAC.getInstance().getFactionList(pid, Tools.str2int(request.getParameter("faction_getlist1")));
			} else 
			if(check(request, "faction_getdata")){
				return FactionBAC.getInstance().getFactionData(pid, Tools.str2int(request.getParameter("faction_getdata1")));
			} else 
			if(check(request, "faction_getranking")){
				return ServerFacBAC.getInstance().getPlaRanking(pid);
			} else 
			if(check(request, "faction_getranking2")){
				return ServerFacBAC.getInstance().getRanking(pid, Tools.str2int(request.getParameter("faction_getranking21")));
			} else 
			if(check(request, "faction_search")){
				return ServerFacBAC.getInstance().searchFaction(pid, request.getParameter("faction_search1"));
			} else 
			if(check(request, "faction_impeach")){
				return FactionBAC.getInstance().impeach(pid);
			} else 
			if(check(request, "faction_uplevel")){
				return FactionBAC.getInstance().upLevel(pid);
			} else 
			if(check(request, "faction_uptechnology")){
				int num = Tools.str2int(request.getParameter("faction_uptechnology1"));
				return FactionBAC.getInstance().upTechnology(pid, num);
			} else 
			if(check(request, "faction_getwelfare")){
				return FactionBAC.getInstance().getWelfare(pid);
			} else 
			if(check(request, "faction_worship")){
				int worshippid = Tools.str2int(request.getParameter("faction_worship1"));
				int num = Tools.str2int(request.getParameter("faction_worship2"));
				return FactionBAC.getInstance().worship(pid, worshippid, num);
			} else 
			if(check(request, "faction_getbeworshipaward")){
				return FactionBAC.getInstance().getBeWorshipAward(pid);
			} else 
			if(check(request, "faction_revocationapply")){
				return FactionBAC.getInstance().revocationApply(pid, Tools.str2int(request.getParameter("faction_revocationapply1")));
			} else 
			if(check(request, "faction_processapply")){
				return FactionBAC.getInstance().processApply(pid, Tools.str2int(request.getParameter("faction_processapply1")), Tools.str2byte(request.getParameter("faction_processapply2")));
			} else 
			if(check(request, "faction_addmoney")){
				int addmoney = Tools.str2int(request.getParameter("faction_addmoney1"));
				return FactionBAC.getInstance().debugAddMoney(pid, addmoney);
			} else 
			if(check(request, "faction_addcon")){
				int addcon = Tools.str2int(request.getParameter("faction_addcon1"));
				return PlaFacBAC.getInstance().debugChangeValue(pid, "factioncon", addcon, 0, 1000000000, "帮派功勋");
			} else 
			if(check(request, "giftcode_get")){
				String code = request.getParameter("giftcode_get1");
				return PlatformGiftCodeBAC.getInstance().getPlatformGift(pid, uid, code, false);
			} else 
			if(check(request, "user_bindcard")){
				String card = request.getParameter("user_bindcard1");
				String realname = request.getParameter("user_bindcard2");
				return UserBAC.getInstance().bindCard(uid, card, realname, ip, pid);
			} else 
			if(check(request, "user_getmobilevaildnum")){
				String phone = request.getParameter("user_getmobilevaildnum1");
				String card = request.getParameter("user_getmobilevaildnum2");
				return UserBAC.getInstance().getMobileVaildNum(uid, phone, card, ip);
			} else 
			if(check(request, "user_bindmobile")){
				String phone = request.getParameter("user_bindmobile1");
				String validnum = request.getParameter("user_bindmobile2");
				String card = request.getParameter("user_bindmobile3");
				return UserBAC.getInstance().bindMobile(uid, phone, validnum, card, ip, pid);
			} else 
			if(check(request, "user_bindemail")){
				String email = request.getParameter("user_bindemail1");
				String card = request.getParameter("user_bindemail2");
				return UserBAC.getInstance().bindEmail(uid, email, card, ip, pid);
			} else 
			if(check(request, "user_modifypwd")){
				String oldPwd = request.getParameter("user_modifypwd1");
				String newPwd = request.getParameter("user_modifypwd2");
				return UserBAC.getInstance().modifyPwd(uid, oldPwd, newPwd, ip, pid);
			} else 
			if(check(request, "user_getsafetybindstate")){
				return UserBAC.getInstance().getSafetyBindState(uid,ip);
			} else 
			if(check(request, "user_update_step")){
				return UserBAC.getInstance().updateUserStep(uid, Tools.str2int(request.getParameter("user_update_step1")));
			} else 
			if(check(request, "equip_stre")){
				int itemid = Tools.str2int(request.getParameter("equip_stre1"));
				return EquipOrdinaryBAC.getInstance().stre(pid, itemid);
			} else 
			if(check(request, "equip_upstar")){
				int itemid = Tools.str2int(request.getParameter("equip_upstar1"));
				return EquipOrdinaryBAC.getInstance().upStar(pid, itemid);
			} else 
			if(check(request, "equip_dismantle")){
				int itemid = Tools.str2int(request.getParameter("equip_dismantle1"));
				return EquipOrdinaryBAC.getInstance().dismantle(pid, itemid);
			} else 
			if(check(request, "equip_smelt")){
				String itemidStr = request.getParameter("equip_smelt1");
				return EquipOrdinaryBAC.getInstance().smelt(pid, itemidStr);
			} else 
			if(check(request, "partner_puton_equip")){
				int partnerid = Tools.str2int(request.getParameter("partner_puton_equip1"));
				int partnerid2 = Tools.str2int(request.getParameter("partner_puton_equip2"));
				int itemid = Tools.str2int(request.getParameter("partner_puton_equip3"));
				return PartnerBAC.getInstance().putonEquip(pid, partnerid, partnerid2, itemid);
			} else 
			if(check(request, "partner_shotcutputon_equip")){
				int partnerid = Tools.str2int(request.getParameter("partner_shotcutputon_equip1"));
				return PartnerBAC.getInstance().shortcutPutonEquip(pid, partnerid);
			} else 
			if(check(request, "partner_putoff_equip")){
				int partnerid = Tools.str2int(request.getParameter("partner_putoff_equip1"));
				int pos = Tools.str2int(request.getParameter("partner_putoff_equip2"));
				return PartnerBAC.getInstance().putoffEquip(pid, partnerid, pos);
			} else 
			if(check(request, "partner_puton_orb")){
				int partnerid = Tools.str2int(request.getParameter("partner_puton_orb1"));
				int pos = Tools.str2int(request.getParameter("partner_puton_orb2"));
				return PartnerBAC.getInstance().putonOrb(pid, partnerid, pos);
			} else
			if(check(request, "partner_upphase")){
				int partnerid = Tools.str2int(request.getParameter("partner_upphase1"));
				return PartnerBAC.getInstance().upPhase(pid, partnerid);
			} else 
			if(check(request, "partner_upstar")){
				int partnerid = Tools.str2int(request.getParameter("partner_upstar1"));
				return PartnerBAC.getInstance().upStar(pid, partnerid);
			} else 
			if(check(request, "partner_awaken")){
				int partnerid = Tools.str2int(request.getParameter("partner_awaken1"));
				return PartnerBAC.getInstance().awaken(pid, partnerid);
			} else 
			if(check(request, "partner_upskilllv")){
				int partnerid = Tools.str2int(request.getParameter("partner_upskilllv1"));
				int pos = Tools.str2int(request.getParameter("partner_upskilllv2"));
				int upamount = Tools.str2int(request.getParameter("partner_upskilllv3"));
				return PartnerBAC.getInstance().upskilllv(pid, partnerid, pos, upamount);
			} else 
			if(check(request, "partner_shortcutstreequip")){
				int partnerid = Tools.str2int(request.getParameter("partner_shortcutstreequip1"));
				return PartnerBAC.getInstance().shortcutStreEquip(pid, partnerid);
			} else 
			if(check(request, "partner_exchange")){
				int num = Tools.str2int(request.getParameter("partner_exchange1"));
				return PartnerBAC.getInstance().exchange(pid, num);
			} else 
			if(check(request, "partner_shortcutputonorb")){
				int partnerid = Tools.str2int(request.getParameter("partner_shortcutputonorb1"));
				return PartnerBAC.getInstance().shortcutPutonOrb(pid, partnerid);
			} else 
			if(check(request, "debug_partner_add")){
				int num = Tools.str2int(request.getParameter("debug_partner_add1"));
				int phase = Tools.str2int(request.getParameter("debug_partner_add2"));
				int star = Tools.str2int(request.getParameter("debug_partner_add3"));
				return PartnerBAC.getInstance().debugAdd(pid, num, phase, star);
			} else 
			if(check(request, "debug_partner_del")){
				int num = Tools.str2int(request.getParameter("debug_partner_del1"));
				return PartnerBAC.getInstance().debugDelete(pid, num);
			} else 
			if(check(request, "debug_partner_addphase")){
				int partnerid = Tools.str2int(request.getParameter("debug_partner_addphase1"));
				int add = Tools.str2int(request.getParameter("debug_partner_addphase2"));
				return PartnerBAC.getInstance().debugAddPhase(pid, partnerid, add);
			} else 
			if(check(request, "debug_partner_addstar")){
				int partnerid = Tools.str2int(request.getParameter("debug_partner_addstar1"));
				int add = Tools.str2int(request.getParameter("debug_partner_addstar2"));
				return PartnerBAC.getInstance().debugAddStar(pid, partnerid, add);
			} else 
			if(check(request, "debug_partner_addexp")){
				int partnerid = Tools.str2int(request.getParameter("debug_partner_addexp1"));
				int add = Tools.str2int(request.getParameter("debug_partner_addexp2"));
				return PartnerBAC.getInstance().debugAddExp(pid, partnerid, add);
			} else 
			if(check(request, "debug_partner_reset")){
				int num = Tools.str2int(request.getParameter("debug_partner_reset1"));
				return PartnerBAC.getInstance().debugResetPartner(pid, num);
			} else 
			if(check(request, "role_recoverenergy")){
				return PlaRoleBAC.getInstance().recoverEnergy(pid);
			} else 
			if(check(request, "debug_role_addenergy")){
				int add = Tools.str2int(request.getParameter("debug_role_addenergy1"));
				return PlaRoleBAC.getInstance().debugChangeValue(pid, "energy", add, 0, 9999, "体力");
			} else
			if(check(request, "debug_role_addsp")){
				int add = Tools.str2int(request.getParameter("debug_role_addsp1"));
				return PlaRoleBAC.getInstance().debugChangeValue(pid, "soulpoint", add, 0, 9999, "体力");
			} else
			if(check(request, "copymap_enter")){
				int cmnum = Tools.str2int(request.getParameter("copymap_enter1"));
				String posStr = request.getParameter("copymap_enter2");
				return CopymapBAC.getInstance().enter(pid, cmnum, posStr);
			} else
			if(check(request, "copymap_end")){
				int cmnum = Tools.str2int(request.getParameter("copymap_end1"));
				String battleRecord = request.getParameter("copymap_end2");
				return CopymapBAC.getInstance().endChallenge(pid, cmnum, battleRecord);
			} else
			if(check(request, "copymap_buy")){
				int num = Tools.str2int(request.getParameter("copymap_buy1"));
				return CopymapBAC.getInstance().buy(pid, num);
			} else
			if(check(request, "copymap_sweep")){
				int num = Tools.str2int(request.getParameter("copymap_sweep1"));
				int times = Tools.str2int(request.getParameter("copymap_sweep2"));
				return CopymapBAC.getInstance().sweep(pid, num, times);
			} else
			if(check(request, "copymap_getaward")){
				int bigmap = Tools.str2int(request.getParameter("copymap_getaward1"));
				int awardnum = Tools.str2int(request.getParameter("copymap_getaward2"));
				return CopymapBAC.getInstance().getStarAward(pid, bigmap, awardnum);
			} else
			if(check(request, "copymap_onekey")){
				int start = Tools.str2int(request.getParameter("copymap_onekey1"));
				int end = Tools.str2int(request.getParameter("copymap_onekey2"));
				return CopymapBAC.getInstance().debugOneKeyPass(pid, start, end);
			} else
			if(check(request, "copymap_clear")){
				return CopymapBAC.getInstance().debugClearRecord(pid);
			} else
			if(check(request, "ordinary_shop_get")){
				return PlaOrdinaryShopBAC.getInstance().getShopData(pid);
			} else
			if(check(request, "ordinary_shop_buy")){
				int index = Tools.str2int(request.getParameter("ordinary_shop_buy1"));
				return PlaOrdinaryShopBAC.getInstance().buy(pid, index);
			} else
			if(check(request, "ordinary_shop_refresh")){
				return PlaOrdinaryShopBAC.getInstance().refreshShop(pid);
			} else
			if(check(request, "mystery_shop_get")){
				return PlaMysteryShopBAC.getInstance().getShopData(pid);
			} else
			if(check(request, "mystery_shop_buy")){
				int index = Tools.str2int(request.getParameter("mystery_shop_buy1"));
				return PlaMysteryShopBAC.getInstance().buy(pid, index);
			} else
			if(check(request, "jj_shop_get")){
				return PlaJJShopBAC.getInstance().getShopData(pid);
			} else
			if(check(request, "jj_shop_buy")){
				int index = Tools.str2int(request.getParameter("jj_shop_buy1"));
				return PlaJJShopBAC.getInstance().buy(pid, index);
			} else
			if(check(request, "jj_shop_refresh")){
				return PlaJJShopBAC.getInstance().refreshShop(pid);
			} else
			if(check(request, "faction_shop_get")){
				return PlaFactionShopBAC.getInstance().getShopData(pid);
			} else
			if(check(request, "faction_shop_buy")){
				int index = Tools.str2int(request.getParameter("faction_shop_buy1"));
				return PlaFactionShopBAC.getInstance().buy(pid, index);
			} else
			if(check(request, "faction_shop_refresh")){
				return PlaFactionShopBAC.getInstance().refreshShop(pid);
			} else
			if(check(request, "sp_shop_get")){
				return PlaSpShopBAC.getInstance().getShopData(pid);
			} else
			if(check(request, "sp_shop_buy")){
				int index = Tools.str2int(request.getParameter("sp_shop_buy1"));
				return PlaSpShopBAC.getInstance().buy(pid, index);
			} else
			if(check(request, "sp_shop_refresh")){
				return PlaSpShopBAC.getInstance().refreshShop(pid);
			} else 
			if(check(request, "jjcranking_getinfo")){
				return PlaJJCRankingBAC.getInstance().getInfo(pid);
			} else 
			if(check(request, "jjcranking_getopps")){
				return PlaJJCRankingBAC.getInstance().getOpps(pid);
			} else 
			if(check(request, "jjcranking_battle")){
				int opppid = Tools.str2int(request.getParameter("jjcranking_battle1"));
				int pRanking = Tools.str2int(request.getParameter("jjcranking_battle2"));
				int oppranking = Tools.str2int(request.getParameter("jjcranking_battle3"));
				String posarrStr = request.getParameter("jjcranking_battle4");
				return PlaJJCRankingBAC.getInstance().toBattle(pid, opppid, pRanking, oppranking, posarrStr);
			} else 
			if(check(request, "jjcranking_getrankingdata")){
				return PlaJJCRankingBAC.getInstance().getRankingData(pid);
			} else 
			if(check(request, "jjcranking_refreshopps")){
				return PlaJJCRankingBAC.getInstance().refreshOpps(pid);
			} else 
			if(check(request, "jjcranking_clear_cd")){
				return PlaJJCRankingBAC.getInstance().clearCD(pid);
			} else 
			if(check(request, "jjcranking_reset_cha_am")){
				return PlaJJCRankingBAC.getInstance().resetChallengeAmount(pid);
			} else 
			if(check(request, "jjcranking_set_defform")){
				String posarrStr = request.getParameter("jjcranking_set_defform1");
				return PlaJJCRankingBAC.getInstance().setDefForm(pid, posarrStr);
			} else 
			if(check(request, "jjcranking_get_oppdefdata")){
				int oppid = Tools.str2int(request.getParameter("jjcranking_get_oppdefdata1"));
				return PlaJJCRankingBAC.getInstance().getOppDefFormData(pid, oppid);
			} else 
			if(check(request, "welfare_task")){
				int num = Tools.str2int(request.getParameter("welfare_task1"));
				return PlaWelfareBAC.getInstance().getTaskAward(pid, num);
			} else
			if(check(request, "welfare_achieve")){
				int num = Tools.str2int(request.getParameter("welfare_achieve1"));
				return PlaWelfareBAC.getInstance().getAchievementAward(pid, num);
			} else
			if(check(request, "welfare_checkin")){
				return PlaWelfareBAC.getInstance().checkin(pid);
			} else
			if(check(request, "welfare_checkin_aw")){
				int num = Tools.str2int(request.getParameter("welfare_checkin_aw1"));
				return PlaWelfareBAC.getInstance().getCheckinAward(pid, num);
			} else
			if(check(request, "welfare_task_aw_ok")){
				String numStr = request.getParameter("welfare_task_aw_ok1");
				return PlaWelfareBAC.getInstance().getTaskAwardOneKey(pid, numStr);
			} else
			if(check(request, "welfare_achieve_aw_ok")){
				String numStr = request.getParameter("welfare_achieve_aw_ok1");
				return PlaWelfareBAC.getInstance().getAchievementAwardOneKey(pid, numStr);
			} else
			if(check(request, "welfare_target_aw")){
				int num = Tools.str2int(request.getParameter("welfare_target_aw1"));
				return PlaWelfareBAC.getInstance().gerTargetAward(pid, num);
			} else
			if(check(request, "debug_welfare_finish_task")){
				byte type = Tools.str2byte(request.getParameter("debug_welfare_finish_task1"));
				int amount = Tools.str2int(request.getParameter("debug_welfare_finish_task2"));
				return PlaWelfareBAC.getInstance().debugFinishTask(pid, type, amount);
			} else
			if(check(request, "debug_welfare_add_checkin")){
				int days = Tools.str2int(request.getParameter("debug_welfare_add_checkin1"));
				return PlaWelfareBAC.getInstance().debugAddCheckin(pid, days);
			} else
			if(check(request, "debug_welfare_reset_achieve")){
				return PlaWelfareBAC.getInstance().debugResetAchieveAward(pid);
			} else
			if(check(request, "debug_welfare_reset_target")){
				return PlaWelfareBAC.getInstance().debugResetTargetAward(pid);
			} else
			if(check(request, "debug_welfare_reset_check")){
				return PlaWelfareBAC.getInstance().debugResetCheckIn(pid);
			} else
			if(check(request, "debug_welfare_finish_all")){
				return PlaWelfareBAC.getInstance().debugFinishAllTask(pid);
			} else
			if(check(request, "summon_ordinary")){
				byte multi = Tools.str2byte(request.getParameter("summon_ordinary1"));
				return PlaSummonBAC.getInstance().summonOrdinary(pid, multi);
			} else
			if(check(request, "summon_advanced")){
				byte multi = Tools.str2byte(request.getParameter("summon_advanced1"));
				return PlaSummonBAC.getInstance().summonAdvanced(pid, multi);
			} else
			if(check(request, "summon_mystery")){
				return PlaSummonBAC.getInstance().summonMystery(pid);
			} else
			if(check(request, "debug_reset_summon")){
				return PlaSummonBAC.getInstance().debugResetSummon(pid);
			} else
			if(check(request, "debug_add_summonprop")){
				int amount = Tools.str2int(request.getParameter("debug_add_summonprop1"));
				return PlaSummonBAC.getInstance().debugAddSummonprop(pid, amount);
			} else
			if(check(request, "cb_war")){
				int citynum = Tools.str2int(request.getParameter("cb_war1"));
				String teamidStr = request.getParameter("cb_war2");
				return CBBAC.getInstance().declareWar(pid, citynum, teamidStr);
			} else 
			if(check(request, "cb_addteam")){
				String posarrStr = request.getParameter("cb_addteam1");
				return CBBAC.getInstance().createTeamToPool(pid, posarrStr);
			} else 
			if(check(request, "cb_removeteam")){
				int teamid = Tools.str2int(request.getParameter("cb_removeteam1"));
				return CBBAC.getInstance().cancelTeamFromPool(pid, teamid);
			} else 
			if(check(request, "cb_joinwar")){
				int mapkey = Tools.str2int(request.getParameter("cb_joinwar1"));
				byte teamType = Tools.str2byte(request.getParameter("cb_joinwar2"));
				String posarrStr = request.getParameter("cb_joinwar3");
				return CBBAC.getInstance().joinWar(pid, mapkey, teamType, posarrStr);
			} else 
			if(check(request, "cb_relivepartner")){
				String partneridarrStr = request.getParameter("cb_relivepartner1");
				return CBBAC.getInstance().relivePartner(pid, partneridarrStr);
			} else 
			if(check(request, "cb_contendleader")){
				int citynum = Tools.str2int(request.getParameter("cb_contendleader1"));
				String posarrStr = request.getParameter("cb_contendleader2");
				return CBBAC.getInstance().contendLeader(pid, citynum, posarrStr);
			} else 
			if(check(request, "cb_setleaderdefform")){
				String posarrStr = request.getParameter("cb_setleaderdefform2");
				return CBBAC.getInstance().setLeaderDefForm(pid, posarrStr);
			} else 
			if(check(request, "cb_giveup_leader")){
				return CBBAC.getInstance().giveupLeader(pid);
			} else 
			if(check(request, "cb_getdata")){
				return CBBAC.getInstance().getData(pid);
			} else 
			if(check(request, "cb_getteampooldata")){
				return CBBAC.getInstance().getTeamPoolData(pid);
			} else 
			if(check(request, "cb_getpartnerstate")){
				return CBBAC.getInstance().getPartnerState(pid);
			} else 
			if(check(request, "cb_getcitybattledata")){
				int mapkey = Tools.str2int(request.getParameter("cb_getcitybattledata1"));
				return CBBAC.getInstance().getCityBattleData(pid, mapkey);
			} else 
			if(check(request, "cb_getleaderdata")){
				int citynum = Tools.str2int(request.getParameter("cb_getleaderdata1"));
				return CBBAC.getInstance().getLeaderData(pid, citynum);
			} else
			if(check(request, "cb_getkillranking")){
				int mapkey = Tools.str2int(request.getParameter("cb_getkillranking1"));
				return CBBAC.getInstance().getKillRanking(pid, mapkey);
			} else 
			if(check(request, "cb_getbattlerlist")){
				int mapkey = Tools.str2int(request.getParameter("cb_getbattlerlist1"));
				byte teamType = Tools.str2byte(request.getParameter("cb_getbattlerlist2"));
				return CBBAC.getInstance().getBattlerList(pid, mapkey, teamType);
			} else 
			if(check(request, "trial_money_start")){
				int num = Tools.str2int(request.getParameter("trial_money_start1"));
				String posStr = request.getParameter("trial_money_start2");
				return PlaTrialMoneyBAC.getInstance().startTrial(pid, num, posStr);
			} else
			if(check(request, "trial_money_end")){
				int num = Tools.str2int(request.getParameter("trial_money_end1"));
				String battleRecord = request.getParameter("trial_money_end2");
				return PlaTrialMoneyBAC.getInstance().endTrial(pid, num, battleRecord);
			} else
			if(check(request, "trial_exp_start")){
				int num = Tools.str2int(request.getParameter("trial_exp_start1"));
				String posStr = request.getParameter("trial_exp_start2");
				return PlaTrialExpBAC.getInstance().startTrial(pid, num, posStr);
			} else
			if(check(request, "trial_exp_end")){
				int num = Tools.str2int(request.getParameter("trial_exp_end1"));
				String battleRecord = request.getParameter("trial_exp_end2");
				return PlaTrialExpBAC.getInstance().endTrial(pid, num, battleRecord);
			} else
			if(check(request, "trial_partner_start")){
				int num = Tools.str2int(request.getParameter("trial_partner_start1"));
				String posStr = request.getParameter("trial_partner_start2");
				return PlaTrialPartnerBAC.getInstance().startTrial(pid, num, posStr);
			} else
			if(check(request, "trial_partner_end")){
				int num = Tools.str2int(request.getParameter("trial_partner_end1"));
				String battleRecord = request.getParameter("trial_partner_end2");
				return PlaTrialPartnerBAC.getInstance().endTrial(pid, num, battleRecord);
			} else
			if(check(request, "debug_trial_money")){
				return PlaTrialMoneyBAC.getInstance().debugResetTimes(pid);
			} else
			if(check(request, "debug_trial_exp")){
				return PlaTrialExpBAC.getInstance().debugResetTimes(pid);
			} else
			if(check(request, "debug_trial_partner")){
				return PlaTrialPartnerBAC.getInstance().debugResetTimes(pid);
			} else
			if(check(request, "supply_buy_money")){
				return PlaSupplyBAC.getInstance().buyMoney(pid);
			} else
			if(check(request, "supply_buy_energy")){
				return PlaSupplyBAC.getInstance().buyEnergy(pid);
			} else
			if(check(request, "supply_gettqcoin")){
				return PlaSupplyBAC.getInstance().getTqCoin(pid);
			} else 
			if(check(request, "faccopymap_into")){
				int posnum = Tools.str2int(request.getParameter("faccopymap_into1"));
				String posStr = request.getParameter("faccopymap_into2");
				return FacCopymapBAC.getInstance().into(pid, posnum, posStr);
			} else 
			if(check(request, "faccopymap_end")){
				String battleRecord = request.getParameter("faccopymap_end1");
				return FacCopymapBAC.getInstance().end(pid, battleRecord);
			} else 
			if(check(request, "faccopymap_resetmap")){
				int mapnum = Tools.str2int(request.getParameter("faccopymap_resetmap1"));
				return FacCopymapBAC.getInstance().resetMap(pid, mapnum);
			} else 
			if(check(request, "faccopymap_exit")){
				return FacCopymapBAC.getInstance().exit(pid);
			} else 
			if(check(request, "artifact_eatitem")){
				int num = Tools.str2int(request.getParameter("artifact_eatitem1"));
				String itemdata = request.getParameter("artifact_eatitem2");
				return ArtifactBAC.getInstance().eatItem(pid, num, itemdata);
			} else 
			if(check(request, "artifact_coininput")){
				int num = Tools.str2int(request.getParameter("artifact_coininput1"));
				int upamount = Tools.str2int(request.getParameter("artifact_coininput2"));
				return ArtifactBAC.getInstance().coinInput(pid, num, upamount);
			} else 
			if(check(request, "artifact_comp")){
				int num = Tools.str2int(request.getParameter("artifact_comp1"));
				return ArtifactBAC.getInstance().comp(pid, num);
			} else 
			if(check(request, "artifact_recoverrobtimes")){
				return PlaRoleBAC.getInstance().recoverArtifactRobTimes(pid);
			} else 
			if(check(request, "artifact_openrobprotect")){
				return ArtifactBAC.getInstance().openRobProtect(pid);
			} else 
			if(check(request, "friends_add")){
				String friends = request.getParameter("friends_add1");
				byte type = Tools.str2byte(request.getParameter("friends_add2"));
				return FriendBAC.getInstance().addFriends(pid, friends, type);
			} else
			if(check(request, "friends_delete")){
				String friends = request.getParameter("friends_delete1");
				byte type = Tools.str2byte(request.getParameter("friends_delete2"));
				return FriendBAC.getInstance().deleteFriends(pid, friends, type);
			} else
			if(check(request, "friends_search")){
				String condition = request.getParameter("friends_search1");
				return FriendBAC.getInstance().search(pid, condition);
			} else
			if(check(request, "friends_search_quick")){
				return FriendBAC.getInstance().quickSearch(pid);
			} else
			if(check(request, "debug_friends_add")){
				int amounts = Tools.str2int(request.getParameter("debug_friends_add1"));
				byte type = Tools.str2byte(request.getParameter("debug_friends_add2"));
				return FriendBAC.getInstance().debugAddFriend(pid, amounts, type);
			} else
			if(check(request, "debug_friends_delete")){
				byte type = Tools.str2byte(request.getParameter("debug_friends_delete1"));
				return FriendBAC.getInstance().debugDelFriend(pid, type);
			} else
			if(check(request, "friends_present")){
				int friendid = Tools.str2int(request.getParameter("friends_present1"));
				return FriendBAC.getInstance().presentEnergy(pid, friendid);
			} else
			if(check(request, "friends_present_ok")){
				String friends = request.getParameter("friends_present_ok1");
				return FriendBAC.getInstance().presentEnergyOneKey(pid, friends);
			} else
			if(check(request, "friends_geten")){
				int friendid = Tools.str2int(request.getParameter("friends_geten1"));
				return FriendBAC.getInstance().getEnergy(pid, friendid);
			} else
			if(check(request, "friends_geten_ok")){
				return FriendBAC.getInstance().getEnergyOneKey(pid);
			} else
			if(check(request, "debug_friends_reset_pre")){
				return FriendBAC.getInstance().debugResetPresent(pid);
			} else
			if(check(request, "wb_join")){
				return WorldBossBAC.getInstance().join(pid);
			} else
			if(check(request, "wb_tobattle")){
				String posStr = request.getParameter("wb_tobattle1");
				return WorldBossBAC.getInstance().toBattle(pid, posStr);
			} else
			if(check(request, "wb_getdate")){
				return WorldBossBAC.getInstance().getData(pid);
			} else
			if(check(request, "tower_enter")){
				int layer = Tools.str2int(request.getParameter("tower_enter1"));
				byte diff = Tools.str2byte(request.getParameter("tower_enter2"));
				String posStr = request.getParameter("tower_enter3");
				return PlaTowerBAC.getInstance().enter(pid, layer, diff, posStr);
			} else
			if(check(request, "tower_end")){
				String battleRecord = request.getParameter("tower_end1");
				return PlaTowerBAC.getInstance().end(pid, battleRecord);
			} else
			if(check(request, "tower_shop_get")){
				return PlaTowerShopBAC.getInstance().getShopData(pid);
			} else
			if(check(request, "tower_shop_buy")){
				int index = Tools.str2int(request.getParameter("tower_shop_buy1"));
				return PlaTowerShopBAC.getInstance().buy(pid, index);
			} else
			if(check(request, "tower_shop_refresh")){
				return PlaTowerShopBAC.getInstance().refreshShop(pid);
			} else
			if(check(request, "team_acti_create")){
				int type = Tools.str2int(request.getParameter("team_acti_create1"));
				return PlaTeamBAC.getInstance().createTeam(pid, type);
			} else
			if(check(request, "team_acti_join")){
				int num = Tools.str2int(request.getParameter("team_acti_join1"));
				return PlaTeamBAC.getInstance().joinTeam(pid, num);
			} else
			if(check(request, "team_acti_kick")){
				int num = Tools.str2int(request.getParameter("team_acti_kick1"));
				int memberid = Tools.str2int(request.getParameter("team_acti_kick2"));
				return PlaTeamBAC.getInstance().kickOut(pid, num, memberid);
			} else
			if(check(request, "team_acti_format")){
				int num = Tools.str2int(request.getParameter("team_acti_format1"));
				String posStr = request.getParameter("team_acti_format2");
				return PlaTeamBAC.getInstance().format(pid, num, posStr);
			} else
			if(check(request, "team_acti_beready")){
				int num = Tools.str2int(request.getParameter("team_acti_beready1"));
				return PlaTeamBAC.getInstance().beReady(pid, num);
			} else
			if(check(request, "team_acti_cancelready")){
				int num = Tools.str2int(request.getParameter("team_acti_cancelready1"));
				return PlaTeamBAC.getInstance().cancelReady(uid, num);
			} else
			if(check(request, "team_acti_battle")){
				int num = Tools.str2int(request.getParameter("team_acti_battle1"));
				return PlaTeamBAC.getInstance().battle(pid, num);
			} else
			if(check(request, "team_acti_getlist")){
				int type = Tools.str2int(request.getParameter("team_acti_getlist1"));
				return PlaTeamBAC.getInstance().getTeamList(pid, type);
			} else
			if(check(request, "team_acti_getdata")){
				return PlaTeamBAC.getInstance().getDate(pid);
			} else
			if(check(request, "team_acti_exit")){
				int num = Tools.str2int(request.getParameter("team_acti_battle1"));
				return PlaTeamBAC.getInstance().exitTeam(pid, num);
			} else
			if(check(request, "debug_team_acti_reset")){
				return PlaTeamBAC.getInstance().debugResetTimes(pid);
			} else
			if(check(request, "minerals_clockin")){
				int num = Tools.str2int(request.getParameter("minerals_clockin1"));
				return PlaMineralsBAC.getInstance().clockIn(pid, num);
			} else 
			if(check(request, "minerals_condent")){
				int opppid = Tools.str2int(request.getParameter("minerals_condent1"));
				int num = Tools.str2int(request.getParameter("minerals_condent2"));
				String posarrStr = request.getParameter("minerals_condent3");
				return PlaMineralsBAC.getInstance().contend(pid, opppid, num, posarrStr);
			} else 
			if(check(request, "minerals_setdef")){
				String posarrStr = request.getParameter("minerals_setdef1");
				return PlaMineralsBAC.getInstance().setDefForm(pid, posarrStr);
			} else 
			if(check(request, "minerals_getinfo")){
				return PlaMineralsBAC.getInstance().getInfo(pid);
			} else 
			if(check(request, "minerals_getposdata")){
				return PlaMineralsBAC.getInstance().getPosData();
			} else 
			if(check(request, "minerals_getownerdata")){
				int targetpid = Tools.str2int(request.getParameter("minerals_getownerdata1"));
				return PlaMineralsBAC.getInstance().getOwnerData(targetpid);
			} else 
			{ 
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
