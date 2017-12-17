package bomber.games.util;

import bomber.games.geometry.Point;

import java.util.ArrayList;
import java.util.List;

import static bomber.games.gamesession.GameMechanics.brickSize;
import static bomber.games.gamesession.GameMechanics.gameZone_X;
import static bomber.games.gamesession.GameMechanics.gameZone_Y;

public class SpawnPositionsCollection {
   public static List<Point> getDefaultPositions() {
       List<Point> defaultPositions = new ArrayList<>();
        defaultPositions.add(null);
        defaultPositions.add(new Point(brickSize, brickSize));
        defaultPositions.add(new Point(gameZone_X * brickSize - brickSize * 2, brickSize));
        defaultPositions.add(new Point(brickSize, gameZone_Y * brickSize - brickSize * 2));
        defaultPositions.add(new Point(gameZone_X * brickSize - brickSize * 2, gameZone_Y * brickSize - brickSize * 2));
        return  defaultPositions;
    }

}
