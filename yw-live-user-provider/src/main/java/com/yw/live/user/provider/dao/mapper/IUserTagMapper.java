package com.yw.live.user.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yw.live.user.provider.dao.po.UserTagPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserTagMapper extends BaseMapper<UserTagPo> {

    int addUserTag(Long userId, Long tag, String filedName);
}
