package top.jfunc.websocket;

import javax.websocket.Session;
import java.io.Serializable;

/**
 * @author xiongshiyan at 2018/10/10 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class WebSocket implements Serializable{
    public static final int STATUS_AVAILABLE       = 0;
    public static final int STATUS_UNAVAILABLE     = 1;
    private String identifier;
    private Session session;
    private int status;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
