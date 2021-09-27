package com.gitee.linzl.log.mask.logback;

/**
 * 对于regex匹配成功的字符串，如何处理
 *
 * @author linzhenlie
 * @date 2019/9/30
 */
public enum PolicyEnum {
    /**
     * 将敏感信息除去前三、后四位字符之外的其他字符用“*”替换，也是默认策略
     */
    REPLACE,
    /**
     * 将匹配成功的字符串，全部替换为等长度的“*”
     */
    ERASE,
    /**
     * 直接抛弃，将message重置为一个“终止符号”
     */
    DROP;
}
