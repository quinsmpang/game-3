package com.moonic.bac;

import java.sql.ResultSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.mgr.LockStor;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.PushData;
import com.moonic.socket.SocketServer;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import conf.Conf;
import server.common.Tools;

/**
 * 好友BAC
 * @author wkc
 */
public class FriendBAC extends PlaStorBAC {
	/**
	 * 所有(用于查询指定类型)
	 */
	public static final byte TYPE_ALL = -1;
	/**
	 * NONE(用于判断关系类型)
	 */
	public static final byte TYPE_NONE = 0;
	/**
	 * 好友
	 */
	public static final byte TYPE_FRIEND = 1;
	/**
	 * 黑名单
	 */
	public static final byte TYPE_BLACK = 2;
	
	private static final String[] types = {"无关系", "好友" , "黑名单"};
	
	public static final int MAX_FRIENDS = 50;//好友数量上限
	
	/**
	 * 构造
	 */
	public FriendBAC(){
		super("tab_friend", "playerid", null);
	}
	
	/**
	 * 加好友
	 * @param friends,好友IDJSONArray
	 */
	public ReturnValue addFriends(int playerid, String friends, byte type){
		DBHelper dbHelper = new DBHelper();
		try {
			if(type < 1 || type > 2) {
				BACException.throwInstance("参数错误 type="+type);
			}
			JSONArray fidarr = new JSONArray(friends);
			if(fidarr.size() == 0){
				BACException.throwInstance("好友ID为空");
			}
			if(fidarr.contains(playerid)){
				BACException.throwInstance("不能加自己为"+types[type]);
			}
			if(type == TYPE_FRIEND){
				DBPsRs friRs = query(playerid, "playerid="+playerid+" and type="+type);
				if(fidarr.size() + friRs.count() > MAX_FRIENDS){
					BACException.throwInstance("好友数量超过上限");
				}
			}
			dbHelper.openConnection();
			int[] pushIds = null;//由黑名单加为好友的不加推送
			JSONArray returnarr = new JSONArray();
			for(int i = 0; i < fidarr.size(); i++){
				int fid = fidarr.getInt(i);
				if(fid <= 0) {
					continue;
				}
				byte fritype = getFriendType(playerid, fid);
				if(fritype == TYPE_NONE) {
					pushIds = Tools.addToIntArr(pushIds, fid);
					SqlString sqlStr = new SqlString();
					sqlStr.add("playerid", playerid);
					sqlStr.add("friendid", fid);
					sqlStr.add("type", type);
					insert(dbHelper, playerid, sqlStr);
				} else {
					if(fritype == type){
						continue;
					}
					SqlString sqlStr = new SqlString();
					sqlStr.add("type", type);
					update(dbHelper, playerid, sqlStr, "playerid="+playerid+" and friendid="+fid);
				}
				returnarr.add(getFriendInfo(playerid, fid, type));
			}
			if(returnarr.size() == 0){
				BACException.throwInstance("已在"+types[type]+"中，无法添加");
			}
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < returnarr.length(); i++){
				JSONArray arr = returnarr.optJSONArray(i);
				sb.append(GameLog.formatNameID(arr.optString(1), arr.optInt(0))+"\r\n");
			}
			if(type == TYPE_FRIEND) {
				DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
				JSONArray pusharr = new JSONArray();
				pusharr.add(playerid);
				pusharr.add(plaRs.getInt("num"));
				pusharr.add(plaRs.getString("name"));
				pusharr.add(plaRs.getInt("lv"));
				pusharr.add(0);
				pusharr.add(1);
				pusharr.add(plaRs.getInt("onlinestate"));
				pusharr.add(plaRs.getTime("logintime"));
				PushData.getInstance().sendPlaToSome(SocketServer.ACT_FRIEND_ADD, pusharr.toString(), pushIds);
			}
			CustomActivityBAC.getInstance().updateProcess(dbHelper, playerid, 5, returnarr.size());
			
			GameLog.getInst(playerid, GameServlet.ACT_FRIEND_ADD)
			.addRemark("加"+types[type]+"："+sb)
			.save();
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 删好友
	 * @param type	类型，1 表示好友，2 表示黑名单
	 */
	public ReturnValue deleteFriends(int playerid, String friends, int type){
		DBHelper dbHelper = new DBHelper();
		try {
			JSONArray fidarr = new JSONArray(friends);
			if(fidarr.size() == 0){
				BACException.throwInstance("好友ID为空");
			}
			if(fidarr.contains(playerid)){
				BACException.throwInstance("无法删除自己");
			}
			DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(playerid);
			dbHelper.openConnection();
			StringBuffer sb = new StringBuffer();
			int[] pushIds = null;//推送id
			for(int i = 0; i < fidarr.size(); i++){
				int fid = fidarr.getInt(i);
				byte fritype = getFriendType(playerid, fid);
				if(fritype == TYPE_NONE){
					continue;
				}
				pushIds = Tools.addToIntArr(pushIds, fid);
				delete(dbHelper, playerid, "playerid="+playerid+" and friendid="+fid);
				sb.append(GameLog.formatNameID(PlayerBAC.getInstance().getStrValue(fid, "name"), fid)+"\r\n");
			}
			if(type == TYPE_FRIEND) {
				JSONArray pusharr = new JSONArray();
				pusharr.add(playerid);
				pusharr.add(plaRs.getInt("num"));
				pusharr.add(plaRs.getString("name"));
				PushData.getInstance().sendPlaToSome(SocketServer.ACT_FRIEND_DELETE, pusharr.toString(), pushIds);
				//加入删除好友记录
				DBPaRs presentRs = PlaRoleBAC.getInstance().getDataRs(playerid);
				JSONArray deleteArr = new JSONArray(presentRs.getString("deletefriend"));
				if(pushIds != null){
					for (int i = 0; i < pushIds.length; i++) {
						if(deleteArr.length() > 20){
							deleteArr.remove(0);
						}
						deleteArr.add(pushIds[i]);
					}
					SqlString sqlStr = new SqlString();
					sqlStr.add("deletefriend", deleteArr.toString());
					PlaRoleBAC.getInstance().update(dbHelper, playerid, sqlStr);
				}
			}
			
			GameLog.getInst(playerid, GameServlet.ACT_FRIEND_DELLTE)
			.addRemark("删除"+types[type]+"："+sb)
			.save();
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 搜索添加玩家
	 */
	public ReturnValue search(int playerid, String condition){
		DBHelper dbHelper = new DBHelper();
		try {
			MyTools.checkNoChar(condition);
			if(condition == null || condition.equals("")) {
				BACException.throwInstance("搜索条件不能为空");
			}
			boolean isNum = Tools.isNumber(condition);
			String idWhere  = "serverid="+Conf.sid+" and id="+condition;
			String nameWhere  = "serverid="+Conf.sid+" and name='"+condition+"'";
			ResultSet plaRs = null;
			dbHelper.openConnection();
			if(isNum){
				plaRs = dbHelper.query("tab_player", "id", idWhere);
				if(!plaRs.next()){
					plaRs = dbHelper.query("tab_player", "id", nameWhere);
					if(!plaRs.next()){
						BACException.throwInstance("不存在此玩家");
					}
				}
			} else {
				plaRs = dbHelper.query("tab_player", "id", nameWhere);
				if(!plaRs.next()){
					BACException.throwInstance("不存在此玩家");
				}
			}
			int fid = plaRs.getInt("id");
			if(fid == playerid) {
				BACException.throwInstance("不允许搜索自己");
			}
			byte fritype = getFriendType(playerid, fid);
			if(fritype == TYPE_FRIEND) {
				BACException.throwInstance("已在好友中，无法添加");
			}
			if(fritype == TYPE_NONE) {
				SqlString sqlStr = new SqlString();
				sqlStr.add("playerid", playerid);
				sqlStr.add("friendid", fid);
				sqlStr.add("type", 1);
				insert(dbHelper, playerid, sqlStr);
			} else {
				SqlString sqlStr = new SqlString();
				sqlStr.add("type", 1);
				update(dbHelper, playerid, sqlStr, "playerid="+playerid+" and friendid="+fid);
			}
			DBPaRs selfRs = PlayerBAC.getInstance().getDataRs(playerid);
			JSONArray pusharr = new JSONArray();
			pusharr.add(playerid);
			pusharr.add(selfRs.getInt("num"));
			pusharr.add(selfRs.getString("name"));
			pusharr.add(selfRs.getInt("lv"));
			pusharr.add(0);
			pusharr.add(1);
			pusharr.add(selfRs.getInt("onlinestate"));
			pusharr.add(selfRs.getTime("logintime"));
			long battlePower = PlaRoleBAC.getInstance().getLongValue(playerid, "totalbattlepower");
			pusharr.add(battlePower);//战力
			PushData.getInstance().sendPlaToOne(SocketServer.ACT_FRIEND_ADD, pusharr.toString(), fid);
			CustomActivityBAC.getInstance().updateProcess(dbHelper, playerid, 5);
			
			JSONArray playerarr = getFriendInfo(playerid, fid, TYPE_FRIEND);
			return new ReturnValue(true, playerarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 快速查找
	 */
	public ReturnValue quickSearch(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			int plv = PlayerBAC.getInstance().getIntValue(playerid, "lv");
			dbHelper.openConnection();
			ResultSet plaRs = dbHelper.query("tab_player", "id,num,name,lv,onlinestate,logintime", "serverid="+Conf.sid+" and id!="+playerid+" and lv>="+(plv-10)+" and lv<="+(plv+10), "lv desc", 10);
			JSONArray returnarr = new JSONArray();
			while(plaRs.next()){
				int pid = plaRs.getInt("id");
				if(getFriendType(playerid, pid) != TYPE_NONE){
					continue;
				}
				JSONArray player = new JSONArray();
				player.add(pid);
				player.add(plaRs.getInt("num"));
				player.add(plaRs.getString("name"));
				player.add(plaRs.getInt("lv"));
				player.add(1);
				player.add(0);
				player.add(plaRs.getInt("onlinestate"));
				player.add(MyTools.getTimeLong(plaRs.getTimestamp("logintime")));
				long battlePower = PlaRoleBAC.getInstance().getLongValue(pid, "totalbattlepower");
				player.add(battlePower);//战力
				returnarr.add(player);
			}
			if(returnarr.size() == 0){
				BACException.throwInstance("不存在符合条件的玩家");
			}
			
			return new ReturnValue(true, returnarr.toString());
		} catch(Exception e){
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally{
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 赠送好友体力
	 */
	public ReturnValue presentEnergy(int playerid, int friendid){
		DBHelper dbHelper = new DBHelper();
		try {
			DBPsRs friendRs = query(playerid, "playerid="+playerid+" and friendid="+friendid);
			if(!friendRs.next()){
				BACException.throwInstance("尚未添加此好友");
			}
			int fansType = FansBAC.getInstance().getFansType(playerid, friendid);
			if(fansType != TYPE_FRIEND){
				BACException.throwInstance("互为好友才能赠送");
			}
			synchronized (LockStor.getLock(LockStor.FRIEND_PRESENT, friendid)) {
				DBPaRs presentRs = PlaRoleBAC.getInstance().getDataRs(playerid);
				JSONArray presentArr = new JSONArray(presentRs.getString("present"));
				if(presentArr.contains(friendid)){
					BACException.throwInstance("今日已赠送");
				}
				if(presentArr.size() >= 10){
					BACException.throwInstance("每天最多赠送10次");
				}
				presentArr.add(friendid);
				DBPaRs bePresentRs = PlaRoleBAC.getInstance().getDataRs(friendid);
				JSONObject bePresentObj = new JSONObject(bePresentRs.getString("bepresent"));
				int total = getBePresentTimes(bePresentObj);
				if(total >= 30){
					BACException.throwInstance("最多被赠送30次");
				}
				bePresentObj.put(String.valueOf(playerid), bePresentObj.optInt(String.valueOf(playerid))+1);
				SqlString sqlStr = new SqlString();
				sqlStr.add("present", presentArr.toString());
				PlaRoleBAC.getInstance().update(dbHelper, playerid, sqlStr);
				
				SqlString beSqlStr = new SqlString();
				beSqlStr.add("bepresent", bePresentObj.toString());
				PlaRoleBAC.getInstance().update(dbHelper, friendid, beSqlStr);
				
				PushData.getInstance().sendPlaToOne(SocketServer.ACT_FRIEND_PRESENT, String.valueOf(playerid), friendid);
				
				String friendName = PlayerBAC.getInstance().getStrValue(friendid, "name");
				GameLog.getInst(playerid, GameServlet.ACT_FRIEND_PRESENT)
				.addRemark("赠送体力给好友"+GameLog.formatNameID(friendName, friendid))
				.save();
				
				return new ReturnValue(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 一键赠送好友体力
	 */
	public ReturnValue presentEnergyOneKey(int playerid, String friends){
		DBHelper dbHelper = new DBHelper();
		try {
			JSONArray friendarr = new JSONArray(friends);
			if(friendarr.size() == 0){
				BACException.throwInstance("好友ID为空");
			}
			if(friendarr.contains(playerid)){
				BACException.throwInstance("不能送给自己");
			}
			synchronized (LockStor.getLock(LockStor.FRIEND_PRESENT)) {
				DBPaRs presentRs = PlaRoleBAC.getInstance().getDataRs(playerid);
				JSONArray presentArr = new JSONArray(presentRs.getString("present"));
				JSONArray firarr = new JSONArray();
				for(int i = 0; i < friendarr.size(); i++){
					int friendid = friendarr.getInt(i);
					DBPsRs friendRs = query(playerid, "playerid="+playerid+" and friendid="+friendid);
					if(friendRs.next()){
						int fansType = FansBAC.getInstance().getFansType(playerid, friendid);
						if(fansType == TYPE_FRIEND){
							if(!presentArr.contains(friendid)){
								DBPaRs bePresentRs = PlaRoleBAC.getInstance().getDataRs(friendid);
								JSONObject bePresentObj = new JSONObject(bePresentRs.getString("bepresent"));
								int total = getBePresentTimes(bePresentObj);
								if(total < 30){
									firarr.add(friendid);
								}
							}
						}
					}
				}
				if(presentArr.size() + firarr.length() > 10){
					BACException.throwInstance("每天最多赠送10次");
				}
				if(firarr.size() == 0){
					BACException.throwInstance("没有可以赠送的好友");
				}
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FRIEND_PRESENT_ONEKEY);
				StringBuffer remarkSb = new StringBuffer();
				remarkSb.append("赠送体力给好友");
				for(int i = 0; i < firarr.size(); i++){
					int friendid = firarr.getInt(i);
					presentArr.add(friendid);
					DBPaRs bePresentRs = PlaRoleBAC.getInstance().getDataRs(friendid);
					JSONObject bePresentObj = new JSONObject(bePresentRs.getString("bepresent"));
					bePresentObj.put(String.valueOf(playerid), bePresentObj.optInt(String.valueOf(playerid))+1);
					SqlString beSqlStr = new SqlString();
					beSqlStr.add("bepresent", bePresentObj.toString());
					PlaRoleBAC.getInstance().update(dbHelper, friendid, beSqlStr);
					PushData.getInstance().sendPlaToOne(SocketServer.ACT_FRIEND_PRESENT, String.valueOf(playerid), friendid);
					String friendName = PlayerBAC.getInstance().getStrValue(friendid, "name");
					remarkSb.append(GameLog.formatNameID(friendName, friendid)+",");
				}
				SqlString sqlStr = new SqlString();
				sqlStr.add("present", presentArr.toString());
				PlaRoleBAC.getInstance().update(dbHelper, playerid, sqlStr);
				
				gl.addRemark(remarkSb);
				gl.save();
				return new ReturnValue(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 领取好友体力
	 */
	public ReturnValue getEnergy(int playerid, int friendid){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.FRIEND_PRESENT, friendid)) {
				DBPaRs bePresentRs = PlaRoleBAC.getInstance().getDataRs(playerid);
				JSONObject bePresentObj = new JSONObject(bePresentRs.getString("bepresent"));
				int times = bePresentObj.optInt(String.valueOf(friendid));
				if(times == 0){
					BACException.throwInstance("此好友尚无赠送");
				}
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FRIEND_GETENERGY);
				PlaRoleBAC.getInstance().addValue(dbHelper, playerid, "energy", times*2, gl, "领取好友赠送体力");
				bePresentObj.remove(String.valueOf(friendid));
				SqlString sqlStr = new SqlString();
				sqlStr.add("bepresent", bePresentObj.toString());
				PlaRoleBAC.getInstance().update(dbHelper, playerid, sqlStr);
				
				String friendName = PlayerBAC.getInstance().getStrValue(friendid, "name");
				gl.addRemark("领取好友体力"+GameLog.formatNameID(friendName, friendid));
				gl.save();
				return new ReturnValue(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 一键领取好友体力
	 */
	public ReturnValue getEnergyOneKey(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			synchronized (LockStor.getLock(LockStor.FRIEND_PRESENT)) {
				DBPaRs bePresentRs = PlaRoleBAC.getInstance().getDataRs(playerid);
				JSONObject bePresentObj = new JSONObject(bePresentRs.getString("bepresent"));
				int times = getBePresentTimes(bePresentObj);
				int addEnergy = times*2;
				if(addEnergy == 0){
					BACException.throwInstance("无体力可领取");
				}
				GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_FRIEND_GETENERGY_ONEKEY);
				PlaRoleBAC.getInstance().addValue(dbHelper, playerid, "energy", addEnergy, gl, "领取好友赠送体力");
				SqlString sqlStr = new SqlString();
				sqlStr.add("bepresent", "{}");
				PlaRoleBAC.getInstance().update(dbHelper, playerid, sqlStr);
				
				gl.addRemark("领取体力"+times+"次");
				gl.save();
				return new ReturnValue(true, String.valueOf(addEnergy));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 获取总计已被赠送次数
	 */
	public int getBePresentTimes(JSONObject bePresentObj){
		int total = 0;
		@SuppressWarnings("unchecked")
		Iterator<String> keys = bePresentObj.keys();
		while(keys.hasNext()){
			total += bePresentObj.optInt(keys.next());
		}
		return total;
	}
	
	/**
	 * 登陆获取好友及黑名单列表
	 */
	public JSONArray getLoginData(int playerid) throws Exception {
		DBPaRs plaRoleRs = PlaRoleBAC.getInstance().getDataRs(playerid);
		JSONArray fidArr = new JSONArray(plaRoleRs.getString("deletefriend"));//已删除好友
		JSONArray jsonarr = new JSONArray();
		//好友信息
		JSONArray friarr = new JSONArray();
		DBPsRs myfriRs = query(playerid, "playerid="+playerid);
		while(myfriRs.next()){
			int friendid = myfriRs.getInt("friendid");
			fidArr.add(friendid);
			JSONArray arr = getFriendInfo(playerid, friendid, myfriRs.getByte("type"));
			friarr.add(arr);
		}
		//获取对方添加了我，而我还没加他的角色数据
		JSONArray unaddArr = new JSONArray();//未加好友信息
		int count = 0;
		DBPsRs fansRs = FansBAC.getInstance().query(playerid, "friendid="+ playerid + " and type=" + TYPE_FRIEND);
		while(fansRs.next()){
			int pid = fansRs.getInt("playerid");
			if(!fidArr.contains(pid)) {
				DBPaRs plaRs = PlayerBAC.getInstance().getDataRs(pid);
				JSONArray plaarr = new JSONArray();
				plaarr.add(pid);//玩家ID
				plaarr.add(plaRs.getInt("num"));//角色编号
				plaarr.add(plaRs.getString("name"));//玩家名称
				plaarr.add(plaRs.getInt("lv"));//等级
				unaddArr.add(plaarr);
				count++;
			}
			if(count >= 20){
				break;
			}
		}
		jsonarr.add(friarr);
		jsonarr.add(unaddArr);
		return jsonarr;
	}
	
	/**
	 * 获取好友信息
	 */
	public JSONArray getFriendInfo(int playerid, int friendid, byte type) throws Exception {
		DBPaRs friRs = PlayerBAC.getInstance().getDataRs(friendid);
		JSONArray friarr = new JSONArray();
		friarr.add(friendid);//玩家ID
		friarr.add(friRs.getInt("num"));//角色编号
		friarr.add(friRs.getString("name"));//玩家名称
		friarr.add(friRs.getInt("lv"));//玩家等级
		friarr.add(type);//好友类型
		friarr.add(FansBAC.getInstance().getFansType(playerid, friendid));//对方对我的关注类型
		friarr.add(friRs.getInt("onlinestate"));//是否在线
		friarr.add(friRs.getTime("logintime"));//最后登录时间
		long battlePower = PlaRoleBAC.getInstance().getLongValue(friendid, "totalbattlepower");
		friarr.add(battlePower);//战力
		return friarr;
	}
	
	/**
	 * 检测后者是否是前者的好友
	 */
	public boolean isFriend(DBHelper dbHelper, int playerid, int friendid) throws Exception{
		return getFriendType(playerid, friendid) == TYPE_FRIEND;
	}
	
	/**
	 * 检查后再是否在前者的黑名单中 
	 */
	public boolean isBlack(DBHelper dbHelper, int playerid, int friendid) throws Exception{
		return getFriendType(playerid, friendid) == TYPE_BLACK;
	}
	
	/**
	 * 获取好友类型
	 */
	public byte getFriendType(int playerid, int targetid) throws Exception {
		DBPsRs fansRs = query(playerid, "playerid="+playerid+" and friendid="+targetid);
		byte type = TYPE_NONE;
		if(fansRs.next()){
			type = fansRs.getByte("type");
		}
		return type;
	}
	
	
	//--------------静态区--------------
	
	private static FriendBAC instance = new FriendBAC();
	
	/**
	 * 获取实例
	 */
	public static FriendBAC getInstance(){
		return instance;
	}
	
	//--------------调试区--------------
	
	/**
	 * 后台添加指定数量的好友或黑名单
	 * @param amounts	数量
	 * @param type		类型：1 好友；2 黑名单
	 */
	public ReturnValue debugAddFriend(int playerid, int amounts, byte type){
		DBHelper dbHelper = new DBHelper();
		try {
			if(amounts < 0 || amounts > 99 || type < 1 || type > 2) {
				BACException.throwInstance("参数错误：数量只能为[1~99]，类型只能为[1~2]");
			}
			dbHelper.openConnection();
			ResultSet plaRs = dbHelper.query("tab_player", "id", "serverid="+Conf.sid+" and id!="+playerid, "lv desc");
			int now_amount = 0;
			StringBuffer sb = new StringBuffer();
			while(plaRs.next()) {
				int friendid = plaRs.getInt("id");
				byte friType = getFriendType(playerid, friendid);
				if(friType == TYPE_NONE){
					SqlString sqlStr = new SqlString();
					sqlStr.add("playerid", playerid);
					sqlStr.add("friendid", friendid);
					sqlStr.add("type", type);
					sqlStr.add("times", 0);
					insert(dbHelper, playerid, sqlStr);
				} else {
					SqlString sqlSql = new SqlString();
					sqlSql.add("type", type);
					update(dbHelper, playerid, sqlSql, "playerid="+playerid+" and friendid="+friendid);
				}
				sb.append(PlayerBAC.getInstance().getStrValue(friendid, "name")+"("+friendid+")\r\n");
				now_amount++;
				if(now_amount >= amounts){
					break;
				}
			}
			return new ReturnValue(true, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 后台清除好友或者黑名单
	 * @param type	0表示删除全部（包括好友和黑名单）；1表示删除全部好友，2表示删除全部黑名单
	 */
	public ReturnValue debugDelFriend(int playerid, byte type){
		DBHelper dbHelper = new DBHelper();
		try {
			if(type < 0 || type > 2) {
				BACException.throwInstance("参数错误：类型只能为[0~2]");
			}
			dbHelper.openConnection();
			String where = "playerid="+playerid;
			while(type != TYPE_NONE){
				where += " and type="+type;
			}
			delete(dbHelper, playerid, where);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 调试情况好友赠送信息
	 */
	public ReturnValue debugResetPresent(int playerid){
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			SqlString sqlStr = new SqlString();
			sqlStr.add("present", "[]");
			sqlStr.add("bepresent", "{}");
			PlaRoleBAC.getInstance().update(dbHelper, playerid, sqlStr);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
}
