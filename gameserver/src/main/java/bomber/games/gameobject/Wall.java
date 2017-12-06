package bomber.games.gameobject;

import bomber.games.geometry.Point;
import bomber.games.model.Positionable;
import bomber.games.gamesession.GameSession;
import org.slf4j.LoggerFactory;


public final class Wall implements Positionable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Wall.class);

    private final int id=0;
    private final Point position;

    public Wall(final Point position) {
        this.position = position;
        log.info("New Wall: id={},  id={}, position({}, {})", id, position.getX(), position.getY());
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
