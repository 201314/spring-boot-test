package com.gitee.linzl.commons.aop;

import com.gitee.linzl.commons.annotation.ParamValidator;
import com.gitee.linzl.commons.enums.BaseErrorCode;
import com.gitee.linzl.commons.tools.ApiResults;
import com.gitee.linzl.commons.tools.ValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author linzhenlie
 * @date 2020-05-11
 */
@Configuration
@Aspect
@Slf4j
public class ParamValidatorAspect {

    @Before("@annotation(com.gitee.linzl.commons.annotation.ParamValidator)")
    public Object before(JoinPoint point) {
        // 找出有注解的方法参数
        MethodSignature methodPoint = (MethodSignature) point.getSignature();
        Method method = methodPoint.getMethod();

        Object args;

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (int j = 0; j < parameterAnnotations[i].length; j++) {
                if (parameterAnnotations[i][j] instanceof ParamValidator) {
                    args = point.getArgs()[i];
                    Map<String, String> result = ValidatorUtil.validate(args);
                    log.info("参数校验结果:{}", result);
                    if (MapUtils.isNotEmpty(result)) {
                        return ApiResults.fail(BaseErrorCode.INVALID_PARAMETERS);
                    }
                }
            }
        }
        return null;
    }
}
