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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.Thread.sleep;


@Component
public class EventHandler extends TextWebSocketHandler implements WebSocketHandler {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EventHandler.class);
    private static final Map<WebSocketSession, Player> connectionPool = new HashMap<>();
    public static final String GAMEID_ARG = "gameId";
    public static final String NAME_ARG = "name";

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        //connected player count?
            connectionPool.put(session, uriToPlayer(session.getUri()));
        connectionPool.get(session).setId(session.hashCode());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        GameController.getGameSession(connectionPool.get(session).getGameid()).getInputQueue()
                .add(Json.jsonToPlayerAction(connectionPool.get(session).getId(),message.getPayload()));

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        //connected player count?
        System.out.println("here");
        System.out.println(session.hashCode());
        connectionPool.remove(session);

        super.afterConnectionClosed(session, closeStatus);
    }

    public void sendReplica(final int gameId) throws IOException {
        for (WebSocketSession session : HashMapUtil.getSessionsArrayByGameId(connectionPool, gameId))
            session.sendMessage(
                    new TextMessage(Json.replicaToJson(GameController.getGameSession(gameId).getReplica())));
    }

    public void sendPossess(final int playerId) throws IOException {
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

    public static Set<Integer> getSessionIdSet() {
        Set<Integer> set = new HashSet<>();
        for (WebSocketSession webSocketSession : connectionPool.keySet()) {
            set.add(webSocketSession.hashCode());
        }
        return set;
    }


}