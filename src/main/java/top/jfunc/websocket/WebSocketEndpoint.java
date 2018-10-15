package top.jfunc.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.common.utils.StrUtil;
import top.jfunc.websocket.utils.SpringContextHolder;

import javax.websocket.*;
import javax.websocket.server.PathParam;

/**
 * NOTE: Nginx反向代理要支持WebSocket，需要配置几个header，否则连接的时候就报404
       proxy_http_version 1.1;
       proxy_set_header Upgrade $http_upgrade;
       proxy_set_header Connection "upgrade";
       proxy_read_timeout 3600s; //这个时间不长的话就容易断开连接
 * @author xiongshiyan at 2018/10/10 , contact me with email yanshixiong@126.com or phone 15208384257
 */
/*@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ServerEndpoint(value ="/websocket/connect/{identifier}")*/

/**
 * 写自己的Endpoint类，继承自此类，添加@ServerEndpoint、@Component注解即可
 */
public class WebSocketEndpoint {
    /**
     * 路径标识：目前使用token来代表
     */
    private static final String IDENTIFIER = "identifier";
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEndpoint.class);

    /// 无法通过这种方式注入组件
    /*@Autowired
    private WebSocketManager websocketManager;*/

    public WebSocketEndpoint() {
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(IDENTIFIER) String identifier) {
        try {
            logger.info("*** WebSocket opened from sessionId " + session.getId() + " , identifier = " + identifier);
            if(StrUtil.isBlank(identifier)){
                return;
            }
            WebSocket socket = new WebSocket();
            socket.setIdentifier(identifier);
            socket.setSession(session);
            socket.setStatus(WebSocket.STATUS_AVAILABLE);
            //像刷新这种，id一样，session不一样，后面的覆盖前面的

            WebSocketManager websocketManager = getWebSocketManager();

            websocketManager.put(identifier , socket);

        } catch (Exception e) {
            logger.error(e.getMessage() , e);
        }
    }

    @OnClose
    public void onClose(Session session , @PathParam(IDENTIFIER) String identifier) {
        logger.info("*** WebSocket closed from sessionId " + session.getId() + " , identifier = " + identifier);
        getWebSocketManager().remove(identifier);
    }

    @OnMessage
    public void onMessage(String message, Session session , @PathParam(IDENTIFIER) String identifier) {
        logger.info("接收到的数据为：" + message + " from sessionId " + session.getId() + " , identifier = " + identifier);

        getWebSocketManager().onMessage(identifier , message);
    }

    @OnError
    public void onError(Throwable t , @PathParam(IDENTIFIER) String identifier){
        logger.info("发生异常：, identifier = " + identifier);
        logger.error(t.getMessage() , t);
        getWebSocketManager().remove(identifier);
    }

    protected WebSocketManager getWebSocketManager() {
        return SpringContextHolder.getBean(WebSocketManager.WEBSOCKET_MANAGER_NAME , WebSocketManager.class);
    }
}
