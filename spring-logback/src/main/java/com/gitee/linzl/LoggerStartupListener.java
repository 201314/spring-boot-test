package com.gitee.linzl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 可以通过监听，从阿波罗获取配置
 *
 * @author linzhenlie
 * @date 2019/9/30
 */
@Slf4j
public class LoggerStartupListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {
    private boolean isStarted = false;

    @Override
    public void start() {
        if (isStarted) {
            return;
        }

        String profile = System.getenv("ACTIVE_PROFILE");
        if (Objects.isNull(profile)) {
            profile = "PRODUCTION";
        }
        /**
         * 项目logback-spring.xml可以通过 ${} 获取
         * projectName,profiles,clusterName
         */
        Context context = getContext();
        context.putProperty("ACTIVE_PROFILE", profile);
        context.putObject("projectName", "日志");
        context.putObject("profiles", "开发");
        context.putObject("clusterName", "无集群");
        isStarted = true;
    }

    @Override
    public void onStart(LoggerContext context) {
    }

    @Override
    public boolean isResetResistant() {
        return true;
    }

    @Override
    public void onReset(LoggerContext context) {

    }

    @Override
    public void onStop(LoggerContext context) {

    }

    @Override
    public void onLevelChange(Logger logger, Level level) {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }
}