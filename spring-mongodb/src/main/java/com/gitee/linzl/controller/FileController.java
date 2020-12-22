package com.gitee.linzl.controller;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.tools.ApiResults;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件上传下载
 *
 * @author linzhenlie
 * @date 2019/10/10
 */
@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @return 返回在mongodbc中的id
     */
    @PostMapping(value = "/upload")
    public Object upload(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        ObjectId id = gridFsTemplate.store(multipartFile.getInputStream(), multipartFile.getName(), multipartFile.getContentType());
        return ApiResults.success(id);
    }

    /**
     * 下载
     *
     * @param fileId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam(name = "fileId") String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        // 查询单个文件
        GridFSFile gfsFile = gridFsTemplate.findOne(query);
        GridFsResource gridFsResource = gridFsTemplate.getResource(gfsFile);
        String fileName = gridFsResource.getFilename();
        //处理中文文件名乱码
        if (request.getHeader("User-Agent").toUpperCase().contains("MSIE") ||
                request.getHeader("User-Agent").toUpperCase().contains("TRIDENT")
                || request.getHeader("User-Agent").toUpperCase().contains("EDGE")) {
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        } else {
            //非IE浏览器的处理：
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }
        // 通知浏览器进行文件下载
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        IOUtils.copy(gridFsResource.getInputStream(), response.getOutputStream());
    }

    /**
     * 删除文件
     *
     * @param fileId
     * @return
     */
    @PostMapping(value = "/delete")
    public Object delete(@RequestParam(name = "fileId") String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        // 删除单个文件
        gridFsTemplate.delete(query);
        return ApiResults.success();
    }
}