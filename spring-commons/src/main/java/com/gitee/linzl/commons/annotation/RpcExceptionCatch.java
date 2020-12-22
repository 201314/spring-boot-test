package com.gitee.linzl.commons.annotation;

import java.lang.annotation.*;

/**
 * 远程接口，统一异常包装，用于类上，且返回结果必须是com.gitee.linzl.commons.api.ApiResult类型
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
public @interface RpcExceptionCatch {

}
