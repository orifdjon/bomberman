package bomber.games.gameobject;

import bomber.games.geometry.Point;
import bomber.games.model.Positionable;
import org.slf4j.LoggerFactory;
import bomber.games.model.Tickable;


public final class Bomb implements Tickable, Positionable {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Bomb.class);
    private static final long LIFE_TIME = 2500; //after pass death

    private final Point position;
    private final long id;
    private long lifeTime;
    private int rangeExplosion;
    private final String type = "Bomb";


    public Bomb(long id, final Point position) {
        this.id = id;
        this.position = position;
        this.lifeTime = 0;
        this.rangeExplosion = 1;

        log.info("New Bomb: id={}, position({}, {})\n", id, position.getX(), position.getY());
    }

    @Override
    public void tick(final long elapsed) {
        lifeTime += elapsed;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return (int) this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else
        if (obj instanceof Bomb) {
            Bomb bomb = (Bomb) obj;
            return this.id ==  bomb.id;
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
}
