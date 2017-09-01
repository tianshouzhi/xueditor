package com.tianshouzhi.xueditor.servlet;

import com.tianshouzhi.xueditor.ActionEnter;
import com.tianshouzhi.xueditor.Uploader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class XUeditorServlet extends HttpServlet {
	public static final String UPLOADER_IMPL_KEY = "uploadImpl";
	private Uploader uploader;

	@Override
	public void init(ServletConfig config) throws ServletException {
		String uploadImpl = config.getInitParameter(UPLOADER_IMPL_KEY);
		if (uploadImpl != null) {
			try {
				Class<?> clazz = Class.forName(uploadImpl);
				Uploader uploader = (Uploader) clazz.newInstance();
				this.uploader = uploader;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/html");
		String rootPath=request.getSession().getServletContext().getRealPath("/");
		if(new File(rootPath).list().length==0){
			//处理spring boot case
			rootPath = this.getClass().getClassLoader().getResource("static").getFile();
		}
		String result = new ActionEnter(request, rootPath, uploader).exec();
		response.getWriter().write(result);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
