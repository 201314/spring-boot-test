package com.gitee.linzl.thread;

/**
 * @author linzhenlie
 * @date 2019/9/3
 */
public class ThreadLocalContext {
	private static final ThreadLocal<Object> threadLocal = new InheritableThreadLocal<>();

	public static void put(Object t) {
		threadLocal.set(t);
	}

	public static Object get() {
		Object t = threadLocal.get();
		threadLocal.remove();
		return t;
	}
}
