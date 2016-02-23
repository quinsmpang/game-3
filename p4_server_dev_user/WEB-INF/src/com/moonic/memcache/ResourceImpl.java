package com.moonic.memcache;

import java.net.URL;

public class ResourceImpl extends AbstractResource {

	public URL locateResource(String resourceName) {
		return this.getResourceURL(resourceName, 
				ClassLoaderUtil.getDefaultSystemClassLoader());
	}

	public URL locateResource(String resourceName, ClassLoader loader) {
		return this.getResourceURL(resourceName, loader);
	}

	public URL locateResource(String resourceName, Class<?> targetClazz) {
		return this.getResourceURL(resourceName, 
				ClassLoaderUtil.getClassLoader(targetClazz));
	}		

}
