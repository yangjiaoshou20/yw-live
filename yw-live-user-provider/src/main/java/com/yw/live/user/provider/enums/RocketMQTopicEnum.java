package com.yw.live.user.provider.enums;

import lombok.Getter;

@Getter
public enum RocketMQTopicEnum {

    YW_USER_DELAY_DELETE_TOPIC("yw-user-delay-delete-topic", "用户模块延迟删除topic");

    private final String topicName;
    private final String description;

    RocketMQTopicEnum(String topicName, String description) {
        this.topicName = topicName;
        this.description = description;
    }


}
