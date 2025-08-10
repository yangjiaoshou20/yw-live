package com.yw.live.user.enums;

import java.util.Arrays;

public enum UserTagEnums {

    IS_VIP(Double.valueOf(Math.pow(2, 0)).longValue(), "是否为VIP用户", "tag_info_01","tagInfo01"),
    IS_ACTIVE(Double.valueOf(Math.pow(2, 1)).longValue(), "是否为活跃用户", "tag_info_01","tagInfo01"),
    IS_OLD_USER(Double.valueOf(Math.pow(2, 2)).longValue(), "是否为老用户", "tag_info_01","tagInfo01"),
    IS_RECHARGE_USER(Double.valueOf(Math.pow(2, 3)).longValue(), "是否为充值用户", "tag_info_01","tagInfo01"),
    ;

    public final Long tag;
    public final String description;
    public final String filedName;
    public final String poFiledName;

    UserTagEnums(Long tag, String description, String filedName, String poFiledName) {
        this.tag = tag;
        this.description = description;
        this.filedName = filedName;
        this.poFiledName = poFiledName;
    }

    public static UserTagEnums getUserTagEnums(Long tag) {
        return Arrays.stream(UserTagEnums.values()).filter(e -> e.tag.equals(tag)).findFirst().orElse(null);
    }
}
