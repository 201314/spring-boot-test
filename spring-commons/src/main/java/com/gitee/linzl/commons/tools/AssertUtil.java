package com.gitee.linzl.commons.tools;

import com.gitee.linzl.commons.enums.IBaseErrorCode;
import com.gitee.linzl.commons.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * copy from org.springframework.util.Assert
 * <p>
 * 不符合表达式结果，则抛异常
 *
 * @author linzhenlie
 * @date 2020/7/21
 */
@Slf4j
public abstract class AssertUtil {
    public static void isTrue(boolean expression, IBaseErrorCode message) {
        if (!expression) {
            throw new BusinessException(message);
        }
    }

    public static void isTrue(boolean expression, IBaseErrorCode message, String printLog) {
        if (!expression) {
            log.error(printLog);
            throw new BusinessException(message);
        }
    }

    public static void isNull(Object object, IBaseErrorCode message) {
        if (Objects.nonNull(object)) {
            throw new BusinessException(message);
        }
    }

    public static void isNull(Object object, IBaseErrorCode message, String printLog) {
        if (Objects.nonNull(object)) {
            log.error(printLog);
            throw new BusinessException(message);
        }
    }

    public static void notNull(Object object, IBaseErrorCode message) {
        if (Objects.isNull(object)) {
            throw new BusinessException(message);
        }
    }

    public static void notNull(Object object, IBaseErrorCode message, String printLog) {
        if (Objects.isNull(object)) {
            log.error(printLog);
            throw new BusinessException(message);
        }
    }

    public static void notEmpty(String text, IBaseErrorCode message) {
        if (!StringUtils.hasLength(text)) {
            throw new BusinessException(message);
        }
    }

    public static void notEmpty(String text, IBaseErrorCode message, String printLog) {
        if (!StringUtils.hasLength(text)) {
            log.error(printLog);
            throw new BusinessException(message);
        }
    }

    public static void notEmpty(Object[] array, IBaseErrorCode message) {
        if (ObjectUtils.isEmpty(array)) {
            throw new BusinessException(message);
        }
    }

    public static void notEmpty(Object[] array, IBaseErrorCode message, String printLog) {
        if (ObjectUtils.isEmpty(array)) {
            log.error(printLog);
            throw new BusinessException(message);
        }
    }

    public static void notEmpty(Collection<?> collection, IBaseErrorCode message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(message);
        }
    }

    public static void notEmpty(Collection<?> collection, IBaseErrorCode message, String printLog) {
        if (CollectionUtils.isEmpty(collection)) {
            log.error(printLog);
            throw new BusinessException(message);
        }
    }

    public static void notEmpty(Map<?, ?> map, IBaseErrorCode message) {
        if (CollectionUtils.isEmpty(map)) {
            throw new BusinessException(message);
        }
    }

    public static void notEmpty(Map<?, ?> map, IBaseErrorCode message, String printLog) {
        if (CollectionUtils.isEmpty(map)) {
            log.error(printLog);
            throw new BusinessException(message);
        }
    }

    public static void noNullElements(Object[] array, IBaseErrorCode message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new BusinessException(message);
                }
            }
        }
    }
}
