package com.yw.live.id.generate.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yw.live.id.generate.provider.po.IdGenerateConfigPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IdGenerateMapper extends BaseMapper<IdGenerateConfigPo> {

    int updateIdGenerateConfig(int id, Integer version);
}
