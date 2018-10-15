package top.jfunc.websocket.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.json.impl.JSONObject;
import top.jfunc.websocket.WebSocketManager;
import top.jfunc.websocket.redis.action.Action;
import top.jfunc.websocket.utils.SpringContextHolder;

import java.util.concurrent.CountDownLatch;


/**
 * redis消息订阅者
 * @author xiongshiyan
 */
public class RedisReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisReceiver.class);
    public static final String IDENTIFIER = "identifier";
    public static final String ACTION     = "action";

    private CountDownLatch latch;

    public RedisReceiver(CountDownLatch latch) {
        this.latch = latch;
    }

    /**
     * 此方法会被反射调用
     */
    public void receiveMessage(String message) {
        LOGGER.info(message);

        JSONObject object = new JSONObject(message);
        if(!object.containsKey(ACTION)){
            return;
        }
        String actionName = object.getString(ACTION);
        Action action = getAction(actionName);
        action.doMessage(getWebSocketManager() , object);

        //接收到消息要做的事情
        latch.countDown();
    }

    private Action getAction(String actionName) {
        boolean containsBean = SpringContextHolder.getApplicationContext().containsBean(actionName);
        if(!containsBean){
            throw new RuntimeException("容器中不存在处理这个请求 " + actionName + " 的Action，请确保正确注入了");
        }
        return SpringContextHolder.getBean(actionName, Action.class);
    }
    private WebSocketManager getWebSocketManager() {
        boolean containsBean = SpringContextHolder.getApplicationContext().containsBean(WebSocketManager.WEBSOCKET_MANAGER_NAME);
        if(!containsBean){
            throw new RuntimeException("容器中不存在WebSocketManager，请确保正确注入webSocketManger");
        }
        return SpringContextHolder.getBean(WebSocketManager.WEBSOCKET_MANAGER_NAME, WebSocketManager.class);
    }
}