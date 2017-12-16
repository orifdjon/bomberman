package bomber.games.gameobject;

import bomber.games.geometry.Point;
import bomber.games.model.Positionable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import bomber.games.model.Tickable;


public final class Bomb implements Tickable, Positionable, Comparable {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Bomb.class);
    private static final long LIFE_TIME = 2500; //after pass death

    private Point position;
    private int id;
    @JsonIgnore
    private int lifeTime = 1000;//вообще тут знать бы сколько tick у нас происходит в одну секунду
    @JsonIgnore
    private int explosionRange;
    boolean isNewBombStillCollide = true;
    private final String type = "Bomb";
    private boolean isAlive = true;
    private int playerId;

    public boolean getIsAlive() {
        return isAlive;
    }



    public Bomb() {
    }

    public Bomb(final int id, final Point position, final int explosionRange, final int playerId) {
        this.id = id;
        this.position = position;
        this.explosionRange = explosionRange;
        this.playerId = playerId;

        log.info("New Bomb: id={}, position({}, {})\n", id, position.getX(), position.getY());
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public void tick(final long elapsed) {
        lifeTime -= elapsed;
        log.info("lifeTime " + lifeTime);
        if (lifeTime <= 0)
            isAlive = false;
        log.info("isAlive " + isAlive);
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof Bomb) {
            Bomb bomb = (Bomb) obj;
            return this.id == bomb.id;
        }
        return false;
    }

    @Override
    public String toString() {
        return "\nBomb: {" +
                "\nid = " + id +
                "\nposition = " + position +
                "\nrangeExplosion = " + explosionRange +
                "\nlifeTime = " + lifeTime +
                "\n}";
    }

    public void decrementLifeTime() {
        --this.lifeTime;
    }

    public int getLifeTime() {
        return this.lifeTime;
    }

    public int getExplosionRange() {
        return explosionRange;
    }

    public String getType() {
        return type;
    }

    public boolean isNewBombStillCollide() {
        return isNewBombStillCollide;
    }

    public void setNewBombStillCollide(boolean newBombStillCollide) {
        isNewBombStillCollide = newBombStillCollide;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (this.id == o.hashCode())
            return 0;
        else
            return -1;
    }
}
