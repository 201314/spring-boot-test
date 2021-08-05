package com.gitee.linzl.commons.client;

import com.gitee.linzl.commons.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;

/**
 * @author linzhenlie-jk
 * @date 2021/6/30
 */
@Slf4j
public class DefaultHttpClient implements IHttpClient {
    public static final int POOL_SIZE = 100;
    // 设置连接超时时间,单位毫秒
    public static final int CONNECT_TIMEOUT = 5000;
    // 设置读取超时\套接字超时时间,单位毫秒
    public static final int SOCKET_TIMEOUT = 30000;
    // 设置从连接池获取连接实例的超时
    public static final int CONNECTION_REQUEST_TIMEOUT = 3000;

    private final CloseableHttpClient httpClient;

    public DefaultHttpClient() {
        this(POOL_SIZE, POOL_SIZE,
                CONNECT_TIMEOUT,
                SOCKET_TIMEOUT,
                CONNECTION_REQUEST_TIMEOUT);
    }

    public DefaultHttpClient(Integer maxTotal, Integer defaultMaxPerRoute) {
        this(maxTotal, defaultMaxPerRoute,
                CONNECT_TIMEOUT,
                SOCKET_TIMEOUT,
                CONNECTION_REQUEST_TIMEOUT);
    }

    public DefaultHttpClient(Integer maxTotal, Integer defaultMaxPerRoute, Integer connectTimeout,
                             Integer socketTimeout, Integer connectionRequestTimeout) {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext =
                    (new SSLContextBuilder()).loadTrustMaterial(null, (chain, authType) -> true).build();
            // 信任所有
            sslsf = new SSLConnectionSocketFactory(sslContext, (s, sslSession) -> true);
        } catch (Exception e) {
        }

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", sslsf)
                        .build()
        );
        // 设置连接池总共大小
        connMgr.setMaxTotal(maxTotal);
        // 第一个连接最大只能是defaultMaxPerRoute，所有连接的和加起来不能超过defaultMaxPerRoute
        connMgr.setDefaultMaxPerRoute(defaultMaxPerRoute);

        RequestConfig.Builder cfgBuilder = RequestConfig.custom();
        // 设置连接超时
        cfgBuilder.setConnectTimeout(connectTimeout);
        // 设置读取超时
        cfgBuilder.setSocketTimeout(socketTimeout);
        // 设置从连接池获取连接实例的超时
        cfgBuilder.setConnectionRequestTimeout(connectionRequestTimeout);

        RequestConfig requestConfig = cfgBuilder.build();

        httpClient = HttpClients.custom()
                .setConnectionManager(connMgr)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .build();
    }

    @Override
    public String post(String url, String data, String apiMethod) {
        return doPost(url, data, apiMethod);
    }

    private String doPost(String url, String json, String apiMethod) {
        if (StringUtils.isBlank(json)) {
            throw new ApiException("请求数据为空");
        }
        HttpPost httpPost = new HttpPost(url);
        custom(httpPost, apiMethod);
        HttpEntity strEntity = EntityBuilder.create()
                .setContentType(ContentType.APPLICATION_JSON)
                .setText(json)
                .build();
        httpPost.setEntity(strEntity);

        try (CloseableHttpResponse clsResp = httpClient.execute(httpPost);) {
            if (clsResp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new ApiException(String.valueOf(clsResp.getStatusLine().getStatusCode()),
                        clsResp.getStatusLine().getReasonPhrase());
            }
            return EntityUtils.toString(clsResp.getEntity(), Consts.UTF_8);
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    protected void custom(HttpPost httpPost, String apiMethod) {

    }
}
