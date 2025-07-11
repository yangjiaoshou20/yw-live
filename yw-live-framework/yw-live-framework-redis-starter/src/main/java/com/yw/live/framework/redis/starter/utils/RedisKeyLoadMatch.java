package com.yw.live.framework.redis.starter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * 让各类keyBuilder能够根据类名与引入的模块的spring.application.name做匹配，进行条件装配
 */
public class RedisKeyLoadMatch implements Condition {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedisKeyLoadMatch.class);

    private static final String PREFIX = "yw";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String appName = context.getEnvironment().getProperty("spring.application.name");
        if (appName == null) {
            LOGGER.error("没有匹配到应用名称，所以无法加载任何RedisKeyBuilder对象");
            return false;
        }
        try {
            Field classNameField = metadata.getClass().getDeclaredField("className");
            classNameField.setAccessible(true);
            String keyBuilderName = (String) classNameField.get(metadata);
            List<String> splitList = Arrays.asList(keyBuilderName.split("\\."));
            //忽略大小写，统一前缀
            String classSimplyName = PREFIX + splitList.get(splitList.size() - 1).toLowerCase();
            boolean matchStatus = classSimplyName.contains(appName.replaceAll("-", ""));
            LOGGER.info("keyBuilderClass is {},matchStatus is {}", keyBuilderName, matchStatus);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
