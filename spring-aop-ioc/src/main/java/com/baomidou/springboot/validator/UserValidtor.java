package com.baomidou.springboot.validator;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.baomidou.springboot.entity.User;

public class UserValidtor implements Validator {

	/**
	 * 判断支持的JavaBean类型
	 * 
	 * @param aClass
	 * @return
	 */
	@Override
	public boolean supports(Class<?> aClass) {
		return User.class.equals(aClass);
	}

	/**
	 * 实现Validator中的validate接口
	 * 
	 * @param obj
	 * @param errors
	 */
	@Override
	public void validate(Object obj, Errors errors) {
		// 把校验信息注册到Error的实现类里
		ValidationUtils.rejectIfEmpty(errors, "name", null, "姓名不能为空!");
		User user = (User) obj;
		if (StringUtils.isEmpty(user.getName())) {
			errors.rejectValue("otherName", null, "再提示一次不能为空");
		}
	}
}
