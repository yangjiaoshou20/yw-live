package com.yw.live.id.generate.provider.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.yw.live.id.generate.provider.bo.IdGenerateSeqBo;
import com.yw.live.id.generate.provider.bo.IdGenerateUnSeqBo;
import com.yw.live.id.generate.provider.enums.IdGenerateEnums;
import com.yw.live.id.generate.provider.mapper.IdGenerateMapper;
import com.yw.live.id.generate.provider.po.IdGenerateConfigPo;
import com.yw.live.id.generate.provider.service.IdGenerateService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdGenerateServiceImpl implements IdGenerateService, InitializingBean {

    private final Logger log = LoggerFactory.getLogger(IdGenerateServiceImpl.class);

    @Resource
    private IdGenerateMapper idGenerateMapper;

    private static final Map<Integer, IdGenerateSeqBo> idSeqBoMap = new ConcurrentHashMap<>();
    private static final Map<Integer, IdGenerateUnSeqBo> idUnSeqBoMap = new ConcurrentHashMap<>();
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), r -> {
        Thread thread = new Thread(r);
        thread.setName("id-generate-thread-" + ThreadLocalRandom.current().nextInt());
        return thread;
    });

    @Override
    public Long getSeqId(int id) {
        IdGenerateSeqBo idGenerateSeqBo = idSeqBoMap.get(id);
        if (idGenerateSeqBo == null) {
            log.error("idGenerateSeqBo is null");
            return null;
        }
        refreshLocalSeqIdMap(idGenerateSeqBo);
        return idGenerateSeqBo.getCurrentValue().getAndIncrement();
    }

    private void refreshLocalSeqIdMap(IdGenerateSeqBo idGenerateSeqBo) {
        IdGenerateEnums generateEnums = IdGenerateEnums.getById(idGenerateSeqBo.getId());
        if (generateEnums == null) {
            log.error("generateEnums is null seq enum is not config id is {}", idGenerateSeqBo.getId());
            throw new RuntimeException("id is " + idGenerateSeqBo.getId() + " generateEnums is not config correct !");
        }
        // 达到加载因子更新本地ID
        if ((idGenerateSeqBo.getCurrentValue().get() - idGenerateSeqBo.getCurrentStart()) > generateEnums.loadFactor * (idGenerateSeqBo.getCurrentEnd() - idGenerateSeqBo.getCurrentStart())) {
            if (generateEnums.semaphore.tryAcquire()) {
                threadPoolExecutor.execute(() -> {
                    IdGenerateConfigPo generateConfigPo = idGenerateMapper.selectById(idGenerateSeqBo.getId());
                    tryUpdateRecordAndLocalIdBo(generateConfigPo);
                });
            }
        }
    }

    @Override
    public Long getNoSeqId(int id) {
        IdGenerateUnSeqBo idGenerateUnSeqBo = idUnSeqBoMap.get(id);
        Long poll = idGenerateUnSeqBo.getIdQueue().poll();
        refreshLocalUnSeqIdMap(idGenerateUnSeqBo);
        return poll;
    }

    private void refreshLocalUnSeqIdMap(IdGenerateUnSeqBo idGenerateUnSeqBo) {
        IdGenerateEnums generateEnums = IdGenerateEnums.getById(idGenerateUnSeqBo.getId());
        if (generateEnums == null) {
            log.error("generateEnums is null unSeq enum is not config id is {}", idGenerateUnSeqBo.getId());
            throw new RuntimeException("id is " + idGenerateUnSeqBo.getId() + " generateEnums is not config correct!");
        }
        if (idGenerateUnSeqBo.getIdQueue().size() < (idGenerateUnSeqBo.getCurrentEnd() - idGenerateUnSeqBo.getCurrentStart()) * (1 - generateEnums.loadFactor)) {
            if (generateEnums.semaphore.tryAcquire()) {
                IdGenerateConfigPo generateConfigPo = idGenerateMapper.selectById(idGenerateUnSeqBo.getId());
                threadPoolExecutor.execute(() -> {
                    this.tryUpdateRecordAndLocalIdBo(generateConfigPo);
                });
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        // 启动时必须重新加载新的id段(原因：若某个id段使用过程中服务宕机，重启后需要获取新的id段来避免使用到之前使用过的id段)
        List<IdGenerateConfigPo> generateConfigPoList = idGenerateMapper.selectList(null);
        if (CollectionUtil.isEmpty(generateConfigPoList)) {
            throw new RuntimeException("未查询到id生成配置信息！");
        }
        for (IdGenerateConfigPo po : generateConfigPoList) {
            // 初始化抢占ID段
            tryUpdateRecordAndLocalIdBo(po);
        }
    }

    public void tryUpdateRecordAndLocalIdBo(IdGenerateConfigPo po) {
        log.info("before update current db po info is {}", JSONUtil.toJsonStr(po));
        int i = idGenerateMapper.updateIdGenerateConfig(po.getId(), po.getVersion());
        if (i > 0) {
            po = idGenerateMapper.selectById(po.getId());
            refreshLocalIdMap(po);
            return;
        }
        po = idGenerateMapper.selectById(po.getId());
        tryUpdateRecordAndLocalIdBo(po);
    }

    private void refreshLocalIdMap(IdGenerateConfigPo po) {
        log.info("after update db po info is {}", JSONUtil.toJsonStr(po));
        if (po.getIsSeq() == 1) {
            IdGenerateSeqBo idGenerateSeqBo = new IdGenerateSeqBo();
            idGenerateSeqBo.setId(po.getId());
            idGenerateSeqBo.setCurrentValue(new AtomicLong(po.getCurrentStart()));
            idGenerateSeqBo.setCurrentStart(po.getCurrentStart());
            idGenerateSeqBo.setCurrentEnd(po.getNextThreshold());
            idSeqBoMap.put(po.getId(), idGenerateSeqBo);
        } else {
            IdGenerateUnSeqBo idGenerateUnSeqBo = new IdGenerateUnSeqBo();
            idGenerateUnSeqBo.setId(po.getId());
            idGenerateUnSeqBo.setCurrentStart(po.getCurrentStart());
            idGenerateUnSeqBo.setCurrentEnd(po.getNextThreshold());
            List<Long> idList = new ArrayList<>();
            for (long j = po.getCurrentStart(); j < po.getNextThreshold(); j++) {
                idList.add(j);
            }
            Collections.shuffle(idList);
            ConcurrentLinkedDeque<Long> idQueue = new ConcurrentLinkedDeque<>(idList);
            idGenerateUnSeqBo.setIdQueue(idQueue);
            idUnSeqBoMap.put(po.getId(), idGenerateUnSeqBo);
        }
    }
}
