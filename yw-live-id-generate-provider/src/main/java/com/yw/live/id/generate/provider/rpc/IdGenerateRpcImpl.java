package com.yw.live.id.generate.provider.rpc;

import com.yw.live.id.generate.interfaces.IdGenerateRpc;
import com.yw.live.id.generate.provider.service.IdGenerateService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {

    @Resource
    private IdGenerateService idGenerateService;

    @Override
    public Long getSeqId(int id) {
        return idGenerateService.getSeqId(id);
    }

    @Override
    public Long getNoSeqId(int id) {
        return idGenerateService.getNoSeqId(id);
    }
}
