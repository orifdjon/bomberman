package bomber.gameservice;


import bomber.games.gameobject.Box;
import bomber.games.gameobject.Player;
import bomber.games.gameobject.Wall;
import bomber.games.gamesession.GameMechanics;
import bomber.games.gamesession.MechanicsSubroutines;
import bomber.games.geometry.Point;
import bomber.games.model.GameObject;
import org.junit.Assert;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MechSubTest {

    @Test
    public void colliderTest() {

        MechanicsSubroutines mechanicsSubroutines = new MechanicsSubroutines();
        Map<Integer,GameObject> map = new HashMap<>();
        map.put(1,new Wall(1,new Point(5,0)));
        map.put(2,new Box(2,new Point(0,5)));

        Assert.assertFalse(mechanicsSubroutines.collisionCheck(new Player(5, new Point(0,0)),map));


        Map<Integer,GameObject> map2 = new HashMap<>();
        map2.put(1,new Wall(1,new Point(29,0)));
        map2.put(2,new Box(2,new Point(0,29)));
        Assert.assertTrue(mechanicsSubroutines.collisionCheck(new Player(6, new Point(0,0)),map2));




    }

}
