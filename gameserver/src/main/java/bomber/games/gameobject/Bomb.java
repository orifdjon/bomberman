package bomber.games.gameobject;

import bomber.games.geometry.Point;
import bomber.games.model.Positionable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import bomber.games.model.Tickable;


public final class Bomb implements Tickable, Positionable {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Bomb.class);
    private static final long LIFE_TIME = 2500; //after pass death

    private final Point position;
    private final int id;
    private int lifeTime = 300; //вообще тут знать бы сколько tick у нас происходит в одну секунду
    private int rangeExplosion;
    private final String type = "Bomb";


    public Bomb(final int id, final Point position, final int rangeExplosion) {
        this.id = id;
        this.position = position;
        this.lifeTime = 0;
        this.rangeExplosion = rangeExplosion;

        log.info("New Bomb: id={}, position({}, {})\n", id, position.getX(), position.getY());
    }

    @Override
    public void tick(final long elapsed) {
        lifeTime -= elapsed;
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
                "\nrangeExplosion = " + rangeExplosion +
                "\nlifeTime = " + lifeTime +
                "\n}";
    }

    public void decrementLifeTime() {
        this.lifeTime = lifeTime--;
    }

    public int getLifeTime() {
        return this.lifeTime;
    }

    public int getRangeExplosion() {
        return rangeExplosion;
    }
}
