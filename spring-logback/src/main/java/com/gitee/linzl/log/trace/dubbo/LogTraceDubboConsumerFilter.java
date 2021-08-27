package com.gitee.linzl.log.trace.dubbo;

import com.gitee.linzl.commons.constants.GlobalConstants;
import com.gitee.linzl.commons.tools.NetUtils;
import com.gitee.linzl.log.trace.util.TraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.MDC;

import java.util.Objects;

/**
 * 消费者
 * 创建跟踪流水
 *
 * @author linzhenlie-jk
 * @date 2021/8/27
 */
@Slf4j
@Activate(group = CommonConstants.CONSUMER)
public class LogTraceDubboConsumerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext context = RpcContext.getContext();
        String traceId = invocation.getAttachment(GlobalConstants.TRACE_ID);

        if (StringUtils.isBlank(traceId)) {
            traceId = TraceUtil.getTraceId();
            context.setAttachment(GlobalConstants.TRACE_ID, traceId);
            invocation.put(GlobalConstants.TRACE_ID, traceId);
        }
        MDC.put(GlobalConstants.TRACE_ID, traceId);
        MDC.put("localIp", NetUtils.getLocalHost());

        RpcException ex = null;
        Result result = null;
        try {
            result = invoker.invoke(invocation);
        } catch (RpcException e) {
            ex = e;
        }
        if (Objects.nonNull(ex)) {
            throw ex;
        }
        return result;
    }
}
