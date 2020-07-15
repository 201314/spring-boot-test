package com.gitee.linzl.commons.exception;

import com.gitee.linzl.commons.enums.IBaseErrorCode;

/**
 * 业务异常
 *
 * @author linzhenlie
 * @date 2019/8/26
 */
public class BusinessException extends ServiceException {

    public BusinessException(IBaseErrorCode errorEnum) {
        super(errorEnum);
    }
}
