package com.gitee.linzl.commons.autoconfigure.token;

/**
 * TOKEN 服务
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
public interface ITokenService {
    /**
     * 生成TOKEN
     *
     * @param extBody 需要添加到TOKEN中的自定义属性,可放JSON值,为了保证安全，传入的数据先自行对称加密
     * @return java.lang.String
     */
    default String generate(String extBody) {
        return extBody;
    }

    /**
     * 解析TOKEN
     *
     * @param token TOKEN值
     * @return java.lang.String 返回生成TOKEN时传入的自定义属性
     */
    default String parse(String token) {
        return token;
    }
}
