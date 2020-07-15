package com.gitee.linzl.commons.tools;

import com.gitee.linzl.commons.constants.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户端工具类
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
public class ServletUtil {
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String TYPE_MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    private static final String SCOPE_APPLICATION = "application";
    private static final String SCOPE_SESSION = "session";
    private static final String SCOPE_REQUEST = "request";
    private static final String SCOPE_PAGE = "page";
    private static final String SLASH = "/";

    /**
     * 是否在线打开文件，默认为下载false,在线打开true
     */
    public enum OpenTypeEnum {
        ATTACHMENT("attachment"),
        INLINE("inline");
        String openType;

        OpenTypeEnum(String openType) {
            this.openType = openType;
        }

        public String getOpenType() {
            return this.openType;
        }
    }

    // ---------------------------------------------------------------- multi-part

    /**
     * Returns <code>true</code> if a request is multi-part request.
     */
    public static boolean isMultipartRequest(final HttpServletRequest request) {
        String type = request.getHeader(HEADER_CONTENT_TYPE);
        return Objects.nonNull(type) && type.startsWith(TYPE_MULTIPART_FORM_DATA);
    }

    /**
     * Returns <code>true</code> if client supports gzip encoding.
     */
    public static boolean isGzipSupported(final HttpServletRequest request) {
        String browserEncodings = request.getHeader(HEADER_ACCEPT_ENCODING);
        return Objects.nonNull(browserEncodings) && browserEncodings.contains("gzip");
    }

    // ---------------------------------------------------------------- types

    /**
     * 判断是否为异步提交的请求
     *
     * @param request
     * @return
     */
    public static boolean isAjax(HttpServletRequest request) {
        return isJsonRequest(request)
                ||
                GlobalConstants.AJAX_HEADER_VALUE.equalsIgnoreCase(request.getHeader(GlobalConstants.AJAX_HEADER_NAME));
    }

    /**
     * Returns {@code true} if request has JSON content type.
     */
    public static boolean isJsonRequest(HttpServletRequest request) {
        if (Objects.nonNull(request.getContentType())
                && request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            return true;
        }
        return false;
    }

    // ---------------------------------------------------------------- authorization

    /**
     * Decodes the "Authorization" header and retrieves the
     * user's name from it. Returns <code>null</code> if the header is not present.
     */
    public static String resolveAuthUsername(final HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.isEmpty(header)) {
            return null;
        }
        if (!header.contains("Basic ")) {
            return null;
        }
        final String encoded = header.substring(header.indexOf(' ') + 1);
        final String decoded = new String(Base64.getDecoder().decode(encoded));
        return decoded.substring(0, decoded.indexOf(':'));
    }

    /**
     * Decodes the "Authorization" header and retrieves the
     * password from it. Returns <code>null</code> if the header is not present.
     */
    public static String resolveAuthPassword(final HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.isEmpty(header)) {
            return null;
        }
        if (!header.contains("Basic ")) {
            return null;
        }
        final String encoded = header.substring(header.indexOf(' ') + 1);
        final String decoded = new String(Base64.getDecoder().decode(encoded));
        return decoded.substring(decoded.indexOf(':') + 1);
    }

    /**
     * Returns Bearer token.
     */
    public static String resolveAuthBearerToken(final HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.isEmpty(header)) {
            return null;
        }
        int ndx = header.indexOf("Bearer ");
        if (ndx == -1) {
            return null;
        }
        // Basic : 后开始截取
        return header.substring(ndx + 7).trim();
    }

    /**
     * Sends correct headers to require basic authentication for the given realm.
     */
    public static void requireAuthentication(final HttpServletResponse resp, final String realm) throws IOException {
        resp.setHeader(WWW_AUTHENTICATE, "Basic realm=\"" + realm + '\"');
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    // ---------------------------------------------------------------- download and content disposition
    public static void download(HttpServletRequest request, HttpServletResponse response, File file,
                                String displayName, OpenTypeEnum openType) {
        if (!file.exists() || !file.canRead()) {
            response.setContentType("text/html;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            try {
                response.getWriter().write("您下载的文件不存在！");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        displayName = StringUtils.isEmpty(displayName) ? file.getName() : displayName;
        long fileSize = 0L;
        try {
            fileSize = Files.size(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        prepareDownload(request, response, displayName, fileSize, openType.getOpenType());

        try (FileInputStream input = new FileInputStream(file);
             BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));

             OutputStream os = response.getOutputStream()) {
            byte[] b = new byte[input.available()];
            while (is.read(b) != -1) {
                os.write(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 准备好下载的各参数信息
     *
     * @param request
     * @param response
     * @param displayName
     * @param fileSize
     * @param contentDisposition
     * @throws Exception
     */
    public static void prepareDownload(HttpServletRequest request, HttpServletResponse response,
                                       String displayName, long fileSize, String contentDisposition) {
        if (Objects.isNull(displayName)) {
            return;
        }

        UserClientUtil util = UserClientUtil.builder(request);
        boolean isIE = util.isIE();

        response.reset();
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "must-revalidate, no-transform");
        response.setDateHeader("Expires", 0L);

        response.setContentType("application/x-download");
        response.setContentLengthLong(fileSize);

        try {
            if (isIE) {
                displayName = URLEncoder.encode(displayName, "UTF-8");
                response.setHeader("Content-Disposition",
                        contentDisposition + ";filename=\"" + displayName.replaceAll("\\+", "%20") + "\"");
            } else {
                displayName = new String(displayName.getBytes("UTF-8"), "ISO8859-1");
                response.setHeader("Content-Disposition", contentDisposition + ";filename=" + displayName.replaceAll("\\+", "%20"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    // ---------------------------------------------------------------- cookie

    /**
     * Finds and returns cookie from client by its name.
     * Only the first cookie is returned.
     *
     * @return cookie value or <code>null</code> if cookie with specified name doesn't exist.
     * @see #getAllCookies(javax.servlet.http.HttpServletRequest, String)
     */
    public static Cookie getCookie(final HttpServletRequest request, final String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies)) {
            return null;
        }

        Optional<Cookie> optional = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    /**
     * Returns all cookies from client that matches provided name.
     *
     * @see #getCookie(javax.servlet.http.HttpServletRequest, String)
     */
    public static List<Cookie> getAllCookies(final HttpServletRequest request, final String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies)) {
            return null;
        }

        List<Cookie> list =
                Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).collect(Collectors.toList());
        return CollectionUtils.isEmpty(list) ? null : list;
    }

    // ---------------------------------------------------------------- request body

    /**
     * Reads HTTP request body using the request reader. Once body is read,
     * it cannot be read again!
     */
    public static String readRequestBodyFromReader(final HttpServletRequest request) throws IOException {
        BufferedReader buff = request.getReader();
        StringWriter out = new StringWriter();
        FileCopyUtils.copy(buff, out);
        return out.toString();
    }

    /**
     * Reads HTTP request body using the request stream. Once body is read,
     * it cannot be read again!
     */
    public static String readRequestBodyFromStream(final HttpServletRequest request) throws IOException {
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = StandardCharsets.UTF_8.name();
        }

        InputStream inputStream = request.getInputStream();
        if (inputStream != null) {
            CharArrayWriter charArrayWriter = new CharArrayWriter();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charEncoding));
            FileCopyUtils.copy(bufferedReader, charArrayWriter);
            return charArrayWriter.toString();
        }
        return "";
    }

    // ---------------------------------------------------------------- context path

    /**
     * Returns correct context path, as by Servlet definition. Different
     * application servers return all variants: "", null, "/".
     * <p>
     * The context path always comes first in a request URI. The path
     * starts with a "/" character but does not end with a "/" character.
     * For servlets in the default (root) context, this method returns "".
     */
    public static String getContextPath(final HttpServletRequest request) {
        String contextPath = request.getContextPath();
        if (StringUtils.isEmpty(contextPath) || StringUtils.equals(contextPath, SLASH)) {
            contextPath = "";
        }
        return contextPath;
    }

    /**
     * Returns correct context path, as by Servlet definition. Different
     * application servers return all variants: "", null, "/".
     * <p>
     * The context path always comes first in a request URI. The path
     * starts with a "/" character but does not end with a "/" character.
     * For servlets in the default (root) context, this method returns "".
     */
    public static String getContextPath(final ServletContext servletContext) {
        String contextPath = servletContext.getContextPath();
        if (StringUtils.isEmpty(contextPath) || StringUtils.equals(contextPath, SLASH)) {
            contextPath = "";
        }
        return contextPath;
    }

    /**
     * Stores context path in page context and request scope.
     */
    public static void storeContextPath(final ServletContext servletContext, final String contextPathVariableName) {
        String ctxPath = getContextPath(servletContext);
        servletContext.setAttribute(contextPathVariableName, ctxPath);
    }

    // ---------------------------------------------------------------- attributes and values

    /**
     * Returns value of property/attribute. The following value sets are looked up:
     * <ul>
     *     <li>request attributes</li>
     *     <li>request parameters</li>
     *     <li>session attributes</li>
     *     <li>context attributes</li>
     * </ul>
     */
    public static Object value(final HttpServletRequest request, final String name) {
        Object value = request.getAttribute(name);
        if (Objects.nonNull(value)) {
            return value;
        }

        if (!isMultipartRequest(request)) {
            String[] params = request.getParameterValues(name);
            if (Objects.nonNull(params)) {
                if (params.length == 1) {
                    value = params[0];
                } else {
                    value = params;
                }
            }
        }

        if (Objects.nonNull(value)) {
            return value;
        }

        value = request.getSession().getAttribute(name);
        if (Objects.nonNull(value)) {
            return value;
        }
        return request.getServletContext().getAttribute(name);
    }

    // ---------------------------------------------------------------- resolve URL

    /**
     * Valid characters in a scheme, as specified by RFC 1738.
     */
    public static final String VALID_SCHEME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";

    /**
     * Returns <code>true</code> if current URL is absolute, <code>false</code> otherwise.
     */
    public static boolean isAbsoluteUrl(final String url) {
        if (url == null) {            // a null URL is not absolute
            return false;
        }
        int colonPos;                   // fast simple check first
        if ((colonPos = url.indexOf(':')) == -1) {
            return false;
        }

        // if we DO have a colon, make sure that every character
        // leading up to it is a valid scheme character
        for (int i = 0; i < colonPos; i++) {
            if (VALID_SCHEME_CHARS.indexOf(url.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Strips a servlet session ID from <code>url</code>.  The session ID
     * is encoded as a URL "path parameter" beginning with "jsessionid=".
     * We thus remove anything we find between ";jsessionid=" (inclusive)
     * and either EOS or a subsequent ';' (exclusive).
     */
    public static String stripSessionId(final String url) {
        StringBuilder u = new StringBuilder(url);
        int sessionStart;
        while ((sessionStart = u.toString().indexOf(";jsessionid=")) != -1) {
            int sessionEnd = u.toString().indexOf(';', sessionStart + 1);
            if (sessionEnd == -1) {
                sessionEnd = u.toString().indexOf('?', sessionStart + 1);
            }
            if (sessionEnd == -1) {
                sessionEnd = u.length();
            }
            u.delete(sessionStart, sessionEnd);
        }
        return u.toString();
    }

    public static String resolveUrl(final String url, final HttpServletRequest request) {
        if (isAbsoluteUrl(url)) {
            return url;
        }
        if (url.startsWith(SLASH)) {
            return getContextPath(request) + url;
        }
        return url;
    }

    public static String resolveUrl(final String url, final String context) {
        if (isAbsoluteUrl(url)) {
            return url;
        }
        if (!context.startsWith(SLASH) || !url.startsWith(SLASH)) {
            throw new IllegalArgumentException("Values of both 'context' and 'url' must start with '/'.");
        }
        if (context.equals(SLASH)) {
            return url;
        }
        return (context + url);
    }

    // ---------------------------------------------------------------- params

    /**
     * Returns HTTP request parameter as String or String[].
     */
    public static Object getRequestParameter(final ServletRequest request, final String name) {
        String[] values = request.getParameterValues(name);
        if (values == null) {
            return null;
        }
        if (values.length == 1) {
            return values[0];
        }
        return values;
    }

    /**
     * Prepares parameters for further processing.
     *
     * @param paramValues              string array of param values
     * @param treatEmptyParamsAsNull   empty parameters should be treated as <code>null</code>
     * @param ignoreEmptyRequestParams if all parameters are empty, return <code>null</code>
     */
    public static String[] prepareParameters(
            final String[] paramValues,
            final boolean treatEmptyParamsAsNull,
            final boolean ignoreEmptyRequestParams) {

        if (treatEmptyParamsAsNull || ignoreEmptyRequestParams) {
            int emptyCount = 0;
            int total = paramValues.length;
            for (int i = 0; i < paramValues.length; i++) {
                String paramValue = paramValues[i];
                if (paramValue == null) {
                    emptyCount++;
                    continue;
                }
                if (paramValue.length() == 0) {
                    emptyCount++;
                    if (treatEmptyParamsAsNull) {
                        paramValue = null;
                    }
                }
                paramValues[i] = paramValue;
            }
            if (ignoreEmptyRequestParams && (emptyCount == total)) {
                return null;
            }
        }
        return paramValues;
    }

    // ---------------------------------------------------------------- full urls

    /**
     * Returns full URL: uri + query string, including the context path.
     */
    public static String getFullUrl(final HttpServletRequest request) {
        String url = request.getRequestURI();
        String query = request.getQueryString();
        if ((query != null) && (query.length() != 0)) {
            url += '?' + query;
        }
        return url;
    }

    /**
     * Returns url, without context path, convenient for request dispatcher.
     */
    public static String getUrl(final HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String query = request.getQueryString();
        if ((query != null) && (query.length() != 0)) {
            servletPath += '?' + query;
        }
        return servletPath;
    }

    // ---------------------------------------------------------------- cache

    /**
     * Prevents HTTP cache.
     */
    public static void preventCaching(final HttpServletResponse response) {
        response.setHeader("Cache-Control", "max-age=0, must-revalidate, no-cache, no-store, private, post-check=0, pre-check=0");  // HTTP 1.1
        response.setHeader("Pragma", "no-cache");        // HTTP 1.0
        response.setDateHeader("Expires", 0);          // prevents caching at the proxy server
    }


    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue) {
        return StringUtils.defaultIfBlank(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) {
        return NumberUtils.toInt(getParameter(name));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return NumberUtils.toInt(getParameter(name), defaultValue);
    }

    public static Map<String, String> getHeaderToMap() {
        HttpServletRequest request = getRequest();
        Enumeration<String> enums = request.getHeaderNames();
        HashMap<String, String> map = new HashMap<>(20);
        while (enums.hasMoreElements()) {
            String key = enums.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     * @return null
     */
    public static void renderJSON(HttpServletResponse response, String string) {
        try {
            response.reset();
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.setCharacterEncoding(GlobalConstants.DEFAULT_CHARACTER_ENCODING);
            response.getOutputStream().write(string.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
