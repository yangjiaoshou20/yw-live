package com.yw.live.user.provider.enums;

import lombok.Getter;

@Getter
public enum RocketMQTopicEnum {

    YW_UPDATE_USER_TOPIC("yw-update-user-topic", "更新用户延迟删除topic");

    private final String topicName;
    private final String description;

    RocketMQTopicEnum(String topicName, String description) {
        this.topicName = topicName;
        this.description = description;
    }


}
