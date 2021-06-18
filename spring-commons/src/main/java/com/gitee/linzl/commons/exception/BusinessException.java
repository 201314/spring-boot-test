package com.gitee.linzl.commons.exception;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.enums.IBaseErrorCode;

/**
 * 业务异常
 *
 * @author linzhenlie
 * @date 2019/8/26
 */
public class BusinessException extends ServiceException {
    private Object data;

    public BusinessException(IBaseErrorCode errorEnum) {
        super(errorEnum);
    }

    public BusinessException(IBaseErrorCode errorEnum, Object data) {
        super(errorEnum);
        this.data = data;
    }

    public BusinessException(ApiResult api) {
        super(api);
        this.data = data;
    }
}
