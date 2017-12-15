package bomber.connectionhandler;

import bomber.connectionhandler.json.Json;
import bomber.games.util.HashMapUtil;
import bomber.gameservice.controller.GameController;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import static java.lang.Thread.sleep;


@Component
public class EventHandler extends TextWebSocketHandler implements WebSocketHandler {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EventHandler.class);
    private static final Map<WebSocketSession, Player> connectionPool = new HashMap<>();
    public static final String GAMEID_ARG = "gameId";
    public static final String NAME_ARG = "name";
    private static final List<WebSocketSession> list = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        //connected player count?
        list.add(session);
        connectionPool.put(session, uriToPlayer(session.getUri()));
        connectionPool.get(session).setId(session.hashCode());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(message.getPayload());
        log.info("=============================================================================");
        GameController.getGameSession(connectionPool.get(session).getGameid()).getInputQueue()
                .offer(Json.jsonToPlayerAction(connectionPool.get(session).getId(),message.getPayload()));

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        //connected player count?
        System.out.println("here");
        System.out.println(session.hashCode());
        connectionPool.remove(session);

        super.afterConnectionClosed(session, closeStatus);
    }

    public static void sendReplica(final int gameId) throws IOException {
        /*for (WebSocketSession session : HashMapUtil.getSessionsArrayByGameId(connectionPool, gameId))
            session.sendMessage(
                    new TextMessage(Json.replicaToJson(GameController.getGameSession(gameId).getReplica())));*/
        for (WebSocketSession session : list) {
            session.sendMessage(new TextMessage(Json.replicaToJson(GameController.getGameSession(gameId).getReplica())));
        }
    }

    public static void sendPossess(final int playerId) throws IOException {
        HashMapUtil.getSessionByPlayerId(connectionPool, playerId).sendMessage(
                new TextMessage(Json.possesToJson(playerId)));
    }

    public static Player uriToPlayer(final URI uri) {
        Player player = new Player(); //is id needed?
        for (String iter : uri.getQuery().split("&")) {
            if (iter.contains(GAMEID_ARG) && !(iter.indexOf("=") == iter.length() - 1)) {
                player.setGameid(Integer.parseInt(iter.substring(iter.indexOf("=") + 1, iter.length())));
            }
            if (iter.contains(NAME_ARG) && !(iter.indexOf("=") == iter.length() - 1)) {
                player.setName(iter.substring(iter.indexOf("=") + 1, iter.length()));
            }
        }
        return player;
    }

    public static List<Integer> getSessionIdList() {
        List<Integer> list = new ArrayList<>();
        for (WebSocketSession webSocketSession : connectionPool.keySet()) {
            list.add(webSocketSession.hashCode());
        }
        return list;
    }

    public static Map<WebSocketSession, Player> getConnectionPool() {
        return connectionPool;
    }
}