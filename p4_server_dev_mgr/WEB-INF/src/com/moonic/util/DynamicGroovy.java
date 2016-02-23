package com.moonic.util;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.io.File;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * GROOVY对象
 * @author John
 */
public class DynamicGroovy {
	private GroovyObject groovyObject;
	
	private File groovyFile;
	
	private long lastModifiedTime;
	
	/**
	 * 构造
	 * @param scriptpath 脚本路径
	 */
	private DynamicGroovy(String scriptpath) throws Exception {
		groovyFile = new File(classRootpath+scriptpath);
		check_update();
		timer.scheduleAtFixedRate(new CheckUpdateTT(), 5000, 5000, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 获取属性值
	 * @param name 属性名 
	 * @return 属性值
	 */
	public Object getProperty(String name) throws Exception {
		return groovyObject.getProperty(name);
	}
	
	/**
	 * 执行方法
	 * @param methodName 方法名
	 * @param args 参数
	 * @return 返回值
	 */
	public Object invokeScriptMethod(String methodName, Object... args) throws Exception {
		return groovyObject.invokeMethod(methodName, args);
	}
	
	/**
	 * 更新
	 */
	private void check_update() throws Exception {
		if(groovyObject == null || lastModifiedTime != groovyFile.lastModified()){
			ClassLoader parent = getClass().getClassLoader();
			GroovyClassLoader loader = new GroovyClassLoader(parent);
			Class groovyClass = loader.parseClass(groovyFile);
			groovyObject = (GroovyObject) groovyClass.newInstance();
			lastModifiedTime = groovyFile.lastModified();
			Out.println("更新文件对象："+groovyFile.getName());
		}
	}
	
	/**
	 * 检查文件变化计时器
	 */
	class CheckUpdateTT extends TimerTask {
		public void run() {
			try {
				check_update();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//--------------静态区--------------
	
	private static String classRootpath;
	
	private static ScheduledExecutorService timer = MyTools.createTimer(2);
	
	private static HashMap<String, DynamicGroovy> groovytab = new HashMap<String, DynamicGroovy>();
	
	/**
	 * 设置class根目录
	 */
	public static void setClassRootPath(String path){
		classRootpath = path;
	}
	
	/**
	 * 获取GROOVY脚本对象
	 * @param John
	 */
	public static DynamicGroovy getInstance(String scriptpath){
		try {
			synchronized (groovytab) {
				if(classRootpath == null){
					BACException.throwAndPrintInstance("尚未设置CLASS根目录");
				}
				DynamicGroovy groovyobj = groovytab.get(scriptpath);
				if(groovyobj == null){
					groovyobj = new DynamicGroovy(scriptpath);
					groovytab.put(scriptpath, groovyobj);
				}
				return groovyobj;		
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		try {
			DynamicGroovy.setClassRootPath("");
			DynamicGroovy dynamicGroovy = getInstance("E:/workspace_3.7/GroovyAndJava/bin/hello.groovy");
			System.out.println(dynamicGroovy.getClass().getResource(""));
			Object result = dynamicGroovy.invokeScriptMethod("function1", "abc123123123");
			System.out.println(result);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}