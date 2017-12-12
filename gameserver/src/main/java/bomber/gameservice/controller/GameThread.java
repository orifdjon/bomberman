package bomber.gameservice.controller;

import bomber.games.gamesession.GameMechanics;
import bomber.games.gamesession.GameSession;
import bomber.games.tick.Ticker;
import org.slf4j.LoggerFactory;

import static bomber.gameservice.controller.GameController.gameSessionMap;

public class GameThread implements Runnable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GameThread.class);
    private final long gameId;
    private Thread gameThread;
    private GameMechanics gameMechanics = new GameMechanics();
    Ticker ticker = new Ticker();


    public GameThread(final long gameId) {
        this.gameId = gameId;
    }


    @Override

    public void run() {
        
        log.info("Start new thread called game-mechanics with gameId = " + gameId);
        GameSession gameSession = new GameSession((int) gameId, false);
        log.info("Game has been init gameId={}", gameId);
        gameSessionMap.put(gameId, gameSession);
        gameMechanics.setupGame(gameSession.getReplica(),gameSession.getIdGenerator());
        while (!gameSession.isGameover()) {

            ticker.gameLoop();
            long elapsed = ticker.getElapsed();
            gameMechanics.readInputQueue(gameSession.getInputQueue());
            gameMechanics.doMechanic(gameSession,gameSession.getReplica(),elapsed);
        }
    }
}