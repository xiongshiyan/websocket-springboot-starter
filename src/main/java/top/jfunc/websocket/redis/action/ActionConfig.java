package top.jfunc.websocket.redis.action;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author xiongshiyan
 * 将所有的Action配置进容器，通过名字找到
 */
@Configuration
@Import({SendMessageAction.class , ChangeStatusAction.class , BroadCastAction.class , NoActionAction.class})
public class ActionConfig {
}