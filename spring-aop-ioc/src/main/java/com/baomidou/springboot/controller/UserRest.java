package com.baomidou.springboot.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.baomidou.springboot.entity.User;
import com.baomidou.springboot.services.IMyService;
import com.gitee.linzl.commons.annotation.ApiMethod;
import com.gitee.linzl.commons.annotation.Performance;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.tools.ApiResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserRest {
    @Autowired
    private IMyService iMyService;
    /**
     * 分页 PAGE
     */
    @PostMapping("/test")
    @Performance
    public ApiResult<User> test(@RequestBody @Valid List<User> param, BindingResult bindingResult) {
        StringBuffer sb = new StringBuffer();
        if (bindingResult.hasErrors()) {

            for (ObjectError objectError : bindingResult.getAllErrors()) {
                sb.append(((FieldError) objectError).getField() + " : ").append(objectError.getDefaultMessage())
                        .append("\n");
            }
            log.error("错误日志：" + sb);
        }

        User user = new User();
        user.setName("name");
        user.setTestDate(new Date());
        user.setLocalDateTime(param.get(0).getLocalDateTime());
        user.setLocalDate(param.get(0).getLocalDate());
        user.setTestType(200);
        user.setMessage(sb.toString());
        return ApiResults.success(user);
    }

    @GetMapping("/test1")
    @Performance
    public ApiResult<User> test1(@Valid User param) {
        StringBuffer sb = new StringBuffer();
        User user = new User();
        user.setName("/test1");
        user.setTestDate(new Date());
        user.setLocalDateTime(param.getLocalDateTime());
        user.setLocalDate(param.getLocalDate());
        user.setTestType(200);
        user.setMessage(sb.toString());
        iMyService.select1();
        iMyService.add();
        iMyService.select2();
        return ApiResults.success(user);
    }

    @GetMapping("/test2")
    @Performance
    public ApiResult<User> test2(@RequestParam("localDateTime") LocalDateTime localDateTime,@RequestParam("localDate") LocalDate localDate) {
        StringBuffer sb = new StringBuffer();
        User user = new User();
        user.setName("/test2");
        user.setTestDate(new Date());
        user.setLocalDateTime(localDateTime);
        user.setLocalDate(localDate);
        user.setTestType(200);
        user.setMessage(sb.toString());
        return ApiResults.success(user);
    }


    @PostMapping("/test3")
    public ApiResult<User> test3(@RequestBody @Valid User param, BindingResult bindingResult) {
        StringBuffer sb = new StringBuffer();
        if (bindingResult.hasErrors()) {

            for (ObjectError objectError : bindingResult.getAllErrors()) {
                sb.append(((FieldError) objectError).getField() + " : ").append(objectError.getDefaultMessage())
                        .append("\n");
            }
            log.error("错误日志：" + sb);
        }

        User user = new User();
        user.setName("name");
        user.setTestDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setTestType(200);
        user.setMessage(sb.toString());
        return ApiResults.success(user);
    }


    @ApiMethod("api.user.get")
    @RequestMapping(value = "/get")
    @Performance
    public User getUser(HttpServletRequest request) throws IOException {
        System.out.println("Authorization==" + request.getHeader("Authorization"));
        try {
            BufferedReader reader = request.getReader();
            StringBuffer sb = new StringBuffer();
            String content = reader.readLine();
            while ((content) != null) {
                sb.append(content);
                content = reader.readLine();
            }
            System.out.println("第一次" + sb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setName("name");
        user.setTestDate(new Date());
        user.setTestType(200);
        user.setMessage("我就测试一下zipkin");
        return user;
    }

    @RequestMapping(value = "/get2")
    public User getUser2(HttpServletRequest request) throws IOException {
        System.out.println("Authorization22==" + request.getHeader("Authorization"));
        try {
            BufferedReader reader = request.getReader();
            StringBuffer sb = new StringBuffer();
            String content = reader.readLine();
            while ((content) != null) {
                sb.append(content);
                content = reader.readLine();
            }
            System.out.println("第一次" + sb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setName("name");
        user.setTestDate(new Date());
        user.setTestType(200);
        user.setMessage("我就测试一下zipkin");
        return user;
    }
}
