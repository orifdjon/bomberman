package bomber.connectionhandler;

import bomber.connectionhandler.json.HandleInputJson;
import bomber.connectionhandler.json.JsonReplica;
import bomber.games.gameobject.Bomb;
import bomber.games.gameobject.Box;
import bomber.games.gameobject.Player;
import bomber.games.gameobject.Wall;
import bomber.games.geometry.Point;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

import java.util.ArrayList;
import java.util.List;


public class JsonTest {
    @Test
    public void possessTest() {
        String json = EventHandler.handlePossessToJson(123);
        Assert.assertEquals(json, "{\"topic\":\"POSSESS\",\"data\":123}");
    }

    @Test
    public void replicaTest() {
        JsonReplica jsonReplica = new JsonReplica();
        List<Bomb> list = new ArrayList<>();
        list.add(new Bomb(1, new Point(10, 20)));
        list.add(new Bomb(2, new Point(1, 2)));
        list.add(new Bomb(3, new Point(1, 10)));
        String json = EventHandler.handleReplica(jsonReplica, list);
        System.out.println(json);

    }

  /*  @Test
    public void replicaTestExample() {
        Replica replica = new Replica();
        List<GameObject> list = new ArrayList<>();
        *//*list.add(new Bomb(1, new Point(10, 20)));
        list.add(new Bomb(2, new Point(1, 2)));
        list.add(new Bomb(3, new Point(1, 10)));
        list.add(new Wall(6, new Point(6, 8)));*//*
        Map<GameObject, GameObject> map = new HashMap<>();
        Bomb bomb = new Bomb(9, new Point(20,15));
        map.put(bomb, bomb);
        String json = EventHandler.handleReplica(replica, list, map);
        System.out.println(json);

    }*/

    @Test
    public void MoveTest() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        FileReader fin = new FileReader("C:/Users/Arif/Desktop/Texnoatom/bomberman/Move.txt");
        int c;
        String json = "";
        while ((c = fin.read()) != -1) {
            json += (char) c;
        }
        PlayerAction playerAction = EventHandler.handleMoveAndPlanBombFromJson(json);
        Assert.assertEquals(playerAction.getType(), PlayerAction.EventType.DOWN);
    }

    @Test
    public void PlantBombTest() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        FileReader fin = new FileReader("C:/Projects/bomberman/Replica.txt");
        int c;
        String json = "";
        while ((c = fin.read()) != -1) {
            json += (char) c;
        }
        PlayerAction playerAction = EventHandler.handleMoveAndPlanBombFromJson(json);
        Assert.assertEquals(playerAction.getType(), PlayerAction.EventType.BOMB);
    }


}