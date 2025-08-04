package com.yw.live.id.generate.provider.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@Data
@NoArgsConstructor
public class IdGenerateSeqBo {
    private int id;
    private AtomicLong currentValue;
    private Long currentStart;
    private Long currentEnd;
}
