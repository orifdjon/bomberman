package bomber.games.gamesession;


import bomber.connectionhandler.PlayerAction;
import bomber.games.model.GameObject;
import bomber.games.model.Tickable;
import bomber.games.util.GeneratorIdSession;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GameSession implements Tickable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GameSession.class);
    private Map<Integer, GameObject> replica = new HashMap<>();
    private final int id;
    private final AtomicInteger idGenerator = new AtomicInteger(0); // У каждой сессии свой набор id
    private ConcurrentLinkedQueue<PlayerAction> inputQueue = new ConcurrentLinkedQueue<>();
    public static final int DEFAULT_SETTING = 0;
    private GameMechanics gameMechanics = new GameMechanics(DEFAULT_SETTING);
    private boolean gameOver = false;

    public ConcurrentLinkedQueue<PlayerAction> getInputQueue() {
        return inputQueue;
    }

    public GameSession(int id) {
        this.id = id;
    }

    public void setupGameMap() {
        gameMechanics.setupGame(replica, idGenerator);
    }

    public Integer getInc() {
        return idGenerator.getAndIncrement();
    }

    public Integer getId() {
        return id;
    }

    public long getIdGenerator() {
        return idGenerator.get();
    }

    public HashMap<Integer, GameObject> getReplica() {
        return new HashMap<>(replica);
    }

    public void addGameObject(GameObject gameObject) {
        replica.put(idGenerator.getAndIncrement(), gameObject);
    }

    public GameMechanics getGameMechanics() {
        return gameMechanics;
    }

    @Override
    public void tick(long elapsed) {
        log.info("tick");
        for (GameObject gameObject : replica.values()) {
            if (gameObject instanceof Tickable) {
                ((Tickable) gameObject).tick(elapsed);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        } else {
            if (obj instanceof GameSession) {
                GameSession gameSession = (GameSession) obj;
                return this.id == gameSession.id;
            }
            return false;
        }
    }


    public boolean isGameOver() {
        return gameOver;
    }
}