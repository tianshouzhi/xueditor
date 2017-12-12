package com.tianshouzhi.xueditor.upload;

import com.tianshouzhi.xueditor.define.State;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class Uploader {
	private HttpServletRequest request = null;
	private Map<String, Object> conf = null;
	private com.tianshouzhi.xueditor.Uploader uploadService;

	public Uploader(HttpServletRequest request, Map<String, Object> conf, com.tianshouzhi.xueditor.Uploader uploadService) {
		this.request = request;
		this.conf = conf;
		this.uploadService = uploadService;
	}

	public final State doExec() {
		String filedName = (String) this.conf.get("fieldName");
		State state = null;

		if ("true".equals(this.conf.get("isBase64"))) {
			state = Base64Uploader.save(this.request.getParameter(filedName),
					this.conf,uploadService);
		} else {
			state = BinaryUploader.save(this.request, this.conf,uploadService);
		}

		return state;
	}
}
