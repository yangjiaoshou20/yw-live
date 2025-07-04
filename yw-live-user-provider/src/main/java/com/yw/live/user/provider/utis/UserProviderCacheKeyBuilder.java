package com.yw.live.user.provider.utis;

import com.yw.live.framework.redis.starter.utils.RedisKeyBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
// 可放在公共依赖中进行条件注解继续依赖注入，此处认为应放置于业务模块中，故不启用
//@Conditional(RedisKeyLoadMatch.class)
public class UserProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static final String USER_INFO_KEY = "userInfo";

    public String getModuleName() {
        return USER_INFO_KEY;
    }
}
