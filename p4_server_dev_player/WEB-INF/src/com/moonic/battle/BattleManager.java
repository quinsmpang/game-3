package com.moonic.battle;

import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ehc.common.ReturnValue;
import com.moonic.util.MyLog;

/**
 * 所有战斗的管理对象
 * @author huangyan
 *
 */
public class BattleManager 
{	
	private static Vector<Battle> battleVec = new Vector<Battle>();
	
	private static MyLog log = new MyLog(MyLog.NAME_DATE, "battletime", "BATTLETIME", true, true, true, null);
	
	public static String Secret_Key = "5D769B6B39B15BB7584D457710C8DDA6";  //签名秘钥
	/**
	 * 生成PVP战斗录像
	 * @param battleBox
	 * @return
	 */
	public static void createPVPBattle(BattleBox battleBox) 
	{
		//BattlePrint.print("收到PVP战斗请求");
		Battle battle = new Battle();
		battle.init(battleBox);
		battleVec.add(battle);
		JSONObject jsonObj = new JSONObject();			
		String team = battleBox.getJSONArray().toString();
		
		//BattlePrint.print("jsonObj="+jsonObj.toString());
		JSONObject battleResult=battle.doPVPBattle();  //进行服务端快速战斗
		battleVec.remove(battle);
		if(System.currentTimeMillis()-battle.battleStartTime>10){
			log.d("战斗 "+battle.battleBox.battleId+" 用时："+(System.currentTimeMillis()-battle.battleStartTime)+"ms");
		}		
		
		jsonObj.put("winTeam", battleResult.optInt("winTeam"));
		jsonObj.put("replay", battleResult.optJSONArray("replay"));
		
		battleBox.replayData = new JSONArray();		
		battleBox.replayData.add(jsonObj);
		
		try {
			jsonObj.put("team", new JSONArray(team));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BattlePrint.print("battleBox.replayData="+battleBox.replayData.toString());
		
	}	
	
	/**
	 * 获取PVPV战斗信息
	 */
	public static ReturnValue getPVPBattleInfo(){
		Battle[] battleArr = battleVec.toArray(new Battle[battleVec.size()]);
		StringBuffer sb = new StringBuffer();
		sb.append("信息清单：\r\n");
		for(int i = 0; i < battleArr.length; i++){
			sb.append("战斗 "+battleArr[i].battleBox.battleId+" 已用时："+(System.currentTimeMillis()-battleArr[i].battleStartTime)+"ms"+"\r\n");
		}
		return new ReturnValue(true, sb.toString());
	}
	
	/**
	 * 验证PVE战斗过程和结果
	 * @param battleBox
	 * @param battleRecord
	 * @return
	 */
	public static ReturnValue verifyPVEBattle(BattleBox battleBox,String battleRecord) 
	{		
		//BattlePrint.print(battleRecord);
		try {
			JSONArray array = new JSONArray(battleRecord);
			JSONObject json = (JSONObject)array.get(array.length()-1);
			String winTeam = json.optString("win");
			if(winTeam.equals("0"))
			{
				battleBox.winTeam = Const.teamA;
				setRoleHP(battleBox,array);
				return new ReturnValue(true);
			}
			else if (winTeam.equals("1"))
			{
				battleBox.winTeam = Const.teamB;
				setRoleHP(battleBox,array);
				return new ReturnValue(true);
			}
			else
			{
				battleBox.winTeam = -1;  //作弊等
				return new ReturnValue(false);
			}				
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ReturnValue(false,e.toString());
		}		
	}
	private static void setRoleHP(BattleBox battleBox,JSONArray battleRecord) throws JSONException
	{
		//设置剩余血量
		JSONArray hpArray = (JSONArray)battleRecord.get(battleRecord.length()-2);
		for(int i=0;i<hpArray.length();i++)
		{
			JSONArray data = hpArray.optJSONArray(i);
			setRoleHP(battleBox,data.optInt(0),data.optInt(1));
		}
	}
	private static void setRoleHP(BattleBox battleBox,int id,int hp)
	{
		for (int i = 0; battleBox.teamArr != null && i < battleBox.teamArr.length; i++)   //遍历AB两队
		{
			ArrayList<TeamBox> teamBoxArr = battleBox.teamArr[i];
			for(int j=0;j<teamBoxArr.size();j++)  //遍历A或B的某小队，P4都是只有一个小队
			{
				TeamBox team = teamBoxArr.get(j);
				
				for (int k = 0; team.sprites != null && k < team.sprites.size(); k++)  //遍历角色 
				{
					SpriteBox sprite = team.sprites.get(k);
					if(sprite.id == id)
					{
						sprite.battle_prop[Const.PROP_HP] = hp;
					}					
				}				
			}			
		}
	}
}
