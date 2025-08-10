package com.yw.live.user.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@TableName("t_user_tag")
public class UserTagPo {
    @TableId(type = IdType.INPUT)
    private Long userId;
    @TableField("tag_info_01")
    private Long tagInfo01;
    @TableField("tag_info_02")
    private Long tagInfo02;
    @TableField("tag_info_03")
    private Long tagInfo03;
    private Date createTime;
    private Date updateTime;
}
