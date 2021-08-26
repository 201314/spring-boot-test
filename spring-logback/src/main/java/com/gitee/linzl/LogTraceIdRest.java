package com.gitee.linzl;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.tools.ApiResults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping
public class LogTraceIdRest {

    @GetMapping
    public ApiResult<String> testTradeId() {
        MDC.put("tid", UUID.randomUUID().toString().replace("-", ""));

        StringBuffer sb = new StringBuffer();
        sb.append("打印日志,链接追踪");
        log.info("错误日志：" + sb);
        return ApiResults.success(sb.toString());
    }

}
