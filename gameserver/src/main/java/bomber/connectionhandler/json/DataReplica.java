package bomber.connectionhandler.json;

import bomber.games.model.GameObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataReplica {
    private List<? extends GameObject> objects;
    private boolean gameOver = false;
/*    private Map<? extends GameObject, ? extends GameObject> exampleEEE = new HashMap<>();*/

    public DataReplica() {
    }

    public List<? extends GameObject> getObjects() {
        return objects;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setObjects(List<? extends GameObject> objects) {
        this.objects = objects;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

   /* public Map<? extends GameObject, ? extends GameObject> getExampleEEE() {
        return exampleEEE;
    }

    public void setExampleEEE(Map<? extends GameObject, ? extends GameObject> exampleEEE) {
        this.exampleEEE = exampleEEE;
    }*/
}
