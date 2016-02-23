package com.moonic.team;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.json.JSONArray;

import com.moonic.bac.PartnerBAC;
import com.moonic.battle.SpriteBox;
import com.moonic.battle.TeamBox;
import com.moonic.socket.PushData;
import com.moonic.util.BACException;
import com.moonic.util.DBPool;
import com.moonic.util.DBPsRs;

import server.common.Sortable;
import server.common.Tools;

/**
 * 队伍
 * @author wkc
 */
public class Team implements Sortable{
	public int num;//队伍编号
	public int type;//队伍类型
	public String name;//名字
	public int lvLimit;//等级限制
	public String award;//奖励
	public String boss;//敌人
	
	public Member leader;//队长
	
	public Hashtable<Integer, Member> memberTable = new Hashtable<Integer, Member>();//队伍中所有成员
	
	public static int id = 1;//自增唯一ID
	
	public static final String tab_team_boss = "tab_team_boss";
	
	/**
	 * 获取队伍编号
	 */
	public static synchronized int getTeamNum(){
		return id++;
	} 
	
	/**
	 * 构造
	 * @throws Exception 
	 */
	public Team(Member leader, int type) throws Exception{
		this.num = getTeamNum();
		this.type = type;
		this.leader = leader;
		int plv = leader.lv;
		int row = 0;
		DBPsRs bossListRs = DBPool.getInst().pQueryS(tab_team_boss, "type="+type);
		while(bossListRs.next()){
			if(bossListRs.getInt("lv") > plv){
				break;
			}
			row++;
		}
		if(row == 0){
			BACException.throwInstance("没有符合条件的BOSS");		
		}
		bossListRs.setRow(row);
		this.name = bossListRs.getString("name");
		this.lvLimit = bossListRs.getInt("lv");
		this.award = bossListRs.getString("award");
		this.boss = bossListRs.getString("boss");
	}
	
	/**
	 * 获取队伍TeamBox
	 * @throws Exception
	 */
	public TeamBox getTeamBox() throws Exception{
		Member[] memberArr = memberTable.values().toArray(new Member[memberTable.size()]);
		if(memberArr.length == 3 && !memberArr[1].isLeader){//队长放中间
			int index = 0;
			for(int i = 0; i < memberArr.length; i++){
				if(memberArr[i].isLeader){
					index = i;
					break;
				}
			}
			Member temp = memberArr[1]; 
			memberArr[1] =  memberArr[index];
			memberArr[index] = temp;
		} 
		SpriteBox[][] spriteboxs = new SpriteBox[2][3];
		int column = 0;
		for(int i = 0; i < memberArr.length; i++){
			Member member = memberArr[i];
			int playerid = member.id;
			JSONArray posArr = member.getPosArr();
			ArrayList<SpriteBox> spritesList = PartnerBAC.getInstance().getSpriteBoxList(playerid, posArr, null);
			int row = 0;
			for (int j = 0; j < spritesList.size(); j++) {
				spriteboxs[row][column] = spritesList.get(j);
				row++;
			}
			column++;
		}
		TeamBox teambox = PartnerBAC.getInstance().getTeamBox(0, leader.name, leader.num, 0, spriteboxs);
		return teambox;
	}
	
	/**
	 * 检查准备状态
	 * @throws BACException 
	 */
	public void checkReady() throws BACException{
		Iterator<Member> members = memberTable.values().iterator();
		while (members.hasNext()) {
			Member member = members.next();
			if(!member.isReady){
				BACException.throwInstance("队员"+member.name+"尚未准备");
			}
		}
	}
	
	/**
	 * 推送信息给队伍成员
	 */
	public void sendMsgToTeam(short act, String info, int excludeid) throws Exception {
		Iterator<Integer> keys = memberTable.keySet().iterator();
		int[] pids = null;
		while (keys.hasNext()) {
			int id = keys.next();
			if(id != excludeid){
				pids = Tools.addToIntArr(pids, id);
			}
		}
		PushData.getInstance().sendPlaToSome(act, info, pids);
	}

	@Override
	public double getSortValue() {
		return this.num;
	}
	
	/**
	 * 获取队伍列表信息
	 */
	public JSONArray getTeamInfo(){
		JSONArray jsonarr = new JSONArray();
		jsonarr.add(this.num);//队伍编号
		jsonarr.add(this.leader.name);//队长名字
		jsonarr.add(this.lvLimit);//等级限制
		jsonarr.add(this.memberTable.size());//当前人数
		return jsonarr;
	}
	
	/**
	 * 获取队伍成员列表
	 */
	public JSONArray getMemberList(){
		JSONArray jsonarr = new JSONArray();
		Iterator<Member> members = memberTable.values().iterator();
		while(members.hasNext()){
			jsonarr.add(members.next().getMemberInfo());
		}
		return jsonarr;
	}
	
}
