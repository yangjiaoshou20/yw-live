package com.yw.live.user.provider.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yw.live.framework.redis.starter.utils.RedisKeyBuilder;
import com.yw.live.user.provider.config.props.RocketMQConsumerProperties;
import com.yw.live.user.provider.enums.RocketMQTopicEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Slf4j
public class RocketMQConsumer implements InitializingBean {

    @Resource
    private RocketMQConsumerProperties consumerProperties;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void afterPropertiesSet() {
        initConsume();

    }

    public void initConsume() {
        // 创建消费者实例，并设置消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerProperties.getConsumerGroup());
        // 设置 Name Server 地址，此处为示例，实际使用时请替换为真实的 Name Server 地址
        consumer.setNamesrvAddr(consumerProperties.getNameSrvAddr());
        // 订阅指定的主题和标签（* 表示所有标签）
        try {
            consumer.subscribe(RocketMQTopicEnum.YW_USER_DELAY_DELETE_TOPIC.getTopicName(), "*");
            // 注册消息监听器
            consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
                for (MessageExt msg : messages) {
                    log.info("消费消息：{}", new String(msg.getBody()));
                    JSONObject jsonObject = JSONUtil.parseObj(new String(msg.getBody()));
                    String delayDeleteKey = jsonObject.getStr(RedisKeyBuilder.DELAY_DELETE_KEY);
                    if (StrUtil.isNotBlank(delayDeleteKey)) {
                        redisTemplate.delete(delayDeleteKey);
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });

            // 启动消费者
            consumer.start();
        } catch (MQClientException e) {
            log.error("consumer start is failure !", e);
            throw new RuntimeException(e);
        }
        log.info("Consumer Started successfully !!!");

    }
}
