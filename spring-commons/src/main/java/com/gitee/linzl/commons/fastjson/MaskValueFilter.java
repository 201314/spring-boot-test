package com.gitee.linzl.commons.fastjson;

import com.alibaba.fastjson.serializer.ValueFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author linzhenlie
 * @date 2019/10/8
 */
@Slf4j
public class MaskValueFilter implements ValueFilter {
    @Override
    public Object process(Object object, String name, Object value) {
        if (Objects.isNull(value) || !(value instanceof String) || ((String) value).length() == 0) {
            return value;
        }

        try {
            Field field = object.getClass().getDeclaredField(name);
            if (String.class != field.getType()) {
                return value;
            }

            MaskSensitiveField desensitization = field.getAnnotation(MaskSensitiveField.class);
            String columnVal = String.valueOf(value);
            if (Objects.nonNull(desensitization)) {
                int maxLength = desensitization.max();
                /**
                 * 匹配到规则，先使用规则脱敏
                 */
                String padding = desensitization.padding();
                String regex = desensitization.regex();
                if (StringUtils.isNotEmpty(regex)) {
                    columnVal = columnVal.replaceAll(regex, padding);
                    return columnVal.length() > maxLength ? columnVal.substring(0, maxLength) : columnVal;
                }

                int left = desensitization.left();
                int right = desensitization.right();
                if (left > 0 || right > 0) {
                    columnVal = replace(columnVal, left, right, padding);
                    return columnVal.length() > maxLength ? columnVal.substring(0, maxLength) : columnVal;
                }
            }

            /**
             * 默认规则
             */
            MaskMobile mobileAnnotation = field.getAnnotation(MaskMobile.class);
            if (Objects.nonNull(mobileAnnotation)) {
                return replace(columnVal, mobileAnnotation.left(), mobileAnnotation.right(), mobileAnnotation.padding());
            }

            MaskIdCard idCardAnnotation = field.getAnnotation(MaskIdCard.class);
            if (Objects.nonNull(idCardAnnotation)) {
                return replace(columnVal, idCardAnnotation.left(), idCardAnnotation.right(), idCardAnnotation.padding());
            }

            MaskBankCard bankCardAnnotation = field.getAnnotation(MaskBankCard.class);
            if (Objects.nonNull(bankCardAnnotation)) {
                return replace(columnVal, bankCardAnnotation.left(), bankCardAnnotation.right(), bankCardAnnotation.padding());
            }

            MaskName nameAnnotation = field.getAnnotation(MaskName.class);
            if (Objects.nonNull(nameAnnotation)) {
                return replace(columnVal, nameAnnotation.left(), nameAnnotation.right(), nameAnnotation.padding());
            }
        } catch (NoSuchFieldException e) {
            log.warn("ValueDesensitizeFilter the class 【{}】 has no field 【{}】", object.getClass(), name);
        }
        return value;
    }

    private String replace(String columnVal, int left, int right, String padding) {
        if (left > 0 || right > 0) {
            return StringUtils.rightPad(StringUtils.left(columnVal, left), StringUtils.length(columnVal), padding)
                    .concat(StringUtils.right(columnVal, right));
        }
        return columnVal;
    }
}
