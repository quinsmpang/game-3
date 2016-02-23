package com.moonic.memcache;

import java.net.MalformedURLException;
import java.net.URL;

public class ClassLoaderUtil {

	public static URL getResource(String resource) {
		return ClassLoaderUtil.getDefaultSystemClassLoader().getResource(
				resource);
	}

	public static String getAbsolutePathOfClassLoaderClassPath() {
		return ClassLoaderUtil.getDefaultSystemClassLoader().getResource("")
				.toString();

	}

	public static ClassLoader getDefaultSystemClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public static ClassLoader getClassLoader(Class<?> targetClazz) {
		if (targetClazz == null) {
			return getDefaultSystemClassLoader();
		}
		ClassLoader loader0 = targetClazz.getClassLoader();
		if (loader0 != null)
			return loader0;
		loader0 = ClassLoaderUtil.class.getClassLoader();
		if (loader0 != null)
			return loader0;
		return getDefaultSystemClassLoader();
	}

	public static URL getExtendResource(String relativePath)
			throws MalformedURLException {
		if (!relativePath.contains("../")) {
			return ClassLoaderUtil.getResource(relativePath);
		}
		String classPathAbsolutePath = ClassLoaderUtil
				.getAbsolutePathOfClassLoaderClassPath();
		if (relativePath.substring(0, 1).equals("/")) {
			relativePath = relativePath.substring(1);
		}
		String wildcardString = relativePath.substring(0,
				relativePath.lastIndexOf("../") + 3);
		relativePath = relativePath
				.substring(relativePath.lastIndexOf("../") + 3);
		int containSum = ClassLoaderUtil.containSum(wildcardString, "../");
		classPathAbsolutePath = ClassLoaderUtil.cutLastString(
				classPathAbsolutePath, "/", containSum);
		String resourceAbsolutePath = classPathAbsolutePath + relativePath;
		URL resourceAbsoluteURL = new URL(resourceAbsolutePath);
		return resourceAbsoluteURL;
	}

	private static int containSum(String source, String dest) {
		int containSum = 0;
		int destLength = dest.length();
		while (source.contains(dest)) {
			containSum = containSum + 1;
			source = source.substring(destLength);
		}
		return containSum;
	}

	private static String cutLastString(String source, String dest, int num) {
		// String cutSource=null;
		for (int i = 0; i < num; i++) {
			source = source.substring(0,
					source.lastIndexOf(dest, source.length() - 2) + 1);
		}
		return source;
	}
}
