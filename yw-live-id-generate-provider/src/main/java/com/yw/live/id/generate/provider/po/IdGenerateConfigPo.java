package com.yw.live.id.generate.provider.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("t_id_generate_config")
public class IdGenerateConfigPo {
    private Integer id;
    private String remark;
    private Long nextThreshold;
    private Long initNum;
    private Long currentStart;
    private int step;
    private int isSeq;
    private String idPrefix;
    private int version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
