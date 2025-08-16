package com.yw.live.user.provider.utis;

import com.yw.live.framework.redis.starter.utils.RedisKeyBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
// 可放在公共依赖中进行条件注解继续依赖注入，此处认为应放置于业务模块中，故不启用
//@Conditional(RedisKeyLoadMatch.class)
public class UserTagProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static final String USER_TAG_KEY = "userTag";
    private static final String USER_TAG_LOCK_KEY = "userTagLock";

    public String getModuleName() {
        return USER_TAG_KEY;
    }

    public String buildUserTagLockKey(Long userId) {
        return this.getPrefix() + USER_TAG_LOCK_KEY + this.getSplitItem() + userId;
    }
}
