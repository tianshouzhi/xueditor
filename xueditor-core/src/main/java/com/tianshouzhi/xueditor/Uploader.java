package com.tianshouzhi.xueditor;

import java.io.InputStream;

/**
 * Created by tianshouzhi on 2017/9/1.
 */
public interface Uploader {
	public String upload(InputStream in, String key) throws Exception;
}
