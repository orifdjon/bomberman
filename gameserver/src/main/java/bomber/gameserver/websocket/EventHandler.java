package bomber.gameserver.websocket;

import bomber.gameserver.controller.GameController;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@Component
public class EventHandler extends TextWebSocketHandler {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EventHandler.class);
    public static final int CONNECTION_TIMEOUT = 300;
    List<WebSocketSession> peers = new ArrayList<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        log.info("WebSocket connection established - " + session);
        GameController.setConnectedPlayerCount(GameController.getConnectedPlayerCount() + 1);
        log.info("Prolonging WS connection for {} SEC for player {}",
                CONNECTION_TIMEOUT, GameController.getConnectedPlayerCount());
        sleep(TimeUnit.SECONDS.toMillis(CONNECTION_TIMEOUT));
        log.info("Closing connection for player #" + "asd");
        peers.add(session);
        //session.close(); попробовать тест когда подключения не закрываются
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        GameController.setConnectedPlayerCount(GameController.getConnectedPlayerCount() - 1);
        log.info("Socket Closed: [" + closeStatus.getCode() + "] " + closeStatus.getReason());
        super.afterConnectionClosed(session, closeStatus);
        peers.remove(session);
    }

    public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {

        for(WebSocketSession webSocketSession : peers) {

            webSocketSession.sendMessage(message);
        }
    }


}