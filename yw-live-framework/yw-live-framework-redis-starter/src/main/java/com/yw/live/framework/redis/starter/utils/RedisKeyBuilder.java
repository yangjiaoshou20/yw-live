package com.yw.live.framework.redis.starter.utils;

import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ThreadLocalRandom;

public class RedisKeyBuilder {

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String SPLIT_ITEM = ":";
    public static final String DELAY_DELETE_KEY = "deleteKey";

    public String getSplitItem() {
        return SPLIT_ITEM;
    }

    public String getPrefix() {
        return applicationName + SPLIT_ITEM;
    }

    public String getModuleName() {
        throw new RuntimeException("ModuleName is not declared!");
    }

    public String buildKey(Long id) {
        return this.getPrefix() + this.getModuleName() + this.getSplitItem() + id;
    }

    // 生成随机过期时间，单位：秒
    public long createRandomExpireTime() {
        return ThreadLocalRandom.current().nextLong(10) * 60 + 30 * 60;
    }
}
