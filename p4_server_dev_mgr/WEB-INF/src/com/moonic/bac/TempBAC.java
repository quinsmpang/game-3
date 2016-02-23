package com.moonic.bac;

import org.json.JSONArray;

import server.common.Tools;

import com.ehc.common.ReturnValue;
import com.moonic.util.DBPool;
import com.moonic.util.DBUtil;
import com.moonic.util.DynamicGroovy;
import com.moonic.util.MyTools;


/**
 * 临时处理方法
 * @author John
 */
public class TempBAC {
	
	/**
	 * 内存查询速度测试
	 * @return
	 */
	public ReturnValue jsonQueryTest(String paraStr){
		try {
			JSONArray jsonarr = DBUtil.jsonQuery("tab_player", DBPool.getInst().readTableFromPool("tab_player"), null, null, 1, 1000);
			int[] para = Tools.splitStrToIntArr(paraStr, ",");
			JSONArray returnarr = new JSONArray();
			for(int i = 0; i < para.length; i++){
				JSONArray arr = new JSONArray();
				for(int t = 0; t < para[i]; t += jsonarr.length()){
					MyTools.combJsonarr(arr, jsonarr);
				}
				JSONArray temp = new JSONArray();
				temp.add(arr.length());
				for(int t = 0; t < 10; t++){
					long t1 = System.currentTimeMillis();
					DBUtil.jsonQuery("tab_player", arr, "id=251", "id", 0, 0);
					long t2 = System.currentTimeMillis();
					temp.add(t2-t1);
				}
				returnarr.add(temp);
			}
			return new ReturnValue(true, returnarr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	DynamicGroovy dynamicGroovy = DynamicGroovy.getInstance("com/moonic/bac/Test.groovy");
	
	public ReturnValue groovyTest(){
		try {
			String str = (String)dynamicGroovy.getProperty("test_str");
			return new ReturnValue(true, str);		
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	//----------------静态区------------------
	
	private static TempBAC instance = new TempBAC();

	public static TempBAC getInstance() {
		return instance;
	}
}
