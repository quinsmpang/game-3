package com.moonic.bac;

import org.json.JSONArray;

import com.moonic.gamelog.GameLog;
import com.moonic.util.DBPaRs;
import com.moonic.util.DBPool;

/**
 * 经验
 * @author John
 */
public class ExpBAC {
	
	/**
	 * 增加经验
	 * @param logname 在日志中的名称
	 */
	public JSONArray addExp(String table, int lv, long exp, long addexp, int maxlv, String logname, GameLog gl) throws Exception{
		long beforeexp = exp;//增加前经验
		long beforelv = lv;//增加前等级
		exp += addexp;
		long totalexp = exp;//增加后总经验
		long destroyexp = 0;//销毁的经验
		while(true){
			DBPaRs expRs = DBPool.getInst().pQueryA(table, "lv="+lv);
			if(!expRs.exist() || expRs.getLong("needexp")==-1){//等级已达已达上限
				destroyexp = exp;//记录销毁经验数
				exp = 0;//经验归零
				break;
			}
			long needexp = expRs.getLong("needexp");//升级需要经验
			if(maxlv > 0 && lv >= maxlv){//是否有单独的等级限制需求
				if(exp > needexp){//如果当前拥有经验超过升级需要经验量，则销毁超过的经验
					destroyexp = exp - needexp;
					exp = needexp;
				}
				break;
			}
			if(exp < needexp){//经验不足则退出循环
				break;
			}
			lv++;
			exp -= needexp;
		}
		if(beforeexp != exp){//经验变化
			gl.addChaNote(logname+"经验", beforeexp, addexp);
		}
		if(destroyexp > 0){//销毁经验量
			if(exp == 0){
				gl.addRemark(logname+"等级已满，销毁经验"+destroyexp);
			} else {
				gl.addRemark(logname+"等级达到指定限制，销毁经验"+destroyexp);
			}
		}
		if(beforelv != lv){//等级变化
			gl.addChaNote(logname+"经验", totalexp, exp-totalexp);
			gl.addChaNote(logname+"等级", beforelv, lv-beforelv);
		}
		if(beforelv != lv || beforeexp != exp){//等级或经验变化
			JSONArray returnarr = new JSONArray();
			returnarr.add(lv);
			returnarr.add(exp);
			returnarr.add(totalexp-beforeexp-destroyexp);//实际使用的经验
			return returnarr;
		} else {
			return null;
		}
	}
	
	//--------------静态区--------------
	
	private static ExpBAC instance = new ExpBAC();
	
	/**
	 * 获取实例
	 */
	public static ExpBAC getInstance(){
		return instance;
	}
}
