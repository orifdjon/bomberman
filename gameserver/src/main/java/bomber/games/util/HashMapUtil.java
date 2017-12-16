//package bomber.games.util;
//
//import bomber.connectionhandler.Player;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//public class HashMapUtil {
//    public static ArrayList<WebSocketSession> getSessionsArrayByGameId(Map<WebSocketSession, Player> map, final int gameId) {
//        ArrayList<WebSocketSession> arrayList = new ArrayList<>();
//        for (Map.Entry<WebSocketSession, Player> entry : map.entrySet()) {
//            if (gameId == entry.getValue().getGameid()) {
//                arrayList.add(entry.getKey());
//            }
//        }
//        return arrayList;
//    }
//    public static WebSocketSession getSessionByPlayerId(Map<WebSocketSession, Player> map, final int playerId) {
//        for (Map.Entry<WebSocketSession, Player> entry : map.entrySet()) {
//            if (playerId == entry.getValue().getId()) {
//                return entry.getKey();
//            }
//        }
//        return null;
//    }
//}
