package com.moonic.bac;

import com.ehc.common.ReturnValue;
import com.moonic.util.FileUtil;

import conf.Conf;

public class SaveFileBAC {
	
	/**
	 * ´æÎÄ¼þ
	 */
	public ReturnValue saveFile(String savepath, byte[] filedata){
		try {
			FileUtil fileutil = new FileUtil();
			fileutil.save(Conf.savepath+savepath, filedata);
			return new ReturnValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnValue(false, e.toString());
		}
	}
	
	//--------¾²Ì¬Çø----------
	
	private static SaveFileBAC instance = new SaveFileBAC();
	
	public static SaveFileBAC getInstance(){
		return instance;
	}
}
