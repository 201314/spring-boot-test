package com.gitee.linzl.commons.validator;

import com.gitee.linzl.commons.tools.IdCardUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 身份证验证
 *
 * @author linzhenlie
 * @date 2019/8/27
 */
public class IDCardValidator implements ConstraintValidator<IDCard, String> {

    @Override
    public void initialize(IDCard sensitiveWord) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return IdCardUtil.isValidatedIdcard(value);
    }
}