package com.moonic.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Sortable;
import server.common.Tools;

import com.ehc.common.ToolFunc;
import com.moonic.mgr.LockStor;

/**
 * 工具集
 * @author John
 */
public class MyTools {
	public static final long long_minu = 60 * 1000;
	public static final long long_hour = 60 * long_minu;
	public static final long long_day = 24 * long_hour;
	
	/**
	 * 限定时间
	 * @param week	星期几： 1表示星期天、2表示星期一...7表示星期六
	 * @param hour	小时：24小时制，0表示凌晨0点、12表示中午12点、17表示下午5点
	 * @param minute	分钟：0~59
	 * @return	毫秒数
	 */
	public static long setDateTime(int week, int hour, int minute) {
		Calendar cal = Calendar.getInstance();	//当前日期
		cal.set(Calendar.DAY_OF_WEEK,week);	//星期几
		cal.set(Calendar.HOUR_OF_DAY, hour);	//时间24小时制
		cal.set(Calendar.MINUTE, minute);	
		cal.set(Calendar.SECOND, 0);
		return cal.getTimeInMillis();	//返回此时间的毫秒数
	}
	
	/**
	 * 获取指定星期0点的下个时间点
	 * @param time 用于计算的起始时间
	 * @param d_num 星期编号 [星期日~星期六 = 1~7]
	 * @param pointtime 截至时间点
	 */
	public static long getNextWeekDay(long time, int d_num, long pointtime){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		int week = cal.get(Calendar.DAY_OF_WEEK);
		int add = 0;
		if(week < d_num){
			add = d_num - week;
		} else 
		if(week == d_num){
			if(time - MyTools.getCurrentDateLong(time) < pointtime){
				add = d_num - week;
			} else {
				add = 7 + d_num - week;
			}
		} else {
			add = 7 + d_num - week;
		}
		long thetime = cal.getTimeInMillis() + add * long_day;
		return thetime;
	}
	
	/**
	 * 检查两个时间是否经过指定周一
	 */
	public static boolean checkWeek(long start, long end){
		long ftime = getFirstDayOfWeek();
		return start < ftime && end >= ftime;
	}
	
	/**
	 * 检查两个时间是否经过月首
	 */
	public static boolean checkMonth(long start, long end){
		long ftime = getFirstDayOfMonth();
		return start < ftime && end >= ftime;
	}
	
	/**
	 * 获取周一时间毫秒
	 */
	public static long getFirstDayOfWeek(){
		return getCurrentDateLong() - (getWeekEx()-1) * long_day;
	}	

	
	/**
	 * 获取月首时间毫秒
	 */
	public static long getFirstDayOfMonth(){
		return getCurrentDateLong() - (getMonthDay()-1)*long_day;
	}
	
	/**
	 * 获取当前时间的周次
	 */
	public static int getWeek(){
		return getWeek(System.currentTimeMillis());
	}
	
	
	/**
	 * 获取周一为第一天的周次
	 * @return
	 */
	public static int getWeekEx()
	{
		return getWeekEx(System.currentTimeMillis());
	} 
	/**
	 * 获取指定时间的周次
	 */
	public static int getWeek(long time){
		return getCal(time).get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * 获取周一为第一天的周次
	 * @param time
	 * @return
	 */
	public static int getWeekEx(long time)
	{
		int weekNum = getCal(time).get(Calendar.DAY_OF_WEEK);
		if(weekNum==1) //周日
		{
			weekNum=7;
		}
		else
		{
			weekNum = weekNum-1; //周一到周六从2到7改成1到6
		}
		return weekNum;
	}
	
	/**
	 * 获取指定时间的月天
	 */
	public static int getMonthDay(){
		return getMonthDay(System.currentTimeMillis());
	}
	
	/**
	 * 获取指定时间的月天
	 */
	public static int getMonthDay(long time){
		return getCal(time).get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 获取时间对象
	 */
	public static Calendar getCal(long time){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal;
	}
	
	/**
	 * 检查系统时间是否到达(超过)指定时间
	 */
	public static boolean checkSysTimeBeyondSqlDate(Timestamp timestamp){
		return checkSysTimeBeyondSqlDate(timestamp, 0, true);
	}
	
	/**
	 * 检查系统时间是否到达(超过)指定时间
	 * @param offtime 对指定时间偏移
	 */
	public static boolean checkSysTimeBeyondSqlDate(Timestamp timestamp, long offtime){
		return checkSysTimeBeyondSqlDate(timestamp, offtime, true);
	}
	
	/**
	 * 检查系统时间是否到达(超过)指定时间
	 */
	public static boolean checkSysTimeBeyondSqlDate(Timestamp timestamp, long offtime, boolean defaultResult){
		if(timestamp != null){
			long sqlTime = timestamp.getTime();
			return System.currentTimeMillis() >= sqlTime+offtime;
		} else {
			return defaultResult;
		}
	}
	
	/**
	 * 检查指定时间是否超过数据库时间
	 */
	public static boolean checkTheTimeBeyondSqlDate(Timestamp timestamp, long thetime){
		return checkTheTimeBeyondSqlDate(timestamp, thetime, true);
	}
	
	/**
	 * 检查指定时间是否超过数据库时间
	 */
	public static boolean checkTheTimeBeyondSqlDate(Timestamp timestamp, long thetime, boolean defaultResult){
		if(timestamp != null){
			long sqlTime = timestamp.getTime();
			return thetime >= sqlTime;
		} else {
			return defaultResult;
		}
	}
	
	/**
	 * 检查系统时间是否到达(超过)指定时间
	 */
	public static boolean checkSysTimeBeyondSqlDate(String thetime){
		return checkSysTimeBeyondSqlDate(MyTools.getTimeLong(thetime));
	}
	
	/**
	 * 检查系统时间是否到达(超过)指定时间
	 */
	public static boolean checkSysTimeBeyondSqlDate(long thetime){
		return System.currentTimeMillis() >= thetime;
	}
	
	/**
	 * 获取日期的毫秒形式
	 */
	public static long getCurrentDateLong(){
		return getCurrentDateLong(System.currentTimeMillis());
	}
	
	/**
	 * 根据指定时间点获取时间点的LONG形式
	 */
	public static long getPointTimeLong(String ptStr){
		return Tools.str2date(MyTools.getDateStr()+" "+ptStr).getTime()-MyTools.getCurrentDateLong();
	}
	
	/**
	 * 获取时间 long 形式
	 */
	public static long getTimeLong(String str){
		if(str != null && !str.equals("") && !str.equals("null")){
			return Tools.str2date(str).getTime();
		} else {
			return 0;
		}
	}
	
	/**
	 * 获取精确到小时的毫秒形式
	 */
	public static long getCurrentHourLong(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * 获取日期的毫秒形式
	 */
	public static long getCurrentDateLong(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * 检查字符串是否在指定数组中
	 */
	public static boolean checkInStrArr(String[] arr, String str){
		for(int i = 0; arr != null && i < arr.length; i++){
			if(arr[i].equals(str)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取时间 long 形式
	 */
	public static long getTimeLong(Timestamp timestamp){
		if(timestamp != null){
			return timestamp.getTime();
		} else {
			return 0;
		}
	}
	
	public static final char[] noChar = {'\'', ' ', '(', ')', '='};
	
	/**
	 * 检查是否有敏感字符
	 */
	public static void checkNoChar(String str) throws Exception {
		checkNoChar(str, noChar);
	}
	
	/**
	 * 检查是否有敏感字符
	 */
	public static void checkNoChar(String str, char[] noChar) throws Exception {
		if(str != null){
			for(int i = 0; i < noChar.length; i++){
				if(str.indexOf(noChar[i]) != -1){
					BACException.throwInstance("有非法字符，请更改后重试");
				}
			}		
		}
	}
	
	/**
	 * 检查是否有敏感字符
	 */
	public static void checkNoCharEx(String str, char... addNoChar) throws Exception {
		if(str != null){
			for(int i = 0; i < noChar.length; i++){
				if(str.indexOf(noChar[i]) != -1){
					BACException.throwInstance("有非法字符，请更改后重试");
				}
			}
			for(int i = 0; addNoChar != null && i < addNoChar.length; i++){
				if(str.indexOf(addNoChar[i]) != -1){
					BACException.throwInstance("有非法字符，请更改后重试");
				}
			}
		}
	}
	
	private static Random ran = new Random(System.currentTimeMillis());
	
	public static void main2(String[] args){
		getRandom(0, -2);
	}
	
	/**
	 * 获得 指定范围的随机数(包含end)
	 */
	public static int getRandom(int startInt, int endInt) {
		return getRandom(ran, startInt, endInt);
	}
	
	private static Hashtable<Integer, Hashtable<Short, Random>> randomStor = new Hashtable<Integer, Hashtable<Short,Random>>();
	
	public static final short RAN_SPIRIT_SMELT = 1;
	public static final short RAN_SPIRIT_DEBRIS = 2;
	public static final short RAN_SPIRIT_ROLE = 3;
	public static final short RAN_ESCORT_REFRESH = 4;
	public static final short RAN_SPIN = 5;
	public static final short RAN_MSHOP_REFRESH_1 = 6;
	public static final short RAN_MSHOP_REFRESH_3 = 7;
	public static final short RAN_MSHOP_REFRESH_4 = 8;
	public static final short RAN_MSHOP_REFRESH_5 = 9;
	public static final short RAN_MSHOP_REFRESH_6 = 10;
	
	/**
	 * 获得 指定范围的随机数(包含end)
	 */
	public static int getRandom(int playerid, short type, int startInt, int endInt) {
		Random random = null;
		synchronized (LockStor.getLock(LockStor.RANDOM_NEXT, playerid)) {
			Hashtable<Short, Random> stor = randomStor.get(playerid);
			if(stor == null){
				stor = new Hashtable<Short, Random>();
				randomStor.put(playerid, stor);
				//System.out.println("加入"+playerid+"随机库到随机总库");
			}
			random = stor.get(type);
			if(random == null){
				synchronized (LockStor.getLock(LockStor.RANDOM_TIME)) {
					random = new Random(ran.nextLong());
				}
				stor.put(type, random);
				//System.out.println("加入"+playerid+"的"+type+"类型到随机库");
			}	
		}
		return getRandom(random, startInt, endInt);
	}
	
	/**
	 * 清除随机数缓存
	 */
	public static void cleanRandom(int playerid){
		synchronized (LockStor.getLock(LockStor.RANDOM_NEXT, playerid)) {
			randomStor.remove(playerid);
			//System.out.println("从随机总库清除"+playerid+"的随机库");
		}
	}
	
	/**
	 * 将jsonarr2的元素加入jsonarr1中
	 */
	public static void combJsonarr(JSONArray jsonarr1, JSONArray jsonarr2){
		for(int i = 0; i < jsonarr2.length(); i++){
			jsonarr1.add(jsonarr2.opt(i));
		}
	}
	
	/**
	 * 获得 指定范围的随机数(包含end)
	 */
	public static int getRandom(Random random, int startInt, int endInt) {
		if (endInt < startInt) {
			try {
				throw new Exception("随机数异常："+startInt+"~"+endInt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return startInt;
		}
		return startInt + Math.abs(random.nextInt()) % (endInt - startInt + 1);
	}
	
	/**
     * 将毫秒转换为字符串
     */
    public static String formatTime(String format){
    	return formatTime(System.currentTimeMillis(), format);
    }
	
	/**
     * 将毫秒转换为字符串
     */
    public static String formatTime(long time, String format){
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
        String str = sdf.format(new Date(time));
        return str;
    }
    
    /**
     * 获取当前日期的str形式
     */
    public static String getDateStr(){
    	return getDateStr(System.currentTimeMillis());
    }
    
    /**
     * 获取指定日期的str形式
     */
    public static String getDateStr(Timestamp timestamp){
    	return getDateStr(MyTools.getTimeLong(timestamp));
    }
    
    /**
     * 获取指定日期的str形式
     */
    public static String getDateStr(long time){
    	return MyTools.formatTime(time, "yyyy-MM-dd");
    }
    
    /**
     * 获取当前时间的str形式
     */
    public static String getTimeStr(){
    	return getTimeStr(System.currentTimeMillis());
    }
    
    /**
     * 获取指定时间的str形式
     */
    public static String getTimeStr(Timestamp timestamp){
    	return getTimeStr(MyTools.getTimeLong(timestamp));
    }
    
    /**
     * 获取指定时间的str形式
     */
    public static String getTimeStr(long time){
    	return formatTime(time, "yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * 获取指定时间的str形式
     */
    public static String getTimeMSStr(){
    	return getTimeMSStr(System.currentTimeMillis());
    }
    
    /**
     * 获取指定时间的str形式
     */
    public static String getTimeMSStr(long time){
    	return formatTime(time, "yyyy-MM-dd HH:mm:ss.SSS");
    }
    
    /**
     * 获取当前时间的Timestamp形式
     */
    public static Timestamp getTimestamp(){
    	return getTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 获取指定时间的Timestamp形式
     */
    public static Timestamp getTimestamp(long time){
    	return java.sql.Timestamp.valueOf(Tools.formatDate(getTimeStr(time), "yyyy-MM-dd HH:mm:ss"));
    }
    
    /**
     * 获取当前日期的sql形式
     */
    public static String getDateSQL(){
    	return getDateSQL(System.currentTimeMillis());
    }
    
    /**
     * 获取指定日期的sql形式
     */
    public static String getDateSQL(Timestamp timestamp){
    	return getDateSQL(MyTools.getTimeLong(timestamp));
    }
    
    /**
     * 获取指定日期的sql形式
     */
    public static String getDateSQL(long time){
    	return "to_date('" + MyTools.formatTime(time, "yyyy-MM-dd") + "' ,'YYYY-MM-DD')";
    }
    
    /**
     * 获取当前时间的sql形式
     */
    public static String getTimeSQL(){
    	return getTimeSQL(System.currentTimeMillis());
    }
    
    /**
     * 获取指定时间的sql形式
     */
    public static String getTimeSQL(Timestamp timestamp){
    	return getTimeSQL(MyTools.getTimeLong(timestamp));
    }
    
    /**
     * 获取指定时间的sql形式
     */
    public static String getTimeSQL(long time){
    	return "to_date('" + MyTools.formatTime(time, "yyyy-MM-dd HH:mm:ss") + "' ,'YYYY-MM-DD HH24:MI:SS')";
    }
    
    /**
     * 加入int到JSON数组，前提为int大于0
     */
    public static void putByNoZero(JSONObject jsonobj, String key, int value){
    	if(value > 0){
    		jsonobj.put(key, value);
    	}
    }
    
    /**
     * 格式化JSONARR输出格式
     */
    public static String getFormatJsonarrStr(JSONArray jsonarr){
    	StringBuffer sb = new StringBuffer("");
    	for(int i = 0; jsonarr!=null && i<jsonarr.length(); i++){
    		sb.append(jsonarr.opt(i).toString()+"\r\n");
    	}
    	return sb.toString();
    }
    
    /**
     * 将数组转换为SQL条件
     */
    public static String converWhere(String sign, String column, String operator, int[] paras){
		StringBuffer sb = new StringBuffer("");
		for(int i = 0; i<paras.length; i++){
    		if(i > 0){
    			sb.append(" ");
    			sb.append(sign);
    			sb.append(" ");
    		}
    		sb.append(column);
    		sb.append(operator);
    		sb.append(paras[i]);
    	}
    	return sb.toString();
    }
    
    /**
     * 将数组转换为SQL条件
     */
    public static String converWhere(String sign, String column, String operator, String[] paras){
		StringBuffer sb = new StringBuffer("");
		for(int i = 0; i<paras.length; i++){
    		if(i > 0){
    			sb.append(" ");
    			sb.append(sign);
    			sb.append(" ");
    		}
    		sb.append(column);
    		sb.append(operator);
    		sb.append("'");
    		sb.append(paras[i]);
    		sb.append("'");
    	}
    	return sb.toString();
    }
    
    /**
     * 将数组转换为SQL条件
     */
    public static String converTimeWhere(String column, String[] paras){
		StringBuffer sb = new StringBuffer("");
		String sign = " or ";
    	for(int i = 0; i<paras.length; i++){
    		if(i > 0){
    			sb.append(sign);
    		}
    		sb.append(column);
    		sb.append("=");
    		sb.append(paras[i]);
    	}
    	return sb.toString();
    }
    
    /**
     * 格式化时间段为时分秒字符串形式
     */
    public static String formatHMS(long timelen){
    	try {
    		DecimalFormat df = new DecimalFormat("00");
    		String hour = df.format(timelen / (60 * 60 * 1000));
    		timelen = timelen % (60 * 60 * 1000);
    		String minu = df.format(timelen / (60 * 1000));
    		timelen = timelen % (60 * 1000);
    		String sec = df.format(timelen / 1000);
    		return hour+":"+minu+":"+sec;
    	} catch (Exception e) {
			return "格式化出错";
		}
    }
    
    private static ArrayList<ScheduledExecutorService> timerlist = new ArrayList<ScheduledExecutorService>();
    
    /**
     * 创建Timer对象
     */
    public static ScheduledExecutorService createTimer(int threadamount){
    	ScheduledExecutorService timer = Executors.newScheduledThreadPool(threadamount);
    	timerlist.add(timer);
    	return timer;
    }
    
    /**
     * 取消Timer
     */
    public static void cancelTimer(ScheduledExecutorService timer){
    	if(timer != null){
    		timerlist.remove(timer);
        	timer.shutdownNow();
    	}
    }
    
    /**
     * 取消所有Timer
     */
    public static void closeAllTimer(){
    	for(int i = 0; i < timerlist.size(); i++){
    		ScheduledExecutorService timer = timerlist.get(i);
    		if(timer != null)
    		{
    			try
    			{
    				timer.shutdownNow();
    			}
    			catch(Exception ex){}
    		}
    	}
    	timerlist = null;
    }
    
    /**
	 * 通过指定数组和编号参数获取对应下标
	 * @return 查询结果，-1表示未找到
	 */
	public static int getIndexByInt(int[] numArr , int num){
		int result = -1;
		for(int i = 0 ; numArr != null && i < numArr.length ; i++){
			if(numArr[i] == num){
				result = i;
				break;
			}
		}
		return result;
	}
	
	/**
	 * 通过指定原始数据数组，编号和编号下标获取对应下标
	 * @return 查询结果，-1表示未找到
	 */
	public static int getIndexByString2(String[][] source, int index, int num){
		int result = -1;
		for(int i = 0; source != null && i < source.length; i++){
			if(Tools.str2int(source[i][index]) == num){
				result = i;
				break;
			}
		}
		return result;
	}
	
	public static void main1(String[] args){
		String str1 = "420112198809152718";
		System.out.println(getEncrypeStr(str1, 6, str1.length()-2));
		String str2 = "1b";
		System.out.println(getEncrypeStr(str2, str2.length()/2, str2.length()));
		String str3 = "15021592157";
		System.out.println(getEncrypeStr(str3, 3, 6));
	}
	
	/**
	 * 获取星号加密字符串
	 */
	public static String getEncrypeStr(String str, int start_ind, int end_ind){
		if(str!=null && !str.equals("") && start_ind<end_ind && str.length()>=end_ind){
			StringBuffer sb = new StringBuffer();
			sb.append(str.substring(0, start_ind));
			int star_am = end_ind-start_ind;
			if(end_ind < str.length()){
				star_am++;
			}
			for(int i = 0; i < star_am; i++){
				sb.append('*');
			}
			if(end_ind < str.length()){
				sb.append(str.substring(end_ind+1));
			}
			str = sb.toString();
		}
		return str;
	}
	
	/**
	 * 读取txt文件中的所有内容
	 */
	public static String readTxtFile(String path){
		String fileStr = null;
		try {
			byte[] data = ToolFunc.getBytesFromFile(path);
			if(data[0]==(byte)0xEF && data[1]==(byte)0xBB && data[2]==(byte)0xBF){
				byte[] newdata = new byte[data.length-3];
				System.arraycopy(data, 3, newdata, 0, newdata.length);
				data = newdata;
			}
			fileStr = new String(data, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileStr;
	}
	
	private static final DecimalFormat doubleDf = new DecimalFormat("0.00");
	
	/**
	 * 格式化小数
	 */
	public static double formatNum(double number){
		return Double.valueOf(doubleDf.format(number));
	}
	
	/**
	 * 格式化小数
	 */
	public static double formatNum(double number, int precision){
		String pattern = null;
		String str = Tools.copy("0", precision);
		if(str.length() > 0){
			pattern = "0."+str;
		} else {
			pattern = "0";
		}
		DecimalFormat doubleDf = new DecimalFormat(pattern);
		return Double.valueOf(doubleDf.format(number));
	}
	
	public static void main3(String[] args){
		System.out.println("格式化前："+(30.3+30.6));
		System.out.println("格式化保留2位小数："+formatNum(30.3+30.6));
		System.out.println("格式化保留0位小数："+formatNum(30.3+30.6, 0));
		System.out.println("格式化保留20位小数："+formatNum(30.3+30.6, 20));
	}
	
	public static void main5(String[] args){
		System.out.println();
		System.out.println("0.15".length()-1-("0.15".indexOf('.')));
	}
	
	/**
	 * 对JSONARR数组排序
	 * @param jsonarr 需要排序的JSONARR 支持元素为JSONArray的数组
	 * @param order 排序条件 [INDEX,INDEX DESC,INDEX...]
	 */
	public static JSONArray sortJSONArray(JSONArray jsonarr, String order) throws Exception {
		if(jsonarr==null || jsonarr.length()<=0 || order==null || order.equals("")){
			return jsonarr;
		}
		order = order.toLowerCase();
		//System.out.println("order:"+order);
		//TimeTest tt = new TimeTest(null, "", false, false, true);
		String[] groups = Tools.splitStr(order, ",");//排序条件组
		byte[] sort_ind = new byte[groups.length];
		byte[] sort_type = new byte[groups.length];
		for(int i = 0; i < groups.length; i++){
			String[] group = Tools.splitStr(groups[i], " ");
			sort_ind[i] = Byte.valueOf(group[0]);
			if(group.length > 1 && group[1].equals("desc")){
				sort_type[i] = 1;
			}
		}
		double[] maxnum = new double[groups.length];//排序字段最大数
		double[] mul = new double[groups.length];//排序字段倍数
		//tt.add("准备");
		for(int k = 0; k < sort_ind.length; k++){//排序条件循环，收集数据的综合排序信息
			int maxlen = 0;
			int decimalslen = 0;
			for(int i = 0; i < jsonarr.length(); i++){//数据循环
				String val = jsonarr.optJSONArray(i).optString(sort_ind[k]);
				double theone = 0;//本条数据本字段的值
				//System.out.println(val);
				if(val.equals("") || (val.indexOf('-')!=0 && val.indexOf('-')!=-1)){
					theone = MyTools.getTimeLong(val);
				} else {
					theone = Double.valueOf(val);
				}
				if(i == 0 || theone > maxnum[k]){//最大值判断
					maxnum[k] = theone;
				}
				int len = 0;
				int point_ind = val.indexOf('.');
				if(point_ind == -1){
					len = val.length();
				} else {
					len = point_ind;
					decimalslen = Math.max(decimalslen, val.length() - 1 - point_ind);
				}
				if(len > maxlen){//最大长度判断
					maxlen = len;
				}
			}
			if(decimalslen > 0){
				maxlen += decimalslen;
			}
			mul[k] = 1;//倍数初始化
			for(int i = 0; i < decimalslen; i++){
				mul[k]*=10;
			}
			for(int i = k-1; i >= 0; i--){
				for(int j = 0; j < maxlen; j++){
					mul[i]*=10;
				}
			}
			//System.out.println(new JSONArray(groups[k]));
			//System.out.println("maxlen:"+maxlen);
		}
		//tt.add("收集排序信息");
		/*for(int k = 0; k < groups.length; k++){
			System.out.println("k:"+maxnum[k]+" "+mul[k]);
		}*/
		SortObj[] sortobj = new SortObj[jsonarr.length()];//排序数组
		for(int i = 0; i < jsonarr.length(); i++){//数据循环
			JSONArray arr = jsonarr.optJSONArray(i);
			double sv = 0;//排序值
			for(int k = 0; k < sort_ind.length; k++){//条件循环，计算排序值
				String dStr = arr.optString(sort_ind[k]);
				double d = 0;//字段值
				if(dStr.equals("") || (dStr.indexOf('-')!=0 && dStr.indexOf('-')!=-1)){
					d = MyTools.getTimeLong(dStr);
				} else {
					d = Double.valueOf(dStr);
				}
				boolean reverse = false;//反向
				if(k > 0){//首个条件不做反向
					if(sort_type[k] != sort_type[0]){//与首条件排序类型不一致，则数值需要反向
						reverse = true;
					}
				}
				if(reverse){
					sv += (maxnum[k]-d) * mul[k];
				} else {
					sv += d * mul[k];//乘需要放大的倍数
				}
			}
			//System.out.println(arr.optInt(0)+" sv:"+String.valueOf(sv));
			//tt.add("装配 1");
			sortobj[i] = new SortObj(arr, sv);
			//tt.add("装配 2");
		}
		Tools.sort(sortobj, sort_type[0]);
		//tt.add("排序");
		JSONArray temparr = new JSONArray();
		for(int i = 0; i < sortobj.length; i++){
			temparr.add(sortobj[i].obj);
		}
		//tt.add("整理");
		//tt.print();
		return temparr;
	}
	
	/**
	 * 排序类
	 * @author John
	 */
	private static class SortObj implements Sortable {
		public JSONArray obj;
		public double sortValue;
		public SortObj(JSONArray obj, double sortValue){
			this.obj = obj;
			this.sortValue = sortValue;
		}
		public double getSortValue() {
			return sortValue;
		}
	}
	
	public static boolean isDateBefore(String date1, String date2) {
		try {
			DateFormat df = DateFormat.getDateTimeInstance();
			return df.parse(date1).before(df.parse(date2));
		} catch (ParseException e) {
			return false;
		}
	}

	public static boolean isDateBefore(String date2) {
		try {
			java.util.Date date1 = new java.util.Date();
			return date1.before(Tools.str2date(date2));
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 加UTF8头
	 */
	public static byte[] addUTF8Head(byte[] srcData) throws Exception {
		byte[] data = new byte[srcData.length+3];
		data[0] = (byte)0xEF;
		data[1] = (byte)0xBB;
		data[2] = (byte)0xBF;
		System.arraycopy(srcData, 0, data, 3, srcData.length);
		return data;
	}
	
	/**
	 * 根据几率获取对应的下标(从0开始)
	 * 仅对此类格式字符串有效，例如：
	 * 500,250,150,70,20,10
	 * @param odds	几率字符串
	 */
	public static int getIndexOfRandom(String odds) {
		int[] arr = Tools.splitStrToIntArr(odds, ",");
		return getIndexOfRandom(arr);
	}
	
	/**
	 * 根据数组几率，随机获取数组下标
	 * 仅对此类格式int数组有效，例如：
	 * int[] arr_odds = {500,250,150,70,20,10};
	 * @param arr_odds
	 */
	public static int getIndexOfRandom(int[] arr_odds) {
		return getIndexAndRandomArr(arr_odds)[0];
	}
	
	/**
	 * 返回下标和随机数的数组
	 * {index, random}
	 */
	public static int[] getIndexAndRandomArr(String odds) {
		int[] arr = Tools.splitStrToIntArr(odds, ",");
		return getIndexAndRandomArr(arr);
	}
	
	/**
	 * 返回下标和随机数的数组
	 * {index, random}
	 */
	public static int[] getIndexAndRandomArr(int[] arr_odds) {
		int sum = 0;
		int[] tmpArr = null;
		for(int i = 0;arr_odds != null && i < arr_odds.length; i ++) {
			sum += arr_odds[i];
			tmpArr = Tools.addToIntArr(tmpArr, sum);
		}
		int random = Tools.getRandomNumber(1, sum);
		int index = 0;
		for(int i = 0; tmpArr != null && i < tmpArr.length; i ++) {
			if(random <= tmpArr[i]) {
				index = i;
				break;
			}
		}
		return new int[]{index, random};
	}
	/**
	 * 生成随机字串码
	 * @param type 类型 1字母加数字 2字母 3数字
	 * @param len 位数
	 * @param amount 数量
	 * @param exclude 排除的码
	 * @return
	 */
	public static String[] generateCode(int type,int len,int amount,String[] exclude)
	{
		if(len==0 || amount==0)
		{
			return null;
		}
		//生成2-9 a-z组成的字串，不包括容易混淆的0,1,o,l字符
		String[] lib = null;
		boolean usezimu=false;
		boolean usenumber=false;
		if(type==1) //字母加数字
		{
			usezimu=true;
			usenumber=true;
		}
		else
		if(type==2) //字母
		{
			usezimu=true;
			usenumber=false;
		}
		else
		if(type==3) //数字
		{
			usezimu=false;
			usenumber=true;
		}
		if(usezimu) //字母
		{
			//a=97
			//z=122
			//A=65
			//Z=90
			for(int i=65;i<=90;i++)
			{
				if(i==(int)'O' || i==(int)'I')
				{
					continue;
				}
				lib = Tools.addToStrArr(lib, String.valueOf((char)i));
			}			
		}
		if(usenumber) //加数字
		{			
			for(int i=2;i<10;i++)
			{
				lib = Tools.addToStrArr(lib, String.valueOf(i));
			}
		}			
		String[] gen = Tools.generateRandomStr(lib,len,amount,exclude);	
		return gen;		
	}
}
