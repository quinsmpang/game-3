package com.moonic.mgr;

import com.ehc.common.ReturnValue;
import com.moonic.util.MyLog;

import conf.Conf;

/**
 * 动作管理
 * @author John
 */
public abstract class ActMgr {
	public ActBag reqact;
	
	public long lasttime;//最后请求时间
	
	public static MyLog log = new MyLog(MyLog.NAME_DATE, "log_actmgr", "ACTMGR", Conf.debug, false, true, null);
	
	/**
	 * 加入正在处理的请求
	 */
	public ReturnValue addReqing(short act, long time){
		synchronized (this) {
			ReturnValue val = null;
			long currtime = System.currentTimeMillis();
			if(currtime>lasttime+5){//请求频率控制
				log.d(getKey()+"有客户端请求：" + act + "," + time);
				if(reqact != null && !reqact.req_finish){//单个请求处理控制
					String str = "有请求正在处理(act="+reqact.act+")...";
					val = new ReturnValue(false, str);
					log.d(str);
				} else {
					if(reqact != null && reqact.act == act && reqact.time == time){//客户端为接收到结果，再次发相同请求，返回历史结果
						val = reqact.rv;
						val.parameter = "";//标记返回历史请求
						log.d("返回历史结果");
					} else {//新的请求，创建新的对象
						reqact = new ActBag();
						reqact.act = act;
						reqact.time = time;
						val = new ReturnValue(true);
						log.d("开始处理请求");
					}
				}
				lasttime = currtime;
			} else {
				log.d(getKey()+" 请求频率过快，上次请求动作："+reqact.act+"，上次请求时间："+lasttime+"，本次请求动作："+act+"，本次请求时间："+currtime, true);
				val = new ReturnValue(false, "请求频率过快，上次请求动作："+reqact.act+"，上次请求时间："+lasttime+"，本次请求动作："+act+"，本次请求时间："+currtime);
			}
			return val;
		}
	}
	
	/**
	 * 移除正在被处理的请求
	 */
	public void removeReqing(short act, ReturnValue rv){
		synchronized (this) {
			if(reqact != null && reqact.act == act){
				reqact.rv = rv;//记录最后处理的请求的结果
				reqact.req_finish = true;
				log.d("处理请求" + reqact.act + "," + reqact.time + "完成");
			} else {
				log.d("处理请求异常 " + reqact.act + "/" + act);
			}
		}
	}
	
	//-----------抽象区------------
	
	/**
	 * 获取KEY
	 */
	public abstract String getKey();
	
	//-----------内部类------------
	
	/**
	 * 请求包
	 */
	class ActBag {
		public short act;
		public long time;
		public boolean req_finish;
		public ReturnValue rv;
		
		/**
		 * 重写
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			sb.append(act);
			sb.append(",");
			sb.append(time);
			sb.append("]");
			return sb.toString();
		}
	}
}
