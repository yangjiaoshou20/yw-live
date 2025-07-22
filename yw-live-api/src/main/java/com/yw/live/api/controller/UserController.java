package com.yw.live.api.controller;

import com.github.javafaker.Faker;
import com.yw.live.user.dto.UserDTO;
import com.yw.live.user.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    @GetMapping("/batchInsert")
    public List<Long> batchInsert(Integer size) {
        List<UserDTO> userDTOList = new ArrayList<>();
        Faker faker = new Faker(Locale.CHINA);
        for (int i = 0; i < size; i++) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(Long.valueOf(faker.random().nextInt(100000, 900000)));
            userDTO.setNickName(faker.funnyName().name());
            userDTO.setTrueName(faker.name().fullName());
            userDTO.setAvatar(faker.avatar().image());
            userDTO.setSex(faker.random().nextInt(0, 2));
            userDTO.setBornCity(faker.random().nextInt(100, 999));
            userDTO.setWorkCity(faker.random().nextInt(100, 999));
            userDTO.setBornDate(faker.date().birthday());
            userDTO.setCreateTime(new Date());
            userDTO.setUpdateTime(new Date());
            userDTOList.add(userDTO);
        }
        userRpc.batchInsert(userDTOList);
        return userDTOList.stream().map(UserDTO::getUserId).toList();
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
