package com.gitee.linzl.commons.api;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公共基础实体
 *
 * @author linzhenlie
 * @date 2019/8/26
 */
@Setter
@Getter
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = -2179140352630663047L;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 创建者
     */
    private String createdBy;
    /**
     * 修改时间
     */
    private LocalDateTime updatedTime;
    /**
     * 更新者
     */
    private String updatedBy;

}
