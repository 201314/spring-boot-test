package com.gitee.linzl.commons.mybatis.service;

/**
 * @author linzhenlie
 * @date 2020-06-03
 */
@FunctionalInterface
public interface OptUserService {
    /**
     * 获取当前用户ID
     *
     * @return
     */
    String getUserId();
}
