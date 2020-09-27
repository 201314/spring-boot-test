package com.gitee.linzl.commons.file;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 下载文件的
 * @see com.gitee.linzl.commons.tools.ServletUtil#download(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.io.File, java.lang.String, com.gitee.linzl.commons.tools.ServletUtil.OpenTypeEnum)
 */
public class FileUtil {
    /**
     * File 转换成 MultipartFile
     *
     * @param fieldName 转换时用的别名
     * @param file      文件
     * @return
     * @throws IOException
     */
    public MultipartFile toFile(String fieldName, File file) throws IOException {
        FileItemFactory factory = new DiskFileItemFactory();
        FileItem item = factory.createItem(fieldName, "multipart/form-data", true, file.getName());
        FileCopyUtils.copy(new FileInputStream(file), item.getOutputStream());
        return new CommonsMultipartFile(item);
    }
}
