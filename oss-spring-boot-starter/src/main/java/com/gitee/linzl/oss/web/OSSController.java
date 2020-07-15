package com.gitee.linzl.oss.web;

import com.gitee.linzl.commons.tools.ServletUtil;
import com.gitee.linzl.oss.service.OSSService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("upload")
@Api(value = "oss服务")
@Slf4j
public class OSSController {
    @Autowired
    private OSSService ossService;

    @ApiOperation(value = "文件上传接口")
    @PostMapping(value = "file", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public void uploadFile(@RequestParam("file") @ApiParam(value = "文件", required = true) MultipartFile file) {
        try {
            ossService.upload(file.getInputStream(), "2017/12/11/hello.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "多文件上传接口")
    @PostMapping(value = "files", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public void uploadFiles(@RequestParam("file") @ApiParam(value = "文件", required = true) MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                ossService.upload(file.getInputStream(), "2017/12/11/hello.jpg");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
