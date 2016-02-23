package com.moonic.util;

import java.util.Vector;

public class Pool 
{
	private static Vector<PoolObj> vc;
	
	/**
	 * 加入对象到Pool，默认过期时长30秒
	 * @param id 对象id
	 * @param obj 要缓存的对象
	 */
	public static synchronized void addObjectToPool(String id,Object obj)
	{
		addObjectToPool(id,30,obj);
	}
	
	/**
	 * 加入对象到Pool
	 * @param id 对象id
	 * @param expireSecond 过期时长（秒）-1 不过期
	 * @param obj 要缓存的对象
	 */
	public static synchronized void addObjectToPool(String id,int expireSecond,Object obj)
	{
		if(obj==null)
		{
			return;
		}
		if(vc==null)		
		{
			vc = new Vector<PoolObj>();
		}	
		
		PoolObj poolObj = new PoolObj();
		poolObj.id = id;
		poolObj.time = System.currentTimeMillis();
		poolObj.expireSecond = expireSecond;
		poolObj.obj = obj;
		
		//遍历更新替换原来的
		for(int i=0;i<vc.size();i++)
		{
			PoolObj oldPoolObj = vc.elementAt(i);
			if(oldPoolObj.id.equals(id))
			{
				vc.remove(i);
				vc.add(poolObj);
				return;
			}
		}
		//加入新的
		vc.add(poolObj);		
	}
	/**
	 * 从缓存中移除
	 * @param id
	 */
	public static synchronized void removeObjectFromPoolById(String id)
	{
		for(int i=0;i<vc.size();i++)
		{
			PoolObj poolObj = vc.elementAt(i);
			if(poolObj.id.equals(id))
			{	
				vc.remove(poolObj);
				return;
			}
		}
	}
	/**
	 * 从Pool中获取缓存的对象，如果为空表示无此对象或对象已过期
	 * @param id 对象id
	 * @return
	 */
	public static synchronized Object getObjectFromPoolById(String id)
	{
		if(vc!=null)
		{
			for(int i=0;i<vc.size();i++)
			{
				PoolObj poolObj = vc.elementAt(i);
				if(poolObj.id.equals(id))
				{
					//判断时间
					if(System.currentTimeMillis()-poolObj.time < poolObj.expireSecond * 1000 || poolObj.expireSecond==-1)
					{
						return poolObj.obj;
					}
					else
					{
						//过期清除
						vc.remove(poolObj);
						return null;
					}
				}
			}
			return null;
		}
		else
		{
			return null;
		}
	}
	/**
	 * 缓存对象
	 */
	static class PoolObj
	{
		String id;
		long time; //存储时的毫秒数
		int expireSecond; //默认30秒 
		Object obj;
	}
}

