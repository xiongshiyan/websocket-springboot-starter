package top.jfunc.websocket.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import top.jfunc.websocket.WebSocketManager;
import top.jfunc.websocket.utils.SpringContextHolder;

import java.util.concurrent.CountDownLatch;

/**
 * @author xiongshiyan
 * redis管理websocket配置，利用redis的发布订阅功能实现，具备集群功能
 */
@Configuration
public class RedisWebSocketConfig {
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(RedisWebSocketManager.CHANNEL));

        return container;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean(WebSocketManager.WEBSOCKET_MANAGER_NAME)
    @ConditionalOnMissingBean(name = WebSocketManager.WEBSOCKET_MANAGER_NAME)
    public RedisWebSocketManager webSocketManager(@Autowired StringRedisTemplate stringRedisTemplate) {
        return new RedisWebSocketManager(stringRedisTemplate);
    }

    @Bean
    public RedisReceiver receiver(
            @Autowired@Qualifier("latch") CountDownLatch latch) {
        return new RedisReceiver(latch);
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    public CountDownLatch latch() {
        return new CountDownLatch(1);
    }

    /**
     * applicationContext全局保存器
     */
    @Bean
    public SpringContextHolder springContextHolder(){
        return new SpringContextHolder();
    }
}