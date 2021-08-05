package com.gitee.linzl.commons.tools;

import com.gitee.linzl.commons.client.ApiConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author linzhenlie-jk
 * @date 2021/6/29
 */
public class SignUtil {
    /**
     * @param sortedParams  需要自己添加appId
     * @param signSecretKey
     * @return
     */
    public static String createSign(Map<String, Object> sortedParams) {
        List<String> keys = new ArrayList<>(sortedParams.keySet());
        Collections.sort(keys);
        StringBuffer content = new StringBuffer();
        int index = 0;
        for (int i = 0, size = keys.size(); i < size; i++) {
            String key = keys.get(i);
            // 不参与签名
            if (ApiConstant.SIGN.equals(key) || ApiConstant.SIGN_TYPE.equals(key)) {
                continue;
            }
            Object value = sortedParams.get(key);
            //如果参数的值为空不参与签名
            if (Objects.nonNull(value)) {
                if (value instanceof List && ((List) value).isEmpty()) {
                    continue;
                }
                if (value instanceof String && StringUtils.isBlank((String) value)) {
                    continue;
                }
                content.append((index == 0 ? "" : "&") + key + "=" + value);
                index++;
            }
        }
        return content.toString();
    }
}
