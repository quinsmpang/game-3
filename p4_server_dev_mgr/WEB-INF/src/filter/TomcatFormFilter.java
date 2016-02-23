package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * 过滤器
 * @author alexhy
 */
public class TomcatFormFilter implements Filter {
	
	public void init(FilterConfig filterConfig) throws ServletException {

	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		//HttpServletRequest httpreq = (HttpServletRequest) request;
		/*if (httpreq.getMethod().equals("POST")) 
		{
			request.setCharacterEncoding("UTF-8");
		} else {
			request = new Request(httpreq);
		}*/
		request.setCharacterEncoding("UTF-8");		
		chain.doFilter(request, response);
	}
	
	public void destroy() {

	}
	
	//-------------------内部类-------------------
	
	/**
	 * 对 HttpServletRequestWrapper 进行扩充, 不影响原来的功能并能提供所有的 HttpServletRequest接口中的功能. 它可以统一的对 TOMCAT 默认设置下的中文问题进行解决而只需要用新的 Request 对象替换页面中的request 对象即可. 
	 */
	/*class Request extends HttpServletRequestWrapper 
	{
		
		*//**
		 * 构造
		 *//*
		public Request(HttpServletRequest request) {
			super(request);
		}

		*//**
		 * 转换由表单读取的数据的内码. 从 ISO 字符转到 GBK.
		 *//*
		public String toChi(String input) {
			try {
				byte[] bytes = input.getBytes("ISO8859-1");
				return new String(bytes, "UTF-8");
			} catch (Exception ex) {
			}
			return null;
		}

		*//**
		 * Return the HttpServletRequest holded by this object.
		 *//*
		private HttpServletRequest getHttpServletRequest() {
			return (HttpServletRequest) super.getRequest();
		}

		*//**
		 * 
		 * 读取参数 -- 修正了中文问题.
		 *//*
		public String getParameter(String name) {
			return toChi(getHttpServletRequest().getParameter(name));
		}

		*//**
		 * 读取参数列表 - 修正了中文问题.
		 *//*
		public String[] getParameterValues(String name) {
			String values[] = getHttpServletRequest().getParameterValues(name);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					values[i] = toChi(values[i]);
				}
			}
			return values;
		}
	}*/
}
