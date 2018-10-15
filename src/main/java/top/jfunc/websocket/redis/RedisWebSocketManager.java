package top.jfunc.websocket.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import top.jfunc.websocket.memory.MemWebSocketManager;
import top.jfunc.websocket.WebSocket;
import top.jfunc.websocket.redis.action.BroadCastAction;
import top.jfunc.websocket.redis.action.ChangeStatusAction;
import top.jfunc.websocket.redis.action.SendMessageAction;
import top.jfunc.websocket.utils.JsonUtil;
import top.jfunc.websocket.utils.WebSocketUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket的session无法序列化,所以session还是保存在本地内存中，发送消息这种就走订阅发布模式
 * 1.redis或者mq进行发布订阅，广播->有某个节点能找到此人就发送消息，其他的忽略
 * 2.Nginx进行IP hash 可以使用{@link MemWebSocketManager}
 *
 * 3.需要扩展不同的功能,就写相应的Action,放入容器中,然后给订阅的频道发布一条包含该Action的JSON串
 * @see RedisWebSocketManager#sendMessage
 * @author xiongshiyan at 2018/10/10 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class RedisWebSocketManager extends MemWebSocketManager {
    public static final String CHANNEL    = "websocket";
    private static final String COUNT_KEY = "RedisWebSocketManagerCountKey";
    private StringRedisTemplate stringRedisTemplate;

    public RedisWebSocketManager(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public void put(String identifier, WebSocket webSocket) {
        super.put(identifier, webSocket);
        //在线数量加1
        countChange(1);
    }

    @Override
    public void remove(String identifier) {
        super.remove(identifier);
        //在线数量减1
        countChange(-1);
    }

    @Override
    public int size() {
        return getCount();
    }

    @Override
    public void sendMessage(String identifier, String message) {
        WebSocket webSocket = get(identifier);
        //本地能找到就直接发
        if(null != webSocket && WebSocket.STATUS_AVAILABLE == webSocket.getStatus()){
            WebSocketUtil.sendMessage(webSocket.getSession() , message);
            return;
        }


        Map<String , Object> map = new HashMap<>(3);
        map.put(RedisReceiver.ACTION , SendMessageAction.class.getName());
        map.put(RedisReceiver.IDENTIFIER , identifier);
        map.put("message" , message);
        //在websocket频道上发布发送消息的消息
        stringRedisTemplate.convertAndSend(CHANNEL , JsonUtil.serializeMap(map));
    }

    @Override
    public void broadcast(String message) {
        Map<String , Object> map = new HashMap<>(2);
        map.put(RedisReceiver.ACTION , BroadCastAction.class.getName());
        map.put("message" , message);
        //在websocket频道上发布广播的消息
        stringRedisTemplate.convertAndSend(CHANNEL , JsonUtil.serializeMap(map));
    }

    @Override
    public void changeStatus(String identifier, int status) {
        WebSocket webSocket = get(identifier);
        if(null != webSocket){
            webSocket.setStatus(status);
            return;
        }
        Map<String , Object> map = new HashMap<>(3);
        map.put(RedisReceiver.ACTION , ChangeStatusAction.class.getName());
        map.put(RedisReceiver.IDENTIFIER , identifier);
        map.put("status" , status);
        //在websocket频道上发布改变状态的消息
        stringRedisTemplate.convertAndSend(CHANNEL , JsonUtil.serializeMap(map));
    }

    /**
     * 增减在线数量
     */
    private void countChange(int delta){
        ValueOperations<String, String> value = stringRedisTemplate.opsForValue();

        //获取在线当前数量
        int count = getCount(value);

        count = count + delta;
        count = count > 0 ? count : 0;

        //设置新的数量
        value.set(COUNT_KEY , "" + count);
    }

    /**
     * 获取当前在线数量
     */
    private int getCount(){
        ValueOperations<String, String> value = stringRedisTemplate.opsForValue();
        return getCount(value);
    }
    private int getCount(ValueOperations<String, String> value) {
        String countStr = value.get(COUNT_KEY);
        int count = 0;
        if(null != countStr){
            count = Integer.parseInt(countStr);
        }
        return count;
    }
}
