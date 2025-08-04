package com.yw.live.api.controller;

import com.yw.live.id.generate.interfaces.IdGenerateRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/id/generate")
public class IdGenerateController {

    @DubboReference
    private IdGenerateRpc idGenerateRpc;

    @RequestMapping("/seq/{id}")
    public Long getSeqId(@PathVariable("id") int id) {
        return idGenerateRpc.getSeqId(id);
    }

    @RequestMapping("/unSeq/{id}")
    public Long getUnSeqId(@PathVariable("id") int id) {
        return idGenerateRpc.getNoSeqId(id);
    }
}
