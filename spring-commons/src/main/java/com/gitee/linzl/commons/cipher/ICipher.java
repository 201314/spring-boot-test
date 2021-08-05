package com.gitee.linzl.commons.cipher;

import java.util.Map;

/**
 * @author linzhenlie-jk
 * @date 2021/7/1
 */
public interface ICipher {
    public String encrypt(String content, String signType, String charset);

    public String sign(Map<String, Object> map, String signType);

    public boolean verify(Map<String, Object> map, String signType);
}
