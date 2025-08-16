package com.yw.live.user.provider.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yw.live.framework.redis.starter.utils.RedisKeyBuilder;
import com.yw.live.user.dto.UserTagDTO;
import com.yw.live.user.enums.UserTagEnums;
import com.yw.live.user.provider.dao.mapper.IUserTagMapper;
import com.yw.live.user.provider.dao.po.UserTagPo;
import com.yw.live.user.provider.enums.RocketMQTopicEnum;
import com.yw.live.user.provider.service.IUserTagService;
import com.yw.live.user.provider.utis.UserTagProviderCacheKeyBuilder;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserTagServiceImpl extends ServiceImpl<IUserTagMapper, UserTagPo> implements IUserTagService {

    private final Logger log = LoggerFactory.getLogger(UserTagServiceImpl.class);

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserTagProviderCacheKeyBuilder userTagProviderCacheKeyBuilder;

    @Resource
    private RedisTemplate<String, UserTagDTO> redisTemplate;

    @Resource
    private MQProducer mqProducer;

    @Override
    public boolean addUserTag(Long userId, UserTagEnums tagEnum) {
        int i = baseMapper.addUserTag(userId, tagEnum.tag, tagEnum.filedName);
        if (i > 0) {
            delayDeleteUserTag(userId);
            return true;
        }
        UserTagPo userTagPo = baseMapper.selectById(userId);
        if (userTagPo != null) {
            return false; // 重复设置
        }
        // 分布式集群环境下需添加分布式锁
        RLock lock = redissonClient.getLock(userTagProviderCacheKeyBuilder.buildUserTagLockKey(userId));
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
        int flag = baseMapper.deleteUserTag(userId, tagEnum.tag, tagEnum.filedName);
        if (flag > 0) {
            delayDeleteUserTag(userId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isUserTagExist(Long userId, UserTagEnums tagEnum) {
        UserTagDTO userTagDTO = getDbUserTagDTO(userId);
        if (userTagDTO == null) return false;
        Class<? extends UserTagDTO> aClass = userTagDTO.getClass();
        try {
            Field field = aClass.getDeclaredField(tagEnum.poFiledName);
            field.setAccessible(true);
            Object val = field.get(userTagDTO);
            if (val instanceof Long tagDb) {
                return tagEnum.tag == (tagDb & tagEnum.tag);
            }
        } catch (Exception e) {
            log.error("属性获取失败 property is {},po is {}", tagEnum.poFiledName, JSONUtil.toJsonStr(userTagDTO), e);
            return false;
        }
        return false;
    }

    private UserTagDTO getDbUserTagDTO(Long userId) {
        UserTagDTO userTagDTO = redisTemplate.opsForValue().get(userTagProviderCacheKeyBuilder.buildKey(userId));
        if (userTagDTO == null) {
            UserTagPo userTagPo = baseMapper.selectById(userId);
            if (userTagPo == null) {
                return null;
            }
            userTagDTO = BeanUtil.copyProperties(userTagPo, UserTagDTO.class);
            redisTemplate.opsForValue().set(userTagProviderCacheKeyBuilder.buildKey(userId), userTagDTO, userTagProviderCacheKeyBuilder.createRandomExpireTime(), TimeUnit.SECONDS);
        }
        return userTagDTO;
    }

    private void delayDeleteUserTag(Long userId) {
        JSONObject jsonObject = JSONUtil.createObj().set(RedisKeyBuilder.DELAY_DELETE_KEY, userTagProviderCacheKeyBuilder.buildKey(userId));
        Message message = new Message(RocketMQTopicEnum.YW_USER_DELAY_DELETE_TOPIC.getTopicName(), JSONUtil.toJsonStr(jsonObject).getBytes());
        message.setDelayTimeLevel(2);
        try {
            mqProducer.send(message);
        } catch (Exception e) {
            log.error("消息发送失败!", e);
            throw new RuntimeException(e);
        }
        redisTemplate.delete(userTagProviderCacheKeyBuilder.buildKey(userId));
    }
}
