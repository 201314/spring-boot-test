package com.gitee.linzl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/he")
public class ProductRest2 {

    @RequestMapping(value = "/wytd/callback.do", method = RequestMethod.GET)
    public String rechargeCallBack(@RequestParam(value = "imei") String imei, HttpServletRequest request) {
        return imei;
    }

    @RequestMapping(value = "/wytd/callback2.do", method = RequestMethod.POST)
    public Map rechargeCallBack2(HttpServletRequest request) {
        return request.getParameterMap();
    }

    @RequestMapping(value = "/wytd/callback3.do", method = RequestMethod.POST)
    public Student rechargeCallBack3(Student map) {
        return map;
    }

    @RequestMapping(value = "/wytd/callback4.do", method = RequestMethod.POST)
    public String rechargeCallBack4(HttpServletRequest request) {

        return request.getParameter("name");
    }

    @RequestMapping(value = "/wytd/callback5.do", method = RequestMethod.POST)
    public String[] rechargeCallBack5(HttpServletRequest request) {
        System.out.println(request.getParameterMap());
        System.out.println("1====" + request.getParameterValues("name"));
        System.out.println("2====" + request.getParameter("name"));
        return request.getParameterValues("name");
    }

    @RequestMapping(value = "/wytd/callback6.do", method = RequestMethod.GET)
    public Student rechargeCallBack6(Student map) {
        return map;
    }

    @GetMapping(value = "/wytd/callback7.do")
    public String rechargeCallBacK7(HttpServletRequest request) throws IOException {
        System.out.println("GetMapping==================");
        String json = new String(StreamUtils.copyToByteArray(request.getInputStream()));
        System.out.println(request.getParameter("name"));
        System.out.println(request.getParameterValues("name"));
        System.out.println("getParameterMap:" + request.getParameterMap());
        System.out.println("getParameterNames:" + request.getParameterNames());
        return json;
    }

    @PostMapping(value = "/wytd/callback8.do")
    public String rechargeCallBacK8(HttpServletRequest request) throws IOException {
        System.out.println("PostMapping==================");
        String json = new String(StreamUtils.copyToByteArray(request.getInputStream()));
        System.out.println(request.getParameter("name"));
        System.out.println(request.getParameterValues("name"));
        System.out.println("getParameterMap:" + request.getParameterMap());
        System.out.println("getParameterNames:" + request.getParameterNames());
        return json;
    }
}
