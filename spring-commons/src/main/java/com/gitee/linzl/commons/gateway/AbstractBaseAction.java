package com.gitee.linzl.commons.gateway;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.api.BaseApi;
import com.gitee.linzl.commons.tools.UserClientUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractBaseAction {

	/**
	 * 处理网关controller分发过来的请求
	 * 
	 * @param requestInfo
	 * @return
	 */
	public ApiResult<Map> handle(BaseApi requestInfo, HttpServletRequest request) {
		String json = JSON.toJSONString(requestInfo);
		Map requestMap = JSONObject.parseObject(json).getInnerMap();
		String clientRealIp = UserClientUtil.getInstance().getIp(request);
		String remoteAddr = request.getRemoteAddr();
		log.debug("获取终端IP信息: clientRealIp:【{}】, remoteAddr:【{}】", clientRealIp, remoteAddr);
		clientRealIp = StringUtils.isEmpty(clientRealIp) ? remoteAddr : clientRealIp;
		requestMap.put("ip", clientRealIp);

		// 设置Context变量
		// ServiceContext.getContext().addContextVar("ip", clientRealIp);
		// 可以通过ServiceContext.getContext().getContextVar("ip")方式获取;
		return internalHandle(requestMap);
	}

	/**
	 * 真正处理业务请求
	 * 
	 * @param requestMap
	 * @return
	 */
	public abstract ApiResult<Map> internalHandle(Map requestMap);
}
