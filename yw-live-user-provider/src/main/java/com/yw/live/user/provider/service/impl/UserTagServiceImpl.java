package com.yw.live.user.provider.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yw.live.user.enums.UserTagEnums;
import com.yw.live.user.provider.dao.mapper.IUserTagMapper;
import com.yw.live.user.provider.dao.po.UserTagPo;
import com.yw.live.user.provider.service.IUserTagService;
import com.yw.live.user.provider.utis.UserTagProviderCacheKeyBuilder;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserTagServiceImpl extends ServiceImpl<IUserTagMapper, UserTagPo> implements IUserTagService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserTagProviderCacheKeyBuilder userTagProviderCacheKeyBuilder;

    @Override
    public boolean addUserTag(Long userId, UserTagEnums tagEnum) {
        int i = baseMapper.addUserTag(userId, tagEnum.tag, tagEnum.filedName);
        if (i > 0) {
            return true;
        }
        UserTagPo userTagPo = baseMapper.selectById(userId);
        if (userTagPo != null) {
            return false; // 重复设置
        }
        // 分布式集群环境下需添加分布式锁
        RLock lock = redissonClient.getLock(userTagProviderCacheKeyBuilder.buildKey(userId));
        try {
            if (lock.tryLock(30, TimeUnit.SECONDS)) {
                // 没有该用户的标签记录
                userTagPo = new UserTagPo();
                userTagPo.setUserId(userId);
                userTagPo.setCreateTime(new Date());
                userTagPo.setUpdateTime(new Date());
                baseMapper.insert(userTagPo);
                return baseMapper.addUserTag(userId, tagEnum.tag, tagEnum.filedName) > 0;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return false;
    }

    @Override
    public boolean deleteUserTag(Long userId, UserTagEnums tagEnum) {
        return baseMapper.deleteUserTag(userId,tagEnum.tag,tagEnum.filedName) > 0;
    }

    @Override
    public boolean isUserTagExist(Long userId, UserTagEnums tagEnum) {
        UserTagPo userTagPo = baseMapper.selectById(userId);
        if (userTagPo == null) {
            return false;
        }
        Class<? extends UserTagPo> aClass = userTagPo.getClass();
        try {
            Field field = aClass.getDeclaredField(tagEnum.poFiledName);
            field.setAccessible(true);
            Object val = field.get(userTagPo);
            if (val instanceof Long tagDb) {
                return tagEnum.tag == (tagDb & tagEnum.tag);
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
