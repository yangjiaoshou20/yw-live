package com.yw.live.api.controller;

import com.yw.live.user.enums.UserTagEnums;
import com.yw.live.user.interfaces.IUserTagRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userTag")
public class UserTagController {

    @DubboReference
    private IUserTagRpc userTagRpc;

    @PostMapping("/add")
    public boolean addUserTag(@RequestParam("userId") Long userId, @RequestParam("tag") long tag) {
        UserTagEnums userTagEnums = UserTagEnums.getUserTagEnums(tag);
        if (userTagEnums == null) {
            return false;
        }
        return userTagRpc.addUserTag(userId, userTagEnums);
    }

    @RequestMapping("/delete")
    public boolean deleteUserTag(@RequestParam("userId") Long userId,@RequestParam("tag") long tag) {
        UserTagEnums userTagEnums = UserTagEnums.getUserTagEnums(tag);
        if (userTagEnums == null) {
            return false;
        }
        return userTagRpc.deleteUserTag(userId, userTagEnums);
    }

    @RequestMapping("/exist")
    public boolean isUserTagExist(@RequestParam("userId") Long userId,@RequestParam("tag") long tag) {
        UserTagEnums userTagEnums = UserTagEnums.getUserTagEnums(tag);
        if (userTagEnums == null) {
            return false;
        }
        return userTagRpc.isUserTagExist(userId, userTagEnums);
    }
}
