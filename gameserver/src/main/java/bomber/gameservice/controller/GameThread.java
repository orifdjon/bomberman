package bomber.gameservice.controller;


import bomber.games.gameobject.Bomb;
import bomber.games.gameobject.Explosion;
import bomber.games.gameobject.Player;
import bomber.games.gameobject.Explosion;
import bomber.games.gamesession.GameSession;
import bomber.games.model.Tickable;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static bomber.games.gamesession.GameSession.MAX_PLAYER_IN_GAME;
import static bomber.gameservice.controller.GameController.gameSessionMap;

public class GameThread implements Runnable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GameThread.class);
    private final long gameId;
    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;
    private Set<Tickable> tickables = new ConcurrentSkipListSet<>();
    private long tickNumber = 0;
    private GameSession gameSession;

    public GameThread(final long gameId) {
        this.gameId = gameId;
    }

    
    @Override
    public void run() {
        log.info("Start new thread called game-mechanics with gameId = " + gameId);
        gameSession = new GameSession((int) gameId, tickables);
        log.info("Game has been init gameId={}", gameId);
        gameSession.setupGameMap();
        gameSessionMap.put(gameId, gameSession);
        while (!Thread.currentThread().isInterrupted() || !gameSession.isGameOver()) {

            long started = System.currentTimeMillis();
            act(FRAME_TIME);
            long elapsed = System.currentTimeMillis() - started;
            if (elapsed < FRAME_TIME) {
                /*        log.info("All tick finish at {} ms", elapsed);*/
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(FRAME_TIME - elapsed));
            } else {
                log.warn("tick lag {} ms", elapsed - FRAME_TIME);
            }
            /*  log.info("{}: tick ", tickNumber);*/
            tickNumber++;

        }
    }


    private void act(long elapsed) {
        try {

            EventHandler.sendReplica(gameSession.getId());
        } catch (IOException e) {
            log.error("Error to send REPLICA");
        }
        int gameOverCondition = MAX_PLAYER_IN_GAME;
        for (Tickable tickable : tickables) {
            if (tickable instanceof Player)
                gameOverCondition--;
            tickable.tick(elapsed);

            if (tickable instanceof Bomb || tickable instanceof Explosion) {
                if (!tickable.isAlive()) {
                    if (tickable instanceof Bomb) {
                        Player tmpPlayer = (Player) gameSession.getReplica().get(((Bomb) tickable).getPlayerId());
                        tmpPlayer.decBombCount();
                    }
                    log.info("it IS'NT alive");
                    unregisterTickable(tickable);
                }
            }
        }
        if (!(gameOverCondition == 1)) {
            if (!gameSession.getInputQueue().isEmpty()) {
                gameSession.getGameMechanics().readInputQueue(gameSession.getInputQueue());
                gameSession.getGameMechanics().doMechanic(gameSession.getReplica(), gameSession.getIdGenerator());
                gameSession.getGameMechanics().clearInputQueue(gameSession.getInputQueue());
            }
        } else {
            gameSession.setGameOver(true);
        }

    }

    public long getTickNumber() {
        return tickNumber;
    }

    public void unregisterTickable(Tickable tickable) {
        tickables.remove(tickable);
    }

}