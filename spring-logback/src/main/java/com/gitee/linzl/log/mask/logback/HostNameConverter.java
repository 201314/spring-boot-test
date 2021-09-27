package com.gitee.linzl.log.mask.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.ContextUtil;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author linzhenlie-jk
 * @date 2021/8/30
 */
public class HostNameConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        String hostName = "-";
        try {
            hostName = ContextUtil.getLocalHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostName;
    }
}
