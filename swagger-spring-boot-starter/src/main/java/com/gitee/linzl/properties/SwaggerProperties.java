package com.gitee.linzl.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author linzhenlie
 * @date 2019/8/29
 */
@Data
@ConfigurationProperties(ignoreUnknownFields = false, prefix = "swagger")
public class SwaggerProperties {
    /**
     * 是否采用全局Token模式
     */
    private boolean globalToken;
    /**
     * API所在分组名
     */
    private String groupName;
    /**
     * 标题
     */
    private String title;
    /**
     * 详细描述
     */
    private String description;
    /**
     * 版本号
     */
    private String version = "1.0";
    /**
     * 作者名字
     */
    private String name;
    /**
     * 作者网站首页
     */
    private String url;
    /**
     * 作者联系邮箱
     */
    private String email;
}
