package bomber.games.gameobject;


import bomber.games.gamesession.GameSession;
import bomber.games.geometry.Point;
import bomber.games.model.Positionable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.LoggerFactory;

public final class  Box implements Positionable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Box.class);
    private final Point position;
    private final long id;
    private final String type = "Box";
    @JsonIgnore
    private boolean exist;

    public Box(final long id, final Point position, boolean exist) {
        this.id = id;
        this.position = position;
        this.exist = exist;
        log.info("New Box: id={},  id={}, position({}, {})\n", position.getX(), position.getY());
    }


    @Override
    public Point getPosition() {
        return position;
    }

    public boolean isExist() {
        return exist;
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
            Box box = (Box) obj;
            return this.id ==  box.id;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Box: {" +
                "\nid = " + id +
                "\nposition = " + position +
                "\n}";
    }
}
