package com.yw.live.aip.controller;

import com.yw.live.user.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    @DubboReference
    private IUserRpc userRpc;

    @RequestMapping("/sayHello/{name}")
    public void sayHello(@PathVariable(name = "name") String name) {
        System.out.println("api:"+name);
        userRpc.sayHello(name);
    }
}
