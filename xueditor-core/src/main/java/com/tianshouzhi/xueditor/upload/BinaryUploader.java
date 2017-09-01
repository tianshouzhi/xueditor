package com.tianshouzhi.xueditor.upload;

import com.tianshouzhi.xueditor.PathFormat;
import com.tianshouzhi.xueditor.define.AppInfo;
import com.tianshouzhi.xueditor.define.BaseState;
import com.tianshouzhi.xueditor.define.FileType;
import com.tianshouzhi.xueditor.define.State;
import com.tianshouzhi.xueditor.Uploader;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BinaryUploader {


	public static final State save(HttpServletRequest request, Map<String, Object> conf,Uploader uploader) {
		FileItemStream fileStream = null;
		boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;

		if (!ServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}

		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

		if (isAjaxUpload) {
			upload.setHeaderEncoding("UTF-8");
		}

		try {
			FileItemIterator iterator = upload.getItemIterator(request);

			while (iterator.hasNext()) {
				fileStream = iterator.next();

				if (!fileStream.isFormField())
					break;
				fileStream = null;
			}

			if (fileStream == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}

			String savePath = (String) conf.get("savePath");
			String originFileName = fileStream.getName();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);
			State storageState = null;
			InputStream is = fileStream.openStream();
			if (uploader != null) {
				BaseState tempState = new BaseState(false);
				try {
					savePath = uploader.upload(is, savePath);
					tempState.setState(true);
					tempState.putInfo("size", is.available());
					tempState.putInfo("title", originFileName);
					storageState = tempState;
				} catch (Exception e) {
					storageState = new BaseState(false, AppInfo.IO_ERROR);
				}
			} else {
				String physicalPath = (String) conf.get("rootPath") + savePath;
				storageState = StorageManager.saveFileByInputStream(is, physicalPath, maxSize);
			}
			is.close();
			if (storageState.isSuccess()) {
				storageState.putInfo("url", PathFormat.format(savePath));
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}

			return storageState;
		} catch (FileUploadException e) {
			return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
}
