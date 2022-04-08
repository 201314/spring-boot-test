package com.gitee.linzl.controller;

import com.gitee.linzl.service.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {
    @GetMapping(value = "/push/{userId}")
    public Map<String, Object> push(@PathVariable("userId") String userId) {
        log.debug("socket 主动发布");
        Map<String, Object> result = new HashMap<String, Object>();
        WebSocketServer.broadcast("有新客户呼入,sltAccountId" + Math.random(), userId);
        result.put("operationResult", true);
        return result;
    }
}