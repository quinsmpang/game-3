package com.moonic.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;

import server.common.Tools;

import com.moonic.socket.PushData;

/**
 * 战斗角色逻辑对象
 * @author huangyan
 *
 */
public class BattleRole 
{	
	//public int id; //id（当前战斗里唯一角色id）
	public SpriteBox spriteBox; //数据	
	public TeamBox teamBox; //所属的队
	public Battle battle; //所属的战斗
	
	public byte state;	
    

    public Command currCommand;//本次行动指令对象
    //public BattleSkill currBattleSkill; //当前使用中的技能   
    
    public ArrayList<BattleSkill> autoSkills; //随机技能   
    public ArrayList<BattleSkill> angrySkills; //怒技能
    public ArrayList<BattleSkill> beSkills; //被动技能
    

    public ArrayList<Buff> buffArr; //中到的buff状态    
    
    public int orderPriority; //排序优先级
    
    private Random ran;
    
    public int[] prop_change; //属性的变化值
    
    private static Object lock=new Object();
    private static int seed;
    
    private Vector<BattleRole> frontEnemy;
    private Vector<BattleRole> backEnemy;
    private Vector<BattleRole> allEnemy;
    private Vector<BattleRole> frontFriend;
    private Vector<BattleRole> backFriend;
    private Vector<BattleRole> allFriend;
    
    Vector<BattleRole> targetRoles;
    
    byte row;
    byte col;
    
    public Actor actor; //行动者
    
    public boolean waitUseAngry; //等待放怒技中
    
    public boolean isChangeBody;
    
    //战斗过程中数据变化（ 客户端封包用）
    public JSONArray removeBuffNumArr;  //攻击后去除buff记录  [roleId,buffNum]
    public JSONArray blueBloodArr;  //盾牌扣血记录  [roleId,shellHarm]
    public JSONArray addBuffNumArr;  //攻击后加buff记录  [roleId,buffNum]
    
    public BattleRole()
    {    	
		ran = new Random();
		synchronized (lock)  //确保同时生成的角色也有不同的随机数序列
		{
			seed++;
			ran.setSeed(seed);  
		}
		
		prop_change = new int[SpriteBox.BATTLE_PROP_LEN];
		
    } 
    
    public void init()
    {
    	removeBuffNumArr = new JSONArray();
    	blueBloodArr = new JSONArray();
    	addBuffNumArr = new JSONArray();
    }
    
    /**
     * 是否前排
     * @return
     */
    public boolean isFront()
    {
    	if(row==0)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }    
   
    public int getId()
    {
    	if(spriteBox!=null)
    	{
    		return spriteBox.id;
    	}
    	return 0;
    }
    public int getLevel()
    {
    	return spriteBox.level;
    }  
    
    public void initSkills()
    {
    	autoSkills = new ArrayList<BattleSkill>();
    	angrySkills = new ArrayList<BattleSkill>();
    	beSkills = new ArrayList<BattleSkill>();
    	//BattlePrint.print("spriteBox.autoSkills.length()="+spriteBox.autoSkills.length());
    	//BattlePrint.print("spriteBox.angrySkills.length()="+spriteBox.angrySkills.length());
    	
    	if(spriteBox.autoSkills.length()>0)
    	{
    		for(int i=0;i<spriteBox.autoSkills.length();i++)
    		{
    			JSONArray arr = (JSONArray)spriteBox.autoSkills.opt(i);
    			BattleSkill autoSkill=BattleSkillLib.getSkill(arr.optInt(0), arr.optInt(1));
    	    	autoSkills.add(autoSkill);
    		}
    	}
    	
    	if(spriteBox.angrySkills.length()>0)
    	{
    		for(int i=0;i<spriteBox.angrySkills.length();i++)
    		{
    			JSONArray arr = (JSONArray)spriteBox.angrySkills.opt(i);
    			BattleSkill angrySkill=BattleSkillLib.getSkill(arr.optInt(0), arr.optInt(1));
    			angrySkills.add(angrySkill);
    		}
    	} 
    	
    	if(spriteBox.beSkills.length()>0)
    	{
    		for(int i=0;i<spriteBox.beSkills.length();i++)
    		{
    			JSONArray arr = (JSONArray)spriteBox.beSkills.opt(i);
    			BattleSkill beSkill=BattleSkillLib.getSkill(arr.optInt(0), arr.optInt(1));
    			beSkills.add(beSkill);
    		}
    	} 
    }
	
    public boolean canFight()
    {
    	if(getFinalProp(Const.PROP_HP)>0  && !isChangeBody)
    	{    		
    		return true;
    	}    	
    	else
    	{
    		return false;
    	}
    }
    
    public void addBuff(BattleRole fromRole,Buff addBuff)
    {
        if (buffArr == null)
        {
            buffArr = new ArrayList<Buff>();
        }            

        //查有无同组的buff，需去除
        for (int i = buffArr.size()-1;i >=0; i--)
        {
            Buff buff = (Buff)buffArr.get(i);
            if (buff.buffFixData.buffGroup == addBuff.buffFixData.buffGroup)
            {
                buffArr.remove(i);                    
            }
        }
        
        //客户端封包数据
        //BattlePrint.print(fromRole.getName()+".addBuffNumArr"+this.getId()+" "+addBuff.buffFixData.num);
        if(fromRole!=null)
        {
        	fromRole.addBuffNumArr.add(this.getId());
        	fromRole.addBuffNumArr.add(addBuff.buffFixData.num);
        }
        //BattlePrint.print(fromRole.getName()+".addBuffNumArr.length()="+fromRole.addBuffNumArr.length());
        //BattlePrint.print(this.getName()+"被"+fromRole.getName()+"加buff"+addBuff.buffFixData.name);
        buffArr.add(addBuff);        
    }
    /// <summary>
    /// 去除全部buff效果
    /// </summary>
    public void removeAllBuff()
    {       
        if(buffArr != null)buffArr.clear();
    }
    public void removeBuff(Buff buff)
    {       
        if(buffArr != null)
        {
        	buffArr.remove(buff);
        }
    }
    /// <summary>
    /// buff每轮对属性的变化，如中毒，加血加魔等
    /// </summary>
    /// <returns>显示buff处理特效所需的秒数</returns>
    
    private void addBuffResult(JSONArray buffResult,int roleId,int type,int value)
    {
    	if(buffResult!=null)
    	{	    	
    		buffResult.add(roleId);
    		buffResult.add(type);
    		buffResult.add(value);	    	
    	}
    }
    /**
     * 每回合前执行被动技能
     * @param buffResult
     */
    public void execBeSkill(JSONArray changeResult)
    {
    	//todo buffResult加处理结果
    	if(isLive())
    	{
    		for(int i=0;beSkills!=null && i<beSkills.size();i++)
	        {
	        	BattleSkill skill = beSkills.get(i);	
	           
	            int beSkillLevel = skill.level;
	            BattleSkillFixData beSkillData = skill.battleSkillFixData;
	           
                if(beSkillData.owner ==2) 
                {
                    for(int j=0;j<beSkillData.args.length;j++)
                    {
                        short[] arg = beSkillData.args[j];
                        short t = arg[0];

                        if(t == Const.BESKILL_TYPE9) //9.每回合行动前恢复指定值的%生命，参数：9,条件类型(0,最大生命值|1,损失生命值),恢复百分比,升级提升百分比
                        {
                            int baseV = 0;
                            if(arg[1] == 0)  //最大生命值
                            {
                                baseV = this.getFinalProp(Const.PROP_MAXHP);
                            }
                            else if(arg[1] == 1)  //损失生命值
                            {
                                baseV = this.getFinalProp(Const.PROP_MAXHP) - this.getFinalProp(Const.PROP_HP);
                            }
                            int addHP = (int)(baseV * (arg[2] + (beSkillLevel -1 ) * arg[3])/100);
                            if(addHP >0)
                            {
                                changeHP(addHP);
                                changeResult.add(this.getId());
                                changeResult.add(Const.EXEC_RESULT_TYPE_HP);
                                changeResult.add(addHP);	  
                            }
                        }
                        else if(t == Const.BESKILL_TYPE12)  //12.每回合自动恢复怒气，参数：12,恢复怒气值
                        {
                            changeAngry(arg[1]);
                            changeResult.add(this.getId());
                            changeResult.add(Const.EXEC_RESULT_TYPE_ANGRY);
                            changeResult.add(arg[1]);
                        }
                        else if(t == Const.BESKILL_TYPE13)  //13.特定时间获得BUFF，参数：13,（0,回合前|除0以外其他值则表示指定回合及其倍数）,BUFF编号
                        {
                            if(arg[1]==0)
                            {
                                if(battle.turnCount==1)
                                {
                                    bindBuff(null,beSkillLevel,arg[2]);
                                    changeResult.add(this.getId());
                                    changeResult.add(Const.EXEC_RESULT_TYPE_ADDBUFF);
                                    changeResult.add(arg[2]);
                                }
                            }
                            else 
                            {
                                if(battle.turnCount % arg[1] ==0)
                                {
                                    bindBuff(null,beSkillLevel,arg[2]);
                                    changeResult.add(this.getId());
                                    changeResult.add(Const.EXEC_RESULT_TYPE_ADDBUFF);
                                    changeResult.add(arg[2]);
                                }
                            }
                        }                         
                	}
            	}  
        	}
        }    	
    }
    /**
     * 每回合前执行buff
     */
    public void execBuff(JSONArray buffResult)
    {       
        //处理中毒加血等buff
        for (int i = 0; buffArr != null && i < buffArr.size(); i++)
        {
            Buff buff = (Buff)buffArr.get(i);
            if(isLive())
            {            	
            	for(int j=0;j<buff.buffFixData.buffTypes.length;j++)
            	{
                    if(buff.buffFixData.buffTypes[j][0] == Const.BUFF_TYPE_LOSTHP)
                    {
                        int t = buff.buffFixData.args[j][0];
                        int v = buff.buffFixData.args[j][1];
                        int lvup = buff.buffFixData.args[j][2];
                        int harm=0;
                        if(t==0)
                        {
                            harm = (int)Math.ceil(buff.fromRoleAtk * (v + (buff.level - 1) * lvup)/100);
                        }
                        else
                        {
                            harm = v + (buff.level - 1) * lvup;
                        }

                        //被动技能改变伤害
                        int[] harmTypes = new int[]{1,Const.HARM_TYPE_DEBUFF};                        
                        harm = getChangeHarmByBeSkill(null,harm,harmTypes);  

                        if(harm > 0)
                        {                        	
                        	changeHP(-harm);
                        	if(harm!=0)
                            {                        		
                        		BattlePrint.print("第"+battle.turnCount+"回合"+this.getName()+"回合前buff扣血"+harm);
                            	addBuffResult(buffResult,getId(),Const.EXEC_RESULT_TYPE_HP,-harm);
                            }                                                   
                        }
                    }
            	}            	           
            }            
            buff.haveExeced=true;
        }               
    }
    

    /**
     * 被动技能对伤害的改变
     * @param attackRole
     * @param harm
     * @param harmTypes
     * @return
     */
    public int getChangeHarmByBeSkill(BattleRole attackRole,int harm ,int[] harmTypes)
    {
	    //harmTypes : 0,全部伤害|1,指向性伤害|2,DEBUFF伤害|3,物理伤害|4,法术伤害
	
	    //打人者的被动技能对伤害的改变
	    if(attackRole !=null && attackRole.beSkills!=null)
	    {
	        for(int i=0;i<attackRole.beSkills.size();i++)
	        {
	        	BattleSkill skill = attackRole.beSkills.get(i);	  
	        	int beSkillLevel = skill.level;
	            BattleSkillFixData beSkillData = skill.battleSkillFixData;
	           
	            if(beSkillData.owner ==2)  //过滤出打人时触发的被动技能
	            {  
	                for(int j=0;j<beSkillData.args.length;j++)
	                {
	                    short[] arg = beSkillData.args[j];
	                    short t = arg[0];
	
	                    if(t == Const.BESKILL_TYPE3)  //3.对指定职业类型伤害值变化，参数：3,职业编号,(1,增加|-1,减少),初始效果绝对值,升级效果绝对值
	                    {	                    	
	                        if(arg[1] == this.getSeries())
	                        {
	                            short updown = arg[2];
	                            short v = arg[3];
	                            short lvup = arg[4];
	                            harm = (int)(harm + harm * (float)(updown * (v + (beSkillLevel -1) * lvup))/100);
	                        }
	                    }
                        else if(t == Const.BESKILL_TYPE4)  //4.对到指定性别类型伤害值变化，参数：4,性别编号,(1,增加|-1,减少),初始效果绝对值,升级效果绝对值
                        {
	                        if(arg[1] == getSex())
	                        {
	                        	short updown = arg[2];
	                        	short v = arg[3];
	                        	short lvup = arg[4];
	                            harm = (int)(harm + harm * (float)(updown * (v + (beSkillLevel -1) * lvup))/100);
	                        }                            
                        }
                        else if(t == Const.BESKILL_TYPE6)   //6.对当前生命值指定百分比以上单位加成最终伤害，参数：6,条件参数,加成绝对值,升级提升绝对值 (伤害值 + 加成=最终伤害)
                        {
                        	short percent = arg[1];
                        	short v = arg[2];
                        	short lvup = arg[3];
	                        if((float)getFinalProp(Const.PROP_HP) / getFinalProp(Const.PROP_MAXHP) >= (float)percent/100)
	                        {
	                            harm = (int)(harm + harm * (float)(v + (beSkillLevel -1) * lvup)/100);
	                        }
                        }
	                }            
	            }	            
	        }
	    }
	    //被打者被动技能
	    if(beSkills!=null)
	    {
	        for(int i=0;i<beSkills.size();i++)
	        {
	        	BattleSkill skill = beSkills.get(i);	  
	        	int beSkillLevel = skill.level;
	            BattleSkillFixData beSkillData = skill.battleSkillFixData;
	           
	            
	            if(beSkillData.owner ==1) //过滤出被打时触发的被动技能 //被打者的被动技能对伤害的改变
	            {
	            	for(int j=0;j<beSkillData.args.length;j++)
	            	{
		                short[] arg = beSkillData.args[j];
		                short t = arg[0];
		
		                if(t == Const.BESKILL_TYPE3) //3.对指定职业类型伤害值变化，参数：3,职业编号,(1,增加|-1,减少),初始效果绝对值,升级效果绝对值
		                {
		                    if(attackRole!=null && arg[1] == attackRole.getSeries())
		                    {
		                        short updown = arg[2];
		                        short v = arg[3];
		                        short lvup = arg[4];
		                        harm = (int)(harm + harm * (float)(updown * (v + (beSkillLevel -1) * lvup))/100);
		                    }
		                }
		                else if(t == Const.BESKILL_TYPE4) //4.对到指定性别类型伤害值变化，参数：4,性别编号,(1,增加|-1,减少),初始效果绝对值,升级效果绝对值
		                {
		                    if(attackRole!=null && arg[1] == attackRole.getSex())
		                    {
		                        short updown = arg[2];
		                        short v = arg[3];
		                        short lvup = arg[4];
		                        harm = (int)(harm + harm * (float)(updown * (v + (beSkillLevel -1) * lvup))/100);
		                    }
		                }
		                else if(t == Const.BESKILL_TYPE5)  //5.概率受到指定类型伤害值变化，参数：5,触发几率,等级提升值,伤害类型(0,全部伤害|1,指向性伤害|2,DEBUFF伤害|3,物理伤害|4,法术伤害),条件类型(1,增加|-1,减少),初始绝对值,升级提升绝对值
		                {		                	
		                    short rate = (short)(arg[1] + (beSkillLevel - 1) * arg[2]);
		                    short rnd = (short)Tools.getRandomNumber(1,100);		                    
		                    
		                    if(rnd <= rate)
		                    {
		                        short harmType = arg[3];
		                        boolean check = false;
		                        if(harmType ==0)
		                        {
		                            check = true;
		                        }
		                        else
		                        {
		                            check = Tools.intArrContain(harmTypes,harmType);
		                        }
		                        if(check)
		                        {
		                            short updown = arg[4];
		                            short v = arg[5];
		                            short lvup = arg[6];
		                            
		                            harm = (int)(harm + harm * (float)(updown * (v + (beSkillLevel -1) * lvup))/100);
		                        }
		                    }
		                }
		                else if(t == Const.BESKILL_TYPE7)   //7.生命值不足指定百分比时减少受到伤害，参数：7,条件参数,减免百分比,升级提升百分比
		                {
		                    short percent = arg[1];
		                    short v = arg[2];
		                    short lvup = arg[3];
		                    if((float)getFinalProp(Const.PROP_HP) / getFinalProp(Const.PROP_MAXHP) <= (float)percent/100)
		                    {
		                        harm = (int)(harm - harm * (float)(v + (beSkillLevel -1) * lvup)/100);
		                    }                    
		                }
	            	} 
	            }
	        }
	    }
	
	    if(harm <0)
	    {
	        harm=0;
	    }
	
	    return harm;
    }
   
    /**
     * 每回合结束后减buff剩余次数
     */
    public void reduceBuffTurns(JSONArray array)
    {
    	for (int i = 0; buffArr != null && i < buffArr.size(); i++)
        {
            Buff buff = (Buff)buffArr.get(i);
            if(buff.haveExeced)
            {
            	buff.turns--;  //剩余回合数减一
            	//BattlePrint.print(getName()+"的buff "+buff.buffFixData.name+" 剩余回合数="+buff.turns);
            	//battle.addDeailStrData(getName()+"("+id+")"+"的"+buff.buffFixData.name+"BUFF剩余回合数="+buff.turns);
            }
        }
    	clearBuff(array);
    }
    private void clearBuff(JSONArray array)
    {
    	//剩余回合数为0的去掉
        if (buffArr != null && buffArr.size() > 0)
        { 
            for (int i = buffArr.size()-1; buffArr != null && i>=0; i--)
            {
                Buff buff = (Buff)buffArr.get(i);
                if (buff.turns == 0)
                {                	
					array.add(getId());
					array.add(buff.buffFixData.num);	
					if(buff.isChangeBuff)  //是变色buff需还原状态
					{
						this.isChangeBody=false;
					}
					//BattlePrint.print(getName()+"被移除buff"+buff.buffFixData.name);
                    buffArr.remove(i);                   
                }        
            }
        } 
    }
    /**
     * 检查角色是否属于某异常状态：
     * @param buffType
     * @return
     */
    public boolean isInAbnormalStatus(int buffType)
    {
    	for (int i = 0; buffArr != null && i < buffArr.size(); i++)
        {
            Buff buff = (Buff)buffArr.get(i);
            for(int j=0;j<buff.buffFixData.buffTypes.length;j++)
            {
                if(buff.buffFixData.buffTypes[j][0] == buffType)
                {
                    return true;
                }
            }            
        }
        return false;
    }
    /**
     * 检查角色是否中了某编号的buff
     * @param buffNum
     * @return
     */
    public boolean isInBuff(int buffNum)
    {
    	for (int i = 0; buffArr != null && i < buffArr.size(); i++)
        {
            Buff buff = (Buff)buffArr.get(i);
            if(buff.buffFixData.num == buffNum)
            {
            	return true;
            }
        }
        return false;
    }   

    /// <summary>
    ///  获取经过buff和增益加成后的最终属性值
    /// </summary>
    /// <param name="propIndex"></param>
    /// <returns></returns>
    public int getFinalProp(byte propIndex)
    {
        int orgProp = getProp(propIndex); 
        int addProp = getBuffAddProp(propIndex);
        if(addProp!=0)
        {
        	//BattlePrint.print(this.getName()+"的属性"+propIndex+"受buff变化"+addProp);
        }
        //updatePropChange();
        int addProp2 = prop_change[propIndex];
        //BattlePrint.print(getName()+"的属性"+propIndex+" BuffAddProp="+addProp);
        int finalProp = orgProp + addProp+addProp2;
        if (finalProp < 0) finalProp = 0;
        return finalProp;
    }  
    
	
	//获得buff改变的伤害,返回角色的伤害，盾的伤害
	public int getChangeHarmByBuff(BattleRole attackRole,int harm)
	{
	    if(buffArr == null)
	    {
	        return harm;
	    }
	    int roleHarm=harm;
	    int shellHarm=0;
	
	    ArrayList<Buff> removeBuffList = new ArrayList<Buff>();    
	
	    for(int i = 0;i<buffArr.size();i++)
	    {
	       Buff buff = buffArr.get(i);
	       for(int j=0;j<buff.buffFixData.buffTypes.length;j++)
	       {
	            if(buff.buffFixData.buffTypes[j][0] ==Const.BUFF_TYPE_SHELL)
	            {	                
	                int hp = buff.shellHP;
	                //BattlePrint.print(this.getName()+"的盾牌血="+buff.shellHP);
	                if(hp>0)
	                {
	                   if(buff.shellHP >= roleHarm)
	                   {
	                        buff.shellHP = buff.shellHP - roleHarm;
	                        if(buff.shellHP<=0)
	                        {
	                        	removeBuffList.add(buff);	                            
	                        }
	                        shellHarm = roleHarm;
	                        attackRole.blueBloodArr.add(this.getId());
	                        attackRole.blueBloodArr.add(shellHarm);
	                        roleHarm =0;
	                        //BattlePrint.print(getName()+"的盾牌被"+attackRole.getName()+"打掉"+shellHarm+"血,还剩"+buff.shellHP);
	                   }
	                   else
	                   {
	                        roleHarm = roleHarm - hp;
	                        shellHarm = hp;
	                        attackRole.blueBloodArr.add(this.getId());
	                        attackRole.blueBloodArr.add(shellHarm);
	                        
	                        buff.shellHP = 0;
	                        removeBuffList.add(buff);
	                        //BattlePrint.print(getName()+"的盾牌被"+attackRole.getName()+"打暴");
	                   }
	                }
	            }
	            else if( buff.buffFixData.buffTypes[j][0] ==Const.BUFF_TYPE_CHANGEHARM)
	            {
	                int updown = buff.buffFixData.updown;
	                int t = buff.buffFixData.args[j][0];
	                int v = buff.buffFixData.args[j][1];
	                int lvup = buff.buffFixData.args[j][2];
	                //BattlePrint.print(this.getName()+"改变伤害buff t="+t+",v="+v+",lvup="+lvup+",buff.level="+buff.level);
	                //BattlePrint.print("原roleHarm="+roleHarm);
	                if(t==0)  //百分比
	                {
	                    roleHarm = (int)Math.ceil(roleHarm + updown * roleHarm * (v + (buff.level-1) * lvup)/100);
	                }
                    else  //绝对值
                    {
	                    roleHarm = (int)Math.ceil(roleHarm + updown * (v + (buff.level-1) * lvup));
	                    if(roleHarm <0)
	                    {
	                        roleHarm=0;
	                    }
                    }
	                //BattlePrint.print("变化后的roleHarm="+roleHarm);
	            }
	       }
	    }
	    
	    for(int i=0;i<removeBuffList.size();i++)
	    {	    	
	    	//记录到json给客户端
	    	attackRole.removeBuffNumArr.add(this.getId());
	    	attackRole.removeBuffNumArr.add(((Buff)removeBuffList.get(i)).buffFixData.num);
            
            removeBuff((Buff)removeBuffList.get(i));            
	    }
	
	    return roleHarm;	    
	}
   
    
    /// <summary>
    /// 获取经过buff加成的属性add值
    /// </summary>
    /// <param name="propIndex">从0开始的属性索引</param>
    /// <returns></returns>
    public int getBuffAddProp(int propIndex)
    {
    	int add = 0;  
    	
        if (propIndex >= Const.PROP_MAXHP && propIndex <= Const.PROP_BECRIT_SUB_DAMAGE)
        {                      
            for (int i = 0; buffArr != null && i < buffArr.size(); i++)
            {
                Buff buff = (Buff)buffArr.get(i);
                
                for(int j=0;j<buff.buffFixData.buffTypes.length;j++)
                {
                    if(buff.buffFixData.buffTypes[j][0] == Const.BUFF_TYPE_PROP)
                    {
                        if(buff.buffFixData.buffTypes[j][1] == propIndex)
                        {                         
                            byte updown = buff.buffFixData.updown;                        
                            int[] arg = buff.buffFixData.args[j];
                            int t=arg[0];
                            int v = arg[1];
                            int lvup = arg[2];
                            if(t==0) //按百分比算
                            {
                                add = add + getProp(propIndex) * updown * (v +(buff.level - 1) * lvup)/100;
                            }
                            else  //按绝对值算
                            {
                                add = add + updown * (v +(buff.level - 1) * lvup);
                            }
                        }
                    }
                }  
            }               
        }
        if(add!=0)
        {
        	//battle.addDeailStrData(getName()+"("+id+")的"+propNames[propIndex-2]+"的最终变化值为"+getProp(propIndex)+"+("+add+")"+"->"+(getProp(propIndex)+add));
        	//BattlePrint.print(getName()+"("+id+")的"+propNames[propIndex-2]+"的最终变化值为"+getProp(propIndex)+"+("+add+")"+"->"+(getProp(propIndex)+add));
        }    	
        return add;
    }
   
    public void setState(int theState)
    {
    	this.state = (byte)theState;
    }
    public byte getTeamType()
    {
    	return spriteBox.teamType;
    }   
    
    public int getSpeed()
    {
    	return getFinalProp(Const.PROP_SPEED);
    } 
    
    /**
     * 收集前后排敌我角色集合
     */
    public void collectEnemys()
    {
    	frontEnemy = new Vector<BattleRole>();
        backEnemy = new Vector<BattleRole>();
        allEnemy = new Vector<BattleRole>();
        frontFriend = new Vector<BattleRole>();
        backFriend = new Vector<BattleRole>();
        allFriend = new Vector<BattleRole>();
    	
        for(int i=0;i<battle.battleRoleArr.size();i++)
        {
        	BattleRole battleRole = battle.battleRoleArr.get(i);
        	if(battleRole.getFinalProp(Const.PROP_HP)>0)
        	{
        		if(battleRole.getTeamType() != this.getTeamType())
        		{
        			if(battleRole.isFront())
        			{
        				frontEnemy.add(battleRole);
        			}
        			else
        			{
        				backEnemy.add(battleRole);
        			}
        			allEnemy.add(battleRole);
        		}
        		else
        		{
        			if(battleRole.isFront())
        			{
        				frontFriend.add(battleRole);
        			}
        			else
        			{
        				backFriend.add(battleRole);
        			}
        			allFriend.add(battleRole);
        		}
        	}
        }
    } 

	/**
	 * 找列活人数最多的目标
	 * @param team
	 * @return
	 */
	public Vector<BattleRole> findColTargets(int team)
	{
		Vector<BattleRole> tmp = new Vector<BattleRole>();
		
	    BattleRole[][] grid=new BattleRole[2][3];
	    
	    for(int i=0;i<battle.battleRoleArr.size();i++)
	    {
	    	BattleRole role = battle.battleRoleArr.get(i);
	        if(role.getTeamType() == team)
	        {
	            if(role.getFinalProp(Const.PROP_HP)>0)
	            {
	                grid[role.row][role.col] = role;           
	            }
	        }
	    }

	    //先找正前方位置  
	    int targetCol = 2 - col;
	    if(grid[0][targetCol]!=null && grid[1][targetCol]!=null)
	    {
	    	tmp.add(grid[0][targetCol]);
	    	tmp.add(grid[1][targetCol]);
	        return tmp;
	    }

	    if(grid[0][targetCol]!=null)
	    {
	    	tmp.add(grid[0][targetCol]);
	        return tmp;
	    }

	    if(grid[1][targetCol]!=null)
	    {
	    	tmp.add(grid[1][targetCol]);
	        return tmp;
	    }
	    
	    //先找有2活人的列
	    for(int col=0;col<3;col++)
	    {
	        if(grid[0][col]!=null && grid[1][col]!=null)
        	{
	        	tmp.add(grid[0][col]);
	        	tmp.add(grid[1][col]);
	        	return tmp;        	   
        	}
	    }
	    
	    //找前排
	    for(int col=0;col<3;col++)
	    {
	        if(grid[0][col]!=null)
	        {
	        	tmp.add(grid[0][col]);	       
	            return tmp;
	        }        
	    }
	    //找后排
	    for(int col=0;col<3;col++)
	    {
	        if(grid[1][col]!=null)
	        {
	        	tmp.add(grid[1][col]);	       
	            return tmp;
	        }        
	    }	   
	    return null;
	}
    
    public void searchTarget(int skillTargetType,int rangeType)
    {
    	targetRoles = new Vector<BattleRole>();
    	collectEnemys();
    	Vector<BattleRole> tmp=null;
    	
    	if(skillTargetType == Const.TARGET_ENEMY)  //对敌方技能
		{
	        if(rangeType == Const.RANGE_FRONT_SINGLE || rangeType == Const.RANGE_BACK_SINGLE)  //优先前排单体,优先后排单体
        	{
	        	if(rangeType == Const.RANGE_FRONT_SINGLE)
	        	{
		            //有前排则只能选前排目标，没前排可选后排目标
		            if(frontEnemy.size() > 0)
	            	{
		                tmp = frontEnemy;
	            	}
	        	}
	        	else if(rangeType == Const.RANGE_BACK_SINGLE)
	        	{
	        		if(backEnemy.size() > 0)
	            	{
		                tmp = backEnemy;
	            	}
	        	}
	            if(tmp==null)	            
	            {
	                tmp = allEnemy;
	            }		            
	            
	            if(tmp.size()>0)
            	{
	            	BattleRole target=null;
	            	
	            	byte[] searchCols = new byte[3];
	            	searchCols[0] = (byte)(2 - col); //先找正前方
	            	searchCols[1] = (byte)(3- col); //左前方
	            	searchCols[2] = (byte)(1- col); //右前方	                

	                for(int i=0;i<searchCols.length;i++)
	                {
	                	for(int j=0;j<tmp.size();j++)
		            	{
		                    if(tmp.get(j).col == searchCols[i])
	                    	{
		                    	target = tmp.get(j);	                    	
		                    	break;
	                    	}
		            	}
	                }
	              
	            	if(target==null) 
	            	{
	            		int index = getRandomNumber(0,tmp.size()-1);  //随机找一个
	            		target = tmp.get(index);
	            	}
	            	if(actor.battleSkill!=null)
	            	{
	            		if(actor.battleSkill.battleSkillFixData.maxUseTimes>1)  //是连击
	            		{
	            			for(int i=0;i<actor.battleSkill.battleSkillFixData.maxUseTimes;i++)
	            			{
	            				targetRoles.add(target);
	            			}
	            		}
	            		else
	            		{
	            			targetRoles.add(target);	
	            		}
	            	}
	            	else
	            	{
	            		targetRoles.add(target);	
	            	}
	            }
        	}
	        else if(rangeType == Const.RANGE_FRONT_LINE) //优先打前排          
	        {
	            if(frontEnemy.size()>0)
            	{
	                targetRoles = frontEnemy;
            	}
	            else if(backEnemy.size()>0)
            	{
	                targetRoles = backEnemy; 
            	}
	        }
	        else if(rangeType == Const.RANGE_COL)  //打纵列
	        {
	            targetRoles = findColTargets(1 - getTeamType());
	        }	        
	        else if(rangeType == Const.RANGE_N_HIGH)
        	{
	            tmp = allEnemy;
	            if(tmp.size() > 0)
	            {
	                final byte propIndex = (byte)actor.battleSkill.battleSkillFixData.rangeArgs[0];
	                int amount = actor.battleSkill.battleSkillFixData.rangeArgs[1];
	                if(amount > tmp.size())
	                {
	                    amount = tmp.size();
	                }
	                Collections.sort(tmp, new Comparator<BattleRole>() 
            		{
            			public int compare(BattleRole role1,BattleRole role2)
            			{
            				return role2.getFinalProp(propIndex) - role1.getFinalProp(propIndex);
            			}				
            		});	                
	                
	                Vector<BattleRole> finalTargets = new Vector<BattleRole>();
	                for(int i=0;i<amount;i++)
	                {
	                	finalTargets.add(tmp.get(i));	                    
	                }
	                targetRoles = finalTargets;
	            }
        	}
	        else if(rangeType == Const.RANGE_N_LOW)
	        {
	        	tmp = allEnemy;
	            if(tmp.size() > 0)
	            {
	                final byte propIndex = (byte)actor.battleSkill.battleSkillFixData.rangeArgs[0];
	                int amount = actor.battleSkill.battleSkillFixData.rangeArgs[1];
	                if(amount > tmp.size())
	                {
	                    amount = tmp.size();
	                }
	               
	                Collections.sort(tmp, new Comparator<BattleRole>() 
            		{
            			public int compare(BattleRole role1,BattleRole role2)
            			{
            				return role1.getFinalProp(propIndex) - role2.getFinalProp(propIndex) ;
            			}				
            		});	                
	                
	                Vector<BattleRole> finalTargets = new Vector<BattleRole>();
	                for(int i=0;i<amount;i++)
	                {
	                	finalTargets.add(tmp.get(i));	                    
	                }
	                targetRoles = finalTargets;
	            }
	        }
	        else if(rangeType == Const.RANGE_ALL)
	        {
	            if(allEnemy.size() > 0)
	            {
	                targetRoles = allEnemy;
	            }
	        }
	        else if(rangeType == Const.RANGE_SINGLE) //任意单体
	        {
	            tmp = allEnemy;
	            if(tmp.size()>0)
	            {
	                int index = getRandomNumber(0,tmp.size()-1);	                
	                targetRoles.add(tmp.get(index));        
	            } 
	        }
	        else if(rangeType == Const.RANGE_BACK_LINE)  //优先打后排
	        {
	            if(backEnemy.size() > 0)
            	{
	                targetRoles = backEnemy;  
            	}
	            else if(frontEnemy.size() > 0)
            	{
	                targetRoles = frontEnemy;  
            	}
	        }
	        else if(rangeType == Const.RANGE_SPLIT)  //散打
	        {
	        	if(allEnemy.size()>0)
	        	{
	        		targetRoles = getRandomAmountTargets(allEnemy,actor.battleSkill.battleSkillFixData.maxUseTimes);
	        	}
	        }
	        else
	        {
	            //BattlePrint.print("targetType="+skillTargetType+",未知range类型="+rangeType);
	        }   
		}
    	else if(skillTargetType == Const.TARGET_FRIEND)  //对友方技能
    	{
	        if(rangeType == Const.RANGE_COL)  //纵列
	        {
	            targetRoles = findColTargets(getTeamType());
	        }
	        else if(rangeType == Const.RANGE_SELF)   //自己
	        {
	        	targetRoles.add(this);
	        }
	        else if(rangeType == Const.RANGE_N_HIGH)  
	        {
	            tmp = allFriend;
	            if(tmp.size() > 0)
	            {
	                final byte propIndex = (byte)actor.battleSkill.battleSkillFixData.rangeArgs[0];
	                int amount = actor.battleSkill.battleSkillFixData.rangeArgs[1];
	                if(amount > tmp.size())
	                {
	                    amount = tmp.size();
	                }
	                Collections.sort(tmp, new Comparator<BattleRole>() 
            		{
            			public int compare(BattleRole role1,BattleRole role2)
            			{
            				return role2.getFinalProp(propIndex) - role1.getFinalProp(propIndex);
            			}				
            		});	                
	                
	                Vector<BattleRole> finalTargets = new Vector<BattleRole>();
	                for(int i=0;i<amount;i++)
	                {
	                	finalTargets.add(tmp.get(i));	                    
	                }
	                targetRoles = finalTargets;
	            }
	        }
	        else if(rangeType == Const.RANGE_N_LOW)
	        {
	            tmp = allFriend;
	            if(tmp.size() > 0)
	            {
	                final byte propIndex = (byte)actor.battleSkill.battleSkillFixData.rangeArgs[0];
	                int amount = actor.battleSkill.battleSkillFixData.rangeArgs[1];
	                if(amount > tmp.size())
	                {
	                    amount = tmp.size();
	                }
	               
	                Collections.sort(tmp, new Comparator<BattleRole>() 
            		{
            			public int compare(BattleRole role1,BattleRole role2)
            			{
            				return role1.getFinalProp(propIndex) - role2.getFinalProp(propIndex) ;
            			}				
            		});	                
	                
	                Vector<BattleRole> finalTargets = new Vector<BattleRole>();
	                for(int i=0;i<amount;i++)
	                {
	                	finalTargets.add(tmp.get(i));	                    
	                }
	                targetRoles = finalTargets;
	            }
	        }
	        else if(rangeType == Const.RANGE_ALL)  //全体
	        {
	            targetRoles = allFriend;
	        }
	        else if(rangeType == Const.RANGE_SINGLE)  //任意单体
	        {
	            tmp = allFriend;
	            if(tmp.size()>0)
            	{
	                int index = getRandomNumber(0,tmp.size()-1);	                
	                targetRoles.add(tmp.get(index));        
            	} 
	        }
	        else if(rangeType== Const.RANGE_LINE)  //单排
	        {
	            if(frontFriend.size() >0)
	            {
	                targetRoles = frontFriend;
	            }
	            else if(backFriend.size() >0)
            	{
	                targetRoles = backFriend;
            	}
	        }
	        else
	        {
	           // BattlePrint.print("targetType="+skillTargetType+",未知range类型="+rangeType);
	        } 
    	} 
    }
    
    //获得随机的N个目标
    public Vector<BattleRole> getRandomAmountTargets(Vector<BattleRole> allTarget,int amount)
    {
        if(allTarget==null)
    	{
            return null;
    	}
        else
        {
            if(allTarget.size() <= amount)
            {
                return allTarget;
            }
            else
            {
            	Vector<BattleRole> targets = new Vector<BattleRole>();
            	Vector<BattleRole> tmp = allTarget;
                for(int i=0;i<amount;i++)
                {
                    int num = Tools.getRandomNumber(0,tmp.size()-1);
                    targets.add(tmp.get(num));
                    tmp.remove(num);                    
                }
                return targets;
            }
        }
    }
	
    
    /**
     * 获取随机技能
     * @param num
     * @return
     */
    public BattleSkill getAutoSkill(int num)
    {
    	for(int i=0;autoSkills!=null && i<autoSkills.size();i++)
		{
    		if(autoSkills.get(i).battleSkillFixData.num==num)
    		{
    			return autoSkills.get(i);
    		}
		}    
    	return null;
    }
    
    /**
     * 获取怒技能
     * @param num
     * @return
     */
    public BattleSkill getAngrySkill(int num)
    {
    	for(int i=0;angrySkills!=null && i<angrySkills.size();i++)
		{
    		if(angrySkills.get(i).battleSkillFixData.num==num)
    		{
    			return angrySkills.get(i);
    		}
		}    
    	return null;
    }

    /**
     * 一轮行动结束，清除之前指令
     */
    public void clearCommand()
    {
        currCommand = null;                
    }
   
	/**
	 * 是否存活
	 * @return
	 */
	public boolean isLive()
    {
		return getFinalProp(Const.PROP_HP)>0?true:false;
    }
	public int getPlayerId()
    {
		return spriteBox.playerId;
    }
	public int getPartnerId()
    {
		return spriteBox.partnerId;
    }
	
	public int getProp(int index)
	{
		return spriteBox.battle_prop[index];
	}
	public void changeHP(int changeValue)
	{
		if(changeValue!=0)
		{
			if(changeValue >0)
			{
		        //查是否有不能被加血的buff
		        if(inBuffStatus(Const.BUFF_TYPE_UNCUREABLE))
        		{
		            return;
        		}
			}
			int result = spriteBox.battle_prop[Const.PROP_HP] + changeValue;
			if(result<0)
			{
				result=0;
			}
			else
			if(result>spriteBox.battle_prop[Const.PROP_MAXHP])
			{
				result = spriteBox.battle_prop[Const.PROP_MAXHP];
			}
			spriteBox.battle_prop[Const.PROP_HP] = result;
			if(spriteBox.battle_prop[Const.PROP_HP]<=0)
			{
				removeAllBuff();				
			}
		}		
	}
	
	public byte changeAngry(int changeValue)
	{
		if(changeValue!=0)
		{
			//查是否有不能加怒气的buff
            if(changeValue>0 && inBuffStatus(Const.BUFF_TYPE_NOTADDANGRY))
            {
                return 0; 
            }
            
			int result = spriteBox.battle_prop[Const.PROP_ANGER] + changeValue;
			if(result<0)
			{
				result=0;
			}
			else
			if(result> BattleConfig.angryMaxValue)
			{
				result = BattleConfig.angryMaxValue;
			}
			spriteBox.battle_prop[Const.PROP_ANGER] = result;
			//todo  满100插队到非怒技能后面
			if(spriteBox.battle_prop[Const.PROP_ANGER] >= BattleConfig.angryMaxValue && !waitUseAngry)
			{
				waitUseAngry=true;
				this.battle.insertActor(this);				
			}
		}	
		
		return (byte)spriteBox.battle_prop[Const.PROP_ANGER];
	}
	
	public void setAngry(int setValue)
	{
		spriteBox.battle_prop[Const.PROP_ANGER] = setValue;
	}
	
	
	public boolean calcDodge(BattleRole attackRole)
	{
	    //实际闪避率=（自身闪避-敌方命中）/(伙伴等级*mod10+mod11*（自身闪避-敌方命中）)
	    int hit = attackRole.getFinalProp(Const.PROP_HIT);
	    int dodge = getFinalProp(Const.PROP_DODGE);
	    if(dodge > hit)
	    {
	        double dodgeRate = (dodge - hit)/(getLevel() * BattleData.mod10 + BattleData.mod11 * (dodge - hit));
	        double rnd=getRandomFloatNumber(0, 1);
	        
	        if(rnd<dodgeRate)
	        {	            
	            return true;                      
	        }
	        else
	        {
	        	return false;
	        }
	    }
	    else
	    {
	    	return false;
	    }
	}
	
	public int calcCritical(BattleResult br,BattleRole attackRole,int baseHarm)
	{
	    //1.计算格挡
	    //实际格挡率=（自身格挡-敌方破击）/(伙伴等级*mod13+mod14*（自身格挡-敌方破击）)
	    int bk = attackRole.getFinalProp(Const.PROP_BREAK);
	    int block = getFinalProp(Const.PROP_BLOCK);
	    if(block > bk)
	    {
	        double blockRate = (block - bk)/(getLevel() * BattleData.mod13 + BattleData.mod14 * (block - bk));
	        
	        double rnd= getRandomFloatNumber(0, 1);
	        
	        if(rnd<blockRate)
	        {
	        	br.isBlock=true;
	            //格挡后形成的伤害值=最终伤害值*mod16
	            return (int)Math.ceil(baseHarm * BattleData.mod16);
	        }
	    }
	    
	    //2.计算暴击    
	    //暴击率=(攻击方暴击率-敌方韧性)/(伙伴等级*mod5+mod6*(攻击方暴击率-敌方韧性))	
	    int critical = attackRole.getFinalProp(Const.PROP_CRITICAL);
	    int toughness = getFinalProp(Const.PROP_TOUGHNESS);
	    if(critical > toughness)
	    {
	        double criticalRate = (critical - toughness)/(getLevel() * BattleData.mod5 + BattleData.mod6 * (critical - toughness));
	        
	        double rnd= getRandomFloatNumber(0, 1);	
	        if(rnd<criticalRate)
	        {
	        	br.isCriticalAtk = true;
	            return (int)Math.ceil(baseHarm * (BattleData.mod8 + attackRole.getFinalProp(Const.PROP_CRITICAL_MUL) / BattleData.mod9));
	        }
	        else
	        {
	            return baseHarm;
	        }
	    }
	
	    return baseHarm;
	}

	public int getSeries()
	{
		return spriteBox.battletype;
	}
	
	public int getSex()
	{
		return spriteBox.sex;
	}
	
	
	/**
	 * 计算基础伤害，普攻和技能公用
	 * @param attackRole
	 * @return
	 */
	private int calcBaseHarm(BattleRole attackRole)
	{
		int harm=0;	
		int defence = getFinalProp(Const.PROP_DEFENCE);
		
		if(attackRole.getSeries() == Const.SERIES_MAGIC || attackRole.getSeries() == Const.SERIES_HELP)
		{
            defence = getFinalProp(Const.PROP_MAGICDEF);
		}
		
		//被动技能改变防御
		defence = attackRole.getDefChangeByBeSkill(defence);
		if(defence<0)
        {
            defence=0;
        }
		
        harm = (int)((attackRole.getFinalProp(Const.PROP_ATTACK) * BattleConfig.atkFactor - defence * BattleConfig.defFactor) * BattleConfig.harmFactor);

        if(harm<=0)
        {
            harm = 1;
        }
        return harm;
	}
	//计算技能伤害
	private int calcBaseSkillHarm(BattleResult br,BattleRole attackRole,BattleSkill skill,int harm) 
	{
	    //加技能伤害
	    //初始伤害百分比,升级伤害百分比提升,初始伤害固定值,升级伤害固定值提升
	    //技能伤害计算公式=（物理/法术）攻击伤害*[初始伤害百分比+（技能等级-1）*升级伤害百分比提升]+初始伤害固定值+（技能等级-1）*升级伤害固定值提升
	    short[] harmArgs = skill.battleSkillFixData.harmArgs;
	    //int attackerHPChange=0;
	    int skillLevel = skill.level;
	    BattleSkillFixData skillData = skill.battleSkillFixData;

	    if(harmArgs !=null)
	    {
	        if(harmArgs[0] > 0 || harmArgs[1] > 0 || harmArgs[2] > 0 || harmArgs[3] > 0)
	        {
	            harm = harm * (harmArgs[0]+(skillLevel-1)*harmArgs[1])/100 + harmArgs[2] + (skillLevel-1)*harmArgs[3];
	            if(harm<=0)
	            {
	                harm = 1;
	            }
	        }
	        else
	        {
	            harm = 0;
	        }
	    } 

	    if(skillData.otherArgs!=null)
	    {
	        if(skillData.otherArgs[0]==1)   //吸血，参数：1,伤害的初始百分比，升级提升
	        {
	            int percent = skillData.otherArgs[1];
	            int levelUP = skillData.otherArgs[2];
	          //检查是否在不能加血的buff中
	            if(!attackRole.inBuffStatus(Const.BUFF_TYPE_UNCUREABLE))
	            {
	            	br.fromRoleHpChange += (int)Math.ceil(harm * (percent + levelUP * (skillLevel-1)) / 100);	            	
	            }	            
	        }
	        else if(skillData.otherArgs[0]==2)   //怒气变化,参数：2,（0,减少|1,增加）基础怒气值，升级提升       
	        {
	        	
	        }
	    }
	    
	    if(harm<0)
	    {
	        harm=0;
	    }

	    return harm;
	}
	//计算buff和被动技能伤害
	private int calcOtherHarm(BattleResult br,BattleRole attackRole,int harm)
	{
	    //buff改变伤害	   
	    harm = getChangeHarmByBuff(attackRole,harm);	   

	    //被动技能改变伤害  harmType:1,指向性伤害|2,DEBUFF伤害|3,物理伤害|4,法术伤害	    
	    int[] harmTypes = new int[]{1};
        if(isPhy())
        {
        	harmTypes = Tools.addToIntArr(harmTypes, Const.HARM_TYPE_PHY);            
        }
        else
        {
        	harmTypes = Tools.addToIntArr(harmTypes, Const.HARM_TYPE_MAGIC);
        }        

        harm = getChangeHarmByBeSkill(attackRole ,harm,harmTypes);
	   
      //反弹攻击者
        int bounceHarm = getBounceHarmByBeSkill(harm);
        if(bounceHarm > 0)
        {
        	int attackRoleHarm =0;
            if(bounceHarm >= attackRole.getFinalProp(Const.PROP_HP)) //给攻击者留一滴血
            {
            	attackRoleHarm = -(attackRole.getFinalProp(Const.PROP_HP)-1);
            	//attackRole.changeHP(attackRoleHarm);                
            }
            else
            {
            	attackRoleHarm = -bounceHarm;
            	//attackRole.changeHP(attackRoleHarm);                
            }             
            br.fromRoleHpChange += attackRoleHarm;            
        } 
        
	    //计算暴击和格挡
	    harm = calcCritical(br,attackRole,harm);

	    if(harm<0)
	    {
	        harm=0;
	    }

	    return harm;
	}
	/**
	 * 计算普攻伤害
	 * @param attackRole 施暴者
	 */
	public BattleResult calcHarm(BattleRole attackRole)
	{		
		BattleResult br = new BattleResult();
		boolean dodge = calcDodge(attackRole);
		if(dodge)
		{				
			br.isDodge=true;
			br.toRoleHpChange = 0;
		    return br;
		}
		
		int harm=0;		
		
		int hpStartPercent = getFinalProp(Const.PROP_HP) / getFinalProp(Const.PROP_MAXHP) * 100;    
		
		//第1步计算基础伤害
		harm = calcBaseHarm(attackRole);
		
		//第2步计算buff,被动，暴击，格挡等对伤害的改变		
		harm = calcOtherHarm(br,attackRole,harm);	
        
        harm = (int)Math.ceil(harm);
        
        //查攻击方是否有免加怒气技能
        boolean notAngry = attackRole.haveBeSkill(14);

        if(isLive() && !notAngry)  //被打者加怒
        {   
	    	br.angryValueChanged = true;
	    	br.angryValue = changeAngry((int)(BattleConfig.beHitAddAngry +  harm / this.getFinalProp(Const.PROP_MAXHP) * BattleData.mod21));		    
        }
        
        changeHP(-harm);

        if(isLive())
        {
            int hpEndPercent = getFinalProp(Const.PROP_HP) / getFinalProp(Const.PROP_MAXHP) * 100;
            addBuffByHarm(attackRole,hpStartPercent,hpEndPercent);
        }
		
		br.toRoleHpChange = -harm;
		return br;
	}
	

	/**
	 * 
	 * @param attackRole
	 */
	public BattleResult calcSkillHarm(BattleRole attackRole,boolean isAngry)
	{
		BattleResult br = new BattleResult();
		boolean dodge = calcDodge(attackRole);
		if(dodge)
		{				
			br.isDodge=true;
			br.toRoleHpChange = 0;
		    return br;
		}
		
		int hpStartPercent = getFinalProp(Const.PROP_HP) / getFinalProp(Const.PROP_MAXHP) * 100;
				
	    int harm=0;
	    
	    //第1步计算基础伤害
	    harm = calcBaseHarm(attackRole);
	    
	    //第2步计算技能对伤害的改变
	    harm = calcBaseSkillHarm(br,attackRole,attackRole.actor.battleSkill,harm);	   
	    
	    //第3步计算buff,被动，暴击，格挡等对伤害的改变
        harm = calcOtherHarm(br,attackRole,harm);	    
        
	    harm = (int)Math.ceil(harm);	
	    
	    br.toRoleHpChange = -harm;
	    
	    //查攻击方是否有免加怒气技能
        boolean notAngry = attackRole.haveBeSkill(14);

        if(isLive() && !notAngry)
        {   
	    	br.angryValueChanged = true;
	    	if(isAngry)
	    	{
	    		br.angryValue = changeAngry((int)(BattleConfig.beAngrySkillHitAddAngry + harm / this.getFinalProp(Const.PROP_MAXHP) * BattleData.mod21));
	    	}
	    	else
	    	{
	    		br.angryValue = changeAngry((int)(BattleConfig.beAutoSkillHitAddAngry+ harm / this.getFinalProp(Const.PROP_MAXHP) * BattleData.mod21));	    		
	    	}
            //加可能中的buff
		    hitBuff(br,attackRole);
        }

        changeHP(-harm);
        
        if(isLive())
        {
	        int hpEndPercent = getFinalProp(Const.PROP_HP) / getFinalProp(Const.PROP_MAXHP) * 100;
	        addBuffByHarm(attackRole,hpStartPercent,hpEndPercent);
        }
        
	    return br; 
	}

	public Buff bindBuff(BattleRole fromRole,int level,int buffNum)
	{
	    BuffFixData buffData = BuffLib.getBuffFixDataByNum(buffNum);
	    if(buffData==null)
	    {
	    	BattlePrint.print("未找到buff="+buffNum+"的数据");
	    }
	    //是异常状态buff查是否有免疫buff
	    if(buffData.goodbad == 2) //坏的buff
	    {
	        if(inBuffStatus(Const.BUFF_TYPE_ABNORMAL_SHELL))
	        {
	            Buff shellBuff = getBuffByType(Const.BUFF_TYPE_ABNORMAL_SHELL);
	            if(shellBuff!=null && shellBuff.shellAbnormal >0)
	            {	            	
	                shellBuff.shellAbnormal = (byte)(shellBuff.shellAbnormal -1);
	                BattlePrint.print(this.getIdName()+"免疫盾牌剩余次数="+shellBuff.shellAbnormal+",防范一次debuff");
	                if(shellBuff.shellAbnormal <=0)
	                {
	                	//记录到json给客户端
	                	fromRole.removeBuffNumArr.add(this.getId());
	                	fromRole.removeBuffNumArr.add(shellBuff.buffFixData.num);
	        	    	
	                    removeBuff(shellBuff);
	                }
	                return null;
	            }
	        }
	    }
	
	    if(buffData !=null)
	    {
	        if(buffData.buffTypes!=null)
	        {
	            Buff buffObj = BuffLib.createBuff(buffNum, level);
	
	            for(int i=0;i<buffData.buffTypes.length;i++)
	            {
	                int num = buffData.buffTypes[i][0];	                
	                    
	                if(num== Const.BUFF_TYPE_SHELL) //增加抵挡指定伤害的护盾
	                {
	                    buffObj.shellHP = buffData.args[i][1] + (level-1) * buffData.args[i][2];	                   
	                    
	                    //BattlePrint.print("盾牌血被赋值"+buffObj.shellHP);
	                }
	                else if(num== Const.BUFF_TYPE_ABNORMAL_SHELL) //增加免疫指定次数异常状态的护盾
	                {
	                    buffObj.shellAbnormal = (byte)(buffData.args[i][1] + (level-1) * buffData.args[i][2]);
	                }
	                else if(num== Const.BUFF_TYPE_CHANGEBODY)  //变身
	                {
	                    if(!isChangeBody)
	                    {
	                        isChangeBody=true;	                        
	                        BattlePrint.print(this.getIdName()+"被变身");
	                    }   
	                    buffObj.isChangeBuff=true;
	                }
	            }	           
	           
	            if(fromRole !=null)
	            {
	                buffObj.fromRoleAtk = (short)fromRole.getFinalProp(Const.PROP_ATTACK);
	            }
	            addBuff(fromRole,buffObj);
	            //BattlePrint.print(getName()+"("+getId()+")被加buff:"+buffData.name+"turns="+buffObj.turns);
	            return buffObj;
	        }
	    }
	    return null;
	}
	
	//获得被动技能改变对方的防御力 11.忽视对方防御力，参数：11,基础绝对值,升级提升绝对值
	public int getDefChangeByBeSkill(int defence)
	{
	    if(isLive())
	    {
	        for(int i=0;i<beSkills.size();i++)
	        {
	        	BattleSkill skill = beSkills.get(i);	            
	            int beSkillLevel = skill.level;
	            BattleSkillFixData beSkillData = skill.battleSkillFixData;
	            if(beSkillData.owner ==2)
	            {
	                for(int j=0;j<beSkillData.args.length;j++)
	                {
	                    short[] arg = beSkillData.args[j];
	                    short t = arg[0];

	                    if(t == Const.BESKILL_TYPE11) //11.忽视对方防御力，参数：11,基础绝对值,升级提升绝对值
	                    {	                     
	                        return defence * (100-(arg[1] + arg[2] * (beSkillLevel - 1)))/100;
	                    }	                    
	                }  
	            }       
	        }
	    }	    
	    return defence;
	}
	
	//生命值不足指定百分比时获得BUFF，参数：8,条件参数,BUFF编号
	public void addBuffByHarm(BattleRole attackRole,int hpStartPercent,int hpEndPercent)
	{
	    //被打者的被动技能对伤害的改变
	    for(int i=0;i<beSkills.size();i++)
	    {
	    	BattleSkill skill = beSkills.get(i);	        
	        int beSkillLevel = skill.level;
	        BattleSkillFixData beSkillData = skill.battleSkillFixData;
	        if(beSkillData.owner ==1)  //过滤出被打时触发的被动技能
	        {
	            for(int j=0;j<beSkillData.args.length;j++)
	            {
	                short[] arg = beSkillData.args[j];
	                short t = arg[0];

	                if(t == Const.BESKILL_TYPE8)  //8.生命值不足指定百分比时获得BUFF，参数：8,条件参数,BUFF编号
	                {
	                    if(hpStartPercent>= arg[1] && hpEndPercent <arg[1])
	                    {	
	                    	//BattlePrint.print(this.getName()+"被"+attackRole.getName()+"打加buff"+arg[2]);
	                    	bindBuff(attackRole, beSkillLevel, arg[2]);	                    	                   
	                    }                    
	                }
	            }  
	        }       
	    }
	}
	
	public boolean isPhy()
	{
	    if(getSeries() == Const.SERIES_NEAR || getSeries() == Const.SERIES_DEF || getSeries() == Const.SERIES_KILL)
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }	    
	}
	
	
	/**
	 * 根据概率计算中的buff
	 * @param br
	 * @param attackRole
	 */
	public void hitBuff(BattleResult br,BattleRole attackRole)
	{			
		short[] buffsNum = attackRole.actor.battleSkill.battleSkillFixData.buffs;
		int skillLevel = attackRole.actor.battleSkill.level;
		short[][] buffsRate = attackRole.actor.battleSkill.battleSkillFixData.buffsRate;
		
		
	    
	    
		for(int i=0;buffsNum!=null && i<buffsNum.length;i++)
		{			
			int rate = buffsRate[i][0] + (skillLevel-1)*buffsRate[i][1];
			int rnd = getRandomNumber(0, 100);
			if(rnd<=rate)
			{
				this.bindBuff(attackRole, skillLevel, buffsNum[i]);				
			}			
		}		
	}
	public Buff getBuffByType(int buffType)
	{
	    if(buffArr==null)
	    {
	        return null;
	    }
	
	    for(int i=0;i<buffArr.size();i++)
	    {
	        Buff buff = buffArr.get(i);
	
	        for(int j=0;j<buff.buffFixData.buffTypes.length;j++)
	        {
	            if(buff.buffFixData.buffTypes[j][0] == buffType)
	            {
	                return buff;
	            }
	        }
	    }
	    return null;
	}
	
	public BattleResult calcRecover(BattleRole fromRole)
	{
		BattleResult br = new BattleResult();
        BattleSkillFixData skillData = fromRole.actor.battleSkill.battleSkillFixData;
        int skillLevel = fromRole.actor.battleSkill.level;

        int recover = fromRole.getFinalProp(Const.PROP_ATTACK);

        //加技能伤害
        //初始伤害百分比,升级伤害百分比提升,初始伤害固定值,升级伤害固定值提升
        //技能伤害计算公式=（物理/法术）攻击伤害*[初始伤害百分比+（技能等级-1）*升级伤害百分比提升]+初始伤害固定值+（技能等级-1）*升级伤害固定值提升
        short[] harmArgs = skillData.harmArgs;

        if(harmArgs !=null)
        {
            if(harmArgs[0] > 0 || harmArgs[1] > 0 || harmArgs[2] > 0 || harmArgs[3] > 0)
        	{
                recover = (int)((recover * (harmArgs[0]+(skillLevel-1)*harmArgs[1])/100 + harmArgs[2] + (skillLevel-1)*harmArgs[3]) * BattleData.mod20);
                if(recover<=0)
                {
                    recover = 1;
                }        
        	}
        } 

        recover = (int)Math.ceil(recover);

        changeHP(recover);
        br.toRoleHpChange = recover;
        
        return br;
	}    
	public String getIdName()
	{
		return this.getId() +"."+spriteBox.name;
	}
	
	public String getName()
	{
		return spriteBox.name;
	}
	
	public int getRandomNumber(int start, int end) 
    {
    	if(start==end || end - start + 1==0)    	
    	{
    		return start;
    	}
	    int rnd = (Math.abs(ran.nextInt()) % (end - start + 1)) + start;
	    return rnd;
	}
	public double getRandomFloatNumber(float start, float end) 
    {
    	if(start==end)    	
    	{
    		return start;
    	}
    	double r = start + ran.nextDouble() * (end - start);	    
	    return r;
	}
	
	private static int[] getIdsByTargetRoles(Vector<BattleRole> targetVC)
	{
		int[] ids = null;
		for(int i=0;targetVC!=null && i<targetVC.size();i++)
		{
			BattleRole role = targetVC.get(i);
			ids = Tools.addToIntArr(ids, role.getId());
		}
		return ids;
	}
	

	//获得反弹伤害值
	public int getBounceHarmByBeSkill(int harm)
	{
	    if(getFinalProp(Const.PROP_HP) > 0)
	    {
	        for(int i=0;i<beSkills.size();i++)
	        {
	        	BattleSkill skill = beSkills.get(i);	            
	            int beSkillLevel = skill.level;
	            BattleSkillFixData beSkillData = skill.battleSkillFixData;
	            if(beSkillData.owner ==1)
	            {
	                for(int j=0;j<beSkillData.args.length;j++)
	                {
	                    short[] arg = beSkillData.args[j];
	                    short t = arg[0];
	                    if(t == Const.BESKILL_TYPE17)  //17.反弹受到的伤害给攻击方，反弹伤害不会造成对方死亡，反弹伤害不会超过自身生命上限，参数：17,反弹伤害百分比,升级提升百分比（单位0.1）
	                    {
	                        return (int)(harm * (arg[1] + arg[2]* 0.1 * (beSkillLevel - 1))/100);
	                    }	                    
	                }  
	            }       
	        }
	    }
	    return 0;
	}
	
	private static JSONArray getJsonArrByIntArr(int[] arr)
	{
		try {
			JSONArray jsonArr = new JSONArray(arr);
			return jsonArr;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	//查是否有被动技能
	public boolean haveBeSkill(int typenum)
	{
	    if(getFinalProp(Const.PROP_HP) > 0)
	    {
	        for(int i=0;i<beSkills.size();i++)
	        {
	        	BattleSkill skill = beSkills.get(i);	            
	            int beSkillLevel = skill.level;
	            BattleSkillFixData beSkillData = skill.battleSkillFixData;
	           
	            for(int j=0;j<beSkillData.args.length;j++)
	            {
	                short[] arg = beSkillData.args[j];
	                short t = arg[0];
	                if(t == typenum)
	                {
	                    return true;
	                }                    
	            }    
	        }
	    }

	    return false;
	}
	
	public boolean inBuffStatus(int buffType)
	{
	    if(buffArr==null)
	    {
	        return false;
	    }
	
	    for(int i=0;i<buffArr.size();i++)
	    {
	        Buff buff = buffArr.get(i);
	
	        for(int j=0;j<buff.buffFixData.buffTypes.length;j++)
	        {
	            if(buff.buffFixData.buffTypes[j][0] == buffType)
	            {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	/**
	 *执行战斗逻辑 
	 */
	public JSONWrap doBattle()
	{	 	   
        if(isLive())
        {            
            if(actor!=null)
            {               	
            	if(actor.cmdType == Command.CMD_NONE)
            	{
            		//todo用AI出指令
            		
                    //A方用测试指令
                    if(getTeamType() == Const.teamA)
                    {
                        actor.cmdType = Command.CMD_ATTACK;
                    }
                    else
                    {
                    	actor.cmdType = Command.CMD_ATTACK;
                    }
            	}

            
                if(actor.cmdType == Command.CMD_WAIT || actor.cmdType == Command.CMD_NONE)
                {                   
                    return null;
                }
              
                
                //眩晕跳过
                if(inBuffStatus(Const.BUFF_TYPE_DIZZY))
                {
                	return null;
                } 
                //变身跳过
                if(isChangeBody)
                {
                	return null;
                }

                //检查有无技能
                if(actor.cmdType == Command.CMD_AUTOSKILL)
                {
                    if(autoSkills.size()==0)   
                    {
                        actor.cmdType = Command.CMD_ATTACK;
                	}
                }

                if(actor.cmdType == Command.CMD_ANGRYSKILL)
                {
                    if(angrySkills.size() == 0)
                    {
                       actor.cmdType = Command.CMD_ATTACK;
                    }
                }   
                
                if(inBuffStatus(Const.BUFF_TYPE_SILENCE))  //沉默
                {
                	actor.cmdType = Command.CMD_ATTACK;
                }

                if(actor.cmdType == Command.CMD_ATTACK)  //普攻
                {
                    //寻找目标       
                    searchTarget(Const.TARGET_ENEMY,Const.RANGE_FRONT_SINGLE);
                    
                    if(targetRoles!=null && targetRoles.size()>0)
                    {                    	
                    	JSONWrap jsonWrap = new JSONWrap();
                        
                        jsonWrap.put(JSONWrap.KEY.ID, getId());
                        jsonWrap.put(JSONWrap.KEY.CMD, actor.cmdType);
                        
                    	//BattlePrint.print(this.getName()+"使用普攻");
                    	jsonWrap.put(JSONWrap.KEY.TARGET_IDS, getJsonArrByIntArr(getIdsByTargetRoles(targetRoles)));
                    	//todo 计算普攻伤害
                    	int[] hpChanges=null;
                    	int[] dodgeIds=null;
                    	int[] criticalIds = null;
                    	int[] blockIds = null;
                    	int[] angryChangeData=null;
                    	int totalHarm=0;
                    	int killAmount=0;
                    	int fromRoleHpChange=0;
                    	
                    	for(int i=0;i<targetRoles.size();i++)
                    	{
                    		BattleResult br = targetRoles.get(i).calcHarm(this);
                    		totalHarm += br.toRoleHpChange;
                    		if(br.toRoleHpChange!=0)BattlePrint.print(targetRoles.get(i).getIdName()+"被"+this.getIdName()+"普攻打掉"+br.toRoleHpChange+"血,剩余血="+targetRoles.get(i).getFinalProp(Const.PROP_HP));
                    		if(!targetRoles.get(i).isLive())  //打死
                    		{
                    			killAmount++;	
                    		}
                    		
                    		hpChanges = Tools.addToIntArr(hpChanges, br.toRoleHpChange);
                    		fromRoleHpChange += br.fromRoleHpChange;
                    		
                    		if(br.fromRoleHpChange!=0)BattlePrint.print(this.getIdName()+"血改变"+br.fromRoleHpChange+",剩余血="+this.getFinalProp(Const.PROP_HP));
                    		
                    		if(br.isBlock)
                    		{
                    			//BattlePrint.print(targetRoles.get(i).getName()+"格挡成功");
                    			blockIds = Tools.addToIntArr(blockIds, targetRoles.get(i).getId());
                    		}
                    		if(br.isCriticalAtk)
                    		{
                    			//BattlePrint.print(targetRoles.get(i).getName()+"暴击成功");
                    			criticalIds = Tools.addToIntArr(criticalIds, targetRoles.get(i).getId());
                    		}
                    		if(br.isDodge)
                    		{
                    			//BattlePrint.print(targetRoles.get(i).getName()+"闪避成功");
                    			dodgeIds = Tools.addToIntArr(dodgeIds, targetRoles.get(i).getId());
                    		}
                    		if(br.angryValueChanged)  //被打者加怒
                    		{
                    			angryChangeData = Tools.addToIntArr(angryChangeData, targetRoles.get(i).getId());
                    			angryChangeData = Tools.addToIntArr(angryChangeData, br.angryValue);
                    		}
                    	}
                    	if(totalHarm != 0) //打人者加怒
                		{
                    		int angry = this.changeAngry(BattleConfig.hitAddAngry + killAmount * 20);       
                    		angryChangeData = Tools.addToIntArr(angryChangeData, this.getId());
                			angryChangeData = Tools.addToIntArr(angryChangeData, angry);
                		}
                            
                    	jsonWrap.put(JSONWrap.KEY.TARGET_HP_CHANGE, hpChanges);
                    	
                    	
                    	if(fromRoleHpChange!=0)
	                	{
                    		BattlePrint.print(this.getIdName()+"被反伤"+fromRoleHpChange);
                    		changeHP(fromRoleHpChange);
	                		jsonWrap.put(JSONWrap.KEY.SOURCE_HP_CHANGE,fromRoleHpChange);
	                	}
                    	if(blockIds!=null)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.BLOCK, blockIds);
                    	}
                    	if(criticalIds!=null)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.CRITICAL, criticalIds);
                    	}
                    	if(dodgeIds!=null)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.DODGE, dodgeIds);
                    	}
                    	if(angryChangeData !=null)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.ANGRY, angryChangeData);
                    	}                      	
                    	if(addBuffNumArr.length()>0)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.ADD_BUFF, addBuffNumArr);
                    	}
                    	if(removeBuffNumArr.length()>0)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.REMOVE_BUFF, removeBuffNumArr);
                    	}
                    	if(blueBloodArr.length()>0)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.BLUE_BLOOD, blueBloodArr);
                    	}                    	
                    	return jsonWrap;
                    }
                }             
                else if(actor.cmdType == Command.CMD_AUTOSKILL || actor.cmdType == Command.CMD_ANGRYSKILL)  //随机技能或怒技能
                {     
                	int rangeType = actor.battleSkill.battleSkillFixData.range;
                    searchTarget(actor.battleSkill.battleSkillFixData.targetType,rangeType );  
                    
                    if(targetRoles!=null && targetRoles.size()>0)
	                {	                
	                	JSONWrap jsonWrap = new JSONWrap();
	                    
	                    jsonWrap.put(JSONWrap.KEY.ID, getId());
	                    jsonWrap.put(JSONWrap.KEY.CMD, actor.cmdType);
	                    
	                	int[] hpChanges=null;
	                	int[] angryChangeData=null;
	                	int[] dodgeIds=null;
                    	int[] criticalIds = null;
                    	int[] blockIds = null;
                    	//JSONArray addBuffs = null;
                    	int fromRoleHpChange=0;
                    	int killAmount=0;
                    	
	                	if(actor.cmdType == Command.CMD_ANGRYSKILL)
	                	{
	                		BattlePrint.print(this.getName()+"使用怒技能"+actor.battleSkill.battleSkillFixData.num+"."+actor.battleSkill.battleSkillFixData.name);
	                		//怒气清0
	                		waitUseAngry=false;
	                		setAngry(0);
	                		angryChangeData = Tools.addToIntArr(angryChangeData, this.getId());
                			angryChangeData = Tools.addToIntArr(angryChangeData, 0);
	                	}
	                	else
	                	{
	                		BattlePrint.print(this.getName()+"使用随机技能"+actor.battleSkill.battleSkillFixData.num+"."+actor.battleSkill.battleSkillFixData.name);
	                	}
	                	jsonWrap.put(JSONWrap.KEY.SKILL_NUM,actor.battleSkill.battleSkillFixData.num);
	                	
	                	
	                	BattleResult br =null;
	                	if(rangeType==Const.RANGE_SPLIT)  //连击特殊处理
	                    {	                		
	                		int[] targetIds = null;
	                		int harmIndex=0; //目标指针	                		
	                    	int hitTimes = actor.battleSkill.battleSkillFixData.maxUseTimes;  //连击打击次数
	                    	for(int i=0;i<hitTimes;i++)
	                    	{
	                    		int fromIndex = harmIndex;  //本次找的起点
	                    		while(true)  //找一个活的目标攻击
	                    		{
	                    			if(targetRoles.get(harmIndex).isLive())
		                    		{
		                    			br = targetRoles.get(harmIndex).calcSkillHarm(this,actor.cmdType == Command.CMD_ANGRYSKILL);
		                    			targetIds = Tools.addToIntArr(targetIds, targetRoles.get(harmIndex).getId());
		                    			if(!targetRoles.get(harmIndex).isLive())
			                			{
			                				killAmount++;
			                			}
		                    			BattlePrint.print(this.getName()+"对"+targetRoles.get(harmIndex).getName()+"第"+(i+1)+"次连击造成伤害"+br.toRoleHpChange+",剩余血="+targetRoles.get(harmIndex).getFinalProp(Const.PROP_HP));
		                    			hpChanges = Tools.addToIntArr(hpChanges, br.toRoleHpChange);
				                		fromRoleHpChange += br.fromRoleHpChange;
				                		if(br.isBlock)
			                    		{
			                    			//BattlePrint.print(targetRoles.get(i).getName()+"格挡成功");
			                    			blockIds = Tools.addToIntArr(blockIds, targetRoles.get(harmIndex).getId());
			                    		}
			                    		if(br.isCriticalAtk)
			                    		{
			                    			//BattlePrint.print(targetRoles.get(i).getName()+"暴击成功");
			                    			criticalIds = Tools.addToIntArr(criticalIds, targetRoles.get(harmIndex).getId());
			                    		}
			                    		if(br.isDodge)
			                    		{
			                    			//BattlePrint.print(targetRoles.get(i).getName()+"闪避成功");
			                    			dodgeIds = Tools.addToIntArr(dodgeIds, targetRoles.get(harmIndex).getId());
			                    		}
			                    		if(br.angryValueChanged) //被打者加怒
			                    		{
			                    			angryChangeData = Tools.addToIntArr(angryChangeData, targetRoles.get(harmIndex).getId());
			                    			angryChangeData = Tools.addToIntArr(angryChangeData, br.angryValue);
			                    		}
		                    			
		                    			harmIndex = harmIndex +1;
		                                if(harmIndex >= targetRoles.size())
		                                {
		                                    harmIndex =0;
		                                }
		                                break;
		                    		}
		                    		else
		                    		{
			                    		harmIndex = harmIndex +1;  //找下一个
			                            if(harmIndex >= targetRoles.size())
			                            {
			                                harmIndex =0;
			                            }
			                            if(harmIndex == fromIndex)  //找了一轮不到活的
			                            {
			                                break;
			                            }
		                    		}	
	                    		}	                    		
	                    	}
	                    	jsonWrap.put(JSONWrap.KEY.TARGET_IDS, targetIds);
	                    }
	                    else  //普通技能打击
	                    {
	                    	jsonWrap.put(JSONWrap.KEY.TARGET_IDS, getIdsByTargetRoles(targetRoles));
	                    	for(int i=0;i<targetRoles.size();i++)
	                    	{	                		
		                		if(actor.battleSkill.battleSkillFixData.type == Const.SKILLTYPE_HARM)
		                		{
		                			br = targetRoles.get(i).calcSkillHarm(this,actor.cmdType == Command.CMD_ANGRYSKILL);
		                			if(actor.cmdType == Command.CMD_ANGRYSKILL)
		                			{
		                				if(br.toRoleHpChange!=0)BattlePrint.print(targetRoles.get(i).getIdName()+"被"+this.getIdName()+"怒技能打掉"+br.toRoleHpChange+"血,剩余血="+targetRoles.get(i).getFinalProp(Const.PROP_HP));	
		                			}
		                			else
		                			{
		                				if(br.toRoleHpChange!=0)BattlePrint.print(targetRoles.get(i).getIdName()+"被"+this.getIdName()+"随机技能打掉"+br.toRoleHpChange+"血,剩余血="+targetRoles.get(i).getFinalProp(Const.PROP_HP));
		                			}
		                    		
		                			if(!targetRoles.get(i).isLive())
		                			{
		                				killAmount++;
		                			}
		                		}
		                		else if(actor.battleSkill.battleSkillFixData.type == Const.SKILLTYPE_RECOVER)
		                		{
		                			br = targetRoles.get(i).calcRecover(this);
		                		}
		                		else if(actor.battleSkill.battleSkillFixData.type == Const.SKILLTYPE_BUFF)
		                		{
		                			br = new BattleResult();
		                			targetRoles.get(i).hitBuff(br,this);
		                		}
		                		hpChanges = Tools.addToIntArr(hpChanges, br.toRoleHpChange);
		                		fromRoleHpChange += br.fromRoleHpChange;
		                		
		                		if(br.isBlock)
	                    		{
	                    			//BattlePrint.print(targetRoles.get(i).getName()+"格挡成功");
	                    			blockIds = Tools.addToIntArr(blockIds, targetRoles.get(i).getId());
	                    		}
	                    		if(br.isCriticalAtk)
	                    		{
	                    			//BattlePrint.print(targetRoles.get(i).getName()+"暴击成功");
	                    			criticalIds = Tools.addToIntArr(criticalIds, targetRoles.get(i).getId());
	                    		}
	                    		if(br.isDodge)
	                    		{
	                    			//BattlePrint.print(targetRoles.get(i).getName()+"闪避成功");
	                    			dodgeIds = Tools.addToIntArr(dodgeIds, targetRoles.get(i).getId());
	                    		}
	                    		if(br.angryValueChanged) //被打者加怒
	                    		{
	                    			angryChangeData = Tools.addToIntArr(angryChangeData, targetRoles.get(i).getId());
	                    			angryChangeData = Tools.addToIntArr(angryChangeData, br.angryValue);
	                    		}
	                    		/*short[] buffIds = br.getBuffIds();
	                    		if(buffIds !=null)
	                    		{
	                    			addBuffs = new JSONArray();
	                    			addBuffs.add(targetRoles.get(i).getId());
	                    			addBuffs.add(buffIds);
	                    		}*/
	                    	}
	                    }
	                	
	                	//加自身buff
	                    if(actor.battleSkill.battleSkillFixData.selfBuff > 0)
	                    {
	                        bindBuff(this,actor.battleSkill.level,actor.battleSkill.battleSkillFixData.selfBuff); 
	                    }
	                	
	                	if(actor.cmdType != Command.CMD_ANGRYSKILL)  //打人者加怒
	                	{
	                		//if(totalHarm != 0)
	                		{
	                    		int angry = this.changeAngry(BattleConfig.autoSkillAddAngry + killAmount * 20);       
	                    		angryChangeData = Tools.addToIntArr(angryChangeData, this.getId());
	                			angryChangeData = Tools.addToIntArr(angryChangeData, angry);
	                		}
	                	}	                	
	                	else  //怒技打死人也加怒
	                	{
	                		int angry = this.changeAngry(killAmount * 20);       
                    		angryChangeData = Tools.addToIntArr(angryChangeData, this.getId());
                			angryChangeData = Tools.addToIntArr(angryChangeData, angry);
	                	}
	                	
	                	jsonWrap.put(JSONWrap.KEY.TARGET_HP_CHANGE, hpChanges);
	                	if(fromRoleHpChange!=0)
	                	{
	                		BattlePrint.print(this.getIdName()+"被反伤"+fromRoleHpChange);
	                		changeHP(fromRoleHpChange);
	                		jsonWrap.put(JSONWrap.KEY.SOURCE_HP_CHANGE,fromRoleHpChange);
	                	}
	                	if(blockIds!=null)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.BLOCK, blockIds);
                    	}
                    	if(criticalIds!=null)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.CRITICAL, criticalIds);
                    	}
                    	if(dodgeIds!=null)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.DODGE, dodgeIds);
                    	}
                    	if(angryChangeData !=null)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.ANGRY, angryChangeData);
                    	}
                    	
                    	if(addBuffNumArr.length()>0)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.ADD_BUFF, addBuffNumArr);
                    	}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
                    	if(removeBuffNumArr.length()>0)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
                    	{
                    		jsonWrap.put(JSONWrap.KEY.REMOVE_BUFF, removeBuffNumArr);
                    	}
                    	if(blueBloodArr.length()>0)
                    	{
                    		jsonWrap.put(JSONWrap.KEY.BLUE_BLOOD, blueBloodArr);
                    	}
                    	return jsonWrap;
	                }                                    
                }                
                return null;
        	}
        }        
        return null;        
	}
}
