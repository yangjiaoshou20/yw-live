package com.yw.live.user.provider.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yw.live.user.dto.UserDTO;
import com.yw.live.user.provider.dao.mapper.IUserMapper;
import com.yw.live.user.provider.dao.po.UserPO;
import com.yw.live.user.provider.enums.RocketMQTopicEnum;
import com.yw.live.user.provider.service.IUserService;
import com.yw.live.user.provider.utis.UserProviderCacheKeyBuilder;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<IUserMapper, UserPO> implements IUserService {

    @Resource
    private RedisTemplate<String, UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Resource
    private MQProducer mqProducer;

    @Override
    public UserDTO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return Optional.ofNullable(redisTemplate.opsForValue().get(userProviderCacheKeyBuilder.buildUserInfoKey(userId)))
                .orElseGet(() -> {
                    UserDTO userDTO = BeanUtil.copyProperties(baseMapper.selectById(userId), UserDTO.class);
                    if (userDTO != null) {
                        redisTemplate.opsForValue().set(String.valueOf(userProviderCacheKeyBuilder.buildUserInfoKey(userId)), userDTO, userProviderCacheKeyBuilder.createRandomExpireTime(), TimeUnit.SECONDS);
                    }
                    return userDTO;
                });
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        baseMapper.updateById(BeanUtil.copyProperties(userDTO, UserPO.class));
        redisTemplate.delete(userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()));
        Message message = new Message(RocketMQTopicEnum.YW_UPDATE_USER_TOPIC.getTopicName(), JSONUtil.toJsonStr(userDTO).getBytes());
        message.setDelayTimeLevel(2);
        try {
            mqProducer.send(message);
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            log.error("消息发送失败!", e);
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        baseMapper.insert(BeanUtil.copyProperties(userDTO, UserPO.class));
        return true;
    }

    @Override
    @Transactional
    public boolean batchInsert(List<UserDTO> userDTOList) {
        List<UserPO> list = userDTOList.stream().map(userDTO -> BeanUtil.copyProperties(userDTO, UserPO.class)).toList();
        return this.saveBatch(list,100);
    }

    @Override
    public List<UserDTO> findUserByUserIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }

        List<String> keys = userIds.stream()
                .map(userProviderCacheKeyBuilder::buildUserInfoKey)
                .toList();

        // 获取redis中的用户信息
        List<UserDTO> cacheUserDTOList = new ArrayList<>(Objects.requireNonNull(redisTemplate.opsForValue().multiGet(keys))
                .stream()
                .filter(Objects::nonNull)
                .toList());

        // 处理缓存未命中的用户信息
        List<Long> cacheUserIdList = cacheUserDTOList.stream().map(UserDTO::getUserId).toList();
        List<Long> notCacheUserIdList = userIds.stream()
                .filter(userId -> !cacheUserIdList.contains(userId))
                .toList();

        Map<Long, List<Long>> userIdMap = notCacheUserIdList.stream()
                .collect(Collectors.groupingBy(userId -> userId % 100));

        List<UserPO> dbUserPoList = new CopyOnWriteArrayList<>();
        // 并行查询数据库记录
        userIdMap.values().parallelStream()
                .forEach(ids -> dbUserPoList.addAll(baseMapper.selectBatchIds(ids)));

        Map<String, UserDTO> userPOMap = dbUserPoList.stream()
                .collect(Collectors.toMap(userPO -> userProviderCacheKeyBuilder.buildUserInfoKey(userPO.getUserId()), userPO -> BeanUtil.copyProperties(userPO, UserDTO.class)));

        // 将未命中的用户信息放入redis缓存中
        if (!CollectionUtils.isEmpty(dbUserPoList)) {
            redisTemplate.opsForValue().multiSet(userPOMap);
            redisTemplate.executePipelined(new SessionCallback<>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    userPOMap.keySet().forEach(key -> operations.expire((K) key, userProviderCacheKeyBuilder.createRandomExpireTime(), TimeUnit.SECONDS));
                    return null;
                }
            });
        }

        cacheUserDTOList.addAll(dbUserPoList.stream()
                .map(userPO -> BeanUtil.copyProperties(userPO, UserDTO.class)).toList());

        return cacheUserDTOList;
    }

    @Override
    public int deleteAll() {
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        return baseMapper.delete(queryWrapper);
    }
}
