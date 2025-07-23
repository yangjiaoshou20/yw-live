package com.yw.live.user.provider.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "yw.mq.producer")
@Data
public class RocketMQProducerProperties {
    private String nameSrvAddr;
    private String producerGroup;
}
