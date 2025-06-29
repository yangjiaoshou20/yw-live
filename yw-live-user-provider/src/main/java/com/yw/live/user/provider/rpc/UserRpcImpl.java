package com.yw.live.user.provider.rpc;

import com.yw.live.user.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboService;


@DubboService
public class UserRpcImpl implements IUserRpc {

    @Override
    public void sayHello(String name) {
        System.out.println("hello " + name);
    }
}
