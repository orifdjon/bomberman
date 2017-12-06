package bomber.gameservice;

import bomber.games.gamesession.GameMechanics;
import bomber.games.model.GameObject;
import org.junit.Assert;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

public class StartGameTest {


    @Test
    public void objectsCount() {
        List<GameObject> gameObjects = new ArrayList<>();
        GameMechanics gameMechanics = new GameMechanics();
        gameObjects = gameMechanics.setupGame();
        Assert.assertNotNull(gameObjects);
        Assert.assertTrue(gameObjects.size() == gameMechanics.objectCounter());

    }

}