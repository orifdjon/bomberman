package bomber.gameservice.controller;

import bomber.games.gamesession.GameSession;
import org.slf4j.LoggerFactory;

import static bomber.gameservice.controller.GameController.gameSessionMap;

public class GameThread implements Runnable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GameThread.class);
    private final long gameId;

    public GameThread(final long gameId) {
        this.gameId = gameId;
    }

    @Override
    public void run() {
        log.info("Start new thread called game-mechanics with gameId = " + gameId);
        GameSession gameSession = new GameSession(gameId);
        log.info("Game has been init gameId={}", gameId);

        GameSession oldGameSession = gameSessionMap.get(gameId);
        gameSessionMap.replace(gameId, oldGameSession, gameSession); // засунули gameSession с заданным gameId в gameSessionMap

        // Дальше надо добавить игровые сущности ....
    }
}
