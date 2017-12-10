package bomber.games.gameobject;


import bomber.games.geometry.Point;
import bomber.games.model.Movable;
import org.slf4j.LoggerFactory;


public final class Player implements Movable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Player.class);


    private Point position;
    private final long id;
    private long lifeTime;
    private int velocity;
    private int explosionRange;
    private int bombCount;

    private final String type = "Pawn";


    public Player(final long id, final Point position) {
        this.id = id;
        this.position = position;
        this.explosionRange = 1;
        this.velocity = 1;
        this.bombCount = 1;
        this.lifeTime = 0; //надо над этим подумать
    }

    @Override
    public Point move(Direction direction, long time) {
        if (time > 0) {
            switch (direction) {
                case UP:
                    position = new Point(position.getX(), (int) (position.getY() + (time * velocity)));
                    tick(1L);
                    break;
                case DOWN:
                    position = new Point(position.getX(), (int) (position.getY() - (time * velocity)));
                    tick(1L);
                    break;
                case RIGHT:
                    position = new Point((int) (position.getX() + (time * velocity)), position.getY());
                    tick(1L);
                    break;
                case LEFT:
                    position = new Point((int) (position.getX() - (time * velocity)), position.getY());
                    tick(1L);
                    break;
                case IDLE:
                    tick(1L);
                    break;
                default:
                    break;
            }
        } else
            throw new IllegalArgumentException("can't support negative time");
        return position;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void tick(long elapsed) {
        lifeTime += elapsed;
    }

    public int getExplosionRange() {
        return explosionRange;
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
                Player player = (Player) obj;
                return this.id ==  player.id;
            }
            return false;
    }

    @Override
    public String toString() {
        return "Player: {" +
                "\nid = " + id +
                "\nposition = " + position +
                "\nexplosionRange = " + explosionRange +
                "\nvelocity = " + velocity +
                "\nbombCount = " + bombCount +
                "\nlifeTime = " + lifeTime +
                "\n}";
    }

    public int getBombCount() {
        return bombCount;
    }


}
