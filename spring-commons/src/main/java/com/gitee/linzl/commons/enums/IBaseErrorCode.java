package com.gitee.linzl.commons.enums;

/**
 * @author linzhenlie
 * @date 2019/8/26
 */
public interface IBaseErrorCode {
    /**
     * 返回码
     */
    String getCode();

    /**
     * 英文提醒
     */
    String getEnMsg();

    /**
     * 中文提醒
     */
    String getMsg();
}
