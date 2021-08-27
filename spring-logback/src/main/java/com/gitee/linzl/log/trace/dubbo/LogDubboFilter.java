package com.gitee.linzl.log.trace.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.linzl.commons.constants.GlobalConstants;
import com.gitee.linzl.commons.tools.NetUtils;
import com.gitee.linzl.log.core.IOLog;
import com.gitee.linzl.log.core.MonitorLog;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StopWatch;

import java.util.Objects;

/**
 * 记录所有生产者、消费者日志
 *
 * @author linzhenlie-jk
 * @date 2021/8/27
 */
@Slf4j
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER})
public class LogDubboFilter implements Filter {
    private static final Logger ioLog = LoggerFactory.getLogger(IOLog.class);
    private static final Logger monitorLog = LoggerFactory.getLogger(MonitorLog.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext rpcContext = RpcContext.getContext();
        String traceId = invocation.getAttachment(GlobalConstants.TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = rpcContext.getAttachment(GlobalConstants.TRACE_ID);
        }
        MDC.put(GlobalConstants.TRACE_ID, traceId);

        StopWatch clock = new StopWatch();
        clock.start(); //计时开始

        String rpcStatus = "S";
        RpcException ex = null;
        Result result = null;
        try {
            result = invoker.invoke(invocation);
        } catch (RpcException e) {
            rpcStatus = "F";
            ex = e;
        } catch (Exception e) {
            throw e;
        } finally {
            clock.stop();
            long elapsed = clock.getTotalTimeMillis();
            ioLog(invoker, invocation, result);
            monitorLog(invoker, invocation, elapsed, result, rpcStatus);
            if (Objects.nonNull(ex)) {
                throw ex;
            }
            return result;
        }
    }

    private void ioLog(Invoker invoker, Invocation invocation, Result result) {
        String logPattern = "方法={}#{},入参:{},出参:{}";
        String interfaceName = abbreviate(invoker.getInterface().getName());
        String methodName = invocation.getMethodName();
        Object[] arguments = invocation.getArguments();

        String argString = JSON.toJSONString(arguments, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        String resultString = null;
        if (Objects.nonNull(result) && !result.hasException()) {
            resultString = JSON.toJSONString(result.getValue(), SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        }

        ioLog.info(logPattern, interfaceName, methodName, argString, resultString);
    }

    private void monitorLog(Invoker invoker, Invocation invocation, Long elapsed, Result result, String rpcStatus) {
        // localIp | localApp | traceId | interface#method | status | remoteIp |remoteApp
        String logPattern = "{}|{}|{}|{}#{}|{}|{}|{}";
        // 生产者IP(自己)
        String localIp = NetUtils.getLocalHost();
        // 应用名称
        String localApp = invoker.getUrl().getParameter(CommonConstants.APPLICATION_KEY, "");
        // 跟踪流水traceId
        String traceId = MDC.get(GlobalConstants.TRACE_ID);
        String interfaceName = abbreviate(invoker.getInterface().getName());
        String methodName = invocation.getMethodName();
        //elapsed
        String status = rpcStatus;
        // 消费者IP
        RpcContext context = RpcContext.getContext();
        String remoteIp = context.getRemoteHost();
        String remoteApp = invocation.getAttachment(CommonConstants.APPLICATION_KEY, "");
        monitorLog.info(logPattern, localIp, localApp, traceId, interfaceName, methodName, status, remoteIp, remoteApp);
    }

    private static String point = ".";

    /**
     * 可参考 ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator#abbreviate(java.lang.String)
     * <p>
     * 完整clsName ==》 缩略x.x.x.ClassName
     */
    public static String abbreviate(String clsName) {
        if (StringUtils.isEmpty(clsName)) {
            return StringUtils.EMPTY;
        }
        StringBuffer sb = new StringBuffer();
        int count = StringUtils.countMatches(clsName, point) + 1;
        int lastCount = count - 1;
        int pointIndex = 0;
        for (int i = 0; i < count; i++) {
            if (i == lastCount) {
                sb.append(StringUtils.substring(clsName, pointIndex, clsName.length()));
            } else {
                String temp = StringUtils.substring(clsName, pointIndex, pointIndex + 1);
                sb.append(StringUtils.equals(temp, point) ? StringUtils.EMPTY : temp)
                        .append(point);
                pointIndex = StringUtils.indexOf(clsName, point, pointIndex) + 1;
            }
        }
        return sb.toString();
    }
}
