package bomber.games.model;

/**
 * Any game object that changes with time
 */
public interface Tickable {
    /**
     * Applies changes to game objects that happen after elapsed time
     */
    void tick(long elapsed);

    /**
     * @return true if tickable object still exists
     */
    boolean isAlive();
}
