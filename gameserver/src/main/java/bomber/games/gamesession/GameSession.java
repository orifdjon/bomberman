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
    private Map<Integer, GameObject> gameObjects = new HashMap<>();
    private final long Id;
    private boolean gameOver;
    private final AtomicInteger idGenerator = new AtomicInteger(1); // IdGenerator for each session


    public GameSession(final long id) {
        this.Id = id;
        gameOver = false;
    }

    public long getId() {
        return Id;
    }


    public HashMap<Integer, GameObject> getGameObjects() {
        return new HashMap<>(gameObjects);
    }

    public void addGameObject(final GameObject gameObject) {
        gameObjects.put(idGenerator.getAndIncrement(), gameObject);
    }

    @Override
    public void tick(final long elapsed) {
        log.info("tick");
        for (GameObject gameObject : gameObjects.values()) {
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
