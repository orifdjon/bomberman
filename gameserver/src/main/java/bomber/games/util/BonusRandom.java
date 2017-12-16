package bomber.games.util;

import bomber.games.gameobject.Bonus;

import java.util.Random;

public class BonusRandom {
    private final int Each_Bonus_Max;
    public static final int BONUS_PER_PLAYER = 3; //if changing to a number not dividable by 3 make sure that
    private int bonusCount = 0;                                              //Each_Bonus_Max will be ok
    private final int Total_Maximum;
    private Bonus.Type bonusType[] = new Bonus.Type[] {null, Bonus.Type.Bonus_Speed, Bonus.Type.Bonus_Bomb, Bonus.Type.Bonus_Fire};
    private Random random;

    public BonusRandom(int playersCount) {
        random = new Random(System.currentTimeMillis());
        Total_Maximum = playersCount * BONUS_PER_PLAYER;
        Each_Bonus_Max = Total_Maximum / playersCount;
    }

    public Bonus.Type randomBonus() {
        if (bonusCount < Total_Maximum) {
            int tmp = random.nextInt(4);
            if (tmp == 0) {
                return null;
            } else {
                if (random.nextInt(2) == 0) {
                    return null;
                } else {
                    return bonusType[tmp];
                }
            }
        } else {
            return null;
        }
    }

}
