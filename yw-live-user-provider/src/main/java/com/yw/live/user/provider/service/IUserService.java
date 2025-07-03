package com.yw.live.user.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yw.live.user.dto.UserDTO;
import com.yw.live.user.provider.dao.po.UserPO;

public interface IUserService extends IService<UserPO> {

    /**
     * 根据用户id进行查询
     */
    UserDTO getUserById(Long userId);

    /**
     * 更新用户信息
     */
    boolean updateUserInfo(UserDTO userDTO);

    /**
     * 插入用户
     */
    boolean insertOne(UserDTO userDTO);

}
