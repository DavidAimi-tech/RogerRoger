package com.example.roger_roger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
enum Clan { FORSYTHE, LEONE, BACCUS}
enum   BattleState{ONGOING, WIN, LOSS, DRAW}



public class BattleEngine {

    //Army Conditions
    private final Army userArmy;
    private final Army enemyArmy;

    private final Clan playerClan;
    private final Clan enemyClan;

    //Battle conditions
    private final int combustionRate;

    private int trumpsRemaining = BattleConfig.MAX_TRUMPS;
    private int trumpsUsed;

    //generation
    private final Random random;

    //recording gamestate
    private int skipsRemaining;
    private int userCapturesLeft;
    private int enemyCapturesLeft;

    private BattleState battleState;

    //Result screen
    private int successfulCaptures;
    private int userSoldiersKilled;

    private int enemySoldiersKilled;
    private int killsResisted;
    private int soldiersCombusted;
    private int skipsUsed;
    private int rounds;

    private int skipAvailableAtRound = BattleConfig.MIN_ROUNDS_BEFORE_SKIP_OR_TRUMP;

    private int trumpAvailableAtRound = BattleConfig.MIN_ROUNDS_BEFORE_SKIP_OR_TRUMP;

    private final List<BattleEvent> lastEvents = new ArrayList<>();


    public BattleEngine(Army userArmy, Army enemyArmy, Clan playerClan, Clan enemyClan,  Random random) {
        if(userArmy.size() != enemyArmy.size()){
            throw new IllegalArgumentException("Armies must be equal in size");
        }

        this.userArmy = userArmy;
        this.enemyArmy = enemyArmy;
        this.playerClan = playerClan;
        this.enemyClan = enemyClan;
        this.combustionRate = random.nextInt(4);
        this.random = random;

        this.skipsRemaining = BattleConfig.skipsFor(userArmy.size());
        this.userCapturesLeft = BattleConfig.MAX_CAPTURES + (playerClan == Clan.LEONE ? BattleConfig.LEONE_BONUS_CAPTURES : 0);
        this.enemyCapturesLeft = BattleConfig.MAX_CAPTURES + (enemyClan == Clan.LEONE ? BattleConfig.LEONE_BONUS_CAPTURES : 0);
        this.battleState = BattleState.ONGOING;
    }

    public int getCombustionRate() {
        return combustionRate;
    }

    public Clan getPlayerClan() {
        return playerClan;
    }

    public Clan getEnemyClan(){
        return enemyClan;
    }

    public int getSkipsRemaining() {
        return skipsRemaining;
    }

    public BattleState getBattleState() {
        return battleState;
    }

    public int getSuccessfulCaptures() {
        return successfulCaptures;
    }

    public  int getUsersCapturesLeft() {
        return userCapturesLeft;
    }

    public int getKillsResisted(){
        return killsResisted;
    }

    public int getEnemySoldiersKilled(){
        return enemySoldiersKilled;
    }
    public int getUserSoldiersKilled(){
        return userSoldiersKilled;
    }
    public int getEnemyCapturesLeft() {
        return enemyCapturesLeft;
    }

    public int getUserCapturesLeft() {
        return userCapturesLeft;
    }

    public int getRounds() {
        return rounds;
    }

    public int getSoldiersCombusted() {
        return soldiersCombusted;
    }

    public int getSkipsUsed() {
        return skipsUsed;
    }

    public int getUserCount(){
        return userArmy.size();
    }

    public int getEnemyCount(){
        return enemyArmy.size();
    }

    public int getNextUserStrength() {
        return getEffectiveStrength(userArmy.FindfirstSoldier(), playerClan);
    }

    public int getNextEnemyStrength() {
        return getEffectiveStrength(enemyArmy.FindfirstSoldier(), enemyClan);
    }

    public int getUpcomingUserStrength() {
        return getEffectiveStrength(userArmy.findNextSoldier(), playerClan);
    }

    public int getUpcomingEnemyStrength() {
        return getEffectiveStrength(enemyArmy.findNextSoldier(), enemyClan);
    }

    public int getEnemyStrongestStrength() {
        return getEffectiveStrength(enemyArmy.getStrongestSoldier(), enemyClan);
    }

    public boolean isGameOver(){
        return battleState != BattleState.ONGOING;
    }

    public boolean canSkip(){
        return battleState == BattleState.ONGOING && skipsRemaining > 0 && rounds >= skipAvailableAtRound;
    }

    public int getSkipCooldownRemaining(){
        return Math.max(0, skipAvailableAtRound - rounds);
    }
    public boolean canCapture(){
        return battleState == BattleState.ONGOING && userCapturesLeft > 0;
    }

    public int getTrumpsRemaining() {
        return trumpsRemaining;
    }

    public int getTrumpsUsed() {
        return trumpsUsed;
    }

    public List<BattleEvent> getLastEvents() {
        return Collections.unmodifiableList(lastEvents);
    }


    public boolean canTrump(){
        if (battleState != BattleState.ONGOING || trumpsRemaining <= 0 || rounds < trumpAvailableAtRound) {
            return false;
        }
        Soldier strongest = userArmy.getStrongestSoldier();
        Soldier first = userArmy.FindfirstSoldier();
        if (strongest == null || first == null) return false;
        return strongest.getBaseStrength() > first.getBaseStrength();
    }

    public String playRound(boolean useTrump, boolean tryCapture) {
        if (battleState != BattleState.ONGOING) {
            throw new IllegalStateException("Battle is over");
        }
        lastEvents.clear();

        Soldier userSoldier;
        if (useTrump) {
            if (!canTrump()) throw new IllegalStateException("Trump not available");
            userSoldier = userArmy.removeStrongest();
            trumpsRemaining--;
            trumpsUsed++;
        } else {
            userSoldier = userArmy.CurrentSoldier();
        }

        Soldier enemySoldier = enemyArmy.CurrentSoldier();
        int userSoldierStrength = getEffectiveStrength(userSoldier, playerClan);
        int enemySoldierStrength = getEffectiveStrength(enemySoldier, enemyClan);

        boolean tie = userSoldierStrength == enemySoldierStrength;
        boolean userWins = tie ? random.nextBoolean() : userSoldierStrength > enemySoldierStrength;

        String log = "You: " + userSoldierStrength + "  Enemy: " + enemySoldierStrength + " | ";

        if (userWins) {
            userArmy.StealSoldier(userSoldier);
            if (tryCapture && userCapturesLeft > 0) {
                userArmy.StealSoldier(enemySoldier);
                userCapturesLeft--;
                successfulCaptures++;
                lastEvents.add(new BattleEvent(BattleEvent.Kind.CAPTURE, false));
                log += "You captured the enemy soldier";
            } else if (rollKillResist()) {
                enemyArmy.StealSoldier(enemySoldier);
                killsResisted++;
                lastEvents.add(new BattleEvent(BattleEvent.Kind.LOYALTY, false));
                log += "You Win but the enemy escapes";
            } else {
                enemySoldiersKilled++;
                lastEvents.add(new BattleEvent(BattleEvent.Kind.DEATH, false));
                log += "You killed the enemy soldier";
            }
        } else {
            enemyArmy.StealSoldier(enemySoldier);
            boolean enemyCaptures = enemyCapturesLeft > 0
                    && userSoldier.getBaseStrength() >= BattleConfig.ENEMY_CAPTURE_MIN_STRENGTH;
            if (enemyCaptures) {
                enemyArmy.StealSoldier(userSoldier);
                enemyCapturesLeft--;
                successfulCaptures++;
                lastEvents.add(new BattleEvent(BattleEvent.Kind.CAPTURE, true));
                log += "Enemy Decided to Recruit your soldier to their ranks";
            } else if (rollKillResist()) {
                userArmy.StealSoldier(userSoldier);
                killsResisted++;
                lastEvents.add(new BattleEvent(BattleEvent.Kind.LOYALTY, true));
                log += "Loyalty! Your Soldier Escaped to rejoin your cause";
            } else {
                userSoldiersKilled++;
                lastEvents.add(new BattleEvent(BattleEvent.Kind.DEATH, true));
                log += "Enemy Killed your soldier";
            }
        }

        int burned = soldiersCombusted;
        log += rollCombustion(enemyArmy, enemyClan, "Enemy");
        if (soldiersCombusted > burned) lastEvents.add(new BattleEvent(BattleEvent.Kind.COMBUSTION, false));
        burned = soldiersCombusted;
        log += rollCombustion(userArmy, playerClan, "One of your");
        if (soldiersCombusted > burned) lastEvents.add(new BattleEvent(BattleEvent.Kind.COMBUSTION, true));

        rounds++;
        if (useTrump) {
            trumpAvailableAtRound = rounds + BattleConfig.SKIP_OR_TRUMP_COOLDOWN_ROUNDS;
        }
        checkBattleEnd();
        return log;
    }


    public String playRound(boolean useTrump) {return playRound(useTrump, false);}
    public String playRound(){
        return playRound(false, false);
    }

    private void checkBattleEnd() {
        if(enemyArmy.isEmpty() &&  userArmy.isEmpty()){
             battleState = BattleState.DRAW;
        }
        else if(enemyArmy.isEmpty()){
            battleState = BattleState.WIN;
        }
        else if(userArmy.isEmpty()){
            battleState = BattleState.LOSS;
        }
        else if(rounds >= BattleConfig.MAX_ROUNDS){
            battleState = BattleState.DRAW;
        }
    }

    public String skipRound(){
        if(battleState != BattleState.ONGOING){
            throw new IllegalStateException("Battle is over");
        }
        if(!canSkip()){
            throw new IllegalStateException("Skip not available");
        }
        lastEvents.clear();

        userArmy.reshuffleArmy();

        skipsRemaining--;
        skipsUsed++;
        rounds++;
        skipAvailableAtRound = rounds + BattleConfig.SKIP_OR_TRUMP_COOLDOWN_ROUNDS;
        checkBattleEnd();
        String log = "You have skipped this round";
        return log;
    }

    private int getEffectiveStrength(Soldier soldier, Clan clan){
        if (soldier == null) return 0;
        int strength = soldier.getBaseStrength();
        if (clan == Clan.FORSYTHE){
            strength = strength+ BattleConfig.FORSYTHE_STRENGTH_BONUS;
        }
        return strength;
    }


    private boolean rollKillResist() {
        return random.nextInt(100) < BattleConfig.KILL_RESIST_CHANCE;
    }


    private int getCombustionChance(Clan clan){
        double chance = combustionRate;
        if (clan == Clan.BACCUS){
            chance -= BattleConfig.BACCUS_COMBUSTION_REDUCTION;
        }
        return (int) Math.max(0, chance);
    }

    private String rollCombustion(Army army, Clan clan, String Owner){
        if (soldiersCombusted >= BattleConfig.MAX_COMBUSTION_CHANCE){
            return "";
        }

        int chance = getCombustionChance(clan);
        if(chance <= 0 || army.isEmpty()){
            return "";
        }

        if(random.nextInt(100) < chance){
            Soldier lost = army.Combust(random);
            soldiersCombusted++;
            return " | " + Owner + " soldier (strength " + lost.getBaseStrength() + ") spontaneously combusts!";
        }
        return "";
    }


    public int getUserStrongestStrength() {
        return getEffectiveStrength(userArmy.getStrongestSoldier(), playerClan);
    }

    public Soldier getNextUserSoldier() {
        return userArmy.FindfirstSoldier();
    }

    public Soldier getNextEnemySoldier(){
        return enemyArmy.FindfirstSoldier();
    }
}


