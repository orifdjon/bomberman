package bomber.games.gamesession;

import bomber.connectionhandler.PlayerAction;
import bomber.games.gameobject.*;
import bomber.games.geometry.Point;
import bomber.games.model.GameObject;
import bomber.games.model.Movable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameMechanics {


    private Map<Integer, GameObject> replica = new HashMap<>();
    ConcurrentLinkedQueue<PlayerAction> eventQueue = new ConcurrentLinkedQueue();
    private Map<Integer, PlayerAction> actionOnMap = new HashMap<>();


    //В оригинальной версии поле 16*16
    final int gameZone = 15;//0,16 - стенки
    public int playersCount = 4;//Число игроков
    final int brickSize = 0;//в будущем, когда будет накладываться на это дело фронтенд, это пригодится
    final int bonusCount = 4;//3*Количество бонусов, которые отспаунятся

    public Map<Integer, GameObject> setupGame(GameSession gameSession) {

        //Создали землю, на которой будем играть
        replica.put(gameSession.getInc(), new GameGround(gameSession.getId(), new Point(0, 0)));//для механики бесполезно, а фронтенду необходимо

        /*
        Площадкамана построили, насяльника, можно заселять игроков
        */
        replica.put(gameSession.getInc(), new Player(gameSession.getId(), new Point(1, 1)));//Первый игрок
        replica.put(gameSession.getInc(), new Player(gameSession.getId(), new Point(gameZone, 1)));//Второй игрок
        replica.put(gameSession.getInc(), new Player(gameSession.getId(), new Point(1, gameZone)));//Третий игрок
        replica.put(gameSession.getInc(), new Player(gameSession.getId(), new Point(gameZone, gameZone)));//Четвертый игрок


        //Заполним Box и Wall
        for (int j = 1; j <= gameZone; j = j + brickSize) {
            for (int i = 1; i <= gameZone; i = i + brickSize) {
                    /*
                    Представим нашу игровую площадку как двумерный массив. Прогуляемся по нему,
                    попутно расставляя объекты по принципу:
                    четная i и четная j заполняется Wall, остальное Box
                    */
                if ((i % 2 == 0) && (j % 2 == 0)) {
                    replica.put(gameSession.getInc(), new Wall(gameSession.getId(), new Point(i, j)));
                } else {
                    replica.put(gameSession.getInc(), new Box(gameSession.getId(), new Point(i, j)));
                }
            }
        }

        /*
        Пространство вокруг игроков надо освободить, поэтому
        ВАЖНО!!Если найдется герой, который это сделает грамотней - добро пожаловать, а пока вот так
        (когда каждый объект лежал в отдельной мапе - такой лажи не было
        */

        //Для первого игрока(Вверх-Влево)
        replica.remove(1 + 5);
        replica.remove(2 + 5);
        replica.remove(gameZone + 1 + 5);
        //Для второго игрока(Вверх-Вправо)
        replica.remove(gameZone + 5);
        replica.remove(gameZone - 1 + 5);
        replica.remove(2 * gameZone + 5);
        //Для третьего игрока(Вниз-Влево)
        replica.remove(gameZone * (gameZone - 1 + 5));
        replica.remove(gameZone * (gameZone - 1) + 1 + 5);
        replica.remove(gameZone * (gameZone - 1) - gameZone + 5);
        //Для четвертого игрока(Вниз-Влево)
        replica.remove(gameZone * gameZone + 5);
        replica.remove(gameZone * gameZone - 1 + 5);
        replica.remove(gameZone * gameZone - gameZone + 5);



         /*
         Теперь (в итоге) бонусы
         */

        Random rand = new Random();//Рандомная координата выпадающего бонуса (но в пределах gameZone)
        for (int i = 0; i <= bonusCount; i++) {

            replica.put(gameSession.getInc(), new Bonus(gameSession.getId(), new Point(rand.nextInt(gameZone - 1) + 1,
                    rand.nextInt(gameZone - 1) + 1), Bonus.Type.BONUS_SPEED));
        }

        for (int i = 0; i <= bonusCount; i++) {
            replica.put(gameSession.getInc(), new Bonus(gameSession.getId(), new Point(rand.nextInt(gameZone - 1) + 1,
                    rand.nextInt(gameZone - 1) + 1), Bonus.Type.BONUS_BOMB));
        }

        for (int i = 0; i <= bonusCount; i++) {
            replica.put(gameSession.getInc(), new Bonus(gameSession.getId(), new Point(rand.nextInt(gameZone - 1) + 1,
                    rand.nextInt(gameZone - 1) + 1), Bonus.Type.BONUS_RANGE));
        }

        /*
        Теперь надо окружить игровое поле непробиваемыми стенами, чтобы никто не убежал
         */
        for (int j = 1; j <= gameZone + 1; j = j + brickSize) {
                replica.put(gameSession.getInc(), new Wall(gameSession.getId(), new Point(0, j)));
                replica.put(gameSession.getInc(), new Wall(gameSession.getId(), new Point(gameZone+1, j)));
                replica.put(gameSession.getInc(),new Wall(gameSession.getId(),new Point(j,0)));
                replica.put(gameSession.getInc(),new Wall(gameSession.getId(),new Point(j,gameZone+1)));
        }


        return replica;
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

    public Map<Integer, GameObject> doMechanic(GameSession gameSession, Map<Integer, GameObject> replica) {

        for (GameObject gameObject : replica.values()) {
            MechanicsSubroutines mechanicsSubroutines = new MechanicsSubroutines();//подняли вспомогательные методы

            if (gameObject instanceof Player) {
                Player currentPlayer = ((Player) gameObject);

                switch (actionOnMap.get(gameObject.getId()).getType()) { //либо шагает Up,Down,Right,Left, либо ставит бомбу Bomb

                    case UP: //если идет вверх
                        currentPlayer.setPosition(((Player) gameObject).move(Movable.Direction.UP));//задали новые координаты
                        if (mechanicsSubroutines.collisionCheck(gameObject, replica)) {//Если никуда не врезается, то
                            replica.replace(gameObject.getId(), currentPlayer);//перемещаем игрока
                        }
                        //Если проверку не прошла, то все остается по старому
                        break;

                    case DOWN:
                        currentPlayer.setPosition(((Player) gameObject).move(Movable.Direction.DOWN));//задали новые координаты
                        if (mechanicsSubroutines.collisionCheck(gameObject, replica)) {//Если никуда не врезается, то
                            replica.replace(gameObject.getId(), currentPlayer);//перемещаем игрока
                        }
                        //Если проверку не прошла, то все остается по старому

                        break;
                    case LEFT:
                        currentPlayer.setPosition(((Player) gameObject).move(Movable.Direction.LEFT));//задали новые координаты
                        if (mechanicsSubroutines.collisionCheck(gameObject, replica)) {//Если никуда не врезается, то
                            replica.replace(gameObject.getId(), currentPlayer);//перемещаем игрока
                        }
                        //Если проверку не прошла, то все остается по старому

                        break;
                    case RIGHT:
                        currentPlayer.setPosition(((Player) gameObject).move(Movable.Direction.RIGHT));//задали новые координаты
                        if (mechanicsSubroutines.collisionCheck(gameObject, replica)) {//Если никуда не врезается, то
                            replica.replace(gameObject.getId(), currentPlayer);//перемещаем игрока
                        }
                        //Если проверку не прошла, то все остается по старому

                        break;
                    case BOMB:
                        replica.put(gameSession.getInc(), new Bomb(gameSession.getId(),
                                currentPlayer.getPosition(), currentPlayer.getRangeExplosion()));

                    default:
                        break;
                }
                if (!(mechanicsSubroutines.bonusCheck(currentPlayer, replica) == null)) { //если был взят бонус
                    Bonus getBonus = (Bonus) replica.get(mechanicsSubroutines.bonusCheck(currentPlayer, replica));
                    switch (getBonus.getType()) { //Узнаем что это за бонус

                        case BONUS_BOMB:
                            currentPlayer.setCountBomb(currentPlayer.getCountBomb() + 1);
                            break;

                        case BONUS_RANGE:
                            currentPlayer.setRangeExplosion(currentPlayer.getRangeExplosion() + 1);
                            break;
                        case BONUS_SPEED:
                            currentPlayer.setVelocity(currentPlayer.getVelocity() + 1); //вот тут конечно надо бы оптимизировать
                            break;
                        default:
                            break;
                    }

                }
            }

            if (gameObject instanceof Bomb) { //начинаем работать с бомбами
                if (!(((Bomb) gameObject).getLifeTime() == 0)) { //если эта бомба еще не взорвалась
                    ((Bomb) gameObject).decrementLifeTime(); //отнимем время до взрыва
                } else { //если взорвалась то

                    //Слооожнаа
                    boolean up = true;
                    boolean down = true;
                    boolean left = true;
                    boolean right = true;
                    //это все индикаторы, отслеживающие был ли уже взрыв объекта по одной их 4х сторон взрыва, чего то более изящного не придумал

                    //Начальные координаты (он же эпицентр взрыва)
                    int epicenterX = gameObject.getPosition().getX();
                    int epicenterY = gameObject.getPosition().getY();


                    for (GameObject boxObject : replica.values()) { //пройдем по реплике в поисках жертв
                        if ((boxObject instanceof Box) | (boxObject instanceof Wall)) { //упростим прогон, пробежавшись только по коробкам
                            for (int i = 1; i <= ((Bomb) gameObject).getRangeExplosion(); i++) { //надо узнать силу взрыва
                                for (int j = 1; j <= 4; j++) { //взрыв на все 4 стороны
                                    switch (j) {
                                        case 1: //если идет вверх
                                            if (up) {
                                                Point currentPoint = new Point(epicenterX, epicenterY + i * brickSize);//сместились вверх от эпицентра, в зависимости от глубины взрыва
                                                if (mechanicsSubroutines.createExplosions(currentPoint, gameObject, replica)) {
                                                    replica.put(gameSession.getInc(), new Explosion(gameSession.getId(), currentPoint)); //место не занято, отрисуем взрыв
                                                } else {
                                                    up = false; //взрыв был потрачен либо в стену, либо в коробку, дальше взрыв не пойдет
                                                }
                                            }
                                            break;
                                        case 2://если идет вниз
                                            if (down) {
                                                Point currentPoint = new Point(epicenterX, epicenterY - i * brickSize);
                                                if (mechanicsSubroutines.createExplosions(currentPoint, gameObject, replica)) {
                                                    replica.put(gameSession.getInc(), new Explosion(gameSession.getId(), currentPoint));
                                                } else {
                                                    down = false;
                                                }
                                            }
                                            break;
                                        case 3://если идет влево
                                            if (left) {
                                                Point currentPoint = new Point(epicenterX - i * brickSize, epicenterY);
                                                if (mechanicsSubroutines.createExplosions(currentPoint, gameObject, replica)) {
                                                    replica.put(gameSession.getInc(), new Explosion(gameSession.getId(), currentPoint));
                                                } else {
                                                    left = false;
                                                }
                                            }
                                            break;
                                        case 4://если идет вправо
                                            if (right) {
                                                Point currentPoint = new Point(epicenterX + i * brickSize, epicenterY);
                                                if (mechanicsSubroutines.createExplosions(currentPoint, gameObject, replica)) {
                                                    replica.put(gameSession.getInc(), new Explosion(gameSession.getId(), currentPoint));
                                                } else {
                                                    right = false;
                                                }
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            replica.remove(gameObject.getId()); //Удалим бомбу из replica
            if (gameObject instanceof Explosion) { //Найдем тех, кого в итоге убило ^__^ ну и протикаем продолжительность взрыва
                if (((Explosion) gameObject).getLifeTime() == 0) { //если время закончилось
                    replica.remove(gameObject.getId());//удалим его
                } else { //если взрыв еще держится
                    ((Explosion) gameObject).decrementLifeTime(); //отнимем время до взрыва


                    for (GameObject playerObject : replica.values()) { //Далее охотимся на игроков
                        if (playerObject instanceof Player) {
                            mechanicsSubroutines.youDied(replica, ((Player) playerObject), ((Explosion) gameObject));
                        }
                    }
                }
            }
        }
        return replica;
    }
}