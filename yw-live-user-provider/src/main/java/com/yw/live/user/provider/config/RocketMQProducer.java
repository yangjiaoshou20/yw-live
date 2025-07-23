package com.yw.live.user.provider.config;

import com.yw.live.user.provider.config.props.RocketMQProducerProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@Slf4j
public class RocketMQProducer {

    @Resource
    private RocketMQProducerProperties producerProperties;

    @Bean
    public MQProducer producer() {
        // 创建生产者实例，并设置生产者组名
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), r -> {
            Thread thread = new Thread(r);
            thread.setName("yw:user:update:producer" + ThreadLocalRandom.current().nextInt(100));
            return thread;
        });
        DefaultMQProducer producer = new DefaultMQProducer(producerProperties.getProducerGroup());
        // 设置 Name Server 地址，此处为示例，实际使用时请替换为真实的 Name Server 地址
        producer.setNamesrvAddr(producerProperties.getNameSrvAddr());
        producer.setAsyncSenderExecutor(threadPoolExecutor);
        try {
            producer.start();
        } catch (MQClientException e) {
            log.error("rocketMQ producer init is failure !", e);
            throw new RuntimeException(e);
        }
        return producer;
    }
}
