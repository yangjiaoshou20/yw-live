package com.yw.live.id.generate.provider.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum IdGenerateEnums {

    SEQ_ID_GENERATE(1, 0.75f, new Semaphore(1), "有序ID生成器"),
    UN_SEQ_ID_GENERATE(2, 0.75f, new Semaphore(1), "有序ID生成器");

    public final int id;
    public final float loadFactor;
    public final Semaphore semaphore;
    public final String description;

    IdGenerateEnums(int id, float loadFactor, Semaphore semaphore, String description) {
        this.id = id;
        this.loadFactor = loadFactor;
        this.semaphore = semaphore;
        this.description = description;
    }

    public static IdGenerateEnums getById(int id) {
        return Arrays.stream(IdGenerateEnums.values())
                .filter(e -> e.id == id)
                .findFirst().orElse(null);
    }

    public static Map<Integer, IdGenerateEnums> getEnumsMap() {
        return Arrays.stream(IdGenerateEnums.values())
                .collect(Collectors.toMap(e -> e.id, Function.identity()));
    }
}
