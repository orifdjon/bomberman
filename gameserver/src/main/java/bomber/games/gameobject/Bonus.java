package bomber.games.gameobject;


import bomber.games.gamesession.GameSession;
import bomber.games.geometry.Point;
import bomber.games.model.Positionable;
import org.slf4j.LoggerFactory;

public final class Bonus implements Positionable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Bonus.class);
    private final int id=0;
    private final Point position;
    private final Type type;
    private boolean show;
    private boolean exist;

    public enum Type {
        SPEED, BOMB, RANGE
    }

    public Bonus(final Point position, final Type type, boolean show, boolean exist) {
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
    public int getId() {
        return id;
    }

}
