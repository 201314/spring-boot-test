package com.gitee.linzl.commons.exception;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.enums.IBaseErrorCode;

/**
 * 服务异常
 *
 * @author linzhenlie
 * @date 2019/8/26
 */
public class ServiceException extends BaseException {

    public ServiceException(IBaseErrorCode errorEnum) {
        super(errorEnum.getCode(), errorEnum.getMsg());
    }

    public ServiceException(ApiResult api) {
        super(api.getCode(), api.getMsg());
    }
}
