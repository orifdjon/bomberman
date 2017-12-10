package bomber.connectionhandler;

import bomber.connectionhandler.json.*;
import bomber.games.model.GameObject;
import bomber.games.util.JsonHelper;
import bomber.gameservice.controller.GameController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@Component
public class EventHandler extends TextWebSocketHandler implements WebSocketHandler {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EventHandler.class);



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        log.info("WebSocket connection established - " + session);
        GameController.setConnectedPlayerCount(GameController.getConnectedPlayerCount() + 1);
        log.info("Prolonging WS connection for 60 SEC for player #" + GameController.getConnectedPlayerCount());
        sleep(TimeUnit.SECONDS.toMillis(300));
        log.info("Closing connection for player #" + "asd");
        session.close();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        GameController.setConnectedPlayerCount(GameController.getConnectedPlayerCount() - 1);
        log.info("Socket Closed: [" + closeStatus.getCode() + "] " + closeStatus.getReason());
        super.afterConnectionClosed(session, closeStatus);
    }




    public static PlayerAction handleMoveAndPlanBombFromJson(@NotNull String json) { // из json делает объект
        HandleInputJson handleInputJson = JsonHelper.fromJson(json, HandleInputJson.class);
        PlayerAction playerAction = convertToPlayerAction(handleInputJson);
        if (playerAction == null) {
            throw new NullPointerException("Мы не смогли конверитировать json в playerAction");
        } else {
            return playerAction;
        }

    }

    @NotNull
    public static String handlePossessToJson(@NotNull final Integer data) { // возврщает json
        Possess possess = new Possess();
        possess.setData(data);
        return JsonHelper.toJson(possess);
    }


    @NotNull
    public static String handleReplica(@NotNull final JsonReplica replica, @NotNull final List<? extends GameObject> list) {
        DataReplica dataReplica = replica.getData();
        dataReplica.setObjects(list);
        return JsonHelper.toJson(replica);
    }

    @Nullable
    private static PlayerAction convertToPlayerAction(@NotNull HandleInputJson handleInputJson) {
        PlayerAction playerAction = new PlayerAction();
        if (handleInputJson.getTopic() == Topic.MOVE) {
            playerAction.setType(handleInputJson.getData().getEventType());
            return playerAction;
        }
        if (handleInputJson.getTopic() == Topic.PLANT_BOMB) {
            playerAction.setType(PlayerAction.EventType.BOMB);
            return playerAction;
        }
        return null;
    }
   /* public static String handleReplica(@NotNull Replica replica, @NotNull List<? extends GameObject> list,
                                       Map<? extends GameObject, ? extends GameObject> map) {
        DataReplica dataReplica = replica.getData();
        dataReplica.setObjects(list);
        dataReplica.setExampleEEE(map);
        String json = JsonHelper.toJson(replica);
        return json;
    }*/




}