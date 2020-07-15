package com.gitee.linzl.commons.api;

import com.gitee.linzl.commons.constants.GlobalConstants;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author linzl
 * @description 请求基类， 不需要添加@ApiModel
 * @email 2225010489@qq.com
 * @date 2017年10月26日
 */
@Getter
@Setter
public class BaseRequest implements Serializable {
    /**
     * 当前页码
     */
    private int page = GlobalConstants.CURRENT_PAGE;
    /**
     * 每页条数
     */
    private int limit = GlobalConstants.PAGE_SIZE;
}
