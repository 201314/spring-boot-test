package com.gitee.linzl.commons.config;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.UpgradeProtocol;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.Servlet;

/**
 * @author linzhenlie-jk
 * @date 2021/3/23
 */
@ConditionalOnClass({ Servlet.class, Tomcat.class, UpgradeProtocol.class })
@Component
public class TomcatConfig {
    /**
     * Slow http 拒绝服务原理:
     * 请求以很低的速度发送post请求数据包，当客户端连接了许多以后，占用了所有webserver可用连接，从而导致服务夯死。
     * http慢速攻击是利用http合法机制，在建立连接后，尽量长时间保持连接，不释放，从而达到对HTTP服务攻击,
     * 攻击者发送POST请求，自行构造报文向服务器提交数据，将报文长度设置一个很大的值，且在随后每次发送中，每次只发送一个很小的报文，这样导致服务器一直等待数据，连接始终一直被占用。
     * 如果攻击者使用多线程或傀儡机子去做同样操作，服务器WEB容器很快就被占满TCP连接从而不再接受新请求。
     */
    @Bean
    public TomcatConnectorCustomizer tomcatConnectorCustomizer() {
        return new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                ProtocolHandler handler = (ProtocolHandler) connector.getProtocolHandler();
                if (handler instanceof AbstractProtocol) {
                    AbstractProtocol<?> protocol = (AbstractProtocol<?>) handler;
                    //设置最大连接数
                    protocol.setMaxConnections(2000);
                    //设置最大线程数
                    protocol.setMaxThreads(2000);
                    protocol.setConnectionTimeout(30000);
                }
            }
        };
    }
}
