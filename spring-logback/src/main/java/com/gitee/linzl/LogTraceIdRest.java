package com.gitee.linzl;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.tools.ApiResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
public class LogTraceIdRest {

    @GetMapping
    public ApiResult<String> testTradeId() {
        StringBuffer sb = new StringBuffer();
        sb.append("打印日志,链接追踪");
        log.info("错误日志：" + sb);

        Student stu2 = new Student();
        stu2.setFileName("我是Student2222");
        stu2.setMobile("13828498029===");
        stu2.setFullName("邓小小");
        stu2.setIdCardNo("445222197906498761");
        stu2.setBankCard("6217790001073282390");
        stu2.setUserName("linzl哈哈最长就2");
        stu2.setUserName2("中国人");
        stu2.setUserName3("13690251143");
        stu2.setEmail("2225010412@qq.com");
        log.info("stu222:{}", stu2);
        return ApiResults.success(sb.toString());
    }

}
