package com.yw.live.user.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yw.live.user.provider.dao.po.UserTagPo;
import com.yw.live.user.enums.UserTagEnums;

public interface IUserTagService extends IService<UserTagPo> {

    boolean addUserTag(Long userId, UserTagEnums tagEnum);
    boolean deleteUserTag(Long userId, UserTagEnums tagEnum);
    boolean isUserTagExist(Long userId, UserTagEnums tagEnum);
}
