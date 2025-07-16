package com.yw.live.user.provider.rpc;

import com.yw.live.user.dto.UserDTO;
import com.yw.live.user.interfaces.IUserRpc;
import com.yw.live.user.provider.service.IUserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


@DubboService
public class UserRpcImpl implements IUserRpc {

    @Resource
    private IUserService userService;

    @Override
    public void sayHello(String name) {
        System.out.println("hello " + name);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        return userService.updateUserInfo(userDTO);
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        return userService.insertOne(userDTO);
    }

    @Override
    public List<UserDTO> findUserByUserIds(List<Long> userIds) {
        return userService.findUserByUserIds(userIds);
    }

    @Override
    public int deleteAll() {
        return userService.deleteAll();
    }
}
