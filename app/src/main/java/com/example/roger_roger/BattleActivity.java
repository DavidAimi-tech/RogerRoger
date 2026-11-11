package com.example.roger_roger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class BattleActivity extends AppCompatActivity {

    private static final int HIGH_TIER_MIN = 6;     // base strength 6+ shows the knight card, below shows the archer

    private int[] strengths;
    private Clan playerClan;
    private int enemyBudget;

    private BattleEngine engine;
    private SoldierNames names;
    private final Random random = new Random();

    private ImageView enemyArt, playerArt;
    private TextView enemyName, enemyStrength, playerName, playerStrength;

    private TextView txtEnemyCount, txtRound, txtYouCount, txtSkips, txtTrumps, txtCaptures;

    private TextView scYouClan, scEnemyClan, scYouBest, scEnemyBest, scYouNext, scEnemyNext,
            scYouCapt, scEnemyCapt;
    private View[] pips;

    private TextView txtLog;
    private ScrollView scrollLog;
    private CheckBox chkCapture;
    private TextView btnFight, btnSkip, btnTrump, btnQuit;

    private ImageView commanderArt;
    private TextView commanderName;
    private TextView commanderLine;   // dialogue box: commander lines

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        strengths = getIntent().getIntArrayExtra("strengths");
        if (strengths == null || strengths.length == 0) {
            strengths = new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5};
        }
        String clanExtra = getIntent().getStringExtra("clan");
        playerClan = (clanExtra != null) ? Clan.valueOf(clanExtra) : Clan.LEONE;
        enemyBudget = getIntent().getIntExtra("budget", BattleConfig.MEDIUM);

        View enemyCard = findViewById(R.id.enemyCard);
        enemyArt = enemyCard.findViewById(R.id.cardArt);
        enemyName = enemyCard.findViewById(R.id.cardName);
        enemyStrength = enemyCard.findViewById(R.id.cardStrength);
        View playerCard = findViewById(R.id.playerCard);
        playerArt = playerCard.findViewById(R.id.cardArt);
        playerName = playerCard.findViewById(R.id.cardName);
        playerStrength = playerCard.findViewById(R.id.cardStrength);

        txtEnemyCount = findViewById(R.id.txtEnemyCount);
        txtRound = findViewById(R.id.txtRound);
        txtYouCount = findViewById(R.id.txtYouCount);
        txtSkips = findViewById(R.id.txtSkips);
        txtTrumps = findViewById(R.id.txtTrumps);
        txtCaptures = findViewById(R.id.txtCaptures);

        scYouClan = findViewById(R.id.scYouClan);
        scEnemyClan = findViewById(R.id.scEnemyClan);
        scYouBest = findViewById(R.id.scYouBest);
        scEnemyBest = findViewById(R.id.scEnemyBest);
        scYouNext = findViewById(R.id.scYouNext);
        scEnemyNext = findViewById(R.id.scEnemyNext);
        scYouCapt = findViewById(R.id.scYouCapt);
        scEnemyCapt = findViewById(R.id.scEnemyCapt);
        pips = new View[]{findViewById(R.id.pip1), findViewById(R.id.pip2), findViewById(R.id.pip3)};

        txtLog = findViewById(R.id.txtLog);
        scrollLog = findViewById(R.id.scrollLog);
        chkCapture = findViewById(R.id.checkCapture);
        btnFight = findViewById(R.id.btnFight);
        btnSkip = findViewById(R.id.btnSkip);
        btnTrump = findViewById(R.id.btnTrump);
        btnQuit = findViewById(R.id.btnQuit);

        commanderArt = findViewById(R.id.commanderArt);
        commanderName = findViewById(R.id.commanderName);
        commanderLine = findViewById(R.id.commanderLine);

        btnFight.setOnClickListener(GameAudio.withClick(this, v -> takeTurn(engine.playRound(false, chkCapture.isChecked()))));
        btnSkip.setOnClickListener(GameAudio.withClick(this, v -> takeTurn(engine.skipRound())));
        btnTrump.setOnClickListener(GameAudio.withClick(this, v -> takeTurn(engine.playRound(true, chkCapture.isChecked()))));
        btnQuit.setOnClickListener(GameAudio.withClick(this, v -> confirmQuit()));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmQuit();
            }
        });

        startNewBattle();
    }

    // Always builds fresh armies and fresh names, so a replay never carries over old state
    private void startNewBattle() {
        names = new SoldierNames(random);

        Army userArmy = Army.CreateArmy("Player", strengths);
        Army enemyArmy = new Army("Enemy", EnemyGenerator.generateEnemyRoster(
                strengths.length, enemyBudget, random));

        Clan[] clans = Clan.values();
        Clan enemyClan = clans[random.nextInt(clans.length)];

        engine = new BattleEngine(userArmy, enemyArmy, playerClan, enemyClan, random);

        commanderArt.setImageResource(CommanderDialogue.portrait(playerClan));
        commanderName.setText(CommanderDialogue.name(playerClan));
        commanderLine.setText(CommanderDialogue.opening(playerClan));
        txtLog.setText("");
        refresh();
    }

    private void takeTurn(String engineLog) {
        String line = CommanderDialogue.lineFor(playerClan, engine.getLastEvents(), random);
        txtLog.setText(engineLog);                          // log box: only this round's BattleEngine log
        if (line != null) commanderLine.setText(line);      // dialogue box: commander line
        scrollLog.post(() -> scrollLog.scrollTo(0, 0));     // start at the top so the message is readable
        chkCapture.setChecked(false);
        refresh();
    }

    private void refresh() {
        updateScoreboard();

        if (engine.isGameOver()) {
            setActive(btnFight, false);
            setActive(btnSkip, false);
            setActive(btnTrump, false);
            chkCapture.setEnabled(false);
            GameAudio.get(this).playResult(engine.getBattleState() == BattleState.WIN);
            scrollLog.postDelayed(this::openResults, 1500);
            return;
        }

        updateCards();
        updateScouting();

        setActive(btnFight, true);
        setActive(btnSkip, engine.canSkip());
        setActive(btnTrump, engine.canTrump());
        chkCapture.setEnabled(engine.canCapture());
    }

    private void setActive(TextView button, boolean on) {
        button.setEnabled(on);
        button.setAlpha(on ? 1f : 0.45f);
    }

    @SuppressLint("SetTextI18n")
    private void updateCards() {
        Soldier mine = engine.getNextUserSoldier();
        Soldier theirs = engine.getNextEnemySoldier();

        playerArt.setImageResource(cardArt(engine.getPlayerClan(), mine));
        playerName.setText(names.nameOf(mine).toUpperCase());
        playerStrength.setText(String.valueOf(engine.getNextUserStrength()));

        enemyArt.setImageResource(cardArt(engine.getEnemyClan(), theirs));
        enemyName.setText(names.nameOf(theirs).toUpperCase());
        enemyStrength.setText(String.valueOf(engine.getNextEnemyStrength()));
    }

    private int cardArt(Clan clan, Soldier soldier) {
        boolean high = soldier.getBaseStrength() >= HIGH_TIER_MIN;
        switch (clan) {
            case FORSYTHE: return high ? R.drawable.card_forsythe_high : R.drawable.card_forsythe_mid;
            case LEONE:    return high ? R.drawable.card_leone_high : R.drawable.card_leone_mid;
            default:       return high ? R.drawable.card_baccus_high : R.drawable.card_baccus_mid;
        }
    }

    private void updateScoreboard() {
        txtEnemyCount.setText(String.valueOf(engine.getEnemyCount()));
        txtYouCount.setText(String.valueOf(engine.getUserCount()));
        txtRound.setText(String.valueOf(engine.getRounds()));
        txtSkips.setText(String.valueOf(engine.getSkipsRemaining()));
        txtTrumps.setText(String.valueOf(engine.getTrumpsRemaining()));
        txtCaptures.setText(String.valueOf(engine.getUserCapturesLeft()));
    }

    private void updateScouting() {
        scYouClan.setText(engine.getPlayerClan().name());
        scYouClan.setTextColor(clanColor(engine.getPlayerClan()));
        scEnemyClan.setText(engine.getEnemyClan().name());
        scEnemyClan.setTextColor(clanColor(engine.getEnemyClan()));

        scYouBest.setText(String.valueOf(engine.getUserStrongestStrength()));
        scEnemyBest.setText(String.valueOf(engine.getEnemyStrongestStrength()));
        scYouNext.setText(String.valueOf(engine.getUpcomingUserStrength()));
        scEnemyNext.setText(String.valueOf(engine.getUpcomingEnemyStrength()));
        scYouCapt.setText(String.valueOf(engine.getUserCapturesLeft()));
        scEnemyCapt.setText(String.valueOf(engine.getEnemyCapturesLeft()));

        int rating = engine.getCombustionRate();          // 0-3
        for (int i = 0; i < pips.length; i++) {
            pips[i].setBackgroundColor(i < rating ? 0xFFE8701C : 0xFFB0A076);
        }
    }

    private int clanColor(Clan clan) {
        switch (clan) {
            case FORSYTHE: return 0xFF346296;
            case LEONE:    return 0xFFB0700E;
            default:       return 0xFF703CA0;
        }
    }

    private void confirmQuit() {
        new AlertDialog.Builder(this)
                .setTitle("Quit battle?")
                .setMessage("Your progress in this battle will be lost.")
                .setPositiveButton("Quit", (d, w) -> {
                    Intent menu = new Intent(this, MainActivity.class);
                    menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(menu);
                    finish();
                })
                .setNegativeButton("Keep fighting", null)
                .show();
    }

    private void openResults() {
        if (isFinishing()) return;

        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("outcome", engine.getBattleState().name());
        intent.putExtra("rounds", engine.getRounds());
        intent.putExtra("captures", engine.getSuccessfulCaptures());
        intent.putExtra("enemy killed", engine.getEnemySoldiersKilled());
        intent.putExtra("userCasualties", engine.getUserSoldiersKilled());
        intent.putExtra("resisted", engine.getKillsResisted());
        intent.putExtra("combusted", engine.getSoldiersCombusted());
        intent.putExtra("skips", engine.getSkipsUsed());
        intent.putExtra("trumps", engine.getTrumpsUsed());
        intent.putExtra("userLeft", engine.getUserCount());
        intent.putExtra("enemyLeft", engine.getEnemyCount());

        intent.putExtra("strengths", strengths);
        intent.putExtra("clan", playerClan.name());
        intent.putExtra("budget", enemyBudget);

        startActivity(intent);
        finish();
    }
}