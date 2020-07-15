package com.gitee.linzl.commons.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 脱敏
 *
 * @author linzhenlie
 * @date 2019/10/9
 */
public class MaskSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType,
                      int features) throws IOException {
        String value = String.valueOf(object);
        if (Objects.isNull(value)) {
            return;
        }

        value = value.trim();
        String field = String.valueOf(fieldName);
        switch (field) {
            case "idCard":
                serializer.write(MaskSensitiveUtil.bankCard(value));
                break;
            case "bankCard":
                serializer.write(MaskSensitiveUtil.bankCard(value));
                break;
            case "account":
            case "name":
                serializer.write(MaskSensitiveUtil.chineseName(value));
                break;
            case "mobile":
                serializer.write(MaskSensitiveUtil.mobile(value));
                break;
            default:
                serializer.write(value);
                break;
        }
    }
}