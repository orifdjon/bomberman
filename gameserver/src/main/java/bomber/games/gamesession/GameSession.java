package bomber.games.gamesession;


import bomber.games.model.GameObject;
import bomber.games.model.Tickable;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GameSession implements Tickable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GameSession.class);
    private List<GameObject> gameObjects = new ArrayList<>();
    private static int id = 0;


    public List<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }

    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    @Override
    public void tick(long elapsed) {
        log.info("tick");
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Tickable) {
                ((Tickable) gameObject).tick(elapsed);
            }
        }
    }

    public static int nextId() {
        return ++id;
    }
}
