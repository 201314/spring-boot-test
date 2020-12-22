package com.gitee.linzl.commons.gateway;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.tools.ApiResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("qihoo.sdk.marketing.test")
public class TestAction extends AbstractBaseAction {
	@Override
	public ApiResult<Map> internalHandle(Map requestMap) {
		log.info("测试action");
		try {
			return ApiResults.success(new HashMap<>());
		} catch (Exception e) {
			return ApiResults.fail();
		}
	}
}
