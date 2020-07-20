package com.gitee.linzl.thread;

/**
 * @author linzhenlie
 * @date 2019/9/3
 */
public class UserContextHandler {
    private static final ThreadLocal<UserContext> USER_CONTEXT_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static void setContext(UserContext t) {
        USER_CONTEXT_THREAD_LOCAL.set(t);
    }

    public static UserContext getContext() {
        return USER_CONTEXT_THREAD_LOCAL.get();
    }

    public static void clear() {
        USER_CONTEXT_THREAD_LOCAL.remove();
    }
}
