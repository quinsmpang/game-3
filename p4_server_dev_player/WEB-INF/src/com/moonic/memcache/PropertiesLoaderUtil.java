package com.moonic.memcache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import server.config.ServerConfig;

public class PropertiesLoaderUtil {

	/**
	 * º”‘ÿ≈‰÷√◊ ‘¥
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	public static Properties loadProperties(String resourceName)throws IOException 
	{
		File file = new File(ServerConfig.getWebInfPath()+"conf/"+resourceName);
		if(file.exists())
		{
			InputStream ins = new FileInputStream(file);
			/*AbstractResource resource = new ResourceImpl();
			InputStream ins = resource.openInputStream(resourceName);*/
			Properties props = new Properties();			
			props.load(ins);
			ins.close();
			return props;
		}
		else
		{
			return null;
		}
	}
}
