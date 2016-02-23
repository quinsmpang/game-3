package server.config;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import server.common.Tools;

import com.ehc.common.SqlString;
import com.ehc.dbc.BaseActCtrl;
import com.moonic.util.DBHelper;
import com.moonic.util.MyTools;

import conf.Conf;
import conf.LogTbName;

public class LogBAC extends BaseActCtrl {
	
	public LogBAC()
	{
		super.setTbName(LogTbName.tb_log());
		setDataBase(ServerConfig.getDataBase_Log());
	}
	
	public JSONObject getOpereatLogList(int page, int rowsPerPage){
    	return getJsonPageList(null, "savedate desc", page, rowsPerPage);
    }
	
	private static LogBAC instance = new LogBAC();
	
	public static LogBAC getInstance(){
		return instance;
	}
	
	public static synchronized void addLog(String user,String act,String param1,String ip)
	{
		SqlString logSqlStr = new SqlString();
		logSqlStr.add("username",user);
		logSqlStr.add("act",act);
		logSqlStr.add("param1",param1);
		logSqlStr.add("ip",ip);
		logSqlStr.addDateTime("savedate",MyTools.getTimeStr());
		DBHelper.logInsert(LogTbName.tb_log(), logSqlStr);
	}
	
	/**
	 * 输出到WEB-INF/logs/目录下的自命名日志文件
	 * @param folder
	 * @param str
	 */
	public static synchronized void logout(String folder,String str)
	{		
		if(ServerConfig.getAppRootPath()!=null)
		{
			File file = new File(Conf.logRoot+folder+"/");
			if(!file.exists())
			{
				file.mkdirs();
			}
			file = new File(Conf.logRoot+folder+"/"+MyTools.getDateStr()+".txt");
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file, true);
				DataOutputStream dos = new DataOutputStream(fos);
				dos.write((Tools.getCurrentDateTimeStr()+"--"+str+"\r\n").getBytes("GBK"));					
				dos.close();
				fos.close();
			} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
		}
		public static synchronized void logout(String folder,String filename,String str)
		{
			logout(folder,filename,str,true,"GBK");
		}
		public static synchronized void logout(String folder,String filename,String str,boolean addTime,String encode)
		{		
			//System.out.println(str);
			if(ServerConfig.getAppRootPath()!=null)
			{
				File file = new File(Conf.logRoot+folder+"/");
				if(!file.exists())
				{
					file.mkdirs();
				}
				file = new File(Conf.logRoot+folder+"/"+filename);
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(file, true);
					DataOutputStream dos = new DataOutputStream(fos);
					dos.write(((addTime?(Tools.getCurrentDateTimeStr()+"--"):"")+str+"\r\n").getBytes(encode));					
					dos.close();
					fos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
		}
		public static String getLogFileStr(String folder,String filename,String encode)
		{			
			File file = new File(Conf.logRoot+folder+"/"+filename);
			if(file.exists())
			{				
				try {
					byte[] fileBytes = Tools.getBytesFromFile(file);
					String fileStr=new String(fileBytes,encode);	
					return fileStr;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				} 
			}	
			else
			{
				return null;
			}
		}
}
