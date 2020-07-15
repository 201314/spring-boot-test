package com.gitee.linzl.commons.gateway;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.linzl.commons.api.ApiResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GatewayController {
    @Resource
    private Map<String, AbstractBaseAction> actionMap;

    @PostMapping(value = "/api/m/gateway")
    public ApiResult<Map> handleRequest(@RequestBody GatewayRequest requestInfo, HttpServletRequest request, HttpServletResponse response) {
        // 参数校验
        //ValidationResult vRst = ValidationUtils.validateEntity(requestInfo);
        //if (vRst.isHasErrors()) {
        //    log.warn("参数校验不通过, requestInfo={}, vRst={}", ResponseUtil.compressRequest(requestInfo), vRst);
        //    return new BaseResponse<>().fail(ResponseEnums.ILLEGAL_ARGUMENT);
        //}
        String method = requestInfo.getMethod();
        AbstractBaseAction action = actionMap.get(method);
        if (action == null) {
            log.error("GatewayController找不到该方法. method:【{}】", method);
        }
        ApiResult<Map> responseMap = action.handle(requestInfo, request);
        log.info("处理后返回给前端的响应信息:responseMap:【{}】,method:【{}】", responseMap,method);
        //ServiceContext.getContext().addContextVar("responseMsg", responseMap.checkIfSuccess() ? "S" : responseMap.getCode());
        return responseMap;
    }
}
