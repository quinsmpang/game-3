package com.moonic.util;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Sortable;
import server.common.Tools;

import com.ehc.common.SqlString;


public class DBUtil {
	public static final char[] compares2 = {'>', '<', '=', '!'};
	
	public static final String[] compares = {">=", "<=", "!=", "=", ">", "<", "<>"};
	
	/**
	 * 将SQL条件转换为绑定变量形式
	 */
	public static String convertWhere(String where, SqlString wStr){
		/*
		long t1 = System.currentTimeMillis();
		System.out.println("↓↓↓--------------------------");
		System.out.println("--where1:"+where);
		*/
		String src_where = where;
		try{
			if(where != null && !where.equals("")){
				ArrayList<CData> cdata_arr = new ArrayList<CData>();
				for(int i = 0; i < compares.length; i++){
					int fromindex = 0;//从指定位置开始搜索比较符
					while(true){
						//System.out.println("fromindex:"+fromindex+" where:"+where+" compares[i]:"+compares[i]);
						//检查是否已搜索完字符串
						if(fromindex == where.length()){
							break;
						}
						//从上次被替换内容起始位置，检查是否还存在指定比较符
						int beginindex = where.indexOf(compares[i], fromindex);
						if(beginindex == -1){
							break;
						}
						//当前查找的比较符长度为一时，判断找到的比较符是否为真实的一长度运算符
						if(compares[i].length() == 1){
							boolean next = false;
							char a = where.charAt(beginindex-1);
							char b = where.charAt(beginindex+1);
							for(int k = 0; k < compares2.length; k++){
								if(a == compares2[k] || b == compares2[k]){
									next = true;
									break;
								}
							}
							if(next){
								fromindex++;
								continue;
							}
						}
						//查找字段名
						int columnendindex = beginindex;
						do {
							columnendindex--;
						} while(where.charAt(columnendindex)==' ');
						int columnbeginindex = where.lastIndexOf(' ', columnendindex-1);
						/*
						if(columnbeginindex == -1){//后面始终会+1，所以这里不用修正
							columnbeginindex = 0;
						}
						*/
						while(where.charAt(columnbeginindex+1) == '('){
							columnbeginindex++;
						}
						String column = where.substring(columnbeginindex+1, columnendindex+1);
						//当前查找的比较符长度为二时，将起始指针移动到比较符最后一位
						if(compares[i].length() == 2){
							beginindex += compares[i].length()-1;
						}
						do {
							beginindex++;
						} while(where.charAt(beginindex)==' ');//过滤空格，直到遇到正确内容
						//查找被替换内容终止位置+1，截取字符串时最后一位不被截取
						int endindex = 0;
						if(where.charAt(beginindex)=='\''){//截取内容为字符串时，以'判断结束位置
							endindex = where.indexOf("'", beginindex+1)+1;
						} else 
						if(where.substring(beginindex).toLowerCase().startsWith("to_date")){//时间日期格式to_date('','')
							beginindex += 8;
							endindex = where.indexOf("'", beginindex+1)+1;
						} else {//截取内容为非字符串时，以空格判断结束位置
							endindex = where.indexOf(" ", beginindex);
						}
						//判断值为详细时间时，跳过时间的空格
						if(where.charAt(beginindex)!='\'' && endindex!=-1 && beginindex+4<where.length() && (where.charAt(beginindex+4)=='-' || where.charAt(beginindex+4)=='/')){
							int timeindex = where.indexOf(":", endindex);
							if(timeindex != -1 && timeindex <= endindex+3){//下标在合理的出现范围内
								endindex = where.indexOf(" ", endindex+1);
							}
						}
						//结束位置为-1时，修正为字符串末尾
						if(endindex == -1){
							endindex = where.length();
						}
						//结束位置的前一位为括号时，修正，直到移动到正确内容
						while(where.charAt(endindex-1) == ')'){
							endindex--;
						}
						//截取值
						String value = where.substring(beginindex, endindex);
						//修正下次查找字符串的起始位置
						fromindex = endindex-value.length();
						//确定数据类型
						byte valType = 0;
						if(value.charAt(0) == '\''){//被截取值为字符串时，过滤'
							valType = 1;
						} else 
						if(value.indexOf(':') != -1){//被截取内容为非字符串时，正常使用
							valType = 2;
						} else 
						if((value.indexOf('-') != 0 && value.indexOf('-') != -1) || value.indexOf('/') != -1 || value.charAt(0)=='?'){
							valType = 3;
						} else 
						if(value.equals("?")){
							valType = 4;
						} else 
						if(value.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")){
							valType = 5;
						} else {
							continue;
						}
						//封装对象
						CData cdata = new CData();
						cdata.valType = valType;
						cdata.index = beginindex;
						cdata.column = column;
						cdata.value = value;
						cdata.compare = compares[i];
						cdata_arr.add(cdata);
						for(int t = 0; t < cdata_arr.size(); t++){
							CData obj = cdata_arr.get(t);
							if(obj.index > beginindex){
								obj.index -= value.length()-1;
							}
						}
						//System.out.println("column="+column+" value="+value);
						//重新拼接，更新语句
						StringBuffer sb = new StringBuffer();
						sb.append(where.substring(0, beginindex));
						sb.append("?");
						sb.append(where.substring(endindex, where.length()));
						where = sb.toString();
					}
				}
				CData[] arr = cdata_arr.toArray(new CData[cdata_arr.size()]);
				Tools.sort(arr, 0);
				for(int i = 0; i < arr.length; i++){
					if(arr[i].valType == 1){//被截取值为字符串时，过滤'
						wStr.add(arr[i].column, arr[i].value.substring(1, arr[i].value.length()-1), arr[i].compare);
					} else 
					if(arr[i].valType == 2){//被截取内容为非字符串时，正常使用
						wStr.addDateTime(arr[i].column, arr[i].value, arr[i].compare);
					} else 
					if(arr[i].valType == 3){
						wStr.addDate(arr[i].column, arr[i].value, arr[i].compare);
					} else 
					if(arr[i].valType == 4){
						wStr.add(arr[i].column, arr[i].value, arr[i].compare, SqlString.DATATYPE_CUSTOM);
					} else 
					if(arr[i].valType == 5){
						wStr.add(arr[i].column, arr[i].value, arr[i].compare, SqlString.DATATYPE_NUMBER);
					}
				}
			}
			/*
			long t2 = System.currentTimeMillis();
			System.out.println("--where2:"+where);
			System.out.println("--wStrCol:"+wStr.colString());
			System.out.println("--wStrVal:"+wStr.valueString());
			System.out.println("--转换用时：" + (t2-t1));
			System.out.println("↑↑↑--------------------------");
			*/
			return where;	
		} catch (Exception e) {
			System.out.println("解析条件语句失败，返回原始语句  where="+src_where);
			e.printStackTrace();
			return src_where;
		}
	}
	
	/**
	 * 转换数据包
	 * @author John
	 */
	static class CData implements Sortable {
		protected byte valType;
		protected int index;
		protected String column;
		protected String value;
		protected String compare;
		public double getSortValue() {return index;}
	}
	
	/**
	 * 获取规范型数据的字符串形式
	 */
	public static String getFormatStr(String tab, JSONArray jsonarr) {
		try {
			if(jsonarr == null){
				return "";
			}
			JSONObject colobj = colmap.optJSONObject(tab);
			JSONArray colarr = converColobjToArr(colobj);
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < colarr.length(); i++){
				sb.append(colarr.optString(i)+"\t");
			}
			sb.append("\r\n");
			//System.out.println("jsonarr.length():"+jsonarr.length()+" jsonarr:"+jsonarr+" tab:"+tab);
			for(int k = 0; k < jsonarr.length(); k++){
				JSONArray arr = jsonarr.optJSONArray(k);
				for(int i = 0; i < colarr.length(); i++){
					//System.out.println(" k:"+k+" i:"+i);
					sb.append((arr.opt(i)!=null?arr.opt(i):"无数据")+"\t");
				}
				sb.append("\r\n");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}
	
	/**
	 * 将COLOBJ转换为COLARR
	 */
	public static JSONArray converColobjToArr(JSONObject colobj) throws Exception {
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = colobj.keys();
		JSONArray colarr = new JSONArray();
		while(iterator.hasNext()){
			String col = iterator.next();
			colarr.put(colobj.optInt(col), col);
		}
		return colarr;
	}
	
	/**
	 * 查询
	 */
	public static JsonRs sQuery(String table, String target, String where) throws Exception {
		return sQuery(table, target, where, null, null, 0, 0);
	}
	
	/**
	 * 查询
	 */
	public static JsonRs sQuery(String table, String target, String where, String order, String group, int minRow, int maxRow) throws Exception {
		DBHelper dbHelper = new DBHelper();
		try {
			dbHelper.openConnection();
			ResultSet rs = dbHelper.query(table, target, where, order, group, minRow, maxRow);
			return convertRsToJsonRs(rs);
		} catch (Exception e) {
			throw e;
		} finally {
			dbHelper.closeConnection();
		}
	}
	
	/**
	 * 将Rs转换为JsonRs
	 */
	public static JsonRs convertRsToJsonRs(ResultSet rs) throws Exception {
		return new JsonRs(convertRsToJsonarr(rs));
	}
	
	/**
	 * 将RS转换为JSONARR
	 */
	public static JSONArray convertRsToJsonarr(ResultSet rs) throws Exception {
		JSONArray jsonarr = new JSONArray();
		ResultSetMetaData rsmd = rs.getMetaData();
		int colCount = rsmd.getColumnCount();
		if(rs.getRow() != -1){
			rs.beforeFirst();
		}
		while(rs.next()){
			JSONObject obj = new JSONObject();
			for (int i = 1; i <= colCount; i++) {
				String colName = rsmd.getColumnName(i);
				int colType = rsmd.getColumnType(i);
				if(colType == Types.BLOB){
					obj.put(colName, new String(rs.getBytes(i), "UTF-8"));
				} else 
				{
					obj.put(colName, rs.getString(i));
				}
			}
			jsonarr.add(obj);
		}
		return jsonarr;
	}
	
	/**
	 * 将RS转换为JSONOBJ
	 */
	public static JSONObject convertRsToJsonobj(ResultSet rs) throws Exception {
		JSONObject obj = null;
		ResultSetMetaData rsmd = rs.getMetaData();
		int colCount = rsmd.getColumnCount();
		if(rs.getRow() != -1){
			rs.beforeFirst();
		}
		if(rs.next()) {
			obj = new JSONObject();
			for (int i = 1; i <= colCount; i++) {
				String colName = rsmd.getColumnName(i);
				int colType = rsmd.getColumnType(i);
				if(colType == Types.BLOB){
					obj.put(colName, new String(rs.getBytes(i), "UTF-8"));
				} else 
				{
					obj.put(colName, rs.getString(i));
				}
			}
		}
		return obj;
	}
	
	public static JSONObject colmap = new JSONObject();
	public static JSONObject coltypemap = new JSONObject();
	
	/**
	 * 清理字段信息
	 */
	public static void clearColData(String tab){
		if(colmap.has(tab)){
			colmap.remove(tab);
			coltypemap.remove(tab);
		}
	}
	
	/**
	 * 将RS转换为规范型JSONARR(当前仅允许查询目标为NULL[*]的查询结果调用此方法)
	 */
	public static JSONArray convertRsToFormat(String tab, ResultSet rs) throws Exception {
		JSONArray jsonarr = new JSONArray();
		ResultSetMetaData rsmd = rs.getMetaData();
		int colCount = rsmd.getColumnCount();
		if(rs.getRow() != -1){
			rs.beforeFirst();
		}
		if(!colmap.has(tab)){
			JSONObject colobj = new JSONObject();
			JSONArray coltypearr = new JSONArray();
			for(int i = 1; i <= colCount; i++){
				String colName = rsmd.getColumnName(i);
				int colType = rsmd.getColumnType(i);
				colobj.put(colName, i-1);
				coltypearr.add(colType);
			}
			colmap.put(tab, colobj);
			coltypemap.put(tab, coltypearr);
		}
		while(rs.next()){
			JSONArray arr = new JSONArray();
			for (int i = 1; i <= colCount; i++) {
				int colType = rsmd.getColumnType(i);
				if(colType == Types.BLOB){
					arr.add(new String(rs.getBytes(i), "UTF-8"));
				} else 
				if(colType == Types.DATE ||colType == Types.TIME || colType == Types.TIMESTAMP){
					String val = rs.getString(i);
					if(val == null){
						arr.add(null);
					} else {
						arr.add(MyTools.getTimeLong(val));
					}
				} else 
				if(colType == Types.NUMERIC) {
					String val = rs.getString(i);
					if(val == null){
						arr.add(0);
					} else {
						arr.add(val);
					}
				} else 
				{
					arr.add(rs.getString(i));
				}
			}
			jsonarr.add(arr);
		}
		return jsonarr;
	}
	
	private static final String[] Q_COMP_SPLIT = {" is ", ">=", "<=", "!=", "=", ">", "<"};//拆分符号
	private static final String[] Q_COMP_USE = {"=", ">=", "<=", "!=", "=", ">", "<"};//实际使用符号
	
	private static MyLog jsonQueryLog = new MyLog(MyLog.NAME_DATE, "jsonquery", "JSONQUERY", true, false, true, null);
	
	/**
	 * 基于JSON数据源的SQL查询
	 */
	public static JSONArray jsonQuery(String tab, JSONArray jsonarr, String where, String order, int minRow, int maxRow) throws Exception {
		long t1 = System.currentTimeMillis();
		int len = jsonarr.length();
		JSONObject colobj = colmap.optJSONObject(tab);
		JSONArray coltype = coltypemap.optJSONArray(tab);
		jsonarr = jsonQuery(colobj, coltype, jsonarr, where, order, minRow, maxRow);
		long t2 = System.currentTimeMillis();
		if(t2-t1>5){
			jsonQueryLog.d("JSON查询用时："+(t2-t1)+" 表："+tab+" 条件："+where+" 数据量:"+len);
		}
		return jsonarr;
	}
	
	/**
	 * 基于JSON数据源的SQL查询
	 * !!!备注：被查询的数据集如果有时间类型的字段，对应值必须为毫秒形式!!!
	 */
	public static JSONArray jsonQuery(JSONObject colobj, JSONArray coltype, JSONArray jsonarr, String where, String order, int minRow, int maxRow) throws Exception {
		//System.out.println("src:\r\n"+MyTools.getFormatJsonarrStr(jsonarr));
		if(where != null){
			//where = where.toLowerCase();
			JSONArray rootarr = new JSONArray();//条件分解结果
			//["and",["and","serverid=1"]]
			//["and",["and","serverid=1","savetime>2015-03-08 23:56:55"]]
			splitBracket(rootarr, rootarr, where, 1);
			//{"resetdate":26,"sex":19,"buycoin":9...}
			//System.out.println("where:"+rootarr);
			//[2,2,2,93,12...]
			//System.out.println("colobj:"+colobj);
			//System.out.println("coltype:"+coltype);
			//索引查询
			boolean needCheckWhere = true;//是否需要进行普通查询
			if(rootarr.optString(0).equals("and")){
				JSONArray arr1 = rootarr.optJSONArray(1);
				if(arr1.optString(0).equals("and")){//第一个条件关系为AND条件
					Object obj = null;
					while((obj = arr1.opt(1)) instanceof String){//第一个条件非嵌套条件
						String subWhere = obj.toString();//获取条件字符串
						//System.out.println(subWhere);
						JSONArray temparr2 = new JSONArray();
						//long t1 = System.currentTimeMillis();
						//long tx = 0;
						//long ty = 0;
						String[] spWhere = splitSubWhere(colobj, coltype, subWhere);//切割子条件
						//savetime > 1425830215000
						//System.out.println(spWhere[0]+" "+spWhere[1]+" "+spWhere[2]);
						int col_index = colobj.optInt(spWhere[0]);//获取条件字段下标
						for(int i = 0; i < jsonarr.length(); i++){//循环查询目标集合
							JSONArray json = jsonarr.getJSONArray(i);//获取查询目标
							//long ta1 = System.currentTimeMillis();
							String value = json.optString(col_index);//获取条件目标值
							//long ta2 = System.currentTimeMillis();
							boolean match = checkSubWhere(colobj, coltype, spWhere, value);//条件匹配结果
							//long ta3 = System.currentTimeMillis();
							//tx += ta2-ta1;
							//ty += ta3-ta2;
							if(match){
								temparr2.add(json);//将匹配的数据加入到临时集合
							}
						}
						//long t2 = System.currentTimeMillis();
						//System.out.println("抓取用时："+(t2-t1)+" 判断用时："+ty+" 取值用时："+tx);
						jsonarr = temparr2;//确认查询结果
						arr1.remove(1);//将此条件从查询条件中移除
						if(arr1.length() <= 1){//如果只剩下一个AND，查询结束，不需要在进行普通查询
							needCheckWhere = false;
							break;
						}
					}
				}
			}
			//System.out.println("rootarr:"+rootarr);
			//普通查询
			if(needCheckWhere){
				JSONArray temparr = new JSONArray();
				for(int i = 0; i < jsonarr.length(); i++){//遍历查询目标集合
					JSONArray json = jsonarr.getJSONArray(i);//获取查询目标
					JSONArray userootarr = new JSONArray(rootarr.toString());//生成查询条件
					boolean match = checkWhere(colobj, coltype, json, userootarr, userootarr, 1);
					if(match){
						temparr.add(json);
					}
				}
				jsonarr = temparr;
				//System.out.println("where:\r\n"+MyTools.getFormatJsonarrStr(jsonarr));		
			}
		} else {
			JSONArray temparr = new JSONArray();
			for(int i = 0; i < jsonarr.length(); i++){
				JSONArray json = jsonarr.getJSONArray(i);
				temparr.add(json);
			}
			jsonarr = temparr;
			//System.out.println("where:\r\n"+MyTools.getFormatJsonarrStr(jsonarr));
		}
		if(order != null){
			String[][] groups = Tools.splitStrToStrArr2(order, ",", " ");
			for(int i = 0; i < groups.length; i++){
				order = order.replace(groups[i][0], colobj.optString(groups[i][0]));
			}
			jsonarr = MyTools.sortJSONArray(jsonarr, order);
			//System.out.println("order:\r\n"+MyTools.getFormatJsonarrStr(jsonarr));
		}
		if(minRow>0 && maxRow>0){
			if(minRow <= maxRow){
				JSONArray temparr = new JSONArray();
				for(int i = 0; i < jsonarr.length(); i++){
					if(i >= (minRow-1) && i <= (maxRow-1)){
						temparr.add(jsonarr.optJSONArray(i));
					}
				}
				jsonarr = temparr;
			} else {
				BACException.throwInstance("查询行号参数错误："+minRow+","+maxRow);
			}
			//System.out.println("row:\r\n"+MyTools.getFormatJsonarrStr(jsonarr));
		}
		return jsonarr;
	}
	
	/**
	 * 分解条件
	 */
	private static void splitBracket(JSONArray rootarr, JSONArray jsonarr, String where, int nestlayer) throws Exception {
		if(where == null){
			return;
		}
		JSONArray subarr = new JSONArray();
		while(true){
			int startindex = where.indexOf('(');
			if(startindex == - 1){
				break;
			}
			int endindex = 0;
			int layer = 0;
			int fromindex = startindex+1;
			while(true){
				int start = where.indexOf('(', fromindex);
				int end = where.indexOf(')', fromindex);
				
				int index = 0;
				if(start == -1 && end == -1){
					throw new Exception("解析条件失败");
				} else 
				if(start == -1){
					index = end;
				} else 
				if(end == -1){
					index = start;
				} else 
				{
					index = Math.min(start, end);
				}
				if(index == start){
					layer++;
				} else 
				if(index == end){
					if(layer > 0){
						layer--;
					} else {
						endindex = index;
						break;
					}
				}
				fromindex = index+1;
			}
			String leftStr = null;
			String rightStr = null;
			if(startindex != 0){
				leftStr = where.substring(0, startindex);
			} else {
				leftStr = "";
			}
			if(endindex != where.length()-1){
				rightStr = where.substring(endindex+1);
			} else {
				rightStr = "";
			}
			JSONArray thearr = new JSONArray();
			splitBracket(rootarr, thearr, where.substring(startindex+1, endindex), nestlayer+1);
			subarr.add(thearr);
			where = leftStr + "?" + rightStr;
			//System.out.println("where:"+where+" "+nestlayer);
			//System.out.println("subarr:"+subarr+" "+nestlayer);
		}
		int use = 0;
		String[] orwhere = where.split("\\s+(o|O)(r|R)\\s+");
		if(orwhere.length > 1){
			jsonarr.add("or");
		} else {
			jsonarr.add("and");
		}
		for(int k = 0; orwhere != null && k < orwhere.length; k++){
			String[] subwhere = orwhere[k].split("\\s+(a|A)(n|N)(d|D)\\s+");
			JSONArray onearr = new JSONArray();
			onearr.add("and");
			for(int i = 0; i < subwhere.length; i++){
				if(subwhere[i].equals("?")){
					onearr.add(subarr.opt(use++));
				} else {
					onearr.add(subwhere[i]);
				}
			}
			jsonarr.add(onearr);
		}
		//System.out.println("aft jsonarr:"+jsonarr+" "+nestlayer);
	}
	
	/**
	 * 检查是否满足条件
	 */
	private static boolean checkWhere(JSONObject colobj, JSONArray coltype, JSONArray json, JSONArray rootarr, JSONArray jsonarr, int nestlayer) throws Exception {
		for(int i = 0; i < jsonarr.size(); i++){
			Object obj = jsonarr.opt(i);
			if(obj instanceof JSONArray){
				boolean meet = checkWhere(colobj, coltype, json, rootarr, (JSONArray)obj, nestlayer+1);
				jsonarr.put(i, meet);
			}
		}
		String connnect = jsonarr.optString(0);
		boolean result = true;
		for(int i = 1; i < jsonarr.size(); i++){
			String str = jsonarr.optString(i);
			boolean meet = checkSubWhere(colobj, coltype, json, str);
			if(connnect.equals("and")){
				if(!meet){
					result = false;
					break;
				} else {
					result = true;
				}
			} else 
			if(connnect.equals("or")){
				if(meet){
					result = true;
					break;
				} else {
					result = false;
				}
			}
		}
		//System.out.println("the b:"+rootarr+" layer:"+nestlayer);
		return result;
	}
	
	/**
	 * 检查是否满足子条件
	 */
	private static boolean checkSubWhere(JSONObject colobj, JSONArray coltype, JSONArray json, String where) throws Exception {
		if(where.equals("true")){
			return true;
		} else 
		if(where.equals("false")){
			return false;
		} else {
			String[] spWhere = splitSubWhere(colobj, coltype, where);//切割条件
			String value = json.optString(colobj.optInt(spWhere[0]));//获取数据值
			return checkSubWhere(colobj, coltype, spWhere, value);//检查是否满足条件
		}
	}
	
	/**
	 * 检查是否满足子条件
	 * @param colobj 字段名-字段所在下标
	 * @param coltype 字段类型
	 * @param column 字段名
	 * @param value 数据值(非条件值)
	 */
	private static boolean checkSubWhere(JSONObject colobj, JSONArray coltype, String[] spWhere, String value) throws Exception {
		if(value.equals("")){
			int type = coltype.optInt(colobj.optInt(spWhere[0]));
			if(type == Types.DATE || type == Types.TIME || type == Types.TIMESTAMP){
				value = "0";
			} else 
			if(type == Types.NUMERIC){
				value = "0";
			}
		}
		String val1 = value;//数据值 converValue(colobj, coltype, spWhere[0], value)
		String val2 = spWhere[2];//条件值
		String comp = spWhere[1];//判断条件
		if(comp.equals("=")){
			return val1.equals(val2);
		} else
		if(comp.equals("!=")){
			return !val1.equals(val2);
		} else 
		if(comp.equals(">=")){
			return Double.valueOf(val1)>=Double.valueOf(val2);
		} else 
		if(comp.equals("<=")){
			return Double.valueOf(val1)<=Double.valueOf(val2);
		} else 
		if(comp.equals(">")){
			return Double.valueOf(val1)>Double.valueOf(val2);
		} else
		if(comp.equals("<")){
			return Double.valueOf(val1)<Double.valueOf(val2);
		}
		throw new RuntimeException("意外的条件运算符："+comp);
	}
	
	/**
	 * 切割子条件
	 * @param colobj 字段名-字段所在下标
	 * @param coltype 字段类型
	 * @param where 条件
	 * @return 拆分结果
	 */
	public static String[] splitSubWhere(JSONObject colobj, JSONArray coltype, String where) throws Exception {
		String[] spWhere = new String[3];//拆分结果
		for(int c = 0; c < Q_COMP_SPLIT.length; c++){
			if(where.indexOf(Q_COMP_SPLIT[c])!=-1){
				String[] kv = Tools.splitStr(where, Q_COMP_SPLIT[c]);
				spWhere[0] = kv[0];
				spWhere[1] = Q_COMP_USE[c];
				spWhere[2] = converValue(colobj, coltype, kv[0], kv[1]);
				break;
			}
		}
		return spWhere;
	}
	
	/**
	 * 转换值
	 * @param colobj 字段名-字段所在下标
	 * @param coltype 字段类型
	 * @param column 字段名
	 * @param value 字段值
	 */
	public static String converValue(JSONObject colobj, JSONArray coltype, String column, String value) throws Exception {
		if(!colobj.has(column)){
			BACException.throwAndPrintInstance("无效标识符“"+column+"”");
		}
		int type = coltype.optInt(colobj.optInt(column));
		if(type == Types.VARCHAR || type == Types.NVARCHAR){
			if(value.equals("null")){
				value = "";
			} else 
			if(value!=null && !value.equals("") && value.charAt(0)=='\''){//不能放在外面去引号，否则null字符串将被转换为""
				value = value.substring(1, value.length()-1);//去除字符串包容的引号
			}
		} else 
		if(type == Types.DATE || type == Types.TIME || type == Types.TIMESTAMP){
			value = String.valueOf(MyTools.getTimeLong(value));
		} else 
		if(type == Types.NUMERIC){
			if(value == null || value.equals("null") || value.equals("")){
				value = String.valueOf(0);
			} else 
			if(value!=null && !value.equals("") && value.charAt(0)=='\''){//不能放在外面去引号，否则null字符串将被转换为""
				value = value.substring(1, value.length()-1);//去除字符串包容的引号
			}
		}
		return value;
	}
	
	public static void main(String[] args){
		//第一次用时较多可能是因为要载入类
		String where = "playerid=792 and (starttime=2013-07-19 09:20:00 or starttime=2013-07-19 00:00:00)";
		//String where = "table_name=upper('tab_partner') order by column_position";
		//for(int i = 0; i < 10; i++)
		{
			long t1 = System.currentTimeMillis();
			System.out.println("where1:"+where);
			SqlString wStr = new SqlString();
			where = convertWhere(where, wStr);
			
			long t2 = System.currentTimeMillis();
			
			System.out.println("where2:"+where);
			System.out.println("wStr:"+wStr.whereString());
			System.out.println("转换条件用时： " + (t2-t1));
		}
	}
}
