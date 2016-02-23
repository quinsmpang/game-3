package com.moonic.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.moonic.bac.SystemFolderBAC;
import com.moonic.util.StreamHelper;

public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = -1921508287315688709L;
	
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String path = request.getParameter("path");
			String pack = request.getParameter("pack");
			if (path != null) {
				if (pack != null) {
					if (pack.equals("1")) {
						SystemFolderBAC.getInstance().zipFolderAndDownload(response, path);
					}
				} else {
					StreamHelper.getInstance().download(response, path);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}