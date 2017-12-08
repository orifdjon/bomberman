package bomber.games.gamesession;

import bomber.games.gameobject.Bomb;
import bomber.games.gameobject.Bonus;
import bomber.games.gameobject.Player;
import bomber.games.model.GameObject;

import java.util.HashMap;
import java.util.Map;

public class Replica {
    private Map<Integer,Player> playersOnMap = new HashMap<>();
    private Map<Integer,GameObject> bricksOnMap = new HashMap<>();
    private Map<Integer,Bomb> bombsOnMap = new HashMap<>();
    private Map<Integer,Bonus> bonusOnMap = new HashMap<>();

    public Replica(Map playersOnMap,Map bricksOnMap, Map bombsOnMap, Map bonusOnMap) {
        this.playersOnMap = playersOnMap;
        this.bricksOnMap = bricksOnMap;
        this.bombsOnMap = bombsOnMap;
        this.bonusOnMap = bonusOnMap;
    }

    public void setPlayersOnMap(Map<Integer, Player> playersOnMap) {
        this.playersOnMap = playersOnMap;
    }

    public void setBricksOnMap(Map<Integer, GameObject> bricksOnMap) {
        this.bricksOnMap = bricksOnMap;
    }

    public void setBombsOnMap(Map<Integer, Bomb> bombsOnMap) {
        this.bombsOnMap = bombsOnMap;
    }

    public void setBonusOnMap(Map<Integer, Bonus> bonusOnMap) {
        this.bonusOnMap = bonusOnMap;
    }


    public Map<Integer, Player> getPlayersOnMap() {
        return playersOnMap;
    }

    public Map<Integer, GameObject> getBricksOnMap() {
        return bricksOnMap;
    }

    public Map<Integer, Bomb> getBombsOnMap() {
        return bombsOnMap;
    }

    public Map<Integer, Bonus> getBonusOnMap() {
        return bonusOnMap;
    }

}

