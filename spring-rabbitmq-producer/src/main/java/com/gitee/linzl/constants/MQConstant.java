package com.gitee.linzl.constants;

public class MQConstant {
	// 如发送短信验证码(点对点模式，直接由短信系统消费)
	// exchange命名:{bizObj}.{usecase}.exchange
	// bizObj业务对象，usecase业务场景
	public static final String SMS_EXCHANGE = "sms.exchange";
	// 对应于event,如置顶(settop)、刷新(refresh)
	// “_”组合表示多个event
	public static final String SMS_ROUTE_KEY = "send";
	// queue命名:{bizObj}.{usecase}.{event}.queue
	public static final String SMS_QUEUE = "sms.send.queue";

	/**
	 * “.”把路由键分为几部分，“*”匹配特定的任意文本
	 * 
	 * "*"操作符将“.”视为分隔符；“#”操作符将“.”视为关键字的匹配部分
	 */
	// 登录送积分,写入登录日志,登录地址异常邮件提醒(订阅发布模式)
	public static final String TOPIC_EXCHANGE = "login.exchange";
	public static final String TOPIC_ROUTE_KEY_LIKE = "login.integral.#.routingKey";
	public static final String TOPIC_QUEUE_LIKE = "login.integral.#.queue";

	public static final String TOPIC_ROUTE_KEY = "login.integral.routingKey";
	public static final String TOPIC_QUEUE = "login.integral.queue";

	// 站内信息,广播机制
	public static final String FANOUT_EXCHANGE = "bbs.exchange";
	public static final String FANOUT_ROUTE_KEY = "#";
	public static final String FANOUT_QUEUE_0 = "bbs.send.queue0.queue";
	public static final String FANOUT_QUEUE_1 = "bbs.send.queue1.queue";
	public static final String FANOUT_QUEUE_2 = "bbs.send.queue2.queue";

	// 还未死之前
	public static final String BEFORE_DLX_EXCHANGE = "before.dlx.exchange";
	public static final String BEFORE_DLX_QUEUE = "before.dlx.dead.letter.queue";
	public static final String BEFORE_DLX_ROUTE_KEY = "before.dlx.route.key";
	// 死信队列
	public static final String DLX_EXCHANGE = "dlx.exchange";
	public static final String DLX_QUEUE = "dlx.dead.letter.queue";
	public static final String DLX_ROUTE_KEY = "dlx.route.key";

	// reply_to
	public static final String REPLY_TO_EXCHANGE = "reply_to.exchange";
	public static final String REPLY_TO_QUEUE = "reply_to.queue";
	// “_”组合表示多个event
	public static final String REPLY_TO_ROUTE_KEY = "replyTo.route.key";
}
