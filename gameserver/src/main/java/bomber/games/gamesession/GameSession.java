package bomber.games.gamesession;


import bomber.games.model.GameObject;
import bomber.games.model.Tickable;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GameSession implements Tickable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GameSession.class);
    private Map<Integer, GameObject> replica = new HashMap<>();
    private boolean gameOver = false;
    private final long Id;
    private final AtomicInteger idGenerator = new AtomicInteger(1); // IdGenerator for each sessiond

    public GameSession(final long Id) {
        this.Id = Id;
    }

    public long getId() {
        return Id;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public HashMap<Integer, GameObject> getGameObjects() {
        return new HashMap<>(replica);
    }

    public void addGameObject(final GameObject gameObject) {
        replica.put(idGenerator.getAndIncrement(), gameObject);
    }

    @Override
    public void tick(final long elapsed) {
        log.info("tick");
        for (GameObject gameObject : replica.values()) {
            if (gameObject instanceof Tickable) {
                ((Tickable) gameObject).tick(elapsed);
            }
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj){
            return true;
        } else {
            if (obj instanceof GameSession) {
               GameSession gameSession = (GameSession) obj;
               return this.Id == gameSession.Id;
            }
            return false;
        }
    }


}
