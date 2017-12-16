package bomber.games.gamesession;

import bomber.gameservice.controller.EventHandler;
import bomber.connectionhandler.PlayerAction;
import bomber.games.gameobject.*;
import bomber.games.geometry.Point;
import bomber.games.model.GameObject;
import bomber.games.model.Movable;
import bomber.games.model.Tickable;
import bomber.games.util.BonusRandom;
import bomber.games.util.SpawnPositionsCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class GameMechanics {

    private static final Logger log = LoggerFactory.getLogger(GameMechanics.class);
    private static final int MAX_PLAYER_IN_GAME = 4;
    private Map<Integer, PlayerAction> actionOnMap = new HashMap<>();
    public static final int gameZone_X = 17;//0,16 - стенки по X
    public static final int gameZone_Y = 13; //0,12 - стенки по Y
    private int playersCount;//Число игроков
    public static final int brickSize = 32;//в будущем, когда будет накладываться на это дело фронтенд, это пригодится
    private final List<Integer> listPlayerId;
    private int positionSetting;  //choose which spawn positions will be applied
    private Set<Tickable> tickables = new ConcurrentSkipListSet<>();
    private ArrayList<GameObject> map = new ArrayList<>();

    public GameMechanics(int positionSetting, int playersCount) {
        this.positionSetting = positionSetting;
        this.playersCount = playersCount;
        this.listPlayerId = new ArrayList<>();
        listPlayerId.add(null);
        this.listPlayerId.addAll(EventHandler.getSessionIdList());
    }

    public void setupGame(Map<Integer, GameObject> replica, AtomicInteger idGenerator) {


//        idGenerator.getAndIncrement();
//        replica.put(idGenerator.get(), new Bonus(idGenerator.get(),
//                new Point(brickSize, 2 * brickSize), Bonus.Type.Bonus_Speed));
//
//        idGenerator.getAndIncrement();
//        replica.put(idGenerator.get(), new Bonus(idGenerator.get(),
//                new Point(2*brickSize, brickSize), Bonus.Type.Bonus_Bomb));

        BonusRandom bonusRandom = new BonusRandom(playersCount);
        List<Point> playerPositions = SpawnPositionsCollection.getDefaultPositions();
        for (int x = 0; x <= gameZone_X; x++) {
            for (int y = 0; y <= gameZone_Y; y++) {
                if (y == 0 || x == 0 || x * brickSize == (gameZone_X * brickSize - brickSize) ||
                        y * brickSize == (gameZone_Y * brickSize - brickSize)) {
                    idGenerator.getAndIncrement();
                    replica.put(idGenerator.get(), new Wall(idGenerator.get(),
                            new Point(x * brickSize, y * brickSize)));
                }
                if (!(y == 0 || x == 0 || x * brickSize == (gameZone_X * brickSize - brickSize) ||
                        y * brickSize == (gameZone_Y * brickSize - brickSize)) && ((x % 2 == 0) && (y % 2 == 0))) {
                    idGenerator.getAndIncrement();
                    replica.put(idGenerator.get(), new Wall(idGenerator.get(),
                            new Point(x * brickSize, y * brickSize)));
                } else {
                    if (!(y == 0 || x == 0 || x * brickSize == (gameZone_X * brickSize - brickSize) ||
                            y * brickSize == (gameZone_Y * brickSize - brickSize))) {
                        if (!isPlayerSpawn(x, y, playerPositions)) {
                            Bonus.Type bonus = bonusRandom.randomBonus();
                            if (bonus != null) {
                                idGenerator.getAndIncrement();
                                replica.put(idGenerator.get(), new Bonus(idGenerator.get(),
                                        new Point(x * brickSize, y * brickSize), bonus));
                            }
                            idGenerator.getAndIncrement();
                            replica.put(idGenerator.get(), new Box(idGenerator.get(),
                                    new Point(x * brickSize, y * brickSize)));
                        }
                    }
                }
            }
        }

        for (int i = 1; i <= playersCount; i++) {
            replica.put(listPlayerId.get(i), new Player(listPlayerId.get(i), playerPositions.get(i)));
            registerTickable((Tickable) replica.get(listPlayerId.get(i)));
            try {
                EventHandler.sendPossess(listPlayerId.get(i));
            } catch (IOException e) {
                log.error("unable to sendPosses");
            }
        }
    }

    private boolean isPlayerSpawn(int x, int y, List<Point> PlayerPosition) {
        boolean flag = false;
        for (int i = 1; i <= playersCount; i++) {
            Point playerPoint = new Point(PlayerPosition.get(i).getX(),
                    PlayerPosition.get(i).getY());
            Point currentPoint = new Point(x * brickSize, y * brickSize);
            if (playerPoint.equals(currentPoint))
                flag = true;
            if (new Point(playerPoint.getX() + brickSize, playerPoint.getY()).equals(currentPoint))
                flag = true;
            if (new Point(playerPoint.getX(), playerPoint.getY() + brickSize).equals(currentPoint))
                flag = true;
            if (new Point(playerPoint.getX() - brickSize, playerPoint.getY()).equals(currentPoint))
                flag = true;
            if (new Point(playerPoint.getX(), playerPoint.getY() - brickSize).equals(currentPoint))
                flag = true;
        }
        return flag;
    }

    public void readInputQueue(ConcurrentLinkedQueue<PlayerAction> inputQueue) {

        while (!inputQueue.isEmpty()) { //делать до тех пор пока очередь не опустеет
            Integer playerId = inputQueue.element().getId(); //заранее узнаем id игрока, возглавляющего очередь
            if (!actionOnMap.containsKey(playerId)) { //если действий от этого игрока еще не было
                actionOnMap.put(playerId, inputQueue.element());//Запишем действие в мапу
            }
            log.info("Здесь будет actionOnMap");
            log.info(actionOnMap.toString());
            inputQueue.remove();//удаляем главу этой очереди
        }
    }

    public void clearInputQueue(ConcurrentLinkedQueue<PlayerAction> inputQueue) {
        if (!inputQueue.isEmpty())
            inputQueue.clear();
        actionOnMap.clear();
    }


    public void doMechanic(Map<Integer, GameObject> replica, AtomicInteger idGenerator) {
        for (GameObject gameObject : replica.values()) {
            MechanicsSubroutines mechanicsSubroutines = new MechanicsSubroutines();//подняли вспомогательные методы
            if (gameObject instanceof Player) {
                Player currentPlayer = ((Player) gameObject);
                Point previosPos = currentPlayer.getPosition();
                if (actionOnMap.containsKey(gameObject.getId())) {
                    log.info("currentPlayerId = " + currentPlayer.getId());
                    switch (actionOnMap.get(currentPlayer.getId()).getType()) { //либо шагает Up,Down,Right,Left, либо ставит бомбу Bomb
                        case UP: //если идет вверх
                            //задали новые координаты
                                currentPlayer.setPosition(currentPlayer.move(Movable.Direction.UP));
                            if (!(mechanicsSubroutines.collisionCheck(currentPlayer, replica))) {
                                currentPlayer.setPosition(previosPos);
                            }
                            break;

                        case DOWN:
                            //задали новые координаты
                                currentPlayer.setPosition(currentPlayer.move(Movable.Direction.DOWN));
                            if (!(mechanicsSubroutines.collisionCheck(currentPlayer, replica))) {
                                currentPlayer.setPosition(previosPos);
                            }
                            break;
                        case LEFT:
                            //задали новые координаты

                                currentPlayer.setPosition(currentPlayer.move(Movable.Direction.LEFT));
                            if (!(mechanicsSubroutines.collisionCheck(currentPlayer, replica))) {
                                currentPlayer.setPosition(previosPos);
                            }
                            break;
                        case RIGHT:

                                currentPlayer.setPosition(currentPlayer.move(Movable.Direction.RIGHT));
                            if (!(mechanicsSubroutines.collisionCheck(currentPlayer, replica))) {
                                currentPlayer.setPosition(previosPos);
                            }
                            break;
                        case BOMB:
                            if (currentPlayer.getBombCount() < currentPlayer.getMaxBombs()) {
                                idGenerator.getAndIncrement();
                                Bomb tmpBomb = new Bomb(idGenerator.get(),
                                        new Point(currentPlayer.getPosition().getX() + brickSize / 4,
                                                currentPlayer.getPosition().getY() - brickSize / 4),
                                        currentPlayer.getBombPower(), currentPlayer.getId());
                                replica.put(idGenerator.get(), tmpBomb);
                                registerTickable(tmpBomb);
                                currentPlayer.incBombCount();
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (!(mechanicsSubroutines.bonusCheck(currentPlayer, replica) == null)) { //если был взят бонус
                    Integer tmp = mechanicsSubroutines.bonusCheck(currentPlayer, replica);
                    Bonus getBonus = (Bonus) replica.get(tmp);
                    switch (getBonus.getType()) { //Узнаем что это за бонус
                        case Bonus_Bomb:
                            currentPlayer.setMaxBombs(currentPlayer.getMaxBombs() + 1);
                            break;
                        case Bonus_Fire:
                            currentPlayer.setBombPower(currentPlayer.getBombPower() + 1);
                            break;
                        case Bonus_Speed:
                            currentPlayer.setVelocity(currentPlayer.getVelocity() * 2); //вот тут конечно надо бы оптимизировать
                            break;
                        default:
                            break;
                    }
                    replica.remove(tmp);
                }
            }

            /*if (gameObject instanceof Bomb) { //начинаем работать с бомбами
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
                            for (int i = 1; i <= ((Bomb) gameObject).getBombPower(); i++) { //надо узнать силу взрыва
                                for (int j = 1; j <= 4; j++) { //взрыв на все 4 стороны
                                    switch (j) {
                                        case 1: //если идет вверх
                                            if (up) {
                                                Point currentPoint = new Point(epicenterX, epicenterY + i * brickSize);//сместились вверх от эпицентра, в зависимости от глубины взрыва
                                                if (mechanicsSubroutines.createExplosions(currentPoint, gameObject, replica)) {
                                                    replica.put(idGenerator.getAndIncrement(), new Explosion(idGenerator.get(), currentPoint)); //место не занято, отрисуем взрыв
                                                } else {
                                                    up = false; //взрыв был потрачен либо в стену, либо в коробку, дальше взрыв не пойдет
                                                }
                                            }
                                            break;
                                        case 2://если идет вниз
                                            if (down) {
                                                Point currentPoint = new Point(epicenterX, epicenterY - i * brickSize);
                                                if (mechanicsSubroutines.createExplosions(currentPoint, gameObject, replica)) {
                                                    replica.put(idGenerator.getAndIncrement(), new Explosion(idGenerator.get(), currentPoint));
                                                } else {
                                                    down = false;
                                                }
                                            }
                                            break;
                                        case 3://если идет влево
                                            if (left) {
                                                Point currentPoint = new Point(epicenterX - i * brickSize, epicenterY);
                                                if (mechanicsSubroutines.createExplosions(currentPoint, gameObject, replica)) {
                                                    replica.put(idGenerator.getAndIncrement(), new Explosion(idGenerator.get(), currentPoint));
                                                } else {
                                                    left = false;
                                                }
                                            }
                                            break;
                                        case 4://если идет вправо
                                            if (right) {
                                                Point currentPoint = new Point(epicenterX + i * brickSize, epicenterY);
                                                if (mechanicsSubroutines.createExplosions(currentPoint, gameObject, replica)) {
                                                    replica.put(idGenerator.getAndIncrement(), new Explosion(idGenerator.get(), currentPoint));
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
            }*/
        }
    }


    private List<Point> setSpawnPositions() {   //only default realisation now, may be expanded for more spawn options
        return null;
    }

    public void setTickables(Set<Tickable> tickables) {
        this.tickables = tickables;
    }

    public void registerTickable(Tickable tickable) {
        tickables.add(tickable);
    }

    public void unregisterTickable(Tickable tickable) {
        tickables.remove(tickable);
    }

    public ArrayList<GameObject> getMap() {
        return map;
    }
}

