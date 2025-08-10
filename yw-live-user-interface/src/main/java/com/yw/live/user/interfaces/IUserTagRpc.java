package com.yw.live.user.interfaces;

import com.yw.live.user.enums.UserTagEnums;

public interface IUserTagRpc {
    boolean addUserTag(Long userId, UserTagEnums tagEnum);
    boolean deleteUserTag(Long userId, UserTagEnums tagEnum);
    boolean isUserTagExist(Long userId, UserTagEnums tagEnum);
}
