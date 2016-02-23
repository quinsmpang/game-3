package com.moonic.battle;


import java.util.ArrayList;
import com.moonic.util.DBPool;
import com.moonic.util.DBPoolClearListener;

import server.common.Tools;


public class BattleSkillLib 
{
	static
	{
		readAutoSkillData();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {
			@Override
			public void callback(String key) {
				if(key.equals("skill_auto")){
					readAutoSkillData();
				}
			}
		});
		readAngrySkillData();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {
			@Override
			public void callback(String key) {
				if(key.equals("skill_anger")){
					readAngrySkillData();
				}
			}
		});
		readBeSkillData();
		DBPool.getInst().addTxtClearListener(new DBPoolClearListener() {			
			public void callback(String key) {
				if(key.equals("skill_be")){
					readBeSkillData();
				}
			}
		});
	}
	public static ArrayList<BattleSkillFixData> autoSkillFixDataVC;
	public static ArrayList<BattleSkillFixData> angrySkillFixDataVC;
	public static ArrayList<BattleSkillFixData> beSkillFixDataVC;
	
	
	//随机字段 ：1编号	2技能名	3图标编码	4最大等级	5对自身BUFF编号	6触发几率	7施放时机	8技能类型	9对敌对友	10范围	11范围参数	12最大攻击次数	13战力	14计算参数	15暴击增幅	16BUFF编号	17BUFF几率	18其他参数	19其他触发概率	20命中率	21技能特效CFG编号	22延迟	23是否物理攻击	24飘字、出命中特效时间差（秒）	25目标身上特效编号	26人物坐标偏移	27命中音效	28动作形式	29动作名
	
	public enum AUTO_DATA_INDEX //随机技能数据字段列
    {
        NUM, //1编号
        NAME, //2技能名
        ICON_PATH, //3图标编码
        MAXLV, //4最大等级
        SELFBUFF, //5对自身BUFF编号
        RATE,  //6触发几率
        USE_TURN, //7施放时机
        TYPE, //8技能类型 1:伤害,2:恢复,3:BUFF
        TARGET_TYPE, //9对敌对友1:对友 2:对敌
        RANGE,  //10攻击范围
        RANGE_ARGS, //11范围参数
        MAXUSETIMES, //12最大使用参数
        BATTLE_POWER, //13战力
        HARM_ARGS, //14计算参数
        CRITICAL_ADD, //15暴击增幅
        BUFF, //16BUFF编号
        BUFF_RATE, //17BUFF几率
        OTHER_ARGS, //18其他参数
        OTHER_RATE, //19其他触发概率
        HIT_RATE, //20命中率
    };
    
  //怒字段：  1编号	2技能名	3图标编码	4最大等级	5怒气保留	6对自身BUFF编号	7技能类型	8对敌对友	9范围	10范围参数	11最大攻击次数	12战力	13计算参数	14暴击增幅	15BUFF编号	16BUFF几率	17其他参数	18其他触发概率	19驱散数量	20命中率	21技能特效CFG编号	22延迟	23是否物理攻击	24飘字、出命中特效时间差（秒）	25目标身上特效编号	26人物坐标偏移	27命中音效	28动作形式
	
    public enum ANGRY_DATA_INDEX //怒技能数据字段列
    {
        NUM, //1编号
        NAME, //2技能名
        ICON_PATH, //3图标编码
        MAXLV, //4最大等级
        POWER_LEFT, //5怒气保留
        SELFBUFF, //6对自身BUFF编号                
        TYPE, //7技能类型 1:伤害,2:恢复,3:BUFF
        TARGET_TYPE, //8对敌对友1:对友 2:对敌
        RANGE,  //9攻击范围
        RANGE_ARGS, //10范围参数
        MAXUSETIMES, //11最大使用参数
        BATTLE_POWER, //12战力
        HARM_ARGS, //13计算参数
        CRITICAL_ADD, //14暴击增幅
        BUFF, //15BUFF编号
        BUFF_RATE, //16BUFF几率
        OTHER_ARGS, //17其他参数
        OTHER_RATE, //18他触发概率
        CLEAR_AMOUNT, //19驱散数量
        HIT_RATE, //20命中率
    };
    
    public enum BESKILL_DATA_INDEX //被动技能数据字段列
    {
        NUM, //1编号
        NAME, //2技能名
        ICON,
        ARGS, // 效果参数       
        POWER, //战力
        OWNER  //判定对象
    }
    
    public static void readBeSkillData()
    {		
		beSkillFixDataVC = new ArrayList<BattleSkillFixData>();

        //byte[] fileBytes = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/res/battleskill.txt");      
        String battleSkillTxt=null;
		try {
			battleSkillTxt = DBPool.getInst().readTxtFromPool("skill_be");
		} catch (Exception e) {		
			e.printStackTrace();
			return;
		}
		if(battleSkillTxt==null)
		{
			System.out.println("tab_txt中skill_be不存在！");	
		}
		
        String[][] battleSkillData = Tools.getStrLineArrEx2(battleSkillTxt, "data:","dataEnd");  //随机技能二维数据       
     
        for (int i = 0; battleSkillData != null && i < battleSkillData.length; i++)
        {
        	BattleSkillFixData data = new BattleSkillFixData();
        	data.skillCategory = Const.SKILLCATEGORY_BE;
        	data.num = Tools.str2int(battleSkillData[i][AUTO_DATA_INDEX.NUM.ordinal()]);
        	data.name = battleSkillData[i][AUTO_DATA_INDEX.NAME.ordinal()];
        	data.args = Tools.splitStrToShortArr2(battleSkillData[i][(int)BESKILL_DATA_INDEX.ARGS.ordinal()],"|",",");
        	data.owner = Tools.str2byte(battleSkillData[i][(int)BESKILL_DATA_INDEX.OWNER.ordinal()]);        	
        	beSkillFixDataVC.add(data);
        }
    }
	
	public static void readAutoSkillData()
    {		
		autoSkillFixDataVC = new ArrayList<BattleSkillFixData>();

        //byte[] fileBytes = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/res/battleskill.txt");      
        String battleSkillTxt=null;
		try {
			battleSkillTxt = DBPool.getInst().readTxtFromPool("skill_auto");
		} catch (Exception e) {		
			e.printStackTrace();
			return;
		}
		if(battleSkillTxt==null)
		{
			System.out.println("tab_txt中skill_auto不存在！");	
		}
		//System.out.println("battleSkillTxt="+battleSkillTxt);
		
        String[][] battleSkillData = Tools.getStrLineArrEx2(battleSkillTxt, "data:","dataEnd");  //随机技能二维数据
        //System.out.println("battleSkillData ="+Tools.strArr2Str2(battleSkillData));
        /*NUM, //1编号
        NAME, //2技能名
        ICON_PATH, //3图标编码
        MAXLV, //4最大等级
        SELFBUFF, //5对自身BUFF编号
        RATE,  //6触发几率
        RELEASE, //7施放时机
        TYPE, //8技能类型 1:伤害,2:恢复,3:BUFF
        TARGET_TYPE, //9对敌对友1:对友 2:对敌
        RANGE,  //10攻击范围
        RANGE_ARGS, //11范围参数
        MAXUSETIMES, //12最大使用次数
        BATTLE_POWER, //13战力
        CALC_ARGS, //14计算参数
        CRITICAL_ADD, //15暴击增幅
        BUFF, //16BUFF编号
        BUFF_RATE, //17BUFF几率
        OTHER_ARGS, //18其他参数
        OTHER_RATE, //19其他触发概率
        HIT_RATE, //20命中率
*/        
       
        //battleSkillFixDataArr = new BattleSkillFixData[battleSkillData.length];
        for (int i = 0; battleSkillData != null && i < battleSkillData.length; i++)
        {
        	BattleSkillFixData data = new BattleSkillFixData();
        	data.skillCategory = Const.SKILLCATEGORY_AUTO;
        	data.num = Tools.str2int(battleSkillData[i][AUTO_DATA_INDEX.NUM.ordinal()]);
        	data.name = battleSkillData[i][AUTO_DATA_INDEX.NAME.ordinal()];
        	data.maxLv = Tools.str2byte(battleSkillData[i][(int)AUTO_DATA_INDEX.MAXLV.ordinal()]);
        	data.selfBuff = Tools.str2short(battleSkillData[i][(int)AUTO_DATA_INDEX.SELFBUFF.ordinal()]);
        	data.rate = Tools.str2byte(battleSkillData[i][(int)AUTO_DATA_INDEX.RATE.ordinal()]);
        	data.useTurn = Tools.splitStrToShortArr(battleSkillData[i][(int)AUTO_DATA_INDEX.USE_TURN.ordinal()],",",true);
        	data.type = Tools.str2byte(battleSkillData[i][(int)AUTO_DATA_INDEX.TYPE.ordinal()]);
        	data.targetType = Tools.str2byte(battleSkillData[i][(int)AUTO_DATA_INDEX.TARGET_TYPE.ordinal()]); 
        	data.range = Tools.str2byte(battleSkillData[i][(int)AUTO_DATA_INDEX.RANGE.ordinal()]);        
        	data.maxUseTimes = Tools.str2byte(battleSkillData[i][(int)AUTO_DATA_INDEX.MAXUSETIMES.ordinal()]);   
        	data.harmArgs = Tools.splitStrToShortArr(battleSkillData[i][(int)AUTO_DATA_INDEX.HARM_ARGS.ordinal()], ",",true);
        	data.criticalAdd = Tools.str2short(battleSkillData[i][(int)AUTO_DATA_INDEX.CRITICAL_ADD.ordinal()]);
        	data.buffs = Tools.splitStrToShortArr(battleSkillData[i][(int)AUTO_DATA_INDEX.BUFF.ordinal()],",",true);
        	data.buffsRate = Tools.splitStrToShortArr2(battleSkillData[i][(int)AUTO_DATA_INDEX.BUFF_RATE.ordinal()],"|",",");
        	data.otherArgs = Tools.splitStrToShortArr(battleSkillData[i][(int)AUTO_DATA_INDEX.OTHER_ARGS.ordinal()],",",true);
        	data.otherRate = Tools.str2byte(battleSkillData[i][(int)AUTO_DATA_INDEX.OTHER_RATE.ordinal()]);
        	data.hitRate = Tools.str2byte(battleSkillData[i][(int)AUTO_DATA_INDEX.HIT_RATE.ordinal()]);
        	data.rangeArgs = Tools.splitStrToShortArr(battleSkillData[i][(int)AUTO_DATA_INDEX.RANGE_ARGS.ordinal()],",",true);
        	autoSkillFixDataVC.add(data);
        }
    }
	
	public static void readAngrySkillData()
    {		
		angrySkillFixDataVC = new ArrayList<BattleSkillFixData>();

        //byte[] fileBytes = Tools.getBytesFromFile(ServerConfig.getAppRootPath()+"WEB-INF/res/battleskill.txt");      
        String battleSkillTxt=null;
		try {
			battleSkillTxt = DBPool.getInst().readTxtFromPool("skill_anger");
		} catch (Exception e) {		
			e.printStackTrace();
			return;
		}
		if(battleSkillTxt==null)
		{
			System.out.println("tab_txt中skill_anger不存在！");	
		}
		//System.out.println("battleSkillTxt="+battleSkillTxt);
		
        String[][] battleSkillData = Tools.getStrLineArrEx2(battleSkillTxt, "data:","dataEnd");  //随机技能二维数据
        //System.out.println("battleSkillData ="+Tools.strArr2Str2(battleSkillData));
        /*NUM, //1编号
        NAME, //2技能名
        ICON_PATH, //3图标编码
        MAXLV, //4最大等级
        POWER_LEFT, //5怒气保留
        SELFBUFF, //6对自身BUFF编号                
        TYPE, //7技能类型 1:伤害,2:恢复,3:BUFF
        TARGET_TYPE, //8对敌对友1:对友 2:对敌
        RANGE,  //9攻击范围
        RANGE_ARGS, //10范围参数
        MAXUSETIMES, //11最大使用参数
        BATTLE_POWER, //12战力
        CALC_ARGS, //13计算参数
        CRITICAL_ADD, //14暴击增幅
        BUFF, //15BUFF编号
        BUFF_RATE, //16BUFF几率
        OTHER_ARGS, //17其他参数
        OTHER_RATE, //18他触发概率
        CLEAR_AMOUNT, //19驱散数量
        HIT_RATE, //20命中率
*/        
       
        //battleSkillFixDataArr = new BattleSkillFixData[battleSkillData.length];
        for (int i = 0; battleSkillData != null && i < battleSkillData.length; i++)
        {
        	BattleSkillFixData data = new BattleSkillFixData();
        	data.skillCategory = Const.SKILLCATEGORY_ANGRY;
        	data.num = Tools.str2int(battleSkillData[i][ANGRY_DATA_INDEX.NUM.ordinal()]);
        	data.name = battleSkillData[i][ANGRY_DATA_INDEX.NAME.ordinal()];
        	data.maxLv = Tools.str2byte(battleSkillData[i][(int)ANGRY_DATA_INDEX.MAXLV.ordinal()]);
        	data.powerLeft = Tools.str2byte(battleSkillData[i][(int)ANGRY_DATA_INDEX.MAXLV.ordinal()]);        	
        	data.selfBuff = Tools.str2short(battleSkillData[i][(int)ANGRY_DATA_INDEX.SELFBUFF.ordinal()]);
        	data.type = Tools.str2byte(battleSkillData[i][(int)ANGRY_DATA_INDEX.TYPE.ordinal()]);
        	data.targetType = Tools.str2byte(battleSkillData[i][(int)ANGRY_DATA_INDEX.TARGET_TYPE.ordinal()]); 
        	data.range = Tools.str2byte(battleSkillData[i][(int)ANGRY_DATA_INDEX.RANGE.ordinal()]);        
        	data.maxUseTimes = Tools.str2byte(battleSkillData[i][(int)ANGRY_DATA_INDEX.MAXUSETIMES.ordinal()]);   
        	data.harmArgs = Tools.splitStrToShortArr(battleSkillData[i][(int)ANGRY_DATA_INDEX.HARM_ARGS.ordinal()], ",",true);
        	data.criticalAdd = Tools.str2short(battleSkillData[i][(int)ANGRY_DATA_INDEX.CRITICAL_ADD.ordinal()]);
        	data.buffs = Tools.splitStrToShortArr(battleSkillData[i][(int)ANGRY_DATA_INDEX.BUFF.ordinal()],",",true);
        	data.buffsRate = Tools.splitStrToShortArr2(battleSkillData[i][(int)ANGRY_DATA_INDEX.BUFF_RATE.ordinal()],"|",",");
        	data.otherArgs = Tools.splitStrToShortArr(battleSkillData[i][(int)ANGRY_DATA_INDEX.OTHER_ARGS.ordinal()],",",true);
        	data.otherRate = Tools.str2byte(battleSkillData[i][(int)ANGRY_DATA_INDEX.OTHER_RATE.ordinal()]);
        	data.clearAmount = Tools.str2byte(battleSkillData[i][(int)ANGRY_DATA_INDEX.CLEAR_AMOUNT.ordinal()]);
        	data.hitRate = Tools.str2byte(battleSkillData[i][(int)ANGRY_DATA_INDEX.HIT_RATE.ordinal()]);
        	data.rangeArgs = Tools.splitStrToShortArr(battleSkillData[i][(int)ANGRY_DATA_INDEX.RANGE_ARGS.ordinal()],",",true);
        	angrySkillFixDataVC.add(data);
        }
    }
	
    public static BattleSkillFixData getSkillDataByNum(int num)
    {
    	if(num>=2001)  //随机技能
    	{
    		for (int i = 0; autoSkillFixDataVC != null && i < autoSkillFixDataVC.size(); i++)
            {
            	BattleSkillFixData data = autoSkillFixDataVC.get(i);
                if (data.num == num)
                {
                    return data;
                }
            }
    		System.out.println("随机技能编号"+num+"不存在");
    	}
    	else if(num>=1001 && num<=2000)   //被动技能
    	{
    		for (int i = 0; beSkillFixDataVC != null && i < beSkillFixDataVC.size(); i++)
            {
    			BattleSkillFixData data = beSkillFixDataVC.get(i);
                if (data.num == num)
                {
                    return data;
                }
            }
            System.out.println("被动技能编号"+num+"不存在");
    	}
    	else if(num>=1 && num<=1000)
    	{
    		for (int i = 0; angrySkillFixDataVC != null && i < angrySkillFixDataVC.size(); i++)
            {
            	BattleSkillFixData data = angrySkillFixDataVC.get(i);
                if (data.num == num)
                {
                    return data;
                }
            }
            System.out.println("怒技能编号"+num+"不存在");
    	}        
        return null;
    }
    public static BattleSkill getSkill(int skillNum,int level)
    {
    	BattleSkillFixData data =  getSkillDataByNum(skillNum);
    	BattleSkill skill = new BattleSkill();
    	skill.level = (byte)level;
    	skill.battleSkillFixData = data;
    	return skill;    	
    }
}
