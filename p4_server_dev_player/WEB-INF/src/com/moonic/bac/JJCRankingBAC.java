package com.moonic.bac;

import com.moonic.mirror.MirrorOne;

/**
 * 竞技场排位战
 * @author John
 */
public class JJCRankingBAC extends MirrorOne {
	
	/**
	 * 构造
	 */
	public JJCRankingBAC() {
		super("tab_pla_jjcranking", "ranking");
		needcheck = false;
		serverWhere = true;
	}
	
	//------------------静态区--------------------
	
	private static JJCRankingBAC instance = new JJCRankingBAC();

	public static JJCRankingBAC getInstance() {
		return instance;
	}
}
