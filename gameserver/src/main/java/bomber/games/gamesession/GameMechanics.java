package bomber.games.gamesession;

import bomber.games.gameobject.*;
import bomber.games.geometry.Point;
import bomber.games.model.GameObject;
import bomber.games.model.Movable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameMechanics {

    //InputQueue inputQueue = new InputQueue;
    Replicator replicator = new Replicator();
    ConcurrentLinkedQueue<PlayerAction> eventQueue = new ConcurrentLinkedQueue();

    private ArrayList<GameObject> playersOnMap = new ArrayList<>();
    private ArrayList<GameObject> objectsOnMap = new ArrayList<>();
    private ArrayList<GameObject> bombOnMap = new ArrayList<>();
    private ArrayList<GameObject> bonusOnMap = new ArrayList<>();


    //В оригинальной версии поле 16*16
    final int gameZone = 15;//0,16 - стенки
    final int brickSize = 0;//в будущем, когда будет накладываться на это дело фронтенд, это пригодится
    final int bonusCount = 4;//3*Количество бонусов, которые отспаунятся

    public ArrayList<GameObject> setupGame() {


        //заполним objectsOnMap
        objectsOnMap.add(new PlayGround(new Point(0, 0)));//для механики бесполезно, а фронтенду необходимо

                    /*
                    Площадкамана построили, насяльника, можно заселять игроков
                    */

        objectsOnMap.add(new Player(new Point(1, 1)));//Первый игрок, id = 1
        objectsOnMap.add(new Player(new Point(gameZone, 1)));//Второй игрок, id = 2
        objectsOnMap.add(new Player(new Point(1, gameZone)));//Третий игрок, id = 3
        objectsOnMap.add(new Player(new Point(gameZone, gameZone)));//Четвертый игрок, id = 4


        //Заполним Box и Wall
        for (int j = 0; j <= gameZone; j = j + brickSize) {
            for (int i = 0; i <= gameZone; i = i + brickSize) {
                    /*
                    Представим нашу игровую площадку как двумерный массив. Прогуляемся по нему,
                    попутно расставляя объекты по принципу:
                    четная i и четная j заполняется Wall, остальное Box
                    */
                if ((i % 2 == 0) && (j % 2 == 0)) {
                    objectsOnMap.add(new Wall(new Point(i, j)));
                } else {
                    objectsOnMap.add(new Box(new Point(i, j), true));
                }
            }
        }

                    /*
                    Пространство вокруг игроков надо освободить, поэтому
                    */

        //Для первого игрока(Вверх-Влево)
        objectsOnMap.set(1, new Box(new Point(1, 1), false));
        objectsOnMap.set(2, new Box(new Point(2, 1), false));
        objectsOnMap.set(gameZone + 1, new Box(new Point(1, 2), false));
        //Для второго игрока(Вверх-Вправо)
        objectsOnMap.set(gameZone, new Box(new Point(gameZone, 1), false));
        objectsOnMap.set(gameZone - 1, new Box(new Point(gameZone - 1, 1), false));
        objectsOnMap.set(2 * gameZone, new Box(new Point(gameZone, 2), false));
        //Для третьего игрока(Вниз-Влево)
        int curId = gameZone * (gameZone - 1);
        objectsOnMap.set(curId, new Box(new Point(1, gameZone), false));
        objectsOnMap.set(curId + 1, new Box(new Point(2, gameZone), false));
        objectsOnMap.set(curId - gameZone, new Box(new Point(1, gameZone - 1), false));

        //Для четвертого игрока(Вниз-Влево)
        curId = gameZone * gameZone;
        objectsOnMap.set(curId, new Box(new Point(gameZone, gameZone), false));
        objectsOnMap.set(curId - 1, new Box(new Point(gameZone - 1, gameZone), false));
        objectsOnMap.set(curId - gameZone, new Box(new Point(gameZone - 1, gameZone), false));


                    /*
                    Теперь (в итоге) бонусы, отдельный лист, так как объект не материален
                    */

        Random rand = new Random();//Рандомная координата выпадающего бонуса (но в пределах gameZone)
        for (int i = 0; i <= bonusCount; i++) {

            bonusOnMap.add(new Bonus(new Point(rand.nextInt(gameZone - 1) + 1,
                    rand.nextInt(gameZone - 1) + 1), Bonus.Type.SPEED, false, true));
        }

        for (int i = 0; i <= bonusCount; i++) {
            bonusOnMap.add(new Bonus(new Point((rand.nextInt(gameZone - 1) + 1),
                    (rand.nextInt(gameZone - 1) + 1)), Bonus.Type.BOMB, false, true));
        }

        for (int i = 0; i <= bonusCount; i++) {
            bonusOnMap.add(new Bonus(new Point((rand.nextInt(gameZone - 1) + 1),
                    (rand.nextInt(gameZone - 1) + 1)), Bonus.Type.RANGE, false, true));
        }
        return objectsOnMap;
    }

    public void readInputQueue() {

        //Напоминаю, что в objectsOnMap Игроки занимают индексты (id) с 1 по 4
        while (!eventQueue.isEmpty()) { //делать до тех пор пока очередь не опустеет

            Integer playerId = eventQueue.element().getId(); //заранее узнаем id игрока, возглавляющего очередь

            switch (eventQueue.element().getType()) { //либо шагает U,D,R,L, либо ставит бомбу B

                case U: //если идет вверх то заменяем игрока новым с координатами, к которым он прошагал за move
                    Player currentPlayer = (Player) objectsOnMap.get(playerId);
                    playersOnMap.add(playerId, new Player(currentPlayer.move(Movable.Direction.UP, 10L)));
                    break;

                case D://вместо 10l надо вставить tick
                    currentPlayer = (Player) objectsOnMap.get(playerId);
                    playersOnMap.add(playerId, new Player(currentPlayer.move(Movable.Direction.DOWN, 10L)));
                    break;

                case L:
                    currentPlayer = (Player) objectsOnMap.get(playerId);
                    playersOnMap.add(playerId, new Player(currentPlayer.move(Movable.Direction.LEFT, 10L)));
                    break;

                case R:
                    currentPlayer = (Player) objectsOnMap.get(playerId);
                    playersOnMap.add(playerId, new Player(currentPlayer.move(Movable.Direction.RIGHT, 10L)));
                    break;

                case B:
                    currentPlayer = (Player) objectsOnMap.get(playerId);

                    playersOnMap.add(playerId, new Player(currentPlayer.move(Movable.Direction.IDLE, 10L)));
                    bombOnMap.add(playerId, new Bomb(currentPlayer.getPosition()));
                    break;

                default:
                    break;
            }
            eventQueue.remove();//удаляем главу этой очереди


        }


    }

    public void clearInputQueue() {
        eventQueue.clear();
    }

    public ArrayList<GameObject> doMechanic() {

        ArrayList<GameObject> mechanic = new ArrayList<>();

        //Сначала разберемся с перемещениями
        for (GameObject list : objectsOnMap) {
            for (GameObject player : playersOnMap) {

                if (!list.getPosition().isColliding(player.getPosition())) {//если перемещение не сквозь стену, то
                    objectsOnMap.set(player.getId(), new Player(player.getPosition()));//Переместить игрока
                }
            }
        }

        //Вдруг взяли бонус?

        //Надо протикать бомбу или взорвать ее если время вышло

        //Если взрыв прошел в этот тик, уничтожить коробки в эпцентре взрыва

        //Вдруг у кого то game over?







        /*
     Прежде чем писать механику, надо бы определиться как вообще описывать объекты
     (аля Brick содержит поля coord и exist?(bool))
     Player содержит coord
     Bomb содержит coord и countdown
     Bonus предлагаю давать рандомные координаты по полю (и ставить условие, что если бонус на неразрушаемой стене,
     то прогнать алгоритм еще раз)
     */
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
