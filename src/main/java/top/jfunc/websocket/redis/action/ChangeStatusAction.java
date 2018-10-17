package top.jfunc.websocket.redis.action;

import top.jfunc.json.impl.JSONObject;
import top.jfunc.websocket.WebSocket;
import top.jfunc.websocket.WebSocketManager;
import top.jfunc.websocket.redis.DefaultRedisReceiver;

/**
 * {
 *     "action":"changeStatus",
 *     "identifier":"xxx",
 *     "status":1
 * }
 * 改变状态的action
 * @author xiongshiyan at 2018/10/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ChangeStatusAction implements Action{
    private static final String STATUS = "status";
    @Override
    public void doMessage(WebSocketManager manager , JSONObject object) {
        if(!object.containsKey(DefaultRedisReceiver.IDENTIFIER)){
            return;
        }
        if(!object.containsKey(STATUS)){
            return;
        }
        WebSocket webSocket = manager.get(object.getString(DefaultRedisReceiver.IDENTIFIER));
        if(null == webSocket){
            return;
        }
        webSocket.setStatus(object.getInteger(STATUS));
    }
}
