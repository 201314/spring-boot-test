package com.gitee.linzl.commons.fileupload;

import org.apache.commons.fileupload.ProgressListener;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class UploadProgressListener implements ProgressListener {

    private HttpSession session;

    /**
     * 在相应action中通过存储在session中的对象 status 来获取最新的上传进度并返回展示即可。
     *
     * @param session
     */
    public void setSession(HttpSession session) {
        this.session = session;
        ProgressEntity status = new ProgressEntity();
        session.setAttribute("status", status);
    }

    /**
     * @param pBytesRead     到目前为止读取文件的比特数
     * @param pContentLength 文件总大小
     * @param pItems         目前正在读取第几个文件
     */
    @Override
    public void update(long pBytesRead, long pContentLength, int pItems) {
        ProgressEntity status = (ProgressEntity) session.getAttribute("status");
        status.setBytesRead(pBytesRead);
        status.setContentLength(pContentLength);
        status.setItems(pItems);
    }
}