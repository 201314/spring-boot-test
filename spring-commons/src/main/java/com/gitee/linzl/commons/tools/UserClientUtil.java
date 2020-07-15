package com.gitee.linzl.commons.tools;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author linzl
 * @description 获取客户端用户信息
 * @email 2225010489@qq.com
 * @date 2018年8月19日
 */
public class UserClientUtil {
    private UserAgent userAgent;

    private static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private UserClientUtil(UserAgent userAgent) {
        this.userAgent = userAgent;
    }

    public static UserClientUtil getInstance() {
        UserAgent userAgent = UserAgent.parseUserAgentString(getCurrentRequest().getHeader("User-Agent"));
        return new UserClientUtil(userAgent);
    }

    public static UserClientUtil builder(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        return new UserClientUtil(userAgent);
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public OperatingSystem getOperatingSystem() {
        return userAgent.getOperatingSystem();
    }

    public Browser getBrowser() {
        return userAgent.getBrowser();
    }

    public Version getVersion() {
        return userAgent.getBrowserVersion();
    }

    /**
     * 获取用户当前请求的IP地址
     * <p>
     * PS:注意该方法不能在filter中使用，必须传入request参数
     *
     * @return
     */
    public String getIp() {
        return getIp(getCurrentRequest());
    }

    /**
     * 获取用户当前请求的IP地址
     *
     * @param request
     * @return
     */
    public String getIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.indexOf(",") > 0) {
            String[] arr = ipAddress.split(",");
            for (String str : arr) {
                if (!"unknown".equalsIgnoreCase(str)) {
                    ipAddress = str;
                    break;
                }
            }
        }
        return ipAddress;
    }

    /**
     * 获取客户端浏览器名称,包含主版本号
     *
     * @return
     */
    public String getBrowserName() {
        return userAgent.getBrowser().getName();
    }

    /**
     * 获取客户端浏览器类别
     *
     * @return
     */
    public String getBrowserType() {
        return userAgent.getBrowser().getGroup().getName();
    }

    /**
     * 是否是IE浏览器
     *
     * @return
     */
    public boolean isIE() {
        String browserType = getBrowserType();
        return browserType.indexOf("Internet") > -1 || browserType.indexOf("IE Mobile") > -1
                || browserType.indexOf("Windows Live Mail") > -1;
    }

    /**
     * 获取客户端浏览器详细版本
     *
     * @return
     */
    public String getDetailVersion() {
        return getVersion().getVersion();
    }

    /**
     * 获取客户端浏览器主版本
     *
     * @return
     */
    public String getMajorVersion() {
        return getVersion().getMajorVersion();
    }

    /**
     * 获取客户端浏览器次版本号
     *
     * @return
     */
    public String getMinorVersion() {
        return getVersion().getMinorVersion();
    }

    /**
     * 获取客户端操作系统的名称
     *
     * @return
     */
    public String getOSName() {
        return getOperatingSystem().getName();
    }

    /**
     * 获取客户端访问应用的设备类型：如个人计算机、手机、平板等
     *
     * @return
     */
    public String getDeviceType() {
        return getOperatingSystem().getDeviceType().getName();
    }

    /**
     * 获取客户端设备的制造厂商
     *
     * @return
     */
    public String getManufacturer() {
        return getOperatingSystem().getManufacturer().getName();
    }
}
