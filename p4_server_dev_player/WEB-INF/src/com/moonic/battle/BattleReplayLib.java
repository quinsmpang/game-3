package com.moonic.battle;

import java.util.Hashtable;

import server.config.ServerConfig;

import com.moonic.util.DBHelper;

public class BattleReplayLib 
{
	//public static String replayStrData;
	//public static Vector<String> replayCacheData;
	public static int maxCacheLen=1000; //最多缓存数
	public static Hashtable<String, String> replayCacheData = new Hashtable<String, String>(1000);
	
	/*static
	{
		readFixData();
	}*/
    

    /*public static void readFixData()
    {
        if (replayStrData != null) return;  //只载入一次
        
        replayCacheData=new Vector<String>();
        
		try {
			replayStrData = DBPool.getInst().readTxtFromPool("battle_replay");
		} catch (Exception e) {		
			e.printStackTrace();
			return;
		}
		if(replayStrData==null)
		{
			System.out.println("tab_txt中battle_replay不存在！");
			return;
		}
    }*/
	
	/**
	 * 加入战斗回放缓存
	 * @param battleId
	 * @param replayData
	 */
	public static synchronized void addBattleReplayData(long battleId,String replayData)
	{
		if(!replayCacheData.containsKey(String.valueOf(battleId)))
		{
			replayCacheData.put(String.valueOf(battleId), replayData);		
		}
	}
    public static synchronized String getReplayStrData(long battleId)
    {
    	//从数据库缓存里找
    	/*if(replayStrData != null)
    	{
    		String str = Tools.getSubString(replayStrData, "battle"+battleId+":", "battle"+battleId+"End");
    		if(str!=null)
    		{
    			//System.out.println("从数据库缓存中获取battleId="+battleId+"的回放数据");
    			return "data:"+str+"dataEnd";
    		}
    	}*/
    	
    	//从内存缓存对象里找
    	if(replayCacheData!=null)
    	{
    		String cacheStr =replayCacheData.get(String.valueOf(battleId));
    		if(cacheStr!=null && !cacheStr.equals(""))
    		{
    			//System.out.println("从回放缓存对象中获取battleId="+battleId+"的回放数据");
    			return cacheStr;
    		}
    	}
    	
    	//从日志文件目录里寻找
    	String logFileStr = null;
    	DBHelper dbHelper = new DBHelper(ServerConfig.getDataBase_Log());
    	try {
    		dbHelper.openConnection();
    		/*ResultSet logRs = dbHelper.query(LogTbName.TAB_BATTLE_RECORD(), "replaydata", "battleid="+battleId);
    		if(logRs.next()){
    			logFileStr = new String(logRs.getBytes("replaydata"), "UTF-8");
    		}*/
    		
    	} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeConnection();
		}
    	if(logFileStr!=null)
    	{
    		//System.out.println("从日志文件中获取battleId="+battleId+"的回放数据");
    		
    		if(replayCacheData.size()>maxCacheLen)
    		{    		
    			replayCacheData.clear();    			
    			System.out.println("战斗回放缓存对象已超过"+maxCacheLen+",清理回放缓存对象");
    		}    		
    		replayCacheData.put(String.valueOf(battleId), logFileStr);    		
    		
    		//System.out.println("replayCacheData.size()="+replayCacheData.size());
    			
    		return logFileStr;
    	}
    	
        return null;
    }   
}
