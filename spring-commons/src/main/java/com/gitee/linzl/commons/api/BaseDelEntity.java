package com.gitee.linzl.commons.api;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author linzhenlie
 * @date 2019/9/9
 */
@Setter
@Getter
public class BaseDelEntity extends BaseEntity {
    /**
     * 0未删除，1已删除(假删除)
     */
    private Integer isDeleted;
    /**
     * 删除时间
     */
    private LocalDateTime deletedTime;
    /**
     * 删除者
     */
    private String deletedBy;
}
