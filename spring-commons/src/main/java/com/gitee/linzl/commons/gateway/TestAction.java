package com.gitee.linzl.commons.gateway;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gitee.linzl.commons.api.ApiResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("qihoo.sdk.marketing.test")
public class TestAction extends AbstractBaseAction {
	@Override
	public ApiResult<Map> internalHandle(Map requestMap) {
		log.info("测试action");
		try {
			return ApiResult.success(new HashMap<>());
		} catch (Exception e) {
			return ApiResult.fail();
		}
	}
}
