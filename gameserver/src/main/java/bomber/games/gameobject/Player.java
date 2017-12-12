package bomber.games.gameobject;


import bomber.games.geometry.Point;
import bomber.games.model.Movable;
import org.slf4j.LoggerFactory;


public final class Player implements Movable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Player.class);


    private Point position;
    private final int id;
    private long lifeTime;
    private int velocity;
    private int rangeExplosion;
    private int countBomb;
    private final String type = "Pawn";


    public Player(final int id, final Point position) {
        this.id = id;
        this.position = position;
        this.rangeExplosion = 1;
        this.velocity = 1;
        this.countBomb = 1;
        this.lifeTime = 0; //надо над этим подумать
    }

    @Override
    public Point move(Direction direction, long time) {

        switch (direction) {
            case UP:
                position = new Point(position.getX(), (int) (position.getY() + (time*velocity)));
                tick(1L);
                break;
            case DOWN:
                position = new Point(position.getX(), (int) (position.getY() - (time*velocity)));
                tick(1L);
                break;
            case RIGHT:
                position = new Point((int) (position.getX() + (time*velocity)), position.getY());
                tick(1L);
                break;
            case LEFT:
                position = new Point((int) (position.getX() - (time*velocity)), position.getY());
                tick(1L);
                break;
            case IDLE:
                tick(1L);
                break;
            default:
                break;
        }

        return position;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }


    @Override
    public void tick(long elapsed) {
        lifeTime += elapsed;
    }

    public int getRangeExplosion() {
        return rangeExplosion;
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
                "\nrangeExplosion = " + rangeExplosion +
                "\nvelocity = " + velocity +
                "\ncountBomb = " + countBomb +
                "\nlifeTime = " + lifeTime +
                "\n}";
    }

    public int getCountBomb() {
        return countBomb;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public void setCountBomb(int countBomb) {
        this.countBomb = countBomb;
    }

    public void setRangeExplosion(int rangeExplosion) {
        this.rangeExplosion = rangeExplosion;
    }
}