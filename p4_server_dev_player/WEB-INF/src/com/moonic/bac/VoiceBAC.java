package com.moonic.bac;

import com.moonic.servlet.STSServlet;
import com.moonic.util.BACException;
import com.moonic.util.NetResult;
import com.moonic.util.STSNetSender;

import conf.Conf;

/**
 * 语音BAC
 * @author alexhy,John
 */
public class VoiceBAC {
	
	/**
	 * 上传语音
	 */
	public String uploadVoice(int vsid, byte[] voiceData)  throws Exception {
		String filename = null;
		synchronized (instance) {
			filename = String.valueOf(System.currentTimeMillis());
		}
		STSNetSender sender = new STSNetSender(STSServlet.R_SAVE_FILE);
		sender.dos.writeUTF("voice/"+vsid+"/"+filename+".dat");
		sender.dos.writeInt(voiceData.length);
		sender.dos.write(voiceData);
		NetResult nr = ServerBAC.getInstance().sendReqToOne(sender, Conf.res_url);
		if(!nr.rv.success){
			BACException.throwAndOutInstance(nr.rv.info);
		}
		return filename;
	}

	// --------------静态区--------------

	public static VoiceBAC instance = new VoiceBAC();

	/**
	 * 获取实例
	 */
	public static VoiceBAC getInstance() {
		return instance;
	}
}
