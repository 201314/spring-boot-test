package com.gitee.linzl.commons.gateway;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.api.BaseRequestProtocol;
import com.gitee.linzl.commons.tools.UserClientUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractBaseAction {
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 处理网关controller分发过来的请求
     *
     * @param requestInfo
     * @return
     */
    @SneakyThrows
    public ApiResult<String> handle(BaseRequestProtocol requestInfo, HttpServletRequest request) {
        String json = objectMapper.writeValueAsString(requestInfo);
        JavaType javaType = objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, String.class);
        Map<String, String> requestMap = objectMapper.readValue(json, javaType);
        String clientRealIp = UserClientUtil.getInstance().getIp(request);
        String remoteAddr = request.getRemoteAddr();
        log.debug("获取终端IP信息: clientRealIp:【{}】, remoteAddr:【{}】", clientRealIp, remoteAddr);
        clientRealIp = StringUtils.isEmpty(clientRealIp) ? remoteAddr : clientRealIp;
        requestMap.put("ip", clientRealIp);
        return internalHandle(requestMap);
    }

    /**
     * 真正处理业务请求
     *
     * @param requestMap
     * @return
     */
    public abstract ApiResult<String> internalHandle(Map requestMap);
}
