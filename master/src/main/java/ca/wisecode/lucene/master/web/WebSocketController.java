package ca.wisecode.lucene.master.web;

import ca.wisecode.lucene.common.exception.BusinessException;
import ca.wisecode.lucene.common.grpc.node.NodeChannel;
import ca.wisecode.lucene.common.grpc.node.NodeState;
import ca.wisecode.lucene.master.grpc.node.MasterNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/23/2024 11:58 PM
 * @Version: 1.0
 * @description:
 */
@Component
@ServerEndpoint("/websocket/slaveState")
@Slf4j
@CrossOrigin(origins = "*")
public class WebSocketController {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MasterNode masterNode;

    private String preMessage = "";

    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    //    @EventListener
//    public void handleNodeStatusChange(SlaveStatusChangeEvent event) {
//        try {
//            if (!masterNode.getChannels().isEmpty()) {
//                List<Map<String, Object>> list = new ArrayList<>();
//                for (NodeChannel nodeChannel : masterNode.getChannels()) {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("targetHost", nodeChannel.getTargetHost());
//                    map.put("targetPort", nodeChannel.getTargetPort());
//                    map.put("sourceHost", nodeChannel.getSourceHost());
//                    map.put("sourcePort", nodeChannel.getSourcePort());
//                    map.put("state", nodeChannel.getState());
//                    map.put("docsTotal", nodeChannel.getDocsTotal());
//                    list.add(map);
//                }
//                String message = objectMapper.writeValueAsString(list);
//                log.info(message);
//                this.sendMessageToAll(message);
//            }
//        } catch (JsonProcessingException e) {
//            throw new BusinessException(e);
//        }
//
//    }
    @Scheduled(initialDelay = 30000, fixedRate = 1000)
    public void scanMaster() {
       if(!sessions.isEmpty()){
           try {
            if (!masterNode.getChannels().isEmpty()) {
                List<Map<String, Object>> list = new ArrayList<>();
                for (NodeChannel nodeChannel : masterNode.getChannels()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("targetHost", nodeChannel.getTargetHost());
                    map.put("targetPort", nodeChannel.getTargetPort());
                    map.put("sourceHost", nodeChannel.getSourceHost());
                    map.put("sourcePort", nodeChannel.getSourcePort());
                    map.put("state", nodeChannel.getState());
                    map.put("docsTotal", nodeChannel.getDocsTotal());
                    list.add(map);
                }
                String currMessage = objectMapper.writeValueAsString(list);
                if(!currMessage.equals(preMessage)){
                    this.sendMessageToAll(currMessage);
                    preMessage = currMessage;
                }
            }
        } catch (JsonProcessingException e) {
            throw new BusinessException(e);
        }
       }
    }

    // Called when a new connection is established
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        log.info("New connection established, session id: " + session.getId());
    }

    /**
     * 当收到客户端发送过来的消息时触发
     *
     * @param message 客户端发送过来的消息
     * @param session 客户端的session会话
     */
    // Called when a message is received
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("Received message from session id: " + session.getId() + " - " + message);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        log.info("Connection closed, session id: " + session.getId());
    }

    // Send a message to all connected clients
    private void sendMessageToAll(String message) {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
