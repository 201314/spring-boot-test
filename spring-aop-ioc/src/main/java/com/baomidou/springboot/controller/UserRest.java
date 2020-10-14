package com.baomidou.springboot.controller;

import com.baomidou.springboot.entity.User;
import com.gitee.linzl.commons.annotation.ApiMethod;
import com.gitee.linzl.commons.annotation.Performance;
import com.gitee.linzl.commons.annotation.RequiredPermissionToken;
import com.gitee.linzl.commons.api.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserRest {
    /**
     * 分页 PAGE
     */
    @PostMapping("/test")
    public ApiResult<User> test(@RequestBody @Valid User param, BindingResult bindingResult) {
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
        user.setTestType(200);
        user.setMessage(sb.toString());
        return ApiResult.success(user);
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
