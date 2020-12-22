package com.gitee.linzl.commons.exception;

import com.gitee.linzl.commons.enums.IBaseErrorCode;

/**
 * 异常基类
 *
 * @author linzhenlie
 * @date 2019/8/26
 */
public class BaseException extends RuntimeException {
    /**
     * 国际化文件对应的key
     */
    private String code;

    private final static String DEFAULT_MESSAGE = "无提醒内容";

    /**
     * 提醒内容
     */
    private String message = DEFAULT_MESSAGE;
    /**
     * 国际化文件提醒信息中的参数值
     * <p>
     * 如： 你有{0}份消息,请在{1}之前处理
     */
    private Object[] params;

    private static final long serialVersionUID = 3096778872435935504L;

    /**
     * @param code 国际化文件对应的key
     */
    public BaseException(String code) {
        this.code = code;
    }


    /**
     * @param code    国际化文件对应的key
     * @param message 默认的信息提醒
     */
    public BaseException(final String code, final String message) {
        super("【" + code + "】" + message);
        this.code = code;
        this.message = message;
    }

    public BaseException(final String code, final Throwable throwable) {
        super(throwable);
        this.code = code;
    }

    /**
     * @param code    国际化文件对应的key
     * @param params  你有{0}份消息,请在{1}之前处理
     * @param message 默认的信息提醒
     */
    public BaseException(final String code, final Object[] params, final String message) {
        super(message);
        this.code = code;
        this.params = params;
        this.message = message;
    }


    /**
     * @return Returns the code. If there is a chained exception it recursively
     * calls {@code getCode()} on the cause of the chained exception rather
     * than the returning the code itself.
     */
    public final String getCode() {
        final Throwable cause = this.getCause();
        if (cause instanceof BaseException) {
            return ((BaseException) cause).getCode();
        }
        return this.code;
    }

    public final Object[] getParams() {
        final Throwable cause = this.getCause();
        if (cause instanceof BaseException) {
            return ((BaseException) cause).getParams();
        }
        return this.params;
    }

    public final String getDefaultMessage() {
        final Throwable cause = this.getCause();
        if (cause instanceof BaseException) {
            return ((BaseException) cause).getDefaultMessage();
        }
        return this.message;
    }
}
