package com.yw.live.user.interfaces;

import com.yw.live.user.dto.UserDTO;

import java.util.List;

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

    /**
     * 批量插入用户
     */
    boolean batchInsert(List<UserDTO> userDTOList);

    /**
     * 用户信息批量查询
     */
    List<UserDTO> findUserByUserIds(List<Long> userIds);

    /**
     * 清空用户表
     */
    int deleteAll();
}
