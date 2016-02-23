package com.moonic.bac;

import java.util.ArrayList;

import org.json.JSONArray;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.gamelog.GameLog;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.GamePushData;
import com.moonic.util.BACException;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTools;

import conf.Conf;

/**
 * 自定义活动
 * @author John
 */
public class CustomActivityBAC extends PlaStorBAC {
	public static String tab_custom_activity = "tab_custom_activity";
	
	//---------活动类型---------
	public static final byte TYPE_AWARD = 0;//奖励型活动
	public static final byte TYPE_CM_DOUBLE = 1;//普通副本掉率翻倍
	public static final byte TYPE_CM_ELITE_DOUBLE = 2;//精英副本掉率翻倍
	public static final byte TYPE_CM_OTHER_ITEM = 3;//普通副本追加其他奖励
	public static final byte TYPE_CM_ELITE_OTHER_ITEM = 4;//精英副本追加其他奖励
	public static final byte TYPE_TRIAL_PARTNER_OPEN = 5;//伙伴试练必开
	public static final byte TYPE_TRIAL_PARTNER_MUL = 6;//伙伴试练多倍掉落
	public static final byte TYPE_TRIAL_MONEY_MUL = 7;//铜钱试练多倍掉落
	public static final byte TYPE_TRIAL_EXP_MUL = 8;//经验试练多倍掉落
	public static final byte TYPE_TOWER_MORE_ITEM = 9;//轮回塔提升奖励数
	public static final byte TYPE_TOWER_OTHER_ITEM = 10;//轮回塔追加其他奖励
	public static final byte TYPE_WORLDBOSS_OTHER_ITEM = 11;//世界BOSS追加其他奖励
	public static final byte TYPE_SUMMON_PARTNER_COERCE = 12;//神秘召唤强制指定伙伴
	
	/**
	 * 构造
	 */
	public CustomActivityBAC() {
		super("tab_custom_activity_stor", "playerid", null);
	}
	
	/**
	 * 领取活动奖励
	 */
	public ReturnValue getAward(int playerid, int actiid, byte index){
		DBHelper dbHelper = new DBHelper();
		try {
			long currtime = System.currentTimeMillis();
			DBPaRs actiRs = DBPool.getInst().pQueryA(tab_custom_activity, "id="+actiid);
			if(!actiRs.exist() || actiRs.getTime("starttime") > currtime || (actiRs.getTime("hidetime") != 0 && actiRs.getTime("hidetime") < currtime)){
				BACException.throwInstance("不在活动时间内");
			}
			DBPsRs storRs = query(playerid, "playerid="+playerid+" and actiid="+actiid);
			if(!storRs.next()){
				BACException.throwInstance("未找到活动记录 actiid="+actiid);
			}
			JSONArray getdataarr = new JSONArray(storRs.getString("getdata"));
			if(index > getdataarr.length()-1){
				BACException.throwInstance("错误的奖励序号 index="+index);
			}
			if(getdataarr.optInt(index) == 1){
				BACException.throwInstance("已领取过此奖励");
			}
			if(storRs.getTime("expirationtime")!=0 && storRs.getTime("expirationtime")<=System.currentTimeMillis()){
				BACException.throwInstance("活动已过期");
			}
			String[] award = Tools.splitStrToStrArr2(actiRs.getString("award"), "##", "#")[index];//条件#奖励##条件#奖励//条件可多个，奖励可多个
			JSONArray condarr = new JSONArray(storRs.getString("process")).getJSONArray(index);//[[进度1，进度2]，[...]]
			int[][] cond = Tools.splitStrToIntArr2(award[0], "|", ",");
			for(int i = 0; i < cond.length; i++){
				int type = cond[i][0];
				boolean match = true;
				//TODO 条件判断
				if(type == 1){//等级
					match = PlayerBAC.getInstance().getIntValue(playerid, "lv") >= cond[i][1];
				} else 
				if(type == 11){//拥有指定伙伴
					match = PartnerBAC.getInstance().checkHave(playerid, cond[i][1]);
				} else 
				if(type == 12){//拥有指定星数的指定数量的伙伴
					match = PartnerBAC.getInstance().getAmountByStar(playerid, cond[i][1]) >= cond[i][1];
				} else 
				if(type == 13){//指定伙伴达到指定星级
					match = PartnerBAC.getInstance().getStar(playerid, cond[i][1]) >= cond[i][1];
				} else 
				if(type == 24){//占领城市数量
					match = CBBAC.getInstance().getHaveCityCount(playerid) >= cond[i][1];
				} else
				if(type == 27 || type == 29 || type == 31)
				{//判断条件
					match = condarr.optInt(i)==1;
				} else 
				{//判断数量
					match = condarr.optInt(i) >= cond[i][1];
				}
				if(!match){
					BACException.throwInstance("不满足领取条件");
				}
			}
			GameLog gl = GameLog.getInst(playerid, GameServlet.ACT_CONSTOMACTI_GET_AWARD);
			StringBuffer awardSb = new StringBuffer();
			if(!award[1].equals("-1")){
				awardSb.append(award[1]);
			}
			if(award.length >= 3 && !award[2].equals("-1")){
				//TODO 职业奖励，此游戏没有主角职业区分，所以没有职业奖励
			}
			if(award.length >= 4 && !award[3].equals("-1")){
				String[] paras = Tools.splitStr(award[3], ",");
				GamePushData gpd = GamePushData.getInstance(Tools.str2int(paras[0]));
				for(int i = 1; i < paras.length; i++){
					if(paras[i].equals("角色名")){
						gpd.add(PlayerBAC.getInstance().getStrValue(playerid, "name"));
					} else {
						gpd.add(paras[i]);
					}
				}
				gpd.sendToAllOL();
			}
			JSONArray awardarr = AwardBAC.getInstance().getAward(dbHelper, playerid, awardSb.toString(), ItemBAC.SHORTCUT_MAIL, 38, gl);
			getdataarr.put(index, 1);
			SqlString sqlStr = new SqlString();
			sqlStr.add("getdata", getdataarr.toString());
			update(dbHelper, playerid, sqlStr, "playerid="+playerid+" and actiid="+actiid);
			
			gl.addRemark(actiRs.getString("name") + "的第[" + (index + 1) + "]项奖励")
			.save();
			return new ReturnValue(true, awardarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 更新进度
	 */
	public void updateProcess(DBHelper dbHelper, int playerid, int type, int... para) throws Exception {
		DBPsRs storRs = query(playerid, "playerid="+playerid);
		long currtime = System.currentTimeMillis();
		while(storRs.next()){
			DBPaRs actiRs = DBPool.getInst().pQueryA(tab_custom_activity, "id="+storRs.getInt("actiid"));
			if(!actiRs.exist() || actiRs.getTime("starttime") > currtime || (actiRs.getTime("endtime") != 0 && actiRs.getTime("endtime") < currtime)){
				continue;
			}
			if(storRs.getTime("expirationtime")!=0 && storRs.getTime("expirationtime")<=System.currentTimeMillis()){
				continue;
			}
			JSONArray process = new JSONArray(storRs.getString("process"));
			String[][] award = Tools.splitStrToStrArr2(actiRs.getString("award"), "##", "#");
			boolean upd = false;
			for(int i = 0; award != null && i < award.length; i++){
				int[][] cond = Tools.splitStrToIntArr2(award[i][0], "|", ",");
				JSONArray condarr = process.optJSONArray(i);
				if(condarr == null){
					System.out.println("抽象活动异常：playerid="+playerid+" actiid="+storRs.getInt("actiid"));
					continue;
				}
				for(int j = 0; j < cond.length; j++){
					if(cond[j][0] == type){
						int the_process = condarr.optInt(j);
						if(type == 27 || type == 29 || type == 31){//通关指定副本||国战首席获得指定规模城池||帮派占领城市获得指定规模城池
							if(the_process == 1){
								continue;
							}
							if(para[0] != cond[j][1]){
								continue;
							}
							condarr.put(j, 1);
							upd = true;
						//TODO 进度更新
						} else
						if(type == 30) {//帮派获得占领指定次数及城数
							if(the_process >= cond[j][2]){
								continue;
							}
							if(para[0] < cond[j][1]){
								continue;
							}
							int addamount = 1;
							if(para.length > 1){
								addamount = para[1];
							}
							condarr.put(j, the_process+addamount);
							upd = true;
						} else
						{
							if(the_process >= cond[j][1]){
								continue;
							}
							int addamount = 1;
							if(para.length > 0){
								addamount = para[0];
							}
							condarr.put(j, Math.min(the_process+addamount, cond[j][1]));
							upd = true;
						}
					}
				}
			}
			if(upd){
				SqlString sqlStr = new SqlString();
				sqlStr.add("process", process.toString());
				update(dbHelper, playerid, sqlStr, "playerid="+playerid+" and actiid="+storRs.getInt("actiid"));
			}
		}
	}
	
	/**
	 * 补充活动
	 */
	public void supplement(DBHelper dbHelper, int playerid, String channel) throws Exception {
		DBPsRs storRs = query(playerid, "playerid="+playerid);
		ArrayList<Integer> actiidarr = new ArrayList<Integer>();
		while(storRs.next()){
			actiidarr.add(storRs.getInt("actiid"));
		}
		DBPsRs actiRs = DBPool.getInst().pQueryS(tab_custom_activity, "actitype=0 and showtime<="+MyTools.getTimeStr()+" and (hidetime=null or hidetime>"+MyTools.getTimeStr()+")");
		while(actiRs.next()){
			if(actiidarr.contains(actiRs.getInt("id"))){
				continue;
			}
			String serverStr = actiRs.getString("server");
			if(!serverStr.equals("0") && serverStr.indexOf("|"+Conf.sid+"|")==-1){
				continue;
			}
			String channelStr = actiRs.getString("channel");
			if(channelStr!=null && !channelStr.equals("0") && channelStr.indexOf("|"+channel+"|")==-1){
				continue;
			}
			if(actiRs.getTime("opentime") != 0){
				DBPaRs serverRs = DBPool.getInst().pQueryA(ServerBAC.tab_server, "id="+Conf.sid);
				if(serverRs.getTime("opentime") >= actiRs.getTime("opentime")){
					continue;
				}
			}
			String[][] award = Tools.splitStrToStrArr2(actiRs.getString("award"), "##", "#");
			JSONArray process = new JSONArray();
			JSONArray getdata = new JSONArray();
			for(int i = 0; award != null && i < award.length; i++){
				int[][] cond = Tools.splitStrToIntArr2(award[i][0], "|", ",");
				JSONArray condarr = new JSONArray();
				for(int j = 0; j < cond.length; j++){
					condarr.add(0);
				}
				process.add(condarr);
				getdata.add(0);
			}
			SqlString sqlStr = new SqlString();
			sqlStr.add("playerid", playerid);
			sqlStr.add("actiid", actiRs.getInt("id"));
			sqlStr.add("process", process.toString());
			sqlStr.add("getdata", getdata.toString());
			if(actiRs.getInt("expirationlen") > 0){
				sqlStr.addDateTime("expirationtime", MyTools.getTimeStr(System.currentTimeMillis()+actiRs.getInt("expirationlen")*MyTools.long_hour));
			}
			insert(dbHelper, playerid, sqlStr);
		}
	}
	
	/**
	 * 获取登录数据
	 */
	public JSONArray getLoginData(int playerid) throws Exception {
		DBPsRs storRs = query(playerid, "playerid="+playerid);
		JSONArray jsonarr = new JSONArray();
		while(storRs.next()){
			DBPaRs actiRs = DBPool.getInst().pQueryA(tab_custom_activity, "id="+storRs.getInt("actiid"));
			if(actiRs.exist()){
				if(storRs.getTime("expirationtime")!=0 && storRs.getTime("expirationtime")<=System.currentTimeMillis()){
					continue;
				}
				JSONArray process = new JSONArray(storRs.getString("process"));
				JSONArray getdata = new JSONArray(storRs.getString("getdata"));
				JSONArray arr = new JSONArray();
				arr.add(actiRs.getInt("id"));//活动ID
				arr.add(actiRs.getString("name"));//活动名称
				arr.add(actiRs.getTime("starttime"));//起始时间
				arr.add(actiRs.getTime("endtime"));//结束时间
				arr.add(actiRs.getString("note"));//活动说明
				arr.add(actiRs.getString("award"));//条件1#奖励1##条件2#奖励2
				arr.add(process);//[[条件1进度],[条件2进度]]
				arr.add(getdata);//[奖励1领取，奖励2领取]
				arr.add(actiRs.getString("imgurl"));//配图
				arr.add(actiRs.getTime("showtime"));//显示时间
				arr.add(actiRs.getTime("hidetime"));//隐藏时间
				arr.add(actiRs.getInt("layout"));//布局类型
				arr.add(storRs.getTime("expirationtime"));//过期时间
				arr.add(actiRs.getInt("actitype"));//活动类型
				jsonarr.add(arr);
			}
		}
		DBPsRs actiRs = DBPool.getInst().pQueryS(tab_custom_activity, "actitype!=0 and showtime<="+MyTools.getTimeStr()+" and (hidetime=null or hidetime>"+MyTools.getTimeStr()+")");
		while(actiRs.next()){
			String serverStr = actiRs.getString("server");
			if(!serverStr.equals("0") && serverStr.indexOf("|"+Conf.sid+"|")==-1){
				continue;
			}
			JSONArray arr = new JSONArray();
			arr.add(actiRs.getInt("id"));//活动ID
			arr.add(actiRs.getString("name"));//活动名称
			arr.add(actiRs.getTime("starttime"));//起始时间
			arr.add(actiRs.getTime("endtime"));//结束时间
			arr.add(actiRs.getString("note"));//活动说明
			arr.add(actiRs.getString("award"));//条件1#奖励1##条件2#奖励2
			arr.add(null);//[[条件1进度],[条件2进度]]
			arr.add(null);//[奖励1领取，奖励2领取]
			arr.add(actiRs.getString("imgurl"));//配图
			arr.add(actiRs.getTime("showtime"));//显示时间
			arr.add(actiRs.getTime("hidetime"));//隐藏时间
			arr.add(actiRs.getInt("layout"));//布局类型
			arr.add(0);//过期时间
			arr.add(actiRs.getInt("actitype"));//活动类型
			jsonarr.add(arr);
		}
		return jsonarr;
	}
	
	/**
	 * 获取功能活动参数
	 */
	public String getFuncActiPara(int actitype) throws Exception {
		DBPsRs actiRs = DBPool.getInst().pQueryS(tab_custom_activity, "actitype="+actitype+" and showtime<="+MyTools.getTimeStr()+" and (hidetime=null or hidetime>"+MyTools.getTimeStr()+")");
		while(actiRs.next()){
			String serverStr = actiRs.getString("server");
			if(!serverStr.equals("0") && serverStr.indexOf("|"+Conf.sid+"|")==-1){
				continue;
			}
			return actiRs.getString("award");
		}
		return null;
	}
	
	//----------------静态区------------------
	
	private static CustomActivityBAC instance = new CustomActivityBAC();

	public static CustomActivityBAC getInstance() {
		return instance;
	}
}
