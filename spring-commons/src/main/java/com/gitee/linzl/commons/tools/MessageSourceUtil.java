package com.gitee.linzl.commons.tools;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取国际化信息的工具类
 * 
 * @author linzl
 *
 *         2016年11月25日
 */
@Component
public class MessageSourceUtil {

	private static MessageSource messageSource;

	public void setMessageSource(MessageSource messageSource) {
		MessageSourceUtil.messageSource = messageSource;
	}

	/**
	 * 获取国际化文件对应的信息值
	 * 
	 * @param code
	 *            国际化文件对应的key
	 * @return
	 */
	public static String getMessage(String code) {
		return getMessage(code, null);
	}

	/**
	 * 获取国际化文件对应的信息值
	 * 
	 * @param code
	 *            国际化文件对应的key
	 * @param params
	 *            国际化文件提醒信息中的参数值 如： 你有{0}份消息,请在{1}之前处理
	 * @return
	 */
	public static String getMessage(String code, Object[] params) {
		return getMessage(code, params, "");
	}

	/**
	 * 获取国际化文件对应的信息值
	 * 
	 * @param code
	 *            国际化文件对应的key
	 * @param params
	 *            国际化文件提醒信息中的参数值 如： 你有{0}份消息,请在{1}之前处理
	 * @param defaultMessage
	 *            当不存在key时,用defaultMessage显示
	 * @return
	 */
	public static String getMessage(String code, Object[] params, String defaultMessage) {
		String msg = messageSource.getMessage(code, params, defaultMessage, LocaleContextHolder.getLocale());
		return msg != null ? msg.trim() : defaultMessage;
	}

	/**
	 * * 获取国际化文件对应的信息值
	 * 
	 * @param code
	 *            国际化文件对应的key
	 * @param params
	 *            国际化文件提醒信息中的参数值 如： 你有{0}份消息,请在{1}之前处理
	 * @param defaultMessage
	 *            当不存在key时,用defaultMessage显示
	 * @param request
	 * @return
	 */
	public static String getMessage(String code, Object[] params, String defaultMessage, HttpServletRequest request) {
		String msg = messageSource.getMessage(code, params, defaultMessage, RequestContextUtils.getLocale(request));
		return msg != null ? msg.trim() : defaultMessage;
	}
}