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
    private GameMechanics gameMechanics = new GameMechanics();
    private boolean gameover = false;

    public ConcurrentLinkedQueue<PlayerAction> getInputQueue() {
        return inputQueue;
    }

    public GameSession(int id, boolean gameover) {
        gameMechanics.setupGame(replica, idGenerator);
        this.id = id;
        this.gameover = gameover;
    }

    public Integer getInc() {
        return idGenerator.getAndIncrement();
    }

    public Integer getId() {
        return id;
    }

    public AtomicInteger getIdGenerator() {
        return idGenerator;
    }

    public HashMap<Integer, GameObject> getReplica() {
        return new HashMap<>(replica);
    }

    public void addGameObject(GameObject gameObject) {
        replica.put(idGenerator.getAndIncrement(), gameObject);
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

    public boolean isGameover() {
        return gameover;
    }

    public void setGameover(boolean gameover) {
        this.gameover = gameover;
    }
}