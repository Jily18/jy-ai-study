package com.jy.study.common.ossfile;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.StorageClass;
import com.jy.study.common.config.OssProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Map;

@Component
public class OssClientUtil {
    private static final Logger log = LoggerFactory.getLogger(OssClientUtil.class);
    
    private static OSS ossClient;
    private static OssProperties ossProperties;
    
    @Autowired
    private OssProperties autowiredOssProperties;
    
    @PostConstruct
    public void init() {
        ossProperties = autowiredOssProperties;
        initOssClient();
    }
    
    private static void initOssClient() {
        try {
            log.info("正在初始化OSS客户端...");
            ossClient = new OSSClientBuilder().build(
                    ossProperties.getEndpoint(),
                    ossProperties.getAccessKeyId(),
                    ossProperties.getAccessKeySecret()
            );
            
            if (!ossClient.doesBucketExist(ossProperties.getBucketName())) {
                log.warn("Bucket {} does not exist", ossProperties.getBucketName());
            } else {
                log.info("OSS客户端初始化成功");
            }
        } catch (Exception e) {
            log.error("连接OSS客户端错误！", e);
        }
    }

    public static OSS getOSSClient() {
        if (ossClient == null) {
            log.info("OSS客户端未初始化，尝试重新初始化");
            initOssClient();
        }
        return ossClient;
    }

    public static void closeOSSClient() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("OSS客户端已关闭");
            ossClient = null;
        }
    }
    
    public static String getBucketName() {
        return ossProperties.getBucketName();
    }
    
    public static String getEndpoint() {
        return ossProperties.getEndpoint();
    }

    public static void main(String[] args) {
        try {
            // 获取 OSS 客户端实例
            OSS ossClient = OssClientUtil.getOSSClient();

            if (ossClient != null) {
                System.out.println("OSS 客户端连接成功！");
                // 测试bucket是否存在
                boolean exists = ossClient.doesBucketExist(OssClientUtil.getBucketName());
                System.out.println("Bucket存在: " + exists);
            } else {
                System.out.println("OSS 客户端连接失败！");
            }
        } catch (Exception e) {
            System.out.println("发生错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭 OSS 客户端
            OssClientUtil.closeOSSClient();
        }
    }
}


