package com.langyastudio.edu.common.util;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.langyastudio.edu.common.exception.MyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket 封装
 */
@ServerEndpoint("/wss")
@Component
@Log4j2
public class WebSocketT
{
    //新：使用map对象，便于根据userId来获取对应的WebSocket
    private static final ConcurrentHashMap<String, WebSocketT> WS_LIST     = new ConcurrentHashMap<>();
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static       int                                   onlineCount = 0;
    private static       ApplicationContext                    applicationContext;
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private              Session                               session;
    //接收clientId
    private              String                                clientId    = "";

    // ------------------------------------------------

    /**
     * 群发消息
     *
     * @param message 消息内容
     */
    public static void send(String message)
    {
        //遍历值
        for (WebSocketT webSocket : WS_LIST.values())
        {

            webSocket.sendMessage(message);
        }

        log.info("群发内容:" + message);
    }

    /**
     * 发送消息
     *
     * @param clientId 客户端ID
     * @param message  消息内容
     */
    public static void send(String clientId, String message) throws MyException
    {
        if (clientId == null)
        {
            throw new MyException("clientId不能为空");
        }

        WebSocketT webSocket = WS_LIST.get(clientId);
        if (webSocket == null)
        {
            throw new MyException("客户端已不存在");
        }

        webSocket.sendMessage(message);

        log.info("推送消息到 " + clientId + "，内容:" + message);
    }

    // ------------------------------------------------

    /**
     * 发送消息
     * 实践表明，每次浏览器刷新，session会发生变化。
     *
     * @param session session
     * @param message message
     */
    private static void sendMessage(Session session, String message)
    {
        try
        {

            session.getBasicRemote().sendText(String.format("%s (From Server，Session ID=%s)", message,
                                                            session.getId()));
        }
        catch (IOException e)
        {
            log.error("发送消息出错：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    private static synchronized int getOnlineCount()
    {
        return onlineCount;
    }

    /**
     * 解决WebSocket不能注入的问题
     *
     * @param applicationContext ApplicationContext
     */
    public static void setApplicationContext(ApplicationContext applicationContext)
    {
        WebSocketT.applicationContext = applicationContext;
    }

    private static synchronized void addOnlineCount()
    {
        WebSocketT.onlineCount++;
    }

    private static synchronized void subOnlineCount()
    {
        WebSocketT.onlineCount--;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose()
    {
        if (WS_LIST.get(this.clientId) == null)
        {

            return;
        }

        WS_LIST.remove(this.clientId);//从wsList中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    // ------------------------------------------------

    /**
     * @param session session
     * @param error   error
     */
    @OnError
    public void onError(Session session, Throwable error)
    {
        log.error("发生错误:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session)
    {
        this.session = session;
        addOnlineCount();           //在线数加1
        log.info("有新窗口开始监听:" + clientId + ",当前在线人数为" + getOnlineCount());

        String cid = UUID.randomUUID().toString();
        this.clientId = cid;

        HashMap<String, Object> data = new HashMap<>()
        {{
            put("clientId", cid);
        }};

        WS_LIST.put(clientId, this); //添加到wsList

        //this.sendMessage(R.ws("connection", data));

        log.info("wsList->" + JSON.toJSONString(WS_LIST));
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session)
    {
        log.info("收到来自窗口 " + clientId + " 的信息:" + message);

        sendMessage(session, "收到消息，消息内容：" + message);

        //群发消息
        //send(message);
    }

    /**
     * 实现服务器主动推送
     */
    private void sendMessage(String message)
    {
        try
        {
            this.session.getBasicRemote().sendText(message);
        }
        catch (IOException e)
        {
            log.error("消息发送失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}