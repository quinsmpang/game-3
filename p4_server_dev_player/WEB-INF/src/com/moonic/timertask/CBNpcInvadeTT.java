package com.moonic.timertask;

import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.moonic.bac.CBBAC;
import com.moonic.bac.ServerBAC;
import com.moonic.txtdata.CBDATA;
import com.moonic.util.DBHelper;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;
import com.moonic.util.MyTimerTask;
import com.moonic.util.MyTools;
import com.moonic.util.Out;

/**
 * 国战NPC入侵
 * @author John
 */
public class CBNpcInvadeTT extends MyTimerTask {
	private String invadetimeStr;
	
	/**
	 * 构造
	 */
	public CBNpcInvadeTT(String invadetimeStr){
		this.invadetimeStr = invadetimeStr;
	}
	
	/**
	 * 执行
	 */
	public void run2() {
		DBHelper dbHelper = new DBHelper();
		try {
			for(int i = 0; i < CBDATA.invadedeclare.length; i++){
				if(invadetimeStr.equals(CBDATA.invadedeclare[i][0])){//查找条目
					int waramount = Tools.str2int(CBDATA.invadedeclare[i][2]);
					for(int k = 0; k < waramount; k++){//需要入侵城池数
						int[] npcamount = Tools.splitStrToIntArr(CBDATA.invadescale[MyTools.getRandom(0, CBDATA.invadescale.length-1)][1], ",");//确定入侵部队规模
						JSONArray npcinfluencearr = new JSONArray();//可用入侵势力
						for(int m = 0; m < CBDATA.invadeinfluence.length; m++){
							npcinfluencearr.add(CBDATA.invadeinfluence[m][0]);
						}
						int citynum = 0;//入侵城市
						int npcinfluence = 0;//入侵势力
						while(npcinfluencearr.length() > 0){//有可用入侵势力
							npcinfluence = npcinfluencearr.optInt(MyTools.getRandom(0, npcinfluencearr.length()-1));//确认入侵势力
							//System.out.println("-------while------1----"+npcinfluencearr+"----"+npcinfluence+"----");
							DBPsRs citylistRs = DBPool.getInst().pQueryS(CBBAC.tab_cb_city, "citytype=3 and display=1");
							JSONArray cancitynumarr = new JSONArray();//集合可用于进攻的城市
							while(citylistRs.next()){
								cancitynumarr.add(citylistRs.getInt("num"));
							}
							while(cancitynumarr.length() > 0){//有可进攻城市
								//System.out.println("-------while------2------------");
								citynum = cancitynumarr.optInt(MyTools.getRandom(0, cancitynumarr.length()-1));//确认城市编号
								if(CBBAC.cbmgr.checkCityInWar(citynum)){//此城市正在战斗中
									cancitynumarr.remove((Integer)citynum);
									continue;
								}
								DBPaRs cityRs = DBPool.getInst().pQueryA(CBBAC.tab_cb_city, "num="+citynum);
								DBPsRs cityStorRs = CBBAC.getInstance().getCityStorRs(dbHelper, cityRs);
								if(!MyTools.checkSysTimeBeyondSqlDate(cityStorRs.getTime("nowarendtime"))){//免战中无法进攻
									cancitynumarr.remove((Integer)cityStorRs.getInt("citynum"));
									continue;
								}
								if(cityStorRs.getInt("influencenum") == npcinfluence){//不能入侵与自己势力相同的城市
									cancitynumarr.remove((Integer)cityStorRs.getInt("citynum"));
									continue;
								}
								break;//找到可用城池，结束查找
							}
							if(cancitynumarr.length() > 0){//确定了城池，结束查找
								break;
							} else {//未找到可用城池，移除此势力，继续查找
								npcinfluencearr.remove(String.valueOf(npcinfluence));
							}
						}
						if(npcinfluencearr.length() > 0){//确定了势力，发动入侵
							ReturnValue rv = CBBAC.getInstance().npcInvade(citynum, npcinfluence, npcamount);
							Out.println("国战NPC入侵 启动结果："+rv.info);
						} else {//无可用势力，提示失败
							Out.println("没有可进攻的城市");
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 初始化计时器
	 */
	public static void init(){
		for(int i = 0; i < CBDATA.invadedeclare.length; i++){
			long time = MyTools.getTimeLong(MyTools.getDateStr()+" "+CBDATA.invadedeclare[i][0]);
			long delay = time - System.currentTimeMillis();
			if(delay <= 0){
				delay += MyTools.long_day;
			}
			//System.out.println("CBDATA.invadedeclare[i][0]:"+MyTools.getTimeStr(System.currentTimeMillis()+delay));
			ServerBAC.timer.scheduleAtFixedRate(new CBNpcInvadeTT(CBDATA.invadedeclare[i][0]), delay, MyTools.long_day, TimeUnit.MILLISECONDS);
		}
		Out.println("国战NPC入侵计时器启动完成");
	}
}
