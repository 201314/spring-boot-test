package com.gitee.linzl.boot.autoconfigure.oss;

import com.gitee.linzl.oss.service.OSSService;
import com.gitee.linzl.oss.service.impl.QiniuServiceImpl;
import com.qiniu.cdn.CdnManager;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * additional-spring-configuration-metadata.json格式与spring-configuration-metadata.json一致，
 * <p>
 * 用于不使用@ConfigurationProperties注解的属性配置,可以参考mybatisplus-spring-boot-starter
 */
@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(OSSProperties.class)
@ConditionalOnProperty(prefix = OSSProperties.OSSPREFIX, name = "type", havingValue = "qiniu")
@Slf4j
public class QiniuOSSAutoConfiguration {
    private OSSProperties properties;

    public QiniuOSSAutoConfiguration(OSSProperties properties) {
        if (log.isDebugEnabled()) {
            log.debug("使用七牛云上传");
        }
        this.properties = properties;
    }

    @Bean
    public Auth auth() {
        return Auth.create(properties.getQiniu().getAccessKey(), properties.getQiniu().getSecretKey());
    }

    @Bean
    public Configuration config() {
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        // ...其他参数参考类注释
        return cfg;
    }

    @Bean
    public BucketManager bucketManager(Auth auth, Configuration cfg) {
        return new BucketManager(auth, cfg);
    }

    @Bean
    public CdnManager cdnManager(Auth auth) {
        CdnManager cdn = new CdnManager(auth);
        return cdn;
    }

    @Bean
    public OperationManager operationManager(Auth auth, Configuration cfg) {
        // 构建持久化数据处理对象
        return new OperationManager(auth, cfg);
    }

    @Bean
    public UploadManager uploadManager(Configuration cfg) {
        String localTempDir = Paths.get(System.getenv("java.io.tmpdir"), this.properties.getQiniu().getBucket()).toString();
        FileRecorder fileRecorder = null;
        try {
            //设置断点续传文件进度保存目录
            fileRecorder = new FileRecorder(localTempDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        UploadManager uploadManager = new UploadManager(cfg, fileRecorder);
        return uploadManager;
    }

    @Bean
    public OSSService ossService(Auth auth, UploadManager uploadManager) {
        return new QiniuServiceImpl(auth, uploadManager, properties);
    }
}
