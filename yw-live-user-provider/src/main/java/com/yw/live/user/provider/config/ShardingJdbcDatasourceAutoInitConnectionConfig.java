package com.yw.live.user.provider.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class ShardingJdbcDatasourceAutoInitConnectionConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardingJdbcDatasourceAutoInitConnectionConfig.class);


    /**
     * 初始化数据库链接
     *
     * @param dataSource 数据源
     * @return ApplicationRunner方法
     */
    @Bean
    public ApplicationRunner runner(DataSource dataSource) {
        return args -> {
            LOGGER.info("dataSource: {}", dataSource.getClass().getName());
            Connection connection = dataSource.getConnection();
            LOGGER.info("dataSource init is ok!");
            connection.close();
        };
    }
}