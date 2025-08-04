package com.yw.live.id.generate.provider.bo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.concurrent.ConcurrentLinkedDeque;

@Data
@NoArgsConstructor
public class IdGenerateUnSeqBo {
    private int id;
    private Long currentStart;
    private Long currentEnd;
    private ConcurrentLinkedDeque<Long> idQueue;
}
