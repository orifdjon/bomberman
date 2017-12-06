package bomber.gameservice.websocket;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

public class WebSocketClient {
    private static final String name = "c";

    public static void main(String[] args) {
        // connection url
        String uri = "ws://localhost:8090/game/connect?gameId=" + 42 + "&name=" + name;

        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = null;
        try {
            EventHandler socket = new EventHandler();
            ListenableFuture<WebSocketSession> fut = client.doHandshake(socket, uri);
            session = fut.get();

        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
