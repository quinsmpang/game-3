package com.moonic.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Cookie操作
 * @author 
 */
public class CookieUtil {
	
	/**
	 * 设置值
	 */
	public static void save(PageContext pageContext, String name, String value) {
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		try {
			Cookie cookie = new Cookie(name, URLEncoder.encode(value, "UTF-8"));
			cookie.setMaxAge(31104000);
			response.addCookie(cookie);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取值
	 */
	public static String get(PageContext pageContext, String name) {
		String value = null;
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Cookie[] cookies = request.getCookies();
		for (int i = 0; cookies != null && i < cookies.length; i++) {
			// System.out.println(cookies[i].getName()+"=" + URLDecoder.decode(cookies[i].getValue(),"UTF-8"));
			if (cookies[i].getName().equals(name)) {
				try {
					value = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
}
