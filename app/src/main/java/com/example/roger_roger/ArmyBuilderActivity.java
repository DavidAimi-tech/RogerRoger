package com.example.roger_roger;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.Random;

public class ArmyBuilderActivity extends AppCompatActivity {

    private static final int CREAM = 0xFFF4EFE6;
    private static final int RED = 0xFFFF6B6B;
    private static final int ORANGE = 0xFFFCA311;

    private LinearLayout btnClanForsythe, btnClanLeone, btnClanBaccus;
    private TextView btnSize15, btnSize20, btnSize30;
    private TextView btnEasy, btnMedium, btnHard;
    private TextView txtSoldiersHeader, txtPointsLeft, txtStrengthLabel;
    private ScrollView scrollRoster;
    private LinearLayout rosterContainer;
    private TextView btnStart;

    private Clan selectedClan = Clan.LEONE;
    private int selectedSize = BattleConfig.MEDIUM_ARMY;
    private int selectedBudget = BattleConfig.MEDIUM;

    private int[] strengths;
    private boolean rosterExpanded = true;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_army_builder);

        btnClanForsythe = findViewById(R.id.btnClanForsythe);
        btnClanLeone = findViewById(R.id.btnClanLeone);
        btnClanBaccus = findViewById(R.id.btnClanBaccus);
        btnSize15 = findViewById(R.id.btnSize15);
        btnSize20 = findViewById(R.id.btnSize20);
        btnSize30 = findViewById(R.id.btnSize30);
        btnEasy = findViewById(R.id.btnEasy);
        btnMedium = findViewById(R.id.btnMedium);
        btnHard = findViewById(R.id.btnHard);
        txtSoldiersHeader = findViewById(R.id.txtSoldiersHeader);
        txtPointsLeft = findViewById(R.id.txtPointsLeft);
        txtStrengthLabel = findViewById(R.id.txtStrengthLabel);
        scrollRoster = findViewById(R.id.scrollRoster);
        rosterContainer = findViewById(R.id.rosterContainer);
        btnStart = findViewById(R.id.btnStartBattle);

        btnClanForsythe.setOnClickListener(GameAudio.withClick(this, v -> selectClan(Clan.FORSYTHE)));
        btnClanLeone.setOnClickListener(GameAudio.withClick(this, v -> selectClan(Clan.LEONE)));
        btnClanBaccus.setOnClickListener(GameAudio.withClick(this, v -> selectClan(Clan.BACCUS)));

        btnSize15.setOnClickListener(GameAudio.withClick(this, v -> selectSize(BattleConfig.SMALL_ARMY)));
        btnSize20.setOnClickListener(GameAudio.withClick(this, v -> selectSize(BattleConfig.MEDIUM_ARMY)));
        btnSize30.setOnClickListener(GameAudio.withClick(this, v -> selectSize(BattleConfig.LARGE_ARMY)));

        btnEasy.setOnClickListener(GameAudio.withClick(this, v -> selectDifficulty(BattleConfig.EASY)));
        btnMedium.setOnClickListener(GameAudio.withClick(this, v -> selectDifficulty(BattleConfig.MEDIUM)));
        btnHard.setOnClickListener(GameAudio.withClick(this, v -> selectDifficulty(BattleConfig.HARD)));

        txtSoldiersHeader.setOnClickListener(GameAudio.withClick(this, v -> {
            rosterExpanded = !rosterExpanded;
            applyExpanded();
        }));
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(GameAudio.withClick(this, v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }));
        }
        findViewById(R.id.btnEven).setOnClickListener(GameAudio.withClick(this, v -> setEvenRoster()));
        findViewById(R.id.btnRandomize).setOnClickListener(GameAudio.withClick(this, v -> setRandomRoster()));
        btnStart.setOnClickListener(GameAudio.withClick(this, v -> startBattle()));

        // Defaults. selectSize builds the roster, so it goes last.
        selectClan(selectedClan);
        selectDifficulty(selectedBudget);
        selectSize(selectedSize);
    }

    // ---------- selections ----------

    private void selectClan(Clan clan) {
        selectedClan = clan;
        btnClanForsythe.setSelected(clan == Clan.FORSYTHE);
        btnClanLeone.setSelected(clan == Clan.LEONE);
        btnClanBaccus.setSelected(clan == Clan.BACCUS);
    }

    private void selectSize(int size) {
        selectedSize = size;
        btnSize15.setSelected(size == BattleConfig.SMALL_ARMY);
        btnSize20.setSelected(size == BattleConfig.MEDIUM_ARMY);
        btnSize30.setSelected(size == BattleConfig.LARGE_ARMY);

        if (strengths == null || strengths.length != size) {
            setEvenRoster();
        }
    }

    private void selectDifficulty(int budget) {
        selectedBudget = budget;
        btnEasy.setSelected(budget == BattleConfig.EASY);
        btnMedium.setSelected(budget == BattleConfig.MEDIUM);
        btnHard.setSelected(budget == BattleConfig.HARD);
    }

    // ---------- roster ----------

    private void setEvenRoster() {
        int size = selectedSize;
        int base = BattleConfig.BUDGET / size;
        int remainder = BattleConfig.BUDGET % size;
        int[] s = new int[size];
        for (int i = 0; i < size; i++) {
            s[i] = base + (i < remainder ? 1 : 0);
        }
        setRoster(s);
    }

    private void setRandomRoster() {
        int size = selectedSize;
        LinkedList<Soldier> soldiers = EnemyGenerator.generateEnemyRoster(
                size, BattleConfig.BUDGET, random);
        int[] s = new int[size];
        int i = 0;
        for (Soldier soldier : soldiers) {
            s[i++] = soldier.getBaseStrength();
        }
        setRoster(s);
    }

    private void setRoster(int[] newStrengths) {
        strengths = newStrengths;
        buildRosterRows();
        updateSummary();
        applyExpanded();
    }

    private void buildRosterRows() {
        rosterContainer.removeAllViews();

        for (int i = 0; i < strengths.length; i++) {
            final int index = i;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.bottomMargin = dp(8);
            row.setLayoutParams(rowParams);

            TextView label = new TextView(this);
            label.setText("SOLDIER");
            label.setTextColor(CREAM);
            label.setTextSize(15);
            label.setTypeface(Typeface.DEFAULT_BOLD);
            label.setLayoutParams(new LinearLayout.LayoutParams(dp(78),
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView number = new TextView(this);
            number.setText(String.valueOf(i + 1));
            number.setTextColor(CREAM);
            number.setGravity(Gravity.CENTER);
            number.setBackgroundResource(R.drawable.button_empty_small);
            number.setLayoutParams(new LinearLayout.LayoutParams(dp(40), dp(40)));

            TextView colon = new TextView(this);
            colon.setText(":");
            colon.setTextColor(CREAM);
            colon.setTextSize(20);
            colon.setGravity(Gravity.CENTER);
            colon.setLayoutParams(new LinearLayout.LayoutParams(dp(20),
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            final EditText field = new EditText(this);
            field.setInputType(InputType.TYPE_CLASS_NUMBER);
            field.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
            field.setText(String.valueOf(strengths[i]));          // set before adding the watcher
            field.setTextColor(CREAM);
            field.setBackgroundResource(R.drawable.button_empty_small);
            field.setPadding(dp(12), 0, dp(12), 0);
            field.setGravity(Gravity.CENTER_VERTICAL);
            
            field.setContentDescription("Strength of soldier " + (i + 1));
            LinearLayout.LayoutParams fieldParams = new LinearLayout.LayoutParams(0, dp(40), 1f);
            fieldParams.setMargins(dp(4), 0, dp(8), 0);
            field.setLayoutParams(fieldParams);

            field.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
                @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
                @Override
                public void afterTextChanged(Editable s) {
                    int value = 0;
                    try {
                        value = Integer.parseInt(s.toString());
                    } catch (NumberFormatException ignored) { }
                    strengths[index] = value;
                    boolean ok = value >= BattleConfig.MIN_STRENGTH && value <= BattleConfig.MAX_STRENGTH;
                    field.setTextColor(ok ? CREAM : RED);
                    updateSummary();
                }
            });

            CheckBox lock = new CheckBox(this);
            lock.setButtonTintList(ColorStateList.valueOf(ORANGE));
            lock.setContentDescription("Lock soldier " + (i + 1));
            lock.setOnCheckedChangeListener((button, checked) -> field.setEnabled(!checked));

            row.addView(label);
            row.addView(number);
            row.addView(colon);
            row.addView(field);
            row.addView(lock);
            rosterContainer.addView(row);
        }
    }

    private void updateSummary() {
        int total = 0;
        boolean allInRange = true;
        for (int s : strengths) {
            total += s;
            if (s < BattleConfig.MIN_STRENGTH || s > BattleConfig.MAX_STRENGTH) {
                allInRange = false;
            }
        }
        int left = BattleConfig.BUDGET - total;

        if (!allInRange) {
            txtPointsLeft.setText("Each soldier needs " + BattleConfig.MIN_STRENGTH
                    + "-" + BattleConfig.MAX_STRENGTH);
        } else if (left == 0) {
            txtPointsLeft.setText("Points left: 0 (ready!)");
        } else if (left > 0) {
            txtPointsLeft.setText("Points left: " + left);
        } else {
            txtPointsLeft.setText("Over budget by " + (-left));
        }
        btnStart.setEnabled(allInRange && left == 0);
    }

    private void applyExpanded() {
        int visibility = rosterExpanded ? android.view.View.VISIBLE : android.view.View.GONE;
        scrollRoster.setVisibility(visibility);
        txtStrengthLabel.setVisibility(visibility);
        txtSoldiersHeader.setText("SOLDIERS (" + strengths.length + ") "
                + (rosterExpanded ? "▾" : "▸"));
    }

    private void startBattle() {
        ValidationResult result = ValidateRoster.validate(
                strengths, selectedSize, BattleConfig.BUDGET);
        if (!result.valid) {
            Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, BattleActivity.class);
        intent.putExtra("strengths", strengths);
        intent.putExtra("clan", selectedClan.name());
        intent.putExtra("budget", selectedBudget);
        startActivity(intent);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}