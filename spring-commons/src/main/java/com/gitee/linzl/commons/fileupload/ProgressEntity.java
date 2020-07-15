package com.gitee.linzl.commons.fileupload;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Setter
@Getter
public class ProgressEntity implements Serializable {
    /**
     * 到目前为止读取文件的比特数
     */
    private long bytesRead = 0L;
    /**
     * 文件总大小
     */
    private long contentLength = 0L;
    /**
     * 目前正在读取第几个文件
     */
    private int items;

    @Override
    public String toString() {
        float tmp = (float) bytesRead;
        float result = tmp / contentLength * 100;
        return "ProgressEntity [BytesRead=" + bytesRead + ", ContentLength=" + contentLength + ", percentage=" + result
                + "% , Items=" + items + "]";
    }
}