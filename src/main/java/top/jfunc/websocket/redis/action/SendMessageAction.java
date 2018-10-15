package top.jfunc.websocket.redis.action;

import top.jfunc.json.impl.JSONObject;
import top.jfunc.websocket.WebSocket;
import top.jfunc.websocket.WebSocketManager;
import top.jfunc.websocket.redis.RedisReceiver;
import top.jfunc.websocket.utils.WebSocketUtil;

/**
 * {
 *     "action":"sendMessage",
 *     "identifier":"xxx",
 *     "message":"xxxxxxxxxxx"
 * }
 * 给webSocket发送消息的action
 * @author xiongshiyan at 2018/10/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class SendMessageAction implements Action{
    private static final String MESSAGE = "message";
    @Override
    public void doMessage(WebSocketManager manager, JSONObject object) {
        if(!object.containsKey(RedisReceiver.IDENTIFIER)){
            return;
        }
        if(!object.containsKey(MESSAGE)){
            return;
        }

        String identifier = object.getString(RedisReceiver.IDENTIFIER);

        WebSocket webSocket = manager.get(identifier);
        if(null == webSocket || WebSocket.STATUS_AVAILABLE != webSocket.getStatus()){
            return;
        }
        WebSocketUtil.sendMessage(webSocket.getSession() , object.getString(MESSAGE));
    }
}
