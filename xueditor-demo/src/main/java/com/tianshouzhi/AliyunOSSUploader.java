package com.tianshouzhi;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.tianshouzhi.xueditor.Uploader;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class AliyunOSSUploader implements Uploader {
    private static final String ACCESS_PREFIX = "外网域名";
    private static String endpoint = "your endpoint";
    private static String accessKeyId = "your accessKeyId";
    private static String accessKeySecret = "your accessKeySecret";
    private static String bucketName = "bucketName";
    private static OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

    @Override
    public String upload(InputStream in, String path) throws Exception {
        String url = null;
        if (!ossClient.doesBucketExist(bucketName)) {
            createBucket();
        }
        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }
        byte[] bytes = IOUtils.toByteArray(in);
        PutObjectResult putObjectResult = ossClient.putObject(new PutObjectRequest(bucketName, path, new ByteArrayInputStream(bytes)));

        return ACCESS_PREFIX + path;
    }

    private void createBucket() {
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
        ossClient.createBucket(createBucketRequest);
    }
}
