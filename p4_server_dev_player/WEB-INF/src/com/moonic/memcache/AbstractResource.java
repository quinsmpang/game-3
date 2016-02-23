package com.moonic.memcache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public abstract class AbstractResource {


	public abstract URL locateResource(String resourceName);
	
	public abstract URL locateResource(String resourceName, ClassLoader loader);
	

	public abstract URL locateResource(String resourceName, Class<?> targetClazz);
	
	public InputStream openInputStream(String resourceName) throws FileNotFoundException {
		URL resourceURL = locateResource(resourceName);
		FileInputStream	fis = new FileInputStream(resourceURL.getPath());
		return fis;
	}
	
	
	public URL getResourceURL(String resourceName, ClassLoader loader) {
		return loader.getResource(resourceName);
	}
}
