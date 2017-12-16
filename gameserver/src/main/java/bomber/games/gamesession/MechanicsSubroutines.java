package bomber.games.gamesession;

import bomber.games.gameobject.*;
import bomber.games.geometry.Bar;
import bomber.games.geometry.Point;
import bomber.games.model.GameObject;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MechanicsSubroutines {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MechanicsSubroutines.class);

    public boolean collisionCheck(GameObject currentPlayer, Map<Integer, GameObject> replica) {
        final int brickSize = 31;
        final int playerSize = 28;
        int player_X = currentPlayer.getPosition().getX();
        int player_Y = currentPlayer.getPosition().getY();

        Bar playerBar = new Bar(player_X, player_X + playerSize, player_Y, player_Y - playerSize);
        int playerId = currentPlayer.getId();


        //Повторюсь, надо проверить новые координаты игроков на коллизии с другими объектами
        for (GameObject gameObject : replica.values()) {
            int brick_X = gameObject.getPosition().getX();
            int brick_Y = gameObject.getPosition().getY();
            Bar brickBar = new Bar(brick_X, brick_X + brickSize, brick_Y, brick_Y - brickSize);
            if ((!(gameObject instanceof Bonus)) && (!(gameObject instanceof Bomb))
                    && (!(gameObject instanceof Player) && (!(gameObject instanceof Explosion)))) {
                if ((brickBar.isColliding(playerBar)) && !(gameObject.getId() == playerId)) {
                    log.info("isColliding");
                    return false;
                }
            }

            if (gameObject instanceof Bomb) {
                if (!brickBar.isColliding(playerBar)) {
                    ((Bomb) gameObject).setNewBombStillCollide(false);
                }
                if ((!((Bomb) gameObject).isNewBombStillCollide()) && (brickBar.isColliding(playerBar))) {
                    return false;
                }
            }
        }


        return true;
    }

    public Integer bonusCheck(Player currentPlayer, Map<Integer, GameObject> replica) {


        int playerId = currentPlayer.getId();
        Point playerPosition = currentPlayer.getPosition();

        final int bonusSize = 31;
        final int playerSize = 25;
        int player_X = currentPlayer.getPosition().getX();
        int player_Y = currentPlayer.getPosition().getY();

        Bar playerBar = new Bar(player_X, player_X + playerSize, player_Y, player_Y - playerSize);


        for (GameObject gameObject : replica.values()) {
            int brick_X = gameObject.getPosition().getX();
            int brick_Y = gameObject.getPosition().getY();
            Bar brickBar = new Bar(brick_X, brick_X + bonusSize, brick_Y, brick_Y - bonusSize);

            if (gameObject instanceof Bonus) {
                if (brickBar.isColliding(playerBar)) {
                    return gameObject.getId();
                }
            }
        }
        return null;
    }

    public Boolean createExplosions(Point currentPoint, Map<Integer, GameObject> replica) {


        final int brickSize = 31;
        final int fireSize = 28;
        int fire_X = currentPoint.getX();
        int fire_Y = currentPoint.getY();

        Bar fireBar = new Bar(fire_X, fire_X + fireSize, fire_Y, fire_Y - fireSize);

        for (GameObject gameObject : replica.values()) {

            int brick_X = gameObject.getPosition().getX();
            int brick_Y = gameObject.getPosition().getY();
            Bar brickBar = new Bar(brick_X, brick_X + brickSize, brick_Y, brick_Y - brickSize);

            if (brickBar.isColliding(fireBar)) { //если на пути взрыва встал НЛО
                if (gameObject instanceof Box) { //и это НЛО - коробка
                    replica.remove(gameObject.getId()); //удаляем взорвавшуюся коробку
                }
                return false;//все, один объект взорвался, дальше не надо работать по этому кейсу
            }

        }

        return true;
    }


    public boolean youDied(Map<Integer, GameObject> replica, Explosion explosion) {
        final int brickSize = 31;
        final int fireSize = 28;
        int fire_X = explosion.getPosition().getX();
        int fire_Y = explosion.getPosition().getY();

        Bar fireBar = new Bar(fire_X, fire_X + fireSize, fire_Y, fire_Y - fireSize);

        for (GameObject gameObject : replica.values()) {
            if ( gameObject instanceof Player) {
                int brick_X = gameObject.getPosition().getX();
                int brick_Y = gameObject.getPosition().getY();
                Bar brickBar = new Bar(brick_X, brick_X + brickSize, brick_Y, brick_Y - brickSize);

                if (brickBar.isColliding(fireBar)) {
                    replica.remove(gameObject.getId()); //удаляем взорвавшуюся player'а
                    return false;//все, один объект взорвался, дальше не надо работать по этому кейсу
                }
            }

        }

        return true;
    }
}