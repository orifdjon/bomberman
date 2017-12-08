package bomber.games.gamesession;

import bomber.games.gameobject.Bomb;
import bomber.games.gameobject.Bonus;
import bomber.games.gameobject.Player;
import bomber.games.geometry.Point;
import bomber.games.model.GameObject;

import java.util.ArrayList;
import java.util.Map;

public class MechanicsSubroutines {

    public boolean collisionCheck(Player currentPlayer,Replica replica) {
        //Распаковываем реплику для работы
        Map<Integer, Player> players = replica.getPlayersOnMap();
        Map<Integer, GameObject> bricks = replica.getBricksOnMap();
        Map<Integer, Bomb> bombs = replica.getBombsOnMap();
        ArrayList<Boolean> check = new ArrayList<>();

        //Узнаем всё о переданном нам новом экземпляре игрока
        int playerId = currentPlayer.getId();
        Point playerPosition = currentPlayer.getPosition();

        //Повторюсь, надо проверить новые координаты игроков на коллизии с другими объектами
        players.forEach((key,player)-> {
                    if ((!players.get(key).getPosition().isColliding(playerPosition))|(key == playerId) ) { //Столкновение с игроками
                        //Если непонятно, нафига здесь второе условие - спроси меня, я поясню, печатать долго
                        check.add(true); //Проверка по игрокам прошла успешно
                    } else {
                        check.add(false);
                    }
                });
        bricks.forEach((key,brick)-> {
            if (!bricks.get(key).getPosition().isColliding(playerPosition)) { //Столкновение с коробками
                check.add(true); //Проверка по коробкам прошла успешно
            } else {
                check.add(false);
            }
        });
        bombs.forEach((key,bomb)-> {
            if (!bombs.get(key).getPosition().isColliding(playerPosition)) { //Столкновение с бомбой
                check.add(true); //Проверка по игрокам прошла успешно
            } else {
                check.add(false);
            }
        });
        //на бонусы проверять не надо, так как это нематериальный объект и с ним своя история

        return !check.contains(false); //если хотя бы одна проверка провалилась, то проверку на коллизии сущность не прошла
    }

    public Integer bonusCheck(Player currentPlayer, Replica replica) {

        Map<Integer, Bonus> bonuses = replica.getBonusOnMap();
        int playerId = currentPlayer.getId();
        Point playerPosition = currentPlayer.getPosition();
        ArrayList<Boolean> check = new ArrayList<>();

        bonuses.forEach((key,bonus)-> {
            if (bonuses.get(key).getPosition().isColliding(playerPosition)) { //Столкновение с бонусом
                check.add(true); //есть столкновение
            } else {
                check.add(false);
            }
        });
        return check.indexOf(true); //возвращаем id бонуса, с которым столкнулся игрок
        //Написан по такому же принципу, что и проверка коллизий, поэтому сильно комментить я его не стал
    }

    public void bombTickAndExplode(Replica replica) {
        //Слооожнаа



    }

    public boolean youDied(Replica replica) {
        return false;
    }
}
