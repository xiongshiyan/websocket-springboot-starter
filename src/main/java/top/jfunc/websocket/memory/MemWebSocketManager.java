package top.jfunc.websocket.memory;

import top.jfunc.websocket.WebSocket;
import top.jfunc.websocket.WebSocketManager;
import top.jfunc.websocket.utils.WebSocketUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiongshiyan at 2018/10/10 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class MemWebSocketManager implements WebSocketManager {
    /**
     * 因为全局只有一个 WebSocketManager ，所以才敢定义为非static
     */
    private final Map<String, WebSocket> connections = new ConcurrentHashMap<>(100);

    @Override
    public WebSocket get(String identifier) {
        return connections.get(identifier);
    }

    @Override
    public void put(String identifier, WebSocket webSocket) {
        connections.put(identifier , webSocket);
    }

    @Override
    public void remove(String identifier) {
        connections.remove(identifier);
    }


    @Override
    public Map<String, WebSocket> localWebSocketMap() {
        return connections;
    }

    @Override
    public void sendMessage(String identifier, String message) {
        WebSocket webSocket = get(identifier);
        if(null == webSocket){throw new RuntimeException("identifier 不存在");}

        if(WebSocket.STATUS_AVAILABLE != webSocket.getStatus()){
            return;
        }

        WebSocketUtil.sendMessage(webSocket.getSession() , message);
    }

    @Override
    public void broadcast(String message) {
        localWebSocketMap().values().forEach(
                webSocket -> WebSocketUtil.sendMessage(
                        webSocket.getSession() , message));
    }

    /**
     * 修改当前的状态
     * @param identifier 标识
     * @param status 状态
     */
    @Override
    public void changeStatus(String identifier , int status) {
        WebSocket socket = get(identifier);
        if(null == socket){return;}

        socket.setStatus(status);
    }

    @Override
    public void onMessage(String identifier, String message) {

    }
}
