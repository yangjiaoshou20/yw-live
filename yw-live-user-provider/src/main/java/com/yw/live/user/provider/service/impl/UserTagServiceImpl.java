package com.yw.live.user.provider.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yw.live.user.provider.dao.mapper.IUserTagMapper;
import com.yw.live.user.provider.dao.po.UserTagPo;
import com.yw.live.user.enums.UserTagEnums;
import com.yw.live.user.provider.service.IUserTagService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Date;

@Service
public class UserTagServiceImpl extends ServiceImpl<IUserTagMapper, UserTagPo> implements IUserTagService {


    @Resource
    private IUserTagMapper userTagMapper;

    @Override
    public boolean addUserTag(Long userId, UserTagEnums tagEnum) {
        int i = userTagMapper.addUserTag(userId, tagEnum.tag, tagEnum.filedName);
        if (i > 0) {
            return true;
        }
        UserTagPo userTagPo = userTagMapper.selectById(userId);
        if (userTagPo != null) {
            return false; // 重复设置
        }
        // 没有该用户的标签记录
        userTagPo = new UserTagPo();
        userTagPo.setUserId(userId);
        userTagPo.setTagInfo01(tagEnum.tag);
        userTagPo.setCreateTime(new Date());
        userTagPo.setUpdateTime(new Date());
        return userTagMapper.insert(userTagPo) > 0;
    }

    @Override
    public boolean deleteUserTag(Long userId, UserTagEnums tagEnum) {
        return userTagMapper.deleteUserTag(userId,tagEnum.tag,tagEnum.filedName) > 0;
    }

    @Override
    public boolean isUserTagExist(Long userId, UserTagEnums tagEnum) {
        UserTagPo userTagPo = userTagMapper.selectById(userId);
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
