package bomber.games.gameobject;

import bomber.games.geometry.Point;
import bomber.games.model.Positionable;
import org.slf4j.LoggerFactory;

public final class Bonus implements Positionable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Bonus.class);
    private final long id;
    private final Point position;
    private boolean show;
    private boolean exist;
    private final Type type;

    public enum Type {
        BONUS_SPEED, BONUS_BOMB, BONUS_RANGE
    }

    public Bonus(final long id, final Point position, final Type type, final boolean show, final boolean exist) {
        this.id = id;
        this.position = position;
        this.type = type;
        this.show = show;
        this.exist = exist;
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
        if (obj instanceof Player) {
            Bonus bonus = (Bonus) obj;
            return this.id ==  bonus.id;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Bonus: {" +
                "\nid = " + id +
                "\nposition = " + position +
                "\ntype = " + type +
                "\n}";
    }
}
