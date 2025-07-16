package com.yw.live.api.controller;

import com.yw.live.user.dto.UserDTO;
import com.yw.live.user.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @DubboReference
    private IUserRpc userRpc;

    @GetMapping("/getUserInfo")
    public UserDTO getUserInfo(Long userId) {
        return userRpc.getUserById(userId);
    }

    @GetMapping("/updateUserInfo")
    public boolean updateUserInfo(UserDTO userDTO) {
        return userRpc.updateUserInfo(userDTO);
    }

    @GetMapping("/insertUserInfo")
    public boolean insertUserInfo(UserDTO userDTO) {
        return userRpc.insertOne(userDTO);
    }

    @PostMapping("/findUserByUserIds")
    public List<UserDTO> findUserByUserIds(@RequestBody List<Long> userIds) {
        return userRpc.findUserByUserIds(userIds);
    }

    @GetMapping("/deleteAll")
    public int deleteAll() {
        return userRpc.deleteAll();
    }
}
