package com.ssafy.globalcc.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // TODO: Consider import Master - Replica strategy. Write with master while reading with replica
        /*
         LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
         .readFrom(REPLICA_PREFERRED)
         .build();

         RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(host, port);

         return new LettuceConnectionFactory(serverConfig, clientConfig);
        */
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host,port));
    }
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());   //connection
        redisTemplate.setKeySerializer(new StringRedisSerializer());    // key
        redisTemplate.setValueSerializer(new StringRedisSerializer());  // value
        return redisTemplate;
    }
}
