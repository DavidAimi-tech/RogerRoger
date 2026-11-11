package com.example.roger_roger;
public class BattleConfig {

    public static final int MIN_STRENGTH = 1;
    public static final int MAX_STRENGTH = 10;

    public static final int EASY = 90;
    public static final int MEDIUM = 100;
    public static final int HARD = 110;

    public static final int SMALL_ARMY = 15;
    public static final int MEDIUM_ARMY = 20;
    public static final int LARGE_ARMY = 30;

    public static final int SMALL_ARMY_SKIPS = 5;
    public static final int MEDIUM_ARMY_SKIPS = 7;
    public static final int LARGE_ARMY_SKIPS = 9;

    public static final int KILL_RESIST_CHANCE = 35;
    public static final int MAX_CAPTURES = 3;                // per army, per battle
    public static final int LEONE_BONUS_CAPTURES = 2;
    public static final int ENEMY_CAPTURE_MIN_STRENGTH = 6;  // enemy only spends a capture on soldiers this strong
    // percentage points off the chance
    public static final int MAX_ROUNDS = 300;

    public static final int MIN_ROUNDS_BEFORE_SKIP_OR_TRUMP = 2;

    public static final int SKIP_OR_TRUMP_COOLDOWN_ROUNDS = 2;

    public static final int BACCUS_COMBUSTION_REDUCTION = 1;
    public static final int MAX_COMBUSTION_CHANCE = 5;
    public static final int FORSYTHE_STRENGTH_BONUS = 1;

    public static final int MAX_TRUMPS = 3;

    public static final int BUDGET = 100;

    static int skipsFor(int armySize){
        switch (armySize){
            case SMALL_ARMY: return SMALL_ARMY_SKIPS;
            case MEDIUM_ARMY: return MEDIUM_ARMY_SKIPS;
            case LARGE_ARMY: return LARGE_ARMY_SKIPS;
            default: throw new IllegalArgumentException("Invalid army size" + armySize);
        }
    }
}
