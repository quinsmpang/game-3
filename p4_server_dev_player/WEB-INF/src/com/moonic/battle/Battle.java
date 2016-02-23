package com.moonic.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import server.common.Tools;
import server.config.LogBAC;

import com.ehc.common.ReturnValue;
import com.ehc.common.SqlString;
import com.moonic.bac.ConfigBAC;
import com.moonic.servlet.GameServlet;
import com.moonic.socket.PushData;
import com.moonic.util.DBHelper;
import com.moonic.util.MyRunnable;
import com.moonic.util.MyTools;

import conf.Conf;
import conf.LogTbName;

/**
 * 一场战斗的管理对象
 * 
 * @author huangyan
 * 
 */
public class Battle {
	public byte state;

	long battleId;
	BattleBox battleBox; // 包含AB两队	
	//public int enemyBatch=0;  //敌人批次
	public Vector<Actor> actorArr; //行动者队列，会有重复角色
	public Vector<BattleRole> battleRoleArr; //战斗双方角色全体集合，无重复角色

	public int turnCount; // 回合数计数
	public int[] playerIds; // 双方玩家id
	public Vector<BattleRole> sortedFighterlist; // 每回合根据速度排序可战斗的角色	
	long battleStartTime;// 战斗发起的时间
	int actorIndex;
	
	/**
	 * 战斗初始化
	 */
	public void init(BattleBox battleBox) {
		this.battleBox = battleBox;
		battleStartTime = System.currentTimeMillis();
		
		this.battleBox.createSpriteIds();
		createBattleRoleArrByBattleBox(battleBox);
		
		//按速度排序
		Collections.sort(battleRoleArr, new Comparator<BattleRole>() 
		{
			public int compare(BattleRole role1,BattleRole role2)
			{
				return role2.getSpeed() - role1.getSpeed();
			}				
		});
		
		//BattlePrint.print("排序后的角色");
		for (int i = 0; i < battleRoleArr.size(); i++) 
		{
			BattleRole role = battleRoleArr.get(i);
			//BattlePrint.print("排序后的角色"+role.getId()+" name="+role.getName()+" speed="+role.getSpeed());
			battleRoleArr.get(i).initSkills(); // 技能数据转为对象
		}
	}
	
	public void initActors(JSONObject turnJson)
	{
	    actorArr = new Vector<Actor>();
	   
	    JSONArray addBuffData=new JSONArray(); //装战前加的buff，[id,buffnum,id,buffnum]
	    
	    for(int i=0;i<battleRoleArr.size();i++)
    	{
	    	BattleRole battleRole = battleRoleArr.get(i);
	    	if(battleRole.isLive())
    		{	    		
	    		//BattlePrint.print(battleRole.getId()+"."+battleRole.getName()+" HP="+battleRole.getFinalProp(Const.PROP_HP));
	    				       
		        //BattlePrint.print(battleRole.getName()+".autoSkills.size()="+actor.battleRole.autoSkills.size());
		        if(battleRole.autoSkills.size() > 0)
		        {        		        	
	                boolean haveUseSkill = false;
	                for(int j=0;j<battleRole.autoSkills.size();j++)
	                {    
	                	boolean allowUseSkill=true;
	                	BattleSkill skill = battleRole.autoSkills.get(j);
	                    BattleSkillFixData skillData = skill.battleSkillFixData;	                   
	                    
	                    if(skillData.useTurn!=null)
	                    {
	                    	if(skillData.useTurn[0]==2)  //第一回合执行
	                    	{
	                    		allowUseSkill=false;
	                    		if(turnCount==1)
	                    		{
                                    //BattlePrint.print("执行"+actor.battleRole.getName()+"第一回合buff技能");
                                    int targetType = skillData.targetType;
                                    int rangeType = skillData.range;
                                    if(targetType==Const.TARGET_FRIEND)
                                    {
                                        if(rangeType == Const.RANGE_SELF) //对自己
                                        {
                                            for(int k=0;k<skillData.buffs.length;k++)
                                            {
                                            	battleRole.bindBuff(battleRole, skill.level, skillData.buffs[k]);
                                            	addBuffData.add(battleRole.getId());
                                            	addBuffData.add(skill.level);
                                            	addBuffData.add(skillData.buffs[k]);                                            	
                                            }
                                        }
                                    }
                                    else if(targetType==Const.TARGET_ENEMY)
                                    {
                                        if(rangeType == Const.RANGE_FRONT_SINGLE)  //正对面的敌人
                                        {
                                        	battleRole.searchTarget(targetType, rangeType);  
                                            if(battleRole.targetRoles!=null)
                                            {
                                                for(int k=0;k<battleRole.targetRoles.size();i++)
                                                {
                                                	for(int m=0;m<skillData.buffs.length;m++)
                                                	{
                                                		battleRole.targetRoles.get(k).bindBuff(battleRole,skill.level,skillData.buffs[m]);
                                                		addBuffData.add(battleRole.targetRoles.get(k).getId());
                                                		addBuffData.add(skill.level);
                                                    	addBuffData.add(skillData.buffs[m]); 
                                                	}                                                	                                                  
                                                }
                                            }
                                        }
                                    }
	                    		}
	                    	}
	                    	else if(skillData.useTurn[0]==3)  //限定回合执行
	                    	{
	                    		if(turnCount % skillData.useTurn[1] !=0)
	                    		{
	                    			allowUseSkill=false;
	                    		}	                    		
	                    	}
	                    }
	                    
	                    if(allowUseSkill)
	                    {
		                    int rate = skillData.rate;
		                    //BattlePrint.print(battleRole.getName()+"技能"+skill.battleSkillFixData.name+"概率="+rate);
		                    int rnd = battleRole.getRandomNumber(1, 100); //随机
		                    //BattlePrint.print("随机数="+rnd);
		                    if(rnd<=rate)
		                	{     
		                    	Actor actor = new Actor();
		        		        actor.battleRole = battleRole;	
		                    	//BattlePrint.print(battleRole.getName()+"使用技能"+skill.battleSkillFixData.name);
		                        actor.cmdType = Command.CMD_AUTOSKILL;
		                        actor.battleSkill = skill;                                           
		                        haveUseSkill=true;  
		                        actorArr.add(actor);	
		                	}  
	                    }
	                }
	                
	                //封包addbuff
	                if(addBuffData.length()>0)
	                {
	                	JSONObject addBuffJson = new JSONObject();
	                	addBuffJson.put(JSONWrap.KEY.ADD_BUFF, addBuffData);
	                	turnJson.put(JSONWrap.KEY.PRE, addBuffJson);
	                }

	                if(!haveUseSkill)  //没随到技能
	                {
	                	if(inAbnormal(battleRole))
            			{
			            	Actor actor = new Actor();
					        actor.battleRole = battleRole;	
			            	//BattlePrint.print(battleRole.getName()+"没有随机到随机技能，使用普攻");                    
			                actor.cmdType = Command.CMD_ATTACK;  
			                actorArr.add(actor);
            			}
	                }
		        }
		        else
		        {
		        	Actor actor = new Actor();
			        actor.battleRole = battleRole;	
	                //print(role.name,"没有随机技能，使用普攻");
	                actor.cmdType = Command.CMD_ATTACK;
	                actorArr.add(actor);	
		        }
    		}	        
    	}	
	}
	
	/**
	 * 怒技插入
	 * @param battleRole
	 */
	public void insertActor(BattleRole battleRole)
	{
		Actor actor = new Actor();
	    actor.battleRole = battleRole;
	    actor.cmdType = Command.CMD_ANGRYSKILL;
	    if(actor.battleRole.angrySkills!=null && actor.battleRole.angrySkills.size()>0)
	    {	    	
	        actor.battleSkill = actor.battleRole.angrySkills.get(0);
		    //插入到后面非怒技能位置
		    int insertIndex = actorIndex+1;
		    while(true)
		    {
		        if(insertIndex >= actorArr.size() || actorArr.get(insertIndex).cmdType != Command.CMD_ANGRYSKILL)
		        {
		        	//BattlePrint.print(battleRole.getName()+"怒气满了，插入到队列"+insertIndex+"位置");
		        	actorArr.insertElementAt(actor, insertIndex);
		        	break;
		        }  
		        else
		        {
		            insertIndex = insertIndex + 1;
		        }
		    }
	    }	    
	}
	public void printLeftHP()
	{
		//BattlePrint.print("----------第"+turnCount+"回合剩余血量-----------");
		for(int i=0;i<battleRoleArr.size();i++)
    	{
	    	BattleRole battleRole = battleRoleArr.get(i);
	    	//if(battleRole.isLive())
    		{
	    		BattlePrint.print(battleRole.getId()+"."+battleRole.getName()+" HP="+battleRole.getFinalProp(Const.PROP_HP));
    		}
    	}
	}
	public int[] getLeftHPData()
	{		
		int[] leftHP=null;
		for(int i=0;i<battleRoleArr.size();i++)
    	{
	    	BattleRole battleRole = battleRoleArr.get(i);
	    	leftHP = Tools.addToIntArr(leftHP, battleRole.getId());	    	
	    	leftHP = Tools.addToIntArr(leftHP, battleRole.getFinalProp(Const.PROP_HP));	    	
    	}
		return leftHP;
	}
	
	public JSONObject doPVPBattle() 
	{		
		JSONObject resultJson = new JSONObject();
		try
		{
			JSONArray replayJsonArray = new JSONArray();
			JSONWrap jsonWrap=null;
			turnCount=0;
			while (true) 
			{	
				turnCount++;
				//BattlePrint.print("--------第"+turnCount+"回合--------");
				JSONObject turnJson = new JSONObject();
				
				initAllBattleRole();
				//1.回合前执行被动技能
				JSONArray beSkillResult = new JSONArray();
				execAllBeSkill(beSkillResult);
				if(beSkillResult.length()>0)
				{					
					turnJson.put(JSONWrap.KEY.EXECBESKILL, beSkillResult);
				}
				//BattlePrint.print("JSONWrap.KEY.EXECBESKILL="+turnJson.toString());
				//2.回合前执行全部角色的buff，如中毒等
				JSONArray buffResult = new JSONArray();
				execAllBuff(buffResult);
				
				if(buffResult.length()>0)
				{					
					turnJson.put(JSONWrap.KEY.EXECBUFF, buffResult);
				}
				//BattlePrint.print("JSONWrap.KEY.EXECBUFF="+turnJson.toString());
				
				//3.生成战斗排队
				//BattlePrint.print("战斗排队");
				initActors(turnJson);	
				
				//BattlePrint.print("----------第"+turnCount+"回合开始血量-----------");
				//printLeftHP();
				//4.按队列执行本回合的战斗攻击流程				
				JSONArray queue = new JSONArray();
				for(int i=0;i<actorArr.size();i++)
				{					
					actorIndex=i;
					Actor actor = actorArr.get(i);		
					//BattlePrint.print(actor.battleRole.getName()+"行动,cmdType="+actor.cmdType);
					jsonWrap = actor.doBattle();
					if(jsonWrap!=null)
					{												
						//BattlePrint.print(jsonWrap.getJsonObj().toString());
						queue.add(jsonWrap.getJsonObj());						
					}
				}	
				turnJson.put(JSONWrap.KEY.LEFTHP,getLeftHPData());
				
				//BattlePrint.print("----------第"+turnCount+"回合结束血量-----------");
				//printLeftHP();
				turnJson.put(JSONWrap.KEY.QUEUE,queue);
				
				//5.回合结束执行buff减剩余回合数和清理
				JSONArray removeBuffResult = new JSONArray();
				reduceAllBuff(removeBuffResult);
				if(removeBuffResult.length()>0)
				{					
					turnJson.put(JSONWrap.KEY.REMOVE_BUFF, removeBuffResult);					
				}				
				
				replayJsonArray.add(turnJson);
				
				int winTeam = isEnd();
				if (winTeam>=0) 
				{	
					turnJson = new JSONObject();
					turnJson.put(JSONWrap.KEY.WIN_TEAM, winTeam);
					replayJsonArray.add(turnJson);
					//sb.append("winTeam="+winTeam+"\r\n");
					//BattlePrint.print("战斗结束");
					battleBox.winTeam = (byte)winTeam;
					break;
				}
				else 
				if(turnCount>=BattleConfig.maxTurns)
				{
					jsonWrap = new JSONWrap();
					jsonWrap.put(JSONWrap.KEY.WIN_TEAM, Const.teamB);  //到了回合限制数未出胜负，算守方赢
					replayJsonArray.add(jsonWrap.getJsonObj());

					battleBox.winTeam = Const.teamB; 
					break;
				}
			}
			resultJson.put("winTeam", battleBox.winTeam);
			resultJson.put("replay", replayJsonArray);
			//BattlePrint.print(replayJsonArray.toString());
			return resultJson;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 回合前执行被动技能
	 * @param beSkillResult
	 */
	public void execAllBeSkill(JSONArray beSkillResult)
	{
		for(int i=0;i<battleRoleArr.size();i++)
    	{
	    	BattleRole battleRole = battleRoleArr.get(i);
	    	battleRole.execBeSkill(beSkillResult);
    	}
	}
	
	public void initAllBattleRole()
	{
		for(int i=0;i<battleRoleArr.size();i++)
    	{
	    	BattleRole battleRole = battleRoleArr.get(i);
	    	battleRole.init();
    	}
	}
	
	/**
	 * 回合前执行buff
	 * @param buffResult
	 */
	public void execAllBuff(JSONArray buffResult)
	{
		for(int i=0;i<battleRoleArr.size();i++)
    	{
	    	BattleRole battleRole = battleRoleArr.get(i);
	    	battleRole.execBuff(buffResult);
    	}
	}
	
	/**
	 * 每回合减剩余回合数
	 * @param reduceResult
	 */
	public void reduceAllBuff(JSONArray reduceResult)
	{
		for(int i=0;i<battleRoleArr.size();i++)
    	{
	    	BattleRole battleRole = battleRoleArr.get(i);
	    	battleRole.reduceBuffTurns(reduceResult);
    	}
	}
	
	public int isEnd()
	{
	    int[] liveAmount = new int[2];
	    liveAmount[Const.teamA]=0;
	    liveAmount[Const.teamB]=0;
	
	    for(int i=0;i<battleRoleArr.size();i++)
	    {
	        BattleRole battleRole = battleRoleArr.get(i);
	        if(battleRole.isLive())
	        {
	        	liveAmount[battleRole.getTeamType()] = liveAmount[battleRole.getTeamType()]+1;
	        }
	    }
	    if(liveAmount[Const.teamA]>0 && liveAmount[Const.teamB]==0)
	    {
	        return Const.teamA; //A队赢
	    }
	    else if(liveAmount[Const.teamA]==0 && liveAmount[Const.teamB]>0)
	    {
	        return Const.teamB; //B队赢
	    }
	    else
	    {
	        return -1; //无胜负
	    }
	}
	
	public String getBattleReplay()
	{
		return null;
	}
	
	public void createBattleRoleArrByBattleBox(BattleBox battleBox) {
		battleRoleArr = new Vector<BattleRole>();
		createBattleRoleArrByBattleTeam(battleRoleArr, battleBox.teamArr[Const.teamA], Const.teamA);
		createBattleRoleArrByBattleTeam(battleRoleArr, battleBox.teamArr[Const.teamB], Const.teamB);		
	}
	
	private void createBattleRoleArrByBattleTeam(Vector<BattleRole> battleRoleList, ArrayList<TeamBox> teamBoxArr, byte teamType) {
		for (int i = 0; teamBoxArr != null && i < teamBoxArr.size(); i++) {
			TeamBox team = teamBoxArr.get(i); //其中一个小队，P4都是只有一个小队		
			
			for (int k = 0; team.sprites != null && k < team.sprites.size(); k++) {
				SpriteBox sprite = team.sprites.get(k);

				sprite.teamType = teamType;				
				if (sprite != null) {
					BattleRole battleRole = createBattleRole(team, sprite);
					battleRoleList.add(battleRole);					
				}
			}				
		}
	}
	private BattleRole createBattleRole(TeamBox team, SpriteBox sprite) {
		BattleRole battleRole = new BattleRole();
		battleRole.battle = this;		
		
		//sprite.battleRole = battleRole;
		battleRole.spriteBox = sprite;
		battleRole.teamBox = team;		
		battleRole.row = (byte)((sprite.posNum-1) / 3);
		battleRole.col = (byte)((sprite.posNum-1) % 3);
		return battleRole;
	}
	//在异常状态中
	public boolean inAbnormal(BattleRole role)
	{
	    if(role.inBuffStatus(Const.BUFF_TYPE_DIZZY) || role.inBuffStatus(Const.BUFF_TYPE_SILENCE))
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }	    
	}
	public static void main(String[] args)
	{
		Vector testVC = new Vector();
		for(int i=0;i<5;i++)
		{
			testVC.add(i);
		}
		for(int i=0;i<testVC.size();i++)
		{
			if(i==3)
			{
				testVC.add(5, "a");
			}
			//BattlePrint.print(testVC.get(i));
		}
	}

}
