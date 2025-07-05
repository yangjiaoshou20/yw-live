package com.yw.live.framework.redis.starter.utils;

import org.springframework.beans.factory.annotation.Value;

public class RedisKeyBuilder {

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String SPLIT_ITEM = ":";

    public String getSplitItem() {
        return SPLIT_ITEM;
    }

    public String getPrefix() {
        return applicationName + SPLIT_ITEM;
    }

    public String getModuleName() {
        throw new RuntimeException("ModuleName is not declared!");
    }

    public String buildUserInfoKey(Long userId) {
        return this.getPrefix() + this.getModuleName() + this.getSplitItem() + userId;
    }
}
