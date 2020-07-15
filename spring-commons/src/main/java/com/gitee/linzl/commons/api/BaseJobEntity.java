package com.gitee.linzl.commons.api;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 基础跑批实体
 *
 * @author linzhenlie
 * @date 2019/8/26
 */
@Setter
@Getter
public class BaseJobEntity extends BaseEntity {
    /**
     * 跑批状态
     * 0跑批成功,1等待跑批,2跑批中,3跑批取消,4跑批失败
     */
    private int status;
    /**
     * 重试次数
     */
    private int retryCount;
    /**
     * 首次跑批时间
     */
    private LocalDateTime firstTime;
    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;
}
