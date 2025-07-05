package com.yw.live.user.provider.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yw.live.user.dto.UserDTO;
import com.yw.live.user.provider.dao.mapper.IUserMapper;
import com.yw.live.user.provider.dao.po.UserPO;
import com.yw.live.user.provider.service.IUserService;
import com.yw.live.user.provider.utis.UserProviderCacheKeyBuilder;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl extends ServiceImpl<IUserMapper, UserPO> implements IUserService {

    @Resource
    private RedisTemplate<String,UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Override
    public UserDTO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return Optional.ofNullable(redisTemplate.opsForValue().get(userProviderCacheKeyBuilder.buildUserInfoKey(userId)))
                .orElseGet(() -> {
                    UserDTO userDTO = BeanUtil.copyProperties(baseMapper.selectById(userId), UserDTO.class);
                    if (userDTO != null) {
                        redisTemplate.opsForValue().set(String.valueOf(userProviderCacheKeyBuilder.buildUserInfoKey(userId)), userDTO);
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
}
