package bomber.games.gameobject;

import bomber.games.geometry.Point;
import bomber.games.model.Positionable;
import org.slf4j.LoggerFactory;
import bomber.games.model.Tickable;


public final class Explosion implements Tickable, Positionable {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Bomb.class);


    private final Point position;
    private final int id;
    private int lifeTime = 60; //вообще тут знать бы сколько tick у нас происходит в одну секунду


    public Explosion(int id, final Point position) {
        this.id = id;
        this.position = position;


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
        return (int) this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof Explosion) {
            Explosion explosion = (Explosion) obj;
            return this.id == explosion.id;
        }
        return false;
    }

    @Override
    public String toString() {
        return "\nExplosion: {" +
                "\nid = " + id +
                "\nposition = " + position +
                "\nlifeTime = " + lifeTime +
                "\n}";
    }

    public void decrementLifeTime() {
        this.lifeTime = lifeTime--;
    }

    public int getLifeTime() {
        return this.lifeTime;
    }
}