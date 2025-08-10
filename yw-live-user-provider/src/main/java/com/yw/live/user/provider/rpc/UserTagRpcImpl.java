package com.yw.live.user.provider.rpc;

import com.yw.live.user.enums.UserTagEnums;
import com.yw.live.user.interfaces.IUserTagRpc;
import com.yw.live.user.provider.service.IUserTagService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class UserTagRpcImpl implements IUserTagRpc {

    @Resource
    private IUserTagService userTagService;

    @Override
    public boolean addUserTag(Long userId, UserTagEnums tagEnum) {
        return userTagService.addUserTag(userId, tagEnum);
    }

    @Override
    public boolean deleteUserTag(Long userId, UserTagEnums tagEnum) {
        return userTagService.deleteUserTag(userId, tagEnum);
    }

    @Override
    public boolean isUserTagExist(Long userId, UserTagEnums tagEnum) {
        return userTagService.isUserTagExist(userId, tagEnum);
    }
}
