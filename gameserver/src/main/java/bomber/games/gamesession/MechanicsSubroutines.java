package bomber.games.gamesession;

import bomber.games.gameobject.*;
import bomber.games.geometry.Point;
import bomber.games.model.GameObject;

import java.util.Map;

public class MechanicsSubroutines {

    public boolean collisionCheck(GameObject currentPlayer, Map<Integer, GameObject> replica) {


        //Узнаем всё о переданном нам новом экземпляре игрока
        int playerId = currentPlayer.getId();
        Point playerPosition = currentPlayer.getPosition();

        //Повторюсь, надо проверить новые координаты игроков на коллизии с другими объектами
        for (GameObject gameObject : replica.values()) {
            if (!(gameObject.getPosition() == playerPosition) | (gameObject.getId() == playerId)) {
                return true;
            }
        }
        return false;
    }

    public Integer bonusCheck(Player currentPlayer, Map<Integer, GameObject> replica) {


        int playerId = currentPlayer.getId();
        Point playerPosition = currentPlayer.getPosition();

        for (GameObject gameObject : replica.values()) {
            if (gameObject instanceof Bomb) {
                if (gameObject.getPosition() == playerPosition) {
                    return gameObject.getId();
                }
            }
        }
        return null;
    }

    public Boolean createExplosions(Point currentPoint, GameObject gameObject, Map<Integer, GameObject> replica) {

        if (gameObject.getPosition().isColliding(currentPoint)) { //если на пути взрыва встал НЛО
            if (gameObject instanceof Box) { //и это НЛО - коробка
                replica.remove(gameObject.getId()); //удаляем взорвавшуюся коробку
            }
            return false;//все, один объект взорвался, дальше не надо работать по этому кейсу
        }
        return true;
    }


    public void youDied(Map<Integer, GameObject> replica, Player player, Explosion explosion) {
        if (player.getPosition().isColliding(explosion.getPosition())) {
            replica.remove(player.getId());
        }
    }
}