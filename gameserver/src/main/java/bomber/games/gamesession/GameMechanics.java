package bomber.games.gamesession;

import bomber.games.gameobject.*;
import bomber.games.geometry.Point;
import bomber.games.model.GameObject;
import bomber.games.model.Movable;
import bomber.games.tick.Ticker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameMechanics {

    //InputQueue inputQueue = new InputQueue;
    Replicator replicator = new Replicator();
    ConcurrentLinkedQueue<PlayerAction> eventQueue = new ConcurrentLinkedQueue();

    private Map<Integer, Player> playersOnMap = new HashMap<>();
    private Map<Integer, GameObject> bricksOnMap = new HashMap<>();
    private Map<Integer, Bomb> bombsOnMap = new HashMap<>();
    private Map<Integer, Bonus> bonusOnMap = new HashMap<>();
    private Map<Integer, PlayerAction> actionOnMap = new HashMap<>();


    //В оригинальной версии поле 16*16
    final int gameZone = 15;//0,16 - стенки
    public int playersCount = 4;//Число игроков
    final int brickSize = 0;//в будущем, когда будет накладываться на это дело фронтенд, это пригодится
    final int bonusCount = 4;//3*Количество бонусов, которые отспаунятся

    public Replica setupGame() {


        //заполним objectsOnMap
        bricksOnMap.put(0, new PlayGround(new Point(0, 0)));//для механики бесполезно, а фронтенду необходимо

                    /*
                    Площадкамана построили, насяльника, можно заселять игроков
                    */
        playersOnMap.put(1,new Player(new Point(1, 1)));//Первый игрок, id = 1
        playersOnMap.put(2,new Player(new Point(gameZone, 1)));//Второй игрок, id = 2
        playersOnMap.put(3,new Player(new Point(1, gameZone)));//Третий игрок, id = 3
        playersOnMap.put(4,new Player(new Point(gameZone, gameZone)));//Четвертый игрок, id = 4
        //надо обсудить что тут делать, если игроков придет меньше


        //Заполним Box и Wall
        for (int j = 0; j <= gameZone; j = j + brickSize) {
            for (int i = 0; i <= gameZone; i = i + brickSize) {
                    /*
                    Представим нашу игровую площадку как двумерный массив. Прогуляемся по нему,
                    попутно расставляя объекты по принципу:
                    четная i и четная j заполняется Wall, остальное Box
                    */
                if ((i % 2 == 0) && (j % 2 == 0)) {
                    bricksOnMap.put(i*j,new Wall(new Point(i, j)));
                } else {
                    bricksOnMap.put(i*j,new Box(new Point(i, j)));
                }
            }
        }

                    /*
                    Пространство вокруг игроков надо освободить, поэтому
                    */

        //Для первого игрока(Вверх-Влево)
        bricksOnMap.remove(1);
        bricksOnMap.remove(2);
        bricksOnMap.remove(gameZone + 1);
        //Для второго игрока(Вверх-Вправо)
        bricksOnMap.remove(gameZone);
        bricksOnMap.remove(gameZone - 1);
        bricksOnMap.remove(2 * gameZone);
        //Для третьего игрока(Вниз-Влево)
        bricksOnMap.remove(gameZone * (gameZone - 1));
        bricksOnMap.remove(gameZone * (gameZone - 1) + 1);
        bricksOnMap.remove(gameZone * (gameZone - 1) - gameZone);
        //Для четвертого игрока(Вниз-Влево)
        bricksOnMap.remove(gameZone * gameZone);
        bricksOnMap.remove(gameZone * gameZone - 1);
        bricksOnMap.remove(gameZone * gameZone - gameZone);


                    /*
                    Теперь (в итоге) бонусы, отдельный лист, так как объект не материален
                    */

        Random rand = new Random();//Рандомная координата выпадающего бонуса (но в пределах gameZone)
        for (int i = 0; i <= bonusCount; i++) {

            bonusOnMap.put( i, new Bonus(new Point(rand.nextInt(gameZone - 1) + 1,
                    rand.nextInt(gameZone - 1) + 1), Bonus.Type.SPEED, false, true));
        }

        for (int i = 0; i <= bonusCount; i++) {
            bonusOnMap.put( i, new Bonus(new Point((rand.nextInt(gameZone - 1) + 1),
                    (rand.nextInt(gameZone - 1) + 1)), Bonus.Type.BOMB, false, true));
        }

        for (int i = 0; i <= bonusCount; i++) {
            bonusOnMap.put( i, new Bonus(new Point((rand.nextInt(gameZone - 1) + 1),
                    (rand.nextInt(gameZone - 1) + 1)), Bonus.Type.RANGE, false, true));
        }
        return new Replica( playersOnMap, bricksOnMap, bombsOnMap, bonusOnMap);
    }

    public void readInputQueue() {

        while (!eventQueue.isEmpty()) { //делать до тех пор пока очередь не опустеет
            Integer playerId = eventQueue.element().getId(); //заранее узнаем id игрока, возглавляющего очередь
            if (!actionOnMap.containsKey(playerId)) { //если действий от этого игрока еще не было
                actionOnMap.put(playerId, eventQueue.element());//Запишем действие в мапу
            }
            eventQueue.remove();//удаляем главу этой очереди
        }
    }

    public void clearInputQueue() {
        eventQueue.clear();
    }

    public Replica doMechanic(Replica replica, Ticker tick) {


        for (int i = 0; i <= playersCount; i++) {
            Player currentPlayer = playersOnMap.get(i);//выцепили нужного игрока из списка
            MechanicsSubroutines mechanicsSubroutines = new MechanicsSubroutines();//подняли вспомогательные методы

            /*
            Сначала разберемся с перемещениями
            Переходим от Action к Player_position or Bomb_position
            */

            switch (actionOnMap.get(i).getType()) { //либо шагает Up,Down,Right,Left, либо ставит бомбу Bomb

                case Up: //если идет вверх
                    currentPlayer.setPosition(currentPlayer.move(Movable.Direction.UP, tick));//задали новые координаты

                    if (mechanicsSubroutines.collisionCheck(currentPlayer, replica)) {//Если никуда не врезается, то
                        playersOnMap.replace(i, currentPlayer);//перемещаем игрока
                    }
                    //Если проверку не прошла, то все остается по старому
                    break;

                case Down:
                    currentPlayer.setPosition(currentPlayer.move(Movable.Direction.DOWN, tick));

                    if (mechanicsSubroutines.collisionCheck(currentPlayer, replica)) {
                        playersOnMap.replace(i, currentPlayer);
                    }

                    break;
                case Left:
                    currentPlayer.setPosition(currentPlayer.move(Movable.Direction.LEFT, tick));

                    if (mechanicsSubroutines.collisionCheck(currentPlayer, replica)) {
                        playersOnMap.replace(i, currentPlayer);
                    }

                    break;
                case Right:
                    currentPlayer.setPosition(currentPlayer.move(Movable.Direction.RIGHT, tick));

                    if (mechanicsSubroutines.collisionCheck(currentPlayer, replica)) {
                        playersOnMap.replace(i, currentPlayer);
                    }

                    break;
                case Bomb:
                    //Тут особый случай

                default:
                    break;
            }


            if (!(mechanicsSubroutines.bonusCheck(currentPlayer, replica) == null)) { //если был взят бонус

            }


        }


        //Вдруг взяли бонус?

        //Надо протикать бомбу или взорвать ее если время вышло

        //Если взрыв прошел в этот тик, уничтожить коробки в эпцентре взрыва

        //Вдруг у кого то game over?


        playersOnMap.clear();
        bombOnMap.clear();
        return mechanic;
    }

    public Replicator writeRepica() {
        /*
        Здесь как мне кажется должна быть мапа GameObject, с описанием состояния каждого элемента
        */
        return null;
    }
}
