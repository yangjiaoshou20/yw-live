package com.yw.live.user.interfaces;

import com.yw.live.user.dto.UserDTO;

public interface IUserRpc {

    void sayHello(String name);

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
