package com.gitee.linzl.commons.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * 敏感词验证
 *
 * @author linzhenlie
 * @date 2019/8/27
 */
public class SensitiveWordValidator implements ConstraintValidator<SensitiveWord, String> {
    @Value("${validator.sensitiveWords}")
    private static String sensitiveWords = "限量,秒杀,抽奖,全网最低,天猫,京东,淘宝";

    @Override
    public void initialize(SensitiveWord sensitiveWord) {
        //初始化，得到注解数据
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(sensitiveWords) || StringUtils.isEmpty(value)) {
            return true;
        }
        String[] sensitiveWordArr = sensitiveWords.split(",");
        String res = Arrays.stream(sensitiveWordArr).filter(target -> value.contains(target)).findFirst().orElse(null);
        return res == null;
    }
}