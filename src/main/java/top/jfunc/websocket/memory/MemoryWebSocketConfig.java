package top.jfunc.websocket.memory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.jfunc.websocket.WebSocketManager;
import top.jfunc.websocket.utils.SpringContextHolder;

/**
 * @author xiongshiyan
 * 内存管理websocket配置
 */
@Configuration
public class MemoryWebSocketConfig {
    /**
     * applicationContext全局保存器
     */
    @Bean
    public SpringContextHolder springContextHolder(){
        return new SpringContextHolder();
    }

    @Bean(WebSocketManager.WEBSOCKET_MANAGER_NAME)
    @ConditionalOnMissingBean(name = WebSocketManager.WEBSOCKET_MANAGER_NAME)
    public WebSocketManager webSocketManager() {
        return new MemWebSocketManager();
    }
}