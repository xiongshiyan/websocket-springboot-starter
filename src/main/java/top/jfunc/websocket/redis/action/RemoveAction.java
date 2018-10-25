package top.jfunc.websocket.redis.action;

import top.jfunc.json.impl.JSONObject;
import top.jfunc.websocket.WebSocketManager;
import top.jfunc.websocket.redis.DefaultRedisReceiver;

import javax.websocket.Session;
import java.util.Map;

/**
 * {
 *     "action":"remove",
 *     "identifier":"xxx"
 * }
 * 给webSocket发送消息的action
 * @author xiongshiyan at 2018/10/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class RemoveAction implements Action{
    @Override
    public void doMessage(WebSocketManager manager, JSONObject object) {
        if(!object.containsKey(DefaultRedisReceiver.IDENTIFIER)){
            return;
        }

        String identifier = object.getString(DefaultRedisReceiver.IDENTIFIER);

        Map<String, Session> localWebSocketMap = manager.localWebSocketMap();
        if(localWebSocketMap.containsKey(identifier)){
            localWebSocketMap.remove(identifier);
        }
    }
}
